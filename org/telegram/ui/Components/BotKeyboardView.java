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
import org.telegram.ui.ActionBar.Theme;

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
  private ScrollView scrollView;
  
  public BotKeyboardView(Context paramContext)
  {
    super(paramContext);
    setOrientation(1);
    this.scrollView = new ScrollView(paramContext);
    addView(this.scrollView);
    this.container = new LinearLayout(paramContext);
    this.container.setOrientation(1);
    this.scrollView.addView(this.container);
    AndroidUtilities.setScrollViewEdgeEffectColor(this.scrollView, Theme.getColor("chat_emojiPanelBackground"));
    setBackgroundColor(Theme.getColor("chat_emojiPanelBackground"));
  }
  
  public int getKeyboardHeight()
  {
    if (this.isFullSize) {}
    for (int i = this.panelHeight;; i = this.botButtons.rows.size() * AndroidUtilities.dp(this.buttonHeight) + AndroidUtilities.dp(30.0F) + (this.botButtons.rows.size() - 1) * AndroidUtilities.dp(10.0F)) {
      return i;
    }
  }
  
  public void invalidateViews()
  {
    for (int i = 0; i < this.buttonViews.size(); i++) {
      ((TextView)this.buttonViews.get(i)).invalidate();
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
    this.scrollView.scrollTo(0, 0);
    if ((paramTL_replyKeyboardMarkup != null) && (this.botButtons.rows.size() != 0))
    {
      boolean bool;
      if (!paramTL_replyKeyboardMarkup.resize)
      {
        bool = true;
        this.isFullSize = bool;
        if (this.isFullSize) {
          break label405;
        }
        i = 42;
        label69:
        this.buttonHeight = i;
      }
      for (int i = 0;; i++)
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
        label139:
        float f2;
        label156:
        label193:
        TextView localTextView;
        if (i == 0)
        {
          f1 = 15.0F;
          if (i != paramTL_replyKeyboardMarkup.rows.size() - 1) {
            break label467;
          }
          f2 = 15.0F;
          ((LinearLayout)localObject).addView(localLinearLayout, LayoutHelper.createLinear(-1, j, 15.0F, f1, 15.0F, f2));
          f1 = 1.0F / localTL_keyboardButtonRow.buttons.size();
          j = 0;
          if (j >= localTL_keyboardButtonRow.buttons.size()) {
            continue;
          }
          localObject = (TLRPC.KeyboardButton)localTL_keyboardButtonRow.buttons.get(j);
          localTextView = new TextView(getContext());
          localTextView.setTag(localObject);
          localTextView.setTextColor(Theme.getColor("chat_botKeyboardButtonText"));
          localTextView.setTextSize(1, 16.0F);
          localTextView.setGravity(17);
          localTextView.setBackgroundDrawable(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0F), Theme.getColor("chat_botKeyboardButtonBackground"), Theme.getColor("chat_botKeyboardButtonBackgroundPressed")));
          localTextView.setPadding(AndroidUtilities.dp(4.0F), 0, AndroidUtilities.dp(4.0F), 0);
          localTextView.setText(Emoji.replaceEmoji(((TLRPC.KeyboardButton)localObject).text, localTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(16.0F), false));
          if (j == localTL_keyboardButtonRow.buttons.size() - 1) {
            break label473;
          }
        }
        label405:
        label467:
        label473:
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
          j++;
          break label193;
          bool = false;
          break;
          i = (int)Math.max(42.0F, (this.panelHeight - AndroidUtilities.dp(30.0F) - (this.botButtons.rows.size() - 1) * AndroidUtilities.dp(10.0F)) / this.botButtons.rows.size() / AndroidUtilities.density);
          break label69;
          f1 = 10.0F;
          break label139;
          f2 = 0.0F;
          break label156;
        }
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
        for (paramInt = 0; paramInt < i; paramInt++)
        {
          View localView = this.container.getChildAt(paramInt);
          LinearLayout.LayoutParams localLayoutParams = (LinearLayout.LayoutParams)localView.getLayoutParams();
          if (localLayoutParams.height != j)
          {
            localLayoutParams.height = j;
            localView.setLayoutParams(localLayoutParams);
          }
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