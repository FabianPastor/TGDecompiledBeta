package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.ui.ActionBar.Theme;

public class LetterDrawable
  extends Drawable
{
  private static TextPaint namePaint;
  public static Paint paint = new Paint();
  private StringBuilder stringBuilder = new StringBuilder(5);
  private float textHeight;
  private StaticLayout textLayout;
  private float textLeft;
  private float textWidth;
  
  public LetterDrawable()
  {
    if (namePaint == null)
    {
      paint.setColor(Theme.getColor("sharedMedia_linkPlaceholder"));
      namePaint = new TextPaint(1);
      namePaint.setColor(Theme.getColor("sharedMedia_linkPlaceholderText"));
    }
    namePaint.setTextSize(AndroidUtilities.dp(28.0F));
  }
  
  public void draw(Canvas paramCanvas)
  {
    Rect localRect = getBounds();
    if (localRect == null) {}
    for (;;)
    {
      return;
      int i = localRect.width();
      paramCanvas.save();
      paramCanvas.drawRect(localRect.left, localRect.top, localRect.right, localRect.bottom, paint);
      if (this.textLayout != null)
      {
        paramCanvas.translate(localRect.left + (i - this.textWidth) / 2.0F - this.textLeft, localRect.top + (i - this.textHeight) / 2.0F);
        this.textLayout.draw(paramCanvas);
      }
      paramCanvas.restore();
    }
  }
  
  public int getIntrinsicHeight()
  {
    return 0;
  }
  
  public int getIntrinsicWidth()
  {
    return 0;
  }
  
  public int getOpacity()
  {
    return -2;
  }
  
  public void setAlpha(int paramInt) {}
  
  public void setBackgroundColor(int paramInt)
  {
    paint.setColor(paramInt);
  }
  
  public void setColor(int paramInt)
  {
    namePaint.setColor(paramInt);
  }
  
  public void setColorFilter(ColorFilter paramColorFilter) {}
  
  public void setTitle(String paramString)
  {
    this.stringBuilder.setLength(0);
    if ((paramString != null) && (paramString.length() > 0)) {
      this.stringBuilder.append(paramString.substring(0, 1));
    }
    String str;
    if (this.stringBuilder.length() > 0) {
      str = this.stringBuilder.toString().toUpperCase();
    }
    for (;;)
    {
      try
      {
        paramString = new android/text/StaticLayout;
        paramString.<init>(str, namePaint, AndroidUtilities.dp(100.0F), Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
        this.textLayout = paramString;
        if (this.textLayout.getLineCount() > 0)
        {
          this.textLeft = this.textLayout.getLineLeft(0);
          this.textWidth = this.textLayout.getLineWidth(0);
          this.textHeight = this.textLayout.getLineBottom(0);
        }
        return;
      }
      catch (Exception paramString)
      {
        FileLog.e(paramString);
        continue;
      }
      this.textLayout = null;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/LetterDrawable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */