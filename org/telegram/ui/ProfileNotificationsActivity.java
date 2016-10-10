package org.telegram.ui;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.provider.Settings.System;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView<*>;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;
import java.util.concurrent.ConcurrentHashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.NotificationsController;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC.TL_dialog;
import org.telegram.tgnet.TLRPC.TL_peerNotifySettings;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Adapters.BaseFragmentAdapter;
import org.telegram.ui.Cells.TextColorCell;
import org.telegram.ui.Cells.TextDetailSettingsCell;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.ColorPickerView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.NumberPicker;

public class ProfileNotificationsActivity
  extends BaseFragment
  implements NotificationCenter.NotificationCenterDelegate
{
  private long dialog_id;
  private ListView listView;
  private int rowCount = 0;
  private int settingsLedRow;
  private int settingsNotificationsRow;
  private int settingsPriorityRow;
  private int settingsSoundRow;
  private int settingsVibrateRow;
  private int smartRow;
  
  public ProfileNotificationsActivity(Bundle paramBundle)
  {
    super(paramBundle);
    this.dialog_id = paramBundle.getLong("dialog_id");
  }
  
  public View createView(Context paramContext)
  {
    this.actionBar.setBackButtonImage(2130837700);
    this.actionBar.setAllowOverlayTitle(true);
    this.actionBar.setTitle(LocaleController.getString("NotificationsAndSounds", 2131166031));
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramAnonymousInt)
      {
        if (paramAnonymousInt == -1) {
          ProfileNotificationsActivity.this.finishFragment();
        }
      }
    });
    this.fragmentView = new FrameLayout(paramContext);
    Object localObject = (FrameLayout)this.fragmentView;
    this.listView = new ListView(paramContext);
    this.listView.setDivider(null);
    this.listView.setDividerHeight(0);
    this.listView.setVerticalScrollBarEnabled(false);
    AndroidUtilities.setListViewEdgeEffectColor(this.listView, AvatarDrawable.getProfileBackColorForId(5));
    ((FrameLayout)localObject).addView(this.listView);
    localObject = (FrameLayout.LayoutParams)this.listView.getLayoutParams();
    ((FrameLayout.LayoutParams)localObject).width = -1;
    ((FrameLayout.LayoutParams)localObject).height = -1;
    this.listView.setLayoutParams((ViewGroup.LayoutParams)localObject);
    this.listView.setAdapter(new ListAdapter(paramContext));
    this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
    {
      public void onItemClick(AdapterView<?> paramAnonymousAdapterView, final View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
      {
        Object localObject4;
        if (paramAnonymousInt == ProfileNotificationsActivity.this.settingsVibrateRow)
        {
          paramAnonymousAdapterView = new AlertDialog.Builder(ProfileNotificationsActivity.this.getParentActivity());
          paramAnonymousAdapterView.setTitle(LocaleController.getString("Vibrate", 2131166382));
          paramAnonymousView = LocaleController.getString("VibrationDisabled", 2131166384);
          localObject1 = LocaleController.getString("SettingsDefault", 2131166271);
          localObject2 = LocaleController.getString("SystemDefault", 2131166323);
          localObject3 = LocaleController.getString("Short", 2131166288);
          localObject4 = LocaleController.getString("Long", 2131165847);
          DialogInterface.OnClickListener local1 = new DialogInterface.OnClickListener()
          {
            public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
            {
              paramAnonymous2DialogInterface = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
              if (paramAnonymous2Int == 0) {
                paramAnonymous2DialogInterface.putInt("vibrate_" + ProfileNotificationsActivity.this.dialog_id, 2);
              }
              for (;;)
              {
                paramAnonymous2DialogInterface.commit();
                if (ProfileNotificationsActivity.this.listView != null) {
                  ProfileNotificationsActivity.this.listView.invalidateViews();
                }
                return;
                if (paramAnonymous2Int == 1) {
                  paramAnonymous2DialogInterface.putInt("vibrate_" + ProfileNotificationsActivity.this.dialog_id, 0);
                } else if (paramAnonymous2Int == 2) {
                  paramAnonymous2DialogInterface.putInt("vibrate_" + ProfileNotificationsActivity.this.dialog_id, 4);
                } else if (paramAnonymous2Int == 3) {
                  paramAnonymous2DialogInterface.putInt("vibrate_" + ProfileNotificationsActivity.this.dialog_id, 1);
                } else if (paramAnonymous2Int == 4) {
                  paramAnonymous2DialogInterface.putInt("vibrate_" + ProfileNotificationsActivity.this.dialog_id, 3);
                }
              }
            }
          };
          paramAnonymousAdapterView.setItems(new CharSequence[] { paramAnonymousView, localObject1, localObject2, localObject3, localObject4 }, local1);
          paramAnonymousAdapterView.setNegativeButton(LocaleController.getString("Cancel", 2131165386), null);
          ProfileNotificationsActivity.this.showDialog(paramAnonymousAdapterView.create());
        }
        label467:
        do
        {
          do
          {
            do
            {
              return;
              if (paramAnonymousInt != ProfileNotificationsActivity.this.settingsNotificationsRow) {
                break;
              }
            } while (ProfileNotificationsActivity.this.getParentActivity() == null);
            paramAnonymousAdapterView = new AlertDialog.Builder(ProfileNotificationsActivity.this.getParentActivity());
            paramAnonymousAdapterView.setTitle(LocaleController.getString("AppName", 2131165299));
            paramAnonymousView = LocaleController.getString("Default", 2131165559);
            localObject1 = LocaleController.getString("Enabled", 2131165602);
            localObject2 = LocaleController.getString("NotificationsDisabled", 2131166032);
            localObject3 = new DialogInterface.OnClickListener()
            {
              public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
              {
                paramAnonymous2DialogInterface = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
                paramAnonymous2DialogInterface.putInt("notify2_" + ProfileNotificationsActivity.this.dialog_id, paramAnonymous2Int);
                if (paramAnonymous2Int == 2) {
                  NotificationsController.getInstance().removeNotificationsForDialog(ProfileNotificationsActivity.this.dialog_id);
                }
                MessagesStorage localMessagesStorage = MessagesStorage.getInstance();
                long l2 = ProfileNotificationsActivity.this.dialog_id;
                if (paramAnonymous2Int == 2) {}
                for (long l1 = 1L;; l1 = 0L)
                {
                  localMessagesStorage.setDialogFlags(l2, l1);
                  paramAnonymous2DialogInterface.commit();
                  paramAnonymous2DialogInterface = (TLRPC.TL_dialog)MessagesController.getInstance().dialogs_dict.get(Long.valueOf(ProfileNotificationsActivity.this.dialog_id));
                  if (paramAnonymous2DialogInterface != null)
                  {
                    paramAnonymous2DialogInterface.notify_settings = new TLRPC.TL_peerNotifySettings();
                    if (paramAnonymous2Int == 2) {
                      paramAnonymous2DialogInterface.notify_settings.mute_until = Integer.MAX_VALUE;
                    }
                  }
                  if (ProfileNotificationsActivity.this.listView != null) {
                    ProfileNotificationsActivity.this.listView.invalidateViews();
                  }
                  NotificationsController.updateServerNotificationsSettings(ProfileNotificationsActivity.this.dialog_id);
                  return;
                }
              }
            };
            paramAnonymousAdapterView.setItems(new CharSequence[] { paramAnonymousView, localObject1, localObject2 }, (DialogInterface.OnClickListener)localObject3);
            paramAnonymousAdapterView.setNegativeButton(LocaleController.getString("Cancel", 2131165386), null);
            ProfileNotificationsActivity.this.showDialog(paramAnonymousAdapterView.create());
            return;
            if (paramAnonymousInt == ProfileNotificationsActivity.this.settingsSoundRow) {
              for (;;)
              {
                try
                {
                  localObject3 = new Intent("android.intent.action.RINGTONE_PICKER");
                  ((Intent)localObject3).putExtra("android.intent.extra.ringtone.TYPE", 2);
                  ((Intent)localObject3).putExtra("android.intent.extra.ringtone.SHOW_DEFAULT", true);
                  ((Intent)localObject3).putExtra("android.intent.extra.ringtone.DEFAULT_URI", RingtoneManager.getDefaultUri(2));
                  paramAnonymousAdapterView = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
                  localObject1 = null;
                  paramAnonymousView = null;
                  localObject2 = Settings.System.DEFAULT_NOTIFICATION_URI;
                  if (localObject2 != null) {
                    paramAnonymousView = ((Uri)localObject2).getPath();
                  }
                  localObject4 = paramAnonymousAdapterView.getString("sound_path_" + ProfileNotificationsActivity.this.dialog_id, paramAnonymousView);
                  paramAnonymousAdapterView = (AdapterView<?>)localObject1;
                  if (localObject4 != null)
                  {
                    paramAnonymousAdapterView = (AdapterView<?>)localObject1;
                    if (!((String)localObject4).equals("NoSound"))
                    {
                      if (!((String)localObject4).equals(paramAnonymousView)) {
                        break label467;
                      }
                      paramAnonymousAdapterView = (AdapterView<?>)localObject2;
                    }
                  }
                  ((Intent)localObject3).putExtra("android.intent.extra.ringtone.EXISTING_URI", paramAnonymousAdapterView);
                  ProfileNotificationsActivity.this.startActivityForResult((Intent)localObject3, 12);
                  return;
                }
                catch (Exception paramAnonymousAdapterView)
                {
                  FileLog.e("tmessages", paramAnonymousAdapterView);
                  return;
                }
                paramAnonymousAdapterView = Uri.parse((String)localObject4);
              }
            }
            if (paramAnonymousInt != ProfileNotificationsActivity.this.settingsLedRow) {
              break;
            }
          } while (ProfileNotificationsActivity.this.getParentActivity() == null);
          paramAnonymousAdapterView = new LinearLayout(ProfileNotificationsActivity.this.getParentActivity());
          paramAnonymousAdapterView.setOrientation(1);
          paramAnonymousView = new ColorPickerView(ProfileNotificationsActivity.this.getParentActivity());
          paramAnonymousAdapterView.addView(paramAnonymousView, LayoutHelper.createLinear(-2, -2, 17));
          localObject1 = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
          if (((SharedPreferences)localObject1).contains("color_" + ProfileNotificationsActivity.this.dialog_id)) {
            paramAnonymousView.setOldCenterColor(((SharedPreferences)localObject1).getInt("color_" + ProfileNotificationsActivity.this.dialog_id, -16711936));
          }
          for (;;)
          {
            localObject1 = new AlertDialog.Builder(ProfileNotificationsActivity.this.getParentActivity());
            ((AlertDialog.Builder)localObject1).setTitle(LocaleController.getString("LedColor", 2131165819));
            ((AlertDialog.Builder)localObject1).setView(paramAnonymousAdapterView);
            ((AlertDialog.Builder)localObject1).setPositiveButton(LocaleController.getString("Set", 2131166259), new DialogInterface.OnClickListener()
            {
              public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
              {
                paramAnonymous2DialogInterface = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
                paramAnonymous2DialogInterface.putInt("color_" + ProfileNotificationsActivity.this.dialog_id, paramAnonymousView.getColor());
                paramAnonymous2DialogInterface.commit();
                ProfileNotificationsActivity.this.listView.invalidateViews();
              }
            });
            ((AlertDialog.Builder)localObject1).setNeutralButton(LocaleController.getString("LedDisabled", 2131165820), new DialogInterface.OnClickListener()
            {
              public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
              {
                paramAnonymous2DialogInterface = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
                paramAnonymous2DialogInterface.putInt("color_" + ProfileNotificationsActivity.this.dialog_id, 0);
                paramAnonymous2DialogInterface.commit();
                ProfileNotificationsActivity.this.listView.invalidateViews();
              }
            });
            ((AlertDialog.Builder)localObject1).setNegativeButton(LocaleController.getString("Default", 2131165559), new DialogInterface.OnClickListener()
            {
              public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
              {
                paramAnonymous2DialogInterface = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
                paramAnonymous2DialogInterface.remove("color_" + ProfileNotificationsActivity.this.dialog_id);
                paramAnonymous2DialogInterface.commit();
                ProfileNotificationsActivity.this.listView.invalidateViews();
              }
            });
            ProfileNotificationsActivity.this.showDialog(((AlertDialog.Builder)localObject1).create());
            return;
            if ((int)ProfileNotificationsActivity.this.dialog_id < 0) {
              paramAnonymousView.setOldCenterColor(((SharedPreferences)localObject1).getInt("GroupLed", -16711936));
            } else {
              paramAnonymousView.setOldCenterColor(((SharedPreferences)localObject1).getInt("MessagesLed", -16711936));
            }
          }
          if (paramAnonymousInt == ProfileNotificationsActivity.this.settingsPriorityRow)
          {
            paramAnonymousAdapterView = new AlertDialog.Builder(ProfileNotificationsActivity.this.getParentActivity());
            paramAnonymousAdapterView.setTitle(LocaleController.getString("NotificationsPriority", 2131166034));
            paramAnonymousView = LocaleController.getString("SettingsDefault", 2131166271);
            localObject1 = LocaleController.getString("NotificationsPriorityDefault", 2131166035);
            localObject2 = LocaleController.getString("NotificationsPriorityHigh", 2131166036);
            localObject3 = LocaleController.getString("NotificationsPriorityMax", 2131166038);
            localObject4 = new DialogInterface.OnClickListener()
            {
              public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
              {
                if (paramAnonymous2Int == 0) {
                  paramAnonymous2Int = 3;
                }
                for (;;)
                {
                  ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit().putInt("priority_" + ProfileNotificationsActivity.this.dialog_id, paramAnonymous2Int).commit();
                  if (ProfileNotificationsActivity.this.listView != null) {
                    ProfileNotificationsActivity.this.listView.invalidateViews();
                  }
                  return;
                  paramAnonymous2Int -= 1;
                }
              }
            };
            paramAnonymousAdapterView.setItems(new CharSequence[] { paramAnonymousView, localObject1, localObject2, localObject3 }, (DialogInterface.OnClickListener)localObject4);
            paramAnonymousAdapterView.setNegativeButton(LocaleController.getString("Cancel", 2131165386), null);
            ProfileNotificationsActivity.this.showDialog(paramAnonymousAdapterView.create());
            return;
          }
        } while ((paramAnonymousInt != ProfileNotificationsActivity.this.smartRow) || (ProfileNotificationsActivity.this.getParentActivity() == null));
        paramAnonymousAdapterView = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
        int i = paramAnonymousAdapterView.getInt("smart_max_count_" + ProfileNotificationsActivity.this.dialog_id, 2);
        int j = paramAnonymousAdapterView.getInt("smart_delay_" + ProfileNotificationsActivity.this.dialog_id, 180);
        paramAnonymousInt = i;
        if (i == 0) {
          paramAnonymousInt = 2;
        }
        paramAnonymousAdapterView = new LinearLayout(ProfileNotificationsActivity.this.getParentActivity());
        paramAnonymousAdapterView.setOrientation(1);
        Object localObject1 = new LinearLayout(ProfileNotificationsActivity.this.getParentActivity());
        ((LinearLayout)localObject1).setOrientation(0);
        paramAnonymousAdapterView.addView((View)localObject1);
        paramAnonymousView = (LinearLayout.LayoutParams)((LinearLayout)localObject1).getLayoutParams();
        paramAnonymousView.width = -2;
        paramAnonymousView.height = -2;
        paramAnonymousView.gravity = 49;
        ((LinearLayout)localObject1).setLayoutParams(paramAnonymousView);
        paramAnonymousView = new TextView(ProfileNotificationsActivity.this.getParentActivity());
        paramAnonymousView.setText(LocaleController.getString("SmartNotificationsSoundAtMost", 2131166297));
        paramAnonymousView.setTextSize(1, 18.0F);
        ((LinearLayout)localObject1).addView(paramAnonymousView);
        final Object localObject2 = (LinearLayout.LayoutParams)paramAnonymousView.getLayoutParams();
        ((LinearLayout.LayoutParams)localObject2).width = -2;
        ((LinearLayout.LayoutParams)localObject2).height = -2;
        ((LinearLayout.LayoutParams)localObject2).gravity = 19;
        paramAnonymousView.setLayoutParams((ViewGroup.LayoutParams)localObject2);
        paramAnonymousView = new NumberPicker(ProfileNotificationsActivity.this.getParentActivity());
        paramAnonymousView.setMinValue(1);
        paramAnonymousView.setMaxValue(10);
        paramAnonymousView.setValue(paramAnonymousInt);
        ((LinearLayout)localObject1).addView(paramAnonymousView);
        localObject2 = (LinearLayout.LayoutParams)paramAnonymousView.getLayoutParams();
        ((LinearLayout.LayoutParams)localObject2).width = AndroidUtilities.dp(50.0F);
        paramAnonymousView.setLayoutParams((ViewGroup.LayoutParams)localObject2);
        localObject2 = new TextView(ProfileNotificationsActivity.this.getParentActivity());
        ((TextView)localObject2).setText(LocaleController.getString("SmartNotificationsTimes", 2131166298));
        ((TextView)localObject2).setTextSize(1, 18.0F);
        ((LinearLayout)localObject1).addView((View)localObject2);
        localObject1 = (LinearLayout.LayoutParams)((TextView)localObject2).getLayoutParams();
        ((LinearLayout.LayoutParams)localObject1).width = -2;
        ((LinearLayout.LayoutParams)localObject1).height = -2;
        ((LinearLayout.LayoutParams)localObject1).gravity = 19;
        ((TextView)localObject2).setLayoutParams((ViewGroup.LayoutParams)localObject1);
        localObject1 = new LinearLayout(ProfileNotificationsActivity.this.getParentActivity());
        ((LinearLayout)localObject1).setOrientation(0);
        paramAnonymousAdapterView.addView((View)localObject1);
        localObject2 = (LinearLayout.LayoutParams)((LinearLayout)localObject1).getLayoutParams();
        ((LinearLayout.LayoutParams)localObject2).width = -2;
        ((LinearLayout.LayoutParams)localObject2).height = -2;
        ((LinearLayout.LayoutParams)localObject2).gravity = 49;
        ((LinearLayout)localObject1).setLayoutParams((ViewGroup.LayoutParams)localObject2);
        localObject2 = new TextView(ProfileNotificationsActivity.this.getParentActivity());
        ((TextView)localObject2).setText(LocaleController.getString("SmartNotificationsWithin", 2131166299));
        ((TextView)localObject2).setTextSize(1, 18.0F);
        ((LinearLayout)localObject1).addView((View)localObject2);
        Object localObject3 = (LinearLayout.LayoutParams)((TextView)localObject2).getLayoutParams();
        ((LinearLayout.LayoutParams)localObject3).width = -2;
        ((LinearLayout.LayoutParams)localObject3).height = -2;
        ((LinearLayout.LayoutParams)localObject3).gravity = 19;
        ((TextView)localObject2).setLayoutParams((ViewGroup.LayoutParams)localObject3);
        localObject2 = new NumberPicker(ProfileNotificationsActivity.this.getParentActivity());
        ((NumberPicker)localObject2).setMinValue(1);
        ((NumberPicker)localObject2).setMaxValue(10);
        ((NumberPicker)localObject2).setValue(j / 60);
        ((LinearLayout)localObject1).addView((View)localObject2);
        localObject3 = (LinearLayout.LayoutParams)((NumberPicker)localObject2).getLayoutParams();
        ((LinearLayout.LayoutParams)localObject3).width = AndroidUtilities.dp(50.0F);
        ((NumberPicker)localObject2).setLayoutParams((ViewGroup.LayoutParams)localObject3);
        localObject3 = new TextView(ProfileNotificationsActivity.this.getParentActivity());
        ((TextView)localObject3).setText(LocaleController.getString("SmartNotificationsMinutes", 2131166296));
        ((TextView)localObject3).setTextSize(1, 18.0F);
        ((LinearLayout)localObject1).addView((View)localObject3);
        localObject1 = (LinearLayout.LayoutParams)((TextView)localObject3).getLayoutParams();
        ((LinearLayout.LayoutParams)localObject1).width = -2;
        ((LinearLayout.LayoutParams)localObject1).height = -2;
        ((LinearLayout.LayoutParams)localObject1).gravity = 19;
        ((TextView)localObject3).setLayoutParams((ViewGroup.LayoutParams)localObject1);
        localObject1 = new AlertDialog.Builder(ProfileNotificationsActivity.this.getParentActivity());
        ((AlertDialog.Builder)localObject1).setTitle(LocaleController.getString("SmartNotifications", 2131166293));
        ((AlertDialog.Builder)localObject1).setView(paramAnonymousAdapterView);
        ((AlertDialog.Builder)localObject1).setPositiveButton(LocaleController.getString("OK", 2131166044), new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
          {
            paramAnonymous2DialogInterface = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
            paramAnonymous2DialogInterface.edit().putInt("smart_max_count_" + ProfileNotificationsActivity.this.dialog_id, paramAnonymousView.getValue()).commit();
            paramAnonymous2DialogInterface.edit().putInt("smart_delay_" + ProfileNotificationsActivity.this.dialog_id, localObject2.getValue() * 60).commit();
            if (ProfileNotificationsActivity.this.listView != null) {
              ProfileNotificationsActivity.this.listView.invalidateViews();
            }
          }
        });
        ((AlertDialog.Builder)localObject1).setNegativeButton(LocaleController.getString("SmartNotificationsDisabled", 2131166294), new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
          {
            ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit().putInt("smart_max_count_" + ProfileNotificationsActivity.this.dialog_id, 0).commit();
            if (ProfileNotificationsActivity.this.listView != null) {
              ProfileNotificationsActivity.this.listView.invalidateViews();
            }
          }
        });
        ProfileNotificationsActivity.this.showDialog(((AlertDialog.Builder)localObject1).create());
      }
    });
    return this.fragmentView;
  }
  
  public void didReceivedNotification(int paramInt, Object... paramVarArgs)
  {
    if (paramInt == NotificationCenter.notificationsSettingsUpdated) {
      this.listView.invalidateViews();
    }
  }
  
  public void onActivityResultFragment(int paramInt1, int paramInt2, Intent paramIntent)
  {
    if ((paramInt2 != -1) || (paramIntent == null)) {
      return;
    }
    Uri localUri = (Uri)paramIntent.getParcelableExtra("android.intent.extra.ringtone.PICKED_URI");
    SharedPreferences.Editor localEditor = null;
    paramIntent = localEditor;
    Ringtone localRingtone;
    if (localUri != null)
    {
      localRingtone = RingtoneManager.getRingtone(ApplicationLoader.applicationContext, localUri);
      paramIntent = localEditor;
      if (localRingtone != null)
      {
        if (!localUri.equals(Settings.System.DEFAULT_NOTIFICATION_URI)) {
          break label184;
        }
        paramIntent = LocaleController.getString("SoundDefault", 2131166306);
        localRingtone.stop();
      }
    }
    localEditor = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
    if (paramInt1 == 12)
    {
      if (paramIntent == null) {
        break label197;
      }
      localEditor.putString("sound_" + this.dialog_id, paramIntent);
      localEditor.putString("sound_path_" + this.dialog_id, localUri.toString());
    }
    for (;;)
    {
      localEditor.commit();
      this.listView.invalidateViews();
      return;
      label184:
      paramIntent = localRingtone.getTitle(getParentActivity());
      break;
      label197:
      localEditor.putString("sound_" + this.dialog_id, "NoSound");
      localEditor.putString("sound_path_" + this.dialog_id, "NoSound");
    }
  }
  
  public boolean onFragmentCreate()
  {
    int i = this.rowCount;
    this.rowCount = (i + 1);
    this.settingsNotificationsRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.settingsVibrateRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.settingsSoundRow = i;
    if (Build.VERSION.SDK_INT >= 21)
    {
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.settingsPriorityRow = i;
      if ((int)this.dialog_id >= 0) {
        break label141;
      }
      i = this.rowCount;
      this.rowCount = (i + 1);
    }
    label141:
    for (this.smartRow = i;; this.smartRow = 1)
    {
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.settingsLedRow = i;
      NotificationCenter.getInstance().addObserver(this, NotificationCenter.notificationsSettingsUpdated);
      return super.onFragmentCreate();
      this.settingsPriorityRow = -1;
      break;
    }
  }
  
  public void onFragmentDestroy()
  {
    super.onFragmentDestroy();
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.notificationsSettingsUpdated);
  }
  
  private class ListAdapter
    extends BaseFragmentAdapter
  {
    private Context mContext;
    
    public ListAdapter(Context paramContext)
    {
      this.mContext = paramContext;
    }
    
    public boolean areAllItemsEnabled()
    {
      return true;
    }
    
    public int getCount()
    {
      return ProfileNotificationsActivity.this.rowCount;
    }
    
    public Object getItem(int paramInt)
    {
      return null;
    }
    
    public long getItemId(int paramInt)
    {
      return paramInt;
    }
    
    public int getItemViewType(int paramInt)
    {
      if ((paramInt == ProfileNotificationsActivity.this.settingsNotificationsRow) || (paramInt == ProfileNotificationsActivity.this.settingsVibrateRow) || (paramInt == ProfileNotificationsActivity.this.settingsSoundRow) || (paramInt == ProfileNotificationsActivity.this.settingsPriorityRow) || (paramInt == ProfileNotificationsActivity.this.smartRow)) {}
      while (paramInt != ProfileNotificationsActivity.this.settingsLedRow) {
        return 0;
      }
      return 1;
    }
    
    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      int i = getItemViewType(paramInt);
      TextDetailSettingsCell localTextDetailSettingsCell;
      if (i == 0)
      {
        paramViewGroup = paramView;
        if (paramView == null) {
          paramViewGroup = new TextDetailSettingsCell(this.mContext);
        }
        localTextDetailSettingsCell = (TextDetailSettingsCell)paramViewGroup;
        paramView = this.mContext.getSharedPreferences("Notifications", 0);
        if (paramInt == ProfileNotificationsActivity.this.settingsVibrateRow)
        {
          paramInt = paramView.getInt("vibrate_" + ProfileNotificationsActivity.this.dialog_id, 0);
          if (paramInt == 0)
          {
            localTextDetailSettingsCell.setTextAndValue(LocaleController.getString("Vibrate", 2131166382), LocaleController.getString("SettingsDefault", 2131166271), true);
            localObject = paramViewGroup;
          }
        }
      }
      do
      {
        do
        {
          do
          {
            do
            {
              do
              {
                return (View)localObject;
                if (paramInt == 1)
                {
                  localTextDetailSettingsCell.setTextAndValue(LocaleController.getString("Vibrate", 2131166382), LocaleController.getString("Short", 2131166288), true);
                  return paramViewGroup;
                }
                if (paramInt == 2)
                {
                  localTextDetailSettingsCell.setTextAndValue(LocaleController.getString("Vibrate", 2131166382), LocaleController.getString("VibrationDisabled", 2131166384), true);
                  return paramViewGroup;
                }
                if (paramInt == 3)
                {
                  localTextDetailSettingsCell.setTextAndValue(LocaleController.getString("Vibrate", 2131166382), LocaleController.getString("Long", 2131165847), true);
                  return paramViewGroup;
                }
                localObject = paramViewGroup;
              } while (paramInt != 4);
              localTextDetailSettingsCell.setTextAndValue(LocaleController.getString("Vibrate", 2131166382), LocaleController.getString("SystemDefault", 2131166323), true);
              return paramViewGroup;
              if (paramInt != ProfileNotificationsActivity.this.settingsNotificationsRow) {
                break;
              }
              paramInt = paramView.getInt("notify2_" + ProfileNotificationsActivity.this.dialog_id, 0);
              if (paramInt == 0)
              {
                localTextDetailSettingsCell.setTextAndValue(LocaleController.getString("Notifications", 2131166030), LocaleController.getString("Default", 2131165559), true);
                return paramViewGroup;
              }
              if (paramInt == 1)
              {
                localTextDetailSettingsCell.setTextAndValue(LocaleController.getString("Notifications", 2131166030), LocaleController.getString("Enabled", 2131165602), true);
                return paramViewGroup;
              }
              if (paramInt == 2)
              {
                localTextDetailSettingsCell.setTextAndValue(LocaleController.getString("Notifications", 2131166030), LocaleController.getString("NotificationsDisabled", 2131166032), true);
                return paramViewGroup;
              }
              localObject = paramViewGroup;
            } while (paramInt != 3);
            paramInt = paramView.getInt("notifyuntil_" + ProfileNotificationsActivity.this.dialog_id, 0) - ConnectionsManager.getInstance().getCurrentTime();
            if (paramInt <= 0) {
              paramView = LocaleController.getString("Enabled", 2131165602);
            }
            while (paramView != null)
            {
              localTextDetailSettingsCell.setTextAndValue(LocaleController.getString("Notifications", 2131166030), paramView, true);
              return paramViewGroup;
              if (paramInt < 3600) {
                paramView = LocaleController.formatString("WillUnmuteIn", 2131166403, new Object[] { LocaleController.formatPluralString("Minutes", paramInt / 60) });
              } else if (paramInt < 86400) {
                paramView = LocaleController.formatString("WillUnmuteIn", 2131166403, new Object[] { LocaleController.formatPluralString("Hours", (int)Math.ceil(paramInt / 60.0F / 60.0F)) });
              } else if (paramInt < 31536000) {
                paramView = LocaleController.formatString("WillUnmuteIn", 2131166403, new Object[] { LocaleController.formatPluralString("Days", (int)Math.ceil(paramInt / 60.0F / 60.0F / 24.0F)) });
              } else {
                paramView = null;
              }
            }
            localTextDetailSettingsCell.setTextAndValue(LocaleController.getString("Notifications", 2131166030), LocaleController.getString("NotificationsDisabled", 2131166032), true);
            return paramViewGroup;
            if (paramInt == ProfileNotificationsActivity.this.settingsSoundRow)
            {
              localObject = paramView.getString("sound_" + ProfileNotificationsActivity.this.dialog_id, LocaleController.getString("SoundDefault", 2131166306));
              paramView = (View)localObject;
              if (((String)localObject).equals("NoSound")) {
                paramView = LocaleController.getString("NoSound", 2131165956);
              }
              localTextDetailSettingsCell.setTextAndValue(LocaleController.getString("Sound", 2131166305), paramView, true);
              return paramViewGroup;
            }
            if (paramInt != ProfileNotificationsActivity.this.settingsPriorityRow) {
              break;
            }
            paramInt = paramView.getInt("priority_" + ProfileNotificationsActivity.this.dialog_id, 3);
            if (paramInt == 0)
            {
              localTextDetailSettingsCell.setTextAndValue(LocaleController.getString("NotificationsPriority", 2131166034), LocaleController.getString("NotificationsPriorityDefault", 2131166035), true);
              return paramViewGroup;
            }
            if (paramInt == 1)
            {
              localTextDetailSettingsCell.setTextAndValue(LocaleController.getString("NotificationsPriority", 2131166034), LocaleController.getString("NotificationsPriorityHigh", 2131166036), true);
              return paramViewGroup;
            }
            if (paramInt == 2)
            {
              localTextDetailSettingsCell.setTextAndValue(LocaleController.getString("NotificationsPriority", 2131166034), LocaleController.getString("NotificationsPriorityMax", 2131166038), true);
              return paramViewGroup;
            }
            localObject = paramViewGroup;
          } while (paramInt != 3);
          localTextDetailSettingsCell.setTextAndValue(LocaleController.getString("NotificationsPriority", 2131166034), LocaleController.getString("SettingsDefault", 2131166271), true);
          return paramViewGroup;
          localObject = paramViewGroup;
        } while (paramInt != ProfileNotificationsActivity.this.smartRow);
        paramInt = paramView.getInt("smart_max_count_" + ProfileNotificationsActivity.this.dialog_id, 2);
        i = paramView.getInt("smart_delay_" + ProfileNotificationsActivity.this.dialog_id, 180);
        if (paramInt == 0)
        {
          localTextDetailSettingsCell.setTextAndValue(LocaleController.getString("SmartNotifications", 2131166293), LocaleController.getString("SmartNotificationsDisabled", 2131166294), true);
          return paramViewGroup;
        }
        paramView = LocaleController.formatPluralString("Times", paramInt);
        localObject = LocaleController.formatPluralString("Minutes", i / 60);
        localTextDetailSettingsCell.setTextAndValue(LocaleController.getString("SmartNotifications", 2131166293), LocaleController.formatString("SmartNotificationsInfo", 2131166295, new Object[] { paramView, localObject }), true);
        return paramViewGroup;
        localObject = paramView;
      } while (i != 1);
      paramViewGroup = paramView;
      if (paramView == null) {
        paramViewGroup = new TextColorCell(this.mContext);
      }
      paramView = (TextColorCell)paramViewGroup;
      Object localObject = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
      if (((SharedPreferences)localObject).contains("color_" + ProfileNotificationsActivity.this.dialog_id))
      {
        paramView.setTextAndColor(LocaleController.getString("LedColor", 2131165819), ((SharedPreferences)localObject).getInt("color_" + ProfileNotificationsActivity.this.dialog_id, -16711936), false);
        return paramViewGroup;
      }
      if ((int)ProfileNotificationsActivity.this.dialog_id < 0)
      {
        paramView.setTextAndColor(LocaleController.getString("LedColor", 2131165819), ((SharedPreferences)localObject).getInt("GroupLed", -16711936), false);
        return paramViewGroup;
      }
      paramView.setTextAndColor(LocaleController.getString("LedColor", 2131165819), ((SharedPreferences)localObject).getInt("MessagesLed", -16711936), false);
      return paramViewGroup;
    }
    
    public int getViewTypeCount()
    {
      return 2;
    }
    
    public boolean hasStableIds()
    {
      return false;
    }
    
    public boolean isEmpty()
    {
      return false;
    }
    
    public boolean isEnabled(int paramInt)
    {
      return true;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/ProfileNotificationsActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */