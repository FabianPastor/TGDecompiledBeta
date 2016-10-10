package org.telegram.ui.Cells;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.Components.LayoutHelper;

public class TextColorCell
  extends FrameLayout
{
  private static Paint paint;
  private Drawable colorDrawable;
  private int currentColor;
  private boolean needDivider;
  private TextView textView;
  
  public TextColorCell(Context paramContext)
  {
    super(paramContext);
    if (paint == null)
    {
      paint = new Paint();
      paint.setColor(-2500135);
      paint.setStrokeWidth(1.0F);
    }
    this.colorDrawable = getResources().getDrawable(2130837998);
    this.textView = new TextView(paramContext);
    this.textView.setTextColor(-14606047);
    this.textView.setTextSize(1, 16.0F);
    this.textView.setLines(1);
    this.textView.setMaxLines(1);
    this.textView.setSingleLine(true);
    paramContext = this.textView;
    if (LocaleController.isRTL)
    {
      i = 5;
      paramContext.setGravity(i | 0x10);
      paramContext = this.textView;
      if (!LocaleController.isRTL) {
        break label167;
      }
    }
    label167:
    for (int i = j;; i = 3)
    {
      addView(paramContext, LayoutHelper.createFrame(-1, -1.0F, i | 0x30, 17.0F, 0.0F, 17.0F, 0.0F));
      return;
      i = 3;
      break;
    }
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    if (this.needDivider) {
      paramCanvas.drawLine(getPaddingLeft(), getHeight() - 1, getWidth() - getPaddingRight(), getHeight() - 1, paint);
    }
    int j;
    if ((this.currentColor != 0) && (this.colorDrawable != null))
    {
      j = (getMeasuredHeight() - this.colorDrawable.getMinimumHeight()) / 2;
      if (LocaleController.isRTL) {
        break label133;
      }
    }
    label133:
    for (int i = getMeasuredWidth() - this.colorDrawable.getIntrinsicWidth() - AndroidUtilities.dp(14.5F);; i = AndroidUtilities.dp(14.5F))
    {
      this.colorDrawable.setBounds(i, j, this.colorDrawable.getIntrinsicWidth() + i, this.colorDrawable.getIntrinsicHeight() + j);
      this.colorDrawable.draw(paramCanvas);
      return;
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    int i = AndroidUtilities.dp(48.0F);
    if (this.needDivider) {}
    for (paramInt2 = 1;; paramInt2 = 0)
    {
      super.onMeasure(paramInt1, View.MeasureSpec.makeMeasureSpec(paramInt2 + i, 1073741824));
      return;
    }
  }
  
  public void setTextAndColor(String paramString, int paramInt, boolean paramBoolean)
  {
    this.textView.setText(paramString);
    this.needDivider = paramBoolean;
    this.currentColor = paramInt;
    this.colorDrawable.setColorFilter(new PorterDuffColorFilter(paramInt, PorterDuff.Mode.MULTIPLY));
    if ((!this.needDivider) && (this.currentColor == 0)) {}
    for (paramBoolean = true;; paramBoolean = false)
    {
      setWillNotDraw(paramBoolean);
      invalidate();
      return;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Cells/TextColorCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */