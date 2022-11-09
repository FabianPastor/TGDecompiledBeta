package org.telegram.ui.Components;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.ui.ActionBar.Theme;
/* loaded from: classes3.dex */
public class DrawingInBackgroundThreadDrawable implements NotificationCenter.NotificationCenterDelegate {
    public static DispatchQueuePool queuePool;
    boolean attachedToWindow;
    Bitmap backgroundBitmap;
    Canvas backgroundCanvas;
    private final DispatchQueue backgroundQueue;
    Bitmap bitmap;
    Canvas bitmapCanvas;
    private boolean bitmapUpdating;
    private int currentOpenedLayerFlags;
    boolean error;
    int frameGuid;
    int height;
    private int lastFrameId;
    boolean needSwapBitmaps;
    int padding;
    protected boolean paused;
    private boolean reset;
    public final int threadIndex;
    int width;
    public int currentLayerNum = 1;
    private Paint paint = new Paint(1);
    private final Runnable bitmapCreateTask = new Runnable() { // from class: org.telegram.ui.Components.DrawingInBackgroundThreadDrawable.1
        /* JADX WARN: Code restructure failed: missing block: B:7:0x001b, code lost:
            if (r2.backgroundBitmap.getHeight() == r1) goto L8;
         */
        @Override // java.lang.Runnable
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public void run() {
            /*
                r4 = this;
                org.telegram.ui.Components.DrawingInBackgroundThreadDrawable r0 = org.telegram.ui.Components.DrawingInBackgroundThreadDrawable.this     // Catch: java.lang.Exception -> L68
                int r1 = r0.height     // Catch: java.lang.Exception -> L68
                int r2 = r0.padding     // Catch: java.lang.Exception -> L68
                int r1 = r1 + r2
                android.graphics.Bitmap r0 = r0.backgroundBitmap     // Catch: java.lang.Exception -> L68
                if (r0 == 0) goto L1d
                int r0 = r0.getWidth()     // Catch: java.lang.Exception -> L68
                org.telegram.ui.Components.DrawingInBackgroundThreadDrawable r2 = org.telegram.ui.Components.DrawingInBackgroundThreadDrawable.this     // Catch: java.lang.Exception -> L68
                int r3 = r2.width     // Catch: java.lang.Exception -> L68
                if (r0 != r3) goto L1d
                android.graphics.Bitmap r0 = r2.backgroundBitmap     // Catch: java.lang.Exception -> L68
                int r0 = r0.getHeight()     // Catch: java.lang.Exception -> L68
                if (r0 == r1) goto L3f
            L1d:
                org.telegram.ui.Components.DrawingInBackgroundThreadDrawable r0 = org.telegram.ui.Components.DrawingInBackgroundThreadDrawable.this     // Catch: java.lang.Exception -> L68
                android.graphics.Bitmap r0 = r0.backgroundBitmap     // Catch: java.lang.Exception -> L68
                if (r0 == 0) goto L26
                r0.recycle()     // Catch: java.lang.Exception -> L68
            L26:
                org.telegram.ui.Components.DrawingInBackgroundThreadDrawable r0 = org.telegram.ui.Components.DrawingInBackgroundThreadDrawable.this     // Catch: java.lang.Exception -> L68
                int r2 = r0.width     // Catch: java.lang.Exception -> L68
                android.graphics.Bitmap$Config r3 = android.graphics.Bitmap.Config.ARGB_8888     // Catch: java.lang.Exception -> L68
                android.graphics.Bitmap r1 = android.graphics.Bitmap.createBitmap(r2, r1, r3)     // Catch: java.lang.Exception -> L68
                r0.backgroundBitmap = r1     // Catch: java.lang.Exception -> L68
                org.telegram.ui.Components.DrawingInBackgroundThreadDrawable r0 = org.telegram.ui.Components.DrawingInBackgroundThreadDrawable.this     // Catch: java.lang.Exception -> L68
                android.graphics.Canvas r1 = new android.graphics.Canvas     // Catch: java.lang.Exception -> L68
                org.telegram.ui.Components.DrawingInBackgroundThreadDrawable r2 = org.telegram.ui.Components.DrawingInBackgroundThreadDrawable.this     // Catch: java.lang.Exception -> L68
                android.graphics.Bitmap r2 = r2.backgroundBitmap     // Catch: java.lang.Exception -> L68
                r1.<init>(r2)     // Catch: java.lang.Exception -> L68
                r0.backgroundCanvas = r1     // Catch: java.lang.Exception -> L68
            L3f:
                org.telegram.ui.Components.DrawingInBackgroundThreadDrawable r0 = org.telegram.ui.Components.DrawingInBackgroundThreadDrawable.this     // Catch: java.lang.Exception -> L68
                android.graphics.Bitmap r0 = r0.backgroundBitmap     // Catch: java.lang.Exception -> L68
                r1 = 0
                r0.eraseColor(r1)     // Catch: java.lang.Exception -> L68
                org.telegram.ui.Components.DrawingInBackgroundThreadDrawable r0 = org.telegram.ui.Components.DrawingInBackgroundThreadDrawable.this     // Catch: java.lang.Exception -> L68
                android.graphics.Canvas r0 = r0.backgroundCanvas     // Catch: java.lang.Exception -> L68
                r0.save()     // Catch: java.lang.Exception -> L68
                org.telegram.ui.Components.DrawingInBackgroundThreadDrawable r0 = org.telegram.ui.Components.DrawingInBackgroundThreadDrawable.this     // Catch: java.lang.Exception -> L68
                android.graphics.Canvas r1 = r0.backgroundCanvas     // Catch: java.lang.Exception -> L68
                r2 = 0
                int r0 = r0.padding     // Catch: java.lang.Exception -> L68
                float r0 = (float) r0     // Catch: java.lang.Exception -> L68
                r1.translate(r2, r0)     // Catch: java.lang.Exception -> L68
                org.telegram.ui.Components.DrawingInBackgroundThreadDrawable r0 = org.telegram.ui.Components.DrawingInBackgroundThreadDrawable.this     // Catch: java.lang.Exception -> L68
                android.graphics.Canvas r1 = r0.backgroundCanvas     // Catch: java.lang.Exception -> L68
                r0.drawInBackground(r1)     // Catch: java.lang.Exception -> L68
                org.telegram.ui.Components.DrawingInBackgroundThreadDrawable r0 = org.telegram.ui.Components.DrawingInBackgroundThreadDrawable.this     // Catch: java.lang.Exception -> L68
                android.graphics.Canvas r0 = r0.backgroundCanvas     // Catch: java.lang.Exception -> L68
                r0.restore()     // Catch: java.lang.Exception -> L68
                goto L71
            L68:
                r0 = move-exception
                org.telegram.messenger.FileLog.e(r0)
                org.telegram.ui.Components.DrawingInBackgroundThreadDrawable r0 = org.telegram.ui.Components.DrawingInBackgroundThreadDrawable.this
                r1 = 1
                r0.error = r1
            L71:
                org.telegram.ui.Components.DrawingInBackgroundThreadDrawable r0 = org.telegram.ui.Components.DrawingInBackgroundThreadDrawable.this
                java.lang.Runnable r0 = r0.uiFrameRunnable
                org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.DrawingInBackgroundThreadDrawable.AnonymousClass1.run():void");
        }
    };
    Runnable uiFrameRunnable = new Runnable() { // from class: org.telegram.ui.Components.DrawingInBackgroundThreadDrawable.2
        @Override // java.lang.Runnable
        public void run() {
            DrawingInBackgroundThreadDrawable.this.bitmapUpdating = false;
            DrawingInBackgroundThreadDrawable.this.onFrameReady();
            DrawingInBackgroundThreadDrawable drawingInBackgroundThreadDrawable = DrawingInBackgroundThreadDrawable.this;
            if (drawingInBackgroundThreadDrawable.attachedToWindow) {
                if (drawingInBackgroundThreadDrawable.frameGuid != drawingInBackgroundThreadDrawable.lastFrameId) {
                    return;
                }
                DrawingInBackgroundThreadDrawable.this.needSwapBitmaps = true;
                return;
            }
            Bitmap bitmap = drawingInBackgroundThreadDrawable.backgroundBitmap;
            if (bitmap == null) {
                return;
            }
            bitmap.recycle();
            DrawingInBackgroundThreadDrawable.this.backgroundBitmap = null;
        }
    };

    public void drawInBackground(Canvas canvas) {
        throw null;
    }

    protected void drawInUiThread(Canvas canvas, float f) {
        throw null;
    }

    public void onFrameReady() {
    }

    public void onPaused() {
    }

    public void onResume() {
    }

    public void prepareDraw(long j) {
        throw null;
    }

    public DrawingInBackgroundThreadDrawable() {
        if (queuePool == null) {
            queuePool = new DispatchQueuePool(2);
        }
        this.backgroundQueue = queuePool.getNextQueue();
        this.threadIndex = queuePool.pointer;
    }

    public void draw(Canvas canvas, long j, int i, int i2, float f) {
        if (this.error) {
            if (!BuildVars.DEBUG_PRIVATE_VERSION) {
                return;
            }
            canvas.drawRect(0.0f, 0.0f, i, i2, Theme.DEBUG_RED);
            return;
        }
        this.height = i2;
        this.width = i;
        if (this.needSwapBitmaps) {
            this.needSwapBitmaps = false;
            Bitmap bitmap = this.bitmap;
            Canvas canvas2 = this.bitmapCanvas;
            this.bitmap = this.backgroundBitmap;
            this.bitmapCanvas = this.backgroundCanvas;
            this.backgroundBitmap = bitmap;
            this.backgroundCanvas = canvas2;
        }
        Bitmap bitmap2 = this.bitmap;
        if (bitmap2 == null || this.reset) {
            this.reset = false;
            if (bitmap2 != null) {
                ArrayList arrayList = new ArrayList();
                arrayList.add(this.bitmap);
                AndroidUtilities.recycleBitmaps(arrayList);
                this.bitmap = null;
            }
            int i3 = this.height + this.padding;
            Bitmap bitmap3 = this.bitmap;
            if (bitmap3 == null || bitmap3.getHeight() != i3 || this.bitmap.getWidth() != this.width) {
                this.bitmap = Bitmap.createBitmap(this.width, i3, Bitmap.Config.ARGB_8888);
                this.bitmapCanvas = new Canvas(this.bitmap);
            } else {
                this.bitmap.eraseColor(0);
            }
            this.bitmapCanvas.save();
            this.bitmapCanvas.translate(0.0f, this.padding);
            drawInUiThread(this.bitmapCanvas, 1.0f);
            this.bitmapCanvas.restore();
        }
        if (!this.bitmapUpdating && !this.paused) {
            this.bitmapUpdating = true;
            prepareDraw(j);
            this.lastFrameId = this.frameGuid;
            this.backgroundQueue.postRunnable(this.bitmapCreateTask);
        }
        Bitmap bitmap4 = this.bitmap;
        if (bitmap4 == null) {
            return;
        }
        this.paint.setAlpha((int) (f * 255.0f));
        canvas.save();
        canvas.translate(0.0f, -this.padding);
        drawBitmap(canvas, bitmap4, this.paint);
        canvas.restore();
    }

    protected void drawBitmap(Canvas canvas, Bitmap bitmap, Paint paint) {
        canvas.drawBitmap(bitmap, 0.0f, 0.0f, paint);
    }

    public void onAttachToWindow() {
        this.attachedToWindow = true;
        int currentHeavyOperationFlags = NotificationCenter.getGlobalInstance().getCurrentHeavyOperationFlags();
        this.currentOpenedLayerFlags = currentHeavyOperationFlags;
        int i = currentHeavyOperationFlags & (this.currentLayerNum ^ (-1));
        this.currentOpenedLayerFlags = i;
        if (i == 0 && this.paused) {
            this.paused = false;
            onResume();
        }
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.stopAllHeavyOperations);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.startAllHeavyOperations);
    }

    public void onDetachFromWindow() {
        ArrayList arrayList = new ArrayList();
        Bitmap bitmap = this.bitmap;
        if (bitmap != null) {
            arrayList.add(bitmap);
        }
        this.bitmap = null;
        AndroidUtilities.recycleBitmaps(arrayList);
        this.attachedToWindow = false;
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.stopAllHeavyOperations);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.startAllHeavyOperations);
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        int i3;
        if (i == NotificationCenter.stopAllHeavyOperations) {
            Integer num = (Integer) objArr[0];
            if (this.currentLayerNum >= num.intValue()) {
                return;
            }
            if (num.intValue() == 512 && SharedConfig.getDevicePerformanceClass() >= 2) {
                return;
            }
            int intValue = num.intValue() | this.currentOpenedLayerFlags;
            this.currentOpenedLayerFlags = intValue;
            if (intValue == 0 || this.paused) {
                return;
            }
            this.paused = true;
            onPaused();
        } else if (i != NotificationCenter.startAllHeavyOperations) {
        } else {
            Integer num2 = (Integer) objArr[0];
            if (this.currentLayerNum >= num2.intValue() || (i3 = this.currentOpenedLayerFlags) == 0) {
                return;
            }
            int intValue2 = (num2.intValue() ^ (-1)) & i3;
            this.currentOpenedLayerFlags = intValue2;
            if (intValue2 != 0 || !this.paused) {
                return;
            }
            this.paused = false;
            onResume();
        }
    }

    public void reset() {
        this.reset = true;
        this.frameGuid++;
        if (this.bitmap != null) {
            ArrayList arrayList = new ArrayList();
            arrayList.add(this.bitmap);
            this.bitmap = null;
            AndroidUtilities.recycleBitmaps(arrayList);
        }
    }

    /* loaded from: classes3.dex */
    public static class DispatchQueuePool {
        int pointer;
        public final DispatchQueue[] pool;
        final int size;

        private DispatchQueuePool(int i) {
            this.size = i;
            this.pool = new DispatchQueue[i];
        }

        public DispatchQueue getNextQueue() {
            int i = this.pointer + 1;
            this.pointer = i;
            if (i > this.size - 1) {
                this.pointer = 0;
            }
            DispatchQueue[] dispatchQueueArr = this.pool;
            int i2 = this.pointer;
            DispatchQueue dispatchQueue = dispatchQueueArr[i2];
            if (dispatchQueue == null) {
                DispatchQueue dispatchQueue2 = new DispatchQueue("draw_background_queue_" + this.pointer);
                dispatchQueueArr[i2] = dispatchQueue2;
                return dispatchQueue2;
            }
            return dispatchQueue;
        }
    }
}
