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

public class TextDetailSettingsCell
  extends FrameLayout
{
  private static Paint paint;
  private boolean multiline;
  private boolean needDivider;
  private TextView textView;
  private TextView valueTextView;
  
  public TextDetailSettingsCell(Context paramContext)
  {
    super(paramContext);
    if (paint == null)
    {
      paint = new Paint();
      paint.setColor(-2500135);
      paint.setStrokeWidth(1.0F);
    }
    this.textView = new TextView(paramContext);
    this.textView.setTextColor(-14606047);
    this.textView.setTextSize(1, 16.0F);
    this.textView.setLines(1);
    this.textView.setMaxLines(1);
    this.textView.setSingleLine(true);
    TextView localTextView = this.textView;
    if (LocaleController.isRTL)
    {
      i = 5;
      localTextView.setGravity(i | 0x10);
      localTextView = this.textView;
      if (!LocaleController.isRTL) {
        break label280;
      }
      i = 5;
      label130:
      addView(localTextView, LayoutHelper.createFrame(-2, -2.0F, i | 0x30, 17.0F, 10.0F, 17.0F, 0.0F));
      this.valueTextView = new TextView(paramContext);
      this.valueTextView.setTextColor(-7697782);
      this.valueTextView.setTextSize(1, 13.0F);
      paramContext = this.valueTextView;
      if (!LocaleController.isRTL) {
        break label285;
      }
      i = 5;
      label198:
      paramContext.setGravity(i);
      this.valueTextView.setLines(1);
      this.valueTextView.setMaxLines(1);
      this.valueTextView.setSingleLine(true);
      this.valueTextView.setPadding(0, 0, 0, 0);
      paramContext = this.valueTextView;
      if (!LocaleController.isRTL) {
        break label290;
      }
    }
    label280:
    label285:
    label290:
    for (int i = j;; i = 3)
    {
      addView(paramContext, LayoutHelper.createFrame(-2, -2.0F, i | 0x30, 17.0F, 35.0F, 17.0F, 0.0F));
      return;
      i = 3;
      break;
      i = 3;
      break label130;
      i = 3;
      break label198;
    }
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    if (this.needDivider) {
      paramCanvas.drawLine(getPaddingLeft(), getHeight() - 1, getWidth() - getPaddingRight(), getHeight() - 1, paint);
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    paramInt2 = 0;
    if (!this.multiline)
    {
      int i = AndroidUtilities.dp(64.0F);
      if (this.needDivider) {
        paramInt2 = 1;
      }
      super.onMeasure(paramInt1, View.MeasureSpec.makeMeasureSpec(paramInt2 + i, 1073741824));
      return;
    }
    super.onMeasure(paramInt1, View.MeasureSpec.makeMeasureSpec(0, 0));
  }
  
  public void setMultilineDetail(boolean paramBoolean)
  {
    this.multiline = paramBoolean;
    if (paramBoolean)
    {
      this.valueTextView.setLines(0);
      this.valueTextView.setMaxLines(0);
      this.valueTextView.setSingleLine(false);
      this.valueTextView.setPadding(0, 0, 0, AndroidUtilities.dp(12.0F));
      return;
    }
    this.valueTextView.setLines(1);
    this.valueTextView.setMaxLines(1);
    this.valueTextView.setSingleLine(true);
    this.valueTextView.setPadding(0, 0, 0, 0);
  }
  
  public void setTextAndValue(String paramString1, String paramString2, boolean paramBoolean)
  {
    this.textView.setText(paramString1);
    this.valueTextView.setText(paramString2);
    this.needDivider = paramBoolean;
    if (!paramBoolean) {}
    for (paramBoolean = true;; paramBoolean = false)
    {
      setWillNotDraw(paramBoolean);
      return;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Cells/TextDetailSettingsCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */