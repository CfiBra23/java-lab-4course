package friendlycodingbrata.repository;

import friendlycodingbrata.entity.CompanyEntity;

import java.util.List;

public interface CompanyRepository {

    // Create (возвращает id созданной записи)
    int save(CompanyEntity company);

    // Read (возвращает null, если запись не найдена)
    CompanyEntity findById(int id);

    // Read по второму параметру сущности (name)
    CompanyEntity findByName(String name);

    List<CompanyEntity> findAll();

    // Update (используйте id для поиска сущности, возвращайте false, если запись не найдена)
    boolean update(CompanyEntity company);

    // Delete
    void deleteById(int id);
}