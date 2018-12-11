package org.telegram.p005ui.Components;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.CLASSNAMER;
import org.telegram.messenger.LocaleController;
import org.telegram.p005ui.ActionBar.Theme;

/* renamed from: org.telegram.ui.Components.PickerBottomLayout */
public class PickerBottomLayout extends FrameLayout {
    public TextView cancelButton;
    public LinearLayout doneButton;
    public TextView doneButtonBadgeTextView;
    public TextView doneButtonTextView;
    private boolean isDarkTheme;

    public PickerBottomLayout(Context context) {
        this(context, true);
    }

    public PickerBottomLayout(Context context, boolean darkTheme) {
        Drawable drawable;
        int i = -1;
        super(context);
        this.isDarkTheme = darkTheme;
        setBackgroundColor(this.isDarkTheme ? -15066598 : Theme.getColor(Theme.key_windowBackgroundWhite));
        this.cancelButton = new TextView(context);
        this.cancelButton.setTextSize(1, 14.0f);
        this.cancelButton.setTextColor(this.isDarkTheme ? -1 : Theme.getColor(Theme.key_picker_enabledButton));
        this.cancelButton.setGravity(17);
        this.cancelButton.setBackgroundDrawable(Theme.createSelectorDrawable(this.isDarkTheme ? Theme.ACTION_BAR_PICKER_SELECTOR_COLOR : Theme.ACTION_BAR_AUDIO_SELECTOR_COLOR, 0));
        this.cancelButton.setPadding(AndroidUtilities.m10dp(29.0f), 0, AndroidUtilities.m10dp(29.0f), 0);
        this.cancelButton.setText(LocaleController.getString("Cancel", CLASSNAMER.string.Cancel).toUpperCase());
        this.cancelButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        addView(this.cancelButton, LayoutHelper.createFrame(-2, -1, 51));
        this.doneButton = new LinearLayout(context);
        this.doneButton.setOrientation(0);
        this.doneButton.setBackgroundDrawable(Theme.createSelectorDrawable(this.isDarkTheme ? Theme.ACTION_BAR_PICKER_SELECTOR_COLOR : Theme.ACTION_BAR_AUDIO_SELECTOR_COLOR, 0));
        this.doneButton.setPadding(AndroidUtilities.m10dp(29.0f), 0, AndroidUtilities.m10dp(29.0f), 0);
        addView(this.doneButton, LayoutHelper.createFrame(-2, -1, 53));
        this.doneButtonBadgeTextView = new TextView(context);
        this.doneButtonBadgeTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.doneButtonBadgeTextView.setTextSize(1, 13.0f);
        this.doneButtonBadgeTextView.setTextColor(this.isDarkTheme ? -1 : Theme.getColor(Theme.key_picker_badgeText));
        this.doneButtonBadgeTextView.setGravity(17);
        if (this.isDarkTheme) {
            drawable = Theme.createRoundRectDrawable(AndroidUtilities.m10dp(11.0f), -10043398);
        } else {
            drawable = Theme.createRoundRectDrawable(AndroidUtilities.m10dp(11.0f), Theme.getColor(Theme.key_picker_badge));
        }
        this.doneButtonBadgeTextView.setBackgroundDrawable(drawable);
        this.doneButtonBadgeTextView.setMinWidth(AndroidUtilities.m10dp(23.0f));
        this.doneButtonBadgeTextView.setPadding(AndroidUtilities.m10dp(8.0f), 0, AndroidUtilities.m10dp(8.0f), AndroidUtilities.m10dp(1.0f));
        this.doneButton.addView(this.doneButtonBadgeTextView, LayoutHelper.createLinear(-2, 23, 16, 0, 0, 10, 0));
        this.doneButtonTextView = new TextView(context);
        this.doneButtonTextView.setTextSize(1, 14.0f);
        TextView textView = this.doneButtonTextView;
        if (!this.isDarkTheme) {
            i = Theme.getColor(Theme.key_picker_enabledButton);
        }
        textView.setTextColor(i);
        this.doneButtonTextView.setGravity(17);
        this.doneButtonTextView.setCompoundDrawablePadding(AndroidUtilities.m10dp(8.0f));
        this.doneButtonTextView.setText(LocaleController.getString("Send", CLASSNAMER.string.Send).toUpperCase());
        this.doneButtonTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.doneButton.addView(this.doneButtonTextView, LayoutHelper.createLinear(-2, -2, 16));
    }

    public void updateSelectedCount(int count, boolean disable) {
        int i = -1;
        Object obj = null;
        TextView textView;
        if (count == 0) {
            this.doneButtonBadgeTextView.setVisibility(8);
            if (disable) {
                TextView textView2 = this.doneButtonTextView;
                if (!this.isDarkTheme) {
                    obj = Theme.key_picker_disabledButton;
                }
                textView2.setTag(obj);
                this.doneButtonTextView.setTextColor(this.isDarkTheme ? -6710887 : Theme.getColor(Theme.key_picker_disabledButton));
                this.doneButton.setEnabled(false);
                return;
            }
            textView = this.doneButtonTextView;
            if (!this.isDarkTheme) {
                obj = Theme.key_picker_enabledButton;
            }
            textView.setTag(obj);
            this.doneButtonTextView.setTextColor(this.isDarkTheme ? -1 : Theme.getColor(Theme.key_picker_enabledButton));
            return;
        }
        this.doneButtonBadgeTextView.setVisibility(0);
        this.doneButtonBadgeTextView.setText(String.format("%d", new Object[]{Integer.valueOf(count)}));
        textView = this.doneButtonTextView;
        if (!this.isDarkTheme) {
            obj = Theme.key_picker_enabledButton;
        }
        textView.setTag(obj);
        TextView textView3 = this.doneButtonTextView;
        if (!this.isDarkTheme) {
            i = Theme.getColor(Theme.key_picker_enabledButton);
        }
        textView3.setTextColor(i);
        if (disable) {
            this.doneButton.setEnabled(true);
        }
    }
}
