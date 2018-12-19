package utkarsh.app.com.expmanager.data;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import utkarsh.app.com.expmanager.R;
import utkarsh.app.com.expmanager.data.ExpContract.ExpEntry;

public class ExpCursorAdapter extends CursorAdapter {

    public ExpCursorAdapter(Context context, Cursor c)
    {
        super(context, c, 0);

    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.expense_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView nameTextView = (TextView) view.findViewById(R.id.exp_name);
        TextView detailsTextView = (TextView) view.findViewById(R.id.exp_details);
        TextView amtTextView = (TextView) view.findViewById(R.id.exp_amount);
        TextView discTextView = (TextView) view.findViewById(R.id.exp_discount);

        String name = cursor.getString(cursor.getColumnIndexOrThrow(ExpEntry.COLUMN_NAME));
        String details = cursor.getString(cursor.getColumnIndexOrThrow(ExpEntry.COLUMN_DETAILS));
        String amount = cursor.getString(cursor.getColumnIndexOrThrow(ExpEntry.COLUMN_AMOUNT));
        String discount = cursor.getString(cursor.getColumnIndexOrThrow(ExpEntry.COLUMN_DISCOUNT));

        nameTextView.setText(name);
        detailsTextView.setText(details);
        amtTextView.setText(amount);
        discTextView.setText(discount);
    }


}
