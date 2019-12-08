package org.telegram.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.NotificationsController;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_account_resetNotifySettings;
import org.telegram.tgnet.TLRPC.TL_account_setContactSignUpNotification;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.NotificationsCheckCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextDetailSettingsCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;

public class NotificationsSettingsActivity extends BaseFragment implements NotificationCenterDelegate {
    private int accountsAllRow;
    private int accountsInfoRow;
    private int accountsSectionRow;
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
    private LinearLayoutManager layoutManager;
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

    public static class NotificationException {
        public long did;
        public boolean hasCustom;
        public int muteUntil;
        public int notify;
    }

    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            int adapterPosition = viewHolder.getAdapterPosition();
            return (adapterPosition == NotificationsSettingsActivity.this.notificationsSectionRow || adapterPosition == NotificationsSettingsActivity.this.notificationsSection2Row || adapterPosition == NotificationsSettingsActivity.this.inappSectionRow || adapterPosition == NotificationsSettingsActivity.this.eventsSectionRow || adapterPosition == NotificationsSettingsActivity.this.otherSectionRow || adapterPosition == NotificationsSettingsActivity.this.resetSectionRow || adapterPosition == NotificationsSettingsActivity.this.badgeNumberSection || adapterPosition == NotificationsSettingsActivity.this.otherSection2Row || adapterPosition == NotificationsSettingsActivity.this.resetSection2Row || adapterPosition == NotificationsSettingsActivity.this.callsSection2Row || adapterPosition == NotificationsSettingsActivity.this.callsSectionRow || adapterPosition == NotificationsSettingsActivity.this.badgeNumberSection2Row || adapterPosition == NotificationsSettingsActivity.this.accountsSectionRow || adapterPosition == NotificationsSettingsActivity.this.accountsInfoRow) ? false : true;
        }

        public int getItemCount() {
            return NotificationsSettingsActivity.this.rowCount;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View headerCell;
            String str = "windowBackgroundWhite";
            if (i == 0) {
                headerCell = new HeaderCell(this.mContext);
                headerCell.setBackgroundColor(Theme.getColor(str));
            } else if (i == 1) {
                headerCell = new TextCheckCell(this.mContext);
                headerCell.setBackgroundColor(Theme.getColor(str));
            } else if (i == 2) {
                headerCell = new TextDetailSettingsCell(this.mContext);
                headerCell.setBackgroundColor(Theme.getColor(str));
            } else if (i != 3) {
                View shadowSectionCell;
                if (i == 4) {
                    shadowSectionCell = new ShadowSectionCell(this.mContext);
                } else if (i != 5) {
                    shadowSectionCell = new TextInfoPrivacyCell(this.mContext);
                    shadowSectionCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                } else {
                    headerCell = new TextSettingsCell(this.mContext);
                    headerCell.setBackgroundColor(Theme.getColor(str));
                }
                headerCell = shadowSectionCell;
            } else {
                headerCell = new NotificationsCheckCell(this.mContext);
                headerCell.setBackgroundColor(Theme.getColor(str));
            }
            return new Holder(headerCell);
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType != 0) {
                String str = "Vibrate";
                boolean z = false;
                SharedPreferences notificationsSettings;
                String str2;
                String str3;
                if (itemViewType == 1) {
                    TextCheckCell textCheckCell = (TextCheckCell) viewHolder.itemView;
                    notificationsSettings = MessagesController.getNotificationsSettings(NotificationsSettingsActivity.this.currentAccount);
                    if (i == NotificationsSettingsActivity.this.inappSoundRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("InAppSounds", NUM), notificationsSettings.getBoolean("EnableInAppSounds", true), true);
                        return;
                    } else if (i == NotificationsSettingsActivity.this.inappVibrateRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("InAppVibrate", NUM), notificationsSettings.getBoolean("EnableInAppVibrate", true), true);
                        return;
                    } else if (i == NotificationsSettingsActivity.this.inappPreviewRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("InAppPreview", NUM), notificationsSettings.getBoolean("EnableInAppPreview", true), true);
                        return;
                    } else if (i == NotificationsSettingsActivity.this.inappPriorityRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("NotificationsImportance", NUM), notificationsSettings.getBoolean("EnableInAppPriority", false), false);
                        return;
                    } else if (i == NotificationsSettingsActivity.this.contactJoinedRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("ContactJoined", NUM), notificationsSettings.getBoolean("EnableContactJoined", true), true);
                        return;
                    } else if (i == NotificationsSettingsActivity.this.pinnedMessageRow) {
                        str2 = "PinnedMessages";
                        textCheckCell.setTextAndCheck(LocaleController.getString(str2, NUM), notificationsSettings.getBoolean(str2, true), false);
                        return;
                    } else if (i == NotificationsSettingsActivity.this.androidAutoAlertRow) {
                        textCheckCell.setTextAndCheck("Android Auto", notificationsSettings.getBoolean("EnableAutoNotifications", false), true);
                        return;
                    } else if (i == NotificationsSettingsActivity.this.notificationsServiceRow) {
                        textCheckCell.setTextAndValueAndCheck(LocaleController.getString("NotificationsService", NUM), LocaleController.getString("NotificationsServiceInfo", NUM), notificationsSettings.getBoolean("pushService", true), true, true);
                        return;
                    } else if (i == NotificationsSettingsActivity.this.notificationsServiceConnectionRow) {
                        textCheckCell.setTextAndValueAndCheck(LocaleController.getString("NotificationsServiceConnection", NUM), LocaleController.getString("NotificationsServiceConnectionInfo", NUM), notificationsSettings.getBoolean("pushConnection", true), true, true);
                        return;
                    } else if (i == NotificationsSettingsActivity.this.badgeNumberShowRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("BadgeNumberShow", NUM), NotificationsController.getInstance(NotificationsSettingsActivity.this.currentAccount).showBadgeNumber, true);
                        return;
                    } else if (i == NotificationsSettingsActivity.this.badgeNumberMutedRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("BadgeNumberMutedChats", NUM), NotificationsController.getInstance(NotificationsSettingsActivity.this.currentAccount).showBadgeMuted, true);
                        return;
                    } else if (i == NotificationsSettingsActivity.this.badgeNumberMessagesRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("BadgeNumberUnread", NUM), NotificationsController.getInstance(NotificationsSettingsActivity.this.currentAccount).showBadgeMessages, false);
                        return;
                    } else if (i == NotificationsSettingsActivity.this.inchatSoundRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("InChatSound", NUM), notificationsSettings.getBoolean("EnableInChatSound", true), true);
                        return;
                    } else if (i == NotificationsSettingsActivity.this.callsVibrateRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString(str, NUM), notificationsSettings.getBoolean("EnableCallVibrate", true), true);
                        return;
                    } else if (i == NotificationsSettingsActivity.this.accountsAllRow) {
                        str3 = "AllAccounts";
                        textCheckCell.setTextAndCheck(LocaleController.getString(str3, NUM), MessagesController.getGlobalNotificationsSettings().getBoolean(str3, true), false);
                        return;
                    } else {
                        return;
                    }
                } else if (itemViewType == 2) {
                    TextDetailSettingsCell textDetailSettingsCell = (TextDetailSettingsCell) viewHolder.itemView;
                    textDetailSettingsCell.setMultilineDetail(true);
                    if (i == NotificationsSettingsActivity.this.resetNotificationsRow) {
                        textDetailSettingsCell.setTextAndValue(LocaleController.getString("ResetAllNotifications", NUM), LocaleController.getString("UndoAllCustom", NUM), false);
                        return;
                    }
                    return;
                } else if (itemViewType == 3) {
                    String string;
                    ArrayList access$3800;
                    int i2;
                    NotificationsCheckCell notificationsCheckCell = (NotificationsCheckCell) viewHolder.itemView;
                    notificationsSettings = MessagesController.getNotificationsSettings(NotificationsSettingsActivity.this.currentAccount);
                    itemViewType = ConnectionsManager.getInstance(NotificationsSettingsActivity.this.currentAccount).getCurrentTime();
                    if (i == NotificationsSettingsActivity.this.privateRow) {
                        string = LocaleController.getString("NotificationsPrivateChats", NUM);
                        access$3800 = NotificationsSettingsActivity.this.exceptionUsers;
                        i2 = notificationsSettings.getInt("EnableAll2", 0);
                    } else if (i == NotificationsSettingsActivity.this.groupRow) {
                        string = LocaleController.getString("NotificationsGroups", NUM);
                        access$3800 = NotificationsSettingsActivity.this.exceptionChats;
                        i2 = notificationsSettings.getInt("EnableGroup2", 0);
                    } else {
                        string = LocaleController.getString("NotificationsChannels", NUM);
                        access$3800 = NotificationsSettingsActivity.this.exceptionChannels;
                        i2 = notificationsSettings.getInt("EnableChannel2", 0);
                    }
                    String str4 = string;
                    boolean z2 = i2 < itemViewType;
                    int i3 = (!z2 && i2 - 31536000 < itemViewType) ? 2 : 0;
                    StringBuilder stringBuilder = new StringBuilder();
                    if (access$3800 == null || access$3800.isEmpty()) {
                        stringBuilder.append(LocaleController.getString("TapToChange", NUM));
                    } else {
                        z2 = i2 < itemViewType;
                        if (z2) {
                            stringBuilder.append(LocaleController.getString("NotificationsOn", NUM));
                        } else if (i2 - 31536000 >= itemViewType) {
                            stringBuilder.append(LocaleController.getString("NotificationsOff", NUM));
                        } else {
                            stringBuilder.append(LocaleController.formatString("NotificationsOffUntil", NUM, LocaleController.stringForMessageListDate((long) i2)));
                        }
                        if (stringBuilder.length() != 0) {
                            stringBuilder.append(", ");
                        }
                        stringBuilder.append(LocaleController.formatPluralString("Exception", access$3800.size()));
                    }
                    notificationsCheckCell.setTextAndValueAndCheck(str4, stringBuilder, z2, i3, i != NotificationsSettingsActivity.this.channelsRow);
                    return;
                } else if (itemViewType == 5) {
                    TextSettingsCell textSettingsCell = (TextSettingsCell) viewHolder.itemView;
                    SharedPreferences notificationsSettings2 = MessagesController.getNotificationsSettings(NotificationsSettingsActivity.this.currentAccount);
                    if (i == NotificationsSettingsActivity.this.callsRingtoneRow) {
                        str3 = notificationsSettings2.getString("CallsRingtone", LocaleController.getString("DefaultRingtone", NUM));
                        str2 = "NoSound";
                        if (str3.equals(str2)) {
                            str3 = LocaleController.getString(str2, NUM);
                        }
                        textSettingsCell.setTextAndValue(LocaleController.getString("VoipSettingsRingtone", NUM), str3, false);
                        return;
                    } else if (i == NotificationsSettingsActivity.this.callsVibrateRow) {
                        if (i == NotificationsSettingsActivity.this.callsVibrateRow) {
                            z = notificationsSettings2.getInt("vibrate_calls", 0);
                        }
                        if (!z) {
                            textSettingsCell.setTextAndValue(LocaleController.getString(str, NUM), LocaleController.getString("VibrationDefault", NUM), true);
                            return;
                        } else if (z) {
                            textSettingsCell.setTextAndValue(LocaleController.getString(str, NUM), LocaleController.getString("Short", NUM), true);
                            return;
                        } else if (z) {
                            textSettingsCell.setTextAndValue(LocaleController.getString(str, NUM), LocaleController.getString("VibrationDisabled", NUM), true);
                            return;
                        } else if (z) {
                            textSettingsCell.setTextAndValue(LocaleController.getString(str, NUM), LocaleController.getString("Long", NUM), true);
                            return;
                        } else if (z) {
                            textSettingsCell.setTextAndValue(LocaleController.getString(str, NUM), LocaleController.getString("OnlyIfSilent", NUM), true);
                            return;
                        } else {
                            return;
                        }
                    } else if (i == NotificationsSettingsActivity.this.repeatRow) {
                        itemViewType = notificationsSettings2.getInt("repeat_messages", 60);
                        if (itemViewType == 0) {
                            str3 = LocaleController.getString("RepeatNotificationsNever", NUM);
                        } else if (itemViewType < 60) {
                            str3 = LocaleController.formatPluralString("Minutes", itemViewType);
                        } else {
                            str3 = LocaleController.formatPluralString("Hours", itemViewType / 60);
                        }
                        textSettingsCell.setTextAndValue(LocaleController.getString("RepeatNotifications", NUM), str3, false);
                        return;
                    } else {
                        return;
                    }
                } else if (itemViewType == 6) {
                    TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) viewHolder.itemView;
                    if (i == NotificationsSettingsActivity.this.accountsInfoRow) {
                        textInfoPrivacyCell.setText(LocaleController.getString("ShowNotificationsForInfo", NUM));
                        return;
                    }
                    return;
                } else {
                    return;
                }
            }
            HeaderCell headerCell = (HeaderCell) viewHolder.itemView;
            if (i == NotificationsSettingsActivity.this.notificationsSectionRow) {
                headerCell.setText(LocaleController.getString("NotificationsForChats", NUM));
            } else if (i == NotificationsSettingsActivity.this.inappSectionRow) {
                headerCell.setText(LocaleController.getString("InAppNotifications", NUM));
            } else if (i == NotificationsSettingsActivity.this.eventsSectionRow) {
                headerCell.setText(LocaleController.getString("Events", NUM));
            } else if (i == NotificationsSettingsActivity.this.otherSectionRow) {
                headerCell.setText(LocaleController.getString("NotificationsOther", NUM));
            } else if (i == NotificationsSettingsActivity.this.resetSectionRow) {
                headerCell.setText(LocaleController.getString("Reset", NUM));
            } else if (i == NotificationsSettingsActivity.this.callsSectionRow) {
                headerCell.setText(LocaleController.getString("VoipNotificationSettings", NUM));
            } else if (i == NotificationsSettingsActivity.this.badgeNumberSection) {
                headerCell.setText(LocaleController.getString("BadgeNumber", NUM));
            } else if (i == NotificationsSettingsActivity.this.accountsSectionRow) {
                headerCell.setText(LocaleController.getString("ShowNotificationsFor", NUM));
            }
        }

        public int getItemViewType(int i) {
            if (i == NotificationsSettingsActivity.this.eventsSectionRow || i == NotificationsSettingsActivity.this.otherSectionRow || i == NotificationsSettingsActivity.this.resetSectionRow || i == NotificationsSettingsActivity.this.callsSectionRow || i == NotificationsSettingsActivity.this.badgeNumberSection || i == NotificationsSettingsActivity.this.inappSectionRow || i == NotificationsSettingsActivity.this.notificationsSectionRow || i == NotificationsSettingsActivity.this.accountsSectionRow) {
                return 0;
            }
            if (i == NotificationsSettingsActivity.this.inappSoundRow || i == NotificationsSettingsActivity.this.inappVibrateRow || i == NotificationsSettingsActivity.this.notificationsServiceConnectionRow || i == NotificationsSettingsActivity.this.inappPreviewRow || i == NotificationsSettingsActivity.this.contactJoinedRow || i == NotificationsSettingsActivity.this.pinnedMessageRow || i == NotificationsSettingsActivity.this.notificationsServiceRow || i == NotificationsSettingsActivity.this.badgeNumberMutedRow || i == NotificationsSettingsActivity.this.badgeNumberMessagesRow || i == NotificationsSettingsActivity.this.badgeNumberShowRow || i == NotificationsSettingsActivity.this.inappPriorityRow || i == NotificationsSettingsActivity.this.inchatSoundRow || i == NotificationsSettingsActivity.this.androidAutoAlertRow || i == NotificationsSettingsActivity.this.accountsAllRow) {
                return 1;
            }
            if (i == NotificationsSettingsActivity.this.resetNotificationsRow) {
                return 2;
            }
            if (i == NotificationsSettingsActivity.this.privateRow || i == NotificationsSettingsActivity.this.groupRow || i == NotificationsSettingsActivity.this.channelsRow) {
                return 3;
            }
            if (i == NotificationsSettingsActivity.this.eventsSection2Row || i == NotificationsSettingsActivity.this.notificationsSection2Row || i == NotificationsSettingsActivity.this.otherSection2Row || i == NotificationsSettingsActivity.this.resetSection2Row || i == NotificationsSettingsActivity.this.callsSection2Row || i == NotificationsSettingsActivity.this.badgeNumberSection2Row) {
                return 4;
            }
            return i == NotificationsSettingsActivity.this.accountsInfoRow ? 6 : 5;
        }
    }

    static /* synthetic */ void lambda$null$5(TLObject tLObject, TL_error tL_error) {
    }

    public boolean onFragmentCreate() {
        int i;
        MessagesController.getInstance(this.currentAccount).loadSignUpNotificationsSettings();
        loadExceptions();
        if (UserConfig.getActivatedAccountsCount() > 1) {
            i = this.rowCount;
            this.rowCount = i + 1;
            this.accountsSectionRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.accountsAllRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.accountsInfoRow = i;
        } else {
            this.accountsSectionRow = -1;
            this.accountsAllRow = -1;
            this.accountsInfoRow = -1;
        }
        i = this.rowCount;
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

    private void loadExceptions() {
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new -$$Lambda$NotificationsSettingsActivity$uXiQpIA5YYdlg9kVau5gYRsTuCM(this));
    }

    /* JADX WARNING: Removed duplicated region for block: B:90:0x0242  */
    /* JADX WARNING: Removed duplicated region for block: B:107:0x0283  */
    /* JADX WARNING: Removed duplicated region for block: B:113:0x029e A:{LOOP_END, LOOP:3: B:112:0x029c->B:113:0x029e} */
    /* JADX WARNING: Removed duplicated region for block: B:116:0x02b5  */
    /* JADX WARNING: Removed duplicated region for block: B:90:0x0242  */
    /* JADX WARNING: Removed duplicated region for block: B:107:0x0283  */
    /* JADX WARNING: Removed duplicated region for block: B:113:0x029e A:{LOOP_END, LOOP:3: B:112:0x029c->B:113:0x029e} */
    /* JADX WARNING: Removed duplicated region for block: B:116:0x02b5  */
    /* JADX WARNING: Removed duplicated region for block: B:90:0x0242  */
    /* JADX WARNING: Removed duplicated region for block: B:107:0x0283  */
    /* JADX WARNING: Removed duplicated region for block: B:113:0x029e A:{LOOP_END, LOOP:3: B:112:0x029c->B:113:0x029e} */
    /* JADX WARNING: Removed duplicated region for block: B:116:0x02b5  */
    /* JADX WARNING: Missing block: B:21:0x0107, code skipped:
            if (r11.deleted != false) goto L_0x0150;
     */
    /* JADX WARNING: Missing block: B:48:0x01a8, code skipped:
            if (r7.deleted != false) goto L_0x01c7;
     */
    public /* synthetic */ void lambda$loadExceptions$1$NotificationsSettingsActivity() {
        /*
        r23 = this;
        r9 = r23;
        r6 = new java.util.ArrayList;
        r6.<init>();
        r7 = new java.util.ArrayList;
        r7.<init>();
        r8 = new java.util.ArrayList;
        r8.<init>();
        r1 = new android.util.LongSparseArray;
        r1.<init>();
        r0 = new java.util.ArrayList;
        r0.<init>();
        r2 = new java.util.ArrayList;
        r2.<init>();
        r3 = new java.util.ArrayList;
        r3.<init>();
        r4 = new java.util.ArrayList;
        r4.<init>();
        r5 = new java.util.ArrayList;
        r5.<init>();
        r10 = new java.util.ArrayList;
        r10.<init>();
        r11 = r9.currentAccount;
        r11 = org.telegram.messenger.UserConfig.getInstance(r11);
        r11 = r11.clientUserId;
        r12 = r9.currentAccount;
        r12 = org.telegram.messenger.MessagesController.getNotificationsSettings(r12);
        r13 = r12.getAll();
        r14 = r13.entrySet();
        r14 = r14.iterator();
    L_0x004e:
        r15 = r14.hasNext();
        r16 = 32;
        r17 = r5;
        if (r15 == 0) goto L_0x01d6;
    L_0x0058:
        r15 = r14.next();
        r15 = (java.util.Map.Entry) r15;
        r18 = r15.getKey();
        r5 = r18;
        r5 = (java.lang.String) r5;
        r18 = r14;
        r14 = "notify2_";
        r19 = r5.startsWith(r14);
        if (r19 == 0) goto L_0x01be;
    L_0x0070:
        r19 = r4;
        r4 = "";
        r4 = r5.replace(r14, r4);
        r5 = org.telegram.messenger.Utilities.parseLong(r4);
        r14 = r7;
        r20 = r8;
        r7 = r5.longValue();
        r21 = 0;
        r5 = (r7 > r21 ? 1 : (r7 == r21 ? 0 : -1));
        if (r5 == 0) goto L_0x01b7;
    L_0x0089:
        r5 = r2;
        r21 = r3;
        r2 = (long) r11;
        r22 = (r7 > r2 ? 1 : (r7 == r2 ? 0 : -1));
        if (r22 == 0) goto L_0x01af;
    L_0x0091:
        r2 = new org.telegram.ui.NotificationsSettingsActivity$NotificationException;
        r2.<init>();
        r2.did = r7;
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r22 = r11;
        r11 = "custom_";
        r3.append(r11);
        r3.append(r7);
        r3 = r3.toString();
        r11 = 0;
        r3 = r12.getBoolean(r3, r11);
        r2.hasCustom = r3;
        r3 = r15.getValue();
        r3 = (java.lang.Integer) r3;
        r3 = r3.intValue();
        r2.notify = r3;
        r3 = r2.notify;
        if (r3 == 0) goto L_0x00e1;
    L_0x00c2:
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r11 = "notifyuntil_";
        r3.append(r11);
        r3.append(r4);
        r3 = r3.toString();
        r3 = r13.get(r3);
        r3 = (java.lang.Integer) r3;
        if (r3 == 0) goto L_0x00e1;
    L_0x00db:
        r3 = r3.intValue();
        r2.muteUntil = r3;
    L_0x00e1:
        r3 = (int) r7;
        r4 = r12;
        r11 = r7 << r16;
        r12 = (int) r11;
        if (r3 == 0) goto L_0x0161;
    L_0x00e8:
        if (r3 <= 0) goto L_0x010f;
    L_0x00ea:
        r11 = r9.currentAccount;
        r11 = org.telegram.messenger.MessagesController.getInstance(r11);
        r12 = java.lang.Integer.valueOf(r3);
        r11 = r11.getUser(r12);
        if (r11 != 0) goto L_0x0105;
    L_0x00fa:
        r3 = java.lang.Integer.valueOf(r3);
        r0.add(r3);
        r1.put(r7, r2);
        goto L_0x010a;
    L_0x0105:
        r3 = r11.deleted;
        if (r3 == 0) goto L_0x010a;
    L_0x0109:
        goto L_0x0150;
    L_0x010a:
        r6.add(r2);
        goto L_0x01b2;
    L_0x010f:
        r11 = r9.currentAccount;
        r11 = org.telegram.messenger.MessagesController.getInstance(r11);
        r3 = -r3;
        r12 = java.lang.Integer.valueOf(r3);
        r11 = r11.getChat(r12);
        if (r11 != 0) goto L_0x012b;
    L_0x0120:
        r3 = java.lang.Integer.valueOf(r3);
        r5.add(r3);
        r1.put(r7, r2);
        goto L_0x0150;
    L_0x012b:
        r3 = r11.left;
        if (r3 != 0) goto L_0x0150;
    L_0x012f:
        r3 = r11.kicked;
        if (r3 != 0) goto L_0x0150;
    L_0x0133:
        r3 = r11.migrated_to;
        if (r3 == 0) goto L_0x0138;
    L_0x0137:
        goto L_0x0150;
    L_0x0138:
        r3 = org.telegram.messenger.ChatObject.isChannel(r11);
        if (r3 == 0) goto L_0x0149;
    L_0x013e:
        r3 = r11.megagroup;
        if (r3 != 0) goto L_0x0149;
    L_0x0142:
        r15 = r20;
        r15.add(r2);
        goto L_0x01b4;
    L_0x0149:
        r15 = r20;
        r14.add(r2);
        goto L_0x01b4;
    L_0x0150:
        r12 = r4;
        r2 = r5;
        r7 = r14;
        r5 = r17;
        r14 = r18;
        r4 = r19;
        r8 = r20;
        r3 = r21;
        r11 = r22;
        goto L_0x004e;
    L_0x0161:
        r15 = r20;
        if (r12 == 0) goto L_0x01b4;
    L_0x0165:
        r3 = r9.currentAccount;
        r3 = org.telegram.messenger.MessagesController.getInstance(r3);
        r11 = java.lang.Integer.valueOf(r12);
        r3 = r3.getEncryptedChat(r11);
        if (r3 != 0) goto L_0x0182;
    L_0x0175:
        r3 = java.lang.Integer.valueOf(r12);
        r12 = r21;
        r12.add(r3);
        r1.put(r7, r2);
        goto L_0x01ab;
    L_0x0182:
        r12 = r21;
        r7 = r9.currentAccount;
        r7 = org.telegram.messenger.MessagesController.getInstance(r7);
        r8 = r3.user_id;
        r8 = java.lang.Integer.valueOf(r8);
        r7 = r7.getUser(r8);
        if (r7 != 0) goto L_0x01a6;
    L_0x0196:
        r7 = r3.user_id;
        r7 = java.lang.Integer.valueOf(r7);
        r0.add(r7);
        r3 = r3.user_id;
        r7 = (long) r3;
        r1.put(r7, r2);
        goto L_0x01ab;
    L_0x01a6:
        r3 = r7.deleted;
        if (r3 == 0) goto L_0x01ab;
    L_0x01aa:
        goto L_0x01c7;
    L_0x01ab:
        r6.add(r2);
        goto L_0x01c7;
    L_0x01af:
        r22 = r11;
        r4 = r12;
    L_0x01b2:
        r15 = r20;
    L_0x01b4:
        r12 = r21;
        goto L_0x01c7;
    L_0x01b7:
        r5 = r2;
        r22 = r11;
        r4 = r12;
        r15 = r20;
        goto L_0x01c6;
    L_0x01be:
        r5 = r2;
        r19 = r4;
        r14 = r7;
        r15 = r8;
        r22 = r11;
        r4 = r12;
    L_0x01c6:
        r12 = r3;
    L_0x01c7:
        r2 = r5;
        r3 = r12;
        r7 = r14;
        r8 = r15;
        r5 = r17;
        r14 = r18;
        r11 = r22;
        r12 = r4;
        r4 = r19;
        goto L_0x004e;
    L_0x01d6:
        r5 = r2;
        r12 = r3;
        r19 = r4;
        r14 = r7;
        r15 = r8;
        r11 = 0;
        r2 = r1.size();
        if (r2 == 0) goto L_0x02d5;
    L_0x01e3:
        r2 = r12.isEmpty();	 Catch:{ Exception -> 0x0233 }
        r3 = ",";
        if (r2 != 0) goto L_0x01f8;
    L_0x01eb:
        r2 = r9.currentAccount;	 Catch:{ Exception -> 0x0233 }
        r2 = org.telegram.messenger.MessagesStorage.getInstance(r2);	 Catch:{ Exception -> 0x0233 }
        r4 = android.text.TextUtils.join(r3, r12);	 Catch:{ Exception -> 0x0233 }
        r2.getEncryptedChatsInternal(r4, r10, r0);	 Catch:{ Exception -> 0x0233 }
    L_0x01f8:
        r2 = r0.isEmpty();	 Catch:{ Exception -> 0x0233 }
        if (r2 != 0) goto L_0x0212;
    L_0x01fe:
        r2 = r9.currentAccount;	 Catch:{ Exception -> 0x020e }
        r2 = org.telegram.messenger.MessagesStorage.getInstance(r2);	 Catch:{ Exception -> 0x020e }
        r0 = android.text.TextUtils.join(r3, r0);	 Catch:{ Exception -> 0x020e }
        r4 = r19;
        r2.getUsersInternal(r0, r4);	 Catch:{ Exception -> 0x022f }
        goto L_0x0214;
    L_0x020e:
        r0 = move-exception;
        r4 = r19;
        goto L_0x0230;
    L_0x0212:
        r4 = r19;
    L_0x0214:
        r0 = r5.isEmpty();	 Catch:{ Exception -> 0x022f }
        if (r0 != 0) goto L_0x022c;
    L_0x021a:
        r0 = r9.currentAccount;	 Catch:{ Exception -> 0x022f }
        r0 = org.telegram.messenger.MessagesStorage.getInstance(r0);	 Catch:{ Exception -> 0x022f }
        r2 = android.text.TextUtils.join(r3, r5);	 Catch:{ Exception -> 0x022f }
        r5 = r17;
        r0.getChatsInternal(r2, r5);	 Catch:{ Exception -> 0x022a }
        goto L_0x023b;
    L_0x022a:
        r0 = move-exception;
        goto L_0x0238;
    L_0x022c:
        r5 = r17;
        goto L_0x023b;
    L_0x022f:
        r0 = move-exception;
    L_0x0230:
        r5 = r17;
        goto L_0x0238;
    L_0x0233:
        r0 = move-exception;
        r5 = r17;
        r4 = r19;
    L_0x0238:
        org.telegram.messenger.FileLog.e(r0);
    L_0x023b:
        r0 = r5.size();
        r2 = 0;
    L_0x0240:
        if (r2 >= r0) goto L_0x027c;
    L_0x0242:
        r3 = r5.get(r2);
        r3 = (org.telegram.tgnet.TLRPC.Chat) r3;
        r7 = r3.left;
        if (r7 != 0) goto L_0x0279;
    L_0x024c:
        r7 = r3.kicked;
        if (r7 != 0) goto L_0x0279;
    L_0x0250:
        r7 = r3.migrated_to;
        if (r7 == 0) goto L_0x0255;
    L_0x0254:
        goto L_0x0279;
    L_0x0255:
        r7 = r3.id;
        r7 = -r7;
        r7 = (long) r7;
        r7 = r1.get(r7);
        r7 = (org.telegram.ui.NotificationsSettingsActivity.NotificationException) r7;
        r8 = r3.id;
        r8 = -r8;
        r12 = (long) r8;
        r1.remove(r12);
        if (r7 == 0) goto L_0x0279;
    L_0x0268:
        r8 = org.telegram.messenger.ChatObject.isChannel(r3);
        if (r8 == 0) goto L_0x0276;
    L_0x026e:
        r3 = r3.megagroup;
        if (r3 != 0) goto L_0x0276;
    L_0x0272:
        r15.add(r7);
        goto L_0x0279;
    L_0x0276:
        r14.add(r7);
    L_0x0279:
        r2 = r2 + 1;
        goto L_0x0240;
    L_0x027c:
        r0 = r4.size();
        r2 = 0;
    L_0x0281:
        if (r2 >= r0) goto L_0x0297;
    L_0x0283:
        r3 = r4.get(r2);
        r3 = (org.telegram.tgnet.TLRPC.User) r3;
        r7 = r3.deleted;
        if (r7 == 0) goto L_0x028e;
    L_0x028d:
        goto L_0x0294;
    L_0x028e:
        r3 = r3.id;
        r7 = (long) r3;
        r1.remove(r7);
    L_0x0294:
        r2 = r2 + 1;
        goto L_0x0281;
    L_0x0297:
        r0 = r10.size();
        r2 = 0;
    L_0x029c:
        if (r2 >= r0) goto L_0x02af;
    L_0x029e:
        r3 = r10.get(r2);
        r3 = (org.telegram.tgnet.TLRPC.EncryptedChat) r3;
        r3 = r3.id;
        r7 = (long) r3;
        r7 = r7 << r16;
        r1.remove(r7);
        r2 = r2 + 1;
        goto L_0x029c;
    L_0x02af:
        r0 = r1.size();
    L_0x02b3:
        if (r11 >= r0) goto L_0x02d9;
    L_0x02b5:
        r2 = r1.keyAt(r11);
        r3 = (int) r2;
        if (r3 >= 0) goto L_0x02cb;
    L_0x02bc:
        r2 = r1.valueAt(r11);
        r14.remove(r2);
        r2 = r1.valueAt(r11);
        r15.remove(r2);
        goto L_0x02d2;
    L_0x02cb:
        r2 = r1.valueAt(r11);
        r6.remove(r2);
    L_0x02d2:
        r11 = r11 + 1;
        goto L_0x02b3;
    L_0x02d5:
        r5 = r17;
        r4 = r19;
    L_0x02d9:
        r0 = new org.telegram.ui.-$$Lambda$NotificationsSettingsActivity$tpC7zsNM6CXmGWZ2kbXQvv8Lbxg;
        r1 = r0;
        r2 = r23;
        r3 = r4;
        r4 = r5;
        r5 = r10;
        r7 = r14;
        r8 = r15;
        r1.<init>(r2, r3, r4, r5, r6, r7, r8);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r0);
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.NotificationsSettingsActivity.lambda$loadExceptions$1$NotificationsSettingsActivity():void");
    }

    public /* synthetic */ void lambda$null$0$NotificationsSettingsActivity(ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3, ArrayList arrayList4, ArrayList arrayList5, ArrayList arrayList6) {
        MessagesController.getInstance(this.currentAccount).putUsers(arrayList, true);
        MessagesController.getInstance(this.currentAccount).putChats(arrayList2, true);
        MessagesController.getInstance(this.currentAccount).putEncryptedChats(arrayList3, true);
        this.exceptionUsers = arrayList4;
        this.exceptionChats = arrayList5;
        this.exceptionChannels = arrayList6;
        this.adapter.notifyItemChanged(this.privateRow);
        this.adapter.notifyItemChanged(this.groupRow);
        this.adapter.notifyItemChanged(this.channelsRow);
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.notificationsSettingsUpdated);
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("NotificationsAndSounds", NUM));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    NotificationsSettingsActivity.this.finishFragment();
                }
            }
        });
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        this.listView = new RecyclerListView(context);
        this.listView.setItemAnimator(null);
        this.listView.setLayoutAnimation(null);
        RecyclerListView recyclerListView = this.listView;
        AnonymousClass2 anonymousClass2 = new LinearLayoutManager(context, 1, false) {
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        this.layoutManager = anonymousClass2;
        recyclerListView.setLayoutManager(anonymousClass2);
        this.listView.setVerticalScrollBarEnabled(false);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        RecyclerListView recyclerListView2 = this.listView;
        ListAdapter listAdapter = new ListAdapter(context);
        this.adapter = listAdapter;
        recyclerListView2.setAdapter(listAdapter);
        this.listView.setOnItemClickListener(new -$$Lambda$NotificationsSettingsActivity$g4GVhVtYkD5_YPczTHGsTY58dkg(this));
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$8$NotificationsSettingsActivity(View view, int i, float f, float f2) {
        if (getParentActivity() != null) {
            boolean z;
            int i2 = 2;
            boolean z2 = false;
            if (i == this.privateRow || i == this.groupRow || i == this.channelsRow) {
                ArrayList arrayList;
                if (i == this.privateRow) {
                    arrayList = this.exceptionUsers;
                    i2 = 1;
                } else if (i == this.groupRow) {
                    arrayList = this.exceptionChats;
                    i2 = 0;
                } else {
                    arrayList = this.exceptionChannels;
                }
                NotificationsCheckCell notificationsCheckCell = (NotificationsCheckCell) view;
                boolean isGlobalNotificationsEnabled = NotificationsController.getInstance(this.currentAccount).isGlobalNotificationsEnabled(i2);
                if ((!LocaleController.isRTL || f > ((float) AndroidUtilities.dp(76.0f))) && (LocaleController.isRTL || f < ((float) (view.getMeasuredWidth() - AndroidUtilities.dp(76.0f))))) {
                    presentFragment(new NotificationsCustomSettingsActivity(i2, arrayList));
                } else {
                    NotificationsController.getInstance(this.currentAccount).setGlobalNotificationsEnabled(i2, !isGlobalNotificationsEnabled ? 0 : Integer.MAX_VALUE);
                    showExceptionsAlert(i);
                    notificationsCheckCell.setChecked(isGlobalNotificationsEnabled ^ 1, 0);
                    this.adapter.notifyItemChanged(i);
                }
                z = isGlobalNotificationsEnabled;
            } else {
                String str = null;
                String path;
                if (i == this.callsRingtoneRow) {
                    try {
                        Parcelable parse;
                        SharedPreferences notificationsSettings = MessagesController.getNotificationsSettings(this.currentAccount);
                        Intent intent = new Intent("android.intent.action.RINGTONE_PICKER");
                        intent.putExtra("android.intent.extra.ringtone.TYPE", 1);
                        intent.putExtra("android.intent.extra.ringtone.SHOW_DEFAULT", true);
                        intent.putExtra("android.intent.extra.ringtone.SHOW_SILENT", true);
                        intent.putExtra("android.intent.extra.ringtone.DEFAULT_URI", RingtoneManager.getDefaultUri(1));
                        Uri uri = System.DEFAULT_RINGTONE_URI;
                        path = uri != null ? uri.getPath() : null;
                        String string = notificationsSettings.getString("CallsRingtonePath", path);
                        if (!(string == null || string.equals("NoSound"))) {
                            parse = string.equals(path) ? uri : Uri.parse(string);
                        }
                        intent.putExtra("android.intent.extra.ringtone.EXISTING_URI", parse);
                        startActivityForResult(intent, i);
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                } else {
                    path = "Cancel";
                    SharedPreferences notificationsSettings2;
                    Editor edit;
                    if (i == this.resetNotificationsRow) {
                        Builder builder = new Builder(getParentActivity());
                        builder.setMessage(LocaleController.getString("ResetNotificationsAlert", NUM));
                        builder.setTitle(LocaleController.getString("AppName", NUM));
                        builder.setPositiveButton(LocaleController.getString("Reset", NUM), new -$$Lambda$NotificationsSettingsActivity$yCH91Gy9ARU8yn1KTl14GsaHDf4(this));
                        builder.setNegativeButton(LocaleController.getString(path, NUM), null);
                        showDialog(builder.create());
                    } else if (i == this.inappSoundRow) {
                        notificationsSettings2 = MessagesController.getNotificationsSettings(this.currentAccount);
                        edit = notificationsSettings2.edit();
                        str = "EnableInAppSounds";
                        z = notificationsSettings2.getBoolean(str, true);
                        edit.putBoolean(str, z ^ 1);
                        edit.commit();
                    } else if (i == this.inappVibrateRow) {
                        notificationsSettings2 = MessagesController.getNotificationsSettings(this.currentAccount);
                        edit = notificationsSettings2.edit();
                        str = "EnableInAppVibrate";
                        z = notificationsSettings2.getBoolean(str, true);
                        edit.putBoolean(str, z ^ 1);
                        edit.commit();
                    } else if (i == this.inappPreviewRow) {
                        notificationsSettings2 = MessagesController.getNotificationsSettings(this.currentAccount);
                        edit = notificationsSettings2.edit();
                        str = "EnableInAppPreview";
                        z = notificationsSettings2.getBoolean(str, true);
                        edit.putBoolean(str, z ^ 1);
                        edit.commit();
                    } else if (i == this.inchatSoundRow) {
                        notificationsSettings2 = MessagesController.getNotificationsSettings(this.currentAccount);
                        edit = notificationsSettings2.edit();
                        str = "EnableInChatSound";
                        z = notificationsSettings2.getBoolean(str, true);
                        edit.putBoolean(str, z ^ 1);
                        edit.commit();
                        NotificationsController.getInstance(this.currentAccount).setInChatSoundEnabled(z ^ 1);
                    } else if (i == this.inappPriorityRow) {
                        notificationsSettings2 = MessagesController.getNotificationsSettings(this.currentAccount);
                        edit = notificationsSettings2.edit();
                        str = "EnableInAppPriority";
                        z = notificationsSettings2.getBoolean(str, false);
                        edit.putBoolean(str, z ^ 1);
                        edit.commit();
                    } else if (i == this.contactJoinedRow) {
                        notificationsSettings2 = MessagesController.getNotificationsSettings(this.currentAccount);
                        edit = notificationsSettings2.edit();
                        str = "EnableContactJoined";
                        z = notificationsSettings2.getBoolean(str, true);
                        MessagesController.getInstance(this.currentAccount).enableJoined = z ^ 1;
                        edit.putBoolean(str, z ^ 1);
                        edit.commit();
                        TL_account_setContactSignUpNotification tL_account_setContactSignUpNotification = new TL_account_setContactSignUpNotification();
                        tL_account_setContactSignUpNotification.silent = z;
                        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_account_setContactSignUpNotification, -$$Lambda$NotificationsSettingsActivity$viFpXODmAg-Q4M-X6ggvpEc5GAg.INSTANCE);
                    } else if (i == this.pinnedMessageRow) {
                        notificationsSettings2 = MessagesController.getNotificationsSettings(this.currentAccount);
                        edit = notificationsSettings2.edit();
                        z = notificationsSettings2.getBoolean("PinnedMessages", true);
                        edit.putBoolean("PinnedMessages", z ^ 1);
                        edit.commit();
                    } else if (i == this.androidAutoAlertRow) {
                        notificationsSettings2 = MessagesController.getNotificationsSettings(this.currentAccount);
                        edit = notificationsSettings2.edit();
                        z = notificationsSettings2.getBoolean("EnableAutoNotifications", false);
                        edit.putBoolean("EnableAutoNotifications", z ^ 1);
                        edit.commit();
                    } else {
                        boolean z3;
                        Editor edit2;
                        if (i == this.badgeNumberShowRow) {
                            edit2 = MessagesController.getNotificationsSettings(this.currentAccount).edit();
                            z3 = NotificationsController.getInstance(this.currentAccount).showBadgeNumber;
                            NotificationsController.getInstance(this.currentAccount).showBadgeNumber = z3 ^ 1;
                            edit2.putBoolean("badgeNumber", NotificationsController.getInstance(this.currentAccount).showBadgeNumber);
                            edit2.commit();
                            NotificationsController.getInstance(this.currentAccount).updateBadge();
                        } else if (i == this.badgeNumberMutedRow) {
                            edit2 = MessagesController.getNotificationsSettings(this.currentAccount).edit();
                            z3 = NotificationsController.getInstance(this.currentAccount).showBadgeMuted;
                            NotificationsController.getInstance(this.currentAccount).showBadgeMuted = z3 ^ 1;
                            edit2.putBoolean("badgeNumberMuted", NotificationsController.getInstance(this.currentAccount).showBadgeMuted);
                            edit2.commit();
                            NotificationsController.getInstance(this.currentAccount).updateBadge();
                        } else if (i == this.badgeNumberMessagesRow) {
                            edit2 = MessagesController.getNotificationsSettings(this.currentAccount).edit();
                            z3 = NotificationsController.getInstance(this.currentAccount).showBadgeMessages;
                            NotificationsController.getInstance(this.currentAccount).showBadgeMessages = z3 ^ 1;
                            edit2.putBoolean("badgeNumberMessages", NotificationsController.getInstance(this.currentAccount).showBadgeMessages);
                            edit2.commit();
                            NotificationsController.getInstance(this.currentAccount).updateBadge();
                        } else if (i == this.notificationsServiceConnectionRow) {
                            notificationsSettings2 = MessagesController.getNotificationsSettings(this.currentAccount);
                            z3 = notificationsSettings2.getBoolean("pushConnection", true);
                            edit2 = notificationsSettings2.edit();
                            edit2.putBoolean("pushConnection", z3 ^ 1);
                            edit2.commit();
                            if (z3) {
                                ConnectionsManager.getInstance(this.currentAccount).setPushConnectionEnabled(false);
                            } else {
                                ConnectionsManager.getInstance(this.currentAccount).setPushConnectionEnabled(true);
                            }
                        } else if (i == this.accountsAllRow) {
                            notificationsSettings2 = MessagesController.getGlobalNotificationsSettings();
                            z3 = notificationsSettings2.getBoolean("AllAccounts", true);
                            edit2 = notificationsSettings2.edit();
                            edit2.putBoolean("AllAccounts", z3 ^ 1);
                            edit2.commit();
                            SharedConfig.showNotificationsForAllAccounts = z3 ^ 1;
                            for (i = 0; i < 3; i++) {
                                if (SharedConfig.showNotificationsForAllAccounts) {
                                    NotificationsController.getInstance(i).showNotifications();
                                } else if (i == this.currentAccount) {
                                    NotificationsController.getInstance(i).showNotifications();
                                } else {
                                    NotificationsController.getInstance(i).hideNotifications();
                                }
                            }
                        } else if (i == this.notificationsServiceRow) {
                            notificationsSettings2 = MessagesController.getNotificationsSettings(this.currentAccount);
                            z3 = notificationsSettings2.getBoolean("pushService", true);
                            edit2 = notificationsSettings2.edit();
                            edit2.putBoolean("pushService", z3 ^ 1);
                            edit2.commit();
                            if (z3) {
                                ApplicationLoader.stopPushService();
                            } else {
                                ApplicationLoader.startPushService();
                            }
                        } else if (i == this.callsVibrateRow) {
                            if (getParentActivity() != null) {
                                if (i == this.callsVibrateRow) {
                                    str = "vibrate_calls";
                                }
                                showDialog(AlertsCreator.createVibrationSelectDialog(getParentActivity(), 0, str, new -$$Lambda$NotificationsSettingsActivity$7IXN7L8E_cyofxGsKJruA7N2DeY(this, i)));
                            } else {
                                return;
                            }
                        } else if (i == this.repeatRow) {
                            Builder builder2 = new Builder(getParentActivity());
                            builder2.setTitle(LocaleController.getString("RepeatNotifications", NUM));
                            r5 = new CharSequence[7];
                            String str2 = "Minutes";
                            r5[1] = LocaleController.formatPluralString(str2, 5);
                            r5[2] = LocaleController.formatPluralString(str2, 10);
                            r5[3] = LocaleController.formatPluralString(str2, 30);
                            str2 = "Hours";
                            r5[4] = LocaleController.formatPluralString(str2, 1);
                            r5[5] = LocaleController.formatPluralString(str2, 2);
                            r5[6] = LocaleController.formatPluralString(str2, 4);
                            builder2.setItems(r5, new -$$Lambda$NotificationsSettingsActivity$KVxXWyv-zLmmyeu95JQljLmRuOE(this, i));
                            builder2.setNegativeButton(LocaleController.getString(path, NUM), null);
                            showDialog(builder2.create());
                        }
                        z = z3;
                    }
                }
                z = false;
            }
            if (view instanceof TextCheckCell) {
                TextCheckCell textCheckCell = (TextCheckCell) view;
                if (!z) {
                    z2 = true;
                }
                textCheckCell.setChecked(z2);
            }
        }
    }

    public /* synthetic */ void lambda$null$4$NotificationsSettingsActivity(DialogInterface dialogInterface, int i) {
        if (!this.reseting) {
            this.reseting = true;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_account_resetNotifySettings(), new -$$Lambda$NotificationsSettingsActivity$aJyIXj-uVdoHSGnj-u9gTZci7Yo(this));
        }
    }

    public /* synthetic */ void lambda$null$3$NotificationsSettingsActivity(TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$NotificationsSettingsActivity$CxAkqO4VuKiAX9yDzFMx1BE_9aM(this));
    }

    public /* synthetic */ void lambda$null$2$NotificationsSettingsActivity() {
        MessagesController.getInstance(this.currentAccount).enableJoined = true;
        this.reseting = false;
        Editor edit = MessagesController.getNotificationsSettings(this.currentAccount).edit();
        edit.clear();
        edit.commit();
        this.exceptionChats.clear();
        this.exceptionUsers.clear();
        this.adapter.notifyDataSetChanged();
        if (getParentActivity() != null) {
            Toast.makeText(getParentActivity(), LocaleController.getString("ResetNotificationsText", NUM), 0).show();
        }
    }

    public /* synthetic */ void lambda$null$6$NotificationsSettingsActivity(int i) {
        this.adapter.notifyItemChanged(i);
    }

    public /* synthetic */ void lambda$null$7$NotificationsSettingsActivity(int i, DialogInterface dialogInterface, int i2) {
        int i3 = 5;
        if (i2 != 1) {
            i3 = i2 == 2 ? 10 : i2 == 3 ? 30 : i2 == 4 ? 60 : i2 == 5 ? 120 : i2 == 6 ? 240 : 0;
        }
        MessagesController.getNotificationsSettings(this.currentAccount).edit().putInt("repeat_messages", i3).commit();
        this.adapter.notifyItemChanged(i);
    }

    public void onActivityResultFragment(int i, int i2, Intent intent) {
        if (i2 == -1) {
            Uri uri = (Uri) intent.getParcelableExtra("android.intent.extra.ringtone.PICKED_URI");
            String str = null;
            if (uri != null) {
                Ringtone ringtone = RingtoneManager.getRingtone(getParentActivity(), uri);
                if (ringtone != null) {
                    if (i == this.callsRingtoneRow) {
                        if (uri.equals(System.DEFAULT_RINGTONE_URI)) {
                            str = LocaleController.getString("DefaultRingtone", NUM);
                        } else {
                            str = ringtone.getTitle(getParentActivity());
                        }
                    } else if (uri.equals(System.DEFAULT_NOTIFICATION_URI)) {
                        str = LocaleController.getString("SoundDefault", NUM);
                    } else {
                        str = ringtone.getTitle(getParentActivity());
                    }
                    ringtone.stop();
                }
            }
            Editor edit = MessagesController.getNotificationsSettings(this.currentAccount).edit();
            if (i == this.callsRingtoneRow) {
                String str2 = "CallsRingtonePath";
                String str3 = "CallsRingtone";
                if (str == null || uri == null) {
                    String str4 = "NoSound";
                    edit.putString(str3, str4);
                    edit.putString(str2, str4);
                } else {
                    edit.putString(str3, str);
                    edit.putString(str2, uri.toString());
                }
            }
            edit.commit();
            this.adapter.notifyItemChanged(i);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:22:0x004c  */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x004b A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x004b A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x004c  */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x004c  */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x004b A:{RETURN} */
    private void showExceptionsAlert(int r7) {
        /*
        r6 = this;
        r0 = r6.privateRow;
        r1 = 0;
        if (r7 != r0) goto L_0x001c;
    L_0x0005:
        r7 = r6.exceptionUsers;
        if (r7 == 0) goto L_0x001a;
    L_0x0009:
        r0 = r7.isEmpty();
        if (r0 != 0) goto L_0x001a;
    L_0x000f:
        r0 = r7.size();
        r2 = "ChatsException";
        r0 = org.telegram.messenger.LocaleController.formatPluralString(r2, r0);
        goto L_0x0049;
    L_0x001a:
        r0 = r1;
        goto L_0x0049;
    L_0x001c:
        r0 = r6.groupRow;
        if (r7 != r0) goto L_0x0035;
    L_0x0020:
        r7 = r6.exceptionChats;
        if (r7 == 0) goto L_0x001a;
    L_0x0024:
        r0 = r7.isEmpty();
        if (r0 != 0) goto L_0x001a;
    L_0x002a:
        r0 = r7.size();
        r2 = "Groups";
        r0 = org.telegram.messenger.LocaleController.formatPluralString(r2, r0);
        goto L_0x0049;
    L_0x0035:
        r7 = r6.exceptionChannels;
        if (r7 == 0) goto L_0x001a;
    L_0x0039:
        r0 = r7.isEmpty();
        if (r0 != 0) goto L_0x001a;
    L_0x003f:
        r0 = r7.size();
        r2 = "Channels";
        r0 = org.telegram.messenger.LocaleController.formatPluralString(r2, r0);
    L_0x0049:
        if (r0 != 0) goto L_0x004c;
    L_0x004b:
        return;
    L_0x004c:
        r2 = new org.telegram.ui.ActionBar.AlertDialog$Builder;
        r3 = r6.getParentActivity();
        r2.<init>(r3);
        r3 = r7.size();
        r4 = 0;
        r5 = 1;
        if (r3 != r5) goto L_0x0072;
    L_0x005d:
        r3 = NUM; // 0x7f0d06bc float:1.8745611E38 double:1.0531306293E-314;
        r5 = new java.lang.Object[r5];
        r5[r4] = r0;
        r0 = "NotificationsExceptionsSingleAlert";
        r0 = org.telegram.messenger.LocaleController.formatString(r0, r3, r5);
        r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0);
        r2.setMessage(r0);
        goto L_0x0086;
    L_0x0072:
        r3 = NUM; // 0x7f0d06bb float:1.874561E38 double:1.053130629E-314;
        r5 = new java.lang.Object[r5];
        r5[r4] = r0;
        r0 = "NotificationsExceptionsAlert";
        r0 = org.telegram.messenger.LocaleController.formatString(r0, r3, r5);
        r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0);
        r2.setMessage(r0);
    L_0x0086:
        r0 = NUM; // 0x7f0d06ba float:1.8745607E38 double:1.0531306283E-314;
        r3 = "NotificationsExceptions";
        r0 = org.telegram.messenger.LocaleController.getString(r3, r0);
        r2.setTitle(r0);
        r0 = NUM; // 0x7f0d0ab1 float:1.8747666E38 double:1.05313113E-314;
        r3 = "ViewExceptions";
        r0 = org.telegram.messenger.LocaleController.getString(r3, r0);
        r3 = new org.telegram.ui.-$$Lambda$NotificationsSettingsActivity$9FhV71oy8_vyXyR3LWFGjX-RReE;
        r3.<init>(r6, r7);
        r2.setNeutralButton(r0, r3);
        r7 = NUM; // 0x7f0d06dc float:1.8745676E38 double:1.053130645E-314;
        r0 = "OK";
        r7 = org.telegram.messenger.LocaleController.getString(r0, r7);
        r2.setNegativeButton(r7, r1);
        r7 = r2.create();
        r6.showDialog(r7);
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.NotificationsSettingsActivity.showExceptionsAlert(int):void");
    }

    public /* synthetic */ void lambda$showExceptionsAlert$9$NotificationsSettingsActivity(ArrayList arrayList, DialogInterface dialogInterface, int i) {
        presentFragment(new NotificationsCustomSettingsActivity(-1, arrayList));
    }

    public void onResume() {
        super.onResume();
        ListAdapter listAdapter = this.adapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.notificationsSettingsUpdated) {
            this.adapter.notifyDataSetChanged();
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[26];
        themeDescriptionArr[0] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{HeaderCell.class, TextCheckCell.class, TextDetailSettingsCell.class, TextSettingsCell.class, NotificationsCheckCell.class}, null, null, null, "windowBackgroundWhite");
        themeDescriptionArr[1] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundGray");
        themeDescriptionArr[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
        themeDescriptionArr[3] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault");
        themeDescriptionArr[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
        themeDescriptionArr[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
        themeDescriptionArr[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
        themeDescriptionArr[7] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
        themeDescriptionArr[8] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, "divider");
        View view = this.listView;
        Class[] clsArr = new Class[]{HeaderCell.class};
        String[] strArr = new String[1];
        strArr[0] = "textView";
        themeDescriptionArr[9] = new ThemeDescription(view, 0, clsArr, strArr, null, null, null, "windowBackgroundWhiteBlueHeader");
        themeDescriptionArr[10] = new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        view = this.listView;
        clsArr = new Class[]{NotificationsCheckCell.class};
        strArr = new String[1];
        strArr[0] = "valueTextView";
        themeDescriptionArr[11] = new ThemeDescription(view, 0, clsArr, strArr, null, null, null, "windowBackgroundWhiteGrayText2");
        view = this.listView;
        clsArr = new Class[]{NotificationsCheckCell.class};
        strArr = new String[1];
        strArr[0] = "checkBox";
        themeDescriptionArr[12] = new ThemeDescription(view, 0, clsArr, strArr, null, null, null, "switchTrack");
        themeDescriptionArr[13] = new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, null, null, null, "switchTrackChecked");
        themeDescriptionArr[14] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        themeDescriptionArr[15] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"valueTextView"}, null, null, null, "windowBackgroundWhiteGrayText2");
        themeDescriptionArr[16] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, "switchTrack");
        themeDescriptionArr[17] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, "switchTrackChecked");
        themeDescriptionArr[18] = new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        themeDescriptionArr[19] = new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, null, null, null, "windowBackgroundWhiteValueText");
        themeDescriptionArr[20] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, "windowBackgroundGrayShadow");
        themeDescriptionArr[21] = new ThemeDescription(this.listView, 0, new Class[]{TextDetailSettingsCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        themeDescriptionArr[22] = new ThemeDescription(this.listView, 0, new Class[]{TextDetailSettingsCell.class}, new String[]{"valueTextView"}, null, null, null, "windowBackgroundWhiteGrayText2");
        themeDescriptionArr[23] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, "windowBackgroundGrayShadow");
        themeDescriptionArr[24] = new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteGrayText4");
        themeDescriptionArr[25] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LINKCOLOR, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteLinkText");
        return themeDescriptionArr;
    }
}
