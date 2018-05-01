package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.os.SystemClock;
import android.text.Layout;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;

public class EditTextBoldCursor
  extends EditText
{
  private static Method getVerticalOffsetMethod;
  private static Field mCursorDrawableField;
  private static Field mCursorDrawableResField;
  private static Field mEditor;
  private static Field mScrollYField;
  private static Field mShowCursorField;
  private boolean allowDrawCursor = true;
  private int cursorSize;
  private float cursorWidth = 2.0F;
  private Object editor;
  private GradientDrawable gradientDrawable;
  private float hintAlpha = 1.0F;
  private int hintColor;
  private StaticLayout hintLayout;
  private boolean hintVisible = true;
  private int ignoreBottomCount;
  private int ignoreTopCount;
  private long lastUpdateTime;
  private float lineSpacingExtra;
  private Drawable[] mCursorDrawable;
  private Rect rect = new Rect();
  private int scrollY;
  
  public EditTextBoldCursor(Context paramContext)
  {
    super(paramContext);
    if (mCursorDrawableField == null) {}
    try
    {
      mScrollYField = View.class.getDeclaredField("mScrollY");
      mScrollYField.setAccessible(true);
      mCursorDrawableResField = TextView.class.getDeclaredField("mCursorDrawableRes");
      mCursorDrawableResField.setAccessible(true);
      mEditor = TextView.class.getDeclaredField("mEditor");
      mEditor.setAccessible(true);
      paramContext = Class.forName("android.widget.Editor");
      mShowCursorField = paramContext.getDeclaredField("mShowCursor");
      mShowCursorField.setAccessible(true);
      mCursorDrawableField = paramContext.getDeclaredField("mCursorDrawable");
      mCursorDrawableField.setAccessible(true);
      getVerticalOffsetMethod = TextView.class.getDeclaredMethod("getVerticalOffset", new Class[] { Boolean.TYPE });
      getVerticalOffsetMethod.setAccessible(true);
      try
      {
        paramContext = new android/graphics/drawable/GradientDrawable;
        paramContext.<init>(GradientDrawable.Orientation.TOP_BOTTOM, new int[] { -11230757, -11230757 });
        this.gradientDrawable = paramContext;
        this.editor = mEditor.get(this);
        this.mCursorDrawable = ((Drawable[])mCursorDrawableField.get(this.editor));
        mCursorDrawableResField.set(this, Integer.valueOf(NUM));
        this.cursorSize = AndroidUtilities.dp(24.0F);
        return;
      }
      catch (Exception paramContext)
      {
        for (;;)
        {
          FileLog.e(paramContext);
        }
      }
    }
    catch (Throwable paramContext)
    {
      for (;;) {}
    }
  }
  
  public int getExtendedPaddingBottom()
  {
    int i;
    if (this.ignoreBottomCount != 0)
    {
      this.ignoreBottomCount -= 1;
      if (this.scrollY != Integer.MAX_VALUE) {
        i = -this.scrollY;
      }
    }
    for (;;)
    {
      return i;
      i = 0;
      continue;
      i = super.getExtendedPaddingBottom();
    }
  }
  
  public int getExtendedPaddingTop()
  {
    if (this.ignoreTopCount != 0) {
      this.ignoreTopCount -= 1;
    }
    for (int i = 0;; i = super.getExtendedPaddingTop()) {
      return i;
    }
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    int i = getExtendedPaddingTop();
    this.scrollY = Integer.MAX_VALUE;
    try
    {
      this.scrollY = mScrollYField.getInt(this);
      mScrollYField.set(this, Integer.valueOf(0));
      this.ignoreTopCount = 1;
      this.ignoreBottomCount = 1;
      paramCanvas.save();
      paramCanvas.translate(0.0F, i);
      try
      {
        super.onDraw(paramCanvas);
        if (this.scrollY == Integer.MAX_VALUE) {}
      }
      catch (Exception localException1)
      {
        try
        {
          mScrollYField.set(this, Integer.valueOf(this.scrollY));
          paramCanvas.restore();
          long l3;
          int j;
          if ((length() == 0) && (this.hintLayout != null) && ((this.hintVisible) || (this.hintAlpha != 0.0F)))
          {
            if (((this.hintVisible) && (this.hintAlpha != 1.0F)) || ((!this.hintVisible) && (this.hintAlpha != 0.0F)))
            {
              long l1 = System.currentTimeMillis();
              long l2 = l1 - this.lastUpdateTime;
              if (l2 >= 0L)
              {
                l3 = l2;
                if (l2 <= 17L) {}
              }
              else
              {
                l3 = 17L;
              }
              this.lastUpdateTime = l1;
              if (!this.hintVisible) {
                break label664;
              }
              this.hintAlpha += (float)l3 / 150.0F;
              if (this.hintAlpha > 1.0F) {
                this.hintAlpha = 1.0F;
              }
              invalidate();
            }
            j = getPaint().getColor();
            getPaint().setColor(this.hintColor);
            getPaint().setAlpha((int)(255.0F * this.hintAlpha));
            paramCanvas.save();
            i = 0;
            float f = this.hintLayout.getLineLeft(0);
            if (f != 0.0F) {
              i = (int)(0 - f);
            }
            paramCanvas.translate(i, (getMeasuredHeight() - this.hintLayout.getHeight()) / 2.0F);
            this.hintLayout.draw(paramCanvas);
            getPaint().setColor(j);
            paramCanvas.restore();
          }
          for (;;)
          {
            try
            {
              if ((this.allowDrawCursor) && (mShowCursorField != null) && (this.mCursorDrawable != null) && (this.mCursorDrawable[0] != null))
              {
                l3 = mShowCursorField.getLong(this.editor);
                if (((SystemClock.uptimeMillis() - l3) % 1000L >= 500L) || (!isFocused())) {
                  continue;
                }
                i = 1;
                if (i != 0)
                {
                  paramCanvas.save();
                  i = 0;
                  if ((getGravity() & 0x70) != 48) {
                    i = ((Integer)getVerticalOffsetMethod.invoke(this, new Object[] { Boolean.valueOf(true) })).intValue();
                  }
                  paramCanvas.translate(getPaddingLeft(), getExtendedPaddingTop() + i);
                  Object localObject = getLayout();
                  i = ((Layout)localObject).getLineForOffset(getSelectionStart());
                  j = ((Layout)localObject).getLineCount();
                  localObject = this.mCursorDrawable[0].getBounds();
                  this.rect.left = ((Rect)localObject).left;
                  this.rect.right = (((Rect)localObject).left + AndroidUtilities.dp(this.cursorWidth));
                  this.rect.bottom = ((Rect)localObject).bottom;
                  this.rect.top = ((Rect)localObject).top;
                  if ((this.lineSpacingExtra != 0.0F) && (i < j - 1))
                  {
                    localObject = this.rect;
                    ((Rect)localObject).bottom = ((int)(((Rect)localObject).bottom - this.lineSpacingExtra));
                  }
                  this.rect.top = (this.rect.centerY() - this.cursorSize / 2);
                  this.rect.bottom = (this.rect.top + this.cursorSize);
                  this.gradientDrawable.setBounds(this.rect);
                  this.gradientDrawable.draw(paramCanvas);
                  paramCanvas.restore();
                }
              }
              return;
            }
            catch (Throwable paramCanvas)
            {
              label664:
              continue;
            }
            this.hintAlpha -= (float)l3 / 150.0F;
            if (this.hintAlpha >= 0.0F) {
              break;
            }
            this.hintAlpha = 0.0F;
            break;
            i = 0;
          }
          localException1 = localException1;
        }
        catch (Exception localException2)
        {
          for (;;) {}
        }
      }
    }
    catch (Exception localException3)
    {
      for (;;) {}
    }
  }
  
  public void setAllowDrawCursor(boolean paramBoolean)
  {
    this.allowDrawCursor = paramBoolean;
  }
  
  public void setCursorColor(int paramInt)
  {
    this.gradientDrawable.setColor(paramInt);
    invalidate();
  }
  
  public void setCursorSize(int paramInt)
  {
    this.cursorSize = paramInt;
  }
  
  public void setCursorWidth(float paramFloat)
  {
    this.cursorWidth = paramFloat;
  }
  
  public void setHintColor(int paramInt)
  {
    this.hintColor = paramInt;
    invalidate();
  }
  
  public void setHintText(String paramString)
  {
    this.hintLayout = new StaticLayout(paramString, getPaint(), AndroidUtilities.dp(1000.0F), Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
  }
  
  public void setHintVisible(boolean paramBoolean)
  {
    if (this.hintVisible == paramBoolean) {}
    for (;;)
    {
      return;
      this.lastUpdateTime = System.currentTimeMillis();
      this.hintVisible = paramBoolean;
      invalidate();
    }
  }
  
  public void setLineSpacing(float paramFloat1, float paramFloat2)
  {
    super.setLineSpacing(paramFloat1, paramFloat2);
    this.lineSpacingExtra = paramFloat1;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/EditTextBoldCursor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */