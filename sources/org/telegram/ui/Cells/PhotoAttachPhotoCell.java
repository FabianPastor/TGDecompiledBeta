package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Property;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$PhotoSize;
import org.telegram.tgnet.TLRPC$TL_videoSize;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CheckBox2;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.PhotoViewer;

public class PhotoAttachPhotoCell extends FrameLayout {
    private static Rect rect = new Rect();
    /* access modifiers changed from: private */
    public AnimatorSet animator;
    /* access modifiers changed from: private */
    public AnimatorSet animatorSet;
    private Paint backgroundPaint = new Paint();
    private CheckBox2 checkBox;
    private FrameLayout checkFrame;
    private FrameLayout container;
    private PhotoAttachPhotoCellDelegate delegate;
    private BackupImageView imageView;
    private boolean isLast;
    private boolean isVertical;
    private int itemSize;
    private boolean itemSizeChanged;
    private MediaController.PhotoEntry photoEntry;
    private boolean pressed;
    private MediaController.SearchImage searchEntry;
    private FrameLayout videoInfoContainer;
    private TextView videoTextView;
    private boolean zoomOnSelect = true;

    public interface PhotoAttachPhotoCellDelegate {
        void onCheckClick(PhotoAttachPhotoCell photoAttachPhotoCell);
    }

    public PhotoAttachPhotoCell(Context context) {
        super(context);
        setWillNotDraw(false);
        FrameLayout frameLayout = new FrameLayout(context);
        this.container = frameLayout;
        addView(frameLayout, LayoutHelper.createFrame(80, 80.0f));
        BackupImageView backupImageView = new BackupImageView(context);
        this.imageView = backupImageView;
        this.container.addView(backupImageView, LayoutHelper.createFrame(-1, -1.0f));
        AnonymousClass1 r2 = new FrameLayout(this, context) {
            private RectF rect = new RectF();

            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                this.rect.set(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight());
                canvas.drawRoundRect(this.rect, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), Theme.chat_timeBackgroundPaint);
            }
        };
        this.videoInfoContainer = r2;
        r2.setWillNotDraw(false);
        this.videoInfoContainer.setPadding(AndroidUtilities.dp(5.0f), 0, AndroidUtilities.dp(5.0f), 0);
        this.container.addView(this.videoInfoContainer, LayoutHelper.createFrame(-2, 17.0f, 83, 4.0f, 0.0f, 0.0f, 4.0f));
        ImageView imageView2 = new ImageView(context);
        imageView2.setImageResource(NUM);
        this.videoInfoContainer.addView(imageView2, LayoutHelper.createFrame(-2, -2, 19));
        TextView textView = new TextView(context);
        this.videoTextView = textView;
        textView.setTextColor(-1);
        this.videoTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.videoTextView.setTextSize(1, 12.0f);
        this.videoTextView.setImportantForAccessibility(2);
        this.videoInfoContainer.addView(this.videoTextView, LayoutHelper.createFrame(-2, -2.0f, 19, 13.0f, -0.7f, 0.0f, 0.0f));
        CheckBox2 checkBox2 = new CheckBox2(context, 24);
        this.checkBox = checkBox2;
        checkBox2.setDrawBackgroundAsArc(7);
        this.checkBox.setColor("chat_attachCheckBoxBackground", "chat_attachPhotoBackground", "chat_attachCheckBoxCheck");
        addView(this.checkBox, LayoutHelper.createFrame(26, 26.0f, 51, 52.0f, 4.0f, 0.0f, 0.0f));
        this.checkBox.setVisibility(0);
        setFocusable(true);
        FrameLayout frameLayout2 = new FrameLayout(context);
        this.checkFrame = frameLayout2;
        addView(frameLayout2, LayoutHelper.createFrame(42, 42.0f, 51, 38.0f, 0.0f, 0.0f, 0.0f));
        this.itemSize = AndroidUtilities.dp(80.0f);
    }

    public void setIsVertical(boolean z) {
        this.isVertical = z;
    }

    public void setItemSize(int i) {
        this.itemSize = i;
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.container.getLayoutParams();
        int i2 = this.itemSize;
        layoutParams.height = i2;
        layoutParams.width = i2;
        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.checkFrame.getLayoutParams();
        layoutParams2.gravity = 53;
        layoutParams2.leftMargin = 0;
        FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) this.checkBox.getLayoutParams();
        layoutParams3.gravity = 53;
        layoutParams3.leftMargin = 0;
        int dp = AndroidUtilities.dp(5.0f);
        layoutParams3.topMargin = dp;
        layoutParams3.rightMargin = dp;
        this.checkBox.setDrawBackgroundAsArc(6);
        this.itemSizeChanged = true;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        if (this.itemSizeChanged) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(this.itemSize, NUM), View.MeasureSpec.makeMeasureSpec(this.itemSize + AndroidUtilities.dp(5.0f), NUM));
            return;
        }
        int i3 = 0;
        if (this.isVertical) {
            int makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(80.0f), NUM);
            if (!this.isLast) {
                i3 = 6;
            }
            super.onMeasure(makeMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp((float) (i3 + 80)), NUM));
            return;
        }
        if (!this.isLast) {
            i3 = 6;
        }
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp((float) (i3 + 80)), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(80.0f), NUM));
    }

    public MediaController.PhotoEntry getPhotoEntry() {
        return this.photoEntry;
    }

    public BackupImageView getImageView() {
        return this.imageView;
    }

    public float getScale() {
        return this.container.getScaleX();
    }

    public CheckBox2 getCheckBox() {
        return this.checkBox;
    }

    public FrameLayout getCheckFrame() {
        return this.checkFrame;
    }

    public View getVideoInfoContainer() {
        return this.videoInfoContainer;
    }

    public void setPhotoEntry(MediaController.PhotoEntry photoEntry2, boolean z, boolean z2) {
        boolean z3 = false;
        this.pressed = false;
        this.photoEntry = photoEntry2;
        this.isLast = z2;
        if (photoEntry2.isVideo) {
            this.imageView.setOrientation(0, true);
            this.videoInfoContainer.setVisibility(0);
            this.videoTextView.setText(AndroidUtilities.formatShortDuration(this.photoEntry.duration));
        } else {
            this.videoInfoContainer.setVisibility(4);
        }
        MediaController.PhotoEntry photoEntry3 = this.photoEntry;
        String str = photoEntry3.thumbPath;
        if (str != null) {
            this.imageView.setImage(str, (String) null, Theme.chat_attachEmptyDrawable);
        } else if (photoEntry3.path == null) {
            this.imageView.setImageDrawable(Theme.chat_attachEmptyDrawable);
        } else if (photoEntry3.isVideo) {
            BackupImageView backupImageView = this.imageView;
            backupImageView.setImage("vthumb://" + this.photoEntry.imageId + ":" + this.photoEntry.path, (String) null, Theme.chat_attachEmptyDrawable);
        } else {
            this.imageView.setOrientation(photoEntry3.orientation, true);
            BackupImageView backupImageView2 = this.imageView;
            backupImageView2.setImage("thumb://" + this.photoEntry.imageId + ":" + this.photoEntry.path, (String) null, Theme.chat_attachEmptyDrawable);
        }
        if (z && PhotoViewer.isShowingImage(this.photoEntry.path)) {
            z3 = true;
        }
        this.imageView.getImageReceiver().setVisible(!z3, true);
        float f = 0.0f;
        this.checkBox.setAlpha(z3 ? 0.0f : 1.0f);
        FrameLayout frameLayout = this.videoInfoContainer;
        if (!z3) {
            f = 1.0f;
        }
        frameLayout.setAlpha(f);
        requestLayout();
    }

    public void setPhotoEntry(MediaController.SearchImage searchImage, boolean z, boolean z2) {
        boolean z3 = false;
        this.pressed = false;
        this.searchEntry = searchImage;
        this.isLast = z2;
        Drawable drawable = this.zoomOnSelect ? Theme.chat_attachEmptyDrawable : getResources().getDrawable(NUM);
        TLRPC$PhotoSize tLRPC$PhotoSize = searchImage.thumbPhotoSize;
        if (tLRPC$PhotoSize != null) {
            this.imageView.setImage(ImageLocation.getForPhoto(tLRPC$PhotoSize, searchImage.photo), (String) null, drawable, (Object) searchImage);
        } else {
            TLRPC$PhotoSize tLRPC$PhotoSize2 = searchImage.photoSize;
            if (tLRPC$PhotoSize2 != null) {
                this.imageView.setImage(ImageLocation.getForPhoto(tLRPC$PhotoSize2, searchImage.photo), "80_80", drawable, (Object) searchImage);
            } else {
                String str = searchImage.thumbPath;
                if (str != null) {
                    this.imageView.setImage(str, (String) null, drawable);
                } else if (!TextUtils.isEmpty(searchImage.thumbUrl)) {
                    ImageLocation forPath = ImageLocation.getForPath(searchImage.thumbUrl);
                    if (searchImage.type == 1 && searchImage.thumbUrl.endsWith("mp4")) {
                        forPath.imageType = 2;
                    }
                    this.imageView.setImage(forPath, (String) null, drawable, (Object) searchImage);
                } else {
                    TLRPC$Document tLRPC$Document = searchImage.document;
                    if (tLRPC$Document != null) {
                        MessageObject.getDocumentVideoThumb(tLRPC$Document);
                        TLRPC$TL_videoSize documentVideoThumb = MessageObject.getDocumentVideoThumb(searchImage.document);
                        if (documentVideoThumb != null) {
                            this.imageView.setImage(ImageLocation.getForDocument(documentVideoThumb, searchImage.document), (String) null, ImageLocation.getForDocument(FileLoader.getClosestPhotoSizeWithSize(searchImage.document.thumbs, 90), searchImage.document), "52_52", (String) null, -1, 1, searchImage);
                        } else {
                            this.imageView.setImage(ImageLocation.getForDocument(FileLoader.getClosestPhotoSizeWithSize(searchImage.document.thumbs, 320), searchImage.document), (String) null, drawable, (Object) searchImage);
                        }
                    } else {
                        this.imageView.setImageDrawable(drawable);
                    }
                }
            }
        }
        if (z && PhotoViewer.isShowingImage(searchImage.getPathToAttach())) {
            z3 = true;
        }
        this.imageView.getImageReceiver().setVisible(!z3, true);
        float f = 0.0f;
        this.checkBox.setAlpha(z3 ? 0.0f : 1.0f);
        FrameLayout frameLayout = this.videoInfoContainer;
        if (!z3) {
            f = 1.0f;
        }
        frameLayout.setAlpha(f);
        requestLayout();
    }

    public boolean isChecked() {
        return this.checkBox.isChecked();
    }

    public void setChecked(int i, final boolean z, boolean z2) {
        this.checkBox.setChecked(i, z, z2);
        if (this.itemSizeChanged) {
            AnimatorSet animatorSet2 = this.animator;
            if (animatorSet2 != null) {
                animatorSet2.cancel();
                this.animator = null;
            }
            float f = 0.787f;
            if (z2) {
                AnimatorSet animatorSet3 = new AnimatorSet();
                this.animator = animatorSet3;
                Animator[] animatorArr = new Animator[2];
                FrameLayout frameLayout = this.container;
                Property property = View.SCALE_X;
                float[] fArr = new float[1];
                fArr[0] = z ? 0.787f : 1.0f;
                animatorArr[0] = ObjectAnimator.ofFloat(frameLayout, property, fArr);
                FrameLayout frameLayout2 = this.container;
                Property property2 = View.SCALE_Y;
                float[] fArr2 = new float[1];
                if (!z) {
                    f = 1.0f;
                }
                fArr2[0] = f;
                animatorArr[1] = ObjectAnimator.ofFloat(frameLayout2, property2, fArr2);
                animatorSet3.playTogether(animatorArr);
                this.animator.setDuration(200);
                this.animator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        if (PhotoAttachPhotoCell.this.animator != null && PhotoAttachPhotoCell.this.animator.equals(animator)) {
                            AnimatorSet unused = PhotoAttachPhotoCell.this.animator = null;
                            if (!z) {
                                PhotoAttachPhotoCell.this.setBackgroundColor(0);
                            }
                        }
                    }

                    public void onAnimationCancel(Animator animator) {
                        if (PhotoAttachPhotoCell.this.animator != null && PhotoAttachPhotoCell.this.animator.equals(animator)) {
                            AnimatorSet unused = PhotoAttachPhotoCell.this.animator = null;
                        }
                    }
                });
                this.animator.start();
                return;
            }
            this.container.setScaleX(z ? 0.787f : 1.0f);
            FrameLayout frameLayout3 = this.container;
            if (!z) {
                f = 1.0f;
            }
            frameLayout3.setScaleY(f);
        }
    }

    public void setNum(int i) {
        this.checkBox.setNum(i);
    }

    public void setOnCheckClickLisnener(View.OnClickListener onClickListener) {
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
        if (z && this.checkBox.getAlpha() == 1.0f) {
            return;
        }
        if (z || this.checkBox.getAlpha() != 0.0f) {
            AnimatorSet animatorSet2 = this.animatorSet;
            if (animatorSet2 != null) {
                animatorSet2.cancel();
                this.animatorSet = null;
            }
            AnimatorSet animatorSet3 = new AnimatorSet();
            this.animatorSet = animatorSet3;
            animatorSet3.setInterpolator(new DecelerateInterpolator());
            this.animatorSet.setDuration(180);
            AnimatorSet animatorSet4 = this.animatorSet;
            Animator[] animatorArr = new Animator[2];
            FrameLayout frameLayout = this.videoInfoContainer;
            Property property = View.ALPHA;
            float[] fArr = new float[1];
            fArr[0] = z ? 1.0f : 0.0f;
            animatorArr[0] = ObjectAnimator.ofFloat(frameLayout, property, fArr);
            CheckBox2 checkBox2 = this.checkBox;
            Property property2 = View.ALPHA;
            float[] fArr2 = new float[1];
            if (!z) {
                f = 0.0f;
            }
            fArr2[0] = f;
            animatorArr[1] = ObjectAnimator.ofFloat(checkBox2, property2, fArr2);
            animatorSet4.playTogether(animatorArr);
            this.animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    if (animator.equals(PhotoAttachPhotoCell.this.animatorSet)) {
                        AnimatorSet unused = PhotoAttachPhotoCell.this.animatorSet = null;
                    }
                }
            });
            this.animatorSet.start();
        }
    }

    public void clearAnimation() {
        super.clearAnimation();
        AnimatorSet animatorSet2 = this.animator;
        if (animatorSet2 != null) {
            animatorSet2.cancel();
            this.animator = null;
            float f = 0.787f;
            this.container.setScaleX(this.checkBox.isChecked() ? 0.787f : 1.0f);
            FrameLayout frameLayout = this.container;
            if (!this.checkBox.isChecked()) {
                f = 1.0f;
            }
            frameLayout.setScaleY(f);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:20:0x0077  */
    /* JADX WARNING: Removed duplicated region for block: B:22:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onTouchEvent(android.view.MotionEvent r6) {
        /*
            r5 = this;
            android.widget.FrameLayout r0 = r5.checkFrame
            android.graphics.Rect r1 = rect
            r0.getHitRect(r1)
            int r0 = r6.getAction()
            r1 = 1
            r2 = 0
            if (r0 != 0) goto L_0x0027
            android.graphics.Rect r0 = rect
            float r3 = r6.getX()
            int r3 = (int) r3
            float r4 = r6.getY()
            int r4 = (int) r4
            boolean r0 = r0.contains(r3, r4)
            if (r0 == 0) goto L_0x0074
            r5.pressed = r1
            r5.invalidate()
            goto L_0x0075
        L_0x0027:
            boolean r0 = r5.pressed
            if (r0 == 0) goto L_0x0074
            int r0 = r6.getAction()
            if (r0 != r1) goto L_0x0049
            android.view.ViewParent r0 = r5.getParent()
            r0.requestDisallowInterceptTouchEvent(r1)
            r5.pressed = r2
            r5.playSoundEffect(r2)
            r5.sendAccessibilityEvent(r1)
            org.telegram.ui.Cells.PhotoAttachPhotoCell$PhotoAttachPhotoCellDelegate r0 = r5.delegate
            r0.onCheckClick(r5)
            r5.invalidate()
            goto L_0x0074
        L_0x0049:
            int r0 = r6.getAction()
            r1 = 3
            if (r0 != r1) goto L_0x0056
            r5.pressed = r2
            r5.invalidate()
            goto L_0x0074
        L_0x0056:
            int r0 = r6.getAction()
            r1 = 2
            if (r0 != r1) goto L_0x0074
            android.graphics.Rect r0 = rect
            float r1 = r6.getX()
            int r1 = (int) r1
            float r3 = r6.getY()
            int r3 = (int) r3
            boolean r0 = r0.contains(r1, r3)
            if (r0 != 0) goto L_0x0074
            r5.pressed = r2
            r5.invalidate()
        L_0x0074:
            r1 = 0
        L_0x0075:
            if (r1 != 0) goto L_0x007b
            boolean r1 = super.onTouchEvent(r6)
        L_0x007b:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.PhotoAttachPhotoCell.onTouchEvent(android.view.MotionEvent):boolean");
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        MediaController.PhotoEntry photoEntry2;
        MediaController.SearchImage searchImage;
        if (this.checkBox.isChecked() || this.container.getScaleX() != 1.0f || !this.imageView.getImageReceiver().hasNotThumb() || this.imageView.getImageReceiver().getCurrentAlpha() != 1.0f || (((photoEntry2 = this.photoEntry) != null && PhotoViewer.isShowingImage(photoEntry2.path)) || ((searchImage = this.searchEntry) != null && PhotoViewer.isShowingImage(searchImage.getPathToAttach())))) {
            this.backgroundPaint.setColor(Theme.getColor("chat_attachPhotoBackground"));
            canvas.drawRect(0.0f, 0.0f, (float) this.imageView.getMeasuredWidth(), (float) this.imageView.getMeasuredHeight(), this.backgroundPaint);
        }
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setEnabled(true);
        MediaController.PhotoEntry photoEntry2 = this.photoEntry;
        if (photoEntry2 == null || !photoEntry2.isVideo) {
            accessibilityNodeInfo.setText(LocaleController.getString("AttachPhoto", NUM));
        } else {
            accessibilityNodeInfo.setText(LocaleController.getString("AttachVideo", NUM) + ", " + LocaleController.formatCallDuration(this.photoEntry.duration));
        }
        if (this.checkBox.isChecked()) {
            accessibilityNodeInfo.setSelected(true);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            accessibilityNodeInfo.addAction(new AccessibilityNodeInfo.AccessibilityAction(NUM, LocaleController.getString("Open", NUM)));
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
