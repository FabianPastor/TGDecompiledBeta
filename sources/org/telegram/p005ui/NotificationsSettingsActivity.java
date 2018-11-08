package org.telegram.p005ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build.VERSION;
import android.provider.Settings.System;
import android.text.TextUtils;
import android.util.LongSparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;
import com.google.android.exoplayer2.extractor.p003ts.PsExtractor;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.NotificationsController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.p005ui.ActionBar.AlertDialog.Builder;
import org.telegram.p005ui.ActionBar.BaseFragment;
import org.telegram.p005ui.ActionBar.C0646ActionBar.ActionBarMenuOnItemClick;
import org.telegram.p005ui.ActionBar.Theme;
import org.telegram.p005ui.ActionBar.ThemeDescription;
import org.telegram.p005ui.Cells.HeaderCell;
import org.telegram.p005ui.Cells.ShadowSectionCell;
import org.telegram.p005ui.Cells.TextCheckCell;
import org.telegram.p005ui.Cells.TextColorCell;
import org.telegram.p005ui.Cells.TextDetailSettingsCell;
import org.telegram.p005ui.Cells.TextSettingsCell;
import org.telegram.p005ui.Components.AlertsCreator;
import org.telegram.p005ui.Components.LayoutHelper;
import org.telegram.p005ui.Components.RecyclerListView;
import org.telegram.p005ui.Components.RecyclerListView.Holder;
import org.telegram.p005ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.TL_account_resetNotifySettings;
import org.telegram.tgnet.TLRPC.TL_account_updateNotifySettings;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputNotifyChats;
import org.telegram.tgnet.TLRPC.TL_inputNotifyUsers;
import org.telegram.tgnet.TLRPC.TL_inputPeerNotifySettings;
import org.telegram.tgnet.TLRPC.User;

/* renamed from: org.telegram.ui.NotificationsSettingsActivity */
public class NotificationsSettingsActivity extends BaseFragment implements NotificationCenterDelegate {
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
    private ArrayList<NotificationException> exceptionChats = null;
    private ArrayList<NotificationException> exceptionUsers = null;
    private int groupAlertRow;
    private int groupExceptionsRow;
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
    private int messageExceptionsRow;
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

    /* renamed from: org.telegram.ui.NotificationsSettingsActivity$NotificationException */
    public static class NotificationException {
        public long did;
        public boolean hasCustom;
        public int muteUntil;
        public int notify;
    }

    /* renamed from: org.telegram.ui.NotificationsSettingsActivity$1 */
    class C21831 extends ActionBarMenuOnItemClick {
        C21831() {
        }

        public void onItemClick(int id) {
            if (id == -1) {
                NotificationsSettingsActivity.this.lambda$checkDiscard$69$PassportActivity();
            }
        }
    }

    /* renamed from: org.telegram.ui.NotificationsSettingsActivity$ListAdapter */
    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(ViewHolder holder) {
            int position = holder.getAdapterPosition();
            return (position == NotificationsSettingsActivity.this.messageSectionRow || position == NotificationsSettingsActivity.this.groupSectionRow || position == NotificationsSettingsActivity.this.inappSectionRow || position == NotificationsSettingsActivity.this.eventsSectionRow || position == NotificationsSettingsActivity.this.otherSectionRow || position == NotificationsSettingsActivity.this.resetSectionRow || position == NotificationsSettingsActivity.this.eventsSectionRow2 || position == NotificationsSettingsActivity.this.groupSectionRow2 || position == NotificationsSettingsActivity.this.inappSectionRow2 || position == NotificationsSettingsActivity.this.otherSectionRow2 || position == NotificationsSettingsActivity.this.resetSectionRow2 || position == NotificationsSettingsActivity.this.callsSectionRow2 || position == NotificationsSettingsActivity.this.callsSectionRow) ? false : true;
        }

        public int getItemCount() {
            return NotificationsSettingsActivity.this.rowCount;
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new HeaderCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 1:
                    view = new TextCheckCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 2:
                    view = new TextDetailSettingsCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 3:
                    view = new TextColorCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 4:
                    view = new ShadowSectionCell(this.mContext);
                    break;
                default:
                    view = new TextSettingsCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
            }
            return new Holder(view);
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            SharedPreferences preferences;
            int a;
            switch (holder.getItemViewType()) {
                case 0:
                    HeaderCell headerCell = holder.itemView;
                    if (position == NotificationsSettingsActivity.this.messageSectionRow) {
                        headerCell.setText(LocaleController.getString("MessageNotifications", R.string.MessageNotifications));
                        return;
                    } else if (position == NotificationsSettingsActivity.this.groupSectionRow) {
                        headerCell.setText(LocaleController.getString("GroupNotifications", R.string.GroupNotifications));
                        return;
                    } else if (position == NotificationsSettingsActivity.this.inappSectionRow) {
                        headerCell.setText(LocaleController.getString("InAppNotifications", R.string.InAppNotifications));
                        return;
                    } else if (position == NotificationsSettingsActivity.this.eventsSectionRow) {
                        headerCell.setText(LocaleController.getString("Events", R.string.Events));
                        return;
                    } else if (position == NotificationsSettingsActivity.this.otherSectionRow) {
                        headerCell.setText(LocaleController.getString("NotificationsOther", R.string.NotificationsOther));
                        return;
                    } else if (position == NotificationsSettingsActivity.this.resetSectionRow) {
                        headerCell.setText(LocaleController.getString("Reset", R.string.Reset));
                        return;
                    } else if (position == NotificationsSettingsActivity.this.callsSectionRow) {
                        headerCell.setText(LocaleController.getString("VoipNotificationSettings", R.string.VoipNotificationSettings));
                        return;
                    } else {
                        return;
                    }
                case 1:
                    TextCheckCell checkCell = holder.itemView;
                    preferences = MessagesController.getNotificationsSettings(NotificationsSettingsActivity.this.currentAccount);
                    if (position == NotificationsSettingsActivity.this.messageAlertRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("Alert", R.string.Alert), preferences.getBoolean("EnableAll", true), true);
                        return;
                    } else if (position == NotificationsSettingsActivity.this.groupAlertRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("Alert", R.string.Alert), preferences.getBoolean("EnableGroup", true), true);
                        return;
                    } else if (position == NotificationsSettingsActivity.this.messagePreviewRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("MessagePreview", R.string.MessagePreview), preferences.getBoolean("EnablePreviewAll", true), true);
                        return;
                    } else if (position == NotificationsSettingsActivity.this.groupPreviewRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("MessagePreview", R.string.MessagePreview), preferences.getBoolean("EnablePreviewGroup", true), true);
                        return;
                    } else if (position == NotificationsSettingsActivity.this.inappSoundRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("InAppSounds", R.string.InAppSounds), preferences.getBoolean("EnableInAppSounds", true), true);
                        return;
                    } else if (position == NotificationsSettingsActivity.this.inappVibrateRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("InAppVibrate", R.string.InAppVibrate), preferences.getBoolean("EnableInAppVibrate", true), true);
                        return;
                    } else if (position == NotificationsSettingsActivity.this.inappPreviewRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("InAppPreview", R.string.InAppPreview), preferences.getBoolean("EnableInAppPreview", true), true);
                        return;
                    } else if (position == NotificationsSettingsActivity.this.inappPriorityRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("NotificationsImportance", R.string.NotificationsImportance), preferences.getBoolean("EnableInAppPriority", false), false);
                        return;
                    } else if (position == NotificationsSettingsActivity.this.contactJoinedRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("ContactJoined", R.string.ContactJoined), preferences.getBoolean("EnableContactJoined", true), true);
                        return;
                    } else if (position == NotificationsSettingsActivity.this.pinnedMessageRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("PinnedMessages", R.string.PinnedMessages), preferences.getBoolean("PinnedMessages", true), false);
                        return;
                    } else if (position == NotificationsSettingsActivity.this.androidAutoAlertRow) {
                        checkCell.setTextAndCheck("Android Auto", preferences.getBoolean("EnableAutoNotifications", false), true);
                        return;
                    } else if (position == NotificationsSettingsActivity.this.notificationsServiceRow) {
                        checkCell.setTextAndValueAndCheck(LocaleController.getString("NotificationsService", R.string.NotificationsService), LocaleController.getString("NotificationsServiceInfo", R.string.NotificationsServiceInfo), preferences.getBoolean("pushService", true), true, true);
                        return;
                    } else if (position == NotificationsSettingsActivity.this.notificationsServiceConnectionRow) {
                        checkCell.setTextAndValueAndCheck(LocaleController.getString("NotificationsServiceConnection", R.string.NotificationsServiceConnection), LocaleController.getString("NotificationsServiceConnectionInfo", R.string.NotificationsServiceConnectionInfo), preferences.getBoolean("pushConnection", true), true, true);
                        return;
                    } else if (position == NotificationsSettingsActivity.this.badgeNumberRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("BadgeNumber", R.string.BadgeNumber), NotificationsController.getInstance(NotificationsSettingsActivity.this.currentAccount).showBadgeNumber, true);
                        return;
                    } else if (position == NotificationsSettingsActivity.this.inchatSoundRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("InChatSound", R.string.InChatSound), preferences.getBoolean("EnableInChatSound", true), true);
                        return;
                    } else if (position == NotificationsSettingsActivity.this.callsVibrateRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("Vibrate", R.string.Vibrate), preferences.getBoolean("EnableCallVibrate", true), true);
                        return;
                    } else {
                        return;
                    }
                case 2:
                    TextDetailSettingsCell settingsCell = (TextDetailSettingsCell) holder.itemView;
                    settingsCell.setMultilineDetail(true);
                    if (position == NotificationsSettingsActivity.this.resetNotificationsRow) {
                        settingsCell.setTextAndValue(LocaleController.getString("ResetAllNotifications", R.string.ResetAllNotifications), LocaleController.getString("UndoAllCustom", R.string.UndoAllCustom), false);
                        return;
                    } else if (position == NotificationsSettingsActivity.this.messageExceptionsRow || position == NotificationsSettingsActivity.this.groupExceptionsRow) {
                        ArrayList<NotificationException> arrayList;
                        if (position == NotificationsSettingsActivity.this.groupExceptionsRow) {
                            arrayList = NotificationsSettingsActivity.this.exceptionChats;
                        } else {
                            arrayList = NotificationsSettingsActivity.this.exceptionUsers;
                        }
                        StringBuilder builder = new StringBuilder();
                        int length = 0;
                        int size = arrayList.size();
                        for (a = 0; a < size; a++) {
                            NotificationException exception = (NotificationException) arrayList.get(a);
                            int lower_id = (int) exception.did;
                            int high_id = (int) (exception.did >> 32);
                            String name = null;
                            User user;
                            if (lower_id == 0) {
                                EncryptedChat encryptedChat = MessagesController.getInstance(NotificationsSettingsActivity.this.currentAccount).getEncryptedChat(Integer.valueOf(high_id));
                                if (encryptedChat != null) {
                                    user = MessagesController.getInstance(NotificationsSettingsActivity.this.currentAccount).getUser(Integer.valueOf(encryptedChat.user_id));
                                    if (user != null) {
                                        name = ContactsController.formatName(user.first_name, user.last_name);
                                    }
                                }
                            } else if (lower_id > 0) {
                                user = MessagesController.getInstance(NotificationsSettingsActivity.this.currentAccount).getUser(Integer.valueOf(lower_id));
                                if (user != null) {
                                    name = ContactsController.formatName(user.first_name, user.last_name);
                                }
                            } else {
                                Chat chat = MessagesController.getInstance(NotificationsSettingsActivity.this.currentAccount).getChat(Integer.valueOf(-lower_id));
                                if (chat != null) {
                                    name = chat.title;
                                }
                            }
                            if (name != null) {
                                if (name.length() > 20) {
                                    int pos = 19;
                                    while (pos >= 0 && name.charAt(pos) == ' ') {
                                        pos--;
                                    }
                                    name = name.substring(0, pos + 1) + "..";
                                }
                                length = (int) (((float) length) + settingsCell.getValueTextView().getPaint().measureText(name));
                                if (builder.length() > 0) {
                                    builder.append(", ");
                                }
                                builder.append(name);
                                if (((float) length) > ((float) AndroidUtilities.displaySize.x) * 2.0f) {
                                    if (a != size - 1) {
                                        builder.append(", ...");
                                    }
                                    settingsCell.setTextAndValue(LocaleController.getString("NotificationsExceptions", R.string.NotificationsExceptions), builder.toString(), false);
                                    return;
                                }
                            }
                        }
                        settingsCell.setTextAndValue(LocaleController.getString("NotificationsExceptions", R.string.NotificationsExceptions), builder.toString(), false);
                        return;
                    } else {
                        return;
                    }
                case 3:
                    int color;
                    TextColorCell textColorCell = (TextColorCell) holder.itemView;
                    preferences = MessagesController.getNotificationsSettings(NotificationsSettingsActivity.this.currentAccount);
                    if (position == NotificationsSettingsActivity.this.messageLedRow) {
                        color = preferences.getInt("MessagesLed", -16776961);
                    } else {
                        color = preferences.getInt("GroupLed", -16776961);
                    }
                    for (a = 0; a < 9; a++) {
                        if (TextColorCell.colorsToSave[a] == color) {
                            color = TextColorCell.colors[a];
                            textColorCell.setTextAndColor(LocaleController.getString("LedColor", R.string.LedColor), color, true);
                            return;
                        }
                    }
                    textColorCell.setTextAndColor(LocaleController.getString("LedColor", R.string.LedColor), color, true);
                    return;
                case 5:
                    TextSettingsCell textCell = (TextSettingsCell) holder.itemView;
                    preferences = MessagesController.getNotificationsSettings(NotificationsSettingsActivity.this.currentAccount);
                    String value;
                    int value2;
                    if (position == NotificationsSettingsActivity.this.messageSoundRow || position == NotificationsSettingsActivity.this.groupSoundRow || position == NotificationsSettingsActivity.this.callsRingtoneRow) {
                        value = null;
                        if (position == NotificationsSettingsActivity.this.messageSoundRow) {
                            value = preferences.getString("GlobalSound", LocaleController.getString("SoundDefault", R.string.SoundDefault));
                        } else if (position == NotificationsSettingsActivity.this.groupSoundRow) {
                            value = preferences.getString("GroupSound", LocaleController.getString("SoundDefault", R.string.SoundDefault));
                        } else if (position == NotificationsSettingsActivity.this.callsRingtoneRow) {
                            value = preferences.getString("CallsRingtone", LocaleController.getString("DefaultRingtone", R.string.DefaultRingtone));
                        }
                        if (value.equals("NoSound")) {
                            value = LocaleController.getString("NoSound", R.string.NoSound);
                        }
                        if (position == NotificationsSettingsActivity.this.callsRingtoneRow) {
                            textCell.setTextAndValue(LocaleController.getString("VoipSettingsRingtone", R.string.VoipSettingsRingtone), value, true);
                            return;
                        }
                        textCell.setTextAndValue(LocaleController.getString("Sound", R.string.Sound), value, true);
                        return;
                    } else if (position == NotificationsSettingsActivity.this.messageVibrateRow || position == NotificationsSettingsActivity.this.groupVibrateRow || position == NotificationsSettingsActivity.this.callsVibrateRow) {
                        value2 = 0;
                        if (position == NotificationsSettingsActivity.this.messageVibrateRow) {
                            value2 = preferences.getInt("vibrate_messages", 0);
                        } else if (position == NotificationsSettingsActivity.this.groupVibrateRow) {
                            value2 = preferences.getInt("vibrate_group", 0);
                        } else if (position == NotificationsSettingsActivity.this.callsVibrateRow) {
                            value2 = preferences.getInt("vibrate_calls", 0);
                        }
                        if (value2 == 0) {
                            textCell.setTextAndValue(LocaleController.getString("Vibrate", R.string.Vibrate), LocaleController.getString("VibrationDefault", R.string.VibrationDefault), true);
                            return;
                        } else if (value2 == 1) {
                            textCell.setTextAndValue(LocaleController.getString("Vibrate", R.string.Vibrate), LocaleController.getString("Short", R.string.Short), true);
                            return;
                        } else if (value2 == 2) {
                            textCell.setTextAndValue(LocaleController.getString("Vibrate", R.string.Vibrate), LocaleController.getString("VibrationDisabled", R.string.VibrationDisabled), true);
                            return;
                        } else if (value2 == 3) {
                            textCell.setTextAndValue(LocaleController.getString("Vibrate", R.string.Vibrate), LocaleController.getString("Long", R.string.Long), true);
                            return;
                        } else if (value2 == 4) {
                            textCell.setTextAndValue(LocaleController.getString("Vibrate", R.string.Vibrate), LocaleController.getString("OnlyIfSilent", R.string.OnlyIfSilent), true);
                            return;
                        } else {
                            return;
                        }
                    } else if (position == NotificationsSettingsActivity.this.repeatRow) {
                        int minutes = preferences.getInt("repeat_messages", 60);
                        if (minutes == 0) {
                            value = LocaleController.getString("RepeatNotificationsNever", R.string.RepeatNotificationsNever);
                        } else if (minutes < 60) {
                            value = LocaleController.formatPluralString("Minutes", minutes);
                        } else {
                            value = LocaleController.formatPluralString("Hours", minutes / 60);
                        }
                        textCell.setTextAndValue(LocaleController.getString("RepeatNotifications", R.string.RepeatNotifications), value, false);
                        return;
                    } else if (position == NotificationsSettingsActivity.this.messagePriorityRow || position == NotificationsSettingsActivity.this.groupPriorityRow) {
                        value2 = 0;
                        if (position == NotificationsSettingsActivity.this.messagePriorityRow) {
                            value2 = preferences.getInt("priority_messages", 1);
                        } else if (position == NotificationsSettingsActivity.this.groupPriorityRow) {
                            value2 = preferences.getInt("priority_group", 1);
                        }
                        if (value2 == 0) {
                            textCell.setTextAndValue(LocaleController.getString("NotificationsImportance", R.string.NotificationsImportance), LocaleController.getString("NotificationsPriorityHigh", R.string.NotificationsPriorityHigh), true);
                            return;
                        } else if (value2 == 1 || value2 == 2) {
                            textCell.setTextAndValue(LocaleController.getString("NotificationsImportance", R.string.NotificationsImportance), LocaleController.getString("NotificationsPriorityUrgent", R.string.NotificationsPriorityUrgent), true);
                            return;
                        } else if (value2 == 4) {
                            textCell.setTextAndValue(LocaleController.getString("NotificationsImportance", R.string.NotificationsImportance), LocaleController.getString("NotificationsPriorityLow", R.string.NotificationsPriorityLow), true);
                            return;
                        } else if (value2 == 5) {
                            textCell.setTextAndValue(LocaleController.getString("NotificationsImportance", R.string.NotificationsImportance), LocaleController.getString("NotificationsPriorityMedium", R.string.NotificationsPriorityMedium), true);
                            return;
                        } else {
                            return;
                        }
                    } else if (position == NotificationsSettingsActivity.this.messageExceptionsRow || position == NotificationsSettingsActivity.this.groupExceptionsRow) {
                        String text;
                        if ((position == NotificationsSettingsActivity.this.messageExceptionsRow && NotificationsSettingsActivity.this.exceptionUsers == null) || (position == NotificationsSettingsActivity.this.groupExceptionsRow && NotificationsSettingsActivity.this.exceptionChats == null)) {
                            text = LocaleController.getString("Loading", R.string.Loading);
                        } else if (position == NotificationsSettingsActivity.this.messageExceptionsRow) {
                            if (NotificationsSettingsActivity.this.exceptionUsers.isEmpty()) {
                                text = LocaleController.getString("EmptyExceptions", R.string.EmptyExceptions);
                            } else {
                                text = String.format("%d", new Object[]{Integer.valueOf(NotificationsSettingsActivity.this.exceptionUsers.size())});
                            }
                        } else if (NotificationsSettingsActivity.this.exceptionChats.isEmpty()) {
                            text = LocaleController.getString("EmptyExceptions", R.string.EmptyExceptions);
                        } else {
                            text = String.format("%d", new Object[]{Integer.valueOf(NotificationsSettingsActivity.this.exceptionChats.size())});
                        }
                        textCell.setTextAndValue(LocaleController.getString("NotificationsExceptions", R.string.NotificationsExceptions), text, false);
                        return;
                    } else if (position == NotificationsSettingsActivity.this.messagePopupNotificationRow || position == NotificationsSettingsActivity.this.groupPopupNotificationRow) {
                        int option = 0;
                        if (position == NotificationsSettingsActivity.this.messagePopupNotificationRow) {
                            option = preferences.getInt("popupAll", 0);
                        } else if (position == NotificationsSettingsActivity.this.groupPopupNotificationRow) {
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
                        textCell.setTextAndValue(LocaleController.getString("PopupNotification", R.string.PopupNotification), value, true);
                        return;
                    } else {
                        return;
                    }
                default:
                    return;
            }
        }

        public int getItemViewType(int position) {
            if (position == NotificationsSettingsActivity.this.messageSectionRow || position == NotificationsSettingsActivity.this.groupSectionRow || position == NotificationsSettingsActivity.this.inappSectionRow || position == NotificationsSettingsActivity.this.eventsSectionRow || position == NotificationsSettingsActivity.this.otherSectionRow || position == NotificationsSettingsActivity.this.resetSectionRow || position == NotificationsSettingsActivity.this.callsSectionRow) {
                return 0;
            }
            if (position == NotificationsSettingsActivity.this.messageAlertRow || position == NotificationsSettingsActivity.this.messagePreviewRow || position == NotificationsSettingsActivity.this.groupAlertRow || position == NotificationsSettingsActivity.this.groupPreviewRow || position == NotificationsSettingsActivity.this.inappSoundRow || position == NotificationsSettingsActivity.this.inappVibrateRow || position == NotificationsSettingsActivity.this.inappPreviewRow || position == NotificationsSettingsActivity.this.contactJoinedRow || position == NotificationsSettingsActivity.this.pinnedMessageRow || position == NotificationsSettingsActivity.this.notificationsServiceRow || position == NotificationsSettingsActivity.this.badgeNumberRow || position == NotificationsSettingsActivity.this.inappPriorityRow || position == NotificationsSettingsActivity.this.inchatSoundRow || position == NotificationsSettingsActivity.this.androidAutoAlertRow || position == NotificationsSettingsActivity.this.notificationsServiceConnectionRow) {
                return 1;
            }
            if (position == NotificationsSettingsActivity.this.messageLedRow || position == NotificationsSettingsActivity.this.groupLedRow) {
                return 3;
            }
            if (position == NotificationsSettingsActivity.this.eventsSectionRow2 || position == NotificationsSettingsActivity.this.groupSectionRow2 || position == NotificationsSettingsActivity.this.inappSectionRow2 || position == NotificationsSettingsActivity.this.otherSectionRow2 || position == NotificationsSettingsActivity.this.resetSectionRow2 || position == NotificationsSettingsActivity.this.callsSectionRow2) {
                return 4;
            }
            if (position == NotificationsSettingsActivity.this.resetNotificationsRow || ((position == NotificationsSettingsActivity.this.messageExceptionsRow && NotificationsSettingsActivity.this.exceptionUsers != null && !NotificationsSettingsActivity.this.exceptionUsers.isEmpty()) || (position == NotificationsSettingsActivity.this.groupExceptionsRow && NotificationsSettingsActivity.this.exceptionChats != null && !NotificationsSettingsActivity.this.exceptionChats.isEmpty()))) {
                return 2;
            }
            return 5;
        }
    }

    public boolean onFragmentCreate() {
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new NotificationsSettingsActivity$$Lambda$0(this));
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
        this.messageExceptionsRow = i;
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
        this.groupExceptionsRow = i;
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
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.notificationsSettingsUpdated);
        return super.onFragmentCreate();
    }

    final /* synthetic */ void lambda$onFragmentCreate$1$NotificationsSettingsActivity() {
        User user;
        Chat chat;
        ArrayList<NotificationException> usersResult = new ArrayList();
        ArrayList<NotificationException> chatsResult = new ArrayList();
        LongSparseArray<NotificationException> waitingForLoadExceptions = new LongSparseArray();
        ArrayList<Integer> usersToLoad = new ArrayList();
        ArrayList<Integer> chatsToLoad = new ArrayList();
        ArrayList<Integer> encryptedChatsToLoad = new ArrayList();
        ArrayList<User> users = new ArrayList();
        ArrayList<Chat> chats = new ArrayList();
        ArrayList<EncryptedChat> encryptedChats = new ArrayList();
        int selfId = UserConfig.getInstance(this.currentAccount).clientUserId;
        SharedPreferences preferences = MessagesController.getNotificationsSettings(this.currentAccount);
        Map<String, ?> values = preferences.getAll();
        for (Entry<String, ?> entry : values.entrySet()) {
            String key = (String) entry.getKey();
            if (key.startsWith("notify2_")) {
                key = key.replace("notify2_", TtmlNode.ANONYMOUS_REGION_ID);
                long did = Utilities.parseLong(key).longValue();
                if (!(did == 0 || did == ((long) selfId))) {
                    NotificationException exception = new NotificationException();
                    exception.did = did;
                    exception.hasCustom = preferences.getBoolean("custom_" + did, false);
                    exception.notify = ((Integer) entry.getValue()).intValue();
                    if (exception.notify != 0) {
                        Integer time = (Integer) values.get("notifyuntil_" + key);
                        if (time != null) {
                            exception.muteUntil = time.intValue();
                        }
                    }
                    int lower_id = (int) did;
                    int high_id = (int) (did << 32);
                    if (lower_id != 0) {
                        if (lower_id > 0) {
                            user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(lower_id));
                            if (user == null) {
                                usersToLoad.add(Integer.valueOf(lower_id));
                                waitingForLoadExceptions.put(did, exception);
                            } else if (user.deleted) {
                            }
                            usersResult.add(exception);
                        } else {
                            chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-lower_id));
                            if (chat == null) {
                                chatsToLoad.add(Integer.valueOf(-lower_id));
                                waitingForLoadExceptions.put(did, exception);
                            } else if (!(chat.left || chat.kicked)) {
                                if (chat.migrated_to != null) {
                                }
                            }
                            chatsResult.add(exception);
                        }
                    } else if (high_id != 0) {
                        EncryptedChat encryptedChat = MessagesController.getInstance(this.currentAccount).getEncryptedChat(Integer.valueOf(high_id));
                        if (encryptedChat == null) {
                            encryptedChatsToLoad.add(Integer.valueOf(high_id));
                            waitingForLoadExceptions.put(did, exception);
                        } else {
                            user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(encryptedChat.user_id));
                            if (user == null) {
                                usersToLoad.add(Integer.valueOf(encryptedChat.user_id));
                                waitingForLoadExceptions.put((long) encryptedChat.user_id, exception);
                            } else if (user.deleted) {
                            }
                        }
                        usersResult.add(exception);
                    }
                }
            }
        }
        if (waitingForLoadExceptions.size() != 0) {
            int a;
            try {
                if (!encryptedChatsToLoad.isEmpty()) {
                    MessagesStorage.getInstance(this.currentAccount).getEncryptedChatsInternal(TextUtils.join(",", encryptedChatsToLoad), encryptedChats, usersToLoad);
                }
                if (!usersToLoad.isEmpty()) {
                    MessagesStorage.getInstance(this.currentAccount).getUsersInternal(TextUtils.join(",", usersToLoad), users);
                }
                if (!chatsToLoad.isEmpty()) {
                    MessagesStorage.getInstance(this.currentAccount).getChatsInternal(TextUtils.join(",", chatsToLoad), chats);
                }
            } catch (Throwable e) {
                FileLog.m14e(e);
            }
            int size = chats.size();
            for (a = 0; a < size; a++) {
                chat = (Chat) chats.get(a);
                if (!(chat.left || chat.kicked || chat.migrated_to != null)) {
                    waitingForLoadExceptions.remove((long) (-chat.f78id));
                }
            }
            size = users.size();
            for (a = 0; a < size; a++) {
                user = (User) users.get(a);
                if (!user.deleted) {
                    waitingForLoadExceptions.remove((long) user.f177id);
                }
            }
            size = encryptedChats.size();
            for (a = 0; a < size; a++) {
                waitingForLoadExceptions.remove(((long) ((EncryptedChat) encryptedChats.get(a)).f88id) << 32);
            }
            size = waitingForLoadExceptions.size();
            for (a = 0; a < size; a++) {
                if (((int) waitingForLoadExceptions.keyAt(a)) < 0) {
                    chatsResult.remove(waitingForLoadExceptions.valueAt(a));
                } else {
                    usersResult.remove(waitingForLoadExceptions.valueAt(a));
                }
            }
        }
        AndroidUtilities.runOnUIThread(new NotificationsSettingsActivity$$Lambda$11(this, users, chats, encryptedChats, usersResult, chatsResult));
    }

    final /* synthetic */ void lambda$null$0$NotificationsSettingsActivity(ArrayList users, ArrayList chats, ArrayList encryptedChats, ArrayList usersResult, ArrayList chatsResult) {
        MessagesController.getInstance(this.currentAccount).putUsers(users, true);
        MessagesController.getInstance(this.currentAccount).putChats(chats, true);
        MessagesController.getInstance(this.currentAccount).putEncryptedChats(encryptedChats, true);
        this.exceptionUsers = usersResult;
        this.exceptionChats = chatsResult;
        this.adapter.notifyItemChanged(this.messageExceptionsRow);
        this.adapter.notifyItemChanged(this.groupExceptionsRow);
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.notificationsSettingsUpdated);
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("NotificationsAndSounds", R.string.NotificationsAndSounds));
        this.actionBar.setActionBarMenuOnItemClick(new C21831());
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = this.fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        this.listView = new RecyclerListView(context);
        this.listView.setItemAnimator(null);
        this.listView.setLayoutAnimation(null);
        this.listView.setLayoutManager(new LinearLayoutManager(context, 1, false) {
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        });
        this.listView.setVerticalScrollBarEnabled(false);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        RecyclerListView recyclerListView = this.listView;
        Adapter listAdapter = new ListAdapter(context);
        this.adapter = listAdapter;
        recyclerListView.setAdapter(listAdapter);
        this.listView.setOnItemClickListener(new NotificationsSettingsActivity$$Lambda$1(this));
        return this.fragmentView;
    }

    final /* synthetic */ void lambda$createView$10$NotificationsSettingsActivity(View view, int position) {
        boolean enabled = false;
        if (getParentActivity() != null) {
            boolean z;
            SharedPreferences preferences;
            Editor editor;
            Builder builder;
            if (position == this.messageAlertRow || position == this.groupAlertRow) {
                preferences = MessagesController.getNotificationsSettings(this.currentAccount);
                editor = preferences.edit();
                if (position == this.messageAlertRow) {
                    enabled = preferences.getBoolean("EnableAll", true);
                    editor.putBoolean("EnableAll", !enabled);
                } else if (position == this.groupAlertRow) {
                    enabled = preferences.getBoolean("EnableGroup", true);
                    editor.putBoolean("EnableGroup", !enabled);
                }
                editor.commit();
                if (position == this.groupAlertRow) {
                    z = true;
                } else {
                    z = false;
                }
                updateServerNotificationsSettings(z);
            } else if (position == this.messagePreviewRow || position == this.groupPreviewRow) {
                preferences = MessagesController.getNotificationsSettings(this.currentAccount);
                editor = preferences.edit();
                if (position == this.messagePreviewRow) {
                    enabled = preferences.getBoolean("EnablePreviewAll", true);
                    editor.putBoolean("EnablePreviewAll", !enabled);
                } else if (position == this.groupPreviewRow) {
                    enabled = preferences.getBoolean("EnablePreviewGroup", true);
                    editor.putBoolean("EnablePreviewGroup", !enabled);
                }
                editor.commit();
                if (position == this.groupPreviewRow) {
                    z = true;
                } else {
                    z = false;
                }
                updateServerNotificationsSettings(z);
            } else if (position == this.messageSoundRow || position == this.groupSoundRow || position == this.callsRingtoneRow) {
                try {
                    preferences = MessagesController.getNotificationsSettings(this.currentAccount);
                    Intent intent = new Intent("android.intent.action.RINGTONE_PICKER");
                    intent.putExtra("android.intent.extra.ringtone.TYPE", position == this.callsRingtoneRow ? 1 : 2);
                    intent.putExtra("android.intent.extra.ringtone.SHOW_DEFAULT", true);
                    intent.putExtra("android.intent.extra.ringtone.DEFAULT_URI", RingtoneManager.getDefaultUri(position == this.callsRingtoneRow ? 1 : 2));
                    Uri currentSound = null;
                    String defaultPath = null;
                    Uri defaultUri = position == this.callsRingtoneRow ? System.DEFAULT_RINGTONE_URI : System.DEFAULT_NOTIFICATION_URI;
                    if (defaultUri != null) {
                        defaultPath = defaultUri.getPath();
                    }
                    String path;
                    if (position == this.messageSoundRow) {
                        path = preferences.getString("GlobalSoundPath", defaultPath);
                        if (path != null) {
                            if (!path.equals("NoSound")) {
                                currentSound = path.equals(defaultPath) ? defaultUri : Uri.parse(path);
                            }
                        }
                    } else if (position == this.groupSoundRow) {
                        path = preferences.getString("GroupSoundPath", defaultPath);
                        if (path != null) {
                            if (!path.equals("NoSound")) {
                                currentSound = path.equals(defaultPath) ? defaultUri : Uri.parse(path);
                            }
                        }
                    } else if (position == this.callsRingtoneRow) {
                        path = preferences.getString("CallsRingtonfePath", defaultPath);
                        if (path != null) {
                            if (!path.equals("NoSound")) {
                                currentSound = path.equals(defaultPath) ? defaultUri : Uri.parse(path);
                            }
                        }
                    }
                    intent.putExtra("android.intent.extra.ringtone.EXISTING_URI", currentSound);
                    startActivityForResult(intent, position);
                } catch (Throwable e) {
                    FileLog.m14e(e);
                }
            } else if (position == this.resetNotificationsRow) {
                builder = new Builder(getParentActivity());
                builder.setMessage(LocaleController.getString("ResetNotificationsAlert", R.string.ResetNotificationsAlert));
                builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                builder.setPositiveButton(LocaleController.getString("Reset", R.string.Reset), new NotificationsSettingsActivity$$Lambda$3(this));
                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                showDialog(builder.create());
            } else if (position == this.inappSoundRow) {
                preferences = MessagesController.getNotificationsSettings(this.currentAccount);
                editor = preferences.edit();
                enabled = preferences.getBoolean("EnableInAppSounds", true);
                editor.putBoolean("EnableInAppSounds", !enabled);
                editor.commit();
            } else if (position == this.inappVibrateRow) {
                preferences = MessagesController.getNotificationsSettings(this.currentAccount);
                editor = preferences.edit();
                enabled = preferences.getBoolean("EnableInAppVibrate", true);
                editor.putBoolean("EnableInAppVibrate", !enabled);
                editor.commit();
            } else if (position == this.inappPreviewRow) {
                preferences = MessagesController.getNotificationsSettings(this.currentAccount);
                editor = preferences.edit();
                enabled = preferences.getBoolean("EnableInAppPreview", true);
                editor.putBoolean("EnableInAppPreview", !enabled);
                editor.commit();
            } else if (position == this.inchatSoundRow) {
                preferences = MessagesController.getNotificationsSettings(this.currentAccount);
                editor = preferences.edit();
                enabled = preferences.getBoolean("EnableInChatSound", true);
                editor.putBoolean("EnableInChatSound", !enabled);
                editor.commit();
                NotificationsController.getInstance(this.currentAccount).setInChatSoundEnabled(!enabled);
            } else if (position == this.inappPriorityRow) {
                preferences = MessagesController.getNotificationsSettings(this.currentAccount);
                editor = preferences.edit();
                enabled = preferences.getBoolean("EnableInAppPriority", false);
                editor.putBoolean("EnableInAppPriority", !enabled);
                editor.commit();
            } else if (position == this.contactJoinedRow) {
                preferences = MessagesController.getNotificationsSettings(this.currentAccount);
                editor = preferences.edit();
                enabled = preferences.getBoolean("EnableContactJoined", true);
                MessagesController.getInstance(this.currentAccount).enableJoined = !enabled;
                editor.putBoolean("EnableContactJoined", !enabled);
                editor.commit();
            } else if (position == this.pinnedMessageRow) {
                preferences = MessagesController.getNotificationsSettings(this.currentAccount);
                editor = preferences.edit();
                enabled = preferences.getBoolean("PinnedMessages", true);
                editor.putBoolean("PinnedMessages", !enabled);
                editor.commit();
            } else if (position == this.androidAutoAlertRow) {
                preferences = MessagesController.getNotificationsSettings(this.currentAccount);
                editor = preferences.edit();
                enabled = preferences.getBoolean("EnableAutoNotifications", false);
                editor.putBoolean("EnableAutoNotifications", !enabled);
                editor.commit();
            } else if (position == this.badgeNumberRow) {
                editor = MessagesController.getNotificationsSettings(this.currentAccount).edit();
                enabled = NotificationsController.getInstance(this.currentAccount).showBadgeNumber;
                NotificationsController.getInstance(this.currentAccount).showBadgeNumber = !enabled;
                editor.putBoolean("badgeNumber", NotificationsController.getInstance(this.currentAccount).showBadgeNumber);
                editor.commit();
                NotificationsController.getInstance(this.currentAccount).setBadgeEnabled(!enabled);
            } else if (position == this.notificationsServiceConnectionRow) {
                preferences = MessagesController.getNotificationsSettings(this.currentAccount);
                enabled = preferences.getBoolean("pushConnection", true);
                editor = preferences.edit();
                editor.putBoolean("pushConnection", !enabled);
                editor.commit();
                if (enabled) {
                    ConnectionsManager.getInstance(this.currentAccount).setPushConnectionEnabled(false);
                } else {
                    ConnectionsManager.getInstance(this.currentAccount).setPushConnectionEnabled(true);
                }
            } else if (position == this.notificationsServiceRow) {
                preferences = MessagesController.getNotificationsSettings(this.currentAccount);
                enabled = preferences.getBoolean("pushService", true);
                editor = preferences.edit();
                editor.putBoolean("pushService", !enabled);
                editor.commit();
                if (enabled) {
                    ApplicationLoader.stopPushService();
                } else {
                    ApplicationLoader.startPushService();
                }
            } else if (position == this.messageLedRow || position == this.groupLedRow) {
                if (getParentActivity() != null) {
                    showDialog(AlertsCreator.createColorSelectDialog(getParentActivity(), 0, position == this.groupLedRow, position == this.messageLedRow, new NotificationsSettingsActivity$$Lambda$4(this, position)));
                } else {
                    return;
                }
            } else if (position == this.messagePopupNotificationRow || position == this.groupPopupNotificationRow) {
                if (getParentActivity() != null) {
                    showDialog(AlertsCreator.createPopupSelectDialog(getParentActivity(), this, position == this.groupPopupNotificationRow, position == this.messagePopupNotificationRow, new NotificationsSettingsActivity$$Lambda$5(this, position)));
                } else {
                    return;
                }
            } else if (position == this.messageVibrateRow || position == this.groupVibrateRow || position == this.callsVibrateRow) {
                if (getParentActivity() != null) {
                    String key = null;
                    if (position == this.messageVibrateRow) {
                        key = "vibrate_messages";
                    } else if (position == this.groupVibrateRow) {
                        key = "vibrate_group";
                    } else if (position == this.callsVibrateRow) {
                        key = "vibrate_calls";
                    }
                    showDialog(AlertsCreator.createVibrationSelectDialog(getParentActivity(), this, 0, key, new NotificationsSettingsActivity$$Lambda$6(this, position)));
                } else {
                    return;
                }
            } else if (position == this.messagePriorityRow || position == this.groupPriorityRow) {
                showDialog(AlertsCreator.createPrioritySelectDialog(getParentActivity(), this, 0, position == this.groupPriorityRow, position == this.messagePriorityRow, new NotificationsSettingsActivity$$Lambda$7(this, position)));
            } else if (position == this.messageExceptionsRow || position == this.groupExceptionsRow) {
                presentFragment(new NotificationsExceptionsActivity(position == this.messageExceptionsRow ? this.exceptionUsers : this.exceptionChats));
            } else if (position == this.repeatRow) {
                builder = new Builder(getParentActivity());
                builder.setTitle(LocaleController.getString("RepeatNotifications", R.string.RepeatNotifications));
                builder.setItems(new CharSequence[]{LocaleController.getString("RepeatDisabled", R.string.RepeatDisabled), LocaleController.formatPluralString("Minutes", 5), LocaleController.formatPluralString("Minutes", 10), LocaleController.formatPluralString("Minutes", 30), LocaleController.formatPluralString("Hours", 1), LocaleController.formatPluralString("Hours", 2), LocaleController.formatPluralString("Hours", 4)}, new NotificationsSettingsActivity$$Lambda$8(this, position));
                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                showDialog(builder.create());
            }
            if (view instanceof TextCheckCell) {
                TextCheckCell textCheckCell = (TextCheckCell) view;
                if (enabled) {
                    z = false;
                } else {
                    z = true;
                }
                textCheckCell.setChecked(z);
            }
        }
    }

    final /* synthetic */ void lambda$null$4$NotificationsSettingsActivity(DialogInterface dialogInterface, int i) {
        if (!this.reseting) {
            this.reseting = true;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_account_resetNotifySettings(), new NotificationsSettingsActivity$$Lambda$9(this));
        }
    }

    final /* synthetic */ void lambda$null$3$NotificationsSettingsActivity(TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new NotificationsSettingsActivity$$Lambda$10(this));
    }

    final /* synthetic */ void lambda$null$2$NotificationsSettingsActivity() {
        MessagesController.getInstance(this.currentAccount).enableJoined = true;
        this.reseting = false;
        Editor editor = MessagesController.getNotificationsSettings(this.currentAccount).edit();
        editor.clear();
        editor.commit();
        this.exceptionChats.clear();
        this.exceptionUsers.clear();
        this.adapter.notifyDataSetChanged();
        if (getParentActivity() != null) {
            Toast.makeText(getParentActivity(), LocaleController.getString("ResetNotificationsText", R.string.ResetNotificationsText), 0).show();
        }
    }

    final /* synthetic */ void lambda$null$5$NotificationsSettingsActivity(int position) {
        this.adapter.notifyItemChanged(position);
    }

    final /* synthetic */ void lambda$null$6$NotificationsSettingsActivity(int position) {
        this.adapter.notifyItemChanged(position);
    }

    final /* synthetic */ void lambda$null$7$NotificationsSettingsActivity(int position) {
        this.adapter.notifyItemChanged(position);
    }

    final /* synthetic */ void lambda$null$8$NotificationsSettingsActivity(int position) {
        this.adapter.notifyItemChanged(position);
    }

    final /* synthetic */ void lambda$null$9$NotificationsSettingsActivity(int position, DialogInterface dialog, int which) {
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
        MessagesController.getNotificationsSettings(this.currentAccount).edit().putInt("repeat_messages", minutes).commit();
        this.adapter.notifyItemChanged(position);
    }

    public void updateServerNotificationsSettings(boolean group) {
        int i = 0;
        SharedPreferences preferences = MessagesController.getNotificationsSettings(this.currentAccount);
        TL_account_updateNotifySettings req = new TL_account_updateNotifySettings();
        req.settings = new TL_inputPeerNotifySettings();
        req.settings.flags = 5;
        TL_inputPeerNotifySettings tL_inputPeerNotifySettings;
        if (group) {
            req.peer = new TL_inputNotifyChats();
            tL_inputPeerNotifySettings = req.settings;
            if (!preferences.getBoolean("EnableGroup", true)) {
                i = ConnectionsManager.DEFAULT_DATACENTER_ID;
            }
            tL_inputPeerNotifySettings.mute_until = i;
            req.settings.show_previews = preferences.getBoolean("EnablePreviewGroup", true);
        } else {
            req.peer = new TL_inputNotifyUsers();
            tL_inputPeerNotifySettings = req.settings;
            if (!preferences.getBoolean("EnableAll", true)) {
                i = ConnectionsManager.DEFAULT_DATACENTER_ID;
            }
            tL_inputPeerNotifySettings.mute_until = i;
            req.settings.show_previews = preferences.getBoolean("EnablePreviewAll", true);
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, NotificationsSettingsActivity$$Lambda$2.$instance);
    }

    /* renamed from: lambda$updateServerNotificationsSettings$11$NotificationsSettingsActivity */
    static final /* synthetic */ void m26xafa3bc98(TLObject response, TL_error error) {
    }

    public void onActivityResultFragment(int requestCode, int resultCode, Intent data) {
        if (resultCode == -1) {
            Uri ringtone = (Uri) data.getParcelableExtra("android.intent.extra.ringtone.PICKED_URI");
            String name = null;
            if (ringtone != null) {
                Ringtone rng = RingtoneManager.getRingtone(getParentActivity(), ringtone);
                if (rng != null) {
                    if (requestCode == this.callsRingtoneRow) {
                        if (ringtone.equals(System.DEFAULT_RINGTONE_URI)) {
                            name = LocaleController.getString("DefaultRingtone", R.string.DefaultRingtone);
                        } else {
                            name = rng.getTitle(getParentActivity());
                        }
                    } else if (ringtone.equals(System.DEFAULT_NOTIFICATION_URI)) {
                        name = LocaleController.getString("SoundDefault", R.string.SoundDefault);
                    } else {
                        name = rng.getTitle(getParentActivity());
                    }
                    rng.stop();
                }
            }
            Editor editor = MessagesController.getNotificationsSettings(this.currentAccount).edit();
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
            if (requestCode == this.messageSoundRow || requestCode == this.groupSoundRow) {
                updateServerNotificationsSettings(requestCode == this.groupSoundRow);
            }
            this.adapter.notifyItemChanged(requestCode);
        }
    }

    public void onResume() {
        super.onResume();
        if (this.adapter != null) {
            this.adapter.notifyDataSetChanged();
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.notificationsSettingsUpdated) {
            this.adapter.notifyDataSetChanged();
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[22];
        themeDescriptionArr[0] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{HeaderCell.class, TextCheckCell.class, TextDetailSettingsCell.class, TextColorCell.class, TextSettingsCell.class}, null, null, null, Theme.key_windowBackgroundWhite);
        themeDescriptionArr[1] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray);
        themeDescriptionArr[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault);
        themeDescriptionArr[3] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault);
        themeDescriptionArr[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon);
        themeDescriptionArr[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle);
        themeDescriptionArr[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector);
        themeDescriptionArr[7] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector);
        themeDescriptionArr[8] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider);
        themeDescriptionArr[9] = new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlueHeader);
        themeDescriptionArr[10] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[11] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2);
        themeDescriptionArr[12] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchThumb);
        themeDescriptionArr[13] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrack);
        themeDescriptionArr[14] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchThumbChecked);
        themeDescriptionArr[15] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrackChecked);
        themeDescriptionArr[16] = new ThemeDescription(this.listView, 0, new Class[]{TextColorCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[17] = new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[18] = new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteValueText);
        themeDescriptionArr[19] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        themeDescriptionArr[20] = new ThemeDescription(this.listView, 0, new Class[]{TextDetailSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[21] = new ThemeDescription(this.listView, 0, new Class[]{TextDetailSettingsCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2);
        return themeDescriptionArr;
    }
}
