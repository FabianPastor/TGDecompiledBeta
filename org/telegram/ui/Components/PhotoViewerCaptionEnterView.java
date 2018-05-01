package org.telegram.ui.Components;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Point;
import android.os.Build.VERSION;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
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
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.InputStickerSet;
import org.telegram.tgnet.TLRPC.StickerSet;
import org.telegram.tgnet.TLRPC.StickerSetCovered;
import org.telegram.ui.ActionBar.Theme;

public class PhotoViewerCaptionEnterView
  extends FrameLayout
  implements NotificationCenter.NotificationCenterDelegate, SizeNotifierFrameLayoutPhoto.SizeNotifierFrameLayoutPhotoDelegate
{
  private int audioInterfaceState;
  private final int captionMaxLength = 200;
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
  
  public PhotoViewerCaptionEnterView(Context paramContext, SizeNotifierFrameLayoutPhoto paramSizeNotifierFrameLayoutPhoto, View paramView)
  {
    super(paramContext);
    setBackgroundColor(NUM);
    setFocusable(true);
    setFocusableInTouchMode(true);
    this.windowView = paramView;
    this.sizeNotifierLayout = paramSizeNotifierFrameLayoutPhoto;
    paramSizeNotifierFrameLayoutPhoto = new LinearLayout(paramContext);
    paramSizeNotifierFrameLayoutPhoto.setOrientation(0);
    addView(paramSizeNotifierFrameLayoutPhoto, LayoutHelper.createFrame(-1, -2.0F, 51, 2.0F, 0.0F, 0.0F, 0.0F));
    FrameLayout localFrameLayout = new FrameLayout(paramContext);
    paramSizeNotifierFrameLayoutPhoto.addView(localFrameLayout, LayoutHelper.createLinear(0, -2, 1.0F));
    this.emojiButton = new ImageView(paramContext);
    this.emojiButton.setImageResource(NUM);
    this.emojiButton.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
    this.emojiButton.setPadding(AndroidUtilities.dp(4.0F), AndroidUtilities.dp(1.0F), 0, 0);
    localFrameLayout.addView(this.emojiButton, LayoutHelper.createFrame(48, 48, 83));
    this.emojiButton.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        if (!PhotoViewerCaptionEnterView.this.isPopupShowing()) {
          PhotoViewerCaptionEnterView.this.showPopup(1);
        }
        for (;;)
        {
          return;
          PhotoViewerCaptionEnterView.this.openKeyboardInternal();
        }
      }
    });
    this.messageEditText = new EditTextCaption(paramContext)
    {
      protected void onMeasure(int paramAnonymousInt1, int paramAnonymousInt2)
      {
        try
        {
          super.onMeasure(paramAnonymousInt1, paramAnonymousInt2);
          return;
        }
        catch (Exception localException)
        {
          for (;;)
          {
            setMeasuredDimension(View.MeasureSpec.getSize(paramAnonymousInt1), AndroidUtilities.dp(51.0F));
            FileLog.e(localException);
          }
        }
      }
    };
    if ((Build.VERSION.SDK_INT >= 23) && (this.windowView != null))
    {
      this.messageEditText.setCustomSelectionActionModeCallback(new ActionMode.Callback()
      {
        public boolean onActionItemClicked(ActionMode paramAnonymousActionMode, MenuItem paramAnonymousMenuItem)
        {
          return false;
        }
        
        public boolean onCreateActionMode(ActionMode paramAnonymousActionMode, Menu paramAnonymousMenu)
        {
          PhotoViewerCaptionEnterView.access$202(PhotoViewerCaptionEnterView.this, paramAnonymousActionMode);
          return true;
        }
        
        public void onDestroyActionMode(ActionMode paramAnonymousActionMode)
        {
          if (PhotoViewerCaptionEnterView.this.currentActionMode == paramAnonymousActionMode) {
            PhotoViewerCaptionEnterView.access$202(PhotoViewerCaptionEnterView.this, null);
          }
        }
        
        public boolean onPrepareActionMode(ActionMode paramAnonymousActionMode, Menu paramAnonymousMenu)
        {
          if (Build.VERSION.SDK_INT >= 23) {
            PhotoViewerCaptionEnterView.this.fixActionMode(paramAnonymousActionMode);
          }
          return true;
        }
      });
      this.messageEditText.setCustomInsertionActionModeCallback(new ActionMode.Callback()
      {
        public boolean onActionItemClicked(ActionMode paramAnonymousActionMode, MenuItem paramAnonymousMenuItem)
        {
          return false;
        }
        
        public boolean onCreateActionMode(ActionMode paramAnonymousActionMode, Menu paramAnonymousMenu)
        {
          PhotoViewerCaptionEnterView.access$202(PhotoViewerCaptionEnterView.this, paramAnonymousActionMode);
          return true;
        }
        
        public void onDestroyActionMode(ActionMode paramAnonymousActionMode)
        {
          if (PhotoViewerCaptionEnterView.this.currentActionMode == paramAnonymousActionMode) {
            PhotoViewerCaptionEnterView.access$202(PhotoViewerCaptionEnterView.this, null);
          }
        }
        
        public boolean onPrepareActionMode(ActionMode paramAnonymousActionMode, Menu paramAnonymousMenu)
        {
          if (Build.VERSION.SDK_INT >= 23) {
            PhotoViewerCaptionEnterView.this.fixActionMode(paramAnonymousActionMode);
          }
          return true;
        }
      });
    }
    this.messageEditText.setHint(LocaleController.getString("AddCaption", NUM));
    this.messageEditText.setImeOptions(268435456);
    this.messageEditText.setInputType(this.messageEditText.getInputType() | 0x4000);
    this.messageEditText.setMaxLines(4);
    this.messageEditText.setHorizontallyScrolling(false);
    this.messageEditText.setTextSize(1, 18.0F);
    this.messageEditText.setGravity(80);
    this.messageEditText.setPadding(0, AndroidUtilities.dp(11.0F), 0, AndroidUtilities.dp(12.0F));
    this.messageEditText.setBackgroundDrawable(null);
    this.messageEditText.setCursorColor(-1);
    this.messageEditText.setCursorSize(AndroidUtilities.dp(20.0F));
    this.messageEditText.setTextColor(-1);
    this.messageEditText.setHintTextColor(-NUM);
    paramView = new InputFilter.LengthFilter(200);
    this.messageEditText.setFilters(new InputFilter[] { paramView });
    localFrameLayout.addView(this.messageEditText, LayoutHelper.createFrame(-1, -2.0F, 83, 52.0F, 0.0F, 6.0F, 0.0F));
    this.messageEditText.setOnKeyListener(new View.OnKeyListener()
    {
      public boolean onKey(View paramAnonymousView, int paramAnonymousInt, KeyEvent paramAnonymousKeyEvent)
      {
        boolean bool1 = true;
        boolean bool2;
        if (paramAnonymousInt == 4) {
          if ((PhotoViewerCaptionEnterView.this.windowView != null) && (PhotoViewerCaptionEnterView.this.hideActionMode())) {
            bool2 = bool1;
          }
        }
        for (;;)
        {
          return bool2;
          if ((!PhotoViewerCaptionEnterView.this.keyboardVisible) && (PhotoViewerCaptionEnterView.this.isPopupShowing()))
          {
            bool2 = bool1;
            if (paramAnonymousKeyEvent.getAction() == 1)
            {
              PhotoViewerCaptionEnterView.this.showPopup(0);
              bool2 = bool1;
            }
          }
          else
          {
            bool2 = false;
          }
        }
      }
    });
    this.messageEditText.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        if (PhotoViewerCaptionEnterView.this.isPopupShowing())
        {
          paramAnonymousView = PhotoViewerCaptionEnterView.this;
          if (!AndroidUtilities.usingHardwareInput) {
            break label29;
          }
        }
        label29:
        for (int i = 0;; i = 2)
        {
          paramAnonymousView.showPopup(i);
          return;
        }
      }
    });
    this.messageEditText.addTextChangedListener(new TextWatcher()
    {
      boolean processChange = false;
      
      public void afterTextChanged(Editable paramAnonymousEditable)
      {
        if (PhotoViewerCaptionEnterView.this.innerTextChange) {}
        for (;;)
        {
          return;
          if (this.processChange)
          {
            ImageSpan[] arrayOfImageSpan = (ImageSpan[])paramAnonymousEditable.getSpans(0, paramAnonymousEditable.length(), ImageSpan.class);
            for (int i = 0; i < arrayOfImageSpan.length; i++) {
              paramAnonymousEditable.removeSpan(arrayOfImageSpan[i]);
            }
            Emoji.replaceEmoji(paramAnonymousEditable, PhotoViewerCaptionEnterView.this.messageEditText.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0F), false);
            this.processChange = false;
          }
        }
      }
      
      public void beforeTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {}
      
      public void onTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3)
      {
        if (PhotoViewerCaptionEnterView.this.innerTextChange) {}
        for (;;)
        {
          return;
          if (PhotoViewerCaptionEnterView.this.delegate != null) {
            PhotoViewerCaptionEnterView.this.delegate.onTextChanged(paramAnonymousCharSequence);
          }
          if ((paramAnonymousInt2 != paramAnonymousInt3) && (paramAnonymousInt3 - paramAnonymousInt2 > 1)) {
            this.processChange = true;
          }
        }
      }
    });
    paramContext = new ImageView(paramContext);
    paramContext.setScaleType(ImageView.ScaleType.CENTER);
    paramContext.setImageResource(NUM);
    paramSizeNotifierFrameLayoutPhoto.addView(paramContext, LayoutHelper.createLinear(48, 48, 80));
    if (Build.VERSION.SDK_INT >= 21) {
      paramContext.setBackgroundDrawable(Theme.createSelectorDrawable(NUM));
    }
    paramContext.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        PhotoViewerCaptionEnterView.this.delegate.onCaptionEnter();
      }
    });
  }
  
  private void createEmojiView()
  {
    if (this.emojiView != null) {}
    for (;;)
    {
      return;
      this.emojiView = new EmojiView(false, false, getContext(), null);
      this.emojiView.setListener(new EmojiView.Listener()
      {
        public boolean isExpanded()
        {
          return false;
        }
        
        public boolean isSearchOpened()
        {
          return false;
        }
        
        public boolean onBackspace()
        {
          boolean bool = false;
          if (PhotoViewerCaptionEnterView.this.messageEditText.length() == 0) {}
          for (;;)
          {
            return bool;
            PhotoViewerCaptionEnterView.this.messageEditText.dispatchKeyEvent(new KeyEvent(0, 67));
            bool = true;
          }
        }
        
        public void onClearEmojiRecent() {}
        
        public void onEmojiSelected(String paramAnonymousString)
        {
          if (PhotoViewerCaptionEnterView.this.messageEditText.length() + paramAnonymousString.length() > 200) {}
          for (;;)
          {
            return;
            int i = PhotoViewerCaptionEnterView.this.messageEditText.getSelectionEnd();
            int j = i;
            if (i < 0) {
              j = 0;
            }
            try
            {
              PhotoViewerCaptionEnterView.access$602(PhotoViewerCaptionEnterView.this, true);
              paramAnonymousString = Emoji.replaceEmoji(paramAnonymousString, PhotoViewerCaptionEnterView.this.messageEditText.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0F), false);
              PhotoViewerCaptionEnterView.this.messageEditText.setText(PhotoViewerCaptionEnterView.this.messageEditText.getText().insert(j, paramAnonymousString));
              j += paramAnonymousString.length();
              PhotoViewerCaptionEnterView.this.messageEditText.setSelection(j, j);
              PhotoViewerCaptionEnterView.access$602(PhotoViewerCaptionEnterView.this, false);
            }
            catch (Exception paramAnonymousString)
            {
              FileLog.e(paramAnonymousString);
              PhotoViewerCaptionEnterView.access$602(PhotoViewerCaptionEnterView.this, false);
            }
            finally
            {
              PhotoViewerCaptionEnterView.access$602(PhotoViewerCaptionEnterView.this, false);
            }
          }
        }
        
        public void onGifSelected(TLRPC.Document paramAnonymousDocument) {}
        
        public void onGifTab(boolean paramAnonymousBoolean) {}
        
        public void onSearchOpenClose(boolean paramAnonymousBoolean) {}
        
        public void onShowStickerSet(TLRPC.StickerSet paramAnonymousStickerSet, TLRPC.InputStickerSet paramAnonymousInputStickerSet) {}
        
        public void onStickerSelected(TLRPC.Document paramAnonymousDocument) {}
        
        public void onStickerSetAdd(TLRPC.StickerSetCovered paramAnonymousStickerSetCovered) {}
        
        public void onStickerSetRemove(TLRPC.StickerSetCovered paramAnonymousStickerSetCovered) {}
        
        public void onStickersGroupClick(int paramAnonymousInt) {}
        
        public void onStickersSettingsClick() {}
        
        public void onStickersTab(boolean paramAnonymousBoolean) {}
      });
      this.sizeNotifierLayout.addView(this.emojiView);
    }
  }
  
  @SuppressLint({"PrivateApi"})
  private void fixActionMode(ActionMode paramActionMode)
  {
    try
    {
      Object localObject1 = Class.forName("com.android.internal.view.FloatingActionMode");
      Field localField = ((Class)localObject1).getDeclaredField("mFloatingToolbar");
      localField.setAccessible(true);
      Object localObject2 = localField.get(paramActionMode);
      Object localObject3 = Class.forName("com.android.internal.widget.FloatingToolbar");
      localField = ((Class)localObject3).getDeclaredField("mPopup");
      localObject3 = ((Class)localObject3).getDeclaredField("mWidthChanged");
      localField.setAccessible(true);
      ((Field)localObject3).setAccessible(true);
      localObject2 = localField.get(localObject2);
      localField = Class.forName("com.android.internal.widget.FloatingToolbar$FloatingToolbarPopup").getDeclaredField("mParent");
      localField.setAccessible(true);
      if ((View)localField.get(localObject2) != this.windowView)
      {
        localField.set(localObject2, this.windowView);
        localObject1 = ((Class)localObject1).getDeclaredMethod("updateViewLocationInWindow", new Class[0]);
        ((Method)localObject1).setAccessible(true);
        ((Method)localObject1).invoke(paramActionMode, new Object[0]);
      }
      return;
    }
    catch (Throwable paramActionMode)
    {
      for (;;)
      {
        FileLog.e(paramActionMode);
      }
    }
  }
  
  private void onWindowSizeChanged()
  {
    int i = this.sizeNotifierLayout.getHeight();
    int j = i;
    if (!this.keyboardVisible) {
      j = i - this.emojiPadding;
    }
    if (this.delegate != null) {
      this.delegate.onWindowSizeChanged(j);
    }
  }
  
  private void openKeyboardInternal()
  {
    if (AndroidUtilities.usingHardwareInput) {}
    for (int i = 0;; i = 2)
    {
      showPopup(i);
      openKeyboard();
      return;
    }
  }
  
  private void showPopup(int paramInt)
  {
    if (paramInt == 1)
    {
      if (this.emojiView == null) {
        createEmojiView();
      }
      this.emojiView.setVisibility(0);
      if (this.keyboardHeight <= 0) {
        this.keyboardHeight = MessagesController.getGlobalEmojiSettings().getInt("kbd_height", AndroidUtilities.dp(200.0F));
      }
      if (this.keyboardHeightLand <= 0) {
        this.keyboardHeightLand = MessagesController.getGlobalEmojiSettings().getInt("kbd_height_land3", AndroidUtilities.dp(200.0F));
      }
      if (AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y)
      {
        paramInt = this.keyboardHeightLand;
        FrameLayout.LayoutParams localLayoutParams = (FrameLayout.LayoutParams)this.emojiView.getLayoutParams();
        localLayoutParams.width = AndroidUtilities.displaySize.x;
        localLayoutParams.height = paramInt;
        this.emojiView.setLayoutParams(localLayoutParams);
        if ((!AndroidUtilities.isInMultiwindow) && (!this.forceFloatingEmoji)) {
          AndroidUtilities.hideKeyboard(this.messageEditText);
        }
        if (this.sizeNotifierLayout != null)
        {
          this.emojiPadding = paramInt;
          this.sizeNotifierLayout.requestLayout();
          this.emojiButton.setImageResource(NUM);
          onWindowSizeChanged();
        }
      }
    }
    for (;;)
    {
      return;
      paramInt = this.keyboardHeight;
      break;
      if (this.emojiButton != null) {
        this.emojiButton.setImageResource(NUM);
      }
      if (this.emojiView != null) {
        this.emojiView.setVisibility(8);
      }
      if (this.sizeNotifierLayout != null)
      {
        if (paramInt == 0) {
          this.emojiPadding = 0;
        }
        this.sizeNotifierLayout.requestLayout();
        onWindowSizeChanged();
      }
    }
  }
  
  public void addEmojiToRecent(String paramString)
  {
    createEmojiView();
    this.emojiView.addEmojiToRecent(paramString);
  }
  
  public void closeKeyboard()
  {
    AndroidUtilities.hideKeyboard(this.messageEditText);
  }
  
  public void didReceivedNotification(int paramInt1, int paramInt2, Object... paramVarArgs)
  {
    if ((paramInt1 == NotificationCenter.emojiDidLoaded) && (this.emojiView != null)) {
      this.emojiView.invalidateViews();
    }
  }
  
  public int getCursorPosition()
  {
    if (this.messageEditText == null) {}
    for (int i = 0;; i = this.messageEditText.getSelectionStart()) {
      return i;
    }
  }
  
  public int getEmojiPadding()
  {
    return this.emojiPadding;
  }
  
  public CharSequence getFieldCharSequence()
  {
    return this.messageEditText.getText();
  }
  
  public int getSelectionLength()
  {
    int i = 0;
    if (this.messageEditText == null) {}
    for (;;)
    {
      return i;
      try
      {
        int j = this.messageEditText.getSelectionEnd();
        int k = this.messageEditText.getSelectionStart();
        i = j - k;
      }
      catch (Exception localException)
      {
        FileLog.e(localException);
      }
    }
  }
  
  public boolean hideActionMode()
  {
    if ((Build.VERSION.SDK_INT >= 23) && (this.currentActionMode != null)) {}
    for (;;)
    {
      try
      {
        this.currentActionMode.finish();
        this.currentActionMode = null;
        bool = true;
        return bool;
      }
      catch (Exception localException)
      {
        FileLog.e(localException);
        continue;
      }
      boolean bool = false;
    }
  }
  
  public void hidePopup()
  {
    if (isPopupShowing()) {
      showPopup(0);
    }
  }
  
  public boolean isKeyboardVisible()
  {
    if (((AndroidUtilities.usingHardwareInput) && (getTag() != null)) || (this.keyboardVisible)) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public boolean isPopupShowing()
  {
    if ((this.emojiView != null) && (this.emojiView.getVisibility() == 0)) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public boolean isPopupView(View paramView)
  {
    if (paramView == this.emojiView) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public void onCreate()
  {
    NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiDidLoaded);
    this.sizeNotifierLayout.setDelegate(this);
  }
  
  public void onDestroy()
  {
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
  
  public void onSizeChanged(int paramInt, boolean paramBoolean)
  {
    if ((paramInt > AndroidUtilities.dp(50.0F)) && (this.keyboardVisible) && (!AndroidUtilities.isInMultiwindow) && (!this.forceFloatingEmoji))
    {
      if (paramBoolean)
      {
        this.keyboardHeightLand = paramInt;
        MessagesController.getGlobalEmojiSettings().edit().putInt("kbd_height_land3", this.keyboardHeightLand).commit();
      }
    }
    else if (isPopupShowing()) {
      if (!paramBoolean) {
        break label224;
      }
    }
    label224:
    for (int i = this.keyboardHeightLand;; i = this.keyboardHeight)
    {
      FrameLayout.LayoutParams localLayoutParams = (FrameLayout.LayoutParams)this.emojiView.getLayoutParams();
      if ((localLayoutParams.width != AndroidUtilities.displaySize.x) || (localLayoutParams.height != i))
      {
        localLayoutParams.width = AndroidUtilities.displaySize.x;
        localLayoutParams.height = i;
        this.emojiView.setLayoutParams(localLayoutParams);
        if (this.sizeNotifierLayout != null)
        {
          this.emojiPadding = localLayoutParams.height;
          this.sizeNotifierLayout.requestLayout();
          onWindowSizeChanged();
        }
      }
      if ((this.lastSizeChangeValue1 != paramInt) || (this.lastSizeChangeValue2 != paramBoolean)) {
        break label232;
      }
      onWindowSizeChanged();
      return;
      this.keyboardHeight = paramInt;
      MessagesController.getGlobalEmojiSettings().edit().putInt("kbd_height", this.keyboardHeight).commit();
      break;
    }
    label232:
    this.lastSizeChangeValue1 = paramInt;
    this.lastSizeChangeValue2 = paramBoolean;
    boolean bool = this.keyboardVisible;
    if (paramInt > 0) {}
    for (paramBoolean = true;; paramBoolean = false)
    {
      this.keyboardVisible = paramBoolean;
      if ((this.keyboardVisible) && (isPopupShowing())) {
        showPopup(0);
      }
      if ((this.emojiPadding != 0) && (!this.keyboardVisible) && (this.keyboardVisible != bool) && (!isPopupShowing()))
      {
        this.emojiPadding = 0;
        this.sizeNotifierLayout.requestLayout();
      }
      onWindowSizeChanged();
      break;
    }
  }
  
  public void openKeyboard()
  {
    try
    {
      i = this.messageEditText.getSelectionStart();
      MotionEvent localMotionEvent = MotionEvent.obtain(0L, 0L, 0, 0.0F, 0.0F, 0);
      this.messageEditText.onTouchEvent(localMotionEvent);
      localMotionEvent.recycle();
      localMotionEvent = MotionEvent.obtain(0L, 0L, 1, 0.0F, 0.0F, 0);
      this.messageEditText.onTouchEvent(localMotionEvent);
      localMotionEvent.recycle();
      AndroidUtilities.showKeyboard(this.messageEditText);
    }
    catch (Exception localException1)
    {
      try
      {
        this.messageEditText.setSelection(i);
        return;
        localException1 = localException1;
        int i = this.messageEditText.length();
        FileLog.e(localException1);
      }
      catch (Exception localException2)
      {
        for (;;)
        {
          FileLog.e(localException2);
        }
      }
    }
  }
  
  public void replaceWithText(int paramInt1, int paramInt2, CharSequence paramCharSequence, boolean paramBoolean)
  {
    for (;;)
    {
      try
      {
        SpannableStringBuilder localSpannableStringBuilder = new android/text/SpannableStringBuilder;
        localSpannableStringBuilder.<init>(this.messageEditText.getText());
        localSpannableStringBuilder.replace(paramInt1, paramInt1 + paramInt2, paramCharSequence);
        if (paramBoolean) {
          Emoji.replaceEmoji(localSpannableStringBuilder, this.messageEditText.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0F), false);
        }
        this.messageEditText.setText(localSpannableStringBuilder);
        if (paramCharSequence.length() + paramInt1 <= this.messageEditText.length())
        {
          this.messageEditText.setSelection(paramCharSequence.length() + paramInt1);
          return;
        }
      }
      catch (Exception paramCharSequence)
      {
        FileLog.e(paramCharSequence);
        continue;
      }
      this.messageEditText.setSelection(this.messageEditText.length());
    }
  }
  
  public void setDelegate(PhotoViewerCaptionEnterViewDelegate paramPhotoViewerCaptionEnterViewDelegate)
  {
    this.delegate = paramPhotoViewerCaptionEnterViewDelegate;
  }
  
  public void setFieldFocused(boolean paramBoolean)
  {
    if (this.messageEditText == null) {}
    for (;;)
    {
      return;
      if (paramBoolean)
      {
        if (!this.messageEditText.isFocused()) {
          this.messageEditText.postDelayed(new Runnable()
          {
            public void run()
            {
              if (PhotoViewerCaptionEnterView.this.messageEditText != null) {}
              try
              {
                PhotoViewerCaptionEnterView.this.messageEditText.requestFocus();
                return;
              }
              catch (Exception localException)
              {
                for (;;)
                {
                  FileLog.e(localException);
                }
              }
            }
          }, 600L);
        }
      }
      else if ((this.messageEditText.isFocused()) && (!this.keyboardVisible)) {
        this.messageEditText.clearFocus();
      }
    }
  }
  
  public void setFieldText(CharSequence paramCharSequence)
  {
    if (this.messageEditText == null) {}
    for (;;)
    {
      return;
      this.messageEditText.setText(paramCharSequence);
      this.messageEditText.setSelection(this.messageEditText.getText().length());
      if (this.delegate != null) {
        this.delegate.onTextChanged(this.messageEditText.getText());
      }
    }
  }
  
  public void setForceFloatingEmoji(boolean paramBoolean)
  {
    this.forceFloatingEmoji = paramBoolean;
  }
  
  public static abstract interface PhotoViewerCaptionEnterViewDelegate
  {
    public abstract void onCaptionEnter();
    
    public abstract void onTextChanged(CharSequence paramCharSequence);
    
    public abstract void onWindowSizeChanged(int paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/PhotoViewerCaptionEnterView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */