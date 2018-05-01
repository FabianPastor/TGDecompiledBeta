package org.telegram.ui.Cells;

import android.content.Context;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;

public class TextInfoCell
  extends FrameLayout
{
  private TextView textView;
  
  public TextInfoCell(Context paramContext)
  {
    super(paramContext);
    this.textView = new TextView(paramContext);
    this.textView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText5"));
    this.textView.setTextSize(1, 13.0F);
    this.textView.setGravity(17);
    this.textView.setPadding(0, AndroidUtilities.dp(19.0F), 0, AndroidUtilities.dp(19.0F));
    addView(this.textView, LayoutHelper.createFrame(-2, -2.0F, 17, 17.0F, 0.0F, 17.0F, 0.0F));
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(paramInt1), NUM), View.MeasureSpec.makeMeasureSpec(0, 0));
  }
  
  public void setText(String paramString)
  {
    this.textView.setText(paramString);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Cells/TextInfoCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */