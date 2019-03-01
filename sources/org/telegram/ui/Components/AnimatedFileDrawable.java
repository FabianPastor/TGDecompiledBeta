package org.telegram.ui.Components;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.BitmapDrawable;
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
    private static ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(2, new DiscardPolicy());
    private static final Handler uiHandler = new Handler(Looper.getMainLooper());
    private RectF actualDrawRect = new RectF();
    private boolean applyTransformation;
    private Bitmap backgroundBitmap;
    private int backgroundBitmapTime;
    private BitmapShader backgroundShader;
    private RectF bitmapRect = new RectF();
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
                if (!AnimatedFileDrawable.this.decoderCreated && AnimatedFileDrawable.this.nativePtr == 0) {
                    AnimatedFileDrawable.this.nativePtr = AnimatedFileDrawable.createDecoder(AnimatedFileDrawable.this.path.getAbsolutePath(), AnimatedFileDrawable.this.metaData, AnimatedFileDrawable.this.currentAccount, AnimatedFileDrawable.this.streamFileSize, AnimatedFileDrawable.this.stream);
                    AnimatedFileDrawable.this.decoderCreated = true;
                }
                try {
                    if (AnimatedFileDrawable.this.nativePtr != 0 || AnimatedFileDrawable.this.metaData[0] == 0 || AnimatedFileDrawable.this.metaData[1] == 0) {
                        if (AnimatedFileDrawable.this.backgroundBitmap == null && AnimatedFileDrawable.this.metaData[0] > 0 && AnimatedFileDrawable.this.metaData[1] > 0) {
                            AnimatedFileDrawable.this.backgroundBitmap = Bitmap.createBitmap(AnimatedFileDrawable.this.metaData[0], AnimatedFileDrawable.this.metaData[1], Config.ARGB_8888);
                            if (!(AnimatedFileDrawable.this.backgroundShader != null || AnimatedFileDrawable.this.backgroundBitmap == null || AnimatedFileDrawable.this.roundRadius == 0)) {
                                AnimatedFileDrawable.this.backgroundShader = new BitmapShader(AnimatedFileDrawable.this.backgroundBitmap, TileMode.CLAMP, TileMode.CLAMP);
                            }
                        }
                        boolean seekWas = false;
                        if (AnimatedFileDrawable.this.pendingSeekTo >= 0) {
                            AnimatedFileDrawable.this.metaData[3] = (int) AnimatedFileDrawable.this.pendingSeekTo;
                            long seekTo = AnimatedFileDrawable.this.pendingSeekTo;
                            synchronized (AnimatedFileDrawable.this.sync) {
                                AnimatedFileDrawable.this.pendingSeekTo = -1;
                            }
                            seekWas = true;
                            AnimatedFileDrawable.this.stream.reset();
                            AnimatedFileDrawable.seekToMs(AnimatedFileDrawable.this.nativePtr, seekTo);
                        }
                        if (AnimatedFileDrawable.this.backgroundBitmap != null) {
                            AnimatedFileDrawable.this.lastFrameDecodeTime = System.currentTimeMillis();
                            if (AnimatedFileDrawable.getVideoFrame(AnimatedFileDrawable.this.nativePtr, AnimatedFileDrawable.this.backgroundBitmap, AnimatedFileDrawable.this.metaData, AnimatedFileDrawable.this.backgroundBitmap.getRowBytes()) == 0) {
                                AndroidUtilities.runOnUIThread(AnimatedFileDrawable.this.uiRunnableNoFrame);
                                return;
                            }
                            if (seekWas) {
                                AnimatedFileDrawable.this.lastTimeStamp = AnimatedFileDrawable.this.metaData[3];
                            }
                            AnimatedFileDrawable.this.backgroundBitmapTime = AnimatedFileDrawable.this.metaData[3];
                        }
                    } else {
                        AndroidUtilities.runOnUIThread(AnimatedFileDrawable.this.uiRunnableNoFrame);
                        return;
                    }
                } catch (Throwable e) {
                    FileLog.e(e);
                }
            }
            AndroidUtilities.runOnUIThread(AnimatedFileDrawable.this.uiRunnable);
        }
    };
    private Runnable loadFrameTask;
    protected final Runnable mInvalidateTask = new AnimatedFileDrawable$$Lambda$0(this);
    private final Runnable mStartTask = new AnimatedFileDrawable$$Lambda$1(this);
    private final int[] metaData = new int[5];
    public volatile long nativePtr;
    private Bitmap nextRenderingBitmap;
    private int nextRenderingBitmapTime;
    private BitmapShader nextRenderingShader;
    private View parentView = null;
    private File path;
    private boolean pendingRemoveLoading;
    private int pendingRemoveLoadingFramesReset;
    private volatile long pendingSeekTo = -1;
    private boolean recycleWithSecond;
    private Bitmap renderingBitmap;
    private int renderingBitmapTime;
    private BitmapShader renderingShader;
    private int roundRadius;
    private RectF roundRect = new RectF();
    private float scaleX = 1.0f;
    private float scaleY = 1.0f;
    private View secondParentView = null;
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
                    return;
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
            AnimatedFileDrawable.this.nextRenderingBitmap = AnimatedFileDrawable.this.backgroundBitmap;
            AnimatedFileDrawable.this.nextRenderingBitmapTime = AnimatedFileDrawable.this.backgroundBitmapTime;
            AnimatedFileDrawable.this.nextRenderingShader = AnimatedFileDrawable.this.backgroundShader;
            if (AnimatedFileDrawable.this.metaData[3] < AnimatedFileDrawable.this.lastTimeStamp) {
                AnimatedFileDrawable.this.lastTimeStamp = 0;
            }
            if (AnimatedFileDrawable.this.metaData[3] - AnimatedFileDrawable.this.lastTimeStamp != 0) {
                AnimatedFileDrawable.this.invalidateAfter = AnimatedFileDrawable.this.metaData[3] - AnimatedFileDrawable.this.lastTimeStamp;
                if (AnimatedFileDrawable.this.invalidateAfter > 1000) {
                    AnimatedFileDrawable.this.invalidateAfter = 0;
                }
            }
            AnimatedFileDrawable.this.lastTimeStamp = AnimatedFileDrawable.this.metaData[3];
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
                    return;
                }
                return;
            }
            AnimatedFileDrawable.this.loadFrameTask = null;
            AnimatedFileDrawable.this.scheduleNextGetFrame();
        }
    };

    private static native long createDecoder(String str, int[] iArr, int i, long j, Object obj);

    private static native void destroyDecoder(long j);

    private static native int getVideoFrame(long j, Bitmap bitmap, int[] iArr, int i);

    private static native void prepareToSeek(long j);

    private static native void seekToMs(long j, long j2);

    private static native void stopDecoder(long j);

    final /* synthetic */ void lambda$new$0$AnimatedFileDrawable() {
        if (this.secondParentView != null) {
            this.secondParentView.invalidate();
        } else if (this.parentView != null) {
            this.parentView.invalidate();
        }
    }

    final /* synthetic */ void lambda$new$1$AnimatedFileDrawable() {
        if (this.secondParentView != null) {
            this.secondParentView.invalidate();
        } else if (this.parentView != null) {
            this.parentView.invalidate();
        }
    }

    public AnimatedFileDrawable(File file, boolean createDecoder, long streamSize, Document document, Object parentObject, int account) {
        this.path = file;
        this.streamFileSize = streamSize;
        this.currentAccount = account;
        if (!(streamSize == 0 || document == null)) {
            this.stream = new AnimatedFileDrawableStream(document, parentObject, account);
        }
        if (createDecoder) {
            this.nativePtr = createDecoder(file.getAbsolutePath(), this.metaData, this.currentAccount, this.streamFileSize, this.stream);
            this.decoderCreated = true;
        }
    }

    public void setParentView(View view) {
        if (this.parentView == null) {
            this.parentView = view;
        }
    }

    public void setSecondParentView(View view) {
        this.secondParentView = view;
        if (view == null && this.recycleWithSecond) {
            recycle();
        }
    }

    public void setAllowDecodeSingleFrame(boolean value) {
        this.decodeSingleFrame = value;
        if (this.decodeSingleFrame) {
            scheduleNextGetFrame();
        }
    }

    public void seekTo(long ms, boolean removeLoading) {
        synchronized (this.sync) {
            this.pendingSeekTo = ms;
            prepareToSeek(this.nativePtr);
            if (this.decoderCreated && this.stream != null) {
                this.stream.cancel(removeLoading);
                this.pendingRemoveLoading = removeLoading;
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
            if (this.renderingBitmap != null) {
                this.renderingBitmap.recycle();
                this.renderingBitmap = null;
            }
            if (this.nextRenderingBitmap != null) {
                this.nextRenderingBitmap.recycle();
                this.nextRenderingBitmap = null;
            }
            if (this.decodeQueue != null) {
                this.decodeQueue.recycle();
                this.decodeQueue = null;
            }
        } else {
            this.destroyWhenDone = true;
        }
        if (this.stream != null) {
            this.stream.cancel(true);
        }
    }

    protected static void runOnUiThread(Runnable task) {
        if (Looper.myLooper() == uiHandler.getLooper()) {
            task.run();
        } else {
            uiHandler.post(task);
        }
    }

    protected void finalize() throws Throwable {
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
        if (this.pendingSeekTo >= 0) {
            return ((float) this.pendingSeekTo) / ((float) this.metaData[4]);
        }
        return ((float) this.metaData[3]) / ((float) this.metaData[4]);
    }

    public int getCurrentProgressMs() {
        return this.nextRenderingBitmapTime != 0 ? this.nextRenderingBitmapTime : this.renderingBitmapTime;
    }

    public int getDurationMs() {
        return this.metaData[4];
    }

    private void scheduleNextGetFrame() {
        if (this.loadFrameTask != null) {
            return;
        }
        if ((this.nativePtr != 0 || !this.decoderCreated) && !this.destroyWhenDone) {
            if (!this.isRunning) {
                if (!this.decodeSingleFrame) {
                    return;
                }
                if (this.decodeSingleFrame && this.singleFrameDecoded) {
                    return;
                }
            }
            long ms = 0;
            if (this.lastFrameDecodeTime != 0) {
                ms = Math.min((long) this.invalidateAfter, Math.max(0, ((long) this.invalidateAfter) - (System.currentTimeMillis() - this.lastFrameDecodeTime)));
            }
            Runnable runnable;
            if (this.streamFileSize != 0) {
                if (this.decodeQueue == null) {
                    this.decodeQueue = new DispatchQueue("decodeQueue" + this);
                }
                DispatchQueue dispatchQueue = this.decodeQueue;
                runnable = this.loadFrameRunnable;
                this.loadFrameTask = runnable;
                dispatchQueue.postRunnable(runnable, ms);
                return;
            }
            ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = executor;
            runnable = this.loadFrameRunnable;
            this.loadFrameTask = runnable;
            scheduledThreadPoolExecutor.schedule(runnable, ms, TimeUnit.MILLISECONDS);
        }
    }

    public void stop() {
        this.isRunning = false;
    }

    public boolean isRunning() {
        return this.isRunning;
    }

    public int getIntrinsicHeight() {
        int height = 0;
        if (this.decoderCreated) {
            height = (this.metaData[2] == 90 || this.metaData[2] == 270) ? this.metaData[0] : this.metaData[1];
        }
        if (height == 0) {
            return AndroidUtilities.dp(100.0f);
        }
        return height;
    }

    public int getIntrinsicWidth() {
        int width = 0;
        if (this.decoderCreated) {
            width = (this.metaData[2] == 90 || this.metaData[2] == 270) ? this.metaData[1] : this.metaData[0];
        }
        if (width == 0) {
            return AndroidUtilities.dp(100.0f);
        }
        return width;
    }

    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        this.applyTransformation = true;
    }

    public void draw(Canvas canvas) {
        if ((this.nativePtr != 0 || !this.decoderCreated) && !this.destroyWhenDone) {
            long now = System.currentTimeMillis();
            if (this.isRunning) {
                if (this.renderingBitmap == null && this.nextRenderingBitmap == null) {
                    scheduleNextGetFrame();
                } else if (this.nextRenderingBitmap != null && (this.renderingBitmap == null || Math.abs(now - this.lastFrameTime) >= ((long) this.invalidateAfter))) {
                    this.renderingBitmap = this.nextRenderingBitmap;
                    this.renderingBitmapTime = this.nextRenderingBitmapTime;
                    this.renderingShader = this.nextRenderingShader;
                    this.nextRenderingBitmap = null;
                    this.nextRenderingBitmapTime = 0;
                    this.nextRenderingShader = null;
                    this.lastFrameTime = now;
                }
            } else if (!this.isRunning && this.decodeSingleFrame && Math.abs(now - this.lastFrameTime) >= ((long) this.invalidateAfter) && this.nextRenderingBitmap != null) {
                this.renderingBitmap = this.nextRenderingBitmap;
                this.renderingBitmapTime = this.nextRenderingBitmapTime;
                this.renderingShader = this.nextRenderingShader;
                this.nextRenderingBitmap = null;
                this.nextRenderingBitmapTime = 0;
                this.nextRenderingShader = null;
                this.lastFrameTime = now;
            }
            if (this.renderingBitmap != null) {
                if (this.applyTransformation) {
                    int bitmapW = this.renderingBitmap.getWidth();
                    int bitmapH = this.renderingBitmap.getHeight();
                    if (this.metaData[2] == 90 || this.metaData[2] == 270) {
                        int temp = bitmapW;
                        bitmapW = bitmapH;
                        bitmapH = temp;
                    }
                    this.dstRect.set(getBounds());
                    this.scaleX = ((float) this.dstRect.width()) / ((float) bitmapW);
                    this.scaleY = ((float) this.dstRect.height()) / ((float) bitmapH);
                    this.applyTransformation = false;
                }
                if (this.roundRadius != 0) {
                    float scale = Math.max(this.scaleX, this.scaleY);
                    if (this.renderingShader == null) {
                        this.renderingShader = new BitmapShader(this.backgroundBitmap, TileMode.CLAMP, TileMode.CLAMP);
                    }
                    getPaint().setShader(this.renderingShader);
                    this.roundRect.set(this.dstRect);
                    this.shaderMatrix.reset();
                    this.bitmapRect.set(0.0f, 0.0f, (float) this.renderingBitmap.getWidth(), (float) this.renderingBitmap.getHeight());
                    AndroidUtilities.setRectToRect(this.shaderMatrix, this.bitmapRect, this.roundRect, this.metaData[2], true);
                    this.renderingShader.setLocalMatrix(this.shaderMatrix);
                    canvas.drawRoundRect(this.actualDrawRect, (float) this.roundRadius, (float) this.roundRadius, getPaint());
                } else {
                    canvas.translate((float) this.dstRect.left, (float) this.dstRect.top);
                    if (this.metaData[2] == 90) {
                        canvas.rotate(90.0f);
                        canvas.translate(0.0f, (float) (-this.dstRect.width()));
                    } else if (this.metaData[2] == 180) {
                        canvas.rotate(180.0f);
                        canvas.translate((float) (-this.dstRect.width()), (float) (-this.dstRect.height()));
                    } else if (this.metaData[2] == 270) {
                        canvas.rotate(270.0f);
                        canvas.translate((float) (-this.dstRect.height()), 0.0f);
                    }
                    canvas.scale(this.scaleX, this.scaleY);
                    canvas.drawBitmap(this.renderingBitmap, 0.0f, 0.0f, getPaint());
                }
                if (this.isRunning) {
                    long timeToNextFrame = Math.max(1, (((long) this.invalidateAfter) - (now - this.lastFrameTime)) - 17);
                    uiHandler.removeCallbacks(this.mInvalidateTask);
                    uiHandler.postDelayed(this.mInvalidateTask, Math.min(timeToNextFrame, (long) this.invalidateAfter));
                }
            }
        }
    }

    public int getMinimumHeight() {
        int height = 0;
        if (this.decoderCreated) {
            height = (this.metaData[2] == 90 || this.metaData[2] == 270) ? this.metaData[0] : this.metaData[1];
        }
        if (height == 0) {
            return AndroidUtilities.dp(100.0f);
        }
        return height;
    }

    public int getMinimumWidth() {
        int width = 0;
        if (this.decoderCreated) {
            width = (this.metaData[2] == 90 || this.metaData[2] == 270) ? this.metaData[1] : this.metaData[0];
        }
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
        if (this.renderingBitmap != null) {
            return this.renderingBitmap;
        }
        if (this.nextRenderingBitmap != null) {
            return this.nextRenderingBitmap;
        }
        return null;
    }

    public void setActualDrawRect(int x, int y, int width, int height) {
        this.actualDrawRect.set((float) x, (float) y, (float) (x + width), (float) (y + height));
    }

    public void setRoundRadius(int value) {
        this.roundRadius = value;
        getPaint().setFlags(1);
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
            drawable = new AnimatedFileDrawable(this.path, false, this.streamFileSize, this.stream.getDocument(), this.stream.getParentObject(), this.currentAccount);
        } else {
            drawable = new AnimatedFileDrawable(this.path, false, this.streamFileSize, null, null, this.currentAccount);
        }
        drawable.metaData[0] = this.metaData[0];
        drawable.metaData[1] = this.metaData[1];
        return drawable;
    }
}
