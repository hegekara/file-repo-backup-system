package com.filesystem.entities;

import com.filesystem.entities.user.User;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "teams")
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @ManyToOne
    @JoinColumn(name = "manager", nullable = false)
    private User manager;

    @ManyToOne
    @JoinColumn(name = "member", nullable = false)
    private User member;

    @Column(nullable = false, unique = true)
    private String repoPath;
}
