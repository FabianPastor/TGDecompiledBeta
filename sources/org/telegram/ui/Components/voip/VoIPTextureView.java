package org.telegram.ui.Components.voip;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.os.Build;
import android.view.TextureView;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.Utilities;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.webrtc.RendererCommon;
import org.webrtc.TextureViewRenderer;

public class VoIPTextureView extends FrameLayout {
    public static int SCALE_TYPE_ADAPTIVE = 2;
    public static int SCALE_TYPE_FILL = 0;
    public static int SCALE_TYPE_FIT = 1;
    public static int SCALE_TYPE_NONE = 3;
    int animateFromHeight;
    float animateFromRendererH;
    float animateFromRendererW;
    float animateFromThumbScale;
    int animateFromWidth;
    float animateFromX;
    float animateFromY;
    long animateNextDuration;
    boolean animateOnNextLayout;
    ArrayList<Animator> animateOnNextLayoutAnimations;
    boolean animateWithParent;
    public float animationProgress;
    float aninateFromScale;
    float aninateFromScaleBlur;
    final boolean applyRotation;
    public View backgroundView;
    public TextureView blurRenderer;
    public Bitmap cameraLastBitmap;
    float clipHorizontal;
    boolean clipToTexture;
    float clipVertical;
    ValueAnimator currentAnimation;
    float currentClipHorizontal;
    float currentClipVertical;
    float currentThumbScale;
    boolean ignoreLayout;
    public final ImageView imageView;
    final boolean isCamera;
    public final TextureViewRenderer renderer;
    float roundRadius;
    public float scaleTextureToFill;
    /* access modifiers changed from: private */
    public float scaleTextureToFillBlur;
    /* access modifiers changed from: private */
    public float scaleThumb;
    public int scaleType;
    public float stubVisibleProgress;
    private Bitmap thumb;

    public VoIPTextureView(Context context, boolean z, boolean z2) {
        this(context, z, z2, true, false);
    }

    public VoIPTextureView(Context context, boolean z, boolean z2, boolean z3, boolean z4) {
        super(context);
        this.stubVisibleProgress = 1.0f;
        this.animateOnNextLayoutAnimations = new ArrayList<>();
        this.aninateFromScale = 1.0f;
        this.aninateFromScaleBlur = 1.0f;
        this.animateFromThumbScale = 1.0f;
        this.isCamera = z;
        this.applyRotation = z2;
        ImageView imageView2 = new ImageView(context);
        this.imageView = imageView2;
        AnonymousClass1 r1 = new TextureViewRenderer(context) {
            public void onFirstFrameRendered() {
                super.onFirstFrameRendered();
                VoIPTextureView.this.onFirstFrameRendered();
            }
        };
        this.renderer = r1;
        r1.setFpsReduction(30.0f);
        r1.setOpaque(false);
        r1.setEnableHardwareScaler(true);
        r1.setIsCamera(!z2);
        if (!z && z2) {
            View view = new View(context);
            this.backgroundView = view;
            view.setBackgroundColor(-14999773);
            addView(this.backgroundView, LayoutHelper.createFrame(-1, -1.0f));
            if (z4) {
                TextureView textureView = new TextureView(context);
                this.blurRenderer = textureView;
                addView(textureView, LayoutHelper.createFrame(-1, -2, 17));
            }
            r1.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
            addView(r1, LayoutHelper.createFrame(-1, -2, 17));
        } else if (!z) {
            if (z4) {
                TextureView textureView2 = new TextureView(context);
                this.blurRenderer = textureView2;
                addView(textureView2, LayoutHelper.createFrame(-1, -2, 17));
            }
            addView(r1, LayoutHelper.createFrame(-1, -2, 17));
        } else {
            if (z4) {
                TextureView textureView3 = new TextureView(context);
                this.blurRenderer = textureView3;
                addView(textureView3, LayoutHelper.createFrame(-1, -2, 17));
            }
            addView(r1);
        }
        addView(imageView2);
        TextureView textureView4 = this.blurRenderer;
        if (textureView4 != null) {
            textureView4.setOpaque(false);
        }
        if (z3 && Build.VERSION.SDK_INT >= 21) {
            setOutlineProvider(new ViewOutlineProvider() {
                @TargetApi(21)
                public void getOutline(View view, Outline outline) {
                    VoIPTextureView voIPTextureView = VoIPTextureView.this;
                    if (voIPTextureView.roundRadius < 1.0f) {
                        outline.setRect((int) voIPTextureView.currentClipHorizontal, (int) voIPTextureView.currentClipVertical, (int) (((float) view.getMeasuredWidth()) - VoIPTextureView.this.currentClipHorizontal), (int) (((float) view.getMeasuredHeight()) - VoIPTextureView.this.currentClipVertical));
                        return;
                    }
                    int i = (int) voIPTextureView.currentClipHorizontal;
                    int i2 = (int) voIPTextureView.currentClipVertical;
                    int measuredWidth = (int) (((float) view.getMeasuredWidth()) - VoIPTextureView.this.currentClipHorizontal);
                    VoIPTextureView voIPTextureView2 = VoIPTextureView.this;
                    outline.setRoundRect(i, i2, measuredWidth, (int) (((float) view.getMeasuredHeight()) - voIPTextureView2.currentClipVertical), voIPTextureView2.roundRadius);
                }
            });
            setClipToOutline(true);
        }
        if (z && this.cameraLastBitmap == null) {
            try {
                Bitmap decodeFile = BitmapFactory.decodeFile(new File(ApplicationLoader.getFilesDirFixed(), "voip_icthumb.jpg").getAbsolutePath());
                this.cameraLastBitmap = decodeFile;
                if (decodeFile == null) {
                    this.cameraLastBitmap = BitmapFactory.decodeFile(new File(ApplicationLoader.getFilesDirFixed(), "icthumb.jpg").getAbsolutePath());
                }
                imageView2.setImageBitmap(this.cameraLastBitmap);
                imageView2.setScaleType(ImageView.ScaleType.FIT_XY);
            } catch (Throwable unused) {
            }
        }
        if (!z2) {
            this.renderer.setScreenRotation(((WindowManager) getContext().getSystemService("window")).getDefaultDisplay().getRotation());
        }
    }

    /* access modifiers changed from: protected */
    public void onFirstFrameRendered() {
        invalidate();
        if (this.renderer.getAlpha() != 1.0f) {
            this.renderer.animate().setDuration(300).alpha(1.0f);
        }
        TextureView textureView = this.blurRenderer;
        if (textureView != null && textureView.getAlpha() != 1.0f) {
            this.blurRenderer.animate().setDuration(300).alpha(1.0f);
        }
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (this.imageView.getVisibility() == 0 && this.renderer.isFirstFrameRendered()) {
            float f = this.stubVisibleProgress - 0.10666667f;
            this.stubVisibleProgress = f;
            if (f <= 0.0f) {
                this.stubVisibleProgress = 0.0f;
                this.imageView.setVisibility(8);
                return;
            }
            invalidate();
            this.imageView.setAlpha(this.stubVisibleProgress);
        }
    }

    public void setRoundCorners(float f) {
        if (this.roundRadius != f) {
            this.roundRadius = f;
            if (Build.VERSION.SDK_INT >= 21) {
                invalidateOutline();
            } else {
                invalidate();
            }
        }
    }

    public void saveCameraLastBitmap() {
        Bitmap bitmap = this.renderer.getBitmap(150, 150);
        if (bitmap != null && bitmap.getPixel(0, 0) != 0) {
            Utilities.blurBitmap(bitmap, 3, 1, bitmap.getWidth(), bitmap.getHeight(), bitmap.getRowBytes());
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(new File(ApplicationLoader.getFilesDirFixed(), "voip_icthumb.jpg"));
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                fileOutputStream.close();
            } catch (Throwable unused) {
            }
        }
    }

    public void setStub(VoIPTextureView voIPTextureView) {
        Bitmap bitmap = voIPTextureView.renderer.getBitmap();
        if (bitmap == null || bitmap.getPixel(0, 0) == 0) {
            this.imageView.setImageDrawable(voIPTextureView.imageView.getDrawable());
        } else {
            this.imageView.setImageBitmap(bitmap);
            this.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
        this.stubVisibleProgress = 1.0f;
        this.imageView.setVisibility(0);
        this.imageView.setAlpha(1.0f);
    }

    public void animateToLayout() {
        if (!this.animateOnNextLayout && getMeasuredHeight() != 0 && getMeasuredWidth() != 0) {
            this.animateFromHeight = getMeasuredHeight();
            this.animateFromWidth = getMeasuredWidth();
            if (!this.animateWithParent || getParent() == null) {
                this.animateFromY = getY();
                this.animateFromX = getX();
            } else {
                View view = (View) getParent();
                this.animateFromY = view.getY();
                this.animateFromX = view.getX();
            }
            this.aninateFromScale = this.scaleTextureToFill;
            this.aninateFromScaleBlur = this.scaleTextureToFillBlur;
            this.animateFromThumbScale = this.scaleThumb;
            this.animateFromRendererW = (float) this.renderer.getMeasuredWidth();
            this.animateFromRendererH = (float) this.renderer.getMeasuredHeight();
            this.animateOnNextLayout = true;
            requestLayout();
        }
    }

    public void requestLayout() {
        if (!this.ignoreLayout) {
            super.requestLayout();
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        if (!this.applyRotation) {
            this.ignoreLayout = true;
            this.renderer.setScreenRotation(((WindowManager) getContext().getSystemService("window")).getDefaultDisplay().getRotation());
            this.ignoreLayout = false;
        }
        super.onMeasure(i, i2);
        updateRendererSize();
        super.onMeasure(i, i2);
    }

    /* access modifiers changed from: protected */
    public void updateRendererSize() {
        TextureView textureView = this.blurRenderer;
        if (textureView != null) {
            textureView.getLayoutParams().width = this.renderer.getMeasuredWidth();
            this.blurRenderer.getLayoutParams().height = this.renderer.getMeasuredHeight();
        }
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        float f;
        float f2;
        super.onLayout(z, i, i2, i3, i4);
        if (this.blurRenderer != null) {
            this.scaleTextureToFillBlur = Math.max(((float) getMeasuredHeight()) / ((float) this.blurRenderer.getMeasuredHeight()), ((float) getMeasuredWidth()) / ((float) this.blurRenderer.getMeasuredWidth()));
        }
        if (this.scaleType == SCALE_TYPE_NONE) {
            TextureView textureView = this.blurRenderer;
            if (textureView != null) {
                textureView.setScaleX(this.scaleTextureToFillBlur);
                this.blurRenderer.setScaleY(this.scaleTextureToFillBlur);
                return;
            }
            return;
        }
        if (this.renderer.getMeasuredHeight() == 0 || this.renderer.getMeasuredWidth() == 0 || getMeasuredHeight() == 0 || getMeasuredWidth() == 0) {
            this.scaleTextureToFill = 1.0f;
            if (this.currentAnimation == null && !this.animateOnNextLayout) {
                this.currentClipHorizontal = 0.0f;
                this.currentClipVertical = 0.0f;
            }
        } else {
            int i5 = this.scaleType;
            if (i5 == SCALE_TYPE_FILL) {
                this.scaleTextureToFill = Math.max(((float) getMeasuredHeight()) / ((float) this.renderer.getMeasuredHeight()), ((float) getMeasuredWidth()) / ((float) this.renderer.getMeasuredWidth()));
            } else if (i5 == SCALE_TYPE_ADAPTIVE) {
                if (Math.abs((((float) getMeasuredHeight()) / ((float) getMeasuredWidth())) - 1.0f) < 0.02f) {
                    this.scaleTextureToFill = Math.max(((float) getMeasuredHeight()) / ((float) this.renderer.getMeasuredHeight()), ((float) getMeasuredWidth()) / ((float) this.renderer.getMeasuredWidth()));
                } else if (getMeasuredWidth() <= getMeasuredHeight() || this.renderer.getMeasuredHeight() <= this.renderer.getMeasuredWidth()) {
                    this.scaleTextureToFill = Math.min(((float) getMeasuredHeight()) / ((float) this.renderer.getMeasuredHeight()), ((float) getMeasuredWidth()) / ((float) this.renderer.getMeasuredWidth()));
                } else {
                    this.scaleTextureToFill = Math.max(((float) getMeasuredHeight()) / ((float) this.renderer.getMeasuredHeight()), (((float) getMeasuredWidth()) / 2.0f) / ((float) this.renderer.getMeasuredWidth()));
                }
            } else if (i5 == SCALE_TYPE_FIT) {
                this.scaleTextureToFill = Math.min(((float) getMeasuredHeight()) / ((float) this.renderer.getMeasuredHeight()), ((float) getMeasuredWidth()) / ((float) this.renderer.getMeasuredWidth()));
                if (this.clipToTexture && !this.animateWithParent && this.currentAnimation == null && !this.animateOnNextLayout) {
                    this.currentClipHorizontal = ((float) (getMeasuredWidth() - this.renderer.getMeasuredWidth())) / 2.0f;
                    this.currentClipVertical = ((float) (getMeasuredHeight() - this.renderer.getMeasuredHeight())) / 2.0f;
                    if (Build.VERSION.SDK_INT >= 21) {
                        invalidateOutline();
                    }
                }
            }
        }
        if (this.thumb != null) {
            this.scaleThumb = Math.max(((float) getMeasuredWidth()) / ((float) this.thumb.getWidth()), ((float) getMeasuredHeight()) / ((float) this.thumb.getHeight()));
        }
        if (this.animateOnNextLayout) {
            this.aninateFromScale /= ((float) this.renderer.getMeasuredWidth()) / this.animateFromRendererW;
            this.aninateFromScaleBlur /= ((float) this.renderer.getMeasuredWidth()) / this.animateFromRendererW;
            this.animateOnNextLayout = false;
            if (!this.animateWithParent || getParent() == null) {
                f2 = this.animateFromY - ((float) getTop());
                f = this.animateFromX - ((float) getLeft());
            } else {
                View view = (View) getParent();
                f2 = this.animateFromY - ((float) view.getTop());
                f = this.animateFromX - ((float) view.getLeft());
            }
            this.clipVertical = 0.0f;
            this.clipHorizontal = 0.0f;
            if (this.animateFromHeight != getMeasuredHeight()) {
                float measuredHeight = ((float) (getMeasuredHeight() - this.animateFromHeight)) / 2.0f;
                this.clipVertical = measuredHeight;
                f2 -= measuredHeight;
            }
            float f3 = f2;
            if (this.animateFromWidth != getMeasuredWidth()) {
                float measuredWidth = ((float) (getMeasuredWidth() - this.animateFromWidth)) / 2.0f;
                this.clipHorizontal = measuredWidth;
                f -= measuredWidth;
            }
            float f4 = f;
            setTranslationY(f3);
            setTranslationX(f4);
            ValueAnimator valueAnimator = this.currentAnimation;
            if (valueAnimator != null) {
                valueAnimator.removeAllListeners();
                this.currentAnimation.cancel();
            }
            this.renderer.setScaleX(this.aninateFromScale);
            this.renderer.setScaleY(this.aninateFromScale);
            TextureView textureView2 = this.blurRenderer;
            if (textureView2 != null) {
                textureView2.setScaleX(this.aninateFromScaleBlur);
                this.blurRenderer.setScaleY(this.aninateFromScaleBlur);
            }
            this.currentClipVertical = this.clipVertical;
            this.currentClipHorizontal = this.clipHorizontal;
            if (Build.VERSION.SDK_INT >= 21) {
                invalidateOutline();
            }
            invalidate();
            float f5 = this.aninateFromScale;
            float f6 = this.aninateFromScaleBlur;
            float f7 = this.animateFromThumbScale;
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{1.0f, 0.0f});
            this.currentAnimation = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(f5, f6, f4, f3, f7) {
                public final /* synthetic */ float f$1;
                public final /* synthetic */ float f$2;
                public final /* synthetic */ float f$3;
                public final /* synthetic */ float f$4;
                public final /* synthetic */ float f$5;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                    this.f$5 = r6;
                }

                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    VoIPTextureView.this.lambda$onLayout$0$VoIPTextureView(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, valueAnimator);
                }
            });
            long j = this.animateNextDuration;
            if (j != 0) {
                this.currentAnimation.setDuration(j);
            } else {
                this.currentAnimation.setDuration(350);
            }
            this.currentAnimation.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.currentAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    VoIPTextureView voIPTextureView = VoIPTextureView.this;
                    voIPTextureView.currentClipVertical = 0.0f;
                    voIPTextureView.currentClipHorizontal = 0.0f;
                    voIPTextureView.renderer.setScaleX(voIPTextureView.scaleTextureToFill);
                    VoIPTextureView voIPTextureView2 = VoIPTextureView.this;
                    voIPTextureView2.renderer.setScaleY(voIPTextureView2.scaleTextureToFill);
                    VoIPTextureView voIPTextureView3 = VoIPTextureView.this;
                    TextureView textureView = voIPTextureView3.blurRenderer;
                    if (textureView != null) {
                        textureView.setScaleX(voIPTextureView3.scaleTextureToFillBlur);
                        VoIPTextureView voIPTextureView4 = VoIPTextureView.this;
                        voIPTextureView4.blurRenderer.setScaleY(voIPTextureView4.scaleTextureToFillBlur);
                    }
                    VoIPTextureView.this.setTranslationY(0.0f);
                    VoIPTextureView.this.setTranslationX(0.0f);
                    VoIPTextureView voIPTextureView5 = VoIPTextureView.this;
                    voIPTextureView5.currentThumbScale = voIPTextureView5.scaleThumb;
                    VoIPTextureView.this.currentAnimation = null;
                }
            });
            this.currentAnimation.start();
            if (!this.animateOnNextLayoutAnimations.isEmpty()) {
                for (int i6 = 0; i6 < this.animateOnNextLayoutAnimations.size(); i6++) {
                    this.animateOnNextLayoutAnimations.get(i6).start();
                }
            }
            this.animateOnNextLayoutAnimations.clear();
            this.animateNextDuration = 0;
        } else if (this.currentAnimation == null) {
            this.renderer.setScaleX(this.scaleTextureToFill);
            this.renderer.setScaleY(this.scaleTextureToFill);
            TextureView textureView3 = this.blurRenderer;
            if (textureView3 != null) {
                textureView3.setScaleX(this.scaleTextureToFillBlur);
                this.blurRenderer.setScaleY(this.scaleTextureToFillBlur);
            }
            this.currentThumbScale = this.scaleThumb;
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onLayout$0 */
    public /* synthetic */ void lambda$onLayout$0$VoIPTextureView(float f, float f2, float f3, float f4, float f5, ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        float f6 = 1.0f - floatValue;
        this.animationProgress = f6;
        this.currentClipVertical = this.clipVertical * floatValue;
        this.currentClipHorizontal = this.clipHorizontal * floatValue;
        if (Build.VERSION.SDK_INT >= 21) {
            invalidateOutline();
        }
        invalidate();
        float f7 = (f * floatValue) + (this.scaleTextureToFill * f6);
        this.renderer.setScaleX(f7);
        this.renderer.setScaleY(f7);
        float f8 = (f2 * floatValue) + (this.scaleTextureToFillBlur * f6);
        TextureView textureView = this.blurRenderer;
        if (textureView != null) {
            textureView.setScaleX(f8);
            this.blurRenderer.setScaleY(f8);
        }
        setTranslationX(f3 * floatValue);
        setTranslationY(f4 * floatValue);
        this.currentThumbScale = (f5 * floatValue) + (this.scaleThumb * f6);
    }

    public void setAnimateWithParent(boolean z) {
        this.animateWithParent = z;
    }

    public void synchOrRunAnimation(Animator animator) {
        if (this.animateOnNextLayout) {
            this.animateOnNextLayoutAnimations.add(animator);
        } else {
            animator.start();
        }
    }

    public void cancelAnimation() {
        this.animateOnNextLayout = false;
        this.animateNextDuration = 0;
    }

    public void setAnimateNextDuration(long j) {
        this.animateNextDuration = j;
    }

    public void setThumb(Bitmap bitmap) {
        this.thumb = bitmap;
    }

    public void attachBackgroundRenderer() {
        TextureView textureView = this.blurRenderer;
        if (textureView != null) {
            this.renderer.setBackgroundRenderer(textureView);
            if (!this.renderer.isFirstFrameRendered()) {
                this.blurRenderer.setAlpha(0.0f);
            }
        }
    }

    public boolean isInAnimation() {
        return this.currentAnimation != null;
    }

    public void updateRotation() {
        if (!this.applyRotation) {
            this.renderer.setScreenRotation(((WindowManager) getContext().getSystemService("window")).getDefaultDisplay().getRotation());
        }
    }
}
