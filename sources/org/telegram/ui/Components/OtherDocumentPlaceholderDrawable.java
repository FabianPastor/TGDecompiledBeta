package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import java.io.File;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;

public class OtherDocumentPlaceholderDrawable extends RecyclableDrawable implements DownloadController.FileDownloadProgressListener {
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
        progressPaint.setStrokeCap(Paint.Cap.ROUND);
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
        TLRPC.Document document = messageObject.getDocument();
        if (document != null) {
            String documentFileName = FileLoader.getDocumentFileName(messageObject.getDocument());
            this.fileName = documentFileName;
            if (TextUtils.isEmpty(documentFileName)) {
                this.fileName = "name";
            }
            int idx = this.fileName.lastIndexOf(46);
            String upperCase = idx == -1 ? "" : this.fileName.substring(idx + 1).toUpperCase();
            this.ext = upperCase;
            if (((int) Math.ceil((double) docPaint.measureText(upperCase))) > AndroidUtilities.dp(40.0f)) {
                this.ext = TextUtils.ellipsize(this.ext, docPaint, (float) AndroidUtilities.dp(40.0f), TextUtils.TruncateAt.END).toString();
            }
            this.thumbDrawable = context.getResources().getDrawable(AndroidUtilities.getThumbForNameOrMime(this.fileName, messageObject.getDocument().mime_type, true)).mutate();
            this.fileSize = AndroidUtilities.formatFileSize(document.size);
            if (((int) Math.ceil((double) namePaint.measureText(this.fileName))) > AndroidUtilities.dp(320.0f)) {
                this.fileName = TextUtils.ellipsize(this.fileName, namePaint, (float) AndroidUtilities.dp(320.0f), TextUtils.TruncateAt.END).toString();
            }
        }
        checkFileExist();
    }

    public void setColorFilter(ColorFilter colorFilter) {
    }

    public void setAlpha(int alpha) {
        Drawable drawable = this.thumbDrawable;
        if (drawable != null) {
            drawable.setAlpha(alpha);
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
        int offsetY;
        TextPaint paint2;
        String button;
        int w;
        String button2;
        Canvas canvas2 = canvas;
        Rect bounds = getBounds();
        int width = bounds.width();
        int height = bounds.height();
        canvas.save();
        canvas2.translate((float) bounds.left, (float) bounds.top);
        canvas.drawRect(0.0f, 0.0f, (float) width, (float) height, paint);
        int y = (height - AndroidUtilities.dp(240.0f)) / 2;
        int x = (width - AndroidUtilities.dp(48.0f)) / 2;
        this.thumbDrawable.setBounds(x, y, AndroidUtilities.dp(48.0f) + x, AndroidUtilities.dp(48.0f) + y);
        this.thumbDrawable.draw(canvas2);
        canvas2.drawText(this.ext, (float) ((width - ((int) Math.ceil((double) docPaint.measureText(this.ext)))) / 2), (float) (AndroidUtilities.dp(31.0f) + y), docPaint);
        canvas2.drawText(this.fileName, (float) ((width - ((int) Math.ceil((double) namePaint.measureText(this.fileName)))) / 2), (float) (AndroidUtilities.dp(96.0f) + y), namePaint);
        canvas2.drawText(this.fileSize, (float) ((width - ((int) Math.ceil((double) sizePaint.measureText(this.fileSize)))) / 2), (float) (AndroidUtilities.dp(125.0f) + y), sizePaint);
        if (this.loaded) {
            button = LocaleController.getString("OpenFile", NUM);
            paint2 = openPaint;
            offsetY = 0;
        } else {
            if (this.loading) {
                button2 = LocaleController.getString("Cancel", NUM).toUpperCase();
            } else {
                button2 = LocaleController.getString("TapToDownload", NUM);
            }
            int offsetY2 = AndroidUtilities.dp(28.0f);
            button = button2;
            paint2 = buttonPaint;
            offsetY = offsetY2;
        }
        int w2 = (int) Math.ceil((double) paint2.measureText(button));
        canvas2.drawText(button, (float) ((width - w2) / 2), (float) (AndroidUtilities.dp(235.0f) + y + offsetY), paint2);
        if (this.progressVisible) {
            String str = this.progress;
            if (str != null) {
                int w3 = (int) Math.ceil((double) percentPaint.measureText(str));
                canvas2.drawText(this.progress, (float) ((width - w3) / 2), (float) (AndroidUtilities.dp(210.0f) + y), percentPaint);
                w = w3;
            } else {
                w = w2;
            }
            int x2 = (width - AndroidUtilities.dp(240.0f)) / 2;
            int y2 = y + AndroidUtilities.dp(232.0f);
            progressPaint.setColor(-10327179);
            progressPaint.setAlpha((int) (this.animatedAlphaValue * 255.0f));
            int start = (int) (((float) AndroidUtilities.dp(240.0f)) * this.animatedProgressValue);
            int i = start;
            int y3 = y2;
            int x3 = x2;
            canvas.drawRect((float) (x2 + start), (float) y2, (float) (AndroidUtilities.dp(240.0f) + x2), (float) (y2 + AndroidUtilities.dp(2.0f)), progressPaint);
            progressPaint.setColor(-1);
            progressPaint.setAlpha((int) (this.animatedAlphaValue * 255.0f));
            canvas.drawRect((float) x3, (float) y3, (((float) AndroidUtilities.dp(240.0f)) * this.animatedProgressValue) + ((float) x3), (float) (y3 + AndroidUtilities.dp(2.0f)), progressPaint);
            updateAnimation();
            int i2 = y3;
            int i3 = x3;
            int i4 = w;
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

    public void onProgressDownload(String fileName2, long downloadedSize, long totalSize) {
        if (!this.progressVisible) {
            checkFileExist();
        }
        setProgress(Math.min(1.0f, ((float) downloadedSize) / ((float) totalSize)), true);
    }

    public void onProgressUpload(String fileName2, long uploadedSize, long totalSize, boolean isEncrypted) {
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
        MessageObject messageObject = this.parentMessageObject;
        if (messageObject == null || messageObject.messageOwner.media == null) {
            this.loading = false;
            this.loaded = true;
            this.progressVisible = false;
            setProgress(0.0f, false);
            DownloadController.getInstance(this.parentMessageObject.currentAccount).removeLoadingFileObserver(this);
        } else {
            String fileName2 = null;
            if ((TextUtils.isEmpty(this.parentMessageObject.messageOwner.attachPath) || !new File(this.parentMessageObject.messageOwner.attachPath).exists()) && !FileLoader.getInstance(UserConfig.selectedAccount).getPathToMessage(this.parentMessageObject.messageOwner).exists()) {
                fileName2 = FileLoader.getAttachFileName(this.parentMessageObject.getDocument());
            }
            this.loaded = false;
            if (fileName2 == null) {
                this.progressVisible = false;
                this.loading = false;
                this.loaded = true;
                DownloadController.getInstance(this.parentMessageObject.currentAccount).removeLoadingFileObserver(this);
            } else {
                DownloadController.getInstance(this.parentMessageObject.currentAccount).addLoadingFileObserver(fileName2, this);
                boolean isLoadingFile = FileLoader.getInstance(this.parentMessageObject.currentAccount).isLoadingFile(fileName2);
                this.loading = isLoadingFile;
                if (isLoadingFile) {
                    this.progressVisible = true;
                    Float progress2 = ImageLoader.getInstance().getFileProgress(fileName2);
                    if (progress2 == null) {
                        progress2 = Float.valueOf(0.0f);
                    }
                    setProgress(progress2.floatValue(), false);
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
        float f = this.animatedProgressValue;
        if (f != 1.0f) {
            float f2 = this.currentProgress;
            if (f != f2) {
                float f3 = this.animationProgressStart;
                float progressDiff = f2 - f3;
                if (progressDiff > 0.0f) {
                    long j = this.currentProgressTime + dt;
                    this.currentProgressTime = j;
                    if (j >= 300) {
                        this.animatedProgressValue = f2;
                        this.animationProgressStart = f2;
                        this.currentProgressTime = 0;
                    } else {
                        this.animatedProgressValue = f3 + (decelerateInterpolator.getInterpolation(((float) j) / 300.0f) * progressDiff);
                    }
                }
                this.parentView.invalidate();
            }
        }
        float f4 = this.animatedProgressValue;
        if (f4 >= 1.0f && f4 == 1.0f) {
            float f5 = this.animatedAlphaValue;
            if (f5 != 0.0f) {
                float f6 = f5 - (((float) dt) / 200.0f);
                this.animatedAlphaValue = f6;
                if (f6 <= 0.0f) {
                    this.animatedAlphaValue = 0.0f;
                }
                this.parentView.invalidate();
            }
        }
    }

    public void setProgress(float value, boolean animated) {
        if (!animated) {
            this.animatedProgressValue = value;
            this.animationProgressStart = value;
        } else {
            this.animationProgressStart = this.animatedProgressValue;
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
