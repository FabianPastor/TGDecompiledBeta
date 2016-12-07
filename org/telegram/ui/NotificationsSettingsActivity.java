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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
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
import org.telegram.messenger.beta.R;
import org.telegram.messenger.exoplayer2.extractor.ts.PsExtractor;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_account_resetNotifySettings;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Adapters.BaseFragmentAdapter;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextColorCell;
import org.telegram.ui.Cells.TextDetailSettingsCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.LayoutHelper;

public class NotificationsSettingsActivity extends BaseFragment implements NotificationCenterDelegate {
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

    private class ListAdapter extends BaseFragmentAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean areAllItemsEnabled() {
            return false;
        }

        public boolean isEnabled(int i) {
            return (i == NotificationsSettingsActivity.this.messageSectionRow || i == NotificationsSettingsActivity.this.groupSectionRow || i == NotificationsSettingsActivity.this.inappSectionRow || i == NotificationsSettingsActivity.this.eventsSectionRow || i == NotificationsSettingsActivity.this.otherSectionRow || i == NotificationsSettingsActivity.this.resetSectionRow || i == NotificationsSettingsActivity.this.eventsSectionRow2 || i == NotificationsSettingsActivity.this.groupSectionRow2 || i == NotificationsSettingsActivity.this.inappSectionRow2 || i == NotificationsSettingsActivity.this.otherSectionRow2 || i == NotificationsSettingsActivity.this.resetSectionRow2 || i == NotificationsSettingsActivity.this.callsSectionRow2 || i == NotificationsSettingsActivity.this.callsSectionRow) ? false : true;
        }

        public int getCount() {
            return NotificationsSettingsActivity.this.rowCount;
        }

        public Object getItem(int i) {
            return null;
        }

        public long getItemId(int i) {
            return (long) i;
        }

        public boolean hasStableIds() {
            return false;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            int type = getItemViewType(i);
            if (type == 0) {
                if (view == null) {
                    View headerCell = new HeaderCell(this.mContext);
                }
                if (i == NotificationsSettingsActivity.this.messageSectionRow) {
                    ((HeaderCell) view).setText(LocaleController.getString("MessageNotifications", R.string.MessageNotifications));
                } else if (i == NotificationsSettingsActivity.this.groupSectionRow) {
                    ((HeaderCell) view).setText(LocaleController.getString("GroupNotifications", R.string.GroupNotifications));
                } else if (i == NotificationsSettingsActivity.this.inappSectionRow) {
                    ((HeaderCell) view).setText(LocaleController.getString("InAppNotifications", R.string.InAppNotifications));
                } else if (i == NotificationsSettingsActivity.this.eventsSectionRow) {
                    ((HeaderCell) view).setText(LocaleController.getString("Events", R.string.Events));
                } else if (i == NotificationsSettingsActivity.this.otherSectionRow) {
                    ((HeaderCell) view).setText(LocaleController.getString("NotificationsOther", R.string.NotificationsOther));
                } else if (i == NotificationsSettingsActivity.this.resetSectionRow) {
                    ((HeaderCell) view).setText(LocaleController.getString("Reset", R.string.Reset));
                } else if (i == NotificationsSettingsActivity.this.callsSectionRow) {
                    ((HeaderCell) view).setText(LocaleController.getString("VoipNotificationSettings", R.string.VoipNotificationSettings));
                }
            }
            SharedPreferences preferences;
            if (type == 1) {
                if (view == null) {
                    headerCell = new TextCheckCell(this.mContext);
                }
                TextCheckCell checkCell = (TextCheckCell) view;
                preferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
                if (i == NotificationsSettingsActivity.this.messageAlertRow) {
                    checkCell.setTextAndCheck(LocaleController.getString("Alert", R.string.Alert), preferences.getBoolean("EnableAll", true), true);
                    return view;
                } else if (i == NotificationsSettingsActivity.this.groupAlertRow) {
                    checkCell.setTextAndCheck(LocaleController.getString("Alert", R.string.Alert), preferences.getBoolean("EnableGroup", true), true);
                    return view;
                } else if (i == NotificationsSettingsActivity.this.messagePreviewRow) {
                    checkCell.setTextAndCheck(LocaleController.getString("MessagePreview", R.string.MessagePreview), preferences.getBoolean("EnablePreviewAll", true), true);
                    return view;
                } else if (i == NotificationsSettingsActivity.this.groupPreviewRow) {
                    checkCell.setTextAndCheck(LocaleController.getString("MessagePreview", R.string.MessagePreview), preferences.getBoolean("EnablePreviewGroup", true), true);
                    return view;
                } else if (i == NotificationsSettingsActivity.this.inappSoundRow) {
                    checkCell.setTextAndCheck(LocaleController.getString("InAppSounds", R.string.InAppSounds), preferences.getBoolean("EnableInAppSounds", true), true);
                    return view;
                } else if (i == NotificationsSettingsActivity.this.inappVibrateRow) {
                    checkCell.setTextAndCheck(LocaleController.getString("InAppVibrate", R.string.InAppVibrate), preferences.getBoolean("EnableInAppVibrate", true), true);
                    return view;
                } else if (i == NotificationsSettingsActivity.this.inappPreviewRow) {
                    checkCell.setTextAndCheck(LocaleController.getString("InAppPreview", R.string.InAppPreview), preferences.getBoolean("EnableInAppPreview", true), true);
                    return view;
                } else if (i == NotificationsSettingsActivity.this.inappPriorityRow) {
                    checkCell.setTextAndCheck(LocaleController.getString("NotificationsPriority", R.string.NotificationsPriority), preferences.getBoolean("EnableInAppPriority", false), false);
                    return view;
                } else if (i == NotificationsSettingsActivity.this.contactJoinedRow) {
                    checkCell.setTextAndCheck(LocaleController.getString("ContactJoined", R.string.ContactJoined), preferences.getBoolean("EnableContactJoined", true), true);
                    return view;
                } else if (i == NotificationsSettingsActivity.this.pinnedMessageRow) {
                    checkCell.setTextAndCheck(LocaleController.getString("PinnedMessages", R.string.PinnedMessages), preferences.getBoolean("PinnedMessages", true), false);
                    return view;
                } else if (i == NotificationsSettingsActivity.this.androidAutoAlertRow) {
                    checkCell.setTextAndCheck("Android Auto", preferences.getBoolean("EnableAutoNotifications", false), true);
                    return view;
                } else if (i == NotificationsSettingsActivity.this.notificationsServiceRow) {
                    checkCell.setTextAndValueAndCheck(LocaleController.getString("NotificationsService", R.string.NotificationsService), LocaleController.getString("NotificationsServiceInfo", R.string.NotificationsServiceInfo), preferences.getBoolean("pushService", true), true, true);
                    return view;
                } else if (i == NotificationsSettingsActivity.this.notificationsServiceConnectionRow) {
                    checkCell.setTextAndValueAndCheck(LocaleController.getString("NotificationsServiceConnection", R.string.NotificationsServiceConnection), LocaleController.getString("NotificationsServiceConnectionInfo", R.string.NotificationsServiceConnectionInfo), preferences.getBoolean("pushConnection", true), true, true);
                    return view;
                } else if (i == NotificationsSettingsActivity.this.badgeNumberRow) {
                    checkCell.setTextAndCheck(LocaleController.getString("BadgeNumber", R.string.BadgeNumber), preferences.getBoolean("badgeNumber", true), true);
                    return view;
                } else if (i == NotificationsSettingsActivity.this.inchatSoundRow) {
                    checkCell.setTextAndCheck(LocaleController.getString("InChatSound", R.string.InChatSound), preferences.getBoolean("EnableInChatSound", true), true);
                    return view;
                } else if (i != NotificationsSettingsActivity.this.callsVibrateRow) {
                    return view;
                } else {
                    checkCell.setTextAndCheck(LocaleController.getString("Vibrate", R.string.Vibrate), preferences.getBoolean("EnableCallVibrate", true), true);
                    return view;
                }
            } else if (type == 2) {
                if (view == null) {
                    headerCell = new TextDetailSettingsCell(this.mContext);
                }
                TextDetailSettingsCell textCell = (TextDetailSettingsCell) view;
                textCell.setMultilineDetail(true);
                textCell.setTextAndValue(LocaleController.getString("ResetAllNotifications", R.string.ResetAllNotifications), LocaleController.getString("UndoAllCustom", R.string.UndoAllCustom), false);
                return view;
            } else if (type == 3) {
                int color;
                if (view == null) {
                    headerCell = new TextColorCell(this.mContext);
                }
                TextColorCell textCell2 = (TextColorCell) view;
                preferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
                if (i == NotificationsSettingsActivity.this.messageLedRow) {
                    color = preferences.getInt("MessagesLed", -16776961);
                } else {
                    color = preferences.getInt("GroupLed", -16776961);
                }
                for (int a = 0; a < 9; a++) {
                    if (TextColorCell.colorsToSave[a] == color) {
                        color = TextColorCell.colors[a];
                        break;
                    }
                }
                textCell2.setTextAndColor(LocaleController.getString("LedColor", R.string.LedColor), color, true);
                return view;
            } else if (type == 4) {
                if (view == null) {
                    return new ShadowSectionCell(this.mContext);
                }
                return view;
            } else if (type != 5) {
                return view;
            } else {
                if (view == null) {
                    headerCell = new TextSettingsCell(this.mContext);
                }
                TextSettingsCell textCell3 = (TextSettingsCell) view;
                preferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
                String value;
                if (i == NotificationsSettingsActivity.this.messageSoundRow || i == NotificationsSettingsActivity.this.groupSoundRow || i == NotificationsSettingsActivity.this.callsRingtoneRow) {
                    value = null;
                    if (i == NotificationsSettingsActivity.this.messageSoundRow) {
                        value = preferences.getString("GlobalSound", LocaleController.getString("SoundDefault", R.string.SoundDefault));
                    } else if (i == NotificationsSettingsActivity.this.groupSoundRow) {
                        value = preferences.getString("GroupSound", LocaleController.getString("SoundDefault", R.string.SoundDefault));
                    } else if (i == NotificationsSettingsActivity.this.callsRingtoneRow) {
                        value = preferences.getString("CallsRingtone", LocaleController.getString("SoundDefault", R.string.SoundDefault));
                    }
                    if (value.equals("NoSound")) {
                        value = LocaleController.getString("NoSound", R.string.NoSound);
                    }
                    if (i == NotificationsSettingsActivity.this.callsRingtoneRow) {
                        textCell3.setTextAndValue(LocaleController.getString("VoipSettingsRingtone", R.string.VoipSettingsRingtone), value, true);
                        return view;
                    }
                    textCell3.setTextAndValue(LocaleController.getString("Sound", R.string.Sound), value, true);
                    return view;
                } else if (i == NotificationsSettingsActivity.this.messageVibrateRow || i == NotificationsSettingsActivity.this.groupVibrateRow || i == NotificationsSettingsActivity.this.callsVibrateRow) {
                    value = 0;
                    if (i == NotificationsSettingsActivity.this.messageVibrateRow) {
                        value = preferences.getInt("vibrate_messages", 0);
                    } else if (i == NotificationsSettingsActivity.this.groupVibrateRow) {
                        value = preferences.getInt("vibrate_group", 0);
                    } else if (i == NotificationsSettingsActivity.this.callsVibrateRow) {
                        value = preferences.getInt("vibrate_calls", 0);
                    }
                    if (value == 0) {
                        textCell3.setTextAndValue(LocaleController.getString("Vibrate", R.string.Vibrate), LocaleController.getString("VibrationDefault", R.string.VibrationDefault), true);
                        return view;
                    } else if (value == 1) {
                        textCell3.setTextAndValue(LocaleController.getString("Vibrate", R.string.Vibrate), LocaleController.getString("Short", R.string.Short), true);
                        return view;
                    } else if (value == 2) {
                        textCell3.setTextAndValue(LocaleController.getString("Vibrate", R.string.Vibrate), LocaleController.getString("VibrationDisabled", R.string.VibrationDisabled), true);
                        return view;
                    } else if (value == 3) {
                        textCell3.setTextAndValue(LocaleController.getString("Vibrate", R.string.Vibrate), LocaleController.getString("Long", R.string.Long), true);
                        return view;
                    } else if (value != 4) {
                        return view;
                    } else {
                        textCell3.setTextAndValue(LocaleController.getString("Vibrate", R.string.Vibrate), LocaleController.getString("OnlyIfSilent", R.string.OnlyIfSilent), true);
                        return view;
                    }
                } else if (i == NotificationsSettingsActivity.this.repeatRow) {
                    int minutes = preferences.getInt("repeat_messages", 60);
                    if (minutes == 0) {
                        value = LocaleController.getString("RepeatNotificationsNever", R.string.RepeatNotificationsNever);
                    } else if (minutes < 60) {
                        value = LocaleController.formatPluralString("Minutes", minutes);
                    } else {
                        value = LocaleController.formatPluralString("Hours", minutes / 60);
                    }
                    textCell3.setTextAndValue(LocaleController.getString("RepeatNotifications", R.string.RepeatNotifications), value, false);
                    return view;
                } else if (i == NotificationsSettingsActivity.this.messagePriorityRow || i == NotificationsSettingsActivity.this.groupPriorityRow) {
                    value = 0;
                    if (i == NotificationsSettingsActivity.this.messagePriorityRow) {
                        value = preferences.getInt("priority_messages", 1);
                    } else if (i == NotificationsSettingsActivity.this.groupPriorityRow) {
                        value = preferences.getInt("priority_group", 1);
                    }
                    if (value == 0) {
                        textCell3.setTextAndValue(LocaleController.getString("NotificationsPriority", R.string.NotificationsPriority), LocaleController.getString("NotificationsPriorityDefault", R.string.NotificationsPriorityDefault), false);
                        return view;
                    } else if (value == 1) {
                        textCell3.setTextAndValue(LocaleController.getString("NotificationsPriority", R.string.NotificationsPriority), LocaleController.getString("NotificationsPriorityHigh", R.string.NotificationsPriorityHigh), false);
                        return view;
                    } else if (value != 2) {
                        return view;
                    } else {
                        textCell3.setTextAndValue(LocaleController.getString("NotificationsPriority", R.string.NotificationsPriority), LocaleController.getString("NotificationsPriorityMax", R.string.NotificationsPriorityMax), false);
                        return view;
                    }
                } else if (i != NotificationsSettingsActivity.this.messagePopupNotificationRow && i != NotificationsSettingsActivity.this.groupPopupNotificationRow) {
                    return view;
                } else {
                    int option = 0;
                    if (i == NotificationsSettingsActivity.this.messagePopupNotificationRow) {
                        option = preferences.getInt("popupAll", 0);
                    } else if (i == NotificationsSettingsActivity.this.groupPopupNotificationRow) {
                        option = preferences.getInt("popupGroup", 0);
                    }
                    if (option == 0) {
                        value = LocaleController.getString("NoPopup", R.string.NoPopup);
                    } else if (option == 1) {
                        value = LocaleController.getString("OnlyWhenScreenOn", R.string.OnlyWhenScreenOn);
                    } else if (option == 2) {
                        value = LocaleController.getString("OnlyWhenScreenOff", R.string.OnlyWhenScreenOff);
                    } else {
                        value = LocaleController.getString("AlwaysShowPopup", R.string.AlwaysShowPopup);
                    }
                    textCell3.setTextAndValue(LocaleController.getString("PopupNotification", R.string.PopupNotification), value, true);
                    return view;
                }
            }
        }

        public int getItemViewType(int i) {
            if (i == NotificationsSettingsActivity.this.messageSectionRow || i == NotificationsSettingsActivity.this.groupSectionRow || i == NotificationsSettingsActivity.this.inappSectionRow || i == NotificationsSettingsActivity.this.eventsSectionRow || i == NotificationsSettingsActivity.this.otherSectionRow || i == NotificationsSettingsActivity.this.resetSectionRow || i == NotificationsSettingsActivity.this.callsSectionRow) {
                return 0;
            }
            if (i == NotificationsSettingsActivity.this.messageAlertRow || i == NotificationsSettingsActivity.this.messagePreviewRow || i == NotificationsSettingsActivity.this.groupAlertRow || i == NotificationsSettingsActivity.this.groupPreviewRow || i == NotificationsSettingsActivity.this.inappSoundRow || i == NotificationsSettingsActivity.this.inappVibrateRow || i == NotificationsSettingsActivity.this.inappPreviewRow || i == NotificationsSettingsActivity.this.contactJoinedRow || i == NotificationsSettingsActivity.this.pinnedMessageRow || i == NotificationsSettingsActivity.this.notificationsServiceRow || i == NotificationsSettingsActivity.this.badgeNumberRow || i == NotificationsSettingsActivity.this.inappPriorityRow || i == NotificationsSettingsActivity.this.inchatSoundRow || i == NotificationsSettingsActivity.this.androidAutoAlertRow || i == NotificationsSettingsActivity.this.notificationsServiceConnectionRow) {
                return 1;
            }
            if (i == NotificationsSettingsActivity.this.messageLedRow || i == NotificationsSettingsActivity.this.groupLedRow) {
                return 3;
            }
            if (i == NotificationsSettingsActivity.this.eventsSectionRow2 || i == NotificationsSettingsActivity.this.groupSectionRow2 || i == NotificationsSettingsActivity.this.inappSectionRow2 || i == NotificationsSettingsActivity.this.otherSectionRow2 || i == NotificationsSettingsActivity.this.resetSectionRow2 || i == NotificationsSettingsActivity.this.callsSectionRow2) {
                return 4;
            }
            if (i == NotificationsSettingsActivity.this.resetNotificationsRow) {
                return 2;
            }
            return 5;
        }

        public int getViewTypeCount() {
            return 6;
        }

        public boolean isEmpty() {
            return false;
        }
    }

    public boolean onFragmentCreate() {
        int i = this.rowCount;
        this.rowCount = i + 1;
        this.messageSectionRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.messageAlertRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.messagePreviewRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.messageLedRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.messageVibrateRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.messagePopupNotificationRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.messageSoundRow = i;
        if (VERSION.SDK_INT >= 21) {
            i = this.rowCount;
            this.rowCount = i + 1;
            this.messagePriorityRow = i;
        } else {
            this.messagePriorityRow = -1;
        }
        i = this.rowCount;
        this.rowCount = i + 1;
        this.groupSectionRow2 = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.groupSectionRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.groupAlertRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.groupPreviewRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.groupLedRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.groupVibrateRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.groupPopupNotificationRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.groupSoundRow = i;
        if (VERSION.SDK_INT >= 21) {
            i = this.rowCount;
            this.rowCount = i + 1;
            this.groupPriorityRow = i;
        } else {
            this.groupPriorityRow = -1;
        }
        i = this.rowCount;
        this.rowCount = i + 1;
        this.inappSectionRow2 = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.inappSectionRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.inappSoundRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.inappVibrateRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.inappPreviewRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.inchatSoundRow = i;
        if (VERSION.SDK_INT >= 21) {
            i = this.rowCount;
            this.rowCount = i + 1;
            this.inappPriorityRow = i;
        } else {
            this.inappPriorityRow = -1;
        }
        if (MessagesController.getInstance().callsEnabled) {
            i = this.rowCount;
            this.rowCount = i + 1;
            this.callsSectionRow2 = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.callsSectionRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.callsVibrateRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.callsRingtoneRow = i;
        } else {
            this.callsRingtoneRow = -1;
            this.callsVibrateRow = -1;
            this.callsSectionRow = -1;
            this.callsSectionRow2 = -1;
        }
        i = this.rowCount;
        this.rowCount = i + 1;
        this.eventsSectionRow2 = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.eventsSectionRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.contactJoinedRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.pinnedMessageRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.otherSectionRow2 = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.otherSectionRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.notificationsServiceRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.notificationsServiceConnectionRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.badgeNumberRow = i;
        this.androidAutoAlertRow = -1;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.repeatRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.resetSectionRow2 = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.resetSectionRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.resetNotificationsRow = i;
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.notificationsSettingsUpdated);
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.notificationsSettingsUpdated);
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("NotificationsAndSounds", R.string.NotificationsAndSounds));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    NotificationsSettingsActivity.this.finishFragment();
                }
            }
        });
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = this.fragmentView;
        this.listView = new ListView(context);
        this.listView.setDivider(null);
        this.listView.setDividerHeight(0);
        this.listView.setVerticalScrollBarEnabled(false);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setAdapter(new ListAdapter(context));
        this.listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                boolean enabled = false;
                SharedPreferences preferences;
                Editor editor;
                NotificationsSettingsActivity notificationsSettingsActivity;
                boolean z;
                if (i == NotificationsSettingsActivity.this.messageAlertRow || i == NotificationsSettingsActivity.this.groupAlertRow) {
                    preferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
                    editor = preferences.edit();
                    if (i == NotificationsSettingsActivity.this.messageAlertRow) {
                        enabled = preferences.getBoolean("EnableAll", true);
                        editor.putBoolean("EnableAll", !enabled);
                    } else if (i == NotificationsSettingsActivity.this.groupAlertRow) {
                        enabled = preferences.getBoolean("EnableGroup", true);
                        editor.putBoolean("EnableGroup", !enabled);
                    }
                    editor.commit();
                    notificationsSettingsActivity = NotificationsSettingsActivity.this;
                    if (i == NotificationsSettingsActivity.this.groupAlertRow) {
                        z = true;
                    } else {
                        z = false;
                    }
                    notificationsSettingsActivity.updateServerNotificationsSettings(z);
                } else if (i == NotificationsSettingsActivity.this.messagePreviewRow || i == NotificationsSettingsActivity.this.groupPreviewRow) {
                    preferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
                    editor = preferences.edit();
                    if (i == NotificationsSettingsActivity.this.messagePreviewRow) {
                        enabled = preferences.getBoolean("EnablePreviewAll", true);
                        editor.putBoolean("EnablePreviewAll", !enabled);
                    } else if (i == NotificationsSettingsActivity.this.groupPreviewRow) {
                        enabled = preferences.getBoolean("EnablePreviewGroup", true);
                        editor.putBoolean("EnablePreviewGroup", !enabled);
                    }
                    editor.commit();
                    notificationsSettingsActivity = NotificationsSettingsActivity.this;
                    if (i == NotificationsSettingsActivity.this.groupPreviewRow) {
                        z = true;
                    } else {
                        z = false;
                    }
                    notificationsSettingsActivity.updateServerNotificationsSettings(z);
                } else if (i == NotificationsSettingsActivity.this.messageSoundRow || i == NotificationsSettingsActivity.this.groupSoundRow || i == NotificationsSettingsActivity.this.callsRingtoneRow) {
                    try {
                        preferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
                        Intent intent = new Intent("android.intent.action.RINGTONE_PICKER");
                        intent.putExtra("android.intent.extra.ringtone.TYPE", i == NotificationsSettingsActivity.this.callsRingtoneRow ? 1 : 2);
                        intent.putExtra("android.intent.extra.ringtone.SHOW_DEFAULT", true);
                        intent.putExtra("android.intent.extra.ringtone.DEFAULT_URI", RingtoneManager.getDefaultUri(i == NotificationsSettingsActivity.this.callsRingtoneRow ? 1 : 2));
                        Uri currentSound = null;
                        String defaultPath = null;
                        Uri defaultUri = i == NotificationsSettingsActivity.this.callsRingtoneRow ? System.DEFAULT_RINGTONE_URI : System.DEFAULT_NOTIFICATION_URI;
                        if (defaultUri != null) {
                            defaultPath = defaultUri.getPath();
                        }
                        String path;
                        if (i == NotificationsSettingsActivity.this.messageSoundRow) {
                            path = preferences.getString("GlobalSoundPath", defaultPath);
                            if (path != null) {
                                if (!path.equals("NoSound")) {
                                    currentSound = path.equals(defaultPath) ? defaultUri : Uri.parse(path);
                                }
                            }
                        } else if (i == NotificationsSettingsActivity.this.groupSoundRow) {
                            path = preferences.getString("GroupSoundPath", defaultPath);
                            if (path != null) {
                                if (!path.equals("NoSound")) {
                                    currentSound = path.equals(defaultPath) ? defaultUri : Uri.parse(path);
                                }
                            }
                        } else if (i == NotificationsSettingsActivity.this.callsRingtoneRow) {
                            path = preferences.getString("CallsRingtonfePath", defaultPath);
                            if (path != null) {
                                if (!path.equals("NoSound")) {
                                    currentSound = path.equals(defaultPath) ? defaultUri : Uri.parse(path);
                                }
                            }
                        }
                        intent.putExtra("android.intent.extra.ringtone.EXISTING_URI", currentSound);
                        NotificationsSettingsActivity.this.startActivityForResult(intent, i);
                    } catch (Throwable e) {
                        FileLog.e("tmessages", e);
                    }
                } else if (i == NotificationsSettingsActivity.this.resetNotificationsRow) {
                    if (!NotificationsSettingsActivity.this.reseting) {
                        NotificationsSettingsActivity.this.reseting = true;
                        ConnectionsManager.getInstance().sendRequest(new TL_account_resetNotifySettings(), new RequestDelegate() {
                            public void run(TLObject response, TL_error error) {
                                AndroidUtilities.runOnUIThread(new Runnable() {
                                    public void run() {
                                        MessagesController.getInstance().enableJoined = true;
                                        NotificationsSettingsActivity.this.reseting = false;
                                        Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
                                        editor.clear();
                                        editor.commit();
                                        if (NotificationsSettingsActivity.this.listView != null) {
                                            NotificationsSettingsActivity.this.listView.invalidateViews();
                                        }
                                        if (NotificationsSettingsActivity.this.getParentActivity() != null) {
                                            Toast.makeText(NotificationsSettingsActivity.this.getParentActivity(), LocaleController.getString("ResetNotificationsText", R.string.ResetNotificationsText), 0).show();
                                        }
                                    }
                                });
                            }
                        });
                    } else {
                        return;
                    }
                } else if (i == NotificationsSettingsActivity.this.inappSoundRow) {
                    preferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
                    editor = preferences.edit();
                    enabled = preferences.getBoolean("EnableInAppSounds", true);
                    editor.putBoolean("EnableInAppSounds", !enabled);
                    editor.commit();
                } else if (i == NotificationsSettingsActivity.this.inappVibrateRow) {
                    preferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
                    editor = preferences.edit();
                    enabled = preferences.getBoolean("EnableInAppVibrate", true);
                    editor.putBoolean("EnableInAppVibrate", !enabled);
                    editor.commit();
                } else if (i == NotificationsSettingsActivity.this.inappPreviewRow) {
                    preferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
                    editor = preferences.edit();
                    enabled = preferences.getBoolean("EnableInAppPreview", true);
                    editor.putBoolean("EnableInAppPreview", !enabled);
                    editor.commit();
                } else if (i == NotificationsSettingsActivity.this.inchatSoundRow) {
                    preferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
                    editor = preferences.edit();
                    enabled = preferences.getBoolean("EnableInChatSound", true);
                    editor.putBoolean("EnableInChatSound", !enabled);
                    editor.commit();
                    NotificationsController.getInstance().setInChatSoundEnabled(!enabled);
                } else if (i == NotificationsSettingsActivity.this.inappPriorityRow) {
                    preferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
                    editor = preferences.edit();
                    enabled = preferences.getBoolean("EnableInAppPriority", false);
                    editor.putBoolean("EnableInAppPriority", !enabled);
                    editor.commit();
                } else if (i == NotificationsSettingsActivity.this.contactJoinedRow) {
                    preferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
                    editor = preferences.edit();
                    enabled = preferences.getBoolean("EnableContactJoined", true);
                    MessagesController.getInstance().enableJoined = !enabled;
                    editor.putBoolean("EnableContactJoined", !enabled);
                    editor.commit();
                } else if (i == NotificationsSettingsActivity.this.pinnedMessageRow) {
                    preferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
                    editor = preferences.edit();
                    enabled = preferences.getBoolean("PinnedMessages", true);
                    editor.putBoolean("PinnedMessages", !enabled);
                    editor.commit();
                } else if (i == NotificationsSettingsActivity.this.androidAutoAlertRow) {
                    preferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
                    editor = preferences.edit();
                    enabled = preferences.getBoolean("EnableAutoNotifications", false);
                    editor.putBoolean("EnableAutoNotifications", !enabled);
                    editor.commit();
                } else if (i == NotificationsSettingsActivity.this.badgeNumberRow) {
                    preferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
                    editor = preferences.edit();
                    enabled = preferences.getBoolean("badgeNumber", true);
                    editor.putBoolean("badgeNumber", !enabled);
                    editor.commit();
                    NotificationsController.getInstance().setBadgeEnabled(!enabled);
                } else if (i == NotificationsSettingsActivity.this.notificationsServiceConnectionRow) {
                    preferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
                    enabled = preferences.getBoolean("pushConnection", true);
                    editor = preferences.edit();
                    editor.putBoolean("pushConnection", !enabled);
                    editor.commit();
                    if (enabled) {
                        ConnectionsManager.getInstance().setPushConnectionEnabled(false);
                    } else {
                        ConnectionsManager.getInstance().setPushConnectionEnabled(true);
                    }
                } else if (i == NotificationsSettingsActivity.this.notificationsServiceRow) {
                    preferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
                    enabled = preferences.getBoolean("pushService", true);
                    editor = preferences.edit();
                    editor.putBoolean("pushService", !enabled);
                    editor.commit();
                    if (enabled) {
                        ApplicationLoader.stopPushService();
                    } else {
                        ApplicationLoader.startPushService();
                    }
                } else if (i == NotificationsSettingsActivity.this.messageLedRow || i == NotificationsSettingsActivity.this.groupLedRow) {
                    if (NotificationsSettingsActivity.this.getParentActivity() != null) {
                        NotificationsSettingsActivity.this.showDialog(AlertsCreator.createColorSelectDialog(NotificationsSettingsActivity.this.getParentActivity(), 0, i == NotificationsSettingsActivity.this.groupLedRow, i == NotificationsSettingsActivity.this.messageLedRow, new Runnable() {
                            public void run() {
                                if (NotificationsSettingsActivity.this.listView != null) {
                                    NotificationsSettingsActivity.this.listView.invalidateViews();
                                }
                            }
                        }));
                    } else {
                        return;
                    }
                } else if (i == NotificationsSettingsActivity.this.messagePopupNotificationRow || i == NotificationsSettingsActivity.this.groupPopupNotificationRow) {
                    if (NotificationsSettingsActivity.this.getParentActivity() != null) {
                        NotificationsSettingsActivity.this.showDialog(AlertsCreator.createPopupSelectDialog(NotificationsSettingsActivity.this.getParentActivity(), NotificationsSettingsActivity.this, i == NotificationsSettingsActivity.this.groupPopupNotificationRow, i == NotificationsSettingsActivity.this.messagePopupNotificationRow, new Runnable() {
                            public void run() {
                                if (NotificationsSettingsActivity.this.listView != null) {
                                    NotificationsSettingsActivity.this.listView.invalidateViews();
                                }
                            }
                        }));
                    } else {
                        return;
                    }
                } else if (i == NotificationsSettingsActivity.this.messageVibrateRow || i == NotificationsSettingsActivity.this.groupVibrateRow || i == NotificationsSettingsActivity.this.callsVibrateRow) {
                    String key = null;
                    if (i == NotificationsSettingsActivity.this.messageVibrateRow) {
                        key = "vibrate_messages";
                    } else if (i == NotificationsSettingsActivity.this.groupVibrateRow) {
                        key = "vibrate_group";
                    } else if (i == NotificationsSettingsActivity.this.callsVibrateRow) {
                        key = "vibrate_calls";
                    }
                    NotificationsSettingsActivity.this.showDialog(AlertsCreator.createVibrationSelectDialog(NotificationsSettingsActivity.this.getParentActivity(), NotificationsSettingsActivity.this, 0, key, new Runnable() {
                        public void run() {
                            if (NotificationsSettingsActivity.this.listView != null) {
                                NotificationsSettingsActivity.this.listView.invalidateViews();
                            }
                        }
                    }));
                } else if (i == NotificationsSettingsActivity.this.messagePriorityRow || i == NotificationsSettingsActivity.this.groupPriorityRow) {
                    NotificationsSettingsActivity.this.showDialog(AlertsCreator.createPrioritySelectDialog(NotificationsSettingsActivity.this.getParentActivity(), NotificationsSettingsActivity.this, 0, i == NotificationsSettingsActivity.this.groupPriorityRow, i == NotificationsSettingsActivity.this.messagePriorityRow, new Runnable() {
                        public void run() {
                            if (NotificationsSettingsActivity.this.listView != null) {
                                NotificationsSettingsActivity.this.listView.invalidateViews();
                            }
                        }
                    }));
                } else if (i == NotificationsSettingsActivity.this.repeatRow) {
                    Builder builder = new Builder(NotificationsSettingsActivity.this.getParentActivity());
                    builder.setTitle(LocaleController.getString("RepeatNotifications", R.string.RepeatNotifications));
                    builder.setItems(new CharSequence[]{LocaleController.getString("RepeatDisabled", R.string.RepeatDisabled), LocaleController.formatPluralString("Minutes", 5), LocaleController.formatPluralString("Minutes", 10), LocaleController.formatPluralString("Minutes", 30), LocaleController.formatPluralString("Hours", 1), LocaleController.formatPluralString("Hours", 2), LocaleController.formatPluralString("Hours", 4)}, new OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            int minutes = 0;
                            if (which == 1) {
                                minutes = 5;
                            } else if (which == 2) {
                                minutes = 10;
                            } else if (which == 3) {
                                minutes = 30;
                            } else if (which == 4) {
                                minutes = 60;
                            } else if (which == 5) {
                                minutes = 120;
                            } else if (which == 6) {
                                minutes = PsExtractor.VIDEO_STREAM_MASK;
                            }
                            ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit().putInt("repeat_messages", minutes).commit();
                            if (NotificationsSettingsActivity.this.listView != null) {
                                NotificationsSettingsActivity.this.listView.invalidateViews();
                            }
                        }
                    });
                    builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                    NotificationsSettingsActivity.this.showDialog(builder.create());
                }
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(!enabled);
                }
            }
        });
        return this.fragmentView;
    }

    public void updateServerNotificationsSettings(boolean group) {
    }

    public void onActivityResultFragment(int requestCode, int resultCode, Intent data) {
        if (resultCode == -1) {
            Uri ringtone = (Uri) data.getParcelableExtra("android.intent.extra.ringtone.PICKED_URI");
            String name = null;
            if (ringtone != null) {
                Ringtone rng = RingtoneManager.getRingtone(getParentActivity(), ringtone);
                if (rng != null) {
                    if (ringtone.equals(requestCode == this.callsRingtoneRow ? System.DEFAULT_RINGTONE_URI : System.DEFAULT_NOTIFICATION_URI)) {
                        name = LocaleController.getString("SoundDefault", R.string.SoundDefault);
                    } else {
                        name = rng.getTitle(getParentActivity());
                    }
                    rng.stop();
                }
            }
            Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
            if (requestCode == this.messageSoundRow) {
                if (name == null || ringtone == null) {
                    editor.putString("GlobalSound", "NoSound");
                    editor.putString("GlobalSoundPath", "NoSound");
                } else {
                    editor.putString("GlobalSound", name);
                    editor.putString("GlobalSoundPath", ringtone.toString());
                }
            } else if (requestCode == this.groupSoundRow) {
                if (name == null || ringtone == null) {
                    editor.putString("GroupSound", "NoSound");
                    editor.putString("GroupSoundPath", "NoSound");
                } else {
                    editor.putString("GroupSound", name);
                    editor.putString("GroupSoundPath", ringtone.toString());
                }
            } else if (requestCode == this.callsRingtoneRow) {
                if (name == null || ringtone == null) {
                    editor.putString("CallsRingtone", "NoSound");
                    editor.putString("CallsRingtonePath", "NoSound");
                } else {
                    editor.putString("CallsRingtone", name);
                    editor.putString("CallsRingtonePath", ringtone.toString());
                }
            }
            editor.commit();
            this.listView.invalidateViews();
        }
    }

    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationCenter.notificationsSettingsUpdated) {
            this.listView.invalidateViews();
        }
    }
}
