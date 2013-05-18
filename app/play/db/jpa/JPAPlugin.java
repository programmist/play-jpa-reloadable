package play.db.jpa;

import play.Application;
import play.Configuration;
import play.Plugin;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;

/**
 * A Play plugin that automatically manages JPA configuration.
 */
public class JPAPlugin extends Plugin {

    private final Application application;

    public JPAPlugin(Application application) {
        this.application = application;
    }

    private Map<String,EntityManagerFactory> emfs = new HashMap<String,EntityManagerFactory>();

    /**
     * Reads the configuration file and initialises required JPA EntityManagerFactories.
     */
    public void onStart() {

        Configuration jpaConf = Configuration.root().getConfig("jpa");
        if(jpaConf != null) {
            for(String key: jpaConf.keys()) {
                String persistenceUnit = jpaConf.getString(key);
                emfs.put(key, Persistence.createEntityManagerFactory(persistenceUnit));
            }
        }
    }

    public void resetFactories() {
        emfs = new HashMap<String,EntityManagerFactory>();
        onStart();
    }

    private boolean isPluginDisabled() {
        String status =  application.configuration().getString("jpaplugin");
        return status != null && status.equals("disabled");
    }

    @Override
    public boolean enabled() {
        return !isPluginDisabled();
    }

    public void onStop() {
        for(EntityManagerFactory emf: emfs.values()) {
            emf.close();
        }
    }

    public EntityManager em(String key) {
        EntityManagerFactory emf = emfs.get(key);
        if(emf == null) {
            return null;
        }
        return emf.createEntityManager();
    }
}