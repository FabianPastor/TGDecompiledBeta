package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.text.TextUtils.TruncateAt;
import android.view.View.MeasureSpec;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.PhotoEditorSeekBar;
import org.telegram.ui.Components.PhotoEditorSeekBar.PhotoEditorSeekBarDelegate;

public class PhotoEditToolCell extends FrameLayout {
    private Runnable hideValueRunnable = new Runnable() {
        public void run() {
            PhotoEditToolCell.this.valueTextView.setTag(null);
            PhotoEditToolCell.this.valueAnimation = new AnimatorSet();
            AnimatorSet access$100 = PhotoEditToolCell.this.valueAnimation;
            Animator[] animatorArr = new Animator[2];
            String str = "alpha";
            animatorArr[0] = ObjectAnimator.ofFloat(PhotoEditToolCell.this.valueTextView, str, new float[]{0.0f});
            animatorArr[1] = ObjectAnimator.ofFloat(PhotoEditToolCell.this.nameTextView, str, new float[]{1.0f});
            access$100.playTogether(animatorArr);
            PhotoEditToolCell.this.valueAnimation.setDuration(180);
            PhotoEditToolCell.this.valueAnimation.setInterpolator(new DecelerateInterpolator());
            PhotoEditToolCell.this.valueAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    if (animator.equals(PhotoEditToolCell.this.valueAnimation)) {
                        PhotoEditToolCell.this.valueAnimation = null;
                    }
                }
            });
            PhotoEditToolCell.this.valueAnimation.start();
        }
    };
    private TextView nameTextView;
    private PhotoEditorSeekBar seekBar;
    private AnimatorSet valueAnimation;
    private TextView valueTextView;

    public PhotoEditToolCell(Context context) {
        super(context);
        this.nameTextView = new TextView(context);
        this.nameTextView.setGravity(5);
        this.nameTextView.setTextColor(-1);
        this.nameTextView.setTextSize(1, 12.0f);
        this.nameTextView.setMaxLines(1);
        this.nameTextView.setSingleLine(true);
        this.nameTextView.setEllipsize(TruncateAt.END);
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

    public void setSeekBarDelegate(final PhotoEditorSeekBarDelegate photoEditorSeekBarDelegate) {
        this.seekBar.setDelegate(new PhotoEditorSeekBarDelegate() {
            public void onProgressChanged(int i, int i2) {
                photoEditorSeekBarDelegate.onProgressChanged(i, i2);
                TextView access$000;
                StringBuilder stringBuilder;
                if (i2 > 0) {
                    access$000 = PhotoEditToolCell.this.valueTextView;
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("+");
                    stringBuilder.append(i2);
                    access$000.setText(stringBuilder.toString());
                } else {
                    access$000 = PhotoEditToolCell.this.valueTextView;
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("");
                    stringBuilder.append(i2);
                    access$000.setText(stringBuilder.toString());
                }
                if (PhotoEditToolCell.this.valueTextView.getTag() == null) {
                    if (PhotoEditToolCell.this.valueAnimation != null) {
                        PhotoEditToolCell.this.valueAnimation.cancel();
                    }
                    PhotoEditToolCell.this.valueTextView.setTag(Integer.valueOf(1));
                    PhotoEditToolCell.this.valueAnimation = new AnimatorSet();
                    AnimatorSet access$100 = PhotoEditToolCell.this.valueAnimation;
                    Animator[] animatorArr = new Animator[2];
                    String str = "alpha";
                    animatorArr[0] = ObjectAnimator.ofFloat(PhotoEditToolCell.this.valueTextView, str, new float[]{1.0f});
                    animatorArr[1] = ObjectAnimator.ofFloat(PhotoEditToolCell.this.nameTextView, str, new float[]{0.0f});
                    access$100.playTogether(animatorArr);
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

    /* Access modifiers changed, original: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(40.0f), NUM));
    }

    public void setIconAndTextAndValue(String str, float f, int i, int i2) {
        AnimatorSet animatorSet = this.valueAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.valueAnimation = null;
        }
        AndroidUtilities.cancelRunOnUIThread(this.hideValueRunnable);
        this.valueTextView.setTag(null);
        TextView textView = this.nameTextView;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(str.substring(0, 1).toUpperCase());
        stringBuilder.append(str.substring(1).toLowerCase());
        textView.setText(stringBuilder.toString());
        if (f > 0.0f) {
            textView = this.valueTextView;
            stringBuilder = new StringBuilder();
            stringBuilder.append("+");
            stringBuilder.append((int) f);
            textView.setText(stringBuilder.toString());
        } else {
            textView = this.valueTextView;
            stringBuilder = new StringBuilder();
            stringBuilder.append("");
            stringBuilder.append((int) f);
            textView.setText(stringBuilder.toString());
        }
        this.valueTextView.setAlpha(0.0f);
        this.nameTextView.setAlpha(1.0f);
        this.seekBar.setMinMax(i, i2);
        this.seekBar.setProgress((int) f, false);
    }
}
