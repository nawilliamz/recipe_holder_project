package com.example.recipeholder;

import android.annotation.SuppressLint;
import android.database.SQLException;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.UUID;

public class RecipeFragmentNew extends Fragment implements IngredientListDialog.OnIngredientInputSelected,
        InstructionListDialog.OnInstructionInputSelected{


    public static final String TAG = "RecipeFragmentNew";
    public static final String DIALOG_INGREDIENTS = "DialogIngredients";

    private Recipe mRecipe;
    private Toolbar mToolBar;
    private EditText mNameField;
    private Button mIngredientAdd;
    private Button mIngredientDelete;
    private ListView mIngredientWindow;
    private Button mInstructionAdd;
    private Button mInstructionDelete;
    private ListView mInstructionWindow;

    private ArrayList<Ingredient> list_read_in_from_DB;

    private int mIngredientListViewPosition;
    private int mInstructionListViewPosition;
    private boolean isDeleteClicked;

    public static final String ARG_RECIPE_ID = "crime_id";

    private ArrayAdapter<Ingredient> mListAdapter;
    private ArrayAdapter<Instruction> mInstructionAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Let's FragmentManager know this frag needs to receive menu callbacks
        setHasOptionsMenu(true);



//        mRecipe = new Recipe();

        //Need to catch the recipe ID number from the parent Activity and use this here to show
        //the correct recipe view and also save the correct recipe name
        //Need to call newIntent here to retrieve the UUID???
        UUID recipeId = (UUID) getArguments().getSerializable(ARG_RECIPE_ID);
//        for (Recipe recipe : RecipeQueue.get(getContext()).getRecipes()) {
//            if (recipe.getID().equals(recipeId)){
//                mRecipe = recipe;
//            }
//        }

        mRecipe = RecipeQueue.get(getActivity()).getRecipe(recipeId);

        //Remaining code below is to read in the list of ingredients corresponding to mRecipe from the database (this is
        //accomplished by use of the RecipeQueue.get(getActivity()).getIngredients() method
        //This is going to return all of the ingredients for all of the Recipes from the DB. Therefore, you
        //will need to pick out the Ingredients that correspond to mRecipe
        //Note: If there is only one Ingredient in the ingredient table, you need to run getIngredient()
        //rather than getIngredients() to read that one ingredient in from memory

        //This method reads in the ingredients only applicable for mRecipe from the DB (the DB contains
        //all of the recipes that have been entered. But this method separates out those associated
        //with mRecipe by using the UUID (which is unique for each recipe)).


        //CODE TO READ INGREDIENTS FROM DATABASE

        long numRows = RecipeQueue.get(getActivity()).QueryNumEntriesIngredientTable();

        //Only want to run getIngredientsFromTable() once, so running and putting into a variable
        //Will eventually want to put this on its own thread

        try {

            list_read_in_from_DB = RecipeQueue.get(getActivity()).getIngredientsFromTable();
        } catch (SQLException e) {

            //Still need to notify user --> create switch statement with toasts
            Log.d(TAG, "Ingredients were not downloaded from DB successfully");
            e.getStackTrace();
        }


        if (numRows == 1 && mRecipe.getIngredients().size() != 0) {

            try {

                Ingredient ingredient = RecipeQueue.get(getActivity()).getIngredientFromTable(mRecipe.getIngredients().get(0).getIngredient_id());
                mRecipe.getIngredients().add(ingredient);

            } catch (SQLException e) {
//                /Still need to notify user --> create switch statement with toasts
                Log.d(TAG, "Ingredient was not downloaded from DB successfully");
                e.getStackTrace();
            }

//            mRecipe.getIngredients().add(ingredient);
//            mRecipe.getIngredients().add(ingredient);
//            Log.d(TAG, "mRecipe.Ingredients has been read in from the DB");

        } else if (numRows > 1)
        {
            for (Ingredient ingredient : list_read_in_from_DB) {
                //Pick the elements of list_read_in_from_DB that match those associated with mRecipe
                //by using the UUID field variable

                if (ingredient.getId().toString().equals(mRecipe.getID().toString())) {

                    //For those elements associated with mRecipe, add them to mRecipe's ingredients list
//                    mRecipe.getIngredients().add(ingredient);
                    mRecipe.getIngredients().add(ingredient);
                    Log.d(TAG, "mRecipe.Ingredients has been read in from the DB");

                }
            }
        }

        //CODE TO READ INSTRUCTIONS FROM DATABASE

//        if (mRecipe.getIngredients().size() != 0) {
//            mListAdapter.addAll(readIngredientsFromDatabase());
//
//            Log.d(TAG, "RecipeFragmentNew created again: mListAdapter now has " + mListAdapter.getCount() + "elements in it.");
//        }



        //not adding to mListAdapter so they will be viewed in the ListView
//        mListAdapter.addAll(readIngredientsFromDatabase());
    }



    @Override
    public void onPause() {
        super.onPause();
        //So mRecipe is not added to Recipe table in DB until the app is paused
        RecipeQueue.get(getActivity()).updateRecipe(mRecipe);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.lists_detail_view, container, false);

        //These two lines of code were moved from onCreate() method above
//        UUID recipeId = (UUID) getArguments().getSerializable(ARG_RECIPE_ID);
//        mRecipe = RecipeQueue.get(getActivity()).getRecipe(recipeId);






        mNameField = v.findViewById(R.id.ingredient_name_view);
        mNameField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mRecipe.setName(s.toString());
//                mNameField.setText(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        //INGREDIENT CODE

        mIngredientAdd = v.findViewById(R.id.add_ingredient_button);
        mIngredientAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: opening ingredient dialog");
                IngredientListDialog dialog = new IngredientListDialog();


                dialog.setTargetFragment(RecipeFragmentNew.this, 1);
                dialog.show(getFragmentManager(), "MyIngredientDialog");
            }
        });


        mIngredientDelete = v.findViewById(R.id.delete_ingredient_button);
        
        mIngredientDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (int i = 0; i < mRecipe.getIngredients().size(); i++) {
                    if (i == mIngredientListViewPosition) {

                        Log.d(TAG, "*****The value of mRecipe.getIngredients().get(i) is" + mRecipe.getIngredients().get(i));

                        //This method removes the ingredient from IngredientTable in mDatabase
                        RecipeQueue.get(getActivity()).deleteIngredient(mRecipe.getIngredients().get(i));

                        mRecipe.getIngredients().remove(i);

                    }
                    mListAdapter.notifyDataSetChanged();
                }

//                listAdapter.remove(listAdapter.getItem(listViewPosition));

            }
        });



        //Will need to set the text in this window from Dialog that captures input from user

        mListAdapter = new ArrayAdapter<Ingredient>(
                getActivity(),
                android.R.layout.simple_list_item_1,

                //Note: this is where the ListAdapter retrieves the list of ingredients. Therefore,
                //you need to read in the current list of ingredients from the database before
                //this line of code. Therefore, this is done above on onCreate()
                mRecipe.getIngredients()
        );


        mIngredientWindow = v.findViewById(R.id.ingredients_window);
        mIngredientWindow.setAdapter(mListAdapter);

//        AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                listViewPosition = position;
////                selectedItemFromList = (Ingredient) mIngredientWindow.getItemAtPosition(position);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        };

        AdapterView.OnItemClickListener onIngredientItemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //isClicked is set to true whenever an item is clicked then used by the listener
                //on mIngredientDelete button to determine whether to allow a click or not
                mIngredientListViewPosition = position;
//                isDeleteClicked = true;

//                selectedItemFromList = (Ingredient) mIngredientWindow.getItemAtPosition(position);



            }
        };

        //This line of code is critical for attaching the listener to your IngredientWindow. Note that,
        //for some reason, it has to be after/below the line where the OnItemClicklistener is defined
        mIngredientWindow.setOnItemClickListener(onIngredientItemClickListener);



        //INSTRUCTION CODE

        mInstructionDelete = v.findViewById((R.id.delete_instruction_button));

        mInstructionDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < mRecipe.getInstructions().size(); i++) {
                    if (i == mInstructionListViewPosition) {
                        mRecipe.getInstructions().remove(i);


                    }
                    mInstructionAdapter.notifyDataSetChanged();
                }
            }
        });

        mInstructionAdd = v.findViewById(R.id.add_instruction_button);

        mInstructionAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: opening instruction dialog");
                InstructionListDialog dialog = new InstructionListDialog();

                //Instead of using setTargetFragmenet, you will need to use code to pass Fragment
                //args from the DialogFragment to RecipeFragmentNew, retrieve those args as Strings,
                //and then set those strings, use a request code to specify the fragment
                dialog.setTargetFragment(RecipeFragmentNew.this, 2);

                //dialog.show displays the Dialog, the tag is a unique tag name the system uses to
                //save and restore the fragment state when necessary
                //use getChildFragmentManager() instead to display the dialog
                dialog.show(getFragmentManager(), "MyInstructionDialog");
            }
        });


        mInstructionAdapter = new ArrayAdapter<Instruction>(
            getActivity(),
            android.R.layout.simple_list_item_1,
            mRecipe.getInstructions()

        );


        mInstructionWindow = v.findViewById(R.id.instructions_window);
        mInstructionWindow.setAdapter(mInstructionAdapter);

        AdapterView.OnItemClickListener onInstructionItemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //isClicked is set to true whenever an item is clicked then used by the listener
                //on mIngredientDelete button to determine whether to allow a click or not
                mInstructionListViewPosition = position;
//                isDeleteClicked = true;

//                selectedItemFromList = (Ingredient) mIngredientWindow.getItemAtPosition(position);

            }
        };

        mInstructionWindow.setOnItemClickListener(onInstructionItemClickListener);


        /////////////////////////////////
        //THIS CODE BELOW IS TO RETRIEVE, INFLATE, AND PLACE LISTENER ON THE "IMAGE VIEW" IN THE RECIPE VIEW
        //THAT I'VE "INCLUDED" IN THE LISTS_DETAIL_VIEW LAYOUT

         ConstraintLayout constraintLayout = (ConstraintLayout) v.findViewById(R.id.recipe_layout);

        LinearLayout includeContainerView = (LinearLayout) constraintLayout.findViewById(R.id.linearLayout);

        ConstraintLayout outermost_recipe_toolbar_view = (ConstraintLayout) includeContainerView.findViewById(R.id.include);

        LinearLayout linearLayout = (LinearLayout) outermost_recipe_toolbar_view.findViewById(R.id.toolbar_layout_container2);

        @SuppressLint("ResourceType") ImageView imageViewInsideToolbar = (ImageView) linearLayout.findViewById(R.id.delete_recipe_view);

        TextView textViewInsideToolbar = (TextView) linearLayout.findViewById(R.id.recipe_toolbar_text);

        if (mRecipe.getName() != null) {
            textViewInsideToolbar.setText(mRecipe.getName());
        } else {
            textViewInsideToolbar.setText(R.string.app_name);
        }


        imageViewInsideToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecipeQueue.get(getActivity()).deleteRecipe(mRecipe);
                //Removes recipe from our arraylist of recipes
                Recipe.list_of_recipes.remove(mRecipe);
                Toast.makeText(getActivity(), R.string.delete_recipe_confirmation, Toast.LENGTH_SHORT).show();
            }
        });

        //////////////////////////////////////

        return v;
    }



    @Override
    public void sendIngredientInput(String ingredientInput, String ingredientAmount) {
        Log.d(TAG, "sendInput: found incoming input");

//        Recipe recipe_temp = null;

        //This code captures the same UUID as the recipeID for the recipe selected from recyclerView
        // and stores in a variable
        //The intent here is to store the UUID for the ingredient to be the same as that of the
        //corresponding Recipe
        UUID ingredient_recipe_Id = (UUID) getArguments().getSerializable(ARG_RECIPE_ID);

        if ((!ingredientInput.equals("")) && (!ingredientAmount.equals(""))) {

            //Capture a list ID for each ingredient that is the String concatenation of
            //UUID, name,and amount

            String ingredient_list_id = ingredient_recipe_Id.toString() + "" + ingredientInput + "" + ingredientAmount;

            Ingredient ingredient = new Ingredient(ingredient_recipe_Id, ingredient_list_id, ingredientInput, ingredientAmount);


            //If you use listAdapter.add(ingredients), the ingredient fills the list window immediately
//            mRecipe.getIngredients().add(ingredient);

            //Need to retrieve the the ingredients arraylist for the Recipe this ingredient is part of
            // to use it in the conditional
//            for (Recipe recipe : Recipe.list_of_recipes) {
//                UUID x = recipe.getID();
//                UUID y = ingredient.getId();
//                if (x.toString().equals(y.toString())) {
//                    recipe_temp = recipe;
//                }
//            }

            //This code adds the new ingredient to the database
            RecipeQueue.get(getActivity()).addIngredient(ingredient);


            //If you switch to a cursor adaptor, you s/b able to be rid of this code below since
            //you can read the ingredient table into the ListView directly from the DB

//            mRecipe.getIngredients().add(ingredient);
            mListAdapter.add(ingredient);


            //Need to use the get() method in RecipeQueue to retrieve our Singleton, then
            //use dot notation to run the addIngredient() method using ingredient instance
            //variable above as your input
            //This will add a row for your ingredient into the IngredientTable and save its values

        }
    }

    @Override
    public void sendInstructionInput(String instructionInput) {
        Log.d(TAG, "sendInput: found incoming input");

        if((!instructionInput.equals(""))) {
            Instruction instruction = new Instruction(instructionInput);
            mInstructionAdapter.add(instruction);
        }
    }



    public static RecipeFragmentNew newInstance(UUID recipeId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_RECIPE_ID, recipeId);

        RecipeFragmentNew fragment = new RecipeFragmentNew();
        fragment.setArguments(args);
        return fragment;


    }

    //MENU CODE: no longer using /unable to get menu working at fragment level

//    @Override
//    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
////        super.onCreateOptionsMenu(menu, inflater);
//        inflater.inflate(R.menu.fragment_recipe, menu);
//
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.delete_recipe:
//                //Deletes the recipe from the recipe table in DB
//                RecipeQueue.get(getActivity()).deleteRecipe(mRecipe);
//                //Removes recipe from our arraylist of recipes
//                Recipe.list_of_recipes.remove(mRecipe);
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//
//    }

    //    public UUID getRecipeUUID() {
//        return (UUID) getArguments().getSerializable(ARG_RECIPE_ID);
//    }
}