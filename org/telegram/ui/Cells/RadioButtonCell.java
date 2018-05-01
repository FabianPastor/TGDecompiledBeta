package org.telegram.ui.Cells;

import android.content.Context;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RadioButton;

public class RadioButtonCell
  extends FrameLayout
{
  private RadioButton radioButton;
  private TextView textView;
  private TextView valueTextView;
  
  public RadioButtonCell(Context paramContext)
  {
    super(paramContext);
    this.radioButton = new RadioButton(paramContext);
    this.radioButton.setSize(AndroidUtilities.dp(20.0F));
    this.radioButton.setColor(Theme.getColor("radioBackground"), Theme.getColor("radioBackgroundChecked"));
    Object localObject = this.radioButton;
    int k;
    label75:
    float f;
    if (LocaleController.isRTL)
    {
      k = 5;
      if (!LocaleController.isRTL) {
        break label432;
      }
      m = 0;
      f = m;
      if (!LocaleController.isRTL) {
        break label439;
      }
      m = 18;
      label90:
      addView((View)localObject, LayoutHelper.createFrame(22, 22.0F, k | 0x30, f, 10.0F, m, 0.0F));
      this.textView = new TextView(paramContext);
      this.textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
      this.textView.setTextSize(1, 16.0F);
      this.textView.setLines(1);
      this.textView.setMaxLines(1);
      this.textView.setSingleLine(true);
      localObject = this.textView;
      if (!LocaleController.isRTL) {
        break label445;
      }
      k = 5;
      label189:
      ((TextView)localObject).setGravity(k | 0x10);
      localObject = this.textView;
      if (!LocaleController.isRTL) {
        break label451;
      }
      k = 5;
      label214:
      if (!LocaleController.isRTL) {
        break label457;
      }
      m = 17;
      label224:
      f = m;
      if (!LocaleController.isRTL) {
        break label464;
      }
      m = 51;
      label239:
      addView((View)localObject, LayoutHelper.createFrame(-2, -2.0F, k | 0x30, f, 10.0F, m, 0.0F));
      this.valueTextView = new TextView(paramContext);
      this.valueTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText2"));
      this.valueTextView.setTextSize(1, 13.0F);
      paramContext = this.valueTextView;
      if (!LocaleController.isRTL) {
        break label471;
      }
      k = 5;
      label313:
      paramContext.setGravity(k);
      this.valueTextView.setLines(0);
      this.valueTextView.setMaxLines(0);
      this.valueTextView.setSingleLine(false);
      this.valueTextView.setPadding(0, 0, 0, AndroidUtilities.dp(12.0F));
      paramContext = this.valueTextView;
      if (!LocaleController.isRTL) {
        break label477;
      }
      k = j;
      label372:
      if (!LocaleController.isRTL) {
        break label483;
      }
    }
    label432:
    label439:
    label445:
    label451:
    label457:
    label464:
    label471:
    label477:
    label483:
    for (int m = 17;; m = 51)
    {
      f = m;
      m = i;
      if (LocaleController.isRTL) {
        m = 51;
      }
      addView(paramContext, LayoutHelper.createFrame(-2, -2.0F, k | 0x30, f, 35.0F, m, 0.0F));
      return;
      k = 3;
      break;
      m = 18;
      break label75;
      m = 0;
      break label90;
      k = 3;
      break label189;
      k = 3;
      break label214;
      m = 51;
      break label224;
      m = 17;
      break label239;
      k = 3;
      break label313;
      k = 3;
      break label372;
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(paramInt1), NUM), View.MeasureSpec.makeMeasureSpec(0, 0));
  }
  
  public void setChecked(boolean paramBoolean1, boolean paramBoolean2)
  {
    this.radioButton.setChecked(paramBoolean1, paramBoolean2);
  }
  
  public void setTextAndValue(String paramString1, String paramString2, boolean paramBoolean)
  {
    this.textView.setText(paramString1);
    this.valueTextView.setText(paramString2);
    this.radioButton.setChecked(paramBoolean, false);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Cells/RadioButtonCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */