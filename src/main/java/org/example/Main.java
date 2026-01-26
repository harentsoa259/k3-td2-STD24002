package org.example;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        DataRetriever dr = new DataRetriever();

        System.out.println("--- 1. Insertion des Ingrédients ---");
        Ingredient tomate = new Ingredient(null, "Tomate", 600.0, CategoryEnum.VEGETABLE);
        Ingredient laitue = new Ingredient(null, "Laitue", 400.0, CategoryEnum.VEGETABLE);
        Ingredient poulet = new Ingredient(null, "Poulet", 9000.0, CategoryEnum.ANIMAL);

        dr.saveIngredient(tomate);
        dr.saveIngredient(laitue);
        dr.saveIngredient(poulet);
        System.out.println("Ingrédients sauvegardés avec succès.\n");

        System.out.println("--- 2. Création et Liaison des Plats ---");

        Dish salade = new Dish(null, "Salade fraîche", DishTypeEnum.STARTER, 15000.0, new ArrayList<>());
        dr.saveDish(salade);

        DishIngredient di1 = new DishIngredient(salade.getId(), tomate.getId(), 0.25, "KG");
        DishIngredient di2 = new DishIngredient(salade.getId(), laitue.getId(), 1.0, "PIECE");

        salade.setDishIngredients(List.of(di1, di2));
        dr.saveDish(salade);

        Dish pouletGrille = new Dish(null, "Poulet grillé", DishTypeEnum.MAIN, 35000.0, new ArrayList<>());
        dr.saveDish(pouletGrille);

        DishIngredient di3 = new DishIngredient(pouletGrille.getId(), poulet.getId(), 0.5, "KG");

        pouletGrille.setDishIngredients(List.of(di3));
        dr.saveDish(pouletGrille);
        System.out.println("Plats et compositions sauvegardés.\n");

        System.out.println("--- 3. Vérification des Calculs ---");
        displayDishReport(dr, salade.getId());
        displayDishReport(dr, pouletGrille.getId());
    }

    private static void displayDishReport(DataRetriever dr, Integer dishId) {
        Dish dish = dr.findDishById(dishId);
        double cost = dr.getDishCost(dish);
        double margin = dr.getGrossMargin(dish);

        System.out.println("Plat : " + dish.getName());
        System.out.println("  > Prix de vente : " + dish.getPrice());
        System.out.println("  > Coût de revient : " + cost);
        System.out.println("  > Marge brute : " + margin);
        System.out.println("------------------------------------");
    }
}