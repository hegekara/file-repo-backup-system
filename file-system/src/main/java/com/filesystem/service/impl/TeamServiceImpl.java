package com.filesystem.service.impl;

import java.util.List;
import java.util.Optional;

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
public class TeamServiceImpl implements ITeamService{

    @Autowired
    private ITeamRepository teamRepository;

    @Autowired
    private IUserRepository userRepository;

    public ResponseEntity<Team> createTeam(Team team) {
        Optional<User> manager = userRepository.findById(team.getManager().getId());
        if (manager.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // Manager bulunamadı
        }
        
        team.setManager(manager.get());
        team.setRepoPath("repos/teams/" + team.getName()); // Takım repo yolu

        Team savedTeam = teamRepository.save(team);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedTeam);
    }

    public ResponseEntity<List<Team>> getAllTeams() {
        List<Team> teams = teamRepository.findAll();
        if (teams.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }
        return ResponseEntity.ok().body(teams);
    }

    public ResponseEntity<List<Team>> getTeamsByUser(Long userId) {
        List<Team> teams = teamRepository.findByMembersId(userId);
        if (teams.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }
        return ResponseEntity.ok().body(teams);
    }

    public ResponseEntity<Void> deleteTeam(Long teamId) {
        Optional<Team> teamOptional = teamRepository.findById(teamId);
        if (teamOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        
        teamRepository.deleteById(teamId);
        return ResponseEntity.noContent().build();
    }

}
