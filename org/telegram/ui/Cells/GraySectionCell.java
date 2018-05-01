package org.telegram.ui.Cells;

import android.content.Context;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;

public class GraySectionCell
  extends FrameLayout
{
  private TextView textView;
  
  public GraySectionCell(Context paramContext)
  {
    super(paramContext);
    setBackgroundColor(Theme.getColor("graySection"));
    this.textView = new TextView(getContext());
    this.textView.setTextSize(1, 13.0F);
    this.textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
    this.textView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText2"));
    paramContext = this.textView;
    if (LocaleController.isRTL)
    {
      j = 5;
      paramContext.setGravity(j | 0x10);
      paramContext = this.textView;
      if (!LocaleController.isRTL) {
        break label126;
      }
    }
    label126:
    for (int j = i;; j = 3)
    {
      addView(paramContext, LayoutHelper.createFrame(-1, -1.0F, j | 0x30, 16.0F, 0.0F, 16.0F, 0.0F));
      return;
      j = 3;
      break;
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(paramInt1), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(32.0F), NUM));
  }
  
  public void setText(String paramString)
  {
    this.textView.setText(paramString);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Cells/GraySectionCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */