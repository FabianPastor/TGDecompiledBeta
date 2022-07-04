package org.telegram.ui.Components.Premium.GLIcon;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.opengl.GLUtils;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import java.util.ArrayList;
import java.util.Collections;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL10;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.EmuDetector;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.Utilities;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.Premium.StarParticlesView;

public class GLIconTextureView extends TextureView implements TextureView.SurfaceTextureListener {
    private static final int EGL_CONTEXT_CLIENT_VERSION = 12440;
    private static final int EGL_OPENGL_ES2_BIT = 4;
    ArrayList<Integer> animationIndexes = new ArrayList<>();
    int animationPointer;
    private final int animationsCount = 5;
    AnimatorSet animatorSet = new AnimatorSet();
    boolean attached;
    ValueAnimator backAnimation;
    private boolean dialogIsVisible = false;
    private EGLConfig eglConfig;
    GestureDetector gestureDetector;
    Runnable idleAnimation = new Runnable() {
        public void run() {
            if ((GLIconTextureView.this.animatorSet == null || !GLIconTextureView.this.animatorSet.isRunning()) && (GLIconTextureView.this.backAnimation == null || !GLIconTextureView.this.backAnimation.isRunning())) {
                GLIconTextureView.this.startIdleAnimation();
                return;
            }
            GLIconTextureView gLIconTextureView = GLIconTextureView.this;
            gLIconTextureView.scheduleIdleAnimation(gLIconTextureView.idleDelay);
        }
    };
    /* access modifiers changed from: private */
    public long idleDelay = 2000;
    public boolean isRunning = false;
    private EGL10 mEgl;
    private EGLContext mEglContext;
    private EGLDisplay mEglDisplay;
    private EGLSurface mEglSurface;
    private GL10 mGl;
    public GLIconRenderer mRenderer;
    private SurfaceTexture mSurface;
    private boolean paused = true;
    /* access modifiers changed from: private */
    public boolean rendererChanged = false;
    StarParticlesView starParticlesView;
    private int surfaceHeight;
    private int surfaceWidth;
    private int targetFps;
    /* access modifiers changed from: private */
    public int targetFrameDurationMillis;
    private RenderThread thread;
    public boolean touched;
    ValueAnimator.AnimatorUpdateListener xUpdater = new GLIconTextureView$$ExternalSyntheticLambda1(this);
    ValueAnimator.AnimatorUpdateListener xUpdater2 = new GLIconTextureView$$ExternalSyntheticLambda0(this);
    ValueAnimator.AnimatorUpdateListener yUpdater = new GLIconTextureView$$ExternalSyntheticLambda2(this);

    public GLIconTextureView(Context context, int style) {
        super(context);
        setOpaque(false);
        setRenderer(new GLIconRenderer(context, style));
        initialize(context);
        GestureDetector gestureDetector2 = new GestureDetector(context, new GestureDetector.OnGestureListener() {
            public boolean onDown(MotionEvent motionEvent) {
                if (GLIconTextureView.this.backAnimation != null) {
                    GLIconTextureView.this.backAnimation.removeAllListeners();
                    GLIconTextureView.this.backAnimation.cancel();
                    GLIconTextureView.this.backAnimation = null;
                }
                if (GLIconTextureView.this.animatorSet != null) {
                    GLIconTextureView.this.animatorSet.removeAllListeners();
                    GLIconTextureView.this.animatorSet.cancel();
                    GLIconTextureView.this.animatorSet = null;
                }
                AndroidUtilities.cancelRunOnUIThread(GLIconTextureView.this.idleAnimation);
                GLIconTextureView.this.touched = true;
                return true;
            }

            public void onShowPress(MotionEvent motionEvent) {
            }

            public boolean onSingleTapUp(MotionEvent motionEvent) {
                float rad = ((float) GLIconTextureView.this.getMeasuredWidth()) / 2.0f;
                AndroidUtilities.runOnUIThread(new GLIconTextureView$1$$ExternalSyntheticLambda0(this, (((float) (Utilities.random.nextInt(30) + 40)) * (rad - motionEvent.getX())) / rad, (((float) (Utilities.random.nextInt(30) + 40)) * (rad - motionEvent.getY())) / rad), 16);
                return true;
            }

            /* renamed from: lambda$onSingleTapUp$0$org-telegram-ui-Components-Premium-GLIcon-GLIconTextureView$1  reason: not valid java name */
            public /* synthetic */ void m1236x35462var_(float toAngleX, float toAngleY) {
                if (GLIconTextureView.this.backAnimation != null) {
                    GLIconTextureView.this.backAnimation.removeAllListeners();
                    GLIconTextureView.this.backAnimation.cancel();
                    GLIconTextureView.this.backAnimation = null;
                }
                if (GLIconTextureView.this.animatorSet != null) {
                    GLIconTextureView.this.animatorSet.removeAllListeners();
                    GLIconTextureView.this.animatorSet.cancel();
                    GLIconTextureView.this.animatorSet = null;
                }
                if (Math.abs(GLIconTextureView.this.mRenderer.angleX) > 10.0f) {
                    GLIconTextureView.this.startBackAnimation();
                    return;
                }
                AndroidUtilities.cancelRunOnUIThread(GLIconTextureView.this.idleAnimation);
                GLIconTextureView.this.animatorSet = new AnimatorSet();
                ValueAnimator v1 = ValueAnimator.ofFloat(new float[]{GLIconTextureView.this.mRenderer.angleX, toAngleX});
                v1.addUpdateListener(GLIconTextureView.this.xUpdater);
                v1.setDuration((long) 220);
                v1.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
                ValueAnimator v2 = ValueAnimator.ofFloat(new float[]{toAngleX, 0.0f});
                v2.addUpdateListener(GLIconTextureView.this.xUpdater);
                v2.setStartDelay((long) 220);
                v2.setDuration(600);
                v2.setInterpolator(AndroidUtilities.overshootInterpolator);
                ValueAnimator v3 = ValueAnimator.ofFloat(new float[]{GLIconTextureView.this.mRenderer.angleY, toAngleY});
                v3.addUpdateListener(GLIconTextureView.this.yUpdater);
                v3.setDuration((long) 220);
                v3.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
                ValueAnimator v4 = ValueAnimator.ofFloat(new float[]{toAngleY, 0.0f});
                v4.addUpdateListener(GLIconTextureView.this.yUpdater);
                v4.setStartDelay((long) 220);
                v4.setDuration(600);
                v4.setInterpolator(AndroidUtilities.overshootInterpolator);
                GLIconTextureView.this.animatorSet.playTogether(new Animator[]{v1, v2, v3, v4});
                GLIconTextureView.this.animatorSet.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        GLIconTextureView.this.mRenderer.angleX = 0.0f;
                        GLIconTextureView.this.animatorSet = null;
                        GLIconTextureView.this.scheduleIdleAnimation(GLIconTextureView.this.idleDelay);
                    }
                });
                GLIconTextureView.this.animatorSet.start();
            }

            public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
                GLIconTextureView.this.mRenderer.angleX += 0.5f * v;
                GLIconTextureView.this.mRenderer.angleY += 0.05f * v1;
                return true;
            }

            public void onLongPress(MotionEvent motionEvent) {
                GLIconTextureView.this.onLongPress();
            }

            public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
                return false;
            }
        });
        this.gestureDetector = gestureDetector2;
        gestureDetector2.setIsLongpressEnabled(true);
        for (int i = 0; i < 5; i++) {
            this.animationIndexes.add(Integer.valueOf(i));
        }
        Collections.shuffle(this.animationIndexes);
    }

    public void onLongPress() {
    }

    public synchronized void setRenderer(GLIconRenderer renderer) {
        this.mRenderer = renderer;
        this.rendererChanged = true;
    }

    private void initialize(Context context) {
        this.targetFps = (int) AndroidUtilities.screenRefreshRate;
        setSurfaceTextureListener(this);
    }

    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        startThread(surface, width, height);
    }

    public void startThread(SurfaceTexture surface, int width, int height) {
        this.thread = new RenderThread();
        this.mSurface = surface;
        setDimensions(width, height);
        this.targetFrameDurationMillis = Math.max(0, ((int) ((1.0f / ((float) this.targetFps)) * 1000.0f)) - 1);
        this.thread.start();
    }

    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        setDimensions(width, height);
        GLIconRenderer gLIconRenderer = this.mRenderer;
        if (gLIconRenderer != null) {
            gLIconRenderer.onSurfaceChanged(this.mGl, width, height);
        }
    }

    public synchronized void setPaused(boolean isPaused) {
        this.paused = isPaused;
    }

    public synchronized boolean isPaused() {
        return this.paused;
    }

    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        stopThread();
        return false;
    }

    public void stopThread() {
        RenderThread renderThread = this.thread;
        if (renderThread != null) {
            this.isRunning = false;
            try {
                renderThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.thread = null;
        }
    }

    /* access modifiers changed from: private */
    public boolean shouldSleep() {
        return isPaused() || this.mRenderer == null;
    }

    public void setBackgroundBitmap(Bitmap gradientTextureBitmap) {
        this.mRenderer.setBackground(gradientTextureBitmap);
    }

    private class RenderThread extends Thread {
        private RenderThread() {
        }

        public void run() {
            GLIconTextureView.this.isRunning = true;
            GLIconTextureView.this.initGL();
            GLIconTextureView.this.checkGlError();
            long lastFrameTime = System.currentTimeMillis();
            while (GLIconTextureView.this.isRunning) {
                while (GLIconTextureView.this.mRenderer == null) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                    }
                }
                if (GLIconTextureView.this.rendererChanged) {
                    GLIconTextureView gLIconTextureView = GLIconTextureView.this;
                    gLIconTextureView.initializeRenderer(gLIconTextureView.mRenderer);
                    boolean unused = GLIconTextureView.this.rendererChanged = false;
                }
                if (!GLIconTextureView.this.shouldSleep()) {
                    lastFrameTime = System.currentTimeMillis();
                    GLIconTextureView.this.drawSingleFrame();
                }
                try {
                    if (GLIconTextureView.this.shouldSleep()) {
                        Thread.sleep(100);
                    } else {
                        for (long thisFrameTime = System.currentTimeMillis(); thisFrameTime - lastFrameTime < ((long) GLIconTextureView.this.targetFrameDurationMillis); thisFrameTime = System.currentTimeMillis()) {
                        }
                    }
                } catch (InterruptedException e2) {
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public synchronized void initializeRenderer(GLIconRenderer renderer) {
        if (renderer != null) {
            if (this.isRunning) {
                renderer.onSurfaceCreated(this.mGl, this.eglConfig);
                renderer.onSurfaceChanged(this.mGl, this.surfaceWidth, this.surfaceHeight);
            }
        }
    }

    /* access modifiers changed from: private */
    public synchronized void drawSingleFrame() {
        checkCurrent();
        GLIconRenderer gLIconRenderer = this.mRenderer;
        if (gLIconRenderer != null) {
            gLIconRenderer.onDrawFrame(this.mGl);
        }
        checkGlError();
        this.mEgl.eglSwapBuffers(this.mEglDisplay, this.mEglSurface);
    }

    public void setDimensions(int width, int height) {
        this.surfaceWidth = width;
        this.surfaceHeight = height;
    }

    private void checkCurrent() {
        if (!this.mEglContext.equals(this.mEgl.eglGetCurrentContext()) || !this.mEglSurface.equals(this.mEgl.eglGetCurrentSurface(12377))) {
            checkEglError();
            EGL10 egl10 = this.mEgl;
            EGLDisplay eGLDisplay = this.mEglDisplay;
            EGLSurface eGLSurface = this.mEglSurface;
            if (egl10.eglMakeCurrent(eGLDisplay, eGLSurface, eGLSurface, this.mEglContext)) {
                checkEglError();
                return;
            }
            throw new RuntimeException("eglMakeCurrent failed " + GLUtils.getEGLErrorString(this.mEgl.eglGetError()));
        }
    }

    private void checkEglError() {
        if (this.mEgl.eglGetError() != 12288) {
            FileLog.e("cannot swap buffers!");
        }
    }

    /* access modifiers changed from: private */
    public void checkGlError() {
        int error = this.mGl.glGetError();
        if (error != 0) {
            FileLog.e("GL error = 0x" + Integer.toHexString(error));
        }
    }

    /* access modifiers changed from: private */
    public void initGL() {
        int[] configSpec;
        EGL10 egl10 = (EGL10) EGLContext.getEGL();
        this.mEgl = egl10;
        EGLDisplay eglGetDisplay = egl10.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
        this.mEglDisplay = eglGetDisplay;
        if (eglGetDisplay != EGL10.EGL_NO_DISPLAY) {
            if (this.mEgl.eglInitialize(this.mEglDisplay, new int[2])) {
                int[] configsCount = new int[1];
                EGLConfig[] configs = new EGLConfig[1];
                if (EmuDetector.with(getContext()).detect()) {
                    configSpec = new int[]{12324, 8, 12323, 8, 12322, 8, 12321, 8, 12325, 16, 12344};
                } else {
                    configSpec = new int[]{12352, 4, 12324, 8, 12323, 8, 12322, 8, 12321, 8, 12325, 16, 12326, 0, 12338, 1, 12344};
                }
                this.eglConfig = null;
                if (this.mEgl.eglChooseConfig(this.mEglDisplay, configSpec, configs, 1, configsCount)) {
                    if (configsCount[0] > 0) {
                        this.eglConfig = configs[0];
                    }
                    EGLConfig eGLConfig = this.eglConfig;
                    if (eGLConfig != null) {
                        this.mEglContext = this.mEgl.eglCreateContext(this.mEglDisplay, eGLConfig, EGL10.EGL_NO_CONTEXT, new int[]{12440, 2, 12344});
                        checkEglError();
                        this.mEglSurface = this.mEgl.eglCreateWindowSurface(this.mEglDisplay, this.eglConfig, this.mSurface, (int[]) null);
                        checkEglError();
                        EGLSurface eGLSurface = this.mEglSurface;
                        if (eGLSurface == null || eGLSurface == EGL10.EGL_NO_SURFACE) {
                            int error = this.mEgl.eglGetError();
                            if (error == 12299) {
                                FileLog.e("eglCreateWindowSurface returned EGL10.EGL_BAD_NATIVE_WINDOW");
                                return;
                            }
                            throw new RuntimeException("eglCreateWindowSurface failed " + GLUtils.getEGLErrorString(error));
                        }
                        EGL10 egl102 = this.mEgl;
                        EGLDisplay eGLDisplay = this.mEglDisplay;
                        EGLSurface eGLSurface2 = this.mEglSurface;
                        if (egl102.eglMakeCurrent(eGLDisplay, eGLSurface2, eGLSurface2, this.mEglContext)) {
                            checkEglError();
                            this.mGl = (GL10) this.mEglContext.getGL();
                            checkEglError();
                            return;
                        }
                        throw new RuntimeException("eglMakeCurrent failed " + GLUtils.getEGLErrorString(this.mEgl.eglGetError()));
                    }
                    throw new RuntimeException("eglConfig not initialized");
                }
                throw new IllegalArgumentException("eglChooseConfig failed " + GLUtils.getEGLErrorString(this.mEgl.eglGetError()));
            }
            throw new RuntimeException("eglInitialize failed " + GLUtils.getEGLErrorString(this.mEgl.eglGetError()));
        }
        throw new RuntimeException("eglGetDisplay failed " + GLUtils.getEGLErrorString(this.mEgl.eglGetError()));
    }

    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == 0) {
            getParent().requestDisallowInterceptTouchEvent(true);
        } else if (event.getAction() == 3 || event.getAction() == 1) {
            this.touched = false;
            startBackAnimation();
            getParent().requestDisallowInterceptTouchEvent(false);
        }
        return this.gestureDetector.onTouchEvent(event);
    }

    /* access modifiers changed from: private */
    public void startBackAnimation() {
        cancelAnimatons();
        float fromX = this.mRenderer.angleX;
        float fromY = this.mRenderer.angleY;
        float fromX2 = this.mRenderer.angleX2;
        float sum = fromX + fromY;
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{1.0f, 0.0f});
        this.backAnimation = ofFloat;
        ofFloat.addUpdateListener(new GLIconTextureView$$ExternalSyntheticLambda3(this, fromX, fromX2, fromY));
        this.backAnimation.setDuration(600);
        this.backAnimation.setInterpolator(new OvershootInterpolator());
        this.backAnimation.start();
        StarParticlesView starParticlesView2 = this.starParticlesView;
        if (starParticlesView2 != null) {
            starParticlesView2.flingParticles(Math.abs(sum));
        }
        scheduleIdleAnimation(this.idleDelay);
    }

    /* renamed from: lambda$startBackAnimation$0$org-telegram-ui-Components-Premium-GLIcon-GLIconTextureView  reason: not valid java name */
    public /* synthetic */ void m1235x94cc1daa(float fromX, float fromX2, float fromY, ValueAnimator valueAnimator) {
        float v = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.mRenderer.angleX = v * fromX;
        this.mRenderer.angleX2 = v * fromX2;
        this.mRenderer.angleY = v * fromY;
    }

    private void cancelAnimatons() {
        ValueAnimator valueAnimator = this.backAnimation;
        if (valueAnimator != null) {
            valueAnimator.removeAllListeners();
            this.backAnimation.cancel();
            this.backAnimation = null;
        }
        AnimatorSet animatorSet2 = this.animatorSet;
        if (animatorSet2 != null) {
            animatorSet2.removeAllListeners();
            this.animatorSet.cancel();
            this.animatorSet = null;
        }
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.attached = true;
        this.rendererChanged = true;
        scheduleIdleAnimation(this.idleDelay);
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        cancelAnimatons();
        this.attached = false;
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-Premium-GLIcon-GLIconTextureView  reason: not valid java name */
    public /* synthetic */ void m1232x3ddf7var_(ValueAnimator valueAnimator) {
        this.mRenderer.angleX2 = ((Float) valueAnimator.getAnimatedValue()).floatValue();
    }

    /* renamed from: lambda$new$2$org-telegram-ui-Components-Premium-GLIcon-GLIconTextureView  reason: not valid java name */
    public /* synthetic */ void m1233xvar_var_(ValueAnimator valueAnimator) {
        this.mRenderer.angleX = ((Float) valueAnimator.getAnimatedValue()).floatValue();
    }

    /* renamed from: lambda$new$3$org-telegram-ui-Components-Premium-GLIcon-GLIconTextureView  reason: not valid java name */
    public /* synthetic */ void m1234xb2caCLASSNAME(ValueAnimator valueAnimator) {
        this.mRenderer.angleY = ((Float) valueAnimator.getAnimatedValue()).floatValue();
    }

    /* access modifiers changed from: private */
    public void scheduleIdleAnimation(long time) {
        AndroidUtilities.cancelRunOnUIThread(this.idleAnimation);
        if (!this.dialogIsVisible) {
            AndroidUtilities.runOnUIThread(this.idleAnimation, time);
        }
    }

    /* access modifiers changed from: private */
    public void startIdleAnimation() {
        if (this.attached) {
            int i = this.animationIndexes.get(this.animationPointer).intValue();
            int i2 = this.animationPointer + 1;
            this.animationPointer = i2;
            if (i2 >= this.animationIndexes.size()) {
                Collections.shuffle(this.animationIndexes);
                this.animationPointer = 0;
            }
            if (i == 0) {
                pullAnimation();
            } else if (i == 1) {
                slowFlipAination();
            } else if (i == 2) {
                sleepAnimation();
            } else {
                flipAnimation();
            }
        }
    }

    private void slowFlipAination() {
        this.animatorSet = new AnimatorSet();
        ValueAnimator v1 = ValueAnimator.ofFloat(new float[]{this.mRenderer.angleX, 360.0f});
        v1.addUpdateListener(this.xUpdater);
        v1.setDuration(8000);
        v1.setInterpolator(CubicBezierInterpolator.DEFAULT);
        this.animatorSet.playTogether(new Animator[]{v1});
        this.animatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                GLIconTextureView.this.mRenderer.angleX = 0.0f;
                GLIconTextureView.this.animatorSet = null;
                GLIconTextureView gLIconTextureView = GLIconTextureView.this;
                gLIconTextureView.scheduleIdleAnimation(gLIconTextureView.idleDelay);
            }
        });
        this.animatorSet.start();
    }

    private void pullAnimation() {
        int i = Math.abs(Utilities.random.nextInt() % 4);
        this.animatorSet = new AnimatorSet();
        if (i == 0) {
            ValueAnimator v1 = ValueAnimator.ofFloat(new float[]{this.mRenderer.angleY, (float) 48});
            v1.addUpdateListener(this.yUpdater);
            v1.setDuration(2300);
            v1.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
            ValueAnimator v2 = ValueAnimator.ofFloat(new float[]{(float) 48, 0.0f});
            v2.addUpdateListener(this.yUpdater);
            v2.setDuration(500);
            v2.setStartDelay(2300);
            v2.setInterpolator(AndroidUtilities.overshootInterpolator);
            this.animatorSet.playTogether(new Animator[]{v1, v2});
        } else {
            int a = 485;
            if (i == 2) {
                a = -485;
            }
            ValueAnimator v12 = ValueAnimator.ofFloat(new float[]{this.mRenderer.angleY, (float) a});
            v12.addUpdateListener(this.xUpdater);
            v12.setDuration(3000);
            v12.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
            ValueAnimator v22 = ValueAnimator.ofFloat(new float[]{(float) a, 0.0f});
            v22.addUpdateListener(this.xUpdater);
            v22.setDuration(1000);
            v22.setStartDelay(3000);
            v22.setInterpolator(AndroidUtilities.overshootInterpolator);
            this.animatorSet.playTogether(new Animator[]{v12, v22});
        }
        this.animatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                GLIconTextureView.this.mRenderer.angleX = 0.0f;
                GLIconTextureView.this.animatorSet = null;
                GLIconTextureView gLIconTextureView = GLIconTextureView.this;
                gLIconTextureView.scheduleIdleAnimation(gLIconTextureView.idleDelay);
            }
        });
        this.animatorSet.start();
    }

    private void flipAnimation() {
        this.animatorSet = new AnimatorSet();
        ValueAnimator v1 = ValueAnimator.ofFloat(new float[]{this.mRenderer.angleX, 180.0f});
        v1.addUpdateListener(this.xUpdater);
        v1.setDuration(600);
        v1.setInterpolator(CubicBezierInterpolator.DEFAULT);
        ValueAnimator v2 = ValueAnimator.ofFloat(new float[]{180.0f, 360.0f});
        v2.addUpdateListener(this.xUpdater);
        v2.setDuration(600);
        v2.setStartDelay(2000);
        v2.setInterpolator(CubicBezierInterpolator.DEFAULT);
        this.animatorSet.playTogether(new Animator[]{v1, v2});
        this.animatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                GLIconTextureView.this.mRenderer.angleX = 0.0f;
                GLIconTextureView.this.animatorSet = null;
                GLIconTextureView gLIconTextureView = GLIconTextureView.this;
                gLIconTextureView.scheduleIdleAnimation(gLIconTextureView.idleDelay);
            }
        });
        this.animatorSet.start();
    }

    private void sleepAnimation() {
        this.animatorSet = new AnimatorSet();
        ValueAnimator v1 = ValueAnimator.ofFloat(new float[]{this.mRenderer.angleX, 184.0f});
        v1.addUpdateListener(this.xUpdater);
        v1.setDuration(600);
        v1.setInterpolator(CubicBezierInterpolator.EASE_OUT);
        ValueAnimator v2 = ValueAnimator.ofFloat(new float[]{this.mRenderer.angleY, 50.0f});
        v2.addUpdateListener(this.yUpdater);
        v2.setDuration(600);
        v2.setInterpolator(CubicBezierInterpolator.EASE_OUT);
        ValueAnimator v3 = ValueAnimator.ofFloat(new float[]{180.0f, 0.0f});
        v3.addUpdateListener(this.xUpdater);
        v3.setDuration(800);
        v3.setStartDelay(10000);
        v3.setInterpolator(AndroidUtilities.overshootInterpolator);
        ValueAnimator v4 = ValueAnimator.ofFloat(new float[]{60.0f, 0.0f});
        v4.addUpdateListener(this.yUpdater);
        v4.setDuration(800);
        v4.setStartDelay(10000);
        v4.setInterpolator(AndroidUtilities.overshootInterpolator);
        ValueAnimator v5 = ValueAnimator.ofFloat(new float[]{0.0f, 2.0f, -3.0f, 2.0f, -1.0f, 2.0f, -3.0f, 2.0f, -1.0f, 0.0f});
        v5.addUpdateListener(this.xUpdater2);
        v5.setDuration(10000);
        v5.setInterpolator(new LinearInterpolator());
        this.animatorSet.playTogether(new Animator[]{v1, v2, v3, v4, v5});
        this.animatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                GLIconTextureView.this.mRenderer.angleX = 0.0f;
                GLIconTextureView.this.animatorSet = null;
                GLIconTextureView gLIconTextureView = GLIconTextureView.this;
                gLIconTextureView.scheduleIdleAnimation(gLIconTextureView.idleDelay);
            }
        });
        this.animatorSet.start();
    }

    public void setStarParticlesView(StarParticlesView starParticlesView2) {
        this.starParticlesView = starParticlesView2;
    }

    public void startEnterAnimation(int angle, long delay) {
        GLIconRenderer gLIconRenderer = this.mRenderer;
        if (gLIconRenderer != null) {
            gLIconRenderer.angleX = -180.0f;
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    GLIconTextureView.this.startBackAnimation();
                }
            }, delay);
        }
    }

    public void setDialogVisible(boolean isVisible) {
        this.dialogIsVisible = isVisible;
        if (isVisible) {
            AndroidUtilities.cancelRunOnUIThread(this.idleAnimation);
            startBackAnimation();
            return;
        }
        scheduleIdleAnimation(this.idleDelay);
    }
}
