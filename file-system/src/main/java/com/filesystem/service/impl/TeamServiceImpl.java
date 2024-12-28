package com.filesystem.service.impl;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.filesystem.entities.Notification;
import com.filesystem.entities.Team;
import com.filesystem.entities.TeamRequest;
import com.filesystem.entities.user.User;
import com.filesystem.repositories.INotificationRepository;
import com.filesystem.repositories.ITeamRepository;
import com.filesystem.repositories.IUserRepository;
import com.filesystem.service.ITeamService;

@Service
public class TeamServiceImpl implements ITeamService {

    @Autowired
    private ITeamRepository teamRepository;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private INotificationRepository notificationRepository;

    private static final Logger logger = LoggerFactory.getLogger(TeamServiceImpl.class);

    @Override
    public ResponseEntity<Team> createTeam(TeamRequest team) {
        logger.info("Starting team creation for name: {}", team.getName());

        // Manager kontrolü
        Optional<User> manager = userRepository.findById(team.getManager());
        if (manager.isEmpty()) {
            logger.warn("Team creation failed: Manager with ID {} not found.", team.getManager());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        User member = userRepository.findByUsername(team.getTeamMemberName());
        if (member==null) {
            logger.warn("Team creation failed: Member with ID {} not found.", team.getTeamMemberName());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        // Takım adı kontrolü
        if (teamRepository.existsByName(team.getName())) {
            logger.warn("Team creation failed: Team name '{}' already exists.", team.getName());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }

        try {
            // Takım oluşturma
            Team newTeam = new Team();
            newTeam.setName(team.getName());
            newTeam.setManager(manager.get());;
            newTeam.setMember(member);;
            newTeam.setRepoPath("repos/teams/" + team.getName()); // Takım repo yolu
            Team savedTeam = teamRepository.save(newTeam);

            createNotification(member.getId(), (manager.get().getUsername()).toString()+" added you a team" );

            try {
                Path repoDirectory = Paths.get(newTeam.getRepoPath());
                Files.createDirectories(repoDirectory);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }

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
    public ResponseEntity<Team> getTeamById(Long teamId) {
        logger.info("Fetching team {}.", teamId);

        Optional<Team> team = teamRepository.findById(teamId);
        if (team.isEmpty()) {
            logger.warn("No teams found.");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }

        logger.info("Successfully fetched {} teams.", team.get().getName());
        return ResponseEntity.ok().body(team.get());
    }

    @Override
    public ResponseEntity<List<Team>> getTeamsByUser(Long userId) {
        logger.info("Fetching teams for user ID: {}", userId);

        List<Team> teams = Stream.concat(
            teamRepository.findByMemberId(userId).stream(),
            teamRepository.findByManagerId(userId).stream()
            )
            .distinct() 
            .collect(Collectors.toList());

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

    private void createNotification(Long userId, String message){
        Optional<User> optional = userRepository.findById(userId);
    
        if (optional.isPresent()) {
            User user = optional.get();

            Notification notification = new Notification(user, message);
            notificationRepository.save(notification);
        }
    }
}