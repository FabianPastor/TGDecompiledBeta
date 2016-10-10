package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RadioButton;

public class RadioButtonCell
  extends FrameLayout
{
  private static Paint paint;
  private boolean needDivider;
  private RadioButton radioButton;
  private TextView textView;
  private TextView valueTextView;
  
  public RadioButtonCell(Context paramContext)
  {
    super(paramContext);
    if (paint == null)
    {
      paint = new Paint();
      paint.setColor(-2500135);
      paint.setStrokeWidth(1.0F);
    }
    this.radioButton = new RadioButton(paramContext);
    this.radioButton.setSize(AndroidUtilities.dp(20.0F));
    this.radioButton.setColor(-5000269, -13129232);
    Object localObject = this.radioButton;
    int i;
    label101:
    float f;
    if (LocaleController.isRTL)
    {
      i = 5;
      if (!LocaleController.isRTL) {
        break label438;
      }
      j = 0;
      f = j;
      if (!LocaleController.isRTL) {
        break label445;
      }
      j = 18;
      label115:
      addView((View)localObject, LayoutHelper.createFrame(22, 22.0F, i | 0x30, f, 10.0F, j, 0.0F));
      this.textView = new TextView(paramContext);
      this.textView.setTextColor(-14606047);
      this.textView.setTextSize(1, 16.0F);
      this.textView.setLines(1);
      this.textView.setMaxLines(1);
      this.textView.setSingleLine(true);
      localObject = this.textView;
      if (!LocaleController.isRTL) {
        break label451;
      }
      i = 5;
      label208:
      ((TextView)localObject).setGravity(i | 0x10);
      localObject = this.textView;
      if (!LocaleController.isRTL) {
        break label456;
      }
      i = 5;
      label231:
      if (!LocaleController.isRTL) {
        break label461;
      }
      j = 17;
      label241:
      f = j;
      if (!LocaleController.isRTL) {
        break label468;
      }
      j = 51;
      label255:
      addView((View)localObject, LayoutHelper.createFrame(-2, -2.0F, i | 0x30, f, 10.0F, j, 0.0F));
      this.valueTextView = new TextView(paramContext);
      this.valueTextView.setTextColor(-7697782);
      this.valueTextView.setTextSize(1, 13.0F);
      paramContext = this.valueTextView;
      if (!LocaleController.isRTL) {
        break label475;
      }
      i = 5;
      label323:
      paramContext.setGravity(i);
      this.valueTextView.setLines(0);
      this.valueTextView.setMaxLines(0);
      this.valueTextView.setSingleLine(false);
      this.valueTextView.setPadding(0, 0, 0, AndroidUtilities.dp(12.0F));
      paramContext = this.valueTextView;
      if (!LocaleController.isRTL) {
        break label480;
      }
      i = m;
      label381:
      if (!LocaleController.isRTL) {
        break label485;
      }
    }
    label438:
    label445:
    label451:
    label456:
    label461:
    label468:
    label475:
    label480:
    label485:
    for (int j = 17;; j = 51)
    {
      f = j;
      j = k;
      if (LocaleController.isRTL) {
        j = 51;
      }
      addView(paramContext, LayoutHelper.createFrame(-2, -2.0F, i | 0x30, f, 35.0F, j, 0.0F));
      return;
      i = 3;
      break;
      j = 18;
      break label101;
      j = 0;
      break label115;
      i = 3;
      break label208;
      i = 3;
      break label231;
      j = 51;
      break label241;
      j = 17;
      break label255;
      i = 3;
      break label323;
      i = 3;
      break label381;
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
    super.onMeasure(paramInt1, View.MeasureSpec.makeMeasureSpec(0, 0));
  }
  
  public void setChecked(boolean paramBoolean1, boolean paramBoolean2)
  {
    this.radioButton.setChecked(paramBoolean1, paramBoolean2);
  }
  
  public void setTextAndValue(String paramString1, String paramString2, boolean paramBoolean1, boolean paramBoolean2)
  {
    boolean bool = false;
    this.textView.setText(paramString1);
    this.valueTextView.setText(paramString2);
    this.needDivider = paramBoolean2;
    this.radioButton.setChecked(paramBoolean1, false);
    paramBoolean1 = bool;
    if (!paramBoolean2) {
      paramBoolean1 = true;
    }
    setWillNotDraw(paramBoolean1);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Cells/RadioButtonCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */