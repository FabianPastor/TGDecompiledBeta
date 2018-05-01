package org.telegram.ui.Components;

import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.widget.EditText;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.tgnet.ConnectionsManager;

public class EditTextBoldCursor extends EditText {
    private static Method getVerticalOffsetMethod;
    private static Field mCursorDrawableField;
    private static Field mCursorDrawableResField;
    private static Field mEditor;
    private static Field mScrollYField;
    private static Field mShowCursorField;
    private boolean allowDrawCursor = true;
    private int cursorSize;
    private float cursorWidth = 2.0f;
    private Object editor;
    private GradientDrawable gradientDrawable;
    private float hintAlpha = 1.0f;
    private int hintColor;
    private StaticLayout hintLayout;
    private boolean hintVisible = true;
    private int ignoreBottomCount;
    private int ignoreTopCount;
    private long lastUpdateTime;
    private float lineSpacingExtra;
    private Drawable[] mCursorDrawable;
    private Rect rect = new Rect();
    private int scrollY;

    public EditTextBoldCursor(android.content.Context r6) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
        /*
        r5 = this;
        r5.<init>(r6);
        r6 = new android.graphics.Rect;
        r6.<init>();
        r5.rect = r6;
        r6 = 1;
        r5.hintVisible = r6;
        r0 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r5.hintAlpha = r0;
        r5.allowDrawCursor = r6;
        r0 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r5.cursorWidth = r0;
        r0 = mCursorDrawableField;
        if (r0 != 0) goto L_0x007e;
    L_0x001b:
        r0 = android.view.View.class;	 Catch:{ Throwable -> 0x007e }
        r1 = "mScrollY";	 Catch:{ Throwable -> 0x007e }
        r0 = r0.getDeclaredField(r1);	 Catch:{ Throwable -> 0x007e }
        mScrollYField = r0;	 Catch:{ Throwable -> 0x007e }
        r0 = mScrollYField;	 Catch:{ Throwable -> 0x007e }
        r0.setAccessible(r6);	 Catch:{ Throwable -> 0x007e }
        r0 = android.widget.TextView.class;	 Catch:{ Throwable -> 0x007e }
        r1 = "mCursorDrawableRes";	 Catch:{ Throwable -> 0x007e }
        r0 = r0.getDeclaredField(r1);	 Catch:{ Throwable -> 0x007e }
        mCursorDrawableResField = r0;	 Catch:{ Throwable -> 0x007e }
        r0 = mCursorDrawableResField;	 Catch:{ Throwable -> 0x007e }
        r0.setAccessible(r6);	 Catch:{ Throwable -> 0x007e }
        r0 = android.widget.TextView.class;	 Catch:{ Throwable -> 0x007e }
        r1 = "mEditor";	 Catch:{ Throwable -> 0x007e }
        r0 = r0.getDeclaredField(r1);	 Catch:{ Throwable -> 0x007e }
        mEditor = r0;	 Catch:{ Throwable -> 0x007e }
        r0 = mEditor;	 Catch:{ Throwable -> 0x007e }
        r0.setAccessible(r6);	 Catch:{ Throwable -> 0x007e }
        r0 = "android.widget.Editor";	 Catch:{ Throwable -> 0x007e }
        r0 = java.lang.Class.forName(r0);	 Catch:{ Throwable -> 0x007e }
        r1 = "mShowCursor";	 Catch:{ Throwable -> 0x007e }
        r1 = r0.getDeclaredField(r1);	 Catch:{ Throwable -> 0x007e }
        mShowCursorField = r1;	 Catch:{ Throwable -> 0x007e }
        r1 = mShowCursorField;	 Catch:{ Throwable -> 0x007e }
        r1.setAccessible(r6);	 Catch:{ Throwable -> 0x007e }
        r1 = "mCursorDrawable";	 Catch:{ Throwable -> 0x007e }
        r0 = r0.getDeclaredField(r1);	 Catch:{ Throwable -> 0x007e }
        mCursorDrawableField = r0;	 Catch:{ Throwable -> 0x007e }
        r0 = mCursorDrawableField;	 Catch:{ Throwable -> 0x007e }
        r0.setAccessible(r6);	 Catch:{ Throwable -> 0x007e }
        r0 = android.widget.TextView.class;	 Catch:{ Throwable -> 0x007e }
        r1 = "getVerticalOffset";	 Catch:{ Throwable -> 0x007e }
        r2 = new java.lang.Class[r6];	 Catch:{ Throwable -> 0x007e }
        r3 = 0;	 Catch:{ Throwable -> 0x007e }
        r4 = java.lang.Boolean.TYPE;	 Catch:{ Throwable -> 0x007e }
        r2[r3] = r4;	 Catch:{ Throwable -> 0x007e }
        r0 = r0.getDeclaredMethod(r1, r2);	 Catch:{ Throwable -> 0x007e }
        getVerticalOffsetMethod = r0;	 Catch:{ Throwable -> 0x007e }
        r0 = getVerticalOffsetMethod;	 Catch:{ Throwable -> 0x007e }
        r0.setAccessible(r6);	 Catch:{ Throwable -> 0x007e }
    L_0x007e:
        r6 = new android.graphics.drawable.GradientDrawable;	 Catch:{ Exception -> 0x00ae }
        r0 = android.graphics.drawable.GradientDrawable.Orientation.TOP_BOTTOM;	 Catch:{ Exception -> 0x00ae }
        r1 = 2;	 Catch:{ Exception -> 0x00ae }
        r1 = new int[r1];	 Catch:{ Exception -> 0x00ae }
        r1 = {-11230757, -11230757};	 Catch:{ Exception -> 0x00ae }
        r6.<init>(r0, r1);	 Catch:{ Exception -> 0x00ae }
        r5.gradientDrawable = r6;	 Catch:{ Exception -> 0x00ae }
        r6 = mEditor;	 Catch:{ Exception -> 0x00ae }
        r6 = r6.get(r5);	 Catch:{ Exception -> 0x00ae }
        r5.editor = r6;	 Catch:{ Exception -> 0x00ae }
        r6 = mCursorDrawableField;	 Catch:{ Exception -> 0x00ae }
        r0 = r5.editor;	 Catch:{ Exception -> 0x00ae }
        r6 = r6.get(r0);	 Catch:{ Exception -> 0x00ae }
        r6 = (android.graphics.drawable.Drawable[]) r6;	 Catch:{ Exception -> 0x00ae }
        r5.mCursorDrawable = r6;	 Catch:{ Exception -> 0x00ae }
        r6 = mCursorDrawableResField;	 Catch:{ Exception -> 0x00ae }
        r0 = NUM; // 0x7f070082 float:1.7944842E38 double:1.0529355673E-314;	 Catch:{ Exception -> 0x00ae }
        r0 = java.lang.Integer.valueOf(r0);	 Catch:{ Exception -> 0x00ae }
        r6.set(r5, r0);	 Catch:{ Exception -> 0x00ae }
        goto L_0x00b2;
    L_0x00ae:
        r6 = move-exception;
        org.telegram.messenger.FileLog.m3e(r6);
    L_0x00b2:
        r6 = NUM; // 0x41c00000 float:24.0 double:5.450047783E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r5.cursorSize = r6;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.EditTextBoldCursor.<init>(android.content.Context):void");
    }

    public void setAllowDrawCursor(boolean z) {
        this.allowDrawCursor = z;
    }

    public void setCursorWidth(float f) {
        this.cursorWidth = f;
    }

    public void setCursorColor(int i) {
        this.gradientDrawable.setColor(i);
        invalidate();
    }

    public void setCursorSize(int i) {
        this.cursorSize = i;
    }

    public void setHintVisible(boolean z) {
        if (this.hintVisible != z) {
            this.lastUpdateTime = System.currentTimeMillis();
            this.hintVisible = z;
            invalidate();
        }
    }

    public void setHintColor(int i) {
        this.hintColor = i;
        invalidate();
    }

    public void setHintText(String str) {
        this.hintLayout = new StaticLayout(str, getPaint(), AndroidUtilities.dp(1000.0f), Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
    }

    public void setLineSpacing(float f, float f2) {
        super.setLineSpacing(f, f2);
        this.lineSpacingExtra = f;
    }

    public int getExtendedPaddingTop() {
        if (this.ignoreTopCount == 0) {
            return super.getExtendedPaddingTop();
        }
        this.ignoreTopCount--;
        return 0;
    }

    public int getExtendedPaddingBottom() {
        if (this.ignoreBottomCount == 0) {
            return super.getExtendedPaddingBottom();
        }
        this.ignoreBottomCount--;
        return this.scrollY != ConnectionsManager.DEFAULT_DATACENTER_ID ? -this.scrollY : 0;
    }

    protected void onDraw(android.graphics.Canvas r12) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
        /*
        r11 = this;
        r0 = r11.getExtendedPaddingTop();
        r1 = NUM; // 0x7fffffff float:NaN double:1.060997895E-314;
        r11.scrollY = r1;
        r2 = 0;
        r3 = mScrollYField;	 Catch:{ Exception -> 0x001b }
        r3 = r3.getInt(r11);	 Catch:{ Exception -> 0x001b }
        r11.scrollY = r3;	 Catch:{ Exception -> 0x001b }
        r3 = mScrollYField;	 Catch:{ Exception -> 0x001b }
        r4 = java.lang.Integer.valueOf(r2);	 Catch:{ Exception -> 0x001b }
        r3.set(r11, r4);	 Catch:{ Exception -> 0x001b }
    L_0x001b:
        r3 = 1;
        r11.ignoreTopCount = r3;
        r11.ignoreBottomCount = r3;
        r12.save();
        r0 = (float) r0;
        r4 = 0;
        r12.translate(r4, r0);
        super.onDraw(r12);	 Catch:{ Exception -> 0x002b }
    L_0x002b:
        r0 = r11.scrollY;
        if (r0 == r1) goto L_0x003a;
    L_0x002f:
        r0 = mScrollYField;	 Catch:{ Exception -> 0x003a }
        r1 = r11.scrollY;	 Catch:{ Exception -> 0x003a }
        r1 = java.lang.Integer.valueOf(r1);	 Catch:{ Exception -> 0x003a }
        r0.set(r11, r1);	 Catch:{ Exception -> 0x003a }
    L_0x003a:
        r12.restore();
        r0 = r11.length();
        if (r0 != 0) goto L_0x00f9;
    L_0x0043:
        r0 = r11.hintLayout;
        if (r0 == 0) goto L_0x00f9;
    L_0x0047:
        r0 = r11.hintVisible;
        if (r0 != 0) goto L_0x0051;
    L_0x004b:
        r0 = r11.hintAlpha;
        r0 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1));
        if (r0 == 0) goto L_0x00f9;
    L_0x0051:
        r0 = r11.hintVisible;
        r1 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;
        if (r0 == 0) goto L_0x005d;
    L_0x0057:
        r0 = r11.hintAlpha;
        r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1));
        if (r0 != 0) goto L_0x0067;
    L_0x005d:
        r0 = r11.hintVisible;
        if (r0 != 0) goto L_0x00a7;
    L_0x0061:
        r0 = r11.hintAlpha;
        r0 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1));
        if (r0 == 0) goto L_0x00a7;
    L_0x0067:
        r5 = java.lang.System.currentTimeMillis();
        r7 = r11.lastUpdateTime;
        r9 = r5 - r7;
        r7 = 0;
        r0 = (r9 > r7 ? 1 : (r9 == r7 ? 0 : -1));
        r7 = 17;
        if (r0 < 0) goto L_0x007d;
    L_0x0077:
        r0 = (r9 > r7 ? 1 : (r9 == r7 ? 0 : -1));
        if (r0 <= 0) goto L_0x007c;
    L_0x007b:
        goto L_0x007d;
    L_0x007c:
        r7 = r9;
    L_0x007d:
        r11.lastUpdateTime = r5;
        r0 = r11.hintVisible;
        r5 = NUM; // 0x43160000 float:150.0 double:5.56078426E-315;
        if (r0 == 0) goto L_0x0095;
    L_0x0085:
        r0 = r11.hintAlpha;
        r6 = (float) r7;
        r6 = r6 / r5;
        r0 = r0 + r6;
        r11.hintAlpha = r0;
        r0 = r11.hintAlpha;
        r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1));
        if (r0 <= 0) goto L_0x00a4;
    L_0x0092:
        r11.hintAlpha = r1;
        goto L_0x00a4;
    L_0x0095:
        r0 = r11.hintAlpha;
        r1 = (float) r7;
        r1 = r1 / r5;
        r0 = r0 - r1;
        r11.hintAlpha = r0;
        r0 = r11.hintAlpha;
        r0 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1));
        if (r0 >= 0) goto L_0x00a4;
    L_0x00a2:
        r11.hintAlpha = r4;
    L_0x00a4:
        r11.invalidate();
    L_0x00a7:
        r0 = r11.getPaint();
        r0 = r0.getColor();
        r1 = r11.getPaint();
        r5 = r11.hintColor;
        r1.setColor(r5);
        r1 = r11.getPaint();
        r5 = NUM; // 0x437f0000 float:255.0 double:5.5947823E-315;
        r6 = r11.hintAlpha;
        r5 = r5 * r6;
        r5 = (int) r5;
        r1.setAlpha(r5);
        r12.save();
        r1 = r11.hintLayout;
        r1 = r1.getLineLeft(r2);
        r5 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1));
        if (r5 == 0) goto L_0x00d6;
    L_0x00d2:
        r5 = (float) r2;
        r5 = r5 - r1;
        r1 = (int) r5;
        goto L_0x00d7;
    L_0x00d6:
        r1 = r2;
    L_0x00d7:
        r1 = (float) r1;
        r5 = r11.getMeasuredHeight();
        r6 = r11.hintLayout;
        r6 = r6.getHeight();
        r5 = r5 - r6;
        r5 = (float) r5;
        r6 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r5 = r5 / r6;
        r12.translate(r1, r5);
        r1 = r11.hintLayout;
        r1.draw(r12);
        r1 = r11.getPaint();
        r1.setColor(r0);
        r12.restore();
    L_0x00f9:
        r0 = r11.allowDrawCursor;	 Catch:{ Throwable -> 0x01d2 }
        if (r0 == 0) goto L_0x01d2;	 Catch:{ Throwable -> 0x01d2 }
    L_0x00fd:
        r0 = mShowCursorField;	 Catch:{ Throwable -> 0x01d2 }
        if (r0 == 0) goto L_0x01d2;	 Catch:{ Throwable -> 0x01d2 }
    L_0x0101:
        r0 = r11.mCursorDrawable;	 Catch:{ Throwable -> 0x01d2 }
        if (r0 == 0) goto L_0x01d2;	 Catch:{ Throwable -> 0x01d2 }
    L_0x0105:
        r0 = r11.mCursorDrawable;	 Catch:{ Throwable -> 0x01d2 }
        r0 = r0[r2];	 Catch:{ Throwable -> 0x01d2 }
        if (r0 == 0) goto L_0x01d2;	 Catch:{ Throwable -> 0x01d2 }
    L_0x010b:
        r0 = mShowCursorField;	 Catch:{ Throwable -> 0x01d2 }
        r1 = r11.editor;	 Catch:{ Throwable -> 0x01d2 }
        r0 = r0.getLong(r1);	 Catch:{ Throwable -> 0x01d2 }
        r5 = android.os.SystemClock.uptimeMillis();	 Catch:{ Throwable -> 0x01d2 }
        r7 = r5 - r0;	 Catch:{ Throwable -> 0x01d2 }
        r0 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;	 Catch:{ Throwable -> 0x01d2 }
        r7 = r7 % r0;	 Catch:{ Throwable -> 0x01d2 }
        r0 = 500; // 0x1f4 float:7.0E-43 double:2.47E-321;	 Catch:{ Throwable -> 0x01d2 }
        r5 = (r7 > r0 ? 1 : (r7 == r0 ? 0 : -1));	 Catch:{ Throwable -> 0x01d2 }
        if (r5 >= 0) goto L_0x012a;	 Catch:{ Throwable -> 0x01d2 }
    L_0x0122:
        r0 = r11.isFocused();	 Catch:{ Throwable -> 0x01d2 }
        if (r0 == 0) goto L_0x012a;	 Catch:{ Throwable -> 0x01d2 }
    L_0x0128:
        r0 = r3;	 Catch:{ Throwable -> 0x01d2 }
        goto L_0x012b;	 Catch:{ Throwable -> 0x01d2 }
    L_0x012a:
        r0 = r2;	 Catch:{ Throwable -> 0x01d2 }
    L_0x012b:
        if (r0 == 0) goto L_0x01d2;	 Catch:{ Throwable -> 0x01d2 }
    L_0x012d:
        r12.save();	 Catch:{ Throwable -> 0x01d2 }
        r0 = r11.getGravity();	 Catch:{ Throwable -> 0x01d2 }
        r0 = r0 & 112;	 Catch:{ Throwable -> 0x01d2 }
        r1 = 48;	 Catch:{ Throwable -> 0x01d2 }
        if (r0 == r1) goto L_0x014f;	 Catch:{ Throwable -> 0x01d2 }
    L_0x013a:
        r0 = getVerticalOffsetMethod;	 Catch:{ Throwable -> 0x01d2 }
        r1 = new java.lang.Object[r3];	 Catch:{ Throwable -> 0x01d2 }
        r5 = java.lang.Boolean.valueOf(r3);	 Catch:{ Throwable -> 0x01d2 }
        r1[r2] = r5;	 Catch:{ Throwable -> 0x01d2 }
        r0 = r0.invoke(r11, r1);	 Catch:{ Throwable -> 0x01d2 }
        r0 = (java.lang.Integer) r0;	 Catch:{ Throwable -> 0x01d2 }
        r0 = r0.intValue();	 Catch:{ Throwable -> 0x01d2 }
        goto L_0x0150;	 Catch:{ Throwable -> 0x01d2 }
    L_0x014f:
        r0 = r2;	 Catch:{ Throwable -> 0x01d2 }
    L_0x0150:
        r1 = r11.getPaddingLeft();	 Catch:{ Throwable -> 0x01d2 }
        r1 = (float) r1;	 Catch:{ Throwable -> 0x01d2 }
        r5 = r11.getExtendedPaddingTop();	 Catch:{ Throwable -> 0x01d2 }
        r5 = r5 + r0;	 Catch:{ Throwable -> 0x01d2 }
        r0 = (float) r5;	 Catch:{ Throwable -> 0x01d2 }
        r12.translate(r1, r0);	 Catch:{ Throwable -> 0x01d2 }
        r0 = r11.getLayout();	 Catch:{ Throwable -> 0x01d2 }
        r1 = r11.getSelectionStart();	 Catch:{ Throwable -> 0x01d2 }
        r1 = r0.getLineForOffset(r1);	 Catch:{ Throwable -> 0x01d2 }
        r0 = r0.getLineCount();	 Catch:{ Throwable -> 0x01d2 }
        r5 = r11.mCursorDrawable;	 Catch:{ Throwable -> 0x01d2 }
        r2 = r5[r2];	 Catch:{ Throwable -> 0x01d2 }
        r2 = r2.getBounds();	 Catch:{ Throwable -> 0x01d2 }
        r5 = r11.rect;	 Catch:{ Throwable -> 0x01d2 }
        r6 = r2.left;	 Catch:{ Throwable -> 0x01d2 }
        r5.left = r6;	 Catch:{ Throwable -> 0x01d2 }
        r5 = r11.rect;	 Catch:{ Throwable -> 0x01d2 }
        r6 = r2.left;	 Catch:{ Throwable -> 0x01d2 }
        r7 = r11.cursorWidth;	 Catch:{ Throwable -> 0x01d2 }
        r7 = org.telegram.messenger.AndroidUtilities.dp(r7);	 Catch:{ Throwable -> 0x01d2 }
        r6 = r6 + r7;	 Catch:{ Throwable -> 0x01d2 }
        r5.right = r6;	 Catch:{ Throwable -> 0x01d2 }
        r5 = r11.rect;	 Catch:{ Throwable -> 0x01d2 }
        r6 = r2.bottom;	 Catch:{ Throwable -> 0x01d2 }
        r5.bottom = r6;	 Catch:{ Throwable -> 0x01d2 }
        r5 = r11.rect;	 Catch:{ Throwable -> 0x01d2 }
        r2 = r2.top;	 Catch:{ Throwable -> 0x01d2 }
        r5.top = r2;	 Catch:{ Throwable -> 0x01d2 }
        r2 = r11.lineSpacingExtra;	 Catch:{ Throwable -> 0x01d2 }
        r2 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1));	 Catch:{ Throwable -> 0x01d2 }
        if (r2 == 0) goto L_0x01a9;	 Catch:{ Throwable -> 0x01d2 }
    L_0x019b:
        r0 = r0 - r3;	 Catch:{ Throwable -> 0x01d2 }
        if (r1 >= r0) goto L_0x01a9;	 Catch:{ Throwable -> 0x01d2 }
    L_0x019e:
        r0 = r11.rect;	 Catch:{ Throwable -> 0x01d2 }
        r1 = r0.bottom;	 Catch:{ Throwable -> 0x01d2 }
        r1 = (float) r1;	 Catch:{ Throwable -> 0x01d2 }
        r2 = r11.lineSpacingExtra;	 Catch:{ Throwable -> 0x01d2 }
        r1 = r1 - r2;	 Catch:{ Throwable -> 0x01d2 }
        r1 = (int) r1;	 Catch:{ Throwable -> 0x01d2 }
        r0.bottom = r1;	 Catch:{ Throwable -> 0x01d2 }
    L_0x01a9:
        r0 = r11.rect;	 Catch:{ Throwable -> 0x01d2 }
        r1 = r11.rect;	 Catch:{ Throwable -> 0x01d2 }
        r1 = r1.centerY();	 Catch:{ Throwable -> 0x01d2 }
        r2 = r11.cursorSize;	 Catch:{ Throwable -> 0x01d2 }
        r2 = r2 / 2;	 Catch:{ Throwable -> 0x01d2 }
        r1 = r1 - r2;	 Catch:{ Throwable -> 0x01d2 }
        r0.top = r1;	 Catch:{ Throwable -> 0x01d2 }
        r0 = r11.rect;	 Catch:{ Throwable -> 0x01d2 }
        r1 = r11.rect;	 Catch:{ Throwable -> 0x01d2 }
        r1 = r1.top;	 Catch:{ Throwable -> 0x01d2 }
        r2 = r11.cursorSize;	 Catch:{ Throwable -> 0x01d2 }
        r1 = r1 + r2;	 Catch:{ Throwable -> 0x01d2 }
        r0.bottom = r1;	 Catch:{ Throwable -> 0x01d2 }
        r0 = r11.gradientDrawable;	 Catch:{ Throwable -> 0x01d2 }
        r1 = r11.rect;	 Catch:{ Throwable -> 0x01d2 }
        r0.setBounds(r1);	 Catch:{ Throwable -> 0x01d2 }
        r0 = r11.gradientDrawable;	 Catch:{ Throwable -> 0x01d2 }
        r0.draw(r12);	 Catch:{ Throwable -> 0x01d2 }
        r12.restore();	 Catch:{ Throwable -> 0x01d2 }
    L_0x01d2:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.EditTextBoldCursor.onDraw(android.graphics.Canvas):void");
    }
}
