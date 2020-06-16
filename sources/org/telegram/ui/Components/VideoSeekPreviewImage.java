package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextPaint;
import android.view.View;
import android.view.ViewGroup;
import java.io.File;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Bitmaps;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$TL_document;
import org.telegram.tgnet.TLRPC$TL_documentAttributeFilename;
import org.telegram.tgnet.TLRPC$TL_documentAttributeVideo;

public class VideoSeekPreviewImage extends View {
    private Paint bitmapPaint = new Paint(2);
    private RectF bitmapRect = new RectF();
    private BitmapShader bitmapShader;
    private Bitmap bitmapToDraw;
    private Bitmap bitmapToRecycle;
    private int currentPixel = -1;
    private VideoSeekPreviewImageDelegate delegate;
    private RectF dstR = new RectF();
    private long duration;
    private AnimatedFileDrawable fileDrawable;
    private Drawable frameDrawable;
    private String frameTime;
    private Runnable loadRunnable;
    private Matrix matrix = new Matrix();
    private Paint paint = new Paint(2);
    private float pendingProgress;
    private int pixelWidth;
    private Runnable progressRunnable;
    private boolean ready;
    private TextPaint textPaint = new TextPaint(1);
    private int timeWidth;
    private Uri videoUri;

    public interface VideoSeekPreviewImageDelegate {
        void onReady();
    }

    public VideoSeekPreviewImage(Context context, VideoSeekPreviewImageDelegate videoSeekPreviewImageDelegate) {
        super(context);
        setVisibility(4);
        this.frameDrawable = context.getResources().getDrawable(NUM);
        this.textPaint.setTextSize((float) AndroidUtilities.dp(13.0f));
        this.textPaint.setColor(-1);
        this.delegate = videoSeekPreviewImageDelegate;
    }

    public void setProgress(float f, int i) {
        if (i != 0) {
            this.pixelWidth = i;
            int i2 = ((int) (((float) i) * f)) / 5;
            if (this.currentPixel != i2) {
                this.currentPixel = i2;
            } else {
                return;
            }
        }
        long j = (long) (((float) this.duration) * f);
        String formatShortDuration = AndroidUtilities.formatShortDuration((int) (j / 1000));
        this.frameTime = formatShortDuration;
        this.timeWidth = (int) Math.ceil((double) this.textPaint.measureText(formatShortDuration));
        invalidate();
        if (this.progressRunnable != null) {
            Utilities.globalQueue.cancelRunnable(this.progressRunnable);
        }
        AnimatedFileDrawable animatedFileDrawable = this.fileDrawable;
        if (animatedFileDrawable != null) {
            animatedFileDrawable.resetStream(false);
        }
        DispatchQueue dispatchQueue = Utilities.globalQueue;
        $$Lambda$VideoSeekPreviewImage$Os3KazHQPmAT5faayq_HBEMpGLU r2 = new Runnable(f, j) {
            public final /* synthetic */ float f$1;
            public final /* synthetic */ long f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                VideoSeekPreviewImage.this.lambda$setProgress$1$VideoSeekPreviewImage(this.f$1, this.f$2);
            }
        };
        this.progressRunnable = r2;
        dispatchQueue.postRunnable(r2);
    }

    public /* synthetic */ void lambda$setProgress$1$VideoSeekPreviewImage(float f, long j) {
        int i;
        if (this.fileDrawable == null) {
            this.pendingProgress = f;
            return;
        }
        int max = Math.max(200, AndroidUtilities.dp(100.0f));
        Bitmap frameAtTime = this.fileDrawable.getFrameAtTime(j);
        if (frameAtTime != null) {
            int width = frameAtTime.getWidth();
            int height = frameAtTime.getHeight();
            if (width > height) {
                i = (int) (((float) height) / (((float) width) / ((float) max)));
            } else {
                int i2 = (int) (((float) width) / (((float) height) / ((float) max)));
                i = max;
                max = i2;
            }
            try {
                Bitmap createBitmap = Bitmaps.createBitmap(max, i, Bitmap.Config.ARGB_8888);
                this.dstR.set(0.0f, 0.0f, (float) max, (float) i);
                Canvas canvas = new Canvas(createBitmap);
                canvas.drawBitmap(frameAtTime, (Rect) null, this.dstR, this.paint);
                canvas.setBitmap((Bitmap) null);
                frameAtTime = createBitmap;
            } catch (Throwable unused) {
                frameAtTime = null;
            }
        }
        AndroidUtilities.runOnUIThread(new Runnable(frameAtTime) {
            public final /* synthetic */ Bitmap f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                VideoSeekPreviewImage.this.lambda$null$0$VideoSeekPreviewImage(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$null$0$VideoSeekPreviewImage(Bitmap bitmap) {
        int i;
        if (bitmap != null) {
            if (this.bitmapToDraw != null) {
                Bitmap bitmap2 = this.bitmapToRecycle;
                if (bitmap2 != null) {
                    bitmap2.recycle();
                }
                this.bitmapToRecycle = this.bitmapToDraw;
            }
            this.bitmapToDraw = bitmap;
            Bitmap bitmap3 = this.bitmapToDraw;
            Shader.TileMode tileMode = Shader.TileMode.CLAMP;
            BitmapShader bitmapShader2 = new BitmapShader(bitmap3, tileMode, tileMode);
            this.bitmapShader = bitmapShader2;
            bitmapShader2.setLocalMatrix(this.matrix);
            this.bitmapPaint.setShader(this.bitmapShader);
            invalidate();
            int dp = AndroidUtilities.dp(150.0f);
            float width = ((float) bitmap.getWidth()) / ((float) bitmap.getHeight());
            if (width > 1.0f) {
                i = (int) (((float) dp) / width);
            } else {
                int i2 = dp;
                dp = (int) (((float) dp) * width);
                i = i2;
            }
            ViewGroup.LayoutParams layoutParams = getLayoutParams();
            if (!(getVisibility() == 0 && layoutParams.width == dp && layoutParams.height == i)) {
                layoutParams.width = dp;
                layoutParams.height = i;
                setVisibility(0);
                requestLayout();
            }
        }
        this.progressRunnable = null;
    }

    public void open(Uri uri) {
        if (uri != null && !uri.equals(this.videoUri)) {
            this.videoUri = uri;
            DispatchQueue dispatchQueue = Utilities.globalQueue;
            $$Lambda$VideoSeekPreviewImage$zy4HD28BPX6FxdovZogZ2Hvfeys r1 = new Runnable(uri) {
                public final /* synthetic */ Uri f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    VideoSeekPreviewImage.this.lambda$open$3$VideoSeekPreviewImage(this.f$1);
                }
            };
            this.loadRunnable = r1;
            dispatchQueue.postRunnable(r1);
        }
    }

    public /* synthetic */ void lambda$open$3$VideoSeekPreviewImage(Uri uri) {
        String str;
        if ("tg".equals(uri.getScheme())) {
            int intValue = Utilities.parseInt(uri.getQueryParameter("account")).intValue();
            Object parentObject = FileLoader.getInstance(intValue).getParentObject(Utilities.parseInt(uri.getQueryParameter("rid")).intValue());
            TLRPC$TL_document tLRPC$TL_document = new TLRPC$TL_document();
            tLRPC$TL_document.access_hash = Utilities.parseLong(uri.getQueryParameter("hash")).longValue();
            tLRPC$TL_document.id = Utilities.parseLong(uri.getQueryParameter("id")).longValue();
            tLRPC$TL_document.size = Utilities.parseInt(uri.getQueryParameter("size")).intValue();
            tLRPC$TL_document.dc_id = Utilities.parseInt(uri.getQueryParameter("dc")).intValue();
            tLRPC$TL_document.mime_type = uri.getQueryParameter("mime");
            tLRPC$TL_document.file_reference = Utilities.hexToBytes(uri.getQueryParameter("reference"));
            TLRPC$TL_documentAttributeFilename tLRPC$TL_documentAttributeFilename = new TLRPC$TL_documentAttributeFilename();
            tLRPC$TL_documentAttributeFilename.file_name = uri.getQueryParameter("name");
            tLRPC$TL_document.attributes.add(tLRPC$TL_documentAttributeFilename);
            tLRPC$TL_document.attributes.add(new TLRPC$TL_documentAttributeVideo());
            if (FileLoader.getInstance(intValue).isLoadingFile(FileLoader.getAttachFileName(tLRPC$TL_document))) {
                File directory = FileLoader.getDirectory(4);
                str = new File(directory, tLRPC$TL_document.dc_id + "_" + tLRPC$TL_document.id + ".temp").getAbsolutePath();
            } else {
                str = FileLoader.getPathToAttach(tLRPC$TL_document, false).getAbsolutePath();
            }
            this.fileDrawable = new AnimatedFileDrawable(new File(str), true, (long) tLRPC$TL_document.size, tLRPC$TL_document, parentObject, intValue, true);
        } else {
            this.fileDrawable = new AnimatedFileDrawable(new File(uri.getPath()), true, 0, (TLRPC$Document) null, (Object) null, 0, true);
        }
        this.duration = (long) this.fileDrawable.getDurationMs();
        float f = this.pendingProgress;
        if (f != 0.0f) {
            setProgress(f, this.pixelWidth);
            this.pendingProgress = 0.0f;
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            public final void run() {
                VideoSeekPreviewImage.this.lambda$null$2$VideoSeekPreviewImage();
            }
        });
    }

    public /* synthetic */ void lambda$null$2$VideoSeekPreviewImage() {
        this.loadRunnable = null;
        if (this.fileDrawable != null) {
            this.ready = true;
            this.delegate.onReady();
        }
    }

    public boolean isReady() {
        return this.ready;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        Bitmap bitmap = this.bitmapToRecycle;
        if (bitmap != null) {
            bitmap.recycle();
            this.bitmapToRecycle = null;
        }
        if (this.bitmapToDraw != null && this.bitmapShader != null) {
            this.matrix.reset();
            float measuredWidth = ((float) getMeasuredWidth()) / ((float) this.bitmapToDraw.getWidth());
            this.matrix.preScale(measuredWidth, measuredWidth);
            this.bitmapRect.set(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight());
            canvas.drawRoundRect(this.bitmapRect, (float) AndroidUtilities.dp(6.0f), (float) AndroidUtilities.dp(6.0f), this.bitmapPaint);
            this.frameDrawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
            this.frameDrawable.draw(canvas);
            canvas.drawText(this.frameTime, (float) ((getMeasuredWidth() - this.timeWidth) / 2), (float) (getMeasuredHeight() - AndroidUtilities.dp(9.0f)), this.textPaint);
        }
    }

    public void close() {
        if (this.loadRunnable != null) {
            Utilities.globalQueue.cancelRunnable(this.loadRunnable);
            this.loadRunnable = null;
        }
        if (this.progressRunnable != null) {
            Utilities.globalQueue.cancelRunnable(this.progressRunnable);
            this.progressRunnable = null;
        }
        AnimatedFileDrawable animatedFileDrawable = this.fileDrawable;
        if (animatedFileDrawable != null) {
            animatedFileDrawable.resetStream(true);
        }
        Utilities.globalQueue.postRunnable(new Runnable() {
            public final void run() {
                VideoSeekPreviewImage.this.lambda$close$4$VideoSeekPreviewImage();
            }
        });
        setVisibility(4);
        this.bitmapToDraw = null;
        this.bitmapShader = null;
        invalidate();
        this.currentPixel = -1;
        this.videoUri = null;
        this.ready = false;
    }

    public /* synthetic */ void lambda$close$4$VideoSeekPreviewImage() {
        this.pendingProgress = 0.0f;
        AnimatedFileDrawable animatedFileDrawable = this.fileDrawable;
        if (animatedFileDrawable != null) {
            animatedFileDrawable.recycle();
            this.fileDrawable = null;
        }
    }
}
