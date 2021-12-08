package org.telegram.ui.Components;

import android.content.Context;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;

public class PickerBottomLayout extends FrameLayout {
    public TextView cancelButton;
    public LinearLayout doneButton;
    public TextView doneButtonBadgeTextView;
    public TextView doneButtonTextView;

    public PickerBottomLayout(Context context) {
        this(context, true);
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public PickerBottomLayout(Context context, boolean darkTheme) {
        super(context);
        Context context2 = context;
        setBackgroundColor(Theme.getColor(darkTheme ? "dialogBackground" : "windowBackgroundWhite"));
        TextView textView = new TextView(context2);
        this.cancelButton = textView;
        textView.setTextSize(1, 14.0f);
        this.cancelButton.setTextColor(Theme.getColor("picker_enabledButton"));
        this.cancelButton.setGravity(17);
        this.cancelButton.setBackgroundDrawable(Theme.createSelectorDrawable(NUM, 0));
        this.cancelButton.setPadding(AndroidUtilities.dp(33.0f), 0, AndroidUtilities.dp(33.0f), 0);
        this.cancelButton.setText(LocaleController.getString("Cancel", NUM).toUpperCase());
        this.cancelButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        addView(this.cancelButton, LayoutHelper.createFrame(-2, -1, 51));
        LinearLayout linearLayout = new LinearLayout(context2);
        this.doneButton = linearLayout;
        linearLayout.setOrientation(0);
        this.doneButton.setBackgroundDrawable(Theme.createSelectorDrawable(NUM, 0));
        this.doneButton.setPadding(AndroidUtilities.dp(33.0f), 0, AndroidUtilities.dp(33.0f), 0);
        addView(this.doneButton, LayoutHelper.createFrame(-2, -1, 53));
        TextView textView2 = new TextView(context2);
        this.doneButtonBadgeTextView = textView2;
        textView2.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.doneButtonBadgeTextView.setTextSize(1, 13.0f);
        this.doneButtonBadgeTextView.setTextColor(Theme.getColor("picker_badgeText"));
        this.doneButtonBadgeTextView.setGravity(17);
        this.doneButtonBadgeTextView.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(11.0f), Theme.getColor("picker_badge")));
        this.doneButtonBadgeTextView.setMinWidth(AndroidUtilities.dp(23.0f));
        this.doneButtonBadgeTextView.setPadding(AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f), AndroidUtilities.dp(1.0f));
        this.doneButton.addView(this.doneButtonBadgeTextView, LayoutHelper.createLinear(-2, 23, 16, 0, 0, 10, 0));
        TextView textView3 = new TextView(context2);
        this.doneButtonTextView = textView3;
        textView3.setTextSize(1, 14.0f);
        this.doneButtonTextView.setTextColor(Theme.getColor("picker_enabledButton"));
        this.doneButtonTextView.setGravity(17);
        this.doneButtonTextView.setCompoundDrawablePadding(AndroidUtilities.dp(8.0f));
        this.doneButtonTextView.setText(LocaleController.getString("Send", NUM).toUpperCase());
        this.doneButtonTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.doneButton.addView(this.doneButtonTextView, LayoutHelper.createLinear(-2, -2, 16));
    }

    public void updateSelectedCount(int count, boolean disable) {
        if (count == 0) {
            this.doneButtonBadgeTextView.setVisibility(8);
            if (disable) {
                this.doneButtonTextView.setTag("picker_disabledButton");
                this.doneButtonTextView.setTextColor(Theme.getColor("picker_disabledButton"));
                this.doneButton.setEnabled(false);
                return;
            }
            this.doneButtonTextView.setTag("picker_enabledButton");
            this.doneButtonTextView.setTextColor(Theme.getColor("picker_enabledButton"));
            return;
        }
        this.doneButtonBadgeTextView.setVisibility(0);
        this.doneButtonBadgeTextView.setText(String.format("%d", new Object[]{Integer.valueOf(count)}));
        this.doneButtonTextView.setTag("picker_enabledButton");
        this.doneButtonTextView.setTextColor(Theme.getColor("picker_enabledButton"));
        if (disable) {
            this.doneButton.setEnabled(true);
        }
    }
}
