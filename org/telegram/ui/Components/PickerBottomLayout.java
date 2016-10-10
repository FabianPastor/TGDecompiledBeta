package org.telegram.ui.Components;

import android.content.Context;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;

public class PickerBottomLayout
  extends FrameLayout
{
  public TextView cancelButton;
  public LinearLayout doneButton;
  public TextView doneButtonBadgeTextView;
  public TextView doneButtonTextView;
  private boolean isDarkTheme;
  
  public PickerBottomLayout(Context paramContext)
  {
    this(paramContext, true);
  }
  
  public PickerBottomLayout(Context paramContext, boolean paramBoolean)
  {
    super(paramContext);
    this.isDarkTheme = paramBoolean;
    if (this.isDarkTheme)
    {
      i = -15066598;
      setBackgroundColor(i);
      this.cancelButton = new TextView(paramContext);
      this.cancelButton.setTextSize(1, 14.0F);
      Object localObject = this.cancelButton;
      if (!this.isDarkTheme) {
        break label501;
      }
      i = -1;
      label65:
      ((TextView)localObject).setTextColor(i);
      this.cancelButton.setGravity(17);
      localObject = this.cancelButton;
      if (!this.isDarkTheme) {
        break label507;
      }
      i = -12763843;
      label96:
      ((TextView)localObject).setBackgroundDrawable(Theme.createBarSelectorDrawable(i, false));
      this.cancelButton.setPadding(AndroidUtilities.dp(29.0F), 0, AndroidUtilities.dp(29.0F), 0);
      this.cancelButton.setText(LocaleController.getString("Cancel", 2131165386).toUpperCase());
      this.cancelButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      addView(this.cancelButton, LayoutHelper.createFrame(-2, -1, 51));
      this.doneButton = new LinearLayout(paramContext);
      this.doneButton.setOrientation(0);
      localObject = this.doneButton;
      if (!this.isDarkTheme) {
        break label513;
      }
      i = -12763843;
      label206:
      ((LinearLayout)localObject).setBackgroundDrawable(Theme.createBarSelectorDrawable(i, false));
      this.doneButton.setPadding(AndroidUtilities.dp(29.0F), 0, AndroidUtilities.dp(29.0F), 0);
      addView(this.doneButton, LayoutHelper.createFrame(-2, -1, 53));
      this.doneButtonBadgeTextView = new TextView(paramContext);
      this.doneButtonBadgeTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      this.doneButtonBadgeTextView.setTextSize(1, 13.0F);
      this.doneButtonBadgeTextView.setTextColor(-1);
      this.doneButtonBadgeTextView.setGravity(17);
      localObject = this.doneButtonBadgeTextView;
      if (!this.isDarkTheme) {
        break label519;
      }
      i = 2130837894;
      label318:
      ((TextView)localObject).setBackgroundResource(i);
      this.doneButtonBadgeTextView.setMinWidth(AndroidUtilities.dp(23.0F));
      this.doneButtonBadgeTextView.setPadding(AndroidUtilities.dp(8.0F), 0, AndroidUtilities.dp(8.0F), AndroidUtilities.dp(1.0F));
      this.doneButton.addView(this.doneButtonBadgeTextView, LayoutHelper.createLinear(-2, 23, 16, 0, 0, 10, 0));
      this.doneButtonTextView = new TextView(paramContext);
      this.doneButtonTextView.setTextSize(1, 14.0F);
      paramContext = this.doneButtonTextView;
      if (!this.isDarkTheme) {
        break label525;
      }
    }
    label501:
    label507:
    label513:
    label519:
    label525:
    for (int i = j;; i = -15095832)
    {
      paramContext.setTextColor(i);
      this.doneButtonTextView.setGravity(17);
      this.doneButtonTextView.setCompoundDrawablePadding(AndroidUtilities.dp(8.0F));
      this.doneButtonTextView.setText(LocaleController.getString("Send", 2131166233).toUpperCase());
      this.doneButtonTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      this.doneButton.addView(this.doneButtonTextView, LayoutHelper.createLinear(-2, -2, 16));
      return;
      i = -1;
      break;
      i = -15095832;
      break label65;
      i = 788529152;
      break label96;
      i = 788529152;
      break label206;
      i = 2130837551;
      break label318;
    }
  }
  
  public void updateSelectedCount(int paramInt, boolean paramBoolean)
  {
    int i = -1;
    if (paramInt == 0)
    {
      this.doneButtonBadgeTextView.setVisibility(8);
      if (paramBoolean)
      {
        this.doneButtonTextView.setTextColor(-6710887);
        this.doneButton.setEnabled(false);
        return;
      }
      localTextView = this.doneButtonTextView;
      if (this.isDarkTheme) {}
      for (;;)
      {
        localTextView.setTextColor(i);
        return;
        i = -15095832;
      }
    }
    this.doneButtonBadgeTextView.setVisibility(0);
    this.doneButtonBadgeTextView.setText(String.format("%d", new Object[] { Integer.valueOf(paramInt) }));
    TextView localTextView = this.doneButtonTextView;
    if (this.isDarkTheme) {}
    for (;;)
    {
      localTextView.setTextColor(i);
      if (!paramBoolean) {
        break;
      }
      this.doneButton.setEnabled(true);
      return;
      i = -15095832;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/PickerBottomLayout.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */