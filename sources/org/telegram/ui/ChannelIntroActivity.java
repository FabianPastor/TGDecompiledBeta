package org.telegram.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;

public class ChannelIntroActivity extends BaseFragment {
    private TextView createChannelText;
    private TextView descriptionText;
    private ImageView imageView;
    private TextView whatIsChannelText;

    public View createView(Context context) {
        String str = "windowBackgroundWhite";
        this.actionBar.setBackgroundColor(Theme.getColor(str));
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setItemsColor(Theme.getColor("windowBackgroundWhiteGrayText2"), false);
        this.actionBar.setItemsBackgroundColor(Theme.getColor("actionBarWhiteSelector"), false);
        this.actionBar.setCastShadows(false);
        if (!AndroidUtilities.isTablet()) {
            this.actionBar.showActionModeTop();
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    ChannelIntroActivity.this.finishFragment();
                }
            }
        });
        this.fragmentView = new ViewGroup(context) {
            /* Access modifiers changed, original: protected */
            public void onMeasure(int i, int i2) {
                i = MeasureSpec.getSize(i);
                i2 = MeasureSpec.getSize(i2);
                if (i > i2) {
                    float f = (float) i;
                    ChannelIntroActivity.this.imageView.measure(MeasureSpec.makeMeasureSpec((int) (0.45f * f), NUM), MeasureSpec.makeMeasureSpec((int) (((float) i2) * 0.78f), NUM));
                    int i3 = (int) (0.6f * f);
                    ChannelIntroActivity.this.whatIsChannelText.measure(MeasureSpec.makeMeasureSpec(i3, NUM), MeasureSpec.makeMeasureSpec(i2, 0));
                    ChannelIntroActivity.this.descriptionText.measure(MeasureSpec.makeMeasureSpec((int) (f * 0.5f), NUM), MeasureSpec.makeMeasureSpec(i2, 0));
                    ChannelIntroActivity.this.createChannelText.measure(MeasureSpec.makeMeasureSpec(i3, NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(24.0f), NUM));
                } else {
                    ChannelIntroActivity.this.imageView.measure(MeasureSpec.makeMeasureSpec(i, NUM), MeasureSpec.makeMeasureSpec((int) (((float) i2) * 0.44f), NUM));
                    ChannelIntroActivity.this.whatIsChannelText.measure(MeasureSpec.makeMeasureSpec(i, NUM), MeasureSpec.makeMeasureSpec(i2, 0));
                    ChannelIntroActivity.this.descriptionText.measure(MeasureSpec.makeMeasureSpec((int) (((float) i) * 0.9f), NUM), MeasureSpec.makeMeasureSpec(i2, 0));
                    ChannelIntroActivity.this.createChannelText.measure(MeasureSpec.makeMeasureSpec(i, NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(24.0f), NUM));
                }
                setMeasuredDimension(i, i2);
            }

            /* Access modifiers changed, original: protected */
            public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                int i5 = i3 - i;
                i = i4 - i2;
                float f;
                if (i3 > i4) {
                    f = (float) i;
                    i2 = (int) (0.05f * f);
                    ChannelIntroActivity.this.imageView.layout(0, i2, ChannelIntroActivity.this.imageView.getMeasuredWidth(), ChannelIntroActivity.this.imageView.getMeasuredHeight() + i2);
                    float f2 = (float) i5;
                    i2 = (int) (0.4f * f2);
                    i3 = (int) (0.14f * f);
                    ChannelIntroActivity.this.whatIsChannelText.layout(i2, i3, ChannelIntroActivity.this.whatIsChannelText.getMeasuredWidth() + i2, ChannelIntroActivity.this.whatIsChannelText.getMeasuredHeight() + i3);
                    i3 = (int) (0.61f * f);
                    ChannelIntroActivity.this.createChannelText.layout(i2, i3, ChannelIntroActivity.this.createChannelText.getMeasuredWidth() + i2, ChannelIntroActivity.this.createChannelText.getMeasuredHeight() + i3);
                    i5 = (int) (f2 * 0.45f);
                    i = (int) (f * 0.31f);
                    ChannelIntroActivity.this.descriptionText.layout(i5, i, ChannelIntroActivity.this.descriptionText.getMeasuredWidth() + i5, ChannelIntroActivity.this.descriptionText.getMeasuredHeight() + i);
                    return;
                }
                f = (float) i;
                i3 = (int) (f * 0.05f);
                ChannelIntroActivity.this.imageView.layout(0, i3, ChannelIntroActivity.this.imageView.getMeasuredWidth(), ChannelIntroActivity.this.imageView.getMeasuredHeight() + i3);
                i3 = (int) (0.59f * f);
                ChannelIntroActivity.this.whatIsChannelText.layout(0, i3, ChannelIntroActivity.this.whatIsChannelText.getMeasuredWidth(), ChannelIntroActivity.this.whatIsChannelText.getMeasuredHeight() + i3);
                i3 = (int) (0.68f * f);
                i5 = (int) (((float) i5) * 0.05f);
                ChannelIntroActivity.this.descriptionText.layout(i5, i3, ChannelIntroActivity.this.descriptionText.getMeasuredWidth() + i5, ChannelIntroActivity.this.descriptionText.getMeasuredHeight() + i3);
                i5 = (int) (f * 0.86f);
                ChannelIntroActivity.this.createChannelText.layout(0, i5, ChannelIntroActivity.this.createChannelText.getMeasuredWidth(), ChannelIntroActivity.this.createChannelText.getMeasuredHeight() + i5);
            }
        };
        this.fragmentView.setBackgroundColor(Theme.getColor(str));
        ViewGroup viewGroup = (ViewGroup) this.fragmentView;
        viewGroup.setOnTouchListener(-$$Lambda$ChannelIntroActivity$0c8d8mysDNu6O6Ps8WT2KcKJBXc.INSTANCE);
        this.imageView = new ImageView(context);
        this.imageView.setImageResource(NUM);
        this.imageView.setScaleType(ScaleType.FIT_CENTER);
        viewGroup.addView(this.imageView);
        this.whatIsChannelText = new TextView(context);
        this.whatIsChannelText.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.whatIsChannelText.setGravity(1);
        this.whatIsChannelText.setTextSize(1, 24.0f);
        this.whatIsChannelText.setText(LocaleController.getString("ChannelAlertTitle", NUM));
        viewGroup.addView(this.whatIsChannelText);
        this.descriptionText = new TextView(context);
        this.descriptionText.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText6"));
        this.descriptionText.setGravity(1);
        this.descriptionText.setTextSize(1, 16.0f);
        this.descriptionText.setText(LocaleController.getString("ChannelAlertText", NUM));
        viewGroup.addView(this.descriptionText);
        this.createChannelText = new TextView(context);
        this.createChannelText.setTextColor(Theme.getColor("windowBackgroundWhiteBlueText5"));
        this.createChannelText.setGravity(17);
        this.createChannelText.setTextSize(1, 16.0f);
        this.createChannelText.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.createChannelText.setText(LocaleController.getString("ChannelAlertCreate", NUM));
        viewGroup.addView(this.createChannelText);
        this.createChannelText.setOnClickListener(new -$$Lambda$ChannelIntroActivity$M58NjDpXQDsy4vkbKcDWC5YHj9o(this));
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$1$ChannelIntroActivity(View view) {
        Bundle bundle = new Bundle();
        bundle.putInt("step", 0);
        presentFragment(new ChannelCreateActivity(bundle), true);
    }

    public ThemeDescription[] getThemeDescriptions() {
        return new ThemeDescription[]{new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText2"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarWhiteSelector"), new ThemeDescription(this.whatIsChannelText, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.descriptionText, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText6"), new ThemeDescription(this.createChannelText, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "windowBackgroundWhiteBlueText5")};
    }
}
