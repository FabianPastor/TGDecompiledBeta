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
import org.telegram.messenger.C0446R;
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
    class C11211 implements OnClickListener {
        C11211() {
        }

        public void onClick(View view) {
            if (CropRotationWheel.this.rotationListener != null) {
                CropRotationWheel.this.rotationListener.aspectRatioPressed();
            }
        }
    }

    /* renamed from: org.telegram.ui.Components.Crop.CropRotationWheel$2 */
    class C11222 implements OnClickListener {
        C11222() {
        }

        public void onClick(View view) {
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
        this.aspectRatioButton.setImageResource(C0446R.drawable.tool_cropfix);
        this.aspectRatioButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
        this.aspectRatioButton.setScaleType(ScaleType.CENTER);
        this.aspectRatioButton.setOnClickListener(new C11211());
        addView(this.aspectRatioButton, LayoutHelper.createFrame(70, 64, 19));
        this.rotation90Button = new ImageView(context);
        this.rotation90Button.setImageResource(C0446R.drawable.tool_rotate);
        this.rotation90Button.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
        this.rotation90Button.setScaleType(ScaleType.CENTER);
        this.rotation90Button.setOnClickListener(new C11222());
        addView(this.rotation90Button, LayoutHelper.createFrame(70, 64, 21));
        this.degreesLabel = new TextView(context);
        this.degreesLabel.setTextColor(-1);
        addView(this.degreesLabel, LayoutHelper.createFrame(-2, -2, 49));
        setWillNotDraw(false);
        setRotation(0.0f, false);
    }

    public void setFreeform(boolean z) {
        this.aspectRatioButton.setVisibility(z ? false : true);
    }

    protected void onMeasure(int i, int i2) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(Math.min(MeasureSpec.getSize(i), AndroidUtilities.dp(400.0f)), NUM), i2);
    }

    public void reset() {
        setRotation(0.0f, false);
    }

    public void setListener(RotationWheelListener rotationWheelListener) {
        this.rotationListener = rotationWheelListener;
    }

    public void setRotation(float f, boolean z) {
        this.rotation = f;
        f = this.rotation;
        if (((double) Math.abs(f)) < 0.099d) {
            f = Math.abs(f);
        }
        this.degreesLabel.setText(String.format("%.1f\u00ba", new Object[]{Float.valueOf(f)}));
        invalidate();
    }

    public void setAspectLock(boolean z) {
        this.aspectRatioButton.setColorFilter(z ? new PorterDuffColorFilter(-11420173, Mode.MULTIPLY) : false);
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        int actionMasked = motionEvent.getActionMasked();
        motionEvent = motionEvent.getX();
        if (actionMasked == 0) {
            this.prevX = motionEvent;
            if (this.rotationListener != null) {
                this.rotationListener.onStart();
            }
        } else {
            if (actionMasked != 1) {
                if (actionMasked != 3) {
                    if (actionMasked == 2) {
                        float max = Math.max(-45.0f, Math.min(45.0f, this.rotation + ((float) ((((double) ((this.prevX - motionEvent) / AndroidUtilities.density)) / 3.141592653589793d) / 1.649999976158142d))));
                        if (((double) Math.abs(max - this.rotation)) > 0.001d) {
                            if (((double) Math.abs(max)) < 0.05d) {
                                max = 0.0f;
                            }
                            setRotation(max, false);
                            if (this.rotationListener != null) {
                                this.rotationListener.onChange(this.rotation);
                            }
                            this.prevX = motionEvent;
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
        float f = (-this.rotation) * 2.0f;
        float f2 = f % 5.0f;
        int floor = (int) Math.floor((double) (f / 5.0f));
        for (int i = 0; i < 16; i++) {
            Paint paint;
            boolean z;
            int i2;
            Paint paint2 = r8.whitePaint;
            if (i >= floor) {
                if (i == 0 && f2 < 0.0f) {
                }
                paint = paint2;
                if (i != floor) {
                    if (i == 0 || floor != -1) {
                        z = false;
                        drawLine(canvas, i, f2, width, height, z, paint);
                        if (i == 0) {
                            i2 = -i;
                            drawLine(canvas, i2, f2, width, height, i2 != floor + 1, i2 <= floor ? r8.bluePaint : r8.whitePaint);
                        }
                    }
                }
                z = true;
                drawLine(canvas, i, f2, width, height, z, paint);
                if (i == 0) {
                    i2 = -i;
                    if (i2 <= floor) {
                    }
                    if (i2 != floor + 1) {
                    }
                    drawLine(canvas, i2, f2, width, height, i2 != floor + 1, i2 <= floor ? r8.bluePaint : r8.whitePaint);
                }
            }
            paint2 = r8.bluePaint;
            paint = paint2;
            if (i != floor) {
                if (i == 0) {
                }
                z = false;
                drawLine(canvas, i, f2, width, height, z, paint);
                if (i == 0) {
                    i2 = -i;
                    if (i2 <= floor) {
                    }
                    if (i2 != floor + 1) {
                    }
                    drawLine(canvas, i2, f2, width, height, i2 != floor + 1, i2 <= floor ? r8.bluePaint : r8.whitePaint);
                }
            }
            z = true;
            drawLine(canvas, i, f2, width, height, z, paint);
            if (i == 0) {
                i2 = -i;
                if (i2 <= floor) {
                }
                if (i2 != floor + 1) {
                }
                drawLine(canvas, i2, f2, width, height, i2 != floor + 1, i2 <= floor ? r8.bluePaint : r8.whitePaint);
            }
        }
        r8.bluePaint.setAlpha(255);
        r8.tempRect.left = (float) ((width - AndroidUtilities.dp(2.5f)) / 2);
        r8.tempRect.top = (float) ((height - AndroidUtilities.dp(22.0f)) / 2);
        r8.tempRect.right = (float) ((width + AndroidUtilities.dp(2.5f)) / 2);
        r8.tempRect.bottom = (float) ((height + AndroidUtilities.dp(22.0f)) / 2);
        canvas.drawRoundRect(r8.tempRect, (float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(2.0f), r8.bluePaint);
    }

    protected void drawLine(Canvas canvas, int i, float f, int i2, int i3, boolean z, Paint paint) {
        int dp = (int) ((((float) i2) / 2.0f) - ((float) AndroidUtilities.dp(70.0f)));
        i = (int) (((double) dp) * Math.cos(Math.toRadians((double) (90.0f - (((float) (i * 5)) + f)))));
        i2 = (i2 / 2) + i;
        i = ((float) Math.abs(i)) / ((float) dp);
        i = Math.min(255, Math.max(0, (int) ((1.0f - (i * i)) * NUM)));
        if (z) {
            paint = this.bluePaint;
        }
        Paint paint2 = paint;
        paint2.setAlpha(i);
        i = z ? 4 : 2;
        if (z) {
            z = true;
        } else {
            z = true;
        }
        z = AndroidUtilities.dp(z);
        i /= 2;
        canvas.drawRect((float) (i2 - i), (float) ((i3 - z) / 2), (float) (i2 + i), (float) ((i3 + z) / 2), paint2);
    }
}
