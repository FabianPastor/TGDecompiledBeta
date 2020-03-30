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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.ui.Components.RLottieDrawable;

public class RLottieDrawable extends BitmapDrawable implements Animatable {
    private static ThreadLocal<byte[]> bufferLocal = new ThreadLocal<>();
    private static ExecutorService loadFrameRunnableQueue = Executors.newCachedThreadPool();
    /* access modifiers changed from: private */
    public static ThreadPoolExecutor lottieCacheGenerateQueue;
    private static ThreadLocal<byte[]> readBufferLocal = new ThreadLocal<>();
    /* access modifiers changed from: private */
    public static final Handler uiHandler = new Handler(Looper.getMainLooper());
    private boolean applyTransformation;
    private boolean applyingLayerColors;
    /* access modifiers changed from: private */
    public int autoRepeat;
    private int autoRepeatPlayCount;
    /* access modifiers changed from: private */
    public volatile Bitmap backgroundBitmap;
    /* access modifiers changed from: private */
    public Runnable cacheGenerateTask;
    /* access modifiers changed from: private */
    public int currentFrame;
    private View currentParentView;
    private boolean decodeSingleFrame;
    /* access modifiers changed from: private */
    public boolean destroyWhenDone;
    private boolean doNotRemoveInvalidOnFrameReady;
    private final Rect dstRect;
    private boolean forceFrameRedraw;
    /* access modifiers changed from: private */
    public int height;
    private boolean invalidateOnProgressSet;
    /* access modifiers changed from: private */
    public int isDice;
    private boolean isInvalid;
    /* access modifiers changed from: private */
    public volatile boolean isRecycled;
    /* access modifiers changed from: private */
    public volatile boolean isRunning;
    private long lastFrameTime;
    private Runnable loadFrameRunnable;
    /* access modifiers changed from: private */
    public Runnable loadFrameTask;
    /* access modifiers changed from: private */
    public final int[] metaData;
    /* access modifiers changed from: private */
    public volatile long nativePtr;
    private HashMap<String, Integer> newColorUpdates;
    private int[] newReplaceColors;
    /* access modifiers changed from: private */
    public volatile boolean nextFrameIsLast;
    /* access modifiers changed from: private */
    public volatile Bitmap nextRenderingBitmap;
    private ArrayList<WeakReference<View>> parentViews;
    /* access modifiers changed from: private */
    public volatile HashMap<String, Integer> pendingColorUpdates;
    /* access modifiers changed from: private */
    public int[] pendingReplaceColors;
    private volatile Bitmap renderingBitmap;
    private float scaleX;
    private float scaleY;
    /* access modifiers changed from: private */
    public volatile long secondNativePtr;
    /* access modifiers changed from: private */
    public volatile boolean setLastFrame;
    /* access modifiers changed from: private */
    public boolean shouldLimitFps;
    /* access modifiers changed from: private */
    public boolean singleFrameDecoded;
    private int timeBetweenFrames;
    /* access modifiers changed from: private */
    public Runnable uiRunnable;
    /* access modifiers changed from: private */
    public Runnable uiRunnableCacheFinished;
    /* access modifiers changed from: private */
    public Runnable uiRunnableGenerateCache;
    /* access modifiers changed from: private */
    public Runnable uiRunnableNoFrame;
    private HashMap<Integer, Integer> vibrationPattern;
    /* access modifiers changed from: private */
    public int width;

    private static native long create(String str, int i, int i2, int[] iArr, boolean z, int[] iArr2, boolean z2);

    /* access modifiers changed from: private */
    public static native void createCache(long j, int i, int i2);

    private static native long createWithJson(String str, String str2, int[] iArr, int[] iArr2);

    private static native void destroy(long j);

    /* access modifiers changed from: private */
    public static native int getFrame(long j, int i, Bitmap bitmap, int i2, int i3, int i4);

    /* access modifiers changed from: private */
    public static native void replaceColors(long j, int[] iArr);

    /* access modifiers changed from: private */
    public static native void setLayerColor(long j, String str, int i);

    public int getOpacity() {
        return -2;
    }

    static /* synthetic */ int access$3108(RLottieDrawable rLottieDrawable) {
        int i = rLottieDrawable.autoRepeatPlayCount;
        rLottieDrawable.autoRepeatPlayCount = i + 1;
        return i;
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

    /* access modifiers changed from: private */
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

    public RLottieDrawable(File file, int i, int i2, boolean z, boolean z2, int[] iArr) {
        this.metaData = new int[3];
        this.newColorUpdates = new HashMap<>();
        this.pendingColorUpdates = new HashMap<>();
        this.autoRepeat = 1;
        this.scaleX = 1.0f;
        this.scaleY = 1.0f;
        this.dstRect = new Rect();
        this.parentViews = new ArrayList<>();
        this.uiRunnableNoFrame = new Runnable() {
            public void run() {
                Runnable unused = RLottieDrawable.this.loadFrameTask = null;
                RLottieDrawable.this.decodeFrameFinishedInternal();
            }
        };
        this.uiRunnableCacheFinished = new Runnable() {
            public void run() {
                Runnable unused = RLottieDrawable.this.cacheGenerateTask = null;
                RLottieDrawable.this.decodeFrameFinishedInternal();
            }
        };
        this.uiRunnable = new Runnable() {
            public void run() {
                boolean unused = RLottieDrawable.this.singleFrameDecoded = true;
                RLottieDrawable.this.invalidateInternal();
                RLottieDrawable.this.decodeFrameFinishedInternal();
            }
        };
        new Runnable() {
            public void run() {
                boolean unused = RLottieDrawable.this.singleFrameDecoded = true;
                boolean unused2 = RLottieDrawable.this.isRunning = false;
                RLottieDrawable.this.invalidateInternal();
                RLottieDrawable.this.decodeFrameFinishedInternal();
            }
        };
        this.uiRunnableGenerateCache = new Runnable() {
            public void run() {
                if (!RLottieDrawable.this.isRecycled && !RLottieDrawable.this.destroyWhenDone && RLottieDrawable.this.nativePtr != 0) {
                    ThreadPoolExecutor access$900 = RLottieDrawable.lottieCacheGenerateQueue;
                    RLottieDrawable rLottieDrawable = RLottieDrawable.this;
                    $$Lambda$RLottieDrawable$5$Yb_jU2tqNZSr5pLiqbJfFDeXef4 r2 = new Runnable() {
                        public final void run() {
                            RLottieDrawable.AnonymousClass5.this.lambda$run$0$RLottieDrawable$5();
                        }
                    };
                    Runnable unused = rLottieDrawable.cacheGenerateTask = r2;
                    access$900.execute(r2);
                }
                RLottieDrawable.this.decodeFrameFinishedInternal();
            }

            public /* synthetic */ void lambda$run$0$RLottieDrawable$5() {
                if (RLottieDrawable.this.cacheGenerateTask != null) {
                    RLottieDrawable.createCache(RLottieDrawable.this.nativePtr, RLottieDrawable.this.width, RLottieDrawable.this.height);
                    RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnableCacheFinished);
                }
            }
        };
        this.loadFrameRunnable = new Runnable() {
            public void run() {
                long access$800;
                if (!RLottieDrawable.this.isRecycled) {
                    if (RLottieDrawable.this.nativePtr == 0) {
                        RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnableNoFrame);
                        return;
                    }
                    if (RLottieDrawable.this.backgroundBitmap == null) {
                        try {
                            Bitmap unused = RLottieDrawable.this.backgroundBitmap = Bitmap.createBitmap(RLottieDrawable.this.width, RLottieDrawable.this.height, Bitmap.Config.ARGB_8888);
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
                        } catch (Exception unused2) {
                        }
                        if (RLottieDrawable.this.pendingReplaceColors != null) {
                            RLottieDrawable.replaceColors(RLottieDrawable.this.nativePtr, RLottieDrawable.this.pendingReplaceColors);
                            int[] unused3 = RLottieDrawable.this.pendingReplaceColors = null;
                        }
                        try {
                            if (RLottieDrawable.this.isDice == 1) {
                                access$800 = RLottieDrawable.this.nativePtr;
                            } else if (RLottieDrawable.this.isDice == 2) {
                                access$800 = RLottieDrawable.this.secondNativePtr;
                                if (RLottieDrawable.this.setLastFrame) {
                                    int unused4 = RLottieDrawable.this.currentFrame = 179;
                                }
                            } else {
                                access$800 = RLottieDrawable.this.nativePtr;
                            }
                            if (RLottieDrawable.getFrame(access$800, RLottieDrawable.this.currentFrame, RLottieDrawable.this.backgroundBitmap, RLottieDrawable.this.width, RLottieDrawable.this.height, RLottieDrawable.this.backgroundBitmap.getRowBytes()) == -1) {
                                RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnableNoFrame);
                                return;
                            }
                            if (RLottieDrawable.this.metaData[2] != 0) {
                                RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnableGenerateCache);
                                RLottieDrawable.this.metaData[2] = 0;
                            }
                            Bitmap unused5 = RLottieDrawable.this.nextRenderingBitmap = RLottieDrawable.this.backgroundBitmap;
                            int i = RLottieDrawable.this.shouldLimitFps ? 2 : 1;
                            if (RLottieDrawable.this.isDice == 1) {
                                if (RLottieDrawable.this.currentFrame + i < 60) {
                                    int unused6 = RLottieDrawable.this.currentFrame = RLottieDrawable.this.currentFrame + i;
                                } else {
                                    int unused7 = RLottieDrawable.this.currentFrame = 0;
                                    boolean unused8 = RLottieDrawable.this.nextFrameIsLast = false;
                                    if (RLottieDrawable.this.secondNativePtr != 0) {
                                        int unused9 = RLottieDrawable.this.isDice = 2;
                                    }
                                }
                            } else if (RLottieDrawable.this.isDice == 2) {
                                if (RLottieDrawable.this.currentFrame + i < 180) {
                                    int unused10 = RLottieDrawable.this.currentFrame = RLottieDrawable.this.currentFrame + i;
                                } else {
                                    boolean unused11 = RLottieDrawable.this.nextFrameIsLast = true;
                                    RLottieDrawable.access$3108(RLottieDrawable.this);
                                }
                            } else if (RLottieDrawable.this.currentFrame + i < RLottieDrawable.this.metaData[0]) {
                                if (RLottieDrawable.this.autoRepeat == 3) {
                                    boolean unused12 = RLottieDrawable.this.nextFrameIsLast = true;
                                    RLottieDrawable.access$3108(RLottieDrawable.this);
                                } else {
                                    int unused13 = RLottieDrawable.this.currentFrame = RLottieDrawable.this.currentFrame + i;
                                    boolean unused14 = RLottieDrawable.this.nextFrameIsLast = false;
                                }
                            } else if (RLottieDrawable.this.autoRepeat == 1) {
                                int unused15 = RLottieDrawable.this.currentFrame = 0;
                                boolean unused16 = RLottieDrawable.this.nextFrameIsLast = false;
                            } else if (RLottieDrawable.this.autoRepeat == 2) {
                                int unused17 = RLottieDrawable.this.currentFrame = 0;
                                boolean unused18 = RLottieDrawable.this.nextFrameIsLast = true;
                                RLottieDrawable.access$3108(RLottieDrawable.this);
                            } else {
                                boolean unused19 = RLottieDrawable.this.nextFrameIsLast = true;
                            }
                        } catch (Exception e) {
                            FileLog.e((Throwable) e);
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
        this.nativePtr = create(file.getAbsolutePath(), i, i2, this.metaData, z, iArr, this.shouldLimitFps);
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
        this(i, str, i2, i3, true, (int[]) null);
    }

    public RLottieDrawable(int i, int i2, int i3) {
        this.metaData = new int[3];
        this.newColorUpdates = new HashMap<>();
        this.pendingColorUpdates = new HashMap<>();
        this.autoRepeat = 1;
        this.scaleX = 1.0f;
        this.scaleY = 1.0f;
        this.dstRect = new Rect();
        this.parentViews = new ArrayList<>();
        this.uiRunnableNoFrame = new Runnable() {
            public void run() {
                Runnable unused = RLottieDrawable.this.loadFrameTask = null;
                RLottieDrawable.this.decodeFrameFinishedInternal();
            }
        };
        this.uiRunnableCacheFinished = new Runnable() {
            public void run() {
                Runnable unused = RLottieDrawable.this.cacheGenerateTask = null;
                RLottieDrawable.this.decodeFrameFinishedInternal();
            }
        };
        this.uiRunnable = new Runnable() {
            public void run() {
                boolean unused = RLottieDrawable.this.singleFrameDecoded = true;
                RLottieDrawable.this.invalidateInternal();
                RLottieDrawable.this.decodeFrameFinishedInternal();
            }
        };
        new Runnable() {
            public void run() {
                boolean unused = RLottieDrawable.this.singleFrameDecoded = true;
                boolean unused2 = RLottieDrawable.this.isRunning = false;
                RLottieDrawable.this.invalidateInternal();
                RLottieDrawable.this.decodeFrameFinishedInternal();
            }
        };
        this.uiRunnableGenerateCache = new Runnable() {
            public void run() {
                if (!RLottieDrawable.this.isRecycled && !RLottieDrawable.this.destroyWhenDone && RLottieDrawable.this.nativePtr != 0) {
                    ThreadPoolExecutor access$900 = RLottieDrawable.lottieCacheGenerateQueue;
                    RLottieDrawable rLottieDrawable = RLottieDrawable.this;
                    $$Lambda$RLottieDrawable$5$Yb_jU2tqNZSr5pLiqbJfFDeXef4 r2 = new Runnable() {
                        public final void run() {
                            RLottieDrawable.AnonymousClass5.this.lambda$run$0$RLottieDrawable$5();
                        }
                    };
                    Runnable unused = rLottieDrawable.cacheGenerateTask = r2;
                    access$900.execute(r2);
                }
                RLottieDrawable.this.decodeFrameFinishedInternal();
            }

            public /* synthetic */ void lambda$run$0$RLottieDrawable$5() {
                if (RLottieDrawable.this.cacheGenerateTask != null) {
                    RLottieDrawable.createCache(RLottieDrawable.this.nativePtr, RLottieDrawable.this.width, RLottieDrawable.this.height);
                    RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnableCacheFinished);
                }
            }
        };
        this.loadFrameRunnable = new Runnable() {
            public void run() {
                long access$800;
                if (!RLottieDrawable.this.isRecycled) {
                    if (RLottieDrawable.this.nativePtr == 0) {
                        RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnableNoFrame);
                        return;
                    }
                    if (RLottieDrawable.this.backgroundBitmap == null) {
                        try {
                            Bitmap unused = RLottieDrawable.this.backgroundBitmap = Bitmap.createBitmap(RLottieDrawable.this.width, RLottieDrawable.this.height, Bitmap.Config.ARGB_8888);
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
                        } catch (Exception unused2) {
                        }
                        if (RLottieDrawable.this.pendingReplaceColors != null) {
                            RLottieDrawable.replaceColors(RLottieDrawable.this.nativePtr, RLottieDrawable.this.pendingReplaceColors);
                            int[] unused3 = RLottieDrawable.this.pendingReplaceColors = null;
                        }
                        try {
                            if (RLottieDrawable.this.isDice == 1) {
                                access$800 = RLottieDrawable.this.nativePtr;
                            } else if (RLottieDrawable.this.isDice == 2) {
                                access$800 = RLottieDrawable.this.secondNativePtr;
                                if (RLottieDrawable.this.setLastFrame) {
                                    int unused4 = RLottieDrawable.this.currentFrame = 179;
                                }
                            } else {
                                access$800 = RLottieDrawable.this.nativePtr;
                            }
                            if (RLottieDrawable.getFrame(access$800, RLottieDrawable.this.currentFrame, RLottieDrawable.this.backgroundBitmap, RLottieDrawable.this.width, RLottieDrawable.this.height, RLottieDrawable.this.backgroundBitmap.getRowBytes()) == -1) {
                                RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnableNoFrame);
                                return;
                            }
                            if (RLottieDrawable.this.metaData[2] != 0) {
                                RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnableGenerateCache);
                                RLottieDrawable.this.metaData[2] = 0;
                            }
                            Bitmap unused5 = RLottieDrawable.this.nextRenderingBitmap = RLottieDrawable.this.backgroundBitmap;
                            int i = RLottieDrawable.this.shouldLimitFps ? 2 : 1;
                            if (RLottieDrawable.this.isDice == 1) {
                                if (RLottieDrawable.this.currentFrame + i < 60) {
                                    int unused6 = RLottieDrawable.this.currentFrame = RLottieDrawable.this.currentFrame + i;
                                } else {
                                    int unused7 = RLottieDrawable.this.currentFrame = 0;
                                    boolean unused8 = RLottieDrawable.this.nextFrameIsLast = false;
                                    if (RLottieDrawable.this.secondNativePtr != 0) {
                                        int unused9 = RLottieDrawable.this.isDice = 2;
                                    }
                                }
                            } else if (RLottieDrawable.this.isDice == 2) {
                                if (RLottieDrawable.this.currentFrame + i < 180) {
                                    int unused10 = RLottieDrawable.this.currentFrame = RLottieDrawable.this.currentFrame + i;
                                } else {
                                    boolean unused11 = RLottieDrawable.this.nextFrameIsLast = true;
                                    RLottieDrawable.access$3108(RLottieDrawable.this);
                                }
                            } else if (RLottieDrawable.this.currentFrame + i < RLottieDrawable.this.metaData[0]) {
                                if (RLottieDrawable.this.autoRepeat == 3) {
                                    boolean unused12 = RLottieDrawable.this.nextFrameIsLast = true;
                                    RLottieDrawable.access$3108(RLottieDrawable.this);
                                } else {
                                    int unused13 = RLottieDrawable.this.currentFrame = RLottieDrawable.this.currentFrame + i;
                                    boolean unused14 = RLottieDrawable.this.nextFrameIsLast = false;
                                }
                            } else if (RLottieDrawable.this.autoRepeat == 1) {
                                int unused15 = RLottieDrawable.this.currentFrame = 0;
                                boolean unused16 = RLottieDrawable.this.nextFrameIsLast = false;
                            } else if (RLottieDrawable.this.autoRepeat == 2) {
                                int unused17 = RLottieDrawable.this.currentFrame = 0;
                                boolean unused18 = RLottieDrawable.this.nextFrameIsLast = true;
                                RLottieDrawable.access$3108(RLottieDrawable.this);
                            } else {
                                boolean unused19 = RLottieDrawable.this.nextFrameIsLast = true;
                            }
                        } catch (Exception e) {
                            FileLog.e((Throwable) e);
                        }
                    }
                    RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnable);
                }
            }
        };
        this.width = i2;
        this.height = i3;
        this.isDice = 1;
        String readRes = readRes(i);
        if (!TextUtils.isEmpty(readRes)) {
            getPaint().setFlags(2);
            this.nativePtr = createWithJson(readRes, "dice", this.metaData, (int[]) null);
            this.timeBetweenFrames = Math.max(16, (int) (1000.0f / ((float) this.metaData[1])));
        }
    }

    public void setDiceNumber(int i, boolean z) {
        int i2;
        switch (i) {
            case 1:
                i2 = NUM;
                break;
            case 2:
                i2 = NUM;
                break;
            case 3:
                i2 = NUM;
                break;
            case 4:
                i2 = NUM;
                break;
            case 5:
                i2 = NUM;
                break;
            case 6:
                i2 = NUM;
                break;
            default:
                return;
        }
        String readRes = readRes(i2);
        if (!TextUtils.isEmpty(readRes)) {
            if (z && this.nextRenderingBitmap == null && this.renderingBitmap == null && this.loadFrameTask == null) {
                this.isDice = 2;
                this.setLastFrame = true;
            }
            this.secondNativePtr = createWithJson(readRes, "dice", this.metaData, (int[]) null);
        }
    }

    public RLottieDrawable(int i, String str, int i2, int i3, boolean z, int[] iArr) {
        this.metaData = new int[3];
        this.newColorUpdates = new HashMap<>();
        this.pendingColorUpdates = new HashMap<>();
        this.autoRepeat = 1;
        this.scaleX = 1.0f;
        this.scaleY = 1.0f;
        this.dstRect = new Rect();
        this.parentViews = new ArrayList<>();
        this.uiRunnableNoFrame = new Runnable() {
            public void run() {
                Runnable unused = RLottieDrawable.this.loadFrameTask = null;
                RLottieDrawable.this.decodeFrameFinishedInternal();
            }
        };
        this.uiRunnableCacheFinished = new Runnable() {
            public void run() {
                Runnable unused = RLottieDrawable.this.cacheGenerateTask = null;
                RLottieDrawable.this.decodeFrameFinishedInternal();
            }
        };
        this.uiRunnable = new Runnable() {
            public void run() {
                boolean unused = RLottieDrawable.this.singleFrameDecoded = true;
                RLottieDrawable.this.invalidateInternal();
                RLottieDrawable.this.decodeFrameFinishedInternal();
            }
        };
        new Runnable() {
            public void run() {
                boolean unused = RLottieDrawable.this.singleFrameDecoded = true;
                boolean unused2 = RLottieDrawable.this.isRunning = false;
                RLottieDrawable.this.invalidateInternal();
                RLottieDrawable.this.decodeFrameFinishedInternal();
            }
        };
        this.uiRunnableGenerateCache = new Runnable() {
            public void run() {
                if (!RLottieDrawable.this.isRecycled && !RLottieDrawable.this.destroyWhenDone && RLottieDrawable.this.nativePtr != 0) {
                    ThreadPoolExecutor access$900 = RLottieDrawable.lottieCacheGenerateQueue;
                    RLottieDrawable rLottieDrawable = RLottieDrawable.this;
                    $$Lambda$RLottieDrawable$5$Yb_jU2tqNZSr5pLiqbJfFDeXef4 r2 = new Runnable() {
                        public final void run() {
                            RLottieDrawable.AnonymousClass5.this.lambda$run$0$RLottieDrawable$5();
                        }
                    };
                    Runnable unused = rLottieDrawable.cacheGenerateTask = r2;
                    access$900.execute(r2);
                }
                RLottieDrawable.this.decodeFrameFinishedInternal();
            }

            public /* synthetic */ void lambda$run$0$RLottieDrawable$5() {
                if (RLottieDrawable.this.cacheGenerateTask != null) {
                    RLottieDrawable.createCache(RLottieDrawable.this.nativePtr, RLottieDrawable.this.width, RLottieDrawable.this.height);
                    RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnableCacheFinished);
                }
            }
        };
        this.loadFrameRunnable = new Runnable() {
            public void run() {
                long access$800;
                if (!RLottieDrawable.this.isRecycled) {
                    if (RLottieDrawable.this.nativePtr == 0) {
                        RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnableNoFrame);
                        return;
                    }
                    if (RLottieDrawable.this.backgroundBitmap == null) {
                        try {
                            Bitmap unused = RLottieDrawable.this.backgroundBitmap = Bitmap.createBitmap(RLottieDrawable.this.width, RLottieDrawable.this.height, Bitmap.Config.ARGB_8888);
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
                        } catch (Exception unused2) {
                        }
                        if (RLottieDrawable.this.pendingReplaceColors != null) {
                            RLottieDrawable.replaceColors(RLottieDrawable.this.nativePtr, RLottieDrawable.this.pendingReplaceColors);
                            int[] unused3 = RLottieDrawable.this.pendingReplaceColors = null;
                        }
                        try {
                            if (RLottieDrawable.this.isDice == 1) {
                                access$800 = RLottieDrawable.this.nativePtr;
                            } else if (RLottieDrawable.this.isDice == 2) {
                                access$800 = RLottieDrawable.this.secondNativePtr;
                                if (RLottieDrawable.this.setLastFrame) {
                                    int unused4 = RLottieDrawable.this.currentFrame = 179;
                                }
                            } else {
                                access$800 = RLottieDrawable.this.nativePtr;
                            }
                            if (RLottieDrawable.getFrame(access$800, RLottieDrawable.this.currentFrame, RLottieDrawable.this.backgroundBitmap, RLottieDrawable.this.width, RLottieDrawable.this.height, RLottieDrawable.this.backgroundBitmap.getRowBytes()) == -1) {
                                RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnableNoFrame);
                                return;
                            }
                            if (RLottieDrawable.this.metaData[2] != 0) {
                                RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnableGenerateCache);
                                RLottieDrawable.this.metaData[2] = 0;
                            }
                            Bitmap unused5 = RLottieDrawable.this.nextRenderingBitmap = RLottieDrawable.this.backgroundBitmap;
                            int i = RLottieDrawable.this.shouldLimitFps ? 2 : 1;
                            if (RLottieDrawable.this.isDice == 1) {
                                if (RLottieDrawable.this.currentFrame + i < 60) {
                                    int unused6 = RLottieDrawable.this.currentFrame = RLottieDrawable.this.currentFrame + i;
                                } else {
                                    int unused7 = RLottieDrawable.this.currentFrame = 0;
                                    boolean unused8 = RLottieDrawable.this.nextFrameIsLast = false;
                                    if (RLottieDrawable.this.secondNativePtr != 0) {
                                        int unused9 = RLottieDrawable.this.isDice = 2;
                                    }
                                }
                            } else if (RLottieDrawable.this.isDice == 2) {
                                if (RLottieDrawable.this.currentFrame + i < 180) {
                                    int unused10 = RLottieDrawable.this.currentFrame = RLottieDrawable.this.currentFrame + i;
                                } else {
                                    boolean unused11 = RLottieDrawable.this.nextFrameIsLast = true;
                                    RLottieDrawable.access$3108(RLottieDrawable.this);
                                }
                            } else if (RLottieDrawable.this.currentFrame + i < RLottieDrawable.this.metaData[0]) {
                                if (RLottieDrawable.this.autoRepeat == 3) {
                                    boolean unused12 = RLottieDrawable.this.nextFrameIsLast = true;
                                    RLottieDrawable.access$3108(RLottieDrawable.this);
                                } else {
                                    int unused13 = RLottieDrawable.this.currentFrame = RLottieDrawable.this.currentFrame + i;
                                    boolean unused14 = RLottieDrawable.this.nextFrameIsLast = false;
                                }
                            } else if (RLottieDrawable.this.autoRepeat == 1) {
                                int unused15 = RLottieDrawable.this.currentFrame = 0;
                                boolean unused16 = RLottieDrawable.this.nextFrameIsLast = false;
                            } else if (RLottieDrawable.this.autoRepeat == 2) {
                                int unused17 = RLottieDrawable.this.currentFrame = 0;
                                boolean unused18 = RLottieDrawable.this.nextFrameIsLast = true;
                                RLottieDrawable.access$3108(RLottieDrawable.this);
                            } else {
                                boolean unused19 = RLottieDrawable.this.nextFrameIsLast = true;
                            }
                        } catch (Exception e) {
                            FileLog.e((Throwable) e);
                        }
                    }
                    RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnable);
                }
            }
        };
        this.width = i2;
        this.height = i3;
        this.autoRepeat = 0;
        String readRes = readRes(i);
        if (!TextUtils.isEmpty(readRes)) {
            getPaint().setFlags(2);
            this.nativePtr = createWithJson(readRes, str, this.metaData, iArr);
            this.timeBetweenFrames = Math.max(16, (int) (1000.0f / ((float) this.metaData[1])));
            if (z) {
                setAllowDecodeSingleFrame(true);
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:26:0x0060, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x0061, code lost:
        if (r8 != null) goto L_0x0063;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:?, code lost:
        r8.close();
     */
    /* JADX WARNING: Missing exception handler attribute for start block: B:30:0x0066 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.lang.String readRes(int r8) {
        /*
            r7 = this;
            java.lang.ThreadLocal<byte[]> r0 = readBufferLocal
            java.lang.Object r0 = r0.get()
            byte[] r0 = (byte[]) r0
            if (r0 != 0) goto L_0x0013
            r0 = 65536(0x10000, float:9.18355E-41)
            byte[] r0 = new byte[r0]
            java.lang.ThreadLocal<byte[]> r1 = readBufferLocal
            r1.set(r0)
        L_0x0013:
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x0067 }
            android.content.res.Resources r1 = r1.getResources()     // Catch:{ all -> 0x0067 }
            java.io.InputStream r8 = r1.openRawResource(r8)     // Catch:{ all -> 0x0067 }
            java.lang.ThreadLocal<byte[]> r1 = bufferLocal     // Catch:{ all -> 0x005e }
            java.lang.Object r1 = r1.get()     // Catch:{ all -> 0x005e }
            byte[] r1 = (byte[]) r1     // Catch:{ all -> 0x005e }
            r2 = 0
            if (r1 != 0) goto L_0x0031
            r1 = 4096(0x1000, float:5.74E-42)
            byte[] r1 = new byte[r1]     // Catch:{ all -> 0x005e }
            java.lang.ThreadLocal<byte[]> r3 = bufferLocal     // Catch:{ all -> 0x005e }
            r3.set(r1)     // Catch:{ all -> 0x005e }
        L_0x0031:
            r3 = 0
        L_0x0032:
            int r4 = r1.length     // Catch:{ all -> 0x005e }
            int r4 = r8.read(r1, r2, r4)     // Catch:{ all -> 0x005e }
            if (r4 < 0) goto L_0x0053
            int r5 = r0.length     // Catch:{ all -> 0x005e }
            int r6 = r3 + r4
            if (r5 >= r6) goto L_0x004c
            int r5 = r0.length     // Catch:{ all -> 0x005e }
            int r5 = r5 * 2
            byte[] r5 = new byte[r5]     // Catch:{ all -> 0x005e }
            java.lang.System.arraycopy(r0, r2, r5, r2, r3)     // Catch:{ all -> 0x005e }
            java.lang.ThreadLocal<byte[]> r0 = readBufferLocal     // Catch:{ all -> 0x005e }
            r0.set(r5)     // Catch:{ all -> 0x005e }
            r0 = r5
        L_0x004c:
            if (r4 <= 0) goto L_0x0032
            java.lang.System.arraycopy(r1, r2, r0, r3, r4)     // Catch:{ all -> 0x005e }
            r3 = r6
            goto L_0x0032
        L_0x0053:
            if (r8 == 0) goto L_0x0058
            r8.close()     // Catch:{ all -> 0x0067 }
        L_0x0058:
            java.lang.String r8 = new java.lang.String
            r8.<init>(r0, r2, r3)
            return r8
        L_0x005e:
            r0 = move-exception
            throw r0     // Catch:{ all -> 0x0060 }
        L_0x0060:
            r0 = move-exception
            if (r8 == 0) goto L_0x0066
            r8.close()     // Catch:{ all -> 0x0066 }
        L_0x0066:
            throw r0     // Catch:{ all -> 0x0067 }
        L_0x0067:
            r8 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r8)
            r8 = 0
            return r8
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.RLottieDrawable.readRes(int):java.lang.String");
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

    private boolean hasParentView() {
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

    /* access modifiers changed from: private */
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
        if (this.loadFrameTask == null && this.cacheGenerateTask == null) {
            if (this.nativePtr != 0) {
                destroy(this.nativePtr);
                this.nativePtr = 0;
            }
            if (this.secondNativePtr != 0) {
                destroy(this.secondNativePtr);
                this.secondNativePtr = 0;
            }
            recycleResources();
            return;
        }
        this.destroyWhenDone = true;
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

    private boolean scheduleNextGetFrame() {
        if (this.loadFrameTask != null || this.nextRenderingBitmap != null || this.nativePtr == 0 || this.destroyWhenDone) {
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
        ExecutorService executorService = loadFrameRunnableQueue;
        Runnable runnable = this.loadFrameRunnable;
        this.loadFrameTask = runnable;
        executorService.execute(runnable);
        return true;
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
        this.singleFrameDecoded = false;
        if (this.invalidateOnProgressSet) {
            this.isInvalid = true;
            if (this.loadFrameTask != null) {
                this.doNotRemoveInvalidOnFrameReady = true;
            }
        }
        if (!scheduleNextGetFrame()) {
            this.forceFrameRedraw = true;
        }
        invalidateSelf();
    }

    public void setCurrentParentView(View view) {
        this.currentParentView = view;
    }

    private boolean isCurrentParentViewMaster() {
        if (getCallback() != null) {
            return true;
        }
        int size = this.parentViews.size();
        while (size > 0) {
            if (this.parentViews.get(0).get() == null) {
                this.parentViews.remove(0);
                size--;
            } else if (this.parentViews.get(0).get() == this.currentParentView) {
                return true;
            } else {
                return false;
            }
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
        this.backgroundBitmap = this.renderingBitmap;
        this.renderingBitmap = this.nextRenderingBitmap;
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
        this.nextRenderingBitmap = null;
        if (AndroidUtilities.screenRefreshRate <= 60.0f) {
            this.lastFrameTime = j;
        } else {
            this.lastFrameTime = j - Math.min(16, j2 - j3);
        }
        if (z && this.forceFrameRedraw) {
            this.singleFrameDecoded = false;
            this.forceFrameRedraw = false;
        }
        scheduleNextGetFrame();
    }

    public void draw(Canvas canvas) {
        int i;
        Integer num;
        if (this.nativePtr != 0 && !this.destroyWhenDone) {
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
                } else if (this.nextRenderingBitmap != null && ((this.renderingBitmap == null || abs >= ((long) i)) && isCurrentParentViewMaster())) {
                    HashMap<Integer, Integer> hashMap = this.vibrationPattern;
                    if (!(hashMap == null || this.currentParentView == null || (num = hashMap.get(Integer.valueOf(this.currentFrame - 1))) == null)) {
                        this.currentParentView.performHapticFeedback(num.intValue() == 1 ? 0 : 3, 2);
                    }
                    setCurrentFrame(elapsedRealtime, abs, (long) i, false);
                }
            } else if ((this.forceFrameRedraw || (this.decodeSingleFrame && abs >= ((long) i))) && this.nextRenderingBitmap != null) {
                setCurrentFrame(elapsedRealtime, abs, (long) i, true);
            }
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
