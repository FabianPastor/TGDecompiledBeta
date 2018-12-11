package org.telegram.p005ui;

import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.CLASSNAMER;
import org.telegram.messenger.LocaleController;
import org.telegram.p005ui.ActionBar.BaseFragment;
import org.telegram.p005ui.ActionBar.CLASSNAMEActionBar.ActionBarMenuOnItemClick;
import org.telegram.p005ui.ActionBar.Theme;
import org.telegram.p005ui.ActionBar.ThemeDescription;

/* renamed from: org.telegram.ui.ChannelIntroActivity */
public class ChannelIntroActivity extends BaseFragment {
    private TextView createChannelText;
    private TextView descriptionText;
    private ImageView imageView;
    private TextView whatIsChannelText;

    /* renamed from: org.telegram.ui.ChannelIntroActivity$1 */
    class CLASSNAME extends ActionBarMenuOnItemClick {
        CLASSNAME() {
        }

        public void onItemClick(int id) {
            if (id == -1) {
                ChannelIntroActivity.this.finishFragment();
            }
        }
    }

    /* renamed from: org.telegram.ui.ChannelIntroActivity$3 */
    class CLASSNAME implements OnTouchListener {
        CLASSNAME() {
        }

        public boolean onTouch(View v, MotionEvent event) {
            return true;
        }
    }

    /* renamed from: org.telegram.ui.ChannelIntroActivity$4 */
    class CLASSNAME implements OnClickListener {
        CLASSNAME() {
        }

        public void onClick(View v) {
            Bundle args = new Bundle();
            args.putInt("step", 0);
            ChannelIntroActivity.this.presentFragment(new ChannelCreateActivity(args), true);
        }
    }

    public View createView(Context context) {
        this.actionBar.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        this.actionBar.setBackButtonImage(CLASSNAMER.drawable.ic_ab_back);
        this.actionBar.setItemsColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2), false);
        this.actionBar.setItemsBackgroundColor(Theme.getColor(Theme.key_actionBarWhiteSelector), false);
        this.actionBar.setCastShadows(false);
        if (!AndroidUtilities.isTablet()) {
            this.actionBar.showActionModeTop();
        }
        this.actionBar.setActionBarMenuOnItemClick(new CLASSNAME());
        this.fragmentView = new ViewGroup(context) {
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int width = MeasureSpec.getSize(widthMeasureSpec);
                int height = MeasureSpec.getSize(heightMeasureSpec);
                if (width > height) {
                    ChannelIntroActivity.this.imageView.measure(MeasureSpec.makeMeasureSpec((int) (((float) width) * 0.45f), NUM), MeasureSpec.makeMeasureSpec((int) (((float) height) * 0.78f), NUM));
                    ChannelIntroActivity.this.whatIsChannelText.measure(MeasureSpec.makeMeasureSpec((int) (((float) width) * 0.6f), NUM), MeasureSpec.makeMeasureSpec(height, 0));
                    ChannelIntroActivity.this.descriptionText.measure(MeasureSpec.makeMeasureSpec((int) (((float) width) * 0.5f), NUM), MeasureSpec.makeMeasureSpec(height, 0));
                    ChannelIntroActivity.this.createChannelText.measure(MeasureSpec.makeMeasureSpec((int) (((float) width) * 0.6f), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.m10dp(24.0f), NUM));
                } else {
                    ChannelIntroActivity.this.imageView.measure(MeasureSpec.makeMeasureSpec(width, NUM), MeasureSpec.makeMeasureSpec((int) (((float) height) * 0.44f), NUM));
                    ChannelIntroActivity.this.whatIsChannelText.measure(MeasureSpec.makeMeasureSpec(width, NUM), MeasureSpec.makeMeasureSpec(height, 0));
                    ChannelIntroActivity.this.descriptionText.measure(MeasureSpec.makeMeasureSpec((int) (((float) width) * 0.9f), NUM), MeasureSpec.makeMeasureSpec(height, 0));
                    ChannelIntroActivity.this.createChannelText.measure(MeasureSpec.makeMeasureSpec(width, NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.m10dp(24.0f), NUM));
                }
                setMeasuredDimension(width, height);
            }

            protected void onLayout(boolean changed, int l, int t, int r, int b) {
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
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        ViewGroup viewGroup = this.fragmentView;
        viewGroup.setOnTouchListener(new CLASSNAME());
        this.imageView = new ImageView(context);
        this.imageView.setImageResource(CLASSNAMER.drawable.channelintro);
        this.imageView.setScaleType(ScaleType.FIT_CENTER);
        viewGroup.addView(this.imageView);
        this.whatIsChannelText = new TextView(context);
        this.whatIsChannelText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.whatIsChannelText.setGravity(1);
        this.whatIsChannelText.setTextSize(1, 24.0f);
        this.whatIsChannelText.setText(LocaleController.getString("ChannelAlertTitle", CLASSNAMER.string.ChannelAlertTitle));
        viewGroup.addView(this.whatIsChannelText);
        this.descriptionText = new TextView(context);
        this.descriptionText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText6));
        this.descriptionText.setGravity(1);
        this.descriptionText.setTextSize(1, 16.0f);
        this.descriptionText.setText(LocaleController.getString("ChannelAlertText", CLASSNAMER.string.ChannelAlertText));
        viewGroup.addView(this.descriptionText);
        this.createChannelText = new TextView(context);
        this.createChannelText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText5));
        this.createChannelText.setGravity(17);
        this.createChannelText.setTextSize(1, 16.0f);
        this.createChannelText.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.createChannelText.setText(LocaleController.getString("ChannelAlertCreate", CLASSNAMER.string.ChannelAlertCreate));
        viewGroup.addView(this.createChannelText);
        this.createChannelText.setOnClickListener(new CLASSNAME());
        return this.fragmentView;
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[7];
        themeDescriptionArr[0] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite);
        themeDescriptionArr[1] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite);
        themeDescriptionArr[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText2);
        themeDescriptionArr[3] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarWhiteSelector);
        themeDescriptionArr[4] = new ThemeDescription(this.whatIsChannelText, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[5] = new ThemeDescription(this.descriptionText, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText6);
        themeDescriptionArr[6] = new ThemeDescription(this.createChannelText, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlueText5);
        return themeDescriptionArr;
    }
}
