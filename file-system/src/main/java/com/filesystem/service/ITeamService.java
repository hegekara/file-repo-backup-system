package com.filesystem.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.filesystem.entities.Team;

public interface ITeamService {

    public ResponseEntity<Team> createTeam(Team team);

    public ResponseEntity<List<Team>> getAllTeams();

    public ResponseEntity<List<Team>> getTeamsByUser(Long userId);

    public ResponseEntity<Void> deleteTeam(Long teamId);

}
