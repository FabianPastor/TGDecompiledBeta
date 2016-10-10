package org.telegram.ui.Cells;

import android.content.Context;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.Components.LayoutHelper;

public class LocationPoweredCell
  extends FrameLayout
{
  public LocationPoweredCell(Context paramContext)
  {
    super(paramContext);
    LinearLayout localLinearLayout = new LinearLayout(paramContext);
    addView(localLinearLayout, LayoutHelper.createFrame(-2, -2, 17));
    Object localObject = new TextView(paramContext);
    ((TextView)localObject).setTextSize(1, 16.0F);
    ((TextView)localObject).setTextColor(-6710887);
    ((TextView)localObject).setText("Powered by");
    localLinearLayout.addView((View)localObject, LayoutHelper.createLinear(-2, -2));
    localObject = new ImageView(paramContext);
    ((ImageView)localObject).setImageResource(2130837687);
    ((ImageView)localObject).setPadding(0, AndroidUtilities.dp(2.0F), 0, 0);
    localLinearLayout.addView((View)localObject, LayoutHelper.createLinear(35, -2));
    paramContext = new TextView(paramContext);
    paramContext.setTextSize(1, 16.0F);
    paramContext.setTextColor(-6710887);
    paramContext.setText("Foursquare");
    localLinearLayout.addView(paramContext, LayoutHelper.createLinear(-2, -2));
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(paramInt1, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(56.0F), 1073741824));
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Cells/LocationPoweredCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */