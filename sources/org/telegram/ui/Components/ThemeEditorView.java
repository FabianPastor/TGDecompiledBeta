package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Keep;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.TextColorThemeCell;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.ui.Components.WallpaperUpdater.WallpaperUpdaterDelegate;
import org.telegram.ui.LaunchActivity;

public class ThemeEditorView {
    @SuppressLint({"StaticFieldLeak"})
    private static volatile ThemeEditorView Instance;
    private ArrayList<ThemeDescription> currentThemeDesription;
    private int currentThemeDesriptionPosition;
    private String currentThemeName;
    private DecelerateInterpolator decelerateInterpolator;
    private EditorAlert editorAlert;
    private final int editorHeight = AndroidUtilities.dp(54.0f);
    private final int editorWidth = AndroidUtilities.dp(54.0f);
    private boolean hidden;
    private Activity parentActivity;
    private SharedPreferences preferences;
    private WallpaperUpdater wallpaperUpdater;
    private LayoutParams windowLayoutParams;
    private WindowManager windowManager;
    private FrameLayout windowView;

    public class EditorAlert extends BottomSheet {
        private boolean animationInProgress;
        private FrameLayout bottomLayout;
        private FrameLayout bottomSaveLayout;
        private TextView cancelButton;
        private AnimatorSet colorChangeAnimation;
        private ColorPicker colorPicker;
        private TextView defaultButtom;
        private boolean ignoreTextChange;
        private LinearLayoutManager layoutManager;
        private ListAdapter listAdapter;
        private RecyclerListView listView;
        private int previousScrollPosition;
        private TextView saveButton;
        private int scrollOffsetY;
        private View shadow;
        private Drawable shadowDrawable;
        private boolean startedColorChange;
        final /* synthetic */ ThemeEditorView this$0;
        private int topBeforeSwitch;

        private class ColorPicker extends FrameLayout {
            private float alpha = 1.0f;
            private LinearGradient alphaGradient;
            private boolean alphaPressed;
            private Drawable circleDrawable;
            private Paint circlePaint;
            private boolean circlePressed;
            private EditTextBoldCursor[] colorEditText = new EditTextBoldCursor[4];
            private LinearGradient colorGradient;
            private float[] colorHSV = new float[]{0.0f, 0.0f, 1.0f};
            private boolean colorPressed;
            private Bitmap colorWheelBitmap;
            private Paint colorWheelPaint;
            private int colorWheelRadius;
            private DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator();
            private float[] hsvTemp = new float[3];
            private LinearLayout linearLayout;
            private final int paramValueSliderWidth = AndroidUtilities.dp(20.0f);
            final /* synthetic */ EditorAlert this$1;
            private Paint valueSliderPaint;

            public ColorPicker(EditorAlert editorAlert, Context context) {
                final EditorAlert editorAlert2 = editorAlert;
                Context context2 = context;
                this.this$1 = editorAlert2;
                super(context2);
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
                addView(this.linearLayout, LayoutHelper.createFrame(-2, -2, 49));
                int i = 0;
                while (i < 4) {
                    this.colorEditText[i] = new EditTextBoldCursor(context2);
                    this.colorEditText[i].setInputType(2);
                    this.colorEditText[i].setTextColor(-14606047);
                    this.colorEditText[i].setCursorColor(-14606047);
                    this.colorEditText[i].setCursorSize(AndroidUtilities.dp(20.0f));
                    this.colorEditText[i].setCursorWidth(1.5f);
                    this.colorEditText[i].setTextSize(1, 18.0f);
                    this.colorEditText[i].setBackgroundDrawable(Theme.createEditTextDrawable(context2, true));
                    this.colorEditText[i].setMaxLines(1);
                    this.colorEditText[i].setTag(Integer.valueOf(i));
                    this.colorEditText[i].setGravity(17);
                    if (i == 0) {
                        this.colorEditText[i].setHint("red");
                    } else if (i == 1) {
                        this.colorEditText[i].setHint("green");
                    } else if (i == 2) {
                        this.colorEditText[i].setHint("blue");
                    } else if (i == 3) {
                        this.colorEditText[i].setHint("alpha");
                    }
                    this.colorEditText[i].setImeOptions((i == 3 ? 6 : 5) | NUM);
                    this.colorEditText[i].setFilters(new InputFilter[]{new LengthFilter(3)});
                    this.linearLayout.addView(this.colorEditText[i], LayoutHelper.createLinear(55, 36, 0.0f, 0.0f, i != 3 ? 16.0f : 0.0f, 0.0f));
                    this.colorEditText[i].addTextChangedListener(new TextWatcher() {
                        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                        }

                        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                        }

                        /* JADX WARNING: Removed duplicated region for block: B:22:0x00df A:{LOOP_END, LOOP:0: B:20:0x00cf->B:22:0x00df} */
                        public void afterTextChanged(android.text.Editable r7) {
                            /*
                            r6 = this;
                            r0 = org.telegram.ui.Components.ThemeEditorView.EditorAlert.ColorPicker.this;
                            r0 = r0.this$1;
                            r0 = r0.ignoreTextChange;
                            if (r0 == 0) goto L_0x000b;
                        L_0x000a:
                            return;
                        L_0x000b:
                            r0 = org.telegram.ui.Components.ThemeEditorView.EditorAlert.ColorPicker.this;
                            r0 = r0.this$1;
                            r1 = 1;
                            r0.ignoreTextChange = r1;
                            r7 = r7.toString();
                            r7 = org.telegram.messenger.Utilities.parseInt(r7);
                            r7 = r7.intValue();
                            r0 = "";
                            r2 = 0;
                            r3 = 255; // 0xff float:3.57E-43 double:1.26E-321;
                            if (r7 >= 0) goto L_0x005f;
                        L_0x0026:
                            r7 = org.telegram.ui.Components.ThemeEditorView.EditorAlert.ColorPicker.this;
                            r7 = r7.colorEditText;
                            r4 = r7;
                            r7 = r7[r4];
                            r4 = new java.lang.StringBuilder;
                            r4.<init>();
                            r4.append(r0);
                            r4.append(r2);
                            r0 = r4.toString();
                            r7.setText(r0);
                            r7 = org.telegram.ui.Components.ThemeEditorView.EditorAlert.ColorPicker.this;
                            r7 = r7.colorEditText;
                            r0 = r7;
                            r7 = r7[r0];
                            r0 = org.telegram.ui.Components.ThemeEditorView.EditorAlert.ColorPicker.this;
                            r0 = r0.colorEditText;
                            r4 = r7;
                            r0 = r0[r4];
                            r0 = r0.length();
                            r7.setSelection(r0);
                            r7 = 0;
                            goto L_0x009a;
                        L_0x005f:
                            if (r7 <= r3) goto L_0x009a;
                        L_0x0061:
                            r7 = org.telegram.ui.Components.ThemeEditorView.EditorAlert.ColorPicker.this;
                            r7 = r7.colorEditText;
                            r4 = r7;
                            r7 = r7[r4];
                            r4 = new java.lang.StringBuilder;
                            r4.<init>();
                            r4.append(r0);
                            r4.append(r3);
                            r0 = r4.toString();
                            r7.setText(r0);
                            r7 = org.telegram.ui.Components.ThemeEditorView.EditorAlert.ColorPicker.this;
                            r7 = r7.colorEditText;
                            r0 = r7;
                            r7 = r7[r0];
                            r0 = org.telegram.ui.Components.ThemeEditorView.EditorAlert.ColorPicker.this;
                            r0 = r0.colorEditText;
                            r4 = r7;
                            r0 = r0[r4];
                            r0 = r0.length();
                            r7.setSelection(r0);
                            r7 = 255; // 0xff float:3.57E-43 double:1.26E-321;
                        L_0x009a:
                            r0 = org.telegram.ui.Components.ThemeEditorView.EditorAlert.ColorPicker.this;
                            r0 = r0.getColor();
                            r4 = r7;
                            r5 = 2;
                            if (r4 != r5) goto L_0x00aa;
                        L_0x00a5:
                            r0 = r0 & -256;
                            r7 = r7 & r3;
                        L_0x00a8:
                            r0 = r0 | r7;
                            goto L_0x00c9;
                        L_0x00aa:
                            if (r4 != r1) goto L_0x00b4;
                        L_0x00ac:
                            r1 = -65281; // 0xfffffffffffvar_ff float:NaN double:NaN;
                            r0 = r0 & r1;
                            r7 = r7 & r3;
                            r7 = r7 << 8;
                            goto L_0x00a8;
                        L_0x00b4:
                            if (r4 != 0) goto L_0x00be;
                        L_0x00b6:
                            r1 = -16711681; // 0xfffffffffvar_ffff float:-1.714704E38 double:NaN;
                            r0 = r0 & r1;
                            r7 = r7 & r3;
                            r7 = r7 << 16;
                            goto L_0x00a8;
                        L_0x00be:
                            r1 = 3;
                            if (r4 != r1) goto L_0x00c9;
                        L_0x00c1:
                            r1 = 16777215; // 0xffffff float:2.3509886E-38 double:8.2890456E-317;
                            r0 = r0 & r1;
                            r7 = r7 & r3;
                            r7 = r7 << 24;
                            goto L_0x00a8;
                        L_0x00c9:
                            r7 = org.telegram.ui.Components.ThemeEditorView.EditorAlert.ColorPicker.this;
                            r7.setColor(r0);
                            r7 = 0;
                        L_0x00cf:
                            r0 = org.telegram.ui.Components.ThemeEditorView.EditorAlert.ColorPicker.this;
                            r0 = r0.this$1;
                            r0 = r0.this$0;
                            r0 = r0.currentThemeDesription;
                            r0 = r0.size();
                            if (r7 >= r0) goto L_0x00fb;
                        L_0x00df:
                            r0 = org.telegram.ui.Components.ThemeEditorView.EditorAlert.ColorPicker.this;
                            r0 = r0.this$1;
                            r0 = r0.this$0;
                            r0 = r0.currentThemeDesription;
                            r0 = r0.get(r7);
                            r0 = (org.telegram.ui.ActionBar.ThemeDescription) r0;
                            r1 = org.telegram.ui.Components.ThemeEditorView.EditorAlert.ColorPicker.this;
                            r1 = r1.getColor();
                            r0.setColor(r1, r2);
                            r7 = r7 + 1;
                            goto L_0x00cf;
                        L_0x00fb:
                            r7 = org.telegram.ui.Components.ThemeEditorView.EditorAlert.ColorPicker.this;
                            r7 = r7.this$1;
                            r7.ignoreTextChange = r2;
                            return;
                            */
                            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ThemeEditorView$EditorAlert$ColorPicker$AnonymousClass1.afterTextChanged(android.text.Editable):void");
                        }
                    });
                    this.colorEditText[i].setOnEditorActionListener(-$$Lambda$ThemeEditorView$EditorAlert$ColorPicker$ajPoxH4sFpvaVlkZvar_J0Sean_E.INSTANCE);
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
                int min = Math.min(MeasureSpec.getSize(i), MeasureSpec.getSize(i2));
                measureChild(this.linearLayout, i, i2);
                setMeasuredDimension(min, min);
            }

            /* Access modifiers changed, original: protected */
            public void onDraw(Canvas canvas) {
                float f;
                Canvas canvas2 = canvas;
                int width = (getWidth() / 2) - (this.paramValueSliderWidth * 2);
                int height = (getHeight() / 2) - AndroidUtilities.dp(8.0f);
                Bitmap bitmap = this.colorWheelBitmap;
                int i = this.colorWheelRadius;
                canvas2.drawBitmap(bitmap, (float) (width - i), (float) (height - i), null);
                double toRadians = (double) ((float) Math.toRadians((double) this.colorHSV[0]));
                double d = -Math.cos(toRadians);
                double d2 = (double) this.colorHSV[1];
                Double.isNaN(d2);
                d *= d2;
                d2 = (double) this.colorWheelRadius;
                Double.isNaN(d2);
                int i2 = ((int) (d * d2)) + width;
                toRadians = -Math.sin(toRadians);
                float[] fArr = this.colorHSV;
                d2 = (double) fArr[1];
                Double.isNaN(d2);
                toRadians *= d2;
                d2 = (double) this.colorWheelRadius;
                Double.isNaN(d2);
                int i3 = ((int) (toRadians * d2)) + height;
                float[] fArr2 = this.hsvTemp;
                fArr2[0] = fArr[0];
                fArr2[1] = fArr[1];
                fArr2[2] = 1.0f;
                drawPointerArrow(canvas2, i2, i3, Color.HSVToColor(fArr2));
                i3 = this.colorWheelRadius;
                int i4 = (width + i3) + this.paramValueSliderWidth;
                height -= i3;
                int dp = AndroidUtilities.dp(9.0f);
                int i5 = this.colorWheelRadius * 2;
                if (this.colorGradient == null) {
                    this.colorGradient = new LinearGradient((float) i4, (float) height, (float) (i4 + dp), (float) (height + i5), new int[]{-16777216, Color.HSVToColor(this.hsvTemp)}, null, TileMode.CLAMP);
                }
                this.valueSliderPaint.setShader(this.colorGradient);
                float f2 = (float) height;
                float f3 = (float) (height + i5);
                canvas.drawRect((float) i4, f2, (float) (i4 + dp), f3, this.valueSliderPaint);
                int i6 = dp / 2;
                width = i4 + i6;
                float[] fArr3 = this.colorHSV;
                float f4 = (float) i5;
                drawPointerArrow(canvas2, width, (int) ((fArr3[2] * f4) + f2), Color.HSVToColor(fArr3));
                i4 += this.paramValueSliderWidth * 2;
                if (this.alphaGradient == null) {
                    width = Color.HSVToColor(this.hsvTemp);
                    f = f3;
                    this.alphaGradient = new LinearGradient((float) i4, f2, (float) (i4 + dp), f, new int[]{width, width & 16777215}, null, TileMode.CLAMP);
                } else {
                    f = f3;
                }
                this.valueSliderPaint.setShader(this.alphaGradient);
                canvas.drawRect((float) i4, f2, (float) (dp + i4), f, this.valueSliderPaint);
                drawPointerArrow(canvas2, i4 + i6, (int) (f2 + ((1.0f - this.alpha) * f4)), (Color.HSVToColor(this.colorHSV) & 16777215) | (((int) (this.alpha * 255.0f)) << 24));
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
                this.colorWheelRadius = Math.max(1, ((i / 2) - (this.paramValueSliderWidth * 2)) - AndroidUtilities.dp(20.0f));
                i = this.colorWheelRadius;
                this.colorWheelBitmap = createColorWheelBitmap(i * 2, i * 2);
                this.colorGradient = null;
                this.alphaGradient = null;
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
                float f = (float) (i / 2);
                float f2 = (float) (i2 / 2);
                this.colorWheelPaint.setShader(new ComposeShader(new SweepGradient(f, f2, iArr, null), new RadialGradient(f, f2, (float) this.colorWheelRadius, -1, 16777215, TileMode.CLAMP), Mode.SRC_OVER));
                new Canvas(createBitmap).drawCircle(f, f2, (float) this.colorWheelRadius, this.colorWheelPaint);
                return createBitmap;
            }

            private void startColorChange(boolean z) {
                if (this.this$1.startedColorChange != z) {
                    if (this.this$1.colorChangeAnimation != null) {
                        this.this$1.colorChangeAnimation.cancel();
                    }
                    this.this$1.startedColorChange = z;
                    this.this$1.colorChangeAnimation = new AnimatorSet();
                    AnimatorSet access$400 = this.this$1.colorChangeAnimation;
                    Animator[] animatorArr = new Animator[2];
                    ColorDrawable access$500 = this.this$1.backDrawable;
                    int[] iArr = new int[1];
                    iArr[0] = z ? 0 : 51;
                    String str = "alpha";
                    animatorArr[0] = ObjectAnimator.ofInt(access$500, str, iArr);
                    ViewGroup access$600 = this.this$1.containerView;
                    float[] fArr = new float[1];
                    fArr[0] = z ? 0.2f : 1.0f;
                    animatorArr[1] = ObjectAnimator.ofFloat(access$600, str, fArr);
                    access$400.playTogether(animatorArr);
                    this.this$1.colorChangeAnimation.setDuration(150);
                    this.this$1.colorChangeAnimation.setInterpolator(this.decelerateInterpolator);
                    this.this$1.colorChangeAnimation.start();
                }
            }

            /* JADX WARNING: Removed duplicated region for block: B:70:0x0148 A:{LOOP_END, LOOP:0: B:68:0x013a->B:70:0x0148} */
            /* JADX WARNING: Removed duplicated region for block: B:73:0x0174  */
            /* JADX WARNING: Removed duplicated region for block: B:42:0x00e5  */
            /* JADX WARNING: Removed duplicated region for block: B:57:0x011e  */
            /* JADX WARNING: Removed duplicated region for block: B:56:0x011b  */
            /* JADX WARNING: Removed duplicated region for block: B:70:0x0148 A:{LOOP_END, LOOP:0: B:68:0x013a->B:70:0x0148} */
            /* JADX WARNING: Removed duplicated region for block: B:73:0x0174  */
            /* JADX WARNING: Missing block: B:3:0x000d, code skipped:
            if (r1 != 2) goto L_0x0019;
     */
            /* JADX WARNING: Missing block: B:32:0x00bd, code skipped:
            if (r5 <= (r8 + r7)) goto L_0x00bf;
     */
            /* JADX WARNING: Missing block: B:53:0x0103, code skipped:
            if (r5 <= (r8 + r7)) goto L_0x0105;
     */
            public boolean onTouchEvent(android.view.MotionEvent r17) {
                /*
                r16 = this;
                r0 = r16;
                r1 = r17.getAction();
                r2 = 2;
                r3 = 0;
                r4 = 1;
                if (r1 == 0) goto L_0x001e;
            L_0x000b:
                if (r1 == r4) goto L_0x0010;
            L_0x000d:
                if (r1 == r2) goto L_0x001e;
            L_0x000f:
                goto L_0x0019;
            L_0x0010:
                r0.alphaPressed = r3;
                r0.colorPressed = r3;
                r0.circlePressed = r3;
                r0.startColorChange(r3);
            L_0x0019:
                r1 = super.onTouchEvent(r17);
                return r1;
            L_0x001e:
                r1 = r17.getX();
                r1 = (int) r1;
                r5 = r17.getY();
                r5 = (int) r5;
                r6 = r16.getWidth();
                r6 = r6 / r2;
                r7 = r0.paramValueSliderWidth;
                r7 = r7 * 2;
                r6 = r6 - r7;
                r7 = r16.getHeight();
                r7 = r7 / r2;
                r8 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
                r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
                r7 = r7 - r8;
                r8 = r1 - r6;
                r9 = r5 - r7;
                r10 = r8 * r8;
                r11 = r9 * r9;
                r10 = r10 + r11;
                r10 = (double) r10;
                r10 = java.lang.Math.sqrt(r10);
                r12 = r0.circlePressed;
                if (r12 != 0) goto L_0x005f;
            L_0x0050:
                r12 = r0.alphaPressed;
                if (r12 != 0) goto L_0x009a;
            L_0x0054:
                r12 = r0.colorPressed;
                if (r12 != 0) goto L_0x009a;
            L_0x0058:
                r12 = r0.colorWheelRadius;
                r13 = (double) r12;
                r12 = (r10 > r13 ? 1 : (r10 == r13 ? 0 : -1));
                if (r12 > 0) goto L_0x009a;
            L_0x005f:
                r12 = r0.colorWheelRadius;
                r13 = (double) r12;
                r15 = (r10 > r13 ? 1 : (r10 == r13 ? 0 : -1));
                if (r15 <= 0) goto L_0x0067;
            L_0x0066:
                r10 = (double) r12;
            L_0x0067:
                r0.circlePressed = r4;
                r12 = r0.colorHSV;
                r13 = (double) r9;
                r8 = (double) r8;
                r8 = java.lang.Math.atan2(r13, r8);
                r8 = java.lang.Math.toDegrees(r8);
                r13 = NUM; // 0xNUM float:0.0 double:180.0;
                r8 = r8 + r13;
                r8 = (float) r8;
                r12[r3] = r8;
                r8 = r0.colorHSV;
                r9 = r0.colorWheelRadius;
                r12 = (double) r9;
                java.lang.Double.isNaN(r12);
                r10 = r10 / r12;
                r9 = (float) r10;
                r10 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
                r9 = java.lang.Math.min(r10, r9);
                r10 = 0;
                r9 = java.lang.Math.max(r10, r9);
                r8[r4] = r9;
                r8 = 0;
                r0.colorGradient = r8;
                r0.alphaGradient = r8;
            L_0x009a:
                r8 = r0.colorPressed;
                r9 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
                if (r8 != 0) goto L_0x00bf;
            L_0x00a0:
                r8 = r0.circlePressed;
                if (r8 != 0) goto L_0x00e0;
            L_0x00a4:
                r8 = r0.alphaPressed;
                if (r8 != 0) goto L_0x00e0;
            L_0x00a8:
                r8 = r0.colorWheelRadius;
                r10 = r6 + r8;
                r11 = r0.paramValueSliderWidth;
                r10 = r10 + r11;
                if (r1 < r10) goto L_0x00e0;
            L_0x00b1:
                r10 = r6 + r8;
                r11 = r11 * 2;
                r10 = r10 + r11;
                if (r1 > r10) goto L_0x00e0;
            L_0x00b8:
                r10 = r7 - r8;
                if (r5 < r10) goto L_0x00e0;
            L_0x00bc:
                r8 = r8 + r7;
                if (r5 > r8) goto L_0x00e0;
            L_0x00bf:
                r8 = r0.colorWheelRadius;
                r10 = r7 - r8;
                r10 = r5 - r10;
                r10 = (float) r10;
                r8 = (float) r8;
                r8 = r8 * r9;
                r13 = r10 / r8;
                r8 = 0;
                r10 = (r13 > r8 ? 1 : (r13 == r8 ? 0 : -1));
                if (r10 >= 0) goto L_0x00d2;
            L_0x00d0:
                r13 = 0;
                goto L_0x00da;
            L_0x00d2:
                r8 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
                r10 = (r13 > r8 ? 1 : (r13 == r8 ? 0 : -1));
                if (r10 <= 0) goto L_0x00da;
            L_0x00d8:
                r13 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
            L_0x00da:
                r8 = r0.colorHSV;
                r8[r2] = r13;
                r0.colorPressed = r4;
            L_0x00e0:
                r8 = r0.alphaPressed;
                r10 = 4;
                if (r8 != 0) goto L_0x0105;
            L_0x00e5:
                r8 = r0.circlePressed;
                if (r8 != 0) goto L_0x0126;
            L_0x00e9:
                r8 = r0.colorPressed;
                if (r8 != 0) goto L_0x0126;
            L_0x00ed:
                r8 = r0.colorWheelRadius;
                r11 = r6 + r8;
                r12 = r0.paramValueSliderWidth;
                r13 = r12 * 3;
                r11 = r11 + r13;
                if (r1 < r11) goto L_0x0126;
            L_0x00f8:
                r6 = r6 + r8;
                r12 = r12 * 4;
                r6 = r6 + r12;
                if (r1 > r6) goto L_0x0126;
            L_0x00fe:
                r1 = r7 - r8;
                if (r5 < r1) goto L_0x0126;
            L_0x0102:
                r8 = r8 + r7;
                if (r5 > r8) goto L_0x0126;
            L_0x0105:
                r1 = r0.colorWheelRadius;
                r7 = r7 - r1;
                r5 = r5 - r7;
                r5 = (float) r5;
                r1 = (float) r1;
                r1 = r1 * r9;
                r5 = r5 / r1;
                r1 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
                r14 = r1 - r5;
                r0.alpha = r14;
                r5 = r0.alpha;
                r6 = 0;
                r7 = (r5 > r6 ? 1 : (r5 == r6 ? 0 : -1));
                if (r7 >= 0) goto L_0x011e;
            L_0x011b:
                r0.alpha = r6;
                goto L_0x0124;
            L_0x011e:
                r5 = (r5 > r1 ? 1 : (r5 == r1 ? 0 : -1));
                if (r5 <= 0) goto L_0x0124;
            L_0x0122:
                r0.alpha = r1;
            L_0x0124:
                r0.alphaPressed = r4;
            L_0x0126:
                r1 = r0.alphaPressed;
                if (r1 != 0) goto L_0x0132;
            L_0x012a:
                r1 = r0.colorPressed;
                if (r1 != 0) goto L_0x0132;
            L_0x012e:
                r1 = r0.circlePressed;
                if (r1 == 0) goto L_0x01ef;
            L_0x0132:
                r0.startColorChange(r4);
                r1 = r16.getColor();
                r5 = 0;
            L_0x013a:
                r6 = r0.this$1;
                r6 = r6.this$0;
                r6 = r6.currentThemeDesription;
                r6 = r6.size();
                if (r5 >= r6) goto L_0x015c;
            L_0x0148:
                r6 = r0.this$1;
                r6 = r6.this$0;
                r6 = r6.currentThemeDesription;
                r6 = r6.get(r5);
                r6 = (org.telegram.ui.ActionBar.ThemeDescription) r6;
                r6.setColor(r1, r3);
                r5 = r5 + 1;
                goto L_0x013a;
            L_0x015c:
                r5 = android.graphics.Color.red(r1);
                r6 = android.graphics.Color.green(r1);
                r7 = android.graphics.Color.blue(r1);
                r1 = android.graphics.Color.alpha(r1);
                r8 = r0.this$1;
                r8 = r8.ignoreTextChange;
                if (r8 != 0) goto L_0x01ec;
            L_0x0174:
                r8 = r0.this$1;
                r8.ignoreTextChange = r4;
                r8 = r0.colorEditText;
                r8 = r8[r3];
                r9 = new java.lang.StringBuilder;
                r9.<init>();
                r11 = "";
                r9.append(r11);
                r9.append(r5);
                r5 = r9.toString();
                r8.setText(r5);
                r5 = r0.colorEditText;
                r5 = r5[r4];
                r8 = new java.lang.StringBuilder;
                r8.<init>();
                r8.append(r11);
                r8.append(r6);
                r6 = r8.toString();
                r5.setText(r6);
                r5 = r0.colorEditText;
                r2 = r5[r2];
                r5 = new java.lang.StringBuilder;
                r5.<init>();
                r5.append(r11);
                r5.append(r7);
                r5 = r5.toString();
                r2.setText(r5);
                r2 = r0.colorEditText;
                r5 = 3;
                r2 = r2[r5];
                r5 = new java.lang.StringBuilder;
                r5.<init>();
                r5.append(r11);
                r5.append(r1);
                r1 = r5.toString();
                r2.setText(r1);
                r1 = 0;
            L_0x01d5:
                if (r1 >= r10) goto L_0x01e7;
            L_0x01d7:
                r2 = r0.colorEditText;
                r5 = r2[r1];
                r2 = r2[r1];
                r2 = r2.length();
                r5.setSelection(r2);
                r1 = r1 + 1;
                goto L_0x01d5;
            L_0x01e7:
                r1 = r0.this$1;
                r1.ignoreTextChange = r3;
            L_0x01ec:
                r16.invalidate();
            L_0x01ef:
                return r4;
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ThemeEditorView$EditorAlert$ColorPicker.onTouchEvent(android.view.MotionEvent):boolean");
            }

            public void setColor(int i) {
                int red = Color.red(i);
                int green = Color.green(i);
                int blue = Color.blue(i);
                int alpha = Color.alpha(i);
                if (!this.this$1.ignoreTextChange) {
                    this.this$1.ignoreTextChange = true;
                    EditText editText = this.colorEditText[0];
                    StringBuilder stringBuilder = new StringBuilder();
                    String str = "";
                    stringBuilder.append(str);
                    stringBuilder.append(red);
                    editText.setText(stringBuilder.toString());
                    EditText editText2 = this.colorEditText[1];
                    StringBuilder stringBuilder2 = new StringBuilder();
                    stringBuilder2.append(str);
                    stringBuilder2.append(green);
                    editText2.setText(stringBuilder2.toString());
                    editText2 = this.colorEditText[2];
                    StringBuilder stringBuilder3 = new StringBuilder();
                    stringBuilder3.append(str);
                    stringBuilder3.append(blue);
                    editText2.setText(stringBuilder3.toString());
                    editText2 = this.colorEditText[3];
                    stringBuilder3 = new StringBuilder();
                    stringBuilder3.append(str);
                    stringBuilder3.append(alpha);
                    editText2.setText(stringBuilder3.toString());
                    for (red = 0; red < 4; red++) {
                        EditTextBoldCursor[] editTextBoldCursorArr = this.colorEditText;
                        editTextBoldCursorArr[red].setSelection(editTextBoldCursorArr[red].length());
                    }
                    this.this$1.ignoreTextChange = false;
                }
                this.alphaGradient = null;
                this.colorGradient = null;
                this.alpha = ((float) alpha) / 255.0f;
                Color.colorToHSV(i, this.colorHSV);
                invalidate();
            }

            public int getColor() {
                return (Color.HSVToColor(this.colorHSV) & 16777215) | (((int) (this.alpha * 255.0f)) << 24);
            }
        }

        private class ListAdapter extends SelectionAdapter {
            private Context context;
            private int currentCount;
            private ArrayList<ArrayList<ThemeDescription>> items = new ArrayList();
            private HashMap<String, ArrayList<ThemeDescription>> itemsMap = new HashMap();

            public int getItemViewType(int i) {
                return 0;
            }

            public boolean isEnabled(ViewHolder viewHolder) {
                return true;
            }

            public ListAdapter(Context context, ThemeDescription[] themeDescriptionArr) {
                this.context = context;
                for (ThemeDescription themeDescription : themeDescriptionArr) {
                    String currentKey = themeDescription.getCurrentKey();
                    ArrayList arrayList = (ArrayList) this.itemsMap.get(currentKey);
                    if (arrayList == null) {
                        arrayList = new ArrayList();
                        this.itemsMap.put(currentKey, arrayList);
                        this.items.add(arrayList);
                    }
                    arrayList.add(themeDescription);
                }
            }

            public int getItemCount() {
                return this.items.size();
            }

            public ArrayList<ThemeDescription> getItem(int i) {
                return (i < 0 || i >= this.items.size()) ? null : (ArrayList) this.items.get(i);
            }

            public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                TextColorThemeCell textColorThemeCell = new TextColorThemeCell(this.context);
                textColorThemeCell.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                return new Holder(textColorThemeCell);
            }

            public void onBindViewHolder(ViewHolder viewHolder, int i) {
                int i2 = 0;
                ThemeDescription themeDescription = (ThemeDescription) ((ArrayList) this.items.get(i)).get(0);
                if (!themeDescription.getCurrentKey().equals("chat_wallpaper")) {
                    i2 = themeDescription.getSetColor();
                }
                ((TextColorThemeCell) viewHolder.itemView).setTextAndColor(themeDescription.getTitle(), i2);
            }
        }

        /* Access modifiers changed, original: protected */
        public boolean canDismissWithSwipe() {
            return false;
        }

        public EditorAlert(ThemeEditorView themeEditorView, Context context, ThemeDescription[] themeDescriptionArr) {
            final ThemeEditorView themeEditorView2 = themeEditorView;
            Context context2 = context;
            this.this$0 = themeEditorView2;
            super(context2, true, 0);
            this.shadowDrawable = context.getResources().getDrawable(NUM).mutate();
            this.containerView = new FrameLayout(context2) {
                private boolean ignoreLayout = false;

                public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                    if (motionEvent.getAction() != 0 || EditorAlert.this.scrollOffsetY == 0 || motionEvent.getY() >= ((float) EditorAlert.this.scrollOffsetY)) {
                        return super.onInterceptTouchEvent(motionEvent);
                    }
                    EditorAlert.this.dismiss();
                    return true;
                }

                public boolean onTouchEvent(MotionEvent motionEvent) {
                    return !EditorAlert.this.isDismissed() && super.onTouchEvent(motionEvent);
                }

                /* Access modifiers changed, original: protected */
                public void onMeasure(int i, int i2) {
                    int size = MeasureSpec.getSize(i);
                    i2 = MeasureSpec.getSize(i2);
                    if (VERSION.SDK_INT >= 21) {
                        i2 -= AndroidUtilities.statusBarHeight;
                    }
                    size = i2 - Math.min(size, i2);
                    if (EditorAlert.this.listView.getPaddingTop() != size) {
                        this.ignoreLayout = true;
                        EditorAlert.this.listView.getPaddingTop();
                        EditorAlert.this.listView.setPadding(0, size, 0, AndroidUtilities.dp(48.0f));
                        if (EditorAlert.this.colorPicker.getVisibility() == 0) {
                            EditorAlert editorAlert = EditorAlert.this;
                            editorAlert.scrollOffsetY = editorAlert.listView.getPaddingTop();
                            EditorAlert.this.listView.setTopGlowOffset(EditorAlert.this.scrollOffsetY);
                            EditorAlert.this.colorPicker.setTranslationY((float) EditorAlert.this.scrollOffsetY);
                            EditorAlert.this.previousScrollPosition = 0;
                        }
                        this.ignoreLayout = false;
                    }
                    super.onMeasure(i, MeasureSpec.makeMeasureSpec(i2, NUM));
                }

                /* Access modifiers changed, original: protected */
                public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                    super.onLayout(z, i, i2, i3, i4);
                    EditorAlert.this.updateLayout();
                }

                public void requestLayout() {
                    if (!this.ignoreLayout) {
                        super.requestLayout();
                    }
                }

                /* Access modifiers changed, original: protected */
                public void onDraw(Canvas canvas) {
                    EditorAlert.this.shadowDrawable.setBounds(0, EditorAlert.this.scrollOffsetY - EditorAlert.this.backgroundPaddingTop, getMeasuredWidth(), getMeasuredHeight());
                    EditorAlert.this.shadowDrawable.draw(canvas);
                }
            };
            this.containerView.setWillNotDraw(false);
            ViewGroup viewGroup = this.containerView;
            int i = this.backgroundPaddingLeft;
            viewGroup.setPadding(i, 0, i, 0);
            this.listView = new RecyclerListView(context2);
            this.listView.setPadding(0, 0, 0, AndroidUtilities.dp(48.0f));
            this.listView.setClipToPadding(false);
            RecyclerListView recyclerListView = this.listView;
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            this.layoutManager = linearLayoutManager;
            recyclerListView.setLayoutManager(linearLayoutManager);
            this.listView.setHorizontalScrollBarEnabled(false);
            this.listView.setVerticalScrollBarEnabled(false);
            this.containerView.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
            recyclerListView = this.listView;
            ListAdapter listAdapter = new ListAdapter(context2, themeDescriptionArr);
            this.listAdapter = listAdapter;
            recyclerListView.setAdapter(listAdapter);
            this.listView.setGlowColor(-657673);
            this.listView.setItemAnimator(null);
            this.listView.setLayoutAnimation(null);
            this.listView.setOnItemClickListener(new -$$Lambda$ThemeEditorView$EditorAlert$kK2jW5xMCGw4an44hq65kfySCdU(this));
            this.listView.setOnScrollListener(new OnScrollListener() {
                public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                    EditorAlert.this.updateLayout();
                }
            });
            this.colorPicker = new ColorPicker(this, context2);
            this.colorPicker.setVisibility(8);
            this.containerView.addView(this.colorPicker, LayoutHelper.createFrame(-1, -1, 1));
            this.shadow = new View(context2);
            this.shadow.setBackgroundResource(NUM);
            this.containerView.addView(this.shadow, LayoutHelper.createFrame(-1, 3.0f, 83, 0.0f, 0.0f, 0.0f, 48.0f));
            this.bottomSaveLayout = new FrameLayout(context2);
            this.bottomSaveLayout.setBackgroundColor(-1);
            this.containerView.addView(this.bottomSaveLayout, LayoutHelper.createFrame(-1, 48, 83));
            TextView textView = new TextView(context2);
            textView.setTextSize(1, 14.0f);
            textView.setTextColor(-15095832);
            textView.setGravity(17);
            textView.setBackgroundDrawable(Theme.createSelectorDrawable(NUM, 0));
            textView.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
            textView.setText(LocaleController.getString("CloseEditor", NUM).toUpperCase());
            String str = "fonts/rmedium.ttf";
            textView.setTypeface(AndroidUtilities.getTypeface(str));
            this.bottomSaveLayout.addView(textView, LayoutHelper.createFrame(-2, -1, 51));
            textView.setOnClickListener(new -$$Lambda$ThemeEditorView$EditorAlert$kRxzT12O1gEcsTUiGkZYFSscUt8(this));
            textView = new TextView(context2);
            textView.setTextSize(1, 14.0f);
            textView.setTextColor(-15095832);
            textView.setGravity(17);
            textView.setBackgroundDrawable(Theme.createSelectorDrawable(NUM, 0));
            textView.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
            textView.setText(LocaleController.getString("SaveTheme", NUM).toUpperCase());
            textView.setTypeface(AndroidUtilities.getTypeface(str));
            this.bottomSaveLayout.addView(textView, LayoutHelper.createFrame(-2, -1, 53));
            textView.setOnClickListener(new -$$Lambda$ThemeEditorView$EditorAlert$-CO4an8qJdIEQQjX1P4ENdamV9E(this));
            this.bottomLayout = new FrameLayout(context2);
            this.bottomLayout.setVisibility(8);
            this.bottomLayout.setBackgroundColor(-1);
            this.containerView.addView(this.bottomLayout, LayoutHelper.createFrame(-1, 48, 83));
            this.cancelButton = new TextView(context2);
            this.cancelButton.setTextSize(1, 14.0f);
            this.cancelButton.setTextColor(-15095832);
            this.cancelButton.setGravity(17);
            this.cancelButton.setBackgroundDrawable(Theme.createSelectorDrawable(NUM, 0));
            this.cancelButton.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
            this.cancelButton.setText(LocaleController.getString("Cancel", NUM).toUpperCase());
            this.cancelButton.setTypeface(AndroidUtilities.getTypeface(str));
            this.bottomLayout.addView(this.cancelButton, LayoutHelper.createFrame(-2, -1, 51));
            this.cancelButton.setOnClickListener(new -$$Lambda$ThemeEditorView$EditorAlert$e8sB4SzqRAAe3BbXeRhAVLL0Fkg(this));
            LinearLayout linearLayout = new LinearLayout(context2);
            linearLayout.setOrientation(0);
            this.bottomLayout.addView(linearLayout, LayoutHelper.createFrame(-2, -1, 53));
            this.defaultButtom = new TextView(context2);
            this.defaultButtom.setTextSize(1, 14.0f);
            this.defaultButtom.setTextColor(-15095832);
            this.defaultButtom.setGravity(17);
            this.defaultButtom.setBackgroundDrawable(Theme.createSelectorDrawable(NUM, 0));
            this.defaultButtom.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
            this.defaultButtom.setText(LocaleController.getString("Default", NUM).toUpperCase());
            this.defaultButtom.setTypeface(AndroidUtilities.getTypeface(str));
            linearLayout.addView(this.defaultButtom, LayoutHelper.createFrame(-2, -1, 51));
            this.defaultButtom.setOnClickListener(new -$$Lambda$ThemeEditorView$EditorAlert$KOpMpGNwWrKZ5XW39TuNtWWXkWM(this));
            TextView textView2 = new TextView(context2);
            textView2.setTextSize(1, 14.0f);
            textView2.setTextColor(-15095832);
            textView2.setGravity(17);
            textView2.setBackgroundDrawable(Theme.createSelectorDrawable(NUM, 0));
            textView2.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
            textView2.setText(LocaleController.getString("Save", NUM).toUpperCase());
            textView2.setTypeface(AndroidUtilities.getTypeface(str));
            linearLayout.addView(textView2, LayoutHelper.createFrame(-2, -1, 51));
            textView2.setOnClickListener(new -$$Lambda$ThemeEditorView$EditorAlert$NlmVbEdgSNku-2tKRx9Agp6b-e4(this));
        }

        public /* synthetic */ void lambda$new$0$ThemeEditorView$EditorAlert(View view, int i) {
            this.this$0.currentThemeDesription = this.listAdapter.getItem(i);
            this.this$0.currentThemeDesriptionPosition = i;
            for (int i2 = 0; i2 < this.this$0.currentThemeDesription.size(); i2++) {
                ThemeDescription themeDescription = (ThemeDescription) this.this$0.currentThemeDesription.get(i2);
                if (themeDescription.getCurrentKey().equals("chat_wallpaper")) {
                    this.this$0.wallpaperUpdater.showAlert(true);
                    return;
                }
                themeDescription.startEditing();
                if (i2 == 0) {
                    this.colorPicker.setColor(themeDescription.getCurrentColor());
                }
            }
            setColorPickerVisible(true);
        }

        public /* synthetic */ void lambda$new$1$ThemeEditorView$EditorAlert(View view) {
            dismiss();
        }

        public /* synthetic */ void lambda$new$2$ThemeEditorView$EditorAlert(View view) {
            Theme.saveCurrentTheme(this.this$0.currentThemeName, true);
            setOnDismissListener(null);
            dismiss();
            this.this$0.close();
        }

        public /* synthetic */ void lambda$new$3$ThemeEditorView$EditorAlert(View view) {
            for (int i = 0; i < this.this$0.currentThemeDesription.size(); i++) {
                ((ThemeDescription) this.this$0.currentThemeDesription.get(i)).setPreviousColor();
            }
            setColorPickerVisible(false);
        }

        public /* synthetic */ void lambda$new$4$ThemeEditorView$EditorAlert(View view) {
            for (int i = 0; i < this.this$0.currentThemeDesription.size(); i++) {
                ((ThemeDescription) this.this$0.currentThemeDesription.get(i)).setDefaultColor();
            }
            setColorPickerVisible(false);
        }

        public /* synthetic */ void lambda$new$5$ThemeEditorView$EditorAlert(View view) {
            setColorPickerVisible(false);
        }

        private void setColorPickerVisible(boolean z) {
            String str = "scrollOffsetY";
            String str2 = "alpha";
            AnimatorSet animatorSet;
            Animator[] animatorArr;
            if (z) {
                this.animationInProgress = true;
                this.colorPicker.setVisibility(0);
                this.bottomLayout.setVisibility(0);
                this.colorPicker.setAlpha(0.0f);
                this.bottomLayout.setAlpha(0.0f);
                animatorSet = new AnimatorSet();
                animatorArr = new Animator[5];
                animatorArr[0] = ObjectAnimator.ofFloat(this.colorPicker, str2, new float[]{1.0f});
                animatorArr[1] = ObjectAnimator.ofFloat(this.bottomLayout, str2, new float[]{1.0f});
                animatorArr[2] = ObjectAnimator.ofFloat(this.listView, str2, new float[]{0.0f});
                animatorArr[3] = ObjectAnimator.ofFloat(this.bottomSaveLayout, str2, new float[]{0.0f});
                animatorArr[4] = ObjectAnimator.ofInt(this, str, new int[]{this.listView.getPaddingTop()});
                animatorSet.playTogether(animatorArr);
                animatorSet.setDuration(150);
                animatorSet.setInterpolator(this.this$0.decelerateInterpolator);
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        EditorAlert.this.listView.setVisibility(4);
                        EditorAlert.this.bottomSaveLayout.setVisibility(4);
                        EditorAlert.this.animationInProgress = false;
                    }
                });
                animatorSet.start();
                this.previousScrollPosition = this.scrollOffsetY;
                return;
            }
            if (this.this$0.parentActivity != null) {
                ((LaunchActivity) this.this$0.parentActivity).rebuildAllFragments(false);
            }
            Theme.saveCurrentTheme(this.this$0.currentThemeName, false);
            AndroidUtilities.hideKeyboard(getCurrentFocus());
            this.animationInProgress = true;
            this.listView.setVisibility(0);
            this.bottomSaveLayout.setVisibility(0);
            this.listView.setAlpha(0.0f);
            animatorSet = new AnimatorSet();
            animatorArr = new Animator[5];
            animatorArr[0] = ObjectAnimator.ofFloat(this.colorPicker, str2, new float[]{0.0f});
            animatorArr[1] = ObjectAnimator.ofFloat(this.bottomLayout, str2, new float[]{0.0f});
            animatorArr[2] = ObjectAnimator.ofFloat(this.listView, str2, new float[]{1.0f});
            animatorArr[3] = ObjectAnimator.ofFloat(this.bottomSaveLayout, str2, new float[]{1.0f});
            animatorArr[4] = ObjectAnimator.ofInt(this, str, new int[]{this.previousScrollPosition});
            animatorSet.playTogether(animatorArr);
            animatorSet.setDuration(150);
            animatorSet.setInterpolator(this.this$0.decelerateInterpolator);
            animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    EditorAlert.this.colorPicker.setVisibility(8);
                    EditorAlert.this.bottomLayout.setVisibility(8);
                    EditorAlert.this.animationInProgress = false;
                }
            });
            animatorSet.start();
            this.listAdapter.notifyItemChanged(this.this$0.currentThemeDesriptionPosition);
        }

        private int getCurrentTop() {
            if (this.listView.getChildCount() != 0) {
                int i = 0;
                View childAt = this.listView.getChildAt(0);
                Holder holder = (Holder) this.listView.findContainingViewHolder(childAt);
                if (holder != null) {
                    int paddingTop = this.listView.getPaddingTop();
                    if (holder.getAdapterPosition() == 0 && childAt.getTop() >= 0) {
                        i = childAt.getTop();
                    }
                    return paddingTop - i;
                }
            }
            return -1000;
        }

        @SuppressLint({"NewApi"})
        private void updateLayout() {
            if (this.listView.getChildCount() > 0 && this.listView.getVisibility() == 0 && !this.animationInProgress) {
                int paddingTop;
                View childAt = this.listView.getChildAt(0);
                Holder holder = (Holder) this.listView.findContainingViewHolder(childAt);
                if (this.listView.getVisibility() != 0 || this.animationInProgress) {
                    paddingTop = this.listView.getPaddingTop();
                } else {
                    paddingTop = childAt.getTop() - AndroidUtilities.dp(8.0f);
                }
                if (paddingTop <= 0 || holder == null || holder.getAdapterPosition() != 0) {
                    paddingTop = 0;
                }
                if (this.scrollOffsetY != paddingTop) {
                    setScrollOffsetY(paddingTop);
                }
            }
        }

        public int getScrollOffsetY() {
            return this.scrollOffsetY;
        }

        @Keep
        public void setScrollOffsetY(int i) {
            RecyclerListView recyclerListView = this.listView;
            this.scrollOffsetY = i;
            recyclerListView.setTopGlowOffset(i);
            this.colorPicker.setTranslationY((float) this.scrollOffsetY);
            this.containerView.invalidate();
        }
    }

    public static ThemeEditorView getInstance() {
        return Instance;
    }

    public void destroy() {
        this.wallpaperUpdater.cleanup();
        if (this.parentActivity != null) {
            FrameLayout frameLayout = this.windowView;
            if (frameLayout != null) {
                try {
                    this.windowManager.removeViewImmediate(frameLayout);
                    this.windowView = null;
                } catch (Exception e) {
                    FileLog.e(e);
                }
                try {
                    if (this.editorAlert != null) {
                        this.editorAlert.dismiss();
                        this.editorAlert = null;
                    }
                } catch (Exception e2) {
                    FileLog.e(e2);
                }
                this.parentActivity = null;
                Instance = null;
            }
        }
    }

    public void show(Activity activity, final String str) {
        if (Instance != null) {
            Instance.destroy();
        }
        this.hidden = false;
        this.currentThemeName = str;
        this.windowView = new FrameLayout(activity) {
            private boolean dragging;
            private float startX;
            private float startY;

            static /* synthetic */ void lambda$onTouchEvent$0(DialogInterface dialogInterface) {
            }

            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                return true;
            }

            /* JADX WARNING: Removed duplicated region for block: B:32:0x008d  */
            /* JADX WARNING: Removed duplicated region for block: B:34:0x0093  */
            /* JADX WARNING: Missing block: B:29:0x0088, code skipped:
            if (r6.fragmentsStack.isEmpty() != false) goto L_0x008a;
     */
            public boolean onTouchEvent(android.view.MotionEvent r10) {
                /*
                r9 = this;
                r0 = r10.getRawX();
                r1 = r10.getRawY();
                r2 = r10.getAction();
                r3 = 2;
                r4 = 0;
                r5 = 1;
                if (r2 != 0) goto L_0x0017;
            L_0x0011:
                r9.startX = r0;
                r9.startY = r1;
                goto L_0x00e6;
            L_0x0017:
                r2 = r10.getAction();
                if (r2 != r3) goto L_0x004a;
            L_0x001d:
                r2 = r9.dragging;
                if (r2 != 0) goto L_0x004a;
            L_0x0021:
                r2 = r9.startX;
                r2 = r2 - r0;
                r2 = java.lang.Math.abs(r2);
                r6 = NUM; // 0x3e99999a float:0.3 double:5.188942835E-315;
                r7 = org.telegram.messenger.AndroidUtilities.getPixelsInCM(r6, r5);
                r2 = (r2 > r7 ? 1 : (r2 == r7 ? 0 : -1));
                if (r2 >= 0) goto L_0x0042;
            L_0x0033:
                r2 = r9.startY;
                r2 = r2 - r1;
                r2 = java.lang.Math.abs(r2);
                r6 = org.telegram.messenger.AndroidUtilities.getPixelsInCM(r6, r4);
                r2 = (r2 > r6 ? 1 : (r2 == r6 ? 0 : -1));
                if (r2 < 0) goto L_0x00e6;
            L_0x0042:
                r9.dragging = r5;
                r9.startX = r0;
                r9.startY = r1;
                goto L_0x00e6;
            L_0x004a:
                r2 = r10.getAction();
                if (r2 != r5) goto L_0x00e6;
            L_0x0050:
                r2 = r9.dragging;
                if (r2 != 0) goto L_0x00e6;
            L_0x0054:
                r2 = org.telegram.ui.Components.ThemeEditorView.this;
                r2 = r2.editorAlert;
                if (r2 != 0) goto L_0x00e6;
            L_0x005c:
                r2 = org.telegram.ui.Components.ThemeEditorView.this;
                r2 = r2.parentActivity;
                r2 = (org.telegram.ui.LaunchActivity) r2;
                r6 = org.telegram.messenger.AndroidUtilities.isTablet();
                r7 = 0;
                if (r6 == 0) goto L_0x008a;
            L_0x006b:
                r6 = r2.getLayersActionBarLayout();
                if (r6 == 0) goto L_0x007a;
            L_0x0071:
                r8 = r6.fragmentsStack;
                r8 = r8.isEmpty();
                if (r8 == 0) goto L_0x007a;
            L_0x0079:
                r6 = r7;
            L_0x007a:
                if (r6 != 0) goto L_0x008b;
            L_0x007c:
                r6 = r2.getRightActionBarLayout();
                if (r6 == 0) goto L_0x008b;
            L_0x0082:
                r8 = r6.fragmentsStack;
                r8 = r8.isEmpty();
                if (r8 == 0) goto L_0x008b;
            L_0x008a:
                r6 = r7;
            L_0x008b:
                if (r6 != 0) goto L_0x0091;
            L_0x008d:
                r6 = r2.getActionBarLayout();
            L_0x0091:
                if (r6 == 0) goto L_0x00e6;
            L_0x0093:
                r2 = r6.fragmentsStack;
                r2 = r2.isEmpty();
                if (r2 != 0) goto L_0x00a9;
            L_0x009b:
                r2 = r6.fragmentsStack;
                r6 = r2.size();
                r6 = r6 - r5;
                r2 = r2.get(r6);
                r7 = r2;
                r7 = (org.telegram.ui.ActionBar.BaseFragment) r7;
            L_0x00a9:
                if (r7 == 0) goto L_0x00e6;
            L_0x00ab:
                r2 = r7.getThemeDescriptions();
                if (r2 == 0) goto L_0x00e6;
            L_0x00b1:
                r6 = org.telegram.ui.Components.ThemeEditorView.this;
                r7 = new org.telegram.ui.Components.ThemeEditorView$EditorAlert;
                r8 = r6.parentActivity;
                r7.<init>(r6, r8, r2);
                r6.editorAlert = r7;
                r2 = org.telegram.ui.Components.ThemeEditorView.this;
                r2 = r2.editorAlert;
                r6 = org.telegram.ui.Components.-$$Lambda$ThemeEditorView$1$wsYCYqNbqFfDt4B1cHgNjwuFWAI.INSTANCE;
                r2.setOnDismissListener(r6);
                r2 = org.telegram.ui.Components.ThemeEditorView.this;
                r2 = r2.editorAlert;
                r6 = new org.telegram.ui.Components.-$$Lambda$ThemeEditorView$1$E9dxEm5ftbqgpiMvar_EOuTy-RY;
                r6.<init>(r9);
                r2.setOnDismissListener(r6);
                r2 = org.telegram.ui.Components.ThemeEditorView.this;
                r2 = r2.editorAlert;
                r2.show();
                r2 = org.telegram.ui.Components.ThemeEditorView.this;
                r2.hide();
            L_0x00e6:
                r2 = r9.dragging;
                if (r2 == 0) goto L_0x022a;
            L_0x00ea:
                r2 = r10.getAction();
                if (r2 != r3) goto L_0x021d;
            L_0x00f0:
                r10 = r9.startX;
                r10 = r0 - r10;
                r2 = r9.startY;
                r2 = r1 - r2;
                r6 = org.telegram.ui.Components.ThemeEditorView.this;
                r6 = r6.windowLayoutParams;
                r7 = r6.x;
                r7 = (float) r7;
                r7 = r7 + r10;
                r10 = (int) r7;
                r6.x = r10;
                r10 = org.telegram.ui.Components.ThemeEditorView.this;
                r10 = r10.windowLayoutParams;
                r6 = r10.y;
                r6 = (float) r6;
                r6 = r6 + r2;
                r2 = (int) r6;
                r10.y = r2;
                r10 = org.telegram.ui.Components.ThemeEditorView.this;
                r10 = r10.editorWidth;
                r10 = r10 / r3;
                r2 = org.telegram.ui.Components.ThemeEditorView.this;
                r2 = r2.windowLayoutParams;
                r2 = r2.x;
                r3 = -r10;
                if (r2 >= r3) goto L_0x012d;
            L_0x0124:
                r2 = org.telegram.ui.Components.ThemeEditorView.this;
                r2 = r2.windowLayoutParams;
                r2.x = r3;
                goto L_0x015b;
            L_0x012d:
                r2 = org.telegram.ui.Components.ThemeEditorView.this;
                r2 = r2.windowLayoutParams;
                r2 = r2.x;
                r3 = org.telegram.messenger.AndroidUtilities.displaySize;
                r3 = r3.x;
                r6 = org.telegram.ui.Components.ThemeEditorView.this;
                r6 = r6.windowLayoutParams;
                r6 = r6.width;
                r3 = r3 - r6;
                r3 = r3 + r10;
                if (r2 <= r3) goto L_0x015b;
            L_0x0145:
                r2 = org.telegram.ui.Components.ThemeEditorView.this;
                r2 = r2.windowLayoutParams;
                r3 = org.telegram.messenger.AndroidUtilities.displaySize;
                r3 = r3.x;
                r6 = org.telegram.ui.Components.ThemeEditorView.this;
                r6 = r6.windowLayoutParams;
                r6 = r6.width;
                r3 = r3 - r6;
                r3 = r3 + r10;
                r2.x = r3;
            L_0x015b:
                r2 = org.telegram.ui.Components.ThemeEditorView.this;
                r2 = r2.windowLayoutParams;
                r2 = r2.x;
                r3 = NUM; // 0x3var_ float:0.5 double:5.222099017E-315;
                r6 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
                if (r2 >= 0) goto L_0x0178;
            L_0x0169:
                r2 = org.telegram.ui.Components.ThemeEditorView.this;
                r2 = r2.windowLayoutParams;
                r2 = r2.x;
                r2 = (float) r2;
                r10 = (float) r10;
                r2 = r2 / r10;
                r2 = r2 * r3;
                r6 = r6 + r2;
                goto L_0x01ab;
            L_0x0178:
                r2 = org.telegram.ui.Components.ThemeEditorView.this;
                r2 = r2.windowLayoutParams;
                r2 = r2.x;
                r7 = org.telegram.messenger.AndroidUtilities.displaySize;
                r7 = r7.x;
                r8 = org.telegram.ui.Components.ThemeEditorView.this;
                r8 = r8.windowLayoutParams;
                r8 = r8.width;
                r7 = r7 - r8;
                if (r2 <= r7) goto L_0x01ab;
            L_0x018f:
                r2 = org.telegram.ui.Components.ThemeEditorView.this;
                r2 = r2.windowLayoutParams;
                r2 = r2.x;
                r7 = org.telegram.messenger.AndroidUtilities.displaySize;
                r7 = r7.x;
                r2 = r2 - r7;
                r7 = org.telegram.ui.Components.ThemeEditorView.this;
                r7 = r7.windowLayoutParams;
                r7 = r7.width;
                r2 = r2 + r7;
                r2 = (float) r2;
                r10 = (float) r10;
                r2 = r2 / r10;
                r2 = r2 * r3;
                r6 = r6 - r2;
            L_0x01ab:
                r10 = org.telegram.ui.Components.ThemeEditorView.this;
                r10 = r10.windowView;
                r10 = r10.getAlpha();
                r10 = (r10 > r6 ? 1 : (r10 == r6 ? 0 : -1));
                if (r10 == 0) goto L_0x01c2;
            L_0x01b9:
                r10 = org.telegram.ui.Components.ThemeEditorView.this;
                r10 = r10.windowView;
                r10.setAlpha(r6);
            L_0x01c2:
                r10 = org.telegram.ui.Components.ThemeEditorView.this;
                r10 = r10.windowLayoutParams;
                r10 = r10.y;
                if (r10 >= 0) goto L_0x01d5;
            L_0x01cc:
                r10 = org.telegram.ui.Components.ThemeEditorView.this;
                r10 = r10.windowLayoutParams;
                r10.y = r4;
                goto L_0x0203;
            L_0x01d5:
                r10 = org.telegram.ui.Components.ThemeEditorView.this;
                r10 = r10.windowLayoutParams;
                r10 = r10.y;
                r2 = org.telegram.messenger.AndroidUtilities.displaySize;
                r2 = r2.y;
                r3 = org.telegram.ui.Components.ThemeEditorView.this;
                r3 = r3.windowLayoutParams;
                r3 = r3.height;
                r2 = r2 - r3;
                r2 = r2 + r4;
                if (r10 <= r2) goto L_0x0203;
            L_0x01ed:
                r10 = org.telegram.ui.Components.ThemeEditorView.this;
                r10 = r10.windowLayoutParams;
                r2 = org.telegram.messenger.AndroidUtilities.displaySize;
                r2 = r2.y;
                r3 = org.telegram.ui.Components.ThemeEditorView.this;
                r3 = r3.windowLayoutParams;
                r3 = r3.height;
                r2 = r2 - r3;
                r2 = r2 + r4;
                r10.y = r2;
            L_0x0203:
                r10 = org.telegram.ui.Components.ThemeEditorView.this;
                r10 = r10.windowManager;
                r2 = org.telegram.ui.Components.ThemeEditorView.this;
                r2 = r2.windowView;
                r3 = org.telegram.ui.Components.ThemeEditorView.this;
                r3 = r3.windowLayoutParams;
                r10.updateViewLayout(r2, r3);
                r9.startX = r0;
                r9.startY = r1;
                goto L_0x022a;
            L_0x021d:
                r10 = r10.getAction();
                if (r10 != r5) goto L_0x022a;
            L_0x0223:
                r9.dragging = r4;
                r10 = org.telegram.ui.Components.ThemeEditorView.this;
                r10.animateToBoundsMaybe();
            L_0x022a:
                return r5;
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ThemeEditorView$AnonymousClass1.onTouchEvent(android.view.MotionEvent):boolean");
            }

            public /* synthetic */ void lambda$onTouchEvent$1$ThemeEditorView$1(DialogInterface dialogInterface) {
                ThemeEditorView.this.editorAlert = null;
                ThemeEditorView.this.show();
            }
        };
        this.windowView.setBackgroundResource(NUM);
        this.windowManager = (WindowManager) activity.getSystemService("window");
        this.preferences = ApplicationLoader.applicationContext.getSharedPreferences("themeconfig", 0);
        int i = this.preferences.getInt("sidex", 1);
        int i2 = this.preferences.getInt("sidey", 0);
        float f = this.preferences.getFloat("px", 0.0f);
        float f2 = this.preferences.getFloat("py", 0.0f);
        try {
            this.windowLayoutParams = new LayoutParams();
            this.windowLayoutParams.width = this.editorWidth;
            this.windowLayoutParams.height = this.editorHeight;
            this.windowLayoutParams.x = getSideCoord(true, i, f, this.editorWidth);
            this.windowLayoutParams.y = getSideCoord(false, i2, f2, this.editorHeight);
            this.windowLayoutParams.format = -3;
            this.windowLayoutParams.gravity = 51;
            this.windowLayoutParams.type = 99;
            this.windowLayoutParams.flags = 16777736;
            this.windowManager.addView(this.windowView, this.windowLayoutParams);
            this.wallpaperUpdater = new WallpaperUpdater(activity, null, new WallpaperUpdaterDelegate() {
                public void didSelectWallpaper(File file, Bitmap bitmap, boolean z) {
                    Theme.setThemeWallpaper(str, bitmap, file);
                }

                public void needOpenColorPicker() {
                    for (int i = 0; i < ThemeEditorView.this.currentThemeDesription.size(); i++) {
                        ThemeDescription themeDescription = (ThemeDescription) ThemeEditorView.this.currentThemeDesription.get(i);
                        themeDescription.startEditing();
                        if (i == 0) {
                            ThemeEditorView.this.editorAlert.colorPicker.setColor(themeDescription.getCurrentColor());
                        }
                    }
                    ThemeEditorView.this.editorAlert.setColorPickerVisible(true);
                }
            });
            Instance = this;
            this.parentActivity = activity;
            showWithAnimation();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private void showWithAnimation() {
        AnimatorSet animatorSet = new AnimatorSet();
        Animator[] animatorArr = new Animator[3];
        animatorArr[0] = ObjectAnimator.ofFloat(this.windowView, "alpha", new float[]{0.0f, 1.0f});
        animatorArr[1] = ObjectAnimator.ofFloat(this.windowView, "scaleX", new float[]{0.0f, 1.0f});
        animatorArr[2] = ObjectAnimator.ofFloat(this.windowView, "scaleY", new float[]{0.0f, 1.0f});
        animatorSet.playTogether(animatorArr);
        animatorSet.setInterpolator(this.decelerateInterpolator);
        animatorSet.setDuration(150);
        animatorSet.start();
    }

    private static int getSideCoord(boolean z, int i, float f, int i2) {
        int i3;
        if (z) {
            i3 = AndroidUtilities.displaySize.x;
        } else {
            i3 = AndroidUtilities.displaySize.y - i2;
            i2 = ActionBar.getCurrentActionBarHeight();
        }
        i3 -= i2;
        if (i == 0) {
            i = AndroidUtilities.dp(10.0f);
        } else if (i == 1) {
            i = i3 - AndroidUtilities.dp(10.0f);
        } else {
            i = Math.round(((float) (i3 - AndroidUtilities.dp(20.0f))) * f) + AndroidUtilities.dp(10.0f);
        }
        return !z ? i + ActionBar.getCurrentActionBarHeight() : i;
    }

    private void hide() {
        if (this.parentActivity != null) {
            try {
                AnimatorSet animatorSet = new AnimatorSet();
                r1 = new Animator[3];
                r1[0] = ObjectAnimator.ofFloat(this.windowView, "alpha", new float[]{1.0f, 0.0f});
                r1[1] = ObjectAnimator.ofFloat(this.windowView, "scaleX", new float[]{1.0f, 0.0f});
                r1[2] = ObjectAnimator.ofFloat(this.windowView, "scaleY", new float[]{1.0f, 0.0f});
                animatorSet.playTogether(r1);
                animatorSet.setInterpolator(this.decelerateInterpolator);
                animatorSet.setDuration(150);
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        if (ThemeEditorView.this.windowView != null) {
                            ThemeEditorView.this.windowManager.removeView(ThemeEditorView.this.windowView);
                        }
                    }
                });
                animatorSet.start();
                this.hidden = true;
            } catch (Exception unused) {
            }
        }
    }

    private void show() {
        if (this.parentActivity != null) {
            try {
                this.windowManager.addView(this.windowView, this.windowLayoutParams);
                this.hidden = false;
                showWithAnimation();
            } catch (Exception unused) {
            }
        }
    }

    public void close() {
        try {
            this.windowManager.removeView(this.windowView);
        } catch (Exception unused) {
        }
        this.parentActivity = null;
    }

    public void onConfigurationChanged() {
        int i = this.preferences.getInt("sidex", 1);
        int i2 = this.preferences.getInt("sidey", 0);
        float f = this.preferences.getFloat("px", 0.0f);
        float f2 = this.preferences.getFloat("py", 0.0f);
        this.windowLayoutParams.x = getSideCoord(true, i, f, this.editorWidth);
        this.windowLayoutParams.y = getSideCoord(false, i2, f2, this.editorHeight);
        try {
            if (this.windowView.getParent() != null) {
                this.windowManager.updateViewLayout(this.windowView, this.windowLayoutParams);
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        WallpaperUpdater wallpaperUpdater = this.wallpaperUpdater;
        if (wallpaperUpdater != null) {
            wallpaperUpdater.onActivityResult(i, i2, intent);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:31:0x0117  */
    /* JADX WARNING: Removed duplicated region for block: B:54:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x0184  */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x0117  */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x0184  */
    /* JADX WARNING: Removed duplicated region for block: B:54:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x0117  */
    /* JADX WARNING: Removed duplicated region for block: B:54:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x0184  */
    private void animateToBoundsMaybe() {
        /*
        r16 = this;
        r0 = r16;
        r1 = r0.editorWidth;
        r2 = 0;
        r3 = 0;
        r4 = 1;
        r1 = getSideCoord(r4, r3, r2, r1);
        r5 = r0.editorWidth;
        r5 = getSideCoord(r4, r4, r2, r5);
        r6 = r0.editorHeight;
        r6 = getSideCoord(r3, r3, r2, r6);
        r7 = r0.editorHeight;
        r7 = getSideCoord(r3, r4, r2, r7);
        r8 = r0.preferences;
        r8 = r8.edit();
        r9 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r10 = r0.windowLayoutParams;
        r10 = r10.x;
        r10 = r1 - r10;
        r10 = java.lang.Math.abs(r10);
        r12 = "alpha";
        r13 = "sidex";
        r14 = "x";
        r15 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        if (r10 <= r9) goto L_0x00e8;
    L_0x003e:
        r10 = r0.windowLayoutParams;
        r10 = r10.x;
        if (r10 >= 0) goto L_0x004d;
    L_0x0044:
        r2 = r0.editorWidth;
        r2 = -r2;
        r2 = r2 / 4;
        if (r10 <= r2) goto L_0x004d;
    L_0x004b:
        goto L_0x00e8;
    L_0x004d:
        r2 = r0.windowLayoutParams;
        r2 = r2.x;
        r2 = r5 - r2;
        r2 = java.lang.Math.abs(r2);
        if (r2 <= r9) goto L_0x00ba;
    L_0x0059:
        r2 = r0.windowLayoutParams;
        r2 = r2.x;
        r10 = org.telegram.messenger.AndroidUtilities.displaySize;
        r10 = r10.x;
        r11 = r0.editorWidth;
        r3 = r10 - r11;
        if (r2 <= r3) goto L_0x006f;
    L_0x0067:
        r11 = r11 / 4;
        r11 = r11 * 3;
        r10 = r10 - r11;
        if (r2 >= r10) goto L_0x006f;
    L_0x006e:
        goto L_0x00ba;
    L_0x006f:
        r2 = r0.windowView;
        r2 = r2.getAlpha();
        r2 = (r2 > r15 ? 1 : (r2 == r15 ? 0 : -1));
        if (r2 == 0) goto L_0x00a6;
    L_0x0079:
        r1 = new java.util.ArrayList;
        r1.<init>();
        r2 = r0.windowLayoutParams;
        r2 = r2.x;
        if (r2 >= 0) goto L_0x0094;
    L_0x0084:
        r2 = new int[r4];
        r3 = r0.editorWidth;
        r3 = -r3;
        r5 = 0;
        r2[r5] = r3;
        r2 = android.animation.ObjectAnimator.ofInt(r0, r14, r2);
        r1.add(r2);
        goto L_0x00a4;
    L_0x0094:
        r5 = 0;
        r2 = new int[r4];
        r3 = org.telegram.messenger.AndroidUtilities.displaySize;
        r3 = r3.x;
        r2[r5] = r3;
        r2 = android.animation.ObjectAnimator.ofInt(r0, r14, r2);
        r1.add(r2);
    L_0x00a4:
        r10 = 1;
        goto L_0x0115;
    L_0x00a6:
        r2 = r0.windowLayoutParams;
        r2 = r2.x;
        r2 = r2 - r1;
        r2 = (float) r2;
        r5 = r5 - r1;
        r1 = (float) r5;
        r2 = r2 / r1;
        r1 = "px";
        r8.putFloat(r1, r2);
        r1 = 2;
        r8.putInt(r13, r1);
        r1 = 0;
        goto L_0x0114;
    L_0x00ba:
        r1 = new java.util.ArrayList;
        r1.<init>();
        r8.putInt(r13, r4);
        r2 = r0.windowView;
        r2 = r2.getAlpha();
        r2 = (r2 > r15 ? 1 : (r2 == r15 ? 0 : -1));
        if (r2 == 0) goto L_0x00db;
    L_0x00cc:
        r2 = r0.windowView;
        r3 = new float[r4];
        r10 = 0;
        r3[r10] = r15;
        r2 = android.animation.ObjectAnimator.ofFloat(r2, r12, r3);
        r1.add(r2);
        goto L_0x00dc;
    L_0x00db:
        r10 = 0;
    L_0x00dc:
        r2 = new int[r4];
        r2[r10] = r5;
        r2 = android.animation.ObjectAnimator.ofInt(r0, r14, r2);
        r1.add(r2);
        goto L_0x0115;
    L_0x00e8:
        r10 = 0;
        r2 = new java.util.ArrayList;
        r2.<init>();
        r8.putInt(r13, r10);
        r3 = r0.windowView;
        r3 = r3.getAlpha();
        r3 = (r3 > r15 ? 1 : (r3 == r15 ? 0 : -1));
        if (r3 == 0) goto L_0x0108;
    L_0x00fb:
        r3 = r0.windowView;
        r5 = new float[r4];
        r5[r10] = r15;
        r3 = android.animation.ObjectAnimator.ofFloat(r3, r12, r5);
        r2.add(r3);
    L_0x0108:
        r3 = new int[r4];
        r3[r10] = r1;
        r1 = android.animation.ObjectAnimator.ofInt(r0, r14, r3);
        r2.add(r1);
        r1 = r2;
    L_0x0114:
        r10 = 0;
    L_0x0115:
        if (r10 != 0) goto L_0x0182;
    L_0x0117:
        r2 = r0.windowLayoutParams;
        r2 = r2.y;
        r2 = r6 - r2;
        r2 = java.lang.Math.abs(r2);
        r3 = "y";
        r5 = "sidey";
        if (r2 <= r9) goto L_0x0169;
    L_0x0128:
        r2 = r0.windowLayoutParams;
        r2 = r2.y;
        r11 = org.telegram.ui.ActionBar.ActionBar.getCurrentActionBarHeight();
        if (r2 > r11) goto L_0x0133;
    L_0x0132:
        goto L_0x0169;
    L_0x0133:
        r2 = r0.windowLayoutParams;
        r2 = r2.y;
        r2 = r7 - r2;
        r2 = java.lang.Math.abs(r2);
        if (r2 > r9) goto L_0x0156;
    L_0x013f:
        if (r1 != 0) goto L_0x0146;
    L_0x0141:
        r1 = new java.util.ArrayList;
        r1.<init>();
    L_0x0146:
        r8.putInt(r5, r4);
        r2 = new int[r4];
        r5 = 0;
        r2[r5] = r7;
        r2 = android.animation.ObjectAnimator.ofInt(r0, r3, r2);
        r1.add(r2);
        goto L_0x017f;
    L_0x0156:
        r2 = r0.windowLayoutParams;
        r2 = r2.y;
        r2 = r2 - r6;
        r2 = (float) r2;
        r7 = r7 - r6;
        r3 = (float) r7;
        r2 = r2 / r3;
        r3 = "py";
        r8.putFloat(r3, r2);
        r2 = 2;
        r8.putInt(r5, r2);
        goto L_0x017f;
    L_0x0169:
        if (r1 != 0) goto L_0x0170;
    L_0x016b:
        r1 = new java.util.ArrayList;
        r1.<init>();
    L_0x0170:
        r2 = 0;
        r8.putInt(r5, r2);
        r5 = new int[r4];
        r5[r2] = r6;
        r2 = android.animation.ObjectAnimator.ofInt(r0, r3, r5);
        r1.add(r2);
    L_0x017f:
        r8.commit();
    L_0x0182:
        if (r1 == 0) goto L_0x01bd;
    L_0x0184:
        r2 = r0.decelerateInterpolator;
        if (r2 != 0) goto L_0x018f;
    L_0x0188:
        r2 = new android.view.animation.DecelerateInterpolator;
        r2.<init>();
        r0.decelerateInterpolator = r2;
    L_0x018f:
        r2 = new android.animation.AnimatorSet;
        r2.<init>();
        r3 = r0.decelerateInterpolator;
        r2.setInterpolator(r3);
        r5 = 150; // 0x96 float:2.1E-43 double:7.4E-322;
        r2.setDuration(r5);
        if (r10 == 0) goto L_0x01b7;
    L_0x01a0:
        r3 = r0.windowView;
        r4 = new float[r4];
        r5 = 0;
        r6 = 0;
        r4[r6] = r5;
        r3 = android.animation.ObjectAnimator.ofFloat(r3, r12, r4);
        r1.add(r3);
        r3 = new org.telegram.ui.Components.ThemeEditorView$4;
        r3.<init>();
        r2.addListener(r3);
    L_0x01b7:
        r2.playTogether(r1);
        r2.start();
    L_0x01bd:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ThemeEditorView.animateToBoundsMaybe():void");
    }

    public int getX() {
        return this.windowLayoutParams.x;
    }

    public int getY() {
        return this.windowLayoutParams.y;
    }

    @Keep
    public void setX(int i) {
        LayoutParams layoutParams = this.windowLayoutParams;
        layoutParams.x = i;
        this.windowManager.updateViewLayout(this.windowView, layoutParams);
    }

    @Keep
    public void setY(int i) {
        LayoutParams layoutParams = this.windowLayoutParams;
        layoutParams.y = i;
        this.windowManager.updateViewLayout(this.windowView, layoutParams);
    }
}
