package DatabaseHelper;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class GlobalData {

    private Context context;
    private List<Integer> expensesData;

    public GlobalData() {
    }

    public GlobalData(Context context) {
        this.context = context;
    }

    public List<Integer> getExpensesData() {
        expensesData = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            expensesData.add(i);
        }
        return expensesData;
    }

    public void generateSnackBar(View view, String msg) {
        Snackbar snackbar = Snackbar.make(view, msg, Snackbar.LENGTH_LONG);
        snackbar.setAction("Ok", v -> {
            snackbar.dismiss();
        });
        new Handler(Looper.getMainLooper()).post(snackbar::show);
    }
}
