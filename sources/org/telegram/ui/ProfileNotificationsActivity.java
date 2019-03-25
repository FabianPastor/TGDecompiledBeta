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
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.NotificationsController;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.LayoutParams;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.TL_dialog;
import org.telegram.tgnet.TLRPC.TL_peerNotifySettings;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.RadioCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCheckBoxCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextColorCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Cells2.UserCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.ui.NotificationsSettingsActivity.NotificationException;

public class ProfileNotificationsActivity extends BaseFragment implements NotificationCenterDelegate {
    private static final int done_button = 1;
    private ListAdapter adapter;
    private boolean addingException;
    private AnimatorSet animatorSet;
    private int avatarRow;
    private int avatarSectionRow;
    private int callsRow;
    private int callsVibrateRow;
    private int colorRow;
    private boolean customEnabled;
    private int customInfoRow;
    private int customRow;
    private ProfileNotificationsActivityDelegate delegate;
    private long dialog_id;
    private int enableRow;
    private int generalRow;
    private int ledInfoRow;
    private int ledRow;
    private RecyclerListView listView;
    private boolean notificationsEnabled;
    private int popupDisabledRow;
    private int popupEnabledRow;
    private int popupInfoRow;
    private int popupRow;
    private int priorityInfoRow;
    private int priorityRow;
    private int ringtoneInfoRow;
    private int ringtoneRow;
    private int rowCount;
    private int smartRow;
    private int soundRow;
    private int vibrateRow;

    public interface ProfileNotificationsActivityDelegate {
        void didCreateNewException(NotificationException notificationException);
    }

    private class ListAdapter extends Adapter {
        private Context context;

        public ListAdapter(Context ctx) {
            this.context = ctx;
        }

        public int getItemCount() {
            return ProfileNotificationsActivity.this.rowCount;
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new HeaderCell(this.context);
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    break;
                case 1:
                    view = new TextSettingsCell(this.context);
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    break;
                case 2:
                    view = new TextInfoPrivacyCell(this.context);
                    break;
                case 3:
                    view = new TextColorCell(this.context);
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    break;
                case 4:
                    view = new RadioCell(this.context);
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    break;
                case 5:
                    view = new TextCheckBoxCell(this.context);
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    break;
                case 6:
                    view = new UserCell(this.context, 4, 0);
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    break;
                case 7:
                    view = new ShadowSectionCell(this.context);
                    break;
                default:
                    view = new TextCheckCell(this.context);
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    break;
            }
            view.setLayoutParams(new LayoutParams(-1, -2));
            return new Holder(view);
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            SharedPreferences preferences;
            String string;
            boolean z;
            switch (holder.getItemViewType()) {
                case 0:
                    HeaderCell headerCell = holder.itemView;
                    if (position == ProfileNotificationsActivity.this.generalRow) {
                        headerCell.setText(LocaleController.getString("General", NUM));
                        return;
                    } else if (position == ProfileNotificationsActivity.this.popupRow) {
                        headerCell.setText(LocaleController.getString("ProfilePopupNotification", NUM));
                        return;
                    } else if (position == ProfileNotificationsActivity.this.ledRow) {
                        headerCell.setText(LocaleController.getString("NotificationsLed", NUM));
                        return;
                    } else if (position == ProfileNotificationsActivity.this.callsRow) {
                        headerCell.setText(LocaleController.getString("VoipNotificationSettings", NUM));
                        return;
                    } else {
                        return;
                    }
                case 1:
                    TextSettingsCell textCell = (TextSettingsCell) holder.itemView;
                    preferences = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount);
                    String value;
                    int value2;
                    if (position == ProfileNotificationsActivity.this.soundRow) {
                        value = preferences.getString("sound_" + ProfileNotificationsActivity.this.dialog_id, LocaleController.getString("SoundDefault", NUM));
                        if (value.equals("NoSound")) {
                            value = LocaleController.getString("NoSound", NUM);
                        }
                        textCell.setTextAndValue(LocaleController.getString("Sound", NUM), value, true);
                        return;
                    } else if (position == ProfileNotificationsActivity.this.ringtoneRow) {
                        value = preferences.getString("ringtone_" + ProfileNotificationsActivity.this.dialog_id, LocaleController.getString("DefaultRingtone", NUM));
                        if (value.equals("NoSound")) {
                            value = LocaleController.getString("NoSound", NUM);
                        }
                        textCell.setTextAndValue(LocaleController.getString("VoipSettingsRingtone", NUM), value, false);
                        return;
                    } else if (position == ProfileNotificationsActivity.this.vibrateRow) {
                        value2 = preferences.getInt("vibrate_" + ProfileNotificationsActivity.this.dialog_id, 0);
                        String string2;
                        if (value2 == 0 || value2 == 4) {
                            string = LocaleController.getString("Vibrate", NUM);
                            string2 = LocaleController.getString("VibrationDefault", NUM);
                            z = (ProfileNotificationsActivity.this.smartRow == -1 && ProfileNotificationsActivity.this.priorityRow == -1) ? false : true;
                            textCell.setTextAndValue(string, string2, z);
                            return;
                        } else if (value2 == 1) {
                            string = LocaleController.getString("Vibrate", NUM);
                            string2 = LocaleController.getString("Short", NUM);
                            z = (ProfileNotificationsActivity.this.smartRow == -1 && ProfileNotificationsActivity.this.priorityRow == -1) ? false : true;
                            textCell.setTextAndValue(string, string2, z);
                            return;
                        } else if (value2 == 2) {
                            string = LocaleController.getString("Vibrate", NUM);
                            string2 = LocaleController.getString("VibrationDisabled", NUM);
                            z = (ProfileNotificationsActivity.this.smartRow == -1 && ProfileNotificationsActivity.this.priorityRow == -1) ? false : true;
                            textCell.setTextAndValue(string, string2, z);
                            return;
                        } else if (value2 == 3) {
                            string = LocaleController.getString("Vibrate", NUM);
                            string2 = LocaleController.getString("Long", NUM);
                            z = (ProfileNotificationsActivity.this.smartRow == -1 && ProfileNotificationsActivity.this.priorityRow == -1) ? false : true;
                            textCell.setTextAndValue(string, string2, z);
                            return;
                        } else {
                            return;
                        }
                    } else if (position == ProfileNotificationsActivity.this.priorityRow) {
                        value2 = preferences.getInt("priority_" + ProfileNotificationsActivity.this.dialog_id, 3);
                        if (value2 == 0) {
                            textCell.setTextAndValue(LocaleController.getString("NotificationsImportance", NUM), LocaleController.getString("NotificationsPriorityHigh", NUM), false);
                            return;
                        } else if (value2 == 1 || value2 == 2) {
                            textCell.setTextAndValue(LocaleController.getString("NotificationsImportance", NUM), LocaleController.getString("NotificationsPriorityUrgent", NUM), false);
                            return;
                        } else if (value2 == 3) {
                            textCell.setTextAndValue(LocaleController.getString("NotificationsImportance", NUM), LocaleController.getString("NotificationsPrioritySettings", NUM), false);
                            return;
                        } else if (value2 == 4) {
                            textCell.setTextAndValue(LocaleController.getString("NotificationsImportance", NUM), LocaleController.getString("NotificationsPriorityLow", NUM), false);
                            return;
                        } else if (value2 == 5) {
                            textCell.setTextAndValue(LocaleController.getString("NotificationsImportance", NUM), LocaleController.getString("NotificationsPriorityMedium", NUM), false);
                            return;
                        } else {
                            return;
                        }
                    } else if (position == ProfileNotificationsActivity.this.smartRow) {
                        int notifyMaxCount = preferences.getInt("smart_max_count_" + ProfileNotificationsActivity.this.dialog_id, 2);
                        int notifyDelay = preferences.getInt("smart_delay_" + ProfileNotificationsActivity.this.dialog_id, 180);
                        if (notifyMaxCount == 0) {
                            textCell.setTextAndValue(LocaleController.getString("SmartNotifications", NUM), LocaleController.getString("SmartNotificationsDisabled", NUM), ProfileNotificationsActivity.this.priorityRow != -1);
                            return;
                        }
                        String minutes = LocaleController.formatPluralString("Minutes", notifyDelay / 60);
                        textCell.setTextAndValue(LocaleController.getString("SmartNotifications", NUM), LocaleController.formatString("SmartNotificationsInfo", NUM, Integer.valueOf(notifyMaxCount), minutes), ProfileNotificationsActivity.this.priorityRow != -1);
                        return;
                    } else if (position == ProfileNotificationsActivity.this.callsVibrateRow) {
                        value2 = preferences.getInt("calls_vibrate_" + ProfileNotificationsActivity.this.dialog_id, 0);
                        if (value2 == 0 || value2 == 4) {
                            textCell.setTextAndValue(LocaleController.getString("Vibrate", NUM), LocaleController.getString("VibrationDefault", NUM), true);
                            return;
                        } else if (value2 == 1) {
                            textCell.setTextAndValue(LocaleController.getString("Vibrate", NUM), LocaleController.getString("Short", NUM), true);
                            return;
                        } else if (value2 == 2) {
                            textCell.setTextAndValue(LocaleController.getString("Vibrate", NUM), LocaleController.getString("VibrationDisabled", NUM), true);
                            return;
                        } else if (value2 == 3) {
                            textCell.setTextAndValue(LocaleController.getString("Vibrate", NUM), LocaleController.getString("Long", NUM), true);
                            return;
                        } else {
                            return;
                        }
                    } else {
                        return;
                    }
                case 2:
                    TextInfoPrivacyCell textCell2 = (TextInfoPrivacyCell) holder.itemView;
                    if (position == ProfileNotificationsActivity.this.popupInfoRow) {
                        textCell2.setText(LocaleController.getString("ProfilePopupNotificationInfo", NUM));
                        textCell2.setBackgroundDrawable(Theme.getThemedDrawable(this.context, NUM, "windowBackgroundGrayShadow"));
                        return;
                    } else if (position == ProfileNotificationsActivity.this.ledInfoRow) {
                        textCell2.setText(LocaleController.getString("NotificationsLedInfo", NUM));
                        textCell2.setBackgroundDrawable(Theme.getThemedDrawable(this.context, NUM, "windowBackgroundGrayShadow"));
                        return;
                    } else if (position == ProfileNotificationsActivity.this.priorityInfoRow) {
                        if (ProfileNotificationsActivity.this.priorityRow == -1) {
                            textCell2.setText("");
                        } else {
                            textCell2.setText(LocaleController.getString("PriorityInfo", NUM));
                        }
                        textCell2.setBackgroundDrawable(Theme.getThemedDrawable(this.context, NUM, "windowBackgroundGrayShadow"));
                        return;
                    } else if (position == ProfileNotificationsActivity.this.customInfoRow) {
                        textCell2.setText(null);
                        textCell2.setBackgroundDrawable(Theme.getThemedDrawable(this.context, NUM, "windowBackgroundGrayShadow"));
                        return;
                    } else if (position == ProfileNotificationsActivity.this.ringtoneInfoRow) {
                        textCell2.setText(LocaleController.getString("VoipRingtoneInfo", NUM));
                        textCell2.setBackgroundDrawable(Theme.getThemedDrawable(this.context, NUM, "windowBackgroundGrayShadow"));
                        return;
                    } else {
                        return;
                    }
                case 3:
                    int color;
                    TextColorCell textCell3 = (TextColorCell) holder.itemView;
                    preferences = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount);
                    if (preferences.contains("color_" + ProfileNotificationsActivity.this.dialog_id)) {
                        color = preferences.getInt("color_" + ProfileNotificationsActivity.this.dialog_id, -16776961);
                    } else if (((int) ProfileNotificationsActivity.this.dialog_id) < 0) {
                        color = preferences.getInt("GroupLed", -16776961);
                    } else {
                        color = preferences.getInt("MessagesLed", -16776961);
                    }
                    for (int a = 0; a < 9; a++) {
                        if (TextColorCell.colorsToSave[a] == color) {
                            color = TextColorCell.colors[a];
                            textCell3.setTextAndColor(LocaleController.getString("NotificationsLedColor", NUM), color, false);
                            return;
                        }
                    }
                    textCell3.setTextAndColor(LocaleController.getString("NotificationsLedColor", NUM), color, false);
                    return;
                case 4:
                    RadioCell radioCell = (RadioCell) holder.itemView;
                    preferences = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount);
                    int popup = preferences.getInt("popup_" + ProfileNotificationsActivity.this.dialog_id, 0);
                    if (popup == 0) {
                        if (preferences.getInt(((int) ProfileNotificationsActivity.this.dialog_id) < 0 ? "popupGroup" : "popupAll", 0) != 0) {
                            popup = 1;
                        } else {
                            popup = 2;
                        }
                    }
                    if (position == ProfileNotificationsActivity.this.popupEnabledRow) {
                        radioCell.setText(LocaleController.getString("PopupEnabled", NUM), popup == 1, true);
                        radioCell.setTag(Integer.valueOf(1));
                        return;
                    } else if (position == ProfileNotificationsActivity.this.popupDisabledRow) {
                        radioCell.setText(LocaleController.getString("PopupDisabled", NUM), popup == 2, false);
                        radioCell.setTag(Integer.valueOf(2));
                        return;
                    } else {
                        return;
                    }
                case 5:
                    TextCheckBoxCell cell = holder.itemView;
                    preferences = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount);
                    string = LocaleController.getString("NotificationsEnableCustom", NUM);
                    z = ProfileNotificationsActivity.this.customEnabled && ProfileNotificationsActivity.this.notificationsEnabled;
                    cell.setTextAndCheck(string, z, false);
                    return;
                case 6:
                    TLObject object;
                    UserCell userCell = (UserCell) holder.itemView;
                    int lower_id = (int) ProfileNotificationsActivity.this.dialog_id;
                    if (lower_id > 0) {
                        object = MessagesController.getInstance(ProfileNotificationsActivity.this.currentAccount).getUser(Integer.valueOf(lower_id));
                    } else {
                        object = MessagesController.getInstance(ProfileNotificationsActivity.this.currentAccount).getChat(Integer.valueOf(-lower_id));
                    }
                    userCell.setData(object, null, null, 0);
                    return;
                case 8:
                    TextCheckCell checkCell = holder.itemView;
                    preferences = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount);
                    if (position == ProfileNotificationsActivity.this.enableRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("Notifications", NUM), ProfileNotificationsActivity.this.notificationsEnabled, true);
                        return;
                    }
                    return;
                default:
                    return;
            }
        }

        public void onViewAttachedToWindow(ViewHolder holder) {
            boolean z = true;
            if (holder.getItemViewType() != 0) {
                switch (holder.getItemViewType()) {
                    case 1:
                        TextSettingsCell textCell = holder.itemView;
                        if (!(ProfileNotificationsActivity.this.customEnabled && ProfileNotificationsActivity.this.notificationsEnabled)) {
                            z = false;
                        }
                        textCell.setEnabled(z, null);
                        return;
                    case 2:
                        TextInfoPrivacyCell textCell2 = holder.itemView;
                        if (!(ProfileNotificationsActivity.this.customEnabled && ProfileNotificationsActivity.this.notificationsEnabled)) {
                            z = false;
                        }
                        textCell2.setEnabled(z, null);
                        return;
                    case 3:
                        TextColorCell textCell3 = holder.itemView;
                        if (!(ProfileNotificationsActivity.this.customEnabled && ProfileNotificationsActivity.this.notificationsEnabled)) {
                            z = false;
                        }
                        textCell3.setEnabled(z, null);
                        return;
                    case 4:
                        RadioCell radioCell = holder.itemView;
                        if (!(ProfileNotificationsActivity.this.customEnabled && ProfileNotificationsActivity.this.notificationsEnabled)) {
                            z = false;
                        }
                        radioCell.setEnabled(z, null);
                        return;
                    default:
                        return;
                }
            }
        }

        public int getItemViewType(int position) {
            if (position == ProfileNotificationsActivity.this.generalRow || position == ProfileNotificationsActivity.this.popupRow || position == ProfileNotificationsActivity.this.ledRow || position == ProfileNotificationsActivity.this.callsRow) {
                return 0;
            }
            if (position == ProfileNotificationsActivity.this.soundRow || position == ProfileNotificationsActivity.this.vibrateRow || position == ProfileNotificationsActivity.this.priorityRow || position == ProfileNotificationsActivity.this.smartRow || position == ProfileNotificationsActivity.this.ringtoneRow || position == ProfileNotificationsActivity.this.callsVibrateRow) {
                return 1;
            }
            if (position == ProfileNotificationsActivity.this.popupInfoRow || position == ProfileNotificationsActivity.this.ledInfoRow || position == ProfileNotificationsActivity.this.priorityInfoRow || position == ProfileNotificationsActivity.this.customInfoRow || position == ProfileNotificationsActivity.this.ringtoneInfoRow) {
                return 2;
            }
            if (position == ProfileNotificationsActivity.this.colorRow) {
                return 3;
            }
            if (position == ProfileNotificationsActivity.this.popupEnabledRow || position == ProfileNotificationsActivity.this.popupDisabledRow) {
                return 4;
            }
            if (position == ProfileNotificationsActivity.this.customRow) {
                return 5;
            }
            if (position == ProfileNotificationsActivity.this.avatarRow) {
                return 6;
            }
            if (position == ProfileNotificationsActivity.this.avatarSectionRow) {
                return 7;
            }
            if (position == ProfileNotificationsActivity.this.enableRow) {
                return 8;
            }
            return 0;
        }
    }

    public ProfileNotificationsActivity(Bundle args) {
        super(args);
        this.dialog_id = args.getLong("dialog_id");
        this.addingException = args.getBoolean("exception", false);
    }

    public boolean onFragmentCreate() {
        int i;
        boolean z;
        this.rowCount = 0;
        if (this.addingException) {
            i = this.rowCount;
            this.rowCount = i + 1;
            this.avatarRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.avatarSectionRow = i;
            this.customRow = -1;
            this.customInfoRow = -1;
        } else {
            this.avatarRow = -1;
            this.avatarSectionRow = -1;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.customRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.customInfoRow = i;
        }
        i = this.rowCount;
        this.rowCount = i + 1;
        this.generalRow = i;
        if (this.addingException) {
            i = this.rowCount;
            this.rowCount = i + 1;
            this.enableRow = i;
        } else {
            this.enableRow = -1;
        }
        i = this.rowCount;
        this.rowCount = i + 1;
        this.soundRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.vibrateRow = i;
        if (((int) this.dialog_id) < 0) {
            i = this.rowCount;
            this.rowCount = i + 1;
            this.smartRow = i;
        } else {
            this.smartRow = -1;
        }
        if (VERSION.SDK_INT >= 21) {
            i = this.rowCount;
            this.rowCount = i + 1;
            this.priorityRow = i;
        } else {
            this.priorityRow = -1;
        }
        i = this.rowCount;
        this.rowCount = i + 1;
        this.priorityInfoRow = i;
        int lower_id = (int) this.dialog_id;
        boolean isChannel;
        if (lower_id < 0) {
            Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-lower_id));
            if (chat == null || !ChatObject.isChannel(chat) || chat.megagroup) {
                isChannel = false;
            } else {
                isChannel = true;
            }
        } else {
            isChannel = false;
        }
        if (lower_id == 0 || isChannel) {
            this.popupRow = -1;
            this.popupEnabledRow = -1;
            this.popupDisabledRow = -1;
            this.popupInfoRow = -1;
        } else {
            i = this.rowCount;
            this.rowCount = i + 1;
            this.popupRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.popupEnabledRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.popupDisabledRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.popupInfoRow = i;
        }
        if (lower_id > 0) {
            i = this.rowCount;
            this.rowCount = i + 1;
            this.callsRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.callsVibrateRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.ringtoneRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.ringtoneInfoRow = i;
        } else {
            this.callsRow = -1;
            this.callsVibrateRow = -1;
            this.ringtoneRow = -1;
            this.ringtoneInfoRow = -1;
        }
        i = this.rowCount;
        this.rowCount = i + 1;
        this.ledRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.colorRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.ledInfoRow = i;
        SharedPreferences preferences = MessagesController.getNotificationsSettings(this.currentAccount);
        if (preferences.getBoolean("custom_" + this.dialog_id, false) || this.addingException) {
            z = true;
        } else {
            z = false;
        }
        this.customEnabled = z;
        boolean hasOverride = preferences.contains("notify2_" + this.dialog_id);
        int value = preferences.getInt("notify2_" + this.dialog_id, 0);
        if (value == 0) {
            if (hasOverride) {
                this.notificationsEnabled = true;
            } else {
                this.notificationsEnabled = NotificationsController.getInstance(this.currentAccount).isGlobalNotificationsEnabled(this.dialog_id);
            }
        } else if (value == 1) {
            this.notificationsEnabled = true;
        } else if (value == 2) {
            this.notificationsEnabled = false;
        } else {
            this.notificationsEnabled = false;
        }
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.notificationsSettingsUpdated);
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.notificationsSettingsUpdated);
    }

    public View createView(final Context context) {
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    if (!ProfileNotificationsActivity.this.addingException && ProfileNotificationsActivity.this.notificationsEnabled && ProfileNotificationsActivity.this.customEnabled) {
                        MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount).edit().putInt("notify2_" + ProfileNotificationsActivity.this.dialog_id, 0).commit();
                    }
                } else if (id == 1) {
                    SharedPreferences preferences = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount);
                    Editor editor = preferences.edit();
                    editor.putBoolean("custom_" + ProfileNotificationsActivity.this.dialog_id, true);
                    TL_dialog dialog = (TL_dialog) MessagesController.getInstance(ProfileNotificationsActivity.this.currentAccount).dialogs_dict.get(ProfileNotificationsActivity.this.dialog_id);
                    if (ProfileNotificationsActivity.this.notificationsEnabled) {
                        editor.putInt("notify2_" + ProfileNotificationsActivity.this.dialog_id, 0);
                        MessagesStorage.getInstance(ProfileNotificationsActivity.this.currentAccount).setDialogFlags(ProfileNotificationsActivity.this.dialog_id, 0);
                        if (dialog != null) {
                            dialog.notify_settings = new TL_peerNotifySettings();
                        }
                    } else {
                        editor.putInt("notify2_" + ProfileNotificationsActivity.this.dialog_id, 2);
                        NotificationsController.getInstance(ProfileNotificationsActivity.this.currentAccount).removeNotificationsForDialog(ProfileNotificationsActivity.this.dialog_id);
                        MessagesStorage.getInstance(ProfileNotificationsActivity.this.currentAccount).setDialogFlags(ProfileNotificationsActivity.this.dialog_id, 1);
                        if (dialog != null) {
                            dialog.notify_settings = new TL_peerNotifySettings();
                            dialog.notify_settings.mute_until = Integer.MAX_VALUE;
                        }
                    }
                    editor.commit();
                    NotificationsController.getInstance(ProfileNotificationsActivity.this.currentAccount).updateServerNotificationsSettings(ProfileNotificationsActivity.this.dialog_id);
                    if (ProfileNotificationsActivity.this.delegate != null) {
                        NotificationException exception = new NotificationException();
                        exception.did = ProfileNotificationsActivity.this.dialog_id;
                        exception.hasCustom = true;
                        exception.notify = preferences.getInt("notify2_" + ProfileNotificationsActivity.this.dialog_id, 0);
                        if (exception.notify != 0) {
                            exception.muteUntil = preferences.getInt("notifyuntil_" + ProfileNotificationsActivity.this.dialog_id, 0);
                        }
                        ProfileNotificationsActivity.this.delegate.didCreateNewException(exception);
                    }
                }
                ProfileNotificationsActivity.this.finishFragment();
            }
        });
        if (this.addingException) {
            this.actionBar.setTitle(LocaleController.getString("NotificationsNewException", NUM));
            this.actionBar.createMenu().addItemWithWidth(1, NUM, AndroidUtilities.dp(56.0f));
        } else {
            this.actionBar.setTitle(LocaleController.getString("CustomNotifications", NUM));
        }
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = this.fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        this.listView = new RecyclerListView(context);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        RecyclerListView recyclerListView = this.listView;
        ListAdapter listAdapter = new ListAdapter(context);
        this.adapter = listAdapter;
        recyclerListView.setAdapter(listAdapter);
        this.listView.setItemAnimator(null);
        this.listView.setLayoutAnimation(null);
        this.listView.setLayoutManager(new LinearLayoutManager(context) {
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        });
        this.listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(View view, int position) {
                SharedPreferences preferences;
                if (position == ProfileNotificationsActivity.this.customRow && (view instanceof TextCheckBoxCell)) {
                    preferences = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount);
                    ProfileNotificationsActivity.this.customEnabled = !ProfileNotificationsActivity.this.customEnabled;
                    ProfileNotificationsActivity.this.notificationsEnabled = ProfileNotificationsActivity.this.customEnabled;
                    preferences.edit().putBoolean("custom_" + ProfileNotificationsActivity.this.dialog_id, ProfileNotificationsActivity.this.customEnabled).commit();
                    ((TextCheckBoxCell) view).setChecked(ProfileNotificationsActivity.this.customEnabled);
                    int count = ProfileNotificationsActivity.this.listView.getChildCount();
                    ArrayList<Animator> animators = new ArrayList();
                    for (int a = 0; a < count; a++) {
                        Holder holder = (Holder) ProfileNotificationsActivity.this.listView.getChildViewHolder(ProfileNotificationsActivity.this.listView.getChildAt(a));
                        int type = holder.getItemViewType();
                        if (!(holder.getAdapterPosition() == ProfileNotificationsActivity.this.customRow || type == 0)) {
                            switch (type) {
                                case 1:
                                    ((TextSettingsCell) holder.itemView).setEnabled(ProfileNotificationsActivity.this.customEnabled, animators);
                                    break;
                                case 2:
                                    ((TextInfoPrivacyCell) holder.itemView).setEnabled(ProfileNotificationsActivity.this.customEnabled, animators);
                                    break;
                                case 3:
                                    ((TextColorCell) holder.itemView).setEnabled(ProfileNotificationsActivity.this.customEnabled, animators);
                                    break;
                                case 4:
                                    ((RadioCell) holder.itemView).setEnabled(ProfileNotificationsActivity.this.customEnabled, animators);
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                    if (!animators.isEmpty()) {
                        if (ProfileNotificationsActivity.this.animatorSet != null) {
                            ProfileNotificationsActivity.this.animatorSet.cancel();
                        }
                        ProfileNotificationsActivity.this.animatorSet = new AnimatorSet();
                        ProfileNotificationsActivity.this.animatorSet.playTogether(animators);
                        ProfileNotificationsActivity.this.animatorSet.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animator) {
                                if (animator.equals(ProfileNotificationsActivity.this.animatorSet)) {
                                    ProfileNotificationsActivity.this.animatorSet = null;
                                }
                            }
                        });
                        ProfileNotificationsActivity.this.animatorSet.setDuration(150);
                        ProfileNotificationsActivity.this.animatorSet.start();
                    }
                } else if (!ProfileNotificationsActivity.this.customEnabled) {
                } else {
                    Intent intent;
                    Uri currentSound;
                    String defaultPath;
                    Uri defaultUri;
                    String path;
                    if (position == ProfileNotificationsActivity.this.soundRow) {
                        try {
                            intent = new Intent("android.intent.action.RINGTONE_PICKER");
                            intent.putExtra("android.intent.extra.ringtone.TYPE", 2);
                            intent.putExtra("android.intent.extra.ringtone.SHOW_DEFAULT", true);
                            intent.putExtra("android.intent.extra.ringtone.DEFAULT_URI", RingtoneManager.getDefaultUri(2));
                            preferences = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount);
                            currentSound = null;
                            defaultPath = null;
                            defaultUri = System.DEFAULT_NOTIFICATION_URI;
                            if (defaultUri != null) {
                                defaultPath = defaultUri.getPath();
                            }
                            path = preferences.getString("sound_path_" + ProfileNotificationsActivity.this.dialog_id, defaultPath);
                            if (path != null) {
                                if (!path.equals("NoSound")) {
                                    currentSound = path.equals(defaultPath) ? defaultUri : Uri.parse(path);
                                }
                            }
                            intent.putExtra("android.intent.extra.ringtone.EXISTING_URI", currentSound);
                            ProfileNotificationsActivity.this.startActivityForResult(intent, 12);
                        } catch (Exception e) {
                            FileLog.e(e);
                        }
                    } else if (position == ProfileNotificationsActivity.this.ringtoneRow) {
                        try {
                            intent = new Intent("android.intent.action.RINGTONE_PICKER");
                            intent.putExtra("android.intent.extra.ringtone.TYPE", 1);
                            intent.putExtra("android.intent.extra.ringtone.SHOW_DEFAULT", true);
                            intent.putExtra("android.intent.extra.ringtone.DEFAULT_URI", RingtoneManager.getDefaultUri(1));
                            preferences = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount);
                            currentSound = null;
                            defaultPath = null;
                            defaultUri = System.DEFAULT_NOTIFICATION_URI;
                            if (defaultUri != null) {
                                defaultPath = defaultUri.getPath();
                            }
                            path = preferences.getString("ringtone_path_" + ProfileNotificationsActivity.this.dialog_id, defaultPath);
                            if (path != null) {
                                if (!path.equals("NoSound")) {
                                    currentSound = path.equals(defaultPath) ? defaultUri : Uri.parse(path);
                                }
                            }
                            intent.putExtra("android.intent.extra.ringtone.EXISTING_URI", currentSound);
                            ProfileNotificationsActivity.this.startActivityForResult(intent, 13);
                        } catch (Exception e2) {
                            FileLog.e(e2);
                        }
                    } else if (position == ProfileNotificationsActivity.this.vibrateRow) {
                        ProfileNotificationsActivity.this.showDialog(AlertsCreator.createVibrationSelectDialog(ProfileNotificationsActivity.this.getParentActivity(), ProfileNotificationsActivity.this.dialog_id, false, false, new ProfileNotificationsActivity$3$$Lambda$0(this)));
                    } else if (position == ProfileNotificationsActivity.this.enableRow) {
                        TextCheckCell checkCell = (TextCheckCell) view;
                        ProfileNotificationsActivity.this.notificationsEnabled = !checkCell.isChecked();
                        checkCell.setChecked(ProfileNotificationsActivity.this.notificationsEnabled);
                    } else if (position == ProfileNotificationsActivity.this.callsVibrateRow) {
                        ProfileNotificationsActivity.this.showDialog(AlertsCreator.createVibrationSelectDialog(ProfileNotificationsActivity.this.getParentActivity(), ProfileNotificationsActivity.this.dialog_id, "calls_vibrate_", new ProfileNotificationsActivity$3$$Lambda$1(this)));
                    } else if (position == ProfileNotificationsActivity.this.priorityRow) {
                        ProfileNotificationsActivity.this.showDialog(AlertsCreator.createPrioritySelectDialog(ProfileNotificationsActivity.this.getParentActivity(), ProfileNotificationsActivity.this.dialog_id, -1, new ProfileNotificationsActivity$3$$Lambda$2(this)));
                    } else if (position == ProfileNotificationsActivity.this.smartRow) {
                        if (ProfileNotificationsActivity.this.getParentActivity() != null) {
                            final Context context1 = ProfileNotificationsActivity.this.getParentActivity();
                            preferences = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount);
                            int notifyMaxCount = preferences.getInt("smart_max_count_" + ProfileNotificationsActivity.this.dialog_id, 2);
                            int notifyDelay = preferences.getInt("smart_delay_" + ProfileNotificationsActivity.this.dialog_id, 180);
                            if (notifyMaxCount == 0) {
                                notifyMaxCount = 2;
                            }
                            int selected = ((((notifyDelay / 60) - 1) * 10) + notifyMaxCount) - 1;
                            View recyclerListView = new RecyclerListView(ProfileNotificationsActivity.this.getParentActivity());
                            recyclerListView.setLayoutManager(new LinearLayoutManager(context, 1, false));
                            recyclerListView.setClipToPadding(true);
                            final int i = selected;
                            recyclerListView.setAdapter(new SelectionAdapter() {
                                public int getItemCount() {
                                    return 100;
                                }

                                public boolean isEnabled(ViewHolder holder) {
                                    return true;
                                }

                                public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                                    View view = new TextView(context1) {
                                        /* Access modifiers changed, original: protected */
                                        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                                            super.onMeasure(MeasureSpec.makeMeasureSpec(widthMeasureSpec, NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), NUM));
                                        }
                                    };
                                    TextView textView = (TextView) view;
                                    textView.setGravity(17);
                                    textView.setTextSize(1, 18.0f);
                                    textView.setSingleLine(true);
                                    textView.setEllipsize(TruncateAt.END);
                                    textView.setLayoutParams(new LayoutParams(-1, -2));
                                    return new Holder(view);
                                }

                                public void onBindViewHolder(ViewHolder holder, int position) {
                                    TextView textView = holder.itemView;
                                    textView.setTextColor(Theme.getColor(position == i ? "dialogTextGray" : "dialogTextBlack"));
                                    int notifyDelay = position / 10;
                                    String times = LocaleController.formatPluralString("Times", (position % 10) + 1);
                                    String minutes = LocaleController.formatPluralString("Minutes", notifyDelay + 1);
                                    textView.setText(LocaleController.formatString("SmartNotificationsDetail", NUM, times, minutes));
                                }
                            });
                            recyclerListView.setPadding(0, AndroidUtilities.dp(12.0f), 0, AndroidUtilities.dp(8.0f));
                            recyclerListView.setOnItemClickListener(new ProfileNotificationsActivity$3$$Lambda$3(this));
                            Builder builder = new Builder(ProfileNotificationsActivity.this.getParentActivity());
                            builder.setTitle(LocaleController.getString("SmartNotificationsAlert", NUM));
                            builder.setView(recyclerListView);
                            builder.setPositiveButton(LocaleController.getString("Cancel", NUM), null);
                            builder.setNegativeButton(LocaleController.getString("SmartNotificationsDisabled", NUM), new ProfileNotificationsActivity$3$$Lambda$4(this));
                            ProfileNotificationsActivity.this.showDialog(builder.create());
                        }
                    } else if (position == ProfileNotificationsActivity.this.colorRow) {
                        if (ProfileNotificationsActivity.this.getParentActivity() != null) {
                            ProfileNotificationsActivity.this.showDialog(AlertsCreator.createColorSelectDialog(ProfileNotificationsActivity.this.getParentActivity(), ProfileNotificationsActivity.this.dialog_id, -1, new ProfileNotificationsActivity$3$$Lambda$5(this)));
                        }
                    } else if (position == ProfileNotificationsActivity.this.popupEnabledRow) {
                        MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount).edit().putInt("popup_" + ProfileNotificationsActivity.this.dialog_id, 1).commit();
                        ((RadioCell) view).setChecked(true, true);
                        view = ProfileNotificationsActivity.this.listView.findViewWithTag(Integer.valueOf(2));
                        if (view != null) {
                            ((RadioCell) view).setChecked(false, true);
                        }
                    } else if (position == ProfileNotificationsActivity.this.popupDisabledRow) {
                        MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount).edit().putInt("popup_" + ProfileNotificationsActivity.this.dialog_id, 2).commit();
                        ((RadioCell) view).setChecked(true, true);
                        view = ProfileNotificationsActivity.this.listView.findViewWithTag(Integer.valueOf(1));
                        if (view != null) {
                            ((RadioCell) view).setChecked(false, true);
                        }
                    }
                }
            }

            /* Access modifiers changed, original: final|synthetic */
            public final /* synthetic */ void lambda$onItemClick$0$ProfileNotificationsActivity$3() {
                if (ProfileNotificationsActivity.this.adapter != null) {
                    ProfileNotificationsActivity.this.adapter.notifyItemChanged(ProfileNotificationsActivity.this.vibrateRow);
                }
            }

            /* Access modifiers changed, original: final|synthetic */
            public final /* synthetic */ void lambda$onItemClick$1$ProfileNotificationsActivity$3() {
                if (ProfileNotificationsActivity.this.adapter != null) {
                    ProfileNotificationsActivity.this.adapter.notifyItemChanged(ProfileNotificationsActivity.this.callsVibrateRow);
                }
            }

            /* Access modifiers changed, original: final|synthetic */
            public final /* synthetic */ void lambda$onItemClick$2$ProfileNotificationsActivity$3() {
                if (ProfileNotificationsActivity.this.adapter != null) {
                    ProfileNotificationsActivity.this.adapter.notifyItemChanged(ProfileNotificationsActivity.this.priorityRow);
                }
            }

            /* Access modifiers changed, original: final|synthetic */
            public final /* synthetic */ void lambda$onItemClick$3$ProfileNotificationsActivity$3(View view1, int position1) {
                if (position1 >= 0 && position1 < 100) {
                    int notifyMaxCount1 = (position1 % 10) + 1;
                    int notifyDelay1 = (position1 / 10) + 1;
                    SharedPreferences preferences1 = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount);
                    preferences1.edit().putInt("smart_max_count_" + ProfileNotificationsActivity.this.dialog_id, notifyMaxCount1).commit();
                    preferences1.edit().putInt("smart_delay_" + ProfileNotificationsActivity.this.dialog_id, notifyDelay1 * 60).commit();
                    if (ProfileNotificationsActivity.this.adapter != null) {
                        ProfileNotificationsActivity.this.adapter.notifyItemChanged(ProfileNotificationsActivity.this.smartRow);
                    }
                    ProfileNotificationsActivity.this.dismissCurrentDialig();
                }
            }

            /* Access modifiers changed, original: final|synthetic */
            public final /* synthetic */ void lambda$onItemClick$4$ProfileNotificationsActivity$3(DialogInterface dialog, int which) {
                MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount).edit().putInt("smart_max_count_" + ProfileNotificationsActivity.this.dialog_id, 0).commit();
                if (ProfileNotificationsActivity.this.adapter != null) {
                    ProfileNotificationsActivity.this.adapter.notifyItemChanged(ProfileNotificationsActivity.this.smartRow);
                }
                ProfileNotificationsActivity.this.dismissCurrentDialig();
            }

            /* Access modifiers changed, original: final|synthetic */
            public final /* synthetic */ void lambda$onItemClick$5$ProfileNotificationsActivity$3() {
                if (ProfileNotificationsActivity.this.adapter != null) {
                    ProfileNotificationsActivity.this.adapter.notifyItemChanged(ProfileNotificationsActivity.this.colorRow);
                }
            }
        });
        return this.fragmentView;
    }

    public void onActivityResultFragment(int requestCode, int resultCode, Intent data) {
        if (resultCode == -1 && data != null) {
            Uri ringtone = (Uri) data.getParcelableExtra("android.intent.extra.ringtone.PICKED_URI");
            String name = null;
            if (ringtone != null) {
                Ringtone rng = RingtoneManager.getRingtone(ApplicationLoader.applicationContext, ringtone);
                if (rng != null) {
                    if (requestCode == 13) {
                        if (ringtone.equals(System.DEFAULT_RINGTONE_URI)) {
                            name = LocaleController.getString("DefaultRingtone", NUM);
                        } else {
                            name = rng.getTitle(getParentActivity());
                        }
                    } else if (ringtone.equals(System.DEFAULT_NOTIFICATION_URI)) {
                        name = LocaleController.getString("SoundDefault", NUM);
                    } else {
                        name = rng.getTitle(getParentActivity());
                    }
                    rng.stop();
                }
            }
            Editor editor = MessagesController.getNotificationsSettings(this.currentAccount).edit();
            if (requestCode == 12) {
                if (name != null) {
                    editor.putString("sound_" + this.dialog_id, name);
                    editor.putString("sound_path_" + this.dialog_id, ringtone.toString());
                } else {
                    editor.putString("sound_" + this.dialog_id, "NoSound");
                    editor.putString("sound_path_" + this.dialog_id, "NoSound");
                }
            } else if (requestCode == 13) {
                if (name != null) {
                    editor.putString("ringtone_" + this.dialog_id, name);
                    editor.putString("ringtone_path_" + this.dialog_id, ringtone.toString());
                } else {
                    editor.putString("ringtone_" + this.dialog_id, "NoSound");
                    editor.putString("ringtone_path_" + this.dialog_id, "NoSound");
                }
            }
            editor.commit();
            if (this.adapter != null) {
                int i;
                ListAdapter listAdapter = this.adapter;
                if (requestCode == 13) {
                    i = this.ringtoneRow;
                } else {
                    i = this.soundRow;
                }
                listAdapter.notifyItemChanged(i);
            }
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.notificationsSettingsUpdated) {
            this.adapter.notifyDataSetChanged();
        }
    }

    public void setDelegate(ProfileNotificationsActivityDelegate profileNotificationsActivityDelegate) {
        this.delegate = profileNotificationsActivityDelegate;
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescriptionDelegate cellDelegate = new ProfileNotificationsActivity$$Lambda$0(this);
        r10 = new ThemeDescription[39];
        r10[0] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{HeaderCell.class, TextSettingsCell.class, TextColorCell.class, RadioCell.class, UserCell.class, TextCheckCell.class, TextCheckBoxCell.class}, null, null, null, "windowBackgroundWhite");
        r10[1] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundGray");
        r10[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
        r10[3] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault");
        r10[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
        r10[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
        r10[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
        r10[7] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
        r10[8] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, "divider");
        r10[9] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, "windowBackgroundGrayShadow");
        r10[10] = new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlueHeader");
        r10[11] = new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        r10[12] = new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, null, null, null, "windowBackgroundWhiteValueText");
        r10[13] = new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteGrayText4");
        r10[14] = new ThemeDescription(this.listView, 0, new Class[]{TextColorCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        r10[15] = new ThemeDescription(this.listView, 0, new Class[]{RadioCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        r10[16] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKBOX, new Class[]{RadioCell.class}, new String[]{"radioButton"}, null, null, null, "radioBackground");
        r10[17] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{RadioCell.class}, new String[]{"radioButton"}, null, null, null, "radioBackgroundChecked");
        r10[18] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, "windowBackgroundGrayShadow");
        r10[19] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        r10[20] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"valueTextView"}, null, null, null, "windowBackgroundWhiteGrayText2");
        r10[21] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, "switchTrack");
        r10[22] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, "switchTrackChecked");
        r10[23] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"nameTextView"}, null, null, null, "windowBackgroundWhiteBlackText");
        r10[24] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusColor"}, null, null, cellDelegate, "windowBackgroundWhiteGrayText");
        r10[25] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusOnlineColor"}, null, null, cellDelegate, "windowBackgroundWhiteBlueText");
        r10[26] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, null, new Drawable[]{Theme.avatar_broadcastDrawable, Theme.avatar_savedDrawable}, null, "avatar_text");
        r10[27] = new ThemeDescription(null, 0, null, null, null, cellDelegate, "avatar_backgroundRed");
        r10[28] = new ThemeDescription(null, 0, null, null, null, cellDelegate, "avatar_backgroundOrange");
        r10[29] = new ThemeDescription(null, 0, null, null, null, cellDelegate, "avatar_backgroundViolet");
        r10[30] = new ThemeDescription(null, 0, null, null, null, cellDelegate, "avatar_backgroundGreen");
        r10[31] = new ThemeDescription(null, 0, null, null, null, cellDelegate, "avatar_backgroundCyan");
        r10[32] = new ThemeDescription(null, 0, null, null, null, cellDelegate, "avatar_backgroundBlue");
        r10[33] = new ThemeDescription(null, 0, null, null, null, cellDelegate, "avatar_backgroundPink");
        r10[34] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckBoxCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        r10[35] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckBoxCell.class}, null, null, null, "checkboxSquareUnchecked");
        r10[36] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckBoxCell.class}, null, null, null, "checkboxSquareDisabled");
        r10[37] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckBoxCell.class}, null, null, null, "checkboxSquareBackground");
        r10[38] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckBoxCell.class}, null, null, null, "checkboxSquareCheck");
        return r10;
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$getThemeDescriptions$0$ProfileNotificationsActivity() {
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
