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
import android.graphics.PorterDuffColorFilter;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.SweepGradient;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Property;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Keep;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.OnScrollListener;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.Theme.ThemeInfo;
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
    private DecelerateInterpolator decelerateInterpolator;
    private EditorAlert editorAlert;
    private final int editorHeight = AndroidUtilities.dp(54.0f);
    private final int editorWidth = AndroidUtilities.dp(54.0f);
    private boolean hidden;
    private Activity parentActivity;
    private SharedPreferences preferences;
    private ThemeInfo themeInfo;
    private WallpaperUpdater wallpaperUpdater;
    private LayoutParams windowLayoutParams;
    private WindowManager windowManager;
    private FrameLayout windowView;

    public class EditorAlert extends BottomSheet {
        private boolean animationInProgress;
        private FrameLayout bottomLayout;
        private FrameLayout bottomSaveLayout;
        private AnimatorSet colorChangeAnimation;
        private ColorPicker colorPicker;
        private FrameLayout frameLayout;
        private boolean ignoreTextChange;
        private LinearLayoutManager layoutManager;
        private ListAdapter listAdapter;
        private RecyclerListView listView;
        private int previousScrollPosition;
        private TextView saveButton;
        private int scrollOffsetY;
        private SearchAdapter searchAdapter;
        private EmptyTextProgressView searchEmptyView;
        private SearchField searchField;
        private View[] shadow = new View[2];
        private AnimatorSet[] shadowAnimation = new AnimatorSet[2];
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
                    AnimatorSet access$1300 = this.this$1.colorChangeAnimation;
                    Animator[] animatorArr = new Animator[2];
                    ColorDrawable access$1400 = this.this$1.backDrawable;
                    Property property = AnimationProperties.COLOR_DRAWABLE_ALPHA;
                    int[] iArr = new int[1];
                    iArr[0] = z ? 0 : 51;
                    animatorArr[0] = ObjectAnimator.ofInt(access$1400, property, iArr);
                    ViewGroup access$1500 = this.this$1.containerView;
                    property = View.ALPHA;
                    float[] fArr = new float[1];
                    fArr[0] = z ? 0.2f : 1.0f;
                    animatorArr[1] = ObjectAnimator.ofFloat(access$1500, property, fArr);
                    access$1300.playTogether(animatorArr);
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

        private class SearchField extends FrameLayout {
            private View backgroundView;
            private ImageView clearSearchImageView;
            private EditTextBoldCursor searchEditText;

            public SearchField(Context context) {
                super(context);
                View view = new View(context);
                view.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(18.0f), -854795));
                addView(view, LayoutHelper.createFrame(-1, 36.0f, 51, 14.0f, 11.0f, 14.0f, 0.0f));
                ImageView imageView = new ImageView(context);
                imageView.setScaleType(ScaleType.CENTER);
                imageView.setImageResource(NUM);
                imageView.setColorFilter(new PorterDuffColorFilter(-6182737, Mode.MULTIPLY));
                addView(imageView, LayoutHelper.createFrame(36, 36.0f, 51, 16.0f, 11.0f, 0.0f, 0.0f));
                this.clearSearchImageView = new ImageView(context);
                this.clearSearchImageView.setScaleType(ScaleType.CENTER);
                imageView = this.clearSearchImageView;
                CloseProgressDrawable2 closeProgressDrawable2 = new CloseProgressDrawable2();
                imageView.setImageDrawable(closeProgressDrawable2);
                closeProgressDrawable2.setSide(AndroidUtilities.dp(7.0f));
                this.clearSearchImageView.setScaleX(0.1f);
                this.clearSearchImageView.setScaleY(0.1f);
                this.clearSearchImageView.setAlpha(0.0f);
                this.clearSearchImageView.setColorFilter(new PorterDuffColorFilter(-6182737, Mode.MULTIPLY));
                addView(this.clearSearchImageView, LayoutHelper.createFrame(36, 36.0f, 53, 14.0f, 11.0f, 14.0f, 0.0f));
                this.clearSearchImageView.setOnClickListener(new -$$Lambda$ThemeEditorView$EditorAlert$SearchField$oyMCfmJu6kX9C4_2783iB9VXPBE(this));
                this.searchEditText = new EditTextBoldCursor(context, EditorAlert.this) {
                    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
                        MotionEvent obtain = MotionEvent.obtain(motionEvent);
                        obtain.setLocation(obtain.getRawX(), obtain.getRawY() - EditorAlert.this.containerView.getTranslationY());
                        EditorAlert.this.listView.dispatchTouchEvent(obtain);
                        obtain.recycle();
                        return super.dispatchTouchEvent(motionEvent);
                    }
                };
                this.searchEditText.setTextSize(1, 16.0f);
                this.searchEditText.setHintTextColor(-6774617);
                this.searchEditText.setTextColor(-14540254);
                this.searchEditText.setBackgroundDrawable(null);
                this.searchEditText.setPadding(0, 0, 0, 0);
                this.searchEditText.setMaxLines(1);
                this.searchEditText.setLines(1);
                this.searchEditText.setSingleLine(true);
                this.searchEditText.setImeOptions(NUM);
                this.searchEditText.setHint(LocaleController.getString("Search", NUM));
                this.searchEditText.setCursorColor(-11491093);
                this.searchEditText.setCursorSize(AndroidUtilities.dp(20.0f));
                this.searchEditText.setCursorWidth(1.5f);
                addView(this.searchEditText, LayoutHelper.createFrame(-1, 40.0f, 51, 54.0f, 9.0f, 46.0f, 0.0f));
                this.searchEditText.addTextChangedListener(new TextWatcher(EditorAlert.this) {
                    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    }

                    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    }

                    public void afterTextChanged(Editable editable) {
                        Object obj = 1;
                        Object obj2 = SearchField.this.searchEditText.length() > 0 ? 1 : null;
                        float f = 0.0f;
                        if (SearchField.this.clearSearchImageView.getAlpha() == 0.0f) {
                            obj = null;
                        }
                        if (obj2 != obj) {
                            ViewPropertyAnimator animate = SearchField.this.clearSearchImageView.animate();
                            float f2 = 1.0f;
                            if (obj2 != null) {
                                f = 1.0f;
                            }
                            animate = animate.alpha(f).setDuration(150).scaleX(obj2 != null ? 1.0f : 0.1f);
                            if (obj2 == null) {
                                f2 = 0.1f;
                            }
                            animate.scaleY(f2).start();
                        }
                        String obj3 = SearchField.this.searchEditText.getText().toString();
                        if (obj3.length() != 0) {
                            if (EditorAlert.this.searchEmptyView != null) {
                                EditorAlert.this.searchEmptyView.setText(LocaleController.getString("NoResult", NUM));
                            }
                        } else if (EditorAlert.this.listView.getAdapter() != EditorAlert.this.listAdapter) {
                            int access$600 = EditorAlert.this.getCurrentTop();
                            EditorAlert.this.searchEmptyView.setText(LocaleController.getString("NoChats", NUM));
                            EditorAlert.this.searchEmptyView.showTextView();
                            EditorAlert.this.listView.setAdapter(EditorAlert.this.listAdapter);
                            EditorAlert.this.listAdapter.notifyDataSetChanged();
                            if (access$600 > 0) {
                                EditorAlert.this.layoutManager.scrollToPositionWithOffset(0, -access$600);
                            }
                        }
                        if (EditorAlert.this.searchAdapter != null) {
                            EditorAlert.this.searchAdapter.searchDialogs(obj3);
                        }
                    }
                });
                this.searchEditText.setOnEditorActionListener(new -$$Lambda$ThemeEditorView$EditorAlert$SearchField$7PNsSR7Iu-AOTIs-B2ibPMDGudM(this));
            }

            public /* synthetic */ void lambda$new$0$ThemeEditorView$EditorAlert$SearchField(View view) {
                this.searchEditText.setText("");
                AndroidUtilities.showKeyboard(this.searchEditText);
            }

            public /* synthetic */ boolean lambda$new$1$ThemeEditorView$EditorAlert$SearchField(TextView textView, int i, KeyEvent keyEvent) {
                if (keyEvent != null && ((keyEvent.getAction() == 1 && keyEvent.getKeyCode() == 84) || (keyEvent.getAction() == 0 && keyEvent.getKeyCode() == 66))) {
                    AndroidUtilities.hideKeyboard(this.searchEditText);
                }
                return false;
            }

            public void hideKeyboard() {
                AndroidUtilities.hideKeyboard(this.searchEditText);
            }

            public void showKeyboard() {
                this.searchEditText.requestFocus();
                AndroidUtilities.showKeyboard(this.searchEditText);
            }

            public void requestDisallowInterceptTouchEvent(boolean z) {
                super.requestDisallowInterceptTouchEvent(z);
            }
        }

        private class ListAdapter extends SelectionAdapter {
            private Context context;
            private int currentCount;
            private ArrayList<ArrayList<ThemeDescription>> items = new ArrayList();

            public int getItemViewType(int i) {
                return i == 0 ? 1 : 0;
            }

            public boolean isEnabled(ViewHolder viewHolder) {
                return true;
            }

            public ListAdapter(Context context, ThemeDescription[] themeDescriptionArr) {
                this.context = context;
                HashMap hashMap = new HashMap();
                for (ThemeDescription themeDescription : themeDescriptionArr) {
                    String currentKey = themeDescription.getCurrentKey();
                    ArrayList arrayList = (ArrayList) hashMap.get(currentKey);
                    if (arrayList == null) {
                        arrayList = new ArrayList();
                        hashMap.put(currentKey, arrayList);
                        this.items.add(arrayList);
                    }
                    arrayList.add(themeDescription);
                }
                if (VERSION.SDK_INT >= 26 && !hashMap.containsKey("windowBackgroundGray")) {
                    ArrayList arrayList2 = new ArrayList();
                    arrayList2.add(new ThemeDescription(null, 0, null, null, null, null, "windowBackgroundGray"));
                    this.items.add(arrayList2);
                }
            }

            public int getItemCount() {
                return this.items.isEmpty() ? 0 : this.items.size() + 1;
            }

            public ArrayList<ThemeDescription> getItem(int i) {
                return (i < 0 || i >= this.items.size()) ? null : (ArrayList) this.items.get(i);
            }

            public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                View view;
                if (i != 0) {
                    view = new View(this.context);
                    view.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(56.0f)));
                } else {
                    view = new TextColorThemeCell(this.context);
                    view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                }
                return new Holder(view);
            }

            public void onBindViewHolder(ViewHolder viewHolder, int i) {
                if (viewHolder.getItemViewType() == 0) {
                    int i2 = 0;
                    ThemeDescription themeDescription = (ThemeDescription) ((ArrayList) this.items.get(i - 1)).get(0);
                    if (!themeDescription.getCurrentKey().equals("chat_wallpaper")) {
                        i2 = themeDescription.getSetColor();
                    }
                    ((TextColorThemeCell) viewHolder.itemView).setTextAndColor(themeDescription.getTitle(), i2);
                }
            }
        }

        public class SearchAdapter extends SelectionAdapter {
            private Context context;
            private int currentCount;
            private int lastSearchId;
            private String lastSearchText;
            private ArrayList<CharSequence> searchNames = new ArrayList();
            private ArrayList<ArrayList<ThemeDescription>> searchResult = new ArrayList();
            private Runnable searchRunnable;

            public int getItemViewType(int i) {
                return i == 0 ? 1 : 0;
            }

            public boolean isEnabled(ViewHolder viewHolder) {
                return true;
            }

            public SearchAdapter(Context context) {
                this.context = context;
            }

            public CharSequence generateSearchName(String str, String str2) {
                if (TextUtils.isEmpty(str)) {
                    return "";
                }
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
                str = str.trim();
                String toLowerCase = str.toLowerCase();
                int i = 0;
                while (true) {
                    int indexOf = toLowerCase.indexOf(str2, i);
                    if (indexOf == -1) {
                        break;
                    }
                    int length = str2.length() + indexOf;
                    if (i != 0 && i != indexOf + 1) {
                        spannableStringBuilder.append(str.substring(i, indexOf));
                    } else if (i == 0 && indexOf != 0) {
                        spannableStringBuilder.append(str.substring(0, indexOf));
                    }
                    String substring = str.substring(indexOf, Math.min(str.length(), length));
                    String str3 = " ";
                    if (substring.startsWith(str3)) {
                        spannableStringBuilder.append(str3);
                    }
                    substring = substring.trim();
                    indexOf = spannableStringBuilder.length();
                    spannableStringBuilder.append(substring);
                    spannableStringBuilder.setSpan(new ForegroundColorSpan(-11697229), indexOf, substring.length() + indexOf, 33);
                    i = length;
                }
                if (i != -1 && i < str.length()) {
                    spannableStringBuilder.append(str.substring(i));
                }
                return spannableStringBuilder;
            }

            private void searchDialogsInternal(String str, int i) {
                try {
                    str = str.trim().toLowerCase();
                    if (str.length() == 0) {
                        this.lastSearchId = -1;
                        updateSearchResults(new ArrayList(), new ArrayList(), this.lastSearchId);
                        return;
                    }
                    String translitString = LocaleController.getInstance().getTranslitString(str);
                    if (str.equals(translitString) || translitString.length() == 0) {
                        translitString = null;
                    }
                    String[] strArr = new String[((translitString != null ? 1 : 0) + 1)];
                    strArr[0] = str;
                    if (translitString != null) {
                        strArr[1] = translitString;
                    }
                    ArrayList arrayList = new ArrayList();
                    ArrayList arrayList2 = new ArrayList();
                    int size = EditorAlert.this.listAdapter.items.size();
                    for (int i2 = 0; i2 < size; i2++) {
                        ArrayList arrayList3 = (ArrayList) EditorAlert.this.listAdapter.items.get(i2);
                        String currentKey = ((ThemeDescription) arrayList3.get(0)).getCurrentKey();
                        String toLowerCase = currentKey.toLowerCase();
                        for (CharSequence charSequence : strArr) {
                            if (toLowerCase.contains(charSequence)) {
                                arrayList.add(arrayList3);
                                arrayList2.add(generateSearchName(currentKey, charSequence));
                                break;
                            }
                        }
                    }
                    updateSearchResults(arrayList, arrayList2, i);
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }

            private void updateSearchResults(ArrayList<ArrayList<ThemeDescription>> arrayList, ArrayList<CharSequence> arrayList2, int i) {
                AndroidUtilities.runOnUIThread(new -$$Lambda$ThemeEditorView$EditorAlert$SearchAdapter$p-mG9tNYo33F1pFcBkO4OWkissQ(this, i, arrayList, arrayList2));
            }

            public /* synthetic */ void lambda$updateSearchResults$0$ThemeEditorView$EditorAlert$SearchAdapter(int i, ArrayList arrayList, ArrayList arrayList2) {
                if (i == this.lastSearchId) {
                    if (EditorAlert.this.listView.getAdapter() != EditorAlert.this.searchAdapter) {
                        EditorAlert editorAlert = EditorAlert.this;
                        editorAlert.topBeforeSwitch = editorAlert.getCurrentTop();
                        EditorAlert.this.listView.setAdapter(EditorAlert.this.searchAdapter);
                        EditorAlert.this.searchAdapter.notifyDataSetChanged();
                    }
                    Object obj = 1;
                    Object obj2 = (this.searchResult.isEmpty() || !arrayList.isEmpty()) ? null : 1;
                    if (!(this.searchResult.isEmpty() && arrayList.isEmpty())) {
                        obj = null;
                    }
                    if (obj2 != null) {
                        EditorAlert editorAlert2 = EditorAlert.this;
                        editorAlert2.topBeforeSwitch = editorAlert2.getCurrentTop();
                    }
                    this.searchResult = arrayList;
                    this.searchNames = arrayList2;
                    notifyDataSetChanged();
                    if (obj == null && obj2 == null && EditorAlert.this.topBeforeSwitch > 0) {
                        EditorAlert.this.layoutManager.scrollToPositionWithOffset(0, -EditorAlert.this.topBeforeSwitch);
                        EditorAlert.this.topBeforeSwitch = -1000;
                    }
                    EditorAlert.this.searchEmptyView.showTextView();
                }
            }

            public void searchDialogs(String str) {
                if (str == null || !str.equals(this.lastSearchText)) {
                    this.lastSearchText = str;
                    if (this.searchRunnable != null) {
                        Utilities.searchQueue.cancelRunnable(this.searchRunnable);
                        this.searchRunnable = null;
                    }
                    if (str == null || str.length() == 0) {
                        this.searchResult.clear();
                        EditorAlert editorAlert = EditorAlert.this;
                        editorAlert.topBeforeSwitch = editorAlert.getCurrentTop();
                        this.lastSearchId = -1;
                        notifyDataSetChanged();
                    } else {
                        int i = this.lastSearchId + 1;
                        this.lastSearchId = i;
                        this.searchRunnable = new -$$Lambda$ThemeEditorView$EditorAlert$SearchAdapter$QO0-7n9Zk7XEogLqnxXJbl4gmjs(this, str, i);
                        Utilities.searchQueue.postRunnable(this.searchRunnable, 300);
                    }
                }
            }

            public /* synthetic */ void lambda$searchDialogs$1$ThemeEditorView$EditorAlert$SearchAdapter(String str, int i) {
                searchDialogsInternal(str, i);
            }

            public int getItemCount() {
                return this.searchResult.isEmpty() ? 0 : this.searchResult.size() + 1;
            }

            public ArrayList<ThemeDescription> getItem(int i) {
                return (i < 0 || i >= this.searchResult.size()) ? null : (ArrayList) this.searchResult.get(i);
            }

            public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                View view;
                if (i != 0) {
                    view = new View(this.context);
                    view.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(56.0f)));
                } else {
                    view = new TextColorThemeCell(this.context);
                    view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                }
                return new Holder(view);
            }

            public void onBindViewHolder(ViewHolder viewHolder, int i) {
                if (viewHolder.getItemViewType() == 0) {
                    i--;
                    int i2 = 0;
                    ThemeDescription themeDescription = (ThemeDescription) ((ArrayList) this.searchResult.get(i)).get(0);
                    if (!themeDescription.getCurrentKey().equals("chat_wallpaper")) {
                        i2 = themeDescription.getSetColor();
                    }
                    ((TextColorThemeCell) viewHolder.itemView).setTextAndColor((CharSequence) this.searchNames.get(i), i2);
                }
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
            super(context2, true);
            this.shadowDrawable = context.getResources().getDrawable(NUM).mutate();
            this.containerView = new FrameLayout(context2) {
                private boolean ignoreLayout = false;
                private RectF rect1 = new RectF();

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
                    if (VERSION.SDK_INT >= 21 && !EditorAlert.this.isFullscreen) {
                        this.ignoreLayout = true;
                        setPadding(EditorAlert.this.backgroundPaddingLeft, AndroidUtilities.statusBarHeight, EditorAlert.this.backgroundPaddingLeft, 0);
                        this.ignoreLayout = false;
                    }
                    int dp = ((i2 - (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0)) + AndroidUtilities.dp(8.0f)) - Math.min(size, i2 - (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0));
                    if (EditorAlert.this.listView.getPaddingTop() != dp) {
                        this.ignoreLayout = true;
                        EditorAlert.this.listView.getPaddingTop();
                        EditorAlert.this.listView.setPadding(0, dp, 0, AndroidUtilities.dp(48.0f));
                        if (EditorAlert.this.colorPicker.getVisibility() == 0) {
                            EditorAlert editorAlert = EditorAlert.this;
                            editorAlert.setScrollOffsetY(editorAlert.listView.getPaddingTop());
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
                /* JADX WARNING: Removed duplicated region for block: B:15:0x00b4  */
                /* JADX WARNING: Removed duplicated region for block: B:20:? A:{SYNTHETIC, RETURN} */
                /* JADX WARNING: Removed duplicated region for block: B:18:0x0154  */
                public void onDraw(android.graphics.Canvas r14) {
                    /*
                    r13 = this;
                    r0 = org.telegram.ui.Components.ThemeEditorView.EditorAlert.this;
                    r0 = r0.scrollOffsetY;
                    r1 = org.telegram.ui.Components.ThemeEditorView.EditorAlert.this;
                    r1 = r1.backgroundPaddingTop;
                    r0 = r0 - r1;
                    r1 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
                    r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
                    r0 = r0 + r1;
                    r1 = org.telegram.ui.Components.ThemeEditorView.EditorAlert.this;
                    r1 = r1.scrollOffsetY;
                    r2 = org.telegram.ui.Components.ThemeEditorView.EditorAlert.this;
                    r2 = r2.backgroundPaddingTop;
                    r1 = r1 - r2;
                    r2 = NUM; // 0x41500000 float:13.0 double:5.413783207E-315;
                    r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
                    r1 = r1 - r2;
                    r2 = r13.getMeasuredHeight();
                    r3 = NUM; // 0x41var_ float:30.0 double:5.465589745E-315;
                    r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
                    r2 = r2 + r3;
                    r3 = org.telegram.ui.Components.ThemeEditorView.EditorAlert.this;
                    r3 = r3.backgroundPaddingTop;
                    r2 = r2 + r3;
                    r3 = org.telegram.ui.Components.ThemeEditorView.EditorAlert.this;
                    r3 = r3.isFullscreen;
                    r4 = 0;
                    r5 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
                    if (r3 != 0) goto L_0x0096;
                L_0x0045:
                    r3 = android.os.Build.VERSION.SDK_INT;
                    r6 = 21;
                    if (r3 < r6) goto L_0x0096;
                L_0x004b:
                    r3 = org.telegram.messenger.AndroidUtilities.statusBarHeight;
                    r1 = r1 + r3;
                    r0 = r0 + r3;
                    r2 = r2 - r3;
                    r3 = org.telegram.ui.Components.ThemeEditorView.EditorAlert.this;
                    r3 = r3.backgroundPaddingTop;
                    r3 = r3 + r1;
                    r6 = org.telegram.messenger.AndroidUtilities.statusBarHeight;
                    r7 = r6 * 2;
                    if (r3 >= r7) goto L_0x007b;
                L_0x005d:
                    r3 = r6 * 2;
                    r3 = r3 - r1;
                    r7 = org.telegram.ui.Components.ThemeEditorView.EditorAlert.this;
                    r7 = r7.backgroundPaddingTop;
                    r3 = r3 - r7;
                    r3 = java.lang.Math.min(r6, r3);
                    r1 = r1 - r3;
                    r2 = r2 + r3;
                    r3 = r3 * 2;
                    r3 = (float) r3;
                    r6 = org.telegram.messenger.AndroidUtilities.statusBarHeight;
                    r6 = (float) r6;
                    r3 = r3 / r6;
                    r3 = java.lang.Math.min(r5, r3);
                    r3 = r5 - r3;
                    goto L_0x007d;
                L_0x007b:
                    r3 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
                L_0x007d:
                    r6 = org.telegram.ui.Components.ThemeEditorView.EditorAlert.this;
                    r6 = r6.backgroundPaddingTop;
                    r6 = r6 + r1;
                    r7 = org.telegram.messenger.AndroidUtilities.statusBarHeight;
                    if (r6 >= r7) goto L_0x0098;
                L_0x0088:
                    r6 = r7 - r1;
                    r8 = org.telegram.ui.Components.ThemeEditorView.EditorAlert.this;
                    r8 = r8.backgroundPaddingTop;
                    r6 = r6 - r8;
                    r6 = java.lang.Math.min(r7, r6);
                    goto L_0x0099;
                L_0x0096:
                    r3 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
                L_0x0098:
                    r6 = 0;
                L_0x0099:
                    r7 = org.telegram.ui.Components.ThemeEditorView.EditorAlert.this;
                    r7 = r7.shadowDrawable;
                    r8 = r13.getMeasuredWidth();
                    r7.setBounds(r4, r1, r8, r2);
                    r2 = org.telegram.ui.Components.ThemeEditorView.EditorAlert.this;
                    r2 = r2.shadowDrawable;
                    r2.draw(r14);
                    r2 = -1;
                    r4 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1));
                    if (r4 == 0) goto L_0x00ff;
                L_0x00b4:
                    r4 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint;
                    r4.setColor(r2);
                    r4 = r13.rect1;
                    r5 = org.telegram.ui.Components.ThemeEditorView.EditorAlert.this;
                    r5 = r5.backgroundPaddingLeft;
                    r5 = (float) r5;
                    r7 = org.telegram.ui.Components.ThemeEditorView.EditorAlert.this;
                    r7 = r7.backgroundPaddingTop;
                    r7 = r7 + r1;
                    r7 = (float) r7;
                    r8 = r13.getMeasuredWidth();
                    r9 = org.telegram.ui.Components.ThemeEditorView.EditorAlert.this;
                    r9 = r9.backgroundPaddingLeft;
                    r8 = r8 - r9;
                    r8 = (float) r8;
                    r9 = org.telegram.ui.Components.ThemeEditorView.EditorAlert.this;
                    r9 = r9.backgroundPaddingTop;
                    r9 = r9 + r1;
                    r1 = NUM; // 0x41CLASSNAME float:24.0 double:5.450047783E-315;
                    r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
                    r9 = r9 + r1;
                    r1 = (float) r9;
                    r4.set(r5, r7, r8, r1);
                    r1 = r13.rect1;
                    r4 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
                    r5 = org.telegram.messenger.AndroidUtilities.dp(r4);
                    r5 = (float) r5;
                    r5 = r5 * r3;
                    r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
                    r4 = (float) r4;
                    r4 = r4 * r3;
                    r3 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint;
                    r14.drawRoundRect(r1, r5, r4, r3);
                L_0x00ff:
                    r1 = NUM; // 0x42100000 float:36.0 double:5.47595105E-315;
                    r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
                    r3 = r13.rect1;
                    r4 = r13.getMeasuredWidth();
                    r4 = r4 - r1;
                    r4 = r4 / 2;
                    r4 = (float) r4;
                    r5 = (float) r0;
                    r7 = r13.getMeasuredWidth();
                    r7 = r7 + r1;
                    r7 = r7 / 2;
                    r1 = (float) r7;
                    r7 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
                    r7 = org.telegram.messenger.AndroidUtilities.dp(r7);
                    r0 = r0 + r7;
                    r0 = (float) r0;
                    r3.set(r4, r5, r1, r0);
                    r0 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint;
                    r1 = -1973016; // 0xffffffffffe1e4e8 float:NaN double:NaN;
                    r0.setColor(r1);
                    r0 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint;
                    r1 = NUM; // 0x437var_ float:255.0 double:5.5947823E-315;
                    r3 = org.telegram.ui.Components.ThemeEditorView.EditorAlert.this;
                    r3 = r3.listView;
                    r3 = r3.getAlpha();
                    r3 = r3 * r1;
                    r1 = (int) r3;
                    r0.setAlpha(r1);
                    r0 = r13.rect1;
                    r1 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
                    r3 = org.telegram.messenger.AndroidUtilities.dp(r1);
                    r3 = (float) r3;
                    r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
                    r1 = (float) r1;
                    r4 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint;
                    r14.drawRoundRect(r0, r3, r1, r4);
                    if (r6 <= 0) goto L_0x019a;
                L_0x0154:
                    r0 = 255; // 0xff float:3.57E-43 double:1.26E-321;
                    r1 = android.graphics.Color.red(r2);
                    r1 = (float) r1;
                    r3 = NUM; // 0x3f4ccccd float:0.8 double:5.246966156E-315;
                    r1 = r1 * r3;
                    r1 = (int) r1;
                    r4 = android.graphics.Color.green(r2);
                    r4 = (float) r4;
                    r4 = r4 * r3;
                    r4 = (int) r4;
                    r2 = android.graphics.Color.blue(r2);
                    r2 = (float) r2;
                    r2 = r2 * r3;
                    r2 = (int) r2;
                    r0 = android.graphics.Color.argb(r0, r1, r4, r2);
                    r1 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint;
                    r1.setColor(r0);
                    r0 = org.telegram.ui.Components.ThemeEditorView.EditorAlert.this;
                    r0 = r0.backgroundPaddingLeft;
                    r8 = (float) r0;
                    r0 = org.telegram.messenger.AndroidUtilities.statusBarHeight;
                    r0 = r0 - r6;
                    r9 = (float) r0;
                    r0 = r13.getMeasuredWidth();
                    r1 = org.telegram.ui.Components.ThemeEditorView.EditorAlert.this;
                    r1 = r1.backgroundPaddingLeft;
                    r0 = r0 - r1;
                    r10 = (float) r0;
                    r0 = org.telegram.messenger.AndroidUtilities.statusBarHeight;
                    r11 = (float) r0;
                    r12 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint;
                    r7 = r14;
                    r7.drawRect(r8, r9, r10, r11, r12);
                L_0x019a:
                    return;
                    */
                    throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ThemeEditorView$EditorAlert$AnonymousClass1.onDraw(android.graphics.Canvas):void");
                }
            };
            this.containerView.setWillNotDraw(false);
            ViewGroup viewGroup = this.containerView;
            int i = this.backgroundPaddingLeft;
            viewGroup.setPadding(i, 0, i, 0);
            this.frameLayout = new FrameLayout(context2);
            this.frameLayout.setBackgroundColor(-1);
            this.searchField = new SearchField(context2);
            this.frameLayout.addView(this.searchField, LayoutHelper.createFrame(-1, -1, 51));
            this.listView = new RecyclerListView(context2) {
                /* Access modifiers changed, original: protected */
                public boolean allowSelectChildAtPosition(float f, float f2) {
                    return f2 >= ((float) ((EditorAlert.this.scrollOffsetY + AndroidUtilities.dp(48.0f)) + (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0)));
                }
            };
            this.listView.setSelectorDrawableColor(NUM);
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
            this.searchAdapter = new SearchAdapter(context2);
            this.listView.setGlowColor(-657673);
            this.listView.setItemAnimator(null);
            this.listView.setLayoutAnimation(null);
            this.listView.setOnItemClickListener(new -$$Lambda$ThemeEditorView$EditorAlert$kK2jW5xMCGw4an44hq65kfySCdU(this));
            this.listView.setOnScrollListener(new OnScrollListener() {
                public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                    EditorAlert.this.updateLayout();
                }
            });
            this.searchEmptyView = new EmptyTextProgressView(context2);
            this.searchEmptyView.setShowAtCenter(true);
            this.searchEmptyView.showTextView();
            this.searchEmptyView.setText(LocaleController.getString("NoResult", NUM));
            this.listView.setEmptyView(this.searchEmptyView);
            this.containerView.addView(this.searchEmptyView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 52.0f, 0.0f, 0.0f));
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-1, AndroidUtilities.getShadowHeight(), 51);
            layoutParams.topMargin = AndroidUtilities.dp(58.0f);
            this.shadow[0] = new View(context2);
            this.shadow[0].setBackgroundColor(NUM);
            this.shadow[0].setAlpha(0.0f);
            this.shadow[0].setTag(Integer.valueOf(1));
            this.containerView.addView(this.shadow[0], layoutParams);
            this.containerView.addView(this.frameLayout, LayoutHelper.createFrame(-1, 58, 51));
            this.colorPicker = new ColorPicker(this, context2);
            this.colorPicker.setVisibility(8);
            this.containerView.addView(this.colorPicker, LayoutHelper.createFrame(-1, -1, 1));
            layoutParams = new FrameLayout.LayoutParams(-1, AndroidUtilities.getShadowHeight(), 83);
            layoutParams.bottomMargin = AndroidUtilities.dp(48.0f);
            this.shadow[1] = new View(context2);
            this.shadow[1].setBackgroundColor(NUM);
            this.containerView.addView(this.shadow[1], layoutParams);
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
            textView = new TextView(context2);
            textView.setTextSize(1, 14.0f);
            textView.setTextColor(-15095832);
            textView.setGravity(17);
            textView.setBackgroundDrawable(Theme.createSelectorDrawable(NUM, 0));
            textView.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
            textView.setText(LocaleController.getString("Cancel", NUM).toUpperCase());
            textView.setTypeface(AndroidUtilities.getTypeface(str));
            this.bottomLayout.addView(textView, LayoutHelper.createFrame(-2, -1, 51));
            textView.setOnClickListener(new -$$Lambda$ThemeEditorView$EditorAlert$e8sB4SzqRAAe3BbXeRhAVLL0Fkg(this));
            LinearLayout linearLayout = new LinearLayout(context2);
            linearLayout.setOrientation(0);
            this.bottomLayout.addView(linearLayout, LayoutHelper.createFrame(-2, -1, 53));
            TextView textView2 = new TextView(context2);
            textView2.setTextSize(1, 14.0f);
            textView2.setTextColor(-15095832);
            textView2.setGravity(17);
            textView2.setBackgroundDrawable(Theme.createSelectorDrawable(NUM, 0));
            textView2.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
            textView2.setText(LocaleController.getString("Default", NUM).toUpperCase());
            textView2.setTypeface(AndroidUtilities.getTypeface(str));
            linearLayout.addView(textView2, LayoutHelper.createFrame(-2, -1, 51));
            textView2.setOnClickListener(new -$$Lambda$ThemeEditorView$EditorAlert$KOpMpGNwWrKZ5XW39TuNtWWXkWM(this));
            textView2 = new TextView(context2);
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
            if (i != 0) {
                Adapter adapter = this.listView.getAdapter();
                Adapter adapter2 = this.listAdapter;
                if (adapter == adapter2) {
                    this.this$0.currentThemeDesription = adapter2.getItem(i - 1);
                } else {
                    this.this$0.currentThemeDesription = this.searchAdapter.getItem(i - 1);
                }
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
        }

        public /* synthetic */ void lambda$new$1$ThemeEditorView$EditorAlert(View view) {
            dismiss();
        }

        public /* synthetic */ void lambda$new$2$ThemeEditorView$EditorAlert(View view) {
            Theme.saveCurrentTheme(this.this$0.themeInfo, true, false, false);
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

        private void runShadowAnimation(final int i, final boolean z) {
            if ((z && this.shadow[i].getTag() != null) || (!z && this.shadow[i].getTag() == null)) {
                this.shadow[i].setTag(z ? null : Integer.valueOf(1));
                if (z) {
                    this.shadow[i].setVisibility(0);
                }
                AnimatorSet[] animatorSetArr = this.shadowAnimation;
                if (animatorSetArr[i] != null) {
                    animatorSetArr[i].cancel();
                }
                this.shadowAnimation[i] = new AnimatorSet();
                AnimatorSet animatorSet = this.shadowAnimation[i];
                Animator[] animatorArr = new Animator[1];
                Object obj = this.shadow[i];
                Property property = View.ALPHA;
                float[] fArr = new float[1];
                fArr[0] = z ? 1.0f : 0.0f;
                animatorArr[0] = ObjectAnimator.ofFloat(obj, property, fArr);
                animatorSet.playTogether(animatorArr);
                this.shadowAnimation[i].setDuration(150);
                this.shadowAnimation[i].addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        if (EditorAlert.this.shadowAnimation[i] != null && EditorAlert.this.shadowAnimation[i].equals(animator)) {
                            if (!z) {
                                EditorAlert.this.shadow[i].setVisibility(4);
                            }
                            EditorAlert.this.shadowAnimation[i] = null;
                        }
                    }

                    public void onAnimationCancel(Animator animator) {
                        if (EditorAlert.this.shadowAnimation[i] != null && EditorAlert.this.shadowAnimation[i].equals(animator)) {
                            EditorAlert.this.shadowAnimation[i] = null;
                        }
                    }
                });
                this.shadowAnimation[i].start();
            }
        }

        public void dismissInternal() {
            super.dismissInternal();
            if (this.searchField.searchEditText.isFocused()) {
                AndroidUtilities.hideKeyboard(this.searchField.searchEditText);
            }
        }

        private void setColorPickerVisible(boolean z) {
            String str = "scrollOffsetY";
            float f = 0.0f;
            if (z) {
                this.animationInProgress = true;
                this.colorPicker.setVisibility(0);
                this.bottomLayout.setVisibility(0);
                this.colorPicker.setAlpha(0.0f);
                this.bottomLayout.setAlpha(0.0f);
                this.previousScrollPosition = this.scrollOffsetY;
                AnimatorSet animatorSet = new AnimatorSet();
                Animator[] animatorArr = new Animator[8];
                animatorArr[0] = ObjectAnimator.ofFloat(this.colorPicker, View.ALPHA, new float[]{1.0f});
                animatorArr[1] = ObjectAnimator.ofFloat(this.bottomLayout, View.ALPHA, new float[]{1.0f});
                animatorArr[2] = ObjectAnimator.ofFloat(this.listView, View.ALPHA, new float[]{0.0f});
                animatorArr[3] = ObjectAnimator.ofFloat(this.frameLayout, View.ALPHA, new float[]{0.0f});
                animatorArr[4] = ObjectAnimator.ofFloat(this.shadow[0], View.ALPHA, new float[]{0.0f});
                animatorArr[5] = ObjectAnimator.ofFloat(this.searchEmptyView, View.ALPHA, new float[]{0.0f});
                animatorArr[6] = ObjectAnimator.ofFloat(this.bottomSaveLayout, View.ALPHA, new float[]{0.0f});
                animatorArr[7] = ObjectAnimator.ofInt(this, str, new int[]{this.listView.getPaddingTop()});
                animatorSet.playTogether(animatorArr);
                animatorSet.setDuration(150);
                animatorSet.setInterpolator(this.this$0.decelerateInterpolator);
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        EditorAlert.this.listView.setVisibility(4);
                        EditorAlert.this.searchField.setVisibility(4);
                        EditorAlert.this.bottomSaveLayout.setVisibility(4);
                        EditorAlert.this.animationInProgress = false;
                    }
                });
                animatorSet.start();
                return;
            }
            if (this.this$0.parentActivity != null) {
                ((LaunchActivity) this.this$0.parentActivity).rebuildAllFragments(false);
            }
            Theme.saveCurrentTheme(this.this$0.themeInfo, false, false, false);
            if (this.listView.getAdapter() == this.listAdapter) {
                AndroidUtilities.hideKeyboard(getCurrentFocus());
            }
            this.animationInProgress = true;
            this.listView.setVisibility(0);
            this.bottomSaveLayout.setVisibility(0);
            this.searchField.setVisibility(0);
            this.listView.setAlpha(0.0f);
            AnimatorSet animatorSet2 = new AnimatorSet();
            Animator[] animatorArr2 = new Animator[8];
            animatorArr2[0] = ObjectAnimator.ofFloat(this.colorPicker, View.ALPHA, new float[]{0.0f});
            animatorArr2[1] = ObjectAnimator.ofFloat(this.bottomLayout, View.ALPHA, new float[]{0.0f});
            animatorArr2[2] = ObjectAnimator.ofFloat(this.listView, View.ALPHA, new float[]{1.0f});
            animatorArr2[3] = ObjectAnimator.ofFloat(this.frameLayout, View.ALPHA, new float[]{1.0f});
            View[] viewArr = this.shadow;
            Object obj = viewArr[0];
            Property property = View.ALPHA;
            float[] fArr = new float[1];
            if (viewArr[0].getTag() == null) {
                f = 1.0f;
            }
            fArr[0] = f;
            animatorArr2[4] = ObjectAnimator.ofFloat(obj, property, fArr);
            animatorArr2[5] = ObjectAnimator.ofFloat(this.searchEmptyView, View.ALPHA, new float[]{1.0f});
            animatorArr2[6] = ObjectAnimator.ofFloat(this.bottomSaveLayout, View.ALPHA, new float[]{1.0f});
            animatorArr2[7] = ObjectAnimator.ofInt(this, str, new int[]{this.previousScrollPosition});
            animatorSet2.playTogether(animatorArr2);
            animatorSet2.setDuration(150);
            animatorSet2.setInterpolator(this.this$0.decelerateInterpolator);
            animatorSet2.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    if (EditorAlert.this.listView.getAdapter() == EditorAlert.this.searchAdapter) {
                        EditorAlert.this.searchField.showKeyboard();
                    }
                    EditorAlert.this.colorPicker.setVisibility(8);
                    EditorAlert.this.bottomLayout.setVisibility(8);
                    EditorAlert.this.animationInProgress = false;
                }
            });
            animatorSet2.start();
            this.listView.getAdapter().notifyItemChanged(this.this$0.currentThemeDesriptionPosition);
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
                if (paddingTop <= (-AndroidUtilities.dp(1.0f)) || holder == null || holder.getAdapterPosition() != 0) {
                    runShadowAnimation(0, true);
                    paddingTop = 0;
                } else {
                    runShadowAnimation(0, false);
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
            this.frameLayout.setTranslationY((float) this.scrollOffsetY);
            this.colorPicker.setTranslationY((float) this.scrollOffsetY);
            this.searchEmptyView.setTranslationY((float) this.scrollOffsetY);
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

    public void show(Activity activity, ThemeInfo themeInfo) {
        if (Instance != null) {
            Instance.destroy();
        }
        this.hidden = false;
        this.themeInfo = themeInfo;
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
                    Theme.setThemeWallpaper(ThemeEditorView.this.themeInfo, bitmap, file);
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
        this.windowView.setBackgroundResource(NUM);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.windowView, View.ALPHA, new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(this.windowView, View.SCALE_X, new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(this.windowView, View.SCALE_Y, new float[]{0.0f, 1.0f})});
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
                Animator[] animatorArr = new Animator[3];
                animatorArr[0] = ObjectAnimator.ofFloat(this.windowView, View.ALPHA, new float[]{1.0f, 0.0f});
                animatorArr[1] = ObjectAnimator.ofFloat(this.windowView, View.SCALE_X, new float[]{1.0f, 0.0f});
                animatorArr[2] = ObjectAnimator.ofFloat(this.windowView, View.SCALE_Y, new float[]{1.0f, 0.0f});
                animatorSet.playTogether(animatorArr);
                animatorSet.setInterpolator(this.decelerateInterpolator);
                animatorSet.setDuration(150);
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        if (ThemeEditorView.this.windowView != null) {
                            ThemeEditorView.this.windowView.setBackground(null);
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

    /* JADX WARNING: Removed duplicated region for block: B:31:0x0114  */
    /* JADX WARNING: Removed duplicated region for block: B:54:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x017f  */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x0114  */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x017f  */
    /* JADX WARNING: Removed duplicated region for block: B:54:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x0114  */
    /* JADX WARNING: Removed duplicated region for block: B:54:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x017f  */
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
        r12 = "sidex";
        r13 = "x";
        r14 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        if (r10 <= r9) goto L_0x00e5;
    L_0x003c:
        r10 = r0.windowLayoutParams;
        r10 = r10.x;
        if (r10 >= 0) goto L_0x004b;
    L_0x0042:
        r15 = r0.editorWidth;
        r15 = -r15;
        r15 = r15 / 4;
        if (r10 <= r15) goto L_0x004b;
    L_0x0049:
        goto L_0x00e5;
    L_0x004b:
        r10 = r0.windowLayoutParams;
        r10 = r10.x;
        r10 = r5 - r10;
        r10 = java.lang.Math.abs(r10);
        if (r10 <= r9) goto L_0x00b7;
    L_0x0057:
        r10 = r0.windowLayoutParams;
        r10 = r10.x;
        r15 = org.telegram.messenger.AndroidUtilities.displaySize;
        r15 = r15.x;
        r2 = r0.editorWidth;
        r11 = r15 - r2;
        if (r10 <= r11) goto L_0x006d;
    L_0x0065:
        r2 = r2 / 4;
        r2 = r2 * 3;
        r15 = r15 - r2;
        if (r10 >= r15) goto L_0x006d;
    L_0x006c:
        goto L_0x00b7;
    L_0x006d:
        r2 = r0.windowView;
        r2 = r2.getAlpha();
        r2 = (r2 > r14 ? 1 : (r2 == r14 ? 0 : -1));
        if (r2 == 0) goto L_0x00a3;
    L_0x0077:
        r1 = new java.util.ArrayList;
        r1.<init>();
        r2 = r0.windowLayoutParams;
        r2 = r2.x;
        if (r2 >= 0) goto L_0x0091;
    L_0x0082:
        r2 = new int[r4];
        r5 = r0.editorWidth;
        r5 = -r5;
        r2[r3] = r5;
        r2 = android.animation.ObjectAnimator.ofInt(r0, r13, r2);
        r1.add(r2);
        goto L_0x00a0;
    L_0x0091:
        r2 = new int[r4];
        r5 = org.telegram.messenger.AndroidUtilities.displaySize;
        r5 = r5.x;
        r2[r3] = r5;
        r2 = android.animation.ObjectAnimator.ofInt(r0, r13, r2);
        r1.add(r2);
    L_0x00a0:
        r2 = r1;
        r1 = 1;
        goto L_0x0112;
    L_0x00a3:
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
        r8.putInt(r12, r1);
        r1 = 0;
        goto L_0x00e3;
    L_0x00b7:
        r1 = new java.util.ArrayList;
        r1.<init>();
        r8.putInt(r12, r4);
        r2 = r0.windowView;
        r2 = r2.getAlpha();
        r2 = (r2 > r14 ? 1 : (r2 == r14 ? 0 : -1));
        if (r2 == 0) goto L_0x00d8;
    L_0x00c9:
        r2 = r0.windowView;
        r10 = android.view.View.ALPHA;
        r11 = new float[r4];
        r11[r3] = r14;
        r2 = android.animation.ObjectAnimator.ofFloat(r2, r10, r11);
        r1.add(r2);
    L_0x00d8:
        r2 = new int[r4];
        r2[r3] = r5;
        r2 = android.animation.ObjectAnimator.ofInt(r0, r13, r2);
        r1.add(r2);
    L_0x00e3:
        r2 = r1;
        goto L_0x0111;
    L_0x00e5:
        r2 = new java.util.ArrayList;
        r2.<init>();
        r8.putInt(r12, r3);
        r5 = r0.windowView;
        r5 = r5.getAlpha();
        r5 = (r5 > r14 ? 1 : (r5 == r14 ? 0 : -1));
        if (r5 == 0) goto L_0x0106;
    L_0x00f7:
        r5 = r0.windowView;
        r10 = android.view.View.ALPHA;
        r11 = new float[r4];
        r11[r3] = r14;
        r5 = android.animation.ObjectAnimator.ofFloat(r5, r10, r11);
        r2.add(r5);
    L_0x0106:
        r5 = new int[r4];
        r5[r3] = r1;
        r1 = android.animation.ObjectAnimator.ofInt(r0, r13, r5);
        r2.add(r1);
    L_0x0111:
        r1 = 0;
    L_0x0112:
        if (r1 != 0) goto L_0x017d;
    L_0x0114:
        r5 = r0.windowLayoutParams;
        r5 = r5.y;
        r5 = r6 - r5;
        r5 = java.lang.Math.abs(r5);
        r10 = "y";
        r11 = "sidey";
        if (r5 <= r9) goto L_0x0165;
    L_0x0125:
        r5 = r0.windowLayoutParams;
        r5 = r5.y;
        r12 = org.telegram.ui.ActionBar.ActionBar.getCurrentActionBarHeight();
        if (r5 > r12) goto L_0x0130;
    L_0x012f:
        goto L_0x0165;
    L_0x0130:
        r5 = r0.windowLayoutParams;
        r5 = r5.y;
        r5 = r7 - r5;
        r5 = java.lang.Math.abs(r5);
        if (r5 > r9) goto L_0x0152;
    L_0x013c:
        if (r2 != 0) goto L_0x0143;
    L_0x013e:
        r2 = new java.util.ArrayList;
        r2.<init>();
    L_0x0143:
        r8.putInt(r11, r4);
        r5 = new int[r4];
        r5[r3] = r7;
        r5 = android.animation.ObjectAnimator.ofInt(r0, r10, r5);
        r2.add(r5);
        goto L_0x017a;
    L_0x0152:
        r5 = r0.windowLayoutParams;
        r5 = r5.y;
        r5 = r5 - r6;
        r5 = (float) r5;
        r7 = r7 - r6;
        r6 = (float) r7;
        r5 = r5 / r6;
        r6 = "py";
        r8.putFloat(r6, r5);
        r5 = 2;
        r8.putInt(r11, r5);
        goto L_0x017a;
    L_0x0165:
        if (r2 != 0) goto L_0x016c;
    L_0x0167:
        r2 = new java.util.ArrayList;
        r2.<init>();
    L_0x016c:
        r8.putInt(r11, r3);
        r5 = new int[r4];
        r5[r3] = r6;
        r5 = android.animation.ObjectAnimator.ofInt(r0, r10, r5);
        r2.add(r5);
    L_0x017a:
        r8.commit();
    L_0x017d:
        if (r2 == 0) goto L_0x01b9;
    L_0x017f:
        r5 = r0.decelerateInterpolator;
        if (r5 != 0) goto L_0x018a;
    L_0x0183:
        r5 = new android.view.animation.DecelerateInterpolator;
        r5.<init>();
        r0.decelerateInterpolator = r5;
    L_0x018a:
        r5 = new android.animation.AnimatorSet;
        r5.<init>();
        r6 = r0.decelerateInterpolator;
        r5.setInterpolator(r6);
        r6 = 150; // 0x96 float:2.1E-43 double:7.4E-322;
        r5.setDuration(r6);
        if (r1 == 0) goto L_0x01b3;
    L_0x019b:
        r1 = r0.windowView;
        r6 = android.view.View.ALPHA;
        r4 = new float[r4];
        r7 = 0;
        r4[r3] = r7;
        r1 = android.animation.ObjectAnimator.ofFloat(r1, r6, r4);
        r2.add(r1);
        r1 = new org.telegram.ui.Components.ThemeEditorView$4;
        r1.<init>();
        r5.addListener(r1);
    L_0x01b3:
        r5.playTogether(r2);
        r5.start();
    L_0x01b9:
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
