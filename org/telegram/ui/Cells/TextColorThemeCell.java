package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.Components.LayoutHelper;

public class TextColorThemeCell
  extends FrameLayout
{
  private static Paint colorPaint;
  private float alpha = 1.0F;
  private int currentColor;
  private boolean needDivider;
  private TextView textView;
  
  public TextColorThemeCell(Context paramContext)
  {
    super(paramContext);
    if (colorPaint == null) {
      colorPaint = new Paint(1);
    }
    this.textView = new TextView(paramContext);
    this.textView.setTextColor(-14606047);
    this.textView.setTextSize(1, 16.0F);
    this.textView.setLines(1);
    this.textView.setMaxLines(1);
    this.textView.setSingleLine(true);
    paramContext = this.textView;
    int j;
    label133:
    label142:
    float f;
    if (LocaleController.isRTL)
    {
      j = 5;
      paramContext.setGravity(j | 0x10);
      this.textView.setPadding(0, 0, 0, AndroidUtilities.dp(3.0F));
      paramContext = this.textView;
      if (!LocaleController.isRTL) {
        break label182;
      }
      j = i;
      if (!LocaleController.isRTL) {
        break label187;
      }
      i = 17;
      f = i;
      if (!LocaleController.isRTL) {
        break label193;
      }
    }
    label182:
    label187:
    label193:
    for (i = 53;; i = 17)
    {
      addView(paramContext, LayoutHelper.createFrame(-1, -1.0F, j | 0x30, f, 0.0F, i, 0.0F));
      return;
      j = 3;
      break;
      j = 3;
      break label133;
      i = 53;
      break label142;
    }
  }
  
  public float getAlpha()
  {
    return this.alpha;
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    if (this.currentColor != 0)
    {
      colorPaint.setColor(this.currentColor);
      colorPaint.setAlpha((int)(255.0F * this.alpha));
      if (LocaleController.isRTL) {
        break label66;
      }
    }
    label66:
    for (float f = AndroidUtilities.dp(28.0F);; f = getMeasuredWidth() - AndroidUtilities.dp(28.0F))
    {
      paramCanvas.drawCircle(f, getMeasuredHeight() / 2, AndroidUtilities.dp(10.0F), colorPaint);
      return;
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    int i = View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(paramInt1), NUM);
    paramInt2 = AndroidUtilities.dp(48.0F);
    if (this.needDivider) {}
    for (paramInt1 = 1;; paramInt1 = 0)
    {
      super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(paramInt1 + paramInt2, NUM));
      return;
    }
  }
  
  public void setAlpha(float paramFloat)
  {
    this.alpha = paramFloat;
    invalidate();
  }
  
  public void setTextAndColor(String paramString, int paramInt)
  {
    this.textView.setText(paramString);
    this.currentColor = paramInt;
    if ((!this.needDivider) && (this.currentColor == 0)) {}
    for (boolean bool = true;; bool = false)
    {
      setWillNotDraw(bool);
      invalidate();
      return;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Cells/TextColorThemeCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */