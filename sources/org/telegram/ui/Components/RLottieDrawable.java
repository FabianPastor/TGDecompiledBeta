package org.telegram.ui.Components;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.DispatchQueuePool;
import org.telegram.messenger.DispatchQueuePoolBackground;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.utils.BitmapsCache;

public class RLottieDrawable extends BitmapDrawable implements Animatable, BitmapsCache.Cacheable {
    private static ThreadLocal<byte[]> bufferLocal = new ThreadLocal<>();
    private static final DispatchQueuePool loadFrameRunnableQueue = new DispatchQueuePool(4);
    public static DispatchQueue lottieCacheGenerateQueue;
    private static ThreadLocal<byte[]> readBufferLocal = new ThreadLocal<>();
    protected static final Handler uiHandler = new Handler(Looper.getMainLooper());
    private boolean applyTransformation;
    private boolean applyingLayerColors;
    NativePtrArgs args;
    protected int autoRepeat;
    protected int autoRepeatPlayCount;
    protected volatile Bitmap backgroundBitmap;
    private Paint backgroundPaint;
    BitmapsCache bitmapsCache;
    protected Runnable cacheGenerateTask;
    protected int currentFrame;
    private View currentParentView;
    protected int customEndFrame;
    private boolean decodeSingleFrame;
    protected boolean destroyAfterLoading;
    protected boolean destroyWhenDone;
    protected int diceSwitchFramesCount;
    private boolean doNotRemoveInvalidOnFrameReady;
    private final RectF dstRect;
    private RectF dstRectBackground;
    File file;
    private int finishFrame;
    private boolean forceFrameRedraw;
    protected CountDownLatch frameWaitSync;
    /* access modifiers changed from: private */
    public boolean genCacheSend;
    int generateCacheFramePointer;
    long generateCacheNativePtr;
    boolean generatingCache;
    protected final int height;
    private boolean invalidateOnProgressSet;
    protected int isDice;
    private boolean isInvalid;
    protected volatile boolean isRecycled;
    protected volatile boolean isRunning;
    private long lastFrameTime;
    protected Runnable loadFrameRunnable;
    protected Runnable loadFrameTask;
    protected boolean loadingInBackground;
    private View masterParent;
    protected final int[] metaData;
    protected volatile long nativePtr;
    private boolean needScale;
    private HashMap<String, Integer> newColorUpdates;
    private int[] newReplaceColors;
    protected volatile boolean nextFrameIsLast;
    protected volatile Bitmap nextRenderingBitmap;
    private Runnable onAnimationEndListener;
    protected WeakReference<Runnable> onFinishCallback;
    /* access modifiers changed from: private */
    public Runnable onFrameReadyRunnable;
    private ArrayList<ImageReceiver> parentViews;
    /* access modifiers changed from: private */
    public volatile HashMap<String, Integer> pendingColorUpdates;
    /* access modifiers changed from: private */
    public int[] pendingReplaceColors;
    protected boolean playInDirectionOfCustomEndFrame;
    boolean precache;
    protected volatile Bitmap renderingBitmap;
    private float scaleX;
    private float scaleY;
    protected int secondFramesCount;
    protected boolean secondLoadingInBackground;
    protected volatile long secondNativePtr;
    protected volatile boolean setLastFrame;
    /* access modifiers changed from: private */
    public boolean shouldLimitFps;
    /* access modifiers changed from: private */
    public boolean singleFrameDecoded;
    public boolean skipFrameUpdate;
    long startTime;
    protected int timeBetweenFrames;
    protected Runnable uiRunnable;
    /* access modifiers changed from: private */
    public Runnable uiRunnableCacheFinished;
    /* access modifiers changed from: private */
    public Runnable uiRunnableGenerateCache;
    protected Runnable uiRunnableNoFrame;
    private HashMap<Integer, Integer> vibrationPattern;
    protected boolean waitingForNextTask;
    protected final int width;

    public static native long create(String str, String str2, int i, int i2, int[] iArr, boolean z, int[] iArr2, boolean z2, int i3);

    protected static native long createWithJson(String str, String str2, int[] iArr, int[] iArr2);

    public static native void destroy(long j);

    public static native int getFrame(long j, int i, Bitmap bitmap, int i2, int i3, int i4, boolean z);

    /* access modifiers changed from: private */
    public static native void replaceColors(long j, int[] iArr);

    /* access modifiers changed from: private */
    public static native void setLayerColor(long j, String str, int i);

    public int getOpacity() {
        return -2;
    }

    public void setAutoRepeatTimeout(long j) {
    }

    public static void createCacheGenQueue() {
        lottieCacheGenerateQueue = new DispatchQueue("cache generator queue");
    }

    /* access modifiers changed from: protected */
    public void checkRunningTasks() {
        Runnable runnable = this.cacheGenerateTask;
        if (runnable != null) {
            lottieCacheGenerateQueue.cancelRunnable(runnable);
            this.cacheGenerateTask = null;
        }
        if (!hasParentView() && this.nextRenderingBitmap != null && this.loadFrameTask != null) {
            this.loadFrameTask = null;
            this.nextRenderingBitmap = null;
        }
    }

    /* access modifiers changed from: protected */
    public void decodeFrameFinishedInternal() {
        if (this.destroyWhenDone) {
            checkRunningTasks();
            if (this.loadFrameTask == null && this.cacheGenerateTask == null && this.nativePtr != 0) {
                destroy(this.nativePtr);
                this.nativePtr = 0;
                if (this.secondNativePtr != 0) {
                    destroy(this.secondNativePtr);
                    this.secondNativePtr = 0;
                }
            }
        }
        if (this.nativePtr == 0 && this.secondNativePtr == 0 && this.bitmapsCache == null) {
            recycleResources();
            return;
        }
        this.waitingForNextTask = true;
        if (!hasParentView()) {
            stop();
        }
        scheduleNextGetFrame();
    }

    /* access modifiers changed from: protected */
    public void recycleResources() {
        ArrayList arrayList = new ArrayList();
        arrayList.add(this.renderingBitmap);
        arrayList.add(this.nextRenderingBitmap);
        this.renderingBitmap = null;
        this.backgroundBitmap = null;
        AndroidUtilities.recycleBitmaps(arrayList);
        if (this.onAnimationEndListener != null) {
            this.onAnimationEndListener = null;
        }
        invalidateInternal();
    }

    public void setOnFinishCallback(Runnable runnable, int i) {
        if (runnable != null) {
            this.onFinishCallback = new WeakReference<>(runnable);
            this.finishFrame = i;
        } else if (this.onFinishCallback != null) {
            this.onFinishCallback = null;
        }
    }

    public RLottieDrawable(File file2, int i, int i2, BitmapsCache.CacheOptions cacheOptions, boolean z, int[] iArr, int i3) {
        int[] iArr2;
        char c;
        boolean z2;
        int[] iArr3 = new int[3];
        this.metaData = iArr3;
        this.customEndFrame = -1;
        this.newColorUpdates = new HashMap<>();
        this.pendingColorUpdates = new HashMap<>();
        this.parentViews = new ArrayList<>();
        this.diceSwitchFramesCount = -1;
        this.autoRepeat = 1;
        this.scaleX = 1.0f;
        this.scaleY = 1.0f;
        this.dstRect = new RectF();
        this.uiRunnableNoFrame = new Runnable() {
            public void run() {
                RLottieDrawable rLottieDrawable = RLottieDrawable.this;
                rLottieDrawable.loadFrameTask = null;
                rLottieDrawable.decodeFrameFinishedInternal();
                if (RLottieDrawable.this.onFrameReadyRunnable != null) {
                    RLottieDrawable.this.onFrameReadyRunnable.run();
                }
            }
        };
        this.uiRunnable = new Runnable() {
            public void run() {
                boolean unused = RLottieDrawable.this.singleFrameDecoded = true;
                RLottieDrawable.this.invalidateInternal();
                RLottieDrawable.this.decodeFrameFinishedInternal();
                if (RLottieDrawable.this.onFrameReadyRunnable != null) {
                    RLottieDrawable.this.onFrameReadyRunnable.run();
                }
            }
        };
        this.uiRunnableGenerateCache = new Runnable() {
            public void run() {
                if (!RLottieDrawable.this.isRecycled) {
                    RLottieDrawable rLottieDrawable = RLottieDrawable.this;
                    if (!rLottieDrawable.destroyWhenDone && rLottieDrawable.canLoadFrames()) {
                        RLottieDrawable rLottieDrawable2 = RLottieDrawable.this;
                        if (rLottieDrawable2.cacheGenerateTask == null) {
                            rLottieDrawable2.startTime = System.currentTimeMillis();
                            RLottieDrawable rLottieDrawable3 = RLottieDrawable.this;
                            rLottieDrawable3.generatingCache = true;
                            DispatchQueue dispatchQueue = RLottieDrawable.lottieCacheGenerateQueue;
                            RLottieDrawable$3$$ExternalSyntheticLambda0 rLottieDrawable$3$$ExternalSyntheticLambda0 = new RLottieDrawable$3$$ExternalSyntheticLambda0(this);
                            rLottieDrawable3.cacheGenerateTask = rLottieDrawable$3$$ExternalSyntheticLambda0;
                            dispatchQueue.postRunnable(rLottieDrawable$3$$ExternalSyntheticLambda0);
                        }
                    }
                }
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$run$0() {
                RLottieDrawable.this.bitmapsCache.createCache();
                RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnableCacheFinished);
            }
        };
        this.uiRunnableCacheFinished = new Runnable() {
            public void run() {
                RLottieDrawable rLottieDrawable = RLottieDrawable.this;
                rLottieDrawable.cacheGenerateTask = null;
                rLottieDrawable.generatingCache = false;
                rLottieDrawable.decodeFrameFinishedInternal();
            }
        };
        this.loadFrameRunnable = new Runnable() {
            public void run() {
                long j;
                int i;
                BitmapsCache bitmapsCache;
                if (!RLottieDrawable.this.isRecycled) {
                    if (RLottieDrawable.this.canLoadFrames()) {
                        RLottieDrawable rLottieDrawable = RLottieDrawable.this;
                        if (!(rLottieDrawable.isDice == 2 && rLottieDrawable.secondNativePtr == 0)) {
                            if (RLottieDrawable.this.backgroundBitmap == null) {
                                try {
                                    RLottieDrawable rLottieDrawable2 = RLottieDrawable.this;
                                    rLottieDrawable2.backgroundBitmap = Bitmap.createBitmap(rLottieDrawable2.width, rLottieDrawable2.height, Bitmap.Config.ARGB_8888);
                                } catch (Throwable th) {
                                    FileLog.e(th);
                                }
                            }
                            if (RLottieDrawable.this.backgroundBitmap != null) {
                                try {
                                    if (!RLottieDrawable.this.pendingColorUpdates.isEmpty()) {
                                        for (Map.Entry entry : RLottieDrawable.this.pendingColorUpdates.entrySet()) {
                                            RLottieDrawable.setLayerColor(RLottieDrawable.this.nativePtr, (String) entry.getKey(), ((Integer) entry.getValue()).intValue());
                                        }
                                        RLottieDrawable.this.pendingColorUpdates.clear();
                                    }
                                } catch (Exception unused) {
                                }
                                if (!(RLottieDrawable.this.pendingReplaceColors == null || RLottieDrawable.this.nativePtr == 0)) {
                                    RLottieDrawable.replaceColors(RLottieDrawable.this.nativePtr, RLottieDrawable.this.pendingReplaceColors);
                                    int[] unused2 = RLottieDrawable.this.pendingReplaceColors = null;
                                }
                                try {
                                    RLottieDrawable rLottieDrawable3 = RLottieDrawable.this;
                                    int i2 = rLottieDrawable3.isDice;
                                    if (i2 == 1) {
                                        j = rLottieDrawable3.nativePtr;
                                    } else if (i2 == 2) {
                                        j = rLottieDrawable3.secondNativePtr;
                                        if (RLottieDrawable.this.setLastFrame) {
                                            RLottieDrawable rLottieDrawable4 = RLottieDrawable.this;
                                            rLottieDrawable4.currentFrame = rLottieDrawable4.secondFramesCount - 1;
                                        }
                                    } else {
                                        j = rLottieDrawable3.nativePtr;
                                    }
                                    long j2 = j;
                                    int i3 = RLottieDrawable.this.shouldLimitFps ? 2 : 1;
                                    RLottieDrawable rLottieDrawable5 = RLottieDrawable.this;
                                    if (!rLottieDrawable5.precache || (bitmapsCache = rLottieDrawable5.bitmapsCache) == null) {
                                        int i4 = rLottieDrawable5.currentFrame;
                                        Bitmap bitmap = rLottieDrawable5.backgroundBitmap;
                                        RLottieDrawable rLottieDrawable6 = RLottieDrawable.this;
                                        i = RLottieDrawable.getFrame(j2, i4, bitmap, rLottieDrawable6.width, rLottieDrawable6.height, rLottieDrawable6.backgroundBitmap.getRowBytes(), true);
                                    } else {
                                        try {
                                            i = bitmapsCache.getFrame(rLottieDrawable5.currentFrame / i3, rLottieDrawable5.backgroundBitmap);
                                        } catch (Exception e) {
                                            FileLog.e((Throwable) e);
                                            i = 0;
                                        }
                                    }
                                    BitmapsCache bitmapsCache2 = RLottieDrawable.this.bitmapsCache;
                                    if (bitmapsCache2 != null && bitmapsCache2.needGenCache()) {
                                        if (!RLottieDrawable.this.genCacheSend) {
                                            boolean unused3 = RLottieDrawable.this.genCacheSend = true;
                                            RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnableGenerateCache);
                                        }
                                        i = -1;
                                    }
                                    if (i == -1) {
                                        RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnableNoFrame);
                                        CountDownLatch countDownLatch = RLottieDrawable.this.frameWaitSync;
                                        if (countDownLatch != null) {
                                            countDownLatch.countDown();
                                            return;
                                        }
                                        return;
                                    }
                                    RLottieDrawable rLottieDrawable7 = RLottieDrawable.this;
                                    rLottieDrawable7.nextRenderingBitmap = rLottieDrawable7.backgroundBitmap;
                                    RLottieDrawable rLottieDrawable8 = RLottieDrawable.this;
                                    int i5 = rLottieDrawable8.isDice;
                                    if (i5 == 1) {
                                        int i6 = rLottieDrawable8.currentFrame;
                                        int i7 = i6 + i3;
                                        int i8 = rLottieDrawable8.diceSwitchFramesCount;
                                        if (i8 == -1) {
                                            i8 = rLottieDrawable8.metaData[0];
                                        }
                                        if (i7 < i8) {
                                            rLottieDrawable8.currentFrame = i6 + i3;
                                        } else {
                                            rLottieDrawable8.currentFrame = 0;
                                            rLottieDrawable8.nextFrameIsLast = false;
                                            if (RLottieDrawable.this.secondNativePtr != 0) {
                                                RLottieDrawable.this.isDice = 2;
                                            }
                                        }
                                    } else if (i5 == 2) {
                                        int i9 = rLottieDrawable8.currentFrame;
                                        if (i9 + i3 < rLottieDrawable8.secondFramesCount) {
                                            rLottieDrawable8.currentFrame = i9 + i3;
                                        } else {
                                            rLottieDrawable8.nextFrameIsLast = true;
                                            RLottieDrawable.this.autoRepeatPlayCount++;
                                        }
                                    } else {
                                        int i10 = rLottieDrawable8.customEndFrame;
                                        if (i10 < 0 || !rLottieDrawable8.playInDirectionOfCustomEndFrame) {
                                            int i11 = rLottieDrawable8.currentFrame;
                                            int i12 = i11 + i3;
                                            if (i10 < 0) {
                                                i10 = rLottieDrawable8.metaData[0];
                                            }
                                            if (i12 >= i10) {
                                                int i13 = rLottieDrawable8.autoRepeat;
                                                if (i13 == 1) {
                                                    rLottieDrawable8.currentFrame = 0;
                                                    rLottieDrawable8.nextFrameIsLast = false;
                                                } else if (i13 == 2) {
                                                    rLottieDrawable8.currentFrame = 0;
                                                    rLottieDrawable8.nextFrameIsLast = true;
                                                    RLottieDrawable.this.autoRepeatPlayCount++;
                                                } else {
                                                    rLottieDrawable8.nextFrameIsLast = true;
                                                    RLottieDrawable.this.checkDispatchOnAnimationEnd();
                                                }
                                            } else if (rLottieDrawable8.autoRepeat == 3) {
                                                rLottieDrawable8.nextFrameIsLast = true;
                                                RLottieDrawable.this.autoRepeatPlayCount++;
                                            } else {
                                                rLottieDrawable8.currentFrame = i11 + i3;
                                                rLottieDrawable8.nextFrameIsLast = false;
                                            }
                                        } else {
                                            int i14 = rLottieDrawable8.currentFrame;
                                            if (i14 > i10) {
                                                if (i14 - i3 >= i10) {
                                                    rLottieDrawable8.currentFrame = i14 - i3;
                                                    rLottieDrawable8.nextFrameIsLast = false;
                                                } else {
                                                    rLottieDrawable8.nextFrameIsLast = true;
                                                    RLottieDrawable.this.checkDispatchOnAnimationEnd();
                                                }
                                            } else if (i14 + i3 < i10) {
                                                rLottieDrawable8.currentFrame = i14 + i3;
                                                rLottieDrawable8.nextFrameIsLast = false;
                                            } else {
                                                rLottieDrawable8.nextFrameIsLast = true;
                                                RLottieDrawable.this.checkDispatchOnAnimationEnd();
                                            }
                                        }
                                    }
                                } catch (Exception e2) {
                                    FileLog.e((Throwable) e2);
                                }
                            }
                            RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnable);
                            CountDownLatch countDownLatch2 = RLottieDrawable.this.frameWaitSync;
                            if (countDownLatch2 != null) {
                                countDownLatch2.countDown();
                                return;
                            }
                            return;
                        }
                    }
                    CountDownLatch countDownLatch3 = RLottieDrawable.this.frameWaitSync;
                    if (countDownLatch3 != null) {
                        countDownLatch3.countDown();
                    }
                    RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnableNoFrame);
                }
            }
        };
        this.width = i;
        this.height = i2;
        this.shouldLimitFps = z;
        this.precache = cacheOptions != null;
        getPaint().setFlags(2);
        this.file = file2;
        if (this.precache && lottieCacheGenerateQueue == null) {
            createCacheGenQueue();
        }
        if (this.precache) {
            this.bitmapsCache = new BitmapsCache(file2, this, cacheOptions, i, i2);
            NativePtrArgs nativePtrArgs = new NativePtrArgs();
            this.args = nativePtrArgs;
            nativePtrArgs.file = file2.getAbsoluteFile();
            NativePtrArgs nativePtrArgs2 = this.args;
            nativePtrArgs2.json = null;
            nativePtrArgs2.colorReplacement = iArr;
            nativePtrArgs2.fitzModifier = i3;
            z2 = false;
            c = 1;
            iArr2 = iArr3;
            this.nativePtr = create(file2.getAbsolutePath(), (String) null, i, i2, iArr3, this.precache, iArr, this.shouldLimitFps, i3);
            destroy(this.nativePtr);
            this.nativePtr = 0;
        } else {
            iArr2 = iArr3;
            z2 = false;
            c = 1;
            this.nativePtr = create(file2.getAbsolutePath(), (String) null, i, i2, iArr2, this.precache, iArr, this.shouldLimitFps, i3);
            if (this.nativePtr == 0) {
                file2.delete();
            }
        }
        if (this.shouldLimitFps && iArr2[c] < 60) {
            this.shouldLimitFps = z2;
        }
        this.timeBetweenFrames = Math.max(this.shouldLimitFps ? 33 : 16, (int) (1000.0f / ((float) iArr2[c])));
    }

    public RLottieDrawable(File file2, String str, int i, int i2, BitmapsCache.CacheOptions cacheOptions, boolean z, int[] iArr, int i3) {
        char c;
        int[] iArr2;
        boolean z2;
        int[] iArr3 = new int[3];
        this.metaData = iArr3;
        this.customEndFrame = -1;
        this.newColorUpdates = new HashMap<>();
        this.pendingColorUpdates = new HashMap<>();
        this.parentViews = new ArrayList<>();
        this.diceSwitchFramesCount = -1;
        this.autoRepeat = 1;
        this.scaleX = 1.0f;
        this.scaleY = 1.0f;
        this.dstRect = new RectF();
        this.uiRunnableNoFrame = new Runnable() {
            public void run() {
                RLottieDrawable rLottieDrawable = RLottieDrawable.this;
                rLottieDrawable.loadFrameTask = null;
                rLottieDrawable.decodeFrameFinishedInternal();
                if (RLottieDrawable.this.onFrameReadyRunnable != null) {
                    RLottieDrawable.this.onFrameReadyRunnable.run();
                }
            }
        };
        this.uiRunnable = new Runnable() {
            public void run() {
                boolean unused = RLottieDrawable.this.singleFrameDecoded = true;
                RLottieDrawable.this.invalidateInternal();
                RLottieDrawable.this.decodeFrameFinishedInternal();
                if (RLottieDrawable.this.onFrameReadyRunnable != null) {
                    RLottieDrawable.this.onFrameReadyRunnable.run();
                }
            }
        };
        this.uiRunnableGenerateCache = new Runnable() {
            public void run() {
                if (!RLottieDrawable.this.isRecycled) {
                    RLottieDrawable rLottieDrawable = RLottieDrawable.this;
                    if (!rLottieDrawable.destroyWhenDone && rLottieDrawable.canLoadFrames()) {
                        RLottieDrawable rLottieDrawable2 = RLottieDrawable.this;
                        if (rLottieDrawable2.cacheGenerateTask == null) {
                            rLottieDrawable2.startTime = System.currentTimeMillis();
                            RLottieDrawable rLottieDrawable3 = RLottieDrawable.this;
                            rLottieDrawable3.generatingCache = true;
                            DispatchQueue dispatchQueue = RLottieDrawable.lottieCacheGenerateQueue;
                            RLottieDrawable$3$$ExternalSyntheticLambda0 rLottieDrawable$3$$ExternalSyntheticLambda0 = new RLottieDrawable$3$$ExternalSyntheticLambda0(this);
                            rLottieDrawable3.cacheGenerateTask = rLottieDrawable$3$$ExternalSyntheticLambda0;
                            dispatchQueue.postRunnable(rLottieDrawable$3$$ExternalSyntheticLambda0);
                        }
                    }
                }
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$run$0() {
                RLottieDrawable.this.bitmapsCache.createCache();
                RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnableCacheFinished);
            }
        };
        this.uiRunnableCacheFinished = new Runnable() {
            public void run() {
                RLottieDrawable rLottieDrawable = RLottieDrawable.this;
                rLottieDrawable.cacheGenerateTask = null;
                rLottieDrawable.generatingCache = false;
                rLottieDrawable.decodeFrameFinishedInternal();
            }
        };
        this.loadFrameRunnable = new Runnable() {
            public void run() {
                long j;
                int i;
                BitmapsCache bitmapsCache;
                if (!RLottieDrawable.this.isRecycled) {
                    if (RLottieDrawable.this.canLoadFrames()) {
                        RLottieDrawable rLottieDrawable = RLottieDrawable.this;
                        if (!(rLottieDrawable.isDice == 2 && rLottieDrawable.secondNativePtr == 0)) {
                            if (RLottieDrawable.this.backgroundBitmap == null) {
                                try {
                                    RLottieDrawable rLottieDrawable2 = RLottieDrawable.this;
                                    rLottieDrawable2.backgroundBitmap = Bitmap.createBitmap(rLottieDrawable2.width, rLottieDrawable2.height, Bitmap.Config.ARGB_8888);
                                } catch (Throwable th) {
                                    FileLog.e(th);
                                }
                            }
                            if (RLottieDrawable.this.backgroundBitmap != null) {
                                try {
                                    if (!RLottieDrawable.this.pendingColorUpdates.isEmpty()) {
                                        for (Map.Entry entry : RLottieDrawable.this.pendingColorUpdates.entrySet()) {
                                            RLottieDrawable.setLayerColor(RLottieDrawable.this.nativePtr, (String) entry.getKey(), ((Integer) entry.getValue()).intValue());
                                        }
                                        RLottieDrawable.this.pendingColorUpdates.clear();
                                    }
                                } catch (Exception unused) {
                                }
                                if (!(RLottieDrawable.this.pendingReplaceColors == null || RLottieDrawable.this.nativePtr == 0)) {
                                    RLottieDrawable.replaceColors(RLottieDrawable.this.nativePtr, RLottieDrawable.this.pendingReplaceColors);
                                    int[] unused2 = RLottieDrawable.this.pendingReplaceColors = null;
                                }
                                try {
                                    RLottieDrawable rLottieDrawable3 = RLottieDrawable.this;
                                    int i2 = rLottieDrawable3.isDice;
                                    if (i2 == 1) {
                                        j = rLottieDrawable3.nativePtr;
                                    } else if (i2 == 2) {
                                        j = rLottieDrawable3.secondNativePtr;
                                        if (RLottieDrawable.this.setLastFrame) {
                                            RLottieDrawable rLottieDrawable4 = RLottieDrawable.this;
                                            rLottieDrawable4.currentFrame = rLottieDrawable4.secondFramesCount - 1;
                                        }
                                    } else {
                                        j = rLottieDrawable3.nativePtr;
                                    }
                                    long j2 = j;
                                    int i3 = RLottieDrawable.this.shouldLimitFps ? 2 : 1;
                                    RLottieDrawable rLottieDrawable5 = RLottieDrawable.this;
                                    if (!rLottieDrawable5.precache || (bitmapsCache = rLottieDrawable5.bitmapsCache) == null) {
                                        int i4 = rLottieDrawable5.currentFrame;
                                        Bitmap bitmap = rLottieDrawable5.backgroundBitmap;
                                        RLottieDrawable rLottieDrawable6 = RLottieDrawable.this;
                                        i = RLottieDrawable.getFrame(j2, i4, bitmap, rLottieDrawable6.width, rLottieDrawable6.height, rLottieDrawable6.backgroundBitmap.getRowBytes(), true);
                                    } else {
                                        try {
                                            i = bitmapsCache.getFrame(rLottieDrawable5.currentFrame / i3, rLottieDrawable5.backgroundBitmap);
                                        } catch (Exception e) {
                                            FileLog.e((Throwable) e);
                                            i = 0;
                                        }
                                    }
                                    BitmapsCache bitmapsCache2 = RLottieDrawable.this.bitmapsCache;
                                    if (bitmapsCache2 != null && bitmapsCache2.needGenCache()) {
                                        if (!RLottieDrawable.this.genCacheSend) {
                                            boolean unused3 = RLottieDrawable.this.genCacheSend = true;
                                            RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnableGenerateCache);
                                        }
                                        i = -1;
                                    }
                                    if (i == -1) {
                                        RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnableNoFrame);
                                        CountDownLatch countDownLatch = RLottieDrawable.this.frameWaitSync;
                                        if (countDownLatch != null) {
                                            countDownLatch.countDown();
                                            return;
                                        }
                                        return;
                                    }
                                    RLottieDrawable rLottieDrawable7 = RLottieDrawable.this;
                                    rLottieDrawable7.nextRenderingBitmap = rLottieDrawable7.backgroundBitmap;
                                    RLottieDrawable rLottieDrawable8 = RLottieDrawable.this;
                                    int i5 = rLottieDrawable8.isDice;
                                    if (i5 == 1) {
                                        int i6 = rLottieDrawable8.currentFrame;
                                        int i7 = i6 + i3;
                                        int i8 = rLottieDrawable8.diceSwitchFramesCount;
                                        if (i8 == -1) {
                                            i8 = rLottieDrawable8.metaData[0];
                                        }
                                        if (i7 < i8) {
                                            rLottieDrawable8.currentFrame = i6 + i3;
                                        } else {
                                            rLottieDrawable8.currentFrame = 0;
                                            rLottieDrawable8.nextFrameIsLast = false;
                                            if (RLottieDrawable.this.secondNativePtr != 0) {
                                                RLottieDrawable.this.isDice = 2;
                                            }
                                        }
                                    } else if (i5 == 2) {
                                        int i9 = rLottieDrawable8.currentFrame;
                                        if (i9 + i3 < rLottieDrawable8.secondFramesCount) {
                                            rLottieDrawable8.currentFrame = i9 + i3;
                                        } else {
                                            rLottieDrawable8.nextFrameIsLast = true;
                                            RLottieDrawable.this.autoRepeatPlayCount++;
                                        }
                                    } else {
                                        int i10 = rLottieDrawable8.customEndFrame;
                                        if (i10 < 0 || !rLottieDrawable8.playInDirectionOfCustomEndFrame) {
                                            int i11 = rLottieDrawable8.currentFrame;
                                            int i12 = i11 + i3;
                                            if (i10 < 0) {
                                                i10 = rLottieDrawable8.metaData[0];
                                            }
                                            if (i12 >= i10) {
                                                int i13 = rLottieDrawable8.autoRepeat;
                                                if (i13 == 1) {
                                                    rLottieDrawable8.currentFrame = 0;
                                                    rLottieDrawable8.nextFrameIsLast = false;
                                                } else if (i13 == 2) {
                                                    rLottieDrawable8.currentFrame = 0;
                                                    rLottieDrawable8.nextFrameIsLast = true;
                                                    RLottieDrawable.this.autoRepeatPlayCount++;
                                                } else {
                                                    rLottieDrawable8.nextFrameIsLast = true;
                                                    RLottieDrawable.this.checkDispatchOnAnimationEnd();
                                                }
                                            } else if (rLottieDrawable8.autoRepeat == 3) {
                                                rLottieDrawable8.nextFrameIsLast = true;
                                                RLottieDrawable.this.autoRepeatPlayCount++;
                                            } else {
                                                rLottieDrawable8.currentFrame = i11 + i3;
                                                rLottieDrawable8.nextFrameIsLast = false;
                                            }
                                        } else {
                                            int i14 = rLottieDrawable8.currentFrame;
                                            if (i14 > i10) {
                                                if (i14 - i3 >= i10) {
                                                    rLottieDrawable8.currentFrame = i14 - i3;
                                                    rLottieDrawable8.nextFrameIsLast = false;
                                                } else {
                                                    rLottieDrawable8.nextFrameIsLast = true;
                                                    RLottieDrawable.this.checkDispatchOnAnimationEnd();
                                                }
                                            } else if (i14 + i3 < i10) {
                                                rLottieDrawable8.currentFrame = i14 + i3;
                                                rLottieDrawable8.nextFrameIsLast = false;
                                            } else {
                                                rLottieDrawable8.nextFrameIsLast = true;
                                                RLottieDrawable.this.checkDispatchOnAnimationEnd();
                                            }
                                        }
                                    }
                                } catch (Exception e2) {
                                    FileLog.e((Throwable) e2);
                                }
                            }
                            RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnable);
                            CountDownLatch countDownLatch2 = RLottieDrawable.this.frameWaitSync;
                            if (countDownLatch2 != null) {
                                countDownLatch2.countDown();
                                return;
                            }
                            return;
                        }
                    }
                    CountDownLatch countDownLatch3 = RLottieDrawable.this.frameWaitSync;
                    if (countDownLatch3 != null) {
                        countDownLatch3.countDown();
                    }
                    RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnableNoFrame);
                }
            }
        };
        this.width = i;
        this.height = i2;
        this.shouldLimitFps = z;
        this.precache = cacheOptions != null;
        getPaint().setFlags(2);
        if (this.precache && lottieCacheGenerateQueue == null) {
            createCacheGenQueue();
        }
        if (this.precache) {
            this.bitmapsCache = new BitmapsCache(file2, this, cacheOptions, i, i2);
            NativePtrArgs nativePtrArgs = new NativePtrArgs();
            this.args = nativePtrArgs;
            nativePtrArgs.file = file2.getAbsoluteFile();
            NativePtrArgs nativePtrArgs2 = this.args;
            nativePtrArgs2.json = str;
            nativePtrArgs2.colorReplacement = iArr;
            nativePtrArgs2.fitzModifier = i3;
            z2 = false;
            c = 1;
            iArr2 = iArr3;
            this.nativePtr = create(file2.getAbsolutePath(), str, i, i2, iArr3, this.precache, iArr, this.shouldLimitFps, i3);
            if (this.nativePtr != 0) {
                destroy(this.nativePtr);
            }
            this.nativePtr = 0;
        } else {
            int i4 = i3;
            iArr2 = iArr3;
            z2 = false;
            c = 1;
            this.nativePtr = create(file2.getAbsolutePath(), str, i, i2, iArr2, this.precache, iArr, this.shouldLimitFps, i3);
            if (this.nativePtr == 0) {
                file2.delete();
            }
        }
        if (this.shouldLimitFps && iArr2[c] < 60) {
            this.shouldLimitFps = z2;
        }
        this.timeBetweenFrames = Math.max(this.shouldLimitFps ? 33 : 16, (int) (1000.0f / ((float) iArr2[c])));
    }

    public RLottieDrawable(int i, String str, int i2, int i3) {
        this(i, str, i2, i3, true, (int[]) null);
    }

    public RLottieDrawable(String str, int i, int i2) {
        String str2;
        int[] iArr = new int[3];
        this.metaData = iArr;
        this.customEndFrame = -1;
        this.newColorUpdates = new HashMap<>();
        this.pendingColorUpdates = new HashMap<>();
        this.parentViews = new ArrayList<>();
        this.diceSwitchFramesCount = -1;
        this.autoRepeat = 1;
        this.scaleX = 1.0f;
        this.scaleY = 1.0f;
        this.dstRect = new RectF();
        this.uiRunnableNoFrame = new Runnable() {
            public void run() {
                RLottieDrawable rLottieDrawable = RLottieDrawable.this;
                rLottieDrawable.loadFrameTask = null;
                rLottieDrawable.decodeFrameFinishedInternal();
                if (RLottieDrawable.this.onFrameReadyRunnable != null) {
                    RLottieDrawable.this.onFrameReadyRunnable.run();
                }
            }
        };
        this.uiRunnable = new Runnable() {
            public void run() {
                boolean unused = RLottieDrawable.this.singleFrameDecoded = true;
                RLottieDrawable.this.invalidateInternal();
                RLottieDrawable.this.decodeFrameFinishedInternal();
                if (RLottieDrawable.this.onFrameReadyRunnable != null) {
                    RLottieDrawable.this.onFrameReadyRunnable.run();
                }
            }
        };
        this.uiRunnableGenerateCache = new Runnable() {
            public void run() {
                if (!RLottieDrawable.this.isRecycled) {
                    RLottieDrawable rLottieDrawable = RLottieDrawable.this;
                    if (!rLottieDrawable.destroyWhenDone && rLottieDrawable.canLoadFrames()) {
                        RLottieDrawable rLottieDrawable2 = RLottieDrawable.this;
                        if (rLottieDrawable2.cacheGenerateTask == null) {
                            rLottieDrawable2.startTime = System.currentTimeMillis();
                            RLottieDrawable rLottieDrawable3 = RLottieDrawable.this;
                            rLottieDrawable3.generatingCache = true;
                            DispatchQueue dispatchQueue = RLottieDrawable.lottieCacheGenerateQueue;
                            RLottieDrawable$3$$ExternalSyntheticLambda0 rLottieDrawable$3$$ExternalSyntheticLambda0 = new RLottieDrawable$3$$ExternalSyntheticLambda0(this);
                            rLottieDrawable3.cacheGenerateTask = rLottieDrawable$3$$ExternalSyntheticLambda0;
                            dispatchQueue.postRunnable(rLottieDrawable$3$$ExternalSyntheticLambda0);
                        }
                    }
                }
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$run$0() {
                RLottieDrawable.this.bitmapsCache.createCache();
                RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnableCacheFinished);
            }
        };
        this.uiRunnableCacheFinished = new Runnable() {
            public void run() {
                RLottieDrawable rLottieDrawable = RLottieDrawable.this;
                rLottieDrawable.cacheGenerateTask = null;
                rLottieDrawable.generatingCache = false;
                rLottieDrawable.decodeFrameFinishedInternal();
            }
        };
        this.loadFrameRunnable = new Runnable() {
            public void run() {
                long j;
                int i;
                BitmapsCache bitmapsCache;
                if (!RLottieDrawable.this.isRecycled) {
                    if (RLottieDrawable.this.canLoadFrames()) {
                        RLottieDrawable rLottieDrawable = RLottieDrawable.this;
                        if (!(rLottieDrawable.isDice == 2 && rLottieDrawable.secondNativePtr == 0)) {
                            if (RLottieDrawable.this.backgroundBitmap == null) {
                                try {
                                    RLottieDrawable rLottieDrawable2 = RLottieDrawable.this;
                                    rLottieDrawable2.backgroundBitmap = Bitmap.createBitmap(rLottieDrawable2.width, rLottieDrawable2.height, Bitmap.Config.ARGB_8888);
                                } catch (Throwable th) {
                                    FileLog.e(th);
                                }
                            }
                            if (RLottieDrawable.this.backgroundBitmap != null) {
                                try {
                                    if (!RLottieDrawable.this.pendingColorUpdates.isEmpty()) {
                                        for (Map.Entry entry : RLottieDrawable.this.pendingColorUpdates.entrySet()) {
                                            RLottieDrawable.setLayerColor(RLottieDrawable.this.nativePtr, (String) entry.getKey(), ((Integer) entry.getValue()).intValue());
                                        }
                                        RLottieDrawable.this.pendingColorUpdates.clear();
                                    }
                                } catch (Exception unused) {
                                }
                                if (!(RLottieDrawable.this.pendingReplaceColors == null || RLottieDrawable.this.nativePtr == 0)) {
                                    RLottieDrawable.replaceColors(RLottieDrawable.this.nativePtr, RLottieDrawable.this.pendingReplaceColors);
                                    int[] unused2 = RLottieDrawable.this.pendingReplaceColors = null;
                                }
                                try {
                                    RLottieDrawable rLottieDrawable3 = RLottieDrawable.this;
                                    int i2 = rLottieDrawable3.isDice;
                                    if (i2 == 1) {
                                        j = rLottieDrawable3.nativePtr;
                                    } else if (i2 == 2) {
                                        j = rLottieDrawable3.secondNativePtr;
                                        if (RLottieDrawable.this.setLastFrame) {
                                            RLottieDrawable rLottieDrawable4 = RLottieDrawable.this;
                                            rLottieDrawable4.currentFrame = rLottieDrawable4.secondFramesCount - 1;
                                        }
                                    } else {
                                        j = rLottieDrawable3.nativePtr;
                                    }
                                    long j2 = j;
                                    int i3 = RLottieDrawable.this.shouldLimitFps ? 2 : 1;
                                    RLottieDrawable rLottieDrawable5 = RLottieDrawable.this;
                                    if (!rLottieDrawable5.precache || (bitmapsCache = rLottieDrawable5.bitmapsCache) == null) {
                                        int i4 = rLottieDrawable5.currentFrame;
                                        Bitmap bitmap = rLottieDrawable5.backgroundBitmap;
                                        RLottieDrawable rLottieDrawable6 = RLottieDrawable.this;
                                        i = RLottieDrawable.getFrame(j2, i4, bitmap, rLottieDrawable6.width, rLottieDrawable6.height, rLottieDrawable6.backgroundBitmap.getRowBytes(), true);
                                    } else {
                                        try {
                                            i = bitmapsCache.getFrame(rLottieDrawable5.currentFrame / i3, rLottieDrawable5.backgroundBitmap);
                                        } catch (Exception e) {
                                            FileLog.e((Throwable) e);
                                            i = 0;
                                        }
                                    }
                                    BitmapsCache bitmapsCache2 = RLottieDrawable.this.bitmapsCache;
                                    if (bitmapsCache2 != null && bitmapsCache2.needGenCache()) {
                                        if (!RLottieDrawable.this.genCacheSend) {
                                            boolean unused3 = RLottieDrawable.this.genCacheSend = true;
                                            RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnableGenerateCache);
                                        }
                                        i = -1;
                                    }
                                    if (i == -1) {
                                        RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnableNoFrame);
                                        CountDownLatch countDownLatch = RLottieDrawable.this.frameWaitSync;
                                        if (countDownLatch != null) {
                                            countDownLatch.countDown();
                                            return;
                                        }
                                        return;
                                    }
                                    RLottieDrawable rLottieDrawable7 = RLottieDrawable.this;
                                    rLottieDrawable7.nextRenderingBitmap = rLottieDrawable7.backgroundBitmap;
                                    RLottieDrawable rLottieDrawable8 = RLottieDrawable.this;
                                    int i5 = rLottieDrawable8.isDice;
                                    if (i5 == 1) {
                                        int i6 = rLottieDrawable8.currentFrame;
                                        int i7 = i6 + i3;
                                        int i8 = rLottieDrawable8.diceSwitchFramesCount;
                                        if (i8 == -1) {
                                            i8 = rLottieDrawable8.metaData[0];
                                        }
                                        if (i7 < i8) {
                                            rLottieDrawable8.currentFrame = i6 + i3;
                                        } else {
                                            rLottieDrawable8.currentFrame = 0;
                                            rLottieDrawable8.nextFrameIsLast = false;
                                            if (RLottieDrawable.this.secondNativePtr != 0) {
                                                RLottieDrawable.this.isDice = 2;
                                            }
                                        }
                                    } else if (i5 == 2) {
                                        int i9 = rLottieDrawable8.currentFrame;
                                        if (i9 + i3 < rLottieDrawable8.secondFramesCount) {
                                            rLottieDrawable8.currentFrame = i9 + i3;
                                        } else {
                                            rLottieDrawable8.nextFrameIsLast = true;
                                            RLottieDrawable.this.autoRepeatPlayCount++;
                                        }
                                    } else {
                                        int i10 = rLottieDrawable8.customEndFrame;
                                        if (i10 < 0 || !rLottieDrawable8.playInDirectionOfCustomEndFrame) {
                                            int i11 = rLottieDrawable8.currentFrame;
                                            int i12 = i11 + i3;
                                            if (i10 < 0) {
                                                i10 = rLottieDrawable8.metaData[0];
                                            }
                                            if (i12 >= i10) {
                                                int i13 = rLottieDrawable8.autoRepeat;
                                                if (i13 == 1) {
                                                    rLottieDrawable8.currentFrame = 0;
                                                    rLottieDrawable8.nextFrameIsLast = false;
                                                } else if (i13 == 2) {
                                                    rLottieDrawable8.currentFrame = 0;
                                                    rLottieDrawable8.nextFrameIsLast = true;
                                                    RLottieDrawable.this.autoRepeatPlayCount++;
                                                } else {
                                                    rLottieDrawable8.nextFrameIsLast = true;
                                                    RLottieDrawable.this.checkDispatchOnAnimationEnd();
                                                }
                                            } else if (rLottieDrawable8.autoRepeat == 3) {
                                                rLottieDrawable8.nextFrameIsLast = true;
                                                RLottieDrawable.this.autoRepeatPlayCount++;
                                            } else {
                                                rLottieDrawable8.currentFrame = i11 + i3;
                                                rLottieDrawable8.nextFrameIsLast = false;
                                            }
                                        } else {
                                            int i14 = rLottieDrawable8.currentFrame;
                                            if (i14 > i10) {
                                                if (i14 - i3 >= i10) {
                                                    rLottieDrawable8.currentFrame = i14 - i3;
                                                    rLottieDrawable8.nextFrameIsLast = false;
                                                } else {
                                                    rLottieDrawable8.nextFrameIsLast = true;
                                                    RLottieDrawable.this.checkDispatchOnAnimationEnd();
                                                }
                                            } else if (i14 + i3 < i10) {
                                                rLottieDrawable8.currentFrame = i14 + i3;
                                                rLottieDrawable8.nextFrameIsLast = false;
                                            } else {
                                                rLottieDrawable8.nextFrameIsLast = true;
                                                RLottieDrawable.this.checkDispatchOnAnimationEnd();
                                            }
                                        }
                                    }
                                } catch (Exception e2) {
                                    FileLog.e((Throwable) e2);
                                }
                            }
                            RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnable);
                            CountDownLatch countDownLatch2 = RLottieDrawable.this.frameWaitSync;
                            if (countDownLatch2 != null) {
                                countDownLatch2.countDown();
                                return;
                            }
                            return;
                        }
                    }
                    CountDownLatch countDownLatch3 = RLottieDrawable.this.frameWaitSync;
                    if (countDownLatch3 != null) {
                        countDownLatch3.countDown();
                    }
                    RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnableNoFrame);
                }
            }
        };
        this.width = i;
        this.height = i2;
        this.isDice = 1;
        if ("🎲".equals(str)) {
            str2 = readRes((File) null, NUM);
            this.diceSwitchFramesCount = 60;
        } else {
            str2 = "🎯".equals(str) ? readRes((File) null, NUM) : null;
        }
        getPaint().setFlags(2);
        if (TextUtils.isEmpty(str2)) {
            this.timeBetweenFrames = 16;
            return;
        }
        this.nativePtr = createWithJson(str2, "dice", iArr, (int[]) null);
        this.timeBetweenFrames = Math.max(16, (int) (1000.0f / ((float) iArr[1])));
    }

    /* access modifiers changed from: private */
    public void checkDispatchOnAnimationEnd() {
        Runnable runnable = this.onAnimationEndListener;
        if (runnable != null) {
            runnable.run();
            this.onAnimationEndListener = null;
        }
    }

    public void setOnAnimationEndListener(Runnable runnable) {
        this.onAnimationEndListener = runnable;
    }

    public boolean setBaseDice(File file2) {
        if (this.nativePtr == 0 && !this.loadingInBackground) {
            String readRes = readRes(file2, 0);
            if (TextUtils.isEmpty(readRes)) {
                return false;
            }
            this.loadingInBackground = true;
            Utilities.globalQueue.postRunnable(new RLottieDrawable$$ExternalSyntheticLambda2(this, readRes));
        }
        return true;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setBaseDice$1(String str) {
        this.nativePtr = createWithJson(str, "dice", this.metaData, (int[]) null);
        AndroidUtilities.runOnUIThread(new RLottieDrawable$$ExternalSyntheticLambda0(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setBaseDice$0() {
        this.loadingInBackground = false;
        if (this.secondLoadingInBackground || !this.destroyAfterLoading) {
            this.timeBetweenFrames = Math.max(16, (int) (1000.0f / ((float) this.metaData[1])));
            scheduleNextGetFrame();
            invalidateInternal();
            return;
        }
        recycle();
    }

    public boolean hasBaseDice() {
        return this.nativePtr != 0 || this.loadingInBackground;
    }

    public boolean setDiceNumber(File file2, boolean z) {
        if (this.secondNativePtr == 0 && !this.secondLoadingInBackground) {
            String readRes = readRes(file2, 0);
            if (TextUtils.isEmpty(readRes)) {
                return false;
            }
            if (z && this.nextRenderingBitmap == null && this.renderingBitmap == null && this.loadFrameTask == null) {
                this.isDice = 2;
                this.setLastFrame = true;
            }
            this.secondLoadingInBackground = true;
            Utilities.globalQueue.postRunnable(new RLottieDrawable$$ExternalSyntheticLambda3(this, readRes));
        }
        return true;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setDiceNumber$4(String str) {
        if (this.destroyAfterLoading) {
            AndroidUtilities.runOnUIThread(new RLottieDrawable$$ExternalSyntheticLambda1(this));
            return;
        }
        int[] iArr = new int[3];
        this.secondNativePtr = createWithJson(str, "dice", iArr, (int[]) null);
        AndroidUtilities.runOnUIThread(new RLottieDrawable$$ExternalSyntheticLambda4(this, iArr));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setDiceNumber$2() {
        this.secondLoadingInBackground = false;
        if (!this.loadingInBackground && this.destroyAfterLoading) {
            recycle();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setDiceNumber$3(int[] iArr) {
        this.secondLoadingInBackground = false;
        if (this.destroyAfterLoading) {
            recycle();
            return;
        }
        this.secondFramesCount = iArr[0];
        this.timeBetweenFrames = Math.max(16, (int) (1000.0f / ((float) iArr[1])));
        scheduleNextGetFrame();
        invalidateInternal();
    }

    public RLottieDrawable(int i, String str, int i2, int i3, boolean z, int[] iArr) {
        int[] iArr2 = new int[3];
        this.metaData = iArr2;
        this.customEndFrame = -1;
        this.newColorUpdates = new HashMap<>();
        this.pendingColorUpdates = new HashMap<>();
        this.parentViews = new ArrayList<>();
        this.diceSwitchFramesCount = -1;
        this.autoRepeat = 1;
        this.scaleX = 1.0f;
        this.scaleY = 1.0f;
        this.dstRect = new RectF();
        this.uiRunnableNoFrame = new Runnable() {
            public void run() {
                RLottieDrawable rLottieDrawable = RLottieDrawable.this;
                rLottieDrawable.loadFrameTask = null;
                rLottieDrawable.decodeFrameFinishedInternal();
                if (RLottieDrawable.this.onFrameReadyRunnable != null) {
                    RLottieDrawable.this.onFrameReadyRunnable.run();
                }
            }
        };
        this.uiRunnable = new Runnable() {
            public void run() {
                boolean unused = RLottieDrawable.this.singleFrameDecoded = true;
                RLottieDrawable.this.invalidateInternal();
                RLottieDrawable.this.decodeFrameFinishedInternal();
                if (RLottieDrawable.this.onFrameReadyRunnable != null) {
                    RLottieDrawable.this.onFrameReadyRunnable.run();
                }
            }
        };
        this.uiRunnableGenerateCache = new Runnable() {
            public void run() {
                if (!RLottieDrawable.this.isRecycled) {
                    RLottieDrawable rLottieDrawable = RLottieDrawable.this;
                    if (!rLottieDrawable.destroyWhenDone && rLottieDrawable.canLoadFrames()) {
                        RLottieDrawable rLottieDrawable2 = RLottieDrawable.this;
                        if (rLottieDrawable2.cacheGenerateTask == null) {
                            rLottieDrawable2.startTime = System.currentTimeMillis();
                            RLottieDrawable rLottieDrawable3 = RLottieDrawable.this;
                            rLottieDrawable3.generatingCache = true;
                            DispatchQueue dispatchQueue = RLottieDrawable.lottieCacheGenerateQueue;
                            RLottieDrawable$3$$ExternalSyntheticLambda0 rLottieDrawable$3$$ExternalSyntheticLambda0 = new RLottieDrawable$3$$ExternalSyntheticLambda0(this);
                            rLottieDrawable3.cacheGenerateTask = rLottieDrawable$3$$ExternalSyntheticLambda0;
                            dispatchQueue.postRunnable(rLottieDrawable$3$$ExternalSyntheticLambda0);
                        }
                    }
                }
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$run$0() {
                RLottieDrawable.this.bitmapsCache.createCache();
                RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnableCacheFinished);
            }
        };
        this.uiRunnableCacheFinished = new Runnable() {
            public void run() {
                RLottieDrawable rLottieDrawable = RLottieDrawable.this;
                rLottieDrawable.cacheGenerateTask = null;
                rLottieDrawable.generatingCache = false;
                rLottieDrawable.decodeFrameFinishedInternal();
            }
        };
        this.loadFrameRunnable = new Runnable() {
            public void run() {
                long j;
                int i;
                BitmapsCache bitmapsCache;
                if (!RLottieDrawable.this.isRecycled) {
                    if (RLottieDrawable.this.canLoadFrames()) {
                        RLottieDrawable rLottieDrawable = RLottieDrawable.this;
                        if (!(rLottieDrawable.isDice == 2 && rLottieDrawable.secondNativePtr == 0)) {
                            if (RLottieDrawable.this.backgroundBitmap == null) {
                                try {
                                    RLottieDrawable rLottieDrawable2 = RLottieDrawable.this;
                                    rLottieDrawable2.backgroundBitmap = Bitmap.createBitmap(rLottieDrawable2.width, rLottieDrawable2.height, Bitmap.Config.ARGB_8888);
                                } catch (Throwable th) {
                                    FileLog.e(th);
                                }
                            }
                            if (RLottieDrawable.this.backgroundBitmap != null) {
                                try {
                                    if (!RLottieDrawable.this.pendingColorUpdates.isEmpty()) {
                                        for (Map.Entry entry : RLottieDrawable.this.pendingColorUpdates.entrySet()) {
                                            RLottieDrawable.setLayerColor(RLottieDrawable.this.nativePtr, (String) entry.getKey(), ((Integer) entry.getValue()).intValue());
                                        }
                                        RLottieDrawable.this.pendingColorUpdates.clear();
                                    }
                                } catch (Exception unused) {
                                }
                                if (!(RLottieDrawable.this.pendingReplaceColors == null || RLottieDrawable.this.nativePtr == 0)) {
                                    RLottieDrawable.replaceColors(RLottieDrawable.this.nativePtr, RLottieDrawable.this.pendingReplaceColors);
                                    int[] unused2 = RLottieDrawable.this.pendingReplaceColors = null;
                                }
                                try {
                                    RLottieDrawable rLottieDrawable3 = RLottieDrawable.this;
                                    int i2 = rLottieDrawable3.isDice;
                                    if (i2 == 1) {
                                        j = rLottieDrawable3.nativePtr;
                                    } else if (i2 == 2) {
                                        j = rLottieDrawable3.secondNativePtr;
                                        if (RLottieDrawable.this.setLastFrame) {
                                            RLottieDrawable rLottieDrawable4 = RLottieDrawable.this;
                                            rLottieDrawable4.currentFrame = rLottieDrawable4.secondFramesCount - 1;
                                        }
                                    } else {
                                        j = rLottieDrawable3.nativePtr;
                                    }
                                    long j2 = j;
                                    int i3 = RLottieDrawable.this.shouldLimitFps ? 2 : 1;
                                    RLottieDrawable rLottieDrawable5 = RLottieDrawable.this;
                                    if (!rLottieDrawable5.precache || (bitmapsCache = rLottieDrawable5.bitmapsCache) == null) {
                                        int i4 = rLottieDrawable5.currentFrame;
                                        Bitmap bitmap = rLottieDrawable5.backgroundBitmap;
                                        RLottieDrawable rLottieDrawable6 = RLottieDrawable.this;
                                        i = RLottieDrawable.getFrame(j2, i4, bitmap, rLottieDrawable6.width, rLottieDrawable6.height, rLottieDrawable6.backgroundBitmap.getRowBytes(), true);
                                    } else {
                                        try {
                                            i = bitmapsCache.getFrame(rLottieDrawable5.currentFrame / i3, rLottieDrawable5.backgroundBitmap);
                                        } catch (Exception e) {
                                            FileLog.e((Throwable) e);
                                            i = 0;
                                        }
                                    }
                                    BitmapsCache bitmapsCache2 = RLottieDrawable.this.bitmapsCache;
                                    if (bitmapsCache2 != null && bitmapsCache2.needGenCache()) {
                                        if (!RLottieDrawable.this.genCacheSend) {
                                            boolean unused3 = RLottieDrawable.this.genCacheSend = true;
                                            RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnableGenerateCache);
                                        }
                                        i = -1;
                                    }
                                    if (i == -1) {
                                        RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnableNoFrame);
                                        CountDownLatch countDownLatch = RLottieDrawable.this.frameWaitSync;
                                        if (countDownLatch != null) {
                                            countDownLatch.countDown();
                                            return;
                                        }
                                        return;
                                    }
                                    RLottieDrawable rLottieDrawable7 = RLottieDrawable.this;
                                    rLottieDrawable7.nextRenderingBitmap = rLottieDrawable7.backgroundBitmap;
                                    RLottieDrawable rLottieDrawable8 = RLottieDrawable.this;
                                    int i5 = rLottieDrawable8.isDice;
                                    if (i5 == 1) {
                                        int i6 = rLottieDrawable8.currentFrame;
                                        int i7 = i6 + i3;
                                        int i8 = rLottieDrawable8.diceSwitchFramesCount;
                                        if (i8 == -1) {
                                            i8 = rLottieDrawable8.metaData[0];
                                        }
                                        if (i7 < i8) {
                                            rLottieDrawable8.currentFrame = i6 + i3;
                                        } else {
                                            rLottieDrawable8.currentFrame = 0;
                                            rLottieDrawable8.nextFrameIsLast = false;
                                            if (RLottieDrawable.this.secondNativePtr != 0) {
                                                RLottieDrawable.this.isDice = 2;
                                            }
                                        }
                                    } else if (i5 == 2) {
                                        int i9 = rLottieDrawable8.currentFrame;
                                        if (i9 + i3 < rLottieDrawable8.secondFramesCount) {
                                            rLottieDrawable8.currentFrame = i9 + i3;
                                        } else {
                                            rLottieDrawable8.nextFrameIsLast = true;
                                            RLottieDrawable.this.autoRepeatPlayCount++;
                                        }
                                    } else {
                                        int i10 = rLottieDrawable8.customEndFrame;
                                        if (i10 < 0 || !rLottieDrawable8.playInDirectionOfCustomEndFrame) {
                                            int i11 = rLottieDrawable8.currentFrame;
                                            int i12 = i11 + i3;
                                            if (i10 < 0) {
                                                i10 = rLottieDrawable8.metaData[0];
                                            }
                                            if (i12 >= i10) {
                                                int i13 = rLottieDrawable8.autoRepeat;
                                                if (i13 == 1) {
                                                    rLottieDrawable8.currentFrame = 0;
                                                    rLottieDrawable8.nextFrameIsLast = false;
                                                } else if (i13 == 2) {
                                                    rLottieDrawable8.currentFrame = 0;
                                                    rLottieDrawable8.nextFrameIsLast = true;
                                                    RLottieDrawable.this.autoRepeatPlayCount++;
                                                } else {
                                                    rLottieDrawable8.nextFrameIsLast = true;
                                                    RLottieDrawable.this.checkDispatchOnAnimationEnd();
                                                }
                                            } else if (rLottieDrawable8.autoRepeat == 3) {
                                                rLottieDrawable8.nextFrameIsLast = true;
                                                RLottieDrawable.this.autoRepeatPlayCount++;
                                            } else {
                                                rLottieDrawable8.currentFrame = i11 + i3;
                                                rLottieDrawable8.nextFrameIsLast = false;
                                            }
                                        } else {
                                            int i14 = rLottieDrawable8.currentFrame;
                                            if (i14 > i10) {
                                                if (i14 - i3 >= i10) {
                                                    rLottieDrawable8.currentFrame = i14 - i3;
                                                    rLottieDrawable8.nextFrameIsLast = false;
                                                } else {
                                                    rLottieDrawable8.nextFrameIsLast = true;
                                                    RLottieDrawable.this.checkDispatchOnAnimationEnd();
                                                }
                                            } else if (i14 + i3 < i10) {
                                                rLottieDrawable8.currentFrame = i14 + i3;
                                                rLottieDrawable8.nextFrameIsLast = false;
                                            } else {
                                                rLottieDrawable8.nextFrameIsLast = true;
                                                RLottieDrawable.this.checkDispatchOnAnimationEnd();
                                            }
                                        }
                                    }
                                } catch (Exception e2) {
                                    FileLog.e((Throwable) e2);
                                }
                            }
                            RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnable);
                            CountDownLatch countDownLatch2 = RLottieDrawable.this.frameWaitSync;
                            if (countDownLatch2 != null) {
                                countDownLatch2.countDown();
                                return;
                            }
                            return;
                        }
                    }
                    CountDownLatch countDownLatch3 = RLottieDrawable.this.frameWaitSync;
                    if (countDownLatch3 != null) {
                        countDownLatch3.countDown();
                    }
                    RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnableNoFrame);
                }
            }
        };
        this.width = i2;
        this.height = i3;
        this.autoRepeat = 0;
        String readRes = readRes((File) null, i);
        if (!TextUtils.isEmpty(readRes)) {
            getPaint().setFlags(2);
            this.nativePtr = createWithJson(readRes, str, iArr2, iArr);
            this.timeBetweenFrames = Math.max(16, (int) (1000.0f / ((float) iArr2[1])));
            if (z) {
                setAllowDecodeSingleFrame(true);
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:30:0x0068 A[SYNTHETIC, Splitter:B:30:0x0068] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.String readRes(java.io.File r7, int r8) {
        /*
            java.lang.ThreadLocal<byte[]> r0 = readBufferLocal
            java.lang.Object r0 = r0.get()
            byte[] r0 = (byte[]) r0
            if (r0 != 0) goto L_0x0013
            r0 = 65536(0x10000, float:9.18355E-41)
            byte[] r0 = new byte[r0]
            java.lang.ThreadLocal<byte[]> r1 = readBufferLocal
            r1.set(r0)
        L_0x0013:
            r1 = 0
            if (r7 == 0) goto L_0x001c
            java.io.FileInputStream r8 = new java.io.FileInputStream     // Catch:{ all -> 0x0065 }
            r8.<init>(r7)     // Catch:{ all -> 0x0065 }
            goto L_0x0026
        L_0x001c:
            android.content.Context r7 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x0065 }
            android.content.res.Resources r7 = r7.getResources()     // Catch:{ all -> 0x0065 }
            java.io.InputStream r8 = r7.openRawResource(r8)     // Catch:{ all -> 0x0065 }
        L_0x0026:
            java.lang.ThreadLocal<byte[]> r7 = bufferLocal     // Catch:{ all -> 0x0066 }
            java.lang.Object r7 = r7.get()     // Catch:{ all -> 0x0066 }
            byte[] r7 = (byte[]) r7     // Catch:{ all -> 0x0066 }
            r2 = 0
            if (r7 != 0) goto L_0x003a
            r7 = 4096(0x1000, float:5.74E-42)
            byte[] r7 = new byte[r7]     // Catch:{ all -> 0x0066 }
            java.lang.ThreadLocal<byte[]> r3 = bufferLocal     // Catch:{ all -> 0x0066 }
            r3.set(r7)     // Catch:{ all -> 0x0066 }
        L_0x003a:
            r3 = 0
        L_0x003b:
            int r4 = r7.length     // Catch:{ all -> 0x0066 }
            int r4 = r8.read(r7, r2, r4)     // Catch:{ all -> 0x0066 }
            if (r4 < 0) goto L_0x005c
            int r5 = r0.length     // Catch:{ all -> 0x0066 }
            int r6 = r3 + r4
            if (r5 >= r6) goto L_0x0055
            int r5 = r0.length     // Catch:{ all -> 0x0066 }
            int r5 = r5 * 2
            byte[] r5 = new byte[r5]     // Catch:{ all -> 0x0066 }
            java.lang.System.arraycopy(r0, r2, r5, r2, r3)     // Catch:{ all -> 0x0066 }
            java.lang.ThreadLocal<byte[]> r0 = readBufferLocal     // Catch:{ all -> 0x0066 }
            r0.set(r5)     // Catch:{ all -> 0x0066 }
            r0 = r5
        L_0x0055:
            if (r4 <= 0) goto L_0x003b
            java.lang.System.arraycopy(r7, r2, r0, r3, r4)     // Catch:{ all -> 0x0066 }
            r3 = r6
            goto L_0x003b
        L_0x005c:
            r8.close()     // Catch:{ all -> 0x005f }
        L_0x005f:
            java.lang.String r7 = new java.lang.String
            r7.<init>(r0, r2, r3)
            return r7
        L_0x0065:
            r8 = r1
        L_0x0066:
            if (r8 == 0) goto L_0x006b
            r8.close()     // Catch:{ all -> 0x006b }
        L_0x006b:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.RLottieDrawable.readRes(java.io.File, int):java.lang.String");
    }

    public int getCurrentFrame() {
        return this.currentFrame;
    }

    public int getCustomEndFrame() {
        return this.customEndFrame;
    }

    public long getDuration() {
        int[] iArr = this.metaData;
        return (long) ((((float) iArr[0]) / ((float) iArr[1])) * 1000.0f);
    }

    public void setPlayInDirectionOfCustomEndFrame(boolean z) {
        this.playInDirectionOfCustomEndFrame = z;
    }

    public boolean setCustomEndFrame(int i) {
        if (this.customEndFrame == i || i > this.metaData[0]) {
            return false;
        }
        this.customEndFrame = i;
        return true;
    }

    public int getFramesCount() {
        return this.metaData[0];
    }

    public void addParentView(ImageReceiver imageReceiver) {
        if (imageReceiver != null) {
            this.parentViews.add(imageReceiver);
        }
    }

    public void removeParentView(ImageReceiver imageReceiver) {
        if (imageReceiver != null) {
            this.parentViews.remove(imageReceiver);
        }
    }

    /* access modifiers changed from: protected */
    public boolean hasParentView() {
        return (this.parentViews.isEmpty() && this.masterParent == null && getCallback() == null) ? false : true;
    }

    /* access modifiers changed from: protected */
    public void invalidateInternal() {
        for (int i = 0; i < this.parentViews.size(); i++) {
            this.parentViews.get(i).invalidate();
        }
        View view = this.masterParent;
        if (view != null) {
            view.invalidate();
        }
        if (getCallback() != null) {
            invalidateSelf();
        }
    }

    public void setAllowDecodeSingleFrame(boolean z) {
        this.decodeSingleFrame = z;
        if (z) {
            scheduleNextGetFrame();
        }
    }

    public void recycle() {
        this.isRunning = false;
        this.isRecycled = true;
        checkRunningTasks();
        if (this.loadingInBackground || this.secondLoadingInBackground) {
            this.destroyAfterLoading = true;
        } else if (this.loadFrameTask == null && this.cacheGenerateTask == null) {
            if (this.nativePtr != 0) {
                destroy(this.nativePtr);
                this.nativePtr = 0;
            }
            if (this.secondNativePtr != 0) {
                destroy(this.secondNativePtr);
                this.secondNativePtr = 0;
            }
            BitmapsCache bitmapsCache2 = this.bitmapsCache;
            if (bitmapsCache2 != null) {
                bitmapsCache2.recycle();
                this.bitmapsCache = null;
            }
            recycleResources();
        } else {
            this.destroyWhenDone = true;
        }
    }

    public void setAutoRepeat(int i) {
        if (this.autoRepeat != 2 || i != 3 || this.currentFrame == 0) {
            this.autoRepeat = i;
        }
    }

    /* access modifiers changed from: protected */
    public void finalize() throws Throwable {
        try {
            recycle();
        } finally {
            super.finalize();
        }
    }

    public void start() {
        if (this.isRunning) {
            return;
        }
        if ((this.autoRepeat < 2 || this.autoRepeatPlayCount == 0) && this.customEndFrame != this.currentFrame) {
            this.isRunning = true;
            if (this.invalidateOnProgressSet) {
                this.isInvalid = true;
                if (this.loadFrameTask != null) {
                    this.doNotRemoveInvalidOnFrameReady = true;
                }
            }
            scheduleNextGetFrame();
            invalidateInternal();
        }
    }

    public boolean restart() {
        if (this.autoRepeat < 2 || this.autoRepeatPlayCount == 0) {
            return false;
        }
        this.autoRepeatPlayCount = 0;
        this.autoRepeat = 2;
        start();
        return true;
    }

    public void setVibrationPattern(HashMap<Integer, Integer> hashMap) {
        this.vibrationPattern = hashMap;
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

    public void replaceColors(int[] iArr) {
        this.newReplaceColors = iArr;
        requestRedrawColors();
    }

    public void setLayerColor(String str, int i) {
        this.newColorUpdates.put(str, Integer.valueOf(i));
        requestRedrawColors();
    }

    private void requestRedrawColors() {
        if (!this.applyingLayerColors && !this.isRunning && this.decodeSingleFrame) {
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

    /* access modifiers changed from: protected */
    public boolean scheduleNextGetFrame() {
        return scheduleNextGetFrame(false);
    }

    /* access modifiers changed from: protected */
    public boolean scheduleNextGetFrame(boolean z) {
        boolean z2;
        if (this.loadFrameTask != null || this.nextRenderingBitmap != null || !canLoadFrames() || this.loadingInBackground || this.destroyWhenDone || ((!this.isRunning && (!(z2 = this.decodeSingleFrame) || (z2 && this.singleFrameDecoded))) || this.generatingCache)) {
            return false;
        }
        if (!this.newColorUpdates.isEmpty()) {
            this.pendingColorUpdates.putAll(this.newColorUpdates);
            this.newColorUpdates.clear();
        }
        int[] iArr = this.newReplaceColors;
        if (iArr != null) {
            this.pendingReplaceColors = iArr;
            this.newReplaceColors = null;
        }
        Runnable runnable = this.loadFrameRunnable;
        this.loadFrameTask = runnable;
        if (!z || !this.shouldLimitFps) {
            loadFrameRunnableQueue.execute(runnable);
            return true;
        }
        DispatchQueuePoolBackground.execute(runnable);
        return true;
    }

    public boolean isHeavyDrawable() {
        return this.isDice == 0;
    }

    public void stop() {
        this.isRunning = false;
    }

    public void setCurrentFrame(int i) {
        setCurrentFrame(i, true);
    }

    public void setCurrentFrame(int i, boolean z) {
        setCurrentFrame(i, z, false);
    }

    public void setCurrentFrame(int i, boolean z, boolean z2) {
        if (i >= 0 && i <= this.metaData[0]) {
            if (this.currentFrame != i || z2) {
                this.currentFrame = i;
                this.nextFrameIsLast = false;
                this.singleFrameDecoded = false;
                if (this.invalidateOnProgressSet) {
                    this.isInvalid = true;
                    if (this.loadFrameTask != null) {
                        this.doNotRemoveInvalidOnFrameReady = true;
                    }
                }
                if ((!z || z2) && this.waitingForNextTask && this.nextRenderingBitmap != null) {
                    this.backgroundBitmap = this.nextRenderingBitmap;
                    this.nextRenderingBitmap = null;
                    this.loadFrameTask = null;
                    this.waitingForNextTask = false;
                }
                if (!z && this.loadFrameTask == null) {
                    this.frameWaitSync = new CountDownLatch(1);
                }
                if (z2 && !this.isRunning) {
                    this.isRunning = true;
                }
                if (!scheduleNextGetFrame(false)) {
                    this.forceFrameRedraw = true;
                } else if (!z) {
                    try {
                        this.frameWaitSync.await();
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    }
                    this.frameWaitSync = null;
                }
                invalidateSelf();
            }
        }
    }

    public void setProgressMs(long j) {
        setCurrentFrame((int) ((Math.max(0, j) / ((long) this.timeBetweenFrames)) % ((long) this.metaData[0])), true, true);
    }

    public void setProgress(float f) {
        setProgress(f, true);
    }

    public void setProgress(float f, boolean z) {
        if (f < 0.0f) {
            f = 0.0f;
        } else if (f > 1.0f) {
            f = 1.0f;
        }
        setCurrentFrame((int) (((float) this.metaData[0]) * f), z);
    }

    public void setCurrentParentView(View view) {
        this.currentParentView = view;
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

    /* access modifiers changed from: protected */
    public void onBoundsChange(Rect rect) {
        super.onBoundsChange(rect);
        this.applyTransformation = true;
    }

    private void setCurrentFrame(long j, long j2, long j3, boolean z) {
        WeakReference<Runnable> weakReference;
        Runnable runnable;
        WeakReference<Runnable> weakReference2;
        this.backgroundBitmap = this.renderingBitmap;
        this.renderingBitmap = this.nextRenderingBitmap;
        this.nextRenderingBitmap = null;
        if (this.isDice == 2 && (weakReference2 = this.onFinishCallback) != null && this.currentFrame - 1 >= this.finishFrame) {
            Runnable runnable2 = (Runnable) weakReference2.get();
            if (runnable2 != null) {
                runnable2.run();
            }
            this.onFinishCallback = null;
        }
        if (this.nextFrameIsLast) {
            stop();
        }
        this.loadFrameTask = null;
        if (this.doNotRemoveInvalidOnFrameReady) {
            this.doNotRemoveInvalidOnFrameReady = false;
        } else if (this.isInvalid) {
            this.isInvalid = false;
        }
        this.singleFrameDecoded = true;
        this.waitingForNextTask = false;
        if (AndroidUtilities.screenRefreshRate <= 60.0f) {
            this.lastFrameTime = j;
        } else {
            this.lastFrameTime = j - Math.min(16, j2 - j3);
        }
        if (z && this.forceFrameRedraw) {
            this.singleFrameDecoded = false;
            this.forceFrameRedraw = false;
        }
        if (this.isDice == 0 && (weakReference = this.onFinishCallback) != null && this.currentFrame >= this.finishFrame && (runnable = (Runnable) weakReference.get()) != null) {
            runnable.run();
        }
        scheduleNextGetFrame(true);
    }

    public void draw(Canvas canvas) {
        drawInternal(canvas, false, 0);
    }

    public void drawInBackground(Canvas canvas, float f, float f2, float f3, float f4, int i) {
        if (this.dstRectBackground == null) {
            this.dstRectBackground = new RectF();
            Paint paint = new Paint(1);
            this.backgroundPaint = paint;
            paint.setFilterBitmap(true);
        }
        this.backgroundPaint.setAlpha(i);
        this.dstRectBackground.set(f, f2, f3 + f, f4 + f2);
        drawInternal(canvas, true, 0);
    }

    public void drawInternal(Canvas canvas, boolean z, long j) {
        boolean z2;
        float f;
        float f2;
        if (canLoadFrames() && !this.destroyWhenDone) {
            boolean z3 = false;
            if (!z) {
                updateCurrentFrame(j, false);
            }
            RectF rectF = z ? this.dstRectBackground : this.dstRect;
            Paint paint = z ? this.backgroundPaint : getPaint();
            if (paint.getAlpha() != 0 && !this.isInvalid && this.renderingBitmap != null) {
                if (!z) {
                    rectF.set(getBounds());
                    if (this.applyTransformation) {
                        this.scaleX = rectF.width() / ((float) this.width);
                        this.scaleY = rectF.height() / ((float) this.height);
                        this.applyTransformation = false;
                        if (Math.abs(rectF.width() - ((float) this.width)) >= ((float) AndroidUtilities.dp(1.0f)) || Math.abs(rectF.height() - ((float) this.height)) >= ((float) AndroidUtilities.dp(1.0f))) {
                            z3 = true;
                        }
                        this.needScale = z3;
                    }
                    f2 = this.scaleX;
                    f = this.scaleY;
                    z2 = this.needScale;
                } else {
                    float width2 = rectF.width() / ((float) this.width);
                    float height2 = rectF.height() / ((float) this.height);
                    if (Math.abs(rectF.width() - ((float) this.width)) >= ((float) AndroidUtilities.dp(1.0f)) || Math.abs(rectF.height() - ((float) this.height)) >= ((float) AndroidUtilities.dp(1.0f))) {
                        z3 = true;
                    }
                    z2 = z3;
                    f2 = width2;
                    f = height2;
                }
                if (!z2) {
                    canvas.drawBitmap(this.renderingBitmap, rectF.left, rectF.top, paint);
                } else {
                    canvas.save();
                    canvas.translate(rectF.left, rectF.top);
                    canvas.scale(f2, f);
                    canvas.drawBitmap(this.renderingBitmap, 0.0f, 0.0f, paint);
                    canvas.restore();
                }
                if (this.isRunning && !z) {
                    invalidateInternal();
                }
            }
        }
    }

    public void updateCurrentFrame(long j, boolean z) {
        int i;
        Integer num;
        if (j == 0) {
            j = System.currentTimeMillis();
        }
        long j2 = j;
        long j3 = j2 - this.lastFrameTime;
        float f = AndroidUtilities.screenRefreshRate;
        if (f <= 60.0f || (z && f <= 80.0f)) {
            i = this.timeBetweenFrames - 6;
        } else {
            i = this.timeBetweenFrames;
        }
        if (this.isRunning) {
            if (this.renderingBitmap == null && this.nextRenderingBitmap == null) {
                scheduleNextGetFrame(true);
            } else if (this.nextRenderingBitmap == null) {
            } else {
                if (this.renderingBitmap == null || (j3 >= ((long) i) && !this.skipFrameUpdate)) {
                    HashMap<Integer, Integer> hashMap = this.vibrationPattern;
                    if (!(hashMap == null || this.currentParentView == null || (num = hashMap.get(Integer.valueOf(this.currentFrame - 1))) == null)) {
                        this.currentParentView.performHapticFeedback(num.intValue() == 1 ? 0 : 3, 2);
                    }
                    setCurrentFrame(j2, j3, (long) i, false);
                }
            }
        } else if ((this.forceFrameRedraw || (this.decodeSingleFrame && j3 >= ((long) i))) && this.nextRenderingBitmap != null) {
            setCurrentFrame(j2, j3, (long) i, true);
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

    public Bitmap getBackgroundBitmap() {
        return this.backgroundBitmap;
    }

    public Bitmap getAnimatedBitmap() {
        if (this.renderingBitmap != null) {
            return this.renderingBitmap;
        }
        if (this.nextRenderingBitmap != null) {
            return this.nextRenderingBitmap;
        }
        return null;
    }

    public boolean hasBitmap() {
        return !this.isRecycled && !(this.renderingBitmap == null && this.nextRenderingBitmap == null) && !this.isInvalid;
    }

    public void setInvalidateOnProgressSet(boolean z) {
        this.invalidateOnProgressSet = z;
    }

    public boolean isGeneratingCache() {
        return this.cacheGenerateTask != null;
    }

    public void setOnFrameReadyRunnable(Runnable runnable) {
        this.onFrameReadyRunnable = runnable;
    }

    public boolean isLastFrame() {
        return this.currentFrame == getFramesCount() - 1;
    }

    public void prepareForGenerateCache() {
        String file2 = this.args.file.toString();
        NativePtrArgs nativePtrArgs = this.args;
        long create = create(file2, nativePtrArgs.json, this.width, this.height, new int[3], false, nativePtrArgs.colorReplacement, false, nativePtrArgs.fitzModifier);
        this.generateCacheNativePtr = create;
        if (create == 0) {
            this.file.delete();
        }
    }

    public int getNextFrame(Bitmap bitmap) {
        long j = this.generateCacheNativePtr;
        if (j == 0) {
            return -1;
        }
        int i = this.shouldLimitFps ? 2 : 1;
        if (getFrame(j, this.generateCacheFramePointer, bitmap, this.width, this.height, bitmap.getRowBytes(), true) == -5) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return getNextFrame(bitmap);
        }
        int i2 = this.generateCacheFramePointer + i;
        this.generateCacheFramePointer = i2;
        if (i2 > this.metaData[0]) {
            return 0;
        }
        return 1;
    }

    public void releaseForGenerateCache() {
        long j = this.generateCacheNativePtr;
        if (j != 0) {
            destroy(j);
            this.generateCacheNativePtr = 0;
        }
    }

    public Bitmap getFirstFrame(Bitmap bitmap) {
        String file2 = this.args.file.toString();
        NativePtrArgs nativePtrArgs = this.args;
        long create = create(file2, nativePtrArgs.json, this.width, this.height, new int[3], false, nativePtrArgs.colorReplacement, false, nativePtrArgs.fitzModifier);
        if (create == 0) {
            return bitmap;
        }
        getFrame(create, 0, bitmap, this.width, this.height, bitmap.getRowBytes(), true);
        destroy(create);
        return bitmap;
    }

    /* access modifiers changed from: package-private */
    public void setMasterParent(View view) {
        this.masterParent = view;
    }

    public boolean canLoadFrames() {
        if (this.precache) {
            if (this.bitmapsCache != null) {
                return true;
            }
            return false;
        } else if (this.nativePtr != 0) {
            return true;
        } else {
            return false;
        }
    }

    private class NativePtrArgs {
        public int[] colorReplacement;
        File file;
        public int fitzModifier;
        String json;

        private NativePtrArgs() {
        }
    }
}
