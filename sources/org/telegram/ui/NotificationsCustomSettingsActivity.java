package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.Drawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.provider.Settings.System;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.OnScrollListener;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationsController;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.NotificationsCheckCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextColorCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Cells.UserCell;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.ui.NotificationsSettingsActivity.NotificationException;

public class NotificationsCustomSettingsActivity extends BaseFragment {
    private static final int search_button = 0;
    private ListAdapter adapter;
    private int alertRow;
    private int alertSection2Row;
    private AnimatorSet animatorSet;
    private int currentType;
    private EmptyTextProgressView emptyView;
    private ArrayList<NotificationException> exceptions;
    private int exceptionsAddRow;
    private int exceptionsEndRow;
    private int exceptionsSection2Row;
    private int exceptionsStartRow;
    private int groupSection2Row;
    private RecyclerListView listView;
    private int messageLedRow;
    private int messagePopupNotificationRow;
    private int messagePriorityRow;
    private int messageSectionRow;
    private int messageSoundRow;
    private int messageVibrateRow;
    private int previewRow;
    private int rowCount;
    private SearchAdapter searchListViewAdapter;
    private boolean searchWas;
    private boolean searching;

    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            int itemViewType = viewHolder.getItemViewType();
            return (itemViewType == 0 || itemViewType == 4) ? false : true;
        }

        public int getItemCount() {
            return NotificationsCustomSettingsActivity.this.rowCount;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View headerCell;
            String str = "windowBackgroundWhite";
            switch (i) {
                case 0:
                    headerCell = new HeaderCell(this.mContext);
                    headerCell.setBackgroundColor(Theme.getColor(str));
                    break;
                case 1:
                    headerCell = new TextCheckCell(this.mContext);
                    headerCell.setBackgroundColor(Theme.getColor(str));
                    break;
                case 2:
                    headerCell = new UserCell(this.mContext, 6, 0, false);
                    headerCell.setBackgroundColor(Theme.getColor(str));
                    break;
                case 3:
                    headerCell = new TextColorCell(this.mContext);
                    headerCell.setBackgroundColor(Theme.getColor(str));
                    break;
                case 4:
                    headerCell = new ShadowSectionCell(this.mContext);
                    break;
                case 5:
                    headerCell = new TextSettingsCell(this.mContext);
                    headerCell.setBackgroundColor(Theme.getColor(str));
                    break;
                case 6:
                    headerCell = new NotificationsCheckCell(this.mContext);
                    headerCell.setBackgroundColor(Theme.getColor(str));
                    break;
                default:
                    headerCell = new TextCell(this.mContext);
                    headerCell.setBackgroundColor(Theme.getColor(str));
                    break;
            }
            return new Holder(headerCell);
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            boolean z = false;
            SharedPreferences notificationsSettings;
            String str;
            String string;
            switch (viewHolder.getItemViewType()) {
                case 0:
                    HeaderCell headerCell = (HeaderCell) viewHolder.itemView;
                    if (i == NotificationsCustomSettingsActivity.this.messageSectionRow) {
                        headerCell.setText(LocaleController.getString("SETTINGS", NUM));
                        return;
                    }
                    return;
                case 1:
                    TextCheckCell textCheckCell = (TextCheckCell) viewHolder.itemView;
                    notificationsSettings = MessagesController.getNotificationsSettings(NotificationsCustomSettingsActivity.this.currentAccount);
                    if (i == NotificationsCustomSettingsActivity.this.previewRow) {
                        boolean z2;
                        if (NotificationsCustomSettingsActivity.this.currentType == 1) {
                            z2 = notificationsSettings.getBoolean("EnablePreviewAll", true);
                        } else if (NotificationsCustomSettingsActivity.this.currentType == 0) {
                            z2 = notificationsSettings.getBoolean("EnablePreviewGroup", true);
                        } else {
                            z2 = notificationsSettings.getBoolean("EnablePreviewChannel", true);
                        }
                        textCheckCell.setTextAndCheck(LocaleController.getString("MessagePreview", NUM), z2, true);
                        return;
                    }
                    return;
                case 2:
                    UserCell userCell = (UserCell) viewHolder.itemView;
                    NotificationException notificationException = (NotificationException) NotificationsCustomSettingsActivity.this.exceptions.get(i - NotificationsCustomSettingsActivity.this.exceptionsStartRow);
                    if (i != NotificationsCustomSettingsActivity.this.exceptionsEndRow - 1) {
                        z = true;
                    }
                    userCell.setException(notificationException, null, z);
                    return;
                case 3:
                    TextColorCell textColorCell = (TextColorCell) viewHolder.itemView;
                    SharedPreferences notificationsSettings2 = MessagesController.getNotificationsSettings(NotificationsCustomSettingsActivity.this.currentAccount);
                    if (NotificationsCustomSettingsActivity.this.currentType == 1) {
                        i = notificationsSettings2.getInt("MessagesLed", -16776961);
                    } else if (NotificationsCustomSettingsActivity.this.currentType == 0) {
                        i = notificationsSettings2.getInt("GroupLed", -16776961);
                    } else {
                        i = notificationsSettings2.getInt("ChannelLed", -16776961);
                    }
                    int i2;
                    while (i2 < 9) {
                        if (TextColorCell.colorsToSave[i2] == i) {
                            i = TextColorCell.colors[i2];
                            textColorCell.setTextAndColor(LocaleController.getString("LedColor", NUM), i, true);
                            return;
                        }
                        i2++;
                    }
                    textColorCell.setTextAndColor(LocaleController.getString("LedColor", NUM), i, true);
                    return;
                case 4:
                    str = "windowBackgroundGrayShadow";
                    if (i == NotificationsCustomSettingsActivity.this.exceptionsSection2Row || (i == NotificationsCustomSettingsActivity.this.groupSection2Row && NotificationsCustomSettingsActivity.this.exceptionsSection2Row == -1)) {
                        viewHolder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, str));
                        return;
                    } else {
                        viewHolder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, str));
                        return;
                    }
                case 5:
                    TextSettingsCell textSettingsCell = (TextSettingsCell) viewHolder.itemView;
                    notificationsSettings = MessagesController.getNotificationsSettings(NotificationsCustomSettingsActivity.this.currentAccount);
                    String str2;
                    if (i == NotificationsCustomSettingsActivity.this.messageSoundRow) {
                        str = "SoundDefault";
                        if (NotificationsCustomSettingsActivity.this.currentType == 1) {
                            string = notificationsSettings.getString("GlobalSound", LocaleController.getString(str, NUM));
                        } else if (NotificationsCustomSettingsActivity.this.currentType == 0) {
                            string = notificationsSettings.getString("GroupSound", LocaleController.getString(str, NUM));
                        } else {
                            string = notificationsSettings.getString("ChannelSound", LocaleController.getString(str, NUM));
                        }
                        String str3 = "NoSound";
                        if (string.equals(str3)) {
                            string = LocaleController.getString(str3, NUM);
                        }
                        textSettingsCell.setTextAndValue(LocaleController.getString("Sound", NUM), string, true);
                        return;
                    } else if (i == NotificationsCustomSettingsActivity.this.messageVibrateRow) {
                        if (NotificationsCustomSettingsActivity.this.currentType == 1) {
                            i = notificationsSettings.getInt("vibrate_messages", 0);
                        } else if (NotificationsCustomSettingsActivity.this.currentType == 0) {
                            i = notificationsSettings.getInt("vibrate_group", 0);
                        } else {
                            i = notificationsSettings.getInt("vibrate_channel", 0);
                        }
                        str2 = "Vibrate";
                        if (i == 0) {
                            textSettingsCell.setTextAndValue(LocaleController.getString(str2, NUM), LocaleController.getString("VibrationDefault", NUM), true);
                            return;
                        } else if (i == 1) {
                            textSettingsCell.setTextAndValue(LocaleController.getString(str2, NUM), LocaleController.getString("Short", NUM), true);
                            return;
                        } else if (i == 2) {
                            textSettingsCell.setTextAndValue(LocaleController.getString(str2, NUM), LocaleController.getString("VibrationDisabled", NUM), true);
                            return;
                        } else if (i == 3) {
                            textSettingsCell.setTextAndValue(LocaleController.getString(str2, NUM), LocaleController.getString("Long", NUM), true);
                            return;
                        } else if (i == 4) {
                            textSettingsCell.setTextAndValue(LocaleController.getString(str2, NUM), LocaleController.getString("OnlyIfSilent", NUM), true);
                            return;
                        } else {
                            return;
                        }
                    } else if (i == NotificationsCustomSettingsActivity.this.messagePriorityRow) {
                        if (NotificationsCustomSettingsActivity.this.currentType == 1) {
                            i = notificationsSettings.getInt("priority_messages", 1);
                        } else if (NotificationsCustomSettingsActivity.this.currentType == 0) {
                            i = notificationsSettings.getInt("priority_group", 1);
                        } else {
                            i = notificationsSettings.getInt("priority_channel", 1);
                        }
                        str2 = "NotificationsImportance";
                        if (i == 0) {
                            textSettingsCell.setTextAndValue(LocaleController.getString(str2, NUM), LocaleController.getString("NotificationsPriorityHigh", NUM), true);
                            return;
                        } else if (i == 1 || i == 2) {
                            textSettingsCell.setTextAndValue(LocaleController.getString(str2, NUM), LocaleController.getString("NotificationsPriorityUrgent", NUM), true);
                            return;
                        } else if (i == 4) {
                            textSettingsCell.setTextAndValue(LocaleController.getString(str2, NUM), LocaleController.getString("NotificationsPriorityLow", NUM), true);
                            return;
                        } else if (i == 5) {
                            textSettingsCell.setTextAndValue(LocaleController.getString(str2, NUM), LocaleController.getString("NotificationsPriorityMedium", NUM), true);
                            return;
                        } else {
                            return;
                        }
                    } else if (i == NotificationsCustomSettingsActivity.this.messagePopupNotificationRow) {
                        if (NotificationsCustomSettingsActivity.this.currentType == 1) {
                            i = notificationsSettings.getInt("popupAll", 0);
                        } else if (NotificationsCustomSettingsActivity.this.currentType == 0) {
                            i = notificationsSettings.getInt("popupGroup", 0);
                        } else {
                            i = notificationsSettings.getInt("popupChannel", 0);
                        }
                        if (i == 0) {
                            string = LocaleController.getString("NoPopup", NUM);
                        } else if (i == 1) {
                            string = LocaleController.getString("OnlyWhenScreenOn", NUM);
                        } else if (i == 2) {
                            string = LocaleController.getString("OnlyWhenScreenOff", NUM);
                        } else {
                            string = LocaleController.getString("AlwaysShowPopup", NUM);
                        }
                        textSettingsCell.setTextAndValue(LocaleController.getString("PopupNotification", NUM), string, true);
                        return;
                    } else {
                        return;
                    }
                case 6:
                    int i3;
                    int i4;
                    NotificationsCheckCell notificationsCheckCell = (NotificationsCheckCell) viewHolder.itemView;
                    notificationsCheckCell.setDrawLine(false);
                    StringBuilder stringBuilder = new StringBuilder();
                    SharedPreferences notificationsSettings3 = MessagesController.getNotificationsSettings(NotificationsCustomSettingsActivity.this.currentAccount);
                    if (NotificationsCustomSettingsActivity.this.currentType == 1) {
                        string = LocaleController.getString("NotificationsForPrivateChats", NUM);
                        i3 = notificationsSettings3.getInt("EnableAll2", 0);
                    } else if (NotificationsCustomSettingsActivity.this.currentType == 0) {
                        string = LocaleController.getString("NotificationsForGroups", NUM);
                        i3 = notificationsSettings3.getInt("EnableGroup2", 0);
                    } else {
                        string = LocaleController.getString("NotificationsForChannels", NUM);
                        i3 = notificationsSettings3.getInt("EnableChannel2", 0);
                    }
                    String str4 = string;
                    i = ConnectionsManager.getInstance(NotificationsCustomSettingsActivity.this.currentAccount).getCurrentTime();
                    boolean z3 = i3 < i;
                    if (z3) {
                        stringBuilder.append(LocaleController.getString("NotificationsOn", NUM));
                    } else if (i3 - 31536000 >= i) {
                        stringBuilder.append(LocaleController.getString("NotificationsOff", NUM));
                    } else {
                        stringBuilder.append(LocaleController.formatString("NotificationsOffUntil", NUM, LocaleController.stringForMessageListDate((long) i3)));
                        i4 = 2;
                        notificationsCheckCell.setTextAndValueAndCheck(str4, stringBuilder, z3, i4, false);
                        return;
                    }
                    i4 = 0;
                    notificationsCheckCell.setTextAndValueAndCheck(str4, stringBuilder, z3, i4, false);
                    return;
                case 7:
                    TextCell textCell = (TextCell) viewHolder.itemView;
                    textCell.setColors("windowBackgroundWhiteBlueIcon", "windowBackgroundWhiteBlueButton");
                    if (i == NotificationsCustomSettingsActivity.this.exceptionsAddRow) {
                        string = LocaleController.getString("NotificationsAddAnException", NUM);
                        if (NotificationsCustomSettingsActivity.this.exceptionsStartRow != -1) {
                            z = true;
                        }
                        textCell.setTextAndIcon(string, NUM, z);
                        return;
                    }
                    return;
                default:
                    return;
            }
        }

        public void onViewAttachedToWindow(ViewHolder viewHolder) {
            if (NotificationsCustomSettingsActivity.this.exceptions.isEmpty()) {
                boolean isGlobalNotificationsEnabled = NotificationsController.getInstance(NotificationsCustomSettingsActivity.this.currentAccount).isGlobalNotificationsEnabled(NotificationsCustomSettingsActivity.this.currentType);
                int itemViewType = viewHolder.getItemViewType();
                if (itemViewType == 0) {
                    HeaderCell headerCell = (HeaderCell) viewHolder.itemView;
                    if (viewHolder.getAdapterPosition() == NotificationsCustomSettingsActivity.this.messageSectionRow) {
                        headerCell.setEnabled(isGlobalNotificationsEnabled, null);
                    } else {
                        headerCell.setEnabled(true, null);
                    }
                } else if (itemViewType == 1) {
                    ((TextCheckCell) viewHolder.itemView).setEnabled(isGlobalNotificationsEnabled, null);
                } else if (itemViewType == 3) {
                    ((TextColorCell) viewHolder.itemView).setEnabled(isGlobalNotificationsEnabled, null);
                } else if (itemViewType == 5) {
                    ((TextSettingsCell) viewHolder.itemView).setEnabled(isGlobalNotificationsEnabled, null);
                }
            }
        }

        public int getItemViewType(int i) {
            if (i == NotificationsCustomSettingsActivity.this.messageSectionRow) {
                return 0;
            }
            if (i == NotificationsCustomSettingsActivity.this.previewRow) {
                return 1;
            }
            if (i >= NotificationsCustomSettingsActivity.this.exceptionsStartRow && i < NotificationsCustomSettingsActivity.this.exceptionsEndRow) {
                return 2;
            }
            if (i == NotificationsCustomSettingsActivity.this.messageLedRow) {
                return 3;
            }
            if (i == NotificationsCustomSettingsActivity.this.groupSection2Row || i == NotificationsCustomSettingsActivity.this.alertSection2Row || i == NotificationsCustomSettingsActivity.this.exceptionsSection2Row) {
                return 4;
            }
            if (i == NotificationsCustomSettingsActivity.this.alertRow) {
                return 6;
            }
            return i == NotificationsCustomSettingsActivity.this.exceptionsAddRow ? 7 : 5;
        }
    }

    private class SearchAdapter extends SelectionAdapter {
        private Context mContext;
        private ArrayList<NotificationException> searchResult = new ArrayList();
        private ArrayList<CharSequence> searchResultNames = new ArrayList();
        private Timer searchTimer;

        public int getItemViewType(int i) {
            return 0;
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            return true;
        }

        public SearchAdapter(Context context) {
            this.mContext = context;
        }

        public void searchDialogs(final String str) {
            try {
                if (this.searchTimer != null) {
                    this.searchTimer.cancel();
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
            if (str == null) {
                this.searchResult.clear();
                this.searchResultNames.clear();
                notifyDataSetChanged();
                return;
            }
            this.searchTimer = new Timer();
            this.searchTimer.schedule(new TimerTask() {
                public void run() {
                    try {
                        SearchAdapter.this.searchTimer.cancel();
                        SearchAdapter.this.searchTimer = null;
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                    SearchAdapter.this.processSearch(str);
                }
            }, 200, 300);
        }

        private void processSearch(String str) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$NotificationsCustomSettingsActivity$SearchAdapter$R_yRN0Y21CFdspI_6cy-hAalLcc(this, str));
        }

        public /* synthetic */ void lambda$processSearch$1$NotificationsCustomSettingsActivity$SearchAdapter(String str) {
            Utilities.searchQueue.postRunnable(new -$$Lambda$NotificationsCustomSettingsActivity$SearchAdapter$1sbGs4NEyQ8S6Oqw0ayErmMeXIk(this, str, new ArrayList(NotificationsCustomSettingsActivity.this.exceptions)));
        }

        /* JADX WARNING: Removed duplicated region for block: B:71:0x01af A:{LOOP_END, LOOP:1: B:47:0x011b->B:71:0x01af} */
        /* JADX WARNING: Removed duplicated region for block: B:78:0x0175 A:{SYNTHETIC} */
        /* JADX WARNING: Removed duplicated region for block: B:78:0x0175 A:{SYNTHETIC} */
        /* JADX WARNING: Removed duplicated region for block: B:71:0x01af A:{LOOP_END, LOOP:1: B:47:0x011b->B:71:0x01af} */
        /* JADX WARNING: Removed duplicated region for block: B:49:0x011e  */
        /* JADX WARNING: Removed duplicated region for block: B:49:0x011e  */
        /* JADX WARNING: Missing block: B:54:0x0143, code skipped:
            if (r3.contains(r5.toString()) == false) goto L_0x0145;
     */
        /* JADX WARNING: Missing block: B:59:0x0160, code skipped:
            if (r13.contains(r3.toString()) != false) goto L_0x0162;
     */
        public /* synthetic */ void lambda$null$0$NotificationsCustomSettingsActivity$SearchAdapter(java.lang.String r18, java.util.ArrayList r19) {
            /*
            r17 = this;
            r0 = r17;
            r1 = r18.trim();
            r1 = r1.toLowerCase();
            r2 = r1.length();
            if (r2 != 0) goto L_0x001e;
        L_0x0010:
            r1 = new java.util.ArrayList;
            r1.<init>();
            r2 = new java.util.ArrayList;
            r2.<init>();
            r0.updateSearchResults(r1, r2);
            return;
        L_0x001e:
            r2 = org.telegram.messenger.LocaleController.getInstance();
            r2 = r2.getTranslitString(r1);
            r3 = r1.equals(r2);
            if (r3 != 0) goto L_0x0032;
        L_0x002c:
            r3 = r2.length();
            if (r3 != 0) goto L_0x0033;
        L_0x0032:
            r2 = 0;
        L_0x0033:
            r3 = 1;
            r5 = 0;
            if (r2 == 0) goto L_0x0039;
        L_0x0037:
            r6 = 1;
            goto L_0x003a;
        L_0x0039:
            r6 = 0;
        L_0x003a:
            r6 = r6 + r3;
            r6 = new java.lang.String[r6];
            r6[r5] = r1;
            if (r2 == 0) goto L_0x0043;
        L_0x0041:
            r6[r3] = r2;
        L_0x0043:
            r1 = new java.util.ArrayList;
            r1.<init>();
            r2 = new java.util.ArrayList;
            r2.<init>();
            r7 = 2;
            r8 = new java.lang.String[r7];
            r9 = 0;
        L_0x0051:
            r10 = r19.size();
            if (r9 >= r10) goto L_0x01bb;
        L_0x0057:
            r10 = r19;
            r11 = r10.get(r9);
            r11 = (org.telegram.ui.NotificationsSettingsActivity.NotificationException) r11;
            r12 = r11.did;
            r14 = (int) r12;
            r15 = 32;
            r12 = r12 >> r15;
            r13 = (int) r12;
            if (r14 == 0) goto L_0x00c0;
        L_0x0068:
            if (r14 <= 0) goto L_0x0092;
        L_0x006a:
            r12 = org.telegram.ui.NotificationsCustomSettingsActivity.this;
            r12 = r12.currentAccount;
            r12 = org.telegram.messenger.MessagesController.getInstance(r12);
            r13 = java.lang.Integer.valueOf(r14);
            r12 = r12.getUser(r13);
            r13 = r12.deleted;
            if (r13 == 0) goto L_0x0081;
        L_0x0080:
            goto L_0x00bd;
        L_0x0081:
            if (r12 == 0) goto L_0x00f8;
        L_0x0083:
            r13 = r12.first_name;
            r14 = r12.last_name;
            r13 = org.telegram.messenger.ContactsController.formatName(r13, r14);
            r8[r5] = r13;
            r12 = r12.username;
            r8[r3] = r12;
            goto L_0x00f8;
        L_0x0092:
            r12 = org.telegram.ui.NotificationsCustomSettingsActivity.this;
            r12 = r12.currentAccount;
            r12 = org.telegram.messenger.MessagesController.getInstance(r12);
            r13 = -r14;
            r13 = java.lang.Integer.valueOf(r13);
            r12 = r12.getChat(r13);
            if (r12 == 0) goto L_0x00f8;
        L_0x00a7:
            r13 = r12.left;
            if (r13 != 0) goto L_0x00bd;
        L_0x00ab:
            r13 = r12.kicked;
            if (r13 != 0) goto L_0x00bd;
        L_0x00af:
            r13 = r12.migrated_to;
            if (r13 == 0) goto L_0x00b4;
        L_0x00b3:
            goto L_0x00bd;
        L_0x00b4:
            r13 = r12.title;
            r8[r5] = r13;
            r12 = r12.username;
            r8[r3] = r12;
            goto L_0x00f8;
        L_0x00bd:
            r7 = 0;
            goto L_0x01b5;
        L_0x00c0:
            r12 = org.telegram.ui.NotificationsCustomSettingsActivity.this;
            r12 = r12.currentAccount;
            r12 = org.telegram.messenger.MessagesController.getInstance(r12);
            r13 = java.lang.Integer.valueOf(r13);
            r12 = r12.getEncryptedChat(r13);
            if (r12 == 0) goto L_0x00f8;
        L_0x00d4:
            r13 = org.telegram.ui.NotificationsCustomSettingsActivity.this;
            r13 = r13.currentAccount;
            r13 = org.telegram.messenger.MessagesController.getInstance(r13);
            r12 = r12.user_id;
            r12 = java.lang.Integer.valueOf(r12);
            r12 = r13.getUser(r12);
            if (r12 == 0) goto L_0x00f8;
        L_0x00ea:
            r13 = r12.first_name;
            r14 = r12.last_name;
            r13 = org.telegram.messenger.ContactsController.formatName(r13, r14);
            r8[r5] = r13;
            r12 = r12.username;
            r8[r3] = r12;
        L_0x00f8:
            r12 = r8[r5];
            r13 = r8[r5];
            r13 = r13.toLowerCase();
            r8[r5] = r13;
            r13 = org.telegram.messenger.LocaleController.getInstance();
            r14 = r8[r5];
            r13 = r13.getTranslitString(r14);
            r14 = r8[r5];
            if (r14 == 0) goto L_0x0119;
        L_0x0110:
            r14 = r8[r5];
            r14 = r14.equals(r13);
            if (r14 == 0) goto L_0x0119;
        L_0x0118:
            r13 = 0;
        L_0x0119:
            r14 = 0;
            r15 = 0;
        L_0x011b:
            r7 = r6.length;
            if (r14 >= r7) goto L_0x00bd;
        L_0x011e:
            r7 = r6[r14];
            r16 = r8[r5];
            r4 = " ";
            if (r16 == 0) goto L_0x0145;
        L_0x0126:
            r3 = r8[r5];
            r3 = r3.startsWith(r7);
            if (r3 != 0) goto L_0x0162;
        L_0x012e:
            r3 = r8[r5];
            r5 = new java.lang.StringBuilder;
            r5.<init>();
            r5.append(r4);
            r5.append(r7);
            r5 = r5.toString();
            r3 = r3.contains(r5);
            if (r3 != 0) goto L_0x0162;
        L_0x0145:
            if (r13 == 0) goto L_0x0165;
        L_0x0147:
            r3 = r13.startsWith(r7);
            if (r3 != 0) goto L_0x0162;
        L_0x014d:
            r3 = new java.lang.StringBuilder;
            r3.<init>();
            r3.append(r4);
            r3.append(r7);
            r3 = r3.toString();
            r3 = r13.contains(r3);
            if (r3 == 0) goto L_0x0165;
        L_0x0162:
            r3 = 1;
            r15 = 1;
            goto L_0x0173;
        L_0x0165:
            r3 = 1;
            r4 = r8[r3];
            if (r4 == 0) goto L_0x0173;
        L_0x016a:
            r4 = r8[r3];
            r4 = r4.startsWith(r7);
            if (r4 == 0) goto L_0x0173;
        L_0x0172:
            r15 = 2;
        L_0x0173:
            if (r15 == 0) goto L_0x01af;
        L_0x0175:
            if (r15 != r3) goto L_0x0181;
        L_0x0177:
            r4 = 0;
            r5 = org.telegram.messenger.AndroidUtilities.generateSearchName(r12, r4, r7);
            r2.add(r5);
            r7 = 0;
            goto L_0x01ab;
        L_0x0181:
            r4 = new java.lang.StringBuilder;
            r4.<init>();
            r5 = "@";
            r4.append(r5);
            r12 = r8[r3];
            r4.append(r12);
            r4 = r4.toString();
            r12 = new java.lang.StringBuilder;
            r12.<init>();
            r12.append(r5);
            r12.append(r7);
            r5 = r12.toString();
            r7 = 0;
            r4 = org.telegram.messenger.AndroidUtilities.generateSearchName(r4, r7, r5);
            r2.add(r4);
        L_0x01ab:
            r1.add(r11);
            goto L_0x01b5;
        L_0x01af:
            r7 = 0;
            r14 = r14 + 1;
            r5 = 0;
            goto L_0x011b;
        L_0x01b5:
            r9 = r9 + 1;
            r5 = 0;
            r7 = 2;
            goto L_0x0051;
        L_0x01bb:
            r0.updateSearchResults(r1, r2);
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.NotificationsCustomSettingsActivity$SearchAdapter.lambda$null$0$NotificationsCustomSettingsActivity$SearchAdapter(java.lang.String, java.util.ArrayList):void");
        }

        private void updateSearchResults(ArrayList<NotificationException> arrayList, ArrayList<CharSequence> arrayList2) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$NotificationsCustomSettingsActivity$SearchAdapter$f9v_aVxREJMj58yLknoa0t4SGPc(this, arrayList, arrayList2));
        }

        public /* synthetic */ void lambda$updateSearchResults$2$NotificationsCustomSettingsActivity$SearchAdapter(ArrayList arrayList, ArrayList arrayList2) {
            this.searchResult = arrayList;
            this.searchResultNames = arrayList2;
            notifyDataSetChanged();
        }

        public int getItemCount() {
            return this.searchResult.size();
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            UserCell userCell = new UserCell(this.mContext, 9, 0, false);
            userCell.setPadding(AndroidUtilities.dp(6.0f), 0, AndroidUtilities.dp(6.0f), 0);
            userCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            return new Holder(userCell);
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            UserCell userCell = (UserCell) viewHolder.itemView;
            NotificationException notificationException = (NotificationException) this.searchResult.get(i);
            CharSequence charSequence = (CharSequence) this.searchResultNames.get(i);
            boolean z = true;
            if (i == this.searchResult.size() - 1) {
                z = false;
            }
            userCell.setException(notificationException, charSequence, z);
        }
    }

    public NotificationsCustomSettingsActivity(int i, ArrayList<NotificationException> arrayList) {
        this(i, arrayList, false);
    }

    public NotificationsCustomSettingsActivity(int i, ArrayList<NotificationException> arrayList, boolean z) {
        this.rowCount = 0;
        this.currentType = i;
        this.exceptions = arrayList;
        if (z) {
            loadExceptions();
        }
    }

    public boolean onFragmentCreate() {
        updateRows();
        return super.onFragmentCreate();
    }

    public View createView(Context context) {
        this.searching = false;
        this.searchWas = false;
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(true);
        if (this.currentType == -1) {
            this.actionBar.setTitle(LocaleController.getString("NotificationsExceptions", NUM));
        } else {
            this.actionBar.setTitle(LocaleController.getString("Notifications", NUM));
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    NotificationsCustomSettingsActivity.this.finishFragment();
                }
            }
        });
        ArrayList arrayList = this.exceptions;
        if (!(arrayList == null || arrayList.isEmpty())) {
            this.actionBar.createMenu().addItem(0, NUM).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItemSearchListener() {
                public void onSearchExpand() {
                    NotificationsCustomSettingsActivity.this.searching = true;
                    NotificationsCustomSettingsActivity.this.emptyView.setShowAtCenter(true);
                }

                public void onSearchCollapse() {
                    NotificationsCustomSettingsActivity.this.searchListViewAdapter.searchDialogs(null);
                    NotificationsCustomSettingsActivity.this.searching = false;
                    NotificationsCustomSettingsActivity.this.searchWas = false;
                    NotificationsCustomSettingsActivity.this.emptyView.setText(LocaleController.getString("NoExceptions", NUM));
                    NotificationsCustomSettingsActivity.this.listView.setAdapter(NotificationsCustomSettingsActivity.this.adapter);
                    NotificationsCustomSettingsActivity.this.adapter.notifyDataSetChanged();
                    NotificationsCustomSettingsActivity.this.listView.setFastScrollVisible(true);
                    NotificationsCustomSettingsActivity.this.listView.setVerticalScrollBarEnabled(false);
                    NotificationsCustomSettingsActivity.this.emptyView.setShowAtCenter(false);
                }

                public void onTextChanged(EditText editText) {
                    if (NotificationsCustomSettingsActivity.this.searchListViewAdapter != null) {
                        String obj = editText.getText().toString();
                        if (obj.length() != 0) {
                            NotificationsCustomSettingsActivity.this.searchWas = true;
                            if (NotificationsCustomSettingsActivity.this.listView != null) {
                                NotificationsCustomSettingsActivity.this.emptyView.setText(LocaleController.getString("NoResult", NUM));
                                NotificationsCustomSettingsActivity.this.listView.setAdapter(NotificationsCustomSettingsActivity.this.searchListViewAdapter);
                                NotificationsCustomSettingsActivity.this.searchListViewAdapter.notifyDataSetChanged();
                                NotificationsCustomSettingsActivity.this.listView.setFastScrollVisible(false);
                                NotificationsCustomSettingsActivity.this.listView.setVerticalScrollBarEnabled(true);
                            }
                        }
                        NotificationsCustomSettingsActivity.this.searchListViewAdapter.searchDialogs(obj);
                    }
                }
            }).setSearchFieldHint(LocaleController.getString("Search", NUM));
        }
        this.searchListViewAdapter = new SearchAdapter(context);
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        this.emptyView = new EmptyTextProgressView(context);
        this.emptyView.setTextSize(18);
        this.emptyView.setText(LocaleController.getString("NoExceptions", NUM));
        this.emptyView.showTextView();
        frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView = new RecyclerListView(context);
        this.listView.setEmptyView(this.emptyView);
        this.listView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        this.listView.setVerticalScrollBarEnabled(false);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        RecyclerListView recyclerListView = this.listView;
        ListAdapter listAdapter = new ListAdapter(context);
        this.adapter = listAdapter;
        recyclerListView.setAdapter(listAdapter);
        this.listView.setOnItemClickListener(new -$$Lambda$NotificationsCustomSettingsActivity$6X-KPwufVk5Y33wBeKoVyvVbszw(this));
        this.listView.setOnScrollListener(new OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                if (i == 1 && NotificationsCustomSettingsActivity.this.searching && NotificationsCustomSettingsActivity.this.searchWas) {
                    AndroidUtilities.hideKeyboard(NotificationsCustomSettingsActivity.this.getParentActivity().getCurrentFocus());
                }
            }

            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                super.onScrolled(recyclerView, i, i2);
            }
        });
        return this.fragmentView;
    }

    /* JADX WARNING: Removed duplicated region for block: B:101:0x01f4  */
    public /* synthetic */ void lambda$createView$8$NotificationsCustomSettingsActivity(android.view.View r11, int r12, float r13, float r14) {
        /*
        r10 = this;
        r13 = r10.getParentActivity();
        if (r13 != 0) goto L_0x0007;
    L_0x0006:
        return;
    L_0x0007:
        r13 = r10.listView;
        r13 = r13.getAdapter();
        r14 = r10.searchListViewAdapter;
        if (r13 == r14) goto L_0x01fd;
    L_0x0011:
        r13 = r10.exceptionsStartRow;
        if (r12 < r13) goto L_0x001b;
    L_0x0015:
        r13 = r10.exceptionsEndRow;
        if (r12 >= r13) goto L_0x001b;
    L_0x0019:
        goto L_0x01fd;
    L_0x001b:
        r13 = r10.exceptionsAddRow;
        r14 = 2;
        r0 = 0;
        r1 = 1;
        if (r12 != r13) goto L_0x0059;
    L_0x0022:
        r12 = new android.os.Bundle;
        r12.<init>();
        r13 = "onlySelect";
        r12.putBoolean(r13, r1);
        r13 = "checkCanWrite";
        r12.putBoolean(r13, r0);
        r13 = r10.currentType;
        r2 = "dialogsType";
        if (r13 != 0) goto L_0x003c;
    L_0x0037:
        r13 = 6;
        r12.putInt(r2, r13);
        goto L_0x0047;
    L_0x003c:
        if (r13 != r14) goto L_0x0043;
    L_0x003e:
        r13 = 5;
        r12.putInt(r2, r13);
        goto L_0x0047;
    L_0x0043:
        r13 = 4;
        r12.putInt(r2, r13);
    L_0x0047:
        r13 = new org.telegram.ui.DialogsActivity;
        r13.<init>(r12);
        r12 = new org.telegram.ui.-$$Lambda$NotificationsCustomSettingsActivity$b2R0H8L52T9zKW4WbPYtRj3Tixo;
        r12.<init>(r10);
        r13.setDelegate(r12);
        r10.presentFragment(r13);
        goto L_0x01ef;
    L_0x0059:
        r13 = r10.alertRow;
        if (r12 != r13) goto L_0x00a3;
    L_0x005d:
        r13 = r10.currentAccount;
        r13 = org.telegram.messenger.NotificationsController.getInstance(r13);
        r14 = r10.currentType;
        r13 = r13.isGlobalNotificationsEnabled(r14);
        r14 = r11;
        r14 = (org.telegram.ui.Cells.NotificationsCheckCell) r14;
        r2 = r10.listView;
        r2 = r2.findViewHolderForAdapterPosition(r12);
        if (r13 != 0) goto L_0x008f;
    L_0x0074:
        r3 = r10.currentAccount;
        r3 = org.telegram.messenger.NotificationsController.getInstance(r3);
        r4 = r10.currentType;
        r3.setGlobalNotificationsEnabled(r4, r0);
        r3 = r13 ^ 1;
        r14.setChecked(r3);
        if (r2 == 0) goto L_0x008b;
    L_0x0086:
        r14 = r10.adapter;
        r14.onBindViewHolder(r2, r12);
    L_0x008b:
        r10.checkRowsEnabled();
        goto L_0x00a0;
    L_0x008f:
        r4 = 0;
        r6 = r10.currentType;
        r7 = r10.exceptions;
        r8 = r10.currentAccount;
        r9 = new org.telegram.ui.-$$Lambda$NotificationsCustomSettingsActivity$ZmFDQGaW1AFFelUamuZaIwn-BvM;
        r9.<init>(r10, r14, r2, r12);
        r3 = r10;
        org.telegram.ui.Components.AlertsCreator.showCustomNotificationsDialog(r3, r4, r6, r7, r8, r9);
    L_0x00a0:
        r12 = r13;
        goto L_0x01f0;
    L_0x00a3:
        r13 = r10.previewRow;
        if (r12 != r13) goto L_0x00f1;
    L_0x00a7:
        r12 = r11.isEnabled();
        if (r12 != 0) goto L_0x00ae;
    L_0x00ad:
        return;
    L_0x00ae:
        r12 = r10.currentAccount;
        r12 = org.telegram.messenger.MessagesController.getNotificationsSettings(r12);
        r13 = r12.edit();
        r14 = r10.currentType;
        if (r14 != r1) goto L_0x00c8;
    L_0x00bc:
        r14 = "EnablePreviewAll";
        r12 = r12.getBoolean(r14, r1);
        r2 = r12 ^ 1;
        r13.putBoolean(r14, r2);
        goto L_0x00e1;
    L_0x00c8:
        if (r14 != 0) goto L_0x00d6;
    L_0x00ca:
        r14 = "EnablePreviewGroup";
        r12 = r12.getBoolean(r14, r1);
        r2 = r12 ^ 1;
        r13.putBoolean(r14, r2);
        goto L_0x00e1;
    L_0x00d6:
        r14 = "EnablePreviewChannel";
        r12 = r12.getBoolean(r14, r1);
        r2 = r12 ^ 1;
        r13.putBoolean(r14, r2);
    L_0x00e1:
        r13.commit();
        r13 = r10.currentAccount;
        r13 = org.telegram.messenger.NotificationsController.getInstance(r13);
        r14 = r10.currentType;
        r13.updateServerNotificationsSettings(r14);
        goto L_0x01f0;
    L_0x00f1:
        r13 = r10.messageSoundRow;
        if (r12 != r13) goto L_0x016a;
    L_0x00f5:
        r13 = r11.isEnabled();
        if (r13 != 0) goto L_0x00fc;
    L_0x00fb:
        return;
    L_0x00fc:
        r13 = r10.currentAccount;	 Catch:{ Exception -> 0x0164 }
        r13 = org.telegram.messenger.MessagesController.getNotificationsSettings(r13);	 Catch:{ Exception -> 0x0164 }
        r2 = new android.content.Intent;	 Catch:{ Exception -> 0x0164 }
        r3 = "android.intent.action.RINGTONE_PICKER";
        r2.<init>(r3);	 Catch:{ Exception -> 0x0164 }
        r3 = "android.intent.extra.ringtone.TYPE";
        r2.putExtra(r3, r14);	 Catch:{ Exception -> 0x0164 }
        r3 = "android.intent.extra.ringtone.SHOW_DEFAULT";
        r2.putExtra(r3, r1);	 Catch:{ Exception -> 0x0164 }
        r3 = "android.intent.extra.ringtone.DEFAULT_URI";
        r14 = android.media.RingtoneManager.getDefaultUri(r14);	 Catch:{ Exception -> 0x0164 }
        r2.putExtra(r3, r14);	 Catch:{ Exception -> 0x0164 }
        r14 = android.provider.Settings.System.DEFAULT_NOTIFICATION_URI;	 Catch:{ Exception -> 0x0164 }
        r3 = 0;
        if (r14 == 0) goto L_0x0126;
    L_0x0121:
        r4 = r14.getPath();	 Catch:{ Exception -> 0x0164 }
        goto L_0x0127;
    L_0x0126:
        r4 = r3;
    L_0x0127:
        r5 = r10.currentType;	 Catch:{ Exception -> 0x0164 }
        if (r5 != r1) goto L_0x0132;
    L_0x012b:
        r5 = "GlobalSoundPath";
        r13 = r13.getString(r5, r4);	 Catch:{ Exception -> 0x0164 }
        goto L_0x0143;
    L_0x0132:
        r5 = r10.currentType;	 Catch:{ Exception -> 0x0164 }
        if (r5 != 0) goto L_0x013d;
    L_0x0136:
        r5 = "GroupSoundPath";
        r13 = r13.getString(r5, r4);	 Catch:{ Exception -> 0x0164 }
        goto L_0x0143;
    L_0x013d:
        r5 = "ChannelSoundPath";
        r13 = r13.getString(r5, r4);	 Catch:{ Exception -> 0x0164 }
    L_0x0143:
        if (r13 == 0) goto L_0x0159;
    L_0x0145:
        r5 = "NoSound";
        r5 = r13.equals(r5);	 Catch:{ Exception -> 0x0164 }
        if (r5 != 0) goto L_0x0159;
    L_0x014d:
        r3 = r13.equals(r4);	 Catch:{ Exception -> 0x0164 }
        if (r3 == 0) goto L_0x0154;
    L_0x0153:
        goto L_0x015a;
    L_0x0154:
        r14 = android.net.Uri.parse(r13);	 Catch:{ Exception -> 0x0164 }
        goto L_0x015a;
    L_0x0159:
        r14 = r3;
    L_0x015a:
        r13 = "android.intent.extra.ringtone.EXISTING_URI";
        r2.putExtra(r13, r14);	 Catch:{ Exception -> 0x0164 }
        r10.startActivityForResult(r2, r12);	 Catch:{ Exception -> 0x0164 }
        goto L_0x01ef;
    L_0x0164:
        r12 = move-exception;
        org.telegram.messenger.FileLog.e(r12);
        goto L_0x01ef;
    L_0x016a:
        r13 = r10.messageLedRow;
        r2 = 0;
        if (r12 != r13) goto L_0x018a;
    L_0x0170:
        r13 = r11.isEnabled();
        if (r13 != 0) goto L_0x0177;
    L_0x0176:
        return;
    L_0x0177:
        r13 = r10.getParentActivity();
        r14 = r10.currentType;
        r4 = new org.telegram.ui.-$$Lambda$NotificationsCustomSettingsActivity$4rEUWVGkx4-tAGGYZA5DW_owM_s;
        r4.<init>(r10, r12);
        r12 = org.telegram.ui.Components.AlertsCreator.createColorSelectDialog(r13, r2, r14, r4);
        r10.showDialog(r12);
        goto L_0x01ef;
    L_0x018a:
        r13 = r10.messagePopupNotificationRow;
        if (r12 != r13) goto L_0x01a8;
    L_0x018e:
        r13 = r11.isEnabled();
        if (r13 != 0) goto L_0x0195;
    L_0x0194:
        return;
    L_0x0195:
        r13 = r10.getParentActivity();
        r14 = r10.currentType;
        r2 = new org.telegram.ui.-$$Lambda$NotificationsCustomSettingsActivity$2Ybp2U7lQ3YxdvjXmNbMJQtkwOQ;
        r2.<init>(r10, r12);
        r12 = org.telegram.ui.Components.AlertsCreator.createPopupSelectDialog(r13, r14, r2);
        r10.showDialog(r12);
        goto L_0x01ef;
    L_0x01a8:
        r13 = r10.messageVibrateRow;
        if (r12 != r13) goto L_0x01d2;
    L_0x01ac:
        r13 = r11.isEnabled();
        if (r13 != 0) goto L_0x01b3;
    L_0x01b2:
        return;
    L_0x01b3:
        r13 = r10.currentType;
        if (r13 != r1) goto L_0x01ba;
    L_0x01b7:
        r13 = "vibrate_messages";
        goto L_0x01c1;
    L_0x01ba:
        if (r13 != 0) goto L_0x01bf;
    L_0x01bc:
        r13 = "vibrate_group";
        goto L_0x01c1;
    L_0x01bf:
        r13 = "vibrate_channel";
    L_0x01c1:
        r14 = r10.getParentActivity();
        r4 = new org.telegram.ui.-$$Lambda$NotificationsCustomSettingsActivity$5JQeM2VZAZohz71seJkzo9Axyl8;
        r4.<init>(r10, r12);
        r12 = org.telegram.ui.Components.AlertsCreator.createVibrationSelectDialog(r14, r2, r13, r4);
        r10.showDialog(r12);
        goto L_0x01ef;
    L_0x01d2:
        r13 = r10.messagePriorityRow;
        if (r12 != r13) goto L_0x01ef;
    L_0x01d6:
        r13 = r11.isEnabled();
        if (r13 != 0) goto L_0x01dd;
    L_0x01dc:
        return;
    L_0x01dd:
        r13 = r10.getParentActivity();
        r14 = r10.currentType;
        r4 = new org.telegram.ui.-$$Lambda$NotificationsCustomSettingsActivity$tal7L4g8KTgKJ9Uilx8RMZ5-g8Y;
        r4.<init>(r10, r12);
        r12 = org.telegram.ui.Components.AlertsCreator.createPrioritySelectDialog(r13, r2, r14, r4);
        r10.showDialog(r12);
    L_0x01ef:
        r12 = 0;
    L_0x01f0:
        r13 = r11 instanceof org.telegram.ui.Cells.TextCheckCell;
        if (r13 == 0) goto L_0x01fc;
    L_0x01f4:
        r11 = (org.telegram.ui.Cells.TextCheckCell) r11;
        if (r12 != 0) goto L_0x01f9;
    L_0x01f8:
        r0 = 1;
    L_0x01f9:
        r11.setChecked(r0);
    L_0x01fc:
        return;
    L_0x01fd:
        r11 = r10.listView;
        r11 = r11.getAdapter();
        r13 = r10.searchListViewAdapter;
        if (r11 != r13) goto L_0x020d;
    L_0x0207:
        r11 = r13.searchResult;
        r13 = r12;
        goto L_0x0213;
    L_0x020d:
        r11 = r10.exceptions;
        r13 = r10.exceptionsStartRow;
        r13 = r12 - r13;
    L_0x0213:
        if (r13 < 0) goto L_0x0232;
    L_0x0215:
        r14 = r11.size();
        if (r13 < r14) goto L_0x021c;
    L_0x021b:
        goto L_0x0232;
    L_0x021c:
        r13 = r11.get(r13);
        r13 = (org.telegram.ui.NotificationsSettingsActivity.NotificationException) r13;
        r1 = r13.did;
        r3 = -1;
        r4 = 0;
        r5 = r10.currentAccount;
        r6 = 0;
        r7 = new org.telegram.ui.-$$Lambda$NotificationsCustomSettingsActivity$dRvEr5S0zYnz2y_9i0nz2iSedtM;
        r7.<init>(r10, r11, r13, r12);
        r0 = r10;
        org.telegram.ui.Components.AlertsCreator.showCustomNotificationsDialog(r0, r1, r3, r4, r5, r6, r7);
    L_0x0232:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.NotificationsCustomSettingsActivity.lambda$createView$8$NotificationsCustomSettingsActivity(android.view.View, int, float, float):void");
    }

    public /* synthetic */ void lambda$null$0$NotificationsCustomSettingsActivity(ArrayList arrayList, NotificationException notificationException, int i, int i2) {
        if (i2 == 0) {
            ArrayList arrayList2 = this.exceptions;
            if (arrayList != arrayList2) {
                i2 = arrayList2.indexOf(notificationException);
                if (i2 >= 0) {
                    this.exceptions.remove(i2);
                }
            }
            arrayList.remove(notificationException);
            if (this.exceptionsAddRow != -1 && arrayList.isEmpty() && arrayList == this.exceptions) {
                this.listView.getAdapter().notifyItemChanged(this.exceptionsAddRow);
            }
            this.listView.getAdapter().notifyItemRemoved(i);
            updateRows();
            checkRowsEnabled();
            return;
        }
        SharedPreferences notificationsSettings = MessagesController.getNotificationsSettings(this.currentAccount);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("custom_");
        stringBuilder.append(notificationException.did);
        notificationException.hasCustom = notificationsSettings.getBoolean(stringBuilder.toString(), false);
        stringBuilder = new StringBuilder();
        stringBuilder.append("notify2_");
        stringBuilder.append(notificationException.did);
        notificationException.notify = notificationsSettings.getInt(stringBuilder.toString(), 0);
        if (notificationException.notify != 0) {
            stringBuilder = new StringBuilder();
            stringBuilder.append("notifyuntil_");
            stringBuilder.append(notificationException.did);
            int i3 = notificationsSettings.getInt(stringBuilder.toString(), -1);
            if (i3 != -1) {
                notificationException.muteUntil = i3;
            }
        }
        this.listView.getAdapter().notifyItemChanged(i);
    }

    public /* synthetic */ void lambda$null$2$NotificationsCustomSettingsActivity(DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
        Bundle bundle = new Bundle();
        bundle.putLong("dialog_id", ((Long) arrayList.get(0)).longValue());
        bundle.putBoolean("exception", true);
        ProfileNotificationsActivity profileNotificationsActivity = new ProfileNotificationsActivity(bundle);
        profileNotificationsActivity.setDelegate(new -$$Lambda$NotificationsCustomSettingsActivity$aAVZUf3lzgt6V9lFSmZZFnxR_Ws(this));
        presentFragment(profileNotificationsActivity, true);
    }

    public /* synthetic */ void lambda$null$1$NotificationsCustomSettingsActivity(NotificationException notificationException) {
        this.exceptions.add(0, notificationException);
        updateRows();
        this.adapter.notifyDataSetChanged();
    }

    public /* synthetic */ void lambda$null$3$NotificationsCustomSettingsActivity(NotificationsCheckCell notificationsCheckCell, ViewHolder viewHolder, int i, int i2) {
        SharedPreferences notificationsSettings = MessagesController.getNotificationsSettings(this.currentAccount);
        int i3 = this.currentType;
        int i4 = 0;
        if (i3 == 1) {
            i2 = notificationsSettings.getInt("EnableAll2", 0);
        } else if (i3 == 0) {
            i2 = notificationsSettings.getInt("EnableGroup2", 0);
        } else {
            i2 = notificationsSettings.getInt("EnableChannel2", 0);
        }
        i3 = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
        if (i2 >= i3 && i2 - 31536000 < i3) {
            i4 = 2;
        }
        notificationsCheckCell.setChecked(NotificationsController.getInstance(this.currentAccount).isGlobalNotificationsEnabled(this.currentType), i4);
        if (viewHolder != null) {
            this.adapter.onBindViewHolder(viewHolder, i);
        }
        checkRowsEnabled();
    }

    public /* synthetic */ void lambda$null$4$NotificationsCustomSettingsActivity(int i) {
        ViewHolder findViewHolderForAdapterPosition = this.listView.findViewHolderForAdapterPosition(i);
        if (findViewHolderForAdapterPosition != null) {
            this.adapter.onBindViewHolder(findViewHolderForAdapterPosition, i);
        }
    }

    public /* synthetic */ void lambda$null$5$NotificationsCustomSettingsActivity(int i) {
        ViewHolder findViewHolderForAdapterPosition = this.listView.findViewHolderForAdapterPosition(i);
        if (findViewHolderForAdapterPosition != null) {
            this.adapter.onBindViewHolder(findViewHolderForAdapterPosition, i);
        }
    }

    public /* synthetic */ void lambda$null$6$NotificationsCustomSettingsActivity(int i) {
        ViewHolder findViewHolderForAdapterPosition = this.listView.findViewHolderForAdapterPosition(i);
        if (findViewHolderForAdapterPosition != null) {
            this.adapter.onBindViewHolder(findViewHolderForAdapterPosition, i);
        }
    }

    public /* synthetic */ void lambda$null$7$NotificationsCustomSettingsActivity(int i) {
        ViewHolder findViewHolderForAdapterPosition = this.listView.findViewHolderForAdapterPosition(i);
        if (findViewHolderForAdapterPosition != null) {
            this.adapter.onBindViewHolder(findViewHolderForAdapterPosition, i);
        }
    }

    private void checkRowsEnabled() {
        if (this.exceptions.isEmpty()) {
            int childCount = this.listView.getChildCount();
            ArrayList arrayList = new ArrayList();
            boolean isGlobalNotificationsEnabled = NotificationsController.getInstance(this.currentAccount).isGlobalNotificationsEnabled(this.currentType);
            for (int i = 0; i < childCount; i++) {
                Holder holder = (Holder) this.listView.getChildViewHolder(this.listView.getChildAt(i));
                int itemViewType = holder.getItemViewType();
                if (itemViewType == 0) {
                    HeaderCell headerCell = (HeaderCell) holder.itemView;
                    if (holder.getAdapterPosition() == this.messageSectionRow) {
                        headerCell.setEnabled(isGlobalNotificationsEnabled, arrayList);
                    }
                } else if (itemViewType == 1) {
                    ((TextCheckCell) holder.itemView).setEnabled(isGlobalNotificationsEnabled, arrayList);
                } else if (itemViewType == 3) {
                    ((TextColorCell) holder.itemView).setEnabled(isGlobalNotificationsEnabled, arrayList);
                } else if (itemViewType == 5) {
                    ((TextSettingsCell) holder.itemView).setEnabled(isGlobalNotificationsEnabled, arrayList);
                }
            }
            if (!arrayList.isEmpty()) {
                AnimatorSet animatorSet = this.animatorSet;
                if (animatorSet != null) {
                    animatorSet.cancel();
                }
                this.animatorSet = new AnimatorSet();
                this.animatorSet.playTogether(arrayList);
                this.animatorSet.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        if (animator.equals(NotificationsCustomSettingsActivity.this.animatorSet)) {
                            NotificationsCustomSettingsActivity.this.animatorSet = null;
                        }
                    }
                });
                this.animatorSet.setDuration(150);
                this.animatorSet.start();
            }
        }
    }

    private void loadExceptions() {
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new -$$Lambda$NotificationsCustomSettingsActivity$1DHOVp0GIYy95W4ah22F-PhCl-A(this));
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
    public /* synthetic */ void lambda$loadExceptions$10$NotificationsCustomSettingsActivity() {
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
        r0 = new org.telegram.ui.-$$Lambda$NotificationsCustomSettingsActivity$KpcytPX_h4eA3kA--wtdyBIK9-w;
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
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.NotificationsCustomSettingsActivity.lambda$loadExceptions$10$NotificationsCustomSettingsActivity():void");
    }

    public /* synthetic */ void lambda$null$9$NotificationsCustomSettingsActivity(ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3, ArrayList arrayList4, ArrayList arrayList5, ArrayList arrayList6) {
        MessagesController.getInstance(this.currentAccount).putUsers(arrayList, true);
        MessagesController.getInstance(this.currentAccount).putChats(arrayList2, true);
        MessagesController.getInstance(this.currentAccount).putEncryptedChats(arrayList3, true);
        int i = this.currentType;
        if (i == 1) {
            this.exceptions = arrayList4;
        } else if (i == 0) {
            this.exceptions = arrayList5;
        } else {
            this.exceptions = arrayList6;
        }
        updateRows();
        this.adapter.notifyDataSetChanged();
    }

    private void updateRows() {
        this.rowCount = 0;
        int i = this.currentType;
        if (i != -1) {
            int i2 = this.rowCount;
            this.rowCount = i2 + 1;
            this.alertRow = i2;
            i2 = this.rowCount;
            this.rowCount = i2 + 1;
            this.alertSection2Row = i2;
            i2 = this.rowCount;
            this.rowCount = i2 + 1;
            this.messageSectionRow = i2;
            i2 = this.rowCount;
            this.rowCount = i2 + 1;
            this.previewRow = i2;
            i2 = this.rowCount;
            this.rowCount = i2 + 1;
            this.messageLedRow = i2;
            i2 = this.rowCount;
            this.rowCount = i2 + 1;
            this.messageVibrateRow = i2;
            if (i == 2) {
                this.messagePopupNotificationRow = -1;
            } else {
                i = this.rowCount;
                this.rowCount = i + 1;
                this.messagePopupNotificationRow = i;
            }
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
            this.groupSection2Row = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.exceptionsAddRow = i;
        } else {
            this.alertRow = -1;
            this.alertSection2Row = -1;
            this.messageSectionRow = -1;
            this.previewRow = -1;
            this.messageLedRow = -1;
            this.messageVibrateRow = -1;
            this.messagePopupNotificationRow = -1;
            this.messageSoundRow = -1;
            this.messagePriorityRow = -1;
            this.groupSection2Row = -1;
            this.exceptionsAddRow = -1;
        }
        ArrayList arrayList = this.exceptions;
        if (arrayList == null || arrayList.isEmpty()) {
            this.exceptionsStartRow = -1;
            this.exceptionsEndRow = -1;
        } else {
            i = this.rowCount;
            this.exceptionsStartRow = i;
            this.rowCount = i + this.exceptions.size();
            this.exceptionsEndRow = this.rowCount;
        }
        if (this.currentType == -1) {
            arrayList = this.exceptions;
            if (arrayList == null || arrayList.isEmpty()) {
                this.exceptionsSection2Row = -1;
                return;
            }
        }
        i = this.rowCount;
        this.rowCount = i + 1;
        this.exceptionsSection2Row = i;
    }

    public void onActivityResultFragment(int i, int i2, Intent intent) {
        if (i2 == -1) {
            Uri uri = (Uri) intent.getParcelableExtra("android.intent.extra.ringtone.PICKED_URI");
            String str = null;
            if (uri != null) {
                Ringtone ringtone = RingtoneManager.getRingtone(getParentActivity(), uri);
                if (ringtone != null) {
                    if (uri.equals(System.DEFAULT_NOTIFICATION_URI)) {
                        str = LocaleController.getString("SoundDefault", NUM);
                    } else {
                        str = ringtone.getTitle(getParentActivity());
                    }
                    ringtone.stop();
                }
            }
            Editor edit = MessagesController.getNotificationsSettings(this.currentAccount).edit();
            int i3 = this.currentType;
            String str2 = "NoSound";
            String str3;
            String str4;
            if (i3 == 1) {
                str3 = "GlobalSoundPath";
                str4 = "GlobalSound";
                if (str == null || uri == null) {
                    edit.putString(str4, str2);
                    edit.putString(str3, str2);
                } else {
                    edit.putString(str4, str);
                    edit.putString(str3, uri.toString());
                }
            } else if (i3 == 0) {
                str3 = "GroupSoundPath";
                str4 = "GroupSound";
                if (str == null || uri == null) {
                    edit.putString(str4, str2);
                    edit.putString(str3, str2);
                } else {
                    edit.putString(str4, str);
                    edit.putString(str3, uri.toString());
                }
            } else if (i3 == 2) {
                str3 = "ChannelSoundPath";
                str4 = "ChannelSound";
                if (str == null || uri == null) {
                    edit.putString(str4, str2);
                    edit.putString(str3, str2);
                } else {
                    edit.putString(str4, str);
                    edit.putString(str3, uri.toString());
                }
            }
            edit.commit();
            NotificationsController.getInstance(this.currentAccount).updateServerNotificationsSettings(this.currentType);
            ViewHolder findViewHolderForAdapterPosition = this.listView.findViewHolderForAdapterPosition(i);
            if (findViewHolderForAdapterPosition != null) {
                this.adapter.onBindViewHolder(findViewHolderForAdapterPosition, i);
            }
        }
    }

    public void onResume() {
        super.onResume();
        ListAdapter listAdapter = this.adapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        -$$Lambda$NotificationsCustomSettingsActivity$r6_70T0AtxrwXcm6fzXqKDLMaSc -__lambda_notificationscustomsettingsactivity_r6_70t0atxrwxcm6fzxqkdlmasc = new -$$Lambda$NotificationsCustomSettingsActivity$r6_70T0AtxrwXcm6fzXqKDLMaSc(this);
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[36];
        themeDescriptionArr[0] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{HeaderCell.class, TextCheckCell.class, TextColorCell.class, TextSettingsCell.class, UserCell.class, NotificationsCheckCell.class}, null, null, null, "windowBackgroundWhite");
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
        themeDescriptionArr[10] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        view = this.listView;
        clsArr = new Class[]{TextCheckCell.class};
        strArr = new String[1];
        strArr[0] = "valueTextView";
        themeDescriptionArr[11] = new ThemeDescription(view, 0, clsArr, strArr, null, null, null, "windowBackgroundWhiteGrayText2");
        view = this.listView;
        clsArr = new Class[]{TextCheckCell.class};
        strArr = new String[1];
        strArr[0] = "checkBox";
        themeDescriptionArr[12] = new ThemeDescription(view, 0, clsArr, strArr, null, null, null, "switchTrack");
        themeDescriptionArr[13] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, "switchTrackChecked");
        view = this.listView;
        clsArr = new Class[]{UserCell.class};
        strArr = new String[1];
        strArr[0] = "imageView";
        themeDescriptionArr[14] = new ThemeDescription(view, 0, clsArr, strArr, null, null, null, "windowBackgroundWhiteGrayIcon");
        themeDescriptionArr[15] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"nameTextView"}, null, null, null, "windowBackgroundWhiteBlackText");
        ThemeDescriptionDelegate themeDescriptionDelegate = -__lambda_notificationscustomsettingsactivity_r6_70t0atxrwxcm6fzxqkdlmasc;
        themeDescriptionArr[16] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusColor"}, null, null, themeDescriptionDelegate, "windowBackgroundWhiteGrayText");
        themeDescriptionArr[17] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusOnlineColor"}, null, null, themeDescriptionDelegate, "windowBackgroundWhiteBlueText");
        themeDescriptionArr[18] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, null, new Drawable[]{Theme.avatar_broadcastDrawable, Theme.avatar_savedDrawable}, null, "avatar_text");
        -$$Lambda$NotificationsCustomSettingsActivity$r6_70T0AtxrwXcm6fzXqKDLMaSc -__lambda_notificationscustomsettingsactivity_r6_70t0atxrwxcm6fzxqkdlmasc2 = -__lambda_notificationscustomsettingsactivity_r6_70t0atxrwxcm6fzxqkdlmasc;
        themeDescriptionArr[19] = new ThemeDescription(null, 0, null, null, null, -__lambda_notificationscustomsettingsactivity_r6_70t0atxrwxcm6fzxqkdlmasc2, "avatar_backgroundRed");
        themeDescriptionArr[20] = new ThemeDescription(null, 0, null, null, null, -__lambda_notificationscustomsettingsactivity_r6_70t0atxrwxcm6fzxqkdlmasc2, "avatar_backgroundOrange");
        themeDescriptionArr[21] = new ThemeDescription(null, 0, null, null, null, -__lambda_notificationscustomsettingsactivity_r6_70t0atxrwxcm6fzxqkdlmasc2, "avatar_backgroundViolet");
        themeDescriptionArr[22] = new ThemeDescription(null, 0, null, null, null, -__lambda_notificationscustomsettingsactivity_r6_70t0atxrwxcm6fzxqkdlmasc2, "avatar_backgroundGreen");
        themeDescriptionArr[23] = new ThemeDescription(null, 0, null, null, null, -__lambda_notificationscustomsettingsactivity_r6_70t0atxrwxcm6fzxqkdlmasc2, "avatar_backgroundCyan");
        themeDescriptionArr[24] = new ThemeDescription(null, 0, null, null, null, -__lambda_notificationscustomsettingsactivity_r6_70t0atxrwxcm6fzxqkdlmasc2, "avatar_backgroundBlue");
        themeDescriptionArr[25] = new ThemeDescription(null, 0, null, null, null, -__lambda_notificationscustomsettingsactivity_r6_70t0atxrwxcm6fzxqkdlmasc2, "avatar_backgroundPink");
        themeDescriptionArr[26] = new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        themeDescriptionArr[27] = new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"valueTextView"}, null, null, null, "windowBackgroundWhiteGrayText2");
        themeDescriptionArr[28] = new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, null, null, null, "switchTrack");
        themeDescriptionArr[29] = new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, null, null, null, "switchTrackChecked");
        themeDescriptionArr[30] = new ThemeDescription(this.listView, 0, new Class[]{TextColorCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        themeDescriptionArr[31] = new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        themeDescriptionArr[32] = new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, null, null, null, "windowBackgroundWhiteValueText");
        themeDescriptionArr[33] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, "windowBackgroundGrayShadow");
        view = this.listView;
        View view2 = view;
        themeDescriptionArr[34] = new ThemeDescription(view2, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlueButton");
        view = this.listView;
        view2 = view;
        themeDescriptionArr[35] = new ThemeDescription(view2, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"imageView"}, null, null, null, "windowBackgroundWhiteBlueIcon");
        return themeDescriptionArr;
    }

    public /* synthetic */ void lambda$getThemeDescriptions$11$NotificationsCustomSettingsActivity() {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            int childCount = recyclerListView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = this.listView.getChildAt(i);
                if (childAt instanceof UserCell) {
                    ((UserCell) childAt).update(0);
                }
            }
        }
    }
}
