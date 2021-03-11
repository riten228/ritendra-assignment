package de.cyberport.core.models;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author ritendra_singh
 *
 */
@ExtendWith({ AemContextExtension.class, MockitoExtension.class })
public class FilmTest {

    @InjectMocks
    private Film filmModel;

    private final AemContext ctx = new AemContext();

    @Test
    void getTitle_givenTitleExists_thenReturnTitle() {
        filmModel.setTitle("some title text");
        String actual = filmModel.getTitle();
        assertEquals(actual,"some title text");
    }

    @Test
    void getAwards_givenAwardsExists_thenReturnAwards() {
        filmModel.setAwards("some awards text");
        String actual = filmModel.getAwards();
        assertEquals(actual,"some awards text");
    }

    @Test
    void getYear_givenYearExists_thenReturnYear() {
        filmModel.setYear("some year text");
        String actual = filmModel.getYear();
        assertEquals(actual,"some year text");
    }

    @Test
    void getNominations_givenNominationsExists_thenReturnNominations() {
        filmModel.setNominations("some Nominations text");
        String actual = filmModel.getNominations();
        assertEquals(actual,"some Nominations text");
    }

    @Test
    void getIsBestPicture_givenIsBestPictureExists_thenReturnIsBestPicture() {
        filmModel.setIsBestPicture("IsBestPicture text");
        String actual = filmModel.getIsBestPicture();
        assertEquals(actual,"IsBestPicture text");
    }

    @Test
    void getNoOfReferences_givenNoOfReferencesExists_thenReturnNoOfReferences() {
        filmModel.setNumberOfReferences("No of references text");
        String actual = filmModel.getNumberOfReferences();
        assertEquals(actual,"No of references text");
    }

}
