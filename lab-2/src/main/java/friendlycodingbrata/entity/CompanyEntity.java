package friendlycodingbrata.entity;

import java.util.Objects;

public class CompanyEntity {

    private Integer id;
    private String name;
    private Integer employeesCount;

    public CompanyEntity() {
    }

    public CompanyEntity(String name, Integer employeesCount) {
        this.name = name;
        this.employeesCount = employeesCount;
    }

    public CompanyEntity(Integer id, String name, Integer employeesCount) {
        this.id = id;
        this.name = name;
        this.employeesCount = employeesCount;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getEmployeesCount() {
        return employeesCount;
    }

    public void setEmployeesCount(Integer employeesCount) {
        this.employeesCount = employeesCount;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        CompanyEntity that = (CompanyEntity) object;
        return Objects.equals(id, that.id)
                && Objects.equals(name, that.name)
                && Objects.equals(employeesCount, that.employeesCount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, employeesCount);
    }

    @Override
    public String toString() {
        return "CompanyEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", employeesCount=" + employeesCount +
                '}';
    }
}