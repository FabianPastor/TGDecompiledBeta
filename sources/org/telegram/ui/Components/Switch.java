package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build.VERSION;
import android.util.StateSet;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import androidx.annotation.Keep;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;

public class Switch extends View {
    private boolean attachedToWindow;
    private boolean bitmapsCreated;
    private ObjectAnimator checkAnimator;
    private int colorSet;
    private int drawIconType;
    private boolean drawRipple;
    private ObjectAnimator iconAnimator;
    private Drawable iconDrawable;
    private float iconProgress = 1.0f;
    private boolean isChecked;
    private int lastIconColor;
    private OnCheckedChangeListener onCheckedChangeListener;
    private Bitmap[] overlayBitmap;
    private Canvas[] overlayCanvas;
    private float overlayCx;
    private float overlayCy;
    private Paint overlayEraserPaint;
    private Bitmap overlayMaskBitmap;
    private Canvas overlayMaskCanvas;
    private Paint overlayMaskPaint;
    private float overlayRad;
    private int overrideColorProgress;
    private Paint paint;
    private Paint paint2;
    private int[] pressedState;
    private float progress;
    private RectF rectF;
    private RippleDrawable rippleDrawable;
    private Paint ripplePaint;
    private String thumbCheckedColorKey;
    private String thumbColorKey;
    private String trackCheckedColorKey = "switch2TrackChecked";
    private String trackColorKey = "switch2Track";

    public interface OnCheckedChangeListener {
        void onCheckedChanged(Switch switchR, boolean z);
    }

    public Switch(Context context) {
        super(context);
        String str = "windowBackgroundWhite";
        this.thumbColorKey = str;
        this.thumbCheckedColorKey = str;
        this.pressedState = new int[]{16842910, 16842919};
        this.rectF = new RectF();
        this.paint = new Paint(1);
        this.paint2 = new Paint(1);
        this.paint2.setStyle(Style.STROKE);
        this.paint2.setStrokeCap(Cap.ROUND);
        this.paint2.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
    }

    @Keep
    public void setProgress(float f) {
        if (this.progress != f) {
            this.progress = f;
            invalidate();
        }
    }

    @Keep
    public float getProgress() {
        return this.progress;
    }

    @Keep
    public void setIconProgress(float f) {
        if (this.iconProgress != f) {
            this.iconProgress = f;
            invalidate();
        }
    }

    @Keep
    public float getIconProgress() {
        return this.iconProgress;
    }

    private void cancelCheckAnimator() {
        ObjectAnimator objectAnimator = this.checkAnimator;
        if (objectAnimator != null) {
            objectAnimator.cancel();
            this.checkAnimator = null;
        }
    }

    private void cancelIconAnimator() {
        ObjectAnimator objectAnimator = this.iconAnimator;
        if (objectAnimator != null) {
            objectAnimator.cancel();
            this.iconAnimator = null;
        }
    }

    public void setDrawIconType(int i) {
        this.drawIconType = i;
    }

    public void setDrawRipple(boolean z) {
        if (VERSION.SDK_INT >= 21 && z != this.drawRipple) {
            this.drawRipple = z;
            int i = 1;
            if (this.rippleDrawable == null) {
                Drawable drawable;
                this.ripplePaint = new Paint(1);
                this.ripplePaint.setColor(-1);
                if (VERSION.SDK_INT >= 23) {
                    drawable = null;
                } else {
                    drawable = new Drawable() {
                        public int getOpacity() {
                            return 0;
                        }

                        public void setAlpha(int i) {
                        }

                        public void setColorFilter(ColorFilter colorFilter) {
                        }

                        public void draw(Canvas canvas) {
                            Rect bounds = getBounds();
                            canvas.drawCircle((float) bounds.centerX(), (float) bounds.centerY(), (float) AndroidUtilities.dp(18.0f), Switch.this.ripplePaint);
                        }
                    };
                }
                this.rippleDrawable = new RippleDrawable(new ColorStateList(new int[][]{StateSet.WILD_CARD}, new int[]{0}), null, drawable);
                if (VERSION.SDK_INT >= 23) {
                    this.rippleDrawable.setRadius(AndroidUtilities.dp(18.0f));
                }
                this.rippleDrawable.setCallback(this);
            }
            if ((this.isChecked && this.colorSet != 2) || !(this.isChecked || this.colorSet == 1)) {
                int color = Theme.getColor(this.isChecked ? "switchTrackBlueSelectorChecked" : "switchTrackBlueSelector");
                this.rippleDrawable.setColor(new ColorStateList(new int[][]{StateSet.WILD_CARD}, new int[]{color}));
                if (this.isChecked) {
                    i = 2;
                }
                this.colorSet = i;
            }
            if (VERSION.SDK_INT >= 28 && z) {
                this.rippleDrawable.setHotspot(this.isChecked ? 0.0f : (float) AndroidUtilities.dp(100.0f), (float) AndroidUtilities.dp(18.0f));
            }
            this.rippleDrawable.setState(z ? this.pressedState : StateSet.NOTHING);
            invalidate();
        }
    }

    /* Access modifiers changed, original: protected */
    public boolean verifyDrawable(Drawable drawable) {
        if (!super.verifyDrawable(drawable)) {
            Drawable drawable2 = this.rippleDrawable;
            if (drawable2 == null || drawable != drawable2) {
                return false;
            }
        }
        return true;
    }

    public void setColors(String str, String str2, String str3, String str4) {
        this.trackColorKey = str;
        this.trackCheckedColorKey = str2;
        this.thumbColorKey = str3;
        this.thumbCheckedColorKey = str4;
    }

    private void animateToCheckedState(boolean z) {
        float[] fArr = new float[1];
        fArr[0] = z ? 1.0f : 0.0f;
        this.checkAnimator = ObjectAnimator.ofFloat(this, "progress", fArr);
        this.checkAnimator.setDuration(250);
        this.checkAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                Switch.this.checkAnimator = null;
            }
        });
        this.checkAnimator.start();
    }

    private void animateIcon(boolean z) {
        float[] fArr = new float[1];
        fArr[0] = z ? 1.0f : 0.0f;
        this.iconAnimator = ObjectAnimator.ofFloat(this, "iconProgress", fArr);
        this.iconAnimator.setDuration(250);
        this.iconAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                Switch.this.iconAnimator = null;
            }
        });
        this.iconAnimator.start();
    }

    /* Access modifiers changed, original: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.attachedToWindow = true;
    }

    /* Access modifiers changed, original: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.attachedToWindow = false;
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
        this.onCheckedChangeListener = onCheckedChangeListener;
    }

    public void setChecked(boolean z, boolean z2) {
        setChecked(z, this.drawIconType, z2);
    }

    public void setChecked(boolean z, int i, boolean z2) {
        float f = 1.0f;
        if (z != this.isChecked) {
            this.isChecked = z;
            if (this.attachedToWindow && z2) {
                animateToCheckedState(z);
            } else {
                cancelCheckAnimator();
                setProgress(z ? 1.0f : 0.0f);
            }
            OnCheckedChangeListener onCheckedChangeListener = this.onCheckedChangeListener;
            if (onCheckedChangeListener != null) {
                onCheckedChangeListener.onCheckedChanged(this, z);
            }
        }
        if (this.drawIconType != i) {
            this.drawIconType = i;
            if (this.attachedToWindow && z2) {
                animateIcon(i == 0);
                return;
            }
            cancelIconAnimator();
            if (i != 0) {
                f = 0.0f;
            }
            setIconProgress(f);
        }
    }

    public void setIcon(int i) {
        if (i != 0) {
            this.iconDrawable = getResources().getDrawable(i).mutate();
            Drawable drawable = this.iconDrawable;
            if (drawable != null) {
                int color = Theme.getColor(this.isChecked ? this.trackCheckedColorKey : this.trackColorKey);
                this.lastIconColor = color;
                drawable.setColorFilter(new PorterDuffColorFilter(color, Mode.MULTIPLY));
                return;
            }
            return;
        }
        this.iconDrawable = null;
    }

    public boolean hasIcon() {
        return this.iconDrawable != null;
    }

    public boolean isChecked() {
        return this.isChecked;
    }

    public void setOverrideColor(int i) {
        if (this.overrideColorProgress != i) {
            if (this.overlayBitmap == null) {
                try {
                    this.overlayBitmap = new Bitmap[2];
                    this.overlayCanvas = new Canvas[2];
                    for (int i2 = 0; i2 < 2; i2++) {
                        this.overlayBitmap[i2] = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Config.ARGB_8888);
                        this.overlayCanvas[i2] = new Canvas(this.overlayBitmap[i2]);
                    }
                    this.overlayMaskBitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Config.ARGB_8888);
                    this.overlayMaskCanvas = new Canvas(this.overlayMaskBitmap);
                    this.overlayEraserPaint = new Paint(1);
                    this.overlayEraserPaint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
                    this.overlayMaskPaint = new Paint(1);
                    this.overlayMaskPaint.setXfermode(new PorterDuffXfermode(Mode.DST_OUT));
                    this.bitmapsCreated = true;
                } catch (Throwable unused) {
                    return;
                }
            }
            if (this.bitmapsCreated) {
                this.overrideColorProgress = i;
                this.overlayCx = 0.0f;
                this.overlayCy = 0.0f;
                this.overlayRad = 0.0f;
                invalidate();
            }
        }
    }

    public void setOverrideColorProgress(float f, float f2, float f3) {
        this.overlayCx = f;
        this.overlayCy = f2;
        this.overlayRad = f3;
        invalidate();
    }

    /* Access modifiers changed, original: protected */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x00d3  */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x00d1  */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x00d6  */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x0173  */
    /* JADX WARNING: Removed duplicated region for block: B:101:0x01a3 A:{SYNTHETIC} */
    /* JADX WARNING: Removed duplicated region for block: B:44:0x019b  */
    /* JADX WARNING: Removed duplicated region for block: B:91:0x03d7  */
    /* JADX WARNING: Removed duplicated region for block: B:90:0x03ce  */
    /* JADX WARNING: Removed duplicated region for block: B:90:0x03ce  */
    /* JADX WARNING: Removed duplicated region for block: B:91:0x03d7  */
    /* JADX WARNING: Removed duplicated region for block: B:91:0x03d7  */
    /* JADX WARNING: Removed duplicated region for block: B:90:0x03ce  */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x026d  */
    /* JADX WARNING: Removed duplicated region for block: B:90:0x03ce  */
    /* JADX WARNING: Removed duplicated region for block: B:91:0x03d7  */
    /* JADX WARNING: Missing block: B:17:0x00a8, code skipped:
            if (r12 == 0) goto L_0x00aa;
     */
    /* JADX WARNING: Missing block: B:19:0x00ad, code skipped:
            r16 = 1.0f;
     */
    /* JADX WARNING: Missing block: B:21:0x00b2, code skipped:
            if (r12 == 0) goto L_0x00ad;
     */
    /* JADX WARNING: Missing block: B:66:0x01ef, code skipped:
            if (r1 == 0) goto L_0x01f1;
     */
    /* JADX WARNING: Missing block: B:68:0x01f3, code skipped:
            r4 = 1.0f;
     */
    /* JADX WARNING: Missing block: B:71:0x01f9, code skipped:
            if (r1 == 0) goto L_0x01f3;
     */
    public void onDraw(android.graphics.Canvas r32) {
        /*
        r31 = this;
        r0 = r31;
        r1 = r32;
        r2 = r31.getVisibility();
        if (r2 == 0) goto L_0x000b;
    L_0x000a:
        return;
    L_0x000b:
        r2 = NUM; // 0x41var_ float:31.0 double:5.46818007E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r3 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        org.telegram.messenger.AndroidUtilities.dp(r3);
        r3 = r31.getMeasuredWidth();
        r3 = r3 - r2;
        r4 = 2;
        r3 = r3 / r4;
        r5 = r31.getMeasuredHeight();
        r5 = (float) r5;
        r6 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r7 = org.telegram.messenger.AndroidUtilities.dpf2(r6);
        r5 = r5 - r7;
        r7 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r5 = r5 / r7;
        r8 = NUM; // 0x40e00000 float:7.0 double:5.37751863E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r9 = r9 + r3;
        r10 = NUM; // 0x41880000 float:17.0 double:5.431915495E-315;
        r10 = org.telegram.messenger.AndroidUtilities.dp(r10);
        r10 = (float) r10;
        r11 = r0.progress;
        r10 = r10 * r11;
        r10 = (int) r10;
        r9 = r9 + r10;
        r10 = r31.getMeasuredHeight();
        r10 = r10 / r4;
        r11 = 0;
        r12 = 0;
    L_0x0047:
        r15 = 1;
        if (r12 >= r4) goto L_0x01b1;
    L_0x004a:
        if (r12 != r15) goto L_0x0054;
    L_0x004c:
        r13 = r0.overrideColorProgress;
        if (r13 != 0) goto L_0x0054;
    L_0x0050:
        r7 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        goto L_0x01a3;
    L_0x0054:
        if (r12 != 0) goto L_0x0058;
    L_0x0056:
        r13 = r1;
        goto L_0x005c;
    L_0x0058:
        r13 = r0.overlayCanvas;
        r13 = r13[r11];
    L_0x005c:
        if (r12 != r15) goto L_0x00a4;
    L_0x005e:
        r7 = r0.overlayBitmap;
        r7 = r7[r11];
        r7.eraseColor(r11);
        r7 = r0.paint;
        r11 = -16777216; // 0xfffffffffvar_ float:-1.7014118E38 double:NaN;
        r7.setColor(r11);
        r7 = r0.overlayMaskCanvas;
        r20 = 0;
        r21 = 0;
        r11 = r0.overlayMaskBitmap;
        r11 = r11.getWidth();
        r11 = (float) r11;
        r14 = r0.overlayMaskBitmap;
        r14 = r14.getHeight();
        r14 = (float) r14;
        r8 = r0.paint;
        r19 = r7;
        r22 = r11;
        r23 = r14;
        r24 = r8;
        r19.drawRect(r20, r21, r22, r23, r24);
        r7 = r0.overlayMaskCanvas;
        r8 = r0.overlayCx;
        r11 = r31.getX();
        r8 = r8 - r11;
        r11 = r0.overlayCy;
        r14 = r31.getY();
        r11 = r11 - r14;
        r14 = r0.overlayRad;
        r6 = r0.overlayEraserPaint;
        r7.drawCircle(r8, r11, r14, r6);
    L_0x00a4:
        r6 = r0.overrideColorProgress;
        if (r6 != r15) goto L_0x00b0;
    L_0x00a8:
        if (r12 != 0) goto L_0x00ad;
    L_0x00aa:
        r16 = 0;
        goto L_0x00b9;
    L_0x00ad:
        r16 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        goto L_0x00b9;
    L_0x00b0:
        if (r6 != r4) goto L_0x00b5;
    L_0x00b2:
        if (r12 != 0) goto L_0x00aa;
    L_0x00b4:
        goto L_0x00ad;
    L_0x00b5:
        r6 = r0.progress;
        r16 = r6;
    L_0x00b9:
        r6 = r0.trackColorKey;
        r6 = org.telegram.ui.ActionBar.Theme.getColor(r6);
        r7 = r0.trackCheckedColorKey;
        r7 = org.telegram.ui.ActionBar.Theme.getColor(r7);
        if (r12 != 0) goto L_0x00eb;
    L_0x00c7:
        r8 = r0.iconDrawable;
        if (r8 == 0) goto L_0x00eb;
    L_0x00cb:
        r8 = r0.lastIconColor;
        r11 = r0.isChecked;
        if (r11 == 0) goto L_0x00d3;
    L_0x00d1:
        r11 = r7;
        goto L_0x00d4;
    L_0x00d3:
        r11 = r6;
    L_0x00d4:
        if (r8 == r11) goto L_0x00eb;
    L_0x00d6:
        r8 = r0.iconDrawable;
        r11 = new android.graphics.PorterDuffColorFilter;
        r14 = r0.isChecked;
        if (r14 == 0) goto L_0x00e0;
    L_0x00de:
        r14 = r7;
        goto L_0x00e1;
    L_0x00e0:
        r14 = r6;
    L_0x00e1:
        r0.lastIconColor = r14;
        r4 = android.graphics.PorterDuff.Mode.MULTIPLY;
        r11.<init>(r14, r4);
        r8.setColorFilter(r11);
    L_0x00eb:
        r4 = android.graphics.Color.red(r6);
        r8 = android.graphics.Color.red(r7);
        r11 = android.graphics.Color.green(r6);
        r14 = android.graphics.Color.green(r7);
        r15 = android.graphics.Color.blue(r6);
        r22 = android.graphics.Color.blue(r7);
        r6 = android.graphics.Color.alpha(r6);
        r7 = android.graphics.Color.alpha(r7);
        r1 = (float) r4;
        r8 = r8 - r4;
        r4 = (float) r8;
        r4 = r4 * r16;
        r1 = r1 + r4;
        r1 = (int) r1;
        r4 = (float) r11;
        r14 = r14 - r11;
        r8 = (float) r14;
        r8 = r8 * r16;
        r4 = r4 + r8;
        r4 = (int) r4;
        r8 = (float) r15;
        r11 = r22 - r15;
        r11 = (float) r11;
        r11 = r11 * r16;
        r8 = r8 + r11;
        r8 = (int) r8;
        r11 = (float) r6;
        r7 = r7 - r6;
        r6 = (float) r7;
        r6 = r6 * r16;
        r11 = r11 + r6;
        r6 = (int) r11;
        r6 = r6 & 255;
        r6 = r6 << 24;
        r1 = r1 & 255;
        r1 = r1 << 16;
        r1 = r1 | r6;
        r4 = r4 & 255;
        r4 = r4 << 8;
        r1 = r1 | r4;
        r4 = r8 & 255;
        r1 = r1 | r4;
        r4 = r0.paint;
        r4.setColor(r1);
        r4 = r0.paint2;
        r4.setColor(r1);
        r1 = r0.rectF;
        r4 = (float) r3;
        r6 = r3 + r2;
        r6 = (float) r6;
        r7 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dpf2(r7);
        r8 = r8 + r5;
        r1.set(r4, r5, r6, r8);
        r1 = r0.rectF;
        r4 = NUM; // 0x40e00000 float:7.0 double:5.37751863E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dpf2(r4);
        r8 = org.telegram.messenger.AndroidUtilities.dpf2(r4);
        r4 = r0.paint;
        r13.drawRoundRect(r1, r6, r8, r4);
        r1 = (float) r9;
        r4 = (float) r10;
        r6 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dpf2(r6);
        r8 = r0.paint;
        r13.drawCircle(r1, r4, r6, r8);
        if (r12 != 0) goto L_0x0198;
    L_0x0173:
        r1 = r0.rippleDrawable;
        if (r1 == 0) goto L_0x0198;
    L_0x0177:
        r4 = NUM; // 0x41900000 float:18.0 double:5.43450582E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r6 = r9 - r6;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r8 = r10 - r8;
        r11 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r11 = r11 + r9;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r4 + r10;
        r1.setBounds(r6, r8, r11, r4);
        r1 = r0.rippleDrawable;
        r1.draw(r13);
        goto L_0x01a3;
    L_0x0198:
        r1 = 1;
        if (r12 != r1) goto L_0x01a3;
    L_0x019b:
        r1 = r0.overlayMaskBitmap;
        r4 = r0.overlayMaskPaint;
        r6 = 0;
        r13.drawBitmap(r1, r6, r6, r4);
    L_0x01a3:
        r12 = r12 + 1;
        r1 = r32;
        r4 = 2;
        r6 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r7 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r8 = NUM; // 0x40e00000 float:7.0 double:5.37751863E-315;
        r11 = 0;
        goto L_0x0047;
    L_0x01b1:
        r6 = 0;
        r1 = r0.overrideColorProgress;
        r2 = 0;
        if (r1 == 0) goto L_0x01c2;
    L_0x01b7:
        r1 = r0.overlayBitmap;
        r3 = 0;
        r1 = r1[r3];
        r3 = r32;
        r3.drawBitmap(r1, r6, r6, r2);
        goto L_0x01c4;
    L_0x01c2:
        r3 = r32;
    L_0x01c4:
        r1 = 0;
    L_0x01c5:
        r4 = 2;
        if (r1 >= r4) goto L_0x03dd;
    L_0x01c8:
        r4 = 1;
        if (r1 != r4) goto L_0x01d7;
    L_0x01cb:
        r5 = r0.overrideColorProgress;
        if (r5 != 0) goto L_0x01d7;
    L_0x01cf:
        r2 = 2;
        r7 = 0;
        r8 = NUM; // 0x40e00000 float:7.0 double:5.37751863E-315;
        r13 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        goto L_0x03d8;
    L_0x01d7:
        if (r1 != 0) goto L_0x01db;
    L_0x01d9:
        r5 = r3;
        goto L_0x01df;
    L_0x01db:
        r5 = r0.overlayCanvas;
        r5 = r5[r4];
    L_0x01df:
        if (r1 != r4) goto L_0x01ea;
    L_0x01e1:
        r6 = r0.overlayBitmap;
        r6 = r6[r4];
        r7 = 0;
        r6.eraseColor(r7);
        goto L_0x01eb;
    L_0x01ea:
        r7 = 0;
    L_0x01eb:
        r6 = r0.overrideColorProgress;
        if (r6 != r4) goto L_0x01f6;
    L_0x01ef:
        if (r1 != 0) goto L_0x01f3;
    L_0x01f1:
        r4 = 0;
        goto L_0x01fe;
    L_0x01f3:
        r4 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        goto L_0x01fe;
    L_0x01f6:
        r4 = 2;
        if (r6 != r4) goto L_0x01fc;
    L_0x01f9:
        if (r1 != 0) goto L_0x01f1;
    L_0x01fb:
        goto L_0x01f3;
    L_0x01fc:
        r4 = r0.progress;
    L_0x01fe:
        r6 = r0.thumbColorKey;
        r6 = org.telegram.ui.ActionBar.Theme.getColor(r6);
        r8 = r0.thumbCheckedColorKey;
        r8 = org.telegram.ui.ActionBar.Theme.getColor(r8);
        r11 = android.graphics.Color.red(r6);
        r12 = android.graphics.Color.red(r8);
        r13 = android.graphics.Color.green(r6);
        r14 = android.graphics.Color.green(r8);
        r15 = android.graphics.Color.blue(r6);
        r18 = android.graphics.Color.blue(r8);
        r6 = android.graphics.Color.alpha(r6);
        r8 = android.graphics.Color.alpha(r8);
        r7 = (float) r11;
        r12 = r12 - r11;
        r11 = (float) r12;
        r11 = r11 * r4;
        r7 = r7 + r11;
        r7 = (int) r7;
        r11 = (float) r13;
        r14 = r14 - r13;
        r12 = (float) r14;
        r12 = r12 * r4;
        r11 = r11 + r12;
        r11 = (int) r11;
        r12 = (float) r15;
        r13 = r18 - r15;
        r13 = (float) r13;
        r13 = r13 * r4;
        r12 = r12 + r13;
        r12 = (int) r12;
        r13 = (float) r6;
        r8 = r8 - r6;
        r6 = (float) r8;
        r6 = r6 * r4;
        r13 = r13 + r6;
        r4 = (int) r13;
        r6 = r0.paint;
        r4 = r4 & 255;
        r4 = r4 << 24;
        r7 = r7 & 255;
        r7 = r7 << 16;
        r4 = r4 | r7;
        r7 = r11 & 255;
        r7 = r7 << 8;
        r4 = r4 | r7;
        r7 = r12 & 255;
        r4 = r4 | r7;
        r6.setColor(r4);
        r4 = (float) r9;
        r6 = (float) r10;
        r7 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r7 = (float) r7;
        r8 = r0.paint;
        r5.drawCircle(r4, r6, r7, r8);
        if (r1 != 0) goto L_0x03c6;
    L_0x026d:
        r7 = r0.iconDrawable;
        if (r7 == 0) goto L_0x029c;
    L_0x0271:
        r4 = r7.getIntrinsicWidth();
        r6 = 2;
        r4 = r4 / r6;
        r4 = r9 - r4;
        r8 = r0.iconDrawable;
        r8 = r8.getIntrinsicHeight();
        r8 = r8 / r6;
        r8 = r10 - r8;
        r11 = r0.iconDrawable;
        r11 = r11.getIntrinsicWidth();
        r11 = r11 / r6;
        r11 = r11 + r9;
        r12 = r0.iconDrawable;
        r12 = r12.getIntrinsicHeight();
        r12 = r12 / r6;
        r12 = r12 + r10;
        r7.setBounds(r4, r8, r11, r12);
        r4 = r0.iconDrawable;
        r4.draw(r5);
        goto L_0x03c6;
    L_0x029c:
        r7 = r0.drawIconType;
        r8 = 1;
        if (r7 != r8) goto L_0x0370;
    L_0x02a1:
        r7 = NUM; // 0x412ccccd float:10.8 double:5.40238577E-315;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r7 = (float) r7;
        r8 = NUM; // 0x3fa66666 float:1.3 double:5.275977814E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r8 = (float) r8;
        r9 = r0.progress;
        r8 = r8 * r9;
        r7 = r7 - r8;
        r4 = r4 - r7;
        r9 = (int) r4;
        r4 = NUM; // 0x41080000 float:8.5 double:5.390470265E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = (float) r4;
        r7 = NUM; // 0x3var_ float:0.5 double:5.222099017E-315;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r7 = (float) r7;
        r8 = r0.progress;
        r7 = r7 * r8;
        r4 = r4 - r7;
        r6 = r6 - r4;
        r10 = (int) r6;
        r4 = NUM; // 0x40933333 float:4.6 double:5.35265149E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dpf2(r4);
        r4 = (int) r4;
        r4 = r4 + r9;
        r6 = NUM; // 0x41180000 float:9.5 double:5.39565092E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dpf2(r6);
        r7 = (float) r10;
        r6 = r6 + r7;
        r6 = (int) r6;
        r8 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r7 = r7 + r4;
        r11 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r11 = r11 + r6;
        r12 = NUM; // 0x40var_ float:7.5 double:5.382699284E-315;
        r13 = org.telegram.messenger.AndroidUtilities.dpf2(r12);
        r13 = (int) r13;
        r13 = r13 + r9;
        r14 = NUM; // 0x40accccd float:5.4 double:5.36094054E-315;
        r14 = org.telegram.messenger.AndroidUtilities.dpf2(r14);
        r14 = (int) r14;
        r14 = r14 + r10;
        r15 = NUM; // 0x40e00000 float:7.0 double:5.37751863E-315;
        r17 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r8 = r13 + r17;
        r17 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r15 = r14 + r17;
        r2 = (float) r13;
        r4 = r4 - r13;
        r4 = (float) r4;
        r13 = r0.progress;
        r4 = r4 * r13;
        r2 = r2 + r4;
        r2 = (int) r2;
        r4 = (float) r14;
        r6 = r6 - r14;
        r6 = (float) r6;
        r6 = r6 * r13;
        r4 = r4 + r6;
        r4 = (int) r4;
        r6 = (float) r8;
        r7 = r7 - r8;
        r7 = (float) r7;
        r7 = r7 * r13;
        r6 = r6 + r7;
        r6 = (int) r6;
        r7 = (float) r15;
        r11 = r11 - r15;
        r8 = (float) r11;
        r8 = r8 * r13;
        r7 = r7 + r8;
        r7 = (int) r7;
        r2 = (float) r2;
        r4 = (float) r4;
        r6 = (float) r6;
        r7 = (float) r7;
        r8 = r0.paint2;
        r25 = r5;
        r26 = r2;
        r27 = r4;
        r28 = r6;
        r29 = r7;
        r30 = r8;
        r25.drawLine(r26, r27, r28, r29, r30);
        r2 = org.telegram.messenger.AndroidUtilities.dpf2(r12);
        r2 = (int) r2;
        r2 = r2 + r9;
        r4 = NUM; // 0x41480000 float:12.5 double:5.41119288E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dpf2(r4);
        r4 = (int) r4;
        r4 = r4 + r10;
        r8 = NUM; // 0x40e00000 float:7.0 double:5.37751863E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r6 = r6 + r2;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r7 = r4 - r7;
        r2 = (float) r2;
        r4 = (float) r4;
        r6 = (float) r6;
        r7 = (float) r7;
        r11 = r0.paint2;
        r26 = r2;
        r27 = r4;
        r28 = r6;
        r29 = r7;
        r30 = r11;
        r25.drawLine(r26, r27, r28, r29, r30);
        r2 = 2;
        r4 = 1;
        r13 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        goto L_0x03cc;
    L_0x0370:
        r2 = 2;
        r8 = NUM; // 0x40e00000 float:7.0 double:5.37751863E-315;
        if (r7 == r2) goto L_0x0379;
    L_0x0375:
        r7 = r0.iconAnimator;
        if (r7 == 0) goto L_0x03c9;
    L_0x0379:
        r7 = r0.paint2;
        r11 = NUM; // 0x437var_ float:255.0 double:5.5947823E-315;
        r12 = r0.iconProgress;
        r13 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r12 = r13 - r12;
        r12 = r12 * r11;
        r11 = (int) r12;
        r7.setAlpha(r11);
        r7 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r7 = r10 - r7;
        r7 = (float) r7;
        r11 = r0.paint2;
        r25 = r5;
        r26 = r4;
        r27 = r6;
        r28 = r4;
        r29 = r7;
        r30 = r11;
        r25.drawLine(r26, r27, r28, r29, r30);
        r5.save();
        r7 = -NUM; // 0xffffffffc2b40000 float:-90.0 double:NaN;
        r11 = r0.iconProgress;
        r11 = r11 * r7;
        r5.rotate(r11, r4, r6);
        r7 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r7 = r7 + r9;
        r7 = (float) r7;
        r11 = r0.paint2;
        r28 = r7;
        r29 = r6;
        r30 = r11;
        r25.drawLine(r26, r27, r28, r29, r30);
        r5.restore();
        goto L_0x03cb;
    L_0x03c6:
        r2 = 2;
        r8 = NUM; // 0x40e00000 float:7.0 double:5.37751863E-315;
    L_0x03c9:
        r13 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
    L_0x03cb:
        r4 = 1;
    L_0x03cc:
        if (r1 != r4) goto L_0x03d7;
    L_0x03ce:
        r4 = r0.overlayMaskBitmap;
        r6 = r0.overlayMaskPaint;
        r7 = 0;
        r5.drawBitmap(r4, r7, r7, r6);
        goto L_0x03d8;
    L_0x03d7:
        r7 = 0;
    L_0x03d8:
        r1 = r1 + 1;
        r2 = 0;
        goto L_0x01c5;
    L_0x03dd:
        r7 = 0;
        r1 = r0.overrideColorProgress;
        if (r1 == 0) goto L_0x03eb;
    L_0x03e2:
        r1 = r0.overlayBitmap;
        r2 = 1;
        r1 = r1[r2];
        r2 = 0;
        r3.drawBitmap(r1, r7, r7, r2);
    L_0x03eb:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.Switch.onDraw(android.graphics.Canvas):void");
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setClassName("android.widget.Switch");
        accessibilityNodeInfo.setCheckable(true);
        accessibilityNodeInfo.setChecked(this.isChecked);
    }
}
