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
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.exoplayer2.C0539C;
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
    private static volatile ThemeEditorView Instance = null;
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
    class C13153 extends AnimatorListenerAdapter {
        C13153() {
        }

        public void onAnimationEnd(Animator animation) {
            if (ThemeEditorView.this.windowView != null) {
                ThemeEditorView.this.windowManager.removeView(ThemeEditorView.this.windowView);
            }
        }
    }

    /* renamed from: org.telegram.ui.Components.ThemeEditorView$4 */
    class C13164 extends AnimatorListenerAdapter {
        C13164() {
        }

        public void onAnimationEnd(Animator animation) {
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
        class C13239 extends AnimatorListenerAdapter {
            C13239() {
            }

            public void onAnimationEnd(Animator animation) {
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
                this.circleDrawable = context.getResources().getDrawable(R.drawable.knob_shadow).mutate();
                this.colorWheelPaint = new Paint();
                this.colorWheelPaint.setAntiAlias(true);
                this.colorWheelPaint.setDither(true);
                this.valueSliderPaint = new Paint();
                this.valueSliderPaint.setAntiAlias(true);
                this.valueSliderPaint.setDither(true);
                this.linearLayout = new LinearLayout(context2);
                this.linearLayout.setOrientation(0);
                addView(this.linearLayout, LayoutHelper.createFrame(-2, -2, 49));
                int a = 0;
                while (a < 4) {
                    r0.colorEditText[a] = new EditTextBoldCursor(context2);
                    r0.colorEditText[a].setInputType(2);
                    r0.colorEditText[a].setTextColor(-14606047);
                    r0.colorEditText[a].setCursorColor(-14606047);
                    r0.colorEditText[a].setCursorSize(AndroidUtilities.dp(20.0f));
                    r0.colorEditText[a].setCursorWidth(1.5f);
                    r0.colorEditText[a].setTextSize(1, 18.0f);
                    r0.colorEditText[a].setBackgroundDrawable(Theme.createEditTextDrawable(context2, true));
                    r0.colorEditText[a].setMaxLines(1);
                    r0.colorEditText[a].setTag(Integer.valueOf(a));
                    r0.colorEditText[a].setGravity(17);
                    if (a == 0) {
                        r0.colorEditText[a].setHint("red");
                    } else if (a == 1) {
                        r0.colorEditText[a].setHint("green");
                    } else if (a == 2) {
                        r0.colorEditText[a].setHint("blue");
                    } else if (a == 3) {
                        r0.colorEditText[a].setHint("alpha");
                    }
                    r0.colorEditText[a].setImeOptions((a == 3 ? 6 : 5) | 268435456);
                    r0.colorEditText[a].setFilters(new InputFilter[]{new LengthFilter(3)});
                    final int num = a;
                    r0.linearLayout.addView(r0.colorEditText[a], LayoutHelper.createLinear(55, 36, 0.0f, 0.0f, a != 3 ? 16.0f : 0.0f, 0.0f));
                    r0.colorEditText[a].addTextChangedListener(new TextWatcher() {
                        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                        }

                        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                        }

                        public void afterTextChanged(Editable editable) {
                            if (!ColorPicker.this.this$1.ignoreTextChange) {
                                ColorPicker.this.this$1.ignoreTextChange = true;
                                int color = Utilities.parseInt(editable.toString()).intValue();
                                EditTextBoldCursor editTextBoldCursor;
                                StringBuilder stringBuilder;
                                if (color < 0) {
                                    color = 0;
                                    editTextBoldCursor = ColorPicker.this.colorEditText[num];
                                    stringBuilder = new StringBuilder();
                                    stringBuilder.append(TtmlNode.ANONYMOUS_REGION_ID);
                                    stringBuilder.append(0);
                                    editTextBoldCursor.setText(stringBuilder.toString());
                                    ColorPicker.this.colorEditText[num].setSelection(ColorPicker.this.colorEditText[num].length());
                                } else if (color > 255) {
                                    color = 255;
                                    editTextBoldCursor = ColorPicker.this.colorEditText[num];
                                    stringBuilder = new StringBuilder();
                                    stringBuilder.append(TtmlNode.ANONYMOUS_REGION_ID);
                                    stringBuilder.append(255);
                                    editTextBoldCursor.setText(stringBuilder.toString());
                                    ColorPicker.this.colorEditText[num].setSelection(ColorPicker.this.colorEditText[num].length());
                                }
                                int currentColor = ColorPicker.this.getColor();
                                if (num == 2) {
                                    currentColor = (currentColor & -256) | (color & 255);
                                } else if (num == 1) {
                                    currentColor = (-65281 & currentColor) | ((color & 255) << 8);
                                } else if (num == 0) {
                                    currentColor = (-16711681 & currentColor) | ((color & 255) << 16);
                                } else if (num == 3) {
                                    currentColor = (16777215 & currentColor) | ((color & 255) << 24);
                                }
                                ColorPicker.this.setColor(currentColor);
                                for (int a = 0; a < ColorPicker.this.this$1.this$0.currentThemeDesription.size(); a++) {
                                    ((ThemeDescription) ColorPicker.this.this$1.this$0.currentThemeDesription.get(a)).setColor(ColorPicker.this.getColor(), false);
                                }
                                ColorPicker.this.this$1.ignoreTextChange = false;
                            }
                        }
                    });
                    r0.colorEditText[a].setOnEditorActionListener(new OnEditorActionListener() {
                        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                            if (i != 6) {
                                return false;
                            }
                            AndroidUtilities.hideKeyboard(textView);
                            return true;
                        }
                    });
                    a++;
                }
            }

            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int size = Math.min(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
                measureChild(this.linearLayout, widthMeasureSpec, heightMeasureSpec);
                setMeasuredDimension(size, size);
            }

            protected void onDraw(Canvas canvas) {
                Canvas canvas2 = canvas;
                int centerX = (getWidth() / 2) - (this.paramValueSliderWidth * 2);
                int centerY = (getHeight() / 2) - AndroidUtilities.dp(8.0f);
                canvas2.drawBitmap(this.colorWheelBitmap, (float) (centerX - this.colorWheelRadius), (float) (centerY - this.colorWheelRadius), null);
                float hueAngle = (float) Math.toRadians((double) this.colorHSV[0]);
                int colorPointX = ((int) (((-Math.cos((double) hueAngle)) * ((double) this.colorHSV[1])) * ((double) this.colorWheelRadius))) + centerX;
                int colorPointY = ((int) (((-Math.sin((double) hueAngle)) * ((double) this.colorHSV[1])) * ((double) this.colorWheelRadius))) + centerY;
                float pointerRadius = 0.075f * ((float) this.colorWheelRadius);
                this.hsvTemp[0] = this.colorHSV[0];
                this.hsvTemp[1] = this.colorHSV[1];
                this.hsvTemp[2] = 1.0f;
                drawPointerArrow(canvas2, colorPointX, colorPointY, Color.HSVToColor(this.hsvTemp));
                int x = (this.colorWheelRadius + centerX) + this.paramValueSliderWidth;
                int y = centerY - this.colorWheelRadius;
                int width = AndroidUtilities.dp(9.0f);
                int height = this.colorWheelRadius * 2;
                if (this.colorGradient == null) {
                    r0.colorGradient = new LinearGradient((float) x, (float) y, (float) (x + width), (float) (y + height), new int[]{Theme.ACTION_BAR_VIDEO_EDIT_COLOR, Color.HSVToColor(r0.hsvTemp)}, null, TileMode.CLAMP);
                }
                r0.valueSliderPaint.setShader(r0.colorGradient);
                float f = (float) y;
                float f2 = (float) (y + height);
                int height2 = height;
                float f3 = (float) (x + width);
                int y2 = y;
                float f4 = f2;
                centerX = x;
                canvas2.drawRect((float) x, f, f3, f4, r0.valueSliderPaint);
                drawPointerArrow(canvas2, centerX + (width / 2), (int) (((float) y2) + (r0.colorHSV[2] * ((float) height2))), Color.HSVToColor(r0.colorHSV));
                centerX += r0.paramValueSliderWidth * 2;
                if (r0.alphaGradient == null) {
                    int color = Color.HSVToColor(r0.hsvTemp);
                    r0.alphaGradient = new LinearGradient((float) centerX, (float) y2, (float) (centerX + width), (float) (y2 + height2), new int[]{color, color & 16777215}, null, TileMode.CLAMP);
                }
                r0.valueSliderPaint.setShader(r0.alphaGradient);
                canvas2.drawRect((float) centerX, (float) y2, (float) (centerX + width), (float) (y2 + height2), r0.valueSliderPaint);
                drawPointerArrow(canvas2, (width / 2) + centerX, (int) (((float) y2) + ((1.0f - r0.alpha) * ((float) height2))), (Color.HSVToColor(r0.colorHSV) & 16777215) | (((int) (255.0f * r0.alpha)) << 24));
            }

            private void drawPointerArrow(Canvas canvas, int x, int y, int color) {
                int side = AndroidUtilities.dp(NUM);
                this.circleDrawable.setBounds(x - side, y - side, x + side, y + side);
                this.circleDrawable.draw(canvas);
                this.circlePaint.setColor(-1);
                canvas.drawCircle((float) x, (float) y, (float) AndroidUtilities.dp(11.0f), this.circlePaint);
                this.circlePaint.setColor(color);
                canvas.drawCircle((float) x, (float) y, (float) AndroidUtilities.dp(9.0f), this.circlePaint);
            }

            protected void onSizeChanged(int width, int height, int oldw, int oldh) {
                this.colorWheelRadius = Math.max(1, ((width / 2) - (this.paramValueSliderWidth * 2)) - AndroidUtilities.dp(20.0f));
                this.colorWheelBitmap = createColorWheelBitmap(this.colorWheelRadius * 2, this.colorWheelRadius * 2);
                this.colorGradient = null;
                this.alphaGradient = null;
            }

            private Bitmap createColorWheelBitmap(int width, int height) {
                ColorPicker colorPicker = this;
                int i = width;
                int i2 = height;
                Bitmap bitmap = Bitmap.createBitmap(i, i2, Config.ARGB_8888);
                int[] colors = new int[(12 + 1)];
                float[] hsv = new float[]{0.0f, 1.0f, 1.0f};
                for (int i3 = 0; i3 < colors.length; i3++) {
                    hsv[0] = (float) (((i3 * 30) + 180) % 360);
                    colors[i3] = Color.HSVToColor(hsv);
                }
                colors[12] = colors[0];
                colorPicker.colorWheelPaint.setShader(new ComposeShader(new SweepGradient((float) (i / 2), (float) (i2 / 2), colors, null), new RadialGradient((float) (i / 2), (float) (i2 / 2), (float) colorPicker.colorWheelRadius, -1, 16777215, TileMode.CLAMP), Mode.SRC_OVER));
                new Canvas(bitmap).drawCircle((float) (i / 2), (float) (i2 / 2), (float) colorPicker.colorWheelRadius, colorPicker.colorWheelPaint);
                return bitmap;
            }

            private void startColorChange(boolean start) {
                if (this.this$1.startedColorChange != start) {
                    if (this.this$1.colorChangeAnimation != null) {
                        this.this$1.colorChangeAnimation.cancel();
                    }
                    this.this$1.startedColorChange = start;
                    this.this$1.colorChangeAnimation = new AnimatorSet();
                    AnimatorSet access$400 = this.this$1.colorChangeAnimation;
                    Animator[] animatorArr = new Animator[2];
                    ColorDrawable access$500 = this.this$1.backDrawable;
                    String str = "alpha";
                    int[] iArr = new int[1];
                    iArr[0] = start ? 0 : 51;
                    animatorArr[0] = ObjectAnimator.ofInt(access$500, str, iArr);
                    ViewGroup access$600 = this.this$1.containerView;
                    str = "alpha";
                    float[] fArr = new float[1];
                    fArr[0] = start ? 0.2f : 1.0f;
                    animatorArr[1] = ObjectAnimator.ofFloat(access$600, str, fArr);
                    access$400.playTogether(animatorArr);
                    this.this$1.colorChangeAnimation.setDuration(150);
                    this.this$1.colorChangeAnimation.setInterpolator(this.decelerateInterpolator);
                    this.this$1.colorChangeAnimation.start();
                }
            }

            public boolean onTouchEvent(MotionEvent event) {
                ColorPicker colorPicker = this;
                int action = event.getAction();
                int i;
                switch (action) {
                    case 0:
                    case 2:
                        int centerY;
                        int x;
                        int x2;
                        int color;
                        int green;
                        int blue;
                        EditTextBoldCursor editTextBoldCursor;
                        StringBuilder stringBuilder;
                        StringBuilder stringBuilder2;
                        float value;
                        int x3 = (int) event.getX();
                        int y = (int) event.getY();
                        int centerX = (getWidth() / 2) - (colorPicker.paramValueSliderWidth * 2);
                        int centerY2 = (getHeight() / 2) - AndroidUtilities.dp(8.0f);
                        int cx = x3 - centerX;
                        int cy = y - centerY2;
                        double d = Math.sqrt((double) ((cx * cx) + (cy * cy)));
                        if (colorPicker.circlePressed) {
                            centerY = centerY2;
                        } else {
                            if (colorPicker.alphaPressed || colorPicker.colorPressed) {
                                centerY = centerY2;
                            } else {
                                centerY = centerY2;
                                if (d <= ((double) colorPicker.colorWheelRadius)) {
                                }
                            }
                            x = x3;
                            if (!colorPicker.colorPressed) {
                                x2 = x;
                            } else if (!colorPicker.circlePressed || colorPicker.alphaPressed) {
                                x2 = x;
                                if (colorPicker.alphaPressed || (!colorPicker.circlePressed && !colorPicker.colorPressed && x >= (colorPicker.colorWheelRadius + centerX) + (colorPicker.paramValueSliderWidth * 3) && x <= (colorPicker.colorWheelRadius + centerX) + (colorPicker.paramValueSliderWidth * 4) && y >= centerY - colorPicker.colorWheelRadius && y <= centerY + colorPicker.colorWheelRadius)) {
                                    colorPicker.alpha = 1.0f - (((float) (y - (centerY - colorPicker.colorWheelRadius))) / (((float) colorPicker.colorWheelRadius) * 2.0f));
                                    if (colorPicker.alpha < 0.0f) {
                                        colorPicker.alpha = 0.0f;
                                    } else if (colorPicker.alpha > 1.0f) {
                                        colorPicker.alpha = 1.0f;
                                    }
                                    colorPicker.alphaPressed = true;
                                }
                                if (!(colorPicker.alphaPressed || colorPicker.colorPressed)) {
                                    if (!colorPicker.circlePressed) {
                                        i = action;
                                        return true;
                                    }
                                }
                                startColorChange(true);
                                color = getColor();
                                for (x3 = 0; x3 < colorPicker.this$1.this$0.currentThemeDesription.size(); x3++) {
                                    ((ThemeDescription) colorPicker.this$1.this$0.currentThemeDesription.get(x3)).setColor(color, false);
                                }
                                x3 = Color.red(color);
                                green = Color.green(color);
                                blue = Color.blue(color);
                                centerY2 = Color.alpha(color);
                                if (colorPicker.this$1.ignoreTextChange) {
                                } else {
                                    colorPicker.this$1.ignoreTextChange = true;
                                    editTextBoldCursor = colorPicker.colorEditText[0];
                                    stringBuilder = new StringBuilder();
                                    i = action;
                                    stringBuilder.append(TtmlNode.ANONYMOUS_REGION_ID);
                                    stringBuilder.append(x3);
                                    editTextBoldCursor.setText(stringBuilder.toString());
                                    action = colorPicker.colorEditText[1];
                                    stringBuilder2 = new StringBuilder();
                                    stringBuilder2.append(TtmlNode.ANONYMOUS_REGION_ID);
                                    stringBuilder2.append(green);
                                    action.setText(stringBuilder2.toString());
                                    action = colorPicker.colorEditText[2];
                                    stringBuilder2 = new StringBuilder();
                                    stringBuilder2.append(TtmlNode.ANONYMOUS_REGION_ID);
                                    stringBuilder2.append(blue);
                                    action.setText(stringBuilder2.toString());
                                    action = colorPicker.colorEditText[3];
                                    stringBuilder2 = new StringBuilder();
                                    stringBuilder2.append(TtmlNode.ANONYMOUS_REGION_ID);
                                    stringBuilder2.append(centerY2);
                                    action.setText(stringBuilder2.toString());
                                    for (action = 0; action < 4; action++) {
                                        colorPicker.colorEditText[action].setSelection(colorPicker.colorEditText[action].length());
                                    }
                                    colorPicker.this$1.ignoreTextChange = false;
                                }
                                invalidate();
                                return true;
                            } else {
                                x2 = x;
                                if (x2 >= (colorPicker.colorWheelRadius + centerX) + colorPicker.paramValueSliderWidth && x2 <= (colorPicker.colorWheelRadius + centerX) + (colorPicker.paramValueSliderWidth * 2) && y >= centerY - colorPicker.colorWheelRadius && y <= centerY + colorPicker.colorWheelRadius) {
                                }
                                colorPicker.alpha = 1.0f - (((float) (y - (centerY - colorPicker.colorWheelRadius))) / (((float) colorPicker.colorWheelRadius) * 2.0f));
                                if (colorPicker.alpha < 0.0f) {
                                    colorPicker.alpha = 0.0f;
                                } else if (colorPicker.alpha > 1.0f) {
                                    colorPicker.alpha = 1.0f;
                                }
                                colorPicker.alphaPressed = true;
                                if (!colorPicker.circlePressed) {
                                    i = action;
                                    return true;
                                }
                                startColorChange(true);
                                color = getColor();
                                for (x3 = 0; x3 < colorPicker.this$1.this$0.currentThemeDesription.size(); x3++) {
                                    ((ThemeDescription) colorPicker.this$1.this$0.currentThemeDesription.get(x3)).setColor(color, false);
                                }
                                x3 = Color.red(color);
                                green = Color.green(color);
                                blue = Color.blue(color);
                                centerY2 = Color.alpha(color);
                                if (colorPicker.this$1.ignoreTextChange) {
                                } else {
                                    colorPicker.this$1.ignoreTextChange = true;
                                    editTextBoldCursor = colorPicker.colorEditText[0];
                                    stringBuilder = new StringBuilder();
                                    i = action;
                                    stringBuilder.append(TtmlNode.ANONYMOUS_REGION_ID);
                                    stringBuilder.append(x3);
                                    editTextBoldCursor.setText(stringBuilder.toString());
                                    action = colorPicker.colorEditText[1];
                                    stringBuilder2 = new StringBuilder();
                                    stringBuilder2.append(TtmlNode.ANONYMOUS_REGION_ID);
                                    stringBuilder2.append(green);
                                    action.setText(stringBuilder2.toString());
                                    action = colorPicker.colorEditText[2];
                                    stringBuilder2 = new StringBuilder();
                                    stringBuilder2.append(TtmlNode.ANONYMOUS_REGION_ID);
                                    stringBuilder2.append(blue);
                                    action.setText(stringBuilder2.toString());
                                    action = colorPicker.colorEditText[3];
                                    stringBuilder2 = new StringBuilder();
                                    stringBuilder2.append(TtmlNode.ANONYMOUS_REGION_ID);
                                    stringBuilder2.append(centerY2);
                                    action.setText(stringBuilder2.toString());
                                    for (action = 0; action < 4; action++) {
                                        colorPicker.colorEditText[action].setSelection(colorPicker.colorEditText[action].length());
                                    }
                                    colorPicker.this$1.ignoreTextChange = false;
                                }
                                invalidate();
                                return true;
                            }
                            value = ((float) (y - (centerY - colorPicker.colorWheelRadius))) / (((float) colorPicker.colorWheelRadius) * 2.0f);
                            if (value < 0.0f) {
                                value = 0.0f;
                            } else if (value > 1.0f) {
                                value = 1.0f;
                            }
                            colorPicker.colorHSV[2] = value;
                            colorPicker.colorPressed = true;
                            colorPicker.alpha = 1.0f - (((float) (y - (centerY - colorPicker.colorWheelRadius))) / (((float) colorPicker.colorWheelRadius) * 2.0f));
                            if (colorPicker.alpha < 0.0f) {
                                colorPicker.alpha = 0.0f;
                            } else if (colorPicker.alpha > 1.0f) {
                                colorPicker.alpha = 1.0f;
                            }
                            colorPicker.alphaPressed = true;
                            if (!colorPicker.circlePressed) {
                                startColorChange(true);
                                color = getColor();
                                for (x3 = 0; x3 < colorPicker.this$1.this$0.currentThemeDesription.size(); x3++) {
                                    ((ThemeDescription) colorPicker.this$1.this$0.currentThemeDesription.get(x3)).setColor(color, false);
                                }
                                x3 = Color.red(color);
                                green = Color.green(color);
                                blue = Color.blue(color);
                                centerY2 = Color.alpha(color);
                                if (colorPicker.this$1.ignoreTextChange) {
                                    colorPicker.this$1.ignoreTextChange = true;
                                    editTextBoldCursor = colorPicker.colorEditText[0];
                                    stringBuilder = new StringBuilder();
                                    i = action;
                                    stringBuilder.append(TtmlNode.ANONYMOUS_REGION_ID);
                                    stringBuilder.append(x3);
                                    editTextBoldCursor.setText(stringBuilder.toString());
                                    action = colorPicker.colorEditText[1];
                                    stringBuilder2 = new StringBuilder();
                                    stringBuilder2.append(TtmlNode.ANONYMOUS_REGION_ID);
                                    stringBuilder2.append(green);
                                    action.setText(stringBuilder2.toString());
                                    action = colorPicker.colorEditText[2];
                                    stringBuilder2 = new StringBuilder();
                                    stringBuilder2.append(TtmlNode.ANONYMOUS_REGION_ID);
                                    stringBuilder2.append(blue);
                                    action.setText(stringBuilder2.toString());
                                    action = colorPicker.colorEditText[3];
                                    stringBuilder2 = new StringBuilder();
                                    stringBuilder2.append(TtmlNode.ANONYMOUS_REGION_ID);
                                    stringBuilder2.append(centerY2);
                                    action.setText(stringBuilder2.toString());
                                    for (action = 0; action < 4; action++) {
                                        colorPicker.colorEditText[action].setSelection(colorPicker.colorEditText[action].length());
                                    }
                                    colorPicker.this$1.ignoreTextChange = false;
                                }
                                invalidate();
                                return true;
                            }
                            i = action;
                            return true;
                        }
                        if (d > ((double) colorPicker.colorWheelRadius)) {
                            d = (double) colorPicker.colorWheelRadius;
                        }
                        colorPicker.circlePressed = true;
                        x = x3;
                        colorPicker.colorHSV[0] = (float) (Math.toDegrees(Math.atan2((double) cy, (double) cx)) + 180.0d);
                        colorPicker.colorHSV[1] = Math.max(0.0f, Math.min(1.0f, (float) (d / ((double) colorPicker.colorWheelRadius))));
                        colorPicker.colorGradient = null;
                        colorPicker.alphaGradient = null;
                        if (!colorPicker.colorPressed) {
                            x2 = x;
                        } else {
                            if (colorPicker.circlePressed) {
                                break;
                            }
                            x2 = x;
                            colorPicker.alpha = 1.0f - (((float) (y - (centerY - colorPicker.colorWheelRadius))) / (((float) colorPicker.colorWheelRadius) * 2.0f));
                            if (colorPicker.alpha < 0.0f) {
                                colorPicker.alpha = 0.0f;
                            } else if (colorPicker.alpha > 1.0f) {
                                colorPicker.alpha = 1.0f;
                            }
                            colorPicker.alphaPressed = true;
                            if (!colorPicker.circlePressed) {
                                i = action;
                                return true;
                            }
                            startColorChange(true);
                            color = getColor();
                            for (x3 = 0; x3 < colorPicker.this$1.this$0.currentThemeDesription.size(); x3++) {
                                ((ThemeDescription) colorPicker.this$1.this$0.currentThemeDesription.get(x3)).setColor(color, false);
                            }
                            x3 = Color.red(color);
                            green = Color.green(color);
                            blue = Color.blue(color);
                            centerY2 = Color.alpha(color);
                            if (colorPicker.this$1.ignoreTextChange) {
                            } else {
                                colorPicker.this$1.ignoreTextChange = true;
                                editTextBoldCursor = colorPicker.colorEditText[0];
                                stringBuilder = new StringBuilder();
                                i = action;
                                stringBuilder.append(TtmlNode.ANONYMOUS_REGION_ID);
                                stringBuilder.append(x3);
                                editTextBoldCursor.setText(stringBuilder.toString());
                                action = colorPicker.colorEditText[1];
                                stringBuilder2 = new StringBuilder();
                                stringBuilder2.append(TtmlNode.ANONYMOUS_REGION_ID);
                                stringBuilder2.append(green);
                                action.setText(stringBuilder2.toString());
                                action = colorPicker.colorEditText[2];
                                stringBuilder2 = new StringBuilder();
                                stringBuilder2.append(TtmlNode.ANONYMOUS_REGION_ID);
                                stringBuilder2.append(blue);
                                action.setText(stringBuilder2.toString());
                                action = colorPicker.colorEditText[3];
                                stringBuilder2 = new StringBuilder();
                                stringBuilder2.append(TtmlNode.ANONYMOUS_REGION_ID);
                                stringBuilder2.append(centerY2);
                                action.setText(stringBuilder2.toString());
                                for (action = 0; action < 4; action++) {
                                    colorPicker.colorEditText[action].setSelection(colorPicker.colorEditText[action].length());
                                }
                                colorPicker.this$1.ignoreTextChange = false;
                            }
                            invalidate();
                            return true;
                        }
                        value = ((float) (y - (centerY - colorPicker.colorWheelRadius))) / (((float) colorPicker.colorWheelRadius) * 2.0f);
                        if (value < 0.0f) {
                            value = 0.0f;
                        } else if (value > 1.0f) {
                            value = 1.0f;
                        }
                        colorPicker.colorHSV[2] = value;
                        colorPicker.colorPressed = true;
                        colorPicker.alpha = 1.0f - (((float) (y - (centerY - colorPicker.colorWheelRadius))) / (((float) colorPicker.colorWheelRadius) * 2.0f));
                        if (colorPicker.alpha < 0.0f) {
                            colorPicker.alpha = 0.0f;
                        } else if (colorPicker.alpha > 1.0f) {
                            colorPicker.alpha = 1.0f;
                        }
                        colorPicker.alphaPressed = true;
                        if (!colorPicker.circlePressed) {
                            startColorChange(true);
                            color = getColor();
                            for (x3 = 0; x3 < colorPicker.this$1.this$0.currentThemeDesription.size(); x3++) {
                                ((ThemeDescription) colorPicker.this$1.this$0.currentThemeDesription.get(x3)).setColor(color, false);
                            }
                            x3 = Color.red(color);
                            green = Color.green(color);
                            blue = Color.blue(color);
                            centerY2 = Color.alpha(color);
                            if (colorPicker.this$1.ignoreTextChange) {
                                colorPicker.this$1.ignoreTextChange = true;
                                editTextBoldCursor = colorPicker.colorEditText[0];
                                stringBuilder = new StringBuilder();
                                i = action;
                                stringBuilder.append(TtmlNode.ANONYMOUS_REGION_ID);
                                stringBuilder.append(x3);
                                editTextBoldCursor.setText(stringBuilder.toString());
                                action = colorPicker.colorEditText[1];
                                stringBuilder2 = new StringBuilder();
                                stringBuilder2.append(TtmlNode.ANONYMOUS_REGION_ID);
                                stringBuilder2.append(green);
                                action.setText(stringBuilder2.toString());
                                action = colorPicker.colorEditText[2];
                                stringBuilder2 = new StringBuilder();
                                stringBuilder2.append(TtmlNode.ANONYMOUS_REGION_ID);
                                stringBuilder2.append(blue);
                                action.setText(stringBuilder2.toString());
                                action = colorPicker.colorEditText[3];
                                stringBuilder2 = new StringBuilder();
                                stringBuilder2.append(TtmlNode.ANONYMOUS_REGION_ID);
                                stringBuilder2.append(centerY2);
                                action.setText(stringBuilder2.toString());
                                for (action = 0; action < 4; action++) {
                                    colorPicker.colorEditText[action].setSelection(colorPicker.colorEditText[action].length());
                                }
                                colorPicker.this$1.ignoreTextChange = false;
                            }
                            invalidate();
                            return true;
                        }
                        i = action;
                        return true;
                    case 1:
                        colorPicker.alphaPressed = false;
                        colorPicker.colorPressed = false;
                        colorPicker.circlePressed = false;
                        startColorChange(false);
                        i = action;
                        break;
                    default:
                        break;
                }
                return super.onTouchEvent(event);
            }

            public void setColor(int color) {
                int red = Color.red(color);
                int green = Color.green(color);
                int blue = Color.blue(color);
                int a = Color.alpha(color);
                if (!this.this$1.ignoreTextChange) {
                    this.this$1.ignoreTextChange = true;
                    EditTextBoldCursor editTextBoldCursor = this.colorEditText[0];
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(TtmlNode.ANONYMOUS_REGION_ID);
                    stringBuilder.append(red);
                    editTextBoldCursor.setText(stringBuilder.toString());
                    editTextBoldCursor = this.colorEditText[1];
                    StringBuilder stringBuilder2 = new StringBuilder();
                    stringBuilder2.append(TtmlNode.ANONYMOUS_REGION_ID);
                    stringBuilder2.append(green);
                    editTextBoldCursor.setText(stringBuilder2.toString());
                    editTextBoldCursor = this.colorEditText[2];
                    stringBuilder2 = new StringBuilder();
                    stringBuilder2.append(TtmlNode.ANONYMOUS_REGION_ID);
                    stringBuilder2.append(blue);
                    editTextBoldCursor.setText(stringBuilder2.toString());
                    editTextBoldCursor = this.colorEditText[3];
                    stringBuilder2 = new StringBuilder();
                    stringBuilder2.append(TtmlNode.ANONYMOUS_REGION_ID);
                    stringBuilder2.append(a);
                    editTextBoldCursor.setText(stringBuilder2.toString());
                    for (int b = 0; b < 4; b++) {
                        this.colorEditText[b].setSelection(this.colorEditText[b].length());
                    }
                    this.this$1.ignoreTextChange = false;
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

        private class ListAdapter extends SelectionAdapter {
            private Context context;
            private int currentCount;
            private ArrayList<ArrayList<ThemeDescription>> items = new ArrayList();
            private HashMap<String, ArrayList<ThemeDescription>> itemsMap = new HashMap();

            public ListAdapter(Context context, ThemeDescription[] descriptions) {
                this.context = context;
                for (ThemeDescription description : descriptions) {
                    String key = description.getCurrentKey();
                    ArrayList<ThemeDescription> arrayList = (ArrayList) this.itemsMap.get(key);
                    if (arrayList == null) {
                        arrayList = new ArrayList();
                        this.itemsMap.put(key, arrayList);
                        this.items.add(arrayList);
                    }
                    arrayList.add(description);
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
                return null;
            }

            public boolean isEnabled(ViewHolder holder) {
                return true;
            }

            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = new TextColorThemeCell(this.context);
                view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                return new Holder(view);
            }

            public void onBindViewHolder(ViewHolder holder, int position) {
                int color;
                ThemeDescription description = (ThemeDescription) ((ArrayList) this.items.get(position)).get(0);
                if (description.getCurrentKey().equals(Theme.key_chat_wallpaper)) {
                    color = 0;
                } else {
                    color = description.getSetColor();
                }
                ((TextColorThemeCell) holder.itemView).setTextAndColor(description.getTitle(), color);
            }

            public int getItemViewType(int i) {
                return 0;
            }
        }

        public EditorAlert(ThemeEditorView this$0, Context context, ThemeDescription[] items) {
            final ThemeEditorView themeEditorView = this$0;
            Context context2 = context;
            this.this$0 = themeEditorView;
            super(context2, true);
            this.shadowDrawable = context.getResources().getDrawable(R.drawable.sheet_shadow).mutate();
            this.containerView = new FrameLayout(context2) {
                private boolean ignoreLayout = null;

                public boolean onInterceptTouchEvent(MotionEvent ev) {
                    if (ev.getAction() != 0 || EditorAlert.this.scrollOffsetY == 0 || ev.getY() >= ((float) EditorAlert.this.scrollOffsetY)) {
                        return super.onInterceptTouchEvent(ev);
                    }
                    EditorAlert.this.dismiss();
                    return true;
                }

                public boolean onTouchEvent(MotionEvent e) {
                    return !EditorAlert.this.isDismissed() && super.onTouchEvent(e);
                }

                protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    int width = MeasureSpec.getSize(widthMeasureSpec);
                    int height = MeasureSpec.getSize(heightMeasureSpec);
                    if (VERSION.SDK_INT >= 21) {
                        height -= AndroidUtilities.statusBarHeight;
                    }
                    int padding = height - Math.min(width, height);
                    if (EditorAlert.this.listView.getPaddingTop() != padding) {
                        this.ignoreLayout = true;
                        int previousPadding = EditorAlert.this.listView.getPaddingTop();
                        EditorAlert.this.listView.setPadding(0, padding, 0, AndroidUtilities.dp(48.0f));
                        if (EditorAlert.this.colorPicker.getVisibility() == 0) {
                            EditorAlert.this.scrollOffsetY = EditorAlert.this.listView.getPaddingTop();
                            EditorAlert.this.listView.setTopGlowOffset(EditorAlert.this.scrollOffsetY);
                            EditorAlert.this.colorPicker.setTranslationY((float) EditorAlert.this.scrollOffsetY);
                            EditorAlert.this.previousScrollPosition = 0;
                        }
                        this.ignoreLayout = false;
                    }
                    super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(height, NUM));
                }

                protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                    super.onLayout(changed, left, top, right, bottom);
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
            Adapter listAdapter = new ListAdapter(context2, items);
            this.listAdapter = listAdapter;
            recyclerListView.setAdapter(listAdapter);
            this.listView.setGlowColor(-657673);
            this.listView.setItemAnimator(null);
            this.listView.setLayoutAnimation(null);
            this.listView.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(View view, int position) {
                    EditorAlert.this.this$0.currentThemeDesription = EditorAlert.this.listAdapter.getItem(position);
                    EditorAlert.this.this$0.currentThemeDesriptionPosition = position;
                    for (int a = 0; a < EditorAlert.this.this$0.currentThemeDesription.size(); a++) {
                        ThemeDescription description = (ThemeDescription) EditorAlert.this.this$0.currentThemeDesription.get(a);
                        if (description.getCurrentKey().equals(Theme.key_chat_wallpaper)) {
                            EditorAlert.this.this$0.wallpaperUpdater.showAlert(true);
                            return;
                        }
                        description.startEditing();
                        if (a == 0) {
                            EditorAlert.this.colorPicker.setColor(description.getCurrentColor());
                        }
                    }
                    EditorAlert.this.setColorPickerVisible(true);
                }
            });
            this.listView.setOnScrollListener(new OnScrollListener() {
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    EditorAlert.this.updateLayout();
                }
            });
            this.colorPicker = new ColorPicker(this, context2);
            this.colorPicker.setVisibility(8);
            this.containerView.addView(this.colorPicker, LayoutHelper.createFrame(-1, -1, 1));
            this.shadow = new View(context2);
            this.shadow.setBackgroundResource(R.drawable.header_shadow_reverse);
            this.containerView.addView(this.shadow, LayoutHelper.createFrame(-1, 3.0f, 83, 0.0f, 0.0f, 0.0f, 48.0f));
            this.bottomSaveLayout = new FrameLayout(context2);
            this.bottomSaveLayout.setBackgroundColor(-1);
            this.containerView.addView(this.bottomSaveLayout, LayoutHelper.createFrame(-1, 48, 83));
            TextView closeButton = new TextView(context2);
            closeButton.setTextSize(1, 14.0f);
            closeButton.setTextColor(-15095832);
            closeButton.setGravity(17);
            closeButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_AUDIO_SELECTOR_COLOR, 0));
            closeButton.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
            closeButton.setText(LocaleController.getString("CloseEditor", R.string.CloseEditor).toUpperCase());
            closeButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.bottomSaveLayout.addView(closeButton, LayoutHelper.createFrame(-2, -1, 51));
            closeButton.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    EditorAlert.this.dismiss();
                }
            });
            TextView saveButton = new TextView(context2);
            saveButton.setTextSize(1, 14.0f);
            saveButton.setTextColor(-15095832);
            saveButton.setGravity(17);
            saveButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_AUDIO_SELECTOR_COLOR, 0));
            saveButton.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
            saveButton.setText(LocaleController.getString("SaveTheme", R.string.SaveTheme).toUpperCase());
            saveButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.bottomSaveLayout.addView(saveButton, LayoutHelper.createFrame(-2, -1, 53));
            saveButton.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
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
            this.cancelButton.setText(LocaleController.getString("Cancel", R.string.Cancel).toUpperCase());
            this.cancelButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.bottomLayout.addView(this.cancelButton, LayoutHelper.createFrame(-2, -1, 51));
            this.cancelButton.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    for (int a = 0; a < EditorAlert.this.this$0.currentThemeDesription.size(); a++) {
                        ((ThemeDescription) EditorAlert.this.this$0.currentThemeDesription.get(a)).setPreviousColor();
                    }
                    EditorAlert.this.setColorPickerVisible(false);
                }
            });
            LinearLayout linearLayout = new LinearLayout(context2);
            linearLayout.setOrientation(0);
            this.bottomLayout.addView(linearLayout, LayoutHelper.createFrame(-2, -1, 53));
            this.defaultButtom = new TextView(context2);
            this.defaultButtom.setTextSize(1, 14.0f);
            this.defaultButtom.setTextColor(-15095832);
            this.defaultButtom.setGravity(17);
            this.defaultButtom.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_AUDIO_SELECTOR_COLOR, 0));
            this.defaultButtom.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
            this.defaultButtom.setText(LocaleController.getString("Default", R.string.Default).toUpperCase());
            this.defaultButtom.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            linearLayout.addView(this.defaultButtom, LayoutHelper.createFrame(-2, -1, 51));
            this.defaultButtom.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    for (int a = 0; a < EditorAlert.this.this$0.currentThemeDesription.size(); a++) {
                        ((ThemeDescription) EditorAlert.this.this$0.currentThemeDesription.get(a)).setDefaultColor();
                    }
                    EditorAlert.this.setColorPickerVisible(false);
                }
            });
            saveButton = new TextView(context2);
            saveButton.setTextSize(1, 14.0f);
            saveButton.setTextColor(-15095832);
            saveButton.setGravity(17);
            saveButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_AUDIO_SELECTOR_COLOR, 0));
            saveButton.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
            saveButton.setText(LocaleController.getString("Save", R.string.Save).toUpperCase());
            saveButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            linearLayout.addView(saveButton, LayoutHelper.createFrame(-2, -1, 51));
            saveButton.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    EditorAlert.this.setColorPickerVisible(false);
                }
            });
        }

        private void setColorPickerVisible(boolean visible) {
            if (visible) {
                this.animationInProgress = true;
                this.colorPicker.setVisibility(0);
                this.bottomLayout.setVisibility(0);
                this.colorPicker.setAlpha(0.0f);
                this.bottomLayout.setAlpha(0.0f);
                AnimatorSet animatorSet = new AnimatorSet();
                r5 = new Animator[5];
                r5[0] = ObjectAnimator.ofFloat(this.colorPicker, "alpha", new float[]{1.0f});
                r5[1] = ObjectAnimator.ofFloat(this.bottomLayout, "alpha", new float[]{1.0f});
                r5[2] = ObjectAnimator.ofFloat(this.listView, "alpha", new float[]{0.0f});
                r5[3] = ObjectAnimator.ofFloat(this.bottomSaveLayout, "alpha", new float[]{0.0f});
                r5[4] = ObjectAnimator.ofInt(this, "scrollOffsetY", new int[]{this.listView.getPaddingTop()});
                animatorSet.playTogether(r5);
                animatorSet.setDuration(150);
                animatorSet.setInterpolator(this.this$0.decelerateInterpolator);
                animatorSet.addListener(new C13239());
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
            r5 = new Animator[5];
            r5[0] = ObjectAnimator.ofFloat(this.colorPicker, "alpha", new float[]{0.0f});
            r5[1] = ObjectAnimator.ofFloat(this.bottomLayout, "alpha", new float[]{0.0f});
            r5[2] = ObjectAnimator.ofFloat(this.listView, "alpha", new float[]{1.0f});
            r5[3] = ObjectAnimator.ofFloat(this.bottomSaveLayout, "alpha", new float[]{1.0f});
            r5[4] = ObjectAnimator.ofInt(this, "scrollOffsetY", new int[]{this.previousScrollPosition});
            animatorSet.playTogether(r5);
            animatorSet.setDuration(150);
            animatorSet.setInterpolator(this.this$0.decelerateInterpolator);
            animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
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
                View child = this.listView.getChildAt(0);
                Holder holder = (Holder) this.listView.findContainingViewHolder(child);
                if (holder != null) {
                    int paddingTop = this.listView.getPaddingTop();
                    if (holder.getAdapterPosition() == 0 && child.getTop() >= 0) {
                        i = child.getTop();
                    }
                    return paddingTop - i;
                }
            }
            return C0539C.PRIORITY_DOWNLOAD;
        }

        protected boolean canDismissWithSwipe() {
            return false;
        }

        @SuppressLint({"NewApi"})
        private void updateLayout() {
            if (this.listView.getChildCount() > 0 && this.listView.getVisibility() == 0) {
                if (!this.animationInProgress) {
                    int top;
                    int newOffset = 0;
                    View child = this.listView.getChildAt(0);
                    Holder holder = (Holder) this.listView.findContainingViewHolder(child);
                    if (this.listView.getVisibility() == 0) {
                        if (!this.animationInProgress) {
                            top = child.getTop() - AndroidUtilities.dp(8.0f);
                            if (top > 0 && holder != null && holder.getAdapterPosition() == 0) {
                                newOffset = top;
                            }
                            if (this.scrollOffsetY != newOffset) {
                                setScrollOffsetY(newOffset);
                            }
                        }
                    }
                    top = this.listView.getPaddingTop();
                    newOffset = top;
                    if (this.scrollOffsetY != newOffset) {
                        setScrollOffsetY(newOffset);
                    }
                }
            }
        }

        public int getScrollOffsetY() {
            return this.scrollOffsetY;
        }

        @Keep
        public void setScrollOffsetY(int value) {
            RecyclerListView recyclerListView = this.listView;
            this.scrollOffsetY = value;
            recyclerListView.setTopGlowOffset(value);
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

    public void show(Activity activity, final String themeName) {
        if (Instance != null) {
            Instance.destroy();
        }
        this.hidden = false;
        this.currentThemeName = themeName;
        this.windowView = new FrameLayout(activity) {
            private boolean dragging;
            private float startX;
            private float startY;

            /* renamed from: org.telegram.ui.Components.ThemeEditorView$1$1 */
            class C13121 implements OnDismissListener {
                C13121() {
                }

                public void onDismiss(DialogInterface dialog) {
                }
            }

            /* renamed from: org.telegram.ui.Components.ThemeEditorView$1$2 */
            class C13132 implements OnDismissListener {
                C13132() {
                }

                public void onDismiss(DialogInterface dialog) {
                    ThemeEditorView.this.editorAlert = null;
                    ThemeEditorView.this.show();
                }
            }

            public boolean onInterceptTouchEvent(MotionEvent event) {
                return true;
            }

            public boolean onTouchEvent(MotionEvent event) {
                float x = event.getRawX();
                float y = event.getRawY();
                if (event.getAction() == 0) {
                    this.startX = x;
                    this.startY = y;
                } else if (event.getAction() != 2 || this.dragging) {
                    if (event.getAction() == 1 && !this.dragging && ThemeEditorView.this.editorAlert == null) {
                        ActionBarLayout actionBarLayout = ((LaunchActivity) ThemeEditorView.this.parentActivity).getActionBarLayout();
                        if (!actionBarLayout.fragmentsStack.isEmpty()) {
                            ThemeDescription[] items = ((BaseFragment) actionBarLayout.fragmentsStack.get(actionBarLayout.fragmentsStack.size() - 1)).getThemeDescriptions();
                            if (items != null) {
                                ThemeEditorView.this.editorAlert = new EditorAlert(ThemeEditorView.this, ThemeEditorView.this.parentActivity, items);
                                ThemeEditorView.this.editorAlert.setOnDismissListener(new C13121());
                                ThemeEditorView.this.editorAlert.setOnDismissListener(new C13132());
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
                        float dx = x - this.startX;
                        float dy = y - this.startY;
                        LayoutParams access$2700 = ThemeEditorView.this.windowLayoutParams;
                        access$2700.x = (int) (((float) access$2700.x) + dx);
                        access$2700 = ThemeEditorView.this.windowLayoutParams;
                        access$2700.y = (int) (((float) access$2700.y) + dy);
                        int maxDiff = ThemeEditorView.this.editorWidth / 2;
                        if (ThemeEditorView.this.windowLayoutParams.x < (-maxDiff)) {
                            ThemeEditorView.this.windowLayoutParams.x = -maxDiff;
                        } else if (ThemeEditorView.this.windowLayoutParams.x > (AndroidUtilities.displaySize.x - ThemeEditorView.this.windowLayoutParams.width) + maxDiff) {
                            ThemeEditorView.this.windowLayoutParams.x = (AndroidUtilities.displaySize.x - ThemeEditorView.this.windowLayoutParams.width) + maxDiff;
                        }
                        float alpha = 1.0f;
                        if (ThemeEditorView.this.windowLayoutParams.x < 0) {
                            alpha = 1.0f + ((((float) ThemeEditorView.this.windowLayoutParams.x) / ((float) maxDiff)) * 0.5f);
                        } else if (ThemeEditorView.this.windowLayoutParams.x > AndroidUtilities.displaySize.x - ThemeEditorView.this.windowLayoutParams.width) {
                            alpha = 1.0f - ((((float) ((ThemeEditorView.this.windowLayoutParams.x - AndroidUtilities.displaySize.x) + ThemeEditorView.this.windowLayoutParams.width)) / ((float) maxDiff)) * 0.5f);
                        }
                        if (ThemeEditorView.this.windowView.getAlpha() != alpha) {
                            ThemeEditorView.this.windowView.setAlpha(alpha);
                        }
                        if (ThemeEditorView.this.windowLayoutParams.y < (-null)) {
                            ThemeEditorView.this.windowLayoutParams.y = -null;
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
        };
        this.windowView.setBackgroundResource(R.drawable.theme_picker);
        this.windowManager = (WindowManager) activity.getSystemService("window");
        this.preferences = ApplicationLoader.applicationContext.getSharedPreferences("themeconfig", 0);
        int sidex = this.preferences.getInt("sidex", 1);
        int sidey = this.preferences.getInt("sidey", 0);
        float px = this.preferences.getFloat("px", 0.0f);
        float py = this.preferences.getFloat("py", 0.0f);
        try {
            this.windowLayoutParams = new LayoutParams();
            this.windowLayoutParams.width = this.editorWidth;
            this.windowLayoutParams.height = this.editorHeight;
            this.windowLayoutParams.x = getSideCoord(true, sidex, px, this.editorWidth);
            this.windowLayoutParams.y = getSideCoord(false, sidey, py, this.editorHeight);
            this.windowLayoutParams.format = -3;
            this.windowLayoutParams.gravity = 51;
            this.windowLayoutParams.type = 99;
            this.windowLayoutParams.flags = 16777736;
            this.windowManager.addView(this.windowView, this.windowLayoutParams);
            this.wallpaperUpdater = new WallpaperUpdater(activity, new WallpaperUpdaterDelegate() {
                public void didSelectWallpaper(File file, Bitmap bitmap) {
                    Theme.setThemeWallpaper(themeName, bitmap, file);
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
            if (isX) {
                return result + ActionBar.getCurrentActionBarHeight();
            }
            return result;
        }
        if (isX) {
            return result;
        }
        return result + ActionBar.getCurrentActionBarHeight();
    }

    private void hide() {
        if (this.parentActivity != null) {
            try {
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.windowView, "alpha", new float[]{1.0f, 0.0f}), ObjectAnimator.ofFloat(this.windowView, "scaleX", new float[]{1.0f, 0.0f}), ObjectAnimator.ofFloat(this.windowView, "scaleY", new float[]{1.0f, 0.0f})});
                animatorSet.setInterpolator(this.decelerateInterpolator);
                animatorSet.setDuration(150);
                animatorSet.addListener(new C13153());
                animatorSet.start();
                this.hidden = true;
            } catch (Exception e) {
            }
        }
    }

    private void show() {
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
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (this.wallpaperUpdater != null) {
            this.wallpaperUpdater.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void animateToBoundsMaybe() {
        AnimatorSet animatorSet;
        int startX = getSideCoord(true, 0, 0.0f, this.editorWidth);
        int endX = getSideCoord(true, 1, 0.0f, this.editorWidth);
        int startY = getSideCoord(false, 0, 0.0f, this.editorHeight);
        int endY = getSideCoord(false, 1, 0.0f, this.editorHeight);
        ArrayList<Animator> animators = null;
        Editor editor = this.preferences.edit();
        int maxDiff = AndroidUtilities.dp(NUM);
        boolean slideOut = false;
        if (Math.abs(startX - this.windowLayoutParams.x) > maxDiff) {
            if (r0.windowLayoutParams.x >= 0 || r0.windowLayoutParams.x <= (-r0.editorWidth) / 4) {
                if (Math.abs(endX - r0.windowLayoutParams.x) > maxDiff) {
                    if (r0.windowLayoutParams.x <= AndroidUtilities.displaySize.x - r0.editorWidth || r0.windowLayoutParams.x >= AndroidUtilities.displaySize.x - ((r0.editorWidth / 4) * 3)) {
                        if (r0.windowView.getAlpha() != 1.0f) {
                            if (null == null) {
                                animators = new ArrayList();
                            }
                            if (r0.windowLayoutParams.x < 0) {
                                animators.add(ObjectAnimator.ofInt(r0, "x", new int[]{-r0.editorWidth}));
                            } else {
                                animators.add(ObjectAnimator.ofInt(r0, "x", new int[]{AndroidUtilities.displaySize.x}));
                            }
                            slideOut = true;
                        } else {
                            editor.putFloat("px", ((float) (r0.windowLayoutParams.x - startX)) / ((float) (endX - startX)));
                            editor.putInt("sidex", 2);
                        }
                        if (!slideOut) {
                            if (Math.abs(startY - r0.windowLayoutParams.y) > maxDiff) {
                                if (r0.windowLayoutParams.y <= ActionBar.getCurrentActionBarHeight()) {
                                    if (Math.abs(endY - r0.windowLayoutParams.y) <= maxDiff) {
                                        if (animators == null) {
                                            animators = new ArrayList();
                                        }
                                        editor.putInt("sidey", 1);
                                        animators.add(ObjectAnimator.ofInt(r0, "y", new int[]{endY}));
                                    } else {
                                        editor.putFloat("py", ((float) (r0.windowLayoutParams.y - startY)) / ((float) (endY - startY)));
                                        editor.putInt("sidey", 2);
                                    }
                                    editor.commit();
                                }
                            }
                            if (animators == null) {
                                animators = new ArrayList();
                            }
                            editor.putInt("sidey", 0);
                            animators.add(ObjectAnimator.ofInt(r0, "y", new int[]{startY}));
                            editor.commit();
                        }
                        if (animators == null) {
                            if (r0.decelerateInterpolator == null) {
                                r0.decelerateInterpolator = new DecelerateInterpolator();
                            }
                            animatorSet = new AnimatorSet();
                            animatorSet.setInterpolator(r0.decelerateInterpolator);
                            animatorSet.setDuration(150);
                            if (slideOut) {
                                animators.add(ObjectAnimator.ofFloat(r0.windowView, "alpha", new float[]{0.0f}));
                                animatorSet.addListener(new C13164());
                            }
                            animatorSet.playTogether(animators);
                            animatorSet.start();
                        }
                    }
                }
                if (null == null) {
                    animators = new ArrayList();
                }
                editor.putInt("sidex", 1);
                if (r0.windowView.getAlpha() != 1.0f) {
                    animators.add(ObjectAnimator.ofFloat(r0.windowView, "alpha", new float[]{1.0f}));
                }
                animators.add(ObjectAnimator.ofInt(r0, "x", new int[]{endX}));
                if (slideOut) {
                    if (Math.abs(startY - r0.windowLayoutParams.y) > maxDiff) {
                        if (r0.windowLayoutParams.y <= ActionBar.getCurrentActionBarHeight()) {
                            if (Math.abs(endY - r0.windowLayoutParams.y) <= maxDiff) {
                                editor.putFloat("py", ((float) (r0.windowLayoutParams.y - startY)) / ((float) (endY - startY)));
                                editor.putInt("sidey", 2);
                            } else {
                                if (animators == null) {
                                    animators = new ArrayList();
                                }
                                editor.putInt("sidey", 1);
                                animators.add(ObjectAnimator.ofInt(r0, "y", new int[]{endY}));
                            }
                            editor.commit();
                        }
                    }
                    if (animators == null) {
                        animators = new ArrayList();
                    }
                    editor.putInt("sidey", 0);
                    animators.add(ObjectAnimator.ofInt(r0, "y", new int[]{startY}));
                    editor.commit();
                }
                if (animators == null) {
                    if (r0.decelerateInterpolator == null) {
                        r0.decelerateInterpolator = new DecelerateInterpolator();
                    }
                    animatorSet = new AnimatorSet();
                    animatorSet.setInterpolator(r0.decelerateInterpolator);
                    animatorSet.setDuration(150);
                    if (slideOut) {
                        animators.add(ObjectAnimator.ofFloat(r0.windowView, "alpha", new float[]{0.0f}));
                        animatorSet.addListener(new C13164());
                    }
                    animatorSet.playTogether(animators);
                    animatorSet.start();
                }
            }
        }
        if (null == null) {
            animators = new ArrayList();
        }
        editor.putInt("sidex", 0);
        if (r0.windowView.getAlpha() != 1.0f) {
            animators.add(ObjectAnimator.ofFloat(r0.windowView, "alpha", new float[]{1.0f}));
        }
        animators.add(ObjectAnimator.ofInt(r0, "x", new int[]{startX}));
        if (slideOut) {
            if (Math.abs(startY - r0.windowLayoutParams.y) > maxDiff) {
                if (r0.windowLayoutParams.y <= ActionBar.getCurrentActionBarHeight()) {
                    if (Math.abs(endY - r0.windowLayoutParams.y) <= maxDiff) {
                        if (animators == null) {
                            animators = new ArrayList();
                        }
                        editor.putInt("sidey", 1);
                        animators.add(ObjectAnimator.ofInt(r0, "y", new int[]{endY}));
                    } else {
                        editor.putFloat("py", ((float) (r0.windowLayoutParams.y - startY)) / ((float) (endY - startY)));
                        editor.putInt("sidey", 2);
                    }
                    editor.commit();
                }
            }
            if (animators == null) {
                animators = new ArrayList();
            }
            editor.putInt("sidey", 0);
            animators.add(ObjectAnimator.ofInt(r0, "y", new int[]{startY}));
            editor.commit();
        }
        if (animators == null) {
            if (r0.decelerateInterpolator == null) {
                r0.decelerateInterpolator = new DecelerateInterpolator();
            }
            animatorSet = new AnimatorSet();
            animatorSet.setInterpolator(r0.decelerateInterpolator);
            animatorSet.setDuration(150);
            if (slideOut) {
                animators.add(ObjectAnimator.ofFloat(r0.windowView, "alpha", new float[]{0.0f}));
                animatorSet.addListener(new C13164());
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

    @Keep
    public void setX(int value) {
        this.windowLayoutParams.x = value;
        this.windowManager.updateViewLayout(this.windowView, this.windowLayoutParams);
    }

    @Keep
    public void setY(int value) {
        this.windowLayoutParams.y = value;
        this.windowManager.updateViewLayout(this.windowView, this.windowLayoutParams);
    }
}
