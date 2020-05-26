package util.guice;

import com.google.inject.AbstractModule;
import com.google.inject.persist.jpa.JpaPersistModule;

/**
 * Persistence module extends {@link AbstractModule}.
 */
public class PersistenceModule extends AbstractModule {

    private String jpaUnit;

    /**
     * JpaUnit for testing.
     * @param jpaUnit
     */
    public PersistenceModule(String jpaUnit) {
        this.jpaUnit = jpaUnit;
    }

    @Override
    protected void configure() {
        install(new JpaPersistModule(jpaUnit));
        bind(JpaInitializer.class).asEagerSingleton();
    }

}
