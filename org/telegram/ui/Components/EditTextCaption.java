package org.telegram.ui.Components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build.VERSION;
import android.text.Editable;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.text.style.CharacterStyle;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View.MeasureSpec;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;

public class EditTextCaption
  extends EditTextBoldCursor
{
  private String caption;
  private StaticLayout captionLayout;
  private boolean copyPasteShowed;
  private int hintColor;
  private int triesCount = 0;
  private int userNameLength;
  private int xOffset;
  private int yOffset;
  
  public EditTextCaption(Context paramContext)
  {
    super(paramContext);
  }
  
  private void applyTextStyleToSelection(TypefaceSpan paramTypefaceSpan)
  {
    int i = getSelectionStart();
    int j = getSelectionEnd();
    Editable localEditable = getText();
    CharacterStyle[] arrayOfCharacterStyle = (CharacterStyle[])localEditable.getSpans(i, j, CharacterStyle.class);
    if ((arrayOfCharacterStyle != null) && (arrayOfCharacterStyle.length > 0)) {
      for (int k = 0; k < arrayOfCharacterStyle.length; k++)
      {
        CharacterStyle localCharacterStyle = arrayOfCharacterStyle[k];
        int m = localEditable.getSpanStart(localCharacterStyle);
        int n = localEditable.getSpanEnd(localCharacterStyle);
        localEditable.removeSpan(localCharacterStyle);
        if (m < i) {
          localEditable.setSpan(localCharacterStyle, m, i, 33);
        }
        if (n > j) {
          localEditable.setSpan(localCharacterStyle, j, n, 33);
        }
      }
    }
    if (paramTypefaceSpan != null) {
      localEditable.setSpan(paramTypefaceSpan, i, j, 33);
    }
  }
  
  private void makeSelectedBold()
  {
    applyTextStyleToSelection(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf")));
  }
  
  private void makeSelectedItalic()
  {
    applyTextStyleToSelection(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/ritalic.ttf")));
  }
  
  private void makeSelectedRegular()
  {
    applyTextStyleToSelection(null);
  }
  
  private ActionMode.Callback overrideCallback(final ActionMode.Callback paramCallback)
  {
    new ActionMode.Callback()
    {
      public boolean onActionItemClicked(ActionMode paramAnonymousActionMode, MenuItem paramAnonymousMenuItem)
      {
        boolean bool1 = true;
        if (paramAnonymousMenuItem.getItemId() == NUM)
        {
          EditTextCaption.this.makeSelectedRegular();
          paramAnonymousActionMode.finish();
        }
        for (;;)
        {
          return bool1;
          if (paramAnonymousMenuItem.getItemId() == NUM)
          {
            EditTextCaption.this.makeSelectedBold();
            paramAnonymousActionMode.finish();
          }
          else if (paramAnonymousMenuItem.getItemId() == NUM)
          {
            EditTextCaption.this.makeSelectedItalic();
            paramAnonymousActionMode.finish();
          }
          else
          {
            try
            {
              boolean bool2 = paramCallback.onActionItemClicked(paramAnonymousActionMode, paramAnonymousMenuItem);
              bool1 = bool2;
            }
            catch (Exception paramAnonymousActionMode) {}
          }
        }
      }
      
      public boolean onCreateActionMode(ActionMode paramAnonymousActionMode, Menu paramAnonymousMenu)
      {
        EditTextCaption.access$002(EditTextCaption.this, true);
        return paramCallback.onCreateActionMode(paramAnonymousActionMode, paramAnonymousMenu);
      }
      
      public void onDestroyActionMode(ActionMode paramAnonymousActionMode)
      {
        EditTextCaption.access$002(EditTextCaption.this, false);
        paramCallback.onDestroyActionMode(paramAnonymousActionMode);
      }
      
      public boolean onPrepareActionMode(ActionMode paramAnonymousActionMode, Menu paramAnonymousMenu)
      {
        return paramCallback.onPrepareActionMode(paramAnonymousActionMode, paramAnonymousMenu);
      }
    };
  }
  
  public String getCaption()
  {
    return this.caption;
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    super.onDraw(paramCanvas);
    try
    {
      if ((this.captionLayout != null) && (this.userNameLength == length()))
      {
        TextPaint localTextPaint = getPaint();
        int i = getPaint().getColor();
        localTextPaint.setColor(this.hintColor);
        paramCanvas.save();
        paramCanvas.translate(this.xOffset, this.yOffset);
        this.captionLayout.draw(paramCanvas);
        paramCanvas.restore();
        localTextPaint.setColor(i);
      }
      return;
    }
    catch (Exception paramCanvas)
    {
      for (;;)
      {
        FileLog.e(paramCanvas);
      }
    }
  }
  
  @SuppressLint({"DrawAllocation"})
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    try
    {
      super.onMeasure(paramInt1, paramInt2);
      this.captionLayout = null;
      if ((this.caption != null) && (this.caption.length() > 0))
      {
        Editable localEditable = getText();
        if ((localEditable.length() > 1) && (localEditable.charAt(0) == '@'))
        {
          paramInt1 = TextUtils.indexOf(localEditable, ' ');
          if (paramInt1 != -1)
          {
            localObject1 = getPaint();
            localObject2 = localEditable.subSequence(0, paramInt1 + 1);
            paramInt1 = (int)Math.ceil(((TextPaint)localObject1).measureText(localEditable, 0, paramInt1 + 1));
            paramInt2 = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
            this.userNameLength = ((CharSequence)localObject2).length();
            localObject1 = TextUtils.ellipsize(this.caption, (TextPaint)localObject1, paramInt2 - paramInt1, TextUtils.TruncateAt.END);
            this.xOffset = paramInt1;
          }
        }
      }
    }
    catch (Exception localException1)
    {
      for (;;)
      {
        try
        {
          Object localObject1;
          Object localObject2 = new android/text/StaticLayout;
          ((StaticLayout)localObject2).<init>((CharSequence)localObject1, getPaint(), paramInt2 - paramInt1, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
          this.captionLayout = ((StaticLayout)localObject2);
          if (this.captionLayout.getLineCount() > 0) {
            this.xOffset = ((int)(this.xOffset + -this.captionLayout.getLineLeft(0)));
          }
          this.yOffset = ((getMeasuredHeight() - this.captionLayout.getLineBottom(0)) / 2 + AndroidUtilities.dp(0.5F));
          return;
        }
        catch (Exception localException2)
        {
          FileLog.e(localException2);
          continue;
        }
        localException1 = localException1;
        setMeasuredDimension(View.MeasureSpec.getSize(paramInt1), AndroidUtilities.dp(51.0F));
        FileLog.e(localException1);
      }
    }
  }
  
  public void onWindowFocusChanged(boolean paramBoolean)
  {
    if ((Build.VERSION.SDK_INT < 23) && (!paramBoolean) && (this.copyPasteShowed)) {}
    for (;;)
    {
      return;
      super.onWindowFocusChanged(paramBoolean);
    }
  }
  
  public void setCaption(String paramString)
  {
    if (((this.caption != null) && (this.caption.length() != 0)) || ((paramString == null) || (paramString.length() == 0) || ((this.caption != null) && (paramString != null) && (this.caption.equals(paramString))))) {}
    for (;;)
    {
      return;
      this.caption = paramString;
      if (this.caption != null) {
        this.caption = this.caption.replace('\n', ' ');
      }
      requestLayout();
    }
  }
  
  public void setHintColor(int paramInt)
  {
    super.setHintColor(paramInt);
    this.hintColor = paramInt;
    invalidate();
  }
  
  public ActionMode startActionMode(ActionMode.Callback paramCallback)
  {
    return super.startActionMode(overrideCallback(paramCallback));
  }
  
  public ActionMode startActionMode(ActionMode.Callback paramCallback, int paramInt)
  {
    return super.startActionMode(overrideCallback(paramCallback), paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/EditTextCaption.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */