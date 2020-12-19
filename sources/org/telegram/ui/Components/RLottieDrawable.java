package org.telegram.ui.Components;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DispatchQueuePool;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.Utilities;
import org.telegram.ui.Components.RLottieDrawable;

public class RLottieDrawable extends BitmapDrawable implements Animatable {
    private static ThreadLocal<byte[]> bufferLocal = new ThreadLocal<>();
    private static DispatchQueuePool loadFrameRunnableQueue = new DispatchQueuePool(4);
    /* access modifiers changed from: private */
    public static ThreadPoolExecutor lottieCacheGenerateQueue;
    private static ThreadLocal<byte[]> readBufferLocal = new ThreadLocal<>();
    protected static final Handler uiHandler = new Handler(Looper.getMainLooper());
    private boolean applyTransformation;
    private boolean applyingLayerColors;
    protected int autoRepeat;
    protected int autoRepeatPlayCount;
    protected volatile Bitmap backgroundBitmap;
    protected Runnable cacheGenerateTask;
    protected int currentFrame;
    private View currentParentView;
    protected int customEndFrame;
    private boolean decodeSingleFrame;
    protected boolean destroyAfterLoading;
    protected boolean destroyWhenDone;
    protected int diceSwitchFramesCount;
    private boolean doNotRemoveInvalidOnFrameReady;
    private final Rect dstRect;
    private int finishFrame;
    private boolean forceFrameRedraw;
    protected CountDownLatch frameWaitSync;
    protected int height;
    private boolean invalidateOnProgressSet;
    protected int isDice;
    private boolean isInvalid;
    protected volatile boolean isRecycled;
    protected volatile boolean isRunning;
    private long lastFrameTime;
    protected Runnable loadFrameRunnable;
    protected Runnable loadFrameTask;
    protected boolean loadingInBackground;
    protected final int[] metaData;
    protected volatile long nativePtr;
    private HashMap<String, Integer> newColorUpdates;
    private int[] newReplaceColors;
    protected volatile boolean nextFrameIsLast;
    protected volatile Bitmap nextRenderingBitmap;
    protected WeakReference<Runnable> onFinishCallback;
    private ArrayList<WeakReference<View>> parentViews;
    /* access modifiers changed from: private */
    public volatile HashMap<String, Integer> pendingColorUpdates;
    /* access modifiers changed from: private */
    public int[] pendingReplaceColors;
    protected boolean playInDirectionOfCustomEndFrame;
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
    protected int timeBetweenFrames;
    protected Runnable uiRunnable;
    /* access modifiers changed from: private */
    public Runnable uiRunnableCacheFinished;
    /* access modifiers changed from: private */
    public Runnable uiRunnableGenerateCache;
    protected Runnable uiRunnableNoFrame;
    private HashMap<Integer, Integer> vibrationPattern;
    protected boolean waitingForNextTask;
    protected int width;

    public static native long create(String str, int i, int i2, int[] iArr, boolean z, int[] iArr2, boolean z2);

    /* access modifiers changed from: private */
    public static native void createCache(long j, int i, int i2);

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

    /* access modifiers changed from: protected */
    public void checkRunningTasks() {
        Runnable runnable = this.cacheGenerateTask;
        if (runnable != null && lottieCacheGenerateQueue.remove(runnable)) {
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
        if (this.nativePtr == 0 && this.secondNativePtr == 0) {
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
        if (this.renderingBitmap != null) {
            this.renderingBitmap.recycle();
            this.renderingBitmap = null;
        }
        if (this.backgroundBitmap != null) {
            this.backgroundBitmap.recycle();
            this.backgroundBitmap = null;
        }
    }

    public void setOnFinishCallback(Runnable runnable, int i) {
        if (runnable != null) {
            this.onFinishCallback = new WeakReference<>(runnable);
            this.finishFrame = i;
        } else if (this.onFinishCallback != null) {
            this.onFinishCallback = null;
        }
    }

    public RLottieDrawable(File file, int i, int i2, boolean z, boolean z2, int[] iArr) {
        int[] iArr2 = new int[3];
        this.metaData = iArr2;
        this.customEndFrame = -1;
        this.newColorUpdates = new HashMap<>();
        this.pendingColorUpdates = new HashMap<>();
        this.diceSwitchFramesCount = -1;
        this.autoRepeat = 1;
        this.scaleX = 1.0f;
        this.scaleY = 1.0f;
        this.dstRect = new Rect();
        this.parentViews = new ArrayList<>();
        this.uiRunnableNoFrame = new Runnable() {
            public void run() {
                RLottieDrawable rLottieDrawable = RLottieDrawable.this;
                rLottieDrawable.loadFrameTask = null;
                rLottieDrawable.decodeFrameFinishedInternal();
            }
        };
        this.uiRunnableCacheFinished = new Runnable() {
            public void run() {
                RLottieDrawable rLottieDrawable = RLottieDrawable.this;
                rLottieDrawable.cacheGenerateTask = null;
                rLottieDrawable.decodeFrameFinishedInternal();
            }
        };
        this.uiRunnable = new Runnable() {
            public void run() {
                boolean unused = RLottieDrawable.this.singleFrameDecoded = true;
                RLottieDrawable.this.invalidateInternal();
                RLottieDrawable.this.decodeFrameFinishedInternal();
            }
        };
        this.uiRunnableGenerateCache = new Runnable() {
            public void run() {
                if (!RLottieDrawable.this.isRecycled) {
                    RLottieDrawable rLottieDrawable = RLottieDrawable.this;
                    if (!rLottieDrawable.destroyWhenDone && rLottieDrawable.nativePtr != 0) {
                        ThreadPoolExecutor access$100 = RLottieDrawable.lottieCacheGenerateQueue;
                        RLottieDrawable rLottieDrawable2 = RLottieDrawable.this;
                        $$Lambda$RLottieDrawable$5$Rp_svL8xqUdQVx69qbU3oM4ZVsc r2 = new Runnable() {
                            public final void run() {
                                RLottieDrawable.AnonymousClass5.this.lambda$run$0$RLottieDrawable$5();
                            }
                        };
                        rLottieDrawable2.cacheGenerateTask = r2;
                        access$100.execute(r2);
                    }
                }
                RLottieDrawable.this.decodeFrameFinishedInternal();
            }

            /* access modifiers changed from: private */
            /* renamed from: lambda$run$0 */
            public /* synthetic */ void lambda$run$0$RLottieDrawable$5() {
                RLottieDrawable rLottieDrawable = RLottieDrawable.this;
                if (rLottieDrawable.cacheGenerateTask != null) {
                    long j = rLottieDrawable.nativePtr;
                    RLottieDrawable rLottieDrawable2 = RLottieDrawable.this;
                    RLottieDrawable.createCache(j, rLottieDrawable2.width, rLottieDrawable2.height);
                    RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnableCacheFinished);
                }
            }
        };
        this.loadFrameRunnable = new Runnable() {
            public void run() {
                long j;
                if (!RLottieDrawable.this.isRecycled) {
                    if (RLottieDrawable.this.nativePtr != 0) {
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
                                if (RLottieDrawable.this.pendingReplaceColors != null) {
                                    RLottieDrawable.replaceColors(RLottieDrawable.this.nativePtr, RLottieDrawable.this.pendingReplaceColors);
                                    int[] unused2 = RLottieDrawable.this.pendingReplaceColors = null;
                                }
                                try {
                                    RLottieDrawable rLottieDrawable3 = RLottieDrawable.this;
                                    int i = rLottieDrawable3.isDice;
                                    if (i == 1) {
                                        j = rLottieDrawable3.nativePtr;
                                    } else if (i == 2) {
                                        j = rLottieDrawable3.secondNativePtr;
                                        if (RLottieDrawable.this.setLastFrame) {
                                            RLottieDrawable rLottieDrawable4 = RLottieDrawable.this;
                                            rLottieDrawable4.currentFrame = rLottieDrawable4.secondFramesCount - 1;
                                        }
                                    } else {
                                        j = rLottieDrawable3.nativePtr;
                                    }
                                    long j2 = j;
                                    RLottieDrawable rLottieDrawable5 = RLottieDrawable.this;
                                    int i2 = rLottieDrawable5.currentFrame;
                                    Bitmap bitmap = rLottieDrawable5.backgroundBitmap;
                                    RLottieDrawable rLottieDrawable6 = RLottieDrawable.this;
                                    if (RLottieDrawable.getFrame(j2, i2, bitmap, rLottieDrawable6.width, rLottieDrawable6.height, rLottieDrawable6.backgroundBitmap.getRowBytes(), true) == -1) {
                                        RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnableNoFrame);
                                        CountDownLatch countDownLatch = RLottieDrawable.this.frameWaitSync;
                                        if (countDownLatch != null) {
                                            countDownLatch.countDown();
                                            return;
                                        }
                                        return;
                                    }
                                    RLottieDrawable rLottieDrawable7 = RLottieDrawable.this;
                                    if (rLottieDrawable7.metaData[2] != 0) {
                                        RLottieDrawable.uiHandler.post(rLottieDrawable7.uiRunnableGenerateCache);
                                        RLottieDrawable.this.metaData[2] = 0;
                                    }
                                    RLottieDrawable rLottieDrawable8 = RLottieDrawable.this;
                                    rLottieDrawable8.nextRenderingBitmap = rLottieDrawable8.backgroundBitmap;
                                    int i3 = RLottieDrawable.this.shouldLimitFps ? 2 : 1;
                                    RLottieDrawable rLottieDrawable9 = RLottieDrawable.this;
                                    int i4 = rLottieDrawable9.isDice;
                                    if (i4 == 1) {
                                        int i5 = rLottieDrawable9.currentFrame;
                                        int i6 = i5 + i3;
                                        int i7 = rLottieDrawable9.diceSwitchFramesCount;
                                        if (i7 == -1) {
                                            i7 = rLottieDrawable9.metaData[0];
                                        }
                                        if (i6 < i7) {
                                            rLottieDrawable9.currentFrame = i5 + i3;
                                        } else {
                                            rLottieDrawable9.currentFrame = 0;
                                            rLottieDrawable9.nextFrameIsLast = false;
                                            if (RLottieDrawable.this.secondNativePtr != 0) {
                                                RLottieDrawable.this.isDice = 2;
                                            }
                                        }
                                    } else if (i4 == 2) {
                                        int i8 = rLottieDrawable9.currentFrame;
                                        if (i8 + i3 < rLottieDrawable9.secondFramesCount) {
                                            rLottieDrawable9.currentFrame = i8 + i3;
                                        } else {
                                            rLottieDrawable9.nextFrameIsLast = true;
                                            RLottieDrawable.this.autoRepeatPlayCount++;
                                        }
                                    } else {
                                        int i9 = rLottieDrawable9.customEndFrame;
                                        if (i9 < 0 || !rLottieDrawable9.playInDirectionOfCustomEndFrame) {
                                            int i10 = rLottieDrawable9.currentFrame;
                                            int i11 = i10 + i3;
                                            if (i9 < 0) {
                                                i9 = rLottieDrawable9.metaData[0];
                                            }
                                            if (i11 >= i9) {
                                                int i12 = rLottieDrawable9.autoRepeat;
                                                if (i12 == 1) {
                                                    rLottieDrawable9.currentFrame = 0;
                                                    rLottieDrawable9.nextFrameIsLast = false;
                                                } else if (i12 == 2) {
                                                    rLottieDrawable9.currentFrame = 0;
                                                    rLottieDrawable9.nextFrameIsLast = true;
                                                    RLottieDrawable.this.autoRepeatPlayCount++;
                                                } else {
                                                    rLottieDrawable9.nextFrameIsLast = true;
                                                }
                                            } else if (rLottieDrawable9.autoRepeat == 3) {
                                                rLottieDrawable9.nextFrameIsLast = true;
                                                RLottieDrawable.this.autoRepeatPlayCount++;
                                            } else {
                                                rLottieDrawable9.currentFrame = i10 + i3;
                                                rLottieDrawable9.nextFrameIsLast = false;
                                            }
                                        } else {
                                            int i13 = rLottieDrawable9.currentFrame;
                                            if (i13 > i9) {
                                                if (i13 - i3 >= i9) {
                                                    rLottieDrawable9.currentFrame = i13 - i3;
                                                    rLottieDrawable9.nextFrameIsLast = false;
                                                } else {
                                                    rLottieDrawable9.nextFrameIsLast = true;
                                                }
                                            } else if (i13 + i3 < i9) {
                                                rLottieDrawable9.currentFrame = i13 + i3;
                                                rLottieDrawable9.nextFrameIsLast = false;
                                            } else {
                                                rLottieDrawable9.nextFrameIsLast = true;
                                            }
                                        }
                                    }
                                } catch (Exception e) {
                                    FileLog.e((Throwable) e);
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
        this.shouldLimitFps = z2;
        getPaint().setFlags(2);
        this.nativePtr = create(file.getAbsolutePath(), i, i2, iArr2, z, iArr, this.shouldLimitFps);
        if (z && lottieCacheGenerateQueue == null) {
            lottieCacheGenerateQueue = new ThreadPoolExecutor(1, 1, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue());
        }
        if (this.nativePtr == 0) {
            file.delete();
        }
        if (this.shouldLimitFps && iArr2[1] < 60) {
            this.shouldLimitFps = false;
        }
        this.timeBetweenFrames = Math.max(this.shouldLimitFps ? 33 : 16, (int) (1000.0f / ((float) iArr2[1])));
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
        this.diceSwitchFramesCount = -1;
        this.autoRepeat = 1;
        this.scaleX = 1.0f;
        this.scaleY = 1.0f;
        this.dstRect = new Rect();
        this.parentViews = new ArrayList<>();
        this.uiRunnableNoFrame = new Runnable() {
            public void run() {
                RLottieDrawable rLottieDrawable = RLottieDrawable.this;
                rLottieDrawable.loadFrameTask = null;
                rLottieDrawable.decodeFrameFinishedInternal();
            }
        };
        this.uiRunnableCacheFinished = new Runnable() {
            public void run() {
                RLottieDrawable rLottieDrawable = RLottieDrawable.this;
                rLottieDrawable.cacheGenerateTask = null;
                rLottieDrawable.decodeFrameFinishedInternal();
            }
        };
        this.uiRunnable = new Runnable() {
            public void run() {
                boolean unused = RLottieDrawable.this.singleFrameDecoded = true;
                RLottieDrawable.this.invalidateInternal();
                RLottieDrawable.this.decodeFrameFinishedInternal();
            }
        };
        this.uiRunnableGenerateCache = new Runnable() {
            public void run() {
                if (!RLottieDrawable.this.isRecycled) {
                    RLottieDrawable rLottieDrawable = RLottieDrawable.this;
                    if (!rLottieDrawable.destroyWhenDone && rLottieDrawable.nativePtr != 0) {
                        ThreadPoolExecutor access$100 = RLottieDrawable.lottieCacheGenerateQueue;
                        RLottieDrawable rLottieDrawable2 = RLottieDrawable.this;
                        $$Lambda$RLottieDrawable$5$Rp_svL8xqUdQVx69qbU3oM4ZVsc r2 = new Runnable() {
                            public final void run() {
                                RLottieDrawable.AnonymousClass5.this.lambda$run$0$RLottieDrawable$5();
                            }
                        };
                        rLottieDrawable2.cacheGenerateTask = r2;
                        access$100.execute(r2);
                    }
                }
                RLottieDrawable.this.decodeFrameFinishedInternal();
            }

            /* access modifiers changed from: private */
            /* renamed from: lambda$run$0 */
            public /* synthetic */ void lambda$run$0$RLottieDrawable$5() {
                RLottieDrawable rLottieDrawable = RLottieDrawable.this;
                if (rLottieDrawable.cacheGenerateTask != null) {
                    long j = rLottieDrawable.nativePtr;
                    RLottieDrawable rLottieDrawable2 = RLottieDrawable.this;
                    RLottieDrawable.createCache(j, rLottieDrawable2.width, rLottieDrawable2.height);
                    RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnableCacheFinished);
                }
            }
        };
        this.loadFrameRunnable = new Runnable() {
            public void run() {
                long j;
                if (!RLottieDrawable.this.isRecycled) {
                    if (RLottieDrawable.this.nativePtr != 0) {
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
                                if (RLottieDrawable.this.pendingReplaceColors != null) {
                                    RLottieDrawable.replaceColors(RLottieDrawable.this.nativePtr, RLottieDrawable.this.pendingReplaceColors);
                                    int[] unused2 = RLottieDrawable.this.pendingReplaceColors = null;
                                }
                                try {
                                    RLottieDrawable rLottieDrawable3 = RLottieDrawable.this;
                                    int i = rLottieDrawable3.isDice;
                                    if (i == 1) {
                                        j = rLottieDrawable3.nativePtr;
                                    } else if (i == 2) {
                                        j = rLottieDrawable3.secondNativePtr;
                                        if (RLottieDrawable.this.setLastFrame) {
                                            RLottieDrawable rLottieDrawable4 = RLottieDrawable.this;
                                            rLottieDrawable4.currentFrame = rLottieDrawable4.secondFramesCount - 1;
                                        }
                                    } else {
                                        j = rLottieDrawable3.nativePtr;
                                    }
                                    long j2 = j;
                                    RLottieDrawable rLottieDrawable5 = RLottieDrawable.this;
                                    int i2 = rLottieDrawable5.currentFrame;
                                    Bitmap bitmap = rLottieDrawable5.backgroundBitmap;
                                    RLottieDrawable rLottieDrawable6 = RLottieDrawable.this;
                                    if (RLottieDrawable.getFrame(j2, i2, bitmap, rLottieDrawable6.width, rLottieDrawable6.height, rLottieDrawable6.backgroundBitmap.getRowBytes(), true) == -1) {
                                        RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnableNoFrame);
                                        CountDownLatch countDownLatch = RLottieDrawable.this.frameWaitSync;
                                        if (countDownLatch != null) {
                                            countDownLatch.countDown();
                                            return;
                                        }
                                        return;
                                    }
                                    RLottieDrawable rLottieDrawable7 = RLottieDrawable.this;
                                    if (rLottieDrawable7.metaData[2] != 0) {
                                        RLottieDrawable.uiHandler.post(rLottieDrawable7.uiRunnableGenerateCache);
                                        RLottieDrawable.this.metaData[2] = 0;
                                    }
                                    RLottieDrawable rLottieDrawable8 = RLottieDrawable.this;
                                    rLottieDrawable8.nextRenderingBitmap = rLottieDrawable8.backgroundBitmap;
                                    int i3 = RLottieDrawable.this.shouldLimitFps ? 2 : 1;
                                    RLottieDrawable rLottieDrawable9 = RLottieDrawable.this;
                                    int i4 = rLottieDrawable9.isDice;
                                    if (i4 == 1) {
                                        int i5 = rLottieDrawable9.currentFrame;
                                        int i6 = i5 + i3;
                                        int i7 = rLottieDrawable9.diceSwitchFramesCount;
                                        if (i7 == -1) {
                                            i7 = rLottieDrawable9.metaData[0];
                                        }
                                        if (i6 < i7) {
                                            rLottieDrawable9.currentFrame = i5 + i3;
                                        } else {
                                            rLottieDrawable9.currentFrame = 0;
                                            rLottieDrawable9.nextFrameIsLast = false;
                                            if (RLottieDrawable.this.secondNativePtr != 0) {
                                                RLottieDrawable.this.isDice = 2;
                                            }
                                        }
                                    } else if (i4 == 2) {
                                        int i8 = rLottieDrawable9.currentFrame;
                                        if (i8 + i3 < rLottieDrawable9.secondFramesCount) {
                                            rLottieDrawable9.currentFrame = i8 + i3;
                                        } else {
                                            rLottieDrawable9.nextFrameIsLast = true;
                                            RLottieDrawable.this.autoRepeatPlayCount++;
                                        }
                                    } else {
                                        int i9 = rLottieDrawable9.customEndFrame;
                                        if (i9 < 0 || !rLottieDrawable9.playInDirectionOfCustomEndFrame) {
                                            int i10 = rLottieDrawable9.currentFrame;
                                            int i11 = i10 + i3;
                                            if (i9 < 0) {
                                                i9 = rLottieDrawable9.metaData[0];
                                            }
                                            if (i11 >= i9) {
                                                int i12 = rLottieDrawable9.autoRepeat;
                                                if (i12 == 1) {
                                                    rLottieDrawable9.currentFrame = 0;
                                                    rLottieDrawable9.nextFrameIsLast = false;
                                                } else if (i12 == 2) {
                                                    rLottieDrawable9.currentFrame = 0;
                                                    rLottieDrawable9.nextFrameIsLast = true;
                                                    RLottieDrawable.this.autoRepeatPlayCount++;
                                                } else {
                                                    rLottieDrawable9.nextFrameIsLast = true;
                                                }
                                            } else if (rLottieDrawable9.autoRepeat == 3) {
                                                rLottieDrawable9.nextFrameIsLast = true;
                                                RLottieDrawable.this.autoRepeatPlayCount++;
                                            } else {
                                                rLottieDrawable9.currentFrame = i10 + i3;
                                                rLottieDrawable9.nextFrameIsLast = false;
                                            }
                                        } else {
                                            int i13 = rLottieDrawable9.currentFrame;
                                            if (i13 > i9) {
                                                if (i13 - i3 >= i9) {
                                                    rLottieDrawable9.currentFrame = i13 - i3;
                                                    rLottieDrawable9.nextFrameIsLast = false;
                                                } else {
                                                    rLottieDrawable9.nextFrameIsLast = true;
                                                }
                                            } else if (i13 + i3 < i9) {
                                                rLottieDrawable9.currentFrame = i13 + i3;
                                                rLottieDrawable9.nextFrameIsLast = false;
                                            } else {
                                                rLottieDrawable9.nextFrameIsLast = true;
                                            }
                                        }
                                    }
                                } catch (Exception e) {
                                    FileLog.e((Throwable) e);
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

    public boolean setBaseDice(File file) {
        if (this.nativePtr == 0 && !this.loadingInBackground) {
            String readRes = readRes(file, 0);
            if (TextUtils.isEmpty(readRes)) {
                return false;
            }
            this.loadingInBackground = true;
            Utilities.globalQueue.postRunnable(new Runnable(readRes) {
                public final /* synthetic */ String f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    RLottieDrawable.this.lambda$setBaseDice$1$RLottieDrawable(this.f$1);
                }
            });
        }
        return true;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$setBaseDice$1 */
    public /* synthetic */ void lambda$setBaseDice$1$RLottieDrawable(String str) {
        this.nativePtr = createWithJson(str, "dice", this.metaData, (int[]) null);
        AndroidUtilities.runOnUIThread(new Runnable() {
            public final void run() {
                RLottieDrawable.this.lambda$null$0$RLottieDrawable();
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$0 */
    public /* synthetic */ void lambda$null$0$RLottieDrawable() {
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

    public boolean setDiceNumber(File file, boolean z) {
        if (this.secondNativePtr == 0 && !this.secondLoadingInBackground) {
            String readRes = readRes(file, 0);
            if (TextUtils.isEmpty(readRes)) {
                return false;
            }
            if (z && this.nextRenderingBitmap == null && this.renderingBitmap == null && this.loadFrameTask == null) {
                this.isDice = 2;
                this.setLastFrame = true;
            }
            this.secondLoadingInBackground = true;
            Utilities.globalQueue.postRunnable(new Runnable(readRes) {
                public final /* synthetic */ String f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    RLottieDrawable.this.lambda$setDiceNumber$4$RLottieDrawable(this.f$1);
                }
            });
        }
        return true;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$setDiceNumber$4 */
    public /* synthetic */ void lambda$setDiceNumber$4$RLottieDrawable(String str) {
        if (this.destroyAfterLoading) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    RLottieDrawable.this.lambda$null$2$RLottieDrawable();
                }
            });
            return;
        }
        int[] iArr = new int[3];
        this.secondNativePtr = createWithJson(str, "dice", iArr, (int[]) null);
        AndroidUtilities.runOnUIThread(new Runnable(iArr) {
            public final /* synthetic */ int[] f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                RLottieDrawable.this.lambda$null$3$RLottieDrawable(this.f$1);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$2 */
    public /* synthetic */ void lambda$null$2$RLottieDrawable() {
        this.secondLoadingInBackground = false;
        if (!this.loadingInBackground && this.destroyAfterLoading) {
            recycle();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$3 */
    public /* synthetic */ void lambda$null$3$RLottieDrawable(int[] iArr) {
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
        this.diceSwitchFramesCount = -1;
        this.autoRepeat = 1;
        this.scaleX = 1.0f;
        this.scaleY = 1.0f;
        this.dstRect = new Rect();
        this.parentViews = new ArrayList<>();
        this.uiRunnableNoFrame = new Runnable() {
            public void run() {
                RLottieDrawable rLottieDrawable = RLottieDrawable.this;
                rLottieDrawable.loadFrameTask = null;
                rLottieDrawable.decodeFrameFinishedInternal();
            }
        };
        this.uiRunnableCacheFinished = new Runnable() {
            public void run() {
                RLottieDrawable rLottieDrawable = RLottieDrawable.this;
                rLottieDrawable.cacheGenerateTask = null;
                rLottieDrawable.decodeFrameFinishedInternal();
            }
        };
        this.uiRunnable = new Runnable() {
            public void run() {
                boolean unused = RLottieDrawable.this.singleFrameDecoded = true;
                RLottieDrawable.this.invalidateInternal();
                RLottieDrawable.this.decodeFrameFinishedInternal();
            }
        };
        this.uiRunnableGenerateCache = new Runnable() {
            public void run() {
                if (!RLottieDrawable.this.isRecycled) {
                    RLottieDrawable rLottieDrawable = RLottieDrawable.this;
                    if (!rLottieDrawable.destroyWhenDone && rLottieDrawable.nativePtr != 0) {
                        ThreadPoolExecutor access$100 = RLottieDrawable.lottieCacheGenerateQueue;
                        RLottieDrawable rLottieDrawable2 = RLottieDrawable.this;
                        $$Lambda$RLottieDrawable$5$Rp_svL8xqUdQVx69qbU3oM4ZVsc r2 = new Runnable() {
                            public final void run() {
                                RLottieDrawable.AnonymousClass5.this.lambda$run$0$RLottieDrawable$5();
                            }
                        };
                        rLottieDrawable2.cacheGenerateTask = r2;
                        access$100.execute(r2);
                    }
                }
                RLottieDrawable.this.decodeFrameFinishedInternal();
            }

            /* access modifiers changed from: private */
            /* renamed from: lambda$run$0 */
            public /* synthetic */ void lambda$run$0$RLottieDrawable$5() {
                RLottieDrawable rLottieDrawable = RLottieDrawable.this;
                if (rLottieDrawable.cacheGenerateTask != null) {
                    long j = rLottieDrawable.nativePtr;
                    RLottieDrawable rLottieDrawable2 = RLottieDrawable.this;
                    RLottieDrawable.createCache(j, rLottieDrawable2.width, rLottieDrawable2.height);
                    RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnableCacheFinished);
                }
            }
        };
        this.loadFrameRunnable = new Runnable() {
            public void run() {
                long j;
                if (!RLottieDrawable.this.isRecycled) {
                    if (RLottieDrawable.this.nativePtr != 0) {
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
                                if (RLottieDrawable.this.pendingReplaceColors != null) {
                                    RLottieDrawable.replaceColors(RLottieDrawable.this.nativePtr, RLottieDrawable.this.pendingReplaceColors);
                                    int[] unused2 = RLottieDrawable.this.pendingReplaceColors = null;
                                }
                                try {
                                    RLottieDrawable rLottieDrawable3 = RLottieDrawable.this;
                                    int i = rLottieDrawable3.isDice;
                                    if (i == 1) {
                                        j = rLottieDrawable3.nativePtr;
                                    } else if (i == 2) {
                                        j = rLottieDrawable3.secondNativePtr;
                                        if (RLottieDrawable.this.setLastFrame) {
                                            RLottieDrawable rLottieDrawable4 = RLottieDrawable.this;
                                            rLottieDrawable4.currentFrame = rLottieDrawable4.secondFramesCount - 1;
                                        }
                                    } else {
                                        j = rLottieDrawable3.nativePtr;
                                    }
                                    long j2 = j;
                                    RLottieDrawable rLottieDrawable5 = RLottieDrawable.this;
                                    int i2 = rLottieDrawable5.currentFrame;
                                    Bitmap bitmap = rLottieDrawable5.backgroundBitmap;
                                    RLottieDrawable rLottieDrawable6 = RLottieDrawable.this;
                                    if (RLottieDrawable.getFrame(j2, i2, bitmap, rLottieDrawable6.width, rLottieDrawable6.height, rLottieDrawable6.backgroundBitmap.getRowBytes(), true) == -1) {
                                        RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnableNoFrame);
                                        CountDownLatch countDownLatch = RLottieDrawable.this.frameWaitSync;
                                        if (countDownLatch != null) {
                                            countDownLatch.countDown();
                                            return;
                                        }
                                        return;
                                    }
                                    RLottieDrawable rLottieDrawable7 = RLottieDrawable.this;
                                    if (rLottieDrawable7.metaData[2] != 0) {
                                        RLottieDrawable.uiHandler.post(rLottieDrawable7.uiRunnableGenerateCache);
                                        RLottieDrawable.this.metaData[2] = 0;
                                    }
                                    RLottieDrawable rLottieDrawable8 = RLottieDrawable.this;
                                    rLottieDrawable8.nextRenderingBitmap = rLottieDrawable8.backgroundBitmap;
                                    int i3 = RLottieDrawable.this.shouldLimitFps ? 2 : 1;
                                    RLottieDrawable rLottieDrawable9 = RLottieDrawable.this;
                                    int i4 = rLottieDrawable9.isDice;
                                    if (i4 == 1) {
                                        int i5 = rLottieDrawable9.currentFrame;
                                        int i6 = i5 + i3;
                                        int i7 = rLottieDrawable9.diceSwitchFramesCount;
                                        if (i7 == -1) {
                                            i7 = rLottieDrawable9.metaData[0];
                                        }
                                        if (i6 < i7) {
                                            rLottieDrawable9.currentFrame = i5 + i3;
                                        } else {
                                            rLottieDrawable9.currentFrame = 0;
                                            rLottieDrawable9.nextFrameIsLast = false;
                                            if (RLottieDrawable.this.secondNativePtr != 0) {
                                                RLottieDrawable.this.isDice = 2;
                                            }
                                        }
                                    } else if (i4 == 2) {
                                        int i8 = rLottieDrawable9.currentFrame;
                                        if (i8 + i3 < rLottieDrawable9.secondFramesCount) {
                                            rLottieDrawable9.currentFrame = i8 + i3;
                                        } else {
                                            rLottieDrawable9.nextFrameIsLast = true;
                                            RLottieDrawable.this.autoRepeatPlayCount++;
                                        }
                                    } else {
                                        int i9 = rLottieDrawable9.customEndFrame;
                                        if (i9 < 0 || !rLottieDrawable9.playInDirectionOfCustomEndFrame) {
                                            int i10 = rLottieDrawable9.currentFrame;
                                            int i11 = i10 + i3;
                                            if (i9 < 0) {
                                                i9 = rLottieDrawable9.metaData[0];
                                            }
                                            if (i11 >= i9) {
                                                int i12 = rLottieDrawable9.autoRepeat;
                                                if (i12 == 1) {
                                                    rLottieDrawable9.currentFrame = 0;
                                                    rLottieDrawable9.nextFrameIsLast = false;
                                                } else if (i12 == 2) {
                                                    rLottieDrawable9.currentFrame = 0;
                                                    rLottieDrawable9.nextFrameIsLast = true;
                                                    RLottieDrawable.this.autoRepeatPlayCount++;
                                                } else {
                                                    rLottieDrawable9.nextFrameIsLast = true;
                                                }
                                            } else if (rLottieDrawable9.autoRepeat == 3) {
                                                rLottieDrawable9.nextFrameIsLast = true;
                                                RLottieDrawable.this.autoRepeatPlayCount++;
                                            } else {
                                                rLottieDrawable9.currentFrame = i10 + i3;
                                                rLottieDrawable9.nextFrameIsLast = false;
                                            }
                                        } else {
                                            int i13 = rLottieDrawable9.currentFrame;
                                            if (i13 > i9) {
                                                if (i13 - i3 >= i9) {
                                                    rLottieDrawable9.currentFrame = i13 - i3;
                                                    rLottieDrawable9.nextFrameIsLast = false;
                                                } else {
                                                    rLottieDrawable9.nextFrameIsLast = true;
                                                }
                                            } else if (i13 + i3 < i9) {
                                                rLottieDrawable9.currentFrame = i13 + i3;
                                                rLottieDrawable9.nextFrameIsLast = false;
                                            } else {
                                                rLottieDrawable9.nextFrameIsLast = true;
                                            }
                                        }
                                    }
                                } catch (Exception e) {
                                    FileLog.e((Throwable) e);
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

    /* JADX WARNING: Removed duplicated region for block: B:31:0x006a A[SYNTHETIC, Splitter:B:31:0x006a] */
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
            java.io.FileInputStream r8 = new java.io.FileInputStream     // Catch:{ all -> 0x0067 }
            r8.<init>(r7)     // Catch:{ all -> 0x0067 }
            goto L_0x0026
        L_0x001c:
            android.content.Context r7 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x0067 }
            android.content.res.Resources r7 = r7.getResources()     // Catch:{ all -> 0x0067 }
            java.io.InputStream r8 = r7.openRawResource(r8)     // Catch:{ all -> 0x0067 }
        L_0x0026:
            java.lang.ThreadLocal<byte[]> r7 = bufferLocal     // Catch:{ all -> 0x0068 }
            java.lang.Object r7 = r7.get()     // Catch:{ all -> 0x0068 }
            byte[] r7 = (byte[]) r7     // Catch:{ all -> 0x0068 }
            r2 = 0
            if (r7 != 0) goto L_0x003a
            r7 = 4096(0x1000, float:5.74E-42)
            byte[] r7 = new byte[r7]     // Catch:{ all -> 0x0068 }
            java.lang.ThreadLocal<byte[]> r3 = bufferLocal     // Catch:{ all -> 0x0068 }
            r3.set(r7)     // Catch:{ all -> 0x0068 }
        L_0x003a:
            r3 = 0
        L_0x003b:
            int r4 = r7.length     // Catch:{ all -> 0x0068 }
            int r4 = r8.read(r7, r2, r4)     // Catch:{ all -> 0x0068 }
            if (r4 < 0) goto L_0x005c
            int r5 = r0.length     // Catch:{ all -> 0x0068 }
            int r6 = r3 + r4
            if (r5 >= r6) goto L_0x0055
            int r5 = r0.length     // Catch:{ all -> 0x0068 }
            int r5 = r5 * 2
            byte[] r5 = new byte[r5]     // Catch:{ all -> 0x0068 }
            java.lang.System.arraycopy(r0, r2, r5, r2, r3)     // Catch:{ all -> 0x0068 }
            java.lang.ThreadLocal<byte[]> r0 = readBufferLocal     // Catch:{ all -> 0x0068 }
            r0.set(r5)     // Catch:{ all -> 0x0068 }
            r0 = r5
        L_0x0055:
            if (r4 <= 0) goto L_0x003b
            java.lang.System.arraycopy(r7, r2, r0, r3, r4)     // Catch:{ all -> 0x0068 }
            r3 = r6
            goto L_0x003b
        L_0x005c:
            if (r8 == 0) goto L_0x0061
            r8.close()     // Catch:{ all -> 0x0061 }
        L_0x0061:
            java.lang.String r7 = new java.lang.String
            r7.<init>(r0, r2, r3)
            return r7
        L_0x0067:
            r8 = r1
        L_0x0068:
            if (r8 == 0) goto L_0x006d
            r8.close()     // Catch:{ all -> 0x006d }
        L_0x006d:
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
        int i2 = this.customEndFrame;
        if (i2 == i || i2 > this.metaData[0]) {
            return false;
        }
        this.customEndFrame = i;
        return true;
    }

    public void addParentView(View view) {
        if (view != null) {
            int size = this.parentViews.size();
            int i = 0;
            while (i < size) {
                if (this.parentViews.get(i).get() != view) {
                    if (this.parentViews.get(i).get() == null) {
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
                View view2 = (View) this.parentViews.get(i).get();
                if (view2 == view || view2 == null) {
                    this.parentViews.remove(i);
                    size--;
                    i--;
                }
                i++;
            }
        }
    }

    /* access modifiers changed from: protected */
    public boolean hasParentView() {
        if (getCallback() != null) {
            return true;
        }
        for (int size = this.parentViews.size(); size > 0; size--) {
            if (((View) this.parentViews.get(0).get()) != null) {
                return true;
            }
            this.parentViews.remove(0);
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public void invalidateInternal() {
        int size = this.parentViews.size();
        int i = 0;
        while (i < size) {
            View view = (View) this.parentViews.get(i).get();
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
        if (this.autoRepeat < 2 || this.autoRepeatPlayCount == 0) {
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
        if (this.loadFrameTask != null || this.nextRenderingBitmap != null || this.nativePtr == 0 || this.loadingInBackground || this.destroyWhenDone) {
            return false;
        }
        if (!this.isRunning) {
            boolean z = this.decodeSingleFrame;
            if (!z) {
                return false;
            }
            if (z && this.singleFrameDecoded) {
                return false;
            }
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
        DispatchQueuePool dispatchQueuePool = loadFrameRunnableQueue;
        Runnable runnable = this.loadFrameRunnable;
        this.loadFrameTask = runnable;
        dispatchQueuePool.execute(runnable);
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
            if (!scheduleNextGetFrame()) {
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

    private boolean isCurrentParentViewMaster() {
        if (getCallback() != null) {
            return true;
        }
        int size = this.parentViews.size();
        int i = 0;
        while (i < size) {
            View view = (View) this.parentViews.get(i).get();
            if (view == null) {
                this.parentViews.remove(i);
                size--;
                i--;
            } else if (view.isShown()) {
                if (view == this.currentParentView) {
                    return true;
                }
                return false;
            }
            i++;
        }
        return true;
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
        scheduleNextGetFrame();
    }

    public void draw(Canvas canvas) {
        if (this.nativePtr != 0 && !this.destroyWhenDone) {
            updateCurrentFrame();
            if (!this.isInvalid && this.renderingBitmap != null) {
                if (this.applyTransformation) {
                    this.dstRect.set(getBounds());
                    this.scaleX = ((float) this.dstRect.width()) / ((float) this.width);
                    this.scaleY = ((float) this.dstRect.height()) / ((float) this.height);
                    this.applyTransformation = false;
                }
                canvas.save();
                Rect rect = this.dstRect;
                canvas.translate((float) rect.left, (float) rect.top);
                canvas.scale(this.scaleX, this.scaleY);
                canvas.drawBitmap(this.renderingBitmap, 0.0f, 0.0f, getPaint());
                if (this.isRunning) {
                    invalidateInternal();
                }
                canvas.restore();
            }
        }
    }

    public void updateCurrentFrame() {
        int i;
        Integer num;
        long elapsedRealtime = SystemClock.elapsedRealtime();
        long abs = Math.abs(elapsedRealtime - this.lastFrameTime);
        if (AndroidUtilities.screenRefreshRate <= 60.0f) {
            i = this.timeBetweenFrames - 6;
        } else {
            i = this.timeBetweenFrames;
        }
        if (this.isRunning) {
            if (this.renderingBitmap == null && this.nextRenderingBitmap == null) {
                scheduleNextGetFrame();
            } else if (this.nextRenderingBitmap == null) {
            } else {
                if ((this.renderingBitmap == null || abs >= ((long) i)) && isCurrentParentViewMaster()) {
                    HashMap<Integer, Integer> hashMap = this.vibrationPattern;
                    if (!(hashMap == null || this.currentParentView == null || (num = hashMap.get(Integer.valueOf(this.currentFrame - 1))) == null)) {
                        this.currentParentView.performHapticFeedback(num.intValue() == 1 ? 0 : 3, 2);
                    }
                    setCurrentFrame(elapsedRealtime, abs, (long) i, false);
                }
            }
        } else if ((this.forceFrameRedraw || (this.decodeSingleFrame && abs >= ((long) i))) && this.nextRenderingBitmap != null) {
            setCurrentFrame(elapsedRealtime, abs, (long) i, true);
        }
    }

    public int getMinimumHeight() {
        return this.height;
    }

    public int getMinimumWidth() {
        return this.width;
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
        return this.nativePtr != 0 && !(this.renderingBitmap == null && this.nextRenderingBitmap == null) && !this.isInvalid;
    }

    public void setInvalidateOnProgressSet(boolean z) {
        this.invalidateOnProgressSet = z;
    }
}
