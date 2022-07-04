package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BlendMode;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewOutlineProvider;
import java.io.File;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Components.BackgroundGradientDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.MotionBackgroundDrawable;
import org.telegram.ui.Components.RadialProgress2;

public class PatternCell extends BackupImageView implements DownloadController.FileDownloadProgressListener {
    private final int SIZE = 100;
    private int TAG;
    private MotionBackgroundDrawable backgroundDrawable;
    private Paint backgroundPaint;
    private int currentAccount = UserConfig.selectedAccount;
    private int currentBackgroundColor;
    private int currentGradientAngle;
    private int currentGradientColor1;
    private int currentGradientColor2;
    private int currentGradientColor3;
    private TLRPC.TL_wallPaper currentPattern;
    private PatternCellDelegate delegate;
    private LinearGradient gradientShader;
    private int maxWallpaperSize;
    private RadialProgress2 radialProgress;
    private RectF rect = new RectF();
    private boolean wasSelected;

    public interface PatternCellDelegate {
        int getBackgroundColor();

        int getBackgroundGradientAngle();

        int getBackgroundGradientColor1();

        int getBackgroundGradientColor2();

        int getBackgroundGradientColor3();

        int getCheckColor();

        float getIntensity();

        int getPatternColor();

        TLRPC.TL_wallPaper getSelectedPattern();
    }

    public PatternCell(Context context, int maxSize, PatternCellDelegate patternCellDelegate) {
        super(context);
        setRoundRadius(AndroidUtilities.dp(6.0f));
        this.maxWallpaperSize = maxSize;
        this.delegate = patternCellDelegate;
        RadialProgress2 radialProgress2 = new RadialProgress2(this);
        this.radialProgress = radialProgress2;
        radialProgress2.setProgressRect(AndroidUtilities.dp(30.0f), AndroidUtilities.dp(30.0f), AndroidUtilities.dp(70.0f), AndroidUtilities.dp(70.0f));
        this.backgroundPaint = new Paint(3);
        this.TAG = DownloadController.getInstance(this.currentAccount).generateObserverTag();
        if (Build.VERSION.SDK_INT >= 21) {
            setOutlineProvider(new ViewOutlineProvider() {
                public void getOutline(View view, Outline outline) {
                    outline.setRoundRect(AndroidUtilities.dp(1.0f), AndroidUtilities.dp(1.0f), view.getMeasuredWidth() - AndroidUtilities.dp(1.0f), view.getMeasuredHeight() - AndroidUtilities.dp(1.0f), (float) AndroidUtilities.dp(6.0f));
                }
            });
            setClipToOutline(true);
        }
    }

    public void setPattern(TLRPC.TL_wallPaper wallPaper) {
        this.currentPattern = wallPaper;
        if (wallPaper != null) {
            setImage(ImageLocation.getForDocument(FileLoader.getClosestPhotoSizeWithSize(wallPaper.document.thumbs, AndroidUtilities.dp(100.0f)), wallPaper.document), "100_100", (ImageLocation) null, (String) null, "png", 0, 1, wallPaper);
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

    public void updateSelected(boolean animated) {
        TLRPC.TL_wallPaper selectedPattern = this.delegate.getSelectedPattern();
        TLRPC.TL_wallPaper tL_wallPaper = this.currentPattern;
        if ((tL_wallPaper == null && selectedPattern == null) || !(selectedPattern == null || tL_wallPaper == null || tL_wallPaper.id != selectedPattern.id)) {
            updateButtonState(selectedPattern, false, animated);
        } else {
            this.radialProgress.setIcon(4, false, animated);
        }
        invalidate();
    }

    public void invalidate() {
        super.invalidate();
    }

    private void updateButtonState(Object image, boolean ifSame, boolean animated) {
        String fileName;
        File path;
        File path2;
        if ((image instanceof TLRPC.TL_wallPaper) || (image instanceof MediaController.SearchImage)) {
            if (image instanceof TLRPC.TL_wallPaper) {
                TLRPC.TL_wallPaper wallPaper = (TLRPC.TL_wallPaper) image;
                fileName = FileLoader.getAttachFileName(wallPaper.document);
                if (!TextUtils.isEmpty(fileName)) {
                    path = FileLoader.getInstance(this.currentAccount).getPathToAttach(wallPaper.document, true);
                } else {
                    return;
                }
            } else {
                MediaController.SearchImage wallPaper2 = (MediaController.SearchImage) image;
                if (wallPaper2.photo != null) {
                    TLRPC.PhotoSize photoSize = FileLoader.getClosestPhotoSizeWithSize(wallPaper2.photo.sizes, this.maxWallpaperSize, true);
                    path2 = FileLoader.getInstance(this.currentAccount).getPathToAttach(photoSize, true);
                    fileName = FileLoader.getAttachFileName(photoSize);
                } else {
                    path2 = ImageLoader.getHttpFilePath(wallPaper2.imageUrl, "jpg");
                    fileName = path2.getName();
                }
                if (!TextUtils.isEmpty(fileName)) {
                    path = path2;
                } else {
                    return;
                }
            }
            if (path.exists()) {
                DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
                this.radialProgress.setProgress(1.0f, animated);
                this.radialProgress.setIcon(6, ifSame, animated);
                return;
            }
            DownloadController.getInstance(this.currentAccount).addLoadingFileObserver(fileName, (MessageObject) null, this);
            boolean isLoadingFile = FileLoader.getInstance(this.currentAccount).isLoadingFile(fileName);
            Float progress = ImageLoader.getInstance().getFileProgress(fileName);
            if (progress != null) {
                this.radialProgress.setProgress(progress.floatValue(), animated);
            } else {
                this.radialProgress.setProgress(0.0f, animated);
            }
            this.radialProgress.setIcon(10, ifSame, animated);
            return;
        }
        this.radialProgress.setIcon(6, ifSame, animated);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        int checkColor;
        int i;
        float f;
        Canvas canvas2 = canvas;
        float intensity = this.delegate.getIntensity();
        this.imageReceiver.setBlendMode((Object) null);
        int backgroundColor = this.delegate.getBackgroundColor();
        int backgroundGradientColor1 = this.delegate.getBackgroundGradientColor1();
        int backgroundGradientColor2 = this.delegate.getBackgroundGradientColor2();
        int backgroundGradientColor3 = this.delegate.getBackgroundGradientColor3();
        int backgroundGradientAngle = this.delegate.getBackgroundGradientAngle();
        int checkColor2 = this.delegate.getCheckColor();
        if (backgroundGradientColor1 == 0) {
            checkColor = checkColor2;
            i = 0;
            this.gradientShader = null;
            this.backgroundDrawable = null;
            this.imageReceiver.setGradientBitmap((Bitmap) null);
        } else if (this.gradientShader != null && backgroundColor == this.currentBackgroundColor && backgroundGradientColor1 == this.currentGradientColor1 && backgroundGradientColor2 == this.currentGradientColor2 && backgroundGradientColor3 == this.currentGradientColor3 && backgroundGradientAngle == this.currentGradientAngle) {
            checkColor = checkColor2;
            i = 0;
        } else {
            this.currentBackgroundColor = backgroundColor;
            this.currentGradientColor1 = backgroundGradientColor1;
            this.currentGradientColor2 = backgroundGradientColor2;
            this.currentGradientColor3 = backgroundGradientColor3;
            this.currentGradientAngle = backgroundGradientAngle;
            if (backgroundGradientColor2 != 0) {
                this.gradientShader = null;
                MotionBackgroundDrawable motionBackgroundDrawable = this.backgroundDrawable;
                if (motionBackgroundDrawable != null) {
                    f = 0.0f;
                    checkColor = checkColor2;
                    motionBackgroundDrawable.setColors(backgroundColor, backgroundGradientColor1, backgroundGradientColor2, backgroundGradientColor3, 0, false);
                } else {
                    checkColor = checkColor2;
                    f = 0.0f;
                    MotionBackgroundDrawable motionBackgroundDrawable2 = new MotionBackgroundDrawable(backgroundColor, backgroundGradientColor1, backgroundGradientColor2, backgroundGradientColor3, true);
                    this.backgroundDrawable = motionBackgroundDrawable2;
                    motionBackgroundDrawable2.setRoundRadius(AndroidUtilities.dp(6.0f));
                    this.backgroundDrawable.setParentView(this);
                }
                if (intensity < f) {
                    this.imageReceiver.setGradientBitmap(this.backgroundDrawable.getBitmap());
                    i = 0;
                } else {
                    this.imageReceiver.setGradientBitmap((Bitmap) null);
                    if (Build.VERSION.SDK_INT >= 29) {
                        this.imageReceiver.setBlendMode(BlendMode.SOFT_LIGHT);
                        i = 0;
                    } else {
                        this.imageReceiver.setColorFilter(new PorterDuffColorFilter(this.delegate.getPatternColor(), PorterDuff.Mode.SRC_IN));
                        i = 0;
                    }
                }
            } else {
                checkColor = checkColor2;
                Rect r = BackgroundGradientDrawable.getGradientPoints(backgroundGradientAngle, getMeasuredWidth(), getMeasuredHeight());
                i = 0;
                this.gradientShader = new LinearGradient((float) r.left, (float) r.top, (float) r.right, (float) r.bottom, new int[]{backgroundColor, backgroundGradientColor1}, (float[]) null, Shader.TileMode.CLAMP);
                this.backgroundDrawable = null;
                this.imageReceiver.setGradientBitmap((Bitmap) null);
            }
        }
        MotionBackgroundDrawable motionBackgroundDrawable3 = this.backgroundDrawable;
        if (motionBackgroundDrawable3 != null) {
            motionBackgroundDrawable3.setBounds(i, i, getMeasuredWidth(), getMeasuredHeight());
            this.backgroundDrawable.draw(canvas2);
        } else {
            this.backgroundPaint.setShader(this.gradientShader);
            if (this.gradientShader == null) {
                this.backgroundPaint.setColor(backgroundColor);
            }
            this.rect.set(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight());
            canvas2.drawRoundRect(this.rect, (float) AndroidUtilities.dp(6.0f), (float) AndroidUtilities.dp(6.0f), this.backgroundPaint);
        }
        super.onDraw(canvas);
        if (this.radialProgress.getIcon() != 4) {
            int checkColor3 = checkColor;
            this.radialProgress.setColors(checkColor3, checkColor3, -1, -1);
            this.radialProgress.draw(canvas2);
            return;
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(AndroidUtilities.dp(100.0f), AndroidUtilities.dp(100.0f));
    }

    public void onFailedDownload(String fileName, boolean canceled) {
        TLRPC.TL_wallPaper selectedPattern = this.delegate.getSelectedPattern();
        TLRPC.TL_wallPaper tL_wallPaper = this.currentPattern;
        if (!((tL_wallPaper == null && selectedPattern == null) || !(selectedPattern == null || tL_wallPaper == null || tL_wallPaper.id != selectedPattern.id))) {
            return;
        }
        if (canceled) {
            this.radialProgress.setIcon(4, false, true);
        } else {
            updateButtonState(this.currentPattern, true, canceled);
        }
    }

    public void onSuccessDownload(String fileName) {
        this.radialProgress.setProgress(1.0f, true);
        TLRPC.TL_wallPaper selectedPattern = this.delegate.getSelectedPattern();
        TLRPC.TL_wallPaper tL_wallPaper = this.currentPattern;
        if ((tL_wallPaper == null && selectedPattern == null) || !(selectedPattern == null || tL_wallPaper == null || tL_wallPaper.id != selectedPattern.id)) {
            updateButtonState(this.currentPattern, false, true);
        }
    }

    public void onProgressDownload(String fileName, long downloadedSize, long totalSize) {
        this.radialProgress.setProgress(Math.min(1.0f, ((float) downloadedSize) / ((float) totalSize)), true);
        TLRPC.TL_wallPaper selectedPattern = this.delegate.getSelectedPattern();
        TLRPC.TL_wallPaper tL_wallPaper = this.currentPattern;
        if (((tL_wallPaper == null && selectedPattern == null) || !(selectedPattern == null || tL_wallPaper == null || tL_wallPaper.id != selectedPattern.id)) && this.radialProgress.getIcon() != 10) {
            updateButtonState(this.currentPattern, false, true);
        }
    }

    public void onProgressUpload(String fileName, long uploadedSize, long totalSize, boolean isEncrypted) {
    }

    public int getObserverTag() {
        return this.TAG;
    }
}
