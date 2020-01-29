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
    private float prevX;
    protected float rotation;
    private RotationWheelListener rotationListener;
    private RectF tempRect = new RectF(0.0f, 0.0f, 0.0f, 0.0f);
    private Paint whitePaint = new Paint();

    public interface RotationWheelListener {
        void aspectRatioPressed();

        void onChange(float f);

        void onEnd(float f);

        void onStart();

        void rotate90Pressed();
    }

    public CropRotationWheel(Context context) {
        super(context);
        this.whitePaint.setStyle(Paint.Style.FILL);
        this.whitePaint.setColor(-1);
        this.whitePaint.setAlpha(255);
        this.whitePaint.setAntiAlias(true);
        this.bluePaint = new Paint();
        this.bluePaint.setStyle(Paint.Style.FILL);
        this.bluePaint.setColor(-11420173);
        this.bluePaint.setAlpha(255);
        this.bluePaint.setAntiAlias(true);
        this.aspectRatioButton = new ImageView(context);
        this.aspectRatioButton.setImageResource(NUM);
        this.aspectRatioButton.setBackgroundDrawable(Theme.createSelectorDrawable(NUM));
        this.aspectRatioButton.setScaleType(ImageView.ScaleType.CENTER);
        this.aspectRatioButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                CropRotationWheel.this.lambda$new$0$CropRotationWheel(view);
            }
        });
        this.aspectRatioButton.setContentDescription(LocaleController.getString("AccDescrAspectRatio", NUM));
        addView(this.aspectRatioButton, LayoutHelper.createFrame(70, 64, 19));
        ImageView imageView = new ImageView(context);
        imageView.setImageResource(NUM);
        imageView.setBackgroundDrawable(Theme.createSelectorDrawable(NUM));
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        imageView.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                CropRotationWheel.this.lambda$new$1$CropRotationWheel(view);
            }
        });
        imageView.setContentDescription(LocaleController.getString("AccDescrRotate", NUM));
        addView(imageView, LayoutHelper.createFrame(70, 64, 21));
        this.degreesLabel = new TextView(context);
        this.degreesLabel.setTextColor(-1);
        addView(this.degreesLabel, LayoutHelper.createFrame(-2, -2, 49));
        setWillNotDraw(false);
        setRotation(0.0f, false);
    }

    public /* synthetic */ void lambda$new$0$CropRotationWheel(View view) {
        RotationWheelListener rotationWheelListener = this.rotationListener;
        if (rotationWheelListener != null) {
            rotationWheelListener.aspectRatioPressed();
        }
    }

    public /* synthetic */ void lambda$new$1$CropRotationWheel(View view) {
        RotationWheelListener rotationWheelListener = this.rotationListener;
        if (rotationWheelListener != null) {
            rotationWheelListener.rotate90Pressed();
        }
    }

    public void setFreeform(boolean z) {
        this.aspectRatioButton.setVisibility(z ? 0 : 8);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(Math.min(View.MeasureSpec.getSize(i), AndroidUtilities.dp(400.0f)), NUM), i2);
    }

    public void reset() {
        setRotation(0.0f, false);
    }

    public void setListener(RotationWheelListener rotationWheelListener) {
        this.rotationListener = rotationWheelListener;
    }

    public void setRotation(float f, boolean z) {
        this.rotation = f;
        float f2 = this.rotation;
        if (((double) Math.abs(f2)) < 0.099d) {
            f2 = Math.abs(f2);
        }
        this.degreesLabel.setText(String.format("%.1fº", new Object[]{Float.valueOf(f2)}));
        invalidate();
    }

    public void setAspectLock(boolean z) {
        this.aspectRatioButton.setColorFilter(z ? new PorterDuffColorFilter(-11420173, PorterDuff.Mode.MULTIPLY) : null);
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        int actionMasked = motionEvent.getActionMasked();
        float x = motionEvent.getX();
        if (actionMasked == 0) {
            this.prevX = x;
            RotationWheelListener rotationWheelListener = this.rotationListener;
            if (rotationWheelListener != null) {
                rotationWheelListener.onStart();
            }
        } else if (actionMasked == 1 || actionMasked == 3) {
            RotationWheelListener rotationWheelListener2 = this.rotationListener;
            if (rotationWheelListener2 != null) {
                rotationWheelListener2.onEnd(this.rotation);
            }
            AndroidUtilities.makeAccessibilityAnnouncement(String.format("%.1f°", new Object[]{Float.valueOf(this.rotation)}));
        } else if (actionMasked == 2) {
            float f = this.rotation;
            double d = (double) ((this.prevX - x) / AndroidUtilities.density);
            Double.isNaN(d);
            float max = Math.max(-45.0f, Math.min(45.0f, f + ((float) ((d / 3.141592653589793d) / 1.649999976158142d))));
            if (((double) Math.abs(max - this.rotation)) > 0.001d) {
                if (((double) Math.abs(max)) < 0.05d) {
                    max = 0.0f;
                }
                setRotation(max, false);
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
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        float f = (-this.rotation) * 2.0f;
        float f2 = f % 5.0f;
        int floor = (int) Math.floor((double) (f / 5.0f));
        int i = 0;
        while (i < 16) {
            Paint paint = this.whitePaint;
            if (i < floor || (i == 0 && f2 < 0.0f)) {
                paint = this.bluePaint;
            }
            drawLine(canvas, i, f2, width, height, i == floor || (i == 0 && floor == -1), paint);
            if (i != 0) {
                int i2 = -i;
                drawLine(canvas, i2, f2, width, height, i2 == floor + 1, i2 > floor ? this.bluePaint : this.whitePaint);
            }
            i++;
        }
        this.bluePaint.setAlpha(255);
        this.tempRect.left = (float) ((width - AndroidUtilities.dp(2.5f)) / 2);
        this.tempRect.top = (float) ((height - AndroidUtilities.dp(22.0f)) / 2);
        this.tempRect.right = (float) ((width + AndroidUtilities.dp(2.5f)) / 2);
        this.tempRect.bottom = (float) ((height + AndroidUtilities.dp(22.0f)) / 2);
        canvas.drawRoundRect(this.tempRect, (float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(2.0f), this.bluePaint);
    }

    /* access modifiers changed from: protected */
    public void drawLine(Canvas canvas, int i, float f, int i2, int i3, boolean z, Paint paint) {
        int dp = (int) ((((float) i2) / 2.0f) - ((float) AndroidUtilities.dp(70.0f)));
        double d = (double) dp;
        double cos = Math.cos(Math.toRadians((double) (90.0f - (((float) (i * 5)) + f))));
        Double.isNaN(d);
        int i4 = (int) (d * cos);
        int i5 = (i2 / 2) + i4;
        float abs = ((float) Math.abs(i4)) / ((float) dp);
        int min = Math.min(255, Math.max(0, (int) ((1.0f - (abs * abs)) * 255.0f)));
        if (z) {
            paint = this.bluePaint;
        }
        Paint paint2 = paint;
        paint2.setAlpha(min);
        int i6 = z ? 4 : 2;
        int dp2 = AndroidUtilities.dp(z ? 16.0f : 12.0f);
        int i7 = i6 / 2;
        canvas.drawRect((float) (i5 - i7), (float) ((i3 - dp2) / 2), (float) (i5 + i7), (float) ((i3 + dp2) / 2), paint2);
    }
}
