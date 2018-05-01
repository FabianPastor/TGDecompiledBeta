package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;

public class LocationPoweredCell
  extends FrameLayout
{
  private ImageView imageView;
  private TextView textView;
  private TextView textView2;
  
  public LocationPoweredCell(Context paramContext)
  {
    super(paramContext);
    LinearLayout localLinearLayout = new LinearLayout(paramContext);
    addView(localLinearLayout, LayoutHelper.createFrame(-2, -2, 17));
    this.textView = new TextView(paramContext);
    this.textView.setTextSize(1, 16.0F);
    this.textView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText3"));
    this.textView.setText("Powered by");
    localLinearLayout.addView(this.textView, LayoutHelper.createLinear(-2, -2));
    this.imageView = new ImageView(paramContext);
    this.imageView.setImageResource(NUM);
    this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteGrayText3"), PorterDuff.Mode.MULTIPLY));
    this.imageView.setPadding(0, AndroidUtilities.dp(2.0F), 0, 0);
    localLinearLayout.addView(this.imageView, LayoutHelper.createLinear(35, -2));
    this.textView2 = new TextView(paramContext);
    this.textView2.setTextSize(1, 16.0F);
    this.textView2.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText3"));
    this.textView2.setText("Foursquare");
    localLinearLayout.addView(this.textView2, LayoutHelper.createLinear(-2, -2));
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(paramInt1), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(56.0F), NUM));
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Cells/LocationPoweredCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */