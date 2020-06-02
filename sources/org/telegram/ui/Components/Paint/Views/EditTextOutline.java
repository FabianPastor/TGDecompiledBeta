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
        if (this.mFrameColor == 0 && i != 0) {
            setPadding(AndroidUtilities.dp(19.0f), AndroidUtilities.dp(7.0f), AndroidUtilities.dp(19.0f), AndroidUtilities.dp(7.0f));
            setCursorColor(-16777216);
        } else if (this.mFrameColor != 0 && i == 0) {
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
    /* JADX WARNING: Removed duplicated region for block: B:101:0x02dd  */
    /* JADX WARNING: Removed duplicated region for block: B:107:0x0355  */
    /* JADX WARNING: Removed duplicated region for block: B:110:0x036c  */
    /* JADX WARNING: Removed duplicated region for block: B:113:0x039f  */
    /* JADX WARNING: Removed duplicated region for block: B:116:0x03b8  */
    /* JADX WARNING: Removed duplicated region for block: B:117:0x03bc  */
    /* JADX WARNING: Removed duplicated region for block: B:77:0x0211  */
    /* JADX WARNING: Removed duplicated region for block: B:78:0x0213  */
    /* JADX WARNING: Removed duplicated region for block: B:80:0x0216  */
    /* JADX WARNING: Removed duplicated region for block: B:88:0x0235  */
    /* JADX WARNING: Removed duplicated region for block: B:89:0x0237  */
    /* JADX WARNING: Removed duplicated region for block: B:92:0x023f  */
    /* JADX WARNING: Removed duplicated region for block: B:95:0x026a  */
    /* JADX WARNING: Removed duplicated region for block: B:98:0x02c1  */
    @android.annotation.SuppressLint({"DrawAllocation"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onDraw(android.graphics.Canvas r26) {
        /*
            r25 = this;
            r0 = r25
            r1 = r26
            android.graphics.Bitmap r2 = r0.mCache
            r3 = 0
            r4 = 1073741824(0x40000000, float:2.0)
            r5 = 0
            if (r2 == 0) goto L_0x00bd
            int r2 = r0.mStrokeColor
            if (r2 == 0) goto L_0x00bd
            boolean r2 = r0.mUpdateCachedBitmap
            if (r2 == 0) goto L_0x00b6
            int r2 = r25.getMeasuredWidth()
            int r6 = r25.getPaddingLeft()
            int r2 = r2 - r6
            int r6 = r25.getPaddingRight()
            int r10 = r2 - r6
            int r2 = r25.getMeasuredHeight()
            android.text.Editable r6 = r25.getText()
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
            float r6 = r25.getTextSize()
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
            float r7 = r25.getTextSize()
            r6.setTextSize(r7)
            android.text.TextPaint r6 = r0.textPaint
            android.graphics.Typeface r7 = r25.getTypeface()
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
            int r7 = r25.getPaddingTop()
            int r2 = r2 - r7
            int r7 = r25.getPaddingBottom()
            int r2 = r2 - r7
            int r7 = r6.getHeight()
            int r2 = r2 - r7
            float r2 = (float) r2
            float r2 = r2 / r4
            android.graphics.Canvas r7 = r0.mCanvas
            int r8 = r25.getPaddingLeft()
            float r8 = (float) r8
            int r9 = r25.getPaddingTop()
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
            if (r2 == 0) goto L_0x03d5
            android.graphics.Paint r6 = r0.paint
            r6.setColor(r2)
            android.text.Layout r2 = r25.getLayout()
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
            if (r12 != 0) goto L_0x03cc
            int r4 = r25.getMeasuredWidth()
            int r4 = r4 / 2
            int r5 = r25.getMeasuredHeight()
            int r8 = r2.getHeight()
            int r5 = r5 - r8
            int r5 = r5 / 2
            float r5 = (float) r5
            r8 = 0
        L_0x01a1:
            float[] r10 = r0.lines
            int r10 = r10.length
            if (r8 >= r10) goto L_0x03d5
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
            if (r12 > 0) goto L_0x01e7
            float r10 = (float) r10
            float r5 = r5 + r10
            r22 = r2
            r24 = r4
            r23 = r6
            r21 = r8
            r2 = 1065353216(0x3var_, float:1.0)
            r3 = 1073741824(0x40000000, float:2.0)
            r9 = 0
            r10 = 0
            goto L_0x03c0
        L_0x01e7:
            if (r8 <= 0) goto L_0x01fb
            int r12 = r8 + -1
            r13 = r11[r12]
            r16 = r11[r8]
            int r13 = (r13 > r16 ? 1 : (r13 == r16 ? 0 : -1))
            if (r13 <= 0) goto L_0x01fb
            r11 = r11[r12]
            int r11 = (r11 > r15 ? 1 : (r11 == r15 ? 0 : -1))
            if (r11 <= 0) goto L_0x01fb
            r11 = 1
            goto L_0x01fc
        L_0x01fb:
            r11 = 0
        L_0x01fc:
            int r12 = r8 + 1
            float[] r13 = r0.lines
            int r14 = r13.length
            if (r12 >= r14) goto L_0x0213
            r14 = r13[r12]
            r18 = r13[r8]
            int r14 = (r14 > r18 ? 1 : (r14 == r18 ? 0 : -1))
            if (r14 <= 0) goto L_0x0213
            r13 = r13[r12]
            int r13 = (r13 > r15 ? 1 : (r13 == r15 ? 0 : -1))
            if (r13 <= 0) goto L_0x0213
            r13 = 1
            goto L_0x0214
        L_0x0213:
            r13 = 0
        L_0x0214:
            if (r8 == 0) goto L_0x0225
            float[] r14 = r0.lines
            int r15 = r8 + -1
            r15 = r14[r15]
            r14 = r14[r8]
            int r14 = (r15 > r14 ? 1 : (r15 == r14 ? 0 : -1))
            if (r14 == 0) goto L_0x0223
            goto L_0x0225
        L_0x0223:
            r15 = 0
            goto L_0x0226
        L_0x0225:
            r15 = 1
        L_0x0226:
            float[] r14 = r0.lines
            int r3 = r14.length
            int r3 = r3 - r9
            if (r8 == r3) goto L_0x0237
            r3 = r14[r8]
            r12 = r14[r12]
            int r3 = (r3 > r12 ? 1 : (r3 == r12 ? 0 : -1))
            if (r3 == 0) goto L_0x0235
            goto L_0x0237
        L_0x0235:
            r3 = 0
            goto L_0x0238
        L_0x0237:
            r3 = 1
        L_0x0238:
            android.graphics.Path r12 = r0.path
            r12.reset()
            if (r8 == 0) goto L_0x0244
            r12 = 1065353216(0x3var_, float:1.0)
            float r5 = r5 - r12
            int r10 = r10 + 1
        L_0x0244:
            float r12 = (float) r10
            float r12 = r12 + r5
            r19 = r10
            double r9 = (double) r12
            double r9 = java.lang.Math.ceil(r9)
            float r9 = (float) r9
            float r10 = (float) r4
            float[] r12 = r0.lines
            r20 = r12[r8]
            r17 = 1073741824(0x40000000, float:2.0)
            float r20 = r20 / r17
            float r20 = r10 - r20
            float r14 = r20 + r7
            r12 = r12[r8]
            float r12 = r12 / r17
            float r10 = r10 + r12
            float r10 = r10 - r7
            android.graphics.Path r12 = r0.path
            r12.moveTo(r14, r5)
            r20 = 1077936128(0x40400000, float:3.0)
            if (r15 == 0) goto L_0x02c1
            if (r11 == 0) goto L_0x0299
            android.graphics.Path r12 = r0.path
            r17 = 1073741824(0x40000000, float:2.0)
            float r21 = r7 * r17
            r22 = r2
            float r2 = r10 + r21
            r12.lineTo(r2, r5)
            android.graphics.RectF r2 = r0.rect
            float r12 = r10 + r7
            float r23 = r7 * r20
            r24 = r4
            float r4 = r10 + r23
            r23 = r6
            float r6 = r5 + r21
            r2.set(r12, r5, r4, r6)
            android.graphics.Path r2 = r0.path
            android.graphics.RectF r4 = r0.rect
            r21 = r8
            r6 = 1132920832(0x43870000, float:270.0)
            r8 = 0
            r12 = -1028390912(0xffffffffc2b40000, float:-90.0)
            r2.arcTo(r4, r6, r12, r8)
            goto L_0x02d0
        L_0x0299:
            r22 = r2
            r24 = r4
            r23 = r6
            r21 = r8
            android.graphics.Path r2 = r0.path
            r2.lineTo(r10, r5)
            android.graphics.RectF r2 = r0.rect
            float r4 = r10 - r7
            float r6 = r10 + r7
            r8 = 1073741824(0x40000000, float:2.0)
            float r12 = r7 * r8
            float r12 = r12 + r5
            r2.set(r4, r5, r6, r12)
            android.graphics.Path r2 = r0.path
            android.graphics.RectF r4 = r0.rect
            r6 = 1132920832(0x43870000, float:270.0)
            r8 = 1119092736(0x42b40000, float:90.0)
            r12 = 0
            r2.arcTo(r4, r6, r8, r12)
            goto L_0x02d0
        L_0x02c1:
            r22 = r2
            r24 = r4
            r23 = r6
            r21 = r8
            android.graphics.Path r2 = r0.path
            float r4 = r10 + r7
            r2.lineTo(r4, r5)
        L_0x02d0:
            android.graphics.Path r2 = r0.path
            float r4 = r10 + r7
            float r6 = r9 - r7
            r2.lineTo(r4, r6)
            r2 = 1127481344(0x43340000, float:180.0)
            if (r3 == 0) goto L_0x0355
            if (r13 == 0) goto L_0x02ff
            android.graphics.RectF r3 = r0.rect
            r6 = 1073741824(0x40000000, float:2.0)
            float r8 = r7 * r6
            float r6 = r9 - r8
            float r12 = r7 * r20
            float r10 = r10 + r12
            r3.set(r4, r6, r10, r9)
            android.graphics.Path r3 = r0.path
            android.graphics.RectF r4 = r0.rect
            r6 = -1028390912(0xffffffffc2b40000, float:-90.0)
            r10 = 0
            r3.arcTo(r4, r2, r6, r10)
            android.graphics.Path r3 = r0.path
            float r4 = r14 - r8
            r3.lineTo(r4, r9)
            goto L_0x031b
        L_0x02ff:
            android.graphics.RectF r3 = r0.rect
            float r10 = r10 - r7
            r6 = 1073741824(0x40000000, float:2.0)
            float r8 = r7 * r6
            float r6 = r9 - r8
            r3.set(r10, r6, r4, r9)
            android.graphics.Path r3 = r0.path
            android.graphics.RectF r4 = r0.rect
            r6 = 1119092736(0x42b40000, float:90.0)
            r8 = 0
            r10 = 0
            r3.arcTo(r4, r8, r6, r10)
            android.graphics.Path r3 = r0.path
            r3.lineTo(r14, r9)
        L_0x031b:
            if (r13 == 0) goto L_0x033b
            android.graphics.RectF r3 = r0.rect
            float r4 = r7 * r20
            float r4 = r14 - r4
            r6 = 1073741824(0x40000000, float:2.0)
            float r8 = r7 * r6
            float r8 = r9 - r8
            float r10 = r14 - r7
            r3.set(r4, r8, r10, r9)
            android.graphics.Path r3 = r0.path
            android.graphics.RectF r4 = r0.rect
            r8 = -1028390912(0xffffffffc2b40000, float:-90.0)
            r10 = 1119092736(0x42b40000, float:90.0)
            r12 = 0
            r3.arcTo(r4, r10, r8, r12)
            goto L_0x0361
        L_0x033b:
            r6 = 1073741824(0x40000000, float:2.0)
            r10 = 1119092736(0x42b40000, float:90.0)
            r12 = 0
            android.graphics.RectF r3 = r0.rect
            float r4 = r14 - r7
            float r8 = r7 * r6
            float r6 = r9 - r8
            float r8 = r14 + r7
            r3.set(r4, r6, r8, r9)
            android.graphics.Path r3 = r0.path
            android.graphics.RectF r4 = r0.rect
            r3.arcTo(r4, r10, r10, r12)
            goto L_0x0361
        L_0x0355:
            android.graphics.Path r3 = r0.path
            r3.lineTo(r4, r9)
            android.graphics.Path r3 = r0.path
            float r4 = r14 - r7
            r3.lineTo(r4, r9)
        L_0x0361:
            android.graphics.Path r3 = r0.path
            float r4 = r14 - r7
            float r6 = r5 - r7
            r3.lineTo(r4, r6)
            if (r15 == 0) goto L_0x039f
            if (r11 == 0) goto L_0x0388
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
            goto L_0x03a8
        L_0x0388:
            r3 = 1073741824(0x40000000, float:2.0)
            r9 = 0
            r10 = 0
            android.graphics.RectF r6 = r0.rect
            float r14 = r14 + r7
            float r8 = r7 * r3
            float r8 = r8 + r5
            r6.set(r4, r5, r14, r8)
            android.graphics.Path r4 = r0.path
            android.graphics.RectF r6 = r0.rect
            r8 = 1119092736(0x42b40000, float:90.0)
            r4.arcTo(r6, r2, r8, r10)
            goto L_0x03a8
        L_0x039f:
            r3 = 1073741824(0x40000000, float:2.0)
            r9 = 0
            r10 = 0
            android.graphics.Path r2 = r0.path
            r2.lineTo(r4, r5)
        L_0x03a8:
            android.graphics.Path r2 = r0.path
            r2.close()
            android.graphics.Path r2 = r0.path
            android.graphics.Paint r4 = r0.paint
            r1.drawPath(r2, r4)
            r2 = 1065353216(0x3var_, float:1.0)
            if (r21 == 0) goto L_0x03bc
            float r5 = r5 + r2
            int r4 = r19 + -1
            goto L_0x03be
        L_0x03bc:
            r4 = r19
        L_0x03be:
            float r4 = (float) r4
            float r5 = r5 + r4
        L_0x03c0:
            int r8 = r21 + 1
            r2 = r22
            r6 = r23
            r4 = r24
            r3 = 0
            r9 = 1
            goto L_0x01a1
        L_0x03cc:
            r9 = 0
            r3 = 0
            r4 = 1073741824(0x40000000, float:2.0)
            r5 = 0
            r11 = 1065353216(0x3var_, float:1.0)
            goto L_0x011f
        L_0x03d5:
            super.onDraw(r26)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.Paint.Views.EditTextOutline.onDraw(android.graphics.Canvas):void");
    }
}
