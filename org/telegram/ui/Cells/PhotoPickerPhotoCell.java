package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.beta.R;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CheckBox;
import org.telegram.ui.Components.LayoutHelper;

public class PhotoPickerPhotoCell extends FrameLayout {
    private AnimatorSet animator;
    public CheckBox checkBox;
    public FrameLayout checkFrame;
    public int itemWidth;
    public BackupImageView photoImage;

    public PhotoPickerPhotoCell(Context context) {
        super(context);
        this.photoImage = new BackupImageView(context);
        addView(this.photoImage, LayoutHelper.createFrame(-1, -1.0f));
        this.checkFrame = new FrameLayout(context);
        addView(this.checkFrame, LayoutHelper.createFrame(42, 42, 53));
        this.checkBox = new CheckBox(context, R.drawable.checkbig);
        this.checkBox.setSize(30);
        this.checkBox.setCheckOffset(AndroidUtilities.dp(1.0f));
        this.checkBox.setDrawBackground(true);
        this.checkBox.setColor(-12793105);
        addView(this.checkBox, LayoutHelper.createFrame(30, BitmapDescriptorFactory.HUE_ORANGE, 53, 0.0f, 4.0f, 4.0f, 0.0f));
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(this.itemWidth, NUM), MeasureSpec.makeMeasureSpec(this.itemWidth, NUM));
    }

    public void setChecked(final boolean checked, boolean animated) {
        int i = -16119286;
        float f = 0.85f;
        this.checkBox.setChecked(checked, animated);
        if (this.animator != null) {
            this.animator.cancel();
            this.animator = null;
        }
        if (animated) {
            if (checked) {
                setBackgroundColor(-16119286);
            }
            this.animator = new AnimatorSet();
            AnimatorSet animatorSet = this.animator;
            Animator[] animatorArr = new Animator[2];
            BackupImageView backupImageView = this.photoImage;
            String str = "scaleX";
            float[] fArr = new float[1];
            fArr[0] = checked ? 0.85f : 1.0f;
            animatorArr[0] = ObjectAnimator.ofFloat(backupImageView, str, fArr);
            BackupImageView backupImageView2 = this.photoImage;
            String str2 = "scaleY";
            float[] fArr2 = new float[1];
            if (!checked) {
                f = 1.0f;
            }
            fArr2[0] = f;
            animatorArr[1] = ObjectAnimator.ofFloat(backupImageView2, str2, fArr2);
            animatorSet.playTogether(animatorArr);
            this.animator.setDuration(200);
            this.animator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    if (PhotoPickerPhotoCell.this.animator != null && PhotoPickerPhotoCell.this.animator.equals(animation)) {
                        PhotoPickerPhotoCell.this.animator = null;
                        if (!checked) {
                            PhotoPickerPhotoCell.this.setBackgroundColor(0);
                        }
                    }
                }

                public void onAnimationCancel(Animator animation) {
                    if (PhotoPickerPhotoCell.this.animator != null && PhotoPickerPhotoCell.this.animator.equals(animation)) {
                        PhotoPickerPhotoCell.this.animator = null;
                    }
                }
            });
            this.animator.start();
            return;
        }
        float f2;
        if (!checked) {
            i = 0;
        }
        setBackgroundColor(i);
        BackupImageView backupImageView3 = this.photoImage;
        if (checked) {
            f2 = 0.85f;
        } else {
            f2 = 1.0f;
        }
        backupImageView3.setScaleX(f2);
        backupImageView2 = this.photoImage;
        if (!checked) {
            f = 1.0f;
        }
        backupImageView2.setScaleY(f);
    }
}
