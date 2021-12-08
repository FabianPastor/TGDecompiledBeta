package org.telegram.ui.Charts.view_data;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;

public class PieLegendView extends LegendSignatureView {
    TextView signature;
    TextView value;

    public PieLegendView(Context context) {
        super(context);
        LinearLayout root = new LinearLayout(getContext());
        root.setPadding(AndroidUtilities.dp(4.0f), AndroidUtilities.dp(2.0f), AndroidUtilities.dp(4.0f), AndroidUtilities.dp(2.0f));
        TextView textView = new TextView(getContext());
        this.signature = textView;
        root.addView(textView);
        this.signature.getLayoutParams().width = AndroidUtilities.dp(96.0f);
        TextView textView2 = new TextView(getContext());
        this.value = textView2;
        root.addView(textView2);
        addView(root);
        this.value.setTypeface(Typeface.create("sans-serif-medium", 0));
        setPadding(AndroidUtilities.dp(12.0f), AndroidUtilities.dp(12.0f), AndroidUtilities.dp(12.0f), AndroidUtilities.dp(12.0f));
        this.chevron.setVisibility(8);
        this.zoomEnabled = false;
    }

    public void recolor() {
        if (this.signature != null) {
            super.recolor();
            this.signature.setTextColor(Theme.getColor("dialogTextBlack"));
        }
    }

    public void setData(String name, int value2, int color) {
        this.signature.setText(name);
        this.value.setText(Integer.toString(value2));
        this.value.setTextColor(color);
    }

    public void setSize(int n) {
    }

    public void setData(int index, long date, ArrayList<LineViewData> arrayList) {
    }
}
