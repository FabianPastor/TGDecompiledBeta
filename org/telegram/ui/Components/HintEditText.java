package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.widget.EditText;
import org.telegram.messenger.AndroidUtilities;

public class HintEditText
  extends EditText
{
  private String hintText;
  private float numberSize;
  private Paint paint = new Paint();
  private Rect rect = new Rect();
  private float spaceSize;
  private float textOffset;
  
  public HintEditText(Context paramContext)
  {
    super(paramContext);
    this.paint.setColor(-6842473);
  }
  
  public String getHintText()
  {
    return this.hintText;
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    super.onDraw(paramCanvas);
    if ((this.hintText != null) && (length() < this.hintText.length()))
    {
      int j = getMeasuredHeight() / 2;
      float f = this.textOffset;
      int i = length();
      if (i < this.hintText.length())
      {
        if (this.hintText.charAt(i) == ' ') {}
        for (f += this.spaceSize;; f += this.numberSize)
        {
          i += 1;
          break;
          this.rect.set((int)f + AndroidUtilities.dp(1.0F), j, (int)(this.numberSize + f) - AndroidUtilities.dp(1.0F), AndroidUtilities.dp(2.0F) + j);
          paramCanvas.drawRect(this.rect, this.paint);
        }
      }
    }
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    onTextChange();
  }
  
  public void onTextChange()
  {
    if (length() > 0) {}
    for (float f = getPaint().measureText(getText(), 0, length());; f = 0.0F)
    {
      this.textOffset = f;
      this.spaceSize = getPaint().measureText(" ");
      this.numberSize = getPaint().measureText("1");
      invalidate();
      return;
    }
  }
  
  public void setHintText(String paramString)
  {
    this.hintText = paramString;
    onTextChange();
    setText(getText());
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/HintEditText.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */