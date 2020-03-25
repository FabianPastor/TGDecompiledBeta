package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import java.io.File;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC$Photo;
import org.telegram.tgnet.TLRPC$PhotoSize;
import org.telegram.tgnet.TLRPC$TL_wallPaper;
import org.telegram.ui.Components.BackgroundGradientDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.RadialProgress2;

public class PatternCell extends BackupImageView implements DownloadController.FileDownloadProgressListener {
    private int TAG;
    private Paint backgroundPaint;
    private int currentAccount = UserConfig.selectedAccount;
    private int currentGradientAngle;
    private int currentGradientColor1;
    private int currentGradientColor2;
    private TLRPC$TL_wallPaper currentPattern;
    private PatternCellDelegate delegate;
    private LinearGradient gradientShader;
    private int maxWallpaperSize;
    private RadialProgress2 radialProgress;
    private RectF rect = new RectF();

    public interface PatternCellDelegate {
        int getBackgroundColor();

        int getBackgroundGradientAngle();

        int getBackgroundGradientColor();

        int getPatternColor();

        TLRPC$TL_wallPaper getSelectedPattern();
    }

    public void onProgressUpload(String str, long j, long j2, boolean z) {
    }

    public PatternCell(Context context, int i, PatternCellDelegate patternCellDelegate) {
        super(context);
        setRoundRadius(AndroidUtilities.dp(6.0f));
        this.maxWallpaperSize = i;
        this.delegate = patternCellDelegate;
        RadialProgress2 radialProgress2 = new RadialProgress2(this);
        this.radialProgress = radialProgress2;
        radialProgress2.setProgressRect(AndroidUtilities.dp(30.0f), AndroidUtilities.dp(30.0f), AndroidUtilities.dp(70.0f), AndroidUtilities.dp(70.0f));
        this.backgroundPaint = new Paint(1);
        this.TAG = DownloadController.getInstance(this.currentAccount).generateObserverTag();
    }

    public void setPattern(TLRPC$TL_wallPaper tLRPC$TL_wallPaper) {
        this.currentPattern = tLRPC$TL_wallPaper;
        if (tLRPC$TL_wallPaper != null) {
            setImage(ImageLocation.getForDocument(FileLoader.getClosestPhotoSizeWithSize(tLRPC$TL_wallPaper.document.thumbs, 100), tLRPC$TL_wallPaper.document), "100_100", (ImageLocation) null, (String) null, "jpg", 0, 1, tLRPC$TL_wallPaper);
        } else {
            setImageDrawable((Drawable) null);
        }
        updateSelected(false);
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        updateSelected(false);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:4:0x000f, code lost:
        r1 = r7.currentPattern;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updateSelected(boolean r8) {
        /*
            r7 = this;
            org.telegram.ui.Cells.PatternCell$PatternCellDelegate r0 = r7.delegate
            org.telegram.tgnet.TLRPC$TL_wallPaper r0 = r0.getSelectedPattern()
            org.telegram.tgnet.TLRPC$TL_wallPaper r1 = r7.currentPattern
            r2 = 0
            if (r1 != 0) goto L_0x000d
            if (r0 == 0) goto L_0x001b
        L_0x000d:
            if (r0 == 0) goto L_0x001d
            org.telegram.tgnet.TLRPC$TL_wallPaper r1 = r7.currentPattern
            if (r1 == 0) goto L_0x001d
            long r3 = r1.id
            long r5 = r0.id
            int r1 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r1 != 0) goto L_0x001d
        L_0x001b:
            r1 = 1
            goto L_0x001e
        L_0x001d:
            r1 = 0
        L_0x001e:
            if (r1 == 0) goto L_0x0024
            r7.updateButtonState(r0, r2, r8)
            goto L_0x002a
        L_0x0024:
            org.telegram.ui.Components.RadialProgress2 r0 = r7.radialProgress
            r1 = 4
            r0.setIcon(r1, r2, r8)
        L_0x002a:
            r7.invalidate()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.PatternCell.updateSelected(boolean):void");
    }

    private void updateButtonState(Object obj, boolean z, boolean z2) {
        File file;
        String str;
        boolean z3 = obj instanceof TLRPC$TL_wallPaper;
        if (z3 || (obj instanceof MediaController.SearchImage)) {
            if (z3) {
                TLRPC$TL_wallPaper tLRPC$TL_wallPaper = (TLRPC$TL_wallPaper) obj;
                str = FileLoader.getAttachFileName(tLRPC$TL_wallPaper.document);
                if (!TextUtils.isEmpty(str)) {
                    file = FileLoader.getPathToAttach(tLRPC$TL_wallPaper.document, true);
                } else {
                    return;
                }
            } else {
                MediaController.SearchImage searchImage = (MediaController.SearchImage) obj;
                TLRPC$Photo tLRPC$Photo = searchImage.photo;
                if (tLRPC$Photo != null) {
                    TLRPC$PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(tLRPC$Photo.sizes, this.maxWallpaperSize, true);
                    File pathToAttach = FileLoader.getPathToAttach(closestPhotoSizeWithSize, true);
                    str = FileLoader.getAttachFileName(closestPhotoSizeWithSize);
                    file = pathToAttach;
                } else {
                    file = ImageLoader.getHttpFilePath(searchImage.imageUrl, "jpg");
                    str = file.getName();
                }
                if (TextUtils.isEmpty(str)) {
                    return;
                }
            }
            if (file.exists()) {
                DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
                this.radialProgress.setProgress(1.0f, z2);
                this.radialProgress.setIcon(6, z, z2);
                return;
            }
            DownloadController.getInstance(this.currentAccount).addLoadingFileObserver(str, (MessageObject) null, this);
            FileLoader.getInstance(this.currentAccount).isLoadingFile(str);
            Float fileProgress = ImageLoader.getInstance().getFileProgress(str);
            if (fileProgress != null) {
                this.radialProgress.setProgress(fileProgress.floatValue(), z2);
            } else {
                this.radialProgress.setProgress(0.0f, z2);
            }
            this.radialProgress.setIcon(10, z, z2);
            return;
        }
        this.radialProgress.setIcon(6, z, z2);
    }

    /* access modifiers changed from: protected */
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
            Rect gradientPoints = BackgroundGradientDrawable.getGradientPoints(backgroundGradientAngle, getMeasuredWidth(), getMeasuredHeight());
            this.gradientShader = new LinearGradient((float) gradientPoints.left, (float) gradientPoints.top, (float) gradientPoints.right, (float) gradientPoints.bottom, new int[]{backgroundColor, backgroundGradientColor}, (float[]) null, Shader.TileMode.CLAMP);
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

    /* access modifiers changed from: protected */
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

    public void onProgressDownload(String str, long j, long j2) {
        this.radialProgress.setProgress(Math.min(1.0f, ((float) j) / ((float) j2)), true);
        if (this.radialProgress.getIcon() != 10) {
            updateButtonState(this.currentPattern, false, true);
        }
    }

    public int getObserverTag() {
        return this.TAG;
    }
}
