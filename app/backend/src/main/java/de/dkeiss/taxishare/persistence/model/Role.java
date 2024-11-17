package de.dkeiss.taxishare.persistence.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Data
public class Role {
    @Id @GeneratedValue
    private Long roleId;

    @NotNull
    private String name;

    public Role() {}

    public Role(Long roleId, String name) {
        this.roleId = roleId;
        this.name = name;
    }
}