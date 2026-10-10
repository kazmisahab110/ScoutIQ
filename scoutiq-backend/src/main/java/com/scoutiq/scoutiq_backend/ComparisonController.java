package com.scoutiq.scoutiq_backend;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/compare")
public class ComparisonController {

    private final JdbcTemplate jdbcTemplate;

    public ComparisonController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/players")
    public List<Map<String, Object>> comparePlayers(
            @RequestParam Long player1,
            @RequestParam Long player2,
            @RequestParam String season,
            @RequestParam String league) {

        String sql = """
            SELECT player_id AS "playerId",
                   full_name AS "name",
                   league AS "league",
                   season AS "season",
                   goals AS "goals",
                   assists AS "assists",
                   xg_p90 AS "xGPer90",
                   xa_p90 AS "xAPer90",
                   goals_p90 AS "goalsPer90",
                   assists_p90 AS "assistsPer90"
            FROM vw_player_scouting_stats
            WHERE player_id IN (?, ?)
               AND season = ?
               AND league = ?
            ORDER BY player_id
            """;

        return jdbcTemplate.queryForList(sql, player1, player2, season, league);
    }
}