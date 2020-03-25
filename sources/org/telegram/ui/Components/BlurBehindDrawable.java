package org.telegram.ui.Components;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BlurBehindDrawable;

public class BlurBehindDrawable {
    /* access modifiers changed from: private */
    public Bitmap backgroundBitmap;
    /* access modifiers changed from: private */
    public Canvas backgroundBitmapCanvas;
    private View behindView;
    private float blurAlpha;
    BlurBackgroundTask blurBackgroundTask = new BlurBackgroundTask();
    private Canvas blurCanvas;
    /* access modifiers changed from: private */
    public Bitmap blurredBitmapTmp;
    Paint emptyPaint = new Paint(2);
    private boolean error;
    Paint errorBlackoutPaint;
    private boolean invalidate = true;
    private int lastH;
    private int lastW;
    /* access modifiers changed from: private */
    public View parentView;
    /* access modifiers changed from: private */
    public boolean processingNextFrame;
    DispatchQueue queue;
    /* access modifiers changed from: private */
    public Bitmap renderingBitmap;
    /* access modifiers changed from: private */
    public Canvas renderingBitmapCanvas;
    private boolean show;
    private boolean skipDraw;
    private boolean wasDraw;

    public BlurBehindDrawable(View view, View view2) {
        Paint paint = new Paint();
        this.errorBlackoutPaint = paint;
        this.behindView = view;
        this.parentView = view2;
        paint.setColor(-16777216);
    }

    /* JADX WARNING: Removed duplicated region for block: B:46:0x0097 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:47:0x0098  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void draw(android.graphics.Canvas r8) {
        /*
            r7 = this;
            android.graphics.Bitmap r0 = r7.renderingBitmap
            r1 = 1034147594(0x3da3d70a, float:0.08)
            r2 = 1065353216(0x3var_, float:1.0)
            r3 = 0
            if (r0 != 0) goto L_0x000e
            boolean r4 = r7.error
            if (r4 == 0) goto L_0x003f
        L_0x000e:
            boolean r4 = r7.show
            if (r4 == 0) goto L_0x0027
            float r4 = r7.blurAlpha
            int r5 = (r4 > r2 ? 1 : (r4 == r2 ? 0 : -1))
            if (r5 == 0) goto L_0x0027
            float r4 = r4 + r1
            r7.blurAlpha = r4
            int r4 = (r4 > r2 ? 1 : (r4 == r2 ? 0 : -1))
            if (r4 <= 0) goto L_0x0021
            r7.blurAlpha = r2
        L_0x0021:
            android.view.View r4 = r7.parentView
            r4.invalidate()
            goto L_0x003f
        L_0x0027:
            boolean r4 = r7.show
            if (r4 != 0) goto L_0x003f
            float r4 = r7.blurAlpha
            int r5 = (r4 > r3 ? 1 : (r4 == r3 ? 0 : -1))
            if (r5 == 0) goto L_0x003f
            float r4 = r4 - r1
            r7.blurAlpha = r4
            int r4 = (r4 > r3 ? 1 : (r4 == r3 ? 0 : -1))
            if (r4 >= 0) goto L_0x003a
            r7.blurAlpha = r3
        L_0x003a:
            android.view.View r4 = r7.parentView
            r4.invalidate()
        L_0x003f:
            if (r0 != 0) goto L_0x0056
            boolean r4 = r7.error
            if (r4 == 0) goto L_0x0056
            android.graphics.Paint r4 = r7.errorBlackoutPaint
            r5 = 1112014848(0x42480000, float:50.0)
            float r6 = r7.blurAlpha
            float r6 = r6 * r5
            int r5 = (int) r6
            r4.setAlpha(r5)
            android.graphics.Paint r4 = r7.errorBlackoutPaint
            r8.drawPaint(r4)
        L_0x0056:
            r4 = 1
            if (r0 == 0) goto L_0x00ab
            boolean r5 = r7.show
            if (r5 == 0) goto L_0x0072
            float r5 = r7.blurAlpha
            int r6 = (r5 > r2 ? 1 : (r5 == r2 ? 0 : -1))
            if (r6 == 0) goto L_0x0072
            float r5 = r5 + r1
            r7.blurAlpha = r5
            int r1 = (r5 > r2 ? 1 : (r5 == r2 ? 0 : -1))
            if (r1 <= 0) goto L_0x006c
            r7.blurAlpha = r2
        L_0x006c:
            android.view.View r1 = r7.parentView
            r1.invalidate()
            goto L_0x008d
        L_0x0072:
            boolean r1 = r7.show
            if (r1 != 0) goto L_0x008d
            float r1 = r7.blurAlpha
            int r2 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r2 == 0) goto L_0x008d
            r2 = 1008981770(0x3CLASSNAMEd70a, float:0.01)
            float r1 = r1 - r2
            r7.blurAlpha = r1
            int r1 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r1 >= 0) goto L_0x0088
            r7.blurAlpha = r3
        L_0x0088:
            android.view.View r1 = r7.parentView
            r1.invalidate()
        L_0x008d:
            boolean r1 = r7.show
            if (r1 != 0) goto L_0x0098
            float r1 = r7.blurAlpha
            int r1 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r1 != 0) goto L_0x0098
            return
        L_0x0098:
            android.graphics.Paint r1 = r7.emptyPaint
            r2 = 1132396544(0x437var_, float:255.0)
            float r5 = r7.blurAlpha
            float r5 = r5 * r2
            int r2 = (int) r5
            r1.setAlpha(r2)
            android.graphics.Paint r1 = r7.emptyPaint
            r8.drawBitmap(r0, r3, r3, r1)
            r7.wasDraw = r4
        L_0x00ab:
            boolean r8 = r7.show
            if (r8 == 0) goto L_0x0193
            boolean r8 = r7.processingNextFrame
            if (r8 != 0) goto L_0x0193
            android.graphics.Bitmap r8 = r7.renderingBitmap
            if (r8 == 0) goto L_0x00bb
            boolean r8 = r7.invalidate
            if (r8 == 0) goto L_0x0193
        L_0x00bb:
            r7.processingNextFrame = r4
            r8 = 0
            r7.invalidate = r8
            android.graphics.Bitmap r0 = r7.blurredBitmapTmp
            if (r0 == 0) goto L_0x00d8
            android.view.View r0 = r7.parentView
            int r0 = r0.getMeasuredWidth()
            int r1 = r7.lastW
            if (r0 != r1) goto L_0x00d8
            android.view.View r0 = r7.parentView
            int r0 = r0.getMeasuredHeight()
            int r1 = r7.lastH
            if (r0 == r1) goto L_0x0110
        L_0x00d8:
            android.view.View r0 = r7.parentView
            int r0 = r0.getMeasuredHeight()
            r7.lastH = r0
            android.view.View r0 = r7.parentView
            int r0 = r0.getMeasuredWidth()
            r7.lastW = r0
            float r0 = (float) r0
            r1 = 1086324736(0x40CLASSNAME, float:6.0)
            float r0 = r0 / r1
            int r0 = (int) r0
            int r2 = r7.lastH     // Catch:{ Exception -> 0x0104 }
            float r2 = (float) r2     // Catch:{ Exception -> 0x0104 }
            float r2 = r2 / r1
            int r1 = (int) r2     // Catch:{ Exception -> 0x0104 }
            android.graphics.Bitmap$Config r2 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ Exception -> 0x0104 }
            android.graphics.Bitmap r0 = android.graphics.Bitmap.createBitmap(r0, r1, r2)     // Catch:{ Exception -> 0x0104 }
            r7.blurredBitmapTmp = r0     // Catch:{ Exception -> 0x0104 }
            android.graphics.Canvas r0 = new android.graphics.Canvas     // Catch:{ Exception -> 0x0104 }
            android.graphics.Bitmap r1 = r7.blurredBitmapTmp     // Catch:{ Exception -> 0x0104 }
            r0.<init>(r1)     // Catch:{ Exception -> 0x0104 }
            r7.blurCanvas = r0     // Catch:{ Exception -> 0x0104 }
            goto L_0x0110
        L_0x0104:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            org.telegram.ui.Components.-$$Lambda$BlurBehindDrawable$4wNbJpFheeWmN3aQbGxwiVt2zu0 r0 = new org.telegram.ui.Components.-$$Lambda$BlurBehindDrawable$4wNbJpFheeWmN3aQbGxwiVt2zu0
            r0.<init>()
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)
        L_0x0110:
            android.graphics.Canvas r0 = r7.blurCanvas
            r0.save()
            android.graphics.Canvas r0 = r7.blurCanvas
            r1 = 1042983595(0x3e2aaaab, float:0.16666667)
            r0.scale(r1, r1, r3, r3)
            android.view.View r0 = r7.behindView
            java.lang.Integer r1 = java.lang.Integer.valueOf(r8)
            r2 = 67108867(0x4000003, float:1.5046333E-36)
            r0.setTag(r2, r1)
            android.graphics.Canvas r0 = r7.blurCanvas
            java.lang.String r1 = "windowBackgroundWhite"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.drawColor(r1)
            android.view.View r0 = r7.behindView
            android.graphics.Canvas r1 = r7.blurCanvas
            r0.draw(r1)
            android.view.View r0 = r7.behindView
            r1 = 0
            r0.setTag(r2, r1)
            android.graphics.Canvas r0 = r7.blurCanvas
            r0.restore()
            android.graphics.Canvas r0 = r7.blurCanvas
            r1 = 436207616(0x1a000000, float:2.646978E-23)
            r0.drawColor(r1)
            org.telegram.ui.Components.BlurBehindDrawable$BlurBackgroundTask r0 = r7.blurBackgroundTask
            android.view.View r1 = r7.parentView
            int r1 = r1.getMeasuredWidth()
            r0.width = r1
            org.telegram.ui.Components.BlurBehindDrawable$BlurBackgroundTask r0 = r7.blurBackgroundTask
            android.view.View r1 = r7.parentView
            int r1 = r1.getMeasuredHeight()
            r0.height = r1
            org.telegram.ui.Components.BlurBehindDrawable$BlurBackgroundTask r0 = r7.blurBackgroundTask
            int r1 = r0.width
            if (r1 == 0) goto L_0x0191
            int r0 = r0.height
            if (r0 != 0) goto L_0x016d
            goto L_0x0191
        L_0x016d:
            org.telegram.messenger.DispatchQueue r8 = r7.queue
            if (r8 != 0) goto L_0x0189
            org.telegram.messenger.DispatchQueue r8 = new org.telegram.messenger.DispatchQueue
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "blur_thread_"
            r0.append(r1)
            r0.append(r7)
            java.lang.String r0 = r0.toString()
            r8.<init>(r0)
            r7.queue = r8
        L_0x0189:
            org.telegram.messenger.DispatchQueue r8 = r7.queue
            org.telegram.ui.Components.BlurBehindDrawable$BlurBackgroundTask r0 = r7.blurBackgroundTask
            r8.postRunnable(r0)
            goto L_0x0193
        L_0x0191:
            r7.processingNextFrame = r8
        L_0x0193:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.BlurBehindDrawable.draw(android.graphics.Canvas):void");
    }

    public /* synthetic */ void lambda$draw$0$BlurBehindDrawable() {
        this.error = true;
        this.parentView.invalidate();
    }

    /* access modifiers changed from: private */
    public int getBlurRadius() {
        return Math.max(7, Math.max(this.lastH, this.lastW) / 180);
    }

    public void clear() {
        this.invalidate = true;
        this.wasDraw = false;
        this.error = false;
        this.blurAlpha = 0.0f;
        this.lastW = 0;
        this.lastH = 0;
        DispatchQueue dispatchQueue = this.queue;
        if (dispatchQueue != null) {
            dispatchQueue.cleanupQueue();
            this.queue.postRunnable(new Runnable() {
                public final void run() {
                    BlurBehindDrawable.this.lambda$clear$2$BlurBehindDrawable();
                }
            });
        }
    }

    public /* synthetic */ void lambda$clear$2$BlurBehindDrawable() {
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
        this.renderingBitmapCanvas = null;
        this.skipDraw = false;
        AndroidUtilities.runOnUIThread(new Runnable() {
            public final void run() {
                BlurBehindDrawable.this.lambda$null$1$BlurBehindDrawable();
            }
        });
    }

    public /* synthetic */ void lambda$null$1$BlurBehindDrawable() {
        DispatchQueue dispatchQueue = this.queue;
        if (dispatchQueue != null) {
            dispatchQueue.recycle();
            this.queue = null;
        }
    }

    public void invalidate() {
        this.invalidate = true;
        View view = this.parentView;
        if (view != null) {
            view.invalidate();
        }
    }

    public boolean isFullyDrawing() {
        return !this.skipDraw && this.wasDraw && this.blurAlpha == 1.0f && this.show;
    }

    public void checkSizes() {
        Bitmap bitmap = this.renderingBitmap;
        if (bitmap != null && this.parentView.getMeasuredHeight() != 0 && this.parentView.getMeasuredWidth() != 0) {
            if (bitmap.getHeight() != this.parentView.getMeasuredHeight() || bitmap.getWidth() != this.parentView.getMeasuredWidth()) {
                this.queue.cleanupQueue();
                this.blurBackgroundTask.canceled = true;
                this.blurBackgroundTask = new BlurBackgroundTask();
                this.lastH = this.parentView.getMeasuredHeight();
                int measuredWidth = this.parentView.getMeasuredWidth();
                this.lastW = measuredWidth;
                this.blurredBitmapTmp = Bitmap.createBitmap((int) (((float) measuredWidth) / 6.0f), (int) (((float) this.lastH) / 6.0f), Bitmap.Config.ARGB_8888);
                this.blurCanvas = new Canvas(this.blurredBitmapTmp);
                this.renderingBitmap = Bitmap.createBitmap(this.lastW, this.lastH, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(this.renderingBitmap);
                this.renderingBitmapCanvas = canvas;
                canvas.scale(6.0f, 6.0f);
                this.blurCanvas.save();
                this.blurCanvas.scale(0.16666667f, 0.16666667f, 0.0f, 0.0f);
                this.behindView.setTag(67108867, 0);
                this.blurCanvas.drawColor(Theme.getColor("windowBackgroundWhite"));
                this.behindView.draw(this.blurCanvas);
                this.behindView.setTag(67108867, (Object) null);
                this.blurCanvas.restore();
                this.blurCanvas.drawColor(NUM);
                Utilities.stackBlurBitmap(this.blurredBitmapTmp, getBlurRadius());
                this.emptyPaint.setAlpha(255);
                this.renderingBitmapCanvas.drawBitmap(this.blurredBitmapTmp, 0.0f, 0.0f, this.emptyPaint);
            }
        }
    }

    public void show(boolean z) {
        this.show = z;
    }

    public class BlurBackgroundTask implements Runnable {
        boolean canceled;
        int height;
        int width;

        public BlurBackgroundTask() {
        }

        public void run() {
            if (!(BlurBehindDrawable.this.backgroundBitmap == null || ((BlurBehindDrawable.this.backgroundBitmap.getHeight() == this.height && BlurBehindDrawable.this.backgroundBitmap.getWidth() == this.width) || BlurBehindDrawable.this.backgroundBitmap == null))) {
                BlurBehindDrawable.this.backgroundBitmap.recycle();
                Bitmap unused = BlurBehindDrawable.this.backgroundBitmap = null;
            }
            System.currentTimeMillis();
            if (BlurBehindDrawable.this.backgroundBitmap == null) {
                Bitmap unused2 = BlurBehindDrawable.this.backgroundBitmap = Bitmap.createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888);
                Canvas unused3 = BlurBehindDrawable.this.backgroundBitmapCanvas = new Canvas(BlurBehindDrawable.this.backgroundBitmap);
                BlurBehindDrawable.this.backgroundBitmapCanvas.scale((((float) this.height) + AndroidUtilities.dpf2(1.0f)) / ((float) BlurBehindDrawable.this.blurredBitmapTmp.getHeight()), (((float) this.width) + AndroidUtilities.dpf2(1.0f)) / ((float) BlurBehindDrawable.this.blurredBitmapTmp.getWidth()));
            }
            BlurBehindDrawable.this.emptyPaint.setAlpha(255);
            Utilities.stackBlurBitmap(BlurBehindDrawable.this.blurredBitmapTmp, BlurBehindDrawable.this.getBlurRadius());
            BlurBehindDrawable.this.backgroundBitmapCanvas.drawBitmap(BlurBehindDrawable.this.blurredBitmapTmp, 0.0f, 0.0f, BlurBehindDrawable.this.emptyPaint);
            if (!this.canceled) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public final void run() {
                        BlurBehindDrawable.BlurBackgroundTask.this.lambda$run$0$BlurBehindDrawable$BlurBackgroundTask();
                    }
                });
            }
        }

        public /* synthetic */ void lambda$run$0$BlurBehindDrawable$BlurBackgroundTask() {
            if (!this.canceled) {
                Bitmap access$400 = BlurBehindDrawable.this.renderingBitmap;
                Canvas access$500 = BlurBehindDrawable.this.renderingBitmapCanvas;
                BlurBehindDrawable blurBehindDrawable = BlurBehindDrawable.this;
                Bitmap unused = blurBehindDrawable.renderingBitmap = blurBehindDrawable.backgroundBitmap;
                BlurBehindDrawable blurBehindDrawable2 = BlurBehindDrawable.this;
                Canvas unused2 = blurBehindDrawable2.renderingBitmapCanvas = blurBehindDrawable2.backgroundBitmapCanvas;
                Bitmap unused3 = BlurBehindDrawable.this.backgroundBitmap = access$400;
                Canvas unused4 = BlurBehindDrawable.this.backgroundBitmapCanvas = access$500;
                boolean unused5 = BlurBehindDrawable.this.processingNextFrame = false;
                if (BlurBehindDrawable.this.parentView != null) {
                    BlurBehindDrawable.this.parentView.invalidate();
                }
            }
        }
    }
}
