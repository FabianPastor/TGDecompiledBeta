package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Property;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.MediaController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.BubbleActivity;
import org.telegram.ui.Components.AnimationProperties;
import org.telegram.ui.Components.Crop.CropRotationWheel;
import org.telegram.ui.Components.Crop.CropTransform;
import org.telegram.ui.Components.Crop.CropView;

public class PhotoCropView extends FrameLayout {
    public final Property<PhotoCropView, Float> ANIMATION_VALUE = new AnimationProperties.FloatProperty<PhotoCropView>("thumbAnimationProgress") {
        public void setValue(PhotoCropView object, float value) {
            float unused = PhotoCropView.this.thumbAnimationProgress = value;
            object.invalidate();
        }

        public Float get(PhotoCropView object) {
            return Float.valueOf(PhotoCropView.this.thumbAnimationProgress);
        }
    };
    public final Property<PhotoCropView, Float> PROGRESS_VALUE = new AnimationProperties.FloatProperty<PhotoCropView>("thumbImageVisibleProgress") {
        public void setValue(PhotoCropView object, float value) {
            float unused = PhotoCropView.this.thumbImageVisibleProgress = value;
            object.invalidate();
        }

        public Float get(PhotoCropView object) {
            return Float.valueOf(PhotoCropView.this.thumbImageVisibleProgress);
        }
    };
    private Paint circlePaint = new Paint(1);
    /* access modifiers changed from: private */
    public CropView cropView;
    /* access modifiers changed from: private */
    public PhotoCropViewDelegate delegate;
    private float flashAlpha = 0.0f;
    private boolean inBubbleMode;
    private final Theme.ResourcesProvider resourcesProvider;
    /* access modifiers changed from: private */
    public AnimatorSet thumbAnimation;
    /* access modifiers changed from: private */
    public float thumbAnimationProgress = 1.0f;
    private ImageReceiver thumbImageView;
    private boolean thumbImageVisible;
    private boolean thumbImageVisibleOverride = true;
    /* access modifiers changed from: private */
    public float thumbImageVisibleProgress;
    /* access modifiers changed from: private */
    public AnimatorSet thumbOverrideAnimation;
    /* access modifiers changed from: private */
    public CropRotationWheel wheelView;

    public interface PhotoCropViewDelegate {
        int getVideoThumbX();

        void onChange(boolean z);

        void onTapUp();

        void onUpdate();

        void onVideoThumbClick();
    }

    public PhotoCropView(Context context, Theme.ResourcesProvider resourcesProvider2) {
        super(context);
        this.resourcesProvider = resourcesProvider2;
        this.inBubbleMode = context instanceof BubbleActivity;
        CropView cropView2 = new CropView(context);
        this.cropView = cropView2;
        cropView2.setListener(new CropView.CropViewListener() {
            public void onChange(boolean reset) {
                if (PhotoCropView.this.delegate != null) {
                    PhotoCropView.this.delegate.onChange(reset);
                }
            }

            public void onUpdate() {
                if (PhotoCropView.this.delegate != null) {
                    PhotoCropView.this.delegate.onUpdate();
                }
            }

            public void onAspectLock(boolean enabled) {
                PhotoCropView.this.wheelView.setAspectLock(enabled);
            }

            public void onTapUp() {
                if (PhotoCropView.this.delegate != null) {
                    PhotoCropView.this.delegate.onTapUp();
                }
            }
        });
        this.cropView.setBottomPadding((float) AndroidUtilities.dp(64.0f));
        addView(this.cropView);
        this.thumbImageView = new ImageReceiver(this);
        CropRotationWheel cropRotationWheel = new CropRotationWheel(context);
        this.wheelView = cropRotationWheel;
        cropRotationWheel.setListener(new CropRotationWheel.RotationWheelListener() {
            public void onStart() {
                PhotoCropView.this.cropView.onRotationBegan();
            }

            public void onChange(float angle) {
                PhotoCropView.this.cropView.setRotation(angle);
                if (PhotoCropView.this.delegate != null) {
                    PhotoCropView.this.delegate.onChange(false);
                }
            }

            public void onEnd(float angle) {
                PhotoCropView.this.cropView.onRotationEnded();
            }

            public void aspectRatioPressed() {
                PhotoCropView.this.cropView.showAspectRatioDialog();
            }

            public boolean rotate90Pressed() {
                return PhotoCropView.this.rotate();
            }

            public boolean mirror() {
                return PhotoCropView.this.mirror();
            }
        });
        addView(this.wheelView, LayoutHelper.createFrame(-1, -2.0f, 81, 0.0f, 0.0f, 0.0f, 0.0f));
    }

    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (!this.thumbImageVisibleOverride || !this.thumbImageVisible || !this.thumbImageView.isInsideImage(event.getX(), event.getY())) {
            return super.onInterceptTouchEvent(event);
        }
        if (event.getAction() == 1) {
            this.delegate.onVideoThumbClick();
        }
        return true;
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (!this.thumbImageVisibleOverride || !this.thumbImageVisible || !this.thumbImageView.isInsideImage(event.getX(), event.getY())) {
            return super.onTouchEvent(event);
        }
        if (event.getAction() == 1) {
            this.delegate.onVideoThumbClick();
        }
        return true;
    }

    /* access modifiers changed from: protected */
    public boolean drawChild(Canvas canvas, View child, long drawingTime) {
        Canvas canvas2 = canvas;
        boolean result = super.drawChild(canvas, child, drawingTime);
        if (this.thumbImageVisible) {
            CropView cropView2 = this.cropView;
            if (child == cropView2) {
                RectF rect = cropView2.getActualRect();
                int targetSize = AndroidUtilities.dp(32.0f);
                int targetX = (this.delegate.getVideoThumbX() - (targetSize / 2)) + AndroidUtilities.dp(2.0f);
                int targetY = getMeasuredHeight() - AndroidUtilities.dp(156.0f);
                float x = rect.left + ((((float) targetX) - rect.left) * this.thumbAnimationProgress);
                float y = rect.top + ((((float) targetY) - rect.top) * this.thumbAnimationProgress);
                float size = rect.width() + ((((float) targetSize) - rect.width()) * this.thumbAnimationProgress);
                this.thumbImageView.setRoundRadius((int) (size / 2.0f));
                this.thumbImageView.setImageCoords(x, y, size, size);
                this.thumbImageView.setAlpha(this.thumbImageVisibleProgress);
                this.thumbImageView.draw(canvas2);
                if (this.flashAlpha > 0.0f) {
                    this.circlePaint.setColor(-1);
                    this.circlePaint.setAlpha((int) (this.flashAlpha * 255.0f));
                    canvas2.drawCircle(rect.centerX(), rect.centerY(), rect.width() / 2.0f, this.circlePaint);
                }
                this.circlePaint.setColor(getThemedColor("dialogFloatingButton"));
                this.circlePaint.setAlpha(Math.min(255, (int) (this.thumbAnimationProgress * 255.0f * this.thumbImageVisibleProgress)));
                canvas2.drawCircle((float) ((targetSize / 2) + targetX), (float) (targetY + targetSize + AndroidUtilities.dp(8.0f)), (float) AndroidUtilities.dp(3.0f), this.circlePaint);
            }
        } else {
            View view = child;
        }
        return result;
    }

    public boolean rotate() {
        CropRotationWheel cropRotationWheel = this.wheelView;
        if (cropRotationWheel != null) {
            cropRotationWheel.reset(false);
        }
        return this.cropView.rotate90Degrees();
    }

    public boolean mirror() {
        return this.cropView.mirror();
    }

    public void setBitmap(Bitmap bitmap, int rotation, boolean freeform, boolean update, PaintingOverlay paintingOverlay, CropTransform cropTransform, VideoEditTextureView videoView, MediaController.CropState state) {
        boolean z = freeform;
        MediaController.CropState cropState = state;
        requestLayout();
        int i = 0;
        this.thumbImageVisible = false;
        this.thumbImageView.setImageBitmap((Drawable) null);
        this.cropView.setBitmap(bitmap, rotation, freeform, update, paintingOverlay, cropTransform, videoView, state);
        this.wheelView.setFreeform(z);
        boolean z2 = true;
        this.wheelView.reset(true);
        if (cropState != null) {
            this.wheelView.setRotation(cropState.cropRotate, false);
            CropRotationWheel cropRotationWheel = this.wheelView;
            if (cropState.transformRotation == 0) {
                z2 = false;
            }
            cropRotationWheel.setRotated(z2);
            this.wheelView.setMirrored(cropState.mirrored);
        } else {
            this.wheelView.setRotated(false);
            this.wheelView.setMirrored(false);
        }
        CropRotationWheel cropRotationWheel2 = this.wheelView;
        if (!z) {
            i = 4;
        }
        cropRotationWheel2.setVisibility(i);
    }

    public void setVideoThumbFlashAlpha(float alpha) {
        this.flashAlpha = alpha;
        invalidate();
    }

    public Bitmap getVideoThumb() {
        if (!this.thumbImageVisible || !this.thumbImageVisibleOverride) {
            return null;
        }
        return this.thumbImageView.getBitmap();
    }

    public void setVideoThumb(Bitmap bitmap, int orientation) {
        this.thumbImageVisible = bitmap != null;
        this.thumbImageView.setImageBitmap(bitmap);
        this.thumbImageView.setOrientation(orientation, false);
        AnimatorSet animatorSet = this.thumbAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        AnimatorSet animatorSet2 = this.thumbOverrideAnimation;
        if (animatorSet2 != null) {
            animatorSet2.cancel();
        }
        this.thumbImageVisibleOverride = true;
        this.thumbImageVisibleProgress = 1.0f;
        AnimatorSet animatorSet3 = new AnimatorSet();
        this.thumbAnimation = animatorSet3;
        animatorSet3.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, this.ANIMATION_VALUE, new float[]{0.0f, 1.0f})});
        this.thumbAnimation.setDuration(250);
        this.thumbAnimation.setInterpolator(new OvershootInterpolator(1.01f));
        this.thumbAnimation.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                AnimatorSet unused = PhotoCropView.this.thumbAnimation = null;
            }
        });
        this.thumbAnimation.start();
    }

    public void cancelThumbAnimation() {
        AnimatorSet animatorSet = this.thumbAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.thumbAnimation = null;
            this.thumbImageVisible = false;
        }
    }

    public void setVideoThumbVisible(boolean visible) {
        if (this.thumbImageVisibleOverride != visible) {
            this.thumbImageVisibleOverride = visible;
            AnimatorSet animatorSet = this.thumbOverrideAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
            }
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.thumbOverrideAnimation = animatorSet2;
            Animator[] animatorArr = new Animator[1];
            Property<PhotoCropView, Float> property = this.PROGRESS_VALUE;
            float[] fArr = new float[1];
            fArr[0] = visible ? 1.0f : 0.0f;
            animatorArr[0] = ObjectAnimator.ofFloat(this, property, fArr);
            animatorSet2.playTogether(animatorArr);
            this.thumbOverrideAnimation.setDuration(180);
            this.thumbOverrideAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    AnimatorSet unused = PhotoCropView.this.thumbOverrideAnimation = null;
                }
            });
            this.thumbOverrideAnimation.start();
        }
    }

    public boolean isReady() {
        return this.cropView.isReady();
    }

    public void reset() {
        this.wheelView.reset(true);
        this.cropView.reset();
    }

    public void onAppear() {
        this.cropView.willShow();
    }

    public void setAspectRatio(float ratio) {
        this.cropView.setAspectRatio(ratio);
    }

    public void setFreeform(boolean freeform) {
        this.cropView.setFreeform(freeform);
    }

    public void onAppeared() {
        this.cropView.show();
    }

    public void onDisappear() {
        this.cropView.hide();
    }

    public void onShow() {
        this.cropView.onShow();
    }

    public void onHide() {
        this.cropView.onHide();
    }

    public float getRectX() {
        return this.cropView.getCropLeft() - ((float) AndroidUtilities.dp(14.0f));
    }

    public float getRectY() {
        return (this.cropView.getCropTop() - ((float) AndroidUtilities.dp(14.0f))) - ((float) ((Build.VERSION.SDK_INT < 21 || this.inBubbleMode) ? 0 : AndroidUtilities.statusBarHeight));
    }

    public float getRectSizeX() {
        return this.cropView.getCropWidth();
    }

    public float getRectSizeY() {
        return this.cropView.getCropHeight();
    }

    public void makeCrop(MediaController.MediaEditState editState) {
        this.cropView.makeCrop(editState);
    }

    public void setDelegate(PhotoCropViewDelegate photoCropViewDelegate) {
        this.delegate = photoCropViewDelegate;
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        this.cropView.updateLayout();
    }

    public void invalidate() {
        super.invalidate();
        this.cropView.invalidate();
    }

    private int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }
}
