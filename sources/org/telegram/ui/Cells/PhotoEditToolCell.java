package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.PhotoEditorSeekBar;

public class PhotoEditToolCell extends FrameLayout {
    /* access modifiers changed from: private */
    public Runnable hideValueRunnable = new Runnable() {
        public void run() {
            PhotoEditToolCell.this.valueTextView.setTag((Object) null);
            AnimatorSet unused = PhotoEditToolCell.this.valueAnimation = new AnimatorSet();
            PhotoEditToolCell.this.valueAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(PhotoEditToolCell.this.valueTextView, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(PhotoEditToolCell.this.nameTextView, View.ALPHA, new float[]{1.0f})});
            PhotoEditToolCell.this.valueAnimation.setDuration(250);
            PhotoEditToolCell.this.valueAnimation.setInterpolator(new DecelerateInterpolator());
            PhotoEditToolCell.this.valueAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    if (animation.equals(PhotoEditToolCell.this.valueAnimation)) {
                        AnimatorSet unused = PhotoEditToolCell.this.valueAnimation = null;
                    }
                }
            });
            PhotoEditToolCell.this.valueAnimation.start();
        }
    };
    /* access modifiers changed from: private */
    public TextView nameTextView;
    private final Theme.ResourcesProvider resourcesProvider;
    private PhotoEditorSeekBar seekBar;
    /* access modifiers changed from: private */
    public AnimatorSet valueAnimation;
    /* access modifiers changed from: private */
    public TextView valueTextView;

    public PhotoEditToolCell(Context context, Theme.ResourcesProvider resourcesProvider2) {
        super(context);
        this.resourcesProvider = resourcesProvider2;
        TextView textView = new TextView(context);
        this.nameTextView = textView;
        textView.setGravity(5);
        this.nameTextView.setTextColor(-1);
        this.nameTextView.setTextSize(1, 12.0f);
        this.nameTextView.setMaxLines(1);
        this.nameTextView.setSingleLine(true);
        this.nameTextView.setEllipsize(TextUtils.TruncateAt.END);
        addView(this.nameTextView, LayoutHelper.createFrame(80, -2.0f, 19, 0.0f, 0.0f, 0.0f, 0.0f));
        TextView textView2 = new TextView(context);
        this.valueTextView = textView2;
        textView2.setTextColor(getThemedColor("dialogFloatingButton"));
        this.valueTextView.setTextSize(1, 12.0f);
        this.valueTextView.setGravity(5);
        this.valueTextView.setSingleLine(true);
        addView(this.valueTextView, LayoutHelper.createFrame(80, -2.0f, 19, 0.0f, 0.0f, 0.0f, 0.0f));
        PhotoEditorSeekBar photoEditorSeekBar = new PhotoEditorSeekBar(context);
        this.seekBar = photoEditorSeekBar;
        addView(photoEditorSeekBar, LayoutHelper.createFrame(-1, 40.0f, 19, 96.0f, 0.0f, 24.0f, 0.0f));
    }

    public void setSeekBarDelegate(PhotoEditorSeekBar.PhotoEditorSeekBarDelegate photoEditorSeekBarDelegate) {
        this.seekBar.setDelegate(new PhotoEditToolCell$$ExternalSyntheticLambda0(this, photoEditorSeekBarDelegate));
    }

    /* renamed from: lambda$setSeekBarDelegate$0$org-telegram-ui-Cells-PhotoEditToolCell  reason: not valid java name */
    public /* synthetic */ void m1546x61679307(PhotoEditorSeekBar.PhotoEditorSeekBarDelegate photoEditorSeekBarDelegate, int i, int progress) {
        photoEditorSeekBarDelegate.onProgressChanged(i, progress);
        if (progress > 0) {
            TextView textView = this.valueTextView;
            textView.setText("+" + progress);
        } else {
            TextView textView2 = this.valueTextView;
            textView2.setText("" + progress);
        }
        if (this.valueTextView.getTag() == null) {
            AnimatorSet animatorSet = this.valueAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
            }
            this.valueTextView.setTag(1);
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.valueAnimation = animatorSet2;
            animatorSet2.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.valueTextView, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.nameTextView, View.ALPHA, new float[]{0.0f})});
            this.valueAnimation.setDuration(250);
            this.valueAnimation.setInterpolator(new DecelerateInterpolator());
            this.valueAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    AndroidUtilities.runOnUIThread(PhotoEditToolCell.this.hideValueRunnable, 1000);
                }
            });
            this.valueAnimation.start();
            return;
        }
        AndroidUtilities.cancelRunOnUIThread(this.hideValueRunnable);
        AndroidUtilities.runOnUIThread(this.hideValueRunnable, 1000);
    }

    public void setTag(Object tag) {
        super.setTag(tag);
        this.seekBar.setTag(tag);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(40.0f), NUM));
    }

    public void setIconAndTextAndValue(String text, float value, int min, int max) {
        AnimatorSet animatorSet = this.valueAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.valueAnimation = null;
        }
        AndroidUtilities.cancelRunOnUIThread(this.hideValueRunnable);
        this.valueTextView.setTag((Object) null);
        TextView textView = this.nameTextView;
        textView.setText(text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase());
        if (value > 0.0f) {
            TextView textView2 = this.valueTextView;
            textView2.setText("+" + ((int) value));
        } else {
            TextView textView3 = this.valueTextView;
            textView3.setText("" + ((int) value));
        }
        this.valueTextView.setAlpha(0.0f);
        this.nameTextView.setAlpha(1.0f);
        this.seekBar.setMinMax(min, max);
        this.seekBar.setProgress((int) value, false);
    }

    private int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }
}
