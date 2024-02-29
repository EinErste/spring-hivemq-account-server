package org.ein.erste.iot.account.settings;

import org.ein.erste.iot.account.AccountApplication;
import org.springframework.boot.system.ApplicationHome;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Settings {
    private static final String iniFileName = "iot-account-settings.ini";
    private static final String JELASTIC_CONFIG_DIRECTORY = "/home/jelastic/conf/";
    private static final String filePathParamName = "settings.file.path";
    private final String configFileName;
    private final Map<String, Map<String, String>> entries;

    private Settings(String configFileName) {
        this.configFileName = configFileName;
        entries = new HashMap<>();
    }

    public static void loadCustomIniFile(String[] args) {
        String path = loadLocalIniFile();
        if (path == null)
            path = loadFromArgs(args);
        Settings settings = new Settings(path);
        settings.config();
    }

    private static String loadFromArgs(String[] args) {
        for (String arg : args) {
            System.out.println(arg);
            if (arg.contains(filePathParamName) && arg.contains("=")) {
                String path = arg.split("=")[1].trim();
                if (!path.endsWith("/"))
                    path = path + "/";
                return path + iniFileName;
            }
        }
        return null;
    }

    private static String loadLocalIniFile() {
        File file = new File(iniFileName);
        if (file.exists())
            return iniFileName;
        ApplicationHome home = new ApplicationHome(AccountApplication.class);
        file = new File(home.getDir().getAbsolutePath() + "/" + iniFileName);
        if (file.exists())
            return home.getDir().getAbsolutePath() + "/" + iniFileName;
        file = new File(JELASTIC_CONFIG_DIRECTORY + iniFileName);
        if (file.exists())
            return JELASTIC_CONFIG_DIRECTORY + iniFileName;
        return null;
    }

    public void config() {
        if (this.configFileName != null)
            readConfig();

        //Server settings
        System.setProperty("server.port", getInt("Server", "Port", 44001).toString());
        System.setProperty("server.http.port", getInt("Server", "HttpPort", 44001).toString());
        System.setProperty("server.swagger.enabled", getString("Server", "SwaggerOn", "true"));
        System.setProperty("server.swagger.api.host", getString("Server", "SwaggerApiHost", "http://localhost:" + System.getProperty("server.http.port")));
        System.setProperty("spring.jpa.properties.hibernate.jdbc.time_zone", getString("Server", "timezone", "UTC"));

        //Spring DATASOURCE

        System.setProperty("spring.jpa.properties.hibernate.dialect", getString("Hibernate", "Dialect", "org.hibernate.dialect.PostgreSQLDialect"));
        System.setProperty("spring.jpa.hibernate.ddl-auto", getString("Hibernate", "DdlAuto", "validate"));

        System.setProperty("spring.datasource.jdbc-url", getString("Datasource", "Url", "jdbc:postgresql://localhost:5432/iot-account"));
        System.setProperty("spring.datasource.username", getString("Datasource", "Username", "root"));
        System.setProperty("spring.datasource.password", getString("Datasource", "Password", "root"));

        //Hikari Connection Pool
        System.setProperty("spring.datasource.hikari.connectionTimeout", getInt("Hikari", "ConnectionTimeout", 30000).toString());
        System.setProperty("spring.datasource.hikari.idleTimeout", getInt("Hikari", "IdleTimeout", 600000).toString());
        System.setProperty("spring.datasource.hikari.maxLifetime", getInt("Hikari", "MaxLifeTime", 1800000).toString());
        System.setProperty("spring.datasource.hikari.maximumPoolSize", getInt("Hikari", "MaximumPoolSize", 12).toString());
        System.setProperty("spring.datasource.hikari.minimumIdle", getInt("Hikari", "MinimumIdle", 2).toString());
        System.setProperty("spring.datasource.hikari.poolName", getString("Hikari", "PoolName", "Hibernate Connection Pool"));
        System.setProperty("spring.datasource.hikari.validationTimeout", getInt("Hikari", "ValidationTimeout", 5000).toString());
        System.setProperty("spring.datasource.hikari.leakDetectionThreshold", getInt("Hikari", "LeakDetection", 60000).toString());

        //Spring FLYWAY
        System.setProperty("spring.flyway.url", getString("Datasource", "Url", "jdbc:postgresql://localhost:5432/iot-account"));
        System.setProperty("spring.flyway.user", getString("Datasource", "Username", "root"));
        System.setProperty("spring.flyway.password", getString("Datasource", "Password", "root"));
        System.setProperty("spring.flyway.locations", getString("Flyway", "Location", "classpath:migration"));
        System.setProperty("spring.flyway.connectRetries", getString("Flyway", "ConnectRetries", "300"));

        //JWT Security constants
        System.setProperty("jwt.access.token.secret", getString("JWT", "AccessTokenSecret", "secret"));
        System.setProperty("jwt.access.token.expiration.time", getInt("JWT", "AccessTokenExpirationTime", 900000).toString()); //in ms, 15 minutes

        //Logging
        System.setProperty("logging.level.root", getString("Logging", "SpringLevel", "error"));
        System.setProperty("logging.level.org.hibernate", getString("Logging", "Hibernate", "error"));
        System.setProperty("logging.level.org.springframework.boot", getString("Logging", "SpringLevel", "error"));
        System.setProperty("logging.level.org.springframework.web", getString("Logging", "SpringLevel", "error"));
        System.setProperty("logging.level.com.zaxxer.hikari", getString("Logging", "Hikari", "error"));
        System.setProperty("logging.level.org.springframework.orm.jpa", getString("Logging", "OrmJpa", "error"));
        System.setProperty("logging.level.org.springframework.transaction", getString("Logging", "Transaction", "error"));
        System.setProperty("logging.directory", getString("Logging", "Directory", "./logs"));
        System.setProperty("custom.logging.level.console", getString("Logging", "ConsoleLevel", "info"));

        //Hardcoded users
        System.setProperty("hardcoded.user.email.super.admin", getString("Users", "SuperAdminLogin", "superadmin@gmail.com"));
        System.setProperty("hardcoded.user.password.super.admin", getString("Users", "SuperAdminPassword", "superadmin"));
    }


    private void readConfig() {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(configFileName))) {
            Pattern sectionPattern = Pattern.compile("\\s*\\[([^]]*)]\\s*");
            Pattern keyPattern = Pattern.compile("\\s*([^=]*)=(.*)");
            String section = null;
            String line = bufferedReader.readLine();
            while (line != null) {
                Matcher m = sectionPattern.matcher(line);
                if (m.matches())
                    section = m.group(1).trim();
                else if (section != null) {
                    m = keyPattern.matcher(line);
                    if (m.matches()) {
                        String key = m.group(1).trim();
                        String value = m.group(2).trim();
                        if (!entries.containsKey(section))
                            entries.put(section, new HashMap<>());
                        entries.get(section).put(key, value);
                    }
                }
                line = bufferedReader.readLine();
            }
        } catch (IOException ex) {
            System.err.println("file not found" + this.configFileName);
        }
    }

    private String getString(String section, String key, String defaultValue) {
        if (!entries.containsKey(section))
            return defaultValue;
        if (!entries.get(section).containsKey(key))
            return defaultValue;
        return entries.get(section).get(key);
    }

    private Integer getInt(String section, String key, Integer defaultValue) {
        if (!entries.containsKey(section))
            return defaultValue;
        if (!entries.get(section).containsKey(key))
            return defaultValue;
        try {
            return Integer.parseInt(entries.get(section).get(key));
        } catch (NumberFormatException ex) {
            return defaultValue;
        }
    }
}
