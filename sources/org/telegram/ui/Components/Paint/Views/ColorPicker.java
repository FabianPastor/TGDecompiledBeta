package org.telegram.ui.Components.Paint.Views;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Paint.Swatch;

public class ColorPicker extends FrameLayout {
    private static final int[] COLORS = {-1431751, -2409774, -13610525, -11942419, -8337308, -205211, -223667, -16777216, -1};
    private static final float[] LOCATIONS = {0.0f, 0.14f, 0.24f, 0.39f, 0.49f, 0.62f, 0.73f, 0.85f, 1.0f};
    private Paint backgroundPaint = new Paint(1);
    private boolean changingWeight;
    private ColorPickerDelegate delegate;
    private boolean dragging;
    private float draggingFactor;
    private Paint gradientPaint = new Paint(1);
    private boolean interacting;
    private OvershootInterpolator interpolator = new OvershootInterpolator(1.02f);
    private float location;
    private RectF rectF = new RectF();
    private ImageView settingsButton;
    private Drawable shadowDrawable;
    private Paint swatchPaint = new Paint(1);
    private Paint swatchStrokePaint = new Paint(1);
    private ImageView undoButton;
    private boolean wasChangingWeight;
    private float weight = 0.016773745f;

    public interface ColorPickerDelegate {
        void onBeganColorPicking();

        void onColorValueChanged();

        void onFinishedColorPicking();

        void onSettingsPressed();

        void onUndoPressed();
    }

    public ColorPicker(Context context) {
        super(context);
        setWillNotDraw(false);
        this.shadowDrawable = getResources().getDrawable(NUM);
        this.backgroundPaint.setColor(-1);
        this.swatchStrokePaint.setStyle(Paint.Style.STROKE);
        this.swatchStrokePaint.setStrokeWidth((float) AndroidUtilities.dp(1.0f));
        ImageView imageView = new ImageView(context);
        this.settingsButton = imageView;
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        this.settingsButton.setImageResource(NUM);
        addView(this.settingsButton, LayoutHelper.createFrame(46, 52.0f));
        this.settingsButton.setOnClickListener(new ColorPicker$$ExternalSyntheticLambda0(this));
        ImageView imageView2 = new ImageView(context);
        this.undoButton = imageView2;
        imageView2.setScaleType(ImageView.ScaleType.CENTER);
        this.undoButton.setImageResource(NUM);
        addView(this.undoButton, LayoutHelper.createFrame(46, 52.0f));
        this.undoButton.setOnClickListener(new ColorPicker$$ExternalSyntheticLambda1(this));
        SharedPreferences preferences = context.getSharedPreferences("paint", 0);
        this.location = preferences.getFloat("last_color_location", 1.0f);
        setWeight(preferences.getFloat("last_color_weight", 0.016773745f));
        setLocation(this.location);
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-Paint-Views-ColorPicker  reason: not valid java name */
    public /* synthetic */ void m4155lambda$new$0$orgtelegramuiComponentsPaintViewsColorPicker(View v) {
        ColorPickerDelegate colorPickerDelegate = this.delegate;
        if (colorPickerDelegate != null) {
            colorPickerDelegate.onSettingsPressed();
        }
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-Paint-Views-ColorPicker  reason: not valid java name */
    public /* synthetic */ void m4156lambda$new$1$orgtelegramuiComponentsPaintViewsColorPicker(View v) {
        ColorPickerDelegate colorPickerDelegate = this.delegate;
        if (colorPickerDelegate != null) {
            colorPickerDelegate.onUndoPressed();
        }
    }

    public void setUndoEnabled(boolean enabled) {
        this.undoButton.setAlpha(enabled ? 1.0f : 0.3f);
        this.undoButton.setEnabled(enabled);
    }

    public void setDelegate(ColorPickerDelegate colorPickerDelegate) {
        this.delegate = colorPickerDelegate;
    }

    public View getSettingsButton() {
        return this.settingsButton;
    }

    public void setSettingsButtonImage(int resId) {
        this.settingsButton.setImageResource(resId);
    }

    public Swatch getSwatch() {
        return new Swatch(colorForLocation(this.location), this.location, this.weight);
    }

    public void setSwatch(Swatch swatch) {
        setLocation(swatch.colorLocation);
        setWeight(swatch.brushWeight);
    }

    public int colorForLocation(float location2) {
        float[] fArr;
        if (location2 <= 0.0f) {
            return COLORS[0];
        }
        if (location2 >= 1.0f) {
            int[] iArr = COLORS;
            return iArr[iArr.length - 1];
        }
        int leftIndex = -1;
        int rightIndex = -1;
        int i = 1;
        while (true) {
            fArr = LOCATIONS;
            if (i >= fArr.length) {
                break;
            } else if (fArr[i] >= location2) {
                leftIndex = i - 1;
                rightIndex = i;
                break;
            } else {
                i++;
            }
        }
        float leftLocation = fArr[leftIndex];
        int[] iArr2 = COLORS;
        return interpolateColors(iArr2[leftIndex], iArr2[rightIndex], (location2 - leftLocation) / (fArr[rightIndex] - leftLocation));
    }

    private int interpolateColors(int leftColor, int rightColor, float factor) {
        float factor2 = Math.min(Math.max(factor, 0.0f), 1.0f);
        int r1 = Color.red(leftColor);
        int r2 = Color.red(rightColor);
        int g1 = Color.green(leftColor);
        int g2 = Color.green(rightColor);
        int b1 = Color.blue(leftColor);
        return Color.argb(255, Math.min(255, (int) (((float) r1) + (((float) (r2 - r1)) * factor2))), Math.min(255, (int) (((float) g1) + (((float) (g2 - g1)) * factor2))), Math.min(255, (int) (((float) b1) + (((float) (Color.blue(rightColor) - b1)) * factor2))));
    }

    public void setLocation(float value) {
        this.location = value;
        int color = colorForLocation(value);
        this.swatchPaint.setColor(color);
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        if (((double) hsv[0]) >= 0.001d || ((double) hsv[1]) >= 0.001d || hsv[2] <= 0.92f) {
            this.swatchStrokePaint.setColor(color);
        } else {
            int c = (int) ((1.0f - (((hsv[2] - 0.92f) / 0.08f) * 0.22f)) * 255.0f);
            this.swatchStrokePaint.setColor(Color.rgb(c, c, c));
        }
        invalidate();
    }

    public void setWeight(float value) {
        this.weight = value;
        invalidate();
    }

    public boolean onTouchEvent(MotionEvent event) {
        ColorPickerDelegate colorPickerDelegate;
        if (event.getPointerCount() > 1) {
            return false;
        }
        float x = event.getX() - this.rectF.left;
        float y = event.getY() - this.rectF.top;
        if (!this.interacting && y < ((float) (-AndroidUtilities.dp(10.0f)))) {
            return false;
        }
        int action = event.getActionMasked();
        if (action == 3 || action == 1 || action == 6) {
            if (this.interacting && (colorPickerDelegate = this.delegate) != null) {
                colorPickerDelegate.onFinishedColorPicking();
                SharedPreferences.Editor editor = getContext().getSharedPreferences("paint", 0).edit();
                editor.putFloat("last_color_location", this.location);
                editor.putFloat("last_color_weight", this.weight);
                editor.commit();
            }
            this.interacting = false;
            this.wasChangingWeight = this.changingWeight;
            this.changingWeight = false;
            setDragging(false, true);
        } else if (action == 0 || action == 2) {
            if (!this.interacting) {
                this.interacting = true;
                ColorPickerDelegate colorPickerDelegate2 = this.delegate;
                if (colorPickerDelegate2 != null) {
                    colorPickerDelegate2.onBeganColorPicking();
                }
            }
            setLocation(Math.max(0.0f, Math.min(1.0f, x / this.rectF.width())));
            setDragging(true, true);
            if (y < ((float) (-AndroidUtilities.dp(10.0f)))) {
                this.changingWeight = true;
                setWeight(Math.max(0.0f, Math.min(1.0f, ((-y) - ((float) AndroidUtilities.dp(10.0f))) / ((float) AndroidUtilities.dp(190.0f)))));
            }
            ColorPickerDelegate colorPickerDelegate3 = this.delegate;
            if (colorPickerDelegate3 != null) {
                colorPickerDelegate3.onColorValueChanged();
            }
            return true;
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int width = right - left;
        int height = bottom - top;
        this.gradientPaint.setShader(new LinearGradient((float) AndroidUtilities.dp(56.0f), 0.0f, (float) (width - AndroidUtilities.dp(56.0f)), 0.0f, COLORS, LOCATIONS, Shader.TileMode.REPEAT));
        int y = height - AndroidUtilities.dp(32.0f);
        this.rectF.set((float) AndroidUtilities.dp(56.0f), (float) y, (float) (width - AndroidUtilities.dp(56.0f)), (float) (AndroidUtilities.dp(12.0f) + y));
        ImageView imageView = this.settingsButton;
        imageView.layout(width - imageView.getMeasuredWidth(), height - AndroidUtilities.dp(52.0f), width, height);
        this.undoButton.layout(0, height - AndroidUtilities.dp(52.0f), this.settingsButton.getMeasuredWidth(), height);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        canvas.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(6.0f), (float) AndroidUtilities.dp(6.0f), this.gradientPaint);
        int cx = (int) (this.rectF.left + (this.rectF.width() * this.location));
        int cy = (int) ((this.rectF.centerY() + (this.draggingFactor * ((float) (-AndroidUtilities.dp(70.0f))))) - (this.changingWeight ? this.weight * ((float) AndroidUtilities.dp(190.0f)) : 0.0f));
        int side = (int) (((float) AndroidUtilities.dp(24.0f)) * (this.draggingFactor + 1.0f) * 0.5f);
        this.shadowDrawable.setBounds(cx - side, cy - side, cx + side, cy + side);
        this.shadowDrawable.draw(canvas);
        float swatchRadius = (((float) ((int) Math.floor((double) (((float) AndroidUtilities.dp(4.0f)) + (((float) (AndroidUtilities.dp(19.0f) - AndroidUtilities.dp(4.0f))) * this.weight))))) * (this.draggingFactor + 1.0f)) / 2.0f;
        canvas.drawCircle((float) cx, (float) cy, ((float) (AndroidUtilities.dp(22.0f) / 2)) * (this.draggingFactor + 1.0f), this.backgroundPaint);
        canvas.drawCircle((float) cx, (float) cy, swatchRadius, this.swatchPaint);
        canvas.drawCircle((float) cx, (float) cy, swatchRadius - ((float) AndroidUtilities.dp(0.5f)), this.swatchStrokePaint);
    }

    private void setDraggingFactor(float factor) {
        this.draggingFactor = factor;
        invalidate();
    }

    public float getDraggingFactor() {
        return this.draggingFactor;
    }

    private void setDragging(boolean value, boolean animated) {
        if (this.dragging != value) {
            this.dragging = value;
            float target = value ? 1.0f : 0.0f;
            if (animated) {
                Animator a = ObjectAnimator.ofFloat(this, "draggingFactor", new float[]{this.draggingFactor, target});
                a.setInterpolator(this.interpolator);
                int duration = 300;
                if (this.wasChangingWeight) {
                    duration = (int) (((float) 300) + (this.weight * 75.0f));
                }
                a.setDuration((long) duration);
                a.start();
                return;
            }
            setDraggingFactor(target);
        }
    }
}
