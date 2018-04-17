package org.telegram.ui;

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
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.beta.R;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;

public class ChannelIntroActivity extends BaseFragment {
    private TextView createChannelText;
    private TextView descriptionText;
    private ImageView imageView;
    private TextView whatIsChannelText;

    /* renamed from: org.telegram.ui.ChannelIntroActivity$3 */
    class C09923 implements OnTouchListener {
        C09923() {
        }

        public boolean onTouch(View v, MotionEvent event) {
            return true;
        }
    }

    /* renamed from: org.telegram.ui.ChannelIntroActivity$4 */
    class C09934 implements OnClickListener {
        C09934() {
        }

        public void onClick(View v) {
            Bundle args = new Bundle();
            args.putInt("step", 0);
            ChannelIntroActivity.this.presentFragment(new ChannelCreateActivity(args), true);
        }
    }

    /* renamed from: org.telegram.ui.ChannelIntroActivity$1 */
    class C19841 extends ActionBarMenuOnItemClick {
        C19841() {
        }

        public void onItemClick(int id) {
            if (id == -1) {
                ChannelIntroActivity.this.finishFragment();
            }
        }
    }

    public View createView(Context context) {
        this.actionBar.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setItemsColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2), false);
        this.actionBar.setItemsBackgroundColor(Theme.getColor(Theme.key_actionBarWhiteSelector), false);
        this.actionBar.setCastShadows(false);
        if (!AndroidUtilities.isTablet()) {
            this.actionBar.showActionModeTop();
        }
        this.actionBar.setActionBarMenuOnItemClick(new C19841());
        this.fragmentView = new ViewGroup(context) {
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
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

            protected void onLayout(boolean changed, int l, int t, int r, int b) {
                int width = r - l;
                int height = b - t;
                if (r > b) {
                    int y = (int) (((float) height) * 0.05f);
                    ChannelIntroActivity.this.imageView.layout(0, y, ChannelIntroActivity.this.imageView.getMeasuredWidth(), ChannelIntroActivity.this.imageView.getMeasuredHeight() + y);
                    int x = (int) (((float) width) * NUM);
                    y = (int) (((float) height) * 0.14f);
                    ChannelIntroActivity.this.whatIsChannelText.layout(x, y, ChannelIntroActivity.this.whatIsChannelText.getMeasuredWidth() + x, ChannelIntroActivity.this.whatIsChannelText.getMeasuredHeight() + y);
                    y = (int) (((float) height) * 0.61f);
                    ChannelIntroActivity.this.createChannelText.layout(x, y, ChannelIntroActivity.this.createChannelText.getMeasuredWidth() + x, ChannelIntroActivity.this.createChannelText.getMeasuredHeight() + y);
                    x = (int) (((float) width) * 0.45f);
                    y = (int) (((float) height) * 0.31f);
                    ChannelIntroActivity.this.descriptionText.layout(x, y, ChannelIntroActivity.this.descriptionText.getMeasuredWidth() + x, ChannelIntroActivity.this.descriptionText.getMeasuredHeight() + y);
                    return;
                }
                int y2 = (int) (((float) height) * NUM);
                ChannelIntroActivity.this.imageView.layout(0, y2, ChannelIntroActivity.this.imageView.getMeasuredWidth(), ChannelIntroActivity.this.imageView.getMeasuredHeight() + y2);
                y2 = (int) (((float) height) * 0.59f);
                ChannelIntroActivity.this.whatIsChannelText.layout(0, y2, ChannelIntroActivity.this.whatIsChannelText.getMeasuredWidth(), ChannelIntroActivity.this.whatIsChannelText.getMeasuredHeight() + y2);
                y2 = (int) (((float) height) * 0.68f);
                y = (int) (((float) width) * 0.05f);
                ChannelIntroActivity.this.descriptionText.layout(y, y2, ChannelIntroActivity.this.descriptionText.getMeasuredWidth() + y, ChannelIntroActivity.this.descriptionText.getMeasuredHeight() + y2);
                y2 = (int) (((float) height) * 0.86f);
                ChannelIntroActivity.this.createChannelText.layout(0, y2, ChannelIntroActivity.this.createChannelText.getMeasuredWidth(), ChannelIntroActivity.this.createChannelText.getMeasuredHeight() + y2);
            }
        };
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        ViewGroup viewGroup = this.fragmentView;
        viewGroup.setOnTouchListener(new C09923());
        this.imageView = new ImageView(context);
        this.imageView.setImageResource(R.drawable.channelintro);
        this.imageView.setScaleType(ScaleType.FIT_CENTER);
        viewGroup.addView(this.imageView);
        this.whatIsChannelText = new TextView(context);
        this.whatIsChannelText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.whatIsChannelText.setGravity(1);
        this.whatIsChannelText.setTextSize(1, 24.0f);
        this.whatIsChannelText.setText(LocaleController.getString("ChannelAlertTitle", R.string.ChannelAlertTitle));
        viewGroup.addView(this.whatIsChannelText);
        this.descriptionText = new TextView(context);
        this.descriptionText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText6));
        this.descriptionText.setGravity(1);
        this.descriptionText.setTextSize(1, 16.0f);
        this.descriptionText.setText(LocaleController.getString("ChannelAlertText", R.string.ChannelAlertText));
        viewGroup.addView(this.descriptionText);
        this.createChannelText = new TextView(context);
        this.createChannelText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText5));
        this.createChannelText.setGravity(17);
        this.createChannelText.setTextSize(1, 16.0f);
        this.createChannelText.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.createChannelText.setText(LocaleController.getString("ChannelAlertCreate", R.string.ChannelAlertCreate));
        viewGroup.addView(this.createChannelText);
        this.createChannelText.setOnClickListener(new C09934());
        return this.fragmentView;
    }

    public ThemeDescription[] getThemeDescriptions() {
        return new ThemeDescription[]{new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText2), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarWhiteSelector), new ThemeDescription(this.whatIsChannelText, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription(this.descriptionText, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText6), new ThemeDescription(this.createChannelText, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlueText5)};
    }
}
