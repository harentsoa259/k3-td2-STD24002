package org.example;

import java.util.List;

public class Main {
    public static void main(String[] args) {

        DataRetriever dr = new DataRetriever();

        Ingredient i1 = new Ingredient(null, "Tomate", 600.0, CategoryEnum.VEGETABLE);
        Ingredient i2 = new Ingredient(null, "Laitue", 400.0, CategoryEnum.VEGETABLE);
        Ingredient i3 = new Ingredient(null, "Poulet", 9000.0, CategoryEnum.ANIMAL);

        dr.saveIngredient(i1);
        dr.saveIngredient(i2);
        dr.saveIngredient(i3);

        Dish d1 = new Dish(null, "Salade fraîche", DishTypeEnum.STARTER, 0.0, List.of());
        Dish d2 = new Dish(null, "Poulet grillé", DishTypeEnum.MAIN, 0.0, List.of());

        dr.saveDish(d1);
        dr.saveDish(d2);

    }
}