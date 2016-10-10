package org.telegram.ui.Cells;

import android.content.Context;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.ui.Components.LayoutHelper;

public class DrawerActionCell
  extends FrameLayout
{
  private TextView textView;
  
  public DrawerActionCell(Context paramContext)
  {
    super(paramContext);
    this.textView = new TextView(paramContext);
    this.textView.setTextColor(-12303292);
    this.textView.setTextSize(1, 15.0F);
    this.textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
    this.textView.setLines(1);
    this.textView.setMaxLines(1);
    this.textView.setSingleLine(true);
    this.textView.setGravity(19);
    this.textView.setCompoundDrawablePadding(AndroidUtilities.dp(34.0F));
    addView(this.textView, LayoutHelper.createFrame(-1, -1.0F, 51, 14.0F, 0.0F, 16.0F, 0.0F));
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(paramInt1, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0F), 1073741824));
  }
  
  public void setTextAndIcon(String paramString, int paramInt)
  {
    try
    {
      this.textView.setText(paramString);
      this.textView.setCompoundDrawablesWithIntrinsicBounds(paramInt, 0, 0, 0);
      return;
    }
    catch (Throwable paramString)
    {
      FileLog.e("tmessages", paramString);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Cells/DrawerActionCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */