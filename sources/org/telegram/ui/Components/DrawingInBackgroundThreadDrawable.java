package org.telegram.ui.Components;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;

public class DrawingInBackgroundThreadDrawable implements NotificationCenter.NotificationCenterDelegate {
    private static DispatchQueue backgroundQueue;
    boolean attachedToWindow;
    Bitmap backgroundBitmap;
    Canvas backgroundCanvas;
    Bitmap bitmap;
    Canvas bitmapCanvas;
    Runnable bitmapCreateTask = new Runnable() {
        /* JADX WARNING: Code restructure failed: missing block: B:6:0x001b, code lost:
            if (r2.backgroundBitmap.getHeight() == r1) goto L_0x003f;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void run() {
            /*
                r4 = this;
                org.telegram.ui.Components.DrawingInBackgroundThreadDrawable r0 = org.telegram.ui.Components.DrawingInBackgroundThreadDrawable.this     // Catch:{ Exception -> 0x0068 }
                int r1 = r0.height     // Catch:{ Exception -> 0x0068 }
                int r2 = r0.padding     // Catch:{ Exception -> 0x0068 }
                int r1 = r1 + r2
                android.graphics.Bitmap r0 = r0.backgroundBitmap     // Catch:{ Exception -> 0x0068 }
                if (r0 == 0) goto L_0x001d
                int r0 = r0.getWidth()     // Catch:{ Exception -> 0x0068 }
                org.telegram.ui.Components.DrawingInBackgroundThreadDrawable r2 = org.telegram.ui.Components.DrawingInBackgroundThreadDrawable.this     // Catch:{ Exception -> 0x0068 }
                int r3 = r2.width     // Catch:{ Exception -> 0x0068 }
                if (r0 != r3) goto L_0x001d
                android.graphics.Bitmap r0 = r2.backgroundBitmap     // Catch:{ Exception -> 0x0068 }
                int r0 = r0.getHeight()     // Catch:{ Exception -> 0x0068 }
                if (r0 == r1) goto L_0x003f
            L_0x001d:
                org.telegram.ui.Components.DrawingInBackgroundThreadDrawable r0 = org.telegram.ui.Components.DrawingInBackgroundThreadDrawable.this     // Catch:{ Exception -> 0x0068 }
                android.graphics.Bitmap r0 = r0.backgroundBitmap     // Catch:{ Exception -> 0x0068 }
                if (r0 == 0) goto L_0x0026
                r0.recycle()     // Catch:{ Exception -> 0x0068 }
            L_0x0026:
                org.telegram.ui.Components.DrawingInBackgroundThreadDrawable r0 = org.telegram.ui.Components.DrawingInBackgroundThreadDrawable.this     // Catch:{ Exception -> 0x0068 }
                int r2 = r0.width     // Catch:{ Exception -> 0x0068 }
                android.graphics.Bitmap$Config r3 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ Exception -> 0x0068 }
                android.graphics.Bitmap r1 = android.graphics.Bitmap.createBitmap(r2, r1, r3)     // Catch:{ Exception -> 0x0068 }
                r0.backgroundBitmap = r1     // Catch:{ Exception -> 0x0068 }
                org.telegram.ui.Components.DrawingInBackgroundThreadDrawable r0 = org.telegram.ui.Components.DrawingInBackgroundThreadDrawable.this     // Catch:{ Exception -> 0x0068 }
                android.graphics.Canvas r1 = new android.graphics.Canvas     // Catch:{ Exception -> 0x0068 }
                org.telegram.ui.Components.DrawingInBackgroundThreadDrawable r2 = org.telegram.ui.Components.DrawingInBackgroundThreadDrawable.this     // Catch:{ Exception -> 0x0068 }
                android.graphics.Bitmap r2 = r2.backgroundBitmap     // Catch:{ Exception -> 0x0068 }
                r1.<init>(r2)     // Catch:{ Exception -> 0x0068 }
                r0.backgroundCanvas = r1     // Catch:{ Exception -> 0x0068 }
            L_0x003f:
                org.telegram.ui.Components.DrawingInBackgroundThreadDrawable r0 = org.telegram.ui.Components.DrawingInBackgroundThreadDrawable.this     // Catch:{ Exception -> 0x0068 }
                android.graphics.Bitmap r0 = r0.backgroundBitmap     // Catch:{ Exception -> 0x0068 }
                r1 = 0
                r0.eraseColor(r1)     // Catch:{ Exception -> 0x0068 }
                org.telegram.ui.Components.DrawingInBackgroundThreadDrawable r0 = org.telegram.ui.Components.DrawingInBackgroundThreadDrawable.this     // Catch:{ Exception -> 0x0068 }
                android.graphics.Canvas r0 = r0.backgroundCanvas     // Catch:{ Exception -> 0x0068 }
                r0.save()     // Catch:{ Exception -> 0x0068 }
                org.telegram.ui.Components.DrawingInBackgroundThreadDrawable r0 = org.telegram.ui.Components.DrawingInBackgroundThreadDrawable.this     // Catch:{ Exception -> 0x0068 }
                android.graphics.Canvas r1 = r0.backgroundCanvas     // Catch:{ Exception -> 0x0068 }
                r2 = 0
                int r0 = r0.padding     // Catch:{ Exception -> 0x0068 }
                float r0 = (float) r0     // Catch:{ Exception -> 0x0068 }
                r1.translate(r2, r0)     // Catch:{ Exception -> 0x0068 }
                org.telegram.ui.Components.DrawingInBackgroundThreadDrawable r0 = org.telegram.ui.Components.DrawingInBackgroundThreadDrawable.this     // Catch:{ Exception -> 0x0068 }
                android.graphics.Canvas r1 = r0.backgroundCanvas     // Catch:{ Exception -> 0x0068 }
                r0.drawInBackground(r1)     // Catch:{ Exception -> 0x0068 }
                org.telegram.ui.Components.DrawingInBackgroundThreadDrawable r0 = org.telegram.ui.Components.DrawingInBackgroundThreadDrawable.this     // Catch:{ Exception -> 0x0068 }
                android.graphics.Canvas r0 = r0.backgroundCanvas     // Catch:{ Exception -> 0x0068 }
                r0.restore()     // Catch:{ Exception -> 0x0068 }
                goto L_0x0071
            L_0x0068:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
                org.telegram.ui.Components.DrawingInBackgroundThreadDrawable r0 = org.telegram.ui.Components.DrawingInBackgroundThreadDrawable.this
                r1 = 1
                r0.error = r1
            L_0x0071:
                org.telegram.ui.Components.DrawingInBackgroundThreadDrawable r0 = org.telegram.ui.Components.DrawingInBackgroundThreadDrawable.this
                java.lang.Runnable r0 = r0.uiFrameRunnable
                org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.DrawingInBackgroundThreadDrawable.AnonymousClass1.run():void");
        }
    };
    /* access modifiers changed from: private */
    public boolean bitmapUpdating;
    public int currentLayerNum = 1;
    private int currentOpenedLayerFlags;
    boolean error;
    int frameGuid;
    int height;
    /* access modifiers changed from: private */
    public int lastFrameId;
    Bitmap nextRenderingBitmap;
    Canvas nextRenderingCanvas;
    int padding;
    private Paint paint = new Paint(1);
    protected boolean paused;
    private boolean reset;
    Runnable uiFrameRunnable = new Runnable() {
        public void run() {
            boolean unused = DrawingInBackgroundThreadDrawable.this.bitmapUpdating = false;
            DrawingInBackgroundThreadDrawable.this.onFrameReady();
            DrawingInBackgroundThreadDrawable drawingInBackgroundThreadDrawable = DrawingInBackgroundThreadDrawable.this;
            if (!drawingInBackgroundThreadDrawable.attachedToWindow) {
                Bitmap bitmap = drawingInBackgroundThreadDrawable.backgroundBitmap;
                if (bitmap != null) {
                    bitmap.recycle();
                    DrawingInBackgroundThreadDrawable.this.backgroundBitmap = null;
                }
            } else if (drawingInBackgroundThreadDrawable.frameGuid == drawingInBackgroundThreadDrawable.lastFrameId) {
                DrawingInBackgroundThreadDrawable drawingInBackgroundThreadDrawable2 = DrawingInBackgroundThreadDrawable.this;
                Bitmap bitmap2 = drawingInBackgroundThreadDrawable2.bitmap;
                Canvas canvas = drawingInBackgroundThreadDrawable2.bitmapCanvas;
                drawingInBackgroundThreadDrawable2.bitmap = drawingInBackgroundThreadDrawable2.nextRenderingBitmap;
                drawingInBackgroundThreadDrawable2.bitmapCanvas = drawingInBackgroundThreadDrawable2.nextRenderingCanvas;
                drawingInBackgroundThreadDrawable2.nextRenderingBitmap = drawingInBackgroundThreadDrawable2.backgroundBitmap;
                drawingInBackgroundThreadDrawable2.nextRenderingCanvas = drawingInBackgroundThreadDrawable2.backgroundCanvas;
                drawingInBackgroundThreadDrawable2.backgroundBitmap = bitmap2;
                drawingInBackgroundThreadDrawable2.backgroundCanvas = canvas;
            }
        }
    };
    int width;

    public void drawInBackground(Canvas canvas) {
        throw null;
    }

    /* access modifiers changed from: protected */
    public void drawInUiThread(Canvas canvas, float f) {
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
        if (backgroundQueue == null) {
            backgroundQueue = new DispatchQueue("draw_background_queue");
        }
    }

    public void draw(Canvas canvas, long j, int i, int i2, float f) {
        if (!this.error) {
            this.height = i2;
            this.width = i;
            Bitmap bitmap2 = this.bitmap;
            if ((bitmap2 == null && this.nextRenderingBitmap == null) || this.reset) {
                this.reset = false;
                if (bitmap2 != null) {
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(this.bitmap);
                    AndroidUtilities.recycleBitmaps(arrayList);
                    this.bitmap = null;
                }
                int i3 = this.height + this.padding;
                Bitmap bitmap3 = this.nextRenderingBitmap;
                if (bitmap3 != null && bitmap3.getHeight() == i3 && this.nextRenderingBitmap.getWidth() == this.width) {
                    this.nextRenderingBitmap.eraseColor(0);
                } else {
                    this.nextRenderingBitmap = Bitmap.createBitmap(this.width, i3, Bitmap.Config.ARGB_8888);
                    this.nextRenderingCanvas = new Canvas(this.nextRenderingBitmap);
                }
                this.nextRenderingCanvas.save();
                this.nextRenderingCanvas.translate(0.0f, (float) this.padding);
                drawInUiThread(this.nextRenderingCanvas, 1.0f);
                this.nextRenderingCanvas.restore();
            }
            if (!this.bitmapUpdating && !this.paused) {
                this.bitmapUpdating = true;
                prepareDraw(j);
                this.lastFrameId = this.frameGuid;
                backgroundQueue.postRunnable(this.bitmapCreateTask);
            }
            Bitmap bitmap4 = this.bitmap;
            if (bitmap4 != null || this.nextRenderingBitmap != null) {
                if (bitmap4 == null) {
                    bitmap4 = this.nextRenderingBitmap;
                }
                this.paint.setAlpha((int) (f * 255.0f));
                canvas.save();
                canvas.translate(0.0f, (float) (-this.padding));
                drawBitmap(canvas, bitmap4, this.paint);
                canvas.restore();
            }
        }
    }

    /* access modifiers changed from: protected */
    public void drawBitmap(Canvas canvas, Bitmap bitmap2, Paint paint2) {
        canvas.drawBitmap(bitmap2, 0.0f, 0.0f, paint2);
    }

    public void onAttachToWindow() {
        this.attachedToWindow = true;
        int currentHeavyOperationFlags = NotificationCenter.getGlobalInstance().getCurrentHeavyOperationFlags();
        this.currentOpenedLayerFlags = currentHeavyOperationFlags;
        int i = currentHeavyOperationFlags & (this.currentLayerNum ^ -1);
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
        Bitmap bitmap2 = this.bitmap;
        if (bitmap2 != null) {
            arrayList.add(bitmap2);
        }
        Bitmap bitmap3 = this.nextRenderingBitmap;
        if (bitmap3 != null) {
            arrayList.add(bitmap3);
        }
        this.bitmap = null;
        this.nextRenderingBitmap = null;
        AndroidUtilities.recycleBitmaps(arrayList);
        this.attachedToWindow = false;
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.stopAllHeavyOperations);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.startAllHeavyOperations);
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        int i3;
        if (i == NotificationCenter.stopAllHeavyOperations) {
            Integer num = objArr[0];
            if (this.currentLayerNum >= num.intValue()) {
                return;
            }
            if (num.intValue() != 512 || SharedConfig.getDevicePerformanceClass() < 2) {
                int intValue = num.intValue() | this.currentOpenedLayerFlags;
                this.currentOpenedLayerFlags = intValue;
                if (intValue != 0 && !this.paused) {
                    this.paused = true;
                    onPaused();
                }
            }
        } else if (i == NotificationCenter.startAllHeavyOperations) {
            Integer num2 = objArr[0];
            if (this.currentLayerNum < num2.intValue() && (i3 = this.currentOpenedLayerFlags) != 0) {
                int intValue2 = (num2.intValue() ^ -1) & i3;
                this.currentOpenedLayerFlags = intValue2;
                if (intValue2 == 0 && this.paused) {
                    this.paused = false;
                    onResume();
                }
            }
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
}
