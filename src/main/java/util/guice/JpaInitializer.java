package util.guice;

import com.google.inject.persist.PersistService;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Configure the initial Jpa.
 */
@Singleton
public class JpaInitializer {

    /**
     * Method of initializing.
     * @param persistService configuration of jpaUnit.
     */
    @Inject
    public JpaInitializer (PersistService persistService) {
        persistService.start();
    }

}
