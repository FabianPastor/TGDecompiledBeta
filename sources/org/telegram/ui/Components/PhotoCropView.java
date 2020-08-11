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
        public void setValue(PhotoCropView photoCropView, float f) {
            float unused = PhotoCropView.this.thumbAnimationProgress = f;
            photoCropView.invalidate();
        }

        public Float get(PhotoCropView photoCropView) {
            return Float.valueOf(PhotoCropView.this.thumbAnimationProgress);
        }
    };
    public final Property<PhotoCropView, Float> PROGRESS_VALUE = new AnimationProperties.FloatProperty<PhotoCropView>("thumbImageVisibleProgress") {
        public void setValue(PhotoCropView photoCropView, float f) {
            float unused = PhotoCropView.this.thumbImageVisibleProgress = f;
            photoCropView.invalidate();
        }

        public Float get(PhotoCropView photoCropView) {
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

    public PhotoCropView(Context context) {
        super(context);
        this.inBubbleMode = context instanceof BubbleActivity;
        CropView cropView2 = new CropView(context);
        this.cropView = cropView2;
        cropView2.setListener(new CropView.CropViewListener() {
            public void onChange(boolean z) {
                if (PhotoCropView.this.delegate != null) {
                    PhotoCropView.this.delegate.onChange(z);
                }
            }

            public void onUpdate() {
                if (PhotoCropView.this.delegate != null) {
                    PhotoCropView.this.delegate.onUpdate();
                }
            }

            public void onAspectLock(boolean z) {
                PhotoCropView.this.wheelView.setAspectLock(z);
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

            public void onChange(float f) {
                PhotoCropView.this.cropView.setRotation(f);
                if (PhotoCropView.this.delegate != null) {
                    PhotoCropView.this.delegate.onChange(false);
                }
            }

            public void onEnd(float f) {
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

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (!this.thumbImageVisibleOverride || !this.thumbImageVisible || !this.thumbImageView.isInsideImage(motionEvent.getX(), motionEvent.getY())) {
            return super.onInterceptTouchEvent(motionEvent);
        }
        if (motionEvent.getAction() == 1) {
            this.delegate.onVideoThumbClick();
        }
        return true;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (!this.thumbImageVisibleOverride || !this.thumbImageVisible || !this.thumbImageView.isInsideImage(motionEvent.getX(), motionEvent.getY())) {
            return super.onTouchEvent(motionEvent);
        }
        if (motionEvent.getAction() == 1) {
            this.delegate.onVideoThumbClick();
        }
        return true;
    }

    /* access modifiers changed from: protected */
    public boolean drawChild(Canvas canvas, View view, long j) {
        CropView cropView2;
        boolean drawChild = super.drawChild(canvas, view, j);
        if (this.thumbImageVisible && view == (cropView2 = this.cropView)) {
            RectF actualRect = cropView2.getActualRect();
            int dp = AndroidUtilities.dp(32.0f);
            int i = dp / 2;
            int videoThumbX = (this.delegate.getVideoThumbX() - i) + AndroidUtilities.dp(2.0f);
            int measuredHeight = getMeasuredHeight() - AndroidUtilities.dp(156.0f);
            float f = actualRect.left;
            float f2 = this.thumbAnimationProgress;
            float f3 = f + ((((float) videoThumbX) - f) * f2);
            float f4 = actualRect.top;
            float f5 = f4 + ((((float) measuredHeight) - f4) * f2);
            float width = actualRect.width() + ((((float) dp) - actualRect.width()) * this.thumbAnimationProgress);
            this.thumbImageView.setRoundRadius((int) (width / 2.0f));
            this.thumbImageView.setImageCoords(f3, f5, width, width);
            this.thumbImageView.setAlpha(this.thumbImageVisibleProgress);
            this.thumbImageView.draw(canvas);
            if (this.flashAlpha > 0.0f) {
                this.circlePaint.setColor(-1);
                this.circlePaint.setAlpha((int) (this.flashAlpha * 255.0f));
                canvas.drawCircle(actualRect.centerX(), actualRect.centerY(), actualRect.width() / 2.0f, this.circlePaint);
            }
            this.circlePaint.setColor(Theme.getColor("dialogFloatingButton"));
            this.circlePaint.setAlpha(Math.min(255, (int) (this.thumbAnimationProgress * 255.0f * this.thumbImageVisibleProgress)));
            canvas.drawCircle((float) (videoThumbX + i), (float) (measuredHeight + dp + AndroidUtilities.dp(8.0f)), (float) AndroidUtilities.dp(3.0f), this.circlePaint);
        }
        return drawChild;
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

    public void setBitmap(Bitmap bitmap, int i, boolean z, boolean z2, PaintingOverlay paintingOverlay, CropTransform cropTransform, VideoEditTextureView videoEditTextureView, MediaController.CropState cropState) {
        boolean z3 = z;
        MediaController.CropState cropState2 = cropState;
        requestLayout();
        int i2 = 0;
        this.thumbImageVisible = false;
        this.thumbImageView.setImageBitmap((Drawable) null);
        this.cropView.setBitmap(bitmap, i, z, z2, paintingOverlay, cropTransform, videoEditTextureView, cropState);
        this.wheelView.setFreeform(z3);
        boolean z4 = true;
        this.wheelView.reset(true);
        if (cropState2 != null) {
            this.wheelView.setRotation(cropState2.cropRotate, false);
            CropRotationWheel cropRotationWheel = this.wheelView;
            if (cropState2.transformRotation == 0) {
                z4 = false;
            }
            cropRotationWheel.setRotated(z4);
            this.wheelView.setMirrored(cropState2.mirrored);
        } else {
            this.wheelView.setRotated(false);
            this.wheelView.setMirrored(false);
        }
        CropRotationWheel cropRotationWheel2 = this.wheelView;
        if (!z3) {
            i2 = 4;
        }
        cropRotationWheel2.setVisibility(i2);
    }

    public void setVideoThumbFlashAlpha(float f) {
        this.flashAlpha = f;
        invalidate();
    }

    public Bitmap getVideoThumb() {
        if (!this.thumbImageVisible || !this.thumbImageVisibleOverride) {
            return null;
        }
        return this.thumbImageView.getBitmap();
    }

    public void setVideoThumb(Bitmap bitmap, int i) {
        this.thumbImageVisible = bitmap != null;
        this.thumbImageView.setImageBitmap(bitmap);
        this.thumbImageView.setOrientation(i, false);
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
            public void onAnimationEnd(Animator animator) {
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

    public void setVideoThumbVisible(boolean z) {
        if (this.thumbImageVisibleOverride != z) {
            this.thumbImageVisibleOverride = z;
            AnimatorSet animatorSet = this.thumbOverrideAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
            }
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.thumbOverrideAnimation = animatorSet2;
            Animator[] animatorArr = new Animator[1];
            Property<PhotoCropView, Float> property = this.PROGRESS_VALUE;
            float[] fArr = new float[1];
            fArr[0] = z ? 1.0f : 0.0f;
            animatorArr[0] = ObjectAnimator.ofFloat(this, property, fArr);
            animatorSet2.playTogether(animatorArr);
            this.thumbOverrideAnimation.setDuration(180);
            this.thumbOverrideAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
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

    public void setAspectRatio(float f) {
        this.cropView.setAspectRatio(f);
    }

    public void setFreeform(boolean z) {
        this.cropView.setFreeform(z);
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

    public void makeCrop(MediaController.MediaEditState mediaEditState) {
        this.cropView.makeCrop(mediaEditState);
    }

    public void setDelegate(PhotoCropViewDelegate photoCropViewDelegate) {
        this.delegate = photoCropViewDelegate;
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        this.cropView.updateLayout();
    }

    public void invalidate() {
        super.invalidate();
        this.cropView.invalidate();
    }
}
