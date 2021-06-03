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
import androidx.annotation.Keep;
import java.util.ArrayList;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;

public class ColorPicker extends FrameLayout {
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
    /* access modifiers changed from: private */
    public ImageView exchangeButton;
    private float[] hsvTemp = new float[3];
    boolean ignoreTextChange;
    private long lastUpdateTime;
    private Paint linePaint;
    /* access modifiers changed from: private */
    public LinearLayout linearLayout;
    private float maxBrightness = 1.0f;
    private float maxHsvBrightness = 1.0f;
    private ActionBarMenuItem menuItem;
    private float minBrightness = 0.0f;
    private float minHsvBrightness = 0.0f;
    /* access modifiers changed from: private */
    public boolean myMessagesColor;
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

    public interface ColorPickerDelegate {
        void deleteTheme();

        int getDefaultColor(int i);

        void openThemeCreate(boolean z);

        void setColor(int i, int i2, boolean z);
    }

    static /* synthetic */ void lambda$new$5(View view) {
    }

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
        public void updateCheckedState(boolean z) {
            ObjectAnimator objectAnimator = this.checkAnimator;
            if (objectAnimator != null) {
                objectAnimator.cancel();
            }
            float f = 1.0f;
            if (z) {
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

        public void setChecked(boolean z, boolean z2) {
            this.checked = z;
            updateCheckedState(z2);
        }

        public void setColor(int i) {
            this.currentColor = i;
            invalidate();
        }

        public int getColor() {
            return this.currentColor;
        }

        @Keep
        public void setCheckedState(float f) {
            this.checkedState = f;
            invalidate();
        }

        @Keep
        public float getCheckedState() {
            return this.checkedState;
        }

        /* access modifiers changed from: protected */
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            updateCheckedState(false);
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(30.0f), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(30.0f), NUM));
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            float dp = (float) AndroidUtilities.dp(15.0f);
            float measuredWidth = ((float) getMeasuredWidth()) * 0.5f;
            float measuredHeight = ((float) getMeasuredHeight()) * 0.5f;
            this.paint.setColor(this.currentColor);
            this.paint.setStyle(Paint.Style.STROKE);
            this.paint.setStrokeWidth((float) AndroidUtilities.dp(3.0f));
            this.paint.setAlpha(Math.round(this.checkedState * 255.0f));
            canvas.drawCircle(measuredWidth, measuredHeight, dp - (this.paint.getStrokeWidth() * 0.5f), this.paint);
            this.paint.setAlpha(255);
            this.paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(measuredWidth, measuredHeight, dp - (((float) AndroidUtilities.dp(5.0f)) * this.checkedState), this.paint);
        }

        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
            super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
            accessibilityNodeInfo.setText(LocaleController.getString("ColorPickerMainColor", NUM));
            accessibilityNodeInfo.setClassName(Button.class.getName());
            accessibilityNodeInfo.setChecked(this.checked);
            accessibilityNodeInfo.setCheckable(true);
            accessibilityNodeInfo.setEnabled(true);
        }
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public ColorPicker(Context context, boolean z, ColorPickerDelegate colorPickerDelegate) {
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
        AnonymousClass1 r8 = new LinearLayout(context2) {
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
        this.linearLayout = r8;
        r8.setOrientation(0);
        addView(this.linearLayout, LayoutHelper.createFrame(-1, 54.0f, 51, 27.0f, -6.0f, 17.0f, 0.0f));
        this.linearLayout.setWillNotDraw(false);
        FrameLayout frameLayout = new FrameLayout(context2);
        this.radioContainer = frameLayout;
        frameLayout.setClipChildren(false);
        addView(this.radioContainer, LayoutHelper.createFrame(174, 30.0f, 49, 72.0f, 1.0f, 0.0f, 0.0f));
        int i = 0;
        while (i < 4) {
            this.radioButton[i] = new RadioButton(context2);
            this.radioButton[i].setChecked(this.selectedColor == i, false);
            this.radioContainer.addView(this.radioButton[i], LayoutHelper.createFrame(30, 30.0f, 48, 0.0f, 0.0f, 0.0f, 0.0f));
            this.radioButton[i].setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ColorPicker.this.lambda$new$0$ColorPicker(view);
                }
            });
            i++;
        }
        final int i2 = 0;
        while (true) {
            EditTextBoldCursor[] editTextBoldCursorArr = this.colorEditText;
            if (i2 >= editTextBoldCursorArr.length) {
                break;
            }
            if (i2 % 2 == 0) {
                editTextBoldCursorArr[i2] = new EditTextBoldCursor(context2) {
                    public boolean onTouchEvent(MotionEvent motionEvent) {
                        if (getAlpha() == 1.0f && motionEvent.getAction() == 0) {
                            if (ColorPicker.this.colorEditText[i2 + 1].isFocused()) {
                                AndroidUtilities.showKeyboard(ColorPicker.this.colorEditText[i2 + 1]);
                            } else {
                                ColorPicker.this.colorEditText[i2 + 1].requestFocus();
                            }
                        }
                        return false;
                    }
                };
                this.colorEditText[i2].setBackgroundDrawable((Drawable) null);
                this.colorEditText[i2].setText("#");
                this.colorEditText[i2].setEnabled(false);
                this.colorEditText[i2].setFocusable(false);
                this.colorEditText[i2].setPadding(0, AndroidUtilities.dp(5.0f), 0, AndroidUtilities.dp(16.0f));
                this.linearLayout.addView(this.colorEditText[i2], LayoutHelper.createLinear(-2, -1, 0.0f, 0.0f, 0.0f, 0.0f));
            } else {
                editTextBoldCursorArr[i2] = new EditTextBoldCursor(context2) {
                    public boolean onTouchEvent(MotionEvent motionEvent) {
                        if (getAlpha() != 1.0f) {
                            return false;
                        }
                        if (!isFocused()) {
                            requestFocus();
                            return false;
                        }
                        AndroidUtilities.showKeyboard(this);
                        return super.onTouchEvent(motionEvent);
                    }

                    public boolean getGlobalVisibleRect(Rect rect, Point point) {
                        boolean globalVisibleRect = super.getGlobalVisibleRect(rect, point);
                        rect.bottom += AndroidUtilities.dp(40.0f);
                        return globalVisibleRect;
                    }

                    public void invalidate() {
                        super.invalidate();
                        ColorPicker.this.colorEditText[i2 - 1].invalidate();
                    }
                };
                this.colorEditText[i2].setBackgroundDrawable((Drawable) null);
                this.colorEditText[i2].setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});
                this.colorEditText[i2].setHint("8BC6ED");
                this.colorEditText[i2].setPadding(0, AndroidUtilities.dp(5.0f), 0, AndroidUtilities.dp(16.0f));
                this.linearLayout.addView(this.colorEditText[i2], LayoutHelper.createLinear(71, -1, 0.0f, 0.0f, 0.0f, 0.0f));
                this.colorEditText[i2].addTextChangedListener(new TextWatcher() {
                    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    }

                    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    }

                    public void afterTextChanged(Editable editable) {
                        ColorPicker colorPicker = ColorPicker.this;
                        if (!colorPicker.ignoreTextChange) {
                            colorPicker.ignoreTextChange = true;
                            int i = 0;
                            while (i < editable.length()) {
                                char charAt = editable.charAt(i);
                                if ((charAt < '0' || charAt > '9') && ((charAt < 'a' || charAt > 'f') && (charAt < 'A' || charAt > 'F'))) {
                                    editable.replace(i, i + 1, "");
                                    i--;
                                }
                                i++;
                            }
                            if (editable.length() == 0) {
                                ColorPicker.this.ignoreTextChange = false;
                                return;
                            }
                            ColorPicker colorPicker2 = ColorPicker.this;
                            colorPicker2.setColorInner(colorPicker2.getFieldColor(i2, -1));
                            int color = ColorPicker.this.getColor();
                            if (editable.length() == 6) {
                                editable.replace(0, editable.length(), String.format("%02x%02x%02x", new Object[]{Byte.valueOf((byte) Color.red(color)), Byte.valueOf((byte) Color.green(color)), Byte.valueOf((byte) Color.blue(color))}).toUpperCase());
                                ColorPicker.this.colorEditText[i2].setSelection(editable.length());
                            }
                            ColorPicker.this.radioButton[ColorPicker.this.selectedColor].setColor(color);
                            ColorPicker.this.delegate.setColor(color, ColorPicker.this.selectedColor, true);
                            ColorPicker.this.ignoreTextChange = false;
                        }
                    }
                });
                this.colorEditText[i2].setOnEditorActionListener($$Lambda$ColorPicker$Zc7eIW9hmbbuwjjRymCs8IqW4Q.INSTANCE);
            }
            this.colorEditText[i2].setTextSize(1, 16.0f);
            this.colorEditText[i2].setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
            this.colorEditText[i2].setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.colorEditText[i2].setCursorColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.colorEditText[i2].setCursorSize(AndroidUtilities.dp(18.0f));
            this.colorEditText[i2].setCursorWidth(1.5f);
            this.colorEditText[i2].setSingleLine(true);
            this.colorEditText[i2].setGravity(19);
            this.colorEditText[i2].setHeaderHintColor(Theme.getColor("windowBackgroundWhiteBlueHeader"));
            this.colorEditText[i2].setTransformHintToHeader(true);
            this.colorEditText[i2].setInputType(524416);
            this.colorEditText[i2].setImeOptions(NUM);
            if (i2 == 1) {
                this.colorEditText[i2].requestFocus();
            } else if (i2 == 2 || i2 == 3) {
                this.colorEditText[i2].setVisibility(8);
            }
            i2++;
        }
        ImageView imageView = new ImageView(getContext());
        this.exchangeButton = imageView;
        imageView.setBackground(Theme.createSelectorDrawable(Theme.getColor("dialogButtonSelector"), 1));
        this.exchangeButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteBlackText"), PorterDuff.Mode.MULTIPLY));
        this.exchangeButton.setScaleType(ImageView.ScaleType.CENTER);
        this.exchangeButton.setVisibility(8);
        this.exchangeButton.setImageResource(NUM);
        this.exchangeButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                ColorPicker.this.lambda$new$2$ColorPicker(view);
            }
        });
        this.radioContainer.addView(this.exchangeButton, 1, LayoutHelper.createFrame(30, 30.0f, 51, 0.0f, 1.0f, 0.0f, 0.0f));
        ImageView imageView2 = new ImageView(getContext());
        this.addButton = imageView2;
        imageView2.setBackground(Theme.createSelectorDrawable(Theme.getColor("dialogButtonSelector"), 1));
        this.addButton.setImageResource(NUM);
        this.addButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteBlackText"), PorterDuff.Mode.MULTIPLY));
        this.addButton.setScaleType(ImageView.ScaleType.CENTER);
        this.addButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                ColorPicker.this.lambda$new$3$ColorPicker(view);
            }
        });
        this.addButton.setContentDescription(LocaleController.getString("Add", NUM));
        addView(this.addButton, LayoutHelper.createFrame(30, 30.0f, 49, 36.0f, 1.0f, 0.0f, 0.0f));
        AnonymousClass6 r2 = new ImageView(getContext()) {
            public void setAlpha(float f) {
                super.setAlpha(f);
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
        this.clearButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                ColorPicker.this.lambda$new$4$ColorPicker(view);
            }
        });
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
        this.resetButton.setOnClickListener($$Lambda$ColorPicker$syEQkClvdDnLfY8eHlLzSDMvD84.INSTANCE);
        if (z) {
            ActionBarMenuItem actionBarMenuItem = new ActionBarMenuItem(context2, (ActionBarMenu) null, 0, Theme.getColor("windowBackgroundWhiteBlackText"));
            this.menuItem = actionBarMenuItem;
            actionBarMenuItem.setLongClickEnabled(false);
            this.menuItem.setIcon(NUM);
            this.menuItem.setContentDescription(LocaleController.getString("AccDescrMoreOptions", NUM));
            this.menuItem.addSubItem(1, NUM, LocaleController.getString("OpenInEditor", NUM));
            this.menuItem.addSubItem(2, NUM, LocaleController.getString("ShareTheme", NUM));
            this.menuItem.addSubItem(3, NUM, LocaleController.getString("DeleteTheme", NUM));
            this.menuItem.setMenuYOffset(-AndroidUtilities.dp(80.0f));
            this.menuItem.setSubMenuOpenSide(2);
            this.menuItem.setDelegate(new ActionBarMenuItem.ActionBarMenuItemDelegate() {
                public final void onItemClick(int i) {
                    ColorPicker.this.lambda$new$6$ColorPicker(i);
                }
            });
            this.menuItem.setAdditionalYOffset(AndroidUtilities.dp(72.0f));
            this.menuItem.setTranslationX((float) AndroidUtilities.dp(6.0f));
            this.menuItem.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("dialogButtonSelector"), 1));
            addView(this.menuItem, LayoutHelper.createFrame(30, 30.0f, 53, 0.0f, 2.0f, 10.0f, 0.0f));
            this.menuItem.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ColorPicker.this.lambda$new$7$ColorPicker(view);
                }
            });
        }
        updateColorsPosition((ArrayList<Animator>) null, 0, false, getMeasuredWidth());
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$ColorPicker(View view) {
        RadioButton radioButton2 = (RadioButton) view;
        int i = 0;
        while (true) {
            RadioButton[] radioButtonArr = this.radioButton;
            if (i < radioButtonArr.length) {
                boolean z = radioButtonArr[i] == radioButton2;
                radioButtonArr[i].setChecked(z, true);
                if (z) {
                    this.selectedColor = i;
                }
                i++;
            } else {
                int color = radioButton2.getColor();
                setColorInner(color);
                this.colorEditText[1].setText(String.format("%02x%02x%02x", new Object[]{Byte.valueOf((byte) Color.red(color)), Byte.valueOf((byte) Color.green(color)), Byte.valueOf((byte) Color.blue(color))}).toUpperCase());
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$2 */
    public /* synthetic */ void lambda$new$2$ColorPicker(View view) {
        if (this.exchangeButton.getAlpha() == 1.0f) {
            int color = this.radioButton[0].getColor();
            RadioButton[] radioButtonArr = this.radioButton;
            radioButtonArr[0].setColor(radioButtonArr[1].getColor());
            this.radioButton[1].setColor(color);
            this.delegate.setColor(this.radioButton[0].getColor(), 0, false);
            this.delegate.setColor(this.radioButton[1].getColor(), 1, true);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$3 */
    public /* synthetic */ void lambda$new$3$ColorPicker(View view) {
        ArrayList arrayList;
        if (this.colorsAnimator == null) {
            int i = this.colorsCount;
            if (i == 1) {
                if (this.radioButton[1].getColor() == 0) {
                    RadioButton[] radioButtonArr = this.radioButton;
                    radioButtonArr[1].setColor(generateGradientColors(radioButtonArr[0].getColor()));
                }
                this.delegate.setColor(this.radioButton[1].getColor(), 1, true);
                this.colorsCount = 2;
                this.clearButton.setVisibility(0);
                arrayList = new ArrayList();
                arrayList.add(ObjectAnimator.ofFloat(this.clearButton, View.ALPHA, new float[]{1.0f}));
                arrayList.add(ObjectAnimator.ofFloat(this.clearButton, View.SCALE_X, new float[]{1.0f}));
                arrayList.add(ObjectAnimator.ofFloat(this.clearButton, View.SCALE_Y, new float[]{1.0f}));
                arrayList.add(ObjectAnimator.ofFloat(this.addButton, View.TRANSLATION_X, new float[]{(float) (AndroidUtilities.dp(30.0f) + AndroidUtilities.dp(13.0f))}));
                if (this.myMessagesColor) {
                    this.exchangeButton.setVisibility(0);
                    arrayList.add(ObjectAnimator.ofFloat(this.addButton, View.ALPHA, new float[]{0.0f}));
                    arrayList.add(ObjectAnimator.ofFloat(this.addButton, View.SCALE_X, new float[]{0.0f}));
                    arrayList.add(ObjectAnimator.ofFloat(this.addButton, View.SCALE_Y, new float[]{0.0f}));
                    arrayList.add(ObjectAnimator.ofFloat(this.exchangeButton, View.ALPHA, new float[]{1.0f}));
                    arrayList.add(ObjectAnimator.ofFloat(this.exchangeButton, View.SCALE_X, new float[]{1.0f}));
                    arrayList.add(ObjectAnimator.ofFloat(this.exchangeButton, View.SCALE_Y, new float[]{1.0f}));
                }
            } else if (i == 2) {
                if (!this.myMessagesColor) {
                    this.colorsCount = 3;
                    if (this.radioButton[2].getColor() == 0) {
                        float[] fArr = new float[3];
                        Color.colorToHSV(this.radioButton[0].getColor(), fArr);
                        if (fArr[0] > 180.0f) {
                            fArr[0] = fArr[0] - 60.0f;
                        } else {
                            fArr[0] = fArr[0] + 60.0f;
                        }
                        this.radioButton[2].setColor(Color.HSVToColor(255, fArr));
                    }
                    arrayList = new ArrayList();
                    arrayList.add(ObjectAnimator.ofFloat(this.addButton, View.TRANSLATION_X, new float[]{(float) ((AndroidUtilities.dp(30.0f) * 2) + (AndroidUtilities.dp(13.0f) * 2))}));
                    this.delegate.setColor(this.radioButton[2].getColor(), 2, true);
                } else {
                    return;
                }
            } else if (i == 3 && !this.myMessagesColor) {
                this.colorsCount = 4;
                if (this.radioButton[3].getColor() == 0) {
                    RadioButton[] radioButtonArr2 = this.radioButton;
                    radioButtonArr2[3].setColor(generateGradientColors(radioButtonArr2[2].getColor()));
                }
                this.delegate.setColor(this.radioButton[3].getColor(), 3, true);
                arrayList = new ArrayList();
                arrayList.add(ObjectAnimator.ofFloat(this.addButton, View.TRANSLATION_X, new float[]{(float) ((AndroidUtilities.dp(30.0f) * 3) + (AndroidUtilities.dp(13.0f) * 3))}));
                arrayList.add(ObjectAnimator.ofFloat(this.addButton, View.ALPHA, new float[]{0.0f}));
                arrayList.add(ObjectAnimator.ofFloat(this.addButton, View.SCALE_X, new float[]{0.0f}));
                arrayList.add(ObjectAnimator.ofFloat(this.addButton, View.SCALE_Y, new float[]{0.0f}));
            } else {
                return;
            }
            this.radioButton[this.colorsCount - 1].callOnClick();
            this.colorsAnimator = new AnimatorSet();
            updateColorsPosition(arrayList, 0, false, getMeasuredWidth());
            this.colorsAnimator.playTogether(arrayList);
            this.colorsAnimator.setDuration(180);
            this.colorsAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT);
            this.colorsAnimator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    if (ColorPicker.this.colorsCount == 4 || (ColorPicker.this.myMessagesColor && ColorPicker.this.colorsCount == 2)) {
                        ColorPicker.this.addButton.setVisibility(4);
                    }
                    AnimatorSet unused = ColorPicker.this.colorsAnimator = null;
                }
            });
            this.colorsAnimator.start();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$4 */
    public /* synthetic */ void lambda$new$4$ColorPicker(View view) {
        ArrayList arrayList;
        RadioButton[] radioButtonArr;
        if (this.colorsAnimator == null) {
            int i = this.colorsCount;
            if (i == 2) {
                this.colorsCount = 1;
                arrayList = new ArrayList();
                arrayList.add(ObjectAnimator.ofFloat(this.clearButton, View.ALPHA, new float[]{0.0f}));
                arrayList.add(ObjectAnimator.ofFloat(this.clearButton, View.SCALE_X, new float[]{0.0f}));
                arrayList.add(ObjectAnimator.ofFloat(this.clearButton, View.SCALE_Y, new float[]{0.0f}));
                arrayList.add(ObjectAnimator.ofFloat(this.addButton, View.TRANSLATION_X, new float[]{0.0f}));
                if (this.myMessagesColor) {
                    this.addButton.setVisibility(0);
                    arrayList.add(ObjectAnimator.ofFloat(this.exchangeButton, View.ALPHA, new float[]{0.0f}));
                    arrayList.add(ObjectAnimator.ofFloat(this.exchangeButton, View.SCALE_X, new float[]{0.0f}));
                    arrayList.add(ObjectAnimator.ofFloat(this.exchangeButton, View.SCALE_Y, new float[]{0.0f}));
                    arrayList.add(ObjectAnimator.ofFloat(this.addButton, View.ALPHA, new float[]{1.0f}));
                    arrayList.add(ObjectAnimator.ofFloat(this.addButton, View.SCALE_X, new float[]{1.0f}));
                    arrayList.add(ObjectAnimator.ofFloat(this.addButton, View.SCALE_Y, new float[]{1.0f}));
                }
            } else if (i == 3) {
                this.colorsCount = 2;
                arrayList = new ArrayList();
                arrayList.add(ObjectAnimator.ofFloat(this.addButton, View.TRANSLATION_X, new float[]{(float) (AndroidUtilities.dp(30.0f) + AndroidUtilities.dp(13.0f))}));
            } else if (i == 4) {
                this.colorsCount = 3;
                this.addButton.setVisibility(0);
                arrayList = new ArrayList();
                arrayList.add(ObjectAnimator.ofFloat(this.addButton, View.TRANSLATION_X, new float[]{(float) ((AndroidUtilities.dp(30.0f) * 2) + (AndroidUtilities.dp(13.0f) * 2))}));
                arrayList.add(ObjectAnimator.ofFloat(this.addButton, View.ALPHA, new float[]{1.0f}));
                arrayList.add(ObjectAnimator.ofFloat(this.addButton, View.SCALE_X, new float[]{1.0f}));
                arrayList.add(ObjectAnimator.ofFloat(this.addButton, View.SCALE_Y, new float[]{1.0f}));
            } else {
                return;
            }
            int i2 = this.selectedColor;
            if (i2 != 3) {
                RadioButton radioButton2 = this.radioButton[i2];
                int i3 = i2 + 1;
                while (true) {
                    radioButtonArr = this.radioButton;
                    if (i3 >= radioButtonArr.length) {
                        break;
                    }
                    radioButtonArr[i3 - 1] = radioButtonArr[i3];
                    i3++;
                }
                radioButtonArr[3] = radioButton2;
            }
            this.radioButton[0].callOnClick();
            int i4 = 0;
            while (true) {
                RadioButton[] radioButtonArr2 = this.radioButton;
                if (i4 < radioButtonArr2.length) {
                    if (i4 < this.colorsCount) {
                        this.delegate.setColor(radioButtonArr2[i4].getColor(), i4, i4 == this.radioButton.length - 1);
                    } else {
                        this.delegate.setColor(0, i4, i4 == radioButtonArr2.length - 1);
                    }
                    i4++;
                } else {
                    this.colorsAnimator = new AnimatorSet();
                    updateColorsPosition(arrayList, this.selectedColor, true, getMeasuredWidth());
                    this.colorsAnimator.playTogether(arrayList);
                    this.colorsAnimator.setDuration(180);
                    this.colorsAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT);
                    this.colorsAnimator.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            if (ColorPicker.this.colorsCount == 1) {
                                ColorPicker.this.clearButton.setVisibility(4);
                                if (ColorPicker.this.myMessagesColor) {
                                    ColorPicker.this.exchangeButton.setVisibility(4);
                                }
                            }
                            for (int i = 0; i < ColorPicker.this.radioButton.length; i++) {
                                if (ColorPicker.this.radioButton[i].getTag(NUM) == null) {
                                    ColorPicker.this.radioButton[i].setVisibility(4);
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$6 */
    public /* synthetic */ void lambda$new$6$ColorPicker(int i) {
        boolean z = true;
        if (i == 1 || i == 2) {
            ColorPickerDelegate colorPickerDelegate = this.delegate;
            if (i != 2) {
                z = false;
            }
            colorPickerDelegate.openThemeCreate(z);
        } else if (i == 3) {
            this.delegate.deleteTheme();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$7 */
    public /* synthetic */ void lambda$new$7$ColorPicker(View view) {
        this.menuItem.toggleSubMenu();
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        updateColorsPosition((ArrayList<Animator>) null, 0, false, getMeasuredWidth());
    }

    private void updateColorsPosition(ArrayList<Animator> arrayList, int i, boolean z, int i2) {
        int i3 = this.colorsCount;
        if (this.myMessagesColor && i3 == 2) {
            i3++;
        }
        int left = this.radioContainer.getLeft() + (AndroidUtilities.dp(30.0f) * i3) + ((i3 - 1) * AndroidUtilities.dp(13.0f));
        int dp = i2 - AndroidUtilities.dp(this.currentResetType == 1 ? 50.0f : 0.0f);
        float f = left > dp ? (float) (left - dp) : 0.0f;
        if (arrayList != null) {
            arrayList.add(ObjectAnimator.ofFloat(this.radioContainer, View.TRANSLATION_X, new float[]{-f}));
        } else {
            this.radioContainer.setTranslationX(-f);
        }
        int i4 = 0;
        int i5 = 0;
        while (true) {
            RadioButton[] radioButtonArr = this.radioButton;
            if (i4 < radioButtonArr.length) {
                boolean z2 = radioButtonArr[i4].getTag(NUM) != null;
                if (i4 < this.colorsCount) {
                    if (i4 == 1 && this.myMessagesColor) {
                        this.exchangeButton.setTranslationX((float) i5);
                        i5 += AndroidUtilities.dp(30.0f) + AndroidUtilities.dp(13.0f);
                    }
                    this.radioButton[i4].setVisibility(0);
                    if (arrayList != null) {
                        if (!z2) {
                            arrayList.add(ObjectAnimator.ofFloat(this.radioButton[i4], View.ALPHA, new float[]{1.0f}));
                            arrayList.add(ObjectAnimator.ofFloat(this.radioButton[i4], View.SCALE_X, new float[]{1.0f}));
                            arrayList.add(ObjectAnimator.ofFloat(this.radioButton[i4], View.SCALE_Y, new float[]{1.0f}));
                        }
                        if (z || (!z && i4 != this.colorsCount - 1)) {
                            arrayList.add(ObjectAnimator.ofFloat(this.radioButton[i4], View.TRANSLATION_X, new float[]{(float) i5}));
                        } else {
                            this.radioButton[i4].setTranslationX((float) i5);
                        }
                    } else {
                        this.radioButton[i4].setVisibility(0);
                        this.radioButton[i4].setAlpha(1.0f);
                        this.radioButton[i4].setScaleX(1.0f);
                        this.radioButton[i4].setScaleY(1.0f);
                        this.radioButton[i4].setTranslationX((float) i5);
                    }
                    this.radioButton[i4].setTag(NUM, 1);
                } else {
                    if (arrayList == null) {
                        this.radioButton[i4].setVisibility(4);
                        this.radioButton[i4].setAlpha(0.0f);
                        this.radioButton[i4].setScaleX(0.0f);
                        this.radioButton[i4].setScaleY(0.0f);
                    } else if (z2) {
                        arrayList.add(ObjectAnimator.ofFloat(this.radioButton[i4], View.ALPHA, new float[]{0.0f}));
                        arrayList.add(ObjectAnimator.ofFloat(this.radioButton[i4], View.SCALE_X, new float[]{0.0f}));
                        arrayList.add(ObjectAnimator.ofFloat(this.radioButton[i4], View.SCALE_Y, new float[]{0.0f}));
                    }
                    if (!z) {
                        this.radioButton[i4].setTranslationX((float) i5);
                    }
                    this.radioButton[i4].setTag(NUM, (Object) null);
                }
                i5 += AndroidUtilities.dp(30.0f) + AndroidUtilities.dp(13.0f);
                i4++;
            } else {
                return;
            }
        }
    }

    public void hideKeyboard() {
        AndroidUtilities.hideKeyboard(this.colorEditText[1]);
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:11:0x009f  */
    /* JADX WARNING: Removed duplicated region for block: B:12:0x00a7  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onDraw(android.graphics.Canvas r24) {
        /*
            r23 = this;
            r6 = r23
            r7 = r24
            r0 = 1110704128(0x42340000, float:45.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r0)
            android.graphics.Bitmap r0 = r6.colorWheelBitmap
            float r9 = (float) r8
            r1 = 0
            r2 = 0
            r7.drawBitmap(r0, r1, r9, r2)
            android.graphics.Bitmap r0 = r6.colorWheelBitmap
            int r0 = r0.getHeight()
            int r10 = r8 + r0
            int r0 = r23.getMeasuredWidth()
            float r3 = (float) r0
            int r0 = r8 + 1
            float r4 = (float) r0
            android.graphics.Paint r5 = r6.linePaint
            r0 = r24
            r2 = r9
            r0.drawRect(r1, r2, r3, r4, r5)
            int r0 = r10 + -1
            float r2 = (float) r0
            int r0 = r23.getMeasuredWidth()
            float r3 = (float) r0
            float r4 = (float) r10
            android.graphics.Paint r5 = r6.linePaint
            r0 = r24
            r0.drawRect(r1, r2, r3, r4, r5)
            float[] r0 = r6.hsvTemp
            float[] r1 = r6.colorHSV
            r11 = 0
            r2 = r1[r11]
            r0[r11] = r2
            r12 = 1
            r2 = r1[r12]
            r0[r12] = r2
            r13 = 2
            r14 = 1065353216(0x3var_, float:1.0)
            r0[r13] = r14
            r0 = r1[r11]
            int r1 = r23.getMeasuredWidth()
            float r1 = (float) r1
            float r0 = r0 * r1
            r1 = 1135869952(0x43b40000, float:360.0)
            float r0 = r0 / r1
            int r0 = (int) r0
            android.graphics.Bitmap r1 = r6.colorWheelBitmap
            int r1 = r1.getHeight()
            float r1 = (float) r1
            float[] r2 = r6.colorHSV
            r2 = r2[r12]
            float r2 = r14 - r2
            float r1 = r1 * r2
            float r9 = r9 + r1
            int r1 = (int) r9
            boolean r2 = r6.circlePressed
            if (r2 != 0) goto L_0x00c0
            r2 = 1098907648(0x41800000, float:16.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            org.telegram.ui.Components.CubicBezierInterpolator r3 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_OUT
            float r4 = r6.pressedMoveProgress
            float r3 = r3.getInterpolation(r4)
            if (r0 >= r2) goto L_0x0088
            float r4 = (float) r0
            int r0 = r2 - r0
            float r0 = (float) r0
            float r0 = r0 * r3
            float r4 = r4 + r0
        L_0x0086:
            int r0 = (int) r4
            goto L_0x009b
        L_0x0088:
            int r4 = r23.getMeasuredWidth()
            int r4 = r4 - r2
            if (r0 <= r4) goto L_0x009b
            float r4 = (float) r0
            int r5 = r23.getMeasuredWidth()
            int r5 = r5 - r2
            int r0 = r0 - r5
            float r0 = (float) r0
            float r0 = r0 * r3
            float r4 = r4 - r0
            goto L_0x0086
        L_0x009b:
            int r4 = r8 + r2
            if (r1 >= r4) goto L_0x00a7
            float r2 = (float) r1
            int r4 = r4 - r1
            float r1 = (float) r4
            float r3 = r3 * r1
            float r2 = r2 + r3
            int r1 = (int) r2
            goto L_0x00c0
        L_0x00a7:
            android.graphics.Bitmap r4 = r6.colorWheelBitmap
            int r4 = r4.getHeight()
            int r4 = r4 + r8
            int r4 = r4 - r2
            if (r1 <= r4) goto L_0x00c0
            float r4 = (float) r1
            android.graphics.Bitmap r5 = r6.colorWheelBitmap
            int r5 = r5.getHeight()
            int r8 = r8 + r5
            int r8 = r8 - r2
            int r1 = r1 - r8
            float r1 = (float) r1
            float r3 = r3 * r1
            float r4 = r4 - r3
            int r1 = (int) r4
        L_0x00c0:
            r2 = r0
            r3 = r1
            float[] r0 = r6.hsvTemp
            int r4 = android.graphics.Color.HSVToColor(r0)
            r5 = 0
            r0 = r23
            r1 = r24
            r0.drawPointerArrow(r1, r2, r3, r4, r5)
            android.graphics.RectF r0 = r6.sliderRect
            r1 = 1102053376(0x41b00000, float:22.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r2 = (float) r2
            r3 = 1104150528(0x41d00000, float:26.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r3 = r3 + r10
            float r3 = (float) r3
            int r4 = r23.getMeasuredWidth()
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r4 = r4 - r1
            float r1 = (float) r4
            r4 = 1107820544(0x42080000, float:34.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r10 = r10 + r4
            float r4 = (float) r10
            r0.set(r2, r3, r1, r4)
            android.graphics.LinearGradient r0 = r6.colorGradient
            if (r0 != 0) goto L_0x0137
            float[] r0 = r6.hsvTemp
            float r1 = r6.minHsvBrightness
            r0[r13] = r1
            int r0 = android.graphics.Color.HSVToColor(r0)
            float[] r1 = r6.hsvTemp
            float r2 = r6.maxHsvBrightness
            r1[r13] = r2
            int r1 = android.graphics.Color.HSVToColor(r1)
            android.graphics.LinearGradient r2 = new android.graphics.LinearGradient
            android.graphics.RectF r3 = r6.sliderRect
            float r4 = r3.left
            float r5 = r3.top
            float r3 = r3.right
            int[] r8 = new int[r13]
            r8[r11] = r1
            r8[r12] = r0
            r21 = 0
            android.graphics.Shader$TileMode r22 = android.graphics.Shader.TileMode.CLAMP
            r15 = r2
            r16 = r4
            r17 = r5
            r18 = r3
            r19 = r5
            r20 = r8
            r15.<init>(r16, r17, r18, r19, r20, r21, r22)
            r6.colorGradient = r2
            android.graphics.Paint r0 = r6.valueSliderPaint
            r0.setShader(r2)
        L_0x0137:
            android.graphics.RectF r0 = r6.sliderRect
            r1 = 1082130432(0x40800000, float:4.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r2 = (float) r2
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            android.graphics.Paint r3 = r6.valueSliderPaint
            r7.drawRoundRect(r0, r2, r1, r3)
            float r0 = r6.minHsvBrightness
            float r1 = r6.maxHsvBrightness
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 != 0) goto L_0x0155
            r0 = 1056964608(0x3var_, float:0.5)
            goto L_0x0160
        L_0x0155:
            float r0 = r23.getBrightness()
            float r1 = r6.minHsvBrightness
            float r0 = r0 - r1
            float r2 = r6.maxHsvBrightness
            float r2 = r2 - r1
            float r0 = r0 / r2
        L_0x0160:
            android.graphics.RectF r1 = r6.sliderRect
            float r2 = r1.left
            float r0 = r14 - r0
            float r1 = r1.width()
            float r0 = r0 * r1
            float r2 = r2 + r0
            int r2 = (int) r2
            android.graphics.RectF r0 = r6.sliderRect
            float r0 = r0.centerY()
            int r3 = (int) r0
            int r4 = r23.getColor()
            r5 = 1
            r0 = r23
            r1 = r24
            r0.drawPointerArrow(r1, r2, r3, r4, r5)
            boolean r0 = r6.circlePressed
            if (r0 != 0) goto L_0x01a7
            float r0 = r6.pressedMoveProgress
            int r0 = (r0 > r14 ? 1 : (r0 == r14 ? 0 : -1))
            if (r0 >= 0) goto L_0x01a7
            long r0 = android.os.SystemClock.elapsedRealtime()
            long r2 = r6.lastUpdateTime
            long r2 = r0 - r2
            r6.lastUpdateTime = r0
            float r0 = r6.pressedMoveProgress
            float r1 = (float) r2
            r2 = 1127481344(0x43340000, float:180.0)
            float r1 = r1 / r2
            float r0 = r0 + r1
            r6.pressedMoveProgress = r0
            int r0 = (r0 > r14 ? 1 : (r0 == r14 ? 0 : -1))
            if (r0 <= 0) goto L_0x01a4
            r6.pressedMoveProgress = r14
        L_0x01a4:
            r23.invalidate()
        L_0x01a7:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ColorPicker.onDraw(android.graphics.Canvas):void");
    }

    /* access modifiers changed from: private */
    public int getFieldColor(int i, int i2) {
        try {
            return Integer.parseInt(this.colorEditText[i].getText().toString(), 16) | -16777216;
        } catch (Exception unused) {
            return i2;
        }
    }

    private void drawPointerArrow(Canvas canvas, int i, int i2, int i3, boolean z) {
        int dp = AndroidUtilities.dp(z ? 12.0f : 16.0f);
        this.circleDrawable.setBounds(i - dp, i2 - dp, i + dp, dp + i2);
        this.circleDrawable.draw(canvas);
        this.circlePaint.setColor(-1);
        float f = (float) i;
        float f2 = (float) i2;
        canvas.drawCircle(f, f2, (float) AndroidUtilities.dp(z ? 11.0f : 15.0f), this.circlePaint);
        this.circlePaint.setColor(i3);
        canvas.drawCircle(f, f2, (float) AndroidUtilities.dp(z ? 9.0f : 13.0f), this.circlePaint);
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int i, int i2, int i3, int i4) {
        if (this.colorWheelWidth != i) {
            this.colorWheelWidth = i;
            this.colorWheelBitmap = createColorWheelBitmap(i, AndroidUtilities.dp(180.0f));
            this.colorGradient = null;
        }
    }

    private Bitmap createColorWheelBitmap(int i, int i2) {
        int i3 = i;
        int i4 = i2;
        Bitmap createBitmap = Bitmap.createBitmap(i3, i4, Bitmap.Config.ARGB_8888);
        float f = (float) i3;
        float f2 = (float) i4;
        this.colorWheelPaint.setShader(new ComposeShader(new LinearGradient(0.0f, (float) (i4 / 3), 0.0f, f2, new int[]{-1, 0}, (float[]) null, Shader.TileMode.CLAMP), new LinearGradient(0.0f, 0.0f, f, 0.0f, new int[]{-65536, -256, -16711936, -16711681, -16776961, -65281, -65536}, (float[]) null, Shader.TileMode.CLAMP), PorterDuff.Mode.MULTIPLY));
        new Canvas(createBitmap).drawRect(0.0f, 0.0f, f, f2, this.colorWheelPaint);
        return createBitmap;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:33:0x00fa, code lost:
        if (r12 <= (r11.sliderRect.bottom + ((float) org.telegram.messenger.AndroidUtilities.dp(7.0f)))) goto L_0x00fc;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:3:0x000b, code lost:
        if (r0 != 2) goto L_0x001b;
     */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x0141  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onTouchEvent(android.view.MotionEvent r12) {
        /*
            r11 = this;
            int r0 = r12.getAction()
            r1 = 2
            r2 = 0
            r3 = 1
            if (r0 == 0) goto L_0x0020
            if (r0 == r3) goto L_0x000e
            if (r0 == r1) goto L_0x0020
            goto L_0x001b
        L_0x000e:
            r11.colorPressed = r2
            r11.circlePressed = r2
            long r0 = android.os.SystemClock.elapsedRealtime()
            r11.lastUpdateTime = r0
            r11.invalidate()
        L_0x001b:
            boolean r12 = super.onTouchEvent(r12)
            return r12
        L_0x0020:
            float r0 = r12.getX()
            int r0 = (int) r0
            float r12 = r12.getY()
            int r12 = (int) r12
            r4 = 1110704128(0x42340000, float:45.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            boolean r5 = r11.circlePressed
            r6 = 0
            r7 = 1065353216(0x3var_, float:1.0)
            if (r5 != 0) goto L_0x0046
            boolean r5 = r11.colorPressed
            if (r5 != 0) goto L_0x00c8
            if (r12 < r4) goto L_0x00c8
            android.graphics.Bitmap r5 = r11.colorWheelBitmap
            int r5 = r5.getHeight()
            int r5 = r5 + r4
            if (r12 > r5) goto L_0x00c8
        L_0x0046:
            boolean r5 = r11.circlePressed
            if (r5 != 0) goto L_0x0051
            android.view.ViewParent r5 = r11.getParent()
            r5.requestDisallowInterceptTouchEvent(r3)
        L_0x0051:
            r11.circlePressed = r3
            r11.pressedMoveProgress = r6
            long r8 = android.os.SystemClock.elapsedRealtime()
            r11.lastUpdateTime = r8
            android.graphics.Bitmap r5 = r11.colorWheelBitmap
            int r5 = r5.getWidth()
            int r0 = java.lang.Math.min(r0, r5)
            int r0 = java.lang.Math.max(r2, r0)
            android.graphics.Bitmap r5 = r11.colorWheelBitmap
            int r5 = r5.getHeight()
            int r5 = r5 + r4
            int r12 = java.lang.Math.min(r12, r5)
            int r12 = java.lang.Math.max(r4, r12)
            float r5 = r11.minHsvBrightness
            float r8 = r11.maxHsvBrightness
            int r5 = (r5 > r8 ? 1 : (r5 == r8 ? 0 : -1))
            if (r5 != 0) goto L_0x0083
            r5 = 1056964608(0x3var_, float:0.5)
            goto L_0x008e
        L_0x0083:
            float r5 = r11.getBrightness()
            float r8 = r11.minHsvBrightness
            float r5 = r5 - r8
            float r9 = r11.maxHsvBrightness
            float r9 = r9 - r8
            float r5 = r5 / r9
        L_0x008e:
            float[] r8 = r11.colorHSV
            float r9 = (float) r0
            r10 = 1135869952(0x43b40000, float:360.0)
            float r9 = r9 * r10
            android.graphics.Bitmap r10 = r11.colorWheelBitmap
            int r10 = r10.getWidth()
            float r10 = (float) r10
            float r9 = r9 / r10
            r8[r2] = r9
            float[] r8 = r11.colorHSV
            android.graphics.Bitmap r9 = r11.colorWheelBitmap
            int r9 = r9.getHeight()
            float r9 = (float) r9
            float r9 = r7 / r9
            int r4 = r12 - r4
            float r4 = (float) r4
            float r9 = r9 * r4
            float r4 = r7 - r9
            r8[r3] = r4
            r11.updateHsvMinMaxBrightness()
            float[] r4 = r11.colorHSV
            float r8 = r11.minHsvBrightness
            float r9 = r7 - r5
            float r8 = r8 * r9
            float r9 = r11.maxHsvBrightness
            float r9 = r9 * r5
            float r8 = r8 + r9
            r4[r1] = r8
            r4 = 0
            r11.colorGradient = r4
        L_0x00c8:
            boolean r4 = r11.colorPressed
            if (r4 != 0) goto L_0x00fc
            boolean r4 = r11.circlePressed
            if (r4 != 0) goto L_0x0131
            float r4 = (float) r0
            android.graphics.RectF r5 = r11.sliderRect
            float r8 = r5.left
            int r8 = (r4 > r8 ? 1 : (r4 == r8 ? 0 : -1))
            if (r8 < 0) goto L_0x0131
            float r8 = r5.right
            int r4 = (r4 > r8 ? 1 : (r4 == r8 ? 0 : -1))
            if (r4 > 0) goto L_0x0131
            float r12 = (float) r12
            float r4 = r5.top
            r5 = 1088421888(0x40e00000, float:7.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r8 = (float) r8
            float r4 = r4 - r8
            int r4 = (r12 > r4 ? 1 : (r12 == r4 ? 0 : -1))
            if (r4 < 0) goto L_0x0131
            android.graphics.RectF r4 = r11.sliderRect
            float r4 = r4.bottom
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r5 = (float) r5
            float r4 = r4 + r5
            int r12 = (r12 > r4 ? 1 : (r12 == r4 ? 0 : -1))
            if (r12 > 0) goto L_0x0131
        L_0x00fc:
            float r12 = (float) r0
            android.graphics.RectF r0 = r11.sliderRect
            float r4 = r0.left
            float r12 = r12 - r4
            float r0 = r0.width()
            float r12 = r12 / r0
            float r12 = r7 - r12
            int r0 = (r12 > r6 ? 1 : (r12 == r6 ? 0 : -1))
            if (r0 >= 0) goto L_0x010e
            goto L_0x0116
        L_0x010e:
            int r0 = (r12 > r7 ? 1 : (r12 == r7 ? 0 : -1))
            if (r0 <= 0) goto L_0x0115
            r6 = 1065353216(0x3var_, float:1.0)
            goto L_0x0116
        L_0x0115:
            r6 = r12
        L_0x0116:
            float[] r12 = r11.colorHSV
            float r0 = r11.minHsvBrightness
            float r7 = r7 - r6
            float r0 = r0 * r7
            float r4 = r11.maxHsvBrightness
            float r4 = r4 * r6
            float r0 = r0 + r4
            r12[r1] = r0
            boolean r12 = r11.colorPressed
            if (r12 != 0) goto L_0x012f
            android.view.ViewParent r12 = r11.getParent()
            r12.requestDisallowInterceptTouchEvent(r3)
        L_0x012f:
            r11.colorPressed = r3
        L_0x0131:
            boolean r12 = r11.colorPressed
            if (r12 != 0) goto L_0x0139
            boolean r12 = r11.circlePressed
            if (r12 == 0) goto L_0x0195
        L_0x0139:
            int r12 = r11.getColor()
            boolean r0 = r11.ignoreTextChange
            if (r0 != 0) goto L_0x018b
            int r0 = android.graphics.Color.red(r12)
            int r4 = android.graphics.Color.green(r12)
            int r5 = android.graphics.Color.blue(r12)
            r11.ignoreTextChange = r3
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]
            byte r0 = (byte) r0
            java.lang.Byte r0 = java.lang.Byte.valueOf(r0)
            r6[r2] = r0
            byte r0 = (byte) r4
            java.lang.Byte r0 = java.lang.Byte.valueOf(r0)
            r6[r3] = r0
            byte r0 = (byte) r5
            java.lang.Byte r0 = java.lang.Byte.valueOf(r0)
            r6[r1] = r0
            java.lang.String r0 = "%02x%02x%02x"
            java.lang.String r0 = java.lang.String.format(r0, r6)
            java.lang.String r0 = r0.toUpperCase()
            org.telegram.ui.Components.EditTextBoldCursor[] r1 = r11.colorEditText
            r1 = r1[r3]
            android.text.Editable r1 = r1.getText()
            int r4 = r1.length()
            r1.replace(r2, r4, r0)
            org.telegram.ui.Components.ColorPicker$RadioButton[] r0 = r11.radioButton
            int r1 = r11.selectedColor
            r0 = r0[r1]
            r0.setColor(r12)
            r11.ignoreTextChange = r2
        L_0x018b:
            org.telegram.ui.Components.ColorPicker$ColorPickerDelegate r0 = r11.delegate
            int r1 = r11.selectedColor
            r0.setColor(r12, r1, r2)
            r11.invalidate()
        L_0x0195:
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ColorPicker.onTouchEvent(android.view.MotionEvent):boolean");
    }

    /* access modifiers changed from: private */
    public void setColorInner(int i) {
        Color.colorToHSV(i, this.colorHSV);
        int defaultColor = this.delegate.getDefaultColor(this.selectedColor);
        if (defaultColor == 0 || defaultColor != i) {
            updateHsvMinMaxBrightness();
        }
        this.colorGradient = null;
        invalidate();
    }

    public void setColor(int i, int i2) {
        if (!this.ignoreTextChange) {
            this.ignoreTextChange = true;
            if (this.selectedColor == i2) {
                String upperCase = String.format("%02x%02x%02x", new Object[]{Byte.valueOf((byte) Color.red(i)), Byte.valueOf((byte) Color.green(i)), Byte.valueOf((byte) Color.blue(i))}).toUpperCase();
                this.colorEditText[1].setText(upperCase);
                this.colorEditText[1].setSelection(upperCase.length());
            }
            this.radioButton[i2].setColor(i);
            this.ignoreTextChange = false;
        }
        setColorInner(i);
    }

    public void setHasChanges(final boolean z) {
        if (z && this.resetButton.getTag() != null) {
            return;
        }
        if ((z || this.resetButton.getTag() != null) && this.clearButton.getTag() == null) {
            this.resetButton.setTag(z ? 1 : null);
            AnimatorSet animatorSet = new AnimatorSet();
            ArrayList arrayList = new ArrayList();
            if (z) {
                this.resetButton.setVisibility(0);
            }
            TextView textView = this.resetButton;
            Property property = View.ALPHA;
            float[] fArr = new float[1];
            fArr[0] = z ? 1.0f : 0.0f;
            arrayList.add(ObjectAnimator.ofFloat(textView, property, fArr));
            animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    if (!z) {
                        ColorPicker.this.resetButton.setVisibility(8);
                    }
                }
            });
            animatorSet.playTogether(arrayList);
            animatorSet.setDuration(180);
            animatorSet.start();
        }
    }

    public void setType(int i, boolean z, final boolean z2, int i2, boolean z3, int i3, boolean z4) {
        this.currentResetType = i;
        this.myMessagesColor = z3;
        this.colorsCount = i2;
        float f = 0.0f;
        if (i2 == 1) {
            this.addButton.setTranslationX(0.0f);
        } else if (i2 == 2) {
            this.addButton.setTranslationX((float) (AndroidUtilities.dp(30.0f) + AndroidUtilities.dp(13.0f)));
        } else if (i2 == 3) {
            this.addButton.setTranslationX((float) ((AndroidUtilities.dp(30.0f) * 2) + (AndroidUtilities.dp(13.0f) * 2)));
        } else {
            this.addButton.setTranslationX((float) ((AndroidUtilities.dp(30.0f) * 3) + (AndroidUtilities.dp(13.0f) * 3)));
        }
        ActionBarMenuItem actionBarMenuItem = this.menuItem;
        if (actionBarMenuItem != null) {
            if (i == 1) {
                actionBarMenuItem.setVisibility(0);
                this.clearButton.setTranslationX((float) (-AndroidUtilities.dp(40.0f)));
            } else {
                actionBarMenuItem.setVisibility(8);
                this.clearButton.setTranslationX(0.0f);
            }
        }
        int i4 = 4;
        if (!z2) {
            this.addButton.setVisibility(8);
            this.clearButton.setVisibility(8);
        } else {
            if (i2 < (z3 ? 2 : 4)) {
                this.addButton.setVisibility(0);
                this.addButton.setScaleX(1.0f);
                this.addButton.setScaleY(1.0f);
                this.addButton.setAlpha(1.0f);
            } else {
                this.addButton.setVisibility(8);
            }
            if (i2 > 1) {
                this.clearButton.setVisibility(0);
                this.clearButton.setScaleX(1.0f);
                this.clearButton.setScaleY(1.0f);
                this.clearButton.setAlpha(1.0f);
            } else {
                this.clearButton.setVisibility(8);
            }
        }
        if (z3) {
            ImageView imageView = this.exchangeButton;
            if (i2 == 2) {
                i4 = 0;
            }
            imageView.setVisibility(i4);
            this.exchangeButton.setAlpha(i2 == 2 ? 1.0f : 0.0f);
            this.exchangeButton.setScaleX(i2 == 2 ? 1.0f : 0.0f);
            ImageView imageView2 = this.exchangeButton;
            if (i2 == 2) {
                f = 1.0f;
            }
            imageView2.setScaleY(f);
        } else {
            this.exchangeButton.setVisibility(8);
        }
        this.linearLayout.invalidate();
        ArrayList arrayList = null;
        updateColorsPosition((ArrayList<Animator>) null, 0, false, getMeasuredWidth());
        if (z4) {
            arrayList = new ArrayList();
        }
        if (arrayList != null && !arrayList.isEmpty()) {
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(arrayList);
            animatorSet.setDuration(180);
            animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    if (!z2) {
                        ColorPicker.this.clearButton.setVisibility(8);
                        ColorPicker.this.exchangeButton.setVisibility(8);
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
            float f = imageView.getTag() != null ? 0.0f : this.minBrightness;
            float f2 = this.clearButton.getTag() != null ? 1.0f : this.maxBrightness;
            float[] fArr = this.colorHSV;
            float f3 = fArr[2];
            if (f == 0.0f && f2 == 1.0f) {
                this.minHsvBrightness = 0.0f;
                this.maxHsvBrightness = 1.0f;
                return;
            }
            fArr[2] = 1.0f;
            int HSVToColor = Color.HSVToColor(fArr);
            this.colorHSV[2] = f3;
            float computePerceivedBrightness = AndroidUtilities.computePerceivedBrightness(HSVToColor);
            float max = Math.max(0.0f, Math.min(f / computePerceivedBrightness, 1.0f));
            this.minHsvBrightness = max;
            this.maxHsvBrightness = Math.max(max, Math.min(f2 / computePerceivedBrightness, 1.0f));
        }
    }

    public void setMinBrightness(float f) {
        this.minBrightness = f;
        updateHsvMinMaxBrightness();
    }

    public void setMaxBrightness(float f) {
        this.maxBrightness = f;
        updateHsvMinMaxBrightness();
    }

    public void provideThemeDescriptions(List<ThemeDescription> list) {
        List<ThemeDescription> list2 = list;
        for (int i = 0; i < this.colorEditText.length; i++) {
            list2.add(new ThemeDescription(this.colorEditText[i], ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
            list2.add(new ThemeDescription(this.colorEditText[i], ThemeDescription.FLAG_CURSORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
            list2.add(new ThemeDescription(this.colorEditText[i], ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteHintText"));
            list2.add(new ThemeDescription(this.colorEditText[i], ThemeDescription.FLAG_HINTTEXTCOLOR | ThemeDescription.FLAG_PROGRESSBAR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"));
            list2.add(new ThemeDescription(this.colorEditText[i], ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputField"));
            list2.add(new ThemeDescription(this.colorEditText[i], ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputFieldActivated"));
        }
        list2.add(new ThemeDescription(this.clearButton, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        list2.add(new ThemeDescription(this.clearButton, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogButtonSelector"));
        list2.add(new ThemeDescription(this.exchangeButton, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        list2.add(new ThemeDescription(this.exchangeButton, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogButtonSelector"));
        if (this.menuItem != null) {
            $$Lambda$ColorPicker$PtUvOjwsWsQZ593TTF1yZMwiw r9 = new ThemeDescription.ThemeDescriptionDelegate() {
                public final void didSetColor() {
                    ColorPicker.this.lambda$provideThemeDescriptions$8$ColorPicker();
                }
            };
            list2.add(new ThemeDescription(this.menuItem, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r9, "windowBackgroundWhiteBlackText"));
            list2.add(new ThemeDescription(this.menuItem, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r9, "dialogButtonSelector"));
            list2.add(new ThemeDescription(this.menuItem, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r9, "actionBarDefaultSubmenuItem"));
            list2.add(new ThemeDescription(this.menuItem, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r9, "actionBarDefaultSubmenuItemIcon"));
            list2.add(new ThemeDescription(this.menuItem, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r9, "actionBarDefaultSubmenuBackground"));
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$provideThemeDescriptions$8 */
    public /* synthetic */ void lambda$provideThemeDescriptions$8$ColorPicker() {
        this.menuItem.setIconColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        Theme.setDrawableColor(this.menuItem.getBackground(), Theme.getColor("dialogButtonSelector"));
        this.menuItem.setPopupItemsColor(Theme.getColor("actionBarDefaultSubmenuItem"), false);
        this.menuItem.setPopupItemsColor(Theme.getColor("actionBarDefaultSubmenuItemIcon"), true);
        this.menuItem.redrawPopup(Theme.getColor("actionBarDefaultSubmenuBackground"));
    }

    public static int generateGradientColors(int i) {
        float[] fArr = new float[3];
        Color.colorToHSV(i, fArr);
        if (fArr[1] > 0.5f) {
            fArr[1] = fArr[1] - 0.15f;
        } else {
            fArr[1] = fArr[1] + 0.15f;
        }
        if (fArr[0] > 180.0f) {
            fArr[0] = fArr[0] - 20.0f;
        } else {
            fArr[0] = fArr[0] + 20.0f;
        }
        return Color.HSVToColor(255, fArr);
    }
}
