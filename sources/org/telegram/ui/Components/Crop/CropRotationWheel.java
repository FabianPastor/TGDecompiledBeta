package org.telegram.ui.Components.Crop;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;

public class CropRotationWheel extends FrameLayout {
    private static final int DELTA_ANGLE = 5;
    private static final int MAX_ANGLE = 45;
    private ImageView aspectRatioButton;
    private Paint bluePaint;
    private TextView degreesLabel;
    private ImageView mirrorButton;
    private float prevX;
    protected float rotation;
    private ImageView rotation90Button;
    private RotationWheelListener rotationListener;
    private RectF tempRect = new RectF(0.0f, 0.0f, 0.0f, 0.0f);
    private Paint whitePaint;

    public interface RotationWheelListener {
        void aspectRatioPressed();

        boolean mirror();

        void onChange(float f);

        void onEnd(float f);

        void onStart();

        boolean rotate90Pressed();
    }

    public CropRotationWheel(Context context) {
        super(context);
        Paint paint = new Paint();
        this.whitePaint = paint;
        paint.setStyle(Paint.Style.FILL);
        this.whitePaint.setColor(-1);
        this.whitePaint.setAlpha(255);
        this.whitePaint.setAntiAlias(true);
        Paint paint2 = new Paint();
        this.bluePaint = paint2;
        paint2.setStyle(Paint.Style.FILL);
        this.bluePaint.setColor(-11420173);
        this.bluePaint.setAlpha(255);
        this.bluePaint.setAntiAlias(true);
        ImageView imageView = new ImageView(context);
        this.mirrorButton = imageView;
        imageView.setImageResource(NUM);
        this.mirrorButton.setBackgroundDrawable(Theme.createSelectorDrawable(NUM));
        this.mirrorButton.setScaleType(ImageView.ScaleType.CENTER);
        this.mirrorButton.setOnClickListener(new CropRotationWheel$$ExternalSyntheticLambda0(this));
        this.mirrorButton.setOnLongClickListener(new CropRotationWheel$$ExternalSyntheticLambda3(this));
        this.mirrorButton.setContentDescription(LocaleController.getString("AccDescrMirror", NUM));
        addView(this.mirrorButton, LayoutHelper.createFrame(70, 64, 19));
        ImageView imageView2 = new ImageView(context);
        this.aspectRatioButton = imageView2;
        imageView2.setImageResource(NUM);
        this.aspectRatioButton.setBackgroundDrawable(Theme.createSelectorDrawable(NUM));
        this.aspectRatioButton.setScaleType(ImageView.ScaleType.CENTER);
        this.aspectRatioButton.setOnClickListener(new CropRotationWheel$$ExternalSyntheticLambda1(this));
        this.aspectRatioButton.setVisibility(8);
        this.aspectRatioButton.setContentDescription(LocaleController.getString("AccDescrAspectRatio", NUM));
        addView(this.aspectRatioButton, LayoutHelper.createFrame(70, 64, 19));
        ImageView imageView3 = new ImageView(context);
        this.rotation90Button = imageView3;
        imageView3.setImageResource(NUM);
        this.rotation90Button.setBackgroundDrawable(Theme.createSelectorDrawable(NUM));
        this.rotation90Button.setScaleType(ImageView.ScaleType.CENTER);
        this.rotation90Button.setOnClickListener(new CropRotationWheel$$ExternalSyntheticLambda2(this));
        this.rotation90Button.setContentDescription(LocaleController.getString("AccDescrRotate", NUM));
        addView(this.rotation90Button, LayoutHelper.createFrame(70, 64, 21));
        TextView textView = new TextView(context);
        this.degreesLabel = textView;
        textView.setTextColor(-1);
        addView(this.degreesLabel, LayoutHelper.createFrame(-2, -2, 49));
        setWillNotDraw(false);
        setRotation(0.0f, false);
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-Crop-CropRotationWheel  reason: not valid java name */
    public /* synthetic */ void m3930lambda$new$0$orgtelegramuiComponentsCropCropRotationWheel(View v) {
        RotationWheelListener rotationWheelListener = this.rotationListener;
        if (rotationWheelListener != null) {
            setMirrored(rotationWheelListener.mirror());
        }
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-Crop-CropRotationWheel  reason: not valid java name */
    public /* synthetic */ boolean m3931lambda$new$1$orgtelegramuiComponentsCropCropRotationWheel(View v) {
        this.aspectRatioButton.callOnClick();
        return true;
    }

    /* renamed from: lambda$new$2$org-telegram-ui-Components-Crop-CropRotationWheel  reason: not valid java name */
    public /* synthetic */ void m3932lambda$new$2$orgtelegramuiComponentsCropCropRotationWheel(View v) {
        RotationWheelListener rotationWheelListener = this.rotationListener;
        if (rotationWheelListener != null) {
            rotationWheelListener.aspectRatioPressed();
        }
    }

    /* renamed from: lambda$new$3$org-telegram-ui-Components-Crop-CropRotationWheel  reason: not valid java name */
    public /* synthetic */ void m3933lambda$new$3$orgtelegramuiComponentsCropCropRotationWheel(View v) {
        RotationWheelListener rotationWheelListener = this.rotationListener;
        if (rotationWheelListener != null) {
            setRotated(rotationWheelListener.rotate90Pressed());
        }
    }

    public void setFreeform(boolean freeform) {
    }

    public void setMirrored(boolean value) {
        this.mirrorButton.setColorFilter(value ? new PorterDuffColorFilter(Theme.getColor("dialogFloatingButton"), PorterDuff.Mode.MULTIPLY) : null);
    }

    public void setRotated(boolean value) {
        this.rotation90Button.setColorFilter(value ? new PorterDuffColorFilter(Theme.getColor("dialogFloatingButton"), PorterDuff.Mode.MULTIPLY) : null);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(Math.min(View.MeasureSpec.getSize(widthMeasureSpec), AndroidUtilities.dp(400.0f)), NUM), heightMeasureSpec);
    }

    public void reset(boolean resetMirror) {
        setRotation(0.0f, false);
        if (resetMirror) {
            setMirrored(false);
        }
        setRotated(false);
    }

    public void setListener(RotationWheelListener listener) {
        this.rotationListener = listener;
    }

    public void setRotation(float rotation2, boolean animated) {
        this.rotation = rotation2;
        float value = this.rotation;
        if (((double) Math.abs(value)) < 0.099d) {
            value = Math.abs(value);
        }
        this.degreesLabel.setText(String.format("%.1fº", new Object[]{Float.valueOf(value)}));
        invalidate();
    }

    public void setAspectLock(boolean enabled) {
        this.aspectRatioButton.setColorFilter(enabled ? new PorterDuffColorFilter(-11420173, PorterDuff.Mode.MULTIPLY) : null);
    }

    public boolean onTouchEvent(MotionEvent ev) {
        int action = ev.getActionMasked();
        float x = ev.getX();
        if (action == 0) {
            this.prevX = x;
            RotationWheelListener rotationWheelListener = this.rotationListener;
            if (rotationWheelListener != null) {
                rotationWheelListener.onStart();
            }
        } else if (action == 1 || action == 3) {
            RotationWheelListener rotationWheelListener2 = this.rotationListener;
            if (rotationWheelListener2 != null) {
                rotationWheelListener2.onEnd(this.rotation);
            }
            AndroidUtilities.makeAccessibilityAnnouncement(String.format("%.1f°", new Object[]{Float.valueOf(this.rotation)}));
        } else if (action == 2) {
            float f = this.rotation;
            double d = (double) ((this.prevX - x) / AndroidUtilities.density);
            Double.isNaN(d);
            float newAngle = Math.max(-45.0f, Math.min(45.0f, f + ((float) ((d / 3.141592653589793d) / 1.649999976158142d))));
            if (((double) Math.abs(newAngle - this.rotation)) > 0.001d) {
                if (((double) Math.abs(newAngle)) < 0.05d) {
                    newAngle = 0.0f;
                }
                setRotation(newAngle, false);
                RotationWheelListener rotationWheelListener3 = this.rotationListener;
                if (rotationWheelListener3 != null) {
                    rotationWheelListener3.onChange(this.rotation);
                }
                this.prevX = x;
            }
        }
        return true;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        Paint paint;
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        float angle = (-this.rotation) * 2.0f;
        float delta = angle % 5.0f;
        int segments = (int) Math.floor((double) (angle / 5.0f));
        for (int i = 0; i < 16; i++) {
            Paint paint2 = this.whitePaint;
            int a = i;
            if (a < segments || (a == 0 && delta < 0.0f)) {
                paint = this.bluePaint;
            } else {
                paint = paint2;
            }
            boolean z = false;
            int i2 = a;
            drawLine(canvas, a, delta, width, height, a == segments || (a == 0 && segments == -1), paint);
            if (i != 0) {
                int a2 = -i;
                Paint paint3 = a2 > segments ? this.bluePaint : this.whitePaint;
                if (a2 == segments + 1) {
                    z = true;
                }
                int i3 = a2;
                drawLine(canvas, a2, delta, width, height, z, paint3);
            }
        }
        this.bluePaint.setAlpha(255);
        this.tempRect.left = (float) ((width - AndroidUtilities.dp(2.5f)) / 2);
        this.tempRect.top = (float) ((height - AndroidUtilities.dp(22.0f)) / 2);
        this.tempRect.right = (float) ((AndroidUtilities.dp(2.5f) + width) / 2);
        this.tempRect.bottom = (float) ((AndroidUtilities.dp(22.0f) + height) / 2);
        canvas.drawRoundRect(this.tempRect, (float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(2.0f), this.bluePaint);
    }

    /* access modifiers changed from: protected */
    public void drawLine(Canvas canvas, int i, float delta, int width, int height, boolean center, Paint paint) {
        Paint paint2;
        int i2 = width;
        int radius = (int) ((((float) i2) / 2.0f) - ((float) AndroidUtilities.dp(70.0f)));
        double d = (double) radius;
        double cos = Math.cos(Math.toRadians((double) (90.0f - (((float) (i * 5)) + delta))));
        Double.isNaN(d);
        int val = (int) (d * cos);
        int x = (i2 / 2) + val;
        float f = ((float) Math.abs(val)) / ((float) radius);
        int alpha = Math.min(255, Math.max(0, (int) ((1.0f - (f * f)) * 255.0f)));
        if (center) {
            paint2 = this.bluePaint;
        } else {
            paint2 = paint;
        }
        paint2.setAlpha(alpha);
        int w = center ? 4 : 2;
        int h = AndroidUtilities.dp(center ? 16.0f : 12.0f);
        canvas.drawRect((float) (x - (w / 2)), (float) ((height - h) / 2), (float) ((w / 2) + x), (float) ((height + h) / 2), paint2);
    }
}
