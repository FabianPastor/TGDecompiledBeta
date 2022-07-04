package org.telegram.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.view.View;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.RLottieDrawable;

public class DownloadProgressIcon extends View implements NotificationCenter.NotificationCenterDelegate {
    private int currentAccount;
    int currentColor;
    ArrayList<ProgressObserver> currentListeners = new ArrayList<>();
    float currentProgress;
    RLottieDrawable downloadCompleteDrawable;
    ImageReceiver downloadCompleteImageReceiver = new ImageReceiver(this);
    RLottieDrawable downloadDrawable;
    ImageReceiver downloadImageReceiver = new ImageReceiver(this);
    boolean hasUnviewedDownloads;
    Paint paint = new Paint(1);
    Paint paint2 = new Paint(1);
    float progress;
    float progressDt;
    boolean showCompletedIcon;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public DownloadProgressIcon(int currentAccount2, Context context) {
        super(context);
        this.currentAccount = currentAccount2;
        RLottieDrawable rLottieDrawable = new RLottieDrawable(NUM, "download_progress", AndroidUtilities.dp(28.0f), AndroidUtilities.dp(28.0f), true, (int[]) null);
        this.downloadDrawable = rLottieDrawable;
        rLottieDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("actionBarDefaultIcon"), PorterDuff.Mode.MULTIPLY));
        RLottieDrawable rLottieDrawable2 = new RLottieDrawable(NUM, "download_finish", AndroidUtilities.dp(28.0f), AndroidUtilities.dp(28.0f), true, (int[]) null);
        this.downloadCompleteDrawable = rLottieDrawable2;
        rLottieDrawable2.setColorFilter(new PorterDuffColorFilter(Theme.getColor("actionBarDefaultIcon"), PorterDuff.Mode.MULTIPLY));
        this.downloadImageReceiver.setImageBitmap((Drawable) this.downloadDrawable);
        this.downloadCompleteImageReceiver.setImageBitmap((Drawable) this.downloadCompleteDrawable);
        this.downloadImageReceiver.setAutoRepeat(1);
        this.downloadDrawable.setAutoRepeat(1);
        this.downloadDrawable.start();
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), NUM), View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(heightMeasureSpec), NUM));
        int padding = AndroidUtilities.dp(15.0f);
        this.downloadImageReceiver.setImageCoords((float) padding, (float) padding, (float) (getMeasuredWidth() - (padding * 2)), (float) (getMeasuredHeight() - (padding * 2)));
        this.downloadCompleteImageReceiver.setImageCoords((float) padding, (float) padding, (float) (getMeasuredWidth() - (padding * 2)), (float) (getMeasuredHeight() - (padding * 2)));
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (getAlpha() != 0.0f) {
            if (this.currentColor != Theme.getColor("actionBarDefaultIcon")) {
                this.currentColor = Theme.getColor("actionBarDefaultIcon");
                this.paint.setColor(Theme.getColor("actionBarDefaultIcon"));
                this.paint2.setColor(Theme.getColor("actionBarDefaultIcon"));
                this.downloadImageReceiver.setColorFilter(new PorterDuffColorFilter(Theme.getColor("actionBarDefaultIcon"), PorterDuff.Mode.MULTIPLY));
                this.downloadCompleteImageReceiver.setColorFilter(new PorterDuffColorFilter(Theme.getColor("actionBarDefaultIcon"), PorterDuff.Mode.MULTIPLY));
                this.paint2.setAlpha(100);
            }
            float f = this.currentProgress;
            float f2 = this.progress;
            if (f != f2) {
                float f3 = this.progressDt;
                float f4 = f + f3;
                this.currentProgress = f4;
                if (f3 > 0.0f && f4 > f2) {
                    this.currentProgress = f2;
                } else if (f3 >= 0.0f || f4 >= f2) {
                    invalidate();
                } else {
                    this.currentProgress = f2;
                }
            }
            int cy = (getMeasuredHeight() / 2) + AndroidUtilities.dp(8.0f);
            float r = (float) AndroidUtilities.dp(1.0f);
            float startPadding = (float) AndroidUtilities.dp(16.0f);
            float width = ((float) getMeasuredWidth()) - (2.0f * startPadding);
            AndroidUtilities.rectTmp.set(startPadding, ((float) cy) - r, ((float) getMeasuredWidth()) - startPadding, ((float) cy) + r);
            canvas.drawRoundRect(AndroidUtilities.rectTmp, r, r, this.paint2);
            AndroidUtilities.rectTmp.set(startPadding, ((float) cy) - r, (this.currentProgress * width) + startPadding, ((float) cy) + r);
            canvas.drawRoundRect(AndroidUtilities.rectTmp, r, r, this.paint);
            canvas.save();
            canvas.clipRect(0.0f, 0.0f, (float) getMeasuredWidth(), ((float) cy) - r);
            if (this.progress != 1.0f) {
                this.showCompletedIcon = false;
            }
            if (this.showCompletedIcon) {
                this.downloadCompleteImageReceiver.draw(canvas);
            } else {
                this.downloadImageReceiver.draw(canvas);
            }
            if (this.progress == 1.0f && !this.showCompletedIcon && this.downloadDrawable.getCurrentFrame() == 0) {
                this.downloadCompleteDrawable.setCurrentFrame(0, false);
                this.downloadCompleteDrawable.start();
                this.showCompletedIcon = true;
            }
            canvas.restore();
        }
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        updateDownloadingListeners();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.onDownloadingFilesChanged);
        this.downloadImageReceiver.onAttachedToWindow();
        this.downloadCompleteImageReceiver.onAttachedToWindow();
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        detachCurrentListeners();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.onDownloadingFilesChanged);
        this.downloadImageReceiver.onDetachedFromWindow();
        this.downloadCompleteImageReceiver.onDetachedFromWindow();
    }

    private void updateDownloadingListeners() {
        DownloadController downloadController = DownloadController.getInstance(this.currentAccount);
        HashMap<String, ProgressObserver> observerHashMap = new HashMap<>();
        for (int i = 0; i < this.currentListeners.size(); i++) {
            observerHashMap.put(this.currentListeners.get(i).fileName, this.currentListeners.get(i));
            DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this.currentListeners.get(i));
        }
        this.currentListeners.clear();
        for (int i2 = 0; i2 < downloadController.downloadingFiles.size(); i2++) {
            String filename = downloadController.downloadingFiles.get(i2).getFileName();
            if (FileLoader.getInstance(this.currentAccount).isLoadingFile(filename)) {
                ProgressObserver progressObserver = observerHashMap.get(filename);
                if (progressObserver == null) {
                    progressObserver = new ProgressObserver(filename);
                }
                DownloadController.getInstance(this.currentAccount).addLoadingFileObserver(filename, progressObserver);
                this.currentListeners.add(progressObserver);
            }
        }
        if (this.currentListeners.size() != 0) {
            return;
        }
        if (getVisibility() != 0 || getAlpha() != 1.0f) {
            this.progress = 0.0f;
            this.currentProgress = 0.0f;
        }
    }

    public void updateProgress() {
        MessagesStorage instance = MessagesStorage.getInstance(this.currentAccount);
        long total = 0;
        long downloaded = 0;
        for (int i = 0; i < this.currentListeners.size(); i++) {
            total += this.currentListeners.get(i).total;
            downloaded += this.currentListeners.get(i).downloaded;
        }
        if (total == 0) {
            this.progress = 1.0f;
        } else {
            this.progress = ((float) downloaded) / ((float) total);
        }
        float f = this.progress;
        if (f > 1.0f) {
            this.progress = 1.0f;
        } else if (f < 0.0f) {
            this.progress = 0.0f;
        }
        this.progressDt = ((this.progress - this.currentProgress) * 16.0f) / 150.0f;
        invalidate();
    }

    private void detachCurrentListeners() {
        for (int i = 0; i < this.currentListeners.size(); i++) {
            DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this.currentListeners.get(i));
        }
        this.currentListeners.clear();
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.onDownloadingFilesChanged) {
            updateDownloadingListeners();
            updateProgress();
        }
    }

    private class ProgressObserver implements DownloadController.FileDownloadProgressListener {
        long downloaded;
        /* access modifiers changed from: private */
        public final String fileName;
        long total;

        private ProgressObserver(String fileName2) {
            this.fileName = fileName2;
        }

        public void onFailedDownload(String fileName2, boolean canceled) {
        }

        public void onSuccessDownload(String fileName2) {
        }

        public void onProgressDownload(String fileName2, long downloadSize, long totalSize) {
            this.downloaded = downloadSize;
            this.total = totalSize;
            DownloadProgressIcon.this.updateProgress();
        }

        public void onProgressUpload(String fileName2, long downloadSize, long totalSize, boolean isEncrypted) {
        }

        public int getObserverTag() {
            return 0;
        }
    }
}
