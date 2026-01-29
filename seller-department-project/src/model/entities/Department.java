package model.entities;

import java.io.Serializable;
import java.util.Objects;

public class Department implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String name;

    public Department(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return "[ " + this.getId() + ", " + this.getName() + " ]";
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Department other)) return false;
        return Objects.equals(this.id, other.id);
    }
}
