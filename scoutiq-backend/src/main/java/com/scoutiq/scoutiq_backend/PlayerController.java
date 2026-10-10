package com.scoutiq.scoutiq_backend;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/players")
public class PlayerController {

    private final JdbcTemplate jdbcTemplate;

    public PlayerController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/count")
    public Integer getPlayerCount() {
        return jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM players",
                Integer.class
        );
    }


    @GetMapping("/top-scorers")
    public List<Map<String, Object>> getTopScorers(
            @RequestParam(defaultValue = "EPL") String league,
            @RequestParam(defaultValue = "2023/24") String season) {

        String sql = """
            SELECT full_name AS "name",
                   goals AS "goals",
                   assists AS "assists",
                   goals_p90 AS "goalsPer90"
            FROM vw_player_scouting_stats
            WHERE league = ?
              AND season = ?
            ORDER BY goals DESC, full_name
            FETCH FIRST 10 ROWS ONLY
            """;

        return jdbcTemplate.queryForList(sql, league, season);
    }

    @GetMapping("/{playerId}/stats")
    public List<Map<String, Object>> getPlayerStats(
        @PathVariable Long playerId) {

        String sql = """
            SELECT season AS "season",
                league AS "league",
                matches_played AS "matchesPlayed",
                minutes_played AS "minutesPlayed",
                goals AS "goals",
                assists AS "assists",
                xg AS "xG",
                 xa AS "xA"
            FROM player_season_stats
            WHERE player_id = ?
            ORDER BY season DESC, league
            """;

    return jdbcTemplate.queryForList(sql, playerId);
}






}
