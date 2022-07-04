package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Property;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;

public class ColorPicker extends FrameLayout {
    private static final int item_delete = 3;
    private static final int item_edit = 1;
    private static final int item_share = 2;
    /* access modifiers changed from: private */
    public ImageView addButton;
    private Drawable circleDrawable;
    private Paint circlePaint;
    private boolean circlePressed;
    /* access modifiers changed from: private */
    public ImageView clearButton;
    /* access modifiers changed from: private */
    public EditTextBoldCursor[] colorEditText;
    private LinearGradient colorGradient;
    private float[] colorHSV = {0.0f, 0.0f, 1.0f};
    private boolean colorPressed;
    private Bitmap colorWheelBitmap;
    private Paint colorWheelPaint;
    private int colorWheelWidth;
    /* access modifiers changed from: private */
    public AnimatorSet colorsAnimator;
    /* access modifiers changed from: private */
    public int colorsCount = 1;
    private int currentResetType;
    /* access modifiers changed from: private */
    public final ColorPickerDelegate delegate;
    private float[] hsvTemp = new float[3];
    boolean ignoreTextChange;
    private long lastUpdateTime;
    private Paint linePaint;
    /* access modifiers changed from: private */
    public LinearLayout linearLayout;
    private float maxBrightness = 1.0f;
    /* access modifiers changed from: private */
    public int maxColorsCount = 1;
    private float maxHsvBrightness = 1.0f;
    private ActionBarMenuItem menuItem;
    private float minBrightness = 0.0f;
    private float minHsvBrightness = 0.0f;
    private boolean myMessagesColor;
    private int originalFirstColor;
    private float pressedMoveProgress = 1.0f;
    /* access modifiers changed from: private */
    public RadioButton[] radioButton = new RadioButton[4];
    private FrameLayout radioContainer;
    /* access modifiers changed from: private */
    public TextView resetButton;
    /* access modifiers changed from: private */
    public int selectedColor;
    private RectF sliderRect = new RectF();
    private Paint valueSliderPaint;

    private static class RadioButton extends View {
        private ObjectAnimator checkAnimator;
        private boolean checked;
        private float checkedState;
        private int currentColor;
        private final Paint paint = new Paint(1);

        public RadioButton(Context context) {
            super(context);
        }

        /* access modifiers changed from: package-private */
        public void updateCheckedState(boolean animate) {
            ObjectAnimator objectAnimator = this.checkAnimator;
            if (objectAnimator != null) {
                objectAnimator.cancel();
            }
            float f = 1.0f;
            if (animate) {
                float[] fArr = new float[1];
                if (!this.checked) {
                    f = 0.0f;
                }
                fArr[0] = f;
                ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this, "checkedState", fArr);
                this.checkAnimator = ofFloat;
                ofFloat.setDuration(200);
                this.checkAnimator.start();
                return;
            }
            if (!this.checked) {
                f = 0.0f;
            }
            setCheckedState(f);
        }

        public void setChecked(boolean value, boolean animated) {
            this.checked = value;
            updateCheckedState(animated);
        }

        public void setColor(int color) {
            this.currentColor = color;
            invalidate();
        }

        public int getColor() {
            return this.currentColor;
        }

        public void setCheckedState(float state) {
            this.checkedState = state;
            invalidate();
        }

        public float getCheckedState() {
            return this.checkedState;
        }

        /* access modifiers changed from: protected */
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            updateCheckedState(false);
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(30.0f), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(30.0f), NUM));
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            float radius = (float) AndroidUtilities.dp(15.0f);
            float cx = ((float) getMeasuredWidth()) * 0.5f;
            float cy = ((float) getMeasuredHeight()) * 0.5f;
            this.paint.setColor(this.currentColor);
            this.paint.setStyle(Paint.Style.STROKE);
            this.paint.setStrokeWidth((float) AndroidUtilities.dp(3.0f));
            this.paint.setAlpha(Math.round(this.checkedState * 255.0f));
            canvas.drawCircle(cx, cy, radius - (this.paint.getStrokeWidth() * 0.5f), this.paint);
            this.paint.setAlpha(255);
            this.paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(cx, cy, radius - (((float) AndroidUtilities.dp(5.0f)) * this.checkedState), this.paint);
        }

        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
            super.onInitializeAccessibilityNodeInfo(info);
            info.setText(LocaleController.getString("ColorPickerMainColor", NUM));
            info.setClassName(Button.class.getName());
            info.setChecked(this.checked);
            info.setCheckable(true);
            info.setEnabled(true);
        }
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public ColorPicker(Context context, boolean hasMenu, ColorPickerDelegate colorPickerDelegate) {
        super(context);
        Context context2 = context;
        this.delegate = colorPickerDelegate;
        this.colorEditText = new EditTextBoldCursor[2];
        setWillNotDraw(false);
        this.circleDrawable = context.getResources().getDrawable(NUM).mutate();
        this.circlePaint = new Paint(1);
        this.colorWheelPaint = new Paint(5);
        this.valueSliderPaint = new Paint(5);
        Paint paint = new Paint();
        this.linePaint = paint;
        paint.setColor(NUM);
        setClipChildren(false);
        AnonymousClass1 r9 = new LinearLayout(context2) {
            private Paint paint;
            private RectF rect = new RectF();

            {
                Paint paint2 = new Paint(1);
                this.paint = paint2;
                paint2.setColor(Theme.getColor("dialogBackgroundGray"));
            }

            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                int left = ColorPicker.this.colorEditText[0].getLeft() - AndroidUtilities.dp(13.0f);
                this.rect.set((float) left, (float) AndroidUtilities.dp(5.0f), (float) (left + ((int) (((float) AndroidUtilities.dp(91.0f)) + (ColorPicker.this.clearButton.getVisibility() == 0 ? ((float) AndroidUtilities.dp(25.0f)) * ColorPicker.this.clearButton.getAlpha() : 0.0f)))), (float) AndroidUtilities.dp(37.0f));
                canvas.drawRoundRect(this.rect, (float) AndroidUtilities.dp(16.0f), (float) AndroidUtilities.dp(16.0f), this.paint);
            }
        };
        this.linearLayout = r9;
        r9.setOrientation(0);
        addView(this.linearLayout, LayoutHelper.createFrame(-1, 54.0f, 51, 27.0f, -6.0f, 17.0f, 0.0f));
        this.linearLayout.setWillNotDraw(false);
        FrameLayout frameLayout = new FrameLayout(context2);
        this.radioContainer = frameLayout;
        frameLayout.setClipChildren(false);
        addView(this.radioContainer, LayoutHelper.createFrame(174, 30.0f, 49, 72.0f, 1.0f, 0.0f, 0.0f));
        int a = 0;
        while (a < 4) {
            this.radioButton[a] = new RadioButton(context2);
            this.radioButton[a].setChecked(this.selectedColor == a, false);
            this.radioContainer.addView(this.radioButton[a], LayoutHelper.createFrame(30, 30.0f, 48, 0.0f, 0.0f, 0.0f, 0.0f));
            this.radioButton[a].setOnClickListener(new ColorPicker$$ExternalSyntheticLambda0(this));
            a++;
        }
        int a2 = 0;
        while (true) {
            EditTextBoldCursor[] editTextBoldCursorArr = this.colorEditText;
            if (a2 >= editTextBoldCursorArr.length) {
                break;
            }
            final int num = a2;
            if (a2 % 2 == 0) {
                editTextBoldCursorArr[a2] = new EditTextBoldCursor(context2) {
                    public boolean onTouchEvent(MotionEvent event) {
                        if (getAlpha() == 1.0f && event.getAction() == 0) {
                            if (ColorPicker.this.colorEditText[num + 1].isFocused()) {
                                AndroidUtilities.showKeyboard(ColorPicker.this.colorEditText[num + 1]);
                            } else {
                                ColorPicker.this.colorEditText[num + 1].requestFocus();
                            }
                        }
                        return false;
                    }
                };
                this.colorEditText[a2].setBackgroundDrawable((Drawable) null);
                this.colorEditText[a2].setText("#");
                this.colorEditText[a2].setEnabled(false);
                this.colorEditText[a2].setFocusable(false);
                this.colorEditText[a2].setPadding(0, AndroidUtilities.dp(5.0f), 0, AndroidUtilities.dp(16.0f));
                this.linearLayout.addView(this.colorEditText[a2], LayoutHelper.createLinear(-2, -1, 0.0f, 0.0f, 0.0f, 0.0f));
            } else {
                editTextBoldCursorArr[a2] = new EditTextBoldCursor(context2) {
                    public boolean onTouchEvent(MotionEvent event) {
                        if (getAlpha() != 1.0f) {
                            return false;
                        }
                        if (!isFocused()) {
                            requestFocus();
                            return false;
                        }
                        AndroidUtilities.showKeyboard(this);
                        return super.onTouchEvent(event);
                    }

                    public boolean getGlobalVisibleRect(Rect r, Point globalOffset) {
                        boolean value = super.getGlobalVisibleRect(r, globalOffset);
                        r.bottom += AndroidUtilities.dp(40.0f);
                        return value;
                    }

                    public void invalidate() {
                        super.invalidate();
                        ColorPicker.this.colorEditText[num - 1].invalidate();
                    }
                };
                this.colorEditText[a2].setBackgroundDrawable((Drawable) null);
                this.colorEditText[a2].setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});
                this.colorEditText[a2].setHint("8BC6ED");
                this.colorEditText[a2].setPadding(0, AndroidUtilities.dp(5.0f), 0, AndroidUtilities.dp(16.0f));
                this.linearLayout.addView(this.colorEditText[a2], LayoutHelper.createLinear(71, -1, 0.0f, 0.0f, 0.0f, 0.0f));
                this.colorEditText[a2].addTextChangedListener(new TextWatcher() {
                    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    }

                    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    }

                    public void afterTextChanged(Editable editable) {
                        if (!ColorPicker.this.ignoreTextChange) {
                            ColorPicker.this.ignoreTextChange = true;
                            int a = 0;
                            while (a < editable.length()) {
                                char ch = editable.charAt(a);
                                if ((ch < '0' || ch > '9') && ((ch < 'a' || ch > 'f') && (ch < 'A' || ch > 'F'))) {
                                    editable.replace(a, a + 1, "");
                                    a--;
                                }
                                a++;
                            }
                            if (editable.length() == 0) {
                                ColorPicker.this.ignoreTextChange = false;
                                return;
                            }
                            ColorPicker colorPicker = ColorPicker.this;
                            colorPicker.setColorInner(colorPicker.getFieldColor(num, -1));
                            int color = ColorPicker.this.getColor();
                            if (editable.length() == 6) {
                                editable.replace(0, editable.length(), String.format("%02x%02x%02x", new Object[]{Byte.valueOf((byte) Color.red(color)), Byte.valueOf((byte) Color.green(color)), Byte.valueOf((byte) Color.blue(color))}).toUpperCase());
                                ColorPicker.this.colorEditText[num].setSelection(editable.length());
                            }
                            ColorPicker.this.radioButton[ColorPicker.this.selectedColor].setColor(color);
                            ColorPicker.this.delegate.setColor(color, ColorPicker.this.selectedColor, true);
                            ColorPicker.this.ignoreTextChange = false;
                        }
                    }
                });
                this.colorEditText[a2].setOnEditorActionListener(ColorPicker$$ExternalSyntheticLambda5.INSTANCE);
            }
            this.colorEditText[a2].setTextSize(1, 16.0f);
            this.colorEditText[a2].setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
            this.colorEditText[a2].setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.colorEditText[a2].setCursorColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.colorEditText[a2].setCursorSize(AndroidUtilities.dp(18.0f));
            this.colorEditText[a2].setCursorWidth(1.5f);
            this.colorEditText[a2].setSingleLine(true);
            this.colorEditText[a2].setGravity(19);
            this.colorEditText[a2].setHeaderHintColor(Theme.getColor("windowBackgroundWhiteBlueHeader"));
            this.colorEditText[a2].setTransformHintToHeader(true);
            this.colorEditText[a2].setInputType(524416);
            this.colorEditText[a2].setImeOptions(NUM);
            if (a2 == 1) {
                this.colorEditText[a2].requestFocus();
            } else if (a2 == 2 || a2 == 3) {
                this.colorEditText[a2].setVisibility(8);
            }
            a2++;
        }
        ImageView imageView = new ImageView(getContext());
        this.addButton = imageView;
        imageView.setBackground(Theme.createSelectorDrawable(Theme.getColor("dialogButtonSelector"), 1));
        this.addButton.setImageResource(NUM);
        this.addButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteBlackText"), PorterDuff.Mode.MULTIPLY));
        this.addButton.setScaleType(ImageView.ScaleType.CENTER);
        this.addButton.setOnClickListener(new ColorPicker$$ExternalSyntheticLambda1(this));
        this.addButton.setContentDescription(LocaleController.getString("Add", NUM));
        addView(this.addButton, LayoutHelper.createFrame(30, 30.0f, 49, 36.0f, 1.0f, 0.0f, 0.0f));
        AnonymousClass6 r2 = new ImageView(getContext()) {
            public void setAlpha(float alpha) {
                super.setAlpha(alpha);
                ColorPicker.this.linearLayout.invalidate();
            }
        };
        this.clearButton = r2;
        r2.setBackground(Theme.createSelectorDrawable(Theme.getColor("dialogButtonSelector"), 1));
        this.clearButton.setImageResource(NUM);
        this.clearButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteBlackText"), PorterDuff.Mode.MULTIPLY));
        this.clearButton.setAlpha(0.0f);
        this.clearButton.setScaleX(0.0f);
        this.clearButton.setScaleY(0.0f);
        this.clearButton.setScaleType(ImageView.ScaleType.CENTER);
        this.clearButton.setVisibility(4);
        this.clearButton.setOnClickListener(new ColorPicker$$ExternalSyntheticLambda2(this));
        this.clearButton.setContentDescription(LocaleController.getString("ClearButton", NUM));
        addView(this.clearButton, LayoutHelper.createFrame(30, 30.0f, 51, 97.0f, 1.0f, 0.0f, 0.0f));
        TextView textView = new TextView(context2);
        this.resetButton = textView;
        textView.setTextSize(1, 15.0f);
        this.resetButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.resetButton.setGravity(17);
        this.resetButton.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
        this.resetButton.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        addView(this.resetButton, LayoutHelper.createFrame(-2, 36.0f, 53, 0.0f, 3.0f, 14.0f, 0.0f));
        this.resetButton.setOnClickListener(ColorPicker$$ExternalSyntheticLambda4.INSTANCE);
        if (hasMenu) {
            ActionBarMenuItem actionBarMenuItem = new ActionBarMenuItem(context2, (ActionBarMenu) null, 0, Theme.getColor("windowBackgroundWhiteBlackText"));
            this.menuItem = actionBarMenuItem;
            actionBarMenuItem.setLongClickEnabled(false);
            this.menuItem.setIcon(NUM);
            this.menuItem.setContentDescription(LocaleController.getString("AccDescrMoreOptions", NUM));
            this.menuItem.addSubItem(1, NUM, (CharSequence) LocaleController.getString("OpenInEditor", NUM));
            this.menuItem.addSubItem(2, NUM, (CharSequence) LocaleController.getString("ShareTheme", NUM));
            this.menuItem.addSubItem(3, NUM, (CharSequence) LocaleController.getString("DeleteTheme", NUM));
            this.menuItem.setMenuYOffset(-AndroidUtilities.dp(80.0f));
            this.menuItem.setSubMenuOpenSide(2);
            this.menuItem.setDelegate(new ColorPicker$$ExternalSyntheticLambda6(this));
            this.menuItem.setAdditionalYOffset(AndroidUtilities.dp(72.0f));
            this.menuItem.setTranslationX((float) AndroidUtilities.dp(6.0f));
            this.menuItem.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("dialogButtonSelector"), 1));
            addView(this.menuItem, LayoutHelper.createFrame(30, 30.0f, 53, 0.0f, 2.0f, 10.0f, 0.0f));
            this.menuItem.setOnClickListener(new ColorPicker$$ExternalSyntheticLambda3(this));
        }
        updateColorsPosition((ArrayList<Animator>) null, 0, false, getMeasuredWidth());
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-ColorPicker  reason: not valid java name */
    public /* synthetic */ void m887lambda$new$0$orgtelegramuiComponentsColorPicker(View v) {
        RadioButton radioButton1 = (RadioButton) v;
        int b = 0;
        while (true) {
            RadioButton[] radioButtonArr = this.radioButton;
            boolean z = false;
            if (b < radioButtonArr.length) {
                if (radioButtonArr[b] == radioButton1) {
                    z = true;
                }
                boolean checked = z;
                radioButtonArr[b].setChecked(checked, true);
                if (checked) {
                    this.selectedColor = b;
                }
                b++;
            } else {
                int b2 = radioButton1.getColor();
                setColorInner(b2);
                this.colorEditText[1].setText(String.format("%02x%02x%02x", new Object[]{Byte.valueOf((byte) Color.red(b2)), Byte.valueOf((byte) Color.green(b2)), Byte.valueOf((byte) Color.blue(b2))}).toUpperCase());
                return;
            }
        }
    }

    static /* synthetic */ boolean lambda$new$1(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 6) {
            return false;
        }
        AndroidUtilities.hideKeyboard(textView);
        return true;
    }

    /* renamed from: lambda$new$2$org-telegram-ui-Components-ColorPicker  reason: not valid java name */
    public /* synthetic */ void m888lambda$new$2$orgtelegramuiComponentsColorPicker(View v) {
        if (this.colorsAnimator == null) {
            int i = this.colorsCount;
            if (i == 1) {
                if (this.radioButton[1].getColor() == 0) {
                    RadioButton[] radioButtonArr = this.radioButton;
                    radioButtonArr[1].setColor(generateGradientColors(radioButtonArr[0].getColor()));
                }
                if (this.myMessagesColor) {
                    this.delegate.setColor(this.radioButton[0].getColor(), 0, true);
                }
                this.delegate.setColor(this.radioButton[1].getColor(), 1, true);
                this.colorsCount = 2;
            } else if (i == 2) {
                this.colorsCount = 3;
                if (this.radioButton[2].getColor() == 0) {
                    float[] hsv = new float[3];
                    Color.colorToHSV(this.radioButton[0].getColor(), hsv);
                    if (hsv[0] > 180.0f) {
                        hsv[0] = hsv[0] - 60.0f;
                    } else {
                        hsv[0] = hsv[0] + 60.0f;
                    }
                    this.radioButton[2].setColor(Color.HSVToColor(255, hsv));
                }
                this.delegate.setColor(this.radioButton[2].getColor(), 2, true);
            } else if (i == 3) {
                this.colorsCount = 4;
                if (this.radioButton[3].getColor() == 0) {
                    RadioButton[] radioButtonArr2 = this.radioButton;
                    radioButtonArr2[3].setColor(generateGradientColors(radioButtonArr2[2].getColor()));
                }
                this.delegate.setColor(this.radioButton[3].getColor(), 3, true);
            } else {
                return;
            }
            ArrayList<Animator> animators = new ArrayList<>();
            if (this.colorsCount < this.maxColorsCount) {
                animators.add(ObjectAnimator.ofFloat(this.addButton, View.ALPHA, new float[]{1.0f}));
                animators.add(ObjectAnimator.ofFloat(this.addButton, View.SCALE_X, new float[]{1.0f}));
                animators.add(ObjectAnimator.ofFloat(this.addButton, View.SCALE_Y, new float[]{1.0f}));
                animators.add(ObjectAnimator.ofFloat(this.addButton, View.TRANSLATION_X, new float[]{(float) ((AndroidUtilities.dp(30.0f) * (this.colorsCount - 1)) + (AndroidUtilities.dp(13.0f) * (this.colorsCount - 1)))}));
            } else {
                animators.add(ObjectAnimator.ofFloat(this.addButton, View.TRANSLATION_X, new float[]{(float) ((AndroidUtilities.dp(30.0f) * (this.colorsCount - 1)) + (AndroidUtilities.dp(13.0f) * (this.colorsCount - 1)))}));
                animators.add(ObjectAnimator.ofFloat(this.addButton, View.ALPHA, new float[]{0.0f}));
                animators.add(ObjectAnimator.ofFloat(this.addButton, View.SCALE_X, new float[]{0.0f}));
                animators.add(ObjectAnimator.ofFloat(this.addButton, View.SCALE_Y, new float[]{0.0f}));
            }
            if (this.colorsCount > 1) {
                if (this.clearButton.getVisibility() != 0) {
                    this.clearButton.setScaleX(0.0f);
                    this.clearButton.setScaleY(0.0f);
                }
                this.clearButton.setVisibility(0);
                animators.add(ObjectAnimator.ofFloat(this.clearButton, View.ALPHA, new float[]{1.0f}));
                animators.add(ObjectAnimator.ofFloat(this.clearButton, View.SCALE_X, new float[]{1.0f}));
                animators.add(ObjectAnimator.ofFloat(this.clearButton, View.SCALE_Y, new float[]{1.0f}));
            }
            this.radioButton[this.colorsCount - 1].callOnClick();
            this.colorsAnimator = new AnimatorSet();
            updateColorsPosition(animators, 0, false, getMeasuredWidth());
            this.colorsAnimator.playTogether(animators);
            this.colorsAnimator.setDuration(180);
            this.colorsAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT);
            this.colorsAnimator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    if (ColorPicker.this.colorsCount == ColorPicker.this.maxColorsCount) {
                        ColorPicker.this.addButton.setVisibility(4);
                    }
                    AnimatorSet unused = ColorPicker.this.colorsAnimator = null;
                }
            });
            this.colorsAnimator.start();
        }
    }

    /* renamed from: lambda$new$3$org-telegram-ui-Components-ColorPicker  reason: not valid java name */
    public /* synthetic */ void m889lambda$new$3$orgtelegramuiComponentsColorPicker(View v) {
        RadioButton[] radioButtonArr;
        if (this.colorsAnimator == null) {
            ArrayList<Animator> animators = new ArrayList<>();
            int i = this.colorsCount;
            if (i == 2) {
                this.colorsCount = 1;
                animators.add(ObjectAnimator.ofFloat(this.clearButton, View.ALPHA, new float[]{0.0f}));
                animators.add(ObjectAnimator.ofFloat(this.clearButton, View.SCALE_X, new float[]{0.0f}));
                animators.add(ObjectAnimator.ofFloat(this.clearButton, View.SCALE_Y, new float[]{0.0f}));
                animators.add(ObjectAnimator.ofFloat(this.addButton, View.TRANSLATION_X, new float[]{0.0f}));
            } else if (i == 3) {
                this.colorsCount = 2;
                animators.add(ObjectAnimator.ofFloat(this.addButton, View.TRANSLATION_X, new float[]{(float) (AndroidUtilities.dp(30.0f) + AndroidUtilities.dp(13.0f))}));
            } else if (i == 4) {
                this.colorsCount = 3;
                animators.add(ObjectAnimator.ofFloat(this.addButton, View.TRANSLATION_X, new float[]{(float) ((AndroidUtilities.dp(30.0f) * 2) + (AndroidUtilities.dp(13.0f) * 2))}));
            } else {
                return;
            }
            if (this.colorsCount < this.maxColorsCount) {
                this.addButton.setVisibility(0);
                animators.add(ObjectAnimator.ofFloat(this.addButton, View.ALPHA, new float[]{1.0f}));
                animators.add(ObjectAnimator.ofFloat(this.addButton, View.SCALE_X, new float[]{1.0f}));
                animators.add(ObjectAnimator.ofFloat(this.addButton, View.SCALE_Y, new float[]{1.0f}));
            } else {
                animators.add(ObjectAnimator.ofFloat(this.addButton, View.ALPHA, new float[]{0.0f}));
                animators.add(ObjectAnimator.ofFloat(this.addButton, View.SCALE_X, new float[]{0.0f}));
                animators.add(ObjectAnimator.ofFloat(this.addButton, View.SCALE_Y, new float[]{0.0f}));
            }
            int i2 = this.selectedColor;
            if (i2 != 3) {
                RadioButton button = this.radioButton[i2];
                int a = i2 + 1;
                while (true) {
                    radioButtonArr = this.radioButton;
                    if (a >= radioButtonArr.length) {
                        break;
                    }
                    radioButtonArr[a - 1] = radioButtonArr[a];
                    a++;
                }
                radioButtonArr[3] = button;
            }
            this.radioButton[0].callOnClick();
            int a2 = 0;
            while (true) {
                RadioButton[] radioButtonArr2 = this.radioButton;
                if (a2 < radioButtonArr2.length) {
                    if (a2 < this.colorsCount) {
                        this.delegate.setColor(radioButtonArr2[a2].getColor(), a2, a2 == this.radioButton.length - 1);
                    } else {
                        this.delegate.setColor(0, a2, a2 == radioButtonArr2.length - 1);
                    }
                    a2++;
                } else {
                    this.colorsAnimator = new AnimatorSet();
                    updateColorsPosition(animators, this.selectedColor, true, getMeasuredWidth());
                    this.colorsAnimator.playTogether(animators);
                    this.colorsAnimator.setDuration(180);
                    this.colorsAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT);
                    this.colorsAnimator.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animation) {
                            if (ColorPicker.this.colorsCount == 1) {
                                ColorPicker.this.clearButton.setVisibility(4);
                            }
                            for (int a = 0; a < ColorPicker.this.radioButton.length; a++) {
                                if (ColorPicker.this.radioButton[a].getTag(NUM) == null) {
                                    ColorPicker.this.radioButton[a].setVisibility(4);
                                }
                            }
                            AnimatorSet unused = ColorPicker.this.colorsAnimator = null;
                        }
                    });
                    this.colorsAnimator.start();
                    return;
                }
            }
        }
    }

    static /* synthetic */ void lambda$new$4(View v) {
    }

    /* renamed from: lambda$new$5$org-telegram-ui-Components-ColorPicker  reason: not valid java name */
    public /* synthetic */ void m890lambda$new$5$orgtelegramuiComponentsColorPicker(int id) {
        boolean z = true;
        if (id == 1 || id == 2) {
            ColorPickerDelegate colorPickerDelegate = this.delegate;
            if (id != 2) {
                z = false;
            }
            colorPickerDelegate.openThemeCreate(z);
        } else if (id == 3) {
            this.delegate.deleteTheme();
        }
    }

    /* renamed from: lambda$new$6$org-telegram-ui-Components-ColorPicker  reason: not valid java name */
    public /* synthetic */ void m891lambda$new$6$orgtelegramuiComponentsColorPicker(View v) {
        this.menuItem.toggleSubMenu();
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        updateColorsPosition((ArrayList<Animator>) null, 0, false, getMeasuredWidth());
    }

    private void updateColorsPosition(ArrayList<Animator> animators, int hidingIndex, boolean hiding, int width) {
        float tr;
        ArrayList<Animator> arrayList = animators;
        int allX = 0;
        int count = this.colorsCount;
        int left = this.radioContainer.getLeft() + (AndroidUtilities.dp(30.0f) * count) + ((count - 1) * AndroidUtilities.dp(13.0f));
        int w = width - AndroidUtilities.dp(this.currentResetType == 1 ? 50.0f : 0.0f);
        if (left > w) {
            tr = (float) (left - w);
        } else {
            tr = 0.0f;
        }
        if (arrayList != null) {
            arrayList.add(ObjectAnimator.ofFloat(this.radioContainer, View.TRANSLATION_X, new float[]{-tr}));
        } else {
            this.radioContainer.setTranslationX(-tr);
        }
        int a = 0;
        while (true) {
            RadioButton[] radioButtonArr = this.radioButton;
            if (a < radioButtonArr.length) {
                boolean wasVisible = radioButtonArr[a].getTag(NUM) != null;
                if (a < this.colorsCount) {
                    this.radioButton[a].setVisibility(0);
                    if (arrayList != null) {
                        if (!wasVisible) {
                            arrayList.add(ObjectAnimator.ofFloat(this.radioButton[a], View.ALPHA, new float[]{1.0f}));
                            arrayList.add(ObjectAnimator.ofFloat(this.radioButton[a], View.SCALE_X, new float[]{1.0f}));
                            arrayList.add(ObjectAnimator.ofFloat(this.radioButton[a], View.SCALE_Y, new float[]{1.0f}));
                        }
                        if (hiding || (!hiding && a != this.colorsCount - 1)) {
                            arrayList.add(ObjectAnimator.ofFloat(this.radioButton[a], View.TRANSLATION_X, new float[]{(float) allX}));
                        } else {
                            this.radioButton[a].setTranslationX((float) allX);
                        }
                    } else {
                        this.radioButton[a].setVisibility(0);
                        if (this.colorsAnimator == null) {
                            this.radioButton[a].setAlpha(1.0f);
                            this.radioButton[a].setScaleX(1.0f);
                            this.radioButton[a].setScaleY(1.0f);
                        }
                        this.radioButton[a].setTranslationX((float) allX);
                    }
                    this.radioButton[a].setTag(NUM, 1);
                } else {
                    if (arrayList == null) {
                        this.radioButton[a].setVisibility(4);
                        if (this.colorsAnimator == null) {
                            this.radioButton[a].setAlpha(0.0f);
                            this.radioButton[a].setScaleX(0.0f);
                            this.radioButton[a].setScaleY(0.0f);
                        }
                    } else if (wasVisible) {
                        arrayList.add(ObjectAnimator.ofFloat(this.radioButton[a], View.ALPHA, new float[]{0.0f}));
                        arrayList.add(ObjectAnimator.ofFloat(this.radioButton[a], View.SCALE_X, new float[]{0.0f}));
                        arrayList.add(ObjectAnimator.ofFloat(this.radioButton[a], View.SCALE_Y, new float[]{0.0f}));
                    }
                    if (!hiding) {
                        this.radioButton[a].setTranslationX((float) allX);
                    }
                    this.radioButton[a].setTag(NUM, (Object) null);
                }
                allX += AndroidUtilities.dp(30.0f) + AndroidUtilities.dp(13.0f);
                a++;
            } else {
                return;
            }
        }
    }

    public void hideKeyboard() {
        AndroidUtilities.hideKeyboard(this.colorEditText[1]);
    }

    private int getIndex(int num) {
        if (num == 1) {
            return 0;
        }
        if (num == 3) {
            return 1;
        }
        if (num == 5) {
            return 2;
        }
        return 3;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        int colorPointY;
        int colorPointX;
        float f;
        Canvas canvas2 = canvas;
        int top = AndroidUtilities.dp(45.0f);
        canvas2.drawBitmap(this.colorWheelBitmap, 0.0f, (float) top, (Paint) null);
        int y = top + this.colorWheelBitmap.getHeight();
        canvas.drawRect(0.0f, (float) top, (float) getMeasuredWidth(), (float) (top + 1), this.linePaint);
        canvas.drawRect(0.0f, (float) (y - 1), (float) getMeasuredWidth(), (float) y, this.linePaint);
        float[] fArr = this.hsvTemp;
        float[] fArr2 = this.colorHSV;
        fArr[0] = fArr2[0];
        fArr[1] = fArr2[1];
        fArr[2] = 1.0f;
        int colorPointX2 = (int) ((fArr2[0] * ((float) getMeasuredWidth())) / 360.0f);
        int colorPointY2 = (int) (((float) top) + (((float) this.colorWheelBitmap.getHeight()) * (1.0f - this.colorHSV[1])));
        if (!this.circlePressed) {
            int minD = AndroidUtilities.dp(16.0f);
            float progress = CubicBezierInterpolator.EASE_OUT.getInterpolation(this.pressedMoveProgress);
            if (colorPointX2 < minD) {
                colorPointX2 = (int) (((float) colorPointX2) + (((float) (minD - colorPointX2)) * progress));
            } else if (colorPointX2 > getMeasuredWidth() - minD) {
                colorPointX2 = (int) (((float) colorPointX2) - (((float) (colorPointX2 - (getMeasuredWidth() - minD))) * progress));
            }
            if (colorPointY2 < top + minD) {
                colorPointX = colorPointX2;
                colorPointY = (int) (((float) colorPointY2) + (((float) ((top + minD) - colorPointY2)) * progress));
            } else if (colorPointY2 > (this.colorWheelBitmap.getHeight() + top) - minD) {
                colorPointX = colorPointX2;
                colorPointY = (int) (((float) colorPointY2) - (((float) (colorPointY2 - ((this.colorWheelBitmap.getHeight() + top) - minD))) * progress));
            } else {
                colorPointX = colorPointX2;
                colorPointY = colorPointY2;
            }
        } else {
            colorPointX = colorPointX2;
            colorPointY = colorPointY2;
        }
        drawPointerArrow(canvas, colorPointX, colorPointY, Color.HSVToColor(this.hsvTemp), false);
        this.sliderRect.set((float) AndroidUtilities.dp(22.0f), (float) (AndroidUtilities.dp(26.0f) + y), (float) (getMeasuredWidth() - AndroidUtilities.dp(22.0f)), (float) (AndroidUtilities.dp(34.0f) + y));
        if (this.colorGradient == null) {
            float[] fArr3 = this.hsvTemp;
            fArr3[2] = this.minHsvBrightness;
            int minColor = Color.HSVToColor(fArr3);
            float[] fArr4 = this.hsvTemp;
            fArr4[2] = this.maxHsvBrightness;
            int maxColor = Color.HSVToColor(fArr4);
            float f2 = this.sliderRect.left;
            float f3 = this.sliderRect.top;
            float f4 = this.sliderRect.right;
            LinearGradient linearGradient = new LinearGradient(f2, f3, f4, this.sliderRect.top, new int[]{maxColor, minColor}, (float[]) null, Shader.TileMode.CLAMP);
            this.colorGradient = linearGradient;
            this.valueSliderPaint.setShader(linearGradient);
        }
        canvas2.drawRoundRect(this.sliderRect, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.valueSliderPaint);
        if (this.minHsvBrightness == this.maxHsvBrightness) {
            f = 0.5f;
        } else {
            float brightness = getBrightness();
            float f5 = this.minHsvBrightness;
            f = (brightness - f5) / (this.maxHsvBrightness - f5);
        }
        float value = f;
        drawPointerArrow(canvas, (int) (this.sliderRect.left + ((1.0f - value) * this.sliderRect.width())), (int) this.sliderRect.centerY(), getColor(), true);
        if (!this.circlePressed && this.pressedMoveProgress < 1.0f) {
            long newTime = SystemClock.elapsedRealtime();
            this.lastUpdateTime = newTime;
            float f6 = this.pressedMoveProgress + (((float) (newTime - this.lastUpdateTime)) / 180.0f);
            this.pressedMoveProgress = f6;
            if (f6 > 1.0f) {
                this.pressedMoveProgress = 1.0f;
            }
            invalidate();
        }
    }

    /* access modifiers changed from: private */
    public int getFieldColor(int num, int defaultColor) {
        try {
            return Integer.parseInt(this.colorEditText[num].getText().toString(), 16) | -16777216;
        } catch (Exception e) {
            return defaultColor;
        }
    }

    private void drawPointerArrow(Canvas canvas, int x, int y, int color, boolean small) {
        int side = AndroidUtilities.dp(small ? 12.0f : 16.0f);
        this.circleDrawable.setBounds(x - side, y - side, x + side, y + side);
        this.circleDrawable.draw(canvas);
        this.circlePaint.setColor(-1);
        canvas.drawCircle((float) x, (float) y, (float) AndroidUtilities.dp(small ? 11.0f : 15.0f), this.circlePaint);
        this.circlePaint.setColor(color);
        canvas.drawCircle((float) x, (float) y, (float) AndroidUtilities.dp(small ? 9.0f : 13.0f), this.circlePaint);
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int width, int height, int oldw, int oldh) {
        if (this.colorWheelWidth != width) {
            this.colorWheelWidth = width;
            this.colorWheelBitmap = createColorWheelBitmap(width, AndroidUtilities.dp(180.0f));
            this.colorGradient = null;
        }
    }

    private Bitmap createColorWheelBitmap(int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        this.colorWheelPaint.setShader(new ComposeShader(new LinearGradient(0.0f, (float) (height / 3), 0.0f, (float) height, new int[]{-1, 0}, (float[]) null, Shader.TileMode.CLAMP), new LinearGradient(0.0f, 0.0f, (float) width, 0.0f, new int[]{-65536, -256, -16711936, -16711681, -16776961, -65281, -65536}, (float[]) null, Shader.TileMode.CLAMP), PorterDuff.Mode.MULTIPLY));
        new Canvas(bitmap).drawRect(0.0f, 0.0f, (float) width, (float) height, this.colorWheelPaint);
        return bitmap;
    }

    public boolean onTouchEvent(MotionEvent event) {
        float oldBrightnessPos;
        switch (event.getAction()) {
            case 0:
            case 2:
                int x = (int) event.getX();
                int y = (int) event.getY();
                int top = AndroidUtilities.dp(45.0f);
                if (this.circlePressed || (!this.colorPressed && y >= top && y <= this.colorWheelBitmap.getHeight() + top)) {
                    if (!this.circlePressed) {
                        getParent().requestDisallowInterceptTouchEvent(true);
                    }
                    this.circlePressed = true;
                    this.pressedMoveProgress = 0.0f;
                    this.lastUpdateTime = SystemClock.elapsedRealtime();
                    x = Math.max(0, Math.min(x, this.colorWheelBitmap.getWidth()));
                    y = Math.max(top, Math.min(y, this.colorWheelBitmap.getHeight() + top));
                    if (this.minHsvBrightness == this.maxHsvBrightness) {
                        oldBrightnessPos = 0.5f;
                    } else {
                        float brightness = getBrightness();
                        float f = this.minHsvBrightness;
                        oldBrightnessPos = (brightness - f) / (this.maxHsvBrightness - f);
                    }
                    this.colorHSV[0] = (((float) x) * 360.0f) / ((float) this.colorWheelBitmap.getWidth());
                    this.colorHSV[1] = 1.0f - ((1.0f / ((float) this.colorWheelBitmap.getHeight())) * ((float) (y - top)));
                    updateHsvMinMaxBrightness();
                    this.colorHSV[2] = (this.minHsvBrightness * (1.0f - oldBrightnessPos)) + (this.maxHsvBrightness * oldBrightnessPos);
                    this.colorGradient = null;
                }
                if (this.colorPressed || (!this.circlePressed && ((float) x) >= this.sliderRect.left && ((float) x) <= this.sliderRect.right && ((float) y) >= this.sliderRect.top - ((float) AndroidUtilities.dp(7.0f)) && ((float) y) <= this.sliderRect.bottom + ((float) AndroidUtilities.dp(7.0f)))) {
                    float value = 1.0f - ((((float) x) - this.sliderRect.left) / this.sliderRect.width());
                    if (value < 0.0f) {
                        value = 0.0f;
                    } else if (value > 1.0f) {
                        value = 1.0f;
                    }
                    this.colorHSV[2] = (this.minHsvBrightness * (1.0f - value)) + (this.maxHsvBrightness * value);
                    if (!this.colorPressed) {
                        getParent().requestDisallowInterceptTouchEvent(true);
                    }
                    this.colorPressed = true;
                }
                if (this.colorPressed || this.circlePressed) {
                    int color = getColor();
                    if (!this.ignoreTextChange) {
                        int red = Color.red(color);
                        int green = Color.green(color);
                        int blue = Color.blue(color);
                        this.ignoreTextChange = true;
                        String text = String.format("%02x%02x%02x", new Object[]{Byte.valueOf((byte) red), Byte.valueOf((byte) green), Byte.valueOf((byte) blue)}).toUpperCase();
                        Editable editable = this.colorEditText[1].getText();
                        editable.replace(0, editable.length(), text);
                        this.radioButton[this.selectedColor].setColor(color);
                        this.ignoreTextChange = false;
                    }
                    this.delegate.setColor(color, this.selectedColor, false);
                    invalidate();
                }
                return true;
            case 1:
                this.colorPressed = false;
                this.circlePressed = false;
                this.lastUpdateTime = SystemClock.elapsedRealtime();
                invalidate();
                break;
        }
        return super.onTouchEvent(event);
    }

    /* access modifiers changed from: private */
    public void setColorInner(int color) {
        Color.colorToHSV(color, this.colorHSV);
        int defaultColor = this.delegate.getDefaultColor(this.selectedColor);
        if (defaultColor == 0 || defaultColor != color) {
            updateHsvMinMaxBrightness();
        }
        this.colorGradient = null;
        invalidate();
    }

    public void setColor(int color, int num) {
        if (!this.ignoreTextChange) {
            this.ignoreTextChange = true;
            if (this.selectedColor == num) {
                String text = String.format("%02x%02x%02x", new Object[]{Byte.valueOf((byte) Color.red(color)), Byte.valueOf((byte) Color.green(color)), Byte.valueOf((byte) Color.blue(color))}).toUpperCase();
                this.colorEditText[1].setText(text);
                this.colorEditText[1].setSelection(text.length());
            }
            this.radioButton[num].setColor(color);
            this.ignoreTextChange = false;
        }
        setColorInner(color);
    }

    public void setHasChanges(final boolean value) {
        if (value && this.resetButton.getTag() != null) {
            return;
        }
        if ((value || this.resetButton.getTag() != null) && this.clearButton.getTag() == null) {
            this.resetButton.setTag(value ? 1 : null);
            AnimatorSet animatorSet = new AnimatorSet();
            ArrayList<Animator> animators = new ArrayList<>();
            if (value) {
                this.resetButton.setVisibility(0);
            }
            TextView textView = this.resetButton;
            Property property = View.ALPHA;
            float[] fArr = new float[1];
            fArr[0] = value ? 1.0f : 0.0f;
            animators.add(ObjectAnimator.ofFloat(textView, property, fArr));
            animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    if (!value) {
                        ColorPicker.this.resetButton.setVisibility(8);
                    }
                }
            });
            animatorSet.playTogether(animators);
            animatorSet.setDuration(180);
            animatorSet.start();
        }
    }

    public void setType(int resetType, boolean hasChanges, final int maxColorsCount2, int newColorsCount, boolean myMessages, int angle, boolean animated) {
        ArrayList<Animator> animators;
        if (resetType != this.currentResetType) {
            this.selectedColor = 0;
            int i = 0;
            while (i < 4) {
                this.radioButton[i].setChecked(i == this.selectedColor, true);
                i++;
            }
        }
        this.maxColorsCount = maxColorsCount2;
        this.currentResetType = resetType;
        this.myMessagesColor = myMessages;
        this.colorsCount = newColorsCount;
        if (newColorsCount == 1) {
            this.addButton.setTranslationX(0.0f);
        } else if (newColorsCount == 2) {
            this.addButton.setTranslationX((float) (AndroidUtilities.dp(30.0f) + AndroidUtilities.dp(13.0f)));
        } else if (newColorsCount == 3) {
            this.addButton.setTranslationX((float) ((AndroidUtilities.dp(30.0f) * 2) + (AndroidUtilities.dp(13.0f) * 2)));
        } else {
            this.addButton.setTranslationX((float) ((AndroidUtilities.dp(30.0f) * 3) + (AndroidUtilities.dp(13.0f) * 3)));
        }
        ActionBarMenuItem actionBarMenuItem = this.menuItem;
        if (actionBarMenuItem != null) {
            if (resetType == 1) {
                actionBarMenuItem.setVisibility(0);
            } else {
                actionBarMenuItem.setVisibility(8);
                this.clearButton.setTranslationX(0.0f);
            }
        }
        if (maxColorsCount2 <= 1) {
            this.addButton.setVisibility(8);
            this.clearButton.setVisibility(8);
        } else {
            if (newColorsCount < maxColorsCount2) {
                this.addButton.setVisibility(0);
                this.addButton.setScaleX(1.0f);
                this.addButton.setScaleY(1.0f);
                this.addButton.setAlpha(1.0f);
            } else {
                this.addButton.setVisibility(8);
            }
            if (newColorsCount > 1) {
                this.clearButton.setVisibility(0);
                this.clearButton.setScaleX(1.0f);
                this.clearButton.setScaleY(1.0f);
                this.clearButton.setAlpha(1.0f);
            } else {
                this.clearButton.setVisibility(8);
            }
        }
        this.linearLayout.invalidate();
        updateColorsPosition((ArrayList<Animator>) null, 0, false, getMeasuredWidth());
        if (animated) {
            animators = new ArrayList<>();
        } else {
            animators = null;
        }
        if (animators != null && !animators.isEmpty()) {
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(animators);
            animatorSet.setDuration(180);
            animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    if (maxColorsCount2 <= 1) {
                        ColorPicker.this.clearButton.setVisibility(8);
                    }
                }
            });
            animatorSet.start();
        }
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
        return Math.max(this.minHsvBrightness, Math.min(this.colorHSV[2], this.maxHsvBrightness));
    }

    private void updateHsvMinMaxBrightness() {
        ImageView imageView = this.clearButton;
        if (imageView != null) {
            float min = imageView.getTag() != null ? 0.0f : this.minBrightness;
            float max = this.clearButton.getTag() != null ? 1.0f : this.maxBrightness;
            float[] fArr = this.colorHSV;
            float hsvBrightness = fArr[2];
            if (min == 0.0f && max == 1.0f) {
                this.minHsvBrightness = 0.0f;
                this.maxHsvBrightness = 1.0f;
                return;
            }
            fArr[2] = 1.0f;
            int maxColor = Color.HSVToColor(fArr);
            this.colorHSV[2] = hsvBrightness;
            float maxPerceivedBrightness = AndroidUtilities.computePerceivedBrightness(maxColor);
            float max2 = Math.max(0.0f, Math.min(min / maxPerceivedBrightness, 1.0f));
            this.minHsvBrightness = max2;
            this.maxHsvBrightness = Math.max(max2, Math.min(max / maxPerceivedBrightness, 1.0f));
        }
    }

    public void setMinBrightness(float limit) {
        this.minBrightness = limit;
        updateHsvMinMaxBrightness();
    }

    public void setMaxBrightness(float limit) {
        this.maxBrightness = limit;
        updateHsvMinMaxBrightness();
    }

    public void provideThemeDescriptions(List<ThemeDescription> arrayList) {
        List<ThemeDescription> list = arrayList;
        for (int a = 0; a < this.colorEditText.length; a++) {
            list.add(new ThemeDescription(this.colorEditText[a], ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
            list.add(new ThemeDescription(this.colorEditText[a], ThemeDescription.FLAG_CURSORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
            list.add(new ThemeDescription(this.colorEditText[a], ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteHintText"));
            list.add(new ThemeDescription(this.colorEditText[a], ThemeDescription.FLAG_HINTTEXTCOLOR | ThemeDescription.FLAG_PROGRESSBAR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"));
            list.add(new ThemeDescription(this.colorEditText[a], ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputField"));
            list.add(new ThemeDescription(this.colorEditText[a], ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputFieldActivated"));
        }
        list.add(new ThemeDescription(this.clearButton, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        list.add(new ThemeDescription(this.clearButton, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogButtonSelector"));
        if (this.menuItem != null) {
            ThemeDescription.ThemeDescriptionDelegate delegate2 = new ColorPicker$$ExternalSyntheticLambda7(this);
            list.add(new ThemeDescription(this.menuItem, 0, (Class[]) null, (Paint) null, (Drawable[]) null, delegate2, "windowBackgroundWhiteBlackText"));
            list.add(new ThemeDescription(this.menuItem, 0, (Class[]) null, (Paint) null, (Drawable[]) null, delegate2, "dialogButtonSelector"));
            list.add(new ThemeDescription(this.menuItem, 0, (Class[]) null, (Paint) null, (Drawable[]) null, delegate2, "actionBarDefaultSubmenuItem"));
            list.add(new ThemeDescription(this.menuItem, 0, (Class[]) null, (Paint) null, (Drawable[]) null, delegate2, "actionBarDefaultSubmenuItemIcon"));
            list.add(new ThemeDescription(this.menuItem, 0, (Class[]) null, (Paint) null, (Drawable[]) null, delegate2, "actionBarDefaultSubmenuBackground"));
        }
    }

    /* renamed from: lambda$provideThemeDescriptions$7$org-telegram-ui-Components-ColorPicker  reason: not valid java name */
    public /* synthetic */ void m892xdb6var_() {
        this.menuItem.setIconColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        Theme.setDrawableColor(this.menuItem.getBackground(), Theme.getColor("dialogButtonSelector"));
        this.menuItem.setPopupItemsColor(Theme.getColor("actionBarDefaultSubmenuItem"), false);
        this.menuItem.setPopupItemsColor(Theme.getColor("actionBarDefaultSubmenuItemIcon"), true);
        this.menuItem.redrawPopup(Theme.getColor("actionBarDefaultSubmenuBackground"));
    }

    public interface ColorPickerDelegate {
        void deleteTheme();

        int getDefaultColor(int i);

        boolean hasChanges();

        void openThemeCreate(boolean z);

        void rotateColors();

        void setColor(int i, int i2, boolean z);

        /* renamed from: org.telegram.ui.Components.ColorPicker$ColorPickerDelegate$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static void $default$openThemeCreate(ColorPickerDelegate _this, boolean share) {
            }

            public static void $default$deleteTheme(ColorPickerDelegate _this) {
            }

            public static void $default$rotateColors(ColorPickerDelegate _this) {
            }

            public static int $default$getDefaultColor(ColorPickerDelegate _this, int num) {
                return 0;
            }

            public static boolean $default$hasChanges(ColorPickerDelegate _this) {
                return true;
            }
        }
    }

    public static int generateGradientColors(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        if (hsv[1] > 0.5f) {
            hsv[1] = hsv[1] - 0.15f;
        } else {
            hsv[1] = hsv[1] + 0.15f;
        }
        if (hsv[0] > 180.0f) {
            hsv[0] = hsv[0] - 20.0f;
        } else {
            hsv[0] = hsv[0] + 20.0f;
        }
        return Color.HSVToColor(255, hsv);
    }
}
