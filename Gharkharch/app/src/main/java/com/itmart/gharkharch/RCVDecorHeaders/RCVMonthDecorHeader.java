package com.itmart.gharkharch.RCVDecorHeaders;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.itmart.gharkharch.Adapters.SimpleListAdapter;
import com.itmart.gharkharch.DataModels.ExpenseItem;
import com.itmart.gharkharch.DataModels.GenericItem;
import com.itmart.gharkharch.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RCVMonthDecorHeader extends RecyclerView.ItemDecoration {
    private final int headerHeight;
    private final Paint headerPaint;
    private final Paint textPaint;
    private final SimpleDateFormat monthYearFormat;

    public RCVMonthDecorHeader(Context context) {
        headerHeight = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 40, context.getResources().getDisplayMetrics());

        headerPaint = new Paint();
        headerPaint.setColor(context.getColor(R.color.secondaryColor));

        textPaint = new Paint();
        textPaint.setColor(context.getColor(R.color.primaryTextColor));
        textPaint.setTextSize(40f);
        textPaint.setAntiAlias(true);

        monthYearFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
    }

    @Override
    public void onDrawOver(@NonNull Canvas canvas, RecyclerView parent, @NonNull RecyclerView.State state) {
        SimpleListAdapter adapter = (SimpleListAdapter) parent.getAdapter();
        if (adapter == null || adapter.getItemCount() == 0) return;

        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();

        View topChild = parent.getChildAt(0);
        int topChildPosition = parent.getChildAdapterPosition(topChild);
        if (topChildPosition == RecyclerView.NO_POSITION) return;

        String currentHeader = getMonthYearForPosition(topChildPosition, adapter);
        float headerTop = parent.getPaddingTop();

        // Check if next header is coming up to push the current one
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            int position = parent.getChildAdapterPosition(child);

            if (isFirstInMonth(position, adapter)) {
                float childTop = child.getTop();
                if (childTop < headerHeight && childTop > 0) {
                    headerTop = childTop - headerHeight;
                    break;
                }
            }
        }

        // Draw sticky header background
        canvas.drawRect(left, headerTop, right, headerTop + headerHeight, headerPaint);

        // Draw sticky header text
        float textY = headerTop + headerHeight / 2f + textPaint.getTextSize() / 3f;
        canvas.drawText(currentHeader, left + 40, textY, textPaint);
    }

    private boolean isFirstInMonth(int position, SimpleListAdapter adapter) {
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

    private String getMonthYearForPosition(int position, SimpleListAdapter adapter) {
        GenericItem item = adapter.getItemAtCurrentPos(position);
        if (item instanceof ExpenseItem) {
            return monthYearFormat.format(new Date(((ExpenseItem) item).get_date()));
        }
        return "";
    }
}
