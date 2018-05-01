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
    private Runnable hideValueRunnable = new C08861();
    private TextView nameTextView;
    private PhotoEditorSeekBar seekBar;
    private AnimatorSet valueAnimation;
    private TextView valueTextView;

    /* renamed from: org.telegram.ui.Cells.PhotoEditToolCell$1 */
    class C08861 implements Runnable {

        /* renamed from: org.telegram.ui.Cells.PhotoEditToolCell$1$1 */
        class C08851 extends AnimatorListenerAdapter {
            C08851() {
            }

            public void onAnimationEnd(Animator animator) {
                if (animator.equals(PhotoEditToolCell.this.valueAnimation) != null) {
                    PhotoEditToolCell.this.valueAnimation = null;
                }
            }
        }

        C08861() {
        }

        public void run() {
            PhotoEditToolCell.this.valueTextView.setTag(null);
            PhotoEditToolCell.this.valueAnimation = new AnimatorSet();
            AnimatorSet access$100 = PhotoEditToolCell.this.valueAnimation;
            r1 = new Animator[2];
            r1[0] = ObjectAnimator.ofFloat(PhotoEditToolCell.this.valueTextView, "alpha", new float[]{0.0f});
            r1[1] = ObjectAnimator.ofFloat(PhotoEditToolCell.this.nameTextView, "alpha", new float[]{1.0f});
            access$100.playTogether(r1);
            PhotoEditToolCell.this.valueAnimation.setDuration(180);
            PhotoEditToolCell.this.valueAnimation.setInterpolator(new DecelerateInterpolator());
            PhotoEditToolCell.this.valueAnimation.addListener(new C08851());
            PhotoEditToolCell.this.valueAnimation.start();
        }
    }

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

            /* renamed from: org.telegram.ui.Cells.PhotoEditToolCell$2$1 */
            class C08871 extends AnimatorListenerAdapter {
                C08871() {
                }

                public void onAnimationEnd(Animator animator) {
                    AndroidUtilities.runOnUIThread(PhotoEditToolCell.this.hideValueRunnable, 1000);
                }
            }

            public void onProgressChanged(int i, int i2) {
                photoEditorSeekBarDelegate.onProgressChanged(i, i2);
                StringBuilder stringBuilder;
                if (i2 > 0) {
                    i = PhotoEditToolCell.this.valueTextView;
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("+");
                    stringBuilder.append(i2);
                    i.setText(stringBuilder.toString());
                } else {
                    i = PhotoEditToolCell.this.valueTextView;
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(TtmlNode.ANONYMOUS_REGION_ID);
                    stringBuilder.append(i2);
                    i.setText(stringBuilder.toString());
                }
                if (PhotoEditToolCell.this.valueTextView.getTag() == 0) {
                    if (PhotoEditToolCell.this.valueAnimation != 0) {
                        PhotoEditToolCell.this.valueAnimation.cancel();
                    }
                    PhotoEditToolCell.this.valueTextView.setTag(Integer.valueOf(1));
                    PhotoEditToolCell.this.valueAnimation = new AnimatorSet();
                    i = PhotoEditToolCell.this.valueAnimation;
                    r0 = new Animator[2];
                    r0[0] = ObjectAnimator.ofFloat(PhotoEditToolCell.this.valueTextView, "alpha", new float[]{1.0f});
                    r0[1] = ObjectAnimator.ofFloat(PhotoEditToolCell.this.nameTextView, "alpha", new float[]{0.0f});
                    i.playTogether(r0);
                    PhotoEditToolCell.this.valueAnimation.setDuration(180);
                    PhotoEditToolCell.this.valueAnimation.setInterpolator(new DecelerateInterpolator());
                    PhotoEditToolCell.this.valueAnimation.addListener(new C08871());
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

    protected void onMeasure(int i, int i2) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(40.0f), NUM));
    }

    public void setIconAndTextAndValue(String str, float f, int i, int i2) {
        if (this.valueAnimation != null) {
            this.valueAnimation.cancel();
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
            stringBuilder.append(TtmlNode.ANONYMOUS_REGION_ID);
            stringBuilder.append((int) f);
            textView.setText(stringBuilder.toString());
        }
        this.valueTextView.setAlpha(0.0f);
        this.nameTextView.setAlpha(1.0f);
        this.seekBar.setMinMax(i, i2);
        this.seekBar.setProgress((int) f, false);
    }
}
