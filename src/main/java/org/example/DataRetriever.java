package org.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataRetriever {

    // --- SECTION DISH ---

    public Dish findDishById(Integer id) {
        String sql = "SELECT id, name, dish_type, price FROM dish WHERE id = ?;";

        try (Connection conn = new DBConnection().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) throw new RuntimeException("Dish not found with id: " + id);

                Dish dish = new Dish();
                dish.setId(rs.getInt("id"));
                dish.setName(rs.getString("name"));
                dish.setDishType(DishTypeEnum.valueOf(rs.getString("dish_type")));
                dish.setPrice(rs.getObject("price") == null ? null : rs.getDouble("price"));

                dish.setDishIngredients(findDishIngredientByDishId(id));
                return dish;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<DishIngredient> findDishIngredientByDishId(Integer dishId) {
        String sql = "SELECT dish_id, ingredient_id, required_quantity, unit FROM dish_ingredient WHERE dish_id = ?";
        List<DishIngredient> list = new ArrayList<>();

        try (Connection conn = new DBConnection().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, dishId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DishIngredient di = new DishIngredient();
                    di.setDishId(rs.getInt("dish_id"));
                    di.setIngredientId(rs.getInt("ingredient_id"));
                    di.setRequiredQuantity(rs.getDouble("required_quantity"));
                    di.setUnit(rs.getString("unit"));
                    list.add(di);
                }
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveDish(Dish toSave) {
        String sql = """
            INSERT INTO dish (name, dish_type, price)
            VALUES (?, ?::dish_type, ?)
            ON CONFLICT (id) DO UPDATE
            SET name = EXCLUDED.name,
                dish_type = EXCLUDED.dish_type,
                price = EXCLUDED.price
            RETURNING id;
        """;

        try (Connection conn = new DBConnection().getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, toSave.getName());
                ps.setString(2, toSave.getDishType().name());
                ps.setObject(3, toSave.getPrice());

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        toSave.setId(rs.getInt(1));
                    }
                }

                deleteDishIngredients(conn, toSave.getId());
                insertDishIngredients(conn, toSave.getId(), toSave.getDishIngredients());

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // --- SECTION INGREDIENT ---

    public Ingredient findIngredientById(Integer id) {
        String sql = "SELECT id, name, price, category FROM ingredient WHERE id = ?";

        try (Connection conn = new DBConnection().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) throw new RuntimeException("Ingredient not found " + id);

                Ingredient ing = new Ingredient();
                ing.setId(rs.getInt("id"));
                ing.setName(rs.getString("name"));
                ing.setPrice(rs.getDouble("price"));
                ing.setCategory(CategoryEnum.valueOf(rs.getString("category")));

                // Chargement des mouvements liés
                ing.setStockMovementList(findStockMovementsByIngredientId(id));
                return ing;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Ingredient saveIngredient(Ingredient ingredient) {
        String sqlIngredient = """
            INSERT INTO ingredient (id, name, price, category)
            VALUES (COALESCE(?, nextval(pg_get_serial_sequence('ingredient', 'id'))), ?, ?, ?::ingredient_category)
            ON CONFLICT (id) DO UPDATE
            SET name = EXCLUDED.name,
                price = EXCLUDED.price,
                category = EXCLUDED.category
            RETURNING id;
        """;

        try (Connection conn = new DBConnection().getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sqlIngredient)) {
                ps.setObject(1, ingredient.getId()); // Utilise l'ID si présent, sinon le trigger/sequence
                ps.setString(2, ingredient.getName());
                ps.setDouble(3, ingredient.getPrice());
                ps.setString(4, ingredient.getCategory().name());

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        ingredient.setId(rs.getInt(1));
                    }
                }

                if (ingredient.getStockMovementList() != null) {
                    saveStockMovements(conn, ingredient.getId(), ingredient.getStockMovementList());
                }

                conn.commit();
                return ingredient;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void saveStockMovements(Connection conn, Integer ingredientId, List<StockMovement> movements) throws SQLException {
        String sql = """
            INSERT INTO stock_movement (id, ingredient_id, quantity, unit, type, creation_datetime)
            VALUES (COALESCE(?, nextval(pg_get_serial_sequence('stock_movement', 'id'))), ?, ?, ?, ?::movement_type, ?)
            ON CONFLICT (id) DO NOTHING
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (StockMovement sm : movements) {
                ps.setObject(1, sm.getId());
                ps.setInt(2, ingredientId);
                ps.setDouble(3, sm.getValue().getQuantity());
                ps.setString(4, sm.getValue().getUnit().name());
                ps.setString(5, sm.getType().name());
                ps.setTimestamp(6, Timestamp.from(sm.getCreationDatetime()));
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private List<StockMovement> findStockMovementsByIngredientId(Integer ingredientId) {
        String sql = "SELECT id, quantity, unit, type, creation_datetime FROM stock_movement WHERE ingredient_id = ?";
        List<StockMovement> list = new ArrayList<>();

        try (Connection conn = new DBConnection().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, ingredientId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    StockValue val = new StockValue(rs.getDouble("quantity"), Unit.valueOf(rs.getString("unit")));
                    StockMovement sm = new StockMovement(rs.getInt("id"), val,
                            MovementTypeEnum.valueOf(rs.getString("type")),
                            rs.getTimestamp("creation_datetime").toInstant());
                    sm.setIngredientId(ingredientId);
                    list.add(sm);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }


    private void deleteDishIngredients(Connection conn, Integer dishId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM dish_ingredient WHERE dish_id = ?")) {
            ps.setInt(1, dishId);
            ps.executeUpdate();
        }
    }

    private void insertDishIngredients(Connection conn, Integer dishId, List<DishIngredient> list) throws SQLException {
        if (list == null || list.isEmpty()) return;
        String sql = "INSERT INTO dish_ingredient (dish_id, ingredient_id, required_quantity, unit) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (DishIngredient di : list) {
                ps.setInt(1, dishId);
                ps.setInt(2, di.getIngredientId());
                ps.setDouble(3, di.getRequiredQuantity());
                ps.setString(4, di.getUnit());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    public double getDishCost(Dish dish) {
        double cost = 0.0;
        for (DishIngredient di : dish.getDishIngredients()) {
            Ingredient ing = findIngredientById(di.getIngredientId());
            cost += ing.getPrice() * di.getRequiredQuantity();
        }
        return cost;
    }
}