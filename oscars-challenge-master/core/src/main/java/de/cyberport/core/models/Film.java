package de.cyberport.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author ritendra_singh
 *
 */
@Model(adaptables = {Resource.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class Film {

    @Self
    private Resource resource;

    @Inject
    @Named("title")
    private String title;

    @Inject
    @Named("year")
    private String year;

    @Inject
    @Named("awards")
    private String awards;

    @Inject
    @Named("nominations")
    private String nominations;

    @Inject
    @Named("isBestPicture")
    private String isBestPicture;

    @Inject
    @Named("numberOfReferences")
    private String numberOfReferences;

    public String getTitle() {
        return title;
    }

    public String getYear() {
        return year;
    }

    public String getAwards() {
        return awards;
    }

    public String getNominations() {
        return nominations;
    }

    public String getIsBestPicture() {
        return isBestPicture;
    }

    public String getNumberOfReferences() {
        return numberOfReferences;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public void setAwards(String awards) {
        this.awards = awards;
    }

    public void setNominations(String nominations) {
        this.nominations = nominations;
    }

    public void setIsBestPicture(String isBestPicture) {
        this.isBestPicture = isBestPicture;
    }

    public void setNumberOfReferences(String numberOfReferences) {
        this.numberOfReferences = numberOfReferences;
    }
}
