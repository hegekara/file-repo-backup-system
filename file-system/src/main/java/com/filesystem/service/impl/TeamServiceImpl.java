package com.filesystem.service.impl;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.filesystem.entities.Team;
import com.filesystem.entities.User;
import com.filesystem.repositories.ITeamRepository;
import com.filesystem.repositories.IUserRepository;
import com.filesystem.service.ITeamService;

@Service
public class TeamServiceImpl implements ITeamService {

    @Autowired
    private ITeamRepository teamRepository;

    @Autowired
    private IUserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(TeamServiceImpl.class);

    @Override
    public ResponseEntity<Team> createTeam(Team team) {
        logger.info("Starting team creation for name: {}", team.getName());

        // Manager kontrolü
        Optional<User> manager = userRepository.findById(team.getManager().getId());
        if (manager.isEmpty()) {
            logger.warn("Team creation failed: Manager with ID {} not found.", team.getManager().getId());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // Manager bulunamadı
        }

        // Takım adı kontrolü
        if (teamRepository.existsByName(team.getName())) {
            logger.warn("Team creation failed: Team name '{}' already exists.", team.getName());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null); // Takım adı zaten var
        }

        try {
            // Takım oluşturma
            team.setManager(manager.get());
            team.setRepoPath("repos/teams/" + team.getName()); // Takım repo yolu
            Team savedTeam = teamRepository.save(team);

            logger.info("Team '{}' created successfully with ID: {}", savedTeam.getName(), savedTeam.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(savedTeam);
        } catch (Exception e) {
            logger.error("Error creating team '{}': {}", team.getName(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Override
    public ResponseEntity<List<Team>> getAllTeams() {
        logger.info("Fetching all teams.");

        List<Team> teams = teamRepository.findAll();
        if (teams.isEmpty()) {
            logger.warn("No teams found.");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }

        logger.info("Successfully fetched {} teams.", teams.size());
        return ResponseEntity.ok().body(teams);
    }

    @Override
    public ResponseEntity<List<Team>> getTeamsByUser(Long userId) {
        logger.info("Fetching teams for user ID: {}", userId);

        List<Team> teams = teamRepository.findByMembersId(userId);
        if (teams.isEmpty()) {
            logger.warn("No teams found for user ID: {}", userId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }

        logger.info("Successfully fetched {} teams for user ID: {}", teams.size(), userId);
        return ResponseEntity.ok().body(teams);
    }

    @Override
    public ResponseEntity<Void> deleteTeam(Long teamId) {
        logger.info("Attempting to delete team with ID: {}", teamId);

        Optional<Team> teamOptional = teamRepository.findById(teamId);
        if (teamOptional.isEmpty()) {
            logger.warn("Delete failed: Team with ID {} not found.", teamId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        teamRepository.deleteById(teamId);
        logger.info("Team with ID {} successfully deleted.", teamId);
        return ResponseEntity.noContent().build();
    }
}