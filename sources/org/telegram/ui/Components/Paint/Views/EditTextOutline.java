package org.telegram.ui.Components.Paint.Views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
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
    public void onTextChanged(CharSequence text, int start, int before, int after) {
        super.onTextChanged(text, start, before, after);
        this.mUpdateCachedBitmap = true;
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w <= 0 || h <= 0) {
            this.mCache = null;
            return;
        }
        this.mUpdateCachedBitmap = true;
        Bitmap bitmap = this.mCache;
        if (bitmap != null) {
            bitmap.recycle();
        }
        this.mCache = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
    }

    public void setStrokeColor(int strokeColor) {
        this.mStrokeColor = strokeColor;
        this.mUpdateCachedBitmap = true;
        invalidate();
    }

    public void setFrameColor(int frameColor) {
        int i = this.mFrameColor;
        if (i == 0 && frameColor != 0) {
            setPadding(AndroidUtilities.dp(19.0f), AndroidUtilities.dp(7.0f), AndroidUtilities.dp(19.0f), AndroidUtilities.dp(7.0f));
            setCursorColor(-16777216);
        } else if (i != 0 && frameColor == 0) {
            setPadding(AndroidUtilities.dp(7.0f), AndroidUtilities.dp(7.0f), AndroidUtilities.dp(7.0f), AndroidUtilities.dp(7.0f));
            setCursorColor(-1);
        }
        this.mFrameColor = frameColor;
        if (frameColor != 0) {
            float lightness = AndroidUtilities.computePerceivedBrightness(frameColor);
            if (lightness == 0.0f) {
                lightness = ((float) Color.red(this.mFrameColor)) / 255.0f;
            }
            if (((double) lightness) > 0.87d) {
                setTextColor(-16777216);
            } else {
                setTextColor(-1);
            }
        }
        this.mUpdateCachedBitmap = true;
        invalidate();
    }

    public void setStrokeWidth(float strokeWidth) {
        this.mStrokeWidth = strokeWidth;
        this.mUpdateCachedBitmap = true;
        invalidate();
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        boolean hasChanges;
        int i;
        float f;
        float inset;
        int cx;
        Layout sl;
        boolean hasChanges2;
        float padding;
        float f2;
        Canvas canvas2 = canvas;
        float f3 = 0.0f;
        if (!(this.mCache == null || this.mStrokeColor == 0)) {
            if (this.mUpdateCachedBitmap) {
                int w = (getMeasuredWidth() - getPaddingLeft()) - getPaddingRight();
                int h = getMeasuredHeight();
                String text = getText().toString();
                this.mCanvas.setBitmap(this.mCache);
                this.mCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
                float f4 = this.mStrokeWidth;
                if (f4 <= 0.0f) {
                    f4 = (float) Math.ceil((double) (getTextSize() / 11.5f));
                }
                float strokeWidth = f4;
                this.textPaint.setStrokeWidth(strokeWidth);
                this.textPaint.setColor(this.mStrokeColor);
                this.textPaint.setTextSize(getTextSize());
                this.textPaint.setTypeface(getTypeface());
                this.textPaint.setStyle(Paint.Style.FILL_AND_STROKE);
                float f5 = strokeWidth;
                StaticLayout sl2 = new StaticLayout(text, this.textPaint, w, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, true);
                this.mCanvas.save();
                this.mCanvas.translate((float) getPaddingLeft(), ((float) getPaddingTop()) + (((float) (((h - getPaddingTop()) - getPaddingBottom()) - sl2.getHeight())) / 2.0f));
                sl2.draw(this.mCanvas);
                this.mCanvas.restore();
                this.mUpdateCachedBitmap = false;
            }
            canvas2.drawBitmap(this.mCache, 0.0f, 0.0f, this.textPaint);
        }
        int i2 = this.mFrameColor;
        if (i2 != 0) {
            this.paint.setColor(i2);
            Layout sl3 = getLayout();
            float[] fArr = this.lines;
            if (fArr == null || fArr.length != sl3.getLineCount()) {
                this.lines = new float[sl3.getLineCount()];
            }
            float rad = (float) AndroidUtilities.dp(6.0f);
            float padding2 = (float) AndroidUtilities.dp(6.0f);
            float inset2 = (float) AndroidUtilities.dp(26.0f);
            for (int a = 0; a < this.lines.length; a++) {
                float w2 = (float) Math.ceil((double) (sl3.getLineRight(a) - sl3.getLineLeft(a)));
                if (w2 > ((float) AndroidUtilities.dp(1.0f))) {
                    this.lines[a] = (padding2 * 2.0f) + w2;
                } else {
                    this.lines[a] = 0.0f;
                }
            }
            while (true) {
                hasChanges = false;
                int a2 = 1;
                while (true) {
                    float[] fArr2 = this.lines;
                    if (a2 >= fArr2.length) {
                        break;
                    }
                    if (fArr2[a2] != f3) {
                        float diff = fArr2[a2] - fArr2[a2 - 1];
                        if (diff > f3) {
                            if (diff < inset2) {
                                fArr2[a2 - 1] = fArr2[a2];
                                hasChanges = true;
                            } else if (diff < rad * 4.0f) {
                                double d = (double) fArr2[a2];
                                double ceil = Math.ceil((double) ((4.0f * rad) - diff));
                                Double.isNaN(d);
                                fArr2[a2] = (float) (d + ceil);
                                hasChanges = true;
                            }
                        } else if (diff < f3) {
                            if ((-diff) < inset2) {
                                fArr2[a2] = fArr2[a2 - 1];
                                hasChanges = true;
                            } else if ((-diff) < rad * 4.0f) {
                                int i3 = a2 - 1;
                                double d2 = (double) fArr2[i3];
                                double ceil2 = Math.ceil((double) ((4.0f * rad) + diff));
                                Double.isNaN(d2);
                                fArr2[i3] = (float) (d2 + ceil2);
                                hasChanges = true;
                            }
                        }
                    }
                    a2++;
                    f3 = 0.0f;
                }
                if (!hasChanges) {
                    break;
                }
                float f6 = padding2;
                float f7 = inset2;
                boolean z = hasChanges;
                sl3 = sl3;
                f3 = 0.0f;
            }
            int cx2 = getMeasuredWidth() / 2;
            float top = (float) ((getMeasuredHeight() - sl3.getHeight()) / 2);
            int a3 = 0;
            while (a3 < this.lines.length) {
                int lineBottom = sl3.getLineBottom(a3) - sl3.getLineTop(a3);
                boolean z2 = true;
                if (a3 != this.lines.length - 1) {
                    f = 1.0f;
                    i = AndroidUtilities.dp(1.0f);
                } else {
                    f = 1.0f;
                    i = 0;
                }
                int h2 = (lineBottom - i) + (a3 != 0 ? AndroidUtilities.dp(f) : 0);
                float[] fArr3 = this.lines;
                if (fArr3[a3] <= padding2 * 2.0f) {
                    top += (float) h2;
                    sl = sl3;
                    cx = cx2;
                    padding = padding2;
                    inset = inset2;
                    hasChanges2 = hasChanges;
                } else {
                    boolean topLess = a3 > 0 && fArr3[a3 + -1] > fArr3[a3] && fArr3[a3 + -1] > padding2 * 2.0f;
                    boolean bottomLess = a3 + 1 < fArr3.length && fArr3[a3 + 1] > fArr3[a3] && fArr3[a3 + 1] > padding2 * 2.0f;
                    boolean drawTop = a3 == 0 || fArr3[a3 + -1] != fArr3[a3];
                    if (a3 != fArr3.length - 1 && fArr3[a3] == fArr3[a3 + 1]) {
                        z2 = false;
                    }
                    boolean drawBottom = z2;
                    this.path.reset();
                    if (a3 != 0) {
                        top -= 1.0f;
                        h2++;
                    }
                    padding = padding2;
                    float bottom = (float) Math.ceil((double) (((float) h2) + top));
                    float[] fArr4 = this.lines;
                    float cx1 = (((float) cx2) - (fArr4[a3] / 2.0f)) + rad;
                    float cx22 = (((float) cx2) + (fArr4[a3] / 2.0f)) - rad;
                    this.path.moveTo(cx1, top);
                    if (!drawTop) {
                        sl = sl3;
                        cx = cx2;
                        inset = inset2;
                        hasChanges2 = hasChanges;
                        this.path.lineTo(cx22 + rad, top);
                    } else if (topLess) {
                        sl = sl3;
                        this.path.lineTo(cx22 + (rad * 2.0f), top);
                        cx = cx2;
                        inset = inset2;
                        this.rect.set(cx22 + rad, top, cx22 + (rad * 3.0f), top + (rad * 2.0f));
                        hasChanges2 = hasChanges;
                        this.path.arcTo(this.rect, 270.0f, -90.0f, false);
                    } else {
                        sl = sl3;
                        cx = cx2;
                        inset = inset2;
                        hasChanges2 = hasChanges;
                        this.path.lineTo(cx22, top);
                        this.rect.set(cx22 - rad, top, cx22 + rad, (rad * 2.0f) + top);
                        this.path.arcTo(this.rect, 270.0f, 90.0f, false);
                    }
                    this.path.lineTo(cx22 + rad, bottom - rad);
                    if (drawBottom) {
                        if (bottomLess) {
                            this.rect.set(cx22 + rad, bottom - (rad * 2.0f), cx22 + (rad * 3.0f), bottom);
                            this.path.arcTo(this.rect, 180.0f, -90.0f, false);
                            this.path.lineTo(cx1 - (rad * 2.0f), bottom);
                            f2 = -90.0f;
                        } else {
                            this.rect.set(cx22 - rad, bottom - (rad * 2.0f), cx22 + rad, bottom);
                            this.path.arcTo(this.rect, 0.0f, 90.0f, false);
                            f2 = -90.0f;
                            this.path.lineTo(cx1, bottom);
                        }
                        if (bottomLess) {
                            this.rect.set(cx1 - (rad * 3.0f), bottom - (rad * 2.0f), cx1 - rad, bottom);
                            this.path.arcTo(this.rect, 90.0f, f2, false);
                        } else {
                            this.rect.set(cx1 - rad, bottom - (rad * 2.0f), cx1 + rad, bottom);
                            this.path.arcTo(this.rect, 90.0f, 90.0f, false);
                        }
                    } else {
                        f2 = -90.0f;
                        this.path.lineTo(cx22 + rad, bottom);
                        this.path.lineTo(cx1 - rad, bottom);
                    }
                    this.path.lineTo(cx1 - rad, top - rad);
                    if (!drawTop) {
                        this.path.lineTo(cx1 - rad, top);
                    } else if (topLess) {
                        this.rect.set(cx1 - (3.0f * rad), top, cx1 - rad, top + (rad * 2.0f));
                        this.path.arcTo(this.rect, 0.0f, f2, false);
                    } else {
                        this.rect.set(cx1 - rad, top, cx1 + rad, top + (rad * 2.0f));
                        this.path.arcTo(this.rect, 180.0f, 90.0f, false);
                    }
                    this.path.close();
                    canvas2.drawPath(this.path, this.paint);
                    if (a3 != 0) {
                        top += 1.0f;
                        h2--;
                    }
                    top += (float) h2;
                }
                a3++;
                padding2 = padding;
                hasChanges = hasChanges2;
                sl3 = sl;
                cx2 = cx;
                inset2 = inset;
            }
            int i4 = cx2;
            float f8 = padding2;
            float f9 = inset2;
            boolean z3 = hasChanges;
        }
        super.onDraw(canvas);
    }
}
