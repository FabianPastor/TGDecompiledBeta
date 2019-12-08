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

    public PickerBottomLayout(Context context, boolean z) {
        Context context2 = context;
        super(context);
        setBackgroundColor(Theme.getColor(z ? "dialogBackground" : "windowBackgroundWhite"));
        this.cancelButton = new TextView(context2);
        this.cancelButton.setTextSize(1, 14.0f);
        String str = "picker_enabledButton";
        this.cancelButton.setTextColor(Theme.getColor(str));
        this.cancelButton.setGravity(17);
        this.cancelButton.setBackgroundDrawable(Theme.createSelectorDrawable(NUM, 0));
        this.cancelButton.setPadding(AndroidUtilities.dp(29.0f), 0, AndroidUtilities.dp(29.0f), 0);
        this.cancelButton.setText(LocaleController.getString("Cancel", NUM).toUpperCase());
        String str2 = "fonts/rmedium.ttf";
        this.cancelButton.setTypeface(AndroidUtilities.getTypeface(str2));
        addView(this.cancelButton, LayoutHelper.createFrame(-2, -1, 51));
        this.doneButton = new LinearLayout(context2);
        this.doneButton.setOrientation(0);
        this.doneButton.setBackgroundDrawable(Theme.createSelectorDrawable(NUM, 0));
        this.doneButton.setPadding(AndroidUtilities.dp(29.0f), 0, AndroidUtilities.dp(29.0f), 0);
        addView(this.doneButton, LayoutHelper.createFrame(-2, -1, 53));
        this.doneButtonBadgeTextView = new TextView(context2);
        this.doneButtonBadgeTextView.setTypeface(AndroidUtilities.getTypeface(str2));
        this.doneButtonBadgeTextView.setTextSize(1, 13.0f);
        this.doneButtonBadgeTextView.setTextColor(Theme.getColor("picker_badgeText"));
        this.doneButtonBadgeTextView.setGravity(17);
        this.doneButtonBadgeTextView.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(11.0f), Theme.getColor("picker_badge")));
        this.doneButtonBadgeTextView.setMinWidth(AndroidUtilities.dp(23.0f));
        this.doneButtonBadgeTextView.setPadding(AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f), AndroidUtilities.dp(1.0f));
        this.doneButton.addView(this.doneButtonBadgeTextView, LayoutHelper.createLinear(-2, 23, 16, 0, 0, 10, 0));
        this.doneButtonTextView = new TextView(context2);
        this.doneButtonTextView.setTextSize(1, 14.0f);
        this.doneButtonTextView.setTextColor(Theme.getColor(str));
        this.doneButtonTextView.setGravity(17);
        this.doneButtonTextView.setCompoundDrawablePadding(AndroidUtilities.dp(8.0f));
        this.doneButtonTextView.setText(LocaleController.getString("Send", NUM).toUpperCase());
        this.doneButtonTextView.setTypeface(AndroidUtilities.getTypeface(str2));
        this.doneButton.addView(this.doneButtonTextView, LayoutHelper.createLinear(-2, -2, 16));
    }

    public void updateSelectedCount(int i, boolean z) {
        String str = "picker_enabledButton";
        if (i == 0) {
            this.doneButtonBadgeTextView.setVisibility(8);
            if (z) {
                String str2 = "picker_disabledButton";
                this.doneButtonTextView.setTag(str2);
                this.doneButtonTextView.setTextColor(Theme.getColor(str2));
                this.doneButton.setEnabled(false);
                return;
            }
            this.doneButtonTextView.setTag(str);
            this.doneButtonTextView.setTextColor(Theme.getColor(str));
            return;
        }
        this.doneButtonBadgeTextView.setVisibility(0);
        this.doneButtonBadgeTextView.setText(String.format("%d", new Object[]{Integer.valueOf(i)}));
        this.doneButtonTextView.setTag(str);
        this.doneButtonTextView.setTextColor(Theme.getColor(str));
        if (z) {
            this.doneButton.setEnabled(true);
        }
    }
}
