package org.telegram.ui;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
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
import org.telegram.ui.ActionBar.BaseFragment;

public class ChangePhoneHelpActivity
  extends BaseFragment
{
  public View createView(Context paramContext)
  {
    this.actionBar.setBackButtonImage(2130837700);
    this.actionBar.setAllowOverlayTitle(true);
    Object localObject1 = UserConfig.getCurrentUser();
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
      localObject1 = (RelativeLayout)this.fragmentView;
      Object localObject2 = new ScrollView(paramContext);
      ((RelativeLayout)localObject1).addView((View)localObject2);
      localObject1 = (RelativeLayout.LayoutParams)((ScrollView)localObject2).getLayoutParams();
      ((RelativeLayout.LayoutParams)localObject1).width = -1;
      ((RelativeLayout.LayoutParams)localObject1).height = -2;
      ((RelativeLayout.LayoutParams)localObject1).addRule(15, -1);
      ((ScrollView)localObject2).setLayoutParams((ViewGroup.LayoutParams)localObject1);
      localObject1 = new LinearLayout(paramContext);
      ((LinearLayout)localObject1).setOrientation(1);
      ((LinearLayout)localObject1).setPadding(0, AndroidUtilities.dp(20.0F), 0, AndroidUtilities.dp(20.0F));
      ((ScrollView)localObject2).addView((View)localObject1);
      localObject2 = (FrameLayout.LayoutParams)((LinearLayout)localObject1).getLayoutParams();
      ((FrameLayout.LayoutParams)localObject2).width = -1;
      ((FrameLayout.LayoutParams)localObject2).height = -2;
      ((LinearLayout)localObject1).setLayoutParams((ViewGroup.LayoutParams)localObject2);
      localObject2 = new ImageView(paramContext);
      ((ImageView)localObject2).setImageResource(2130837878);
      ((LinearLayout)localObject1).addView((View)localObject2);
      LinearLayout.LayoutParams localLayoutParams = (LinearLayout.LayoutParams)((ImageView)localObject2).getLayoutParams();
      localLayoutParams.width = -2;
      localLayoutParams.height = -2;
      localLayoutParams.gravity = 1;
      ((ImageView)localObject2).setLayoutParams(localLayoutParams);
      localObject2 = new TextView(paramContext);
      ((TextView)localObject2).setTextSize(1, 16.0F);
      ((TextView)localObject2).setGravity(1);
      ((TextView)localObject2).setTextColor(-14606047);
      try
      {
        ((TextView)localObject2).setText(AndroidUtilities.replaceTags(LocaleController.getString("PhoneNumberHelp", 2131166109)));
        ((LinearLayout)localObject1).addView((View)localObject2);
        localLayoutParams = (LinearLayout.LayoutParams)((TextView)localObject2).getLayoutParams();
        localLayoutParams.width = -2;
        localLayoutParams.height = -2;
        localLayoutParams.gravity = 1;
        localLayoutParams.leftMargin = AndroidUtilities.dp(20.0F);
        localLayoutParams.rightMargin = AndroidUtilities.dp(20.0F);
        localLayoutParams.topMargin = AndroidUtilities.dp(56.0F);
        ((TextView)localObject2).setLayoutParams(localLayoutParams);
        paramContext = new TextView(paramContext);
        paramContext.setTextSize(1, 18.0F);
        paramContext.setGravity(1);
        paramContext.setTextColor(-11697229);
        paramContext.setText(LocaleController.getString("PhoneNumberChange", 2131166108));
        paramContext.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        paramContext.setPadding(0, AndroidUtilities.dp(10.0F), 0, AndroidUtilities.dp(10.0F));
        ((LinearLayout)localObject1).addView(paramContext);
        localObject1 = (LinearLayout.LayoutParams)paramContext.getLayoutParams();
        ((LinearLayout.LayoutParams)localObject1).width = -2;
        ((LinearLayout.LayoutParams)localObject1).height = -2;
        ((LinearLayout.LayoutParams)localObject1).gravity = 1;
        ((LinearLayout.LayoutParams)localObject1).leftMargin = AndroidUtilities.dp(20.0F);
        ((LinearLayout.LayoutParams)localObject1).rightMargin = AndroidUtilities.dp(20.0F);
        ((LinearLayout.LayoutParams)localObject1).topMargin = AndroidUtilities.dp(46.0F);
        paramContext.setLayoutParams((ViewGroup.LayoutParams)localObject1);
        paramContext.setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramAnonymousView)
          {
            if (ChangePhoneHelpActivity.this.getParentActivity() == null) {
              return;
            }
            paramAnonymousView = new AlertDialog.Builder(ChangePhoneHelpActivity.this.getParentActivity());
            paramAnonymousView.setTitle(LocaleController.getString("AppName", 2131165299));
            paramAnonymousView.setMessage(LocaleController.getString("PhoneNumberAlert", 2131166107));
            paramAnonymousView.setPositiveButton(LocaleController.getString("OK", 2131166044), new DialogInterface.OnClickListener()
            {
              public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
              {
                ChangePhoneHelpActivity.this.presentFragment(new ChangePhoneActivity(), true);
              }
            });
            paramAnonymousView.setNegativeButton(LocaleController.getString("Cancel", 2131165386), null);
            ChangePhoneHelpActivity.this.showDialog(paramAnonymousView.create());
          }
        });
        return this.fragmentView;
        localObject1 = LocaleController.getString("NumberUnknown", 2131166043);
      }
      catch (Exception localException)
      {
        for (;;)
        {
          FileLog.e("tmessages", localException);
          ((TextView)localObject2).setText(LocaleController.getString("PhoneNumberHelp", 2131166109));
        }
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/ChangePhoneHelpActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */