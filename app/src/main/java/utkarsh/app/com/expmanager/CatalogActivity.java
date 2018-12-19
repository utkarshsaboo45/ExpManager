package utkarsh.app.com.expmanager;

import android.app.LoaderManager;
import android.content.ClipData;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.content.ClipboardManager;

import utkarsh.app.com.expmanager.data.ExpCursorAdapter;
import utkarsh.app.com.expmanager.data.ExpDbHelper;
import utkarsh.app.com.expmanager.data.ExpContract.ExpEntry;

public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private ExpDbHelper mDbHelper;
    private static final int EXP_LOADER = 0;
    private ExpCursorAdapter expCursorAdapter;

    private TextView totalAmountTextView;
    private TextView totalDiscountTextView;
    private LinearLayout totalLinearLayout;

    private int totalAmount;
    private int totalDiscount;

    String report;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        totalAmount = 0;
        totalDiscount = 0;

        // Setup FAB to open EditorActivity
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
//                startActivity(intent);
//            }
//        });

        Button addExpense = findViewById(R.id.add_expense_button);
        addExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        totalAmountTextView = findViewById(R.id.total_amount);
        totalDiscountTextView = findViewById(R.id.total_discount);
        totalLinearLayout = findViewById(R.id.total_linear_layout);

        mDbHelper = new ExpDbHelper(this);

        ListView expensesListView = (ListView) findViewById(R.id.expenses_list);
        View emptyView = (RelativeLayout) findViewById(R.id.empty_view);

        expensesListView.setEmptyView(emptyView);
        if(expensesListView.getAdapter() == null) {
            totalLinearLayout.setVisibility(View.INVISIBLE);
        }
        else if(expensesListView.getAdapter().getCount() < 1) {
            totalLinearLayout.setVisibility(View.INVISIBLE);
        }

        expCursorAdapter = new ExpCursorAdapter(this, null);

        expensesListView.setAdapter(expCursorAdapter);

        expensesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), EditorActivity.class);
                intent.setData(ContentUris.withAppendedId(ExpEntry.CONTENT_URI, id));
                startActivity(intent);
            }
        });
        getLoaderManager().initLoader(EXP_LOADER, null, this);

        Button copyToClipboardButton = findViewById(R.id.copy_to_clipboard_button);
        copyToClipboardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //COPY TO CLIPBOARD
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Expense Report", report);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getApplicationContext(), "Report copied to clipboard!", Toast.LENGTH_SHORT).show();
            }
        });
    }

//    private void displayDatabaseInfo() {
//         To access our database, we instantiate our subclass of SQLiteOpenHelper
//         and pass the context, which is the current activity.
//
//         Perform this raw SQL query "SELECT * FROM pets"
//         to get a Cursor that contains all rows from the pets table.
//        Cursor cursor = db.rawQuery("SELECT * FROM " + PetEntry.TABLE_NAME, null);
//
//        String[] projection = {
//                PetEntry._ID,
//                PetEntry.COLUMN_NAME,
//                PetEntry.COLUMN_BREED,
//                PetEntry.COLUMN_GENDER,
//                PetEntry.COLUMN_WEIGHT
//        };
//        String selection = null;
//        String[] selectionArgs = null;
//
//        Cursor cursor = getContentResolver().query(PetEntry.CONTENT_URI, projection, selection, selectionArgs, null);
//
//        ListView petsListView = (ListView) findViewById(R.id.pet_list);
//
//        View emptyView = (RelativeLayout) findViewById(R.id.empty_view);
//        petsListView.setEmptyView(emptyView);
//
//        petCursorAdapter = new PetCursorAdapter(this, cursor);
//
//        petsListView.setAdapter(petCursorAdapter);
//
//        Cursor cursor = db.query(PetEntry.TABLE_NAME,
//                projection,
//                selection,
//                selectionArgs,
//                null,
//                null,
//                null);
//
//        try {
//            // Display the number of rows in the Cursor (which reflects the number of rows in the
//            // pets table in the database).
//            TextView displayView = (TextView) findViewById(R.id.text_view_pet);
//            //displayView.setText("Number of rows in pets database table: " + cursor.getCount());
//
//            displayView.append("Number of rows in pets database table = " + cursor.getCount());
//            displayView.append("\n\n" +
//                    PetEntry._ID + " - " +
//                    PetEntry.COLUMN_NAME + " - " +
//                    PetEntry.COLUMN_BREED + " - " +
//                    PetEntry.COLUMN_GENDER + " - " +
//                    PetEntry.COLUMN_WEIGHT +
//                    "\n");
//
//            int idIndex = cursor.getColumnIndex(PetEntry._ID);
//            int nameIndex = cursor.getColumnIndex(PetEntry.COLUMN_NAME);
//            int breedIndex = cursor.getColumnIndex(PetEntry.COLUMN_BREED);
//            int genderIndex = cursor.getColumnIndex(PetEntry.COLUMN_GENDER);
//            int weightIndex = cursor.getColumnIndex(PetEntry.COLUMN_WEIGHT);
//
//            while (cursor.moveToNext())
//            {
//                displayView.append("\n" +
//                                cursor.getString(idIndex) + " - " +
//                                cursor.getString(nameIndex) + " - " +
//                                cursor.getString(breedIndex) + " - " +
//                                cursor.getString(genderIndex) + " - " +
//                                cursor.getString(weightIndex)
//                );
//            }
//            displayView.append("\n");
//        } finally {
//            // Always close the cursor when you're done reading from it. This releases all its
//            // resources and makes it invalid.
//            //cursor.close();
//        }
//    }

    private void insertDummyData()
    {
        ContentValues values = new ContentValues();

        Cursor cursor = getContentResolver().query(ExpEntry.CONTENT_URI, null, null, null, null);
        if(cursor.getCount() == 0)
        {
            SQLiteDatabase db = mDbHelper.getReadableDatabase();
            db.execSQL("DELETE FROM sqlite_sequence WHERE name = '" +
                    ExpEntry.TABLE_NAME + "';");
        }

        values.put(ExpEntry.COLUMN_NAME, "Dinner");
        values.put(ExpEntry.COLUMN_DETAILS, "-");
        values.put(ExpEntry.COLUMN_AMOUNT, 100);
        values.put(ExpEntry.COLUMN_DISCOUNT, 10);

        /*Uri uri = */getContentResolver().insert(ExpEntry.CONTENT_URI, values);
    }

    private void deleteAllEntries()
    {
        AlertDialog alertDialog = new AlertDialog.Builder(CatalogActivity.this).create();
        alertDialog.setMessage(getString(R.string.delete_all_expenses_dialog_msg));
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.delete),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        getContentResolver().delete(ExpEntry.CONTENT_URI, null, null);
                        Toast.makeText(getApplicationContext(), "Data deleted successfully", Toast.LENGTH_SHORT).show();
                        totalLinearLayout.setVisibility(View.INVISIBLE);
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.no_delete),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertDummyData();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteAllEntries();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {
                ExpEntry._ID,
                ExpEntry.COLUMN_NAME,
                ExpEntry.COLUMN_DETAILS,
                ExpEntry.COLUMN_AMOUNT,
                ExpEntry.COLUMN_DISCOUNT
        };

        switch (id)
        {
            case EXP_LOADER :
                return new CursorLoader(this, ExpEntry.CONTENT_URI, projection, null, null, null);
            default :
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        expCursorAdapter.swapCursor(cursor);
        totalAmount = 0;
        totalDiscount = 0;
        report = "Expense Report : \n\n";

        if (cursor.moveToFirst()){
            do{
                String amount = cursor.getString(cursor.getColumnIndex(ExpEntry.COLUMN_AMOUNT));
                String discount = cursor.getString(cursor.getColumnIndex(ExpEntry.COLUMN_DISCOUNT));
                totalAmount += Integer.parseInt(amount);
                totalDiscount += Integer.parseInt(discount);
                String name = cursor.getString(cursor.getColumnIndex(ExpEntry.COLUMN_NAME));;
                String details = cursor.getString(cursor.getColumnIndex(ExpEntry.COLUMN_DETAILS));
                //report += name + " (" + details + ") : " + amount + " - " + discount + "\n";
                report += name;
                if(!details.equals(""))
                {
                    report += " (" + details + ")";
                }
                report += " : " + amount;
                if(!discount.equals("0"))
                {
                    report += " - " + discount;
                }
                report += "\n";

            }while(cursor.moveToNext());
        }

        report += "\n\nTotal amount : " + String.valueOf(totalAmount) + "\nTotal Discount : " + String.valueOf(totalDiscount);
        totalAmountTextView.setText(String.valueOf(totalAmount));
        totalDiscountTextView.setText(String.valueOf(totalDiscount));
        totalLinearLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        expCursorAdapter.swapCursor(null);
    }
}
