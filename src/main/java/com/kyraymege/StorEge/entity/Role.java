package com.kyraymege.StorEge.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kyraymege.StorEge.enums.Authority;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "roles")
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class Role extends Auditable{
    private String name;
    private Authority authorities;
}
