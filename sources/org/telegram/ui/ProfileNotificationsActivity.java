package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.Drawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings.System;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView.LayoutParams;
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
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Dialog;
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
import org.telegram.ui.Cells.UserCell2;
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
    private int previewRow;
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

    private class ListAdapter extends SelectionAdapter {
        private Context context;

        public ListAdapter(Context context) {
            this.context = context;
        }

        public int getItemCount() {
            return ProfileNotificationsActivity.this.rowCount;
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            boolean z = false;
            switch (viewHolder.getItemViewType()) {
                case 0:
                case 2:
                case 6:
                case 7:
                    break;
                case 1:
                case 3:
                case 4:
                    if (ProfileNotificationsActivity.this.customEnabled && ProfileNotificationsActivity.this.notificationsEnabled) {
                        z = true;
                        break;
                    }
                case 8:
                    TextCheckCell textCheckCell = (TextCheckCell) viewHolder.itemView;
                    if (viewHolder.getAdapterPosition() != ProfileNotificationsActivity.this.previewRow) {
                        return true;
                    }
                    if (ProfileNotificationsActivity.this.customEnabled && ProfileNotificationsActivity.this.notificationsEnabled) {
                        z = true;
                    }
                    return z;
                default:
                    return true;
            }
            return z;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View headerCell;
            View textInfoPrivacyCell;
            String str = "windowBackgroundWhite";
            switch (i) {
                case 0:
                    headerCell = new HeaderCell(this.context);
                    headerCell.setBackgroundColor(Theme.getColor(str));
                    break;
                case 1:
                    headerCell = new TextSettingsCell(this.context);
                    headerCell.setBackgroundColor(Theme.getColor(str));
                    break;
                case 2:
                    textInfoPrivacyCell = new TextInfoPrivacyCell(this.context);
                    break;
                case 3:
                    headerCell = new TextColorCell(this.context);
                    headerCell.setBackgroundColor(Theme.getColor(str));
                    break;
                case 4:
                    headerCell = new RadioCell(this.context);
                    headerCell.setBackgroundColor(Theme.getColor(str));
                    break;
                case 5:
                    headerCell = new TextCheckBoxCell(this.context);
                    headerCell.setBackgroundColor(Theme.getColor(str));
                    break;
                case 6:
                    headerCell = new UserCell2(this.context, 4, 0);
                    headerCell.setBackgroundColor(Theme.getColor(str));
                    break;
                case 7:
                    textInfoPrivacyCell = new ShadowSectionCell(this.context);
                    break;
                default:
                    headerCell = new TextCheckCell(this.context);
                    headerCell.setBackgroundColor(Theme.getColor(str));
                    break;
            }
            headerCell = textInfoPrivacyCell;
            headerCell.setLayoutParams(new LayoutParams(-1, -2));
            return new Holder(headerCell);
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            boolean z = false;
            boolean z2 = true;
            SharedPreferences notificationsSettings;
            String string;
            StringBuilder stringBuilder;
            int i2;
            switch (viewHolder.getItemViewType()) {
                case 0:
                    HeaderCell headerCell = (HeaderCell) viewHolder.itemView;
                    if (i == ProfileNotificationsActivity.this.generalRow) {
                        headerCell.setText(LocaleController.getString("General", NUM));
                        return;
                    } else if (i == ProfileNotificationsActivity.this.popupRow) {
                        headerCell.setText(LocaleController.getString("ProfilePopupNotification", NUM));
                        return;
                    } else if (i == ProfileNotificationsActivity.this.ledRow) {
                        headerCell.setText(LocaleController.getString("NotificationsLed", NUM));
                        return;
                    } else if (i == ProfileNotificationsActivity.this.callsRow) {
                        headerCell.setText(LocaleController.getString("VoipNotificationSettings", NUM));
                        return;
                    } else {
                        return;
                    }
                case 1:
                    TextSettingsCell textSettingsCell = (TextSettingsCell) viewHolder.itemView;
                    notificationsSettings = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount);
                    String str = "NoSound";
                    StringBuilder stringBuilder2;
                    if (i == ProfileNotificationsActivity.this.soundRow) {
                        stringBuilder2 = new StringBuilder();
                        stringBuilder2.append("sound_");
                        stringBuilder2.append(ProfileNotificationsActivity.this.dialog_id);
                        string = notificationsSettings.getString(stringBuilder2.toString(), LocaleController.getString("SoundDefault", NUM));
                        if (string.equals(str)) {
                            string = LocaleController.getString(str, NUM);
                        }
                        textSettingsCell.setTextAndValue(LocaleController.getString("Sound", NUM), string, true);
                        return;
                    } else if (i == ProfileNotificationsActivity.this.ringtoneRow) {
                        stringBuilder2 = new StringBuilder();
                        stringBuilder2.append("ringtone_");
                        stringBuilder2.append(ProfileNotificationsActivity.this.dialog_id);
                        string = notificationsSettings.getString(stringBuilder2.toString(), LocaleController.getString("DefaultRingtone", NUM));
                        if (string.equals(str)) {
                            string = LocaleController.getString(str, NUM);
                        }
                        textSettingsCell.setTextAndValue(LocaleController.getString("VoipSettingsRingtone", NUM), string, false);
                        return;
                    } else {
                        String str2 = "Vibrate";
                        String string2;
                        String str3;
                        if (i == ProfileNotificationsActivity.this.vibrateRow) {
                            stringBuilder2 = new StringBuilder();
                            stringBuilder2.append("vibrate_");
                            stringBuilder2.append(ProfileNotificationsActivity.this.dialog_id);
                            i = notificationsSettings.getInt(stringBuilder2.toString(), 0);
                            if (i == 0 || i == 4) {
                                string = LocaleController.getString(str2, NUM);
                                string2 = LocaleController.getString("VibrationDefault", NUM);
                                if (!(ProfileNotificationsActivity.this.smartRow == -1 && ProfileNotificationsActivity.this.priorityRow == -1)) {
                                    z = true;
                                }
                                textSettingsCell.setTextAndValue(string, string2, z);
                                return;
                            } else if (i == 1) {
                                string = LocaleController.getString(str2, NUM);
                                string2 = LocaleController.getString("Short", NUM);
                                if (!(ProfileNotificationsActivity.this.smartRow == -1 && ProfileNotificationsActivity.this.priorityRow == -1)) {
                                    z = true;
                                }
                                textSettingsCell.setTextAndValue(string, string2, z);
                                return;
                            } else if (i == 2) {
                                string = LocaleController.getString(str2, NUM);
                                string2 = LocaleController.getString("VibrationDisabled", NUM);
                                if (!(ProfileNotificationsActivity.this.smartRow == -1 && ProfileNotificationsActivity.this.priorityRow == -1)) {
                                    z = true;
                                }
                                textSettingsCell.setTextAndValue(string, string2, z);
                                return;
                            } else if (i == 3) {
                                string = LocaleController.getString(str2, NUM);
                                string2 = LocaleController.getString("Long", NUM);
                                if (!(ProfileNotificationsActivity.this.smartRow == -1 && ProfileNotificationsActivity.this.priorityRow == -1)) {
                                    z = true;
                                }
                                textSettingsCell.setTextAndValue(string, string2, z);
                                return;
                            } else {
                                return;
                            }
                        } else if (i == ProfileNotificationsActivity.this.priorityRow) {
                            stringBuilder2 = new StringBuilder();
                            stringBuilder2.append("priority_");
                            stringBuilder2.append(ProfileNotificationsActivity.this.dialog_id);
                            i = notificationsSettings.getInt(stringBuilder2.toString(), 3);
                            str3 = "NotificationsImportance";
                            if (i == 0) {
                                textSettingsCell.setTextAndValue(LocaleController.getString(str3, NUM), LocaleController.getString("NotificationsPriorityHigh", NUM), false);
                                return;
                            } else if (i == 1 || i == 2) {
                                textSettingsCell.setTextAndValue(LocaleController.getString(str3, NUM), LocaleController.getString("NotificationsPriorityUrgent", NUM), false);
                                return;
                            } else if (i == 3) {
                                textSettingsCell.setTextAndValue(LocaleController.getString(str3, NUM), LocaleController.getString("NotificationsPrioritySettings", NUM), false);
                                return;
                            } else if (i == 4) {
                                textSettingsCell.setTextAndValue(LocaleController.getString(str3, NUM), LocaleController.getString("NotificationsPriorityLow", NUM), false);
                                return;
                            } else if (i == 5) {
                                textSettingsCell.setTextAndValue(LocaleController.getString(str3, NUM), LocaleController.getString("NotificationsPriorityMedium", NUM), false);
                                return;
                            } else {
                                return;
                            }
                        } else if (i == ProfileNotificationsActivity.this.smartRow) {
                            stringBuilder2 = new StringBuilder();
                            stringBuilder2.append("smart_max_count_");
                            stringBuilder2.append(ProfileNotificationsActivity.this.dialog_id);
                            i = notificationsSettings.getInt(stringBuilder2.toString(), 2);
                            stringBuilder = new StringBuilder();
                            stringBuilder.append("smart_delay_");
                            stringBuilder.append(ProfileNotificationsActivity.this.dialog_id);
                            i2 = notificationsSettings.getInt(stringBuilder.toString(), 180);
                            if (i == 0) {
                                string = LocaleController.getString("SmartNotifications", NUM);
                                string2 = LocaleController.getString("SmartNotificationsDisabled", NUM);
                                if (ProfileNotificationsActivity.this.priorityRow != -1) {
                                    z = true;
                                }
                                textSettingsCell.setTextAndValue(string, string2, z);
                                return;
                            }
                            string2 = LocaleController.formatPluralString("Minutes", i2 / 60);
                            str3 = LocaleController.getString("SmartNotifications", NUM);
                            string = LocaleController.formatString("SmartNotificationsInfo", NUM, Integer.valueOf(i), string2);
                            if (ProfileNotificationsActivity.this.priorityRow != -1) {
                                z = true;
                            }
                            textSettingsCell.setTextAndValue(str3, string, z);
                            return;
                        } else if (i == ProfileNotificationsActivity.this.callsVibrateRow) {
                            stringBuilder2 = new StringBuilder();
                            stringBuilder2.append("calls_vibrate_");
                            stringBuilder2.append(ProfileNotificationsActivity.this.dialog_id);
                            i = notificationsSettings.getInt(stringBuilder2.toString(), 0);
                            if (i == 0 || i == 4) {
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
                            } else {
                                return;
                            }
                        } else {
                            return;
                        }
                    }
                case 2:
                    TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) viewHolder.itemView;
                    String str4 = "windowBackgroundGrayShadow";
                    if (i == ProfileNotificationsActivity.this.popupInfoRow) {
                        textInfoPrivacyCell.setText(LocaleController.getString("ProfilePopupNotificationInfo", NUM));
                        textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.context, NUM, str4));
                        return;
                    } else if (i == ProfileNotificationsActivity.this.ledInfoRow) {
                        textInfoPrivacyCell.setText(LocaleController.getString("NotificationsLedInfo", NUM));
                        textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.context, NUM, str4));
                        return;
                    } else if (i == ProfileNotificationsActivity.this.priorityInfoRow) {
                        if (ProfileNotificationsActivity.this.priorityRow == -1) {
                            textInfoPrivacyCell.setText("");
                        } else {
                            textInfoPrivacyCell.setText(LocaleController.getString("PriorityInfo", NUM));
                        }
                        textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.context, NUM, str4));
                        return;
                    } else if (i == ProfileNotificationsActivity.this.customInfoRow) {
                        textInfoPrivacyCell.setText(null);
                        textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.context, NUM, str4));
                        return;
                    } else if (i == ProfileNotificationsActivity.this.ringtoneInfoRow) {
                        textInfoPrivacyCell.setText(LocaleController.getString("VoipRingtoneInfo", NUM));
                        textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.context, NUM, str4));
                        return;
                    } else {
                        return;
                    }
                case 3:
                    TextColorCell textColorCell = (TextColorCell) viewHolder.itemView;
                    SharedPreferences notificationsSettings2 = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount);
                    StringBuilder stringBuilder3 = new StringBuilder();
                    stringBuilder3.append("color_");
                    stringBuilder3.append(ProfileNotificationsActivity.this.dialog_id);
                    if (notificationsSettings2.contains(stringBuilder3.toString())) {
                        stringBuilder3 = new StringBuilder();
                        stringBuilder3.append("color_");
                        stringBuilder3.append(ProfileNotificationsActivity.this.dialog_id);
                        i = notificationsSettings2.getInt(stringBuilder3.toString(), -16776961);
                    } else if (((int) ProfileNotificationsActivity.this.dialog_id) < 0) {
                        i = notificationsSettings2.getInt("GroupLed", -16776961);
                    } else {
                        i = notificationsSettings2.getInt("MessagesLed", -16776961);
                    }
                    for (i2 = 0; i2 < 9; i2++) {
                        if (TextColorCell.colorsToSave[i2] == i) {
                            i = TextColorCell.colors[i2];
                            textColorCell.setTextAndColor(LocaleController.getString("NotificationsLedColor", NUM), i, false);
                            return;
                        }
                    }
                    textColorCell.setTextAndColor(LocaleController.getString("NotificationsLedColor", NUM), i, false);
                    return;
                case 4:
                    RadioCell radioCell = (RadioCell) viewHolder.itemView;
                    notificationsSettings = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount);
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("popup_");
                    stringBuilder.append(ProfileNotificationsActivity.this.dialog_id);
                    int i3 = notificationsSettings.getInt(stringBuilder.toString(), 0);
                    if (i3 == 0) {
                        i3 = notificationsSettings.getInt(((int) ProfileNotificationsActivity.this.dialog_id) < 0 ? "popupGroup" : "popupAll", 0) != 0 ? 1 : 2;
                    }
                    if (i == ProfileNotificationsActivity.this.popupEnabledRow) {
                        string = LocaleController.getString("PopupEnabled", NUM);
                        if (i3 == 1) {
                            z = true;
                        }
                        radioCell.setText(string, z, true);
                        radioCell.setTag(Integer.valueOf(1));
                        return;
                    } else if (i == ProfileNotificationsActivity.this.popupDisabledRow) {
                        string = LocaleController.getString("PopupDisabled", NUM);
                        if (i3 != 2) {
                            z2 = false;
                        }
                        radioCell.setText(string, z2, false);
                        radioCell.setTag(Integer.valueOf(2));
                        return;
                    } else {
                        return;
                    }
                case 5:
                    TextCheckBoxCell textCheckBoxCell = (TextCheckBoxCell) viewHolder.itemView;
                    MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount);
                    string = LocaleController.getString("NotificationsEnableCustom", NUM);
                    if (!(ProfileNotificationsActivity.this.customEnabled && ProfileNotificationsActivity.this.notificationsEnabled)) {
                        z2 = false;
                    }
                    textCheckBoxCell.setTextAndCheck(string, z2, false);
                    return;
                case 6:
                    TLObject user;
                    UserCell2 userCell2 = (UserCell2) viewHolder.itemView;
                    i = (int) ProfileNotificationsActivity.this.dialog_id;
                    if (i > 0) {
                        user = MessagesController.getInstance(ProfileNotificationsActivity.this.currentAccount).getUser(Integer.valueOf(i));
                    } else {
                        user = MessagesController.getInstance(ProfileNotificationsActivity.this.currentAccount).getChat(Integer.valueOf(-i));
                    }
                    userCell2.setData(user, null, null, 0);
                    return;
                case 8:
                    TextCheckCell textCheckCell = (TextCheckCell) viewHolder.itemView;
                    notificationsSettings = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount);
                    if (i == ProfileNotificationsActivity.this.enableRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("Notifications", NUM), ProfileNotificationsActivity.this.notificationsEnabled, true);
                        return;
                    } else if (i == ProfileNotificationsActivity.this.previewRow) {
                        string = LocaleController.getString("MessagePreview", NUM);
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("content_preview_");
                        stringBuilder.append(ProfileNotificationsActivity.this.dialog_id);
                        textCheckCell.setTextAndCheck(string, notificationsSettings.getBoolean(stringBuilder.toString(), true), true);
                        return;
                    } else {
                        return;
                    }
                default:
                    return;
            }
        }

        public void onViewAttachedToWindow(ViewHolder viewHolder) {
            if (viewHolder.getItemViewType() != 0) {
                int itemViewType = viewHolder.getItemViewType();
                boolean z = false;
                if (itemViewType == 1) {
                    TextSettingsCell textSettingsCell = (TextSettingsCell) viewHolder.itemView;
                    if (ProfileNotificationsActivity.this.customEnabled && ProfileNotificationsActivity.this.notificationsEnabled) {
                        z = true;
                    }
                    textSettingsCell.setEnabled(z, null);
                } else if (itemViewType == 2) {
                    TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) viewHolder.itemView;
                    if (ProfileNotificationsActivity.this.customEnabled && ProfileNotificationsActivity.this.notificationsEnabled) {
                        z = true;
                    }
                    textInfoPrivacyCell.setEnabled(z, null);
                } else if (itemViewType == 3) {
                    TextColorCell textColorCell = (TextColorCell) viewHolder.itemView;
                    if (ProfileNotificationsActivity.this.customEnabled && ProfileNotificationsActivity.this.notificationsEnabled) {
                        z = true;
                    }
                    textColorCell.setEnabled(z, null);
                } else if (itemViewType == 4) {
                    RadioCell radioCell = (RadioCell) viewHolder.itemView;
                    if (ProfileNotificationsActivity.this.customEnabled && ProfileNotificationsActivity.this.notificationsEnabled) {
                        z = true;
                    }
                    radioCell.setEnabled(z, null);
                } else if (itemViewType == 8) {
                    TextCheckCell textCheckCell = (TextCheckCell) viewHolder.itemView;
                    if (viewHolder.getAdapterPosition() == ProfileNotificationsActivity.this.previewRow) {
                        if (ProfileNotificationsActivity.this.customEnabled && ProfileNotificationsActivity.this.notificationsEnabled) {
                            z = true;
                        }
                        textCheckCell.setEnabled(z, null);
                        return;
                    }
                    textCheckCell.setEnabled(true, null);
                }
            }
        }

        public int getItemViewType(int i) {
            if (i == ProfileNotificationsActivity.this.generalRow || i == ProfileNotificationsActivity.this.popupRow || i == ProfileNotificationsActivity.this.ledRow || i == ProfileNotificationsActivity.this.callsRow) {
                return 0;
            }
            if (i == ProfileNotificationsActivity.this.soundRow || i == ProfileNotificationsActivity.this.vibrateRow || i == ProfileNotificationsActivity.this.priorityRow || i == ProfileNotificationsActivity.this.smartRow || i == ProfileNotificationsActivity.this.ringtoneRow || i == ProfileNotificationsActivity.this.callsVibrateRow) {
                return 1;
            }
            if (i == ProfileNotificationsActivity.this.popupInfoRow || i == ProfileNotificationsActivity.this.ledInfoRow || i == ProfileNotificationsActivity.this.priorityInfoRow || i == ProfileNotificationsActivity.this.customInfoRow || i == ProfileNotificationsActivity.this.ringtoneInfoRow) {
                return 2;
            }
            if (i == ProfileNotificationsActivity.this.colorRow) {
                return 3;
            }
            if (i == ProfileNotificationsActivity.this.popupEnabledRow || i == ProfileNotificationsActivity.this.popupDisabledRow) {
                return 4;
            }
            if (i == ProfileNotificationsActivity.this.customRow) {
                return 5;
            }
            if (i == ProfileNotificationsActivity.this.avatarRow) {
                return 6;
            }
            if (i == ProfileNotificationsActivity.this.avatarSectionRow) {
                return 7;
            }
            if (i == ProfileNotificationsActivity.this.enableRow || i == ProfileNotificationsActivity.this.previewRow) {
                return 8;
            }
            return 0;
        }
    }

    public ProfileNotificationsActivity(Bundle bundle) {
        super(bundle);
        this.dialog_id = bundle.getLong("dialog_id");
        this.addingException = bundle.getBoolean("exception", false);
    }

    /* JADX WARNING: Removed duplicated region for block: B:34:0x0103  */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x00e2  */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x018e  */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x017a  */
    public boolean onFragmentCreate() {
        /*
        r8 = this;
        r0 = 0;
        r8.rowCount = r0;
        r1 = r8.addingException;
        r2 = -1;
        if (r1 == 0) goto L_0x001d;
    L_0x0008:
        r1 = r8.rowCount;
        r3 = r1 + 1;
        r8.rowCount = r3;
        r8.avatarRow = r1;
        r1 = r8.rowCount;
        r3 = r1 + 1;
        r8.rowCount = r3;
        r8.avatarSectionRow = r1;
        r8.customRow = r2;
        r8.customInfoRow = r2;
        goto L_0x0031;
    L_0x001d:
        r8.avatarRow = r2;
        r8.avatarSectionRow = r2;
        r1 = r8.rowCount;
        r3 = r1 + 1;
        r8.rowCount = r3;
        r8.customRow = r1;
        r1 = r8.rowCount;
        r3 = r1 + 1;
        r8.rowCount = r3;
        r8.customInfoRow = r1;
    L_0x0031:
        r1 = r8.rowCount;
        r3 = r1 + 1;
        r8.rowCount = r3;
        r8.generalRow = r1;
        r1 = r8.addingException;
        if (r1 == 0) goto L_0x0046;
    L_0x003d:
        r1 = r8.rowCount;
        r3 = r1 + 1;
        r8.rowCount = r3;
        r8.enableRow = r1;
        goto L_0x0048;
    L_0x0046:
        r8.enableRow = r2;
    L_0x0048:
        r3 = r8.dialog_id;
        r1 = (int) r3;
        if (r1 == 0) goto L_0x0056;
    L_0x004d:
        r1 = r8.rowCount;
        r3 = r1 + 1;
        r8.rowCount = r3;
        r8.previewRow = r1;
        goto L_0x0058;
    L_0x0056:
        r8.previewRow = r2;
    L_0x0058:
        r1 = r8.rowCount;
        r3 = r1 + 1;
        r8.rowCount = r3;
        r8.soundRow = r1;
        r1 = r8.rowCount;
        r3 = r1 + 1;
        r8.rowCount = r3;
        r8.vibrateRow = r1;
        r3 = r8.dialog_id;
        r1 = (int) r3;
        if (r1 >= 0) goto L_0x0076;
    L_0x006d:
        r1 = r8.rowCount;
        r3 = r1 + 1;
        r8.rowCount = r3;
        r8.smartRow = r1;
        goto L_0x0078;
    L_0x0076:
        r8.smartRow = r2;
    L_0x0078:
        r1 = android.os.Build.VERSION.SDK_INT;
        r3 = 21;
        if (r1 < r3) goto L_0x0087;
    L_0x007e:
        r1 = r8.rowCount;
        r3 = r1 + 1;
        r8.rowCount = r3;
        r8.priorityRow = r1;
        goto L_0x0089;
    L_0x0087:
        r8.priorityRow = r2;
    L_0x0089:
        r1 = r8.rowCount;
        r3 = r1 + 1;
        r8.rowCount = r3;
        r8.priorityInfoRow = r1;
        r3 = r8.dialog_id;
        r1 = (int) r3;
        r3 = 1;
        if (r1 >= 0) goto L_0x00b2;
    L_0x0097:
        r4 = r8.currentAccount;
        r4 = org.telegram.messenger.MessagesController.getInstance(r4);
        r5 = -r1;
        r5 = java.lang.Integer.valueOf(r5);
        r4 = r4.getChat(r5);
        r5 = org.telegram.messenger.ChatObject.isChannel(r4);
        if (r5 == 0) goto L_0x00b2;
    L_0x00ac:
        r4 = r4.megagroup;
        if (r4 != 0) goto L_0x00b2;
    L_0x00b0:
        r4 = 1;
        goto L_0x00b3;
    L_0x00b2:
        r4 = 0;
    L_0x00b3:
        if (r1 == 0) goto L_0x00d8;
    L_0x00b5:
        if (r4 != 0) goto L_0x00d8;
    L_0x00b7:
        r4 = r8.rowCount;
        r5 = r4 + 1;
        r8.rowCount = r5;
        r8.popupRow = r4;
        r4 = r8.rowCount;
        r5 = r4 + 1;
        r8.rowCount = r5;
        r8.popupEnabledRow = r4;
        r4 = r8.rowCount;
        r5 = r4 + 1;
        r8.rowCount = r5;
        r8.popupDisabledRow = r4;
        r4 = r8.rowCount;
        r5 = r4 + 1;
        r8.rowCount = r5;
        r8.popupInfoRow = r4;
        goto L_0x00e0;
    L_0x00d8:
        r8.popupRow = r2;
        r8.popupEnabledRow = r2;
        r8.popupDisabledRow = r2;
        r8.popupInfoRow = r2;
    L_0x00e0:
        if (r1 <= 0) goto L_0x0103;
    L_0x00e2:
        r1 = r8.rowCount;
        r2 = r1 + 1;
        r8.rowCount = r2;
        r8.callsRow = r1;
        r1 = r8.rowCount;
        r2 = r1 + 1;
        r8.rowCount = r2;
        r8.callsVibrateRow = r1;
        r1 = r8.rowCount;
        r2 = r1 + 1;
        r8.rowCount = r2;
        r8.ringtoneRow = r1;
        r1 = r8.rowCount;
        r2 = r1 + 1;
        r8.rowCount = r2;
        r8.ringtoneInfoRow = r1;
        goto L_0x010b;
    L_0x0103:
        r8.callsRow = r2;
        r8.callsVibrateRow = r2;
        r8.ringtoneRow = r2;
        r8.ringtoneInfoRow = r2;
    L_0x010b:
        r1 = r8.rowCount;
        r2 = r1 + 1;
        r8.rowCount = r2;
        r8.ledRow = r1;
        r1 = r8.rowCount;
        r2 = r1 + 1;
        r8.rowCount = r2;
        r8.colorRow = r1;
        r1 = r8.rowCount;
        r2 = r1 + 1;
        r8.rowCount = r2;
        r8.ledInfoRow = r1;
        r1 = r8.currentAccount;
        r1 = org.telegram.messenger.MessagesController.getNotificationsSettings(r1);
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r4 = "custom_";
        r2.append(r4);
        r4 = r8.dialog_id;
        r2.append(r4);
        r2 = r2.toString();
        r2 = r1.getBoolean(r2, r0);
        if (r2 != 0) goto L_0x0149;
    L_0x0142:
        r2 = r8.addingException;
        if (r2 == 0) goto L_0x0147;
    L_0x0146:
        goto L_0x0149;
    L_0x0147:
        r2 = 0;
        goto L_0x014a;
    L_0x0149:
        r2 = 1;
    L_0x014a:
        r8.customEnabled = r2;
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r4 = "notify2_";
        r2.append(r4);
        r5 = r8.dialog_id;
        r2.append(r5);
        r2 = r2.toString();
        r2 = r1.contains(r2);
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r5.append(r4);
        r6 = r8.dialog_id;
        r5.append(r6);
        r4 = r5.toString();
        r1 = r1.getInt(r4, r0);
        if (r1 != 0) goto L_0x018e;
    L_0x017a:
        if (r2 == 0) goto L_0x017f;
    L_0x017c:
        r8.notificationsEnabled = r3;
        goto L_0x019b;
    L_0x017f:
        r0 = r8.currentAccount;
        r0 = org.telegram.messenger.NotificationsController.getInstance(r0);
        r1 = r8.dialog_id;
        r0 = r0.isGlobalNotificationsEnabled(r1);
        r8.notificationsEnabled = r0;
        goto L_0x019b;
    L_0x018e:
        if (r1 != r3) goto L_0x0193;
    L_0x0190:
        r8.notificationsEnabled = r3;
        goto L_0x019b;
    L_0x0193:
        r2 = 2;
        if (r1 != r2) goto L_0x0199;
    L_0x0196:
        r8.notificationsEnabled = r0;
        goto L_0x019b;
    L_0x0199:
        r8.notificationsEnabled = r0;
    L_0x019b:
        r0 = r8.currentAccount;
        r0 = org.telegram.messenger.NotificationCenter.getInstance(r0);
        r1 = org.telegram.messenger.NotificationCenter.notificationsSettingsUpdated;
        r0.addObserver(r8, r1);
        r0 = super.onFragmentCreate();
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ProfileNotificationsActivity.onFragmentCreate():boolean");
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.notificationsSettingsUpdated);
    }

    public View createView(final Context context) {
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                String str = "notify2_";
                StringBuilder stringBuilder;
                if (i == -1) {
                    if (!ProfileNotificationsActivity.this.addingException && ProfileNotificationsActivity.this.notificationsEnabled && ProfileNotificationsActivity.this.customEnabled) {
                        Editor edit = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount).edit();
                        stringBuilder = new StringBuilder();
                        stringBuilder.append(str);
                        stringBuilder.append(ProfileNotificationsActivity.this.dialog_id);
                        edit.putInt(stringBuilder.toString(), 0).commit();
                    }
                } else if (i == 1) {
                    SharedPreferences notificationsSettings = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount);
                    Editor edit2 = notificationsSettings.edit();
                    StringBuilder stringBuilder2 = new StringBuilder();
                    stringBuilder2.append("custom_");
                    stringBuilder2.append(ProfileNotificationsActivity.this.dialog_id);
                    edit2.putBoolean(stringBuilder2.toString(), true);
                    Dialog dialog = (Dialog) MessagesController.getInstance(ProfileNotificationsActivity.this.currentAccount).dialogs_dict.get(ProfileNotificationsActivity.this.dialog_id);
                    StringBuilder stringBuilder3;
                    if (ProfileNotificationsActivity.this.notificationsEnabled) {
                        stringBuilder3 = new StringBuilder();
                        stringBuilder3.append(str);
                        stringBuilder3.append(ProfileNotificationsActivity.this.dialog_id);
                        edit2.putInt(stringBuilder3.toString(), 0);
                        MessagesStorage.getInstance(ProfileNotificationsActivity.this.currentAccount).setDialogFlags(ProfileNotificationsActivity.this.dialog_id, 0);
                        if (dialog != null) {
                            dialog.notify_settings = new TL_peerNotifySettings();
                        }
                    } else {
                        stringBuilder3 = new StringBuilder();
                        stringBuilder3.append(str);
                        stringBuilder3.append(ProfileNotificationsActivity.this.dialog_id);
                        edit2.putInt(stringBuilder3.toString(), 2);
                        NotificationsController.getInstance(ProfileNotificationsActivity.this.currentAccount).removeNotificationsForDialog(ProfileNotificationsActivity.this.dialog_id);
                        MessagesStorage.getInstance(ProfileNotificationsActivity.this.currentAccount).setDialogFlags(ProfileNotificationsActivity.this.dialog_id, 1);
                        if (dialog != null) {
                            dialog.notify_settings = new TL_peerNotifySettings();
                            dialog.notify_settings.mute_until = Integer.MAX_VALUE;
                        }
                    }
                    edit2.commit();
                    NotificationsController.getInstance(ProfileNotificationsActivity.this.currentAccount).updateServerNotificationsSettings(ProfileNotificationsActivity.this.dialog_id);
                    if (ProfileNotificationsActivity.this.delegate != null) {
                        NotificationException notificationException = new NotificationException();
                        notificationException.did = ProfileNotificationsActivity.this.dialog_id;
                        notificationException.hasCustom = true;
                        stringBuilder = new StringBuilder();
                        stringBuilder.append(str);
                        stringBuilder.append(ProfileNotificationsActivity.this.dialog_id);
                        notificationException.notify = notificationsSettings.getInt(stringBuilder.toString(), 0);
                        if (notificationException.notify != 0) {
                            StringBuilder stringBuilder4 = new StringBuilder();
                            stringBuilder4.append("notifyuntil_");
                            stringBuilder4.append(ProfileNotificationsActivity.this.dialog_id);
                            notificationException.muteUntil = notificationsSettings.getInt(stringBuilder4.toString(), 0);
                        }
                        ProfileNotificationsActivity.this.delegate.didCreateNewException(notificationException);
                    }
                }
                ProfileNotificationsActivity.this.finishFragment();
            }
        });
        if (this.addingException) {
            this.actionBar.setTitle(LocaleController.getString("NotificationsNewException", NUM));
            this.actionBar.createMenu().addItem(1, LocaleController.getString("Done", NUM).toUpperCase());
        } else {
            this.actionBar.setTitle(LocaleController.getString("CustomNotifications", NUM));
        }
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
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
            public void onItemClick(View view, int i) {
                SharedPreferences notificationsSettings;
                Editor edit;
                StringBuilder stringBuilder;
                if (i == ProfileNotificationsActivity.this.customRow && (view instanceof TextCheckBoxCell)) {
                    notificationsSettings = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount);
                    ProfileNotificationsActivity profileNotificationsActivity = ProfileNotificationsActivity.this;
                    profileNotificationsActivity.customEnabled = 1 ^ profileNotificationsActivity.customEnabled;
                    profileNotificationsActivity = ProfileNotificationsActivity.this;
                    profileNotificationsActivity.notificationsEnabled = profileNotificationsActivity.customEnabled;
                    edit = notificationsSettings.edit();
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("custom_");
                    stringBuilder.append(ProfileNotificationsActivity.this.dialog_id);
                    edit.putBoolean(stringBuilder.toString(), ProfileNotificationsActivity.this.customEnabled).commit();
                    ((TextCheckBoxCell) view).setChecked(ProfileNotificationsActivity.this.customEnabled);
                    ProfileNotificationsActivity.this.checkRowsEnabled();
                } else if (ProfileNotificationsActivity.this.customEnabled && view.isEnabled()) {
                    String str = "NoSound";
                    String str2 = "android.intent.extra.ringtone.EXISTING_URI";
                    String str3 = "android.intent.extra.ringtone.DEFAULT_URI";
                    String str4 = "android.intent.extra.ringtone.SHOW_SILENT";
                    String str5 = "android.intent.extra.ringtone.SHOW_DEFAULT";
                    String str6 = "android.intent.extra.ringtone.TYPE";
                    String str7 = "android.intent.action.RINGTONE_PICKER";
                    Parcelable parcelable = null;
                    Intent intent;
                    Uri uri;
                    String path;
                    StringBuilder stringBuilder2;
                    String string;
                    ProfileNotificationsActivity profileNotificationsActivity2;
                    TextCheckCell textCheckCell;
                    if (i == ProfileNotificationsActivity.this.soundRow) {
                        try {
                            intent = new Intent(str7);
                            intent.putExtra(str6, 2);
                            intent.putExtra(str5, true);
                            intent.putExtra(str4, true);
                            intent.putExtra(str3, RingtoneManager.getDefaultUri(2));
                            notificationsSettings = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount);
                            uri = System.DEFAULT_NOTIFICATION_URI;
                            path = uri != null ? uri.getPath() : null;
                            stringBuilder2 = new StringBuilder();
                            stringBuilder2.append("sound_path_");
                            stringBuilder2.append(ProfileNotificationsActivity.this.dialog_id);
                            string = notificationsSettings.getString(stringBuilder2.toString(), path);
                            if (!(string == null || string.equals(str))) {
                                parcelable = string.equals(path) ? uri : Uri.parse(string);
                            }
                            intent.putExtra(str2, parcelable);
                            ProfileNotificationsActivity.this.startActivityForResult(intent, 12);
                        } catch (Exception e) {
                            FileLog.e(e);
                        }
                    } else if (i == ProfileNotificationsActivity.this.ringtoneRow) {
                        try {
                            intent = new Intent(str7);
                            intent.putExtra(str6, 1);
                            intent.putExtra(str5, true);
                            intent.putExtra(str4, true);
                            intent.putExtra(str3, RingtoneManager.getDefaultUri(1));
                            notificationsSettings = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount);
                            uri = System.DEFAULT_NOTIFICATION_URI;
                            path = uri != null ? uri.getPath() : null;
                            stringBuilder2 = new StringBuilder();
                            stringBuilder2.append("ringtone_path_");
                            stringBuilder2.append(ProfileNotificationsActivity.this.dialog_id);
                            string = notificationsSettings.getString(stringBuilder2.toString(), path);
                            if (!(string == null || string.equals(str))) {
                                parcelable = string.equals(path) ? uri : Uri.parse(string);
                            }
                            intent.putExtra(str2, parcelable);
                            ProfileNotificationsActivity.this.startActivityForResult(intent, 13);
                        } catch (Exception e2) {
                            FileLog.e(e2);
                        }
                    } else if (i == ProfileNotificationsActivity.this.vibrateRow) {
                        profileNotificationsActivity2 = ProfileNotificationsActivity.this;
                        profileNotificationsActivity2.showDialog(AlertsCreator.createVibrationSelectDialog(profileNotificationsActivity2.getParentActivity(), ProfileNotificationsActivity.this.dialog_id, false, false, new -$$Lambda$ProfileNotificationsActivity$3$2IDon9XZ3wORVvesLoqeJ8GuKLA(this)));
                    } else if (i == ProfileNotificationsActivity.this.enableRow) {
                        textCheckCell = (TextCheckCell) view;
                        ProfileNotificationsActivity.this.notificationsEnabled = textCheckCell.isChecked() ^ 1;
                        textCheckCell.setChecked(ProfileNotificationsActivity.this.notificationsEnabled);
                        ProfileNotificationsActivity.this.checkRowsEnabled();
                    } else if (i == ProfileNotificationsActivity.this.previewRow) {
                        textCheckCell = (TextCheckCell) view;
                        edit = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount).edit();
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("content_preview_");
                        stringBuilder.append(ProfileNotificationsActivity.this.dialog_id);
                        edit.putBoolean(stringBuilder.toString(), textCheckCell.isChecked() ^ 1).commit();
                        textCheckCell.setChecked(textCheckCell.isChecked() ^ 1);
                    } else if (i == ProfileNotificationsActivity.this.callsVibrateRow) {
                        profileNotificationsActivity2 = ProfileNotificationsActivity.this;
                        profileNotificationsActivity2.showDialog(AlertsCreator.createVibrationSelectDialog(profileNotificationsActivity2.getParentActivity(), ProfileNotificationsActivity.this.dialog_id, "calls_vibrate_", new -$$Lambda$ProfileNotificationsActivity$3$PrZXv2W2aGcQ-SHFSnntK6Tp69w(this)));
                    } else if (i == ProfileNotificationsActivity.this.priorityRow) {
                        profileNotificationsActivity2 = ProfileNotificationsActivity.this;
                        profileNotificationsActivity2.showDialog(AlertsCreator.createPrioritySelectDialog(profileNotificationsActivity2.getParentActivity(), ProfileNotificationsActivity.this.dialog_id, -1, new -$$Lambda$ProfileNotificationsActivity$3$mwfzE5pr44nJotHZxBv3FvA5wOc(this)));
                    } else if (i == ProfileNotificationsActivity.this.smartRow) {
                        if (ProfileNotificationsActivity.this.getParentActivity() != null) {
                            final Activity parentActivity = ProfileNotificationsActivity.this.getParentActivity();
                            notificationsSettings = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount);
                            stringBuilder = new StringBuilder();
                            stringBuilder.append("smart_max_count_");
                            stringBuilder.append(ProfileNotificationsActivity.this.dialog_id);
                            int i2 = notificationsSettings.getInt(stringBuilder.toString(), 2);
                            StringBuilder stringBuilder3 = new StringBuilder();
                            stringBuilder3.append("smart_delay_");
                            stringBuilder3.append(ProfileNotificationsActivity.this.dialog_id);
                            i = notificationsSettings.getInt(stringBuilder3.toString(), 180);
                            if (i2 == 0) {
                                i2 = 2;
                            }
                            i = ((((i / 60) - 1) * 10) + i2) - 1;
                            RecyclerListView recyclerListView = new RecyclerListView(ProfileNotificationsActivity.this.getParentActivity());
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
                                    AnonymousClass1 anonymousClass1 = new TextView(parentActivity) {
                                        /* Access modifiers changed, original: protected */
                                        public void onMeasure(int i, int i2) {
                                            super.onMeasure(MeasureSpec.makeMeasureSpec(i, NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), NUM));
                                        }
                                    };
                                    anonymousClass1.setGravity(17);
                                    anonymousClass1.setTextSize(1, 18.0f);
                                    anonymousClass1.setSingleLine(true);
                                    anonymousClass1.setEllipsize(TruncateAt.END);
                                    anonymousClass1.setLayoutParams(new LayoutParams(-1, -2));
                                    return new Holder(anonymousClass1);
                                }

                                public void onBindViewHolder(ViewHolder viewHolder, int i) {
                                    TextView textView = (TextView) viewHolder.itemView;
                                    textView.setTextColor(Theme.getColor(i == i ? "dialogTextGray" : "dialogTextBlack"));
                                    int i2 = i % 10;
                                    i /= 10;
                                    String formatPluralString = LocaleController.formatPluralString("Times", i2 + 1);
                                    String formatPluralString2 = LocaleController.formatPluralString("Minutes", i + 1);
                                    textView.setText(LocaleController.formatString("SmartNotificationsDetail", NUM, formatPluralString, formatPluralString2));
                                }
                            });
                            recyclerListView.setPadding(0, AndroidUtilities.dp(12.0f), 0, AndroidUtilities.dp(8.0f));
                            recyclerListView.setOnItemClickListener(new -$$Lambda$ProfileNotificationsActivity$3$NllPggjTkVFXgR--HalhnaOhR0w(this));
                            Builder builder = new Builder(ProfileNotificationsActivity.this.getParentActivity());
                            builder.setTitle(LocaleController.getString("SmartNotificationsAlert", NUM));
                            builder.setView(recyclerListView);
                            builder.setPositiveButton(LocaleController.getString("Cancel", NUM), null);
                            builder.setNegativeButton(LocaleController.getString("SmartNotificationsDisabled", NUM), new -$$Lambda$ProfileNotificationsActivity$3$C5kBfrMiInE1eRjHc3NVVjNb77Q(this));
                            ProfileNotificationsActivity.this.showDialog(builder.create());
                        }
                    } else if (i != ProfileNotificationsActivity.this.colorRow) {
                        str = "popup_";
                        if (i == ProfileNotificationsActivity.this.popupEnabledRow) {
                            edit = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount).edit();
                            stringBuilder = new StringBuilder();
                            stringBuilder.append(str);
                            stringBuilder.append(ProfileNotificationsActivity.this.dialog_id);
                            edit.putInt(stringBuilder.toString(), 1).commit();
                            ((RadioCell) view).setChecked(true, true);
                            view = ProfileNotificationsActivity.this.listView.findViewWithTag(Integer.valueOf(2));
                            if (view != null) {
                                ((RadioCell) view).setChecked(false, true);
                            }
                        } else if (i == ProfileNotificationsActivity.this.popupDisabledRow) {
                            edit = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount).edit();
                            stringBuilder = new StringBuilder();
                            stringBuilder.append(str);
                            stringBuilder.append(ProfileNotificationsActivity.this.dialog_id);
                            edit.putInt(stringBuilder.toString(), 2).commit();
                            ((RadioCell) view).setChecked(true, true);
                            view = ProfileNotificationsActivity.this.listView.findViewWithTag(Integer.valueOf(1));
                            if (view != null) {
                                ((RadioCell) view).setChecked(false, true);
                            }
                        }
                    } else if (ProfileNotificationsActivity.this.getParentActivity() != null) {
                        profileNotificationsActivity2 = ProfileNotificationsActivity.this;
                        profileNotificationsActivity2.showDialog(AlertsCreator.createColorSelectDialog(profileNotificationsActivity2.getParentActivity(), ProfileNotificationsActivity.this.dialog_id, -1, new -$$Lambda$ProfileNotificationsActivity$3$9hbVVL7iBMMo3IfpTFOUhufLFBM(this)));
                    }
                }
            }

            public /* synthetic */ void lambda$onItemClick$0$ProfileNotificationsActivity$3() {
                if (ProfileNotificationsActivity.this.adapter != null) {
                    ProfileNotificationsActivity.this.adapter.notifyItemChanged(ProfileNotificationsActivity.this.vibrateRow);
                }
            }

            public /* synthetic */ void lambda$onItemClick$1$ProfileNotificationsActivity$3() {
                if (ProfileNotificationsActivity.this.adapter != null) {
                    ProfileNotificationsActivity.this.adapter.notifyItemChanged(ProfileNotificationsActivity.this.callsVibrateRow);
                }
            }

            public /* synthetic */ void lambda$onItemClick$2$ProfileNotificationsActivity$3() {
                if (ProfileNotificationsActivity.this.adapter != null) {
                    ProfileNotificationsActivity.this.adapter.notifyItemChanged(ProfileNotificationsActivity.this.priorityRow);
                }
            }

            public /* synthetic */ void lambda$onItemClick$3$ProfileNotificationsActivity$3(View view, int i) {
                if (i >= 0 && i < 100) {
                    int i2 = (i % 10) + 1;
                    i = (i / 10) + 1;
                    SharedPreferences notificationsSettings = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount);
                    Editor edit = notificationsSettings.edit();
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("smart_max_count_");
                    stringBuilder.append(ProfileNotificationsActivity.this.dialog_id);
                    edit.putInt(stringBuilder.toString(), i2).commit();
                    Editor edit2 = notificationsSettings.edit();
                    StringBuilder stringBuilder2 = new StringBuilder();
                    stringBuilder2.append("smart_delay_");
                    stringBuilder2.append(ProfileNotificationsActivity.this.dialog_id);
                    edit2.putInt(stringBuilder2.toString(), i * 60).commit();
                    if (ProfileNotificationsActivity.this.adapter != null) {
                        ProfileNotificationsActivity.this.adapter.notifyItemChanged(ProfileNotificationsActivity.this.smartRow);
                    }
                    ProfileNotificationsActivity.this.dismissCurrentDialig();
                }
            }

            public /* synthetic */ void lambda$onItemClick$4$ProfileNotificationsActivity$3(DialogInterface dialogInterface, int i) {
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

            public /* synthetic */ void lambda$onItemClick$5$ProfileNotificationsActivity$3() {
                if (ProfileNotificationsActivity.this.adapter != null) {
                    ProfileNotificationsActivity.this.adapter.notifyItemChanged(ProfileNotificationsActivity.this.colorRow);
                }
            }
        });
        return this.fragmentView;
    }

    public void onActivityResultFragment(int i, int i2, Intent intent) {
        if (i2 == -1 && intent != null) {
            Uri uri = (Uri) intent.getParcelableExtra("android.intent.extra.ringtone.PICKED_URI");
            String str = null;
            if (uri != null) {
                Ringtone ringtone = RingtoneManager.getRingtone(ApplicationLoader.applicationContext, uri);
                if (ringtone != null) {
                    if (i == 13) {
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
            String str2 = "NoSound";
            String str3;
            String str4;
            StringBuilder stringBuilder;
            StringBuilder stringBuilder2;
            StringBuilder stringBuilder3;
            if (i == 12) {
                str3 = "sound_path_";
                str4 = "sound_";
                if (str != null) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(str4);
                    stringBuilder.append(this.dialog_id);
                    edit.putString(stringBuilder.toString(), str);
                    stringBuilder2 = new StringBuilder();
                    stringBuilder2.append(str3);
                    stringBuilder2.append(this.dialog_id);
                    edit.putString(stringBuilder2.toString(), uri.toString());
                } else {
                    stringBuilder3 = new StringBuilder();
                    stringBuilder3.append(str4);
                    stringBuilder3.append(this.dialog_id);
                    edit.putString(stringBuilder3.toString(), str2);
                    stringBuilder3 = new StringBuilder();
                    stringBuilder3.append(str3);
                    stringBuilder3.append(this.dialog_id);
                    edit.putString(stringBuilder3.toString(), str2);
                }
            } else if (i == 13) {
                str3 = "ringtone_path_";
                str4 = "ringtone_";
                if (str != null) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(str4);
                    stringBuilder.append(this.dialog_id);
                    edit.putString(stringBuilder.toString(), str);
                    stringBuilder2 = new StringBuilder();
                    stringBuilder2.append(str3);
                    stringBuilder2.append(this.dialog_id);
                    edit.putString(stringBuilder2.toString(), uri.toString());
                } else {
                    stringBuilder3 = new StringBuilder();
                    stringBuilder3.append(str4);
                    stringBuilder3.append(this.dialog_id);
                    edit.putString(stringBuilder3.toString(), str2);
                    stringBuilder3 = new StringBuilder();
                    stringBuilder3.append(str3);
                    stringBuilder3.append(this.dialog_id);
                    edit.putString(stringBuilder3.toString(), str2);
                }
            }
            edit.commit();
            ListAdapter listAdapter = this.adapter;
            if (listAdapter != null) {
                listAdapter.notifyItemChanged(i == 13 ? this.ringtoneRow : this.soundRow);
            }
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.notificationsSettingsUpdated) {
            this.adapter.notifyDataSetChanged();
        }
    }

    public void setDelegate(ProfileNotificationsActivityDelegate profileNotificationsActivityDelegate) {
        this.delegate = profileNotificationsActivityDelegate;
    }

    private void checkRowsEnabled() {
        int childCount = this.listView.getChildCount();
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < childCount; i++) {
            Holder holder = (Holder) this.listView.getChildViewHolder(this.listView.getChildAt(i));
            int itemViewType = holder.getItemViewType();
            int adapterPosition = holder.getAdapterPosition();
            if (!(adapterPosition == this.customRow || adapterPosition == this.enableRow || itemViewType == 0)) {
                boolean z = true;
                if (itemViewType == 1) {
                    TextSettingsCell textSettingsCell = (TextSettingsCell) holder.itemView;
                    if (!(this.customEnabled && this.notificationsEnabled)) {
                        z = false;
                    }
                    textSettingsCell.setEnabled(z, arrayList);
                } else if (itemViewType == 2) {
                    TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) holder.itemView;
                    if (!(this.customEnabled && this.notificationsEnabled)) {
                        z = false;
                    }
                    textInfoPrivacyCell.setEnabled(z, arrayList);
                } else if (itemViewType == 3) {
                    TextColorCell textColorCell = (TextColorCell) holder.itemView;
                    if (!(this.customEnabled && this.notificationsEnabled)) {
                        z = false;
                    }
                    textColorCell.setEnabled(z, arrayList);
                } else if (itemViewType == 4) {
                    RadioCell radioCell = (RadioCell) holder.itemView;
                    if (!(this.customEnabled && this.notificationsEnabled)) {
                        z = false;
                    }
                    radioCell.setEnabled(z, arrayList);
                } else if (itemViewType == 8 && adapterPosition == this.previewRow) {
                    TextCheckCell textCheckCell = (TextCheckCell) holder.itemView;
                    if (!(this.customEnabled && this.notificationsEnabled)) {
                        z = false;
                    }
                    textCheckCell.setEnabled(z, arrayList);
                }
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
                    if (animator.equals(ProfileNotificationsActivity.this.animatorSet)) {
                        ProfileNotificationsActivity.this.animatorSet = null;
                    }
                }
            });
            this.animatorSet.setDuration(150);
            this.animatorSet.start();
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        -$$Lambda$ProfileNotificationsActivity$7A70wCk-sskw_wSSS5mOEgsRl6M -__lambda_profilenotificationsactivity_7a70wck-sskw_wsss5moegsrl6m = new -$$Lambda$ProfileNotificationsActivity$7A70wCk-sskw_wSSS5mOEgsRl6M(this);
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[39];
        themeDescriptionArr[0] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{HeaderCell.class, TextSettingsCell.class, TextColorCell.class, RadioCell.class, UserCell2.class, TextCheckCell.class, TextCheckBoxCell.class}, null, null, null, "windowBackgroundWhite");
        themeDescriptionArr[1] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundGray");
        themeDescriptionArr[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
        themeDescriptionArr[3] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault");
        themeDescriptionArr[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
        themeDescriptionArr[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
        themeDescriptionArr[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
        themeDescriptionArr[7] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
        themeDescriptionArr[8] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, "divider");
        View view = this.listView;
        View view2 = view;
        themeDescriptionArr[9] = new ThemeDescription(view2, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, "windowBackgroundGrayShadow");
        view = this.listView;
        Class[] clsArr = new Class[]{HeaderCell.class};
        String[] strArr = new String[1];
        strArr[0] = "textView";
        themeDescriptionArr[10] = new ThemeDescription(view, 0, clsArr, strArr, null, null, null, "windowBackgroundWhiteBlueHeader");
        themeDescriptionArr[11] = new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        view = this.listView;
        clsArr = new Class[]{TextSettingsCell.class};
        strArr = new String[1];
        strArr[0] = "valueTextView";
        themeDescriptionArr[12] = new ThemeDescription(view, 0, clsArr, strArr, null, null, null, "windowBackgroundWhiteValueText");
        themeDescriptionArr[13] = new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteGrayText4");
        themeDescriptionArr[14] = new ThemeDescription(this.listView, 0, new Class[]{TextColorCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        themeDescriptionArr[15] = new ThemeDescription(this.listView, 0, new Class[]{RadioCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        view = this.listView;
        int i = ThemeDescription.FLAG_CHECKBOX;
        clsArr = new Class[]{RadioCell.class};
        strArr = new String[1];
        strArr[0] = "radioButton";
        themeDescriptionArr[16] = new ThemeDescription(view, i, clsArr, strArr, null, null, null, "radioBackground");
        view = this.listView;
        view2 = view;
        themeDescriptionArr[17] = new ThemeDescription(view2, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{RadioCell.class}, new String[]{"radioButton"}, null, null, null, "radioBackgroundChecked");
        view = this.listView;
        view2 = view;
        themeDescriptionArr[18] = new ThemeDescription(view2, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, "windowBackgroundGrayShadow");
        themeDescriptionArr[19] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        themeDescriptionArr[20] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"valueTextView"}, null, null, null, "windowBackgroundWhiteGrayText2");
        view = this.listView;
        clsArr = new Class[]{TextCheckCell.class};
        strArr = new String[1];
        strArr[0] = "checkBox";
        themeDescriptionArr[21] = new ThemeDescription(view, 0, clsArr, strArr, null, null, null, "switchTrack");
        themeDescriptionArr[22] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, "switchTrackChecked");
        themeDescriptionArr[23] = new ThemeDescription(this.listView, 0, new Class[]{UserCell2.class}, new String[]{"nameTextView"}, null, null, null, "windowBackgroundWhiteBlackText");
        ThemeDescriptionDelegate themeDescriptionDelegate = -__lambda_profilenotificationsactivity_7a70wck-sskw_wsss5moegsrl6m;
        themeDescriptionArr[24] = new ThemeDescription(this.listView, 0, new Class[]{UserCell2.class}, new String[]{"statusColor"}, null, null, themeDescriptionDelegate, "windowBackgroundWhiteGrayText");
        themeDescriptionArr[25] = new ThemeDescription(this.listView, 0, new Class[]{UserCell2.class}, new String[]{"statusOnlineColor"}, null, null, themeDescriptionDelegate, "windowBackgroundWhiteBlueText");
        themeDescriptionArr[26] = new ThemeDescription(this.listView, 0, new Class[]{UserCell2.class}, null, new Drawable[]{Theme.avatar_savedDrawable}, null, "avatar_text");
        -$$Lambda$ProfileNotificationsActivity$7A70wCk-sskw_wSSS5mOEgsRl6M -__lambda_profilenotificationsactivity_7a70wck-sskw_wsss5moegsrl6m2 = -__lambda_profilenotificationsactivity_7a70wck-sskw_wsss5moegsrl6m;
        themeDescriptionArr[27] = new ThemeDescription(null, 0, null, null, null, -__lambda_profilenotificationsactivity_7a70wck-sskw_wsss5moegsrl6m2, "avatar_backgroundRed");
        themeDescriptionArr[28] = new ThemeDescription(null, 0, null, null, null, -__lambda_profilenotificationsactivity_7a70wck-sskw_wsss5moegsrl6m2, "avatar_backgroundOrange");
        themeDescriptionArr[29] = new ThemeDescription(null, 0, null, null, null, -__lambda_profilenotificationsactivity_7a70wck-sskw_wsss5moegsrl6m2, "avatar_backgroundViolet");
        themeDescriptionArr[30] = new ThemeDescription(null, 0, null, null, null, -__lambda_profilenotificationsactivity_7a70wck-sskw_wsss5moegsrl6m2, "avatar_backgroundGreen");
        themeDescriptionArr[31] = new ThemeDescription(null, 0, null, null, null, -__lambda_profilenotificationsactivity_7a70wck-sskw_wsss5moegsrl6m2, "avatar_backgroundCyan");
        themeDescriptionArr[32] = new ThemeDescription(null, 0, null, null, null, -__lambda_profilenotificationsactivity_7a70wck-sskw_wsss5moegsrl6m2, "avatar_backgroundBlue");
        themeDescriptionArr[33] = new ThemeDescription(null, 0, null, null, null, -__lambda_profilenotificationsactivity_7a70wck-sskw_wsss5moegsrl6m2, "avatar_backgroundPink");
        themeDescriptionArr[34] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckBoxCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        themeDescriptionArr[35] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckBoxCell.class}, null, null, null, "checkboxSquareUnchecked");
        themeDescriptionArr[36] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckBoxCell.class}, null, null, null, "checkboxSquareDisabled");
        themeDescriptionArr[37] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckBoxCell.class}, null, null, null, "checkboxSquareBackground");
        themeDescriptionArr[38] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckBoxCell.class}, null, null, null, "checkboxSquareCheck");
        return themeDescriptionArr;
    }

    public /* synthetic */ void lambda$getThemeDescriptions$0$ProfileNotificationsActivity() {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            int childCount = recyclerListView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = this.listView.getChildAt(i);
                if (childAt instanceof UserCell2) {
                    ((UserCell2) childAt).update(0);
                }
            }
        }
    }
}
