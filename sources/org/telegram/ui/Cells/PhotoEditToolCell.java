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
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.PhotoEditorSeekBar;

public class PhotoEditToolCell extends FrameLayout {
    /* access modifiers changed from: private */
    public Runnable hideValueRunnable = new Runnable() {
        public void run() {
            PhotoEditToolCell.this.valueTextView.setTag((Object) null);
            AnimatorSet unused = PhotoEditToolCell.this.valueAnimation = new AnimatorSet();
            PhotoEditToolCell.this.valueAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(PhotoEditToolCell.this.valueTextView, "alpha", new float[]{0.0f}), ObjectAnimator.ofFloat(PhotoEditToolCell.this.nameTextView, "alpha", new float[]{1.0f})});
            PhotoEditToolCell.this.valueAnimation.setDuration(180);
            PhotoEditToolCell.this.valueAnimation.setInterpolator(new DecelerateInterpolator());
            PhotoEditToolCell.this.valueAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    if (animator.equals(PhotoEditToolCell.this.valueAnimation)) {
                        AnimatorSet unused = PhotoEditToolCell.this.valueAnimation = null;
                    }
                }
            });
            PhotoEditToolCell.this.valueAnimation.start();
        }
    };
    /* access modifiers changed from: private */
    public TextView nameTextView;
    private PhotoEditorSeekBar seekBar;
    /* access modifiers changed from: private */
    public AnimatorSet valueAnimation;
    /* access modifiers changed from: private */
    public TextView valueTextView;

    public PhotoEditToolCell(Context context) {
        super(context);
        this.nameTextView = new TextView(context);
        this.nameTextView.setGravity(5);
        this.nameTextView.setTextColor(-1);
        this.nameTextView.setTextSize(1, 12.0f);
        this.nameTextView.setMaxLines(1);
        this.nameTextView.setSingleLine(true);
        this.nameTextView.setEllipsize(TextUtils.TruncateAt.END);
        addView(this.nameTextView, LayoutHelper.createFrame(80, -2.0f, 19, 0.0f, 0.0f, 0.0f, 0.0f));
        this.valueTextView = new TextView(context);
        this.valueTextView.setTextColor(-9649153);
        this.valueTextView.setTextSize(1, 12.0f);
        this.valueTextView.setGravity(5);
        this.valueTextView.setSingleLine(true);
        addView(this.valueTextView, LayoutHelper.createFrame(80, -2.0f, 19, 0.0f, 0.0f, 0.0f, 0.0f));
        this.seekBar = new PhotoEditorSeekBar(context);
        addView(this.seekBar, LayoutHelper.createFrame(-1, 40.0f, 19, 96.0f, 0.0f, 24.0f, 0.0f));
    }

    public void setSeekBarDelegate(final PhotoEditorSeekBar.PhotoEditorSeekBarDelegate photoEditorSeekBarDelegate) {
        this.seekBar.setDelegate(new PhotoEditorSeekBar.PhotoEditorSeekBarDelegate() {
            public void onProgressChanged(int i, int i2) {
                photoEditorSeekBarDelegate.onProgressChanged(i, i2);
                if (i2 > 0) {
                    TextView access$000 = PhotoEditToolCell.this.valueTextView;
                    access$000.setText("+" + i2);
                } else {
                    TextView access$0002 = PhotoEditToolCell.this.valueTextView;
                    access$0002.setText("" + i2);
                }
                if (PhotoEditToolCell.this.valueTextView.getTag() == null) {
                    if (PhotoEditToolCell.this.valueAnimation != null) {
                        PhotoEditToolCell.this.valueAnimation.cancel();
                    }
                    PhotoEditToolCell.this.valueTextView.setTag(1);
                    AnimatorSet unused = PhotoEditToolCell.this.valueAnimation = new AnimatorSet();
                    PhotoEditToolCell.this.valueAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(PhotoEditToolCell.this.valueTextView, "alpha", new float[]{1.0f}), ObjectAnimator.ofFloat(PhotoEditToolCell.this.nameTextView, "alpha", new float[]{0.0f})});
                    PhotoEditToolCell.this.valueAnimation.setDuration(180);
                    PhotoEditToolCell.this.valueAnimation.setInterpolator(new DecelerateInterpolator());
                    PhotoEditToolCell.this.valueAnimation.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            AndroidUtilities.runOnUIThread(PhotoEditToolCell.this.hideValueRunnable, 1000);
                        }
                    });
                    PhotoEditToolCell.this.valueAnimation.start();
                    return;
                }
                AndroidUtilities.cancelRunOnUIThread(PhotoEditToolCell.this.hideValueRunnable);
                AndroidUtilities.runOnUIThread(PhotoEditToolCell.this.hideValueRunnable, 1000);
            }
        });
    }

    public void setTag(Object obj) {
        super.setTag(obj);
        this.seekBar.setTag(obj);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(40.0f), NUM));
    }

    public void setIconAndTextAndValue(String str, float f, int i, int i2) {
        AnimatorSet animatorSet = this.valueAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.valueAnimation = null;
        }
        AndroidUtilities.cancelRunOnUIThread(this.hideValueRunnable);
        this.valueTextView.setTag((Object) null);
        TextView textView = this.nameTextView;
        textView.setText(str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase());
        if (f > 0.0f) {
            TextView textView2 = this.valueTextView;
            textView2.setText("+" + ((int) f));
        } else {
            TextView textView3 = this.valueTextView;
            textView3.setText("" + ((int) f));
        }
        this.valueTextView.setAlpha(0.0f);
        this.nameTextView.setAlpha(1.0f);
        this.seekBar.setMinMax(i, i2);
        this.seekBar.setProgress((int) f, false);
    }
}
