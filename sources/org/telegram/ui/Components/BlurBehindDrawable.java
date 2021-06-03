package org.telegram.ui.Components;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BlurBehindDrawable;

public class BlurBehindDrawable {
    private final float DOWN_SCALE = 6.0f;
    /* access modifiers changed from: private */
    public Bitmap[] backgroundBitmap;
    /* access modifiers changed from: private */
    public Canvas[] backgroundBitmapCanvas;
    private View behindView;
    private float blurAlpha;
    BlurBackgroundTask blurBackgroundTask = new BlurBackgroundTask();
    private Canvas[] blurCanvas;
    /* access modifiers changed from: private */
    public Bitmap[] blurredBitmapTmp;
    Paint emptyPaint = new Paint(2);
    private boolean error;
    Paint errorBlackoutPaint;
    private boolean invalidate = true;
    private int lastH;
    private int lastW;
    private float panTranslationY;
    /* access modifiers changed from: private */
    public View parentView;
    /* access modifiers changed from: private */
    public boolean processingNextFrame;
    DispatchQueue queue;
    /* access modifiers changed from: private */
    public Bitmap[] renderingBitmap;
    /* access modifiers changed from: private */
    public Canvas[] renderingBitmapCanvas;
    private boolean show;
    private boolean skipDraw;
    /* access modifiers changed from: private */
    public int toolbarH;
    private boolean wasDraw;

    public BlurBehindDrawable(View view, View view2) {
        Paint paint = new Paint();
        this.errorBlackoutPaint = paint;
        this.behindView = view;
        this.parentView = view2;
        paint.setColor(-16777216);
    }

    public void draw(Canvas canvas) {
        Bitmap[] bitmapArr = this.renderingBitmap;
        if (bitmapArr != null || this.error) {
            boolean z = this.show;
            if (z) {
                float f = this.blurAlpha;
                if (f != 1.0f) {
                    float f2 = f + 0.09f;
                    this.blurAlpha = f2;
                    if (f2 > 1.0f) {
                        this.blurAlpha = 1.0f;
                    }
                    this.parentView.invalidate();
                }
            }
            if (!z) {
                float f3 = this.blurAlpha;
                if (f3 != 0.0f) {
                    float f4 = f3 - 0.09f;
                    this.blurAlpha = f4;
                    if (f4 < 0.0f) {
                        this.blurAlpha = 0.0f;
                    }
                    this.parentView.invalidate();
                }
            }
        }
        if (bitmapArr != null || !this.error) {
            canvas.saveLayerAlpha(0.0f, 0.0f, (float) this.parentView.getMeasuredWidth(), (float) this.parentView.getMeasuredHeight(), (int) (this.blurAlpha * 255.0f), 31);
            if (bitmapArr != null) {
                this.emptyPaint.setAlpha((int) (this.blurAlpha * 255.0f));
                canvas.drawBitmap(bitmapArr[1], 0.0f, 0.0f, (Paint) null);
                canvas.save();
                canvas.translate(0.0f, this.panTranslationY);
                canvas.drawBitmap(bitmapArr[0], 0.0f, 0.0f, (Paint) null);
                canvas.restore();
                this.wasDraw = true;
                canvas.drawColor(NUM);
            }
            canvas.restore();
            if (this.show && !this.processingNextFrame) {
                if (this.renderingBitmap == null || this.invalidate) {
                    this.processingNextFrame = true;
                    this.invalidate = false;
                    if (this.blurredBitmapTmp == null) {
                        this.blurredBitmapTmp = new Bitmap[2];
                        this.blurCanvas = new Canvas[2];
                    }
                    for (int i = 0; i < 2; i++) {
                        if (!(this.blurredBitmapTmp[i] != null && this.parentView.getMeasuredWidth() == this.lastW && this.parentView.getMeasuredHeight() == this.lastH)) {
                            int measuredHeight = this.parentView.getMeasuredHeight();
                            int measuredWidth = this.parentView.getMeasuredWidth();
                            int dp = AndroidUtilities.statusBarHeight + AndroidUtilities.dp(100.0f);
                            this.toolbarH = dp;
                            if (i == 0) {
                                measuredHeight = dp;
                            }
                            try {
                                this.blurredBitmapTmp[i] = Bitmap.createBitmap((int) (((float) measuredWidth) / 6.0f), (int) (((float) measuredHeight) / 6.0f), Bitmap.Config.ARGB_8888);
                                this.blurCanvas[i] = new Canvas(this.blurredBitmapTmp[i]);
                            } catch (Exception e) {
                                e.printStackTrace();
                                FileLog.e((Throwable) e);
                                AndroidUtilities.runOnUIThread(new Runnable() {
                                    public final void run() {
                                        BlurBehindDrawable.this.lambda$draw$0$BlurBehindDrawable();
                                    }
                                });
                                return;
                            }
                        }
                        this.blurCanvas[i].save();
                        this.blurCanvas[i].scale(0.16666667f, 0.16666667f, 0.0f, 0.0f);
                        Drawable background = this.behindView.getBackground();
                        if (background == null) {
                            background = Theme.getCachedWallpaperNonBlocking();
                        }
                        this.behindView.setTag(67108867, Integer.valueOf(i));
                        if (i == 0) {
                            this.blurCanvas[i].translate(0.0f, -this.panTranslationY);
                            this.behindView.draw(this.blurCanvas[i]);
                        }
                        if (background != null && i == 1) {
                            Rect bounds = background.getBounds();
                            background.setBounds(0, 0, this.behindView.getMeasuredWidth(), this.behindView.getMeasuredHeight());
                            background.draw(this.blurCanvas[i]);
                            background.setBounds(bounds);
                            this.behindView.draw(this.blurCanvas[i]);
                        }
                        this.behindView.setTag(67108867, (Object) null);
                        this.blurCanvas[i].restore();
                    }
                    this.lastH = this.parentView.getMeasuredHeight();
                    this.lastW = this.parentView.getMeasuredWidth();
                    this.blurBackgroundTask.width = this.parentView.getMeasuredWidth();
                    this.blurBackgroundTask.height = this.parentView.getMeasuredHeight();
                    BlurBackgroundTask blurBackgroundTask2 = this.blurBackgroundTask;
                    if (blurBackgroundTask2.width == 0 || blurBackgroundTask2.height == 0) {
                        this.processingNextFrame = false;
                        return;
                    }
                    if (this.queue == null) {
                        this.queue = new DispatchQueue("blur_thread_" + this);
                    }
                    this.queue.postRunnable(this.blurBackgroundTask);
                    return;
                }
                return;
            }
            return;
        }
        this.errorBlackoutPaint.setAlpha((int) (this.blurAlpha * 50.0f));
        canvas.drawPaint(this.errorBlackoutPaint);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$draw$0 */
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$clear$2 */
    public /* synthetic */ void lambda$clear$2$BlurBehindDrawable() {
        Bitmap[] bitmapArr = this.renderingBitmap;
        if (bitmapArr != null) {
            bitmapArr[0].recycle();
            this.renderingBitmap[1].recycle();
            this.renderingBitmap = null;
        }
        Bitmap[] bitmapArr2 = this.backgroundBitmap;
        if (bitmapArr2 != null) {
            bitmapArr2[0].recycle();
            this.backgroundBitmap[1].recycle();
            this.backgroundBitmap = null;
        }
        this.renderingBitmapCanvas = null;
        this.skipDraw = false;
        AndroidUtilities.runOnUIThread(new Runnable() {
            public final void run() {
                BlurBehindDrawable.this.lambda$clear$1$BlurBehindDrawable();
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$clear$1 */
    public /* synthetic */ void lambda$clear$1$BlurBehindDrawable() {
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
        Bitmap[] bitmapArr = this.renderingBitmap;
        if (bitmapArr != null && this.parentView.getMeasuredHeight() != 0 && this.parentView.getMeasuredWidth() != 0) {
            this.blurBackgroundTask.canceled = true;
            this.blurBackgroundTask = new BlurBackgroundTask();
            for (int i = 0; i < 2; i++) {
                int measuredHeight = this.parentView.getMeasuredHeight();
                int measuredWidth = this.parentView.getMeasuredWidth();
                int dp = AndroidUtilities.statusBarHeight + AndroidUtilities.dp(100.0f);
                this.toolbarH = dp;
                if (i != 0) {
                    dp = measuredHeight;
                }
                if (bitmapArr[i].getHeight() != dp || bitmapArr[i].getWidth() != this.parentView.getMeasuredWidth()) {
                    DispatchQueue dispatchQueue = this.queue;
                    if (dispatchQueue != null) {
                        dispatchQueue.cleanupQueue();
                    }
                    this.blurredBitmapTmp[i] = Bitmap.createBitmap((int) (((float) measuredWidth) / 6.0f), (int) (((float) dp) / 6.0f), Bitmap.Config.ARGB_8888);
                    this.blurCanvas[i] = new Canvas(this.blurredBitmapTmp[i]);
                    Bitmap[] bitmapArr2 = this.renderingBitmap;
                    if (i == 0) {
                        measuredHeight = this.toolbarH;
                    }
                    bitmapArr2[i] = Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888);
                    this.renderingBitmapCanvas[i] = new Canvas(this.renderingBitmap[i]);
                    this.renderingBitmapCanvas[i].scale(6.0f, 6.0f);
                    this.blurCanvas[i].save();
                    this.blurCanvas[i].scale(0.16666667f, 0.16666667f, 0.0f, 0.0f);
                    Drawable background = this.behindView.getBackground();
                    if (background == null) {
                        background = Theme.getCachedWallpaperNonBlocking();
                    }
                    this.behindView.setTag(67108867, Integer.valueOf(i));
                    if (i == 0) {
                        this.blurCanvas[i].translate(0.0f, -this.panTranslationY);
                        this.behindView.draw(this.blurCanvas[i]);
                    }
                    if (i == 1) {
                        Rect bounds = background.getBounds();
                        background.setBounds(0, 0, this.behindView.getMeasuredWidth(), this.behindView.getMeasuredHeight());
                        background.draw(this.blurCanvas[i]);
                        background.setBounds(bounds);
                        this.behindView.draw(this.blurCanvas[i]);
                    }
                    this.behindView.setTag(67108867, (Object) null);
                    this.blurCanvas[i].restore();
                    Utilities.stackBlurBitmap(this.blurredBitmapTmp[i], getBlurRadius());
                    this.emptyPaint.setAlpha(255);
                    this.renderingBitmapCanvas[i].drawBitmap(this.blurredBitmapTmp[i], 0.0f, 0.0f, this.emptyPaint);
                }
            }
            this.lastH = this.parentView.getMeasuredHeight();
            this.lastW = this.parentView.getMeasuredWidth();
        }
    }

    public void show(boolean z) {
        this.show = z;
    }

    public void onPanTranslationUpdate(float f) {
        this.panTranslationY = f;
        this.parentView.invalidate();
    }

    public class BlurBackgroundTask implements Runnable {
        boolean canceled;
        int height;
        int width;

        public BlurBackgroundTask() {
        }

        public void run() {
            if (BlurBehindDrawable.this.backgroundBitmap == null) {
                Bitmap[] unused = BlurBehindDrawable.this.backgroundBitmap = new Bitmap[2];
                Canvas[] unused2 = BlurBehindDrawable.this.backgroundBitmapCanvas = new Canvas[2];
            }
            int i = 0;
            while (i < 2) {
                int access$200 = i == 0 ? BlurBehindDrawable.this.toolbarH : this.height;
                if (!(BlurBehindDrawable.this.backgroundBitmap[i] == null || ((BlurBehindDrawable.this.backgroundBitmap[i].getHeight() == access$200 && BlurBehindDrawable.this.backgroundBitmap[i].getWidth() == this.width) || BlurBehindDrawable.this.backgroundBitmap[i] == null))) {
                    BlurBehindDrawable.this.backgroundBitmap[i].recycle();
                    BlurBehindDrawable.this.backgroundBitmap[i] = null;
                }
                System.currentTimeMillis();
                if (BlurBehindDrawable.this.backgroundBitmap[i] == null) {
                    BlurBehindDrawable.this.backgroundBitmap[i] = Bitmap.createBitmap(this.width, access$200, Bitmap.Config.ARGB_8888);
                    BlurBehindDrawable.this.backgroundBitmapCanvas[i] = new Canvas(BlurBehindDrawable.this.backgroundBitmap[i]);
                    BlurBehindDrawable.this.backgroundBitmapCanvas[i].scale(6.0f, 6.0f);
                }
                BlurBehindDrawable.this.emptyPaint.setAlpha(255);
                Utilities.stackBlurBitmap(BlurBehindDrawable.this.blurredBitmapTmp[i], BlurBehindDrawable.this.getBlurRadius());
                BlurBehindDrawable.this.backgroundBitmapCanvas[i].drawBitmap(BlurBehindDrawable.this.blurredBitmapTmp[i], 0.0f, 0.0f, BlurBehindDrawable.this.emptyPaint);
                if (!this.canceled) {
                    i++;
                } else {
                    return;
                }
            }
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    BlurBehindDrawable.BlurBackgroundTask.this.lambda$run$0$BlurBehindDrawable$BlurBackgroundTask();
                }
            });
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$run$0 */
        public /* synthetic */ void lambda$run$0$BlurBehindDrawable$BlurBackgroundTask() {
            if (!this.canceled) {
                Bitmap[] access$500 = BlurBehindDrawable.this.renderingBitmap;
                Canvas[] access$600 = BlurBehindDrawable.this.renderingBitmapCanvas;
                BlurBehindDrawable blurBehindDrawable = BlurBehindDrawable.this;
                Bitmap[] unused = blurBehindDrawable.renderingBitmap = blurBehindDrawable.backgroundBitmap;
                BlurBehindDrawable blurBehindDrawable2 = BlurBehindDrawable.this;
                Canvas[] unused2 = blurBehindDrawable2.renderingBitmapCanvas = blurBehindDrawable2.backgroundBitmapCanvas;
                Bitmap[] unused3 = BlurBehindDrawable.this.backgroundBitmap = access$500;
                Canvas[] unused4 = BlurBehindDrawable.this.backgroundBitmapCanvas = access$600;
                boolean unused5 = BlurBehindDrawable.this.processingNextFrame = false;
                if (BlurBehindDrawable.this.parentView != null) {
                    BlurBehindDrawable.this.parentView.invalidate();
                }
            }
        }
    }
}
