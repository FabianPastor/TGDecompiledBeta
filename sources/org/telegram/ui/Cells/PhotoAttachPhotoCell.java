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
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.TLRPC;
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
    private boolean needCheckShow;
    private MediaController.PhotoEntry photoEntry;
    private boolean pressed;
    private final Theme.ResourcesProvider resourcesProvider;
    private MediaController.SearchImage searchEntry;
    private FrameLayout videoInfoContainer;
    private TextView videoTextView;
    private boolean zoomOnSelect = true;

    public interface PhotoAttachPhotoCellDelegate {
        void onCheckClick(PhotoAttachPhotoCell photoAttachPhotoCell);
    }

    public PhotoAttachPhotoCell(Context context, Theme.ResourcesProvider resourcesProvider2) {
        super(context);
        this.resourcesProvider = resourcesProvider2;
        setWillNotDraw(false);
        FrameLayout frameLayout = new FrameLayout(context);
        this.container = frameLayout;
        addView(frameLayout, LayoutHelper.createFrame(80, 80.0f));
        BackupImageView backupImageView = new BackupImageView(context);
        this.imageView = backupImageView;
        this.container.addView(backupImageView, LayoutHelper.createFrame(-1, -1.0f));
        AnonymousClass1 r2 = new FrameLayout(context) {
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
        ImageView imageView1 = new ImageView(context);
        imageView1.setImageResource(NUM);
        this.videoInfoContainer.addView(imageView1, LayoutHelper.createFrame(-2, -2, 19));
        TextView textView = new TextView(context);
        this.videoTextView = textView;
        textView.setTextColor(-1);
        this.videoTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.videoTextView.setTextSize(1, 12.0f);
        this.videoTextView.setImportantForAccessibility(2);
        this.videoInfoContainer.addView(this.videoTextView, LayoutHelper.createFrame(-2, -2.0f, 19, 13.0f, -0.7f, 0.0f, 0.0f));
        CheckBox2 checkBox2 = new CheckBox2(context, 24, resourcesProvider2);
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

    public void setIsVertical(boolean value) {
        this.isVertical = value;
    }

    public void setItemSize(int size) {
        this.itemSize = size;
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.container.getLayoutParams();
        int i = this.itemSize;
        layoutParams.height = i;
        layoutParams.width = i;
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
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (this.itemSizeChanged) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(this.itemSize, NUM), View.MeasureSpec.makeMeasureSpec(this.itemSize + AndroidUtilities.dp(5.0f), NUM));
            return;
        }
        int i = 0;
        if (this.isVertical) {
            int makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(80.0f), NUM);
            if (!this.isLast) {
                i = 6;
            }
            super.onMeasure(makeMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp((float) (i + 80)), NUM));
            return;
        }
        if (!this.isLast) {
            i = 6;
        }
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp((float) (i + 80)), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(80.0f), NUM));
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

    public void setPhotoEntry(MediaController.PhotoEntry entry, boolean needCheckShow2, boolean last) {
        boolean z = false;
        this.pressed = false;
        this.photoEntry = entry;
        this.isLast = last;
        if (entry.isVideo) {
            this.imageView.setOrientation(0, true);
            this.videoInfoContainer.setVisibility(0);
            this.videoTextView.setText(AndroidUtilities.formatShortDuration(this.photoEntry.duration));
        } else {
            this.videoInfoContainer.setVisibility(4);
        }
        if (this.photoEntry.thumbPath != null) {
            this.imageView.setImage(this.photoEntry.thumbPath, (String) null, Theme.chat_attachEmptyDrawable);
        } else if (this.photoEntry.path == null) {
            this.imageView.setImageDrawable(Theme.chat_attachEmptyDrawable);
        } else if (this.photoEntry.isVideo) {
            BackupImageView backupImageView = this.imageView;
            backupImageView.setImage("vthumb://" + this.photoEntry.imageId + ":" + this.photoEntry.path, (String) null, Theme.chat_attachEmptyDrawable);
        } else {
            this.imageView.setOrientation(this.photoEntry.orientation, true);
            BackupImageView backupImageView2 = this.imageView;
            backupImageView2.setImage("thumb://" + this.photoEntry.imageId + ":" + this.photoEntry.path, (String) null, Theme.chat_attachEmptyDrawable);
        }
        boolean showing = needCheckShow2 && PhotoViewer.isShowingImage(this.photoEntry.path);
        ImageReceiver imageReceiver = this.imageView.getImageReceiver();
        if (!showing) {
            z = true;
        }
        imageReceiver.setVisible(z, true);
        float f = 0.0f;
        this.checkBox.setAlpha(showing ? 0.0f : 1.0f);
        FrameLayout frameLayout = this.videoInfoContainer;
        if (!showing) {
            f = 1.0f;
        }
        frameLayout.setAlpha(f);
        requestLayout();
    }

    public void setPhotoEntry(MediaController.SearchImage searchImage, boolean needCheckShow2, boolean last) {
        MediaController.SearchImage searchImage2 = searchImage;
        boolean z = false;
        this.pressed = false;
        this.searchEntry = searchImage2;
        this.isLast = last;
        Drawable thumb = this.zoomOnSelect ? Theme.chat_attachEmptyDrawable : getResources().getDrawable(NUM);
        if (searchImage2.thumbPhotoSize != null) {
            this.imageView.setImage(ImageLocation.getForPhoto(searchImage2.thumbPhotoSize, searchImage2.photo), (String) null, thumb, (Object) searchImage2);
        } else if (searchImage2.photoSize != null) {
            this.imageView.setImage(ImageLocation.getForPhoto(searchImage2.photoSize, searchImage2.photo), "80_80", thumb, (Object) searchImage2);
        } else if (searchImage2.thumbPath != null) {
            this.imageView.setImage(searchImage2.thumbPath, (String) null, thumb);
        } else if (!TextUtils.isEmpty(searchImage2.thumbUrl)) {
            ImageLocation location = ImageLocation.getForPath(searchImage2.thumbUrl);
            if (searchImage2.type == 1 && searchImage2.thumbUrl.endsWith("mp4")) {
                location.imageType = 2;
            }
            this.imageView.setImage(location, (String) null, thumb, (Object) searchImage2);
        } else if (searchImage2.document != null) {
            MessageObject.getDocumentVideoThumb(searchImage2.document);
            TLRPC.VideoSize videoSize = MessageObject.getDocumentVideoThumb(searchImage2.document);
            if (videoSize != null) {
                TLRPC.PhotoSize currentPhotoObject = FileLoader.getClosestPhotoSizeWithSize(searchImage2.document.thumbs, 90);
                TLRPC.PhotoSize photoSize = currentPhotoObject;
                TLRPC.VideoSize videoSize2 = videoSize;
                this.imageView.setImage(ImageLocation.getForDocument(videoSize, searchImage2.document), (String) null, ImageLocation.getForDocument(currentPhotoObject, searchImage2.document), "52_52", (String) null, -1, 1, searchImage);
            } else {
                this.imageView.setImage(ImageLocation.getForDocument(FileLoader.getClosestPhotoSizeWithSize(searchImage2.document.thumbs, 320), searchImage2.document), (String) null, thumb, (Object) searchImage2);
            }
        } else {
            this.imageView.setImageDrawable(thumb);
        }
        boolean showing = needCheckShow2 && PhotoViewer.isShowingImage(searchImage.getPathToAttach());
        ImageReceiver imageReceiver = this.imageView.getImageReceiver();
        if (!showing) {
            z = true;
        }
        imageReceiver.setVisible(z, true);
        float f = 0.0f;
        this.checkBox.setAlpha(showing ? 0.0f : 1.0f);
        FrameLayout frameLayout = this.videoInfoContainer;
        if (!showing) {
            f = 1.0f;
        }
        frameLayout.setAlpha(f);
        requestLayout();
    }

    public boolean isChecked() {
        return this.checkBox.isChecked();
    }

    public void setChecked(int num, final boolean checked, boolean animated) {
        this.checkBox.setChecked(num, checked, animated);
        if (this.itemSizeChanged) {
            AnimatorSet animatorSet2 = this.animator;
            if (animatorSet2 != null) {
                animatorSet2.cancel();
                this.animator = null;
            }
            float f = 0.787f;
            if (animated) {
                AnimatorSet animatorSet3 = new AnimatorSet();
                this.animator = animatorSet3;
                Animator[] animatorArr = new Animator[2];
                FrameLayout frameLayout = this.container;
                Property property = View.SCALE_X;
                float[] fArr = new float[1];
                fArr[0] = checked ? 0.787f : 1.0f;
                animatorArr[0] = ObjectAnimator.ofFloat(frameLayout, property, fArr);
                FrameLayout frameLayout2 = this.container;
                Property property2 = View.SCALE_Y;
                float[] fArr2 = new float[1];
                if (!checked) {
                    f = 1.0f;
                }
                fArr2[0] = f;
                animatorArr[1] = ObjectAnimator.ofFloat(frameLayout2, property2, fArr2);
                animatorSet3.playTogether(animatorArr);
                this.animator.setDuration(200);
                this.animator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        if (PhotoAttachPhotoCell.this.animator != null && PhotoAttachPhotoCell.this.animator.equals(animation)) {
                            AnimatorSet unused = PhotoAttachPhotoCell.this.animator = null;
                            if (!checked) {
                                PhotoAttachPhotoCell.this.setBackgroundColor(0);
                            }
                        }
                    }

                    public void onAnimationCancel(Animator animation) {
                        if (PhotoAttachPhotoCell.this.animator != null && PhotoAttachPhotoCell.this.animator.equals(animation)) {
                            AnimatorSet unused = PhotoAttachPhotoCell.this.animator = null;
                        }
                    }
                });
                this.animator.start();
                return;
            }
            this.container.setScaleX(checked ? 0.787f : 1.0f);
            FrameLayout frameLayout3 = this.container;
            if (!checked) {
                f = 1.0f;
            }
            frameLayout3.setScaleY(f);
        }
    }

    public void setNum(int num) {
        this.checkBox.setNum(num);
    }

    public void setOnCheckClickLisnener(View.OnClickListener onCheckClickLisnener) {
        this.checkFrame.setOnClickListener(onCheckClickLisnener);
    }

    public void setDelegate(PhotoAttachPhotoCellDelegate delegate2) {
        this.delegate = delegate2;
    }

    public void callDelegate() {
        this.delegate.onCheckClick(this);
    }

    public void showImage() {
        this.imageView.getImageReceiver().setVisible(true, true);
    }

    public void showCheck(boolean show) {
        float f = 1.0f;
        if (show && this.checkBox.getAlpha() == 1.0f) {
            return;
        }
        if (show || this.checkBox.getAlpha() != 0.0f) {
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
            fArr[0] = show ? 1.0f : 0.0f;
            animatorArr[0] = ObjectAnimator.ofFloat(frameLayout, property, fArr);
            CheckBox2 checkBox2 = this.checkBox;
            Property property2 = View.ALPHA;
            float[] fArr2 = new float[1];
            if (!show) {
                f = 0.0f;
            }
            fArr2[0] = f;
            animatorArr[1] = ObjectAnimator.ofFloat(checkBox2, property2, fArr2);
            animatorSet4.playTogether(animatorArr);
            this.animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    if (animation.equals(PhotoAttachPhotoCell.this.animatorSet)) {
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

    public boolean onTouchEvent(MotionEvent event) {
        boolean result = false;
        this.checkFrame.getHitRect(rect);
        if (event.getAction() == 0) {
            if (rect.contains((int) event.getX(), (int) event.getY())) {
                this.pressed = true;
                invalidate();
                result = true;
            }
        } else if (this.pressed) {
            if (event.getAction() == 1) {
                getParent().requestDisallowInterceptTouchEvent(true);
                this.pressed = false;
                playSoundEffect(0);
                sendAccessibilityEvent(1);
                this.delegate.onCheckClick(this);
                invalidate();
            } else if (event.getAction() == 3) {
                this.pressed = false;
                invalidate();
            } else if (event.getAction() == 2 && !rect.contains((int) event.getX(), (int) event.getY())) {
                this.pressed = false;
                invalidate();
            }
        }
        if (!result) {
            return super.onTouchEvent(event);
        }
        return result;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        MediaController.PhotoEntry photoEntry2;
        MediaController.SearchImage searchImage;
        if (this.checkBox.isChecked() || this.container.getScaleX() != 1.0f || !this.imageView.getImageReceiver().hasNotThumb() || this.imageView.getImageReceiver().getCurrentAlpha() != 1.0f || (((photoEntry2 = this.photoEntry) != null && PhotoViewer.isShowingImage(photoEntry2.path)) || ((searchImage = this.searchEntry) != null && PhotoViewer.isShowingImage(searchImage.getPathToAttach())))) {
            this.backgroundPaint.setColor(getThemedColor("chat_attachPhotoBackground"));
            canvas.drawRect(0.0f, 0.0f, (float) this.imageView.getMeasuredWidth(), (float) this.imageView.getMeasuredHeight(), this.backgroundPaint);
        }
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setEnabled(true);
        MediaController.PhotoEntry photoEntry2 = this.photoEntry;
        if (photoEntry2 == null || !photoEntry2.isVideo) {
            info.setText(LocaleController.getString("AttachPhoto", NUM));
        } else {
            info.setText(LocaleController.getString("AttachVideo", NUM) + ", " + LocaleController.formatDuration(this.photoEntry.duration));
        }
        if (this.checkBox.isChecked()) {
            info.setSelected(true);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            info.addAction(new AccessibilityNodeInfo.AccessibilityAction(NUM, LocaleController.getString("Open", NUM)));
        }
    }

    public boolean performAccessibilityAction(int action, Bundle arguments) {
        if (action == NUM) {
            View parent = (View) getParent();
            parent.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), 0, (float) getLeft(), (float) ((getTop() + getHeight()) - 1), 0));
            parent.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), 1, (float) getLeft(), (float) ((getTop() + getHeight()) - 1), 0));
        }
        return super.performAccessibilityAction(action, arguments);
    }

    /* access modifiers changed from: protected */
    public int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }
}
