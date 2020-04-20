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

public class CheckBoxBase {
    private static Paint eraser;
    private static Paint paint;
    private boolean attachedToWindow;
    private String background2ColorKey = "chat_serviceBackground";
    private float backgroundAlpha = 1.0f;
    private String backgroundColorKey = "chat_serviceBackground";
    private Paint backgroundPaint;
    private Canvas bitmapCanvas;
    private Rect bounds = new Rect();
    /* access modifiers changed from: private */
    public ObjectAnimator checkAnimator;
    private String checkColorKey = "checkboxCheck";
    private Paint checkPaint;
    /* access modifiers changed from: private */
    public String checkedText;
    private int drawBackgroundAsArc;
    private Bitmap drawBitmap;
    private boolean drawUnchecked = true;
    private boolean enabled = true;
    /* access modifiers changed from: private */
    public boolean isChecked;
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

    public void setDrawBackgroundAsArc(int i) {
        this.drawBackgroundAsArc = i;
        if (i == 4 || i == 5) {
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

    /* JADX WARNING: Removed duplicated region for block: B:119:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x0214  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void draw(android.graphics.Canvas r22) {
        /*
            r21 = this;
            r0 = r21
            r7 = r22
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
            float r9 = (float) r1
            int r1 = r0.drawBackgroundAsArc
            if (r1 == 0) goto L_0x0026
            r1 = 1045220557(0x3e4ccccd, float:0.2)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            float r1 = r9 - r1
            goto L_0x0027
        L_0x0026:
            r1 = r9
        L_0x0027:
            float r3 = r0.progress
            r11 = 1056964608(0x3var_, float:0.5)
            int r4 = (r3 > r11 ? 1 : (r3 == r11 ? 0 : -1))
            if (r4 < 0) goto L_0x0032
            r12 = 1065353216(0x3var_, float:1.0)
            goto L_0x0034
        L_0x0032:
            float r3 = r3 / r11
            r12 = r3
        L_0x0034:
            android.graphics.Rect r3 = r0.bounds
            int r13 = r3.centerX()
            android.graphics.Rect r3 = r0.bounds
            int r14 = r3.centerY()
            java.lang.String r3 = r0.backgroundColorKey
            r4 = 8
            r5 = 16777215(0xffffff, float:2.3509886E-38)
            r15 = 10
            r6 = 7
            r8 = 6
            if (r3 == 0) goto L_0x00b3
            boolean r3 = r0.drawUnchecked
            if (r3 == 0) goto L_0x009a
            int r3 = r0.drawBackgroundAsArc
            if (r3 == r8) goto L_0x0083
            if (r3 != r6) goto L_0x0058
            goto L_0x0083
        L_0x0058:
            if (r3 != r15) goto L_0x0067
            android.graphics.Paint r3 = r0.backgroundPaint
            java.lang.String r5 = r0.background2ColorKey
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r3.setColor(r5)
            goto L_0x0104
        L_0x0067:
            android.graphics.Paint r3 = paint
            int r16 = org.telegram.ui.ActionBar.Theme.getServiceMessageColor()
            r5 = r16 & r5
            r16 = 671088640(0x28000000, float:7.1054274E-15)
            r5 = r5 | r16
            r3.setColor(r5)
            android.graphics.Paint r3 = r0.backgroundPaint
            java.lang.String r5 = r0.checkColorKey
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r3.setColor(r5)
            goto L_0x0104
        L_0x0083:
            android.graphics.Paint r3 = paint
            java.lang.String r5 = r0.background2ColorKey
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r3.setColor(r5)
            android.graphics.Paint r3 = r0.backgroundPaint
            java.lang.String r5 = r0.checkColorKey
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r3.setColor(r5)
            goto L_0x0104
        L_0x009a:
            android.graphics.Paint r3 = r0.backgroundPaint
            java.lang.String r11 = r0.background2ColorKey
            if (r11 == 0) goto L_0x00a1
            goto L_0x00a3
        L_0x00a1:
            java.lang.String r11 = r0.checkColorKey
        L_0x00a3:
            int r11 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            float r10 = r0.progress
            float r6 = r0.backgroundAlpha
            int r5 = org.telegram.messenger.AndroidUtilities.getOffsetColor(r5, r11, r10, r6)
            r3.setColor(r5)
            goto L_0x0104
        L_0x00b3:
            boolean r3 = r0.drawUnchecked
            if (r3 == 0) goto L_0x00ec
            android.graphics.Paint r3 = paint
            r5 = 1103626240(0x41CLASSNAME, float:25.0)
            float r6 = r0.backgroundAlpha
            float r6 = r6 * r5
            int r5 = (int) r6
            int r5 = android.graphics.Color.argb(r5, r2, r2, r2)
            r3.setColor(r5)
            int r3 = r0.drawBackgroundAsArc
            if (r3 != r4) goto L_0x00d7
            android.graphics.Paint r3 = r0.backgroundPaint
            java.lang.String r5 = r0.background2ColorKey
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r3.setColor(r5)
            goto L_0x0104
        L_0x00d7:
            android.graphics.Paint r3 = r0.backgroundPaint
            r5 = -1
            java.lang.String r6 = r0.checkColorKey
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            float r10 = r0.progress
            float r11 = r0.backgroundAlpha
            int r5 = org.telegram.messenger.AndroidUtilities.getOffsetColor(r5, r6, r10, r11)
            r3.setColor(r5)
            goto L_0x0104
        L_0x00ec:
            android.graphics.Paint r3 = r0.backgroundPaint
            java.lang.String r6 = r0.background2ColorKey
            if (r6 == 0) goto L_0x00f3
            goto L_0x00f5
        L_0x00f3:
            java.lang.String r6 = r0.checkColorKey
        L_0x00f5:
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            float r10 = r0.progress
            float r11 = r0.backgroundAlpha
            int r5 = org.telegram.messenger.AndroidUtilities.getOffsetColor(r5, r6, r10, r11)
            r3.setColor(r5)
        L_0x0104:
            boolean r3 = r0.drawUnchecked
            r10 = 1069547520(0x3fCLASSNAME, float:1.5)
            if (r3 == 0) goto L_0x014a
            int r3 = r0.drawBackgroundAsArc
            if (r3 == r4) goto L_0x013c
            if (r3 != r15) goto L_0x0111
            goto L_0x013c
        L_0x0111:
            if (r3 == r8) goto L_0x011f
            r5 = 7
            if (r3 != r5) goto L_0x0117
            goto L_0x011f
        L_0x0117:
            float r3 = (float) r13
            float r5 = (float) r14
            android.graphics.Paint r6 = paint
            r7.drawCircle(r3, r5, r9, r6)
            goto L_0x014a
        L_0x011f:
            float r3 = (float) r13
            float r5 = (float) r14
            r6 = 1065353216(0x3var_, float:1.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r6 = (float) r11
            float r6 = r9 - r6
            android.graphics.Paint r11 = paint
            r7.drawCircle(r3, r5, r6, r11)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r10)
            float r6 = (float) r6
            float r6 = r9 - r6
            android.graphics.Paint r11 = r0.backgroundPaint
            r7.drawCircle(r3, r5, r6, r11)
            goto L_0x014a
        L_0x013c:
            float r3 = (float) r13
            float r5 = (float) r14
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r10)
            float r6 = (float) r6
            float r6 = r9 - r6
            android.graphics.Paint r11 = r0.backgroundPaint
            r7.drawCircle(r3, r5, r6, r11)
        L_0x014a:
            android.graphics.Paint r3 = paint
            java.lang.String r5 = r0.checkColorKey
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r3.setColor(r5)
            int r3 = r0.drawBackgroundAsArc
            r11 = 9
            r6 = 1
            r5 = 7
            if (r3 == r5) goto L_0x020e
            if (r3 == r4) goto L_0x020e
            if (r3 == r11) goto L_0x020e
            if (r3 == r15) goto L_0x020e
            if (r3 != 0) goto L_0x016e
            float r1 = (float) r13
            float r2 = (float) r14
            android.graphics.Paint r3 = r0.backgroundPaint
            r7.drawCircle(r1, r2, r9, r3)
            goto L_0x020e
        L_0x016e:
            android.graphics.RectF r3 = r0.rect
            float r4 = (float) r13
            float r2 = r4 - r1
            float r5 = (float) r14
            float r10 = r5 - r1
            float r4 = r4 + r1
            float r5 = r5 + r1
            r3.set(r2, r10, r4, r5)
            int r1 = r0.drawBackgroundAsArc
            if (r1 != r8) goto L_0x0188
            r1 = -1011613696(0xffffffffc3b40000, float:-360.0)
            float r2 = r0.progress
            float r2 = r2 * r1
            int r1 = (int) r2
            r2 = 0
            goto L_0x019a
        L_0x0188:
            if (r1 != r6) goto L_0x0191
            r2 = -90
            r1 = -1014562816(0xffffffffCLASSNAME, float:-270.0)
            float r3 = r0.progress
            goto L_0x0197
        L_0x0191:
            r2 = 90
            r1 = 1132920832(0x43870000, float:270.0)
            float r3 = r0.progress
        L_0x0197:
            float r3 = r3 * r1
            int r1 = (int) r3
        L_0x019a:
            int r3 = r0.drawBackgroundAsArc
            if (r3 != r8) goto L_0x01fb
            java.lang.String r3 = "dialogBackground"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            int r4 = android.graphics.Color.alpha(r3)
            android.graphics.Paint r5 = r0.backgroundPaint
            r5.setColor(r3)
            android.graphics.Paint r3 = r0.backgroundPaint
            float r4 = (float) r4
            float r5 = r0.progress
            float r4 = r4 * r5
            int r4 = (int) r4
            r3.setAlpha(r4)
            android.graphics.RectF r3 = r0.rect
            float r10 = (float) r2
            float r5 = (float) r1
            r17 = 0
            android.graphics.Paint r4 = r0.backgroundPaint
            r1 = r22
            r2 = r3
            r3 = r10
            r19 = r4
            r4 = r5
            r18 = r5
            r20 = 7
            r5 = r17
            r15 = 7
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
            android.graphics.RectF r2 = r0.rect
            r5 = 0
            android.graphics.Paint r6 = r0.backgroundPaint
            r1 = r22
            r3 = r10
            r4 = r18
            r1.drawArc(r2, r3, r4, r5, r6)
            goto L_0x020f
        L_0x01fb:
            r15 = 7
            android.graphics.RectF r3 = r0.rect
            float r4 = (float) r2
            float r5 = (float) r1
            r6 = 0
            android.graphics.Paint r10 = r0.backgroundPaint
            r1 = r22
            r2 = r3
            r3 = r4
            r4 = r5
            r5 = r6
            r6 = r10
            r1.drawArc(r2, r3, r4, r5, r6)
            goto L_0x020f
        L_0x020e:
            r15 = 7
        L_0x020f:
            r1 = 0
            int r2 = (r12 > r1 ? 1 : (r12 == r1 ? 0 : -1))
            if (r2 <= 0) goto L_0x03ae
            float r2 = r0.progress
            r3 = 1056964608(0x3var_, float:0.5)
            int r4 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r4 >= 0) goto L_0x021e
            r2 = 0
            goto L_0x0220
        L_0x021e:
            float r2 = r2 - r3
            float r2 = r2 / r3
        L_0x0220:
            int r3 = r0.drawBackgroundAsArc
            if (r3 != r11) goto L_0x0230
            android.graphics.Paint r3 = paint
            java.lang.String r4 = r0.background2ColorKey
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            r3.setColor(r4)
            goto L_0x025f
        L_0x0230:
            if (r3 == r8) goto L_0x0254
            if (r3 == r15) goto L_0x0254
            r4 = 10
            if (r3 == r4) goto L_0x0254
            boolean r3 = r0.drawUnchecked
            if (r3 != 0) goto L_0x0241
            java.lang.String r3 = r0.backgroundColorKey
            if (r3 == 0) goto L_0x0241
            goto L_0x0254
        L_0x0241:
            android.graphics.Paint r3 = paint
            boolean r4 = r0.enabled
            if (r4 == 0) goto L_0x024a
            java.lang.String r4 = "checkbox"
            goto L_0x024c
        L_0x024a:
            java.lang.String r4 = "checkboxDisabled"
        L_0x024c:
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            r3.setColor(r4)
            goto L_0x025f
        L_0x0254:
            android.graphics.Paint r3 = paint
            java.lang.String r4 = r0.backgroundColorKey
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            r3.setColor(r4)
        L_0x025f:
            boolean r3 = r0.useDefaultCheck
            if (r3 != 0) goto L_0x0271
            java.lang.String r3 = r0.checkColorKey
            if (r3 == 0) goto L_0x0271
            android.graphics.Paint r4 = r0.checkPaint
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r4.setColor(r3)
            goto L_0x027c
        L_0x0271:
            android.graphics.Paint r3 = r0.checkPaint
            java.lang.String r4 = "checkboxCheck"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            r3.setColor(r4)
        L_0x027c:
            r3 = 1056964608(0x3var_, float:0.5)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r3
            float r9 = r9 - r3
            android.graphics.Canvas r3 = r0.bitmapCanvas
            android.graphics.Bitmap r4 = r0.drawBitmap
            int r4 = r4.getWidth()
            r5 = 2
            int r4 = r4 / r5
            float r4 = (float) r4
            android.graphics.Bitmap r6 = r0.drawBitmap
            int r6 = r6.getHeight()
            int r6 = r6 / r5
            float r6 = (float) r6
            android.graphics.Paint r8 = paint
            r3.drawCircle(r4, r6, r9, r8)
            android.graphics.Canvas r3 = r0.bitmapCanvas
            android.graphics.Bitmap r4 = r0.drawBitmap
            int r4 = r4.getWidth()
            int r4 = r4 / r5
            float r4 = (float) r4
            android.graphics.Bitmap r6 = r0.drawBitmap
            int r6 = r6.getWidth()
            int r6 = r6 / r5
            float r6 = (float) r6
            r8 = 1065353216(0x3var_, float:1.0)
            float r10 = r8 - r12
            float r9 = r9 * r10
            android.graphics.Paint r8 = eraser
            r3.drawCircle(r4, r6, r9, r8)
            android.graphics.Bitmap r3 = r0.drawBitmap
            int r4 = r3.getWidth()
            int r4 = r4 / r5
            int r4 = r13 - r4
            float r4 = (float) r4
            android.graphics.Bitmap r6 = r0.drawBitmap
            int r6 = r6.getHeight()
            int r6 = r6 / r5
            int r6 = r14 - r6
            float r6 = (float) r6
            r8 = 0
            r7.drawBitmap(r3, r4, r6, r8)
            int r1 = (r2 > r1 ? 1 : (r2 == r1 ? 0 : -1))
            if (r1 == 0) goto L_0x03ae
            java.lang.String r1 = r0.checkedText
            if (r1 == 0) goto L_0x0346
            android.text.TextPaint r1 = r0.textPaint
            if (r1 != 0) goto L_0x02ef
            android.text.TextPaint r1 = new android.text.TextPaint
            r3 = 1
            r1.<init>(r3)
            r0.textPaint = r1
            java.lang.String r4 = "fonts/rmedium.ttf"
            android.graphics.Typeface r4 = org.telegram.messenger.AndroidUtilities.getTypeface(r4)
            r1.setTypeface(r4)
            goto L_0x02f0
        L_0x02ef:
            r3 = 1
        L_0x02f0:
            java.lang.String r1 = r0.checkedText
            int r1 = r1.length()
            if (r1 == 0) goto L_0x0309
            if (r1 == r3) goto L_0x0309
            if (r1 == r5) goto L_0x0309
            r3 = 3
            if (r1 == r3) goto L_0x0304
            r1 = 1090519040(0x41000000, float:8.0)
            r3 = 1098645504(0x417CLASSNAME, float:15.75)
            goto L_0x030d
        L_0x0304:
            r1 = 1092616192(0x41200000, float:10.0)
            r3 = 1099169792(0x41840000, float:16.5)
            goto L_0x030d
        L_0x0309:
            r1 = 1096810496(0x41600000, float:14.0)
            r3 = 1099956224(0x41900000, float:18.0)
        L_0x030d:
            android.text.TextPaint r4 = r0.textPaint
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            r4.setTextSize(r1)
            android.text.TextPaint r1 = r0.textPaint
            java.lang.String r4 = r0.checkColorKey
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            r1.setColor(r4)
            r22.save()
            float r1 = (float) r13
            float r4 = (float) r14
            r5 = 1065353216(0x3var_, float:1.0)
            r7.scale(r2, r5, r1, r4)
            java.lang.String r2 = r0.checkedText
            android.text.TextPaint r4 = r0.textPaint
            float r4 = r4.measureText(r2)
            r5 = 1073741824(0x40000000, float:2.0)
            float r4 = r4 / r5
            float r1 = r1 - r4
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r3
            android.text.TextPaint r4 = r0.textPaint
            r7.drawText(r2, r1, r3, r4)
            r22.restore()
            goto L_0x03ae
        L_0x0346:
            r5 = 1065353216(0x3var_, float:1.0)
            android.graphics.Path r1 = r0.path
            r1.reset()
            int r1 = r0.drawBackgroundAsArc
            r3 = 5
            if (r1 != r3) goto L_0x0356
            r10 = 1061997773(0x3f4ccccd, float:0.8)
            goto L_0x0358
        L_0x0356:
            r10 = 1065353216(0x3var_, float:1.0)
        L_0x0358:
            r1 = 1091567616(0x41100000, float:9.0)
            float r1 = r1 * r10
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            float r1 = r1 * r2
            r3 = 1082130432(0x40800000, float:4.0)
            float r10 = r10 * r3
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r10)
            float r4 = (float) r4
            float r4 = r4 * r2
            r2 = 1069547520(0x3fCLASSNAME, float:1.5)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r13 = r13 - r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r14 = r14 + r2
            float r4 = r4 * r4
            r2 = 1073741824(0x40000000, float:2.0)
            float r4 = r4 / r2
            double r2 = (double) r4
            double r2 = java.lang.Math.sqrt(r2)
            float r2 = (float) r2
            android.graphics.Path r3 = r0.path
            float r4 = (float) r13
            float r5 = r4 - r2
            float r6 = (float) r14
            float r2 = r6 - r2
            r3.moveTo(r5, r2)
            android.graphics.Path r2 = r0.path
            r2.lineTo(r4, r6)
            float r1 = r1 * r1
            r2 = 1073741824(0x40000000, float:2.0)
            float r1 = r1 / r2
            double r1 = (double) r1
            double r1 = java.lang.Math.sqrt(r1)
            float r1 = (float) r1
            android.graphics.Path r2 = r0.path
            float r4 = r4 + r1
            float r6 = r6 - r1
            r2.lineTo(r4, r6)
            android.graphics.Path r1 = r0.path
            android.graphics.Paint r2 = r0.checkPaint
            r7.drawPath(r1, r2)
        L_0x03ae:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.CheckBoxBase.draw(android.graphics.Canvas):void");
    }
}
