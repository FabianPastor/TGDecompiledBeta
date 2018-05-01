package org.telegram.ui.Components;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.LongSparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.MessagesStorage.IntCallback;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationsController;
import org.telegram.messenger.SecretChatHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.InputPeer;
import org.telegram.tgnet.TLRPC.TL_account_changePhone;
import org.telegram.tgnet.TLRPC.TL_account_confirmPhone;
import org.telegram.tgnet.TLRPC.TL_account_getPassword;
import org.telegram.tgnet.TLRPC.TL_account_getTmpPassword;
import org.telegram.tgnet.TLRPC.TL_account_reportPeer;
import org.telegram.tgnet.TLRPC.TL_account_sendChangePhoneCode;
import org.telegram.tgnet.TLRPC.TL_account_sendConfirmPhoneCode;
import org.telegram.tgnet.TLRPC.TL_auth_resendCode;
import org.telegram.tgnet.TLRPC.TL_channels_createChannel;
import org.telegram.tgnet.TLRPC.TL_channels_editAdmin;
import org.telegram.tgnet.TLRPC.TL_channels_editBanned;
import org.telegram.tgnet.TLRPC.TL_channels_inviteToChannel;
import org.telegram.tgnet.TLRPC.TL_channels_joinChannel;
import org.telegram.tgnet.TLRPC.TL_contacts_importContacts;
import org.telegram.tgnet.TLRPC.TL_dialog;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputReportReasonPornography;
import org.telegram.tgnet.TLRPC.TL_inputReportReasonSpam;
import org.telegram.tgnet.TLRPC.TL_inputReportReasonViolence;
import org.telegram.tgnet.TLRPC.TL_messages_addChatUser;
import org.telegram.tgnet.TLRPC.TL_messages_createChat;
import org.telegram.tgnet.TLRPC.TL_messages_editMessage;
import org.telegram.tgnet.TLRPC.TL_messages_forwardMessages;
import org.telegram.tgnet.TLRPC.TL_messages_getAttachedStickers;
import org.telegram.tgnet.TLRPC.TL_messages_importChatInvite;
import org.telegram.tgnet.TLRPC.TL_messages_report;
import org.telegram.tgnet.TLRPC.TL_messages_sendBroadcast;
import org.telegram.tgnet.TLRPC.TL_messages_sendInlineBotResult;
import org.telegram.tgnet.TLRPC.TL_messages_sendMedia;
import org.telegram.tgnet.TLRPC.TL_messages_sendMessage;
import org.telegram.tgnet.TLRPC.TL_messages_startBot;
import org.telegram.tgnet.TLRPC.TL_payments_sendPaymentForm;
import org.telegram.tgnet.TLRPC.TL_payments_validateRequestedInfo;
import org.telegram.tgnet.TLRPC.TL_peerNotifySettings;
import org.telegram.tgnet.TLRPC.TL_updateUserName;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet.Builder;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.CacheControlActivity;
import org.telegram.ui.Cells.RadioColorCell;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.ReportOtherActivity;

public class AlertsCreator
{
  public static Dialog createColorSelectDialog(Activity paramActivity, final long paramLong, final boolean paramBoolean1, boolean paramBoolean2, Runnable paramRunnable)
  {
    final Object localObject = MessagesController.getNotificationsSettings(UserConfig.selectedAccount);
    int i;
    LinearLayout localLinearLayout;
    int j;
    label136:
    RadioColorCell localRadioColorCell;
    String str10;
    if (paramBoolean1)
    {
      i = ((SharedPreferences)localObject).getInt("GroupLed", -16776961);
      localLinearLayout = new LinearLayout(paramActivity);
      localLinearLayout.setOrientation(1);
      String str1 = LocaleController.getString("ColorRed", NUM);
      String str2 = LocaleController.getString("ColorOrange", NUM);
      String str3 = LocaleController.getString("ColorYellow", NUM);
      String str4 = LocaleController.getString("ColorGreen", NUM);
      String str5 = LocaleController.getString("ColorCyan", NUM);
      String str6 = LocaleController.getString("ColorBlue", NUM);
      String str7 = LocaleController.getString("ColorViolet", NUM);
      String str8 = LocaleController.getString("ColorPink", NUM);
      String str9 = LocaleController.getString("ColorWhite", NUM);
      localObject = new int[1];
      localObject[0] = i;
      j = 0;
      if (j >= 9) {
        break label433;
      }
      localRadioColorCell = new RadioColorCell(paramActivity);
      localRadioColorCell.setPadding(AndroidUtilities.dp(4.0F), 0, AndroidUtilities.dp(4.0F), 0);
      localRadioColorCell.setTag(Integer.valueOf(j));
      localRadioColorCell.setCheckColor(org.telegram.ui.Cells.TextColorCell.colors[j], org.telegram.ui.Cells.TextColorCell.colors[j]);
      str10 = new String[] { str1, str2, str3, str4, str5, str6, str7, str8, str9 }[j];
      if (i != org.telegram.ui.Cells.TextColorCell.colorsToSave[j]) {
        break label427;
      }
    }
    label427:
    for (boolean bool = true;; bool = false)
    {
      localRadioColorCell.setTextAndValue(str10, bool);
      localLinearLayout.addView(localRadioColorCell);
      localRadioColorCell.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          int i = this.val$linearLayout.getChildCount();
          int j = 0;
          if (j < i)
          {
            RadioColorCell localRadioColorCell = (RadioColorCell)this.val$linearLayout.getChildAt(j);
            if (localRadioColorCell == paramAnonymousView) {}
            for (boolean bool = true;; bool = false)
            {
              localRadioColorCell.setChecked(bool, true);
              j++;
              break;
            }
          }
          localObject[0] = org.telegram.ui.Cells.TextColorCell.colorsToSave[((Integer)paramAnonymousView.getTag()).intValue()];
        }
      });
      j++;
      break label136;
      if (paramBoolean2)
      {
        i = ((SharedPreferences)localObject).getInt("MessagesLed", -16776961);
        break;
      }
      if (((SharedPreferences)localObject).contains("color_" + paramLong))
      {
        i = ((SharedPreferences)localObject).getInt("color_" + paramLong, -16776961);
        break;
      }
      if ((int)paramLong < 0)
      {
        i = ((SharedPreferences)localObject).getInt("GroupLed", -16776961);
        break;
      }
      i = ((SharedPreferences)localObject).getInt("MessagesLed", -16776961);
      break;
    }
    label433:
    paramActivity = new AlertDialog.Builder(paramActivity);
    paramActivity.setTitle(LocaleController.getString("LedColor", NUM));
    paramActivity.setView(localLinearLayout);
    paramActivity.setPositiveButton(LocaleController.getString("Set", NUM), new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        paramAnonymousDialogInterface = MessagesController.getNotificationsSettings(UserConfig.selectedAccount).edit();
        if (this.val$globalAll) {
          paramAnonymousDialogInterface.putInt("MessagesLed", localObject[0]);
        }
        for (;;)
        {
          paramAnonymousDialogInterface.commit();
          if (this.val$onSelect != null) {
            this.val$onSelect.run();
          }
          return;
          if (paramBoolean1) {
            paramAnonymousDialogInterface.putInt("GroupLed", localObject[0]);
          } else {
            paramAnonymousDialogInterface.putInt("color_" + paramLong, localObject[0]);
          }
        }
      }
    });
    paramActivity.setNeutralButton(LocaleController.getString("LedDisabled", NUM), new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        paramAnonymousDialogInterface = MessagesController.getNotificationsSettings(UserConfig.selectedAccount).edit();
        if (this.val$globalAll) {
          paramAnonymousDialogInterface.putInt("MessagesLed", 0);
        }
        for (;;)
        {
          paramAnonymousDialogInterface.commit();
          if (this.val$onSelect != null) {
            this.val$onSelect.run();
          }
          return;
          if (paramBoolean1) {
            paramAnonymousDialogInterface.putInt("GroupLed", 0);
          } else {
            paramAnonymousDialogInterface.putInt("color_" + paramLong, 0);
          }
        }
      }
    });
    if ((!paramBoolean2) && (!paramBoolean1)) {
      paramActivity.setNegativeButton(LocaleController.getString("Default", NUM), new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          paramAnonymousDialogInterface = MessagesController.getNotificationsSettings(UserConfig.selectedAccount).edit();
          paramAnonymousDialogInterface.remove("color_" + this.val$dialog_id);
          paramAnonymousDialogInterface.commit();
          if (this.val$onSelect != null) {
            this.val$onSelect.run();
          }
        }
      });
    }
    return paramActivity.create();
  }
  
  public static Dialog createFreeSpaceDialog(LaunchActivity paramLaunchActivity)
  {
    int[] arrayOfInt = new int[1];
    int i = MessagesController.getGlobalMainSettings().getInt("keep_media", 2);
    final LinearLayout localLinearLayout;
    Object localObject2;
    label148:
    label165:
    label192:
    String str;
    if (i == 2)
    {
      arrayOfInt[0] = 3;
      localObject1 = new String[4];
      localObject1[0] = LocaleController.formatPluralString("Days", 3);
      localObject1[1] = LocaleController.formatPluralString("Weeks", 1);
      localObject1[2] = LocaleController.formatPluralString("Months", 1);
      localObject1[3] = LocaleController.getString("LowDiskSpaceNeverRemove", NUM);
      localLinearLayout = new LinearLayout(paramLaunchActivity);
      localLinearLayout.setOrientation(1);
      localObject2 = new TextView(paramLaunchActivity);
      ((TextView)localObject2).setText(LocaleController.getString("LowDiskSpaceTitle2", NUM));
      ((TextView)localObject2).setTextColor(Theme.getColor("dialogTextBlack"));
      ((TextView)localObject2).setTextSize(1, 16.0F);
      ((TextView)localObject2).setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      if (!LocaleController.isRTL) {
        break label338;
      }
      i = 5;
      ((TextView)localObject2).setGravity(i | 0x30);
      if (!LocaleController.isRTL) {
        break label343;
      }
      i = 5;
      localLinearLayout.addView((View)localObject2, LayoutHelper.createLinear(-2, -2, i | 0x30, 24, 0, 24, 8));
      i = 0;
      if (i >= localObject1.length) {
        break label354;
      }
      localObject2 = new RadioColorCell(paramLaunchActivity);
      ((RadioColorCell)localObject2).setPadding(AndroidUtilities.dp(4.0F), 0, AndroidUtilities.dp(4.0F), 0);
      ((RadioColorCell)localObject2).setTag(Integer.valueOf(i));
      ((RadioColorCell)localObject2).setCheckColor(Theme.getColor("radioBackground"), Theme.getColor("dialogRadioBackgroundChecked"));
      str = localObject1[i];
      if (arrayOfInt[0] != i) {
        break label348;
      }
    }
    label338:
    label343:
    label348:
    for (boolean bool = true;; bool = false)
    {
      ((RadioColorCell)localObject2).setTextAndValue(str, bool);
      localLinearLayout.addView((View)localObject2);
      ((RadioColorCell)localObject2).setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          int i = ((Integer)paramAnonymousView.getTag()).intValue();
          label32:
          RadioColorCell localRadioColorCell;
          if (i == 0)
          {
            this.val$selected[0] = 3;
            int j = localLinearLayout.getChildCount();
            i = 0;
            if (i >= j) {
              return;
            }
            View localView = localLinearLayout.getChildAt(i);
            if ((localView instanceof RadioColorCell))
            {
              localRadioColorCell = (RadioColorCell)localView;
              if (localView != paramAnonymousView) {
                break label130;
              }
            }
          }
          label130:
          for (boolean bool = true;; bool = false)
          {
            localRadioColorCell.setChecked(bool, true);
            i++;
            break label32;
            if (i == 1)
            {
              this.val$selected[0] = 0;
              break;
            }
            if (i == 2)
            {
              this.val$selected[0] = 1;
              break;
            }
            if (i != 3) {
              break;
            }
            this.val$selected[0] = 2;
            break;
          }
        }
      });
      i++;
      break label192;
      if (i == 0)
      {
        arrayOfInt[0] = 1;
        break;
      }
      if (i == 1)
      {
        arrayOfInt[0] = 2;
        break;
      }
      if (i != 3) {
        break;
      }
      arrayOfInt[0] = 0;
      break;
      i = 3;
      break label148;
      i = 3;
      break label165;
    }
    label354:
    Object localObject1 = new AlertDialog.Builder(paramLaunchActivity);
    ((AlertDialog.Builder)localObject1).setTitle(LocaleController.getString("LowDiskSpaceTitle", NUM));
    ((AlertDialog.Builder)localObject1).setMessage(LocaleController.getString("LowDiskSpaceMessage", NUM));
    ((AlertDialog.Builder)localObject1).setView(localLinearLayout);
    ((AlertDialog.Builder)localObject1).setPositiveButton(LocaleController.getString("OK", NUM), new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        MessagesController.getGlobalMainSettings().edit().putInt("keep_media", this.val$selected[0]).commit();
      }
    });
    ((AlertDialog.Builder)localObject1).setNeutralButton(LocaleController.getString("ClearMediaCache", NUM), new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        this.val$parentActivity.presentFragment(new CacheControlActivity());
      }
    });
    return ((AlertDialog.Builder)localObject1).create();
  }
  
  public static Dialog createLocationUpdateDialog(Activity paramActivity, TLRPC.User paramUser, final MessagesStorage.IntCallback paramIntCallback)
  {
    int[] arrayOfInt = new int[1];
    String[] arrayOfString = new String[3];
    arrayOfString[0] = LocaleController.getString("SendLiveLocationFor15m", NUM);
    arrayOfString[1] = LocaleController.getString("SendLiveLocationFor1h", NUM);
    arrayOfString[2] = LocaleController.getString("SendLiveLocationFor8h", NUM);
    final LinearLayout localLinearLayout = new LinearLayout(paramActivity);
    localLinearLayout.setOrientation(1);
    Object localObject = new TextView(paramActivity);
    int i;
    if (paramUser != null)
    {
      ((TextView)localObject).setText(LocaleController.formatString("LiveLocationAlertPrivate", NUM, new Object[] { UserObject.getFirstName(paramUser) }));
      ((TextView)localObject).setTextColor(Theme.getColor("dialogTextBlack"));
      ((TextView)localObject).setTextSize(1, 16.0F);
      if (!LocaleController.isRTL) {
        break label313;
      }
      i = 5;
      label133:
      ((TextView)localObject).setGravity(i | 0x30);
      if (!LocaleController.isRTL) {
        break label319;
      }
      i = 5;
      label152:
      localLinearLayout.addView((View)localObject, LayoutHelper.createLinear(-2, -2, i | 0x30, 24, 0, 24, 8));
      i = 0;
      label181:
      if (i >= arrayOfString.length) {
        break label331;
      }
      localObject = new RadioColorCell(paramActivity);
      ((RadioColorCell)localObject).setPadding(AndroidUtilities.dp(4.0F), 0, AndroidUtilities.dp(4.0F), 0);
      ((RadioColorCell)localObject).setTag(Integer.valueOf(i));
      ((RadioColorCell)localObject).setCheckColor(Theme.getColor("radioBackground"), Theme.getColor("dialogRadioBackgroundChecked"));
      paramUser = arrayOfString[i];
      if (arrayOfInt[0] != i) {
        break label325;
      }
    }
    label313:
    label319:
    label325:
    for (boolean bool = true;; bool = false)
    {
      ((RadioColorCell)localObject).setTextAndValue(paramUser, bool);
      localLinearLayout.addView((View)localObject);
      ((RadioColorCell)localObject).setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          int i = ((Integer)paramAnonymousView.getTag()).intValue();
          this.val$selected[0] = i;
          int j = localLinearLayout.getChildCount();
          i = 0;
          if (i < j)
          {
            View localView = localLinearLayout.getChildAt(i);
            RadioColorCell localRadioColorCell;
            if ((localView instanceof RadioColorCell))
            {
              localRadioColorCell = (RadioColorCell)localView;
              if (localView != paramAnonymousView) {
                break label81;
              }
            }
            label81:
            for (boolean bool = true;; bool = false)
            {
              localRadioColorCell.setChecked(bool, true);
              i++;
              break;
            }
          }
        }
      });
      i++;
      break label181;
      ((TextView)localObject).setText(LocaleController.getString("LiveLocationAlertGroup", NUM));
      break;
      i = 3;
      break label133;
      i = 3;
      break label152;
    }
    label331:
    paramUser = new AlertDialog.Builder(paramActivity);
    paramUser.setTopImage(new ShareLocationDrawable(paramActivity, false), Theme.getColor("dialogTopBackground"));
    paramUser.setView(localLinearLayout);
    paramUser.setPositiveButton(LocaleController.getString("ShareFile", NUM), new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        if (this.val$selected[0] == 0) {
          paramAnonymousInt = 900;
        }
        for (;;)
        {
          paramIntCallback.run(paramAnonymousInt);
          return;
          if (this.val$selected[0] == 1) {
            paramAnonymousInt = 3600;
          } else {
            paramAnonymousInt = 28800;
          }
        }
      }
    });
    paramUser.setNeutralButton(LocaleController.getString("Cancel", NUM), null);
    return paramUser.create();
  }
  
  public static Dialog createMuteAlert(Context paramContext, long paramLong)
  {
    if (paramContext == null) {}
    for (paramContext = null;; paramContext = paramContext.create())
    {
      return paramContext;
      paramContext = new BottomSheet.Builder(paramContext);
      paramContext.setTitle(LocaleController.getString("Notifications", NUM));
      String str1 = LocaleController.formatString("MuteFor", NUM, new Object[] { LocaleController.formatPluralString("Hours", 1) });
      String str2 = LocaleController.formatString("MuteFor", NUM, new Object[] { LocaleController.formatPluralString("Hours", 8) });
      String str3 = LocaleController.formatString("MuteFor", NUM, new Object[] { LocaleController.formatPluralString("Days", 2) });
      String str4 = LocaleController.getString("MuteDisable", NUM);
      DialogInterface.OnClickListener local1 = new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          int i = ConnectionsManager.getInstance(UserConfig.selectedAccount).getCurrentTime();
          if (paramAnonymousInt == 0)
          {
            i += 3600;
            paramAnonymousDialogInterface = MessagesController.getNotificationsSettings(UserConfig.selectedAccount).edit();
            if (paramAnonymousInt != 3) {
              break label200;
            }
            paramAnonymousDialogInterface.putInt("notify2_" + this.val$dialog_id, 2);
          }
          for (long l = 1L;; l = i << 32 | 1L)
          {
            NotificationsController.getInstance(UserConfig.selectedAccount).removeNotificationsForDialog(this.val$dialog_id);
            MessagesStorage.getInstance(UserConfig.selectedAccount).setDialogFlags(this.val$dialog_id, l);
            paramAnonymousDialogInterface.commit();
            paramAnonymousDialogInterface = (TLRPC.TL_dialog)MessagesController.getInstance(UserConfig.selectedAccount).dialogs_dict.get(this.val$dialog_id);
            if (paramAnonymousDialogInterface != null)
            {
              paramAnonymousDialogInterface.notify_settings = new TLRPC.TL_peerNotifySettings();
              paramAnonymousDialogInterface.notify_settings.mute_until = i;
            }
            NotificationsController.getInstance(UserConfig.selectedAccount).updateServerNotificationsSettings(this.val$dialog_id);
            return;
            if (paramAnonymousInt == 1)
            {
              i += 28800;
              break;
            }
            if (paramAnonymousInt == 2)
            {
              i += 172800;
              break;
            }
            if (paramAnonymousInt != 3) {
              break;
            }
            i = Integer.MAX_VALUE;
            break;
            label200:
            paramAnonymousDialogInterface.putInt("notify2_" + this.val$dialog_id, 3);
            paramAnonymousDialogInterface.putInt("notifyuntil_" + this.val$dialog_id, i);
          }
        }
      };
      paramContext.setItems(new CharSequence[] { str1, str2, str3, str4 }, local1);
    }
  }
  
  public static Dialog createPopupSelectDialog(Activity paramActivity, final BaseFragment paramBaseFragment, final boolean paramBoolean1, boolean paramBoolean2, final Runnable paramRunnable)
  {
    Object localObject = MessagesController.getNotificationsSettings(UserConfig.selectedAccount);
    int[] arrayOfInt = new int[1];
    int i;
    label109:
    RadioColorCell localRadioColorCell;
    String str;
    if (paramBoolean2)
    {
      arrayOfInt[0] = ((SharedPreferences)localObject).getInt("popupAll", 0);
      String[] arrayOfString = new String[4];
      arrayOfString[0] = LocaleController.getString("NoPopup", NUM);
      arrayOfString[1] = LocaleController.getString("OnlyWhenScreenOn", NUM);
      arrayOfString[2] = LocaleController.getString("OnlyWhenScreenOff", NUM);
      arrayOfString[3] = LocaleController.getString("AlwaysShowPopup", NUM);
      localObject = new LinearLayout(paramActivity);
      ((LinearLayout)localObject).setOrientation(1);
      i = 0;
      if (i >= arrayOfString.length) {
        break label255;
      }
      localRadioColorCell = new RadioColorCell(paramActivity);
      localRadioColorCell.setTag(Integer.valueOf(i));
      localRadioColorCell.setPadding(AndroidUtilities.dp(4.0F), 0, AndroidUtilities.dp(4.0F), 0);
      localRadioColorCell.setCheckColor(Theme.getColor("radioBackground"), Theme.getColor("dialogRadioBackgroundChecked"));
      str = arrayOfString[i];
      if (arrayOfInt[0] != i) {
        break label250;
      }
    }
    label250:
    for (paramBoolean2 = true;; paramBoolean2 = false)
    {
      localRadioColorCell.setTextAndValue(str, paramBoolean2);
      ((LinearLayout)localObject).addView(localRadioColorCell);
      localRadioColorCell.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          this.val$selected[0] = ((Integer)paramAnonymousView.getTag()).intValue();
          SharedPreferences.Editor localEditor = MessagesController.getNotificationsSettings(UserConfig.selectedAccount).edit();
          if (paramBoolean1) {}
          for (paramAnonymousView = "popupGroup";; paramAnonymousView = "popupAll")
          {
            localEditor.putInt(paramAnonymousView, this.val$selected[0]);
            localEditor.commit();
            if (paramBaseFragment != null) {
              paramBaseFragment.dismissCurrentDialig();
            }
            if (paramRunnable != null) {
              paramRunnable.run();
            }
            return;
          }
        }
      });
      i++;
      break label109;
      if (!paramBoolean1) {
        break;
      }
      arrayOfInt[0] = ((SharedPreferences)localObject).getInt("popupGroup", 0);
      break;
    }
    label255:
    paramActivity = new AlertDialog.Builder(paramActivity);
    paramActivity.setTitle(LocaleController.getString("PopupNotification", NUM));
    paramActivity.setView((View)localObject);
    paramActivity.setPositiveButton(LocaleController.getString("Cancel", NUM), null);
    return paramActivity.create();
  }
  
  public static Dialog createPrioritySelectDialog(Activity paramActivity, final BaseFragment paramBaseFragment, final long paramLong, boolean paramBoolean1, boolean paramBoolean2, final Runnable paramRunnable)
  {
    Object localObject = MessagesController.getNotificationsSettings(UserConfig.selectedAccount);
    int[] arrayOfInt = new int[1];
    LinearLayout localLinearLayout;
    int i;
    label154:
    RadioColorCell localRadioColorCell;
    String str;
    if (paramLong != 0L)
    {
      arrayOfInt[0] = ((SharedPreferences)localObject).getInt("priority_" + paramLong, 3);
      if (arrayOfInt[0] == 3)
      {
        arrayOfInt[0] = 0;
        localObject = new String[5];
        localObject[0] = LocaleController.getString("NotificationsPrioritySettings", NUM);
        localObject[1] = LocaleController.getString("NotificationsPriorityLow", NUM);
        localObject[2] = LocaleController.getString("NotificationsPriorityMedium", NUM);
        localObject[3] = LocaleController.getString("NotificationsPriorityHigh", NUM);
        localObject[4] = LocaleController.getString("NotificationsPriorityUrgent", NUM);
        localLinearLayout = new LinearLayout(paramActivity);
        localLinearLayout.setOrientation(1);
        i = 0;
        if (i >= localObject.length) {
          break label494;
        }
        localRadioColorCell = new RadioColorCell(paramActivity);
        localRadioColorCell.setPadding(AndroidUtilities.dp(4.0F), 0, AndroidUtilities.dp(4.0F), 0);
        localRadioColorCell.setTag(Integer.valueOf(i));
        localRadioColorCell.setCheckColor(Theme.getColor("radioBackground"), Theme.getColor("dialogRadioBackgroundChecked"));
        str = localObject[i];
        if (arrayOfInt[0] != i) {
          break label488;
        }
      }
    }
    label352:
    label449:
    label488:
    for (paramBoolean2 = true;; paramBoolean2 = false)
    {
      localRadioColorCell.setTextAndValue(str, paramBoolean2);
      localLinearLayout.addView(localRadioColorCell);
      localRadioColorCell.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          this.val$selected[0] = ((Integer)paramAnonymousView.getTag()).intValue();
          SharedPreferences.Editor localEditor = MessagesController.getNotificationsSettings(UserConfig.selectedAccount).edit();
          int i;
          if (paramLong != 0L)
          {
            if (this.val$selected[0] == 0) {
              i = 3;
            }
            for (;;)
            {
              localEditor.putInt("priority_" + paramLong, i);
              localEditor.commit();
              if (paramRunnable != null) {
                paramRunnable.dismissCurrentDialig();
              }
              if (this.val$onSelect != null) {
                this.val$onSelect.run();
              }
              return;
              if (this.val$selected[0] == 1) {
                i = 4;
              } else if (this.val$selected[0] == 2) {
                i = 5;
              } else if (this.val$selected[0] == 3) {
                i = 0;
              } else {
                i = 1;
              }
            }
          }
          if (this.val$selected[0] == 0)
          {
            i = 4;
            label177:
            if (!paramBaseFragment) {
              break label234;
            }
          }
          label234:
          for (paramAnonymousView = "priority_group";; paramAnonymousView = "priority_messages")
          {
            localEditor.putInt(paramAnonymousView, i);
            break;
            if (this.val$selected[0] == 1)
            {
              i = 5;
              break label177;
            }
            if (this.val$selected[0] == 2)
            {
              i = 0;
              break label177;
            }
            i = 1;
            break label177;
          }
        }
      });
      i++;
      break label154;
      if (arrayOfInt[0] == 4)
      {
        arrayOfInt[0] = 1;
        break;
      }
      if (arrayOfInt[0] == 5)
      {
        arrayOfInt[0] = 2;
        break;
      }
      if (arrayOfInt[0] == 0)
      {
        arrayOfInt[0] = 3;
        break;
      }
      arrayOfInt[0] = 4;
      break;
      if (paramBoolean2)
      {
        arrayOfInt[0] = ((SharedPreferences)localObject).getInt("priority_messages", 1);
        if (arrayOfInt[0] != 4) {
          break label449;
        }
        arrayOfInt[0] = 0;
      }
      for (;;)
      {
        localObject = new String[4];
        localObject[0] = LocaleController.getString("NotificationsPriorityLow", NUM);
        localObject[1] = LocaleController.getString("NotificationsPriorityMedium", NUM);
        localObject[2] = LocaleController.getString("NotificationsPriorityHigh", NUM);
        localObject[3] = LocaleController.getString("NotificationsPriorityUrgent", NUM);
        break;
        if (!paramBoolean1) {
          break label352;
        }
        arrayOfInt[0] = ((SharedPreferences)localObject).getInt("priority_group", 1);
        break label352;
        if (arrayOfInt[0] == 5) {
          arrayOfInt[0] = 1;
        } else if (arrayOfInt[0] == 0) {
          arrayOfInt[0] = 2;
        } else {
          arrayOfInt[0] = 3;
        }
      }
    }
    label494:
    paramActivity = new AlertDialog.Builder(paramActivity);
    paramActivity.setTitle(LocaleController.getString("NotificationsImportance", NUM));
    paramActivity.setView(localLinearLayout);
    paramActivity.setPositiveButton(LocaleController.getString("Cancel", NUM), null);
    return paramActivity.create();
  }
  
  public static Dialog createReportAlert(final Context paramContext, long paramLong, int paramInt, final BaseFragment paramBaseFragment)
  {
    if ((paramContext == null) || (paramBaseFragment == null)) {}
    BottomSheet.Builder localBuilder;
    for (paramContext = null;; paramContext = localBuilder.create())
    {
      return paramContext;
      localBuilder = new BottomSheet.Builder(paramContext);
      localBuilder.setTitle(LocaleController.getString("ReportChat", NUM));
      String str1 = LocaleController.getString("ReportChatSpam", NUM);
      String str2 = LocaleController.getString("ReportChatViolence", NUM);
      String str3 = LocaleController.getString("ReportChatPornography", NUM);
      String str4 = LocaleController.getString("ReportChatOther", NUM);
      paramContext = new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          if (paramAnonymousInt == 3)
          {
            paramAnonymousDialogInterface = new Bundle();
            paramAnonymousDialogInterface.putLong("dialog_id", this.val$dialog_id);
            paramAnonymousDialogInterface.putLong("message_id", paramBaseFragment);
            paramContext.presentFragment(new ReportOtherActivity(paramAnonymousDialogInterface));
            return;
          }
          TLRPC.InputPeer localInputPeer = MessagesController.getInstance(UserConfig.selectedAccount).getInputPeer((int)this.val$dialog_id);
          if (paramBaseFragment != 0)
          {
            paramAnonymousDialogInterface = new TLRPC.TL_messages_report();
            paramAnonymousDialogInterface.peer = localInputPeer;
            paramAnonymousDialogInterface.id.add(Integer.valueOf(paramBaseFragment));
            if (paramAnonymousInt == 0) {
              paramAnonymousDialogInterface.reason = new TLRPC.TL_inputReportReasonSpam();
            }
            for (;;)
            {
              ConnectionsManager.getInstance(UserConfig.selectedAccount).sendRequest(paramAnonymousDialogInterface, new RequestDelegate()
              {
                public void run(TLObject paramAnonymous2TLObject, TLRPC.TL_error paramAnonymous2TL_error) {}
              });
              Toast.makeText(this.val$context, LocaleController.getString("ReportChatSent", NUM), 0).show();
              break;
              if (paramAnonymousInt == 1) {
                paramAnonymousDialogInterface.reason = new TLRPC.TL_inputReportReasonViolence();
              } else if (paramAnonymousInt == 2) {
                paramAnonymousDialogInterface.reason = new TLRPC.TL_inputReportReasonPornography();
              }
            }
          }
          paramAnonymousDialogInterface = new TLRPC.TL_account_reportPeer();
          paramAnonymousDialogInterface.peer = localInputPeer;
          if (paramAnonymousInt == 0) {
            paramAnonymousDialogInterface.reason = new TLRPC.TL_inputReportReasonSpam();
          }
          for (;;)
          {
            break;
            if (paramAnonymousInt == 1) {
              paramAnonymousDialogInterface.reason = new TLRPC.TL_inputReportReasonViolence();
            } else if (paramAnonymousInt == 2) {
              paramAnonymousDialogInterface.reason = new TLRPC.TL_inputReportReasonPornography();
            }
          }
        }
      };
      localBuilder.setItems(new CharSequence[] { str1, str2, str3, str4 }, paramContext);
    }
  }
  
  public static Dialog createSingleChoiceDialog(Activity paramActivity, BaseFragment paramBaseFragment, String[] paramArrayOfString, String paramString, int paramInt, final DialogInterface.OnClickListener paramOnClickListener)
  {
    LinearLayout localLinearLayout = new LinearLayout(paramActivity);
    localLinearLayout.setOrientation(1);
    int i = 0;
    if (i < paramArrayOfString.length)
    {
      RadioColorCell localRadioColorCell = new RadioColorCell(paramActivity);
      localRadioColorCell.setPadding(AndroidUtilities.dp(4.0F), 0, AndroidUtilities.dp(4.0F), 0);
      localRadioColorCell.setTag(Integer.valueOf(i));
      localRadioColorCell.setCheckColor(Theme.getColor("radioBackground"), Theme.getColor("dialogRadioBackgroundChecked"));
      String str = paramArrayOfString[i];
      if (paramInt == i) {}
      for (boolean bool = true;; bool = false)
      {
        localRadioColorCell.setTextAndValue(str, bool);
        localLinearLayout.addView(localRadioColorCell);
        localRadioColorCell.setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramAnonymousView)
          {
            int i = ((Integer)paramAnonymousView.getTag()).intValue();
            if (this.val$parentFragment != null) {
              this.val$parentFragment.dismissCurrentDialig();
            }
            paramOnClickListener.onClick(null, i);
          }
        });
        i++;
        break;
      }
    }
    paramActivity = new AlertDialog.Builder(paramActivity);
    paramActivity.setTitle(paramString);
    paramActivity.setView(localLinearLayout);
    paramActivity.setPositiveButton(LocaleController.getString("Cancel", NUM), null);
    return paramActivity.create();
  }
  
  public static AlertDialog.Builder createTTLAlert(final Context paramContext, TLRPC.EncryptedChat paramEncryptedChat)
  {
    AlertDialog.Builder localBuilder = new AlertDialog.Builder(paramContext);
    localBuilder.setTitle(LocaleController.getString("MessageLifetime", NUM));
    paramContext = new NumberPicker(paramContext);
    paramContext.setMinValue(0);
    paramContext.setMaxValue(20);
    if ((paramEncryptedChat.ttl > 0) && (paramEncryptedChat.ttl < 16)) {
      paramContext.setValue(paramEncryptedChat.ttl);
    }
    for (;;)
    {
      paramContext.setFormatter(new NumberPicker.Formatter()
      {
        public String format(int paramAnonymousInt)
        {
          String str;
          if (paramAnonymousInt == 0) {
            str = LocaleController.getString("ShortMessageLifetimeForever", NUM);
          }
          for (;;)
          {
            return str;
            if ((paramAnonymousInt >= 1) && (paramAnonymousInt < 16)) {
              str = LocaleController.formatTTLString(paramAnonymousInt);
            } else if (paramAnonymousInt == 16) {
              str = LocaleController.formatTTLString(30);
            } else if (paramAnonymousInt == 17) {
              str = LocaleController.formatTTLString(60);
            } else if (paramAnonymousInt == 18) {
              str = LocaleController.formatTTLString(3600);
            } else if (paramAnonymousInt == 19) {
              str = LocaleController.formatTTLString(86400);
            } else if (paramAnonymousInt == 20) {
              str = LocaleController.formatTTLString(604800);
            } else {
              str = "";
            }
          }
        }
      });
      localBuilder.setView(paramContext);
      localBuilder.setNegativeButton(LocaleController.getString("Done", NUM), new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          paramAnonymousInt = this.val$encryptedChat.ttl;
          int i = paramContext.getValue();
          if ((i >= 0) && (i < 16)) {
            this.val$encryptedChat.ttl = i;
          }
          for (;;)
          {
            if (paramAnonymousInt != this.val$encryptedChat.ttl)
            {
              SecretChatHelper.getInstance(UserConfig.selectedAccount).sendTTLMessage(this.val$encryptedChat, null);
              MessagesStorage.getInstance(UserConfig.selectedAccount).updateEncryptedChatTTL(this.val$encryptedChat);
            }
            return;
            if (i == 16) {
              this.val$encryptedChat.ttl = 30;
            } else if (i == 17) {
              this.val$encryptedChat.ttl = 60;
            } else if (i == 18) {
              this.val$encryptedChat.ttl = 3600;
            } else if (i == 19) {
              this.val$encryptedChat.ttl = 86400;
            } else if (i == 20) {
              this.val$encryptedChat.ttl = 604800;
            }
          }
        }
      });
      return localBuilder;
      if (paramEncryptedChat.ttl == 30) {
        paramContext.setValue(16);
      } else if (paramEncryptedChat.ttl == 60) {
        paramContext.setValue(17);
      } else if (paramEncryptedChat.ttl == 3600) {
        paramContext.setValue(18);
      } else if (paramEncryptedChat.ttl == 86400) {
        paramContext.setValue(19);
      } else if (paramEncryptedChat.ttl == 604800) {
        paramContext.setValue(20);
      } else if (paramEncryptedChat.ttl == 0) {
        paramContext.setValue(0);
      }
    }
  }
  
  public static Dialog createVibrationSelectDialog(Activity paramActivity, final BaseFragment paramBaseFragment, final long paramLong, String paramString, final Runnable paramRunnable)
  {
    Object localObject = MessagesController.getNotificationsSettings(UserConfig.selectedAccount);
    int[] arrayOfInt = new int[1];
    LinearLayout localLinearLayout;
    int i;
    label140:
    RadioColorCell localRadioColorCell;
    String str;
    if (paramLong != 0L)
    {
      arrayOfInt[0] = ((SharedPreferences)localObject).getInt(paramString + paramLong, 0);
      if (arrayOfInt[0] == 3)
      {
        arrayOfInt[0] = 2;
        localObject = new String[4];
        localObject[0] = LocaleController.getString("VibrationDefault", NUM);
        localObject[1] = LocaleController.getString("Short", NUM);
        localObject[2] = LocaleController.getString("Long", NUM);
        localObject[3] = LocaleController.getString("VibrationDisabled", NUM);
        localLinearLayout = new LinearLayout(paramActivity);
        localLinearLayout.setOrientation(1);
        i = 0;
        if (i >= localObject.length) {
          break label417;
        }
        localRadioColorCell = new RadioColorCell(paramActivity);
        localRadioColorCell.setPadding(AndroidUtilities.dp(4.0F), 0, AndroidUtilities.dp(4.0F), 0);
        localRadioColorCell.setTag(Integer.valueOf(i));
        localRadioColorCell.setCheckColor(Theme.getColor("radioBackground"), Theme.getColor("dialogRadioBackgroundChecked"));
        str = localObject[i];
        if (arrayOfInt[0] != i) {
          break label411;
        }
      }
    }
    label411:
    for (boolean bool = true;; bool = false)
    {
      localRadioColorCell.setTextAndValue(str, bool);
      localLinearLayout.addView(localRadioColorCell);
      localRadioColorCell.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          this.val$selected[0] = ((Integer)paramAnonymousView.getTag()).intValue();
          paramAnonymousView = MessagesController.getNotificationsSettings(UserConfig.selectedAccount).edit();
          if (paramLong != 0L) {
            if (this.val$selected[0] == 0) {
              paramAnonymousView.putInt(paramBaseFragment + paramLong, 0);
            }
          }
          for (;;)
          {
            paramAnonymousView.commit();
            if (paramRunnable != null) {
              paramRunnable.dismissCurrentDialig();
            }
            if (this.val$onSelect != null) {
              this.val$onSelect.run();
            }
            return;
            if (this.val$selected[0] == 1)
            {
              paramAnonymousView.putInt(paramBaseFragment + paramLong, 1);
            }
            else if (this.val$selected[0] == 2)
            {
              paramAnonymousView.putInt(paramBaseFragment + paramLong, 3);
            }
            else if (this.val$selected[0] == 3)
            {
              paramAnonymousView.putInt(paramBaseFragment + paramLong, 2);
              continue;
              if (this.val$selected[0] == 0) {
                paramAnonymousView.putInt(paramBaseFragment, 2);
              } else if (this.val$selected[0] == 1) {
                paramAnonymousView.putInt(paramBaseFragment, 0);
              } else if (this.val$selected[0] == 2) {
                paramAnonymousView.putInt(paramBaseFragment, 1);
              } else if (this.val$selected[0] == 3) {
                paramAnonymousView.putInt(paramBaseFragment, 3);
              } else if (this.val$selected[0] == 4) {
                paramAnonymousView.putInt(paramBaseFragment, 4);
              }
            }
          }
        }
      });
      i++;
      break label140;
      if (arrayOfInt[0] != 2) {
        break;
      }
      arrayOfInt[0] = 3;
      break;
      arrayOfInt[0] = ((SharedPreferences)localObject).getInt(paramString, 0);
      if (arrayOfInt[0] == 0) {
        arrayOfInt[0] = 1;
      }
      for (;;)
      {
        localObject = new String[5];
        localObject[0] = LocaleController.getString("VibrationDisabled", NUM);
        localObject[1] = LocaleController.getString("VibrationDefault", NUM);
        localObject[2] = LocaleController.getString("Short", NUM);
        localObject[3] = LocaleController.getString("Long", NUM);
        localObject[4] = LocaleController.getString("OnlyIfSilent", NUM);
        break;
        if (arrayOfInt[0] == 1) {
          arrayOfInt[0] = 2;
        } else if (arrayOfInt[0] == 2) {
          arrayOfInt[0] = 0;
        }
      }
    }
    label417:
    paramActivity = new AlertDialog.Builder(paramActivity);
    paramActivity.setTitle(LocaleController.getString("Vibrate", NUM));
    paramActivity.setView(localLinearLayout);
    paramActivity.setPositiveButton(LocaleController.getString("Cancel", NUM), null);
    return paramActivity.create();
  }
  
  public static Dialog createVibrationSelectDialog(Activity paramActivity, BaseFragment paramBaseFragment, long paramLong, boolean paramBoolean1, boolean paramBoolean2, Runnable paramRunnable)
  {
    if (paramLong != 0L)
    {
      str = "vibrate_";
      return createVibrationSelectDialog(paramActivity, paramBaseFragment, paramLong, str, paramRunnable);
    }
    if (paramBoolean1) {}
    for (String str = "vibrate_group";; str = "vibrate_messages") {
      break;
    }
  }
  
  private static String getFloodWaitString(String paramString)
  {
    int i = Utilities.parseInt(paramString).intValue();
    if (i < 60) {}
    for (paramString = LocaleController.formatPluralString("Seconds", i);; paramString = LocaleController.formatPluralString("Minutes", i / 60)) {
      return LocaleController.formatString("FloodWaitTime", NUM, new Object[] { paramString });
    }
  }
  
  public static Dialog processError(int paramInt, TLRPC.TL_error paramTL_error, BaseFragment paramBaseFragment, TLObject paramTLObject, Object... paramVarArgs)
  {
    int i = 0;
    int j = 0;
    if ((paramTL_error.code == 406) || (paramTL_error.text == null))
    {
      paramTL_error = null;
      return paramTL_error;
    }
    if (((paramTLObject instanceof TLRPC.TL_channels_joinChannel)) || ((paramTLObject instanceof TLRPC.TL_channels_editAdmin)) || ((paramTLObject instanceof TLRPC.TL_channels_inviteToChannel)) || ((paramTLObject instanceof TLRPC.TL_messages_addChatUser)) || ((paramTLObject instanceof TLRPC.TL_messages_startBot)) || ((paramTLObject instanceof TLRPC.TL_channels_editBanned))) {
      if (paramBaseFragment != null) {
        showAddUserAlert(paramTL_error.text, paramBaseFragment, ((Boolean)paramVarArgs[0]).booleanValue());
      }
    }
    for (;;)
    {
      label91:
      paramTL_error = null;
      break;
      if (paramTL_error.text.equals("PEER_FLOOD"))
      {
        NotificationCenter.getInstance(paramInt).postNotificationName(NotificationCenter.needShowAlert, new Object[] { Integer.valueOf(1) });
        continue;
        if ((paramTLObject instanceof TLRPC.TL_messages_createChat))
        {
          if (paramTL_error.text.startsWith("FLOOD_WAIT")) {
            showFloodWaitAlert(paramTL_error.text, paramBaseFragment);
          } else {
            showAddUserAlert(paramTL_error.text, paramBaseFragment, false);
          }
        }
        else if ((paramTLObject instanceof TLRPC.TL_channels_createChannel))
        {
          if (paramTL_error.text.startsWith("FLOOD_WAIT")) {
            showFloodWaitAlert(paramTL_error.text, paramBaseFragment);
          } else {
            showAddUserAlert(paramTL_error.text, paramBaseFragment, false);
          }
        }
        else if ((paramTLObject instanceof TLRPC.TL_messages_editMessage))
        {
          if (!paramTL_error.text.equals("MESSAGE_NOT_MODIFIED")) {
            showSimpleAlert(paramBaseFragment, LocaleController.getString("EditMessageError", NUM));
          }
        }
        else if (((paramTLObject instanceof TLRPC.TL_messages_sendMessage)) || ((paramTLObject instanceof TLRPC.TL_messages_sendMedia)) || ((paramTLObject instanceof TLRPC.TL_messages_sendBroadcast)) || ((paramTLObject instanceof TLRPC.TL_messages_sendInlineBotResult)) || ((paramTLObject instanceof TLRPC.TL_messages_forwardMessages)))
        {
          if (paramTL_error.text.equals("PEER_FLOOD")) {
            NotificationCenter.getInstance(paramInt).postNotificationName(NotificationCenter.needShowAlert, new Object[] { Integer.valueOf(0) });
          }
        }
        else if ((paramTLObject instanceof TLRPC.TL_messages_importChatInvite))
        {
          if (paramTL_error.text.startsWith("FLOOD_WAIT")) {
            showSimpleAlert(paramBaseFragment, LocaleController.getString("FloodWait", NUM));
          } else if (paramTL_error.text.equals("USERS_TOO_MUCH")) {
            showSimpleAlert(paramBaseFragment, LocaleController.getString("JoinToGroupErrorFull", NUM));
          } else {
            showSimpleAlert(paramBaseFragment, LocaleController.getString("JoinToGroupErrorNotExist", NUM));
          }
        }
        else if ((paramTLObject instanceof TLRPC.TL_messages_getAttachedStickers))
        {
          if ((paramBaseFragment != null) && (paramBaseFragment.getParentActivity() != null)) {
            Toast.makeText(paramBaseFragment.getParentActivity(), LocaleController.getString("ErrorOccurred", NUM) + "\n" + paramTL_error.text, 0).show();
          }
        }
        else if ((paramTLObject instanceof TLRPC.TL_account_confirmPhone))
        {
          if ((paramTL_error.text.contains("PHONE_CODE_EMPTY")) || (paramTL_error.text.contains("PHONE_CODE_INVALID"))) {
            showSimpleAlert(paramBaseFragment, LocaleController.getString("InvalidCode", NUM));
          } else if (paramTL_error.text.contains("PHONE_CODE_EXPIRED")) {
            showSimpleAlert(paramBaseFragment, LocaleController.getString("CodeExpired", NUM));
          } else if (paramTL_error.text.startsWith("FLOOD_WAIT")) {
            showSimpleAlert(paramBaseFragment, LocaleController.getString("FloodWait", NUM));
          } else {
            showSimpleAlert(paramBaseFragment, paramTL_error.text);
          }
        }
        else if ((paramTLObject instanceof TLRPC.TL_auth_resendCode))
        {
          if (paramTL_error.text.contains("PHONE_NUMBER_INVALID")) {
            showSimpleAlert(paramBaseFragment, LocaleController.getString("InvalidPhoneNumber", NUM));
          } else if ((paramTL_error.text.contains("PHONE_CODE_EMPTY")) || (paramTL_error.text.contains("PHONE_CODE_INVALID"))) {
            showSimpleAlert(paramBaseFragment, LocaleController.getString("InvalidCode", NUM));
          } else if (paramTL_error.text.contains("PHONE_CODE_EXPIRED")) {
            showSimpleAlert(paramBaseFragment, LocaleController.getString("CodeExpired", NUM));
          } else if (paramTL_error.text.startsWith("FLOOD_WAIT")) {
            showSimpleAlert(paramBaseFragment, LocaleController.getString("FloodWait", NUM));
          } else if (paramTL_error.code != 64536) {
            showSimpleAlert(paramBaseFragment, LocaleController.getString("ErrorOccurred", NUM) + "\n" + paramTL_error.text);
          }
        }
        else
        {
          if ((paramTLObject instanceof TLRPC.TL_account_sendConfirmPhoneCode))
          {
            if (paramTL_error.code == 400)
            {
              paramTL_error = showSimpleAlert(paramBaseFragment, LocaleController.getString("CancelLinkExpired", NUM));
              break;
            }
            if (paramTL_error.text == null) {
              continue;
            }
            if (paramTL_error.text.startsWith("FLOOD_WAIT"))
            {
              paramTL_error = showSimpleAlert(paramBaseFragment, LocaleController.getString("FloodWait", NUM));
              break;
            }
            paramTL_error = showSimpleAlert(paramBaseFragment, LocaleController.getString("ErrorOccurred", NUM));
            break;
          }
          if ((paramTLObject instanceof TLRPC.TL_account_changePhone))
          {
            if (paramTL_error.text.contains("PHONE_NUMBER_INVALID")) {
              showSimpleAlert(paramBaseFragment, LocaleController.getString("InvalidPhoneNumber", NUM));
            } else if ((paramTL_error.text.contains("PHONE_CODE_EMPTY")) || (paramTL_error.text.contains("PHONE_CODE_INVALID"))) {
              showSimpleAlert(paramBaseFragment, LocaleController.getString("InvalidCode", NUM));
            } else if (paramTL_error.text.contains("PHONE_CODE_EXPIRED")) {
              showSimpleAlert(paramBaseFragment, LocaleController.getString("CodeExpired", NUM));
            } else if (paramTL_error.text.startsWith("FLOOD_WAIT")) {
              showSimpleAlert(paramBaseFragment, LocaleController.getString("FloodWait", NUM));
            } else {
              showSimpleAlert(paramBaseFragment, paramTL_error.text);
            }
          }
          else if ((paramTLObject instanceof TLRPC.TL_account_sendChangePhoneCode))
          {
            if (paramTL_error.text.contains("PHONE_NUMBER_INVALID")) {
              showSimpleAlert(paramBaseFragment, LocaleController.getString("InvalidPhoneNumber", NUM));
            } else if ((paramTL_error.text.contains("PHONE_CODE_EMPTY")) || (paramTL_error.text.contains("PHONE_CODE_INVALID"))) {
              showSimpleAlert(paramBaseFragment, LocaleController.getString("InvalidCode", NUM));
            } else if (paramTL_error.text.contains("PHONE_CODE_EXPIRED")) {
              showSimpleAlert(paramBaseFragment, LocaleController.getString("CodeExpired", NUM));
            } else if (paramTL_error.text.startsWith("FLOOD_WAIT")) {
              showSimpleAlert(paramBaseFragment, LocaleController.getString("FloodWait", NUM));
            } else if (paramTL_error.text.startsWith("PHONE_NUMBER_OCCUPIED")) {
              showSimpleAlert(paramBaseFragment, LocaleController.formatString("ChangePhoneNumberOccupied", NUM, new Object[] { (String)paramVarArgs[0] }));
            } else {
              showSimpleAlert(paramBaseFragment, LocaleController.getString("ErrorOccurred", NUM));
            }
          }
          else if ((paramTLObject instanceof TLRPC.TL_updateUserName))
          {
            paramTL_error = paramTL_error.text;
            switch (paramTL_error.hashCode())
            {
            default: 
              label1276:
              paramInt = -1;
            }
            for (;;)
            {
              switch (paramInt)
              {
              default: 
                showSimpleAlert(paramBaseFragment, LocaleController.getString("ErrorOccurred", NUM));
                break label91;
                if (!paramTL_error.equals("USERNAME_INVALID")) {
                  break label1276;
                }
                paramInt = j;
                continue;
                if (!paramTL_error.equals("USERNAME_OCCUPIED")) {
                  break label1276;
                }
                paramInt = 1;
              }
            }
            showSimpleAlert(paramBaseFragment, LocaleController.getString("UsernameInvalid", NUM));
            continue;
            showSimpleAlert(paramBaseFragment, LocaleController.getString("UsernameInUse", NUM));
          }
          else if ((paramTLObject instanceof TLRPC.TL_contacts_importContacts))
          {
            if ((paramTL_error == null) || (paramTL_error.text.startsWith("FLOOD_WAIT"))) {
              showSimpleAlert(paramBaseFragment, LocaleController.getString("FloodWait", NUM));
            } else {
              showSimpleAlert(paramBaseFragment, LocaleController.getString("ErrorOccurred", NUM) + "\n" + paramTL_error.text);
            }
          }
          else if (((paramTLObject instanceof TLRPC.TL_account_getPassword)) || ((paramTLObject instanceof TLRPC.TL_account_getTmpPassword)))
          {
            if (paramTL_error.text.startsWith("FLOOD_WAIT")) {
              showSimpleToast(paramBaseFragment, getFloodWaitString(paramTL_error.text));
            } else {
              showSimpleToast(paramBaseFragment, paramTL_error.text);
            }
          }
          else if ((paramTLObject instanceof TLRPC.TL_payments_sendPaymentForm))
          {
            paramTLObject = paramTL_error.text;
            switch (paramTLObject.hashCode())
            {
            default: 
              label1564:
              paramInt = -1;
            }
            for (;;)
            {
              switch (paramInt)
              {
              default: 
                showSimpleToast(paramBaseFragment, paramTL_error.text);
                break label91;
                if (!paramTLObject.equals("BOT_PRECHECKOUT_FAILED")) {
                  break label1564;
                }
                paramInt = i;
                continue;
                if (!paramTLObject.equals("PAYMENT_FAILED")) {
                  break label1564;
                }
                paramInt = 1;
              }
            }
            showSimpleToast(paramBaseFragment, LocaleController.getString("PaymentPrecheckoutFailed", NUM));
            continue;
            showSimpleToast(paramBaseFragment, LocaleController.getString("PaymentFailed", NUM));
          }
          else if ((paramTLObject instanceof TLRPC.TL_payments_validateRequestedInfo))
          {
            paramTLObject = paramTL_error.text;
            paramInt = -1;
            switch (paramTLObject.hashCode())
            {
            }
            for (;;)
            {
              switch (paramInt)
              {
              default: 
                showSimpleToast(paramBaseFragment, paramTL_error.text);
                break label91;
                if (paramTLObject.equals("SHIPPING_NOT_AVAILABLE")) {
                  paramInt = 0;
                }
                break;
              }
            }
            showSimpleToast(paramBaseFragment, LocaleController.getString("PaymentNoShippingMethod", NUM));
          }
        }
      }
    }
  }
  
  public static void showAddUserAlert(String paramString, BaseFragment paramBaseFragment, boolean paramBoolean)
  {
    if ((paramString == null) || (paramBaseFragment == null) || (paramBaseFragment.getParentActivity() == null)) {
      return;
    }
    AlertDialog.Builder localBuilder = new AlertDialog.Builder(paramBaseFragment.getParentActivity());
    localBuilder.setTitle(LocaleController.getString("AppName", NUM));
    int i = -1;
    switch (paramString.hashCode())
    {
    default: 
      switch (i)
      {
      default: 
        label188:
        localBuilder.setMessage(LocaleController.getString("ErrorOccurred", NUM) + "\n" + paramString);
      }
      break;
    }
    for (;;)
    {
      localBuilder.setPositiveButton(LocaleController.getString("OK", NUM), null);
      paramBaseFragment.showDialog(localBuilder.create(), true, null);
      break;
      if (!paramString.equals("PEER_FLOOD")) {
        break label188;
      }
      i = 0;
      break label188;
      if (!paramString.equals("USER_BLOCKED")) {
        break label188;
      }
      i = 1;
      break label188;
      if (!paramString.equals("USER_BOT")) {
        break label188;
      }
      i = 2;
      break label188;
      if (!paramString.equals("USER_ID_INVALID")) {
        break label188;
      }
      i = 3;
      break label188;
      if (!paramString.equals("USERS_TOO_MUCH")) {
        break label188;
      }
      i = 4;
      break label188;
      if (!paramString.equals("USER_NOT_MUTUAL_CONTACT")) {
        break label188;
      }
      i = 5;
      break label188;
      if (!paramString.equals("ADMINS_TOO_MUCH")) {
        break label188;
      }
      i = 6;
      break label188;
      if (!paramString.equals("BOTS_TOO_MUCH")) {
        break label188;
      }
      i = 7;
      break label188;
      if (!paramString.equals("USER_PRIVACY_RESTRICTED")) {
        break label188;
      }
      i = 8;
      break label188;
      if (!paramString.equals("USERS_TOO_FEW")) {
        break label188;
      }
      i = 9;
      break label188;
      if (!paramString.equals("USER_RESTRICTED")) {
        break label188;
      }
      i = 10;
      break label188;
      if (!paramString.equals("YOU_BLOCKED_USER")) {
        break label188;
      }
      i = 11;
      break label188;
      if (!paramString.equals("CHAT_ADMIN_BAN_REQUIRED")) {
        break label188;
      }
      i = 12;
      break label188;
      if (!paramString.equals("USER_KICKED")) {
        break label188;
      }
      i = 13;
      break label188;
      if (!paramString.equals("CHAT_ADMIN_INVITE_REQUIRED")) {
        break label188;
      }
      i = 14;
      break label188;
      if (!paramString.equals("USER_ADMIN_INVALID")) {
        break label188;
      }
      i = 15;
      break label188;
      localBuilder.setMessage(LocaleController.getString("NobodyLikesSpam2", NUM));
      localBuilder.setNegativeButton(LocaleController.getString("MoreInfo", NUM), new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          MessagesController.getInstance(this.val$fragment.getCurrentAccount()).openByUserName("spambot", this.val$fragment, 1);
        }
      });
      continue;
      if (paramBoolean)
      {
        localBuilder.setMessage(LocaleController.getString("ChannelUserCantAdd", NUM));
      }
      else
      {
        localBuilder.setMessage(LocaleController.getString("GroupUserCantAdd", NUM));
        continue;
        if (paramBoolean)
        {
          localBuilder.setMessage(LocaleController.getString("ChannelUserAddLimit", NUM));
        }
        else
        {
          localBuilder.setMessage(LocaleController.getString("GroupUserAddLimit", NUM));
          continue;
          if (paramBoolean)
          {
            localBuilder.setMessage(LocaleController.getString("ChannelUserLeftError", NUM));
          }
          else
          {
            localBuilder.setMessage(LocaleController.getString("GroupUserLeftError", NUM));
            continue;
            if (paramBoolean)
            {
              localBuilder.setMessage(LocaleController.getString("ChannelUserCantAdmin", NUM));
            }
            else
            {
              localBuilder.setMessage(LocaleController.getString("GroupUserCantAdmin", NUM));
              continue;
              if (paramBoolean)
              {
                localBuilder.setMessage(LocaleController.getString("ChannelUserCantBot", NUM));
              }
              else
              {
                localBuilder.setMessage(LocaleController.getString("GroupUserCantBot", NUM));
                continue;
                if (paramBoolean)
                {
                  localBuilder.setMessage(LocaleController.getString("InviteToChannelError", NUM));
                }
                else
                {
                  localBuilder.setMessage(LocaleController.getString("InviteToGroupError", NUM));
                  continue;
                  localBuilder.setMessage(LocaleController.getString("CreateGroupError", NUM));
                  continue;
                  localBuilder.setMessage(LocaleController.getString("UserRestricted", NUM));
                  continue;
                  localBuilder.setMessage(LocaleController.getString("YouBlockedUser", NUM));
                  continue;
                  localBuilder.setMessage(LocaleController.getString("AddAdminErrorBlacklisted", NUM));
                  continue;
                  localBuilder.setMessage(LocaleController.getString("AddAdminErrorNotAMember", NUM));
                  continue;
                  localBuilder.setMessage(LocaleController.getString("AddBannedErrorAdmin", NUM));
                }
              }
            }
          }
        }
      }
    }
  }
  
  public static void showFloodWaitAlert(String paramString, BaseFragment paramBaseFragment)
  {
    if ((paramString == null) || (!paramString.startsWith("FLOOD_WAIT")) || (paramBaseFragment == null) || (paramBaseFragment.getParentActivity() == null)) {
      return;
    }
    int i = Utilities.parseInt(paramString).intValue();
    if (i < 60) {}
    for (paramString = LocaleController.formatPluralString("Seconds", i);; paramString = LocaleController.formatPluralString("Minutes", i / 60))
    {
      AlertDialog.Builder localBuilder = new AlertDialog.Builder(paramBaseFragment.getParentActivity());
      localBuilder.setTitle(LocaleController.getString("AppName", NUM));
      localBuilder.setMessage(LocaleController.formatString("FloodWaitTime", NUM, new Object[] { paramString }));
      localBuilder.setPositiveButton(LocaleController.getString("OK", NUM), null);
      paramBaseFragment.showDialog(localBuilder.create(), true, null);
      break;
    }
  }
  
  public static void showSendMediaAlert(int paramInt, BaseFragment paramBaseFragment)
  {
    if (paramInt == 0) {
      return;
    }
    AlertDialog.Builder localBuilder = new AlertDialog.Builder(paramBaseFragment.getParentActivity());
    localBuilder.setTitle(LocaleController.getString("AppName", NUM));
    if (paramInt == 1) {
      localBuilder.setMessage(LocaleController.getString("ErrorSendRestrictedStickers", NUM));
    }
    for (;;)
    {
      localBuilder.setPositiveButton(LocaleController.getString("OK", NUM), null);
      paramBaseFragment.showDialog(localBuilder.create(), true, null);
      break;
      if (paramInt == 2) {
        localBuilder.setMessage(LocaleController.getString("ErrorSendRestrictedMedia", NUM));
      }
    }
  }
  
  public static Dialog showSimpleAlert(BaseFragment paramBaseFragment, String paramString)
  {
    Object localObject1 = null;
    Object localObject2 = localObject1;
    if (paramString != null)
    {
      localObject2 = localObject1;
      if (paramBaseFragment != null)
      {
        if (paramBaseFragment.getParentActivity() != null) {
          break label25;
        }
        localObject2 = localObject1;
      }
    }
    for (;;)
    {
      return (Dialog)localObject2;
      label25:
      localObject2 = new AlertDialog.Builder(paramBaseFragment.getParentActivity());
      ((AlertDialog.Builder)localObject2).setTitle(LocaleController.getString("AppName", NUM));
      ((AlertDialog.Builder)localObject2).setMessage(paramString);
      ((AlertDialog.Builder)localObject2).setPositiveButton(LocaleController.getString("OK", NUM), null);
      localObject2 = ((AlertDialog.Builder)localObject2).create();
      paramBaseFragment.showDialog((Dialog)localObject2);
    }
  }
  
  public static Toast showSimpleToast(BaseFragment paramBaseFragment, String paramString)
  {
    if ((paramString == null) || (paramBaseFragment == null) || (paramBaseFragment.getParentActivity() == null)) {
      paramBaseFragment = null;
    }
    for (;;)
    {
      return paramBaseFragment;
      paramBaseFragment = Toast.makeText(paramBaseFragment.getParentActivity(), paramString, 1);
      paramBaseFragment.show();
    }
  }
  
  public static abstract interface PaymentAlertDelegate
  {
    public abstract void didPressedNewCard();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/AlertsCreator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */