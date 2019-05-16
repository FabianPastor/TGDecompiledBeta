package org.telegram.ui.Components;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.ui.ActionBar.Theme;

public class RadialProgress2 {
    private int circleColor;
    private String circleColorKey;
    private Paint circleMiniPaint = new Paint(1);
    private Paint circlePaint = new Paint(1);
    private int circlePressedColor;
    private String circlePressedColorKey;
    private int circleRadius;
    private boolean drawBackground = true;
    private boolean drawMiniIcon;
    private int iconColor;
    private String iconColorKey;
    private int iconPressedColor;
    private String iconPressedColorKey;
    private boolean isPressed;
    private boolean isPressedMini;
    private MediaActionDrawable mediaActionDrawable;
    private Bitmap miniDrawBitmap;
    private Canvas miniDrawCanvas;
    private MediaActionDrawable miniMediaActionDrawable;
    private Paint miniProgressBackgroundPaint = new Paint(1);
    private ImageReceiver overlayImageView;
    private Paint overlayPaint = new Paint(1);
    private float overrideAlpha = 1.0f;
    private View parent;
    private boolean previousCheckDrawable;
    private int progressColor = -1;
    private RectF progressRect = new RectF();

    public RadialProgress2(View view) {
        this.parent = view;
        this.overlayImageView = new ImageReceiver(view);
        this.overlayImageView.setInvalidateAll(true);
        this.mediaActionDrawable = new MediaActionDrawable();
        MediaActionDrawable mediaActionDrawable = this.mediaActionDrawable;
        view.getClass();
        mediaActionDrawable.setDelegate(new -$$Lambda$F8rg4UBMmP_S27QL-K3VXBnPS_E(view));
        this.miniMediaActionDrawable = new MediaActionDrawable();
        mediaActionDrawable = this.miniMediaActionDrawable;
        view.getClass();
        mediaActionDrawable.setDelegate(new -$$Lambda$F8rg4UBMmP_S27QL-K3VXBnPS_E(view));
        this.miniMediaActionDrawable.setMini(true);
        this.miniMediaActionDrawable.setIcon(4, false);
        this.circleRadius = AndroidUtilities.dp(22.0f);
        this.overlayImageView.setRoundRadius(this.circleRadius);
        this.overlayPaint.setColor(NUM);
    }

    public void setCircleRadius(int i) {
        this.circleRadius = i;
        this.overlayImageView.setRoundRadius(this.circleRadius);
    }

    public void setImageOverlay(PhotoSize photoSize, Document document, Object obj) {
        this.overlayImageView.setImage(ImageLocation.getForDocument(photoSize, document), String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf(this.circleRadius * 2), Integer.valueOf(this.circleRadius * 2)}), null, null, obj, 1);
    }

    public void setImageOverlay(String str) {
        String format;
        ImageReceiver imageReceiver = this.overlayImageView;
        if (str != null) {
            format = String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf(this.circleRadius * 2), Integer.valueOf(this.circleRadius * 2)});
        } else {
            format = null;
        }
        imageReceiver.setImage(str, format, null, null, -1);
    }

    public void onAttachedToWindow() {
        this.overlayImageView.onAttachedToWindow();
    }

    public void onDetachedFromWindow() {
        this.overlayImageView.onDetachedFromWindow();
    }

    public void setColors(int i, int i2, int i3, int i4) {
        this.circleColor = i;
        this.circlePressedColor = i2;
        this.iconColor = i3;
        this.iconPressedColor = i4;
        this.circleColorKey = null;
        this.circlePressedColorKey = null;
        this.iconColorKey = null;
        this.iconPressedColorKey = null;
    }

    public void setColors(String str, String str2, String str3, String str4) {
        this.circleColorKey = str;
        this.circlePressedColorKey = str2;
        this.iconColorKey = str3;
        this.iconPressedColorKey = str4;
    }

    public void setDrawBackground(boolean z) {
        this.drawBackground = z;
    }

    public void setProgressRect(int i, int i2, int i3, int i4) {
        this.progressRect.set((float) i, (float) i2, (float) i3, (float) i4);
    }

    public RectF getProgressRect() {
        return this.progressRect;
    }

    public void setProgressColor(int i) {
        this.progressColor = i;
    }

    public void setMiniProgressBackgroundColor(int i) {
        this.miniProgressBackgroundPaint.setColor(i);
    }

    public void setProgress(float f, boolean z) {
        if (this.drawMiniIcon) {
            this.miniMediaActionDrawable.setProgress(f, z);
        } else {
            this.mediaActionDrawable.setProgress(f, z);
        }
    }

    private void invalidateParent() {
        int dp = AndroidUtilities.dp(2.0f);
        View view = this.parent;
        RectF rectF = this.progressRect;
        int i = ((int) rectF.left) - dp;
        int i2 = ((int) rectF.top) - dp;
        dp *= 2;
        view.invalidate(i, i2, ((int) rectF.right) + dp, ((int) rectF.bottom) + dp);
    }

    public int getIcon() {
        return this.mediaActionDrawable.getCurrentIcon();
    }

    public int getMiniIcon() {
        return this.miniMediaActionDrawable.getCurrentIcon();
    }

    public void setIcon(int i, boolean z, boolean z2) {
        if (!z || i != this.mediaActionDrawable.getCurrentIcon()) {
            this.mediaActionDrawable.setIcon(i, z2);
            if (z2) {
                invalidateParent();
            } else {
                this.parent.invalidate();
            }
        }
    }

    public void setMiniIcon(int i, boolean z, boolean z2) {
        if (i != 2 && i != 3 && i != 4) {
            return;
        }
        if (!z || i != this.miniMediaActionDrawable.getCurrentIcon()) {
            this.miniMediaActionDrawable.setIcon(i, z2);
            boolean z3 = i != 4 || this.miniMediaActionDrawable.getTransitionProgress() < 1.0f;
            this.drawMiniIcon = z3;
            if (this.drawMiniIcon) {
                initMiniIcons();
            }
            if (z2) {
                invalidateParent();
            } else {
                this.parent.invalidate();
            }
        }
    }

    public void initMiniIcons() {
        if (this.miniDrawBitmap == null) {
            try {
                this.miniDrawBitmap = Bitmap.createBitmap(AndroidUtilities.dp(48.0f), AndroidUtilities.dp(48.0f), Config.ARGB_8888);
                this.miniDrawCanvas = new Canvas(this.miniDrawBitmap);
            } catch (Throwable unused) {
            }
        }
    }

    public boolean swapIcon(int i) {
        return this.mediaActionDrawable.setIcon(i, false);
    }

    public void setPressed(boolean z, boolean z2) {
        if (z2) {
            this.isPressedMini = z;
        } else {
            this.isPressed = z;
        }
        invalidateParent();
    }

    public void setOverrideAlpha(float f) {
        this.overrideAlpha = f;
    }

    public void draw(Canvas canvas) {
        Canvas canvas2 = canvas;
        if (this.mediaActionDrawable.getCurrentIcon() != 4 || this.mediaActionDrawable.getTransitionProgress() < 1.0f) {
            String str;
            int color;
            int i;
            int centerY;
            int i2;
            Object obj;
            Canvas canvas3;
            int currentIcon = this.mediaActionDrawable.getCurrentIcon();
            float transitionProgress = ((currentIcon == 3 || currentIcon == 6 || currentIcon == 10 || currentIcon == 8 || currentIcon == 0) && this.mediaActionDrawable.getPreviousIcon() == 4) ? this.mediaActionDrawable.getTransitionProgress() : currentIcon != 4 ? 1.0f : 1.0f - this.mediaActionDrawable.getTransitionProgress();
            if (this.isPressedMini) {
                str = this.iconPressedColorKey;
                if (str != null) {
                    this.miniMediaActionDrawable.setColor(Theme.getColor(str));
                } else {
                    this.miniMediaActionDrawable.setColor(this.iconPressedColor);
                }
                str = this.circlePressedColorKey;
                if (str != null) {
                    this.circleMiniPaint.setColor(Theme.getColor(str));
                } else {
                    this.circleMiniPaint.setColor(this.circlePressedColor);
                }
            } else {
                str = this.iconColorKey;
                if (str != null) {
                    this.miniMediaActionDrawable.setColor(Theme.getColor(str));
                } else {
                    this.miniMediaActionDrawable.setColor(this.iconColor);
                }
                str = this.circleColorKey;
                if (str != null) {
                    this.circleMiniPaint.setColor(Theme.getColor(str));
                } else {
                    this.circleMiniPaint.setColor(this.circleColor);
                }
            }
            MediaActionDrawable mediaActionDrawable;
            MediaActionDrawable mediaActionDrawable2;
            String str2;
            if (this.isPressed) {
                str = this.iconPressedColorKey;
                if (str != null) {
                    mediaActionDrawable = this.mediaActionDrawable;
                    color = Theme.getColor(str);
                    mediaActionDrawable.setColor(color);
                } else {
                    mediaActionDrawable2 = this.mediaActionDrawable;
                    i = this.iconPressedColor;
                    mediaActionDrawable2.setColor(i);
                    color = i;
                }
                str2 = this.circlePressedColorKey;
                if (str2 != null) {
                    this.circlePaint.setColor(Theme.getColor(str2));
                } else {
                    this.circlePaint.setColor(this.circlePressedColor);
                }
            } else {
                str = this.iconColorKey;
                if (str != null) {
                    mediaActionDrawable = this.mediaActionDrawable;
                    color = Theme.getColor(str);
                    mediaActionDrawable.setColor(color);
                } else {
                    mediaActionDrawable2 = this.mediaActionDrawable;
                    i = this.iconColor;
                    mediaActionDrawable2.setColor(i);
                    color = i;
                }
                str2 = this.circleColorKey;
                if (str2 != null) {
                    this.circlePaint.setColor(Theme.getColor(str2));
                } else {
                    this.circlePaint.setColor(this.circleColor);
                }
            }
            if (this.drawMiniIcon && this.miniDrawCanvas != null) {
                this.miniDrawBitmap.eraseColor(0);
            }
            this.circlePaint.setAlpha((int) ((((float) this.circlePaint.getAlpha()) * transitionProgress) * this.overrideAlpha));
            this.circleMiniPaint.setAlpha((int) ((((float) this.circleMiniPaint.getAlpha()) * transitionProgress) * this.overrideAlpha));
            if (!this.drawMiniIcon || this.miniDrawCanvas == null) {
                i = (int) this.progressRect.centerX();
                centerY = (int) this.progressRect.centerY();
            } else {
                i = (int) (this.progressRect.width() / 2.0f);
                centerY = (int) (this.progressRect.height() / 2.0f);
            }
            int i3 = 2;
            if (this.overlayImageView.hasBitmapImage()) {
                Object obj2;
                float currentAlpha = this.overlayImageView.getCurrentAlpha();
                this.overlayPaint.setAlpha((int) (((100.0f * currentAlpha) * transitionProgress) * this.overrideAlpha));
                if (currentAlpha >= 1.0f) {
                    color = -1;
                    obj2 = null;
                } else {
                    int red = Color.red(color);
                    int green = Color.green(color);
                    int blue = Color.blue(color);
                    color = Color.alpha(color);
                    color = Color.argb(color + ((int) (((float) (255 - color)) * currentAlpha)), red + ((int) (((float) (255 - red)) * currentAlpha)), green + ((int) (((float) (255 - green)) * currentAlpha)), blue + ((int) (((float) (255 - blue)) * currentAlpha)));
                    obj2 = 1;
                }
                this.mediaActionDrawable.setColor(color);
                ImageReceiver imageReceiver = this.overlayImageView;
                i2 = this.circleRadius;
                imageReceiver.setImageCoords(i - i2, centerY - i2, i2 * 2, i2 * 2);
                obj = obj2;
            } else {
                obj = 1;
            }
            if (obj != null && this.drawBackground) {
                if (this.drawMiniIcon) {
                    Canvas canvas4 = this.miniDrawCanvas;
                    if (canvas4 != null) {
                        canvas4.drawCircle((float) i, (float) centerY, (float) this.circleRadius, this.circlePaint);
                    }
                }
                if (!(currentIcon == 4 && transitionProgress == 0.0f)) {
                    canvas2.drawCircle((float) i, (float) centerY, ((float) this.circleRadius) * transitionProgress, this.circlePaint);
                }
            }
            if (this.overlayImageView.hasBitmapImage()) {
                this.overlayImageView.setAlpha(transitionProgress * this.overrideAlpha);
                if (this.drawMiniIcon) {
                    canvas3 = this.miniDrawCanvas;
                    if (canvas3 != null) {
                        this.overlayImageView.draw(canvas3);
                        this.miniDrawCanvas.drawCircle((float) i, (float) centerY, (float) this.circleRadius, this.overlayPaint);
                    }
                }
                this.overlayImageView.draw(canvas2);
                canvas2.drawCircle((float) i, (float) centerY, (float) this.circleRadius, this.overlayPaint);
            }
            MediaActionDrawable mediaActionDrawable3 = this.mediaActionDrawable;
            i2 = this.circleRadius;
            mediaActionDrawable3.setBounds(i - i2, centerY - i2, i + i2, centerY + i2);
            if (this.drawMiniIcon) {
                canvas3 = this.miniDrawCanvas;
                if (canvas3 != null) {
                    this.mediaActionDrawable.draw(canvas3);
                } else {
                    this.mediaActionDrawable.draw(canvas2);
                }
            } else {
                this.mediaActionDrawable.setOverrideAlpha(this.overrideAlpha);
                this.mediaActionDrawable.draw(canvas2);
            }
            if (this.drawMiniIcon) {
                float centerX;
                float centerY2;
                float f;
                if (Math.abs(this.progressRect.width() - ((float) AndroidUtilities.dp(44.0f))) < AndroidUtilities.density) {
                    currentIcon = 20;
                    transitionProgress = (float) 16;
                    centerX = this.progressRect.centerX() + ((float) AndroidUtilities.dp(transitionProgress));
                    centerY2 = this.progressRect.centerY() + ((float) AndroidUtilities.dp(transitionProgress));
                    i3 = 0;
                } else {
                    currentIcon = 22;
                    centerX = this.progressRect.centerX() + ((float) AndroidUtilities.dp(18.0f));
                    centerY2 = this.progressRect.centerY() + ((float) AndroidUtilities.dp(18.0f));
                }
                int i4 = currentIcon / 2;
                float transitionProgress2 = this.miniMediaActionDrawable.getCurrentIcon() != 4 ? 1.0f : 1.0f - this.miniMediaActionDrawable.getTransitionProgress();
                if (transitionProgress2 == 0.0f) {
                    this.drawMiniIcon = false;
                }
                Canvas canvas5 = this.miniDrawCanvas;
                if (canvas5 != null) {
                    f = (float) ((currentIcon + 18) + i3);
                    canvas5.drawCircle((float) AndroidUtilities.dp(f), (float) AndroidUtilities.dp(f), ((float) AndroidUtilities.dp((float) (i4 + 1))) * transitionProgress2, Theme.checkboxSquare_eraserPaint);
                } else {
                    this.miniProgressBackgroundPaint.setColor(this.progressColor);
                    canvas2.drawCircle(centerX, centerY2, (float) AndroidUtilities.dp(12.0f), this.miniProgressBackgroundPaint);
                }
                if (this.miniDrawCanvas != null) {
                    Bitmap bitmap = this.miniDrawBitmap;
                    RectF rectF = this.progressRect;
                    canvas2.drawBitmap(bitmap, (float) ((int) rectF.left), (float) ((int) rectF.top), null);
                }
                f = (float) i4;
                canvas2.drawCircle(centerX, centerY2, ((float) AndroidUtilities.dp(f)) * transitionProgress2, this.circleMiniPaint);
                this.miniMediaActionDrawable.setBounds((int) (centerX - (((float) AndroidUtilities.dp(f)) * transitionProgress2)), (int) (centerY2 - (((float) AndroidUtilities.dp(f)) * transitionProgress2)), (int) (centerX + (((float) AndroidUtilities.dp(f)) * transitionProgress2)), (int) (centerY2 + (((float) AndroidUtilities.dp(f)) * transitionProgress2)));
                this.miniMediaActionDrawable.draw(canvas2);
            }
        }
    }
}
