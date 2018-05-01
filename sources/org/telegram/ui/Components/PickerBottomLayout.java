package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0446R;
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
        setBackgroundColor(this.isDarkTheme ? -15066598 : Theme.getColor(Theme.key_windowBackgroundWhite));
        r0.cancelButton = new TextView(context2);
        r0.cancelButton.setTextSize(1, 14.0f);
        int i = -1;
        r0.cancelButton.setTextColor(r0.isDarkTheme ? -1 : Theme.getColor(Theme.key_picker_enabledButton));
        r0.cancelButton.setGravity(17);
        TextView textView = r0.cancelButton;
        boolean z2 = r0.isDarkTheme;
        int i2 = Theme.ACTION_BAR_AUDIO_SELECTOR_COLOR;
        textView.setBackgroundDrawable(Theme.createSelectorDrawable(z2 ? Theme.ACTION_BAR_PICKER_SELECTOR_COLOR : Theme.ACTION_BAR_AUDIO_SELECTOR_COLOR, 0));
        r0.cancelButton.setPadding(AndroidUtilities.dp(29.0f), 0, AndroidUtilities.dp(29.0f), 0);
        r0.cancelButton.setText(LocaleController.getString("Cancel", C0446R.string.Cancel).toUpperCase());
        r0.cancelButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        addView(r0.cancelButton, LayoutHelper.createFrame(-2, -1, 51));
        r0.doneButton = new LinearLayout(context2);
        r0.doneButton.setOrientation(0);
        LinearLayout linearLayout = r0.doneButton;
        if (r0.isDarkTheme) {
            i2 = Theme.ACTION_BAR_PICKER_SELECTOR_COLOR;
        }
        linearLayout.setBackgroundDrawable(Theme.createSelectorDrawable(i2, 0));
        r0.doneButton.setPadding(AndroidUtilities.dp(29.0f), 0, AndroidUtilities.dp(29.0f), 0);
        addView(r0.doneButton, LayoutHelper.createFrame(-2, -1, 53));
        r0.doneButtonBadgeTextView = new TextView(context2);
        r0.doneButtonBadgeTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        r0.doneButtonBadgeTextView.setTextSize(1, 13.0f);
        r0.doneButtonBadgeTextView.setTextColor(r0.isDarkTheme ? -1 : Theme.getColor(Theme.key_picker_badgeText));
        r0.doneButtonBadgeTextView.setGravity(17);
        if (r0.isDarkTheme) {
            createRoundRectDrawable = Theme.createRoundRectDrawable(AndroidUtilities.dp(11.0f), -10043398);
        } else {
            createRoundRectDrawable = Theme.createRoundRectDrawable(AndroidUtilities.dp(11.0f), Theme.getColor(Theme.key_picker_badge));
        }
        r0.doneButtonBadgeTextView.setBackgroundDrawable(createRoundRectDrawable);
        r0.doneButtonBadgeTextView.setMinWidth(AndroidUtilities.dp(23.0f));
        r0.doneButtonBadgeTextView.setPadding(AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f), AndroidUtilities.dp(1.0f));
        r0.doneButton.addView(r0.doneButtonBadgeTextView, LayoutHelper.createLinear(-2, 23, 16, 0, 0, 10, 0));
        r0.doneButtonTextView = new TextView(context2);
        r0.doneButtonTextView.setTextSize(1, 14.0f);
        TextView textView2 = r0.doneButtonTextView;
        if (!r0.isDarkTheme) {
            i = Theme.getColor(Theme.key_picker_enabledButton);
        }
        textView2.setTextColor(i);
        r0.doneButtonTextView.setGravity(17);
        r0.doneButtonTextView.setCompoundDrawablePadding(AndroidUtilities.dp(8.0f));
        r0.doneButtonTextView.setText(LocaleController.getString("Send", C0446R.string.Send).toUpperCase());
        r0.doneButtonTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        r0.doneButton.addView(r0.doneButtonTextView, LayoutHelper.createLinear(-2, -2, 16));
    }

    public void updateSelectedCount(int i, boolean z) {
        int i2 = -1;
        Object obj = null;
        if (i == 0) {
            this.doneButtonBadgeTextView.setVisibility(8);
            if (z) {
                i = this.doneButtonTextView;
                if (!this.isDarkTheme) {
                    obj = Theme.key_picker_disabledButton;
                }
                i.setTag(obj);
                this.doneButtonTextView.setTextColor(this.isDarkTheme ? true : Theme.getColor(Theme.key_picker_disabledButton));
                this.doneButton.setEnabled(false);
                return;
            }
            i = this.doneButtonTextView;
            if (!this.isDarkTheme) {
                obj = Theme.key_picker_enabledButton;
            }
            i.setTag(obj);
            i = this.doneButtonTextView;
            if (!this.isDarkTheme) {
                i2 = Theme.getColor(Theme.key_picker_enabledButton);
            }
            i.setTextColor(i2);
            return;
        }
        this.doneButtonBadgeTextView.setVisibility(0);
        this.doneButtonBadgeTextView.setText(String.format("%d", new Object[]{Integer.valueOf(i)}));
        i = this.doneButtonTextView;
        if (!this.isDarkTheme) {
            obj = Theme.key_picker_enabledButton;
        }
        i.setTag(obj);
        i = this.doneButtonTextView;
        if (!this.isDarkTheme) {
            i2 = Theme.getColor(Theme.key_picker_enabledButton);
        }
        i.setTextColor(i2);
        if (z) {
            this.doneButton.setEnabled(true);
        }
    }
}
