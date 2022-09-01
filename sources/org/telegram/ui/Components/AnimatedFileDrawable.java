package org.telegram.ui.Components;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.view.View;
import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.AnimatedFileDrawableStream;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.utils.BitmapsCache;
import org.telegram.tgnet.TLRPC$Document;

public class AnimatedFileDrawable extends BitmapDrawable implements Animatable, BitmapsCache.Cacheable {
    private static ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(8, new ThreadPoolExecutor.DiscardPolicy());
    private static float[] radii = new float[8];
    private RectF actualDrawRect;
    private boolean applyTransformation;
    /* access modifiers changed from: private */
    public Bitmap backgroundBitmap;
    /* access modifiers changed from: private */
    public int backgroundBitmapTime;
    private Paint backgroundPaint;
    /* access modifiers changed from: private */
    public BitmapShader backgroundShader;
    BitmapsCache bitmapsCache;
    Runnable cacheGenRunnable;
    long cacheGenerateNativePtr;
    long cacheGenerateTimestamp;
    BitmapsCache.Metadata cacheMetadata;
    private Runnable cancelCache;
    /* access modifiers changed from: private */
    public int currentAccount;
    private DispatchQueue decodeQueue;
    private boolean decodeSingleFrame;
    /* access modifiers changed from: private */
    public boolean decoderCreated;
    /* access modifiers changed from: private */
    public boolean destroyWhenDone;
    private final TLRPC$Document document;
    private final RectF dstRect;
    private RectF dstRectBackground;
    /* access modifiers changed from: private */
    public float endTime;
    /* access modifiers changed from: private */
    public boolean forceDecodeAfterNextFrame;
    boolean generatingCache;
    Bitmap generatingCacheBitmap;
    public boolean ignoreNoParent;
    /* access modifiers changed from: private */
    public int invalidateAfter;
    private boolean invalidateParentViewWithSecond;
    private boolean invalidatePath;
    /* access modifiers changed from: private */
    public volatile boolean isRecycled;
    /* access modifiers changed from: private */
    public boolean isRestarted;
    private volatile boolean isRunning;
    public boolean isWebmSticker;
    /* access modifiers changed from: private */
    public long lastFrameDecodeTime;
    private long lastFrameTime;
    int lastMetadata;
    /* access modifiers changed from: private */
    public int lastTimeStamp;
    /* access modifiers changed from: private */
    public boolean limitFps;
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
    private ArrayList<ImageReceiver> parents;
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
    private boolean precache;
    private boolean recycleWithSecond;
    private Bitmap renderingBitmap;
    private int renderingBitmapTime;
    /* access modifiers changed from: private */
    public int renderingHeight;
    private BitmapShader renderingShader;
    /* access modifiers changed from: private */
    public int renderingWidth;
    public int repeatCount;
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
    public boolean skipFrameUpdate;
    /* access modifiers changed from: private */
    public float startTime;
    /* access modifiers changed from: private */
    public AnimatedFileDrawableStream stream;
    /* access modifiers changed from: private */
    public long streamFileSize;
    /* access modifiers changed from: private */
    public final Object sync;
    int tryCount;
    /* access modifiers changed from: private */
    public Runnable uiRunnable;
    /* access modifiers changed from: private */
    public Runnable uiRunnableGenerateCache;
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

    public int getOpacity() {
        return -2;
    }

    static /* synthetic */ int access$1010(AnimatedFileDrawable animatedFileDrawable) {
        int i = animatedFileDrawable.pendingRemoveLoadingFramesReset;
        animatedFileDrawable.pendingRemoveLoadingFramesReset = i - 1;
        return i;
    }

    /* access modifiers changed from: private */
    public void chekDestroyDecoder() {
        if (this.loadFrameRunnable == null && this.destroyWhenDone && this.nativePtr != 0 && !this.generatingCache) {
            destroyDecoder(this.nativePtr);
            this.nativePtr = 0;
        }
        if (!canLoadFrames()) {
            Bitmap bitmap = this.renderingBitmap;
            if (bitmap != null) {
                bitmap.recycle();
                this.renderingBitmap = null;
            }
            Bitmap bitmap2 = this.backgroundBitmap;
            if (bitmap2 != null) {
                bitmap2.recycle();
                this.backgroundBitmap = null;
            }
            DispatchQueue dispatchQueue = this.decodeQueue;
            if (dispatchQueue != null) {
                dispatchQueue.recycle();
                this.decodeQueue = null;
            }
            invalidateInternal();
        }
    }

    /* access modifiers changed from: private */
    public void invalidateInternal() {
        for (int i = 0; i < this.parents.size(); i++) {
            this.parents.get(i).invalidate();
        }
    }

    public void checkRepeat() {
        if (this.ignoreNoParent) {
            start();
            return;
        }
        int i = 0;
        int i2 = 0;
        while (i < this.parents.size()) {
            ImageReceiver imageReceiver = this.parents.get(i);
            if (!imageReceiver.isAttachedToWindow()) {
                this.parents.remove(i);
                i--;
            }
            int i3 = imageReceiver.animatedFileDrawableRepeatMaxCount;
            if (i3 > 0 && this.repeatCount >= i3) {
                i2++;
            }
            i++;
        }
        if (this.parents.size() == i2) {
            stop();
        } else {
            start();
        }
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

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        View view;
        if (!this.secondParentViews.isEmpty()) {
            int size = this.secondParentViews.size();
            for (int i = 0; i < size; i++) {
                this.secondParentViews.get(i).invalidate();
            }
        }
        if ((this.secondParentViews.isEmpty() || this.invalidateParentViewWithSecond) && (view = this.parentView) != null) {
            view.invalidate();
        }
    }

    public AnimatedFileDrawable(File file, boolean z, long j, TLRPC$Document tLRPC$Document, ImageLocation imageLocation, Object obj, long j2, int i, boolean z2, BitmapsCache.CacheOptions cacheOptions) {
        this(file, z, j, tLRPC$Document, imageLocation, obj, j2, i, z2, 0, 0, cacheOptions);
    }

    /* JADX WARNING: Removed duplicated region for block: B:38:0x0154  */
    /* JADX WARNING: Removed duplicated region for block: B:40:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public AnimatedFileDrawable(java.io.File r18, boolean r19, long r20, org.telegram.tgnet.TLRPC$Document r22, org.telegram.messenger.ImageLocation r23, java.lang.Object r24, long r25, int r27, boolean r28, int r29, int r30, org.telegram.messenger.utils.BitmapsCache.CacheOptions r31) {
        /*
            r17 = this;
            r7 = r17
            r0 = r20
            r2 = r22
            r8 = r25
            r3 = r29
            r4 = r30
            r17.<init>()
            r5 = 50
            r7.invalidateAfter = r5
            r5 = 5
            int[] r6 = new int[r5]
            r7.metaData = r6
            r10 = -1
            r7.pendingSeekTo = r10
            r7.pendingSeekToUI = r10
            java.lang.Object r5 = new java.lang.Object
            r5.<init>()
            r7.sync = r5
            android.graphics.RectF r5 = new android.graphics.RectF
            r5.<init>()
            r7.actualDrawRect = r5
            r5 = 4
            int[] r5 = new int[r5]
            r7.roundRadius = r5
            android.graphics.Matrix r5 = new android.graphics.Matrix
            r5.<init>()
            r7.shaderMatrix = r5
            android.graphics.Path r5 = new android.graphics.Path
            r5.<init>()
            r7.roundPath = r5
            r5 = 1065353216(0x3var_, float:1.0)
            r7.scaleX = r5
            r7.scaleY = r5
            android.graphics.RectF r10 = new android.graphics.RectF
            r10.<init>()
            r7.dstRect = r10
            r7.scaleFactor = r5
            java.util.ArrayList r5 = new java.util.ArrayList
            r5.<init>()
            r7.secondParentViews = r5
            java.util.ArrayList r5 = new java.util.ArrayList
            r5.<init>()
            r7.parents = r5
            r15 = 1
            r7.invalidatePath = r15
            org.telegram.ui.Components.AnimatedFileDrawable$1 r5 = new org.telegram.ui.Components.AnimatedFileDrawable$1
            r5.<init>()
            r7.uiRunnableNoFrame = r5
            org.telegram.ui.Components.AnimatedFileDrawable$2 r5 = new org.telegram.ui.Components.AnimatedFileDrawable$2
            r5.<init>()
            r7.uiRunnableGenerateCache = r5
            org.telegram.ui.Components.AnimatedFileDrawable$3 r5 = new org.telegram.ui.Components.AnimatedFileDrawable$3
            r5.<init>()
            r7.uiRunnable = r5
            org.telegram.ui.Components.AnimatedFileDrawable$4 r5 = new org.telegram.ui.Components.AnimatedFileDrawable$4
            r5.<init>()
            r7.loadFrameRunnable = r5
            org.telegram.ui.Components.AnimatedFileDrawable$$ExternalSyntheticLambda0 r5 = new org.telegram.ui.Components.AnimatedFileDrawable$$ExternalSyntheticLambda0
            r5.<init>(r7)
            r7.mStartTask = r5
            r13 = r18
            r7.path = r13
            r7.streamFileSize = r0
            r5 = r27
            r7.currentAccount = r5
            r7.renderingHeight = r4
            r7.renderingWidth = r3
            r14 = 0
            if (r31 == 0) goto L_0x0099
            if (r3 <= 0) goto L_0x0099
            if (r4 <= 0) goto L_0x0099
            r3 = 1
            goto L_0x009a
        L_0x0099:
            r3 = 0
        L_0x009a:
            r7.precache = r3
            r7.document = r2
            android.graphics.Paint r3 = r17.getPaint()
            r4 = 3
            r3.setFlags(r4)
            r11 = 0
            int r3 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1))
            if (r3 == 0) goto L_0x00c2
            if (r2 != 0) goto L_0x00b0
            if (r23 == 0) goto L_0x00c2
        L_0x00b0:
            org.telegram.messenger.AnimatedFileDrawableStream r10 = new org.telegram.messenger.AnimatedFileDrawableStream
            r0 = r10
            r1 = r22
            r2 = r23
            r3 = r24
            r4 = r27
            r5 = r28
            r0.<init>(r1, r2, r3, r4, r5)
            r7.stream = r10
        L_0x00c2:
            r0 = 3840(0xvar_, float:5.381E-42)
            if (r19 == 0) goto L_0x00fe
            boolean r1 = r7.precache
            if (r1 != 0) goto L_0x00fe
            java.lang.String r10 = r18.getAbsolutePath()
            int r1 = r7.currentAccount
            long r2 = r7.streamFileSize
            org.telegram.messenger.AnimatedFileDrawableStream r4 = r7.stream
            r8 = r11
            r11 = r6
            r12 = r1
            r5 = 0
            r13 = r2
            r1 = 1
            r15 = r4
            r16 = r28
            long r2 = createDecoder(r10, r11, r12, r13, r15, r16)
            r7.nativePtr = r2
            long r2 = r7.nativePtr
            int r4 = (r2 > r8 ? 1 : (r2 == r8 ? 0 : -1))
            if (r4 == 0) goto L_0x00f8
            r2 = r6[r5]
            if (r2 > r0) goto L_0x00f1
            r2 = r6[r1]
            if (r2 <= r0) goto L_0x00f8
        L_0x00f1:
            long r2 = r7.nativePtr
            destroyDecoder(r2)
            r7.nativePtr = r8
        L_0x00f8:
            r17.updateScaleFactor()
            r7.decoderCreated = r1
            goto L_0x0101
        L_0x00fe:
            r8 = r11
            r1 = 1
            r5 = 0
        L_0x0101:
            boolean r2 = r7.precache
            if (r2 == 0) goto L_0x014c
            java.lang.String r10 = r18.getAbsolutePath()
            int r12 = r7.currentAccount
            long r13 = r7.streamFileSize
            org.telegram.messenger.AnimatedFileDrawableStream r15 = r7.stream
            r11 = r6
            r16 = r28
            long r2 = createDecoder(r10, r11, r12, r13, r15, r16)
            r7.nativePtr = r2
            long r2 = r7.nativePtr
            int r4 = (r2 > r8 ? 1 : (r2 == r8 ? 0 : -1))
            if (r4 == 0) goto L_0x0132
            r2 = r6[r5]
            if (r2 > r0) goto L_0x0126
            r2 = r6[r1]
            if (r2 <= r0) goto L_0x0132
        L_0x0126:
            long r0 = r7.nativePtr
            destroyDecoder(r0)
            r7.nativePtr = r8
            r0 = r25
            r2 = r8
            r12 = 0
            goto L_0x0150
        L_0x0132:
            org.telegram.messenger.utils.BitmapsCache r10 = new org.telegram.messenger.utils.BitmapsCache
            int r4 = r7.renderingWidth
            int r6 = r7.renderingHeight
            boolean r0 = r7.limitFps
            r11 = r0 ^ 1
            r0 = r10
            r1 = r18
            r2 = r17
            r3 = r31
            r12 = 0
            r5 = r6
            r6 = r11
            r0.<init>(r1, r2, r3, r4, r5, r6)
            r7.bitmapsCache = r10
            goto L_0x014d
        L_0x014c:
            r12 = 0
        L_0x014d:
            r0 = r25
            r2 = r8
        L_0x0150:
            int r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r4 == 0) goto L_0x0157
            r7.seekTo(r0, r12)
        L_0x0157:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.AnimatedFileDrawable.<init>(java.io.File, boolean, long, org.telegram.tgnet.TLRPC$Document, org.telegram.messenger.ImageLocation, java.lang.Object, long, int, boolean, int, int, org.telegram.messenger.utils.BitmapsCache$CacheOptions):void");
    }

    public void setIsWebmSticker(boolean z) {
        this.isWebmSticker = z;
        if (z) {
            this.useSharedQueue = true;
        }
    }

    public Bitmap getFrameAtTime(long j) {
        return getFrameAtTime(j, false);
    }

    public Bitmap getFrameAtTime(long j, boolean z) {
        int i;
        if (!this.decoderCreated || this.nativePtr == 0) {
            return null;
        }
        AnimatedFileDrawableStream animatedFileDrawableStream = this.stream;
        if (animatedFileDrawableStream != null) {
            animatedFileDrawableStream.cancel(false);
            this.stream.reset();
        }
        if (!z) {
            seekToMs(this.nativePtr, j, z);
        }
        if (this.backgroundBitmap == null) {
            int[] iArr = this.metaData;
            float f = this.scaleFactor;
            this.backgroundBitmap = Bitmap.createBitmap((int) (((float) iArr[0]) * f), (int) (((float) iArr[1]) * f), Bitmap.Config.ARGB_8888);
        }
        if (z) {
            long j2 = this.nativePtr;
            Bitmap bitmap = this.backgroundBitmap;
            i = getFrameAtTime(j2, j, bitmap, this.metaData, bitmap.getRowBytes());
        } else {
            long j3 = this.nativePtr;
            Bitmap bitmap2 = this.backgroundBitmap;
            i = getVideoFrame(j3, bitmap2, this.metaData, bitmap2.getRowBytes(), true, 0.0f, 0.0f);
        }
        if (i != 0) {
            return this.backgroundBitmap;
        }
        return null;
    }

    public void setParentView(View view) {
        if (this.parentView == null) {
            this.parentView = view;
        }
    }

    public void addParent(ImageReceiver imageReceiver) {
        if (imageReceiver != null && !this.parents.contains(imageReceiver)) {
            this.parents.add(imageReceiver);
            if (this.isRunning) {
                scheduleNextGetFrame();
            }
        }
        checkCacheCancel();
    }

    public void removeParent(ImageReceiver imageReceiver) {
        this.parents.remove(imageReceiver);
        if (this.parents.size() == 0) {
            this.repeatCount = 0;
        }
        checkCacheCancel();
    }

    public void checkCacheCancel() {
        Runnable runnable;
        if (this.bitmapsCache != null) {
            boolean isEmpty = this.parents.isEmpty();
            if (isEmpty && this.cancelCache == null) {
                AnimatedFileDrawable$$ExternalSyntheticLambda1 animatedFileDrawable$$ExternalSyntheticLambda1 = new AnimatedFileDrawable$$ExternalSyntheticLambda1(this);
                this.cancelCache = animatedFileDrawable$$ExternalSyntheticLambda1;
                AndroidUtilities.runOnUIThread(animatedFileDrawable$$ExternalSyntheticLambda1, 600);
            } else if (!isEmpty && (runnable = this.cancelCache) != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
                this.cancelCache = null;
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$checkCacheCancel$1() {
        BitmapsCache bitmapsCache2 = this.bitmapsCache;
        if (bitmapsCache2 != null) {
            bitmapsCache2.cancelCreate();
        }
    }

    public void setInvalidateParentViewWithSecond(boolean z) {
        this.invalidateParentViewWithSecond = z;
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

    public void setAllowDecodeSingleFrame(boolean z) {
        this.decodeSingleFrame = z;
        if (z) {
            scheduleNextGetFrame();
        }
    }

    public void seekTo(long j, boolean z) {
        seekTo(j, z, false);
    }

    public void seekTo(long j, boolean z, boolean z2) {
        AnimatedFileDrawableStream animatedFileDrawableStream;
        synchronized (this.sync) {
            this.pendingSeekTo = j;
            this.pendingSeekToUI = j;
            if (this.nativePtr != 0) {
                prepareToSeek(this.nativePtr);
            }
            if (this.decoderCreated && (animatedFileDrawableStream = this.stream) != null) {
                animatedFileDrawableStream.cancel(z);
                this.pendingRemoveLoading = z;
                this.pendingRemoveLoadingFramesReset = z ? 0 : 10;
            }
            if (z2 && this.decodeSingleFrame) {
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
        Runnable runnable = this.cacheGenRunnable;
        if (runnable != null) {
            RLottieDrawable.lottieCacheGenerateQueue.cancelRunnable(runnable);
        }
        if (this.loadFrameTask == null) {
            if (this.nativePtr != 0) {
                destroyDecoder(this.nativePtr);
                this.nativePtr = 0;
            }
            ArrayList arrayList = new ArrayList();
            arrayList.add(this.renderingBitmap);
            arrayList.add(this.nextRenderingBitmap);
            if (this.renderingBitmap != null) {
                this.renderingBitmap = null;
            }
            if (this.nextRenderingBitmap != null) {
                this.nextRenderingBitmap = null;
            }
            DispatchQueue dispatchQueue = this.decodeQueue;
            if (dispatchQueue != null) {
                dispatchQueue.recycle();
                this.decodeQueue = null;
            }
            getPaint().setShader((Shader) null);
            AndroidUtilities.recycleBitmaps(arrayList);
        } else {
            this.destroyWhenDone = true;
        }
        AnimatedFileDrawableStream animatedFileDrawableStream = this.stream;
        if (animatedFileDrawableStream != null) {
            animatedFileDrawableStream.cancel(true);
        }
        invalidateInternal();
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

    public void setUseSharedQueue(boolean z) {
        if (!this.isWebmSticker) {
            this.useSharedQueue = z;
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
        if (this.parents.size() != 0 || this.ignoreNoParent) {
            this.isRunning = true;
            scheduleNextGetFrame();
            AndroidUtilities.runOnUIThread(this.mStartTask);
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
        if (this.loadFrameTask == null && canLoadFrames() && !this.destroyWhenDone) {
            if (!this.isRunning) {
                boolean z = this.decodeSingleFrame;
                if (!z) {
                    return;
                }
                if (z && this.singleFrameDecoded) {
                    return;
                }
            }
            if ((this.parents.size() != 0 || this.ignoreNoParent) && !this.generatingCache) {
                long j = 0;
                if (this.lastFrameDecodeTime != 0) {
                    int i = this.invalidateAfter;
                    j = Math.min((long) i, Math.max(0, ((long) i) - (System.currentTimeMillis() - this.lastFrameDecodeTime)));
                }
                if (this.useSharedQueue) {
                    ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = executor;
                    Runnable runnable = this.loadFrameRunnable;
                    this.loadFrameTask = runnable;
                    scheduledThreadPoolExecutor.schedule(runnable, j, TimeUnit.MILLISECONDS);
                    return;
                }
                if (this.decodeQueue == null) {
                    this.decodeQueue = new DispatchQueue("decodeQueue" + this);
                }
                DispatchQueue dispatchQueue = this.decodeQueue;
                Runnable runnable2 = this.loadFrameRunnable;
                this.loadFrameTask = runnable2;
                dispatchQueue.postRunnable(runnable2, j);
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
        if (i == 0) {
            return AndroidUtilities.dp(100.0f);
        }
        return (int) (((float) i) * this.scaleFactor);
    }

    public int getIntrinsicWidth() {
        int i = 0;
        if (this.decoderCreated) {
            int[] iArr = this.metaData;
            i = (iArr[2] == 90 || iArr[2] == 270) ? iArr[1] : iArr[0];
        }
        if (i == 0) {
            return AndroidUtilities.dp(100.0f);
        }
        return (int) (((float) i) * this.scaleFactor);
    }

    /* access modifiers changed from: protected */
    public void onBoundsChange(Rect rect) {
        super.onBoundsChange(rect);
        this.applyTransformation = true;
    }

    public void draw(Canvas canvas) {
        drawInternal(canvas, false, System.currentTimeMillis());
    }

    public void drawInBackground(Canvas canvas, float f, float f2, float f3, float f4, int i, ColorFilter colorFilter) {
        if (this.dstRectBackground == null) {
            this.dstRectBackground = new RectF();
            Paint paint = new Paint();
            this.backgroundPaint = paint;
            paint.setFilterBitmap(true);
        }
        this.backgroundPaint.setAlpha(i);
        this.backgroundPaint.setColorFilter(colorFilter);
        this.dstRectBackground.set(f, f2, f3 + f, f4 + f2);
        drawInternal(canvas, true, 0);
    }

    /* JADX WARNING: Removed duplicated region for block: B:38:0x00af  */
    /* JADX WARNING: Removed duplicated region for block: B:58:0x015c  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void drawInternal(android.graphics.Canvas r18, boolean r19, long r20) {
        /*
            r17 = this;
            r0 = r17
            r1 = r18
            boolean r2 = r17.canLoadFrames()
            if (r2 == 0) goto L_0x01a3
            boolean r2 = r0.destroyWhenDone
            if (r2 == 0) goto L_0x0010
            goto L_0x01a3
        L_0x0010:
            r2 = 0
            int r4 = (r20 > r2 ? 1 : (r20 == r2 ? 0 : -1))
            if (r4 != 0) goto L_0x001b
            long r2 = java.lang.System.currentTimeMillis()
            goto L_0x001d
        L_0x001b:
            r2 = r20
        L_0x001d:
            if (r19 == 0) goto L_0x0022
            android.graphics.RectF r4 = r0.dstRectBackground
            goto L_0x0024
        L_0x0022:
            android.graphics.RectF r4 = r0.dstRect
        L_0x0024:
            if (r19 == 0) goto L_0x0029
            android.graphics.Paint r5 = r0.backgroundPaint
            goto L_0x002d
        L_0x0029:
            android.graphics.Paint r5 = r17.getPaint()
        L_0x002d:
            r6 = 0
            if (r19 != 0) goto L_0x0033
            r0.updateCurrentFrame(r2, r6)
        L_0x0033:
            android.graphics.Bitmap r2 = r0.renderingBitmap
            if (r2 == 0) goto L_0x01a3
            float r3 = r0.scaleX
            float r7 = r0.scaleY
            r8 = 270(0x10e, float:3.78E-43)
            r9 = 90
            r10 = 2
            if (r19 == 0) goto L_0x006a
            int r2 = r2.getWidth()
            android.graphics.Bitmap r3 = r0.renderingBitmap
            int r3 = r3.getHeight()
            int[] r7 = r0.metaData
            r11 = r7[r10]
            if (r11 == r9) goto L_0x0056
            r7 = r7[r10]
            if (r7 != r8) goto L_0x005b
        L_0x0056:
            r16 = r3
            r3 = r2
            r2 = r16
        L_0x005b:
            float r7 = r4.width()
            float r2 = (float) r2
            float r2 = r7 / r2
            float r7 = r4.height()
            float r3 = (float) r3
            float r7 = r7 / r3
        L_0x0068:
            r3 = r2
            goto L_0x00a2
        L_0x006a:
            boolean r11 = r0.applyTransformation
            if (r11 == 0) goto L_0x00a2
            int r2 = r2.getWidth()
            android.graphics.Bitmap r3 = r0.renderingBitmap
            int r3 = r3.getHeight()
            int[] r7 = r0.metaData
            r11 = r7[r10]
            if (r11 == r9) goto L_0x0082
            r7 = r7[r10]
            if (r7 != r8) goto L_0x0087
        L_0x0082:
            r16 = r3
            r3 = r2
            r2 = r16
        L_0x0087:
            android.graphics.Rect r7 = r17.getBounds()
            r4.set(r7)
            float r7 = r4.width()
            float r2 = (float) r2
            float r2 = r7 / r2
            r0.scaleX = r2
            float r7 = r4.height()
            float r3 = (float) r3
            float r7 = r7 / r3
            r0.scaleY = r7
            r0.applyTransformation = r6
            goto L_0x0068
        L_0x00a2:
            boolean r2 = r17.hasRoundRadius()
            r12 = 1127481344(0x43340000, float:180.0)
            r13 = 1119092736(0x42b40000, float:90.0)
            r14 = 180(0xb4, float:2.52E-43)
            r15 = 0
            if (r2 == 0) goto L_0x015c
            android.graphics.BitmapShader r2 = r0.renderingShader
            if (r2 != 0) goto L_0x00be
            android.graphics.BitmapShader r2 = new android.graphics.BitmapShader
            android.graphics.Bitmap r6 = r0.renderingBitmap
            android.graphics.Shader$TileMode r11 = android.graphics.Shader.TileMode.CLAMP
            r2.<init>(r6, r11, r11)
            r0.renderingShader = r2
        L_0x00be:
            android.graphics.BitmapShader r2 = r0.renderingShader
            r5.setShader(r2)
            android.graphics.Matrix r2 = r0.shaderMatrix
            r2.reset()
            android.graphics.Matrix r2 = r0.shaderMatrix
            float r6 = r4.left
            float r11 = r4.top
            r2.setTranslate(r6, r11)
            int[] r2 = r0.metaData
            r6 = r2[r10]
            if (r6 != r9) goto L_0x00e7
            android.graphics.Matrix r2 = r0.shaderMatrix
            r2.preRotate(r13)
            android.graphics.Matrix r2 = r0.shaderMatrix
            float r4 = r4.width()
            float r4 = -r4
            r2.preTranslate(r15, r4)
            goto L_0x0115
        L_0x00e7:
            r6 = r2[r10]
            if (r6 != r14) goto L_0x0100
            android.graphics.Matrix r2 = r0.shaderMatrix
            r2.preRotate(r12)
            android.graphics.Matrix r2 = r0.shaderMatrix
            float r6 = r4.width()
            float r6 = -r6
            float r4 = r4.height()
            float r4 = -r4
            r2.preTranslate(r6, r4)
            goto L_0x0115
        L_0x0100:
            r2 = r2[r10]
            if (r2 != r8) goto L_0x0115
            android.graphics.Matrix r2 = r0.shaderMatrix
            r6 = 1132920832(0x43870000, float:270.0)
            r2.preRotate(r6)
            android.graphics.Matrix r2 = r0.shaderMatrix
            float r4 = r4.height()
            float r4 = -r4
            r2.preTranslate(r4, r15)
        L_0x0115:
            android.graphics.Matrix r2 = r0.shaderMatrix
            r2.preScale(r3, r7)
            android.graphics.BitmapShader r2 = r0.renderingShader
            android.graphics.Matrix r3 = r0.shaderMatrix
            r2.setLocalMatrix(r3)
            boolean r2 = r0.invalidatePath
            if (r2 == 0) goto L_0x0156
            r2 = 0
            r0.invalidatePath = r2
            r6 = 0
        L_0x0129:
            int[] r2 = r0.roundRadius
            int r3 = r2.length
            if (r6 >= r3) goto L_0x0141
            float[] r3 = radii
            int r4 = r6 * 2
            r7 = r2[r6]
            float r7 = (float) r7
            r3[r4] = r7
            int r4 = r4 + 1
            r2 = r2[r6]
            float r2 = (float) r2
            r3[r4] = r2
            int r6 = r6 + 1
            goto L_0x0129
        L_0x0141:
            android.graphics.Path r2 = r0.roundPath
            r2.reset()
            android.graphics.Path r2 = r0.roundPath
            android.graphics.RectF r3 = r0.actualDrawRect
            float[] r4 = radii
            android.graphics.Path$Direction r6 = android.graphics.Path.Direction.CW
            r2.addRoundRect(r3, r4, r6)
            android.graphics.Path r2 = r0.roundPath
            r2.close()
        L_0x0156:
            android.graphics.Path r2 = r0.roundPath
            r1.drawPath(r2, r5)
            goto L_0x01a3
        L_0x015c:
            float r2 = r4.left
            float r6 = r4.top
            r1.translate(r2, r6)
            int[] r2 = r0.metaData
            r6 = r2[r10]
            if (r6 != r9) goto L_0x0175
            r1.rotate(r13)
            float r2 = r4.width()
            float r2 = -r2
            r1.translate(r15, r2)
            goto L_0x019b
        L_0x0175:
            r6 = r2[r10]
            if (r6 != r14) goto L_0x018a
            r1.rotate(r12)
            float r2 = r4.width()
            float r2 = -r2
            float r4 = r4.height()
            float r4 = -r4
            r1.translate(r2, r4)
            goto L_0x019b
        L_0x018a:
            r2 = r2[r10]
            if (r2 != r8) goto L_0x019b
            r2 = 1132920832(0x43870000, float:270.0)
            r1.rotate(r2)
            float r2 = r4.height()
            float r2 = -r2
            r1.translate(r2, r15)
        L_0x019b:
            r1.scale(r3, r7)
            android.graphics.Bitmap r2 = r0.renderingBitmap
            r1.drawBitmap(r2, r15, r15, r5)
        L_0x01a3:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.AnimatedFileDrawable.drawInternal(android.graphics.Canvas, boolean, long):void");
    }

    public long getLastFrameTimestamp() {
        return (long) this.lastTimeStamp;
    }

    public int getMinimumHeight() {
        int i = 0;
        if (this.decoderCreated) {
            int[] iArr = this.metaData;
            i = (iArr[2] == 90 || iArr[2] == 270) ? iArr[0] : iArr[1];
        }
        return i == 0 ? AndroidUtilities.dp(100.0f) : i;
    }

    public int getMinimumWidth() {
        int i = 0;
        if (this.decoderCreated) {
            int[] iArr = this.metaData;
            i = (iArr[2] == 90 || iArr[2] == 270) ? iArr[1] : iArr[0];
        }
        return i == 0 ? AndroidUtilities.dp(100.0f) : i;
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

    public void setActualDrawRect(float f, float f2, float f3, float f4) {
        float f5 = f4 + f2;
        float f6 = f3 + f;
        RectF rectF = this.actualDrawRect;
        if (rectF.left != f || rectF.top != f2 || rectF.right != f6 || rectF.bottom != f5) {
            rectF.set(f, f2, f6, f5);
            this.invalidatePath = true;
        }
    }

    public void setRoundRadius(int[] iArr) {
        if (!this.secondParentViews.isEmpty()) {
            if (this.roundRadiusBackup == null) {
                this.roundRadiusBackup = new int[4];
            }
            int[] iArr2 = this.roundRadius;
            int[] iArr3 = this.roundRadiusBackup;
            System.arraycopy(iArr2, 0, iArr3, 0, iArr3.length);
        }
        for (int i = 0; i < 4; i++) {
            if (!this.invalidatePath && iArr[i] != this.roundRadius[i]) {
                this.invalidatePath = true;
            }
            this.roundRadius[i] = iArr[i];
        }
    }

    /* access modifiers changed from: private */
    public boolean hasRoundRadius() {
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
        return canLoadFrames() && !(this.renderingBitmap == null && this.nextRenderingBitmap == null);
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
            TLRPC$Document document2 = animatedFileDrawableStream.getDocument();
            ImageLocation location = this.stream.getLocation();
            Object parentObject = this.stream.getParentObject();
            long j2 = this.pendingSeekToUI;
            int i = this.currentAccount;
            AnimatedFileDrawableStream animatedFileDrawableStream2 = this.stream;
            animatedFileDrawable = new AnimatedFileDrawable(file, false, j, document2, location, parentObject, j2, i, animatedFileDrawableStream2 != null && animatedFileDrawableStream2.isPreview(), (BitmapsCache.CacheOptions) null);
        } else {
            File file2 = this.path;
            long j3 = this.streamFileSize;
            TLRPC$Document tLRPC$Document = this.document;
            long j4 = this.pendingSeekToUI;
            int i2 = this.currentAccount;
            AnimatedFileDrawableStream animatedFileDrawableStream3 = this.stream;
            animatedFileDrawable = new AnimatedFileDrawable(file2, false, j3, tLRPC$Document, (ImageLocation) null, (Object) null, j4, i2, animatedFileDrawableStream3 != null && animatedFileDrawableStream3.isPreview(), (BitmapsCache.CacheOptions) null);
        }
        AnimatedFileDrawable animatedFileDrawable2 = animatedFileDrawable;
        int[] iArr = animatedFileDrawable2.metaData;
        int[] iArr2 = this.metaData;
        iArr[0] = iArr2[0];
        iArr[1] = iArr2[1];
        return animatedFileDrawable2;
    }

    public static void getVideoInfo(String str, int[] iArr) {
        getVideoInfo(Build.VERSION.SDK_INT, str, iArr);
    }

    public void setStartEndTime(long j, long j2) {
        this.startTime = ((float) j) / 1000.0f;
        this.endTime = ((float) j2) / 1000.0f;
        if (((long) getCurrentProgressMs()) < j) {
            seekTo(j, true);
        }
    }

    public long getStartTime() {
        return (long) (this.startTime * 1000.0f);
    }

    public boolean isRecycled() {
        return this.isRecycled;
    }

    public Bitmap getNextFrame() {
        if (this.nativePtr == 0) {
            return this.backgroundBitmap;
        }
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

    public void setLimitFps(boolean z) {
        this.limitFps = z;
    }

    public ArrayList<ImageReceiver> getParents() {
        return this.parents;
    }

    public void prepareForGenerateCache() {
        this.cacheGenerateNativePtr = createDecoder(this.path.getAbsolutePath(), this.metaData, this.currentAccount, this.streamFileSize, this.stream, false);
    }

    public void releaseForGenerateCache() {
        long j = this.cacheGenerateNativePtr;
        if (j != 0) {
            destroyDecoder(j);
        }
    }

    public int getNextFrame(Bitmap bitmap) {
        if (this.cacheGenerateNativePtr == 0) {
            return -1;
        }
        Canvas canvas = new Canvas(bitmap);
        if (this.generatingCacheBitmap == null) {
            int[] iArr = this.metaData;
            this.generatingCacheBitmap = Bitmap.createBitmap(iArr[0], iArr[1], Bitmap.Config.ARGB_8888);
        }
        long j = this.cacheGenerateNativePtr;
        Bitmap bitmap2 = this.generatingCacheBitmap;
        getVideoFrame(j, bitmap2, this.metaData, bitmap2.getRowBytes(), false, this.startTime, this.endTime);
        long j2 = this.cacheGenerateTimestamp;
        if (j2 != 0) {
            int[] iArr2 = this.metaData;
            if (iArr2[3] == 0 || j2 > ((long) iArr2[3])) {
                return 0;
            }
        }
        int i = this.lastMetadata;
        int[] iArr3 = this.metaData;
        if (i == iArr3[3]) {
            int i2 = this.tryCount + 1;
            this.tryCount = i2;
            if (i2 > 5) {
                return 0;
            }
        }
        this.lastMetadata = iArr3[3];
        bitmap.eraseColor(0);
        canvas.save();
        float width = ((float) this.renderingWidth) / ((float) this.generatingCacheBitmap.getWidth());
        canvas.scale(width, width);
        canvas.drawBitmap(this.generatingCacheBitmap, 0.0f, 0.0f, (Paint) null);
        canvas.restore();
        this.cacheGenerateTimestamp = (long) this.metaData[3];
        return 1;
    }

    public Bitmap getFirstFrame(Bitmap bitmap) {
        Bitmap bitmap2 = bitmap;
        Canvas canvas = new Canvas(bitmap2);
        if (this.generatingCacheBitmap == null) {
            int[] iArr = this.metaData;
            this.generatingCacheBitmap = Bitmap.createBitmap(iArr[0], iArr[1], Bitmap.Config.ARGB_8888);
        }
        long createDecoder = createDecoder(this.path.getAbsolutePath(), this.metaData, this.currentAccount, this.streamFileSize, this.stream, false);
        if (createDecoder == 0) {
            return bitmap2;
        }
        Bitmap bitmap3 = this.generatingCacheBitmap;
        getVideoFrame(createDecoder, bitmap3, this.metaData, bitmap3.getRowBytes(), false, this.startTime, this.endTime);
        destroyDecoder(createDecoder);
        bitmap2.eraseColor(0);
        canvas.save();
        float width = ((float) this.renderingWidth) / ((float) this.generatingCacheBitmap.getWidth());
        canvas.scale(width, width);
        canvas.drawBitmap(this.generatingCacheBitmap, 0.0f, 0.0f, (Paint) null);
        canvas.restore();
        return bitmap2;
    }

    public boolean canLoadFrames() {
        if (this.precache) {
            if (this.bitmapsCache != null) {
                return true;
            }
            return false;
        } else if (this.nativePtr != 0 || !this.decoderCreated) {
            return true;
        } else {
            return false;
        }
    }

    public void updateCurrentFrame(long j, boolean z) {
        Bitmap bitmap;
        if (this.isRunning) {
            Bitmap bitmap2 = this.renderingBitmap;
            if (bitmap2 == null && this.nextRenderingBitmap == null) {
                scheduleNextGetFrame();
            } else if (this.nextRenderingBitmap == null) {
            } else {
                if (bitmap2 == null || (Math.abs(j - this.lastFrameTime) >= ((long) this.invalidateAfter) && !this.skipFrameUpdate)) {
                    if (this.precache) {
                        this.backgroundBitmap = this.renderingBitmap;
                    }
                    this.renderingBitmap = this.nextRenderingBitmap;
                    this.renderingBitmapTime = this.nextRenderingBitmapTime;
                    this.renderingShader = this.nextRenderingShader;
                    this.nextRenderingBitmap = null;
                    this.nextRenderingBitmapTime = 0;
                    this.nextRenderingShader = null;
                    this.lastFrameTime = j;
                }
            }
        } else if (!this.isRunning && this.decodeSingleFrame && Math.abs(j - this.lastFrameTime) >= ((long) this.invalidateAfter) && (bitmap = this.nextRenderingBitmap) != null) {
            if (this.precache) {
                this.backgroundBitmap = this.renderingBitmap;
            }
            this.renderingBitmap = bitmap;
            this.renderingBitmapTime = this.nextRenderingBitmapTime;
            this.renderingShader = this.nextRenderingShader;
            this.nextRenderingBitmap = null;
            this.nextRenderingBitmapTime = 0;
            this.nextRenderingShader = null;
            this.lastFrameTime = j;
        }
    }
}
