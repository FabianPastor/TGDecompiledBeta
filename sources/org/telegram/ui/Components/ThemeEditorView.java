package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Property;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarLayout;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.TextColorThemeCell;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.WallpaperUpdater;
import org.telegram.ui.LaunchActivity;

public class ThemeEditorView {
    private static volatile ThemeEditorView Instance = null;
    /* access modifiers changed from: private */
    public ArrayList<ThemeDescription> currentThemeDesription;
    /* access modifiers changed from: private */
    public int currentThemeDesriptionPosition;
    /* access modifiers changed from: private */
    public DecelerateInterpolator decelerateInterpolator;
    /* access modifiers changed from: private */
    public EditorAlert editorAlert;
    private final int editorHeight = AndroidUtilities.dp(54.0f);
    /* access modifiers changed from: private */
    public final int editorWidth = AndroidUtilities.dp(54.0f);
    private boolean hidden;
    /* access modifiers changed from: private */
    public Activity parentActivity;
    private SharedPreferences preferences;
    /* access modifiers changed from: private */
    public Theme.ThemeInfo themeInfo;
    /* access modifiers changed from: private */
    public WallpaperUpdater wallpaperUpdater;
    /* access modifiers changed from: private */
    public WindowManager.LayoutParams windowLayoutParams;
    /* access modifiers changed from: private */
    public WindowManager windowManager;
    /* access modifiers changed from: private */
    public FrameLayout windowView;

    public static ThemeEditorView getInstance() {
        return Instance;
    }

    public void destroy() {
        FrameLayout frameLayout;
        this.wallpaperUpdater.cleanup();
        if (this.parentActivity != null && (frameLayout = this.windowView) != null) {
            try {
                this.windowManager.removeViewImmediate(frameLayout);
                this.windowView = null;
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            try {
                EditorAlert editorAlert2 = this.editorAlert;
                if (editorAlert2 != null) {
                    editorAlert2.dismiss();
                    this.editorAlert = null;
                }
            } catch (Exception e2) {
                FileLog.e((Throwable) e2);
            }
            this.parentActivity = null;
            Instance = null;
        }
    }

    public class EditorAlert extends BottomSheet {
        /* access modifiers changed from: private */
        public boolean animationInProgress;
        /* access modifiers changed from: private */
        public FrameLayout bottomLayout;
        /* access modifiers changed from: private */
        public FrameLayout bottomSaveLayout;
        /* access modifiers changed from: private */
        public AnimatorSet colorChangeAnimation;
        /* access modifiers changed from: private */
        public ColorPicker colorPicker;
        private FrameLayout frameLayout;
        /* access modifiers changed from: private */
        public boolean ignoreTextChange;
        /* access modifiers changed from: private */
        public LinearLayoutManager layoutManager;
        /* access modifiers changed from: private */
        public ListAdapter listAdapter;
        /* access modifiers changed from: private */
        public RecyclerListView listView;
        /* access modifiers changed from: private */
        public int previousScrollPosition;
        private TextView saveButton;
        /* access modifiers changed from: private */
        public int scrollOffsetY;
        /* access modifiers changed from: private */
        public SearchAdapter searchAdapter;
        /* access modifiers changed from: private */
        public EmptyTextProgressView searchEmptyView;
        /* access modifiers changed from: private */
        public SearchField searchField;
        /* access modifiers changed from: private */
        public View[] shadow = new View[2];
        /* access modifiers changed from: private */
        public AnimatorSet[] shadowAnimation = new AnimatorSet[2];
        /* access modifiers changed from: private */
        public Drawable shadowDrawable;
        /* access modifiers changed from: private */
        public boolean startedColorChange;
        final /* synthetic */ ThemeEditorView this$0;
        /* access modifiers changed from: private */
        public int topBeforeSwitch;

        private class SearchField extends FrameLayout {
            private View backgroundView;
            /* access modifiers changed from: private */
            public ImageView clearSearchImageView;
            /* access modifiers changed from: private */
            public EditTextBoldCursor searchEditText;

            public SearchField(Context context) {
                super(context);
                View searchBackground = new View(context);
                searchBackground.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(18.0f), -854795));
                addView(searchBackground, LayoutHelper.createFrame(-1, 36.0f, 51, 14.0f, 11.0f, 14.0f, 0.0f));
                ImageView searchIconImageView = new ImageView(context);
                searchIconImageView.setScaleType(ImageView.ScaleType.CENTER);
                searchIconImageView.setImageResource(NUM);
                searchIconImageView.setColorFilter(new PorterDuffColorFilter(-6182737, PorterDuff.Mode.MULTIPLY));
                addView(searchIconImageView, LayoutHelper.createFrame(36, 36.0f, 51, 16.0f, 11.0f, 0.0f, 0.0f));
                ImageView imageView = new ImageView(context);
                this.clearSearchImageView = imageView;
                imageView.setScaleType(ImageView.ScaleType.CENTER);
                ImageView imageView2 = this.clearSearchImageView;
                CloseProgressDrawable2 progressDrawable = new CloseProgressDrawable2(EditorAlert.this) {
                    public int getCurrentColor() {
                        return -6182737;
                    }
                };
                imageView2.setImageDrawable(progressDrawable);
                progressDrawable.setSide(AndroidUtilities.dp(7.0f));
                this.clearSearchImageView.setScaleX(0.1f);
                this.clearSearchImageView.setScaleY(0.1f);
                this.clearSearchImageView.setAlpha(0.0f);
                addView(this.clearSearchImageView, LayoutHelper.createFrame(36, 36.0f, 53, 14.0f, 11.0f, 14.0f, 0.0f));
                this.clearSearchImageView.setOnClickListener(new ThemeEditorView$EditorAlert$SearchField$$ExternalSyntheticLambda0(this));
                AnonymousClass2 r2 = new EditTextBoldCursor(context, EditorAlert.this) {
                    public boolean dispatchTouchEvent(MotionEvent event) {
                        MotionEvent e = MotionEvent.obtain(event);
                        e.setLocation(e.getRawX(), e.getRawY() - EditorAlert.this.containerView.getTranslationY());
                        EditorAlert.this.listView.dispatchTouchEvent(e);
                        e.recycle();
                        return super.dispatchTouchEvent(event);
                    }
                };
                this.searchEditText = r2;
                r2.setTextSize(1, 16.0f);
                this.searchEditText.setHintTextColor(-6774617);
                this.searchEditText.setTextColor(-14540254);
                this.searchEditText.setBackgroundDrawable((Drawable) null);
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
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    public void afterTextChanged(Editable s) {
                        boolean showed = true;
                        boolean show = SearchField.this.searchEditText.length() > 0;
                        float f = 0.0f;
                        if (SearchField.this.clearSearchImageView.getAlpha() == 0.0f) {
                            showed = false;
                        }
                        if (show != showed) {
                            ViewPropertyAnimator animate = SearchField.this.clearSearchImageView.animate();
                            float f2 = 1.0f;
                            if (show) {
                                f = 1.0f;
                            }
                            ViewPropertyAnimator scaleX = animate.alpha(f).setDuration(150).scaleX(show ? 1.0f : 0.1f);
                            if (!show) {
                                f2 = 0.1f;
                            }
                            scaleX.scaleY(f2).start();
                        }
                        String text = SearchField.this.searchEditText.getText().toString();
                        if (text.length() != 0) {
                            if (EditorAlert.this.searchEmptyView != null) {
                                EditorAlert.this.searchEmptyView.setText(LocaleController.getString("NoResult", NUM));
                            }
                        } else if (EditorAlert.this.listView.getAdapter() != EditorAlert.this.listAdapter) {
                            int top = EditorAlert.this.getCurrentTop();
                            EditorAlert.this.searchEmptyView.setText(LocaleController.getString("NoChats", NUM));
                            EditorAlert.this.searchEmptyView.showTextView();
                            EditorAlert.this.listView.setAdapter(EditorAlert.this.listAdapter);
                            EditorAlert.this.listAdapter.notifyDataSetChanged();
                            if (top > 0) {
                                EditorAlert.this.layoutManager.scrollToPositionWithOffset(0, -top);
                            }
                        }
                        if (EditorAlert.this.searchAdapter != null) {
                            EditorAlert.this.searchAdapter.searchDialogs(text);
                        }
                    }
                });
                this.searchEditText.setOnEditorActionListener(new ThemeEditorView$EditorAlert$SearchField$$ExternalSyntheticLambda1(this));
            }

            /* renamed from: lambda$new$0$org-telegram-ui-Components-ThemeEditorView$EditorAlert$SearchField  reason: not valid java name */
            public /* synthetic */ void m1492xacCLASSNAMEvar_(View v) {
                this.searchEditText.setText("");
                AndroidUtilities.showKeyboard(this.searchEditText);
            }

            /* renamed from: lambda$new$1$org-telegram-ui-Components-ThemeEditorView$EditorAlert$SearchField  reason: not valid java name */
            public /* synthetic */ boolean m1493x49326be7(TextView v, int actionId, KeyEvent event) {
                if (event == null) {
                    return false;
                }
                if ((event.getAction() != 1 || event.getKeyCode() != 84) && (event.getAction() != 0 || event.getKeyCode() != 66)) {
                    return false;
                }
                AndroidUtilities.hideKeyboard(this.searchEditText);
                return false;
            }

            public void hideKeyboard() {
                AndroidUtilities.hideKeyboard(this.searchEditText);
            }

            public void showKeyboard() {
                this.searchEditText.requestFocus();
                AndroidUtilities.showKeyboard(this.searchEditText);
            }

            public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
                super.requestDisallowInterceptTouchEvent(disallowIntercept);
            }
        }

        private class ColorPicker extends FrameLayout {
            private float alpha = 1.0f;
            private LinearGradient alphaGradient;
            private boolean alphaPressed;
            private Drawable circleDrawable;
            private Paint circlePaint;
            private boolean circlePressed;
            /* access modifiers changed from: private */
            public EditTextBoldCursor[] colorEditText = new EditTextBoldCursor[4];
            private LinearGradient colorGradient;
            private float[] colorHSV = {0.0f, 0.0f, 1.0f};
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

            /* JADX WARNING: Illegal instructions before constructor call */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public ColorPicker(org.telegram.ui.Components.ThemeEditorView.EditorAlert r20, android.content.Context r21) {
                /*
                    r19 = this;
                    r0 = r19
                    r1 = r20
                    r2 = r21
                    r0.this$1 = r1
                    r0.<init>(r2)
                    r3 = 1101004800(0x41a00000, float:20.0)
                    int r4 = org.telegram.messenger.AndroidUtilities.dp(r3)
                    r0.paramValueSliderWidth = r4
                    r4 = 4
                    org.telegram.ui.Components.EditTextBoldCursor[] r5 = new org.telegram.ui.Components.EditTextBoldCursor[r4]
                    r0.colorEditText = r5
                    r5 = 3
                    float[] r6 = new float[r5]
                    r6 = {0, 0, NUM} // fill-array
                    r0.colorHSV = r6
                    r6 = 1065353216(0x3var_, float:1.0)
                    r0.alpha = r6
                    float[] r6 = new float[r5]
                    r0.hsvTemp = r6
                    android.view.animation.DecelerateInterpolator r6 = new android.view.animation.DecelerateInterpolator
                    r6.<init>()
                    r0.decelerateInterpolator = r6
                    r6 = 0
                    r0.setWillNotDraw(r6)
                    android.graphics.Paint r7 = new android.graphics.Paint
                    r8 = 1
                    r7.<init>(r8)
                    r0.circlePaint = r7
                    android.content.res.Resources r7 = r21.getResources()
                    r9 = 2131165570(0x7var_, float:1.794536E38)
                    android.graphics.drawable.Drawable r7 = r7.getDrawable(r9)
                    android.graphics.drawable.Drawable r7 = r7.mutate()
                    r0.circleDrawable = r7
                    android.graphics.Paint r7 = new android.graphics.Paint
                    r7.<init>()
                    r0.colorWheelPaint = r7
                    r7.setAntiAlias(r8)
                    android.graphics.Paint r7 = r0.colorWheelPaint
                    r7.setDither(r8)
                    android.graphics.Paint r7 = new android.graphics.Paint
                    r7.<init>()
                    r0.valueSliderPaint = r7
                    r7.setAntiAlias(r8)
                    android.graphics.Paint r7 = r0.valueSliderPaint
                    r7.setDither(r8)
                    android.widget.LinearLayout r7 = new android.widget.LinearLayout
                    r7.<init>(r2)
                    r0.linearLayout = r7
                    r7.setOrientation(r6)
                    android.widget.LinearLayout r7 = r0.linearLayout
                    r9 = -2
                    r10 = 49
                    android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r9, (int) r9, (int) r10)
                    r0.addView(r7, r9)
                    r7 = 0
                L_0x0081:
                    if (r7 >= r4) goto L_0x0183
                    org.telegram.ui.Components.EditTextBoldCursor[] r9 = r0.colorEditText
                    org.telegram.ui.Components.EditTextBoldCursor r10 = new org.telegram.ui.Components.EditTextBoldCursor
                    r10.<init>(r2)
                    r9[r7] = r10
                    org.telegram.ui.Components.EditTextBoldCursor[] r9 = r0.colorEditText
                    r9 = r9[r7]
                    r10 = 2
                    r9.setInputType(r10)
                    org.telegram.ui.Components.EditTextBoldCursor[] r9 = r0.colorEditText
                    r9 = r9[r7]
                    r11 = -14606047(0xfffffffffvar_, float:-2.1417772E38)
                    r9.setTextColor(r11)
                    org.telegram.ui.Components.EditTextBoldCursor[] r9 = r0.colorEditText
                    r9 = r9[r7]
                    r9.setCursorColor(r11)
                    org.telegram.ui.Components.EditTextBoldCursor[] r9 = r0.colorEditText
                    r9 = r9[r7]
                    int r11 = org.telegram.messenger.AndroidUtilities.dp(r3)
                    r9.setCursorSize(r11)
                    org.telegram.ui.Components.EditTextBoldCursor[] r9 = r0.colorEditText
                    r9 = r9[r7]
                    r11 = 1069547520(0x3fCLASSNAME, float:1.5)
                    r9.setCursorWidth(r11)
                    org.telegram.ui.Components.EditTextBoldCursor[] r9 = r0.colorEditText
                    r9 = r9[r7]
                    r11 = 1099956224(0x41900000, float:18.0)
                    r9.setTextSize(r8, r11)
                    org.telegram.ui.Components.EditTextBoldCursor[] r9 = r0.colorEditText
                    r9 = r9[r7]
                    r11 = 0
                    r9.setBackground(r11)
                    org.telegram.ui.Components.EditTextBoldCursor[] r9 = r0.colorEditText
                    r9 = r9[r7]
                    java.lang.String r11 = "dialogInputField"
                    int r11 = org.telegram.ui.ActionBar.Theme.getColor(r11)
                    java.lang.String r12 = "dialogInputFieldActivated"
                    int r12 = org.telegram.ui.ActionBar.Theme.getColor(r12)
                    java.lang.String r13 = "dialogTextRed2"
                    int r13 = org.telegram.ui.ActionBar.Theme.getColor(r13)
                    r9.setLineColors(r11, r12, r13)
                    org.telegram.ui.Components.EditTextBoldCursor[] r9 = r0.colorEditText
                    r9 = r9[r7]
                    r9.setMaxLines(r8)
                    org.telegram.ui.Components.EditTextBoldCursor[] r9 = r0.colorEditText
                    r9 = r9[r7]
                    java.lang.Integer r11 = java.lang.Integer.valueOf(r7)
                    r9.setTag(r11)
                    org.telegram.ui.Components.EditTextBoldCursor[] r9 = r0.colorEditText
                    r9 = r9[r7]
                    r11 = 17
                    r9.setGravity(r11)
                    if (r7 != 0) goto L_0x010a
                    org.telegram.ui.Components.EditTextBoldCursor[] r9 = r0.colorEditText
                    r9 = r9[r7]
                    java.lang.String r10 = "red"
                    r9.setHint(r10)
                    goto L_0x012d
                L_0x010a:
                    if (r7 != r8) goto L_0x0116
                    org.telegram.ui.Components.EditTextBoldCursor[] r9 = r0.colorEditText
                    r9 = r9[r7]
                    java.lang.String r10 = "green"
                    r9.setHint(r10)
                    goto L_0x012d
                L_0x0116:
                    if (r7 != r10) goto L_0x0122
                    org.telegram.ui.Components.EditTextBoldCursor[] r9 = r0.colorEditText
                    r9 = r9[r7]
                    java.lang.String r10 = "blue"
                    r9.setHint(r10)
                    goto L_0x012d
                L_0x0122:
                    if (r7 != r5) goto L_0x012d
                    org.telegram.ui.Components.EditTextBoldCursor[] r9 = r0.colorEditText
                    r9 = r9[r7]
                    java.lang.String r10 = "alpha"
                    r9.setHint(r10)
                L_0x012d:
                    org.telegram.ui.Components.EditTextBoldCursor[] r9 = r0.colorEditText
                    r9 = r9[r7]
                    if (r7 != r5) goto L_0x0135
                    r10 = 6
                    goto L_0x0136
                L_0x0135:
                    r10 = 5
                L_0x0136:
                    r11 = 268435456(0x10000000, float:2.5243549E-29)
                    r10 = r10 | r11
                    r9.setImeOptions(r10)
                    android.text.InputFilter[] r9 = new android.text.InputFilter[r8]
                    android.text.InputFilter$LengthFilter r10 = new android.text.InputFilter$LengthFilter
                    r10.<init>(r5)
                    r9[r6] = r10
                    org.telegram.ui.Components.EditTextBoldCursor[] r10 = r0.colorEditText
                    r10 = r10[r7]
                    r10.setFilters(r9)
                    r10 = r7
                    android.widget.LinearLayout r11 = r0.linearLayout
                    org.telegram.ui.Components.EditTextBoldCursor[] r12 = r0.colorEditText
                    r12 = r12[r7]
                    r13 = 55
                    r14 = 36
                    r15 = 0
                    r16 = 0
                    if (r7 == r5) goto L_0x015f
                    r17 = 1098907648(0x41800000, float:16.0)
                    goto L_0x0161
                L_0x015f:
                    r17 = 0
                L_0x0161:
                    r18 = 0
                    android.widget.LinearLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createLinear(r13, r14, r15, r16, r17, r18)
                    r11.addView(r12, r13)
                    org.telegram.ui.Components.EditTextBoldCursor[] r11 = r0.colorEditText
                    r11 = r11[r7]
                    org.telegram.ui.Components.ThemeEditorView$EditorAlert$ColorPicker$1 r12 = new org.telegram.ui.Components.ThemeEditorView$EditorAlert$ColorPicker$1
                    r12.<init>(r1, r10)
                    r11.addTextChangedListener(r12)
                    org.telegram.ui.Components.EditTextBoldCursor[] r11 = r0.colorEditText
                    r11 = r11[r7]
                    org.telegram.ui.Components.ThemeEditorView$EditorAlert$ColorPicker$$ExternalSyntheticLambda0 r12 = org.telegram.ui.Components.ThemeEditorView$EditorAlert$ColorPicker$$ExternalSyntheticLambda0.INSTANCE
                    r11.setOnEditorActionListener(r12)
                    int r7 = r7 + 1
                    goto L_0x0081
                L_0x0183:
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ThemeEditorView.EditorAlert.ColorPicker.<init>(org.telegram.ui.Components.ThemeEditorView$EditorAlert, android.content.Context):void");
            }

            static /* synthetic */ boolean lambda$new$0(TextView textView, int i, KeyEvent keyEvent) {
                if (i != 6) {
                    return false;
                }
                AndroidUtilities.hideKeyboard(textView);
                return true;
            }

            /* access modifiers changed from: protected */
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int size = Math.min(View.MeasureSpec.getSize(widthMeasureSpec), View.MeasureSpec.getSize(heightMeasureSpec));
                measureChild(this.linearLayout, widthMeasureSpec, heightMeasureSpec);
                setMeasuredDimension(size, size);
            }

            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                Canvas canvas2 = canvas;
                int centerX = (getWidth() / 2) - (this.paramValueSliderWidth * 2);
                int centerY = (getHeight() / 2) - AndroidUtilities.dp(8.0f);
                Bitmap bitmap = this.colorWheelBitmap;
                int i = this.colorWheelRadius;
                canvas2.drawBitmap(bitmap, (float) (centerX - i), (float) (centerY - i), (Paint) null);
                float hueAngle = (float) Math.toRadians((double) this.colorHSV[0]);
                double d = (double) this.colorHSV[1];
                Double.isNaN(d);
                double d2 = (-Math.cos((double) hueAngle)) * d;
                double d3 = (double) this.colorWheelRadius;
                Double.isNaN(d3);
                int colorPointX = ((int) (d2 * d3)) + centerX;
                float[] fArr = this.colorHSV;
                double d4 = (double) fArr[1];
                Double.isNaN(d4);
                double d5 = (-Math.sin((double) hueAngle)) * d4;
                int i2 = this.colorWheelRadius;
                double d6 = (double) i2;
                Double.isNaN(d6);
                int colorPointY = ((int) (d5 * d6)) + centerY;
                float f = ((float) i2) * 0.075f;
                float[] fArr2 = this.hsvTemp;
                fArr2[0] = fArr[0];
                fArr2[1] = fArr[1];
                fArr2[2] = 1.0f;
                drawPointerArrow(canvas2, colorPointX, colorPointY, Color.HSVToColor(fArr2));
                int i3 = this.colorWheelRadius;
                int x = centerX + i3 + this.paramValueSliderWidth;
                int y = centerY - i3;
                int width = AndroidUtilities.dp(9.0f);
                int height = this.colorWheelRadius * 2;
                if (this.colorGradient == null) {
                    int i4 = centerX;
                    this.colorGradient = new LinearGradient((float) x, (float) y, (float) (x + width), (float) (y + height), new int[]{-16777216, Color.HSVToColor(this.hsvTemp)}, (float[]) null, Shader.TileMode.CLAMP);
                }
                this.valueSliderPaint.setShader(this.colorGradient);
                float f2 = (float) y;
                float f3 = (float) (y + height);
                int height2 = height;
                float f4 = (float) (x + width);
                int y2 = y;
                float f5 = f3;
                int x2 = x;
                canvas.drawRect((float) x, f2, f4, f5, this.valueSliderPaint);
                float[] fArr3 = this.colorHSV;
                drawPointerArrow(canvas2, x2 + (width / 2), (int) (((float) y2) + (fArr3[2] * ((float) height2))), Color.HSVToColor(fArr3));
                int x3 = x2 + (this.paramValueSliderWidth * 2);
                if (this.alphaGradient == null) {
                    int color = Color.HSVToColor(this.hsvTemp);
                    this.alphaGradient = new LinearGradient((float) x3, (float) y2, (float) (x3 + width), (float) (y2 + height2), new int[]{color, color & 16777215}, (float[]) null, Shader.TileMode.CLAMP);
                }
                this.valueSliderPaint.setShader(this.alphaGradient);
                canvas.drawRect((float) x3, (float) y2, (float) (x3 + width), (float) (y2 + height2), this.valueSliderPaint);
                drawPointerArrow(canvas2, (width / 2) + x3, (int) (((float) y2) + ((1.0f - this.alpha) * ((float) height2))), (Color.HSVToColor(this.colorHSV) & 16777215) | (((int) (this.alpha * 255.0f)) << 24));
            }

            private void drawPointerArrow(Canvas canvas, int x, int y, int color) {
                int side = AndroidUtilities.dp(13.0f);
                this.circleDrawable.setBounds(x - side, y - side, x + side, y + side);
                this.circleDrawable.draw(canvas);
                this.circlePaint.setColor(-1);
                canvas.drawCircle((float) x, (float) y, (float) AndroidUtilities.dp(11.0f), this.circlePaint);
                this.circlePaint.setColor(color);
                canvas.drawCircle((float) x, (float) y, (float) AndroidUtilities.dp(9.0f), this.circlePaint);
            }

            /* access modifiers changed from: protected */
            public void onSizeChanged(int width, int height, int oldw, int oldh) {
                int max = Math.max(1, ((width / 2) - (this.paramValueSliderWidth * 2)) - AndroidUtilities.dp(20.0f));
                this.colorWheelRadius = max;
                this.colorWheelBitmap = createColorWheelBitmap(max * 2, max * 2);
                this.colorGradient = null;
                this.alphaGradient = null;
            }

            private Bitmap createColorWheelBitmap(int width, int height) {
                int i = width;
                int i2 = height;
                Bitmap bitmap = Bitmap.createBitmap(i, i2, Bitmap.Config.ARGB_8888);
                int[] colors = new int[(12 + 1)];
                float[] hsv = {0.0f, 1.0f, 1.0f};
                for (int i3 = 0; i3 < colors.length; i3++) {
                    hsv[0] = (float) (((i3 * 30) + 180) % 360);
                    colors[i3] = Color.HSVToColor(hsv);
                }
                colors[12] = colors[0];
                this.colorWheelPaint.setShader(new ComposeShader(new SweepGradient((float) (i / 2), (float) (i2 / 2), colors, (float[]) null), new RadialGradient((float) (i / 2), (float) (i2 / 2), (float) this.colorWheelRadius, -1, 16777215, Shader.TileMode.CLAMP), PorterDuff.Mode.SRC_OVER));
                new Canvas(bitmap).drawCircle((float) (i / 2), (float) (i2 / 2), (float) this.colorWheelRadius, this.colorWheelPaint);
                return bitmap;
            }

            private void startColorChange(boolean start) {
                if (this.this$1.startedColorChange != start) {
                    if (this.this$1.colorChangeAnimation != null) {
                        this.this$1.colorChangeAnimation.cancel();
                    }
                    boolean unused = this.this$1.startedColorChange = start;
                    AnimatorSet unused2 = this.this$1.colorChangeAnimation = new AnimatorSet();
                    AnimatorSet access$1300 = this.this$1.colorChangeAnimation;
                    Animator[] animatorArr = new Animator[2];
                    ColorDrawable access$1400 = this.this$1.backDrawable;
                    Property<ColorDrawable, Integer> property = AnimationProperties.COLOR_DRAWABLE_ALPHA;
                    int[] iArr = new int[1];
                    iArr[0] = start ? 0 : 51;
                    animatorArr[0] = ObjectAnimator.ofInt(access$1400, property, iArr);
                    ViewGroup access$1500 = this.this$1.containerView;
                    Property property2 = View.ALPHA;
                    float[] fArr = new float[1];
                    fArr[0] = start ? 0.2f : 1.0f;
                    animatorArr[1] = ObjectAnimator.ofFloat(access$1500, property2, fArr);
                    access$1300.playTogether(animatorArr);
                    this.this$1.colorChangeAnimation.setDuration(150);
                    this.this$1.colorChangeAnimation.setInterpolator(this.decelerateInterpolator);
                    this.this$1.colorChangeAnimation.start();
                }
            }

            /* JADX WARNING: Code restructure failed: missing block: B:11:0x005e, code lost:
                if (r10 <= ((double) r0.colorWheelRadius)) goto L_0x0068;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:32:0x00cf, code lost:
                if (r4 <= (r17 + r2)) goto L_0x00d7;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:55:0x011e, code lost:
                if (r4 <= (r17 + r2)) goto L_0x0120;
             */
            /* JADX WARNING: Removed duplicated region for block: B:21:0x00ad  */
            /* JADX WARNING: Removed duplicated region for block: B:34:0x00d5  */
            /* JADX WARNING: Removed duplicated region for block: B:37:0x00e7  */
            /* JADX WARNING: Removed duplicated region for block: B:38:0x00e9  */
            /* JADX WARNING: Removed duplicated region for block: B:44:0x00fd  */
            /* JADX WARNING: Removed duplicated region for block: B:58:0x0136  */
            /* JADX WARNING: Removed duplicated region for block: B:59:0x0139  */
            /* JADX WARNING: Removed duplicated region for block: B:69:0x014e  */
            /* JADX WARNING: Removed duplicated region for block: B:70:0x0152  */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public boolean onTouchEvent(android.view.MotionEvent r23) {
                /*
                    r22 = this;
                    r0 = r22
                    int r1 = r23.getAction()
                    r2 = 0
                    switch(r1) {
                        case 0: goto L_0x001b;
                        case 1: goto L_0x000e;
                        case 2: goto L_0x001b;
                        default: goto L_0x000a;
                    }
                L_0x000a:
                    r21 = r1
                    goto L_0x0265
                L_0x000e:
                    r0.alphaPressed = r2
                    r0.colorPressed = r2
                    r0.circlePressed = r2
                    r0.startColorChange(r2)
                    r21 = r1
                    goto L_0x0265
                L_0x001b:
                    float r3 = r23.getX()
                    int r3 = (int) r3
                    float r4 = r23.getY()
                    int r4 = (int) r4
                    int r5 = r22.getWidth()
                    r6 = 2
                    int r5 = r5 / r6
                    int r7 = r0.paramValueSliderWidth
                    int r7 = r7 * 2
                    int r5 = r5 - r7
                    int r7 = r22.getHeight()
                    int r7 = r7 / r6
                    r8 = 1090519040(0x41000000, float:8.0)
                    int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
                    int r7 = r7 - r8
                    int r8 = r3 - r5
                    int r9 = r4 - r7
                    int r10 = r8 * r8
                    int r11 = r9 * r9
                    int r10 = r10 + r11
                    double r10 = (double) r10
                    double r10 = java.lang.Math.sqrt(r10)
                    boolean r12 = r0.circlePressed
                    r15 = 1
                    if (r12 != 0) goto L_0x0066
                    boolean r12 = r0.alphaPressed
                    if (r12 != 0) goto L_0x0061
                    boolean r12 = r0.colorPressed
                    if (r12 != 0) goto L_0x0061
                    int r12 = r0.colorWheelRadius
                    r17 = r7
                    double r6 = (double) r12
                    int r12 = (r10 > r6 ? 1 : (r10 == r6 ? 0 : -1))
                    if (r12 > 0) goto L_0x0063
                    goto L_0x0068
                L_0x0061:
                    r17 = r7
                L_0x0063:
                    r19 = r3
                    goto L_0x00a7
                L_0x0066:
                    r17 = r7
                L_0x0068:
                    int r6 = r0.colorWheelRadius
                    double r13 = (double) r6
                    int r18 = (r10 > r13 ? 1 : (r10 == r13 ? 0 : -1))
                    if (r18 <= 0) goto L_0x0070
                    double r10 = (double) r6
                L_0x0070:
                    r0.circlePressed = r15
                    float[] r6 = r0.colorHSV
                    double r13 = (double) r9
                    r19 = r3
                    double r2 = (double) r8
                    double r2 = java.lang.Math.atan2(r13, r2)
                    double r2 = java.lang.Math.toDegrees(r2)
                    r13 = 4640537203540230144(0xNUM, double:180.0)
                    double r2 = r2 + r13
                    float r2 = (float) r2
                    r3 = 0
                    r6[r3] = r2
                    float[] r2 = r0.colorHSV
                    int r3 = r0.colorWheelRadius
                    double r13 = (double) r3
                    java.lang.Double.isNaN(r13)
                    double r13 = r10 / r13
                    float r3 = (float) r13
                    r6 = 1065353216(0x3var_, float:1.0)
                    float r3 = java.lang.Math.min(r6, r3)
                    r6 = 0
                    float r3 = java.lang.Math.max(r6, r3)
                    r2[r15] = r3
                    r2 = 0
                    r0.colorGradient = r2
                    r0.alphaGradient = r2
                L_0x00a7:
                    boolean r2 = r0.colorPressed
                    r3 = 1073741824(0x40000000, float:2.0)
                    if (r2 != 0) goto L_0x00d5
                    boolean r2 = r0.circlePressed
                    if (r2 != 0) goto L_0x00d2
                    boolean r2 = r0.alphaPressed
                    if (r2 != 0) goto L_0x00d2
                    int r2 = r0.colorWheelRadius
                    int r6 = r5 + r2
                    int r13 = r0.paramValueSliderWidth
                    int r6 = r6 + r13
                    r14 = r19
                    if (r14 < r6) goto L_0x00f8
                    int r6 = r5 + r2
                    r16 = 2
                    int r13 = r13 * 2
                    int r6 = r6 + r13
                    if (r14 > r6) goto L_0x00f8
                    int r6 = r17 - r2
                    if (r4 < r6) goto L_0x00f8
                    int r2 = r17 + r2
                    if (r4 > r2) goto L_0x00f8
                    goto L_0x00d7
                L_0x00d2:
                    r14 = r19
                    goto L_0x00f8
                L_0x00d5:
                    r14 = r19
                L_0x00d7:
                    int r2 = r0.colorWheelRadius
                    int r6 = r17 - r2
                    int r6 = r4 - r6
                    float r6 = (float) r6
                    float r2 = (float) r2
                    float r2 = r2 * r3
                    float r6 = r6 / r2
                    r2 = 0
                    int r13 = (r6 > r2 ? 1 : (r6 == r2 ? 0 : -1))
                    if (r13 >= 0) goto L_0x00e9
                    r6 = 0
                    goto L_0x00f1
                L_0x00e9:
                    r2 = 1065353216(0x3var_, float:1.0)
                    int r13 = (r6 > r2 ? 1 : (r6 == r2 ? 0 : -1))
                    if (r13 <= 0) goto L_0x00f1
                    r6 = 1065353216(0x3var_, float:1.0)
                L_0x00f1:
                    float[] r2 = r0.colorHSV
                    r13 = 2
                    r2[r13] = r6
                    r0.colorPressed = r15
                L_0x00f8:
                    boolean r2 = r0.alphaPressed
                    r6 = 4
                    if (r2 != 0) goto L_0x0120
                    boolean r2 = r0.circlePressed
                    if (r2 != 0) goto L_0x0141
                    boolean r2 = r0.colorPressed
                    if (r2 != 0) goto L_0x0141
                    int r2 = r0.colorWheelRadius
                    int r13 = r5 + r2
                    int r7 = r0.paramValueSliderWidth
                    int r20 = r7 * 3
                    int r13 = r13 + r20
                    if (r14 < r13) goto L_0x0141
                    int r13 = r5 + r2
                    int r7 = r7 * 4
                    int r13 = r13 + r7
                    if (r14 > r13) goto L_0x0141
                    int r7 = r17 - r2
                    if (r4 < r7) goto L_0x0141
                    int r7 = r17 + r2
                    if (r4 > r7) goto L_0x0141
                L_0x0120:
                    int r2 = r0.colorWheelRadius
                    int r7 = r17 - r2
                    int r7 = r4 - r7
                    float r7 = (float) r7
                    float r2 = (float) r2
                    float r2 = r2 * r3
                    float r7 = r7 / r2
                    r2 = 1065353216(0x3var_, float:1.0)
                    float r3 = r2 - r7
                    r0.alpha = r3
                    r7 = 0
                    int r12 = (r3 > r7 ? 1 : (r3 == r7 ? 0 : -1))
                    if (r12 >= 0) goto L_0x0139
                    r0.alpha = r7
                    goto L_0x013f
                L_0x0139:
                    int r3 = (r3 > r2 ? 1 : (r3 == r2 ? 0 : -1))
                    if (r3 <= 0) goto L_0x013f
                    r0.alpha = r2
                L_0x013f:
                    r0.alphaPressed = r15
                L_0x0141:
                    boolean r2 = r0.alphaPressed
                    if (r2 != 0) goto L_0x0152
                    boolean r2 = r0.colorPressed
                    if (r2 != 0) goto L_0x0152
                    boolean r2 = r0.circlePressed
                    if (r2 == 0) goto L_0x014e
                    goto L_0x0152
                L_0x014e:
                    r21 = r1
                    goto L_0x0263
                L_0x0152:
                    r0.startColorChange(r15)
                    int r2 = r22.getColor()
                    r3 = 0
                L_0x015a:
                    org.telegram.ui.Components.ThemeEditorView$EditorAlert r7 = r0.this$1
                    org.telegram.ui.Components.ThemeEditorView r7 = r7.this$0
                    java.util.ArrayList r7 = r7.currentThemeDesription
                    int r7 = r7.size()
                    if (r3 >= r7) goto L_0x01c5
                    org.telegram.ui.Components.ThemeEditorView$EditorAlert r7 = r0.this$1
                    org.telegram.ui.Components.ThemeEditorView r7 = r7.this$0
                    java.util.ArrayList r7 = r7.currentThemeDesription
                    java.lang.Object r7 = r7.get(r3)
                    org.telegram.ui.ActionBar.ThemeDescription r7 = (org.telegram.ui.ActionBar.ThemeDescription) r7
                    java.lang.String r12 = r7.getCurrentKey()
                    if (r3 != 0) goto L_0x0184
                    java.lang.String r13 = "chat_wallpaper"
                    boolean r13 = r12.equals(r13)
                    if (r13 != 0) goto L_0x01ac
                L_0x0184:
                    java.lang.String r13 = "chat_wallpaper_gradient_to"
                    boolean r13 = r12.equals(r13)
                    if (r13 != 0) goto L_0x01ac
                    java.lang.String r13 = "key_chat_wallpaper_gradient_to2"
                    boolean r13 = r12.equals(r13)
                    if (r13 != 0) goto L_0x01ac
                    java.lang.String r13 = "key_chat_wallpaper_gradient_to3"
                    boolean r13 = r12.equals(r13)
                    if (r13 != 0) goto L_0x01ac
                    java.lang.String r13 = "windowBackgroundWhite"
                    boolean r13 = r12.equals(r13)
                    if (r13 != 0) goto L_0x01ac
                    java.lang.String r13 = "windowBackgroundGray"
                    boolean r13 = r12.equals(r13)
                    if (r13 == 0) goto L_0x01af
                L_0x01ac:
                    r13 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
                    r2 = r2 | r13
                L_0x01af:
                    org.telegram.ui.Components.ThemeEditorView$EditorAlert r13 = r0.this$1
                    org.telegram.ui.Components.ThemeEditorView r13 = r13.this$0
                    java.util.ArrayList r13 = r13.currentThemeDesription
                    java.lang.Object r13 = r13.get(r3)
                    org.telegram.ui.ActionBar.ThemeDescription r13 = (org.telegram.ui.ActionBar.ThemeDescription) r13
                    r6 = 0
                    r13.setColor(r2, r6)
                    int r3 = r3 + 1
                    r6 = 4
                    goto L_0x015a
                L_0x01c5:
                    int r3 = android.graphics.Color.red(r2)
                    int r6 = android.graphics.Color.green(r2)
                    int r7 = android.graphics.Color.blue(r2)
                    int r12 = android.graphics.Color.alpha(r2)
                    org.telegram.ui.Components.ThemeEditorView$EditorAlert r13 = r0.this$1
                    boolean r13 = r13.ignoreTextChange
                    if (r13 != 0) goto L_0x025e
                    org.telegram.ui.Components.ThemeEditorView$EditorAlert r13 = r0.this$1
                    boolean unused = r13.ignoreTextChange = r15
                    org.telegram.ui.Components.EditTextBoldCursor[] r13 = r0.colorEditText
                    r18 = 0
                    r13 = r13[r18]
                    java.lang.StringBuilder r15 = new java.lang.StringBuilder
                    r15.<init>()
                    r21 = r1
                    java.lang.String r1 = ""
                    r15.append(r1)
                    r15.append(r3)
                    java.lang.String r15 = r15.toString()
                    r13.setText(r15)
                    org.telegram.ui.Components.EditTextBoldCursor[] r13 = r0.colorEditText
                    r15 = 1
                    r13 = r13[r15]
                    java.lang.StringBuilder r15 = new java.lang.StringBuilder
                    r15.<init>()
                    r15.append(r1)
                    r15.append(r6)
                    java.lang.String r15 = r15.toString()
                    r13.setText(r15)
                    org.telegram.ui.Components.EditTextBoldCursor[] r13 = r0.colorEditText
                    r15 = 2
                    r13 = r13[r15]
                    java.lang.StringBuilder r15 = new java.lang.StringBuilder
                    r15.<init>()
                    r15.append(r1)
                    r15.append(r7)
                    java.lang.String r15 = r15.toString()
                    r13.setText(r15)
                    org.telegram.ui.Components.EditTextBoldCursor[] r13 = r0.colorEditText
                    r15 = 3
                    r13 = r13[r15]
                    java.lang.StringBuilder r15 = new java.lang.StringBuilder
                    r15.<init>()
                    r15.append(r1)
                    r15.append(r12)
                    java.lang.String r1 = r15.toString()
                    r13.setText(r1)
                    r1 = 0
                L_0x0244:
                    r13 = 4
                    if (r1 >= r13) goto L_0x0257
                    org.telegram.ui.Components.EditTextBoldCursor[] r15 = r0.colorEditText
                    r13 = r15[r1]
                    r15 = r15[r1]
                    int r15 = r15.length()
                    r13.setSelection(r15)
                    int r1 = r1 + 1
                    goto L_0x0244
                L_0x0257:
                    org.telegram.ui.Components.ThemeEditorView$EditorAlert r1 = r0.this$1
                    r13 = 0
                    boolean unused = r1.ignoreTextChange = r13
                    goto L_0x0260
                L_0x025e:
                    r21 = r1
                L_0x0260:
                    r22.invalidate()
                L_0x0263:
                    r1 = 1
                    return r1
                L_0x0265:
                    boolean r1 = super.onTouchEvent(r23)
                    return r1
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ThemeEditorView.EditorAlert.ColorPicker.onTouchEvent(android.view.MotionEvent):boolean");
            }

            public void setColor(int color) {
                int red = Color.red(color);
                int green = Color.green(color);
                int blue = Color.blue(color);
                int a = Color.alpha(color);
                if (!this.this$1.ignoreTextChange) {
                    boolean unused = this.this$1.ignoreTextChange = true;
                    EditTextBoldCursor editTextBoldCursor = this.colorEditText[0];
                    editTextBoldCursor.setText("" + red);
                    EditTextBoldCursor editTextBoldCursor2 = this.colorEditText[1];
                    editTextBoldCursor2.setText("" + green);
                    EditTextBoldCursor editTextBoldCursor3 = this.colorEditText[2];
                    editTextBoldCursor3.setText("" + blue);
                    EditTextBoldCursor editTextBoldCursor4 = this.colorEditText[3];
                    editTextBoldCursor4.setText("" + a);
                    for (int b = 0; b < 4; b++) {
                        EditTextBoldCursor[] editTextBoldCursorArr = this.colorEditText;
                        editTextBoldCursorArr[b].setSelection(editTextBoldCursorArr[b].length());
                    }
                    boolean unused2 = this.this$1.ignoreTextChange = false;
                }
                this.alphaGradient = null;
                this.colorGradient = null;
                this.alpha = ((float) a) / 255.0f;
                Color.colorToHSV(color, this.colorHSV);
                invalidate();
            }

            public int getColor() {
                return (Color.HSVToColor(this.colorHSV) & 16777215) | (((int) (this.alpha * 255.0f)) << 24);
            }
        }

        /* JADX WARNING: Illegal instructions before constructor call */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public EditorAlert(org.telegram.ui.Components.ThemeEditorView r19, android.content.Context r20, java.util.ArrayList<org.telegram.ui.ActionBar.ThemeDescription> r21) {
            /*
                r18 = this;
                r0 = r18
                r1 = r19
                r2 = r20
                r0.this$0 = r1
                r3 = 1
                r0.<init>(r2, r3)
                r4 = 2
                android.view.View[] r5 = new android.view.View[r4]
                r0.shadow = r5
                android.animation.AnimatorSet[] r4 = new android.animation.AnimatorSet[r4]
                r0.shadowAnimation = r4
                android.content.res.Resources r4 = r20.getResources()
                r5 = 2131166138(0x7var_ba, float:1.7946513E38)
                android.graphics.drawable.Drawable r4 = r4.getDrawable(r5)
                android.graphics.drawable.Drawable r4 = r4.mutate()
                r0.shadowDrawable = r4
                org.telegram.ui.Components.ThemeEditorView$EditorAlert$1 r4 = new org.telegram.ui.Components.ThemeEditorView$EditorAlert$1
                r4.<init>(r2, r1)
                r0.containerView = r4
                android.view.ViewGroup r4 = r0.containerView
                r5 = 0
                r4.setWillNotDraw(r5)
                android.view.ViewGroup r4 = r0.containerView
                int r6 = r0.backgroundPaddingLeft
                int r7 = r0.backgroundPaddingLeft
                r4.setPadding(r6, r5, r7, r5)
                android.widget.FrameLayout r4 = new android.widget.FrameLayout
                r4.<init>(r2)
                r0.frameLayout = r4
                r6 = -1
                r4.setBackgroundColor(r6)
                org.telegram.ui.Components.ThemeEditorView$EditorAlert$SearchField r4 = new org.telegram.ui.Components.ThemeEditorView$EditorAlert$SearchField
                r4.<init>(r2)
                r0.searchField = r4
                android.widget.FrameLayout r7 = r0.frameLayout
                r8 = 51
                android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r6, (int) r6, (int) r8)
                r7.addView(r4, r9)
                org.telegram.ui.Components.ThemeEditorView$EditorAlert$2 r4 = new org.telegram.ui.Components.ThemeEditorView$EditorAlert$2
                r4.<init>(r2, r1)
                r0.listView = r4
                r7 = 251658240(0xvar_, float:6.3108872E-30)
                r4.setSelectorDrawableColor(r7)
                org.telegram.ui.Components.RecyclerListView r4 = r0.listView
                r7 = 1111490560(0x42400000, float:48.0)
                int r9 = org.telegram.messenger.AndroidUtilities.dp(r7)
                r4.setPadding(r5, r5, r5, r9)
                org.telegram.ui.Components.RecyclerListView r4 = r0.listView
                r4.setClipToPadding(r5)
                org.telegram.ui.Components.RecyclerListView r4 = r0.listView
                androidx.recyclerview.widget.LinearLayoutManager r9 = new androidx.recyclerview.widget.LinearLayoutManager
                android.content.Context r10 = r18.getContext()
                r9.<init>(r10)
                r0.layoutManager = r9
                r4.setLayoutManager(r9)
                org.telegram.ui.Components.RecyclerListView r4 = r0.listView
                r4.setHorizontalScrollBarEnabled(r5)
                org.telegram.ui.Components.RecyclerListView r4 = r0.listView
                r4.setVerticalScrollBarEnabled(r5)
                android.view.ViewGroup r4 = r0.containerView
                org.telegram.ui.Components.RecyclerListView r9 = r0.listView
                android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r6, (int) r6, (int) r8)
                r4.addView(r9, r10)
                org.telegram.ui.Components.RecyclerListView r4 = r0.listView
                org.telegram.ui.Components.ThemeEditorView$EditorAlert$ListAdapter r9 = new org.telegram.ui.Components.ThemeEditorView$EditorAlert$ListAdapter
                r10 = r21
                r9.<init>(r2, r10)
                r0.listAdapter = r9
                r4.setAdapter(r9)
                org.telegram.ui.Components.ThemeEditorView$EditorAlert$SearchAdapter r4 = new org.telegram.ui.Components.ThemeEditorView$EditorAlert$SearchAdapter
                r4.<init>(r2)
                r0.searchAdapter = r4
                org.telegram.ui.Components.RecyclerListView r4 = r0.listView
                r9 = -657673(0xfffffffffff5f6f7, float:NaN)
                r4.setGlowColor(r9)
                org.telegram.ui.Components.RecyclerListView r4 = r0.listView
                r9 = 0
                r4.setItemAnimator(r9)
                org.telegram.ui.Components.RecyclerListView r4 = r0.listView
                r4.setLayoutAnimation(r9)
                org.telegram.ui.Components.RecyclerListView r4 = r0.listView
                org.telegram.ui.Components.ThemeEditorView$EditorAlert$$ExternalSyntheticLambda5 r9 = new org.telegram.ui.Components.ThemeEditorView$EditorAlert$$ExternalSyntheticLambda5
                r9.<init>(r0)
                r4.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListener) r9)
                org.telegram.ui.Components.RecyclerListView r4 = r0.listView
                org.telegram.ui.Components.ThemeEditorView$EditorAlert$3 r9 = new org.telegram.ui.Components.ThemeEditorView$EditorAlert$3
                r9.<init>(r1)
                r4.setOnScrollListener(r9)
                org.telegram.ui.Components.EmptyTextProgressView r4 = new org.telegram.ui.Components.EmptyTextProgressView
                r4.<init>(r2)
                r0.searchEmptyView = r4
                r4.setShowAtCenter(r3)
                org.telegram.ui.Components.EmptyTextProgressView r4 = r0.searchEmptyView
                r4.showTextView()
                org.telegram.ui.Components.EmptyTextProgressView r4 = r0.searchEmptyView
                java.lang.String r9 = "NoResult"
                r11 = 2131626858(0x7f0e0b6a, float:1.8880964E38)
                java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r9, r11)
                r4.setText(r9)
                org.telegram.ui.Components.RecyclerListView r4 = r0.listView
                org.telegram.ui.Components.EmptyTextProgressView r9 = r0.searchEmptyView
                r4.setEmptyView(r9)
                android.view.ViewGroup r4 = r0.containerView
                org.telegram.ui.Components.EmptyTextProgressView r9 = r0.searchEmptyView
                r11 = -1
                r12 = -1082130432(0xffffffffbvar_, float:-1.0)
                r13 = 51
                r14 = 0
                r15 = 1112539136(0x42500000, float:52.0)
                r16 = 0
                r17 = 0
                android.widget.FrameLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r12, r13, r14, r15, r16, r17)
                r4.addView(r9, r11)
                android.widget.FrameLayout$LayoutParams r4 = new android.widget.FrameLayout$LayoutParams
                int r9 = org.telegram.messenger.AndroidUtilities.getShadowHeight()
                r4.<init>(r6, r9, r8)
                r9 = 1114112000(0x42680000, float:58.0)
                int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
                r4.topMargin = r9
                android.view.View[] r9 = r0.shadow
                android.view.View r11 = new android.view.View
                r11.<init>(r2)
                r9[r5] = r11
                android.view.View[] r9 = r0.shadow
                r9 = r9[r5]
                r11 = 301989888(0x12000000, float:4.0389678E-28)
                r9.setBackgroundColor(r11)
                android.view.View[] r9 = r0.shadow
                r9 = r9[r5]
                r12 = 0
                r9.setAlpha(r12)
                android.view.View[] r9 = r0.shadow
                r9 = r9[r5]
                java.lang.Integer r12 = java.lang.Integer.valueOf(r3)
                r9.setTag(r12)
                android.view.ViewGroup r9 = r0.containerView
                android.view.View[] r12 = r0.shadow
                r12 = r12[r5]
                r9.addView(r12, r4)
                android.view.ViewGroup r9 = r0.containerView
                android.widget.FrameLayout r12 = r0.frameLayout
                r13 = 58
                android.widget.FrameLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r6, (int) r13, (int) r8)
                r9.addView(r12, r13)
                org.telegram.ui.Components.ThemeEditorView$EditorAlert$ColorPicker r9 = new org.telegram.ui.Components.ThemeEditorView$EditorAlert$ColorPicker
                r9.<init>(r0, r2)
                r0.colorPicker = r9
                r12 = 8
                r9.setVisibility(r12)
                android.view.ViewGroup r9 = r0.containerView
                org.telegram.ui.Components.ThemeEditorView$EditorAlert$ColorPicker r13 = r0.colorPicker
                android.widget.FrameLayout$LayoutParams r14 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r6, (int) r6, (int) r3)
                r9.addView(r13, r14)
                android.widget.FrameLayout$LayoutParams r9 = new android.widget.FrameLayout$LayoutParams
                int r13 = org.telegram.messenger.AndroidUtilities.getShadowHeight()
                r14 = 83
                r9.<init>(r6, r13, r14)
                r4 = r9
                int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
                r4.bottomMargin = r7
                android.view.View[] r7 = r0.shadow
                android.view.View r9 = new android.view.View
                r9.<init>(r2)
                r7[r3] = r9
                android.view.View[] r7 = r0.shadow
                r7 = r7[r3]
                r7.setBackgroundColor(r11)
                android.view.ViewGroup r7 = r0.containerView
                android.view.View[] r9 = r0.shadow
                r9 = r9[r3]
                r7.addView(r9, r4)
                android.widget.FrameLayout r7 = new android.widget.FrameLayout
                r7.<init>(r2)
                r0.bottomSaveLayout = r7
                r7.setBackgroundColor(r6)
                android.view.ViewGroup r7 = r0.containerView
                android.widget.FrameLayout r9 = r0.bottomSaveLayout
                r11 = 48
                android.widget.FrameLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r6, (int) r11, (int) r14)
                r7.addView(r9, r13)
                android.widget.TextView r7 = new android.widget.TextView
                r7.<init>(r2)
                r9 = 1096810496(0x41600000, float:14.0)
                r7.setTextSize(r3, r9)
                r13 = -15095832(0xfffffffffvar_a7e8, float:-2.042437E38)
                r7.setTextColor(r13)
                r15 = 17
                r7.setGravity(r15)
                r11 = 788529152(0x2var_, float:1.1641532E-10)
                android.graphics.drawable.Drawable r14 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r11, r5)
                r7.setBackgroundDrawable(r14)
                r14 = 1099956224(0x41900000, float:18.0)
                int r12 = org.telegram.messenger.AndroidUtilities.dp(r14)
                int r11 = org.telegram.messenger.AndroidUtilities.dp(r14)
                r7.setPadding(r12, r5, r11, r5)
                java.lang.String r11 = "CloseEditor"
                r12 = 2131625168(0x7f0e04d0, float:1.8877536E38)
                java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r11, r12)
                java.lang.String r11 = r11.toUpperCase()
                r7.setText(r11)
                java.lang.String r11 = "fonts/rmedium.ttf"
                android.graphics.Typeface r12 = org.telegram.messenger.AndroidUtilities.getTypeface(r11)
                r7.setTypeface(r12)
                android.widget.FrameLayout r12 = r0.bottomSaveLayout
                r14 = -2
                android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r14, (int) r6, (int) r8)
                r12.addView(r7, r5)
                org.telegram.ui.Components.ThemeEditorView$EditorAlert$$ExternalSyntheticLambda0 r5 = new org.telegram.ui.Components.ThemeEditorView$EditorAlert$$ExternalSyntheticLambda0
                r5.<init>(r0)
                r7.setOnClickListener(r5)
                android.widget.TextView r5 = new android.widget.TextView
                r5.<init>(r2)
                r5.setTextSize(r3, r9)
                r5.setTextColor(r13)
                r5.setGravity(r15)
                r8 = 0
                r12 = 788529152(0x2var_, float:1.1641532E-10)
                android.graphics.drawable.Drawable r15 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r12, r8)
                r5.setBackgroundDrawable(r15)
                r12 = 1099956224(0x41900000, float:18.0)
                int r15 = org.telegram.messenger.AndroidUtilities.dp(r12)
                int r13 = org.telegram.messenger.AndroidUtilities.dp(r12)
                r5.setPadding(r15, r8, r13, r8)
                java.lang.String r8 = "SaveTheme"
                r12 = 2131628066(0x7f0e1022, float:1.8883414E38)
                java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r8, r12)
                java.lang.String r8 = r8.toUpperCase()
                r5.setText(r8)
                android.graphics.Typeface r8 = org.telegram.messenger.AndroidUtilities.getTypeface(r11)
                r5.setTypeface(r8)
                android.widget.FrameLayout r8 = r0.bottomSaveLayout
                r12 = 53
                android.widget.FrameLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r14, (int) r6, (int) r12)
                r8.addView(r5, r13)
                org.telegram.ui.Components.ThemeEditorView$EditorAlert$$ExternalSyntheticLambda1 r8 = new org.telegram.ui.Components.ThemeEditorView$EditorAlert$$ExternalSyntheticLambda1
                r8.<init>(r0)
                r5.setOnClickListener(r8)
                android.widget.FrameLayout r8 = new android.widget.FrameLayout
                r8.<init>(r2)
                r0.bottomLayout = r8
                r13 = 8
                r8.setVisibility(r13)
                android.widget.FrameLayout r8 = r0.bottomLayout
                r8.setBackgroundColor(r6)
                android.view.ViewGroup r8 = r0.containerView
                android.widget.FrameLayout r13 = r0.bottomLayout
                r12 = 83
                r15 = 48
                android.widget.FrameLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r6, (int) r15, (int) r12)
                r8.addView(r13, r12)
                android.widget.TextView r8 = new android.widget.TextView
                r8.<init>(r2)
                r8.setTextSize(r3, r9)
                r12 = -15095832(0xfffffffffvar_a7e8, float:-2.042437E38)
                r8.setTextColor(r12)
                r12 = 17
                r8.setGravity(r12)
                r12 = 788529152(0x2var_, float:1.1641532E-10)
                r13 = 0
                android.graphics.drawable.Drawable r15 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r12, r13)
                r8.setBackgroundDrawable(r15)
                r12 = 1099956224(0x41900000, float:18.0)
                int r15 = org.telegram.messenger.AndroidUtilities.dp(r12)
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r12)
                r8.setPadding(r15, r13, r3, r13)
                java.lang.String r3 = "Cancel"
                r12 = 2131624819(0x7f0e0373, float:1.8876828E38)
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r12)
                java.lang.String r3 = r3.toUpperCase()
                r8.setText(r3)
                android.graphics.Typeface r3 = org.telegram.messenger.AndroidUtilities.getTypeface(r11)
                r8.setTypeface(r3)
                android.widget.FrameLayout r3 = r0.bottomLayout
                r12 = 51
                android.widget.FrameLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r14, (int) r6, (int) r12)
                r3.addView(r8, r13)
                org.telegram.ui.Components.ThemeEditorView$EditorAlert$$ExternalSyntheticLambda2 r3 = new org.telegram.ui.Components.ThemeEditorView$EditorAlert$$ExternalSyntheticLambda2
                r3.<init>(r0)
                r8.setOnClickListener(r3)
                android.widget.LinearLayout r3 = new android.widget.LinearLayout
                r3.<init>(r2)
                r12 = 0
                r3.setOrientation(r12)
                android.widget.FrameLayout r12 = r0.bottomLayout
                r13 = 53
                android.widget.FrameLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r14, (int) r6, (int) r13)
                r12.addView(r3, r13)
                android.widget.TextView r12 = new android.widget.TextView
                r12.<init>(r2)
                r13 = 1
                r12.setTextSize(r13, r9)
                r13 = -15095832(0xfffffffffvar_a7e8, float:-2.042437E38)
                r12.setTextColor(r13)
                r13 = 17
                r12.setGravity(r13)
                r13 = 788529152(0x2var_, float:1.1641532E-10)
                r15 = 0
                android.graphics.drawable.Drawable r9 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r13, r15)
                r12.setBackgroundDrawable(r9)
                r9 = 1099956224(0x41900000, float:18.0)
                int r13 = org.telegram.messenger.AndroidUtilities.dp(r9)
                int r6 = org.telegram.messenger.AndroidUtilities.dp(r9)
                r12.setPadding(r13, r15, r6, r15)
                java.lang.String r6 = "Default"
                r9 = 2131625366(0x7f0e0596, float:1.8877938E38)
                java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r6, r9)
                java.lang.String r6 = r6.toUpperCase()
                r12.setText(r6)
                android.graphics.Typeface r6 = org.telegram.messenger.AndroidUtilities.getTypeface(r11)
                r12.setTypeface(r6)
                r6 = 51
                r9 = -1
                android.widget.FrameLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r14, (int) r9, (int) r6)
                r3.addView(r12, r13)
                org.telegram.ui.Components.ThemeEditorView$EditorAlert$$ExternalSyntheticLambda3 r6 = new org.telegram.ui.Components.ThemeEditorView$EditorAlert$$ExternalSyntheticLambda3
                r6.<init>(r0)
                r12.setOnClickListener(r6)
                android.widget.TextView r6 = new android.widget.TextView
                r6.<init>(r2)
                r5 = r6
                r6 = 1096810496(0x41600000, float:14.0)
                r9 = 1
                r5.setTextSize(r9, r6)
                r6 = -15095832(0xfffffffffvar_a7e8, float:-2.042437E38)
                r5.setTextColor(r6)
                r6 = 17
                r5.setGravity(r6)
                r6 = 788529152(0x2var_, float:1.1641532E-10)
                r9 = 0
                android.graphics.drawable.Drawable r6 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r6, r9)
                r5.setBackgroundDrawable(r6)
                r6 = 1099956224(0x41900000, float:18.0)
                int r13 = org.telegram.messenger.AndroidUtilities.dp(r6)
                int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
                r5.setPadding(r13, r9, r6, r9)
                java.lang.String r6 = "Save"
                r9 = 2131628060(0x7f0e101c, float:1.8883402E38)
                java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r6, r9)
                java.lang.String r6 = r6.toUpperCase()
                r5.setText(r6)
                android.graphics.Typeface r6 = org.telegram.messenger.AndroidUtilities.getTypeface(r11)
                r5.setTypeface(r6)
                r6 = 51
                r9 = -1
                android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r14, (int) r9, (int) r6)
                r3.addView(r5, r6)
                org.telegram.ui.Components.ThemeEditorView$EditorAlert$$ExternalSyntheticLambda4 r6 = new org.telegram.ui.Components.ThemeEditorView$EditorAlert$$ExternalSyntheticLambda4
                r6.<init>(r0)
                r5.setOnClickListener(r6)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ThemeEditorView.EditorAlert.<init>(org.telegram.ui.Components.ThemeEditorView, android.content.Context, java.util.ArrayList):void");
        }

        /* renamed from: lambda$new$0$org-telegram-ui-Components-ThemeEditorView$EditorAlert  reason: not valid java name */
        public /* synthetic */ void m1484xc2ffCLASSNAMEa(View view, int position) {
            if (position != 0) {
                RecyclerView.Adapter adapter = this.listView.getAdapter();
                ListAdapter listAdapter2 = this.listAdapter;
                if (adapter == listAdapter2) {
                    ArrayList unused = this.this$0.currentThemeDesription = listAdapter2.getItem(position - 1);
                } else {
                    ArrayList unused2 = this.this$0.currentThemeDesription = this.searchAdapter.getItem(position - 1);
                }
                int unused3 = this.this$0.currentThemeDesriptionPosition = position;
                for (int a = 0; a < this.this$0.currentThemeDesription.size(); a++) {
                    ThemeDescription description = (ThemeDescription) this.this$0.currentThemeDesription.get(a);
                    if (description.getCurrentKey().equals("chat_wallpaper")) {
                        this.this$0.wallpaperUpdater.showAlert(true);
                        return;
                    }
                    description.startEditing();
                    if (a == 0) {
                        this.colorPicker.setColor(description.getCurrentColor());
                    }
                }
                setColorPickerVisible(true);
            }
        }

        /* renamed from: lambda$new$1$org-telegram-ui-Components-ThemeEditorView$EditorAlert  reason: not valid java name */
        public /* synthetic */ void m1485xCLASSNAMEbf9(View v) {
            dismiss();
        }

        /* renamed from: lambda$new$2$org-telegram-ui-Components-ThemeEditorView$EditorAlert  reason: not valid java name */
        public /* synthetic */ void m1486xCLASSNAMEc6ed8(View v) {
            Theme.saveCurrentTheme(this.this$0.themeInfo, true, false, false);
            setOnDismissListener((DialogInterface.OnDismissListener) null);
            dismiss();
            this.this$0.close();
        }

        /* renamed from: lambda$new$3$org-telegram-ui-Components-ThemeEditorView$EditorAlert  reason: not valid java name */
        public /* synthetic */ void m1487xc6a2c1b7(View v) {
            for (int a = 0; a < this.this$0.currentThemeDesription.size(); a++) {
                ((ThemeDescription) this.this$0.currentThemeDesription.get(a)).setPreviousColor();
            }
            setColorPickerVisible(false);
        }

        /* renamed from: lambda$new$4$org-telegram-ui-Components-ThemeEditorView$EditorAlert  reason: not valid java name */
        public /* synthetic */ void m1488xc7d91496(View v) {
            for (int a = 0; a < this.this$0.currentThemeDesription.size(); a++) {
                ((ThemeDescription) this.this$0.currentThemeDesription.get(a)).setDefaultColor();
            }
            setColorPickerVisible(false);
        }

        /* renamed from: lambda$new$5$org-telegram-ui-Components-ThemeEditorView$EditorAlert  reason: not valid java name */
        public /* synthetic */ void m1489xCLASSNAMEvar_(View v) {
            setColorPickerVisible(false);
        }

        private void runShadowAnimation(final int num, final boolean show) {
            if ((show && this.shadow[num].getTag() != null) || (!show && this.shadow[num].getTag() == null)) {
                this.shadow[num].setTag(show ? null : 1);
                if (show) {
                    this.shadow[num].setVisibility(0);
                }
                AnimatorSet[] animatorSetArr = this.shadowAnimation;
                if (animatorSetArr[num] != null) {
                    animatorSetArr[num].cancel();
                }
                this.shadowAnimation[num] = new AnimatorSet();
                AnimatorSet animatorSet = this.shadowAnimation[num];
                Animator[] animatorArr = new Animator[1];
                View view = this.shadow[num];
                Property property = View.ALPHA;
                float[] fArr = new float[1];
                fArr[0] = show ? 1.0f : 0.0f;
                animatorArr[0] = ObjectAnimator.ofFloat(view, property, fArr);
                animatorSet.playTogether(animatorArr);
                this.shadowAnimation[num].setDuration(150);
                this.shadowAnimation[num].addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        if (EditorAlert.this.shadowAnimation[num] != null && EditorAlert.this.shadowAnimation[num].equals(animation)) {
                            if (!show) {
                                EditorAlert.this.shadow[num].setVisibility(4);
                            }
                            EditorAlert.this.shadowAnimation[num] = null;
                        }
                    }

                    public void onAnimationCancel(Animator animation) {
                        if (EditorAlert.this.shadowAnimation[num] != null && EditorAlert.this.shadowAnimation[num].equals(animation)) {
                            EditorAlert.this.shadowAnimation[num] = null;
                        }
                    }
                });
                this.shadowAnimation[num].start();
            }
        }

        public void dismissInternal() {
            super.dismissInternal();
            if (this.searchField.searchEditText.isFocused()) {
                AndroidUtilities.hideKeyboard(this.searchField.searchEditText);
            }
        }

        /* access modifiers changed from: private */
        public void setColorPickerVisible(boolean visible) {
            float f = 0.0f;
            if (visible) {
                this.animationInProgress = true;
                this.colorPicker.setVisibility(0);
                this.bottomLayout.setVisibility(0);
                this.colorPicker.setAlpha(0.0f);
                this.bottomLayout.setAlpha(0.0f);
                this.previousScrollPosition = this.scrollOffsetY;
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.colorPicker, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.bottomLayout, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.listView, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.frameLayout, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.shadow[0], View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.searchEmptyView, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.bottomSaveLayout, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofInt(this, "scrollOffsetY", new int[]{this.listView.getPaddingTop()})});
                animatorSet.setDuration(150);
                animatorSet.setInterpolator(this.this$0.decelerateInterpolator);
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        EditorAlert.this.listView.setVisibility(4);
                        EditorAlert.this.searchField.setVisibility(4);
                        EditorAlert.this.bottomSaveLayout.setVisibility(4);
                        boolean unused = EditorAlert.this.animationInProgress = false;
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
            Animator[] animatorArr = new Animator[8];
            animatorArr[0] = ObjectAnimator.ofFloat(this.colorPicker, View.ALPHA, new float[]{0.0f});
            animatorArr[1] = ObjectAnimator.ofFloat(this.bottomLayout, View.ALPHA, new float[]{0.0f});
            animatorArr[2] = ObjectAnimator.ofFloat(this.listView, View.ALPHA, new float[]{1.0f});
            animatorArr[3] = ObjectAnimator.ofFloat(this.frameLayout, View.ALPHA, new float[]{1.0f});
            View view = this.shadow[0];
            Property property = View.ALPHA;
            float[] fArr = new float[1];
            if (this.shadow[0].getTag() == null) {
                f = 1.0f;
            }
            fArr[0] = f;
            animatorArr[4] = ObjectAnimator.ofFloat(view, property, fArr);
            animatorArr[5] = ObjectAnimator.ofFloat(this.searchEmptyView, View.ALPHA, new float[]{1.0f});
            animatorArr[6] = ObjectAnimator.ofFloat(this.bottomSaveLayout, View.ALPHA, new float[]{1.0f});
            animatorArr[7] = ObjectAnimator.ofInt(this, "scrollOffsetY", new int[]{this.previousScrollPosition});
            animatorSet2.playTogether(animatorArr);
            animatorSet2.setDuration(150);
            animatorSet2.setInterpolator(this.this$0.decelerateInterpolator);
            animatorSet2.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    if (EditorAlert.this.listView.getAdapter() == EditorAlert.this.searchAdapter) {
                        EditorAlert.this.searchField.showKeyboard();
                    }
                    EditorAlert.this.colorPicker.setVisibility(8);
                    EditorAlert.this.bottomLayout.setVisibility(8);
                    boolean unused = EditorAlert.this.animationInProgress = false;
                }
            });
            animatorSet2.start();
            this.listView.getAdapter().notifyItemChanged(this.this$0.currentThemeDesriptionPosition);
        }

        /* access modifiers changed from: private */
        public int getCurrentTop() {
            if (this.listView.getChildCount() == 0) {
                return -1000;
            }
            int i = 0;
            View child = this.listView.getChildAt(0);
            RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findContainingViewHolder(child);
            if (holder == null) {
                return -1000;
            }
            int paddingTop = this.listView.getPaddingTop();
            if (holder.getAdapterPosition() == 0 && child.getTop() >= 0) {
                i = child.getTop();
            }
            return paddingTop - i;
        }

        /* access modifiers changed from: protected */
        public boolean canDismissWithSwipe() {
            return false;
        }

        /* access modifiers changed from: private */
        public void updateLayout() {
            int top;
            int newOffset;
            if (this.listView.getChildCount() > 0 && this.listView.getVisibility() == 0 && !this.animationInProgress) {
                View child = this.listView.getChildAt(0);
                RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findContainingViewHolder(child);
                if (this.listView.getVisibility() != 0 || this.animationInProgress) {
                    top = this.listView.getPaddingTop();
                } else {
                    top = child.getTop() - AndroidUtilities.dp(8.0f);
                }
                if (top <= (-AndroidUtilities.dp(1.0f)) || holder == null || holder.getAdapterPosition() != 0) {
                    newOffset = 0;
                    runShadowAnimation(0, true);
                } else {
                    newOffset = top;
                    runShadowAnimation(0, false);
                }
                if (this.scrollOffsetY != newOffset) {
                    setScrollOffsetY(newOffset);
                }
            }
        }

        public int getScrollOffsetY() {
            return this.scrollOffsetY;
        }

        public void setScrollOffsetY(int value) {
            RecyclerListView recyclerListView = this.listView;
            this.scrollOffsetY = value;
            recyclerListView.setTopGlowOffset(value);
            this.frameLayout.setTranslationY((float) this.scrollOffsetY);
            this.colorPicker.setTranslationY((float) this.scrollOffsetY);
            this.searchEmptyView.setTranslationY((float) this.scrollOffsetY);
            this.containerView.invalidate();
        }

        public class SearchAdapter extends RecyclerListView.SelectionAdapter {
            private Context context;
            private int currentCount;
            private int lastSearchId;
            private String lastSearchText;
            private ArrayList<CharSequence> searchNames = new ArrayList<>();
            private ArrayList<ArrayList<ThemeDescription>> searchResult = new ArrayList<>();
            private Runnable searchRunnable;

            public SearchAdapter(Context context2) {
                this.context = context2;
            }

            public CharSequence generateSearchName(String name, String q) {
                if (TextUtils.isEmpty(name)) {
                    return "";
                }
                SpannableStringBuilder builder = new SpannableStringBuilder();
                String wholeString = name.trim();
                String lower = wholeString.toLowerCase();
                int lastIndex = 0;
                while (true) {
                    int indexOf = lower.indexOf(q, lastIndex);
                    int index = indexOf;
                    if (indexOf == -1) {
                        break;
                    }
                    int end = q.length() + index;
                    if (lastIndex != 0 && lastIndex != index + 1) {
                        builder.append(wholeString.substring(lastIndex, index));
                    } else if (lastIndex == 0 && index != 0) {
                        builder.append(wholeString.substring(0, index));
                    }
                    String query = wholeString.substring(index, Math.min(wholeString.length(), end));
                    if (query.startsWith(" ")) {
                        builder.append(" ");
                    }
                    String query2 = query.trim();
                    int start = builder.length();
                    builder.append(query2);
                    builder.setSpan(new ForegroundColorSpan(-11697229), start, query2.length() + start, 33);
                    lastIndex = end;
                }
                if (lastIndex != -1 && lastIndex < wholeString.length()) {
                    builder.append(wholeString.substring(lastIndex));
                }
                return builder;
            }

            /* access modifiers changed from: private */
            /* renamed from: searchDialogsInternal */
            public void m1490x282ed99(String query, int searchId) {
                try {
                    String search1 = query.trim().toLowerCase();
                    if (search1.length() == 0) {
                        this.lastSearchId = -1;
                        updateSearchResults(new ArrayList(), new ArrayList(), this.lastSearchId);
                        return;
                    }
                    String search2 = LocaleController.getInstance().getTranslitString(search1);
                    if (search1.equals(search2) || search2.length() == 0) {
                        search2 = null;
                    }
                    String[] search = new String[((search2 != null ? 1 : 0) + 1)];
                    search[0] = search1;
                    if (search2 != null) {
                        search[1] = search2;
                    }
                    ArrayList<ArrayList<ThemeDescription>> searchResults = new ArrayList<>();
                    ArrayList<CharSequence> names = new ArrayList<>();
                    int N = EditorAlert.this.listAdapter.items.size();
                    for (int a = 0; a < N; a++) {
                        ArrayList<ThemeDescription> themeDescriptions = (ArrayList) EditorAlert.this.listAdapter.items.get(a);
                        String key = themeDescriptions.get(0).getCurrentKey();
                        String name = key.toLowerCase();
                        int length = search.length;
                        int i = 0;
                        while (true) {
                            if (i >= length) {
                                break;
                            }
                            String q = search[i];
                            if (name.contains(q)) {
                                searchResults.add(themeDescriptions);
                                names.add(generateSearchName(key, q));
                                break;
                            }
                            i++;
                        }
                    }
                    try {
                        updateSearchResults(searchResults, names, searchId);
                    } catch (Exception e) {
                        e = e;
                    }
                } catch (Exception e2) {
                    e = e2;
                    int i2 = searchId;
                    FileLog.e((Throwable) e);
                }
            }

            private void updateSearchResults(ArrayList<ArrayList<ThemeDescription>> result, ArrayList<CharSequence> names, int searchId) {
                AndroidUtilities.runOnUIThread(new ThemeEditorView$EditorAlert$SearchAdapter$$ExternalSyntheticLambda0(this, searchId, result, names));
            }

            /* renamed from: lambda$updateSearchResults$0$org-telegram-ui-Components-ThemeEditorView$EditorAlert$SearchAdapter  reason: not valid java name */
            public /* synthetic */ void m1491x612aavar_(int searchId, ArrayList result, ArrayList names) {
                if (searchId == this.lastSearchId) {
                    if (EditorAlert.this.listView.getAdapter() != EditorAlert.this.searchAdapter) {
                        EditorAlert editorAlert = EditorAlert.this;
                        int unused = editorAlert.topBeforeSwitch = editorAlert.getCurrentTop();
                        EditorAlert.this.listView.setAdapter(EditorAlert.this.searchAdapter);
                        EditorAlert.this.searchAdapter.notifyDataSetChanged();
                    }
                    boolean isEmpty = true;
                    boolean becomeEmpty = !this.searchResult.isEmpty() && result.isEmpty();
                    if (!this.searchResult.isEmpty() || !result.isEmpty()) {
                        isEmpty = false;
                    }
                    if (becomeEmpty) {
                        EditorAlert editorAlert2 = EditorAlert.this;
                        int unused2 = editorAlert2.topBeforeSwitch = editorAlert2.getCurrentTop();
                    }
                    this.searchResult = result;
                    this.searchNames = names;
                    notifyDataSetChanged();
                    if (!isEmpty && !becomeEmpty && EditorAlert.this.topBeforeSwitch > 0) {
                        EditorAlert.this.layoutManager.scrollToPositionWithOffset(0, -EditorAlert.this.topBeforeSwitch);
                        int unused3 = EditorAlert.this.topBeforeSwitch = -1000;
                    }
                    EditorAlert.this.searchEmptyView.showTextView();
                }
            }

            public void searchDialogs(String query) {
                if (query == null || !query.equals(this.lastSearchText)) {
                    this.lastSearchText = query;
                    if (this.searchRunnable != null) {
                        Utilities.searchQueue.cancelRunnable(this.searchRunnable);
                        this.searchRunnable = null;
                    }
                    if (query == null || query.length() == 0) {
                        this.searchResult.clear();
                        EditorAlert editorAlert = EditorAlert.this;
                        int unused = editorAlert.topBeforeSwitch = editorAlert.getCurrentTop();
                        this.lastSearchId = -1;
                        notifyDataSetChanged();
                        return;
                    }
                    int searchId = this.lastSearchId + 1;
                    this.lastSearchId = searchId;
                    this.searchRunnable = new ThemeEditorView$EditorAlert$SearchAdapter$$ExternalSyntheticLambda1(this, query, searchId);
                    Utilities.searchQueue.postRunnable(this.searchRunnable, 300);
                }
            }

            public int getItemCount() {
                if (this.searchResult.isEmpty()) {
                    return 0;
                }
                return this.searchResult.size() + 1;
            }

            public ArrayList<ThemeDescription> getItem(int i) {
                if (i < 0 || i >= this.searchResult.size()) {
                    return null;
                }
                return this.searchResult.get(i);
            }

            public boolean isEnabled(RecyclerView.ViewHolder holder) {
                return true;
            }

            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view;
                switch (viewType) {
                    case 0:
                        view = new TextColorThemeCell(this.context);
                        view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                        break;
                    default:
                        view = new View(this.context);
                        view.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(56.0f)));
                        break;
                }
                return new RecyclerListView.Holder(view);
            }

            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                int color;
                if (holder.getItemViewType() == 0) {
                    ThemeDescription description = this.searchResult.get(position - 1).get(0);
                    if (description.getCurrentKey().equals("chat_wallpaper")) {
                        color = 0;
                    } else {
                        color = description.getSetColor();
                    }
                    ((TextColorThemeCell) holder.itemView).setTextAndColor(this.searchNames.get(position - 1), color);
                }
            }

            public int getItemViewType(int i) {
                if (i == 0) {
                    return 1;
                }
                return 0;
            }
        }

        private class ListAdapter extends RecyclerListView.SelectionAdapter {
            private Context context;
            private int currentCount;
            /* access modifiers changed from: private */
            public ArrayList<ArrayList<ThemeDescription>> items = new ArrayList<>();

            public ListAdapter(Context context2, ArrayList<ThemeDescription> descriptions) {
                this.context = context2;
                HashMap<String, ArrayList<ThemeDescription>> itemsMap = new HashMap<>();
                int N = descriptions.size();
                for (int a = 0; a < N; a++) {
                    ThemeDescription description = descriptions.get(a);
                    String key = description.getCurrentKey();
                    ArrayList<ThemeDescription> arrayList = itemsMap.get(key);
                    if (arrayList == null) {
                        arrayList = new ArrayList<>();
                        itemsMap.put(key, arrayList);
                        this.items.add(arrayList);
                    }
                    arrayList.add(description);
                }
                if (Build.VERSION.SDK_INT >= 26 && !itemsMap.containsKey("windowBackgroundGray")) {
                    ArrayList<ThemeDescription> arrayList2 = new ArrayList<>();
                    arrayList2.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
                    this.items.add(arrayList2);
                }
            }

            public int getItemCount() {
                if (this.items.isEmpty()) {
                    return 0;
                }
                return this.items.size() + 1;
            }

            public ArrayList<ThemeDescription> getItem(int i) {
                if (i < 0 || i >= this.items.size()) {
                    return null;
                }
                return this.items.get(i);
            }

            public boolean isEnabled(RecyclerView.ViewHolder holder) {
                return true;
            }

            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view;
                switch (viewType) {
                    case 0:
                        view = new TextColorThemeCell(this.context);
                        view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                        break;
                    default:
                        view = new View(this.context);
                        view.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(56.0f)));
                        break;
                }
                return new RecyclerListView.Holder(view);
            }

            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                int color;
                if (holder.getItemViewType() == 0) {
                    ThemeDescription description = this.items.get(position - 1).get(0);
                    if (description.getCurrentKey().equals("chat_wallpaper")) {
                        color = 0;
                    } else {
                        color = description.getSetColor();
                    }
                    ((TextColorThemeCell) holder.itemView).setTextAndColor(description.getTitle(), color);
                }
            }

            public int getItemViewType(int i) {
                if (i == 0) {
                    return 1;
                }
                return 0;
            }
        }
    }

    public void show(Activity activity, Theme.ThemeInfo theme) {
        if (Instance != null) {
            Instance.destroy();
        }
        this.hidden = false;
        this.themeInfo = theme;
        this.windowView = new FrameLayout(activity) {
            private boolean dragging;
            private float startX;
            private float startY;

            public boolean onInterceptTouchEvent(MotionEvent event) {
                return true;
            }

            public boolean onTouchEvent(MotionEvent event) {
                BaseFragment fragment;
                ArrayList<ThemeDescription> items;
                float x = event.getRawX();
                float y = event.getRawY();
                if (event.getAction() == 0) {
                    this.startX = x;
                    this.startY = y;
                } else if (event.getAction() != 2 || this.dragging) {
                    if (event.getAction() == 1 && !this.dragging && ThemeEditorView.this.editorAlert == null) {
                        LaunchActivity launchActivity = (LaunchActivity) ThemeEditorView.this.parentActivity;
                        ActionBarLayout actionBarLayout = null;
                        if (AndroidUtilities.isTablet()) {
                            actionBarLayout = launchActivity.getLayersActionBarLayout();
                            if (actionBarLayout != null && actionBarLayout.fragmentsStack.isEmpty()) {
                                actionBarLayout = null;
                            }
                            if (actionBarLayout == null && (actionBarLayout = launchActivity.getRightActionBarLayout()) != null && actionBarLayout.fragmentsStack.isEmpty()) {
                                actionBarLayout = null;
                            }
                        }
                        if (actionBarLayout == null) {
                            actionBarLayout = launchActivity.getActionBarLayout();
                        }
                        if (actionBarLayout != null) {
                            if (!actionBarLayout.fragmentsStack.isEmpty()) {
                                fragment = actionBarLayout.fragmentsStack.get(actionBarLayout.fragmentsStack.size() - 1);
                            } else {
                                fragment = null;
                            }
                            if (!(fragment == null || (items = fragment.getThemeDescriptions()) == null)) {
                                ThemeEditorView themeEditorView = ThemeEditorView.this;
                                ThemeEditorView themeEditorView2 = ThemeEditorView.this;
                                EditorAlert unused = themeEditorView.editorAlert = new EditorAlert(themeEditorView2, themeEditorView2.parentActivity, items);
                                ThemeEditorView.this.editorAlert.setOnDismissListener(ThemeEditorView$1$$ExternalSyntheticLambda1.INSTANCE);
                                ThemeEditorView.this.editorAlert.setOnDismissListener(new ThemeEditorView$1$$ExternalSyntheticLambda0(this));
                                ThemeEditorView.this.editorAlert.show();
                                ThemeEditorView.this.hide();
                            }
                        }
                    }
                } else if (Math.abs(this.startX - x) >= AndroidUtilities.getPixelsInCM(0.3f, true) || Math.abs(this.startY - y) >= AndroidUtilities.getPixelsInCM(0.3f, false)) {
                    this.dragging = true;
                    this.startX = x;
                    this.startY = y;
                }
                if (this.dragging) {
                    if (event.getAction() == 2) {
                        WindowManager.LayoutParams access$5300 = ThemeEditorView.this.windowLayoutParams;
                        access$5300.x = (int) (((float) access$5300.x) + (x - this.startX));
                        WindowManager.LayoutParams access$53002 = ThemeEditorView.this.windowLayoutParams;
                        access$53002.y = (int) (((float) access$53002.y) + (y - this.startY));
                        int maxDiff = ThemeEditorView.this.editorWidth / 2;
                        if (ThemeEditorView.this.windowLayoutParams.x < (-maxDiff)) {
                            ThemeEditorView.this.windowLayoutParams.x = -maxDiff;
                        } else if (ThemeEditorView.this.windowLayoutParams.x > (AndroidUtilities.displaySize.x - ThemeEditorView.this.windowLayoutParams.width) + maxDiff) {
                            ThemeEditorView.this.windowLayoutParams.x = (AndroidUtilities.displaySize.x - ThemeEditorView.this.windowLayoutParams.width) + maxDiff;
                        }
                        float alpha = 1.0f;
                        if (ThemeEditorView.this.windowLayoutParams.x < 0) {
                            alpha = ((((float) ThemeEditorView.this.windowLayoutParams.x) / ((float) maxDiff)) * 0.5f) + 1.0f;
                        } else if (ThemeEditorView.this.windowLayoutParams.x > AndroidUtilities.displaySize.x - ThemeEditorView.this.windowLayoutParams.width) {
                            alpha = 1.0f - ((((float) ((ThemeEditorView.this.windowLayoutParams.x - AndroidUtilities.displaySize.x) + ThemeEditorView.this.windowLayoutParams.width)) / ((float) maxDiff)) * 0.5f);
                        }
                        if (ThemeEditorView.this.windowView.getAlpha() != alpha) {
                            ThemeEditorView.this.windowView.setAlpha(alpha);
                        }
                        if (ThemeEditorView.this.windowLayoutParams.y < (-0)) {
                            ThemeEditorView.this.windowLayoutParams.y = -0;
                        } else if (ThemeEditorView.this.windowLayoutParams.y > (AndroidUtilities.displaySize.y - ThemeEditorView.this.windowLayoutParams.height) + 0) {
                            ThemeEditorView.this.windowLayoutParams.y = (AndroidUtilities.displaySize.y - ThemeEditorView.this.windowLayoutParams.height) + 0;
                        }
                        ThemeEditorView.this.windowManager.updateViewLayout(ThemeEditorView.this.windowView, ThemeEditorView.this.windowLayoutParams);
                        this.startX = x;
                        this.startY = y;
                    } else if (event.getAction() == 1) {
                        this.dragging = false;
                        ThemeEditorView.this.animateToBoundsMaybe();
                    }
                }
                return true;
            }

            static /* synthetic */ void lambda$onTouchEvent$0(DialogInterface dialog) {
            }

            /* renamed from: lambda$onTouchEvent$1$org-telegram-ui-Components-ThemeEditorView$1  reason: not valid java name */
            public /* synthetic */ void m1483x37CLASSNAME(DialogInterface dialog) {
                EditorAlert unused = ThemeEditorView.this.editorAlert = null;
                ThemeEditorView.this.show();
            }
        };
        this.windowManager = (WindowManager) activity.getSystemService("window");
        SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("themeconfig", 0);
        this.preferences = sharedPreferences;
        int sidex = sharedPreferences.getInt("sidex", 1);
        int sidey = this.preferences.getInt("sidey", 0);
        float px = this.preferences.getFloat("px", 0.0f);
        float py = this.preferences.getFloat("py", 0.0f);
        try {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            this.windowLayoutParams = layoutParams;
            layoutParams.width = this.editorWidth;
            this.windowLayoutParams.height = this.editorHeight;
            this.windowLayoutParams.x = getSideCoord(true, sidex, px, this.editorWidth);
            this.windowLayoutParams.y = getSideCoord(false, sidey, py, this.editorHeight);
            this.windowLayoutParams.format = -3;
            this.windowLayoutParams.gravity = 51;
            this.windowLayoutParams.type = 99;
            this.windowLayoutParams.flags = 16777736;
            this.windowManager.addView(this.windowView, this.windowLayoutParams);
            this.wallpaperUpdater = new WallpaperUpdater(activity, (BaseFragment) null, new WallpaperUpdater.WallpaperUpdaterDelegate() {
                public void didSelectWallpaper(File file, Bitmap bitmap, boolean gallery) {
                    Theme.setThemeWallpaper(ThemeEditorView.this.themeInfo, bitmap, file);
                }

                public void needOpenColorPicker() {
                    for (int a = 0; a < ThemeEditorView.this.currentThemeDesription.size(); a++) {
                        ThemeDescription description = (ThemeDescription) ThemeEditorView.this.currentThemeDesription.get(a);
                        description.startEditing();
                        if (a == 0) {
                            ThemeEditorView.this.editorAlert.colorPicker.setColor(description.getCurrentColor());
                        }
                    }
                    ThemeEditorView.this.editorAlert.setColorPickerVisible(true);
                }
            });
            Instance = this;
            this.parentActivity = activity;
            showWithAnimation();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
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

    private static int getSideCoord(boolean isX, int side, float p, int sideSize) {
        int total;
        int result;
        if (isX) {
            total = AndroidUtilities.displaySize.x - sideSize;
        } else {
            total = (AndroidUtilities.displaySize.y - sideSize) - ActionBar.getCurrentActionBarHeight();
        }
        if (side == 0) {
            result = AndroidUtilities.dp(10.0f);
        } else if (side == 1) {
            result = total - AndroidUtilities.dp(10.0f);
        } else {
            result = AndroidUtilities.dp(10.0f) + Math.round(((float) (total - AndroidUtilities.dp(20.0f))) * p);
        }
        if (!isX) {
            return result + ActionBar.getCurrentActionBarHeight();
        }
        return result;
    }

    /* access modifiers changed from: private */
    public void hide() {
        if (this.parentActivity != null) {
            try {
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.windowView, View.ALPHA, new float[]{1.0f, 0.0f}), ObjectAnimator.ofFloat(this.windowView, View.SCALE_X, new float[]{1.0f, 0.0f}), ObjectAnimator.ofFloat(this.windowView, View.SCALE_Y, new float[]{1.0f, 0.0f})});
                animatorSet.setInterpolator(this.decelerateInterpolator);
                animatorSet.setDuration(150);
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        if (ThemeEditorView.this.windowView != null) {
                            ThemeEditorView.this.windowView.setBackground((Drawable) null);
                            ThemeEditorView.this.windowManager.removeView(ThemeEditorView.this.windowView);
                        }
                    }
                });
                animatorSet.start();
                this.hidden = true;
            } catch (Exception e) {
            }
        }
    }

    /* access modifiers changed from: private */
    public void show() {
        if (this.parentActivity != null) {
            try {
                this.windowManager.addView(this.windowView, this.windowLayoutParams);
                this.hidden = false;
                showWithAnimation();
            } catch (Exception e) {
            }
        }
    }

    public void close() {
        try {
            this.windowManager.removeView(this.windowView);
        } catch (Exception e) {
        }
        this.parentActivity = null;
    }

    public void onConfigurationChanged() {
        int sidex = this.preferences.getInt("sidex", 1);
        int sidey = this.preferences.getInt("sidey", 0);
        float px = this.preferences.getFloat("px", 0.0f);
        float py = this.preferences.getFloat("py", 0.0f);
        this.windowLayoutParams.x = getSideCoord(true, sidex, px, this.editorWidth);
        this.windowLayoutParams.y = getSideCoord(false, sidey, py, this.editorHeight);
        try {
            if (this.windowView.getParent() != null) {
                this.windowManager.updateViewLayout(this.windowView, this.windowLayoutParams);
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        WallpaperUpdater wallpaperUpdater2 = this.wallpaperUpdater;
        if (wallpaperUpdater2 != null) {
            wallpaperUpdater2.onActivityResult(requestCode, resultCode, data);
        }
    }

    /* access modifiers changed from: private */
    public void animateToBoundsMaybe() {
        int startX = getSideCoord(true, 0, 0.0f, this.editorWidth);
        int endX = getSideCoord(true, 1, 0.0f, this.editorWidth);
        int startY = getSideCoord(false, 0, 0.0f, this.editorHeight);
        int endY = getSideCoord(false, 1, 0.0f, this.editorHeight);
        ArrayList<Animator> animators = null;
        SharedPreferences.Editor editor = this.preferences.edit();
        int maxDiff = AndroidUtilities.dp(20.0f);
        boolean slideOut = false;
        if (Math.abs(startX - this.windowLayoutParams.x) <= maxDiff || (this.windowLayoutParams.x < 0 && this.windowLayoutParams.x > (-this.editorWidth) / 4)) {
            if (0 == 0) {
                animators = new ArrayList<>();
            }
            editor.putInt("sidex", 0);
            if (this.windowView.getAlpha() != 1.0f) {
                animators.add(ObjectAnimator.ofFloat(this.windowView, View.ALPHA, new float[]{1.0f}));
            }
            animators.add(ObjectAnimator.ofInt(this, "x", new int[]{startX}));
        } else if (Math.abs(endX - this.windowLayoutParams.x) <= maxDiff || (this.windowLayoutParams.x > AndroidUtilities.displaySize.x - this.editorWidth && this.windowLayoutParams.x < AndroidUtilities.displaySize.x - ((this.editorWidth / 4) * 3))) {
            if (0 == 0) {
                animators = new ArrayList<>();
            }
            editor.putInt("sidex", 1);
            if (this.windowView.getAlpha() != 1.0f) {
                animators.add(ObjectAnimator.ofFloat(this.windowView, View.ALPHA, new float[]{1.0f}));
            }
            animators.add(ObjectAnimator.ofInt(this, "x", new int[]{endX}));
        } else if (this.windowView.getAlpha() != 1.0f) {
            if (0 == 0) {
                animators = new ArrayList<>();
            }
            if (this.windowLayoutParams.x < 0) {
                animators.add(ObjectAnimator.ofInt(this, "x", new int[]{-this.editorWidth}));
            } else {
                animators.add(ObjectAnimator.ofInt(this, "x", new int[]{AndroidUtilities.displaySize.x}));
            }
            slideOut = true;
        } else {
            editor.putFloat("px", ((float) (this.windowLayoutParams.x - startX)) / ((float) (endX - startX)));
            editor.putInt("sidex", 2);
        }
        if (!slideOut) {
            if (Math.abs(startY - this.windowLayoutParams.y) <= maxDiff || this.windowLayoutParams.y <= ActionBar.getCurrentActionBarHeight()) {
                if (animators == null) {
                    animators = new ArrayList<>();
                }
                editor.putInt("sidey", 0);
                animators.add(ObjectAnimator.ofInt(this, "y", new int[]{startY}));
            } else if (Math.abs(endY - this.windowLayoutParams.y) <= maxDiff) {
                if (animators == null) {
                    animators = new ArrayList<>();
                }
                editor.putInt("sidey", 1);
                animators.add(ObjectAnimator.ofInt(this, "y", new int[]{endY}));
            } else {
                editor.putFloat("py", ((float) (this.windowLayoutParams.y - startY)) / ((float) (endY - startY)));
                editor.putInt("sidey", 2);
            }
            editor.commit();
        }
        if (animators != null) {
            if (this.decelerateInterpolator == null) {
                this.decelerateInterpolator = new DecelerateInterpolator();
            }
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.setInterpolator(this.decelerateInterpolator);
            animatorSet.setDuration(150);
            if (slideOut) {
                animators.add(ObjectAnimator.ofFloat(this.windowView, View.ALPHA, new float[]{0.0f}));
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        Theme.saveCurrentTheme(ThemeEditorView.this.themeInfo, true, false, false);
                        ThemeEditorView.this.destroy();
                    }
                });
            }
            animatorSet.playTogether(animators);
            animatorSet.start();
        }
    }

    public int getX() {
        return this.windowLayoutParams.x;
    }

    public int getY() {
        return this.windowLayoutParams.y;
    }

    public void setX(int value) {
        this.windowLayoutParams.x = value;
        this.windowManager.updateViewLayout(this.windowView, this.windowLayoutParams);
    }

    public void setY(int value) {
        this.windowLayoutParams.y = value;
        this.windowManager.updateViewLayout(this.windowView, this.windowLayoutParams);
    }
}
