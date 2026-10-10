
import { useEffect, useState } from 'react';
import './App.css';

function App() {
  // Player data and loading states
  const [players, setPlayers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  // Scouting filters
  const [league, setLeague] = useState('EPL');
  const [season, setSeason] = useState('2023/24');
  const [metric, setMetric] = useState('goals');
  const [minMinutes, setMinMinutes] = useState(900);

  // Fetch players whenever filters change
  useEffect(() => {
    const controller = new AbortController();

    const params = new URLSearchParams({
      league,
      season,
      metric,
      minMinutes: String(minMinutes),
    });

    fetch(`/api/leaderboard?${params.toString()}`, {
      signal: controller.signal,
    })
      .then((response) => {
        if (!response.ok) {
          throw new Error('Failed to fetch players');
        }
        return response.json();
      })
      .then((data) => {
        setPlayers(data);
        setLoading(false);
        setError('');
      })
      .catch((err) => {
        if (err.name !== 'AbortError') {
          setError(err.message);
          setLoading(false);
        }
      });

    return () => controller.abort();
  }, [league, season, metric, minMinutes]);

  const leagueNames = {
    EPL: 'Premier League',
    La_Liga: 'La Liga',
    Bundesliga: 'Bundesliga',
    Serie_A: 'Serie A',
    Ligue_1: 'Ligue 1',
    RFPL: 'Russian Premier League',
  };

  const metricNames = {
    goals: 'Goals',
    assists: 'Assists',
    goalsPer90: 'Goals per 90',
    xGPer90: 'Expected Goals per 90',
    xAPer90: 'Expected Assists per 90',
  };

  return (
    <div className="app">
      <header>
        <h1>ScoutIQ</h1>
        <p>Football Scouting & Recruitment Intelligence</p>
      </header>

      <main>
        <h2>Discover Football Talent</h2>
        <p>
          Explore player statistics, compare footballers,
          and discover talent using data-driven scouting.
        </p>

        {/* Scouting Filters */}
        <div className="scouting-filters">

          <div>
            <label htmlFor="league">League</label>
            <select
              id="league"
              value={league}
              onChange={(e) => setLeague(e.target.value)}
            >
              <option value="EPL">Premier League</option>
              <option value="La_Liga">La Liga</option>
              <option value="Bundesliga">Bundesliga</option>
              <option value="Serie_A">Serie A</option>
              <option value="Ligue_1">Ligue 1</option>
              <option value="RFPL">Russian Premier League</option>
            </select>
          </div>

          <div>
            <label htmlFor="season">Season</label>
            <select
              id="season"
              value={season}
              onChange={(e) => setSeason(e.target.value)}
            >
              {Array.from({ length: 11 }, (_, i) => {
                const year = 2014 + i;
                const value = `${year}/${String(year + 1).slice(-2)}`;

                return (
                  <option key={value} value={value}>
                    {value}
                  </option>
                );
              })}
            </select>
          </div>

          <div>
            <label htmlFor="metric">Rank By</label>
            <select
              id="metric"
              value={metric}
              onChange={(e) => setMetric(e.target.value)}
            >
              <option value="goals">Goals</option>
              <option value="assists">Assists</option>
              <option value="goalsPer90">Goals per 90</option>
              <option value="xGPer90">Expected Goals per 90</option>
              <option value="xAPer90">Expected Assists per 90</option>
            </select>
          </div>

          <div>
            <label htmlFor="minMinutes">Minimum Minutes</label>
            <select
              id="minMinutes"
              value={minMinutes}
              onChange={(e) =>
                setMinMinutes(Number(e.target.value))
              }
            >
              <option value={0}>0 minutes</option>
              <option value={450}>450 minutes</option>
              <option value={900}>900 minutes</option>
              <option value={1800}>1800 minutes</option>
              <option value={2700}>2700 minutes</option>
            </select>
          </div>

        </div>

        {/* Dynamic Leaderboard Heading */}
        <h3>
          {leagueNames[league]} — {season} — Top Players by {metricNames[metric]}
        </h3>

        {/* Loading and Error Messages */}
        {loading && <p>Loading players...</p>}

        {error && <p>Error: {error}</p>}

        {/* Player Leaderboard */}
        {!loading && !error && (
          <>
            {players.length === 0 ? (
              <p>No players found for the selected filters.</p>
            ) : (
              <table>
                <thead>
                  <tr>
                    <th>Rank</th>
                    <th>Player</th>
                    <th>Goals</th>
                    <th>Assists</th>
                    <th>Goals / 90</th>
                    <th>xG / 90</th>
                    <th>xA / 90</th>
                    <th>Minutes</th>
                  </tr>
                </thead>

                <tbody>
                  {players.map((player, index) => (
                    <tr key={`${player.playerId}-${player.league}`}>
                      <td>{index + 1}</td>
                      <td>{player.name}</td>
                      <td>{player.goals}</td>
                      <td>{player.assists}</td>
                      <td>{player.goalsPer90}</td>
                      <td>{player.xGPer90}</td>
                      <td>{player.xAPer90}</td>
                      <td>{player.minutesPlayed}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}
          </>
        )}

      </main>
    </div>
  );
}

export default App;
