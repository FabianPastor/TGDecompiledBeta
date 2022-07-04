package org.telegram.ui;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.ColoredImageSpan;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Premium.PremiumButtonView;

public class UnlockPremiumView extends FrameLayout {
    public static final int TYPE_REACTIONS = 1;
    public static final int TYPE_STICKERS = 0;
    public final PremiumButtonView premiumButtonView;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public UnlockPremiumView(Context context, int type, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        String text;
        Context context2 = context;
        int i = type;
        LinearLayout linearLayout = new LinearLayout(context2);
        addView(linearLayout, LayoutHelper.createFrame(-1, -2, 80));
        linearLayout.setOrientation(1);
        TextView descriptionTextView = new TextView(context2);
        descriptionTextView.setTextColor(ColorUtils.setAlphaComponent(Theme.getColor("windowBackgroundWhiteBlackText", resourcesProvider), 100));
        descriptionTextView.setTextSize(1, 13.0f);
        descriptionTextView.setGravity(17);
        if (i == 0) {
            descriptionTextView.setText(LocaleController.getString("UnlockPremiumStickersDescription", NUM));
        } else if (i == 1) {
            descriptionTextView.setText(LocaleController.getString("UnlockPremiumReactionsDescription", NUM));
        }
        linearLayout.addView(descriptionTextView, LayoutHelper.createLinear(-1, -2, 0, 16, 17, 17, 16));
        PremiumButtonView premiumButtonView2 = new PremiumButtonView(context2, false);
        this.premiumButtonView = premiumButtonView2;
        if (i == 0) {
            text = LocaleController.getString("UnlockPremiumStickers", NUM);
        } else {
            text = LocaleController.getString("UnlockPremiumReactions", NUM);
        }
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        spannableStringBuilder.append("d ").setSpan(new ColoredImageSpan(ContextCompat.getDrawable(context2, NUM)), 0, 1, 0);
        spannableStringBuilder.append(text);
        premiumButtonView2.buttonTextView.setText(spannableStringBuilder);
        linearLayout.addView(premiumButtonView2, LayoutHelper.createLinear(-1, 48, 0, 16, 0, 16, 16));
    }
}
