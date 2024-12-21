package com.filesystem.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.filesystem.entities.Team;

public interface ITeamRepository extends JpaRepository<Team, Long> {

    List<Team> findByMembersId(Long userId);

}
