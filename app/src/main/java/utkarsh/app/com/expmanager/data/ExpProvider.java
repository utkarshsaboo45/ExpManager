package utkarsh.app.com.expmanager.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.widget.Toast;

import utkarsh.app.com.expmanager.data.ExpContract.ExpEntry;

public class ExpProvider extends ContentProvider {

    /** Tag for the log messages */
    public static final String LOG_TAG = ExpProvider.class.getSimpleName();

    private ExpDbHelper mDbHelper;

    //URI MATCHER CODES
    private static final int EXPS = 100;
    private static final int EXP_ID = 101;

    //URI MATCHER
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(ExpContract.CONTENT_AUTHORITY, ExpContract.PATH_EXP, EXPS);
        sUriMatcher.addURI(ExpContract.CONTENT_AUTHORITY, ExpContract.PATH_EXP + "/#", EXP_ID);
    }
    /**
     * Initialize the provider and the database helper object.
     */
    @Override
    public boolean onCreate() {
        // Create and initialize a PetDbHelper object to gain access to the pets database.
        // Make sure the variable is a global variable, so it can be referenced from other
        // ContentProvider methods.
        mDbHelper = new ExpDbHelper(getContext());
        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        Cursor cursor;

        int match = sUriMatcher.match(uri);

        switch (match)
        {
            case EXP_ID :
                selection = ExpEntry._ID + " = ?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                cursor = db.query(ExpEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case EXPS :
                cursor = db.query(ExpEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default :
                throw new IllegalArgumentException("Cannot query unknown URI" + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {

        String name = contentValues.getAsString(ExpEntry.COLUMN_NAME);
        //String details = contentValues.getAsString(ExpEntry.COLUMN_DETAILS);
        Integer amount = contentValues.getAsInteger(ExpEntry.COLUMN_AMOUNT);
        //Integer discount = contentValues.getAsInteger(ExpEntry.COLUMN_DISCOUNT);

        //VALIDATION
        if(name == null)
            throw new IllegalArgumentException("A valid expense name expected!");

        if(amount == null || amount < 0)
            throw new IllegalArgumentException("A valid amount expected!");

        final int match = sUriMatcher.match(uri);

        switch (match)
        {
            case EXPS :
                return insertExpense(uri, contentValues);
            default :
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {

        final int match = sUriMatcher.match(uri);
        switch (match)
        {
            case EXP_ID :
                selection = ExpEntry._ID + " = ?";
                selectionArgs = new String[] {
                        String.valueOf(ContentUris.parseId(uri))
                };
                return updateExpense(uri, contentValues, selection, selectionArgs);
            case EXPS :
                return updateExpense(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update not supported for " + uri);
        }
    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int match = sUriMatcher.match(uri);
        switch (match)
        {
            case EXP_ID :
                selection = ExpEntry._ID + " = ?";
                selectionArgs = new String[] {
                        String.valueOf(ContentUris.parseId(uri))
                };
                return deleteExpense(uri, selection, selectionArgs);
            case EXPS :
                return deleteExpense(uri, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Delete not supported for " + uri);
        }
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {

        int match = sUriMatcher.match(uri);
        switch (match)
        {
            case EXP_ID :
                return ExpEntry.CONTENT_ITEM_TYPE;
            case EXPS :
                return ExpEntry.CONTENT_LIST_TYPE;
            default :
                throw new IllegalStateException("Unknown URI " + uri + "with match " + match);
        }
    }

    private Uri insertExpense(Uri uri, ContentValues contentValues)
    {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long newRowId = db.insert(ExpEntry.TABLE_NAME, null, contentValues);
        if(newRowId == -1)
        {
            Toast.makeText(getContext(), "Error inserting Expense", Toast.LENGTH_SHORT).show();
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, newRowId);
    }

    private int updateExpense(Uri uri,
                          ContentValues contentValues,
                          String selection,
                          String[] selecionArgs)
    {
        if(contentValues.size() == 0)
            return 0;

        String name = contentValues.getAsString(ExpEntry.COLUMN_NAME);
        String details = contentValues.getAsString(ExpEntry.COLUMN_DETAILS);
        Integer amount = contentValues.getAsInteger(ExpEntry.COLUMN_AMOUNT);
        Integer discount = contentValues.getAsInteger(ExpEntry.COLUMN_DISCOUNT);

        if(contentValues.containsKey(ExpEntry.COLUMN_NAME) && name == null)
            throw new IllegalArgumentException("A valid name expected!");

        if(contentValues.containsKey(ExpEntry.COLUMN_DETAILS) && (details == null))
            throw new IllegalArgumentException("Valid details expected!");

        if(contentValues.containsKey(ExpEntry.COLUMN_AMOUNT) && (amount != null && amount < 0))
            throw new IllegalArgumentException("A valid weight expected!");

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long noOfRowsUpdated = db.update(ExpEntry.TABLE_NAME, contentValues, selection, selecionArgs);
        if(noOfRowsUpdated != 0)
            getContext().getContentResolver().notifyChange(uri, null);
        return (int)noOfRowsUpdated;
    }

    private int deleteExpense(Uri uri,
                          String selection,
                          String[] selecionArgs)
    {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long noOfRowsDeleted = db.delete(ExpEntry.TABLE_NAME, selection, selecionArgs);
        if(noOfRowsDeleted != 0)
            getContext().getContentResolver().notifyChange(uri, null);
        return (int)noOfRowsDeleted;
    }
}
