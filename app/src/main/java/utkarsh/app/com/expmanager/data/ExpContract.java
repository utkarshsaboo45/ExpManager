package utkarsh.app.com.expmanager.data;

import android.content.ContentResolver;
import android.content.UriMatcher;
import android.net.Uri;
import android.provider.BaseColumns;

public final class ExpContract {

    private ExpContract() {}

    //CONTENT AUTHORITY
    public static final String CONTENT_AUTHORITY = "utkarsh.app.com.expmanager";
    //BASE URI
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    //PATH FOR THE TABLES
    public static final String PATH_EXP = "exps";

    public static abstract class ExpEntry implements BaseColumns {

        //FULL URI
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_EXP);
        //TABLE NAME
        public static final String TABLE_NAME = "exps";
        //COLUMNS
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_DETAILS = "details";
        public static final String COLUMN_AMOUNT = "amount";
        public static final String COLUMN_DISCOUNT = "discount";

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of pets.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_EXP;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single pet.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_EXP;

    }
}
