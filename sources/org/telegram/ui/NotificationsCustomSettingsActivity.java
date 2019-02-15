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
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationsController;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
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
import org.telegram.ui.Components.AlertsCreator;
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
    private int rowCount = 0;
    private SearchAdapter searchListViewAdapter;
    private boolean searchWas;
    private boolean searching;

    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(ViewHolder holder) {
            int type = holder.getItemViewType();
            return (type == 0 || type == 4) ? false : true;
        }

        public int getItemCount() {
            return NotificationsCustomSettingsActivity.this.rowCount;
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new HeaderCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    break;
                case 1:
                    view = new TextCheckCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    break;
                case 2:
                    view = new UserCell(this.mContext, 6, 0, false);
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    break;
                case 3:
                    view = new TextColorCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    break;
                case 4:
                    view = new ShadowSectionCell(this.mContext);
                    break;
                case 5:
                    view = new TextSettingsCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    break;
                case 6:
                    view = new NotificationsCheckCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    break;
                default:
                    view = new TextCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    break;
            }
            return new Holder(view);
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            SharedPreferences preferences;
            boolean enabled;
            switch (holder.getItemViewType()) {
                case 0:
                    HeaderCell headerCell = holder.itemView;
                    if (position == NotificationsCustomSettingsActivity.this.messageSectionRow) {
                        headerCell.setText(LocaleController.getString("SETTINGS", R.string.SETTINGS));
                        return;
                    }
                    return;
                case 1:
                    TextCheckCell checkCell = holder.itemView;
                    preferences = MessagesController.getNotificationsSettings(NotificationsCustomSettingsActivity.this.currentAccount);
                    if (position == NotificationsCustomSettingsActivity.this.previewRow) {
                        if (NotificationsCustomSettingsActivity.this.currentType == 1) {
                            enabled = preferences.getBoolean("EnablePreviewAll", true);
                        } else if (NotificationsCustomSettingsActivity.this.currentType == 0) {
                            enabled = preferences.getBoolean("EnablePreviewGroup", true);
                        } else {
                            enabled = preferences.getBoolean("EnablePreviewChannel", true);
                        }
                        checkCell.setTextAndCheck(LocaleController.getString("MessagePreview", R.string.MessagePreview), enabled, true);
                        return;
                    }
                    return;
                case 2:
                    holder.itemView.setException((NotificationException) NotificationsCustomSettingsActivity.this.exceptions.get(position - NotificationsCustomSettingsActivity.this.exceptionsStartRow), null, position != NotificationsCustomSettingsActivity.this.exceptionsEndRow + -1);
                    return;
                case 3:
                    int color;
                    TextColorCell textColorCell = (TextColorCell) holder.itemView;
                    preferences = MessagesController.getNotificationsSettings(NotificationsCustomSettingsActivity.this.currentAccount);
                    if (NotificationsCustomSettingsActivity.this.currentType == 1) {
                        color = preferences.getInt("MessagesLed", -16776961);
                    } else if (NotificationsCustomSettingsActivity.this.currentType == 0) {
                        color = preferences.getInt("GroupLed", -16776961);
                    } else {
                        color = preferences.getInt("ChannelLed", -16776961);
                    }
                    for (int a = 0; a < 9; a++) {
                        if (TextColorCell.colorsToSave[a] == color) {
                            color = TextColorCell.colors[a];
                            textColorCell.setTextAndColor(LocaleController.getString("LedColor", R.string.LedColor), color, true);
                            return;
                        }
                    }
                    textColorCell.setTextAndColor(LocaleController.getString("LedColor", R.string.LedColor), color, true);
                    return;
                case 4:
                    if (position == NotificationsCustomSettingsActivity.this.exceptionsSection2Row || (position == NotificationsCustomSettingsActivity.this.groupSection2Row && NotificationsCustomSettingsActivity.this.exceptionsSection2Row == -1)) {
                        holder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider_bottom, "windowBackgroundGrayShadow"));
                        return;
                    } else {
                        holder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider, "windowBackgroundGrayShadow"));
                        return;
                    }
                case 5:
                    TextSettingsCell textCell = (TextSettingsCell) holder.itemView;
                    preferences = MessagesController.getNotificationsSettings(NotificationsCustomSettingsActivity.this.currentAccount);
                    String value;
                    int value2;
                    if (position == NotificationsCustomSettingsActivity.this.messageSoundRow) {
                        if (NotificationsCustomSettingsActivity.this.currentType == 1) {
                            value = preferences.getString("GlobalSound", LocaleController.getString("SoundDefault", R.string.SoundDefault));
                        } else if (NotificationsCustomSettingsActivity.this.currentType == 0) {
                            value = preferences.getString("GroupSound", LocaleController.getString("SoundDefault", R.string.SoundDefault));
                        } else {
                            value = preferences.getString("ChannelSound", LocaleController.getString("SoundDefault", R.string.SoundDefault));
                        }
                        if (value.equals("NoSound")) {
                            value = LocaleController.getString("NoSound", R.string.NoSound);
                        }
                        textCell.setTextAndValue(LocaleController.getString("Sound", R.string.Sound), value, true);
                        return;
                    } else if (position == NotificationsCustomSettingsActivity.this.messageVibrateRow) {
                        if (NotificationsCustomSettingsActivity.this.currentType == 1) {
                            value2 = preferences.getInt("vibrate_messages", 0);
                        } else if (NotificationsCustomSettingsActivity.this.currentType == 0) {
                            value2 = preferences.getInt("vibrate_group", 0);
                        } else {
                            value2 = preferences.getInt("vibrate_channel", 0);
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
                    } else if (position == NotificationsCustomSettingsActivity.this.messagePriorityRow) {
                        if (NotificationsCustomSettingsActivity.this.currentType == 1) {
                            value2 = preferences.getInt("priority_messages", 1);
                        } else if (NotificationsCustomSettingsActivity.this.currentType == 0) {
                            value2 = preferences.getInt("priority_group", 1);
                        } else {
                            value2 = preferences.getInt("priority_channel", 1);
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
                    } else if (position == NotificationsCustomSettingsActivity.this.messagePopupNotificationRow) {
                        int option;
                        if (NotificationsCustomSettingsActivity.this.currentType == 1) {
                            option = preferences.getInt("popupAll", 0);
                        } else if (NotificationsCustomSettingsActivity.this.currentType == 0) {
                            option = preferences.getInt("popupGroup", 0);
                        } else {
                            option = preferences.getInt("popupChannel", 0);
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
                case 6:
                    String text;
                    int offUntil;
                    int iconType;
                    NotificationsCheckCell checkCell2 = holder.itemView;
                    checkCell2.setDrawLine(false);
                    StringBuilder builder = new StringBuilder();
                    preferences = MessagesController.getNotificationsSettings(NotificationsCustomSettingsActivity.this.currentAccount);
                    if (NotificationsCustomSettingsActivity.this.currentType == 1) {
                        text = LocaleController.getString("NotificationsForPrivateChats", R.string.NotificationsForPrivateChats);
                        offUntil = preferences.getInt("EnableAll2", 0);
                    } else if (NotificationsCustomSettingsActivity.this.currentType == 0) {
                        text = LocaleController.getString("NotificationsForGroups", R.string.NotificationsForGroups);
                        offUntil = preferences.getInt("EnableGroup2", 0);
                    } else {
                        text = LocaleController.getString("NotificationsForChannels", R.string.NotificationsForChannels);
                        offUntil = preferences.getInt("EnableChannel2", 0);
                    }
                    int currentTime = ConnectionsManager.getInstance(NotificationsCustomSettingsActivity.this.currentAccount).getCurrentTime();
                    enabled = offUntil < currentTime;
                    if (enabled) {
                        builder.append(LocaleController.getString("NotificationsOn", R.string.NotificationsOn));
                        iconType = 0;
                    } else if (offUntil - 31536000 >= currentTime) {
                        builder.append(LocaleController.getString("NotificationsOff", R.string.NotificationsOff));
                        iconType = 0;
                    } else {
                        builder.append(LocaleController.formatString("NotificationsOffUntil", R.string.NotificationsOffUntil, LocaleController.stringForMessageListDate((long) offUntil)));
                        iconType = 2;
                    }
                    checkCell2.setTextAndValueAndCheck(text, builder, enabled, iconType, false);
                    return;
                case 7:
                    TextCell textCell2 = (TextCell) holder.itemView;
                    textCell2.setColors("windowBackgroundWhiteBlueIcon", "windowBackgroundWhiteBlueButton");
                    if (position == NotificationsCustomSettingsActivity.this.exceptionsAddRow) {
                        textCell2.setTextAndIcon(LocaleController.getString("NotificationsAddAnException", R.string.NotificationsAddAnException), R.drawable.actions_addmember2, NotificationsCustomSettingsActivity.this.exceptionsStartRow != -1);
                        return;
                    }
                    return;
                default:
                    return;
            }
        }

        public void onViewAttachedToWindow(ViewHolder holder) {
            if (NotificationsCustomSettingsActivity.this.exceptions.isEmpty()) {
                boolean enabled = NotificationsController.getInstance(NotificationsCustomSettingsActivity.this.currentAccount).isGlobalNotificationsEnabled(NotificationsCustomSettingsActivity.this.currentType);
                switch (holder.getItemViewType()) {
                    case 0:
                        HeaderCell headerCell = holder.itemView;
                        if (holder.getAdapterPosition() == NotificationsCustomSettingsActivity.this.messageSectionRow) {
                            headerCell.setEnabled(enabled, null);
                            return;
                        } else {
                            headerCell.setEnabled(true, null);
                            return;
                        }
                    case 1:
                        holder.itemView.setEnabled(enabled, null);
                        return;
                    case 3:
                        holder.itemView.setEnabled(enabled, null);
                        return;
                    case 5:
                        holder.itemView.setEnabled(enabled, null);
                        return;
                    default:
                        return;
                }
            }
        }

        public int getItemViewType(int position) {
            if (position == NotificationsCustomSettingsActivity.this.messageSectionRow) {
                return 0;
            }
            if (position == NotificationsCustomSettingsActivity.this.previewRow) {
                return 1;
            }
            if (position >= NotificationsCustomSettingsActivity.this.exceptionsStartRow && position < NotificationsCustomSettingsActivity.this.exceptionsEndRow) {
                return 2;
            }
            if (position == NotificationsCustomSettingsActivity.this.messageLedRow) {
                return 3;
            }
            if (position == NotificationsCustomSettingsActivity.this.groupSection2Row || position == NotificationsCustomSettingsActivity.this.alertSection2Row || position == NotificationsCustomSettingsActivity.this.exceptionsSection2Row) {
                return 4;
            }
            if (position == NotificationsCustomSettingsActivity.this.alertRow) {
                return 6;
            }
            if (position == NotificationsCustomSettingsActivity.this.exceptionsAddRow) {
                return 7;
            }
            return 5;
        }
    }

    private class SearchAdapter extends SelectionAdapter {
        private Context mContext;
        private ArrayList<NotificationException> searchResult = new ArrayList();
        private ArrayList<CharSequence> searchResultNames = new ArrayList();
        private Timer searchTimer;

        public SearchAdapter(Context context) {
            this.mContext = context;
        }

        public void searchDialogs(final String query) {
            try {
                if (this.searchTimer != null) {
                    this.searchTimer.cancel();
                }
            } catch (Throwable e) {
                FileLog.e(e);
            }
            if (query == null) {
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
                    } catch (Throwable e) {
                        FileLog.e(e);
                    }
                    SearchAdapter.this.processSearch(query);
                }
            }, 200, 300);
        }

        private void processSearch(String query) {
            AndroidUtilities.runOnUIThread(new NotificationsCustomSettingsActivity$SearchAdapter$$Lambda$0(this, query));
        }

        final /* synthetic */ void lambda$processSearch$1$NotificationsCustomSettingsActivity$SearchAdapter(String query) {
            Utilities.searchQueue.postRunnable(new NotificationsCustomSettingsActivity$SearchAdapter$$Lambda$2(this, query, new ArrayList(NotificationsCustomSettingsActivity.this.exceptions)));
        }

        /* JADX WARNING: Removed duplicated region for block: B:33:0x010a  */
        /* JADX WARNING: Removed duplicated region for block: B:33:0x010a  */
        final /* synthetic */ void lambda$null$0$NotificationsCustomSettingsActivity$SearchAdapter(java.lang.String r27, java.util.ArrayList r28) {
            /*
            r26 = this;
            r22 = r27.trim();
            r18 = r22.toLowerCase();
            r22 = r18.length();
            if (r22 != 0) goto L_0x0022;
        L_0x000e:
            r22 = new java.util.ArrayList;
            r22.<init>();
            r23 = new java.util.ArrayList;
            r23.<init>();
            r0 = r26;
            r1 = r22;
            r2 = r23;
            r0.updateSearchResults(r1, r2);
        L_0x0021:
            return;
        L_0x0022:
            r22 = org.telegram.messenger.LocaleController.getInstance();
            r0 = r22;
            r1 = r18;
            r19 = r0.getTranslitString(r1);
            r22 = r18.equals(r19);
            if (r22 != 0) goto L_0x003a;
        L_0x0034:
            r22 = r19.length();
            if (r22 != 0) goto L_0x003c;
        L_0x003a:
            r19 = 0;
        L_0x003c:
            if (r19 == 0) goto L_0x00aa;
        L_0x003e:
            r22 = 1;
        L_0x0040:
            r22 = r22 + 1;
            r0 = r22;
            r0 = new java.lang.String[r0];
            r17 = r0;
            r22 = 0;
            r17[r22] = r18;
            if (r19 == 0) goto L_0x0052;
        L_0x004e:
            r22 = 1;
            r17[r22] = r19;
        L_0x0052:
            r15 = new java.util.ArrayList;
            r15.<init>();
            r16 = new java.util.ArrayList;
            r16.<init>();
            r22 = 2;
            r0 = r22;
            r12 = new java.lang.String[r0];
            r4 = 0;
        L_0x0063:
            r22 = r28.size();
            r0 = r22;
            if (r4 >= r0) goto L_0x026e;
        L_0x006b:
            r0 = r28;
            r8 = r0.get(r4);
            r8 = (org.telegram.ui.NotificationsSettingsActivity.NotificationException) r8;
            r0 = r8.did;
            r22 = r0;
            r0 = r22;
            r11 = (int) r0;
            r0 = r8.did;
            r22 = r0;
            r24 = 32;
            r22 = r22 >> r24;
            r0 = r22;
            r10 = (int) r0;
            if (r11 == 0) goto L_0x01c4;
        L_0x0087:
            if (r11 <= 0) goto L_0x0185;
        L_0x0089:
            r0 = r26;
            r0 = org.telegram.ui.NotificationsCustomSettingsActivity.this;
            r22 = r0;
            r22 = r22.currentAccount;
            r22 = org.telegram.messenger.MessagesController.getInstance(r22);
            r23 = java.lang.Integer.valueOf(r11);
            r21 = r22.getUser(r23);
            r0 = r21;
            r0 = r0.deleted;
            r22 = r0;
            if (r22 == 0) goto L_0x00ad;
        L_0x00a7:
            r4 = r4 + 1;
            goto L_0x0063;
        L_0x00aa:
            r22 = 0;
            goto L_0x0040;
        L_0x00ad:
            if (r21 == 0) goto L_0x00cd;
        L_0x00af:
            r22 = 0;
            r0 = r21;
            r0 = r0.first_name;
            r23 = r0;
            r0 = r21;
            r0 = r0.last_name;
            r24 = r0;
            r23 = org.telegram.messenger.ContactsController.formatName(r23, r24);
            r12[r22] = r23;
            r22 = 1;
            r0 = r21;
            r0 = r0.username;
            r23 = r0;
            r12[r22] = r23;
        L_0x00cd:
            r22 = 0;
            r13 = r12[r22];
            r22 = 0;
            r23 = 0;
            r23 = r12[r23];
            r23 = r23.toLowerCase();
            r12[r22] = r23;
            r22 = org.telegram.messenger.LocaleController.getInstance();
            r23 = 0;
            r23 = r12[r23];
            r20 = r22.getTranslitString(r23);
            r22 = 0;
            r22 = r12[r22];
            if (r22 == 0) goto L_0x00ff;
        L_0x00ef:
            r22 = 0;
            r22 = r12[r22];
            r0 = r22;
            r1 = r20;
            r22 = r0.equals(r1);
            if (r22 == 0) goto L_0x00ff;
        L_0x00fd:
            r20 = 0;
        L_0x00ff:
            r9 = 0;
            r5 = 0;
        L_0x0101:
            r0 = r17;
            r0 = r0.length;
            r22 = r0;
            r0 = r22;
            if (r5 >= r0) goto L_0x00a7;
        L_0x010a:
            r14 = r17[r5];
            r22 = 0;
            r22 = r12[r22];
            if (r22 == 0) goto L_0x013e;
        L_0x0112:
            r22 = 0;
            r22 = r12[r22];
            r0 = r22;
            r22 = r0.startsWith(r14);
            if (r22 != 0) goto L_0x0168;
        L_0x011e:
            r22 = 0;
            r22 = r12[r22];
            r23 = new java.lang.StringBuilder;
            r23.<init>();
            r24 = " ";
            r23 = r23.append(r24);
            r0 = r23;
            r23 = r0.append(r14);
            r23 = r23.toString();
            r22 = r22.contains(r23);
            if (r22 != 0) goto L_0x0168;
        L_0x013e:
            if (r20 == 0) goto L_0x0218;
        L_0x0140:
            r0 = r20;
            r22 = r0.startsWith(r14);
            if (r22 != 0) goto L_0x0168;
        L_0x0148:
            r22 = new java.lang.StringBuilder;
            r22.<init>();
            r23 = " ";
            r22 = r22.append(r23);
            r0 = r22;
            r22 = r0.append(r14);
            r22 = r22.toString();
            r0 = r20;
            r1 = r22;
            r22 = r0.contains(r1);
            if (r22 == 0) goto L_0x0218;
        L_0x0168:
            r9 = 1;
        L_0x0169:
            if (r9 == 0) goto L_0x026a;
        L_0x016b:
            r22 = 1;
            r0 = r22;
            if (r9 != r0) goto L_0x022d;
        L_0x0171:
            r22 = 0;
            r0 = r22;
            r22 = org.telegram.messenger.AndroidUtilities.generateSearchName(r13, r0, r14);
            r0 = r16;
            r1 = r22;
            r0.add(r1);
        L_0x0180:
            r15.add(r8);
            goto L_0x00a7;
        L_0x0185:
            r0 = r26;
            r0 = org.telegram.ui.NotificationsCustomSettingsActivity.this;
            r22 = r0;
            r22 = r22.currentAccount;
            r22 = org.telegram.messenger.MessagesController.getInstance(r22);
            r0 = -r11;
            r23 = r0;
            r23 = java.lang.Integer.valueOf(r23);
            r6 = r22.getChat(r23);
            if (r6 == 0) goto L_0x00cd;
        L_0x01a0:
            r0 = r6.left;
            r22 = r0;
            if (r22 != 0) goto L_0x00a7;
        L_0x01a6:
            r0 = r6.kicked;
            r22 = r0;
            if (r22 != 0) goto L_0x00a7;
        L_0x01ac:
            r0 = r6.migrated_to;
            r22 = r0;
            if (r22 != 0) goto L_0x00a7;
        L_0x01b2:
            r22 = 0;
            r0 = r6.title;
            r23 = r0;
            r12[r22] = r23;
            r22 = 1;
            r0 = r6.username;
            r23 = r0;
            r12[r22] = r23;
            goto L_0x00cd;
        L_0x01c4:
            r0 = r26;
            r0 = org.telegram.ui.NotificationsCustomSettingsActivity.this;
            r22 = r0;
            r22 = r22.currentAccount;
            r22 = org.telegram.messenger.MessagesController.getInstance(r22);
            r23 = java.lang.Integer.valueOf(r10);
            r7 = r22.getEncryptedChat(r23);
            if (r7 == 0) goto L_0x00cd;
        L_0x01dc:
            r0 = r26;
            r0 = org.telegram.ui.NotificationsCustomSettingsActivity.this;
            r22 = r0;
            r22 = r22.currentAccount;
            r22 = org.telegram.messenger.MessagesController.getInstance(r22);
            r0 = r7.user_id;
            r23 = r0;
            r23 = java.lang.Integer.valueOf(r23);
            r21 = r22.getUser(r23);
            if (r21 == 0) goto L_0x00cd;
        L_0x01f8:
            r22 = 0;
            r0 = r21;
            r0 = r0.first_name;
            r23 = r0;
            r0 = r21;
            r0 = r0.last_name;
            r24 = r0;
            r23 = org.telegram.messenger.ContactsController.formatName(r23, r24);
            r12[r22] = r23;
            r22 = 1;
            r0 = r21;
            r0 = r0.username;
            r23 = r0;
            r12[r22] = r23;
            goto L_0x00cd;
        L_0x0218:
            r22 = 1;
            r22 = r12[r22];
            if (r22 == 0) goto L_0x0169;
        L_0x021e:
            r22 = 1;
            r22 = r12[r22];
            r0 = r22;
            r22 = r0.startsWith(r14);
            if (r22 == 0) goto L_0x0169;
        L_0x022a:
            r9 = 2;
            goto L_0x0169;
        L_0x022d:
            r22 = new java.lang.StringBuilder;
            r22.<init>();
            r23 = "@";
            r22 = r22.append(r23);
            r23 = 1;
            r23 = r12[r23];
            r22 = r22.append(r23);
            r22 = r22.toString();
            r23 = 0;
            r24 = new java.lang.StringBuilder;
            r24.<init>();
            r25 = "@";
            r24 = r24.append(r25);
            r0 = r24;
            r24 = r0.append(r14);
            r24 = r24.toString();
            r22 = org.telegram.messenger.AndroidUtilities.generateSearchName(r22, r23, r24);
            r0 = r16;
            r1 = r22;
            r0.add(r1);
            goto L_0x0180;
        L_0x026a:
            r5 = r5 + 1;
            goto L_0x0101;
        L_0x026e:
            r0 = r26;
            r1 = r16;
            r0.updateSearchResults(r15, r1);
            goto L_0x0021;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.NotificationsCustomSettingsActivity.SearchAdapter.lambda$null$0$NotificationsCustomSettingsActivity$SearchAdapter(java.lang.String, java.util.ArrayList):void");
        }

        private void updateSearchResults(ArrayList<NotificationException> users, ArrayList<CharSequence> names) {
            AndroidUtilities.runOnUIThread(new NotificationsCustomSettingsActivity$SearchAdapter$$Lambda$1(this, users, names));
        }

        final /* synthetic */ void lambda$updateSearchResults$2$NotificationsCustomSettingsActivity$SearchAdapter(ArrayList users, ArrayList names) {
            this.searchResult = users;
            this.searchResultNames = names;
            notifyDataSetChanged();
        }

        public boolean isEnabled(ViewHolder holder) {
            return true;
        }

        public int getItemCount() {
            return this.searchResult.size();
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = new UserCell(this.mContext, 9, 0, false);
            view.setPadding(AndroidUtilities.dp(6.0f), 0, AndroidUtilities.dp(6.0f), 0);
            view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            return new Holder(view);
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.itemView.setException((NotificationException) this.searchResult.get(position), (CharSequence) this.searchResultNames.get(position), position != this.searchResult.size() + -1);
        }

        public int getItemViewType(int i) {
            return 0;
        }
    }

    public NotificationsCustomSettingsActivity(int type, ArrayList<NotificationException> notificationExceptions) {
        this.currentType = type;
        this.exceptions = notificationExceptions;
    }

    public boolean onFragmentCreate() {
        updateRows();
        return super.onFragmentCreate();
    }

    public View createView(Context context) {
        this.searching = false;
        this.searchWas = false;
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        if (this.currentType == -1) {
            this.actionBar.setTitle(LocaleController.getString("NotificationsExceptions", R.string.NotificationsExceptions));
        } else {
            this.actionBar.setTitle(LocaleController.getString("Notifications", R.string.Notifications));
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    NotificationsCustomSettingsActivity.this.lambda$createView$1$PhotoAlbumPickerActivity();
                }
            }
        });
        if (!(this.exceptions == null || this.exceptions.isEmpty())) {
            this.actionBar.createMenu().addItem(0, (int) R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItemSearchListener() {
                public void onSearchExpand() {
                    NotificationsCustomSettingsActivity.this.searching = true;
                    NotificationsCustomSettingsActivity.this.emptyView.setShowAtCenter(true);
                }

                public void onSearchCollapse() {
                    NotificationsCustomSettingsActivity.this.searchListViewAdapter.searchDialogs(null);
                    NotificationsCustomSettingsActivity.this.searching = false;
                    NotificationsCustomSettingsActivity.this.searchWas = false;
                    NotificationsCustomSettingsActivity.this.emptyView.setText(LocaleController.getString("NoExceptions", R.string.NoExceptions));
                    NotificationsCustomSettingsActivity.this.listView.setAdapter(NotificationsCustomSettingsActivity.this.adapter);
                    NotificationsCustomSettingsActivity.this.adapter.notifyDataSetChanged();
                    NotificationsCustomSettingsActivity.this.listView.setFastScrollVisible(true);
                    NotificationsCustomSettingsActivity.this.listView.setVerticalScrollBarEnabled(false);
                    NotificationsCustomSettingsActivity.this.emptyView.setShowAtCenter(false);
                }

                public void onTextChanged(EditText editText) {
                    if (NotificationsCustomSettingsActivity.this.searchListViewAdapter != null) {
                        String text = editText.getText().toString();
                        if (text.length() != 0) {
                            NotificationsCustomSettingsActivity.this.searchWas = true;
                            if (NotificationsCustomSettingsActivity.this.listView != null) {
                                NotificationsCustomSettingsActivity.this.emptyView.setText(LocaleController.getString("NoResult", R.string.NoResult));
                                NotificationsCustomSettingsActivity.this.listView.setAdapter(NotificationsCustomSettingsActivity.this.searchListViewAdapter);
                                NotificationsCustomSettingsActivity.this.searchListViewAdapter.notifyDataSetChanged();
                                NotificationsCustomSettingsActivity.this.listView.setFastScrollVisible(false);
                                NotificationsCustomSettingsActivity.this.listView.setVerticalScrollBarEnabled(true);
                            }
                        }
                        NotificationsCustomSettingsActivity.this.searchListViewAdapter.searchDialogs(text);
                    }
                }
            }).setSearchFieldHint(LocaleController.getString("Search", R.string.Search));
        }
        this.searchListViewAdapter = new SearchAdapter(context);
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = this.fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        this.emptyView = new EmptyTextProgressView(context);
        this.emptyView.setTextSize(18);
        this.emptyView.setText(LocaleController.getString("NoExceptions", R.string.NoExceptions));
        this.emptyView.showTextView();
        frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView = new RecyclerListView(context);
        this.listView.setEmptyView(this.emptyView);
        this.listView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        this.listView.setVerticalScrollBarEnabled(false);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        RecyclerListView recyclerListView = this.listView;
        Adapter listAdapter = new ListAdapter(context);
        this.adapter = listAdapter;
        recyclerListView.setAdapter(listAdapter);
        this.listView.setOnItemClickListener(new NotificationsCustomSettingsActivity$$Lambda$0(this));
        this.listView.setOnScrollListener(new OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == 1 && NotificationsCustomSettingsActivity.this.searching && NotificationsCustomSettingsActivity.this.searchWas) {
                    AndroidUtilities.hideKeyboard(NotificationsCustomSettingsActivity.this.getParentActivity().getCurrentFocus());
                }
            }

            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        return this.fragmentView;
    }

    final /* synthetic */ void lambda$createView$8$NotificationsCustomSettingsActivity(View view, int position, float x, float y) {
        boolean enabled = false;
        if (getParentActivity() != null) {
            if (this.listView.getAdapter() == this.searchListViewAdapter || (position >= this.exceptionsStartRow && position < this.exceptionsEndRow)) {
                ArrayList<NotificationException> arrayList;
                int index = position;
                if (this.listView.getAdapter() == this.searchListViewAdapter) {
                    arrayList = this.searchListViewAdapter.searchResult;
                } else {
                    arrayList = this.exceptions;
                    index -= this.exceptionsStartRow;
                }
                if (index >= 0 && index < arrayList.size()) {
                    NotificationException exception = (NotificationException) arrayList.get(index);
                    AlertsCreator.showCustomNotificationsDialog(this, exception.did, -1, null, this.currentAccount, null, new NotificationsCustomSettingsActivity$$Lambda$2(this, arrayList, exception, position));
                    return;
                }
                return;
            }
            SharedPreferences preferences;
            if (position == this.exceptionsAddRow) {
                Bundle args = new Bundle();
                args.putBoolean("onlySelect", true);
                if (this.currentType == 0) {
                    args.putInt("dialogsType", 2);
                } else if (this.currentType == 2) {
                    args.putInt("dialogsType", 5);
                } else {
                    args.putInt("dialogsType", 4);
                }
                DialogsActivity activity = new DialogsActivity(args);
                activity.setDelegate(new NotificationsCustomSettingsActivity$$Lambda$3(this));
                presentFragment(activity);
            } else if (position == this.alertRow) {
                enabled = NotificationsController.getInstance(this.currentAccount).isGlobalNotificationsEnabled(this.currentType);
                NotificationsCheckCell checkCell = (NotificationsCheckCell) view;
                ViewHolder holder = this.listView.findViewHolderForAdapterPosition(position);
                if (enabled) {
                    AlertsCreator.showCustomNotificationsDialog(this, 0, this.currentType, this.exceptions, this.currentAccount, new NotificationsCustomSettingsActivity$$Lambda$4(this, checkCell, holder, position));
                } else {
                    NotificationsController.getInstance(this.currentAccount).setGlobalNotificationsEnabled(this.currentType, 0);
                    checkCell.setChecked(!enabled);
                    if (holder != null) {
                        this.adapter.onBindViewHolder(holder, position);
                    }
                    checkRowsEnabled();
                }
            } else if (position == this.previewRow) {
                if (view.isEnabled()) {
                    preferences = MessagesController.getNotificationsSettings(this.currentAccount);
                    Editor editor = preferences.edit();
                    if (this.currentType == 1) {
                        enabled = preferences.getBoolean("EnablePreviewAll", true);
                        editor.putBoolean("EnablePreviewAll", !enabled);
                    } else if (this.currentType == 0) {
                        enabled = preferences.getBoolean("EnablePreviewGroup", true);
                        editor.putBoolean("EnablePreviewGroup", !enabled);
                    } else {
                        enabled = preferences.getBoolean("EnablePreviewChannel", true);
                        editor.putBoolean("EnablePreviewChannel", !enabled);
                    }
                    editor.commit();
                    NotificationsController.getInstance(this.currentAccount).updateServerNotificationsSettings(this.currentType);
                } else {
                    return;
                }
            } else if (position == this.messageSoundRow) {
                if (view.isEnabled()) {
                    try {
                        String path;
                        preferences = MessagesController.getNotificationsSettings(this.currentAccount);
                        Intent intent = new Intent("android.intent.action.RINGTONE_PICKER");
                        intent.putExtra("android.intent.extra.ringtone.TYPE", 2);
                        intent.putExtra("android.intent.extra.ringtone.SHOW_DEFAULT", true);
                        intent.putExtra("android.intent.extra.ringtone.DEFAULT_URI", RingtoneManager.getDefaultUri(2));
                        Uri currentSound = null;
                        String defaultPath = null;
                        Uri defaultUri = System.DEFAULT_NOTIFICATION_URI;
                        if (defaultUri != null) {
                            defaultPath = defaultUri.getPath();
                        }
                        if (this.currentType == 1) {
                            path = preferences.getString("GlobalSoundPath", defaultPath);
                        } else if (this.currentType == 0) {
                            path = preferences.getString("GroupSoundPath", defaultPath);
                        } else {
                            path = preferences.getString("ChannelSoundPath", defaultPath);
                        }
                        if (path != null) {
                            if (!path.equals("NoSound")) {
                                currentSound = path.equals(defaultPath) ? defaultUri : Uri.parse(path);
                            }
                        }
                        intent.putExtra("android.intent.extra.ringtone.EXISTING_URI", currentSound);
                        startActivityForResult(intent, position);
                    } catch (Throwable e) {
                        FileLog.e(e);
                    }
                } else {
                    return;
                }
            } else if (position == this.messageLedRow) {
                if (view.isEnabled()) {
                    showDialog(AlertsCreator.createColorSelectDialog(getParentActivity(), 0, this.currentType, new NotificationsCustomSettingsActivity$$Lambda$5(this, position)));
                } else {
                    return;
                }
            } else if (position == this.messagePopupNotificationRow) {
                if (view.isEnabled()) {
                    showDialog(AlertsCreator.createPopupSelectDialog(getParentActivity(), this.currentType, new NotificationsCustomSettingsActivity$$Lambda$6(this, position)));
                } else {
                    return;
                }
            } else if (position == this.messageVibrateRow) {
                if (view.isEnabled()) {
                    String key;
                    if (this.currentType == 1) {
                        key = "vibrate_messages";
                    } else if (this.currentType == 0) {
                        key = "vibrate_group";
                    } else {
                        key = "vibrate_channel";
                    }
                    showDialog(AlertsCreator.createVibrationSelectDialog(getParentActivity(), 0, key, new NotificationsCustomSettingsActivity$$Lambda$7(this, position)));
                } else {
                    return;
                }
            } else if (position == this.messagePriorityRow) {
                if (view.isEnabled()) {
                    showDialog(AlertsCreator.createPrioritySelectDialog(getParentActivity(), 0, this.currentType, new NotificationsCustomSettingsActivity$$Lambda$8(this, position)));
                } else {
                    return;
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

    final /* synthetic */ void lambda$null$0$NotificationsCustomSettingsActivity(ArrayList arrayList, NotificationException exception, int position, int param) {
        if (param == 0) {
            if (arrayList != this.exceptions) {
                int idx = this.exceptions.indexOf(exception);
                if (idx >= 0) {
                    this.exceptions.remove(idx);
                }
            }
            arrayList.remove(exception);
            if (this.exceptionsAddRow != -1 && arrayList.isEmpty() && arrayList == this.exceptions) {
                this.listView.getAdapter().notifyItemChanged(this.exceptionsAddRow);
            }
            this.listView.getAdapter().notifyItemRemoved(position);
            updateRows();
            checkRowsEnabled();
            return;
        }
        SharedPreferences preferences = MessagesController.getNotificationsSettings(this.currentAccount);
        exception.hasCustom = preferences.getBoolean("custom_" + exception.did, false);
        exception.notify = preferences.getInt("notify2_" + exception.did, 0);
        if (exception.notify != 0) {
            int time = preferences.getInt("notifyuntil_" + exception.did, -1);
            if (time != -1) {
                exception.muteUntil = time;
            }
        }
        this.listView.getAdapter().notifyItemChanged(position);
    }

    final /* synthetic */ void lambda$null$2$NotificationsCustomSettingsActivity(DialogsActivity fragment, ArrayList dids, CharSequence message, boolean param) {
        Bundle args2 = new Bundle();
        args2.putLong("dialog_id", ((Long) dids.get(0)).longValue());
        args2.putBoolean("exception", true);
        ProfileNotificationsActivity profileNotificationsActivity = new ProfileNotificationsActivity(args2);
        profileNotificationsActivity.setDelegate(new NotificationsCustomSettingsActivity$$Lambda$9(this));
        presentFragment(profileNotificationsActivity, true);
    }

    final /* synthetic */ void lambda$null$1$NotificationsCustomSettingsActivity(NotificationException exception) {
        this.exceptions.add(0, exception);
        updateRows();
    }

    final /* synthetic */ void lambda$null$3$NotificationsCustomSettingsActivity(NotificationsCheckCell checkCell, ViewHolder holder, int position, int param) {
        int offUntil;
        int iconType;
        SharedPreferences preferences = MessagesController.getNotificationsSettings(this.currentAccount);
        if (this.currentType == 1) {
            offUntil = preferences.getInt("EnableAll2", 0);
        } else if (this.currentType == 0) {
            offUntil = preferences.getInt("EnableGroup2", 0);
        } else {
            offUntil = preferences.getInt("EnableChannel2", 0);
        }
        int currentTime = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
        if (offUntil < currentTime) {
            iconType = 0;
        } else if (offUntil - 31536000 >= currentTime) {
            iconType = 0;
        } else {
            iconType = 2;
        }
        checkCell.setChecked(NotificationsController.getInstance(this.currentAccount).isGlobalNotificationsEnabled(this.currentType), iconType);
        if (holder != null) {
            this.adapter.onBindViewHolder(holder, position);
        }
        checkRowsEnabled();
    }

    final /* synthetic */ void lambda$null$4$NotificationsCustomSettingsActivity(int position) {
        ViewHolder holder = this.listView.findViewHolderForAdapterPosition(position);
        if (holder != null) {
            this.adapter.onBindViewHolder(holder, position);
        }
    }

    final /* synthetic */ void lambda$null$5$NotificationsCustomSettingsActivity(int position) {
        ViewHolder holder = this.listView.findViewHolderForAdapterPosition(position);
        if (holder != null) {
            this.adapter.onBindViewHolder(holder, position);
        }
    }

    final /* synthetic */ void lambda$null$6$NotificationsCustomSettingsActivity(int position) {
        ViewHolder holder = this.listView.findViewHolderForAdapterPosition(position);
        if (holder != null) {
            this.adapter.onBindViewHolder(holder, position);
        }
    }

    final /* synthetic */ void lambda$null$7$NotificationsCustomSettingsActivity(int position) {
        ViewHolder holder = this.listView.findViewHolderForAdapterPosition(position);
        if (holder != null) {
            this.adapter.onBindViewHolder(holder, position);
        }
    }

    private void checkRowsEnabled() {
        if (this.exceptions.isEmpty()) {
            int count = this.listView.getChildCount();
            ArrayList<Animator> animators = new ArrayList();
            boolean enabled = NotificationsController.getInstance(this.currentAccount).isGlobalNotificationsEnabled(this.currentType);
            for (int a = 0; a < count; a++) {
                Holder holder = (Holder) this.listView.getChildViewHolder(this.listView.getChildAt(a));
                switch (holder.getItemViewType()) {
                    case 0:
                        HeaderCell headerCell = holder.itemView;
                        if (holder.getAdapterPosition() != this.messageSectionRow) {
                            break;
                        }
                        headerCell.setEnabled(enabled, animators);
                        break;
                    case 1:
                        holder.itemView.setEnabled(enabled, animators);
                        break;
                    case 3:
                        holder.itemView.setEnabled(enabled, animators);
                        break;
                    case 5:
                        holder.itemView.setEnabled(enabled, animators);
                        break;
                    default:
                        break;
                }
            }
            if (!animators.isEmpty()) {
                if (this.animatorSet != null) {
                    this.animatorSet.cancel();
                }
                this.animatorSet = new AnimatorSet();
                this.animatorSet.playTogether(animators);
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

    private void updateRows() {
        int i;
        this.rowCount = 0;
        if (this.currentType != -1) {
            i = this.rowCount;
            this.rowCount = i + 1;
            this.alertRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.alertSection2Row = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.messageSectionRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.previewRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.messageLedRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.messageVibrateRow = i;
            if (this.currentType == 2) {
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
        if (this.exceptions == null || this.exceptions.isEmpty()) {
            this.exceptionsStartRow = -1;
            this.exceptionsEndRow = -1;
        } else {
            this.exceptionsStartRow = this.rowCount;
            this.rowCount += this.exceptions.size();
            this.exceptionsEndRow = this.rowCount;
        }
        if (this.currentType == -1 && (this.exceptions == null || this.exceptions.isEmpty())) {
            this.exceptionsSection2Row = -1;
            return;
        }
        i = this.rowCount;
        this.rowCount = i + 1;
        this.exceptionsSection2Row = i;
    }

    public void onActivityResultFragment(int requestCode, int resultCode, Intent data) {
        if (resultCode == -1) {
            Uri ringtone = (Uri) data.getParcelableExtra("android.intent.extra.ringtone.PICKED_URI");
            String name = null;
            if (ringtone != null) {
                Ringtone rng = RingtoneManager.getRingtone(getParentActivity(), ringtone);
                if (rng != null) {
                    if (ringtone.equals(System.DEFAULT_NOTIFICATION_URI)) {
                        name = LocaleController.getString("SoundDefault", R.string.SoundDefault);
                    } else {
                        name = rng.getTitle(getParentActivity());
                    }
                    rng.stop();
                }
            }
            Editor editor = MessagesController.getNotificationsSettings(this.currentAccount).edit();
            if (this.currentType == 1) {
                if (name == null || ringtone == null) {
                    editor.putString("GlobalSound", "NoSound");
                    editor.putString("GlobalSoundPath", "NoSound");
                } else {
                    editor.putString("GlobalSound", name);
                    editor.putString("GlobalSoundPath", ringtone.toString());
                }
            } else if (this.currentType == 0) {
                if (name == null || ringtone == null) {
                    editor.putString("GroupSound", "NoSound");
                    editor.putString("GroupSoundPath", "NoSound");
                } else {
                    editor.putString("GroupSound", name);
                    editor.putString("GroupSoundPath", ringtone.toString());
                }
            } else if (this.currentType == 2) {
                if (name == null || ringtone == null) {
                    editor.putString("ChannelSound", "NoSound");
                    editor.putString("ChannelSoundPath", "NoSound");
                } else {
                    editor.putString("ChannelSound", name);
                    editor.putString("ChannelSoundPath", ringtone.toString());
                }
            }
            editor.commit();
            NotificationsController.getInstance(this.currentAccount).updateServerNotificationsSettings(this.currentType);
            ViewHolder holder = this.listView.findViewHolderForAdapterPosition(requestCode);
            if (holder != null) {
                this.adapter.onBindViewHolder(holder, requestCode);
            }
        }
    }

    public void onResume() {
        super.onResume();
        if (this.adapter != null) {
            this.adapter.notifyDataSetChanged();
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescriptionDelegate cellDelegate = new NotificationsCustomSettingsActivity$$Lambda$1(this);
        r10 = new ThemeDescription[36];
        r10[0] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{HeaderCell.class, TextCheckCell.class, TextColorCell.class, TextSettingsCell.class, UserCell.class, NotificationsCheckCell.class}, null, null, null, "windowBackgroundWhite");
        r10[1] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundGray");
        r10[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
        r10[3] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault");
        r10[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
        r10[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
        r10[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
        r10[7] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
        r10[8] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, "divider");
        r10[9] = new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlueHeader");
        r10[10] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        r10[11] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"valueTextView"}, null, null, null, "windowBackgroundWhiteGrayText2");
        r10[12] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, "switchTrack");
        r10[13] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, "switchTrackChecked");
        r10[14] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"imageView"}, null, null, null, "windowBackgroundWhiteGrayIcon");
        r10[15] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"nameTextView"}, null, null, null, "windowBackgroundWhiteBlackText");
        r10[16] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusColor"}, null, null, cellDelegate, "windowBackgroundWhiteGrayText");
        r10[17] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusOnlineColor"}, null, null, cellDelegate, "windowBackgroundWhiteBlueText");
        r10[18] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, null, new Drawable[]{Theme.avatar_broadcastDrawable, Theme.avatar_savedDrawable}, null, "avatar_text");
        r10[19] = new ThemeDescription(null, 0, null, null, null, cellDelegate, "avatar_backgroundRed");
        r10[20] = new ThemeDescription(null, 0, null, null, null, cellDelegate, "avatar_backgroundOrange");
        r10[21] = new ThemeDescription(null, 0, null, null, null, cellDelegate, "avatar_backgroundViolet");
        r10[22] = new ThemeDescription(null, 0, null, null, null, cellDelegate, "avatar_backgroundGreen");
        r10[23] = new ThemeDescription(null, 0, null, null, null, cellDelegate, "avatar_backgroundCyan");
        r10[24] = new ThemeDescription(null, 0, null, null, null, cellDelegate, "avatar_backgroundBlue");
        r10[25] = new ThemeDescription(null, 0, null, null, null, cellDelegate, "avatar_backgroundPink");
        r10[26] = new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        r10[27] = new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"valueTextView"}, null, null, null, "windowBackgroundWhiteGrayText2");
        r10[28] = new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, null, null, null, "switchTrack");
        r10[29] = new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, null, null, null, "switchTrackChecked");
        r10[30] = new ThemeDescription(this.listView, 0, new Class[]{TextColorCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        r10[31] = new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        r10[32] = new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, null, null, null, "windowBackgroundWhiteValueText");
        r10[33] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, "windowBackgroundGrayShadow");
        r10[34] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlueButton");
        r10[35] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"imageView"}, null, null, null, "windowBackgroundWhiteBlueIcon");
        return r10;
    }

    final /* synthetic */ void lambda$getThemeDescriptions$9$NotificationsCustomSettingsActivity() {
        if (this.listView != null) {
            int count = this.listView.getChildCount();
            for (int a = 0; a < count; a++) {
                View child = this.listView.getChildAt(a);
                if (child instanceof UserCell) {
                    ((UserCell) child).update(0);
                }
            }
        }
    }
}
