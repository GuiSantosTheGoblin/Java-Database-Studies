package entities;

import java.math.BigDecimal;
import java.util.Objects;

public class Seller {
    private Integer id;
    private String name;
    private String email;
    private java.util.Date birthDate;
    private BigDecimal baseSalary;
    private Department department;

    public Seller(String name, String email, java.util.Date birthDate, BigDecimal baseSalary, Department department) {
        this.name = name;
        this.email = email;
        this.birthDate = birthDate;
        this.baseSalary = baseSalary;
        this.department = department;
    }

    public Seller(Integer id, String name, String email, java.util.Date birthDate, BigDecimal baseSalary, Department department) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.birthDate = birthDate;
        this.baseSalary = baseSalary;
        this.department = department;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public java.util.Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(java.util.Date birthDate) {
        this.birthDate = birthDate;
    }

    public BigDecimal getBaseSalary() {
        return baseSalary;
    }

    public void setBaseSalary(BigDecimal baseSalary) {
        this.baseSalary = baseSalary;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    @Override
    public String toString() {
        return "Seller [id=" + this.getId() 
                + ", name=" + this.getName() 
                + ", email=" + this.getEmail() 
                + ", birthDate=" + this.getBirthDate() 
                + ", baseSalary=" + this.getBaseSalary() 
                + ", departmentId=" + this.getDepartment() 
                + "]";
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Seller other)) return false;
        return Objects.equals(this.id, other.id);
    }
}