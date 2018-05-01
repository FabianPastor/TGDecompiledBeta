package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0446R;
import org.telegram.messenger.MediaController.PhotoEntry;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CheckBox;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.PhotoViewer;

public class PhotoAttachPhotoCell extends FrameLayout {
    private static Rect rect = new Rect();
    private AnimatorSet animatorSet;
    private CheckBox checkBox;
    private FrameLayout checkFrame;
    private PhotoAttachPhotoCellDelegate delegate;
    private BackupImageView imageView;
    private boolean isLast;
    private boolean isVertical;
    private boolean needCheckShow;
    private PhotoEntry photoEntry;
    private boolean pressed;
    private FrameLayout videoInfoContainer;
    private TextView videoTextView;

    /* renamed from: org.telegram.ui.Cells.PhotoAttachPhotoCell$1 */
    class C08831 extends AnimatorListenerAdapter {
        C08831() {
        }

        public void onAnimationEnd(Animator animator) {
            if (animator.equals(PhotoAttachPhotoCell.this.animatorSet) != null) {
                PhotoAttachPhotoCell.this.animatorSet = null;
            }
        }
    }

    public interface PhotoAttachPhotoCellDelegate {
        void onCheckClick(PhotoAttachPhotoCell photoAttachPhotoCell);
    }

    public PhotoAttachPhotoCell(Context context) {
        super(context);
        this.imageView = new BackupImageView(context);
        addView(this.imageView, LayoutHelper.createFrame(80, 80.0f));
        this.checkFrame = new FrameLayout(context);
        addView(this.checkFrame, LayoutHelper.createFrame(42, 42.0f, 51, 38.0f, 0.0f, 0.0f, 0.0f));
        this.videoInfoContainer = new FrameLayout(context);
        this.videoInfoContainer.setBackgroundResource(C0446R.drawable.phototime);
        this.videoInfoContainer.setPadding(AndroidUtilities.dp(3.0f), 0, AndroidUtilities.dp(3.0f), 0);
        addView(this.videoInfoContainer, LayoutHelper.createFrame(80, 16, 83));
        View imageView = new ImageView(context);
        imageView.setImageResource(C0446R.drawable.ic_video);
        this.videoInfoContainer.addView(imageView, LayoutHelper.createFrame(-2, -2, 19));
        this.videoTextView = new TextView(context);
        this.videoTextView.setTextColor(-1);
        this.videoTextView.setTextSize(1, 12.0f);
        this.videoInfoContainer.addView(this.videoTextView, LayoutHelper.createFrame(-2, -2.0f, 19, 18.0f, -0.7f, 0.0f, 0.0f));
        this.checkBox = new CheckBox(context, C0446R.drawable.checkbig);
        this.checkBox.setSize(30);
        this.checkBox.setCheckOffset(AndroidUtilities.dp(1.0f));
        this.checkBox.setDrawBackground(true);
        this.checkBox.setColor(-12793105, -1);
        addView(this.checkBox, LayoutHelper.createFrame(30, 30.0f, 51, 46.0f, 4.0f, 0.0f, 0.0f));
        this.checkBox.setVisibility(0);
    }

    public void setIsVertical(boolean z) {
        this.isVertical = z;
    }

    protected void onMeasure(int i, int i2) {
        i2 = 6;
        if (this.isVertical != 0) {
            i = MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(80.0f), NUM);
            if (this.isLast) {
                i2 = 0;
            }
            super.onMeasure(i, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp((float) (80 + i2)), NUM));
            return;
        }
        if (this.isLast != 0) {
            i2 = 0;
        }
        super.onMeasure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp((float) (80 + i2)), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(80.0f), NUM));
    }

    public PhotoEntry getPhotoEntry() {
        return this.photoEntry;
    }

    public BackupImageView getImageView() {
        return this.imageView;
    }

    public CheckBox getCheckBox() {
        return this.checkBox;
    }

    public FrameLayout getCheckFrame() {
        return this.checkFrame;
    }

    public View getVideoInfoContainer() {
        return this.videoInfoContainer;
    }

    public void setPhotoEntry(PhotoEntry photoEntry, boolean z, boolean z2) {
        int i = 0;
        this.pressed = false;
        this.photoEntry = photoEntry;
        this.isLast = z2;
        if (this.photoEntry.isVideo != null) {
            this.imageView.setOrientation(0, true);
            this.videoInfoContainer.setVisibility(0);
            int i2 = this.photoEntry.duration - ((this.photoEntry.duration / 60) * 60);
            this.videoTextView.setText(String.format("%d:%02d", new Object[]{Integer.valueOf(photoEntry), Integer.valueOf(i2)}));
        } else {
            this.videoInfoContainer.setVisibility(4);
        }
        if (this.photoEntry.thumbPath != null) {
            this.imageView.setImage(this.photoEntry.thumbPath, null, getResources().getDrawable(C0446R.drawable.nophotos));
        } else if (this.photoEntry.path == null) {
            this.imageView.setImageResource(C0446R.drawable.nophotos);
        } else if (this.photoEntry.isVideo != null) {
            photoEntry = this.imageView;
            r3 = new StringBuilder();
            r3.append("vthumb://");
            r3.append(this.photoEntry.imageId);
            r3.append(":");
            r3.append(this.photoEntry.path);
            photoEntry.setImage(r3.toString(), null, getResources().getDrawable(C0446R.drawable.nophotos));
        } else {
            this.imageView.setOrientation(this.photoEntry.orientation, true);
            photoEntry = this.imageView;
            r3 = new StringBuilder();
            r3.append("thumb://");
            r3.append(this.photoEntry.imageId);
            r3.append(":");
            r3.append(this.photoEntry.path);
            photoEntry.setImage(r3.toString(), null, getResources().getDrawable(C0446R.drawable.nophotos));
        }
        if (z && PhotoViewer.isShowingImage(this.photoEntry.path) != null) {
            i = 1;
        }
        this.imageView.getImageReceiver().setVisible(i ^ 1, true);
        z = true;
        this.checkBox.setAlpha(i != 0 ? 0.0f : 1.0f);
        photoEntry = this.videoInfoContainer;
        if (i != 0) {
            z = false;
        }
        photoEntry.setAlpha(z);
        requestLayout();
    }

    public void setChecked(int i, boolean z, boolean z2) {
        this.checkBox.setChecked(i, z, z2);
    }

    public void setNum(int i) {
        this.checkBox.setNum(i);
    }

    public void setOnCheckClickLisnener(OnClickListener onClickListener) {
        this.checkFrame.setOnClickListener(onClickListener);
    }

    public void setDelegate(PhotoAttachPhotoCellDelegate photoAttachPhotoCellDelegate) {
        this.delegate = photoAttachPhotoCellDelegate;
    }

    public void callDelegate() {
        this.delegate.onCheckClick(this);
    }

    public void showImage() {
        this.imageView.getImageReceiver().setVisible(true, true);
    }

    public void showCheck(boolean z) {
        float f = 1.0f;
        if (!(z && this.checkBox.getAlpha() == 1.0f) && (z || this.checkBox.getAlpha() != 0.0f)) {
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
            fArr[0] = z ? 1.0f : 0.0f;
            animatorArr[0] = ObjectAnimator.ofFloat(frameLayout, str, fArr);
            CheckBox checkBox = this.checkBox;
            str = "alpha";
            fArr = new float[1];
            if (!z) {
                f = 0.0f;
            }
            fArr[0] = f;
            animatorArr[1] = ObjectAnimator.ofFloat(checkBox, str, fArr);
            animatorSet.playTogether(animatorArr);
            this.animatorSet.addListener(new C08831());
            this.animatorSet.start();
        }
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        this.checkFrame.getHitRect(rect);
        boolean z = true;
        if (motionEvent.getAction() == 0) {
            if (rect.contains((int) motionEvent.getX(), (int) motionEvent.getY())) {
                this.pressed = true;
                invalidate();
                return z ? super.onTouchEvent(motionEvent) : z;
            }
        } else if (this.pressed) {
            if (motionEvent.getAction() == 1) {
                getParent().requestDisallowInterceptTouchEvent(true);
                this.pressed = false;
                playSoundEffect(0);
                this.delegate.onCheckClick(this);
                invalidate();
            } else if (motionEvent.getAction() == 3) {
                this.pressed = false;
                invalidate();
            } else if (motionEvent.getAction() == 2 && !rect.contains((int) motionEvent.getX(), (int) motionEvent.getY())) {
                this.pressed = false;
                invalidate();
            }
        }
        z = false;
        if (z) {
        }
    }
}
