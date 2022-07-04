package org.telegram.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.browser.Browser;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;

public class SponsoredMessageInfoView extends FrameLayout {
    LinearLayout linearLayout;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public SponsoredMessageInfoView(Activity context, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        final Activity activity = context;
        Theme.ResourcesProvider resourcesProvider2 = resourcesProvider;
        LinearLayout linearLayout2 = new LinearLayout(activity);
        linearLayout2.setOrientation(1);
        TextView textView = new TextView(activity);
        textView.setText(LocaleController.getString("SponsoredMessageInfo", NUM));
        textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText", resourcesProvider2));
        textView.setTextSize(1, 20.0f);
        TextView description1 = new TextView(activity);
        description1.setText(LocaleController.getString("SponsoredMessageInfoDescription1", NUM));
        description1.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText", resourcesProvider2));
        description1.setTextSize(1, 14.0f);
        description1.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
        TextView description2 = new TextView(activity);
        description2.setText(LocaleController.getString("SponsoredMessageInfoDescription2", NUM));
        description2.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText", resourcesProvider2));
        description2.setTextSize(1, 14.0f);
        description2.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
        TextView description3 = new TextView(activity);
        description3.setText(LocaleController.getString("SponsoredMessageInfoDescription3", NUM));
        description3.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText", resourcesProvider2));
        description3.setTextSize(1, 14.0f);
        description3.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
        final Paint buttonPaint = new Paint(1);
        buttonPaint.setStyle(Paint.Style.STROKE);
        buttonPaint.setColor(Theme.getColor("featuredStickers_addButton", resourcesProvider2));
        buttonPaint.setStrokeWidth((float) AndroidUtilities.dp(1.0f));
        TextView button = new TextView(activity) {
            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                AndroidUtilities.rectTmp.set((float) AndroidUtilities.dp(1.0f), (float) AndroidUtilities.dp(1.0f), (float) (getMeasuredWidth() - AndroidUtilities.dp(1.0f)), (float) (getMeasuredHeight() - AndroidUtilities.dp(1.0f)));
                canvas.drawRoundRect(AndroidUtilities.rectTmp, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), buttonPaint);
            }
        };
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Browser.openUrl((Context) activity, LocaleController.getString("SponsoredMessageAlertLearnMoreUrl", NUM));
            }
        });
        button.setPadding(AndroidUtilities.dp(12.0f), 0, AndroidUtilities.dp(12.0f), 0);
        button.setText(LocaleController.getString("SponsoredMessageAlertLearnMoreUrl", NUM));
        button.setTextColor(Theme.getColor("featuredStickers_addButton", resourcesProvider2));
        button.setBackground(Theme.AdaptiveRipple.filledRect(Theme.getColor("dialogBackground", resourcesProvider2), 4.0f));
        button.setTextSize(1, 14.0f);
        button.setGravity(16);
        TextView description4 = new TextView(activity);
        description4.setText(LocaleController.getString("SponsoredMessageInfoDescription4", NUM));
        description4.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
        description4.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText", resourcesProvider2));
        description4.setTextSize(1, 14.0f);
        linearLayout2.addView(textView);
        linearLayout2.addView(description1, LayoutHelper.createLinear(-1, -2, 0, 0, 18, 0, 0));
        linearLayout2.addView(description2, LayoutHelper.createLinear(-1, -2, 0, 0, 24, 0, 0));
        linearLayout2.addView(description3, LayoutHelper.createLinear(-1, -2, 0, 0, 24, 0, 0));
        linearLayout2.addView(button, LayoutHelper.createLinear(-2, 34, 1, 0, 14, 0, 0));
        linearLayout2.addView(description4, LayoutHelper.createLinear(-1, -2, 0, 0, 14, 0, 0));
        ScrollView scrollView = new ScrollView(getContext());
        scrollView.addView(linearLayout2);
        addView(scrollView, LayoutHelper.createFrame(-1, -2.0f, 0, 22.0f, 12.0f, 22.0f, 22.0f));
    }
}
