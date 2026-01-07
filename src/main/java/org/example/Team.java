package org.example;

import java.util.ArrayList;
import java.util.List;

public class Team {
    private final Integer id;
    private final  String name;
    private final  ContinentEnum continent;
    private final List<Player> players = new ArrayList<>();

    public Team(Integer id, String name, ContinentEnum continent) {
        this.id = id;
        this.name = name;
        this.continent = continent;
    }

    public void addPlayer(Player player) {
        players.add(player);
        player.setTeam(this);
    }

    public Integer getId() { return id; }
    public String getName() { return name; }
    public ContinentEnum getContinent() { return continent; }
    public List<Player> getPlayers() { return players; }

    public Integer getPlayersGoals() {
        int sum = 0;
        for (Player p : players) {
            if (p.getGoalNb() == null) {
                throw new IllegalStateException("Le nombre de buts du joueur " + p.getName() + " est inconnu.");
            }
            sum += p.getGoalNb();
        }
        return sum;
    }
}
