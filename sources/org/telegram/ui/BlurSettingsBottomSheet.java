package org.telegram.ui;

import android.app.Activity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.SeekBarView;

public class BlurSettingsBottomSheet extends BottomSheet {
    public static float blurAlpha = 0.176f;
    public static float blurRadius = 1.0f;
    public static float saturation = 0.2f;

    public static void show(ChatActivity chatActivity) {
        new BlurSettingsBottomSheet(chatActivity).show();
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    private BlurSettingsBottomSheet(ChatActivity chatActivity) {
        super(chatActivity.getParentActivity(), false);
        final ChatActivity chatActivity2 = chatActivity;
        Activity parentActivity = chatActivity.getParentActivity();
        LinearLayout linearLayout = new LinearLayout(parentActivity);
        linearLayout.setOrientation(1);
        final TextView textView = new TextView(parentActivity);
        textView.setText("Saturation " + (saturation * 5.0f));
        textView.setTextColor(Theme.getColor("dialogTextBlue2"));
        textView.setTextSize(1, 16.0f);
        textView.setLines(1);
        textView.setMaxLines(1);
        textView.setSingleLine(true);
        int i = 3;
        textView.setGravity((LocaleController.isRTL ? 3 : 5) | 48);
        linearLayout.addView(textView, LayoutHelper.createFrame(-2, -1.0f, (LocaleController.isRTL ? 3 : 5) | 48, 21.0f, 13.0f, 21.0f, 0.0f));
        final SeekBarView seekBarView = new SeekBarView(parentActivity);
        seekBarView.setDelegate(new SeekBarView.SeekBarViewDelegate(this) {
            public /* synthetic */ CharSequence getContentDescription() {
                return SeekBarView.SeekBarViewDelegate.CC.$default$getContentDescription(this);
            }

            public /* synthetic */ int getStepsCount() {
                return SeekBarView.SeekBarViewDelegate.CC.$default$getStepsCount(this);
            }

            public void onSeekBarPressed(boolean z) {
            }

            public void onSeekBarDrag(boolean z, float f) {
                BlurSettingsBottomSheet.saturation = f;
                TextView textView = textView;
                textView.setText("Saturation " + (f * 5.0f));
                chatActivity2.contentView.invalidateBlurredViews();
                chatActivity2.contentView.invalidateBlur();
            }
        });
        seekBarView.setReportChanges(true);
        linearLayout.addView(seekBarView, LayoutHelper.createFrame(-1, 38.0f, 0, 5.0f, 4.0f, 5.0f, 0.0f));
        final TextView textView2 = new TextView(parentActivity);
        textView2.setText("Alpha " + blurAlpha);
        textView2.setTextColor(Theme.getColor("dialogTextBlue2"));
        textView2.setTextSize(1, 16.0f);
        textView2.setLines(1);
        textView2.setMaxLines(1);
        textView2.setSingleLine(true);
        textView2.setGravity((LocaleController.isRTL ? 3 : 5) | 48);
        linearLayout.addView(textView2, LayoutHelper.createFrame(-2, -1.0f, (LocaleController.isRTL ? 3 : 5) | 48, 21.0f, 13.0f, 21.0f, 0.0f));
        final SeekBarView seekBarView2 = new SeekBarView(parentActivity);
        seekBarView2.setDelegate(new SeekBarView.SeekBarViewDelegate(this) {
            public /* synthetic */ CharSequence getContentDescription() {
                return SeekBarView.SeekBarViewDelegate.CC.$default$getContentDescription(this);
            }

            public /* synthetic */ int getStepsCount() {
                return SeekBarView.SeekBarViewDelegate.CC.$default$getStepsCount(this);
            }

            public void onSeekBarPressed(boolean z) {
            }

            public void onSeekBarDrag(boolean z, float f) {
                TextView textView = textView2;
                textView.setText("Alpha " + BlurSettingsBottomSheet.blurAlpha);
                BlurSettingsBottomSheet.blurAlpha = f;
                chatActivity2.contentView.invalidateBlur();
            }
        });
        seekBarView2.setReportChanges(true);
        linearLayout.addView(seekBarView2, LayoutHelper.createFrame(-1, 38.0f, 0, 5.0f, 4.0f, 5.0f, 0.0f));
        TextView textView3 = new TextView(parentActivity);
        textView3.setText("Blur Radius");
        textView3.setTextColor(Theme.getColor("dialogTextBlue2"));
        textView3.setTextSize(1, 16.0f);
        textView3.setLines(1);
        textView3.setMaxLines(1);
        textView3.setSingleLine(true);
        textView3.setGravity((LocaleController.isRTL ? 3 : 5) | 48);
        linearLayout.addView(textView3, LayoutHelper.createFrame(-2, -1.0f, (!LocaleController.isRTL ? 5 : i) | 48, 21.0f, 13.0f, 21.0f, 0.0f));
        final SeekBarView seekBarView3 = new SeekBarView(parentActivity);
        seekBarView3.setDelegate(new SeekBarView.SeekBarViewDelegate(this) {
            public /* synthetic */ CharSequence getContentDescription() {
                return SeekBarView.SeekBarViewDelegate.CC.$default$getContentDescription(this);
            }

            public /* synthetic */ int getStepsCount() {
                return SeekBarView.SeekBarViewDelegate.CC.$default$getStepsCount(this);
            }

            public void onSeekBarDrag(boolean z, float f) {
                BlurSettingsBottomSheet.blurRadius = f;
                chatActivity2.contentView.invalidateBlur();
                chatActivity2.contentView.invalidateBlurredViews();
            }

            public void onSeekBarPressed(boolean z) {
                chatActivity2.contentView.invalidateBlurredViews();
            }
        });
        seekBarView3.setReportChanges(true);
        linearLayout.addView(seekBarView3, LayoutHelper.createFrame(-1, 38.0f, 0, 5.0f, 4.0f, 5.0f, 0.0f));
        linearLayout.addOnLayoutChangeListener(new View.OnLayoutChangeListener(this) {
            public void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
                seekBarView.setProgress(BlurSettingsBottomSheet.saturation);
                seekBarView3.setProgress(BlurSettingsBottomSheet.blurRadius);
                seekBarView2.setProgress(BlurSettingsBottomSheet.blurAlpha);
            }
        });
        ScrollView scrollView = new ScrollView(parentActivity);
        scrollView.addView(linearLayout);
        setCustomView(scrollView);
    }
}
