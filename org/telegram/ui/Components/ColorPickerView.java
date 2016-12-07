package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.internal.view.SupportMenu;
import android.support.v4.view.InputDeviceCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.exoplayer.C;
import org.telegram.messenger.volley.DefaultRetryPolicy;

public class ColorPickerView extends View {
    private static final int[] COLORS = new int[]{SupportMenu.CATEGORY_MASK, -65281, -16776961, -16711681, -16711936, -1, InputDeviceCompat.SOURCE_ANY, SupportMenu.CATEGORY_MASK};
    private static final String STATE_ANGLE = "angle";
    private static final String STATE_OLD_COLOR = "color";
    private static final String STATE_PARENT = "parent";
    private static final String STATE_SHOW_OLD_COLOR = "showColor";
    private float mAngle;
    private Paint mCenterHaloPaint;
    private int mCenterNewColor;
    private Paint mCenterNewPaint;
    private int mCenterOldColor;
    private Paint mCenterOldPaint;
    private RectF mCenterRectangle;
    private int mColorCenterHaloRadius;
    private int mColorCenterRadius;
    private int mColorPointerHaloRadius;
    private int mColorPointerRadius;
    private Paint mColorWheelPaint;
    private int mColorWheelRadius;
    private RectF mColorWheelRectangle;
    private int mColorWheelThickness;
    private float[] mHSV;
    private Paint mPointerColor;
    private Paint mPointerHaloPaint;
    private int mPreferredColorCenterHaloRadius;
    private int mPreferredColorCenterRadius;
    private int mPreferredColorWheelRadius;
    private boolean mShowCenterOldColor;
    private float mSlopX;
    private float mSlopY;
    private float mTranslationOffset;
    private boolean mUserIsMovingPointer;
    private int oldChangedListenerColor;
    private int oldSelectedListenerColor;
    private OnColorChangedListener onColorChangedListener;
    private OnColorSelectedListener onColorSelectedListener;

    public interface OnColorChangedListener {
        void onColorChanged(int i);
    }

    public interface OnColorSelectedListener {
        void onColorSelected(int i);
    }

    public ColorPickerView(Context context) {
        super(context);
        this.mColorWheelRectangle = new RectF();
        this.mCenterRectangle = new RectF();
        this.mUserIsMovingPointer = false;
        this.mHSV = new float[3];
        init(null, 0);
    }

    public ColorPickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mColorWheelRectangle = new RectF();
        this.mCenterRectangle = new RectF();
        this.mUserIsMovingPointer = false;
        this.mHSV = new float[3];
        init(attrs, 0);
    }

    public ColorPickerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mColorWheelRectangle = new RectF();
        this.mCenterRectangle = new RectF();
        this.mUserIsMovingPointer = false;
        this.mHSV = new float[3];
        init(attrs, defStyle);
    }

    public void setOnColorChangedListener(OnColorChangedListener listener) {
        this.onColorChangedListener = listener;
    }

    public void setOnColorSelectedListener(OnColorSelectedListener listener) {
        this.onColorSelectedListener = listener;
    }

    private void init(AttributeSet attrs, int defStyle) {
        this.mColorWheelThickness = AndroidUtilities.dp(8.0f);
        this.mColorWheelRadius = AndroidUtilities.dp(124.0f);
        this.mPreferredColorWheelRadius = this.mColorWheelRadius;
        this.mColorCenterRadius = AndroidUtilities.dp(54.0f);
        this.mPreferredColorCenterRadius = this.mColorCenterRadius;
        this.mColorCenterHaloRadius = AndroidUtilities.dp(BitmapDescriptorFactory.HUE_YELLOW);
        this.mPreferredColorCenterHaloRadius = this.mColorCenterHaloRadius;
        this.mColorPointerRadius = AndroidUtilities.dp(14.0f);
        this.mColorPointerHaloRadius = AndroidUtilities.dp(18.0f);
        this.mAngle = -1.5707964f;
        Shader s = new SweepGradient(0.0f, 0.0f, COLORS, null);
        this.mColorWheelPaint = new Paint(1);
        this.mColorWheelPaint.setShader(s);
        this.mColorWheelPaint.setStyle(Style.STROKE);
        this.mColorWheelPaint.setStrokeWidth((float) this.mColorWheelThickness);
        this.mPointerHaloPaint = new Paint(1);
        this.mPointerHaloPaint.setColor(-16777216);
        this.mPointerHaloPaint.setAlpha(80);
        this.mPointerColor = new Paint(1);
        this.mPointerColor.setColor(calculateColor(this.mAngle));
        this.mCenterNewPaint = new Paint(1);
        this.mCenterNewPaint.setColor(calculateColor(this.mAngle));
        this.mCenterNewPaint.setStyle(Style.FILL);
        this.mCenterOldPaint = new Paint(1);
        this.mCenterOldPaint.setColor(calculateColor(this.mAngle));
        this.mCenterOldPaint.setStyle(Style.FILL);
        this.mCenterHaloPaint = new Paint(1);
        this.mCenterHaloPaint.setColor(-16777216);
        this.mCenterHaloPaint.setAlpha(0);
        this.mCenterNewColor = calculateColor(this.mAngle);
        this.mCenterOldColor = calculateColor(this.mAngle);
        this.mShowCenterOldColor = true;
    }

    protected void onDraw(Canvas canvas) {
        canvas.translate(this.mTranslationOffset, this.mTranslationOffset);
        canvas.drawOval(this.mColorWheelRectangle, this.mColorWheelPaint);
        float[] pointerPosition = calculatePointerPosition(this.mAngle);
        canvas.drawCircle(pointerPosition[0], pointerPosition[1], (float) this.mColorPointerHaloRadius, this.mPointerHaloPaint);
        canvas.drawCircle(pointerPosition[0], pointerPosition[1], (float) this.mColorPointerRadius, this.mPointerColor);
        canvas.drawCircle(0.0f, 0.0f, (float) this.mColorCenterHaloRadius, this.mCenterHaloPaint);
        if (this.mShowCenterOldColor) {
            canvas.drawArc(this.mCenterRectangle, 90.0f, BitmapDescriptorFactory.HUE_CYAN, true, this.mCenterOldPaint);
            canvas.drawArc(this.mCenterRectangle, BitmapDescriptorFactory.HUE_VIOLET, BitmapDescriptorFactory.HUE_CYAN, true, this.mCenterNewPaint);
            return;
        }
        canvas.drawArc(this.mCenterRectangle, 0.0f, 360.0f, true, this.mCenterNewPaint);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width;
        int height;
        int intrinsicSize = (this.mPreferredColorWheelRadius + this.mColorPointerHaloRadius) * 2;
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if (widthMode == C.ENCODING_PCM_32BIT) {
            width = widthSize;
        } else if (widthMode == Integer.MIN_VALUE) {
            width = Math.min(intrinsicSize, widthSize);
        } else {
            width = intrinsicSize;
        }
        if (heightMode == C.ENCODING_PCM_32BIT) {
            height = heightSize;
        } else if (heightMode == Integer.MIN_VALUE) {
            height = Math.min(intrinsicSize, heightSize);
        } else {
            height = intrinsicSize;
        }
        int min = Math.min(width, height);
        setMeasuredDimension(min, min);
        this.mTranslationOffset = ((float) min) * 0.5f;
        this.mColorWheelRadius = ((min / 2) - this.mColorWheelThickness) - this.mColorPointerHaloRadius;
        this.mColorWheelRectangle.set((float) (-this.mColorWheelRadius), (float) (-this.mColorWheelRadius), (float) this.mColorWheelRadius, (float) this.mColorWheelRadius);
        this.mColorCenterRadius = (int) (((float) this.mPreferredColorCenterRadius) * (((float) this.mColorWheelRadius) / ((float) this.mPreferredColorWheelRadius)));
        this.mColorCenterHaloRadius = (int) (((float) this.mPreferredColorCenterHaloRadius) * (((float) this.mColorWheelRadius) / ((float) this.mPreferredColorWheelRadius)));
        this.mCenterRectangle.set((float) (-this.mColorCenterRadius), (float) (-this.mColorCenterRadius), (float) this.mColorCenterRadius, (float) this.mColorCenterRadius);
    }

    private int ave(int s, int d, float p) {
        return Math.round(((float) (d - s)) * p) + s;
    }

    private int calculateColor(float angle) {
        float unit = (float) (((double) angle) / 6.283185307179586d);
        if (unit < 0.0f) {
            unit += DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
        }
        if (unit <= 0.0f) {
            return COLORS[0];
        }
        if (unit >= DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
            return COLORS[COLORS.length - 1];
        }
        float p = unit * ((float) (COLORS.length - 1));
        int i = (int) p;
        p -= (float) i;
        int c0 = COLORS[i];
        int c1 = COLORS[i + 1];
        return Color.argb(ave(Color.alpha(c0), Color.alpha(c1), p), ave(Color.red(c0), Color.red(c1), p), ave(Color.green(c0), Color.green(c1), p), ave(Color.blue(c0), Color.blue(c1), p));
    }

    public int getColor() {
        return this.mCenterNewColor;
    }

    public void setColor(int color) {
        this.mAngle = colorToAngle(color);
        this.mPointerColor.setColor(calculateColor(this.mAngle));
        this.mCenterNewPaint.setColor(calculateColor(this.mAngle));
        invalidate();
    }

    private float colorToAngle(int color) {
        float[] colors = new float[3];
        Color.colorToHSV(color, colors);
        return (float) Math.toRadians((double) (-colors[0]));
    }

    public boolean onTouchEvent(MotionEvent event) {
        getParent().requestDisallowInterceptTouchEvent(true);
        float x = event.getX() - this.mTranslationOffset;
        float y = event.getY() - this.mTranslationOffset;
        switch (event.getAction()) {
            case 0:
                float[] pointerPosition = calculatePointerPosition(this.mAngle);
                if (x < pointerPosition[0] - ((float) this.mColorPointerHaloRadius) || x > pointerPosition[0] + ((float) this.mColorPointerHaloRadius) || y < pointerPosition[1] - ((float) this.mColorPointerHaloRadius) || y > pointerPosition[1] + ((float) this.mColorPointerHaloRadius)) {
                    if (x >= ((float) (-this.mColorCenterRadius)) && x <= ((float) this.mColorCenterRadius) && y >= ((float) (-this.mColorCenterRadius)) && y <= ((float) this.mColorCenterRadius) && this.mShowCenterOldColor) {
                        this.mCenterHaloPaint.setAlpha(80);
                        setColor(getOldCenterColor());
                        invalidate();
                        break;
                    }
                    getParent().requestDisallowInterceptTouchEvent(false);
                    return false;
                }
                this.mSlopX = x - pointerPosition[0];
                this.mSlopY = y - pointerPosition[1];
                this.mUserIsMovingPointer = true;
                invalidate();
                break;
                break;
            case 1:
                this.mUserIsMovingPointer = false;
                this.mCenterHaloPaint.setAlpha(0);
                if (!(this.onColorSelectedListener == null || this.mCenterNewColor == this.oldSelectedListenerColor)) {
                    this.onColorSelectedListener.onColorSelected(this.mCenterNewColor);
                    this.oldSelectedListenerColor = this.mCenterNewColor;
                }
                invalidate();
                break;
            case 2:
                if (this.mUserIsMovingPointer) {
                    this.mAngle = (float) Math.atan2((double) (y - this.mSlopY), (double) (x - this.mSlopX));
                    this.mPointerColor.setColor(calculateColor(this.mAngle));
                    int calculateColor = calculateColor(this.mAngle);
                    this.mCenterNewColor = calculateColor;
                    setNewCenterColor(calculateColor);
                    invalidate();
                    break;
                }
                getParent().requestDisallowInterceptTouchEvent(false);
                return false;
            case 3:
                if (!(this.onColorSelectedListener == null || this.mCenterNewColor == this.oldSelectedListenerColor)) {
                    this.onColorSelectedListener.onColorSelected(this.mCenterNewColor);
                    this.oldSelectedListenerColor = this.mCenterNewColor;
                    break;
                }
        }
        return true;
    }

    private float[] calculatePointerPosition(float angle) {
        float x = (float) (((double) this.mColorWheelRadius) * Math.cos((double) angle));
        float y = (float) (((double) this.mColorWheelRadius) * Math.sin((double) angle));
        return new float[]{x, y};
    }

    public void setNewCenterColor(int color) {
        this.mCenterNewColor = color;
        this.mCenterNewPaint.setColor(color);
        if (this.mCenterOldColor == 0) {
            this.mCenterOldColor = color;
            this.mCenterOldPaint.setColor(color);
        }
        if (!(this.onColorChangedListener == null || color == this.oldChangedListenerColor)) {
            this.onColorChangedListener.onColorChanged(color);
            this.oldChangedListenerColor = color;
        }
        invalidate();
    }

    public void setOldCenterColor(int color) {
        this.mCenterOldColor = color;
        this.mCenterOldPaint.setColor(color);
        invalidate();
    }

    public int getOldCenterColor() {
        return this.mCenterOldColor;
    }

    public void setShowOldCenterColor(boolean show) {
        this.mShowCenterOldColor = show;
        invalidate();
    }

    public boolean getShowOldCenterColor() {
        return this.mShowCenterOldColor;
    }

    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        Bundle state = new Bundle();
        state.putParcelable(STATE_PARENT, superState);
        state.putFloat(STATE_ANGLE, this.mAngle);
        state.putInt("color", this.mCenterOldColor);
        state.putBoolean(STATE_SHOW_OLD_COLOR, this.mShowCenterOldColor);
        return state;
    }

    protected void onRestoreInstanceState(Parcelable state) {
        Bundle savedState = (Bundle) state;
        super.onRestoreInstanceState(savedState.getParcelable(STATE_PARENT));
        this.mAngle = savedState.getFloat(STATE_ANGLE);
        setOldCenterColor(savedState.getInt("color"));
        this.mShowCenterOldColor = savedState.getBoolean(STATE_SHOW_OLD_COLOR);
        int currentColor = calculateColor(this.mAngle);
        this.mPointerColor.setColor(currentColor);
        setNewCenterColor(currentColor);
    }
}
