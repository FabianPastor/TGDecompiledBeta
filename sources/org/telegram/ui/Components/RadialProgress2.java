package org.telegram.ui.Components;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.View;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;

public class RadialProgress2 {
    private int backgroundStroke;
    private float circleCheckProgress;
    private int circleColor;
    private String circleColorKey;
    private String circleCrossfadeColorKey;
    private float circleCrossfadeColorProgress;
    private Paint circleMiniPaint;
    private Paint circlePaint;
    private int circlePressedColor;
    private String circlePressedColorKey;
    private int circleRadius;
    private boolean drawBackground;
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
    private float miniIconScale;
    private MediaActionDrawable miniMediaActionDrawable;
    private Paint miniProgressBackgroundPaint;
    private ImageReceiver overlayImageView;
    private Paint overlayPaint;
    private float overrideAlpha;
    private View parent;
    private boolean previousCheckDrawable;
    private int progressColor;
    private RectF progressRect;
    private final Theme.ResourcesProvider resourcesProvider;

    public RadialProgress2(View parentView) {
        this(parentView, (Theme.ResourcesProvider) null);
    }

    public RadialProgress2(View parentView, Theme.ResourcesProvider resourcesProvider2) {
        this.progressRect = new RectF();
        this.progressColor = -1;
        this.overlayPaint = new Paint(1);
        this.circlePaint = new Paint(1);
        this.circleMiniPaint = new Paint(1);
        this.miniIconScale = 1.0f;
        this.circleCheckProgress = 1.0f;
        this.drawBackground = true;
        this.overrideAlpha = 1.0f;
        this.resourcesProvider = resourcesProvider2;
        this.miniProgressBackgroundPaint = new Paint(1);
        this.parent = parentView;
        ImageReceiver imageReceiver = new ImageReceiver(parentView);
        this.overlayImageView = imageReceiver;
        imageReceiver.setInvalidateAll(true);
        MediaActionDrawable mediaActionDrawable2 = new MediaActionDrawable();
        this.mediaActionDrawable = mediaActionDrawable2;
        parentView.getClass();
        mediaActionDrawable2.setDelegate(new RadialProgress2$$ExternalSyntheticLambda0(parentView));
        MediaActionDrawable mediaActionDrawable3 = new MediaActionDrawable();
        this.miniMediaActionDrawable = mediaActionDrawable3;
        parentView.getClass();
        mediaActionDrawable3.setDelegate(new RadialProgress2$$ExternalSyntheticLambda0(parentView));
        this.miniMediaActionDrawable.setMini(true);
        this.miniMediaActionDrawable.setIcon(4, false);
        int dp = AndroidUtilities.dp(22.0f);
        this.circleRadius = dp;
        this.overlayImageView.setRoundRadius(dp);
        this.overlayPaint.setColor(NUM);
    }

    public void setAsMini() {
        this.mediaActionDrawable.setMini(true);
    }

    public void setCircleRadius(int value) {
        this.circleRadius = value;
        this.overlayImageView.setRoundRadius(value);
    }

    public void setBackgroundStroke(int value) {
        this.backgroundStroke = value;
        this.circlePaint.setStrokeWidth((float) value);
        this.circlePaint.setStyle(Paint.Style.STROKE);
        invalidateParent();
    }

    public void setBackgroundDrawable(Theme.MessageDrawable drawable) {
        this.mediaActionDrawable.setBackgroundDrawable(drawable);
        this.miniMediaActionDrawable.setBackgroundDrawable(drawable);
    }

    public void setBackgroundGradientDrawable(LinearGradient drawable) {
        this.mediaActionDrawable.setBackgroundGradientDrawable(drawable);
        this.miniMediaActionDrawable.setBackgroundGradientDrawable(drawable);
    }

    public void setImageOverlay(TLRPC.PhotoSize image, TLRPC.Document document, Object parentObject) {
        this.overlayImageView.setImage(ImageLocation.getForDocument(image, document), String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf(this.circleRadius * 2), Integer.valueOf(this.circleRadius * 2)}), (Drawable) null, (String) null, parentObject, 1);
    }

    public void setImageOverlay(String url) {
        String str;
        ImageReceiver imageReceiver = this.overlayImageView;
        if (url != null) {
            str = String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf(this.circleRadius * 2), Integer.valueOf(this.circleRadius * 2)});
        } else {
            str = null;
        }
        imageReceiver.setImage(url, str, (Drawable) null, (String) null, -1);
    }

    public void onAttachedToWindow() {
        this.overlayImageView.onAttachedToWindow();
    }

    public void onDetachedFromWindow() {
        this.overlayImageView.onDetachedFromWindow();
    }

    public void setColors(int circle, int circlePressed, int icon, int iconPressed) {
        this.circleColor = circle;
        this.circlePressedColor = circlePressed;
        this.iconColor = icon;
        this.iconPressedColor = iconPressed;
        this.circleColorKey = null;
        this.circlePressedColorKey = null;
        this.iconColorKey = null;
        this.iconPressedColorKey = null;
    }

    public void setColors(String circle, String circlePressed, String icon, String iconPressed) {
        this.circleColorKey = circle;
        this.circlePressedColorKey = circlePressed;
        this.iconColorKey = icon;
        this.iconPressedColorKey = iconPressed;
    }

    public void setCircleCrossfadeColor(String color, float progress, float checkProgress) {
        this.circleCrossfadeColorKey = color;
        this.circleCrossfadeColorProgress = progress;
        this.circleCheckProgress = checkProgress;
        this.miniIconScale = 1.0f;
        if (color != null) {
            initMiniIcons();
        }
    }

    public void setDrawBackground(boolean value) {
        this.drawBackground = value;
    }

    public void setProgressRect(int left, int top, int right, int bottom) {
        this.progressRect.set((float) left, (float) top, (float) right, (float) bottom);
    }

    public RectF getProgressRect() {
        return this.progressRect;
    }

    public void setProgressColor(int color) {
        this.progressColor = color;
    }

    public void setMiniProgressBackgroundColor(int color) {
        this.miniProgressBackgroundPaint.setColor(color);
    }

    public void setProgress(float value, boolean animated) {
        if (this.drawMiniIcon) {
            this.miniMediaActionDrawable.setProgress(value, animated);
        } else {
            this.mediaActionDrawable.setProgress(value, animated);
        }
    }

    public float getProgress() {
        return (this.drawMiniIcon ? this.miniMediaActionDrawable : this.mediaActionDrawable).getProgress();
    }

    private void invalidateParent() {
        int offset = AndroidUtilities.dp(2.0f);
        this.parent.invalidate(((int) this.progressRect.left) - offset, ((int) this.progressRect.top) - offset, ((int) this.progressRect.right) + (offset * 2), ((int) this.progressRect.bottom) + (offset * 2));
    }

    public int getIcon() {
        return this.mediaActionDrawable.getCurrentIcon();
    }

    public int getMiniIcon() {
        return this.miniMediaActionDrawable.getCurrentIcon();
    }

    public void setIcon(int icon, boolean ifSame, boolean animated) {
        if (!ifSame || icon != this.mediaActionDrawable.getCurrentIcon()) {
            this.mediaActionDrawable.setIcon(icon, animated);
            if (!animated) {
                this.parent.invalidate();
            } else {
                invalidateParent();
            }
        }
    }

    public void setMiniIconScale(float scale) {
        this.miniIconScale = scale;
    }

    public void setMiniIcon(int icon, boolean ifSame, boolean animated) {
        if (icon != 2 && icon != 3 && icon != 4) {
            return;
        }
        if (!ifSame || icon != this.miniMediaActionDrawable.getCurrentIcon()) {
            this.miniMediaActionDrawable.setIcon(icon, animated);
            boolean z = icon != 4 || this.miniMediaActionDrawable.getTransitionProgress() < 1.0f;
            this.drawMiniIcon = z;
            if (z) {
                initMiniIcons();
            }
            if (!animated) {
                this.parent.invalidate();
            } else {
                invalidateParent();
            }
        }
    }

    public void initMiniIcons() {
        if (this.miniDrawBitmap == null) {
            try {
                this.miniDrawBitmap = Bitmap.createBitmap(AndroidUtilities.dp(48.0f), AndroidUtilities.dp(48.0f), Bitmap.Config.ARGB_8888);
                this.miniDrawCanvas = new Canvas(this.miniDrawBitmap);
            } catch (Throwable th) {
            }
        }
    }

    public boolean swapIcon(int icon) {
        if (this.mediaActionDrawable.setIcon(icon, false)) {
            return true;
        }
        return false;
    }

    public void setPressed(boolean value, boolean mini) {
        if (mini) {
            this.isPressedMini = value;
        } else {
            this.isPressed = value;
        }
        invalidateParent();
    }

    public void setOverrideAlpha(float alpha) {
        this.overrideAlpha = alpha;
    }

    public float getOverrideAlpha() {
        return this.overrideAlpha;
    }

    public void draw(Canvas canvas) {
        float wholeAlpha;
        int color;
        int centerY;
        int centerX;
        float cy;
        float cx;
        int size;
        int offset;
        float alpha;
        int restore;
        Canvas canvas2;
        Canvas canvas3;
        Canvas canvas4;
        int r;
        int color2;
        Canvas canvas5 = canvas;
        if ((this.mediaActionDrawable.getCurrentIcon() != 4 || this.mediaActionDrawable.getTransitionProgress() < 1.0f) && !this.progressRect.isEmpty()) {
            int currentIcon = this.mediaActionDrawable.getCurrentIcon();
            int prevIcon = this.mediaActionDrawable.getPreviousIcon();
            if (this.backgroundStroke != 0) {
                wholeAlpha = currentIcon == 3 ? 1.0f - this.mediaActionDrawable.getTransitionProgress() : prevIcon == 3 ? this.mediaActionDrawable.getTransitionProgress() : 1.0f;
            } else if ((currentIcon == 3 || currentIcon == 6 || currentIcon == 10 || currentIcon == 8 || currentIcon == 0) && prevIcon == 4) {
                wholeAlpha = this.mediaActionDrawable.getTransitionProgress();
            } else {
                wholeAlpha = currentIcon != 4 ? 1.0f : 1.0f - this.mediaActionDrawable.getTransitionProgress();
            }
            if (!this.isPressedMini || this.circleCrossfadeColorKey != null) {
                String str = this.iconColorKey;
                if (str != null) {
                    this.miniMediaActionDrawable.setColor(getThemedColor(str));
                } else {
                    this.miniMediaActionDrawable.setColor(this.iconColor);
                }
                String str2 = this.circleColorKey;
                if (str2 == null) {
                    this.circleMiniPaint.setColor(this.circleColor);
                } else if (this.circleCrossfadeColorKey != null) {
                    this.circleMiniPaint.setColor(AndroidUtilities.getOffsetColor(getThemedColor(str2), getThemedColor(this.circleCrossfadeColorKey), this.circleCrossfadeColorProgress, this.circleCheckProgress));
                } else {
                    this.circleMiniPaint.setColor(getThemedColor(str2));
                }
            } else {
                String str3 = this.iconPressedColorKey;
                if (str3 != null) {
                    this.miniMediaActionDrawable.setColor(getThemedColor(str3));
                } else {
                    this.miniMediaActionDrawable.setColor(this.iconPressedColor);
                }
                String str4 = this.circlePressedColorKey;
                if (str4 != null) {
                    this.circleMiniPaint.setColor(getThemedColor(str4));
                } else {
                    this.circleMiniPaint.setColor(this.circlePressedColor);
                }
            }
            if (this.isPressed) {
                String str5 = this.iconPressedColorKey;
                if (str5 != null) {
                    MediaActionDrawable mediaActionDrawable2 = this.mediaActionDrawable;
                    int themedColor = getThemedColor(str5);
                    color = themedColor;
                    mediaActionDrawable2.setColor(themedColor);
                    this.mediaActionDrawable.setBackColor(getThemedColor(this.circlePressedColorKey));
                } else {
                    MediaActionDrawable mediaActionDrawable3 = this.mediaActionDrawable;
                    int i = this.iconPressedColor;
                    color = i;
                    mediaActionDrawable3.setColor(i);
                    this.mediaActionDrawable.setBackColor(this.circlePressedColor);
                }
                String str6 = this.circlePressedColorKey;
                if (str6 != null) {
                    this.circlePaint.setColor(getThemedColor(str6));
                } else {
                    this.circlePaint.setColor(this.circlePressedColor);
                }
            } else {
                String str7 = this.iconColorKey;
                if (str7 != null) {
                    MediaActionDrawable mediaActionDrawable4 = this.mediaActionDrawable;
                    int themedColor2 = getThemedColor(str7);
                    color2 = themedColor2;
                    mediaActionDrawable4.setColor(themedColor2);
                    this.mediaActionDrawable.setBackColor(getThemedColor(this.circleColorKey));
                } else {
                    MediaActionDrawable mediaActionDrawable5 = this.mediaActionDrawable;
                    int i2 = this.iconColor;
                    color2 = i2;
                    mediaActionDrawable5.setColor(i2);
                    this.mediaActionDrawable.setBackColor(this.circleColor);
                }
                String str8 = this.circleColorKey;
                if (str8 != null) {
                    this.circlePaint.setColor(getThemedColor(str8));
                } else {
                    this.circlePaint.setColor(this.circleColor);
                }
            }
            if ((this.drawMiniIcon || this.circleCrossfadeColorKey != null) && this.miniDrawCanvas != null) {
                this.miniDrawBitmap.eraseColor(0);
            }
            this.circlePaint.setAlpha((int) (((float) this.circlePaint.getAlpha()) * wholeAlpha * this.overrideAlpha));
            int originalAlpha = this.circleMiniPaint.getAlpha();
            this.circleMiniPaint.setAlpha((int) (((float) originalAlpha) * wholeAlpha * this.overrideAlpha));
            boolean drawCircle = true;
            if ((this.drawMiniIcon || this.circleCrossfadeColorKey != null) && this.miniDrawCanvas != null) {
                centerX = (int) Math.ceil((double) (this.progressRect.width() / 2.0f));
                centerY = (int) Math.ceil((double) (this.progressRect.height() / 2.0f));
            } else {
                centerX = (int) this.progressRect.centerX();
                centerY = (int) this.progressRect.centerY();
            }
            if (this.overlayImageView.hasBitmapImage()) {
                float alpha2 = this.overlayImageView.getCurrentAlpha();
                this.overlayPaint.setAlpha((int) (this.overrideAlpha * 100.0f * alpha2 * wholeAlpha));
                if (alpha2 >= 1.0f) {
                    drawCircle = false;
                    r = -1;
                    int i3 = prevIcon;
                    int i4 = originalAlpha;
                    int i5 = color;
                } else {
                    int r2 = Color.red(color);
                    int g = Color.green(color);
                    int b = Color.blue(color);
                    int a = Color.alpha(color);
                    int i6 = prevIcon;
                    int rD = (int) (((float) (255 - r2)) * alpha2);
                    int i7 = originalAlpha;
                    int gD = (int) (((float) (255 - g)) * alpha2);
                    int i8 = color;
                    int i9 = a;
                    int i10 = rD;
                    int i11 = gD;
                    r = Color.argb(a + ((int) (((float) (255 - a)) * alpha2)), r2 + rD, g + gD, b + ((int) (((float) (255 - b)) * alpha2)));
                    drawCircle = true;
                }
                this.mediaActionDrawable.setColor(r);
                ImageReceiver imageReceiver = this.overlayImageView;
                int i12 = this.circleRadius;
                imageReceiver.setImageCoords((float) (centerX - i12), (float) (centerY - i12), (float) (i12 * 2), (float) (i12 * 2));
            } else {
                int i13 = originalAlpha;
                int i14 = color;
            }
            int restore2 = Integer.MIN_VALUE;
            Canvas canvas6 = this.miniDrawCanvas;
            if (!(canvas6 == null || this.circleCrossfadeColorKey == null || this.circleCheckProgress == 1.0f)) {
                restore2 = canvas6.save();
                float scaleMini = 1.0f - ((1.0f - this.circleCheckProgress) * 0.1f);
                this.miniDrawCanvas.scale(scaleMini, scaleMini, (float) centerX, (float) centerY);
            }
            if (drawCircle && this.drawBackground) {
                if ((this.drawMiniIcon || this.circleCrossfadeColorKey != null) && (canvas4 = this.miniDrawCanvas) != null) {
                    canvas4.drawCircle((float) centerX, (float) centerY, (float) this.circleRadius, this.circlePaint);
                } else if (!(currentIcon == 4 && wholeAlpha == 0.0f)) {
                    if (this.backgroundStroke != 0) {
                        canvas5.drawCircle((float) centerX, (float) centerY, (float) (this.circleRadius - AndroidUtilities.dp(3.5f)), this.circlePaint);
                    } else {
                        canvas5.drawCircle((float) centerX, (float) centerY, (float) this.circleRadius, this.circlePaint);
                    }
                }
            }
            if (this.overlayImageView.hasBitmapImage()) {
                this.overlayImageView.setAlpha(this.overrideAlpha * wholeAlpha);
                if ((this.drawMiniIcon || this.circleCrossfadeColorKey != null) && (canvas3 = this.miniDrawCanvas) != null) {
                    this.overlayImageView.draw(canvas3);
                    this.miniDrawCanvas.drawCircle((float) centerX, (float) centerY, (float) this.circleRadius, this.overlayPaint);
                } else {
                    this.overlayImageView.draw(canvas5);
                    canvas5.drawCircle((float) centerX, (float) centerY, (float) this.circleRadius, this.overlayPaint);
                }
            }
            MediaActionDrawable mediaActionDrawable6 = this.mediaActionDrawable;
            int i15 = this.circleRadius;
            mediaActionDrawable6.setBounds(centerX - i15, centerY - i15, centerX + i15, i15 + centerY);
            this.mediaActionDrawable.setHasOverlayImage(this.overlayImageView.hasBitmapImage());
            if (this.drawMiniIcon || this.circleCrossfadeColorKey != null) {
                Canvas canvas7 = this.miniDrawCanvas;
                if (canvas7 != null) {
                    this.mediaActionDrawable.draw(canvas7);
                } else {
                    this.mediaActionDrawable.draw(canvas5);
                }
            } else {
                this.mediaActionDrawable.setOverrideAlpha(this.overrideAlpha);
                this.mediaActionDrawable.draw(canvas5);
            }
            if (!(restore2 == Integer.MIN_VALUE || (canvas2 = this.miniDrawCanvas) == null)) {
                canvas2.restoreToCount(restore2);
            }
            if (this.drawMiniIcon || this.circleCrossfadeColorKey != null) {
                if (Math.abs(this.progressRect.width() - ((float) AndroidUtilities.dp(44.0f))) < AndroidUtilities.density) {
                    offset = 0;
                    size = 20;
                    cx = this.progressRect.centerX() + ((float) AndroidUtilities.dp((float) (0 + 16)));
                    cy = this.progressRect.centerY() + ((float) AndroidUtilities.dp((float) (0 + 16)));
                } else {
                    offset = 2;
                    size = 22;
                    cx = this.progressRect.centerX() + ((float) AndroidUtilities.dp(18.0f));
                    cy = ((float) AndroidUtilities.dp(18.0f)) + this.progressRect.centerY();
                }
                int halfSize = size / 2;
                if (this.drawMiniIcon) {
                    alpha = this.miniMediaActionDrawable.getCurrentIcon() != 4 ? 1.0f : 1.0f - this.miniMediaActionDrawable.getTransitionProgress();
                    if (alpha == 0.0f) {
                        this.drawMiniIcon = false;
                    }
                } else {
                    alpha = 1.0f;
                }
                Canvas canvas8 = this.miniDrawCanvas;
                if (canvas8 != null) {
                    int i16 = currentIcon;
                    int i17 = restore2;
                    float f = wholeAlpha;
                    canvas8.drawCircle((float) AndroidUtilities.dp((float) (size + 18 + offset)), (float) AndroidUtilities.dp((float) (size + 18 + offset)), ((float) AndroidUtilities.dp((float) (halfSize + 1))) * alpha * this.miniIconScale, Theme.checkboxSquare_eraserPaint);
                } else {
                    int i18 = restore2;
                    float f2 = wholeAlpha;
                    this.miniProgressBackgroundPaint.setColor(this.progressColor);
                    canvas5.drawCircle(cx, cy, (float) AndroidUtilities.dp(12.0f), this.miniProgressBackgroundPaint);
                }
                if (this.miniDrawCanvas != null) {
                    canvas5.drawBitmap(this.miniDrawBitmap, (float) ((int) this.progressRect.left), (float) ((int) this.progressRect.top), (Paint) null);
                }
                if (this.miniIconScale < 1.0f) {
                    int restore3 = canvas.save();
                    float f3 = this.miniIconScale;
                    canvas5.scale(f3, f3, cx, cy);
                    restore = restore3;
                } else {
                    restore = Integer.MIN_VALUE;
                }
                canvas5.drawCircle(cx, cy, (((float) AndroidUtilities.dp((float) halfSize)) * alpha) + (((float) AndroidUtilities.dp(1.0f)) * (1.0f - this.circleCheckProgress)), this.circleMiniPaint);
                if (this.drawMiniIcon) {
                    int i19 = offset;
                    this.miniMediaActionDrawable.setBounds((int) (cx - (((float) AndroidUtilities.dp((float) halfSize)) * alpha)), (int) (cy - (((float) AndroidUtilities.dp((float) halfSize)) * alpha)), (int) ((((float) AndroidUtilities.dp((float) halfSize)) * alpha) + cx), (int) ((((float) AndroidUtilities.dp((float) halfSize)) * alpha) + cy));
                    this.miniMediaActionDrawable.draw(canvas5);
                }
                if (restore != Integer.MIN_VALUE) {
                    canvas5.restoreToCount(restore);
                    return;
                }
                return;
            }
            int i20 = currentIcon;
            float f4 = wholeAlpha;
        }
    }

    public String getCircleColorKey() {
        return this.circleColorKey;
    }

    private int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }
}
