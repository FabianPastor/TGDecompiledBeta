package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0446R;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CheckBox;
import org.telegram.ui.Components.LayoutHelper;

public class PhotoPickerPhotoCell extends FrameLayout {
    private AnimatorSet animator;
    private AnimatorSet animatorSet;
    public CheckBox checkBox;
    public FrameLayout checkFrame;
    public int itemWidth;
    public BackupImageView photoImage;
    public FrameLayout videoInfoContainer;
    public TextView videoTextView;
    private boolean zoomOnSelect;

    /* renamed from: org.telegram.ui.Cells.PhotoPickerPhotoCell$1 */
    class C08891 extends AnimatorListenerAdapter {
        C08891() {
        }

        public void onAnimationEnd(Animator animator) {
            if (animator.equals(PhotoPickerPhotoCell.this.animatorSet) != null) {
                PhotoPickerPhotoCell.this.animatorSet = null;
            }
        }
    }

    public PhotoPickerPhotoCell(Context context, boolean z) {
        super(context);
        this.zoomOnSelect = z;
        this.photoImage = new BackupImageView(context);
        addView(this.photoImage, LayoutHelper.createFrame(-1, -1.0f));
        this.checkFrame = new FrameLayout(context);
        addView(this.checkFrame, LayoutHelper.createFrame(42, 42, 53));
        this.videoInfoContainer = new FrameLayout(context);
        this.videoInfoContainer.setBackgroundResource(C0446R.drawable.phototime);
        this.videoInfoContainer.setPadding(AndroidUtilities.dp(3.0f), 0, AndroidUtilities.dp(3.0f), 0);
        addView(this.videoInfoContainer, LayoutHelper.createFrame(-1, 16, 83));
        View imageView = new ImageView(context);
        imageView.setImageResource(C0446R.drawable.ic_video);
        this.videoInfoContainer.addView(imageView, LayoutHelper.createFrame(-2, -2, 19));
        this.videoTextView = new TextView(context);
        this.videoTextView.setTextColor(-1);
        this.videoTextView.setTextSize(1, 12.0f);
        this.videoInfoContainer.addView(this.videoTextView, LayoutHelper.createFrame(-2, -2.0f, 19, 18.0f, -0.7f, 0.0f, 0.0f));
        this.checkBox = new CheckBox(context, C0446R.drawable.checkbig);
        this.checkBox.setSize(z ? 30 : 26);
        this.checkBox.setCheckOffset(AndroidUtilities.dp(1.0f));
        this.checkBox.setDrawBackground(true);
        this.checkBox.setColor(-10043398, -1);
        addView(this.checkBox, LayoutHelper.createFrame(z ? 30 : 26, z ? true : true, 53, 0.0f, 4.0f, 4.0f, 0.0f));
    }

    protected void onMeasure(int i, int i2) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(this.itemWidth, NUM), MeasureSpec.makeMeasureSpec(this.itemWidth, NUM));
    }

    public void showCheck(boolean z) {
        if (this.animatorSet != null) {
            this.animatorSet.cancel();
            this.animatorSet = null;
        }
        this.animatorSet = new AnimatorSet();
        this.animatorSet.setInterpolator(new DecelerateInterpolator());
        this.animatorSet.setDuration(180);
        AnimatorSet animatorSet = this.animatorSet;
        Animator[] animatorArr = new Animator[2];
        FrameLayout frameLayout = this.videoInfoContainer;
        String str = "alpha";
        float[] fArr = new float[1];
        float f = 0.0f;
        fArr[0] = z ? 1.0f : 0.0f;
        animatorArr[0] = ObjectAnimator.ofFloat(frameLayout, str, fArr);
        CheckBox checkBox = this.checkBox;
        str = "alpha";
        fArr = new float[1];
        if (z) {
            f = 1.0f;
        }
        fArr[0] = f;
        animatorArr[1] = ObjectAnimator.ofFloat(checkBox, str, fArr);
        animatorSet.playTogether(animatorArr);
        this.animatorSet.addListener(new C08891());
        this.animatorSet.start();
    }

    public void setNum(int i) {
        this.checkBox.setNum(i);
    }

    public void setChecked(int i, final boolean z, boolean z2) {
        this.checkBox.setChecked(i, z, z2);
        if (this.animator != 0) {
            this.animator.cancel();
            this.animator = 0;
        }
        if (this.zoomOnSelect != 0) {
            i = -16119286;
            float f = 1.0f;
            if (z2) {
                if (z) {
                    setBackgroundColor(-16119286);
                }
                this.animator = new AnimatorSet();
                i = this.animator;
                z2 = new Animator[true];
                BackupImageView backupImageView = this.photoImage;
                String str = "scaleX";
                float[] fArr = new float[1];
                fArr[0] = z ? 0.85f : 1.0f;
                z2[0] = ObjectAnimator.ofFloat(backupImageView, str, fArr);
                backupImageView = this.photoImage;
                str = "scaleY";
                fArr = new float[1];
                if (z) {
                    f = 0.85f;
                }
                fArr[0] = f;
                z2[1] = ObjectAnimator.ofFloat(backupImageView, str, fArr);
                i.playTogether(z2);
                this.animator.setDuration(200);
                this.animator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        if (PhotoPickerPhotoCell.this.animator != null && PhotoPickerPhotoCell.this.animator.equals(animator) != null) {
                            PhotoPickerPhotoCell.this.animator = null;
                            if (z == null) {
                                PhotoPickerPhotoCell.this.setBackgroundColor(0);
                            }
                        }
                    }

                    public void onAnimationCancel(Animator animator) {
                        if (PhotoPickerPhotoCell.this.animator != null && PhotoPickerPhotoCell.this.animator.equals(animator) != null) {
                            PhotoPickerPhotoCell.this.animator = null;
                        }
                    }
                });
                this.animator.start();
                return;
            }
            if (!z) {
                i = 0;
            }
            setBackgroundColor(i);
            this.photoImage.setScaleX(z ? true : true);
            i = this.photoImage;
            if (z) {
                f = 0.85f;
            }
            i.setScaleY(f);
        }
    }
}
