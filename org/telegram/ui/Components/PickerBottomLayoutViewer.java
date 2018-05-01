package org.telegram.ui.Components;

import android.content.Context;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;

public class PickerBottomLayoutViewer
  extends FrameLayout
{
  public TextView cancelButton;
  public TextView doneButton;
  public TextView doneButtonBadgeTextView;
  private boolean isDarkTheme;
  
  public PickerBottomLayoutViewer(Context paramContext)
  {
    this(paramContext, true);
  }
  
  public PickerBottomLayoutViewer(Context paramContext, boolean paramBoolean)
  {
    super(paramContext);
    this.isDarkTheme = paramBoolean;
    if (this.isDarkTheme)
    {
      i = -15066598;
      setBackgroundColor(i);
      this.cancelButton = new TextView(paramContext);
      this.cancelButton.setTextSize(1, 14.0F);
      TextView localTextView = this.cancelButton;
      if (!this.isDarkTheme) {
        break label442;
      }
      i = -1;
      label62:
      localTextView.setTextColor(i);
      this.cancelButton.setGravity(17);
      localTextView = this.cancelButton;
      if (!this.isDarkTheme) {
        break label448;
      }
      i = -12763843;
      label93:
      localTextView.setBackgroundDrawable(Theme.createSelectorDrawable(i, 0));
      this.cancelButton.setPadding(AndroidUtilities.dp(20.0F), 0, AndroidUtilities.dp(20.0F), 0);
      this.cancelButton.setText(LocaleController.getString("Cancel", NUM).toUpperCase());
      this.cancelButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      addView(this.cancelButton, LayoutHelper.createFrame(-2, -1, 51));
      this.doneButton = new TextView(paramContext);
      this.doneButton.setTextSize(1, 14.0F);
      localTextView = this.doneButton;
      if (!this.isDarkTheme) {
        break label454;
      }
      i = -1;
      label204:
      localTextView.setTextColor(i);
      this.doneButton.setGravity(17);
      localTextView = this.doneButton;
      if (!this.isDarkTheme) {
        break label460;
      }
      i = -12763843;
      label235:
      localTextView.setBackgroundDrawable(Theme.createSelectorDrawable(i, 0));
      this.doneButton.setPadding(AndroidUtilities.dp(20.0F), 0, AndroidUtilities.dp(20.0F), 0);
      this.doneButton.setText(LocaleController.getString("Send", NUM).toUpperCase());
      this.doneButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      addView(this.doneButton, LayoutHelper.createFrame(-2, -1, 53));
      this.doneButtonBadgeTextView = new TextView(paramContext);
      this.doneButtonBadgeTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      this.doneButtonBadgeTextView.setTextSize(1, 13.0F);
      this.doneButtonBadgeTextView.setTextColor(-1);
      this.doneButtonBadgeTextView.setGravity(17);
      paramContext = this.doneButtonBadgeTextView;
      if (!this.isDarkTheme) {
        break label466;
      }
    }
    label442:
    label448:
    label454:
    label460:
    label466:
    for (int i = NUM;; i = NUM)
    {
      paramContext.setBackgroundResource(i);
      this.doneButtonBadgeTextView.setMinWidth(AndroidUtilities.dp(23.0F));
      this.doneButtonBadgeTextView.setPadding(AndroidUtilities.dp(8.0F), 0, AndroidUtilities.dp(8.0F), AndroidUtilities.dp(1.0F));
      addView(this.doneButtonBadgeTextView, LayoutHelper.createFrame(-2, 23.0F, 53, 0.0F, 0.0F, 7.0F, 0.0F));
      return;
      i = -1;
      break;
      i = -15095832;
      break label62;
      i = 788529152;
      break label93;
      i = -15095832;
      break label204;
      i = 788529152;
      break label235;
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
        this.doneButton.setTextColor(-6710887);
        this.doneButton.setEnabled(false);
        return;
      }
      localTextView = this.doneButton;
      if (this.isDarkTheme) {}
      for (;;)
      {
        localTextView.setTextColor(i);
        break;
        i = -15095832;
      }
    }
    this.doneButtonBadgeTextView.setVisibility(0);
    this.doneButtonBadgeTextView.setText(String.format("%d", new Object[] { Integer.valueOf(paramInt) }));
    TextView localTextView = this.doneButton;
    if (this.isDarkTheme) {}
    for (;;)
    {
      localTextView.setTextColor(i);
      if (!paramBoolean) {
        break;
      }
      this.doneButton.setEnabled(true);
      break;
      i = -15095832;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/PickerBottomLayoutViewer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */