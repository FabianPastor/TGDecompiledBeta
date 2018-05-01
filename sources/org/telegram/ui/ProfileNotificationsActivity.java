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
import android.os.Parcelable;
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
import org.telegram.messenger.C0446R;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
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

        public void onItemClick(int i) {
            if (i == -1) {
                if (!(ProfileNotificationsActivity.this.notificationsEnabled == 0 || ProfileNotificationsActivity.this.customEnabled == 0)) {
                    i = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount).edit();
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("notify2_");
                    stringBuilder.append(ProfileNotificationsActivity.this.dialog_id);
                    i.putInt(stringBuilder.toString(), 0).commit();
                }
                ProfileNotificationsActivity.this.finishFragment();
            }
        }
    }

    private class ListAdapter extends Adapter {
        private Context context;

        public ListAdapter(Context context) {
            this.context = context;
        }

        public int getItemCount() {
            return ProfileNotificationsActivity.this.rowCount;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            switch (i) {
                case 0:
                    viewGroup = new HeaderCell(this.context);
                    viewGroup.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 1:
                    viewGroup = new TextSettingsCell(this.context);
                    viewGroup.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 2:
                    viewGroup = new TextInfoPrivacyCell(this.context);
                    break;
                case 3:
                    viewGroup = new TextColorCell(this.context);
                    viewGroup.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 4:
                    viewGroup = new RadioCell(this.context);
                    viewGroup.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                default:
                    viewGroup = new TextCheckBoxCell(this.context);
                    viewGroup.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
            }
            viewGroup.setLayoutParams(new LayoutParams(-1, -2));
            return new Holder(viewGroup);
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            ListAdapter listAdapter = this;
            ViewHolder viewHolder2 = viewHolder;
            int i2 = i;
            boolean z = true;
            boolean z2 = false;
            SharedPreferences notificationsSettings;
            String string;
            int i3;
            switch (viewHolder.getItemViewType()) {
                case 0:
                    HeaderCell headerCell = (HeaderCell) viewHolder2.itemView;
                    if (i2 == ProfileNotificationsActivity.this.generalRow) {
                        headerCell.setText(LocaleController.getString("General", C0446R.string.General));
                        return;
                    } else if (i2 == ProfileNotificationsActivity.this.popupRow) {
                        headerCell.setText(LocaleController.getString("ProfilePopupNotification", C0446R.string.ProfilePopupNotification));
                        return;
                    } else if (i2 == ProfileNotificationsActivity.this.ledRow) {
                        headerCell.setText(LocaleController.getString("NotificationsLed", C0446R.string.NotificationsLed));
                        return;
                    } else if (i2 == ProfileNotificationsActivity.this.callsRow) {
                        headerCell.setText(LocaleController.getString("VoipNotificationSettings", C0446R.string.VoipNotificationSettings));
                        return;
                    } else {
                        return;
                    }
                case 1:
                    TextSettingsCell textSettingsCell = (TextSettingsCell) viewHolder2.itemView;
                    notificationsSettings = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount);
                    StringBuilder stringBuilder;
                    if (i2 == ProfileNotificationsActivity.this.soundRow) {
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("sound_");
                        stringBuilder.append(ProfileNotificationsActivity.this.dialog_id);
                        string = notificationsSettings.getString(stringBuilder.toString(), LocaleController.getString("SoundDefault", C0446R.string.SoundDefault));
                        if (string.equals("NoSound")) {
                            string = LocaleController.getString("NoSound", C0446R.string.NoSound);
                        }
                        textSettingsCell.setTextAndValue(LocaleController.getString("Sound", C0446R.string.Sound), string, true);
                        return;
                    } else if (i2 == ProfileNotificationsActivity.this.ringtoneRow) {
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("ringtone_");
                        stringBuilder.append(ProfileNotificationsActivity.this.dialog_id);
                        string = notificationsSettings.getString(stringBuilder.toString(), LocaleController.getString("DefaultRingtone", C0446R.string.DefaultRingtone));
                        if (string.equals("NoSound")) {
                            string = LocaleController.getString("NoSound", C0446R.string.NoSound);
                        }
                        textSettingsCell.setTextAndValue(LocaleController.getString("VoipSettingsRingtone", C0446R.string.VoipSettingsRingtone), string, false);
                        return;
                    } else if (i2 == ProfileNotificationsActivity.this.vibrateRow) {
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("vibrate_");
                        stringBuilder.append(ProfileNotificationsActivity.this.dialog_id);
                        i2 = notificationsSettings.getInt(stringBuilder.toString(), 0);
                        if (i2 != 0) {
                            if (i2 != 4) {
                                if (i2 == 1) {
                                    string = LocaleController.getString("Vibrate", C0446R.string.Vibrate);
                                    r3 = LocaleController.getString("Short", C0446R.string.Short);
                                    if (ProfileNotificationsActivity.this.smartRow == -1) {
                                        if (ProfileNotificationsActivity.this.priorityRow == -1) {
                                            z = false;
                                        }
                                    }
                                    textSettingsCell.setTextAndValue(string, r3, z);
                                    return;
                                } else if (i2 == 2) {
                                    string = LocaleController.getString("Vibrate", C0446R.string.Vibrate);
                                    r3 = LocaleController.getString("VibrationDisabled", C0446R.string.VibrationDisabled);
                                    if (ProfileNotificationsActivity.this.smartRow == -1) {
                                        if (ProfileNotificationsActivity.this.priorityRow == -1) {
                                            z = false;
                                        }
                                    }
                                    textSettingsCell.setTextAndValue(string, r3, z);
                                    return;
                                } else if (i2 == 3) {
                                    string = LocaleController.getString("Vibrate", C0446R.string.Vibrate);
                                    r3 = LocaleController.getString("Long", C0446R.string.Long);
                                    if (ProfileNotificationsActivity.this.smartRow == -1) {
                                        if (ProfileNotificationsActivity.this.priorityRow == -1) {
                                            z = false;
                                        }
                                    }
                                    textSettingsCell.setTextAndValue(string, r3, z);
                                    return;
                                } else {
                                    return;
                                }
                            }
                        }
                        string = LocaleController.getString("Vibrate", C0446R.string.Vibrate);
                        r3 = LocaleController.getString("VibrationDefault", C0446R.string.VibrationDefault);
                        if (ProfileNotificationsActivity.this.smartRow == -1) {
                            if (ProfileNotificationsActivity.this.priorityRow == -1) {
                                z = false;
                            }
                        }
                        textSettingsCell.setTextAndValue(string, r3, z);
                        return;
                    } else if (i2 == ProfileNotificationsActivity.this.priorityRow) {
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("priority_");
                        stringBuilder.append(ProfileNotificationsActivity.this.dialog_id);
                        i2 = notificationsSettings.getInt(stringBuilder.toString(), 3);
                        if (i2 == 0) {
                            textSettingsCell.setTextAndValue(LocaleController.getString("NotificationsImportance", C0446R.string.NotificationsImportance), LocaleController.getString("NotificationsPriorityHigh", C0446R.string.NotificationsPriorityHigh), false);
                            return;
                        }
                        if (i2 != 1) {
                            if (i2 != 2) {
                                if (i2 == 3) {
                                    textSettingsCell.setTextAndValue(LocaleController.getString("NotificationsImportance", C0446R.string.NotificationsImportance), LocaleController.getString("NotificationsPrioritySettings", C0446R.string.NotificationsPrioritySettings), false);
                                    return;
                                } else if (i2 == 4) {
                                    textSettingsCell.setTextAndValue(LocaleController.getString("NotificationsImportance", C0446R.string.NotificationsImportance), LocaleController.getString("NotificationsPriorityLow", C0446R.string.NotificationsPriorityLow), false);
                                    return;
                                } else if (i2 == 5) {
                                    textSettingsCell.setTextAndValue(LocaleController.getString("NotificationsImportance", C0446R.string.NotificationsImportance), LocaleController.getString("NotificationsPriorityMedium", C0446R.string.NotificationsPriorityMedium), false);
                                    return;
                                } else {
                                    return;
                                }
                            }
                        }
                        textSettingsCell.setTextAndValue(LocaleController.getString("NotificationsImportance", C0446R.string.NotificationsImportance), LocaleController.getString("NotificationsPriorityUrgent", C0446R.string.NotificationsPriorityUrgent), false);
                        return;
                    } else if (i2 == ProfileNotificationsActivity.this.smartRow) {
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("smart_max_count_");
                        stringBuilder.append(ProfileNotificationsActivity.this.dialog_id);
                        i2 = notificationsSettings.getInt(stringBuilder.toString(), 2);
                        StringBuilder stringBuilder2 = new StringBuilder();
                        stringBuilder2.append("smart_delay_");
                        stringBuilder2.append(ProfileNotificationsActivity.this.dialog_id);
                        i3 = notificationsSettings.getInt(stringBuilder2.toString(), 180);
                        if (i2 == 0) {
                            string = LocaleController.getString("SmartNotifications", C0446R.string.SmartNotifications);
                            r3 = LocaleController.getString("SmartNotificationsDisabled", C0446R.string.SmartNotificationsDisabled);
                            if (ProfileNotificationsActivity.this.priorityRow == -1) {
                                z = false;
                            }
                            textSettingsCell.setTextAndValue(string, r3, z);
                            return;
                        }
                        r3 = LocaleController.formatPluralString("Minutes", i3 / 60);
                        String string2 = LocaleController.getString("SmartNotifications", C0446R.string.SmartNotifications);
                        string = LocaleController.formatString("SmartNotificationsInfo", C0446R.string.SmartNotificationsInfo, Integer.valueOf(i2), r3);
                        if (ProfileNotificationsActivity.this.priorityRow == -1) {
                            z = false;
                        }
                        textSettingsCell.setTextAndValue(string2, string, z);
                        return;
                    } else if (i2 == ProfileNotificationsActivity.this.callsVibrateRow) {
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("calls_vibrate_");
                        stringBuilder.append(ProfileNotificationsActivity.this.dialog_id);
                        i2 = notificationsSettings.getInt(stringBuilder.toString(), 0);
                        if (i2 != 0) {
                            if (i2 != 4) {
                                if (i2 == 1) {
                                    textSettingsCell.setTextAndValue(LocaleController.getString("Vibrate", C0446R.string.Vibrate), LocaleController.getString("Short", C0446R.string.Short), true);
                                    return;
                                } else if (i2 == 2) {
                                    textSettingsCell.setTextAndValue(LocaleController.getString("Vibrate", C0446R.string.Vibrate), LocaleController.getString("VibrationDisabled", C0446R.string.VibrationDisabled), true);
                                    return;
                                } else if (i2 == 3) {
                                    textSettingsCell.setTextAndValue(LocaleController.getString("Vibrate", C0446R.string.Vibrate), LocaleController.getString("Long", C0446R.string.Long), true);
                                    return;
                                } else {
                                    return;
                                }
                            }
                        }
                        textSettingsCell.setTextAndValue(LocaleController.getString("Vibrate", C0446R.string.Vibrate), LocaleController.getString("VibrationDefault", C0446R.string.VibrationDefault), true);
                        return;
                    } else {
                        return;
                    }
                case 2:
                    TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) viewHolder2.itemView;
                    if (i2 == ProfileNotificationsActivity.this.popupInfoRow) {
                        textInfoPrivacyCell.setText(LocaleController.getString("ProfilePopupNotificationInfo", C0446R.string.ProfilePopupNotificationInfo));
                        textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(listAdapter.context, C0446R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else if (i2 == ProfileNotificationsActivity.this.ledInfoRow) {
                        textInfoPrivacyCell.setText(LocaleController.getString("NotificationsLedInfo", C0446R.string.NotificationsLedInfo));
                        textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(listAdapter.context, C0446R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else if (i2 == ProfileNotificationsActivity.this.priorityInfoRow) {
                        if (ProfileNotificationsActivity.this.priorityRow == -1) {
                            textInfoPrivacyCell.setText(TtmlNode.ANONYMOUS_REGION_ID);
                        } else {
                            textInfoPrivacyCell.setText(LocaleController.getString("PriorityInfo", C0446R.string.PriorityInfo));
                        }
                        textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(listAdapter.context, C0446R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else if (i2 == ProfileNotificationsActivity.this.customInfoRow) {
                        textInfoPrivacyCell.setText(null);
                        textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(listAdapter.context, C0446R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else if (i2 == ProfileNotificationsActivity.this.ringtoneInfoRow) {
                        textInfoPrivacyCell.setText(LocaleController.getString("VoipRingtoneInfo", C0446R.string.VoipRingtoneInfo));
                        textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(listAdapter.context, C0446R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else {
                        return;
                    }
                case 3:
                    TextColorCell textColorCell = (TextColorCell) viewHolder2.itemView;
                    SharedPreferences notificationsSettings2 = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount);
                    StringBuilder stringBuilder3 = new StringBuilder();
                    stringBuilder3.append("color_");
                    stringBuilder3.append(ProfileNotificationsActivity.this.dialog_id);
                    if (notificationsSettings2.contains(stringBuilder3.toString())) {
                        stringBuilder3 = new StringBuilder();
                        stringBuilder3.append("color_");
                        stringBuilder3.append(ProfileNotificationsActivity.this.dialog_id);
                        i2 = notificationsSettings2.getInt(stringBuilder3.toString(), -16776961);
                    } else if (((int) ProfileNotificationsActivity.this.dialog_id) < 0) {
                        i2 = notificationsSettings2.getInt("GroupLed", -16776961);
                    } else {
                        i2 = notificationsSettings2.getInt("MessagesLed", -16776961);
                    }
                    for (i3 = 0; i3 < 9; i3++) {
                        if (TextColorCell.colorsToSave[i3] == i2) {
                            i2 = TextColorCell.colors[i3];
                            textColorCell.setTextAndColor(LocaleController.getString("NotificationsLedColor", C0446R.string.NotificationsLedColor), i2, false);
                            return;
                        }
                    }
                    textColorCell.setTextAndColor(LocaleController.getString("NotificationsLedColor", C0446R.string.NotificationsLedColor), i2, false);
                    return;
                case 4:
                    RadioCell radioCell = (RadioCell) viewHolder2.itemView;
                    notificationsSettings = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount);
                    StringBuilder stringBuilder4 = new StringBuilder();
                    stringBuilder4.append("popup_");
                    stringBuilder4.append(ProfileNotificationsActivity.this.dialog_id);
                    int i4 = notificationsSettings.getInt(stringBuilder4.toString(), 0);
                    if (i4 == 0) {
                        i4 = notificationsSettings.getInt(((int) ProfileNotificationsActivity.this.dialog_id) < 0 ? "popupGroup" : "popupAll", 0) != 0 ? 1 : 2;
                    }
                    if (i2 == ProfileNotificationsActivity.this.popupEnabledRow) {
                        string = LocaleController.getString("PopupEnabled", C0446R.string.PopupEnabled);
                        if (i4 == 1) {
                            z2 = true;
                        }
                        radioCell.setText(string, z2, true);
                        radioCell.setTag(Integer.valueOf(1));
                        return;
                    } else if (i2 == ProfileNotificationsActivity.this.popupDisabledRow) {
                        string = LocaleController.getString("PopupDisabled", C0446R.string.PopupDisabled);
                        if (i4 != 2) {
                            z = false;
                        }
                        radioCell.setText(string, z, false);
                        radioCell.setTag(Integer.valueOf(2));
                        return;
                    } else {
                        return;
                    }
                case 5:
                    TextCheckBoxCell textCheckBoxCell = (TextCheckBoxCell) viewHolder2.itemView;
                    MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount);
                    string = LocaleController.getString("NotificationsEnableCustom", C0446R.string.NotificationsEnableCustom);
                    if (!ProfileNotificationsActivity.this.customEnabled || !ProfileNotificationsActivity.this.notificationsEnabled) {
                        z = false;
                    }
                    textCheckBoxCell.setTextAndCheck(string, z, false);
                    return;
                default:
                    return;
            }
        }

        public void onViewAttachedToWindow(ViewHolder viewHolder) {
            if (viewHolder.getItemViewType() != 0) {
                boolean z = false;
                switch (viewHolder.getItemViewType()) {
                    case 1:
                        TextSettingsCell textSettingsCell = (TextSettingsCell) viewHolder.itemView;
                        if (ProfileNotificationsActivity.this.customEnabled && ProfileNotificationsActivity.this.notificationsEnabled) {
                            z = true;
                        }
                        textSettingsCell.setEnabled(z, null);
                        return;
                    case 2:
                        TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) viewHolder.itemView;
                        if (ProfileNotificationsActivity.this.customEnabled && ProfileNotificationsActivity.this.notificationsEnabled) {
                            z = true;
                        }
                        textInfoPrivacyCell.setEnabled(z, null);
                        return;
                    case 3:
                        TextColorCell textColorCell = (TextColorCell) viewHolder.itemView;
                        if (ProfileNotificationsActivity.this.customEnabled && ProfileNotificationsActivity.this.notificationsEnabled) {
                            z = true;
                        }
                        textColorCell.setEnabled(z, null);
                        return;
                    case 4:
                        RadioCell radioCell = (RadioCell) viewHolder.itemView;
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

        public int getItemViewType(int i) {
            if (!(i == ProfileNotificationsActivity.this.generalRow || i == ProfileNotificationsActivity.this.popupRow || i == ProfileNotificationsActivity.this.ledRow)) {
                if (i != ProfileNotificationsActivity.this.callsRow) {
                    if (!(i == ProfileNotificationsActivity.this.soundRow || i == ProfileNotificationsActivity.this.vibrateRow || i == ProfileNotificationsActivity.this.priorityRow || i == ProfileNotificationsActivity.this.smartRow || i == ProfileNotificationsActivity.this.ringtoneRow)) {
                        if (i != ProfileNotificationsActivity.this.callsVibrateRow) {
                            if (!(i == ProfileNotificationsActivity.this.popupInfoRow || i == ProfileNotificationsActivity.this.ledInfoRow || i == ProfileNotificationsActivity.this.priorityInfoRow || i == ProfileNotificationsActivity.this.customInfoRow)) {
                                if (i != ProfileNotificationsActivity.this.ringtoneInfoRow) {
                                    if (i == ProfileNotificationsActivity.this.colorRow) {
                                        return 3;
                                    }
                                    if (i != ProfileNotificationsActivity.this.popupEnabledRow) {
                                        if (i != ProfileNotificationsActivity.this.popupDisabledRow) {
                                            if (i == ProfileNotificationsActivity.this.customRow) {
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

    public ProfileNotificationsActivity(Bundle bundle) {
        super(bundle);
        this.dialog_id = bundle.getLong("dialog_id");
    }

    public boolean onFragmentCreate() {
        boolean z;
        int i;
        SharedPreferences notificationsSettings;
        StringBuilder stringBuilder;
        boolean contains;
        StringBuilder stringBuilder2;
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
        if (i2 < 0) {
            Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-i2));
            if (!(chat == null || !ChatObject.isChannel(chat) || chat.megagroup)) {
                z = true;
                if (i2 != 0 || r4) {
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
                if (i2 <= 0) {
                    i2 = this.rowCount;
                    this.rowCount = i2 + 1;
                    this.callsRow = i2;
                    i2 = this.rowCount;
                    this.rowCount = i2 + 1;
                    this.callsVibrateRow = i2;
                    i2 = this.rowCount;
                    this.rowCount = i2 + 1;
                    this.ringtoneRow = i2;
                    i2 = this.rowCount;
                    this.rowCount = i2 + 1;
                    this.ringtoneInfoRow = i2;
                } else {
                    this.callsRow = -1;
                    this.callsVibrateRow = -1;
                    this.ringtoneRow = -1;
                    this.ringtoneInfoRow = -1;
                }
                i2 = this.rowCount;
                this.rowCount = i2 + 1;
                this.ledRow = i2;
                i2 = this.rowCount;
                this.rowCount = i2 + 1;
                this.colorRow = i2;
                i2 = this.rowCount;
                this.rowCount = i2 + 1;
                this.ledInfoRow = i2;
                notificationsSettings = MessagesController.getNotificationsSettings(this.currentAccount);
                stringBuilder = new StringBuilder();
                stringBuilder.append("custom_");
                stringBuilder.append(this.dialog_id);
                this.customEnabled = notificationsSettings.getBoolean(stringBuilder.toString(), false);
                stringBuilder = new StringBuilder();
                stringBuilder.append("notify2_");
                stringBuilder.append(this.dialog_id);
                contains = notificationsSettings.contains(stringBuilder.toString());
                stringBuilder2 = new StringBuilder();
                stringBuilder2.append("notify2_");
                stringBuilder2.append(this.dialog_id);
                i = notificationsSettings.getInt(stringBuilder2.toString(), 0);
                if (i != 0) {
                    if (contains) {
                        this.notificationsEnabled = true;
                    } else if (((int) this.dialog_id) >= 0) {
                        this.notificationsEnabled = notificationsSettings.getBoolean("EnableGroup", true);
                    } else {
                        this.notificationsEnabled = notificationsSettings.getBoolean("EnableAll", true);
                    }
                } else if (i == 1) {
                    this.notificationsEnabled = true;
                } else if (i != 2) {
                    this.notificationsEnabled = false;
                } else {
                    this.notificationsEnabled = false;
                }
                NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.notificationsSettingsUpdated);
                return super.onFragmentCreate();
            }
        }
        z = false;
        if (i2 != 0) {
        }
        this.popupRow = -1;
        this.popupEnabledRow = -1;
        this.popupDisabledRow = -1;
        this.popupInfoRow = -1;
        if (i2 <= 0) {
            this.callsRow = -1;
            this.callsVibrateRow = -1;
            this.ringtoneRow = -1;
            this.ringtoneInfoRow = -1;
        } else {
            i2 = this.rowCount;
            this.rowCount = i2 + 1;
            this.callsRow = i2;
            i2 = this.rowCount;
            this.rowCount = i2 + 1;
            this.callsVibrateRow = i2;
            i2 = this.rowCount;
            this.rowCount = i2 + 1;
            this.ringtoneRow = i2;
            i2 = this.rowCount;
            this.rowCount = i2 + 1;
            this.ringtoneInfoRow = i2;
        }
        i2 = this.rowCount;
        this.rowCount = i2 + 1;
        this.ledRow = i2;
        i2 = this.rowCount;
        this.rowCount = i2 + 1;
        this.colorRow = i2;
        i2 = this.rowCount;
        this.rowCount = i2 + 1;
        this.ledInfoRow = i2;
        notificationsSettings = MessagesController.getNotificationsSettings(this.currentAccount);
        stringBuilder = new StringBuilder();
        stringBuilder.append("custom_");
        stringBuilder.append(this.dialog_id);
        this.customEnabled = notificationsSettings.getBoolean(stringBuilder.toString(), false);
        stringBuilder = new StringBuilder();
        stringBuilder.append("notify2_");
        stringBuilder.append(this.dialog_id);
        contains = notificationsSettings.contains(stringBuilder.toString());
        stringBuilder2 = new StringBuilder();
        stringBuilder2.append("notify2_");
        stringBuilder2.append(this.dialog_id);
        i = notificationsSettings.getInt(stringBuilder2.toString(), 0);
        if (i != 0) {
            if (i == 1) {
                this.notificationsEnabled = true;
            } else if (i != 2) {
                this.notificationsEnabled = false;
            } else {
                this.notificationsEnabled = false;
            }
        } else if (contains) {
            this.notificationsEnabled = true;
        } else if (((int) this.dialog_id) >= 0) {
            this.notificationsEnabled = notificationsSettings.getBoolean("EnableAll", true);
        } else {
            this.notificationsEnabled = notificationsSettings.getBoolean("EnableGroup", true);
        }
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.notificationsSettingsUpdated);
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.notificationsSettingsUpdated);
    }

    public View createView(final Context context) {
        this.actionBar.setBackButtonImage(C0446R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("CustomNotifications", C0446R.string.CustomNotifications));
        this.actionBar.setActionBarMenuOnItemClick(new C22631());
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
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
                    if (animator.equals(ProfileNotificationsActivity.this.animatorSet) != null) {
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

                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount).edit();
                    i = new StringBuilder();
                    i.append("smart_max_count_");
                    i.append(ProfileNotificationsActivity.this.dialog_id);
                    dialogInterface.putInt(i.toString(), 0).commit();
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

                public void onItemClick(View view, int i) {
                    if (i >= 0) {
                        if (i < 100) {
                            view = (i % 10) + 1;
                            i = (i / 10) + 1;
                            SharedPreferences notificationsSettings = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount);
                            Editor edit = notificationsSettings.edit();
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("smart_max_count_");
                            stringBuilder.append(ProfileNotificationsActivity.this.dialog_id);
                            edit.putInt(stringBuilder.toString(), view).commit();
                            view = notificationsSettings.edit();
                            StringBuilder stringBuilder2 = new StringBuilder();
                            stringBuilder2.append("smart_delay_");
                            stringBuilder2.append(ProfileNotificationsActivity.this.dialog_id);
                            view.putInt(stringBuilder2.toString(), i * 60).commit();
                            if (ProfileNotificationsActivity.this.adapter != null) {
                                ProfileNotificationsActivity.this.adapter.notifyItemChanged(ProfileNotificationsActivity.this.smartRow);
                            }
                            ProfileNotificationsActivity.this.dismissCurrentDialig();
                        }
                    }
                }
            }

            public void onItemClick(View view, int i) {
                int i2 = 0;
                StringBuilder stringBuilder;
                if (i == ProfileNotificationsActivity.this.customRow && (view instanceof TextCheckBoxCell)) {
                    i = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount);
                    ProfileNotificationsActivity.this.customEnabled = true ^ ProfileNotificationsActivity.this.customEnabled;
                    ProfileNotificationsActivity.this.notificationsEnabled = ProfileNotificationsActivity.this.customEnabled;
                    i = i.edit();
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("custom_");
                    stringBuilder.append(ProfileNotificationsActivity.this.dialog_id);
                    i.putBoolean(stringBuilder.toString(), ProfileNotificationsActivity.this.customEnabled).commit();
                    ((TextCheckBoxCell) view).setChecked(ProfileNotificationsActivity.this.customEnabled);
                    view = ProfileNotificationsActivity.this.listView.getChildCount();
                    i = new ArrayList();
                    while (i2 < view) {
                        Holder holder = (Holder) ProfileNotificationsActivity.this.listView.getChildViewHolder(ProfileNotificationsActivity.this.listView.getChildAt(i2));
                        int itemViewType = holder.getItemViewType();
                        if (!(holder.getAdapterPosition() == ProfileNotificationsActivity.this.customRow || itemViewType == 0)) {
                            switch (itemViewType) {
                                case 1:
                                    ((TextSettingsCell) holder.itemView).setEnabled(ProfileNotificationsActivity.this.customEnabled, i);
                                    break;
                                case 2:
                                    ((TextInfoPrivacyCell) holder.itemView).setEnabled(ProfileNotificationsActivity.this.customEnabled, i);
                                    break;
                                case 3:
                                    ((TextColorCell) holder.itemView).setEnabled(ProfileNotificationsActivity.this.customEnabled, i);
                                    break;
                                case 4:
                                    ((RadioCell) holder.itemView).setEnabled(ProfileNotificationsActivity.this.customEnabled, i);
                                    break;
                                default:
                                    break;
                            }
                        }
                        i2++;
                    }
                    if (i.isEmpty() == null) {
                        if (ProfileNotificationsActivity.this.animatorSet != null) {
                            ProfileNotificationsActivity.this.animatorSet.cancel();
                        }
                        ProfileNotificationsActivity.this.animatorSet = new AnimatorSet();
                        ProfileNotificationsActivity.this.animatorSet.playTogether(i);
                        ProfileNotificationsActivity.this.animatorSet.addListener(new C16501());
                        ProfileNotificationsActivity.this.animatorSet.setDuration(150);
                        ProfileNotificationsActivity.this.animatorSet.start();
                    }
                } else if (ProfileNotificationsActivity.this.customEnabled) {
                    Parcelable parcelable = null;
                    Uri uri;
                    String path;
                    StringBuilder stringBuilder2;
                    if (i == ProfileNotificationsActivity.this.soundRow) {
                        try {
                            view = new Intent("android.intent.action.RINGTONE_PICKER");
                            view.putExtra("android.intent.extra.ringtone.TYPE", 2);
                            view.putExtra("android.intent.extra.ringtone.SHOW_DEFAULT", true);
                            view.putExtra("android.intent.extra.ringtone.DEFAULT_URI", RingtoneManager.getDefaultUri(2));
                            i = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount);
                            uri = System.DEFAULT_NOTIFICATION_URI;
                            path = uri != null ? uri.getPath() : null;
                            stringBuilder2 = new StringBuilder();
                            stringBuilder2.append("sound_path_");
                            stringBuilder2.append(ProfileNotificationsActivity.this.dialog_id);
                            i = i.getString(stringBuilder2.toString(), path);
                            if (!(i == 0 || i.equals("NoSound"))) {
                                parcelable = i.equals(path) ? uri : Uri.parse(i);
                            }
                            view.putExtra("android.intent.extra.ringtone.EXISTING_URI", parcelable);
                            ProfileNotificationsActivity.this.startActivityForResult(view, 12);
                        } catch (Throwable e) {
                            FileLog.m3e(e);
                        }
                    } else if (i == ProfileNotificationsActivity.this.ringtoneRow) {
                        try {
                            view = new Intent("android.intent.action.RINGTONE_PICKER");
                            view.putExtra("android.intent.extra.ringtone.TYPE", 1);
                            view.putExtra("android.intent.extra.ringtone.SHOW_DEFAULT", true);
                            view.putExtra("android.intent.extra.ringtone.DEFAULT_URI", RingtoneManager.getDefaultUri(1));
                            i = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount);
                            uri = System.DEFAULT_NOTIFICATION_URI;
                            path = uri != null ? uri.getPath() : null;
                            stringBuilder2 = new StringBuilder();
                            stringBuilder2.append("ringtone_path_");
                            stringBuilder2.append(ProfileNotificationsActivity.this.dialog_id);
                            i = i.getString(stringBuilder2.toString(), path);
                            if (!(i == 0 || i.equals("NoSound"))) {
                                parcelable = i.equals(path) ? uri : Uri.parse(i);
                            }
                            view.putExtra("android.intent.extra.ringtone.EXISTING_URI", parcelable);
                            ProfileNotificationsActivity.this.startActivityForResult(view, 13);
                        } catch (Throwable e2) {
                            FileLog.m3e(e2);
                        }
                    } else if (i == ProfileNotificationsActivity.this.vibrateRow) {
                        ProfileNotificationsActivity.this.showDialog(AlertsCreator.createVibrationSelectDialog(ProfileNotificationsActivity.this.getParentActivity(), ProfileNotificationsActivity.this, ProfileNotificationsActivity.this.dialog_id, false, false, new C16512()));
                    } else if (i == ProfileNotificationsActivity.this.callsVibrateRow) {
                        ProfileNotificationsActivity.this.showDialog(AlertsCreator.createVibrationSelectDialog(ProfileNotificationsActivity.this.getParentActivity(), ProfileNotificationsActivity.this, ProfileNotificationsActivity.this.dialog_id, "calls_vibrate_", new C16523()));
                    } else if (i == ProfileNotificationsActivity.this.priorityRow) {
                        ProfileNotificationsActivity.this.showDialog(AlertsCreator.createPrioritySelectDialog(ProfileNotificationsActivity.this.getParentActivity(), ProfileNotificationsActivity.this, ProfileNotificationsActivity.this.dialog_id, false, false, new C16534()));
                    } else if (i == ProfileNotificationsActivity.this.smartRow) {
                        if (ProfileNotificationsActivity.this.getParentActivity() != null) {
                            view = ProfileNotificationsActivity.this.getParentActivity();
                            i = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount);
                            stringBuilder = new StringBuilder();
                            stringBuilder.append("smart_max_count_");
                            stringBuilder.append(ProfileNotificationsActivity.this.dialog_id);
                            int i3 = i.getInt(stringBuilder.toString(), 2);
                            StringBuilder stringBuilder3 = new StringBuilder();
                            stringBuilder3.append("smart_delay_");
                            stringBuilder3.append(ProfileNotificationsActivity.this.dialog_id);
                            i = i.getInt(stringBuilder3.toString(), 180);
                            if (i3 == 0) {
                                i3 = 2;
                            }
                            i = ((((i / 60) - 1) * 10) + i3) - 1;
                            View recyclerListView = new RecyclerListView(ProfileNotificationsActivity.this.getParentActivity());
                            recyclerListView.setLayoutManager(new LinearLayoutManager(context, 1, false));
                            recyclerListView.setClipToPadding(true);
                            recyclerListView.setAdapter(new SelectionAdapter() {
                                public int getItemCount() {
                                    return 100;
                                }

                                public boolean isEnabled(ViewHolder viewHolder) {
                                    return true;
                                }

                                public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                                    viewGroup = new TextView(view) {
                                        protected void onMeasure(int i, int i2) {
                                            super.onMeasure(MeasureSpec.makeMeasureSpec(i, NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), NUM));
                                        }
                                    };
                                    TextView textView = (TextView) viewGroup;
                                    textView.setGravity(17);
                                    textView.setTextSize(1, 18.0f);
                                    textView.setSingleLine(true);
                                    textView.setEllipsize(TruncateAt.END);
                                    textView.setLayoutParams(new LayoutParams(-1, -2));
                                    return new Holder(viewGroup);
                                }

                                public void onBindViewHolder(ViewHolder viewHolder, int i) {
                                    TextView textView = (TextView) viewHolder.itemView;
                                    textView.setTextColor(Theme.getColor(i == i ? Theme.key_dialogTextGray : Theme.key_dialogTextBlack));
                                    int i2 = i % 10;
                                    i /= 10;
                                    String formatPluralString = LocaleController.formatPluralString("Times", i2 + 1);
                                    i = LocaleController.formatPluralString("Minutes", i + 1);
                                    textView.setText(LocaleController.formatString("SmartNotificationsDetail", C0446R.string.SmartNotificationsDetail, formatPluralString, i));
                                }
                            });
                            recyclerListView.setPadding(0, AndroidUtilities.dp(12.0f), 0, AndroidUtilities.dp(NUM));
                            recyclerListView.setOnItemClickListener(new C22646());
                            view = new Builder(ProfileNotificationsActivity.this.getParentActivity());
                            view.setTitle(LocaleController.getString("SmartNotificationsAlert", C0446R.string.SmartNotificationsAlert));
                            view.setView(recyclerListView);
                            view.setPositiveButton(LocaleController.getString("Cancel", C0446R.string.Cancel), null);
                            view.setNegativeButton(LocaleController.getString("SmartNotificationsDisabled", C0446R.string.SmartNotificationsDisabled), new C16557());
                            ProfileNotificationsActivity.this.showDialog(view.create());
                        }
                    } else if (i == ProfileNotificationsActivity.this.colorRow) {
                        if (ProfileNotificationsActivity.this.getParentActivity() != null) {
                            ProfileNotificationsActivity.this.showDialog(AlertsCreator.createColorSelectDialog(ProfileNotificationsActivity.this.getParentActivity(), ProfileNotificationsActivity.this.dialog_id, false, false, new C16568()));
                        }
                    } else if (i == ProfileNotificationsActivity.this.popupEnabledRow) {
                        i = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount).edit();
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("popup_");
                        stringBuilder.append(ProfileNotificationsActivity.this.dialog_id);
                        i.putInt(stringBuilder.toString(), 1).commit();
                        ((RadioCell) view).setChecked(true, true);
                        view = ProfileNotificationsActivity.this.listView.findViewWithTag(Integer.valueOf(2));
                        if (view != null) {
                            ((RadioCell) view).setChecked(false, true);
                        }
                    } else if (i == ProfileNotificationsActivity.this.popupDisabledRow) {
                        i = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount).edit();
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("popup_");
                        stringBuilder.append(ProfileNotificationsActivity.this.dialog_id);
                        i.putInt(stringBuilder.toString(), 2).commit();
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

    public void onActivityResultFragment(int i, int i2, Intent intent) {
        if (i2 == -1 && intent != null) {
            Uri uri = (Uri) intent.getParcelableExtra("android.intent.extra.ringtone.PICKED_URI");
            intent = null;
            if (uri != null) {
                Ringtone ringtone = RingtoneManager.getRingtone(ApplicationLoader.applicationContext, uri);
                if (ringtone != null) {
                    if (i == 13) {
                        if (uri.equals(System.DEFAULT_RINGTONE_URI) != null) {
                            intent = LocaleController.getString("DefaultRingtone", C0446R.string.DefaultRingtone);
                        } else {
                            intent = ringtone.getTitle(getParentActivity());
                        }
                    } else if (uri.equals(System.DEFAULT_NOTIFICATION_URI) != null) {
                        intent = LocaleController.getString("SoundDefault", C0446R.string.SoundDefault);
                    } else {
                        intent = ringtone.getTitle(getParentActivity());
                    }
                    ringtone.stop();
                }
            }
            Editor edit = MessagesController.getNotificationsSettings(this.currentAccount).edit();
            StringBuilder stringBuilder;
            if (i == 12) {
                if (intent != null) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("sound_");
                    stringBuilder.append(this.dialog_id);
                    edit.putString(stringBuilder.toString(), intent);
                    intent = new StringBuilder();
                    intent.append("sound_path_");
                    intent.append(this.dialog_id);
                    edit.putString(intent.toString(), uri.toString());
                } else {
                    i2 = new StringBuilder();
                    i2.append("sound_");
                    i2.append(this.dialog_id);
                    edit.putString(i2.toString(), "NoSound");
                    i2 = new StringBuilder();
                    i2.append("sound_path_");
                    i2.append(this.dialog_id);
                    edit.putString(i2.toString(), "NoSound");
                }
            } else if (i == 13) {
                if (intent != null) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("ringtone_");
                    stringBuilder.append(this.dialog_id);
                    edit.putString(stringBuilder.toString(), intent);
                    intent = new StringBuilder();
                    intent.append("ringtone_path_");
                    intent.append(this.dialog_id);
                    edit.putString(intent.toString(), uri.toString());
                } else {
                    i2 = new StringBuilder();
                    i2.append("ringtone_");
                    i2.append(this.dialog_id);
                    edit.putString(i2.toString(), "NoSound");
                    i2 = new StringBuilder();
                    i2.append("ringtone_path_");
                    i2.append(this.dialog_id);
                    edit.putString(i2.toString(), "NoSound");
                }
            }
            edit.commit();
            if (this.adapter != 0) {
                this.adapter.notifyItemChanged(i == 13 ? this.ringtoneRow : this.soundRow);
            }
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.notificationsSettingsUpdated) {
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
