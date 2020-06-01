package org.telegram.ui.Components.Paint.Views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.Components.EditTextBoldCursor;

public class EditTextOutline extends EditTextBoldCursor {
    private float[] lines;
    private Bitmap mCache;
    private Canvas mCanvas = new Canvas();
    private int mFrameColor;
    private int mStrokeColor = 0;
    private float mStrokeWidth;
    private boolean mUpdateCachedBitmap;
    private Paint paint = new Paint(1);
    private Path path = new Path();
    private RectF rect = new RectF();
    private TextPaint textPaint = new TextPaint(1);

    public EditTextOutline(Context context) {
        super(context);
        setInputType(getInputType() | 131072 | 524288);
        this.mUpdateCachedBitmap = true;
        this.textPaint.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    /* access modifiers changed from: protected */
    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        super.onTextChanged(charSequence, i, i2, i3);
        this.mUpdateCachedBitmap = true;
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        if (i <= 0 || i2 <= 0) {
            this.mCache = null;
            return;
        }
        this.mUpdateCachedBitmap = true;
        Bitmap bitmap = this.mCache;
        if (bitmap != null) {
            bitmap.recycle();
        }
        this.mCache = Bitmap.createBitmap(i, i2, Bitmap.Config.ARGB_8888);
    }

    public void setStrokeColor(int i) {
        this.mStrokeColor = i;
        this.mUpdateCachedBitmap = true;
        invalidate();
    }

    public void setFrameColor(int i) {
        if (this.mFrameColor == 0 && i != 0) {
            setPadding(AndroidUtilities.dp(19.0f), AndroidUtilities.dp(7.0f), AndroidUtilities.dp(19.0f), AndroidUtilities.dp(7.0f));
            setCursorColor(-16777216);
        } else if (this.mFrameColor != 0 && i == 0) {
            setPadding(AndroidUtilities.dp(7.0f), AndroidUtilities.dp(7.0f), AndroidUtilities.dp(7.0f), AndroidUtilities.dp(7.0f));
            setCursorColor(-1);
        }
        this.mFrameColor = i;
        this.mUpdateCachedBitmap = true;
        invalidate();
    }

    public void setStrokeWidth(float f) {
        this.mStrokeWidth = f;
        this.mUpdateCachedBitmap = true;
        invalidate();
    }

    /* access modifiers changed from: protected */
    @SuppressLint({"DrawAllocation"})
    public void onDraw(Canvas canvas) {
        Layout layout;
        int i;
        Canvas canvas2 = canvas;
        float f = 2.0f;
        if (!(this.mCache == null || this.mStrokeColor == 0)) {
            if (this.mUpdateCachedBitmap) {
                int measuredWidth = (getMeasuredWidth() - getPaddingLeft()) - getPaddingRight();
                int measuredHeight = getMeasuredHeight();
                String obj = getText().toString();
                this.mCanvas.setBitmap(this.mCache);
                this.mCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
                float f2 = this.mStrokeWidth;
                if (f2 <= 0.0f) {
                    f2 = (float) Math.ceil((double) (getTextSize() / 11.5f));
                }
                this.textPaint.setStrokeWidth(f2);
                this.textPaint.setColor(this.mStrokeColor);
                this.textPaint.setTextSize(getTextSize());
                this.textPaint.setTypeface(getTypeface());
                this.textPaint.setStyle(Paint.Style.FILL_AND_STROKE);
                StaticLayout staticLayout = new StaticLayout(obj, this.textPaint, measuredWidth, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, true);
                this.mCanvas.save();
                this.mCanvas.translate((float) getPaddingLeft(), (((float) (((measuredHeight - getPaddingTop()) - getPaddingBottom()) - staticLayout.getHeight())) / 2.0f) + ((float) getPaddingTop()));
                staticLayout.draw(this.mCanvas);
                this.mCanvas.restore();
                this.mUpdateCachedBitmap = false;
            }
            canvas2.drawBitmap(this.mCache, 0.0f, 0.0f, this.textPaint);
        }
        int i2 = this.mFrameColor;
        if (i2 != 0) {
            this.paint.setColor(i2);
            Layout layout2 = getLayout();
            float[] fArr = this.lines;
            if (fArr == null || fArr.length != layout2.getLineCount()) {
                this.lines = new float[layout2.getLineCount()];
            }
            float dp = (float) AndroidUtilities.dp(6.0f);
            float dp2 = (float) AndroidUtilities.dp(6.0f);
            int i3 = 0;
            while (true) {
                float[] fArr2 = this.lines;
                if (i3 >= fArr2.length) {
                    break;
                }
                double ceil = Math.ceil((double) (layout2.getLineRight(i3) - layout2.getLineLeft(i3)));
                double d = (double) (dp2 * 2.0f);
                Double.isNaN(d);
                fArr2[i3] = (float) (ceil + d);
                i3++;
            }
            while (true) {
                int i4 = 1;
                boolean z = false;
                while (true) {
                    float[] fArr3 = this.lines;
                    if (i4 >= fArr3.length) {
                        break;
                    }
                    int i5 = i4 - 1;
                    float f3 = fArr3[i4] - fArr3[i5];
                    if (f3 > 0.0f) {
                        float f4 = f3 / 2.0f;
                        float f5 = dp * 2.0f;
                        if (f4 < f5) {
                            double d2 = (double) fArr3[i4];
                            double ceil2 = Math.ceil((double) (f5 - f4));
                            Double.isNaN(d2);
                            fArr3[i4] = (float) (d2 + ceil2);
                        } else {
                            i4++;
                        }
                    } else {
                        float f6 = dp * 2.0f;
                        if ((-f3) / 2.0f < f6) {
                            double d3 = (double) fArr3[i5];
                            double ceil3 = Math.ceil((double) (f6 + (f3 / 2.0f)));
                            Double.isNaN(d3);
                            fArr3[i5] = (float) (d3 + ceil3);
                        } else {
                            i4++;
                        }
                    }
                    z = true;
                    i4++;
                }
                if (!z) {
                    break;
                }
            }
            int measuredWidth2 = getMeasuredWidth() / 2;
            float measuredHeight2 = (float) ((getMeasuredHeight() - layout2.getHeight()) / 2);
            int i6 = 0;
            while (true) {
                float[] fArr4 = this.lines;
                if (i6 >= fArr4.length) {
                    break;
                }
                boolean z2 = i6 > 0 && fArr4[i6 + -1] > fArr4[i6];
                int i7 = i6 + 1;
                float[] fArr5 = this.lines;
                boolean z3 = i7 < fArr5.length && fArr5[i7] > fArr5[i6];
                this.path.reset();
                int lineBottom = ((layout2.getLineBottom(i6) - layout2.getLineTop(i6)) - (i6 != this.lines.length - 1 ? AndroidUtilities.dp(1.0f) : 0)) + (i6 != 0 ? AndroidUtilities.dp(1.0f) : 0);
                if (i6 != 0) {
                    measuredHeight2 -= 1.0f;
                    lineBottom++;
                }
                int i8 = lineBottom;
                boolean z4 = z3;
                float ceil4 = (float) Math.ceil((double) (((float) i8) + measuredHeight2));
                float f7 = (float) measuredWidth2;
                float[] fArr6 = this.lines;
                float f8 = (f7 - (fArr6[i6] / f)) + dp;
                float f9 = (f7 + (fArr6[i6] / f)) - dp;
                this.path.moveTo(f8, measuredHeight2);
                if (z2) {
                    float var_ = dp * f;
                    this.path.lineTo(f9 + var_, measuredHeight2);
                    layout = layout2;
                    this.rect.set(f9 + dp, measuredHeight2, f9 + (dp * 3.0f), measuredHeight2 + var_);
                    i = measuredWidth2;
                    this.path.arcTo(this.rect, 270.0f, -90.0f, false);
                } else {
                    layout = layout2;
                    i = measuredWidth2;
                    this.path.lineTo(f9, measuredHeight2);
                    this.rect.set(f9 - dp, measuredHeight2, f9 + dp, measuredHeight2 + (dp * 2.0f));
                    this.path.arcTo(this.rect, 270.0f, 90.0f, false);
                }
                float var_ = f9 + dp;
                this.path.lineTo(var_, ceil4 - dp);
                if (z4) {
                    float var_ = dp * 2.0f;
                    this.rect.set(var_, ceil4 - var_, f9 + (dp * 3.0f), ceil4);
                    this.path.arcTo(this.rect, 180.0f, -90.0f, false);
                    this.path.lineTo(f8 - var_, ceil4);
                } else {
                    this.rect.set(f9 - dp, ceil4 - (dp * 2.0f), var_, ceil4);
                    this.path.arcTo(this.rect, 0.0f, 90.0f, false);
                    this.path.lineTo(f8, ceil4);
                }
                if (z4) {
                    this.rect.set(f8 - (dp * 3.0f), ceil4 - (dp * 2.0f), f8 - dp, ceil4);
                    this.path.arcTo(this.rect, 90.0f, -90.0f, false);
                } else {
                    this.rect.set(f8 - dp, ceil4 - (dp * 2.0f), f8 + dp, ceil4);
                    this.path.arcTo(this.rect, 90.0f, 90.0f, false);
                }
                float var_ = f8 - dp;
                this.path.lineTo(var_, measuredHeight2 - dp);
                if (z2) {
                    f = 2.0f;
                    this.rect.set(f8 - (3.0f * dp), measuredHeight2, var_, (dp * 2.0f) + measuredHeight2);
                    this.path.arcTo(this.rect, 0.0f, -90.0f, false);
                } else {
                    f = 2.0f;
                    this.rect.set(var_, measuredHeight2, f8 + dp, (dp * 2.0f) + measuredHeight2);
                    this.path.arcTo(this.rect, 180.0f, 90.0f, false);
                }
                this.path.close();
                canvas2.drawPath(this.path, this.paint);
                if (i6 != 0) {
                    measuredHeight2 += 1.0f;
                    i8--;
                }
                measuredHeight2 += (float) i8;
                i6 = i7;
                measuredWidth2 = i;
                layout2 = layout;
            }
        }
        super.onDraw(canvas);
    }
}
