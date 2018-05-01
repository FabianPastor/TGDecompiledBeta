package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;

public class ChatUnreadCell
  extends FrameLayout
{
  private FrameLayout backgroundLayout;
  private ImageView imageView;
  private TextView textView;
  
  public ChatUnreadCell(Context paramContext)
  {
    super(paramContext);
    this.backgroundLayout = new FrameLayout(paramContext);
    this.backgroundLayout.setBackgroundResource(NUM);
    this.backgroundLayout.getBackground().setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_unreadMessagesStartBackground"), PorterDuff.Mode.MULTIPLY));
    addView(this.backgroundLayout, LayoutHelper.createFrame(-1, 27.0F, 51, 0.0F, 7.0F, 0.0F, 0.0F));
    this.imageView = new ImageView(paramContext);
    this.imageView.setImageResource(NUM);
    this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_unreadMessagesStartArrowIcon"), PorterDuff.Mode.MULTIPLY));
    this.imageView.setPadding(0, AndroidUtilities.dp(2.0F), 0, 0);
    this.backgroundLayout.addView(this.imageView, LayoutHelper.createFrame(-2, -2.0F, 21, 0.0F, 0.0F, 10.0F, 0.0F));
    this.textView = new TextView(paramContext);
    this.textView.setPadding(0, 0, 0, AndroidUtilities.dp(1.0F));
    this.textView.setTextSize(1, 14.0F);
    this.textView.setTextColor(Theme.getColor("chat_unreadMessagesStartText"));
    this.textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
    addView(this.textView, LayoutHelper.createFrame(-2, -2, 17));
  }
  
  public FrameLayout getBackgroundLayout()
  {
    return this.backgroundLayout;
  }
  
  public ImageView getImageView()
  {
    return this.imageView;
  }
  
  public TextView getTextView()
  {
    return this.textView;
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(paramInt1), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(40.0F), NUM));
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