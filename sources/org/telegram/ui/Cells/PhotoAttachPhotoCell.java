package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeInfo.AccessibilityAction;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
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
        this.videoInfoContainer.setBackgroundResource(NUM);
        this.videoInfoContainer.setPadding(AndroidUtilities.dp(3.0f), 0, AndroidUtilities.dp(3.0f), 0);
        addView(this.videoInfoContainer, LayoutHelper.createFrame(80, 16, 83));
        ImageView imageView = new ImageView(context);
        imageView.setImageResource(NUM);
        this.videoInfoContainer.addView(imageView, LayoutHelper.createFrame(-2, -2, 19));
        this.videoTextView = new TextView(context);
        this.videoTextView.setTextColor(-1);
        this.videoTextView.setTextSize(1, 12.0f);
        this.videoTextView.setImportantForAccessibility(2);
        this.videoInfoContainer.addView(this.videoTextView, LayoutHelper.createFrame(-2, -2.0f, 19, 18.0f, -0.7f, 0.0f, 0.0f));
        this.checkBox = new CheckBox(context, NUM);
        this.checkBox.setSize(30);
        this.checkBox.setCheckOffset(AndroidUtilities.dp(1.0f));
        this.checkBox.setDrawBackground(true);
        this.checkBox.setColor(-12793105, -1);
        addView(this.checkBox, LayoutHelper.createFrame(30, 30.0f, 51, 46.0f, 4.0f, 0.0f, 0.0f));
        this.checkBox.setVisibility(0);
        setFocusable(true);
    }

    public void setIsVertical(boolean z) {
        this.isVertical = z;
    }

    /* Access modifiers changed, original: protected */
    public void onMeasure(int i, int i2) {
        i2 = 0;
        if (this.isVertical) {
            i = MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(80.0f), NUM);
            if (!this.isLast) {
                i2 = 6;
            }
            super.onMeasure(i, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp((float) (i2 + 80)), NUM));
            return;
        }
        if (!this.isLast) {
            i2 = 6;
        }
        super.onMeasure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp((float) (i2 + 80)), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(80.0f), NUM));
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
        if (this.photoEntry.isVideo) {
            this.imageView.setOrientation(0, true);
            this.videoInfoContainer.setVisibility(0);
            int i2 = this.photoEntry.duration;
            i2 -= (i2 / 60) * 60;
            this.videoTextView.setText(String.format("%d:%02d", new Object[]{Integer.valueOf(r1), Integer.valueOf(i2)}));
        } else {
            this.videoInfoContainer.setVisibility(4);
        }
        photoEntry = this.photoEntry;
        String str = photoEntry.thumbPath;
        if (str != null) {
            this.imageView.setImage(str, null, getResources().getDrawable(NUM));
        } else if (photoEntry.path != null) {
            String str2 = ":";
            BackupImageView backupImageView;
            StringBuilder stringBuilder;
            if (photoEntry.isVideo) {
                backupImageView = this.imageView;
                stringBuilder = new StringBuilder();
                stringBuilder.append("vthumb://");
                stringBuilder.append(this.photoEntry.imageId);
                stringBuilder.append(str2);
                stringBuilder.append(this.photoEntry.path);
                backupImageView.setImage(stringBuilder.toString(), null, getResources().getDrawable(NUM));
            } else {
                this.imageView.setOrientation(photoEntry.orientation, true);
                backupImageView = this.imageView;
                stringBuilder = new StringBuilder();
                stringBuilder.append("thumb://");
                stringBuilder.append(this.photoEntry.imageId);
                stringBuilder.append(str2);
                stringBuilder.append(this.photoEntry.path);
                backupImageView.setImage(stringBuilder.toString(), null, getResources().getDrawable(NUM));
            }
        } else {
            this.imageView.setImageResource(NUM);
        }
        if (z && PhotoViewer.isShowingImage(this.photoEntry.path)) {
            i = 1;
        }
        this.imageView.getImageReceiver().setVisible(i ^ 1, true);
        float f = 0.0f;
        this.checkBox.setAlpha(i != 0 ? 0.0f : 1.0f);
        FrameLayout frameLayout = this.videoInfoContainer;
        if (i == 0) {
            f = 1.0f;
        }
        frameLayout.setAlpha(f);
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
            AnimatorSet animatorSet = this.animatorSet;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.animatorSet = null;
            }
            this.animatorSet = new AnimatorSet();
            this.animatorSet.setInterpolator(new DecelerateInterpolator());
            this.animatorSet.setDuration(180);
            animatorSet = this.animatorSet;
            Animator[] animatorArr = new Animator[2];
            FrameLayout frameLayout = this.videoInfoContainer;
            float[] fArr = new float[1];
            fArr[0] = z ? 1.0f : 0.0f;
            String str = "alpha";
            animatorArr[0] = ObjectAnimator.ofFloat(frameLayout, str, fArr);
            CheckBox checkBox = this.checkBox;
            fArr = new float[1];
            if (!z) {
                f = 0.0f;
            }
            fArr[0] = f;
            animatorArr[1] = ObjectAnimator.ofFloat(checkBox, str, fArr);
            animatorSet.playTogether(animatorArr);
            this.animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    if (animator.equals(PhotoAttachPhotoCell.this.animatorSet)) {
                        PhotoAttachPhotoCell.this.animatorSet = null;
                    }
                }
            });
            this.animatorSet.start();
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:22:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x0077  */
    public boolean onTouchEvent(android.view.MotionEvent r6) {
        /*
        r5 = this;
        r0 = r5.checkFrame;
        r1 = rect;
        r0.getHitRect(r1);
        r0 = r6.getAction();
        r1 = 1;
        r2 = 0;
        if (r0 != 0) goto L_0x0027;
    L_0x000f:
        r0 = rect;
        r3 = r6.getX();
        r3 = (int) r3;
        r4 = r6.getY();
        r4 = (int) r4;
        r0 = r0.contains(r3, r4);
        if (r0 == 0) goto L_0x0074;
    L_0x0021:
        r5.pressed = r1;
        r5.invalidate();
        goto L_0x0075;
    L_0x0027:
        r0 = r5.pressed;
        if (r0 == 0) goto L_0x0074;
    L_0x002b:
        r0 = r6.getAction();
        if (r0 != r1) goto L_0x0049;
    L_0x0031:
        r0 = r5.getParent();
        r0.requestDisallowInterceptTouchEvent(r1);
        r5.pressed = r2;
        r5.playSoundEffect(r2);
        r5.sendAccessibilityEvent(r1);
        r0 = r5.delegate;
        r0.onCheckClick(r5);
        r5.invalidate();
        goto L_0x0074;
    L_0x0049:
        r0 = r6.getAction();
        r1 = 3;
        if (r0 != r1) goto L_0x0056;
    L_0x0050:
        r5.pressed = r2;
        r5.invalidate();
        goto L_0x0074;
    L_0x0056:
        r0 = r6.getAction();
        r1 = 2;
        if (r0 != r1) goto L_0x0074;
    L_0x005d:
        r0 = rect;
        r1 = r6.getX();
        r1 = (int) r1;
        r3 = r6.getY();
        r3 = (int) r3;
        r0 = r0.contains(r1, r3);
        if (r0 != 0) goto L_0x0074;
    L_0x006f:
        r5.pressed = r2;
        r5.invalidate();
    L_0x0074:
        r1 = 0;
    L_0x0075:
        if (r1 != 0) goto L_0x007b;
    L_0x0077:
        r1 = super.onTouchEvent(r6);
    L_0x007b:
        return r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.PhotoAttachPhotoCell.onTouchEvent(android.view.MotionEvent):boolean");
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setEnabled(true);
        if (this.photoEntry.isVideo) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(LocaleController.getString("AttachVideo", NUM));
            stringBuilder.append(", ");
            stringBuilder.append(LocaleController.formatCallDuration(this.photoEntry.duration));
            accessibilityNodeInfo.setText(stringBuilder.toString());
        } else {
            accessibilityNodeInfo.setText(LocaleController.getString("AttachPhoto", NUM));
        }
        if (this.checkBox.isChecked()) {
            accessibilityNodeInfo.setSelected(true);
        }
        if (VERSION.SDK_INT >= 21) {
            accessibilityNodeInfo.addAction(new AccessibilityAction(NUM, LocaleController.getString("Open", NUM)));
        }
    }

    public boolean performAccessibilityAction(int i, Bundle bundle) {
        if (i == NUM) {
            View view = (View) getParent();
            view.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), 0, (float) getLeft(), (float) ((getTop() + getHeight()) - 1), 0));
            view.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), 1, (float) getLeft(), (float) ((getTop() + getHeight()) - 1), 0));
        }
        return super.performAccessibilityAction(i, bundle);
    }
}
