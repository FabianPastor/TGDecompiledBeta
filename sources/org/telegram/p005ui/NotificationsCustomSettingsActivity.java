package org.telegram.p005ui;

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
import android.provider.Settings.System;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.CLASSNAMER;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationsController;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.p005ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener;
import org.telegram.p005ui.ActionBar.BaseFragment;
import org.telegram.p005ui.ActionBar.CLASSNAMEActionBar.ActionBarMenuOnItemClick;
import org.telegram.p005ui.ActionBar.Theme;
import org.telegram.p005ui.ActionBar.ThemeDescription;
import org.telegram.p005ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate;
import org.telegram.p005ui.Cells.HeaderCell;
import org.telegram.p005ui.Cells.NotificationsCheckCell;
import org.telegram.p005ui.Cells.ProfileSearchCell;
import org.telegram.p005ui.Cells.ShadowSectionCell;
import org.telegram.p005ui.Cells.TextCheckCell;
import org.telegram.p005ui.Cells.TextColorCell;
import org.telegram.p005ui.Cells.TextSettingsCell;
import org.telegram.p005ui.Components.AlertsCreator;
import org.telegram.p005ui.Components.EmptyTextProgressView;
import org.telegram.p005ui.Components.LayoutHelper;
import org.telegram.p005ui.Components.RecyclerListView;
import org.telegram.p005ui.Components.RecyclerListView.Holder;
import org.telegram.p005ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.p005ui.NotificationsSettingsActivity.NotificationException;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.User;

/* renamed from: org.telegram.ui.NotificationsCustomSettingsActivity */
public class NotificationsCustomSettingsActivity extends BaseFragment {
    private static final int search_button = 0;
    private ListAdapter adapter;
    private int alertRow;
    private int alertSection2Row;
    private AnimatorSet animatorSet;
    private int currentType;
    private EmptyTextProgressView emptyView;
    private ArrayList<NotificationException> exceptions;
    private int exceptionsEndRow;
    private int exceptionsSection2Row;
    private int exceptionsSectionRow;
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

    /* renamed from: org.telegram.ui.NotificationsCustomSettingsActivity$1 */
    class CLASSNAME extends ActionBarMenuOnItemClick {
        CLASSNAME() {
        }

        public void onItemClick(int id) {
            if (id == -1) {
                NotificationsCustomSettingsActivity.this.finishFragment();
            }
        }
    }

    /* renamed from: org.telegram.ui.NotificationsCustomSettingsActivity$2 */
    class CLASSNAME extends ActionBarMenuItemSearchListener {
        CLASSNAME() {
        }

        public void onSearchExpand() {
            NotificationsCustomSettingsActivity.this.searching = true;
            NotificationsCustomSettingsActivity.this.emptyView.setShowAtCenter(true);
        }

        public void onSearchCollapse() {
            NotificationsCustomSettingsActivity.this.searchListViewAdapter.searchDialogs(null);
            NotificationsCustomSettingsActivity.this.searching = false;
            NotificationsCustomSettingsActivity.this.searchWas = false;
            NotificationsCustomSettingsActivity.this.emptyView.setText(LocaleController.getString("NoExceptions", CLASSNAMER.string.NoExceptions));
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
                        NotificationsCustomSettingsActivity.this.emptyView.setText(LocaleController.getString("NoResult", CLASSNAMER.string.NoResult));
                        NotificationsCustomSettingsActivity.this.listView.setAdapter(NotificationsCustomSettingsActivity.this.searchListViewAdapter);
                        NotificationsCustomSettingsActivity.this.searchListViewAdapter.notifyDataSetChanged();
                        NotificationsCustomSettingsActivity.this.listView.setFastScrollVisible(false);
                        NotificationsCustomSettingsActivity.this.listView.setVerticalScrollBarEnabled(true);
                    }
                }
                NotificationsCustomSettingsActivity.this.searchListViewAdapter.searchDialogs(text);
            }
        }
    }

    /* renamed from: org.telegram.ui.NotificationsCustomSettingsActivity$3 */
    class CLASSNAME extends OnScrollListener {
        CLASSNAME() {
        }

        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            if (newState == 1 && NotificationsCustomSettingsActivity.this.searching && NotificationsCustomSettingsActivity.this.searchWas) {
                AndroidUtilities.hideKeyboard(NotificationsCustomSettingsActivity.this.getParentActivity().getCurrentFocus());
            }
        }

        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
        }
    }

    /* renamed from: org.telegram.ui.NotificationsCustomSettingsActivity$4 */
    class CLASSNAME extends AnimatorListenerAdapter {
        CLASSNAME() {
        }

        public void onAnimationEnd(Animator animator) {
            if (animator.equals(NotificationsCustomSettingsActivity.this.animatorSet)) {
                NotificationsCustomSettingsActivity.this.animatorSet = null;
            }
        }
    }

    /* renamed from: org.telegram.ui.NotificationsCustomSettingsActivity$ListAdapter */
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
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 1:
                    view = new TextCheckCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 2:
                    view = new ProfileSearchCell(this.mContext);
                    view.setPadding(AndroidUtilities.m9dp(6.0f), 0, AndroidUtilities.m9dp(6.0f), 0);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 3:
                    view = new TextColorCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 4:
                    view = new ShadowSectionCell(this.mContext);
                    break;
                case 5:
                    view = new TextSettingsCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                default:
                    view = new NotificationsCheckCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
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
                        headerCell.setText(LocaleController.getString("SETTINGS", CLASSNAMER.string.SETTINGS));
                        return;
                    } else if (position == NotificationsCustomSettingsActivity.this.exceptionsSectionRow) {
                        headerCell.setText(LocaleController.getString("NotificationsExceptions", CLASSNAMER.string.NotificationsExceptions));
                        return;
                    } else {
                        return;
                    }
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
                        checkCell.setTextAndCheck(LocaleController.getString("MessagePreview", CLASSNAMER.string.MessagePreview), enabled, true);
                        return;
                    }
                    return;
                case 2:
                    holder.itemView.setException((NotificationException) NotificationsCustomSettingsActivity.this.exceptions.get(position - NotificationsCustomSettingsActivity.this.exceptionsStartRow), null);
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
                            textColorCell.setTextAndColor(LocaleController.getString("LedColor", CLASSNAMER.string.LedColor), color, true);
                            return;
                        }
                    }
                    textColorCell.setTextAndColor(LocaleController.getString("LedColor", CLASSNAMER.string.LedColor), color, true);
                    return;
                case 4:
                    if (position == NotificationsCustomSettingsActivity.this.exceptionsSection2Row || (position == NotificationsCustomSettingsActivity.this.groupSection2Row && NotificationsCustomSettingsActivity.this.exceptionsSection2Row == -1)) {
                        holder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) CLASSNAMER.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else {
                        holder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) CLASSNAMER.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                        return;
                    }
                case 5:
                    TextSettingsCell textCell = (TextSettingsCell) holder.itemView;
                    preferences = MessagesController.getNotificationsSettings(NotificationsCustomSettingsActivity.this.currentAccount);
                    String value;
                    int value2;
                    if (position == NotificationsCustomSettingsActivity.this.messageSoundRow) {
                        if (NotificationsCustomSettingsActivity.this.currentType == 1) {
                            value = preferences.getString("GlobalSound", LocaleController.getString("SoundDefault", CLASSNAMER.string.SoundDefault));
                        } else if (NotificationsCustomSettingsActivity.this.currentType == 0) {
                            value = preferences.getString("GroupSound", LocaleController.getString("SoundDefault", CLASSNAMER.string.SoundDefault));
                        } else {
                            value = preferences.getString("ChannelSound", LocaleController.getString("SoundDefault", CLASSNAMER.string.SoundDefault));
                        }
                        if (value.equals("NoSound")) {
                            value = LocaleController.getString("NoSound", CLASSNAMER.string.NoSound);
                        }
                        textCell.setTextAndValue(LocaleController.getString("Sound", CLASSNAMER.string.Sound), value, true);
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
                    } else if (position == NotificationsCustomSettingsActivity.this.messagePriorityRow) {
                        if (NotificationsCustomSettingsActivity.this.currentType == 1) {
                            value2 = preferences.getInt("priority_messages", 1);
                        } else if (NotificationsCustomSettingsActivity.this.currentType == 0) {
                            value2 = preferences.getInt("priority_group", 1);
                        } else {
                            value2 = preferences.getInt("priority_channel", 1);
                        }
                        if (value2 == 0) {
                            textCell.setTextAndValue(LocaleController.getString("NotificationsImportance", CLASSNAMER.string.NotificationsImportance), LocaleController.getString("NotificationsPriorityHigh", CLASSNAMER.string.NotificationsPriorityHigh), true);
                            return;
                        } else if (value2 == 1 || value2 == 2) {
                            textCell.setTextAndValue(LocaleController.getString("NotificationsImportance", CLASSNAMER.string.NotificationsImportance), LocaleController.getString("NotificationsPriorityUrgent", CLASSNAMER.string.NotificationsPriorityUrgent), true);
                            return;
                        } else if (value2 == 4) {
                            textCell.setTextAndValue(LocaleController.getString("NotificationsImportance", CLASSNAMER.string.NotificationsImportance), LocaleController.getString("NotificationsPriorityLow", CLASSNAMER.string.NotificationsPriorityLow), true);
                            return;
                        } else if (value2 == 5) {
                            textCell.setTextAndValue(LocaleController.getString("NotificationsImportance", CLASSNAMER.string.NotificationsImportance), LocaleController.getString("NotificationsPriorityMedium", CLASSNAMER.string.NotificationsPriorityMedium), true);
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
                            value = LocaleController.getString("NoPopup", CLASSNAMER.string.NoPopup);
                        } else if (option == 1) {
                            value = LocaleController.getString("OnlyWhenScreenOn", CLASSNAMER.string.OnlyWhenScreenOn);
                        } else if (option == 2) {
                            value = LocaleController.getString("OnlyWhenScreenOff", CLASSNAMER.string.OnlyWhenScreenOff);
                        } else {
                            value = LocaleController.getString("AlwaysShowPopup", CLASSNAMER.string.AlwaysShowPopup);
                        }
                        textCell.setTextAndValue(LocaleController.getString("PopupNotification", CLASSNAMER.string.PopupNotification), value, true);
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
                        text = LocaleController.getString("NotificationsForPrivateChats", CLASSNAMER.string.NotificationsForPrivateChats);
                        offUntil = preferences.getInt("EnableAll2", 0);
                    } else if (NotificationsCustomSettingsActivity.this.currentType == 0) {
                        text = LocaleController.getString("NotificationsForGroups", CLASSNAMER.string.NotificationsForGroups);
                        offUntil = preferences.getInt("EnableGroup2", 0);
                    } else {
                        text = LocaleController.getString("NotificationsForChannels", CLASSNAMER.string.NotificationsForChannels);
                        offUntil = preferences.getInt("EnableChannel2", 0);
                    }
                    int currentTime = ConnectionsManager.getInstance(NotificationsCustomSettingsActivity.this.currentAccount).getCurrentTime();
                    enabled = offUntil < currentTime;
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
                    checkCell2.setTextAndValueAndCheck(text, builder, enabled, iconType, false);
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
            if (position == NotificationsCustomSettingsActivity.this.messageSectionRow || position == NotificationsCustomSettingsActivity.this.exceptionsSectionRow) {
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
            return 5;
        }
    }

    /* renamed from: org.telegram.ui.NotificationsCustomSettingsActivity$SearchAdapter */
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
                FileLog.m13e(e);
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
                        FileLog.m13e(e);
                    }
                    SearchAdapter.this.processSearch(query);
                }
            }, 200, 300);
        }

        private void processSearch(String query) {
            AndroidUtilities.runOnUIThread(new NotificationsCustomSettingsActivity$SearchAdapter$$Lambda$0(this, query));
        }

        /* renamed from: lambda$processSearch$1$NotificationsCustomSettingsActivity$SearchAdapter */
        final /* synthetic */ void mo18849x19ea3dd1(String query) {
            Utilities.searchQueue.postRunnable(new NotificationsCustomSettingsActivity$SearchAdapter$$Lambda$2(this, query, new ArrayList(NotificationsCustomSettingsActivity.this.exceptions)));
        }

        /* JADX WARNING: Removed duplicated region for block: B:33:0x010a  */
        /* JADX WARNING: Removed duplicated region for block: B:33:0x010a  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        final /* synthetic */ void lambda$null$0$NotificationsCustomSettingsActivity$SearchAdapter(String query, ArrayList contactsCopy) {
            String search1 = query.trim().toLowerCase();
            if (search1.length() == 0) {
                updateSearchResults(new ArrayList(), new ArrayList());
                return;
            }
            String search2 = LocaleController.getInstance().getTranslitString(search1);
            if (search1.equals(search2) || search2.length() == 0) {
                search2 = null;
            }
            String[] search = new String[((search2 != null ? 1 : 0) + 1)];
            search[0] = search1;
            if (search2 != null) {
                search[1] = search2;
            }
            ArrayList<NotificationException> resultArray = new ArrayList();
            ArrayList<CharSequence> resultArrayNames = new ArrayList();
            String[] names = new String[2];
            for (int a = 0; a < contactsCopy.size(); a++) {
                NotificationException exception = (NotificationException) contactsCopy.get(a);
                int lower_id = (int) exception.did;
                int high_id = (int) (exception.did >> 32);
                User user;
                String originalName;
                String tName;
                int found;
                int b;
                if (lower_id == 0) {
                    EncryptedChat encryptedChat = MessagesController.getInstance(NotificationsCustomSettingsActivity.this.currentAccount).getEncryptedChat(Integer.valueOf(high_id));
                    if (encryptedChat != null) {
                        user = MessagesController.getInstance(NotificationsCustomSettingsActivity.this.currentAccount).getUser(Integer.valueOf(encryptedChat.user_id));
                        if (user != null) {
                            names[0] = ContactsController.formatName(user.first_name, user.last_name);
                            names[1] = user.username;
                        }
                    }
                    originalName = names[0];
                    names[0] = names[0].toLowerCase();
                    tName = LocaleController.getInstance().getTranslitString(names[0]);
                    tName = null;
                    found = 0;
                    b = 0;
                    while (b < search.length) {
                    }
                } else if (lower_id > 0) {
                    user = MessagesController.getInstance(NotificationsCustomSettingsActivity.this.currentAccount).getUser(Integer.valueOf(lower_id));
                    if (!user.deleted) {
                        if (user != null) {
                            names[0] = ContactsController.formatName(user.first_name, user.last_name);
                            names[1] = user.username;
                        }
                        originalName = names[0];
                        names[0] = names[0].toLowerCase();
                        tName = LocaleController.getInstance().getTranslitString(names[0]);
                        if (names[0] != null && names[0].equals(tName)) {
                            tName = null;
                        }
                        found = 0;
                        b = 0;
                        while (b < search.length) {
                            String q = search[b];
                            if ((names[0] != null && (names[0].startsWith(q) || names[0].contains(" " + q))) || (tName != null && (tName.startsWith(q) || tName.contains(" " + q)))) {
                                found = 1;
                            } else if (names[1] != null && names[1].startsWith(q)) {
                                found = 2;
                            }
                            if (found != 0) {
                                if (found == 1) {
                                    resultArrayNames.add(AndroidUtilities.generateSearchName(originalName, null, q));
                                } else {
                                    resultArrayNames.add(AndroidUtilities.generateSearchName("@" + names[1], null, "@" + q));
                                }
                                resultArray.add(exception);
                            } else {
                                b++;
                            }
                        }
                    }
                } else {
                    Chat chat = MessagesController.getInstance(NotificationsCustomSettingsActivity.this.currentAccount).getChat(Integer.valueOf(-lower_id));
                    if (chat != null) {
                        if (!(chat.left || chat.kicked || chat.migrated_to != null)) {
                            names[0] = chat.title;
                            names[1] = chat.username;
                        }
                    }
                    originalName = names[0];
                    names[0] = names[0].toLowerCase();
                    tName = LocaleController.getInstance().getTranslitString(names[0]);
                    tName = null;
                    found = 0;
                    b = 0;
                    while (b < search.length) {
                    }
                }
            }
            updateSearchResults(resultArray, resultArrayNames);
        }

        private void updateSearchResults(ArrayList<NotificationException> users, ArrayList<CharSequence> names) {
            AndroidUtilities.runOnUIThread(new NotificationsCustomSettingsActivity$SearchAdapter$$Lambda$1(this, users, names));
        }

        /* renamed from: lambda$updateSearchResults$2$NotificationsCustomSettingsActivity$SearchAdapter */
        final /* synthetic */ void mo18850x264e3040(ArrayList users, ArrayList names) {
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
            View view = new ProfileSearchCell(this.mContext);
            view.setPadding(AndroidUtilities.m9dp(6.0f), 0, AndroidUtilities.m9dp(6.0f), 0);
            view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            return new Holder(view);
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.itemView.setException((NotificationException) this.searchResult.get(position), (CharSequence) this.searchResultNames.get(position));
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
        this.actionBar.setBackButtonImage(CLASSNAMER.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        if (this.currentType == -1) {
            this.actionBar.setTitle(LocaleController.getString("NotificationsExceptions", CLASSNAMER.string.NotificationsExceptions));
        } else {
            this.actionBar.setTitle(LocaleController.getString("Notifications", CLASSNAMER.string.Notifications));
        }
        this.actionBar.setActionBarMenuOnItemClick(new CLASSNAME());
        if (!(this.exceptions == null || this.exceptions.isEmpty())) {
            this.actionBar.createMenu().addItem(0, (int) CLASSNAMER.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new CLASSNAME()).setSearchFieldHint(LocaleController.getString("Search", CLASSNAMER.string.Search));
        }
        this.searchListViewAdapter = new SearchAdapter(context);
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = this.fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        this.emptyView = new EmptyTextProgressView(context);
        this.emptyView.setTextSize(18);
        this.emptyView.setText(LocaleController.getString("NoExceptions", CLASSNAMER.string.NoExceptions));
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
        this.listView.setOnScrollListener(new CLASSNAME());
        return this.fragmentView;
    }

    final /* synthetic */ void lambda$createView$6$NotificationsCustomSettingsActivity(View view, int position, float x, float y) {
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
            if (position == this.alertRow) {
                enabled = NotificationsController.getInstance(this.currentAccount).isGlobalNotificationsEnabled(this.currentType);
                NotificationsCheckCell checkCell = (NotificationsCheckCell) view;
                ViewHolder holder = this.listView.findViewHolderForAdapterPosition(position);
                if (enabled) {
                    AlertsCreator.showCustomNotificationsDialog(this, 0, this.currentType, this.exceptions, this.currentAccount, new NotificationsCustomSettingsActivity$$Lambda$3(this, checkCell, holder, position));
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
                        FileLog.m13e(e);
                    }
                } else {
                    return;
                }
            } else if (position == this.messageLedRow) {
                if (view.isEnabled()) {
                    showDialog(AlertsCreator.createColorSelectDialog(getParentActivity(), 0, this.currentType, new NotificationsCustomSettingsActivity$$Lambda$4(this, position)));
                } else {
                    return;
                }
            } else if (position == this.messagePopupNotificationRow) {
                if (view.isEnabled()) {
                    showDialog(AlertsCreator.createPopupSelectDialog(getParentActivity(), this.currentType, new NotificationsCustomSettingsActivity$$Lambda$5(this, position)));
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
                    showDialog(AlertsCreator.createVibrationSelectDialog(getParentActivity(), 0, key, new NotificationsCustomSettingsActivity$$Lambda$6(this, position)));
                } else {
                    return;
                }
            } else if (position == this.messagePriorityRow) {
                if (view.isEnabled()) {
                    showDialog(AlertsCreator.createPrioritySelectDialog(getParentActivity(), 0, this.currentType, new NotificationsCustomSettingsActivity$$Lambda$7(this, position)));
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
            if (arrayList.isEmpty() && arrayList == this.exceptions) {
                this.listView.getAdapter().notifyItemRemoved(this.exceptionsSection2Row);
                this.listView.getAdapter().notifyItemChanged(this.groupSection2Row);
            }
            this.listView.getAdapter().notifyItemRemoved(position);
            if (arrayList.isEmpty() && arrayList == this.exceptions) {
                this.listView.getAdapter().notifyItemRemoved(this.exceptionsSectionRow);
            }
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

    final /* synthetic */ void lambda$null$1$NotificationsCustomSettingsActivity(NotificationsCheckCell checkCell, ViewHolder holder, int position, int param) {
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

    final /* synthetic */ void lambda$null$2$NotificationsCustomSettingsActivity(int position) {
        ViewHolder holder = this.listView.findViewHolderForAdapterPosition(position);
        if (holder != null) {
            this.adapter.onBindViewHolder(holder, position);
        }
    }

    final /* synthetic */ void lambda$null$3$NotificationsCustomSettingsActivity(int position) {
        ViewHolder holder = this.listView.findViewHolderForAdapterPosition(position);
        if (holder != null) {
            this.adapter.onBindViewHolder(holder, position);
        }
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
                this.animatorSet.addListener(new CLASSNAME());
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
        }
        if (this.exceptions == null || this.exceptions.isEmpty()) {
            this.exceptionsSectionRow = -1;
            this.exceptionsStartRow = -1;
            this.exceptionsEndRow = -1;
            this.exceptionsSection2Row = -1;
            return;
        }
        if (this.currentType != -1) {
            i = this.rowCount;
            this.rowCount = i + 1;
            this.exceptionsSectionRow = i;
        } else {
            this.exceptionsSectionRow = -1;
        }
        this.exceptionsStartRow = this.rowCount;
        this.rowCount += this.exceptions.size();
        this.exceptionsEndRow = this.rowCount;
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
                        name = LocaleController.getString("SoundDefault", CLASSNAMER.string.SoundDefault);
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
        r10 = new ThemeDescription[27];
        r10[0] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{HeaderCell.class, TextCheckCell.class, TextColorCell.class, TextSettingsCell.class, ProfileSearchCell.class, NotificationsCheckCell.class}, null, null, null, Theme.key_windowBackgroundWhite);
        r10[1] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray);
        r10[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault);
        r10[3] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault);
        r10[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon);
        r10[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle);
        r10[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector);
        r10[7] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector);
        r10[8] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider);
        r10[9] = new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlueHeader);
        r10[10] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r10[11] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2);
        r10[12] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrack);
        r10[13] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrackChecked);
        r10[14] = new ThemeDescription(this.listView, 0, new Class[]{ProfileSearchCell.class}, Theme.dialogs_namePaint, null, cellDelegate, Theme.key_chats_name);
        r10[15] = new ThemeDescription(this.listView, 0, new Class[]{ProfileSearchCell.class}, Theme.dialogs_nameEncryptedPaint, null, cellDelegate, Theme.key_chats_secretName);
        r10[16] = new ThemeDescription(this.listView, 0, new Class[]{ProfileSearchCell.class}, null, new Drawable[]{Theme.dialogs_lockDrawable}, cellDelegate, Theme.key_chats_secretIcon);
        r10[17] = new ThemeDescription(this.listView, 0, new Class[]{ProfileSearchCell.class}, null, new Drawable[]{Theme.dialogs_groupDrawable, Theme.dialogs_broadcastDrawable, Theme.dialogs_botDrawable}, cellDelegate, Theme.key_chats_nameIcon);
        r10[18] = new ThemeDescription(this.listView, 0, new Class[]{ProfileSearchCell.class}, Theme.dialogs_offlinePaint, null, cellDelegate, Theme.key_windowBackgroundWhiteGrayText3);
        r10[19] = new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r10[20] = new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2);
        r10[21] = new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrack);
        r10[22] = new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrackChecked);
        r10[23] = new ThemeDescription(this.listView, 0, new Class[]{TextColorCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r10[24] = new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r10[25] = new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteValueText);
        r10[26] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        return r10;
    }

    /* renamed from: lambda$getThemeDescriptions$7$NotificationsCustomSettingsActivity */
    final /* synthetic */ void mo18853xvar_() {
        if (this.listView != null) {
            int count = this.listView.getChildCount();
            for (int a = 0; a < count; a++) {
                View child = this.listView.getChildAt(a);
                if (child instanceof ProfileSearchCell) {
                    ((ProfileSearchCell) child).update(0);
                }
            }
        }
    }
}
