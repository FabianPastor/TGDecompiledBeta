package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableString;
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
import org.telegram.tgnet.TLRPC$ChatFull;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$InputStickerSet;
import org.telegram.tgnet.TLRPC$StickerSet;
import org.telegram.tgnet.TLRPC$StickerSetCovered;
import org.telegram.ui.ActionBar.AdjustPanLayoutHelper;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.EmojiView;
import org.telegram.ui.Components.Premium.PremiumFeatureBottomSheet;
import org.telegram.ui.Components.SizeNotifierFrameLayout;

public class EditTextEmoji extends FrameLayout implements NotificationCenter.NotificationCenterDelegate, SizeNotifierFrameLayout.SizeNotifierFrameLayoutDelegate {
    AdjustPanLayoutHelper adjustPanLayoutHelper;
    private boolean allowAnimatedEmoji;
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

    /* access modifiers changed from: protected */
    public void bottomPanelTranslationY(float f) {
    }

    /* access modifiers changed from: protected */
    public void closeParent() {
    }

    /* access modifiers changed from: protected */
    public void onLineCountChanged(int i, int i2) {
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

    public EditTextEmoji(Context context, SizeNotifierFrameLayout sizeNotifierFrameLayout, BaseFragment baseFragment, int i, boolean z) {
        this(context, sizeNotifierFrameLayout, baseFragment, i, z, (Theme.ResourcesProvider) null);
    }

    public EditTextEmoji(Context context, SizeNotifierFrameLayout sizeNotifierFrameLayout, BaseFragment baseFragment, int i, boolean z, Theme.ResourcesProvider resourcesProvider2) {
        super(context);
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
        this.allowAnimatedEmoji = z;
        this.resourcesProvider = resourcesProvider2;
        this.currentStyle = i;
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiLoaded);
        this.parentFragment = baseFragment;
        this.sizeNotifierLayout = sizeNotifierFrameLayout;
        sizeNotifierFrameLayout.setDelegate(this);
        AnonymousClass2 r11 = new EditTextCaption(context, resourcesProvider2) {
            public boolean onTouchEvent(MotionEvent motionEvent) {
                if (EditTextEmoji.this.isPopupShowing() && motionEvent.getAction() == 0) {
                    EditTextEmoji.this.showPopup(AndroidUtilities.usingHardwareInput ? 0 : 2);
                    EditTextEmoji.this.openKeyboardInternal();
                }
                if (motionEvent.getAction() == 0) {
                    requestFocus();
                    if (!AndroidUtilities.showKeyboard(this)) {
                        clearFocus();
                        requestFocus();
                    }
                }
                try {
                    return super.onTouchEvent(motionEvent);
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                    return false;
                }
            }

            /* access modifiers changed from: protected */
            public void onLineCountChanged(int i, int i2) {
                EditTextEmoji.this.onLineCountChanged(i, i2);
            }
        };
        this.editText = r11;
        r11.setTextSize(1, 18.0f);
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
        float f = 11.0f;
        if (i == 0) {
            this.editText.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            this.editText.setBackground((Drawable) null);
            this.editText.setLineColors(getThemedColor("windowBackgroundWhiteInputField"), getThemedColor("windowBackgroundWhiteInputFieldActivated"), getThemedColor("windowBackgroundWhiteRedText3"));
            this.editText.setHintTextColor(getThemedColor("windowBackgroundWhiteHintText"));
            this.editText.setTextColor(getThemedColor("windowBackgroundWhiteBlackText"));
            this.editText.setPadding(LocaleController.isRTL ? AndroidUtilities.dp(40.0f) : 0, 0, LocaleController.isRTL ? 0 : AndroidUtilities.dp(40.0f), AndroidUtilities.dp(8.0f));
            EditTextCaption editTextCaption3 = this.editText;
            boolean z2 = LocaleController.isRTL;
            addView(editTextCaption3, LayoutHelper.createFrame(-1, -2.0f, 19, z2 ? 11.0f : 0.0f, 1.0f, z2 ? 0.0f : f, 0.0f));
        } else {
            this.editText.setGravity(19);
            this.editText.setHintTextColor(getThemedColor("dialogTextHint"));
            this.editText.setTextColor(getThemedColor("dialogTextBlack"));
            this.editText.setBackground((Drawable) null);
            this.editText.setPadding(0, AndroidUtilities.dp(11.0f), 0, AndroidUtilities.dp(12.0f));
            addView(this.editText, LayoutHelper.createFrame(-1, -1.0f, 19, 48.0f, 0.0f, 0.0f, 0.0f));
        }
        ImageView imageView = new ImageView(context);
        this.emojiButton = imageView;
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        ImageView imageView2 = this.emojiButton;
        ReplaceableIconDrawable replaceableIconDrawable = new ReplaceableIconDrawable(context);
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

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(View view) {
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

    public void setSizeNotifierLayout(SizeNotifierFrameLayout sizeNotifierFrameLayout) {
        this.sizeNotifierLayout = sizeNotifierFrameLayout;
        sizeNotifierFrameLayout.setDelegate(this);
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.emojiLoaded) {
            EmojiView emojiView2 = this.emojiView;
            if (emojiView2 != null) {
                emojiView2.invalidateViews();
            }
            EditTextCaption editTextCaption = this.editText;
            if (editTextCaption != null) {
                int currentTextColor = editTextCaption.getCurrentTextColor();
                this.editText.setTextColor(-1);
                this.editText.setTextColor(currentTextColor);
            }
        }
    }

    public void setEnabled(boolean z) {
        this.editText.setEnabled(z);
        this.emojiButton.setVisibility(z ? 0 : 8);
        if (z) {
            this.editText.setPadding(LocaleController.isRTL ? AndroidUtilities.dp(40.0f) : 0, 0, LocaleController.isRTL ? 0 : AndroidUtilities.dp(40.0f), AndroidUtilities.dp(8.0f));
        } else {
            this.editText.setPadding(0, 0, 0, AndroidUtilities.dp(8.0f));
        }
    }

    public void setFocusable(boolean z) {
        this.editText.setFocusable(z);
    }

    public void hideEmojiView() {
        EmojiView emojiView2;
        if (!(this.emojiViewVisible || (emojiView2 = this.emojiView) == null || emojiView2.getVisibility() == 8)) {
            this.emojiView.setVisibility(8);
        }
        this.emojiPadding = 0;
    }

    public EmojiView getEmojiView() {
        return this.emojiView;
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

    public void setMaxLines(int i) {
        this.editText.setMaxLines(i);
    }

    public int length() {
        return this.editText.length();
    }

    public void setFilters(InputFilter[] inputFilterArr) {
        this.editText.setFilters(inputFilterArr);
    }

    public Editable getText() {
        return this.editText.getText();
    }

    public void setHint(CharSequence charSequence) {
        this.editText.setHint(charSequence);
    }

    public void setText(CharSequence charSequence) {
        this.editText.setText(charSequence);
    }

    public void setSelection(int i) {
        this.editText.setSelection(i);
    }

    public void hidePopup(boolean z) {
        EmojiView emojiView2;
        if (isPopupShowing()) {
            showPopup(0);
        }
        if (!z) {
            return;
        }
        if (!SharedConfig.smoothKeyboard || (emojiView2 = this.emojiView) == null || emojiView2.getVisibility() != 0 || this.waitingForKeyboardOpen) {
            hideEmojiView();
            return;
        }
        int measuredHeight = this.emojiView.getMeasuredHeight();
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, (float) measuredHeight});
        ofFloat.addUpdateListener(new EditTextEmoji$$ExternalSyntheticLambda1(this, measuredHeight));
        this.isAnimatePopupClosing = true;
        ofFloat.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                boolean unused = EditTextEmoji.this.isAnimatePopupClosing = false;
                EditTextEmoji.this.emojiView.setTranslationY(0.0f);
                EditTextEmoji.this.bottomPanelTranslationY(0.0f);
                EditTextEmoji.this.hideEmojiView();
            }
        });
        ofFloat.setDuration(250);
        ofFloat.setInterpolator(AdjustPanLayoutHelper.keyboardInterpolator);
        ofFloat.start();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$hidePopup$1(int i, ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.emojiView.setTranslationY(floatValue);
        bottomPanelTranslationY(floatValue - ((float) i));
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

    /* access modifiers changed from: protected */
    public void showPopup(int i) {
        if (i == 1) {
            EmojiView emojiView2 = this.emojiView;
            boolean z = emojiView2 != null && emojiView2.getVisibility() == 0;
            if (this.emojiView == null) {
                createEmojiView();
            }
            this.emojiView.setVisibility(0);
            this.emojiViewVisible = true;
            EmojiView emojiView3 = this.emojiView;
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
            Point point = AndroidUtilities.displaySize;
            int i2 = point.x > point.y ? this.keyboardHeightLand : this.keyboardHeight;
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) emojiView3.getLayoutParams();
            layoutParams.height = i2;
            emojiView3.setLayoutParams(layoutParams);
            if (!AndroidUtilities.isInMultiwindow && !AndroidUtilities.isTablet()) {
                AndroidUtilities.hideKeyboard(this.editText);
            }
            SizeNotifierFrameLayout sizeNotifierFrameLayout = this.sizeNotifierLayout;
            if (sizeNotifierFrameLayout != null) {
                this.emojiPadding = i2;
                sizeNotifierFrameLayout.requestLayout();
                this.emojiIconDrawable.setIcon(NUM, true);
                onWindowSizeChanged();
            }
            if (!this.keyboardVisible && !z && SharedConfig.smoothKeyboard) {
                ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{(float) this.emojiPadding, 0.0f});
                ofFloat.addUpdateListener(new EditTextEmoji$$ExternalSyntheticLambda0(this));
                ofFloat.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        EditTextEmoji.this.emojiView.setTranslationY(0.0f);
                        EditTextEmoji.this.bottomPanelTranslationY(0.0f);
                    }
                });
                ofFloat.setDuration(250);
                ofFloat.setInterpolator(AdjustPanLayoutHelper.keyboardInterpolator);
                ofFloat.start();
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
        EmojiView emojiView4 = this.emojiView;
        if (emojiView4 != null) {
            this.emojiViewVisible = false;
            if (AndroidUtilities.usingHardwareInput || AndroidUtilities.isInMultiwindow) {
                emojiView4.setVisibility(8);
            }
        }
        SizeNotifierFrameLayout sizeNotifierFrameLayout2 = this.sizeNotifierLayout;
        if (sizeNotifierFrameLayout2 != null) {
            if (i == 0) {
                this.emojiPadding = 0;
            }
            sizeNotifierFrameLayout2.requestLayout();
            onWindowSizeChanged();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showPopup$2(ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.emojiView.setTranslationY(floatValue);
        bottomPanelTranslationY(floatValue);
    }

    private void onWindowSizeChanged() {
        int height = this.sizeNotifierLayout.getHeight();
        if (!this.keyboardVisible) {
            height -= this.emojiPadding;
        }
        EditTextEmojiDelegate editTextEmojiDelegate = this.delegate;
        if (editTextEmojiDelegate != null) {
            editTextEmojiDelegate.onWindowSizeChanged(height);
        }
    }

    /* access modifiers changed from: protected */
    public void createEmojiView() {
        if (this.emojiView == null) {
            EmojiView emojiView2 = new EmojiView(this.parentFragment, this.allowAnimatedEmoji, false, false, getContext(), false, (TLRPC$ChatFull) null, (ViewGroup) null, this.resourcesProvider);
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

                public /* synthetic */ boolean isUserSelf() {
                    return EmojiView.EmojiViewDelegate.CC.$default$isUserSelf(this);
                }

                public /* synthetic */ void onEmojiSettingsClick() {
                    EmojiView.EmojiViewDelegate.CC.$default$onEmojiSettingsClick(this);
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
                    if (EditTextEmoji.this.editText.length() == 0) {
                        return false;
                    }
                    EditTextEmoji.this.editText.dispatchKeyEvent(new KeyEvent(0, 67));
                    return true;
                }

                public void onAnimatedEmojiUnlockClick() {
                    BaseFragment access$700 = EditTextEmoji.this.parentFragment;
                    if (access$700 == null) {
                        new PremiumFeatureBottomSheet(new BaseFragment() {
                            public int getCurrentAccount() {
                                return this.currentAccount;
                            }

                            public Context getContext() {
                                return EditTextEmoji.this.getContext();
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
                                return new Dialog(EditTextEmoji.this.getContext()) {
                                    public void dismiss() {
                                        EditTextEmoji.this.hidePopup(false);
                                        EditTextEmoji.this.closeParent();
                                    }
                                };
                            }
                        }, 11, false).show();
                    } else {
                        access$700.showDialog(new PremiumFeatureBottomSheet(access$700, 11, false));
                    }
                }

                public void onEmojiSelected(String str) {
                    int selectionEnd = EditTextEmoji.this.editText.getSelectionEnd();
                    if (selectionEnd < 0) {
                        selectionEnd = 0;
                    }
                    try {
                        int unused = EditTextEmoji.this.innerTextChange = 2;
                        CharSequence replaceEmoji = Emoji.replaceEmoji(str, EditTextEmoji.this.editText.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
                        EditTextEmoji.this.editText.setText(EditTextEmoji.this.editText.getText().insert(selectionEnd, replaceEmoji));
                        int length = selectionEnd + replaceEmoji.length();
                        EditTextEmoji.this.editText.setSelection(length, length);
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    } catch (Throwable th) {
                        int unused2 = EditTextEmoji.this.innerTextChange = 0;
                        throw th;
                    }
                    int unused3 = EditTextEmoji.this.innerTextChange = 0;
                }

                public void onCustomEmojiSelected(long j, TLRPC$Document tLRPC$Document, String str) {
                    AnimatedEmojiSpan animatedEmojiSpan;
                    int selectionEnd = EditTextEmoji.this.editText.getSelectionEnd();
                    if (selectionEnd < 0) {
                        selectionEnd = 0;
                    }
                    try {
                        int unused = EditTextEmoji.this.innerTextChange = 2;
                        SpannableString spannableString = new SpannableString(str);
                        if (tLRPC$Document != null) {
                            animatedEmojiSpan = new AnimatedEmojiSpan(tLRPC$Document, EditTextEmoji.this.editText.getPaint().getFontMetricsInt());
                        } else {
                            animatedEmojiSpan = new AnimatedEmojiSpan(j, EditTextEmoji.this.editText.getPaint().getFontMetricsInt());
                        }
                        spannableString.setSpan(animatedEmojiSpan, 0, spannableString.length(), 33);
                        EditTextEmoji.this.editText.setText(EditTextEmoji.this.editText.getText().insert(selectionEnd, spannableString));
                        int length = selectionEnd + spannableString.length();
                        EditTextEmoji.this.editText.setSelection(length, length);
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
                    builder.setTitle(LocaleController.getString("ClearRecentEmojiTitle", NUM));
                    builder.setMessage(LocaleController.getString("ClearRecentEmojiText", NUM));
                    builder.setPositiveButton(LocaleController.getString("ClearButton", NUM).toUpperCase(), new EditTextEmoji$5$$ExternalSyntheticLambda0(this));
                    builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                    if (EditTextEmoji.this.parentFragment != null) {
                        EditTextEmoji.this.parentFragment.showDialog(builder.create());
                    } else {
                        builder.show();
                    }
                }

                /* access modifiers changed from: private */
                public /* synthetic */ void lambda$onClearEmojiRecent$0(DialogInterface dialogInterface, int i) {
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

    public void onSizeChanged(int i, boolean z) {
        boolean z2;
        if (i > AndroidUtilities.dp(50.0f) && this.keyboardVisible && !AndroidUtilities.isInMultiwindow && !AndroidUtilities.isTablet()) {
            if (z) {
                this.keyboardHeightLand = i;
                MessagesController.getGlobalEmojiSettings().edit().putInt("kbd_height_land3", this.keyboardHeightLand).commit();
            } else {
                this.keyboardHeight = i;
                MessagesController.getGlobalEmojiSettings().edit().putInt("kbd_height", this.keyboardHeight).commit();
            }
        }
        if (isPopupShowing()) {
            int i2 = z ? this.keyboardHeightLand : this.keyboardHeight;
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.emojiView.getLayoutParams();
            int i3 = layoutParams.width;
            int i4 = AndroidUtilities.displaySize.x;
            if (!(i3 == i4 && layoutParams.height == i2)) {
                layoutParams.width = i4;
                layoutParams.height = i2;
                this.emojiView.setLayoutParams(layoutParams);
                SizeNotifierFrameLayout sizeNotifierFrameLayout = this.sizeNotifierLayout;
                if (sizeNotifierFrameLayout != null) {
                    this.emojiPadding = layoutParams.height;
                    sizeNotifierFrameLayout.requestLayout();
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
        boolean z4 = this.editText.isFocused() && i > 0;
        this.keyboardVisible = z4;
        if (z4 && isPopupShowing()) {
            showPopup(0);
        }
        if (this.emojiPadding != 0 && !(z2 = this.keyboardVisible) && z2 != z3 && !isPopupShowing()) {
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

    private int getThemedColor(String str) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(str) : null;
        return color != null ? color.intValue() : Theme.getColor(str);
    }
}
