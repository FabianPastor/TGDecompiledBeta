package org.telegram.ui;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.SeekBarView;

public class BlurSettingsBottomSheet extends BottomSheet {
    public static float blurAlpha = 0.176f;
    public static float blurRadius = 1.0f;
    public static float saturation = 0.2f;
    BaseFragment fragment;

    public static void show(ChatActivity fragment2) {
        new BlurSettingsBottomSheet(fragment2).show();
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    private BlurSettingsBottomSheet(ChatActivity fragment2) {
        super(fragment2.getParentActivity(), false);
        final ChatActivity chatActivity = fragment2;
        this.fragment = chatActivity;
        Context context = fragment2.getParentActivity();
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        final TextView saturationTextView = new TextView(context);
        saturationTextView.setText("Saturation " + (saturation * 5.0f));
        saturationTextView.setTextColor(Theme.getColor("dialogTextBlue2"));
        saturationTextView.setTextSize(1, 16.0f);
        saturationTextView.setLines(1);
        saturationTextView.setMaxLines(1);
        saturationTextView.setSingleLine(true);
        int i = 3;
        saturationTextView.setGravity((LocaleController.isRTL ? 3 : 5) | 48);
        linearLayout.addView(saturationTextView, LayoutHelper.createFrame(-2, -1.0f, (LocaleController.isRTL ? 3 : 5) | 48, 21.0f, 13.0f, 21.0f, 0.0f));
        final SeekBarView seekBar = new SeekBarView(context);
        seekBar.setDelegate(new SeekBarView.SeekBarViewDelegate() {
            public /* synthetic */ CharSequence getContentDescription() {
                return SeekBarView.SeekBarViewDelegate.CC.$default$getContentDescription(this);
            }

            public /* synthetic */ int getStepsCount() {
                return SeekBarView.SeekBarViewDelegate.CC.$default$getStepsCount(this);
            }

            public void onSeekBarDrag(boolean stop, float progress) {
                BlurSettingsBottomSheet.saturation = progress;
                TextView textView = saturationTextView;
                textView.setText("Saturation " + (5.0f * progress));
                chatActivity.contentView.invalidateBlurredViews();
                chatActivity.contentView.invalidateBlur();
            }

            public void onSeekBarPressed(boolean pressed) {
            }
        });
        seekBar.setReportChanges(true);
        linearLayout.addView(seekBar, LayoutHelper.createFrame(-1, 38.0f, 0, 5.0f, 4.0f, 5.0f, 0.0f));
        final TextView alphaTextView = new TextView(context);
        alphaTextView.setText("Alpha " + blurAlpha);
        alphaTextView.setTextColor(Theme.getColor("dialogTextBlue2"));
        alphaTextView.setTextSize(1, 16.0f);
        alphaTextView.setLines(1);
        alphaTextView.setMaxLines(1);
        alphaTextView.setSingleLine(true);
        alphaTextView.setGravity((LocaleController.isRTL ? 3 : 5) | 48);
        linearLayout.addView(alphaTextView, LayoutHelper.createFrame(-2, -1.0f, (LocaleController.isRTL ? 3 : 5) | 48, 21.0f, 13.0f, 21.0f, 0.0f));
        final SeekBarView seekBar3 = new SeekBarView(context);
        seekBar3.setDelegate(new SeekBarView.SeekBarViewDelegate() {
            public /* synthetic */ CharSequence getContentDescription() {
                return SeekBarView.SeekBarViewDelegate.CC.$default$getContentDescription(this);
            }

            public /* synthetic */ int getStepsCount() {
                return SeekBarView.SeekBarViewDelegate.CC.$default$getStepsCount(this);
            }

            public void onSeekBarDrag(boolean stop, float progress) {
                TextView textView = alphaTextView;
                textView.setText("Alpha " + BlurSettingsBottomSheet.blurAlpha);
                BlurSettingsBottomSheet.blurAlpha = progress;
                chatActivity.contentView.invalidateBlur();
            }

            public void onSeekBarPressed(boolean pressed) {
            }
        });
        seekBar3.setReportChanges(true);
        linearLayout.addView(seekBar3, LayoutHelper.createFrame(-1, 38.0f, 0, 5.0f, 4.0f, 5.0f, 0.0f));
        TextView radiusTextView = new TextView(context);
        radiusTextView.setText("Blur Radius");
        radiusTextView.setTextColor(Theme.getColor("dialogTextBlue2"));
        radiusTextView.setTextSize(1, 16.0f);
        radiusTextView.setLines(1);
        radiusTextView.setMaxLines(1);
        radiusTextView.setSingleLine(true);
        radiusTextView.setGravity((LocaleController.isRTL ? 3 : 5) | 48);
        linearLayout.addView(radiusTextView, LayoutHelper.createFrame(-2, -1.0f, (!LocaleController.isRTL ? 5 : i) | 48, 21.0f, 13.0f, 21.0f, 0.0f));
        final SeekBarView seekBar2 = new SeekBarView(context);
        seekBar2.setDelegate(new SeekBarView.SeekBarViewDelegate() {
            public /* synthetic */ CharSequence getContentDescription() {
                return SeekBarView.SeekBarViewDelegate.CC.$default$getContentDescription(this);
            }

            public /* synthetic */ int getStepsCount() {
                return SeekBarView.SeekBarViewDelegate.CC.$default$getStepsCount(this);
            }

            public void onSeekBarDrag(boolean stop, float progress) {
                BlurSettingsBottomSheet.blurRadius = progress;
                chatActivity.contentView.invalidateBlur();
                chatActivity.contentView.invalidateBlurredViews();
            }

            public void onSeekBarPressed(boolean pressed) {
                chatActivity.contentView.invalidateBlurredViews();
            }
        });
        seekBar2.setReportChanges(true);
        linearLayout.addView(seekBar2, LayoutHelper.createFrame(-1, 38.0f, 0, 5.0f, 4.0f, 5.0f, 0.0f));
        linearLayout.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                seekBar.setProgress(BlurSettingsBottomSheet.saturation);
                seekBar2.setProgress(BlurSettingsBottomSheet.blurRadius);
                seekBar3.setProgress(BlurSettingsBottomSheet.blurAlpha);
            }
        });
        ScrollView scrollView = new ScrollView(context);
        scrollView.addView(linearLayout);
        setCustomView(scrollView);
    }
}
