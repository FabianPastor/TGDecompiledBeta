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
import android.widget.ListView;
import android.widget.Toast;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.NotificationsController;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_account_resetNotifySettings;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Adapters.BaseFragmentAdapter;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextColorCell;
import org.telegram.ui.Cells.TextDetailSettingsCell;
import org.telegram.ui.Components.ColorPickerView;
import org.telegram.ui.Components.LayoutHelper;

public class NotificationsSettingsActivity
  extends BaseFragment
  implements NotificationCenter.NotificationCenterDelegate
{
  private int androidAutoAlertRow;
  private int badgeNumberRow;
  private int contactJoinedRow;
  private int eventsSectionRow;
  private int eventsSectionRow2;
  private int groupAlertRow;
  private int groupLedRow;
  private int groupPopupNotificationRow;
  private int groupPreviewRow;
  private int groupPriorityRow;
  private int groupSectionRow;
  private int groupSectionRow2;
  private int groupSoundRow;
  private int groupVibrateRow;
  private int inappPreviewRow;
  private int inappPriorityRow;
  private int inappSectionRow;
  private int inappSectionRow2;
  private int inappSoundRow;
  private int inappVibrateRow;
  private int inchatSoundRow;
  private ListView listView;
  private int messageAlertRow;
  private int messageLedRow;
  private int messagePopupNotificationRow;
  private int messagePreviewRow;
  private int messagePriorityRow;
  private int messageSectionRow;
  private int messageSoundRow;
  private int messageVibrateRow;
  private int notificationsServiceConnectionRow;
  private int notificationsServiceRow;
  private int otherSectionRow;
  private int otherSectionRow2;
  private int pinnedMessageRow;
  private int repeatRow;
  private int resetNotificationsRow;
  private int resetSectionRow;
  private int resetSectionRow2;
  private boolean reseting = false;
  private int rowCount = 0;
  
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
          NotificationsSettingsActivity.this.finishFragment();
        }
      }
    });
    this.fragmentView = new FrameLayout(paramContext);
    Object localObject = (FrameLayout)this.fragmentView;
    this.listView = new ListView(paramContext);
    this.listView.setDivider(null);
    this.listView.setDividerHeight(0);
    this.listView.setVerticalScrollBarEnabled(false);
    ((FrameLayout)localObject).addView(this.listView);
    localObject = (FrameLayout.LayoutParams)this.listView.getLayoutParams();
    ((FrameLayout.LayoutParams)localObject).width = -1;
    ((FrameLayout.LayoutParams)localObject).height = -1;
    this.listView.setLayoutParams((ViewGroup.LayoutParams)localObject);
    this.listView.setAdapter(new ListAdapter(paramContext));
    this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
    {
      public void onItemClick(AdapterView<?> paramAnonymousAdapterView, final View paramAnonymousView, final int paramAnonymousInt, long paramAnonymousLong)
      {
        boolean bool2 = false;
        boolean bool3 = false;
        boolean bool1 = false;
        final Object localObject1;
        if ((paramAnonymousInt == NotificationsSettingsActivity.this.messageAlertRow) || (paramAnonymousInt == NotificationsSettingsActivity.this.groupAlertRow))
        {
          paramAnonymousAdapterView = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
          localObject1 = paramAnonymousAdapterView.edit();
          if (paramAnonymousInt == NotificationsSettingsActivity.this.messageAlertRow)
          {
            bool2 = paramAnonymousAdapterView.getBoolean("EnableAll", true);
            if (!bool2)
            {
              bool1 = true;
              ((SharedPreferences.Editor)localObject1).putBoolean("EnableAll", bool1);
              bool1 = bool2;
              label95:
              ((SharedPreferences.Editor)localObject1).commit();
              paramAnonymousAdapterView = NotificationsSettingsActivity.this;
              if (paramAnonymousInt != NotificationsSettingsActivity.this.groupAlertRow) {
                break label216;
              }
              bool2 = true;
              label122:
              paramAnonymousAdapterView.updateServerNotificationsSettings(bool2);
              label128:
              if ((paramAnonymousView instanceof TextCheckCell))
              {
                paramAnonymousAdapterView = (TextCheckCell)paramAnonymousView;
                if (bool1) {
                  break label2747;
                }
              }
            }
          }
        }
        label216:
        label292:
        label308:
        label409:
        label610:
        label619:
        label753:
        label1044:
        label1095:
        label1237:
        label1283:
        label1504:
        label1556:
        label1654:
        label1756:
        label2031:
        label2747:
        for (bool1 = true;; bool1 = false)
        {
          paramAnonymousAdapterView.setChecked(bool1);
          do
          {
            do
            {
              return;
              bool1 = false;
              break;
              if (paramAnonymousInt != NotificationsSettingsActivity.this.groupAlertRow) {
                break label95;
              }
              bool2 = paramAnonymousAdapterView.getBoolean("EnableGroup", true);
              if (!bool2) {}
              for (bool1 = true;; bool1 = false)
              {
                ((SharedPreferences.Editor)localObject1).putBoolean("EnableGroup", bool1);
                bool1 = bool2;
                break;
              }
              bool2 = false;
              break label122;
              if ((paramAnonymousInt == NotificationsSettingsActivity.this.messagePreviewRow) || (paramAnonymousInt == NotificationsSettingsActivity.this.groupPreviewRow))
              {
                paramAnonymousAdapterView = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
                localObject1 = paramAnonymousAdapterView.edit();
                if (paramAnonymousInt == NotificationsSettingsActivity.this.messagePreviewRow)
                {
                  bool2 = paramAnonymousAdapterView.getBoolean("EnablePreviewAll", true);
                  if (!bool2)
                  {
                    bool1 = true;
                    ((SharedPreferences.Editor)localObject1).putBoolean("EnablePreviewAll", bool1);
                    bool1 = bool2;
                    ((SharedPreferences.Editor)localObject1).commit();
                    paramAnonymousAdapterView = NotificationsSettingsActivity.this;
                    if (paramAnonymousInt != NotificationsSettingsActivity.this.groupPreviewRow) {
                      break label409;
                    }
                  }
                }
                for (bool2 = true;; bool2 = false)
                {
                  paramAnonymousAdapterView.updateServerNotificationsSettings(bool2);
                  break;
                  bool1 = false;
                  break label292;
                  bool1 = bool3;
                  if (paramAnonymousInt != NotificationsSettingsActivity.this.groupPreviewRow) {
                    break label308;
                  }
                  bool2 = paramAnonymousAdapterView.getBoolean("EnablePreviewGroup", true);
                  if (!bool2) {}
                  for (bool1 = true;; bool1 = false)
                  {
                    ((SharedPreferences.Editor)localObject1).putBoolean("EnablePreviewGroup", bool1);
                    bool1 = bool2;
                    break;
                  }
                }
              }
              if ((paramAnonymousInt == NotificationsSettingsActivity.this.messageSoundRow) || (paramAnonymousInt == NotificationsSettingsActivity.this.groupSoundRow)) {
                for (;;)
                {
                  try
                  {
                    localObject4 = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
                    localObject3 = new Intent("android.intent.action.RINGTONE_PICKER");
                    ((Intent)localObject3).putExtra("android.intent.extra.ringtone.TYPE", 2);
                    ((Intent)localObject3).putExtra("android.intent.extra.ringtone.SHOW_DEFAULT", true);
                    ((Intent)localObject3).putExtra("android.intent.extra.ringtone.DEFAULT_URI", RingtoneManager.getDefaultUri(2));
                    str1 = null;
                    localObject2 = null;
                    localObject1 = Settings.System.DEFAULT_NOTIFICATION_URI;
                    if (localObject1 != null) {
                      localObject2 = ((Uri)localObject1).getPath();
                    }
                    if (paramAnonymousInt != NotificationsSettingsActivity.this.messageSoundRow) {
                      break label619;
                    }
                    localObject4 = ((SharedPreferences)localObject4).getString("GlobalSoundPath", (String)localObject2);
                    paramAnonymousAdapterView = str1;
                    if (localObject4 != null)
                    {
                      paramAnonymousAdapterView = str1;
                      if (!((String)localObject4).equals("NoSound"))
                      {
                        if (!((String)localObject4).equals(localObject2)) {
                          break label610;
                        }
                        paramAnonymousAdapterView = (AdapterView<?>)localObject1;
                      }
                    }
                    ((Intent)localObject3).putExtra("android.intent.extra.ringtone.EXISTING_URI", paramAnonymousAdapterView);
                    NotificationsSettingsActivity.this.startActivityForResult((Intent)localObject3, paramAnonymousInt);
                    bool1 = bool2;
                  }
                  catch (Exception paramAnonymousAdapterView)
                  {
                    FileLog.e("tmessages", paramAnonymousAdapterView);
                    bool1 = bool2;
                  }
                  break;
                  paramAnonymousAdapterView = Uri.parse((String)localObject4);
                  continue;
                  paramAnonymousAdapterView = str1;
                  if (paramAnonymousInt == NotificationsSettingsActivity.this.groupSoundRow)
                  {
                    localObject4 = ((SharedPreferences)localObject4).getString("GroupSoundPath", (String)localObject2);
                    paramAnonymousAdapterView = str1;
                    if (localObject4 != null)
                    {
                      paramAnonymousAdapterView = str1;
                      if (!((String)localObject4).equals("NoSound")) {
                        if (((String)localObject4).equals(localObject2)) {
                          paramAnonymousAdapterView = (AdapterView<?>)localObject1;
                        } else {
                          paramAnonymousAdapterView = Uri.parse((String)localObject4);
                        }
                      }
                    }
                  }
                }
              }
              if (paramAnonymousInt != NotificationsSettingsActivity.this.resetNotificationsRow) {
                break label753;
              }
            } while (NotificationsSettingsActivity.this.reseting);
            NotificationsSettingsActivity.access$702(NotificationsSettingsActivity.this, true);
            paramAnonymousAdapterView = new TLRPC.TL_account_resetNotifySettings();
            ConnectionsManager.getInstance().sendRequest(paramAnonymousAdapterView, new RequestDelegate()
            {
              public void run(TLObject paramAnonymous2TLObject, TLRPC.TL_error paramAnonymous2TL_error)
              {
                AndroidUtilities.runOnUIThread(new Runnable()
                {
                  public void run()
                  {
                    MessagesController.getInstance().enableJoined = true;
                    NotificationsSettingsActivity.access$702(NotificationsSettingsActivity.this, false);
                    SharedPreferences.Editor localEditor = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
                    localEditor.clear();
                    localEditor.commit();
                    if (NotificationsSettingsActivity.this.listView != null) {
                      NotificationsSettingsActivity.this.listView.invalidateViews();
                    }
                    if (NotificationsSettingsActivity.this.getParentActivity() != null) {
                      Toast.makeText(NotificationsSettingsActivity.this.getParentActivity(), LocaleController.getString("ResetNotificationsText", 2131166182), 0).show();
                    }
                  }
                });
              }
            });
            bool1 = bool2;
            break label128;
            if (paramAnonymousInt == NotificationsSettingsActivity.this.inappSoundRow)
            {
              paramAnonymousAdapterView = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
              localObject1 = paramAnonymousAdapterView.edit();
              bool2 = paramAnonymousAdapterView.getBoolean("EnableInAppSounds", true);
              if (!bool2) {}
              for (bool1 = true;; bool1 = false)
              {
                ((SharedPreferences.Editor)localObject1).putBoolean("EnableInAppSounds", bool1);
                ((SharedPreferences.Editor)localObject1).commit();
                bool1 = bool2;
                break;
              }
            }
            if (paramAnonymousInt == NotificationsSettingsActivity.this.inappVibrateRow)
            {
              paramAnonymousAdapterView = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
              localObject1 = paramAnonymousAdapterView.edit();
              bool2 = paramAnonymousAdapterView.getBoolean("EnableInAppVibrate", true);
              if (!bool2) {}
              for (bool1 = true;; bool1 = false)
              {
                ((SharedPreferences.Editor)localObject1).putBoolean("EnableInAppVibrate", bool1);
                ((SharedPreferences.Editor)localObject1).commit();
                bool1 = bool2;
                break;
              }
            }
            if (paramAnonymousInt == NotificationsSettingsActivity.this.inappPreviewRow)
            {
              paramAnonymousAdapterView = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
              localObject1 = paramAnonymousAdapterView.edit();
              bool2 = paramAnonymousAdapterView.getBoolean("EnableInAppPreview", true);
              if (!bool2) {}
              for (bool1 = true;; bool1 = false)
              {
                ((SharedPreferences.Editor)localObject1).putBoolean("EnableInAppPreview", bool1);
                ((SharedPreferences.Editor)localObject1).commit();
                bool1 = bool2;
                break;
              }
            }
            if (paramAnonymousInt == NotificationsSettingsActivity.this.inchatSoundRow)
            {
              paramAnonymousAdapterView = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
              localObject1 = paramAnonymousAdapterView.edit();
              bool2 = paramAnonymousAdapterView.getBoolean("EnableInChatSound", true);
              if (!bool2)
              {
                bool1 = true;
                ((SharedPreferences.Editor)localObject1).putBoolean("EnableInChatSound", bool1);
                ((SharedPreferences.Editor)localObject1).commit();
                paramAnonymousAdapterView = NotificationsController.getInstance();
                if (bool2) {
                  break label1095;
                }
              }
              for (bool1 = true;; bool1 = false)
              {
                paramAnonymousAdapterView.setInChatSoundEnabled(bool1);
                bool1 = bool2;
                break;
                bool1 = false;
                break label1044;
              }
            }
            if (paramAnonymousInt == NotificationsSettingsActivity.this.inappPriorityRow)
            {
              paramAnonymousAdapterView = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
              localObject1 = paramAnonymousAdapterView.edit();
              bool2 = paramAnonymousAdapterView.getBoolean("EnableInAppPriority", false);
              if (!bool2) {}
              for (bool1 = true;; bool1 = false)
              {
                ((SharedPreferences.Editor)localObject1).putBoolean("EnableInAppPriority", bool1);
                ((SharedPreferences.Editor)localObject1).commit();
                bool1 = bool2;
                break;
              }
            }
            if (paramAnonymousInt == NotificationsSettingsActivity.this.contactJoinedRow)
            {
              localObject1 = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
              paramAnonymousAdapterView = ((SharedPreferences)localObject1).edit();
              bool2 = ((SharedPreferences)localObject1).getBoolean("EnableContactJoined", true);
              localObject1 = MessagesController.getInstance();
              if (!bool2)
              {
                bool1 = true;
                ((MessagesController)localObject1).enableJoined = bool1;
                if (bool2) {
                  break label1283;
                }
              }
              for (bool1 = true;; bool1 = false)
              {
                paramAnonymousAdapterView.putBoolean("EnableContactJoined", bool1);
                paramAnonymousAdapterView.commit();
                bool1 = bool2;
                break;
                bool1 = false;
                break label1237;
              }
            }
            if (paramAnonymousInt == NotificationsSettingsActivity.this.pinnedMessageRow)
            {
              paramAnonymousAdapterView = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
              localObject1 = paramAnonymousAdapterView.edit();
              bool2 = paramAnonymousAdapterView.getBoolean("PinnedMessages", true);
              if (!bool2) {}
              for (bool1 = true;; bool1 = false)
              {
                ((SharedPreferences.Editor)localObject1).putBoolean("PinnedMessages", bool1);
                ((SharedPreferences.Editor)localObject1).commit();
                bool1 = bool2;
                break;
              }
            }
            if (paramAnonymousInt == NotificationsSettingsActivity.this.androidAutoAlertRow)
            {
              paramAnonymousAdapterView = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
              localObject1 = paramAnonymousAdapterView.edit();
              bool2 = paramAnonymousAdapterView.getBoolean("EnableAutoNotifications", false);
              if (!bool2) {}
              for (bool1 = true;; bool1 = false)
              {
                ((SharedPreferences.Editor)localObject1).putBoolean("EnableAutoNotifications", bool1);
                ((SharedPreferences.Editor)localObject1).commit();
                bool1 = bool2;
                break;
              }
            }
            if (paramAnonymousInt == NotificationsSettingsActivity.this.badgeNumberRow)
            {
              paramAnonymousAdapterView = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
              localObject1 = paramAnonymousAdapterView.edit();
              bool2 = paramAnonymousAdapterView.getBoolean("badgeNumber", true);
              if (!bool2)
              {
                bool1 = true;
                ((SharedPreferences.Editor)localObject1).putBoolean("badgeNumber", bool1);
                ((SharedPreferences.Editor)localObject1).commit();
                paramAnonymousAdapterView = NotificationsController.getInstance();
                if (bool2) {
                  break label1556;
                }
              }
              for (bool1 = true;; bool1 = false)
              {
                paramAnonymousAdapterView.setBadgeEnabled(bool1);
                bool1 = bool2;
                break;
                bool1 = false;
                break label1504;
              }
            }
            if (paramAnonymousInt == NotificationsSettingsActivity.this.notificationsServiceConnectionRow)
            {
              paramAnonymousAdapterView = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
              bool2 = paramAnonymousAdapterView.getBoolean("pushConnection", true);
              paramAnonymousAdapterView = paramAnonymousAdapterView.edit();
              if (!bool2) {}
              for (bool1 = true;; bool1 = false)
              {
                paramAnonymousAdapterView.putBoolean("pushConnection", bool1);
                paramAnonymousAdapterView.commit();
                if (bool2) {
                  break label1654;
                }
                ConnectionsManager.getInstance().setPushConnectionEnabled(true);
                bool1 = bool2;
                break;
              }
              ConnectionsManager.getInstance().setPushConnectionEnabled(false);
              bool1 = bool2;
              break label128;
            }
            if (paramAnonymousInt == NotificationsSettingsActivity.this.notificationsServiceRow)
            {
              paramAnonymousAdapterView = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
              bool2 = paramAnonymousAdapterView.getBoolean("pushService", true);
              paramAnonymousAdapterView = paramAnonymousAdapterView.edit();
              if (!bool2) {}
              for (bool1 = true;; bool1 = false)
              {
                paramAnonymousAdapterView.putBoolean("pushService", bool1);
                paramAnonymousAdapterView.commit();
                if (bool2) {
                  break label1756;
                }
                ApplicationLoader.startPushService();
                bool1 = bool2;
                break;
              }
              ApplicationLoader.stopPushService();
              bool1 = bool2;
              break label128;
            }
            if ((paramAnonymousInt != NotificationsSettingsActivity.this.messageLedRow) && (paramAnonymousInt != NotificationsSettingsActivity.this.groupLedRow)) {
              break label2031;
            }
          } while (NotificationsSettingsActivity.this.getParentActivity() == null);
          paramAnonymousAdapterView = new LinearLayout(NotificationsSettingsActivity.this.getParentActivity());
          paramAnonymousAdapterView.setOrientation(1);
          localObject1 = new ColorPickerView(NotificationsSettingsActivity.this.getParentActivity());
          paramAnonymousAdapterView.addView((View)localObject1, LayoutHelper.createLinear(-2, -2, 17));
          Object localObject2 = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
          if (paramAnonymousInt == NotificationsSettingsActivity.this.messageLedRow) {
            ((ColorPickerView)localObject1).setOldCenterColor(((SharedPreferences)localObject2).getInt("MessagesLed", -16711936));
          }
          for (;;)
          {
            localObject2 = new AlertDialog.Builder(NotificationsSettingsActivity.this.getParentActivity());
            ((AlertDialog.Builder)localObject2).setTitle(LocaleController.getString("LedColor", 2131165819));
            ((AlertDialog.Builder)localObject2).setView(paramAnonymousAdapterView);
            ((AlertDialog.Builder)localObject2).setPositiveButton(LocaleController.getString("Set", 2131166259), new DialogInterface.OnClickListener()
            {
              public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
              {
                paramAnonymous2DialogInterface = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
                TextColorCell localTextColorCell = (TextColorCell)paramAnonymousView;
                if (paramAnonymousInt == NotificationsSettingsActivity.this.messageLedRow)
                {
                  paramAnonymous2DialogInterface.putInt("MessagesLed", localObject1.getColor());
                  localTextColorCell.setTextAndColor(LocaleController.getString("LedColor", 2131165819), localObject1.getColor(), true);
                }
                for (;;)
                {
                  paramAnonymous2DialogInterface.commit();
                  return;
                  if (paramAnonymousInt == NotificationsSettingsActivity.this.groupLedRow)
                  {
                    paramAnonymous2DialogInterface.putInt("GroupLed", localObject1.getColor());
                    localTextColorCell.setTextAndColor(LocaleController.getString("LedColor", 2131165819), localObject1.getColor(), true);
                  }
                }
              }
            });
            ((AlertDialog.Builder)localObject2).setNeutralButton(LocaleController.getString("LedDisabled", 2131165820), new DialogInterface.OnClickListener()
            {
              public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
              {
                paramAnonymous2DialogInterface = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
                TextColorCell localTextColorCell = (TextColorCell)paramAnonymousView;
                if (paramAnonymousInt == NotificationsSettingsActivity.this.messageLedRow)
                {
                  paramAnonymous2DialogInterface.putInt("MessagesLed", 0);
                  localTextColorCell.setTextAndColor(LocaleController.getString("LedColor", 2131165819), 0, true);
                }
                for (;;)
                {
                  paramAnonymous2DialogInterface.commit();
                  NotificationsSettingsActivity.this.listView.invalidateViews();
                  return;
                  if (paramAnonymousInt == NotificationsSettingsActivity.this.groupLedRow)
                  {
                    paramAnonymous2DialogInterface.putInt("GroupLed", 0);
                    localTextColorCell.setTextAndColor(LocaleController.getString("LedColor", 2131165819), 0, true);
                  }
                }
              }
            });
            NotificationsSettingsActivity.this.showDialog(((AlertDialog.Builder)localObject2).create());
            bool1 = bool2;
            break;
            if (paramAnonymousInt == NotificationsSettingsActivity.this.groupLedRow) {
              ((ColorPickerView)localObject1).setOldCenterColor(((SharedPreferences)localObject2).getInt("GroupLed", -16711936));
            }
          }
          if ((paramAnonymousInt == NotificationsSettingsActivity.this.messagePopupNotificationRow) || (paramAnonymousInt == NotificationsSettingsActivity.this.groupPopupNotificationRow))
          {
            paramAnonymousAdapterView = new AlertDialog.Builder(NotificationsSettingsActivity.this.getParentActivity());
            paramAnonymousAdapterView.setTitle(LocaleController.getString("PopupNotification", 2131166138));
            localObject1 = LocaleController.getString("NoPopup", 2131165945);
            localObject2 = LocaleController.getString("OnlyWhenScreenOn", 2131166055);
            str1 = LocaleController.getString("OnlyWhenScreenOff", 2131166054);
            localObject3 = LocaleController.getString("AlwaysShowPopup", 2131165286);
            localObject4 = new DialogInterface.OnClickListener()
            {
              public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
              {
                paramAnonymous2DialogInterface = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
                if (paramAnonymousInt == NotificationsSettingsActivity.this.messagePopupNotificationRow) {
                  paramAnonymous2DialogInterface.putInt("popupAll", paramAnonymous2Int);
                }
                for (;;)
                {
                  paramAnonymous2DialogInterface.commit();
                  if (NotificationsSettingsActivity.this.listView != null) {
                    NotificationsSettingsActivity.this.listView.invalidateViews();
                  }
                  return;
                  if (paramAnonymousInt == NotificationsSettingsActivity.this.groupPopupNotificationRow) {
                    paramAnonymous2DialogInterface.putInt("popupGroup", paramAnonymous2Int);
                  }
                }
              }
            };
            paramAnonymousAdapterView.setItems(new CharSequence[] { localObject1, localObject2, str1, localObject3 }, (DialogInterface.OnClickListener)localObject4);
            paramAnonymousAdapterView.setNegativeButton(LocaleController.getString("Cancel", 2131165386), null);
            NotificationsSettingsActivity.this.showDialog(paramAnonymousAdapterView.create());
            bool1 = bool2;
            break label128;
          }
          if ((paramAnonymousInt == NotificationsSettingsActivity.this.messageVibrateRow) || (paramAnonymousInt == NotificationsSettingsActivity.this.groupVibrateRow))
          {
            paramAnonymousAdapterView = new AlertDialog.Builder(NotificationsSettingsActivity.this.getParentActivity());
            paramAnonymousAdapterView.setTitle(LocaleController.getString("Vibrate", 2131166382));
            localObject1 = LocaleController.getString("VibrationDisabled", 2131166384);
            localObject2 = LocaleController.getString("VibrationDefault", 2131166383);
            str1 = LocaleController.getString("Short", 2131166288);
            localObject3 = LocaleController.getString("Long", 2131165847);
            localObject4 = LocaleController.getString("OnlyIfSilent", 2131166053);
            localObject5 = new DialogInterface.OnClickListener()
            {
              public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
              {
                SharedPreferences.Editor localEditor = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
                paramAnonymous2DialogInterface = "vibrate_messages";
                if (paramAnonymousInt == NotificationsSettingsActivity.this.groupVibrateRow) {
                  paramAnonymous2DialogInterface = "vibrate_group";
                }
                if (paramAnonymous2Int == 0) {
                  localEditor.putInt(paramAnonymous2DialogInterface, 2);
                }
                for (;;)
                {
                  localEditor.commit();
                  if (NotificationsSettingsActivity.this.listView != null) {
                    NotificationsSettingsActivity.this.listView.invalidateViews();
                  }
                  return;
                  if (paramAnonymous2Int == 1) {
                    localEditor.putInt(paramAnonymous2DialogInterface, 0);
                  } else if (paramAnonymous2Int == 2) {
                    localEditor.putInt(paramAnonymous2DialogInterface, 1);
                  } else if (paramAnonymous2Int == 3) {
                    localEditor.putInt(paramAnonymous2DialogInterface, 3);
                  } else if (paramAnonymous2Int == 4) {
                    localEditor.putInt(paramAnonymous2DialogInterface, 4);
                  }
                }
              }
            };
            paramAnonymousAdapterView.setItems(new CharSequence[] { localObject1, localObject2, str1, localObject3, localObject4 }, (DialogInterface.OnClickListener)localObject5);
            paramAnonymousAdapterView.setNegativeButton(LocaleController.getString("Cancel", 2131165386), null);
            NotificationsSettingsActivity.this.showDialog(paramAnonymousAdapterView.create());
            bool1 = bool2;
            break label128;
          }
          if ((paramAnonymousInt == NotificationsSettingsActivity.this.messagePriorityRow) || (paramAnonymousInt == NotificationsSettingsActivity.this.groupPriorityRow))
          {
            paramAnonymousAdapterView = new AlertDialog.Builder(NotificationsSettingsActivity.this.getParentActivity());
            paramAnonymousAdapterView.setTitle(LocaleController.getString("NotificationsPriority", 2131166034));
            localObject1 = LocaleController.getString("NotificationsPriorityDefault", 2131166035);
            localObject2 = LocaleController.getString("NotificationsPriorityHigh", 2131166036);
            str1 = LocaleController.getString("NotificationsPriorityMax", 2131166038);
            localObject3 = new DialogInterface.OnClickListener()
            {
              public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
              {
                paramAnonymous2DialogInterface = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
                if (paramAnonymousInt == NotificationsSettingsActivity.this.messagePriorityRow) {
                  paramAnonymous2DialogInterface.edit().putInt("priority_messages", paramAnonymous2Int).commit();
                }
                for (;;)
                {
                  if (NotificationsSettingsActivity.this.listView != null) {
                    NotificationsSettingsActivity.this.listView.invalidateViews();
                  }
                  return;
                  if (paramAnonymousInt == NotificationsSettingsActivity.this.groupPriorityRow) {
                    paramAnonymous2DialogInterface.edit().putInt("priority_group", paramAnonymous2Int).commit();
                  }
                }
              }
            };
            paramAnonymousAdapterView.setItems(new CharSequence[] { localObject1, localObject2, str1 }, (DialogInterface.OnClickListener)localObject3);
            paramAnonymousAdapterView.setNegativeButton(LocaleController.getString("Cancel", 2131165386), null);
            NotificationsSettingsActivity.this.showDialog(paramAnonymousAdapterView.create());
            bool1 = bool2;
            break label128;
          }
          bool1 = bool2;
          if (paramAnonymousInt != NotificationsSettingsActivity.this.repeatRow) {
            break label128;
          }
          paramAnonymousAdapterView = new AlertDialog.Builder(NotificationsSettingsActivity.this.getParentActivity());
          paramAnonymousAdapterView.setTitle(LocaleController.getString("RepeatNotifications", 2131166154));
          localObject1 = LocaleController.getString("RepeatDisabled", 2131166153);
          localObject2 = LocaleController.formatPluralString("Minutes", 5);
          String str1 = LocaleController.formatPluralString("Minutes", 10);
          Object localObject3 = LocaleController.formatPluralString("Minutes", 30);
          Object localObject4 = LocaleController.formatPluralString("Hours", 1);
          Object localObject5 = LocaleController.formatPluralString("Hours", 2);
          String str2 = LocaleController.formatPluralString("Hours", 4);
          DialogInterface.OnClickListener local7 = new DialogInterface.OnClickListener()
          {
            public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
            {
              int i = 0;
              if (paramAnonymous2Int == 1) {
                i = 5;
              }
              for (;;)
              {
                ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit().putInt("repeat_messages", i).commit();
                if (NotificationsSettingsActivity.this.listView != null) {
                  NotificationsSettingsActivity.this.listView.invalidateViews();
                }
                return;
                if (paramAnonymous2Int == 2) {
                  i = 10;
                } else if (paramAnonymous2Int == 3) {
                  i = 30;
                } else if (paramAnonymous2Int == 4) {
                  i = 60;
                } else if (paramAnonymous2Int == 5) {
                  i = 120;
                } else if (paramAnonymous2Int == 6) {
                  i = 240;
                }
              }
            }
          };
          paramAnonymousAdapterView.setItems(new CharSequence[] { localObject1, localObject2, str1, localObject3, localObject4, localObject5, str2 }, local7);
          paramAnonymousAdapterView.setNegativeButton(LocaleController.getString("Cancel", 2131165386), null);
          NotificationsSettingsActivity.this.showDialog(paramAnonymousAdapterView.create());
          bool1 = bool2;
          break label128;
        }
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
    Uri localUri;
    SharedPreferences.Editor localEditor;
    Ringtone localRingtone;
    if (paramInt2 == -1)
    {
      localUri = (Uri)paramIntent.getParcelableExtra("android.intent.extra.ringtone.PICKED_URI");
      localEditor = null;
      paramIntent = localEditor;
      if (localUri != null)
      {
        localRingtone = RingtoneManager.getRingtone(getParentActivity(), localUri);
        paramIntent = localEditor;
        if (localRingtone != null)
        {
          if (!localUri.equals(Settings.System.DEFAULT_NOTIFICATION_URI)) {
            break label151;
          }
          paramIntent = LocaleController.getString("SoundDefault", 2131166306);
          localRingtone.stop();
        }
      }
      localEditor = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
      if (paramInt1 != this.messageSoundRow) {
        break label195;
      }
      if ((paramIntent == null) || (localUri == null)) {
        break label164;
      }
      localEditor.putString("GlobalSound", paramIntent);
      localEditor.putString("GlobalSoundPath", localUri.toString());
    }
    for (;;)
    {
      localEditor.commit();
      this.listView.invalidateViews();
      return;
      label151:
      paramIntent = localRingtone.getTitle(getParentActivity());
      break;
      label164:
      localEditor.putString("GlobalSound", "NoSound");
      localEditor.putString("GlobalSoundPath", "NoSound");
      continue;
      label195:
      if (paramInt1 == this.groupSoundRow) {
        if ((paramIntent != null) && (localUri != null))
        {
          localEditor.putString("GroupSound", paramIntent);
          localEditor.putString("GroupSoundPath", localUri.toString());
        }
        else
        {
          localEditor.putString("GroupSound", "NoSound");
          localEditor.putString("GroupSoundPath", "NoSound");
        }
      }
    }
  }
  
  public boolean onFragmentCreate()
  {
    int i = this.rowCount;
    this.rowCount = (i + 1);
    this.messageSectionRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.messageAlertRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.messagePreviewRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.messageLedRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.messageVibrateRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.messagePopupNotificationRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.messageSoundRow = i;
    if (Build.VERSION.SDK_INT >= 21)
    {
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.messagePriorityRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.groupSectionRow2 = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.groupSectionRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.groupAlertRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.groupPreviewRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.groupLedRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.groupVibrateRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.groupPopupNotificationRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.groupSoundRow = i;
      if (Build.VERSION.SDK_INT < 21) {
        break label681;
      }
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.groupPriorityRow = i;
      label305:
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.inappSectionRow2 = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.inappSectionRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.inappSoundRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.inappVibrateRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.inappPreviewRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.inchatSoundRow = i;
      if (Build.VERSION.SDK_INT < 21) {
        break label689;
      }
      i = this.rowCount;
      this.rowCount = (i + 1);
    }
    label681:
    label689:
    for (this.inappPriorityRow = i;; this.inappPriorityRow = -1)
    {
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.eventsSectionRow2 = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.eventsSectionRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.contactJoinedRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.pinnedMessageRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.otherSectionRow2 = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.otherSectionRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.notificationsServiceRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.notificationsServiceConnectionRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.badgeNumberRow = i;
      this.androidAutoAlertRow = -1;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.repeatRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.resetSectionRow2 = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.resetSectionRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.resetNotificationsRow = i;
      NotificationCenter.getInstance().addObserver(this, NotificationCenter.notificationsSettingsUpdated);
      return super.onFragmentCreate();
      this.messagePriorityRow = -1;
      break;
      this.groupPriorityRow = -1;
      break label305;
    }
  }
  
  public void onFragmentDestroy()
  {
    super.onFragmentDestroy();
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.notificationsSettingsUpdated);
  }
  
  public void updateServerNotificationsSettings(boolean paramBoolean) {}
  
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
      return false;
    }
    
    public int getCount()
    {
      return NotificationsSettingsActivity.this.rowCount;
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
      if ((paramInt == NotificationsSettingsActivity.this.messageSectionRow) || (paramInt == NotificationsSettingsActivity.this.groupSectionRow) || (paramInt == NotificationsSettingsActivity.this.inappSectionRow) || (paramInt == NotificationsSettingsActivity.this.eventsSectionRow) || (paramInt == NotificationsSettingsActivity.this.otherSectionRow) || (paramInt == NotificationsSettingsActivity.this.resetSectionRow)) {
        return 0;
      }
      if ((paramInt == NotificationsSettingsActivity.this.messageAlertRow) || (paramInt == NotificationsSettingsActivity.this.messagePreviewRow) || (paramInt == NotificationsSettingsActivity.this.groupAlertRow) || (paramInt == NotificationsSettingsActivity.this.groupPreviewRow) || (paramInt == NotificationsSettingsActivity.this.inappSoundRow) || (paramInt == NotificationsSettingsActivity.this.inappVibrateRow) || (paramInt == NotificationsSettingsActivity.this.inappPreviewRow) || (paramInt == NotificationsSettingsActivity.this.contactJoinedRow) || (paramInt == NotificationsSettingsActivity.this.pinnedMessageRow) || (paramInt == NotificationsSettingsActivity.this.notificationsServiceRow) || (paramInt == NotificationsSettingsActivity.this.badgeNumberRow) || (paramInt == NotificationsSettingsActivity.this.inappPriorityRow) || (paramInt == NotificationsSettingsActivity.this.inchatSoundRow) || (paramInt == NotificationsSettingsActivity.this.androidAutoAlertRow) || (paramInt == NotificationsSettingsActivity.this.notificationsServiceConnectionRow)) {
        return 1;
      }
      if ((paramInt == NotificationsSettingsActivity.this.messageLedRow) || (paramInt == NotificationsSettingsActivity.this.groupLedRow)) {
        return 3;
      }
      if ((paramInt == NotificationsSettingsActivity.this.eventsSectionRow2) || (paramInt == NotificationsSettingsActivity.this.groupSectionRow2) || (paramInt == NotificationsSettingsActivity.this.inappSectionRow2) || (paramInt == NotificationsSettingsActivity.this.otherSectionRow2) || (paramInt == NotificationsSettingsActivity.this.resetSectionRow2)) {
        return 4;
      }
      return 2;
    }
    
    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      int i = getItemViewType(paramInt);
      paramViewGroup = paramView;
      Object localObject1;
      Object localObject2;
      if (i == 0)
      {
        localObject1 = paramView;
        if (paramView == null) {
          localObject1 = new HeaderCell(this.mContext);
        }
        if (paramInt == NotificationsSettingsActivity.this.messageSectionRow)
        {
          ((HeaderCell)localObject1).setText(LocaleController.getString("MessageNotifications", 2131165877));
          paramViewGroup = (ViewGroup)localObject1;
        }
      }
      else
      {
        if (i != 1) {
          break label827;
        }
        localObject1 = paramViewGroup;
        if (paramViewGroup == null) {
          localObject1 = new TextCheckCell(this.mContext);
        }
        paramViewGroup = (TextCheckCell)localObject1;
        localObject2 = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
        if (paramInt != NotificationsSettingsActivity.this.messageAlertRow) {
          break label307;
        }
        paramViewGroup.setTextAndCheck(LocaleController.getString("Alert", 2131165275), ((SharedPreferences)localObject2).getBoolean("EnableAll", true), true);
        paramView = (View)localObject1;
      }
      label307:
      label827:
      label1162:
      label1460:
      label1578:
      label1731:
      label1859:
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
                do
                {
                  do
                  {
                    return paramView;
                    if (paramInt == NotificationsSettingsActivity.this.groupSectionRow)
                    {
                      ((HeaderCell)localObject1).setText(LocaleController.getString("GroupNotifications", 2131165719));
                      paramViewGroup = (ViewGroup)localObject1;
                      break;
                    }
                    if (paramInt == NotificationsSettingsActivity.this.inappSectionRow)
                    {
                      ((HeaderCell)localObject1).setText(LocaleController.getString("InAppNotifications", 2131165746));
                      paramViewGroup = (ViewGroup)localObject1;
                      break;
                    }
                    if (paramInt == NotificationsSettingsActivity.this.eventsSectionRow)
                    {
                      ((HeaderCell)localObject1).setText(LocaleController.getString("Events", 2131165627));
                      paramViewGroup = (ViewGroup)localObject1;
                      break;
                    }
                    if (paramInt == NotificationsSettingsActivity.this.otherSectionRow)
                    {
                      ((HeaderCell)localObject1).setText(LocaleController.getString("NotificationsOther", 2131166033));
                      paramViewGroup = (ViewGroup)localObject1;
                      break;
                    }
                    paramViewGroup = (ViewGroup)localObject1;
                    if (paramInt != NotificationsSettingsActivity.this.resetSectionRow) {
                      break;
                    }
                    ((HeaderCell)localObject1).setText(LocaleController.getString("Reset", 2131166170));
                    paramViewGroup = (ViewGroup)localObject1;
                    break;
                    if (paramInt == NotificationsSettingsActivity.this.groupAlertRow)
                    {
                      paramViewGroup.setTextAndCheck(LocaleController.getString("Alert", 2131165275), ((SharedPreferences)localObject2).getBoolean("EnableGroup", true), true);
                      return (View)localObject1;
                    }
                    if (paramInt == NotificationsSettingsActivity.this.messagePreviewRow)
                    {
                      paramViewGroup.setTextAndCheck(LocaleController.getString("MessagePreview", 2131165878), ((SharedPreferences)localObject2).getBoolean("EnablePreviewAll", true), true);
                      return (View)localObject1;
                    }
                    if (paramInt == NotificationsSettingsActivity.this.groupPreviewRow)
                    {
                      paramViewGroup.setTextAndCheck(LocaleController.getString("MessagePreview", 2131165878), ((SharedPreferences)localObject2).getBoolean("EnablePreviewGroup", true), true);
                      return (View)localObject1;
                    }
                    if (paramInt == NotificationsSettingsActivity.this.inappSoundRow)
                    {
                      paramViewGroup.setTextAndCheck(LocaleController.getString("InAppSounds", 2131165748), ((SharedPreferences)localObject2).getBoolean("EnableInAppSounds", true), true);
                      return (View)localObject1;
                    }
                    if (paramInt == NotificationsSettingsActivity.this.inappVibrateRow)
                    {
                      paramViewGroup.setTextAndCheck(LocaleController.getString("InAppVibrate", 2131165749), ((SharedPreferences)localObject2).getBoolean("EnableInAppVibrate", true), true);
                      return (View)localObject1;
                    }
                    if (paramInt == NotificationsSettingsActivity.this.inappPreviewRow)
                    {
                      paramViewGroup.setTextAndCheck(LocaleController.getString("InAppPreview", 2131165747), ((SharedPreferences)localObject2).getBoolean("EnableInAppPreview", true), true);
                      return (View)localObject1;
                    }
                    if (paramInt == NotificationsSettingsActivity.this.inappPriorityRow)
                    {
                      paramViewGroup.setTextAndCheck(LocaleController.getString("NotificationsPriority", 2131166034), ((SharedPreferences)localObject2).getBoolean("EnableInAppPriority", false), false);
                      return (View)localObject1;
                    }
                    if (paramInt == NotificationsSettingsActivity.this.contactJoinedRow)
                    {
                      paramViewGroup.setTextAndCheck(LocaleController.getString("ContactJoined", 2131165520), ((SharedPreferences)localObject2).getBoolean("EnableContactJoined", true), true);
                      return (View)localObject1;
                    }
                    if (paramInt == NotificationsSettingsActivity.this.pinnedMessageRow)
                    {
                      paramViewGroup.setTextAndCheck(LocaleController.getString("PinnedMessages", 2131166126), ((SharedPreferences)localObject2).getBoolean("PinnedMessages", true), false);
                      return (View)localObject1;
                    }
                    if (paramInt == NotificationsSettingsActivity.this.androidAutoAlertRow)
                    {
                      paramViewGroup.setTextAndCheck("Android Auto", ((SharedPreferences)localObject2).getBoolean("EnableAutoNotifications", false), true);
                      return (View)localObject1;
                    }
                    if (paramInt == NotificationsSettingsActivity.this.notificationsServiceRow)
                    {
                      paramViewGroup.setTextAndValueAndCheck(LocaleController.getString("NotificationsService", 2131166039), LocaleController.getString("NotificationsServiceInfo", 2131166042), ((SharedPreferences)localObject2).getBoolean("pushService", true), true, true);
                      return (View)localObject1;
                    }
                    if (paramInt == NotificationsSettingsActivity.this.notificationsServiceConnectionRow)
                    {
                      paramViewGroup.setTextAndValueAndCheck(LocaleController.getString("NotificationsServiceConnection", 2131166040), LocaleController.getString("NotificationsServiceConnectionInfo", 2131166041), ((SharedPreferences)localObject2).getBoolean("pushConnection", true), true, true);
                      return (View)localObject1;
                    }
                    if (paramInt == NotificationsSettingsActivity.this.badgeNumberRow)
                    {
                      paramViewGroup.setTextAndCheck(LocaleController.getString("BadgeNumber", 2131165357), ((SharedPreferences)localObject2).getBoolean("badgeNumber", true), true);
                      return (View)localObject1;
                    }
                    paramView = (View)localObject1;
                  } while (paramInt != NotificationsSettingsActivity.this.inchatSoundRow);
                  paramViewGroup.setTextAndCheck(LocaleController.getString("InChatSound", 2131165750), ((SharedPreferences)localObject2).getBoolean("EnableInChatSound", true), true);
                  return (View)localObject1;
                  if (i != 2) {
                    break label1731;
                  }
                  localObject1 = paramViewGroup;
                  if (paramViewGroup == null) {
                    localObject1 = new TextDetailSettingsCell(this.mContext);
                  }
                  localObject2 = (TextDetailSettingsCell)localObject1;
                  paramViewGroup = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
                  if ((paramInt == NotificationsSettingsActivity.this.messageSoundRow) || (paramInt == NotificationsSettingsActivity.this.groupSoundRow))
                  {
                    ((TextDetailSettingsCell)localObject2).setMultilineDetail(false);
                    paramView = null;
                    if (paramInt == NotificationsSettingsActivity.this.messageSoundRow) {
                      paramView = paramViewGroup.getString("GlobalSound", LocaleController.getString("SoundDefault", 2131166306));
                    }
                    for (;;)
                    {
                      paramViewGroup = paramView;
                      if (paramView.equals("NoSound")) {
                        paramViewGroup = LocaleController.getString("NoSound", 2131165956);
                      }
                      ((TextDetailSettingsCell)localObject2).setTextAndValue(LocaleController.getString("Sound", 2131166305), paramViewGroup, true);
                      return (View)localObject1;
                      if (paramInt == NotificationsSettingsActivity.this.groupSoundRow) {
                        paramView = paramViewGroup.getString("GroupSound", LocaleController.getString("SoundDefault", 2131166306));
                      }
                    }
                  }
                  if (paramInt == NotificationsSettingsActivity.this.resetNotificationsRow)
                  {
                    ((TextDetailSettingsCell)localObject2).setMultilineDetail(true);
                    ((TextDetailSettingsCell)localObject2).setTextAndValue(LocaleController.getString("ResetAllNotifications", 2131166176), LocaleController.getString("UndoAllCustom", 2131166351), false);
                    return (View)localObject1;
                  }
                  if ((paramInt == NotificationsSettingsActivity.this.messagePopupNotificationRow) || (paramInt == NotificationsSettingsActivity.this.groupPopupNotificationRow))
                  {
                    ((TextDetailSettingsCell)localObject2).setMultilineDetail(false);
                    i = 0;
                    if (paramInt == NotificationsSettingsActivity.this.messagePopupNotificationRow)
                    {
                      i = paramViewGroup.getInt("popupAll", 0);
                      if (i != 0) {
                        break label1162;
                      }
                      paramView = LocaleController.getString("NoPopup", 2131165945);
                    }
                    for (;;)
                    {
                      ((TextDetailSettingsCell)localObject2).setTextAndValue(LocaleController.getString("PopupNotification", 2131166138), paramView, true);
                      return (View)localObject1;
                      if (paramInt != NotificationsSettingsActivity.this.groupPopupNotificationRow) {
                        break;
                      }
                      i = paramViewGroup.getInt("popupGroup", 0);
                      break;
                      if (i == 1) {
                        paramView = LocaleController.getString("OnlyWhenScreenOn", 2131166055);
                      } else if (i == 2) {
                        paramView = LocaleController.getString("OnlyWhenScreenOff", 2131166054);
                      } else {
                        paramView = LocaleController.getString("AlwaysShowPopup", 2131165286);
                      }
                    }
                  }
                  if ((paramInt != NotificationsSettingsActivity.this.messageVibrateRow) && (paramInt != NotificationsSettingsActivity.this.groupVibrateRow)) {
                    break label1460;
                  }
                  ((TextDetailSettingsCell)localObject2).setMultilineDetail(false);
                  i = 0;
                  if (paramInt == NotificationsSettingsActivity.this.messageVibrateRow) {
                    i = paramViewGroup.getInt("vibrate_messages", 0);
                  }
                  while (i == 0)
                  {
                    ((TextDetailSettingsCell)localObject2).setTextAndValue(LocaleController.getString("Vibrate", 2131166382), LocaleController.getString("VibrationDefault", 2131166383), true);
                    return (View)localObject1;
                    if (paramInt == NotificationsSettingsActivity.this.groupVibrateRow) {
                      i = paramViewGroup.getInt("vibrate_group", 0);
                    }
                  }
                  if (i == 1)
                  {
                    ((TextDetailSettingsCell)localObject2).setTextAndValue(LocaleController.getString("Vibrate", 2131166382), LocaleController.getString("Short", 2131166288), true);
                    return (View)localObject1;
                  }
                  if (i == 2)
                  {
                    ((TextDetailSettingsCell)localObject2).setTextAndValue(LocaleController.getString("Vibrate", 2131166382), LocaleController.getString("VibrationDisabled", 2131166384), true);
                    return (View)localObject1;
                  }
                  if (i == 3)
                  {
                    ((TextDetailSettingsCell)localObject2).setTextAndValue(LocaleController.getString("Vibrate", 2131166382), LocaleController.getString("Long", 2131165847), true);
                    return (View)localObject1;
                  }
                  paramView = (View)localObject1;
                } while (i != 4);
                ((TextDetailSettingsCell)localObject2).setTextAndValue(LocaleController.getString("Vibrate", 2131166382), LocaleController.getString("OnlyIfSilent", 2131166053), true);
                return (View)localObject1;
                if (paramInt == NotificationsSettingsActivity.this.repeatRow)
                {
                  ((TextDetailSettingsCell)localObject2).setMultilineDetail(false);
                  paramInt = paramViewGroup.getInt("repeat_messages", 60);
                  if (paramInt == 0) {
                    paramView = LocaleController.getString("RepeatNotificationsNever", 2131166155);
                  }
                  for (;;)
                  {
                    ((TextDetailSettingsCell)localObject2).setTextAndValue(LocaleController.getString("RepeatNotifications", 2131166154), paramView, false);
                    return (View)localObject1;
                    if (paramInt < 60) {
                      paramView = LocaleController.formatPluralString("Minutes", paramInt);
                    } else {
                      paramView = LocaleController.formatPluralString("Hours", paramInt / 60);
                    }
                  }
                }
                if (paramInt == NotificationsSettingsActivity.this.messagePriorityRow) {
                  break label1578;
                }
                paramView = (View)localObject1;
              } while (paramInt != NotificationsSettingsActivity.this.groupPriorityRow);
              ((TextDetailSettingsCell)localObject2).setMultilineDetail(false);
              i = 0;
              if (paramInt == NotificationsSettingsActivity.this.messagePriorityRow) {
                i = paramViewGroup.getInt("priority_messages", 1);
              }
              while (i == 0)
              {
                ((TextDetailSettingsCell)localObject2).setTextAndValue(LocaleController.getString("NotificationsPriority", 2131166034), LocaleController.getString("NotificationsPriorityDefault", 2131166035), false);
                return (View)localObject1;
                if (paramInt == NotificationsSettingsActivity.this.groupPriorityRow) {
                  i = paramViewGroup.getInt("priority_group", 1);
                }
              }
              if (i == 1)
              {
                ((TextDetailSettingsCell)localObject2).setTextAndValue(LocaleController.getString("NotificationsPriority", 2131166034), LocaleController.getString("NotificationsPriorityHigh", 2131166036), false);
                return (View)localObject1;
              }
              paramView = (View)localObject1;
            } while (i != 2);
            ((TextDetailSettingsCell)localObject2).setTextAndValue(LocaleController.getString("NotificationsPriority", 2131166034), LocaleController.getString("NotificationsPriorityMax", 2131166038), false);
            return (View)localObject1;
            if (i != 3) {
              break label1859;
            }
            localObject1 = paramViewGroup;
            if (paramViewGroup == null) {
              localObject1 = new TextColorCell(this.mContext);
            }
            paramViewGroup = (TextColorCell)localObject1;
            localObject2 = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
            if (paramInt == NotificationsSettingsActivity.this.messageLedRow)
            {
              paramViewGroup.setTextAndColor(LocaleController.getString("LedColor", 2131165819), ((SharedPreferences)localObject2).getInt("MessagesLed", -16711936), true);
              return (View)localObject1;
            }
            paramView = (View)localObject1;
          } while (paramInt != NotificationsSettingsActivity.this.groupLedRow);
          paramViewGroup.setTextAndColor(LocaleController.getString("LedColor", 2131165819), ((SharedPreferences)localObject2).getInt("GroupLed", -16711936), true);
          return (View)localObject1;
          paramView = paramViewGroup;
        } while (i != 4);
        paramView = paramViewGroup;
      } while (paramViewGroup != null);
      return new ShadowSectionCell(this.mContext);
    }
    
    public int getViewTypeCount()
    {
      return 5;
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
      return (paramInt != NotificationsSettingsActivity.this.messageSectionRow) && (paramInt != NotificationsSettingsActivity.this.groupSectionRow) && (paramInt != NotificationsSettingsActivity.this.inappSectionRow) && (paramInt != NotificationsSettingsActivity.this.eventsSectionRow) && (paramInt != NotificationsSettingsActivity.this.otherSectionRow) && (paramInt != NotificationsSettingsActivity.this.resetSectionRow) && (paramInt != NotificationsSettingsActivity.this.eventsSectionRow2) && (paramInt != NotificationsSettingsActivity.this.groupSectionRow2) && (paramInt != NotificationsSettingsActivity.this.inappSectionRow2) && (paramInt != NotificationsSettingsActivity.this.otherSectionRow2) && (paramInt != NotificationsSettingsActivity.this.resetSectionRow2);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/NotificationsSettingsActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */