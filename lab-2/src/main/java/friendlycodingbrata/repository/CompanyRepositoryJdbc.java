package friendlycodingbrata.repository;

import friendlycodingbrata.entity.CompanyEntity;
import friendlycodingbrata.exception.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CompanyRepositoryJdbc implements CompanyRepository {

    private static final Logger log = LoggerFactory.getLogger(CompanyRepositoryJdbc.class);

    private static final String INSERT_SQL =
            "INSERT INTO company_schema.company (name, employees_count) VALUES (?, ?)";

    private static final String SELECT_BY_ID_SQL =
            "SELECT id, name, employees_count FROM company_schema.company WHERE id = ?";

    private static final String SELECT_BY_NAME_SQL =
            "SELECT id, name, employees_count FROM company_schema.company WHERE name = ?";

    private static final String SELECT_ALL_SQL =
            "SELECT id, name, employees_count FROM company_schema.company ORDER BY id";

    private static final String UPDATE_SQL =
            "UPDATE company_schema.company SET name = ?, employees_count = ? WHERE id = ?";

    private static final String DELETE_BY_ID_SQL =
            "DELETE FROM company_schema.company WHERE id = ?";

    private final DataSource dataSource;

    public CompanyRepositoryJdbc(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public int save(CompanyEntity company) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, company.getName());
            preparedStatement.setInt(2, company.getEmployeesCount());
            preparedStatement.executeUpdate();

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1);
                    company.setId(generatedId);
                    return generatedId;
                }
                throw new RepositoryException("Failed to retrieve generated id for company: " + company.getName());
            }
        } catch (SQLException e) {
            log.error("Failed to save company: {}", company, e);
            throw new RepositoryException("Error occurred while saving company", e);
        }
    }

    @Override
    public CompanyEntity findById(int id) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_BY_ID_SQL)) {

            preparedStatement.setInt(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToCompany(resultSet);
                }
                return null;
            }
        } catch (SQLException e) {
            log.error("Failed to find company by id: {}", id, e);
            throw new RepositoryException("Error occurred while finding company by id: " + id, e);
        }
    }

    @Override
    public CompanyEntity findByName(String name) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_BY_NAME_SQL)) {

            preparedStatement.setString(1, name);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToCompany(resultSet);
                }
                return null;
            }
        } catch (SQLException e) {
            log.error("Failed to find company by name: {}", name, e);
            throw new RepositoryException("Error occurred while finding company by name: " + name, e);
        }
    }

    @Override
    public List<CompanyEntity> findAll() {
        List<CompanyEntity> companies = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_SQL);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                companies.add(mapResultSetToCompany(resultSet));
            }
            return companies;
        } catch (SQLException e) {
            log.error("Failed to find all companies", e);
            throw new RepositoryException("Error occurred while retrieving all companies", e);
        }
    }

    @Override
    public boolean update(CompanyEntity company) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_SQL)) {

            preparedStatement.setString(1, company.getName());
            preparedStatement.setInt(2, company.getEmployeesCount());
            preparedStatement.setInt(3, company.getId());

            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            log.error("Failed to update company: {}", company, e);
            throw new RepositoryException("Error occurred while updating company with id: " + company.getId(), e);
        }
    }

    @Override
    public void deleteById(int id) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_BY_ID_SQL)) {

            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            log.error("Failed to delete company by id: {}", id, e);
            throw new RepositoryException("Error occurred while deleting company with id: " + id, e);
        }
    }

    private CompanyEntity mapResultSetToCompany(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        String name = resultSet.getString("name");
        int employeesCount = resultSet.getInt("employees_count");
        return new CompanyEntity(id, name, employeesCount);
    }
}