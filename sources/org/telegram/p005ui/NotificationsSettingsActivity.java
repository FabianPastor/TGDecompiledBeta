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
import org.telegram.messenger.CLASSNAMER;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.NotificationsController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.p005ui.ActionBar.AlertDialog.Builder;
import org.telegram.p005ui.ActionBar.BaseFragment;
import org.telegram.p005ui.ActionBar.CLASSNAMEActionBar.ActionBarMenuOnItemClick;
import org.telegram.p005ui.ActionBar.Theme;
import org.telegram.p005ui.ActionBar.ThemeDescription;
import org.telegram.p005ui.Cells.HeaderCell;
import org.telegram.p005ui.Cells.NotificationsCheckCell;
import org.telegram.p005ui.Cells.ShadowSectionCell;
import org.telegram.p005ui.Cells.TextCheckCell;
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
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.User;

/* renamed from: org.telegram.ui.NotificationsSettingsActivity */
public class NotificationsSettingsActivity extends BaseFragment implements NotificationCenterDelegate {
    private ListAdapter adapter;
    private int androidAutoAlertRow;
    private int badgeNumberMessagesRow;
    private int badgeNumberMutedRow;
    private int badgeNumberSection;
    private int badgeNumberSection2Row;
    private int badgeNumberShowRow;
    private int callsRingtoneRow;
    private int callsSection2Row;
    private int callsSectionRow;
    private int callsVibrateRow;
    private int channelsRow;
    private int contactJoinedRow;
    private int eventsSection2Row;
    private int eventsSectionRow;
    private ArrayList<NotificationException> exceptionChannels = null;
    private ArrayList<NotificationException> exceptionChats = null;
    private ArrayList<NotificationException> exceptionUsers = null;
    private int groupRow;
    private int inappPreviewRow;
    private int inappPriorityRow;
    private int inappSectionRow;
    private int inappSoundRow;
    private int inappVibrateRow;
    private int inchatSoundRow;
    private RecyclerListView listView;
    private int notificationsSection2Row;
    private int notificationsSectionRow;
    private int notificationsServiceConnectionRow;
    private int notificationsServiceRow;
    private int otherSection2Row;
    private int otherSectionRow;
    private int pinnedMessageRow;
    private int privateRow;
    private int repeatRow;
    private int resetNotificationsRow;
    private int resetSection2Row;
    private int resetSectionRow;
    private boolean reseting = false;
    private int rowCount = 0;

    /* renamed from: org.telegram.ui.NotificationsSettingsActivity$1 */
    class CLASSNAME extends ActionBarMenuOnItemClick {
        CLASSNAME() {
        }

        public void onItemClick(int id) {
            if (id == -1) {
                NotificationsSettingsActivity.this.finishFragment();
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
            return (position == NotificationsSettingsActivity.this.notificationsSectionRow || position == NotificationsSettingsActivity.this.notificationsSection2Row || position == NotificationsSettingsActivity.this.inappSectionRow || position == NotificationsSettingsActivity.this.eventsSectionRow || position == NotificationsSettingsActivity.this.otherSectionRow || position == NotificationsSettingsActivity.this.resetSectionRow || position == NotificationsSettingsActivity.this.badgeNumberSection || position == NotificationsSettingsActivity.this.otherSection2Row || position == NotificationsSettingsActivity.this.resetSection2Row || position == NotificationsSettingsActivity.this.callsSection2Row || position == NotificationsSettingsActivity.this.callsSectionRow || position == NotificationsSettingsActivity.this.badgeNumberSection2Row) ? false : true;
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
                    view = new NotificationsCheckCell(this.mContext);
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
            switch (holder.getItemViewType()) {
                case 0:
                    HeaderCell headerCell = holder.itemView;
                    if (position == NotificationsSettingsActivity.this.notificationsSectionRow) {
                        headerCell.setText(LocaleController.getString("NotificationsForChats", CLASSNAMER.string.NotificationsForChats));
                        return;
                    } else if (position == NotificationsSettingsActivity.this.inappSectionRow) {
                        headerCell.setText(LocaleController.getString("InAppNotifications", CLASSNAMER.string.InAppNotifications));
                        return;
                    } else if (position == NotificationsSettingsActivity.this.eventsSectionRow) {
                        headerCell.setText(LocaleController.getString("Events", CLASSNAMER.string.Events));
                        return;
                    } else if (position == NotificationsSettingsActivity.this.otherSectionRow) {
                        headerCell.setText(LocaleController.getString("NotificationsOther", CLASSNAMER.string.NotificationsOther));
                        return;
                    } else if (position == NotificationsSettingsActivity.this.resetSectionRow) {
                        headerCell.setText(LocaleController.getString("Reset", CLASSNAMER.string.Reset));
                        return;
                    } else if (position == NotificationsSettingsActivity.this.callsSectionRow) {
                        headerCell.setText(LocaleController.getString("VoipNotificationSettings", CLASSNAMER.string.VoipNotificationSettings));
                        return;
                    } else if (position == NotificationsSettingsActivity.this.badgeNumberSection) {
                        headerCell.setText(LocaleController.getString("BadgeNumber", CLASSNAMER.string.BadgeNumber));
                        return;
                    } else {
                        return;
                    }
                case 1:
                    TextCheckCell checkCell = holder.itemView;
                    preferences = MessagesController.getNotificationsSettings(NotificationsSettingsActivity.this.currentAccount);
                    if (position == NotificationsSettingsActivity.this.inappSoundRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("InAppSounds", CLASSNAMER.string.InAppSounds), preferences.getBoolean("EnableInAppSounds", true), true);
                        return;
                    } else if (position == NotificationsSettingsActivity.this.inappVibrateRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("InAppVibrate", CLASSNAMER.string.InAppVibrate), preferences.getBoolean("EnableInAppVibrate", true), true);
                        return;
                    } else if (position == NotificationsSettingsActivity.this.inappPreviewRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("InAppPreview", CLASSNAMER.string.InAppPreview), preferences.getBoolean("EnableInAppPreview", true), true);
                        return;
                    } else if (position == NotificationsSettingsActivity.this.inappPriorityRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("NotificationsImportance", CLASSNAMER.string.NotificationsImportance), preferences.getBoolean("EnableInAppPriority", false), false);
                        return;
                    } else if (position == NotificationsSettingsActivity.this.contactJoinedRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("ContactJoined", CLASSNAMER.string.ContactJoined), preferences.getBoolean("EnableContactJoined", true), true);
                        return;
                    } else if (position == NotificationsSettingsActivity.this.pinnedMessageRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("PinnedMessages", CLASSNAMER.string.PinnedMessages), preferences.getBoolean("PinnedMessages", true), false);
                        return;
                    } else if (position == NotificationsSettingsActivity.this.androidAutoAlertRow) {
                        checkCell.setTextAndCheck("Android Auto", preferences.getBoolean("EnableAutoNotifications", false), true);
                        return;
                    } else if (position == NotificationsSettingsActivity.this.notificationsServiceRow) {
                        checkCell.setTextAndValueAndCheck(LocaleController.getString("NotificationsService", CLASSNAMER.string.NotificationsService), LocaleController.getString("NotificationsServiceInfo", CLASSNAMER.string.NotificationsServiceInfo), preferences.getBoolean("pushService", true), true, true);
                        return;
                    } else if (position == NotificationsSettingsActivity.this.notificationsServiceConnectionRow) {
                        checkCell.setTextAndValueAndCheck(LocaleController.getString("NotificationsServiceConnection", CLASSNAMER.string.NotificationsServiceConnection), LocaleController.getString("NotificationsServiceConnectionInfo", CLASSNAMER.string.NotificationsServiceConnectionInfo), preferences.getBoolean("pushConnection", true), true, true);
                        return;
                    } else if (position == NotificationsSettingsActivity.this.badgeNumberShowRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("BadgeNumberShow", CLASSNAMER.string.BadgeNumberShow), NotificationsController.getInstance(NotificationsSettingsActivity.this.currentAccount).showBadgeNumber, true);
                        return;
                    } else if (position == NotificationsSettingsActivity.this.badgeNumberMutedRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("BadgeNumberMutedChats", CLASSNAMER.string.BadgeNumberMutedChats), NotificationsController.getInstance(NotificationsSettingsActivity.this.currentAccount).showBadgeMuted, true);
                        return;
                    } else if (position == NotificationsSettingsActivity.this.badgeNumberMessagesRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("BadgeNumberUnread", CLASSNAMER.string.BadgeNumberUnread), NotificationsController.getInstance(NotificationsSettingsActivity.this.currentAccount).showBadgeMessages, false);
                        return;
                    } else if (position == NotificationsSettingsActivity.this.inchatSoundRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("InChatSound", CLASSNAMER.string.InChatSound), preferences.getBoolean("EnableInChatSound", true), true);
                        return;
                    } else if (position == NotificationsSettingsActivity.this.callsVibrateRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("Vibrate", CLASSNAMER.string.Vibrate), preferences.getBoolean("EnableCallVibrate", true), true);
                        return;
                    } else {
                        return;
                    }
                case 2:
                    TextDetailSettingsCell settingsCell = holder.itemView;
                    settingsCell.setMultilineDetail(true);
                    if (position == NotificationsSettingsActivity.this.resetNotificationsRow) {
                        settingsCell.setTextAndValue(LocaleController.getString("ResetAllNotifications", CLASSNAMER.string.ResetAllNotifications), LocaleController.getString("UndoAllCustom", CLASSNAMER.string.UndoAllCustom), false);
                        return;
                    }
                    return;
                case 3:
                    String text;
                    ArrayList<NotificationException> exceptions;
                    int offUntil;
                    int iconType;
                    boolean z;
                    NotificationsCheckCell checkCell2 = holder.itemView;
                    StringBuilder builder = new StringBuilder();
                    preferences = MessagesController.getNotificationsSettings(NotificationsSettingsActivity.this.currentAccount);
                    if (position == NotificationsSettingsActivity.this.privateRow) {
                        text = LocaleController.getString("NotificationsPrivateChats", CLASSNAMER.string.NotificationsPrivateChats);
                        exceptions = NotificationsSettingsActivity.this.exceptionUsers;
                        offUntil = preferences.getInt("EnableAll2", 0);
                    } else if (position == NotificationsSettingsActivity.this.groupRow) {
                        text = LocaleController.getString("NotificationsGroups", CLASSNAMER.string.NotificationsGroups);
                        exceptions = NotificationsSettingsActivity.this.exceptionChats;
                        offUntil = preferences.getInt("EnableGroup2", 0);
                    } else {
                        text = LocaleController.getString("NotificationsChannels", CLASSNAMER.string.NotificationsChannels);
                        exceptions = NotificationsSettingsActivity.this.exceptionChannels;
                        offUntil = preferences.getInt("EnableChannel2", 0);
                    }
                    int currentTime = ConnectionsManager.getInstance(NotificationsSettingsActivity.this.currentAccount).getCurrentTime();
                    boolean enabled = offUntil < currentTime;
                    if (enabled) {
                        builder.append(LocaleController.getString("NotificationsOn", CLASSNAMER.string.NotificationsOn));
                        iconType = 0;
                    } else if (offUntil - 31536000 >= currentTime) {
                        builder.append(LocaleController.getString("NotificationsOff", CLASSNAMER.string.NotificationsOff));
                        iconType = 0;
                    } else {
                        builder.append(LocaleController.formatString("NotificationsOffUntil", CLASSNAMER.string.NotificationsOffUntil, LocaleController.stringForMessageListDate((long) offUntil)));
                        iconType = 2;
                    }
                    if (!(exceptions == null || exceptions.isEmpty())) {
                        if (builder.length() != 0) {
                            builder.append(", ");
                        }
                        builder.append(LocaleController.formatPluralString("Exception", exceptions.size()));
                    }
                    if (position != NotificationsSettingsActivity.this.channelsRow) {
                        z = true;
                    } else {
                        z = false;
                    }
                    checkCell2.setTextAndValueAndCheck(text, builder, enabled, iconType, z);
                    return;
                case 5:
                    TextSettingsCell textCell = holder.itemView;
                    preferences = MessagesController.getNotificationsSettings(NotificationsSettingsActivity.this.currentAccount);
                    String value;
                    if (position == NotificationsSettingsActivity.this.callsRingtoneRow) {
                        value = preferences.getString("CallsRingtone", LocaleController.getString("DefaultRingtone", CLASSNAMER.string.DefaultRingtone));
                        if (value.equals("NoSound")) {
                            value = LocaleController.getString("NoSound", CLASSNAMER.string.NoSound);
                        }
                        textCell.setTextAndValue(LocaleController.getString("VoipSettingsRingtone", CLASSNAMER.string.VoipSettingsRingtone), value, false);
                        return;
                    } else if (position == NotificationsSettingsActivity.this.callsVibrateRow) {
                        int value2 = 0;
                        if (position == NotificationsSettingsActivity.this.callsVibrateRow) {
                            value2 = preferences.getInt("vibrate_calls", 0);
                        }
                        if (value2 == 0) {
                            textCell.setTextAndValue(LocaleController.getString("Vibrate", CLASSNAMER.string.Vibrate), LocaleController.getString("VibrationDefault", CLASSNAMER.string.VibrationDefault), true);
                            return;
                        } else if (value2 == 1) {
                            textCell.setTextAndValue(LocaleController.getString("Vibrate", CLASSNAMER.string.Vibrate), LocaleController.getString("Short", CLASSNAMER.string.Short), true);
                            return;
                        } else if (value2 == 2) {
                            textCell.setTextAndValue(LocaleController.getString("Vibrate", CLASSNAMER.string.Vibrate), LocaleController.getString("VibrationDisabled", CLASSNAMER.string.VibrationDisabled), true);
                            return;
                        } else if (value2 == 3) {
                            textCell.setTextAndValue(LocaleController.getString("Vibrate", CLASSNAMER.string.Vibrate), LocaleController.getString("Long", CLASSNAMER.string.Long), true);
                            return;
                        } else if (value2 == 4) {
                            textCell.setTextAndValue(LocaleController.getString("Vibrate", CLASSNAMER.string.Vibrate), LocaleController.getString("OnlyIfSilent", CLASSNAMER.string.OnlyIfSilent), true);
                            return;
                        } else {
                            return;
                        }
                    } else if (position == NotificationsSettingsActivity.this.repeatRow) {
                        int minutes = preferences.getInt("repeat_messages", 60);
                        if (minutes == 0) {
                            value = LocaleController.getString("RepeatNotificationsNever", CLASSNAMER.string.RepeatNotificationsNever);
                        } else if (minutes < 60) {
                            value = LocaleController.formatPluralString("Minutes", minutes);
                        } else {
                            value = LocaleController.formatPluralString("Hours", minutes / 60);
                        }
                        textCell.setTextAndValue(LocaleController.getString("RepeatNotifications", CLASSNAMER.string.RepeatNotifications), value, false);
                        return;
                    } else {
                        return;
                    }
                default:
                    return;
            }
        }

        public int getItemViewType(int position) {
            if (position == NotificationsSettingsActivity.this.eventsSectionRow || position == NotificationsSettingsActivity.this.otherSectionRow || position == NotificationsSettingsActivity.this.resetSectionRow || position == NotificationsSettingsActivity.this.callsSectionRow || position == NotificationsSettingsActivity.this.badgeNumberSection || position == NotificationsSettingsActivity.this.inappSectionRow || position == NotificationsSettingsActivity.this.notificationsSectionRow) {
                return 0;
            }
            if (position == NotificationsSettingsActivity.this.inappSoundRow || position == NotificationsSettingsActivity.this.inappVibrateRow || position == NotificationsSettingsActivity.this.notificationsServiceConnectionRow || position == NotificationsSettingsActivity.this.inappPreviewRow || position == NotificationsSettingsActivity.this.contactJoinedRow || position == NotificationsSettingsActivity.this.pinnedMessageRow || position == NotificationsSettingsActivity.this.notificationsServiceRow || position == NotificationsSettingsActivity.this.badgeNumberMutedRow || position == NotificationsSettingsActivity.this.badgeNumberMessagesRow || position == NotificationsSettingsActivity.this.badgeNumberShowRow || position == NotificationsSettingsActivity.this.inappPriorityRow || position == NotificationsSettingsActivity.this.inchatSoundRow || position == NotificationsSettingsActivity.this.androidAutoAlertRow) {
                return 1;
            }
            if (position == NotificationsSettingsActivity.this.resetNotificationsRow) {
                return 2;
            }
            if (position == NotificationsSettingsActivity.this.privateRow || position == NotificationsSettingsActivity.this.groupRow || position == NotificationsSettingsActivity.this.channelsRow) {
                return 3;
            }
            if (position == NotificationsSettingsActivity.this.eventsSection2Row || position == NotificationsSettingsActivity.this.notificationsSection2Row || position == NotificationsSettingsActivity.this.otherSection2Row || position == NotificationsSettingsActivity.this.resetSection2Row || position == NotificationsSettingsActivity.this.callsSection2Row || position == NotificationsSettingsActivity.this.badgeNumberSection2Row) {
                return 4;
            }
            return 5;
        }
    }

    /* renamed from: org.telegram.ui.NotificationsSettingsActivity$NotificationException */
    public static class NotificationException {
        public long did;
        public boolean hasCustom;
        public int muteUntil;
        public int notify;
    }

    public boolean onFragmentCreate() {
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new NotificationsSettingsActivity$$Lambda$0(this));
        int i = this.rowCount;
        this.rowCount = i + 1;
        this.notificationsSectionRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.privateRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.groupRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.channelsRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.notificationsSection2Row = i;
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
        this.eventsSection2Row = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.badgeNumberSection = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.badgeNumberShowRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.badgeNumberMutedRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.badgeNumberMessagesRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.badgeNumberSection2Row = i;
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
        this.callsSection2Row = i;
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
        this.otherSection2Row = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.otherSectionRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.notificationsServiceRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.notificationsServiceConnectionRow = i;
        this.androidAutoAlertRow = -1;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.repeatRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.resetSection2Row = i;
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
        NotificationException exception;
        User user;
        Chat chat;
        ArrayList<NotificationException> usersResult = new ArrayList();
        ArrayList<NotificationException> chatsResult = new ArrayList();
        ArrayList<NotificationException> channelsResult = new ArrayList();
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
                    exception = new NotificationException();
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
                            } else if (!(chat.left || chat.kicked || chat.migrated_to != null)) {
                                if (!ChatObject.isChannel(chat) || chat.megagroup) {
                                    chatsResult.add(exception);
                                } else {
                                    channelsResult.add(exception);
                                }
                            }
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
                FileLog.m13e(e);
            }
            int size = chats.size();
            for (a = 0; a < size; a++) {
                chat = (Chat) chats.get(a);
                if (!(chat.left || chat.kicked || chat.migrated_to != null)) {
                    exception = (NotificationException) waitingForLoadExceptions.get((long) (-chat.var_id));
                    waitingForLoadExceptions.remove((long) (-chat.var_id));
                    if (exception != null) {
                        if (!ChatObject.isChannel(chat) || chat.megagroup) {
                            chatsResult.add(exception);
                        } else {
                            channelsResult.add(exception);
                        }
                    }
                }
            }
            size = users.size();
            for (a = 0; a < size; a++) {
                user = (User) users.get(a);
                if (!user.deleted) {
                    waitingForLoadExceptions.remove((long) user.var_id);
                }
            }
            size = encryptedChats.size();
            for (a = 0; a < size; a++) {
                waitingForLoadExceptions.remove(((long) ((EncryptedChat) encryptedChats.get(a)).var_id) << 32);
            }
            size = waitingForLoadExceptions.size();
            for (a = 0; a < size; a++) {
                if (((int) waitingForLoadExceptions.keyAt(a)) < 0) {
                    chatsResult.remove(waitingForLoadExceptions.valueAt(a));
                    channelsResult.remove(waitingForLoadExceptions.valueAt(a));
                } else {
                    usersResult.remove(waitingForLoadExceptions.valueAt(a));
                }
            }
        }
        AndroidUtilities.runOnUIThread(new NotificationsSettingsActivity$$Lambda$8(this, users, chats, encryptedChats, usersResult, chatsResult, channelsResult));
    }

    final /* synthetic */ void lambda$null$0$NotificationsSettingsActivity(ArrayList users, ArrayList chats, ArrayList encryptedChats, ArrayList usersResult, ArrayList chatsResult, ArrayList channelsResult) {
        MessagesController.getInstance(this.currentAccount).putUsers(users, true);
        MessagesController.getInstance(this.currentAccount).putChats(chats, true);
        MessagesController.getInstance(this.currentAccount).putEncryptedChats(encryptedChats, true);
        this.exceptionUsers = usersResult;
        this.exceptionChats = chatsResult;
        this.exceptionChannels = channelsResult;
        this.adapter.notifyItemChanged(this.privateRow);
        this.adapter.notifyItemChanged(this.groupRow);
        this.adapter.notifyItemChanged(this.channelsRow);
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.notificationsSettingsUpdated);
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(CLASSNAMER.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("NotificationsAndSounds", CLASSNAMER.string.NotificationsAndSounds));
        this.actionBar.setActionBarMenuOnItemClick(new CLASSNAME());
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

    final /* synthetic */ void lambda$createView$7$NotificationsSettingsActivity(View view, int position, float x, float y) {
        boolean enabled = false;
        if (getParentActivity() != null) {
            if (position == this.privateRow || position == this.groupRow || position == this.channelsRow) {
                int type;
                ArrayList<NotificationException> exceptions;
                if (position == this.privateRow) {
                    type = 1;
                    exceptions = this.exceptionUsers;
                } else {
                    if (position == this.groupRow) {
                        type = 0;
                        exceptions = this.exceptionChats;
                    } else {
                        type = 2;
                        exceptions = this.exceptionChannels;
                    }
                }
                NotificationsCheckCell checkCell = (NotificationsCheckCell) view;
                enabled = NotificationsController.getInstance(this.currentAccount).isGlobalNotificationsEnabled(type);
                if ((!LocaleController.isRTL || x > ((float) AndroidUtilities.m9dp(76.0f))) && (LocaleController.isRTL || x < ((float) (view.getMeasuredWidth() - AndroidUtilities.m9dp(76.0f))))) {
                    presentFragment(new NotificationsCustomSettingsActivity(type, exceptions));
                } else {
                    NotificationsController.getInstance(this.currentAccount).setGlobalNotificationsEnabled(type, !enabled ? 0 : ConnectionsManager.DEFAULT_DATACENTER_ID);
                    showExceptionsAlert(position);
                    checkCell.setChecked(!enabled, 0);
                    this.adapter.notifyItemChanged(position);
                }
            } else {
                SharedPreferences preferences;
                if (position == this.callsRingtoneRow) {
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
                        if (position == this.callsRingtoneRow) {
                            String path = preferences.getString("CallsRingtonfePath", defaultPath);
                            if (!(path == null || path.equals("NoSound"))) {
                                currentSound = path.equals(defaultPath) ? defaultUri : Uri.parse(path);
                            }
                        }
                        intent.putExtra("android.intent.extra.ringtone.EXISTING_URI", currentSound);
                        startActivityForResult(intent, position);
                    } catch (Throwable e) {
                        FileLog.m13e(e);
                    }
                } else {
                    Builder builder;
                    if (position == this.resetNotificationsRow) {
                        builder = new Builder(getParentActivity());
                        builder.setMessage(LocaleController.getString("ResetNotificationsAlert", CLASSNAMER.string.ResetNotificationsAlert));
                        builder.setTitle(LocaleController.getString("AppName", CLASSNAMER.string.AppName));
                        builder.setPositiveButton(LocaleController.getString("Reset", CLASSNAMER.string.Reset), new NotificationsSettingsActivity$$Lambda$3(this));
                        builder.setNegativeButton(LocaleController.getString("Cancel", CLASSNAMER.string.Cancel), null);
                        showDialog(builder.create());
                    } else {
                        Editor editor;
                        if (position == this.inappSoundRow) {
                            preferences = MessagesController.getNotificationsSettings(this.currentAccount);
                            editor = preferences.edit();
                            enabled = preferences.getBoolean("EnableInAppSounds", true);
                            editor.putBoolean("EnableInAppSounds", !enabled);
                            editor.commit();
                        } else {
                            if (position == this.inappVibrateRow) {
                                preferences = MessagesController.getNotificationsSettings(this.currentAccount);
                                editor = preferences.edit();
                                enabled = preferences.getBoolean("EnableInAppVibrate", true);
                                editor.putBoolean("EnableInAppVibrate", !enabled);
                                editor.commit();
                            } else {
                                if (position == this.inappPreviewRow) {
                                    preferences = MessagesController.getNotificationsSettings(this.currentAccount);
                                    editor = preferences.edit();
                                    enabled = preferences.getBoolean("EnableInAppPreview", true);
                                    editor.putBoolean("EnableInAppPreview", !enabled);
                                    editor.commit();
                                } else {
                                    if (position == this.inchatSoundRow) {
                                        preferences = MessagesController.getNotificationsSettings(this.currentAccount);
                                        editor = preferences.edit();
                                        enabled = preferences.getBoolean("EnableInChatSound", true);
                                        editor.putBoolean("EnableInChatSound", !enabled);
                                        editor.commit();
                                        NotificationsController.getInstance(this.currentAccount).setInChatSoundEnabled(!enabled);
                                    } else {
                                        if (position == this.inappPriorityRow) {
                                            preferences = MessagesController.getNotificationsSettings(this.currentAccount);
                                            editor = preferences.edit();
                                            enabled = preferences.getBoolean("EnableInAppPriority", false);
                                            editor.putBoolean("EnableInAppPriority", !enabled);
                                            editor.commit();
                                        } else {
                                            if (position == this.contactJoinedRow) {
                                                preferences = MessagesController.getNotificationsSettings(this.currentAccount);
                                                editor = preferences.edit();
                                                enabled = preferences.getBoolean("EnableContactJoined", true);
                                                MessagesController.getInstance(this.currentAccount).enableJoined = !enabled;
                                                editor.putBoolean("EnableContactJoined", !enabled);
                                                editor.commit();
                                            } else {
                                                if (position == this.pinnedMessageRow) {
                                                    preferences = MessagesController.getNotificationsSettings(this.currentAccount);
                                                    editor = preferences.edit();
                                                    enabled = preferences.getBoolean("PinnedMessages", true);
                                                    editor.putBoolean("PinnedMessages", !enabled);
                                                    editor.commit();
                                                } else {
                                                    if (position == this.androidAutoAlertRow) {
                                                        preferences = MessagesController.getNotificationsSettings(this.currentAccount);
                                                        editor = preferences.edit();
                                                        enabled = preferences.getBoolean("EnableAutoNotifications", false);
                                                        editor.putBoolean("EnableAutoNotifications", !enabled);
                                                        editor.commit();
                                                    } else {
                                                        if (position == this.badgeNumberShowRow) {
                                                            editor = MessagesController.getNotificationsSettings(this.currentAccount).edit();
                                                            enabled = NotificationsController.getInstance(this.currentAccount).showBadgeNumber;
                                                            NotificationsController.getInstance(this.currentAccount).showBadgeNumber = !enabled;
                                                            editor.putBoolean("badgeNumber", NotificationsController.getInstance(this.currentAccount).showBadgeNumber);
                                                            editor.commit();
                                                            NotificationsController.getInstance(this.currentAccount).updateBadge();
                                                        } else {
                                                            if (position == this.badgeNumberMutedRow) {
                                                                editor = MessagesController.getNotificationsSettings(this.currentAccount).edit();
                                                                enabled = NotificationsController.getInstance(this.currentAccount).showBadgeMuted;
                                                                NotificationsController.getInstance(this.currentAccount).showBadgeMuted = !enabled;
                                                                editor.putBoolean("badgeNumberMuted", NotificationsController.getInstance(this.currentAccount).showBadgeMuted);
                                                                editor.commit();
                                                                NotificationsController.getInstance(this.currentAccount).updateBadge();
                                                            } else {
                                                                if (position == this.badgeNumberMessagesRow) {
                                                                    editor = MessagesController.getNotificationsSettings(this.currentAccount).edit();
                                                                    enabled = NotificationsController.getInstance(this.currentAccount).showBadgeMessages;
                                                                    NotificationsController.getInstance(this.currentAccount).showBadgeMessages = !enabled;
                                                                    editor.putBoolean("badgeNumberMessages", NotificationsController.getInstance(this.currentAccount).showBadgeMessages);
                                                                    editor.commit();
                                                                    NotificationsController.getInstance(this.currentAccount).updateBadge();
                                                                } else {
                                                                    if (position == this.notificationsServiceConnectionRow) {
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
                                                                    } else {
                                                                        if (position == this.notificationsServiceRow) {
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
                                                                        } else {
                                                                            if (position != this.callsVibrateRow) {
                                                                                if (position == this.repeatRow) {
                                                                                    builder = new Builder(getParentActivity());
                                                                                    builder.setTitle(LocaleController.getString("RepeatNotifications", CLASSNAMER.string.RepeatNotifications));
                                                                                    builder.setItems(new CharSequence[]{LocaleController.getString("RepeatDisabled", CLASSNAMER.string.RepeatDisabled), LocaleController.formatPluralString("Minutes", 5), LocaleController.formatPluralString("Minutes", 10), LocaleController.formatPluralString("Minutes", 30), LocaleController.formatPluralString("Hours", 1), LocaleController.formatPluralString("Hours", 2), LocaleController.formatPluralString("Hours", 4)}, new NotificationsSettingsActivity$$Lambda$5(this, position));
                                                                                    builder.setNegativeButton(LocaleController.getString("Cancel", CLASSNAMER.string.Cancel), null);
                                                                                    showDialog(builder.create());
                                                                                }
                                                                            } else if (getParentActivity() != null) {
                                                                                String key = null;
                                                                                if (position == this.callsVibrateRow) {
                                                                                    key = "vibrate_calls";
                                                                                }
                                                                                showDialog(AlertsCreator.createVibrationSelectDialog(getParentActivity(), 0, key, new NotificationsSettingsActivity$$Lambda$4(this, position)));
                                                                            } else {
                                                                                return;
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (view instanceof TextCheckCell) {
                boolean z;
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
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_account_resetNotifySettings(), new NotificationsSettingsActivity$$Lambda$6(this));
        }
    }

    final /* synthetic */ void lambda$null$3$NotificationsSettingsActivity(TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new NotificationsSettingsActivity$$Lambda$7(this));
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
            Toast.makeText(getParentActivity(), LocaleController.getString("ResetNotificationsText", CLASSNAMER.string.ResetNotificationsText), 0).show();
        }
    }

    final /* synthetic */ void lambda$null$5$NotificationsSettingsActivity(int position) {
        this.adapter.notifyItemChanged(position);
    }

    final /* synthetic */ void lambda$null$6$NotificationsSettingsActivity(int position, DialogInterface dialog, int which) {
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

    public void onActivityResultFragment(int requestCode, int resultCode, Intent data) {
        if (resultCode == -1) {
            Uri ringtone = (Uri) data.getParcelableExtra("android.intent.extra.ringtone.PICKED_URI");
            String name = null;
            if (ringtone != null) {
                Ringtone rng = RingtoneManager.getRingtone(getParentActivity(), ringtone);
                if (rng != null) {
                    if (requestCode == this.callsRingtoneRow) {
                        if (ringtone.equals(System.DEFAULT_RINGTONE_URI)) {
                            name = LocaleController.getString("DefaultRingtone", CLASSNAMER.string.DefaultRingtone);
                        } else {
                            name = rng.getTitle(getParentActivity());
                        }
                    } else if (ringtone.equals(System.DEFAULT_NOTIFICATION_URI)) {
                        name = LocaleController.getString("SoundDefault", CLASSNAMER.string.SoundDefault);
                    } else {
                        name = rng.getTitle(getParentActivity());
                    }
                    rng.stop();
                }
            }
            Editor editor = MessagesController.getNotificationsSettings(this.currentAccount).edit();
            if (requestCode == this.callsRingtoneRow) {
                if (name == null || ringtone == null) {
                    editor.putString("CallsRingtone", "NoSound");
                    editor.putString("CallsRingtonePath", "NoSound");
                } else {
                    editor.putString("CallsRingtone", name);
                    editor.putString("CallsRingtonePath", ringtone.toString());
                }
            }
            editor.commit();
            this.adapter.notifyItemChanged(requestCode);
        }
    }

    private void showExceptionsAlert(int position) {
        ArrayList<NotificationException> exceptions;
        String alertText = null;
        if (position == this.privateRow) {
            exceptions = this.exceptionUsers;
            if (!(exceptions == null || exceptions.isEmpty())) {
                alertText = LocaleController.formatPluralString("ChatsException", exceptions.size());
            }
        } else if (position == this.groupRow) {
            exceptions = this.exceptionChats;
            if (!(exceptions == null || exceptions.isEmpty())) {
                alertText = LocaleController.formatPluralString("Groups", exceptions.size());
            }
        } else {
            exceptions = this.exceptionChannels;
            if (!(exceptions == null || exceptions.isEmpty())) {
                alertText = LocaleController.formatPluralString("Channels", exceptions.size());
            }
        }
        if (alertText != null) {
            Builder builder = new Builder(getParentActivity());
            if (exceptions.size() == 1) {
                builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("NotificationsExceptionsSingleAlert", CLASSNAMER.string.NotificationsExceptionsSingleAlert, alertText)));
            } else {
                builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("NotificationsExceptionsAlert", CLASSNAMER.string.NotificationsExceptionsAlert, alertText)));
            }
            builder.setTitle(LocaleController.getString("NotificationsExceptions", CLASSNAMER.string.NotificationsExceptions));
            builder.setNeutralButton(LocaleController.getString("ViewExceptions", CLASSNAMER.string.ViewExceptions), new NotificationsSettingsActivity$$Lambda$2(this, exceptions));
            builder.setNegativeButton(LocaleController.getString("OK", CLASSNAMER.string.OK), null);
            showDialog(builder.create());
        }
    }

    final /* synthetic */ void lambda$showExceptionsAlert$8$NotificationsSettingsActivity(ArrayList exceptions, DialogInterface dialogInterface, int i) {
        presentFragment(new NotificationsCustomSettingsActivity(-1, exceptions));
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
        r9 = new ThemeDescription[23];
        r9[0] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{HeaderCell.class, TextCheckCell.class, TextDetailSettingsCell.class, TextSettingsCell.class, NotificationsCheckCell.class}, null, null, null, Theme.key_windowBackgroundWhite);
        r9[1] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray);
        r9[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault);
        r9[3] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault);
        r9[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon);
        r9[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle);
        r9[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector);
        r9[7] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector);
        r9[8] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider);
        r9[9] = new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlueHeader);
        r9[10] = new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r9[11] = new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2);
        r9[12] = new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrack);
        r9[13] = new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrackChecked);
        r9[14] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r9[15] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2);
        r9[16] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrack);
        r9[17] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrackChecked);
        r9[18] = new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r9[19] = new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteValueText);
        r9[20] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        r9[21] = new ThemeDescription(this.listView, 0, new Class[]{TextDetailSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r9[22] = new ThemeDescription(this.listView, 0, new Class[]{TextDetailSettingsCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2);
        return r9;
    }
}
