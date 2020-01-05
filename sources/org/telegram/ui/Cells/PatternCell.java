package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.text.TextUtils;
import java.io.File;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.DownloadController.FileDownloadProgressListener;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.MediaController.SearchImage;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC.Photo;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_wallPaper;
import org.telegram.ui.Components.BackgroundGradientDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.RadialProgress2;

public class PatternCell extends BackupImageView implements FileDownloadProgressListener {
    private int TAG;
    private Paint backgroundPaint;
    private int currentAccount = UserConfig.selectedAccount;
    private int currentGradientAngle;
    private int currentGradientColor1;
    private int currentGradientColor2;
    private TL_wallPaper currentPattern;
    private PatternCellDelegate delegate;
    private LinearGradient gradientShader;
    private int maxWallpaperSize;
    private RadialProgress2 radialProgress;
    private RectF rect = new RectF();
    private boolean wasSelected;

    public interface PatternCellDelegate {
        int getBackgroundColor();

        int getBackgroundGradientAngle();

        int getBackgroundGradientColor();

        int getPatternColor();

        TL_wallPaper getSelectedPattern();
    }

    public void onProgressUpload(String str, float f, boolean z) {
    }

    public PatternCell(Context context, int i, PatternCellDelegate patternCellDelegate) {
        super(context);
        setRoundRadius(AndroidUtilities.dp(6.0f));
        this.maxWallpaperSize = i;
        this.delegate = patternCellDelegate;
        this.radialProgress = new RadialProgress2(this);
        this.radialProgress.setProgressRect(AndroidUtilities.dp(30.0f), AndroidUtilities.dp(30.0f), AndroidUtilities.dp(70.0f), AndroidUtilities.dp(70.0f));
        this.backgroundPaint = new Paint(1);
        this.TAG = DownloadController.getInstance(this.currentAccount).generateObserverTag();
    }

    public void setPattern(TL_wallPaper tL_wallPaper) {
        this.currentPattern = tL_wallPaper;
        if (tL_wallPaper != null) {
            setImage(ImageLocation.getForDocument(FileLoader.getClosestPhotoSizeWithSize(tL_wallPaper.document.thumbs, 100), tL_wallPaper.document), "100_100", null, null, "jpg", 0, 1, tL_wallPaper);
        } else {
            setImageDrawable(null);
        }
        updateSelected(false);
    }

    /* Access modifiers changed, original: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        updateSelected(false);
    }

    /* JADX WARNING: Removed duplicated region for block: B:12:0x0024  */
    /* JADX WARNING: Removed duplicated region for block: B:11:0x0020  */
    /* JADX WARNING: Missing block: B:7:0x0019, code skipped:
            if (r1.id == r0.id) goto L_0x001b;
     */
    public void updateSelected(boolean r8) {
        /*
        r7 = this;
        r0 = r7.delegate;
        r0 = r0.getSelectedPattern();
        r1 = r7.currentPattern;
        r2 = 0;
        if (r1 != 0) goto L_0x000d;
    L_0x000b:
        if (r0 == 0) goto L_0x001b;
    L_0x000d:
        if (r0 == 0) goto L_0x001d;
    L_0x000f:
        r1 = r7.currentPattern;
        if (r1 == 0) goto L_0x001d;
    L_0x0013:
        r3 = r1.id;
        r5 = r0.id;
        r1 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1));
        if (r1 != 0) goto L_0x001d;
    L_0x001b:
        r1 = 1;
        goto L_0x001e;
    L_0x001d:
        r1 = 0;
    L_0x001e:
        if (r1 == 0) goto L_0x0024;
    L_0x0020:
        r7.updateButtonState(r0, r2, r8);
        goto L_0x002a;
    L_0x0024:
        r0 = r7.radialProgress;
        r1 = 4;
        r0.setIcon(r1, r2, r8);
    L_0x002a:
        r7.invalidate();
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.PatternCell.updateSelected(boolean):void");
    }

    private void updateButtonState(Object obj, boolean z, boolean z2) {
        boolean z3 = obj instanceof TL_wallPaper;
        if (z3 || (obj instanceof SearchImage)) {
            String attachFileName;
            File pathToAttach;
            if (z3) {
                TL_wallPaper tL_wallPaper = (TL_wallPaper) obj;
                attachFileName = FileLoader.getAttachFileName(tL_wallPaper.document);
                if (!TextUtils.isEmpty(attachFileName)) {
                    pathToAttach = FileLoader.getPathToAttach(tL_wallPaper.document, true);
                } else {
                    return;
                }
            }
            SearchImage searchImage = (SearchImage) obj;
            Photo photo = searchImage.photo;
            if (photo != null) {
                PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, this.maxWallpaperSize, true);
                File pathToAttach2 = FileLoader.getPathToAttach(closestPhotoSizeWithSize, true);
                attachFileName = FileLoader.getAttachFileName(closestPhotoSizeWithSize);
                pathToAttach = pathToAttach2;
            } else {
                pathToAttach = ImageLoader.getHttpFilePath(searchImage.imageUrl, "jpg");
                attachFileName = pathToAttach.getName();
            }
            if (TextUtils.isEmpty(attachFileName)) {
                return;
            }
            if (pathToAttach.exists()) {
                DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
                this.radialProgress.setProgress(1.0f, z2);
                this.radialProgress.setIcon(6, z, z2);
            } else {
                DownloadController.getInstance(this.currentAccount).addLoadingFileObserver(attachFileName, null, this);
                FileLoader.getInstance(this.currentAccount).isLoadingFile(attachFileName);
                Float fileProgress = ImageLoader.getInstance().getFileProgress(attachFileName);
                if (fileProgress != null) {
                    this.radialProgress.setProgress(fileProgress.floatValue(), z2);
                } else {
                    this.radialProgress.setProgress(0.0f, z2);
                }
                this.radialProgress.setIcon(10, z, z2);
            }
        } else {
            this.radialProgress.setIcon(6, z, z2);
        }
    }

    /* Access modifiers changed, original: protected */
    public void onDraw(Canvas canvas) {
        getImageReceiver().setAlpha(0.8f);
        int backgroundColor = this.delegate.getBackgroundColor();
        int backgroundGradientColor = this.delegate.getBackgroundGradientColor();
        int backgroundGradientAngle = this.delegate.getBackgroundGradientAngle();
        int patternColor = this.delegate.getPatternColor();
        if (backgroundGradientColor == 0) {
            this.gradientShader = null;
        } else if (!(this.gradientShader != null && backgroundColor == this.currentGradientColor1 && backgroundGradientColor == this.currentGradientColor2 && backgroundGradientAngle == this.currentGradientAngle)) {
            this.currentGradientColor1 = backgroundColor;
            this.currentGradientColor2 = backgroundGradientColor;
            this.currentGradientAngle = backgroundGradientAngle;
            Rect gradientPoints = BackgroundGradientDrawable.getGradientPoints(this.currentGradientAngle, getMeasuredWidth(), getMeasuredHeight());
            this.gradientShader = new LinearGradient((float) gradientPoints.left, (float) gradientPoints.top, (float) gradientPoints.right, (float) gradientPoints.bottom, new int[]{backgroundColor, backgroundGradientColor}, null, TileMode.CLAMP);
        }
        this.backgroundPaint.setShader(this.gradientShader);
        if (this.gradientShader == null) {
            this.backgroundPaint.setColor(backgroundColor);
        }
        this.rect.set(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight());
        canvas.drawRoundRect(this.rect, (float) AndroidUtilities.dp(6.0f), (float) AndroidUtilities.dp(6.0f), this.backgroundPaint);
        super.onDraw(canvas);
        this.radialProgress.setColors(patternColor, patternColor, -1, -1);
        this.radialProgress.draw(canvas);
    }

    /* Access modifiers changed, original: protected */
    public void onMeasure(int i, int i2) {
        setMeasuredDimension(AndroidUtilities.dp(100.0f), AndroidUtilities.dp(100.0f));
    }

    public void onFailedDownload(String str, boolean z) {
        if (z) {
            this.radialProgress.setIcon(4, false, true);
        } else {
            updateButtonState(this.currentPattern, true, z);
        }
    }

    public void onSuccessDownload(String str) {
        this.radialProgress.setProgress(1.0f, true);
        updateButtonState(this.currentPattern, false, true);
    }

    public void onProgressDownload(String str, float f) {
        this.radialProgress.setProgress(f, true);
        if (this.radialProgress.getIcon() != 10) {
            updateButtonState(this.currentPattern, false, true);
        }
    }

    public int getObserverTag() {
        return this.TAG;
    }
}
