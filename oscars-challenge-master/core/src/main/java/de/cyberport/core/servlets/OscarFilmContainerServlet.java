package de.cyberport.core.servlets;

import de.cyberport.core.models.Film;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletResourceTypes;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.cyberport.core.constants.OscarConstants.*;

/**
 * Servlet that writes information about the Oscar films in json format into the response.
 * It is mounted for all resources of a specific Sling resource type.
 *
 * Based on the request parameters, a filtering and sorting should be applied. Default sort order is by title.
 *
 * For cases when there is no supported request parameter provided in the request,
 * the servlet should return all the films below the requested container.
 *
 * The Servlet must support following request parameters:
 * 1. title - String. The exact film title
 * 2. year - Integer. The exact year when the film was nominated
 * 3. minYear - Integer. The minimum value of the year for the nominated film
 * 4. maxYear - Integer. The maximum value of the year for the nominated film
 * 5. minAwards - Integer. The minimum value for number of awards
 * 6. maxAwards - Integer. The maximum value for number of awards
 * 7. nominations - Integer. The exact number of nominations
 * 8. isBestPicture - Boolean. True to return only the winners of the best picture nomination.
 * 9. sortBy - Enumeration. Sorting in ascending order, supported values are: 'title', 'year', 'awards', 'nominations'. Default value should be 'title'.
 * 10. limit - Integer. Maximum amount of result entries in the response.
 *
 * Please note:
 * More then 1 filter must be supported.
 * The resulting JSON must not contain "jcr:primaryType" and "sling:resourceType" properties
 * When there will be no results based on the provided filter an empty array should be returned. Please refer to the 3rd example.
 *
 * Examples based on the data stored in oscars.json in resources directory.
 *
 * 1. Request parameters: year=2019&minAwards=4
 *
 * Sample response:
 * {
 *   "result": [
 *     {
 *       "title": "Parasite",
 *       "year": "2019",
 *       "awards": 4,
 *       "nominations": 6,
 *       "isBestPicture": true,
 *       "numberOfReferences": 8855
 *     }
 *   ]
 * }
 *
 * 2. Request parameters: minYear=2018&minAwards=3&sortBy=nominations&limit=4
 *
 * Sample response:
 * {
 *   "result": [
 *     {
 *       "title": "Bohemian Rhapsody",
 *       "year": "2018",
 *       "awards": 4,
 *       "nominations": 5,
 *       "isBestPicture": false,
 *       "numberOfReferences": 387
 *     },
 *     {
 *       "title": "Green Book",
 *       "year": "2018",
 *       "awards": 3,
 *       "nominations": 5,
 *       "isBestPicture": true,
 *       "numberOfReferences": 2945
 *     },
 *     {
 *       "title": "Parasite",
 *       "year": "2019",
 *       "awards": 4,
 *       "nominations": 6,
 *       "isBestPicture": true,
 *       "numberOfReferences": 8855
 *     },
 *     {
 *       "title": "Black Panther",
 *       "year": "2018",
 *       "awards": 3,
 *       "nominations": 7,
 *       "isBestPicture": false,
 *       "numberOfReferences": 770
 *     }
 *   ]
 * }
 *
 * 3. Request parameters: title=nonExisting
 *
 * Sample response:
 * {
 *   "result": []
 * }
 * @author Vitalii Afonin
 * @author ritendra_singh (Updated)
 */
@Component(service = { Servlet.class }, immediate = true)
@SlingServletResourceTypes(
        resourceTypes="test/filmEntryContainer",
        methods=HttpConstants.METHOD_GET,
        extensions="json")
@ServiceDescription("Oscar Film Container Servlet")
public class OscarFilmContainerServlet extends SlingSafeMethodsServlet {

    private static final long serialVersionUID = 1L;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private List<Film> oscarFilmList = new ArrayList<>();
    private JSONObject resultObj = new JSONObject();

    @Override
    public void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws IOException {
        try {

            // Retrieve List from the provided JSON resource
            retrieveFilmsFromResource(request);

            String limit = request.getParameter(LIMIT);

            List<Film> resultFilms;

            // Create Stream to filter the JSON as per parameter list
            Stream<Film> filmStream = oscarFilmList.stream()
                    .filter(addParamFilters(request).stream().reduce(x->true, Predicate::and))
                    .sorted(getSortComparator(request));

            // Apply Limit and Collect the data
            if(StringUtils.isNotBlank(limit)) {
                resultFilms = filmStream
                        .limit(Integer.parseInt(limit))
                        .collect(Collectors.toList());
            } else {
                resultFilms = filmStream.collect(Collectors.toList());
            }

            logger.info("Size of filtered list: " + resultFilms.size());

            // Add the response array to required JSON object
            resultObj.put("result", resultFilms.toArray());
            response.setStatus(HttpServletResponse.SC_OK);

        } catch (JSONException e) {
            logger.error("Exception caught while calling web service >>", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        // Write response to PrintWriter
        response.setContentType(RESPONSE_CONTENT_TYPE);
        response.getWriter().print(resultObj.toString());
    }

    /**
     * Retrieve Films List from Resource
     * @param request
     */
    private void retrieveFilmsFromResource(SlingHttpServletRequest request) {
        final Resource resource = request.getResource();

        for (Resource child : resource.getChildren()) {
            Film film = child.adaptTo(Film.class);
            oscarFilmList.add(film);
        }

        logger.info("Size of list from the resource: " + oscarFilmList.size());
    }

    /**
     * Add Parameters predicate based on the request
     * @param request
     * @return
     */
    private List<Predicate<Film>> addParamFilters(final SlingHttpServletRequest request) {
        List<Predicate<Film>> paramPredicates = new ArrayList<>();

        if(!request.getRequestParameterList().isEmpty()) {
            String title = request.getParameter(TITLE);
            if (title != null && !title.isEmpty()) {
                paramPredicates.add(film -> film.getTitle().equalsIgnoreCase(title));
            }

            String year = request.getParameter(YEAR);
            if (year != null && !year.isEmpty()) {
                paramPredicates.add(film -> Integer.parseInt(film.getYear()) == Integer.parseInt(year));
            }

            String minYear = request.getParameter(MIN_YEAR);
            if (minYear != null && !minYear.isEmpty()) {
                paramPredicates.add(film -> Integer.parseInt(film.getYear()) >= Integer.parseInt(minYear));
            }

            String maxYear = request.getParameter(MAX_YEAR);
            if (maxYear != null && !maxYear.isEmpty()) {
                paramPredicates.add(film -> Integer.parseInt(film.getYear()) <= Integer.parseInt(maxYear));
            }

            String minAwards = request.getParameter(MIN_AWARDS);
            if (minAwards != null && !minAwards.isEmpty()) {
                paramPredicates.add(film -> Integer.parseInt(film.getAwards()) >= Integer.parseInt(minAwards));
            }

            String maxAwards = request.getParameter(MAX_AWARDS);
            if (maxAwards != null && !maxAwards.isEmpty()) {
                paramPredicates.add(film -> Integer.parseInt(film.getAwards()) <= Integer.parseInt(maxAwards));
            }

            String nominations = request.getParameter(NOMINATIONS);
            if (nominations != null && !nominations.isEmpty()) {
                paramPredicates.add(film -> Integer.parseInt(film.getNominations()) == Integer.parseInt(nominations));
            }

            String isBestPicture = request.getParameter(IS_BEST_PICTURE);
            if (isBestPicture != null && !isBestPicture.isEmpty()) {
                paramPredicates.add(film -> Boolean.parseBoolean(film.getIsBestPicture()) == Boolean.parseBoolean(isBestPicture));
            }
        }

        return paramPredicates;
    }

    /**
     * Prepare Sort comparator based on parameters passed
     * @param request
     * @return
     */
    private Comparator<Film> getSortComparator (final SlingHttpServletRequest request) {
        if(!request.getRequestParameterList().isEmpty()) {
            String sortBy = request.getParameter(SORT_BY);
            if (sortBy != null && !sortBy.isEmpty()) {
                if (sortBy.equalsIgnoreCase(YEAR)) {
                    return Comparator.comparing(Film::getYear);
                } else if (sortBy.equalsIgnoreCase(AWARDS)) {
                    return Comparator.comparing(Film::getAwards);
                } else if (sortBy.equalsIgnoreCase(NOMINATIONS)) {
                    return Comparator.comparing(Film::getNominations);
                }
            }
        }

        return Comparator.comparing(Film::getTitle);
    }
}