package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.drawable.Drawable;
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
    private boolean isDarkTheme;

    public PickerBottomLayout(Context context) {
        this(context, true);
    }

    public PickerBottomLayout(Context context, boolean z) {
        Drawable createRoundRectDrawable;
        Context context2 = context;
        super(context);
        this.isDarkTheme = z;
        setBackgroundColor(this.isDarkTheme ? -15066598 : Theme.getColor("windowBackgroundWhite"));
        this.cancelButton = new TextView(context2);
        this.cancelButton.setTextSize(1, 14.0f);
        String str = "picker_enabledButton";
        int i = -1;
        this.cancelButton.setTextColor(this.isDarkTheme ? -1 : Theme.getColor(str));
        this.cancelButton.setGravity(17);
        int i2 = -12763843;
        this.cancelButton.setBackgroundDrawable(Theme.createSelectorDrawable(this.isDarkTheme ? -12763843 : NUM, 0));
        this.cancelButton.setPadding(AndroidUtilities.dp(29.0f), 0, AndroidUtilities.dp(29.0f), 0);
        this.cancelButton.setText(LocaleController.getString("Cancel", NUM).toUpperCase());
        String str2 = "fonts/rmedium.ttf";
        this.cancelButton.setTypeface(AndroidUtilities.getTypeface(str2));
        addView(this.cancelButton, LayoutHelper.createFrame(-2, -1, 51));
        this.doneButton = new LinearLayout(context2);
        this.doneButton.setOrientation(0);
        LinearLayout linearLayout = this.doneButton;
        if (!this.isDarkTheme) {
            i2 = NUM;
        }
        linearLayout.setBackgroundDrawable(Theme.createSelectorDrawable(i2, 0));
        this.doneButton.setPadding(AndroidUtilities.dp(29.0f), 0, AndroidUtilities.dp(29.0f), 0);
        addView(this.doneButton, LayoutHelper.createFrame(-2, -1, 53));
        this.doneButtonBadgeTextView = new TextView(context2);
        this.doneButtonBadgeTextView.setTypeface(AndroidUtilities.getTypeface(str2));
        this.doneButtonBadgeTextView.setTextSize(1, 13.0f);
        this.doneButtonBadgeTextView.setTextColor(this.isDarkTheme ? -1 : Theme.getColor("picker_badgeText"));
        this.doneButtonBadgeTextView.setGravity(17);
        if (this.isDarkTheme) {
            createRoundRectDrawable = Theme.createRoundRectDrawable(AndroidUtilities.dp(11.0f), -10043398);
        } else {
            createRoundRectDrawable = Theme.createRoundRectDrawable(AndroidUtilities.dp(11.0f), Theme.getColor("picker_badge"));
        }
        this.doneButtonBadgeTextView.setBackgroundDrawable(createRoundRectDrawable);
        this.doneButtonBadgeTextView.setMinWidth(AndroidUtilities.dp(23.0f));
        this.doneButtonBadgeTextView.setPadding(AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f), AndroidUtilities.dp(1.0f));
        this.doneButton.addView(this.doneButtonBadgeTextView, LayoutHelper.createLinear(-2, 23, 16, 0, 0, 10, 0));
        this.doneButtonTextView = new TextView(context2);
        this.doneButtonTextView.setTextSize(1, 14.0f);
        TextView textView = this.doneButtonTextView;
        if (!this.isDarkTheme) {
            i = Theme.getColor(str);
        }
        textView.setTextColor(i);
        this.doneButtonTextView.setGravity(17);
        this.doneButtonTextView.setCompoundDrawablePadding(AndroidUtilities.dp(8.0f));
        this.doneButtonTextView.setText(LocaleController.getString("Send", NUM).toUpperCase());
        this.doneButtonTextView.setTypeface(AndroidUtilities.getTypeface(str2));
        this.doneButton.addView(this.doneButtonTextView, LayoutHelper.createLinear(-2, -2, 16));
    }

    public void updateSelectedCount(int i, boolean z) {
        int i2 = -1;
        Object obj = null;
        String str = "picker_enabledButton";
        TextView textView;
        if (i == 0) {
            this.doneButtonBadgeTextView.setVisibility(8);
            if (z) {
                textView = this.doneButtonTextView;
                String str2 = "picker_disabledButton";
                if (!this.isDarkTheme) {
                    obj = str2;
                }
                textView.setTag(obj);
                this.doneButtonTextView.setTextColor(this.isDarkTheme ? -6710887 : Theme.getColor(str2));
                this.doneButton.setEnabled(false);
                return;
            }
            textView = this.doneButtonTextView;
            if (!this.isDarkTheme) {
                obj = str;
            }
            textView.setTag(obj);
            textView = this.doneButtonTextView;
            if (!this.isDarkTheme) {
                i2 = Theme.getColor(str);
            }
            textView.setTextColor(i2);
            return;
        }
        this.doneButtonBadgeTextView.setVisibility(0);
        this.doneButtonBadgeTextView.setText(String.format("%d", new Object[]{Integer.valueOf(i)}));
        textView = this.doneButtonTextView;
        if (!this.isDarkTheme) {
            obj = str;
        }
        textView.setTag(obj);
        textView = this.doneButtonTextView;
        if (!this.isDarkTheme) {
            i2 = Theme.getColor(str);
        }
        textView.setTextColor(i2);
        if (z) {
            this.doneButton.setEnabled(true);
        }
    }
}
