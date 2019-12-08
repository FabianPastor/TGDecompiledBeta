package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import java.io.File;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Bitmaps;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC.TL_document;
import org.telegram.tgnet.TLRPC.TL_documentAttributeFilename;
import org.telegram.tgnet.TLRPC.TL_documentAttributeVideo;

public class VideoSeekPreviewImage extends ImageView {
    private Bitmap backgroundBitmap;
    private Bitmap bitmapToRecycle;
    private int currentPixel = -1;
    private VideoSeekPreviewImageDelegate delegate;
    private RectF dstR = new RectF();
    private long duration;
    private AnimatedFileDrawable fileDrawable;
    private float lastReuqestedProgress;
    private Runnable loadRunnable;
    private Paint paint = new Paint(2);
    private float pendingProgress;
    private int pixelWidth;
    private Runnable progressRunnable;
    private boolean ready;
    private Uri videoUri;

    public interface VideoSeekPreviewImageDelegate {
        void onReady();
    }

    public VideoSeekPreviewImage(Context context, VideoSeekPreviewImageDelegate videoSeekPreviewImageDelegate) {
        super(context);
        setVisibility(4);
        this.delegate = videoSeekPreviewImageDelegate;
    }

    public void setProgress(float f, int i) {
        if (i != 0) {
            this.pixelWidth = i;
            i = ((int) (((float) i) * f)) / 5;
            if (this.currentPixel != i) {
                this.currentPixel = i;
            } else {
                return;
            }
        }
        if (this.progressRunnable != null) {
            Utilities.globalQueue.cancelRunnable(this.progressRunnable);
        }
        AnimatedFileDrawable animatedFileDrawable = this.fileDrawable;
        if (animatedFileDrawable != null) {
            animatedFileDrawable.resetStream(false);
        }
        DispatchQueue dispatchQueue = Utilities.globalQueue;
        -$$Lambda$VideoSeekPreviewImage$_Nn9KqOWSzxdI-3Avy3USN7gN_c -__lambda_videoseekpreviewimage__nn9kqowszxdi-3avy3usn7gn_c = new -$$Lambda$VideoSeekPreviewImage$_Nn9KqOWSzxdI-3Avy3USN7gN_c(this, f);
        this.progressRunnable = -__lambda_videoseekpreviewimage__nn9kqowszxdi-3avy3usn7gn_c;
        dispatchQueue.postRunnable(-__lambda_videoseekpreviewimage__nn9kqowszxdi-3avy3usn7gn_c);
    }

    public /* synthetic */ void lambda$setProgress$1$VideoSeekPreviewImage(float f) {
        if (this.fileDrawable == null) {
            this.pendingProgress = f;
            return;
        }
        long j = (long) (((float) this.duration) * f);
        int max = Math.max(200, AndroidUtilities.dp(100.0f));
        Bitmap frameAtTime = this.fileDrawable.getFrameAtTime(j);
        if (frameAtTime != null) {
            int width = frameAtTime.getWidth();
            int height = frameAtTime.getHeight();
            if (width > max || height > max) {
                if (frameAtTime.getWidth() > frameAtTime.getHeight()) {
                    int i = max;
                    max = (int) (((float) height) / (((float) width) / ((float) max)));
                    width = i;
                } else {
                    width = (int) (((float) width) / (((float) height) / ((float) max)));
                }
                Bitmap bitmap = this.backgroundBitmap;
                if (!(bitmap != null && bitmap.getWidth() == width && this.backgroundBitmap.getHeight() == max)) {
                    this.backgroundBitmap = Bitmaps.createBitmap(width, max, Config.ARGB_8888);
                }
                this.dstR.set(0.0f, 0.0f, (float) width, (float) max);
                Canvas canvas = new Canvas(this.backgroundBitmap);
                canvas.drawBitmap(frameAtTime, null, this.dstR, this.paint);
                canvas.setBitmap(null);
                frameAtTime = this.backgroundBitmap;
                this.backgroundBitmap = null;
            }
        }
        if (frameAtTime == null) {
            this.lastReuqestedProgress = f;
        } else {
            this.lastReuqestedProgress = -1.0f;
        }
        AndroidUtilities.runOnUIThread(new -$$Lambda$VideoSeekPreviewImage$vtyatQF0Nebnj1Crg8p5jAvt458(this, frameAtTime));
    }

    public /* synthetic */ void lambda$null$0$VideoSeekPreviewImage(Bitmap bitmap) {
        if (bitmap != null) {
            int i;
            Drawable drawable = getDrawable();
            if (drawable instanceof BitmapDrawable) {
                Bitmap bitmap2 = this.bitmapToRecycle;
                if (bitmap2 != null) {
                    bitmap2.recycle();
                }
                this.bitmapToRecycle = ((BitmapDrawable) drawable).getBitmap();
            }
            setImageBitmap(bitmap);
            int dp = AndroidUtilities.dp(150.0f);
            float width = ((float) bitmap.getWidth()) / ((float) bitmap.getHeight());
            if (width > 1.0f) {
                int i2 = dp;
                dp = (int) (((float) dp) / width);
                i = i2;
            } else {
                i = (int) (((float) dp) / width);
            }
            LayoutParams layoutParams = getLayoutParams();
            if (!(getVisibility() == 0 && layoutParams.width == i && layoutParams.height == dp)) {
                layoutParams.width = i;
                layoutParams.height = dp;
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
            -$$Lambda$VideoSeekPreviewImage$zy4HD28BPX6FxdovZogZ2Hvfeys -__lambda_videoseekpreviewimage_zy4hd28bpx6fxdovzogz2hvfeys = new -$$Lambda$VideoSeekPreviewImage$zy4HD28BPX6FxdovZogZ2Hvfeys(this, uri);
            this.loadRunnable = -__lambda_videoseekpreviewimage_zy4hd28bpx6fxdovzogz2hvfeys;
            dispatchQueue.postRunnable(-__lambda_videoseekpreviewimage_zy4hd28bpx6fxdovzogz2hvfeys);
        }
    }

    public /* synthetic */ void lambda$open$3$VideoSeekPreviewImage(Uri uri) {
        if ("tg".equals(uri.getScheme())) {
            String absolutePath;
            int intValue = Utilities.parseInt(uri.getQueryParameter("account")).intValue();
            Object parentObject = FileLoader.getInstance(intValue).getParentObject(Utilities.parseInt(uri.getQueryParameter("rid")).intValue());
            TL_document tL_document = new TL_document();
            tL_document.access_hash = Utilities.parseLong(uri.getQueryParameter("hash")).longValue();
            tL_document.id = Utilities.parseLong(uri.getQueryParameter("id")).longValue();
            tL_document.size = Utilities.parseInt(uri.getQueryParameter("size")).intValue();
            tL_document.dc_id = Utilities.parseInt(uri.getQueryParameter("dc")).intValue();
            tL_document.mime_type = uri.getQueryParameter("mime");
            tL_document.file_reference = Utilities.hexToBytes(uri.getQueryParameter("reference"));
            TL_documentAttributeFilename tL_documentAttributeFilename = new TL_documentAttributeFilename();
            tL_documentAttributeFilename.file_name = uri.getQueryParameter("name");
            tL_document.attributes.add(tL_documentAttributeFilename);
            tL_document.attributes.add(new TL_documentAttributeVideo());
            if (FileLoader.getInstance(intValue).isLoadingFile(FileLoader.getAttachFileName(tL_document))) {
                File directory = FileLoader.getDirectory(4);
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(tL_document.dc_id);
                stringBuilder.append("_");
                stringBuilder.append(tL_document.id);
                stringBuilder.append(".temp");
                absolutePath = new File(directory, stringBuilder.toString()).getAbsolutePath();
            } else {
                absolutePath = FileLoader.getPathToAttach(tL_document, false).getAbsolutePath();
            }
            this.fileDrawable = new AnimatedFileDrawable(new File(absolutePath), true, (long) tL_document.size, tL_document, parentObject, intValue, true);
        } else {
            this.fileDrawable = new AnimatedFileDrawable(new File(uri.getPath()), true, 0, null, null, 0, true);
        }
        this.duration = (long) this.fileDrawable.getDurationMs();
        float f = this.pendingProgress;
        if (f != 0.0f) {
            setProgress(f, this.pixelWidth);
            this.pendingProgress = 0.0f;
        }
        AndroidUtilities.runOnUIThread(new -$$Lambda$VideoSeekPreviewImage$zY_LEyylj0IUOWXp8R6PawTSSaM(this));
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

    /* Access modifiers changed, original: protected */
    public void onDraw(Canvas canvas) {
        Bitmap bitmap = this.bitmapToRecycle;
        if (bitmap != null) {
            bitmap.recycle();
            this.bitmapToRecycle = null;
        }
        try {
            super.onDraw(canvas);
        } catch (Throwable unused) {
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
        Utilities.globalQueue.postRunnable(new -$$Lambda$VideoSeekPreviewImage$8K7QnWcf3CoeFLmHeaIUHXjugzI(this));
        setVisibility(4);
        Drawable drawable = getDrawable();
        if (drawable instanceof BitmapDrawable) {
            Bitmap bitmap = this.bitmapToRecycle;
            if (bitmap != null) {
                bitmap.recycle();
            }
            this.bitmapToRecycle = ((BitmapDrawable) drawable).getBitmap();
        }
        setImageDrawable(null);
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
