package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.FrameLayout;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.WebFile;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Components.AnimatedFileDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;

public class PinchToZoomHelper {
    Callback callback;
    /* access modifiers changed from: private */
    public View child;
    /* access modifiers changed from: private */
    public ImageReceiver childImage;
    ClipBoundsListener clipBoundsListener;
    /* access modifiers changed from: private */
    public float[] clipTopBottom = new float[2];
    /* access modifiers changed from: private */
    public float enterProgress;
    /* access modifiers changed from: private */
    public float finishProgress;
    ValueAnimator finishTransition;
    float fragmentOffsetX;
    float fragmentOffsetY;
    private final ViewGroup fragmentView;
    /* access modifiers changed from: private */
    public ImageReceiver fullImage = new ImageReceiver();
    /* access modifiers changed from: private */
    public float fullImageHeight;
    /* access modifiers changed from: private */
    public float fullImageWidth;
    /* access modifiers changed from: private */
    public float imageHeight;
    /* access modifiers changed from: private */
    public float imageWidth;
    /* access modifiers changed from: private */
    public float imageX;
    /* access modifiers changed from: private */
    public float imageY;
    /* access modifiers changed from: private */
    public boolean inOverlayMode;
    /* access modifiers changed from: private */
    public boolean isHardwareVideo;
    boolean isInPinchToZoomTouchMode;
    private MessageObject messageObject;
    private ZoomOverlayView overlayView;
    float parentOffsetX;
    float parentOffsetY;
    /* access modifiers changed from: private */
    public final ViewGroup parentView;
    float pinchCenterX;
    float pinchCenterY;
    float pinchScale;
    float pinchStartCenterX;
    float pinchStartCenterY;
    float pinchStartDistance;
    float pinchTranslationX;
    float pinchTranslationY;
    private int pointerId1;
    private int pointerId2;
    /* access modifiers changed from: private */
    public float progressToFullView;

    public interface ClipBoundsListener {
        void getClipTopBottom(float[] fArr);
    }

    static /* synthetic */ float access$1416(PinchToZoomHelper x0, float x1) {
        float f = x0.progressToFullView + x1;
        x0.progressToFullView = f;
        return f;
    }

    static /* synthetic */ float access$616(PinchToZoomHelper x0, float x1) {
        float f = x0.enterProgress + x1;
        x0.enterProgress = f;
        return f;
    }

    public PinchToZoomHelper(ViewGroup parentView2, ViewGroup fragmentView2) {
        this.parentView = parentView2;
        this.fragmentView = fragmentView2;
    }

    public void startZoom(View child2, ImageReceiver image, MessageObject messageObject2) {
        this.child = child2;
        this.messageObject = messageObject2;
        if (this.overlayView == null) {
            ZoomOverlayView zoomOverlayView = new ZoomOverlayView(this.parentView.getContext());
            this.overlayView = zoomOverlayView;
            zoomOverlayView.setFocusable(false);
            this.overlayView.setFocusableInTouchMode(false);
            this.overlayView.setEnabled(false);
        }
        if (this.fullImage == null) {
            ImageReceiver imageReceiver = new ImageReceiver();
            this.fullImage = imageReceiver;
            imageReceiver.setCrossfadeAlpha((byte) 2);
            this.fullImage.setCrossfadeWithOldImage(false);
            this.fullImage.onAttachedToWindow();
        }
        this.inOverlayMode = true;
        this.parentView.addView(this.overlayView);
        this.finishProgress = 1.0f;
        this.progressToFullView = 0.0f;
        setFullImage(messageObject2);
        this.imageX = image.getImageX();
        this.imageY = image.getImageY();
        this.imageHeight = image.getImageHeight();
        this.imageWidth = image.getImageWidth();
        this.fullImageHeight = (float) image.getBitmapHeight();
        float bitmapWidth = (float) image.getBitmapWidth();
        this.fullImageWidth = bitmapWidth;
        float f = this.fullImageHeight;
        float f2 = this.imageHeight;
        float f3 = this.imageWidth;
        if (f / bitmapWidth == f2 / f3) {
            this.fullImageHeight = f2;
            this.fullImageWidth = f3;
        } else if (f / bitmapWidth < f2 / f3) {
            this.fullImageWidth = (bitmapWidth / f) * f2;
            this.fullImageHeight = f2;
        } else {
            this.fullImageHeight = (f / bitmapWidth) * f3;
            this.fullImageWidth = f3;
        }
        if (messageObject2 == null || !messageObject2.isVideo() || !MediaController.getInstance().isPlayingMessage(messageObject2)) {
            this.isHardwareVideo = false;
            ImageReceiver imageReceiver2 = new ImageReceiver();
            this.childImage = imageReceiver2;
            imageReceiver2.onAttachedToWindow();
            Drawable drawable = image.getDrawable();
            this.childImage.setImageBitmap(drawable);
            if (drawable instanceof AnimatedFileDrawable) {
                ((AnimatedFileDrawable) drawable).addSecondParentView(this.overlayView);
                ((AnimatedFileDrawable) drawable).setInvalidateParentViewWithSecond(true);
            }
            this.childImage.setImageCoords(this.imageX, this.imageY, this.imageWidth, this.imageHeight);
            this.childImage.setRoundRadius(image.getRoundRadius());
            this.fullImage.setRoundRadius(image.getRoundRadius());
            this.overlayView.videoPlayerContainer.setVisibility(8);
        } else {
            this.isHardwareVideo = true;
            MediaController.getInstance().setTextureView(this.overlayView.videoTextureView, this.overlayView.aspectRatioFrameLayout, this.overlayView.videoPlayerContainer, true);
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.overlayView.videoPlayerContainer.getLayoutParams();
            this.overlayView.videoPlayerContainer.setTag(NUM, image);
            if (!(((float) layoutParams.width) == image.getImageWidth() && ((float) layoutParams.height) == image.getImageHeight())) {
                this.overlayView.aspectRatioFrameLayout.setResizeMode(3);
                layoutParams.width = (int) image.getImageWidth();
                layoutParams.height = (int) image.getImageHeight();
                this.overlayView.videoPlayerContainer.setLayoutParams(layoutParams);
            }
            this.overlayView.videoTextureView.setScaleX(1.0f);
            this.overlayView.videoTextureView.setScaleY(1.0f);
            if (this.callback != null) {
                this.overlayView.backupImageView.setImageBitmap(this.callback.getCurrentTextureView().getBitmap((int) this.fullImageWidth, (int) this.fullImageHeight));
                this.overlayView.backupImageView.setSize((int) this.fullImageWidth, (int) this.fullImageHeight);
                this.overlayView.backupImageView.getImageReceiver().setRoundRadius(image.getRoundRadius());
            }
            this.overlayView.videoPlayerContainer.setVisibility(0);
        }
        Callback callback2 = this.callback;
        if (callback2 != null) {
            callback2.onZoomStarted(messageObject2);
        }
        this.enterProgress = 0.0f;
    }

    private void setFullImage(MessageObject messageObject2) {
        MessageObject messageObject3 = messageObject2;
        if (messageObject3 != null && messageObject2.isPhoto()) {
            int[] size = new int[1];
            ImageLocation imageLocation = getImageLocation(messageObject3, size);
            if (imageLocation != null) {
                this.fullImage.setImage(imageLocation, (String) null, (ImageLocation) null, (String) null, (Drawable) null, (long) size[0], (String) null, messageObject2, messageObject3 != null && messageObject2.isWebpage() ? 1 : 0);
                this.fullImage.setCrossfadeAlpha((byte) 2);
            }
            updateViewsLocation();
        }
    }

    /* JADX WARNING: type inference failed for: r6v5, types: [android.view.ViewParent] */
    /* JADX WARNING: type inference failed for: r3v8, types: [android.view.ViewParent] */
    /* access modifiers changed from: private */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean updateViewsLocation() {
        /*
            r7 = this;
            r0 = 0
            r1 = 0
            android.view.View r2 = r7.child
        L_0x0004:
            android.view.ViewGroup r3 = r7.parentView
            r4 = 0
            if (r2 == r3) goto L_0x0020
            if (r2 != 0) goto L_0x000c
            return r4
        L_0x000c:
            int r3 = r2.getLeft()
            float r3 = (float) r3
            float r0 = r0 + r3
            int r3 = r2.getTop()
            float r3 = (float) r3
            float r1 = r1 + r3
            android.view.ViewParent r3 = r2.getParent()
            r2 = r3
            android.view.View r2 = (android.view.View) r2
            goto L_0x0004
        L_0x0020:
            r3 = 0
            r5 = 0
            android.view.View r2 = r7.child
        L_0x0024:
            android.view.ViewGroup r6 = r7.fragmentView
            if (r2 == r6) goto L_0x003f
            if (r2 != 0) goto L_0x002b
            return r4
        L_0x002b:
            int r6 = r2.getLeft()
            float r6 = (float) r6
            float r3 = r3 + r6
            int r6 = r2.getTop()
            float r6 = (float) r6
            float r5 = r5 + r6
            android.view.ViewParent r6 = r2.getParent()
            r2 = r6
            android.view.View r2 = (android.view.View) r2
            goto L_0x0024
        L_0x003f:
            r7.fragmentOffsetX = r3
            r7.fragmentOffsetY = r5
            r7.parentOffsetX = r0
            r7.parentOffsetY = r1
            r4 = 1
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PinchToZoomHelper.updateViewsLocation():boolean");
    }

    public void finishZoom() {
        if (this.finishTransition == null && this.inOverlayMode) {
            if (!updateViewsLocation()) {
                clear();
            }
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{1.0f, 0.0f});
            this.finishTransition = ofFloat;
            ofFloat.addUpdateListener(new PinchToZoomHelper$$ExternalSyntheticLambda0(this));
            this.finishTransition.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    if (PinchToZoomHelper.this.finishTransition != null) {
                        PinchToZoomHelper.this.finishTransition = null;
                        PinchToZoomHelper.this.clear();
                    }
                }
            });
            this.finishTransition.setDuration(220);
            this.finishTransition.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.finishTransition.start();
        }
    }

    /* renamed from: lambda$finishZoom$0$org-telegram-ui-PinchToZoomHelper  reason: not valid java name */
    public /* synthetic */ void m4341lambda$finishZoom$0$orgtelegramuiPinchToZoomHelper(ValueAnimator valueAnimator) {
        this.finishProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidateViews();
    }

    public void clear() {
        if (this.inOverlayMode) {
            Callback callback2 = this.callback;
            if (callback2 != null) {
                callback2.onZoomFinished(this.messageObject);
            }
            this.inOverlayMode = false;
        }
        ZoomOverlayView zoomOverlayView = this.overlayView;
        if (!(zoomOverlayView == null || zoomOverlayView.getParent() == null)) {
            this.parentView.removeView(this.overlayView);
            this.overlayView.backupImageView.getImageReceiver().clearImage();
            ImageReceiver imageReceiver = this.childImage;
            if (imageReceiver != null) {
                Drawable drawable = imageReceiver.getDrawable();
                if (drawable instanceof AnimatedFileDrawable) {
                    ((AnimatedFileDrawable) drawable).removeSecondParentView(this.overlayView);
                }
            }
        }
        View view = this.child;
        if (view != null) {
            view.invalidate();
            this.child = null;
        }
        ImageReceiver imageReceiver2 = this.childImage;
        if (imageReceiver2 != null) {
            imageReceiver2.onDetachedFromWindow();
            this.childImage.clearImage();
            this.childImage = null;
        }
        ImageReceiver imageReceiver3 = this.fullImage;
        if (imageReceiver3 != null) {
            imageReceiver3.onDetachedFromWindow();
            this.fullImage.clearImage();
            this.fullImage = null;
        }
        this.messageObject = null;
    }

    public boolean inOverlayMode() {
        return this.inOverlayMode;
    }

    public boolean isInOverlayMode() {
        return this.inOverlayMode;
    }

    public boolean isInOverlayModeFor(View child2) {
        return this.inOverlayMode && child2 == this.child;
    }

    public boolean onTouchEvent(MotionEvent ev) {
        if (!updateViewsLocation() || this.child == null) {
            return false;
        }
        ev.offsetLocation(-this.fragmentOffsetX, -this.fragmentOffsetY);
        return this.child.onTouchEvent(ev);
    }

    public Bitmap getVideoBitmap(int w, int h) {
        ZoomOverlayView zoomOverlayView = this.overlayView;
        if (zoomOverlayView == null) {
            return null;
        }
        return zoomOverlayView.videoTextureView.getBitmap(w, h);
    }

    public ImageReceiver getPhotoImage() {
        return this.childImage;
    }

    /* access modifiers changed from: protected */
    public boolean zoomEnabled(View child2, ImageReceiver receiver) {
        if (!(receiver.getDrawable() instanceof AnimatedFileDrawable)) {
            return receiver.hasNotThumb();
        }
        if (((AnimatedFileDrawable) receiver.getDrawable()).isLoadingStream()) {
            return false;
        }
        return true;
    }

    private class ZoomOverlayView extends FrameLayout {
        /* access modifiers changed from: private */
        public Paint aspectPaint = new Paint(1);
        /* access modifiers changed from: private */
        public Path aspectPath = new Path();
        /* access modifiers changed from: private */
        public AspectRatioFrameLayout aspectRatioFrameLayout;
        /* access modifiers changed from: private */
        public BackupImageView backupImageView;
        /* access modifiers changed from: private */
        public FrameLayout videoPlayerContainer;
        /* access modifiers changed from: private */
        public TextureView videoTextureView;

        public ZoomOverlayView(Context context) {
            super(context);
            if (Build.VERSION.SDK_INT >= 21) {
                FrameLayout frameLayout = new FrameLayout(context);
                this.videoPlayerContainer = frameLayout;
                frameLayout.setOutlineProvider(new ViewOutlineProvider(PinchToZoomHelper.this) {
                    public void getOutline(View view, Outline outline) {
                        ImageReceiver imageReceiver = (ImageReceiver) view.getTag(NUM);
                        if (imageReceiver != null) {
                            int[] rad = imageReceiver.getRoundRadius();
                            int maxRad = 0;
                            for (int a = 0; a < 4; a++) {
                                maxRad = Math.max(maxRad, rad[a]);
                            }
                            outline.setRoundRect(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight(), (float) maxRad);
                            return;
                        }
                        outline.setOval(0, 0, AndroidUtilities.roundMessageSize, AndroidUtilities.roundMessageSize);
                    }
                });
                this.videoPlayerContainer.setClipToOutline(true);
            } else {
                this.videoPlayerContainer = new FrameLayout(context, PinchToZoomHelper.this) {
                    RectF rect = new RectF();

                    /* access modifiers changed from: protected */
                    public void onSizeChanged(int w, int h, int oldw, int oldh) {
                        super.onSizeChanged(w, h, oldw, oldh);
                        ZoomOverlayView.this.aspectPath.reset();
                        ImageReceiver imageReceiver = (ImageReceiver) getTag(NUM);
                        if (imageReceiver != null) {
                            int[] rad = imageReceiver.getRoundRadius();
                            int maxRad = 0;
                            for (int a = 0; a < 4; a++) {
                                maxRad = Math.max(maxRad, rad[a]);
                            }
                            this.rect.set(0.0f, 0.0f, (float) w, (float) h);
                            ZoomOverlayView.this.aspectPath.addRoundRect(this.rect, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), Path.Direction.CW);
                        } else {
                            ZoomOverlayView.this.aspectPath.addCircle((float) (w / 2), (float) (h / 2), (float) (w / 2), Path.Direction.CW);
                        }
                        ZoomOverlayView.this.aspectPath.toggleInverseFillType();
                    }

                    public void setVisibility(int visibility) {
                        super.setVisibility(visibility);
                        if (visibility == 0) {
                            setLayerType(2, (Paint) null);
                        }
                    }

                    /* access modifiers changed from: protected */
                    public void dispatchDraw(Canvas canvas) {
                        super.dispatchDraw(canvas);
                        if (getTag() == null) {
                            canvas.drawPath(ZoomOverlayView.this.aspectPath, ZoomOverlayView.this.aspectPaint);
                        }
                    }
                };
                this.aspectPath = new Path();
                Paint paint = new Paint(1);
                this.aspectPaint = paint;
                paint.setColor(-16777216);
                this.aspectPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            }
            BackupImageView backupImageView2 = new BackupImageView(context);
            this.backupImageView = backupImageView2;
            this.videoPlayerContainer.addView(backupImageView2);
            this.videoPlayerContainer.setWillNotDraw(false);
            AspectRatioFrameLayout aspectRatioFrameLayout2 = new AspectRatioFrameLayout(context);
            this.aspectRatioFrameLayout = aspectRatioFrameLayout2;
            aspectRatioFrameLayout2.setBackgroundColor(0);
            this.videoPlayerContainer.addView(this.aspectRatioFrameLayout, LayoutHelper.createFrame(-1, -1, 17));
            TextureView textureView = new TextureView(context);
            this.videoTextureView = textureView;
            textureView.setOpaque(false);
            this.aspectRatioFrameLayout.addView(this.videoTextureView, LayoutHelper.createFrame(-1, -1.0f));
            addView(this.videoPlayerContainer, LayoutHelper.createFrame(-2, -2.0f));
            setWillNotDraw(false);
        }

        /* access modifiers changed from: protected */
        public void dispatchDraw(Canvas canvas) {
            if (PinchToZoomHelper.this.finishTransition == null && PinchToZoomHelper.this.enterProgress != 1.0f) {
                PinchToZoomHelper.access$616(PinchToZoomHelper.this, 0.07272727f);
                if (PinchToZoomHelper.this.enterProgress > 1.0f) {
                    float unused = PinchToZoomHelper.this.enterProgress = 1.0f;
                } else {
                    PinchToZoomHelper.this.invalidateViews();
                }
            }
            float progress = PinchToZoomHelper.this.finishProgress * CubicBezierInterpolator.DEFAULT.getInterpolation(PinchToZoomHelper.this.enterProgress);
            float clipTop = 0.0f;
            float clipBottom = (float) getMeasuredHeight();
            if (progress == 1.0f || PinchToZoomHelper.this.clipBoundsListener == null) {
                drawImage(canvas);
                super.dispatchDraw(canvas);
            } else {
                PinchToZoomHelper.this.clipBoundsListener.getClipTopBottom(PinchToZoomHelper.this.clipTopBottom);
                canvas.save();
                clipTop = PinchToZoomHelper.this.clipTopBottom[0] * (1.0f - progress);
                clipBottom = (PinchToZoomHelper.this.clipTopBottom[1] * (1.0f - progress)) + (((float) getMeasuredHeight()) * progress);
                canvas.clipRect(0.0f, clipTop, (float) getMeasuredWidth(), clipBottom);
                drawImage(canvas);
                super.dispatchDraw(canvas);
                canvas.restore();
            }
            Canvas canvas2 = canvas;
            PinchToZoomHelper.this.drawOverlays(canvas2, 1.0f - progress, PinchToZoomHelper.this.parentOffsetX - ((float) getLeft()), PinchToZoomHelper.this.parentOffsetY - ((float) getTop()), clipTop, clipBottom);
        }

        private void drawImage(Canvas canvas) {
            float p;
            if (PinchToZoomHelper.this.inOverlayMode && PinchToZoomHelper.this.child != null && PinchToZoomHelper.this.parentView != null) {
                boolean unused = PinchToZoomHelper.this.updateViewsLocation();
                float parentOffsetX = PinchToZoomHelper.this.parentOffsetX - ((float) getLeft());
                float parentOffsetY = PinchToZoomHelper.this.parentOffsetY - ((float) getTop());
                canvas.save();
                float s = ((PinchToZoomHelper.this.pinchScale * PinchToZoomHelper.this.finishProgress) + 1.0f) - PinchToZoomHelper.this.finishProgress;
                canvas.scale(s, s, PinchToZoomHelper.this.pinchCenterX + parentOffsetX, PinchToZoomHelper.this.pinchCenterY + parentOffsetY);
                canvas.translate((PinchToZoomHelper.this.pinchTranslationX * PinchToZoomHelper.this.finishProgress) + parentOffsetX, (PinchToZoomHelper.this.pinchTranslationY * PinchToZoomHelper.this.finishProgress) + parentOffsetY);
                if (PinchToZoomHelper.this.fullImage != null && PinchToZoomHelper.this.fullImage.hasNotThumb()) {
                    if (PinchToZoomHelper.this.progressToFullView != 1.0f) {
                        PinchToZoomHelper.access$1416(PinchToZoomHelper.this, 0.10666667f);
                        if (PinchToZoomHelper.this.progressToFullView > 1.0f) {
                            float unused2 = PinchToZoomHelper.this.progressToFullView = 1.0f;
                        } else {
                            PinchToZoomHelper.this.invalidateViews();
                        }
                    }
                    PinchToZoomHelper.this.fullImage.setAlpha(PinchToZoomHelper.this.progressToFullView);
                }
                float x = PinchToZoomHelper.this.imageX;
                float y = PinchToZoomHelper.this.imageY;
                if (!(PinchToZoomHelper.this.imageHeight == PinchToZoomHelper.this.fullImageHeight && PinchToZoomHelper.this.imageWidth == PinchToZoomHelper.this.fullImageWidth)) {
                    if (s < 1.0f) {
                        p = 0.0f;
                    } else if (s < 1.4f) {
                        p = (s - 1.0f) / 0.4f;
                    } else {
                        p = 1.0f;
                    }
                    float verticalPadding = (PinchToZoomHelper.this.fullImageHeight - PinchToZoomHelper.this.imageHeight) / 2.0f;
                    float horizontalPadding = (PinchToZoomHelper.this.fullImageWidth - PinchToZoomHelper.this.imageWidth) / 2.0f;
                    x = PinchToZoomHelper.this.imageX - (horizontalPadding * p);
                    y = PinchToZoomHelper.this.imageY - (verticalPadding * p);
                    if (PinchToZoomHelper.this.childImage != null) {
                        PinchToZoomHelper.this.childImage.setImageCoords(x, y, PinchToZoomHelper.this.imageWidth + (horizontalPadding * p * 2.0f), PinchToZoomHelper.this.imageHeight + (verticalPadding * p * 2.0f));
                    }
                }
                if (PinchToZoomHelper.this.isHardwareVideo) {
                    this.videoPlayerContainer.setPivotX(PinchToZoomHelper.this.pinchCenterX - PinchToZoomHelper.this.imageX);
                    this.videoPlayerContainer.setPivotY(PinchToZoomHelper.this.pinchCenterY - PinchToZoomHelper.this.imageY);
                    this.videoPlayerContainer.setScaleY(s);
                    this.videoPlayerContainer.setScaleX(s);
                    this.videoPlayerContainer.setTranslationX(x + parentOffsetX + (PinchToZoomHelper.this.pinchTranslationX * s * PinchToZoomHelper.this.finishProgress));
                    this.videoPlayerContainer.setTranslationY(y + parentOffsetY + (PinchToZoomHelper.this.pinchTranslationY * s * PinchToZoomHelper.this.finishProgress));
                } else if (PinchToZoomHelper.this.childImage != null) {
                    if (PinchToZoomHelper.this.progressToFullView != 1.0f) {
                        PinchToZoomHelper.this.childImage.draw(canvas);
                        PinchToZoomHelper.this.fullImage.setImageCoords(PinchToZoomHelper.this.childImage.getImageX(), PinchToZoomHelper.this.childImage.getImageY(), PinchToZoomHelper.this.childImage.getImageWidth(), PinchToZoomHelper.this.childImage.getImageHeight());
                        PinchToZoomHelper.this.fullImage.draw(canvas);
                    } else {
                        PinchToZoomHelper.this.fullImage.setImageCoords(PinchToZoomHelper.this.childImage.getImageX(), PinchToZoomHelper.this.childImage.getImageY(), PinchToZoomHelper.this.childImage.getImageWidth(), PinchToZoomHelper.this.childImage.getImageHeight());
                        PinchToZoomHelper.this.fullImage.draw(canvas);
                    }
                }
                canvas.restore();
            }
        }
    }

    /* access modifiers changed from: protected */
    public void drawOverlays(Canvas canvas, float alpha, float parentOffsetX2, float parentOffsetY2, float clipTop, float clipBottom) {
    }

    private ImageLocation getImageLocation(MessageObject message, int[] size) {
        if (message.messageOwner instanceof TLRPC.TL_messageService) {
            if (message.messageOwner.action instanceof TLRPC.TL_messageActionUserUpdatedPhoto) {
                return null;
            }
            TLRPC.PhotoSize sizeFull = FileLoader.getClosestPhotoSizeWithSize(message.photoThumbs, AndroidUtilities.getPhotoSize());
            if (sizeFull != null) {
                if (size != null) {
                    size[0] = sizeFull.size;
                    if (size[0] == 0) {
                        size[0] = -1;
                    }
                }
                return ImageLocation.getForObject(sizeFull, message.photoThumbsObject);
            } else if (size != null) {
                size[0] = -1;
            }
        } else if ((!(message.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto) || message.messageOwner.media.photo == null) && (!(message.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage) || message.messageOwner.media.webpage == null)) {
            if (message.messageOwner.media instanceof TLRPC.TL_messageMediaInvoice) {
                return ImageLocation.getForWebFile(WebFile.createWithWebDocument(((TLRPC.TL_messageMediaInvoice) message.messageOwner.media).photo));
            }
            if (message.getDocument() != null) {
                TLRPC.Document document = message.getDocument();
                if (MessageObject.isDocumentHasThumb(message.getDocument())) {
                    TLRPC.PhotoSize thumb = FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 90);
                    if (size != null) {
                        size[0] = thumb.size;
                        if (size[0] == 0) {
                            size[0] = -1;
                        }
                    }
                    return ImageLocation.getForDocument(thumb, document);
                }
            }
        } else if (message.isGif()) {
            return ImageLocation.getForDocument(message.getDocument());
        } else {
            TLRPC.PhotoSize sizeFull2 = FileLoader.getClosestPhotoSizeWithSize(message.photoThumbs, AndroidUtilities.getPhotoSize(), false, (TLRPC.PhotoSize) null, true);
            if (sizeFull2 != null) {
                if (size != null) {
                    size[0] = sizeFull2.size;
                    if (size[0] == 0) {
                        size[0] = -1;
                    }
                }
                return ImageLocation.getForObject(sizeFull2, message.photoThumbsObject);
            } else if (size != null) {
                size[0] = -1;
            }
        }
        return null;
    }

    public void setClipBoundsListener(ClipBoundsListener clipBoundsListener2) {
        this.clipBoundsListener = clipBoundsListener2;
    }

    public interface Callback {
        TextureView getCurrentTextureView();

        void onZoomFinished(MessageObject messageObject);

        void onZoomStarted(MessageObject messageObject);

        /* renamed from: org.telegram.ui.PinchToZoomHelper$Callback$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static TextureView $default$getCurrentTextureView(Callback _this) {
                return null;
            }

            public static void $default$onZoomStarted(Callback _this, MessageObject messageObject) {
            }

            public static void $default$onZoomFinished(Callback _this, MessageObject messageObject) {
            }
        }
    }

    public void setCallback(Callback callback2) {
        this.callback = callback2;
    }

    public boolean checkPinchToZoom(MotionEvent ev, View child2, ImageReceiver image, MessageObject messageObject2) {
        if (!zoomEnabled(child2, image)) {
            return false;
        }
        if (ev.getActionMasked() == 0 || ev.getActionMasked() == 5) {
            if (!this.isInPinchToZoomTouchMode && ev.getPointerCount() == 2) {
                this.pinchStartDistance = (float) Math.hypot((double) (ev.getX(1) - ev.getX(0)), (double) (ev.getY(1) - ev.getY(0)));
                float x = (ev.getX(0) + ev.getX(1)) / 2.0f;
                this.pinchCenterX = x;
                this.pinchStartCenterX = x;
                float y = (ev.getY(0) + ev.getY(1)) / 2.0f;
                this.pinchCenterY = y;
                this.pinchStartCenterY = y;
                this.pinchScale = 1.0f;
                this.pointerId1 = ev.getPointerId(0);
                this.pointerId2 = ev.getPointerId(1);
                this.isInPinchToZoomTouchMode = true;
            }
        } else if (ev.getActionMasked() == 2 && this.isInPinchToZoomTouchMode) {
            int index1 = -1;
            int index2 = -1;
            for (int i = 0; i < ev.getPointerCount(); i++) {
                if (this.pointerId1 == ev.getPointerId(i)) {
                    index1 = i;
                }
                if (this.pointerId2 == ev.getPointerId(i)) {
                    index2 = i;
                }
            }
            if (index1 == -1 || index2 == -1) {
                this.isInPinchToZoomTouchMode = false;
                child2.getParent().requestDisallowInterceptTouchEvent(false);
                finishZoom();
                return false;
            }
            float hypot = ((float) Math.hypot((double) (ev.getX(index2) - ev.getX(index1)), (double) (ev.getY(index2) - ev.getY(index1)))) / this.pinchStartDistance;
            this.pinchScale = hypot;
            if (hypot > 1.005f && !isInOverlayMode()) {
                this.pinchStartDistance = (float) Math.hypot((double) (ev.getX(index2) - ev.getX(index1)), (double) (ev.getY(index2) - ev.getY(index1)));
                float x2 = (ev.getX(index1) + ev.getX(index2)) / 2.0f;
                this.pinchCenterX = x2;
                this.pinchStartCenterX = x2;
                float y2 = (ev.getY(index1) + ev.getY(index2)) / 2.0f;
                this.pinchCenterY = y2;
                this.pinchStartCenterY = y2;
                this.pinchScale = 1.0f;
                this.pinchTranslationX = 0.0f;
                this.pinchTranslationY = 0.0f;
                child2.getParent().requestDisallowInterceptTouchEvent(true);
                startZoom(child2, image, messageObject2);
            }
            float f = this.pinchStartCenterX;
            float f2 = this.pinchStartCenterY;
            float f3 = this.pinchScale;
            this.pinchTranslationX = (-(f - ((ev.getX(index1) + ev.getX(index2)) / 2.0f))) / f3;
            this.pinchTranslationY = (-(f2 - ((ev.getY(index1) + ev.getY(index2)) / 2.0f))) / f3;
            invalidateViews();
        } else if ((ev.getActionMasked() == 1 || ((ev.getActionMasked() == 6 && checkPointerIds(ev)) || ev.getActionMasked() == 3)) && this.isInPinchToZoomTouchMode) {
            this.isInPinchToZoomTouchMode = false;
            child2.getParent().requestDisallowInterceptTouchEvent(false);
            finishZoom();
        }
        return isInOverlayModeFor(child2);
    }

    private boolean checkPointerIds(MotionEvent ev) {
        if (ev.getPointerCount() < 2) {
            return false;
        }
        if (this.pointerId1 == ev.getPointerId(0) && this.pointerId2 == ev.getPointerId(1)) {
            return true;
        }
        if (this.pointerId1 == ev.getPointerId(1) && this.pointerId2 == ev.getPointerId(0)) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public void invalidateViews() {
        ZoomOverlayView zoomOverlayView = this.overlayView;
        if (zoomOverlayView != null) {
            zoomOverlayView.invalidate();
        }
    }

    public View getChild() {
        return this.child;
    }
}
