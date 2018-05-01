package org.telegram.ui.Cells;

import android.content.Context;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;

public class StickerSetGroupInfoCell
  extends LinearLayout
{
  private TextView addButton;
  private boolean isLast;
  
  public StickerSetGroupInfoCell(Context paramContext)
  {
    super(paramContext);
    setOrientation(1);
    TextView localTextView = new TextView(paramContext);
    localTextView.setTextColor(Theme.getColor("chat_emojiPanelTrendingDescription"));
    localTextView.setTextSize(1, 14.0F);
    localTextView.setText(LocaleController.getString("GroupStickersInfo", NUM));
    addView(localTextView, LayoutHelper.createLinear(-1, -2, 51, 17, 4, 17, 0));
    this.addButton = new TextView(paramContext);
    this.addButton.setPadding(AndroidUtilities.dp(17.0F), 0, AndroidUtilities.dp(17.0F), 0);
    this.addButton.setGravity(17);
    this.addButton.setTextColor(Theme.getColor("featuredStickers_buttonText"));
    this.addButton.setTextSize(1, 14.0F);
    this.addButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
    this.addButton.setBackgroundDrawable(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0F), Theme.getColor("featuredStickers_addButton"), Theme.getColor("featuredStickers_addButtonPressed")));
    this.addButton.setText(LocaleController.getString("ChooseStickerSet", NUM).toUpperCase());
    addView(this.addButton, LayoutHelper.createLinear(-2, 28, 51, 17, 10, 14, 8));
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(paramInt1), NUM), paramInt2);
    if (this.isLast)
    {
      View localView = (View)getParent();
      if (localView != null)
      {
        paramInt1 = localView.getMeasuredHeight() - localView.getPaddingBottom() - localView.getPaddingTop() - AndroidUtilities.dp(24.0F);
        if (getMeasuredHeight() < paramInt1) {
          setMeasuredDimension(getMeasuredWidth(), paramInt1);
        }
      }
    }
  }
  
  public void setAddOnClickListener(View.OnClickListener paramOnClickListener)
  {
    this.addButton.setOnClickListener(paramOnClickListener);
  }
  
  public void setIsLast(boolean paramBoolean)
  {
    this.isLast = paramBoolean;
    requestLayout();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Cells/StickerSetGroupInfoCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */