package org.telegram.p005ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.content.Context;
import android.content.DialogInterface;
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
import org.telegram.messenger.CLASSNAMER;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.NotificationsController;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.LayoutParams;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.p005ui.ActionBar.AlertDialog.Builder;
import org.telegram.p005ui.ActionBar.BaseFragment;
import org.telegram.p005ui.ActionBar.CLASSNAMEActionBar.ActionBarMenuOnItemClick;
import org.telegram.p005ui.ActionBar.Theme;
import org.telegram.p005ui.ActionBar.ThemeDescription;
import org.telegram.p005ui.Cells.HeaderCell;
import org.telegram.p005ui.Cells.RadioCell;
import org.telegram.p005ui.Cells.TextCheckBoxCell;
import org.telegram.p005ui.Cells.TextColorCell;
import org.telegram.p005ui.Cells.TextInfoPrivacyCell;
import org.telegram.p005ui.Cells.TextSettingsCell;
import org.telegram.p005ui.Components.AlertsCreator;
import org.telegram.p005ui.Components.LayoutHelper;
import org.telegram.p005ui.Components.RecyclerListView;
import org.telegram.p005ui.Components.RecyclerListView.Holder;
import org.telegram.p005ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.p005ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.tgnet.TLRPC.Chat;

/* renamed from: org.telegram.ui.ProfileNotificationsActivity */
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
    class CLASSNAME extends ActionBarMenuOnItemClick {
        CLASSNAME() {
        }

        public void onItemClick(int id) {
            if (id == -1) {
                if (ProfileNotificationsActivity.this.notificationsEnabled && ProfileNotificationsActivity.this.customEnabled) {
                    MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount).edit().putInt("notify2_" + ProfileNotificationsActivity.this.dialog_id, 0).commit();
                }
                ProfileNotificationsActivity.this.lambda$checkDiscard$70$PassportActivity();
            }
        }
    }

    /* renamed from: org.telegram.ui.ProfileNotificationsActivity$ListAdapter */
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
            SharedPreferences preferences;
            String string;
            boolean z;
            switch (holder.getItemViewType()) {
                case 0:
                    HeaderCell headerCell = holder.itemView;
                    if (position == ProfileNotificationsActivity.this.generalRow) {
                        headerCell.setText(LocaleController.getString("General", CLASSNAMER.string.General));
                        return;
                    } else if (position == ProfileNotificationsActivity.this.popupRow) {
                        headerCell.setText(LocaleController.getString("ProfilePopupNotification", CLASSNAMER.string.ProfilePopupNotification));
                        return;
                    } else if (position == ProfileNotificationsActivity.this.ledRow) {
                        headerCell.setText(LocaleController.getString("NotificationsLed", CLASSNAMER.string.NotificationsLed));
                        return;
                    } else if (position == ProfileNotificationsActivity.this.callsRow) {
                        headerCell.setText(LocaleController.getString("VoipNotificationSettings", CLASSNAMER.string.VoipNotificationSettings));
                        return;
                    } else {
                        return;
                    }
                case 1:
                    TextSettingsCell textCell = holder.itemView;
                    preferences = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount);
                    String value;
                    int value2;
                    if (position == ProfileNotificationsActivity.this.soundRow) {
                        value = preferences.getString("sound_" + ProfileNotificationsActivity.this.dialog_id, LocaleController.getString("SoundDefault", CLASSNAMER.string.SoundDefault));
                        if (value.equals("NoSound")) {
                            value = LocaleController.getString("NoSound", CLASSNAMER.string.NoSound);
                        }
                        textCell.setTextAndValue(LocaleController.getString("Sound", CLASSNAMER.string.Sound), value, true);
                        return;
                    } else if (position == ProfileNotificationsActivity.this.ringtoneRow) {
                        value = preferences.getString("ringtone_" + ProfileNotificationsActivity.this.dialog_id, LocaleController.getString("DefaultRingtone", CLASSNAMER.string.DefaultRingtone));
                        if (value.equals("NoSound")) {
                            value = LocaleController.getString("NoSound", CLASSNAMER.string.NoSound);
                        }
                        textCell.setTextAndValue(LocaleController.getString("VoipSettingsRingtone", CLASSNAMER.string.VoipSettingsRingtone), value, false);
                        return;
                    } else if (position == ProfileNotificationsActivity.this.vibrateRow) {
                        value2 = preferences.getInt("vibrate_" + ProfileNotificationsActivity.this.dialog_id, 0);
                        String string2;
                        if (value2 == 0 || value2 == 4) {
                            string = LocaleController.getString("Vibrate", CLASSNAMER.string.Vibrate);
                            string2 = LocaleController.getString("VibrationDefault", CLASSNAMER.string.VibrationDefault);
                            z = (ProfileNotificationsActivity.this.smartRow == -1 && ProfileNotificationsActivity.this.priorityRow == -1) ? false : true;
                            textCell.setTextAndValue(string, string2, z);
                            return;
                        } else if (value2 == 1) {
                            string = LocaleController.getString("Vibrate", CLASSNAMER.string.Vibrate);
                            string2 = LocaleController.getString("Short", CLASSNAMER.string.Short);
                            z = (ProfileNotificationsActivity.this.smartRow == -1 && ProfileNotificationsActivity.this.priorityRow == -1) ? false : true;
                            textCell.setTextAndValue(string, string2, z);
                            return;
                        } else if (value2 == 2) {
                            string = LocaleController.getString("Vibrate", CLASSNAMER.string.Vibrate);
                            string2 = LocaleController.getString("VibrationDisabled", CLASSNAMER.string.VibrationDisabled);
                            z = (ProfileNotificationsActivity.this.smartRow == -1 && ProfileNotificationsActivity.this.priorityRow == -1) ? false : true;
                            textCell.setTextAndValue(string, string2, z);
                            return;
                        } else if (value2 == 3) {
                            string = LocaleController.getString("Vibrate", CLASSNAMER.string.Vibrate);
                            string2 = LocaleController.getString("Long", CLASSNAMER.string.Long);
                            z = (ProfileNotificationsActivity.this.smartRow == -1 && ProfileNotificationsActivity.this.priorityRow == -1) ? false : true;
                            textCell.setTextAndValue(string, string2, z);
                            return;
                        } else {
                            return;
                        }
                    } else if (position == ProfileNotificationsActivity.this.priorityRow) {
                        value2 = preferences.getInt("priority_" + ProfileNotificationsActivity.this.dialog_id, 3);
                        if (value2 == 0) {
                            textCell.setTextAndValue(LocaleController.getString("NotificationsImportance", CLASSNAMER.string.NotificationsImportance), LocaleController.getString("NotificationsPriorityHigh", CLASSNAMER.string.NotificationsPriorityHigh), false);
                            return;
                        } else if (value2 == 1 || value2 == 2) {
                            textCell.setTextAndValue(LocaleController.getString("NotificationsImportance", CLASSNAMER.string.NotificationsImportance), LocaleController.getString("NotificationsPriorityUrgent", CLASSNAMER.string.NotificationsPriorityUrgent), false);
                            return;
                        } else if (value2 == 3) {
                            textCell.setTextAndValue(LocaleController.getString("NotificationsImportance", CLASSNAMER.string.NotificationsImportance), LocaleController.getString("NotificationsPrioritySettings", CLASSNAMER.string.NotificationsPrioritySettings), false);
                            return;
                        } else if (value2 == 4) {
                            textCell.setTextAndValue(LocaleController.getString("NotificationsImportance", CLASSNAMER.string.NotificationsImportance), LocaleController.getString("NotificationsPriorityLow", CLASSNAMER.string.NotificationsPriorityLow), false);
                            return;
                        } else if (value2 == 5) {
                            textCell.setTextAndValue(LocaleController.getString("NotificationsImportance", CLASSNAMER.string.NotificationsImportance), LocaleController.getString("NotificationsPriorityMedium", CLASSNAMER.string.NotificationsPriorityMedium), false);
                            return;
                        } else {
                            return;
                        }
                    } else if (position == ProfileNotificationsActivity.this.smartRow) {
                        int notifyMaxCount = preferences.getInt("smart_max_count_" + ProfileNotificationsActivity.this.dialog_id, 2);
                        int notifyDelay = preferences.getInt("smart_delay_" + ProfileNotificationsActivity.this.dialog_id, 180);
                        if (notifyMaxCount == 0) {
                            textCell.setTextAndValue(LocaleController.getString("SmartNotifications", CLASSNAMER.string.SmartNotifications), LocaleController.getString("SmartNotificationsDisabled", CLASSNAMER.string.SmartNotificationsDisabled), ProfileNotificationsActivity.this.priorityRow != -1);
                            return;
                        }
                        String minutes = LocaleController.formatPluralString("Minutes", notifyDelay / 60);
                        textCell.setTextAndValue(LocaleController.getString("SmartNotifications", CLASSNAMER.string.SmartNotifications), LocaleController.formatString("SmartNotificationsInfo", CLASSNAMER.string.SmartNotificationsInfo, Integer.valueOf(notifyMaxCount), minutes), ProfileNotificationsActivity.this.priorityRow != -1);
                        return;
                    } else if (position == ProfileNotificationsActivity.this.callsVibrateRow) {
                        value2 = preferences.getInt("calls_vibrate_" + ProfileNotificationsActivity.this.dialog_id, 0);
                        if (value2 == 0 || value2 == 4) {
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
                        } else {
                            return;
                        }
                    } else {
                        return;
                    }
                case 2:
                    TextInfoPrivacyCell textCell2 = holder.itemView;
                    if (position == ProfileNotificationsActivity.this.popupInfoRow) {
                        textCell2.setText(LocaleController.getString("ProfilePopupNotificationInfo", CLASSNAMER.string.ProfilePopupNotificationInfo));
                        textCell2.setBackgroundDrawable(Theme.getThemedDrawable(this.context, (int) CLASSNAMER.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else if (position == ProfileNotificationsActivity.this.ledInfoRow) {
                        textCell2.setText(LocaleController.getString("NotificationsLedInfo", CLASSNAMER.string.NotificationsLedInfo));
                        textCell2.setBackgroundDrawable(Theme.getThemedDrawable(this.context, (int) CLASSNAMER.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else if (position == ProfileNotificationsActivity.this.priorityInfoRow) {
                        if (ProfileNotificationsActivity.this.priorityRow == -1) {
                            textCell2.setText(TtmlNode.ANONYMOUS_REGION_ID);
                        } else {
                            textCell2.setText(LocaleController.getString("PriorityInfo", CLASSNAMER.string.PriorityInfo));
                        }
                        textCell2.setBackgroundDrawable(Theme.getThemedDrawable(this.context, (int) CLASSNAMER.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else if (position == ProfileNotificationsActivity.this.customInfoRow) {
                        textCell2.setText(null);
                        textCell2.setBackgroundDrawable(Theme.getThemedDrawable(this.context, (int) CLASSNAMER.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else if (position == ProfileNotificationsActivity.this.ringtoneInfoRow) {
                        textCell2.setText(LocaleController.getString("VoipRingtoneInfo", CLASSNAMER.string.VoipRingtoneInfo));
                        textCell2.setBackgroundDrawable(Theme.getThemedDrawable(this.context, (int) CLASSNAMER.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else {
                        return;
                    }
                case 3:
                    int color;
                    TextColorCell textCell3 = holder.itemView;
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
                            textCell3.setTextAndColor(LocaleController.getString("NotificationsLedColor", CLASSNAMER.string.NotificationsLedColor), color, false);
                            return;
                        }
                    }
                    textCell3.setTextAndColor(LocaleController.getString("NotificationsLedColor", CLASSNAMER.string.NotificationsLedColor), color, false);
                    return;
                case 4:
                    RadioCell radioCell = holder.itemView;
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
                        radioCell.setText(LocaleController.getString("PopupEnabled", CLASSNAMER.string.PopupEnabled), popup == 1, true);
                        radioCell.setTag(Integer.valueOf(1));
                        return;
                    } else if (position == ProfileNotificationsActivity.this.popupDisabledRow) {
                        radioCell.setText(LocaleController.getString("PopupDisabled", CLASSNAMER.string.PopupDisabled), popup == 2, false);
                        radioCell.setTag(Integer.valueOf(2));
                        return;
                    } else {
                        return;
                    }
                case 5:
                    TextCheckBoxCell cell = holder.itemView;
                    preferences = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount);
                    string = LocaleController.getString("NotificationsEnableCustom", CLASSNAMER.string.NotificationsEnableCustom);
                    z = ProfileNotificationsActivity.this.customEnabled && ProfileNotificationsActivity.this.notificationsEnabled;
                    cell.setTextAndCheck(string, z, false);
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
            return 0;
        }
    }

    public ProfileNotificationsActivity(Bundle args) {
        super(args);
        this.dialog_id = args.getLong("dialog_id");
    }

    public boolean onFragmentCreate() {
        this.rowCount = 0;
        int i = this.rowCount;
        this.rowCount = i + 1;
        this.customRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.customInfoRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.generalRow = i;
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
        this.customEnabled = preferences.getBoolean("custom_" + this.dialog_id, false);
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
        this.actionBar.setBackButtonImage(CLASSNAMER.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("CustomNotifications", CLASSNAMER.string.CustomNotifications));
        this.actionBar.setActionBarMenuOnItemClick(new CLASSNAME());
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
            class CLASSNAME extends AnimatorListenerAdapter {
                CLASSNAME() {
                }

                public void onAnimationEnd(Animator animator) {
                    if (animator.equals(ProfileNotificationsActivity.this.animatorSet)) {
                        ProfileNotificationsActivity.this.animatorSet = null;
                    }
                }
            }

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
                        ProfileNotificationsActivity.this.animatorSet.addListener(new CLASSNAME());
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
                        } catch (Throwable e) {
                            FileLog.m13e(e);
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
                        } catch (Throwable e2) {
                            FileLog.m13e(e2);
                        }
                    } else if (position == ProfileNotificationsActivity.this.vibrateRow) {
                        ProfileNotificationsActivity.this.showDialog(AlertsCreator.createVibrationSelectDialog(ProfileNotificationsActivity.this.getParentActivity(), ProfileNotificationsActivity.this.dialog_id, false, false, new ProfileNotificationsActivity$3$$Lambda$0(this)));
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
                                        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                                            super.onMeasure(MeasureSpec.makeMeasureSpec(widthMeasureSpec, NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.m9dp(48.0f), NUM));
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
                                    textView.setTextColor(Theme.getColor(position == i ? Theme.key_dialogTextGray : Theme.key_dialogTextBlack));
                                    int notifyDelay = position / 10;
                                    String times = LocaleController.formatPluralString("Times", (position % 10) + 1);
                                    String minutes = LocaleController.formatPluralString("Minutes", notifyDelay + 1);
                                    textView.setText(LocaleController.formatString("SmartNotificationsDetail", CLASSNAMER.string.SmartNotificationsDetail, times, minutes));
                                }
                            });
                            recyclerListView.setPadding(0, AndroidUtilities.m9dp(12.0f), 0, AndroidUtilities.m9dp(8.0f));
                            recyclerListView.setOnItemClickListener(new ProfileNotificationsActivity$3$$Lambda$3(this));
                            Builder builder = new Builder(ProfileNotificationsActivity.this.getParentActivity());
                            builder.setTitle(LocaleController.getString("SmartNotificationsAlert", CLASSNAMER.string.SmartNotificationsAlert));
                            builder.setView(recyclerListView);
                            builder.setPositiveButton(LocaleController.getString("Cancel", CLASSNAMER.string.Cancel), null);
                            builder.setNegativeButton(LocaleController.getString("SmartNotificationsDisabled", CLASSNAMER.string.SmartNotificationsDisabled), new ProfileNotificationsActivity$3$$Lambda$4(this));
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

            final /* synthetic */ void lambda$onItemClick$0$ProfileNotificationsActivity$3() {
                if (ProfileNotificationsActivity.this.adapter != null) {
                    ProfileNotificationsActivity.this.adapter.notifyItemChanged(ProfileNotificationsActivity.this.vibrateRow);
                }
            }

            final /* synthetic */ void lambda$onItemClick$1$ProfileNotificationsActivity$3() {
                if (ProfileNotificationsActivity.this.adapter != null) {
                    ProfileNotificationsActivity.this.adapter.notifyItemChanged(ProfileNotificationsActivity.this.callsVibrateRow);
                }
            }

            final /* synthetic */ void lambda$onItemClick$2$ProfileNotificationsActivity$3() {
                if (ProfileNotificationsActivity.this.adapter != null) {
                    ProfileNotificationsActivity.this.adapter.notifyItemChanged(ProfileNotificationsActivity.this.priorityRow);
                }
            }

            final /* synthetic */ void lambda$onItemClick$3$ProfileNotificationsActivity$3(View view1, int position1) {
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

            final /* synthetic */ void lambda$onItemClick$4$ProfileNotificationsActivity$3(DialogInterface dialog, int which) {
                MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount).edit().putInt("smart_max_count_" + ProfileNotificationsActivity.this.dialog_id, 0).commit();
                if (ProfileNotificationsActivity.this.adapter != null) {
                    ProfileNotificationsActivity.this.adapter.notifyItemChanged(ProfileNotificationsActivity.this.smartRow);
                }
                ProfileNotificationsActivity.this.dismissCurrentDialig();
            }

            final /* synthetic */ void lambda$onItemClick$5$ProfileNotificationsActivity$3() {
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
