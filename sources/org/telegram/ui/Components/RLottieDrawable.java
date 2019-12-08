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
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;

public class RLottieDrawable extends BitmapDrawable implements Animatable {
    private static byte[] buffer = new byte[4096];
    private static ExecutorService loadFrameRunnableQueue = Executors.newCachedThreadPool();
    private static ThreadPoolExecutor lottieCacheGenerateQueue;
    private static byte[] readBuffer = new byte[65536];
    private static final Handler uiHandler = new Handler(Looper.getMainLooper());
    private boolean applyTransformation;
    private boolean applyingLayerColors;
    private boolean autoRepeat;
    private volatile Bitmap backgroundBitmap;
    private Runnable cacheGenerateTask;
    private int currentFrame;
    private View currentParentView;
    private boolean decodeSingleFrame;
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
    private HashMap<String, Integer> newColorUpdates;
    private volatile boolean nextFrameIsLast;
    private volatile Bitmap nextRenderingBitmap;
    private ArrayList<WeakReference<View>> parentViews;
    private volatile HashMap<String, Integer> pendingColorUpdates;
    private volatile Bitmap renderingBitmap;
    private float scaleX;
    private float scaleY;
    private boolean shouldLimitFps;
    private boolean singleFrameDecoded;
    private int timeBetweenFrames;
    private Runnable uiRunnable;
    private Runnable uiRunnableCacheFinished;
    private Runnable uiRunnableGenerateCache;
    private Runnable uiRunnableLastFrame;
    private Runnable uiRunnableNoFrame;
    private int width;

    private static native long create(String str, int[] iArr, boolean z);

    private static native void createCache(long j, Bitmap bitmap, int i, int i2, int i3);

    private static native long createWithJson(String str, String str2, int[] iArr);

    private static native void destroy(long j);

    private static native int getFrame(long j, int i, Bitmap bitmap, int i2, int i3, int i4);

    private static native void setLayerColor(long j, String str, int i);

    public int getOpacity() {
        return -2;
    }

    private void checkRunningTasks() {
        Runnable runnable = this.cacheGenerateTask;
        if (runnable != null && lottieCacheGenerateQueue.remove(runnable)) {
            this.cacheGenerateTask = null;
        }
        if (!hasParentView() && this.nextRenderingBitmap != null && this.loadFrameTask != null) {
            this.loadFrameTask = null;
            this.nextRenderingBitmap = null;
        }
    }

    private void decodeFrameFinishedInternal() {
        if (this.destroyWhenDone) {
            checkRunningTasks();
            if (this.loadFrameTask == null && this.cacheGenerateTask == null && this.nativePtr != 0) {
                destroy(this.nativePtr);
                this.nativePtr = 0;
            }
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
        this.newColorUpdates = new HashMap();
        this.pendingColorUpdates = new HashMap();
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
        this.uiRunnableCacheFinished = new Runnable() {
            public void run() {
                RLottieDrawable.this.cacheGenerateTask = null;
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
        this.uiRunnableGenerateCache = new Runnable() {
            public void run() {
                if (!(RLottieDrawable.this.isRecycled || RLottieDrawable.this.destroyWhenDone || RLottieDrawable.this.nativePtr == 0)) {
                    ThreadPoolExecutor access$900 = RLottieDrawable.lottieCacheGenerateQueue;
                    RLottieDrawable rLottieDrawable = RLottieDrawable.this;
                    -$$Lambda$RLottieDrawable$5$Yb_jU2tqNZSr5pLiqbJfFDeXef4 -__lambda_rlottiedrawable_5_yb_ju2tqnzsr5pliqbjffdexef4 = new -$$Lambda$RLottieDrawable$5$Yb_jU2tqNZSr5pLiqbJfFDeXef4(this);
                    rLottieDrawable.cacheGenerateTask = -__lambda_rlottiedrawable_5_yb_ju2tqnzsr5pliqbjffdexef4;
                    access$900.execute(-__lambda_rlottiedrawable_5_yb_ju2tqnzsr5pliqbjffdexef4);
                }
                RLottieDrawable.this.loadFrameTask = null;
                RLottieDrawable.this.decodeFrameFinishedInternal();
            }

            public /* synthetic */ void lambda$run$0$RLottieDrawable$5() {
                if (RLottieDrawable.this.cacheGenerateTask != null) {
                    RLottieDrawable.createCache(RLottieDrawable.this.nativePtr, RLottieDrawable.this.backgroundBitmap, RLottieDrawable.this.width, RLottieDrawable.this.height, RLottieDrawable.this.backgroundBitmap.getRowBytes());
                    RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnableCacheFinished);
                }
            }
        };
        this.loadFrameRunnable = new Runnable() {
            public void run() {
                if (!RLottieDrawable.this.isRecycled) {
                    if (RLottieDrawable.this.nativePtr == 0) {
                        RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnableNoFrame);
                        return;
                    }
                    if (RLottieDrawable.this.backgroundBitmap == null) {
                        try {
                            RLottieDrawable.this.backgroundBitmap = Bitmap.createBitmap(RLottieDrawable.this.width, RLottieDrawable.this.height, Config.ARGB_8888);
                        } catch (Throwable th) {
                            FileLog.e(th);
                        }
                    }
                    if (RLottieDrawable.this.backgroundBitmap != null) {
                        if (RLottieDrawable.this.needGenerateCache) {
                            RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnableGenerateCache);
                            RLottieDrawable.this.needGenerateCache = false;
                            return;
                        }
                        if (!RLottieDrawable.this.pendingColorUpdates.isEmpty()) {
                            for (Entry entry : RLottieDrawable.this.pendingColorUpdates.entrySet()) {
                                RLottieDrawable.setLayerColor(RLottieDrawable.this.nativePtr, (String) entry.getKey(), ((Integer) entry.getValue()).intValue());
                            }
                            RLottieDrawable.this.pendingColorUpdates.clear();
                        }
                        RLottieDrawable.getFrame(RLottieDrawable.this.nativePtr, RLottieDrawable.this.currentFrame, RLottieDrawable.this.backgroundBitmap, RLottieDrawable.this.width, RLottieDrawable.this.height, RLottieDrawable.this.backgroundBitmap.getRowBytes());
                        int i = 2;
                        if (RLottieDrawable.this.metaData[2] != 0) {
                            RLottieDrawable.this.needGenerateCache = true;
                            RLottieDrawable.this.metaData[2] = 0;
                        }
                        RLottieDrawable rLottieDrawable = RLottieDrawable.this;
                        rLottieDrawable.nextRenderingBitmap = rLottieDrawable.backgroundBitmap;
                        if (!RLottieDrawable.this.shouldLimitFps) {
                            i = 1;
                        }
                        if (RLottieDrawable.this.currentFrame + i < RLottieDrawable.this.metaData[0]) {
                            rLottieDrawable = RLottieDrawable.this;
                            rLottieDrawable.currentFrame = rLottieDrawable.currentFrame + i;
                            RLottieDrawable.this.nextFrameIsLast = false;
                        } else if (RLottieDrawable.this.autoRepeat) {
                            RLottieDrawable.this.currentFrame = 0;
                            RLottieDrawable.this.nextFrameIsLast = false;
                        } else {
                            RLottieDrawable.this.nextFrameIsLast = true;
                        }
                    }
                    RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnable);
                }
            }
        };
        this.width = i;
        this.height = i2;
        this.shouldLimitFps = z2;
        getPaint().setFlags(2);
        this.nativePtr = create(file.getAbsolutePath(), this.metaData, z);
        if (z && lottieCacheGenerateQueue == null) {
            lottieCacheGenerateQueue = new ThreadPoolExecutor(1, 1, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue());
        }
        if (this.nativePtr == 0) {
            file.delete();
        }
        if (this.shouldLimitFps && this.metaData[1] < 60) {
            this.shouldLimitFps = false;
        }
        this.timeBetweenFrames = Math.max(this.shouldLimitFps ? 33 : 16, (int) (1000.0f / ((float) this.metaData[1])));
    }

    public RLottieDrawable(int i, String str, int i2, int i3) {
        this(i, str, i2, i3, true);
    }

    public RLottieDrawable(int i, String str, int i2, int i3, boolean z) {
        this.metaData = new int[3];
        this.newColorUpdates = new HashMap();
        this.pendingColorUpdates = new HashMap();
        this.autoRepeat = true;
        this.scaleX = 1.0f;
        this.scaleY = 1.0f;
        this.dstRect = new Rect();
        this.parentViews = new ArrayList();
        this.uiRunnableNoFrame = /* anonymous class already generated */;
        this.uiRunnableCacheFinished = /* anonymous class already generated */;
        this.uiRunnable = /* anonymous class already generated */;
        this.uiRunnableLastFrame = /* anonymous class already generated */;
        this.uiRunnableGenerateCache = /* anonymous class already generated */;
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
            int size = this.parentViews.size();
            int i = 0;
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
            this.parentViews.add(0, new WeakReference(view));
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

    public void recycle() {
        this.isRunning = false;
        this.isRecycled = true;
        checkRunningTasks();
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

    public void beginApplyLayerColors() {
        this.applyingLayerColors = true;
    }

    public void commitApplyLayerColors() {
        if (this.applyingLayerColors) {
            this.applyingLayerColors = false;
            if (!this.isRunning && this.decodeSingleFrame) {
                if (this.currentFrame <= 2) {
                    this.currentFrame = 0;
                }
                this.nextFrameIsLast = false;
                this.singleFrameDecoded = false;
                if (!scheduleNextGetFrame()) {
                    this.forceFrameRedraw = true;
                }
            }
            invalidateInternal();
        }
    }

    public void setLayerColor(String str, int i) {
        this.newColorUpdates.put(str, Integer.valueOf(i));
        if (!(this.applyingLayerColors || this.isRunning || !this.decodeSingleFrame)) {
            if (this.currentFrame <= 2) {
                this.currentFrame = 0;
            }
            this.nextFrameIsLast = false;
            this.singleFrameDecoded = false;
            if (!scheduleNextGetFrame()) {
                this.forceFrameRedraw = true;
            }
        }
        invalidateInternal();
    }

    /* JADX WARNING: Missing block: B:16:0x0024, code skipped:
            if (r5.singleFrameDecoded == false) goto L_0x0027;
     */
    private boolean scheduleNextGetFrame() {
        /*
        r5 = this;
        r0 = r5.cacheGenerateTask;
        if (r0 != 0) goto L_0x0046;
    L_0x0004:
        r0 = r5.loadFrameTask;
        if (r0 != 0) goto L_0x0046;
    L_0x0008:
        r0 = r5.nextRenderingBitmap;
        if (r0 != 0) goto L_0x0046;
    L_0x000c:
        r0 = r5.nativePtr;
        r2 = 0;
        r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));
        if (r4 == 0) goto L_0x0046;
    L_0x0014:
        r0 = r5.destroyWhenDone;
        if (r0 != 0) goto L_0x0046;
    L_0x0018:
        r0 = r5.isRunning;
        if (r0 != 0) goto L_0x0027;
    L_0x001c:
        r0 = r5.decodeSingleFrame;
        if (r0 == 0) goto L_0x0046;
    L_0x0020:
        if (r0 == 0) goto L_0x0027;
    L_0x0022:
        r0 = r5.singleFrameDecoded;
        if (r0 == 0) goto L_0x0027;
    L_0x0026:
        goto L_0x0046;
    L_0x0027:
        r0 = r5.newColorUpdates;
        r0 = r0.isEmpty();
        if (r0 != 0) goto L_0x003b;
    L_0x002f:
        r0 = r5.pendingColorUpdates;
        r1 = r5.newColorUpdates;
        r0.putAll(r1);
        r0 = r5.newColorUpdates;
        r0.clear();
    L_0x003b:
        r0 = loadFrameRunnableQueue;
        r1 = r5.loadFrameRunnable;
        r5.loadFrameTask = r1;
        r0.execute(r1);
        r0 = 1;
        return r0;
    L_0x0046:
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

    public void setCurrentParentView(View view) {
        this.currentParentView = view;
    }

    private boolean isCurrentParentViewMaster() {
        boolean z = true;
        if (getCallback() != null) {
            return true;
        }
        int size = this.parentViews.size();
        while (size > 0) {
            if (((WeakReference) this.parentViews.get(0)).get() == null) {
                this.parentViews.remove(0);
                size--;
            } else {
                if (((WeakReference) this.parentViews.get(0)).get() != this.currentParentView) {
                    z = false;
                }
                return z;
            }
        }
        return z;
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
        if (this.nativePtr != 0 && !this.destroyWhenDone) {
            long uptimeMillis = SystemClock.uptimeMillis();
            long abs = Math.abs(uptimeMillis - this.lastFrameTime);
            if (this.isRunning) {
                if (this.renderingBitmap == null && this.nextRenderingBitmap == null) {
                    scheduleNextGetFrame();
                } else if (this.nextRenderingBitmap != null && ((this.renderingBitmap == null || abs >= ((long) (this.timeBetweenFrames - 6))) && isCurrentParentViewMaster())) {
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
            } else if (this.forceFrameRedraw || (this.decodeSingleFrame && abs >= ((long) (this.timeBetweenFrames - 6)) && this.nextRenderingBitmap != null)) {
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
