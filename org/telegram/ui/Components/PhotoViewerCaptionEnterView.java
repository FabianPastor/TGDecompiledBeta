package org.telegram.ui.Components;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Build.VERSION;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper.Callback;
import org.telegram.messenger.volley.DefaultRetryPolicy;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.StickerSetCovered;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.EmojiView.Listener;
import org.telegram.ui.Components.SizeNotifierFrameLayoutPhoto.SizeNotifierFrameLayoutPhotoDelegate;

public class PhotoViewerCaptionEnterView extends FrameLayout implements NotificationCenterDelegate, SizeNotifierFrameLayoutPhotoDelegate {
    private int audioInterfaceState;
    private final int captionMaxLength = Callback.DEFAULT_DRAG_ANIMATION_DURATION;
    private ActionMode currentActionMode;
    private PhotoViewerCaptionEnterViewDelegate delegate;
    private ImageView emojiButton;
    private int emojiPadding;
    private EmojiView emojiView;
    private boolean forceFloatingEmoji;
    private boolean innerTextChange;
    private int keyboardHeight;
    private int keyboardHeightLand;
    private boolean keyboardVisible;
    private int lastSizeChangeValue1;
    private boolean lastSizeChangeValue2;
    private EditText messageEditText;
    private AnimatorSet runningAnimation;
    private AnimatorSet runningAnimation2;
    private ObjectAnimator runningAnimationAudio;
    private int runningAnimationType;
    private SizeNotifierFrameLayoutPhoto sizeNotifierLayout;
    private View windowView;

    public interface PhotoViewerCaptionEnterViewDelegate {
        void onCaptionEnter();

        void onTextChanged(CharSequence charSequence);

        void onWindowSizeChanged(int i);
    }

    public PhotoViewerCaptionEnterView(Context context, SizeNotifierFrameLayoutPhoto parent, View window) {
        super(context);
        setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
        setFocusable(true);
        setFocusableInTouchMode(true);
        this.windowView = window;
        this.sizeNotifierLayout = parent;
        LinearLayout textFieldContainer = new LinearLayout(context);
        textFieldContainer.setOrientation(0);
        addView(textFieldContainer, LayoutHelper.createFrame(-1, -2.0f, 51, 2.0f, 0.0f, 0.0f, 0.0f));
        FrameLayout frameLayout = new FrameLayout(context);
        textFieldContainer.addView(frameLayout, LayoutHelper.createLinear(0, -2, (float) DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        this.emojiButton = new ImageView(context);
        this.emojiButton.setImageResource(R.drawable.ic_smile_w);
        this.emojiButton.setScaleType(ScaleType.CENTER_INSIDE);
        this.emojiButton.setPadding(AndroidUtilities.dp(4.0f), AndroidUtilities.dp(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT), 0, 0);
        frameLayout.addView(this.emojiButton, LayoutHelper.createFrame(48, 48, 83));
        this.emojiButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (PhotoViewerCaptionEnterView.this.isPopupShowing()) {
                    PhotoViewerCaptionEnterView.this.openKeyboardInternal();
                } else {
                    PhotoViewerCaptionEnterView.this.showPopup(1);
                }
            }
        });
        this.messageEditText = new EditText(context) {
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                try {
                    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                } catch (Throwable e) {
                    setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), AndroidUtilities.dp(51.0f));
                    FileLog.e("tmessages", e);
                }
            }
        };
        if (VERSION.SDK_INT >= 23 && this.windowView != null) {
            this.messageEditText.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    PhotoViewerCaptionEnterView.this.currentActionMode = mode;
                    return true;
                }

                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    if (VERSION.SDK_INT >= 23) {
                        PhotoViewerCaptionEnterView.this.fixActionMode(mode);
                    }
                    return true;
                }

                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    return false;
                }

                public void onDestroyActionMode(ActionMode mode) {
                    if (PhotoViewerCaptionEnterView.this.currentActionMode == mode) {
                        PhotoViewerCaptionEnterView.this.currentActionMode = null;
                    }
                }
            });
            this.messageEditText.setCustomInsertionActionModeCallback(new ActionMode.Callback() {
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    PhotoViewerCaptionEnterView.this.currentActionMode = mode;
                    return true;
                }

                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    if (VERSION.SDK_INT >= 23) {
                        PhotoViewerCaptionEnterView.this.fixActionMode(mode);
                    }
                    return true;
                }

                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    return false;
                }

                public void onDestroyActionMode(ActionMode mode) {
                    if (PhotoViewerCaptionEnterView.this.currentActionMode == mode) {
                        PhotoViewerCaptionEnterView.this.currentActionMode = null;
                    }
                }
            });
        }
        this.messageEditText.setHint(LocaleController.getString("AddCaption", R.string.AddCaption));
        this.messageEditText.setImeOptions(268435456);
        this.messageEditText.setInputType(this.messageEditText.getInputType() | 16384);
        this.messageEditText.setMaxLines(4);
        this.messageEditText.setHorizontallyScrolling(false);
        this.messageEditText.setTextSize(1, 18.0f);
        this.messageEditText.setGravity(80);
        this.messageEditText.setPadding(0, AndroidUtilities.dp(11.0f), 0, AndroidUtilities.dp(12.0f));
        this.messageEditText.setBackgroundDrawable(null);
        AndroidUtilities.clearCursorDrawable(this.messageEditText);
        this.messageEditText.setTextColor(-1);
        this.messageEditText.setHintTextColor(-NUM);
        this.messageEditText.setFilters(new InputFilter[]{new LengthFilter(Callback.DEFAULT_DRAG_ANIMATION_DURATION)});
        frameLayout.addView(this.messageEditText, LayoutHelper.createFrame(-1, -2.0f, 83, 52.0f, 0.0f, 6.0f, 0.0f));
        this.messageEditText.setOnKeyListener(new OnKeyListener() {
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (i == 4) {
                    if (PhotoViewerCaptionEnterView.this.windowView != null && PhotoViewerCaptionEnterView.this.hideActionMode()) {
                        return true;
                    }
                    if (!PhotoViewerCaptionEnterView.this.keyboardVisible && PhotoViewerCaptionEnterView.this.isPopupShowing()) {
                        if (keyEvent.getAction() != 1) {
                            return true;
                        }
                        PhotoViewerCaptionEnterView.this.showPopup(0);
                        return true;
                    }
                }
                return false;
            }
        });
        this.messageEditText.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (PhotoViewerCaptionEnterView.this.isPopupShowing()) {
                    PhotoViewerCaptionEnterView.this.showPopup(AndroidUtilities.usingHardwareInput ? 0 : 2);
                }
            }
        });
        this.messageEditText.addTextChangedListener(new TextWatcher() {
            boolean processChange = false;

            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (!PhotoViewerCaptionEnterView.this.innerTextChange) {
                    if (PhotoViewerCaptionEnterView.this.delegate != null) {
                        PhotoViewerCaptionEnterView.this.delegate.onTextChanged(charSequence);
                    }
                    if (before != count && count - before > 1) {
                        this.processChange = true;
                    }
                }
            }

            public void afterTextChanged(Editable editable) {
                if (!PhotoViewerCaptionEnterView.this.innerTextChange && this.processChange) {
                    ImageSpan[] spans = (ImageSpan[]) editable.getSpans(0, editable.length(), ImageSpan.class);
                    for (Object removeSpan : spans) {
                        editable.removeSpan(removeSpan);
                    }
                    Emoji.replaceEmoji(editable, PhotoViewerCaptionEnterView.this.messageEditText.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
                    this.processChange = false;
                }
            }
        });
        ImageView doneButton = new ImageView(context);
        doneButton.setScaleType(ScaleType.CENTER);
        doneButton.setImageResource(R.drawable.ic_done);
        textFieldContainer.addView(doneButton, LayoutHelper.createLinear(48, 48, 80));
        if (VERSION.SDK_INT >= 21) {
            doneButton.setBackgroundDrawable(Theme.createBarSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
        }
        doneButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                PhotoViewerCaptionEnterView.this.delegate.onCaptionEnter();
            }
        });
    }

    public void setForceFloatingEmoji(boolean value) {
        this.forceFloatingEmoji = value;
    }

    public boolean hideActionMode() {
        if (VERSION.SDK_INT < 23 || this.currentActionMode == null) {
            return false;
        }
        try {
            this.currentActionMode.finish();
        } catch (Throwable e) {
            FileLog.e("tmessages", e);
        }
        this.currentActionMode = null;
        return true;
    }

    private void fixActionMode(ActionMode mode) {
        try {
            Class classActionMode = Class.forName("com.android.internal.view.FloatingActionMode");
            Field fieldToolbar = classActionMode.getDeclaredField("mFloatingToolbar");
            fieldToolbar.setAccessible(true);
            Object toolbar = fieldToolbar.get(mode);
            Class classToolbar = Class.forName("com.android.internal.widget.FloatingToolbar");
            Field fieldToolbarPopup = classToolbar.getDeclaredField("mPopup");
            Field fieldToolbarWidth = classToolbar.getDeclaredField("mWidthChanged");
            fieldToolbarPopup.setAccessible(true);
            fieldToolbarWidth.setAccessible(true);
            Object popup = fieldToolbarPopup.get(toolbar);
            Field fieldToolbarPopupParent = Class.forName("com.android.internal.widget.FloatingToolbar$FloatingToolbarPopup").getDeclaredField("mParent");
            fieldToolbarPopupParent.setAccessible(true);
            if (((View) fieldToolbarPopupParent.get(popup)) != this.windowView) {
                fieldToolbarPopupParent.set(popup, this.windowView);
                Method method = classActionMode.getDeclaredMethod("updateViewLocationInWindow", new Class[0]);
                method.setAccessible(true);
                method.invoke(mode, new Object[0]);
            }
        } catch (Throwable e) {
            FileLog.e("tmessages", e);
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

    public void onCreate() {
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.emojiDidLoaded);
        this.sizeNotifierLayout.setDelegate(this);
    }

    public void onDestroy() {
        hidePopup();
        if (isKeyboardVisible()) {
            closeKeyboard();
        }
        this.keyboardVisible = false;
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.emojiDidLoaded);
        if (this.sizeNotifierLayout != null) {
            this.sizeNotifierLayout.setDelegate(null);
        }
    }

    public void setDelegate(PhotoViewerCaptionEnterViewDelegate delegate) {
        this.delegate = delegate;
    }

    public void setFieldText(CharSequence text) {
        if (this.messageEditText != null) {
            this.messageEditText.setText(text);
            this.messageEditText.setSelection(this.messageEditText.getText().length());
            if (this.delegate != null) {
                this.delegate.onTextChanged(this.messageEditText.getText());
            }
        }
    }

    public int getCursorPosition() {
        if (this.messageEditText == null) {
            return 0;
        }
        return this.messageEditText.getSelectionStart();
    }

    public void replaceWithText(int start, int len, String text) {
        try {
            StringBuilder builder = new StringBuilder(this.messageEditText.getText());
            builder.replace(start, start + len, text);
            this.messageEditText.setText(builder);
            if (text.length() + start <= this.messageEditText.length()) {
                this.messageEditText.setSelection(text.length() + start);
            } else {
                this.messageEditText.setSelection(this.messageEditText.length());
            }
        } catch (Throwable e) {
            FileLog.e("tmessages", e);
        }
    }

    public void setFieldFocused(boolean focus) {
        if (this.messageEditText != null) {
            if (focus) {
                if (!this.messageEditText.isFocused()) {
                    this.messageEditText.postDelayed(new Runnable() {
                        public void run() {
                            if (PhotoViewerCaptionEnterView.this.messageEditText != null) {
                                try {
                                    PhotoViewerCaptionEnterView.this.messageEditText.requestFocus();
                                } catch (Throwable e) {
                                    FileLog.e("tmessages", e);
                                }
                            }
                        }
                    }, 600);
                }
            } else if (this.messageEditText.isFocused() && !this.keyboardVisible) {
                this.messageEditText.clearFocus();
            }
        }
    }

    public CharSequence getFieldCharSequence() {
        return this.messageEditText.getText();
    }

    public int getEmojiPadding() {
        return this.emojiPadding;
    }

    public boolean isPopupView(View view) {
        return view == this.emojiView;
    }

    private void showPopup(int show) {
        if (show == 1) {
            if (this.emojiView == null) {
                this.emojiView = new EmojiView(false, false, getContext());
                this.emojiView.setListener(new Listener() {
                    public boolean onBackspace() {
                        if (PhotoViewerCaptionEnterView.this.messageEditText.length() == 0) {
                            return false;
                        }
                        PhotoViewerCaptionEnterView.this.messageEditText.dispatchKeyEvent(new KeyEvent(0, 67));
                        return true;
                    }

                    public void onEmojiSelected(String symbol) {
                        if (PhotoViewerCaptionEnterView.this.messageEditText.length() + symbol.length() <= Callback.DEFAULT_DRAG_ANIMATION_DURATION) {
                            int i = PhotoViewerCaptionEnterView.this.messageEditText.getSelectionEnd();
                            if (i < 0) {
                                i = 0;
                            }
                            try {
                                PhotoViewerCaptionEnterView.this.innerTextChange = true;
                                CharSequence localCharSequence = Emoji.replaceEmoji(symbol, PhotoViewerCaptionEnterView.this.messageEditText.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
                                PhotoViewerCaptionEnterView.this.messageEditText.setText(PhotoViewerCaptionEnterView.this.messageEditText.getText().insert(i, localCharSequence));
                                int j = i + localCharSequence.length();
                                PhotoViewerCaptionEnterView.this.messageEditText.setSelection(j, j);
                            } catch (Throwable e) {
                                FileLog.e("tmessages", e);
                            } finally {
                                PhotoViewerCaptionEnterView.this.innerTextChange = false;
                            }
                        }
                    }

                    public void onStickerSelected(Document sticker) {
                    }

                    public void onStickersSettingsClick() {
                    }

                    public void onGifSelected(Document gif) {
                    }

                    public void onGifTab(boolean opened) {
                    }

                    public void onStickersTab(boolean opened) {
                    }

                    public void onClearEmojiRecent() {
                    }

                    public void onShowStickerSet(StickerSetCovered stickerSet) {
                    }

                    public void onStickerSetAdd(StickerSetCovered stickerSet) {
                    }

                    public void onStickerSetRemove(StickerSetCovered stickerSet) {
                    }
                });
                this.sizeNotifierLayout.addView(this.emojiView);
            }
            this.emojiView.setVisibility(0);
            if (this.keyboardHeight <= 0) {
                this.keyboardHeight = ApplicationLoader.applicationContext.getSharedPreferences("emoji", 0).getInt("kbd_height", AndroidUtilities.dp(200.0f));
            }
            if (this.keyboardHeightLand <= 0) {
                this.keyboardHeightLand = ApplicationLoader.applicationContext.getSharedPreferences("emoji", 0).getInt("kbd_height_land3", AndroidUtilities.dp(200.0f));
            }
            int currentHeight = AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y ? this.keyboardHeightLand : this.keyboardHeight;
            LayoutParams layoutParams = (LayoutParams) this.emojiView.getLayoutParams();
            layoutParams.width = AndroidUtilities.displaySize.x;
            layoutParams.height = currentHeight;
            this.emojiView.setLayoutParams(layoutParams);
            if (!(AndroidUtilities.isInMultiwindow || this.forceFloatingEmoji)) {
                AndroidUtilities.hideKeyboard(this.messageEditText);
            }
            if (this.sizeNotifierLayout != null) {
                this.emojiPadding = currentHeight;
                this.sizeNotifierLayout.requestLayout();
                this.emojiButton.setImageResource(R.drawable.ic_keyboard_w);
                onWindowSizeChanged();
                return;
            }
            return;
        }
        if (this.emojiButton != null) {
            this.emojiButton.setImageResource(R.drawable.ic_smile_w);
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

    public void hidePopup() {
        if (isPopupShowing()) {
            showPopup(0);
        }
    }

    private void openKeyboardInternal() {
        showPopup(AndroidUtilities.usingHardwareInput ? 0 : 2);
        openKeyboard();
    }

    public void openKeyboard() {
        int currentSelection;
        try {
            currentSelection = this.messageEditText.getSelectionStart();
        } catch (Throwable e) {
            currentSelection = this.messageEditText.length();
            FileLog.e("tmessages", e);
        }
        MotionEvent event = MotionEvent.obtain(0, 0, 0, 0.0f, 0.0f, 0);
        this.messageEditText.onTouchEvent(event);
        event.recycle();
        event = MotionEvent.obtain(0, 0, 1, 0.0f, 0.0f, 0);
        this.messageEditText.onTouchEvent(event);
        event.recycle();
        AndroidUtilities.showKeyboard(this.messageEditText);
        try {
            this.messageEditText.setSelection(currentSelection);
        } catch (Throwable e2) {
            FileLog.e("tmessages", e2);
        }
    }

    public boolean isPopupShowing() {
        return this.emojiView != null && this.emojiView.getVisibility() == 0;
    }

    public void closeKeyboard() {
        AndroidUtilities.hideKeyboard(this.messageEditText);
    }

    public boolean isKeyboardVisible() {
        return (AndroidUtilities.usingHardwareInput && getTag() != null) || this.keyboardVisible;
    }

    public void onSizeChanged(int height, boolean isWidthGreater) {
        if (height > AndroidUtilities.dp(50.0f) && this.keyboardVisible && !AndroidUtilities.isInMultiwindow && !this.forceFloatingEmoji) {
            if (isWidthGreater) {
                this.keyboardHeightLand = height;
                ApplicationLoader.applicationContext.getSharedPreferences("emoji", 0).edit().putInt("kbd_height_land3", this.keyboardHeightLand).commit();
            } else {
                this.keyboardHeight = height;
                ApplicationLoader.applicationContext.getSharedPreferences("emoji", 0).edit().putInt("kbd_height", this.keyboardHeight).commit();
            }
        }
        if (isPopupShowing()) {
            int newHeight;
            if (isWidthGreater) {
                newHeight = this.keyboardHeightLand;
            } else {
                newHeight = this.keyboardHeight;
            }
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
        onWindowSizeChanged();
    }

    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationCenter.emojiDidLoaded && this.emojiView != null) {
            this.emojiView.invalidateViews();
        }
    }
}
