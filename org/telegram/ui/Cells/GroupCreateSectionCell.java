package org.telegram.ui.Cells;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;

public class GroupCreateSectionCell
  extends FrameLayout
{
  private Drawable drawable;
  private TextView textView;
  
  public GroupCreateSectionCell(Context paramContext)
  {
    super(paramContext);
    setBackgroundColor(Theme.getColor("graySection"));
    this.drawable = getResources().getDrawable(NUM);
    this.drawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("groupcreate_sectionShadow"), PorterDuff.Mode.MULTIPLY));
    this.textView = new TextView(getContext());
    this.textView.setTextSize(1, 14.0F);
    this.textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
    this.textView.setTextColor(Theme.getColor("groupcreate_sectionText"));
    paramContext = this.textView;
    if (LocaleController.isRTL)
    {
      j = 5;
      paramContext.setGravity(j | 0x10);
      paramContext = this.textView;
      if (!LocaleController.isRTL) {
        break label161;
      }
    }
    label161:
    for (int j = i;; j = 3)
    {
      addView(paramContext, LayoutHelper.createFrame(-1, -1.0F, j | 0x30, 16.0F, 0.0F, 16.0F, 0.0F));
      return;
      j = 3;
      break;
    }
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    this.drawable.setBounds(0, getMeasuredHeight() - AndroidUtilities.dp(3.0F), getMeasuredWidth(), getMeasuredHeight());
    this.drawable.draw(paramCanvas);
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


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Cells/GroupCreateSectionCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */