package org.telegram.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.messenger.browser.Browser;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;

public class SponsoredMessageInfoView extends FrameLayout {
    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public SponsoredMessageInfoView(Activity activity, Theme.ResourcesProvider resourcesProvider) {
        super(activity);
        final Activity activity2 = activity;
        Theme.ResourcesProvider resourcesProvider2 = resourcesProvider;
        LinearLayout linearLayout = new LinearLayout(activity2);
        linearLayout.setOrientation(1);
        TextView textView = new TextView(activity2);
        textView.setText(LocaleController.getString("SponsoredMessageInfo", R.string.SponsoredMessageInfo));
        textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText", resourcesProvider2));
        textView.setTextSize(1, 20.0f);
        TextView textView2 = new TextView(activity2);
        textView2.setText(LocaleController.getString("SponsoredMessageInfoDescription1", R.string.SponsoredMessageInfoDescription1));
        textView2.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText", resourcesProvider2));
        textView2.setTextSize(1, 14.0f);
        textView2.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
        TextView textView3 = new TextView(activity2);
        textView3.setText(LocaleController.getString("SponsoredMessageInfoDescription2", R.string.SponsoredMessageInfoDescription2));
        textView3.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText", resourcesProvider2));
        textView3.setTextSize(1, 14.0f);
        textView3.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
        TextView textView4 = new TextView(activity2);
        textView4.setText(LocaleController.getString("SponsoredMessageInfoDescription3", R.string.SponsoredMessageInfoDescription3));
        textView4.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText", resourcesProvider2));
        textView4.setTextSize(1, 14.0f);
        textView4.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
        final Paint paint = new Paint(1);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Theme.getColor("featuredStickers_addButton", resourcesProvider2));
        paint.setStrokeWidth((float) AndroidUtilities.dp(1.0f));
        AnonymousClass1 r15 = new TextView(this, activity2) {
            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                RectF rectF = AndroidUtilities.rectTmp;
                rectF.set((float) AndroidUtilities.dp(1.0f), (float) AndroidUtilities.dp(1.0f), (float) (getMeasuredWidth() - AndroidUtilities.dp(1.0f)), (float) (getMeasuredHeight() - AndroidUtilities.dp(1.0f)));
                canvas.drawRoundRect(rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint);
            }
        };
        r15.setOnClickListener(new View.OnClickListener(this) {
            public void onClick(View view) {
                Browser.openUrl((Context) activity2, LocaleController.getString("SponsoredMessageAlertLearnMoreUrl", R.string.SponsoredMessageAlertLearnMoreUrl));
            }
        });
        r15.setPadding(AndroidUtilities.dp(12.0f), 0, AndroidUtilities.dp(12.0f), 0);
        r15.setText(LocaleController.getString("SponsoredMessageAlertLearnMoreUrl", R.string.SponsoredMessageAlertLearnMoreUrl));
        r15.setTextColor(Theme.getColor("featuredStickers_addButton", resourcesProvider2));
        r15.setBackground(Theme.AdaptiveRipple.filledRect(Theme.getColor("dialogBackground", resourcesProvider2), 4.0f));
        r15.setTextSize(1, 14.0f);
        r15.setGravity(16);
        TextView textView5 = new TextView(activity2);
        textView5.setText(LocaleController.getString("SponsoredMessageInfoDescription4", R.string.SponsoredMessageInfoDescription4));
        textView5.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
        textView5.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText", resourcesProvider2));
        textView5.setTextSize(1, 14.0f);
        linearLayout.addView(textView);
        linearLayout.addView(textView2, LayoutHelper.createLinear(-1, -2, 0, 0, 18, 0, 0));
        linearLayout.addView(textView3, LayoutHelper.createLinear(-1, -2, 0, 0, 24, 0, 0));
        linearLayout.addView(textView4, LayoutHelper.createLinear(-1, -2, 0, 0, 24, 0, 0));
        linearLayout.addView(r15, LayoutHelper.createLinear(-2, 34, 1, 0, 14, 0, 0));
        linearLayout.addView(textView5, LayoutHelper.createLinear(-1, -2, 0, 0, 14, 0, 0));
        ScrollView scrollView = new ScrollView(getContext());
        scrollView.addView(linearLayout);
        addView(scrollView, LayoutHelper.createFrame(-1, -2.0f, 0, 22.0f, 12.0f, 22.0f, 22.0f));
    }
}
