package org.telegram.ui.Cells;

import android.content.Context;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.beta.R;
import org.telegram.ui.Components.LayoutHelper;

public class LocationLoadingCell extends FrameLayout {
    private ProgressBar progressBar;
    private TextView textView;

    public LocationLoadingCell(Context context) {
        super(context);
        this.progressBar = new ProgressBar(context);
        addView(this.progressBar, LayoutHelper.createFrame(-2, -2, 17));
        this.textView = new TextView(context);
        this.textView.setTextColor(-6710887);
        this.textView.setTextSize(1, 16.0f);
        this.textView.setText(LocaleController.getString("NoResult", R.string.NoResult));
        addView(this.textView, LayoutHelper.createFrame(-2, -2, 17));
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec((int) (((float) AndroidUtilities.dp(56.0f)) * 2.5f), NUM));
    }

    public void setLoading(boolean value) {
        int i;
        int i2 = 4;
        ProgressBar progressBar = this.progressBar;
        if (value) {
            i = 0;
        } else {
            i = 4;
        }
        progressBar.setVisibility(i);
        TextView textView = this.textView;
        if (!value) {
            i2 = 0;
        }
        textView.setVisibility(i2);
    }
}
