package com.filesystem.entities.user;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "Admin")
@EqualsAndHashCode(callSuper=true)
public class Admin extends BaseUser{

    @Column(nullable = true)
    private LocalDate startingDate;
}
