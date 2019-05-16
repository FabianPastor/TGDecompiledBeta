package org.telegram.ui.Cells;

import android.content.Context;
import android.text.TextUtils.TruncateAt;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;

public class BotSwitchCell extends FrameLayout {
    private TextView textView;

    public BotSwitchCell(Context context) {
        super(context);
        this.textView = new TextView(context);
        this.textView.setTextSize(1, 15.0f);
        this.textView.setTextColor(Theme.getColor("chat_botSwitchToInlineText"));
        this.textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.textView.setSingleLine(true);
        this.textView.setEllipsize(TruncateAt.END);
        this.textView.setMaxLines(1);
        int i = 5;
        this.textView.setGravity(LocaleController.isRTL ? 5 : 3);
        TextView textView = this.textView;
        if (!LocaleController.isRTL) {
            i = 3;
        }
        addView(textView, LayoutHelper.createFrame(-2, -2.0f, i | 16, 14.0f, 0.0f, 14.0f, 0.0f));
    }

    /* Access modifiers changed, original: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(36.0f), NUM));
    }

    public void setText(String str) {
        this.textView.setText(str);
    }

    public TextView getTextView() {
        return this.textView;
    }
}
