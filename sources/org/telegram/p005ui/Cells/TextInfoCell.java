package org.telegram.p005ui.Cells;

import android.content.Context;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.p005ui.ActionBar.Theme;
import org.telegram.p005ui.Components.LayoutHelper;

/* renamed from: org.telegram.ui.Cells.TextInfoCell */
public class TextInfoCell extends FrameLayout {
    private TextView textView;

    public TextInfoCell(Context context) {
        super(context);
        this.textView = new TextView(context);
        this.textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText5));
        this.textView.setTextSize(1, 13.0f);
        this.textView.setGravity(17);
        this.textView.setPadding(0, AndroidUtilities.m10dp(19.0f), 0, AndroidUtilities.m10dp(19.0f));
        addView(this.textView, LayoutHelper.createFrame(-2, -2.0f, 17, 17.0f, 0.0f, 17.0f, 0.0f));
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), NUM), MeasureSpec.makeMeasureSpec(0, 0));
    }

    public void setText(String text) {
        this.textView.setText(text);
    }
}
