package org.telegram.ui.Components;

import android.content.Context;
import android.text.TextPaint;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.tgnet.TLRPC.KeyboardButton;
import org.telegram.tgnet.TLRPC.TL_keyboardButtonRow;
import org.telegram.tgnet.TLRPC.TL_replyKeyboardMarkup;

public class BotKeyboardView
  extends LinearLayout
{
  private TLRPC.TL_replyKeyboardMarkup botButtons;
  private int buttonHeight;
  private ArrayList<TextView> buttonViews = new ArrayList();
  private LinearLayout container;
  private BotKeyboardViewDelegate delegate;
  private boolean isFullSize;
  private int panelHeight;
  
  public BotKeyboardView(Context paramContext)
  {
    super(paramContext);
    setOrientation(1);
    ScrollView localScrollView = new ScrollView(paramContext);
    addView(localScrollView);
    this.container = new LinearLayout(paramContext);
    this.container.setOrientation(1);
    localScrollView.addView(this.container);
    setBackgroundColor(-657673);
  }
  
  public int getKeyboardHeight()
  {
    if (this.isFullSize) {
      return this.panelHeight;
    }
    return this.botButtons.rows.size() * AndroidUtilities.dp(this.buttonHeight) + AndroidUtilities.dp(30.0F) + (this.botButtons.rows.size() - 1) * AndroidUtilities.dp(10.0F);
  }
  
  public void invalidateViews()
  {
    int i = 0;
    while (i < this.buttonViews.size())
    {
      ((TextView)this.buttonViews.get(i)).invalidate();
      i += 1;
    }
  }
  
  public boolean isFullSize()
  {
    return this.isFullSize;
  }
  
  public void setButtons(TLRPC.TL_replyKeyboardMarkup paramTL_replyKeyboardMarkup)
  {
    this.botButtons = paramTL_replyKeyboardMarkup;
    this.container.removeAllViews();
    this.buttonViews.clear();
    if ((paramTL_replyKeyboardMarkup != null) && (this.botButtons.rows.size() != 0))
    {
      boolean bool;
      int i;
      if (!paramTL_replyKeyboardMarkup.resize)
      {
        bool = true;
        this.isFullSize = bool;
        if (this.isFullSize) {
          break label384;
        }
        i = 42;
        label63:
        this.buttonHeight = i;
        i = 0;
      }
      for (;;)
      {
        if (i >= paramTL_replyKeyboardMarkup.rows.size()) {
          return;
        }
        TLRPC.TL_keyboardButtonRow localTL_keyboardButtonRow = (TLRPC.TL_keyboardButtonRow)paramTL_replyKeyboardMarkup.rows.get(i);
        LinearLayout localLinearLayout = new LinearLayout(getContext());
        localLinearLayout.setOrientation(0);
        Object localObject = this.container;
        int j = this.buttonHeight;
        float f1;
        label137:
        float f2;
        label154:
        label188:
        TextView localTextView;
        if (i == 0)
        {
          f1 = 15.0F;
          if (i != paramTL_replyKeyboardMarkup.rows.size() - 1) {
            break label446;
          }
          f2 = 15.0F;
          ((LinearLayout)localObject).addView(localLinearLayout, LayoutHelper.createLinear(-1, j, 15.0F, f1, 15.0F, f2));
          f1 = 1.0F / localTL_keyboardButtonRow.buttons.size();
          j = 0;
          if (j >= localTL_keyboardButtonRow.buttons.size()) {
            break label457;
          }
          localObject = (TLRPC.KeyboardButton)localTL_keyboardButtonRow.buttons.get(j);
          localTextView = new TextView(getContext());
          localTextView.setTag(localObject);
          localTextView.setTextColor(-13220017);
          localTextView.setTextSize(1, 16.0F);
          localTextView.setGravity(17);
          localTextView.setBackgroundResource(2130837570);
          localTextView.setPadding(AndroidUtilities.dp(4.0F), 0, AndroidUtilities.dp(4.0F), 0);
          localTextView.setText(Emoji.replaceEmoji(((TLRPC.KeyboardButton)localObject).text, localTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(16.0F), false));
          if (j == localTL_keyboardButtonRow.buttons.size() - 1) {
            break label451;
          }
        }
        label384:
        label446:
        label451:
        for (int k = 10;; k = 0)
        {
          localLinearLayout.addView(localTextView, LayoutHelper.createLinear(0, -1, f1, 0, 0, k, 0));
          localTextView.setOnClickListener(new View.OnClickListener()
          {
            public void onClick(View paramAnonymousView)
            {
              BotKeyboardView.this.delegate.didPressedButton((TLRPC.KeyboardButton)paramAnonymousView.getTag());
            }
          });
          this.buttonViews.add(localTextView);
          j += 1;
          break label188;
          bool = false;
          break;
          i = (int)Math.max(42.0F, (this.panelHeight - AndroidUtilities.dp(30.0F) - (this.botButtons.rows.size() - 1) * AndroidUtilities.dp(10.0F)) / this.botButtons.rows.size() / AndroidUtilities.density);
          break label63;
          f1 = 10.0F;
          break label137;
          f2 = 0.0F;
          break label154;
        }
        label457:
        i += 1;
      }
    }
  }
  
  public void setDelegate(BotKeyboardViewDelegate paramBotKeyboardViewDelegate)
  {
    this.delegate = paramBotKeyboardViewDelegate;
  }
  
  public void setPanelHeight(int paramInt)
  {
    this.panelHeight = paramInt;
    if ((this.isFullSize) && (this.botButtons != null) && (this.botButtons.rows.size() != 0))
    {
      if (!this.isFullSize) {}
      for (paramInt = 42;; paramInt = (int)Math.max(42.0F, (this.panelHeight - AndroidUtilities.dp(30.0F) - (this.botButtons.rows.size() - 1) * AndroidUtilities.dp(10.0F)) / this.botButtons.rows.size() / AndroidUtilities.density))
      {
        this.buttonHeight = paramInt;
        int i = this.container.getChildCount();
        int j = AndroidUtilities.dp(this.buttonHeight);
        paramInt = 0;
        while (paramInt < i)
        {
          View localView = this.container.getChildAt(paramInt);
          LinearLayout.LayoutParams localLayoutParams = (LinearLayout.LayoutParams)localView.getLayoutParams();
          if (localLayoutParams.height != j)
          {
            localLayoutParams.height = j;
            localView.setLayoutParams(localLayoutParams);
          }
          paramInt += 1;
        }
      }
    }
  }
  
  public static abstract interface BotKeyboardViewDelegate
  {
    public abstract void didPressedButton(TLRPC.KeyboardButton paramKeyboardButton);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/BotKeyboardView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */