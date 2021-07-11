package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
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
import android.view.MotionEvent;
import android.view.View;
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
import org.telegram.tgnet.TLRPC$ChatFull;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$InputStickerSet;
import org.telegram.tgnet.TLRPC$StickerSet;
import org.telegram.tgnet.TLRPC$StickerSetCovered;
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
        return this.captionMaxLength - this.codePointCount;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public PhotoViewerCaptionEnterView(Context context, SizeNotifierFrameLayoutPhoto sizeNotifierFrameLayoutPhoto, View view) {
        super(context);
        Context context2 = context;
        Paint paint2 = new Paint();
        this.paint = paint2;
        this.offset = 0.0f;
        paint2.setColor(NUM);
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
        this.emojiButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                PhotoViewerCaptionEnterView.this.lambda$new$0$PhotoViewerCaptionEnterView(view);
            }
        });
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
        AnonymousClass1 r7 = new EditTextCaption(context2) {
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
        };
        this.messageEditText = r7;
        r7.setWindowView(this.windowView);
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
        this.messageEditText.setOnKeyListener(new View.OnKeyListener() {
            public final boolean onKey(View view, int i, KeyEvent keyEvent) {
                return PhotoViewerCaptionEnterView.this.lambda$new$1$PhotoViewerCaptionEnterView(view, i, keyEvent);
            }
        });
        this.messageEditText.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                PhotoViewerCaptionEnterView.this.lambda$new$2$PhotoViewerCaptionEnterView(view);
            }
        });
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

            /* JADX WARNING: Removed duplicated region for block: B:35:0x0160  */
            /* JADX WARNING: Removed duplicated region for block: B:48:? A[RETURN, SYNTHETIC] */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void afterTextChanged(android.text.Editable r11) {
                /*
                    r10 = this;
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
                    java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
                    r5[r3] = r0
                    java.lang.String r0 = "%d"
                    java.lang.String r0 = java.lang.String.format(r0, r5)
                    java.lang.String unused = r4.lengthText = r0
                    goto L_0x0031
                L_0x002c:
                    org.telegram.ui.Components.PhotoViewerCaptionEnterView r0 = org.telegram.ui.Components.PhotoViewerCaptionEnterView.this
                    java.lang.String unused = r0.lengthText = r1
                L_0x0031:
                    org.telegram.ui.Components.PhotoViewerCaptionEnterView r0 = org.telegram.ui.Components.PhotoViewerCaptionEnterView.this
                    r0.invalidate()
                    org.telegram.ui.Components.PhotoViewerCaptionEnterView r0 = org.telegram.ui.Components.PhotoViewerCaptionEnterView.this
                    boolean r0 = r0.innerTextChange
                    if (r0 != 0) goto L_0x0073
                    boolean r0 = r10.processChange
                    if (r0 == 0) goto L_0x0073
                    int r0 = r11.length()
                    java.lang.Class<android.text.style.ImageSpan> r4 = android.text.style.ImageSpan.class
                    java.lang.Object[] r0 = r11.getSpans(r3, r0, r4)
                    android.text.style.ImageSpan[] r0 = (android.text.style.ImageSpan[]) r0
                    r4 = 0
                L_0x004f:
                    int r5 = r0.length
                    if (r4 >= r5) goto L_0x005a
                    r5 = r0[r4]
                    r11.removeSpan(r5)
                    int r4 = r4 + 1
                    goto L_0x004f
                L_0x005a:
                    org.telegram.ui.Components.PhotoViewerCaptionEnterView r0 = org.telegram.ui.Components.PhotoViewerCaptionEnterView.this
                    org.telegram.ui.Components.EditTextCaption r0 = r0.messageEditText
                    android.text.TextPaint r0 = r0.getPaint()
                    android.graphics.Paint$FontMetricsInt r0 = r0.getFontMetricsInt()
                    r4 = 1101004800(0x41a00000, float:20.0)
                    int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
                    org.telegram.messenger.Emoji.replaceEmoji(r11, r0, r4, r3)
                    r10.processChange = r3
                L_0x0073:
                    org.telegram.ui.Components.PhotoViewerCaptionEnterView r0 = org.telegram.ui.Components.PhotoViewerCaptionEnterView.this
                    int r4 = r11.length()
                    int r11 = java.lang.Character.codePointCount(r11, r3, r4)
                    int unused = r0.codePointCount = r11
                    org.telegram.ui.Components.PhotoViewerCaptionEnterView r11 = org.telegram.ui.Components.PhotoViewerCaptionEnterView.this
                    int r11 = r11.captionMaxLength
                    r4 = 100
                    r0 = 1056964608(0x3var_, float:0.5)
                    r6 = 0
                    r7 = 1065353216(0x3var_, float:1.0)
                    if (r11 <= 0) goto L_0x0137
                    org.telegram.ui.Components.PhotoViewerCaptionEnterView r11 = org.telegram.ui.Components.PhotoViewerCaptionEnterView.this
                    int r11 = r11.captionMaxLength
                    org.telegram.ui.Components.PhotoViewerCaptionEnterView r8 = org.telegram.ui.Components.PhotoViewerCaptionEnterView.this
                    int r8 = r8.codePointCount
                    int r11 = r11 - r8
                    r8 = 100
                    if (r11 > r8) goto L_0x0137
                    r8 = -9999(0xffffffffffffd8f1, float:NaN)
                    if (r11 >= r8) goto L_0x00a6
                    r11 = -9999(0xffffffffffffd8f1, float:NaN)
                L_0x00a6:
                    org.telegram.ui.Components.PhotoViewerCaptionEnterView r8 = org.telegram.ui.Components.PhotoViewerCaptionEnterView.this
                    org.telegram.ui.Components.NumberTextView r8 = r8.captionLimitView
                    org.telegram.ui.Components.PhotoViewerCaptionEnterView r9 = org.telegram.ui.Components.PhotoViewerCaptionEnterView.this
                    org.telegram.ui.Components.NumberTextView r9 = r9.captionLimitView
                    int r9 = r9.getVisibility()
                    if (r9 != 0) goto L_0x00ba
                    r9 = 1
                    goto L_0x00bb
                L_0x00ba:
                    r9 = 0
                L_0x00bb:
                    r8.setNumber(r11, r9)
                    org.telegram.ui.Components.PhotoViewerCaptionEnterView r8 = org.telegram.ui.Components.PhotoViewerCaptionEnterView.this
                    org.telegram.ui.Components.NumberTextView r8 = r8.captionLimitView
                    int r8 = r8.getVisibility()
                    if (r8 == 0) goto L_0x00ee
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
                L_0x00ee:
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
                    if (r11 >= 0) goto L_0x012c
                    org.telegram.ui.Components.PhotoViewerCaptionEnterView r11 = org.telegram.ui.Components.PhotoViewerCaptionEnterView.this
                    org.telegram.ui.Components.NumberTextView r11 = r11.captionLimitView
                    r0 = -1280137(0xffffffffffeCLASSNAME, float:NaN)
                    r11.setTextColor(r0)
                    r11 = 0
                    goto L_0x015a
                L_0x012c:
                    org.telegram.ui.Components.PhotoViewerCaptionEnterView r11 = org.telegram.ui.Components.PhotoViewerCaptionEnterView.this
                    org.telegram.ui.Components.NumberTextView r11 = r11.captionLimitView
                    r0 = -1
                    r11.setTextColor(r0)
                    goto L_0x0159
                L_0x0137:
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
                L_0x0159:
                    r11 = 1
                L_0x015a:
                    org.telegram.ui.Components.PhotoViewerCaptionEnterView r0 = org.telegram.ui.Components.PhotoViewerCaptionEnterView.this
                    boolean r1 = r0.sendButtonEnabled
                    if (r1 == r11) goto L_0x01aa
                    r0.sendButtonEnabled = r11
                    android.animation.ValueAnimator r11 = r0.sendButtonColorAnimator
                    if (r11 == 0) goto L_0x0171
                    org.telegram.ui.Components.PhotoViewerCaptionEnterView r11 = org.telegram.ui.Components.PhotoViewerCaptionEnterView.this
                    android.animation.ValueAnimator r11 = r11.sendButtonColorAnimator
                    r11.cancel()
                L_0x0171:
                    org.telegram.ui.Components.PhotoViewerCaptionEnterView r11 = org.telegram.ui.Components.PhotoViewerCaptionEnterView.this
                    r0 = 2
                    float[] r0 = new float[r0]
                    boolean r1 = r11.sendButtonEnabled
                    if (r1 == 0) goto L_0x017c
                    r4 = 0
                    goto L_0x017e
                L_0x017c:
                    r4 = 1065353216(0x3var_, float:1.0)
                L_0x017e:
                    r0[r3] = r4
                    if (r1 == 0) goto L_0x0184
                    r6 = 1065353216(0x3var_, float:1.0)
                L_0x0184:
                    r0[r2] = r6
                    android.animation.ValueAnimator r0 = android.animation.ValueAnimator.ofFloat(r0)
                    android.animation.ValueAnimator unused = r11.sendButtonColorAnimator = r0
                    org.telegram.ui.Components.PhotoViewerCaptionEnterView r11 = org.telegram.ui.Components.PhotoViewerCaptionEnterView.this
                    android.animation.ValueAnimator r11 = r11.sendButtonColorAnimator
                    org.telegram.ui.Components.-$$Lambda$PhotoViewerCaptionEnterView$2$Kpzvncy5_cmP5zKv9lNHlpeDwJY r0 = new org.telegram.ui.Components.-$$Lambda$PhotoViewerCaptionEnterView$2$Kpzvncy5_cmP5zKv9lNHlpeDwJY
                    r0.<init>()
                    r11.addUpdateListener(r0)
                    org.telegram.ui.Components.PhotoViewerCaptionEnterView r11 = org.telegram.ui.Components.PhotoViewerCaptionEnterView.this
                    android.animation.ValueAnimator r11 = r11.sendButtonColorAnimator
                    r0 = 150(0x96, double:7.4E-322)
                    android.animation.ValueAnimator r11 = r11.setDuration(r0)
                    r11.start()
                L_0x01aa:
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.PhotoViewerCaptionEnterView.AnonymousClass2.afterTextChanged(android.text.Editable):void");
            }

            /* access modifiers changed from: private */
            /* renamed from: lambda$afterTextChanged$0 */
            public /* synthetic */ void lambda$afterTextChanged$0$PhotoViewerCaptionEnterView$2(ValueAnimator valueAnimator) {
                float unused = PhotoViewerCaptionEnterView.this.sendButtonEnabledProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                int color = Theme.getColor("dialogFloatingIcon");
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
        linearLayout.addView(imageView3, LayoutHelper.createLinear(48, 48, 80));
        imageView3.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                PhotoViewerCaptionEnterView.this.lambda$new$3$PhotoViewerCaptionEnterView(view);
            }
        });
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$PhotoViewerCaptionEnterView(View view) {
        if (this.keyboardVisible || ((AndroidUtilities.isInMultiwindow || AndroidUtilities.usingHardwareInput) && !isPopupShowing())) {
            showPopup(1, false);
        } else {
            openKeyboardInternal();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$1 */
    public /* synthetic */ boolean lambda$new$1$PhotoViewerCaptionEnterView(View view, int i, KeyEvent keyEvent) {
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
    /* renamed from: lambda$new$2 */
    public /* synthetic */ void lambda$new$2$PhotoViewerCaptionEnterView(View view) {
        if (isPopupShowing()) {
            showPopup((AndroidUtilities.isInMultiwindow || AndroidUtilities.usingHardwareInput) ? 0 : 2, false);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$3 */
    public /* synthetic */ void lambda$new$3$PhotoViewerCaptionEnterView(View view) {
        if (this.captionMaxLength - this.codePointCount < 0) {
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
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    PhotoViewerCaptionEnterView.this.lambda$onDraw$4$PhotoViewerCaptionEnterView(valueAnimator);
                }
            });
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
            ofFloat2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    PhotoViewerCaptionEnterView.this.lambda$onDraw$5$PhotoViewerCaptionEnterView(valueAnimator);
                }
            });
            this.topBackgroundAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.topBackgroundAnimator.setDuration(200);
            this.topBackgroundAnimator.start();
            this.chatActivityEnterViewAnimateFromTop = 0.0f;
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onDraw$4 */
    public /* synthetic */ void lambda$onDraw$4$PhotoViewerCaptionEnterView(ValueAnimator valueAnimator) {
        this.messageEditText.setOffsetY(((Float) valueAnimator.getAnimatedValue()).floatValue());
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onDraw$5 */
    public /* synthetic */ void lambda$onDraw$5$PhotoViewerCaptionEnterView(ValueAnimator valueAnimator) {
        this.offset = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
    }

    public void setForceFloatingEmoji(boolean z) {
        this.forceFloatingEmoji = z;
    }

    public void updateColors() {
        Theme.setDrawableColor(this.doneDrawable, Theme.getColor("dialogFloatingButton"));
        int color = Theme.getColor("dialogFloatingIcon");
        Theme.setDrawableColor(this.checkDrawable, ColorUtils.setAlphaComponent(color, (int) (((float) Color.alpha(color)) * ((this.sendButtonEnabledProgress * 0.42f) + 0.58f))));
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
            EmojiView emojiView2 = new EmojiView(false, false, getContext(), false, (TLRPC$ChatFull) null);
            this.emojiView = emojiView2;
            emojiView2.setDelegate(new EmojiView.EmojiViewDelegate() {
                public /* synthetic */ boolean canSchedule() {
                    return EmojiView.EmojiViewDelegate.CC.$default$canSchedule(this);
                }

                public /* synthetic */ long getDialogId() {
                    return EmojiView.EmojiViewDelegate.CC.$default$getDialogId(this);
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

                public /* synthetic */ void onStickersGroupClick(int i) {
                    EmojiView.EmojiViewDelegate.CC.$default$onStickersGroupClick(this, i);
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
                    this.messageEditText.postDelayed(new Runnable() {
                        public final void run() {
                            PhotoViewerCaptionEnterView.this.lambda$setFieldFocused$6$PhotoViewerCaptionEnterView();
                        }
                    }, 600);
                }
            } else if (editTextCaption.isFocused() && !this.keyboardVisible) {
                this.messageEditText.clearFocus();
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$setFieldFocused$6 */
    public /* synthetic */ void lambda$setFieldFocused$6$PhotoViewerCaptionEnterView() {
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
        this.lastShow = i;
        if (i == 1) {
            if (this.emojiView == null) {
                createEmojiView();
            }
            this.emojiView.setVisibility(0);
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
            if (z && SharedConfig.smoothKeyboard && i == 0 && this.emojiView != null) {
                ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{(float) this.emojiPadding, 0.0f});
                this.popupAnimating = true;
                this.delegate.onEmojiViewCloseStart();
                ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener((float) this.emojiPadding) {
                    public final /* synthetic */ float f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                        PhotoViewerCaptionEnterView.this.lambda$showPopup$7$PhotoViewerCaptionEnterView(this.f$1, valueAnimator);
                    }
                });
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
    /* renamed from: lambda$showPopup$7 */
    public /* synthetic */ void lambda$showPopup$7$PhotoViewerCaptionEnterView(float f, ValueAnimator valueAnimator) {
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
        int i;
        try {
            i = this.messageEditText.getSelectionStart();
        } catch (Exception e) {
            int length = this.messageEditText.length();
            FileLog.e((Throwable) e);
            i = length;
        }
        MotionEvent obtain = MotionEvent.obtain(0, 0, 0, 0.0f, 0.0f, 0);
        this.messageEditText.onTouchEvent(obtain);
        obtain.recycle();
        MotionEvent obtain2 = MotionEvent.obtain(0, 0, 1, 0.0f, 0.0f, 0);
        this.messageEditText.onTouchEvent(obtain2);
        obtain2.recycle();
        AndroidUtilities.showKeyboard(this.messageEditText);
        try {
            this.messageEditText.setSelection(i);
        } catch (Exception e2) {
            FileLog.e((Throwable) e2);
        }
    }

    public boolean isPopupShowing() {
        EmojiView emojiView2 = this.emojiView;
        return emojiView2 != null && emojiView2.getVisibility() == 0;
    }

    public boolean isPopupAnimatig() {
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
}
