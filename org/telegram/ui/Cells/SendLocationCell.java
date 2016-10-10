package org.telegram.ui.Cells;

import android.content.Context;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.ImageView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.Components.LayoutHelper;

public class SendLocationCell
  extends FrameLayout
{
  private SimpleTextView accurateTextView;
  private SimpleTextView titleTextView;
  
  public SendLocationCell(Context paramContext)
  {
    super(paramContext);
    Object localObject = new ImageView(paramContext);
    ((ImageView)localObject).setImageResource(2130837915);
    int i;
    float f1;
    if (LocaleController.isRTL)
    {
      i = 5;
      if (!LocaleController.isRTL) {
        break label304;
      }
      f1 = 0.0F;
      label42:
      if (!LocaleController.isRTL) {
        break label310;
      }
      f2 = 17.0F;
      label51:
      addView((View)localObject, LayoutHelper.createFrame(-2, -2.0F, i | 0x30, f1, 13.0F, f2, 0.0F));
      this.titleTextView = new SimpleTextView(paramContext);
      this.titleTextView.setTextSize(16);
      this.titleTextView.setTextColor(-13141330);
      localObject = this.titleTextView;
      if (!LocaleController.isRTL) {
        break label315;
      }
      i = 5;
      label119:
      ((SimpleTextView)localObject).setGravity(i);
      this.titleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      localObject = this.titleTextView;
      if (!LocaleController.isRTL) {
        break label321;
      }
      i = 5;
      label153:
      if (!LocaleController.isRTL) {
        break label327;
      }
      f1 = 16.0F;
      label162:
      if (!LocaleController.isRTL) {
        break label333;
      }
      f2 = 73.0F;
      label171:
      addView((View)localObject, LayoutHelper.createFrame(-1, 20.0F, i | 0x30, f1, 12.0F, f2, 0.0F));
      this.accurateTextView = new SimpleTextView(paramContext);
      this.accurateTextView.setTextSize(14);
      this.accurateTextView.setTextColor(-6710887);
      paramContext = this.accurateTextView;
      if (!LocaleController.isRTL) {
        break label339;
      }
      i = 5;
      label237:
      paramContext.setGravity(i);
      paramContext = this.accurateTextView;
      if (!LocaleController.isRTL) {
        break label345;
      }
      i = j;
      label258:
      if (!LocaleController.isRTL) {
        break label351;
      }
      f1 = 16.0F;
      label267:
      if (!LocaleController.isRTL) {
        break label357;
      }
    }
    label304:
    label310:
    label315:
    label321:
    label327:
    label333:
    label339:
    label345:
    label351:
    label357:
    for (float f2 = 73.0F;; f2 = 16.0F)
    {
      addView(paramContext, LayoutHelper.createFrame(-1, 20.0F, i | 0x30, f1, 37.0F, f2, 0.0F));
      return;
      i = 3;
      break;
      f1 = 17.0F;
      break label42;
      f2 = 0.0F;
      break label51;
      i = 3;
      break label119;
      i = 3;
      break label153;
      f1 = 73.0F;
      break label162;
      f2 = 16.0F;
      break label171;
      i = 3;
      break label237;
      i = 3;
      break label258;
      f1 = 73.0F;
      break label267;
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(paramInt1, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(66.0F), 1073741824));
  }
  
  public void setText(String paramString1, String paramString2)
  {
    this.titleTextView.setText(paramString1);
    this.accurateTextView.setText(paramString2);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Cells/SendLocationCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */