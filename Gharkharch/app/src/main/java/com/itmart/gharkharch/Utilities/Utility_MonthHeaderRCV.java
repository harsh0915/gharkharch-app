package com.itmart.gharkharch.Utilities;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.itmart.gharkharch.Adapters.ManageExpensesAdapter;
import com.itmart.gharkharch.DataModels.ExpenseItem;
import com.itmart.gharkharch.DataModels.GenericItem;
import com.itmart.gharkharch.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utility_MonthHeaderRCV extends RecyclerView.ItemDecoration {
    private final int headerHeight;
    private final Paint headerPaint;
    private final Paint textPaint;
    private final SimpleDateFormat monthYearFormat;

    public Utility_MonthHeaderRCV(Context context) {
        headerHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, context.getResources().getDisplayMetrics());

        headerPaint = new Paint();
        headerPaint.setColor(context.getColor(R.color.secondaryColor));

        textPaint = new Paint();
        textPaint.setColor(context.getColor(R.color.primaryTextColor));
        textPaint.setTextSize(40f);
        textPaint.setAntiAlias(true);

        monthYearFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
    }

    @Override
    public void onDraw(@NonNull Canvas canvas, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        ManageExpensesAdapter adapter = (ManageExpensesAdapter) parent.getAdapter();
        if (adapter == null) return;

        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();

        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            int position = parent.getChildAdapterPosition(child);
            if (position == RecyclerView.NO_POSITION) continue;

            if (isFirstInMonth(position, adapter)) {
                String headerText = getMonthYearForPosition(position, adapter);

                float top = child.getTop() - headerHeight;
                float bottom = child.getTop();

                // Draw background
                canvas.drawRect(left, top, right, bottom, headerPaint);

                // Draw text
                float textY = top + headerHeight / 2f + textPaint.getTextSize() / 3f;
                canvas.drawText(headerText, left + 40, textY, textPaint);
            }
        }
    }


    private boolean isFirstInMonth(int position, ManageExpensesAdapter adapter) {
        if (position == 0) return true;

        GenericItem current = adapter.getItemAtCurrentPos(position);
        GenericItem previous = adapter.getItemAtCurrentPos(position - 1);

        if (current instanceof ExpenseItem && previous instanceof ExpenseItem) {
            String currentMonthYear = monthYearFormat.format(new Date(((ExpenseItem) current).get_date()));
            String prevMonthYear = monthYearFormat.format(new Date(((ExpenseItem) previous).get_date()));
            return !currentMonthYear.equals(prevMonthYear);
        }
        return false;
    }

    private String getMonthYearForPosition(int position, ManageExpensesAdapter adapter) {
        GenericItem item = adapter.getItemAtCurrentPos(position);
        if (item instanceof ExpenseItem) {
            return monthYearFormat.format(new Date(((ExpenseItem) item).get_date()));
        }
        return "";
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        if (position == RecyclerView.NO_POSITION) return;

        ManageExpensesAdapter adapter = (ManageExpensesAdapter) parent.getAdapter();
        if (adapter == null) return;

        // If it's the first item in a month, add top padding for the header
        if (isFirstInMonth(position, adapter)) {
            outRect.top = headerHeight;
        } else {
            outRect.top = 0;
        }
    }
}
