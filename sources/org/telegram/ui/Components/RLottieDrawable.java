package org.telegram.ui.Components;

import android.graphics.Bitmap;
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
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.ui.Components.RLottieDrawable;

public class RLottieDrawable extends BitmapDrawable implements Animatable {
    private static byte[] buffer = new byte[4096];
    private static ExecutorService loadFrameRunnableQueue = Executors.newCachedThreadPool();
    /* access modifiers changed from: private */
    public static ThreadPoolExecutor lottieCacheGenerateQueue;
    private static byte[] readBuffer = new byte[65536];
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
    private final Rect dstRect;
    private boolean forceFrameRedraw;
    /* access modifiers changed from: private */
    public int height;
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
    private Runnable uiRunnableLastFrame;
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

    static /* synthetic */ int access$2908(RLottieDrawable rLottieDrawable) {
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
        this(file, i, i2, z, z2, (int[]) null);
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
        this.uiRunnableLastFrame = new Runnable() {
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
                    RLottieDrawable.lottieCacheGenerateQueue.execute(RLottieDrawable.this.cacheGenerateTask = new Runnable() {
                        public final void run() {
                            RLottieDrawable.AnonymousClass5.this.lambda$run$0$RLottieDrawable$5();
                        }
                    });
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
                            if (RLottieDrawable.getFrame(RLottieDrawable.this.nativePtr, RLottieDrawable.this.currentFrame, RLottieDrawable.this.backgroundBitmap, RLottieDrawable.this.width, RLottieDrawable.this.height, RLottieDrawable.this.backgroundBitmap.getRowBytes()) == -1) {
                                RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnableNoFrame);
                                return;
                            }
                            if (RLottieDrawable.this.metaData[2] != 0) {
                                RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnableGenerateCache);
                                RLottieDrawable.this.metaData[2] = 0;
                            }
                            Bitmap unused4 = RLottieDrawable.this.nextRenderingBitmap = RLottieDrawable.this.backgroundBitmap;
                            int i = RLottieDrawable.this.shouldLimitFps ? 2 : 1;
                            if (RLottieDrawable.this.currentFrame + i < RLottieDrawable.this.metaData[0]) {
                                if (RLottieDrawable.this.autoRepeat == 3) {
                                    boolean unused5 = RLottieDrawable.this.nextFrameIsLast = true;
                                    RLottieDrawable.access$2908(RLottieDrawable.this);
                                } else {
                                    int unused6 = RLottieDrawable.this.currentFrame = RLottieDrawable.this.currentFrame + i;
                                    boolean unused7 = RLottieDrawable.this.nextFrameIsLast = false;
                                }
                            } else if (RLottieDrawable.this.autoRepeat == 1) {
                                int unused8 = RLottieDrawable.this.currentFrame = 0;
                                boolean unused9 = RLottieDrawable.this.nextFrameIsLast = false;
                            } else if (RLottieDrawable.this.autoRepeat == 2) {
                                int unused10 = RLottieDrawable.this.currentFrame = 0;
                                boolean unused11 = RLottieDrawable.this.nextFrameIsLast = true;
                                RLottieDrawable.access$2908(RLottieDrawable.this);
                            } else {
                                boolean unused12 = RLottieDrawable.this.nextFrameIsLast = true;
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
        this.uiRunnableLastFrame = new Runnable() {
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
                    RLottieDrawable.lottieCacheGenerateQueue.execute(RLottieDrawable.this.cacheGenerateTask = new Runnable() {
                        public final void run() {
                            RLottieDrawable.AnonymousClass5.this.lambda$run$0$RLottieDrawable$5();
                        }
                    });
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
                            if (RLottieDrawable.getFrame(RLottieDrawable.this.nativePtr, RLottieDrawable.this.currentFrame, RLottieDrawable.this.backgroundBitmap, RLottieDrawable.this.width, RLottieDrawable.this.height, RLottieDrawable.this.backgroundBitmap.getRowBytes()) == -1) {
                                RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnableNoFrame);
                                return;
                            }
                            if (RLottieDrawable.this.metaData[2] != 0) {
                                RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnableGenerateCache);
                                RLottieDrawable.this.metaData[2] = 0;
                            }
                            Bitmap unused4 = RLottieDrawable.this.nextRenderingBitmap = RLottieDrawable.this.backgroundBitmap;
                            int i = RLottieDrawable.this.shouldLimitFps ? 2 : 1;
                            if (RLottieDrawable.this.currentFrame + i < RLottieDrawable.this.metaData[0]) {
                                if (RLottieDrawable.this.autoRepeat == 3) {
                                    boolean unused5 = RLottieDrawable.this.nextFrameIsLast = true;
                                    RLottieDrawable.access$2908(RLottieDrawable.this);
                                } else {
                                    int unused6 = RLottieDrawable.this.currentFrame = RLottieDrawable.this.currentFrame + i;
                                    boolean unused7 = RLottieDrawable.this.nextFrameIsLast = false;
                                }
                            } else if (RLottieDrawable.this.autoRepeat == 1) {
                                int unused8 = RLottieDrawable.this.currentFrame = 0;
                                boolean unused9 = RLottieDrawable.this.nextFrameIsLast = false;
                            } else if (RLottieDrawable.this.autoRepeat == 2) {
                                int unused10 = RLottieDrawable.this.currentFrame = 0;
                                boolean unused11 = RLottieDrawable.this.nextFrameIsLast = true;
                                RLottieDrawable.access$2908(RLottieDrawable.this);
                            } else {
                                boolean unused12 = RLottieDrawable.this.nextFrameIsLast = true;
                            }
                        } catch (Exception e) {
                            FileLog.e((Throwable) e);
                        }
                    }
                    RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnable);
                }
            }
        };
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
            this.nativePtr = createWithJson(str2, str, this.metaData, iArr);
            this.timeBetweenFrames = Math.max(16, (int) (1000.0f / ((float) this.metaData[1])));
            this.autoRepeat = 0;
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

    public void draw(Canvas canvas) {
        int i;
        Integer num;
        if (this.nativePtr != 0 && !this.destroyWhenDone) {
            long uptimeMillis = SystemClock.uptimeMillis();
            long abs = Math.abs(uptimeMillis - this.lastFrameTime);
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
                    this.backgroundBitmap = this.renderingBitmap;
                    this.renderingBitmap = this.nextRenderingBitmap;
                    if (this.nextFrameIsLast) {
                        stop();
                    }
                    this.loadFrameTask = null;
                    this.singleFrameDecoded = true;
                    this.nextRenderingBitmap = null;
                    if (AndroidUtilities.screenRefreshRate <= 60.0f) {
                        this.lastFrameTime = uptimeMillis;
                    } else {
                        this.lastFrameTime = uptimeMillis - Math.min(16, abs - ((long) i));
                    }
                    scheduleNextGetFrame();
                }
            } else if ((this.forceFrameRedraw || (this.decodeSingleFrame && abs >= ((long) i))) && this.nextRenderingBitmap != null) {
                this.backgroundBitmap = this.renderingBitmap;
                this.renderingBitmap = this.nextRenderingBitmap;
                this.loadFrameTask = null;
                this.singleFrameDecoded = true;
                this.nextRenderingBitmap = null;
                if (AndroidUtilities.screenRefreshRate <= 60.0f) {
                    this.lastFrameTime = uptimeMillis;
                } else {
                    this.lastFrameTime = uptimeMillis - Math.min(16, abs - ((long) i));
                }
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
        return (this.nativePtr == 0 || (this.renderingBitmap == null && this.nextRenderingBitmap == null)) ? false : true;
    }
}
