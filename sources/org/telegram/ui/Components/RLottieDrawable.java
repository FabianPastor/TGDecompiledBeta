package org.telegram.ui.Components;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.View;
import java.io.File;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.DiscardPolicy;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLog;
import org.telegram.tgnet.ConnectionsManager;

public class RLottieDrawable extends BitmapDrawable implements Animatable {
    private static byte[] buffer = new byte[4096];
    private static HashMap<String, Runnable> cacheGenerateMap;
    private static ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(ConnectionsManager.CPU_COUNT, new DiscardPolicy());
    private static DispatchQueue lottieCacheGenerateQueue;
    private static byte[] readBuffer = new byte[65536];
    private static final Handler uiHandler = new Handler(Looper.getMainLooper());
    private boolean applyTransformation;
    private boolean applyingLayerColors;
    private boolean autoRepeat;
    private volatile Bitmap backgroundBitmap;
    private volatile Runnable cacheGenerateTask;
    private boolean cacheGenerateTaskIsRunning;
    private int currentFrame;
    private boolean decodeSingleFrame;
    private boolean decoderCreated;
    private boolean destroyWhenDone;
    private final Rect dstRect;
    private boolean forceFrameRedraw;
    private int height;
    private volatile boolean isRecycled;
    private volatile boolean isRunning;
    private long lastFrameTime;
    private Runnable loadFrameRunnable;
    private Runnable loadFrameTask;
    private final int[] metaData;
    private volatile long nativePtr;
    private boolean needGenerateCache;
    private volatile boolean nextFrameIsLast;
    private volatile Bitmap nextRenderingBitmap;
    private ArrayList<WeakReference<View>> parentViews;
    private volatile Bitmap renderingBitmap;
    private float scaleX;
    private float scaleY;
    private boolean shouldLimitFps;
    private boolean singleFrameDecoded;
    private int timeBetweenFrames;
    private Runnable uiRunnable;
    private Runnable uiRunnableLastFrame;
    private Runnable uiRunnableNoFrame;
    private int width;

    private static native long create(String str, int[] iArr, boolean z);

    private static native int createCache(long j, Bitmap bitmap, int i, int i2, int i3);

    private static native long createWithJson(String str, String str2, int[] iArr);

    private static native void destroy(long j);

    private static native int getFrame(long j, int i, Bitmap bitmap, int i2, int i3, int i4);

    private static native void setLayerColor(long j, String str, int i);

    public int getOpacity() {
        return -2;
    }

    private void decodeFrameFinishedInternal() {
        if (this.cacheGenerateTask == null && this.destroyWhenDone && this.nativePtr != 0) {
            destroy(this.nativePtr);
            this.nativePtr = 0;
        }
        if (this.nativePtr == 0) {
            recycleResources();
            return;
        }
        if (!hasParentView()) {
            stop();
        }
        scheduleNextGetFrame();
    }

    private void recycleResources() {
        if (this.renderingBitmap != null) {
            this.renderingBitmap.recycle();
            this.renderingBitmap = null;
        }
        if (this.backgroundBitmap != null) {
            this.backgroundBitmap.recycle();
            this.backgroundBitmap = null;
        }
    }

    public RLottieDrawable(File file, int i, int i2, boolean z, boolean z2) {
        this.metaData = new int[3];
        this.autoRepeat = true;
        this.scaleX = 1.0f;
        this.scaleY = 1.0f;
        this.dstRect = new Rect();
        this.parentViews = new ArrayList();
        this.uiRunnableNoFrame = new Runnable() {
            public void run() {
                RLottieDrawable.this.loadFrameTask = null;
                RLottieDrawable.this.decodeFrameFinishedInternal();
            }
        };
        this.uiRunnable = new Runnable() {
            public void run() {
                RLottieDrawable.this.singleFrameDecoded = true;
                RLottieDrawable.this.invalidateInternal();
                RLottieDrawable.this.decodeFrameFinishedInternal();
            }
        };
        this.uiRunnableLastFrame = new Runnable() {
            public void run() {
                RLottieDrawable.this.singleFrameDecoded = true;
                RLottieDrawable.this.isRunning = false;
                RLottieDrawable.this.invalidateInternal();
                RLottieDrawable.this.decodeFrameFinishedInternal();
            }
        };
        this.loadFrameRunnable = new Runnable() {
            public void run() {
                if (!RLottieDrawable.this.isRecycled) {
                    try {
                        if (RLottieDrawable.this.nativePtr != 0) {
                            if (RLottieDrawable.this.backgroundBitmap == null) {
                                RLottieDrawable.this.backgroundBitmap = Bitmap.createBitmap(RLottieDrawable.this.width, RLottieDrawable.this.height, Config.ARGB_8888);
                            }
                            if (RLottieDrawable.this.backgroundBitmap != null) {
                                if (RLottieDrawable.this.needGenerateCache) {
                                    DispatchQueue access$1200 = RLottieDrawable.lottieCacheGenerateQueue;
                                    RLottieDrawable rLottieDrawable = RLottieDrawable.this;
                                    -$$Lambda$RLottieDrawable$4$Btcsg2NvFiQMIYZqE06AIrxJah0 -__lambda_rlottiedrawable_4_btcsg2nvfiqmiyzqe06airxjah0 = new -$$Lambda$RLottieDrawable$4$Btcsg2NvFiQMIYZqE06AIrxJah0(this);
                                    rLottieDrawable.cacheGenerateTask = -__lambda_rlottiedrawable_4_btcsg2nvfiqmiyzqe06airxjah0;
                                    access$1200.postRunnable(-__lambda_rlottiedrawable_4_btcsg2nvfiqmiyzqe06airxjah0);
                                    AndroidUtilities.runOnUIThread(RLottieDrawable.this.uiRunnableNoFrame);
                                    RLottieDrawable.this.needGenerateCache = false;
                                    return;
                                }
                                RLottieDrawable.getFrame(RLottieDrawable.this.nativePtr, RLottieDrawable.this.currentFrame, RLottieDrawable.this.backgroundBitmap, RLottieDrawable.this.width, RLottieDrawable.this.height, RLottieDrawable.this.backgroundBitmap.getRowBytes());
                                int i = 2;
                                if (RLottieDrawable.this.metaData[2] != 0 && RLottieDrawable.this.cacheGenerateTask == null) {
                                    RLottieDrawable.this.needGenerateCache = true;
                                    RLottieDrawable.this.metaData[2] = 0;
                                }
                                RLottieDrawable.this.nextRenderingBitmap = RLottieDrawable.this.backgroundBitmap;
                                if (!RLottieDrawable.this.shouldLimitFps) {
                                    i = 1;
                                }
                                if (RLottieDrawable.this.currentFrame + i < RLottieDrawable.this.metaData[0]) {
                                    RLottieDrawable.this.currentFrame = RLottieDrawable.this.currentFrame + i;
                                    RLottieDrawable.this.nextFrameIsLast = false;
                                } else if (RLottieDrawable.this.autoRepeat) {
                                    RLottieDrawable.this.currentFrame = 0;
                                    RLottieDrawable.this.nextFrameIsLast = false;
                                } else {
                                    RLottieDrawable.this.nextFrameIsLast = true;
                                }
                            }
                        } else {
                            AndroidUtilities.runOnUIThread(RLottieDrawable.this.uiRunnableNoFrame);
                            return;
                        }
                    } catch (Throwable th) {
                        FileLog.e(th);
                    }
                }
                AndroidUtilities.runOnUIThread(RLottieDrawable.this.uiRunnable);
            }

            public /* synthetic */ void lambda$run$0$RLottieDrawable$4() {
                if (RLottieDrawable.this.cacheGenerateTask != null) {
                    RLottieDrawable.this.cacheGenerateTaskIsRunning = true;
                    RLottieDrawable.createCache(RLottieDrawable.this.nativePtr, RLottieDrawable.this.backgroundBitmap, RLottieDrawable.this.width, RLottieDrawable.this.height, RLottieDrawable.this.backgroundBitmap.getRowBytes());
                    RLottieDrawable.this.cacheGenerateTask = null;
                    AndroidUtilities.runOnUIThread(RLottieDrawable.this.uiRunnableNoFrame);
                    RLottieDrawable.this.cacheGenerateTaskIsRunning = false;
                }
            }
        };
        this.width = i;
        this.height = i2;
        this.shouldLimitFps = z2;
        getPaint().setFlags(2);
        this.nativePtr = create(file.getAbsolutePath(), this.metaData, z);
        if (z && lottieCacheGenerateQueue == null) {
            lottieCacheGenerateQueue = new DispatchQueue("lottieCacheGenerateQueue");
        }
        if (this.nativePtr == 0) {
            file.delete();
        }
        if (this.shouldLimitFps && this.metaData[1] < 60) {
            this.shouldLimitFps = false;
        }
        this.timeBetweenFrames = Math.max(this.shouldLimitFps ? 33 : 16, (int) (1000.0f / ((float) this.metaData[1])));
        this.decoderCreated = true;
    }

    public RLottieDrawable(int i, String str, int i2, int i3) {
        this(i, str, i2, i3, true);
    }

    public RLottieDrawable(int i, String str, int i2, int i3, boolean z) {
        this.metaData = new int[3];
        this.autoRepeat = true;
        this.scaleX = 1.0f;
        this.scaleY = 1.0f;
        this.dstRect = new Rect();
        this.parentViews = new ArrayList();
        this.uiRunnableNoFrame = /* anonymous class already generated */;
        this.uiRunnable = /* anonymous class already generated */;
        this.uiRunnableLastFrame = /* anonymous class already generated */;
        this.loadFrameRunnable = /* anonymous class already generated */;
        try {
            InputStream openRawResource = ApplicationLoader.applicationContext.getResources().openRawResource(i);
            int i4 = 0;
            while (true) {
                int read = openRawResource.read(buffer, 0, buffer.length);
                if (read <= 0) {
                    break;
                }
                int i5 = i4 + read;
                if (readBuffer.length < i5) {
                    byte[] bArr = new byte[(readBuffer.length * 2)];
                    System.arraycopy(readBuffer, 0, bArr, 0, i4);
                    readBuffer = bArr;
                }
                System.arraycopy(buffer, 0, readBuffer, i4, read);
                i4 = i5;
            }
            String str2 = new String(readBuffer, 0, i4);
            openRawResource.close();
            this.width = i2;
            this.height = i3;
            getPaint().setFlags(2);
            this.nativePtr = createWithJson(str2, str, this.metaData);
            this.timeBetweenFrames = Math.max(16, (int) (1000.0f / ((float) this.metaData[1])));
            this.decoderCreated = true;
            this.autoRepeat = false;
            if (z) {
                setAllowDecodeSingleFrame(true);
            }
        } catch (Throwable th) {
            FileLog.e(th);
        }
    }

    public void addParentView(View view) {
        if (view != null) {
            int i = 0;
            int size = this.parentViews.size();
            while (i < size) {
                if (((WeakReference) this.parentViews.get(i)).get() != view) {
                    if (((WeakReference) this.parentViews.get(i)).get() == null) {
                        this.parentViews.remove(i);
                        size--;
                        i--;
                    }
                    i++;
                } else {
                    return;
                }
            }
            this.parentViews.add(new WeakReference(view));
        }
    }

    public void removeParentView(View view) {
        if (view != null) {
            int i = 0;
            int size = this.parentViews.size();
            while (i < size) {
                View view2 = (View) ((WeakReference) this.parentViews.get(i)).get();
                if (view2 == view || view2 == null) {
                    this.parentViews.remove(i);
                    size--;
                    i--;
                }
                i++;
            }
        }
    }

    private boolean hasParentView() {
        if (getCallback() != null) {
            return true;
        }
        for (int size = this.parentViews.size(); size > 0; size--) {
            if (((View) ((WeakReference) this.parentViews.get(0)).get()) != null) {
                return true;
            }
            this.parentViews.remove(0);
        }
        return false;
    }

    private void invalidateInternal() {
        int size = this.parentViews.size();
        int i = 0;
        while (i < size) {
            View view = (View) ((WeakReference) this.parentViews.get(i)).get();
            if (view != null) {
                view.invalidate();
            } else {
                this.parentViews.remove(i);
                size--;
                i--;
            }
            i++;
        }
        if (getCallback() != null) {
            invalidateSelf();
        }
    }

    public void setAllowDecodeSingleFrame(boolean z) {
        this.decodeSingleFrame = z;
        if (this.decodeSingleFrame) {
            scheduleNextGetFrame();
        }
    }

    public void beginApplyLayerColors() {
        this.applyingLayerColors = true;
    }

    public void commitApplyLayerColors() {
        if (this.applyingLayerColors) {
            this.applyingLayerColors = false;
            if (!this.isRunning && this.decodeSingleFrame) {
                resetCurrentFrame();
                this.nextFrameIsLast = false;
                this.singleFrameDecoded = false;
                if (!scheduleNextGetFrame()) {
                    this.forceFrameRedraw = true;
                }
            }
        }
    }

    public void recycle() {
        this.isRunning = false;
        this.isRecycled = true;
        Runnable runnable = this.cacheGenerateTask;
        if (!(runnable == null || this.cacheGenerateTaskIsRunning)) {
            lottieCacheGenerateQueue.cancelRunnable(runnable);
            this.cacheGenerateTask = null;
        }
        if (this.loadFrameTask == null && this.cacheGenerateTask == null) {
            if (this.nativePtr != 0) {
                destroy(this.nativePtr);
                this.nativePtr = 0;
            }
            recycleResources();
            return;
        }
        this.destroyWhenDone = true;
    }

    protected static void runOnUiThread(Runnable runnable) {
        if (Looper.myLooper() == uiHandler.getLooper()) {
            runnable.run();
        } else {
            uiHandler.post(runnable);
        }
    }

    public void setAutoRepeat(boolean z) {
        this.autoRepeat = z;
    }

    /* Access modifiers changed, original: protected */
    public void finalize() throws Throwable {
        try {
            recycle();
        } finally {
            super.finalize();
        }
    }

    public void start() {
        if (!this.isRunning) {
            this.isRunning = true;
            scheduleNextGetFrame();
            invalidateInternal();
        }
    }

    private void resetCurrentFrame() {
        if (this.currentFrame <= 2) {
            if (this.nextRenderingBitmap != null) {
                this.backgroundBitmap = this.renderingBitmap;
                this.renderingBitmap = this.nextRenderingBitmap;
            }
            this.loadFrameTask = null;
            this.currentFrame = 0;
        }
    }

    public void setLayerColor(String str, int i) {
        if (this.nativePtr != 0) {
            setLayerColor(this.nativePtr, str, i);
            if (!(this.applyingLayerColors || this.isRunning || !this.decodeSingleFrame)) {
                resetCurrentFrame();
                this.nextFrameIsLast = false;
                this.singleFrameDecoded = false;
                if (!scheduleNextGetFrame()) {
                    this.forceFrameRedraw = true;
                }
            }
            invalidateSelf();
        }
    }

    /* JADX WARNING: Missing block: B:18:0x0028, code skipped:
            if (r5.singleFrameDecoded == false) goto L_0x002b;
     */
    private boolean scheduleNextGetFrame() {
        /*
        r5 = this;
        r0 = r5.cacheGenerateTask;
        if (r0 != 0) goto L_0x0038;
    L_0x0004:
        r0 = r5.nextRenderingBitmap;
        if (r0 != 0) goto L_0x0038;
    L_0x0008:
        r0 = r5.loadFrameTask;
        if (r0 != 0) goto L_0x0038;
    L_0x000c:
        r0 = r5.nativePtr;
        r2 = 0;
        r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));
        if (r4 != 0) goto L_0x0018;
    L_0x0014:
        r0 = r5.decoderCreated;
        if (r0 != 0) goto L_0x0038;
    L_0x0018:
        r0 = r5.destroyWhenDone;
        if (r0 != 0) goto L_0x0038;
    L_0x001c:
        r0 = r5.isRunning;
        if (r0 != 0) goto L_0x002b;
    L_0x0020:
        r0 = r5.decodeSingleFrame;
        if (r0 == 0) goto L_0x0038;
    L_0x0024:
        if (r0 == 0) goto L_0x002b;
    L_0x0026:
        r0 = r5.singleFrameDecoded;
        if (r0 == 0) goto L_0x002b;
    L_0x002a:
        goto L_0x0038;
    L_0x002b:
        r0 = executor;
        r1 = r5.loadFrameRunnable;
        r5.loadFrameTask = r1;
        r4 = java.util.concurrent.TimeUnit.MILLISECONDS;
        r0.schedule(r1, r2, r4);
        r0 = 1;
        return r0;
    L_0x0038:
        r0 = 0;
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.RLottieDrawable.scheduleNextGetFrame():boolean");
    }

    public void stop() {
        this.isRunning = false;
    }

    public void setProgress(float f) {
        if (f < 0.0f) {
            f = 0.0f;
        } else if (f > 1.0f) {
            f = 1.0f;
        }
        this.currentFrame = (int) (((float) this.metaData[0]) * f);
        this.nextFrameIsLast = false;
        invalidateSelf();
    }

    public void setCurrentFrame(int i) {
        this.currentFrame = i;
        this.nextFrameIsLast = false;
        invalidateSelf();
    }

    public boolean isRunning() {
        return this.isRunning;
    }

    public int getIntrinsicHeight() {
        return this.height;
    }

    public int getIntrinsicWidth() {
        return this.width;
    }

    /* Access modifiers changed, original: protected */
    public void onBoundsChange(Rect rect) {
        super.onBoundsChange(rect);
        this.applyTransformation = true;
    }

    public void draw(Canvas canvas) {
        if ((this.nativePtr != 0 || !this.decoderCreated) && !this.destroyWhenDone) {
            long uptimeMillis = SystemClock.uptimeMillis();
            long abs = Math.abs(uptimeMillis - this.lastFrameTime);
            if (this.isRunning) {
                if (this.renderingBitmap == null && this.nextRenderingBitmap == null) {
                    scheduleNextGetFrame();
                } else if (this.nextRenderingBitmap != null && (this.renderingBitmap == null || abs >= ((long) (this.timeBetweenFrames - 6)))) {
                    this.backgroundBitmap = this.renderingBitmap;
                    this.renderingBitmap = this.nextRenderingBitmap;
                    if (this.nextFrameIsLast) {
                        stop();
                    }
                    this.loadFrameTask = null;
                    this.singleFrameDecoded = true;
                    this.nextRenderingBitmap = null;
                    this.lastFrameTime = uptimeMillis;
                    scheduleNextGetFrame();
                }
            } else if (this.decodeSingleFrame && abs >= ((long) (this.timeBetweenFrames - 6)) && this.nextRenderingBitmap != null) {
                this.backgroundBitmap = this.renderingBitmap;
                this.renderingBitmap = this.nextRenderingBitmap;
                this.loadFrameTask = null;
                this.singleFrameDecoded = true;
                this.nextRenderingBitmap = null;
                this.lastFrameTime = uptimeMillis;
                if (this.forceFrameRedraw) {
                    this.singleFrameDecoded = false;
                    this.forceFrameRedraw = false;
                }
                scheduleNextGetFrame();
            }
            if (this.renderingBitmap != null) {
                if (this.applyTransformation) {
                    this.dstRect.set(getBounds());
                    this.scaleX = ((float) this.dstRect.width()) / ((float) this.width);
                    this.scaleY = ((float) this.dstRect.height()) / ((float) this.height);
                    this.applyTransformation = false;
                }
                Rect rect = this.dstRect;
                canvas.translate((float) rect.left, (float) rect.top);
                canvas.scale(this.scaleX, this.scaleY);
                canvas.drawBitmap(this.renderingBitmap, 0.0f, 0.0f, getPaint());
                if (this.isRunning) {
                    invalidateInternal();
                }
            }
        }
    }

    public int getMinimumHeight() {
        return this.height;
    }

    public int getMinimumWidth() {
        return this.width;
    }

    public Bitmap getRenderingBitmap() {
        return this.renderingBitmap;
    }

    public Bitmap getNextRenderingBitmap() {
        return this.nextRenderingBitmap;
    }

    public Bitmap getBackgroundBitmap() {
        return this.backgroundBitmap;
    }

    public Bitmap getAnimatedBitmap() {
        if (this.renderingBitmap != null) {
            return this.renderingBitmap;
        }
        return this.nextRenderingBitmap != null ? this.nextRenderingBitmap : null;
    }

    public boolean hasBitmap() {
        return (this.nativePtr == 0 || (this.renderingBitmap == null && this.nextRenderingBitmap == null)) ? false : true;
    }
}
