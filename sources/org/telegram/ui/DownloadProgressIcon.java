package org.telegram.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
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
    Paint paint = new Paint(1);
    Paint paint2 = new Paint(1);
    float progress;
    float progressDt;
    boolean showCompletedIcon;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public DownloadProgressIcon(int i, Context context) {
        super(context);
        this.currentAccount = i;
        this.downloadDrawable = new RLottieDrawable(NUM, "download_progress", AndroidUtilities.dp(28.0f), AndroidUtilities.dp(28.0f), true, (int[]) null);
        this.downloadCompleteDrawable = new RLottieDrawable(NUM, "download_finish", AndroidUtilities.dp(28.0f), AndroidUtilities.dp(28.0f), true, (int[]) null);
        this.downloadImageReceiver.setImageBitmap((Drawable) this.downloadDrawable);
        this.downloadCompleteImageReceiver.setImageBitmap((Drawable) this.downloadCompleteDrawable);
        this.downloadImageReceiver.setAutoRepeat(1);
        this.downloadDrawable.setAutoRepeat(1);
        this.downloadDrawable.start();
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i2), NUM));
        int dp = AndroidUtilities.dp(15.0f);
        float f = (float) dp;
        int i3 = dp * 2;
        this.downloadImageReceiver.setImageCoords(f, f, (float) (getMeasuredWidth() - i3), (float) (getMeasuredHeight() - i3));
        this.downloadCompleteImageReceiver.setImageCoords(f, f, (float) (getMeasuredWidth() - i3), (float) (getMeasuredHeight() - i3));
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
            float dp = (float) AndroidUtilities.dp(1.0f);
            float dp2 = (float) AndroidUtilities.dp(16.0f);
            float measuredWidth = ((float) getMeasuredWidth()) - (2.0f * dp2);
            RectF rectF = AndroidUtilities.rectTmp;
            float measuredHeight = (float) ((getMeasuredHeight() / 2) + AndroidUtilities.dp(8.0f));
            float f5 = measuredHeight - dp;
            float f6 = measuredHeight + dp;
            rectF.set(dp2, f5, ((float) getMeasuredWidth()) - dp2, f6);
            canvas.drawRoundRect(rectF, dp, dp, this.paint2);
            rectF.set(dp2, f5, (measuredWidth * this.currentProgress) + dp2, f6);
            canvas.drawRoundRect(rectF, dp, dp, this.paint);
            canvas.save();
            canvas.clipRect(0.0f, 0.0f, (float) getMeasuredWidth(), f5);
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
        DownloadController instance = DownloadController.getInstance(this.currentAccount);
        HashMap hashMap = new HashMap();
        for (int i = 0; i < this.currentListeners.size(); i++) {
            hashMap.put(this.currentListeners.get(i).fileName, this.currentListeners.get(i));
            DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this.currentListeners.get(i));
        }
        this.currentListeners.clear();
        for (int i2 = 0; i2 < instance.downloadingFiles.size(); i2++) {
            String fileName = instance.downloadingFiles.get(i2).getFileName();
            if (FileLoader.getInstance(this.currentAccount).isLoadingFile(fileName)) {
                ProgressObserver progressObserver = (ProgressObserver) hashMap.get(fileName);
                if (progressObserver == null) {
                    progressObserver = new ProgressObserver(fileName);
                }
                DownloadController.getInstance(this.currentAccount).addLoadingFileObserver(fileName, progressObserver);
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
        MessagesStorage.getInstance(this.currentAccount);
        long j = 0;
        long j2 = 0;
        for (int i = 0; i < this.currentListeners.size(); i++) {
            j += this.currentListeners.get(i).total;
            j2 += this.currentListeners.get(i).downloaded;
        }
        if (j == 0) {
            this.progress = 1.0f;
        } else {
            this.progress = ((float) j2) / ((float) j);
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

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.onDownloadingFilesChanged) {
            updateDownloadingListeners();
            updateProgress();
        }
    }

    private class ProgressObserver implements DownloadController.FileDownloadProgressListener {
        long downloaded;
        /* access modifiers changed from: private */
        public final String fileName;
        long total;

        public int getObserverTag() {
            return 0;
        }

        public void onFailedDownload(String str, boolean z) {
        }

        public void onProgressUpload(String str, long j, long j2, boolean z) {
        }

        public void onSuccessDownload(String str) {
        }

        private ProgressObserver(String str) {
            this.fileName = str;
        }

        public void onProgressDownload(String str, long j, long j2) {
            this.downloaded = j;
            this.total = j2;
            DownloadProgressIcon.this.updateProgress();
        }
    }
}
