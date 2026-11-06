package friendlycodingbrata;

import friendlycodingbrata.config.DatabaseConfig;
import friendlycodingbrata.entity.CompanyEntity;
import friendlycodingbrata.migration.DatabaseMigrationService;
import friendlycodingbrata.repository.CompanyRepository;
import friendlycodingbrata.repository.CompanyRepositoryJdbc;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        log.info("Starting lab-2 application...");

        DatabaseConfig databaseConfig = new DatabaseConfig();

        // 1. Автоматическое закрытие пула соединений через try-with-resources (без блока finally)
        try (HikariDataSource dataSource = databaseConfig.createDataSource()) {
            initDatabase(dataSource);

            CompanyRepository companyRepository = new CompanyRepositoryJdbc(dataSource);

            // 2. Логика разделена по методам вместо одного огромного блока кода
            demonstrateSave(companyRepository);
            demonstrateFind(companyRepository);
            demonstrateFindAll(companyRepository);
            demonstrateUpdate(companyRepository);
            demonstrateDelete(companyRepository);
        }

        log.info("Application demonstration finished successfully.");
    }

    private static void initDatabase(HikariDataSource dataSource) {
        DatabaseMigrationService migrationService = new DatabaseMigrationService(dataSource);
        migrationService.runMigrations();
    }

    private static void demonstrateSave(CompanyRepository companyRepository) {
        log.info("--- 1. save() ---");
        CompanyEntity firstCompany = new CompanyEntity("VK", 11000);
        int firstCompanyId = companyRepository.save(firstCompany);
        log.info("Saved: id={}, entity={}", firstCompanyId, firstCompany);

        CompanyEntity secondCompany = new CompanyEntity("Kaspersky", 4500);
        int secondCompanyId = companyRepository.save(secondCompany);
        log.info("Saved: id={}, entity={}", secondCompanyId, secondCompany);
    }

    private static void demonstrateFind(CompanyRepository companyRepository) {
        log.info("--- 2. findById() & findByName() ---");
        CompanyEntity foundByName = companyRepository.findByName("Kaspersky");
        log.info("Found by name 'Kaspersky': {}", foundByName);

        if (foundByName != null) {
            CompanyEntity foundById = companyRepository.findById(foundByName.getId());
            log.info("Found by id {}: {}", foundByName.getId(), foundById);
        }

        CompanyEntity nonExistentCompany = companyRepository.findById(999999);
        log.info("Found non-existent id 999999: {}", nonExistentCompany);

        CompanyEntity nonExistentName = companyRepository.findByName("UnknownLLC");
        log.info("Found non-existent name 'UnknownLLC': {}", nonExistentName);
    }

    private static void demonstrateFindAll(CompanyRepository companyRepository) {
        log.info("--- 3. findAll() ---");
        List<CompanyEntity> allCompanies = companyRepository.findAll();
        log.info("Total companies in DB: {}", allCompanies.size());
        for (CompanyEntity company : allCompanies) {
            log.info(" - {}", company);
        }
    }

    private static void demonstrateUpdate(CompanyRepository companyRepository) {
        log.info("--- 4. update() & idempotency ---");
        CompanyEntity companyToUpdate = companyRepository.findByName("VK");
        if (companyToUpdate != null) {
            companyToUpdate.setName("VK Tech");
            companyToUpdate.setEmployeesCount(12500);

            boolean firstUpdate = companyRepository.update(companyToUpdate);
            log.info("First update result: {}", firstUpdate);

            // Идемпотентность: повторный вызов с теми же данными не меняет состояние
            boolean secondUpdate = companyRepository.update(companyToUpdate);
            log.info("Second (idempotent) update result: {}", secondUpdate);
        }

        CompanyEntity fakeCompany = new CompanyEntity(999999, "FakeCompany", 1);
        boolean fakeUpdate = companyRepository.update(fakeCompany);
        log.info("Update non-existent company result: {}", fakeUpdate);
    }

    private static void demonstrateDelete(CompanyRepository companyRepository) {
        log.info("--- 5. deleteById() & idempotency ---");
        CompanyEntity companyToDelete = companyRepository.findByName("Kaspersky");
        if (companyToDelete != null) {
            int targetId = companyToDelete.getId();

            companyRepository.deleteById(targetId);
            log.info("Deleted company id: {}", targetId);

            CompanyEntity verifyDeleted = companyRepository.findById(targetId);
            log.info("Verify deletion for id {}: {}", targetId, verifyDeleted);

            // Идемпотентность: повторный вызов deleteById для удаленной записи безопасен
            companyRepository.deleteById(targetId);
            log.info("Second (idempotent) deleteById({}) succeeded without errors.", targetId);
        }
    }
}