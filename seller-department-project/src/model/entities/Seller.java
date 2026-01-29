package model.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Seller implements Serializable {
    private static final long serialVersionUID = 1L;
    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    private Integer id;
    private String name;
    private String email;
    private java.util.Date birthDate;
    private BigDecimal baseSalary;
    private Department department;

    public Seller(Integer id, String name, String email, java.util.Date birthDate, BigDecimal baseSalary, Department department) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.birthDate = birthDate;
        this.baseSalary = baseSalary;
        this.department = department;
    }

    public Integer getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getEmail() {
        return this.email;
    }

    public java.util.Date getBirthDate() {
        return this.birthDate;
    }

    public BigDecimal getBaseSalary() {
        return baseSalary;
    }

    public Department getDepartment() {
        return this.department;
    }

    @Override
    public String toString() {
        LocalDate localDate = this.getBirthDate().toInstant().atZone(ZoneId.of("America")).toLocalDate();

        return 
        "[ " 
        + this.getId() 
        + ", " 
        + this.getName() 
        + ", " 
        + this.getEmail()
        + ", " 
        + localDate.format(fmt)
        + ", " 
        + this.getBaseSalary()
        + ", " 
        + this.getDepartment()
        + " ]";
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
