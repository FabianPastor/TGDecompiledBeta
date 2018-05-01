package org.telegram.ui.Cells;

import android.content.Context;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RadioButton;

public class RadioColorCell
  extends FrameLayout
{
  private RadioButton radioButton;
  private TextView textView;
  
  public RadioColorCell(Context paramContext)
  {
    super(paramContext);
    this.radioButton = new RadioButton(paramContext);
    this.radioButton.setSize(AndroidUtilities.dp(20.0F));
    this.radioButton.setColor(Theme.getColor("dialogRadioBackground"), Theme.getColor("dialogRadioBackgroundChecked"));
    RadioButton localRadioButton = this.radioButton;
    int k;
    label74:
    float f;
    if (LocaleController.isRTL)
    {
      k = 5;
      if (!LocaleController.isRTL) {
        break label270;
      }
      m = 0;
      f = m;
      m = i;
      if (LocaleController.isRTL) {
        m = 18;
      }
      addView(localRadioButton, LayoutHelper.createFrame(22, 22.0F, k | 0x30, f, 13.0F, m, 0.0F));
      this.textView = new TextView(paramContext);
      this.textView.setTextColor(Theme.getColor("dialogTextBlack"));
      this.textView.setTextSize(1, 16.0F);
      this.textView.setLines(1);
      this.textView.setMaxLines(1);
      this.textView.setSingleLine(true);
      paramContext = this.textView;
      if (!LocaleController.isRTL) {
        break label277;
      }
      k = 5;
      label190:
      paramContext.setGravity(k | 0x10);
      paramContext = this.textView;
      if (!LocaleController.isRTL) {
        break label283;
      }
      k = j;
      label213:
      if (!LocaleController.isRTL) {
        break label289;
      }
      m = 17;
      label223:
      f = m;
      if (!LocaleController.isRTL) {
        break label296;
      }
    }
    label270:
    label277:
    label283:
    label289:
    label296:
    for (int m = 51;; m = 17)
    {
      addView(paramContext, LayoutHelper.createFrame(-2, -2.0F, k | 0x30, f, 12.0F, m, 0.0F));
      return;
      k = 3;
      break;
      m = 18;
      break label74;
      k = 3;
      break label190;
      k = 3;
      break label213;
      m = 51;
      break label223;
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(paramInt1), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0F), NUM));
  }
  
  public void setCheckColor(int paramInt1, int paramInt2)
  {
    this.radioButton.setColor(paramInt1, paramInt2);
  }
  
  public void setChecked(boolean paramBoolean1, boolean paramBoolean2)
  {
    this.radioButton.setChecked(paramBoolean1, paramBoolean2);
  }
  
  public void setTextAndValue(String paramString, boolean paramBoolean)
  {
    this.textView.setText(paramString);
    this.radioButton.setChecked(paramBoolean, false);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Cells/RadioColorCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */