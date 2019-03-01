package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.SweepGradient;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.CharacterStyle;
import android.util.Property;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.DownloadController.FileDownloadProgressListener;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController.SearchImage;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.ItemDecoration;
import org.telegram.messenger.support.widget.RecyclerView.LayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.LayoutParams;
import org.telegram.messenger.support.widget.RecyclerView.State;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.KeyboardButton;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_message;
import org.telegram.tgnet.TLRPC.TL_messageMediaEmpty;
import org.telegram.tgnet.TLRPC.TL_peerUser;
import org.telegram.tgnet.TLRPC.TL_pollAnswer;
import org.telegram.tgnet.TLRPC.TL_replyInlineMarkup;
import org.telegram.tgnet.TLRPC.TL_wallPaper;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.ChatActionCell;
import org.telegram.ui.Cells.ChatActionCell$ChatActionCellDelegate$$CC;
import org.telegram.ui.Cells.ChatActionCell.ChatActionCellDelegate;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Cells.ChatMessageCell$ChatMessageCellDelegate$$CC;
import org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Components.AnimationProperties.FloatProperty;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RadialProgress2;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.ui.Components.SeekBarView;
import org.telegram.ui.Components.ShareAlert;
import org.telegram.ui.Components.WallpaperParallaxEffect;
import org.telegram.ui.WallpapersListActivity.ColorWallpaper;
import org.telegram.ui.WallpapersListActivity.FileWallpaper;

public class WallpaperActivity extends BaseFragment implements FileDownloadProgressListener, NotificationCenterDelegate {
    private static final int share_item = 1;
    private int TAG;
    private int backgroundColor;
    private BackupImageView backgroundImage;
    private Paint backgroundPaint;
    private Mode blendMode = Mode.SRC_IN;
    private Bitmap blurredBitmap;
    private FrameLayout bottomOverlayChat;
    private TextView bottomOverlayChatText;
    private FrameLayout buttonsContainer;
    private CheckBoxView[] checkBoxView;
    private Paint checkPaint;
    private ColorPicker colorPicker;
    private float currentIntensity = 0.4f;
    private Object currentWallpaper;
    private Bitmap currentWallpaperBitmap;
    private WallpaperActivityDelegate delegate;
    private Paint eraserPaint;
    private String imageFilter = "640_360";
    private HeaderCell intensityCell;
    private SeekBarView intensitySeekBar;
    private boolean isBlurred;
    private boolean isMotion;
    private RecyclerListView listView;
    private String loadingFile = null;
    private File loadingFileObject = null;
    private PhotoSize loadingSize = null;
    private int maxWallpaperSize = 1920;
    private AnimatorSet motionAnimation;
    private WallpaperParallaxEffect parallaxEffect;
    private float parallaxScale = 1.0f;
    private int patternColor;
    private FrameLayout[] patternLayout = new FrameLayout[3];
    private ArrayList<Object> patterns;
    private PatternsAdapter patternsAdapter;
    private FrameLayout[] patternsButtonsContainer = new FrameLayout[2];
    private TextView[] patternsCancelButton = new TextView[2];
    private LinearLayoutManager patternsLayoutManager;
    private RecyclerListView patternsListView;
    private TextView[] patternsSaveButton = new TextView[2];
    private int previousBackgroundColor;
    private float previousIntensity;
    private TL_wallPaper previousSelectedPattern;
    private boolean progressVisible;
    private RadialProgress2 radialProgress;
    private TL_wallPaper selectedPattern;
    private TextPaint textPaint;
    private Drawable themedWallpaper;
    private boolean viewCreated;

    private class CheckBoxView extends View {
        private static final float progressBounceDiff = 0.2f;
        public final Property<CheckBoxView, Float> PROGRESS_PROPERTY = new FloatProperty<CheckBoxView>("progress") {
            public void setValue(CheckBoxView object, float value) {
                CheckBoxView.this.progress = value;
                CheckBoxView.this.invalidate();
            }

            public Float get(CheckBoxView object) {
                return Float.valueOf(CheckBoxView.this.progress);
            }
        };
        private ObjectAnimator checkAnimator;
        private String currentText;
        private int currentTextSize;
        private Bitmap drawBitmap;
        private Canvas drawCanvas;
        private boolean isChecked;
        private int maxTextSize;
        private float progress;
        private RectF rect = new RectF();

        public CheckBoxView(Context context, boolean check) {
            super(context);
            if (check) {
                this.drawBitmap = Bitmap.createBitmap(AndroidUtilities.dp(18.0f), AndroidUtilities.dp(18.0f), Config.ARGB_4444);
                this.drawCanvas = new Canvas(this.drawBitmap);
            }
        }

        public void setText(String text, int current, int max) {
            this.currentText = text;
            this.currentTextSize = current;
            this.maxTextSize = max;
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(MeasureSpec.makeMeasureSpec(this.maxTextSize + AndroidUtilities.dp(56.0f), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(32.0f), NUM));
        }

        protected void onDraw(Canvas canvas) {
            this.rect.set(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight());
            canvas.drawRoundRect(this.rect, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), Theme.chat_actionBackgroundPaint);
            int x = ((getMeasuredWidth() - this.currentTextSize) - AndroidUtilities.dp(28.0f)) / 2;
            canvas.drawText(this.currentText, (float) (AndroidUtilities.dp(28.0f) + x), (float) AndroidUtilities.dp(21.0f), WallpaperActivity.this.textPaint);
            canvas.save();
            canvas.translate((float) x, (float) AndroidUtilities.dp(7.0f));
            if (this.drawBitmap != null) {
                float checkProgress;
                float bounceProgress;
                if (this.progress <= 0.5f) {
                    checkProgress = this.progress / 0.5f;
                    bounceProgress = checkProgress;
                } else {
                    bounceProgress = 2.0f - (this.progress / 0.5f);
                    checkProgress = 1.0f;
                }
                float bounce = ((float) AndroidUtilities.dp(1.0f)) * bounceProgress;
                this.rect.set(bounce, bounce, ((float) AndroidUtilities.dp(18.0f)) - bounce, ((float) AndroidUtilities.dp(18.0f)) - bounce);
                this.drawBitmap.eraseColor(0);
                WallpaperActivity.this.backgroundPaint.setColor(-1);
                this.drawCanvas.drawRoundRect(this.rect, this.rect.width() / 2.0f, this.rect.height() / 2.0f, WallpaperActivity.this.backgroundPaint);
                if (checkProgress != 1.0f) {
                    float rad = Math.min((float) AndroidUtilities.dp(7.0f), (((float) AndroidUtilities.dp(7.0f)) * checkProgress) + bounce);
                    this.rect.set(((float) AndroidUtilities.dp(2.0f)) + rad, ((float) AndroidUtilities.dp(2.0f)) + rad, ((float) AndroidUtilities.dp(16.0f)) - rad, ((float) AndroidUtilities.dp(16.0f)) - rad);
                    this.drawCanvas.drawRoundRect(this.rect, this.rect.width() / 2.0f, this.rect.height() / 2.0f, WallpaperActivity.this.eraserPaint);
                }
                if (this.progress > 0.5f) {
                    this.drawCanvas.drawLine((float) AndroidUtilities.dp(7.3f), (float) AndroidUtilities.dp(13.0f), (float) ((int) (((float) AndroidUtilities.dp(7.3f)) - (((float) AndroidUtilities.dp(2.5f)) * (1.0f - bounceProgress)))), (float) ((int) (((float) AndroidUtilities.dp(13.0f)) - (((float) AndroidUtilities.dp(2.5f)) * (1.0f - bounceProgress)))), WallpaperActivity.this.checkPaint);
                    this.drawCanvas.drawLine((float) AndroidUtilities.dp(7.3f), (float) AndroidUtilities.dp(13.0f), (float) ((int) (((float) AndroidUtilities.dp(7.3f)) + (((float) AndroidUtilities.dp(6.0f)) * (1.0f - bounceProgress)))), (float) ((int) (((float) AndroidUtilities.dp(13.0f)) - (((float) AndroidUtilities.dp(6.0f)) * (1.0f - bounceProgress)))), WallpaperActivity.this.checkPaint);
                }
                canvas.drawBitmap(this.drawBitmap, 0.0f, 0.0f, null);
            } else {
                WallpaperActivity.this.backgroundPaint.setColor(WallpaperActivity.this.backgroundColor);
                this.rect.set(0.0f, 0.0f, (float) AndroidUtilities.dp(18.0f), (float) AndroidUtilities.dp(18.0f));
                canvas.drawRoundRect(this.rect, this.rect.width() / 2.0f, this.rect.height() / 2.0f, WallpaperActivity.this.backgroundPaint);
            }
            canvas.restore();
        }

        private void setProgress(float value) {
            if (this.progress != value) {
                this.progress = value;
                invalidate();
            }
        }

        private void cancelCheckAnimator() {
            if (this.checkAnimator != null) {
                this.checkAnimator.cancel();
            }
        }

        private void animateToCheckedState(boolean newCheckedState) {
            Property property = this.PROGRESS_PROPERTY;
            float[] fArr = new float[1];
            fArr[0] = newCheckedState ? 1.0f : 0.0f;
            this.checkAnimator = ObjectAnimator.ofFloat(this, property, fArr);
            this.checkAnimator.setDuration(300);
            this.checkAnimator.start();
        }

        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            super.onLayout(changed, left, top, right, bottom);
        }

        public void setChecked(boolean checked, boolean animated) {
            if (checked != this.isChecked) {
                this.isChecked = checked;
                if (animated) {
                    animateToCheckedState(checked);
                    return;
                }
                cancelCheckAnimator();
                this.progress = checked ? 1.0f : 0.0f;
                invalidate();
            }
        }

        public boolean isChecked() {
            return this.isChecked;
        }
    }

    private class ColorPicker extends FrameLayout {
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
        private float[] hsvTemp = new float[3];
        boolean ignoreTextChange;
        private LinearLayout linearLayout;
        private int lx;
        private int ly;
        private final int paramValueSliderWidth = AndroidUtilities.dp(20.0f);
        private Paint valueSliderPaint;

        public ColorPicker(Context context) {
            super(context);
            setWillNotDraw(false);
            this.circlePaint = new Paint(1);
            this.circleDrawable = context.getResources().getDrawable(R.drawable.knob_shadow).mutate();
            this.colorWheelPaint = new Paint();
            this.colorWheelPaint.setAntiAlias(true);
            this.colorWheelPaint.setDither(true);
            this.valueSliderPaint = new Paint();
            this.valueSliderPaint.setAntiAlias(true);
            this.valueSliderPaint.setDither(true);
            this.linearLayout = new LinearLayout(context);
            this.linearLayout.setOrientation(0);
            addView(this.linearLayout, LayoutHelper.createFrame(-1, 64.0f, 51, 12.0f, 14.0f, 21.0f, 0.0f));
            int a = 0;
            while (a < 2) {
                int i;
                final int num = a;
                this.colorEditText[a] = new EditTextBoldCursor(context);
                this.colorEditText[a].setTextSize(1, 18.0f);
                this.colorEditText[a].setHintColor(Theme.getColor("windowBackgroundWhiteHintText"));
                this.colorEditText[a].setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
                this.colorEditText[a].setBackgroundDrawable(null);
                this.colorEditText[a].setCursorColor(Theme.getColor("windowBackgroundWhiteBlackText"));
                this.colorEditText[a].setCursorSize(AndroidUtilities.dp(20.0f));
                this.colorEditText[a].setCursorWidth(1.5f);
                this.colorEditText[a].setSingleLine(true);
                this.colorEditText[a].setGravity(19);
                this.colorEditText[a].setHeaderHintColor(Theme.getColor("windowBackgroundWhiteBlueHeader"));
                this.colorEditText[a].setTransformHintToHeader(true);
                this.colorEditText[a].setLineColors(Theme.getColor("windowBackgroundWhiteInputField"), Theme.getColor("windowBackgroundWhiteInputFieldActivated"), Theme.getColor("windowBackgroundWhiteRedText3"));
                this.colorEditText[a].setPadding(0, 0, 0, 0);
                if (a == 0) {
                    this.colorEditText[a].setInputType(1);
                    this.colorEditText[a].setHintText(LocaleController.getString("BackgroundHexColorCode", R.string.BackgroundHexColorCode));
                } else {
                    this.colorEditText[a].setInputType(2);
                    this.colorEditText[a].setHintText(LocaleController.getString("BackgroundBrightness", R.string.BackgroundBrightness));
                }
                this.colorEditText[a].setImeOptions(NUM);
                InputFilter[] inputFilters = new InputFilter[1];
                inputFilters[0] = new LengthFilter(a == 0 ? 7 : 3);
                this.colorEditText[a].setFilters(inputFilters);
                LinearLayout linearLayout = this.linearLayout;
                View view = this.colorEditText[a];
                float f = a == 0 ? 0.67f : 0.31f;
                if (a != 1) {
                    i = 23;
                } else {
                    i = 0;
                }
                linearLayout.addView(view, LayoutHelper.createLinear(0, -1, f, 0, 0, i, 0));
                this.colorEditText[a].addTextChangedListener(new TextWatcher(WallpaperActivity.this) {
                    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    }

                    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    }

                    public void afterTextChanged(Editable editable) {
                        if (!ColorPicker.this.ignoreTextChange) {
                            ColorPicker.this.ignoreTextChange = true;
                            if (num == 0) {
                                int a = 0;
                                while (a < editable.length()) {
                                    char ch = editable.charAt(a);
                                    if ((ch < '0' || ch > '9') && ((ch < 'a' || ch > 'f') && ((ch < 'A' || ch > 'F') && !(ch == '#' && a == 0)))) {
                                        editable.replace(a, a + 1, "");
                                        a--;
                                    }
                                    a++;
                                }
                                if (editable.length() == 0) {
                                    editable.append("#");
                                } else if (editable.charAt(0) != '#') {
                                    editable.insert(0, "#");
                                }
                                try {
                                    ColorPicker.this.setColor(Integer.parseInt(editable.toString().substring(1), 16) | -16777216);
                                } catch (Exception e) {
                                    ColorPicker.this.setColor(-1);
                                }
                                WallpaperActivity.this.setBackgroundColor(ColorPicker.this.getColor());
                                ColorPicker.this.colorEditText[1].setText("" + ((int) (ColorPicker.this.colorHSV[2] * 255.0f)));
                            } else {
                                int value = Utilities.parseInt(editable.toString()).intValue();
                                if (value > 255 || value < 0) {
                                    if (value > 255) {
                                        value = 255;
                                    } else {
                                        value = 0;
                                    }
                                    editable.replace(0, editable.length(), "" + value);
                                }
                                ColorPicker.this.colorHSV[2] = ((float) value) / 255.0f;
                                WallpaperActivity.this.setBackgroundColor(ColorPicker.this.getColor());
                                int red = Color.red(WallpaperActivity.this.backgroundColor);
                                int green = Color.green(WallpaperActivity.this.backgroundColor);
                                int blue = Color.blue(WallpaperActivity.this.backgroundColor);
                                ColorPicker.this.colorEditText[0].setText(String.format("#%02x%02x%02x", new Object[]{Byte.valueOf((byte) red), Byte.valueOf((byte) green), Byte.valueOf((byte) blue)}));
                            }
                            ColorPicker.this.invalidate();
                            ColorPicker.this.ignoreTextChange = false;
                        }
                    }
                });
                this.colorEditText[a].setOnEditorActionListener(WallpaperActivity$ColorPicker$$Lambda$0.$instance);
                a++;
            }
        }

        static final /* synthetic */ boolean lambda$new$0$WallpaperActivity$ColorPicker(TextView textView, int i, KeyEvent keyEvent) {
            if (i != 6) {
                return false;
            }
            AndroidUtilities.hideKeyboard(textView);
            return true;
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int widthSize = MeasureSpec.getSize(widthMeasureSpec);
            int size = Math.min(widthSize, MeasureSpec.getSize(heightMeasureSpec));
            measureChild(this.linearLayout, MeasureSpec.makeMeasureSpec(widthSize - AndroidUtilities.dp(42.0f), NUM), heightMeasureSpec);
            setMeasuredDimension(size, size);
        }

        protected void onDraw(Canvas canvas) {
            this.centerX = ((getWidth() / 2) - (this.paramValueSliderWidth * 2)) + AndroidUtilities.dp(11.0f);
            this.centerY = (getHeight() / 2) + AndroidUtilities.dp(34.0f);
            canvas.drawBitmap(this.colorWheelBitmap, (float) (this.centerX - this.colorWheelRadius), (float) (this.centerY - this.colorWheelRadius), null);
            float hueAngle = (float) Math.toRadians((double) this.colorHSV[0]);
            int colorPointX = ((int) (((-Math.cos((double) hueAngle)) * ((double) this.colorHSV[1])) * ((double) this.colorWheelRadius))) + this.centerX;
            int colorPointY = ((int) (((-Math.sin((double) hueAngle)) * ((double) this.colorHSV[1])) * ((double) this.colorWheelRadius))) + this.centerY;
            float pointerRadius = 0.075f * ((float) this.colorWheelRadius);
            this.hsvTemp[0] = this.colorHSV[0];
            this.hsvTemp[1] = this.colorHSV[1];
            this.hsvTemp[2] = 1.0f;
            drawPointerArrow(canvas, colorPointX, colorPointY, Color.HSVToColor(this.hsvTemp));
            this.lx = (this.centerX + this.colorWheelRadius) + (this.paramValueSliderWidth * 2);
            this.ly = this.centerY - this.colorWheelRadius;
            int width = AndroidUtilities.dp(9.0f);
            int height = this.colorWheelRadius * 2;
            if (this.colorGradient == null) {
                this.colorGradient = new LinearGradient((float) this.lx, (float) this.ly, (float) (this.lx + width), (float) (this.ly + height), new int[]{-16777216, Color.HSVToColor(this.hsvTemp)}, null, TileMode.CLAMP);
            }
            this.valueSliderPaint.setShader(this.colorGradient);
            canvas.drawRect((float) this.lx, (float) this.ly, (float) (this.lx + width), (float) (this.ly + height), this.valueSliderPaint);
            drawPointerArrow(canvas, this.lx + (width / 2), (int) (((float) this.ly) + (this.colorHSV[2] * ((float) height))), Color.HSVToColor(this.colorHSV));
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

        protected void onSizeChanged(int width, int height, int oldw, int oldh) {
            if (this.colorWheelRadius != AndroidUtilities.dp(120.0f)) {
                this.colorWheelRadius = AndroidUtilities.dp(120.0f);
                this.colorWheelBitmap = createColorWheelBitmap(this.colorWheelRadius * 2, this.colorWheelRadius * 2);
                this.colorGradient = null;
            }
        }

        private Bitmap createColorWheelBitmap(int width, int height) {
            Bitmap bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
            int[] colors = new int[13];
            float[] hsv = new float[]{0.0f, 1.0f, 1.0f};
            for (int i = 0; i < colors.length; i++) {
                hsv[0] = (float) (((i * 30) + 180) % 360);
                colors[i] = Color.HSVToColor(hsv);
            }
            colors[12] = colors[0];
            this.colorWheelPaint.setShader(new ComposeShader(new SweepGradient((float) (width / 2), (float) (height / 2), colors, null), new RadialGradient((float) (width / 2), (float) (height / 2), (float) this.colorWheelRadius, -1, 16777215, TileMode.CLAMP), Mode.SRC_OVER));
            new Canvas(bitmap).drawCircle((float) (width / 2), (float) (height / 2), (float) this.colorWheelRadius, this.colorWheelPaint);
            return bitmap;
        }

        public boolean onTouchEvent(MotionEvent event) {
            switch (event.getAction()) {
                case 0:
                case 2:
                    int x = (int) event.getX();
                    int y = (int) event.getY();
                    int cx = x - this.centerX;
                    int cy = y - this.centerY;
                    double d = Math.sqrt((double) ((cx * cx) + (cy * cy)));
                    if (this.circlePressed || (!this.colorPressed && d <= ((double) this.colorWheelRadius))) {
                        if (d > ((double) this.colorWheelRadius)) {
                            d = (double) this.colorWheelRadius;
                        }
                        if (!this.circlePressed) {
                            getParent().requestDisallowInterceptTouchEvent(true);
                        }
                        this.circlePressed = true;
                        this.colorHSV[0] = (float) (Math.toDegrees(Math.atan2((double) cy, (double) cx)) + 180.0d);
                        this.colorHSV[1] = Math.max(0.0f, Math.min(1.0f, (float) (d / ((double) this.colorWheelRadius))));
                        this.colorGradient = null;
                    }
                    if (this.colorPressed || (!this.circlePressed && x >= this.lx && x <= this.lx + this.paramValueSliderWidth && y >= this.ly && y <= this.ly + (this.colorWheelRadius * 2))) {
                        float value = ((float) (y - this.ly)) / (((float) this.colorWheelRadius) * 2.0f);
                        if (value < 0.0f) {
                            value = 0.0f;
                        } else if (value > 1.0f) {
                            value = 1.0f;
                        }
                        this.colorHSV[2] = value;
                        if (!this.colorPressed) {
                            getParent().requestDisallowInterceptTouchEvent(true);
                        }
                        this.colorPressed = true;
                    }
                    if (this.colorPressed || this.circlePressed) {
                        WallpaperActivity.this.setBackgroundColor(getColor());
                        if (!this.ignoreTextChange) {
                            int red = Color.red(WallpaperActivity.this.backgroundColor);
                            int green = Color.green(WallpaperActivity.this.backgroundColor);
                            int blue = Color.blue(WallpaperActivity.this.backgroundColor);
                            this.ignoreTextChange = true;
                            this.colorEditText[0].setText(String.format("#%02x%02x%02x", new Object[]{Byte.valueOf((byte) red), Byte.valueOf((byte) green), Byte.valueOf((byte) blue)}));
                            this.colorEditText[1].setText("" + ((int) (255.0f * this.colorHSV[2])));
                            for (int b = 0; b < 2; b++) {
                                this.colorEditText[b].setSelection(this.colorEditText[b].length());
                            }
                            this.ignoreTextChange = false;
                        }
                        invalidate();
                    }
                    return true;
                case 1:
                    this.colorPressed = false;
                    this.circlePressed = false;
                    break;
            }
            return super.onTouchEvent(event);
        }

        public void setColor(int color) {
            if (this.ignoreTextChange) {
                Color.colorToHSV(color, this.colorHSV);
            } else {
                this.ignoreTextChange = true;
                int red = Color.red(color);
                int green = Color.green(color);
                int blue = Color.blue(color);
                Color.colorToHSV(color, this.colorHSV);
                this.colorEditText[0].setText(String.format("#%02x%02x%02x", new Object[]{Byte.valueOf((byte) red), Byte.valueOf((byte) green), Byte.valueOf((byte) blue)}));
                this.colorEditText[1].setText("" + ((int) (255.0f * this.colorHSV[2])));
                for (int b = 0; b < 2; b++) {
                    this.colorEditText[b].setSelection(this.colorEditText[b].length());
                }
                this.ignoreTextChange = false;
            }
            this.colorGradient = null;
            invalidate();
        }

        public int getColor() {
            return (Color.HSVToColor(this.colorHSV) & 16777215) | -16777216;
        }
    }

    private class ListAdapter extends SelectionAdapter {
        private Context mContext;
        private ArrayList<MessageObject> messages = new ArrayList();

        public ListAdapter(Context context) {
            this.mContext = context;
            int date = ((int) (System.currentTimeMillis() / 1000)) - 3600;
            Message message = new TL_message();
            if (WallpaperActivity.this.currentWallpaper instanceof ColorWallpaper) {
                message.message = LocaleController.getString("BackgroundColorSinglePreviewLine2", R.string.BackgroundColorSinglePreviewLine2);
            } else {
                message.message = LocaleController.getString("BackgroundPreviewLine2", R.string.BackgroundPreviewLine2);
            }
            message.date = date + 60;
            message.dialog_id = 1;
            message.flags = 259;
            message.from_id = UserConfig.getInstance(WallpaperActivity.this.currentAccount).getClientUserId();
            message.id = 1;
            message.media = new TL_messageMediaEmpty();
            message.out = true;
            message.to_id = new TL_peerUser();
            message.to_id.user_id = 0;
            MessageObject messageObject = new MessageObject(WallpaperActivity.this.currentAccount, message, true);
            messageObject.eventId = 1;
            messageObject.resetLayout();
            this.messages.add(messageObject);
            message = new TL_message();
            if (WallpaperActivity.this.currentWallpaper instanceof ColorWallpaper) {
                message.message = LocaleController.getString("BackgroundColorSinglePreviewLine1", R.string.BackgroundColorSinglePreviewLine1);
            } else {
                message.message = LocaleController.getString("BackgroundPreviewLine1", R.string.BackgroundPreviewLine1);
            }
            message.date = date + 60;
            message.dialog_id = 1;
            message.flags = 265;
            message.from_id = 0;
            message.id = 1;
            message.reply_to_msg_id = 5;
            message.media = new TL_messageMediaEmpty();
            message.out = false;
            message.to_id = new TL_peerUser();
            message.to_id.user_id = UserConfig.getInstance(WallpaperActivity.this.currentAccount).getClientUserId();
            messageObject = new MessageObject(WallpaperActivity.this.currentAccount, message, true);
            messageObject.eventId = 1;
            messageObject.resetLayout();
            this.messages.add(messageObject);
            message = new TL_message();
            message.message = LocaleController.formatDateChat((long) date);
            message.id = 0;
            message.date = date;
            messageObject = new MessageObject(WallpaperActivity.this.currentAccount, message, false);
            messageObject.type = 10;
            messageObject.contentType = 1;
            messageObject.isDateObject = true;
            this.messages.add(messageObject);
        }

        public boolean isEnabled(ViewHolder holder) {
            return false;
        }

        public int getItemCount() {
            return this.messages.size();
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View view = null;
            if (viewType == 0) {
                view = new ChatMessageCell(this.mContext);
                ((ChatMessageCell) view).setDelegate(new ChatMessageCellDelegate() {
                    public boolean canPerformActions() {
                        return ChatMessageCell$ChatMessageCellDelegate$$CC.canPerformActions(this);
                    }

                    public void didLongPress(ChatMessageCell chatMessageCell) {
                        ChatMessageCell$ChatMessageCellDelegate$$CC.didLongPress(this, chatMessageCell);
                    }

                    public void didPressBotButton(ChatMessageCell chatMessageCell, KeyboardButton keyboardButton) {
                        ChatMessageCell$ChatMessageCellDelegate$$CC.didPressBotButton(this, chatMessageCell, keyboardButton);
                    }

                    public void didPressCancelSendButton(ChatMessageCell chatMessageCell) {
                        ChatMessageCell$ChatMessageCellDelegate$$CC.didPressCancelSendButton(this, chatMessageCell);
                    }

                    public void didPressChannelAvatar(ChatMessageCell chatMessageCell, Chat chat, int i) {
                        ChatMessageCell$ChatMessageCellDelegate$$CC.didPressChannelAvatar(this, chatMessageCell, chat, i);
                    }

                    public void didPressImage(ChatMessageCell chatMessageCell) {
                        ChatMessageCell$ChatMessageCellDelegate$$CC.didPressImage(this, chatMessageCell);
                    }

                    public void didPressInstantButton(ChatMessageCell chatMessageCell, int i) {
                        ChatMessageCell$ChatMessageCellDelegate$$CC.didPressInstantButton(this, chatMessageCell, i);
                    }

                    public void didPressOther(ChatMessageCell chatMessageCell) {
                        ChatMessageCell$ChatMessageCellDelegate$$CC.didPressOther(this, chatMessageCell);
                    }

                    public void didPressReplyMessage(ChatMessageCell chatMessageCell, int i) {
                        ChatMessageCell$ChatMessageCellDelegate$$CC.didPressReplyMessage(this, chatMessageCell, i);
                    }

                    public void didPressShare(ChatMessageCell chatMessageCell) {
                        ChatMessageCell$ChatMessageCellDelegate$$CC.didPressShare(this, chatMessageCell);
                    }

                    public void didPressUrl(MessageObject messageObject, CharacterStyle characterStyle, boolean z) {
                        ChatMessageCell$ChatMessageCellDelegate$$CC.didPressUrl(this, messageObject, characterStyle, z);
                    }

                    public void didPressUserAvatar(ChatMessageCell chatMessageCell, User user) {
                        ChatMessageCell$ChatMessageCellDelegate$$CC.didPressUserAvatar(this, chatMessageCell, user);
                    }

                    public void didPressViaBot(ChatMessageCell chatMessageCell, String str) {
                        ChatMessageCell$ChatMessageCellDelegate$$CC.didPressViaBot(this, chatMessageCell, str);
                    }

                    public void didPressVoteButton(ChatMessageCell chatMessageCell, TL_pollAnswer tL_pollAnswer) {
                        ChatMessageCell$ChatMessageCellDelegate$$CC.didPressVoteButton(this, chatMessageCell, tL_pollAnswer);
                    }

                    public boolean isChatAdminCell(int i) {
                        return ChatMessageCell$ChatMessageCellDelegate$$CC.isChatAdminCell(this, i);
                    }

                    public void needOpenWebView(String str, String str2, String str3, String str4, int i, int i2) {
                        ChatMessageCell$ChatMessageCellDelegate$$CC.needOpenWebView(this, str, str2, str3, str4, i, i2);
                    }

                    public boolean needPlayMessage(MessageObject messageObject) {
                        return ChatMessageCell$ChatMessageCellDelegate$$CC.needPlayMessage(this, messageObject);
                    }

                    public void videoTimerReached() {
                        ChatMessageCell$ChatMessageCellDelegate$$CC.videoTimerReached(this);
                    }
                });
            } else if (viewType == 1) {
                view = new ChatActionCell(this.mContext);
                ((ChatActionCell) view).setDelegate(new ChatActionCellDelegate() {
                    public void didClickedImage(ChatActionCell chatActionCell) {
                        ChatActionCell$ChatActionCellDelegate$$CC.didClickedImage(this, chatActionCell);
                    }

                    public void didLongPressed(ChatActionCell chatActionCell) {
                        ChatActionCell$ChatActionCellDelegate$$CC.didLongPressed(this, chatActionCell);
                    }

                    public void didPressedBotButton(MessageObject messageObject, KeyboardButton keyboardButton) {
                        ChatActionCell$ChatActionCellDelegate$$CC.didPressedBotButton(this, messageObject, keyboardButton);
                    }

                    public void didPressedReplyMessage(ChatActionCell chatActionCell, int i) {
                        ChatActionCell$ChatActionCellDelegate$$CC.didPressedReplyMessage(this, chatActionCell, i);
                    }

                    public void needOpenUserProfile(int i) {
                        ChatActionCell$ChatActionCellDelegate$$CC.needOpenUserProfile(this, i);
                    }
                });
            }
            view.setLayoutParams(new LayoutParams(-1, -2));
            return new Holder(view);
        }

        public int getItemViewType(int i) {
            if (i < 0 || i >= this.messages.size()) {
                return 4;
            }
            return ((MessageObject) this.messages.get(i)).contentType;
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            MessageObject message = (MessageObject) this.messages.get(position);
            View view = holder.itemView;
            if (view instanceof ChatMessageCell) {
                boolean pinnedBotton;
                boolean pinnedTop;
                ChatMessageCell messageCell = (ChatMessageCell) view;
                messageCell.isChat = false;
                int nextType = getItemViewType(position - 1);
                int prevType = getItemViewType(position + 1);
                if ((message.messageOwner.reply_markup instanceof TL_replyInlineMarkup) || nextType != holder.getItemViewType()) {
                    pinnedBotton = false;
                } else {
                    MessageObject nextMessage = (MessageObject) this.messages.get(position - 1);
                    pinnedBotton = nextMessage.isOutOwner() == message.isOutOwner() && Math.abs(nextMessage.messageOwner.date - message.messageOwner.date) <= 300;
                }
                if (prevType == holder.getItemViewType()) {
                    MessageObject prevMessage = (MessageObject) this.messages.get(position + 1);
                    pinnedTop = !(prevMessage.messageOwner.reply_markup instanceof TL_replyInlineMarkup) && prevMessage.isOutOwner() == message.isOutOwner() && Math.abs(prevMessage.messageOwner.date - message.messageOwner.date) <= 300;
                } else {
                    pinnedTop = false;
                }
                messageCell.setFullyDraw(true);
                messageCell.setMessageObject(message, null, pinnedBotton, pinnedTop);
            } else if (view instanceof ChatActionCell) {
                ChatActionCell actionCell = (ChatActionCell) view;
                actionCell.setMessageObject(message);
                actionCell.setAlpha(1.0f);
            }
        }
    }

    private class PatternCell extends BackupImageView implements FileDownloadProgressListener {
        private int TAG;
        private TL_wallPaper currentPattern;
        private RadialProgress2 radialProgress;
        private RectF rect = new RectF();
        private boolean wasSelected;

        public PatternCell(Context context) {
            super(context);
            setRoundRadius(AndroidUtilities.dp(6.0f));
            this.radialProgress = new RadialProgress2(this);
            this.radialProgress.setProgressRect(AndroidUtilities.dp(30.0f), AndroidUtilities.dp(30.0f), AndroidUtilities.dp(70.0f), AndroidUtilities.dp(70.0f));
            this.TAG = DownloadController.getInstance(WallpaperActivity.this.currentAccount).generateObserverTag();
        }

        private void setPattern(TL_wallPaper wallPaper) {
            this.currentPattern = wallPaper;
            if (wallPaper != null) {
                setImage(FileLoader.getClosestPhotoSizeWithSize(wallPaper.document.thumbs, 100), "100_100", null, null, "jpg", 0, 1, wallPaper);
            } else {
                setImageDrawable(null);
            }
            updateSelected(false);
        }

        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            updateSelected(false);
        }

        public void updateSelected(boolean animated) {
            boolean isSelected;
            if (!(this.currentPattern == null && WallpaperActivity.this.selectedPattern == null) && (WallpaperActivity.this.selectedPattern == null || this.currentPattern == null || this.currentPattern.id != WallpaperActivity.this.selectedPattern.id)) {
                isSelected = false;
            } else {
                isSelected = true;
            }
            if (isSelected) {
                WallpaperActivity.this.updateButtonState(this.radialProgress, WallpaperActivity.this.selectedPattern, this, false, animated);
            } else {
                this.radialProgress.setIcon(4, false, animated);
            }
            invalidate();
        }

        protected void onDraw(Canvas canvas) {
            getImageReceiver().setAlpha(0.8f);
            WallpaperActivity.this.backgroundPaint.setColor(WallpaperActivity.this.backgroundColor);
            this.rect.set(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight());
            canvas.drawRoundRect(this.rect, (float) AndroidUtilities.dp(6.0f), (float) AndroidUtilities.dp(6.0f), WallpaperActivity.this.backgroundPaint);
            super.onDraw(canvas);
            this.radialProgress.setColors(WallpaperActivity.this.patternColor, WallpaperActivity.this.patternColor, -1, -1);
            this.radialProgress.draw(canvas);
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            setMeasuredDimension(AndroidUtilities.dp(100.0f), AndroidUtilities.dp(100.0f));
        }

        public void onFailedDownload(String fileName, boolean canceled) {
            WallpaperActivity.this.updateButtonState(this.radialProgress, this.currentPattern, this, true, canceled);
        }

        public void onSuccessDownload(String fileName) {
            this.radialProgress.setProgress(1.0f, WallpaperActivity.this.progressVisible);
            WallpaperActivity.this.updateButtonState(this.radialProgress, this.currentPattern, this, false, true);
        }

        public void onProgressDownload(String fileName, float progress) {
            this.radialProgress.setProgress(progress, WallpaperActivity.this.progressVisible);
            if (this.radialProgress.getIcon() != 10) {
                WallpaperActivity.this.updateButtonState(this.radialProgress, this.currentPattern, this, false, true);
            }
        }

        public void onProgressUpload(String fileName, float progress, boolean isEncrypted) {
        }

        public int getObserverTag() {
            return this.TAG;
        }
    }

    private class PatternsAdapter extends SelectionAdapter {
        private Context mContext;

        public PatternsAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemViewType(int position) {
            return super.getItemViewType(position);
        }

        public int getItemCount() {
            return (WallpaperActivity.this.patterns != null ? WallpaperActivity.this.patterns.size() : 0) + 1;
        }

        public boolean isEnabled(ViewHolder holder) {
            return false;
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new Holder(new PatternCell(this.mContext));
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            PatternCell view = holder.itemView;
            if (position == 0) {
                view.setPattern(null);
            } else {
                view.setPattern((TL_wallPaper) WallpaperActivity.this.patterns.get(position - 1));
            }
            view.getImageReceiver().setColorFilter(new PorterDuffColorFilter(WallpaperActivity.this.patternColor, WallpaperActivity.this.blendMode));
        }
    }

    public interface WallpaperActivityDelegate {
        void didSetNewBackground();
    }

    public WallpaperActivity(Object wallPaper, Bitmap bitmap) {
        this.currentWallpaper = wallPaper;
        this.currentWallpaperBitmap = bitmap;
        if (this.currentWallpaper instanceof TL_wallPaper) {
            TL_wallPaper tL_wallPaper = (TL_wallPaper) this.currentWallpaper;
        } else if (this.currentWallpaper instanceof ColorWallpaper) {
            ColorWallpaper object = this.currentWallpaper;
            this.isMotion = object.motion;
            this.selectedPattern = object.pattern;
            if (this.selectedPattern != null) {
                this.currentIntensity = object.intensity;
            }
        }
    }

    public void setInitialModes(boolean blur, boolean motion) {
        this.isBlurred = blur;
        this.isMotion = motion;
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        this.imageFilter = ((int) (1080.0f / AndroidUtilities.density)) + "_" + ((int) (1920.0f / AndroidUtilities.density)) + "_f";
        this.maxWallpaperSize = Math.min(1920, Math.max(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y));
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.wallpapersNeedReload);
        this.TAG = DownloadController.getInstance(this.currentAccount).generateObserverTag();
        this.textPaint = new TextPaint(1);
        this.textPaint.setColor(-1);
        this.textPaint.setTextSize((float) AndroidUtilities.dp(14.0f));
        this.textPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.checkPaint = new Paint(1);
        this.checkPaint.setStyle(Style.STROKE);
        this.checkPaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        this.checkPaint.setColor(0);
        this.checkPaint.setStrokeCap(Cap.ROUND);
        this.checkPaint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
        this.eraserPaint = new Paint(1);
        this.eraserPaint.setColor(0);
        this.eraserPaint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
        this.backgroundPaint = new Paint(1);
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        if (this.blurredBitmap != null) {
            this.blurredBitmap.recycle();
            this.blurredBitmap = null;
        }
        Theme.applyChatServiceMessageColor();
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.wallpapersNeedReload);
    }

    public View createView(Context context) {
        int textsCount;
        int startIndex;
        boolean buttonsAvailable;
        int a;
        int num;
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("BackgroundPreview", R.string.BackgroundPreview));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    WallpaperActivity.this.lambda$checkDiscard$70$PassportActivity();
                } else if (id == 1 && WallpaperActivity.this.getParentActivity() != null) {
                    String link;
                    StringBuilder modes = new StringBuilder();
                    if (WallpaperActivity.this.isBlurred) {
                        modes.append("blur");
                    }
                    if (WallpaperActivity.this.isMotion) {
                        if (modes.length() > 0) {
                            modes.append("+");
                        }
                        modes.append("motion");
                    }
                    if (WallpaperActivity.this.currentWallpaper instanceof TL_wallPaper) {
                        link = "https://" + MessagesController.getInstance(WallpaperActivity.this.currentAccount).linkPrefix + "/bg/" + ((TL_wallPaper) WallpaperActivity.this.currentWallpaper).slug;
                        if (modes.length() > 0) {
                            link = link + "?mode=" + modes.toString();
                        }
                    } else if (WallpaperActivity.this.currentWallpaper instanceof ColorWallpaper) {
                        ColorWallpaper colorWallpaper = (ColorWallpaper) WallpaperActivity.this.currentWallpaper;
                        String color = String.format("%02x%02x%02x", new Object[]{Integer.valueOf(((byte) (WallpaperActivity.this.backgroundColor >> 16)) & 255), Integer.valueOf(((byte) (WallpaperActivity.this.backgroundColor >> 8)) & 255), Byte.valueOf((byte) (WallpaperActivity.this.backgroundColor & 255))}).toLowerCase();
                        if (WallpaperActivity.this.selectedPattern != null) {
                            link = "https://" + MessagesController.getInstance(WallpaperActivity.this.currentAccount).linkPrefix + "/bg/" + WallpaperActivity.this.selectedPattern.slug + "?intensity=" + ((int) (WallpaperActivity.this.currentIntensity * 100.0f)) + "&bg_color=" + color;
                            if (modes.length() > 0) {
                                link = link + "&mode=" + modes.toString();
                            }
                        } else {
                            link = "https://" + MessagesController.getInstance(WallpaperActivity.this.currentAccount).linkPrefix + "/bg/" + color;
                        }
                    } else {
                        return;
                    }
                    WallpaperActivity.this.showDialog(new ShareAlert(WallpaperActivity.this.getParentActivity(), null, link, false, link, false));
                }
            }
        });
        if ((this.currentWallpaper instanceof ColorWallpaper) || (this.currentWallpaper instanceof TL_wallPaper)) {
            this.actionBar.createMenu().addItem(1, (int) R.drawable.ic_share_video);
        }
        FrameLayout frameLayout = new FrameLayout(context);
        this.fragmentView = frameLayout;
        this.hasOwnBackground = true;
        this.backgroundImage = new BackupImageView(context) {
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                WallpaperActivity.this.parallaxScale = WallpaperActivity.this.parallaxEffect.getScale(getMeasuredWidth(), getMeasuredHeight());
                if (WallpaperActivity.this.isMotion) {
                    setScaleX(WallpaperActivity.this.parallaxScale);
                    setScaleY(WallpaperActivity.this.parallaxScale);
                }
                if (WallpaperActivity.this.radialProgress != null) {
                    int size = AndroidUtilities.dp(44.0f);
                    int x = (getMeasuredWidth() - size) / 2;
                    int y = (getMeasuredHeight() - size) / 2;
                    WallpaperActivity.this.radialProgress.setProgressRect(x, y, x + size, y + size);
                }
                WallpaperActivity.this.progressVisible = getMeasuredWidth() <= getMeasuredHeight();
            }

            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                if (WallpaperActivity.this.progressVisible && WallpaperActivity.this.radialProgress != null) {
                    WallpaperActivity.this.radialProgress.draw(canvas);
                }
            }

            public void setAlpha(float alpha) {
                WallpaperActivity.this.radialProgress.setOverrideAlpha(alpha);
            }
        };
        if (this.currentWallpaper instanceof ColorWallpaper) {
            textsCount = 3;
            startIndex = this.patterns != null ? 0 : 2;
            buttonsAvailable = (this.patterns == null && this.selectedPattern == null) ? false : true;
        } else {
            textsCount = 2;
            startIndex = 0;
            buttonsAvailable = true;
        }
        frameLayout.addView(this.backgroundImage, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, 48.0f));
        this.backgroundImage.getImageReceiver().setDelegate(new WallpaperActivity$$Lambda$0(this));
        this.radialProgress = new RadialProgress2(this.backgroundImage);
        this.radialProgress.setColors("chat_serviceBackground", "chat_serviceBackground", "chat_serviceText", "chat_serviceText");
        this.listView = new RecyclerListView(context);
        this.listView.setLayoutManager(new LinearLayoutManager(context, 1, true));
        this.listView.setOverScrollMode(2);
        this.listView.setAdapter(new ListAdapter(context));
        this.listView.setPadding(0, 0, 0, AndroidUtilities.dp(buttonsAvailable ? 64.0f : 4.0f));
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, 48.0f));
        this.bottomOverlayChat = new FrameLayout(context) {
            public void onDraw(Canvas canvas) {
                int bottom = Theme.chat_composeShadowDrawable.getIntrinsicHeight();
                Theme.chat_composeShadowDrawable.setBounds(0, 0, getMeasuredWidth(), bottom);
                Theme.chat_composeShadowDrawable.draw(canvas);
                canvas.drawRect(0.0f, (float) bottom, (float) getMeasuredWidth(), (float) getMeasuredHeight(), Theme.chat_composeBackgroundPaint);
            }
        };
        this.bottomOverlayChat.setWillNotDraw(false);
        this.bottomOverlayChat.setPadding(0, AndroidUtilities.dp(3.0f), 0, 0);
        frameLayout.addView(this.bottomOverlayChat, LayoutHelper.createFrame(-1, 51, 80));
        this.bottomOverlayChat.setOnClickListener(new WallpaperActivity$$Lambda$1(this));
        this.bottomOverlayChatText = new TextView(context);
        this.bottomOverlayChatText.setTextSize(1, 15.0f);
        this.bottomOverlayChatText.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.bottomOverlayChatText.setTextColor(Theme.getColor("chat_fieldOverlayText"));
        this.bottomOverlayChatText.setText(LocaleController.getString("SetBackground", R.string.SetBackground));
        this.bottomOverlayChat.addView(this.bottomOverlayChatText, LayoutHelper.createFrame(-2, -2, 17));
        this.buttonsContainer = new FrameLayout(context);
        frameLayout.addView(this.buttonsContainer, LayoutHelper.createFrame(-2, 32.0f, 81, 0.0f, 0.0f, 0.0f, 66.0f));
        String[] texts = new String[textsCount];
        int[] textSizes = new int[textsCount];
        this.checkBoxView = new CheckBoxView[textsCount];
        if (this.currentWallpaper instanceof ColorWallpaper) {
            texts[0] = LocaleController.getString("BackgroundColor", R.string.BackgroundColor);
            texts[1] = LocaleController.getString("BackgroundPattern", R.string.BackgroundPattern);
            texts[2] = LocaleController.getString("BackgroundMotion", R.string.BackgroundMotion);
        } else {
            texts[0] = LocaleController.getString("BackgroundBlurred", R.string.BackgroundBlurred);
            texts[1] = LocaleController.getString("BackgroundMotion", R.string.BackgroundMotion);
        }
        int maxTextSize = 0;
        for (a = 0; a < texts.length; a++) {
            textSizes[a] = (int) Math.ceil((double) this.textPaint.measureText(texts[a]));
            maxTextSize = Math.max(maxTextSize, textSizes[a]);
        }
        a = startIndex;
        while (a < textsCount) {
            num = a;
            CheckBoxView[] checkBoxViewArr = this.checkBoxView;
            boolean z = ((this.currentWallpaper instanceof ColorWallpaper) && a == 0) ? false : true;
            checkBoxViewArr[a] = new CheckBoxView(context, z);
            this.checkBoxView[a].setText(texts[a], textSizes[a], maxTextSize);
            if (!(this.currentWallpaper instanceof ColorWallpaper)) {
                this.checkBoxView[a].setChecked(a == 0 ? this.isBlurred : this.isMotion, false);
            } else if (a == 1) {
                this.checkBoxView[a].setChecked(this.selectedPattern != null, false);
            } else if (a == 2) {
                this.checkBoxView[a].setChecked(this.isMotion, false);
            }
            int width = maxTextSize + AndroidUtilities.dp(56.0f);
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-2, width);
            layoutParams.gravity = 51;
            layoutParams.leftMargin = a == 1 ? AndroidUtilities.dp(9.0f) + width : 0;
            this.buttonsContainer.addView(this.checkBoxView[a], layoutParams);
            this.checkBoxView[a].setOnClickListener(new WallpaperActivity$$Lambda$2(this, num, this.checkBoxView[a]));
            if (startIndex == 0 && a == 2) {
                this.checkBoxView[a].setAlpha(0.0f);
                this.checkBoxView[a].setVisibility(4);
            }
            a++;
        }
        if (!buttonsAvailable) {
            this.buttonsContainer.setVisibility(8);
        }
        this.parallaxEffect = new WallpaperParallaxEffect(context);
        this.parallaxEffect.setCallback(new WallpaperActivity$$Lambda$3(this));
        if (this.currentWallpaper instanceof ColorWallpaper) {
            this.isBlurred = false;
            a = 0;
            while (a < 2) {
                num = a;
                this.patternLayout[a] = new FrameLayout(context) {
                    public void onDraw(Canvas canvas) {
                        int bottom = Theme.chat_composeShadowDrawable.getIntrinsicHeight();
                        Theme.chat_composeShadowDrawable.setBounds(0, 0, getMeasuredWidth(), bottom);
                        Theme.chat_composeShadowDrawable.draw(canvas);
                        canvas.drawRect(0.0f, (float) bottom, (float) getMeasuredWidth(), (float) getMeasuredHeight(), Theme.chat_composeBackgroundPaint);
                    }
                };
                this.patternLayout[a].setVisibility(4);
                this.patternLayout[a].setWillNotDraw(false);
                frameLayout.addView(this.patternLayout[a], LayoutHelper.createFrame(-1, a == 0 ? 390 : 242, 83));
                this.patternsButtonsContainer[a] = new FrameLayout(context) {
                    public void onDraw(Canvas canvas) {
                        int bottom = Theme.chat_composeShadowDrawable.getIntrinsicHeight();
                        Theme.chat_composeShadowDrawable.setBounds(0, 0, getMeasuredWidth(), bottom);
                        Theme.chat_composeShadowDrawable.draw(canvas);
                        canvas.drawRect(0.0f, (float) bottom, (float) getMeasuredWidth(), (float) getMeasuredHeight(), Theme.chat_composeBackgroundPaint);
                    }
                };
                this.patternsButtonsContainer[a].setWillNotDraw(false);
                this.patternsButtonsContainer[a].setPadding(0, AndroidUtilities.dp(3.0f), 0, 0);
                this.patternLayout[a].addView(this.patternsButtonsContainer[a], LayoutHelper.createFrame(-1, 51, 80));
                this.patternsCancelButton[a] = new TextView(context);
                this.patternsCancelButton[a].setTextSize(1, 15.0f);
                this.patternsCancelButton[a].setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                this.patternsCancelButton[a].setTextColor(Theme.getColor("chat_fieldOverlayText"));
                this.patternsCancelButton[a].setText(LocaleController.getString("Cancel", R.string.Cancel).toUpperCase());
                this.patternsCancelButton[a].setGravity(17);
                this.patternsCancelButton[a].setPadding(AndroidUtilities.dp(21.0f), 0, AndroidUtilities.dp(21.0f), 0);
                this.patternsCancelButton[a].setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("listSelectorSDK21"), 0));
                this.patternsButtonsContainer[a].addView(this.patternsCancelButton[a], LayoutHelper.createFrame(-2, -1, 51));
                this.patternsCancelButton[a].setOnClickListener(new WallpaperActivity$$Lambda$4(this, num));
                this.patternsSaveButton[a] = new TextView(context);
                this.patternsSaveButton[a].setTextSize(1, 15.0f);
                this.patternsSaveButton[a].setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                this.patternsSaveButton[a].setTextColor(Theme.getColor("chat_fieldOverlayText"));
                this.patternsSaveButton[a].setText(LocaleController.getString("Save", R.string.Save).toUpperCase());
                this.patternsSaveButton[a].setGravity(17);
                this.patternsSaveButton[a].setPadding(AndroidUtilities.dp(21.0f), 0, AndroidUtilities.dp(21.0f), 0);
                this.patternsSaveButton[a].setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("listSelectorSDK21"), 0));
                this.patternsButtonsContainer[a].addView(this.patternsSaveButton[a], LayoutHelper.createFrame(-2, -1, 53));
                this.patternsSaveButton[a].setOnClickListener(new WallpaperActivity$$Lambda$5(this, num));
                if (a == 1) {
                    this.patternsListView = new RecyclerListView(context) {
                        public boolean onTouchEvent(MotionEvent event) {
                            if (event.getAction() == 0) {
                                getParent().requestDisallowInterceptTouchEvent(true);
                            }
                            return super.onTouchEvent(event);
                        }
                    };
                    RecyclerListView recyclerListView = this.patternsListView;
                    LayoutManager linearLayoutManager = new LinearLayoutManager(context, 0, false);
                    this.patternsLayoutManager = linearLayoutManager;
                    recyclerListView.setLayoutManager(linearLayoutManager);
                    recyclerListView = this.patternsListView;
                    Adapter patternsAdapter = new PatternsAdapter(context);
                    this.patternsAdapter = patternsAdapter;
                    recyclerListView.setAdapter(patternsAdapter);
                    this.patternsListView.addItemDecoration(new ItemDecoration() {
                        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state) {
                            int position = parent.getChildAdapterPosition(view);
                            outRect.left = AndroidUtilities.dp(12.0f);
                            outRect.top = 0;
                            outRect.bottom = 0;
                            if (position == state.getItemCount() - 1) {
                                outRect.right = AndroidUtilities.dp(12.0f);
                            }
                        }
                    });
                    this.patternLayout[a].addView(this.patternsListView, LayoutHelper.createFrame(-1, 100.0f, 51, 0.0f, 14.0f, 0.0f, 0.0f));
                    this.patternsListView.setOnItemClickListener(new WallpaperActivity$$Lambda$6(this));
                    this.intensityCell = new HeaderCell(context);
                    this.intensityCell.setText(LocaleController.getString("BackgroundIntensity", R.string.BackgroundIntensity));
                    this.patternLayout[a].addView(this.intensityCell, LayoutHelper.createFrame(-1, -2.0f, 51, 0.0f, 113.0f, 0.0f, 0.0f));
                    this.intensitySeekBar = new SeekBarView(context) {
                        public boolean onTouchEvent(MotionEvent event) {
                            if (event.getAction() == 0) {
                                getParent().requestDisallowInterceptTouchEvent(true);
                            }
                            return super.onTouchEvent(event);
                        }
                    };
                    this.intensitySeekBar.setProgress(this.currentIntensity);
                    this.intensitySeekBar.setReportChanges(true);
                    this.intensitySeekBar.setDelegate(new WallpaperActivity$$Lambda$7(this));
                    this.patternLayout[a].addView(this.intensitySeekBar, LayoutHelper.createFrame(-1, 30.0f, 51, 9.0f, 153.0f, 9.0f, 0.0f));
                } else {
                    this.colorPicker = new ColorPicker(context);
                    this.patternLayout[a].addView(this.colorPicker, LayoutHelper.createFrame(-1, -1.0f, 1, 0.0f, 0.0f, 0.0f, 48.0f));
                }
                a++;
            }
        }
        setCurrentImage(true);
        updateButtonState(this.radialProgress, null, this, false, false);
        if (!this.backgroundImage.getImageReceiver().hasBitmapImage()) {
            this.fragmentView.setBackgroundColor(-16777216);
        }
        if (!(this.currentWallpaper instanceof ColorWallpaper)) {
            this.backgroundImage.getImageReceiver().setCrossfadeWithOldImage(true);
            this.backgroundImage.getImageReceiver().setForceCrossfade(true);
        }
        return this.fragmentView;
    }

    final /* synthetic */ void lambda$createView$0$WallpaperActivity(ImageReceiver imageReceiver, boolean set, boolean thumb) {
        if (!(this.currentWallpaper instanceof ColorWallpaper)) {
            Drawable drawable = imageReceiver.getDrawable();
            if (set && drawable != null) {
                Theme.applyChatServiceMessageColor(AndroidUtilities.calcDrawableColor(drawable));
                this.listView.invalidateViews();
                int N = this.buttonsContainer.getChildCount();
                for (int a = 0; a < N; a++) {
                    this.buttonsContainer.getChildAt(a).invalidate();
                }
                if (this.radialProgress != null) {
                    this.radialProgress.setColors("chat_serviceBackground", "chat_serviceBackground", "chat_serviceText", "chat_serviceText");
                }
                if (!thumb && this.isBlurred && this.blurredBitmap == null) {
                    this.backgroundImage.getImageReceiver().setCrossfadeWithOldImage(false);
                    updateBlurred();
                    this.backgroundImage.getImageReceiver().setCrossfadeWithOldImage(true);
                }
            }
        }
    }

    final /* synthetic */ void lambda$createView$1$WallpaperActivity(View view) {
        boolean done;
        ColorWallpaper wallPaper;
        long id;
        boolean sameFile = false;
        File file = new File(ApplicationLoader.getFilesDirFixed(), this.isBlurred ? "wallpaper_original.jpg" : "wallpaper.jpg");
        Bitmap bitmap;
        OutputStream stream;
        if (this.currentWallpaper instanceof TL_wallPaper) {
            try {
                bitmap = this.backgroundImage.getImageReceiver().getBitmap();
                stream = new FileOutputStream(file);
                bitmap.compress(CompressFormat.JPEG, 87, stream);
                stream.close();
                done = true;
            } catch (Throwable e) {
                done = false;
                FileLog.e(e);
            }
            if (!done) {
                try {
                    done = AndroidUtilities.copyFile(FileLoader.getPathToAttach(this.currentWallpaper.document, true), file);
                } catch (Throwable e2) {
                    done = false;
                    FileLog.e(e2);
                }
            }
        } else if (this.currentWallpaper instanceof ColorWallpaper) {
            if (this.selectedPattern != null) {
                try {
                    wallPaper = this.currentWallpaper;
                    bitmap = this.backgroundImage.getImageReceiver().getBitmap();
                    Bitmap dst = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
                    Canvas canvas = new Canvas(dst);
                    canvas.drawColor(this.backgroundColor);
                    Paint paint = new Paint(2);
                    paint.setColorFilter(new PorterDuffColorFilter(this.patternColor, this.blendMode));
                    paint.setAlpha((int) (255.0f * this.currentIntensity));
                    canvas.drawBitmap(bitmap, 0.0f, 0.0f, paint);
                    stream = new FileOutputStream(file);
                    dst.compress(CompressFormat.JPEG, 87, stream);
                    stream.close();
                    done = true;
                } catch (Throwable e22) {
                    FileLog.e(e22);
                    done = false;
                }
            } else {
                done = true;
            }
        } else if (this.currentWallpaper instanceof FileWallpaper) {
            FileWallpaper wallpaper = this.currentWallpaper;
            if (wallpaper.resId != 0 || ((long) wallpaper.resId) == -2) {
                done = true;
            } else {
                try {
                    File fromFile = wallpaper.originalPath != null ? wallpaper.originalPath : wallpaper.path;
                    sameFile = fromFile.equals(file);
                    if (sameFile) {
                        done = true;
                    } else {
                        done = AndroidUtilities.copyFile(fromFile, file);
                    }
                } catch (Throwable e222) {
                    done = false;
                    FileLog.e(e222);
                }
            }
        } else if (this.currentWallpaper instanceof SearchImage) {
            File f;
            SearchImage wallpaper2 = this.currentWallpaper;
            if (wallpaper2.photo != null) {
                f = FileLoader.getPathToAttach(FileLoader.getClosestPhotoSizeWithSize(wallpaper2.photo.sizes, this.maxWallpaperSize, true), true);
            } else {
                f = ImageLoader.getHttpFilePath(wallpaper2.imageUrl, "jpg");
            }
            try {
                done = AndroidUtilities.copyFile(f, file);
            } catch (Throwable e2222) {
                done = false;
                FileLog.e(e2222);
            }
        } else {
            done = false;
        }
        if (this.isBlurred) {
            try {
                OutputStream fileOutputStream = new FileOutputStream(new File(ApplicationLoader.getFilesDirFixed(), "wallpaper.jpg"));
                this.blurredBitmap.compress(CompressFormat.JPEG, 87, fileOutputStream);
                fileOutputStream.close();
                done = true;
            } catch (Throwable e22222) {
                FileLog.e(e22222);
                done = false;
            }
        }
        long saveId = 0;
        long access_hash = 0;
        int color = 0;
        long pattern = 0;
        File path = null;
        if (this.currentWallpaper instanceof TL_wallPaper) {
            TL_wallPaper wallPaper2 = (TL_wallPaper) this.currentWallpaper;
            id = wallPaper2.id;
            saveId = id;
            access_hash = wallPaper2.access_hash;
        } else if (this.currentWallpaper instanceof ColorWallpaper) {
            wallPaper = (ColorWallpaper) this.currentWallpaper;
            if (this.selectedPattern != null) {
                saveId = this.selectedPattern.id;
                access_hash = this.selectedPattern.access_hash;
                if (wallPaper.id == wallPaper.patternId && this.backgroundColor == wallPaper.color && wallPaper.intensity - this.currentIntensity <= 0.001f) {
                    id = this.selectedPattern.id;
                } else {
                    id = -1;
                }
                pattern = this.selectedPattern.id;
            } else {
                id = -1;
            }
            color = this.backgroundColor;
        } else if (this.currentWallpaper instanceof FileWallpaper) {
            FileWallpaper wallPaper3 = this.currentWallpaper;
            id = wallPaper3.id;
            path = wallPaper3.path;
        } else if (this.currentWallpaper instanceof SearchImage) {
            SearchImage wallPaper4 = this.currentWallpaper;
            if (wallPaper4.photo != null) {
                path = FileLoader.getPathToAttach(FileLoader.getClosestPhotoSizeWithSize(wallPaper4.photo.sizes, this.maxWallpaperSize, true), true);
            } else {
                path = ImageLoader.getHttpFilePath(wallPaper4.imageUrl, "jpg");
            }
            id = -1;
        } else {
            id = 0;
            color = 0;
        }
        MessagesController.getInstance(this.currentAccount).saveWallpaperToServer(path, saveId, access_hash, this.isBlurred, this.isMotion, color, this.currentIntensity, access_hash != 0, 0);
        if (done) {
            Theme.serviceMessageColorBackup = Theme.getColor("chat_serviceBackground");
            Editor editor = MessagesController.getGlobalMainSettings().edit();
            editor.putLong("selectedBackground2", id);
            editor.putBoolean("selectedBackgroundBlurred", this.isBlurred);
            editor.putBoolean("selectedBackgroundMotion", this.isMotion);
            editor.putInt("selectedColor", color);
            editor.putFloat("selectedIntensity", this.currentIntensity);
            editor.putLong("selectedPattern", pattern);
            editor.putBoolean("overrideThemeWallpaper", id != -2);
            editor.commit();
            Theme.reloadWallpaper();
            if (!sameFile) {
                ImageLoader.getInstance().removeImage(ImageLoader.getHttpFileName(file.getAbsolutePath()) + "@100_100");
            }
        }
        if (this.delegate != null) {
            this.delegate.didSetNewBackground();
        }
        lambda$checkDiscard$70$PassportActivity();
    }

    final /* synthetic */ void lambda$createView$2$WallpaperActivity(int num, CheckBoxView view, View v) {
        boolean z = false;
        boolean z2 = true;
        if (this.buttonsContainer.getAlpha() == 1.0f) {
            if (!(this.currentWallpaper instanceof ColorWallpaper)) {
                if (!view.isChecked()) {
                    z = true;
                }
                view.setChecked(z, true);
                if (num == 0) {
                    this.isBlurred = view.isChecked();
                    updateBlurred();
                    return;
                }
                this.isMotion = view.isChecked();
                this.parallaxEffect.setEnabled(this.isMotion);
                animateMotionChange();
            } else if (num == 2) {
                if (!view.isChecked()) {
                    z = true;
                }
                view.setChecked(z, true);
                this.isMotion = view.isChecked();
                this.parallaxEffect.setEnabled(this.isMotion);
                animateMotionChange();
            } else {
                if (num == 1 && this.patternLayout[num].getVisibility() == 0) {
                    this.backgroundImage.setImageDrawable(null);
                    this.selectedPattern = null;
                    this.isMotion = false;
                    updateButtonState(this.radialProgress, null, this, false, true);
                    updateSelectedPattern(true);
                    this.checkBoxView[1].setChecked(false, true);
                    this.patternsListView.invalidateViews();
                }
                if (this.patternLayout[num].getVisibility() == 0) {
                    z2 = false;
                }
                showPatternsView(num, z2);
            }
        }
    }

    final /* synthetic */ void lambda$createView$3$WallpaperActivity(int offsetX, int offsetY) {
        if (this.isMotion) {
            float progress;
            if (this.motionAnimation != null) {
                progress = (this.backgroundImage.getScaleX() - 1.0f) / (this.parallaxScale - 1.0f);
            } else {
                progress = 1.0f;
            }
            this.backgroundImage.setTranslationX(((float) offsetX) * progress);
            this.backgroundImage.setTranslationY(((float) offsetY) * progress);
        }
    }

    final /* synthetic */ void lambda$createView$4$WallpaperActivity(int num, View v) {
        if (num == 0) {
            setBackgroundColor(this.previousBackgroundColor);
        } else {
            boolean z;
            this.selectedPattern = this.previousSelectedPattern;
            if (this.selectedPattern == null) {
                this.backgroundImage.setImageDrawable(null);
            } else {
                this.backgroundImage.setImage(this.selectedPattern.document, this.imageFilter, null, null, "jpg", this.selectedPattern.document.size, 1, this.selectedPattern);
            }
            CheckBoxView checkBoxView = this.checkBoxView[1];
            if (this.selectedPattern != null) {
                z = true;
            } else {
                z = false;
            }
            checkBoxView.setChecked(z, false);
            this.currentIntensity = this.previousIntensity;
            this.intensitySeekBar.setProgress(this.currentIntensity);
            this.backgroundImage.getImageReceiver().setAlpha(this.currentIntensity);
            updateButtonState(this.radialProgress, null, this, false, true);
            updateSelectedPattern(true);
        }
        showPatternsView(num, false);
    }

    final /* synthetic */ void lambda$createView$5$WallpaperActivity(int num, View v) {
        showPatternsView(num, false);
    }

    final /* synthetic */ void lambda$createView$6$WallpaperActivity(View view, int position) {
        boolean previousMotion = this.selectedPattern != null;
        if (position == 0) {
            this.backgroundImage.setImageDrawable(null);
            this.selectedPattern = null;
            this.isMotion = false;
            updateButtonState(this.radialProgress, null, this, false, true);
        } else {
            TL_wallPaper wallPaper = (TL_wallPaper) this.patterns.get(position - 1);
            this.backgroundImage.setImage(wallPaper.document, this.imageFilter, null, null, "jpg", wallPaper.document.size, 1, wallPaper);
            this.selectedPattern = wallPaper;
            this.isMotion = this.checkBoxView[2].isChecked();
            updateButtonState(this.radialProgress, null, this, false, true);
        }
        if (previousMotion == (this.selectedPattern == null)) {
            animateMotionChange();
            updateMotionButton();
        }
        updateSelectedPattern(true);
        this.checkBoxView[1].setChecked(this.selectedPattern != null, true);
        this.patternsListView.invalidateViews();
    }

    final /* synthetic */ void lambda$createView$7$WallpaperActivity(float progress) {
        this.currentIntensity = progress;
        this.backgroundImage.getImageReceiver().setAlpha(this.currentIntensity);
        this.backgroundImage.invalidate();
        this.patternsListView.invalidateViews();
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.wallpapersNeedReload && (this.currentWallpaper instanceof FileWallpaper)) {
            FileWallpaper fileWallpaper = this.currentWallpaper;
            if (fileWallpaper.id == -1) {
                fileWallpaper.id = ((Long) args[0]).longValue();
            }
        }
    }

    public void onResume() {
        super.onResume();
        if (this.isMotion) {
            this.parallaxEffect.setEnabled(true);
        }
    }

    public void onPause() {
        super.onPause();
        if (this.isMotion) {
            this.parallaxEffect.setEnabled(false);
        }
    }

    public void onFailedDownload(String fileName, boolean canceled) {
        updateButtonState(this.radialProgress, null, this, true, canceled);
    }

    public void onSuccessDownload(String fileName) {
        this.radialProgress.setProgress(1.0f, this.progressVisible);
        updateButtonState(this.radialProgress, null, this, false, true);
    }

    public void onProgressDownload(String fileName, float progress) {
        this.radialProgress.setProgress(progress, this.progressVisible);
        if (this.radialProgress.getIcon() != 10) {
            updateButtonState(this.radialProgress, null, this, false, true);
        }
    }

    public void onProgressUpload(String fileName, float progress, boolean isEncrypted) {
    }

    public int getObserverTag() {
        return this.TAG;
    }

    private void updateBlurred() {
        if (this.isBlurred && this.blurredBitmap == null) {
            if (this.currentWallpaperBitmap != null) {
                this.blurredBitmap = Utilities.blurWallpaper(this.currentWallpaperBitmap);
            } else {
                ImageReceiver imageReceiver = this.backgroundImage.getImageReceiver();
                if (imageReceiver.hasNotThumb() || imageReceiver.hasStaticThumb()) {
                    this.blurredBitmap = Utilities.blurWallpaper(imageReceiver.getBitmap());
                }
            }
        }
        if (!this.isBlurred) {
            setCurrentImage(false);
        } else if (this.blurredBitmap != null) {
            this.backgroundImage.setImageBitmap(this.blurredBitmap);
        }
    }

    private void updateButtonState(RadialProgress2 radial, Object image, FileDownloadProgressListener listener, boolean ifSame, boolean animated) {
        TL_wallPaper object;
        if (listener != this) {
            object = image;
        } else if (this.selectedPattern != null) {
            object = this.selectedPattern;
        } else {
            object = this.currentWallpaper;
        }
        if ((object instanceof TL_wallPaper) || (object instanceof SearchImage)) {
            String fileName;
            File path;
            int size;
            if (image == null && animated && !this.progressVisible) {
                animated = false;
            }
            if (object instanceof TL_wallPaper) {
                TL_wallPaper wallPaper = object;
                fileName = FileLoader.getAttachFileName(wallPaper.document);
                if (!TextUtils.isEmpty(fileName)) {
                    path = FileLoader.getPathToAttach(wallPaper.document, true);
                    size = wallPaper.document.size;
                } else {
                    return;
                }
            }
            SearchImage wallPaper2 = (SearchImage) object;
            if (wallPaper2.photo != null) {
                PhotoSize photoSize = FileLoader.getClosestPhotoSizeWithSize(wallPaper2.photo.sizes, this.maxWallpaperSize, true);
                path = FileLoader.getPathToAttach(photoSize, true);
                fileName = FileLoader.getAttachFileName(photoSize);
                size = photoSize.size;
            } else {
                path = ImageLoader.getHttpFilePath(wallPaper2.imageUrl, "jpg");
                fileName = path.getName();
                size = wallPaper2.size;
            }
            if (TextUtils.isEmpty(fileName)) {
                return;
            }
            boolean fileExists = path.exists();
            if (fileExists) {
                DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(listener);
                radial.setProgress(1.0f, animated);
                radial.setIcon(image == null ? 4 : 6, ifSame, animated);
                if (image == null) {
                    this.backgroundImage.invalidate();
                    if (size != 0) {
                        this.actionBar.setSubtitle(AndroidUtilities.formatFileSize((long) size));
                    } else {
                        this.actionBar.setSubtitle(null);
                    }
                }
            } else {
                DownloadController.getInstance(this.currentAccount).addLoadingFileObserver(fileName, null, listener);
                boolean isLoading = FileLoader.getInstance(this.currentAccount).isLoadingFile(fileName);
                Float progress = ImageLoader.getInstance().getFileProgress(fileName);
                if (progress != null) {
                    radial.setProgress(progress.floatValue(), animated);
                } else {
                    radial.setProgress(0.0f, animated);
                }
                radial.setIcon(10, ifSame, animated);
                if (image == null) {
                    this.actionBar.setSubtitle(LocaleController.getString("LoadingFullImage", R.string.LoadingFullImage));
                    this.backgroundImage.invalidate();
                }
            }
            if (image == null) {
                if (this.selectedPattern == null) {
                    this.buttonsContainer.setAlpha(fileExists ? 1.0f : 0.5f);
                }
                this.bottomOverlayChat.setEnabled(fileExists);
                this.bottomOverlayChatText.setAlpha(fileExists ? 1.0f : 0.5f);
                return;
            }
            return;
        }
        radial.setIcon(listener == this ? 4 : 6, ifSame, animated);
    }

    public void setDelegate(WallpaperActivityDelegate wallpaperActivityDelegate) {
        this.delegate = wallpaperActivityDelegate;
    }

    public void setPatterns(ArrayList<Object> arrayList) {
        this.patterns = arrayList;
        if (this.currentWallpaper instanceof ColorWallpaper) {
            ColorWallpaper wallPaper = this.currentWallpaper;
            if (wallPaper.patternId != 0) {
                int N = this.patterns.size();
                for (int a = 0; a < N; a++) {
                    TL_wallPaper pattern = (TL_wallPaper) this.patterns.get(a);
                    if (pattern.id == wallPaper.patternId) {
                        this.selectedPattern = pattern;
                        break;
                    }
                }
                this.currentIntensity = wallPaper.intensity;
            }
        }
    }

    private void updateSelectedPattern(boolean animated) {
        int count = this.patternsListView.getChildCount();
        for (int a = 0; a < count; a++) {
            View child = this.patternsListView.getChildAt(a);
            if (child instanceof PatternCell) {
                ((PatternCell) child).updateSelected(animated);
            }
        }
    }

    private void updateMotionButton() {
        int i;
        float f;
        float f2 = 0.0f;
        CheckBoxView[] checkBoxViewArr = this.checkBoxView;
        if (this.selectedPattern != null) {
            i = 2;
        } else {
            i = 0;
        }
        checkBoxViewArr[i].setVisibility(0);
        AnimatorSet animatorSet = new AnimatorSet();
        Animator[] animatorArr = new Animator[2];
        Object obj = this.checkBoxView[2];
        Property property = View.ALPHA;
        float[] fArr = new float[1];
        if (this.selectedPattern != null) {
            f = 1.0f;
        } else {
            f = 0.0f;
        }
        fArr[0] = f;
        animatorArr[0] = ObjectAnimator.ofFloat(obj, property, fArr);
        Object obj2 = this.checkBoxView[0];
        Property property2 = View.ALPHA;
        float[] fArr2 = new float[1];
        if (this.selectedPattern == null) {
            f2 = 1.0f;
        }
        fArr2[0] = f2;
        animatorArr[1] = ObjectAnimator.ofFloat(obj2, property2, fArr2);
        animatorSet.playTogether(animatorArr);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                WallpaperActivity.this.checkBoxView[WallpaperActivity.this.selectedPattern != null ? 0 : 2].setVisibility(4);
            }
        });
        animatorSet.setInterpolator(CubicBezierInterpolator.EASE_OUT);
        animatorSet.setDuration(200);
        animatorSet.start();
    }

    private void showPatternsView(int num, boolean show) {
        final boolean showMotion = show && num == 1 && this.selectedPattern != null;
        if (show) {
            if (num == 0) {
                this.previousBackgroundColor = this.backgroundColor;
                this.colorPicker.setColor(this.backgroundColor);
            } else {
                this.previousSelectedPattern = this.selectedPattern;
                this.previousIntensity = this.currentIntensity;
                this.patternsAdapter.notifyDataSetChanged();
                if (this.patterns != null) {
                    int index;
                    if (this.selectedPattern == null) {
                        index = 0;
                    } else {
                        index = this.patterns.indexOf(this.selectedPattern) + 1;
                    }
                    this.patternsLayoutManager.scrollToPositionWithOffset(index, ((this.patternsListView.getMeasuredWidth() - AndroidUtilities.dp(100.0f)) - AndroidUtilities.dp(12.0f)) / 2);
                }
            }
        }
        this.checkBoxView[showMotion ? 2 : 0].setVisibility(0);
        AnimatorSet animatorSet = new AnimatorSet();
        ArrayList<Animator> animators = new ArrayList();
        final int otherNum = num == 0 ? 1 : 0;
        if (show) {
            this.patternLayout[num].setVisibility(0);
            animators.add(ObjectAnimator.ofFloat(this.listView, View.TRANSLATION_Y, new float[]{(float) ((-this.patternLayout[num].getMeasuredHeight()) + AndroidUtilities.dp(48.0f))}));
            animators.add(ObjectAnimator.ofFloat(this.buttonsContainer, View.TRANSLATION_Y, new float[]{(float) ((-this.patternLayout[num].getMeasuredHeight()) + AndroidUtilities.dp(48.0f))}));
            Object obj = this.checkBoxView[2];
            Property property = View.ALPHA;
            float[] fArr = new float[1];
            fArr[0] = showMotion ? 1.0f : 0.0f;
            animators.add(ObjectAnimator.ofFloat(obj, property, fArr));
            obj = this.checkBoxView[0];
            property = View.ALPHA;
            fArr = new float[1];
            fArr[0] = showMotion ? 0.0f : 1.0f;
            animators.add(ObjectAnimator.ofFloat(obj, property, fArr));
            animators.add(ObjectAnimator.ofFloat(this.backgroundImage, View.ALPHA, new float[]{0.0f}));
            if (this.patternLayout[otherNum].getVisibility() == 0) {
                animators.add(ObjectAnimator.ofFloat(this.patternLayout[otherNum], View.ALPHA, new float[]{0.0f}));
                animators.add(ObjectAnimator.ofFloat(this.patternLayout[num], View.ALPHA, new float[]{0.0f, 1.0f}));
                this.patternLayout[num].setTranslationY(0.0f);
            } else {
                animators.add(ObjectAnimator.ofFloat(this.patternLayout[num], View.TRANSLATION_Y, new float[]{(float) this.patternLayout[num].getMeasuredHeight(), 0.0f}));
            }
        } else {
            animators.add(ObjectAnimator.ofFloat(this.listView, View.TRANSLATION_Y, new float[]{0.0f}));
            animators.add(ObjectAnimator.ofFloat(this.buttonsContainer, View.TRANSLATION_Y, new float[]{0.0f}));
            animators.add(ObjectAnimator.ofFloat(this.patternLayout[num], View.TRANSLATION_Y, new float[]{(float) this.patternLayout[num].getMeasuredHeight()}));
            animators.add(ObjectAnimator.ofFloat(this.checkBoxView[0], View.ALPHA, new float[]{1.0f}));
            animators.add(ObjectAnimator.ofFloat(this.checkBoxView[2], View.ALPHA, new float[]{0.0f}));
            animators.add(ObjectAnimator.ofFloat(this.backgroundImage, View.ALPHA, new float[]{1.0f}));
        }
        animatorSet.playTogether(animators);
        final boolean z = show;
        final int i = num;
        animatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                if (z && WallpaperActivity.this.patternLayout[otherNum].getVisibility() == 0) {
                    WallpaperActivity.this.patternLayout[otherNum].setAlpha(1.0f);
                    WallpaperActivity.this.patternLayout[otherNum].setVisibility(4);
                } else if (!z) {
                    WallpaperActivity.this.patternLayout[i].setVisibility(4);
                }
                WallpaperActivity.this.checkBoxView[showMotion ? 0 : 2].setVisibility(4);
            }
        });
        animatorSet.setInterpolator(CubicBezierInterpolator.EASE_OUT);
        animatorSet.setDuration(200);
        animatorSet.start();
    }

    private void animateMotionChange() {
        if (this.motionAnimation != null) {
            this.motionAnimation.cancel();
        }
        this.motionAnimation = new AnimatorSet();
        AnimatorSet animatorSet;
        Animator[] animatorArr;
        if (this.isMotion) {
            animatorSet = this.motionAnimation;
            animatorArr = new Animator[2];
            animatorArr[0] = ObjectAnimator.ofFloat(this.backgroundImage, View.SCALE_X, new float[]{this.parallaxScale});
            animatorArr[1] = ObjectAnimator.ofFloat(this.backgroundImage, View.SCALE_Y, new float[]{this.parallaxScale});
            animatorSet.playTogether(animatorArr);
        } else {
            animatorSet = this.motionAnimation;
            animatorArr = new Animator[4];
            animatorArr[0] = ObjectAnimator.ofFloat(this.backgroundImage, View.SCALE_X, new float[]{1.0f});
            animatorArr[1] = ObjectAnimator.ofFloat(this.backgroundImage, View.SCALE_Y, new float[]{1.0f});
            animatorArr[2] = ObjectAnimator.ofFloat(this.backgroundImage, View.TRANSLATION_X, new float[]{0.0f});
            animatorArr[3] = ObjectAnimator.ofFloat(this.backgroundImage, View.TRANSLATION_Y, new float[]{0.0f});
            animatorSet.playTogether(animatorArr);
        }
        this.motionAnimation.setInterpolator(CubicBezierInterpolator.EASE_OUT);
        this.motionAnimation.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                WallpaperActivity.this.motionAnimation = null;
            }
        });
        this.motionAnimation.start();
    }

    private void setBackgroundColor(int color) {
        this.backgroundColor = color;
        this.backgroundImage.setBackgroundColor(this.backgroundColor);
        if (this.checkBoxView[0] != null) {
            this.checkBoxView[0].invalidate();
        }
        this.patternColor = AndroidUtilities.getPatternColor(this.backgroundColor);
        Theme.applyChatServiceMessageColor(new int[]{this.patternColor, this.patternColor, this.patternColor, this.patternColor});
        if (this.backgroundImage != null) {
            this.backgroundImage.getImageReceiver().setColorFilter(new PorterDuffColorFilter(this.patternColor, this.blendMode));
            this.backgroundImage.getImageReceiver().setAlpha(this.currentIntensity);
            this.backgroundImage.invalidate();
        }
        if (this.listView != null) {
            this.listView.invalidateViews();
        }
        if (this.buttonsContainer != null) {
            int N = this.buttonsContainer.getChildCount();
            for (int a = 0; a < N; a++) {
                this.buttonsContainer.getChildAt(a).invalidate();
            }
        }
        if (this.radialProgress != null) {
            this.radialProgress.setColors("chat_serviceBackground", "chat_serviceBackground", "chat_serviceText", "chat_serviceText");
        }
    }

    private void setCurrentImage(boolean setThumb) {
        if (this.currentWallpaper instanceof TL_wallPaper) {
            TL_wallPaper wallPaper = this.currentWallpaper;
            this.backgroundImage.setImage(wallPaper.document, this.imageFilter, setThumb ? FileLoader.getClosestPhotoSizeWithSize(wallPaper.document.thumbs, 100) : null, "100_100_b", "jpg", wallPaper.document.size, 1, wallPaper);
        } else if (this.currentWallpaper instanceof ColorWallpaper) {
            setBackgroundColor(this.currentWallpaper.color);
            if (this.selectedPattern != null) {
                this.backgroundImage.setImage(this.selectedPattern.document, this.imageFilter, null, null, "jpg", this.selectedPattern.document.size, 1, this.selectedPattern);
            }
        } else if (this.currentWallpaper instanceof FileWallpaper) {
            if (this.currentWallpaperBitmap != null) {
                this.backgroundImage.setImageBitmap(this.currentWallpaperBitmap);
                return;
            }
            FileWallpaper wallPaper2 = this.currentWallpaper;
            if (wallPaper2.originalPath != null) {
                this.backgroundImage.setImage(wallPaper2.originalPath.getAbsolutePath(), this.imageFilter, null);
            } else if (wallPaper2.path != null) {
                this.backgroundImage.setImage(wallPaper2.path.getAbsolutePath(), this.imageFilter, null);
            } else if (((long) wallPaper2.resId) == -2) {
                this.backgroundImage.setImageDrawable(Theme.getThemedWallpaper(false));
            } else if (wallPaper2.resId != 0) {
                this.backgroundImage.setImageResource(wallPaper2.resId);
            }
        } else if (this.currentWallpaper instanceof SearchImage) {
            SearchImage wallPaper3 = this.currentWallpaper;
            if (wallPaper3.photo != null) {
                PhotoSize thumb = FileLoader.getClosestPhotoSizeWithSize(wallPaper3.photo.sizes, 100);
                PhotoSize image = FileLoader.getClosestPhotoSizeWithSize(wallPaper3.photo.sizes, this.maxWallpaperSize, true);
                if (image == thumb) {
                    image = null;
                }
                this.backgroundImage.setImage(image, this.imageFilter, thumb, "100_100_b", "jpg", image != null ? image.size : 0, 1, wallPaper3);
                return;
            }
            this.backgroundImage.setImage(wallPaper3.imageUrl, this.imageFilter, wallPaper3.thumbUrl, "100_100_b");
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        int a;
        ArrayList<ThemeDescription> arrayList = new ArrayList();
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21"));
        for (a = 0; a < this.patternLayout.length; a++) {
            arrayList.add(new ThemeDescription(this.patternLayout[a], 0, null, null, new Drawable[]{Theme.chat_composeShadowDrawable}, null, "chat_messagePanelShadow"));
            arrayList.add(new ThemeDescription(this.patternLayout[a], 0, null, Theme.chat_composeBackgroundPaint, null, null, "chat_messagePanelBackground"));
        }
        for (a = 0; a < this.patternsButtonsContainer.length; a++) {
            arrayList.add(new ThemeDescription(this.patternsButtonsContainer[a], 0, null, null, new Drawable[]{Theme.chat_composeShadowDrawable}, null, "chat_messagePanelShadow"));
            arrayList.add(new ThemeDescription(this.patternsButtonsContainer[a], 0, null, Theme.chat_composeBackgroundPaint, null, null, "chat_messagePanelBackground"));
        }
        arrayList.add(new ThemeDescription(this.bottomOverlayChat, 0, null, null, new Drawable[]{Theme.chat_composeShadowDrawable}, null, "chat_messagePanelShadow"));
        arrayList.add(new ThemeDescription(this.bottomOverlayChat, 0, null, Theme.chat_composeBackgroundPaint, null, null, "chat_messagePanelBackground"));
        arrayList.add(new ThemeDescription(this.bottomOverlayChatText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "chat_fieldOverlayText"));
        for (View themeDescription : this.patternsSaveButton) {
            arrayList.add(new ThemeDescription(themeDescription, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "chat_fieldOverlayText"));
        }
        for (View themeDescription2 : this.patternsSaveButton) {
            arrayList.add(new ThemeDescription(themeDescription2, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "chat_fieldOverlayText"));
        }
        if (this.colorPicker != null) {
            for (a = 0; a < this.colorPicker.colorEditText.length; a++) {
                arrayList.add(new ThemeDescription(this.colorPicker.colorEditText[a], ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"));
                arrayList.add(new ThemeDescription(this.colorPicker.colorEditText[a], ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText"));
                arrayList.add(new ThemeDescription(this.colorPicker.colorEditText[a], ThemeDescription.FLAG_HINTTEXTCOLOR | ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, "windowBackgroundWhiteBlueHeader"));
                arrayList.add(new ThemeDescription(this.colorPicker.colorEditText[a], ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputField"));
                arrayList.add(new ThemeDescription(this.colorPicker.colorEditText[a], ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "windowBackgroundWhiteInputFieldActivated"));
                arrayList.add(new ThemeDescription(this.colorPicker.colorEditText[a], ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, "windowBackgroundWhiteRedText3"));
            }
        }
        arrayList.add(new ThemeDescription(this.intensitySeekBar, 0, new Class[]{SeekBarView.class}, new String[]{"innerPaint1"}, null, null, null, "player_progressBackground"));
        arrayList.add(new ThemeDescription(this.intensitySeekBar, 0, new Class[]{SeekBarView.class}, new String[]{"outerPaint1"}, null, null, null, "player_progress"));
        arrayList.add(new ThemeDescription(this.intensityCell, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlueHeader"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgInDrawable, Theme.chat_msgInMediaDrawable}, null, "chat_inBubble"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgInSelectedDrawable, Theme.chat_msgInMediaSelectedDrawable}, null, "chat_inBubbleSelected"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgInShadowDrawable, Theme.chat_msgInMediaShadowDrawable}, null, "chat_inBubbleShadow"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, null, "chat_outBubble"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutSelectedDrawable, Theme.chat_msgOutMediaSelectedDrawable}, null, "chat_outBubbleSelected"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutShadowDrawable, Theme.chat_msgOutMediaShadowDrawable}, null, "chat_outBubbleShadow"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_messageTextIn"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_messageTextOut"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutCheckDrawable, Theme.chat_msgOutHalfCheckDrawable}, null, "chat_outSentCheck"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutCheckSelectedDrawable, Theme.chat_msgOutHalfCheckSelectedDrawable}, null, "chat_outSentCheckSelected"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgMediaCheckDrawable, Theme.chat_msgMediaHalfCheckDrawable}, null, "chat_mediaSentCheck"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inReplyLine"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outReplyLine"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inReplyNameText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outReplyNameText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inReplyMessageText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outReplyMessageText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inReplyMediaMessageSelectedText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outReplyMediaMessageSelectedText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inTimeText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outTimeText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inTimeSelectedText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outTimeSelectedText"));
        return (ThemeDescription[]) arrayList.toArray(new ThemeDescription[arrayList.size()]);
    }
}
