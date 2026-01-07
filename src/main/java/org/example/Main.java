package org.example;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        DBConnection db = new DBConnection();
        DataRetriever retriever = new DataRetriever(db);

        try {
            // ===== Test findTeamById =====
            System.out.println("=== Test findTeamById (id=1) ===");
            Team realMadrid = retriever.findTeamById(1);
            printTeam(realMadrid);

            System.out.println("\n=== Test findTeamById (id=5) ===");
            Team interMiami = retriever.findTeamById(5);
            printTeam(interMiami);

            // ===== Test findPlayers avec pagination =====
            System.out.println("\n=== Test findPlayers page=1, size=2 ===");
            List<Player> page1 = retriever.findPlayers(1, 2);
            printPlayers(page1);

            System.out.println("\n=== Test findPlayers page=3, size=5 ===");
            List<Player> page3 = retriever.findPlayers(3, 5);
            printPlayers(page3);

            // ===== Test findTeamsByPlayerName =====
            System.out.println("\n=== Test findTeamsByPlayerName 'Jude' ===");
            List<Team> teamsByPlayer = retriever.findTeamsByPlayerName("Jude");
            if (teamsByPlayer.isEmpty()) System.out.println("Aucune équipe trouvée.");
            else teamsByPlayer.forEach(t -> System.out.println("- " + t.getName()));

            // ===== Test findPlayersByCriteria =====
            System.out.println("\n=== Test findPlayersByCriteria ===");
            List<Player> criteriaPlayers = retriever.findPlayersByCriteria(
                    "ud", PlayerPositionEnum.MIDF, "Madrid", ContinentEnum.EUROPE, 1, 10);
            printPlayers(criteriaPlayers);

            // ===== Test createPlayers avec joueurs existants (exception attendue) =====
            System.out.println("\n=== Test createPlayers (exception attendue) ===");
            List<Player> newPlayers1 = new ArrayList<>();
            newPlayers1.add(new Player(0, "Jude Bellingham", 23, PlayerPositionEnum.STR, null, 5));
            newPlayers1.add(new Player(0, "Pedri", 24, PlayerPositionEnum.MIDF, null, 0));
            try {
                retriever.createPlayers(newPlayers1);
            } catch (RuntimeException e) {
                System.out.println("Exception attendue: " + e.getMessage());
            }

            // ===== Test createPlayers avec nouveaux joueurs =====
            System.out.println("\n=== Test createPlayers (nouveaux joueurs) ===");
            List<Player> newPlayers2 = new ArrayList<>();
            newPlayers2.add(new Player(0, "Vini", 25, PlayerPositionEnum.STR, null, 0));
            newPlayers2.add(new Player(0, "Pedri", 24, PlayerPositionEnum.MIDF, null, 0));
            List<Player> createdPlayers = retriever.createPlayers(newPlayers2);
            printPlayers(createdPlayers);

            // ===== Test saveTeam : ajout d'un joueur =====
            System.out.println("\n=== Test saveTeam : ajout d'un joueur ===");
            if (realMadrid != null) {
                Player newPlayer = new Player(0, "Nouveau Joueur", 22, PlayerPositionEnum.MIDF, null, 1);
                realMadrid.addPlayer(newPlayer);
                retriever.saveTeam(realMadrid);

                Team updatedTeam = retriever.findTeamById(realMadrid.getId());
                printTeam(updatedTeam);
            }

            // ===== Test saveTeam : suppression de tous les joueurs d'une équipe =====
            System.out.println("\n=== Test saveTeam : suppression de joueurs ===");
            if (interMiami != null) {
                interMiami.getPlayers().clear();
                retriever.saveTeam(interMiami);
                Team updated = retriever.findTeamById(interMiami.getId());
                printTeam(updated);
            }

            // ===== Test getPlayersGoals =====
            System.out.println("\n=== Test getPlayersGoals Real Madrid ===");
            if (realMadrid != null) {
                try {
                    int totalGoals = realMadrid.getPlayersGoals();
                    System.out.println("Total buts de Real Madrid: " + totalGoals);
                } catch (RuntimeException e) {
                    System.out.println("Impossible de calculer les buts: " + e.getMessage());
                }
            }

            System.out.println("\n=== Test getPlayersGoals Inter Miami ===");
            if (interMiami != null) {
                try {
                    int totalGoals = interMiami.getPlayersGoals();
                    System.out.println("Total buts Inter Miami: " + totalGoals);
                } catch (RuntimeException e) {
                    System.out.println("Impossible de calculer les buts pour Inter Miami: " + e.getMessage());
                }
            }

        } catch (SQLException e) {
            System.out.println("Erreur SQL: " + e.getMessage());
        }
    }

    private static void printTeam(Team team) {
        if (team == null) {
            System.out.println("Aucune équipe trouvée.");
            return;
        }
        System.out.println("Equipe: " + team.getName() + " | Continent: " + team.getContinent());
        if (team.getPlayers().isEmpty()) {
            System.out.println("Aucun joueur dans cette équipe.");
            return;
        }
        for (Player p : team.getPlayers()) {
            System.out.println(" - " + p.getName() +
                    " | Age: " + p.getAge() +
                    " | Position: " + p.getPosition() +
                    " | Goals: " + (p.getGoalNb() != null ? p.getGoalNb() : "inconnu"));
        }
    }

    private static void printPlayers(List<Player> players) {
        if (players.isEmpty()) {
            System.out.println("Aucun joueur trouvé.");
            return;
        }
        for (Player p : players) {
            System.out.println(" - " + p.getName() +
                    " | Age: " + p.getAge() +
                    " | Position: " + p.getPosition() +
                    " | Goals: " + (p.getGoalNb() != null ? p.getGoalNb() : "inconnu") +
                    " | Team: " + (p.getTeam() != null ? p.getTeam().getName() : "Aucune"));
        }
    }
}
