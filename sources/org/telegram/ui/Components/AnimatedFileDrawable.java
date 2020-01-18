package org.telegram.ui.Components;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import java.io.File;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.DiscardPolicy;
import java.util.concurrent.TimeUnit;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.AnimatedFileDrawableStream;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.tgnet.TLRPC.Document;

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
    private static ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(2, new DiscardPolicy());
    private static float[] radii = new float[8];
    private static final Handler uiHandler = new Handler(Looper.getMainLooper());
    private RectF actualDrawRect = new RectF();
    private boolean applyTransformation;
    private Bitmap backgroundBitmap;
    private int backgroundBitmapTime;
    private BitmapShader backgroundShader;
    private int currentAccount;
    private DispatchQueue decodeQueue;
    private boolean decodeSingleFrame;
    private boolean decoderCreated;
    private boolean destroyWhenDone;
    private final Rect dstRect = new Rect();
    private int invalidateAfter = 50;
    private volatile boolean isRecycled;
    private volatile boolean isRunning;
    private long lastFrameDecodeTime;
    private long lastFrameTime;
    private int lastTimeStamp;
    private Runnable loadFrameRunnable = new Runnable() {
        public void run() {
            if (!AnimatedFileDrawable.this.isRecycled) {
                int i;
                if (!AnimatedFileDrawable.this.decoderCreated && AnimatedFileDrawable.this.nativePtr == 0) {
                    AnimatedFileDrawable animatedFileDrawable = AnimatedFileDrawable.this;
                    animatedFileDrawable.nativePtr = AnimatedFileDrawable.createDecoder(animatedFileDrawable.path.getAbsolutePath(), AnimatedFileDrawable.this.metaData, AnimatedFileDrawable.this.currentAccount, AnimatedFileDrawable.this.streamFileSize, AnimatedFileDrawable.this.stream, false);
                    AnimatedFileDrawable.this.decoderCreated = true;
                }
                try {
                    i = 0;
                    if (AnimatedFileDrawable.this.nativePtr == 0 && AnimatedFileDrawable.this.metaData[0] != 0) {
                        if (AnimatedFileDrawable.this.metaData[1] != 0) {
                            AndroidUtilities.runOnUIThread(AnimatedFileDrawable.this.uiRunnableNoFrame);
                            return;
                        }
                    }
                    if (AnimatedFileDrawable.this.backgroundBitmap == null && AnimatedFileDrawable.this.metaData[0] > 0 && AnimatedFileDrawable.this.metaData[1] > 0) {
                        AnimatedFileDrawable.this.backgroundBitmap = Bitmap.createBitmap(AnimatedFileDrawable.this.metaData[0], AnimatedFileDrawable.this.metaData[1], Config.ARGB_8888);
                        if (AnimatedFileDrawable.this.backgroundShader == null && AnimatedFileDrawable.this.backgroundBitmap != null && AnimatedFileDrawable.this.hasRoundRadius()) {
                            AnimatedFileDrawable.this.backgroundShader = new BitmapShader(AnimatedFileDrawable.this.backgroundBitmap, TileMode.CLAMP, TileMode.CLAMP);
                        }
                    }
                } catch (Throwable th) {
                    FileLog.e(th);
                }
                if (AnimatedFileDrawable.this.pendingSeekTo >= 0) {
                    AnimatedFileDrawable.this.metaData[3] = (int) AnimatedFileDrawable.this.pendingSeekTo;
                    long access$2100 = AnimatedFileDrawable.this.pendingSeekTo;
                    synchronized (AnimatedFileDrawable.this.sync) {
                        AnimatedFileDrawable.this.pendingSeekTo = -1;
                    }
                    if (AnimatedFileDrawable.this.stream != null) {
                        AnimatedFileDrawable.this.stream.reset();
                    }
                    AnimatedFileDrawable.seekToMs(AnimatedFileDrawable.this.nativePtr, access$2100, true);
                    i = 1;
                }
                if (AnimatedFileDrawable.this.backgroundBitmap != null) {
                    AnimatedFileDrawable.this.lastFrameDecodeTime = System.currentTimeMillis();
                    if (AnimatedFileDrawable.getVideoFrame(AnimatedFileDrawable.this.nativePtr, AnimatedFileDrawable.this.backgroundBitmap, AnimatedFileDrawable.this.metaData, AnimatedFileDrawable.this.backgroundBitmap.getRowBytes(), false) == 0) {
                        AndroidUtilities.runOnUIThread(AnimatedFileDrawable.this.uiRunnableNoFrame);
                        return;
                    }
                    if (i != 0) {
                        AnimatedFileDrawable.this.lastTimeStamp = AnimatedFileDrawable.this.metaData[3];
                    }
                    AnimatedFileDrawable.this.backgroundBitmapTime = AnimatedFileDrawable.this.metaData[3];
                }
            }
            AndroidUtilities.runOnUIThread(AnimatedFileDrawable.this.uiRunnable);
        }
    };
    private Runnable loadFrameTask;
    protected final Runnable mInvalidateTask = new -$$Lambda$AnimatedFileDrawable$D96GYyKDLrUXCvNeQ7iluME9Yq4(this);
    private final Runnable mStartTask = new -$$Lambda$AnimatedFileDrawable$AmB2znRBjaDHOIPDjH2S8BYovYQ(this);
    private final int[] metaData = new int[5];
    public volatile long nativePtr;
    private Bitmap nextRenderingBitmap;
    private int nextRenderingBitmapTime;
    private BitmapShader nextRenderingShader;
    private View parentView;
    private File path;
    private boolean pendingRemoveLoading;
    private int pendingRemoveLoadingFramesReset;
    private volatile long pendingSeekTo = -1;
    private volatile long pendingSeekToUI = -1;
    private boolean recycleWithSecond;
    private Bitmap renderingBitmap;
    private int renderingBitmapTime;
    private BitmapShader renderingShader;
    private Path roundPath = new Path();
    private int[] roundRadius = new int[4];
    private int[] roundRadiusBackup;
    private float scaleX = 1.0f;
    private float scaleY = 1.0f;
    private View secondParentView;
    private Matrix shaderMatrix = new Matrix();
    private boolean singleFrameDecoded;
    private AnimatedFileDrawableStream stream;
    private long streamFileSize;
    private final Object sync = new Object();
    private Runnable uiRunnable = new Runnable() {
        public void run() {
            if (AnimatedFileDrawable.this.destroyWhenDone && AnimatedFileDrawable.this.nativePtr != 0) {
                AnimatedFileDrawable.destroyDecoder(AnimatedFileDrawable.this.nativePtr);
                AnimatedFileDrawable.this.nativePtr = 0;
            }
            if (AnimatedFileDrawable.this.nativePtr == 0) {
                if (AnimatedFileDrawable.this.renderingBitmap != null) {
                    AnimatedFileDrawable.this.renderingBitmap.recycle();
                    AnimatedFileDrawable.this.renderingBitmap = null;
                }
                if (AnimatedFileDrawable.this.backgroundBitmap != null) {
                    AnimatedFileDrawable.this.backgroundBitmap.recycle();
                    AnimatedFileDrawable.this.backgroundBitmap = null;
                }
                if (AnimatedFileDrawable.this.decodeQueue != null) {
                    AnimatedFileDrawable.this.decodeQueue.recycle();
                    AnimatedFileDrawable.this.decodeQueue = null;
                }
                return;
            }
            if (AnimatedFileDrawable.this.stream != null && AnimatedFileDrawable.this.pendingRemoveLoading) {
                FileLoader.getInstance(AnimatedFileDrawable.this.currentAccount).removeLoadingVideo(AnimatedFileDrawable.this.stream.getDocument(), false, false);
            }
            if (AnimatedFileDrawable.this.pendingRemoveLoadingFramesReset <= 0) {
                AnimatedFileDrawable.this.pendingRemoveLoading = true;
            } else {
                AnimatedFileDrawable.this.pendingRemoveLoadingFramesReset = AnimatedFileDrawable.this.pendingRemoveLoadingFramesReset - 1;
            }
            AnimatedFileDrawable.this.singleFrameDecoded = true;
            AnimatedFileDrawable.this.loadFrameTask = null;
            AnimatedFileDrawable animatedFileDrawable = AnimatedFileDrawable.this;
            animatedFileDrawable.nextRenderingBitmap = animatedFileDrawable.backgroundBitmap;
            animatedFileDrawable = AnimatedFileDrawable.this;
            animatedFileDrawable.nextRenderingBitmapTime = animatedFileDrawable.backgroundBitmapTime;
            animatedFileDrawable = AnimatedFileDrawable.this;
            animatedFileDrawable.nextRenderingShader = animatedFileDrawable.backgroundShader;
            if (AnimatedFileDrawable.this.metaData[3] < AnimatedFileDrawable.this.lastTimeStamp) {
                AnimatedFileDrawable.this.lastTimeStamp = 0;
            }
            if (AnimatedFileDrawable.this.metaData[3] - AnimatedFileDrawable.this.lastTimeStamp != 0) {
                animatedFileDrawable = AnimatedFileDrawable.this;
                animatedFileDrawable.invalidateAfter = animatedFileDrawable.metaData[3] - AnimatedFileDrawable.this.lastTimeStamp;
            }
            if (AnimatedFileDrawable.this.pendingSeekToUI >= 0 && AnimatedFileDrawable.this.pendingSeekTo == -1) {
                AnimatedFileDrawable.this.pendingSeekToUI = -1;
                AnimatedFileDrawable.this.invalidateAfter = 0;
            }
            animatedFileDrawable = AnimatedFileDrawable.this;
            animatedFileDrawable.lastTimeStamp = animatedFileDrawable.metaData[3];
            if (AnimatedFileDrawable.this.secondParentView != null) {
                AnimatedFileDrawable.this.secondParentView.invalidate();
            } else if (AnimatedFileDrawable.this.parentView != null) {
                AnimatedFileDrawable.this.parentView.invalidate();
            }
            AnimatedFileDrawable.this.scheduleNextGetFrame();
        }
    };
    private Runnable uiRunnableNoFrame = new Runnable() {
        public void run() {
            if (AnimatedFileDrawable.this.destroyWhenDone && AnimatedFileDrawable.this.nativePtr != 0) {
                AnimatedFileDrawable.destroyDecoder(AnimatedFileDrawable.this.nativePtr);
                AnimatedFileDrawable.this.nativePtr = 0;
            }
            if (AnimatedFileDrawable.this.nativePtr == 0) {
                if (AnimatedFileDrawable.this.renderingBitmap != null) {
                    AnimatedFileDrawable.this.renderingBitmap.recycle();
                    AnimatedFileDrawable.this.renderingBitmap = null;
                }
                if (AnimatedFileDrawable.this.backgroundBitmap != null) {
                    AnimatedFileDrawable.this.backgroundBitmap.recycle();
                    AnimatedFileDrawable.this.backgroundBitmap = null;
                }
                if (AnimatedFileDrawable.this.decodeQueue != null) {
                    AnimatedFileDrawable.this.decodeQueue.recycle();
                    AnimatedFileDrawable.this.decodeQueue = null;
                }
                return;
            }
            AnimatedFileDrawable.this.loadFrameTask = null;
            AnimatedFileDrawable.this.scheduleNextGetFrame();
        }
    };
    private boolean useSharedQueue;

    private static native long createDecoder(String str, int[] iArr, int i, long j, Object obj, boolean z);

    private static native void destroyDecoder(long j);

    private static native int getVideoFrame(long j, Bitmap bitmap, int[] iArr, int i, boolean z);

    private static native void getVideoInfo(int i, String str, int[] iArr);

    private static native void prepareToSeek(long j);

    private static native void seekToMs(long j, long j2, boolean z);

    private static native void stopDecoder(long j);

    public int getOpacity() {
        return -2;
    }

    public /* synthetic */ void lambda$new$0$AnimatedFileDrawable() {
        View view = this.secondParentView;
        if (view != null) {
            view.invalidate();
            return;
        }
        view = this.parentView;
        if (view != null) {
            view.invalidate();
        }
    }

    public /* synthetic */ void lambda$new$1$AnimatedFileDrawable() {
        View view = this.secondParentView;
        if (view != null) {
            view.invalidate();
            return;
        }
        view = this.parentView;
        if (view != null) {
            view.invalidate();
        }
    }

    public AnimatedFileDrawable(File file, boolean z, long j, Document document, Object obj, int i, boolean z2) {
        this.path = file;
        this.streamFileSize = j;
        this.currentAccount = i;
        getPaint().setFlags(2);
        if (!(j == 0 || document == null)) {
            this.stream = new AnimatedFileDrawableStream(document, obj, i, z2);
        }
        if (z) {
            this.nativePtr = createDecoder(file.getAbsolutePath(), this.metaData, this.currentAccount, this.streamFileSize, this.stream, z2);
            this.decoderCreated = true;
        }
    }

    public Bitmap getFrameAtTime(long j) {
        if (!this.decoderCreated || this.nativePtr == 0) {
            return null;
        }
        AnimatedFileDrawableStream animatedFileDrawableStream = this.stream;
        if (animatedFileDrawableStream != null) {
            animatedFileDrawableStream.cancel(false);
            this.stream.reset();
        }
        seekToMs(this.nativePtr, j, false);
        if (this.backgroundBitmap == null) {
            int[] iArr = this.metaData;
            this.backgroundBitmap = Bitmap.createBitmap(iArr[0], iArr[1], Config.ARGB_8888);
        }
        long j2 = this.nativePtr;
        Bitmap bitmap = this.backgroundBitmap;
        if (getVideoFrame(j2, bitmap, this.metaData, bitmap.getRowBytes(), true) != 0) {
            return this.backgroundBitmap;
        }
        return null;
    }

    public void setParentView(View view) {
        if (this.parentView == null) {
            this.parentView = view;
        }
    }

    public void setSecondParentView(View view) {
        Object obj = this.secondParentView != null ? 1 : null;
        this.secondParentView = view;
        if (obj != null && view == null) {
            int[] iArr = this.roundRadiusBackup;
            if (iArr != null) {
                setRoundRadius(iArr);
            }
        }
        if (view == null && this.recycleWithSecond) {
            recycle();
        }
    }

    public void setAllowDecodeSingleFrame(boolean z) {
        this.decodeSingleFrame = z;
        if (this.decodeSingleFrame) {
            scheduleNextGetFrame();
        }
    }

    public void seekTo(long j, boolean z) {
        synchronized (this.sync) {
            this.pendingSeekTo = j;
            this.pendingSeekToUI = j;
            prepareToSeek(this.nativePtr);
            if (this.decoderCreated && this.stream != null) {
                this.stream.cancel(z);
                this.pendingRemoveLoading = z;
                this.pendingRemoveLoadingFramesReset = this.pendingRemoveLoading ? 0 : 10;
            }
        }
    }

    public void recycle() {
        if (this.secondParentView != null) {
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
            bitmap = this.nextRenderingBitmap;
            if (bitmap != null) {
                bitmap.recycle();
                this.nextRenderingBitmap = null;
            }
            DispatchQueue dispatchQueue = this.decodeQueue;
            if (dispatchQueue != null) {
                dispatchQueue.recycle();
                this.decodeQueue = null;
            }
        } else {
            this.destroyWhenDone = true;
        }
        AnimatedFileDrawableStream animatedFileDrawableStream = this.stream;
        if (animatedFileDrawableStream != null) {
            animatedFileDrawableStream.cancel(true);
        }
    }

    public void resetStream(boolean z) {
        AnimatedFileDrawableStream animatedFileDrawableStream = this.stream;
        if (animatedFileDrawableStream != null) {
            animatedFileDrawableStream.cancel(true);
        }
        if (this.nativePtr == 0) {
            return;
        }
        if (z) {
            stopDecoder(this.nativePtr);
        } else {
            prepareToSeek(this.nativePtr);
        }
    }

    protected static void runOnUiThread(Runnable runnable) {
        if (Looper.myLooper() == uiHandler.getLooper()) {
            runnable.run();
        } else {
            uiHandler.post(runnable);
        }
    }

    public void setUseSharedQueue(boolean z) {
        this.useSharedQueue = z;
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
        if (i == 0) {
            i = this.renderingBitmapTime;
        }
        return i;
    }

    public int getDurationMs() {
        return this.metaData[4];
    }

    private void scheduleNextGetFrame() {
        if (this.loadFrameTask == null) {
            long j = 0;
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
                if (this.lastFrameDecodeTime != 0) {
                    int i = this.invalidateAfter;
                    j = Math.min((long) i, Math.max(0, ((long) i) - (System.currentTimeMillis() - this.lastFrameDecodeTime)));
                }
                Runnable runnable;
                if (this.useSharedQueue) {
                    ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = executor;
                    runnable = this.loadFrameRunnable;
                    this.loadFrameTask = runnable;
                    scheduledThreadPoolExecutor.schedule(runnable, j, TimeUnit.MILLISECONDS);
                    return;
                }
                if (this.decodeQueue == null) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("decodeQueue");
                    stringBuilder.append(this);
                    this.decodeQueue = new DispatchQueue(stringBuilder.toString());
                }
                DispatchQueue dispatchQueue = this.decodeQueue;
                runnable = this.loadFrameRunnable;
                this.loadFrameTask = runnable;
                dispatchQueue.postRunnable(runnable, j);
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
            i = (iArr[2] == 90 || iArr[2] == 270) ? this.metaData[0] : iArr[1];
        }
        return i == 0 ? AndroidUtilities.dp(100.0f) : i;
    }

    public int getIntrinsicWidth() {
        int i = 0;
        if (this.decoderCreated) {
            int[] iArr = this.metaData;
            i = (iArr[2] == 90 || iArr[2] == 270) ? this.metaData[1] : iArr[0];
        }
        return i == 0 ? AndroidUtilities.dp(100.0f) : i;
    }

    /* Access modifiers changed, original: protected */
    public void onBoundsChange(Rect rect) {
        super.onBoundsChange(rect);
        this.applyTransformation = true;
    }

    public void draw(Canvas canvas) {
        Canvas canvas2 = canvas;
        if ((this.nativePtr != 0 || !this.decoderCreated) && !this.destroyWhenDone) {
            Bitmap bitmap;
            long currentTimeMillis = System.currentTimeMillis();
            if (this.isRunning) {
                if (this.renderingBitmap == null && this.nextRenderingBitmap == null) {
                    scheduleNextGetFrame();
                } else if (this.nextRenderingBitmap != null && (this.renderingBitmap == null || Math.abs(currentTimeMillis - this.lastFrameTime) >= ((long) this.invalidateAfter))) {
                    this.renderingBitmap = this.nextRenderingBitmap;
                    this.renderingBitmapTime = this.nextRenderingBitmapTime;
                    this.renderingShader = this.nextRenderingShader;
                    this.nextRenderingBitmap = null;
                    this.nextRenderingBitmapTime = 0;
                    this.nextRenderingShader = null;
                    this.lastFrameTime = currentTimeMillis;
                }
            } else if (!this.isRunning && this.decodeSingleFrame && Math.abs(currentTimeMillis - this.lastFrameTime) >= ((long) this.invalidateAfter)) {
                bitmap = this.nextRenderingBitmap;
                if (bitmap != null) {
                    this.renderingBitmap = bitmap;
                    this.renderingBitmapTime = this.nextRenderingBitmapTime;
                    this.renderingShader = this.nextRenderingShader;
                    this.nextRenderingBitmap = null;
                    this.nextRenderingBitmapTime = 0;
                    this.nextRenderingShader = null;
                    this.lastFrameTime = currentTimeMillis;
                }
            }
            bitmap = this.renderingBitmap;
            if (bitmap != null) {
                if (this.applyTransformation) {
                    int width = bitmap.getWidth();
                    int height = this.renderingBitmap.getHeight();
                    int[] iArr = this.metaData;
                    if (iArr[2] == 90 || iArr[2] == 270) {
                        int i = height;
                        height = width;
                        width = i;
                    }
                    this.dstRect.set(getBounds());
                    this.scaleX = ((float) this.dstRect.width()) / ((float) width);
                    this.scaleY = ((float) this.dstRect.height()) / ((float) height);
                    this.applyTransformation = false;
                }
                if (hasRoundRadius()) {
                    Math.max(this.scaleX, this.scaleY);
                    if (this.renderingShader == null) {
                        Bitmap bitmap2 = this.backgroundBitmap;
                        TileMode tileMode = TileMode.CLAMP;
                        this.renderingShader = new BitmapShader(bitmap2, tileMode, tileMode);
                    }
                    Paint paint = getPaint();
                    paint.setShader(this.renderingShader);
                    this.shaderMatrix.reset();
                    Matrix matrix = this.shaderMatrix;
                    Rect rect = this.dstRect;
                    matrix.setTranslate((float) rect.left, (float) rect.top);
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
                    int i2 = 0;
                    while (true) {
                        int[] iArr3 = this.roundRadius;
                        if (i2 >= iArr3.length) {
                            break;
                        }
                        float[] fArr = radii;
                        int i3 = i2 * 2;
                        fArr[i3] = (float) iArr3[i2];
                        fArr[i3 + 1] = (float) iArr3[i2];
                        i2++;
                    }
                    this.roundPath.reset();
                    this.roundPath.addRoundRect(this.actualDrawRect, radii, Direction.CW);
                    this.roundPath.close();
                    canvas2.drawPath(this.roundPath, paint);
                } else {
                    Rect rect2 = this.dstRect;
                    canvas2.translate((float) rect2.left, (float) rect2.top);
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
                if (this.isRunning) {
                    long max = Math.max(1, (((long) this.invalidateAfter) - (currentTimeMillis - this.lastFrameTime)) - 17);
                    uiHandler.removeCallbacks(this.mInvalidateTask);
                    uiHandler.postDelayed(this.mInvalidateTask, Math.min(max, (long) this.invalidateAfter));
                }
            }
        }
    }

    public int getMinimumHeight() {
        int i = 0;
        if (this.decoderCreated) {
            int[] iArr = this.metaData;
            i = (iArr[2] == 90 || iArr[2] == 270) ? this.metaData[0] : iArr[1];
        }
        return i == 0 ? AndroidUtilities.dp(100.0f) : i;
    }

    public int getMinimumWidth() {
        int i = 0;
        if (this.decoderCreated) {
            int[] iArr = this.metaData;
            i = (iArr[2] == 90 || iArr[2] == 270) ? this.metaData[1] : iArr[0];
        }
        return i == 0 ? AndroidUtilities.dp(100.0f) : i;
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
        bitmap = this.nextRenderingBitmap;
        return bitmap != null ? bitmap : null;
    }

    public void setActualDrawRect(float f, float f2, float f3, float f4) {
        this.actualDrawRect.set(f, f2, f3 + f, f4 + f2);
    }

    public void setRoundRadius(int[] iArr) {
        int[] iArr2;
        if (this.secondParentView != null) {
            if (this.roundRadiusBackup == null) {
                this.roundRadiusBackup = new int[4];
            }
            iArr2 = this.roundRadius;
            int[] iArr3 = this.roundRadiusBackup;
            System.arraycopy(iArr2, 0, iArr3, 0, iArr3.length);
        }
        iArr2 = this.roundRadius;
        System.arraycopy(iArr, 0, iArr2, 0, iArr2.length);
        getPaint().setFlags(3);
    }

    private boolean hasRoundRadius() {
        int i = 0;
        while (true) {
            int[] iArr = this.roundRadius;
            if (i >= iArr.length) {
                return false;
            }
            if (iArr[i] != 0) {
                return true;
            }
            i++;
        }
    }

    public boolean hasBitmap() {
        return (this.nativePtr == 0 || (this.renderingBitmap == null && this.nextRenderingBitmap == null)) ? false : true;
    }

    public int getOrientation() {
        return this.metaData[2];
    }

    public AnimatedFileDrawable makeCopy() {
        AnimatedFileDrawable animatedFileDrawable;
        AnimatedFileDrawableStream animatedFileDrawableStream = this.stream;
        if (animatedFileDrawableStream != null) {
            File file = this.path;
            long j = this.streamFileSize;
            Document document = animatedFileDrawableStream.getDocument();
            Object parentObject = this.stream.getParentObject();
            int i = this.currentAccount;
            animatedFileDrawableStream = this.stream;
            boolean z = animatedFileDrawableStream != null && animatedFileDrawableStream.isPreview();
            animatedFileDrawable = new AnimatedFileDrawable(file, false, j, document, parentObject, i, z);
        } else {
            File file2 = this.path;
            long j2 = this.streamFileSize;
            int i2 = this.currentAccount;
            boolean z2 = animatedFileDrawableStream != null && animatedFileDrawableStream.isPreview();
            AnimatedFileDrawable animatedFileDrawable2 = new AnimatedFileDrawable(file2, false, j2, null, null, i2, z2);
        }
        int[] iArr = animatedFileDrawable.metaData;
        int[] iArr2 = this.metaData;
        iArr[0] = iArr2[0];
        iArr[1] = iArr2[1];
        return animatedFileDrawable;
    }

    public static void getVideoInfo(String str, int[] iArr) {
        getVideoInfo(VERSION.SDK_INT, str, iArr);
    }
}
