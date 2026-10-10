package com.scoutiq.scoutiq_backend;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/leaderboard")
public class LeaderboardController {

    private final JdbcTemplate jdbcTemplate;

    public LeaderboardController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping
public List<Map<String, Object>> getLeaderboard(
        @RequestParam(defaultValue = "EPL") String league,
        @RequestParam(defaultValue = "2023/24") String season,
        @RequestParam(defaultValue = "goals") String metric,
        @RequestParam(defaultValue = "900") Integer minMinutes) {

    String orderBy;

    switch (metric) {
        case "goalsPer90":
            orderBy = "goals_p90";
            break;
        case "xGPer90":
            orderBy = "xg_p90";
            break;
        case "xAPer90":
            orderBy = "xa_p90";
            break;
        case "assists":
            orderBy = "assists";
            break;
        default:
            orderBy = "goals";
    }

    String sql = """
    SELECT player_id AS "playerId",
           full_name AS "name",
           league AS "league",
           season AS "season",
           matches_played AS "matchesPlayed",
           minutes_played AS "minutesPlayed",
           goals AS "goals",
           assists AS "assists",
           goals_p90 AS "goalsPer90",
           xg_p90 AS "xGPer90",
           xa_p90 AS "xAPer90"
    FROM vw_player_scouting_stats
    WHERE league = ?
      AND season = ?
      AND minutes_played >= ?
    ORDER BY %s DESC NULLS LAST, minutes_played DESC
    FETCH FIRST 20 ROWS ONLY
    """.formatted(orderBy);

    return jdbcTemplate.queryForList(sql, league, season, minMinutes);
}




}