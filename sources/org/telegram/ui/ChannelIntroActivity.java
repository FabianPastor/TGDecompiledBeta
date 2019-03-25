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
        this.actionBar.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setItemsColor(Theme.getColor("windowBackgroundWhiteGrayText2"), false);
        this.actionBar.setItemsBackgroundColor(Theme.getColor("actionBarWhiteSelector"), false);
        this.actionBar.setCastShadows(false);
        if (!AndroidUtilities.isTablet()) {
            this.actionBar.showActionModeTop();
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    ChannelIntroActivity.this.finishFragment();
                }
            }
        });
        this.fragmentView = new ViewGroup(context) {
            /* Access modifiers changed, original: protected */
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int width = MeasureSpec.getSize(widthMeasureSpec);
                int height = MeasureSpec.getSize(heightMeasureSpec);
                if (width > height) {
                    ChannelIntroActivity.this.imageView.measure(MeasureSpec.makeMeasureSpec((int) (((float) width) * 0.45f), NUM), MeasureSpec.makeMeasureSpec((int) (((float) height) * 0.78f), NUM));
                    ChannelIntroActivity.this.whatIsChannelText.measure(MeasureSpec.makeMeasureSpec((int) (((float) width) * 0.6f), NUM), MeasureSpec.makeMeasureSpec(height, 0));
                    ChannelIntroActivity.this.descriptionText.measure(MeasureSpec.makeMeasureSpec((int) (((float) width) * 0.5f), NUM), MeasureSpec.makeMeasureSpec(height, 0));
                    ChannelIntroActivity.this.createChannelText.measure(MeasureSpec.makeMeasureSpec((int) (((float) width) * 0.6f), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(24.0f), NUM));
                } else {
                    ChannelIntroActivity.this.imageView.measure(MeasureSpec.makeMeasureSpec(width, NUM), MeasureSpec.makeMeasureSpec((int) (((float) height) * 0.44f), NUM));
                    ChannelIntroActivity.this.whatIsChannelText.measure(MeasureSpec.makeMeasureSpec(width, NUM), MeasureSpec.makeMeasureSpec(height, 0));
                    ChannelIntroActivity.this.descriptionText.measure(MeasureSpec.makeMeasureSpec((int) (((float) width) * 0.9f), NUM), MeasureSpec.makeMeasureSpec(height, 0));
                    ChannelIntroActivity.this.createChannelText.measure(MeasureSpec.makeMeasureSpec(width, NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(24.0f), NUM));
                }
                setMeasuredDimension(width, height);
            }

            /* Access modifiers changed, original: protected */
            public void onLayout(boolean changed, int l, int t, int r, int b) {
                int width = r - l;
                int height = b - t;
                int y;
                int x;
                if (r > b) {
                    y = (int) (((float) height) * 0.05f);
                    ChannelIntroActivity.this.imageView.layout(0, y, ChannelIntroActivity.this.imageView.getMeasuredWidth(), ChannelIntroActivity.this.imageView.getMeasuredHeight() + y);
                    x = (int) (((float) width) * 0.4f);
                    y = (int) (((float) height) * 0.14f);
                    ChannelIntroActivity.this.whatIsChannelText.layout(x, y, ChannelIntroActivity.this.whatIsChannelText.getMeasuredWidth() + x, ChannelIntroActivity.this.whatIsChannelText.getMeasuredHeight() + y);
                    y = (int) (((float) height) * 0.61f);
                    ChannelIntroActivity.this.createChannelText.layout(x, y, ChannelIntroActivity.this.createChannelText.getMeasuredWidth() + x, ChannelIntroActivity.this.createChannelText.getMeasuredHeight() + y);
                    x = (int) (((float) width) * 0.45f);
                    y = (int) (((float) height) * 0.31f);
                    ChannelIntroActivity.this.descriptionText.layout(x, y, ChannelIntroActivity.this.descriptionText.getMeasuredWidth() + x, ChannelIntroActivity.this.descriptionText.getMeasuredHeight() + y);
                    return;
                }
                y = (int) (((float) height) * 0.05f);
                ChannelIntroActivity.this.imageView.layout(0, y, ChannelIntroActivity.this.imageView.getMeasuredWidth(), ChannelIntroActivity.this.imageView.getMeasuredHeight() + y);
                y = (int) (((float) height) * 0.59f);
                ChannelIntroActivity.this.whatIsChannelText.layout(0, y, ChannelIntroActivity.this.whatIsChannelText.getMeasuredWidth(), ChannelIntroActivity.this.whatIsChannelText.getMeasuredHeight() + y);
                y = (int) (((float) height) * 0.68f);
                x = (int) (((float) width) * 0.05f);
                ChannelIntroActivity.this.descriptionText.layout(x, y, ChannelIntroActivity.this.descriptionText.getMeasuredWidth() + x, ChannelIntroActivity.this.descriptionText.getMeasuredHeight() + y);
                y = (int) (((float) height) * 0.86f);
                ChannelIntroActivity.this.createChannelText.layout(0, y, ChannelIntroActivity.this.createChannelText.getMeasuredWidth(), ChannelIntroActivity.this.createChannelText.getMeasuredHeight() + y);
            }
        };
        this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        ViewGroup viewGroup = this.fragmentView;
        viewGroup.setOnTouchListener(ChannelIntroActivity$$Lambda$0.$instance);
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
        this.createChannelText.setOnClickListener(new ChannelIntroActivity$$Lambda$1(this));
        return this.fragmentView;
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$createView$1$ChannelIntroActivity(View v) {
        Bundle args = new Bundle();
        args.putInt("step", 0);
        presentFragment(new ChannelCreateActivity(args), true);
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[7];
        themeDescriptionArr[0] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite");
        themeDescriptionArr[1] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite");
        themeDescriptionArr[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText2");
        themeDescriptionArr[3] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarWhiteSelector");
        themeDescriptionArr[4] = new ThemeDescription(this.whatIsChannelText, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText");
        themeDescriptionArr[5] = new ThemeDescription(this.descriptionText, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText6");
        themeDescriptionArr[6] = new ThemeDescription(this.createChannelText, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "windowBackgroundWhiteBlueText5");
        return themeDescriptionArr;
    }
}
