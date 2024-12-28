package com.filesystem.entities.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "User")
@EqualsAndHashCode(callSuper=true)
public class User extends BaseUser{

    @Column(nullable = false, unique = true)
    private String repoPath;

    @Column(nullable = false)
    private Double storageLimit;
}
