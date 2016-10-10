package org.telegram.ui.ActionBar;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.Callback;
import android.text.Layout;
import android.text.Layout.Alignment;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.View.MeasureSpec;
import org.telegram.messenger.AndroidUtilities;

public class SimpleTextView
  extends View
  implements Drawable.Callback
{
  private int drawablePadding = AndroidUtilities.dp(4.0F);
  private int gravity = 51;
  private Layout layout;
  private Drawable leftDrawable;
  private int leftDrawableTopPadding;
  private int offsetX;
  private Drawable rightDrawable;
  private int rightDrawableTopPadding;
  private SpannableStringBuilder spannableStringBuilder;
  private CharSequence text;
  private int textHeight;
  private TextPaint textPaint = new TextPaint(1);
  private int textWidth;
  private boolean wasLayout;
  
  public SimpleTextView(Context paramContext)
  {
    super(paramContext);
  }
  
  private void calcOffset(int paramInt)
  {
    if (this.layout.getLineCount() > 0)
    {
      this.textWidth = ((int)Math.ceil(this.layout.getLineWidth(0)));
      this.textHeight = this.layout.getLineBottom(0);
      if ((this.gravity & 0x7) == 3) {
        this.offsetX = (-(int)this.layout.getLineLeft(0));
      }
    }
    else
    {
      return;
    }
    if (this.layout.getLineLeft(0) == 0.0F)
    {
      this.offsetX = (paramInt - this.textWidth);
      return;
    }
    this.offsetX = (-AndroidUtilities.dp(8.0F));
  }
  
  private boolean createLayout(int paramInt)
  {
    int i;
    if (this.text != null) {
      i = paramInt;
    }
    for (;;)
    {
      try
      {
        if (this.leftDrawable != null) {
          i = paramInt - this.leftDrawable.getIntrinsicWidth() - this.drawablePadding;
        }
        paramInt = i;
        if (this.rightDrawable != null) {
          paramInt = i - this.rightDrawable.getIntrinsicWidth() - this.drawablePadding;
        }
        paramInt -= getPaddingLeft() + getPaddingRight();
        CharSequence localCharSequence = TextUtils.ellipsize(this.text, this.textPaint, paramInt, TextUtils.TruncateAt.END);
        if ((this.layout != null) && (TextUtils.equals(this.layout.getText(), localCharSequence)))
        {
          calcOffset(paramInt);
          return false;
        }
        this.layout = new StaticLayout(localCharSequence, 0, localCharSequence.length(), this.textPaint, AndroidUtilities.dp(8.0F) + paramInt, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
        calcOffset(paramInt);
      }
      catch (Exception localException)
      {
        continue;
      }
      invalidate();
      return true;
      this.layout = null;
      this.textWidth = 0;
      this.textHeight = 0;
    }
  }
  
  private boolean recreateLayoutMaybe()
  {
    if (this.wasLayout) {
      return createLayout(getMeasuredWidth());
    }
    requestLayout();
    return true;
  }
  
  public Paint getPaint()
  {
    return this.textPaint;
  }
  
  public int getSideDrawablesSize()
  {
    int i = 0;
    if (this.leftDrawable != null) {
      i = 0 + (this.leftDrawable.getIntrinsicWidth() + this.drawablePadding);
    }
    int j = i;
    if (this.rightDrawable != null) {
      j = i + (this.rightDrawable.getIntrinsicWidth() + this.drawablePadding);
    }
    return j;
  }
  
  public CharSequence getText()
  {
    if (this.text == null) {
      return "";
    }
    return this.text;
  }
  
  public int getTextHeight()
  {
    return this.textHeight;
  }
  
  public int getTextWidth()
  {
    return this.textWidth;
  }
  
  public boolean hasOverlappingRendering()
  {
    return false;
  }
  
  public void invalidateDrawable(Drawable paramDrawable)
  {
    if (paramDrawable == this.leftDrawable) {
      invalidate(this.leftDrawable.getBounds());
    }
    while (paramDrawable != this.rightDrawable) {
      return;
    }
    invalidate(this.rightDrawable.getBounds());
  }
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    this.wasLayout = false;
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    int j = 0;
    int i = j;
    if (this.leftDrawable != null)
    {
      i = (this.textHeight - this.leftDrawable.getIntrinsicHeight()) / 2 + this.leftDrawableTopPadding;
      this.leftDrawable.setBounds(0, i, this.leftDrawable.getIntrinsicWidth(), this.leftDrawable.getIntrinsicHeight() + i);
      this.leftDrawable.draw(paramCanvas);
      i = j;
      if ((this.gravity & 0x7) == 3) {
        i = 0 + (this.drawablePadding + this.leftDrawable.getIntrinsicWidth());
      }
    }
    if (this.rightDrawable != null)
    {
      int k = this.textWidth + i + this.drawablePadding;
      j = k;
      if (this.leftDrawable != null) {
        j = k + (this.drawablePadding + this.leftDrawable.getIntrinsicWidth());
      }
      k = (this.textHeight - this.rightDrawable.getIntrinsicHeight()) / 2 + this.rightDrawableTopPadding;
      this.rightDrawable.setBounds(j, k, this.rightDrawable.getIntrinsicWidth() + j, this.rightDrawable.getIntrinsicHeight() + k);
      this.rightDrawable.draw(paramCanvas);
    }
    if (this.layout != null)
    {
      if (this.offsetX + i != 0)
      {
        paramCanvas.save();
        paramCanvas.translate(this.offsetX + i, 0.0F);
      }
      this.layout.draw(paramCanvas);
      if (this.offsetX + i != 0) {
        paramCanvas.restore();
      }
    }
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    this.wasLayout = true;
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    int i = View.MeasureSpec.getSize(paramInt1);
    paramInt1 = View.MeasureSpec.getSize(paramInt2);
    createLayout(i - getPaddingLeft() - getPaddingRight());
    if (View.MeasureSpec.getMode(paramInt2) == 1073741824) {}
    for (;;)
    {
      setMeasuredDimension(i, paramInt1);
      return;
      paramInt1 = this.textHeight;
    }
  }
  
  public void setDrawablePadding(int paramInt)
  {
    if (this.drawablePadding == paramInt) {}
    do
    {
      return;
      this.drawablePadding = paramInt;
    } while (recreateLayoutMaybe());
    invalidate();
  }
  
  public void setGravity(int paramInt)
  {
    this.gravity = paramInt;
  }
  
  public void setLeftDrawable(int paramInt)
  {
    if (paramInt == 0) {}
    for (Drawable localDrawable = null;; localDrawable = getContext().getResources().getDrawable(paramInt))
    {
      setLeftDrawable(localDrawable);
      return;
    }
  }
  
  public void setLeftDrawable(Drawable paramDrawable)
  {
    if (this.leftDrawable == paramDrawable) {}
    do
    {
      return;
      if (this.leftDrawable != null) {
        this.leftDrawable.setCallback(null);
      }
      this.leftDrawable = paramDrawable;
      if (paramDrawable != null) {
        paramDrawable.setCallback(this);
      }
    } while (recreateLayoutMaybe());
    invalidate();
  }
  
  public void setLeftDrawableTopPadding(int paramInt)
  {
    this.leftDrawableTopPadding = paramInt;
  }
  
  public void setRightDrawable(int paramInt)
  {
    if (paramInt == 0) {}
    for (Drawable localDrawable = null;; localDrawable = getContext().getResources().getDrawable(paramInt))
    {
      setRightDrawable(localDrawable);
      return;
    }
  }
  
  public void setRightDrawable(Drawable paramDrawable)
  {
    if (this.rightDrawable == paramDrawable) {}
    do
    {
      return;
      if (this.rightDrawable != null) {
        this.rightDrawable.setCallback(null);
      }
      this.rightDrawable = paramDrawable;
      if (paramDrawable != null) {
        paramDrawable.setCallback(this);
      }
    } while (recreateLayoutMaybe());
    invalidate();
  }
  
  public void setRightDrawableTopPadding(int paramInt)
  {
    this.rightDrawableTopPadding = paramInt;
  }
  
  public void setText(CharSequence paramCharSequence)
  {
    if (((this.text == null) && (paramCharSequence == null)) || ((this.text != null) && (paramCharSequence != null) && (this.text.equals(paramCharSequence)))) {
      return;
    }
    this.text = paramCharSequence;
    recreateLayoutMaybe();
  }
  
  public void setTextColor(int paramInt)
  {
    this.textPaint.setColor(paramInt);
    invalidate();
  }
  
  public void setTextSize(int paramInt)
  {
    paramInt = AndroidUtilities.dp(paramInt);
    if (paramInt == this.textPaint.getTextSize()) {}
    do
    {
      return;
      this.textPaint.setTextSize(paramInt);
    } while (recreateLayoutMaybe());
    invalidate();
  }
  
  public void setTypeface(Typeface paramTypeface)
  {
    this.textPaint.setTypeface(paramTypeface);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/ActionBar/SimpleTextView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */