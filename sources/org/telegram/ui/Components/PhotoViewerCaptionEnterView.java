package org.telegram.ui.Components;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build.VERSION;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.SpannableStringBuilder;
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
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0446R;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper.Callback;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.InputStickerSet;
import org.telegram.tgnet.TLRPC.StickerSet;
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
    private EditTextCaption messageEditText;
    private AnimatorSet runningAnimation;
    private AnimatorSet runningAnimation2;
    private ObjectAnimator runningAnimationAudio;
    private int runningAnimationType;
    private SizeNotifierFrameLayoutPhoto sizeNotifierLayout;
    private View windowView;

    /* renamed from: org.telegram.ui.Components.PhotoViewerCaptionEnterView$1 */
    class C12611 implements OnClickListener {
        C12611() {
        }

        public void onClick(View view) {
            if (PhotoViewerCaptionEnterView.this.isPopupShowing() == null) {
                PhotoViewerCaptionEnterView.this.showPopup(1);
            } else {
                PhotoViewerCaptionEnterView.this.openKeyboardInternal();
            }
        }
    }

    /* renamed from: org.telegram.ui.Components.PhotoViewerCaptionEnterView$3 */
    class C12623 implements ActionMode.Callback {
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            return false;
        }

        C12623() {
        }

        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            PhotoViewerCaptionEnterView.this.currentActionMode = actionMode;
            return true;
        }

        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            if (VERSION.SDK_INT >= 23) {
                PhotoViewerCaptionEnterView.this.fixActionMode(actionMode);
            }
            return true;
        }

        public void onDestroyActionMode(ActionMode actionMode) {
            if (PhotoViewerCaptionEnterView.this.currentActionMode == actionMode) {
                PhotoViewerCaptionEnterView.this.currentActionMode = null;
            }
        }
    }

    /* renamed from: org.telegram.ui.Components.PhotoViewerCaptionEnterView$4 */
    class C12634 implements ActionMode.Callback {
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            return false;
        }

        C12634() {
        }

        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            PhotoViewerCaptionEnterView.this.currentActionMode = actionMode;
            return true;
        }

        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            if (VERSION.SDK_INT >= 23) {
                PhotoViewerCaptionEnterView.this.fixActionMode(actionMode);
            }
            return true;
        }

        public void onDestroyActionMode(ActionMode actionMode) {
            if (PhotoViewerCaptionEnterView.this.currentActionMode == actionMode) {
                PhotoViewerCaptionEnterView.this.currentActionMode = null;
            }
        }
    }

    /* renamed from: org.telegram.ui.Components.PhotoViewerCaptionEnterView$5 */
    class C12645 implements OnKeyListener {
        C12645() {
        }

        public boolean onKey(View view, int i, KeyEvent keyEvent) {
            if (i == 4) {
                if (PhotoViewerCaptionEnterView.this.windowView != 0 && PhotoViewerCaptionEnterView.this.hideActionMode() != 0) {
                    return true;
                }
                if (PhotoViewerCaptionEnterView.this.keyboardVisible == 0 && PhotoViewerCaptionEnterView.this.isPopupShowing() != 0) {
                    if (keyEvent.getAction() == 1) {
                        PhotoViewerCaptionEnterView.this.showPopup(0);
                    }
                    return true;
                }
            }
            return false;
        }
    }

    /* renamed from: org.telegram.ui.Components.PhotoViewerCaptionEnterView$6 */
    class C12656 implements OnClickListener {
        C12656() {
        }

        public void onClick(View view) {
            if (PhotoViewerCaptionEnterView.this.isPopupShowing() != null) {
                PhotoViewerCaptionEnterView.this.showPopup(AndroidUtilities.usingHardwareInput ? 0 : 2);
            }
        }
    }

    /* renamed from: org.telegram.ui.Components.PhotoViewerCaptionEnterView$7 */
    class C12667 implements TextWatcher {
        boolean processChange = null;

        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        C12667() {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            if (PhotoViewerCaptionEnterView.this.innerTextChange == 0) {
                if (PhotoViewerCaptionEnterView.this.delegate != 0) {
                    PhotoViewerCaptionEnterView.this.delegate.onTextChanged(charSequence);
                }
                if (i2 != i3 && i3 - i2 > 1) {
                    this.processChange = true;
                }
            }
        }

        public void afterTextChanged(Editable editable) {
            if (!PhotoViewerCaptionEnterView.this.innerTextChange && this.processChange) {
                ImageSpan[] imageSpanArr = (ImageSpan[]) editable.getSpans(0, editable.length(), ImageSpan.class);
                for (Object removeSpan : imageSpanArr) {
                    editable.removeSpan(removeSpan);
                }
                Emoji.replaceEmoji(editable, PhotoViewerCaptionEnterView.this.messageEditText.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
                this.processChange = false;
            }
        }
    }

    /* renamed from: org.telegram.ui.Components.PhotoViewerCaptionEnterView$8 */
    class C12678 implements OnClickListener {
        C12678() {
        }

        public void onClick(View view) {
            PhotoViewerCaptionEnterView.this.delegate.onCaptionEnter();
        }
    }

    public interface PhotoViewerCaptionEnterViewDelegate {
        void onCaptionEnter();

        void onTextChanged(CharSequence charSequence);

        void onWindowSizeChanged(int i);
    }

    /* renamed from: org.telegram.ui.Components.PhotoViewerCaptionEnterView$9 */
    class C20759 implements Listener {
        public boolean isExpanded() {
            return false;
        }

        public boolean isSearchOpened() {
            return false;
        }

        public void onClearEmojiRecent() {
        }

        public void onGifSelected(Document document) {
        }

        public void onGifTab(boolean z) {
        }

        public void onSearchOpenClose(boolean z) {
        }

        public void onShowStickerSet(StickerSet stickerSet, InputStickerSet inputStickerSet) {
        }

        public void onStickerSelected(Document document) {
        }

        public void onStickerSetAdd(StickerSetCovered stickerSetCovered) {
        }

        public void onStickerSetRemove(StickerSetCovered stickerSetCovered) {
        }

        public void onStickersGroupClick(int i) {
        }

        public void onStickersSettingsClick() {
        }

        public void onStickersTab(boolean z) {
        }

        C20759() {
        }

        public boolean onBackspace() {
            if (PhotoViewerCaptionEnterView.this.messageEditText.length() == 0) {
                return false;
            }
            PhotoViewerCaptionEnterView.this.messageEditText.dispatchKeyEvent(new KeyEvent(0, 67));
            return true;
        }

        public void onEmojiSelected(String str) {
            if (PhotoViewerCaptionEnterView.this.messageEditText.length() + str.length() <= Callback.DEFAULT_DRAG_ANIMATION_DURATION) {
                int selectionEnd = PhotoViewerCaptionEnterView.this.messageEditText.getSelectionEnd();
                if (selectionEnd < 0) {
                    selectionEnd = 0;
                }
                try {
                    PhotoViewerCaptionEnterView.this.innerTextChange = true;
                    str = Emoji.replaceEmoji(str, PhotoViewerCaptionEnterView.this.messageEditText.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
                    PhotoViewerCaptionEnterView.this.messageEditText.setText(PhotoViewerCaptionEnterView.this.messageEditText.getText().insert(selectionEnd, str));
                    selectionEnd += str.length();
                    PhotoViewerCaptionEnterView.this.messageEditText.setSelection(selectionEnd, selectionEnd);
                } catch (Throwable e) {
                    FileLog.m3e(e);
                } catch (Throwable th) {
                    PhotoViewerCaptionEnterView.this.innerTextChange = false;
                }
                PhotoViewerCaptionEnterView.this.innerTextChange = false;
            }
        }
    }

    public PhotoViewerCaptionEnterView(Context context, SizeNotifierFrameLayoutPhoto sizeNotifierFrameLayoutPhoto, View view) {
        Context context2 = context;
        super(context);
        setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
        setFocusable(true);
        setFocusableInTouchMode(true);
        this.windowView = view;
        this.sizeNotifierLayout = sizeNotifierFrameLayoutPhoto;
        View linearLayout = new LinearLayout(context2);
        linearLayout.setOrientation(0);
        addView(linearLayout, LayoutHelper.createFrame(-1, -2.0f, 51, 2.0f, 0.0f, 0.0f, 0.0f));
        View frameLayout = new FrameLayout(context2);
        linearLayout.addView(frameLayout, LayoutHelper.createLinear(0, -2, 1.0f));
        this.emojiButton = new ImageView(context2);
        this.emojiButton.setImageResource(C0446R.drawable.ic_smile_w);
        this.emojiButton.setScaleType(ScaleType.CENTER_INSIDE);
        this.emojiButton.setPadding(AndroidUtilities.dp(4.0f), AndroidUtilities.dp(1.0f), 0, 0);
        frameLayout.addView(this.emojiButton, LayoutHelper.createFrame(48, 48, 83));
        this.emojiButton.setOnClickListener(new C12611());
        this.messageEditText = new EditTextCaption(context2) {
            protected void onMeasure(int i, int i2) {
                try {
                    super.onMeasure(i, i2);
                } catch (Throwable e) {
                    setMeasuredDimension(MeasureSpec.getSize(i), AndroidUtilities.dp(51.0f));
                    FileLog.m3e(e);
                }
            }
        };
        if (VERSION.SDK_INT >= 23 && r0.windowView != null) {
            r0.messageEditText.setCustomSelectionActionModeCallback(new C12623());
            r0.messageEditText.setCustomInsertionActionModeCallback(new C12634());
        }
        r0.messageEditText.setHint(LocaleController.getString("AddCaption", C0446R.string.AddCaption));
        r0.messageEditText.setImeOptions(268435456);
        r0.messageEditText.setInputType(r0.messageEditText.getInputType() | MessagesController.UPDATE_MASK_CHAT_ADMINS);
        r0.messageEditText.setMaxLines(4);
        r0.messageEditText.setHorizontallyScrolling(false);
        r0.messageEditText.setTextSize(1, 18.0f);
        r0.messageEditText.setGravity(80);
        r0.messageEditText.setPadding(0, AndroidUtilities.dp(11.0f), 0, AndroidUtilities.dp(12.0f));
        r0.messageEditText.setBackgroundDrawable(null);
        r0.messageEditText.setCursorColor(-1);
        r0.messageEditText.setCursorSize(AndroidUtilities.dp(20.0f));
        r0.messageEditText.setTextColor(-1);
        r0.messageEditText.setHintTextColor(-NUM);
        r0.messageEditText.setFilters(new InputFilter[]{new LengthFilter(Callback.DEFAULT_DRAG_ANIMATION_DURATION)});
        frameLayout.addView(r0.messageEditText, LayoutHelper.createFrame(-1, -2.0f, 83, 52.0f, 0.0f, 6.0f, 0.0f));
        r0.messageEditText.setOnKeyListener(new C12645());
        r0.messageEditText.setOnClickListener(new C12656());
        r0.messageEditText.addTextChangedListener(new C12667());
        View imageView = new ImageView(context2);
        imageView.setScaleType(ScaleType.CENTER);
        imageView.setImageResource(C0446R.drawable.ic_done);
        linearLayout.addView(imageView, LayoutHelper.createLinear(48, 48, 80));
        if (VERSION.SDK_INT >= 21) {
            imageView.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
        }
        imageView.setOnClickListener(new C12678());
    }

    public void setForceFloatingEmoji(boolean z) {
        this.forceFloatingEmoji = z;
    }

    public boolean hideActionMode() {
        if (VERSION.SDK_INT < 23 || this.currentActionMode == null) {
            return false;
        }
        try {
            this.currentActionMode.finish();
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
        this.currentActionMode = null;
        return true;
    }

    @SuppressLint({"PrivateApi"})
    private void fixActionMode(ActionMode actionMode) {
        try {
            Class cls = Class.forName("com.android.internal.view.FloatingActionMode");
            Field declaredField = cls.getDeclaredField("mFloatingToolbar");
            declaredField.setAccessible(true);
            Object obj = declaredField.get(actionMode);
            Class cls2 = Class.forName("com.android.internal.widget.FloatingToolbar");
            Field declaredField2 = cls2.getDeclaredField("mPopup");
            Field declaredField3 = cls2.getDeclaredField("mWidthChanged");
            declaredField2.setAccessible(true);
            declaredField3.setAccessible(true);
            obj = declaredField2.get(obj);
            declaredField3 = Class.forName("com.android.internal.widget.FloatingToolbar$FloatingToolbarPopup").getDeclaredField("mParent");
            declaredField3.setAccessible(true);
            if (((View) declaredField3.get(obj)) != this.windowView) {
                declaredField3.set(obj, this.windowView);
                Method declaredMethod = cls.getDeclaredMethod("updateViewLocationInWindow", new Class[0]);
                declaredMethod.setAccessible(true);
                declaredMethod.invoke(actionMode, new Object[0]);
            }
        } catch (Throwable th) {
            FileLog.m3e(th);
        }
    }

    private void onWindowSizeChanged() {
        int height = this.sizeNotifierLayout.getHeight();
        if (!this.keyboardVisible) {
            height -= this.emojiPadding;
        }
        if (this.delegate != null) {
            this.delegate.onWindowSizeChanged(height);
        }
    }

    public void onCreate() {
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiDidLoaded);
        this.sizeNotifierLayout.setDelegate(this);
    }

    public void onDestroy() {
        hidePopup();
        if (isKeyboardVisible()) {
            closeKeyboard();
        }
        this.keyboardVisible = false;
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiDidLoaded);
        if (this.sizeNotifierLayout != null) {
            this.sizeNotifierLayout.setDelegate(null);
        }
    }

    public void setDelegate(PhotoViewerCaptionEnterViewDelegate photoViewerCaptionEnterViewDelegate) {
        this.delegate = photoViewerCaptionEnterViewDelegate;
    }

    public void setFieldText(CharSequence charSequence) {
        if (this.messageEditText != null) {
            this.messageEditText.setText(charSequence);
            this.messageEditText.setSelection(this.messageEditText.getText().length());
            if (this.delegate != null) {
                this.delegate.onTextChanged(this.messageEditText.getText());
            }
        }
    }

    public int getSelectionLength() {
        if (this.messageEditText == null) {
            return 0;
        }
        try {
            return this.messageEditText.getSelectionEnd() - this.messageEditText.getSelectionStart();
        } catch (Throwable e) {
            FileLog.m3e(e);
            return 0;
        }
    }

    public int getCursorPosition() {
        if (this.messageEditText == null) {
            return 0;
        }
        return this.messageEditText.getSelectionStart();
    }

    private void createEmojiView() {
        if (this.emojiView == null) {
            this.emojiView = new EmojiView(false, false, getContext(), null);
            this.emojiView.setListener(new C20759());
            this.sizeNotifierLayout.addView(this.emojiView);
        }
    }

    public void addEmojiToRecent(String str) {
        createEmojiView();
        this.emojiView.addEmojiToRecent(str);
    }

    public void replaceWithText(int i, int i2, CharSequence charSequence, boolean z) {
        try {
            CharSequence spannableStringBuilder = new SpannableStringBuilder(this.messageEditText.getText());
            spannableStringBuilder.replace(i, i2 + i, charSequence);
            if (z) {
                Emoji.replaceEmoji(spannableStringBuilder, this.messageEditText.getPaint().getFontMetricsInt(), AndroidUtilities.dp(true), false);
            }
            this.messageEditText.setText(spannableStringBuilder);
            if (charSequence.length() + i <= this.messageEditText.length()) {
                this.messageEditText.setSelection(i + charSequence.length());
            } else {
                this.messageEditText.setSelection(this.messageEditText.length());
            }
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
    }

    public void setFieldFocused(boolean z) {
        if (this.messageEditText != null) {
            if (z) {
                if (!this.messageEditText.isFocused()) {
                    this.messageEditText.postDelayed(new Runnable() {
                        public void run() {
                            if (PhotoViewerCaptionEnterView.this.messageEditText != null) {
                                try {
                                    PhotoViewerCaptionEnterView.this.messageEditText.requestFocus();
                                } catch (Throwable e) {
                                    FileLog.m3e(e);
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
        return view == this.emojiView ? true : null;
    }

    private void showPopup(int i) {
        if (i == 1) {
            if (this.emojiView == 0) {
                createEmojiView();
            }
            this.emojiView.setVisibility(0);
            if (this.keyboardHeight <= 0) {
                this.keyboardHeight = MessagesController.getGlobalEmojiSettings().getInt("kbd_height", AndroidUtilities.dp(200.0f));
            }
            if (this.keyboardHeightLand <= 0) {
                this.keyboardHeightLand = MessagesController.getGlobalEmojiSettings().getInt("kbd_height_land3", AndroidUtilities.dp(200.0f));
            }
            i = AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y ? this.keyboardHeightLand : this.keyboardHeight;
            LayoutParams layoutParams = (LayoutParams) this.emojiView.getLayoutParams();
            layoutParams.width = AndroidUtilities.displaySize.x;
            layoutParams.height = i;
            this.emojiView.setLayoutParams(layoutParams);
            if (!(AndroidUtilities.isInMultiwindow || this.forceFloatingEmoji)) {
                AndroidUtilities.hideKeyboard(this.messageEditText);
            }
            if (this.sizeNotifierLayout != null) {
                this.emojiPadding = i;
                this.sizeNotifierLayout.requestLayout();
                this.emojiButton.setImageResource(C0446R.drawable.ic_keyboard_w);
                onWindowSizeChanged();
                return;
            }
            return;
        }
        if (this.emojiButton != null) {
            this.emojiButton.setImageResource(C0446R.drawable.ic_smile_w);
        }
        if (this.emojiView != null) {
            this.emojiView.setVisibility(8);
        }
        if (this.sizeNotifierLayout != null) {
            if (i == 0) {
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
        int selectionStart;
        try {
            selectionStart = this.messageEditText.getSelectionStart();
        } catch (Throwable e) {
            int length = this.messageEditText.length();
            FileLog.m3e(e);
            selectionStart = length;
        }
        MotionEvent obtain = MotionEvent.obtain(0, 0, 0, 0.0f, 0.0f, 0);
        this.messageEditText.onTouchEvent(obtain);
        obtain.recycle();
        obtain = MotionEvent.obtain(0, 0, 1, 0.0f, 0.0f, 0);
        this.messageEditText.onTouchEvent(obtain);
        obtain.recycle();
        AndroidUtilities.showKeyboard(this.messageEditText);
        try {
            this.messageEditText.setSelection(selectionStart);
        } catch (Throwable e2) {
            FileLog.m3e(e2);
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

    public void onSizeChanged(int i, boolean z) {
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
            int i2;
            if (z) {
                i2 = this.keyboardHeightLand;
            } else {
                i2 = this.keyboardHeight;
            }
            LayoutParams layoutParams = (LayoutParams) this.emojiView.getLayoutParams();
            if (!(layoutParams.width == AndroidUtilities.displaySize.x && layoutParams.height == i2)) {
                layoutParams.width = AndroidUtilities.displaySize.x;
                layoutParams.height = i2;
                this.emojiView.setLayoutParams(layoutParams);
                if (this.sizeNotifierLayout != null) {
                    this.emojiPadding = layoutParams.height;
                    this.sizeNotifierLayout.requestLayout();
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
        z = this.keyboardVisible;
        this.keyboardVisible = i > 0 ? 1 : 0;
        if (!(this.keyboardVisible == 0 || isPopupShowing() == 0)) {
            showPopup(0);
        }
        if (this.emojiPadding != 0 && this.keyboardVisible == 0 && this.keyboardVisible != z && isPopupShowing() == 0) {
            this.emojiPadding = 0;
            this.sizeNotifierLayout.requestLayout();
        }
        onWindowSizeChanged();
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.emojiDidLoaded && this.emojiView != 0) {
            this.emojiView.invalidateViews();
        }
    }
}
