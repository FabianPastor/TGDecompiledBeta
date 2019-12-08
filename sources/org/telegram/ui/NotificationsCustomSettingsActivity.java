package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.content.Context;
import android.content.DialogInterface;
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
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Dialog;
import org.telegram.tgnet.TLRPC.TL_peerNotifySettings;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate;
import org.telegram.ui.Adapters.SearchAdapterHelper;
import org.telegram.ui.Cells.GraySectionCell;
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
    private int deleteAllRow;
    private int deleteAllSectionRow;
    private EmptyTextProgressView emptyView;
    private ArrayList<NotificationException> exceptions;
    private int exceptionsAddRow;
    private HashMap<Long, NotificationException> exceptionsDict;
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
    private SearchAdapter searchAdapter;
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
            SharedPreferences access$1500;
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
                    access$1500 = NotificationsCustomSettingsActivity.this.getNotificationsSettings();
                    if (i == NotificationsCustomSettingsActivity.this.previewRow) {
                        boolean z2;
                        if (NotificationsCustomSettingsActivity.this.currentType == 1) {
                            z2 = access$1500.getBoolean("EnablePreviewAll", true);
                        } else if (NotificationsCustomSettingsActivity.this.currentType == 0) {
                            z2 = access$1500.getBoolean("EnablePreviewGroup", true);
                        } else {
                            z2 = access$1500.getBoolean("EnablePreviewChannel", true);
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
                    SharedPreferences access$1900 = NotificationsCustomSettingsActivity.this.getNotificationsSettings();
                    if (NotificationsCustomSettingsActivity.this.currentType == 1) {
                        i = access$1900.getInt("MessagesLed", -16776961);
                    } else if (NotificationsCustomSettingsActivity.this.currentType == 0) {
                        i = access$1900.getInt("GroupLed", -16776961);
                    } else {
                        i = access$1900.getInt("ChannelLed", -16776961);
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
                    if (i == NotificationsCustomSettingsActivity.this.deleteAllSectionRow || ((i == NotificationsCustomSettingsActivity.this.groupSection2Row && NotificationsCustomSettingsActivity.this.exceptionsSection2Row == -1) || (i == NotificationsCustomSettingsActivity.this.exceptionsSection2Row && NotificationsCustomSettingsActivity.this.deleteAllRow == -1))) {
                        viewHolder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, str));
                        return;
                    } else {
                        viewHolder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, str));
                        return;
                    }
                case 5:
                    TextSettingsCell textSettingsCell = (TextSettingsCell) viewHolder.itemView;
                    access$1500 = NotificationsCustomSettingsActivity.this.getNotificationsSettings();
                    if (i == NotificationsCustomSettingsActivity.this.messageSoundRow) {
                        String str2 = "SoundDefault";
                        if (NotificationsCustomSettingsActivity.this.currentType == 1) {
                            string = access$1500.getString("GlobalSound", LocaleController.getString(str2, NUM));
                        } else if (NotificationsCustomSettingsActivity.this.currentType == 0) {
                            string = access$1500.getString("GroupSound", LocaleController.getString(str2, NUM));
                        } else {
                            string = access$1500.getString("ChannelSound", LocaleController.getString(str2, NUM));
                        }
                        String str3 = "NoSound";
                        if (string.equals(str3)) {
                            string = LocaleController.getString(str3, NUM);
                        }
                        textSettingsCell.setTextAndValue(LocaleController.getString("Sound", NUM), string, true);
                        return;
                    } else if (i == NotificationsCustomSettingsActivity.this.messageVibrateRow) {
                        if (NotificationsCustomSettingsActivity.this.currentType == 1) {
                            i = access$1500.getInt("vibrate_messages", 0);
                        } else if (NotificationsCustomSettingsActivity.this.currentType == 0) {
                            i = access$1500.getInt("vibrate_group", 0);
                        } else {
                            i = access$1500.getInt("vibrate_channel", 0);
                        }
                        str = "Vibrate";
                        if (i == 0) {
                            textSettingsCell.setTextAndValue(LocaleController.getString(str, NUM), LocaleController.getString("VibrationDefault", NUM), true);
                            return;
                        } else if (i == 1) {
                            textSettingsCell.setTextAndValue(LocaleController.getString(str, NUM), LocaleController.getString("Short", NUM), true);
                            return;
                        } else if (i == 2) {
                            textSettingsCell.setTextAndValue(LocaleController.getString(str, NUM), LocaleController.getString("VibrationDisabled", NUM), true);
                            return;
                        } else if (i == 3) {
                            textSettingsCell.setTextAndValue(LocaleController.getString(str, NUM), LocaleController.getString("Long", NUM), true);
                            return;
                        } else if (i == 4) {
                            textSettingsCell.setTextAndValue(LocaleController.getString(str, NUM), LocaleController.getString("OnlyIfSilent", NUM), true);
                            return;
                        } else {
                            return;
                        }
                    } else if (i == NotificationsCustomSettingsActivity.this.messagePriorityRow) {
                        if (NotificationsCustomSettingsActivity.this.currentType == 1) {
                            i = access$1500.getInt("priority_messages", 1);
                        } else if (NotificationsCustomSettingsActivity.this.currentType == 0) {
                            i = access$1500.getInt("priority_group", 1);
                        } else {
                            i = access$1500.getInt("priority_channel", 1);
                        }
                        str = "NotificationsImportance";
                        if (i == 0) {
                            textSettingsCell.setTextAndValue(LocaleController.getString(str, NUM), LocaleController.getString("NotificationsPriorityHigh", NUM), true);
                            return;
                        } else if (i == 1 || i == 2) {
                            textSettingsCell.setTextAndValue(LocaleController.getString(str, NUM), LocaleController.getString("NotificationsPriorityUrgent", NUM), true);
                            return;
                        } else if (i == 4) {
                            textSettingsCell.setTextAndValue(LocaleController.getString(str, NUM), LocaleController.getString("NotificationsPriorityLow", NUM), true);
                            return;
                        } else if (i == 5) {
                            textSettingsCell.setTextAndValue(LocaleController.getString(str, NUM), LocaleController.getString("NotificationsPriorityMedium", NUM), true);
                            return;
                        } else {
                            return;
                        }
                    } else if (i == NotificationsCustomSettingsActivity.this.messagePopupNotificationRow) {
                        if (NotificationsCustomSettingsActivity.this.currentType == 1) {
                            i = access$1500.getInt("popupAll", 0);
                        } else if (NotificationsCustomSettingsActivity.this.currentType == 0) {
                            i = access$1500.getInt("popupGroup", 0);
                        } else {
                            i = access$1500.getInt("popupChannel", 0);
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
                    SharedPreferences access$2900 = NotificationsCustomSettingsActivity.this.getNotificationsSettings();
                    if (NotificationsCustomSettingsActivity.this.currentType == 1) {
                        string = LocaleController.getString("NotificationsForPrivateChats", NUM);
                        i3 = access$2900.getInt("EnableAll2", 0);
                    } else if (NotificationsCustomSettingsActivity.this.currentType == 0) {
                        string = LocaleController.getString("NotificationsForGroups", NUM);
                        i3 = access$2900.getInt("EnableGroup2", 0);
                    } else {
                        string = LocaleController.getString("NotificationsForChannels", NUM);
                        i3 = access$2900.getInt("EnableChannel2", 0);
                    }
                    String str4 = string;
                    i = NotificationsCustomSettingsActivity.this.getConnectionsManager().getCurrentTime();
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
                    if (i == NotificationsCustomSettingsActivity.this.exceptionsAddRow) {
                        string = LocaleController.getString("NotificationsAddAnException", NUM);
                        if (NotificationsCustomSettingsActivity.this.exceptionsStartRow != -1) {
                            z = true;
                        }
                        textCell.setTextAndIcon(string, NUM, z);
                        textCell.setColors("windowBackgroundWhiteBlueIcon", "windowBackgroundWhiteBlueButton");
                        return;
                    } else if (i == NotificationsCustomSettingsActivity.this.deleteAllRow) {
                        textCell.setText(LocaleController.getString("NotificationsDeleteAllException", NUM), false);
                        textCell.setColors(null, "windowBackgroundWhiteRedText5");
                        return;
                    } else {
                        return;
                    }
                default:
                    return;
            }
        }

        public void onViewAttachedToWindow(ViewHolder viewHolder) {
            if (NotificationsCustomSettingsActivity.this.exceptions != null && NotificationsCustomSettingsActivity.this.exceptions.isEmpty()) {
                boolean isGlobalNotificationsEnabled = NotificationsCustomSettingsActivity.this.getNotificationsController().isGlobalNotificationsEnabled(NotificationsCustomSettingsActivity.this.currentType);
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
            if (i == NotificationsCustomSettingsActivity.this.groupSection2Row || i == NotificationsCustomSettingsActivity.this.alertSection2Row || i == NotificationsCustomSettingsActivity.this.exceptionsSection2Row || i == NotificationsCustomSettingsActivity.this.deleteAllSectionRow) {
                return 4;
            }
            if (i == NotificationsCustomSettingsActivity.this.alertRow) {
                return 6;
            }
            return (i == NotificationsCustomSettingsActivity.this.exceptionsAddRow || i == NotificationsCustomSettingsActivity.this.deleteAllRow) ? 7 : 5;
        }
    }

    private class SearchAdapter extends SelectionAdapter {
        private Context mContext;
        private SearchAdapterHelper searchAdapterHelper;
        private ArrayList<NotificationException> searchResult = new ArrayList();
        private ArrayList<CharSequence> searchResultNames = new ArrayList();
        private Runnable searchRunnable;

        public boolean isEnabled(ViewHolder viewHolder) {
            return true;
        }

        public SearchAdapter(Context context) {
            this.mContext = context;
            this.searchAdapterHelper = new SearchAdapterHelper(true);
            this.searchAdapterHelper.setDelegate(new -$$Lambda$NotificationsCustomSettingsActivity$SearchAdapter$NL6PHs6aDhfzvxPlGBUnXlLRajA(this));
        }

        public /* synthetic */ void lambda$new$0$NotificationsCustomSettingsActivity$SearchAdapter() {
            if (this.searchRunnable == null && !this.searchAdapterHelper.isSearchInProgress()) {
                NotificationsCustomSettingsActivity.this.emptyView.showTextView();
            }
            notifyDataSetChanged();
        }

        public void searchDialogs(String str) {
            if (this.searchRunnable != null) {
                Utilities.searchQueue.cancelRunnable(this.searchRunnable);
                this.searchRunnable = null;
            }
            if (str == null) {
                this.searchResult.clear();
                this.searchResultNames.clear();
                this.searchAdapterHelper.mergeResults(null);
                this.searchAdapterHelper.queryServerSearch(null, true, NotificationsCustomSettingsActivity.this.currentType != 1, true, false, 0, false, 0);
                notifyDataSetChanged();
                return;
            }
            DispatchQueue dispatchQueue = Utilities.searchQueue;
            -$$Lambda$NotificationsCustomSettingsActivity$SearchAdapter$tfF2DKTrRdUn7ry8GPc_cT9UHcw -__lambda_notificationscustomsettingsactivity_searchadapter_tff2dktrrdun7ry8gpc_ct9uhcw = new -$$Lambda$NotificationsCustomSettingsActivity$SearchAdapter$tfF2DKTrRdUn7ry8GPc_cT9UHcw(this, str);
            this.searchRunnable = -__lambda_notificationscustomsettingsactivity_searchadapter_tff2dktrrdun7ry8gpc_ct9uhcw;
            dispatchQueue.postRunnable(-__lambda_notificationscustomsettingsactivity_searchadapter_tff2dktrrdun7ry8gpc_ct9uhcw, 300);
        }

        public /* synthetic */ void lambda$searchDialogs$1$NotificationsCustomSettingsActivity$SearchAdapter(String str) {
            processSearch(str);
        }

        private void processSearch(String str) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$NotificationsCustomSettingsActivity$SearchAdapter$SOpmFh8JpgZMz8hb9y4mwi6TmHA(this, str));
        }

        public /* synthetic */ void lambda$processSearch$3$NotificationsCustomSettingsActivity$SearchAdapter(String str) {
            this.searchAdapterHelper.queryServerSearch(str, true, NotificationsCustomSettingsActivity.this.currentType != 1, true, false, 0, false, 0);
            Utilities.searchQueue.postRunnable(new -$$Lambda$NotificationsCustomSettingsActivity$SearchAdapter$o5HWbYaGlE8-ctf1V7rUoVE87jY(this, str, new ArrayList(NotificationsCustomSettingsActivity.this.exceptions)));
        }

        /* JADX WARNING: Removed duplicated region for block: B:76:0x01bb A:{LOOP_END, LOOP:1: B:48:0x011a->B:76:0x01bb} */
        /* JADX WARNING: Removed duplicated region for block: B:84:0x017c A:{SYNTHETIC} */
        /* JADX WARNING: Removed duplicated region for block: B:84:0x017c A:{SYNTHETIC} */
        /* JADX WARNING: Removed duplicated region for block: B:76:0x01bb A:{LOOP_END, LOOP:1: B:48:0x011a->B:76:0x01bb} */
        /* JADX WARNING: Removed duplicated region for block: B:50:0x011d  */
        /* JADX WARNING: Removed duplicated region for block: B:50:0x011d  */
        /* JADX WARNING: Removed duplicated region for block: B:50:0x011d  */
        /* JADX WARNING: Removed duplicated region for block: B:50:0x011d  */
        /* JADX WARNING: Missing block: B:55:0x0144, code skipped:
            if (r6.contains(r5.toString()) == false) goto L_0x0149;
     */
        /* JADX WARNING: Missing block: B:61:0x0164, code skipped:
            if (r8.contains(r5.toString()) != false) goto L_0x0166;
     */
        public /* synthetic */ void lambda$null$2$NotificationsCustomSettingsActivity$SearchAdapter(java.lang.String r19, java.util.ArrayList r20) {
            /*
            r18 = this;
            r0 = r18;
            r1 = r19.trim();
            r1 = r1.toLowerCase();
            r2 = r1.length();
            if (r2 != 0) goto L_0x0023;
        L_0x0010:
            r1 = new java.util.ArrayList;
            r1.<init>();
            r2 = new java.util.ArrayList;
            r2.<init>();
            r3 = new java.util.ArrayList;
            r3.<init>();
            r0.updateSearchResults(r1, r2, r3);
            return;
        L_0x0023:
            r2 = org.telegram.messenger.LocaleController.getInstance();
            r2 = r2.getTranslitString(r1);
            r3 = r1.equals(r2);
            if (r3 != 0) goto L_0x0037;
        L_0x0031:
            r3 = r2.length();
            if (r3 != 0) goto L_0x0038;
        L_0x0037:
            r2 = 0;
        L_0x0038:
            r3 = 1;
            r5 = 0;
            if (r2 == 0) goto L_0x003e;
        L_0x003c:
            r6 = 1;
            goto L_0x003f;
        L_0x003e:
            r6 = 0;
        L_0x003f:
            r6 = r6 + r3;
            r6 = new java.lang.String[r6];
            r6[r5] = r1;
            if (r2 == 0) goto L_0x0048;
        L_0x0046:
            r6[r3] = r2;
        L_0x0048:
            r1 = new java.util.ArrayList;
            r1.<init>();
            r2 = new java.util.ArrayList;
            r2.<init>();
            r7 = new java.util.ArrayList;
            r7.<init>();
            r8 = 2;
            r9 = new java.lang.String[r8];
            r10 = 0;
        L_0x005b:
            r11 = r20.size();
            if (r10 >= r11) goto L_0x01cd;
        L_0x0061:
            r11 = r20;
            r12 = r11.get(r10);
            r12 = (org.telegram.ui.NotificationsSettingsActivity.NotificationException) r12;
            r13 = r12.did;
            r15 = (int) r13;
            r16 = 32;
            r13 = r13 >> r16;
            r14 = (int) r13;
            if (r15 == 0) goto L_0x00c5;
        L_0x0073:
            if (r15 <= 0) goto L_0x0099;
        L_0x0075:
            r13 = org.telegram.ui.NotificationsCustomSettingsActivity.this;
            r13 = r13.getMessagesController();
            r14 = java.lang.Integer.valueOf(r15);
            r13 = r13.getUser(r14);
            r14 = r13.deleted;
            if (r14 == 0) goto L_0x0088;
        L_0x0087:
            goto L_0x00c0;
        L_0x0088:
            if (r13 == 0) goto L_0x00f5;
        L_0x008a:
            r14 = r13.first_name;
            r15 = r13.last_name;
            r14 = org.telegram.messenger.ContactsController.formatName(r14, r15);
            r9[r5] = r14;
            r14 = r13.username;
            r9[r3] = r14;
            goto L_0x00f6;
        L_0x0099:
            r13 = org.telegram.ui.NotificationsCustomSettingsActivity.this;
            r13 = r13.getMessagesController();
            r14 = -r15;
            r14 = java.lang.Integer.valueOf(r14);
            r13 = r13.getChat(r14);
            if (r13 == 0) goto L_0x00f5;
        L_0x00aa:
            r14 = r13.left;
            if (r14 != 0) goto L_0x00c0;
        L_0x00ae:
            r14 = r13.kicked;
            if (r14 != 0) goto L_0x00c0;
        L_0x00b2:
            r14 = r13.migrated_to;
            if (r14 == 0) goto L_0x00b7;
        L_0x00b6:
            goto L_0x00c0;
        L_0x00b7:
            r14 = r13.title;
            r9[r5] = r14;
            r14 = r13.username;
            r9[r3] = r14;
            goto L_0x00f6;
        L_0x00c0:
            r17 = r6;
            r6 = 0;
            goto L_0x01c5;
        L_0x00c5:
            r13 = org.telegram.ui.NotificationsCustomSettingsActivity.this;
            r13 = r13.getMessagesController();
            r14 = java.lang.Integer.valueOf(r14);
            r13 = r13.getEncryptedChat(r14);
            if (r13 == 0) goto L_0x00f5;
        L_0x00d5:
            r14 = org.telegram.ui.NotificationsCustomSettingsActivity.this;
            r14 = r14.getMessagesController();
            r13 = r13.user_id;
            r13 = java.lang.Integer.valueOf(r13);
            r13 = r14.getUser(r13);
            if (r13 == 0) goto L_0x00f5;
        L_0x00e7:
            r14 = r13.first_name;
            r15 = r13.last_name;
            r14 = org.telegram.messenger.ContactsController.formatName(r14, r15);
            r9[r5] = r14;
            r13 = r13.username;
            r9[r3] = r13;
        L_0x00f5:
            r13 = 0;
        L_0x00f6:
            r14 = r9[r5];
            r15 = r9[r5];
            r15 = r15.toLowerCase();
            r9[r5] = r15;
            r15 = org.telegram.messenger.LocaleController.getInstance();
            r8 = r9[r5];
            r8 = r15.getTranslitString(r8);
            r15 = r9[r5];
            if (r15 == 0) goto L_0x0117;
        L_0x010e:
            r15 = r9[r5];
            r15 = r15.equals(r8);
            if (r15 == 0) goto L_0x0117;
        L_0x0116:
            r8 = 0;
        L_0x0117:
            r15 = 0;
            r16 = 0;
        L_0x011a:
            r4 = r6.length;
            if (r15 >= r4) goto L_0x00c0;
        L_0x011d:
            r4 = r6[r15];
            r17 = r9[r5];
            r3 = " ";
            if (r17 == 0) goto L_0x0147;
        L_0x0125:
            r17 = r6;
            r6 = r9[r5];
            r6 = r6.startsWith(r4);
            if (r6 != 0) goto L_0x0166;
        L_0x012f:
            r6 = r9[r5];
            r5 = new java.lang.StringBuilder;
            r5.<init>();
            r5.append(r3);
            r5.append(r4);
            r5 = r5.toString();
            r5 = r6.contains(r5);
            if (r5 != 0) goto L_0x0166;
        L_0x0146:
            goto L_0x0149;
        L_0x0147:
            r17 = r6;
        L_0x0149:
            if (r8 == 0) goto L_0x0169;
        L_0x014b:
            r5 = r8.startsWith(r4);
            if (r5 != 0) goto L_0x0166;
        L_0x0151:
            r5 = new java.lang.StringBuilder;
            r5.<init>();
            r5.append(r3);
            r5.append(r4);
            r3 = r5.toString();
            r3 = r8.contains(r3);
            if (r3 == 0) goto L_0x0169;
        L_0x0166:
            r3 = 1;
            r5 = 1;
            goto L_0x017a;
        L_0x0169:
            r3 = 1;
            r5 = r9[r3];
            if (r5 == 0) goto L_0x0178;
        L_0x016e:
            r5 = r9[r3];
            r5 = r5.startsWith(r4);
            if (r5 == 0) goto L_0x0178;
        L_0x0176:
            r5 = 2;
            goto L_0x017a;
        L_0x0178:
            r5 = r16;
        L_0x017a:
            if (r5 == 0) goto L_0x01bb;
        L_0x017c:
            if (r5 != r3) goto L_0x0188;
        L_0x017e:
            r5 = 0;
            r4 = org.telegram.messenger.AndroidUtilities.generateSearchName(r14, r5, r4);
            r7.add(r4);
            r6 = 0;
            goto L_0x01b2;
        L_0x0188:
            r5 = new java.lang.StringBuilder;
            r5.<init>();
            r6 = "@";
            r5.append(r6);
            r8 = r9[r3];
            r5.append(r8);
            r5 = r5.toString();
            r8 = new java.lang.StringBuilder;
            r8.<init>();
            r8.append(r6);
            r8.append(r4);
            r4 = r8.toString();
            r6 = 0;
            r4 = org.telegram.messenger.AndroidUtilities.generateSearchName(r5, r6, r4);
            r7.add(r4);
        L_0x01b2:
            r2.add(r12);
            if (r13 == 0) goto L_0x01c5;
        L_0x01b7:
            r1.add(r13);
            goto L_0x01c5;
        L_0x01bb:
            r6 = 0;
            r15 = r15 + 1;
            r16 = r5;
            r6 = r17;
            r5 = 0;
            goto L_0x011a;
        L_0x01c5:
            r10 = r10 + 1;
            r6 = r17;
            r5 = 0;
            r8 = 2;
            goto L_0x005b;
        L_0x01cd:
            r0.updateSearchResults(r1, r2, r7);
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.NotificationsCustomSettingsActivity$SearchAdapter.lambda$null$2$NotificationsCustomSettingsActivity$SearchAdapter(java.lang.String, java.util.ArrayList):void");
        }

        private void updateSearchResults(ArrayList<TLObject> arrayList, ArrayList<NotificationException> arrayList2, ArrayList<CharSequence> arrayList3) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$NotificationsCustomSettingsActivity$SearchAdapter$Xixkpo_qv6HwdTuxjyQyDUtVsrY(this, arrayList2, arrayList3, arrayList));
        }

        public /* synthetic */ void lambda$updateSearchResults$4$NotificationsCustomSettingsActivity$SearchAdapter(ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3) {
            this.searchRunnable = null;
            this.searchResult = arrayList;
            this.searchResultNames = arrayList2;
            this.searchAdapterHelper.mergeResults(arrayList3);
            if (NotificationsCustomSettingsActivity.this.searching && !this.searchAdapterHelper.isSearchInProgress()) {
                NotificationsCustomSettingsActivity.this.emptyView.showTextView();
            }
            notifyDataSetChanged();
        }

        public Object getObject(int i) {
            if (i >= 0 && i < this.searchResult.size()) {
                return this.searchResult.get(i);
            }
            i -= this.searchResult.size() + 1;
            return (i < 0 || i >= this.searchAdapterHelper.getGlobalSearch().size()) ? null : this.searchAdapterHelper.getGlobalSearch().get(i);
        }

        public int getItemCount() {
            int size = this.searchResult.size();
            ArrayList globalSearch = this.searchAdapterHelper.getGlobalSearch();
            return !globalSearch.isEmpty() ? size + (globalSearch.size() + 1) : size;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View graySectionCell;
            if (i != 0) {
                graySectionCell = new GraySectionCell(this.mContext);
            } else {
                View userCell = new UserCell(this.mContext, 4, 0, false, true);
                userCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            }
            return new Holder(graySectionCell);
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            int itemViewType = viewHolder.getItemViewType();
            boolean z = true;
            if (itemViewType == 0) {
                UserCell userCell = (UserCell) viewHolder.itemView;
                if (i < this.searchResult.size()) {
                    NotificationException notificationException = (NotificationException) this.searchResult.get(i);
                    CharSequence charSequence = (CharSequence) this.searchResultNames.get(i);
                    if (i == this.searchResult.size() - 1) {
                        z = false;
                    }
                    userCell.setException(notificationException, charSequence, z);
                    userCell.setAddButtonVisible(false);
                    return;
                }
                i -= this.searchResult.size() + 1;
                ArrayList globalSearch = this.searchAdapterHelper.getGlobalSearch();
                userCell.setData((TLObject) globalSearch.get(i), null, LocaleController.getString("NotificationsOn", NUM), 0, i != globalSearch.size() - 1);
                userCell.setAddButtonVisible(true);
            } else if (itemViewType == 1) {
                ((GraySectionCell) viewHolder.itemView).setText(LocaleController.getString("AddToExceptions", NUM));
            }
        }

        public int getItemViewType(int i) {
            return i == this.searchResult.size() ? 1 : 0;
        }
    }

    public NotificationsCustomSettingsActivity(int i, ArrayList<NotificationException> arrayList) {
        this(i, arrayList, false);
    }

    public NotificationsCustomSettingsActivity(int i, ArrayList<NotificationException> arrayList, boolean z) {
        int i2 = 0;
        this.rowCount = 0;
        this.exceptionsDict = new HashMap();
        this.currentType = i;
        this.exceptions = arrayList;
        i = this.exceptions.size();
        while (i2 < i) {
            NotificationException notificationException = (NotificationException) this.exceptions.get(i2);
            this.exceptionsDict.put(Long.valueOf(notificationException.did), notificationException);
            i2++;
        }
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
                    NotificationsCustomSettingsActivity.this.searchAdapter.searchDialogs(null);
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
                    if (NotificationsCustomSettingsActivity.this.searchAdapter != null) {
                        String obj = editText.getText().toString();
                        if (obj.length() != 0) {
                            NotificationsCustomSettingsActivity.this.searchWas = true;
                            if (NotificationsCustomSettingsActivity.this.listView != null) {
                                NotificationsCustomSettingsActivity.this.emptyView.setText(LocaleController.getString("NoResult", NUM));
                                NotificationsCustomSettingsActivity.this.emptyView.showProgress();
                                NotificationsCustomSettingsActivity.this.listView.setAdapter(NotificationsCustomSettingsActivity.this.searchAdapter);
                                NotificationsCustomSettingsActivity.this.searchAdapter.notifyDataSetChanged();
                                NotificationsCustomSettingsActivity.this.listView.setFastScrollVisible(false);
                                NotificationsCustomSettingsActivity.this.listView.setVerticalScrollBarEnabled(true);
                            }
                        }
                        NotificationsCustomSettingsActivity.this.searchAdapter.searchDialogs(obj);
                    }
                }
            }).setSearchFieldHint(LocaleController.getString("Search", NUM));
        }
        this.searchAdapter = new SearchAdapter(context);
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
        this.listView.setOnItemClickListener(new -$$Lambda$NotificationsCustomSettingsActivity$sfQe40z1hlRdvST16pR6KhKPuNI(this));
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

    /* JADX WARNING: Removed duplicated region for block: B:104:0x0256  */
    public /* synthetic */ void lambda$createView$9$NotificationsCustomSettingsActivity(android.view.View r16, int r17, float r18, float r19) {
        /*
        r15 = this;
        r9 = r15;
        r8 = r16;
        r0 = r17;
        r1 = r15.getParentActivity();
        if (r1 != 0) goto L_0x000c;
    L_0x000b:
        return;
    L_0x000c:
        r1 = r9.listView;
        r1 = r1.getAdapter();
        r2 = r9.searchAdapter;
        r10 = 0;
        r11 = 1;
        if (r1 == r2) goto L_0x0261;
    L_0x0018:
        r1 = r9.exceptionsStartRow;
        if (r0 < r1) goto L_0x0022;
    L_0x001c:
        r1 = r9.exceptionsEndRow;
        if (r0 >= r1) goto L_0x0022;
    L_0x0020:
        goto L_0x0261;
    L_0x0022:
        r1 = r9.exceptionsAddRow;
        r2 = 2;
        if (r0 != r1) goto L_0x005e;
    L_0x0027:
        r0 = new android.os.Bundle;
        r0.<init>();
        r1 = "onlySelect";
        r0.putBoolean(r1, r11);
        r1 = "checkCanWrite";
        r0.putBoolean(r1, r10);
        r1 = r9.currentType;
        r3 = "dialogsType";
        if (r1 != 0) goto L_0x0041;
    L_0x003c:
        r1 = 6;
        r0.putInt(r3, r1);
        goto L_0x004c;
    L_0x0041:
        if (r1 != r2) goto L_0x0048;
    L_0x0043:
        r1 = 5;
        r0.putInt(r3, r1);
        goto L_0x004c;
    L_0x0048:
        r1 = 4;
        r0.putInt(r3, r1);
    L_0x004c:
        r1 = new org.telegram.ui.DialogsActivity;
        r1.<init>(r0);
        r0 = new org.telegram.ui.-$$Lambda$NotificationsCustomSettingsActivity$b2R0H8L52T9zKW4WbPYtRj3Tixo;
        r0.<init>(r15);
        r1.setDelegate(r0);
        r15.presentFragment(r1);
        goto L_0x0251;
    L_0x005e:
        r1 = r9.deleteAllRow;
        r3 = 0;
        if (r0 != r1) goto L_0x00bc;
    L_0x0063:
        r0 = new org.telegram.ui.ActionBar.AlertDialog$Builder;
        r1 = r15.getParentActivity();
        r0.<init>(r1);
        r1 = NUM; // 0x7f0d06b8 float:1.8745603E38 double:1.0531306273E-314;
        r2 = "NotificationsDeleteAllExceptionTitle";
        r1 = org.telegram.messenger.LocaleController.getString(r2, r1);
        r0.setTitle(r1);
        r1 = NUM; // 0x7f0d06b7 float:1.8745601E38 double:1.053130627E-314;
        r2 = "NotificationsDeleteAllExceptionAlert";
        r1 = org.telegram.messenger.LocaleController.getString(r2, r1);
        r0.setMessage(r1);
        r1 = NUM; // 0x7f0d034d float:1.8743829E38 double:1.053130195E-314;
        r2 = "Delete";
        r1 = org.telegram.messenger.LocaleController.getString(r2, r1);
        r2 = new org.telegram.ui.-$$Lambda$NotificationsCustomSettingsActivity$pRUyMQCdjpmMJnKsyd8WpgNg8sY;
        r2.<init>(r15);
        r0.setPositiveButton(r1, r2);
        r1 = NUM; // 0x7f0d01f6 float:1.8743133E38 double:1.0531300256E-314;
        r2 = "Cancel";
        r1 = org.telegram.messenger.LocaleController.getString(r2, r1);
        r0.setNegativeButton(r1, r3);
        r0 = r0.create();
        r15.showDialog(r0);
        r1 = -1;
        r0 = r0.getButton(r1);
        r0 = (android.widget.TextView) r0;
        if (r0 == 0) goto L_0x0251;
    L_0x00b1:
        r1 = "dialogTextRed2";
        r1 = org.telegram.ui.ActionBar.Theme.getColor(r1);
        r0.setTextColor(r1);
        goto L_0x0251;
    L_0x00bc:
        r1 = r9.alertRow;
        if (r0 != r1) goto L_0x0107;
    L_0x00c0:
        r1 = r15.getNotificationsController();
        r2 = r9.currentType;
        r12 = r1.isGlobalNotificationsEnabled(r2);
        r1 = r8;
        r1 = (org.telegram.ui.Cells.NotificationsCheckCell) r1;
        r2 = r9.listView;
        r2 = r2.findViewHolderForAdapterPosition(r0);
        if (r12 != 0) goto L_0x00ef;
    L_0x00d5:
        r3 = r15.getNotificationsController();
        r4 = r9.currentType;
        r3.setGlobalNotificationsEnabled(r4, r10);
        r3 = r12 ^ 1;
        r1.setChecked(r3);
        if (r2 == 0) goto L_0x00ea;
    L_0x00e5:
        r1 = r9.adapter;
        r1.onBindViewHolder(r2, r0);
    L_0x00ea:
        r15.checkRowsEnabled();
        goto L_0x0252;
    L_0x00ef:
        r3 = 0;
        r5 = r9.currentType;
        r6 = r9.exceptions;
        r7 = r9.currentAccount;
        r13 = new org.telegram.ui.-$$Lambda$NotificationsCustomSettingsActivity$VFCQMWFkSW5aZbqqxr0wXj6qWvY;
        r13.<init>(r15, r1, r2, r0);
        r1 = r15;
        r2 = r3;
        r4 = r5;
        r5 = r6;
        r6 = r7;
        r7 = r13;
        org.telegram.ui.Components.AlertsCreator.showCustomNotificationsDialog(r1, r2, r4, r5, r6, r7);
        goto L_0x0252;
    L_0x0107:
        r1 = r9.previewRow;
        if (r0 != r1) goto L_0x0152;
    L_0x010b:
        r0 = r16.isEnabled();
        if (r0 != 0) goto L_0x0112;
    L_0x0111:
        return;
    L_0x0112:
        r0 = r15.getNotificationsSettings();
        r1 = r0.edit();
        r2 = r9.currentType;
        if (r2 != r11) goto L_0x012a;
    L_0x011e:
        r2 = "EnablePreviewAll";
        r0 = r0.getBoolean(r2, r11);
        r3 = r0 ^ 1;
        r1.putBoolean(r2, r3);
        goto L_0x0143;
    L_0x012a:
        if (r2 != 0) goto L_0x0138;
    L_0x012c:
        r2 = "EnablePreviewGroup";
        r0 = r0.getBoolean(r2, r11);
        r3 = r0 ^ 1;
        r1.putBoolean(r2, r3);
        goto L_0x0143;
    L_0x0138:
        r2 = "EnablePreviewChannel";
        r0 = r0.getBoolean(r2, r11);
        r3 = r0 ^ 1;
        r1.putBoolean(r2, r3);
    L_0x0143:
        r1.commit();
        r1 = r15.getNotificationsController();
        r2 = r9.currentType;
        r1.updateServerNotificationsSettings(r2);
        r12 = r0;
        goto L_0x0252;
    L_0x0152:
        r1 = r9.messageSoundRow;
        if (r0 != r1) goto L_0x01cc;
    L_0x0156:
        r1 = r16.isEnabled();
        if (r1 != 0) goto L_0x015d;
    L_0x015c:
        return;
    L_0x015d:
        r1 = r15.getNotificationsSettings();	 Catch:{ Exception -> 0x01c6 }
        r4 = new android.content.Intent;	 Catch:{ Exception -> 0x01c6 }
        r5 = "android.intent.action.RINGTONE_PICKER";
        r4.<init>(r5);	 Catch:{ Exception -> 0x01c6 }
        r5 = "android.intent.extra.ringtone.TYPE";
        r4.putExtra(r5, r2);	 Catch:{ Exception -> 0x01c6 }
        r5 = "android.intent.extra.ringtone.SHOW_DEFAULT";
        r4.putExtra(r5, r11);	 Catch:{ Exception -> 0x01c6 }
        r5 = "android.intent.extra.ringtone.SHOW_SILENT";
        r4.putExtra(r5, r11);	 Catch:{ Exception -> 0x01c6 }
        r5 = "android.intent.extra.ringtone.DEFAULT_URI";
        r2 = android.media.RingtoneManager.getDefaultUri(r2);	 Catch:{ Exception -> 0x01c6 }
        r4.putExtra(r5, r2);	 Catch:{ Exception -> 0x01c6 }
        r2 = android.provider.Settings.System.DEFAULT_NOTIFICATION_URI;	 Catch:{ Exception -> 0x01c6 }
        if (r2 == 0) goto L_0x0189;
    L_0x0184:
        r5 = r2.getPath();	 Catch:{ Exception -> 0x01c6 }
        goto L_0x018a;
    L_0x0189:
        r5 = r3;
    L_0x018a:
        r6 = r9.currentType;	 Catch:{ Exception -> 0x01c6 }
        if (r6 != r11) goto L_0x0195;
    L_0x018e:
        r6 = "GlobalSoundPath";
        r1 = r1.getString(r6, r5);	 Catch:{ Exception -> 0x01c6 }
        goto L_0x01a6;
    L_0x0195:
        r6 = r9.currentType;	 Catch:{ Exception -> 0x01c6 }
        if (r6 != 0) goto L_0x01a0;
    L_0x0199:
        r6 = "GroupSoundPath";
        r1 = r1.getString(r6, r5);	 Catch:{ Exception -> 0x01c6 }
        goto L_0x01a6;
    L_0x01a0:
        r6 = "ChannelSoundPath";
        r1 = r1.getString(r6, r5);	 Catch:{ Exception -> 0x01c6 }
    L_0x01a6:
        if (r1 == 0) goto L_0x01bc;
    L_0x01a8:
        r6 = "NoSound";
        r6 = r1.equals(r6);	 Catch:{ Exception -> 0x01c6 }
        if (r6 != 0) goto L_0x01bc;
    L_0x01b0:
        r3 = r1.equals(r5);	 Catch:{ Exception -> 0x01c6 }
        if (r3 == 0) goto L_0x01b8;
    L_0x01b6:
        r3 = r2;
        goto L_0x01bc;
    L_0x01b8:
        r3 = android.net.Uri.parse(r1);	 Catch:{ Exception -> 0x01c6 }
    L_0x01bc:
        r1 = "android.intent.extra.ringtone.EXISTING_URI";
        r4.putExtra(r1, r3);	 Catch:{ Exception -> 0x01c6 }
        r15.startActivityForResult(r4, r0);	 Catch:{ Exception -> 0x01c6 }
        goto L_0x0251;
    L_0x01c6:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
        goto L_0x0251;
    L_0x01cc:
        r1 = r9.messageLedRow;
        r2 = 0;
        if (r0 != r1) goto L_0x01ec;
    L_0x01d2:
        r1 = r16.isEnabled();
        if (r1 != 0) goto L_0x01d9;
    L_0x01d8:
        return;
    L_0x01d9:
        r1 = r15.getParentActivity();
        r4 = r9.currentType;
        r5 = new org.telegram.ui.-$$Lambda$NotificationsCustomSettingsActivity$2Ybp2U7lQ3YxdvjXmNbMJQtkwOQ;
        r5.<init>(r15, r0);
        r0 = org.telegram.ui.Components.AlertsCreator.createColorSelectDialog(r1, r2, r4, r5);
        r15.showDialog(r0);
        goto L_0x0251;
    L_0x01ec:
        r1 = r9.messagePopupNotificationRow;
        if (r0 != r1) goto L_0x020a;
    L_0x01f0:
        r1 = r16.isEnabled();
        if (r1 != 0) goto L_0x01f7;
    L_0x01f6:
        return;
    L_0x01f7:
        r1 = r15.getParentActivity();
        r2 = r9.currentType;
        r3 = new org.telegram.ui.-$$Lambda$NotificationsCustomSettingsActivity$5JQeM2VZAZohz71seJkzo9Axyl8;
        r3.<init>(r15, r0);
        r0 = org.telegram.ui.Components.AlertsCreator.createPopupSelectDialog(r1, r2, r3);
        r15.showDialog(r0);
        goto L_0x0251;
    L_0x020a:
        r1 = r9.messageVibrateRow;
        if (r0 != r1) goto L_0x0234;
    L_0x020e:
        r1 = r16.isEnabled();
        if (r1 != 0) goto L_0x0215;
    L_0x0214:
        return;
    L_0x0215:
        r1 = r9.currentType;
        if (r1 != r11) goto L_0x021c;
    L_0x0219:
        r1 = "vibrate_messages";
        goto L_0x0223;
    L_0x021c:
        if (r1 != 0) goto L_0x0221;
    L_0x021e:
        r1 = "vibrate_group";
        goto L_0x0223;
    L_0x0221:
        r1 = "vibrate_channel";
    L_0x0223:
        r4 = r15.getParentActivity();
        r5 = new org.telegram.ui.-$$Lambda$NotificationsCustomSettingsActivity$tal7L4g8KTgKJ9Uilx8RMZ5-g8Y;
        r5.<init>(r15, r0);
        r0 = org.telegram.ui.Components.AlertsCreator.createVibrationSelectDialog(r4, r2, r1, r5);
        r15.showDialog(r0);
        goto L_0x0251;
    L_0x0234:
        r1 = r9.messagePriorityRow;
        if (r0 != r1) goto L_0x0251;
    L_0x0238:
        r1 = r16.isEnabled();
        if (r1 != 0) goto L_0x023f;
    L_0x023e:
        return;
    L_0x023f:
        r1 = r15.getParentActivity();
        r4 = r9.currentType;
        r5 = new org.telegram.ui.-$$Lambda$NotificationsCustomSettingsActivity$Ig_caPr5JXCMhTvLGVBkXIYtDXI;
        r5.<init>(r15, r0);
        r0 = org.telegram.ui.Components.AlertsCreator.createPrioritySelectDialog(r1, r2, r4, r5);
        r15.showDialog(r0);
    L_0x0251:
        r12 = 0;
    L_0x0252:
        r0 = r8 instanceof org.telegram.ui.Cells.TextCheckCell;
        if (r0 == 0) goto L_0x0260;
    L_0x0256:
        r0 = r8;
        r0 = (org.telegram.ui.Cells.TextCheckCell) r0;
        if (r12 != 0) goto L_0x025c;
    L_0x025b:
        goto L_0x025d;
    L_0x025c:
        r11 = 0;
    L_0x025d:
        r0.setChecked(r11);
    L_0x0260:
        return;
    L_0x0261:
        r1 = r9.listView;
        r1 = r1.getAdapter();
        r2 = r9.searchAdapter;
        if (r1 != r2) goto L_0x02c7;
    L_0x026b:
        r1 = r2.getObject(r0);
        r2 = r1 instanceof org.telegram.ui.NotificationsSettingsActivity.NotificationException;
        if (r2 == 0) goto L_0x027c;
    L_0x0273:
        r2 = r9.searchAdapter;
        r2 = r2.searchResult;
        r1 = (org.telegram.ui.NotificationsSettingsActivity.NotificationException) r1;
        goto L_0x02c3;
    L_0x027c:
        r2 = r1 instanceof org.telegram.tgnet.TLRPC.User;
        if (r2 == 0) goto L_0x0286;
    L_0x0280:
        r3 = r1;
        r3 = (org.telegram.tgnet.TLRPC.User) r3;
        r3 = r3.id;
        goto L_0x028c;
    L_0x0286:
        r3 = r1;
        r3 = (org.telegram.tgnet.TLRPC.Chat) r3;
        r3 = r3.id;
        r3 = -r3;
    L_0x028c:
        r3 = (long) r3;
        r5 = r9.exceptionsDict;
        r6 = java.lang.Long.valueOf(r3);
        r5 = r5.containsKey(r6);
        if (r5 == 0) goto L_0x02a6;
    L_0x0299:
        r1 = r9.exceptionsDict;
        r2 = java.lang.Long.valueOf(r3);
        r1 = r1.get(r2);
        r1 = (org.telegram.ui.NotificationsSettingsActivity.NotificationException) r1;
        goto L_0x02c1;
    L_0x02a6:
        r5 = new org.telegram.ui.NotificationsSettingsActivity$NotificationException;
        r5.<init>();
        r5.did = r3;
        if (r2 == 0) goto L_0x02b7;
    L_0x02af:
        r1 = (org.telegram.tgnet.TLRPC.User) r1;
        r1 = r1.id;
        r1 = (long) r1;
        r5.did = r1;
        goto L_0x02bf;
    L_0x02b7:
        r1 = (org.telegram.tgnet.TLRPC.Chat) r1;
        r1 = r1.id;
        r1 = -r1;
        r1 = (long) r1;
        r5.did = r1;
    L_0x02bf:
        r1 = r5;
        r10 = 1;
    L_0x02c1:
        r2 = r9.exceptions;
    L_0x02c3:
        r5 = r1;
        r4 = r2;
        r3 = r10;
        goto L_0x02df;
    L_0x02c7:
        r1 = r9.exceptions;
        r2 = r9.exceptionsStartRow;
        r2 = r0 - r2;
        if (r2 < 0) goto L_0x02fc;
    L_0x02cf:
        r3 = r1.size();
        if (r2 < r3) goto L_0x02d6;
    L_0x02d5:
        goto L_0x02fc;
    L_0x02d6:
        r2 = r1.get(r2);
        r2 = (org.telegram.ui.NotificationsSettingsActivity.NotificationException) r2;
        r4 = r1;
        r5 = r2;
        r3 = 0;
    L_0x02df:
        if (r5 != 0) goto L_0x02e2;
    L_0x02e1:
        return;
    L_0x02e2:
        r7 = r5.did;
        r10 = -1;
        r11 = 0;
        r12 = r9.currentAccount;
        r13 = 0;
        r14 = new org.telegram.ui.-$$Lambda$NotificationsCustomSettingsActivity$dM_aKGWU1Pil-W1rpe53rVJTYxQ;
        r1 = r14;
        r2 = r15;
        r6 = r17;
        r1.<init>(r2, r3, r4, r5, r6);
        r1 = r15;
        r2 = r7;
        r4 = r10;
        r5 = r11;
        r6 = r12;
        r7 = r13;
        r8 = r14;
        org.telegram.ui.Components.AlertsCreator.showCustomNotificationsDialog(r1, r2, r4, r5, r6, r7, r8);
    L_0x02fc:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.NotificationsCustomSettingsActivity.lambda$createView$9$NotificationsCustomSettingsActivity(android.view.View, int, float, float):void");
    }

    public /* synthetic */ void lambda$null$0$NotificationsCustomSettingsActivity(boolean z, ArrayList arrayList, NotificationException notificationException, int i, int i2) {
        if (i2 != 0) {
            SharedPreferences notificationsSettings = getNotificationsSettings();
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
            if (z) {
                this.exceptions.add(notificationException);
                this.exceptionsDict.put(Long.valueOf(notificationException.did), notificationException);
                updateRows();
                this.adapter.notifyDataSetChanged();
            } else {
                this.listView.getAdapter().notifyItemChanged(i);
            }
            this.actionBar.closeSearchField();
        } else if (!z) {
            ArrayList arrayList2 = this.exceptions;
            if (arrayList != arrayList2) {
                int indexOf = arrayList2.indexOf(notificationException);
                if (indexOf >= 0) {
                    this.exceptions.remove(indexOf);
                    this.exceptionsDict.remove(Long.valueOf(notificationException.did));
                }
            }
            arrayList.remove(notificationException);
            if (this.exceptionsAddRow != -1 && arrayList.isEmpty() && arrayList == this.exceptions) {
                this.listView.getAdapter().notifyItemChanged(this.exceptionsAddRow);
                this.listView.getAdapter().notifyItemRemoved(this.deleteAllRow);
                this.listView.getAdapter().notifyItemRemoved(this.deleteAllSectionRow);
            }
            this.listView.getAdapter().notifyItemRemoved(i);
            updateRows();
            checkRowsEnabled();
            this.actionBar.closeSearchField();
        }
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

    public /* synthetic */ void lambda$null$3$NotificationsCustomSettingsActivity(DialogInterface dialogInterface, int i) {
        Editor edit = getNotificationsSettings().edit();
        i = this.exceptions.size();
        for (int i2 = 0; i2 < i; i2++) {
            NotificationException notificationException = (NotificationException) this.exceptions.get(i2);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("notify2_");
            stringBuilder.append(notificationException.did);
            Editor remove = edit.remove(stringBuilder.toString());
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("custom_");
            stringBuilder2.append(notificationException.did);
            remove.remove(stringBuilder2.toString());
            getMessagesStorage().setDialogFlags(notificationException.did, 0);
            Dialog dialog = (Dialog) getMessagesController().dialogs_dict.get(notificationException.did);
            if (dialog != null) {
                dialog.notify_settings = new TL_peerNotifySettings();
            }
        }
        edit.commit();
        int size = this.exceptions.size();
        for (i = 0; i < size; i++) {
            getNotificationsController().updateServerNotificationsSettings(((NotificationException) this.exceptions.get(i)).did, false);
        }
        this.exceptions.clear();
        this.exceptionsDict.clear();
        updateRows();
        getNotificationCenter().postNotificationName(NotificationCenter.notificationsSettingsUpdated, new Object[0]);
        this.adapter.notifyDataSetChanged();
    }

    public /* synthetic */ void lambda$null$4$NotificationsCustomSettingsActivity(NotificationsCheckCell notificationsCheckCell, ViewHolder viewHolder, int i, int i2) {
        SharedPreferences notificationsSettings = getNotificationsSettings();
        int i3 = this.currentType;
        int i4 = 0;
        if (i3 == 1) {
            i2 = notificationsSettings.getInt("EnableAll2", 0);
        } else if (i3 == 0) {
            i2 = notificationsSettings.getInt("EnableGroup2", 0);
        } else {
            i2 = notificationsSettings.getInt("EnableChannel2", 0);
        }
        i3 = getConnectionsManager().getCurrentTime();
        if (i2 >= i3 && i2 - 31536000 < i3) {
            i4 = 2;
        }
        notificationsCheckCell.setChecked(getNotificationsController().isGlobalNotificationsEnabled(this.currentType), i4);
        if (viewHolder != null) {
            this.adapter.onBindViewHolder(viewHolder, i);
        }
        checkRowsEnabled();
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

    public /* synthetic */ void lambda$null$8$NotificationsCustomSettingsActivity(int i) {
        ViewHolder findViewHolderForAdapterPosition = this.listView.findViewHolderForAdapterPosition(i);
        if (findViewHolderForAdapterPosition != null) {
            this.adapter.onBindViewHolder(findViewHolderForAdapterPosition, i);
        }
    }

    private void checkRowsEnabled() {
        if (this.exceptions.isEmpty()) {
            int childCount = this.listView.getChildCount();
            ArrayList arrayList = new ArrayList();
            boolean isGlobalNotificationsEnabled = getNotificationsController().isGlobalNotificationsEnabled(this.currentType);
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
        getMessagesStorage().getStorageQueue().postRunnable(new -$$Lambda$NotificationsCustomSettingsActivity$5Nk_2OqryZpXwaqUPQYxIZIo9m8(this));
    }

    /* JADX WARNING: Removed duplicated region for block: B:90:0x0226  */
    /* JADX WARNING: Removed duplicated region for block: B:107:0x0267  */
    /* JADX WARNING: Removed duplicated region for block: B:113:0x0282 A:{LOOP_END, LOOP:3: B:112:0x0280->B:113:0x0282} */
    /* JADX WARNING: Removed duplicated region for block: B:116:0x029a  */
    /* JADX WARNING: Removed duplicated region for block: B:90:0x0226  */
    /* JADX WARNING: Removed duplicated region for block: B:107:0x0267  */
    /* JADX WARNING: Removed duplicated region for block: B:113:0x0282 A:{LOOP_END, LOOP:3: B:112:0x0280->B:113:0x0282} */
    /* JADX WARNING: Removed duplicated region for block: B:116:0x029a  */
    /* JADX WARNING: Removed duplicated region for block: B:90:0x0226  */
    /* JADX WARNING: Removed duplicated region for block: B:107:0x0267  */
    /* JADX WARNING: Removed duplicated region for block: B:113:0x0282 A:{LOOP_END, LOOP:3: B:112:0x0280->B:113:0x0282} */
    /* JADX WARNING: Removed duplicated region for block: B:116:0x029a  */
    /* JADX WARNING: Missing block: B:21:0x00fd, code skipped:
            if (r8.deleted != false) goto L_0x0144;
     */
    /* JADX WARNING: Missing block: B:48:0x0192, code skipped:
            if (r3.deleted != false) goto L_0x01b1;
     */
    public /* synthetic */ void lambda$loadExceptions$11$NotificationsCustomSettingsActivity() {
        /*
        r21 = this;
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
        r9 = new java.util.ArrayList;
        r9.<init>();
        r10 = r21.getUserConfig();
        r10 = r10.clientUserId;
        r11 = r21.getNotificationsSettings();
        r12 = r11.getAll();
        r13 = r12.entrySet();
        r13 = r13.iterator();
    L_0x0048:
        r14 = r13.hasNext();
        if (r14 == 0) goto L_0x01bf;
    L_0x004e:
        r14 = r13.next();
        r14 = (java.util.Map.Entry) r14;
        r16 = r14.getKey();
        r15 = r16;
        r15 = (java.lang.String) r15;
        r16 = r13;
        r13 = "notify2_";
        r17 = r15.startsWith(r13);
        if (r17 == 0) goto L_0x01a7;
    L_0x0066:
        r17 = r5;
        r5 = "";
        r5 = r15.replace(r13, r5);
        r13 = org.telegram.messenger.Utilities.parseLong(r5);
        r15 = r3;
        r18 = r4;
        r3 = r13.longValue();
        r19 = 0;
        r13 = (r3 > r19 ? 1 : (r3 == r19 ? 0 : -1));
        if (r13 == 0) goto L_0x01a0;
    L_0x007f:
        r13 = r7;
        r19 = r8;
        r7 = (long) r10;
        r20 = (r3 > r7 ? 1 : (r3 == r7 ? 0 : -1));
        if (r20 == 0) goto L_0x0199;
    L_0x0087:
        r7 = new org.telegram.ui.NotificationsSettingsActivity$NotificationException;
        r7.<init>();
        r7.did = r3;
        r8 = new java.lang.StringBuilder;
        r8.<init>();
        r20 = r10;
        r10 = "custom_";
        r8.append(r10);
        r8.append(r3);
        r8 = r8.toString();
        r10 = 0;
        r8 = r11.getBoolean(r8, r10);
        r7.hasCustom = r8;
        r8 = r14.getValue();
        r8 = (java.lang.Integer) r8;
        r8 = r8.intValue();
        r7.notify = r8;
        r8 = r7.notify;
        if (r8 == 0) goto L_0x00d7;
    L_0x00b8:
        r8 = new java.lang.StringBuilder;
        r8.<init>();
        r10 = "notifyuntil_";
        r8.append(r10);
        r8.append(r5);
        r5 = r8.toString();
        r5 = r12.get(r5);
        r5 = (java.lang.Integer) r5;
        if (r5 == 0) goto L_0x00d7;
    L_0x00d1:
        r5 = r5.intValue();
        r7.muteUntil = r5;
    L_0x00d7:
        r5 = (int) r3;
        r14 = r11;
        r8 = 32;
        r10 = r3 << r8;
        r8 = (int) r10;
        if (r5 == 0) goto L_0x0151;
    L_0x00e0:
        if (r5 <= 0) goto L_0x0105;
    L_0x00e2:
        r8 = r21.getMessagesController();
        r10 = java.lang.Integer.valueOf(r5);
        r8 = r8.getUser(r10);
        if (r8 != 0) goto L_0x00fb;
    L_0x00f0:
        r5 = java.lang.Integer.valueOf(r5);
        r0.add(r5);
        r1.put(r3, r7);
        goto L_0x0100;
    L_0x00fb:
        r3 = r8.deleted;
        if (r3 == 0) goto L_0x0100;
    L_0x00ff:
        goto L_0x0144;
    L_0x0100:
        r6.add(r7);
        goto L_0x019c;
    L_0x0105:
        r8 = r21.getMessagesController();
        r5 = -r5;
        r10 = java.lang.Integer.valueOf(r5);
        r8 = r8.getChat(r10);
        if (r8 != 0) goto L_0x011f;
    L_0x0114:
        r5 = java.lang.Integer.valueOf(r5);
        r2.add(r5);
        r1.put(r3, r7);
        goto L_0x0144;
    L_0x011f:
        r3 = r8.left;
        if (r3 != 0) goto L_0x0144;
    L_0x0123:
        r3 = r8.kicked;
        if (r3 != 0) goto L_0x0144;
    L_0x0127:
        r3 = r8.migrated_to;
        if (r3 == 0) goto L_0x012c;
    L_0x012b:
        goto L_0x0144;
    L_0x012c:
        r3 = org.telegram.messenger.ChatObject.isChannel(r8);
        if (r3 == 0) goto L_0x013d;
    L_0x0132:
        r3 = r8.megagroup;
        if (r3 != 0) goto L_0x013d;
    L_0x0136:
        r11 = r19;
        r11.add(r7);
        goto L_0x01a5;
    L_0x013d:
        r11 = r19;
        r13.add(r7);
        goto L_0x01a5;
    L_0x0144:
        r7 = r13;
        r11 = r14;
        r3 = r15;
        r13 = r16;
        r5 = r17;
        r4 = r18;
        r8 = r19;
        goto L_0x01bb;
    L_0x0151:
        r11 = r19;
        if (r8 == 0) goto L_0x01a5;
    L_0x0155:
        r5 = r21.getMessagesController();
        r10 = java.lang.Integer.valueOf(r8);
        r5 = r5.getEncryptedChat(r10);
        if (r5 != 0) goto L_0x016f;
    L_0x0163:
        r5 = java.lang.Integer.valueOf(r8);
        r8 = r15;
        r8.add(r5);
        r1.put(r3, r7);
        goto L_0x0195;
    L_0x016f:
        r8 = r15;
        r3 = r21.getMessagesController();
        r4 = r5.user_id;
        r4 = java.lang.Integer.valueOf(r4);
        r3 = r3.getUser(r4);
        if (r3 != 0) goto L_0x0190;
    L_0x0180:
        r3 = r5.user_id;
        r3 = java.lang.Integer.valueOf(r3);
        r0.add(r3);
        r3 = r5.user_id;
        r3 = (long) r3;
        r1.put(r3, r7);
        goto L_0x0195;
    L_0x0190:
        r3 = r3.deleted;
        if (r3 == 0) goto L_0x0195;
    L_0x0194:
        goto L_0x01b1;
    L_0x0195:
        r6.add(r7);
        goto L_0x01b1;
    L_0x0199:
        r20 = r10;
        r14 = r11;
    L_0x019c:
        r8 = r15;
        r11 = r19;
        goto L_0x01b1;
    L_0x01a0:
        r13 = r7;
        r20 = r10;
        r14 = r11;
        r11 = r8;
    L_0x01a5:
        r8 = r15;
        goto L_0x01b1;
    L_0x01a7:
        r18 = r4;
        r17 = r5;
        r13 = r7;
        r20 = r10;
        r14 = r11;
        r11 = r8;
        r8 = r3;
    L_0x01b1:
        r3 = r8;
        r8 = r11;
        r7 = r13;
        r11 = r14;
        r13 = r16;
        r5 = r17;
        r4 = r18;
    L_0x01bb:
        r10 = r20;
        goto L_0x0048;
    L_0x01bf:
        r18 = r4;
        r17 = r5;
        r13 = r7;
        r11 = r8;
        r10 = 0;
        r8 = r3;
        r3 = r1.size();
        if (r3 == 0) goto L_0x02ba;
    L_0x01cd:
        r3 = r8.isEmpty();	 Catch:{ Exception -> 0x0217 }
        r4 = ",";
        if (r3 != 0) goto L_0x01e0;
    L_0x01d5:
        r3 = r21.getMessagesStorage();	 Catch:{ Exception -> 0x0217 }
        r5 = android.text.TextUtils.join(r4, r8);	 Catch:{ Exception -> 0x0217 }
        r3.getEncryptedChatsInternal(r5, r9, r0);	 Catch:{ Exception -> 0x0217 }
    L_0x01e0:
        r3 = r0.isEmpty();	 Catch:{ Exception -> 0x0217 }
        if (r3 != 0) goto L_0x01f8;
    L_0x01e6:
        r3 = r21.getMessagesStorage();	 Catch:{ Exception -> 0x01f4 }
        r0 = android.text.TextUtils.join(r4, r0);	 Catch:{ Exception -> 0x01f4 }
        r5 = r18;
        r3.getUsersInternal(r0, r5);	 Catch:{ Exception -> 0x0213 }
        goto L_0x01fa;
    L_0x01f4:
        r0 = move-exception;
        r5 = r18;
        goto L_0x0214;
    L_0x01f8:
        r5 = r18;
    L_0x01fa:
        r0 = r2.isEmpty();	 Catch:{ Exception -> 0x0213 }
        if (r0 != 0) goto L_0x0210;
    L_0x0200:
        r0 = r21.getMessagesStorage();	 Catch:{ Exception -> 0x0213 }
        r2 = android.text.TextUtils.join(r4, r2);	 Catch:{ Exception -> 0x0213 }
        r4 = r17;
        r0.getChatsInternal(r2, r4);	 Catch:{ Exception -> 0x020e }
        goto L_0x021f;
    L_0x020e:
        r0 = move-exception;
        goto L_0x021c;
    L_0x0210:
        r4 = r17;
        goto L_0x021f;
    L_0x0213:
        r0 = move-exception;
    L_0x0214:
        r4 = r17;
        goto L_0x021c;
    L_0x0217:
        r0 = move-exception;
        r4 = r17;
        r5 = r18;
    L_0x021c:
        org.telegram.messenger.FileLog.e(r0);
    L_0x021f:
        r0 = r4.size();
        r2 = 0;
    L_0x0224:
        if (r2 >= r0) goto L_0x0260;
    L_0x0226:
        r3 = r4.get(r2);
        r3 = (org.telegram.tgnet.TLRPC.Chat) r3;
        r7 = r3.left;
        if (r7 != 0) goto L_0x025d;
    L_0x0230:
        r7 = r3.kicked;
        if (r7 != 0) goto L_0x025d;
    L_0x0234:
        r7 = r3.migrated_to;
        if (r7 == 0) goto L_0x0239;
    L_0x0238:
        goto L_0x025d;
    L_0x0239:
        r7 = r3.id;
        r7 = -r7;
        r7 = (long) r7;
        r7 = r1.get(r7);
        r7 = (org.telegram.ui.NotificationsSettingsActivity.NotificationException) r7;
        r8 = r3.id;
        r8 = -r8;
        r14 = (long) r8;
        r1.remove(r14);
        if (r7 == 0) goto L_0x025d;
    L_0x024c:
        r8 = org.telegram.messenger.ChatObject.isChannel(r3);
        if (r8 == 0) goto L_0x025a;
    L_0x0252:
        r3 = r3.megagroup;
        if (r3 != 0) goto L_0x025a;
    L_0x0256:
        r11.add(r7);
        goto L_0x025d;
    L_0x025a:
        r13.add(r7);
    L_0x025d:
        r2 = r2 + 1;
        goto L_0x0224;
    L_0x0260:
        r0 = r5.size();
        r2 = 0;
    L_0x0265:
        if (r2 >= r0) goto L_0x027b;
    L_0x0267:
        r3 = r5.get(r2);
        r3 = (org.telegram.tgnet.TLRPC.User) r3;
        r7 = r3.deleted;
        if (r7 == 0) goto L_0x0272;
    L_0x0271:
        goto L_0x0278;
    L_0x0272:
        r3 = r3.id;
        r7 = (long) r3;
        r1.remove(r7);
    L_0x0278:
        r2 = r2 + 1;
        goto L_0x0265;
    L_0x027b:
        r0 = r9.size();
        r2 = 0;
    L_0x0280:
        if (r2 >= r0) goto L_0x0294;
    L_0x0282:
        r3 = r9.get(r2);
        r3 = (org.telegram.tgnet.TLRPC.EncryptedChat) r3;
        r3 = r3.id;
        r7 = (long) r3;
        r3 = 32;
        r7 = r7 << r3;
        r1.remove(r7);
        r2 = r2 + 1;
        goto L_0x0280;
    L_0x0294:
        r0 = r1.size();
    L_0x0298:
        if (r10 >= r0) goto L_0x02be;
    L_0x029a:
        r2 = r1.keyAt(r10);
        r3 = (int) r2;
        if (r3 >= 0) goto L_0x02b0;
    L_0x02a1:
        r2 = r1.valueAt(r10);
        r13.remove(r2);
        r2 = r1.valueAt(r10);
        r11.remove(r2);
        goto L_0x02b7;
    L_0x02b0:
        r2 = r1.valueAt(r10);
        r6.remove(r2);
    L_0x02b7:
        r10 = r10 + 1;
        goto L_0x0298;
    L_0x02ba:
        r4 = r17;
        r5 = r18;
    L_0x02be:
        r0 = new org.telegram.ui.-$$Lambda$NotificationsCustomSettingsActivity$jIvwOL310iFc7BZAadFlNMU4qZs;
        r1 = r0;
        r2 = r21;
        r3 = r5;
        r5 = r9;
        r7 = r13;
        r8 = r11;
        r1.<init>(r2, r3, r4, r5, r6, r7, r8);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r0);
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.NotificationsCustomSettingsActivity.lambda$loadExceptions$11$NotificationsCustomSettingsActivity():void");
    }

    public /* synthetic */ void lambda$null$10$NotificationsCustomSettingsActivity(ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3, ArrayList arrayList4, ArrayList arrayList5, ArrayList arrayList6) {
        getMessagesController().putUsers(arrayList, true);
        getMessagesController().putChats(arrayList2, true);
        getMessagesController().putEncryptedChats(arrayList3, true);
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
                arrayList = this.exceptions;
                if (arrayList != null || arrayList.isEmpty()) {
                    this.deleteAllRow = -1;
                    this.deleteAllSectionRow = -1;
                }
                i = this.rowCount;
                this.rowCount = i + 1;
                this.deleteAllRow = i;
                i = this.rowCount;
                this.rowCount = i + 1;
                this.deleteAllSectionRow = i;
                return;
            }
        }
        i = this.rowCount;
        this.rowCount = i + 1;
        this.exceptionsSection2Row = i;
        arrayList = this.exceptions;
        if (arrayList != null) {
        }
        this.deleteAllRow = -1;
        this.deleteAllSectionRow = -1;
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
            Editor edit = getNotificationsSettings().edit();
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
            getNotificationsController().updateServerNotificationsSettings(this.currentType);
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
        -$$Lambda$NotificationsCustomSettingsActivity$YpE9rwbQwhMphj8bqCEt9ez12V8 -__lambda_notificationscustomsettingsactivity_ype9rwbqwhmphj8bqcet9ez12v8 = new -$$Lambda$NotificationsCustomSettingsActivity$YpE9rwbQwhMphj8bqCEt9ez12V8(this);
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[39];
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
        ThemeDescriptionDelegate themeDescriptionDelegate = -__lambda_notificationscustomsettingsactivity_ype9rwbqwhmphj8bqcet9ez12v8;
        themeDescriptionArr[16] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusColor"}, null, null, themeDescriptionDelegate, "windowBackgroundWhiteGrayText");
        themeDescriptionArr[17] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusOnlineColor"}, null, null, themeDescriptionDelegate, "windowBackgroundWhiteBlueText");
        themeDescriptionArr[18] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, null, new Drawable[]{Theme.avatar_broadcastDrawable, Theme.avatar_savedDrawable}, null, "avatar_text");
        -$$Lambda$NotificationsCustomSettingsActivity$YpE9rwbQwhMphj8bqCEt9ez12V8 -__lambda_notificationscustomsettingsactivity_ype9rwbqwhmphj8bqcet9ez12v82 = -__lambda_notificationscustomsettingsactivity_ype9rwbqwhmphj8bqcet9ez12v8;
        themeDescriptionArr[19] = new ThemeDescription(null, 0, null, null, null, -__lambda_notificationscustomsettingsactivity_ype9rwbqwhmphj8bqcet9ez12v82, "avatar_backgroundRed");
        themeDescriptionArr[20] = new ThemeDescription(null, 0, null, null, null, -__lambda_notificationscustomsettingsactivity_ype9rwbqwhmphj8bqcet9ez12v82, "avatar_backgroundOrange");
        themeDescriptionArr[21] = new ThemeDescription(null, 0, null, null, null, -__lambda_notificationscustomsettingsactivity_ype9rwbqwhmphj8bqcet9ez12v82, "avatar_backgroundViolet");
        themeDescriptionArr[22] = new ThemeDescription(null, 0, null, null, null, -__lambda_notificationscustomsettingsactivity_ype9rwbqwhmphj8bqcet9ez12v82, "avatar_backgroundGreen");
        themeDescriptionArr[23] = new ThemeDescription(null, 0, null, null, null, -__lambda_notificationscustomsettingsactivity_ype9rwbqwhmphj8bqcet9ez12v82, "avatar_backgroundCyan");
        themeDescriptionArr[24] = new ThemeDescription(null, 0, null, null, null, -__lambda_notificationscustomsettingsactivity_ype9rwbqwhmphj8bqcet9ez12v82, "avatar_backgroundBlue");
        themeDescriptionArr[25] = new ThemeDescription(null, 0, null, null, null, -__lambda_notificationscustomsettingsactivity_ype9rwbqwhmphj8bqcet9ez12v82, "avatar_backgroundPink");
        themeDescriptionArr[26] = new ThemeDescription(this.listView, 0, new Class[]{GraySectionCell.class}, new String[]{"textView"}, null, null, null, "key_graySectionText");
        themeDescriptionArr[27] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{GraySectionCell.class}, null, null, null, "graySection");
        themeDescriptionArr[28] = new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        themeDescriptionArr[29] = new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"valueTextView"}, null, null, null, "windowBackgroundWhiteGrayText2");
        themeDescriptionArr[30] = new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, null, null, null, "switchTrack");
        themeDescriptionArr[31] = new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, null, null, null, "switchTrackChecked");
        themeDescriptionArr[32] = new ThemeDescription(this.listView, 0, new Class[]{TextColorCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        themeDescriptionArr[33] = new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        themeDescriptionArr[34] = new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, null, null, null, "windowBackgroundWhiteValueText");
        themeDescriptionArr[35] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, "windowBackgroundGrayShadow");
        view = this.listView;
        View view2 = view;
        themeDescriptionArr[36] = new ThemeDescription(view2, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlueButton");
        view = this.listView;
        view2 = view;
        themeDescriptionArr[37] = new ThemeDescription(view2, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteRedText5");
        view = this.listView;
        view2 = view;
        themeDescriptionArr[38] = new ThemeDescription(view2, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"imageView"}, null, null, null, "windowBackgroundWhiteBlueIcon");
        return themeDescriptionArr;
    }

    public /* synthetic */ void lambda$getThemeDescriptions$12$NotificationsCustomSettingsActivity() {
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
