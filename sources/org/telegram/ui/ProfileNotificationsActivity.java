package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
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
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.LayoutParams;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.RadioCell;
import org.telegram.ui.Cells.TextCheckBoxCell;
import org.telegram.ui.Cells.TextColorCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;

public class ProfileNotificationsActivity extends BaseFragment implements NotificationCenterDelegate {
    private ListAdapter adapter;
    private AnimatorSet animatorSet;
    private int callsRow;
    private int callsVibrateRow;
    private int colorRow;
    private boolean customEnabled;
    private int customInfoRow;
    private int customRow;
    private long dialog_id;
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

    /* renamed from: org.telegram.ui.ProfileNotificationsActivity$1 */
    class C22631 extends ActionBarMenuOnItemClick {
        C22631() {
        }

        public void onItemClick(int id) {
            if (id == -1) {
                if (ProfileNotificationsActivity.this.notificationsEnabled && ProfileNotificationsActivity.this.customEnabled) {
                    Editor edit = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount).edit();
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("notify2_");
                    stringBuilder.append(ProfileNotificationsActivity.this.dialog_id);
                    edit.putInt(stringBuilder.toString(), 0).commit();
                }
                ProfileNotificationsActivity.this.finishFragment();
            }
        }
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
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 1:
                    view = new TextSettingsCell(this.context);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 2:
                    view = new TextInfoPrivacyCell(this.context);
                    break;
                case 3:
                    view = new TextColorCell(this.context);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 4:
                    view = new RadioCell(this.context);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                default:
                    view = new TextCheckBoxCell(this.context);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
            }
            view.setLayoutParams(new LayoutParams(-1, -2));
            return new Holder(view);
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            ListAdapter listAdapter = this;
            ViewHolder viewHolder = holder;
            int i = position;
            boolean z = true;
            boolean z2 = false;
            String value;
            String string;
            String string2;
            StringBuilder stringBuilder;
            int notifyDelay;
            SharedPreferences preferences;
            switch (holder.getItemViewType()) {
                case 0:
                    HeaderCell headerCell = viewHolder.itemView;
                    if (i == ProfileNotificationsActivity.this.generalRow) {
                        headerCell.setText(LocaleController.getString("General", R.string.General));
                        return;
                    } else if (i == ProfileNotificationsActivity.this.popupRow) {
                        headerCell.setText(LocaleController.getString("ProfilePopupNotification", R.string.ProfilePopupNotification));
                        return;
                    } else if (i == ProfileNotificationsActivity.this.ledRow) {
                        headerCell.setText(LocaleController.getString("NotificationsLed", R.string.NotificationsLed));
                        return;
                    } else if (i == ProfileNotificationsActivity.this.callsRow) {
                        headerCell.setText(LocaleController.getString("VoipNotificationSettings", R.string.VoipNotificationSettings));
                        return;
                    } else {
                        return;
                    }
                case 1:
                    TextSettingsCell textCell = viewHolder.itemView;
                    SharedPreferences preferences2 = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount);
                    StringBuilder stringBuilder2;
                    if (i == ProfileNotificationsActivity.this.soundRow) {
                        stringBuilder2 = new StringBuilder();
                        stringBuilder2.append("sound_");
                        stringBuilder2.append(ProfileNotificationsActivity.this.dialog_id);
                        value = preferences2.getString(stringBuilder2.toString(), LocaleController.getString("SoundDefault", R.string.SoundDefault));
                        if (value.equals("NoSound")) {
                            value = LocaleController.getString("NoSound", R.string.NoSound);
                        }
                        textCell.setTextAndValue(LocaleController.getString("Sound", R.string.Sound), value, true);
                        return;
                    } else if (i == ProfileNotificationsActivity.this.ringtoneRow) {
                        stringBuilder2 = new StringBuilder();
                        stringBuilder2.append("ringtone_");
                        stringBuilder2.append(ProfileNotificationsActivity.this.dialog_id);
                        value = preferences2.getString(stringBuilder2.toString(), LocaleController.getString("DefaultRingtone", R.string.DefaultRingtone));
                        if (value.equals("NoSound")) {
                            value = LocaleController.getString("NoSound", R.string.NoSound);
                        }
                        textCell.setTextAndValue(LocaleController.getString("VoipSettingsRingtone", R.string.VoipSettingsRingtone), value, false);
                        return;
                    } else if (i == ProfileNotificationsActivity.this.vibrateRow) {
                        StringBuilder stringBuilder3 = new StringBuilder();
                        stringBuilder3.append("vibrate_");
                        stringBuilder3.append(ProfileNotificationsActivity.this.dialog_id);
                        value = preferences2.getInt(stringBuilder3.toString(), 0);
                        if (value != 0) {
                            if (value != 4) {
                                if (value == 1) {
                                    string = LocaleController.getString("Vibrate", R.string.Vibrate);
                                    string2 = LocaleController.getString("Short", R.string.Short);
                                    if (ProfileNotificationsActivity.this.smartRow == -1) {
                                        if (ProfileNotificationsActivity.this.priorityRow == -1) {
                                            z = false;
                                        }
                                    }
                                    textCell.setTextAndValue(string, string2, z);
                                } else if (value == 2) {
                                    string = LocaleController.getString("Vibrate", R.string.Vibrate);
                                    string2 = LocaleController.getString("VibrationDisabled", R.string.VibrationDisabled);
                                    if (ProfileNotificationsActivity.this.smartRow == -1) {
                                        if (ProfileNotificationsActivity.this.priorityRow == -1) {
                                            z = false;
                                        }
                                    }
                                    textCell.setTextAndValue(string, string2, z);
                                } else if (value == 3) {
                                    string = LocaleController.getString("Vibrate", R.string.Vibrate);
                                    string2 = LocaleController.getString("Long", R.string.Long);
                                    if (ProfileNotificationsActivity.this.smartRow == -1) {
                                        if (ProfileNotificationsActivity.this.priorityRow == -1) {
                                            z = false;
                                        }
                                    }
                                    textCell.setTextAndValue(string, string2, z);
                                }
                                return;
                            }
                        }
                        string = LocaleController.getString("Vibrate", R.string.Vibrate);
                        string2 = LocaleController.getString("VibrationDefault", R.string.VibrationDefault);
                        if (ProfileNotificationsActivity.this.smartRow == -1) {
                            if (ProfileNotificationsActivity.this.priorityRow == -1) {
                                z = false;
                            }
                        }
                        textCell.setTextAndValue(string, string2, z);
                        return;
                    } else if (i == ProfileNotificationsActivity.this.priorityRow) {
                        stringBuilder2 = new StringBuilder();
                        stringBuilder2.append("priority_");
                        stringBuilder2.append(ProfileNotificationsActivity.this.dialog_id);
                        value = preferences2.getInt(stringBuilder2.toString(), 3);
                        if (value == 0) {
                            textCell.setTextAndValue(LocaleController.getString("NotificationsImportance", R.string.NotificationsImportance), LocaleController.getString("NotificationsPriorityHigh", R.string.NotificationsPriorityHigh), false);
                        } else {
                            if (value != 1) {
                                if (value != 2) {
                                    if (value == 3) {
                                        textCell.setTextAndValue(LocaleController.getString("NotificationsImportance", R.string.NotificationsImportance), LocaleController.getString("NotificationsPrioritySettings", R.string.NotificationsPrioritySettings), false);
                                    } else if (value == 4) {
                                        textCell.setTextAndValue(LocaleController.getString("NotificationsImportance", R.string.NotificationsImportance), LocaleController.getString("NotificationsPriorityLow", R.string.NotificationsPriorityLow), false);
                                    } else if (value == 5) {
                                        textCell.setTextAndValue(LocaleController.getString("NotificationsImportance", R.string.NotificationsImportance), LocaleController.getString("NotificationsPriorityMedium", R.string.NotificationsPriorityMedium), false);
                                    }
                                }
                            }
                            textCell.setTextAndValue(LocaleController.getString("NotificationsImportance", R.string.NotificationsImportance), LocaleController.getString("NotificationsPriorityUrgent", R.string.NotificationsPriorityUrgent), false);
                        }
                        return;
                    } else if (i == ProfileNotificationsActivity.this.smartRow) {
                        stringBuilder2 = new StringBuilder();
                        stringBuilder2.append("smart_max_count_");
                        stringBuilder2.append(ProfileNotificationsActivity.this.dialog_id);
                        value = preferences2.getInt(stringBuilder2.toString(), 2);
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("smart_delay_");
                        stringBuilder.append(ProfileNotificationsActivity.this.dialog_id);
                        notifyDelay = preferences2.getInt(stringBuilder.toString(), 180);
                        String string3;
                        if (value == 0) {
                            string2 = LocaleController.getString("SmartNotifications", R.string.SmartNotifications);
                            string3 = LocaleController.getString("SmartNotificationsDisabled", R.string.SmartNotificationsDisabled);
                            if (ProfileNotificationsActivity.this.priorityRow == -1) {
                                z = false;
                            }
                            textCell.setTextAndValue(string2, string3, z);
                        } else {
                            string2 = LocaleController.formatPluralString("Minutes", notifyDelay / 60);
                            string3 = LocaleController.getString("SmartNotifications", R.string.SmartNotifications);
                            String formatString = LocaleController.formatString("SmartNotificationsInfo", R.string.SmartNotificationsInfo, Integer.valueOf(value), string2);
                            if (ProfileNotificationsActivity.this.priorityRow == -1) {
                                z = false;
                            }
                            textCell.setTextAndValue(string3, formatString, z);
                        }
                        return;
                    } else if (i == ProfileNotificationsActivity.this.callsVibrateRow) {
                        stringBuilder2 = new StringBuilder();
                        stringBuilder2.append("calls_vibrate_");
                        stringBuilder2.append(ProfileNotificationsActivity.this.dialog_id);
                        value = preferences2.getInt(stringBuilder2.toString(), 0);
                        if (value != 0) {
                            if (value != 4) {
                                if (value == 1) {
                                    textCell.setTextAndValue(LocaleController.getString("Vibrate", R.string.Vibrate), LocaleController.getString("Short", R.string.Short), true);
                                } else if (value == 2) {
                                    textCell.setTextAndValue(LocaleController.getString("Vibrate", R.string.Vibrate), LocaleController.getString("VibrationDisabled", R.string.VibrationDisabled), true);
                                } else if (value == 3) {
                                    textCell.setTextAndValue(LocaleController.getString("Vibrate", R.string.Vibrate), LocaleController.getString("Long", R.string.Long), true);
                                }
                                return;
                            }
                        }
                        textCell.setTextAndValue(LocaleController.getString("Vibrate", R.string.Vibrate), LocaleController.getString("VibrationDefault", R.string.VibrationDefault), true);
                        return;
                    } else {
                        return;
                    }
                case 2:
                    TextInfoPrivacyCell textCell2 = viewHolder.itemView;
                    if (i == ProfileNotificationsActivity.this.popupInfoRow) {
                        textCell2.setText(LocaleController.getString("ProfilePopupNotificationInfo", R.string.ProfilePopupNotificationInfo));
                        textCell2.setBackgroundDrawable(Theme.getThemedDrawable(listAdapter.context, R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else if (i == ProfileNotificationsActivity.this.ledInfoRow) {
                        textCell2.setText(LocaleController.getString("NotificationsLedInfo", R.string.NotificationsLedInfo));
                        textCell2.setBackgroundDrawable(Theme.getThemedDrawable(listAdapter.context, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else if (i == ProfileNotificationsActivity.this.priorityInfoRow) {
                        if (ProfileNotificationsActivity.this.priorityRow == -1) {
                            textCell2.setText(TtmlNode.ANONYMOUS_REGION_ID);
                        } else {
                            textCell2.setText(LocaleController.getString("PriorityInfo", R.string.PriorityInfo));
                        }
                        textCell2.setBackgroundDrawable(Theme.getThemedDrawable(listAdapter.context, R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else if (i == ProfileNotificationsActivity.this.customInfoRow) {
                        textCell2.setText(null);
                        textCell2.setBackgroundDrawable(Theme.getThemedDrawable(listAdapter.context, R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else if (i == ProfileNotificationsActivity.this.ringtoneInfoRow) {
                        textCell2.setText(LocaleController.getString("VoipRingtoneInfo", R.string.VoipRingtoneInfo));
                        textCell2.setBackgroundDrawable(Theme.getThemedDrawable(listAdapter.context, R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else {
                        return;
                    }
                case 3:
                    int a;
                    TextColorCell textCell3 = viewHolder.itemView;
                    preferences = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount);
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("color_");
                    stringBuilder.append(ProfileNotificationsActivity.this.dialog_id);
                    if (preferences.contains(stringBuilder.toString())) {
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("color_");
                        stringBuilder.append(ProfileNotificationsActivity.this.dialog_id);
                        notifyDelay = preferences.getInt(stringBuilder.toString(), -16776961);
                    } else if (((int) ProfileNotificationsActivity.this.dialog_id) < 0) {
                        notifyDelay = preferences.getInt("GroupLed", -16776961);
                    } else {
                        notifyDelay = preferences.getInt("MessagesLed", -16776961);
                        for (a = 0; a < 9; a++) {
                            if (TextColorCell.colorsToSave[a] != notifyDelay) {
                                notifyDelay = TextColorCell.colors[a];
                                textCell3.setTextAndColor(LocaleController.getString("NotificationsLedColor", R.string.NotificationsLedColor), notifyDelay, false);
                                return;
                            }
                        }
                        textCell3.setTextAndColor(LocaleController.getString("NotificationsLedColor", R.string.NotificationsLedColor), notifyDelay, false);
                        return;
                    }
                    while (a < 9) {
                        if (TextColorCell.colorsToSave[a] != notifyDelay) {
                        } else {
                            notifyDelay = TextColorCell.colors[a];
                            textCell3.setTextAndColor(LocaleController.getString("NotificationsLedColor", R.string.NotificationsLedColor), notifyDelay, false);
                            return;
                        }
                    }
                    textCell3.setTextAndColor(LocaleController.getString("NotificationsLedColor", R.string.NotificationsLedColor), notifyDelay, false);
                    return;
                case 4:
                    RadioCell radioCell = viewHolder.itemView;
                    SharedPreferences preferences3 = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount);
                    StringBuilder stringBuilder4 = new StringBuilder();
                    stringBuilder4.append("popup_");
                    stringBuilder4.append(ProfileNotificationsActivity.this.dialog_id);
                    int popup = preferences3.getInt(stringBuilder4.toString(), 0);
                    if (popup == 0) {
                        if (preferences3.getInt(((int) ProfileNotificationsActivity.this.dialog_id) < 0 ? "popupGroup" : "popupAll", 0) != 0) {
                            popup = 1;
                        } else {
                            popup = 2;
                        }
                    }
                    if (i == ProfileNotificationsActivity.this.popupEnabledRow) {
                        value = LocaleController.getString("PopupEnabled", R.string.PopupEnabled);
                        if (popup == 1) {
                            z2 = true;
                        }
                        radioCell.setText(value, z2, true);
                        radioCell.setTag(Integer.valueOf(1));
                        return;
                    } else if (i == ProfileNotificationsActivity.this.popupDisabledRow) {
                        string2 = LocaleController.getString("PopupDisabled", R.string.PopupDisabled);
                        if (popup != 2) {
                            z = false;
                        }
                        radioCell.setText(string2, z, false);
                        radioCell.setTag(Integer.valueOf(2));
                        return;
                    } else {
                        return;
                    }
                case 5:
                    TextCheckBoxCell cell = viewHolder.itemView;
                    preferences = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount);
                    string = LocaleController.getString("NotificationsEnableCustom", R.string.NotificationsEnableCustom);
                    if (!ProfileNotificationsActivity.this.customEnabled || !ProfileNotificationsActivity.this.notificationsEnabled) {
                        z = false;
                    }
                    cell.setTextAndCheck(string, z, false);
                    return;
                default:
                    return;
            }
        }

        public void onViewAttachedToWindow(ViewHolder holder) {
            if (holder.getItemViewType() != 0) {
                boolean z = false;
                switch (holder.getItemViewType()) {
                    case 1:
                        TextSettingsCell textCell = holder.itemView;
                        if (ProfileNotificationsActivity.this.customEnabled && ProfileNotificationsActivity.this.notificationsEnabled) {
                            z = true;
                        }
                        textCell.setEnabled(z, null);
                        return;
                    case 2:
                        TextInfoPrivacyCell textCell2 = holder.itemView;
                        if (ProfileNotificationsActivity.this.customEnabled && ProfileNotificationsActivity.this.notificationsEnabled) {
                            z = true;
                        }
                        textCell2.setEnabled(z, null);
                        return;
                    case 3:
                        TextColorCell textCell3 = holder.itemView;
                        if (ProfileNotificationsActivity.this.customEnabled && ProfileNotificationsActivity.this.notificationsEnabled) {
                            z = true;
                        }
                        textCell3.setEnabled(z, null);
                        return;
                    case 4:
                        RadioCell radioCell = holder.itemView;
                        if (ProfileNotificationsActivity.this.customEnabled && ProfileNotificationsActivity.this.notificationsEnabled) {
                            z = true;
                        }
                        radioCell.setEnabled(z, null);
                        return;
                    default:
                        return;
                }
            }
        }

        public int getItemViewType(int position) {
            if (!(position == ProfileNotificationsActivity.this.generalRow || position == ProfileNotificationsActivity.this.popupRow || position == ProfileNotificationsActivity.this.ledRow)) {
                if (position != ProfileNotificationsActivity.this.callsRow) {
                    if (!(position == ProfileNotificationsActivity.this.soundRow || position == ProfileNotificationsActivity.this.vibrateRow || position == ProfileNotificationsActivity.this.priorityRow || position == ProfileNotificationsActivity.this.smartRow || position == ProfileNotificationsActivity.this.ringtoneRow)) {
                        if (position != ProfileNotificationsActivity.this.callsVibrateRow) {
                            if (!(position == ProfileNotificationsActivity.this.popupInfoRow || position == ProfileNotificationsActivity.this.ledInfoRow || position == ProfileNotificationsActivity.this.priorityInfoRow || position == ProfileNotificationsActivity.this.customInfoRow)) {
                                if (position != ProfileNotificationsActivity.this.ringtoneInfoRow) {
                                    if (position == ProfileNotificationsActivity.this.colorRow) {
                                        return 3;
                                    }
                                    if (position != ProfileNotificationsActivity.this.popupEnabledRow) {
                                        if (position != ProfileNotificationsActivity.this.popupDisabledRow) {
                                            if (position == ProfileNotificationsActivity.this.customRow) {
                                                return 5;
                                            }
                                            return 0;
                                        }
                                    }
                                    return 4;
                                }
                            }
                            return 2;
                        }
                    }
                    return 1;
                }
            }
            return 0;
        }
    }

    public ProfileNotificationsActivity(Bundle args) {
        super(args);
        this.dialog_id = args.getLong("dialog_id");
    }

    public boolean onFragmentCreate() {
        boolean z;
        int i;
        this.rowCount = 0;
        int i2 = this.rowCount;
        this.rowCount = i2 + 1;
        this.customRow = i2;
        i2 = this.rowCount;
        this.rowCount = i2 + 1;
        this.customInfoRow = i2;
        i2 = this.rowCount;
        this.rowCount = i2 + 1;
        this.generalRow = i2;
        i2 = this.rowCount;
        this.rowCount = i2 + 1;
        this.soundRow = i2;
        i2 = this.rowCount;
        this.rowCount = i2 + 1;
        this.vibrateRow = i2;
        if (((int) this.dialog_id) < 0) {
            i2 = this.rowCount;
            this.rowCount = i2 + 1;
            this.smartRow = i2;
        } else {
            this.smartRow = -1;
        }
        if (VERSION.SDK_INT >= 21) {
            i2 = this.rowCount;
            this.rowCount = i2 + 1;
            this.priorityRow = i2;
        } else {
            this.priorityRow = -1;
        }
        i2 = this.rowCount;
        this.rowCount = i2 + 1;
        this.priorityInfoRow = i2;
        i2 = (int) this.dialog_id;
        boolean isChannel;
        if (i2 < 0) {
            Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-i2));
            z = (chat == null || !ChatObject.isChannel(chat) || chat.megagroup) ? false : true;
            isChannel = z;
        } else {
            isChannel = false;
        }
        if (i2 == 0 || isChannel) {
            this.popupRow = -1;
            this.popupEnabledRow = -1;
            this.popupDisabledRow = -1;
            this.popupInfoRow = -1;
        } else {
            int i3 = this.rowCount;
            this.rowCount = i3 + 1;
            this.popupRow = i3;
            i3 = this.rowCount;
            this.rowCount = i3 + 1;
            this.popupEnabledRow = i3;
            i3 = this.rowCount;
            this.rowCount = i3 + 1;
            this.popupDisabledRow = i3;
            i3 = this.rowCount;
            this.rowCount = i3 + 1;
            this.popupInfoRow = i3;
        }
        if (i2 > 0) {
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
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("custom_");
        stringBuilder.append(this.dialog_id);
        this.customEnabled = preferences.getBoolean(stringBuilder.toString(), false);
        stringBuilder = new StringBuilder();
        stringBuilder.append("notify2_");
        stringBuilder.append(this.dialog_id);
        z = preferences.contains(stringBuilder.toString());
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append("notify2_");
        stringBuilder2.append(this.dialog_id);
        int value = preferences.getInt(stringBuilder2.toString(), 0);
        if (value == 0) {
            if (z) {
                this.notificationsEnabled = true;
            } else if (((int) this.dialog_id) < 0) {
                this.notificationsEnabled = preferences.getBoolean("EnableGroup", true);
            } else {
                this.notificationsEnabled = preferences.getBoolean("EnableAll", true);
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
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("CustomNotifications", R.string.CustomNotifications));
        this.actionBar.setActionBarMenuOnItemClick(new C22631());
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = this.fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        this.listView = new RecyclerListView(context);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        RecyclerListView recyclerListView = this.listView;
        Adapter listAdapter = new ListAdapter(context);
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

            /* renamed from: org.telegram.ui.ProfileNotificationsActivity$3$1 */
            class C16501 extends AnimatorListenerAdapter {
                C16501() {
                }

                public void onAnimationEnd(Animator animator) {
                    if (animator.equals(ProfileNotificationsActivity.this.animatorSet)) {
                        ProfileNotificationsActivity.this.animatorSet = null;
                    }
                }
            }

            /* renamed from: org.telegram.ui.ProfileNotificationsActivity$3$2 */
            class C16512 implements Runnable {
                C16512() {
                }

                public void run() {
                    if (ProfileNotificationsActivity.this.adapter != null) {
                        ProfileNotificationsActivity.this.adapter.notifyItemChanged(ProfileNotificationsActivity.this.vibrateRow);
                    }
                }
            }

            /* renamed from: org.telegram.ui.ProfileNotificationsActivity$3$3 */
            class C16523 implements Runnable {
                C16523() {
                }

                public void run() {
                    if (ProfileNotificationsActivity.this.adapter != null) {
                        ProfileNotificationsActivity.this.adapter.notifyItemChanged(ProfileNotificationsActivity.this.callsVibrateRow);
                    }
                }
            }

            /* renamed from: org.telegram.ui.ProfileNotificationsActivity$3$4 */
            class C16534 implements Runnable {
                C16534() {
                }

                public void run() {
                    if (ProfileNotificationsActivity.this.adapter != null) {
                        ProfileNotificationsActivity.this.adapter.notifyItemChanged(ProfileNotificationsActivity.this.priorityRow);
                    }
                }
            }

            /* renamed from: org.telegram.ui.ProfileNotificationsActivity$3$7 */
            class C16557 implements OnClickListener {
                C16557() {
                }

                public void onClick(DialogInterface dialog, int which) {
                    Editor edit = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount).edit();
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("smart_max_count_");
                    stringBuilder.append(ProfileNotificationsActivity.this.dialog_id);
                    edit.putInt(stringBuilder.toString(), 0).commit();
                    if (ProfileNotificationsActivity.this.adapter != null) {
                        ProfileNotificationsActivity.this.adapter.notifyItemChanged(ProfileNotificationsActivity.this.smartRow);
                    }
                    ProfileNotificationsActivity.this.dismissCurrentDialig();
                }
            }

            /* renamed from: org.telegram.ui.ProfileNotificationsActivity$3$8 */
            class C16568 implements Runnable {
                C16568() {
                }

                public void run() {
                    if (ProfileNotificationsActivity.this.adapter != null) {
                        ProfileNotificationsActivity.this.adapter.notifyItemChanged(ProfileNotificationsActivity.this.colorRow);
                    }
                }
            }

            /* renamed from: org.telegram.ui.ProfileNotificationsActivity$3$6 */
            class C22646 implements OnItemClickListener {
                C22646() {
                }

                public void onItemClick(View view, int position) {
                    if (position >= 0) {
                        if (position < 100) {
                            int notifyMaxCount = (position % 10) + 1;
                            int notifyDelay = (position / 10) + 1;
                            SharedPreferences preferences = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount);
                            Editor edit = preferences.edit();
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("smart_max_count_");
                            stringBuilder.append(ProfileNotificationsActivity.this.dialog_id);
                            edit.putInt(stringBuilder.toString(), notifyMaxCount).commit();
                            edit = preferences.edit();
                            stringBuilder = new StringBuilder();
                            stringBuilder.append("smart_delay_");
                            stringBuilder.append(ProfileNotificationsActivity.this.dialog_id);
                            edit.putInt(stringBuilder.toString(), notifyDelay * 60).commit();
                            if (ProfileNotificationsActivity.this.adapter != null) {
                                ProfileNotificationsActivity.this.adapter.notifyItemChanged(ProfileNotificationsActivity.this.smartRow);
                            }
                            ProfileNotificationsActivity.this.dismissCurrentDialig();
                        }
                    }
                }
            }

            public void onItemClick(View view, int position) {
                int a = 0;
                int count;
                if (position == ProfileNotificationsActivity.this.customRow && (view instanceof TextCheckBoxCell)) {
                    SharedPreferences preferences = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount);
                    ProfileNotificationsActivity.this.customEnabled = true ^ ProfileNotificationsActivity.this.customEnabled;
                    ProfileNotificationsActivity.this.notificationsEnabled = ProfileNotificationsActivity.this.customEnabled;
                    Editor edit = preferences.edit();
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("custom_");
                    stringBuilder.append(ProfileNotificationsActivity.this.dialog_id);
                    edit.putBoolean(stringBuilder.toString(), ProfileNotificationsActivity.this.customEnabled).commit();
                    ((TextCheckBoxCell) view).setChecked(ProfileNotificationsActivity.this.customEnabled);
                    count = ProfileNotificationsActivity.this.listView.getChildCount();
                    ArrayList<Animator> animators = new ArrayList();
                    while (a < count) {
                        Holder holder = (Holder) ProfileNotificationsActivity.this.listView.getChildViewHolder(ProfileNotificationsActivity.this.listView.getChildAt(a));
                        int type = holder.getItemViewType();
                        if (!(holder.getAdapterPosition() == ProfileNotificationsActivity.this.customRow || type == 0)) {
                            switch (type) {
                                case 1:
                                    holder.itemView.setEnabled(ProfileNotificationsActivity.this.customEnabled, animators);
                                    break;
                                case 2:
                                    holder.itemView.setEnabled(ProfileNotificationsActivity.this.customEnabled, animators);
                                    break;
                                case 3:
                                    holder.itemView.setEnabled(ProfileNotificationsActivity.this.customEnabled, animators);
                                    break;
                                case 4:
                                    holder.itemView.setEnabled(ProfileNotificationsActivity.this.customEnabled, animators);
                                    break;
                                default:
                                    break;
                            }
                        }
                        a++;
                    }
                    if (!animators.isEmpty()) {
                        if (ProfileNotificationsActivity.this.animatorSet != null) {
                            ProfileNotificationsActivity.this.animatorSet.cancel();
                        }
                        ProfileNotificationsActivity.this.animatorSet = new AnimatorSet();
                        ProfileNotificationsActivity.this.animatorSet.playTogether(animators);
                        ProfileNotificationsActivity.this.animatorSet.addListener(new C16501());
                        ProfileNotificationsActivity.this.animatorSet.setDuration(150);
                        ProfileNotificationsActivity.this.animatorSet.start();
                    }
                } else if (ProfileNotificationsActivity.this.customEnabled) {
                    Intent tmpIntent;
                    SharedPreferences preferences2;
                    Uri currentSound;
                    String defaultPath;
                    Uri defaultUri;
                    StringBuilder stringBuilder2;
                    String path;
                    if (position == ProfileNotificationsActivity.this.soundRow) {
                        try {
                            tmpIntent = new Intent("android.intent.action.RINGTONE_PICKER");
                            tmpIntent.putExtra("android.intent.extra.ringtone.TYPE", 2);
                            tmpIntent.putExtra("android.intent.extra.ringtone.SHOW_DEFAULT", true);
                            tmpIntent.putExtra("android.intent.extra.ringtone.DEFAULT_URI", RingtoneManager.getDefaultUri(2));
                            preferences2 = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount);
                            currentSound = null;
                            defaultPath = null;
                            defaultUri = System.DEFAULT_NOTIFICATION_URI;
                            if (defaultUri != null) {
                                defaultPath = defaultUri.getPath();
                            }
                            stringBuilder2 = new StringBuilder();
                            stringBuilder2.append("sound_path_");
                            stringBuilder2.append(ProfileNotificationsActivity.this.dialog_id);
                            path = preferences2.getString(stringBuilder2.toString(), defaultPath);
                            if (!(path == null || path.equals("NoSound"))) {
                                currentSound = path.equals(defaultPath) ? defaultUri : Uri.parse(path);
                            }
                            tmpIntent.putExtra("android.intent.extra.ringtone.EXISTING_URI", currentSound);
                            ProfileNotificationsActivity.this.startActivityForResult(tmpIntent, 12);
                        } catch (Throwable e) {
                            FileLog.m3e(e);
                        }
                    } else if (position == ProfileNotificationsActivity.this.ringtoneRow) {
                        try {
                            tmpIntent = new Intent("android.intent.action.RINGTONE_PICKER");
                            tmpIntent.putExtra("android.intent.extra.ringtone.TYPE", 1);
                            tmpIntent.putExtra("android.intent.extra.ringtone.SHOW_DEFAULT", true);
                            tmpIntent.putExtra("android.intent.extra.ringtone.DEFAULT_URI", RingtoneManager.getDefaultUri(1));
                            preferences2 = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount);
                            currentSound = null;
                            defaultPath = null;
                            defaultUri = System.DEFAULT_NOTIFICATION_URI;
                            if (defaultUri != null) {
                                defaultPath = defaultUri.getPath();
                            }
                            stringBuilder2 = new StringBuilder();
                            stringBuilder2.append("ringtone_path_");
                            stringBuilder2.append(ProfileNotificationsActivity.this.dialog_id);
                            path = preferences2.getString(stringBuilder2.toString(), defaultPath);
                            if (!(path == null || path.equals("NoSound"))) {
                                currentSound = path.equals(defaultPath) ? defaultUri : Uri.parse(path);
                            }
                            tmpIntent.putExtra("android.intent.extra.ringtone.EXISTING_URI", currentSound);
                            ProfileNotificationsActivity.this.startActivityForResult(tmpIntent, 13);
                        } catch (Throwable e2) {
                            FileLog.m3e(e2);
                        }
                    } else if (position == ProfileNotificationsActivity.this.vibrateRow) {
                        ProfileNotificationsActivity.this.showDialog(AlertsCreator.createVibrationSelectDialog(ProfileNotificationsActivity.this.getParentActivity(), ProfileNotificationsActivity.this, ProfileNotificationsActivity.this.dialog_id, false, false, new C16512()));
                    } else if (position == ProfileNotificationsActivity.this.callsVibrateRow) {
                        ProfileNotificationsActivity.this.showDialog(AlertsCreator.createVibrationSelectDialog(ProfileNotificationsActivity.this.getParentActivity(), ProfileNotificationsActivity.this, ProfileNotificationsActivity.this.dialog_id, "calls_vibrate_", new C16523()));
                    } else if (position == ProfileNotificationsActivity.this.priorityRow) {
                        ProfileNotificationsActivity.this.showDialog(AlertsCreator.createPrioritySelectDialog(ProfileNotificationsActivity.this.getParentActivity(), ProfileNotificationsActivity.this, ProfileNotificationsActivity.this.dialog_id, false, false, new C16534()));
                    } else if (position == ProfileNotificationsActivity.this.smartRow) {
                        if (ProfileNotificationsActivity.this.getParentActivity() != null) {
                            final Context context1 = ProfileNotificationsActivity.this.getParentActivity();
                            SharedPreferences preferences3 = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount);
                            stringBuilder2 = new StringBuilder();
                            stringBuilder2.append("smart_max_count_");
                            stringBuilder2.append(ProfileNotificationsActivity.this.dialog_id);
                            count = preferences3.getInt(stringBuilder2.toString(), 2);
                            stringBuilder2 = new StringBuilder();
                            stringBuilder2.append("smart_delay_");
                            stringBuilder2.append(ProfileNotificationsActivity.this.dialog_id);
                            int notifyDelay = preferences3.getInt(stringBuilder2.toString(), 180);
                            if (count == 0) {
                                count = 2;
                            }
                            final int selected = ((((notifyDelay / 60) - 1) * 10) + count) - 1;
                            RecyclerListView list = new RecyclerListView(ProfileNotificationsActivity.this.getParentActivity());
                            list.setLayoutManager(new LinearLayoutManager(context, 1, false));
                            list.setClipToPadding(true);
                            list.setAdapter(new SelectionAdapter() {
                                public int getItemCount() {
                                    return 100;
                                }

                                public boolean isEnabled(ViewHolder holder) {
                                    return true;
                                }

                                public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                                    View view = new TextView(context1) {
                                        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
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
                                    textView.setTextColor(Theme.getColor(position == selected ? Theme.key_dialogTextGray : Theme.key_dialogTextBlack));
                                    int notifyDelay = position / 10;
                                    String times = LocaleController.formatPluralString("Times", (position % 10) + 1);
                                    String minutes = LocaleController.formatPluralString("Minutes", notifyDelay + 1);
                                    textView.setText(LocaleController.formatString("SmartNotificationsDetail", R.string.SmartNotificationsDetail, times, minutes));
                                }
                            });
                            list.setPadding(0, AndroidUtilities.dp(12.0f), 0, AndroidUtilities.dp(8.0f));
                            list.setOnItemClickListener(new C22646());
                            Builder builder = new Builder(ProfileNotificationsActivity.this.getParentActivity());
                            builder.setTitle(LocaleController.getString("SmartNotificationsAlert", R.string.SmartNotificationsAlert));
                            builder.setView(list);
                            builder.setPositiveButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                            builder.setNegativeButton(LocaleController.getString("SmartNotificationsDisabled", R.string.SmartNotificationsDisabled), new C16557());
                            ProfileNotificationsActivity.this.showDialog(builder.create());
                        }
                    } else if (position == ProfileNotificationsActivity.this.colorRow) {
                        if (ProfileNotificationsActivity.this.getParentActivity() != null) {
                            ProfileNotificationsActivity.this.showDialog(AlertsCreator.createColorSelectDialog(ProfileNotificationsActivity.this.getParentActivity(), ProfileNotificationsActivity.this.dialog_id, false, false, new C16568()));
                        }
                    } else if (position == ProfileNotificationsActivity.this.popupEnabledRow) {
                        r4 = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount).edit();
                        stringBuilder2 = new StringBuilder();
                        stringBuilder2.append("popup_");
                        stringBuilder2.append(ProfileNotificationsActivity.this.dialog_id);
                        r4.putInt(stringBuilder2.toString(), 1).commit();
                        ((RadioCell) view).setChecked(true, true);
                        view = ProfileNotificationsActivity.this.listView.findViewWithTag(Integer.valueOf(2));
                        if (view != null) {
                            ((RadioCell) view).setChecked(false, true);
                        }
                    } else if (position == ProfileNotificationsActivity.this.popupDisabledRow) {
                        r4 = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount).edit();
                        stringBuilder2 = new StringBuilder();
                        stringBuilder2.append("popup_");
                        stringBuilder2.append(ProfileNotificationsActivity.this.dialog_id);
                        r4.putInt(stringBuilder2.toString(), 2).commit();
                        ((RadioCell) view).setChecked(true, true);
                        view = ProfileNotificationsActivity.this.listView.findViewWithTag(Integer.valueOf(1));
                        if (view != null) {
                            ((RadioCell) view).setChecked(false, true);
                        }
                    }
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
            StringBuilder stringBuilder;
            if (requestCode == 12) {
                if (name != null) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("sound_");
                    stringBuilder.append(this.dialog_id);
                    editor.putString(stringBuilder.toString(), name);
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("sound_path_");
                    stringBuilder.append(this.dialog_id);
                    editor.putString(stringBuilder.toString(), ringtone.toString());
                } else {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("sound_");
                    stringBuilder.append(this.dialog_id);
                    editor.putString(stringBuilder.toString(), "NoSound");
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("sound_path_");
                    stringBuilder.append(this.dialog_id);
                    editor.putString(stringBuilder.toString(), "NoSound");
                }
            } else if (requestCode == 13) {
                if (name != null) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("ringtone_");
                    stringBuilder.append(this.dialog_id);
                    editor.putString(stringBuilder.toString(), name);
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("ringtone_path_");
                    stringBuilder.append(this.dialog_id);
                    editor.putString(stringBuilder.toString(), ringtone.toString());
                } else {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("ringtone_");
                    stringBuilder.append(this.dialog_id);
                    editor.putString(stringBuilder.toString(), "NoSound");
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("ringtone_path_");
                    stringBuilder.append(this.dialog_id);
                    editor.putString(stringBuilder.toString(), "NoSound");
                }
            }
            editor.commit();
            if (this.adapter != null) {
                this.adapter.notifyItemChanged(requestCode == 13 ? this.ringtoneRow : this.soundRow);
            }
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.notificationsSettingsUpdated) {
            this.adapter.notifyDataSetChanged();
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[23];
        themeDescriptionArr[0] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{HeaderCell.class, TextSettingsCell.class, TextColorCell.class, RadioCell.class, TextCheckBoxCell.class}, null, null, null, Theme.key_windowBackgroundWhite);
        themeDescriptionArr[1] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray);
        themeDescriptionArr[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault);
        themeDescriptionArr[3] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault);
        themeDescriptionArr[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon);
        themeDescriptionArr[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle);
        themeDescriptionArr[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector);
        themeDescriptionArr[7] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector);
        themeDescriptionArr[8] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider);
        themeDescriptionArr[9] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        themeDescriptionArr[10] = new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlueHeader);
        themeDescriptionArr[11] = new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[12] = new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteValueText);
        themeDescriptionArr[13] = new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText4);
        themeDescriptionArr[14] = new ThemeDescription(this.listView, 0, new Class[]{TextColorCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[15] = new ThemeDescription(this.listView, 0, new Class[]{RadioCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[16] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKBOX, new Class[]{RadioCell.class}, new String[]{"radioButton"}, null, null, null, Theme.key_radioBackground);
        themeDescriptionArr[17] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{RadioCell.class}, new String[]{"radioButton"}, null, null, null, Theme.key_radioBackgroundChecked);
        themeDescriptionArr[18] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckBoxCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[19] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckBoxCell.class}, null, null, null, Theme.key_checkboxSquareUnchecked);
        themeDescriptionArr[20] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckBoxCell.class}, null, null, null, Theme.key_checkboxSquareDisabled);
        themeDescriptionArr[21] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckBoxCell.class}, null, null, null, Theme.key_checkboxSquareBackground);
        themeDescriptionArr[22] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckBoxCell.class}, null, null, null, Theme.key_checkboxSquareCheck);
        return themeDescriptionArr;
    }
}
