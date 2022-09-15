package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
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
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.R;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.utils.BitmapsCache;
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
    private boolean isYoutube;
    private int lastYoutubePosition;
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
    private PhotoViewerWebView webView;
    private ImageReceiver youtubeBoardsReceiver;
    private int ytImageHeight;
    private int ytImageWidth;
    private int ytImageX;
    private int ytImageY;
    private Path ytPath = new Path();

    public interface VideoSeekPreviewImageDelegate {
        void onReady();
    }

    public VideoSeekPreviewImage(Context context, VideoSeekPreviewImageDelegate videoSeekPreviewImageDelegate) {
        super(context);
        setVisibility(4);
        this.frameDrawable = context.getResources().getDrawable(R.drawable.videopreview);
        this.textPaint.setTextSize((float) AndroidUtilities.dp(13.0f));
        this.textPaint.setColor(-1);
        this.delegate = videoSeekPreviewImageDelegate;
        ImageReceiver imageReceiver = new ImageReceiver();
        this.youtubeBoardsReceiver = imageReceiver;
        imageReceiver.setParentView(this);
        this.youtubeBoardsReceiver.setDelegate(new VideoSeekPreviewImage$$ExternalSyntheticLambda5(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(ImageReceiver imageReceiver, boolean z, boolean z2, boolean z3) {
        int i;
        if (z && this.webView != null) {
            int dp = AndroidUtilities.dp(150.0f);
            int youtubeStoryboardImageCount = this.webView.getYoutubeStoryboardImageCount(this.lastYoutubePosition);
            float bitmapWidth = ((float) this.youtubeBoardsReceiver.getBitmapWidth()) / ((float) Math.min(youtubeStoryboardImageCount, 5));
            float bitmapHeight = ((float) this.youtubeBoardsReceiver.getBitmapHeight()) / ((float) ((int) Math.ceil((double) (((float) youtubeStoryboardImageCount) / 5.0f))));
            int min = Math.min(this.webView.getYoutubeStoryboardImageIndex(this.lastYoutubePosition), youtubeStoryboardImageCount - 1);
            this.ytImageX = (int) (((float) (min % 5)) * bitmapWidth);
            this.ytImageY = (int) (((float) (min / 5)) * bitmapHeight);
            this.ytImageWidth = (int) bitmapWidth;
            this.ytImageHeight = (int) bitmapHeight;
            float f = bitmapWidth / bitmapHeight;
            if (f > 1.0f) {
                i = (int) (((float) dp) / f);
            } else {
                i = dp;
                dp = (int) (((float) dp) * f);
            }
            ViewGroup.LayoutParams layoutParams = getLayoutParams();
            if (getVisibility() != 0 || layoutParams.width != dp || layoutParams.height != i) {
                layoutParams.width = dp;
                layoutParams.height = i;
                setVisibility(0);
                requestLayout();
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.youtubeBoardsReceiver.onAttachedToWindow();
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.youtubeBoardsReceiver.onDetachedFromWindow();
    }

    public void setProgressForYouTube(PhotoViewerWebView photoViewerWebView, float f, int i) {
        this.webView = photoViewerWebView;
        this.isYoutube = true;
        if (i != 0) {
            this.pixelWidth = i;
            int i2 = ((int) (((float) i) * f)) / 5;
            if (this.currentPixel != i2) {
                this.currentPixel = i2;
            } else {
                return;
            }
        }
        String formatShortDuration = AndroidUtilities.formatShortDuration((int) (((long) (((float) photoViewerWebView.getVideoDuration()) * f)) / 1000));
        this.frameTime = formatShortDuration;
        this.timeWidth = (int) Math.ceil((double) this.textPaint.measureText(formatShortDuration));
        invalidate();
        if (this.progressRunnable != null) {
            Utilities.globalQueue.cancelRunnable(this.progressRunnable);
        }
        int videoDuration = (int) ((f * ((float) photoViewerWebView.getVideoDuration())) / 1000.0f);
        this.lastYoutubePosition = videoDuration;
        String youtubeStoryboard = photoViewerWebView.getYoutubeStoryboard(videoDuration);
        if (youtubeStoryboard != null) {
            this.youtubeBoardsReceiver.setImage(youtubeStoryboard, (String) null, (Drawable) null, (String) null, 0);
        }
    }

    public void setProgress(float f, int i) {
        this.webView = null;
        this.isYoutube = false;
        this.youtubeBoardsReceiver.setImageBitmap((Drawable) null);
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
        VideoSeekPreviewImage$$ExternalSyntheticLambda2 videoSeekPreviewImage$$ExternalSyntheticLambda2 = new VideoSeekPreviewImage$$ExternalSyntheticLambda2(this, f, j);
        this.progressRunnable = videoSeekPreviewImage$$ExternalSyntheticLambda2;
        dispatchQueue.postRunnable(videoSeekPreviewImage$$ExternalSyntheticLambda2);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setProgress$2(float f, long j) {
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
        AndroidUtilities.runOnUIThread(new VideoSeekPreviewImage$$ExternalSyntheticLambda3(this, frameAtTime));
    }

    /* access modifiers changed from: private */
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
            VideoSeekPreviewImage$$ExternalSyntheticLambda4 videoSeekPreviewImage$$ExternalSyntheticLambda4 = new VideoSeekPreviewImage$$ExternalSyntheticLambda4(this, uri);
            this.loadRunnable = videoSeekPreviewImage$$ExternalSyntheticLambda4;
            dispatchQueue.postRunnable(videoSeekPreviewImage$$ExternalSyntheticLambda4);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$open$4(Uri uri) {
        String str;
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
                str = new File(directory, tLRPC$TL_document.dc_id + "_" + tLRPC$TL_document.id + ".temp").getAbsolutePath();
            } else {
                str = FileLoader.getInstance(intValue).getPathToAttach(tLRPC$TL_document, false).getAbsolutePath();
            }
            this.fileDrawable = new AnimatedFileDrawable(new File(str), true, tLRPC$TL_document.size, tLRPC$TL_document, (ImageLocation) null, parentObject, 0, intValue, true, (BitmapsCache.CacheOptions) null);
        } else {
            this.fileDrawable = new AnimatedFileDrawable(new File(uri.getPath()), true, 0, (TLRPC$Document) null, (ImageLocation) null, (Object) null, 0, 0, true, (BitmapsCache.CacheOptions) null);
        }
        this.duration = (long) this.fileDrawable.getDurationMs();
        float f = this.pendingProgress;
        if (f != 0.0f) {
            setProgress(f, this.pixelWidth);
            this.pendingProgress = 0.0f;
        }
        AndroidUtilities.runOnUIThread(new VideoSeekPreviewImage$$ExternalSyntheticLambda1(this));
    }

    /* access modifiers changed from: private */
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
            canvas.drawText(this.frameTime, ((float) (getMeasuredWidth() - this.timeWidth)) / 2.0f, (float) (getMeasuredHeight() - AndroidUtilities.dp(9.0f)), this.textPaint);
        } else if (this.isYoutube) {
            canvas.save();
            this.ytPath.rewind();
            RectF rectF = AndroidUtilities.rectTmp;
            rectF.set(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight());
            this.ytPath.addRoundRect(rectF, (float) AndroidUtilities.dp(6.0f), (float) AndroidUtilities.dp(6.0f), Path.Direction.CW);
            canvas.clipPath(this.ytPath);
            canvas.scale(((float) getWidth()) / ((float) this.ytImageWidth), ((float) getHeight()) / ((float) this.ytImageHeight));
            canvas.translate((float) (-this.ytImageX), (float) (-this.ytImageY));
            ImageReceiver imageReceiver = this.youtubeBoardsReceiver;
            imageReceiver.setImageCoords(0.0f, 0.0f, (float) imageReceiver.getBitmapWidth(), (float) this.youtubeBoardsReceiver.getBitmapHeight());
            this.youtubeBoardsReceiver.draw(canvas);
            canvas.restore();
            this.frameDrawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
            this.frameDrawable.draw(canvas);
            canvas.drawText(this.frameTime, ((float) (getMeasuredWidth() - this.timeWidth)) / 2.0f, (float) (getMeasuredHeight() - AndroidUtilities.dp(9.0f)), this.textPaint);
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
        Utilities.globalQueue.postRunnable(new VideoSeekPreviewImage$$ExternalSyntheticLambda0(this));
        setVisibility(4);
        this.bitmapToDraw = null;
        this.bitmapShader = null;
        invalidate();
        this.currentPixel = -1;
        this.videoUri = null;
        this.ready = false;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$close$5() {
        this.pendingProgress = 0.0f;
        AnimatedFileDrawable animatedFileDrawable = this.fileDrawable;
        if (animatedFileDrawable != null) {
            animatedFileDrawable.recycle();
            this.fileDrawable = null;
        }
    }
}
