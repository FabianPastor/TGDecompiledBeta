package org.telegram.ui.Cells;

import android.content.Context;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.Components.LayoutHelper;

public class LetterSectionCell
  extends FrameLayout
{
  private TextView textView;
  
  public LetterSectionCell(Context paramContext)
  {
    super(paramContext);
    setLayoutParams(new ViewGroup.LayoutParams(AndroidUtilities.dp(54.0F), AndroidUtilities.dp(64.0F)));
    this.textView = new TextView(getContext());
    this.textView.setTextSize(1, 22.0F);
    this.textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
    this.textView.setTextColor(-8355712);
    this.textView.setGravity(17);
    addView(this.textView, LayoutHelper.createFrame(-1, -1.0F));
  }
  
  public void setCellHeight(int paramInt)
  {
    setLayoutParams(new ViewGroup.LayoutParams(AndroidUtilities.dp(54.0F), paramInt));
  }
  
  public void setLetter(String paramString)
  {
    this.textView.setText(paramString.toUpperCase());
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Cells/LetterSectionCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */