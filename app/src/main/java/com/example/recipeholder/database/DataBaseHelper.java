package com.example.recipeholder.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.recipeholder.database.RecipeDBSchema.IngredientTable;
import com.example.recipeholder.database.RecipeDBSchema.RecipeTable;

public class DataBaseHelper extends SQLiteOpenHelper {

    public static final int VERSION = 1;
    public static final String DATABASE_NAME = "recipeBase.db";

    public DataBaseHelper(Context context) {
        super(context,DATABASE_NAME, null, VERSION);
    }


    //onCreate is called when the database first gets created, so we're using it to create the table
    //and insert data


    //Note: there's no code here used to populate the DB
    @Override
    public void onCreate(SQLiteDatabase db) {
        //This is essentially executing SQL code to create a new table
        db.execSQL("create table " + RecipeTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                RecipeTable.Columns.UUID + " TEXT " + ", " +
                RecipeTable.Columns.NAME + " TEXT " + ", " +
                RecipeTable.Columns.DATE + " TEXT " + ", " +
                RecipeTable.Columns.FAVORITE + " INTEGER" +
                ")"

        );

        //Code that creates a new ingredient table
        db.execSQL("create table " + IngredientTable.NAME + " (" +
                " _id integer primary key autoincrement, " +
                IngredientTable.Columns.INGREDIENT_ID + " TEXT " + ", " +
                IngredientTable.Columns.INGREDIENT_UUID + " TEXT " + ", " +
                IngredientTable.Columns.INGREDIENT_NAME + " TEXT " + ", " +
                IngredientTable.Columns.INGREDIENT_AMOUNT + " TEXT " +
                ")"

        );


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


            db.execSQL("DROP TABLE IF EXISTS " + RecipeTable.NAME);
            db.execSQL("DROP TABLE IF EXISTS " + IngredientTable.NAME);

            onCreate(db);
    }

//    public void deleteIngredient (String id) {
//
//        SQLiteDatabase db = this.getWritableDatabase();
//        db.delete(IngredientTable.NAME, IngredientTable.Columns.INGREDIENT_ID + "= ?", new String[] { id });
//    }
}
