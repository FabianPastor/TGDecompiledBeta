package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Vibrator;
import android.text.SpannableString;
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
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC$ChatFull;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$InputStickerSet;
import org.telegram.tgnet.TLRPC$StickerSet;
import org.telegram.tgnet.TLRPC$StickerSetCovered;
import org.telegram.ui.ActionBar.ActionBarLayout;
import org.telegram.ui.ActionBar.AdjustPanLayoutHelper;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.EmojiView;
import org.telegram.ui.Components.Premium.PremiumFeatureBottomSheet;
import org.telegram.ui.Components.SizeNotifierFrameLayoutPhoto;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.PhotoViewer;

public class PhotoViewerCaptionEnterView extends FrameLayout implements NotificationCenter.NotificationCenterDelegate, SizeNotifierFrameLayoutPhoto.SizeNotifierFrameLayoutPhotoDelegate {
    /* access modifiers changed from: private */
    public NumberTextView captionLimitView;
    private float chatActivityEnterViewAnimateFromTop;
    /* access modifiers changed from: private */
    public Drawable checkDrawable;
    /* access modifiers changed from: private */
    public int codePointCount;
    public int currentAccount = UserConfig.selectedAccount;
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
    float offset = 0.0f;
    Paint paint = new Paint();
    /* access modifiers changed from: private */
    public boolean popupAnimating;
    private final Theme.ResourcesProvider resourcesProvider = new DarkTheme();
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

    /* access modifiers changed from: protected */
    public void extendActionMode(ActionMode actionMode, Menu menu) {
    }

    public boolean hideActionMode() {
        return false;
    }

    public int getCaptionLimitOffset() {
        return MessagesController.getInstance(this.currentAccount).getCaptionMaxLengthLimit() - this.codePointCount;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public PhotoViewerCaptionEnterView(Context context, SizeNotifierFrameLayoutPhoto sizeNotifierFrameLayoutPhoto, View view, Theme.ResourcesProvider resourcesProvider2) {
        super(context);
        Context context2 = context;
        this.paint.setColor(NUM);
        setWillNotDraw(false);
        setFocusable(true);
        setFocusableInTouchMode(true);
        setClipChildren(false);
        this.windowView = view;
        this.sizeNotifierLayout = sizeNotifierFrameLayoutPhoto;
        LinearLayout linearLayout = new LinearLayout(context2);
        linearLayout.setClipChildren(false);
        linearLayout.setOrientation(0);
        addView(linearLayout, LayoutHelper.createFrame(-1, -2.0f, 51, 2.0f, 0.0f, 0.0f, 0.0f));
        FrameLayout frameLayout = new FrameLayout(context2);
        frameLayout.setClipChildren(false);
        linearLayout.addView(frameLayout, LayoutHelper.createLinear(0, -2, 1.0f));
        ImageView imageView = new ImageView(context2);
        this.emojiButton = imageView;
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        this.emojiButton.setPadding(AndroidUtilities.dp(4.0f), AndroidUtilities.dp(1.0f), 0, 0);
        this.emojiButton.setAlpha(0.58f);
        frameLayout.addView(this.emojiButton, LayoutHelper.createFrame(48, 48, 83));
        this.emojiButton.setOnClickListener(new PhotoViewerCaptionEnterView$$ExternalSyntheticLambda5(this));
        this.emojiButton.setContentDescription(LocaleController.getString("Emoji", R.string.Emoji));
        ImageView imageView2 = this.emojiButton;
        ReplaceableIconDrawable replaceableIconDrawable = new ReplaceableIconDrawable(context2);
        this.emojiIconDrawable = replaceableIconDrawable;
        imageView2.setImageDrawable(replaceableIconDrawable);
        this.emojiIconDrawable.setColorFilter(new PorterDuffColorFilter(-1, PorterDuff.Mode.MULTIPLY));
        this.emojiIconDrawable.setIcon(R.drawable.input_smile, false);
        TextPaint textPaint = new TextPaint(1);
        this.lengthTextPaint = textPaint;
        textPaint.setTextSize((float) AndroidUtilities.dp(13.0f));
        this.lengthTextPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.lengthTextPaint.setColor(-2500135);
        AnonymousClass1 r8 = new EditTextCaption(context2, (Theme.ResourcesProvider) null) {
            /* access modifiers changed from: protected */
            public int getActionModeStyle() {
                return 2;
            }

            /* access modifiers changed from: protected */
            public void onMeasure(int i, int i2) {
                try {
                    boolean unused = PhotoViewerCaptionEnterView.this.isInitLineCount = getMeasuredWidth() == 0 && getMeasuredHeight() == 0;
                    super.onMeasure(i, i2);
                    if (PhotoViewerCaptionEnterView.this.isInitLineCount) {
                        int unused2 = PhotoViewerCaptionEnterView.this.lineCount = getLineCount();
                    }
                    boolean unused3 = PhotoViewerCaptionEnterView.this.isInitLineCount = false;
                } catch (Exception e) {
                    setMeasuredDimension(View.MeasureSpec.getSize(i), AndroidUtilities.dp(51.0f));
                    FileLog.e((Throwable) e);
                }
            }

            /* access modifiers changed from: protected */
            public void onSelectionChanged(int i, int i2) {
                super.onSelectionChanged(i, i2);
                if (i != i2) {
                    fixHandleView(false);
                } else {
                    fixHandleView(true);
                }
            }

            /* access modifiers changed from: protected */
            public void extendActionMode(ActionMode actionMode, Menu menu) {
                PhotoViewerCaptionEnterView.this.extendActionMode(actionMode, menu);
            }

            public boolean requestRectangleOnScreen(Rect rect) {
                rect.bottom += AndroidUtilities.dp(1000.0f);
                return super.requestRectangleOnScreen(rect);
            }

            public void setText(CharSequence charSequence, TextView.BufferType bufferType) {
                super.setText(charSequence, bufferType);
                invalidateForce();
            }
        };
        this.messageEditText = r8;
        r8.setOnFocusChangeListener(new PhotoViewerCaptionEnterView$$ExternalSyntheticLambda6(this));
        this.messageEditText.setSelectAllOnFocus(false);
        this.messageEditText.setDelegate(new PhotoViewerCaptionEnterView$$ExternalSyntheticLambda9(this));
        this.messageEditText.setWindowView(this.windowView);
        this.messageEditText.setHint(LocaleController.getString("AddCaption", R.string.AddCaption));
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

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
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
                    if (i3 - i2 > 1) {
                        this.processChange = true;
                    }
                }
            }

            /* JADX WARNING: Removed duplicated region for block: B:35:0x0172  */
            /* JADX WARNING: Removed duplicated region for block: B:48:? A[RETURN, SYNTHETIC] */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void afterTextChanged(android.text.Editable r11) {
                /*
                    r10 = this;
                    org.telegram.ui.Components.PhotoViewerCaptionEnterView r0 = org.telegram.ui.Components.PhotoViewerCaptionEnterView.this
                    int r0 = r0.currentAccount
                    org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
                    int r0 = r0.getCaptionMaxLengthLimit()
                    org.telegram.ui.Components.PhotoViewerCaptionEnterView r1 = org.telegram.ui.Components.PhotoViewerCaptionEnterView.this
                    org.telegram.ui.Components.EditTextCaption r1 = r1.messageEditText
                    int r1 = r1.length()
                    int r0 = r0 - r1
                    r1 = 0
                    r2 = 1
                    r3 = 0
                    r4 = 128(0x80, float:1.794E-43)
                    if (r0 > r4) goto L_0x0032
                    org.telegram.ui.Components.PhotoViewerCaptionEnterView r4 = org.telegram.ui.Components.PhotoViewerCaptionEnterView.this
                    java.lang.Object[] r5 = new java.lang.Object[r2]
                    java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
                    r5[r3] = r0
                    java.lang.String r0 = "%d"
                    java.lang.String r0 = java.lang.String.format(r0, r5)
                    java.lang.String unused = r4.lengthText = r0
                    goto L_0x0037
                L_0x0032:
                    org.telegram.ui.Components.PhotoViewerCaptionEnterView r0 = org.telegram.ui.Components.PhotoViewerCaptionEnterView.this
                    java.lang.String unused = r0.lengthText = r1
                L_0x0037:
                    org.telegram.ui.Components.PhotoViewerCaptionEnterView r0 = org.telegram.ui.Components.PhotoViewerCaptionEnterView.this
                    r0.invalidate()
                    org.telegram.ui.Components.PhotoViewerCaptionEnterView r0 = org.telegram.ui.Components.PhotoViewerCaptionEnterView.this
                    boolean r0 = r0.innerTextChange
                    if (r0 != 0) goto L_0x0079
                    boolean r0 = r10.processChange
                    if (r0 == 0) goto L_0x0079
                    int r0 = r11.length()
                    java.lang.Class<android.text.style.ImageSpan> r4 = android.text.style.ImageSpan.class
                    java.lang.Object[] r0 = r11.getSpans(r3, r0, r4)
                    android.text.style.ImageSpan[] r0 = (android.text.style.ImageSpan[]) r0
                    r4 = 0
                L_0x0055:
                    int r5 = r0.length
                    if (r4 >= r5) goto L_0x0060
                    r5 = r0[r4]
                    r11.removeSpan(r5)
                    int r4 = r4 + 1
                    goto L_0x0055
                L_0x0060:
                    org.telegram.ui.Components.PhotoViewerCaptionEnterView r0 = org.telegram.ui.Components.PhotoViewerCaptionEnterView.this
                    org.telegram.ui.Components.EditTextCaption r0 = r0.messageEditText
                    android.text.TextPaint r0 = r0.getPaint()
                    android.graphics.Paint$FontMetricsInt r0 = r0.getFontMetricsInt()
                    r4 = 1101004800(0x41a00000, float:20.0)
                    int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
                    org.telegram.messenger.Emoji.replaceEmoji(r11, r0, r4, r3)
                    r10.processChange = r3
                L_0x0079:
                    org.telegram.ui.Components.PhotoViewerCaptionEnterView r0 = org.telegram.ui.Components.PhotoViewerCaptionEnterView.this
                    int r4 = r11.length()
                    int r11 = java.lang.Character.codePointCount(r11, r3, r4)
                    int unused = r0.codePointCount = r11
                    org.telegram.ui.Components.PhotoViewerCaptionEnterView r11 = org.telegram.ui.Components.PhotoViewerCaptionEnterView.this
                    int r11 = r11.currentAccount
                    org.telegram.messenger.MessagesController r11 = org.telegram.messenger.MessagesController.getInstance(r11)
                    int r11 = r11.getCaptionMaxLengthLimit()
                    r4 = 100
                    r0 = 1056964608(0x3var_, float:0.5)
                    r6 = 0
                    r7 = 1065353216(0x3var_, float:1.0)
                    if (r11 <= 0) goto L_0x0149
                    org.telegram.ui.Components.PhotoViewerCaptionEnterView r11 = org.telegram.ui.Components.PhotoViewerCaptionEnterView.this
                    int r11 = r11.currentAccount
                    org.telegram.messenger.MessagesController r11 = org.telegram.messenger.MessagesController.getInstance(r11)
                    int r11 = r11.getCaptionMaxLengthLimit()
                    org.telegram.ui.Components.PhotoViewerCaptionEnterView r8 = org.telegram.ui.Components.PhotoViewerCaptionEnterView.this
                    int r8 = r8.codePointCount
                    int r11 = r11 - r8
                    r8 = 100
                    if (r11 > r8) goto L_0x0149
                    r8 = -9999(0xffffffffffffd8f1, float:NaN)
                    if (r11 >= r8) goto L_0x00b8
                    r11 = -9999(0xffffffffffffd8f1, float:NaN)
                L_0x00b8:
                    org.telegram.ui.Components.PhotoViewerCaptionEnterView r8 = org.telegram.ui.Components.PhotoViewerCaptionEnterView.this
                    org.telegram.ui.Components.NumberTextView r8 = r8.captionLimitView
                    org.telegram.ui.Components.PhotoViewerCaptionEnterView r9 = org.telegram.ui.Components.PhotoViewerCaptionEnterView.this
                    org.telegram.ui.Components.NumberTextView r9 = r9.captionLimitView
                    int r9 = r9.getVisibility()
                    if (r9 != 0) goto L_0x00cc
                    r9 = 1
                    goto L_0x00cd
                L_0x00cc:
                    r9 = 0
                L_0x00cd:
                    r8.setNumber(r11, r9)
                    org.telegram.ui.Components.PhotoViewerCaptionEnterView r8 = org.telegram.ui.Components.PhotoViewerCaptionEnterView.this
                    org.telegram.ui.Components.NumberTextView r8 = r8.captionLimitView
                    int r8 = r8.getVisibility()
                    if (r8 == 0) goto L_0x0100
                    org.telegram.ui.Components.PhotoViewerCaptionEnterView r8 = org.telegram.ui.Components.PhotoViewerCaptionEnterView.this
                    org.telegram.ui.Components.NumberTextView r8 = r8.captionLimitView
                    r8.setVisibility(r3)
                    org.telegram.ui.Components.PhotoViewerCaptionEnterView r8 = org.telegram.ui.Components.PhotoViewerCaptionEnterView.this
                    org.telegram.ui.Components.NumberTextView r8 = r8.captionLimitView
                    r8.setAlpha(r6)
                    org.telegram.ui.Components.PhotoViewerCaptionEnterView r8 = org.telegram.ui.Components.PhotoViewerCaptionEnterView.this
                    org.telegram.ui.Components.NumberTextView r8 = r8.captionLimitView
                    r8.setScaleX(r0)
                    org.telegram.ui.Components.PhotoViewerCaptionEnterView r8 = org.telegram.ui.Components.PhotoViewerCaptionEnterView.this
                    org.telegram.ui.Components.NumberTextView r8 = r8.captionLimitView
                    r8.setScaleY(r0)
                L_0x0100:
                    org.telegram.ui.Components.PhotoViewerCaptionEnterView r0 = org.telegram.ui.Components.PhotoViewerCaptionEnterView.this
                    org.telegram.ui.Components.NumberTextView r0 = r0.captionLimitView
                    android.view.ViewPropertyAnimator r0 = r0.animate()
                    android.view.ViewPropertyAnimator r0 = r0.setListener(r1)
                    r0.cancel()
                    org.telegram.ui.Components.PhotoViewerCaptionEnterView r0 = org.telegram.ui.Components.PhotoViewerCaptionEnterView.this
                    org.telegram.ui.Components.NumberTextView r0 = r0.captionLimitView
                    android.view.ViewPropertyAnimator r0 = r0.animate()
                    android.view.ViewPropertyAnimator r0 = r0.alpha(r7)
                    android.view.ViewPropertyAnimator r0 = r0.scaleX(r7)
                    android.view.ViewPropertyAnimator r0 = r0.scaleY(r7)
                    android.view.ViewPropertyAnimator r0 = r0.setDuration(r4)
                    r0.start()
                    if (r11 >= 0) goto L_0x013e
                    org.telegram.ui.Components.PhotoViewerCaptionEnterView r11 = org.telegram.ui.Components.PhotoViewerCaptionEnterView.this
                    org.telegram.ui.Components.NumberTextView r11 = r11.captionLimitView
                    r0 = -1280137(0xffffffffffeCLASSNAME, float:NaN)
                    r11.setTextColor(r0)
                    r11 = 0
                    goto L_0x016c
                L_0x013e:
                    org.telegram.ui.Components.PhotoViewerCaptionEnterView r11 = org.telegram.ui.Components.PhotoViewerCaptionEnterView.this
                    org.telegram.ui.Components.NumberTextView r11 = r11.captionLimitView
                    r0 = -1
                    r11.setTextColor(r0)
                    goto L_0x016b
                L_0x0149:
                    org.telegram.ui.Components.PhotoViewerCaptionEnterView r11 = org.telegram.ui.Components.PhotoViewerCaptionEnterView.this
                    org.telegram.ui.Components.NumberTextView r11 = r11.captionLimitView
                    android.view.ViewPropertyAnimator r11 = r11.animate()
                    android.view.ViewPropertyAnimator r11 = r11.alpha(r6)
                    android.view.ViewPropertyAnimator r11 = r11.scaleX(r0)
                    android.view.ViewPropertyAnimator r11 = r11.scaleY(r0)
                    android.view.ViewPropertyAnimator r11 = r11.setDuration(r4)
                    org.telegram.ui.Components.PhotoViewerCaptionEnterView$2$1 r0 = new org.telegram.ui.Components.PhotoViewerCaptionEnterView$2$1
                    r0.<init>()
                    r11.setListener(r0)
                L_0x016b:
                    r11 = 1
                L_0x016c:
                    org.telegram.ui.Components.PhotoViewerCaptionEnterView r0 = org.telegram.ui.Components.PhotoViewerCaptionEnterView.this
                    boolean r1 = r0.sendButtonEnabled
                    if (r1 == r11) goto L_0x01bc
                    r0.sendButtonEnabled = r11
                    android.animation.ValueAnimator r11 = r0.sendButtonColorAnimator
                    if (r11 == 0) goto L_0x0183
                    org.telegram.ui.Components.PhotoViewerCaptionEnterView r11 = org.telegram.ui.Components.PhotoViewerCaptionEnterView.this
                    android.animation.ValueAnimator r11 = r11.sendButtonColorAnimator
                    r11.cancel()
                L_0x0183:
                    org.telegram.ui.Components.PhotoViewerCaptionEnterView r11 = org.telegram.ui.Components.PhotoViewerCaptionEnterView.this
                    r0 = 2
                    float[] r0 = new float[r0]
                    boolean r1 = r11.sendButtonEnabled
                    if (r1 == 0) goto L_0x018e
                    r4 = 0
                    goto L_0x0190
                L_0x018e:
                    r4 = 1065353216(0x3var_, float:1.0)
                L_0x0190:
                    r0[r3] = r4
                    if (r1 == 0) goto L_0x0196
                    r6 = 1065353216(0x3var_, float:1.0)
                L_0x0196:
                    r0[r2] = r6
                    android.animation.ValueAnimator r0 = android.animation.ValueAnimator.ofFloat(r0)
                    android.animation.ValueAnimator unused = r11.sendButtonColorAnimator = r0
                    org.telegram.ui.Components.PhotoViewerCaptionEnterView r11 = org.telegram.ui.Components.PhotoViewerCaptionEnterView.this
                    android.animation.ValueAnimator r11 = r11.sendButtonColorAnimator
                    org.telegram.ui.Components.PhotoViewerCaptionEnterView$2$$ExternalSyntheticLambda0 r0 = new org.telegram.ui.Components.PhotoViewerCaptionEnterView$2$$ExternalSyntheticLambda0
                    r0.<init>(r10)
                    r11.addUpdateListener(r0)
                    org.telegram.ui.Components.PhotoViewerCaptionEnterView r11 = org.telegram.ui.Components.PhotoViewerCaptionEnterView.this
                    android.animation.ValueAnimator r11 = r11.sendButtonColorAnimator
                    r0 = 150(0x96, double:7.4E-322)
                    android.animation.ValueAnimator r11 = r11.setDuration(r0)
                    r11.start()
                L_0x01bc:
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.PhotoViewerCaptionEnterView.AnonymousClass2.afterTextChanged(android.text.Editable):void");
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$afterTextChanged$0(ValueAnimator valueAnimator) {
                float unused = PhotoViewerCaptionEnterView.this.sendButtonEnabledProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                int access$1200 = PhotoViewerCaptionEnterView.this.getThemedColor("dialogFloatingIcon");
                Theme.setDrawableColor(PhotoViewerCaptionEnterView.this.checkDrawable, ColorUtils.setAlphaComponent(access$1200, (int) (((float) Color.alpha(access$1200)) * ((PhotoViewerCaptionEnterView.this.sendButtonEnabledProgress * 0.42f) + 0.58f))));
                PhotoViewerCaptionEnterView.this.doneButton.invalidate();
            }
        });
        this.doneDrawable = Theme.createCircleDrawable(AndroidUtilities.dp(16.0f), -10043398);
        this.checkDrawable = context.getResources().getDrawable(R.drawable.input_done).mutate();
        CombinedDrawable combinedDrawable = new CombinedDrawable(this.doneDrawable, this.checkDrawable, 0, AndroidUtilities.dp(1.0f));
        combinedDrawable.setCustomSize(AndroidUtilities.dp(32.0f), AndroidUtilities.dp(32.0f));
        ImageView imageView3 = new ImageView(context2);
        this.doneButton = imageView3;
        imageView3.setScaleType(ImageView.ScaleType.CENTER);
        imageView3.setImageDrawable(combinedDrawable);
        linearLayout.addView(imageView3, LayoutHelper.createLinear(48, 48, 80));
        imageView3.setOnClickListener(new PhotoViewerCaptionEnterView$$ExternalSyntheticLambda3(this));
        imageView3.setContentDescription(LocaleController.getString("Done", R.string.Done));
        NumberTextView numberTextView = new NumberTextView(context2);
        this.captionLimitView = numberTextView;
        numberTextView.setVisibility(8);
        this.captionLimitView.setTextSize(15);
        this.captionLimitView.setTextColor(-1);
        this.captionLimitView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.captionLimitView.setCenterAlign(true);
        addView(this.captionLimitView, LayoutHelper.createFrame(48, 20.0f, 85, 3.0f, 0.0f, 3.0f, 48.0f));
        this.currentAccount = UserConfig.selectedAccount;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(View view) {
        if (this.keyboardVisible || ((AndroidUtilities.isInMultiwindow || AndroidUtilities.usingHardwareInput) && !isPopupShowing())) {
            showPopup(1, false);
        } else {
            openKeyboardInternal();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1(View view, boolean z) {
        if (z) {
            try {
                EditTextCaption editTextCaption = this.messageEditText;
                editTextCaption.setSelection(editTextCaption.length(), this.messageEditText.length());
            } catch (Exception unused) {
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$2() {
        this.messageEditText.invalidateEffects();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$new$3(View view, int i, KeyEvent keyEvent) {
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

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$4(View view) {
        if (isPopupShowing()) {
            showPopup((AndroidUtilities.isInMultiwindow || AndroidUtilities.usingHardwareInput) ? 0 : 2, false);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$5(View view) {
        if (MessagesController.getInstance(this.currentAccount).getCaptionMaxLengthLimit() - this.codePointCount < 0) {
            AndroidUtilities.shakeView(this.captionLimitView, 2.0f, 0);
            Vibrator vibrator = (Vibrator) this.captionLimitView.getContext().getSystemService("vibrator");
            if (vibrator != null) {
                vibrator.vibrate(200);
                return;
            }
            return;
        }
        this.delegate.onCaptionEnter();
    }

    /* access modifiers changed from: private */
    public void onLineCountChanged(int i, int i2) {
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
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{this.messageEditText.getOffsetY(), 0.0f});
            ofFloat.addUpdateListener(new PhotoViewerCaptionEnterView$$ExternalSyntheticLambda1(this));
            ValueAnimator valueAnimator = this.messageEditTextAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            this.messageEditTextAnimator = ofFloat;
            ofFloat.setDuration(200);
            ofFloat.setInterpolator(CubicBezierInterpolator.DEFAULT);
            ofFloat.start();
            this.shouldAnimateEditTextWithBounds = false;
        }
        float f = this.chatActivityEnterViewAnimateFromTop;
        if (f != 0.0f && f != ((float) getTop()) + this.offset) {
            ValueAnimator valueAnimator2 = this.topBackgroundAnimator;
            if (valueAnimator2 != null) {
                valueAnimator2.cancel();
            }
            float top = this.chatActivityEnterViewAnimateFromTop - (((float) getTop()) + this.offset);
            this.offset = top;
            ValueAnimator ofFloat2 = ValueAnimator.ofFloat(new float[]{top, 0.0f});
            this.topBackgroundAnimator = ofFloat2;
            ofFloat2.addUpdateListener(new PhotoViewerCaptionEnterView$$ExternalSyntheticLambda0(this));
            this.topBackgroundAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.topBackgroundAnimator.setDuration(200);
            this.topBackgroundAnimator.start();
            this.chatActivityEnterViewAnimateFromTop = 0.0f;
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onDraw$6(ValueAnimator valueAnimator) {
        this.messageEditText.setOffsetY(((Float) valueAnimator.getAnimatedValue()).floatValue());
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onDraw$7(ValueAnimator valueAnimator) {
        this.offset = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
    }

    public void setForceFloatingEmoji(boolean z) {
        this.forceFloatingEmoji = z;
    }

    public void updateColors() {
        Theme.setDrawableColor(this.doneDrawable, getThemedColor("dialogFloatingButton"));
        int themedColor = getThemedColor("dialogFloatingIcon");
        Theme.setDrawableColor(this.checkDrawable, ColorUtils.setAlphaComponent(themedColor, (int) (((float) Color.alpha(themedColor)) * ((this.sendButtonEnabledProgress * 0.42f) + 0.58f))));
        EmojiView emojiView2 = this.emojiView;
        if (emojiView2 != null) {
            emojiView2.updateColors();
        }
    }

    private void onWindowSizeChanged() {
        int height = this.sizeNotifierLayout.getHeight();
        if (!this.keyboardVisible) {
            height -= this.emojiPadding;
        }
        PhotoViewerCaptionEnterViewDelegate photoViewerCaptionEnterViewDelegate = this.delegate;
        if (photoViewerCaptionEnterViewDelegate != null) {
            photoViewerCaptionEnterViewDelegate.onWindowSizeChanged(height);
        }
    }

    public void onCreate() {
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiLoaded);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.currentUserPremiumStatusChanged);
        this.sizeNotifierLayout.setDelegate(this);
    }

    public void onDestroy() {
        hidePopup();
        if (isKeyboardVisible()) {
            closeKeyboard();
        }
        this.keyboardVisible = false;
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiLoaded);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.currentUserPremiumStatusChanged);
        SizeNotifierFrameLayoutPhoto sizeNotifierFrameLayoutPhoto = this.sizeNotifierLayout;
        if (sizeNotifierFrameLayoutPhoto != null) {
            sizeNotifierFrameLayoutPhoto.setDelegate((SizeNotifierFrameLayoutPhoto.SizeNotifierFrameLayoutPhotoDelegate) null);
        }
    }

    public void setDelegate(PhotoViewerCaptionEnterViewDelegate photoViewerCaptionEnterViewDelegate) {
        this.delegate = photoViewerCaptionEnterViewDelegate;
    }

    public void setFieldText(CharSequence charSequence) {
        EditTextCaption editTextCaption = this.messageEditText;
        if (editTextCaption != null) {
            editTextCaption.setText(charSequence);
            EditTextCaption editTextCaption2 = this.messageEditText;
            editTextCaption2.setSelection(editTextCaption2.getText().length());
            PhotoViewerCaptionEnterViewDelegate photoViewerCaptionEnterViewDelegate = this.delegate;
            if (photoViewerCaptionEnterViewDelegate != null) {
                photoViewerCaptionEnterViewDelegate.onTextChanged(this.messageEditText.getText());
            }
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

    private class DarkTheme implements Theme.ResourcesProvider {
        public /* synthetic */ void applyServiceShaderMatrix(int i, int i2, float f, float f2) {
            Theme.ResourcesProvider.CC.$default$applyServiceShaderMatrix(this, i, i2, f, f2);
        }

        public /* synthetic */ int getColorOrDefault(String str) {
            return Theme.ResourcesProvider.CC.$default$getColorOrDefault(this, str);
        }

        public /* synthetic */ Integer getCurrentColor(String str) {
            return Theme.ResourcesProvider.CC.$default$getCurrentColor(this, str);
        }

        public /* synthetic */ Drawable getDrawable(String str) {
            return Theme.ResourcesProvider.CC.$default$getDrawable(this, str);
        }

        public /* synthetic */ Paint getPaint(String str) {
            return Theme.ResourcesProvider.CC.$default$getPaint(this, str);
        }

        public /* synthetic */ boolean hasGradientService() {
            return Theme.ResourcesProvider.CC.$default$hasGradientService(this);
        }

        public /* synthetic */ void setAnimatedColor(String str, int i) {
            Theme.ResourcesProvider.CC.$default$setAnimatedColor(this, str, i);
        }

        private DarkTheme(PhotoViewerCaptionEnterView photoViewerCaptionEnterView) {
        }

        /* JADX WARNING: Can't fix incorrect switch cases order */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public java.lang.Integer getColor(java.lang.String r4) {
            /*
                r3 = this;
                r4.hashCode()
                int r0 = r4.hashCode()
                r1 = -1
                switch(r0) {
                    case -2139469579: goto L_0x00d7;
                    case -1850167367: goto L_0x00cb;
                    case -1849805674: goto L_0x00bf;
                    case -1815610844: goto L_0x00b3;
                    case -1633591792: goto L_0x00a7;
                    case -1345036363: goto L_0x009b;
                    case -1285599213: goto L_0x008f;
                    case -343666293: goto L_0x0083;
                    case -249481380: goto L_0x0076;
                    case 339397761: goto L_0x0069;
                    case 421601145: goto L_0x005c;
                    case 634019162: goto L_0x004f;
                    case 1674318617: goto L_0x0042;
                    case 1929729373: goto L_0x0035;
                    case 1976399936: goto L_0x0028;
                    case 2067556030: goto L_0x001b;
                    case 2133456819: goto L_0x000e;
                    default: goto L_0x000b;
                }
            L_0x000b:
                r4 = -1
                goto L_0x00e2
            L_0x000e:
                java.lang.String r0 = "chat_emojiPanelBackground"
                boolean r4 = r4.equals(r0)
                if (r4 != 0) goto L_0x0017
                goto L_0x000b
            L_0x0017:
                r4 = 16
                goto L_0x00e2
            L_0x001b:
                java.lang.String r0 = "chat_emojiPanelIcon"
                boolean r4 = r4.equals(r0)
                if (r4 != 0) goto L_0x0024
                goto L_0x000b
            L_0x0024:
                r4 = 15
                goto L_0x00e2
            L_0x0028:
                java.lang.String r0 = "chat_emojiSearchIcon"
                boolean r4 = r4.equals(r0)
                if (r4 != 0) goto L_0x0031
                goto L_0x000b
            L_0x0031:
                r4 = 14
                goto L_0x00e2
            L_0x0035:
                java.lang.String r0 = "progressCircle"
                boolean r4 = r4.equals(r0)
                if (r4 != 0) goto L_0x003e
                goto L_0x000b
            L_0x003e:
                r4 = 13
                goto L_0x00e2
            L_0x0042:
                java.lang.String r0 = "divider"
                boolean r4 = r4.equals(r0)
                if (r4 != 0) goto L_0x004b
                goto L_0x000b
            L_0x004b:
                r4 = 12
                goto L_0x00e2
            L_0x004f:
                java.lang.String r0 = "chat_emojiPanelBackspace"
                boolean r4 = r4.equals(r0)
                if (r4 != 0) goto L_0x0058
                goto L_0x000b
            L_0x0058:
                r4 = 11
                goto L_0x00e2
            L_0x005c:
                java.lang.String r0 = "chat_emojiPanelIconSelected"
                boolean r4 = r4.equals(r0)
                if (r4 != 0) goto L_0x0065
                goto L_0x000b
            L_0x0065:
                r4 = 10
                goto L_0x00e2
            L_0x0069:
                java.lang.String r0 = "windowBackgroundWhiteBlackText"
                boolean r4 = r4.equals(r0)
                if (r4 != 0) goto L_0x0072
                goto L_0x000b
            L_0x0072:
                r4 = 9
                goto L_0x00e2
            L_0x0076:
                java.lang.String r0 = "listSelectorSDK21"
                boolean r4 = r4.equals(r0)
                if (r4 != 0) goto L_0x007f
                goto L_0x000b
            L_0x007f:
                r4 = 8
                goto L_0x00e2
            L_0x0083:
                java.lang.String r0 = "windowBackgroundWhite"
                boolean r4 = r4.equals(r0)
                if (r4 != 0) goto L_0x008d
                goto L_0x000b
            L_0x008d:
                r4 = 7
                goto L_0x00e2
            L_0x008f:
                java.lang.String r0 = "chat_emojiBottomPanelIcon"
                boolean r4 = r4.equals(r0)
                if (r4 != 0) goto L_0x0099
                goto L_0x000b
            L_0x0099:
                r4 = 6
                goto L_0x00e2
            L_0x009b:
                java.lang.String r0 = "chat_emojiSearchBackground"
                boolean r4 = r4.equals(r0)
                if (r4 != 0) goto L_0x00a5
                goto L_0x000b
            L_0x00a5:
                r4 = 5
                goto L_0x00e2
            L_0x00a7:
                java.lang.String r0 = "chat_emojiPanelStickerPackSelector"
                boolean r4 = r4.equals(r0)
                if (r4 != 0) goto L_0x00b1
                goto L_0x000b
            L_0x00b1:
                r4 = 4
                goto L_0x00e2
            L_0x00b3:
                java.lang.String r0 = "chat_emojiPanelStickerPackSelectorLine"
                boolean r4 = r4.equals(r0)
                if (r4 != 0) goto L_0x00bd
                goto L_0x000b
            L_0x00bd:
                r4 = 3
                goto L_0x00e2
            L_0x00bf:
                java.lang.String r0 = "dialogBackground"
                boolean r4 = r4.equals(r0)
                if (r4 != 0) goto L_0x00c9
                goto L_0x000b
            L_0x00c9:
                r4 = 2
                goto L_0x00e2
            L_0x00cb:
                java.lang.String r0 = "chat_emojiPanelShadowLine"
                boolean r4 = r4.equals(r0)
                if (r4 != 0) goto L_0x00d5
                goto L_0x000b
            L_0x00d5:
                r4 = 1
                goto L_0x00e2
            L_0x00d7:
                java.lang.String r0 = "chat_emojiPanelEmptyText"
                boolean r4 = r4.equals(r0)
                if (r4 != 0) goto L_0x00e1
                goto L_0x000b
            L_0x00e1:
                r4 = 0
            L_0x00e2:
                r0 = -10177041(0xfffffffffvar_b5ef, float:-3.0400864E38)
                r2 = -9539985(0xffffffffff6e6e6f, float:-3.1692967E38)
                switch(r4) {
                    case 0: goto L_0x0150;
                    case 1: goto L_0x0149;
                    case 2: goto L_0x0141;
                    case 3: goto L_0x013c;
                    case 4: goto L_0x0134;
                    case 5: goto L_0x0134;
                    case 6: goto L_0x012f;
                    case 7: goto L_0x0127;
                    case 8: goto L_0x0120;
                    case 9: goto L_0x011b;
                    case 10: goto L_0x0116;
                    case 11: goto L_0x0111;
                    case 12: goto L_0x010a;
                    case 13: goto L_0x0102;
                    case 14: goto L_0x00fa;
                    case 15: goto L_0x00f5;
                    case 16: goto L_0x00ed;
                    default: goto L_0x00eb;
                }
            L_0x00eb:
                r4 = 0
                return r4
            L_0x00ed:
                r4 = -14803425(0xffffffffff1e1e1f, float:-2.1017442E38)
                java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
                return r4
            L_0x00f5:
                java.lang.Integer r4 = java.lang.Integer.valueOf(r2)
                return r4
            L_0x00fa:
                r4 = -9211020(0xfffffffffvar_, float:-3.2360187E38)
                java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
                return r4
            L_0x0102:
                r4 = -10177027(0xfffffffffvar_b5fd, float:-3.0400892E38)
                java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
                return r4
            L_0x010a:
                r4 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
                java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
                return r4
            L_0x0111:
                java.lang.Integer r4 = java.lang.Integer.valueOf(r2)
                return r4
            L_0x0116:
                java.lang.Integer r4 = java.lang.Integer.valueOf(r0)
                return r4
            L_0x011b:
                java.lang.Integer r4 = java.lang.Integer.valueOf(r1)
                return r4
            L_0x0120:
                r4 = 771751936(0x2e000000, float:2.910383E-11)
                java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
                return r4
            L_0x0127:
                r4 = -15198183(0xfffffffffvar_, float:-2.0216778E38)
                java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
                return r4
            L_0x012f:
                java.lang.Integer r4 = java.lang.Integer.valueOf(r2)
                return r4
            L_0x0134:
                r4 = 181267199(0xacdeaff, float:1.9829178E-32)
                java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
                return r4
            L_0x013c:
                java.lang.Integer r4 = java.lang.Integer.valueOf(r0)
                return r4
            L_0x0141:
                r4 = -14803426(0xffffffffff1e1e1e, float:-2.101744E38)
                java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
                return r4
            L_0x0149:
                r4 = -1610612736(0xffffffffa0000000, float:-1.0842022E-19)
                java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
                return r4
            L_0x0150:
                r4 = -8553090(0xffffffffff7d7d7e, float:-3.3694628E38)
                java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
                return r4
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.PhotoViewerCaptionEnterView.DarkTheme.getColor(java.lang.String):java.lang.Integer");
        }
    }

    private void createEmojiView() {
        EmojiView emojiView2 = this.emojiView;
        if (!(emojiView2 == null || emojiView2.currentAccount == UserConfig.selectedAccount)) {
            this.sizeNotifierLayout.removeView(emojiView2);
            this.emojiView = null;
        }
        if (this.emojiView == null) {
            EmojiView emojiView3 = new EmojiView((BaseFragment) null, true, false, false, getContext(), false, (TLRPC$ChatFull) null, (ViewGroup) null, this.resourcesProvider);
            this.emojiView = emojiView3;
            emojiView3.setDelegate(new EmojiView.EmojiViewDelegate() {
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

                public /* synthetic */ boolean isUserSelf() {
                    return EmojiView.EmojiViewDelegate.CC.$default$isUserSelf(this);
                }

                public /* synthetic */ void onClearEmojiRecent() {
                    EmojiView.EmojiViewDelegate.CC.$default$onClearEmojiRecent(this);
                }

                public /* synthetic */ void onEmojiSettingsClick(ArrayList arrayList) {
                    EmojiView.EmojiViewDelegate.CC.$default$onEmojiSettingsClick(this, arrayList);
                }

                public /* synthetic */ void onGifSelected(View view, Object obj, String str, Object obj2, boolean z, int i) {
                    EmojiView.EmojiViewDelegate.CC.$default$onGifSelected(this, view, obj, str, obj2, z, i);
                }

                public /* synthetic */ void onSearchOpenClose(int i) {
                    EmojiView.EmojiViewDelegate.CC.$default$onSearchOpenClose(this, i);
                }

                public /* synthetic */ void onShowStickerSet(TLRPC$StickerSet tLRPC$StickerSet, TLRPC$InputStickerSet tLRPC$InputStickerSet) {
                    EmojiView.EmojiViewDelegate.CC.$default$onShowStickerSet(this, tLRPC$StickerSet, tLRPC$InputStickerSet);
                }

                public /* synthetic */ void onStickerSelected(View view, TLRPC$Document tLRPC$Document, String str, Object obj, MessageObject.SendAnimationData sendAnimationData, boolean z, int i) {
                    EmojiView.EmojiViewDelegate.CC.$default$onStickerSelected(this, view, tLRPC$Document, str, obj, sendAnimationData, z, i);
                }

                public /* synthetic */ void onStickerSetAdd(TLRPC$StickerSetCovered tLRPC$StickerSetCovered) {
                    EmojiView.EmojiViewDelegate.CC.$default$onStickerSetAdd(this, tLRPC$StickerSetCovered);
                }

                public /* synthetic */ void onStickerSetRemove(TLRPC$StickerSetCovered tLRPC$StickerSetCovered) {
                    EmojiView.EmojiViewDelegate.CC.$default$onStickerSetRemove(this, tLRPC$StickerSetCovered);
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

                public void onAnimatedEmojiUnlockClick() {
                    new PremiumFeatureBottomSheet(new BaseFragment() {
                        public int getCurrentAccount() {
                            return this.currentAccount;
                        }

                        public Context getContext() {
                            return PhotoViewerCaptionEnterView.this.getContext();
                        }

                        public Activity getParentActivity() {
                            for (Context context = getContext(); context instanceof ContextWrapper; context = ((ContextWrapper) context).getBaseContext()) {
                                if (context instanceof Activity) {
                                    return (Activity) context;
                                }
                            }
                            return null;
                        }

                        public Dialog getVisibleDialog() {
                            return new Dialog(PhotoViewerCaptionEnterView.this.getContext()) {
                                public void dismiss() {
                                    if ((AnonymousClass1.this.getParentActivity() instanceof LaunchActivity) && ((LaunchActivity) AnonymousClass1.this.getParentActivity()).getActionBarLayout() != null) {
                                        AnonymousClass1 r0 = AnonymousClass1.this;
                                        ActionBarLayout unused = r0.parentLayout = ((LaunchActivity) r0.getParentActivity()).getActionBarLayout();
                                        if (!(AnonymousClass1.this.parentLayout == null || AnonymousClass1.this.parentLayout.getLastFragment() == null || AnonymousClass1.this.parentLayout.getLastFragment().getVisibleDialog() == null)) {
                                            Dialog visibleDialog = AnonymousClass1.this.parentLayout.getLastFragment().getVisibleDialog();
                                            if (visibleDialog instanceof ChatAttachAlert) {
                                                ((ChatAttachAlert) visibleDialog).dismiss(true);
                                            } else {
                                                visibleDialog.dismiss();
                                            }
                                        }
                                    }
                                    PhotoViewer.getInstance().closePhoto(false, false);
                                }
                            };
                        }
                    }, 11, false).show();
                }

                public void onCustomEmojiSelected(long j, TLRPC$Document tLRPC$Document, String str, boolean z) {
                    AnimatedEmojiSpan animatedEmojiSpan;
                    int selectionEnd = PhotoViewerCaptionEnterView.this.messageEditText.getSelectionEnd();
                    if (selectionEnd < 0) {
                        selectionEnd = 0;
                    }
                    try {
                        boolean unused = PhotoViewerCaptionEnterView.this.innerTextChange = true;
                        SpannableString spannableString = new SpannableString(str);
                        if (tLRPC$Document != null) {
                            animatedEmojiSpan = new AnimatedEmojiSpan(tLRPC$Document, PhotoViewerCaptionEnterView.this.messageEditText.getPaint().getFontMetricsInt());
                        } else {
                            animatedEmojiSpan = new AnimatedEmojiSpan(j, PhotoViewerCaptionEnterView.this.messageEditText.getPaint().getFontMetricsInt());
                        }
                        spannableString.setSpan(animatedEmojiSpan, 0, spannableString.length(), 33);
                        PhotoViewerCaptionEnterView.this.messageEditText.setText(PhotoViewerCaptionEnterView.this.messageEditText.getText().insert(selectionEnd, spannableString));
                        int length = selectionEnd + spannableString.length();
                        PhotoViewerCaptionEnterView.this.messageEditText.setSelection(length, length);
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    } catch (Throwable th) {
                        boolean unused2 = PhotoViewerCaptionEnterView.this.innerTextChange = false;
                        throw th;
                    }
                    boolean unused3 = PhotoViewerCaptionEnterView.this.innerTextChange = false;
                }

                public void onEmojiSelected(String str) {
                    int selectionEnd = PhotoViewerCaptionEnterView.this.messageEditText.getSelectionEnd();
                    if (selectionEnd < 0) {
                        selectionEnd = 0;
                    }
                    try {
                        boolean unused = PhotoViewerCaptionEnterView.this.innerTextChange = true;
                        CharSequence replaceEmoji = Emoji.replaceEmoji(str, PhotoViewerCaptionEnterView.this.messageEditText.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
                        PhotoViewerCaptionEnterView.this.messageEditText.setText(PhotoViewerCaptionEnterView.this.messageEditText.getText().insert(selectionEnd, replaceEmoji));
                        int length = selectionEnd + replaceEmoji.length();
                        PhotoViewerCaptionEnterView.this.messageEditText.setSelection(length, length);
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

    public void addEmojiToRecent(String str) {
        createEmojiView();
        this.emojiView.addEmojiToRecent(str);
    }

    public void replaceWithText(int i, int i2, CharSequence charSequence, boolean z) {
        try {
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(this.messageEditText.getText());
            spannableStringBuilder.replace(i, i2 + i, charSequence);
            if (z) {
                Emoji.replaceEmoji(spannableStringBuilder, this.messageEditText.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
            }
            this.messageEditText.setText(spannableStringBuilder);
            this.messageEditText.setSelection(Math.min(i + charSequence.length(), this.messageEditText.length()));
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public void setFieldFocused(boolean z) {
        EditTextCaption editTextCaption = this.messageEditText;
        if (editTextCaption != null) {
            if (z) {
                if (!editTextCaption.isFocused()) {
                    this.messageEditText.postDelayed(new PhotoViewerCaptionEnterView$$ExternalSyntheticLambda8(this), 600);
                }
            } else if (editTextCaption.isFocused() && !this.keyboardVisible) {
                this.messageEditText.clearFocus();
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setFieldFocused$8() {
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

    private void showPopup(int i, boolean z) {
        EmojiView emojiView2;
        if (i == 1) {
            createEmojiView();
            this.emojiView.setVisibility(0);
            this.delegate.onEmojiViewOpen();
            if (this.keyboardHeight <= 0) {
                this.keyboardHeight = MessagesController.getGlobalEmojiSettings().getInt("kbd_height", AndroidUtilities.dp(200.0f));
            }
            if (this.keyboardHeightLand <= 0) {
                this.keyboardHeightLand = MessagesController.getGlobalEmojiSettings().getInt("kbd_height_land3", AndroidUtilities.dp(200.0f));
            }
            Point point = AndroidUtilities.displaySize;
            int i2 = point.x > point.y ? this.keyboardHeightLand : this.keyboardHeight;
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.emojiView.getLayoutParams();
            layoutParams.width = AndroidUtilities.displaySize.x;
            layoutParams.height = i2;
            this.emojiView.setLayoutParams(layoutParams);
            if (!AndroidUtilities.isInMultiwindow && !this.forceFloatingEmoji) {
                AndroidUtilities.hideKeyboard(this.messageEditText);
            }
            SizeNotifierFrameLayoutPhoto sizeNotifierFrameLayoutPhoto = this.sizeNotifierLayout;
            if (sizeNotifierFrameLayoutPhoto != null) {
                this.emojiPadding = i2;
                sizeNotifierFrameLayoutPhoto.requestLayout();
                this.emojiIconDrawable.setIcon(R.drawable.input_keyboard, true);
                onWindowSizeChanged();
                return;
            }
            return;
        }
        if (this.emojiButton != null) {
            this.emojiIconDrawable.setIcon(R.drawable.input_smile, true);
        }
        if (this.sizeNotifierLayout != null) {
            if (z && SharedConfig.smoothKeyboard && i == 0 && this.emojiView != null) {
                ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{(float) this.emojiPadding, 0.0f});
                this.popupAnimating = true;
                this.delegate.onEmojiViewCloseStart();
                ofFloat.addUpdateListener(new PhotoViewerCaptionEnterView$$ExternalSyntheticLambda2(this, (float) this.emojiPadding));
                ofFloat.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
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
                ofFloat.setDuration(210);
                ofFloat.setInterpolator(AdjustPanLayoutHelper.keyboardInterpolator);
                ofFloat.start();
            } else if (i == 0) {
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

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showPopup$9(float f, ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.emojiPadding = (int) floatValue;
        float f2 = f - floatValue;
        this.emojiView.setTranslationY(f2);
        setTranslationY(f2);
        float f3 = floatValue / f;
        setAlpha(f3);
        this.emojiView.setAlpha(f3);
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

    public void onSizeChanged(int i, boolean z) {
        boolean z2;
        int i2;
        if (i > AndroidUtilities.dp(50.0f) && this.keyboardVisible && !AndroidUtilities.isInMultiwindow && !this.forceFloatingEmoji) {
            if (z) {
                this.keyboardHeightLand = i;
                MessagesController.getGlobalEmojiSettings().edit().putInt("kbd_height_land3", this.keyboardHeightLand).commit();
            } else {
                this.keyboardHeight = i;
                MessagesController.getGlobalEmojiSettings().edit().putInt("kbd_height", this.keyboardHeight).commit();
            }
        }
        if (isPopupShowing()) {
            if (z) {
                i2 = this.keyboardHeightLand;
            } else {
                i2 = this.keyboardHeight;
            }
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.emojiView.getLayoutParams();
            int i3 = layoutParams.width;
            int i4 = AndroidUtilities.displaySize.x;
            if (!(i3 == i4 && layoutParams.height == i2)) {
                layoutParams.width = i4;
                layoutParams.height = i2;
                this.emojiView.setLayoutParams(layoutParams);
                SizeNotifierFrameLayoutPhoto sizeNotifierFrameLayoutPhoto = this.sizeNotifierLayout;
                if (sizeNotifierFrameLayoutPhoto != null) {
                    this.emojiPadding = layoutParams.height;
                    sizeNotifierFrameLayoutPhoto.requestLayout();
                    onWindowSizeChanged();
                }
            }
        }
        if (this.lastSizeChangeValue1 == i && this.lastSizeChangeValue2 == z) {
            onWindowSizeChanged();
            return;
        }
        this.lastSizeChangeValue1 = i;
        this.lastSizeChangeValue2 = z;
        boolean z3 = this.keyboardVisible;
        boolean z4 = i > 0;
        this.keyboardVisible = z4;
        if (z4 && isPopupShowing()) {
            showPopup(0, false);
        }
        if (this.emojiPadding != 0 && !(z2 = this.keyboardVisible) && z2 != z3 && !isPopupShowing()) {
            this.emojiPadding = 0;
            this.sizeNotifierLayout.requestLayout();
        }
        onWindowSizeChanged();
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        EmojiView emojiView2;
        if (i == NotificationCenter.emojiLoaded && (emojiView2 = this.emojiView) != null) {
            emojiView2.invalidateViews();
        }
    }

    public void setAllowTextEntitiesIntersection(boolean z) {
        this.messageEditText.setAllowTextEntitiesIntersection(z);
    }

    public EditTextCaption getMessageEditText() {
        return this.messageEditText;
    }

    /* access modifiers changed from: private */
    public int getThemedColor(String str) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(str) : null;
        return color != null ? color.intValue() : Theme.getColor(str);
    }

    public Theme.ResourcesProvider getResourcesProvider() {
        return this.resourcesProvider;
    }
}
