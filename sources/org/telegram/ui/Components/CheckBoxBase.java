package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.text.TextPaint;
import android.view.View;
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
    private int backgroundType;
    private Canvas bitmapCanvas;
    private Rect bounds = new Rect();
    /* access modifiers changed from: private */
    public ObjectAnimator checkAnimator;
    private String checkColorKey = "checkboxCheck";
    private Paint checkPaint;
    /* access modifiers changed from: private */
    public String checkedText;
    private Bitmap drawBitmap;
    private boolean drawUnchecked = true;
    private boolean enabled = true;
    /* access modifiers changed from: private */
    public boolean isChecked;
    private Theme.MessageDrawable messageDrawable;
    private View parentView;
    private Path path = new Path();
    private float progress;
    private ProgressDelegate progressDelegate;
    private RectF rect = new RectF();
    private final Theme.ResourcesProvider resourcesProvider;
    private float size;
    private TextPaint textPaint;
    private boolean useDefaultCheck;

    public interface ProgressDelegate {
        void setProgress(float f);
    }

    public CheckBoxBase(View parent, int sz, Theme.ResourcesProvider resourcesProvider2) {
        this.resourcesProvider = resourcesProvider2;
        this.parentView = parent;
        this.size = (float) sz;
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

    public void setBounds(int x, int y, int width, int height) {
        this.bounds.left = x;
        this.bounds.top = y;
        this.bounds.right = x + width;
        this.bounds.bottom = y + height;
    }

    public void setDrawUnchecked(boolean value) {
        this.drawUnchecked = value;
    }

    public void setProgress(float value) {
        if (this.progress != value) {
            this.progress = value;
            invalidate();
            ProgressDelegate progressDelegate2 = this.progressDelegate;
            if (progressDelegate2 != null) {
                progressDelegate2.setProgress(value);
            }
        }
    }

    private void invalidate() {
        if (this.parentView.getParent() != null) {
            ((View) this.parentView.getParent()).invalidate();
        }
        this.parentView.invalidate();
    }

    public void setProgressDelegate(ProgressDelegate delegate) {
        this.progressDelegate = delegate;
    }

    public float getProgress() {
        return this.progress;
    }

    public boolean isChecked() {
        return this.isChecked;
    }

    public void setEnabled(boolean value) {
        this.enabled = value;
    }

    public void setBackgroundType(int type) {
        this.backgroundType = type;
        if (type == 12 || type == 13) {
            this.backgroundPaint.setStrokeWidth((float) AndroidUtilities.dp(1.0f));
        } else if (type == 4 || type == 5) {
            this.backgroundPaint.setStrokeWidth((float) AndroidUtilities.dp(1.9f));
            if (type == 5) {
                this.checkPaint.setStrokeWidth((float) AndroidUtilities.dp(1.5f));
            }
        } else if (type == 3) {
            this.backgroundPaint.setStrokeWidth((float) AndroidUtilities.dp(1.2f));
        } else if (type != 0) {
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

    private void animateToCheckedState(boolean newCheckedState) {
        float[] fArr = new float[1];
        fArr[0] = newCheckedState ? 1.0f : 0.0f;
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this, "progress", fArr);
        this.checkAnimator = ofFloat;
        ofFloat.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                if (animation.equals(CheckBoxBase.this.checkAnimator)) {
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

    public void setColor(String background, String background2, String check) {
        this.backgroundColorKey = background;
        this.background2ColorKey = background2;
        this.checkColorKey = check;
    }

    public void setBackgroundDrawable(Theme.MessageDrawable drawable) {
        this.messageDrawable = drawable;
    }

    public void setUseDefaultCheck(boolean value) {
        this.useDefaultCheck = value;
    }

    public void setBackgroundAlpha(float alpha) {
        this.backgroundAlpha = alpha;
    }

    public void setNum(int num) {
        if (num >= 0) {
            this.checkedText = "" + (num + 1);
        } else if (this.checkAnimator == null) {
            this.checkedText = null;
        }
        invalidate();
    }

    public void setChecked(boolean checked, boolean animated) {
        setChecked(-1, checked, animated);
    }

    public void setChecked(int num, boolean checked, boolean animated) {
        if (num >= 0) {
            this.checkedText = "" + (num + 1);
            invalidate();
        }
        if (checked != this.isChecked) {
            this.isChecked = checked;
            if (!this.attachedToWindow || !animated) {
                cancelCheckAnimator();
                setProgress(checked ? 1.0f : 0.0f);
                return;
            }
            animateToCheckedState(checked);
        }
    }

    public void draw(Canvas canvas) {
        float outerRad;
        float rad;
        int cx;
        int cy;
        float y;
        float textSize;
        String str;
        int cy2;
        int cx2;
        int cy3;
        int cx3;
        int startAngle;
        int sweepAngle;
        int sweepAngle2;
        int i;
        Canvas canvas2 = canvas;
        Bitmap bitmap = this.drawBitmap;
        if (bitmap != null) {
            bitmap.eraseColor(0);
            float rad2 = (float) AndroidUtilities.dp(this.size / 2.0f);
            float outerRad2 = rad2;
            int i2 = this.backgroundType;
            if (i2 == 12 || i2 == 13) {
                float rad3 = (float) AndroidUtilities.dp(10.0f);
                rad = rad3;
                outerRad = rad3;
            } else if (i2 == 0 || i2 == 11) {
                rad = rad2;
                outerRad = outerRad2;
            } else {
                rad = rad2;
                outerRad = outerRad2 - ((float) AndroidUtilities.dp(0.2f));
            }
            float rad4 = this.progress;
            float roundProgress = rad4 >= 0.5f ? 1.0f : rad4 / 0.5f;
            int cx4 = this.bounds.centerX();
            int cy4 = this.bounds.centerY();
            String str2 = this.backgroundColorKey;
            if (str2 != null) {
                if (this.drawUnchecked) {
                    int i3 = this.backgroundType;
                    if (i3 == 12 || i3 == 13) {
                        paint.setColor(getThemedColor(str2));
                        paint.setAlpha((int) (this.backgroundAlpha * 255.0f));
                        this.backgroundPaint.setColor(getThemedColor(this.checkColorKey));
                    } else if (i3 == 6 || i3 == 7) {
                        paint.setColor(getThemedColor(this.background2ColorKey));
                        this.backgroundPaint.setColor(getThemedColor(this.checkColorKey));
                    } else if (i3 == 10) {
                        this.backgroundPaint.setColor(getThemedColor(this.background2ColorKey));
                    } else {
                        paint.setColor((Theme.getServiceMessageColor() & 16777215) | NUM);
                        this.backgroundPaint.setColor(getThemedColor(this.checkColorKey));
                    }
                } else {
                    Paint paint2 = this.backgroundPaint;
                    String str3 = this.background2ColorKey;
                    if (str3 == null) {
                        str3 = this.checkColorKey;
                    }
                    paint2.setColor(AndroidUtilities.getOffsetColor(16777215, getThemedColor(str3), this.progress, this.backgroundAlpha));
                }
            } else if (this.drawUnchecked) {
                paint.setColor(Color.argb((int) (this.backgroundAlpha * 25.0f), 0, 0, 0));
                if (this.backgroundType == 8) {
                    this.backgroundPaint.setColor(getThemedColor(this.background2ColorKey));
                } else {
                    this.backgroundPaint.setColor(AndroidUtilities.getOffsetColor(-1, getThemedColor(this.checkColorKey), this.progress, this.backgroundAlpha));
                }
            } else {
                Paint paint3 = this.backgroundPaint;
                String str4 = this.background2ColorKey;
                if (str4 == null) {
                    str4 = this.checkColorKey;
                }
                paint3.setColor(AndroidUtilities.getOffsetColor(16777215, getThemedColor(str4), this.progress, this.backgroundAlpha));
            }
            if (this.drawUnchecked && (i = this.backgroundType) >= 0 && i != 12 && i != 13) {
                if (i == 8 || i == 10) {
                    canvas2.drawCircle((float) cx4, (float) cy4, rad - ((float) AndroidUtilities.dp(1.5f)), this.backgroundPaint);
                } else if (i == 6 || i == 7) {
                    canvas2.drawCircle((float) cx4, (float) cy4, rad - ((float) AndroidUtilities.dp(1.0f)), paint);
                    canvas2.drawCircle((float) cx4, (float) cy4, rad - ((float) AndroidUtilities.dp(1.5f)), this.backgroundPaint);
                } else {
                    canvas2.drawCircle((float) cx4, (float) cy4, rad, paint);
                }
            }
            paint.setColor(getThemedColor(this.checkColorKey));
            int i4 = this.backgroundType;
            if (i4 == -1 || i4 == 7 || i4 == 8 || i4 == 9 || i4 == 10) {
                cy = cy4;
                cx = cx4;
            } else {
                if (i4 == 12) {
                    cy2 = cy4;
                    cx2 = cx4;
                } else if (i4 == 13) {
                    cy2 = cy4;
                    cx2 = cx4;
                } else {
                    if (i4 == 0) {
                        cy3 = cy4;
                        cx3 = cx4;
                    } else if (i4 == 11) {
                        cy3 = cy4;
                        cx3 = cx4;
                    } else {
                        this.rect.set(((float) cx4) - outerRad, ((float) cy4) - outerRad, ((float) cx4) + outerRad, ((float) cy4) + outerRad);
                        int i5 = this.backgroundType;
                        if (i5 == 6) {
                            startAngle = 0;
                            sweepAngle = (int) (this.progress * -360.0f);
                        } else if (i5 == 1) {
                            startAngle = -90;
                            sweepAngle = (int) (this.progress * -270.0f);
                        } else {
                            startAngle = 90;
                            sweepAngle = (int) (this.progress * 270.0f);
                        }
                        if (i5 == 6) {
                            int color = getThemedColor("dialogBackground");
                            int alpha = Color.alpha(color);
                            this.backgroundPaint.setColor(color);
                            this.backgroundPaint.setAlpha((int) (((float) alpha) * this.progress));
                            int i6 = alpha;
                            int i7 = color;
                            sweepAngle2 = sweepAngle;
                            cy = cy4;
                            cx = cx4;
                            canvas.drawArc(this.rect, (float) startAngle, (float) sweepAngle, false, this.backgroundPaint);
                            int color2 = getThemedColor("chat_attachPhotoBackground");
                            int alpha2 = Color.alpha(color2);
                            this.backgroundPaint.setColor(color2);
                            this.backgroundPaint.setAlpha((int) (((float) alpha2) * this.progress));
                        } else {
                            sweepAngle2 = sweepAngle;
                            cy = cy4;
                            cx = cx4;
                        }
                        canvas.drawArc(this.rect, (float) startAngle, (float) sweepAngle2, false, this.backgroundPaint);
                    }
                    canvas2.drawCircle((float) cx, (float) cy, rad, this.backgroundPaint);
                }
                this.backgroundPaint.setStyle(Paint.Style.FILL);
                Theme.MessageDrawable messageDrawable2 = this.messageDrawable;
                if (messageDrawable2 == null || !messageDrawable2.hasGradient()) {
                    this.backgroundPaint.setShader((Shader) null);
                } else {
                    Shader shader = this.messageDrawable.getGradientShader();
                    Matrix matrix = this.messageDrawable.getMatrix();
                    matrix.reset();
                    this.messageDrawable.applyMatrixScale();
                    matrix.postTranslate(0.0f, (float) ((-this.messageDrawable.getTopY()) + this.bounds.top));
                    shader.setLocalMatrix(matrix);
                    this.backgroundPaint.setShader(shader);
                }
                canvas2.drawCircle((float) cx, (float) cy, (rad - ((float) AndroidUtilities.dp(1.0f))) * this.backgroundAlpha, this.backgroundPaint);
                this.backgroundPaint.setStyle(Paint.Style.STROKE);
            }
            if (roundProgress > 0.0f) {
                float f = this.progress;
                float checkProgress = f < 0.5f ? 0.0f : (f - 0.5f) / 0.5f;
                int i8 = this.backgroundType;
                if (i8 == 9) {
                    paint.setColor(getThemedColor(this.background2ColorKey));
                } else if (i8 == 11 || i8 == 6 || i8 == 7 || i8 == 10 || (!this.drawUnchecked && this.backgroundColorKey != null)) {
                    paint.setColor(getThemedColor(this.backgroundColorKey));
                } else {
                    paint.setColor(getThemedColor(this.enabled ? "checkbox" : "checkboxDisabled"));
                }
                if (this.useDefaultCheck || (str = this.checkColorKey) == null) {
                    this.checkPaint.setColor(getThemedColor("checkboxCheck"));
                } else {
                    this.checkPaint.setColor(getThemedColor(str));
                }
                int i9 = this.backgroundType;
                if (i9 != -1) {
                    if (i9 == 12 || i9 == 13) {
                        paint.setAlpha((int) (roundProgress * 255.0f));
                        this.bitmapCanvas.drawCircle((float) (this.drawBitmap.getWidth() / 2), (float) (this.drawBitmap.getHeight() / 2), rad * roundProgress, paint);
                    } else {
                        float rad5 = rad - ((float) AndroidUtilities.dp(0.5f));
                        this.bitmapCanvas.drawCircle((float) (this.drawBitmap.getWidth() / 2), (float) (this.drawBitmap.getHeight() / 2), rad5, paint);
                        this.bitmapCanvas.drawCircle((float) (this.drawBitmap.getWidth() / 2), (float) (this.drawBitmap.getHeight() / 2), (1.0f - roundProgress) * rad5, eraser);
                    }
                    Bitmap bitmap2 = this.drawBitmap;
                    canvas2.drawBitmap(bitmap2, (float) (cx - (bitmap2.getWidth() / 2)), (float) (cy - (this.drawBitmap.getHeight() / 2)), (Paint) null);
                }
                if (checkProgress == 0.0f) {
                    return;
                }
                if (this.checkedText != null) {
                    if (this.textPaint == null) {
                        TextPaint textPaint2 = new TextPaint(1);
                        this.textPaint = textPaint2;
                        textPaint2.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                    }
                    switch (this.checkedText.length()) {
                        case 0:
                        case 1:
                        case 2:
                            textSize = 14.0f;
                            y = 18.0f;
                            break;
                        case 3:
                            textSize = 10.0f;
                            y = 16.5f;
                            break;
                        default:
                            textSize = 8.0f;
                            y = 15.75f;
                            break;
                    }
                    this.textPaint.setTextSize((float) AndroidUtilities.dp(textSize));
                    this.textPaint.setColor(getThemedColor(this.checkColorKey));
                    canvas.save();
                    canvas2.scale(checkProgress, 1.0f, (float) cx, (float) cy);
                    String str5 = this.checkedText;
                    canvas2.drawText(str5, ((float) cx) - (this.textPaint.measureText(str5) / 2.0f), (float) AndroidUtilities.dp(y), this.textPaint);
                    canvas.restore();
                    return;
                }
                this.path.reset();
                float scale = 1.0f;
                int i10 = this.backgroundType;
                if (i10 == -1) {
                    scale = 1.4f;
                } else if (i10 == 5) {
                    scale = 0.8f;
                }
                float checkSide = ((float) AndroidUtilities.dp(9.0f * scale)) * checkProgress;
                float smallCheckSide = ((float) AndroidUtilities.dp(scale * 4.0f)) * checkProgress;
                int x = cx - AndroidUtilities.dp(1.5f);
                int y2 = AndroidUtilities.dp(4.0f) + cy;
                float side = (float) Math.sqrt((double) ((smallCheckSide * smallCheckSide) / 2.0f));
                this.path.moveTo(((float) x) - side, ((float) y2) - side);
                this.path.lineTo((float) x, (float) y2);
                float side2 = (float) Math.sqrt((double) ((checkSide * checkSide) / 2.0f));
                this.path.lineTo(((float) x) + side2, ((float) y2) - side2);
                canvas2.drawPath(this.path, this.checkPaint);
            }
        }
    }

    private int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }
}
