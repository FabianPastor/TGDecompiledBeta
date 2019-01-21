package org.telegram.ui.Components;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.text.Editable;
import android.text.InputFilter;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.R;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.InputStickerSet;
import org.telegram.tgnet.TLRPC.StickerSet;
import org.telegram.tgnet.TLRPC.StickerSetCovered;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.EmojiView.Listener;
import org.telegram.ui.Components.SizeNotifierFrameLayout.SizeNotifierFrameLayoutDelegate;

public class EditTextEmoji extends FrameLayout implements NotificationCenterDelegate, SizeNotifierFrameLayoutDelegate {
    private EditTextEmojiDelegate delegate;
    private boolean destroyed;
    private EditTextBoldCursor editText;
    private ImageView emojiButton;
    private int emojiPadding;
    private EmojiView emojiView;
    private int innerTextChange;
    private boolean isPaused = true;
    private int keyboardHeight;
    private int keyboardHeightLand;
    private boolean keyboardVisible;
    private int lastSizeChangeValue1;
    private boolean lastSizeChangeValue2;
    private Runnable openKeyboardRunnable = new Runnable() {
        public void run() {
            if (!EditTextEmoji.this.destroyed && EditTextEmoji.this.editText != null && EditTextEmoji.this.waitingForKeyboardOpen && !EditTextEmoji.this.keyboardVisible && !AndroidUtilities.usingHardwareInput && !AndroidUtilities.isInMultiwindow && AndroidUtilities.isTablet()) {
                EditTextEmoji.this.editText.requestFocus();
                AndroidUtilities.showKeyboard(EditTextEmoji.this.editText);
                AndroidUtilities.cancelRunOnUIThread(EditTextEmoji.this.openKeyboardRunnable);
                AndroidUtilities.runOnUIThread(EditTextEmoji.this.openKeyboardRunnable, 100);
            }
        }
    };
    private Activity parentActivity;
    private BaseFragment parentFragment;
    private boolean showKeyboardOnResume;
    private SizeNotifierFrameLayout sizeNotifierLayout;
    private boolean waitingForKeyboardOpen;

    public interface EditTextEmojiDelegate {
        void onWindowSizeChanged(int i);
    }

    public EditTextEmoji(Activity context, SizeNotifierFrameLayout parent, BaseFragment fragment) {
        int dp;
        float f;
        float f2 = 11.0f;
        super(context);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiDidLoad);
        this.parentActivity = context;
        this.parentFragment = fragment;
        this.sizeNotifierLayout = parent;
        this.sizeNotifierLayout.setDelegate(this);
        this.editText = new EditTextBoldCursor(context) {
            public boolean onTouchEvent(MotionEvent event) {
                boolean z = false;
                if (EditTextEmoji.this.isPopupShowing() && event.getAction() == 0) {
                    EditTextEmoji.this.showPopup(AndroidUtilities.usingHardwareInput ? z : 2);
                    EditTextEmoji.this.openKeyboardInternal();
                }
                try {
                    return super.onTouchEvent(event);
                } catch (Throwable e) {
                    FileLog.e(e);
                    return z;
                }
            }
        };
        this.editText.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        this.editText.setTextSize(1, 16.0f);
        this.editText.setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
        this.editText.setBackgroundDrawable(Theme.createEditTextDrawable(context, false));
        this.editText.setImeOptions(NUM);
        this.editText.setInputType(16385);
        EditTextBoldCursor editTextBoldCursor = this.editText;
        if (LocaleController.isRTL) {
            dp = AndroidUtilities.dp(40.0f);
        } else {
            dp = 0;
        }
        editTextBoldCursor.setPadding(dp, 0, LocaleController.isRTL ? 0 : AndroidUtilities.dp(40.0f), AndroidUtilities.dp(8.0f));
        this.editText.setFocusable(this.editText.isEnabled());
        this.editText.setCursorColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.editText.setCursorSize(AndroidUtilities.dp(20.0f));
        this.editText.setCursorWidth(1.5f);
        this.editText.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        View view = this.editText;
        if (LocaleController.isRTL) {
            f = 11.0f;
        } else {
            f = 0.0f;
        }
        if (LocaleController.isRTL) {
            f2 = 0.0f;
        }
        addView(view, LayoutHelper.createFrame(-1, -2.0f, 19, f, 1.0f, f2, 0.0f));
        this.emojiButton = new ImageView(context);
        this.emojiButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_messagePanelIcons"), Mode.MULTIPLY));
        this.emojiButton.setScaleType(ScaleType.CENTER_INSIDE);
        this.emojiButton.setImageResource(R.drawable.ic_smile_small);
        this.emojiButton.setPadding(0, 0, 0, AndroidUtilities.dp(7.0f));
        addView(this.emojiButton, LayoutHelper.createFrame(48, 48.0f, (LocaleController.isRTL ? 3 : 5) | 16, 0.0f, 0.0f, 0.0f, 0.0f));
        this.emojiButton.setOnClickListener(new EditTextEmoji$$Lambda$0(this));
    }

    final /* synthetic */ void lambda$new$0$EditTextEmoji(View view) {
        boolean z = true;
        if (!this.emojiButton.isEnabled()) {
            return;
        }
        if (isPopupShowing()) {
            openKeyboardInternal();
            return;
        }
        showPopup(1);
        EmojiView emojiView = this.emojiView;
        if (this.editText.length() <= 0) {
            z = false;
        }
        emojiView.onOpen(z);
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.emojiDidLoad && this.emojiView != null) {
            this.emojiView.invalidateViews();
        }
    }

    public void setEnabled(boolean enabled) {
        this.editText.setEnabled(enabled);
        this.emojiButton.setVisibility(enabled ? 0 : 8);
        if (enabled) {
            int dp;
            EditTextBoldCursor editTextBoldCursor = this.editText;
            if (LocaleController.isRTL) {
                dp = AndroidUtilities.dp(40.0f);
            } else {
                dp = 0;
            }
            editTextBoldCursor.setPadding(dp, 0, LocaleController.isRTL ? 0 : AndroidUtilities.dp(40.0f), AndroidUtilities.dp(8.0f));
            return;
        }
        this.editText.setPadding(0, 0, 0, AndroidUtilities.dp(8.0f));
    }

    public void setFocusable(boolean focusable) {
        this.editText.setFocusable(focusable);
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
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiDidLoad);
        if (this.emojiView != null) {
            this.emojiView.onDestroy();
        }
        if (this.sizeNotifierLayout != null) {
            this.sizeNotifierLayout.setDelegate(null);
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
        if (isPopupShowing()) {
            showPopup(0);
        }
    }

    public void openKeyboard() {
        AndroidUtilities.showKeyboard(this.editText);
    }

    public void closeKeyboard() {
        AndroidUtilities.hideKeyboard(this.editText);
    }

    public boolean isPopupShowing() {
        return this.emojiView != null && this.emojiView.getVisibility() == 0;
    }

    public boolean isKeyboardVisible() {
        return this.keyboardVisible;
    }

    private void openKeyboardInternal() {
        int i = (AndroidUtilities.usingHardwareInput || this.isPaused) ? 0 : 2;
        showPopup(i);
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

    private void showPopup(int show) {
        if (show == 1) {
            if (this.emojiView == null) {
                createEmojiView();
            }
            this.emojiView.setVisibility(0);
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
            LayoutParams layoutParams = (LayoutParams) currentView.getLayoutParams();
            layoutParams.height = currentHeight;
            currentView.setLayoutParams(layoutParams);
            if (!(AndroidUtilities.isInMultiwindow || AndroidUtilities.isTablet())) {
                AndroidUtilities.hideKeyboard(this.editText);
            }
            if (this.sizeNotifierLayout != null) {
                this.emojiPadding = currentHeight;
                this.sizeNotifierLayout.requestLayout();
                this.emojiButton.setImageResource(R.drawable.ic_msg_panel_kb);
                onWindowSizeChanged();
                return;
            }
            return;
        }
        if (this.emojiButton != null) {
            this.emojiButton.setImageResource(R.drawable.ic_smile_small);
        }
        if (this.emojiView != null) {
            this.emojiView.setVisibility(8);
        }
        if (this.sizeNotifierLayout != null) {
            if (show == 0) {
                this.emojiPadding = 0;
            }
            this.sizeNotifierLayout.requestLayout();
            onWindowSizeChanged();
        }
    }

    private void onWindowSizeChanged() {
        int size = this.sizeNotifierLayout.getHeight();
        if (!this.keyboardVisible) {
            size -= this.emojiPadding;
        }
        if (this.delegate != null) {
            this.delegate.onWindowSizeChanged(size);
        }
    }

    private void createEmojiView() {
        if (this.emojiView == null) {
            this.emojiView = new EmojiView(false, false, this.parentActivity, null);
            this.emojiView.setVisibility(8);
            if (AndroidUtilities.isTablet()) {
                this.emojiView.setForseMultiwindowLayout(true);
            }
            this.emojiView.setListener(new Listener() {
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
                        EditTextEmoji.this.innerTextChange = 2;
                        CharSequence localCharSequence = Emoji.replaceEmoji(symbol, EditTextEmoji.this.editText.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
                        EditTextEmoji.this.editText.setText(EditTextEmoji.this.editText.getText().insert(i, localCharSequence));
                        int j = i + localCharSequence.length();
                        EditTextEmoji.this.editText.setSelection(j, j);
                    } catch (Throwable e) {
                        FileLog.e(e);
                    } finally {
                        EditTextEmoji.this.innerTextChange = 0;
                    }
                }

                public void onStickerSelected(Document sticker, Object parent) {
                }

                public void onStickersSettingsClick() {
                }

                public void onGifSelected(Document gif, Object parent) {
                }

                public void onGifTab(boolean opened) {
                }

                public void onStickersTab(boolean opened) {
                }

                public void onClearEmojiRecent() {
                    if (EditTextEmoji.this.parentFragment != null && EditTextEmoji.this.parentActivity != null) {
                        Builder builder = new Builder(EditTextEmoji.this.parentActivity);
                        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                        builder.setMessage(LocaleController.getString("ClearRecentEmoji", R.string.ClearRecentEmoji));
                        builder.setPositiveButton(LocaleController.getString("ClearButton", R.string.ClearButton).toUpperCase(), new EditTextEmoji$3$$Lambda$0(this));
                        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                        EditTextEmoji.this.parentFragment.showDialog(builder.create());
                    }
                }

                final /* synthetic */ void lambda$onClearEmojiRecent$0$EditTextEmoji$3(DialogInterface dialogInterface, int i) {
                    EditTextEmoji.this.emojiView.clearRecentEmoji();
                }

                public void onShowStickerSet(StickerSet stickerSet, InputStickerSet inputStickerSet) {
                }

                public void onStickerSetAdd(StickerSetCovered stickerSet) {
                }

                public void onStickerSetRemove(StickerSetCovered stickerSet) {
                }

                public void onStickersGroupClick(int chatId) {
                }

                public void onSearchOpenClose(boolean open) {
                }

                public boolean isSearchOpened() {
                    return false;
                }

                public boolean isExpanded() {
                    return false;
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
            LayoutParams layoutParams = (LayoutParams) this.emojiView.getLayoutParams();
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
        this.keyboardVisible = height > 0;
        if (this.keyboardVisible && isPopupShowing()) {
            showPopup(0);
        }
        if (!(this.emojiPadding == 0 || this.keyboardVisible || this.keyboardVisible == oldValue || isPopupShowing())) {
            this.emojiPadding = 0;
            this.sizeNotifierLayout.requestLayout();
        }
        if (this.keyboardVisible && this.waitingForKeyboardOpen) {
            this.waitingForKeyboardOpen = false;
            AndroidUtilities.cancelRunOnUIThread(this.openKeyboardRunnable);
        }
        onWindowSizeChanged();
    }

    public EditTextBoldCursor getEditText() {
        return this.editText;
    }
}
