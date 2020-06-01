package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextPaint;
import android.view.View;
import androidx.annotation.Keep;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;

public class CheckBoxBase {
    private static Paint eraser;
    private static Paint paint;
    private boolean attachedToWindow;
    private String background2ColorKey = "chat_serviceBackground";
    private float backgroundAlpha = 1.0f;
    private String backgroundColorKey = "chat_serviceBackground";
    private Paint backgroundPaint;
    private Canvas bitmapCanvas;
    private Rect bounds = new Rect();
    /* access modifiers changed from: private */
    public ObjectAnimator checkAnimator;
    private String checkColorKey = "checkboxCheck";
    private Paint checkPaint;
    /* access modifiers changed from: private */
    public String checkedText;
    private int drawBackgroundAsArc;
    private Bitmap drawBitmap;
    private boolean drawUnchecked = true;
    private boolean enabled = true;
    /* access modifiers changed from: private */
    public boolean isChecked;
    private View parentView;
    private Path path = new Path();
    private float progress;
    private ProgressDelegate progressDelegate;
    private RectF rect = new RectF();
    private float size;
    private TextPaint textPaint;
    private boolean useDefaultCheck;

    public interface ProgressDelegate {
        void setProgress(float f);
    }

    public CheckBoxBase(View view, int i) {
        this.parentView = view;
        this.size = (float) i;
        if (paint == null) {
            paint = new Paint(1);
            Paint paint2 = new Paint(1);
            eraser = paint2;
            paint2.setColor(0);
            eraser.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        }
        Paint paint3 = new Paint(1);
        this.checkPaint = paint3;
        paint3.setStrokeCap(Paint.Cap.ROUND);
        this.checkPaint.setStyle(Paint.Style.STROKE);
        this.checkPaint.setStrokeJoin(Paint.Join.ROUND);
        this.checkPaint.setStrokeWidth((float) AndroidUtilities.dp(1.9f));
        Paint paint4 = new Paint(1);
        this.backgroundPaint = paint4;
        paint4.setStyle(Paint.Style.STROKE);
        this.backgroundPaint.setStrokeWidth((float) AndroidUtilities.dp(1.2f));
        this.drawBitmap = Bitmap.createBitmap(AndroidUtilities.dp(this.size), AndroidUtilities.dp(this.size), Bitmap.Config.ARGB_4444);
        this.bitmapCanvas = new Canvas(this.drawBitmap);
    }

    public void onAttachedToWindow() {
        this.attachedToWindow = true;
    }

    public void onDetachedFromWindow() {
        this.attachedToWindow = false;
    }

    public void setBounds(int i, int i2, int i3, int i4) {
        Rect rect2 = this.bounds;
        rect2.left = i;
        rect2.top = i2;
        rect2.right = i + i3;
        rect2.bottom = i2 + i4;
    }

    public void setDrawUnchecked(boolean z) {
        this.drawUnchecked = z;
    }

    @Keep
    public void setProgress(float f) {
        if (this.progress != f) {
            this.progress = f;
            invalidate();
            ProgressDelegate progressDelegate2 = this.progressDelegate;
            if (progressDelegate2 != null) {
                progressDelegate2.setProgress(f);
            }
        }
    }

    private void invalidate() {
        if (this.parentView.getParent() != null) {
            ((View) this.parentView.getParent()).invalidate();
        }
        this.parentView.invalidate();
    }

    public void setProgressDelegate(ProgressDelegate progressDelegate2) {
        this.progressDelegate = progressDelegate2;
    }

    @Keep
    public float getProgress() {
        return this.progress;
    }

    public boolean isChecked() {
        return this.isChecked;
    }

    public void setEnabled(boolean z) {
        this.enabled = z;
    }

    public void setDrawBackgroundAsArc(int i) {
        this.drawBackgroundAsArc = i;
        if (i == 4 || i == 5) {
            this.backgroundPaint.setStrokeWidth((float) AndroidUtilities.dp(1.9f));
            if (i == 5) {
                this.checkPaint.setStrokeWidth((float) AndroidUtilities.dp(1.5f));
            }
        } else if (i == 3) {
            this.backgroundPaint.setStrokeWidth((float) AndroidUtilities.dp(1.2f));
        } else if (i != 0) {
            this.backgroundPaint.setStrokeWidth((float) AndroidUtilities.dp(1.5f));
        }
    }

    private void cancelCheckAnimator() {
        ObjectAnimator objectAnimator = this.checkAnimator;
        if (objectAnimator != null) {
            objectAnimator.cancel();
            this.checkAnimator = null;
        }
    }

    private void animateToCheckedState(boolean z) {
        float[] fArr = new float[1];
        fArr[0] = z ? 1.0f : 0.0f;
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this, "progress", fArr);
        this.checkAnimator = ofFloat;
        ofFloat.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                if (animator.equals(CheckBoxBase.this.checkAnimator)) {
                    ObjectAnimator unused = CheckBoxBase.this.checkAnimator = null;
                }
                if (!CheckBoxBase.this.isChecked) {
                    String unused2 = CheckBoxBase.this.checkedText = null;
                }
            }
        });
        this.checkAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT);
        this.checkAnimator.setDuration(200);
        this.checkAnimator.start();
    }

    public void setColor(String str, String str2, String str3) {
        this.backgroundColorKey = str;
        this.background2ColorKey = str2;
        this.checkColorKey = str3;
    }

    public void setUseDefaultCheck(boolean z) {
        this.useDefaultCheck = z;
    }

    public void setBackgroundAlpha(float f) {
        this.backgroundAlpha = f;
    }

    public void setNum(int i) {
        if (i >= 0) {
            this.checkedText = "" + (i + 1);
        } else if (this.checkAnimator == null) {
            this.checkedText = null;
        }
        invalidate();
    }

    public void setChecked(boolean z, boolean z2) {
        setChecked(-1, z, z2);
    }

    public void setChecked(int i, boolean z, boolean z2) {
        if (i >= 0) {
            this.checkedText = "" + (i + 1);
            invalidate();
        }
        if (z != this.isChecked) {
            this.isChecked = z;
            if (!this.attachedToWindow || !z2) {
                cancelCheckAnimator();
                setProgress(z ? 1.0f : 0.0f);
                return;
            }
            animateToCheckedState(z);
        }
    }

    public void draw(Canvas canvas) {
        int i;
        float f;
        float f2;
        String str;
        int i2;
        int i3;
        float f3;
        float f4;
        Canvas canvas2 = canvas;
        Bitmap bitmap = this.drawBitmap;
        if (bitmap != null) {
            bitmap.eraseColor(0);
            float dp = (float) AndroidUtilities.dp(this.size / 2.0f);
            int i4 = this.drawBackgroundAsArc;
            float dp2 = (i4 == 0 || i4 == 11) ? dp : dp - ((float) AndroidUtilities.dp(0.2f));
            float f5 = this.progress;
            float f6 = f5 >= 0.5f ? 1.0f : f5 / 0.5f;
            int centerX = this.bounds.centerX();
            int centerY = this.bounds.centerY();
            if (this.backgroundColorKey != null) {
                if (this.drawUnchecked) {
                    int i5 = this.drawBackgroundAsArc;
                    if (i5 == 6 || i5 == 7) {
                        paint.setColor(Theme.getColor(this.background2ColorKey));
                        this.backgroundPaint.setColor(Theme.getColor(this.checkColorKey));
                    } else if (i5 == 10) {
                        this.backgroundPaint.setColor(Theme.getColor(this.background2ColorKey));
                    } else {
                        paint.setColor((Theme.getServiceMessageColor() & 16777215) | NUM);
                        this.backgroundPaint.setColor(Theme.getColor(this.checkColorKey));
                    }
                } else {
                    Paint paint2 = this.backgroundPaint;
                    String str2 = this.background2ColorKey;
                    if (str2 == null) {
                        str2 = this.checkColorKey;
                    }
                    paint2.setColor(AndroidUtilities.getOffsetColor(16777215, Theme.getColor(str2), this.progress, this.backgroundAlpha));
                }
            } else if (this.drawUnchecked) {
                paint.setColor(Color.argb((int) (this.backgroundAlpha * 25.0f), 0, 0, 0));
                if (this.drawBackgroundAsArc == 8) {
                    this.backgroundPaint.setColor(Theme.getColor(this.background2ColorKey));
                } else {
                    this.backgroundPaint.setColor(AndroidUtilities.getOffsetColor(-1, Theme.getColor(this.checkColorKey), this.progress, this.backgroundAlpha));
                }
            } else {
                Paint paint3 = this.backgroundPaint;
                String str3 = this.background2ColorKey;
                if (str3 == null) {
                    str3 = this.checkColorKey;
                }
                paint3.setColor(AndroidUtilities.getOffsetColor(16777215, Theme.getColor(str3), this.progress, this.backgroundAlpha));
            }
            if (this.drawUnchecked) {
                int i6 = this.drawBackgroundAsArc;
                if (i6 == 8 || i6 == 10) {
                    canvas2.drawCircle((float) centerX, (float) centerY, dp - ((float) AndroidUtilities.dp(1.5f)), this.backgroundPaint);
                } else if (i6 == 6 || i6 == 7) {
                    float f7 = (float) centerX;
                    float f8 = (float) centerY;
                    canvas2.drawCircle(f7, f8, dp - ((float) AndroidUtilities.dp(1.0f)), paint);
                    canvas2.drawCircle(f7, f8, dp - ((float) AndroidUtilities.dp(1.5f)), this.backgroundPaint);
                } else {
                    canvas2.drawCircle((float) centerX, (float) centerY, dp, paint);
                }
            }
            paint.setColor(Theme.getColor(this.checkColorKey));
            int i7 = this.drawBackgroundAsArc;
            if (i7 == 7 || i7 == 8 || i7 == 9 || i7 == 10) {
                i = 10;
            } else if (i7 == 0 || i7 == 11) {
                i = 10;
                canvas2.drawCircle((float) centerX, (float) centerY, dp, this.backgroundPaint);
            } else {
                float f9 = (float) centerX;
                float var_ = (float) centerY;
                this.rect.set(f9 - dp2, var_ - dp2, f9 + dp2, var_ + dp2);
                int i8 = this.drawBackgroundAsArc;
                if (i8 == 6) {
                    i3 = (int) (this.progress * -360.0f);
                    i2 = 0;
                } else {
                    if (i8 == 1) {
                        i2 = -90;
                        f4 = -270.0f;
                        f3 = this.progress;
                    } else {
                        i2 = 90;
                        f4 = 270.0f;
                        f3 = this.progress;
                    }
                    i3 = (int) (f3 * f4);
                }
                if (this.drawBackgroundAsArc == 6) {
                    int color = Theme.getColor("dialogBackground");
                    int alpha = Color.alpha(color);
                    this.backgroundPaint.setColor(color);
                    this.backgroundPaint.setAlpha((int) (((float) alpha) * this.progress));
                    float var_ = (float) i2;
                    float var_ = (float) i3;
                    i = 10;
                    canvas.drawArc(this.rect, var_, var_, false, this.backgroundPaint);
                    int color2 = Theme.getColor("chat_attachPhotoBackground");
                    int alpha2 = Color.alpha(color2);
                    this.backgroundPaint.setColor(color2);
                    this.backgroundPaint.setAlpha((int) (((float) alpha2) * this.progress));
                    canvas.drawArc(this.rect, var_, var_, false, this.backgroundPaint);
                } else {
                    i = 10;
                    canvas.drawArc(this.rect, (float) i2, (float) i3, false, this.backgroundPaint);
                }
            }
            if (f6 > 0.0f) {
                float var_ = this.progress;
                float var_ = var_ < 0.5f ? 0.0f : (var_ - 0.5f) / 0.5f;
                int i9 = this.drawBackgroundAsArc;
                if (i9 == 9) {
                    paint.setColor(Theme.getColor(this.background2ColorKey));
                } else if (i9 == 11 || i9 == 6 || i9 == 7 || i9 == i || (!this.drawUnchecked && this.backgroundColorKey != null)) {
                    paint.setColor(Theme.getColor(this.backgroundColorKey));
                } else {
                    paint.setColor(Theme.getColor(this.enabled ? "checkbox" : "checkboxDisabled"));
                }
                if (this.useDefaultCheck || (str = this.checkColorKey) == null) {
                    this.checkPaint.setColor(Theme.getColor("checkboxCheck"));
                } else {
                    this.checkPaint.setColor(Theme.getColor(str));
                }
                float dp3 = dp - ((float) AndroidUtilities.dp(0.5f));
                this.bitmapCanvas.drawCircle((float) (this.drawBitmap.getWidth() / 2), (float) (this.drawBitmap.getHeight() / 2), dp3, paint);
                this.bitmapCanvas.drawCircle((float) (this.drawBitmap.getWidth() / 2), (float) (this.drawBitmap.getWidth() / 2), dp3 * (1.0f - f6), eraser);
                Bitmap bitmap2 = this.drawBitmap;
                canvas2.drawBitmap(bitmap2, (float) (centerX - (bitmap2.getWidth() / 2)), (float) (centerY - (this.drawBitmap.getHeight() / 2)), (Paint) null);
                if (var_ == 0.0f) {
                    return;
                }
                if (this.checkedText != null) {
                    if (this.textPaint == null) {
                        TextPaint textPaint2 = new TextPaint(1);
                        this.textPaint = textPaint2;
                        textPaint2.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                    }
                    int length = this.checkedText.length();
                    if (length == 0 || length == 1 || length == 2) {
                        f2 = 14.0f;
                        f = 18.0f;
                    } else if (length != 3) {
                        f2 = 8.0f;
                        f = 15.75f;
                    } else {
                        f2 = 10.0f;
                        f = 16.5f;
                    }
                    this.textPaint.setTextSize((float) AndroidUtilities.dp(f2));
                    this.textPaint.setColor(Theme.getColor(this.checkColorKey));
                    canvas.save();
                    float var_ = (float) centerX;
                    canvas2.scale(var_, 1.0f, var_, (float) centerY);
                    String str4 = this.checkedText;
                    canvas2.drawText(str4, var_ - (this.textPaint.measureText(str4) / 2.0f), (float) AndroidUtilities.dp(f), this.textPaint);
                    canvas.restore();
                    return;
                }
                this.path.reset();
                float var_ = this.drawBackgroundAsArc == 5 ? 0.8f : 1.0f;
                float dp4 = ((float) AndroidUtilities.dp(9.0f * var_)) * var_;
                float dp5 = ((float) AndroidUtilities.dp(var_ * 4.0f)) * var_;
                int dp6 = centerX - AndroidUtilities.dp(1.5f);
                int dp7 = centerY + AndroidUtilities.dp(4.0f);
                float sqrt = (float) Math.sqrt((double) ((dp5 * dp5) / 2.0f));
                float var_ = (float) dp6;
                float var_ = (float) dp7;
                this.path.moveTo(var_ - sqrt, var_ - sqrt);
                this.path.lineTo(var_, var_);
                float sqrt2 = (float) Math.sqrt((double) ((dp4 * dp4) / 2.0f));
                this.path.lineTo(var_ + sqrt2, var_ - sqrt2);
                canvas2.drawPath(this.path, this.checkPaint);
            }
        }
    }
}
