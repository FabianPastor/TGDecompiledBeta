package org.telegram.ui.Components;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.AnimatedFileDrawableStream;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.TLRPC;

public class AnimatedFileDrawable extends BitmapDrawable implements Animatable {
    public static final int PARAM_NUM_AUDIO_FRAME_SIZE = 5;
    public static final int PARAM_NUM_BITRATE = 3;
    public static final int PARAM_NUM_COUNT = 11;
    public static final int PARAM_NUM_DURATION = 4;
    public static final int PARAM_NUM_FRAMERATE = 7;
    public static final int PARAM_NUM_HAS_AUDIO = 10;
    public static final int PARAM_NUM_HEIGHT = 2;
    public static final int PARAM_NUM_ROTATION = 8;
    public static final int PARAM_NUM_SUPPORTED_AUDIO_CODEC = 9;
    public static final int PARAM_NUM_SUPPORTED_VIDEO_CODEC = 0;
    public static final int PARAM_NUM_VIDEO_FRAME_SIZE = 6;
    public static final int PARAM_NUM_WIDTH = 1;
    private static ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(8, new ThreadPoolExecutor.DiscardPolicy());
    private static float[] radii = new float[8];
    private static final Handler uiHandler = new Handler(Looper.getMainLooper());
    private RectF actualDrawRect;
    private boolean applyTransformation;
    /* access modifiers changed from: private */
    public Bitmap backgroundBitmap;
    /* access modifiers changed from: private */
    public int backgroundBitmapTime;
    /* access modifiers changed from: private */
    public BitmapShader backgroundShader;
    /* access modifiers changed from: private */
    public int currentAccount;
    /* access modifiers changed from: private */
    public DispatchQueue decodeQueue;
    private boolean decodeSingleFrame;
    /* access modifiers changed from: private */
    public boolean decoderCreated;
    /* access modifiers changed from: private */
    public boolean destroyWhenDone;
    private final TLRPC.Document document;
    private final Rect dstRect;
    /* access modifiers changed from: private */
    public float endTime;
    /* access modifiers changed from: private */
    public boolean forceDecodeAfterNextFrame;
    /* access modifiers changed from: private */
    public int invalidateAfter;
    private boolean invalidateParentViewWithSecond;
    private boolean invalidatePath;
    private boolean invalidateTaskIsRunning;
    /* access modifiers changed from: private */
    public volatile boolean isRecycled;
    private volatile boolean isRunning;
    public final boolean isWebmSticker;
    /* access modifiers changed from: private */
    public long lastFrameDecodeTime;
    private long lastFrameTime;
    /* access modifiers changed from: private */
    public int lastTimeStamp;
    private Runnable loadFrameRunnable;
    /* access modifiers changed from: private */
    public Runnable loadFrameTask;
    private final Runnable mStartTask;
    /* access modifiers changed from: private */
    public final int[] metaData;
    public volatile long nativePtr;
    /* access modifiers changed from: private */
    public Bitmap nextRenderingBitmap;
    /* access modifiers changed from: private */
    public int nextRenderingBitmapTime;
    /* access modifiers changed from: private */
    public BitmapShader nextRenderingShader;
    private View parentView;
    /* access modifiers changed from: private */
    public ArrayList<View> parents;
    /* access modifiers changed from: private */
    public File path;
    /* access modifiers changed from: private */
    public boolean pendingRemoveLoading;
    /* access modifiers changed from: private */
    public int pendingRemoveLoadingFramesReset;
    /* access modifiers changed from: private */
    public volatile long pendingSeekTo;
    /* access modifiers changed from: private */
    public volatile long pendingSeekToUI;
    private boolean recycleWithSecond;
    /* access modifiers changed from: private */
    public Bitmap renderingBitmap;
    private int renderingBitmapTime;
    private int renderingHeight;
    private BitmapShader renderingShader;
    private int renderingWidth;
    private Path roundPath;
    private int[] roundRadius;
    private int[] roundRadiusBackup;
    /* access modifiers changed from: private */
    public float scaleFactor;
    private float scaleX;
    private float scaleY;
    /* access modifiers changed from: private */
    public ArrayList<View> secondParentViews;
    private Matrix shaderMatrix;
    /* access modifiers changed from: private */
    public boolean singleFrameDecoded;
    /* access modifiers changed from: private */
    public float startTime;
    /* access modifiers changed from: private */
    public AnimatedFileDrawableStream stream;
    /* access modifiers changed from: private */
    public long streamFileSize;
    /* access modifiers changed from: private */
    public final Object sync;
    /* access modifiers changed from: private */
    public Runnable uiRunnable;
    /* access modifiers changed from: private */
    public Runnable uiRunnableNoFrame;
    private boolean useSharedQueue;

    /* access modifiers changed from: private */
    public static native long createDecoder(String str, int[] iArr, int i, long j, Object obj, boolean z);

    /* access modifiers changed from: private */
    public static native void destroyDecoder(long j);

    private static native int getFrameAtTime(long j, long j2, Bitmap bitmap, int[] iArr, int i);

    /* access modifiers changed from: private */
    public static native int getVideoFrame(long j, Bitmap bitmap, int[] iArr, int i, boolean z, float f, float f2);

    private static native void getVideoInfo(int i, String str, int[] iArr);

    private static native void prepareToSeek(long j);

    /* access modifiers changed from: private */
    public static native void seekToMs(long j, long j2, boolean z);

    private static native void stopDecoder(long j);

    static /* synthetic */ int access$1110(AnimatedFileDrawable x0) {
        int i = x0.pendingRemoveLoadingFramesReset;
        x0.pendingRemoveLoadingFramesReset = i - 1;
        return i;
    }

    /* access modifiers changed from: private */
    public void updateScaleFactor() {
        int i;
        int i2;
        if (!this.isWebmSticker && (i = this.renderingHeight) > 0 && (i2 = this.renderingWidth) > 0) {
            int[] iArr = this.metaData;
            if (iArr[0] > 0 && iArr[1] > 0) {
                float max = Math.max(((float) i2) / ((float) iArr[0]), ((float) i) / ((float) iArr[1]));
                this.scaleFactor = max;
                if (max <= 0.0f || ((double) max) > 0.7d) {
                    this.scaleFactor = 1.0f;
                    return;
                }
                return;
            }
        }
        this.scaleFactor = 1.0f;
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-AnimatedFileDrawable  reason: not valid java name */
    public /* synthetic */ void m3595lambda$new$0$orgtelegramuiComponentsAnimatedFileDrawable() {
        View view;
        if (!this.secondParentViews.isEmpty()) {
            int N = this.secondParentViews.size();
            for (int a = 0; a < N; a++) {
                this.secondParentViews.get(a).invalidate();
            }
        }
        if ((this.secondParentViews.isEmpty() || this.invalidateParentViewWithSecond) && (view = this.parentView) != null) {
            view.invalidate();
        }
    }

    public AnimatedFileDrawable(File file, boolean createDecoder, long streamSize, TLRPC.Document document2, ImageLocation location, Object parentObject, long seekTo, int account, boolean preview) {
        this(file, createDecoder, streamSize, document2, location, parentObject, seekTo, account, preview, 0, 0);
    }

    public AnimatedFileDrawable(File file, boolean createDecoder, long streamSize, TLRPC.Document document2, ImageLocation location, Object parentObject, long seekTo, int account, boolean preview, int w, int h) {
        long j;
        boolean z;
        long j2 = streamSize;
        TLRPC.Document document3 = document2;
        long j3 = seekTo;
        this.invalidateAfter = 50;
        int[] iArr = new int[5];
        this.metaData = iArr;
        this.pendingSeekTo = -1;
        this.pendingSeekToUI = -1;
        this.sync = new Object();
        this.actualDrawRect = new RectF();
        this.roundRadius = new int[4];
        this.shaderMatrix = new Matrix();
        this.roundPath = new Path();
        this.scaleX = 1.0f;
        this.scaleY = 1.0f;
        this.dstRect = new Rect();
        this.scaleFactor = 1.0f;
        this.secondParentViews = new ArrayList<>();
        this.parents = new ArrayList<>();
        this.invalidatePath = true;
        this.uiRunnableNoFrame = new Runnable() {
            public void run() {
                if (AnimatedFileDrawable.this.destroyWhenDone && AnimatedFileDrawable.this.nativePtr != 0) {
                    AnimatedFileDrawable.destroyDecoder(AnimatedFileDrawable.this.nativePtr);
                    AnimatedFileDrawable.this.nativePtr = 0;
                }
                if (AnimatedFileDrawable.this.nativePtr == 0) {
                    if (AnimatedFileDrawable.this.renderingBitmap != null) {
                        AnimatedFileDrawable.this.renderingBitmap.recycle();
                        Bitmap unused = AnimatedFileDrawable.this.renderingBitmap = null;
                    }
                    if (AnimatedFileDrawable.this.backgroundBitmap != null) {
                        AnimatedFileDrawable.this.backgroundBitmap.recycle();
                        Bitmap unused2 = AnimatedFileDrawable.this.backgroundBitmap = null;
                    }
                    if (AnimatedFileDrawable.this.decodeQueue != null) {
                        AnimatedFileDrawable.this.decodeQueue.recycle();
                        DispatchQueue unused3 = AnimatedFileDrawable.this.decodeQueue = null;
                        return;
                    }
                    return;
                }
                Runnable unused4 = AnimatedFileDrawable.this.loadFrameTask = null;
                AnimatedFileDrawable.this.scheduleNextGetFrame();
                for (int i = 0; i < AnimatedFileDrawable.this.parents.size(); i++) {
                    ((View) AnimatedFileDrawable.this.parents.get(i)).invalidate();
                }
            }
        };
        this.uiRunnable = new Runnable() {
            public void run() {
                if (AnimatedFileDrawable.this.destroyWhenDone && AnimatedFileDrawable.this.nativePtr != 0) {
                    AnimatedFileDrawable.destroyDecoder(AnimatedFileDrawable.this.nativePtr);
                    AnimatedFileDrawable.this.nativePtr = 0;
                }
                if (AnimatedFileDrawable.this.nativePtr == 0) {
                    if (AnimatedFileDrawable.this.renderingBitmap != null) {
                        AnimatedFileDrawable.this.renderingBitmap.recycle();
                        Bitmap unused = AnimatedFileDrawable.this.renderingBitmap = null;
                    }
                    if (AnimatedFileDrawable.this.backgroundBitmap != null) {
                        AnimatedFileDrawable.this.backgroundBitmap.recycle();
                        Bitmap unused2 = AnimatedFileDrawable.this.backgroundBitmap = null;
                    }
                    if (AnimatedFileDrawable.this.decodeQueue != null) {
                        AnimatedFileDrawable.this.decodeQueue.recycle();
                        DispatchQueue unused3 = AnimatedFileDrawable.this.decodeQueue = null;
                        return;
                    }
                    return;
                }
                if (AnimatedFileDrawable.this.stream != null && AnimatedFileDrawable.this.pendingRemoveLoading) {
                    FileLoader.getInstance(AnimatedFileDrawable.this.currentAccount).removeLoadingVideo(AnimatedFileDrawable.this.stream.getDocument(), false, false);
                }
                if (AnimatedFileDrawable.this.pendingRemoveLoadingFramesReset <= 0) {
                    boolean unused4 = AnimatedFileDrawable.this.pendingRemoveLoading = true;
                } else {
                    AnimatedFileDrawable.access$1110(AnimatedFileDrawable.this);
                }
                if (!AnimatedFileDrawable.this.forceDecodeAfterNextFrame) {
                    boolean unused5 = AnimatedFileDrawable.this.singleFrameDecoded = true;
                } else {
                    boolean unused6 = AnimatedFileDrawable.this.forceDecodeAfterNextFrame = false;
                }
                Runnable unused7 = AnimatedFileDrawable.this.loadFrameTask = null;
                AnimatedFileDrawable animatedFileDrawable = AnimatedFileDrawable.this;
                Bitmap unused8 = animatedFileDrawable.nextRenderingBitmap = animatedFileDrawable.backgroundBitmap;
                AnimatedFileDrawable animatedFileDrawable2 = AnimatedFileDrawable.this;
                int unused9 = animatedFileDrawable2.nextRenderingBitmapTime = animatedFileDrawable2.backgroundBitmapTime;
                AnimatedFileDrawable animatedFileDrawable3 = AnimatedFileDrawable.this;
                BitmapShader unused10 = animatedFileDrawable3.nextRenderingShader = animatedFileDrawable3.backgroundShader;
                if (AnimatedFileDrawable.this.metaData[3] < AnimatedFileDrawable.this.lastTimeStamp) {
                    AnimatedFileDrawable animatedFileDrawable4 = AnimatedFileDrawable.this;
                    int unused11 = animatedFileDrawable4.lastTimeStamp = animatedFileDrawable4.startTime > 0.0f ? (int) (AnimatedFileDrawable.this.startTime * 1000.0f) : 0;
                }
                if (AnimatedFileDrawable.this.metaData[3] - AnimatedFileDrawable.this.lastTimeStamp != 0) {
                    AnimatedFileDrawable animatedFileDrawable5 = AnimatedFileDrawable.this;
                    int unused12 = animatedFileDrawable5.invalidateAfter = animatedFileDrawable5.metaData[3] - AnimatedFileDrawable.this.lastTimeStamp;
                }
                if (AnimatedFileDrawable.this.pendingSeekToUI >= 0 && AnimatedFileDrawable.this.pendingSeekTo == -1) {
                    long unused13 = AnimatedFileDrawable.this.pendingSeekToUI = -1;
                    int unused14 = AnimatedFileDrawable.this.invalidateAfter = 0;
                }
                AnimatedFileDrawable animatedFileDrawable6 = AnimatedFileDrawable.this;
                int unused15 = animatedFileDrawable6.lastTimeStamp = animatedFileDrawable6.metaData[3];
                if (!AnimatedFileDrawable.this.secondParentViews.isEmpty()) {
                    int N = AnimatedFileDrawable.this.secondParentViews.size();
                    for (int a = 0; a < N; a++) {
                        ((View) AnimatedFileDrawable.this.secondParentViews.get(a)).invalidate();
                    }
                }
                for (int i = 0; i < AnimatedFileDrawable.this.parents.size(); i++) {
                    ((View) AnimatedFileDrawable.this.parents.get(i)).invalidate();
                }
                AnimatedFileDrawable.this.scheduleNextGetFrame();
            }
        };
        this.loadFrameRunnable = new Runnable() {
            public void run() {
                if (!AnimatedFileDrawable.this.isRecycled) {
                    if (!AnimatedFileDrawable.this.decoderCreated && AnimatedFileDrawable.this.nativePtr == 0) {
                        AnimatedFileDrawable animatedFileDrawable = AnimatedFileDrawable.this;
                        animatedFileDrawable.nativePtr = AnimatedFileDrawable.createDecoder(animatedFileDrawable.path.getAbsolutePath(), AnimatedFileDrawable.this.metaData, AnimatedFileDrawable.this.currentAccount, AnimatedFileDrawable.this.streamFileSize, AnimatedFileDrawable.this.stream, false);
                        if (AnimatedFileDrawable.this.nativePtr != 0 && (AnimatedFileDrawable.this.metaData[0] > 3840 || AnimatedFileDrawable.this.metaData[1] > 3840)) {
                            AnimatedFileDrawable.destroyDecoder(AnimatedFileDrawable.this.nativePtr);
                            AnimatedFileDrawable.this.nativePtr = 0;
                        }
                        AnimatedFileDrawable.this.updateScaleFactor();
                        boolean unused = AnimatedFileDrawable.this.decoderCreated = true;
                    }
                    try {
                        if (AnimatedFileDrawable.this.nativePtr == 0 && AnimatedFileDrawable.this.metaData[0] != 0) {
                            if (AnimatedFileDrawable.this.metaData[1] != 0) {
                                AndroidUtilities.runOnUIThread(AnimatedFileDrawable.this.uiRunnableNoFrame);
                                return;
                            }
                        }
                        if (AnimatedFileDrawable.this.backgroundBitmap == null && AnimatedFileDrawable.this.metaData[0] > 0 && AnimatedFileDrawable.this.metaData[1] > 0) {
                            AnimatedFileDrawable animatedFileDrawable2 = AnimatedFileDrawable.this;
                            Bitmap unused2 = animatedFileDrawable2.backgroundBitmap = Bitmap.createBitmap((int) (((float) animatedFileDrawable2.metaData[0]) * AnimatedFileDrawable.this.scaleFactor), (int) (((float) AnimatedFileDrawable.this.metaData[1]) * AnimatedFileDrawable.this.scaleFactor), Bitmap.Config.ARGB_8888);
                            if (AnimatedFileDrawable.this.backgroundShader == null && AnimatedFileDrawable.this.backgroundBitmap != null && AnimatedFileDrawable.this.hasRoundRadius()) {
                                BitmapShader unused3 = AnimatedFileDrawable.this.backgroundShader = new BitmapShader(AnimatedFileDrawable.this.backgroundBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                            }
                        }
                    } catch (Throwable e) {
                        FileLog.e(e);
                    }
                    boolean seekWas = false;
                    if (AnimatedFileDrawable.this.pendingSeekTo >= 0) {
                        AnimatedFileDrawable.this.metaData[3] = (int) AnimatedFileDrawable.this.pendingSeekTo;
                        long seekTo = AnimatedFileDrawable.this.pendingSeekTo;
                        synchronized (AnimatedFileDrawable.this.sync) {
                            long unused4 = AnimatedFileDrawable.this.pendingSeekTo = -1;
                        }
                        seekWas = true;
                        if (AnimatedFileDrawable.this.stream != null) {
                            AnimatedFileDrawable.this.stream.reset();
                        }
                        AnimatedFileDrawable.seekToMs(AnimatedFileDrawable.this.nativePtr, seekTo, true);
                    }
                    if (AnimatedFileDrawable.this.backgroundBitmap != null) {
                        long unused5 = AnimatedFileDrawable.this.lastFrameDecodeTime = System.currentTimeMillis();
                        if (AnimatedFileDrawable.getVideoFrame(AnimatedFileDrawable.this.nativePtr, AnimatedFileDrawable.this.backgroundBitmap, AnimatedFileDrawable.this.metaData, AnimatedFileDrawable.this.backgroundBitmap.getRowBytes(), false, AnimatedFileDrawable.this.startTime, AnimatedFileDrawable.this.endTime) == 0) {
                            AndroidUtilities.runOnUIThread(AnimatedFileDrawable.this.uiRunnableNoFrame);
                            return;
                        }
                        if (seekWas) {
                            AnimatedFileDrawable animatedFileDrawable3 = AnimatedFileDrawable.this;
                            int unused6 = animatedFileDrawable3.lastTimeStamp = animatedFileDrawable3.metaData[3];
                        }
                        AnimatedFileDrawable animatedFileDrawable4 = AnimatedFileDrawable.this;
                        int unused7 = animatedFileDrawable4.backgroundBitmapTime = animatedFileDrawable4.metaData[3];
                    }
                }
                AndroidUtilities.runOnUIThread(AnimatedFileDrawable.this.uiRunnable);
            }
        };
        this.mStartTask = new AnimatedFileDrawable$$ExternalSyntheticLambda0(this);
        this.path = file;
        this.streamFileSize = j2;
        this.currentAccount = account;
        this.renderingHeight = h;
        this.renderingWidth = w;
        this.document = document3;
        boolean z2 = MessageObject.isWebM(document2) || MessageObject.isVideoSticker(document2);
        this.isWebmSticker = z2;
        if (z2) {
            this.useSharedQueue = true;
        }
        getPaint().setFlags(3);
        if (j2 == 0) {
            j = 0;
        } else if (document3 == null && location == null) {
            j = 0;
        } else {
            AnimatedFileDrawableStream animatedFileDrawableStream = r3;
            j = 0;
            AnimatedFileDrawableStream animatedFileDrawableStream2 = new AnimatedFileDrawableStream(document2, location, parentObject, account, preview);
            this.stream = animatedFileDrawableStream2;
        }
        if (createDecoder) {
            int[] iArr2 = iArr;
            this.nativePtr = createDecoder(file.getAbsolutePath(), iArr, this.currentAccount, this.streamFileSize, this.stream, preview);
            if (this.nativePtr != j) {
                z = false;
                if (iArr2[0] > 3840 || iArr2[1] > 3840) {
                    destroyDecoder(this.nativePtr);
                    this.nativePtr = j;
                }
            } else {
                z = false;
            }
            updateScaleFactor();
            this.decoderCreated = true;
        } else {
            z = false;
        }
        if (j3 != j) {
            seekTo(j3, z);
        }
    }

    public Bitmap getFrameAtTime(long ms) {
        return getFrameAtTime(ms, false);
    }

    public Bitmap getFrameAtTime(long ms, boolean precise) {
        int result;
        if (!this.decoderCreated || this.nativePtr == 0) {
            return null;
        }
        AnimatedFileDrawableStream animatedFileDrawableStream = this.stream;
        if (animatedFileDrawableStream != null) {
            animatedFileDrawableStream.cancel(false);
            this.stream.reset();
        }
        if (!precise) {
            seekToMs(this.nativePtr, ms, precise);
        }
        if (this.backgroundBitmap == null) {
            int[] iArr = this.metaData;
            float f = this.scaleFactor;
            this.backgroundBitmap = Bitmap.createBitmap((int) (((float) iArr[0]) * f), (int) (((float) iArr[1]) * f), Bitmap.Config.ARGB_8888);
        }
        if (precise) {
            long j = this.nativePtr;
            Bitmap bitmap = this.backgroundBitmap;
            result = getFrameAtTime(j, ms, bitmap, this.metaData, bitmap.getRowBytes());
        } else {
            long j2 = this.nativePtr;
            Bitmap bitmap2 = this.backgroundBitmap;
            result = getVideoFrame(j2, bitmap2, this.metaData, bitmap2.getRowBytes(), true, 0.0f, 0.0f);
        }
        if (result != 0) {
            return this.backgroundBitmap;
        }
        return null;
    }

    public void setParentView(View view) {
        if (this.parentView == null) {
            this.parentView = view;
        }
    }

    public void addParent(View view) {
        if (view != null && !this.parents.contains(view)) {
            this.parents.add(view);
            if (this.isRunning) {
                scheduleNextGetFrame();
            }
        }
    }

    public void removeParent(View view) {
        this.parents.remove(view);
    }

    public void setInvalidateParentViewWithSecond(boolean value) {
        this.invalidateParentViewWithSecond = value;
    }

    public void addSecondParentView(View view) {
        if (view != null && !this.secondParentViews.contains(view)) {
            this.secondParentViews.add(view);
        }
    }

    public void removeSecondParentView(View view) {
        this.secondParentViews.remove(view);
        if (!this.secondParentViews.isEmpty()) {
            return;
        }
        if (this.recycleWithSecond) {
            recycle();
            return;
        }
        int[] iArr = this.roundRadiusBackup;
        if (iArr != null) {
            setRoundRadius(iArr);
        }
    }

    public void setAllowDecodeSingleFrame(boolean value) {
        this.decodeSingleFrame = value;
        if (value) {
            scheduleNextGetFrame();
        }
    }

    public void seekTo(long ms, boolean removeLoading) {
        seekTo(ms, removeLoading, false);
    }

    public void seekTo(long ms, boolean removeLoading, boolean force) {
        AnimatedFileDrawableStream animatedFileDrawableStream;
        synchronized (this.sync) {
            this.pendingSeekTo = ms;
            this.pendingSeekToUI = ms;
            if (this.nativePtr != 0) {
                prepareToSeek(this.nativePtr);
            }
            if (this.decoderCreated && (animatedFileDrawableStream = this.stream) != null) {
                animatedFileDrawableStream.cancel(removeLoading);
                this.pendingRemoveLoading = removeLoading;
                this.pendingRemoveLoadingFramesReset = removeLoading ? 0 : 10;
            }
            if (force && this.decodeSingleFrame) {
                this.singleFrameDecoded = false;
                if (this.loadFrameTask == null) {
                    scheduleNextGetFrame();
                } else {
                    this.forceDecodeAfterNextFrame = true;
                }
            }
        }
    }

    public void recycle() {
        if (!this.secondParentViews.isEmpty()) {
            this.recycleWithSecond = true;
            return;
        }
        this.isRunning = false;
        this.isRecycled = true;
        if (this.loadFrameTask == null) {
            if (this.nativePtr != 0) {
                destroyDecoder(this.nativePtr);
                this.nativePtr = 0;
            }
            Bitmap bitmap = this.renderingBitmap;
            if (bitmap != null) {
                bitmap.recycle();
                this.renderingBitmap = null;
            }
            Bitmap bitmap2 = this.nextRenderingBitmap;
            if (bitmap2 != null) {
                bitmap2.recycle();
                this.nextRenderingBitmap = null;
            }
            DispatchQueue dispatchQueue = this.decodeQueue;
            if (dispatchQueue != null) {
                dispatchQueue.recycle();
                this.decodeQueue = null;
            }
            getPaint().setShader((Shader) null);
        } else {
            this.destroyWhenDone = true;
        }
        AnimatedFileDrawableStream animatedFileDrawableStream = this.stream;
        if (animatedFileDrawableStream != null) {
            animatedFileDrawableStream.cancel(true);
        }
    }

    public void resetStream(boolean stop) {
        AnimatedFileDrawableStream animatedFileDrawableStream = this.stream;
        if (animatedFileDrawableStream != null) {
            animatedFileDrawableStream.cancel(true);
        }
        if (this.nativePtr == 0) {
            return;
        }
        if (stop) {
            stopDecoder(this.nativePtr);
        } else {
            prepareToSeek(this.nativePtr);
        }
    }

    protected static void runOnUiThread(Runnable task) {
        Looper myLooper = Looper.myLooper();
        Handler handler = uiHandler;
        if (myLooper == handler.getLooper()) {
            task.run();
        } else {
            handler.post(task);
        }
    }

    public void setUseSharedQueue(boolean value) {
        if (!this.isWebmSticker) {
            this.useSharedQueue = value;
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
        if (!this.isRunning && this.parents.size() != 0) {
            this.isRunning = true;
            scheduleNextGetFrame();
            runOnUiThread(this.mStartTask);
        }
    }

    public float getCurrentProgress() {
        if (this.metaData[4] == 0) {
            return 0.0f;
        }
        if (this.pendingSeekToUI >= 0) {
            return ((float) this.pendingSeekToUI) / ((float) this.metaData[4]);
        }
        int[] iArr = this.metaData;
        return ((float) iArr[3]) / ((float) iArr[4]);
    }

    public int getCurrentProgressMs() {
        if (this.pendingSeekToUI >= 0) {
            return (int) this.pendingSeekToUI;
        }
        int i = this.nextRenderingBitmapTime;
        return i != 0 ? i : this.renderingBitmapTime;
    }

    public int getDurationMs() {
        return this.metaData[4];
    }

    /* access modifiers changed from: private */
    public void scheduleNextGetFrame() {
        if (this.loadFrameTask != null) {
            return;
        }
        if ((this.nativePtr != 0 || !this.decoderCreated) && !this.destroyWhenDone) {
            if (!this.isRunning) {
                boolean z = this.decodeSingleFrame;
                if (!z) {
                    return;
                }
                if (z && this.singleFrameDecoded) {
                    return;
                }
            }
            if (this.parents.size() != 0) {
                long ms = 0;
                if (this.lastFrameDecodeTime != 0) {
                    int i = this.invalidateAfter;
                    ms = Math.min((long) i, Math.max(0, ((long) i) - (System.currentTimeMillis() - this.lastFrameDecodeTime)));
                }
                if (this.useSharedQueue) {
                    ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = executor;
                    Runnable runnable = this.loadFrameRunnable;
                    this.loadFrameTask = runnable;
                    scheduledThreadPoolExecutor.schedule(runnable, ms, TimeUnit.MILLISECONDS);
                    return;
                }
                if (this.decodeQueue == null) {
                    this.decodeQueue = new DispatchQueue("decodeQueue" + this);
                }
                DispatchQueue dispatchQueue = this.decodeQueue;
                Runnable runnable2 = this.loadFrameRunnable;
                this.loadFrameTask = runnable2;
                dispatchQueue.postRunnable(runnable2, ms);
            }
        }
    }

    public boolean isLoadingStream() {
        AnimatedFileDrawableStream animatedFileDrawableStream = this.stream;
        return animatedFileDrawableStream != null && animatedFileDrawableStream.isWaitingForLoad();
    }

    public void stop() {
        this.isRunning = false;
    }

    public boolean isRunning() {
        return this.isRunning;
    }

    public int getIntrinsicHeight() {
        int i = 0;
        if (this.decoderCreated) {
            int[] iArr = this.metaData;
            i = (iArr[2] == 90 || iArr[2] == 270) ? iArr[0] : iArr[1];
        }
        int height = i;
        if (height == 0) {
            return AndroidUtilities.dp(100.0f);
        }
        return (int) (((float) height) * this.scaleFactor);
    }

    public int getIntrinsicWidth() {
        int i = 0;
        if (this.decoderCreated) {
            int[] iArr = this.metaData;
            i = (iArr[2] == 90 || iArr[2] == 270) ? iArr[1] : iArr[0];
        }
        int width = i;
        if (width == 0) {
            return AndroidUtilities.dp(100.0f);
        }
        return (int) (((float) width) * this.scaleFactor);
    }

    /* access modifiers changed from: protected */
    public void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        this.applyTransformation = true;
    }

    public void draw(Canvas canvas) {
        Bitmap bitmap;
        Canvas canvas2 = canvas;
        if ((this.nativePtr != 0 || !this.decoderCreated) && !this.destroyWhenDone) {
            long now = System.currentTimeMillis();
            if (this.isRunning) {
                Bitmap bitmap2 = this.renderingBitmap;
                if (bitmap2 == null && this.nextRenderingBitmap == null) {
                    scheduleNextGetFrame();
                } else if (this.nextRenderingBitmap != null && (bitmap2 == null || Math.abs(now - this.lastFrameTime) >= ((long) this.invalidateAfter))) {
                    this.renderingBitmap = this.nextRenderingBitmap;
                    this.renderingBitmapTime = this.nextRenderingBitmapTime;
                    this.renderingShader = this.nextRenderingShader;
                    this.nextRenderingBitmap = null;
                    this.nextRenderingBitmapTime = 0;
                    this.nextRenderingShader = null;
                    this.lastFrameTime = now;
                }
            } else if (!this.isRunning && this.decodeSingleFrame && Math.abs(now - this.lastFrameTime) >= ((long) this.invalidateAfter) && (bitmap = this.nextRenderingBitmap) != null) {
                this.renderingBitmap = bitmap;
                this.renderingBitmapTime = this.nextRenderingBitmapTime;
                this.renderingShader = this.nextRenderingShader;
                this.nextRenderingBitmap = null;
                this.nextRenderingBitmapTime = 0;
                this.nextRenderingShader = null;
                this.lastFrameTime = now;
            }
            Bitmap bitmap3 = this.renderingBitmap;
            if (bitmap3 != null) {
                if (this.applyTransformation) {
                    int bitmapW = bitmap3.getWidth();
                    int bitmapH = this.renderingBitmap.getHeight();
                    int[] iArr = this.metaData;
                    if (iArr[2] == 90 || iArr[2] == 270) {
                        int temp = bitmapW;
                        bitmapW = bitmapH;
                        bitmapH = temp;
                    }
                    this.dstRect.set(getBounds());
                    this.scaleX = ((float) this.dstRect.width()) / ((float) bitmapW);
                    this.scaleY = ((float) this.dstRect.height()) / ((float) bitmapH);
                    this.applyTransformation = false;
                }
                if (hasRoundRadius() != 0) {
                    if (this.renderingShader == null) {
                        this.renderingShader = new BitmapShader(this.backgroundBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                    }
                    Paint paint = getPaint();
                    paint.setShader(this.renderingShader);
                    this.shaderMatrix.reset();
                    this.shaderMatrix.setTranslate((float) this.dstRect.left, (float) this.dstRect.top);
                    int[] iArr2 = this.metaData;
                    if (iArr2[2] == 90) {
                        this.shaderMatrix.preRotate(90.0f);
                        this.shaderMatrix.preTranslate(0.0f, (float) (-this.dstRect.width()));
                    } else if (iArr2[2] == 180) {
                        this.shaderMatrix.preRotate(180.0f);
                        this.shaderMatrix.preTranslate((float) (-this.dstRect.width()), (float) (-this.dstRect.height()));
                    } else if (iArr2[2] == 270) {
                        this.shaderMatrix.preRotate(270.0f);
                        this.shaderMatrix.preTranslate((float) (-this.dstRect.height()), 0.0f);
                    }
                    this.shaderMatrix.preScale(this.scaleX, this.scaleY);
                    this.renderingShader.setLocalMatrix(this.shaderMatrix);
                    if (this.invalidatePath) {
                        this.invalidatePath = false;
                        int a = 0;
                        while (true) {
                            int[] iArr3 = this.roundRadius;
                            if (a >= iArr3.length) {
                                break;
                            }
                            float[] fArr = radii;
                            fArr[a * 2] = (float) iArr3[a];
                            fArr[(a * 2) + 1] = (float) iArr3[a];
                            a++;
                        }
                        this.roundPath.reset();
                        this.roundPath.addRoundRect(this.actualDrawRect, radii, Path.Direction.CW);
                        this.roundPath.close();
                    }
                    canvas2.drawPath(this.roundPath, paint);
                    return;
                }
                canvas2.translate((float) this.dstRect.left, (float) this.dstRect.top);
                int[] iArr4 = this.metaData;
                if (iArr4[2] == 90) {
                    canvas2.rotate(90.0f);
                    canvas2.translate(0.0f, (float) (-this.dstRect.width()));
                } else if (iArr4[2] == 180) {
                    canvas2.rotate(180.0f);
                    canvas2.translate((float) (-this.dstRect.width()), (float) (-this.dstRect.height()));
                } else if (iArr4[2] == 270) {
                    canvas2.rotate(270.0f);
                    canvas2.translate((float) (-this.dstRect.height()), 0.0f);
                }
                canvas2.scale(this.scaleX, this.scaleY);
                canvas2.drawBitmap(this.renderingBitmap, 0.0f, 0.0f, getPaint());
            }
        }
    }

    public int getMinimumHeight() {
        int i = 0;
        if (this.decoderCreated) {
            int[] iArr = this.metaData;
            i = (iArr[2] == 90 || iArr[2] == 270) ? iArr[0] : iArr[1];
        }
        int height = i;
        if (height == 0) {
            return AndroidUtilities.dp(100.0f);
        }
        return height;
    }

    public int getMinimumWidth() {
        int i = 0;
        if (this.decoderCreated) {
            int[] iArr = this.metaData;
            i = (iArr[2] == 90 || iArr[2] == 270) ? iArr[1] : iArr[0];
        }
        int width = i;
        if (width == 0) {
            return AndroidUtilities.dp(100.0f);
        }
        return width;
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
        Bitmap bitmap = this.renderingBitmap;
        if (bitmap != null) {
            return bitmap;
        }
        Bitmap bitmap2 = this.nextRenderingBitmap;
        if (bitmap2 != null) {
            return bitmap2;
        }
        return null;
    }

    public void setActualDrawRect(float x, float y, float width, float height) {
        float bottom = y + height;
        float right = x + width;
        if (this.actualDrawRect.left != x || this.actualDrawRect.top != y || this.actualDrawRect.right != right || this.actualDrawRect.bottom != bottom) {
            this.actualDrawRect.set(x, y, right, bottom);
            this.invalidatePath = true;
        }
    }

    public void setRoundRadius(int[] value) {
        if (!this.secondParentViews.isEmpty()) {
            if (this.roundRadiusBackup == null) {
                this.roundRadiusBackup = new int[4];
            }
            int[] iArr = this.roundRadius;
            int[] iArr2 = this.roundRadiusBackup;
            System.arraycopy(iArr, 0, iArr2, 0, iArr2.length);
        }
        for (int i = 0; i < 4; i++) {
            if (!this.invalidatePath && value[i] != this.roundRadius[i]) {
                this.invalidatePath = true;
            }
            this.roundRadius[i] = value[i];
        }
    }

    /* access modifiers changed from: private */
    public boolean hasRoundRadius() {
        int a = 0;
        while (true) {
            int[] iArr = this.roundRadius;
            if (a >= iArr.length) {
                return false;
            }
            if (iArr[a] != 0) {
                return true;
            }
            a++;
        }
    }

    public boolean hasBitmap() {
        return (this.nativePtr == 0 || (this.renderingBitmap == null && this.nextRenderingBitmap == null)) ? false : true;
    }

    public int getOrientation() {
        return this.metaData[2];
    }

    public AnimatedFileDrawable makeCopy() {
        AnimatedFileDrawable drawable;
        if (this.stream != null) {
            File file = this.path;
            long j = this.streamFileSize;
            TLRPC.Document document2 = this.stream.getDocument();
            ImageLocation location = this.stream.getLocation();
            Object parentObject = this.stream.getParentObject();
            long j2 = this.pendingSeekToUI;
            int i = this.currentAccount;
            AnimatedFileDrawableStream animatedFileDrawableStream = this.stream;
            drawable = new AnimatedFileDrawable(file, false, j, document2, location, parentObject, j2, i, animatedFileDrawableStream != null && animatedFileDrawableStream.isPreview());
        } else {
            File file2 = this.path;
            long j3 = this.streamFileSize;
            TLRPC.Document document3 = this.document;
            long j4 = this.pendingSeekToUI;
            int i2 = this.currentAccount;
            AnimatedFileDrawableStream animatedFileDrawableStream2 = this.stream;
            drawable = new AnimatedFileDrawable(file2, false, j3, document3, (ImageLocation) null, (Object) null, j4, i2, animatedFileDrawableStream2 != null && animatedFileDrawableStream2.isPreview());
        }
        int[] iArr = drawable.metaData;
        int[] iArr2 = this.metaData;
        iArr[0] = iArr2[0];
        iArr[1] = iArr2[1];
        return drawable;
    }

    public static void getVideoInfo(String src, int[] params) {
        getVideoInfo(Build.VERSION.SDK_INT, src, params);
    }

    public void setStartEndTime(long startTime2, long endTime2) {
        this.startTime = ((float) startTime2) / 1000.0f;
        this.endTime = ((float) endTime2) / 1000.0f;
        if (((long) getCurrentProgressMs()) < startTime2) {
            seekTo(startTime2, true);
        }
    }

    public long getStartTime() {
        return (long) (this.startTime * 1000.0f);
    }

    public boolean isRecycled() {
        return this.isRecycled;
    }

    public Bitmap getNextFrame() {
        if (this.backgroundBitmap == null) {
            int[] iArr = this.metaData;
            float f = this.scaleFactor;
            this.backgroundBitmap = Bitmap.createBitmap((int) (((float) iArr[0]) * f), (int) (((float) iArr[1]) * f), Bitmap.Config.ARGB_8888);
        }
        long j = this.nativePtr;
        Bitmap bitmap = this.backgroundBitmap;
        getVideoFrame(j, bitmap, this.metaData, bitmap.getRowBytes(), false, this.startTime, this.endTime);
        return this.backgroundBitmap;
    }
}
