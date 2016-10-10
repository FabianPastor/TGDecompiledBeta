package org.telegram.ui.Cells;

import android.content.Context;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.Components.LayoutHelper;

public class ChatUnreadCell
  extends FrameLayout
{
  private TextView textView;
  
  public ChatUnreadCell(Context paramContext)
  {
    super(paramContext);
    FrameLayout localFrameLayout = new FrameLayout(paramContext);
    localFrameLayout.setBackgroundResource(2130837854);
    addView(localFrameLayout, LayoutHelper.createFrame(-1, 27.0F, 51, 0.0F, 7.0F, 0.0F, 0.0F));
    ImageView localImageView = new ImageView(paramContext);
    localImageView.setImageResource(2130837707);
    localImageView.setPadding(0, AndroidUtilities.dp(2.0F), 0, 0);
    localFrameLayout.addView(localImageView, LayoutHelper.createFrame(-2, -2.0F, 21, 0.0F, 0.0F, 10.0F, 0.0F));
    this.textView = new TextView(paramContext);
    this.textView.setPadding(0, 0, 0, AndroidUtilities.dp(1.0F));
    this.textView.setTextSize(1, 14.0F);
    this.textView.setTextColor(-11102772);
    this.textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
    addView(this.textView, LayoutHelper.createFrame(-2, -2, 17));
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(paramInt1), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(40.0F), 1073741824));
  }
  
  public void setText(String paramString)
  {
    this.textView.setText(paramString);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Cells/ChatUnreadCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */