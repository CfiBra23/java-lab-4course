package friendlycodingbrata.migration;

import friendlycodingbrata.exception.RepositoryException;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseMigrationService {

    private static final Logger log = LoggerFactory.getLogger(DatabaseMigrationService.class);
    private static final String MASTER_CHANGELOG = "db/changelog/db.changelog-master.xml";

    private final DataSource dataSource;

    public DatabaseMigrationService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void runMigrations() {
        log.info("Running Liquibase database migrations...");
        try (Connection connection = dataSource.getConnection()) {
            // Гарантируем наличие схемы перед миграциями
            try (Statement statement = connection.createStatement()) {
                statement.execute("CREATE SCHEMA IF NOT EXISTS company_schema;");
            }

            Database database = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(new JdbcConnection(connection));

            database.setDefaultSchemaName("company_schema");
            database.setLiquibaseSchemaName("company_schema");

            try (Liquibase liquibase = new Liquibase(
                    MASTER_CHANGELOG,
                    new ClassLoaderResourceAccessor(),
                    database
            )) {
                liquibase.update();
            }
            log.info("Liquibase database migrations applied successfully.");
        } catch (SQLException | LiquibaseException e) {
            log.error("Failed to execute database migrations", e);
            throw new RepositoryException("Database migration execution failed", e);
        }
    }
}