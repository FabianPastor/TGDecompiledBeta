package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextPaint;
import android.view.View;
import androidx.annotation.Keep;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;

public class CheckBoxBase {
    private static Paint eraser;
    private static Paint paint;
    private boolean attachedToWindow;
    private String background2ColorKey = "chat_serviceBackground";
    private float backgroundAlpha = 1.0f;
    private String backgroundColorKey = "chat_serviceBackground";
    private Paint backgroundPaint;
    private int backgroundType;
    private Canvas bitmapCanvas;
    private Rect bounds = new Rect();
    /* access modifiers changed from: private */
    public ObjectAnimator checkAnimator;
    private String checkColorKey = "checkboxCheck";
    private Paint checkPaint;
    /* access modifiers changed from: private */
    public String checkedText;
    private Bitmap drawBitmap;
    private boolean drawUnchecked = true;
    private boolean enabled = true;
    /* access modifiers changed from: private */
    public boolean isChecked;
    private Theme.MessageDrawable messageDrawable;
    private View parentView;
    private Path path = new Path();
    private float progress;
    private ProgressDelegate progressDelegate;
    private RectF rect = new RectF();
    private float size;
    private TextPaint textPaint;
    private boolean useDefaultCheck;

    public interface ProgressDelegate {
        void setProgress(float f);
    }

    public CheckBoxBase(View view, int i) {
        this.parentView = view;
        this.size = (float) i;
        if (paint == null) {
            paint = new Paint(1);
            Paint paint2 = new Paint(1);
            eraser = paint2;
            paint2.setColor(0);
            eraser.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        }
        Paint paint3 = new Paint(1);
        this.checkPaint = paint3;
        paint3.setStrokeCap(Paint.Cap.ROUND);
        this.checkPaint.setStyle(Paint.Style.STROKE);
        this.checkPaint.setStrokeJoin(Paint.Join.ROUND);
        this.checkPaint.setStrokeWidth((float) AndroidUtilities.dp(1.9f));
        Paint paint4 = new Paint(1);
        this.backgroundPaint = paint4;
        paint4.setStyle(Paint.Style.STROKE);
        this.backgroundPaint.setStrokeWidth((float) AndroidUtilities.dp(1.2f));
        this.drawBitmap = Bitmap.createBitmap(AndroidUtilities.dp(this.size), AndroidUtilities.dp(this.size), Bitmap.Config.ARGB_4444);
        this.bitmapCanvas = new Canvas(this.drawBitmap);
    }

    public void onAttachedToWindow() {
        this.attachedToWindow = true;
    }

    public void onDetachedFromWindow() {
        this.attachedToWindow = false;
    }

    public void setBounds(int i, int i2, int i3, int i4) {
        Rect rect2 = this.bounds;
        rect2.left = i;
        rect2.top = i2;
        rect2.right = i + i3;
        rect2.bottom = i2 + i4;
    }

    public void setDrawUnchecked(boolean z) {
        this.drawUnchecked = z;
    }

    @Keep
    public void setProgress(float f) {
        if (this.progress != f) {
            this.progress = f;
            invalidate();
            ProgressDelegate progressDelegate2 = this.progressDelegate;
            if (progressDelegate2 != null) {
                progressDelegate2.setProgress(f);
            }
        }
    }

    private void invalidate() {
        if (this.parentView.getParent() != null) {
            ((View) this.parentView.getParent()).invalidate();
        }
        this.parentView.invalidate();
    }

    public void setProgressDelegate(ProgressDelegate progressDelegate2) {
        this.progressDelegate = progressDelegate2;
    }

    @Keep
    public float getProgress() {
        return this.progress;
    }

    public boolean isChecked() {
        return this.isChecked;
    }

    public void setEnabled(boolean z) {
        this.enabled = z;
    }

    public void setBackgroundType(int i) {
        this.backgroundType = i;
        if (i == 12 || i == 13) {
            this.backgroundPaint.setStrokeWidth((float) AndroidUtilities.dp(1.0f));
        } else if (i == 4 || i == 5) {
            this.backgroundPaint.setStrokeWidth((float) AndroidUtilities.dp(1.9f));
            if (i == 5) {
                this.checkPaint.setStrokeWidth((float) AndroidUtilities.dp(1.5f));
            }
        } else if (i == 3) {
            this.backgroundPaint.setStrokeWidth((float) AndroidUtilities.dp(1.2f));
        } else if (i != 0) {
            this.backgroundPaint.setStrokeWidth((float) AndroidUtilities.dp(1.5f));
        }
    }

    private void cancelCheckAnimator() {
        ObjectAnimator objectAnimator = this.checkAnimator;
        if (objectAnimator != null) {
            objectAnimator.cancel();
            this.checkAnimator = null;
        }
    }

    private void animateToCheckedState(boolean z) {
        float[] fArr = new float[1];
        fArr[0] = z ? 1.0f : 0.0f;
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this, "progress", fArr);
        this.checkAnimator = ofFloat;
        ofFloat.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                if (animator.equals(CheckBoxBase.this.checkAnimator)) {
                    ObjectAnimator unused = CheckBoxBase.this.checkAnimator = null;
                }
                if (!CheckBoxBase.this.isChecked) {
                    String unused2 = CheckBoxBase.this.checkedText = null;
                }
            }
        });
        this.checkAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT);
        this.checkAnimator.setDuration(200);
        this.checkAnimator.start();
    }

    public void setColor(String str, String str2, String str3) {
        this.backgroundColorKey = str;
        this.background2ColorKey = str2;
        this.checkColorKey = str3;
    }

    public void setBackgroundDrawable(Theme.MessageDrawable messageDrawable2) {
        this.messageDrawable = messageDrawable2;
    }

    public void setUseDefaultCheck(boolean z) {
        this.useDefaultCheck = z;
    }

    public void setBackgroundAlpha(float f) {
        this.backgroundAlpha = f;
    }

    public void setNum(int i) {
        if (i >= 0) {
            this.checkedText = "" + (i + 1);
        } else if (this.checkAnimator == null) {
            this.checkedText = null;
        }
        invalidate();
    }

    public void setChecked(boolean z, boolean z2) {
        setChecked(-1, z, z2);
    }

    public void setChecked(int i, boolean z, boolean z2) {
        if (i >= 0) {
            this.checkedText = "" + (i + 1);
            invalidate();
        }
        if (z != this.isChecked) {
            this.isChecked = z;
            if (!this.attachedToWindow || !z2) {
                cancelCheckAnimator();
                setProgress(z ? 1.0f : 0.0f);
                return;
            }
            animateToCheckedState(z);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:13:0x0045  */
    /* JADX WARNING: Removed duplicated region for block: B:149:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:14:0x0048  */
    /* JADX WARNING: Removed duplicated region for block: B:17:0x0066  */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x00ef  */
    /* JADX WARNING: Removed duplicated region for block: B:88:0x02bc  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void draw(android.graphics.Canvas r21) {
        /*
            r20 = this;
            r0 = r20
            r7 = r21
            android.graphics.Bitmap r1 = r0.drawBitmap
            if (r1 != 0) goto L_0x0009
            return
        L_0x0009:
            r2 = 0
            r1.eraseColor(r2)
            float r1 = r0.size
            r8 = 1073741824(0x40000000, float:2.0)
            float r1 = r1 / r8
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            int r3 = r0.backgroundType
            r9 = 1092616192(0x41200000, float:10.0)
            r10 = 11
            r11 = 13
            r12 = 12
            if (r3 == r12) goto L_0x0037
            if (r3 != r11) goto L_0x0026
            goto L_0x0037
        L_0x0026:
            if (r3 == 0) goto L_0x003c
            if (r3 == r10) goto L_0x003c
            r3 = 1045220557(0x3e4ccccd, float:0.2)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r3
            float r3 = r1 - r3
            r13 = r1
            r1 = r3
            goto L_0x003d
        L_0x0037:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r9)
            float r1 = (float) r1
        L_0x003c:
            r13 = r1
        L_0x003d:
            float r3 = r0.progress
            r15 = 1056964608(0x3var_, float:0.5)
            int r4 = (r3 > r15 ? 1 : (r3 == r15 ? 0 : -1))
            if (r4 < 0) goto L_0x0048
            r16 = 1065353216(0x3var_, float:1.0)
            goto L_0x004b
        L_0x0048:
            float r3 = r3 / r15
            r16 = r3
        L_0x004b:
            android.graphics.Rect r3 = r0.bounds
            int r6 = r3.centerX()
            android.graphics.Rect r3 = r0.bounds
            int r5 = r3.centerY()
            java.lang.String r3 = r0.backgroundColorKey
            r17 = 1132396544(0x437var_, float:255.0)
            r4 = 8
            r9 = 16777215(0xffffff, float:2.3509886E-38)
            r8 = 10
            r15 = 7
            r10 = 6
            if (r3 == 0) goto L_0x00ef
            boolean r14 = r0.drawUnchecked
            if (r14 == 0) goto L_0x00d6
            int r14 = r0.backgroundType
            if (r14 == r12) goto L_0x00b7
            if (r14 != r11) goto L_0x0071
            goto L_0x00b7
        L_0x0071:
            if (r14 == r10) goto L_0x009f
            if (r14 != r15) goto L_0x0076
            goto L_0x009f
        L_0x0076:
            if (r14 != r8) goto L_0x0085
            android.graphics.Paint r3 = r0.backgroundPaint
            java.lang.String r9 = r0.background2ColorKey
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r9)
            r3.setColor(r9)
            goto L_0x0140
        L_0x0085:
            android.graphics.Paint r3 = paint
            int r14 = org.telegram.ui.ActionBar.Theme.getServiceMessageColor()
            r9 = r9 & r14
            r14 = 671088640(0x28000000, float:7.1054274E-15)
            r9 = r9 | r14
            r3.setColor(r9)
            android.graphics.Paint r3 = r0.backgroundPaint
            java.lang.String r9 = r0.checkColorKey
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r9)
            r3.setColor(r9)
            goto L_0x0140
        L_0x009f:
            android.graphics.Paint r3 = paint
            java.lang.String r9 = r0.background2ColorKey
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r9)
            r3.setColor(r9)
            android.graphics.Paint r3 = r0.backgroundPaint
            java.lang.String r9 = r0.checkColorKey
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r9)
            r3.setColor(r9)
            goto L_0x0140
        L_0x00b7:
            android.graphics.Paint r9 = paint
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r9.setColor(r3)
            android.graphics.Paint r3 = paint
            float r9 = r0.backgroundAlpha
            float r9 = r9 * r17
            int r9 = (int) r9
            r3.setAlpha(r9)
            android.graphics.Paint r3 = r0.backgroundPaint
            java.lang.String r9 = r0.checkColorKey
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r9)
            r3.setColor(r9)
            goto L_0x0140
        L_0x00d6:
            android.graphics.Paint r3 = r0.backgroundPaint
            java.lang.String r14 = r0.background2ColorKey
            if (r14 == 0) goto L_0x00dd
            goto L_0x00df
        L_0x00dd:
            java.lang.String r14 = r0.checkColorKey
        L_0x00df:
            int r14 = org.telegram.ui.ActionBar.Theme.getColor(r14)
            float r15 = r0.progress
            float r10 = r0.backgroundAlpha
            int r9 = org.telegram.messenger.AndroidUtilities.getOffsetColor(r9, r14, r15, r10)
            r3.setColor(r9)
            goto L_0x0140
        L_0x00ef:
            boolean r3 = r0.drawUnchecked
            if (r3 == 0) goto L_0x0128
            android.graphics.Paint r3 = paint
            r9 = 1103626240(0x41CLASSNAME, float:25.0)
            float r10 = r0.backgroundAlpha
            float r10 = r10 * r9
            int r9 = (int) r10
            int r9 = android.graphics.Color.argb(r9, r2, r2, r2)
            r3.setColor(r9)
            int r3 = r0.backgroundType
            if (r3 != r4) goto L_0x0113
            android.graphics.Paint r3 = r0.backgroundPaint
            java.lang.String r9 = r0.background2ColorKey
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r9)
            r3.setColor(r9)
            goto L_0x0140
        L_0x0113:
            android.graphics.Paint r3 = r0.backgroundPaint
            r9 = -1
            java.lang.String r10 = r0.checkColorKey
            int r10 = org.telegram.ui.ActionBar.Theme.getColor(r10)
            float r14 = r0.progress
            float r15 = r0.backgroundAlpha
            int r9 = org.telegram.messenger.AndroidUtilities.getOffsetColor(r9, r10, r14, r15)
            r3.setColor(r9)
            goto L_0x0140
        L_0x0128:
            android.graphics.Paint r3 = r0.backgroundPaint
            java.lang.String r10 = r0.background2ColorKey
            if (r10 == 0) goto L_0x012f
            goto L_0x0131
        L_0x012f:
            java.lang.String r10 = r0.checkColorKey
        L_0x0131:
            int r10 = org.telegram.ui.ActionBar.Theme.getColor(r10)
            float r14 = r0.progress
            float r15 = r0.backgroundAlpha
            int r9 = org.telegram.messenger.AndroidUtilities.getOffsetColor(r9, r10, r14, r15)
            r3.setColor(r9)
        L_0x0140:
            boolean r3 = r0.drawUnchecked
            r9 = 1069547520(0x3fCLASSNAME, float:1.5)
            if (r3 == 0) goto L_0x018c
            int r3 = r0.backgroundType
            if (r3 == r12) goto L_0x018c
            if (r3 != r11) goto L_0x014d
            goto L_0x018c
        L_0x014d:
            if (r3 == r4) goto L_0x017e
            if (r3 != r8) goto L_0x0152
            goto L_0x017e
        L_0x0152:
            r10 = 6
            if (r3 == r10) goto L_0x0161
            r10 = 7
            if (r3 != r10) goto L_0x0159
            goto L_0x0161
        L_0x0159:
            float r3 = (float) r6
            float r10 = (float) r5
            android.graphics.Paint r14 = paint
            r7.drawCircle(r3, r10, r13, r14)
            goto L_0x018c
        L_0x0161:
            float r3 = (float) r6
            float r10 = (float) r5
            r14 = 1065353216(0x3var_, float:1.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r14)
            float r14 = (float) r15
            float r14 = r13 - r14
            android.graphics.Paint r15 = paint
            r7.drawCircle(r3, r10, r14, r15)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r9)
            float r14 = (float) r14
            float r14 = r13 - r14
            android.graphics.Paint r15 = r0.backgroundPaint
            r7.drawCircle(r3, r10, r14, r15)
            goto L_0x018c
        L_0x017e:
            float r3 = (float) r6
            float r10 = (float) r5
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r9)
            float r14 = (float) r14
            float r14 = r13 - r14
            android.graphics.Paint r15 = r0.backgroundPaint
            r7.drawCircle(r3, r10, r14, r15)
        L_0x018c:
            android.graphics.Paint r3 = paint
            java.lang.String r10 = r0.checkColorKey
            int r10 = org.telegram.ui.ActionBar.Theme.getColor(r10)
            r3.setColor(r10)
            int r3 = r0.backgroundType
            r10 = 0
            r14 = 9
            r15 = 1
            r9 = 0
            r2 = 7
            if (r3 == r2) goto L_0x02b6
            if (r3 == r4) goto L_0x02b6
            if (r3 == r14) goto L_0x02b6
            if (r3 == r8) goto L_0x02b6
            if (r3 == r12) goto L_0x0255
            if (r3 != r11) goto L_0x01ad
            goto L_0x0255
        L_0x01ad:
            if (r3 == 0) goto L_0x024b
            r2 = 11
            if (r3 != r2) goto L_0x01b5
            goto L_0x024b
        L_0x01b5:
            android.graphics.RectF r2 = r0.rect
            float r3 = (float) r6
            float r4 = r3 - r1
            float r11 = (float) r5
            float r12 = r11 - r1
            float r3 = r3 + r1
            float r11 = r11 + r1
            r2.set(r4, r12, r3, r11)
            int r1 = r0.backgroundType
            r2 = 6
            if (r1 != r2) goto L_0x01d2
            r2 = -1011613696(0xffffffffc3b40000, float:-360.0)
            float r3 = r0.progress
            float r3 = r3 * r2
            int r2 = (int) r3
            r12 = r2
            r2 = 6
            r11 = 0
            goto L_0x01ef
        L_0x01d2:
            if (r1 != r15) goto L_0x01e2
            r2 = -90
            r3 = -1014562816(0xffffffffCLASSNAME, float:-270.0)
            float r4 = r0.progress
            float r4 = r4 * r3
            int r3 = (int) r4
            r12 = r3
            r2 = 6
            r11 = -90
            goto L_0x01ef
        L_0x01e2:
            r2 = 90
            r3 = 1132920832(0x43870000, float:270.0)
            float r4 = r0.progress
            float r4 = r4 * r3
            int r3 = (int) r4
            r12 = r3
            r2 = 6
            r11 = 90
        L_0x01ef:
            if (r1 != r2) goto L_0x023b
            java.lang.String r1 = "dialogBackground"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            int r2 = android.graphics.Color.alpha(r1)
            android.graphics.Paint r3 = r0.backgroundPaint
            r3.setColor(r1)
            android.graphics.Paint r1 = r0.backgroundPaint
            float r2 = (float) r2
            float r3 = r0.progress
            float r2 = r2 * r3
            int r2 = (int) r2
            r1.setAlpha(r2)
            android.graphics.RectF r2 = r0.rect
            float r3 = (float) r11
            float r4 = (float) r12
            r18 = 0
            android.graphics.Paint r1 = r0.backgroundPaint
            r19 = r1
            r1 = r21
            r15 = r5
            r5 = r18
            r8 = r6
            r6 = r19
            r1.drawArc(r2, r3, r4, r5, r6)
            java.lang.String r1 = "chat_attachPhotoBackground"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            int r2 = android.graphics.Color.alpha(r1)
            android.graphics.Paint r3 = r0.backgroundPaint
            r3.setColor(r1)
            android.graphics.Paint r1 = r0.backgroundPaint
            float r2 = (float) r2
            float r3 = r0.progress
            float r2 = r2 * r3
            int r2 = (int) r2
            r1.setAlpha(r2)
            goto L_0x023d
        L_0x023b:
            r15 = r5
            r8 = r6
        L_0x023d:
            android.graphics.RectF r2 = r0.rect
            float r3 = (float) r11
            float r4 = (float) r12
            r5 = 0
            android.graphics.Paint r6 = r0.backgroundPaint
            r1 = r21
            r1.drawArc(r2, r3, r4, r5, r6)
            goto L_0x02b8
        L_0x024b:
            r15 = r5
            r8 = r6
            float r1 = (float) r8
            float r2 = (float) r15
            android.graphics.Paint r3 = r0.backgroundPaint
            r7.drawCircle(r1, r2, r13, r3)
            goto L_0x02b8
        L_0x0255:
            r15 = r5
            r8 = r6
            android.graphics.Paint r1 = r0.backgroundPaint
            android.graphics.Paint$Style r2 = android.graphics.Paint.Style.FILL
            r1.setStyle(r2)
            org.telegram.ui.ActionBar.Theme$MessageDrawable r1 = r0.messageDrawable
            if (r1 == 0) goto L_0x0295
            boolean r1 = r1.hasGradient()
            if (r1 == 0) goto L_0x0295
            org.telegram.ui.ActionBar.Theme$MessageDrawable r1 = r0.messageDrawable
            android.graphics.Shader r1 = r1.getGradientShader()
            org.telegram.ui.ActionBar.Theme$MessageDrawable r2 = r0.messageDrawable
            android.graphics.Matrix r2 = r2.getMatrix()
            r2.reset()
            org.telegram.ui.ActionBar.Theme$MessageDrawable r3 = r0.messageDrawable
            r3.applyMatrixScale()
            org.telegram.ui.ActionBar.Theme$MessageDrawable r3 = r0.messageDrawable
            int r3 = r3.getTopY()
            int r3 = -r3
            android.graphics.Rect r4 = r0.bounds
            int r4 = r4.top
            int r3 = r3 + r4
            float r3 = (float) r3
            r2.postTranslate(r9, r3)
            r1.setLocalMatrix(r2)
            android.graphics.Paint r2 = r0.backgroundPaint
            r2.setShader(r1)
            goto L_0x029a
        L_0x0295:
            android.graphics.Paint r1 = r0.backgroundPaint
            r1.setShader(r10)
        L_0x029a:
            float r1 = (float) r8
            float r2 = (float) r15
            r3 = 1065353216(0x3var_, float:1.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r4
            float r3 = r13 - r3
            float r4 = r0.backgroundAlpha
            float r3 = r3 * r4
            android.graphics.Paint r4 = r0.backgroundPaint
            r7.drawCircle(r1, r2, r3, r4)
            android.graphics.Paint r1 = r0.backgroundPaint
            android.graphics.Paint$Style r2 = android.graphics.Paint.Style.STROKE
            r1.setStyle(r2)
            goto L_0x02b8
        L_0x02b6:
            r15 = r5
            r8 = r6
        L_0x02b8:
            int r1 = (r16 > r9 ? 1 : (r16 == r9 ? 0 : -1))
            if (r1 <= 0) goto L_0x048a
            float r1 = r0.progress
            r2 = 1056964608(0x3var_, float:0.5)
            int r3 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r3 >= 0) goto L_0x02c6
            r1 = 0
            goto L_0x02c8
        L_0x02c6:
            float r1 = r1 - r2
            float r1 = r1 / r2
        L_0x02c8:
            int r2 = r0.backgroundType
            if (r2 != r14) goto L_0x02d8
            android.graphics.Paint r2 = paint
            java.lang.String r3 = r0.background2ColorKey
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r2.setColor(r3)
            goto L_0x030d
        L_0x02d8:
            r3 = 11
            if (r2 == r3) goto L_0x0302
            r3 = 6
            if (r2 == r3) goto L_0x0302
            r3 = 7
            if (r2 == r3) goto L_0x0302
            r3 = 10
            if (r2 == r3) goto L_0x0302
            boolean r2 = r0.drawUnchecked
            if (r2 != 0) goto L_0x02ef
            java.lang.String r2 = r0.backgroundColorKey
            if (r2 == 0) goto L_0x02ef
            goto L_0x0302
        L_0x02ef:
            android.graphics.Paint r2 = paint
            boolean r3 = r0.enabled
            if (r3 == 0) goto L_0x02f8
            java.lang.String r3 = "checkbox"
            goto L_0x02fa
        L_0x02f8:
            java.lang.String r3 = "checkboxDisabled"
        L_0x02fa:
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r2.setColor(r3)
            goto L_0x030d
        L_0x0302:
            android.graphics.Paint r2 = paint
            java.lang.String r3 = r0.backgroundColorKey
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r2.setColor(r3)
        L_0x030d:
            boolean r2 = r0.useDefaultCheck
            if (r2 != 0) goto L_0x031f
            java.lang.String r2 = r0.checkColorKey
            if (r2 == 0) goto L_0x031f
            android.graphics.Paint r3 = r0.checkPaint
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r3.setColor(r2)
            goto L_0x032a
        L_0x031f:
            android.graphics.Paint r2 = r0.checkPaint
            java.lang.String r3 = "checkboxCheck"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r2.setColor(r3)
        L_0x032a:
            int r2 = r0.backgroundType
            r3 = 2
            r4 = 12
            if (r2 == r4) goto L_0x0373
            r4 = 13
            if (r2 != r4) goto L_0x0336
            goto L_0x0373
        L_0x0336:
            r2 = 1056964608(0x3var_, float:0.5)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r2 = (float) r2
            float r13 = r13 - r2
            android.graphics.Canvas r2 = r0.bitmapCanvas
            android.graphics.Bitmap r4 = r0.drawBitmap
            int r4 = r4.getWidth()
            int r4 = r4 / r3
            float r4 = (float) r4
            android.graphics.Bitmap r5 = r0.drawBitmap
            int r5 = r5.getHeight()
            int r5 = r5 / r3
            float r5 = (float) r5
            android.graphics.Paint r6 = paint
            r2.drawCircle(r4, r5, r13, r6)
            android.graphics.Canvas r2 = r0.bitmapCanvas
            android.graphics.Bitmap r4 = r0.drawBitmap
            int r4 = r4.getWidth()
            int r4 = r4 / r3
            float r4 = (float) r4
            android.graphics.Bitmap r5 = r0.drawBitmap
            int r5 = r5.getHeight()
            int r5 = r5 / r3
            float r5 = (float) r5
            r6 = 1065353216(0x3var_, float:1.0)
            float r14 = r6 - r16
            float r13 = r13 * r14
            android.graphics.Paint r6 = eraser
            r2.drawCircle(r4, r5, r13, r6)
            goto L_0x0394
        L_0x0373:
            android.graphics.Paint r2 = paint
            float r4 = r16 * r17
            int r4 = (int) r4
            r2.setAlpha(r4)
            android.graphics.Canvas r2 = r0.bitmapCanvas
            android.graphics.Bitmap r4 = r0.drawBitmap
            int r4 = r4.getWidth()
            int r4 = r4 / r3
            float r4 = (float) r4
            android.graphics.Bitmap r5 = r0.drawBitmap
            int r5 = r5.getHeight()
            int r5 = r5 / r3
            float r5 = (float) r5
            float r13 = r13 * r16
            android.graphics.Paint r6 = paint
            r2.drawCircle(r4, r5, r13, r6)
        L_0x0394:
            android.graphics.Bitmap r2 = r0.drawBitmap
            int r4 = r2.getWidth()
            int r4 = r4 / r3
            int r6 = r8 - r4
            float r4 = (float) r6
            android.graphics.Bitmap r5 = r0.drawBitmap
            int r5 = r5.getHeight()
            int r5 = r5 / r3
            int r5 = r15 - r5
            float r5 = (float) r5
            r7.drawBitmap(r2, r4, r5, r10)
            int r2 = (r1 > r9 ? 1 : (r1 == r9 ? 0 : -1))
            if (r2 == 0) goto L_0x048a
            java.lang.String r2 = r0.checkedText
            if (r2 == 0) goto L_0x0420
            android.text.TextPaint r2 = r0.textPaint
            if (r2 != 0) goto L_0x03c9
            android.text.TextPaint r2 = new android.text.TextPaint
            r4 = 1
            r2.<init>(r4)
            r0.textPaint = r2
            java.lang.String r5 = "fonts/rmedium.ttf"
            android.graphics.Typeface r5 = org.telegram.messenger.AndroidUtilities.getTypeface(r5)
            r2.setTypeface(r5)
            goto L_0x03ca
        L_0x03c9:
            r4 = 1
        L_0x03ca:
            java.lang.String r2 = r0.checkedText
            int r2 = r2.length()
            if (r2 == 0) goto L_0x03e3
            if (r2 == r4) goto L_0x03e3
            if (r2 == r3) goto L_0x03e3
            r3 = 3
            if (r2 == r3) goto L_0x03de
            r9 = 1090519040(0x41000000, float:8.0)
            r2 = 1098645504(0x417CLASSNAME, float:15.75)
            goto L_0x03e7
        L_0x03de:
            r2 = 1099169792(0x41840000, float:16.5)
            r9 = 1092616192(0x41200000, float:10.0)
            goto L_0x03e7
        L_0x03e3:
            r9 = 1096810496(0x41600000, float:14.0)
            r2 = 1099956224(0x41900000, float:18.0)
        L_0x03e7:
            android.text.TextPaint r3 = r0.textPaint
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r9)
            float r4 = (float) r4
            r3.setTextSize(r4)
            android.text.TextPaint r3 = r0.textPaint
            java.lang.String r4 = r0.checkColorKey
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            r3.setColor(r4)
            r21.save()
            float r3 = (float) r8
            float r4 = (float) r15
            r5 = 1065353216(0x3var_, float:1.0)
            r7.scale(r1, r5, r3, r4)
            java.lang.String r1 = r0.checkedText
            android.text.TextPaint r4 = r0.textPaint
            float r4 = r4.measureText(r1)
            r5 = 1073741824(0x40000000, float:2.0)
            float r4 = r4 / r5
            float r3 = r3 - r4
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r2 = (float) r2
            android.text.TextPaint r4 = r0.textPaint
            r7.drawText(r1, r3, r2, r4)
            r21.restore()
            goto L_0x048a
        L_0x0420:
            r5 = 1065353216(0x3var_, float:1.0)
            android.graphics.Path r2 = r0.path
            r2.reset()
            int r2 = r0.backgroundType
            r3 = 5
            if (r2 != r3) goto L_0x0430
            r14 = 1061997773(0x3f4ccccd, float:0.8)
            goto L_0x0432
        L_0x0430:
            r14 = 1065353216(0x3var_, float:1.0)
        L_0x0432:
            r2 = 1091567616(0x41100000, float:9.0)
            float r2 = r2 * r14
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r2 = (float) r2
            float r2 = r2 * r1
            r3 = 1082130432(0x40800000, float:4.0)
            float r14 = r14 * r3
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r14)
            float r4 = (float) r4
            float r4 = r4 * r1
            r1 = 1069547520(0x3fCLASSNAME, float:1.5)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r6 = r8 - r1
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r5 = r15 + r1
            float r4 = r4 * r4
            r1 = 1073741824(0x40000000, float:2.0)
            float r4 = r4 / r1
            double r3 = (double) r4
            double r3 = java.lang.Math.sqrt(r3)
            float r1 = (float) r3
            android.graphics.Path r3 = r0.path
            float r4 = (float) r6
            float r6 = r4 - r1
            float r5 = (float) r5
            float r1 = r5 - r1
            r3.moveTo(r6, r1)
            android.graphics.Path r1 = r0.path
            r1.lineTo(r4, r5)
            float r2 = r2 * r2
            r1 = 1073741824(0x40000000, float:2.0)
            float r2 = r2 / r1
            double r1 = (double) r2
            double r1 = java.lang.Math.sqrt(r1)
            float r1 = (float) r1
            android.graphics.Path r2 = r0.path
            float r4 = r4 + r1
            float r5 = r5 - r1
            r2.lineTo(r4, r5)
            android.graphics.Path r1 = r0.path
            android.graphics.Paint r2 = r0.checkPaint
            r7.drawPath(r1, r2)
        L_0x048a:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.CheckBoxBase.draw(android.graphics.Canvas):void");
    }
}
