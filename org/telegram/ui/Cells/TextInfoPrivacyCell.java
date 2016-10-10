package org.telegram.ui.Cells;

import android.content.Context;
import android.text.method.LinkMovementMethod;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.Components.LayoutHelper;

public class TextInfoPrivacyCell
  extends FrameLayout
{
  private TextView textView;
  
  public TextInfoPrivacyCell(Context paramContext)
  {
    super(paramContext);
    this.textView = new TextView(paramContext);
    this.textView.setTextColor(-8355712);
    this.textView.setLinkTextColor(-14255946);
    this.textView.setTextSize(1, 14.0F);
    paramContext = this.textView;
    if (LocaleController.isRTL)
    {
      i = 5;
      paramContext.setGravity(i);
      this.textView.setPadding(0, AndroidUtilities.dp(10.0F), 0, AndroidUtilities.dp(17.0F));
      this.textView.setMovementMethod(LinkMovementMethod.getInstance());
      paramContext = this.textView;
      if (!LocaleController.isRTL) {
        break label135;
      }
    }
    label135:
    for (int i = j;; i = 3)
    {
      addView(paramContext, LayoutHelper.createFrame(-2, -2.0F, i | 0x30, 17.0F, 0.0F, 17.0F, 0.0F));
      return;
      i = 3;
      break;
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(paramInt1), 1073741824), View.MeasureSpec.makeMeasureSpec(0, 0));
  }
  
  public void setText(CharSequence paramCharSequence)
  {
    this.textView.setText(paramCharSequence);
  }
  
  public void setTextColor(int paramInt)
  {
    this.textView.setTextColor(paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Cells/TextInfoPrivacyCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */