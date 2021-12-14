package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
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
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$Message;
import org.telegram.tgnet.TLRPC$MessageMedia;
import org.telegram.tgnet.TLRPC$PhotoSize;
import org.telegram.tgnet.TLRPC$TL_messageActionUserUpdatedPhoto;
import org.telegram.tgnet.TLRPC$TL_messageMediaInvoice;
import org.telegram.tgnet.TLRPC$TL_messageMediaPhoto;
import org.telegram.tgnet.TLRPC$TL_messageMediaWebPage;
import org.telegram.tgnet.TLRPC$TL_messageService;
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

    public interface Callback {

        /* renamed from: org.telegram.ui.PinchToZoomHelper$Callback$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static TextureView $default$getCurrentTextureView(Callback callback) {
                return null;
            }

            public static void $default$onZoomFinished(Callback callback, MessageObject messageObject) {
            }
        }

        TextureView getCurrentTextureView();

        void onZoomFinished(MessageObject messageObject);

        void onZoomStarted(MessageObject messageObject);
    }

    public interface ClipBoundsListener {
        void getClipTopBottom(float[] fArr);
    }

    /* access modifiers changed from: protected */
    public void drawOverlays(Canvas canvas, float f, float f2, float f3, float f4, float f5) {
    }

    static /* synthetic */ float access$1416(PinchToZoomHelper pinchToZoomHelper, float f) {
        float f2 = pinchToZoomHelper.progressToFullView + f;
        pinchToZoomHelper.progressToFullView = f2;
        return f2;
    }

    static /* synthetic */ float access$616(PinchToZoomHelper pinchToZoomHelper, float f) {
        float f2 = pinchToZoomHelper.enterProgress + f;
        pinchToZoomHelper.enterProgress = f2;
        return f2;
    }

    public PinchToZoomHelper(ViewGroup viewGroup, ViewGroup viewGroup2) {
        this.parentView = viewGroup;
        this.fragmentView = viewGroup2;
    }

    public void startZoom(View view, ImageReceiver imageReceiver, MessageObject messageObject2) {
        this.child = view;
        this.messageObject = messageObject2;
        if (this.overlayView == null) {
            ZoomOverlayView zoomOverlayView = new ZoomOverlayView(this.parentView.getContext());
            this.overlayView = zoomOverlayView;
            zoomOverlayView.setFocusable(false);
            this.overlayView.setFocusableInTouchMode(false);
            this.overlayView.setEnabled(false);
        }
        if (this.fullImage == null) {
            ImageReceiver imageReceiver2 = new ImageReceiver();
            this.fullImage = imageReceiver2;
            imageReceiver2.setCrossfadeAlpha((byte) 2);
            this.fullImage.setCrossfadeWithOldImage(false);
            this.fullImage.onAttachedToWindow();
        }
        this.inOverlayMode = true;
        this.parentView.addView(this.overlayView);
        this.finishProgress = 1.0f;
        this.progressToFullView = 0.0f;
        setFullImage(messageObject2);
        this.imageX = imageReceiver.getImageX();
        this.imageY = imageReceiver.getImageY();
        this.imageHeight = imageReceiver.getImageHeight();
        this.imageWidth = imageReceiver.getImageWidth();
        this.fullImageHeight = (float) imageReceiver.getBitmapHeight();
        float bitmapWidth = (float) imageReceiver.getBitmapWidth();
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
            ImageReceiver imageReceiver3 = new ImageReceiver();
            this.childImage = imageReceiver3;
            imageReceiver3.onAttachedToWindow();
            Drawable drawable = imageReceiver.getDrawable();
            this.childImage.setImageBitmap(drawable);
            if (drawable instanceof AnimatedFileDrawable) {
                AnimatedFileDrawable animatedFileDrawable = (AnimatedFileDrawable) drawable;
                animatedFileDrawable.addSecondParentView(this.overlayView);
                animatedFileDrawable.setInvalidateParentViewWithSecond(true);
            }
            this.childImage.setImageCoords(this.imageX, this.imageY, this.imageWidth, this.imageHeight);
            this.childImage.setRoundRadius(imageReceiver.getRoundRadius());
            this.fullImage.setRoundRadius(imageReceiver.getRoundRadius());
            this.overlayView.videoPlayerContainer.setVisibility(8);
        } else {
            this.isHardwareVideo = true;
            MediaController.getInstance().setTextureView(this.overlayView.videoTextureView, this.overlayView.aspectRatioFrameLayout, this.overlayView.videoPlayerContainer, true);
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.overlayView.videoPlayerContainer.getLayoutParams();
            this.overlayView.videoPlayerContainer.setTag(NUM, imageReceiver);
            if (!(((float) layoutParams.width) == imageReceiver.getImageWidth() && ((float) layoutParams.height) == imageReceiver.getImageHeight())) {
                this.overlayView.aspectRatioFrameLayout.setResizeMode(3);
                layoutParams.width = (int) imageReceiver.getImageWidth();
                layoutParams.height = (int) imageReceiver.getImageHeight();
                this.overlayView.videoPlayerContainer.setLayoutParams(layoutParams);
            }
            this.overlayView.videoTextureView.setScaleX(1.0f);
            this.overlayView.videoTextureView.setScaleY(1.0f);
            if (this.callback != null) {
                this.overlayView.backupImageView.setImageBitmap(this.callback.getCurrentTextureView().getBitmap((int) this.fullImageWidth, (int) this.fullImageHeight));
                this.overlayView.backupImageView.setSize((int) this.fullImageWidth, (int) this.fullImageHeight);
                this.overlayView.backupImageView.getImageReceiver().setRoundRadius(imageReceiver.getRoundRadius());
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
        if (messageObject2 != null && messageObject2.isPhoto()) {
            int[] iArr = new int[1];
            ImageLocation imageLocation = getImageLocation(messageObject2, iArr);
            if (imageLocation != null) {
                this.fullImage.setImage(imageLocation, (String) null, (ImageLocation) null, (String) null, (Drawable) null, iArr[0], (String) null, messageObject2, messageObject2.isWebpage() ? 1 : 0);
                this.fullImage.setCrossfadeAlpha((byte) 2);
            }
            updateViewsLocation();
        }
    }

    /* access modifiers changed from: private */
    public boolean updateViewsLocation() {
        float f = 0.0f;
        float f2 = 0.0f;
        float f3 = 0.0f;
        for (View view = this.child; view != this.parentView; view = (View) view.getParent()) {
            if (view == null) {
                return false;
            }
            f2 += (float) view.getLeft();
            f3 += (float) view.getTop();
        }
        float f4 = 0.0f;
        for (View view2 = this.child; view2 != this.fragmentView; view2 = (View) view2.getParent()) {
            if (view2 == null) {
                return false;
            }
            f += (float) view2.getLeft();
            f4 += (float) view2.getTop();
        }
        this.fragmentOffsetX = f;
        this.fragmentOffsetY = f4;
        this.parentOffsetX = f2;
        this.parentOffsetY = f3;
        return true;
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
                public void onAnimationEnd(Animator animator) {
                    PinchToZoomHelper pinchToZoomHelper = PinchToZoomHelper.this;
                    if (pinchToZoomHelper.finishTransition != null) {
                        pinchToZoomHelper.finishTransition = null;
                        pinchToZoomHelper.clear();
                    }
                }
            });
            this.finishTransition.setDuration(220);
            this.finishTransition.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.finishTransition.start();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$finishZoom$0(ValueAnimator valueAnimator) {
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

    public boolean isInOverlayMode() {
        return this.inOverlayMode;
    }

    public boolean isInOverlayModeFor(View view) {
        return this.inOverlayMode && view == this.child;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (!updateViewsLocation() || this.child == null) {
            return false;
        }
        motionEvent.offsetLocation(-this.fragmentOffsetX, -this.fragmentOffsetY);
        return this.child.onTouchEvent(motionEvent);
    }

    public Bitmap getVideoBitmap(int i, int i2) {
        ZoomOverlayView zoomOverlayView = this.overlayView;
        if (zoomOverlayView == null) {
            return null;
        }
        return zoomOverlayView.videoTextureView.getBitmap(i, i2);
    }

    public ImageReceiver getPhotoImage() {
        return this.childImage;
    }

    /* access modifiers changed from: protected */
    public boolean zoomEnabled(View view, ImageReceiver imageReceiver) {
        if (imageReceiver.getDrawable() instanceof AnimatedFileDrawable) {
            return !((AnimatedFileDrawable) imageReceiver.getDrawable()).isLoadingStream();
        }
        return imageReceiver.hasNotThumb();
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
                frameLayout.setOutlineProvider(new ViewOutlineProvider(this, PinchToZoomHelper.this) {
                    @TargetApi(21)
                    public void getOutline(View view, Outline outline) {
                        ImageReceiver imageReceiver = (ImageReceiver) view.getTag(NUM);
                        if (imageReceiver != null) {
                            int[] roundRadius = imageReceiver.getRoundRadius();
                            int i = 0;
                            for (int i2 = 0; i2 < 4; i2++) {
                                i = Math.max(i, roundRadius[i2]);
                            }
                            outline.setRoundRect(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight(), (float) i);
                            return;
                        }
                        int i3 = AndroidUtilities.roundMessageSize;
                        outline.setOval(0, 0, i3, i3);
                    }
                });
                this.videoPlayerContainer.setClipToOutline(true);
            } else {
                this.videoPlayerContainer = new FrameLayout(context, PinchToZoomHelper.this) {
                    RectF rect = new RectF();

                    /* access modifiers changed from: protected */
                    public void onSizeChanged(int i, int i2, int i3, int i4) {
                        super.onSizeChanged(i, i2, i3, i4);
                        ZoomOverlayView.this.aspectPath.reset();
                        ImageReceiver imageReceiver = (ImageReceiver) getTag(NUM);
                        if (imageReceiver != null) {
                            int[] roundRadius = imageReceiver.getRoundRadius();
                            int i5 = 0;
                            for (int i6 = 0; i6 < 4; i6++) {
                                i5 = Math.max(i5, roundRadius[i6]);
                            }
                            this.rect.set(0.0f, 0.0f, (float) i, (float) i2);
                            ZoomOverlayView.this.aspectPath.addRoundRect(this.rect, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), Path.Direction.CW);
                        } else {
                            float f = (float) (i / 2);
                            ZoomOverlayView.this.aspectPath.addCircle(f, (float) (i2 / 2), f, Path.Direction.CW);
                        }
                        ZoomOverlayView.this.aspectPath.toggleInverseFillType();
                    }

                    public void setVisibility(int i) {
                        super.setVisibility(i);
                        if (i == 0) {
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
        /* JADX WARNING: Code restructure failed: missing block: B:10:0x004b, code lost:
            r4 = r13.this$0;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void dispatchDraw(android.graphics.Canvas r14) {
            /*
                r13 = this;
                org.telegram.ui.PinchToZoomHelper r0 = org.telegram.ui.PinchToZoomHelper.this
                android.animation.ValueAnimator r1 = r0.finishTransition
                r2 = 1065353216(0x3var_, float:1.0)
                if (r1 != 0) goto L_0x002d
                float r0 = r0.enterProgress
                int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
                if (r0 == 0) goto L_0x002d
                org.telegram.ui.PinchToZoomHelper r0 = org.telegram.ui.PinchToZoomHelper.this
                r1 = 1033171465(0x3d94var_, float:0.07272727)
                org.telegram.ui.PinchToZoomHelper.access$616(r0, r1)
                org.telegram.ui.PinchToZoomHelper r0 = org.telegram.ui.PinchToZoomHelper.this
                float r0 = r0.enterProgress
                int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
                if (r0 <= 0) goto L_0x0028
                org.telegram.ui.PinchToZoomHelper r0 = org.telegram.ui.PinchToZoomHelper.this
                float unused = r0.enterProgress = r2
                goto L_0x002d
            L_0x0028:
                org.telegram.ui.PinchToZoomHelper r0 = org.telegram.ui.PinchToZoomHelper.this
                r0.invalidateViews()
            L_0x002d:
                org.telegram.ui.PinchToZoomHelper r0 = org.telegram.ui.PinchToZoomHelper.this
                float r0 = r0.finishProgress
                org.telegram.ui.Components.CubicBezierInterpolator r1 = org.telegram.ui.Components.CubicBezierInterpolator.DEFAULT
                org.telegram.ui.PinchToZoomHelper r3 = org.telegram.ui.PinchToZoomHelper.this
                float r3 = r3.enterProgress
                float r1 = r1.getInterpolation(r3)
                float r0 = r0 * r1
                int r1 = r13.getMeasuredHeight()
                float r1 = (float) r1
                r3 = 0
                int r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
                if (r4 == 0) goto L_0x008f
                org.telegram.ui.PinchToZoomHelper r4 = org.telegram.ui.PinchToZoomHelper.this
                org.telegram.ui.PinchToZoomHelper$ClipBoundsListener r5 = r4.clipBoundsListener
                if (r5 == 0) goto L_0x008f
                float[] r1 = r4.clipTopBottom
                r5.getClipTopBottom(r1)
                r14.save()
                org.telegram.ui.PinchToZoomHelper r1 = org.telegram.ui.PinchToZoomHelper.this
                float[] r1 = r1.clipTopBottom
                r4 = 0
                r1 = r1[r4]
                float r4 = r2 - r0
                float r1 = r1 * r4
                org.telegram.ui.PinchToZoomHelper r5 = org.telegram.ui.PinchToZoomHelper.this
                float[] r5 = r5.clipTopBottom
                r6 = 1
                r5 = r5[r6]
                float r5 = r5 * r4
                int r4 = r13.getMeasuredHeight()
                float r4 = (float) r4
                float r4 = r4 * r0
                float r4 = r4 + r5
                int r5 = r13.getMeasuredWidth()
                float r5 = (float) r5
                r14.clipRect(r3, r1, r5, r4)
                r13.drawImage(r14)
                super.dispatchDraw(r14)
                r14.restore()
                r11 = r1
                r12 = r4
                goto L_0x0097
            L_0x008f:
                r13.drawImage(r14)
                super.dispatchDraw(r14)
                r12 = r1
                r11 = 0
            L_0x0097:
                org.telegram.ui.PinchToZoomHelper r1 = org.telegram.ui.PinchToZoomHelper.this
                float r1 = r1.parentOffsetX
                int r3 = r13.getLeft()
                float r3 = (float) r3
                float r9 = r1 - r3
                org.telegram.ui.PinchToZoomHelper r1 = org.telegram.ui.PinchToZoomHelper.this
                float r1 = r1.parentOffsetY
                int r3 = r13.getTop()
                float r3 = (float) r3
                float r10 = r1 - r3
                org.telegram.ui.PinchToZoomHelper r6 = org.telegram.ui.PinchToZoomHelper.this
                float r8 = r2 - r0
                r7 = r14
                r6.drawOverlays(r7, r8, r9, r10, r11, r12)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PinchToZoomHelper.ZoomOverlayView.dispatchDraw(android.graphics.Canvas):void");
        }

        private void drawImage(Canvas canvas) {
            if (PinchToZoomHelper.this.inOverlayMode && PinchToZoomHelper.this.child != null && PinchToZoomHelper.this.parentView != null) {
                boolean unused = PinchToZoomHelper.this.updateViewsLocation();
                float left = PinchToZoomHelper.this.parentOffsetX - ((float) getLeft());
                float top = PinchToZoomHelper.this.parentOffsetY - ((float) getTop());
                canvas.save();
                PinchToZoomHelper pinchToZoomHelper = PinchToZoomHelper.this;
                float access$700 = ((pinchToZoomHelper.pinchScale * pinchToZoomHelper.finishProgress) + 1.0f) - PinchToZoomHelper.this.finishProgress;
                PinchToZoomHelper pinchToZoomHelper2 = PinchToZoomHelper.this;
                canvas.scale(access$700, access$700, pinchToZoomHelper2.pinchCenterX + left, pinchToZoomHelper2.pinchCenterY + top);
                PinchToZoomHelper pinchToZoomHelper3 = PinchToZoomHelper.this;
                PinchToZoomHelper pinchToZoomHelper4 = PinchToZoomHelper.this;
                canvas.translate((pinchToZoomHelper3.pinchTranslationX * pinchToZoomHelper3.finishProgress) + left, (pinchToZoomHelper4.pinchTranslationY * pinchToZoomHelper4.finishProgress) + top);
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
                float access$1500 = PinchToZoomHelper.this.imageX;
                float access$1600 = PinchToZoomHelper.this.imageY;
                if (!(PinchToZoomHelper.this.imageHeight == PinchToZoomHelper.this.fullImageHeight && PinchToZoomHelper.this.imageWidth == PinchToZoomHelper.this.fullImageWidth)) {
                    float f = access$700 < 1.0f ? 0.0f : access$700 < 1.4f ? (access$700 - 1.0f) / 0.4f : 1.0f;
                    float access$2000 = ((PinchToZoomHelper.this.fullImageWidth - PinchToZoomHelper.this.imageWidth) / 2.0f) * f;
                    float access$15002 = PinchToZoomHelper.this.imageX - access$2000;
                    float access$1800 = ((PinchToZoomHelper.this.fullImageHeight - PinchToZoomHelper.this.imageHeight) / 2.0f) * f;
                    float access$16002 = PinchToZoomHelper.this.imageY - access$1800;
                    if (PinchToZoomHelper.this.childImage != null) {
                        PinchToZoomHelper.this.childImage.setImageCoords(access$15002, access$16002, PinchToZoomHelper.this.imageWidth + (access$2000 * 2.0f), PinchToZoomHelper.this.imageHeight + (access$1800 * 2.0f));
                    }
                    access$1600 = access$16002;
                    access$1500 = access$15002;
                }
                if (PinchToZoomHelper.this.isHardwareVideo) {
                    FrameLayout frameLayout = this.videoPlayerContainer;
                    PinchToZoomHelper pinchToZoomHelper5 = PinchToZoomHelper.this;
                    frameLayout.setPivotX(pinchToZoomHelper5.pinchCenterX - pinchToZoomHelper5.imageX);
                    FrameLayout frameLayout2 = this.videoPlayerContainer;
                    PinchToZoomHelper pinchToZoomHelper6 = PinchToZoomHelper.this;
                    frameLayout2.setPivotY(pinchToZoomHelper6.pinchCenterY - pinchToZoomHelper6.imageY);
                    this.videoPlayerContainer.setScaleY(access$700);
                    this.videoPlayerContainer.setScaleX(access$700);
                    FrameLayout frameLayout3 = this.videoPlayerContainer;
                    float f2 = access$1500 + left;
                    PinchToZoomHelper pinchToZoomHelper7 = PinchToZoomHelper.this;
                    frameLayout3.setTranslationX(f2 + (pinchToZoomHelper7.pinchTranslationX * access$700 * pinchToZoomHelper7.finishProgress));
                    FrameLayout frameLayout4 = this.videoPlayerContainer;
                    float f3 = access$1600 + top;
                    PinchToZoomHelper pinchToZoomHelper8 = PinchToZoomHelper.this;
                    frameLayout4.setTranslationY(f3 + (pinchToZoomHelper8.pinchTranslationY * access$700 * pinchToZoomHelper8.finishProgress));
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

    private ImageLocation getImageLocation(MessageObject messageObject2, int[] iArr) {
        TLRPC$Message tLRPC$Message = messageObject2.messageOwner;
        if (!(tLRPC$Message instanceof TLRPC$TL_messageService)) {
            TLRPC$MessageMedia tLRPC$MessageMedia = tLRPC$Message.media;
            if ((!(tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaPhoto) || tLRPC$MessageMedia.photo == null) && (!(tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaWebPage) || tLRPC$MessageMedia.webpage == null)) {
                if (tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaInvoice) {
                    return ImageLocation.getForWebFile(WebFile.createWithWebDocument(((TLRPC$TL_messageMediaInvoice) tLRPC$MessageMedia).photo));
                }
                if (messageObject2.getDocument() != null) {
                    TLRPC$Document document = messageObject2.getDocument();
                    if (MessageObject.isDocumentHasThumb(messageObject2.getDocument())) {
                        TLRPC$PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 90);
                        if (iArr != null) {
                            iArr[0] = closestPhotoSizeWithSize.size;
                            if (iArr[0] == 0) {
                                iArr[0] = -1;
                            }
                        }
                        return ImageLocation.getForDocument(closestPhotoSizeWithSize, document);
                    }
                }
            } else if (messageObject2.isGif()) {
                return ImageLocation.getForDocument(messageObject2.getDocument());
            } else {
                TLRPC$PhotoSize closestPhotoSizeWithSize2 = FileLoader.getClosestPhotoSizeWithSize(messageObject2.photoThumbs, AndroidUtilities.getPhotoSize(), false, (TLRPC$PhotoSize) null, true);
                if (closestPhotoSizeWithSize2 != null) {
                    if (iArr != null) {
                        iArr[0] = closestPhotoSizeWithSize2.size;
                        if (iArr[0] == 0) {
                            iArr[0] = -1;
                        }
                    }
                    return ImageLocation.getForObject(closestPhotoSizeWithSize2, messageObject2.photoThumbsObject);
                } else if (iArr != null) {
                    iArr[0] = -1;
                }
            }
        } else if (tLRPC$Message.action instanceof TLRPC$TL_messageActionUserUpdatedPhoto) {
            return null;
        } else {
            TLRPC$PhotoSize closestPhotoSizeWithSize3 = FileLoader.getClosestPhotoSizeWithSize(messageObject2.photoThumbs, AndroidUtilities.getPhotoSize());
            if (closestPhotoSizeWithSize3 != null) {
                if (iArr != null) {
                    iArr[0] = closestPhotoSizeWithSize3.size;
                    if (iArr[0] == 0) {
                        iArr[0] = -1;
                    }
                }
                return ImageLocation.getForObject(closestPhotoSizeWithSize3, messageObject2.photoThumbsObject);
            } else if (iArr != null) {
                iArr[0] = -1;
            }
        }
        return null;
    }

    public void setClipBoundsListener(ClipBoundsListener clipBoundsListener2) {
        this.clipBoundsListener = clipBoundsListener2;
    }

    public void setCallback(Callback callback2) {
        this.callback = callback2;
    }

    public boolean checkPinchToZoom(MotionEvent motionEvent, View view, ImageReceiver imageReceiver, MessageObject messageObject2) {
        if (!zoomEnabled(view, imageReceiver)) {
            return false;
        }
        if (motionEvent.getActionMasked() == 0 || motionEvent.getActionMasked() == 5) {
            if (!this.isInPinchToZoomTouchMode && motionEvent.getPointerCount() == 2) {
                this.pinchStartDistance = (float) Math.hypot((double) (motionEvent.getX(1) - motionEvent.getX(0)), (double) (motionEvent.getY(1) - motionEvent.getY(0)));
                float x = (motionEvent.getX(0) + motionEvent.getX(1)) / 2.0f;
                this.pinchCenterX = x;
                this.pinchStartCenterX = x;
                float y = (motionEvent.getY(0) + motionEvent.getY(1)) / 2.0f;
                this.pinchCenterY = y;
                this.pinchStartCenterY = y;
                this.pinchScale = 1.0f;
                this.pointerId1 = motionEvent.getPointerId(0);
                this.pointerId2 = motionEvent.getPointerId(1);
                this.isInPinchToZoomTouchMode = true;
            }
        } else if (motionEvent.getActionMasked() == 2 && this.isInPinchToZoomTouchMode) {
            int i = -1;
            int i2 = -1;
            for (int i3 = 0; i3 < motionEvent.getPointerCount(); i3++) {
                if (this.pointerId1 == motionEvent.getPointerId(i3)) {
                    i = i3;
                }
                if (this.pointerId2 == motionEvent.getPointerId(i3)) {
                    i2 = i3;
                }
            }
            if (i == -1 || i2 == -1) {
                this.isInPinchToZoomTouchMode = false;
                view.getParent().requestDisallowInterceptTouchEvent(false);
                finishZoom();
                return false;
            }
            float hypot = ((float) Math.hypot((double) (motionEvent.getX(i2) - motionEvent.getX(i)), (double) (motionEvent.getY(i2) - motionEvent.getY(i)))) / this.pinchStartDistance;
            this.pinchScale = hypot;
            if (hypot > 1.005f && !isInOverlayMode()) {
                this.pinchStartDistance = (float) Math.hypot((double) (motionEvent.getX(i2) - motionEvent.getX(i)), (double) (motionEvent.getY(i2) - motionEvent.getY(i)));
                float x2 = (motionEvent.getX(i) + motionEvent.getX(i2)) / 2.0f;
                this.pinchCenterX = x2;
                this.pinchStartCenterX = x2;
                float y2 = (motionEvent.getY(i) + motionEvent.getY(i2)) / 2.0f;
                this.pinchCenterY = y2;
                this.pinchStartCenterY = y2;
                this.pinchScale = 1.0f;
                this.pinchTranslationX = 0.0f;
                this.pinchTranslationY = 0.0f;
                view.getParent().requestDisallowInterceptTouchEvent(true);
                startZoom(view, imageReceiver, messageObject2);
            }
            float x3 = this.pinchStartCenterX - ((motionEvent.getX(i) + motionEvent.getX(i2)) / 2.0f);
            float y3 = this.pinchStartCenterY - ((motionEvent.getY(i) + motionEvent.getY(i2)) / 2.0f);
            float f = this.pinchScale;
            this.pinchTranslationX = (-x3) / f;
            this.pinchTranslationY = (-y3) / f;
            invalidateViews();
        } else if ((motionEvent.getActionMasked() == 1 || ((motionEvent.getActionMasked() == 6 && checkPointerIds(motionEvent)) || motionEvent.getActionMasked() == 3)) && this.isInPinchToZoomTouchMode) {
            this.isInPinchToZoomTouchMode = false;
            view.getParent().requestDisallowInterceptTouchEvent(false);
            finishZoom();
        }
        return isInOverlayModeFor(view);
    }

    private boolean checkPointerIds(MotionEvent motionEvent) {
        if (motionEvent.getPointerCount() < 2) {
            return false;
        }
        if (this.pointerId1 == motionEvent.getPointerId(0) && this.pointerId2 == motionEvent.getPointerId(1)) {
            return true;
        }
        if (this.pointerId1 == motionEvent.getPointerId(1) && this.pointerId2 == motionEvent.getPointerId(0)) {
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
