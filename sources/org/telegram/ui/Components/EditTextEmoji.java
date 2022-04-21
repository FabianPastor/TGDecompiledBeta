package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Editable;
import android.text.InputFilter;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AdjustPanLayoutHelper;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.EmojiView;
import org.telegram.ui.Components.SizeNotifierFrameLayout;

public class EditTextEmoji extends FrameLayout implements NotificationCenter.NotificationCenterDelegate, SizeNotifierFrameLayout.SizeNotifierFrameLayoutDelegate {
    public static final int STYLE_DIALOG = 1;
    public static final int STYLE_FRAGMENT = 0;
    AdjustPanLayoutHelper adjustPanLayoutHelper;
    private int currentStyle;
    private EditTextEmojiDelegate delegate;
    /* access modifiers changed from: private */
    public boolean destroyed;
    /* access modifiers changed from: private */
    public EditTextCaption editText;
    private ImageView emojiButton;
    private ReplaceableIconDrawable emojiIconDrawable;
    private int emojiPadding;
    /* access modifiers changed from: private */
    public EmojiView emojiView;
    private boolean emojiViewVisible;
    /* access modifiers changed from: private */
    public int innerTextChange;
    /* access modifiers changed from: private */
    public boolean isAnimatePopupClosing;
    private boolean isPaused;
    private int keyboardHeight;
    private int keyboardHeightLand;
    /* access modifiers changed from: private */
    public boolean keyboardVisible;
    private int lastSizeChangeValue1;
    private boolean lastSizeChangeValue2;
    /* access modifiers changed from: private */
    public Runnable openKeyboardRunnable;
    /* access modifiers changed from: private */
    public BaseFragment parentFragment;
    /* access modifiers changed from: private */
    public final Theme.ResourcesProvider resourcesProvider;
    private boolean showKeyboardOnResume;
    private SizeNotifierFrameLayout sizeNotifierLayout;
    /* access modifiers changed from: private */
    public boolean waitingForKeyboardOpen;

    public interface EditTextEmojiDelegate {
        void onWindowSizeChanged(int i);
    }

    public boolean isPopupVisible() {
        EmojiView emojiView2 = this.emojiView;
        return emojiView2 != null && emojiView2.getVisibility() == 0;
    }

    public boolean isWaitingForKeyboardOpen() {
        return this.waitingForKeyboardOpen;
    }

    public boolean isAnimatePopupClosing() {
        return this.isAnimatePopupClosing;
    }

    public void setAdjustPanLayoutHelper(AdjustPanLayoutHelper adjustPanLayoutHelper2) {
        this.adjustPanLayoutHelper = adjustPanLayoutHelper2;
    }

    public EditTextEmoji(Context context, SizeNotifierFrameLayout parent, BaseFragment fragment, int style) {
        this(context, parent, fragment, style, (Theme.ResourcesProvider) null);
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public EditTextEmoji(Context context, SizeNotifierFrameLayout parent, BaseFragment fragment, int style, Theme.ResourcesProvider resourcesProvider2) {
        super(context);
        Context context2 = context;
        SizeNotifierFrameLayout sizeNotifierFrameLayout = parent;
        int i = style;
        Theme.ResourcesProvider resourcesProvider3 = resourcesProvider2;
        this.isPaused = true;
        this.openKeyboardRunnable = new Runnable() {
            public void run() {
                if (!EditTextEmoji.this.destroyed && EditTextEmoji.this.editText != null && EditTextEmoji.this.waitingForKeyboardOpen && !EditTextEmoji.this.keyboardVisible && !AndroidUtilities.usingHardwareInput && !AndroidUtilities.isInMultiwindow && AndroidUtilities.isTablet()) {
                    EditTextEmoji.this.editText.requestFocus();
                    AndroidUtilities.showKeyboard(EditTextEmoji.this.editText);
                    AndroidUtilities.cancelRunOnUIThread(EditTextEmoji.this.openKeyboardRunnable);
                    AndroidUtilities.runOnUIThread(EditTextEmoji.this.openKeyboardRunnable, 100);
                }
            }
        };
        this.resourcesProvider = resourcesProvider3;
        this.currentStyle = i;
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiLoaded);
        this.parentFragment = fragment;
        this.sizeNotifierLayout = sizeNotifierFrameLayout;
        sizeNotifierFrameLayout.setDelegate(this);
        AnonymousClass2 r7 = new EditTextCaption(context2, resourcesProvider3) {
            public boolean onTouchEvent(MotionEvent event) {
                if (EditTextEmoji.this.isPopupShowing() && event.getAction() == 0) {
                    EditTextEmoji.this.showPopup(AndroidUtilities.usingHardwareInput ? 0 : 2);
                    EditTextEmoji.this.openKeyboardInternal();
                }
                if (event.getAction() == 0) {
                    requestFocus();
                    if (!AndroidUtilities.showKeyboard(this)) {
                        clearFocus();
                        requestFocus();
                    }
                }
                try {
                    return super.onTouchEvent(event);
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                    return false;
                }
            }

            /* access modifiers changed from: protected */
            public void onLineCountChanged(int oldLineCount, int newLineCount) {
                EditTextEmoji.this.onLineCountChanged(oldLineCount, newLineCount);
            }
        };
        this.editText = r7;
        r7.setTextSize(1, 18.0f);
        this.editText.setImeOptions(NUM);
        EditTextCaption editTextCaption = this.editText;
        editTextCaption.setInputType(editTextCaption.getInputType() | 16384);
        this.editText.setMaxLines(4);
        EditTextCaption editTextCaption2 = this.editText;
        editTextCaption2.setFocusable(editTextCaption2.isEnabled());
        this.editText.setCursorSize(AndroidUtilities.dp(20.0f));
        this.editText.setCursorWidth(1.5f);
        this.editText.setCursorColor(getThemedColor("windowBackgroundWhiteBlackText"));
        int i2 = 5;
        if (i == 0) {
            this.editText.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            this.editText.setBackground((Drawable) null);
            this.editText.setLineColors(getThemedColor("windowBackgroundWhiteInputField"), getThemedColor("windowBackgroundWhiteInputFieldActivated"), getThemedColor("windowBackgroundWhiteRedText3"));
            this.editText.setHintTextColor(getThemedColor("windowBackgroundWhiteHintText"));
            this.editText.setTextColor(getThemedColor("windowBackgroundWhiteBlackText"));
            this.editText.setPadding(LocaleController.isRTL ? AndroidUtilities.dp(40.0f) : 0, 0, LocaleController.isRTL ? 0 : AndroidUtilities.dp(40.0f), AndroidUtilities.dp(8.0f));
            addView(this.editText, LayoutHelper.createFrame(-1, -2.0f, 19, LocaleController.isRTL ? 11.0f : 0.0f, 1.0f, LocaleController.isRTL ? 0.0f : 11.0f, 0.0f));
        } else {
            this.editText.setGravity(19);
            this.editText.setHintTextColor(getThemedColor("dialogTextHint"));
            this.editText.setTextColor(getThemedColor("dialogTextBlack"));
            this.editText.setBackground((Drawable) null);
            this.editText.setPadding(0, AndroidUtilities.dp(11.0f), 0, AndroidUtilities.dp(12.0f));
            addView(this.editText, LayoutHelper.createFrame(-1, -1.0f, 19, 48.0f, 0.0f, 0.0f, 0.0f));
        }
        ImageView imageView = new ImageView(context2);
        this.emojiButton = imageView;
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        ImageView imageView2 = this.emojiButton;
        ReplaceableIconDrawable replaceableIconDrawable = new ReplaceableIconDrawable(context2);
        this.emojiIconDrawable = replaceableIconDrawable;
        imageView2.setImageDrawable(replaceableIconDrawable);
        this.emojiIconDrawable.setColorFilter(new PorterDuffColorFilter(getThemedColor("chat_messagePanelIcons"), PorterDuff.Mode.MULTIPLY));
        if (i == 0) {
            this.emojiIconDrawable.setIcon(NUM, false);
            addView(this.emojiButton, LayoutHelper.createFrame(48, 48.0f, (LocaleController.isRTL ? 3 : i2) | 16, 0.0f, 0.0f, 0.0f, 7.0f));
        } else {
            this.emojiIconDrawable.setIcon(NUM, false);
            addView(this.emojiButton, LayoutHelper.createFrame(48, 48.0f, 83, 0.0f, 0.0f, 0.0f, 0.0f));
        }
        if (Build.VERSION.SDK_INT >= 21) {
            this.emojiButton.setBackground(Theme.createSelectorDrawable(getThemedColor("listSelectorSDK21")));
        }
        this.emojiButton.setOnClickListener(new EditTextEmoji$$ExternalSyntheticLambda2(this));
        this.emojiButton.setContentDescription(LocaleController.getString("Emoji", NUM));
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-EditTextEmoji  reason: not valid java name */
    public /* synthetic */ void m3952lambda$new$0$orgtelegramuiComponentsEditTextEmoji(View view) {
        if (this.emojiButton.isEnabled()) {
            AdjustPanLayoutHelper adjustPanLayoutHelper2 = this.adjustPanLayoutHelper;
            if (adjustPanLayoutHelper2 != null && adjustPanLayoutHelper2.animationInProgress()) {
                return;
            }
            if (!isPopupShowing()) {
                boolean z = true;
                showPopup(1);
                EmojiView emojiView2 = this.emojiView;
                if (this.editText.length() <= 0) {
                    z = false;
                }
                emojiView2.onOpen(z);
                this.editText.requestFocus();
                return;
            }
            openKeyboardInternal();
        }
    }

    /* access modifiers changed from: protected */
    public void onLineCountChanged(int oldLineCount, int newLineCount) {
    }

    public void setSizeNotifierLayout(SizeNotifierFrameLayout layout) {
        this.sizeNotifierLayout = layout;
        layout.setDelegate(this);
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.emojiLoaded) {
            EmojiView emojiView2 = this.emojiView;
            if (emojiView2 != null) {
                emojiView2.invalidateViews();
            }
            EditTextCaption editTextCaption = this.editText;
            if (editTextCaption != null) {
                int color = editTextCaption.getCurrentTextColor();
                this.editText.setTextColor(-1);
                this.editText.setTextColor(color);
            }
        }
    }

    public void setEnabled(boolean enabled) {
        this.editText.setEnabled(enabled);
        this.emojiButton.setVisibility(enabled ? 0 : 8);
        if (enabled) {
            this.editText.setPadding(LocaleController.isRTL ? AndroidUtilities.dp(40.0f) : 0, 0, LocaleController.isRTL ? 0 : AndroidUtilities.dp(40.0f), AndroidUtilities.dp(8.0f));
        } else {
            this.editText.setPadding(0, 0, 0, AndroidUtilities.dp(8.0f));
        }
    }

    public void setFocusable(boolean focusable) {
        this.editText.setFocusable(focusable);
    }

    public void hideEmojiView() {
        EmojiView emojiView2;
        if (!(this.emojiViewVisible || (emojiView2 = this.emojiView) == null || emojiView2.getVisibility() == 8)) {
            this.emojiView.setVisibility(8);
        }
        this.emojiPadding = 0;
    }

    public void setDelegate(EditTextEmojiDelegate editTextEmojiDelegate) {
        this.delegate = editTextEmojiDelegate;
    }

    public void onPause() {
        this.isPaused = true;
        closeKeyboard();
    }

    public void onResume() {
        this.isPaused = false;
        if (this.showKeyboardOnResume) {
            this.showKeyboardOnResume = false;
            this.editText.requestFocus();
            AndroidUtilities.showKeyboard(this.editText);
            if (!AndroidUtilities.usingHardwareInput && !this.keyboardVisible && !AndroidUtilities.isInMultiwindow && !AndroidUtilities.isTablet()) {
                this.waitingForKeyboardOpen = true;
                AndroidUtilities.cancelRunOnUIThread(this.openKeyboardRunnable);
                AndroidUtilities.runOnUIThread(this.openKeyboardRunnable, 100);
            }
        }
    }

    public void onDestroy() {
        this.destroyed = true;
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiLoaded);
        EmojiView emojiView2 = this.emojiView;
        if (emojiView2 != null) {
            emojiView2.onDestroy();
        }
        SizeNotifierFrameLayout sizeNotifierFrameLayout = this.sizeNotifierLayout;
        if (sizeNotifierFrameLayout != null) {
            sizeNotifierFrameLayout.setDelegate((SizeNotifierFrameLayout.SizeNotifierFrameLayoutDelegate) null);
        }
    }

    public void updateColors() {
        if (this.currentStyle == 0) {
            this.editText.setHintTextColor(getThemedColor("windowBackgroundWhiteHintText"));
            this.editText.setCursorColor(getThemedColor("windowBackgroundWhiteBlackText"));
            this.editText.setTextColor(getThemedColor("windowBackgroundWhiteBlackText"));
        } else {
            this.editText.setHintTextColor(getThemedColor("dialogTextHint"));
            this.editText.setTextColor(getThemedColor("dialogTextBlack"));
        }
        this.emojiIconDrawable.setColorFilter(new PorterDuffColorFilter(getThemedColor("chat_messagePanelIcons"), PorterDuff.Mode.MULTIPLY));
        EmojiView emojiView2 = this.emojiView;
        if (emojiView2 != null) {
            emojiView2.updateColors();
        }
    }

    public void setMaxLines(int value) {
        this.editText.setMaxLines(value);
    }

    public int length() {
        return this.editText.length();
    }

    public void setFilters(InputFilter[] filters) {
        this.editText.setFilters(filters);
    }

    public Editable getText() {
        return this.editText.getText();
    }

    public void setHint(CharSequence hint) {
        this.editText.setHint(hint);
    }

    public void setText(CharSequence text) {
        this.editText.setText(text);
    }

    public void setSelection(int selection) {
        this.editText.setSelection(selection);
    }

    public void hidePopup(boolean byBackButton) {
        EmojiView emojiView2;
        if (isPopupShowing()) {
            showPopup(0);
        }
        if (!byBackButton) {
            return;
        }
        if (!SharedConfig.smoothKeyboard || (emojiView2 = this.emojiView) == null || emojiView2.getVisibility() != 0 || this.waitingForKeyboardOpen) {
            hideEmojiView();
            return;
        }
        int height = this.emojiView.getMeasuredHeight();
        ValueAnimator animator = ValueAnimator.ofFloat(new float[]{0.0f, (float) height});
        animator.addUpdateListener(new EditTextEmoji$$ExternalSyntheticLambda1(this, height));
        this.isAnimatePopupClosing = true;
        animator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                boolean unused = EditTextEmoji.this.isAnimatePopupClosing = false;
                EditTextEmoji.this.emojiView.setTranslationY(0.0f);
                EditTextEmoji.this.bottomPanelTranslationY(0.0f);
                EditTextEmoji.this.hideEmojiView();
            }
        });
        animator.setDuration(250);
        animator.setInterpolator(AdjustPanLayoutHelper.keyboardInterpolator);
        animator.start();
    }

    /* renamed from: lambda$hidePopup$1$org-telegram-ui-Components-EditTextEmoji  reason: not valid java name */
    public /* synthetic */ void m3951lambda$hidePopup$1$orgtelegramuiComponentsEditTextEmoji(int height, ValueAnimator animation) {
        float v = ((Float) animation.getAnimatedValue()).floatValue();
        this.emojiView.setTranslationY(v);
        bottomPanelTranslationY(v - ((float) height));
    }

    /* access modifiers changed from: protected */
    public void bottomPanelTranslationY(float translation) {
    }

    public void openKeyboard() {
        AndroidUtilities.showKeyboard(this.editText);
    }

    public void closeKeyboard() {
        AndroidUtilities.hideKeyboard(this.editText);
    }

    public boolean isPopupShowing() {
        return this.emojiViewVisible;
    }

    public boolean isKeyboardVisible() {
        return this.keyboardVisible;
    }

    /* access modifiers changed from: protected */
    public void openKeyboardInternal() {
        showPopup((AndroidUtilities.usingHardwareInput || this.isPaused) ? 0 : 2);
        this.editText.requestFocus();
        AndroidUtilities.showKeyboard(this.editText);
        if (this.isPaused) {
            this.showKeyboardOnResume = true;
        } else if (!AndroidUtilities.usingHardwareInput && !this.keyboardVisible && !AndroidUtilities.isInMultiwindow && !AndroidUtilities.isTablet()) {
            this.waitingForKeyboardOpen = true;
            AndroidUtilities.cancelRunOnUIThread(this.openKeyboardRunnable);
            AndroidUtilities.runOnUIThread(this.openKeyboardRunnable, 100);
        }
    }

    /* access modifiers changed from: private */
    public void showPopup(int show) {
        if (show == 1) {
            EmojiView emojiView2 = this.emojiView;
            boolean emojiWasVisible = emojiView2 != null && emojiView2.getVisibility() == 0;
            if (this.emojiView == null) {
                createEmojiView();
            }
            this.emojiView.setVisibility(0);
            this.emojiViewVisible = true;
            View currentView = this.emojiView;
            if (this.keyboardHeight <= 0) {
                if (AndroidUtilities.isTablet()) {
                    this.keyboardHeight = AndroidUtilities.dp(150.0f);
                } else {
                    this.keyboardHeight = MessagesController.getGlobalEmojiSettings().getInt("kbd_height", AndroidUtilities.dp(200.0f));
                }
            }
            if (this.keyboardHeightLand <= 0) {
                if (AndroidUtilities.isTablet()) {
                    this.keyboardHeightLand = AndroidUtilities.dp(150.0f);
                } else {
                    this.keyboardHeightLand = MessagesController.getGlobalEmojiSettings().getInt("kbd_height_land3", AndroidUtilities.dp(200.0f));
                }
            }
            int currentHeight = AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y ? this.keyboardHeightLand : this.keyboardHeight;
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) currentView.getLayoutParams();
            layoutParams.height = currentHeight;
            currentView.setLayoutParams(layoutParams);
            if (!AndroidUtilities.isInMultiwindow && !AndroidUtilities.isTablet()) {
                AndroidUtilities.hideKeyboard(this.editText);
            }
            SizeNotifierFrameLayout sizeNotifierFrameLayout = this.sizeNotifierLayout;
            if (sizeNotifierFrameLayout != null) {
                this.emojiPadding = currentHeight;
                sizeNotifierFrameLayout.requestLayout();
                this.emojiIconDrawable.setIcon(NUM, true);
                onWindowSizeChanged();
            }
            if (!this.keyboardVisible && !emojiWasVisible && SharedConfig.smoothKeyboard) {
                ValueAnimator animator = ValueAnimator.ofFloat(new float[]{(float) this.emojiPadding, 0.0f});
                animator.addUpdateListener(new EditTextEmoji$$ExternalSyntheticLambda0(this));
                animator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        EditTextEmoji.this.emojiView.setTranslationY(0.0f);
                        EditTextEmoji.this.bottomPanelTranslationY(0.0f);
                    }
                });
                animator.setDuration(250);
                animator.setInterpolator(AdjustPanLayoutHelper.keyboardInterpolator);
                animator.start();
                return;
            }
            return;
        }
        if (this.emojiButton != null) {
            if (this.currentStyle == 0) {
                this.emojiIconDrawable.setIcon(NUM, true);
            } else {
                this.emojiIconDrawable.setIcon(NUM, true);
            }
        }
        if (this.emojiView != null) {
            this.emojiViewVisible = false;
            if (AndroidUtilities.usingHardwareInput || AndroidUtilities.isInMultiwindow) {
                this.emojiView.setVisibility(8);
            }
        }
        SizeNotifierFrameLayout sizeNotifierFrameLayout2 = this.sizeNotifierLayout;
        if (sizeNotifierFrameLayout2 != null) {
            if (show == 0) {
                this.emojiPadding = 0;
            }
            sizeNotifierFrameLayout2.requestLayout();
            onWindowSizeChanged();
        }
    }

    /* renamed from: lambda$showPopup$2$org-telegram-ui-Components-EditTextEmoji  reason: not valid java name */
    public /* synthetic */ void m3953lambda$showPopup$2$orgtelegramuiComponentsEditTextEmoji(ValueAnimator animation) {
        float v = ((Float) animation.getAnimatedValue()).floatValue();
        this.emojiView.setTranslationY(v);
        bottomPanelTranslationY(v);
    }

    private void onWindowSizeChanged() {
        int size = this.sizeNotifierLayout.getHeight();
        if (!this.keyboardVisible) {
            size -= this.emojiPadding;
        }
        EditTextEmojiDelegate editTextEmojiDelegate = this.delegate;
        if (editTextEmojiDelegate != null) {
            editTextEmojiDelegate.onWindowSizeChanged(size);
        }
    }

    private void createEmojiView() {
        if (this.emojiView == null) {
            EmojiView emojiView2 = new EmojiView(false, false, getContext(), false, (TLRPC.ChatFull) null, (ViewGroup) null, this.resourcesProvider);
            this.emojiView = emojiView2;
            emojiView2.setVisibility(8);
            if (AndroidUtilities.isTablet()) {
                this.emojiView.setForseMultiwindowLayout(true);
            }
            this.emojiView.setDelegate(new EmojiView.EmojiViewDelegate() {
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
                    if (EditTextEmoji.this.editText.length() == 0) {
                        return false;
                    }
                    EditTextEmoji.this.editText.dispatchKeyEvent(new KeyEvent(0, 67));
                    return true;
                }

                public void onEmojiSelected(String symbol) {
                    int i = EditTextEmoji.this.editText.getSelectionEnd();
                    if (i < 0) {
                        i = 0;
                    }
                    try {
                        int unused = EditTextEmoji.this.innerTextChange = 2;
                        CharSequence localCharSequence = Emoji.replaceEmoji(symbol, EditTextEmoji.this.editText.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
                        EditTextEmoji.this.editText.setText(EditTextEmoji.this.editText.getText().insert(i, localCharSequence));
                        int j = localCharSequence.length() + i;
                        EditTextEmoji.this.editText.setSelection(j, j);
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    } catch (Throwable th) {
                        int unused2 = EditTextEmoji.this.innerTextChange = 0;
                        throw th;
                    }
                    int unused3 = EditTextEmoji.this.innerTextChange = 0;
                }

                public void onClearEmojiRecent() {
                    AlertDialog.Builder builder = new AlertDialog.Builder(EditTextEmoji.this.getContext(), EditTextEmoji.this.resourcesProvider);
                    builder.setTitle(LocaleController.getString("AppName", NUM));
                    builder.setMessage(LocaleController.getString("ClearRecentEmoji", NUM));
                    builder.setPositiveButton(LocaleController.getString("ClearButton", NUM).toUpperCase(), new EditTextEmoji$5$$ExternalSyntheticLambda0(this));
                    builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                    if (EditTextEmoji.this.parentFragment != null) {
                        EditTextEmoji.this.parentFragment.showDialog(builder.create());
                    } else {
                        builder.show();
                    }
                }

                /* renamed from: lambda$onClearEmojiRecent$0$org-telegram-ui-Components-EditTextEmoji$5  reason: not valid java name */
                public /* synthetic */ void m3954xad0var_c7(DialogInterface dialogInterface, int i) {
                    EditTextEmoji.this.emojiView.clearRecentEmoji();
                }
            });
            this.sizeNotifierLayout.addView(this.emojiView);
        }
    }

    public boolean isPopupView(View view) {
        return view == this.emojiView;
    }

    public int getEmojiPadding() {
        return this.emojiPadding;
    }

    public void onSizeChanged(int height, boolean isWidthGreater) {
        boolean z;
        if (height > AndroidUtilities.dp(50.0f) && this.keyboardVisible && !AndroidUtilities.isInMultiwindow && !AndroidUtilities.isTablet()) {
            if (isWidthGreater) {
                this.keyboardHeightLand = height;
                MessagesController.getGlobalEmojiSettings().edit().putInt("kbd_height_land3", this.keyboardHeightLand).commit();
            } else {
                this.keyboardHeight = height;
                MessagesController.getGlobalEmojiSettings().edit().putInt("kbd_height", this.keyboardHeight).commit();
            }
        }
        if (isPopupShowing()) {
            int newHeight = isWidthGreater ? this.keyboardHeightLand : this.keyboardHeight;
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
        boolean z2 = this.editText.isFocused() && height > 0;
        this.keyboardVisible = z2;
        if (z2 && isPopupShowing()) {
            showPopup(0);
        }
        if (this.emojiPadding != 0 && !(z = this.keyboardVisible) && z != oldValue && !isPopupShowing()) {
            this.emojiPadding = 0;
            this.sizeNotifierLayout.requestLayout();
        }
        if (this.keyboardVisible && this.waitingForKeyboardOpen) {
            this.waitingForKeyboardOpen = false;
            AndroidUtilities.cancelRunOnUIThread(this.openKeyboardRunnable);
        }
        onWindowSizeChanged();
    }

    public EditTextCaption getEditText() {
        return this.editText;
    }

    private int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }
}
