package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;

public class ChatBigEmptyView
  extends LinearLayout
{
  private ArrayList<ImageView> imageViews = new ArrayList();
  private TextView secretViewStatusTextView;
  private ArrayList<TextView> textViews = new ArrayList();
  
  public ChatBigEmptyView(Context paramContext, boolean paramBoolean)
  {
    super(paramContext);
    setBackgroundResource(NUM);
    getBackground().setColorFilter(Theme.colorFilter);
    setPadding(AndroidUtilities.dp(16.0F), AndroidUtilities.dp(12.0F), AndroidUtilities.dp(16.0F), AndroidUtilities.dp(12.0F));
    setOrientation(1);
    Object localObject;
    label190:
    int i;
    label230:
    int j;
    label237:
    label263:
    LinearLayout localLinearLayout;
    label294:
    ImageView localImageView;
    if (paramBoolean)
    {
      this.secretViewStatusTextView = new TextView(paramContext);
      this.secretViewStatusTextView.setTextSize(1, 15.0F);
      this.secretViewStatusTextView.setTextColor(Theme.getColor("chat_serviceText"));
      this.secretViewStatusTextView.setGravity(1);
      this.secretViewStatusTextView.setMaxWidth(AndroidUtilities.dp(210.0F));
      this.textViews.add(this.secretViewStatusTextView);
      addView(this.secretViewStatusTextView, LayoutHelper.createLinear(-2, -2, 49));
      localObject = new TextView(paramContext);
      if (!paramBoolean) {
        break label549;
      }
      ((TextView)localObject).setText(LocaleController.getString("EncryptedDescriptionTitle", NUM));
      ((TextView)localObject).setTextSize(1, 15.0F);
      ((TextView)localObject).setTextColor(Theme.getColor("chat_serviceText"));
      this.textViews.add(localObject);
      ((TextView)localObject).setMaxWidth(AndroidUtilities.dp(260.0F));
      if (!paramBoolean) {
        break label590;
      }
      if (!LocaleController.isRTL) {
        break label584;
      }
      i = 5;
      if (!paramBoolean) {
        break label596;
      }
      j = 0;
      addView((View)localObject, LayoutHelper.createLinear(-2, -2, i | 0x30, 0, 8, 0, j));
      i = 0;
      if (i >= 4) {
        return;
      }
      localLinearLayout = new LinearLayout(paramContext);
      localLinearLayout.setOrientation(0);
      if (!LocaleController.isRTL) {
        break label603;
      }
      j = 5;
      addView(localLinearLayout, LayoutHelper.createLinear(-2, -2, j, 0, 8, 0, 0));
      localImageView = new ImageView(paramContext);
      localImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_serviceText"), PorterDuff.Mode.MULTIPLY));
      if (!paramBoolean) {
        break label609;
      }
      j = NUM;
      label352:
      localImageView.setImageResource(j);
      this.imageViews.add(localImageView);
      localObject = new TextView(paramContext);
      ((TextView)localObject).setTextSize(1, 15.0F);
      ((TextView)localObject).setTextColor(Theme.getColor("chat_serviceText"));
      this.textViews.add(localObject);
      if (!LocaleController.isRTL) {
        break label616;
      }
      j = 5;
      label412:
      ((TextView)localObject).setGravity(j | 0x10);
      ((TextView)localObject).setMaxWidth(AndroidUtilities.dp(260.0F));
      switch (i)
      {
      default: 
        label464:
        if (!LocaleController.isRTL) {
          break label773;
        }
        localLinearLayout.addView((View)localObject, LayoutHelper.createLinear(-2, -2));
        if (paramBoolean) {
          localLinearLayout.addView(localImageView, LayoutHelper.createLinear(-2, -2, 8.0F, 3.0F, 0.0F, 0.0F));
        }
        break;
      }
    }
    for (;;)
    {
      i++;
      break label263;
      localObject = new ImageView(paramContext);
      ((ImageView)localObject).setImageResource(NUM);
      addView((View)localObject, LayoutHelper.createLinear(-2, -2, 49, 0, 2, 0, 0));
      break;
      label549:
      ((TextView)localObject).setText(LocaleController.getString("ChatYourSelfTitle", NUM));
      ((TextView)localObject).setTextSize(1, 16.0F);
      ((TextView)localObject).setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      ((TextView)localObject).setGravity(1);
      break label190;
      label584:
      i = 3;
      break label230;
      label590:
      i = 1;
      break label230;
      label596:
      j = 8;
      break label237;
      label603:
      j = 3;
      break label294;
      label609:
      j = NUM;
      break label352;
      label616:
      j = 3;
      break label412;
      if (paramBoolean)
      {
        ((TextView)localObject).setText(LocaleController.getString("EncryptedDescription1", NUM));
        break label464;
      }
      ((TextView)localObject).setText(LocaleController.getString("ChatYourSelfDescription1", NUM));
      break label464;
      if (paramBoolean)
      {
        ((TextView)localObject).setText(LocaleController.getString("EncryptedDescription2", NUM));
        break label464;
      }
      ((TextView)localObject).setText(LocaleController.getString("ChatYourSelfDescription2", NUM));
      break label464;
      if (paramBoolean)
      {
        ((TextView)localObject).setText(LocaleController.getString("EncryptedDescription3", NUM));
        break label464;
      }
      ((TextView)localObject).setText(LocaleController.getString("ChatYourSelfDescription3", NUM));
      break label464;
      if (paramBoolean)
      {
        ((TextView)localObject).setText(LocaleController.getString("EncryptedDescription4", NUM));
        break label464;
      }
      ((TextView)localObject).setText(LocaleController.getString("ChatYourSelfDescription4", NUM));
      break label464;
      localLinearLayout.addView(localImageView, LayoutHelper.createLinear(-2, -2, 8.0F, 7.0F, 0.0F, 0.0F));
    }
    label773:
    if (paramBoolean) {
      localLinearLayout.addView(localImageView, LayoutHelper.createLinear(-2, -2, 0.0F, 4.0F, 8.0F, 0.0F));
    }
    for (;;)
    {
      localLinearLayout.addView((View)localObject, LayoutHelper.createLinear(-2, -2));
      break;
      localLinearLayout.addView(localImageView, LayoutHelper.createLinear(-2, -2, 0.0F, 8.0F, 8.0F, 0.0F));
    }
  }
  
  public void setSecretText(String paramString)
  {
    this.secretViewStatusTextView.setText(paramString);
  }
  
  public void setTextColor(int paramInt)
  {
    for (int i = 0; i < this.textViews.size(); i++) {
      ((TextView)this.textViews.get(i)).setTextColor(paramInt);
    }
    for (paramInt = 0; paramInt < this.imageViews.size(); paramInt++) {
      ((ImageView)this.imageViews.get(paramInt)).setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_serviceText"), PorterDuff.Mode.MULTIPLY));
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/ChatBigEmptyView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */