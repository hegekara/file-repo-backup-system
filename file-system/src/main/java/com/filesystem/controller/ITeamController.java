package com.filesystem.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.filesystem.entities.Team;
import com.filesystem.entities.TeamRequest;

public interface ITeamController {

    public ResponseEntity<Team> createTeam(TeamRequest team);

    public ResponseEntity<List<Team>> getAllTeams();

    public ResponseEntity<Team> getTeamById(Long teamId);

    public ResponseEntity<List<Team>> getTeamsByUser(Long userId);

    public ResponseEntity<Void> deleteTeam(Long teamId);

}
