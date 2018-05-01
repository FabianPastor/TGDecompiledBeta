package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Point;
import android.os.Build.VERSION;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;

public class DialogsEmptyCell
  extends LinearLayout
{
  private int currentAccount = UserConfig.selectedAccount;
  private TextView emptyTextView1;
  private TextView emptyTextView2;
  
  public DialogsEmptyCell(Context paramContext)
  {
    super(paramContext);
    setGravity(17);
    setOrientation(1);
    setOnTouchListener(new View.OnTouchListener()
    {
      public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
      {
        return true;
      }
    });
    this.emptyTextView1 = new TextView(paramContext);
    this.emptyTextView1.setText(LocaleController.getString("NoChats", NUM));
    this.emptyTextView1.setTextColor(Theme.getColor("emptyListPlaceholder"));
    this.emptyTextView1.setGravity(17);
    this.emptyTextView1.setTextSize(1, 20.0F);
    addView(this.emptyTextView1, LayoutHelper.createLinear(-2, -2));
    this.emptyTextView2 = new TextView(paramContext);
    String str = LocaleController.getString("NoChatsHelp", NUM);
    paramContext = str;
    if (AndroidUtilities.isTablet())
    {
      paramContext = str;
      if (!AndroidUtilities.isSmallTablet()) {
        paramContext = str.replace('\n', ' ');
      }
    }
    this.emptyTextView2.setText(paramContext);
    this.emptyTextView2.setTextColor(Theme.getColor("emptyListPlaceholder"));
    this.emptyTextView2.setTextSize(1, 15.0F);
    this.emptyTextView2.setGravity(17);
    this.emptyTextView2.setPadding(AndroidUtilities.dp(8.0F), AndroidUtilities.dp(6.0F), AndroidUtilities.dp(8.0F), 0);
    this.emptyTextView2.setLineSpacing(AndroidUtilities.dp(2.0F), 1.0F);
    addView(this.emptyTextView2, LayoutHelper.createLinear(-2, -2));
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    int i = View.MeasureSpec.getSize(paramInt2);
    paramInt2 = i;
    int j;
    if (i == 0)
    {
      j = AndroidUtilities.displaySize.y;
      i = ActionBar.getCurrentActionBarHeight();
      if (Build.VERSION.SDK_INT < 21) {
        break label112;
      }
    }
    label112:
    for (paramInt2 = AndroidUtilities.statusBarHeight;; paramInt2 = 0)
    {
      paramInt2 = j - i - paramInt2;
      ArrayList localArrayList = MessagesController.getInstance(this.currentAccount).hintDialogs;
      i = paramInt2;
      if (!localArrayList.isEmpty()) {
        i = paramInt2 - (AndroidUtilities.dp(72.0F) * localArrayList.size() + localArrayList.size() - 1 + AndroidUtilities.dp(50.0F));
      }
      super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(paramInt1), NUM), View.MeasureSpec.makeMeasureSpec(i, NUM));
      return;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Cells/DialogsEmptyCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */