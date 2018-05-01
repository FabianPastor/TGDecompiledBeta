package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RadialProgressView;

public class ChatLoadingCell
  extends FrameLayout
{
  private FrameLayout frameLayout;
  private RadialProgressView progressBar;
  
  public ChatLoadingCell(Context paramContext)
  {
    super(paramContext);
    this.frameLayout = new FrameLayout(paramContext);
    this.frameLayout.setBackgroundResource(NUM);
    this.frameLayout.getBackground().setColorFilter(Theme.colorFilter);
    addView(this.frameLayout, LayoutHelper.createFrame(36, 36, 17));
    this.progressBar = new RadialProgressView(paramContext);
    this.progressBar.setSize(AndroidUtilities.dp(28.0F));
    this.progressBar.setProgressColor(Theme.getColor("chat_serviceText"));
    this.frameLayout.addView(this.progressBar, LayoutHelper.createFrame(32, 32, 17));
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(paramInt1), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(44.0F), NUM));
  }
  
  public void setProgressVisible(boolean paramBoolean)
  {
    FrameLayout localFrameLayout = this.frameLayout;
    if (paramBoolean) {}
    for (int i = 0;; i = 4)
    {
      localFrameLayout.setVisibility(i);
      return;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Cells/ChatLoadingCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */