package org.telegram.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Components.LayoutHelper;

public class ChangePhoneHelpActivity
  extends BaseFragment
{
  private ImageView imageView;
  private TextView textView1;
  private TextView textView2;
  
  public View createView(Context paramContext)
  {
    this.actionBar.setBackButtonImage(NUM);
    this.actionBar.setAllowOverlayTitle(true);
    Object localObject1 = UserConfig.getInstance(this.currentAccount).getCurrentUser();
    if ((localObject1 != null) && (((TLRPC.User)localObject1).phone != null) && (((TLRPC.User)localObject1).phone.length() != 0)) {
      localObject1 = PhoneFormat.getInstance().format("+" + ((TLRPC.User)localObject1).phone);
    }
    for (;;)
    {
      this.actionBar.setTitle((CharSequence)localObject1);
      this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
      {
        public void onItemClick(int paramAnonymousInt)
        {
          if (paramAnonymousInt == -1) {
            ChangePhoneHelpActivity.this.finishFragment();
          }
        }
      });
      this.fragmentView = new RelativeLayout(paramContext);
      this.fragmentView.setOnTouchListener(new View.OnTouchListener()
      {
        public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
        {
          return true;
        }
      });
      Object localObject2 = (RelativeLayout)this.fragmentView;
      localObject1 = new ScrollView(paramContext);
      ((RelativeLayout)localObject2).addView((View)localObject1);
      localObject2 = (RelativeLayout.LayoutParams)((ScrollView)localObject1).getLayoutParams();
      ((RelativeLayout.LayoutParams)localObject2).width = -1;
      ((RelativeLayout.LayoutParams)localObject2).height = -2;
      ((RelativeLayout.LayoutParams)localObject2).addRule(15, -1);
      ((ScrollView)localObject1).setLayoutParams((ViewGroup.LayoutParams)localObject2);
      localObject2 = new LinearLayout(paramContext);
      ((LinearLayout)localObject2).setOrientation(1);
      ((LinearLayout)localObject2).setPadding(0, AndroidUtilities.dp(20.0F), 0, AndroidUtilities.dp(20.0F));
      ((ScrollView)localObject1).addView((View)localObject2);
      localObject1 = (FrameLayout.LayoutParams)((LinearLayout)localObject2).getLayoutParams();
      ((FrameLayout.LayoutParams)localObject1).width = -1;
      ((FrameLayout.LayoutParams)localObject1).height = -2;
      ((LinearLayout)localObject2).setLayoutParams((ViewGroup.LayoutParams)localObject1);
      this.imageView = new ImageView(paramContext);
      this.imageView.setImageResource(NUM);
      this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("changephoneinfo_image"), PorterDuff.Mode.MULTIPLY));
      ((LinearLayout)localObject2).addView(this.imageView, LayoutHelper.createLinear(-2, -2, 1));
      this.textView1 = new TextView(paramContext);
      this.textView1.setTextSize(1, 16.0F);
      this.textView1.setGravity(1);
      this.textView1.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
      try
      {
        this.textView1.setText(AndroidUtilities.replaceTags(LocaleController.getString("PhoneNumberHelp", NUM)));
        ((LinearLayout)localObject2).addView(this.textView1, LayoutHelper.createLinear(-2, -2, 1, 20, 56, 20, 0));
        this.textView2 = new TextView(paramContext);
        this.textView2.setTextSize(1, 18.0F);
        this.textView2.setGravity(1);
        this.textView2.setTextColor(Theme.getColor("windowBackgroundWhiteBlueText4"));
        this.textView2.setText(LocaleController.getString("PhoneNumberChange", NUM));
        this.textView2.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.textView2.setPadding(0, AndroidUtilities.dp(10.0F), 0, AndroidUtilities.dp(10.0F));
        ((LinearLayout)localObject2).addView(this.textView2, LayoutHelper.createLinear(-2, -2, 1, 20, 46, 20, 0));
        this.textView2.setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramAnonymousView)
          {
            if (ChangePhoneHelpActivity.this.getParentActivity() == null) {}
            for (;;)
            {
              return;
              paramAnonymousView = new AlertDialog.Builder(ChangePhoneHelpActivity.this.getParentActivity());
              paramAnonymousView.setTitle(LocaleController.getString("AppName", NUM));
              paramAnonymousView.setMessage(LocaleController.getString("PhoneNumberAlert", NUM));
              paramAnonymousView.setPositiveButton(LocaleController.getString("OK", NUM), new DialogInterface.OnClickListener()
              {
                public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
                {
                  ChangePhoneHelpActivity.this.presentFragment(new ChangePhoneActivity(), true);
                }
              });
              paramAnonymousView.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
              ChangePhoneHelpActivity.this.showDialog(paramAnonymousView.create());
            }
          }
        });
        return this.fragmentView;
        localObject1 = LocaleController.getString("NumberUnknown", NUM);
      }
      catch (Exception localException)
      {
        for (;;)
        {
          FileLog.e(localException);
          this.textView1.setText(LocaleController.getString("PhoneNumberHelp", NUM));
        }
      }
    }
  }
  
  public ThemeDescription[] getThemeDescriptions()
  {
    return new ThemeDescription[] { new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector"), new ThemeDescription(this.textView1, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.textView2, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlueText4"), new ThemeDescription(this.imageView, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, "changephoneinfo_image") };
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/ChangePhoneHelpActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */