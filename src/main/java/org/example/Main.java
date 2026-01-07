package org.example;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        DBConnection db = new DBConnection();
        DataRetriever retriever = new DataRetriever(db);

        try {
            Team team = retriever.findTeamById(1);
            System.out.println("Team: " + team.getName() + " (" + team.getContinent() + ")");
            for (Player p : team.getPlayers()) {
                System.out.println(" - " + p.getName() + " | Age: " + p.getAge() +
                        " | Position: " + p.getPosition() +
                        " | Goals: " + p.getGoalNb());
            }

            try {
                int totalGoals = team.getPlayersGoals();
                System.out.println("Total goals: " + totalGoals);
            } catch (IllegalStateException e) {
                System.out.println("Erreur: " + e.getMessage());
            }

            if (!team.getPlayers().isEmpty()) {
                Player first = team.getPlayers().get(0);
                first.setGoalNb(3);
                retriever.saveTeam(team);
                System.out.println("Team sauvegardée avec les nouvelles valeurs de buts.");
            }

        } catch (SQLException e) {
            System.err.println("Erreur base de données: " + e.getMessage());
        }
    }
}
