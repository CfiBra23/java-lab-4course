package friendlycodingbrata.config;

import friendlycodingbrata.exception.RepositoryException;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class DatabaseConfig {

    private static final Logger log = LoggerFactory.getLogger(DatabaseConfig.class);
    private static final String CONFIG_FILE_NAME = "application.yml";

    private final String url;
    private final String username;
    private final String password;
    private final String schema;
    private final int maximumPoolSize;
    private final long connectionTimeoutMs;

    public DatabaseConfig() {
        Map<String, Object> properties = loadYamlProperties();

        @SuppressWarnings("unchecked")
        Map<String, Object> datasourceProperties = (Map<String, Object>) properties.get("datasource");
        if (datasourceProperties == null) {
            throw new RepositoryException("Missing 'datasource' section in " + CONFIG_FILE_NAME);
        }

        this.url = (String) datasourceProperties.get("url");
        this.username = (String) datasourceProperties.get("username");
        this.password = (String) datasourceProperties.get("password");
        this.schema = (String) datasourceProperties.get("schema");
        this.maximumPoolSize = (Integer) datasourceProperties.getOrDefault("maximum-pool-size", 5);

        Number timeoutNumber = (Number) datasourceProperties.getOrDefault("connection-timeout-ms", 30000);
        this.connectionTimeoutMs = timeoutNumber.longValue();
    }

    private Map<String, Object> loadYamlProperties() {
        Yaml yaml = new Yaml();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE_NAME)) {
            if (inputStream == null) {
                throw new RepositoryException("Configuration file not found: " + CONFIG_FILE_NAME);
            }
            return yaml.load(inputStream);
        } catch (IOException e) {
            log.error("Failed to read configuration from {}", CONFIG_FILE_NAME, e);
            throw new RepositoryException("Could not read configuration file: " + CONFIG_FILE_NAME, e);
        }
    }

    public DataSource createDataSource() {
        log.info("Initializing HikariCP DataSource for URL: {}", url);
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(url);
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);
        hikariConfig.setSchema(schema);
        hikariConfig.setMaximumPoolSize(maximumPoolSize);
        hikariConfig.setConnectionTimeout(connectionTimeoutMs);
        hikariConfig.setPoolName("CompanyHikariPool");

        return new HikariDataSource(hikariConfig);
    }

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getSchema() {
        return schema;
    }

    public int getMaximumPoolSize() {
        return maximumPoolSize;
    }

    public long getConnectionTimeoutMs() {
        return connectionTimeoutMs;
    }
}