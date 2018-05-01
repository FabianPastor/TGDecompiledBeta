package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.drawable.Drawable;
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
    Object localObject;
    if (this.isDarkTheme)
    {
      j = -15066598;
      setBackgroundColor(j);
      this.cancelButton = new TextView(paramContext);
      this.cancelButton.setTextSize(1, 14.0F);
      localObject = this.cancelButton;
      if (!this.isDarkTheme) {
        break label535;
      }
      j = -1;
      label67:
      ((TextView)localObject).setTextColor(j);
      this.cancelButton.setGravity(17);
      localObject = this.cancelButton;
      if (!this.isDarkTheme) {
        break label545;
      }
      j = -12763843;
      label100:
      ((TextView)localObject).setBackgroundDrawable(Theme.createSelectorDrawable(j, 0));
      this.cancelButton.setPadding(AndroidUtilities.dp(29.0F), 0, AndroidUtilities.dp(29.0F), 0);
      this.cancelButton.setText(LocaleController.getString("Cancel", NUM).toUpperCase());
      this.cancelButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      addView(this.cancelButton, LayoutHelper.createFrame(-2, -1, 51));
      this.doneButton = new LinearLayout(paramContext);
      this.doneButton.setOrientation(0);
      localObject = this.doneButton;
      if (!this.isDarkTheme) {
        break label552;
      }
      j = -12763843;
      label212:
      ((LinearLayout)localObject).setBackgroundDrawable(Theme.createSelectorDrawable(j, 0));
      this.doneButton.setPadding(AndroidUtilities.dp(29.0F), 0, AndroidUtilities.dp(29.0F), 0);
      addView(this.doneButton, LayoutHelper.createFrame(-2, -1, 53));
      this.doneButtonBadgeTextView = new TextView(paramContext);
      this.doneButtonBadgeTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      this.doneButtonBadgeTextView.setTextSize(1, 13.0F);
      localObject = this.doneButtonBadgeTextView;
      if (!this.isDarkTheme) {
        break label559;
      }
      j = -1;
      label308:
      ((TextView)localObject).setTextColor(j);
      this.doneButtonBadgeTextView.setGravity(17);
      if (!this.isDarkTheme) {
        break label569;
      }
      localObject = Theme.createRoundRectDrawable(AndroidUtilities.dp(11.0F), -10043398);
      label343:
      this.doneButtonBadgeTextView.setBackgroundDrawable((Drawable)localObject);
      this.doneButtonBadgeTextView.setMinWidth(AndroidUtilities.dp(23.0F));
      this.doneButtonBadgeTextView.setPadding(AndroidUtilities.dp(8.0F), 0, AndroidUtilities.dp(8.0F), AndroidUtilities.dp(1.0F));
      this.doneButton.addView(this.doneButtonBadgeTextView, LayoutHelper.createLinear(-2, 23, 16, 0, 0, 10, 0));
      this.doneButtonTextView = new TextView(paramContext);
      this.doneButtonTextView.setTextSize(1, 14.0F);
      paramContext = this.doneButtonTextView;
      if (!this.isDarkTheme) {
        break label587;
      }
    }
    label535:
    label545:
    label552:
    label559:
    label569:
    label587:
    for (int j = i;; j = Theme.getColor("picker_enabledButton"))
    {
      paramContext.setTextColor(j);
      this.doneButtonTextView.setGravity(17);
      this.doneButtonTextView.setCompoundDrawablePadding(AndroidUtilities.dp(8.0F));
      this.doneButtonTextView.setText(LocaleController.getString("Send", NUM).toUpperCase());
      this.doneButtonTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      this.doneButton.addView(this.doneButtonTextView, LayoutHelper.createLinear(-2, -2, 16));
      return;
      j = Theme.getColor("windowBackgroundWhite");
      break;
      j = Theme.getColor("picker_enabledButton");
      break label67;
      j = 788529152;
      break label100;
      j = 788529152;
      break label212;
      j = Theme.getColor("picker_badgeText");
      break label308;
      localObject = Theme.createRoundRectDrawable(AndroidUtilities.dp(11.0F), Theme.getColor("picker_badge"));
      break label343;
    }
  }
  
  public void updateSelectedCount(int paramInt, boolean paramBoolean)
  {
    int i = -1;
    TextView localTextView1 = null;
    TextView localTextView2 = null;
    Object localObject = null;
    if (paramInt == 0)
    {
      this.doneButtonBadgeTextView.setVisibility(8);
      if (paramBoolean)
      {
        localTextView1 = this.doneButtonTextView;
        if (this.isDarkTheme)
        {
          localTextView1.setTag(localObject);
          localObject = this.doneButtonTextView;
          if (!this.isDarkTheme) {
            break label86;
          }
        }
        label86:
        for (paramInt = -6710887;; paramInt = Theme.getColor("picker_disabledButton"))
        {
          ((TextView)localObject).setTextColor(paramInt);
          this.doneButton.setEnabled(false);
          return;
          localObject = "picker_disabledButton";
          break;
        }
      }
      localTextView2 = this.doneButtonTextView;
      if (this.isDarkTheme)
      {
        localObject = localTextView1;
        label112:
        localTextView2.setTag(localObject);
        localObject = this.doneButtonTextView;
        if (!this.isDarkTheme) {
          break label150;
        }
      }
      label150:
      for (paramInt = -1;; paramInt = Theme.getColor("picker_enabledButton"))
      {
        ((TextView)localObject).setTextColor(paramInt);
        break;
        localObject = "picker_enabledButton";
        break label112;
      }
    }
    this.doneButtonBadgeTextView.setVisibility(0);
    this.doneButtonBadgeTextView.setText(String.format("%d", new Object[] { Integer.valueOf(paramInt) }));
    localTextView1 = this.doneButtonTextView;
    if (this.isDarkTheme)
    {
      localObject = localTextView2;
      label207:
      localTextView1.setTag(localObject);
      localObject = this.doneButtonTextView;
      if (!this.isDarkTheme) {
        break label257;
      }
    }
    label257:
    for (paramInt = i;; paramInt = Theme.getColor("picker_enabledButton"))
    {
      ((TextView)localObject).setTextColor(paramInt);
      if (!paramBoolean) {
        break;
      }
      this.doneButton.setEnabled(true);
      break;
      localObject = "picker_enabledButton";
      break label207;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/PickerBottomLayout.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */