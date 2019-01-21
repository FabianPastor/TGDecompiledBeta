package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import java.io.File;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.DownloadController.FileDownloadProgressListener;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.TLRPC.Document;

public class OtherDocumentPlaceholderDrawable extends RecyclableDrawable implements FileDownloadProgressListener {
    private static TextPaint buttonPaint = new TextPaint(1);
    private static DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator();
    private static TextPaint docPaint = new TextPaint(1);
    private static TextPaint namePaint = new TextPaint(1);
    private static TextPaint openPaint = new TextPaint(1);
    private static Paint paint = new Paint();
    private static TextPaint percentPaint = new TextPaint(1);
    private static Paint progressPaint = new Paint(1);
    private static TextPaint sizePaint = new TextPaint(1);
    private int TAG;
    private float animatedAlphaValue = 1.0f;
    private float animatedProgressValue = 0.0f;
    private float animationProgressStart = 0.0f;
    private float currentProgress = 0.0f;
    private long currentProgressTime = 0;
    private String ext;
    private String fileName;
    private String fileSize;
    private long lastUpdateTime = 0;
    private boolean loaded;
    private boolean loading;
    private MessageObject parentMessageObject;
    private View parentView;
    private String progress;
    private boolean progressVisible;
    private Drawable thumbDrawable;

    static {
        progressPaint.setStrokeCap(Cap.ROUND);
        paint.setColor(-14209998);
        docPaint.setColor(-1);
        namePaint.setColor(-1);
        sizePaint.setColor(-10327179);
        buttonPaint.setColor(-10327179);
        percentPaint.setColor(-1);
        openPaint.setColor(-1);
        docPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        namePaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        buttonPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        percentPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        openPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
    }

    public OtherDocumentPlaceholderDrawable(Context context, View view, MessageObject messageObject) {
        docPaint.setTextSize((float) AndroidUtilities.dp(14.0f));
        namePaint.setTextSize((float) AndroidUtilities.dp(19.0f));
        sizePaint.setTextSize((float) AndroidUtilities.dp(15.0f));
        buttonPaint.setTextSize((float) AndroidUtilities.dp(15.0f));
        percentPaint.setTextSize((float) AndroidUtilities.dp(15.0f));
        openPaint.setTextSize((float) AndroidUtilities.dp(15.0f));
        progressPaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        this.parentView = view;
        this.parentMessageObject = messageObject;
        this.TAG = DownloadController.getInstance(messageObject.currentAccount).generateObserverTag();
        Document document = messageObject.getDocument();
        if (document != null) {
            this.fileName = FileLoader.getDocumentFileName(messageObject.getDocument());
            if (TextUtils.isEmpty(this.fileName)) {
                this.fileName = "name";
            }
            int idx = this.fileName.lastIndexOf(46);
            this.ext = idx == -1 ? "" : this.fileName.substring(idx + 1).toUpperCase();
            if (((int) Math.ceil((double) docPaint.measureText(this.ext))) > AndroidUtilities.dp(40.0f)) {
                this.ext = TextUtils.ellipsize(this.ext, docPaint, (float) AndroidUtilities.dp(40.0f), TruncateAt.END).toString();
            }
            this.thumbDrawable = context.getResources().getDrawable(AndroidUtilities.getThumbForNameOrMime(this.fileName, messageObject.getDocument().mime_type, true)).mutate();
            this.fileSize = AndroidUtilities.formatFileSize((long) document.size);
            if (((int) Math.ceil((double) namePaint.measureText(this.fileName))) > AndroidUtilities.dp(320.0f)) {
                this.fileName = TextUtils.ellipsize(this.fileName, namePaint, (float) AndroidUtilities.dp(320.0f), TruncateAt.END).toString();
            }
        }
        checkFileExist();
    }

    public void setColorFilter(ColorFilter colorFilter) {
    }

    public void setAlpha(int alpha) {
        if (this.thumbDrawable != null) {
            this.thumbDrawable.setAlpha(alpha);
        }
        paint.setAlpha(alpha);
        docPaint.setAlpha(alpha);
        namePaint.setAlpha(alpha);
        sizePaint.setAlpha(alpha);
        buttonPaint.setAlpha(alpha);
        percentPaint.setAlpha(alpha);
        openPaint.setAlpha(alpha);
    }

    public void draw(Canvas canvas) {
        String button;
        TextPaint paint;
        int offsetY;
        Rect bounds = getBounds();
        int width = bounds.width();
        int height = bounds.height();
        canvas.save();
        canvas.translate((float) bounds.left, (float) bounds.top);
        canvas.drawRect(0.0f, 0.0f, (float) width, (float) height, paint);
        int y = (height - AndroidUtilities.dp(240.0f)) / 2;
        int x = (width - AndroidUtilities.dp(48.0f)) / 2;
        this.thumbDrawable.setBounds(x, y, AndroidUtilities.dp(48.0f) + x, AndroidUtilities.dp(48.0f) + y);
        this.thumbDrawable.draw(canvas);
        Canvas canvas2 = canvas;
        canvas2.drawText(this.ext, (float) ((width - ((int) Math.ceil((double) docPaint.measureText(this.ext)))) / 2), (float) (AndroidUtilities.dp(31.0f) + y), docPaint);
        canvas2 = canvas;
        canvas2.drawText(this.fileName, (float) ((width - ((int) Math.ceil((double) namePaint.measureText(this.fileName)))) / 2), (float) (AndroidUtilities.dp(96.0f) + y), namePaint);
        canvas2 = canvas;
        canvas2.drawText(this.fileSize, (float) ((width - ((int) Math.ceil((double) sizePaint.measureText(this.fileSize)))) / 2), (float) (AndroidUtilities.dp(125.0f) + y), sizePaint);
        if (this.loaded) {
            button = LocaleController.getString("OpenFile", R.string.OpenFile);
            paint = openPaint;
            offsetY = 0;
        } else {
            if (this.loading) {
                button = LocaleController.getString("Cancel", R.string.Cancel).toUpperCase();
            } else {
                button = LocaleController.getString("TapToDownload", R.string.TapToDownload);
            }
            offsetY = AndroidUtilities.dp(28.0f);
            paint = buttonPaint;
        }
        canvas.drawText(button, (float) ((width - ((int) Math.ceil((double) paint.measureText(button)))) / 2), (float) ((AndroidUtilities.dp(235.0f) + y) + offsetY), paint);
        if (this.progressVisible) {
            if (this.progress != null) {
                canvas2 = canvas;
                canvas2.drawText(this.progress, (float) ((width - ((int) Math.ceil((double) percentPaint.measureText(this.progress)))) / 2), (float) (AndroidUtilities.dp(210.0f) + y), percentPaint);
            }
            x = (width - AndroidUtilities.dp(240.0f)) / 2;
            y += AndroidUtilities.dp(232.0f);
            progressPaint.setColor(-10327179);
            progressPaint.setAlpha((int) (255.0f * this.animatedAlphaValue));
            canvas.drawRect((float) (x + ((int) (((float) AndroidUtilities.dp(240.0f)) * this.animatedProgressValue))), (float) y, (float) (AndroidUtilities.dp(240.0f) + x), (float) (AndroidUtilities.dp(2.0f) + y), progressPaint);
            progressPaint.setColor(-1);
            progressPaint.setAlpha((int) (255.0f * this.animatedAlphaValue));
            Canvas canvas3 = canvas;
            canvas3.drawRect((float) x, (float) y, (((float) AndroidUtilities.dp(240.0f)) * this.animatedProgressValue) + ((float) x), (float) (AndroidUtilities.dp(2.0f) + y), progressPaint);
            updateAnimation();
        }
        canvas.restore();
    }

    public int getIntrinsicWidth() {
        return this.parentView.getMeasuredWidth();
    }

    public int getIntrinsicHeight() {
        return this.parentView.getMeasuredHeight();
    }

    public int getMinimumWidth() {
        return this.parentView.getMeasuredWidth();
    }

    public int getMinimumHeight() {
        return this.parentView.getMeasuredHeight();
    }

    public int getOpacity() {
        return -1;
    }

    public void onFailedDownload(String name, boolean canceled) {
        checkFileExist();
    }

    public void onSuccessDownload(String name) {
        setProgress(1.0f, true);
        checkFileExist();
    }

    public void onProgressDownload(String fileName, float progress) {
        if (!this.progressVisible) {
            checkFileExist();
        }
        setProgress(progress, true);
    }

    public void onProgressUpload(String fileName, float progress, boolean isEncrypted) {
    }

    public int getObserverTag() {
        return this.TAG;
    }

    public void recycle() {
        DownloadController.getInstance(this.parentMessageObject.currentAccount).removeLoadingFileObserver(this);
        this.parentView = null;
        this.parentMessageObject = null;
    }

    public void checkFileExist() {
        if (this.parentMessageObject == null || this.parentMessageObject.messageOwner.media == null) {
            this.loading = false;
            this.loaded = true;
            this.progressVisible = false;
            setProgress(0.0f, false);
            DownloadController.getInstance(this.parentMessageObject.currentAccount).removeLoadingFileObserver(this);
        } else {
            String fileName = null;
            if ((TextUtils.isEmpty(this.parentMessageObject.messageOwner.attachPath) || !new File(this.parentMessageObject.messageOwner.attachPath).exists()) && !FileLoader.getPathToMessage(this.parentMessageObject.messageOwner).exists()) {
                fileName = FileLoader.getAttachFileName(this.parentMessageObject.getDocument());
            }
            this.loaded = false;
            if (fileName == null) {
                this.progressVisible = false;
                this.loading = false;
                this.loaded = true;
                DownloadController.getInstance(this.parentMessageObject.currentAccount).removeLoadingFileObserver(this);
            } else {
                DownloadController.getInstance(this.parentMessageObject.currentAccount).addLoadingFileObserver(fileName, this);
                this.loading = FileLoader.getInstance(this.parentMessageObject.currentAccount).isLoadingFile(fileName);
                if (this.loading) {
                    this.progressVisible = true;
                    Float progress = ImageLoader.getInstance().getFileProgress(fileName);
                    if (progress == null) {
                        progress = Float.valueOf(0.0f);
                    }
                    setProgress(progress.floatValue(), false);
                } else {
                    this.progressVisible = false;
                }
            }
        }
        this.parentView.invalidate();
    }

    private void updateAnimation() {
        long newTime = System.currentTimeMillis();
        long dt = newTime - this.lastUpdateTime;
        this.lastUpdateTime = newTime;
        if (!(this.animatedProgressValue == 1.0f || this.animatedProgressValue == this.currentProgress)) {
            float progressDiff = this.currentProgress - this.animationProgressStart;
            if (progressDiff > 0.0f) {
                this.currentProgressTime += dt;
                if (this.currentProgressTime >= 300) {
                    this.animatedProgressValue = this.currentProgress;
                    this.animationProgressStart = this.currentProgress;
                    this.currentProgressTime = 0;
                } else {
                    this.animatedProgressValue = this.animationProgressStart + (decelerateInterpolator.getInterpolation(((float) this.currentProgressTime) / 300.0f) * progressDiff);
                }
            }
            this.parentView.invalidate();
        }
        if (this.animatedProgressValue >= 1.0f && this.animatedProgressValue == 1.0f && this.animatedAlphaValue != 0.0f) {
            this.animatedAlphaValue -= ((float) dt) / 200.0f;
            if (this.animatedAlphaValue <= 0.0f) {
                this.animatedAlphaValue = 0.0f;
            }
            this.parentView.invalidate();
        }
    }

    public void setProgress(float value, boolean animated) {
        if (animated) {
            this.animationProgressStart = this.animatedProgressValue;
        } else {
            this.animatedProgressValue = value;
            this.animationProgressStart = value;
        }
        this.progress = String.format("%d%%", new Object[]{Integer.valueOf((int) (100.0f * value))});
        if (value != 1.0f) {
            this.animatedAlphaValue = 1.0f;
        }
        this.currentProgress = value;
        this.currentProgressTime = 0;
        this.lastUpdateTime = System.currentTimeMillis();
        this.parentView.invalidate();
    }

    public float getCurrentProgress() {
        return this.currentProgress;
    }
}
