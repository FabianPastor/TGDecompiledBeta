package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;

public class ChatBigEmptyView
  extends LinearLayout
{
  private TextView secretViewStatusTextView;
  
  public ChatBigEmptyView(Context paramContext, boolean paramBoolean)
  {
    super(paramContext);
    setBackgroundResource(2130838000);
    getBackground().setColorFilter(Theme.colorFilter);
    setPadding(AndroidUtilities.dp(16.0F), AndroidUtilities.dp(12.0F), AndroidUtilities.dp(16.0F), AndroidUtilities.dp(12.0F));
    setOrientation(1);
    Object localObject;
    label155:
    int i;
    label183:
    int j;
    label190:
    label215:
    label245:
    ImageView localImageView;
    label283:
    TextView localTextView;
    if (paramBoolean)
    {
      this.secretViewStatusTextView = new TextView(paramContext);
      this.secretViewStatusTextView.setTextSize(1, 15.0F);
      this.secretViewStatusTextView.setTextColor(-1);
      this.secretViewStatusTextView.setGravity(1);
      this.secretViewStatusTextView.setMaxWidth(AndroidUtilities.dp(210.0F));
      addView(this.secretViewStatusTextView, LayoutHelper.createLinear(-2, -2, 49));
      localObject = new TextView(paramContext);
      if (!paramBoolean) {
        break label466;
      }
      ((TextView)localObject).setText(LocaleController.getString("EncryptedDescriptionTitle", 2131165610));
      ((TextView)localObject).setTextSize(1, 15.0F);
      ((TextView)localObject).setTextColor(-1);
      ((TextView)localObject).setMaxWidth(AndroidUtilities.dp(260.0F));
      if (!paramBoolean) {
        break label510;
      }
      if (!LocaleController.isRTL) {
        break label505;
      }
      i = 5;
      if (!paramBoolean) {
        break label515;
      }
      j = 0;
      addView((View)localObject, LayoutHelper.createLinear(-2, -2, i | 0x30, 0, 8, 0, j));
      i = 0;
      if (i >= 4) {
        return;
      }
      localObject = new LinearLayout(paramContext);
      ((LinearLayout)localObject).setOrientation(0);
      if (!LocaleController.isRTL) {
        break label522;
      }
      j = 5;
      addView((View)localObject, LayoutHelper.createLinear(-2, -2, j, 0, 8, 0, 0));
      localImageView = new ImageView(paramContext);
      if (!paramBoolean) {
        break label528;
      }
      j = 2130837738;
      localImageView.setImageResource(j);
      localTextView = new TextView(paramContext);
      localTextView.setTextSize(1, 15.0F);
      localTextView.setTextColor(-1);
      if (!LocaleController.isRTL) {
        break label535;
      }
      j = 5;
      label323:
      localTextView.setGravity(j | 0x10);
      localTextView.setMaxWidth(AndroidUtilities.dp(260.0F));
      switch (i)
      {
      default: 
        label376:
        if (!LocaleController.isRTL) {
          break label700;
        }
        ((LinearLayout)localObject).addView(localTextView, LayoutHelper.createLinear(-2, -2));
        if (paramBoolean) {
          ((LinearLayout)localObject).addView(localImageView, LayoutHelper.createLinear(-2, -2, 8.0F, 3.0F, 0.0F, 0.0F));
        }
        break;
      }
    }
    for (;;)
    {
      i += 1;
      break label215;
      localObject = new ImageView(paramContext);
      ((ImageView)localObject).setImageResource(2130837600);
      addView((View)localObject, LayoutHelper.createLinear(-2, -2, 49, 0, 2, 0, 0));
      break;
      label466:
      ((TextView)localObject).setText(LocaleController.getString("ChatYourSelfTitle", 2131165504));
      ((TextView)localObject).setTextSize(1, 16.0F);
      ((TextView)localObject).setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      ((TextView)localObject).setGravity(1);
      break label155;
      label505:
      i = 3;
      break label183;
      label510:
      i = 1;
      break label183;
      label515:
      j = 8;
      break label190;
      label522:
      j = 3;
      break label245;
      label528:
      j = 2130837790;
      break label283;
      label535:
      j = 3;
      break label323;
      if (paramBoolean)
      {
        localTextView.setText(LocaleController.getString("EncryptedDescription1", 2131165606));
        break label376;
      }
      localTextView.setText(LocaleController.getString("ChatYourSelfDescription1", 2131165499));
      break label376;
      if (paramBoolean)
      {
        localTextView.setText(LocaleController.getString("EncryptedDescription2", 2131165607));
        break label376;
      }
      localTextView.setText(LocaleController.getString("ChatYourSelfDescription2", 2131165500));
      break label376;
      if (paramBoolean)
      {
        localTextView.setText(LocaleController.getString("EncryptedDescription3", 2131165608));
        break label376;
      }
      localTextView.setText(LocaleController.getString("ChatYourSelfDescription3", 2131165501));
      break label376;
      if (paramBoolean)
      {
        localTextView.setText(LocaleController.getString("EncryptedDescription4", 2131165609));
        break label376;
      }
      localTextView.setText(LocaleController.getString("ChatYourSelfDescription4", 2131165502));
      break label376;
      ((LinearLayout)localObject).addView(localImageView, LayoutHelper.createLinear(-2, -2, 8.0F, 7.0F, 0.0F, 0.0F));
    }
    label700:
    if (paramBoolean) {
      ((LinearLayout)localObject).addView(localImageView, LayoutHelper.createLinear(-2, -2, 0.0F, 4.0F, 8.0F, 0.0F));
    }
    for (;;)
    {
      ((LinearLayout)localObject).addView(localTextView, LayoutHelper.createLinear(-2, -2));
      break;
      ((LinearLayout)localObject).addView(localImageView, LayoutHelper.createLinear(-2, -2, 0.0F, 8.0F, 8.0F, 0.0F));
    }
  }
  
  public void setSecretText(String paramString)
  {
    this.secretViewStatusTextView.setText(paramString);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/ChatBigEmptyView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */