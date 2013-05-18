package play.db;

import com.jolbox.bonecp.BoneCPDataSource;
import play.Application;
import play.Configuration;
import play.Logger;
import play.Plugin;

/**
 * A Play plugin that automatically manages JPA configuration.
 */
public class MyBoneCPPlugin extends Plugin {

    private final Application application;
    private BoneCPDataSource ds;

    public MyBoneCPPlugin(Application application) {
        this.application = application;
    }

    public void onStart() {
        Configuration config = application.configuration().getConfig("db.default");
        loadDataSource(config);
    }

    public void loadDataSource(Configuration config) {
        try {
            Class.forName(config.getString("driver"));
            ds = new BoneCPDataSource();
            ds.setJdbcUrl(config.getString("url"));
            ds.setUsername(config.getString("user"));
            ds.setPassword(config.getString("pass"));

            // Pool configuration
            ds.setPartitionCount(getPropertyOrElse(config.getInt("partitionCount"),1));
            ds.setMaxConnectionsPerPartition(getPropertyOrElse(config.getInt("maxConnectionsPerPartition"), 30));
            ds.setMinConnectionsPerPartition(getPropertyOrElse(config.getInt("minConnectionsPerPartition"), 5));
            ds.setAcquireIncrement(getPropertyOrElse(config.getInt("acquireIncrement"), 1));
            ds.setAcquireRetryAttempts(getPropertyOrElse(config.getInt("acquireRetryAttempts"), 10));
            ds.setAcquireRetryDelayInMs(getPropertyOrElse(config.getMilliseconds("acquireRetryDelay"), 1000L));
            ds.setConnectionTimeoutInMs(getPropertyOrElse(config.getMilliseconds("connectionTimeout"), 1000L));
            ds.setIdleMaxAge(getPropertyOrElse(config.getMilliseconds("idleMaxAge"), (1000L * 60 * 10)), java.util.concurrent.TimeUnit.MILLISECONDS);
            ds.setMaxConnectionAge(getPropertyOrElse(config.getMilliseconds("maxConnectionAge"), (1000L * 60 * 60)), java.util.concurrent.TimeUnit.MILLISECONDS);
            ds.setDisableJMX(getPropertyOrElse(config.getBoolean("disableJMX"), true));
            ds.setStatisticsEnabled(getPropertyOrElse(config.getBoolean("statisticsEnabled"), false));
            ds.setIdleConnectionTestPeriod(getPropertyOrElse(config.getMilliseconds("idleConnectionTestPeriod"), (1000L * 60)), java.util.concurrent.TimeUnit.MILLISECONDS);

            String initSql = config.getString("initSQL");
            if(!isEmpty(initSql)) {
                ds.setInitSQL(initSql);
            }
            Boolean logStatements = config.getBoolean("logStatements");
            if(logStatements != null) {
                ds.setLogStatementsEnabled(logStatements);
            }
            String connectionTestStatement = config.getString("connectionTestStatement");
            if(!isEmpty(connectionTestStatement)) {
                ds.setConnectionTestStatement(connectionTestStatement);
            }

            // Bind in JNDI
            play.JNDI.getContext().rebind(config.getString("jndiName"), ds);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Integer getPropertyOrElse(Integer val, Integer defaultVal) {
        if(val == null) return defaultVal;
        return val;
    }

    private Long getPropertyOrElse(Long val, Long defaultVal) {
        if(val == null) return defaultVal;
        return val;
    }

    private Boolean getPropertyOrElse(Boolean val, Boolean defaultVal) {
        if(val == null) return defaultVal;
        return val;
    }

    private boolean isEmpty(String val) {
        return val == null || val.trim().isEmpty();
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
        try {
            ds.close();
        } catch (Exception e) {
            Logger.debug(e.getMessage(),e);
        }
    }
}