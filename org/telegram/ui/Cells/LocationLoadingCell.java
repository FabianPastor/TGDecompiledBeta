package org.telegram.ui.Cells;

import android.content.Context;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.Components.LayoutHelper;

public class LocationLoadingCell
  extends FrameLayout
{
  private ProgressBar progressBar;
  private TextView textView;
  
  public LocationLoadingCell(Context paramContext)
  {
    super(paramContext);
    this.progressBar = new ProgressBar(paramContext);
    addView(this.progressBar, LayoutHelper.createFrame(-2, -2, 17));
    this.textView = new TextView(paramContext);
    this.textView.setTextColor(-6710887);
    this.textView.setTextSize(1, 16.0F);
    this.textView.setText(LocaleController.getString("NoResult", 2131165949));
    addView(this.textView, LayoutHelper.createFrame(-2, -2, 17));
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(paramInt1, View.MeasureSpec.makeMeasureSpec((int)(AndroidUtilities.dp(56.0F) * 2.5F), 1073741824));
  }
  
  public void setLoading(boolean paramBoolean)
  {
    int j = 4;
    Object localObject = this.progressBar;
    if (paramBoolean)
    {
      i = 0;
      ((ProgressBar)localObject).setVisibility(i);
      localObject = this.textView;
      if (!paramBoolean) {
        break label44;
      }
    }
    label44:
    for (int i = j;; i = 0)
    {
      ((TextView)localObject).setVisibility(i);
      return;
      i = 4;
      break;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Cells/LocationLoadingCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */