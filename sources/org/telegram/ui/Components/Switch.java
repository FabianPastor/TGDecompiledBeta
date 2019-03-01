package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build.VERSION;
import android.support.annotation.Keep;
import android.util.StateSet;
import android.view.View;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;

public class Switch extends View {
    private boolean attachedToWindow;
    private boolean bitmapsCreated;
    private ObjectAnimator checkAnimator;
    private int colorSet;
    private int drawIconType;
    private boolean drawRipple;
    private ObjectAnimator iconAnimator;
    private Drawable iconDrawable;
    private float iconProgress = 1.0f;
    private boolean isChecked;
    private int lastIconColor;
    private OnCheckedChangeListener onCheckedChangeListener;
    private Bitmap[] overlayBitmap;
    private Canvas[] overlayCanvas;
    private float overlayCx;
    private float overlayCy;
    private Paint overlayEraserPaint;
    private Bitmap overlayMaskBitmap;
    private Canvas overlayMaskCanvas;
    private Paint overlayMaskPaint;
    private float overlayRad;
    private int overrideColorProgress;
    private Paint paint = new Paint(1);
    private Paint paint2 = new Paint(1);
    private int[] pressedState = new int[]{16842910, 16842919};
    private float progress;
    private RectF rectF = new RectF();
    private RippleDrawable rippleDrawable;
    private Paint ripplePaint;
    private String thumbCheckedColorKey = "windowBackgroundWhite";
    private String thumbColorKey = "windowBackgroundWhite";
    private String trackCheckedColorKey = "switch2TrackChecked";
    private String trackColorKey = "switch2Track";

    public interface OnCheckedChangeListener {
        void onCheckedChanged(Switch switchR, boolean z);
    }

    public Switch(Context context) {
        super(context);
        this.paint2.setStyle(Style.STROKE);
        this.paint2.setStrokeCap(Cap.ROUND);
        this.paint2.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
    }

    @Keep
    public void setProgress(float value) {
        if (this.progress != value) {
            this.progress = value;
            invalidate();
        }
    }

    @Keep
    public float getProgress() {
        return this.progress;
    }

    @Keep
    public void setIconProgress(float value) {
        if (this.iconProgress != value) {
            this.iconProgress = value;
            invalidate();
        }
    }

    @Keep
    public float getIconProgress() {
        return this.iconProgress;
    }

    private void cancelCheckAnimator() {
        if (this.checkAnimator != null) {
            this.checkAnimator.cancel();
            this.checkAnimator = null;
        }
    }

    private void cancelIconAnimator() {
        if (this.iconAnimator != null) {
            this.iconAnimator.cancel();
            this.iconAnimator = null;
        }
    }

    public void setDrawIconType(int type) {
        this.drawIconType = type;
    }

    public void setDrawRipple(boolean value) {
        int i = 2;
        if (VERSION.SDK_INT >= 21 && value != this.drawRipple) {
            this.drawRipple = value;
            if (this.rippleDrawable == null) {
                this.ripplePaint = new Paint(1);
                this.ripplePaint.setColor(-1);
                this.rippleDrawable = new RippleDrawable(new ColorStateList(new int[][]{StateSet.WILD_CARD}, new int[]{0}), null, new Drawable() {
                    public void draw(Canvas canvas) {
                        Rect bounds = getBounds();
                        canvas.drawCircle((float) bounds.centerX(), (float) bounds.centerY(), (float) AndroidUtilities.dp(18.0f), Switch.this.ripplePaint);
                    }

                    public void setAlpha(int alpha) {
                    }

                    public void setColorFilter(ColorFilter colorFilter) {
                    }

                    public int getOpacity() {
                        return 0;
                    }
                });
                this.rippleDrawable.setCallback(this);
            }
            if ((this.isChecked && this.colorSet != 2) || !(this.isChecked || this.colorSet == 1)) {
                int color = this.isChecked ? Theme.getColor("switchTrackBlueSelectorChecked") : Theme.getColor("switchTrackBlueSelector");
                this.rippleDrawable.setColor(new ColorStateList(new int[][]{StateSet.WILD_CARD}, new int[]{color}));
                if (!this.isChecked) {
                    i = 1;
                }
                this.colorSet = i;
            }
            if (VERSION.SDK_INT >= 28 && value) {
                this.rippleDrawable.setHotspot(this.isChecked ? 0.0f : (float) AndroidUtilities.dp(100.0f), (float) AndroidUtilities.dp(18.0f));
            }
            this.rippleDrawable.setState(value ? this.pressedState : StateSet.NOTHING);
            invalidate();
        }
    }

    protected boolean verifyDrawable(Drawable who) {
        return super.verifyDrawable(who) || (this.rippleDrawable != null && who == this.rippleDrawable);
    }

    public void setColors(String track, String trackChecked, String thumb, String thumbChecked) {
        this.trackColorKey = track;
        this.trackCheckedColorKey = trackChecked;
        this.thumbColorKey = thumb;
        this.thumbCheckedColorKey = thumbChecked;
    }

    private void animateToCheckedState(boolean newCheckedState) {
        String str = "progress";
        float[] fArr = new float[1];
        fArr[0] = newCheckedState ? 1.0f : 0.0f;
        this.checkAnimator = ObjectAnimator.ofFloat(this, str, fArr);
        this.checkAnimator.setDuration(250);
        this.checkAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                Switch.this.checkAnimator = null;
            }
        });
        this.checkAnimator.start();
    }

    private void animateIcon(boolean newCheckedState) {
        String str = "iconProgress";
        float[] fArr = new float[1];
        fArr[0] = newCheckedState ? 1.0f : 0.0f;
        this.iconAnimator = ObjectAnimator.ofFloat(this, str, fArr);
        this.iconAnimator.setDuration(250);
        this.iconAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                Switch.this.iconAnimator = null;
            }
        });
        this.iconAnimator.start();
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.attachedToWindow = true;
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.attachedToWindow = false;
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        this.onCheckedChangeListener = listener;
    }

    public void setChecked(boolean checked, boolean animated) {
        setChecked(checked, this.drawIconType, animated);
    }

    public void setChecked(boolean checked, int iconType, boolean animated) {
        float f = 1.0f;
        if (checked != this.isChecked) {
            this.isChecked = checked;
            if (this.attachedToWindow && animated) {
                animateToCheckedState(checked);
            } else {
                cancelCheckAnimator();
                setProgress(checked ? 1.0f : 0.0f);
            }
            if (this.onCheckedChangeListener != null) {
                this.onCheckedChangeListener.onCheckedChanged(this, checked);
            }
        }
        if (this.drawIconType != iconType) {
            this.drawIconType = iconType;
            if (this.attachedToWindow && animated) {
                animateIcon(iconType == 0);
                return;
            }
            cancelIconAnimator();
            if (iconType != 0) {
                f = 0.0f;
            }
            setIconProgress(f);
        }
    }

    public void setIcon(int icon) {
        if (icon != 0) {
            this.iconDrawable = getResources().getDrawable(icon).mutate();
            if (this.iconDrawable != null) {
                Drawable drawable = this.iconDrawable;
                int color = Theme.getColor(this.isChecked ? this.trackCheckedColorKey : this.trackColorKey);
                this.lastIconColor = color;
                drawable.setColorFilter(new PorterDuffColorFilter(color, Mode.MULTIPLY));
                return;
            }
            return;
        }
        this.iconDrawable = null;
    }

    public boolean hasIcon() {
        return this.iconDrawable != null;
    }

    public boolean isChecked() {
        return this.isChecked;
    }

    public void setOverrideColor(int override) {
        if (this.overrideColorProgress != override) {
            if (this.overlayBitmap == null) {
                try {
                    this.overlayBitmap = new Bitmap[2];
                    this.overlayCanvas = new Canvas[2];
                    for (int a = 0; a < 2; a++) {
                        this.overlayBitmap[a] = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Config.ARGB_8888);
                        this.overlayCanvas[a] = new Canvas(this.overlayBitmap[a]);
                    }
                    this.overlayMaskBitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Config.ARGB_8888);
                    this.overlayMaskCanvas = new Canvas(this.overlayMaskBitmap);
                    this.overlayEraserPaint = new Paint(1);
                    this.overlayEraserPaint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
                    this.overlayMaskPaint = new Paint(1);
                    this.overlayMaskPaint.setXfermode(new PorterDuffXfermode(Mode.DST_OUT));
                    this.bitmapsCreated = true;
                } catch (Throwable th) {
                    return;
                }
            }
            if (this.bitmapsCreated) {
                this.overrideColorProgress = override;
                this.overlayCx = 0.0f;
                this.overlayCy = 0.0f;
                this.overlayRad = 0.0f;
                invalidate();
            }
        }
    }

    public void setOverrideColorProgress(float cx, float cy, float rad) {
        this.overlayCx = cx;
        this.overlayCy = cy;
        this.overlayRad = rad;
        invalidate();
    }

    protected void onDraw(Canvas canvas) {
        if (getVisibility() == 0) {
            float colorProgress;
            int color1;
            int color2;
            int r1;
            int r2;
            int g1;
            int g2;
            int b1;
            int b2;
            int a1;
            int width = AndroidUtilities.dp(31.0f);
            int thumb = AndroidUtilities.dp(20.0f);
            int x = (getMeasuredWidth() - width) / 2;
            int y = (getMeasuredHeight() - AndroidUtilities.dp(14.0f)) / 2;
            int tx = (AndroidUtilities.dp(7.0f) + x) + ((int) (((float) AndroidUtilities.dp(17.0f)) * this.progress));
            int ty = getMeasuredHeight() / 2;
            int a = 0;
            while (a < 2) {
                if (a != 1 || this.overrideColorProgress != 0) {
                    Canvas canvasToDraw = a == 0 ? canvas : this.overlayCanvas[0];
                    if (a == 1) {
                        this.overlayBitmap[0].eraseColor(0);
                        this.paint.setColor(-16777216);
                        this.overlayMaskCanvas.drawRect(0.0f, 0.0f, (float) this.overlayMaskBitmap.getWidth(), (float) this.overlayMaskBitmap.getHeight(), this.paint);
                        this.overlayMaskCanvas.drawCircle(this.overlayCx - getX(), this.overlayCy - getY(), this.overlayRad, this.overlayEraserPaint);
                    }
                    colorProgress = this.overrideColorProgress == 1 ? a == 0 ? 0.0f : 1.0f : this.overrideColorProgress == 2 ? a == 0 ? 1.0f : 0.0f : this.progress;
                    color1 = Theme.getColor(this.trackColorKey);
                    color2 = Theme.getColor(this.trackCheckedColorKey);
                    if (a == 0 && this.iconDrawable != null) {
                        int i;
                        int i2 = this.lastIconColor;
                        if (this.isChecked) {
                            i = color2;
                        } else {
                            i = color1;
                        }
                        if (i2 != i) {
                            Drawable drawable = this.iconDrawable;
                            if (this.isChecked) {
                                i = color2;
                            } else {
                                i = color1;
                            }
                            this.lastIconColor = i;
                            drawable.setColorFilter(new PorterDuffColorFilter(i, Mode.MULTIPLY));
                        }
                    }
                    r1 = Color.red(color1);
                    r2 = Color.red(color2);
                    g1 = Color.green(color1);
                    g2 = Color.green(color2);
                    b1 = Color.blue(color1);
                    b2 = Color.blue(color2);
                    a1 = Color.alpha(color1);
                    int color = ((((((int) (((float) a1) + (((float) (Color.alpha(color2) - a1)) * colorProgress))) & 255) << 24) | ((((int) (((float) r1) + (((float) (r2 - r1)) * colorProgress))) & 255) << 16)) | ((((int) (((float) g1) + (((float) (g2 - g1)) * colorProgress))) & 255) << 8)) | (((int) (((float) b1) + (((float) (b2 - b1)) * colorProgress))) & 255);
                    this.paint.setColor(color);
                    this.paint2.setColor(color);
                    this.rectF.set((float) x, (float) y, (float) (x + width), (float) (AndroidUtilities.dp(14.0f) + y));
                    canvasToDraw.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(7.0f), (float) AndroidUtilities.dp(7.0f), this.paint);
                    canvasToDraw.drawCircle((float) tx, (float) ty, (float) AndroidUtilities.dp(10.0f), this.paint);
                    if (a == 0 && this.rippleDrawable != null) {
                        this.rippleDrawable.setBounds(tx - AndroidUtilities.dp(18.0f), ty - AndroidUtilities.dp(18.0f), AndroidUtilities.dp(18.0f) + tx, AndroidUtilities.dp(18.0f) + ty);
                        this.rippleDrawable.draw(canvasToDraw);
                    } else if (a == 1) {
                        canvasToDraw.drawBitmap(this.overlayMaskBitmap, 0.0f, 0.0f, this.overlayMaskPaint);
                    }
                }
                a++;
            }
            if (this.overrideColorProgress != 0) {
                canvas.drawBitmap(this.overlayBitmap[0], 0.0f, 0.0f, null);
            }
            a = 0;
            while (a < 2) {
                if (a != 1 || this.overrideColorProgress != 0) {
                    Canvas canvasToDraw2 = a == 0 ? canvas : this.overlayCanvas[1];
                    if (a == 1) {
                        this.overlayBitmap[1].eraseColor(0);
                    }
                    colorProgress = this.overrideColorProgress == 1 ? a == 0 ? 0.0f : 1.0f : this.overrideColorProgress == 2 ? a == 0 ? 1.0f : 0.0f : this.progress;
                    color1 = Theme.getColor(this.thumbColorKey);
                    color2 = Theme.getColor(this.thumbCheckedColorKey);
                    r1 = Color.red(color1);
                    r2 = Color.red(color2);
                    g1 = Color.green(color1);
                    g2 = Color.green(color2);
                    b1 = Color.blue(color1);
                    b2 = Color.blue(color2);
                    a1 = Color.alpha(color1);
                    this.paint.setColor(((((((int) (((float) a1) + (((float) (Color.alpha(color2) - a1)) * colorProgress))) & 255) << 24) | ((((int) (((float) r1) + (((float) (r2 - r1)) * colorProgress))) & 255) << 16)) | ((((int) (((float) g1) + (((float) (g2 - g1)) * colorProgress))) & 255) << 8)) | (((int) (((float) b1) + (((float) (b2 - b1)) * colorProgress))) & 255));
                    canvasToDraw2.drawCircle((float) tx, (float) ty, (float) AndroidUtilities.dp(8.0f), this.paint);
                    if (a == 0) {
                        if (this.iconDrawable != null) {
                            this.iconDrawable.setBounds(tx - (this.iconDrawable.getIntrinsicWidth() / 2), ty - (this.iconDrawable.getIntrinsicHeight() / 2), (this.iconDrawable.getIntrinsicWidth() / 2) + tx, (this.iconDrawable.getIntrinsicHeight() / 2) + ty);
                            this.iconDrawable.draw(canvasToDraw2);
                        } else if (this.drawIconType == 1) {
                            tx = (int) (((float) tx) - (((float) AndroidUtilities.dp(10.8f)) - (((float) AndroidUtilities.dp(1.3f)) * this.progress)));
                            ty = (int) (((float) ty) - (((float) AndroidUtilities.dp(8.5f)) - (((float) AndroidUtilities.dp(0.5f)) * this.progress)));
                            int startX2 = ((int) AndroidUtilities.dpf2(4.6f)) + tx;
                            int startY2 = (int) (AndroidUtilities.dpf2(9.5f) + ((float) ty));
                            int startX = ((int) AndroidUtilities.dpf2(7.5f)) + tx;
                            int startY = ((int) AndroidUtilities.dpf2(5.4f)) + ty;
                            int endX = startX + AndroidUtilities.dp(7.0f);
                            int endY = startY + AndroidUtilities.dp(7.0f);
                            canvasToDraw2.drawLine((float) ((int) (((float) startX) + (((float) (startX2 - startX)) * this.progress))), (float) ((int) (((float) startY) + (((float) (startY2 - startY)) * this.progress))), (float) ((int) (((float) endX) + (((float) ((startX2 + AndroidUtilities.dp(2.0f)) - endX)) * this.progress))), (float) ((int) (((float) endY) + (((float) ((startY2 + AndroidUtilities.dp(2.0f)) - endY)) * this.progress))), this.paint2);
                            startX = ((int) AndroidUtilities.dpf2(7.5f)) + tx;
                            startY = ((int) AndroidUtilities.dpf2(12.5f)) + ty;
                            canvasToDraw2.drawLine((float) startX, (float) startY, (float) (startX + AndroidUtilities.dp(7.0f)), (float) (startY - AndroidUtilities.dp(7.0f)), this.paint2);
                        } else if (this.drawIconType == 2 || this.iconAnimator != null) {
                            this.paint2.setAlpha((int) (255.0f * (1.0f - this.iconProgress)));
                            canvasToDraw2.drawLine((float) tx, (float) ty, (float) tx, (float) (ty - AndroidUtilities.dp(5.0f)), this.paint2);
                            canvasToDraw2.save();
                            canvasToDraw2.rotate(-90.0f * this.iconProgress, (float) tx, (float) ty);
                            canvasToDraw2.drawLine((float) tx, (float) ty, (float) (AndroidUtilities.dp(4.0f) + tx), (float) ty, this.paint2);
                            canvasToDraw2.restore();
                        }
                    }
                    if (a == 1) {
                        canvasToDraw2.drawBitmap(this.overlayMaskBitmap, 0.0f, 0.0f, this.overlayMaskPaint);
                    }
                }
                a++;
            }
            if (this.overrideColorProgress != 0) {
                canvas.drawBitmap(this.overlayBitmap[1], 0.0f, 0.0f, null);
            }
        }
    }
}
