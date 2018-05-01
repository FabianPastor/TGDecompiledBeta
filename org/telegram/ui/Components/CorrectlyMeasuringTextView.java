package org.telegram.ui.Components;

import android.content.Context;
import android.text.Layout;
import android.text.TextPaint;
import android.widget.TextView;

public class CorrectlyMeasuringTextView
  extends TextView
{
  public CorrectlyMeasuringTextView(Context paramContext)
  {
    super(paramContext);
  }
  
  public void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(paramInt1, paramInt2);
    try
    {
      Layout localLayout = getLayout();
      if (localLayout.getLineCount() <= 1) {}
      for (;;)
      {
        return;
        paramInt1 = 0;
        for (paramInt2 = localLayout.getLineCount() - 1; paramInt2 >= 0; paramInt2--) {
          paramInt1 = Math.max(paramInt1, Math.round(localLayout.getPaint().measureText(getText(), localLayout.getLineStart(paramInt2), localLayout.getLineEnd(paramInt2))));
        }
        super.onMeasure(Math.min(getPaddingLeft() + paramInt1 + getPaddingRight(), getMeasuredWidth()) | 0x40000000, getMeasuredHeight() | 0x40000000);
      }
    }
    catch (Exception localException)
    {
      for (;;) {}
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/CorrectlyMeasuringTextView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */