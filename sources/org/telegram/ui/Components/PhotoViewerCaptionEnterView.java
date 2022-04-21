package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Vibrator;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.core.graphics.ColorUtils;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AdjustPanLayoutHelper;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.EmojiView;
import org.telegram.ui.Components.SizeNotifierFrameLayoutPhoto;

public class PhotoViewerCaptionEnterView extends FrameLayout implements NotificationCenter.NotificationCenterDelegate, SizeNotifierFrameLayoutPhoto.SizeNotifierFrameLayoutPhotoDelegate {
    float animationProgress = 0.0f;
    /* access modifiers changed from: private */
    public NumberTextView captionLimitView;
    /* access modifiers changed from: private */
    public int captionMaxLength = 1024;
    private float chatActivityEnterViewAnimateFromTop;
    /* access modifiers changed from: private */
    public Drawable checkDrawable;
    /* access modifiers changed from: private */
    public int codePointCount;
    /* access modifiers changed from: private */
    public PhotoViewerCaptionEnterViewDelegate delegate;
    /* access modifiers changed from: private */
    public final ImageView doneButton;
    private Drawable doneDrawable;
    private ImageView emojiButton;
    private ReplaceableIconDrawable emojiIconDrawable;
    /* access modifiers changed from: private */
    public int emojiPadding;
    /* access modifiers changed from: private */
    public EmojiView emojiView;
    private boolean forceFloatingEmoji;
    /* access modifiers changed from: private */
    public boolean innerTextChange;
    /* access modifiers changed from: private */
    public boolean isInitLineCount;
    private int keyboardHeight;
    private int keyboardHeightLand;
    private boolean keyboardVisible;
    int lastShow;
    private int lastSizeChangeValue1;
    private boolean lastSizeChangeValue2;
    /* access modifiers changed from: private */
    public String lengthText;
    private TextPaint lengthTextPaint;
    /* access modifiers changed from: private */
    public int lineCount;
    /* access modifiers changed from: private */
    public EditTextCaption messageEditText;
    ValueAnimator messageEditTextAnimator;
    private int messageEditTextPredrawHeigth;
    private int messageEditTextPredrawScrollY;
    float offset;
    Paint paint;
    /* access modifiers changed from: private */
    public boolean popupAnimating;
    private final Theme.ResourcesProvider resourcesProvider;
    /* access modifiers changed from: private */
    public ValueAnimator sendButtonColorAnimator;
    boolean sendButtonEnabled = true;
    /* access modifiers changed from: private */
    public float sendButtonEnabledProgress = 1.0f;
    private boolean shouldAnimateEditTextWithBounds;
    private SizeNotifierFrameLayoutPhoto sizeNotifierLayout;
    ValueAnimator topBackgroundAnimator;
    private View windowView;

    public interface PhotoViewerCaptionEnterViewDelegate {
        void onCaptionEnter();

        void onEmojiViewCloseEnd();

        void onEmojiViewCloseStart();

        void onEmojiViewOpen();

        void onTextChanged(CharSequence charSequence);

        void onWindowSizeChanged(int i);
    }

    public int getCaptionLimitOffset() {
        return this.captionMaxLength - this.codePointCount;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public PhotoViewerCaptionEnterView(Context context, SizeNotifierFrameLayoutPhoto parent, View window, Theme.ResourcesProvider resourcesProvider2) {
        super(context);
        Context context2 = context;
        Paint paint2 = new Paint();
        this.paint = paint2;
        this.offset = 0.0f;
        this.resourcesProvider = resourcesProvider2;
        paint2.setColor(NUM);
        setWillNotDraw(false);
        setFocusable(true);
        setFocusableInTouchMode(true);
        setClipChildren(false);
        this.windowView = window;
        this.sizeNotifierLayout = parent;
        LinearLayout textFieldContainer = new LinearLayout(context2);
        textFieldContainer.setClipChildren(false);
        textFieldContainer.setOrientation(0);
        addView(textFieldContainer, LayoutHelper.createFrame(-1, -2.0f, 51, 2.0f, 0.0f, 0.0f, 0.0f));
        FrameLayout frameLayout = new FrameLayout(context2);
        frameLayout.setClipChildren(false);
        textFieldContainer.addView(frameLayout, LayoutHelper.createLinear(0, -2, 1.0f));
        ImageView imageView = new ImageView(context2);
        this.emojiButton = imageView;
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        this.emojiButton.setPadding(AndroidUtilities.dp(4.0f), AndroidUtilities.dp(1.0f), 0, 0);
        this.emojiButton.setAlpha(0.58f);
        frameLayout.addView(this.emojiButton, LayoutHelper.createFrame(48, 48, 83));
        this.emojiButton.setOnClickListener(new PhotoViewerCaptionEnterView$$ExternalSyntheticLambda3(this));
        this.emojiButton.setContentDescription(LocaleController.getString("Emoji", NUM));
        ImageView imageView2 = this.emojiButton;
        ReplaceableIconDrawable replaceableIconDrawable = new ReplaceableIconDrawable(context2);
        this.emojiIconDrawable = replaceableIconDrawable;
        imageView2.setImageDrawable(replaceableIconDrawable);
        this.emojiIconDrawable.setColorFilter(new PorterDuffColorFilter(-1, PorterDuff.Mode.MULTIPLY));
        this.emojiIconDrawable.setIcon(NUM, false);
        TextPaint textPaint = new TextPaint(1);
        this.lengthTextPaint = textPaint;
        textPaint.setTextSize((float) AndroidUtilities.dp(13.0f));
        this.lengthTextPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.lengthTextPaint.setColor(-2500135);
        AnonymousClass1 r10 = new EditTextCaption(context2, (Theme.ResourcesProvider) null) {
            /* access modifiers changed from: protected */
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                try {
                    boolean unused = PhotoViewerCaptionEnterView.this.isInitLineCount = getMeasuredWidth() == 0 && getMeasuredHeight() == 0;
                    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                    if (PhotoViewerCaptionEnterView.this.isInitLineCount) {
                        int unused2 = PhotoViewerCaptionEnterView.this.lineCount = getLineCount();
                    }
                    boolean unused3 = PhotoViewerCaptionEnterView.this.isInitLineCount = false;
                } catch (Exception e) {
                    setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), AndroidUtilities.dp(51.0f));
                    FileLog.e((Throwable) e);
                }
            }

            /* access modifiers changed from: protected */
            public void onSelectionChanged(int selStart, int selEnd) {
                super.onSelectionChanged(selStart, selEnd);
                if (selStart != selEnd) {
                    fixHandleView(false);
                } else {
                    fixHandleView(true);
                }
            }

            /* access modifiers changed from: protected */
            public void extendActionMode(ActionMode actionMode, Menu menu) {
                PhotoViewerCaptionEnterView.this.extendActionMode(actionMode, menu);
            }

            /* access modifiers changed from: protected */
            public int getActionModeStyle() {
                return 2;
            }

            public boolean requestRectangleOnScreen(Rect rectangle) {
                rectangle.bottom += AndroidUtilities.dp(1000.0f);
                return super.requestRectangleOnScreen(rectangle);
            }
        };
        this.messageEditText = r10;
        r10.setOnFocusChangeListener(new PhotoViewerCaptionEnterView$$ExternalSyntheticLambda6(this));
        this.messageEditText.setSelectAllOnFocus(false);
        this.messageEditText.setDelegate(new PhotoViewerCaptionEnterView$$ExternalSyntheticLambda9(this));
        this.messageEditText.setWindowView(this.windowView);
        this.messageEditText.setHint(LocaleController.getString("AddCaption", NUM));
        this.messageEditText.setImeOptions(NUM);
        this.messageEditText.setLinkTextColor(-8994063);
        EditTextCaption editTextCaption = this.messageEditText;
        editTextCaption.setInputType(editTextCaption.getInputType() | 16384);
        this.messageEditText.setMaxLines(4);
        this.messageEditText.setHorizontallyScrolling(false);
        this.messageEditText.setTextSize(1, 18.0f);
        this.messageEditText.setGravity(80);
        this.messageEditText.setPadding(0, AndroidUtilities.dp(11.0f), 0, AndroidUtilities.dp(12.0f));
        this.messageEditText.setBackgroundDrawable((Drawable) null);
        this.messageEditText.setCursorColor(-1);
        this.messageEditText.setCursorSize(AndroidUtilities.dp(20.0f));
        this.messageEditText.setTextColor(-1);
        this.messageEditText.setHighlightColor(NUM);
        this.messageEditText.setHintTextColor(-NUM);
        frameLayout.addView(this.messageEditText, LayoutHelper.createFrame(-1, -2.0f, 83, 52.0f, 0.0f, 6.0f, 0.0f));
        this.messageEditText.setOnKeyListener(new PhotoViewerCaptionEnterView$$ExternalSyntheticLambda7(this));
        this.messageEditText.setOnClickListener(new PhotoViewerCaptionEnterView$$ExternalSyntheticLambda4(this));
        this.messageEditText.addTextChangedListener(new TextWatcher() {
            boolean processChange = false;

            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (PhotoViewerCaptionEnterView.this.lineCount != PhotoViewerCaptionEnterView.this.messageEditText.getLineCount()) {
                    if (!PhotoViewerCaptionEnterView.this.isInitLineCount && PhotoViewerCaptionEnterView.this.messageEditText.getMeasuredWidth() > 0) {
                        PhotoViewerCaptionEnterView photoViewerCaptionEnterView = PhotoViewerCaptionEnterView.this;
                        photoViewerCaptionEnterView.onLineCountChanged(photoViewerCaptionEnterView.lineCount, PhotoViewerCaptionEnterView.this.messageEditText.getLineCount());
                    }
                    PhotoViewerCaptionEnterView photoViewerCaptionEnterView2 = PhotoViewerCaptionEnterView.this;
                    int unused = photoViewerCaptionEnterView2.lineCount = photoViewerCaptionEnterView2.messageEditText.getLineCount();
                }
                if (!PhotoViewerCaptionEnterView.this.innerTextChange) {
                    if (PhotoViewerCaptionEnterView.this.delegate != null) {
                        PhotoViewerCaptionEnterView.this.delegate.onTextChanged(charSequence);
                    }
                    if (count - before > 1) {
                        this.processChange = true;
                    }
                }
            }

            /* JADX WARNING: Removed duplicated region for block: B:34:0x0161  */
            /* JADX WARNING: Removed duplicated region for block: B:47:? A[RETURN, SYNTHETIC] */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void afterTextChanged(android.text.Editable r14) {
                /*
                    r13 = this;
                    org.telegram.ui.Components.PhotoViewerCaptionEnterView r0 = org.telegram.ui.Components.PhotoViewerCaptionEnterView.this
                    int r0 = r0.captionMaxLength
                    org.telegram.ui.Components.PhotoViewerCaptionEnterView r1 = org.telegram.ui.Components.PhotoViewerCaptionEnterView.this
                    org.telegram.ui.Components.EditTextCaption r1 = r1.messageEditText
                    int r1 = r1.length()
                    int r0 = r0 - r1
                    r1 = 0
                    r2 = 1
                    r3 = 0
                    r4 = 128(0x80, float:1.794E-43)
                    if (r0 > r4) goto L_0x002c
                    org.telegram.ui.Components.PhotoViewerCaptionEnterView r4 = org.telegram.ui.Components.PhotoViewerCaptionEnterView.this
                    java.lang.Object[] r5 = new java.lang.Object[r2]
                    java.lang.Integer r6 = java.lang.Integer.valueOf(r0)
                    r5[r3] = r6
                    java.lang.String r6 = "%d"
                    java.lang.String r5 = java.lang.String.format(r6, r5)
                    java.lang.String unused = r4.lengthText = r5
                    goto L_0x0031
                L_0x002c:
                    org.telegram.ui.Components.PhotoViewerCaptionEnterView r4 = org.telegram.ui.Components.PhotoViewerCaptionEnterView.this
                    java.lang.String unused = r4.lengthText = r1
                L_0x0031:
                    org.telegram.ui.Components.PhotoViewerCaptionEnterView r4 = org.telegram.ui.Components.PhotoViewerCaptionEnterView.this
                    r4.invalidate()
                    org.telegram.ui.Components.PhotoViewerCaptionEnterView r4 = org.telegram.ui.Components.PhotoViewerCaptionEnterView.this
                    boolean r4 = r4.innerTextChange
                    if (r4 != 0) goto L_0x0073
                    boolean r4 = r13.processChange
                    if (r4 == 0) goto L_0x0073
                    int r4 = r14.length()
                    java.lang.Class<android.text.style.ImageSpan> r5 = android.text.style.ImageSpan.class
                    java.lang.Object[] r4 = r14.getSpans(r3, r4, r5)
                    android.text.style.ImageSpan[] r4 = (android.text.style.ImageSpan[]) r4
                    r5 = 0
                L_0x004f:
                    int r6 = r4.length
                    if (r5 >= r6) goto L_0x005a
                    r6 = r4[r5]
                    r14.removeSpan(r6)
                    int r5 = r5 + 1
                    goto L_0x004f
                L_0x005a:
                    org.telegram.ui.Components.PhotoViewerCaptionEnterView r5 = org.telegram.ui.Components.PhotoViewerCaptionEnterView.this
                    org.telegram.ui.Components.EditTextCaption r5 = r5.messageEditText
                    android.text.TextPaint r5 = r5.getPaint()
                    android.graphics.Paint$FontMetricsInt r5 = r5.getFontMetricsInt()
                    r6 = 1101004800(0x41a00000, float:20.0)
                    int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
                    org.telegram.messenger.Emoji.replaceEmoji(r14, r5, r6, r3)
                    r13.processChange = r3
                L_0x0073:
                    org.telegram.ui.Components.PhotoViewerCaptionEnterView r4 = org.telegram.ui.Components.PhotoViewerCaptionEnterView.this
                    int r5 = r14.length()
                    int r5 = java.lang.Character.codePointCount(r14, r3, r5)
                    int unused = r4.codePointCount = r5
                    r4 = 1
                    org.telegram.ui.Components.PhotoViewerCaptionEnterView r5 = org.telegram.ui.Components.PhotoViewerCaptionEnterView.this
                    int r5 = r5.captionMaxLength
                    r6 = 100
                    r8 = 1056964608(0x3var_, float:0.5)
                    r9 = 0
                    r10 = 1065353216(0x3var_, float:1.0)
                    if (r5 <= 0) goto L_0x0139
                    org.telegram.ui.Components.PhotoViewerCaptionEnterView r5 = org.telegram.ui.Components.PhotoViewerCaptionEnterView.this
                    int r5 = r5.captionMaxLength
                    org.telegram.ui.Components.PhotoViewerCaptionEnterView r11 = org.telegram.ui.Components.PhotoViewerCaptionEnterView.this
                    int r11 = r11.codePointCount
                    int r5 = r5 - r11
                    r11 = r5
                    r12 = 100
                    if (r5 > r12) goto L_0x0139
                    r5 = -9999(0xffffffffffffd8f1, float:NaN)
                    if (r11 >= r5) goto L_0x00a8
                    r11 = -9999(0xffffffffffffd8f1, float:NaN)
                L_0x00a8:
                    org.telegram.ui.Components.PhotoViewerCaptionEnterView r5 = org.telegram.ui.Components.PhotoViewerCaptionEnterView.this
                    org.telegram.ui.Components.NumberTextView r5 = r5.captionLimitView
                    org.telegram.ui.Components.PhotoViewerCaptionEnterView r12 = org.telegram.ui.Components.PhotoViewerCaptionEnterView.this
                    org.telegram.ui.Components.NumberTextView r12 = r12.captionLimitView
                    int r12 = r12.getVisibility()
                    if (r12 != 0) goto L_0x00bc
                    r12 = 1
                    goto L_0x00bd
                L_0x00bc:
                    r12 = 0
                L_0x00bd:
                    r5.setNumber(r11, r12)
                    org.telegram.ui.Components.PhotoViewerCaptionEnterView r5 = org.telegram.ui.Components.PhotoViewerCaptionEnterView.this
                    org.telegram.ui.Components.NumberTextView r5 = r5.captionLimitView
                    int r5 = r5.getVisibility()
                    if (r5 == 0) goto L_0x00f0
                    org.telegram.ui.Components.PhotoViewerCaptionEnterView r5 = org.telegram.ui.Components.PhotoViewerCaptionEnterView.this
                    org.telegram.ui.Components.NumberTextView r5 = r5.captionLimitView
                    r5.setVisibility(r3)
                    org.telegram.ui.Components.PhotoViewerCaptionEnterView r5 = org.telegram.ui.Components.PhotoViewerCaptionEnterView.this
                    org.telegram.ui.Components.NumberTextView r5 = r5.captionLimitView
                    r5.setAlpha(r9)
                    org.telegram.ui.Components.PhotoViewerCaptionEnterView r5 = org.telegram.ui.Components.PhotoViewerCaptionEnterView.this
                    org.telegram.ui.Components.NumberTextView r5 = r5.captionLimitView
                    r5.setScaleX(r8)
                    org.telegram.ui.Components.PhotoViewerCaptionEnterView r5 = org.telegram.ui.Components.PhotoViewerCaptionEnterView.this
                    org.telegram.ui.Components.NumberTextView r5 = r5.captionLimitView
                    r5.setScaleY(r8)
                L_0x00f0:
                    org.telegram.ui.Components.PhotoViewerCaptionEnterView r5 = org.telegram.ui.Components.PhotoViewerCaptionEnterView.this
                    org.telegram.ui.Components.NumberTextView r5 = r5.captionLimitView
                    android.view.ViewPropertyAnimator r5 = r5.animate()
                    android.view.ViewPropertyAnimator r1 = r5.setListener(r1)
                    r1.cancel()
                    org.telegram.ui.Components.PhotoViewerCaptionEnterView r1 = org.telegram.ui.Components.PhotoViewerCaptionEnterView.this
                    org.telegram.ui.Components.NumberTextView r1 = r1.captionLimitView
                    android.view.ViewPropertyAnimator r1 = r1.animate()
                    android.view.ViewPropertyAnimator r1 = r1.alpha(r10)
                    android.view.ViewPropertyAnimator r1 = r1.scaleX(r10)
                    android.view.ViewPropertyAnimator r1 = r1.scaleY(r10)
                    android.view.ViewPropertyAnimator r1 = r1.setDuration(r6)
                    r1.start()
                    if (r11 >= 0) goto L_0x012e
                    r4 = 0
                    org.telegram.ui.Components.PhotoViewerCaptionEnterView r1 = org.telegram.ui.Components.PhotoViewerCaptionEnterView.this
                    org.telegram.ui.Components.NumberTextView r1 = r1.captionLimitView
                    r5 = -1280137(0xffffffffffeCLASSNAME, float:NaN)
                    r1.setTextColor(r5)
                    goto L_0x015b
                L_0x012e:
                    org.telegram.ui.Components.PhotoViewerCaptionEnterView r1 = org.telegram.ui.Components.PhotoViewerCaptionEnterView.this
                    org.telegram.ui.Components.NumberTextView r1 = r1.captionLimitView
                    r5 = -1
                    r1.setTextColor(r5)
                    goto L_0x015b
                L_0x0139:
                    org.telegram.ui.Components.PhotoViewerCaptionEnterView r1 = org.telegram.ui.Components.PhotoViewerCaptionEnterView.this
                    org.telegram.ui.Components.NumberTextView r1 = r1.captionLimitView
                    android.view.ViewPropertyAnimator r1 = r1.animate()
                    android.view.ViewPropertyAnimator r1 = r1.alpha(r9)
                    android.view.ViewPropertyAnimator r1 = r1.scaleX(r8)
                    android.view.ViewPropertyAnimator r1 = r1.scaleY(r8)
                    android.view.ViewPropertyAnimator r1 = r1.setDuration(r6)
                    org.telegram.ui.Components.PhotoViewerCaptionEnterView$2$1 r5 = new org.telegram.ui.Components.PhotoViewerCaptionEnterView$2$1
                    r5.<init>()
                    r1.setListener(r5)
                L_0x015b:
                    org.telegram.ui.Components.PhotoViewerCaptionEnterView r1 = org.telegram.ui.Components.PhotoViewerCaptionEnterView.this
                    boolean r1 = r1.sendButtonEnabled
                    if (r1 == r4) goto L_0x01b3
                    org.telegram.ui.Components.PhotoViewerCaptionEnterView r1 = org.telegram.ui.Components.PhotoViewerCaptionEnterView.this
                    r1.sendButtonEnabled = r4
                    org.telegram.ui.Components.PhotoViewerCaptionEnterView r1 = org.telegram.ui.Components.PhotoViewerCaptionEnterView.this
                    android.animation.ValueAnimator r1 = r1.sendButtonColorAnimator
                    if (r1 == 0) goto L_0x0176
                    org.telegram.ui.Components.PhotoViewerCaptionEnterView r1 = org.telegram.ui.Components.PhotoViewerCaptionEnterView.this
                    android.animation.ValueAnimator r1 = r1.sendButtonColorAnimator
                    r1.cancel()
                L_0x0176:
                    org.telegram.ui.Components.PhotoViewerCaptionEnterView r1 = org.telegram.ui.Components.PhotoViewerCaptionEnterView.this
                    r5 = 2
                    float[] r5 = new float[r5]
                    boolean r6 = r1.sendButtonEnabled
                    if (r6 == 0) goto L_0x0181
                    r6 = 0
                    goto L_0x0183
                L_0x0181:
                    r6 = 1065353216(0x3var_, float:1.0)
                L_0x0183:
                    r5[r3] = r6
                    org.telegram.ui.Components.PhotoViewerCaptionEnterView r3 = org.telegram.ui.Components.PhotoViewerCaptionEnterView.this
                    boolean r3 = r3.sendButtonEnabled
                    if (r3 == 0) goto L_0x018d
                    r9 = 1065353216(0x3var_, float:1.0)
                L_0x018d:
                    r5[r2] = r9
                    android.animation.ValueAnimator r2 = android.animation.ValueAnimator.ofFloat(r5)
                    android.animation.ValueAnimator unused = r1.sendButtonColorAnimator = r2
                    org.telegram.ui.Components.PhotoViewerCaptionEnterView r1 = org.telegram.ui.Components.PhotoViewerCaptionEnterView.this
                    android.animation.ValueAnimator r1 = r1.sendButtonColorAnimator
                    org.telegram.ui.Components.PhotoViewerCaptionEnterView$2$$ExternalSyntheticLambda0 r2 = new org.telegram.ui.Components.PhotoViewerCaptionEnterView$2$$ExternalSyntheticLambda0
                    r2.<init>(r13)
                    r1.addUpdateListener(r2)
                    org.telegram.ui.Components.PhotoViewerCaptionEnterView r1 = org.telegram.ui.Components.PhotoViewerCaptionEnterView.this
                    android.animation.ValueAnimator r1 = r1.sendButtonColorAnimator
                    r2 = 150(0x96, double:7.4E-322)
                    android.animation.ValueAnimator r1 = r1.setDuration(r2)
                    r1.start()
                L_0x01b3:
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.PhotoViewerCaptionEnterView.AnonymousClass2.afterTextChanged(android.text.Editable):void");
            }

            /* renamed from: lambda$afterTextChanged$0$org-telegram-ui-Components-PhotoViewerCaptionEnterView$2  reason: not valid java name */
            public /* synthetic */ void m4224x66ab4dd8(ValueAnimator valueAnimator) {
                float unused = PhotoViewerCaptionEnterView.this.sendButtonEnabledProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                int color = PhotoViewerCaptionEnterView.this.getThemedColor("dialogFloatingIcon");
                Theme.setDrawableColor(PhotoViewerCaptionEnterView.this.checkDrawable, ColorUtils.setAlphaComponent(color, (int) (((float) Color.alpha(color)) * ((PhotoViewerCaptionEnterView.this.sendButtonEnabledProgress * 0.42f) + 0.58f))));
                PhotoViewerCaptionEnterView.this.doneButton.invalidate();
            }
        });
        this.doneDrawable = Theme.createCircleDrawable(AndroidUtilities.dp(16.0f), -10043398);
        this.checkDrawable = context.getResources().getDrawable(NUM).mutate();
        CombinedDrawable combinedDrawable = new CombinedDrawable(this.doneDrawable, this.checkDrawable, 0, AndroidUtilities.dp(1.0f));
        combinedDrawable.setCustomSize(AndroidUtilities.dp(32.0f), AndroidUtilities.dp(32.0f));
        ImageView imageView3 = new ImageView(context2);
        this.doneButton = imageView3;
        imageView3.setScaleType(ImageView.ScaleType.CENTER);
        imageView3.setImageDrawable(combinedDrawable);
        textFieldContainer.addView(imageView3, LayoutHelper.createLinear(48, 48, 80));
        imageView3.setOnClickListener(new PhotoViewerCaptionEnterView$$ExternalSyntheticLambda5(this));
        imageView3.setContentDescription(LocaleController.getString("Done", NUM));
        NumberTextView numberTextView = new NumberTextView(context2);
        this.captionLimitView = numberTextView;
        numberTextView.setVisibility(8);
        this.captionLimitView.setTextSize(15);
        this.captionLimitView.setTextColor(-1);
        this.captionLimitView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.captionLimitView.setCenterAlign(true);
        addView(this.captionLimitView, LayoutHelper.createFrame(48, 20.0f, 85, 3.0f, 0.0f, 3.0f, 48.0f));
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-PhotoViewerCaptionEnterView  reason: not valid java name */
    public /* synthetic */ void m4214x2fe3bd6f(View view) {
        if (this.keyboardVisible || ((AndroidUtilities.isInMultiwindow || AndroidUtilities.usingHardwareInput) && !isPopupShowing())) {
            showPopup(1, false);
        } else {
            openKeyboardInternal();
        }
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-PhotoViewerCaptionEnterView  reason: not valid java name */
    public /* synthetic */ void m4215x311a104e(View view, boolean focused) {
        if (focused) {
            try {
                EditTextCaption editTextCaption = this.messageEditText;
                editTextCaption.setSelection(editTextCaption.length(), this.messageEditText.length());
            } catch (Exception e) {
            }
        }
    }

    /* renamed from: lambda$new$2$org-telegram-ui-Components-PhotoViewerCaptionEnterView  reason: not valid java name */
    public /* synthetic */ void m4216x3250632d() {
        this.messageEditText.invalidateEffects();
    }

    /* renamed from: lambda$new$3$org-telegram-ui-Components-PhotoViewerCaptionEnterView  reason: not valid java name */
    public /* synthetic */ boolean m4217x3386b60c(View view, int i, KeyEvent keyEvent) {
        if (i == 4) {
            if (this.windowView != null && hideActionMode()) {
                return true;
            }
            if (!this.keyboardVisible && isPopupShowing()) {
                if (keyEvent.getAction() == 1) {
                    showPopup(0, true);
                }
                return true;
            }
        }
        return false;
    }

    /* renamed from: lambda$new$4$org-telegram-ui-Components-PhotoViewerCaptionEnterView  reason: not valid java name */
    public /* synthetic */ void m4218x34bd08eb(View view) {
        if (isPopupShowing()) {
            showPopup((AndroidUtilities.isInMultiwindow || AndroidUtilities.usingHardwareInput) ? 0 : 2, false);
        }
    }

    /* renamed from: lambda$new$5$org-telegram-ui-Components-PhotoViewerCaptionEnterView  reason: not valid java name */
    public /* synthetic */ void m4219x35var_bca(View view) {
        if (this.captionMaxLength - this.codePointCount < 0) {
            AndroidUtilities.shakeView(this.captionLimitView, 2.0f, 0);
            Vibrator v = (Vibrator) this.captionLimitView.getContext().getSystemService("vibrator");
            if (v != null) {
                v.vibrate(200);
                return;
            }
            return;
        }
        this.delegate.onCaptionEnter();
    }

    /* access modifiers changed from: private */
    public void onLineCountChanged(int lineCountOld, int lineCountNew) {
        if (!TextUtils.isEmpty(this.messageEditText.getText())) {
            this.shouldAnimateEditTextWithBounds = true;
            this.messageEditTextPredrawHeigth = this.messageEditText.getMeasuredHeight();
            this.messageEditTextPredrawScrollY = this.messageEditText.getScrollY();
            invalidate();
        } else {
            this.messageEditText.animate().cancel();
            this.messageEditText.setOffsetY(0.0f);
            this.shouldAnimateEditTextWithBounds = false;
        }
        this.chatActivityEnterViewAnimateFromTop = ((float) getTop()) + this.offset;
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        canvas.save();
        canvas.drawRect(0.0f, this.offset, (float) getMeasuredWidth(), (float) getMeasuredHeight(), this.paint);
        canvas.clipRect(0.0f, this.offset, (float) getMeasuredWidth(), (float) getMeasuredHeight());
        super.dispatchDraw(canvas);
        canvas.restore();
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.shouldAnimateEditTextWithBounds) {
            EditTextCaption editTextCaption = this.messageEditText;
            editTextCaption.setOffsetY(editTextCaption.getOffsetY() - ((float) ((this.messageEditTextPredrawHeigth - this.messageEditText.getMeasuredHeight()) + (this.messageEditTextPredrawScrollY - this.messageEditText.getScrollY()))));
            ValueAnimator a = ValueAnimator.ofFloat(new float[]{this.messageEditText.getOffsetY(), 0.0f});
            a.addUpdateListener(new PhotoViewerCaptionEnterView$$ExternalSyntheticLambda0(this));
            ValueAnimator valueAnimator = this.messageEditTextAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            this.messageEditTextAnimator = a;
            a.setDuration(200);
            a.setInterpolator(CubicBezierInterpolator.DEFAULT);
            a.start();
            this.shouldAnimateEditTextWithBounds = false;
        }
        float dy = this.chatActivityEnterViewAnimateFromTop;
        if (dy != 0.0f && dy != ((float) getTop()) + this.offset) {
            ValueAnimator valueAnimator2 = this.topBackgroundAnimator;
            if (valueAnimator2 != null) {
                valueAnimator2.cancel();
            }
            float top = this.chatActivityEnterViewAnimateFromTop - (((float) getTop()) + this.offset);
            this.offset = top;
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{top, 0.0f});
            this.topBackgroundAnimator = ofFloat;
            ofFloat.addUpdateListener(new PhotoViewerCaptionEnterView$$ExternalSyntheticLambda1(this));
            this.topBackgroundAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.topBackgroundAnimator.setDuration(200);
            this.topBackgroundAnimator.start();
            this.chatActivityEnterViewAnimateFromTop = 0.0f;
        }
    }

    /* renamed from: lambda$onDraw$6$org-telegram-ui-Components-PhotoViewerCaptionEnterView  reason: not valid java name */
    public /* synthetic */ void m4220xcb57dd4c(ValueAnimator animation) {
        this.messageEditText.setOffsetY(((Float) animation.getAnimatedValue()).floatValue());
    }

    /* renamed from: lambda$onDraw$7$org-telegram-ui-Components-PhotoViewerCaptionEnterView  reason: not valid java name */
    public /* synthetic */ void m4221xcc8e302b(ValueAnimator valueAnimator) {
        this.offset = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
    }

    public void setForceFloatingEmoji(boolean value) {
        this.forceFloatingEmoji = value;
    }

    public void updateColors() {
        Theme.setDrawableColor(this.doneDrawable, getThemedColor("dialogFloatingButton"));
        int color = getThemedColor("dialogFloatingIcon");
        Theme.setDrawableColor(this.checkDrawable, ColorUtils.setAlphaComponent(color, (int) (((float) Color.alpha(color)) * ((this.sendButtonEnabledProgress * 0.42f) + 0.58f))));
        EmojiView emojiView2 = this.emojiView;
        if (emojiView2 != null) {
            emojiView2.updateColors();
        }
    }

    public boolean hideActionMode() {
        return false;
    }

    /* access modifiers changed from: protected */
    public void extendActionMode(ActionMode actionMode, Menu menu) {
    }

    private void onWindowSizeChanged() {
        int size = this.sizeNotifierLayout.getHeight();
        if (!this.keyboardVisible) {
            size -= this.emojiPadding;
        }
        PhotoViewerCaptionEnterViewDelegate photoViewerCaptionEnterViewDelegate = this.delegate;
        if (photoViewerCaptionEnterViewDelegate != null) {
            photoViewerCaptionEnterViewDelegate.onWindowSizeChanged(size);
        }
    }

    public void onCreate() {
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiLoaded);
        this.sizeNotifierLayout.setDelegate(this);
    }

    public void onDestroy() {
        hidePopup();
        if (isKeyboardVisible()) {
            closeKeyboard();
        }
        this.keyboardVisible = false;
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiLoaded);
        SizeNotifierFrameLayoutPhoto sizeNotifierFrameLayoutPhoto = this.sizeNotifierLayout;
        if (sizeNotifierFrameLayoutPhoto != null) {
            sizeNotifierFrameLayoutPhoto.setDelegate((SizeNotifierFrameLayoutPhoto.SizeNotifierFrameLayoutPhotoDelegate) null);
        }
    }

    public void setDelegate(PhotoViewerCaptionEnterViewDelegate delegate2) {
        this.delegate = delegate2;
    }

    public void setFieldText(CharSequence text) {
        EditTextCaption editTextCaption = this.messageEditText;
        if (editTextCaption != null) {
            editTextCaption.setText(text);
            EditTextCaption editTextCaption2 = this.messageEditText;
            editTextCaption2.setSelection(editTextCaption2.getText().length());
            PhotoViewerCaptionEnterViewDelegate photoViewerCaptionEnterViewDelegate = this.delegate;
            if (photoViewerCaptionEnterViewDelegate != null) {
                photoViewerCaptionEnterViewDelegate.onTextChanged(this.messageEditText.getText());
            }
            this.captionMaxLength = MessagesController.getInstance(UserConfig.selectedAccount).maxCaptionLength;
        }
    }

    public int getSelectionLength() {
        EditTextCaption editTextCaption = this.messageEditText;
        if (editTextCaption == null) {
            return 0;
        }
        try {
            return editTextCaption.getSelectionEnd() - this.messageEditText.getSelectionStart();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return 0;
        }
    }

    public int getCursorPosition() {
        EditTextCaption editTextCaption = this.messageEditText;
        if (editTextCaption == null) {
            return 0;
        }
        return editTextCaption.getSelectionStart();
    }

    private void createEmojiView() {
        if (this.emojiView == null) {
            EmojiView emojiView2 = new EmojiView(false, false, getContext(), false, (TLRPC.ChatFull) null, (ViewGroup) null, (Theme.ResourcesProvider) null);
            this.emojiView = emojiView2;
            emojiView2.setDelegate(new EmojiView.EmojiViewDelegate() {
                public /* synthetic */ boolean canSchedule() {
                    return EmojiView.EmojiViewDelegate.CC.$default$canSchedule(this);
                }

                public /* synthetic */ long getDialogId() {
                    return EmojiView.EmojiViewDelegate.CC.$default$getDialogId(this);
                }

                public /* synthetic */ float getProgressToSearchOpened() {
                    return EmojiView.EmojiViewDelegate.CC.$default$getProgressToSearchOpened(this);
                }

                public /* synthetic */ int getThreadId() {
                    return EmojiView.EmojiViewDelegate.CC.$default$getThreadId(this);
                }

                public /* synthetic */ void invalidateEnterView() {
                    EmojiView.EmojiViewDelegate.CC.$default$invalidateEnterView(this);
                }

                public /* synthetic */ boolean isExpanded() {
                    return EmojiView.EmojiViewDelegate.CC.$default$isExpanded(this);
                }

                public /* synthetic */ boolean isInScheduleMode() {
                    return EmojiView.EmojiViewDelegate.CC.$default$isInScheduleMode(this);
                }

                public /* synthetic */ boolean isSearchOpened() {
                    return EmojiView.EmojiViewDelegate.CC.$default$isSearchOpened(this);
                }

                public /* synthetic */ void onClearEmojiRecent() {
                    EmojiView.EmojiViewDelegate.CC.$default$onClearEmojiRecent(this);
                }

                public /* synthetic */ void onGifSelected(View view, Object obj, String str, Object obj2, boolean z, int i) {
                    EmojiView.EmojiViewDelegate.CC.$default$onGifSelected(this, view, obj, str, obj2, z, i);
                }

                public /* synthetic */ void onSearchOpenClose(int i) {
                    EmojiView.EmojiViewDelegate.CC.$default$onSearchOpenClose(this, i);
                }

                public /* synthetic */ void onShowStickerSet(TLRPC.StickerSet stickerSet, TLRPC.InputStickerSet inputStickerSet) {
                    EmojiView.EmojiViewDelegate.CC.$default$onShowStickerSet(this, stickerSet, inputStickerSet);
                }

                public /* synthetic */ void onStickerSelected(View view, TLRPC.Document document, String str, Object obj, MessageObject.SendAnimationData sendAnimationData, boolean z, int i) {
                    EmojiView.EmojiViewDelegate.CC.$default$onStickerSelected(this, view, document, str, obj, sendAnimationData, z, i);
                }

                public /* synthetic */ void onStickerSetAdd(TLRPC.StickerSetCovered stickerSetCovered) {
                    EmojiView.EmojiViewDelegate.CC.$default$onStickerSetAdd(this, stickerSetCovered);
                }

                public /* synthetic */ void onStickerSetRemove(TLRPC.StickerSetCovered stickerSetCovered) {
                    EmojiView.EmojiViewDelegate.CC.$default$onStickerSetRemove(this, stickerSetCovered);
                }

                public /* synthetic */ void onStickersGroupClick(long j) {
                    EmojiView.EmojiViewDelegate.CC.$default$onStickersGroupClick(this, j);
                }

                public /* synthetic */ void onStickersSettingsClick() {
                    EmojiView.EmojiViewDelegate.CC.$default$onStickersSettingsClick(this);
                }

                public /* synthetic */ void onTabOpened(int i) {
                    EmojiView.EmojiViewDelegate.CC.$default$onTabOpened(this, i);
                }

                public /* synthetic */ void showTrendingStickersAlert(TrendingStickersLayout trendingStickersLayout) {
                    EmojiView.EmojiViewDelegate.CC.$default$showTrendingStickersAlert(this, trendingStickersLayout);
                }

                public boolean onBackspace() {
                    if (PhotoViewerCaptionEnterView.this.messageEditText.length() == 0) {
                        return false;
                    }
                    PhotoViewerCaptionEnterView.this.messageEditText.dispatchKeyEvent(new KeyEvent(0, 67));
                    return true;
                }

                public void onEmojiSelected(String symbol) {
                    int i = PhotoViewerCaptionEnterView.this.messageEditText.getSelectionEnd();
                    if (i < 0) {
                        i = 0;
                    }
                    try {
                        boolean unused = PhotoViewerCaptionEnterView.this.innerTextChange = true;
                        CharSequence localCharSequence = Emoji.replaceEmoji(symbol, PhotoViewerCaptionEnterView.this.messageEditText.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
                        PhotoViewerCaptionEnterView.this.messageEditText.setText(PhotoViewerCaptionEnterView.this.messageEditText.getText().insert(i, localCharSequence));
                        int j = localCharSequence.length() + i;
                        PhotoViewerCaptionEnterView.this.messageEditText.setSelection(j, j);
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    } catch (Throwable th) {
                        boolean unused2 = PhotoViewerCaptionEnterView.this.innerTextChange = false;
                        throw th;
                    }
                    boolean unused3 = PhotoViewerCaptionEnterView.this.innerTextChange = false;
                }
            });
            this.sizeNotifierLayout.addView(this.emojiView);
        }
    }

    public void addEmojiToRecent(String code) {
        createEmojiView();
        this.emojiView.addEmojiToRecent(code);
    }

    public void replaceWithText(int start, int len, CharSequence text, boolean parseEmoji) {
        try {
            SpannableStringBuilder builder = new SpannableStringBuilder(this.messageEditText.getText());
            builder.replace(start, start + len, text);
            if (parseEmoji) {
                Emoji.replaceEmoji(builder, this.messageEditText.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
            }
            this.messageEditText.setText(builder);
            this.messageEditText.setSelection(Math.min(text.length() + start, this.messageEditText.length()));
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public void setFieldFocused(boolean focus) {
        EditTextCaption editTextCaption = this.messageEditText;
        if (editTextCaption != null) {
            if (focus) {
                if (!editTextCaption.isFocused()) {
                    this.messageEditText.postDelayed(new PhotoViewerCaptionEnterView$$ExternalSyntheticLambda8(this), 600);
                }
            } else if (editTextCaption.isFocused() && !this.keyboardVisible) {
                this.messageEditText.clearFocus();
            }
        }
    }

    /* renamed from: lambda$setFieldFocused$8$org-telegram-ui-Components-PhotoViewerCaptionEnterView  reason: not valid java name */
    public /* synthetic */ void m4222xe94970c8() {
        EditTextCaption editTextCaption = this.messageEditText;
        if (editTextCaption != null) {
            try {
                editTextCaption.requestFocus();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    public CharSequence getFieldCharSequence() {
        return AndroidUtilities.getTrimmedString(this.messageEditText.getText());
    }

    public int getEmojiPadding() {
        return this.emojiPadding;
    }

    public boolean isPopupView(View view) {
        return view == this.emojiView;
    }

    private void showPopup(int show, boolean animated) {
        EmojiView emojiView2;
        this.lastShow = show;
        if (show == 1) {
            if (this.emojiView == null) {
                createEmojiView();
            }
            this.emojiView.setVisibility(0);
            this.delegate.onEmojiViewOpen();
            if (this.keyboardHeight <= 0) {
                this.keyboardHeight = MessagesController.getGlobalEmojiSettings().getInt("kbd_height", AndroidUtilities.dp(200.0f));
            }
            if (this.keyboardHeightLand <= 0) {
                this.keyboardHeightLand = MessagesController.getGlobalEmojiSettings().getInt("kbd_height_land3", AndroidUtilities.dp(200.0f));
            }
            int currentHeight = AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y ? this.keyboardHeightLand : this.keyboardHeight;
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.emojiView.getLayoutParams();
            layoutParams.width = AndroidUtilities.displaySize.x;
            layoutParams.height = currentHeight;
            this.emojiView.setLayoutParams(layoutParams);
            if (!AndroidUtilities.isInMultiwindow && !this.forceFloatingEmoji) {
                AndroidUtilities.hideKeyboard(this.messageEditText);
            }
            SizeNotifierFrameLayoutPhoto sizeNotifierFrameLayoutPhoto = this.sizeNotifierLayout;
            if (sizeNotifierFrameLayoutPhoto != null) {
                this.emojiPadding = currentHeight;
                sizeNotifierFrameLayoutPhoto.requestLayout();
                this.emojiIconDrawable.setIcon(NUM, true);
                onWindowSizeChanged();
                return;
            }
            return;
        }
        if (this.emojiButton != null) {
            this.emojiIconDrawable.setIcon(NUM, true);
        }
        if (this.sizeNotifierLayout != null) {
            if (animated && SharedConfig.smoothKeyboard && show == 0 && this.emojiView != null) {
                ValueAnimator animator = ValueAnimator.ofFloat(new float[]{(float) this.emojiPadding, 0.0f});
                this.popupAnimating = true;
                this.delegate.onEmojiViewCloseStart();
                animator.addUpdateListener(new PhotoViewerCaptionEnterView$$ExternalSyntheticLambda2(this, (float) this.emojiPadding));
                animator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        int unused = PhotoViewerCaptionEnterView.this.emojiPadding = 0;
                        PhotoViewerCaptionEnterView.this.setTranslationY(0.0f);
                        PhotoViewerCaptionEnterView.this.setAlpha(1.0f);
                        PhotoViewerCaptionEnterView.this.emojiView.setTranslationY(0.0f);
                        boolean unused2 = PhotoViewerCaptionEnterView.this.popupAnimating = false;
                        PhotoViewerCaptionEnterView.this.delegate.onEmojiViewCloseEnd();
                        PhotoViewerCaptionEnterView.this.emojiView.setVisibility(8);
                        PhotoViewerCaptionEnterView.this.emojiView.setAlpha(1.0f);
                    }
                });
                animator.setDuration(210);
                animator.setInterpolator(AdjustPanLayoutHelper.keyboardInterpolator);
                animator.start();
            } else if (show == 0) {
                EmojiView emojiView3 = this.emojiView;
                if (emojiView3 != null) {
                    emojiView3.setVisibility(8);
                }
                this.emojiPadding = 0;
            } else if (!SharedConfig.smoothKeyboard && (emojiView2 = this.emojiView) != null) {
                emojiView2.setVisibility(8);
            }
            this.sizeNotifierLayout.requestLayout();
            onWindowSizeChanged();
        }
    }

    /* renamed from: lambda$showPopup$9$org-telegram-ui-Components-PhotoViewerCaptionEnterView  reason: not valid java name */
    public /* synthetic */ void m4223x43266e57(float animateFrom, ValueAnimator animation) {
        float v = ((Float) animation.getAnimatedValue()).floatValue();
        this.emojiPadding = (int) v;
        this.emojiView.setTranslationY(animateFrom - v);
        setTranslationY(animateFrom - v);
        setAlpha(v / animateFrom);
        this.emojiView.setAlpha(v / animateFrom);
    }

    public void hidePopup() {
        if (isPopupShowing()) {
            showPopup(0, true);
        }
    }

    private void openKeyboardInternal() {
        showPopup((AndroidUtilities.isInMultiwindow || AndroidUtilities.usingHardwareInput) ? 0 : 2, false);
        openKeyboard();
    }

    public void openKeyboard() {
        this.messageEditText.requestFocus();
        AndroidUtilities.showKeyboard(this.messageEditText);
        try {
            EditTextCaption editTextCaption = this.messageEditText;
            editTextCaption.setSelection(editTextCaption.length(), this.messageEditText.length());
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public boolean isPopupShowing() {
        EmojiView emojiView2 = this.emojiView;
        return emojiView2 != null && emojiView2.getVisibility() == 0;
    }

    public boolean isPopupAnimating() {
        return this.popupAnimating;
    }

    public void closeKeyboard() {
        AndroidUtilities.hideKeyboard(this.messageEditText);
        this.messageEditText.clearFocus();
    }

    public boolean isKeyboardVisible() {
        return ((AndroidUtilities.usingHardwareInput || AndroidUtilities.isInMultiwindow) && getTag() != null) || this.keyboardVisible;
    }

    public void onSizeChanged(int height, boolean isWidthGreater) {
        boolean z;
        int newHeight;
        if (height > AndroidUtilities.dp(50.0f) && this.keyboardVisible && !AndroidUtilities.isInMultiwindow && !this.forceFloatingEmoji) {
            if (isWidthGreater) {
                this.keyboardHeightLand = height;
                MessagesController.getGlobalEmojiSettings().edit().putInt("kbd_height_land3", this.keyboardHeightLand).commit();
            } else {
                this.keyboardHeight = height;
                MessagesController.getGlobalEmojiSettings().edit().putInt("kbd_height", this.keyboardHeight).commit();
            }
        }
        if (isPopupShowing()) {
            if (isWidthGreater) {
                newHeight = this.keyboardHeightLand;
            } else {
                newHeight = this.keyboardHeight;
            }
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.emojiView.getLayoutParams();
            if (!(layoutParams.width == AndroidUtilities.displaySize.x && layoutParams.height == newHeight)) {
                layoutParams.width = AndroidUtilities.displaySize.x;
                layoutParams.height = newHeight;
                this.emojiView.setLayoutParams(layoutParams);
                if (this.sizeNotifierLayout != null) {
                    this.emojiPadding = layoutParams.height;
                    this.sizeNotifierLayout.requestLayout();
                    onWindowSizeChanged();
                }
            }
        }
        if (this.lastSizeChangeValue1 == height && this.lastSizeChangeValue2 == isWidthGreater) {
            onWindowSizeChanged();
            return;
        }
        this.lastSizeChangeValue1 = height;
        this.lastSizeChangeValue2 = isWidthGreater;
        boolean oldValue = this.keyboardVisible;
        boolean z2 = height > 0;
        this.keyboardVisible = z2;
        if (z2 && isPopupShowing()) {
            showPopup(0, false);
        }
        if (this.emojiPadding != 0 && !(z = this.keyboardVisible) && z != oldValue && !isPopupShowing()) {
            this.emojiPadding = 0;
            this.sizeNotifierLayout.requestLayout();
        }
        onWindowSizeChanged();
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        EmojiView emojiView2;
        if (id == NotificationCenter.emojiLoaded && (emojiView2 = this.emojiView) != null) {
            emojiView2.invalidateViews();
        }
    }

    public void setAllowTextEntitiesIntersection(boolean value) {
        this.messageEditText.setAllowTextEntitiesIntersection(value);
    }

    public EditTextCaption getMessageEditText() {
        return this.messageEditText;
    }

    /* access modifiers changed from: private */
    public int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }
}
