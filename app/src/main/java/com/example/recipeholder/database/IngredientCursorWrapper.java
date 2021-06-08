package com.example.recipeholder.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.example.recipeholder.Ingredient;
import com.example.recipeholder.database.RecipeDBSchema.IngredientTable;

import java.util.UUID;

public class IngredientCursorWrapper extends CursorWrapper {

    public IngredientCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Ingredient getIngredient() {
        String ingredientId = getString(getColumnIndex(IngredientTable.Columns.INGREDIENT_ID));
        String uuidString = getString(getColumnIndex(IngredientTable.Columns.INGREDIENT_UUID));
        String ingredientName = getString(getColumnIndex(IngredientTable.Columns.INGREDIENT_NAME));
        String ingredientAmount = getString(getColumnIndex(IngredientTable.Columns.INGREDIENT_AMOUNT));

        Ingredient ingredient = new Ingredient(UUID.fromString(uuidString), ingredientId, ingredientName, ingredientAmount);

        return ingredient;
    }
}
