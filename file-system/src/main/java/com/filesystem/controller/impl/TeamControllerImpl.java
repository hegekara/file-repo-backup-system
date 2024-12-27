package com.filesystem.controller.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.filesystem.controller.ITeamController;
import com.filesystem.entities.Team;
import com.filesystem.entities.TeamRequest;
import com.filesystem.service.ITeamService;

@RestController
@RequestMapping("/rest/api/team")
public class TeamControllerImpl implements ITeamController {

    @Autowired
    private ITeamService teamService;

    @Override
    @PostMapping("/create")
    public ResponseEntity<Team> createTeam(@RequestBody TeamRequest team) {
        System.out.println("takım oluşturma başlatıldı");
        return teamService.createTeam(team);
    }

    @Override
    @GetMapping("/list")
    public ResponseEntity<List<Team>> getAllTeams() {
        return teamService.getAllTeams();
    }

    @Override
    @GetMapping("/{teamId}")
    public ResponseEntity<Team> getTeamById(@PathVariable Long teamId) {
        return teamService.getTeamById(teamId);
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