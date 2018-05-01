package org.telegram.ui.Components;

import android.content.Context;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0446R;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;

public class PickerBottomLayoutViewer extends FrameLayout {
    public TextView cancelButton;
    public TextView doneButton;
    public TextView doneButtonBadgeTextView;
    private boolean isDarkTheme;

    public PickerBottomLayoutViewer(Context context) {
        this(context, true);
    }

    public PickerBottomLayoutViewer(Context context, boolean z) {
        super(context);
        this.isDarkTheme = z;
        setBackgroundColor(this.isDarkTheme ? true : true);
        this.cancelButton = new TextView(context);
        this.cancelButton.setTextSize(1, 14.0f);
        int i = -15095832;
        this.cancelButton.setTextColor(this.isDarkTheme ? -1 : -15095832);
        this.cancelButton.setGravity(17);
        z = this.cancelButton;
        boolean z2 = this.isDarkTheme;
        int i2 = Theme.ACTION_BAR_AUDIO_SELECTOR_COLOR;
        z.setBackgroundDrawable(Theme.createSelectorDrawable(z2 ? Theme.ACTION_BAR_PICKER_SELECTOR_COLOR : Theme.ACTION_BAR_AUDIO_SELECTOR_COLOR, 0));
        this.cancelButton.setPadding(AndroidUtilities.dp(20.0f), 0, AndroidUtilities.dp(20.0f), 0);
        this.cancelButton.setText(LocaleController.getString("Cancel", C0446R.string.Cancel).toUpperCase());
        this.cancelButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        addView(this.cancelButton, LayoutHelper.createFrame(-2, -1, 51));
        this.doneButton = new TextView(context);
        this.doneButton.setTextSize(1, 14.0f);
        z = this.doneButton;
        if (this.isDarkTheme) {
            i = -1;
        }
        z.setTextColor(i);
        this.doneButton.setGravity(17);
        z = this.doneButton;
        if (this.isDarkTheme) {
            i2 = Theme.ACTION_BAR_PICKER_SELECTOR_COLOR;
        }
        z.setBackgroundDrawable(Theme.createSelectorDrawable(i2, 0));
        this.doneButton.setPadding(AndroidUtilities.dp(20.0f), 0, AndroidUtilities.dp(20.0f), 0);
        this.doneButton.setText(LocaleController.getString("Send", C0446R.string.Send).toUpperCase());
        this.doneButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        addView(this.doneButton, LayoutHelper.createFrame(-2, -1, 53));
        this.doneButtonBadgeTextView = new TextView(context);
        this.doneButtonBadgeTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.doneButtonBadgeTextView.setTextSize(1, true);
        this.doneButtonBadgeTextView.setTextColor(-1);
        this.doneButtonBadgeTextView.setGravity(17);
        this.doneButtonBadgeTextView.setBackgroundResource(this.isDarkTheme ? true : true);
        this.doneButtonBadgeTextView.setMinWidth(AndroidUtilities.dp(true));
        this.doneButtonBadgeTextView.setPadding(AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f), AndroidUtilities.dp(1.0f));
        addView(this.doneButtonBadgeTextView, LayoutHelper.createFrame(-2, 23.0f, 53, 0.0f, 0.0f, 7.0f, 0.0f));
    }

    public void updateSelectedCount(int i, boolean z) {
        int i2 = -15095832;
        if (i == 0) {
            this.doneButtonBadgeTextView.setVisibility(8);
            if (z) {
                this.doneButton.setTextColor(true);
                this.doneButton.setEnabled(false);
                return;
            }
            i = this.doneButton;
            if (this.isDarkTheme) {
                i2 = -1;
            }
            i.setTextColor(i2);
            return;
        }
        this.doneButtonBadgeTextView.setVisibility(0);
        this.doneButtonBadgeTextView.setText(String.format("%d", new Object[]{Integer.valueOf(i)}));
        i = this.doneButton;
        if (this.isDarkTheme) {
            i2 = -1;
        }
        i.setTextColor(i2);
        if (z) {
            this.doneButton.setEnabled(true);
        }
    }
}
