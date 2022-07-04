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
import org.telegram.ui.ChatActivity;

public class BlurBehindDrawable {
    public static final int ADJUST_PAN_TRANSLATION_CONTENT = 1;
    public static final int STATIC_CONTENT = 0;
    public static final int TAG_DRAWING_AS_BACKGROUND = 67108867;
    private final float DOWN_SCALE = 6.0f;
    private boolean animateAlpha = true;
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
    private final Theme.ResourcesProvider resourcesProvider;
    private boolean show;
    private boolean skipDraw;
    /* access modifiers changed from: private */
    public int toolbarH;
    private final int type;
    private boolean wasDraw;

    public BlurBehindDrawable(View behindView2, View parentView2, int type2, Theme.ResourcesProvider resourcesProvider2) {
        Paint paint = new Paint();
        this.errorBlackoutPaint = paint;
        this.type = type2;
        this.behindView = behindView2;
        this.parentView = parentView2;
        this.resourcesProvider = resourcesProvider2;
        paint.setColor(-16777216);
    }

    public void draw(Canvas canvas) {
        if (this.type == 1 && !this.wasDraw && !this.animateAlpha) {
            generateBlurredBitmaps();
            this.invalidate = false;
        }
        Bitmap[] bitmap = this.renderingBitmap;
        if ((bitmap != null || this.error) && this.animateAlpha) {
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
        float alpha = this.animateAlpha ? this.blurAlpha : 1.0f;
        if (bitmap != null || !this.error) {
            if (alpha == 1.0f) {
                canvas.save();
            } else {
                canvas.saveLayerAlpha(0.0f, 0.0f, (float) this.parentView.getMeasuredWidth(), (float) this.parentView.getMeasuredHeight(), (int) (alpha * 255.0f), 31);
            }
            if (bitmap != null) {
                this.emptyPaint.setAlpha((int) (255.0f * alpha));
                if (this.type == 1) {
                    canvas.translate(0.0f, this.panTranslationY);
                }
                canvas.save();
                canvas.scale(((float) this.parentView.getMeasuredWidth()) / ((float) bitmap[1].getWidth()), ((float) this.parentView.getMeasuredHeight()) / ((float) bitmap[1].getHeight()));
                canvas.drawBitmap(bitmap[1], 0.0f, 0.0f, this.emptyPaint);
                canvas.restore();
                canvas.save();
                if (this.type == 0) {
                    canvas.translate(0.0f, this.panTranslationY);
                }
                canvas.scale(((float) this.parentView.getMeasuredWidth()) / ((float) bitmap[0].getWidth()), ((float) this.toolbarH) / ((float) bitmap[0].getHeight()));
                canvas.drawBitmap(bitmap[0], 0.0f, 0.0f, this.emptyPaint);
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
                        if (this.blurredBitmapTmp[i] != null && this.parentView.getMeasuredWidth() == this.lastW && this.parentView.getMeasuredHeight() == this.lastH) {
                            this.blurredBitmapTmp[i].eraseColor(0);
                        } else {
                            int lastH2 = this.parentView.getMeasuredHeight();
                            int lastW2 = this.parentView.getMeasuredWidth();
                            int h = AndroidUtilities.statusBarHeight + AndroidUtilities.dp(200.0f);
                            this.toolbarH = h;
                            if (i != 0) {
                                h = lastH2;
                            }
                            try {
                                this.blurredBitmapTmp[i] = Bitmap.createBitmap((int) (((float) lastW2) / 6.0f), (int) (((float) h) / 6.0f), Bitmap.Config.ARGB_8888);
                                this.blurCanvas[i] = new Canvas(this.blurredBitmapTmp[i]);
                            } catch (Exception e) {
                                FileLog.e((Throwable) e);
                                AndroidUtilities.runOnUIThread(new BlurBehindDrawable$$ExternalSyntheticLambda2(this));
                                return;
                            }
                        }
                        if (i == 1) {
                            this.blurredBitmapTmp[i].eraseColor(getThemedColor("windowBackgroundWhite"));
                        }
                        this.blurCanvas[i].save();
                        this.blurCanvas[i].scale(0.16666667f, 0.16666667f, 0.0f, 0.0f);
                        Drawable backDrawable = this.behindView.getBackground();
                        if (backDrawable == null) {
                            backDrawable = getBackgroundDrawable();
                        }
                        this.behindView.setTag(67108867, Integer.valueOf(i));
                        if (i == 0) {
                            this.blurCanvas[i].translate(0.0f, -this.panTranslationY);
                            this.behindView.draw(this.blurCanvas[i]);
                        }
                        if (backDrawable != null && i == 1) {
                            Rect oldBounds = backDrawable.getBounds();
                            backDrawable.setBounds(0, 0, this.behindView.getMeasuredWidth(), this.behindView.getMeasuredHeight());
                            backDrawable.draw(this.blurCanvas[i]);
                            backDrawable.setBounds(oldBounds);
                            this.behindView.draw(this.blurCanvas[i]);
                        }
                        this.behindView.setTag(67108867, (Object) null);
                        this.blurCanvas[i].restore();
                    }
                    this.lastH = this.parentView.getMeasuredHeight();
                    this.lastW = this.parentView.getMeasuredWidth();
                    this.blurBackgroundTask.width = this.parentView.getMeasuredWidth();
                    this.blurBackgroundTask.height = this.parentView.getMeasuredHeight();
                    if (this.blurBackgroundTask.width == 0 || this.blurBackgroundTask.height == 0) {
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
        this.errorBlackoutPaint.setAlpha((int) (50.0f * alpha));
        canvas.drawPaint(this.errorBlackoutPaint);
    }

    /* renamed from: lambda$draw$0$org-telegram-ui-Components-BlurBehindDrawable  reason: not valid java name */
    public /* synthetic */ void m574lambda$draw$0$orgtelegramuiComponentsBlurBehindDrawable() {
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
            this.queue.postRunnable(new BlurBehindDrawable$$ExternalSyntheticLambda1(this));
        }
    }

    /* renamed from: lambda$clear$2$org-telegram-ui-Components-BlurBehindDrawable  reason: not valid java name */
    public /* synthetic */ void m573lambda$clear$2$orgtelegramuiComponentsBlurBehindDrawable() {
        Bitmap[] bitmapArr = this.renderingBitmap;
        if (bitmapArr != null) {
            if (bitmapArr[0] != null) {
                bitmapArr[0].recycle();
            }
            Bitmap[] bitmapArr2 = this.renderingBitmap;
            if (bitmapArr2[1] != null) {
                bitmapArr2[1].recycle();
            }
            this.renderingBitmap = null;
        }
        Bitmap[] bitmapArr3 = this.backgroundBitmap;
        if (bitmapArr3 != null) {
            if (bitmapArr3[0] != null) {
                bitmapArr3[0].recycle();
            }
            Bitmap[] bitmapArr4 = this.backgroundBitmap;
            if (bitmapArr4[1] != null) {
                bitmapArr4[1].recycle();
            }
            this.backgroundBitmap = null;
        }
        this.renderingBitmapCanvas = null;
        this.skipDraw = false;
        AndroidUtilities.runOnUIThread(new BlurBehindDrawable$$ExternalSyntheticLambda0(this));
    }

    /* renamed from: lambda$clear$1$org-telegram-ui-Components-BlurBehindDrawable  reason: not valid java name */
    public /* synthetic */ void m572lambda$clear$1$orgtelegramuiComponentsBlurBehindDrawable() {
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
        return !this.skipDraw && this.wasDraw && (this.blurAlpha == 1.0f || !this.animateAlpha) && this.show && this.parentView.getAlpha() == 1.0f;
    }

    public void checkSizes() {
        if (this.renderingBitmap != null && this.parentView.getMeasuredHeight() != 0 && this.parentView.getMeasuredWidth() != 0) {
            generateBlurredBitmaps();
            this.lastH = this.parentView.getMeasuredHeight();
            this.lastW = this.parentView.getMeasuredWidth();
        }
    }

    private void generateBlurredBitmaps() {
        Bitmap[] bitmap = this.renderingBitmap;
        if (bitmap == null) {
            Bitmap[] bitmapArr = new Bitmap[2];
            this.renderingBitmap = bitmapArr;
            bitmap = bitmapArr;
            this.renderingBitmapCanvas = new Canvas[2];
        }
        if (this.blurredBitmapTmp == null) {
            this.blurredBitmapTmp = new Bitmap[2];
            this.blurCanvas = new Canvas[2];
        }
        this.blurBackgroundTask.canceled = true;
        this.blurBackgroundTask = new BlurBackgroundTask();
        int i = 0;
        for (int i2 = 2; i < i2; i2 = 2) {
            int lastH2 = this.parentView.getMeasuredHeight();
            int lastW2 = this.parentView.getMeasuredWidth();
            int h = AndroidUtilities.statusBarHeight + AndroidUtilities.dp(200.0f);
            this.toolbarH = h;
            if (i != 0) {
                h = lastH2;
            }
            if (bitmap[i] == null || bitmap[i].getHeight() != h || bitmap[i].getWidth() != this.parentView.getMeasuredWidth()) {
                DispatchQueue dispatchQueue = this.queue;
                if (dispatchQueue != null) {
                    dispatchQueue.cleanupQueue();
                }
                this.blurredBitmapTmp[i] = Bitmap.createBitmap((int) (((float) lastW2) / 6.0f), (int) (((float) h) / 6.0f), Bitmap.Config.ARGB_8888);
                if (i == 1) {
                    this.blurredBitmapTmp[i].eraseColor(getThemedColor("windowBackgroundWhite"));
                }
                this.blurCanvas[i] = new Canvas(this.blurredBitmapTmp[i]);
                this.renderingBitmap[i] = Bitmap.createBitmap((int) (((float) lastW2) / 6.0f), (int) (((float) (i == 0 ? this.toolbarH : lastH2)) / 6.0f), Bitmap.Config.ARGB_8888);
                this.renderingBitmapCanvas[i] = new Canvas(this.renderingBitmap[i]);
                this.renderingBitmapCanvas[i].scale(((float) this.renderingBitmap[i].getWidth()) / ((float) this.blurredBitmapTmp[i].getWidth()), ((float) this.renderingBitmap[i].getHeight()) / ((float) this.blurredBitmapTmp[i].getHeight()));
                this.blurCanvas[i].save();
                this.blurCanvas[i].scale(0.16666667f, 0.16666667f, 0.0f, 0.0f);
                Drawable backDrawable = this.behindView.getBackground();
                if (backDrawable == null) {
                    backDrawable = getBackgroundDrawable();
                }
                this.behindView.setTag(67108867, Integer.valueOf(i));
                if (i == 0) {
                    this.blurCanvas[i].translate(0.0f, -this.panTranslationY);
                    this.behindView.draw(this.blurCanvas[i]);
                }
                if (i == 1) {
                    Rect oldBounds = backDrawable.getBounds();
                    backDrawable.setBounds(0, 0, this.behindView.getMeasuredWidth(), this.behindView.getMeasuredHeight());
                    backDrawable.draw(this.blurCanvas[i]);
                    backDrawable.setBounds(oldBounds);
                    this.behindView.draw(this.blurCanvas[i]);
                }
                this.behindView.setTag(67108867, (Object) null);
                this.blurCanvas[i].restore();
                Utilities.stackBlurBitmap(this.blurredBitmapTmp[i], getBlurRadius());
                this.emptyPaint.setAlpha(255);
                if (i == 1) {
                    this.renderingBitmap[i].eraseColor(getThemedColor("windowBackgroundWhite"));
                }
                this.renderingBitmapCanvas[i].drawBitmap(this.blurredBitmapTmp[i], 0.0f, 0.0f, this.emptyPaint);
            }
            i++;
        }
    }

    public void show(boolean show2) {
        this.show = show2;
    }

    public void setAnimateAlpha(boolean animateAlpha2) {
        this.animateAlpha = animateAlpha2;
    }

    public void onPanTranslationUpdate(float y) {
        this.panTranslationY = y;
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
            int bitmapWidth = (int) (((float) this.width) / 6.0f);
            int i = 0;
            while (i < 2) {
                int h = (int) (((float) (i == 0 ? BlurBehindDrawable.this.toolbarH : this.height)) / 6.0f);
                if (!(BlurBehindDrawable.this.backgroundBitmap[i] == null || ((BlurBehindDrawable.this.backgroundBitmap[i].getHeight() == h && BlurBehindDrawable.this.backgroundBitmap[i].getWidth() == bitmapWidth) || BlurBehindDrawable.this.backgroundBitmap[i] == null))) {
                    BlurBehindDrawable.this.backgroundBitmap[i].recycle();
                    BlurBehindDrawable.this.backgroundBitmap[i] = null;
                }
                long currentTimeMillis = System.currentTimeMillis();
                if (BlurBehindDrawable.this.backgroundBitmap[i] == null) {
                    try {
                        BlurBehindDrawable.this.backgroundBitmap[i] = Bitmap.createBitmap(bitmapWidth, h, Bitmap.Config.ARGB_8888);
                        BlurBehindDrawable.this.backgroundBitmapCanvas[i] = new Canvas(BlurBehindDrawable.this.backgroundBitmap[i]);
                        BlurBehindDrawable.this.backgroundBitmapCanvas[i].scale(((float) bitmapWidth) / ((float) BlurBehindDrawable.this.blurredBitmapTmp[i].getWidth()), ((float) h) / ((float) BlurBehindDrawable.this.blurredBitmapTmp[i].getHeight()));
                    } catch (Throwable e) {
                        FileLog.e(e);
                    }
                }
                if (i == 1) {
                    BlurBehindDrawable.this.backgroundBitmap[i].eraseColor(BlurBehindDrawable.this.getThemedColor("windowBackgroundWhite"));
                } else {
                    BlurBehindDrawable.this.backgroundBitmap[i].eraseColor(0);
                }
                BlurBehindDrawable.this.emptyPaint.setAlpha(255);
                Utilities.stackBlurBitmap(BlurBehindDrawable.this.blurredBitmapTmp[i], BlurBehindDrawable.this.getBlurRadius());
                if (BlurBehindDrawable.this.backgroundBitmapCanvas[i] != null) {
                    BlurBehindDrawable.this.backgroundBitmapCanvas[i].drawBitmap(BlurBehindDrawable.this.blurredBitmapTmp[i], 0.0f, 0.0f, BlurBehindDrawable.this.emptyPaint);
                }
                if (!this.canceled) {
                    i++;
                } else {
                    return;
                }
            }
            AndroidUtilities.runOnUIThread(new BlurBehindDrawable$BlurBackgroundTask$$ExternalSyntheticLambda0(this));
        }

        /* renamed from: lambda$run$0$org-telegram-ui-Components-BlurBehindDrawable$BlurBackgroundTask  reason: not valid java name */
        public /* synthetic */ void m575xc3c2eb08() {
            if (!this.canceled) {
                Bitmap[] bitmap = BlurBehindDrawable.this.renderingBitmap;
                Canvas[] canvas = BlurBehindDrawable.this.renderingBitmapCanvas;
                BlurBehindDrawable blurBehindDrawable = BlurBehindDrawable.this;
                Bitmap[] unused = blurBehindDrawable.renderingBitmap = blurBehindDrawable.backgroundBitmap;
                BlurBehindDrawable blurBehindDrawable2 = BlurBehindDrawable.this;
                Canvas[] unused2 = blurBehindDrawable2.renderingBitmapCanvas = blurBehindDrawable2.backgroundBitmapCanvas;
                Bitmap[] unused3 = BlurBehindDrawable.this.backgroundBitmap = bitmap;
                Canvas[] unused4 = BlurBehindDrawable.this.backgroundBitmapCanvas = canvas;
                boolean unused5 = BlurBehindDrawable.this.processingNextFrame = false;
                if (BlurBehindDrawable.this.parentView != null) {
                    BlurBehindDrawable.this.parentView.invalidate();
                }
            }
        }
    }

    private Drawable getBackgroundDrawable() {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        if (resourcesProvider2 instanceof ChatActivity.ThemeDelegate) {
            return ((ChatActivity.ThemeDelegate) resourcesProvider2).getWallpaperDrawable();
        }
        return Theme.getCachedWallpaperNonBlocking();
    }

    /* access modifiers changed from: private */
    public int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }
}
