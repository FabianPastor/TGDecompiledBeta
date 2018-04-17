package org.telegram.ui.Cells;

import android.content.Context;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;

public class HeaderCell extends FrameLayout {
    private TextView textView = new TextView(getContext());

    public HeaderCell(Context context) {
        super(context);
        this.textView.setTextSize(1, 15.0f);
        this.textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        int i = 3;
        this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        View view = this.textView;
        if (LocaleController.isRTL) {
            i = 5;
        }
        addView(view, LayoutHelper.createFrame(-1, -1.0f, i | 48, 17.0f, 15.0f, 17.0f, 0.0f));
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueHeader));
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(38.0f), NUM));
    }

    public void setText(String text) {
        this.textView.setText(text);
    }
}
