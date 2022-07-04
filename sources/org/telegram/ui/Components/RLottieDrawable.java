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
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.DispatchQueuePool;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.Utilities;

public class RLottieDrawable extends BitmapDrawable implements Animatable {
    private static ThreadLocal<byte[]> bufferLocal = new ThreadLocal<>();
    /* access modifiers changed from: private */
    public static HashSet<String> generatingCacheFiles = new HashSet<>();
    private static final DispatchQueuePool largeSizeLoadFrameRunnableQueue = new DispatchQueuePool(4);
    private static final DispatchQueuePool loadFrameRunnableQueue = new DispatchQueuePool(2);
    /* access modifiers changed from: private */
    public static ThreadPoolExecutor lottieCacheGenerateQueue;
    private static ThreadLocal<byte[]> readBufferLocal = new ThreadLocal<>();
    protected static final Handler uiHandler = new Handler(Looper.getMainLooper());
    private boolean applyTransformation;
    private boolean applyingLayerColors;
    protected int autoRepeat;
    protected int autoRepeatPlayCount;
    protected volatile Bitmap backgroundBitmap;
    File cacheFile;
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
    File file;
    private int finishFrame;
    private boolean forceFrameRedraw;
    private WeakReference<Runnable> frameReadyCallback;
    protected CountDownLatch frameWaitSync;
    protected int height;
    private boolean invalidateOnProgressSet;
    protected int isDice;
    private boolean isInvalid;
    protected volatile boolean isRecycled;
    protected volatile boolean isRunning;
    private long lastFrameTime;
    private DispatchQueuePool loadFrameQueue;
    protected Runnable loadFrameRunnable;
    protected Runnable loadFrameTask;
    protected boolean loadingInBackground;
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
    private ArrayList<WeakReference<View>> parentViews;
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
    long startTime;
    protected int timeBetweenFrames;
    protected Runnable uiRunnable;
    /* access modifiers changed from: private */
    public Runnable uiRunnableCacheFinished;
    /* access modifiers changed from: private */
    public Runnable uiRunnableGenerateCache;
    private Runnable uiRunnableLastFrame;
    protected Runnable uiRunnableNoFrame;
    private HashMap<Integer, Integer> vibrationPattern;
    protected boolean waitingForNextTask;
    protected int width;

    public static native long create(String str, String str2, int i, int i2, int[] iArr, boolean z, int[] iArr2, boolean z2, int i3);

    /* access modifiers changed from: private */
    public static native void createCache(long j, int i, int i2);

    protected static native long createWithJson(String str, String str2, int[] iArr, int[] iArr2);

    public static native void destroy(long j);

    private static native String getCacheFile(long j);

    public static native int getFrame(long j, int i, Bitmap bitmap, int i2, int i3, int i4, boolean z);

    /* access modifiers changed from: private */
    public static native void replaceColors(long j, int[] iArr);

    /* access modifiers changed from: private */
    public static native void setLayerColor(long j, String str, int i);

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
        ArrayList<Bitmap> bitmapToRecycle = new ArrayList<>();
        bitmapToRecycle.add(this.renderingBitmap);
        bitmapToRecycle.add(this.nextRenderingBitmap);
        this.renderingBitmap = null;
        this.backgroundBitmap = null;
        AndroidUtilities.recycleBitmaps(bitmapToRecycle);
        if (this.onAnimationEndListener != null) {
            this.onAnimationEndListener = null;
        }
        invalidateInternal();
    }

    public void setOnFinishCallback(Runnable callback, int frame) {
        if (callback != null) {
            this.onFinishCallback = new WeakReference<>(callback);
            this.finishFrame = frame;
        } else if (this.onFinishCallback != null) {
            this.onFinishCallback = null;
        }
    }

    public RLottieDrawable(File file2, int w, int h, boolean precache2, boolean limitFps) {
        this(file2, w, h, precache2, limitFps, (int[]) null, 0);
    }

    public RLottieDrawable(File file2, int w, int h, boolean precache2, boolean limitFps, int[] colorReplacement, int fitzModifier) {
        int i = w;
        int i2 = h;
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
        DispatchQueuePool dispatchQueuePool = loadFrameRunnableQueue;
        this.loadFrameQueue = dispatchQueuePool;
        this.uiRunnableNoFrame = new Runnable() {
            public void run() {
                RLottieDrawable.this.loadFrameTask = null;
                RLottieDrawable.this.decodeFrameFinishedInternal();
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
        this.uiRunnableLastFrame = new Runnable() {
            public void run() {
                boolean unused = RLottieDrawable.this.singleFrameDecoded = true;
                RLottieDrawable.this.isRunning = false;
                RLottieDrawable.this.invalidateInternal();
                RLottieDrawable.this.decodeFrameFinishedInternal();
            }
        };
        this.uiRunnableGenerateCache = new Runnable() {
            public void run() {
                if (!RLottieDrawable.this.isRecycled && !RLottieDrawable.this.destroyWhenDone && RLottieDrawable.this.nativePtr != 0) {
                    RLottieDrawable.this.startTime = System.currentTimeMillis();
                    ThreadPoolExecutor access$200 = RLottieDrawable.lottieCacheGenerateQueue;
                    RLottieDrawable rLottieDrawable = RLottieDrawable.this;
                    RLottieDrawable$4$$ExternalSyntheticLambda0 rLottieDrawable$4$$ExternalSyntheticLambda0 = new RLottieDrawable$4$$ExternalSyntheticLambda0(this);
                    rLottieDrawable.cacheGenerateTask = rLottieDrawable$4$$ExternalSyntheticLambda0;
                    access$200.execute(rLottieDrawable$4$$ExternalSyntheticLambda0);
                }
            }

            /* renamed from: lambda$run$0$org-telegram-ui-Components-RLottieDrawable$4  reason: not valid java name */
            public /* synthetic */ void m1280lambda$run$0$orgtelegramuiComponentsRLottieDrawable$4() {
                RLottieDrawable.createCache(RLottieDrawable.this.nativePtr, RLottieDrawable.this.width, RLottieDrawable.this.height);
                RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnableCacheFinished);
            }
        };
        this.uiRunnableCacheFinished = new Runnable() {
            public void run() {
                RLottieDrawable.this.cacheGenerateTask = null;
                RLottieDrawable.generatingCacheFiles.remove(RLottieDrawable.this.cacheFile.getPath());
                RLottieDrawable.this.decodeFrameFinishedInternal();
            }
        };
        this.loadFrameRunnable = new Runnable() {
            public void run() {
                long ptrToUse;
                if (!RLottieDrawable.this.isRecycled) {
                    if (RLottieDrawable.this.nativePtr == 0 || (RLottieDrawable.this.isDice == 2 && RLottieDrawable.this.secondNativePtr == 0)) {
                        if (RLottieDrawable.this.frameWaitSync != null) {
                            RLottieDrawable.this.frameWaitSync.countDown();
                        }
                        RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnableNoFrame);
                        return;
                    }
                    if (RLottieDrawable.this.backgroundBitmap == null) {
                        try {
                            RLottieDrawable rLottieDrawable = RLottieDrawable.this;
                            rLottieDrawable.backgroundBitmap = Bitmap.createBitmap(rLottieDrawable.width, RLottieDrawable.this.height, Bitmap.Config.ARGB_8888);
                        } catch (Throwable e) {
                            FileLog.e(e);
                        }
                    }
                    if (RLottieDrawable.this.backgroundBitmap != null) {
                        try {
                            if (!RLottieDrawable.this.pendingColorUpdates.isEmpty()) {
                                for (Map.Entry<String, Integer> entry : RLottieDrawable.this.pendingColorUpdates.entrySet()) {
                                    RLottieDrawable.setLayerColor(RLottieDrawable.this.nativePtr, entry.getKey(), entry.getValue().intValue());
                                }
                                RLottieDrawable.this.pendingColorUpdates.clear();
                            }
                        } catch (Exception e2) {
                        }
                        if (RLottieDrawable.this.pendingReplaceColors != null) {
                            RLottieDrawable.replaceColors(RLottieDrawable.this.nativePtr, RLottieDrawable.this.pendingReplaceColors);
                            int[] unused = RLottieDrawable.this.pendingReplaceColors = null;
                        }
                        try {
                            if (RLottieDrawable.this.isDice == 1) {
                                ptrToUse = RLottieDrawable.this.nativePtr;
                            } else if (RLottieDrawable.this.isDice == 2) {
                                ptrToUse = RLottieDrawable.this.secondNativePtr;
                                if (RLottieDrawable.this.setLastFrame) {
                                    RLottieDrawable rLottieDrawable2 = RLottieDrawable.this;
                                    rLottieDrawable2.currentFrame = rLottieDrawable2.secondFramesCount - 1;
                                }
                            } else {
                                ptrToUse = RLottieDrawable.this.nativePtr;
                            }
                            if (RLottieDrawable.getFrame(ptrToUse, RLottieDrawable.this.currentFrame, RLottieDrawable.this.backgroundBitmap, RLottieDrawable.this.width, RLottieDrawable.this.height, RLottieDrawable.this.backgroundBitmap.getRowBytes(), true) == -1) {
                                RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnableNoFrame);
                                if (RLottieDrawable.this.frameWaitSync != null) {
                                    RLottieDrawable.this.frameWaitSync.countDown();
                                    return;
                                }
                                return;
                            }
                            if (RLottieDrawable.this.metaData[2] != 0) {
                                RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnableGenerateCache);
                                RLottieDrawable.this.metaData[2] = 0;
                            }
                            RLottieDrawable rLottieDrawable3 = RLottieDrawable.this;
                            rLottieDrawable3.nextRenderingBitmap = rLottieDrawable3.backgroundBitmap;
                            int framesPerUpdates = RLottieDrawable.this.shouldLimitFps ? 2 : 1;
                            if (RLottieDrawable.this.isDice == 1) {
                                if (RLottieDrawable.this.currentFrame + framesPerUpdates < (RLottieDrawable.this.diceSwitchFramesCount == -1 ? RLottieDrawable.this.metaData[0] : RLottieDrawable.this.diceSwitchFramesCount)) {
                                    RLottieDrawable.this.currentFrame += framesPerUpdates;
                                } else {
                                    RLottieDrawable.this.currentFrame = 0;
                                    RLottieDrawable.this.nextFrameIsLast = false;
                                    if (RLottieDrawable.this.secondNativePtr != 0) {
                                        RLottieDrawable.this.isDice = 2;
                                    }
                                }
                            } else if (RLottieDrawable.this.isDice == 2) {
                                if (RLottieDrawable.this.currentFrame + framesPerUpdates < RLottieDrawable.this.secondFramesCount) {
                                    RLottieDrawable.this.currentFrame += framesPerUpdates;
                                } else {
                                    RLottieDrawable.this.nextFrameIsLast = true;
                                    RLottieDrawable.this.autoRepeatPlayCount++;
                                }
                            } else if (RLottieDrawable.this.customEndFrame < 0 || !RLottieDrawable.this.playInDirectionOfCustomEndFrame) {
                                if (RLottieDrawable.this.currentFrame + framesPerUpdates < (RLottieDrawable.this.customEndFrame >= 0 ? RLottieDrawable.this.customEndFrame : RLottieDrawable.this.metaData[0])) {
                                    if (RLottieDrawable.this.autoRepeat == 3) {
                                        RLottieDrawable.this.nextFrameIsLast = true;
                                        RLottieDrawable.this.autoRepeatPlayCount++;
                                    } else {
                                        RLottieDrawable.this.currentFrame += framesPerUpdates;
                                        RLottieDrawable.this.nextFrameIsLast = false;
                                    }
                                } else if (RLottieDrawable.this.autoRepeat == 1) {
                                    RLottieDrawable.this.currentFrame = 0;
                                    RLottieDrawable.this.nextFrameIsLast = false;
                                } else if (RLottieDrawable.this.autoRepeat == 2) {
                                    RLottieDrawable.this.currentFrame = 0;
                                    RLottieDrawable.this.nextFrameIsLast = true;
                                    RLottieDrawable.this.autoRepeatPlayCount++;
                                } else {
                                    RLottieDrawable.this.nextFrameIsLast = true;
                                    RLottieDrawable.this.checkDispatchOnAnimationEnd();
                                }
                            } else if (RLottieDrawable.this.currentFrame > RLottieDrawable.this.customEndFrame) {
                                if (RLottieDrawable.this.currentFrame - framesPerUpdates >= RLottieDrawable.this.customEndFrame) {
                                    RLottieDrawable.this.currentFrame -= framesPerUpdates;
                                    RLottieDrawable.this.nextFrameIsLast = false;
                                } else {
                                    RLottieDrawable.this.nextFrameIsLast = true;
                                    RLottieDrawable.this.checkDispatchOnAnimationEnd();
                                }
                            } else if (RLottieDrawable.this.currentFrame + framesPerUpdates < RLottieDrawable.this.customEndFrame) {
                                RLottieDrawable.this.currentFrame += framesPerUpdates;
                                RLottieDrawable.this.nextFrameIsLast = false;
                            } else {
                                RLottieDrawable.this.nextFrameIsLast = true;
                                RLottieDrawable.this.checkDispatchOnAnimationEnd();
                            }
                        } catch (Exception e3) {
                            FileLog.e((Throwable) e3);
                        }
                    }
                    RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnable);
                    if (RLottieDrawable.this.frameWaitSync != null) {
                        RLottieDrawable.this.frameWaitSync.countDown();
                    }
                }
            }
        };
        this.width = i;
        this.height = i2;
        this.shouldLimitFps = limitFps;
        getPaint().setFlags(2);
        this.file = file2;
        this.nativePtr = create(file2.getAbsolutePath(), (String) null, w, h, iArr, precache2, colorReplacement, this.shouldLimitFps, fitzModifier);
        if (precache2 && lottieCacheGenerateQueue == null) {
            lottieCacheGenerateQueue = new ThreadPoolExecutor(1, 1, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue());
        }
        if (i > AndroidUtilities.dp(120.0f) || i2 > AndroidUtilities.dp(120.0f)) {
            this.loadFrameQueue = largeSizeLoadFrameRunnableQueue;
        } else {
            this.loadFrameQueue = dispatchQueuePool;
        }
        if (this.nativePtr == 0) {
            file2.delete();
        }
        String cacheFilePath = getCacheFile(this.nativePtr);
        if (cacheFilePath != null) {
            this.cacheFile = new File(cacheFilePath);
        }
        if (this.shouldLimitFps && iArr[1] < 60) {
            this.shouldLimitFps = false;
        }
        this.timeBetweenFrames = Math.max(this.shouldLimitFps ? 33 : 16, (int) (1000.0f / ((float) iArr[1])));
    }

    public RLottieDrawable(File file2, String json, int w, int h, boolean precache2, boolean limitFps, int[] colorReplacement, int fitzModifier) {
        int i = w;
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
        DispatchQueuePool dispatchQueuePool = loadFrameRunnableQueue;
        this.loadFrameQueue = dispatchQueuePool;
        this.uiRunnableNoFrame = new Runnable() {
            public void run() {
                RLottieDrawable.this.loadFrameTask = null;
                RLottieDrawable.this.decodeFrameFinishedInternal();
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
        this.uiRunnableLastFrame = new Runnable() {
            public void run() {
                boolean unused = RLottieDrawable.this.singleFrameDecoded = true;
                RLottieDrawable.this.isRunning = false;
                RLottieDrawable.this.invalidateInternal();
                RLottieDrawable.this.decodeFrameFinishedInternal();
            }
        };
        this.uiRunnableGenerateCache = new Runnable() {
            public void run() {
                if (!RLottieDrawable.this.isRecycled && !RLottieDrawable.this.destroyWhenDone && RLottieDrawable.this.nativePtr != 0) {
                    RLottieDrawable.this.startTime = System.currentTimeMillis();
                    ThreadPoolExecutor access$200 = RLottieDrawable.lottieCacheGenerateQueue;
                    RLottieDrawable rLottieDrawable = RLottieDrawable.this;
                    RLottieDrawable$4$$ExternalSyntheticLambda0 rLottieDrawable$4$$ExternalSyntheticLambda0 = new RLottieDrawable$4$$ExternalSyntheticLambda0(this);
                    rLottieDrawable.cacheGenerateTask = rLottieDrawable$4$$ExternalSyntheticLambda0;
                    access$200.execute(rLottieDrawable$4$$ExternalSyntheticLambda0);
                }
            }

            /* renamed from: lambda$run$0$org-telegram-ui-Components-RLottieDrawable$4  reason: not valid java name */
            public /* synthetic */ void m1280lambda$run$0$orgtelegramuiComponentsRLottieDrawable$4() {
                RLottieDrawable.createCache(RLottieDrawable.this.nativePtr, RLottieDrawable.this.width, RLottieDrawable.this.height);
                RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnableCacheFinished);
            }
        };
        this.uiRunnableCacheFinished = new Runnable() {
            public void run() {
                RLottieDrawable.this.cacheGenerateTask = null;
                RLottieDrawable.generatingCacheFiles.remove(RLottieDrawable.this.cacheFile.getPath());
                RLottieDrawable.this.decodeFrameFinishedInternal();
            }
        };
        this.loadFrameRunnable = new Runnable() {
            public void run() {
                long ptrToUse;
                if (!RLottieDrawable.this.isRecycled) {
                    if (RLottieDrawable.this.nativePtr == 0 || (RLottieDrawable.this.isDice == 2 && RLottieDrawable.this.secondNativePtr == 0)) {
                        if (RLottieDrawable.this.frameWaitSync != null) {
                            RLottieDrawable.this.frameWaitSync.countDown();
                        }
                        RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnableNoFrame);
                        return;
                    }
                    if (RLottieDrawable.this.backgroundBitmap == null) {
                        try {
                            RLottieDrawable rLottieDrawable = RLottieDrawable.this;
                            rLottieDrawable.backgroundBitmap = Bitmap.createBitmap(rLottieDrawable.width, RLottieDrawable.this.height, Bitmap.Config.ARGB_8888);
                        } catch (Throwable e) {
                            FileLog.e(e);
                        }
                    }
                    if (RLottieDrawable.this.backgroundBitmap != null) {
                        try {
                            if (!RLottieDrawable.this.pendingColorUpdates.isEmpty()) {
                                for (Map.Entry<String, Integer> entry : RLottieDrawable.this.pendingColorUpdates.entrySet()) {
                                    RLottieDrawable.setLayerColor(RLottieDrawable.this.nativePtr, entry.getKey(), entry.getValue().intValue());
                                }
                                RLottieDrawable.this.pendingColorUpdates.clear();
                            }
                        } catch (Exception e2) {
                        }
                        if (RLottieDrawable.this.pendingReplaceColors != null) {
                            RLottieDrawable.replaceColors(RLottieDrawable.this.nativePtr, RLottieDrawable.this.pendingReplaceColors);
                            int[] unused = RLottieDrawable.this.pendingReplaceColors = null;
                        }
                        try {
                            if (RLottieDrawable.this.isDice == 1) {
                                ptrToUse = RLottieDrawable.this.nativePtr;
                            } else if (RLottieDrawable.this.isDice == 2) {
                                ptrToUse = RLottieDrawable.this.secondNativePtr;
                                if (RLottieDrawable.this.setLastFrame) {
                                    RLottieDrawable rLottieDrawable2 = RLottieDrawable.this;
                                    rLottieDrawable2.currentFrame = rLottieDrawable2.secondFramesCount - 1;
                                }
                            } else {
                                ptrToUse = RLottieDrawable.this.nativePtr;
                            }
                            if (RLottieDrawable.getFrame(ptrToUse, RLottieDrawable.this.currentFrame, RLottieDrawable.this.backgroundBitmap, RLottieDrawable.this.width, RLottieDrawable.this.height, RLottieDrawable.this.backgroundBitmap.getRowBytes(), true) == -1) {
                                RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnableNoFrame);
                                if (RLottieDrawable.this.frameWaitSync != null) {
                                    RLottieDrawable.this.frameWaitSync.countDown();
                                    return;
                                }
                                return;
                            }
                            if (RLottieDrawable.this.metaData[2] != 0) {
                                RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnableGenerateCache);
                                RLottieDrawable.this.metaData[2] = 0;
                            }
                            RLottieDrawable rLottieDrawable3 = RLottieDrawable.this;
                            rLottieDrawable3.nextRenderingBitmap = rLottieDrawable3.backgroundBitmap;
                            int framesPerUpdates = RLottieDrawable.this.shouldLimitFps ? 2 : 1;
                            if (RLottieDrawable.this.isDice == 1) {
                                if (RLottieDrawable.this.currentFrame + framesPerUpdates < (RLottieDrawable.this.diceSwitchFramesCount == -1 ? RLottieDrawable.this.metaData[0] : RLottieDrawable.this.diceSwitchFramesCount)) {
                                    RLottieDrawable.this.currentFrame += framesPerUpdates;
                                } else {
                                    RLottieDrawable.this.currentFrame = 0;
                                    RLottieDrawable.this.nextFrameIsLast = false;
                                    if (RLottieDrawable.this.secondNativePtr != 0) {
                                        RLottieDrawable.this.isDice = 2;
                                    }
                                }
                            } else if (RLottieDrawable.this.isDice == 2) {
                                if (RLottieDrawable.this.currentFrame + framesPerUpdates < RLottieDrawable.this.secondFramesCount) {
                                    RLottieDrawable.this.currentFrame += framesPerUpdates;
                                } else {
                                    RLottieDrawable.this.nextFrameIsLast = true;
                                    RLottieDrawable.this.autoRepeatPlayCount++;
                                }
                            } else if (RLottieDrawable.this.customEndFrame < 0 || !RLottieDrawable.this.playInDirectionOfCustomEndFrame) {
                                if (RLottieDrawable.this.currentFrame + framesPerUpdates < (RLottieDrawable.this.customEndFrame >= 0 ? RLottieDrawable.this.customEndFrame : RLottieDrawable.this.metaData[0])) {
                                    if (RLottieDrawable.this.autoRepeat == 3) {
                                        RLottieDrawable.this.nextFrameIsLast = true;
                                        RLottieDrawable.this.autoRepeatPlayCount++;
                                    } else {
                                        RLottieDrawable.this.currentFrame += framesPerUpdates;
                                        RLottieDrawable.this.nextFrameIsLast = false;
                                    }
                                } else if (RLottieDrawable.this.autoRepeat == 1) {
                                    RLottieDrawable.this.currentFrame = 0;
                                    RLottieDrawable.this.nextFrameIsLast = false;
                                } else if (RLottieDrawable.this.autoRepeat == 2) {
                                    RLottieDrawable.this.currentFrame = 0;
                                    RLottieDrawable.this.nextFrameIsLast = true;
                                    RLottieDrawable.this.autoRepeatPlayCount++;
                                } else {
                                    RLottieDrawable.this.nextFrameIsLast = true;
                                    RLottieDrawable.this.checkDispatchOnAnimationEnd();
                                }
                            } else if (RLottieDrawable.this.currentFrame > RLottieDrawable.this.customEndFrame) {
                                if (RLottieDrawable.this.currentFrame - framesPerUpdates >= RLottieDrawable.this.customEndFrame) {
                                    RLottieDrawable.this.currentFrame -= framesPerUpdates;
                                    RLottieDrawable.this.nextFrameIsLast = false;
                                } else {
                                    RLottieDrawable.this.nextFrameIsLast = true;
                                    RLottieDrawable.this.checkDispatchOnAnimationEnd();
                                }
                            } else if (RLottieDrawable.this.currentFrame + framesPerUpdates < RLottieDrawable.this.customEndFrame) {
                                RLottieDrawable.this.currentFrame += framesPerUpdates;
                                RLottieDrawable.this.nextFrameIsLast = false;
                            } else {
                                RLottieDrawable.this.nextFrameIsLast = true;
                                RLottieDrawable.this.checkDispatchOnAnimationEnd();
                            }
                        } catch (Exception e3) {
                            FileLog.e((Throwable) e3);
                        }
                    }
                    RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnable);
                    if (RLottieDrawable.this.frameWaitSync != null) {
                        RLottieDrawable.this.frameWaitSync.countDown();
                    }
                }
            }
        };
        this.width = i;
        this.height = h;
        this.shouldLimitFps = limitFps;
        getPaint().setFlags(2);
        this.nativePtr = create(file2.getAbsolutePath(), json, w, h, iArr, precache2, colorReplacement, this.shouldLimitFps, fitzModifier);
        if (precache2 && lottieCacheGenerateQueue == null) {
            lottieCacheGenerateQueue = new ThreadPoolExecutor(1, 1, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue());
        }
        if (this.nativePtr == 0) {
            file2.delete();
        }
        if (this.shouldLimitFps && iArr[1] < 60) {
            this.shouldLimitFps = false;
        }
        this.timeBetweenFrames = Math.max(this.shouldLimitFps ? 33 : 16, (int) (1000.0f / ((float) iArr[1])));
        if (i > AndroidUtilities.dp(100.0f) || i > AndroidUtilities.dp(100.0f)) {
            this.loadFrameQueue = largeSizeLoadFrameRunnableQueue;
        } else {
            this.loadFrameQueue = dispatchQueuePool;
        }
    }

    public RLottieDrawable(int rawRes, String name, int w, int h) {
        this(rawRes, name, w, h, true, (int[]) null);
    }

    public RLottieDrawable(String diceEmoji, int w, int h) {
        String jsonString;
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
        this.loadFrameQueue = loadFrameRunnableQueue;
        this.uiRunnableNoFrame = new Runnable() {
            public void run() {
                RLottieDrawable.this.loadFrameTask = null;
                RLottieDrawable.this.decodeFrameFinishedInternal();
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
        this.uiRunnableLastFrame = new Runnable() {
            public void run() {
                boolean unused = RLottieDrawable.this.singleFrameDecoded = true;
                RLottieDrawable.this.isRunning = false;
                RLottieDrawable.this.invalidateInternal();
                RLottieDrawable.this.decodeFrameFinishedInternal();
            }
        };
        this.uiRunnableGenerateCache = new Runnable() {
            public void run() {
                if (!RLottieDrawable.this.isRecycled && !RLottieDrawable.this.destroyWhenDone && RLottieDrawable.this.nativePtr != 0) {
                    RLottieDrawable.this.startTime = System.currentTimeMillis();
                    ThreadPoolExecutor access$200 = RLottieDrawable.lottieCacheGenerateQueue;
                    RLottieDrawable rLottieDrawable = RLottieDrawable.this;
                    RLottieDrawable$4$$ExternalSyntheticLambda0 rLottieDrawable$4$$ExternalSyntheticLambda0 = new RLottieDrawable$4$$ExternalSyntheticLambda0(this);
                    rLottieDrawable.cacheGenerateTask = rLottieDrawable$4$$ExternalSyntheticLambda0;
                    access$200.execute(rLottieDrawable$4$$ExternalSyntheticLambda0);
                }
            }

            /* renamed from: lambda$run$0$org-telegram-ui-Components-RLottieDrawable$4  reason: not valid java name */
            public /* synthetic */ void m1280lambda$run$0$orgtelegramuiComponentsRLottieDrawable$4() {
                RLottieDrawable.createCache(RLottieDrawable.this.nativePtr, RLottieDrawable.this.width, RLottieDrawable.this.height);
                RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnableCacheFinished);
            }
        };
        this.uiRunnableCacheFinished = new Runnable() {
            public void run() {
                RLottieDrawable.this.cacheGenerateTask = null;
                RLottieDrawable.generatingCacheFiles.remove(RLottieDrawable.this.cacheFile.getPath());
                RLottieDrawable.this.decodeFrameFinishedInternal();
            }
        };
        this.loadFrameRunnable = new Runnable() {
            public void run() {
                long ptrToUse;
                if (!RLottieDrawable.this.isRecycled) {
                    if (RLottieDrawable.this.nativePtr == 0 || (RLottieDrawable.this.isDice == 2 && RLottieDrawable.this.secondNativePtr == 0)) {
                        if (RLottieDrawable.this.frameWaitSync != null) {
                            RLottieDrawable.this.frameWaitSync.countDown();
                        }
                        RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnableNoFrame);
                        return;
                    }
                    if (RLottieDrawable.this.backgroundBitmap == null) {
                        try {
                            RLottieDrawable rLottieDrawable = RLottieDrawable.this;
                            rLottieDrawable.backgroundBitmap = Bitmap.createBitmap(rLottieDrawable.width, RLottieDrawable.this.height, Bitmap.Config.ARGB_8888);
                        } catch (Throwable e) {
                            FileLog.e(e);
                        }
                    }
                    if (RLottieDrawable.this.backgroundBitmap != null) {
                        try {
                            if (!RLottieDrawable.this.pendingColorUpdates.isEmpty()) {
                                for (Map.Entry<String, Integer> entry : RLottieDrawable.this.pendingColorUpdates.entrySet()) {
                                    RLottieDrawable.setLayerColor(RLottieDrawable.this.nativePtr, entry.getKey(), entry.getValue().intValue());
                                }
                                RLottieDrawable.this.pendingColorUpdates.clear();
                            }
                        } catch (Exception e2) {
                        }
                        if (RLottieDrawable.this.pendingReplaceColors != null) {
                            RLottieDrawable.replaceColors(RLottieDrawable.this.nativePtr, RLottieDrawable.this.pendingReplaceColors);
                            int[] unused = RLottieDrawable.this.pendingReplaceColors = null;
                        }
                        try {
                            if (RLottieDrawable.this.isDice == 1) {
                                ptrToUse = RLottieDrawable.this.nativePtr;
                            } else if (RLottieDrawable.this.isDice == 2) {
                                ptrToUse = RLottieDrawable.this.secondNativePtr;
                                if (RLottieDrawable.this.setLastFrame) {
                                    RLottieDrawable rLottieDrawable2 = RLottieDrawable.this;
                                    rLottieDrawable2.currentFrame = rLottieDrawable2.secondFramesCount - 1;
                                }
                            } else {
                                ptrToUse = RLottieDrawable.this.nativePtr;
                            }
                            if (RLottieDrawable.getFrame(ptrToUse, RLottieDrawable.this.currentFrame, RLottieDrawable.this.backgroundBitmap, RLottieDrawable.this.width, RLottieDrawable.this.height, RLottieDrawable.this.backgroundBitmap.getRowBytes(), true) == -1) {
                                RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnableNoFrame);
                                if (RLottieDrawable.this.frameWaitSync != null) {
                                    RLottieDrawable.this.frameWaitSync.countDown();
                                    return;
                                }
                                return;
                            }
                            if (RLottieDrawable.this.metaData[2] != 0) {
                                RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnableGenerateCache);
                                RLottieDrawable.this.metaData[2] = 0;
                            }
                            RLottieDrawable rLottieDrawable3 = RLottieDrawable.this;
                            rLottieDrawable3.nextRenderingBitmap = rLottieDrawable3.backgroundBitmap;
                            int framesPerUpdates = RLottieDrawable.this.shouldLimitFps ? 2 : 1;
                            if (RLottieDrawable.this.isDice == 1) {
                                if (RLottieDrawable.this.currentFrame + framesPerUpdates < (RLottieDrawable.this.diceSwitchFramesCount == -1 ? RLottieDrawable.this.metaData[0] : RLottieDrawable.this.diceSwitchFramesCount)) {
                                    RLottieDrawable.this.currentFrame += framesPerUpdates;
                                } else {
                                    RLottieDrawable.this.currentFrame = 0;
                                    RLottieDrawable.this.nextFrameIsLast = false;
                                    if (RLottieDrawable.this.secondNativePtr != 0) {
                                        RLottieDrawable.this.isDice = 2;
                                    }
                                }
                            } else if (RLottieDrawable.this.isDice == 2) {
                                if (RLottieDrawable.this.currentFrame + framesPerUpdates < RLottieDrawable.this.secondFramesCount) {
                                    RLottieDrawable.this.currentFrame += framesPerUpdates;
                                } else {
                                    RLottieDrawable.this.nextFrameIsLast = true;
                                    RLottieDrawable.this.autoRepeatPlayCount++;
                                }
                            } else if (RLottieDrawable.this.customEndFrame < 0 || !RLottieDrawable.this.playInDirectionOfCustomEndFrame) {
                                if (RLottieDrawable.this.currentFrame + framesPerUpdates < (RLottieDrawable.this.customEndFrame >= 0 ? RLottieDrawable.this.customEndFrame : RLottieDrawable.this.metaData[0])) {
                                    if (RLottieDrawable.this.autoRepeat == 3) {
                                        RLottieDrawable.this.nextFrameIsLast = true;
                                        RLottieDrawable.this.autoRepeatPlayCount++;
                                    } else {
                                        RLottieDrawable.this.currentFrame += framesPerUpdates;
                                        RLottieDrawable.this.nextFrameIsLast = false;
                                    }
                                } else if (RLottieDrawable.this.autoRepeat == 1) {
                                    RLottieDrawable.this.currentFrame = 0;
                                    RLottieDrawable.this.nextFrameIsLast = false;
                                } else if (RLottieDrawable.this.autoRepeat == 2) {
                                    RLottieDrawable.this.currentFrame = 0;
                                    RLottieDrawable.this.nextFrameIsLast = true;
                                    RLottieDrawable.this.autoRepeatPlayCount++;
                                } else {
                                    RLottieDrawable.this.nextFrameIsLast = true;
                                    RLottieDrawable.this.checkDispatchOnAnimationEnd();
                                }
                            } else if (RLottieDrawable.this.currentFrame > RLottieDrawable.this.customEndFrame) {
                                if (RLottieDrawable.this.currentFrame - framesPerUpdates >= RLottieDrawable.this.customEndFrame) {
                                    RLottieDrawable.this.currentFrame -= framesPerUpdates;
                                    RLottieDrawable.this.nextFrameIsLast = false;
                                } else {
                                    RLottieDrawable.this.nextFrameIsLast = true;
                                    RLottieDrawable.this.checkDispatchOnAnimationEnd();
                                }
                            } else if (RLottieDrawable.this.currentFrame + framesPerUpdates < RLottieDrawable.this.customEndFrame) {
                                RLottieDrawable.this.currentFrame += framesPerUpdates;
                                RLottieDrawable.this.nextFrameIsLast = false;
                            } else {
                                RLottieDrawable.this.nextFrameIsLast = true;
                                RLottieDrawable.this.checkDispatchOnAnimationEnd();
                            }
                        } catch (Exception e3) {
                            FileLog.e((Throwable) e3);
                        }
                    }
                    RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnable);
                    if (RLottieDrawable.this.frameWaitSync != null) {
                        RLottieDrawable.this.frameWaitSync.countDown();
                    }
                }
            }
        };
        this.width = w;
        this.height = h;
        this.isDice = 1;
        if ("🎲".equals(diceEmoji)) {
            jsonString = readRes((File) null, NUM);
            this.diceSwitchFramesCount = 60;
        } else if ("🎯".equals(diceEmoji)) {
            jsonString = readRes((File) null, NUM);
        } else {
            jsonString = null;
        }
        getPaint().setFlags(2);
        if (TextUtils.isEmpty(jsonString)) {
            this.timeBetweenFrames = 16;
            return;
        }
        this.nativePtr = createWithJson(jsonString, "dice", iArr, (int[]) null);
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

    public void setOnAnimationEndListener(Runnable onAnimationEndListener2) {
        this.onAnimationEndListener = onAnimationEndListener2;
    }

    public boolean isDice() {
        return this.isDice != 0;
    }

    public boolean setBaseDice(File path) {
        if (this.nativePtr != 0 || this.loadingInBackground) {
            return true;
        }
        String jsonString = readRes(path, 0);
        if (TextUtils.isEmpty(jsonString)) {
            return false;
        }
        this.loadingInBackground = true;
        Utilities.globalQueue.postRunnable(new RLottieDrawable$$ExternalSyntheticLambda2(this, jsonString));
        return true;
    }

    /* renamed from: lambda$setBaseDice$1$org-telegram-ui-Components-RLottieDrawable  reason: not valid java name */
    public /* synthetic */ void m1276lambda$setBaseDice$1$orgtelegramuiComponentsRLottieDrawable(String jsonString) {
        this.nativePtr = createWithJson(jsonString, "dice", this.metaData, (int[]) null);
        AndroidUtilities.runOnUIThread(new RLottieDrawable$$ExternalSyntheticLambda0(this));
    }

    /* renamed from: lambda$setBaseDice$0$org-telegram-ui-Components-RLottieDrawable  reason: not valid java name */
    public /* synthetic */ void m1275lambda$setBaseDice$0$orgtelegramuiComponentsRLottieDrawable() {
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

    public boolean setDiceNumber(File path, boolean instant) {
        if (this.secondNativePtr != 0 || this.secondLoadingInBackground) {
            return true;
        }
        String jsonString = readRes(path, 0);
        if (TextUtils.isEmpty(jsonString)) {
            return false;
        }
        if (instant && this.nextRenderingBitmap == null && this.renderingBitmap == null && this.loadFrameTask == null) {
            this.isDice = 2;
            this.setLastFrame = true;
        }
        this.secondLoadingInBackground = true;
        Utilities.globalQueue.postRunnable(new RLottieDrawable$$ExternalSyntheticLambda3(this, jsonString));
        return true;
    }

    /* renamed from: lambda$setDiceNumber$4$org-telegram-ui-Components-RLottieDrawable  reason: not valid java name */
    public /* synthetic */ void m1279x8d5250cd(String jsonString) {
        if (this.destroyAfterLoading) {
            AndroidUtilities.runOnUIThread(new RLottieDrawable$$ExternalSyntheticLambda1(this));
            return;
        }
        int[] metaData2 = new int[3];
        this.secondNativePtr = createWithJson(jsonString, "dice", metaData2, (int[]) null);
        AndroidUtilities.runOnUIThread(new RLottieDrawable$$ExternalSyntheticLambda4(this, metaData2));
    }

    /* renamed from: lambda$setDiceNumber$2$org-telegram-ui-Components-RLottieDrawable  reason: not valid java name */
    public /* synthetic */ void m1277x7797e0f() {
        this.secondLoadingInBackground = false;
        if (!this.loadingInBackground && this.destroyAfterLoading) {
            recycle();
        }
    }

    /* renamed from: lambda$setDiceNumber$3$org-telegram-ui-Components-RLottieDrawable  reason: not valid java name */
    public /* synthetic */ void m1278xca65e76e(int[] metaData2) {
        this.secondLoadingInBackground = false;
        if (this.destroyAfterLoading) {
            recycle();
            return;
        }
        this.secondFramesCount = metaData2[0];
        this.timeBetweenFrames = Math.max(16, (int) (1000.0f / ((float) metaData2[1])));
        scheduleNextGetFrame();
        invalidateInternal();
    }

    public RLottieDrawable(int rawRes, String name, int w, int h, boolean startDecode, int[] colorReplacement) {
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
        this.loadFrameQueue = loadFrameRunnableQueue;
        this.uiRunnableNoFrame = new Runnable() {
            public void run() {
                RLottieDrawable.this.loadFrameTask = null;
                RLottieDrawable.this.decodeFrameFinishedInternal();
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
        this.uiRunnableLastFrame = new Runnable() {
            public void run() {
                boolean unused = RLottieDrawable.this.singleFrameDecoded = true;
                RLottieDrawable.this.isRunning = false;
                RLottieDrawable.this.invalidateInternal();
                RLottieDrawable.this.decodeFrameFinishedInternal();
            }
        };
        this.uiRunnableGenerateCache = new Runnable() {
            public void run() {
                if (!RLottieDrawable.this.isRecycled && !RLottieDrawable.this.destroyWhenDone && RLottieDrawable.this.nativePtr != 0) {
                    RLottieDrawable.this.startTime = System.currentTimeMillis();
                    ThreadPoolExecutor access$200 = RLottieDrawable.lottieCacheGenerateQueue;
                    RLottieDrawable rLottieDrawable = RLottieDrawable.this;
                    RLottieDrawable$4$$ExternalSyntheticLambda0 rLottieDrawable$4$$ExternalSyntheticLambda0 = new RLottieDrawable$4$$ExternalSyntheticLambda0(this);
                    rLottieDrawable.cacheGenerateTask = rLottieDrawable$4$$ExternalSyntheticLambda0;
                    access$200.execute(rLottieDrawable$4$$ExternalSyntheticLambda0);
                }
            }

            /* renamed from: lambda$run$0$org-telegram-ui-Components-RLottieDrawable$4  reason: not valid java name */
            public /* synthetic */ void m1280lambda$run$0$orgtelegramuiComponentsRLottieDrawable$4() {
                RLottieDrawable.createCache(RLottieDrawable.this.nativePtr, RLottieDrawable.this.width, RLottieDrawable.this.height);
                RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnableCacheFinished);
            }
        };
        this.uiRunnableCacheFinished = new Runnable() {
            public void run() {
                RLottieDrawable.this.cacheGenerateTask = null;
                RLottieDrawable.generatingCacheFiles.remove(RLottieDrawable.this.cacheFile.getPath());
                RLottieDrawable.this.decodeFrameFinishedInternal();
            }
        };
        this.loadFrameRunnable = new Runnable() {
            public void run() {
                long ptrToUse;
                if (!RLottieDrawable.this.isRecycled) {
                    if (RLottieDrawable.this.nativePtr == 0 || (RLottieDrawable.this.isDice == 2 && RLottieDrawable.this.secondNativePtr == 0)) {
                        if (RLottieDrawable.this.frameWaitSync != null) {
                            RLottieDrawable.this.frameWaitSync.countDown();
                        }
                        RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnableNoFrame);
                        return;
                    }
                    if (RLottieDrawable.this.backgroundBitmap == null) {
                        try {
                            RLottieDrawable rLottieDrawable = RLottieDrawable.this;
                            rLottieDrawable.backgroundBitmap = Bitmap.createBitmap(rLottieDrawable.width, RLottieDrawable.this.height, Bitmap.Config.ARGB_8888);
                        } catch (Throwable e) {
                            FileLog.e(e);
                        }
                    }
                    if (RLottieDrawable.this.backgroundBitmap != null) {
                        try {
                            if (!RLottieDrawable.this.pendingColorUpdates.isEmpty()) {
                                for (Map.Entry<String, Integer> entry : RLottieDrawable.this.pendingColorUpdates.entrySet()) {
                                    RLottieDrawable.setLayerColor(RLottieDrawable.this.nativePtr, entry.getKey(), entry.getValue().intValue());
                                }
                                RLottieDrawable.this.pendingColorUpdates.clear();
                            }
                        } catch (Exception e2) {
                        }
                        if (RLottieDrawable.this.pendingReplaceColors != null) {
                            RLottieDrawable.replaceColors(RLottieDrawable.this.nativePtr, RLottieDrawable.this.pendingReplaceColors);
                            int[] unused = RLottieDrawable.this.pendingReplaceColors = null;
                        }
                        try {
                            if (RLottieDrawable.this.isDice == 1) {
                                ptrToUse = RLottieDrawable.this.nativePtr;
                            } else if (RLottieDrawable.this.isDice == 2) {
                                ptrToUse = RLottieDrawable.this.secondNativePtr;
                                if (RLottieDrawable.this.setLastFrame) {
                                    RLottieDrawable rLottieDrawable2 = RLottieDrawable.this;
                                    rLottieDrawable2.currentFrame = rLottieDrawable2.secondFramesCount - 1;
                                }
                            } else {
                                ptrToUse = RLottieDrawable.this.nativePtr;
                            }
                            if (RLottieDrawable.getFrame(ptrToUse, RLottieDrawable.this.currentFrame, RLottieDrawable.this.backgroundBitmap, RLottieDrawable.this.width, RLottieDrawable.this.height, RLottieDrawable.this.backgroundBitmap.getRowBytes(), true) == -1) {
                                RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnableNoFrame);
                                if (RLottieDrawable.this.frameWaitSync != null) {
                                    RLottieDrawable.this.frameWaitSync.countDown();
                                    return;
                                }
                                return;
                            }
                            if (RLottieDrawable.this.metaData[2] != 0) {
                                RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnableGenerateCache);
                                RLottieDrawable.this.metaData[2] = 0;
                            }
                            RLottieDrawable rLottieDrawable3 = RLottieDrawable.this;
                            rLottieDrawable3.nextRenderingBitmap = rLottieDrawable3.backgroundBitmap;
                            int framesPerUpdates = RLottieDrawable.this.shouldLimitFps ? 2 : 1;
                            if (RLottieDrawable.this.isDice == 1) {
                                if (RLottieDrawable.this.currentFrame + framesPerUpdates < (RLottieDrawable.this.diceSwitchFramesCount == -1 ? RLottieDrawable.this.metaData[0] : RLottieDrawable.this.diceSwitchFramesCount)) {
                                    RLottieDrawable.this.currentFrame += framesPerUpdates;
                                } else {
                                    RLottieDrawable.this.currentFrame = 0;
                                    RLottieDrawable.this.nextFrameIsLast = false;
                                    if (RLottieDrawable.this.secondNativePtr != 0) {
                                        RLottieDrawable.this.isDice = 2;
                                    }
                                }
                            } else if (RLottieDrawable.this.isDice == 2) {
                                if (RLottieDrawable.this.currentFrame + framesPerUpdates < RLottieDrawable.this.secondFramesCount) {
                                    RLottieDrawable.this.currentFrame += framesPerUpdates;
                                } else {
                                    RLottieDrawable.this.nextFrameIsLast = true;
                                    RLottieDrawable.this.autoRepeatPlayCount++;
                                }
                            } else if (RLottieDrawable.this.customEndFrame < 0 || !RLottieDrawable.this.playInDirectionOfCustomEndFrame) {
                                if (RLottieDrawable.this.currentFrame + framesPerUpdates < (RLottieDrawable.this.customEndFrame >= 0 ? RLottieDrawable.this.customEndFrame : RLottieDrawable.this.metaData[0])) {
                                    if (RLottieDrawable.this.autoRepeat == 3) {
                                        RLottieDrawable.this.nextFrameIsLast = true;
                                        RLottieDrawable.this.autoRepeatPlayCount++;
                                    } else {
                                        RLottieDrawable.this.currentFrame += framesPerUpdates;
                                        RLottieDrawable.this.nextFrameIsLast = false;
                                    }
                                } else if (RLottieDrawable.this.autoRepeat == 1) {
                                    RLottieDrawable.this.currentFrame = 0;
                                    RLottieDrawable.this.nextFrameIsLast = false;
                                } else if (RLottieDrawable.this.autoRepeat == 2) {
                                    RLottieDrawable.this.currentFrame = 0;
                                    RLottieDrawable.this.nextFrameIsLast = true;
                                    RLottieDrawable.this.autoRepeatPlayCount++;
                                } else {
                                    RLottieDrawable.this.nextFrameIsLast = true;
                                    RLottieDrawable.this.checkDispatchOnAnimationEnd();
                                }
                            } else if (RLottieDrawable.this.currentFrame > RLottieDrawable.this.customEndFrame) {
                                if (RLottieDrawable.this.currentFrame - framesPerUpdates >= RLottieDrawable.this.customEndFrame) {
                                    RLottieDrawable.this.currentFrame -= framesPerUpdates;
                                    RLottieDrawable.this.nextFrameIsLast = false;
                                } else {
                                    RLottieDrawable.this.nextFrameIsLast = true;
                                    RLottieDrawable.this.checkDispatchOnAnimationEnd();
                                }
                            } else if (RLottieDrawable.this.currentFrame + framesPerUpdates < RLottieDrawable.this.customEndFrame) {
                                RLottieDrawable.this.currentFrame += framesPerUpdates;
                                RLottieDrawable.this.nextFrameIsLast = false;
                            } else {
                                RLottieDrawable.this.nextFrameIsLast = true;
                                RLottieDrawable.this.checkDispatchOnAnimationEnd();
                            }
                        } catch (Exception e3) {
                            FileLog.e((Throwable) e3);
                        }
                    }
                    RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnable);
                    if (RLottieDrawable.this.frameWaitSync != null) {
                        RLottieDrawable.this.frameWaitSync.countDown();
                    }
                }
            }
        };
        this.width = w;
        this.height = h;
        this.autoRepeat = 0;
        String jsonString = readRes((File) null, rawRes);
        if (!TextUtils.isEmpty(jsonString)) {
            getPaint().setFlags(2);
            this.nativePtr = createWithJson(jsonString, name, iArr, colorReplacement);
            this.timeBetweenFrames = Math.max(16, (int) (1000.0f / ((float) iArr[1])));
            if (startDecode) {
                setAllowDecodeSingleFrame(true);
            }
        }
    }

    public static String readRes(File path, int rawRes) {
        InputStream inputStream;
        int totalRead = 0;
        byte[] readBuffer = readBufferLocal.get();
        if (readBuffer == null) {
            readBuffer = new byte[65536];
            readBufferLocal.set(readBuffer);
        }
        InputStream inputStream2 = null;
        if (path != null) {
            try {
                inputStream = new FileInputStream(path);
            } catch (Throwable th) {
            }
        } else {
            inputStream = ApplicationLoader.applicationContext.getResources().openRawResource(rawRes);
        }
        byte[] buffer = bufferLocal.get();
        if (buffer == null) {
            buffer = new byte[4096];
            bufferLocal.set(buffer);
        }
        while (true) {
            int read = inputStream.read(buffer, 0, buffer.length);
            int readLen = read;
            if (read < 0) {
                break;
            }
            if (readBuffer.length < totalRead + readLen) {
                byte[] newBuffer = new byte[(readBuffer.length * 2)];
                System.arraycopy(readBuffer, 0, newBuffer, 0, totalRead);
                readBuffer = newBuffer;
                readBufferLocal.set(readBuffer);
            }
            if (readLen > 0) {
                System.arraycopy(buffer, 0, readBuffer, totalRead, readLen);
                totalRead += readLen;
            }
        }
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (Throwable th2) {
            }
        }
        return new String(readBuffer, 0, totalRead);
        return null;
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

    public void setPlayInDirectionOfCustomEndFrame(boolean value) {
        this.playInDirectionOfCustomEndFrame = value;
    }

    public boolean setCustomEndFrame(int frame) {
        if (this.customEndFrame == frame || frame > this.metaData[0]) {
            return false;
        }
        this.customEndFrame = frame;
        return true;
    }

    public int getFramesCount() {
        return this.metaData[0];
    }

    public void addParentView(View view) {
        if (view != null) {
            int a = 0;
            int N = this.parentViews.size();
            while (a < N) {
                if (this.parentViews.get(a).get() != view) {
                    if (this.parentViews.get(a).get() == null) {
                        this.parentViews.remove(a);
                        N--;
                        a--;
                    }
                    a++;
                } else {
                    return;
                }
            }
            this.parentViews.add(0, new WeakReference(view));
        }
    }

    public void removeParentView(View view) {
        if (view != null) {
            int a = 0;
            int N = this.parentViews.size();
            while (a < N) {
                View v = (View) this.parentViews.get(a).get();
                if (v == view || v == null) {
                    this.parentViews.remove(a);
                    N--;
                    a--;
                }
                a++;
            }
        }
    }

    /* access modifiers changed from: protected */
    public boolean hasParentView() {
        if (getCallback() != null) {
            return true;
        }
        int N = this.parentViews.size();
        for (int a = 0; a < N; a = (a - 1) + 1) {
            if (((View) this.parentViews.get(a).get()) != null) {
                return true;
            }
            this.parentViews.remove(a);
            N--;
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public void invalidateInternal() {
        int a = 0;
        int N = this.parentViews.size();
        while (a < N) {
            View view = (View) this.parentViews.get(a).get();
            if (view != null) {
                view.invalidate();
            } else {
                this.parentViews.remove(a);
                N--;
                a--;
            }
            a++;
        }
        if (getCallback() != null) {
            invalidateSelf();
        }
    }

    public void setAllowDecodeSingleFrame(boolean value) {
        this.decodeSingleFrame = value;
        if (value) {
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

    public void setAutoRepeat(int value) {
        if (this.autoRepeat != 2 || value != 3 || this.currentFrame == 0) {
            this.autoRepeat = value;
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

    public int getOpacity() {
        return -2;
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

    public void setVibrationPattern(HashMap<Integer, Integer> pattern) {
        this.vibrationPattern = pattern;
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

    public void replaceColors(int[] colors) {
        this.newReplaceColors = colors;
        requestRedrawColors();
    }

    public void setLayerColor(String layerName, int color) {
        this.newColorUpdates.put(layerName, Integer.valueOf(color));
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
        DispatchQueuePool dispatchQueuePool = this.loadFrameQueue;
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

    public void setCurrentFrame(int frame) {
        setCurrentFrame(frame, true);
    }

    public void setCurrentFrame(int frame, boolean async) {
        setCurrentFrame(frame, async, false);
    }

    public void setCurrentFrame(int frame, boolean async, boolean resetFrame) {
        if (frame >= 0 && frame <= this.metaData[0]) {
            if (this.currentFrame != frame || resetFrame) {
                this.currentFrame = frame;
                this.nextFrameIsLast = false;
                this.singleFrameDecoded = false;
                if (this.invalidateOnProgressSet) {
                    this.isInvalid = true;
                    if (this.loadFrameTask != null) {
                        this.doNotRemoveInvalidOnFrameReady = true;
                    }
                }
                if ((!async || resetFrame) && this.waitingForNextTask && this.nextRenderingBitmap != null) {
                    this.backgroundBitmap = this.nextRenderingBitmap;
                    this.nextRenderingBitmap = null;
                    this.loadFrameTask = null;
                    this.waitingForNextTask = false;
                }
                if (!async && this.loadFrameTask == null) {
                    this.frameWaitSync = new CountDownLatch(1);
                }
                if (resetFrame && !this.isRunning) {
                    this.isRunning = true;
                }
                if (!scheduleNextGetFrame()) {
                    this.forceFrameRedraw = true;
                } else if (!async) {
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

    public void setProgressMs(long ms) {
        setCurrentFrame((int) ((Math.max(0, ms) / ((long) this.timeBetweenFrames)) % ((long) this.metaData[0])), true, true);
    }

    public void setProgress(float progress) {
        setProgress(progress, true);
    }

    public void setProgress(float progress, boolean async) {
        if (progress < 0.0f) {
            progress = 0.0f;
        } else if (progress > 1.0f) {
            progress = 1.0f;
        }
        setCurrentFrame((int) (((float) this.metaData[0]) * progress), async);
    }

    public void setCurrentParentView(View view) {
        this.currentParentView = view;
    }

    private boolean isCurrentParentViewMaster() {
        if (getCallback() != null || this.parentViews.size() <= 1) {
            return true;
        }
        int a = 0;
        int N = this.parentViews.size();
        while (a < N) {
            View view = (View) this.parentViews.get(a).get();
            if (view == null) {
                this.parentViews.remove(a);
                N--;
                a--;
            } else if (view.isShown()) {
                if (view == this.currentParentView) {
                    return true;
                }
                return false;
            }
            a++;
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
    public void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        this.applyTransformation = true;
    }

    private void setCurrentFrame(long now, long timeDiff, long timeCheck, boolean force) {
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
            this.lastFrameTime = now;
        } else {
            this.lastFrameTime = now - Math.min(16, timeDiff - timeCheck);
        }
        if (force && this.forceFrameRedraw) {
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
                    boolean z = false;
                    this.applyTransformation = false;
                    if (Math.abs(this.dstRect.width() - this.width) >= AndroidUtilities.dp(1.0f) || Math.abs(this.dstRect.width() - this.width) >= AndroidUtilities.dp(1.0f)) {
                        z = true;
                    }
                    this.needScale = z;
                }
                if (!this.needScale) {
                    canvas.drawBitmap(this.renderingBitmap, (float) this.dstRect.left, (float) this.dstRect.top, getPaint());
                } else {
                    canvas.save();
                    canvas.translate((float) this.dstRect.left, (float) this.dstRect.top);
                    canvas.scale(this.scaleX, this.scaleY);
                    canvas.drawBitmap(this.renderingBitmap, 0.0f, 0.0f, getPaint());
                    canvas.restore();
                }
                if (this.isRunning) {
                    invalidateInternal();
                }
            }
        }
    }

    public void updateCurrentFrame() {
        int timeCheck;
        Integer force;
        long now = SystemClock.elapsedRealtime();
        long timeDiff = Math.abs(now - this.lastFrameTime);
        if (AndroidUtilities.screenRefreshRate <= 60.0f) {
            timeCheck = this.timeBetweenFrames - 6;
        } else {
            timeCheck = this.timeBetweenFrames;
        }
        if (this.isRunning) {
            if (this.renderingBitmap == null && this.nextRenderingBitmap == null) {
                scheduleNextGetFrame();
            } else if (this.nextRenderingBitmap == null) {
            } else {
                if (this.renderingBitmap == null || timeDiff >= ((long) timeCheck)) {
                    HashMap<Integer, Integer> hashMap = this.vibrationPattern;
                    if (!(hashMap == null || this.currentParentView == null || (force = hashMap.get(Integer.valueOf(this.currentFrame - 1))) == null)) {
                        this.currentParentView.performHapticFeedback(force.intValue() == 1 ? 0 : 3, 2);
                    }
                    setCurrentFrame(now, timeDiff, (long) timeCheck, false);
                }
            }
        } else if ((this.forceFrameRedraw || (this.decodeSingleFrame && timeDiff >= ((long) timeCheck))) && this.nextRenderingBitmap != null) {
            setCurrentFrame(now, timeDiff, (long) timeCheck, true);
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
        if (this.nextRenderingBitmap != null) {
            return this.nextRenderingBitmap;
        }
        return null;
    }

    public boolean hasBitmap() {
        return this.nativePtr != 0 && !(this.renderingBitmap == null && this.nextRenderingBitmap == null) && !this.isInvalid;
    }

    public void setInvalidateOnProgressSet(boolean value) {
        this.invalidateOnProgressSet = value;
    }

    public boolean isGeneratingCache() {
        return this.cacheGenerateTask != null;
    }

    public void setOnFrameReadyRunnable(Runnable onFrameReadyRunnable2) {
        this.onFrameReadyRunnable = onFrameReadyRunnable2;
    }

    public boolean isLastFrame() {
        return this.currentFrame == getFramesCount() - 1;
    }
}
