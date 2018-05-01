package org.telegram.ui;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Paint;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Parcelable;
import android.provider.Settings.System;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.NotificationsController;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_account_resetNotifySettings;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextColorCell;
import org.telegram.ui.Cells.TextDetailSettingsCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;

public class NotificationsSettingsActivity
  extends BaseFragment
  implements NotificationCenter.NotificationCenterDelegate
{
  private ListAdapter adapter;
  private int androidAutoAlertRow;
  private int badgeNumberRow;
  private int callsRingtoneRow;
  private int callsSectionRow;
  private int callsSectionRow2;
  private int callsVibrateRow;
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
  private RecyclerListView listView;
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
    this.actionBar.setBackButtonImage(NUM);
    this.actionBar.setAllowOverlayTitle(true);
    this.actionBar.setTitle(LocaleController.getString("NotificationsAndSounds", NUM));
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
    ((FrameLayout)localObject).setBackgroundColor(Theme.getColor("windowBackgroundGray"));
    this.listView = new RecyclerListView(paramContext);
    this.listView.setItemAnimator(null);
    this.listView.setLayoutAnimation(null);
    this.listView.setLayoutManager(new LinearLayoutManager(paramContext, 1, false)
    {
      public boolean supportsPredictiveItemAnimations()
      {
        return false;
      }
    });
    this.listView.setVerticalScrollBarEnabled(false);
    ((FrameLayout)localObject).addView(this.listView, LayoutHelper.createFrame(-1, -1.0F));
    localObject = this.listView;
    paramContext = new ListAdapter(paramContext);
    this.adapter = paramContext;
    ((RecyclerListView)localObject).setAdapter(paramContext);
    this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener()
    {
      public void onItemClick(View paramAnonymousView, final int paramAnonymousInt)
      {
        boolean bool1 = false;
        boolean bool2 = false;
        boolean bool3 = false;
        Object localObject1;
        Object localObject2;
        if ((paramAnonymousInt == NotificationsSettingsActivity.this.messageAlertRow) || (paramAnonymousInt == NotificationsSettingsActivity.this.groupAlertRow))
        {
          localObject1 = MessagesController.getNotificationsSettings(NotificationsSettingsActivity.this.currentAccount);
          localObject2 = ((SharedPreferences)localObject1).edit();
          if (paramAnonymousInt == NotificationsSettingsActivity.this.messageAlertRow)
          {
            bool2 = ((SharedPreferences)localObject1).getBoolean("EnableAll", true);
            if (!bool2)
            {
              bool3 = true;
              ((SharedPreferences.Editor)localObject2).putBoolean("EnableAll", bool3);
              bool3 = bool2;
              label98:
              ((SharedPreferences.Editor)localObject2).commit();
              localObject2 = NotificationsSettingsActivity.this;
              if (paramAnonymousInt != NotificationsSettingsActivity.this.groupAlertRow) {
                break label222;
              }
              bool2 = true;
              label126:
              ((NotificationsSettingsActivity)localObject2).updateServerNotificationsSettings(bool2);
              label133:
              if ((paramAnonymousView instanceof TextCheckCell))
              {
                paramAnonymousView = (TextCheckCell)paramAnonymousView;
                if (bool3) {
                  break label2770;
                }
              }
            }
          }
        }
        label222:
        label302:
        label318:
        label422:
        label679:
        label685:
        label691:
        label699:
        label709:
        label930:
        label1237:
        label1297:
        label1455:
        label1504:
        label1747:
        label1826:
        label1938:
        label2054:
        label2125:
        label2176:
        label2182:
        label2249:
        label2301:
        label2307:
        label2447:
        label2504:
        label2557:
        label2770:
        for (bool3 = true;; bool3 = false)
        {
          paramAnonymousView.setChecked(bool3);
          do
          {
            do
            {
              do
              {
                do
                {
                  return;
                  bool3 = false;
                  break;
                  if (paramAnonymousInt != NotificationsSettingsActivity.this.groupAlertRow) {
                    break label98;
                  }
                  bool2 = ((SharedPreferences)localObject1).getBoolean("EnableGroup", true);
                  if (!bool2) {}
                  for (bool3 = true;; bool3 = false)
                  {
                    ((SharedPreferences.Editor)localObject2).putBoolean("EnableGroup", bool3);
                    bool3 = bool2;
                    break;
                  }
                  bool2 = false;
                  break label126;
                  if ((paramAnonymousInt == NotificationsSettingsActivity.this.messagePreviewRow) || (paramAnonymousInt == NotificationsSettingsActivity.this.groupPreviewRow))
                  {
                    localObject2 = MessagesController.getNotificationsSettings(NotificationsSettingsActivity.this.currentAccount);
                    localObject1 = ((SharedPreferences)localObject2).edit();
                    if (paramAnonymousInt == NotificationsSettingsActivity.this.messagePreviewRow)
                    {
                      bool2 = ((SharedPreferences)localObject2).getBoolean("EnablePreviewAll", true);
                      if (!bool2)
                      {
                        bool3 = true;
                        ((SharedPreferences.Editor)localObject1).putBoolean("EnablePreviewAll", bool3);
                        bool3 = bool2;
                        ((SharedPreferences.Editor)localObject1).commit();
                        localObject2 = NotificationsSettingsActivity.this;
                        if (paramAnonymousInt != NotificationsSettingsActivity.this.groupPreviewRow) {
                          break label422;
                        }
                      }
                    }
                    for (bool2 = true;; bool2 = false)
                    {
                      ((NotificationsSettingsActivity)localObject2).updateServerNotificationsSettings(bool2);
                      break;
                      bool3 = false;
                      break label302;
                      bool3 = bool2;
                      if (paramAnonymousInt != NotificationsSettingsActivity.this.groupPreviewRow) {
                        break label318;
                      }
                      bool2 = ((SharedPreferences)localObject2).getBoolean("EnablePreviewGroup", true);
                      if (!bool2) {}
                      for (bool3 = true;; bool3 = false)
                      {
                        ((SharedPreferences.Editor)localObject1).putBoolean("EnablePreviewGroup", bool3);
                        bool3 = bool2;
                        break;
                      }
                    }
                  }
                  if ((paramAnonymousInt == NotificationsSettingsActivity.this.messageSoundRow) || (paramAnonymousInt == NotificationsSettingsActivity.this.groupSoundRow) || (paramAnonymousInt == NotificationsSettingsActivity.this.callsRingtoneRow)) {
                    for (;;)
                    {
                      try
                      {
                        localObject4 = MessagesController.getNotificationsSettings(NotificationsSettingsActivity.this.currentAccount);
                        localObject5 = new android/content/Intent;
                        ((Intent)localObject5).<init>("android.intent.action.RINGTONE_PICKER");
                        if (paramAnonymousInt != NotificationsSettingsActivity.this.callsRingtoneRow) {
                          break label679;
                        }
                        i = 1;
                        ((Intent)localObject5).putExtra("android.intent.extra.ringtone.TYPE", i);
                        ((Intent)localObject5).putExtra("android.intent.extra.ringtone.SHOW_DEFAULT", true);
                        if (paramAnonymousInt != NotificationsSettingsActivity.this.callsRingtoneRow) {
                          break label685;
                        }
                        i = 1;
                        ((Intent)localObject5).putExtra("android.intent.extra.ringtone.DEFAULT_URI", RingtoneManager.getDefaultUri(i));
                        local6 = null;
                        localObject6 = null;
                        if (paramAnonymousInt != NotificationsSettingsActivity.this.callsRingtoneRow) {
                          break label691;
                        }
                        localObject1 = Settings.System.DEFAULT_RINGTONE_URI;
                        if (localObject1 != null) {
                          localObject6 = ((Uri)localObject1).getPath();
                        }
                        if (paramAnonymousInt != NotificationsSettingsActivity.this.messageSoundRow) {
                          break label709;
                        }
                        localObject4 = ((SharedPreferences)localObject4).getString("GlobalSoundPath", (String)localObject6);
                        localObject2 = local6;
                        if (localObject4 != null)
                        {
                          localObject2 = local6;
                          if (!((String)localObject4).equals("NoSound"))
                          {
                            if (!((String)localObject4).equals(localObject6)) {
                              break label699;
                            }
                            localObject2 = localObject1;
                          }
                        }
                        ((Intent)localObject5).putExtra("android.intent.extra.ringtone.EXISTING_URI", (Parcelable)localObject2);
                        NotificationsSettingsActivity.this.startActivityForResult((Intent)localObject5, paramAnonymousInt);
                        bool3 = bool1;
                      }
                      catch (Exception localException)
                      {
                        FileLog.e(localException);
                        bool3 = bool1;
                      }
                      break;
                      int i = 2;
                      continue;
                      i = 2;
                      continue;
                      localObject1 = Settings.System.DEFAULT_NOTIFICATION_URI;
                      continue;
                      localObject3 = Uri.parse((String)localObject4);
                      continue;
                      if (paramAnonymousInt == NotificationsSettingsActivity.this.groupSoundRow)
                      {
                        localObject4 = ((SharedPreferences)localObject4).getString("GroupSoundPath", (String)localObject6);
                        localObject3 = local6;
                        if (localObject4 != null)
                        {
                          localObject3 = local6;
                          if (!((String)localObject4).equals("NoSound")) {
                            if (((String)localObject4).equals(localObject6)) {
                              localObject3 = localObject1;
                            } else {
                              localObject3 = Uri.parse((String)localObject4);
                            }
                          }
                        }
                      }
                      else
                      {
                        localObject3 = local6;
                        if (paramAnonymousInt == NotificationsSettingsActivity.this.callsRingtoneRow)
                        {
                          localObject4 = ((SharedPreferences)localObject4).getString("CallsRingtonfePath", (String)localObject6);
                          localObject3 = local6;
                          if (localObject4 != null)
                          {
                            localObject3 = local6;
                            if (!((String)localObject4).equals("NoSound")) {
                              if (((String)localObject4).equals(localObject6)) {
                                localObject3 = localObject1;
                              } else {
                                localObject3 = Uri.parse((String)localObject4);
                              }
                            }
                          }
                        }
                      }
                    }
                  }
                  if (paramAnonymousInt != NotificationsSettingsActivity.this.resetNotificationsRow) {
                    break label930;
                  }
                } while (NotificationsSettingsActivity.this.reseting);
                NotificationsSettingsActivity.access$1102(NotificationsSettingsActivity.this, true);
                localObject3 = new TLRPC.TL_account_resetNotifySettings();
                ConnectionsManager.getInstance(NotificationsSettingsActivity.this.currentAccount).sendRequest((TLObject)localObject3, new RequestDelegate()
                {
                  public void run(TLObject paramAnonymous2TLObject, TLRPC.TL_error paramAnonymous2TL_error)
                  {
                    AndroidUtilities.runOnUIThread(new Runnable()
                    {
                      public void run()
                      {
                        MessagesController.getInstance(NotificationsSettingsActivity.this.currentAccount).enableJoined = true;
                        NotificationsSettingsActivity.access$1102(NotificationsSettingsActivity.this, false);
                        SharedPreferences.Editor localEditor = MessagesController.getNotificationsSettings(NotificationsSettingsActivity.this.currentAccount).edit();
                        localEditor.clear();
                        localEditor.commit();
                        NotificationsSettingsActivity.this.adapter.notifyDataSetChanged();
                        if (NotificationsSettingsActivity.this.getParentActivity() != null) {
                          Toast.makeText(NotificationsSettingsActivity.this.getParentActivity(), LocaleController.getString("ResetNotificationsText", NUM), 0).show();
                        }
                      }
                    });
                  }
                });
                bool3 = bool1;
                break label133;
                if (paramAnonymousInt == NotificationsSettingsActivity.this.inappSoundRow)
                {
                  localObject3 = MessagesController.getNotificationsSettings(NotificationsSettingsActivity.this.currentAccount);
                  localObject1 = ((SharedPreferences)localObject3).edit();
                  bool2 = ((SharedPreferences)localObject3).getBoolean("EnableInAppSounds", true);
                  if (!bool2) {}
                  for (bool3 = true;; bool3 = false)
                  {
                    ((SharedPreferences.Editor)localObject1).putBoolean("EnableInAppSounds", bool3);
                    ((SharedPreferences.Editor)localObject1).commit();
                    bool3 = bool2;
                    break;
                  }
                }
                if (paramAnonymousInt == NotificationsSettingsActivity.this.inappVibrateRow)
                {
                  localObject1 = MessagesController.getNotificationsSettings(NotificationsSettingsActivity.this.currentAccount);
                  localObject3 = ((SharedPreferences)localObject1).edit();
                  bool2 = ((SharedPreferences)localObject1).getBoolean("EnableInAppVibrate", true);
                  if (!bool2) {}
                  for (bool3 = true;; bool3 = false)
                  {
                    ((SharedPreferences.Editor)localObject3).putBoolean("EnableInAppVibrate", bool3);
                    ((SharedPreferences.Editor)localObject3).commit();
                    bool3 = bool2;
                    break;
                  }
                }
                if (paramAnonymousInt == NotificationsSettingsActivity.this.inappPreviewRow)
                {
                  localObject1 = MessagesController.getNotificationsSettings(NotificationsSettingsActivity.this.currentAccount);
                  localObject3 = ((SharedPreferences)localObject1).edit();
                  bool2 = ((SharedPreferences)localObject1).getBoolean("EnableInAppPreview", true);
                  if (!bool2) {}
                  for (bool3 = true;; bool3 = false)
                  {
                    ((SharedPreferences.Editor)localObject3).putBoolean("EnableInAppPreview", bool3);
                    ((SharedPreferences.Editor)localObject3).commit();
                    bool3 = bool2;
                    break;
                  }
                }
                if (paramAnonymousInt == NotificationsSettingsActivity.this.inchatSoundRow)
                {
                  localObject3 = MessagesController.getNotificationsSettings(NotificationsSettingsActivity.this.currentAccount);
                  localObject1 = ((SharedPreferences)localObject3).edit();
                  bool2 = ((SharedPreferences)localObject3).getBoolean("EnableInChatSound", true);
                  if (!bool2)
                  {
                    bool3 = true;
                    ((SharedPreferences.Editor)localObject1).putBoolean("EnableInChatSound", bool3);
                    ((SharedPreferences.Editor)localObject1).commit();
                    localObject3 = NotificationsController.getInstance(NotificationsSettingsActivity.this.currentAccount);
                    if (bool2) {
                      break label1297;
                    }
                  }
                  for (bool3 = true;; bool3 = false)
                  {
                    ((NotificationsController)localObject3).setInChatSoundEnabled(bool3);
                    bool3 = bool2;
                    break;
                    bool3 = false;
                    break label1237;
                  }
                }
                if (paramAnonymousInt == NotificationsSettingsActivity.this.inappPriorityRow)
                {
                  localObject1 = MessagesController.getNotificationsSettings(NotificationsSettingsActivity.this.currentAccount);
                  localObject3 = ((SharedPreferences)localObject1).edit();
                  bool2 = ((SharedPreferences)localObject1).getBoolean("EnableInAppPriority", false);
                  if (!bool2) {}
                  for (bool3 = true;; bool3 = false)
                  {
                    ((SharedPreferences.Editor)localObject3).putBoolean("EnableInAppPriority", bool3);
                    ((SharedPreferences.Editor)localObject3).commit();
                    bool3 = bool2;
                    break;
                  }
                }
                if (paramAnonymousInt == NotificationsSettingsActivity.this.contactJoinedRow)
                {
                  localObject1 = MessagesController.getNotificationsSettings(NotificationsSettingsActivity.this.currentAccount);
                  localObject3 = ((SharedPreferences)localObject1).edit();
                  bool2 = ((SharedPreferences)localObject1).getBoolean("EnableContactJoined", true);
                  localObject1 = MessagesController.getInstance(NotificationsSettingsActivity.this.currentAccount);
                  if (!bool2)
                  {
                    bool3 = true;
                    ((MessagesController)localObject1).enableJoined = bool3;
                    if (bool2) {
                      break label1504;
                    }
                  }
                  for (bool3 = true;; bool3 = false)
                  {
                    ((SharedPreferences.Editor)localObject3).putBoolean("EnableContactJoined", bool3);
                    ((SharedPreferences.Editor)localObject3).commit();
                    bool3 = bool2;
                    break;
                    bool3 = false;
                    break label1455;
                  }
                }
                if (paramAnonymousInt == NotificationsSettingsActivity.this.pinnedMessageRow)
                {
                  localObject3 = MessagesController.getNotificationsSettings(NotificationsSettingsActivity.this.currentAccount);
                  localObject1 = ((SharedPreferences)localObject3).edit();
                  bool2 = ((SharedPreferences)localObject3).getBoolean("PinnedMessages", true);
                  if (!bool2) {}
                  for (bool3 = true;; bool3 = false)
                  {
                    ((SharedPreferences.Editor)localObject1).putBoolean("PinnedMessages", bool3);
                    ((SharedPreferences.Editor)localObject1).commit();
                    bool3 = bool2;
                    break;
                  }
                }
                if (paramAnonymousInt == NotificationsSettingsActivity.this.androidAutoAlertRow)
                {
                  localObject3 = MessagesController.getNotificationsSettings(NotificationsSettingsActivity.this.currentAccount);
                  localObject1 = ((SharedPreferences)localObject3).edit();
                  bool2 = ((SharedPreferences)localObject3).getBoolean("EnableAutoNotifications", false);
                  if (!bool2) {}
                  for (bool3 = true;; bool3 = false)
                  {
                    ((SharedPreferences.Editor)localObject1).putBoolean("EnableAutoNotifications", bool3);
                    ((SharedPreferences.Editor)localObject1).commit();
                    bool3 = bool2;
                    break;
                  }
                }
                if (paramAnonymousInt == NotificationsSettingsActivity.this.badgeNumberRow)
                {
                  localObject3 = MessagesController.getNotificationsSettings(NotificationsSettingsActivity.this.currentAccount).edit();
                  bool2 = NotificationsController.getInstance(NotificationsSettingsActivity.this.currentAccount).showBadgeNumber;
                  localObject1 = NotificationsController.getInstance(NotificationsSettingsActivity.this.currentAccount);
                  if (!bool2)
                  {
                    bool3 = true;
                    ((NotificationsController)localObject1).showBadgeNumber = bool3;
                    ((SharedPreferences.Editor)localObject3).putBoolean("badgeNumber", NotificationsController.getInstance(NotificationsSettingsActivity.this.currentAccount).showBadgeNumber);
                    ((SharedPreferences.Editor)localObject3).commit();
                    localObject3 = NotificationsController.getInstance(NotificationsSettingsActivity.this.currentAccount);
                    if (bool2) {
                      break label1826;
                    }
                  }
                  for (bool3 = true;; bool3 = false)
                  {
                    ((NotificationsController)localObject3).setBadgeEnabled(bool3);
                    bool3 = bool2;
                    break;
                    bool3 = false;
                    break label1747;
                  }
                }
                if (paramAnonymousInt == NotificationsSettingsActivity.this.notificationsServiceConnectionRow)
                {
                  localObject3 = MessagesController.getNotificationsSettings(NotificationsSettingsActivity.this.currentAccount);
                  bool2 = ((SharedPreferences)localObject3).getBoolean("pushConnection", true);
                  localObject3 = ((SharedPreferences)localObject3).edit();
                  if (!bool2) {}
                  for (bool3 = true;; bool3 = false)
                  {
                    ((SharedPreferences.Editor)localObject3).putBoolean("pushConnection", bool3);
                    ((SharedPreferences.Editor)localObject3).commit();
                    if (bool2) {
                      break label1938;
                    }
                    ConnectionsManager.getInstance(NotificationsSettingsActivity.this.currentAccount).setPushConnectionEnabled(true);
                    bool3 = bool2;
                    break;
                  }
                  ConnectionsManager.getInstance(NotificationsSettingsActivity.this.currentAccount).setPushConnectionEnabled(false);
                  bool3 = bool2;
                  break label133;
                }
                if (paramAnonymousInt == NotificationsSettingsActivity.this.notificationsServiceRow)
                {
                  localObject3 = MessagesController.getNotificationsSettings(NotificationsSettingsActivity.this.currentAccount);
                  bool2 = ((SharedPreferences)localObject3).getBoolean("pushService", true);
                  localObject3 = ((SharedPreferences)localObject3).edit();
                  if (!bool2) {}
                  for (bool3 = true;; bool3 = false)
                  {
                    ((SharedPreferences.Editor)localObject3).putBoolean("pushService", bool3);
                    ((SharedPreferences.Editor)localObject3).commit();
                    if (bool2) {
                      break label2054;
                    }
                    ApplicationLoader.startPushService();
                    bool3 = bool2;
                    break;
                  }
                  ApplicationLoader.stopPushService();
                  bool3 = bool2;
                  break label133;
                }
                if ((paramAnonymousInt != NotificationsSettingsActivity.this.messageLedRow) && (paramAnonymousInt != NotificationsSettingsActivity.this.groupLedRow)) {
                  break label2182;
                }
              } while (NotificationsSettingsActivity.this.getParentActivity() == null);
              localObject1 = NotificationsSettingsActivity.this;
              localObject3 = NotificationsSettingsActivity.this.getParentActivity();
              if (paramAnonymousInt == NotificationsSettingsActivity.this.groupLedRow)
              {
                bool3 = true;
                if (paramAnonymousInt != NotificationsSettingsActivity.this.messageLedRow) {
                  break label2176;
                }
              }
              for (bool2 = true;; bool2 = false)
              {
                ((NotificationsSettingsActivity)localObject1).showDialog(AlertsCreator.createColorSelectDialog((Activity)localObject3, 0L, bool3, bool2, new Runnable()
                {
                  public void run()
                  {
                    NotificationsSettingsActivity.this.adapter.notifyItemChanged(paramAnonymousInt);
                  }
                }));
                bool3 = bool1;
                break;
                bool3 = false;
                break label2125;
              }
              if ((paramAnonymousInt != NotificationsSettingsActivity.this.messagePopupNotificationRow) && (paramAnonymousInt != NotificationsSettingsActivity.this.groupPopupNotificationRow)) {
                break label2307;
              }
            } while (NotificationsSettingsActivity.this.getParentActivity() == null);
            localObject1 = NotificationsSettingsActivity.this;
            localObject6 = NotificationsSettingsActivity.this.getParentActivity();
            localObject3 = NotificationsSettingsActivity.this;
            if (paramAnonymousInt == NotificationsSettingsActivity.this.groupPopupNotificationRow)
            {
              bool3 = true;
              if (paramAnonymousInt != NotificationsSettingsActivity.this.messagePopupNotificationRow) {
                break label2301;
              }
            }
            for (bool2 = true;; bool2 = false)
            {
              ((NotificationsSettingsActivity)localObject1).showDialog(AlertsCreator.createPopupSelectDialog((Activity)localObject6, (BaseFragment)localObject3, bool3, bool2, new Runnable()
              {
                public void run()
                {
                  NotificationsSettingsActivity.this.adapter.notifyItemChanged(paramAnonymousInt);
                }
              }));
              bool3 = bool1;
              break;
              bool3 = false;
              break label2249;
            }
            if ((paramAnonymousInt != NotificationsSettingsActivity.this.messageVibrateRow) && (paramAnonymousInt != NotificationsSettingsActivity.this.groupVibrateRow) && (paramAnonymousInt != NotificationsSettingsActivity.this.callsVibrateRow)) {
              break label2447;
            }
          } while (NotificationsSettingsActivity.this.getParentActivity() == null);
          Object localObject3 = null;
          if (paramAnonymousInt == NotificationsSettingsActivity.this.messageVibrateRow) {
            localObject3 = "vibrate_messages";
          }
          for (;;)
          {
            NotificationsSettingsActivity.this.showDialog(AlertsCreator.createVibrationSelectDialog(NotificationsSettingsActivity.this.getParentActivity(), NotificationsSettingsActivity.this, 0L, (String)localObject3, new Runnable()
            {
              public void run()
              {
                NotificationsSettingsActivity.this.adapter.notifyItemChanged(paramAnonymousInt);
              }
            }));
            bool3 = bool1;
            break;
            if (paramAnonymousInt == NotificationsSettingsActivity.this.groupVibrateRow) {
              localObject3 = "vibrate_group";
            } else if (paramAnonymousInt == NotificationsSettingsActivity.this.callsVibrateRow) {
              localObject3 = "vibrate_calls";
            }
          }
          if ((paramAnonymousInt == NotificationsSettingsActivity.this.messagePriorityRow) || (paramAnonymousInt == NotificationsSettingsActivity.this.groupPriorityRow))
          {
            localObject6 = NotificationsSettingsActivity.this;
            localObject3 = NotificationsSettingsActivity.this.getParentActivity();
            localObject1 = NotificationsSettingsActivity.this;
            if (paramAnonymousInt == NotificationsSettingsActivity.this.groupPriorityRow)
            {
              bool3 = true;
              if (paramAnonymousInt != NotificationsSettingsActivity.this.messagePriorityRow) {
                break label2557;
              }
            }
            for (bool2 = true;; bool2 = false)
            {
              ((NotificationsSettingsActivity)localObject6).showDialog(AlertsCreator.createPrioritySelectDialog((Activity)localObject3, (BaseFragment)localObject1, 0L, bool3, bool2, new Runnable()
              {
                public void run()
                {
                  NotificationsSettingsActivity.this.adapter.notifyItemChanged(paramAnonymousInt);
                }
              }));
              bool3 = bool1;
              break;
              bool3 = false;
              break label2504;
            }
          }
          bool3 = bool1;
          if (paramAnonymousInt != NotificationsSettingsActivity.this.repeatRow) {
            break label133;
          }
          AlertDialog.Builder localBuilder = new AlertDialog.Builder(NotificationsSettingsActivity.this.getParentActivity());
          localBuilder.setTitle(LocaleController.getString("RepeatNotifications", NUM));
          String str1 = LocaleController.getString("RepeatDisabled", NUM);
          Object localObject6 = LocaleController.formatPluralString("Minutes", 5);
          Object localObject4 = LocaleController.formatPluralString("Minutes", 10);
          String str2 = LocaleController.formatPluralString("Minutes", 30);
          localObject1 = LocaleController.formatPluralString("Hours", 1);
          localObject3 = LocaleController.formatPluralString("Hours", 2);
          Object localObject5 = LocaleController.formatPluralString("Hours", 4);
          DialogInterface.OnClickListener local6 = new DialogInterface.OnClickListener()
          {
            public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
            {
              int i = 0;
              if (paramAnonymous2Int == 1) {
                i = 5;
              }
              for (;;)
              {
                MessagesController.getNotificationsSettings(NotificationsSettingsActivity.this.currentAccount).edit().putInt("repeat_messages", i).commit();
                NotificationsSettingsActivity.this.adapter.notifyItemChanged(paramAnonymousInt);
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
          localBuilder.setItems(new CharSequence[] { str1, localObject6, localObject4, str2, localObject1, localObject3, localObject5 }, local6);
          localBuilder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
          NotificationsSettingsActivity.this.showDialog(localBuilder.create());
          bool3 = bool1;
          break label133;
        }
      }
    });
    return this.fragmentView;
  }
  
  public void didReceivedNotification(int paramInt1, int paramInt2, Object... paramVarArgs)
  {
    if (paramInt1 == NotificationCenter.notificationsSettingsUpdated) {
      this.adapter.notifyDataSetChanged();
    }
  }
  
  public ThemeDescription[] getThemeDescriptions()
  {
    ThemeDescription localThemeDescription1 = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[] { HeaderCell.class, TextCheckCell.class, TextDetailSettingsCell.class, TextColorCell.class, TextSettingsCell.class }, null, null, null, "windowBackgroundWhite");
    ThemeDescription localThemeDescription2 = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundGray");
    ThemeDescription localThemeDescription3 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
    ThemeDescription localThemeDescription4 = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault");
    ThemeDescription localThemeDescription5 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
    ThemeDescription localThemeDescription6 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
    ThemeDescription localThemeDescription7 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
    ThemeDescription localThemeDescription8 = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
    RecyclerListView localRecyclerListView = this.listView;
    Paint localPaint = Theme.dividerPaint;
    return new ThemeDescription[] { localThemeDescription1, localThemeDescription2, localThemeDescription3, localThemeDescription4, localThemeDescription5, localThemeDescription6, localThemeDescription7, localThemeDescription8, new ThemeDescription(localRecyclerListView, 0, new Class[] { View.class }, localPaint, null, null, "divider"), new ThemeDescription(this.listView, 0, new Class[] { HeaderCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlueHeader"), new ThemeDescription(this.listView, 0, new Class[] { TextCheckCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.listView, 0, new Class[] { TextCheckCell.class }, new String[] { "valueTextView" }, null, null, null, "windowBackgroundWhiteGrayText2"), new ThemeDescription(this.listView, 0, new Class[] { TextCheckCell.class }, new String[] { "checkBox" }, null, null, null, "switchThumb"), new ThemeDescription(this.listView, 0, new Class[] { TextCheckCell.class }, new String[] { "checkBox" }, null, null, null, "switchTrack"), new ThemeDescription(this.listView, 0, new Class[] { TextCheckCell.class }, new String[] { "checkBox" }, null, null, null, "switchThumbChecked"), new ThemeDescription(this.listView, 0, new Class[] { TextCheckCell.class }, new String[] { "checkBox" }, null, null, null, "switchTrackChecked"), new ThemeDescription(this.listView, 0, new Class[] { TextColorCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.listView, 0, new Class[] { TextSettingsCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.listView, 0, new Class[] { TextSettingsCell.class }, new String[] { "valueTextView" }, null, null, null, "windowBackgroundWhiteValueText"), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[] { ShadowSectionCell.class }, null, null, null, "windowBackgroundGrayShadow"), new ThemeDescription(this.listView, 0, new Class[] { TextDetailSettingsCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.listView, 0, new Class[] { TextDetailSettingsCell.class }, new String[] { "valueTextView" }, null, null, null, "windowBackgroundWhiteGrayText2") };
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
          if (paramInt1 != this.callsRingtoneRow) {
            break label170;
          }
          if (!localUri.equals(Settings.System.DEFAULT_RINGTONE_URI)) {
            break label157;
          }
          paramIntent = LocaleController.getString("DefaultRingtone", NUM);
          localRingtone.stop();
        }
      }
      localEditor = MessagesController.getNotificationsSettings(this.currentAccount).edit();
      if (paramInt1 != this.messageSoundRow) {
        break label238;
      }
      if ((paramIntent == null) || (localUri == null)) {
        break label207;
      }
      localEditor.putString("GlobalSound", paramIntent);
      localEditor.putString("GlobalSoundPath", localUri.toString());
    }
    for (;;)
    {
      localEditor.commit();
      this.adapter.notifyItemChanged(paramInt1);
      return;
      label157:
      paramIntent = localRingtone.getTitle(getParentActivity());
      break;
      label170:
      if (localUri.equals(Settings.System.DEFAULT_NOTIFICATION_URI))
      {
        paramIntent = LocaleController.getString("SoundDefault", NUM);
        break;
      }
      paramIntent = localRingtone.getTitle(getParentActivity());
      break;
      label207:
      localEditor.putString("GlobalSound", "NoSound");
      localEditor.putString("GlobalSoundPath", "NoSound");
      continue;
      label238:
      if (paramInt1 == this.groupSoundRow)
      {
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
      else if (paramInt1 == this.callsRingtoneRow) {
        if ((paramIntent != null) && (localUri != null))
        {
          localEditor.putString("CallsRingtone", paramIntent);
          localEditor.putString("CallsRingtonePath", localUri.toString());
        }
        else
        {
          localEditor.putString("CallsRingtone", "NoSound");
          localEditor.putString("CallsRingtonePath", "NoSound");
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
        break label753;
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
        break label761;
      }
      i = this.rowCount;
      this.rowCount = (i + 1);
    }
    label753:
    label761:
    for (this.inappPriorityRow = i;; this.inappPriorityRow = -1)
    {
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.callsSectionRow2 = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.callsSectionRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.callsVibrateRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.callsRingtoneRow = i;
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
      NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.notificationsSettingsUpdated);
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
    NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.notificationsSettingsUpdated);
  }
  
  public void updateServerNotificationsSettings(boolean paramBoolean) {}
  
  private class ListAdapter
    extends RecyclerListView.SelectionAdapter
  {
    private Context mContext;
    
    public ListAdapter(Context paramContext)
    {
      this.mContext = paramContext;
    }
    
    public int getItemCount()
    {
      return NotificationsSettingsActivity.this.rowCount;
    }
    
    public int getItemViewType(int paramInt)
    {
      if ((paramInt == NotificationsSettingsActivity.this.messageSectionRow) || (paramInt == NotificationsSettingsActivity.this.groupSectionRow) || (paramInt == NotificationsSettingsActivity.this.inappSectionRow) || (paramInt == NotificationsSettingsActivity.this.eventsSectionRow) || (paramInt == NotificationsSettingsActivity.this.otherSectionRow) || (paramInt == NotificationsSettingsActivity.this.resetSectionRow) || (paramInt == NotificationsSettingsActivity.this.callsSectionRow)) {
        paramInt = 0;
      }
      for (;;)
      {
        return paramInt;
        if ((paramInt == NotificationsSettingsActivity.this.messageAlertRow) || (paramInt == NotificationsSettingsActivity.this.messagePreviewRow) || (paramInt == NotificationsSettingsActivity.this.groupAlertRow) || (paramInt == NotificationsSettingsActivity.this.groupPreviewRow) || (paramInt == NotificationsSettingsActivity.this.inappSoundRow) || (paramInt == NotificationsSettingsActivity.this.inappVibrateRow) || (paramInt == NotificationsSettingsActivity.this.inappPreviewRow) || (paramInt == NotificationsSettingsActivity.this.contactJoinedRow) || (paramInt == NotificationsSettingsActivity.this.pinnedMessageRow) || (paramInt == NotificationsSettingsActivity.this.notificationsServiceRow) || (paramInt == NotificationsSettingsActivity.this.badgeNumberRow) || (paramInt == NotificationsSettingsActivity.this.inappPriorityRow) || (paramInt == NotificationsSettingsActivity.this.inchatSoundRow) || (paramInt == NotificationsSettingsActivity.this.androidAutoAlertRow) || (paramInt == NotificationsSettingsActivity.this.notificationsServiceConnectionRow)) {
          paramInt = 1;
        } else if ((paramInt == NotificationsSettingsActivity.this.messageLedRow) || (paramInt == NotificationsSettingsActivity.this.groupLedRow)) {
          paramInt = 3;
        } else if ((paramInt == NotificationsSettingsActivity.this.eventsSectionRow2) || (paramInt == NotificationsSettingsActivity.this.groupSectionRow2) || (paramInt == NotificationsSettingsActivity.this.inappSectionRow2) || (paramInt == NotificationsSettingsActivity.this.otherSectionRow2) || (paramInt == NotificationsSettingsActivity.this.resetSectionRow2) || (paramInt == NotificationsSettingsActivity.this.callsSectionRow2)) {
          paramInt = 4;
        } else if (paramInt == NotificationsSettingsActivity.this.resetNotificationsRow) {
          paramInt = 2;
        } else {
          paramInt = 5;
        }
      }
    }
    
    public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
    {
      int i = paramViewHolder.getAdapterPosition();
      if ((i != NotificationsSettingsActivity.this.messageSectionRow) && (i != NotificationsSettingsActivity.this.groupSectionRow) && (i != NotificationsSettingsActivity.this.inappSectionRow) && (i != NotificationsSettingsActivity.this.eventsSectionRow) && (i != NotificationsSettingsActivity.this.otherSectionRow) && (i != NotificationsSettingsActivity.this.resetSectionRow) && (i != NotificationsSettingsActivity.this.eventsSectionRow2) && (i != NotificationsSettingsActivity.this.groupSectionRow2) && (i != NotificationsSettingsActivity.this.inappSectionRow2) && (i != NotificationsSettingsActivity.this.otherSectionRow2) && (i != NotificationsSettingsActivity.this.resetSectionRow2) && (i != NotificationsSettingsActivity.this.callsSectionRow2) && (i != NotificationsSettingsActivity.this.callsSectionRow)) {}
      for (boolean bool = true;; bool = false) {
        return bool;
      }
    }
    
    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      switch (paramViewHolder.getItemViewType())
      {
      }
      Object localObject;
      label913:
      TextSettingsCell localTextSettingsCell;
      label1188:
      label1350:
      label1673:
      do
      {
        for (;;)
        {
          return;
          paramViewHolder = (HeaderCell)paramViewHolder.itemView;
          if (paramInt == NotificationsSettingsActivity.this.messageSectionRow)
          {
            paramViewHolder.setText(LocaleController.getString("MessageNotifications", NUM));
          }
          else if (paramInt == NotificationsSettingsActivity.this.groupSectionRow)
          {
            paramViewHolder.setText(LocaleController.getString("GroupNotifications", NUM));
          }
          else if (paramInt == NotificationsSettingsActivity.this.inappSectionRow)
          {
            paramViewHolder.setText(LocaleController.getString("InAppNotifications", NUM));
          }
          else if (paramInt == NotificationsSettingsActivity.this.eventsSectionRow)
          {
            paramViewHolder.setText(LocaleController.getString("Events", NUM));
          }
          else if (paramInt == NotificationsSettingsActivity.this.otherSectionRow)
          {
            paramViewHolder.setText(LocaleController.getString("NotificationsOther", NUM));
          }
          else if (paramInt == NotificationsSettingsActivity.this.resetSectionRow)
          {
            paramViewHolder.setText(LocaleController.getString("Reset", NUM));
          }
          else if (paramInt == NotificationsSettingsActivity.this.callsSectionRow)
          {
            paramViewHolder.setText(LocaleController.getString("VoipNotificationSettings", NUM));
            continue;
            localObject = (TextCheckCell)paramViewHolder.itemView;
            paramViewHolder = MessagesController.getNotificationsSettings(NotificationsSettingsActivity.this.currentAccount);
            if (paramInt == NotificationsSettingsActivity.this.messageAlertRow)
            {
              ((TextCheckCell)localObject).setTextAndCheck(LocaleController.getString("Alert", NUM), paramViewHolder.getBoolean("EnableAll", true), true);
            }
            else if (paramInt == NotificationsSettingsActivity.this.groupAlertRow)
            {
              ((TextCheckCell)localObject).setTextAndCheck(LocaleController.getString("Alert", NUM), paramViewHolder.getBoolean("EnableGroup", true), true);
            }
            else if (paramInt == NotificationsSettingsActivity.this.messagePreviewRow)
            {
              ((TextCheckCell)localObject).setTextAndCheck(LocaleController.getString("MessagePreview", NUM), paramViewHolder.getBoolean("EnablePreviewAll", true), true);
            }
            else if (paramInt == NotificationsSettingsActivity.this.groupPreviewRow)
            {
              ((TextCheckCell)localObject).setTextAndCheck(LocaleController.getString("MessagePreview", NUM), paramViewHolder.getBoolean("EnablePreviewGroup", true), true);
            }
            else if (paramInt == NotificationsSettingsActivity.this.inappSoundRow)
            {
              ((TextCheckCell)localObject).setTextAndCheck(LocaleController.getString("InAppSounds", NUM), paramViewHolder.getBoolean("EnableInAppSounds", true), true);
            }
            else if (paramInt == NotificationsSettingsActivity.this.inappVibrateRow)
            {
              ((TextCheckCell)localObject).setTextAndCheck(LocaleController.getString("InAppVibrate", NUM), paramViewHolder.getBoolean("EnableInAppVibrate", true), true);
            }
            else if (paramInt == NotificationsSettingsActivity.this.inappPreviewRow)
            {
              ((TextCheckCell)localObject).setTextAndCheck(LocaleController.getString("InAppPreview", NUM), paramViewHolder.getBoolean("EnableInAppPreview", true), true);
            }
            else if (paramInt == NotificationsSettingsActivity.this.inappPriorityRow)
            {
              ((TextCheckCell)localObject).setTextAndCheck(LocaleController.getString("NotificationsImportance", NUM), paramViewHolder.getBoolean("EnableInAppPriority", false), false);
            }
            else if (paramInt == NotificationsSettingsActivity.this.contactJoinedRow)
            {
              ((TextCheckCell)localObject).setTextAndCheck(LocaleController.getString("ContactJoined", NUM), paramViewHolder.getBoolean("EnableContactJoined", true), true);
            }
            else if (paramInt == NotificationsSettingsActivity.this.pinnedMessageRow)
            {
              ((TextCheckCell)localObject).setTextAndCheck(LocaleController.getString("PinnedMessages", NUM), paramViewHolder.getBoolean("PinnedMessages", true), false);
            }
            else if (paramInt == NotificationsSettingsActivity.this.androidAutoAlertRow)
            {
              ((TextCheckCell)localObject).setTextAndCheck("Android Auto", paramViewHolder.getBoolean("EnableAutoNotifications", false), true);
            }
            else if (paramInt == NotificationsSettingsActivity.this.notificationsServiceRow)
            {
              ((TextCheckCell)localObject).setTextAndValueAndCheck(LocaleController.getString("NotificationsService", NUM), LocaleController.getString("NotificationsServiceInfo", NUM), paramViewHolder.getBoolean("pushService", true), true, true);
            }
            else if (paramInt == NotificationsSettingsActivity.this.notificationsServiceConnectionRow)
            {
              ((TextCheckCell)localObject).setTextAndValueAndCheck(LocaleController.getString("NotificationsServiceConnection", NUM), LocaleController.getString("NotificationsServiceConnectionInfo", NUM), paramViewHolder.getBoolean("pushConnection", true), true, true);
            }
            else if (paramInt == NotificationsSettingsActivity.this.badgeNumberRow)
            {
              ((TextCheckCell)localObject).setTextAndCheck(LocaleController.getString("BadgeNumber", NUM), NotificationsController.getInstance(NotificationsSettingsActivity.this.currentAccount).showBadgeNumber, true);
            }
            else if (paramInt == NotificationsSettingsActivity.this.inchatSoundRow)
            {
              ((TextCheckCell)localObject).setTextAndCheck(LocaleController.getString("InChatSound", NUM), paramViewHolder.getBoolean("EnableInChatSound", true), true);
            }
            else if (paramInt == NotificationsSettingsActivity.this.callsVibrateRow)
            {
              ((TextCheckCell)localObject).setTextAndCheck(LocaleController.getString("Vibrate", NUM), paramViewHolder.getBoolean("EnableCallVibrate", true), true);
              continue;
              paramViewHolder = (TextDetailSettingsCell)paramViewHolder.itemView;
              paramViewHolder.setMultilineDetail(true);
              paramViewHolder.setTextAndValue(LocaleController.getString("ResetAllNotifications", NUM), LocaleController.getString("UndoAllCustom", NUM), false);
              continue;
              paramViewHolder = (TextColorCell)paramViewHolder.itemView;
              localObject = MessagesController.getNotificationsSettings(NotificationsSettingsActivity.this.currentAccount);
              if (paramInt == NotificationsSettingsActivity.this.messageLedRow) {
                paramInt = ((SharedPreferences)localObject).getInt("MessagesLed", -16776961);
              }
              for (int i = 0;; i++)
              {
                j = paramInt;
                if (i < 9)
                {
                  if (TextColorCell.colorsToSave[i] == paramInt) {
                    j = TextColorCell.colors[i];
                  }
                }
                else
                {
                  paramViewHolder.setTextAndColor(LocaleController.getString("LedColor", NUM), j, true);
                  break;
                  paramInt = ((SharedPreferences)localObject).getInt("GroupLed", -16776961);
                  break label913;
                }
              }
              localTextSettingsCell = (TextSettingsCell)paramViewHolder.itemView;
              localObject = MessagesController.getNotificationsSettings(NotificationsSettingsActivity.this.currentAccount);
              if ((paramInt == NotificationsSettingsActivity.this.messageSoundRow) || (paramInt == NotificationsSettingsActivity.this.groupSoundRow) || (paramInt == NotificationsSettingsActivity.this.callsRingtoneRow))
              {
                paramViewHolder = null;
                if (paramInt == NotificationsSettingsActivity.this.messageSoundRow) {
                  paramViewHolder = ((SharedPreferences)localObject).getString("GlobalSound", LocaleController.getString("SoundDefault", NUM));
                }
                for (;;)
                {
                  localObject = paramViewHolder;
                  if (paramViewHolder.equals("NoSound")) {
                    localObject = LocaleController.getString("NoSound", NUM);
                  }
                  if (paramInt != NotificationsSettingsActivity.this.callsRingtoneRow) {
                    break label1188;
                  }
                  localTextSettingsCell.setTextAndValue(LocaleController.getString("VoipSettingsRingtone", NUM), (String)localObject, true);
                  break;
                  if (paramInt == NotificationsSettingsActivity.this.groupSoundRow) {
                    paramViewHolder = ((SharedPreferences)localObject).getString("GroupSound", LocaleController.getString("SoundDefault", NUM));
                  } else if (paramInt == NotificationsSettingsActivity.this.callsRingtoneRow) {
                    paramViewHolder = ((SharedPreferences)localObject).getString("CallsRingtone", LocaleController.getString("DefaultRingtone", NUM));
                  }
                }
                localTextSettingsCell.setTextAndValue(LocaleController.getString("Sound", NUM), (String)localObject, true);
              }
              else if ((paramInt == NotificationsSettingsActivity.this.messageVibrateRow) || (paramInt == NotificationsSettingsActivity.this.groupVibrateRow) || (paramInt == NotificationsSettingsActivity.this.callsVibrateRow))
              {
                j = 0;
                if (paramInt == NotificationsSettingsActivity.this.messageVibrateRow) {
                  j = ((SharedPreferences)localObject).getInt("vibrate_messages", 0);
                }
                for (;;)
                {
                  if (j != 0) {
                    break label1350;
                  }
                  localTextSettingsCell.setTextAndValue(LocaleController.getString("Vibrate", NUM), LocaleController.getString("VibrationDefault", NUM), true);
                  break;
                  if (paramInt == NotificationsSettingsActivity.this.groupVibrateRow) {
                    j = ((SharedPreferences)localObject).getInt("vibrate_group", 0);
                  } else if (paramInt == NotificationsSettingsActivity.this.callsVibrateRow) {
                    j = ((SharedPreferences)localObject).getInt("vibrate_calls", 0);
                  }
                }
                if (j == 1) {
                  localTextSettingsCell.setTextAndValue(LocaleController.getString("Vibrate", NUM), LocaleController.getString("Short", NUM), true);
                } else if (j == 2) {
                  localTextSettingsCell.setTextAndValue(LocaleController.getString("Vibrate", NUM), LocaleController.getString("VibrationDisabled", NUM), true);
                } else if (j == 3) {
                  localTextSettingsCell.setTextAndValue(LocaleController.getString("Vibrate", NUM), LocaleController.getString("Long", NUM), true);
                } else if (j == 4) {
                  localTextSettingsCell.setTextAndValue(LocaleController.getString("Vibrate", NUM), LocaleController.getString("OnlyIfSilent", NUM), true);
                }
              }
              else
              {
                if (paramInt == NotificationsSettingsActivity.this.repeatRow)
                {
                  paramInt = ((SharedPreferences)localObject).getInt("repeat_messages", 60);
                  if (paramInt == 0) {
                    paramViewHolder = LocaleController.getString("RepeatNotificationsNever", NUM);
                  }
                  for (;;)
                  {
                    localTextSettingsCell.setTextAndValue(LocaleController.getString("RepeatNotifications", NUM), paramViewHolder, false);
                    break;
                    if (paramInt < 60) {
                      paramViewHolder = LocaleController.formatPluralString("Minutes", paramInt);
                    } else {
                      paramViewHolder = LocaleController.formatPluralString("Hours", paramInt / 60);
                    }
                  }
                }
                if ((paramInt != NotificationsSettingsActivity.this.messagePriorityRow) && (paramInt != NotificationsSettingsActivity.this.groupPriorityRow)) {
                  break;
                }
                j = 0;
                if (paramInt == NotificationsSettingsActivity.this.messagePriorityRow) {
                  j = ((SharedPreferences)localObject).getInt("priority_messages", 1);
                }
                for (;;)
                {
                  if (j != 0) {
                    break label1673;
                  }
                  localTextSettingsCell.setTextAndValue(LocaleController.getString("NotificationsImportance", NUM), LocaleController.getString("NotificationsPriorityHigh", NUM), false);
                  break;
                  if (paramInt == NotificationsSettingsActivity.this.groupPriorityRow) {
                    j = ((SharedPreferences)localObject).getInt("priority_group", 1);
                  }
                }
                if ((j == 1) || (j == 2)) {
                  localTextSettingsCell.setTextAndValue(LocaleController.getString("NotificationsImportance", NUM), LocaleController.getString("NotificationsPriorityUrgent", NUM), false);
                } else if (j == 4) {
                  localTextSettingsCell.setTextAndValue(LocaleController.getString("NotificationsImportance", NUM), LocaleController.getString("NotificationsPriorityLow", NUM), false);
                } else if (j == 5) {
                  localTextSettingsCell.setTextAndValue(LocaleController.getString("NotificationsImportance", NUM), LocaleController.getString("NotificationsPriorityMedium", NUM), false);
                }
              }
            }
          }
        }
      } while ((paramInt != NotificationsSettingsActivity.this.messagePopupNotificationRow) && (paramInt != NotificationsSettingsActivity.this.groupPopupNotificationRow));
      int j = 0;
      if (paramInt == NotificationsSettingsActivity.this.messagePopupNotificationRow)
      {
        j = ((SharedPreferences)localObject).getInt("popupAll", 0);
        label1820:
        if (j != 0) {
          break label1880;
        }
        paramViewHolder = LocaleController.getString("NoPopup", NUM);
      }
      for (;;)
      {
        localTextSettingsCell.setTextAndValue(LocaleController.getString("PopupNotification", NUM), paramViewHolder, true);
        break;
        if (paramInt != NotificationsSettingsActivity.this.groupPopupNotificationRow) {
          break label1820;
        }
        j = ((SharedPreferences)localObject).getInt("popupGroup", 0);
        break label1820;
        label1880:
        if (j == 1) {
          paramViewHolder = LocaleController.getString("OnlyWhenScreenOn", NUM);
        } else if (j == 2) {
          paramViewHolder = LocaleController.getString("OnlyWhenScreenOff", NUM);
        } else {
          paramViewHolder = LocaleController.getString("AlwaysShowPopup", NUM);
        }
      }
    }
    
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      switch (paramInt)
      {
      default: 
        paramViewGroup = new TextSettingsCell(this.mContext);
        paramViewGroup.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
      }
      for (;;)
      {
        return new RecyclerListView.Holder(paramViewGroup);
        paramViewGroup = new HeaderCell(this.mContext);
        paramViewGroup.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        continue;
        paramViewGroup = new TextCheckCell(this.mContext);
        paramViewGroup.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        continue;
        paramViewGroup = new TextDetailSettingsCell(this.mContext);
        paramViewGroup.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        continue;
        paramViewGroup = new TextColorCell(this.mContext);
        paramViewGroup.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        continue;
        paramViewGroup = new ShadowSectionCell(this.mContext);
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/NotificationsSettingsActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */