package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
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
import android.support.annotation.Keep;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.C0446R;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.exoplayer2.C0542C;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.LayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarLayout;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.TextColorThemeCell;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
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

    /* renamed from: org.telegram.ui.Components.ThemeEditorView$3 */
    class C13213 extends AnimatorListenerAdapter {
        C13213() {
        }

        public void onAnimationEnd(Animator animator) {
            if (ThemeEditorView.this.windowView != null) {
                ThemeEditorView.this.windowManager.removeView(ThemeEditorView.this.windowView);
            }
        }
    }

    /* renamed from: org.telegram.ui.Components.ThemeEditorView$4 */
    class C13224 extends AnimatorListenerAdapter {
        C13224() {
        }

        public void onAnimationEnd(Animator animator) {
            Theme.saveCurrentTheme(ThemeEditorView.this.currentThemeName, true);
            ThemeEditorView.this.destroy();
        }
    }

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

        /* renamed from: org.telegram.ui.Components.ThemeEditorView$EditorAlert$9 */
        class C13299 extends AnimatorListenerAdapter {
            C13299() {
            }

            public void onAnimationEnd(Animator animator) {
                EditorAlert.this.listView.setVisibility(4);
                EditorAlert.this.bottomSaveLayout.setVisibility(4);
                EditorAlert.this.animationInProgress = false;
            }
        }

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
                this.circleDrawable = context.getResources().getDrawable(C0446R.drawable.knob_shadow).mutate();
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
                    r0.colorEditText[i] = new EditTextBoldCursor(context2);
                    r0.colorEditText[i].setInputType(2);
                    r0.colorEditText[i].setTextColor(-14606047);
                    r0.colorEditText[i].setCursorColor(-14606047);
                    r0.colorEditText[i].setCursorSize(AndroidUtilities.dp(20.0f));
                    r0.colorEditText[i].setCursorWidth(1.5f);
                    r0.colorEditText[i].setTextSize(1, 18.0f);
                    r0.colorEditText[i].setBackgroundDrawable(Theme.createEditTextDrawable(context2, true));
                    r0.colorEditText[i].setMaxLines(1);
                    r0.colorEditText[i].setTag(Integer.valueOf(i));
                    r0.colorEditText[i].setGravity(17);
                    if (i == 0) {
                        r0.colorEditText[i].setHint("red");
                    } else if (i == 1) {
                        r0.colorEditText[i].setHint("green");
                    } else if (i == 2) {
                        r0.colorEditText[i].setHint("blue");
                    } else if (i == 3) {
                        r0.colorEditText[i].setHint("alpha");
                    }
                    r0.colorEditText[i].setImeOptions((i == 3 ? 6 : 5) | 268435456);
                    r0.colorEditText[i].setFilters(new InputFilter[]{new LengthFilter(3)});
                    r0.linearLayout.addView(r0.colorEditText[i], LayoutHelper.createLinear(55, 36, 0.0f, 0.0f, i != 3 ? 16.0f : 0.0f, 0.0f));
                    r0.colorEditText[i].addTextChangedListener(new TextWatcher() {
                        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                        }

                        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                        }

                        public void afterTextChanged(Editable editable) {
                            if (!ColorPicker.this.this$1.ignoreTextChange) {
                                ColorPicker.this.this$1.ignoreTextChange = true;
                                editable = Utilities.parseInt(editable.toString()).intValue();
                                StringBuilder stringBuilder;
                                if (editable < null) {
                                    editable = ColorPicker.this.colorEditText[i];
                                    stringBuilder = new StringBuilder();
                                    stringBuilder.append(TtmlNode.ANONYMOUS_REGION_ID);
                                    stringBuilder.append(0);
                                    editable.setText(stringBuilder.toString());
                                    ColorPicker.this.colorEditText[i].setSelection(ColorPicker.this.colorEditText[i].length());
                                    editable = null;
                                } else if (editable > 255) {
                                    editable = ColorPicker.this.colorEditText[i];
                                    stringBuilder = new StringBuilder();
                                    stringBuilder.append(TtmlNode.ANONYMOUS_REGION_ID);
                                    stringBuilder.append(255);
                                    editable.setText(stringBuilder.toString());
                                    ColorPicker.this.colorEditText[i].setSelection(ColorPicker.this.colorEditText[i].length());
                                    editable = 255;
                                }
                                int color = ColorPicker.this.getColor();
                                if (i == 2) {
                                    color = (color & -256) | (editable & 255);
                                } else if (i == 1) {
                                    color = (-65281 & color) | ((editable & 255) << 8);
                                } else if (i == 0) {
                                    color = (-16711681 & color) | ((editable & 255) << 16);
                                } else if (i == 3) {
                                    color = (16777215 & color) | ((editable & 255) << 24);
                                }
                                ColorPicker.this.setColor(color);
                                for (editable = null; editable < ColorPicker.this.this$1.this$0.currentThemeDesription.size(); editable++) {
                                    ((ThemeDescription) ColorPicker.this.this$1.this$0.currentThemeDesription.get(editable)).setColor(ColorPicker.this.getColor(), false);
                                }
                                ColorPicker.this.this$1.ignoreTextChange = false;
                            }
                        }
                    });
                    r0.colorEditText[i].setOnEditorActionListener(new OnEditorActionListener() {
                        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                            if (i != 6) {
                                return null;
                            }
                            AndroidUtilities.hideKeyboard(textView);
                            return true;
                        }
                    });
                    i++;
                }
            }

            protected void onMeasure(int i, int i2) {
                int min = Math.min(MeasureSpec.getSize(i), MeasureSpec.getSize(i2));
                measureChild(this.linearLayout, i, i2);
                setMeasuredDimension(min, min);
            }

            protected void onDraw(Canvas canvas) {
                float f;
                Canvas canvas2 = canvas;
                int width = (getWidth() / 2) - (this.paramValueSliderWidth * 2);
                int height = (getHeight() / 2) - AndroidUtilities.dp(8.0f);
                canvas2.drawBitmap(this.colorWheelBitmap, (float) (width - this.colorWheelRadius), (float) (height - this.colorWheelRadius), null);
                double toRadians = (double) ((float) Math.toRadians((double) this.colorHSV[0]));
                int i = ((int) (((-Math.cos(toRadians)) * ((double) this.colorHSV[1])) * ((double) this.colorWheelRadius))) + width;
                int i2 = ((int) (((-Math.sin(toRadians)) * ((double) this.colorHSV[1])) * ((double) this.colorWheelRadius))) + height;
                int i3 = this.colorWheelRadius;
                this.hsvTemp[0] = this.colorHSV[0];
                this.hsvTemp[1] = this.colorHSV[1];
                this.hsvTemp[2] = 1.0f;
                drawPointerArrow(canvas2, i, i2, Color.HSVToColor(this.hsvTemp));
                int i4 = (width + this.colorWheelRadius) + this.paramValueSliderWidth;
                height -= this.colorWheelRadius;
                int dp = AndroidUtilities.dp(9.0f);
                int i5 = this.colorWheelRadius * 2;
                if (this.colorGradient == null) {
                    r0.colorGradient = new LinearGradient((float) i4, (float) height, (float) (i4 + dp), (float) (height + i5), new int[]{Theme.ACTION_BAR_VIDEO_EDIT_COLOR, Color.HSVToColor(r0.hsvTemp)}, null, TileMode.CLAMP);
                }
                r0.valueSliderPaint.setShader(r0.colorGradient);
                float f2 = (float) height;
                float f3 = (float) (height + i5);
                canvas2.drawRect((float) i4, f2, (float) (i4 + dp), f3, r0.valueSliderPaint);
                int i6 = dp / 2;
                float f4 = (float) i5;
                drawPointerArrow(canvas2, i4 + i6, (int) ((r0.colorHSV[2] * f4) + f2), Color.HSVToColor(r0.colorHSV));
                i4 += r0.paramValueSliderWidth * 2;
                if (r0.alphaGradient == null) {
                    width = Color.HSVToColor(r0.hsvTemp);
                    f = f3;
                    r0.alphaGradient = new LinearGradient((float) i4, f2, (float) (i4 + dp), f, new int[]{width, width & 16777215}, null, TileMode.CLAMP);
                } else {
                    f = f3;
                }
                r0.valueSliderPaint.setShader(r0.alphaGradient);
                canvas2.drawRect((float) i4, f2, (float) (dp + i4), f, r0.valueSliderPaint);
                drawPointerArrow(canvas2, i4 + i6, (int) (f2 + ((1.0f - r0.alpha) * f4)), (Color.HSVToColor(r0.colorHSV) & 16777215) | (((int) (255.0f * r0.alpha)) << 24));
            }

            private void drawPointerArrow(Canvas canvas, int i, int i2, int i3) {
                int dp = AndroidUtilities.dp(13.0f);
                this.circleDrawable.setBounds(i - dp, i2 - dp, i + dp, dp + i2);
                this.circleDrawable.draw(canvas);
                this.circlePaint.setColor(-1);
                i = (float) i;
                i2 = (float) i2;
                canvas.drawCircle(i, i2, (float) AndroidUtilities.dp(11.0f), this.circlePaint);
                this.circlePaint.setColor(i3);
                canvas.drawCircle(i, i2, (float) AndroidUtilities.dp(NUM), this.circlePaint);
            }

            protected void onSizeChanged(int i, int i2, int i3, int i4) {
                this.colorWheelRadius = Math.max(1, ((i / 2) - (this.paramValueSliderWidth * 2)) - AndroidUtilities.dp(NUM));
                this.colorWheelBitmap = createColorWheelBitmap(this.colorWheelRadius * 2, this.colorWheelRadius * 2);
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
                i = (float) (i / 2);
                i2 = (float) (i2 / 2);
                this.colorWheelPaint.setShader(new ComposeShader(new SweepGradient(i, i2, iArr, null), new RadialGradient(i, i2, (float) this.colorWheelRadius, -1, 16777215, TileMode.CLAMP), Mode.SRC_OVER));
                new Canvas(createBitmap).drawCircle(i, i2, (float) this.colorWheelRadius, this.colorWheelPaint);
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
                    String str = "alpha";
                    int[] iArr = new int[1];
                    iArr[0] = z ? 0 : 51;
                    animatorArr[0] = ObjectAnimator.ofInt(access$500, str, iArr);
                    ViewGroup access$600 = this.this$1.containerView;
                    str = "alpha";
                    float[] fArr = new float[1];
                    fArr[0] = z ? true : true;
                    animatorArr[1] = ObjectAnimator.ofFloat(access$600, str, fArr);
                    access$400.playTogether(animatorArr);
                    this.this$1.colorChangeAnimation.setDuration(150);
                    this.this$1.colorChangeAnimation.setInterpolator(this.decelerateInterpolator);
                    this.this$1.colorChangeAnimation.start();
                }
            }

            public boolean onTouchEvent(MotionEvent motionEvent) {
                ColorPicker colorPicker = this;
                switch (motionEvent.getAction()) {
                    case 0:
                    case 2:
                        int i;
                        float f;
                        int blue;
                        EditTextBoldCursor editTextBoldCursor;
                        StringBuilder stringBuilder;
                        EditTextBoldCursor editTextBoldCursor2;
                        StringBuilder stringBuilder2;
                        StringBuilder stringBuilder3;
                        int x = (int) motionEvent.getX();
                        int y = (int) motionEvent.getY();
                        int width = (getWidth() / 2) - (colorPicker.paramValueSliderWidth * 2);
                        int height = (getHeight() / 2) - AndroidUtilities.dp(8.0f);
                        int i2 = x - width;
                        int i3 = y - height;
                        double sqrt = Math.sqrt((double) ((i2 * i2) + (i3 * i3)));
                        if (colorPicker.circlePressed) {
                            i = height;
                        } else if (colorPicker.alphaPressed || colorPicker.colorPressed) {
                            i = height;
                            if (colorPicker.colorPressed || (!colorPicker.circlePressed && !colorPicker.alphaPressed && x >= (colorPicker.colorWheelRadius + width) + colorPicker.paramValueSliderWidth && x <= (colorPicker.colorWheelRadius + width) + (colorPicker.paramValueSliderWidth * 2) && y >= i - colorPicker.colorWheelRadius && y <= i + colorPicker.colorWheelRadius)) {
                                f = ((float) (y - (i - colorPicker.colorWheelRadius))) / (((float) colorPicker.colorWheelRadius) * 2.0f);
                                if (f >= 0.0f) {
                                    f = 0.0f;
                                } else if (f > 1.0f) {
                                    f = 1.0f;
                                }
                                colorPicker.colorHSV[2] = f;
                                colorPicker.colorPressed = true;
                            }
                            if (colorPicker.alphaPressed || (!colorPicker.circlePressed && !colorPicker.colorPressed && x >= (colorPicker.colorWheelRadius + width) + (colorPicker.paramValueSliderWidth * 3) && x <= (width + colorPicker.colorWheelRadius) + (colorPicker.paramValueSliderWidth * 4) && y >= i - colorPicker.colorWheelRadius && y <= i + colorPicker.colorWheelRadius)) {
                                colorPicker.alpha = 1.0f - (((float) (y - (i - colorPicker.colorWheelRadius))) / (((float) colorPicker.colorWheelRadius) * 2.0f));
                                if (colorPicker.alpha >= 0.0f) {
                                    colorPicker.alpha = 0.0f;
                                } else if (colorPicker.alpha > 1.0f) {
                                    colorPicker.alpha = 1.0f;
                                }
                                colorPicker.alphaPressed = true;
                            }
                            if (!(colorPicker.alphaPressed || colorPicker.colorPressed)) {
                                if (colorPicker.circlePressed) {
                                }
                                return true;
                            }
                            startColorChange(true);
                            x = getColor();
                            for (y = 0; y < colorPicker.this$1.this$0.currentThemeDesription.size(); y++) {
                                ((ThemeDescription) colorPicker.this$1.this$0.currentThemeDesription.get(y)).setColor(x, false);
                            }
                            y = Color.red(x);
                            width = Color.green(x);
                            blue = Color.blue(x);
                            x = Color.alpha(x);
                            if (!colorPicker.this$1.ignoreTextChange) {
                                colorPicker.this$1.ignoreTextChange = true;
                                editTextBoldCursor = colorPicker.colorEditText[0];
                                stringBuilder = new StringBuilder();
                                stringBuilder.append(TtmlNode.ANONYMOUS_REGION_ID);
                                stringBuilder.append(y);
                                editTextBoldCursor.setText(stringBuilder.toString());
                                editTextBoldCursor2 = colorPicker.colorEditText[1];
                                stringBuilder2 = new StringBuilder();
                                stringBuilder2.append(TtmlNode.ANONYMOUS_REGION_ID);
                                stringBuilder2.append(width);
                                editTextBoldCursor2.setText(stringBuilder2.toString());
                                editTextBoldCursor2 = colorPicker.colorEditText[2];
                                stringBuilder3 = new StringBuilder();
                                stringBuilder3.append(TtmlNode.ANONYMOUS_REGION_ID);
                                stringBuilder3.append(blue);
                                editTextBoldCursor2.setText(stringBuilder3.toString());
                                editTextBoldCursor2 = colorPicker.colorEditText[3];
                                stringBuilder3 = new StringBuilder();
                                stringBuilder3.append(TtmlNode.ANONYMOUS_REGION_ID);
                                stringBuilder3.append(x);
                                editTextBoldCursor2.setText(stringBuilder3.toString());
                                for (x = 0; x < 4; x++) {
                                    colorPicker.colorEditText[x].setSelection(colorPicker.colorEditText[x].length());
                                }
                                colorPicker.this$1.ignoreTextChange = false;
                            }
                            invalidate();
                            return true;
                        } else {
                            i = height;
                            if (sqrt <= ((double) colorPicker.colorWheelRadius)) {
                            }
                            f = ((float) (y - (i - colorPicker.colorWheelRadius))) / (((float) colorPicker.colorWheelRadius) * 2.0f);
                            if (f >= 0.0f) {
                                f = 0.0f;
                            } else if (f > 1.0f) {
                                f = 1.0f;
                            }
                            colorPicker.colorHSV[2] = f;
                            colorPicker.colorPressed = true;
                            colorPicker.alpha = 1.0f - (((float) (y - (i - colorPicker.colorWheelRadius))) / (((float) colorPicker.colorWheelRadius) * 2.0f));
                            if (colorPicker.alpha >= 0.0f) {
                                colorPicker.alpha = 0.0f;
                            } else if (colorPicker.alpha > 1.0f) {
                                colorPicker.alpha = 1.0f;
                            }
                            colorPicker.alphaPressed = true;
                            if (colorPicker.circlePressed) {
                                startColorChange(true);
                                x = getColor();
                                for (y = 0; y < colorPicker.this$1.this$0.currentThemeDesription.size(); y++) {
                                    ((ThemeDescription) colorPicker.this$1.this$0.currentThemeDesription.get(y)).setColor(x, false);
                                }
                                y = Color.red(x);
                                width = Color.green(x);
                                blue = Color.blue(x);
                                x = Color.alpha(x);
                                if (colorPicker.this$1.ignoreTextChange) {
                                    colorPicker.this$1.ignoreTextChange = true;
                                    editTextBoldCursor = colorPicker.colorEditText[0];
                                    stringBuilder = new StringBuilder();
                                    stringBuilder.append(TtmlNode.ANONYMOUS_REGION_ID);
                                    stringBuilder.append(y);
                                    editTextBoldCursor.setText(stringBuilder.toString());
                                    editTextBoldCursor2 = colorPicker.colorEditText[1];
                                    stringBuilder2 = new StringBuilder();
                                    stringBuilder2.append(TtmlNode.ANONYMOUS_REGION_ID);
                                    stringBuilder2.append(width);
                                    editTextBoldCursor2.setText(stringBuilder2.toString());
                                    editTextBoldCursor2 = colorPicker.colorEditText[2];
                                    stringBuilder3 = new StringBuilder();
                                    stringBuilder3.append(TtmlNode.ANONYMOUS_REGION_ID);
                                    stringBuilder3.append(blue);
                                    editTextBoldCursor2.setText(stringBuilder3.toString());
                                    editTextBoldCursor2 = colorPicker.colorEditText[3];
                                    stringBuilder3 = new StringBuilder();
                                    stringBuilder3.append(TtmlNode.ANONYMOUS_REGION_ID);
                                    stringBuilder3.append(x);
                                    editTextBoldCursor2.setText(stringBuilder3.toString());
                                    for (x = 0; x < 4; x++) {
                                        colorPicker.colorEditText[x].setSelection(colorPicker.colorEditText[x].length());
                                    }
                                    colorPicker.this$1.ignoreTextChange = false;
                                }
                                invalidate();
                            }
                            return true;
                        }
                        if (sqrt > ((double) colorPicker.colorWheelRadius)) {
                            sqrt = (double) colorPicker.colorWheelRadius;
                        }
                        colorPicker.circlePressed = true;
                        colorPicker.colorHSV[0] = (float) (Math.toDegrees(Math.atan2((double) i3, (double) i2)) + 180.0d);
                        colorPicker.colorHSV[1] = Math.max(0.0f, Math.min(1.0f, (float) (sqrt / ((double) colorPicker.colorWheelRadius))));
                        colorPicker.colorGradient = null;
                        colorPicker.alphaGradient = null;
                        f = ((float) (y - (i - colorPicker.colorWheelRadius))) / (((float) colorPicker.colorWheelRadius) * 2.0f);
                        if (f >= 0.0f) {
                            f = 0.0f;
                        } else if (f > 1.0f) {
                            f = 1.0f;
                        }
                        colorPicker.colorHSV[2] = f;
                        colorPicker.colorPressed = true;
                        colorPicker.alpha = 1.0f - (((float) (y - (i - colorPicker.colorWheelRadius))) / (((float) colorPicker.colorWheelRadius) * 2.0f));
                        if (colorPicker.alpha >= 0.0f) {
                            colorPicker.alpha = 0.0f;
                        } else if (colorPicker.alpha > 1.0f) {
                            colorPicker.alpha = 1.0f;
                        }
                        colorPicker.alphaPressed = true;
                        if (colorPicker.circlePressed) {
                            startColorChange(true);
                            x = getColor();
                            for (y = 0; y < colorPicker.this$1.this$0.currentThemeDesription.size(); y++) {
                                ((ThemeDescription) colorPicker.this$1.this$0.currentThemeDesription.get(y)).setColor(x, false);
                            }
                            y = Color.red(x);
                            width = Color.green(x);
                            blue = Color.blue(x);
                            x = Color.alpha(x);
                            if (colorPicker.this$1.ignoreTextChange) {
                                colorPicker.this$1.ignoreTextChange = true;
                                editTextBoldCursor = colorPicker.colorEditText[0];
                                stringBuilder = new StringBuilder();
                                stringBuilder.append(TtmlNode.ANONYMOUS_REGION_ID);
                                stringBuilder.append(y);
                                editTextBoldCursor.setText(stringBuilder.toString());
                                editTextBoldCursor2 = colorPicker.colorEditText[1];
                                stringBuilder2 = new StringBuilder();
                                stringBuilder2.append(TtmlNode.ANONYMOUS_REGION_ID);
                                stringBuilder2.append(width);
                                editTextBoldCursor2.setText(stringBuilder2.toString());
                                editTextBoldCursor2 = colorPicker.colorEditText[2];
                                stringBuilder3 = new StringBuilder();
                                stringBuilder3.append(TtmlNode.ANONYMOUS_REGION_ID);
                                stringBuilder3.append(blue);
                                editTextBoldCursor2.setText(stringBuilder3.toString());
                                editTextBoldCursor2 = colorPicker.colorEditText[3];
                                stringBuilder3 = new StringBuilder();
                                stringBuilder3.append(TtmlNode.ANONYMOUS_REGION_ID);
                                stringBuilder3.append(x);
                                editTextBoldCursor2.setText(stringBuilder3.toString());
                                for (x = 0; x < 4; x++) {
                                    colorPicker.colorEditText[x].setSelection(colorPicker.colorEditText[x].length());
                                }
                                colorPicker.this$1.ignoreTextChange = false;
                            }
                            invalidate();
                        }
                        return true;
                    case 1:
                        colorPicker.alphaPressed = false;
                        colorPicker.colorPressed = false;
                        colorPicker.circlePressed = false;
                        startColorChange(false);
                        break;
                    default:
                        break;
                }
                return super.onTouchEvent(motionEvent);
            }

            public void setColor(int i) {
                int red = Color.red(i);
                int green = Color.green(i);
                int blue = Color.blue(i);
                int alpha = Color.alpha(i);
                if (!this.this$1.ignoreTextChange) {
                    this.this$1.ignoreTextChange = true;
                    EditTextBoldCursor editTextBoldCursor = this.colorEditText[0];
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(TtmlNode.ANONYMOUS_REGION_ID);
                    stringBuilder.append(red);
                    editTextBoldCursor.setText(stringBuilder.toString());
                    EditTextBoldCursor editTextBoldCursor2 = this.colorEditText[1];
                    StringBuilder stringBuilder2 = new StringBuilder();
                    stringBuilder2.append(TtmlNode.ANONYMOUS_REGION_ID);
                    stringBuilder2.append(green);
                    editTextBoldCursor2.setText(stringBuilder2.toString());
                    editTextBoldCursor2 = this.colorEditText[2];
                    StringBuilder stringBuilder3 = new StringBuilder();
                    stringBuilder3.append(TtmlNode.ANONYMOUS_REGION_ID);
                    stringBuilder3.append(blue);
                    editTextBoldCursor2.setText(stringBuilder3.toString());
                    editTextBoldCursor2 = this.colorEditText[3];
                    stringBuilder3 = new StringBuilder();
                    stringBuilder3.append(TtmlNode.ANONYMOUS_REGION_ID);
                    stringBuilder3.append(alpha);
                    editTextBoldCursor2.setText(stringBuilder3.toString());
                    for (red = 0; red < 4; red++) {
                        this.colorEditText[red].setSelection(this.colorEditText[red].length());
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
                for (Context context2 : themeDescriptionArr) {
                    String currentKey = context2.getCurrentKey();
                    ArrayList arrayList = (ArrayList) this.itemsMap.get(currentKey);
                    if (arrayList == null) {
                        arrayList = new ArrayList();
                        this.itemsMap.put(currentKey, arrayList);
                        this.items.add(arrayList);
                    }
                    arrayList.add(context2);
                }
            }

            public int getItemCount() {
                return this.items.size();
            }

            public ArrayList<ThemeDescription> getItem(int i) {
                if (i >= 0) {
                    if (i < this.items.size()) {
                        return (ArrayList) this.items.get(i);
                    }
                }
                return 0;
            }

            public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                viewGroup = new TextColorThemeCell(this.context);
                viewGroup.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                return new Holder(viewGroup);
            }

            public void onBindViewHolder(ViewHolder viewHolder, int i) {
                int i2 = 0;
                ThemeDescription themeDescription = (ThemeDescription) ((ArrayList) this.items.get(i)).get(0);
                if (!themeDescription.getCurrentKey().equals(Theme.key_chat_wallpaper)) {
                    i2 = themeDescription.getSetColor();
                }
                ((TextColorThemeCell) viewHolder.itemView).setTextAndColor(themeDescription.getTitle(), i2);
            }
        }

        protected boolean canDismissWithSwipe() {
            return false;
        }

        public EditorAlert(ThemeEditorView themeEditorView, Context context, ThemeDescription[] themeDescriptionArr) {
            final ThemeEditorView themeEditorView2 = themeEditorView;
            Context context2 = context;
            this.this$0 = themeEditorView2;
            super(context2, true);
            this.shadowDrawable = context.getResources().getDrawable(C0446R.drawable.sheet_shadow).mutate();
            this.containerView = new FrameLayout(context2) {
                private boolean ignoreLayout = null;

                public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                    if (motionEvent.getAction() != 0 || EditorAlert.this.scrollOffsetY == 0 || motionEvent.getY() >= ((float) EditorAlert.this.scrollOffsetY)) {
                        return super.onInterceptTouchEvent(motionEvent);
                    }
                    EditorAlert.this.dismiss();
                    return true;
                }

                public boolean onTouchEvent(MotionEvent motionEvent) {
                    return (EditorAlert.this.isDismissed() || super.onTouchEvent(motionEvent) == null) ? null : true;
                }

                protected void onMeasure(int i, int i2) {
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
                            EditorAlert.this.scrollOffsetY = EditorAlert.this.listView.getPaddingTop();
                            EditorAlert.this.listView.setTopGlowOffset(EditorAlert.this.scrollOffsetY);
                            EditorAlert.this.colorPicker.setTranslationY((float) EditorAlert.this.scrollOffsetY);
                            EditorAlert.this.previousScrollPosition = 0;
                        }
                        this.ignoreLayout = false;
                    }
                    super.onMeasure(i, MeasureSpec.makeMeasureSpec(i2, NUM));
                }

                protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
                    super.onLayout(z, i, i2, i3, i4);
                    EditorAlert.this.updateLayout();
                }

                public void requestLayout() {
                    if (!this.ignoreLayout) {
                        super.requestLayout();
                    }
                }

                protected void onDraw(Canvas canvas) {
                    EditorAlert.this.shadowDrawable.setBounds(0, EditorAlert.this.scrollOffsetY - EditorAlert.backgroundPaddingTop, getMeasuredWidth(), getMeasuredHeight());
                    EditorAlert.this.shadowDrawable.draw(canvas);
                }
            };
            this.containerView.setWillNotDraw(false);
            this.containerView.setPadding(backgroundPaddingLeft, 0, backgroundPaddingLeft, 0);
            this.listView = new RecyclerListView(context2);
            this.listView.setPadding(0, 0, 0, AndroidUtilities.dp(48.0f));
            this.listView.setClipToPadding(false);
            RecyclerListView recyclerListView = this.listView;
            LayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            this.layoutManager = linearLayoutManager;
            recyclerListView.setLayoutManager(linearLayoutManager);
            this.listView.setHorizontalScrollBarEnabled(false);
            this.listView.setVerticalScrollBarEnabled(false);
            this.containerView.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
            recyclerListView = this.listView;
            Adapter listAdapter = new ListAdapter(context2, themeDescriptionArr);
            this.listAdapter = listAdapter;
            recyclerListView.setAdapter(listAdapter);
            this.listView.setGlowColor(-657673);
            this.listView.setItemAnimator(null);
            this.listView.setLayoutAnimation(null);
            this.listView.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(View view, int i) {
                    EditorAlert.this.this$0.currentThemeDesription = EditorAlert.this.listAdapter.getItem(i);
                    EditorAlert.this.this$0.currentThemeDesriptionPosition = i;
                    for (view = null; view < EditorAlert.this.this$0.currentThemeDesription.size(); view++) {
                        ThemeDescription themeDescription = (ThemeDescription) EditorAlert.this.this$0.currentThemeDesription.get(view);
                        if (themeDescription.getCurrentKey().equals(Theme.key_chat_wallpaper)) {
                            EditorAlert.this.this$0.wallpaperUpdater.showAlert(true);
                            return;
                        }
                        themeDescription.startEditing();
                        if (view == null) {
                            EditorAlert.this.colorPicker.setColor(themeDescription.getCurrentColor());
                        }
                    }
                    EditorAlert.this.setColorPickerVisible(true);
                }
            });
            this.listView.setOnScrollListener(new OnScrollListener() {
                public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                    EditorAlert.this.updateLayout();
                }
            });
            this.colorPicker = new ColorPicker(this, context2);
            this.colorPicker.setVisibility(8);
            this.containerView.addView(this.colorPicker, LayoutHelper.createFrame(-1, -1, 1));
            this.shadow = new View(context2);
            this.shadow.setBackgroundResource(C0446R.drawable.header_shadow_reverse);
            this.containerView.addView(this.shadow, LayoutHelper.createFrame(-1, 3.0f, 83, 0.0f, 0.0f, 0.0f, 48.0f));
            this.bottomSaveLayout = new FrameLayout(context2);
            this.bottomSaveLayout.setBackgroundColor(-1);
            this.containerView.addView(this.bottomSaveLayout, LayoutHelper.createFrame(-1, 48, 83));
            View textView = new TextView(context2);
            textView.setTextSize(1, 14.0f);
            textView.setTextColor(-15095832);
            textView.setGravity(17);
            textView.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_AUDIO_SELECTOR_COLOR, 0));
            textView.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
            textView.setText(LocaleController.getString("CloseEditor", C0446R.string.CloseEditor).toUpperCase());
            textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.bottomSaveLayout.addView(textView, LayoutHelper.createFrame(-2, -1, 51));
            textView.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    EditorAlert.this.dismiss();
                }
            });
            textView = new TextView(context2);
            textView.setTextSize(1, 14.0f);
            textView.setTextColor(-15095832);
            textView.setGravity(17);
            textView.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_AUDIO_SELECTOR_COLOR, 0));
            textView.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
            textView.setText(LocaleController.getString("SaveTheme", C0446R.string.SaveTheme).toUpperCase());
            textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.bottomSaveLayout.addView(textView, LayoutHelper.createFrame(-2, -1, 53));
            textView.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    Theme.saveCurrentTheme(EditorAlert.this.this$0.currentThemeName, true);
                    EditorAlert.this.setOnDismissListener(null);
                    EditorAlert.this.dismiss();
                    EditorAlert.this.this$0.close();
                }
            });
            this.bottomLayout = new FrameLayout(context2);
            this.bottomLayout.setVisibility(8);
            this.bottomLayout.setBackgroundColor(-1);
            this.containerView.addView(this.bottomLayout, LayoutHelper.createFrame(-1, 48, 83));
            this.cancelButton = new TextView(context2);
            this.cancelButton.setTextSize(1, 14.0f);
            this.cancelButton.setTextColor(-15095832);
            this.cancelButton.setGravity(17);
            this.cancelButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_AUDIO_SELECTOR_COLOR, 0));
            this.cancelButton.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
            this.cancelButton.setText(LocaleController.getString("Cancel", C0446R.string.Cancel).toUpperCase());
            this.cancelButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.bottomLayout.addView(this.cancelButton, LayoutHelper.createFrame(-2, -1, 51));
            this.cancelButton.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    for (int i = 0; i < EditorAlert.this.this$0.currentThemeDesription.size(); i++) {
                        ((ThemeDescription) EditorAlert.this.this$0.currentThemeDesription.get(i)).setPreviousColor();
                    }
                    EditorAlert.this.setColorPickerVisible(false);
                }
            });
            textView = new LinearLayout(context2);
            textView.setOrientation(0);
            this.bottomLayout.addView(textView, LayoutHelper.createFrame(-2, -1, 53));
            this.defaultButtom = new TextView(context2);
            this.defaultButtom.setTextSize(1, 14.0f);
            this.defaultButtom.setTextColor(-15095832);
            this.defaultButtom.setGravity(17);
            this.defaultButtom.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_AUDIO_SELECTOR_COLOR, 0));
            this.defaultButtom.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
            this.defaultButtom.setText(LocaleController.getString("Default", C0446R.string.Default).toUpperCase());
            this.defaultButtom.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            textView.addView(this.defaultButtom, LayoutHelper.createFrame(-2, -1, 51));
            this.defaultButtom.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    for (int i = 0; i < EditorAlert.this.this$0.currentThemeDesription.size(); i++) {
                        ((ThemeDescription) EditorAlert.this.this$0.currentThemeDesription.get(i)).setDefaultColor();
                    }
                    EditorAlert.this.setColorPickerVisible(false);
                }
            });
            View textView2 = new TextView(context2);
            textView2.setTextSize(1, 14.0f);
            textView2.setTextColor(-15095832);
            textView2.setGravity(17);
            textView2.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_AUDIO_SELECTOR_COLOR, 0));
            textView2.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
            textView2.setText(LocaleController.getString("Save", C0446R.string.Save).toUpperCase());
            textView2.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            textView.addView(textView2, LayoutHelper.createFrame(-2, -1, 51));
            textView2.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    EditorAlert.this.setColorPickerVisible(false);
                }
            });
        }

        private void setColorPickerVisible(boolean z) {
            if (z) {
                this.animationInProgress = true;
                this.colorPicker.setVisibility(0);
                this.bottomLayout.setVisibility(0);
                this.colorPicker.setAlpha(0.0f);
                this.bottomLayout.setAlpha(0.0f);
                z = new AnimatorSet();
                r5 = new Animator[5];
                r5[0] = ObjectAnimator.ofFloat(this.colorPicker, "alpha", new float[]{1.0f});
                r5[1] = ObjectAnimator.ofFloat(this.bottomLayout, "alpha", new float[]{1.0f});
                r5[2] = ObjectAnimator.ofFloat(this.listView, "alpha", new float[]{0.0f});
                r5[3] = ObjectAnimator.ofFloat(this.bottomSaveLayout, "alpha", new float[]{0.0f});
                r5[4] = ObjectAnimator.ofInt(this, "scrollOffsetY", new int[]{this.listView.getPaddingTop()});
                z.playTogether(r5);
                z.setDuration(150);
                z.setInterpolator(this.this$0.decelerateInterpolator);
                z.addListener(new C13299());
                z.start();
                this.previousScrollPosition = this.scrollOffsetY;
                return;
            }
            if (this.this$0.parentActivity) {
                ((LaunchActivity) this.this$0.parentActivity).rebuildAllFragments(false);
            }
            Theme.saveCurrentTheme(this.this$0.currentThemeName, false);
            AndroidUtilities.hideKeyboard(getCurrentFocus());
            this.animationInProgress = true;
            this.listView.setVisibility(0);
            this.bottomSaveLayout.setVisibility(0);
            this.listView.setAlpha(0.0f);
            z = new AnimatorSet();
            r5 = new Animator[5];
            r5[0] = ObjectAnimator.ofFloat(this.colorPicker, "alpha", new float[]{0.0f});
            r5[1] = ObjectAnimator.ofFloat(this.bottomLayout, "alpha", new float[]{0.0f});
            r5[2] = ObjectAnimator.ofFloat(this.listView, "alpha", new float[]{1.0f});
            r5[3] = ObjectAnimator.ofFloat(this.bottomSaveLayout, "alpha", new float[]{1.0f});
            r5[4] = ObjectAnimator.ofInt(this, "scrollOffsetY", new int[]{this.previousScrollPosition});
            z.playTogether(r5);
            z.setDuration(150);
            z.setInterpolator(this.this$0.decelerateInterpolator);
            z.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    EditorAlert.this.colorPicker.setVisibility(8);
                    EditorAlert.this.bottomLayout.setVisibility(8);
                    EditorAlert.this.animationInProgress = false;
                }
            });
            z.start();
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
            return C0542C.PRIORITY_DOWNLOAD;
        }

        @SuppressLint({"NewApi"})
        private void updateLayout() {
            if (this.listView.getChildCount() > 0 && this.listView.getVisibility() == 0) {
                if (!this.animationInProgress) {
                    int top;
                    View childAt = this.listView.getChildAt(0);
                    Holder holder = (Holder) this.listView.findContainingViewHolder(childAt);
                    if (this.listView.getVisibility() == 0) {
                        if (!this.animationInProgress) {
                            top = childAt.getTop() - AndroidUtilities.dp(8.0f);
                            if (top > 0 || holder == null || holder.getAdapterPosition() != 0) {
                                top = 0;
                            }
                            if (this.scrollOffsetY != top) {
                                setScrollOffsetY(top);
                            }
                        }
                    }
                    top = this.listView.getPaddingTop();
                    if (top > 0) {
                    }
                    top = 0;
                    if (this.scrollOffsetY != top) {
                        setScrollOffsetY(top);
                    }
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
            if (this.windowView != null) {
                try {
                    this.windowManager.removeViewImmediate(this.windowView);
                    this.windowView = null;
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
                try {
                    if (this.editorAlert != null) {
                        this.editorAlert.dismiss();
                        this.editorAlert = null;
                    }
                } catch (Throwable e2) {
                    FileLog.m3e(e2);
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

            /* renamed from: org.telegram.ui.Components.ThemeEditorView$1$1 */
            class C13181 implements OnDismissListener {
                public void onDismiss(DialogInterface dialogInterface) {
                }

                C13181() {
                }
            }

            /* renamed from: org.telegram.ui.Components.ThemeEditorView$1$2 */
            class C13192 implements OnDismissListener {
                C13192() {
                }

                public void onDismiss(DialogInterface dialogInterface) {
                    ThemeEditorView.this.editorAlert = null;
                    ThemeEditorView.this.show();
                }
            }

            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                return true;
            }

            public boolean onTouchEvent(MotionEvent motionEvent) {
                float rawX = motionEvent.getRawX();
                float rawY = motionEvent.getRawY();
                if (motionEvent.getAction() == 0) {
                    this.startX = rawX;
                    this.startY = rawY;
                } else if (motionEvent.getAction() != 2 || this.dragging) {
                    if (motionEvent.getAction() == 1 && !this.dragging && ThemeEditorView.this.editorAlert == null) {
                        ActionBarLayout actionBarLayout = ((LaunchActivity) ThemeEditorView.this.parentActivity).getActionBarLayout();
                        if (!actionBarLayout.fragmentsStack.isEmpty()) {
                            ThemeDescription[] themeDescriptions = ((BaseFragment) actionBarLayout.fragmentsStack.get(actionBarLayout.fragmentsStack.size() - 1)).getThemeDescriptions();
                            if (themeDescriptions != null) {
                                ThemeEditorView.this.editorAlert = new EditorAlert(ThemeEditorView.this, ThemeEditorView.this.parentActivity, themeDescriptions);
                                ThemeEditorView.this.editorAlert.setOnDismissListener(new C13181());
                                ThemeEditorView.this.editorAlert.setOnDismissListener(new C13192());
                                ThemeEditorView.this.editorAlert.show();
                                ThemeEditorView.this.hide();
                            }
                        }
                    }
                } else if (Math.abs(this.startX - rawX) >= AndroidUtilities.getPixelsInCM(0.3f, true) || Math.abs(this.startY - rawY) >= AndroidUtilities.getPixelsInCM(0.3f, false)) {
                    this.dragging = true;
                    this.startX = rawX;
                    this.startY = rawY;
                }
                if (this.dragging) {
                    if (motionEvent.getAction() == 2) {
                        motionEvent = rawX - this.startX;
                        float f = rawY - this.startY;
                        LayoutParams access$2700 = ThemeEditorView.this.windowLayoutParams;
                        access$2700.x = (int) (((float) access$2700.x) + motionEvent);
                        motionEvent = ThemeEditorView.this.windowLayoutParams;
                        motionEvent.y = (int) (((float) motionEvent.y) + f);
                        motionEvent = ThemeEditorView.this.editorWidth / 2;
                        int i = -motionEvent;
                        if (ThemeEditorView.this.windowLayoutParams.x < i) {
                            ThemeEditorView.this.windowLayoutParams.x = i;
                        } else if (ThemeEditorView.this.windowLayoutParams.x > (AndroidUtilities.displaySize.x - ThemeEditorView.this.windowLayoutParams.width) + motionEvent) {
                            ThemeEditorView.this.windowLayoutParams.x = (AndroidUtilities.displaySize.x - ThemeEditorView.this.windowLayoutParams.width) + motionEvent;
                        }
                        float f2 = 1.0f;
                        if (ThemeEditorView.this.windowLayoutParams.x < 0) {
                            f2 = 1.0f + ((((float) ThemeEditorView.this.windowLayoutParams.x) / ((float) motionEvent)) * 0.5f);
                        } else if (ThemeEditorView.this.windowLayoutParams.x > AndroidUtilities.displaySize.x - ThemeEditorView.this.windowLayoutParams.width) {
                            f2 = 1.0f - ((((float) ((ThemeEditorView.this.windowLayoutParams.x - AndroidUtilities.displaySize.x) + ThemeEditorView.this.windowLayoutParams.width)) / ((float) motionEvent)) * 0.5f);
                        }
                        if (ThemeEditorView.this.windowView.getAlpha() != f2) {
                            ThemeEditorView.this.windowView.setAlpha(f2);
                        }
                        if (ThemeEditorView.this.windowLayoutParams.y < null) {
                            ThemeEditorView.this.windowLayoutParams.y = 0;
                        } else if (ThemeEditorView.this.windowLayoutParams.y > (AndroidUtilities.displaySize.y - ThemeEditorView.this.windowLayoutParams.height) + 0) {
                            ThemeEditorView.this.windowLayoutParams.y = (AndroidUtilities.displaySize.y - ThemeEditorView.this.windowLayoutParams.height) + 0;
                        }
                        ThemeEditorView.this.windowManager.updateViewLayout(ThemeEditorView.this.windowView, ThemeEditorView.this.windowLayoutParams);
                        this.startX = rawX;
                        this.startY = rawY;
                    } else if (motionEvent.getAction() == 1) {
                        this.dragging = false;
                        ThemeEditorView.this.animateToBoundsMaybe();
                    }
                }
                return true;
            }
        };
        this.windowView.setBackgroundResource(C0446R.drawable.theme_picker);
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
            this.wallpaperUpdater = new WallpaperUpdater(activity, new WallpaperUpdaterDelegate() {
                public void didSelectWallpaper(File file, Bitmap bitmap) {
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
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
    }

    private void showWithAnimation() {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.windowView, "alpha", new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(this.windowView, "scaleX", new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(this.windowView, "scaleY", new float[]{0.0f, 1.0f})});
        animatorSet.setInterpolator(this.decelerateInterpolator);
        animatorSet.setDuration(150);
        animatorSet.start();
    }

    private static int getSideCoord(boolean z, int i, float f, int i2) {
        int i3;
        if (z) {
            i3 = AndroidUtilities.displaySize.x - i2;
        } else {
            i3 = (AndroidUtilities.displaySize.y - i2) - ActionBar.getCurrentActionBarHeight();
        }
        if (i == 0) {
            i = AndroidUtilities.dp(10.0f);
        } else if (i == 1) {
            i = i3 - AndroidUtilities.dp(10.0f);
        } else {
            i = Math.round(((float) (i3 - AndroidUtilities.dp(NUM))) * f) + AndroidUtilities.dp(10.0f);
        }
        return !z ? i + ActionBar.getCurrentActionBarHeight() : i;
    }

    private void hide() {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
        /*
        r7 = this;
        r0 = r7.parentActivity;
        if (r0 != 0) goto L_0x0005;
    L_0x0004:
        return;
    L_0x0005:
        r0 = new android.animation.AnimatorSet;	 Catch:{ Exception -> 0x0057 }
        r0.<init>();	 Catch:{ Exception -> 0x0057 }
        r1 = 3;	 Catch:{ Exception -> 0x0057 }
        r1 = new android.animation.Animator[r1];	 Catch:{ Exception -> 0x0057 }
        r2 = 0;	 Catch:{ Exception -> 0x0057 }
        r3 = r7.windowView;	 Catch:{ Exception -> 0x0057 }
        r4 = "alpha";	 Catch:{ Exception -> 0x0057 }
        r5 = 2;	 Catch:{ Exception -> 0x0057 }
        r6 = new float[r5];	 Catch:{ Exception -> 0x0057 }
        r6 = {NUM, 0};	 Catch:{ Exception -> 0x0057 }
        r3 = android.animation.ObjectAnimator.ofFloat(r3, r4, r6);	 Catch:{ Exception -> 0x0057 }
        r1[r2] = r3;	 Catch:{ Exception -> 0x0057 }
        r2 = r7.windowView;	 Catch:{ Exception -> 0x0057 }
        r3 = "scaleX";	 Catch:{ Exception -> 0x0057 }
        r4 = new float[r5];	 Catch:{ Exception -> 0x0057 }
        r4 = {NUM, 0};	 Catch:{ Exception -> 0x0057 }
        r2 = android.animation.ObjectAnimator.ofFloat(r2, r3, r4);	 Catch:{ Exception -> 0x0057 }
        r3 = 1;	 Catch:{ Exception -> 0x0057 }
        r1[r3] = r2;	 Catch:{ Exception -> 0x0057 }
        r2 = r7.windowView;	 Catch:{ Exception -> 0x0057 }
        r4 = "scaleY";	 Catch:{ Exception -> 0x0057 }
        r6 = new float[r5];	 Catch:{ Exception -> 0x0057 }
        r6 = {NUM, 0};	 Catch:{ Exception -> 0x0057 }
        r2 = android.animation.ObjectAnimator.ofFloat(r2, r4, r6);	 Catch:{ Exception -> 0x0057 }
        r1[r5] = r2;	 Catch:{ Exception -> 0x0057 }
        r0.playTogether(r1);	 Catch:{ Exception -> 0x0057 }
        r1 = r7.decelerateInterpolator;	 Catch:{ Exception -> 0x0057 }
        r0.setInterpolator(r1);	 Catch:{ Exception -> 0x0057 }
        r1 = 150; // 0x96 float:2.1E-43 double:7.4E-322;	 Catch:{ Exception -> 0x0057 }
        r0.setDuration(r1);	 Catch:{ Exception -> 0x0057 }
        r1 = new org.telegram.ui.Components.ThemeEditorView$3;	 Catch:{ Exception -> 0x0057 }
        r1.<init>();	 Catch:{ Exception -> 0x0057 }
        r0.addListener(r1);	 Catch:{ Exception -> 0x0057 }
        r0.start();	 Catch:{ Exception -> 0x0057 }
        r7.hidden = r3;	 Catch:{ Exception -> 0x0057 }
    L_0x0057:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ThemeEditorView.hide():void");
    }

    private void show() {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
        /*
        r3 = this;
        r0 = r3.parentActivity;
        if (r0 != 0) goto L_0x0005;
    L_0x0004:
        return;
    L_0x0005:
        r0 = r3.windowManager;	 Catch:{ Exception -> 0x0014 }
        r1 = r3.windowView;	 Catch:{ Exception -> 0x0014 }
        r2 = r3.windowLayoutParams;	 Catch:{ Exception -> 0x0014 }
        r0.addView(r1, r2);	 Catch:{ Exception -> 0x0014 }
        r0 = 0;	 Catch:{ Exception -> 0x0014 }
        r3.hidden = r0;	 Catch:{ Exception -> 0x0014 }
        r3.showWithAnimation();	 Catch:{ Exception -> 0x0014 }
    L_0x0014:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ThemeEditorView.show():void");
    }

    public void close() {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
        /*
        r2 = this;
        r0 = r2.windowManager;	 Catch:{ Exception -> 0x0007 }
        r1 = r2.windowView;	 Catch:{ Exception -> 0x0007 }
        r0.removeView(r1);	 Catch:{ Exception -> 0x0007 }
    L_0x0007:
        r0 = 0;
        r2.parentActivity = r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ThemeEditorView.close():void");
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
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        if (this.wallpaperUpdater != null) {
            this.wallpaperUpdater.onActivityResult(i, i2, intent);
        }
    }

    private void animateToBoundsMaybe() {
        Collection collection;
        boolean z;
        AnimatorSet animatorSet;
        int sideCoord = getSideCoord(true, 0, 0.0f, this.editorWidth);
        int sideCoord2 = getSideCoord(true, 1, 0.0f, this.editorWidth);
        int sideCoord3 = getSideCoord(false, 0, 0.0f, this.editorHeight);
        int sideCoord4 = getSideCoord(false, 1, 0.0f, this.editorHeight);
        Editor edit = this.preferences.edit();
        int dp = AndroidUtilities.dp(20.0f);
        if (Math.abs(sideCoord - this.windowLayoutParams.x) > dp) {
            if (this.windowLayoutParams.x >= 0 || this.windowLayoutParams.x <= (-this.editorWidth) / 4) {
                ArrayList arrayList;
                if (Math.abs(sideCoord2 - this.windowLayoutParams.x) > dp) {
                    if (this.windowLayoutParams.x <= AndroidUtilities.displaySize.x - this.editorWidth || this.windowLayoutParams.x >= AndroidUtilities.displaySize.x - ((this.editorWidth / 4) * 3)) {
                        if (this.windowView.getAlpha() != 1.0f) {
                            arrayList = new ArrayList();
                            if (this.windowLayoutParams.x < 0) {
                                arrayList.add(ObjectAnimator.ofInt(this, "x", new int[]{-this.editorWidth}));
                            } else {
                                arrayList.add(ObjectAnimator.ofInt(this, "x", new int[]{AndroidUtilities.displaySize.x}));
                            }
                            collection = arrayList;
                            z = true;
                            if (!z) {
                                if (Math.abs(sideCoord3 - this.windowLayoutParams.y) > dp) {
                                    if (this.windowLayoutParams.y > ActionBar.getCurrentActionBarHeight()) {
                                        if (Math.abs(sideCoord4 - this.windowLayoutParams.y) > dp) {
                                            if (collection == null) {
                                                collection = new ArrayList();
                                            }
                                            edit.putInt("sidey", 1);
                                            collection.add(ObjectAnimator.ofInt(this, "y", new int[]{sideCoord4}));
                                        } else {
                                            edit.putFloat("py", ((float) (this.windowLayoutParams.y - sideCoord3)) / ((float) (sideCoord4 - sideCoord3)));
                                            edit.putInt("sidey", 2);
                                        }
                                        edit.commit();
                                    }
                                }
                                if (collection == null) {
                                    collection = new ArrayList();
                                }
                                edit.putInt("sidey", 0);
                                collection.add(ObjectAnimator.ofInt(this, "y", new int[]{sideCoord3}));
                                edit.commit();
                            }
                            if (collection == null) {
                                if (this.decelerateInterpolator == null) {
                                    this.decelerateInterpolator = new DecelerateInterpolator();
                                }
                                animatorSet = new AnimatorSet();
                                animatorSet.setInterpolator(this.decelerateInterpolator);
                                animatorSet.setDuration(150);
                                if (z) {
                                    collection.add(ObjectAnimator.ofFloat(this.windowView, "alpha", new float[]{0.0f}));
                                    animatorSet.addListener(new C13224());
                                }
                                animatorSet.playTogether(collection);
                                animatorSet.start();
                            }
                        }
                        edit.putFloat("px", ((float) (this.windowLayoutParams.x - sideCoord)) / ((float) (sideCoord2 - sideCoord)));
                        edit.putInt("sidex", 2);
                        arrayList = null;
                        collection = arrayList;
                        z = false;
                        if (z) {
                            if (Math.abs(sideCoord3 - this.windowLayoutParams.y) > dp) {
                                if (this.windowLayoutParams.y > ActionBar.getCurrentActionBarHeight()) {
                                    if (Math.abs(sideCoord4 - this.windowLayoutParams.y) > dp) {
                                        edit.putFloat("py", ((float) (this.windowLayoutParams.y - sideCoord3)) / ((float) (sideCoord4 - sideCoord3)));
                                        edit.putInt("sidey", 2);
                                    } else {
                                        if (collection == null) {
                                            collection = new ArrayList();
                                        }
                                        edit.putInt("sidey", 1);
                                        collection.add(ObjectAnimator.ofInt(this, "y", new int[]{sideCoord4}));
                                    }
                                    edit.commit();
                                }
                            }
                            if (collection == null) {
                                collection = new ArrayList();
                            }
                            edit.putInt("sidey", 0);
                            collection.add(ObjectAnimator.ofInt(this, "y", new int[]{sideCoord3}));
                            edit.commit();
                        }
                        if (collection == null) {
                            if (this.decelerateInterpolator == null) {
                                this.decelerateInterpolator = new DecelerateInterpolator();
                            }
                            animatorSet = new AnimatorSet();
                            animatorSet.setInterpolator(this.decelerateInterpolator);
                            animatorSet.setDuration(150);
                            if (z) {
                                collection.add(ObjectAnimator.ofFloat(this.windowView, "alpha", new float[]{0.0f}));
                                animatorSet.addListener(new C13224());
                            }
                            animatorSet.playTogether(collection);
                            animatorSet.start();
                        }
                    }
                }
                arrayList = new ArrayList();
                edit.putInt("sidex", 1);
                if (this.windowView.getAlpha() != 1.0f) {
                    arrayList.add(ObjectAnimator.ofFloat(this.windowView, "alpha", new float[]{1.0f}));
                }
                arrayList.add(ObjectAnimator.ofInt(this, "x", new int[]{sideCoord2}));
                collection = arrayList;
                z = false;
                if (z) {
                    if (Math.abs(sideCoord3 - this.windowLayoutParams.y) > dp) {
                        if (this.windowLayoutParams.y > ActionBar.getCurrentActionBarHeight()) {
                            if (Math.abs(sideCoord4 - this.windowLayoutParams.y) > dp) {
                                if (collection == null) {
                                    collection = new ArrayList();
                                }
                                edit.putInt("sidey", 1);
                                collection.add(ObjectAnimator.ofInt(this, "y", new int[]{sideCoord4}));
                            } else {
                                edit.putFloat("py", ((float) (this.windowLayoutParams.y - sideCoord3)) / ((float) (sideCoord4 - sideCoord3)));
                                edit.putInt("sidey", 2);
                            }
                            edit.commit();
                        }
                    }
                    if (collection == null) {
                        collection = new ArrayList();
                    }
                    edit.putInt("sidey", 0);
                    collection.add(ObjectAnimator.ofInt(this, "y", new int[]{sideCoord3}));
                    edit.commit();
                }
                if (collection == null) {
                    if (this.decelerateInterpolator == null) {
                        this.decelerateInterpolator = new DecelerateInterpolator();
                    }
                    animatorSet = new AnimatorSet();
                    animatorSet.setInterpolator(this.decelerateInterpolator);
                    animatorSet.setDuration(150);
                    if (z) {
                        collection.add(ObjectAnimator.ofFloat(this.windowView, "alpha", new float[]{0.0f}));
                        animatorSet.addListener(new C13224());
                    }
                    animatorSet.playTogether(collection);
                    animatorSet.start();
                }
            }
        }
        collection = new ArrayList();
        edit.putInt("sidex", 0);
        if (this.windowView.getAlpha() != 1.0f) {
            collection.add(ObjectAnimator.ofFloat(this.windowView, "alpha", new float[]{1.0f}));
        }
        collection.add(ObjectAnimator.ofInt(this, "x", new int[]{sideCoord}));
        z = false;
        if (z) {
            if (Math.abs(sideCoord3 - this.windowLayoutParams.y) > dp) {
                if (this.windowLayoutParams.y > ActionBar.getCurrentActionBarHeight()) {
                    if (Math.abs(sideCoord4 - this.windowLayoutParams.y) > dp) {
                        edit.putFloat("py", ((float) (this.windowLayoutParams.y - sideCoord3)) / ((float) (sideCoord4 - sideCoord3)));
                        edit.putInt("sidey", 2);
                    } else {
                        if (collection == null) {
                            collection = new ArrayList();
                        }
                        edit.putInt("sidey", 1);
                        collection.add(ObjectAnimator.ofInt(this, "y", new int[]{sideCoord4}));
                    }
                    edit.commit();
                }
            }
            if (collection == null) {
                collection = new ArrayList();
            }
            edit.putInt("sidey", 0);
            collection.add(ObjectAnimator.ofInt(this, "y", new int[]{sideCoord3}));
            edit.commit();
        }
        if (collection == null) {
            if (this.decelerateInterpolator == null) {
                this.decelerateInterpolator = new DecelerateInterpolator();
            }
            animatorSet = new AnimatorSet();
            animatorSet.setInterpolator(this.decelerateInterpolator);
            animatorSet.setDuration(150);
            if (z) {
                collection.add(ObjectAnimator.ofFloat(this.windowView, "alpha", new float[]{0.0f}));
                animatorSet.addListener(new C13224());
            }
            animatorSet.playTogether(collection);
            animatorSet.start();
        }
    }

    public int getX() {
        return this.windowLayoutParams.x;
    }

    public int getY() {
        return this.windowLayoutParams.y;
    }

    @Keep
    public void setX(int i) {
        this.windowLayoutParams.x = i;
        this.windowManager.updateViewLayout(this.windowView, this.windowLayoutParams);
    }

    @Keep
    public void setY(int i) {
        this.windowLayoutParams.y = i;
        this.windowManager.updateViewLayout(this.windowView, this.windowLayoutParams);
    }
}
