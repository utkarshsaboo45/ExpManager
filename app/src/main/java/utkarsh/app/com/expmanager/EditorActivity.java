package utkarsh.app.com.expmanager;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import utkarsh.app.com.expmanager.data.ExpContract.ExpEntry;
import utkarsh.app.com.expmanager.data.ExpCursorAdapter;
import utkarsh.app.com.expmanager.data.ExpDbHelper;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = "EditorActivity";
    private static final int EXP_LOADER = 1;

    private EditText mNameEditText;
    private EditText mDetailsEditText;
    private EditText mAmountEditText;
    private EditText mDiscountEditText;

    private ExpDbHelper mDbHelper;

    private Uri intentUri;

    private boolean mExpHasChanged = false;

    // OnTouchListener that listens for any user touches on a View, implying that they are modifying
    // the view, and we change the mPetHasChanged boolean to true.

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mExpHasChanged = true;
            return false;
        }
    };

    private ExpCursorAdapter expCursorAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.edit_expense_name);
        mDetailsEditText = (EditText) findViewById(R.id.edit_expense_details);
        mAmountEditText = (EditText) findViewById(R.id.edit_expense_amount);
        mDiscountEditText = (EditText) findViewById(R.id.edit_expense_discount);

        mNameEditText.setOnTouchListener(mTouchListener);
        mDetailsEditText.setOnTouchListener(mTouchListener);
        mAmountEditText.setOnTouchListener(mTouchListener);
        mDiscountEditText.setOnTouchListener(mTouchListener);

        mDbHelper = new ExpDbHelper(this);

        expCursorAdapter = new ExpCursorAdapter(this, null);

        intentUri = getIntent().getData();
        Log.i(LOG_TAG, "URI = " + String.valueOf(intentUri));

        if (intentUri == null)
            getSupportActionBar().setTitle("Add an expense");
        else {
            getSupportActionBar().setTitle("Edit an expense");
            getLoaderManager().initLoader(EXP_LOADER, null, this);
        }
    }

    public void insertData() {
        String name = mNameEditText.getText().toString().trim();
        String details = mDetailsEditText.getText().toString().trim();
        String tempAmount = mAmountEditText.getText().toString().trim();
        String tempDiscount = mDiscountEditText.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Expense not entered!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (TextUtils.isEmpty(tempAmount)) {
            tempAmount = "0";
        }

        if (TextUtils.isEmpty(tempDiscount)) {
            tempDiscount = "0";
        }

        int amount = Integer.parseInt(tempAmount);
        int discount = Integer.parseInt(tempDiscount);

//        Log.i(LOG_TAG, "URI = " + name);
//        Log.i(LOG_TAG, "URI = " + details);
//        Log.i(LOG_TAG, "URI = " + String.valueOf(amount));
//        Log.i(LOG_TAG, "URI = " + String.valueOf(discount));

        ContentValues values = new ContentValues();

        Log.i(LOG_TAG, "URI = " + values);

        Cursor cursor = getContentResolver().query(ExpEntry.CONTENT_URI, null, null, null, null);

        Log.i(LOG_TAG, "URI = " + String.valueOf(cursor));

        if (cursor.getCount() == 0) {
            SQLiteDatabase db = mDbHelper.getReadableDatabase();
            db.execSQL("DELETE FROM sqlite_sequence WHERE name = '" +
                    ExpEntry.TABLE_NAME + "';");
        }

        values.put(ExpEntry.COLUMN_NAME, name);
        values.put(ExpEntry.COLUMN_DETAILS, details);
        values.put(ExpEntry.COLUMN_AMOUNT, amount);
        values.put(ExpEntry.COLUMN_DISCOUNT, discount);

        /*Uri uri = */
        getContentResolver().insert(ExpEntry.CONTENT_URI, values);
        Toast.makeText(EditorActivity.this, R.string.expense_saved, Toast.LENGTH_SHORT).show();
    }

    private void updateData() {
        String name = mNameEditText.getText().toString().trim();
        String details = mDetailsEditText.getText().toString().trim();
        String tempAmount = mAmountEditText.getText().toString().trim();
        String tempDiscount = mDiscountEditText.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Expense not entered!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (TextUtils.isEmpty(tempAmount)) {
            tempAmount = "0";
        }

        if (TextUtils.isEmpty(tempDiscount)) {
            tempDiscount = "0";
        }

        int amount = Integer.parseInt(tempAmount);
        int discount = Integer.parseInt(tempDiscount);

        ContentValues values = new ContentValues();

        Cursor cursor = getContentResolver().query(ExpEntry.CONTENT_URI, null, null, null, null);

        values.put(ExpEntry.COLUMN_NAME, name);
        values.put(ExpEntry.COLUMN_DETAILS, details);
        values.put(ExpEntry.COLUMN_AMOUNT, amount);
        values.put(ExpEntry.COLUMN_DISCOUNT, discount);

        int noOfRowsUpdated = getContentResolver().update(intentUri, values, null, null);
        // Show a toast message depending on whether or not the update was successful.
        if (noOfRowsUpdated == 0) {
            // If no rows were affected, then there was an error with the update.
            Toast.makeText(this, getString(R.string.editor_update_expense_failed),
                    Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the update was successful and we can display a toast.
            Toast.makeText(this, getString(R.string.editor_update_expense_successful),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void deletePet() {
        AlertDialog alertDialog = new AlertDialog.Builder(EditorActivity.this).create();
        alertDialog.setMessage(getString(R.string.delete_expense_dialog_msg));//"Exit without saving pet?");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.delete),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        getContentResolver().delete(intentUri, null, null);
                        Toast.makeText(getApplicationContext(), "Expense deleted successfully", Toast.LENGTH_SHORT).show();
                        finish();
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
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        if (intentUri == null)
            invalidateOptionsMenu();
        //menu.getItem(1).setEnabled(false);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new pet, hide the "Delete" menu item.
        if (intentUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu

        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                //Insert data
                //Toast.makeText(EditorActivity.this, "Working", Toast.LENGTH_SHORT).show();
                if (intentUri == null) {
                    insertData();
                    finish();
                } else {
                    updateData();
                    finish();
                }

                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Do nothing for now
                if (intentUri != null) {
                    deletePet();
                }
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                if (mExpHasChanged) {
                    AlertDialog alertDialog = new AlertDialog.Builder(EditorActivity.this).create();
                    //alertDialog.setTitle("Alert");
                    alertDialog.setMessage("Discard your changes and quit editing?");//"Exit without saving pet?");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Discard",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    // Navigate back to parent activity (CatalogActivity)
                                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                                }
                            });
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Keep Editing",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                } else
                    // Navigate back to parent activity (CatalogActivity)
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        switch (id) {
            case EXP_LOADER:
                String[] projection = {
                        ExpEntry._ID,
                        ExpEntry.COLUMN_NAME,
                        ExpEntry.COLUMN_DETAILS,
                        ExpEntry.COLUMN_AMOUNT,
                        ExpEntry.COLUMN_DISCOUNT
                };
                return new CursorLoader(this, intentUri, projection, null, null, null);

            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        int expId = (int) ContentUris.parseId(intentUri);
        if (cursor.moveToFirst()) {
            Log.i("EditorActivity", "ID = " + expId);
            mNameEditText.setText(cursor.getString(cursor.getColumnIndex(ExpEntry.COLUMN_NAME)));
            mDetailsEditText.setText(cursor.getString(cursor.getColumnIndex(ExpEntry.COLUMN_DETAILS)));
            mAmountEditText.setText(cursor.getString(cursor.getColumnIndex(ExpEntry.COLUMN_AMOUNT)));
            mDiscountEditText.setText(cursor.getString(cursor.getColumnIndex(ExpEntry.COLUMN_DISCOUNT)));
        }
        cursor.close();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEditText.setText("");
        mDetailsEditText.setText("");
        mAmountEditText.setText("");
        mDiscountEditText.setText("");
        expCursorAdapter.swapCursor(null);
    }

}

