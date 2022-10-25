package org.telegram.ui.Components;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.DispatchQueuePool;
import org.telegram.messenger.DispatchQueuePoolBackground;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.R;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.utils.BitmapsCache;
import org.telegram.ui.Components.RLottieDrawable;
/* loaded from: classes3.dex */
public class RLottieDrawable extends BitmapDrawable implements Animatable, BitmapsCache.Cacheable {
    public static DispatchQueue lottieCacheGenerateQueue;
    private boolean allowVibration;
    private boolean applyTransformation;
    private boolean applyingLayerColors;
    NativePtrArgs args;
    protected int autoRepeat;
    protected int autoRepeatCount;
    protected int autoRepeatPlayCount;
    protected volatile Bitmap backgroundBitmap;
    private Paint backgroundPaint;
    BitmapsCache bitmapsCache;
    protected Runnable cacheGenerateTask;
    private Runnable cancelCache;
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
    private boolean fallbackCache;
    File file;
    private int finishFrame;
    private boolean forceFrameRedraw;
    protected CountDownLatch frameWaitSync;
    private boolean genCacheSend;
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
    private Runnable onFrameReadyRunnable;
    private ArrayList<ImageReceiver> parentViews;
    private volatile HashMap<String, Integer> pendingColorUpdates;
    private int[] pendingReplaceColors;
    protected boolean playInDirectionOfCustomEndFrame;
    boolean precache;
    protected volatile Bitmap renderingBitmap;
    private boolean resetVibrationAfterRestart;
    private float scaleX;
    private float scaleY;
    protected int secondFramesCount;
    protected boolean secondLoadingInBackground;
    protected volatile long secondNativePtr;
    protected volatile boolean setLastFrame;
    private boolean shouldLimitFps;
    private boolean singleFrameDecoded;
    public boolean skipFrameUpdate;
    protected int timeBetweenFrames;
    protected Runnable uiRunnable;
    private Runnable uiRunnableCacheFinished;
    private Runnable uiRunnableGenerateCache;
    protected Runnable uiRunnableNoFrame;
    private HashMap<Integer, Integer> vibrationPattern;
    protected boolean waitingForNextTask;
    protected final int width;
    protected static final Handler uiHandler = new Handler(Looper.getMainLooper());
    private static ThreadLocal<byte[]> readBufferLocal = new ThreadLocal<>();
    private static ThreadLocal<byte[]> bufferLocal = new ThreadLocal<>();
    private static final DispatchQueuePool loadFrameRunnableQueue = new DispatchQueuePool(4);

    public static native long create(String str, String str2, int i, int i2, int[] iArr, boolean z, int[] iArr2, boolean z2, int i3);

    /* JADX INFO: Access modifiers changed from: protected */
    public static native long createWithJson(String str, String str2, int[] iArr, int[] iArr2);

    public static native void destroy(long j);

    public static native int getFrame(long j, int i, Bitmap bitmap, int i2, int i3, int i4, boolean z);

    /* JADX INFO: Access modifiers changed from: private */
    public static native void replaceColors(long j, int[] iArr);

    /* JADX INFO: Access modifiers changed from: private */
    public static native void setLayerColor(long j, String str, int i);

    @Override // android.graphics.drawable.BitmapDrawable, android.graphics.drawable.Drawable
    public int getOpacity() {
        return -2;
    }

    public void setAutoRepeatTimeout(long j) {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: org.telegram.ui.Components.RLottieDrawable$3  reason: invalid class name */
    /* loaded from: classes3.dex */
    public class AnonymousClass3 implements Runnable {
        AnonymousClass3() {
        }

        @Override // java.lang.Runnable
        public void run() {
            if (!RLottieDrawable.this.isRecycled) {
                RLottieDrawable rLottieDrawable = RLottieDrawable.this;
                if (rLottieDrawable.destroyWhenDone || !rLottieDrawable.canLoadFrames()) {
                    return;
                }
                RLottieDrawable rLottieDrawable2 = RLottieDrawable.this;
                if (rLottieDrawable2.cacheGenerateTask != null) {
                    return;
                }
                rLottieDrawable2.generatingCache = true;
                if (RLottieDrawable.lottieCacheGenerateQueue == null) {
                    RLottieDrawable.createCacheGenQueue();
                }
                DispatchQueue dispatchQueue = RLottieDrawable.lottieCacheGenerateQueue;
                RLottieDrawable rLottieDrawable3 = RLottieDrawable.this;
                Runnable runnable = new Runnable() { // from class: org.telegram.ui.Components.RLottieDrawable$3$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        RLottieDrawable.AnonymousClass3.this.lambda$run$0();
                    }
                };
                rLottieDrawable3.cacheGenerateTask = runnable;
                dispatchQueue.postRunnable(runnable);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$run$0() {
            BitmapsCache bitmapsCache = RLottieDrawable.this.bitmapsCache;
            if (bitmapsCache != null) {
                bitmapsCache.createCache();
            }
            RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnableCacheFinished);
        }
    }

    public static void createCacheGenQueue() {
        lottieCacheGenerateQueue = new DispatchQueue("cache generator queue");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void checkRunningTasks() {
        Runnable runnable = this.cacheGenerateTask;
        if (runnable != null) {
            lottieCacheGenerateQueue.cancelRunnable(runnable);
            this.cacheGenerateTask = null;
        }
        if (hasParentView() || this.nextRenderingBitmap == null || this.loadFrameTask == null) {
            return;
        }
        this.loadFrameTask = null;
        this.nextRenderingBitmap = null;
    }

    protected void decodeFrameFinishedInternal() {
        if (this.destroyWhenDone) {
            checkRunningTasks();
            if (this.loadFrameTask == null && this.cacheGenerateTask == null && this.nativePtr != 0) {
                destroy(this.nativePtr);
                this.nativePtr = 0L;
                if (this.secondNativePtr != 0) {
                    destroy(this.secondNativePtr);
                    this.secondNativePtr = 0L;
                }
            }
        }
        if ((this.nativePtr == 0 || this.fallbackCache) && this.secondNativePtr == 0 && this.bitmapsCache == null) {
            recycleResources();
            return;
        }
        this.waitingForNextTask = true;
        if (!hasParentView()) {
            stop();
        }
        scheduleNextGetFrame();
    }

    /* JADX INFO: Access modifiers changed from: protected */
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
        } else if (this.onFinishCallback == null) {
        } else {
            this.onFinishCallback = null;
        }
    }

    public RLottieDrawable(File file, int i, int i2, BitmapsCache.CacheOptions cacheOptions, boolean z, int[] iArr, int i3) {
        int[] iArr2;
        boolean z2;
        char c;
        int[] iArr3 = new int[3];
        this.metaData = iArr3;
        this.customEndFrame = -1;
        this.newColorUpdates = new HashMap<>();
        this.pendingColorUpdates = new HashMap<>();
        this.resetVibrationAfterRestart = false;
        this.allowVibration = true;
        this.parentViews = new ArrayList<>();
        this.diceSwitchFramesCount = -1;
        this.autoRepeat = 1;
        this.autoRepeatCount = -1;
        this.scaleX = 1.0f;
        this.scaleY = 1.0f;
        this.dstRect = new RectF();
        this.uiRunnableNoFrame = new Runnable() { // from class: org.telegram.ui.Components.RLottieDrawable.1
            @Override // java.lang.Runnable
            public void run() {
                RLottieDrawable rLottieDrawable = RLottieDrawable.this;
                rLottieDrawable.loadFrameTask = null;
                rLottieDrawable.decodeFrameFinishedInternal();
                if (RLottieDrawable.this.onFrameReadyRunnable != null) {
                    RLottieDrawable.this.onFrameReadyRunnable.run();
                }
            }
        };
        this.uiRunnable = new Runnable() { // from class: org.telegram.ui.Components.RLottieDrawable.2
            @Override // java.lang.Runnable
            public void run() {
                RLottieDrawable.this.singleFrameDecoded = true;
                RLottieDrawable.this.invalidateInternal();
                RLottieDrawable.this.decodeFrameFinishedInternal();
                if (RLottieDrawable.this.onFrameReadyRunnable != null) {
                    RLottieDrawable.this.onFrameReadyRunnable.run();
                }
            }
        };
        this.uiRunnableGenerateCache = new AnonymousClass3();
        this.uiRunnableCacheFinished = new Runnable() { // from class: org.telegram.ui.Components.RLottieDrawable.4
            @Override // java.lang.Runnable
            public void run() {
                RLottieDrawable rLottieDrawable = RLottieDrawable.this;
                rLottieDrawable.cacheGenerateTask = null;
                rLottieDrawable.generatingCache = false;
                rLottieDrawable.decodeFrameFinishedInternal();
            }
        };
        this.loadFrameRunnable = new Runnable() { // from class: org.telegram.ui.Components.RLottieDrawable.5
            @Override // java.lang.Runnable
            public void run() {
                long j;
                int frame;
                BitmapsCache bitmapsCache;
                if (RLottieDrawable.this.isRecycled) {
                    return;
                }
                if (RLottieDrawable.this.canLoadFrames()) {
                    RLottieDrawable rLottieDrawable = RLottieDrawable.this;
                    if (rLottieDrawable.isDice != 2 || rLottieDrawable.secondNativePtr != 0) {
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
                            if (RLottieDrawable.this.pendingReplaceColors != null && RLottieDrawable.this.nativePtr != 0) {
                                RLottieDrawable.replaceColors(RLottieDrawable.this.nativePtr, RLottieDrawable.this.pendingReplaceColors);
                                RLottieDrawable.this.pendingReplaceColors = null;
                            }
                            try {
                                RLottieDrawable rLottieDrawable3 = RLottieDrawable.this;
                                int i4 = rLottieDrawable3.isDice;
                                if (i4 == 1) {
                                    j = rLottieDrawable3.nativePtr;
                                } else if (i4 == 2) {
                                    j = rLottieDrawable3.secondNativePtr;
                                    if (RLottieDrawable.this.setLastFrame) {
                                        RLottieDrawable rLottieDrawable4 = RLottieDrawable.this;
                                        rLottieDrawable4.currentFrame = rLottieDrawable4.secondFramesCount - 1;
                                    }
                                } else {
                                    j = rLottieDrawable3.nativePtr;
                                }
                                long j2 = j;
                                int i5 = RLottieDrawable.this.shouldLimitFps ? 2 : 1;
                                RLottieDrawable rLottieDrawable5 = RLottieDrawable.this;
                                if (rLottieDrawable5.precache && (bitmapsCache = rLottieDrawable5.bitmapsCache) != null) {
                                    try {
                                        frame = bitmapsCache.getFrame(rLottieDrawable5.currentFrame / i5, rLottieDrawable5.backgroundBitmap);
                                    } catch (Exception e) {
                                        FileLog.e(e);
                                        frame = 0;
                                    }
                                } else {
                                    int i6 = rLottieDrawable5.currentFrame;
                                    Bitmap bitmap = rLottieDrawable5.backgroundBitmap;
                                    RLottieDrawable rLottieDrawable6 = RLottieDrawable.this;
                                    frame = RLottieDrawable.getFrame(j2, i6, bitmap, rLottieDrawable6.width, rLottieDrawable6.height, rLottieDrawable6.backgroundBitmap.getRowBytes(), true);
                                }
                                BitmapsCache bitmapsCache2 = RLottieDrawable.this.bitmapsCache;
                                if (bitmapsCache2 != null && bitmapsCache2.needGenCache()) {
                                    if (!RLottieDrawable.this.genCacheSend) {
                                        RLottieDrawable.this.genCacheSend = true;
                                        RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnableGenerateCache);
                                    }
                                    frame = -1;
                                }
                                if (frame == -1) {
                                    RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnableNoFrame);
                                    CountDownLatch countDownLatch = RLottieDrawable.this.frameWaitSync;
                                    if (countDownLatch == null) {
                                        return;
                                    }
                                    countDownLatch.countDown();
                                    return;
                                }
                                RLottieDrawable rLottieDrawable7 = RLottieDrawable.this;
                                rLottieDrawable7.nextRenderingBitmap = rLottieDrawable7.backgroundBitmap;
                                RLottieDrawable rLottieDrawable8 = RLottieDrawable.this;
                                int i7 = rLottieDrawable8.isDice;
                                if (i7 == 1) {
                                    int i8 = rLottieDrawable8.currentFrame;
                                    int i9 = i8 + i5;
                                    int i10 = rLottieDrawable8.diceSwitchFramesCount;
                                    if (i10 == -1) {
                                        i10 = rLottieDrawable8.metaData[0];
                                    }
                                    if (i9 < i10) {
                                        rLottieDrawable8.currentFrame = i8 + i5;
                                    } else {
                                        rLottieDrawable8.currentFrame = 0;
                                        rLottieDrawable8.nextFrameIsLast = false;
                                        if (RLottieDrawable.this.secondNativePtr != 0) {
                                            RLottieDrawable.this.isDice = 2;
                                        }
                                        if (RLottieDrawable.this.resetVibrationAfterRestart) {
                                            RLottieDrawable.this.vibrationPattern = null;
                                            RLottieDrawable.this.resetVibrationAfterRestart = false;
                                        }
                                    }
                                } else if (i7 == 2) {
                                    int i11 = rLottieDrawable8.currentFrame;
                                    if (i11 + i5 < rLottieDrawable8.secondFramesCount) {
                                        rLottieDrawable8.currentFrame = i11 + i5;
                                    } else {
                                        rLottieDrawable8.nextFrameIsLast = true;
                                        RLottieDrawable.this.autoRepeatPlayCount++;
                                    }
                                } else {
                                    int i12 = rLottieDrawable8.customEndFrame;
                                    if (i12 >= 0 && rLottieDrawable8.playInDirectionOfCustomEndFrame) {
                                        int i13 = rLottieDrawable8.currentFrame;
                                        if (i13 > i12) {
                                            if (i13 - i5 >= i12) {
                                                rLottieDrawable8.currentFrame = i13 - i5;
                                                rLottieDrawable8.nextFrameIsLast = false;
                                            } else {
                                                rLottieDrawable8.nextFrameIsLast = true;
                                                RLottieDrawable.this.checkDispatchOnAnimationEnd();
                                            }
                                        } else if (i13 + i5 < i12) {
                                            rLottieDrawable8.currentFrame = i13 + i5;
                                            rLottieDrawable8.nextFrameIsLast = false;
                                        } else {
                                            rLottieDrawable8.nextFrameIsLast = true;
                                            RLottieDrawable.this.checkDispatchOnAnimationEnd();
                                        }
                                    } else {
                                        int i14 = rLottieDrawable8.currentFrame;
                                        int i15 = i14 + i5;
                                        if (i12 < 0) {
                                            i12 = rLottieDrawable8.metaData[0];
                                        }
                                        if (i15 < i12) {
                                            if (rLottieDrawable8.autoRepeat == 3) {
                                                rLottieDrawable8.nextFrameIsLast = true;
                                                RLottieDrawable.this.autoRepeatPlayCount++;
                                            } else {
                                                rLottieDrawable8.currentFrame = i14 + i5;
                                                rLottieDrawable8.nextFrameIsLast = false;
                                            }
                                        } else {
                                            int i16 = rLottieDrawable8.autoRepeat;
                                            if (i16 == 1) {
                                                rLottieDrawable8.currentFrame = 0;
                                                rLottieDrawable8.nextFrameIsLast = false;
                                                if (RLottieDrawable.this.resetVibrationAfterRestart) {
                                                    RLottieDrawable.this.vibrationPattern = null;
                                                    RLottieDrawable.this.resetVibrationAfterRestart = false;
                                                }
                                                RLottieDrawable rLottieDrawable9 = RLottieDrawable.this;
                                                int i17 = rLottieDrawable9.autoRepeatCount;
                                                if (i17 > 0) {
                                                    rLottieDrawable9.autoRepeatCount = i17 - 1;
                                                }
                                            } else if (i16 == 2) {
                                                rLottieDrawable8.currentFrame = 0;
                                                rLottieDrawable8.nextFrameIsLast = true;
                                                RLottieDrawable rLottieDrawable10 = RLottieDrawable.this;
                                                rLottieDrawable10.autoRepeatPlayCount++;
                                                if (rLottieDrawable10.resetVibrationAfterRestart) {
                                                    RLottieDrawable.this.vibrationPattern = null;
                                                    RLottieDrawable.this.resetVibrationAfterRestart = false;
                                                }
                                            } else {
                                                rLottieDrawable8.nextFrameIsLast = true;
                                                RLottieDrawable.this.checkDispatchOnAnimationEnd();
                                            }
                                        }
                                    }
                                }
                            } catch (Exception e2) {
                                FileLog.e(e2);
                            }
                        }
                        RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnable);
                        CountDownLatch countDownLatch2 = RLottieDrawable.this.frameWaitSync;
                        if (countDownLatch2 == null) {
                            return;
                        }
                        countDownLatch2.countDown();
                        return;
                    }
                }
                CountDownLatch countDownLatch3 = RLottieDrawable.this.frameWaitSync;
                if (countDownLatch3 != null) {
                    countDownLatch3.countDown();
                }
                RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnableNoFrame);
            }
        };
        this.width = i;
        this.height = i2;
        this.shouldLimitFps = z;
        this.precache = cacheOptions != null;
        this.fallbackCache = cacheOptions != null && cacheOptions.fallback;
        getPaint().setFlags(2);
        this.file = file;
        if (this.precache && lottieCacheGenerateQueue == null) {
            createCacheGenQueue();
        }
        if (this.precache) {
            this.bitmapsCache = new BitmapsCache(file, this, cacheOptions, i, i2, !z);
            NativePtrArgs nativePtrArgs = new NativePtrArgs();
            this.args = nativePtrArgs;
            nativePtrArgs.file = file.getAbsoluteFile();
            NativePtrArgs nativePtrArgs2 = this.args;
            nativePtrArgs2.json = null;
            nativePtrArgs2.colorReplacement = iArr;
            nativePtrArgs2.fitzModifier = i3;
            c = 1;
            z2 = false;
            iArr2 = iArr3;
            this.nativePtr = create(file.getAbsolutePath(), null, i, i2, iArr3, this.precache, iArr, this.shouldLimitFps, i3);
            if (this.fallbackCache) {
                if (this.nativePtr == 0) {
                    file.delete();
                }
            } else {
                destroy(this.nativePtr);
                this.nativePtr = 0L;
            }
        } else {
            iArr2 = iArr3;
            z2 = false;
            c = 1;
            this.nativePtr = create(file.getAbsolutePath(), null, i, i2, iArr2, this.precache, iArr, this.shouldLimitFps, i3);
            if (this.nativePtr == 0) {
                file.delete();
            }
        }
        if (this.shouldLimitFps && iArr2[c] < 60) {
            this.shouldLimitFps = z2;
        }
        this.timeBetweenFrames = Math.max(this.shouldLimitFps ? 33 : 16, (int) (1000.0f / iArr2[c]));
    }

    public RLottieDrawable(File file, String str, int i, int i2, BitmapsCache.CacheOptions cacheOptions, boolean z, int[] iArr, int i3) {
        int[] iArr2;
        char c;
        boolean z2;
        int[] iArr3 = new int[3];
        this.metaData = iArr3;
        this.customEndFrame = -1;
        this.newColorUpdates = new HashMap<>();
        this.pendingColorUpdates = new HashMap<>();
        this.resetVibrationAfterRestart = false;
        this.allowVibration = true;
        this.parentViews = new ArrayList<>();
        this.diceSwitchFramesCount = -1;
        this.autoRepeat = 1;
        this.autoRepeatCount = -1;
        this.scaleX = 1.0f;
        this.scaleY = 1.0f;
        this.dstRect = new RectF();
        this.uiRunnableNoFrame = new Runnable() { // from class: org.telegram.ui.Components.RLottieDrawable.1
            @Override // java.lang.Runnable
            public void run() {
                RLottieDrawable rLottieDrawable = RLottieDrawable.this;
                rLottieDrawable.loadFrameTask = null;
                rLottieDrawable.decodeFrameFinishedInternal();
                if (RLottieDrawable.this.onFrameReadyRunnable != null) {
                    RLottieDrawable.this.onFrameReadyRunnable.run();
                }
            }
        };
        this.uiRunnable = new Runnable() { // from class: org.telegram.ui.Components.RLottieDrawable.2
            @Override // java.lang.Runnable
            public void run() {
                RLottieDrawable.this.singleFrameDecoded = true;
                RLottieDrawable.this.invalidateInternal();
                RLottieDrawable.this.decodeFrameFinishedInternal();
                if (RLottieDrawable.this.onFrameReadyRunnable != null) {
                    RLottieDrawable.this.onFrameReadyRunnable.run();
                }
            }
        };
        this.uiRunnableGenerateCache = new AnonymousClass3();
        this.uiRunnableCacheFinished = new Runnable() { // from class: org.telegram.ui.Components.RLottieDrawable.4
            @Override // java.lang.Runnable
            public void run() {
                RLottieDrawable rLottieDrawable = RLottieDrawable.this;
                rLottieDrawable.cacheGenerateTask = null;
                rLottieDrawable.generatingCache = false;
                rLottieDrawable.decodeFrameFinishedInternal();
            }
        };
        this.loadFrameRunnable = new Runnable() { // from class: org.telegram.ui.Components.RLottieDrawable.5
            @Override // java.lang.Runnable
            public void run() {
                long j;
                int frame;
                BitmapsCache bitmapsCache;
                if (RLottieDrawable.this.isRecycled) {
                    return;
                }
                if (RLottieDrawable.this.canLoadFrames()) {
                    RLottieDrawable rLottieDrawable = RLottieDrawable.this;
                    if (rLottieDrawable.isDice != 2 || rLottieDrawable.secondNativePtr != 0) {
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
                            if (RLottieDrawable.this.pendingReplaceColors != null && RLottieDrawable.this.nativePtr != 0) {
                                RLottieDrawable.replaceColors(RLottieDrawable.this.nativePtr, RLottieDrawable.this.pendingReplaceColors);
                                RLottieDrawable.this.pendingReplaceColors = null;
                            }
                            try {
                                RLottieDrawable rLottieDrawable3 = RLottieDrawable.this;
                                int i4 = rLottieDrawable3.isDice;
                                if (i4 == 1) {
                                    j = rLottieDrawable3.nativePtr;
                                } else if (i4 == 2) {
                                    j = rLottieDrawable3.secondNativePtr;
                                    if (RLottieDrawable.this.setLastFrame) {
                                        RLottieDrawable rLottieDrawable4 = RLottieDrawable.this;
                                        rLottieDrawable4.currentFrame = rLottieDrawable4.secondFramesCount - 1;
                                    }
                                } else {
                                    j = rLottieDrawable3.nativePtr;
                                }
                                long j2 = j;
                                int i5 = RLottieDrawable.this.shouldLimitFps ? 2 : 1;
                                RLottieDrawable rLottieDrawable5 = RLottieDrawable.this;
                                if (rLottieDrawable5.precache && (bitmapsCache = rLottieDrawable5.bitmapsCache) != null) {
                                    try {
                                        frame = bitmapsCache.getFrame(rLottieDrawable5.currentFrame / i5, rLottieDrawable5.backgroundBitmap);
                                    } catch (Exception e) {
                                        FileLog.e(e);
                                        frame = 0;
                                    }
                                } else {
                                    int i6 = rLottieDrawable5.currentFrame;
                                    Bitmap bitmap = rLottieDrawable5.backgroundBitmap;
                                    RLottieDrawable rLottieDrawable6 = RLottieDrawable.this;
                                    frame = RLottieDrawable.getFrame(j2, i6, bitmap, rLottieDrawable6.width, rLottieDrawable6.height, rLottieDrawable6.backgroundBitmap.getRowBytes(), true);
                                }
                                BitmapsCache bitmapsCache2 = RLottieDrawable.this.bitmapsCache;
                                if (bitmapsCache2 != null && bitmapsCache2.needGenCache()) {
                                    if (!RLottieDrawable.this.genCacheSend) {
                                        RLottieDrawable.this.genCacheSend = true;
                                        RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnableGenerateCache);
                                    }
                                    frame = -1;
                                }
                                if (frame == -1) {
                                    RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnableNoFrame);
                                    CountDownLatch countDownLatch = RLottieDrawable.this.frameWaitSync;
                                    if (countDownLatch == null) {
                                        return;
                                    }
                                    countDownLatch.countDown();
                                    return;
                                }
                                RLottieDrawable rLottieDrawable7 = RLottieDrawable.this;
                                rLottieDrawable7.nextRenderingBitmap = rLottieDrawable7.backgroundBitmap;
                                RLottieDrawable rLottieDrawable8 = RLottieDrawable.this;
                                int i7 = rLottieDrawable8.isDice;
                                if (i7 == 1) {
                                    int i8 = rLottieDrawable8.currentFrame;
                                    int i9 = i8 + i5;
                                    int i10 = rLottieDrawable8.diceSwitchFramesCount;
                                    if (i10 == -1) {
                                        i10 = rLottieDrawable8.metaData[0];
                                    }
                                    if (i9 < i10) {
                                        rLottieDrawable8.currentFrame = i8 + i5;
                                    } else {
                                        rLottieDrawable8.currentFrame = 0;
                                        rLottieDrawable8.nextFrameIsLast = false;
                                        if (RLottieDrawable.this.secondNativePtr != 0) {
                                            RLottieDrawable.this.isDice = 2;
                                        }
                                        if (RLottieDrawable.this.resetVibrationAfterRestart) {
                                            RLottieDrawable.this.vibrationPattern = null;
                                            RLottieDrawable.this.resetVibrationAfterRestart = false;
                                        }
                                    }
                                } else if (i7 == 2) {
                                    int i11 = rLottieDrawable8.currentFrame;
                                    if (i11 + i5 < rLottieDrawable8.secondFramesCount) {
                                        rLottieDrawable8.currentFrame = i11 + i5;
                                    } else {
                                        rLottieDrawable8.nextFrameIsLast = true;
                                        RLottieDrawable.this.autoRepeatPlayCount++;
                                    }
                                } else {
                                    int i12 = rLottieDrawable8.customEndFrame;
                                    if (i12 >= 0 && rLottieDrawable8.playInDirectionOfCustomEndFrame) {
                                        int i13 = rLottieDrawable8.currentFrame;
                                        if (i13 > i12) {
                                            if (i13 - i5 >= i12) {
                                                rLottieDrawable8.currentFrame = i13 - i5;
                                                rLottieDrawable8.nextFrameIsLast = false;
                                            } else {
                                                rLottieDrawable8.nextFrameIsLast = true;
                                                RLottieDrawable.this.checkDispatchOnAnimationEnd();
                                            }
                                        } else if (i13 + i5 < i12) {
                                            rLottieDrawable8.currentFrame = i13 + i5;
                                            rLottieDrawable8.nextFrameIsLast = false;
                                        } else {
                                            rLottieDrawable8.nextFrameIsLast = true;
                                            RLottieDrawable.this.checkDispatchOnAnimationEnd();
                                        }
                                    } else {
                                        int i14 = rLottieDrawable8.currentFrame;
                                        int i15 = i14 + i5;
                                        if (i12 < 0) {
                                            i12 = rLottieDrawable8.metaData[0];
                                        }
                                        if (i15 < i12) {
                                            if (rLottieDrawable8.autoRepeat == 3) {
                                                rLottieDrawable8.nextFrameIsLast = true;
                                                RLottieDrawable.this.autoRepeatPlayCount++;
                                            } else {
                                                rLottieDrawable8.currentFrame = i14 + i5;
                                                rLottieDrawable8.nextFrameIsLast = false;
                                            }
                                        } else {
                                            int i16 = rLottieDrawable8.autoRepeat;
                                            if (i16 == 1) {
                                                rLottieDrawable8.currentFrame = 0;
                                                rLottieDrawable8.nextFrameIsLast = false;
                                                if (RLottieDrawable.this.resetVibrationAfterRestart) {
                                                    RLottieDrawable.this.vibrationPattern = null;
                                                    RLottieDrawable.this.resetVibrationAfterRestart = false;
                                                }
                                                RLottieDrawable rLottieDrawable9 = RLottieDrawable.this;
                                                int i17 = rLottieDrawable9.autoRepeatCount;
                                                if (i17 > 0) {
                                                    rLottieDrawable9.autoRepeatCount = i17 - 1;
                                                }
                                            } else if (i16 == 2) {
                                                rLottieDrawable8.currentFrame = 0;
                                                rLottieDrawable8.nextFrameIsLast = true;
                                                RLottieDrawable rLottieDrawable10 = RLottieDrawable.this;
                                                rLottieDrawable10.autoRepeatPlayCount++;
                                                if (rLottieDrawable10.resetVibrationAfterRestart) {
                                                    RLottieDrawable.this.vibrationPattern = null;
                                                    RLottieDrawable.this.resetVibrationAfterRestart = false;
                                                }
                                            } else {
                                                rLottieDrawable8.nextFrameIsLast = true;
                                                RLottieDrawable.this.checkDispatchOnAnimationEnd();
                                            }
                                        }
                                    }
                                }
                            } catch (Exception e2) {
                                FileLog.e(e2);
                            }
                        }
                        RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnable);
                        CountDownLatch countDownLatch2 = RLottieDrawable.this.frameWaitSync;
                        if (countDownLatch2 == null) {
                            return;
                        }
                        countDownLatch2.countDown();
                        return;
                    }
                }
                CountDownLatch countDownLatch3 = RLottieDrawable.this.frameWaitSync;
                if (countDownLatch3 != null) {
                    countDownLatch3.countDown();
                }
                RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnableNoFrame);
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
            this.bitmapsCache = new BitmapsCache(file, this, cacheOptions, i, i2, !z);
            NativePtrArgs nativePtrArgs = new NativePtrArgs();
            this.args = nativePtrArgs;
            nativePtrArgs.file = file.getAbsoluteFile();
            NativePtrArgs nativePtrArgs2 = this.args;
            nativePtrArgs2.json = str;
            nativePtrArgs2.colorReplacement = iArr;
            nativePtrArgs2.fitzModifier = i3;
            c = 1;
            z2 = false;
            iArr2 = iArr3;
            this.nativePtr = create(file.getAbsolutePath(), str, i, i2, iArr3, this.precache, iArr, this.shouldLimitFps, i3);
            if (this.fallbackCache) {
                if (this.nativePtr == 0) {
                    file.delete();
                }
            } else {
                if (this.nativePtr != 0) {
                    destroy(this.nativePtr);
                }
                this.nativePtr = 0L;
            }
        } else {
            iArr2 = iArr3;
            c = 1;
            z2 = false;
            this.nativePtr = create(file.getAbsolutePath(), str, i, i2, iArr2, this.precache, iArr, this.shouldLimitFps, i3);
            if (this.nativePtr == 0) {
                file.delete();
            }
        }
        if (this.shouldLimitFps && iArr2[c] < 60) {
            this.shouldLimitFps = z2;
        }
        this.timeBetweenFrames = Math.max(this.shouldLimitFps ? 33 : 16, (int) (1000.0f / iArr2[c]));
    }

    public RLottieDrawable(int i, String str, int i2, int i3) {
        this(i, str, i2, i3, true, null);
    }

    public RLottieDrawable(String str, int i, int i2) {
        String readRes;
        int[] iArr = new int[3];
        this.metaData = iArr;
        this.customEndFrame = -1;
        this.newColorUpdates = new HashMap<>();
        this.pendingColorUpdates = new HashMap<>();
        this.resetVibrationAfterRestart = false;
        this.allowVibration = true;
        this.parentViews = new ArrayList<>();
        this.diceSwitchFramesCount = -1;
        this.autoRepeat = 1;
        this.autoRepeatCount = -1;
        this.scaleX = 1.0f;
        this.scaleY = 1.0f;
        this.dstRect = new RectF();
        this.uiRunnableNoFrame = new Runnable() { // from class: org.telegram.ui.Components.RLottieDrawable.1
            @Override // java.lang.Runnable
            public void run() {
                RLottieDrawable rLottieDrawable = RLottieDrawable.this;
                rLottieDrawable.loadFrameTask = null;
                rLottieDrawable.decodeFrameFinishedInternal();
                if (RLottieDrawable.this.onFrameReadyRunnable != null) {
                    RLottieDrawable.this.onFrameReadyRunnable.run();
                }
            }
        };
        this.uiRunnable = new Runnable() { // from class: org.telegram.ui.Components.RLottieDrawable.2
            @Override // java.lang.Runnable
            public void run() {
                RLottieDrawable.this.singleFrameDecoded = true;
                RLottieDrawable.this.invalidateInternal();
                RLottieDrawable.this.decodeFrameFinishedInternal();
                if (RLottieDrawable.this.onFrameReadyRunnable != null) {
                    RLottieDrawable.this.onFrameReadyRunnable.run();
                }
            }
        };
        this.uiRunnableGenerateCache = new AnonymousClass3();
        this.uiRunnableCacheFinished = new Runnable() { // from class: org.telegram.ui.Components.RLottieDrawable.4
            @Override // java.lang.Runnable
            public void run() {
                RLottieDrawable rLottieDrawable = RLottieDrawable.this;
                rLottieDrawable.cacheGenerateTask = null;
                rLottieDrawable.generatingCache = false;
                rLottieDrawable.decodeFrameFinishedInternal();
            }
        };
        this.loadFrameRunnable = new Runnable() { // from class: org.telegram.ui.Components.RLottieDrawable.5
            @Override // java.lang.Runnable
            public void run() {
                long j;
                int frame;
                BitmapsCache bitmapsCache;
                if (RLottieDrawable.this.isRecycled) {
                    return;
                }
                if (RLottieDrawable.this.canLoadFrames()) {
                    RLottieDrawable rLottieDrawable = RLottieDrawable.this;
                    if (rLottieDrawable.isDice != 2 || rLottieDrawable.secondNativePtr != 0) {
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
                            if (RLottieDrawable.this.pendingReplaceColors != null && RLottieDrawable.this.nativePtr != 0) {
                                RLottieDrawable.replaceColors(RLottieDrawable.this.nativePtr, RLottieDrawable.this.pendingReplaceColors);
                                RLottieDrawable.this.pendingReplaceColors = null;
                            }
                            try {
                                RLottieDrawable rLottieDrawable3 = RLottieDrawable.this;
                                int i4 = rLottieDrawable3.isDice;
                                if (i4 == 1) {
                                    j = rLottieDrawable3.nativePtr;
                                } else if (i4 == 2) {
                                    j = rLottieDrawable3.secondNativePtr;
                                    if (RLottieDrawable.this.setLastFrame) {
                                        RLottieDrawable rLottieDrawable4 = RLottieDrawable.this;
                                        rLottieDrawable4.currentFrame = rLottieDrawable4.secondFramesCount - 1;
                                    }
                                } else {
                                    j = rLottieDrawable3.nativePtr;
                                }
                                long j2 = j;
                                int i5 = RLottieDrawable.this.shouldLimitFps ? 2 : 1;
                                RLottieDrawable rLottieDrawable5 = RLottieDrawable.this;
                                if (rLottieDrawable5.precache && (bitmapsCache = rLottieDrawable5.bitmapsCache) != null) {
                                    try {
                                        frame = bitmapsCache.getFrame(rLottieDrawable5.currentFrame / i5, rLottieDrawable5.backgroundBitmap);
                                    } catch (Exception e) {
                                        FileLog.e(e);
                                        frame = 0;
                                    }
                                } else {
                                    int i6 = rLottieDrawable5.currentFrame;
                                    Bitmap bitmap = rLottieDrawable5.backgroundBitmap;
                                    RLottieDrawable rLottieDrawable6 = RLottieDrawable.this;
                                    frame = RLottieDrawable.getFrame(j2, i6, bitmap, rLottieDrawable6.width, rLottieDrawable6.height, rLottieDrawable6.backgroundBitmap.getRowBytes(), true);
                                }
                                BitmapsCache bitmapsCache2 = RLottieDrawable.this.bitmapsCache;
                                if (bitmapsCache2 != null && bitmapsCache2.needGenCache()) {
                                    if (!RLottieDrawable.this.genCacheSend) {
                                        RLottieDrawable.this.genCacheSend = true;
                                        RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnableGenerateCache);
                                    }
                                    frame = -1;
                                }
                                if (frame == -1) {
                                    RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnableNoFrame);
                                    CountDownLatch countDownLatch = RLottieDrawable.this.frameWaitSync;
                                    if (countDownLatch == null) {
                                        return;
                                    }
                                    countDownLatch.countDown();
                                    return;
                                }
                                RLottieDrawable rLottieDrawable7 = RLottieDrawable.this;
                                rLottieDrawable7.nextRenderingBitmap = rLottieDrawable7.backgroundBitmap;
                                RLottieDrawable rLottieDrawable8 = RLottieDrawable.this;
                                int i7 = rLottieDrawable8.isDice;
                                if (i7 == 1) {
                                    int i8 = rLottieDrawable8.currentFrame;
                                    int i9 = i8 + i5;
                                    int i10 = rLottieDrawable8.diceSwitchFramesCount;
                                    if (i10 == -1) {
                                        i10 = rLottieDrawable8.metaData[0];
                                    }
                                    if (i9 < i10) {
                                        rLottieDrawable8.currentFrame = i8 + i5;
                                    } else {
                                        rLottieDrawable8.currentFrame = 0;
                                        rLottieDrawable8.nextFrameIsLast = false;
                                        if (RLottieDrawable.this.secondNativePtr != 0) {
                                            RLottieDrawable.this.isDice = 2;
                                        }
                                        if (RLottieDrawable.this.resetVibrationAfterRestart) {
                                            RLottieDrawable.this.vibrationPattern = null;
                                            RLottieDrawable.this.resetVibrationAfterRestart = false;
                                        }
                                    }
                                } else if (i7 == 2) {
                                    int i11 = rLottieDrawable8.currentFrame;
                                    if (i11 + i5 < rLottieDrawable8.secondFramesCount) {
                                        rLottieDrawable8.currentFrame = i11 + i5;
                                    } else {
                                        rLottieDrawable8.nextFrameIsLast = true;
                                        RLottieDrawable.this.autoRepeatPlayCount++;
                                    }
                                } else {
                                    int i12 = rLottieDrawable8.customEndFrame;
                                    if (i12 >= 0 && rLottieDrawable8.playInDirectionOfCustomEndFrame) {
                                        int i13 = rLottieDrawable8.currentFrame;
                                        if (i13 > i12) {
                                            if (i13 - i5 >= i12) {
                                                rLottieDrawable8.currentFrame = i13 - i5;
                                                rLottieDrawable8.nextFrameIsLast = false;
                                            } else {
                                                rLottieDrawable8.nextFrameIsLast = true;
                                                RLottieDrawable.this.checkDispatchOnAnimationEnd();
                                            }
                                        } else if (i13 + i5 < i12) {
                                            rLottieDrawable8.currentFrame = i13 + i5;
                                            rLottieDrawable8.nextFrameIsLast = false;
                                        } else {
                                            rLottieDrawable8.nextFrameIsLast = true;
                                            RLottieDrawable.this.checkDispatchOnAnimationEnd();
                                        }
                                    } else {
                                        int i14 = rLottieDrawable8.currentFrame;
                                        int i15 = i14 + i5;
                                        if (i12 < 0) {
                                            i12 = rLottieDrawable8.metaData[0];
                                        }
                                        if (i15 < i12) {
                                            if (rLottieDrawable8.autoRepeat == 3) {
                                                rLottieDrawable8.nextFrameIsLast = true;
                                                RLottieDrawable.this.autoRepeatPlayCount++;
                                            } else {
                                                rLottieDrawable8.currentFrame = i14 + i5;
                                                rLottieDrawable8.nextFrameIsLast = false;
                                            }
                                        } else {
                                            int i16 = rLottieDrawable8.autoRepeat;
                                            if (i16 == 1) {
                                                rLottieDrawable8.currentFrame = 0;
                                                rLottieDrawable8.nextFrameIsLast = false;
                                                if (RLottieDrawable.this.resetVibrationAfterRestart) {
                                                    RLottieDrawable.this.vibrationPattern = null;
                                                    RLottieDrawable.this.resetVibrationAfterRestart = false;
                                                }
                                                RLottieDrawable rLottieDrawable9 = RLottieDrawable.this;
                                                int i17 = rLottieDrawable9.autoRepeatCount;
                                                if (i17 > 0) {
                                                    rLottieDrawable9.autoRepeatCount = i17 - 1;
                                                }
                                            } else if (i16 == 2) {
                                                rLottieDrawable8.currentFrame = 0;
                                                rLottieDrawable8.nextFrameIsLast = true;
                                                RLottieDrawable rLottieDrawable10 = RLottieDrawable.this;
                                                rLottieDrawable10.autoRepeatPlayCount++;
                                                if (rLottieDrawable10.resetVibrationAfterRestart) {
                                                    RLottieDrawable.this.vibrationPattern = null;
                                                    RLottieDrawable.this.resetVibrationAfterRestart = false;
                                                }
                                            } else {
                                                rLottieDrawable8.nextFrameIsLast = true;
                                                RLottieDrawable.this.checkDispatchOnAnimationEnd();
                                            }
                                        }
                                    }
                                }
                            } catch (Exception e2) {
                                FileLog.e(e2);
                            }
                        }
                        RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnable);
                        CountDownLatch countDownLatch2 = RLottieDrawable.this.frameWaitSync;
                        if (countDownLatch2 == null) {
                            return;
                        }
                        countDownLatch2.countDown();
                        return;
                    }
                }
                CountDownLatch countDownLatch3 = RLottieDrawable.this.frameWaitSync;
                if (countDownLatch3 != null) {
                    countDownLatch3.countDown();
                }
                RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnableNoFrame);
            }
        };
        this.width = i;
        this.height = i2;
        this.isDice = 1;
        if ("🎲".equals(str)) {
            readRes = readRes(null, R.raw.diceloop);
            this.diceSwitchFramesCount = 60;
        } else {
            readRes = "🎯".equals(str) ? readRes(null, R.raw.dartloop) : null;
        }
        getPaint().setFlags(2);
        if (TextUtils.isEmpty(readRes)) {
            this.timeBetweenFrames = 16;
            return;
        }
        this.nativePtr = createWithJson(readRes, "dice", iArr, null);
        this.timeBetweenFrames = Math.max(16, (int) (1000.0f / iArr[1]));
    }

    /* JADX INFO: Access modifiers changed from: private */
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

    public boolean setBaseDice(File file) {
        if (this.nativePtr == 0 && !this.loadingInBackground) {
            final String readRes = readRes(file, 0);
            if (TextUtils.isEmpty(readRes)) {
                return false;
            }
            this.loadingInBackground = true;
            Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.ui.Components.RLottieDrawable$$ExternalSyntheticLambda6
                @Override // java.lang.Runnable
                public final void run() {
                    RLottieDrawable.this.lambda$setBaseDice$1(readRes);
                }
            });
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setBaseDice$1(String str) {
        this.nativePtr = createWithJson(str, "dice", this.metaData, null);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.RLottieDrawable$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                RLottieDrawable.this.lambda$setBaseDice$0();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setBaseDice$0() {
        this.loadingInBackground = false;
        if (!this.secondLoadingInBackground && this.destroyAfterLoading) {
            recycle();
            return;
        }
        this.timeBetweenFrames = Math.max(16, (int) (1000.0f / this.metaData[1]));
        scheduleNextGetFrame();
        invalidateInternal();
    }

    public boolean hasBaseDice() {
        return this.nativePtr != 0 || this.loadingInBackground;
    }

    public boolean setDiceNumber(File file, boolean z) {
        if (this.secondNativePtr == 0 && !this.secondLoadingInBackground) {
            final String readRes = readRes(file, 0);
            if (TextUtils.isEmpty(readRes)) {
                return false;
            }
            if (z && this.nextRenderingBitmap == null && this.renderingBitmap == null && this.loadFrameTask == null) {
                this.isDice = 2;
                this.setLastFrame = true;
            }
            this.secondLoadingInBackground = true;
            Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.ui.Components.RLottieDrawable$$ExternalSyntheticLambda7
                @Override // java.lang.Runnable
                public final void run() {
                    RLottieDrawable.this.lambda$setDiceNumber$4(readRes);
                }
            });
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setDiceNumber$4(String str) {
        if (this.destroyAfterLoading) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.RLottieDrawable$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    RLottieDrawable.this.lambda$setDiceNumber$2();
                }
            });
            return;
        }
        final int[] iArr = new int[3];
        this.secondNativePtr = createWithJson(str, "dice", iArr, null);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.RLottieDrawable$$ExternalSyntheticLambda8
            @Override // java.lang.Runnable
            public final void run() {
                RLottieDrawable.this.lambda$setDiceNumber$3(iArr);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setDiceNumber$2() {
        this.secondLoadingInBackground = false;
        if (this.loadingInBackground || !this.destroyAfterLoading) {
            return;
        }
        recycle();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setDiceNumber$3(int[] iArr) {
        this.secondLoadingInBackground = false;
        if (this.destroyAfterLoading) {
            recycle();
            return;
        }
        this.secondFramesCount = iArr[0];
        this.timeBetweenFrames = Math.max(16, (int) (1000.0f / iArr[1]));
        scheduleNextGetFrame();
        invalidateInternal();
    }

    public RLottieDrawable(int i, String str, int i2, int i3, boolean z, int[] iArr) {
        int[] iArr2 = new int[3];
        this.metaData = iArr2;
        this.customEndFrame = -1;
        this.newColorUpdates = new HashMap<>();
        this.pendingColorUpdates = new HashMap<>();
        this.resetVibrationAfterRestart = false;
        this.allowVibration = true;
        this.parentViews = new ArrayList<>();
        this.diceSwitchFramesCount = -1;
        this.autoRepeat = 1;
        this.autoRepeatCount = -1;
        this.scaleX = 1.0f;
        this.scaleY = 1.0f;
        this.dstRect = new RectF();
        this.uiRunnableNoFrame = new Runnable() { // from class: org.telegram.ui.Components.RLottieDrawable.1
            @Override // java.lang.Runnable
            public void run() {
                RLottieDrawable rLottieDrawable = RLottieDrawable.this;
                rLottieDrawable.loadFrameTask = null;
                rLottieDrawable.decodeFrameFinishedInternal();
                if (RLottieDrawable.this.onFrameReadyRunnable != null) {
                    RLottieDrawable.this.onFrameReadyRunnable.run();
                }
            }
        };
        this.uiRunnable = new Runnable() { // from class: org.telegram.ui.Components.RLottieDrawable.2
            @Override // java.lang.Runnable
            public void run() {
                RLottieDrawable.this.singleFrameDecoded = true;
                RLottieDrawable.this.invalidateInternal();
                RLottieDrawable.this.decodeFrameFinishedInternal();
                if (RLottieDrawable.this.onFrameReadyRunnable != null) {
                    RLottieDrawable.this.onFrameReadyRunnable.run();
                }
            }
        };
        this.uiRunnableGenerateCache = new AnonymousClass3();
        this.uiRunnableCacheFinished = new Runnable() { // from class: org.telegram.ui.Components.RLottieDrawable.4
            @Override // java.lang.Runnable
            public void run() {
                RLottieDrawable rLottieDrawable = RLottieDrawable.this;
                rLottieDrawable.cacheGenerateTask = null;
                rLottieDrawable.generatingCache = false;
                rLottieDrawable.decodeFrameFinishedInternal();
            }
        };
        this.loadFrameRunnable = new Runnable() { // from class: org.telegram.ui.Components.RLottieDrawable.5
            @Override // java.lang.Runnable
            public void run() {
                long j;
                int frame;
                BitmapsCache bitmapsCache;
                if (RLottieDrawable.this.isRecycled) {
                    return;
                }
                if (RLottieDrawable.this.canLoadFrames()) {
                    RLottieDrawable rLottieDrawable = RLottieDrawable.this;
                    if (rLottieDrawable.isDice != 2 || rLottieDrawable.secondNativePtr != 0) {
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
                            if (RLottieDrawable.this.pendingReplaceColors != null && RLottieDrawable.this.nativePtr != 0) {
                                RLottieDrawable.replaceColors(RLottieDrawable.this.nativePtr, RLottieDrawable.this.pendingReplaceColors);
                                RLottieDrawable.this.pendingReplaceColors = null;
                            }
                            try {
                                RLottieDrawable rLottieDrawable3 = RLottieDrawable.this;
                                int i4 = rLottieDrawable3.isDice;
                                if (i4 == 1) {
                                    j = rLottieDrawable3.nativePtr;
                                } else if (i4 == 2) {
                                    j = rLottieDrawable3.secondNativePtr;
                                    if (RLottieDrawable.this.setLastFrame) {
                                        RLottieDrawable rLottieDrawable4 = RLottieDrawable.this;
                                        rLottieDrawable4.currentFrame = rLottieDrawable4.secondFramesCount - 1;
                                    }
                                } else {
                                    j = rLottieDrawable3.nativePtr;
                                }
                                long j2 = j;
                                int i5 = RLottieDrawable.this.shouldLimitFps ? 2 : 1;
                                RLottieDrawable rLottieDrawable5 = RLottieDrawable.this;
                                if (rLottieDrawable5.precache && (bitmapsCache = rLottieDrawable5.bitmapsCache) != null) {
                                    try {
                                        frame = bitmapsCache.getFrame(rLottieDrawable5.currentFrame / i5, rLottieDrawable5.backgroundBitmap);
                                    } catch (Exception e) {
                                        FileLog.e(e);
                                        frame = 0;
                                    }
                                } else {
                                    int i6 = rLottieDrawable5.currentFrame;
                                    Bitmap bitmap = rLottieDrawable5.backgroundBitmap;
                                    RLottieDrawable rLottieDrawable6 = RLottieDrawable.this;
                                    frame = RLottieDrawable.getFrame(j2, i6, bitmap, rLottieDrawable6.width, rLottieDrawable6.height, rLottieDrawable6.backgroundBitmap.getRowBytes(), true);
                                }
                                BitmapsCache bitmapsCache2 = RLottieDrawable.this.bitmapsCache;
                                if (bitmapsCache2 != null && bitmapsCache2.needGenCache()) {
                                    if (!RLottieDrawable.this.genCacheSend) {
                                        RLottieDrawable.this.genCacheSend = true;
                                        RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnableGenerateCache);
                                    }
                                    frame = -1;
                                }
                                if (frame == -1) {
                                    RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnableNoFrame);
                                    CountDownLatch countDownLatch = RLottieDrawable.this.frameWaitSync;
                                    if (countDownLatch == null) {
                                        return;
                                    }
                                    countDownLatch.countDown();
                                    return;
                                }
                                RLottieDrawable rLottieDrawable7 = RLottieDrawable.this;
                                rLottieDrawable7.nextRenderingBitmap = rLottieDrawable7.backgroundBitmap;
                                RLottieDrawable rLottieDrawable8 = RLottieDrawable.this;
                                int i7 = rLottieDrawable8.isDice;
                                if (i7 == 1) {
                                    int i8 = rLottieDrawable8.currentFrame;
                                    int i9 = i8 + i5;
                                    int i10 = rLottieDrawable8.diceSwitchFramesCount;
                                    if (i10 == -1) {
                                        i10 = rLottieDrawable8.metaData[0];
                                    }
                                    if (i9 < i10) {
                                        rLottieDrawable8.currentFrame = i8 + i5;
                                    } else {
                                        rLottieDrawable8.currentFrame = 0;
                                        rLottieDrawable8.nextFrameIsLast = false;
                                        if (RLottieDrawable.this.secondNativePtr != 0) {
                                            RLottieDrawable.this.isDice = 2;
                                        }
                                        if (RLottieDrawable.this.resetVibrationAfterRestart) {
                                            RLottieDrawable.this.vibrationPattern = null;
                                            RLottieDrawable.this.resetVibrationAfterRestart = false;
                                        }
                                    }
                                } else if (i7 == 2) {
                                    int i11 = rLottieDrawable8.currentFrame;
                                    if (i11 + i5 < rLottieDrawable8.secondFramesCount) {
                                        rLottieDrawable8.currentFrame = i11 + i5;
                                    } else {
                                        rLottieDrawable8.nextFrameIsLast = true;
                                        RLottieDrawable.this.autoRepeatPlayCount++;
                                    }
                                } else {
                                    int i12 = rLottieDrawable8.customEndFrame;
                                    if (i12 >= 0 && rLottieDrawable8.playInDirectionOfCustomEndFrame) {
                                        int i13 = rLottieDrawable8.currentFrame;
                                        if (i13 > i12) {
                                            if (i13 - i5 >= i12) {
                                                rLottieDrawable8.currentFrame = i13 - i5;
                                                rLottieDrawable8.nextFrameIsLast = false;
                                            } else {
                                                rLottieDrawable8.nextFrameIsLast = true;
                                                RLottieDrawable.this.checkDispatchOnAnimationEnd();
                                            }
                                        } else if (i13 + i5 < i12) {
                                            rLottieDrawable8.currentFrame = i13 + i5;
                                            rLottieDrawable8.nextFrameIsLast = false;
                                        } else {
                                            rLottieDrawable8.nextFrameIsLast = true;
                                            RLottieDrawable.this.checkDispatchOnAnimationEnd();
                                        }
                                    } else {
                                        int i14 = rLottieDrawable8.currentFrame;
                                        int i15 = i14 + i5;
                                        if (i12 < 0) {
                                            i12 = rLottieDrawable8.metaData[0];
                                        }
                                        if (i15 < i12) {
                                            if (rLottieDrawable8.autoRepeat == 3) {
                                                rLottieDrawable8.nextFrameIsLast = true;
                                                RLottieDrawable.this.autoRepeatPlayCount++;
                                            } else {
                                                rLottieDrawable8.currentFrame = i14 + i5;
                                                rLottieDrawable8.nextFrameIsLast = false;
                                            }
                                        } else {
                                            int i16 = rLottieDrawable8.autoRepeat;
                                            if (i16 == 1) {
                                                rLottieDrawable8.currentFrame = 0;
                                                rLottieDrawable8.nextFrameIsLast = false;
                                                if (RLottieDrawable.this.resetVibrationAfterRestart) {
                                                    RLottieDrawable.this.vibrationPattern = null;
                                                    RLottieDrawable.this.resetVibrationAfterRestart = false;
                                                }
                                                RLottieDrawable rLottieDrawable9 = RLottieDrawable.this;
                                                int i17 = rLottieDrawable9.autoRepeatCount;
                                                if (i17 > 0) {
                                                    rLottieDrawable9.autoRepeatCount = i17 - 1;
                                                }
                                            } else if (i16 == 2) {
                                                rLottieDrawable8.currentFrame = 0;
                                                rLottieDrawable8.nextFrameIsLast = true;
                                                RLottieDrawable rLottieDrawable10 = RLottieDrawable.this;
                                                rLottieDrawable10.autoRepeatPlayCount++;
                                                if (rLottieDrawable10.resetVibrationAfterRestart) {
                                                    RLottieDrawable.this.vibrationPattern = null;
                                                    RLottieDrawable.this.resetVibrationAfterRestart = false;
                                                }
                                            } else {
                                                rLottieDrawable8.nextFrameIsLast = true;
                                                RLottieDrawable.this.checkDispatchOnAnimationEnd();
                                            }
                                        }
                                    }
                                }
                            } catch (Exception e2) {
                                FileLog.e(e2);
                            }
                        }
                        RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnable);
                        CountDownLatch countDownLatch2 = RLottieDrawable.this.frameWaitSync;
                        if (countDownLatch2 == null) {
                            return;
                        }
                        countDownLatch2.countDown();
                        return;
                    }
                }
                CountDownLatch countDownLatch3 = RLottieDrawable.this.frameWaitSync;
                if (countDownLatch3 != null) {
                    countDownLatch3.countDown();
                }
                RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnableNoFrame);
            }
        };
        this.width = i2;
        this.height = i3;
        this.autoRepeat = 0;
        String readRes = readRes(null, i);
        if (TextUtils.isEmpty(readRes)) {
            return;
        }
        getPaint().setFlags(2);
        this.nativePtr = createWithJson(readRes, str, iArr2, iArr);
        this.timeBetweenFrames = Math.max(16, (int) (1000.0f / iArr2[1]));
        if (!z) {
            return;
        }
        setAllowDecodeSingleFrame(true);
    }

    public static String readRes(File file, int i) {
        InputStream inputStream;
        byte[] bArr = readBufferLocal.get();
        if (bArr == null) {
            bArr = new byte[65536];
            readBufferLocal.set(bArr);
        }
        try {
            if (file != null) {
                inputStream = new FileInputStream(file);
            } else {
                inputStream = ApplicationLoader.applicationContext.getResources().openRawResource(i);
            }
        } catch (Throwable unused) {
            inputStream = null;
        }
        try {
            byte[] bArr2 = bufferLocal.get();
            if (bArr2 == null) {
                bArr2 = new byte[4096];
                bufferLocal.set(bArr2);
            }
            int i2 = 0;
            while (true) {
                int read = inputStream.read(bArr2, 0, bArr2.length);
                if (read >= 0) {
                    int i3 = i2 + read;
                    if (bArr.length < i3) {
                        byte[] bArr3 = new byte[bArr.length * 2];
                        System.arraycopy(bArr, 0, bArr3, 0, i2);
                        readBufferLocal.set(bArr3);
                        bArr = bArr3;
                    }
                    if (read > 0) {
                        System.arraycopy(bArr2, 0, bArr, i2, read);
                        i2 = i3;
                    }
                } else {
                    try {
                        break;
                    } catch (Throwable unused2) {
                    }
                }
            }
            inputStream.close();
            return new String(bArr, 0, i2);
        } catch (Throwable unused3) {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Throwable unused4) {
                }
            }
            return null;
        }
    }

    public int getCurrentFrame() {
        return this.currentFrame;
    }

    public int getCustomEndFrame() {
        return this.customEndFrame;
    }

    public long getDuration() {
        int[] iArr = this.metaData;
        return (iArr[0] / iArr[1]) * 1000.0f;
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
        if (imageReceiver == null) {
            return;
        }
        this.parentViews.add(imageReceiver);
        checkCacheCancel();
    }

    public void removeParentView(ImageReceiver imageReceiver) {
        if (imageReceiver == null) {
            return;
        }
        this.parentViews.remove(imageReceiver);
        checkCacheCancel();
    }

    public void checkCacheCancel() {
        Runnable runnable;
        View view;
        if (this.bitmapsCache == null || lottieCacheGenerateQueue == null || this.cacheGenerateTask == null) {
            return;
        }
        boolean z = true;
        boolean z2 = this.parentViews.isEmpty() && getCallback() == null;
        if (Build.VERSION.SDK_INT < 19 ? !z2 || this.masterParent != null : !z2 || ((view = this.masterParent) != null && view.isAttachedToWindow())) {
            z = false;
        }
        if (z && this.cancelCache == null) {
            Runnable runnable2 = new Runnable() { // from class: org.telegram.ui.Components.RLottieDrawable$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    RLottieDrawable.this.lambda$checkCacheCancel$5();
                }
            };
            this.cancelCache = runnable2;
            AndroidUtilities.runOnUIThread(runnable2, 600L);
        } else if (z || (runnable = this.cancelCache) == null) {
        } else {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.cancelCache = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$checkCacheCancel$5() {
        lottieCacheGenerateQueue.cancelRunnable(this.cacheGenerateTask);
        BitmapsCache bitmapsCache = this.bitmapsCache;
        if (bitmapsCache != null) {
            bitmapsCache.cancelCreate();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean hasParentView() {
        return (this.parentViews.isEmpty() && this.masterParent == null && getCallback() == null) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
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
        } else if (this.loadFrameTask == null && this.cacheGenerateTask == null && !this.generatingCache) {
            if (this.nativePtr != 0) {
                destroy(this.nativePtr);
                this.nativePtr = 0L;
            }
            if (this.secondNativePtr != 0) {
                destroy(this.secondNativePtr);
                this.secondNativePtr = 0L;
            }
            BitmapsCache bitmapsCache = this.bitmapsCache;
            if (bitmapsCache != null) {
                bitmapsCache.recycle();
                this.bitmapsCache = null;
            }
            recycleResources();
        } else {
            this.destroyWhenDone = true;
        }
    }

    public void setAutoRepeat(int i) {
        if (this.autoRepeat == 2 && i == 3 && this.currentFrame != 0) {
            return;
        }
        this.autoRepeat = i;
    }

    public void setAutoRepeatCount(int i) {
        this.autoRepeatCount = i;
    }

    protected void finalize() throws Throwable {
        try {
            recycle();
        } finally {
            super.finalize();
        }
    }

    @Override // android.graphics.drawable.Animatable
    public void start() {
        if (!this.isRunning) {
            if ((this.autoRepeat >= 2 && this.autoRepeatPlayCount != 0) || this.customEndFrame == this.currentFrame) {
                return;
            }
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
        if ((this.autoRepeat < 2 || this.autoRepeatPlayCount == 0) && this.autoRepeatCount < 0) {
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

    public boolean hasVibrationPattern() {
        return this.vibrationPattern != null;
    }

    public void beginApplyLayerColors() {
        this.applyingLayerColors = true;
    }

    public void commitApplyLayerColors() {
        if (!this.applyingLayerColors) {
            return;
        }
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

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean scheduleNextGetFrame() {
        return scheduleNextGetFrame(false);
    }

    protected boolean scheduleNextGetFrame(boolean z) {
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
        if (z && this.shouldLimitFps) {
            DispatchQueuePoolBackground.execute(runnable);
            return true;
        }
        loadFrameRunnableQueue.execute(runnable);
        return true;
    }

    public boolean isHeavyDrawable() {
        return this.isDice == 0;
    }

    @Override // android.graphics.drawable.Animatable
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
        if (i < 0 || i > this.metaData[0]) {
            return;
        }
        if (this.currentFrame == i && !z2) {
            return;
        }
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
                FileLog.e(e);
            }
            this.frameWaitSync = null;
        }
        invalidateSelf();
    }

    public void setProgressMs(long j) {
        setCurrentFrame((int) ((Math.max(0L, j) / this.timeBetweenFrames) % this.metaData[0]), true, true);
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
        setCurrentFrame((int) (this.metaData[0] * f), z);
    }

    public void setCurrentParentView(View view) {
        this.currentParentView = view;
    }

    @Override // android.graphics.drawable.Animatable
    public boolean isRunning() {
        return this.isRunning;
    }

    @Override // android.graphics.drawable.BitmapDrawable, android.graphics.drawable.Drawable
    public int getIntrinsicHeight() {
        return this.height;
    }

    @Override // android.graphics.drawable.BitmapDrawable, android.graphics.drawable.Drawable
    public int getIntrinsicWidth() {
        return this.width;
    }

    @Override // android.graphics.drawable.BitmapDrawable, android.graphics.drawable.Drawable
    protected void onBoundsChange(android.graphics.Rect rect) {
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
            Runnable runnable2 = weakReference2.get();
            if (runnable2 != null) {
                runnable2.run();
            }
            this.onFinishCallback = null;
        }
        if (this.nextFrameIsLast || (this.autoRepeatCount == 0 && this.autoRepeat == 1)) {
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
            this.lastFrameTime = j - Math.min(16L, j2 - j3);
        }
        if (z && this.forceFrameRedraw) {
            this.singleFrameDecoded = false;
            this.forceFrameRedraw = false;
        }
        if (this.isDice == 0 && (weakReference = this.onFinishCallback) != null && this.currentFrame >= this.finishFrame && (runnable = weakReference.get()) != null) {
            runnable.run();
        }
        scheduleNextGetFrame(true);
    }

    @Override // android.graphics.drawable.BitmapDrawable, android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        drawInternal(canvas, false, 0L);
    }

    public void drawInBackground(Canvas canvas, float f, float f2, float f3, float f4, int i, ColorFilter colorFilter) {
        if (this.dstRectBackground == null) {
            this.dstRectBackground = new RectF();
            Paint paint = new Paint(1);
            this.backgroundPaint = paint;
            paint.setFilterBitmap(true);
        }
        this.backgroundPaint.setAlpha(i);
        this.backgroundPaint.setColorFilter(colorFilter);
        this.dstRectBackground.set(f, f2, f3 + f, f4 + f2);
        drawInternal(canvas, true, 0L);
    }

    public void drawInternal(Canvas canvas, boolean z, long j) {
        boolean z2;
        float f;
        float f2;
        if (!canLoadFrames() || this.destroyWhenDone) {
            return;
        }
        boolean z3 = false;
        if (!z) {
            updateCurrentFrame(j, false);
        }
        RectF rectF = z ? this.dstRectBackground : this.dstRect;
        Paint paint = z ? this.backgroundPaint : getPaint();
        if (paint.getAlpha() == 0 || this.isInvalid || this.renderingBitmap == null) {
            return;
        }
        if (!z) {
            rectF.set(getBounds());
            if (this.applyTransformation) {
                this.scaleX = rectF.width() / this.width;
                this.scaleY = rectF.height() / this.height;
                this.applyTransformation = false;
                if (Math.abs(rectF.width() - this.width) >= AndroidUtilities.dp(1.0f) || Math.abs(rectF.height() - this.height) >= AndroidUtilities.dp(1.0f)) {
                    z3 = true;
                }
                this.needScale = z3;
            }
            f = this.scaleX;
            f2 = this.scaleY;
            z2 = this.needScale;
        } else {
            float width = rectF.width() / this.width;
            float height = rectF.height() / this.height;
            if (Math.abs(rectF.width() - this.width) >= AndroidUtilities.dp(1.0f) || Math.abs(rectF.height() - this.height) >= AndroidUtilities.dp(1.0f)) {
                z3 = true;
            }
            z2 = z3;
            f = width;
            f2 = height;
        }
        if (!z2) {
            canvas.drawBitmap(this.renderingBitmap, rectF.left, rectF.top, paint);
        } else {
            canvas.save();
            canvas.translate(rectF.left, rectF.top);
            canvas.scale(f, f2);
            canvas.drawBitmap(this.renderingBitmap, 0.0f, 0.0f, paint);
            canvas.restore();
        }
        if (!this.isRunning || z) {
            return;
        }
        invalidateInternal();
    }

    public void updateCurrentFrame(long j, boolean z) {
        int i;
        Integer num;
        if (j == 0) {
            j = System.currentTimeMillis();
        }
        long j2 = j;
        long j3 = j2 - this.lastFrameTime;
        if (z && !this.shouldLimitFps) {
            i = this.timeBetweenFrames - 16;
        } else {
            float f = AndroidUtilities.screenRefreshRate;
            if (f <= 60.0f || (z && f <= 80.0f)) {
                i = this.timeBetweenFrames - 6;
            } else {
                i = this.timeBetweenFrames;
            }
        }
        if (this.isRunning) {
            if (this.renderingBitmap == null && this.nextRenderingBitmap == null) {
                scheduleNextGetFrame(true);
            } else if (this.nextRenderingBitmap == null) {
            } else {
                if (this.renderingBitmap != null && (j3 < i || this.skipFrameUpdate)) {
                    return;
                }
                HashMap<Integer, Integer> hashMap = this.vibrationPattern;
                if (hashMap != null && this.currentParentView != null && this.allowVibration && (num = hashMap.get(Integer.valueOf(this.currentFrame - 1))) != null) {
                    this.currentParentView.performHapticFeedback(num.intValue() == 1 ? 0 : 3, 2);
                }
                setCurrentFrame(j2, j3, i, false);
            }
        } else if ((!this.forceFrameRedraw && (!this.decodeSingleFrame || j3 < i)) || this.nextRenderingBitmap == null) {
        } else {
            setCurrentFrame(j2, j3, i, true);
        }
    }

    public void setAllowVibration(boolean z) {
        this.allowVibration = z;
    }

    public void resetVibrationAfterRestart(boolean z) {
        this.resetVibrationAfterRestart = z;
    }

    @Override // android.graphics.drawable.Drawable
    public int getMinimumHeight() {
        return this.height;
    }

    @Override // android.graphics.drawable.Drawable
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
        if (this.nextRenderingBitmap == null) {
            return null;
        }
        return this.nextRenderingBitmap;
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

    @Override // org.telegram.messenger.utils.BitmapsCache.Cacheable
    public void prepareForGenerateCache() {
        File file;
        String file2 = this.args.file.toString();
        NativePtrArgs nativePtrArgs = this.args;
        long create = create(file2, nativePtrArgs.json, this.width, this.height, new int[3], false, nativePtrArgs.colorReplacement, false, nativePtrArgs.fitzModifier);
        this.generateCacheNativePtr = create;
        if (create != 0 || (file = this.file) == null) {
            return;
        }
        file.delete();
    }

    @Override // org.telegram.messenger.utils.BitmapsCache.Cacheable
    public int getNextFrame(Bitmap bitmap) {
        long j = this.generateCacheNativePtr;
        if (j == 0) {
            return -1;
        }
        int i = this.shouldLimitFps ? 2 : 1;
        if (getFrame(j, this.generateCacheFramePointer, bitmap, this.width, this.height, bitmap.getRowBytes(), true) == -5) {
            try {
                Thread.sleep(100L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return getNextFrame(bitmap);
        }
        int i2 = this.generateCacheFramePointer + i;
        this.generateCacheFramePointer = i2;
        return i2 > this.metaData[0] ? 0 : 1;
    }

    @Override // org.telegram.messenger.utils.BitmapsCache.Cacheable
    public void releaseForGenerateCache() {
        long j = this.generateCacheNativePtr;
        if (j != 0) {
            destroy(j);
            this.generateCacheNativePtr = 0L;
        }
    }

    @Override // org.telegram.messenger.utils.BitmapsCache.Cacheable
    public Bitmap getFirstFrame(Bitmap bitmap) {
        String file = this.args.file.toString();
        NativePtrArgs nativePtrArgs = this.args;
        long create = create(file, nativePtrArgs.json, this.width, this.height, new int[3], false, nativePtrArgs.colorReplacement, false, nativePtrArgs.fitzModifier);
        if (create == 0) {
            return bitmap;
        }
        getFrame(create, 0, bitmap, this.width, this.height, bitmap.getRowBytes(), true);
        destroy(create);
        return bitmap;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setMasterParent(View view) {
        this.masterParent = view;
    }

    public boolean canLoadFrames() {
        return this.precache ? this.bitmapsCache != null || this.fallbackCache : this.nativePtr != 0;
    }

    /* loaded from: classes3.dex */
    private class NativePtrArgs {
        public int[] colorReplacement;
        File file;
        public int fitzModifier;
        String json;

        private NativePtrArgs() {
        }
    }

    public void checkCache(final Runnable runnable) {
        if (this.bitmapsCache == null) {
            AndroidUtilities.runOnUIThread(runnable);
        } else {
            loadFrameRunnableQueue.execute(new Runnable() { // from class: org.telegram.ui.Components.RLottieDrawable$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    RLottieDrawable.this.lambda$checkCache$8(runnable);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$checkCache$8(final Runnable runnable) {
        if (this.bitmapsCache.cacheExist()) {
            AndroidUtilities.runOnUIThread(runnable);
        } else {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.RLottieDrawable$$ExternalSyntheticLambda5
                @Override // java.lang.Runnable
                public final void run() {
                    RLottieDrawable.this.lambda$checkCache$7(runnable);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$checkCache$7(final Runnable runnable) {
        this.generatingCache = true;
        if (lottieCacheGenerateQueue == null) {
            createCacheGenQueue();
        }
        DispatchQueue dispatchQueue = lottieCacheGenerateQueue;
        Runnable runnable2 = new Runnable() { // from class: org.telegram.ui.Components.RLottieDrawable$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                RLottieDrawable.this.lambda$checkCache$6(runnable);
            }
        };
        this.cacheGenerateTask = runnable2;
        dispatchQueue.postRunnable(runnable2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$checkCache$6(Runnable runnable) {
        BitmapsCache bitmapsCache = this.bitmapsCache;
        if (bitmapsCache != null) {
            bitmapsCache.createCache();
        }
        AndroidUtilities.runOnUIThread(runnable);
    }
}
