package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.TextWatcher;
import android.util.Property;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;

public class ColorPicker extends FrameLayout {
    private Drawable circleDrawable;
    private Paint circlePaint;
    private boolean circlePressed;
    private ImageView clearButton;
    private EditTextBoldCursor[] colorEditText;
    private LinearGradient colorGradient;
    private float[] colorHSV;
    private int[] colorPairs;
    private boolean colorPressed;
    private Bitmap colorWheelBitmap;
    private Paint colorWheelPaint;
    private int colorWheelWidth;
    private int currentResetType;
    private final ColorPickerDelegate delegate;
    private Paint editTextCirclePaint;
    private ImageView exchangeButton;
    private float[] hsvTemp;
    boolean ignoreTextChange;
    private long lastUpdateTime;
    private Paint linePaint;
    private BrightnessLimit maxBrightness;
    private BrightnessLimit minBrightness;
    private float pressedMoveProgress;
    private TextView resetButton;
    private int selectedEditText;
    private RectF sliderRect = new RectF();
    private Paint valueSliderPaint;

    public interface BrightnessLimit {
        float getLimit(int i, int i2, int i3);
    }

    public interface ColorPickerDelegate {
        void setColor(int i, int i2, boolean z);
    }

    public ColorPicker(Context context, ColorPickerDelegate colorPickerDelegate) {
        Context context2 = context;
        super(context);
        int i = 4;
        this.colorEditText = new EditTextBoldCursor[4];
        this.colorHSV = new float[]{0.0f, 0.0f, 1.0f};
        this.hsvTemp = new float[3];
        this.pressedMoveProgress = 1.0f;
        this.colorPairs = new int[]{-4340793, -13877680, -1139545, -8735, -14576720, -9579027, -4642009, -15374912, -13157564, -12417292, -65383, -11980224, -7459358, -11927328, -14729161, -6688056, -437306, -4645517, -3992526, -14415050, -973039, -676071, -10118759, -755425, -2277804, -9706107, -8167229, -13713519, -11253123, -11182, -16736257, -1298613, -10137949, -1396792, -5701768, -8847402, -1236678, -7132898, -145360, -822475, -16730917, -16743504, -4166, -1, -16754009, -540, -2472877, -7790229, -10263709, -6116520, -5418103, -12840877, -5717761, -12637290, -13421773, -2287592, -11643704, -7367429, -4436324, -522407, -12693167, -2176092, -15623794, -13045891, -15692345, -1077704, -238467, -9796869, -244117, -12624133, -3585205, -11857073, -14481587, -3386573, -1067, -5109204, -16732005, -6895299, -2896669, -1448720, -12829889, -10462148, -3488463, -788057, -8388480, -16181, -16715168, -16419354, -243174, -542925, -9114667, -5458203, -9609177, -2896968, -1970493, -1028013, -14499389, -148691, -26266, -41374, -8453889, -2031361, -3549441, -1907998, -13014276, -14071553, -2512953, -804, -10212493, -3784082, -14904622, -852738, -16777216, -15754481, -13184548, -10778907, -3459733, -4374734, -14140282, -12213689, -1098954, -1, -4179669, -7453523, -15361705, -15378535, -16777146, -14895648, -16746095, -8847402, -11088654, -13664019, -878262, -865972, -1353897, -16777216, -1815258, -957143, -11877734, -4325389, -5046534, -15805705, -13571905, -32203, -2724491, -1927801, -14680020, -3427116, -3983516, -14866831, -551138, -11776, -13309794, -15780797, -10383128, -5783576, -12279667, -16173513, -14679774, -9502720, -16419354, -16639111, -12228388, -5215565, -12335444, -15133100, -16175064, -14452137, -12335444, -458834, -20547, -15456, -986384, -16774080, -1520704, -10260572, -2301093, -12208567, -4144982, -14880769, -2365706, -3835539, -13331226, -1282387, -9981329, -11754803, -815025, -12893327, -1177223, -38400, -12506790, -13695165, -736013, -235526, -16727041, -228, -33185, -84869, -1024, -1, -65332, -13421671, -2203295, -14264341, -1101095, -7733251, -12951162, -7789250, -11612732, -11181456, -6160434, -327727, -4287547, -8665396, -4340793, -13877680, -10085, -15117189, -8355712, -12603992, -202053, -477952, -503758, -1624025, -549632, -10161268, -11097297, -5709725, -16776152, -16757102, -12441766, -9221267, -15458768, -14402731, -13877680, -166804, -13877680, -11755089, -1481661, -7319915, -16037781, -695785, -12944427, -12951437, -16723201, -7172693, -14575885, -769226, -41107, -15503, -46305, -28568, -15286275, -3461018, -1127005, -1088865, -14859440, -6014671, -5767049, -10027264, -524544, -2410844, -46305, -14688769, -4566160, -728360, -11755089, -3874587, -16777216, -12369085, -11830879, -14139823, -8172133, -3125546, -16737801, -977134, -14057287, -13877680, -10862793, -13863100, -11689808, -2908872, -11135824, -2369956, -13667530, -5621192, -14795662, -14003560, -15645865, -879470, -166804, -28568, -1389149, -2707845, -9817965, -6273793, -12222518, -11103800, -5093998, -958599, -4185051, -996555, -12568002, -4302743, -4004452, -10177548, -18594, -1208573, -7467520, -14738408, -8996782, -7486865, -10011977, -11457112, -16725505, -7143779, -758971, -1136567, -16753513, -13224043, -1754827, -1876645, -261889, -16720930, -13877680, -13330213, -3355470, -9079527, -13614254, -2633012, -1139545, -8735, -4551934, -15198184, -11382190, -12750156, -16756743, -1716, -9793261, -15461097, -920907, -15511464, -3043012, -11884, -8698985, -2350032, -7430485, -1117453, -15504758, -14256015, -16728177, -16771824, -65404, -13434853, -10206811, -14022587, -19641, -13261, -12333406, -15181155, -24193, -16768450, -13238221, -16021613, -7041383, -13757385, -14806260, -6650760, -2915566, -5754247, -9189207, -13157564, -5522773, -1, -132025, -14352831, -8149804, -4785153, -12036765, -14077380, -11353548, -16378112, -95232, -510464, -16726273, -16747777, -9379339, -11884, -11181456, -38037, -6467397, -9549654, -8912378, -16379551, -4980821, -15532041, -5570647, -15597635, -16777216, -1618884, -998789, -11857336, -45488, -404445, -5385984, -8678902, -273431, -4507779, -10457976, -12628885, -3539009, -20547, -10186381, -2370140, -4615210, -7559461, -7929856, -15136251, -16723201, -12944427, -2910821, -4200774, -2436455, -5186887, -888676, -27534, -1647910, -14204858, -10665641, -5715270, -2238733, -349016, -10395295, -6568509, -11482685, -6889766, -14590208, -1776423, -4057856, -15104, -1052741, -2829347, -4370, -2232389, -10066432, -6710938, -2203038, -18292, -1453214, -13421773, -2804887, -3429011, -5818569, -8771544, -501850, -42920, -11834185, -15194040, -248500, -16072772, -12497653, -9274857, -1820139, -1682875, -4175800, -12058552, -10539902, -11951971, -1282202, -810621, -9145153, -13333817, -1250842, -1, -2432264, -2710364, -1228188, -4676, -2350044, -11905379, -14367012, -11449699, -14143416, -8023144, -12747094, -7046, -14886702, -7082553, -14473946, -12500155, -9076838, -2630168, -10738291, -12351070, -15511970, -9325952, -13909788, -1381178, -16232328, -8005426, -12093722, -7449367, -10402939, -11443307, -14738388, -7172693, -15326678, -12951437, -32760, -14281, -14837908, -7079495, -1363127, -762813, -2269559, -541801, -11749180, -12790867, -14865564, -471590, -44753, -1009639, -15062656, -14233394, -5634197, -10419105, -44753, -2284426, -1009639, -1188259, -12567734, -1578565, -1745529, -10501148, -16762509, -1710658, -12802724, -4871095, -13332656, -11094829, -2481409, -6867986, -1186444, -1968700, -2944985, -1427379, -15294331, -733121, -10471405, -5070956, -1757913, -5041641, -37249, -4199937, -13547691, -14245670, -13936522, -11648138, -1680384, -404445, -14576720, -9579027, -3396770, -9094520, -1310580, -235673, -15431476, -13946190, -16759169, -5911422, -16292475, -1, -4468027, -11310730, -6842640, -276524, -4745071, -7048853, -11310730, -14078391, -5458203, -7930392, -8192, -8806644, -16760470, -1776154, -7591, -22703, -8806644, -5457032, -13415088, -3421531, -526088, -5457032, -8192, -8806644, -16760470, -1776154};
        this.delegate = colorPickerDelegate;
        setWillNotDraw(false);
        this.circleDrawable = context.getResources().getDrawable(NUM).mutate();
        this.circlePaint = new Paint(1);
        this.colorWheelPaint = new Paint(5);
        this.valueSliderPaint = new Paint(5);
        this.editTextCirclePaint = new Paint(1);
        this.linePaint = new Paint();
        this.linePaint.setColor(NUM);
        LinearLayout linearLayout = new LinearLayout(context2);
        linearLayout.setOrientation(0);
        addView(linearLayout, LayoutHelper.createFrame(-1, 54.0f, 51, 22.0f, 0.0f, 22.0f, 0.0f));
        int i2 = 0;
        while (true) {
            String str = "windowBackgroundWhiteBlackText";
            if (i2 < i) {
                if (i2 == 2) {
                    this.exchangeButton = new ImageView(getContext());
                    this.exchangeButton.setImageResource(NUM);
                    this.exchangeButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(str), Mode.MULTIPLY));
                    this.exchangeButton.setScaleType(ScaleType.CENTER);
                    this.exchangeButton.setVisibility(8);
                    this.exchangeButton.setOnClickListener(new -$$Lambda$ColorPicker$nOG8yQhzeWSldL8LAMcZjsJIcGg(this));
                    linearLayout.addView(this.exchangeButton, LayoutHelper.createLinear(36, 36, 16, 0, 3, 0, 9));
                }
                if (i2 == 0 || i2 == 2) {
                    this.colorEditText[i2] = new EditTextBoldCursor(context2) {
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

                        /* Access modifiers changed, original: protected */
                        public void onDraw(Canvas canvas) {
                            super.onDraw(canvas);
                            ColorPicker.this.editTextCirclePaint.setColor(ColorPicker.this.getFieldColor(i2 + 1, -1));
                            canvas.drawCircle((float) AndroidUtilities.dp(10.0f), (float) AndroidUtilities.dp(21.0f), (float) AndroidUtilities.dp(10.0f), ColorPicker.this.editTextCirclePaint);
                        }
                    };
                    this.colorEditText[i2].setBackgroundDrawable(null);
                    this.colorEditText[i2].setPadding(AndroidUtilities.dp(28.0f), AndroidUtilities.dp(5.0f), 0, AndroidUtilities.dp(18.0f));
                    this.colorEditText[i2].setText("#");
                    this.colorEditText[i2].setEnabled(false);
                    this.colorEditText[i2].setFocusable(false);
                    linearLayout.addView(this.colorEditText[i2], LayoutHelper.createLinear(-2, -1, i2 == 2 ? 9.0f : 0.0f, 0.0f, 0.0f, 0.0f));
                } else {
                    this.colorEditText[i2] = new EditTextBoldCursor(context2) {
                        public boolean onTouchEvent(MotionEvent motionEvent) {
                            if (getAlpha() != 1.0f) {
                                return false;
                            }
                            if (isFocused()) {
                                AndroidUtilities.showKeyboard(this);
                                return super.onTouchEvent(motionEvent);
                            }
                            requestFocus();
                            return false;
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
                    this.colorEditText[i2].setBackgroundDrawable(null);
                    this.colorEditText[i2].setFilters(new InputFilter[]{new LengthFilter(6)});
                    this.colorEditText[i2].setPadding(0, AndroidUtilities.dp(5.0f), 0, AndroidUtilities.dp(18.0f));
                    this.colorEditText[i2].setHint("8BC6ED");
                    linearLayout.addView(this.colorEditText[i2], LayoutHelper.createLinear(71, -1));
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
                                if (editable.length() != 6) {
                                    ColorPicker.this.ignoreTextChange = false;
                                    return;
                                }
                                ColorPicker colorPicker2 = ColorPicker.this;
                                colorPicker2.setColorInner(colorPicker2.getFieldColor(i2, -1));
                                editable.replace(0, editable.length(), String.format("%02x%02x%02x", new Object[]{Byte.valueOf((byte) Color.red(i)), Byte.valueOf((byte) Color.green(i)), Byte.valueOf((byte) Color.blue(ColorPicker.this.getColor()))}).toUpperCase());
                                ColorPicker.this.colorEditText[i2].setSelection(editable.length());
                                ColorPicker.this.delegate.setColor(ColorPicker.this.getColor(), i2 == 1 ? 0 : 1, true);
                                ColorPicker.this.ignoreTextChange = false;
                            }
                        }
                    });
                    this.colorEditText[i2].setOnFocusChangeListener(new -$$Lambda$ColorPicker$s-B1RYLCLoS8fX3x_UBR606wuns(this, i2));
                    this.colorEditText[i2].setOnEditorActionListener(-$$Lambda$ColorPicker$9wTuphTbaWrYvyXIka7C4gyOEfg.INSTANCE);
                }
                this.colorEditText[i2].setTextSize(1, 18.0f);
                this.colorEditText[i2].setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
                this.colorEditText[i2].setTextColor(Theme.getColor(str));
                this.colorEditText[i2].setCursorColor(Theme.getColor(str));
                this.colorEditText[i2].setCursorSize(AndroidUtilities.dp(20.0f));
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
                i = 4;
            } else {
                this.clearButton = new ImageView(getContext());
                this.clearButton.setImageDrawable(new CloseProgressDrawable2());
                this.clearButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(str), Mode.MULTIPLY));
                this.clearButton.setScaleType(ScaleType.CENTER);
                this.clearButton.setVisibility(8);
                this.clearButton.setOnClickListener(new -$$Lambda$ColorPicker$3qnlmHCco11RT0NQHyIBY_wKf_U(this));
                this.clearButton.setContentDescription(LocaleController.getString("ClearButton", NUM));
                addView(this.clearButton, LayoutHelper.createFrame(36, 36.0f, 53, 0.0f, 3.0f, 14.0f, 0.0f));
                this.resetButton = new TextView(context2);
                this.resetButton.setTextSize(1, 15.0f);
                this.resetButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                this.resetButton.setGravity(17);
                this.resetButton.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
                this.resetButton.setTextColor(Theme.getColor(str));
                addView(this.resetButton, LayoutHelper.createFrame(-2, 36.0f, 53, 0.0f, 3.0f, 14.0f, 0.0f));
                this.resetButton.setOnClickListener(new -$$Lambda$ColorPicker$bp18tJLETqQInYZmyn7bmDD68ps(this));
                return;
            }
        }
    }

    public /* synthetic */ void lambda$new$0$ColorPicker(View view) {
        if (getAlpha() == 1.0f) {
            String obj = this.colorEditText[1].getText().toString();
            String obj2 = this.colorEditText[3].getText().toString();
            this.colorEditText[1].setText(obj2);
            this.colorEditText[1].setSelection(obj2.length());
            this.colorEditText[3].setText(obj);
            this.colorEditText[3].setSelection(obj.length());
        }
    }

    public /* synthetic */ void lambda$new$1$ColorPicker(int i, View view, boolean z) {
        if (this.colorEditText[3] != null) {
            int i2 = 1;
            if (i == 1) {
                i2 = 0;
            }
            this.selectedEditText = i2;
            setColorInner(getFieldColor(i, -1));
        }
    }

    static /* synthetic */ boolean lambda$new$2(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 6) {
            return false;
        }
        AndroidUtilities.hideKeyboard(textView);
        return true;
    }

    public /* synthetic */ void lambda$new$3$ColorPicker(View view) {
        int generateGradientColors;
        int i = 0;
        Object obj = this.clearButton.getTag() != null ? 1 : null;
        toggleSecondField();
        int fieldColor = getFieldColor(3, -16777216);
        if (obj == null && fieldColor == -16777216) {
            generateGradientColors = generateGradientColors(getFieldColor(1, 0));
            String toUpperCase = String.format("%02x%02x%02x", new Object[]{Byte.valueOf((byte) Color.red(generateGradientColors)), Byte.valueOf((byte) Color.green(generateGradientColors)), Byte.valueOf((byte) Color.blue(generateGradientColors))}).toUpperCase();
            this.colorEditText[3].setText(toUpperCase);
            this.colorEditText[3].setSelection(toUpperCase.length());
        } else {
            generateGradientColors = fieldColor;
        }
        ColorPickerDelegate colorPickerDelegate = this.delegate;
        if (obj == null) {
            i = generateGradientColors;
        }
        colorPickerDelegate.setColor(i, 1, true);
        if (obj == null) {
            this.colorEditText[3].requestFocus();
        } else if (this.colorEditText[3].isFocused()) {
            this.colorEditText[1].requestFocus();
        }
    }

    public /* synthetic */ void lambda$new$4$ColorPicker(View view) {
        if (this.resetButton.getAlpha() == 1.0f) {
            this.delegate.setColor(0, -1, true);
            this.resetButton.animate().alpha(0.0f).setDuration(180).start();
            this.resetButton.setTag(null);
        }
    }

    private void toggleSecondField() {
        final boolean z = this.clearButton.getTag() != null;
        this.clearButton.setTag(z ? null : Integer.valueOf(1));
        AnimatorSet animatorSet = new AnimatorSet();
        ArrayList arrayList = new ArrayList();
        ImageView imageView = this.clearButton;
        Property property = View.ROTATION;
        float[] fArr = new float[1];
        float f = 0.0f;
        fArr[0] = z ? 45.0f : 0.0f;
        arrayList.add(ObjectAnimator.ofFloat(imageView, property, fArr));
        Object obj = this.colorEditText[2];
        Property property2 = View.ALPHA;
        float[] fArr2 = new float[1];
        fArr2[0] = z ? 0.0f : 1.0f;
        arrayList.add(ObjectAnimator.ofFloat(obj, property2, fArr2));
        obj = this.colorEditText[3];
        property2 = View.ALPHA;
        fArr2 = new float[1];
        fArr2[0] = z ? 0.0f : 1.0f;
        arrayList.add(ObjectAnimator.ofFloat(obj, property2, fArr2));
        imageView = this.exchangeButton;
        property2 = View.ALPHA;
        fArr2 = new float[1];
        fArr2[0] = z ? 0.0f : 1.0f;
        arrayList.add(ObjectAnimator.ofFloat(imageView, property2, fArr2));
        if (this.currentResetType == 2) {
            if (z) {
                this.resetButton.setVisibility(0);
            }
            TextView textView = this.resetButton;
            property = View.ALPHA;
            float[] fArr3 = new float[1];
            if (z) {
                f = 1.0f;
            }
            fArr3[0] = f;
            arrayList.add(ObjectAnimator.ofFloat(textView, property, fArr3));
        }
        animatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                if (ColorPicker.this.currentResetType == 2 && !z) {
                    ColorPicker.this.resetButton.setVisibility(8);
                }
            }
        });
        animatorSet.playTogether(arrayList);
        animatorSet.setDuration(180);
        animatorSet.start();
    }

    /* Access modifiers changed, original: protected */
    /* JADX WARNING: Removed duplicated region for block: B:13:0x00c0  */
    /* JADX WARNING: Removed duplicated region for block: B:11:0x00b3  */
    public void onDraw(android.graphics.Canvas r23) {
        /*
        r22 = this;
        r6 = r22;
        r7 = r23;
        r0 = r6.colorWheelBitmap;
        r8 = NUM; // 0x42580000 float:54.0 double:5.499263994E-315;
        r1 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r1 = (float) r1;
        r2 = 0;
        r3 = 0;
        r7.drawBitmap(r0, r2, r1, r3);
        r0 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r1 = r6.colorWheelBitmap;
        r1 = r1.getHeight();
        r9 = r0 + r1;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r2 = (float) r0;
        r0 = r22.getMeasuredWidth();
        r3 = (float) r0;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r10 = 1;
        r0 = r0 + r10;
        r4 = (float) r0;
        r5 = r6.linePaint;
        r1 = 0;
        r0 = r23;
        r0.drawRect(r1, r2, r3, r4, r5);
        r0 = r9 + -1;
        r2 = (float) r0;
        r0 = r22.getMeasuredWidth();
        r3 = (float) r0;
        r4 = (float) r9;
        r5 = r6.linePaint;
        r0 = r23;
        r0.drawRect(r1, r2, r3, r4, r5);
        r0 = r6.hsvTemp;
        r1 = r6.colorHSV;
        r11 = 0;
        r2 = r1[r11];
        r0[r11] = r2;
        r2 = r1[r10];
        r0[r10] = r2;
        r12 = 2;
        r13 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r0[r12] = r13;
        r0 = r1[r11];
        r1 = r22.getMeasuredWidth();
        r1 = (float) r1;
        r0 = r0 * r1;
        r1 = NUM; // 0x43b40000 float:360.0 double:5.611943214E-315;
        r0 = r0 / r1;
        r0 = (int) r0;
        r1 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r1 = (float) r1;
        r2 = r6.colorWheelBitmap;
        r2 = r2.getHeight();
        r2 = (float) r2;
        r3 = r6.colorHSV;
        r3 = r3[r10];
        r3 = r13 - r3;
        r2 = r2 * r3;
        r1 = r1 + r2;
        r1 = (int) r1;
        r2 = r6.circlePressed;
        if (r2 != 0) goto L_0x00e1;
    L_0x0080:
        r2 = NUM; // 0x41800000 float:16.0 double:5.42932517E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r3 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_OUT;
        r4 = r6.pressedMoveProgress;
        r3 = r3.getInterpolation(r4);
        if (r0 >= r2) goto L_0x0099;
    L_0x0090:
        r4 = (float) r0;
        r0 = r2 - r0;
        r0 = (float) r0;
        r0 = r0 * r3;
        r4 = r4 + r0;
    L_0x0097:
        r0 = (int) r4;
        goto L_0x00ac;
    L_0x0099:
        r4 = r22.getMeasuredWidth();
        r4 = r4 - r2;
        if (r0 <= r4) goto L_0x00ac;
    L_0x00a0:
        r4 = (float) r0;
        r5 = r22.getMeasuredWidth();
        r5 = r5 - r2;
        r0 = r0 - r5;
        r0 = (float) r0;
        r0 = r0 * r3;
        r4 = r4 - r0;
        goto L_0x0097;
    L_0x00ac:
        r4 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r4 = r4 + r2;
        if (r1 >= r4) goto L_0x00c0;
    L_0x00b3:
        r4 = (float) r1;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r5 = r5 + r2;
        r5 = r5 - r1;
        r1 = (float) r5;
        r3 = r3 * r1;
        r4 = r4 + r3;
    L_0x00be:
        r1 = (int) r4;
        goto L_0x00e1;
    L_0x00c0:
        r4 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r5 = r6.colorWheelBitmap;
        r5 = r5.getHeight();
        r4 = r4 + r5;
        r4 = r4 - r2;
        if (r1 <= r4) goto L_0x00e1;
    L_0x00ce:
        r4 = (float) r1;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r8 = r6.colorWheelBitmap;
        r8 = r8.getHeight();
        r5 = r5 + r8;
        r5 = r5 - r2;
        r1 = r1 - r5;
        r1 = (float) r1;
        r3 = r3 * r1;
        r4 = r4 - r3;
        goto L_0x00be;
    L_0x00e1:
        r2 = r0;
        r3 = r1;
        r0 = r6.hsvTemp;
        r4 = android.graphics.Color.HSVToColor(r0);
        r5 = 0;
        r0 = r22;
        r1 = r23;
        r0.drawPointerArrow(r1, r2, r3, r4, r5);
        r0 = r6.sliderRect;
        r1 = NUM; // 0x41b00000 float:22.0 double:5.44486713E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r2 = (float) r2;
        r3 = NUM; // 0x41d00000 float:26.0 double:5.455228437E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r3 = r3 + r9;
        r3 = (float) r3;
        r4 = r22.getMeasuredWidth();
        r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r4 = r4 - r1;
        r1 = (float) r4;
        r4 = NUM; // 0x42080000 float:34.0 double:5.473360725E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r9 = r9 + r4;
        r4 = (float) r9;
        r0.set(r2, r3, r1, r4);
        r0 = r6.colorGradient;
        if (r0 != 0) goto L_0x014c;
    L_0x011b:
        r0 = new android.graphics.LinearGradient;
        r1 = r6.sliderRect;
        r15 = r1.left;
        r2 = r1.top;
        r1 = r1.right;
        r3 = new int[r12];
        r4 = r6.hsvTemp;
        r4 = android.graphics.Color.HSVToColor(r4);
        r3[r11] = r4;
        r4 = -16777216; // 0xfffffffffvar_ float:-1.7014118E38 double:NaN;
        r3[r10] = r4;
        r20 = 0;
        r21 = android.graphics.Shader.TileMode.CLAMP;
        r14 = r0;
        r16 = r2;
        r17 = r1;
        r18 = r2;
        r19 = r3;
        r14.<init>(r15, r16, r17, r18, r19, r20, r21);
        r6.colorGradient = r0;
        r0 = r6.valueSliderPaint;
        r1 = r6.colorGradient;
        r0.setShader(r1);
    L_0x014c:
        r0 = r6.sliderRect;
        r1 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r2 = (float) r2;
        r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r1 = (float) r1;
        r3 = r6.valueSliderPaint;
        r7.drawRoundRect(r0, r2, r1, r3);
        r0 = r6.sliderRect;
        r0 = r0.left;
        r1 = r22.getBrightness();
        r1 = r13 - r1;
        r2 = r6.sliderRect;
        r2 = r2.width();
        r1 = r1 * r2;
        r0 = r0 + r1;
        r2 = (int) r0;
        r0 = r6.sliderRect;
        r0 = r0.centerY();
        r3 = (int) r0;
        r4 = r22.getColor();
        r5 = 1;
        r0 = r22;
        r1 = r23;
        r0.drawPointerArrow(r1, r2, r3, r4, r5);
        r0 = r6.circlePressed;
        if (r0 != 0) goto L_0x01ae;
    L_0x018a:
        r0 = r6.pressedMoveProgress;
        r0 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1));
        if (r0 >= 0) goto L_0x01ae;
    L_0x0190:
        r0 = android.os.SystemClock.uptimeMillis();
        r2 = r6.lastUpdateTime;
        r2 = r0 - r2;
        r6.lastUpdateTime = r0;
        r0 = r6.pressedMoveProgress;
        r1 = (float) r2;
        r2 = NUM; // 0x43340000 float:180.0 double:5.570497984E-315;
        r1 = r1 / r2;
        r0 = r0 + r1;
        r6.pressedMoveProgress = r0;
        r0 = r6.pressedMoveProgress;
        r0 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1));
        if (r0 <= 0) goto L_0x01ab;
    L_0x01a9:
        r6.pressedMoveProgress = r13;
    L_0x01ab:
        r22.invalidate();
    L_0x01ae:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ColorPicker.onDraw(android.graphics.Canvas):void");
    }

    private int getFieldColor(int i, int i2) {
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

    /* Access modifiers changed, original: protected */
    public void onSizeChanged(int i, int i2, int i3, int i4) {
        if (this.colorWheelWidth != i) {
            this.colorWheelWidth = i;
            this.colorWheelBitmap = createColorWheelBitmap(this.colorWheelWidth, AndroidUtilities.dp(180.0f));
            this.colorGradient = null;
        }
    }

    private Bitmap createColorWheelBitmap(int i, int i2) {
        int i3 = i;
        int i4 = i2;
        Bitmap createBitmap = Bitmap.createBitmap(i3, i4, Config.ARGB_8888);
        float f = (float) i3;
        float f2 = (float) (i4 / 3);
        float f3 = (float) i4;
        this.colorWheelPaint.setShader(new ComposeShader(new LinearGradient(0.0f, f2, 0.0f, f3, new int[]{-1, 0}, null, TileMode.CLAMP), new LinearGradient(0.0f, 0.0f, f, 0.0f, new int[]{-65536, -256, -16711936, -16711681, -16776961, -65281, -65536}, null, TileMode.CLAMP), Mode.MULTIPLY));
        new Canvas(createBitmap).drawRect(0.0f, 0.0f, f, f3, this.colorWheelPaint);
        return createBitmap;
    }

    /* JADX WARNING: Removed duplicated region for block: B:47:0x0128  */
    /* JADX WARNING: Missing block: B:3:0x000b, code skipped:
            if (r0 != 2) goto L_0x001b;
     */
    /* JADX WARNING: Missing block: B:30:0x00ec, code skipped:
            if (r11 <= (r10.sliderRect.bottom + ((float) org.telegram.messenger.AndroidUtilities.dp(7.0f)))) goto L_0x00ee;
     */
    public boolean onTouchEvent(android.view.MotionEvent r11) {
        /*
        r10 = this;
        r0 = r11.getAction();
        r1 = 2;
        r2 = 0;
        r3 = 1;
        if (r0 == 0) goto L_0x0020;
    L_0x0009:
        if (r0 == r3) goto L_0x000e;
    L_0x000b:
        if (r0 == r1) goto L_0x0020;
    L_0x000d:
        goto L_0x001b;
    L_0x000e:
        r10.colorPressed = r2;
        r10.circlePressed = r2;
        r0 = android.os.SystemClock.uptimeMillis();
        r10.lastUpdateTime = r0;
        r10.invalidate();
    L_0x001b:
        r11 = super.onTouchEvent(r11);
        return r11;
    L_0x0020:
        r0 = r11.getX();
        r0 = (int) r0;
        r11 = r11.getY();
        r11 = (int) r11;
        r4 = r10.circlePressed;
        r5 = 0;
        r6 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r7 = NUM; // 0x42580000 float:54.0 double:5.499263994E-315;
        if (r4 != 0) goto L_0x004a;
    L_0x0033:
        r4 = r10.colorPressed;
        if (r4 != 0) goto L_0x00b0;
    L_0x0037:
        r4 = org.telegram.messenger.AndroidUtilities.dp(r7);
        if (r11 < r4) goto L_0x00b0;
    L_0x003d:
        r4 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r8 = r10.colorWheelBitmap;
        r8 = r8.getHeight();
        r4 = r4 + r8;
        if (r11 > r4) goto L_0x00b0;
    L_0x004a:
        r4 = r10.circlePressed;
        if (r4 != 0) goto L_0x0055;
    L_0x004e:
        r4 = r10.getParent();
        r4.requestDisallowInterceptTouchEvent(r3);
    L_0x0055:
        r10.circlePressed = r3;
        r10.pressedMoveProgress = r5;
        r8 = android.os.SystemClock.uptimeMillis();
        r10.lastUpdateTime = r8;
        r4 = r10.colorWheelBitmap;
        r4 = r4.getWidth();
        r0 = java.lang.Math.min(r0, r4);
        r0 = java.lang.Math.max(r2, r0);
        r4 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r8 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r9 = r10.colorWheelBitmap;
        r9 = r9.getHeight();
        r8 = r8 + r9;
        r11 = java.lang.Math.min(r11, r8);
        r11 = java.lang.Math.max(r4, r11);
        r4 = r10.colorHSV;
        r8 = (float) r0;
        r9 = NUM; // 0x43b40000 float:360.0 double:5.611943214E-315;
        r8 = r8 * r9;
        r9 = r10.colorWheelBitmap;
        r9 = r9.getWidth();
        r9 = (float) r9;
        r8 = r8 / r9;
        r4[r2] = r8;
        r4 = r10.colorHSV;
        r8 = r10.colorWheelBitmap;
        r8 = r8.getHeight();
        r8 = (float) r8;
        r8 = r6 / r8;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r7 = r11 - r7;
        r7 = (float) r7;
        r8 = r8 * r7;
        r7 = r6 - r8;
        r4[r3] = r7;
        r4 = 0;
        r10.colorGradient = r4;
    L_0x00b0:
        r4 = r10.colorPressed;
        if (r4 != 0) goto L_0x00ee;
    L_0x00b4:
        r4 = r10.circlePressed;
        if (r4 != 0) goto L_0x0118;
    L_0x00b8:
        r4 = (float) r0;
        r7 = r10.sliderRect;
        r8 = r7.left;
        r8 = (r4 > r8 ? 1 : (r4 == r8 ? 0 : -1));
        if (r8 < 0) goto L_0x0118;
    L_0x00c1:
        r7 = r7.right;
        r8 = NUM; // 0x41b00000 float:22.0 double:5.44486713E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r8 = (float) r8;
        r7 = r7 - r8;
        r4 = (r4 > r7 ? 1 : (r4 == r7 ? 0 : -1));
        if (r4 > 0) goto L_0x0118;
    L_0x00cf:
        r11 = (float) r11;
        r4 = r10.sliderRect;
        r4 = r4.top;
        r7 = NUM; // 0x40e00000 float:7.0 double:5.37751863E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r8 = (float) r8;
        r4 = r4 - r8;
        r4 = (r11 > r4 ? 1 : (r11 == r4 ? 0 : -1));
        if (r4 < 0) goto L_0x0118;
    L_0x00e0:
        r4 = r10.sliderRect;
        r4 = r4.bottom;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r7 = (float) r7;
        r4 = r4 + r7;
        r11 = (r11 > r4 ? 1 : (r11 == r4 ? 0 : -1));
        if (r11 > 0) goto L_0x0118;
    L_0x00ee:
        r11 = (float) r0;
        r0 = r10.sliderRect;
        r4 = r0.left;
        r11 = r11 - r4;
        r0 = r0.width();
        r11 = r11 / r0;
        r11 = r6 - r11;
        r0 = (r11 > r5 ? 1 : (r11 == r5 ? 0 : -1));
        if (r0 >= 0) goto L_0x0101;
    L_0x00ff:
        r11 = 0;
        goto L_0x0107;
    L_0x0101:
        r0 = (r11 > r6 ? 1 : (r11 == r6 ? 0 : -1));
        if (r0 <= 0) goto L_0x0107;
    L_0x0105:
        r11 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
    L_0x0107:
        r0 = r10.colorHSV;
        r0[r1] = r11;
        r11 = r10.colorPressed;
        if (r11 != 0) goto L_0x0116;
    L_0x010f:
        r11 = r10.getParent();
        r11.requestDisallowInterceptTouchEvent(r3);
    L_0x0116:
        r10.colorPressed = r3;
    L_0x0118:
        r11 = r10.colorPressed;
        if (r11 != 0) goto L_0x0120;
    L_0x011c:
        r11 = r10.circlePressed;
        if (r11 == 0) goto L_0x0182;
    L_0x0120:
        r11 = r10.getColor();
        r0 = r10.ignoreTextChange;
        if (r0 != 0) goto L_0x0178;
    L_0x0128:
        r0 = android.graphics.Color.red(r11);
        r4 = android.graphics.Color.green(r11);
        r5 = android.graphics.Color.blue(r11);
        r10.ignoreTextChange = r3;
        r6 = 3;
        r7 = new java.lang.Object[r6];
        r0 = (byte) r0;
        r0 = java.lang.Byte.valueOf(r0);
        r7[r2] = r0;
        r0 = (byte) r4;
        r0 = java.lang.Byte.valueOf(r0);
        r7[r3] = r0;
        r0 = (byte) r5;
        r0 = java.lang.Byte.valueOf(r0);
        r7[r1] = r0;
        r0 = "%02x%02x%02x";
        r0 = java.lang.String.format(r0, r7);
        r0 = r0.toUpperCase();
        r1 = r10.colorEditText;
        r4 = r10.selectedEditText;
        if (r4 != 0) goto L_0x0160;
    L_0x015e:
        r4 = 1;
        goto L_0x0161;
    L_0x0160:
        r4 = 3;
    L_0x0161:
        r1 = r1[r4];
        r1.setText(r0);
        r1 = r10.colorEditText;
        r4 = r10.selectedEditText;
        if (r4 != 0) goto L_0x016d;
    L_0x016c:
        r6 = 1;
    L_0x016d:
        r1 = r1[r6];
        r0 = r0.length();
        r1.setSelection(r0);
        r10.ignoreTextChange = r2;
    L_0x0178:
        r0 = r10.delegate;
        r1 = r10.selectedEditText;
        r0.setColor(r11, r1, r2);
        r10.invalidate();
    L_0x0182:
        return r3;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ColorPicker.onTouchEvent(android.view.MotionEvent):boolean");
    }

    private void setColorInner(int i) {
        Color.colorToHSV(i, this.colorHSV);
        this.colorGradient = null;
        invalidate();
    }

    public void setColor(int i, int i2) {
        if (!this.ignoreTextChange) {
            this.ignoreTextChange = true;
            int i3 = 3;
            String toUpperCase = String.format("%02x%02x%02x", new Object[]{Byte.valueOf((byte) Color.red(i)), Byte.valueOf((byte) Color.green(i)), Byte.valueOf((byte) Color.blue(i))}).toUpperCase();
            this.colorEditText[i2 == 0 ? 1 : 3].setText(toUpperCase);
            EditTextBoldCursor[] editTextBoldCursorArr = this.colorEditText;
            if (i2 == 0) {
                i3 = 1;
            }
            editTextBoldCursorArr[i3].setSelection(toUpperCase.length());
            this.ignoreTextChange = false;
        }
        setColorInner(i);
        if (i2 == 1 && i != 0 && this.clearButton.getTag() == null) {
            toggleSecondField();
        }
    }

    public void setHasChanges(final boolean z) {
        if ((!z || this.resetButton.getTag() == null) && (z || this.resetButton.getTag() != null)) {
            this.resetButton.setTag(z ? Integer.valueOf(1) : null);
            if (this.clearButton.getTag() == null) {
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
    }

    public void setType(int i, boolean z, boolean z2, boolean z3, boolean z4) {
        int i2;
        float f;
        float[] fArr;
        ImageView imageView;
        Property property;
        String str;
        int i3 = i;
        final boolean z5 = z;
        final boolean z6 = z2;
        final boolean z7 = z3;
        this.currentResetType = i3;
        Object obj = null;
        ArrayList arrayList = z4 ? new ArrayList() : null;
        if (!(z6 && z7) && this.colorEditText[3].isFocused()) {
            this.colorEditText[1].requestFocus();
        }
        int i4 = 2;
        while (true) {
            i2 = 8;
            f = 1.0f;
            if (i4 >= 4) {
                break;
            }
            if (z4) {
                if (z6) {
                    this.colorEditText[i4].setVisibility(0);
                }
                Object obj2 = this.colorEditText[i4];
                Property property2 = View.ALPHA;
                fArr = new float[1];
                if (!(z6 && z7)) {
                    f = 0.0f;
                }
                fArr[0] = f;
                arrayList.add(ObjectAnimator.ofFloat(obj2, property2, fArr));
            } else {
                EditText editText = this.colorEditText[i4];
                if (z6) {
                    i2 = 0;
                }
                editText.setVisibility(i2);
                editText = this.colorEditText[i4];
                if (!(z6 && z7)) {
                    f = 0.0f;
                }
                editText.setAlpha(f);
            }
            this.colorEditText[i4].setTag(z6 ? Integer.valueOf(1) : null);
            i4++;
        }
        if (z4) {
            if (z6) {
                this.exchangeButton.setVisibility(0);
            }
            imageView = this.exchangeButton;
            property = View.ALPHA;
            fArr = new float[1];
            float f2 = (z6 && z7) ? 1.0f : 0.0f;
            fArr[0] = f2;
            arrayList.add(ObjectAnimator.ofFloat(imageView, property, fArr));
        } else {
            this.exchangeButton.setVisibility(z6 ? 0 : 8);
            imageView = this.exchangeButton;
            float f3 = (z6 && z7) ? 1.0f : 0.0f;
            imageView.setAlpha(f3);
        }
        if (z6) {
            this.clearButton.setTag(z7 ? Integer.valueOf(1) : null);
            this.clearButton.setRotation(z7 ? 0.0f : 45.0f);
        }
        if (z4) {
            if (z6) {
                this.clearButton.setVisibility(0);
            }
            imageView = this.clearButton;
            property = View.ALPHA;
            fArr = new float[1];
            fArr[0] = z6 ? 1.0f : 0.0f;
            arrayList.add(ObjectAnimator.ofFloat(imageView, property, fArr));
        } else {
            this.clearButton.setVisibility(z6 ? 0 : 8);
            this.clearButton.setAlpha(z6 ? 1.0f : 0.0f);
        }
        TextView textView = this.resetButton;
        if (z5) {
            obj = Integer.valueOf(1);
        }
        textView.setTag(obj);
        TextView textView2 = this.resetButton;
        if (i3 == 1) {
            i4 = NUM;
            str = "ColorPickerResetAll";
        } else {
            i4 = NUM;
            str = "ColorPickerReset";
        }
        textView2.setText(LocaleController.getString(str, i4));
        ((LayoutParams) this.resetButton.getLayoutParams()).rightMargin = AndroidUtilities.dp(i3 == 1 ? 14.0f : 61.0f);
        if (!z4) {
            TextView textView3 = this.resetButton;
            if (!z5 || z7) {
                f = 0.0f;
            }
            textView3.setAlpha(f);
            textView3 = this.resetButton;
            if (z5 && !z7) {
                i2 = 0;
            }
            textView3.setVisibility(i2);
        } else if (!z5 || (this.resetButton.getVisibility() == 0 && z7)) {
            arrayList.add(ObjectAnimator.ofFloat(this.resetButton, View.ALPHA, new float[]{0.0f}));
        } else if (!(this.resetButton.getVisibility() == 0 || z7)) {
            this.resetButton.setVisibility(0);
            arrayList.add(ObjectAnimator.ofFloat(this.resetButton, View.ALPHA, new float[]{1.0f}));
        }
        if (arrayList != null && !arrayList.isEmpty()) {
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(arrayList);
            animatorSet.setDuration(180);
            animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    if (!z5 || z7) {
                        ColorPicker.this.resetButton.setVisibility(8);
                    }
                    if (!z6) {
                        ColorPicker.this.clearButton.setVisibility(8);
                        ColorPicker.this.exchangeButton.setVisibility(8);
                        for (int i = 2; i < 4; i++) {
                            ColorPicker.this.colorEditText[i].setVisibility(8);
                        }
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
                list2.add(new ThemeDescription(this.clearButton, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"));
                return;
            }
        }
    }

    private int getColorDistance(int i, int i2) {
        int red = Color.red(i);
        int green = Color.green(i);
        i = Color.blue(i);
        int red2 = Color.red(i2);
        int i3 = (red + red2) / 2;
        red -= red2;
        green -= Color.green(i2);
        i -= Color.blue(i2);
        return (((((i3 + 512) * red) * red) >> 8) + ((green * 4) * green)) + ((((767 - i3) * i) * i) >> 8);
    }

    private int generateGradientColors(int i) {
        int i2 = i;
        int i3 = 0;
        int i4 = 0;
        int i5 = 0;
        int i6 = 0;
        while (true) {
            int[] iArr = this.colorPairs;
            if (i3 >= iArr.length / 2) {
                break;
            }
            int i7 = i3 * 2;
            int i8 = iArr[i7];
            int i9 = iArr[i7 + 1];
            i7 = getColorDistance(i2, i8);
            int colorDistance = getColorDistance(i2, i9);
            if (i3 == 0 || i7 < i6 || colorDistance < i6) {
                if (i7 < colorDistance) {
                    i5 = i9;
                    i6 = i7;
                    i4 = i8;
                } else {
                    i4 = i9;
                    i5 = i8;
                    i6 = colorDistance;
                }
            }
            i3++;
        }
        double[] rgbToHsv = AndroidUtilities.rgbToHsv(i);
        double[] rgbToHsv2 = AndroidUtilities.rgbToHsv(i4);
        double[] rgbToHsv3 = AndroidUtilities.rgbToHsv(i5);
        double[] dArr = new double[3];
        dArr[0] = rgbToHsv2[0] > 0.0d ? rgbToHsv[0] / rgbToHsv2[0] : 1.0d;
        dArr[1] = rgbToHsv2[1] > 0.0d ? rgbToHsv[1] / rgbToHsv2[1] : 1.0d;
        dArr[2] = rgbToHsv2[2] > 0.0d ? rgbToHsv[2] / rgbToHsv2[2] : 1.0d;
        return AndroidUtilities.hsvToColor(Math.min(1.0d, rgbToHsv3[0] * dArr[0]), Math.min(1.0d, rgbToHsv3[1] * dArr[1]), Math.min(1.0d, rgbToHsv3[2] * dArr[2]));
    }
}
