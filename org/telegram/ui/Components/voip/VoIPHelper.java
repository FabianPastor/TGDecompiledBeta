package org.telegram.ui.Components.voip;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Build.VERSION;
import android.provider.Settings.System;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputPhoneCall;
import org.telegram.tgnet.TLRPC.TL_messageActionPhoneCall;
import org.telegram.tgnet.TLRPC.TL_phoneCallDiscardReasonBusy;
import org.telegram.tgnet.TLRPC.TL_phoneCallDiscardReasonMissed;
import org.telegram.tgnet.TLRPC.TL_phone_setCallRating;
import org.telegram.tgnet.TLRPC.TL_updates;
import org.telegram.tgnet.TLRPC.TL_userFull;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.Components.BetterRatingView;
import org.telegram.ui.Components.BetterRatingView.OnRatingChangeListener;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.VoIPActivity;

public class VoIPHelper
{
  private static final int VOIP_SUPPORT_ID = 4244000;
  public static long lastCallTime = 0L;
  
  public static boolean canRateCall(TLRPC.TL_messageActionPhoneCall paramTL_messageActionPhoneCall)
  {
    boolean bool1 = false;
    boolean bool2 = bool1;
    if (!(paramTL_messageActionPhoneCall.reason instanceof TLRPC.TL_phoneCallDiscardReasonBusy))
    {
      bool2 = bool1;
      if (!(paramTL_messageActionPhoneCall.reason instanceof TLRPC.TL_phoneCallDiscardReasonMissed))
      {
        Iterator localIterator = MessagesController.getNotificationsSettings(UserConfig.selectedAccount).getStringSet("calls_access_hashes", Collections.EMPTY_SET).iterator();
        String[] arrayOfString;
        do
        {
          bool2 = bool1;
          if (!localIterator.hasNext()) {
            break;
          }
          arrayOfString = ((String)localIterator.next()).split(" ");
        } while ((arrayOfString.length < 2) || (!arrayOfString[0].equals(paramTL_messageActionPhoneCall.call_id + "")));
        bool2 = true;
      }
    }
    return bool2;
  }
  
  private static void doInitiateCall(TLRPC.User paramUser, Activity paramActivity)
  {
    if ((paramActivity == null) || (paramUser == null)) {}
    for (;;)
    {
      return;
      if (System.currentTimeMillis() - lastCallTime >= 2000L)
      {
        lastCallTime = System.currentTimeMillis();
        Intent localIntent = new Intent(paramActivity, VoIPService.class);
        localIntent.putExtra("user_id", paramUser.id);
        localIntent.putExtra("is_outgoing", true);
        localIntent.putExtra("start_incall_activity", true);
        localIntent.putExtra("account", UserConfig.selectedAccount);
        try
        {
          paramActivity.startService(localIntent);
        }
        catch (Throwable paramUser)
        {
          FileLog.e(paramUser);
        }
      }
    }
  }
  
  public static File getLogsDir()
  {
    File localFile = new File(ApplicationLoader.applicationContext.getCacheDir(), "voip_logs");
    if (!localFile.exists()) {
      localFile.mkdirs();
    }
    return localFile;
  }
  
  private static void initiateCall(TLRPC.User paramUser, final Activity paramActivity)
  {
    if ((paramActivity == null) || (paramUser == null)) {}
    for (;;)
    {
      return;
      if (VoIPService.getSharedInstance() != null)
      {
        TLRPC.User localUser = VoIPService.getSharedInstance().getUser();
        if (localUser.id != paramUser.id) {
          new AlertDialog.Builder(paramActivity).setTitle(LocaleController.getString("VoipOngoingAlertTitle", NUM)).setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("VoipOngoingAlert", NUM, new Object[] { ContactsController.formatName(localUser.first_name, localUser.last_name), ContactsController.formatName(paramUser.first_name, paramUser.last_name) }))).setPositiveButton(LocaleController.getString("OK", NUM), new DialogInterface.OnClickListener()
          {
            public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
            {
              if (VoIPService.getSharedInstance() != null) {
                VoIPService.getSharedInstance().hangUp(new Runnable()
                {
                  public void run()
                  {
                    VoIPHelper.doInitiateCall(VoIPHelper.2.this.val$user, VoIPHelper.2.this.val$activity);
                  }
                });
              }
              for (;;)
              {
                return;
                VoIPHelper.doInitiateCall(this.val$user, paramActivity);
              }
            }
          }).setNegativeButton(LocaleController.getString("Cancel", NUM), null).show();
        } else {
          paramActivity.startActivity(new Intent(paramActivity, VoIPActivity.class).addFlags(268435456));
        }
      }
      else if (VoIPService.callIShouldHavePutIntoIntent == null)
      {
        doInitiateCall(paramUser, paramActivity);
      }
    }
  }
  
  @TargetApi(23)
  public static void permissionDenied(Activity paramActivity, Runnable paramRunnable)
  {
    if (!paramActivity.shouldShowRequestPermissionRationale("android.permission.RECORD_AUDIO")) {
      new AlertDialog.Builder(paramActivity).setTitle(LocaleController.getString("AppName", NUM)).setMessage(LocaleController.getString("VoipNeedMicPermission", NUM)).setPositiveButton(LocaleController.getString("OK", NUM), null).setNegativeButton(LocaleController.getString("Settings", NUM), new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          paramAnonymousDialogInterface = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
          paramAnonymousDialogInterface.setData(Uri.fromParts("package", this.val$activity.getPackageName(), null));
          this.val$activity.startActivity(paramAnonymousDialogInterface);
        }
      }).show().setOnDismissListener(new DialogInterface.OnDismissListener()
      {
        public void onDismiss(DialogInterface paramAnonymousDialogInterface)
        {
          if (this.val$onFinish != null) {
            this.val$onFinish.run();
          }
        }
      });
    }
  }
  
  public static void showRateAlert(final Context paramContext, Runnable paramRunnable, long paramLong1, final long paramLong2, final int paramInt)
  {
    final File localFile = new File(getLogsDir(), paramLong1 + ".log");
    LinearLayout localLinearLayout = new LinearLayout(paramContext);
    localLinearLayout.setOrientation(1);
    int i = AndroidUtilities.dp(16.0F);
    localLinearLayout.setPadding(i, i, i, 0);
    final Object localObject = new TextView(paramContext);
    ((TextView)localObject).setTextSize(2, 16.0F);
    ((TextView)localObject).setTextColor(Theme.getColor("dialogTextBlack"));
    ((TextView)localObject).setGravity(17);
    ((TextView)localObject).setText(LocaleController.getString("VoipRateCallAlert", NUM));
    localLinearLayout.addView((View)localObject);
    BetterRatingView localBetterRatingView = new BetterRatingView(paramContext);
    localLinearLayout.addView(localBetterRatingView, LayoutHelper.createLinear(-2, -2, 1, 0, 16, 0, 0));
    localObject = new EditText(paramContext);
    ((EditText)localObject).setHint(LocaleController.getString("CallReportHint", NUM));
    ((EditText)localObject).setInputType(147457);
    ((EditText)localObject).setTextColor(Theme.getColor("dialogTextBlack"));
    ((EditText)localObject).setHintTextColor(Theme.getColor("dialogTextHint"));
    ((EditText)localObject).setBackgroundDrawable(Theme.createEditTextDrawable(paramContext, true));
    ((EditText)localObject).setPadding(0, AndroidUtilities.dp(4.0F), 0, AndroidUtilities.dp(4.0F));
    ((EditText)localObject).setTextSize(18.0F);
    ((EditText)localObject).setVisibility(8);
    localLinearLayout.addView((View)localObject, LayoutHelper.createLinear(-1, -2, 8.0F, 8.0F, 8.0F, 0.0F));
    boolean[] arrayOfBoolean = new boolean[1];
    arrayOfBoolean[0] = true;
    final CheckBoxCell localCheckBoxCell = new CheckBoxCell(paramContext, 1);
    View.OnClickListener local5 = new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        paramAnonymousView = this.val$includeLogs;
        if (this.val$includeLogs[0] == 0) {}
        for (int i = 1;; i = 0)
        {
          paramAnonymousView[0] = i;
          localCheckBoxCell.setChecked(this.val$includeLogs[0], true);
          return;
        }
      }
    };
    localCheckBoxCell.setText(LocaleController.getString("CallReportIncludeLogs", NUM), null, true, false);
    localCheckBoxCell.setClipToPadding(false);
    localCheckBoxCell.setOnClickListener(local5);
    localLinearLayout.addView(localCheckBoxCell, LayoutHelper.createLinear(-1, -2, -8.0F, 0.0F, -8.0F, 0.0F));
    final TextView localTextView = new TextView(paramContext);
    localTextView.setTextSize(2, 14.0F);
    localTextView.setTextColor(Theme.getColor("dialogTextGray3"));
    localTextView.setText(LocaleController.getString("CallReportLogsExplain", NUM));
    localTextView.setPadding(AndroidUtilities.dp(8.0F), 0, AndroidUtilities.dp(8.0F), 0);
    localTextView.setOnClickListener(local5);
    localLinearLayout.addView(localTextView);
    localCheckBoxCell.setVisibility(8);
    localTextView.setVisibility(8);
    if (!localFile.exists()) {
      arrayOfBoolean[0] = false;
    }
    paramRunnable = new AlertDialog.Builder(paramContext).setTitle(LocaleController.getString("CallMessageReportProblem", NUM)).setView(localLinearLayout).setPositiveButton(LocaleController.getString("Send", NUM), new DialogInterface.OnClickListener()
    {
      public void onClick(final DialogInterface paramAnonymousDialogInterface, final int paramAnonymousInt)
      {
        paramAnonymousInt = UserConfig.selectedAccount;
        paramAnonymousDialogInterface = new TLRPC.TL_phone_setCallRating();
        paramAnonymousDialogInterface.rating = this.val$bar.getRating();
        if (paramAnonymousDialogInterface.rating < 5) {}
        for (paramAnonymousDialogInterface.comment = localObject.getText().toString();; paramAnonymousDialogInterface.comment = "")
        {
          paramAnonymousDialogInterface.peer = new TLRPC.TL_inputPhoneCall();
          paramAnonymousDialogInterface.peer.access_hash = paramLong2;
          paramAnonymousDialogInterface.peer.id = paramInt;
          ConnectionsManager.getInstance(localFile).sendRequest(paramAnonymousDialogInterface, new RequestDelegate()
          {
            public void run(TLObject paramAnonymous2TLObject, TLRPC.TL_error paramAnonymous2TL_error)
            {
              if ((paramAnonymous2TLObject instanceof TLRPC.TL_updates))
              {
                paramAnonymous2TLObject = (TLRPC.TL_updates)paramAnonymous2TLObject;
                MessagesController.getInstance(paramAnonymousInt).processUpdates(paramAnonymous2TLObject, false);
                if ((VoIPHelper.7.this.val$includeLogs[0] != 0) && (VoIPHelper.7.this.val$log.exists()) && (paramAnonymousDialogInterface.rating < 4))
                {
                  SendMessagesHelper.prepareSendingDocument(VoIPHelper.7.this.val$log.getAbsolutePath(), VoIPHelper.7.this.val$log.getAbsolutePath(), null, "text/plain", 4244000L, null, null);
                  Toast.makeText(VoIPHelper.7.this.val$context, LocaleController.getString("CallReportSent", NUM), 1).show();
                }
              }
            }
          });
          return;
        }
      }
    }).setNegativeButton(LocaleController.getString("Cancel", NUM), null).setOnDismissListener(new DialogInterface.OnDismissListener()
    {
      public void onDismiss(DialogInterface paramAnonymousDialogInterface)
      {
        if (this.val$onDismiss != null) {
          this.val$onDismiss.run();
        }
      }
    }).show().getButton(-1);
    paramRunnable.setEnabled(false);
    localBetterRatingView.setOnRatingChangeListener(new BetterRatingView.OnRatingChangeListener()
    {
      public void onRatingChanged(int paramAnonymousInt)
      {
        int i = 0;
        Object localObject = this.val$btn;
        boolean bool;
        label39:
        int j;
        if (paramAnonymousInt > 0)
        {
          bool = true;
          ((View)localObject).setEnabled(bool);
          EditText localEditText = localObject;
          if (paramAnonymousInt >= 4) {
            break label157;
          }
          localObject = LocaleController.getString("CallReportHint", NUM);
          localEditText.setHint((CharSequence)localObject);
          localObject = localObject;
          if ((paramAnonymousInt >= 5) || (paramAnonymousInt <= 0)) {
            break label168;
          }
          j = 0;
          label62:
          ((EditText)localObject).setVisibility(j);
          if (localObject.getVisibility() == 8) {
            ((InputMethodManager)paramContext.getSystemService("input_method")).hideSoftInputFromWindow(localObject.getWindowToken(), 0);
          }
          if (localFile.exists())
          {
            localObject = localCheckBoxCell;
            if (paramAnonymousInt >= 4) {
              break label175;
            }
            j = 0;
            label127:
            ((CheckBoxCell)localObject).setVisibility(j);
            localObject = localTextView;
            if (paramAnonymousInt >= 4) {
              break label182;
            }
          }
        }
        label157:
        label168:
        label175:
        label182:
        for (paramAnonymousInt = i;; paramAnonymousInt = 8)
        {
          ((TextView)localObject).setVisibility(paramAnonymousInt);
          return;
          bool = false;
          break;
          localObject = LocaleController.getString("VoipFeedbackCommentHint", NUM);
          break label39;
          j = 8;
          break label62;
          j = 8;
          break label127;
        }
      }
    });
  }
  
  public static void showRateAlert(Context paramContext, TLRPC.TL_messageActionPhoneCall paramTL_messageActionPhoneCall)
  {
    Iterator localIterator = MessagesController.getNotificationsSettings(UserConfig.selectedAccount).getStringSet("calls_access_hashes", Collections.EMPTY_SET).iterator();
    String[] arrayOfString;
    do
    {
      if (!localIterator.hasNext()) {
        break;
      }
      arrayOfString = ((String)localIterator.next()).split(" ");
    } while ((arrayOfString.length < 2) || (!arrayOfString[0].equals(paramTL_messageActionPhoneCall.call_id + "")));
    try
    {
      long l = Long.parseLong(arrayOfString[1]);
      showRateAlert(paramContext, null, paramTL_messageActionPhoneCall.call_id, l, UserConfig.selectedAccount);
      return;
    }
    catch (Exception paramContext)
    {
      for (;;) {}
    }
  }
  
  public static void startCall(TLRPC.User paramUser, Activity paramActivity, final TLRPC.TL_userFull paramTL_userFull)
  {
    int i = 1;
    if ((paramTL_userFull != null) && (paramTL_userFull.phone_calls_private)) {
      new AlertDialog.Builder(paramActivity).setTitle(LocaleController.getString("VoipFailed", NUM)).setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("CallNotAvailable", NUM, new Object[] { ContactsController.formatName(paramUser.first_name, paramUser.last_name) }))).setPositiveButton(LocaleController.getString("OK", NUM), null).show();
    }
    for (;;)
    {
      return;
      if (ConnectionsManager.getInstance(UserConfig.selectedAccount).getConnectionState() != 3)
      {
        if (Settings.System.getInt(paramActivity.getContentResolver(), "airplane_mode_on", 0) != 0)
        {
          label111:
          paramTL_userFull = new AlertDialog.Builder(paramActivity);
          if (i == 0) {
            break label235;
          }
          paramUser = LocaleController.getString("VoipOfflineAirplaneTitle", NUM);
          label134:
          paramTL_userFull = paramTL_userFull.setTitle(paramUser);
          if (i == 0) {
            break label248;
          }
        }
        label235:
        label248:
        for (paramUser = LocaleController.getString("VoipOfflineAirplane", NUM);; paramUser = LocaleController.getString("VoipOffline", NUM))
        {
          paramUser = paramTL_userFull.setMessage(paramUser).setPositiveButton(LocaleController.getString("OK", NUM), null);
          if (i != 0)
          {
            paramTL_userFull = new Intent("android.settings.AIRPLANE_MODE_SETTINGS");
            if (paramTL_userFull.resolveActivity(paramActivity.getPackageManager()) != null) {
              paramUser.setNeutralButton(LocaleController.getString("VoipOfflineOpenSettings", NUM), new DialogInterface.OnClickListener()
              {
                public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
                {
                  this.val$activity.startActivity(paramTL_userFull);
                }
              });
            }
          }
          paramUser.show();
          break;
          i = 0;
          break label111;
          paramUser = LocaleController.getString("VoipOfflineTitle", NUM);
          break label134;
        }
      }
      if ((Build.VERSION.SDK_INT >= 23) && (paramActivity.checkSelfPermission("android.permission.RECORD_AUDIO") != 0)) {
        paramActivity.requestPermissions(new String[] { "android.permission.RECORD_AUDIO" }, 101);
      } else {
        initiateCall(paramUser, paramActivity);
      }
    }
  }
  
  public static void upgradeP2pSetting(int paramInt)
  {
    SharedPreferences localSharedPreferences = MessagesController.getMainSettings(paramInt);
    if (localSharedPreferences.contains("calls_p2p"))
    {
      SharedPreferences.Editor localEditor = localSharedPreferences.edit();
      if (!localSharedPreferences.getBoolean("calls_p2p", true)) {
        localEditor.putInt("calls_p2p_new", 2);
      }
      localEditor.remove("calls_p2p").commit();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/voip/VoIPHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */