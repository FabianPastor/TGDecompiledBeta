package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
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
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.R;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC$TL_document;
import org.telegram.tgnet.TLRPC$TL_documentAttributeFilename;
import org.telegram.tgnet.TLRPC$TL_documentAttributeVideo;
/* loaded from: classes3.dex */
public class VideoSeekPreviewImage extends View {
    private Paint bitmapPaint;
    private RectF bitmapRect;
    private BitmapShader bitmapShader;
    private Bitmap bitmapToDraw;
    private Bitmap bitmapToRecycle;
    private int currentPixel;
    private VideoSeekPreviewImageDelegate delegate;
    private RectF dstR;
    private long duration;
    private AnimatedFileDrawable fileDrawable;
    private Drawable frameDrawable;
    private String frameTime;
    private boolean isYoutube;
    private int lastYoutubePosition;
    private Runnable loadRunnable;
    private Matrix matrix;
    private Paint paint;
    private float pendingProgress;
    private int pixelWidth;
    private Runnable progressRunnable;
    private boolean ready;
    private TextPaint textPaint;
    private int timeWidth;
    private Uri videoUri;
    private PhotoViewerWebView webView;
    private ImageReceiver youtubeBoardsReceiver;
    private int ytImageHeight;
    private int ytImageWidth;
    private int ytImageX;
    private int ytImageY;
    private Path ytPath;

    /* loaded from: classes3.dex */
    public interface VideoSeekPreviewImageDelegate {
        void onReady();
    }

    public VideoSeekPreviewImage(Context context, VideoSeekPreviewImageDelegate videoSeekPreviewImageDelegate) {
        super(context);
        this.currentPixel = -1;
        this.textPaint = new TextPaint(1);
        this.dstR = new RectF();
        this.paint = new Paint(2);
        this.bitmapPaint = new Paint(2);
        this.bitmapRect = new RectF();
        this.matrix = new Matrix();
        this.ytPath = new Path();
        setVisibility(4);
        this.frameDrawable = context.getResources().getDrawable(R.drawable.videopreview);
        this.textPaint.setTextSize(AndroidUtilities.dp(13.0f));
        this.textPaint.setColor(-1);
        this.delegate = videoSeekPreviewImageDelegate;
        ImageReceiver imageReceiver = new ImageReceiver();
        this.youtubeBoardsReceiver = imageReceiver;
        imageReceiver.setParentView(this);
        this.youtubeBoardsReceiver.setDelegate(new ImageReceiver.ImageReceiverDelegate() { // from class: org.telegram.ui.Components.VideoSeekPreviewImage$$ExternalSyntheticLambda5
            @Override // org.telegram.messenger.ImageReceiver.ImageReceiverDelegate
            public final void didSetImage(ImageReceiver imageReceiver2, boolean z, boolean z2, boolean z3) {
                VideoSeekPreviewImage.this.lambda$new$0(imageReceiver2, z, z2, z3);
            }

            @Override // org.telegram.messenger.ImageReceiver.ImageReceiverDelegate
            public /* synthetic */ void onAnimationReady(ImageReceiver imageReceiver2) {
                ImageReceiver.ImageReceiverDelegate.CC.$default$onAnimationReady(this, imageReceiver2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(ImageReceiver imageReceiver, boolean z, boolean z2, boolean z3) {
        int i;
        if (!z || this.webView == null) {
            return;
        }
        int dp = AndroidUtilities.dp(150.0f);
        int youtubeStoryboardImageCount = this.webView.getYoutubeStoryboardImageCount(this.lastYoutubePosition);
        float bitmapWidth = this.youtubeBoardsReceiver.getBitmapWidth() / Math.min(youtubeStoryboardImageCount, 5);
        float bitmapHeight = this.youtubeBoardsReceiver.getBitmapHeight() / ((int) Math.ceil(youtubeStoryboardImageCount / 5.0f));
        int min = Math.min(this.webView.getYoutubeStoryboardImageIndex(this.lastYoutubePosition), youtubeStoryboardImageCount - 1);
        this.ytImageX = (int) ((min % 5) * bitmapWidth);
        this.ytImageY = (int) ((min / 5) * bitmapHeight);
        this.ytImageWidth = (int) bitmapWidth;
        this.ytImageHeight = (int) bitmapHeight;
        float f = bitmapWidth / bitmapHeight;
        if (f > 1.0f) {
            i = (int) (dp / f);
        } else {
            i = dp;
            dp = (int) (dp * f);
        }
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        if (getVisibility() == 0 && layoutParams.width == dp && layoutParams.height == i) {
            return;
        }
        layoutParams.width = dp;
        layoutParams.height = i;
        setVisibility(0);
        requestLayout();
    }

    @Override // android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.youtubeBoardsReceiver.onAttachedToWindow();
    }

    @Override // android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.youtubeBoardsReceiver.onDetachedFromWindow();
    }

    public void setProgressForYouTube(PhotoViewerWebView photoViewerWebView, float f, int i) {
        this.webView = photoViewerWebView;
        this.isYoutube = true;
        if (i != 0) {
            this.pixelWidth = i;
            int i2 = ((int) (i * f)) / 5;
            if (this.currentPixel == i2) {
                return;
            }
            this.currentPixel = i2;
        }
        String formatShortDuration = AndroidUtilities.formatShortDuration((int) ((photoViewerWebView.getVideoDuration() * f) / 1000));
        this.frameTime = formatShortDuration;
        this.timeWidth = (int) Math.ceil(this.textPaint.measureText(formatShortDuration));
        invalidate();
        if (this.progressRunnable != null) {
            Utilities.globalQueue.cancelRunnable(this.progressRunnable);
        }
        int videoDuration = (int) ((f * photoViewerWebView.getVideoDuration()) / 1000.0f);
        this.lastYoutubePosition = videoDuration;
        String youtubeStoryboard = photoViewerWebView.getYoutubeStoryboard(videoDuration);
        if (youtubeStoryboard != null) {
            this.youtubeBoardsReceiver.setImage(youtubeStoryboard, null, null, null, 0L);
        }
    }

    public void setProgress(final float f, int i) {
        String formatShortDuration;
        this.webView = null;
        this.isYoutube = false;
        this.youtubeBoardsReceiver.setImageBitmap((Drawable) null);
        if (i != 0) {
            this.pixelWidth = i;
            int i2 = ((int) (i * f)) / 5;
            if (this.currentPixel == i2) {
                return;
            }
            this.currentPixel = i2;
        }
        final long j = ((float) this.duration) * f;
        this.frameTime = AndroidUtilities.formatShortDuration((int) (j / 1000));
        this.timeWidth = (int) Math.ceil(this.textPaint.measureText(formatShortDuration));
        invalidate();
        if (this.progressRunnable != null) {
            Utilities.globalQueue.cancelRunnable(this.progressRunnable);
        }
        AnimatedFileDrawable animatedFileDrawable = this.fileDrawable;
        if (animatedFileDrawable != null) {
            animatedFileDrawable.resetStream(false);
        }
        DispatchQueue dispatchQueue = Utilities.globalQueue;
        Runnable runnable = new Runnable() { // from class: org.telegram.ui.Components.VideoSeekPreviewImage$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                VideoSeekPreviewImage.this.lambda$setProgress$2(f, j);
            }
        };
        this.progressRunnable = runnable;
        dispatchQueue.postRunnable(runnable);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setProgress$2(float f, long j) {
        int i;
        if (this.fileDrawable == null) {
            this.pendingProgress = f;
            return;
        }
        int max = Math.max(200, AndroidUtilities.dp(100.0f));
        final Bitmap frameAtTime = this.fileDrawable.getFrameAtTime(j);
        if (frameAtTime != null) {
            int width = frameAtTime.getWidth();
            int height = frameAtTime.getHeight();
            if (width > height) {
                i = (int) (height / (width / max));
            } else {
                int i2 = (int) (width / (height / max));
                i = max;
                max = i2;
            }
            try {
                Bitmap createBitmap = Bitmaps.createBitmap(max, i, Bitmap.Config.ARGB_8888);
                this.dstR.set(0.0f, 0.0f, max, i);
                Canvas canvas = new Canvas(createBitmap);
                canvas.drawBitmap(frameAtTime, (android.graphics.Rect) null, this.dstR, this.paint);
                canvas.setBitmap(null);
                frameAtTime = createBitmap;
            } catch (Throwable unused) {
                frameAtTime = null;
            }
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.VideoSeekPreviewImage$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                VideoSeekPreviewImage.this.lambda$setProgress$1(frameAtTime);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setProgress$1(Bitmap bitmap) {
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
            BitmapShader bitmapShader = new BitmapShader(bitmap3, tileMode, tileMode);
            this.bitmapShader = bitmapShader;
            bitmapShader.setLocalMatrix(this.matrix);
            this.bitmapPaint.setShader(this.bitmapShader);
            invalidate();
            int dp = AndroidUtilities.dp(150.0f);
            float width = bitmap.getWidth() / bitmap.getHeight();
            if (width > 1.0f) {
                i = (int) (dp / width);
            } else {
                dp = (int) (dp * width);
                i = dp;
            }
            ViewGroup.LayoutParams layoutParams = getLayoutParams();
            if (getVisibility() != 0 || layoutParams.width != dp || layoutParams.height != i) {
                layoutParams.width = dp;
                layoutParams.height = i;
                setVisibility(0);
                requestLayout();
            }
        }
        this.progressRunnable = null;
    }

    public void open(final Uri uri) {
        if (uri == null || uri.equals(this.videoUri)) {
            return;
        }
        this.videoUri = uri;
        DispatchQueue dispatchQueue = Utilities.globalQueue;
        Runnable runnable = new Runnable() { // from class: org.telegram.ui.Components.VideoSeekPreviewImage$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                VideoSeekPreviewImage.this.lambda$open$4(uri);
            }
        };
        this.loadRunnable = runnable;
        dispatchQueue.postRunnable(runnable);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$open$4(Uri uri) {
        String absolutePath;
        if ("tg".equals(uri.getScheme())) {
            int intValue = Utilities.parseInt((CharSequence) uri.getQueryParameter("account")).intValue();
            Object parentObject = FileLoader.getInstance(intValue).getParentObject(Utilities.parseInt((CharSequence) uri.getQueryParameter("rid")).intValue());
            TLRPC$TL_document tLRPC$TL_document = new TLRPC$TL_document();
            tLRPC$TL_document.access_hash = Utilities.parseLong(uri.getQueryParameter("hash")).longValue();
            tLRPC$TL_document.id = Utilities.parseLong(uri.getQueryParameter("id")).longValue();
            tLRPC$TL_document.size = Utilities.parseLong(uri.getQueryParameter("size")).longValue();
            tLRPC$TL_document.dc_id = Utilities.parseInt((CharSequence) uri.getQueryParameter("dc")).intValue();
            tLRPC$TL_document.mime_type = uri.getQueryParameter("mime");
            tLRPC$TL_document.file_reference = Utilities.hexToBytes(uri.getQueryParameter("reference"));
            TLRPC$TL_documentAttributeFilename tLRPC$TL_documentAttributeFilename = new TLRPC$TL_documentAttributeFilename();
            tLRPC$TL_documentAttributeFilename.file_name = uri.getQueryParameter("name");
            tLRPC$TL_document.attributes.add(tLRPC$TL_documentAttributeFilename);
            tLRPC$TL_document.attributes.add(new TLRPC$TL_documentAttributeVideo());
            if (FileLoader.getInstance(intValue).isLoadingFile(FileLoader.getAttachFileName(tLRPC$TL_document))) {
                File directory = FileLoader.getDirectory(4);
                absolutePath = new File(directory, tLRPC$TL_document.dc_id + "_" + tLRPC$TL_document.id + ".temp").getAbsolutePath();
            } else {
                absolutePath = FileLoader.getInstance(intValue).getPathToAttach(tLRPC$TL_document, false).getAbsolutePath();
            }
            this.fileDrawable = new AnimatedFileDrawable(new File(absolutePath), true, tLRPC$TL_document.size, tLRPC$TL_document, null, parentObject, 0L, intValue, true, null);
        } else {
            this.fileDrawable = new AnimatedFileDrawable(new File(uri.getPath()), true, 0L, null, null, null, 0L, 0, true, null);
        }
        this.duration = this.fileDrawable.getDurationMs();
        float f = this.pendingProgress;
        if (f != 0.0f) {
            setProgress(f, this.pixelWidth);
            this.pendingProgress = 0.0f;
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.VideoSeekPreviewImage$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                VideoSeekPreviewImage.this.lambda$open$3();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$open$3() {
        this.loadRunnable = null;
        if (this.fileDrawable != null) {
            this.ready = true;
            this.delegate.onReady();
        }
    }

    public boolean isReady() {
        return this.ready;
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        Bitmap bitmap = this.bitmapToRecycle;
        if (bitmap != null) {
            bitmap.recycle();
            this.bitmapToRecycle = null;
        }
        if (this.bitmapToDraw != null && this.bitmapShader != null) {
            this.matrix.reset();
            float measuredWidth = getMeasuredWidth() / this.bitmapToDraw.getWidth();
            this.matrix.preScale(measuredWidth, measuredWidth);
            this.bitmapRect.set(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight());
            canvas.drawRoundRect(this.bitmapRect, AndroidUtilities.dp(6.0f), AndroidUtilities.dp(6.0f), this.bitmapPaint);
            this.frameDrawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
            this.frameDrawable.draw(canvas);
            canvas.drawText(this.frameTime, (getMeasuredWidth() - this.timeWidth) / 2.0f, getMeasuredHeight() - AndroidUtilities.dp(9.0f), this.textPaint);
        } else if (!this.isYoutube) {
        } else {
            canvas.save();
            this.ytPath.rewind();
            RectF rectF = AndroidUtilities.rectTmp;
            rectF.set(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight());
            this.ytPath.addRoundRect(rectF, AndroidUtilities.dp(6.0f), AndroidUtilities.dp(6.0f), Path.Direction.CW);
            canvas.clipPath(this.ytPath);
            canvas.scale(getWidth() / this.ytImageWidth, getHeight() / this.ytImageHeight);
            canvas.translate(-this.ytImageX, -this.ytImageY);
            ImageReceiver imageReceiver = this.youtubeBoardsReceiver;
            imageReceiver.setImageCoords(0.0f, 0.0f, imageReceiver.getBitmapWidth(), this.youtubeBoardsReceiver.getBitmapHeight());
            this.youtubeBoardsReceiver.draw(canvas);
            canvas.restore();
            this.frameDrawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
            this.frameDrawable.draw(canvas);
            canvas.drawText(this.frameTime, (getMeasuredWidth() - this.timeWidth) / 2.0f, getMeasuredHeight() - AndroidUtilities.dp(9.0f), this.textPaint);
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
        Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.ui.Components.VideoSeekPreviewImage$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                VideoSeekPreviewImage.this.lambda$close$5();
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

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$close$5() {
        this.pendingProgress = 0.0f;
        AnimatedFileDrawable animatedFileDrawable = this.fileDrawable;
        if (animatedFileDrawable != null) {
            animatedFileDrawable.recycle();
            this.fileDrawable = null;
        }
    }
}
