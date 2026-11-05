package friendlycodingbrata;

import friendlycodingbrata.config.DatabaseConfig;
import friendlycodingbrata.entity.CompanyEntity;
import friendlycodingbrata.migration.DatabaseMigrationService;
import friendlycodingbrata.repository.CompanyRepository;
import friendlycodingbrata.repository.CompanyRepositoryJdbc;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.List;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        log.info("Starting lab-2 application...");

        DatabaseConfig databaseConfig = new DatabaseConfig();
        DataSource dataSource = databaseConfig.createDataSource();

        try {
            DatabaseMigrationService migrationService = new DatabaseMigrationService(dataSource);
            migrationService.runMigrations();

            CompanyRepository companyRepository = new CompanyRepositoryJdbc(dataSource);

            // 1. Демонстрация save
            log.info("--- 1. save() ---");
            CompanyEntity firstCompany = new CompanyEntity("VK", 11000);
            int firstCompanyId = companyRepository.save(firstCompany);
            log.info("Saved: id={}, entity={}", firstCompanyId, firstCompany);

            CompanyEntity secondCompany = new CompanyEntity("Kaspersky", 4500);
            int secondCompanyId = companyRepository.save(secondCompany);
            log.info("Saved: id={}, entity={}", secondCompanyId, secondCompany);

            // 2. Демонстрация findById
            log.info("--- 2. findById() ---");
            CompanyEntity foundCompany = companyRepository.findById(firstCompanyId);
            log.info("Found by id {}: {}", firstCompanyId, foundCompany);

            CompanyEntity nonExistentCompany = companyRepository.findById(999999);
            log.info("Found non-existent id 999999: {}", nonExistentCompany);

            // 3. Демонстрация findByName
            log.info("--- 3. findByName() ---");
            CompanyEntity foundByName = companyRepository.findByName("Kaspersky");
            log.info("Found by name 'Kaspersky': {}", foundByName);

            CompanyEntity nonExistentName = companyRepository.findByName("UnknownLLC");
            log.info("Found non-existent name 'UnknownLLC': {}", nonExistentName);

            // 4. Демонстрация findAll
            log.info("--- 4. findAll() ---");
            List<CompanyEntity> allCompanies = companyRepository.findAll();
            log.info("Total companies in DB: {}", allCompanies.size());
            for (CompanyEntity company : allCompanies) {
                log.info(" - {}", company);
            }

            // 5. Демонстрация update и идемпотентности
            log.info("--- 5. update() & idempotency ---");
            foundCompany.setName("VK Tech");
            foundCompany.setEmployeesCount(12500);

            boolean firstUpdate = companyRepository.update(foundCompany);
            log.info("First update result: {}", firstUpdate);

            // Повторный вызов не меняет состояние БД после первого раза
            boolean secondUpdate = companyRepository.update(foundCompany);
            log.info("Second (idempotent) update result: {}", secondUpdate);

            CompanyEntity fakeCompany = new CompanyEntity(999999, "FakeCompany", 1);
            boolean fakeUpdate = companyRepository.update(fakeCompany);
            log.info("Update non-existent company result: {}", fakeUpdate);

            // 6. Демонстрация deleteById и идемпотентности
            log.info("--- 6. deleteById() & idempotency ---");
            companyRepository.deleteById(secondCompanyId);
            log.info("Deleted company id: {}", secondCompanyId);

            CompanyEntity verifyDeleted = companyRepository.findById(secondCompanyId);
            log.info("Verify deletion for id {}: {}", secondCompanyId, verifyDeleted);

            // Повторное удаление того же id не должно выбрасывать исключений
            companyRepository.deleteById(secondCompanyId);
            log.info("Second (idempotent) deleteById({}) succeeded without errors.", secondCompanyId);

            log.info("All operations demonstrated successfully.");
        } finally {
            if (dataSource instanceof HikariDataSource hikariDataSource) {
                hikariDataSource.close();
                log.info("HikariCP connection pool closed.");
            }
        }
    }
}