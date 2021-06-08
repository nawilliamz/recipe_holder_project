package com.example.recipeholder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.recipeholder.database.DataBaseHelper;
import com.example.recipeholder.database.IngredientCursorWrapper;
import com.example.recipeholder.database.RecipeCursorWrapper;
import com.example.recipeholder.database.RecipeDBSchema.IngredientTable;
import com.example.recipeholder.database.RecipeDBSchema.RecipeTable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RecipeQueue {

    public static final String TAG = "RecipeQueue";

    private static RecipeQueue sRecipeQueue;

//    private List<Recipe> mRecipes;

    //These two variables part of database code
    private Context mContext;
    private SQLiteDatabase db;


    private RecipeQueue(Context context) {

//        mRecipes = new ArrayList<>();
//        for (int i = 0; i < 100; i++) {
//            Recipe recipe = new Recipe();
//            recipe.setName("Recipe #" + i);
//            recipe.setFavorite(i % 2 == 0);
//            mRecipes.add(recipe);
//        }

        //The following code is part of database code
        //context.tetApplicationContext() returns the context of the single, global Application object
        //of the current process
        //context is stored in the instance variable because the variable will be used in upcoming
        //chapter
        mContext = context.getApplicationContext();
        //mContext is, therefore, the context with which the new database is opened
        db = new DataBaseHelper(mContext).getWritableDatabase();
        //Note: The database is created in RecipeQueue constructor so that it is available as soon
        //as the singleton is created


    }

    public static RecipeQueue get(Context context) {
        if (sRecipeQueue == null) {
            sRecipeQueue = new RecipeQueue(context);
            //You will make use of this context object in Chapter 14
        }
        return sRecipeQueue;

    }

    public void addRecipe (Recipe r) {
//        mRecipes.add(r);
        //getContentValues() is defined below
        ContentValues values = getContentValues(r);

        //This code results in
        db.insert(RecipeTable.NAME, null, values);

//        Recipe.list_of_recipes.add(r);
    }

    public void deleteRecipe (Recipe r) {
        db.delete(RecipeTable.NAME,
                RecipeTable.Columns.UUID + " = ?", new String[] { r.getID().toString()});
    }

    public void addIngredient (Ingredient i) {
        ContentValues values = getIngredientValues(i);

        db.insert(IngredientTable.NAME, null, values);
    }


    //This method has not really been tested yet
    public void deleteIngredient (Ingredient i) {
        String table = IngredientTable.NAME;
        String whereClause = "ingredient_id=?";
        String[] whereArgs = new String[] { i.getIngredient_id()};
        db.delete(table, whereClause, whereArgs);
    }

    public List<Recipe> getRecipes() {
//        return mRecipes;
//        return new ArrayList<>();

        //Need to change our code here to retrieve our Recipes list from my database
        List<Recipe> recipes = new ArrayList<>();

        RecipeCursorWrapper cursor = queryRecipes(null, null);
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                //getRecipe() needs to be updated to retrieve the Recipe from my database
                recipes.add(cursor.getRecipe());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return recipes;

    }

    public Recipe getRecipe(UUID id) {
//        for (Recipe recipe : mRecipes) {
//            if (recipe.getID().equals(id)) {
//                return recipe;
//            }
//        }
        //Need to change this code to retrieves the first Recipe in our database. The other Recipes,
        //if there are any, will be retrieved by the getRecipes() method above
        RecipeCursorWrapper cursor = queryRecipes(
                RecipeTable.Columns.UUID + " = ?",
                new String[] {id.toString()}
        );

        try {
            if (cursor.getCount() == 0) {
                return null;
            }

            cursor.moveToFirst();
            //Note: this getRecipe() method is from RecipeCursorWrapper. It pulls a Recipe out of
            //the database using the Cursor object
            return cursor.getRecipe();
        } finally {
            cursor.close();
        }

    }

    //Writing ingredients in from ingredientsTable
    //***Here, you must use the list of Ingredients that corresponds to the selected Recipe.
    //Note that this List<Ingredients> outputted here combines the ingredients for all of the recipes
    //You can use the ingredient_list_id to pull the ingredients out for each Recipe
    public ArrayList<Ingredient> getIngredientsFromTable() {

        ArrayList<Ingredient> ingredients = new ArrayList<>();


        IngredientCursorWrapper cursor = queryIngredients(null, null);
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                //Recall that this getIngredient() method is from the IngredientCursorWrapper class
                ingredients.add(cursor.getIngredient());
//                Log.d(TAG, "There are " + ingredients.size() + " ingredients in recipes arraylist.");

                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
            return ingredients;
    }



    public Ingredient getIngredientFromTable(String ingredient_list_id) {
        IngredientCursorWrapper cursor = queryIngredients(
                IngredientTable.Columns.INGREDIENT_ID + " = ?",
                new String[] { ingredient_list_id}
        );

        try {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            Log.d(TAG, "The only ingredient in the DB has been read in.");
            return cursor.getIngredient();
        }finally {
            cursor.close();
        }

    }



    private static ContentValues getContentValues(Recipe recipe) {
        ContentValues values = new ContentValues();
        values.put(RecipeTable.Columns.UUID, recipe.getID().toString());
        values.put(RecipeTable.Columns.NAME, recipe.getName());
        values.put(RecipeTable.Columns.DATE, recipe.getDate().getTime());
        values.put(RecipeTable.Columns.FAVORITE, recipe.isFavorite() ? 1 : 0);

        return values;
    }


    //This method retrieves the values from the ingredient argument
    private static ContentValues getIngredientValues (Ingredient ingredient) {
        ContentValues values = new ContentValues();
        values.put(IngredientTable.Columns.INGREDIENT_ID, ingredient.getIngredient_id());
        values.put(IngredientTable.Columns.INGREDIENT_UUID, ingredient.getId().toString());
        values.put(IngredientTable.Columns.INGREDIENT_NAME, ingredient.getName());
        values.put(IngredientTable.Columns.INGREDIENT_AMOUNT, ingredient.getAmount());

        return values;
    }

    //This method was added
    public void updateRecipe(Recipe recipe) {
        String uuidString = recipe.getID().toString();
        ContentValues values = getContentValues(recipe);

        db.update(RecipeTable.NAME, values,
                RecipeTable.Columns.UUID + " = ?",
                new String[] { uuidString });
    }

    public void updateIngredient(Ingredient ingredient) {
        String ingred_id = ingredient.getIngredient_id();
        ContentValues values = getIngredientValues(ingredient);


        //Remember: the whereClause is an SQL where clause that adds criteria for a search
        //Somewhere, need to include criteria to factor in the UUID for Ingredient must match
        //the Recipe that the app user currently has selected
        db.update(IngredientTable.NAME, values,
                IngredientTable.Columns.INGREDIENT_ID + " = ?",
                new String[] { ingred_id });

    }



    private RecipeCursorWrapper queryRecipes(String whereClause, String[] whereArgs) {
//    private Cursor queryRecipes(String whereClause, String[] whereArgs) {
        Cursor cursor = db.query(
                RecipeTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        return new RecipeCursorWrapper(cursor);
    }

    private IngredientCursorWrapper queryIngredients(String whereClause, String[] whereArgs) {

        Cursor cursor = db.query(
                IngredientTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        return new IngredientCursorWrapper(cursor);
    }

    public long QueryNumEntriesIngredientTable() {

//        int numberRows = (int) DatabaseUtils.longForQuery(mDatabase,
//                "SELECT COUNT(*) FROM " + IngredientTable.NAME, null);
        long numberRows = DatabaseUtils.queryNumEntries(db, IngredientTable.NAME);
        Log.d(TAG, "number of rows in Database counted");
//        db.close();
        return numberRows;
    }
}

//        String query = "SELECT COUNT(*) FROM " + IngredientTable.NAME;
//        long totalRows = DatabaseUtils.queryNumEntries(mDatabase, IngredientTable.NAME);