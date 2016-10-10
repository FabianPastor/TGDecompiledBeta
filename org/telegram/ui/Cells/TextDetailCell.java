package org.telegram.ui.Cells;

import android.content.Context;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.Components.LayoutHelper;

public class TextDetailCell
  extends FrameLayout
{
  private ImageView imageView;
  private TextView textView;
  private TextView valueTextView;
  
  public TextDetailCell(Context paramContext)
  {
    super(paramContext);
    this.textView = new TextView(paramContext);
    this.textView.setTextColor(-14606047);
    this.textView.setTextSize(1, 16.0F);
    this.textView.setLines(1);
    this.textView.setMaxLines(1);
    this.textView.setSingleLine(true);
    TextView localTextView = this.textView;
    int i;
    label100:
    float f1;
    if (LocaleController.isRTL)
    {
      i = 5;
      localTextView.setGravity(i);
      localTextView = this.textView;
      if (!LocaleController.isRTL) {
        break label350;
      }
      i = 5;
      if (!LocaleController.isRTL) {
        break label356;
      }
      f1 = 16.0F;
      label109:
      if (!LocaleController.isRTL) {
        break label362;
      }
      f2 = 71.0F;
      label118:
      addView(localTextView, LayoutHelper.createFrame(-2, -2.0F, i, f1, 10.0F, f2, 0.0F));
      this.valueTextView = new TextView(paramContext);
      this.valueTextView.setTextColor(-7697782);
      this.valueTextView.setTextSize(1, 13.0F);
      this.valueTextView.setLines(1);
      this.valueTextView.setMaxLines(1);
      this.valueTextView.setSingleLine(true);
      localTextView = this.valueTextView;
      if (!LocaleController.isRTL) {
        break label368;
      }
      i = 5;
      label208:
      localTextView.setGravity(i);
      localTextView = this.valueTextView;
      if (!LocaleController.isRTL) {
        break label374;
      }
      i = 5;
      label230:
      if (!LocaleController.isRTL) {
        break label380;
      }
      f1 = 16.0F;
      label239:
      if (!LocaleController.isRTL) {
        break label386;
      }
      f2 = 71.0F;
      label248:
      addView(localTextView, LayoutHelper.createFrame(-2, -2.0F, i, f1, 35.0F, f2, 0.0F));
      this.imageView = new ImageView(paramContext);
      this.imageView.setScaleType(ImageView.ScaleType.CENTER);
      paramContext = this.imageView;
      if (!LocaleController.isRTL) {
        break label392;
      }
      i = j;
      label305:
      if (!LocaleController.isRTL) {
        break label398;
      }
      f1 = 0.0F;
      label313:
      if (!LocaleController.isRTL) {
        break label404;
      }
    }
    label350:
    label356:
    label362:
    label368:
    label374:
    label380:
    label386:
    label392:
    label398:
    label404:
    for (float f2 = 16.0F;; f2 = 0.0F)
    {
      addView(paramContext, LayoutHelper.createFrame(-2, -2.0F, i | 0x10, f1, 0.0F, f2, 0.0F));
      return;
      i = 3;
      break;
      i = 3;
      break label100;
      f1 = 71.0F;
      break label109;
      f2 = 16.0F;
      break label118;
      i = 3;
      break label208;
      i = 3;
      break label230;
      f1 = 71.0F;
      break label239;
      f2 = 16.0F;
      break label248;
      i = 3;
      break label305;
      f1 = 16.0F;
      break label313;
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(paramInt1), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0F), 1073741824));
  }
  
  public void setTextAndValue(String paramString1, String paramString2)
  {
    this.textView.setText(paramString1);
    this.valueTextView.setText(paramString2);
    this.imageView.setVisibility(4);
  }
  
  public void setTextAndValueAndIcon(String paramString1, String paramString2, int paramInt)
  {
    this.textView.setText(paramString1);
    this.valueTextView.setText(paramString2);
    this.imageView.setVisibility(0);
    this.imageView.setImageResource(paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Cells/TextDetailCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */