package org.telegram.ui.Components;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.SweepGradient;
import android.support.v4.view.ViewCompat;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.beta.R;

public class ColorPickerDialog extends AlertDialog {

    private class ColorPicker extends View {
        private int arrowPointerSize;
        private float[] colorHSV = new float[]{0.0f, 0.0f, 1.0f};
        private RectF colorPointerCoords;
        private Paint colorPointerPaint;
        private Paint colorViewPaint;
        private Bitmap colorWheelBitmap;
        private Paint colorWheelPaint;
        private int colorWheelRadius;
        private int innerPadding;
        private int innerWheelRadius;
        private int outerPadding;
        private final int paramArrowPointerSize = 4;
        private final int paramInnerPadding = 5;
        private final int paramOuterPadding = 2;
        private final int paramValueSliderWidth = 10;
        private Paint valuePointerArrowPaint;
        private Paint valuePointerPaint;
        private Paint valueSliderPaint;
        private int valueSliderWidth;

        public ColorPicker(Context context) {
            super(context);
            init();
        }

        private void init() {
            this.colorPointerPaint = new Paint();
            this.colorPointerPaint.setStyle(Style.STROKE);
            this.colorPointerPaint.setStrokeWidth(2.0f);
            this.colorPointerPaint.setARGB(128, 0, 0, 0);
            this.valuePointerPaint = new Paint();
            this.valuePointerPaint.setStyle(Style.STROKE);
            this.valuePointerPaint.setStrokeWidth(2.0f);
            this.valuePointerArrowPaint = new Paint();
            this.colorWheelPaint = new Paint();
            this.colorWheelPaint.setAntiAlias(true);
            this.colorWheelPaint.setDither(true);
            this.valueSliderPaint = new Paint();
            this.valueSliderPaint.setAntiAlias(true);
            this.valueSliderPaint.setDither(true);
            this.colorViewPaint = new Paint();
            this.colorViewPaint.setAntiAlias(true);
            this.colorPointerCoords = new RectF();
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int size = Math.min(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
            setMeasuredDimension(size, size);
        }

        protected void onDraw(Canvas canvas) {
            int centerX = getWidth() / 2;
            int centerY = getHeight() / 2;
            canvas.drawBitmap(this.colorWheelBitmap, (float) (centerX - this.colorWheelRadius), (float) (centerY - this.colorWheelRadius), null);
            float hueAngle = (float) Math.toRadians((double) this.colorHSV[0]);
            float pointerRadius = 0.075f * ((float) this.colorWheelRadius);
            int pointerX = (int) (((float) (((int) (((-Math.cos((double) hueAngle)) * ((double) this.colorHSV[1])) * ((double) this.colorWheelRadius))) + centerX)) - (pointerRadius / 2.0f));
            int pointerY = (int) (((float) (((int) (((-Math.sin((double) hueAngle)) * ((double) this.colorHSV[1])) * ((double) this.colorWheelRadius))) + centerY)) - (pointerRadius / 2.0f));
            this.colorPointerCoords.set((float) pointerX, (float) pointerY, ((float) pointerX) + pointerRadius, ((float) pointerY) + pointerRadius);
            canvas.drawOval(this.colorPointerCoords, this.colorPointerPaint);
            float[] hsv = new float[]{this.colorHSV[0], this.colorHSV[1], 1.0f};
            int y = (this.colorWheelRadius + centerY) + AndroidUtilities.dp(10.0f);
            this.valueSliderPaint.setShader(new LinearGradient((float) 0, (float) y, (float) (0 + getMeasuredWidth()), (float) (y + AndroidUtilities.dp(10.0f)), new int[]{-16777216, Color.HSVToColor(hsv), -1}, null, TileMode.CLAMP));
            canvas.drawRect((float) 0, (float) y, (float) (getMeasuredWidth() + 0), (float) (AndroidUtilities.dp(10.0f) + y), this.valueSliderPaint);
        }

        protected void onSizeChanged(int width, int height, int oldw, int oldh) {
            int centerX = width / 2;
            int centerY = height / 2;
            this.innerPadding = (width * 5) / 100;
            this.outerPadding = (width * 2) / 100;
            this.arrowPointerSize = (width * 4) / 100;
            this.valueSliderWidth = (width * 10) / 100;
            this.innerWheelRadius = (((width / 2) - this.outerPadding) - this.arrowPointerSize) - this.valueSliderWidth;
            this.colorWheelRadius = this.innerWheelRadius - this.innerPadding;
            this.colorWheelBitmap = createColorWheelBitmap(this.colorWheelRadius * 2, this.colorWheelRadius * 2);
        }

        private Bitmap createColorWheelBitmap(int width, int height) {
            Bitmap bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
            int[] colors = new int[13];
            float[] hsv = new float[]{0.0f, 1.0f, 1.0f};
            for (int i = 0; i < colors.length; i++) {
                hsv[0] = (float) (((i * 30) + 180) % 360);
                colors[i] = Color.HSVToColor(hsv);
            }
            colors[12] = colors[0];
            this.colorWheelPaint.setShader(new ComposeShader(new SweepGradient((float) (width / 2), (float) (height / 2), colors, null), new RadialGradient((float) (width / 2), (float) (height / 2), (float) this.colorWheelRadius, -1, ViewCompat.MEASURED_SIZE_MASK, TileMode.CLAMP), Mode.SRC_OVER));
            new Canvas(bitmap).drawCircle((float) (width / 2), (float) (height / 2), (float) this.colorWheelRadius, this.colorWheelPaint);
            return bitmap;
        }

        public boolean onTouchEvent(MotionEvent event) {
            switch (event.getAction()) {
                case 0:
                case 2:
                    int x = (int) event.getX();
                    int cx = x - (getWidth() / 2);
                    int cy = ((int) event.getY()) - (getHeight() / 2);
                    double d = Math.sqrt((double) ((cx * cx) + (cy * cy)));
                    if (d <= ((double) this.colorWheelRadius)) {
                        this.colorHSV[0] = (float) (Math.toDegrees(Math.atan2((double) cy, (double) cx)) + 180.0d);
                        this.colorHSV[1] = Math.max(0.0f, Math.min(1.0f, (float) (d / ((double) this.colorWheelRadius))));
                        invalidate();
                    } else if (x >= getWidth() / 2 && d >= ((double) this.innerWheelRadius)) {
                        this.colorHSV[2] = (float) Math.max(0.0d, Math.min(1.0d, (Math.atan2((double) cy, (double) cx) / 3.141592653589793d) + 0.5d));
                        invalidate();
                    }
                    return true;
                default:
                    return super.onTouchEvent(event);
            }
        }

        public void setColor(int color) {
            Color.colorToHSV(color, this.colorHSV);
        }

        public int getColor() {
            return Color.HSVToColor(this.colorHSV);
        }
    }

    public ColorPickerDialog(Context context) {
        super(context);
        setView(new ColorPicker(context));
        setButton(-1, LocaleController.getString("Set", R.string.Set), new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        setButton(-2, LocaleController.getString("Cancel", R.string.Cancel), new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
    }
}
