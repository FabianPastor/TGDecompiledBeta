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
import org.telegram.messenger.C0446R;
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

    /* renamed from: org.telegram.ui.ChannelIntroActivity$3 */
    class C09983 implements OnTouchListener {
        public boolean onTouch(View view, MotionEvent motionEvent) {
            return true;
        }

        C09983() {
        }
    }

    /* renamed from: org.telegram.ui.ChannelIntroActivity$4 */
    class C09994 implements OnClickListener {
        C09994() {
        }

        public void onClick(View view) {
            view = new Bundle();
            view.putInt("step", 0);
            ChannelIntroActivity.this.presentFragment(new ChannelCreateActivity(view), true);
        }
    }

    /* renamed from: org.telegram.ui.ChannelIntroActivity$1 */
    class C19901 extends ActionBarMenuOnItemClick {
        C19901() {
        }

        public void onItemClick(int i) {
            if (i == -1) {
                ChannelIntroActivity.this.finishFragment();
            }
        }
    }

    public View createView(Context context) {
        this.actionBar.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        this.actionBar.setBackButtonImage(C0446R.drawable.ic_ab_back);
        this.actionBar.setItemsColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2), false);
        this.actionBar.setItemsBackgroundColor(Theme.getColor(Theme.key_actionBarWhiteSelector), false);
        this.actionBar.setCastShadows(false);
        if (!AndroidUtilities.isTablet()) {
            this.actionBar.showActionModeTop();
        }
        this.actionBar.setActionBarMenuOnItemClick(new C19901());
        this.fragmentView = new ViewGroup(context) {
            protected void onMeasure(int i, int i2) {
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

            protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
                z = i3 - i;
                i = i4 - i2;
                if (i3 > i4) {
                    i = (float) i;
                    i2 = (int) (NUM * i);
                    ChannelIntroActivity.this.imageView.layout(0, i2, ChannelIntroActivity.this.imageView.getMeasuredWidth(), ChannelIntroActivity.this.imageView.getMeasuredHeight() + i2);
                    z = (float) z;
                    i2 = (int) (NUM * z);
                    i3 = (int) (NUM * i);
                    ChannelIntroActivity.this.whatIsChannelText.layout(i2, i3, ChannelIntroActivity.this.whatIsChannelText.getMeasuredWidth() + i2, ChannelIntroActivity.this.whatIsChannelText.getMeasuredHeight() + i3);
                    i3 = (int) (NUM * i);
                    ChannelIntroActivity.this.createChannelText.layout(i2, i3, ChannelIntroActivity.this.createChannelText.getMeasuredWidth() + i2, ChannelIntroActivity.this.createChannelText.getMeasuredHeight() + i3);
                    z = (int) (z * true);
                    i = (int) (i * NUM);
                    ChannelIntroActivity.this.descriptionText.layout(z, i, ChannelIntroActivity.this.descriptionText.getMeasuredWidth() + z, ChannelIntroActivity.this.descriptionText.getMeasuredHeight() + i);
                    return;
                }
                i = (float) i;
                i3 = (int) (i * NUM);
                ChannelIntroActivity.this.imageView.layout(0, i3, ChannelIntroActivity.this.imageView.getMeasuredWidth(), ChannelIntroActivity.this.imageView.getMeasuredHeight() + i3);
                i3 = (int) (NUM * i);
                ChannelIntroActivity.this.whatIsChannelText.layout(0, i3, ChannelIntroActivity.this.whatIsChannelText.getMeasuredWidth(), ChannelIntroActivity.this.whatIsChannelText.getMeasuredHeight() + i3);
                i3 = (int) (NUM * i);
                z = (int) (((float) z) * true);
                ChannelIntroActivity.this.descriptionText.layout(z, i3, ChannelIntroActivity.this.descriptionText.getMeasuredWidth() + z, ChannelIntroActivity.this.descriptionText.getMeasuredHeight() + i3);
                z = (int) (i * true);
                ChannelIntroActivity.this.createChannelText.layout(0, z, ChannelIntroActivity.this.createChannelText.getMeasuredWidth(), ChannelIntroActivity.this.createChannelText.getMeasuredHeight() + z);
            }
        };
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        ViewGroup viewGroup = (ViewGroup) this.fragmentView;
        viewGroup.setOnTouchListener(new C09983());
        this.imageView = new ImageView(context);
        this.imageView.setImageResource(C0446R.drawable.channelintro);
        this.imageView.setScaleType(ScaleType.FIT_CENTER);
        viewGroup.addView(this.imageView);
        this.whatIsChannelText = new TextView(context);
        this.whatIsChannelText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.whatIsChannelText.setGravity(1);
        this.whatIsChannelText.setTextSize(1, 24.0f);
        this.whatIsChannelText.setText(LocaleController.getString("ChannelAlertTitle", C0446R.string.ChannelAlertTitle));
        viewGroup.addView(this.whatIsChannelText);
        this.descriptionText = new TextView(context);
        this.descriptionText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText6));
        this.descriptionText.setGravity(1);
        this.descriptionText.setTextSize(1, 16.0f);
        this.descriptionText.setText(LocaleController.getString("ChannelAlertText", C0446R.string.ChannelAlertText));
        viewGroup.addView(this.descriptionText);
        this.createChannelText = new TextView(context);
        this.createChannelText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText5));
        this.createChannelText.setGravity(17);
        this.createChannelText.setTextSize(1, 16.0f);
        this.createChannelText.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.createChannelText.setText(LocaleController.getString("ChannelAlertCreate", C0446R.string.ChannelAlertCreate));
        viewGroup.addView(this.createChannelText);
        this.createChannelText.setOnClickListener(new C09994());
        return this.fragmentView;
    }

    public ThemeDescription[] getThemeDescriptions() {
        return new ThemeDescription[]{new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText2), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarWhiteSelector), new ThemeDescription(this.whatIsChannelText, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription(this.descriptionText, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText6), new ThemeDescription(this.createChannelText, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlueText5)};
    }
}
