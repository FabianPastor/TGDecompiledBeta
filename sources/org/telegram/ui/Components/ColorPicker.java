package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.RadialGradient;
import android.graphics.Shader.TileMode;
import android.graphics.SweepGradient;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;

public class ColorPicker extends FrameLayout {
    private int centerX;
    private int centerY;
    private Drawable circleDrawable;
    private Paint circlePaint;
    private boolean circlePressed;
    private EditTextBoldCursor[] colorEditText = new EditTextBoldCursor[2];
    private LinearGradient colorGradient;
    private float[] colorHSV = new float[]{0.0f, 0.0f, 1.0f};
    private boolean colorPressed;
    private Bitmap colorWheelBitmap;
    private Paint colorWheelPaint;
    private int colorWheelRadius;
    private final ColorPickerDelegate delegate;
    private float[] hsvTemp = new float[3];
    boolean ignoreTextChange;
    private LinearLayout linearLayout;
    private int lx;
    private int ly;
    private BrightnessLimit maxBrightness;
    private BrightnessLimit minBrightness;
    private final int paramValueSliderWidth = AndroidUtilities.dp(20.0f);
    private Paint valueSliderPaint;

    public interface BrightnessLimit {
        float getLimit(int i, int i2, int i3);
    }

    public interface ColorPickerDelegate {
        void setColor(int i);
    }

    public ColorPicker(Context context, ColorPickerDelegate colorPickerDelegate) {
        Context context2 = context;
        final ColorPickerDelegate colorPickerDelegate2 = colorPickerDelegate;
        super(context);
        this.delegate = colorPickerDelegate2;
        setWillNotDraw(false);
        this.circlePaint = new Paint(1);
        this.circleDrawable = context.getResources().getDrawable(NUM).mutate();
        this.colorWheelPaint = new Paint();
        this.colorWheelPaint.setAntiAlias(true);
        this.colorWheelPaint.setDither(true);
        this.valueSliderPaint = new Paint();
        this.valueSliderPaint.setAntiAlias(true);
        this.valueSliderPaint.setDither(true);
        this.linearLayout = new LinearLayout(context2);
        this.linearLayout.setOrientation(0);
        addView(this.linearLayout, LayoutHelper.createFrame(-1, 46.0f, 51, 12.0f, 20.0f, 21.0f, 14.0f));
        int i = 0;
        while (i < 2) {
            this.colorEditText[i] = new EditTextBoldCursor(context2);
            this.colorEditText[i].setTextSize(1, 18.0f);
            this.colorEditText[i].setHintColor(Theme.getColor("windowBackgroundWhiteHintText"));
            String str = "windowBackgroundWhiteBlackText";
            this.colorEditText[i].setTextColor(Theme.getColor(str));
            this.colorEditText[i].setBackgroundDrawable(Theme.createEditTextDrawable(context2, false));
            this.colorEditText[i].setCursorColor(Theme.getColor(str));
            this.colorEditText[i].setCursorSize(AndroidUtilities.dp(20.0f));
            this.colorEditText[i].setCursorWidth(1.5f);
            this.colorEditText[i].setSingleLine(true);
            this.colorEditText[i].setGravity(19);
            this.colorEditText[i].setHeaderHintColor(Theme.getColor("windowBackgroundWhiteBlueHeader"));
            this.colorEditText[i].setTransformHintToHeader(true);
            if (i == 0) {
                this.colorEditText[i].setInputType(1);
                this.colorEditText[i].setHintText(LocaleController.getString("BackgroundHexColorCode", NUM));
            } else {
                this.colorEditText[i].setInputType(2);
                this.colorEditText[i].setHintText(LocaleController.getString("BackgroundBrightness", NUM));
            }
            this.colorEditText[i].setImeOptions(NUM);
            InputFilter[] inputFilterArr = new InputFilter[1];
            inputFilterArr[0] = new LengthFilter(i == 0 ? 7 : 3);
            this.colorEditText[i].setFilters(inputFilterArr);
            this.colorEditText[i].setPadding(0, AndroidUtilities.dp(6.0f), 0, 0);
            this.linearLayout.addView(this.colorEditText[i], LayoutHelper.createLinear(0, -1, i == 0 ? 0.67f : 0.31f, 0, 0, i != 1 ? 23 : 0, 0));
            this.colorEditText[i].addTextChangedListener(new TextWatcher() {
                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                public void afterTextChanged(Editable editable) {
                    ColorPicker colorPicker = ColorPicker.this;
                    if (!colorPicker.ignoreTextChange) {
                        int i;
                        int length;
                        colorPicker.ignoreTextChange = true;
                        String str = "";
                        if (i == 0) {
                            i = 0;
                            while (i < editable.length()) {
                                char charAt = editable.charAt(i);
                                if ((charAt < '0' || charAt > '9') && ((charAt < 'a' || charAt > 'f') && ((charAt < 'A' || charAt > 'F') && !(charAt == '#' && i == 0)))) {
                                    editable.replace(i, i + 1, str);
                                    i--;
                                }
                                i++;
                            }
                            str = "#";
                            if (editable.length() == 0) {
                                editable.append(str);
                            } else if (editable.charAt(0) != '#') {
                                editable.insert(0, str);
                            }
                            if (editable.length() != 7) {
                                ColorPicker.this.ignoreTextChange = false;
                                return;
                            } else {
                                try {
                                    ColorPicker.this.setColor(Integer.parseInt(editable.toString().substring(1), 16) | -16777216);
                                } catch (Exception unused) {
                                    ColorPicker.this.setColor(-1);
                                }
                            }
                        } else {
                            i = Utilities.parseInt(editable.toString()).intValue();
                            if (i > 255 || i < 0) {
                                i = i > 255 ? 255 : 0;
                                length = editable.length();
                                StringBuilder stringBuilder = new StringBuilder();
                                stringBuilder.append(str);
                                stringBuilder.append(i);
                                editable.replace(0, length, stringBuilder.toString());
                            }
                            ColorPicker.this.colorHSV[2] = ((float) i) / 255.0f;
                        }
                        int color = ColorPicker.this.getColor();
                        i = Color.red(color);
                        int green = Color.green(color);
                        length = Color.blue(color);
                        ColorPicker.this.colorEditText[0].setTextKeepState(String.format("#%02x%02x%02x", new Object[]{Byte.valueOf((byte) i), Byte.valueOf((byte) green), Byte.valueOf((byte) length)}).toUpperCase());
                        ColorPicker.this.colorEditText[1].setTextKeepState(String.valueOf((int) (ColorPicker.this.getBrightness() * 255.0f)));
                        colorPickerDelegate2.setColor(color);
                        ColorPicker.this.ignoreTextChange = false;
                    }
                }
            });
            this.colorEditText[i].setOnEditorActionListener(-$$Lambda$ColorPicker$S-MA1WCYplDVX93yXNlL7g0yGWk.INSTANCE);
            i++;
        }
    }

    static /* synthetic */ boolean lambda$new$0(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 6) {
            return false;
        }
        AndroidUtilities.hideKeyboard(textView);
        return true;
    }

    /* Access modifiers changed, original: protected */
    public void onMeasure(int i, int i2) {
        i = MeasureSpec.getSize(i);
        int min = Math.min(i, MeasureSpec.getSize(i2));
        measureChild(this.linearLayout, MeasureSpec.makeMeasureSpec(i - AndroidUtilities.dp(42.0f), NUM), i2);
        setMeasuredDimension(min, min);
    }

    /* Access modifiers changed, original: protected */
    public void onDraw(Canvas canvas) {
        Canvas canvas2 = canvas;
        this.centerX = ((getWidth() / 2) - (this.paramValueSliderWidth * 2)) + AndroidUtilities.dp(11.0f);
        this.centerY = (getHeight() / 2) + AndroidUtilities.dp(34.0f);
        Bitmap bitmap = this.colorWheelBitmap;
        int i = this.centerX;
        int i2 = this.colorWheelRadius;
        canvas2.drawBitmap(bitmap, (float) (i - i2), (float) (this.centerY - i2), null);
        double toRadians = (double) ((float) Math.toRadians((double) this.colorHSV[0]));
        double d = -Math.cos(toRadians);
        double d2 = (double) this.colorHSV[1];
        Double.isNaN(d2);
        d *= d2;
        d2 = (double) this.colorWheelRadius;
        Double.isNaN(d2);
        int i3 = ((int) (d * d2)) + this.centerX;
        toRadians = -Math.sin(toRadians);
        float[] fArr = this.colorHSV;
        d2 = (double) fArr[1];
        Double.isNaN(d2);
        toRadians *= d2;
        d2 = (double) this.colorWheelRadius;
        Double.isNaN(d2);
        i2 = ((int) (toRadians * d2)) + this.centerY;
        float[] fArr2 = this.hsvTemp;
        fArr2[0] = fArr[0];
        fArr2[1] = fArr[1];
        fArr2[2] = 1.0f;
        drawPointerArrow(canvas2, i3, i2, Color.HSVToColor(fArr2));
        i3 = this.centerX;
        i2 = this.colorWheelRadius;
        this.lx = (i3 + i2) + (this.paramValueSliderWidth * 2);
        this.ly = this.centerY - i2;
        int dp = AndroidUtilities.dp(9.0f);
        int i4 = this.colorWheelRadius * 2;
        if (this.colorGradient == null) {
            i2 = this.lx;
            float f = (float) i2;
            int i5 = this.ly;
            float f2 = (float) (i2 + dp);
            float f3 = (float) (i5 + i4);
            this.colorGradient = new LinearGradient(f, (float) i5, f2, f3, new int[]{-16777216, Color.HSVToColor(this.hsvTemp)}, null, TileMode.CLAMP);
        }
        this.valueSliderPaint.setShader(this.colorGradient);
        i3 = this.lx;
        float f4 = (float) i3;
        i2 = this.ly;
        canvas.drawRect(f4, (float) i2, (float) (i3 + dp), (float) (i2 + i4), this.valueSliderPaint);
        drawPointerArrow(canvas2, this.lx + (dp / 2), (int) (((float) this.ly) + (getBrightness() * ((float) i4))), getColor());
    }

    private void drawPointerArrow(Canvas canvas, int i, int i2, int i3) {
        int dp = AndroidUtilities.dp(13.0f);
        this.circleDrawable.setBounds(i - dp, i2 - dp, i + dp, dp + i2);
        this.circleDrawable.draw(canvas);
        this.circlePaint.setColor(-1);
        float f = (float) i;
        float f2 = (float) i2;
        canvas.drawCircle(f, f2, (float) AndroidUtilities.dp(11.0f), this.circlePaint);
        this.circlePaint.setColor(i3);
        canvas.drawCircle(f, f2, (float) AndroidUtilities.dp(9.0f), this.circlePaint);
    }

    /* Access modifiers changed, original: protected */
    public void onSizeChanged(int i, int i2, int i3, int i4) {
        if (this.colorWheelRadius != AndroidUtilities.dp(120.0f)) {
            this.colorWheelRadius = AndroidUtilities.dp(120.0f);
            i = this.colorWheelRadius;
            this.colorWheelBitmap = createColorWheelBitmap(i * 2, i * 2);
            this.colorGradient = null;
        }
    }

    private Bitmap createColorWheelBitmap(int i, int i2) {
        Bitmap createBitmap = Bitmap.createBitmap(i, i2, Config.ARGB_8888);
        int[] iArr = new int[13];
        float[] fArr = new float[]{0.0f, 1.0f, 1.0f};
        for (int i3 = 0; i3 < iArr.length; i3++) {
            fArr[0] = (float) (((i3 * 30) + 180) % 360);
            iArr[i3] = Color.HSVToColor(fArr);
        }
        iArr[12] = iArr[0];
        float f = ((float) i) * 0.5f;
        float f2 = ((float) i2) * 0.5f;
        this.colorWheelPaint.setShader(new ComposeShader(new SweepGradient(f, f2, iArr, null), new RadialGradient(f, f2, (float) this.colorWheelRadius, -1, 16777215, TileMode.CLAMP), Mode.SRC_OVER));
        new Canvas(createBitmap).drawCircle(f, f2, (float) this.colorWheelRadius, this.colorWheelPaint);
        return createBitmap;
    }

    /* JADX WARNING: Removed duplicated region for block: B:48:0x00db  */
    /* JADX WARNING: Missing block: B:3:0x000b, code skipped:
            if (r0 != 2) goto L_0x0012;
     */
    /* JADX WARNING: Missing block: B:31:0x00a0, code skipped:
            if (r15 <= (r0 + (r14.colorWheelRadius * 2))) goto L_0x00a2;
     */
    public boolean onTouchEvent(android.view.MotionEvent r15) {
        /*
        r14 = this;
        r0 = r15.getAction();
        r1 = 2;
        r2 = 0;
        r3 = 1;
        if (r0 == 0) goto L_0x0017;
    L_0x0009:
        if (r0 == r3) goto L_0x000e;
    L_0x000b:
        if (r0 == r1) goto L_0x0017;
    L_0x000d:
        goto L_0x0012;
    L_0x000e:
        r14.colorPressed = r2;
        r14.circlePressed = r2;
    L_0x0012:
        r15 = super.onTouchEvent(r15);
        return r15;
    L_0x0017:
        r0 = r15.getX();
        r0 = (int) r0;
        r15 = r15.getY();
        r15 = (int) r15;
        r4 = r14.centerX;
        r4 = r0 - r4;
        r5 = r14.centerY;
        r5 = r15 - r5;
        r6 = r4 * r4;
        r7 = r5 * r5;
        r6 = r6 + r7;
        r6 = (double) r6;
        r6 = java.lang.Math.sqrt(r6);
        r8 = r14.circlePressed;
        r9 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r10 = 0;
        if (r8 != 0) goto L_0x0045;
    L_0x003a:
        r8 = r14.colorPressed;
        if (r8 != 0) goto L_0x0086;
    L_0x003e:
        r8 = r14.colorWheelRadius;
        r11 = (double) r8;
        r8 = (r6 > r11 ? 1 : (r6 == r11 ? 0 : -1));
        if (r8 > 0) goto L_0x0086;
    L_0x0045:
        r8 = r14.colorWheelRadius;
        r11 = (double) r8;
        r13 = (r6 > r11 ? 1 : (r6 == r11 ? 0 : -1));
        if (r13 <= 0) goto L_0x004d;
    L_0x004c:
        r6 = (double) r8;
    L_0x004d:
        r8 = r14.circlePressed;
        if (r8 != 0) goto L_0x0058;
    L_0x0051:
        r8 = r14.getParent();
        r8.requestDisallowInterceptTouchEvent(r3);
    L_0x0058:
        r14.circlePressed = r3;
        r8 = r14.colorHSV;
        r11 = (double) r5;
        r4 = (double) r4;
        r4 = java.lang.Math.atan2(r11, r4);
        r4 = java.lang.Math.toDegrees(r4);
        r11 = NUM; // 0xNUM float:0.0 double:180.0;
        r4 = r4 + r11;
        r4 = (float) r4;
        r8[r2] = r4;
        r4 = r14.colorHSV;
        r5 = r14.colorWheelRadius;
        r11 = (double) r5;
        java.lang.Double.isNaN(r11);
        r6 = r6 / r11;
        r5 = (float) r6;
        r5 = java.lang.Math.min(r9, r5);
        r5 = java.lang.Math.max(r10, r5);
        r4[r3] = r5;
        r4 = 0;
        r14.colorGradient = r4;
    L_0x0086:
        r4 = r14.colorPressed;
        if (r4 != 0) goto L_0x00a2;
    L_0x008a:
        r4 = r14.circlePressed;
        if (r4 != 0) goto L_0x00cb;
    L_0x008e:
        r4 = r14.lx;
        if (r0 < r4) goto L_0x00cb;
    L_0x0092:
        r5 = r14.paramValueSliderWidth;
        r4 = r4 + r5;
        if (r0 > r4) goto L_0x00cb;
    L_0x0097:
        r0 = r14.ly;
        if (r15 < r0) goto L_0x00cb;
    L_0x009b:
        r4 = r14.colorWheelRadius;
        r4 = r4 * 2;
        r0 = r0 + r4;
        if (r15 > r0) goto L_0x00cb;
    L_0x00a2:
        r0 = r14.ly;
        r15 = r15 - r0;
        r15 = (float) r15;
        r0 = r14.colorWheelRadius;
        r0 = (float) r0;
        r4 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r0 = r0 * r4;
        r15 = r15 / r0;
        r0 = (r15 > r10 ? 1 : (r15 == r10 ? 0 : -1));
        if (r0 >= 0) goto L_0x00b4;
    L_0x00b2:
        r15 = 0;
        goto L_0x00ba;
    L_0x00b4:
        r0 = (r15 > r9 ? 1 : (r15 == r9 ? 0 : -1));
        if (r0 <= 0) goto L_0x00ba;
    L_0x00b8:
        r15 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
    L_0x00ba:
        r0 = r14.colorHSV;
        r0[r1] = r15;
        r15 = r14.colorPressed;
        if (r15 != 0) goto L_0x00c9;
    L_0x00c2:
        r15 = r14.getParent();
        r15.requestDisallowInterceptTouchEvent(r3);
    L_0x00c9:
        r14.colorPressed = r3;
    L_0x00cb:
        r15 = r14.colorPressed;
        if (r15 != 0) goto L_0x00d3;
    L_0x00cf:
        r15 = r14.circlePressed;
        if (r15 == 0) goto L_0x0143;
    L_0x00d3:
        r15 = r14.getColor();
        r0 = r14.ignoreTextChange;
        if (r0 != 0) goto L_0x013b;
    L_0x00db:
        r0 = android.graphics.Color.red(r15);
        r4 = android.graphics.Color.green(r15);
        r5 = android.graphics.Color.blue(r15);
        r14.ignoreTextChange = r3;
        r6 = r14.colorEditText;
        r6 = r6[r2];
        r7 = 3;
        r7 = new java.lang.Object[r7];
        r0 = (byte) r0;
        r0 = java.lang.Byte.valueOf(r0);
        r7[r2] = r0;
        r0 = (byte) r4;
        r0 = java.lang.Byte.valueOf(r0);
        r7[r3] = r0;
        r0 = (byte) r5;
        r0 = java.lang.Byte.valueOf(r0);
        r7[r1] = r0;
        r0 = "#%02x%02x%02x";
        r0 = java.lang.String.format(r0, r7);
        r0 = r0.toUpperCase();
        r6.setText(r0);
        r0 = r14.colorEditText;
        r0 = r0[r3];
        r4 = NUM; // 0x437var_ float:255.0 double:5.5947823E-315;
        r5 = r14.getBrightness();
        r5 = r5 * r4;
        r4 = (int) r5;
        r4 = java.lang.String.valueOf(r4);
        r0.setText(r4);
        r0 = 0;
    L_0x0127:
        if (r0 >= r1) goto L_0x0139;
    L_0x0129:
        r4 = r14.colorEditText;
        r5 = r4[r0];
        r4 = r4[r0];
        r4 = r4.length();
        r5.setSelection(r4);
        r0 = r0 + 1;
        goto L_0x0127;
    L_0x0139:
        r14.ignoreTextChange = r2;
    L_0x013b:
        r0 = r14.delegate;
        r0.setColor(r15);
        r14.invalidate();
    L_0x0143:
        return r3;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ColorPicker.onTouchEvent(android.view.MotionEvent):boolean");
    }

    public void setColor(int i) {
        if (this.ignoreTextChange) {
            Color.colorToHSV(i, this.colorHSV);
        } else {
            this.ignoreTextChange = true;
            int red = Color.red(i);
            int green = Color.green(i);
            int blue = Color.blue(i);
            Color.colorToHSV(i, this.colorHSV);
            this.colorEditText[0].setText(String.format("#%02x%02x%02x", new Object[]{Byte.valueOf((byte) red), Byte.valueOf((byte) green), Byte.valueOf((byte) blue)}).toUpperCase());
            this.colorEditText[1].setText(String.valueOf((int) (getBrightness() * 255.0f)));
            for (i = 0; i < 2; i++) {
                EditTextBoldCursor[] editTextBoldCursorArr = this.colorEditText;
                editTextBoldCursorArr[i].setSelection(editTextBoldCursorArr[i].length());
            }
            this.ignoreTextChange = false;
        }
        this.colorGradient = null;
        invalidate();
    }

    public int getColor() {
        float[] fArr = this.hsvTemp;
        float[] fArr2 = this.colorHSV;
        fArr[0] = fArr2[0];
        fArr[1] = fArr2[1];
        fArr[2] = getBrightness();
        return (Color.HSVToColor(this.hsvTemp) & 16777215) | -16777216;
    }

    private float getBrightness() {
        float[] fArr = this.colorHSV;
        float f = fArr[2];
        float f2 = 1.0f;
        fArr[2] = 1.0f;
        int HSVToColor = Color.HSVToColor(fArr);
        int red = Color.red(HSVToColor);
        int green = Color.green(HSVToColor);
        HSVToColor = Color.blue(HSVToColor);
        this.colorHSV[2] = f;
        BrightnessLimit brightnessLimit = this.minBrightness;
        float limit = brightnessLimit == null ? 0.0f : brightnessLimit.getLimit(red, green, HSVToColor);
        BrightnessLimit brightnessLimit2 = this.maxBrightness;
        if (brightnessLimit2 != null) {
            f2 = brightnessLimit2.getLimit(red, green, HSVToColor);
        }
        return Math.max(limit, Math.min(f, f2));
    }

    public void setMinBrightness(BrightnessLimit brightnessLimit) {
        this.minBrightness = brightnessLimit;
    }

    public void setMaxBrightness(BrightnessLimit brightnessLimit) {
        this.maxBrightness = brightnessLimit;
    }

    public void provideThemeDescriptions(List<ThemeDescription> list) {
        List<ThemeDescription> list2 = list;
        int i = 0;
        while (true) {
            EditTextBoldCursor[] editTextBoldCursorArr = this.colorEditText;
            if (i < editTextBoldCursorArr.length) {
                list2.add(new ThemeDescription(editTextBoldCursorArr[i], ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"));
                list2.add(new ThemeDescription(this.colorEditText[i], ThemeDescription.FLAG_CURSORCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"));
                list2.add(new ThemeDescription(this.colorEditText[i], ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText"));
                list2.add(new ThemeDescription(this.colorEditText[i], ThemeDescription.FLAG_HINTTEXTCOLOR | ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, "windowBackgroundWhiteBlueHeader"));
                list2.add(new ThemeDescription(this.colorEditText[i], ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputField"));
                list2.add(new ThemeDescription(this.colorEditText[i], ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "windowBackgroundWhiteInputFieldActivated"));
                i++;
            } else {
                return;
            }
        }
    }
}
