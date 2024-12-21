package com.filesystem.controller.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.filesystem.controller.ITeamController;
import com.filesystem.entities.Team;
import com.filesystem.service.ITeamService;

@RestController
@RequestMapping("/api/teams")
public class TeamControllerImpl implements ITeamController {

    @Autowired
    private ITeamService teamService;

    @Override
    @PostMapping
    public ResponseEntity<Team> createTeam(@RequestBody Team team) {
        return teamService.createTeam(team);
    }

    @Override
    @GetMapping
    public ResponseEntity<List<Team>> getAllTeams() {
        return teamService.getAllTeams();
    }

    @Override
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Team>> getTeamsByUser(@PathVariable Long userId) {
        return teamService.getTeamsByUser(userId);
    }

    @Override
    @DeleteMapping("/{teamId}")
    public ResponseEntity<Void> deleteTeam(@PathVariable Long teamId) {
        return teamService.deleteTeam(teamId);
    }
}