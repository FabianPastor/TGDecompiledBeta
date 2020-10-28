package org.telegram.ui.Components.Paint.Views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
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
        int i2 = this.mFrameColor;
        if (i2 == 0 && i != 0) {
            setPadding(AndroidUtilities.dp(19.0f), AndroidUtilities.dp(7.0f), AndroidUtilities.dp(19.0f), AndroidUtilities.dp(7.0f));
            setCursorColor(-16777216);
        } else if (i2 != 0 && i == 0) {
            setPadding(AndroidUtilities.dp(7.0f), AndroidUtilities.dp(7.0f), AndroidUtilities.dp(7.0f), AndroidUtilities.dp(7.0f));
            setCursorColor(-1);
        }
        this.mFrameColor = i;
        if (i != 0) {
            float computePerceivedBrightness = AndroidUtilities.computePerceivedBrightness(i);
            if (computePerceivedBrightness == 0.0f) {
                computePerceivedBrightness = ((float) Color.red(this.mFrameColor)) / 255.0f;
            }
            if (((double) computePerceivedBrightness) > 0.87d) {
                setTextColor(-16777216);
            } else {
                setTextColor(-1);
            }
        }
        this.mUpdateCachedBitmap = true;
        invalidate();
    }

    public void setStrokeWidth(float f) {
        this.mStrokeWidth = f;
        this.mUpdateCachedBitmap = true;
        invalidate();
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:101:0x02d1  */
    /* JADX WARNING: Removed duplicated region for block: B:107:0x0349  */
    /* JADX WARNING: Removed duplicated region for block: B:110:0x0360  */
    /* JADX WARNING: Removed duplicated region for block: B:113:0x0393  */
    /* JADX WARNING: Removed duplicated region for block: B:116:0x03ac  */
    /* JADX WARNING: Removed duplicated region for block: B:117:0x03b0  */
    /* JADX WARNING: Removed duplicated region for block: B:77:0x020d  */
    /* JADX WARNING: Removed duplicated region for block: B:78:0x020f  */
    /* JADX WARNING: Removed duplicated region for block: B:82:0x021d  */
    /* JADX WARNING: Removed duplicated region for block: B:83:0x0220  */
    /* JADX WARNING: Removed duplicated region for block: B:88:0x022f  */
    /* JADX WARNING: Removed duplicated region for block: B:89:0x0231  */
    /* JADX WARNING: Removed duplicated region for block: B:92:0x0239  */
    /* JADX WARNING: Removed duplicated region for block: B:95:0x0264  */
    /* JADX WARNING: Removed duplicated region for block: B:98:0x02b7  */
    @android.annotation.SuppressLint({"DrawAllocation"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onDraw(android.graphics.Canvas r25) {
        /*
            r24 = this;
            r0 = r24
            r1 = r25
            android.graphics.Bitmap r2 = r0.mCache
            r3 = 0
            r4 = 1073741824(0x40000000, float:2.0)
            r5 = 0
            if (r2 == 0) goto L_0x00bd
            int r2 = r0.mStrokeColor
            if (r2 == 0) goto L_0x00bd
            boolean r2 = r0.mUpdateCachedBitmap
            if (r2 == 0) goto L_0x00b6
            int r2 = r24.getMeasuredWidth()
            int r6 = r24.getPaddingLeft()
            int r2 = r2 - r6
            int r6 = r24.getPaddingRight()
            int r10 = r2 - r6
            int r2 = r24.getMeasuredHeight()
            android.text.Editable r6 = r24.getText()
            java.lang.String r8 = r6.toString()
            android.graphics.Canvas r6 = r0.mCanvas
            android.graphics.Bitmap r7 = r0.mCache
            r6.setBitmap(r7)
            android.graphics.Canvas r6 = r0.mCanvas
            android.graphics.PorterDuff$Mode r7 = android.graphics.PorterDuff.Mode.CLEAR
            r6.drawColor(r5, r7)
            float r6 = r0.mStrokeWidth
            int r7 = (r6 > r3 ? 1 : (r6 == r3 ? 0 : -1))
            if (r7 <= 0) goto L_0x0044
            goto L_0x0051
        L_0x0044:
            float r6 = r24.getTextSize()
            r7 = 1094189056(0x41380000, float:11.5)
            float r6 = r6 / r7
            double r6 = (double) r6
            double r6 = java.lang.Math.ceil(r6)
            float r6 = (float) r6
        L_0x0051:
            android.text.TextPaint r7 = r0.textPaint
            r7.setStrokeWidth(r6)
            android.text.TextPaint r6 = r0.textPaint
            int r7 = r0.mStrokeColor
            r6.setColor(r7)
            android.text.TextPaint r6 = r0.textPaint
            float r7 = r24.getTextSize()
            r6.setTextSize(r7)
            android.text.TextPaint r6 = r0.textPaint
            android.graphics.Typeface r7 = r24.getTypeface()
            r6.setTypeface(r7)
            android.text.TextPaint r6 = r0.textPaint
            android.graphics.Paint$Style r7 = android.graphics.Paint.Style.FILL_AND_STROKE
            r6.setStyle(r7)
            android.text.StaticLayout r6 = new android.text.StaticLayout
            android.text.TextPaint r9 = r0.textPaint
            android.text.Layout$Alignment r11 = android.text.Layout.Alignment.ALIGN_CENTER
            r12 = 1065353216(0x3var_, float:1.0)
            r13 = 0
            r14 = 1
            r7 = r6
            r7.<init>(r8, r9, r10, r11, r12, r13, r14)
            android.graphics.Canvas r7 = r0.mCanvas
            r7.save()
            int r7 = r24.getPaddingTop()
            int r2 = r2 - r7
            int r7 = r24.getPaddingBottom()
            int r2 = r2 - r7
            int r7 = r6.getHeight()
            int r2 = r2 - r7
            float r2 = (float) r2
            float r2 = r2 / r4
            android.graphics.Canvas r7 = r0.mCanvas
            int r8 = r24.getPaddingLeft()
            float r8 = (float) r8
            int r9 = r24.getPaddingTop()
            float r9 = (float) r9
            float r2 = r2 + r9
            r7.translate(r8, r2)
            android.graphics.Canvas r2 = r0.mCanvas
            r6.draw(r2)
            android.graphics.Canvas r2 = r0.mCanvas
            r2.restore()
            r0.mUpdateCachedBitmap = r5
        L_0x00b6:
            android.graphics.Bitmap r2 = r0.mCache
            android.text.TextPaint r6 = r0.textPaint
            r1.drawBitmap(r2, r3, r3, r6)
        L_0x00bd:
            int r2 = r0.mFrameColor
            if (r2 == 0) goto L_0x03c7
            android.graphics.Paint r6 = r0.paint
            r6.setColor(r2)
            android.text.Layout r2 = r24.getLayout()
            float[] r6 = r0.lines
            if (r6 == 0) goto L_0x00d5
            int r6 = r6.length
            int r7 = r2.getLineCount()
            if (r6 == r7) goto L_0x00dd
        L_0x00d5:
            int r6 = r2.getLineCount()
            float[] r6 = new float[r6]
            r0.lines = r6
        L_0x00dd:
            r6 = 1086324736(0x40CLASSNAME, float:6.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r7 = (float) r7
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r6 = (float) r6
            r8 = 1104150528(0x41d00000, float:26.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            float r8 = (float) r8
            r9 = 0
        L_0x00f1:
            float[] r10 = r0.lines
            int r10 = r10.length
            r11 = 1065353216(0x3var_, float:1.0)
            if (r9 >= r10) goto L_0x011f
            float r10 = r2.getLineRight(r9)
            float r12 = r2.getLineLeft(r9)
            float r10 = r10 - r12
            double r12 = (double) r10
            double r12 = java.lang.Math.ceil(r12)
            float r10 = (float) r12
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            float r11 = (float) r11
            int r11 = (r10 > r11 ? 1 : (r10 == r11 ? 0 : -1))
            if (r11 <= 0) goto L_0x0118
            float[] r11 = r0.lines
            float r12 = r6 * r4
            float r10 = r10 + r12
            r11[r9] = r10
            goto L_0x011c
        L_0x0118:
            float[] r10 = r0.lines
            r10[r9] = r3
        L_0x011c:
            int r9 = r9 + 1
            goto L_0x00f1
        L_0x011f:
            r9 = 1
            r10 = 1
            r12 = 0
        L_0x0122:
            float[] r13 = r0.lines
            int r14 = r13.length
            if (r10 >= r14) goto L_0x018c
            r14 = r13[r10]
            int r14 = (r14 > r3 ? 1 : (r14 == r3 ? 0 : -1))
            if (r14 != 0) goto L_0x012e
            goto L_0x0184
        L_0x012e:
            r14 = r13[r10]
            int r15 = r10 + -1
            r16 = r13[r15]
            float r14 = r14 - r16
            r16 = 1082130432(0x40800000, float:4.0)
            int r17 = (r14 > r3 ? 1 : (r14 == r3 ? 0 : -1))
            if (r17 <= 0) goto L_0x015e
            int r17 = (r14 > r8 ? 1 : (r14 == r8 ? 0 : -1))
            if (r17 >= 0) goto L_0x0146
            r12 = r13[r10]
            r13[r15] = r12
        L_0x0144:
            r12 = 1
            goto L_0x0184
        L_0x0146:
            float r16 = r16 * r7
            int r15 = (r14 > r16 ? 1 : (r14 == r16 ? 0 : -1))
            if (r15 >= 0) goto L_0x0184
            r12 = r13[r10]
            double r4 = (double) r12
            float r12 = r16 - r14
            double r14 = (double) r12
            double r14 = java.lang.Math.ceil(r14)
            java.lang.Double.isNaN(r4)
            double r4 = r4 + r14
            float r4 = (float) r4
            r13[r10] = r4
            goto L_0x0144
        L_0x015e:
            int r4 = (r14 > r3 ? 1 : (r14 == r3 ? 0 : -1))
            if (r4 >= 0) goto L_0x0184
            float r4 = -r14
            int r5 = (r4 > r8 ? 1 : (r4 == r8 ? 0 : -1))
            if (r5 >= 0) goto L_0x016c
            r4 = r13[r15]
            r13[r10] = r4
            goto L_0x0144
        L_0x016c:
            float r16 = r16 * r7
            int r4 = (r4 > r16 ? 1 : (r4 == r16 ? 0 : -1))
            if (r4 >= 0) goto L_0x0184
            r4 = r13[r15]
            double r4 = (double) r4
            float r12 = r16 + r14
            double r11 = (double) r12
            double r11 = java.lang.Math.ceil(r11)
            java.lang.Double.isNaN(r4)
            double r4 = r4 + r11
            float r4 = (float) r4
            r13[r15] = r4
            goto L_0x0144
        L_0x0184:
            int r10 = r10 + 1
            r4 = 1073741824(0x40000000, float:2.0)
            r5 = 0
            r11 = 1065353216(0x3var_, float:1.0)
            goto L_0x0122
        L_0x018c:
            if (r12 != 0) goto L_0x03c0
            int r4 = r24.getMeasuredWidth()
            int r4 = r4 / 2
            int r5 = r24.getMeasuredHeight()
            int r8 = r2.getHeight()
            int r5 = r5 - r8
            int r5 = r5 / 2
            float r5 = (float) r5
            r8 = 0
        L_0x01a1:
            float[] r10 = r0.lines
            int r10 = r10.length
            if (r8 >= r10) goto L_0x03c7
            int r10 = r2.getLineBottom(r8)
            int r11 = r2.getLineTop(r8)
            int r10 = r10 - r11
            float[] r11 = r0.lines
            int r11 = r11.length
            int r11 = r11 - r9
            if (r8 == r11) goto L_0x01bc
            r11 = 1065353216(0x3var_, float:1.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r11)
            goto L_0x01bf
        L_0x01bc:
            r11 = 1065353216(0x3var_, float:1.0)
            r12 = 0
        L_0x01bf:
            int r10 = r10 - r12
            if (r8 == 0) goto L_0x01c7
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r11)
            goto L_0x01c8
        L_0x01c7:
            r12 = 0
        L_0x01c8:
            int r10 = r10 + r12
            float[] r11 = r0.lines
            r12 = r11[r8]
            r13 = 1073741824(0x40000000, float:2.0)
            float r15 = r6 * r13
            int r12 = (r12 > r15 ? 1 : (r12 == r15 ? 0 : -1))
            if (r12 > 0) goto L_0x01e5
            float r10 = (float) r10
            float r5 = r5 + r10
            r23 = r2
            r22 = r4
            r21 = r6
            r2 = 1065353216(0x3var_, float:1.0)
            r3 = 1073741824(0x40000000, float:2.0)
            r9 = 0
            r10 = 0
            goto L_0x03b4
        L_0x01e5:
            if (r8 <= 0) goto L_0x01f9
            int r12 = r8 + -1
            r13 = r11[r12]
            r16 = r11[r8]
            int r13 = (r13 > r16 ? 1 : (r13 == r16 ? 0 : -1))
            if (r13 <= 0) goto L_0x01f9
            r12 = r11[r12]
            int r12 = (r12 > r15 ? 1 : (r12 == r15 ? 0 : -1))
            if (r12 <= 0) goto L_0x01f9
            r12 = 1
            goto L_0x01fa
        L_0x01f9:
            r12 = 0
        L_0x01fa:
            int r13 = r8 + 1
            int r14 = r11.length
            if (r13 >= r14) goto L_0x020f
            r14 = r11[r13]
            r18 = r11[r8]
            int r14 = (r14 > r18 ? 1 : (r14 == r18 ? 0 : -1))
            if (r14 <= 0) goto L_0x020f
            r14 = r11[r13]
            int r14 = (r14 > r15 ? 1 : (r14 == r15 ? 0 : -1))
            if (r14 <= 0) goto L_0x020f
            r15 = 1
            goto L_0x0210
        L_0x020f:
            r15 = 0
        L_0x0210:
            if (r8 == 0) goto L_0x0220
            int r14 = r8 + -1
            r14 = r11[r14]
            r18 = r11[r8]
            int r14 = (r14 > r18 ? 1 : (r14 == r18 ? 0 : -1))
            if (r14 == 0) goto L_0x021d
            goto L_0x0220
        L_0x021d:
            r18 = 0
            goto L_0x0222
        L_0x0220:
            r18 = 1
        L_0x0222:
            int r14 = r11.length
            int r14 = r14 - r9
            if (r8 == r14) goto L_0x0231
            r14 = r11[r8]
            r11 = r11[r13]
            int r11 = (r14 > r11 ? 1 : (r14 == r11 ? 0 : -1))
            if (r11 == 0) goto L_0x022f
            goto L_0x0231
        L_0x022f:
            r11 = 0
            goto L_0x0232
        L_0x0231:
            r11 = 1
        L_0x0232:
            android.graphics.Path r13 = r0.path
            r13.reset()
            if (r8 == 0) goto L_0x023e
            r13 = 1065353216(0x3var_, float:1.0)
            float r5 = r5 - r13
            int r10 = r10 + 1
        L_0x023e:
            float r13 = (float) r10
            float r13 = r13 + r5
            r19 = r10
            double r9 = (double) r13
            double r9 = java.lang.Math.ceil(r9)
            float r9 = (float) r9
            float r10 = (float) r4
            float[] r13 = r0.lines
            r20 = r13[r8]
            r17 = 1073741824(0x40000000, float:2.0)
            float r20 = r20 / r17
            float r20 = r10 - r20
            float r14 = r20 + r7
            r13 = r13[r8]
            float r13 = r13 / r17
            float r10 = r10 + r13
            float r10 = r10 - r7
            android.graphics.Path r13 = r0.path
            r13.moveTo(r14, r5)
            r20 = 1077936128(0x40400000, float:3.0)
            if (r18 == 0) goto L_0x02b7
            if (r12 == 0) goto L_0x0291
            android.graphics.Path r3 = r0.path
            r17 = 1073741824(0x40000000, float:2.0)
            float r21 = r7 * r17
            float r13 = r10 + r21
            r3.lineTo(r13, r5)
            android.graphics.RectF r3 = r0.rect
            float r13 = r10 + r7
            float r22 = r7 * r20
            r23 = r2
            float r2 = r10 + r22
            r22 = r4
            float r4 = r5 + r21
            r3.set(r13, r5, r2, r4)
            android.graphics.Path r2 = r0.path
            android.graphics.RectF r3 = r0.rect
            r21 = r6
            r4 = 1132920832(0x43870000, float:270.0)
            r6 = 0
            r13 = -1028390912(0xffffffffc2b40000, float:-90.0)
            r2.arcTo(r3, r4, r13, r6)
            goto L_0x02c4
        L_0x0291:
            r23 = r2
            r22 = r4
            r21 = r6
            android.graphics.Path r2 = r0.path
            r2.lineTo(r10, r5)
            android.graphics.RectF r2 = r0.rect
            float r3 = r10 - r7
            float r4 = r10 + r7
            r6 = 1073741824(0x40000000, float:2.0)
            float r13 = r7 * r6
            float r13 = r13 + r5
            r2.set(r3, r5, r4, r13)
            android.graphics.Path r2 = r0.path
            android.graphics.RectF r3 = r0.rect
            r4 = 1132920832(0x43870000, float:270.0)
            r6 = 1119092736(0x42b40000, float:90.0)
            r13 = 0
            r2.arcTo(r3, r4, r6, r13)
            goto L_0x02c4
        L_0x02b7:
            r23 = r2
            r22 = r4
            r21 = r6
            android.graphics.Path r2 = r0.path
            float r3 = r10 + r7
            r2.lineTo(r3, r5)
        L_0x02c4:
            android.graphics.Path r2 = r0.path
            float r3 = r10 + r7
            float r4 = r9 - r7
            r2.lineTo(r3, r4)
            r2 = 1127481344(0x43340000, float:180.0)
            if (r11 == 0) goto L_0x0349
            if (r15 == 0) goto L_0x02f3
            android.graphics.RectF r4 = r0.rect
            r6 = 1073741824(0x40000000, float:2.0)
            float r11 = r7 * r6
            float r6 = r9 - r11
            float r13 = r7 * r20
            float r10 = r10 + r13
            r4.set(r3, r6, r10, r9)
            android.graphics.Path r3 = r0.path
            android.graphics.RectF r4 = r0.rect
            r6 = -1028390912(0xffffffffc2b40000, float:-90.0)
            r10 = 0
            r3.arcTo(r4, r2, r6, r10)
            android.graphics.Path r3 = r0.path
            float r4 = r14 - r11
            r3.lineTo(r4, r9)
            goto L_0x030f
        L_0x02f3:
            android.graphics.RectF r4 = r0.rect
            float r10 = r10 - r7
            r6 = 1073741824(0x40000000, float:2.0)
            float r11 = r7 * r6
            float r6 = r9 - r11
            r4.set(r10, r6, r3, r9)
            android.graphics.Path r3 = r0.path
            android.graphics.RectF r4 = r0.rect
            r6 = 1119092736(0x42b40000, float:90.0)
            r10 = 0
            r11 = 0
            r3.arcTo(r4, r10, r6, r11)
            android.graphics.Path r3 = r0.path
            r3.lineTo(r14, r9)
        L_0x030f:
            if (r15 == 0) goto L_0x032f
            android.graphics.RectF r3 = r0.rect
            float r4 = r7 * r20
            float r4 = r14 - r4
            r6 = 1073741824(0x40000000, float:2.0)
            float r10 = r7 * r6
            float r10 = r9 - r10
            float r11 = r14 - r7
            r3.set(r4, r10, r11, r9)
            android.graphics.Path r3 = r0.path
            android.graphics.RectF r4 = r0.rect
            r9 = -1028390912(0xffffffffc2b40000, float:-90.0)
            r10 = 1119092736(0x42b40000, float:90.0)
            r11 = 0
            r3.arcTo(r4, r10, r9, r11)
            goto L_0x0355
        L_0x032f:
            r6 = 1073741824(0x40000000, float:2.0)
            r10 = 1119092736(0x42b40000, float:90.0)
            r11 = 0
            android.graphics.RectF r3 = r0.rect
            float r4 = r14 - r7
            float r13 = r7 * r6
            float r6 = r9 - r13
            float r13 = r14 + r7
            r3.set(r4, r6, r13, r9)
            android.graphics.Path r3 = r0.path
            android.graphics.RectF r4 = r0.rect
            r3.arcTo(r4, r10, r10, r11)
            goto L_0x0355
        L_0x0349:
            android.graphics.Path r4 = r0.path
            r4.lineTo(r3, r9)
            android.graphics.Path r3 = r0.path
            float r4 = r14 - r7
            r3.lineTo(r4, r9)
        L_0x0355:
            android.graphics.Path r3 = r0.path
            float r4 = r14 - r7
            float r6 = r5 - r7
            r3.lineTo(r4, r6)
            if (r18 == 0) goto L_0x0393
            if (r12 == 0) goto L_0x037c
            android.graphics.RectF r2 = r0.rect
            float r20 = r20 * r7
            float r14 = r14 - r20
            r3 = 1073741824(0x40000000, float:2.0)
            float r6 = r7 * r3
            float r6 = r6 + r5
            r2.set(r14, r5, r4, r6)
            android.graphics.Path r2 = r0.path
            android.graphics.RectF r4 = r0.rect
            r6 = -1028390912(0xffffffffc2b40000, float:-90.0)
            r9 = 0
            r10 = 0
            r2.arcTo(r4, r9, r6, r10)
            goto L_0x039c
        L_0x037c:
            r3 = 1073741824(0x40000000, float:2.0)
            r9 = 0
            r10 = 0
            android.graphics.RectF r6 = r0.rect
            float r14 = r14 + r7
            float r11 = r7 * r3
            float r11 = r11 + r5
            r6.set(r4, r5, r14, r11)
            android.graphics.Path r4 = r0.path
            android.graphics.RectF r6 = r0.rect
            r11 = 1119092736(0x42b40000, float:90.0)
            r4.arcTo(r6, r2, r11, r10)
            goto L_0x039c
        L_0x0393:
            r3 = 1073741824(0x40000000, float:2.0)
            r9 = 0
            r10 = 0
            android.graphics.Path r2 = r0.path
            r2.lineTo(r4, r5)
        L_0x039c:
            android.graphics.Path r2 = r0.path
            r2.close()
            android.graphics.Path r2 = r0.path
            android.graphics.Paint r4 = r0.paint
            r1.drawPath(r2, r4)
            r2 = 1065353216(0x3var_, float:1.0)
            if (r8 == 0) goto L_0x03b0
            float r5 = r5 + r2
            int r4 = r19 + -1
            goto L_0x03b2
        L_0x03b0:
            r4 = r19
        L_0x03b2:
            float r4 = (float) r4
            float r5 = r5 + r4
        L_0x03b4:
            int r8 = r8 + 1
            r6 = r21
            r4 = r22
            r2 = r23
            r3 = 0
            r9 = 1
            goto L_0x01a1
        L_0x03c0:
            r4 = 1073741824(0x40000000, float:2.0)
            r5 = 0
            r11 = 1065353216(0x3var_, float:1.0)
            goto L_0x011f
        L_0x03c7:
            super.onDraw(r25)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.Paint.Views.EditTextOutline.onDraw(android.graphics.Canvas):void");
    }
}
