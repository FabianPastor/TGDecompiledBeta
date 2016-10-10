package org.telegram.ui.Components;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Point;
import android.os.Build.VERSION;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
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
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.StickerSetCovered;

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
  
  public PhotoViewerCaptionEnterView(Context paramContext, SizeNotifierFrameLayoutPhoto paramSizeNotifierFrameLayoutPhoto, View paramView)
  {
    super(paramContext);
    setBackgroundColor(2130706432);
    setFocusable(true);
    setFocusableInTouchMode(true);
    this.windowView = paramView;
    this.sizeNotifierLayout = paramSizeNotifierFrameLayoutPhoto;
    paramView = new LinearLayout(paramContext);
    paramView.setOrientation(0);
    addView(paramView, LayoutHelper.createFrame(-1, -2.0F, 51, 2.0F, 0.0F, 0.0F, 0.0F));
    paramSizeNotifierFrameLayoutPhoto = new FrameLayout(paramContext);
    paramView.addView(paramSizeNotifierFrameLayoutPhoto, LayoutHelper.createLinear(0, -2, 1.0F));
    this.emojiButton = new ImageView(paramContext);
    this.emojiButton.setImageResource(2130837752);
    this.emojiButton.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
    this.emojiButton.setPadding(AndroidUtilities.dp(4.0F), AndroidUtilities.dp(1.0F), 0, 0);
    paramSizeNotifierFrameLayoutPhoto.addView(this.emojiButton, LayoutHelper.createFrame(48, 48, 83));
    this.emojiButton.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        if (!PhotoViewerCaptionEnterView.this.isPopupShowing())
        {
          PhotoViewerCaptionEnterView.this.showPopup(1);
          return;
        }
        PhotoViewerCaptionEnterView.this.openKeyboardInternal();
      }
    });
    this.messageEditText = new EditText(paramContext)
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
          setMeasuredDimension(View.MeasureSpec.getSize(paramAnonymousInt1), AndroidUtilities.dp(51.0F));
          FileLog.e("tmessages", localException);
        }
      }
    };
    if (Build.VERSION.SDK_INT >= 23)
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
    this.messageEditText.setHint(LocaleController.getString("AddCaption", 2131165255));
    this.messageEditText.setImeOptions(268435456);
    this.messageEditText.setInputType(this.messageEditText.getInputType() | 0x4000);
    this.messageEditText.setMaxLines(4);
    this.messageEditText.setHorizontallyScrolling(false);
    this.messageEditText.setTextSize(1, 18.0F);
    this.messageEditText.setGravity(80);
    this.messageEditText.setPadding(0, AndroidUtilities.dp(11.0F), 0, AndroidUtilities.dp(12.0F));
    this.messageEditText.setBackgroundDrawable(null);
    AndroidUtilities.clearCursorDrawable(this.messageEditText);
    this.messageEditText.setTextColor(-1);
    this.messageEditText.setHintTextColor(-1291845633);
    paramContext = new InputFilter.LengthFilter(200);
    this.messageEditText.setFilters(new InputFilter[] { paramContext });
    paramSizeNotifierFrameLayoutPhoto.addView(this.messageEditText, LayoutHelper.createFrame(-1, -2.0F, 83, 52.0F, 0.0F, 6.0F, 0.0F));
    this.messageEditText.setOnKeyListener(new View.OnKeyListener()
    {
      public boolean onKey(View paramAnonymousView, int paramAnonymousInt, KeyEvent paramAnonymousKeyEvent)
      {
        if (paramAnonymousInt == 4)
        {
          if (PhotoViewerCaptionEnterView.this.hideActionMode()) {}
          do
          {
            return true;
            if ((PhotoViewerCaptionEnterView.this.keyboardVisible) || (!PhotoViewerCaptionEnterView.this.isPopupShowing())) {
              break;
            }
          } while (paramAnonymousKeyEvent.getAction() != 1);
          PhotoViewerCaptionEnterView.this.showPopup(0);
          return true;
        }
        return false;
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
        while (!this.processChange) {
          return;
        }
        ImageSpan[] arrayOfImageSpan = (ImageSpan[])paramAnonymousEditable.getSpans(0, paramAnonymousEditable.length(), ImageSpan.class);
        int i = 0;
        while (i < arrayOfImageSpan.length)
        {
          paramAnonymousEditable.removeSpan(arrayOfImageSpan[i]);
          i += 1;
        }
        Emoji.replaceEmoji(paramAnonymousEditable, PhotoViewerCaptionEnterView.this.messageEditText.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0F), false);
        this.processChange = false;
      }
      
      public void beforeTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {}
      
      public void onTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3)
      {
        if (PhotoViewerCaptionEnterView.this.innerTextChange) {}
        do
        {
          return;
          if (PhotoViewerCaptionEnterView.this.delegate != null) {
            PhotoViewerCaptionEnterView.this.delegate.onTextChanged(paramAnonymousCharSequence);
          }
        } while ((paramAnonymousInt2 == paramAnonymousInt3) || (paramAnonymousInt3 - paramAnonymousInt2 <= 1));
        this.processChange = true;
      }
    });
  }
  
  private void fixActionMode(ActionMode paramActionMode)
  {
    try
    {
      Object localObject1 = Class.forName("com.android.internal.view.FloatingActionMode");
      Object localObject2 = ((Class)localObject1).getDeclaredField("mFloatingToolbar");
      ((Field)localObject2).setAccessible(true);
      localObject2 = ((Field)localObject2).get(paramActionMode);
      Object localObject3 = Class.forName("com.android.internal.widget.FloatingToolbar");
      Field localField = ((Class)localObject3).getDeclaredField("mPopup");
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
      FileLog.e("tmessages", paramActionMode);
    }
  }
  
  private void onWindowSizeChanged()
  {
    int j = this.sizeNotifierLayout.getHeight();
    int i = j;
    if (!this.keyboardVisible) {
      i = j - this.emojiPadding;
    }
    if (this.delegate != null) {
      this.delegate.onWindowSizeChanged(i);
    }
  }
  
  private void openKeyboardInternal()
  {
    if (AndroidUtilities.usingHardwareInput) {}
    for (int i = 0;; i = 2)
    {
      showPopup(i);
      AndroidUtilities.showKeyboard(this.messageEditText);
      return;
    }
  }
  
  private void showPopup(int paramInt)
  {
    if (paramInt == 1)
    {
      if (this.emojiView == null)
      {
        this.emojiView = new EmojiView(false, false, getContext());
        this.emojiView.setListener(new EmojiView.Listener()
        {
          public boolean onBackspace()
          {
            if (PhotoViewerCaptionEnterView.this.messageEditText.length() == 0) {
              return false;
            }
            PhotoViewerCaptionEnterView.this.messageEditText.dispatchKeyEvent(new KeyEvent(0, 67));
            return true;
          }
          
          public void onClearEmojiRecent() {}
          
          public void onEmojiSelected(String paramAnonymousString)
          {
            if (PhotoViewerCaptionEnterView.this.messageEditText.length() + paramAnonymousString.length() > 200) {
              return;
            }
            int j = PhotoViewerCaptionEnterView.this.messageEditText.getSelectionEnd();
            int i = j;
            if (j < 0) {
              i = 0;
            }
            try
            {
              PhotoViewerCaptionEnterView.access$502(PhotoViewerCaptionEnterView.this, true);
              paramAnonymousString = Emoji.replaceEmoji(paramAnonymousString, PhotoViewerCaptionEnterView.this.messageEditText.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0F), false);
              PhotoViewerCaptionEnterView.this.messageEditText.setText(PhotoViewerCaptionEnterView.this.messageEditText.getText().insert(i, paramAnonymousString));
              i += paramAnonymousString.length();
              PhotoViewerCaptionEnterView.this.messageEditText.setSelection(i, i);
              return;
            }
            catch (Exception paramAnonymousString)
            {
              FileLog.e("tmessages", paramAnonymousString);
              return;
            }
            finally
            {
              PhotoViewerCaptionEnterView.access$502(PhotoViewerCaptionEnterView.this, false);
            }
          }
          
          public void onGifSelected(TLRPC.Document paramAnonymousDocument) {}
          
          public void onGifTab(boolean paramAnonymousBoolean) {}
          
          public void onShowStickerSet(TLRPC.StickerSetCovered paramAnonymousStickerSetCovered) {}
          
          public void onStickerSelected(TLRPC.Document paramAnonymousDocument) {}
          
          public void onStickerSetAdd(TLRPC.StickerSetCovered paramAnonymousStickerSetCovered) {}
          
          public void onStickerSetRemove(TLRPC.StickerSetCovered paramAnonymousStickerSetCovered) {}
          
          public void onStickersSettingsClick() {}
          
          public void onStickersTab(boolean paramAnonymousBoolean) {}
        });
        this.sizeNotifierLayout.addView(this.emojiView);
      }
      this.emojiView.setVisibility(0);
      if (this.keyboardHeight <= 0) {
        this.keyboardHeight = ApplicationLoader.applicationContext.getSharedPreferences("emoji", 0).getInt("kbd_height", AndroidUtilities.dp(200.0F));
      }
      if (this.keyboardHeightLand <= 0) {
        this.keyboardHeightLand = ApplicationLoader.applicationContext.getSharedPreferences("emoji", 0).getInt("kbd_height_land3", AndroidUtilities.dp(200.0F));
      }
      if (AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y)
      {
        paramInt = this.keyboardHeightLand;
        FrameLayout.LayoutParams localLayoutParams = (FrameLayout.LayoutParams)this.emojiView.getLayoutParams();
        localLayoutParams.width = AndroidUtilities.displaySize.x;
        localLayoutParams.height = paramInt;
        this.emojiView.setLayoutParams(localLayoutParams);
        if (!AndroidUtilities.isInMultiwindow) {
          AndroidUtilities.hideKeyboard(this.messageEditText);
        }
        if (this.sizeNotifierLayout != null)
        {
          this.emojiPadding = paramInt;
          this.sizeNotifierLayout.requestLayout();
          this.emojiButton.setImageResource(2130837735);
          onWindowSizeChanged();
        }
      }
    }
    do
    {
      return;
      paramInt = this.keyboardHeight;
      break;
      if (this.emojiButton != null) {
        this.emojiButton.setImageResource(2130837752);
      }
      if (this.emojiView != null) {
        this.emojiView.setVisibility(8);
      }
    } while (this.sizeNotifierLayout == null);
    if (paramInt == 0) {
      this.emojiPadding = 0;
    }
    this.sizeNotifierLayout.requestLayout();
    onWindowSizeChanged();
  }
  
  public void closeKeyboard()
  {
    AndroidUtilities.hideKeyboard(this.messageEditText);
  }
  
  public void didReceivedNotification(int paramInt, Object... paramVarArgs)
  {
    if ((paramInt == NotificationCenter.emojiDidLoaded) && (this.emojiView != null)) {
      this.emojiView.invalidateViews();
    }
  }
  
  public int getCursorPosition()
  {
    if (this.messageEditText == null) {
      return 0;
    }
    return this.messageEditText.getSelectionStart();
  }
  
  public int getEmojiPadding()
  {
    return this.emojiPadding;
  }
  
  public CharSequence getFieldCharSequence()
  {
    return this.messageEditText.getText();
  }
  
  public boolean hideActionMode()
  {
    if ((Build.VERSION.SDK_INT >= 23) && (this.currentActionMode != null)) {
      try
      {
        this.currentActionMode.finish();
        this.currentActionMode = null;
        return true;
      }
      catch (Exception localException)
      {
        for (;;)
        {
          FileLog.e("tmessages", localException);
        }
      }
    }
    return false;
  }
  
  public void hidePopup()
  {
    if (isPopupShowing()) {
      showPopup(0);
    }
  }
  
  public boolean isKeyboardVisible()
  {
    return ((AndroidUtilities.usingHardwareInput) && (getLayoutParams() != null) && (((FrameLayout.LayoutParams)getLayoutParams()).bottomMargin == 0)) || (this.keyboardVisible);
  }
  
  public boolean isPopupShowing()
  {
    return (this.emojiView != null) && (this.emojiView.getVisibility() == 0);
  }
  
  public boolean isPopupView(View paramView)
  {
    return paramView == this.emojiView;
  }
  
  public void onCreate()
  {
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.emojiDidLoaded);
    this.sizeNotifierLayout.setDelegate(this);
  }
  
  public void onDestroy()
  {
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
  
  public void onSizeChanged(int paramInt, boolean paramBoolean)
  {
    if ((paramInt > AndroidUtilities.dp(50.0F)) && (this.keyboardVisible) && (!AndroidUtilities.isInMultiwindow))
    {
      if (paramBoolean)
      {
        this.keyboardHeightLand = paramInt;
        ApplicationLoader.applicationContext.getSharedPreferences("emoji", 0).edit().putInt("kbd_height_land3", this.keyboardHeightLand).commit();
      }
    }
    else if (isPopupShowing()) {
      if (!paramBoolean) {
        break label231;
      }
    }
    label231:
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
        break label239;
      }
      onWindowSizeChanged();
      return;
      this.keyboardHeight = paramInt;
      ApplicationLoader.applicationContext.getSharedPreferences("emoji", 0).edit().putInt("kbd_height", this.keyboardHeight).commit();
      break;
    }
    label239:
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
      return;
    }
  }
  
  public void openKeyboard()
  {
    this.messageEditText.requestFocus();
    AndroidUtilities.showKeyboard(this.messageEditText);
  }
  
  public void replaceWithText(int paramInt1, int paramInt2, String paramString)
  {
    try
    {
      StringBuilder localStringBuilder = new StringBuilder(this.messageEditText.getText());
      localStringBuilder.replace(paramInt1, paramInt1 + paramInt2, paramString);
      this.messageEditText.setText(localStringBuilder);
      if (paramString.length() + paramInt1 <= this.messageEditText.length())
      {
        this.messageEditText.setSelection(paramString.length() + paramInt1);
        return;
      }
      this.messageEditText.setSelection(this.messageEditText.length());
      return;
    }
    catch (Exception paramString)
    {
      FileLog.e("tmessages", paramString);
    }
  }
  
  public void setDelegate(PhotoViewerCaptionEnterViewDelegate paramPhotoViewerCaptionEnterViewDelegate)
  {
    this.delegate = paramPhotoViewerCaptionEnterViewDelegate;
  }
  
  public void setFieldFocused(boolean paramBoolean)
  {
    if (this.messageEditText == null) {}
    do
    {
      do
      {
        return;
        if (!paramBoolean) {
          break;
        }
      } while (this.messageEditText.isFocused());
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
            FileLog.e("tmessages", localException);
          }
        }
      }, 600L);
      return;
    } while ((!this.messageEditText.isFocused()) || (this.keyboardVisible));
    this.messageEditText.clearFocus();
  }
  
  public void setFieldText(CharSequence paramCharSequence)
  {
    if (this.messageEditText == null) {}
    do
    {
      return;
      this.messageEditText.setText(paramCharSequence);
      this.messageEditText.setSelection(this.messageEditText.getText().length());
    } while (this.delegate == null);
    this.delegate.onTextChanged(this.messageEditText.getText());
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