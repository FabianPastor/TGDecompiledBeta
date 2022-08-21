package org.telegram.ui;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.ColoredImageSpan;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Premium.PremiumButtonView;

public class UnlockPremiumView extends FrameLayout {
    public final PremiumButtonView premiumButtonView;

    public UnlockPremiumView(Context context, int i, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        String str;
        LinearLayout linearLayout = new LinearLayout(context);
        addView(linearLayout, LayoutHelper.createFrame(-1, -2, 80));
        linearLayout.setOrientation(1);
        TextView textView = new TextView(context);
        textView.setTextColor(ColorUtils.setAlphaComponent(Theme.getColor("windowBackgroundWhiteBlackText", resourcesProvider), 100));
        textView.setTextSize(1, 13.0f);
        textView.setGravity(17);
        if (i == 0) {
            textView.setText(LocaleController.getString("UnlockPremiumStickersDescription", R.string.UnlockPremiumStickersDescription));
        } else if (i == 1) {
            textView.setText(LocaleController.getString("UnlockPremiumReactionsDescription", R.string.UnlockPremiumReactionsDescription));
        }
        linearLayout.addView(textView, LayoutHelper.createLinear(-1, -2, 0, 16, 17, 17, 16));
        PremiumButtonView premiumButtonView2 = new PremiumButtonView(context, false);
        this.premiumButtonView = premiumButtonView2;
        if (i == 0) {
            str = LocaleController.getString("UnlockPremiumStickers", R.string.UnlockPremiumStickers);
        } else {
            str = LocaleController.getString("UnlockPremiumReactions", R.string.UnlockPremiumReactions);
        }
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        spannableStringBuilder.append("d ").setSpan(new ColoredImageSpan(ContextCompat.getDrawable(context, R.drawable.msg_premium_normal)), 0, 1, 0);
        spannableStringBuilder.append(str);
        premiumButtonView2.buttonTextView.setText(spannableStringBuilder);
        linearLayout.addView(premiumButtonView2, LayoutHelper.createLinear(-1, 48, 0, 16, 0, 16, 16));
    }
}
