package org.telegram.ui.Cells;

import android.content.Context;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.Components.LayoutHelper;

public class GreySectionCell extends FrameLayout {
    private TextView textView = new TextView(getContext());

    public GreySectionCell(Context context) {
        int i;
        int i2 = 5;
        super(context);
        setBackgroundColor(-855310);
        this.textView.setTextSize(1, 13.0f);
        this.textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.textView.setTextColor(-7697782);
        TextView textView = this.textView;
        if (LocaleController.isRTL) {
            i = 5;
        } else {
            i = 3;
        }
        textView.setGravity(i | 16);
        View view = this.textView;
        if (!LocaleController.isRTL) {
            i2 = 3;
        }
        addView(view, LayoutHelper.createFrame(-1, -1.0f, i2 | 48, 16.0f, 0.0f, 16.0f, 0.0f));
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(32.0f), NUM));
    }

    public void setText(String text) {
        this.textView.setText(text);
    }
}
