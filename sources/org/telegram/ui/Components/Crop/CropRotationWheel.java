package org.telegram.ui.Components.Crop;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.beta.R;
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
    private ImageView rotation90Button;
    private RotationWheelListener rotationListener;
    private RectF tempRect = new RectF(0.0f, 0.0f, 0.0f, 0.0f);
    private Paint whitePaint = new Paint();

    /* renamed from: org.telegram.ui.Components.Crop.CropRotationWheel$1 */
    class C11151 implements OnClickListener {
        C11151() {
        }

        public void onClick(View v) {
            if (CropRotationWheel.this.rotationListener != null) {
                CropRotationWheel.this.rotationListener.aspectRatioPressed();
            }
        }
    }

    /* renamed from: org.telegram.ui.Components.Crop.CropRotationWheel$2 */
    class C11162 implements OnClickListener {
        C11162() {
        }

        public void onClick(View v) {
            if (CropRotationWheel.this.rotationListener != null) {
                CropRotationWheel.this.rotationListener.rotate90Pressed();
            }
        }
    }

    public interface RotationWheelListener {
        void aspectRatioPressed();

        void onChange(float f);

        void onEnd(float f);

        void onStart();

        void rotate90Pressed();
    }

    public CropRotationWheel(Context context) {
        super(context);
        this.whitePaint.setStyle(Style.FILL);
        this.whitePaint.setColor(-1);
        this.whitePaint.setAlpha(255);
        this.whitePaint.setAntiAlias(true);
        this.bluePaint = new Paint();
        this.bluePaint.setStyle(Style.FILL);
        this.bluePaint.setColor(-11420173);
        this.bluePaint.setAlpha(255);
        this.bluePaint.setAntiAlias(true);
        this.aspectRatioButton = new ImageView(context);
        this.aspectRatioButton.setImageResource(R.drawable.tool_cropfix);
        this.aspectRatioButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
        this.aspectRatioButton.setScaleType(ScaleType.CENTER);
        this.aspectRatioButton.setOnClickListener(new C11151());
        addView(this.aspectRatioButton, LayoutHelper.createFrame(70, 64, 19));
        this.rotation90Button = new ImageView(context);
        this.rotation90Button.setImageResource(R.drawable.tool_rotate);
        this.rotation90Button.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
        this.rotation90Button.setScaleType(ScaleType.CENTER);
        this.rotation90Button.setOnClickListener(new C11162());
        addView(this.rotation90Button, LayoutHelper.createFrame(70, 64, 21));
        this.degreesLabel = new TextView(context);
        this.degreesLabel.setTextColor(-1);
        addView(this.degreesLabel, LayoutHelper.createFrame(-2, -2, 49));
        setWillNotDraw(false);
        setRotation(0.0f, false);
    }

    public void setFreeform(boolean freeform) {
        this.aspectRatioButton.setVisibility(freeform ? 0 : 8);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(Math.min(MeasureSpec.getSize(widthMeasureSpec), AndroidUtilities.dp(400.0f)), NUM), heightMeasureSpec);
    }

    public void reset() {
        setRotation(0.0f, false);
    }

    public void setListener(RotationWheelListener listener) {
        this.rotationListener = listener;
    }

    public void setRotation(float rotation, boolean animated) {
        this.rotation = rotation;
        float value = this.rotation;
        if (((double) Math.abs(value)) < 0.099d) {
            value = Math.abs(value);
        }
        this.degreesLabel.setText(String.format("%.1f\u00ba", new Object[]{Float.valueOf(value)}));
        invalidate();
    }

    public void setAspectLock(boolean enabled) {
        this.aspectRatioButton.setColorFilter(enabled ? new PorterDuffColorFilter(-11420173, Mode.MULTIPLY) : null);
    }

    public boolean onTouchEvent(MotionEvent ev) {
        int action = ev.getActionMasked();
        float x = ev.getX();
        if (action == 0) {
            this.prevX = x;
            if (this.rotationListener != null) {
                this.rotationListener.onStart();
            }
        } else {
            if (action != 1) {
                if (action != 3) {
                    if (action == 2) {
                        float newAngle = Math.max(-45.0f, Math.min(45.0f, this.rotation + ((float) ((((double) ((this.prevX - x) / AndroidUtilities.density)) / 3.141592653589793d) / 1.649999976158142d))));
                        if (((double) Math.abs(newAngle - this.rotation)) > 0.001d) {
                            if (((double) Math.abs(newAngle)) < 0.05d) {
                                newAngle = 0.0f;
                            }
                            setRotation(newAngle, false);
                            if (this.rotationListener != null) {
                                this.rotationListener.onChange(this.rotation);
                            }
                            this.prevX = x;
                        }
                    }
                }
            }
            if (this.rotationListener != null) {
                this.rotationListener.onEnd(this.rotation);
            }
        }
        return true;
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        float angle = (-this.rotation) * 2.0f;
        float delta = angle % 5.0f;
        int segments = (int) Math.floor((double) (angle / 5.0f));
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 < 16) {
                Paint paint;
                int i3;
                boolean z;
                int i4;
                Paint paint2;
                Paint paint3 = r8.whitePaint;
                int a = i2;
                if (a >= segments) {
                    if (a == 0 && delta < 0.0f) {
                    }
                    paint = paint3;
                    i3 = 1;
                    if (a != segments) {
                        if (a == 0 || segments != -1) {
                            z = false;
                            i4 = i2;
                            drawLine(canvas, a, delta, width, height, z, paint);
                            if (i4 == 0) {
                                a = -i4;
                                paint2 = a <= segments ? r8.bluePaint : r8.whitePaint;
                                if (a != segments + 1) {
                                    i3 = 0;
                                }
                                drawLine(canvas, a, delta, width, height, i3, paint2);
                            }
                            i = i4 + 1;
                        }
                    }
                    z = true;
                    i4 = i2;
                    drawLine(canvas, a, delta, width, height, z, paint);
                    if (i4 == 0) {
                        a = -i4;
                        if (a <= segments) {
                        }
                        paint2 = a <= segments ? r8.bluePaint : r8.whitePaint;
                        if (a != segments + 1) {
                            i3 = 0;
                        }
                        drawLine(canvas, a, delta, width, height, i3, paint2);
                    }
                    i = i4 + 1;
                }
                paint3 = r8.bluePaint;
                paint = paint3;
                i3 = 1;
                if (a != segments) {
                    if (a == 0) {
                    }
                    z = false;
                    i4 = i2;
                    drawLine(canvas, a, delta, width, height, z, paint);
                    if (i4 == 0) {
                        a = -i4;
                        if (a <= segments) {
                        }
                        paint2 = a <= segments ? r8.bluePaint : r8.whitePaint;
                        if (a != segments + 1) {
                            i3 = 0;
                        }
                        drawLine(canvas, a, delta, width, height, i3, paint2);
                    }
                    i = i4 + 1;
                }
                z = true;
                i4 = i2;
                drawLine(canvas, a, delta, width, height, z, paint);
                if (i4 == 0) {
                    a = -i4;
                    if (a <= segments) {
                    }
                    paint2 = a <= segments ? r8.bluePaint : r8.whitePaint;
                    if (a != segments + 1) {
                        i3 = 0;
                    }
                    drawLine(canvas, a, delta, width, height, i3, paint2);
                }
                i = i4 + 1;
            } else {
                r8.bluePaint.setAlpha(255);
                r8.tempRect.left = (float) ((width - AndroidUtilities.dp(2.5f)) / 2);
                r8.tempRect.top = (float) ((height - AndroidUtilities.dp(22.0f)) / 2);
                r8.tempRect.right = (float) ((AndroidUtilities.dp(2.5f) + width) / 2);
                r8.tempRect.bottom = (float) ((AndroidUtilities.dp(22.0f) + height) / 2);
                canvas.drawRoundRect(r8.tempRect, (float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(2.0f), r8.bluePaint);
                return;
            }
        }
    }

    protected void drawLine(Canvas canvas, int i, float delta, int width, int height, boolean center, Paint paint) {
        Paint paint2;
        int i2 = width;
        int radius = (int) ((((float) i2) / 2.0f) - ((float) AndroidUtilities.dp(70.0f)));
        int val = (int) (((double) radius) * Math.cos(Math.toRadians((double) (90.0f - (((float) (i * 5)) + delta)))));
        int x = (i2 / 2) + val;
        float f = ((float) Math.abs(val)) / ((float) radius);
        int alpha = Math.min(255, Math.max(0, (int) ((1.0f - (f * f)) * 255.0f)));
        if (center) {
            paint2 = this.bluePaint;
        } else {
            CropRotationWheel cropRotationWheel = this;
            paint2 = paint;
        }
        paint2.setAlpha(alpha);
        int w = center ? 4 : 2;
        int h = AndroidUtilities.dp(center ? 16.0f : 12.0f);
        canvas.drawRect((float) (x - (w / 2)), (float) ((height - h) / 2), (float) (x + (w / 2)), (float) ((height + h) / 2), paint2);
    }
}
