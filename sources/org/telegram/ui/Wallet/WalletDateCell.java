package org.telegram.ui.Wallet;

import android.content.Context;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;

public class WalletDateCell extends FrameLayout {
    private TextView dateTextView;

    public WalletDateCell(Context context) {
        super(context);
        this.dateTextView = new TextView(context);
        this.dateTextView.setTextSize(1, 16.0f);
        this.dateTextView.setTextColor(Theme.getColor("wallet_blackText"));
        this.dateTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        addView(this.dateTextView, LayoutHelper.createFrame(-2, -2.0f, 19, 18.0f, 0.0f, 18.0f, 0.0f));
    }

    /* Access modifiers changed, original: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), NUM), MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(AndroidUtilities.dp(38.0f)), NUM));
    }

    public void setDate(long j) {
        this.dateTextView.setText(LocaleController.formatDateChat(j));
    }

    public void setText(String str) {
        this.dateTextView.setText(str);
    }
}
