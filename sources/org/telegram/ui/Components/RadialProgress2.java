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
import org.telegram.messenger.ImageReceiver;
import org.telegram.tgnet.TLObject;
import org.telegram.ui.ActionBar.Theme;

public class RadialProgress2 {
    private int circleColor;
    private String circleColorKey;
    private Paint circleMiniPaint = new Paint(1);
    private Paint circlePaint = new Paint(1);
    private int circlePressedColor;
    private String circlePressedColorKey;
    private int circleRadius;
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

    public RadialProgress2(View parentView) {
        this.parent = parentView;
        this.overlayImageView = new ImageReceiver(parentView);
        this.overlayImageView.setInvalidateAll(true);
        this.mediaActionDrawable = new MediaActionDrawable();
        MediaActionDrawable mediaActionDrawable = this.mediaActionDrawable;
        parentView.getClass();
        mediaActionDrawable.setDelegate(RadialProgress2$$Lambda$0.get$Lambda(parentView));
        this.miniMediaActionDrawable = new MediaActionDrawable();
        mediaActionDrawable = this.miniMediaActionDrawable;
        parentView.getClass();
        mediaActionDrawable.setDelegate(RadialProgress2$$Lambda$1.get$Lambda(parentView));
        this.miniMediaActionDrawable.setMini(true);
        this.miniMediaActionDrawable.setIcon(4, false);
        this.circleRadius = AndroidUtilities.dp(22.0f);
        this.overlayImageView.setRoundRadius(this.circleRadius);
        this.overlayPaint.setColor(NUM);
    }

    public void setCircleRadius(int value) {
        this.circleRadius = value;
        this.overlayImageView.setRoundRadius(this.circleRadius);
    }

    public void setImageOverlay(TLObject image, Object parentObject) {
        String format;
        ImageReceiver imageReceiver = this.overlayImageView;
        if (image != null) {
            format = String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf(this.circleRadius * 2), Integer.valueOf(this.circleRadius * 2)});
        } else {
            format = null;
        }
        imageReceiver.setImage(image, format, null, null, parentObject, 1);
    }

    public void setImageOverlay(String url) {
        String format;
        ImageReceiver imageReceiver = this.overlayImageView;
        if (url != null) {
            format = String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf(this.circleRadius * 2), Integer.valueOf(this.circleRadius * 2)});
        } else {
            format = null;
        }
        imageReceiver.setImage(url, format, null, null, -1);
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

    private void invalidateParent() {
        int offset = AndroidUtilities.dp(2.0f);
        this.parent.invalidate(((int) this.progressRect.left) - offset, ((int) this.progressRect.top) - offset, ((int) this.progressRect.right) + (offset * 2), ((int) this.progressRect.bottom) + (offset * 2));
    }

    public int getIcon() {
        return this.mediaActionDrawable.getCurrentIcon();
    }

    public void setIcon(int icon, boolean ifSame, boolean animated) {
        if (!ifSame || icon != this.mediaActionDrawable.getCurrentIcon()) {
            this.mediaActionDrawable.setIcon(icon, animated);
            if (animated) {
                invalidateParent();
            } else {
                this.parent.invalidate();
            }
        }
    }

    public void setMiniIcon(int icon, boolean ifSame, boolean animated) {
        if (icon != 2 && icon != 3 && icon != 4) {
            return;
        }
        if (!ifSame || icon != this.miniMediaActionDrawable.getCurrentIcon()) {
            this.miniMediaActionDrawable.setIcon(icon, animated);
            boolean z = icon != 4 || this.miniMediaActionDrawable.getTransitionProgress() < 1.0f;
            this.drawMiniIcon = z;
            if (this.drawMiniIcon) {
                initMiniIcons();
            }
            if (animated) {
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

    public void draw(Canvas canvas) {
        if (this.mediaActionDrawable.getCurrentIcon() != 4 || this.mediaActionDrawable.getTransitionProgress() < 1.0f) {
            int color;
            int centerX;
            int centerY;
            float alpha;
            float wholeAlpha = this.mediaActionDrawable.getCurrentIcon() != 4 ? 1.0f : 1.0f - this.mediaActionDrawable.getTransitionProgress();
            if (this.isPressedMini) {
                if (this.iconPressedColorKey != null) {
                    this.miniMediaActionDrawable.setColor(Theme.getColor(this.iconPressedColorKey));
                } else {
                    this.miniMediaActionDrawable.setColor(this.iconPressedColor);
                }
                if (this.circlePressedColorKey != null) {
                    this.circleMiniPaint.setColor(Theme.getColor(this.circlePressedColorKey));
                } else {
                    this.circleMiniPaint.setColor(this.circlePressedColor);
                }
            } else {
                if (this.iconColorKey != null) {
                    this.miniMediaActionDrawable.setColor(Theme.getColor(this.iconColorKey));
                } else {
                    this.miniMediaActionDrawable.setColor(this.iconColor);
                }
                if (this.circleColorKey != null) {
                    this.circleMiniPaint.setColor(Theme.getColor(this.circleColorKey));
                } else {
                    this.circleMiniPaint.setColor(this.circleColor);
                }
            }
            MediaActionDrawable mediaActionDrawable;
            if (this.isPressed) {
                if (this.iconPressedColorKey != null) {
                    mediaActionDrawable = this.mediaActionDrawable;
                    color = Theme.getColor(this.iconPressedColorKey);
                    mediaActionDrawable.setColor(color);
                } else {
                    mediaActionDrawable = this.mediaActionDrawable;
                    color = this.iconPressedColor;
                    mediaActionDrawable.setColor(color);
                }
                if (this.circlePressedColorKey != null) {
                    this.circlePaint.setColor(Theme.getColor(this.circlePressedColorKey));
                } else {
                    this.circlePaint.setColor(this.circlePressedColor);
                }
            } else {
                if (this.iconColorKey != null) {
                    mediaActionDrawable = this.mediaActionDrawable;
                    color = Theme.getColor(this.iconColorKey);
                    mediaActionDrawable.setColor(color);
                } else {
                    mediaActionDrawable = this.mediaActionDrawable;
                    color = this.iconColor;
                    mediaActionDrawable.setColor(color);
                }
                if (this.circleColorKey != null) {
                    this.circlePaint.setColor(Theme.getColor(this.circleColorKey));
                } else {
                    this.circlePaint.setColor(this.circleColor);
                }
            }
            if (this.drawMiniIcon && this.miniDrawCanvas != null) {
                this.miniDrawBitmap.eraseColor(0);
            }
            this.circlePaint.setAlpha((int) ((((float) this.circlePaint.getAlpha()) * wholeAlpha) * this.overrideAlpha));
            this.circleMiniPaint.setAlpha((int) ((((float) this.circleMiniPaint.getAlpha()) * wholeAlpha) * this.overrideAlpha));
            boolean drawCircle = true;
            if (!this.drawMiniIcon || this.miniDrawCanvas == null) {
                centerX = (int) this.progressRect.centerX();
                centerY = (int) this.progressRect.centerY();
            } else {
                centerX = (int) (this.progressRect.width() / 2.0f);
                centerY = (int) (this.progressRect.height() / 2.0f);
            }
            if (this.overlayImageView.hasBitmapImage()) {
                int c;
                alpha = this.overlayImageView.getCurrentAlpha();
                this.overlayPaint.setAlpha((int) (((100.0f * alpha) * wholeAlpha) * this.overrideAlpha));
                if (alpha >= 1.0f) {
                    drawCircle = false;
                    c = -1;
                } else {
                    int r = Color.red(color);
                    int g = Color.green(color);
                    int b = Color.blue(color);
                    int a = Color.alpha(color);
                    c = Color.argb(a + ((int) (((float) (255 - a)) * alpha)), r + ((int) (((float) (255 - r)) * alpha)), g + ((int) (((float) (255 - g)) * alpha)), b + ((int) (((float) (255 - b)) * alpha)));
                }
                this.mediaActionDrawable.setColor(c);
                this.overlayImageView.setImageCoords(centerX - this.circleRadius, centerY - this.circleRadius, this.circleRadius * 2, this.circleRadius * 2);
            }
            if (drawCircle) {
                if (this.drawMiniIcon && this.miniDrawCanvas != null) {
                    this.miniDrawCanvas.drawCircle((float) centerX, (float) centerY, (float) this.circleRadius, this.circlePaint);
                } else if (!(this.mediaActionDrawable.getCurrentIcon() == 4 && wholeAlpha == 0.0f)) {
                    canvas.drawCircle((float) centerX, (float) centerY, ((float) this.circleRadius) * wholeAlpha, this.circlePaint);
                }
            }
            if (this.overlayImageView.hasBitmapImage()) {
                this.overlayImageView.setAlpha(this.overrideAlpha * wholeAlpha);
                if (!this.drawMiniIcon || this.miniDrawCanvas == null) {
                    this.overlayImageView.draw(canvas);
                    canvas.drawCircle((float) centerX, (float) centerY, (float) this.circleRadius, this.overlayPaint);
                } else {
                    this.overlayImageView.draw(this.miniDrawCanvas);
                    this.miniDrawCanvas.drawCircle((float) centerX, (float) centerY, (float) this.circleRadius, this.overlayPaint);
                }
            }
            this.mediaActionDrawable.setBounds(centerX - this.circleRadius, centerY - this.circleRadius, this.circleRadius + centerX, this.circleRadius + centerY);
            if (this.drawMiniIcon) {
                this.mediaActionDrawable.setAlpha((int) ((255.0f * wholeAlpha) * this.overrideAlpha));
                if (this.miniDrawCanvas != null) {
                    this.mediaActionDrawable.draw(this.miniDrawCanvas);
                } else {
                    this.mediaActionDrawable.draw(canvas);
                }
            } else {
                this.mediaActionDrawable.draw(canvas);
            }
            if (this.drawMiniIcon) {
                int offset;
                int size;
                float cx;
                float cy;
                if (Math.abs(this.progressRect.width() - ((float) AndroidUtilities.dp(44.0f))) < AndroidUtilities.density) {
                    offset = 0;
                    size = 20;
                    cx = this.progressRect.centerX() + ((float) AndroidUtilities.dp((float) 16));
                    cy = this.progressRect.centerY() + ((float) AndroidUtilities.dp((float) 16));
                } else {
                    offset = 2;
                    size = 22;
                    cx = this.progressRect.centerX() + ((float) AndroidUtilities.dp(18.0f));
                    cy = this.progressRect.centerY() + ((float) AndroidUtilities.dp(18.0f));
                }
                int halfSize = size / 2;
                alpha = this.miniMediaActionDrawable.getCurrentIcon() != 4 ? 1.0f : 1.0f - this.miniMediaActionDrawable.getTransitionProgress();
                if (alpha == 0.0f) {
                    this.drawMiniIcon = false;
                }
                if (this.miniDrawCanvas != null) {
                    this.miniDrawCanvas.drawCircle((float) AndroidUtilities.dp((float) ((size + 18) + offset)), (float) AndroidUtilities.dp((float) ((size + 18) + offset)), ((float) AndroidUtilities.dp((float) (halfSize + 1))) * alpha, Theme.checkboxSquare_eraserPaint);
                } else {
                    this.miniProgressBackgroundPaint.setColor(this.progressColor);
                    this.miniProgressBackgroundPaint.setAlpha((int) (((255.0f * alpha) * wholeAlpha) * this.overrideAlpha));
                    canvas.drawCircle(cx, cy, (float) AndroidUtilities.dp(12.0f), this.miniProgressBackgroundPaint);
                }
                if (this.miniDrawCanvas != null) {
                    canvas.drawBitmap(this.miniDrawBitmap, (float) ((int) this.progressRect.left), (float) ((int) this.progressRect.top), null);
                }
                canvas.drawCircle(cx, cy, ((float) AndroidUtilities.dp((float) halfSize)) * alpha, this.circleMiniPaint);
                this.miniMediaActionDrawable.setAlpha((int) ((255.0f * wholeAlpha) * this.overrideAlpha));
                this.miniMediaActionDrawable.setBounds((int) (cx - (((float) AndroidUtilities.dp((float) halfSize)) * alpha)), (int) (cy - (((float) AndroidUtilities.dp((float) halfSize)) * alpha)), (int) ((((float) AndroidUtilities.dp((float) halfSize)) * alpha) + cx), (int) ((((float) AndroidUtilities.dp((float) halfSize)) * alpha) + cy));
                this.miniMediaActionDrawable.draw(canvas);
            }
        }
    }
}
