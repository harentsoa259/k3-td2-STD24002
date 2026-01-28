package org.example;

import java.time.Instant;

public class Main {
    public static void main(String[] args) {
        DataRetriever dataRetriever = new DataRetriever();

        try {
            System.out.println("=== OPÉRATIONS DE STOCK (MODE CONSTRUCTEUR) ===");

            StockValue quantiteInitial = new StockValue(20.0, Unit.KG);

            StockMovement premierMouvement = new StockMovement(
                    null,
                    quantiteInitial,
                    MovementTypeEnum.IN,
                    Instant.now()
            );

            Ingredient tomate = new Ingredient(null, "Tomate Cerise", 3.20, CategoryEnum.VEGETABLE);

            tomate.getStockMovementList().add(premierMouvement);

            // 3. Sauvegarde via DataRetriever
            System.out.println("> Sauvegarde de l'ingrédient...");
            Ingredient tomateSauvegardee = dataRetriever.saveIngredient(tomate);

            StockMovement sortie = new StockMovement(
                    null,
                    new StockValue(5.0, Unit.KG),
                    MovementTypeEnum.OUT,
                    Instant.now()
            );

            tomateSauvegardee.getStockMovementList().add(sortie);
            dataRetriever.saveIngredient(tomateSauvegardee);

            Ingredient resultat = dataRetriever.findIngredientById(tomateSauvegardee.getId());

            System.out.println("------------------------------------------");
            System.out.println("Produit : " + resultat.getName());
            System.out.println("Mouvements enregistrés : " + resultat.getStockMovementList().size());

            double total = 0;
            for (StockMovement sm : resultat.getStockMovementList()) {
                total += (sm.getType() == MovementTypeEnum.IN) ? sm.getValue().getQuantity() : -sm.getValue().getQuantity();
            }
            System.out.println("Stock final : " + total + " " + Unit.KG);
            System.out.println("------------------------------------------");

        } catch (Exception e) {
            System.err.println("Erreur : " + e.getMessage());
        }
    }
}