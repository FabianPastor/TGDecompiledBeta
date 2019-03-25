package org.telegram.ui.Components;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import java.io.File;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.AnimatedFileDrawableStream;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLoader;
import org.telegram.tgnet.TLRPC.Document;

public class AnimatedFileDrawable extends BitmapDrawable implements Animatable {
    public static final int PARAM_NUM_AUDIO_FRAME_SIZE = 5;
    public static final int PARAM_NUM_BITRATE = 3;
    public static final int PARAM_NUM_COUNT = 9;
    public static final int PARAM_NUM_DURATION = 4;
    public static final int PARAM_NUM_FRAMERATE = 7;
    public static final int PARAM_NUM_HEIGHT = 2;
    public static final int PARAM_NUM_IS_AVC = 0;
    public static final int PARAM_NUM_ROTATION = 8;
    public static final int PARAM_NUM_VIDEO_FRAME_SIZE = 6;
    public static final int PARAM_NUM_WIDTH = 1;
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
        /* JADX WARNING: No exception handlers in catch block: Catch:{  } */
        public void run() {
            /*
            r14 = this;
            r12 = 0;
            r11 = 1;
            r1 = org.telegram.ui.Components.AnimatedFileDrawable.this;
            r1 = r1.isRecycled;
            if (r1 != 0) goto L_0x015d;
        L_0x000b:
            r1 = org.telegram.ui.Components.AnimatedFileDrawable.this;
            r1 = r1.decoderCreated;
            if (r1 != 0) goto L_0x004a;
        L_0x0013:
            r1 = org.telegram.ui.Components.AnimatedFileDrawable.this;
            r2 = r1.nativePtr;
            r1 = (r2 > r12 ? 1 : (r2 == r12 ? 0 : -1));
            if (r1 != 0) goto L_0x004a;
        L_0x001b:
            r10 = org.telegram.ui.Components.AnimatedFileDrawable.this;
            r1 = org.telegram.ui.Components.AnimatedFileDrawable.this;
            r1 = r1.path;
            r1 = r1.getAbsolutePath();
            r2 = org.telegram.ui.Components.AnimatedFileDrawable.this;
            r2 = r2.metaData;
            r3 = org.telegram.ui.Components.AnimatedFileDrawable.this;
            r3 = r3.currentAccount;
            r4 = org.telegram.ui.Components.AnimatedFileDrawable.this;
            r4 = r4.streamFileSize;
            r6 = org.telegram.ui.Components.AnimatedFileDrawable.this;
            r6 = r6.stream;
            r2 = org.telegram.ui.Components.AnimatedFileDrawable.createDecoder(r1, r2, r3, r4, r6);
            r10.nativePtr = r2;
            r1 = org.telegram.ui.Components.AnimatedFileDrawable.this;
            r1.decoderCreated = r11;
        L_0x004a:
            r1 = org.telegram.ui.Components.AnimatedFileDrawable.this;	 Catch:{ Throwable -> 0x0159 }
            r2 = r1.nativePtr;	 Catch:{ Throwable -> 0x0159 }
            r1 = (r2 > r12 ? 1 : (r2 == r12 ? 0 : -1));
            if (r1 != 0) goto L_0x0068;
        L_0x0052:
            r1 = org.telegram.ui.Components.AnimatedFileDrawable.this;	 Catch:{ Throwable -> 0x0159 }
            r1 = r1.metaData;	 Catch:{ Throwable -> 0x0159 }
            r2 = 0;
            r1 = r1[r2];	 Catch:{ Throwable -> 0x0159 }
            if (r1 == 0) goto L_0x0068;
        L_0x005d:
            r1 = org.telegram.ui.Components.AnimatedFileDrawable.this;	 Catch:{ Throwable -> 0x0159 }
            r1 = r1.metaData;	 Catch:{ Throwable -> 0x0159 }
            r2 = 1;
            r1 = r1[r2];	 Catch:{ Throwable -> 0x0159 }
            if (r1 != 0) goto L_0x0189;
        L_0x0068:
            r1 = org.telegram.ui.Components.AnimatedFileDrawable.this;	 Catch:{ Throwable -> 0x0159 }
            r1 = r1.backgroundBitmap;	 Catch:{ Throwable -> 0x0159 }
            if (r1 != 0) goto L_0x00cf;
        L_0x0070:
            r1 = org.telegram.ui.Components.AnimatedFileDrawable.this;	 Catch:{ Throwable -> 0x0159 }
            r1 = r1.metaData;	 Catch:{ Throwable -> 0x0159 }
            r2 = 0;
            r1 = r1[r2];	 Catch:{ Throwable -> 0x0159 }
            if (r1 <= 0) goto L_0x00cf;
        L_0x007b:
            r1 = org.telegram.ui.Components.AnimatedFileDrawable.this;	 Catch:{ Throwable -> 0x0159 }
            r1 = r1.metaData;	 Catch:{ Throwable -> 0x0159 }
            r2 = 1;
            r1 = r1[r2];	 Catch:{ Throwable -> 0x0159 }
            if (r1 <= 0) goto L_0x00cf;
        L_0x0086:
            r1 = org.telegram.ui.Components.AnimatedFileDrawable.this;	 Catch:{ Throwable -> 0x0153 }
            r2 = org.telegram.ui.Components.AnimatedFileDrawable.this;	 Catch:{ Throwable -> 0x0153 }
            r2 = r2.metaData;	 Catch:{ Throwable -> 0x0153 }
            r3 = 0;
            r2 = r2[r3];	 Catch:{ Throwable -> 0x0153 }
            r3 = org.telegram.ui.Components.AnimatedFileDrawable.this;	 Catch:{ Throwable -> 0x0153 }
            r3 = r3.metaData;	 Catch:{ Throwable -> 0x0153 }
            r4 = 1;
            r3 = r3[r4];	 Catch:{ Throwable -> 0x0153 }
            r4 = android.graphics.Bitmap.Config.ARGB_8888;	 Catch:{ Throwable -> 0x0153 }
            r2 = android.graphics.Bitmap.createBitmap(r2, r3, r4);	 Catch:{ Throwable -> 0x0153 }
            r1.backgroundBitmap = r2;	 Catch:{ Throwable -> 0x0153 }
        L_0x00a3:
            r1 = org.telegram.ui.Components.AnimatedFileDrawable.this;	 Catch:{ Throwable -> 0x0159 }
            r1 = r1.backgroundShader;	 Catch:{ Throwable -> 0x0159 }
            if (r1 != 0) goto L_0x00cf;
        L_0x00ab:
            r1 = org.telegram.ui.Components.AnimatedFileDrawable.this;	 Catch:{ Throwable -> 0x0159 }
            r1 = r1.backgroundBitmap;	 Catch:{ Throwable -> 0x0159 }
            if (r1 == 0) goto L_0x00cf;
        L_0x00b3:
            r1 = org.telegram.ui.Components.AnimatedFileDrawable.this;	 Catch:{ Throwable -> 0x0159 }
            r1 = r1.roundRadius;	 Catch:{ Throwable -> 0x0159 }
            if (r1 == 0) goto L_0x00cf;
        L_0x00bb:
            r1 = org.telegram.ui.Components.AnimatedFileDrawable.this;	 Catch:{ Throwable -> 0x0159 }
            r2 = new android.graphics.BitmapShader;	 Catch:{ Throwable -> 0x0159 }
            r3 = org.telegram.ui.Components.AnimatedFileDrawable.this;	 Catch:{ Throwable -> 0x0159 }
            r3 = r3.backgroundBitmap;	 Catch:{ Throwable -> 0x0159 }
            r4 = android.graphics.Shader.TileMode.CLAMP;	 Catch:{ Throwable -> 0x0159 }
            r5 = android.graphics.Shader.TileMode.CLAMP;	 Catch:{ Throwable -> 0x0159 }
            r2.<init>(r3, r4, r5);	 Catch:{ Throwable -> 0x0159 }
            r1.backgroundShader = r2;	 Catch:{ Throwable -> 0x0159 }
        L_0x00cf:
            r7 = 0;
            r1 = org.telegram.ui.Components.AnimatedFileDrawable.this;	 Catch:{ Throwable -> 0x0159 }
            r2 = r1.pendingSeekTo;	 Catch:{ Throwable -> 0x0159 }
            r1 = (r2 > r12 ? 1 : (r2 == r12 ? 0 : -1));
            if (r1 < 0) goto L_0x0118;
        L_0x00da:
            r1 = org.telegram.ui.Components.AnimatedFileDrawable.this;	 Catch:{ Throwable -> 0x0159 }
            r1 = r1.metaData;	 Catch:{ Throwable -> 0x0159 }
            r2 = 3;
            r3 = org.telegram.ui.Components.AnimatedFileDrawable.this;	 Catch:{ Throwable -> 0x0159 }
            r4 = r3.pendingSeekTo;	 Catch:{ Throwable -> 0x0159 }
            r3 = (int) r4;	 Catch:{ Throwable -> 0x0159 }
            r1[r2] = r3;	 Catch:{ Throwable -> 0x0159 }
            r1 = org.telegram.ui.Components.AnimatedFileDrawable.this;	 Catch:{ Throwable -> 0x0159 }
            r8 = r1.pendingSeekTo;	 Catch:{ Throwable -> 0x0159 }
            r1 = org.telegram.ui.Components.AnimatedFileDrawable.this;	 Catch:{ Throwable -> 0x0159 }
            r2 = r1.sync;	 Catch:{ Throwable -> 0x0159 }
            monitor-enter(r2);	 Catch:{ Throwable -> 0x0159 }
            r1 = org.telegram.ui.Components.AnimatedFileDrawable.this;	 Catch:{ all -> 0x0167 }
            r4 = -1;
            r1.pendingSeekTo = r4;	 Catch:{ all -> 0x0167 }
            monitor-exit(r2);	 Catch:{ all -> 0x0167 }
            r7 = 1;
            r1 = org.telegram.ui.Components.AnimatedFileDrawable.this;	 Catch:{ Throwable -> 0x0159 }
            r1 = r1.stream;	 Catch:{ Throwable -> 0x0159 }
            if (r1 == 0) goto L_0x0111;
        L_0x0108:
            r1 = org.telegram.ui.Components.AnimatedFileDrawable.this;	 Catch:{ Throwable -> 0x0159 }
            r1 = r1.stream;	 Catch:{ Throwable -> 0x0159 }
            r1.reset();	 Catch:{ Throwable -> 0x0159 }
        L_0x0111:
            r1 = org.telegram.ui.Components.AnimatedFileDrawable.this;	 Catch:{ Throwable -> 0x0159 }
            r2 = r1.nativePtr;	 Catch:{ Throwable -> 0x0159 }
            org.telegram.ui.Components.AnimatedFileDrawable.seekToMs(r2, r8);	 Catch:{ Throwable -> 0x0159 }
        L_0x0118:
            r1 = org.telegram.ui.Components.AnimatedFileDrawable.this;	 Catch:{ Throwable -> 0x0159 }
            r1 = r1.backgroundBitmap;	 Catch:{ Throwable -> 0x0159 }
            if (r1 == 0) goto L_0x015d;
        L_0x0120:
            r1 = org.telegram.ui.Components.AnimatedFileDrawable.this;	 Catch:{ Throwable -> 0x0159 }
            r2 = java.lang.System.currentTimeMillis();	 Catch:{ Throwable -> 0x0159 }
            r1.lastFrameDecodeTime = r2;	 Catch:{ Throwable -> 0x0159 }
            r1 = org.telegram.ui.Components.AnimatedFileDrawable.this;	 Catch:{ Throwable -> 0x0159 }
            r2 = r1.nativePtr;	 Catch:{ Throwable -> 0x0159 }
            r1 = org.telegram.ui.Components.AnimatedFileDrawable.this;	 Catch:{ Throwable -> 0x0159 }
            r1 = r1.backgroundBitmap;	 Catch:{ Throwable -> 0x0159 }
            r4 = org.telegram.ui.Components.AnimatedFileDrawable.this;	 Catch:{ Throwable -> 0x0159 }
            r4 = r4.metaData;	 Catch:{ Throwable -> 0x0159 }
            r5 = org.telegram.ui.Components.AnimatedFileDrawable.this;	 Catch:{ Throwable -> 0x0159 }
            r5 = r5.backgroundBitmap;	 Catch:{ Throwable -> 0x0159 }
            r5 = r5.getRowBytes();	 Catch:{ Throwable -> 0x0159 }
            r1 = org.telegram.ui.Components.AnimatedFileDrawable.getVideoFrame(r2, r1, r4, r5);	 Catch:{ Throwable -> 0x0159 }
            if (r1 != 0) goto L_0x016a;
        L_0x0149:
            r1 = org.telegram.ui.Components.AnimatedFileDrawable.this;	 Catch:{ Throwable -> 0x0159 }
            r1 = r1.uiRunnableNoFrame;	 Catch:{ Throwable -> 0x0159 }
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r1);	 Catch:{ Throwable -> 0x0159 }
        L_0x0152:
            return;
        L_0x0153:
            r0 = move-exception;
            org.telegram.messenger.FileLog.e(r0);	 Catch:{ Throwable -> 0x0159 }
            goto L_0x00a3;
        L_0x0159:
            r0 = move-exception;
            org.telegram.messenger.FileLog.e(r0);
        L_0x015d:
            r1 = org.telegram.ui.Components.AnimatedFileDrawable.this;
            r1 = r1.uiRunnable;
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r1);
            goto L_0x0152;
        L_0x0167:
            r1 = move-exception;
            monitor-exit(r2);	 Catch:{ all -> 0x0167 }
            throw r1;	 Catch:{ Throwable -> 0x0159 }
        L_0x016a:
            if (r7 == 0) goto L_0x017a;
        L_0x016c:
            r1 = org.telegram.ui.Components.AnimatedFileDrawable.this;	 Catch:{ Throwable -> 0x0159 }
            r2 = org.telegram.ui.Components.AnimatedFileDrawable.this;	 Catch:{ Throwable -> 0x0159 }
            r2 = r2.metaData;	 Catch:{ Throwable -> 0x0159 }
            r3 = 3;
            r2 = r2[r3];	 Catch:{ Throwable -> 0x0159 }
            r1.lastTimeStamp = r2;	 Catch:{ Throwable -> 0x0159 }
        L_0x017a:
            r1 = org.telegram.ui.Components.AnimatedFileDrawable.this;	 Catch:{ Throwable -> 0x0159 }
            r2 = org.telegram.ui.Components.AnimatedFileDrawable.this;	 Catch:{ Throwable -> 0x0159 }
            r2 = r2.metaData;	 Catch:{ Throwable -> 0x0159 }
            r3 = 3;
            r2 = r2[r3];	 Catch:{ Throwable -> 0x0159 }
            r1.backgroundBitmapTime = r2;	 Catch:{ Throwable -> 0x0159 }
            goto L_0x015d;
        L_0x0189:
            r1 = org.telegram.ui.Components.AnimatedFileDrawable.this;	 Catch:{ Throwable -> 0x0159 }
            r1 = r1.uiRunnableNoFrame;	 Catch:{ Throwable -> 0x0159 }
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r1);	 Catch:{ Throwable -> 0x0159 }
            goto L_0x0152;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.AnimatedFileDrawable$AnonymousClass3.run():void");
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
    private volatile long pendingSeekToUI = -1;
    private boolean recycleWithSecond;
    private Bitmap renderingBitmap;
    private int renderingBitmapTime;
    private BitmapShader renderingShader;
    private int roundRadius;
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
            }
            if (AnimatedFileDrawable.this.pendingSeekToUI >= 0 && AnimatedFileDrawable.this.pendingSeekTo == -1) {
                AnimatedFileDrawable.this.pendingSeekToUI = -1;
                AnimatedFileDrawable.this.invalidateAfter = 0;
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

    public static native void getVideoInfo(String str, int[] iArr);

    private static native void prepareToSeek(long j);

    private static native void seekToMs(long j, long j2);

    private static native void stopDecoder(long j);

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$new$0$AnimatedFileDrawable() {
        if (this.secondParentView != null) {
            this.secondParentView.invalidate();
        } else if (this.parentView != null) {
            this.parentView.invalidate();
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$new$1$AnimatedFileDrawable() {
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
        getPaint().setFlags(2);
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
            this.pendingSeekToUI = ms;
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

    /* Access modifiers changed, original: protected */
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
        return ((float) this.metaData[3]) / ((float) this.metaData[4]);
    }

    public int getCurrentProgressMs() {
        if (this.pendingSeekToUI >= 0) {
            return (int) this.pendingSeekToUI;
        }
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
            if (this.decodeQueue == null) {
                this.decodeQueue = new DispatchQueue("decodeQueue" + this);
            }
            DispatchQueue dispatchQueue = this.decodeQueue;
            Runnable runnable = this.loadFrameRunnable;
            this.loadFrameTask = runnable;
            dispatchQueue.postRunnable(runnable, ms);
        }
    }

    public boolean isLoadingStream() {
        return this.stream != null && this.stream.isWaitingForLoad();
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

    /* Access modifiers changed, original: protected */
    public void onBoundsChange(Rect bounds) {
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
                    Paint paint = getPaint();
                    paint.setShader(this.renderingShader);
                    this.shaderMatrix.reset();
                    this.shaderMatrix.setTranslate((float) this.dstRect.left, (float) this.dstRect.top);
                    if (this.metaData[2] == 90) {
                        this.shaderMatrix.preRotate(90.0f);
                        this.shaderMatrix.preTranslate(0.0f, (float) (-this.dstRect.width()));
                    } else if (this.metaData[2] == 180) {
                        this.shaderMatrix.preRotate(180.0f);
                        this.shaderMatrix.preTranslate((float) (-this.dstRect.width()), (float) (-this.dstRect.height()));
                    } else if (this.metaData[2] == 270) {
                        this.shaderMatrix.preRotate(270.0f);
                        this.shaderMatrix.preTranslate((float) (-this.dstRect.height()), 0.0f);
                    }
                    this.shaderMatrix.preScale(this.scaleX, this.scaleY);
                    this.renderingShader.setLocalMatrix(this.shaderMatrix);
                    canvas.drawRoundRect(this.actualDrawRect, (float) this.roundRadius, (float) this.roundRadius, paint);
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
        getPaint().setFlags(3);
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
