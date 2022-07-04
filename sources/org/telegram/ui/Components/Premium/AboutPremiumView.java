package org.telegram.ui.Components.Premium;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;

public class AboutPremiumView extends LinearLayout {
    public AboutPremiumView(Context context) {
        super(context);
        setOrientation(1);
        setPadding(AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f));
        TextView textView = new TextView(context);
        textView.setTextSize(1, 14.0f);
        textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        textView.setText(LocaleController.getString("AboutPremiumTitle", NUM));
        addView(textView);
        TextView description = new TextView(context);
        description.setTextSize(1, 14.0f);
        description.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        description.setText(AndroidUtilities.replaceTags(LocaleController.getString("AboutPremiumDescription", NUM)));
        addView(description, LayoutHelper.createLinear(-1, -2, 0.0f, 0, 0, 0, 0, 0));
        TextView description2 = new TextView(context);
        description2.setTextSize(1, 14.0f);
        description2.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        description2.setText(AndroidUtilities.replaceTags(LocaleController.getString("AboutPremiumDescription2", NUM)));
        addView(description2, LayoutHelper.createLinear(-1, -2, 0.0f, 0, 0, 24, 0, 0));
    }
}
