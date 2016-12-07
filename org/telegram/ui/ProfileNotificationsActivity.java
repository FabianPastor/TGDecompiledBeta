package org.telegram.ui;

import android.app.AlertDialog.Builder;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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
import org.telegram.messenger.beta.R;
import org.telegram.messenger.exoplayer.C;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.TL_dialog;
import org.telegram.tgnet.TLRPC.TL_peerNotifySettings;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Adapters.BaseFragmentAdapter;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.RadioCell;
import org.telegram.ui.Cells.TextColorCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AvatarDrawable;

public class ProfileNotificationsActivity extends BaseFragment implements NotificationCenterDelegate {
    private int colorRow;
    private long dialog_id;
    private int generalRow;
    private int ledInfoRow;
    private int ledRow;
    private ListView listView;
    private int notificationsRow;
    private int popupDisabledRow;
    private int popupEnabledRow;
    private int popupInfoRow;
    private int popupRow;
    private int priorityInfoRow;
    private int priorityRow;
    private int rowCount;
    private int smartRow;
    private int soundRow;
    private int vibrateRow;

    private class ListAdapter extends BaseFragmentAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean areAllItemsEnabled() {
            return false;
        }

        public boolean isEnabled(int i) {
            return (i == ProfileNotificationsActivity.this.popupInfoRow || i == ProfileNotificationsActivity.this.ledInfoRow || i == ProfileNotificationsActivity.this.priorityInfoRow) ? false : true;
        }

        public int getCount() {
            return ProfileNotificationsActivity.this.rowCount;
        }

        public Object getItem(int i) {
            return null;
        }

        public long getItemId(int i) {
            return (long) i;
        }

        public boolean hasStableIds() {
            return false;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            View headerCell;
            SharedPreferences preferences;
            switch (getItemViewType(i)) {
                case 0:
                    if (view == null) {
                        headerCell = new HeaderCell(this.mContext);
                        headerCell.setBackgroundColor(-1);
                    }
                    HeaderCell headerCell2 = (HeaderCell) view;
                    if (i != ProfileNotificationsActivity.this.generalRow) {
                        if (i != ProfileNotificationsActivity.this.popupRow) {
                            if (i == ProfileNotificationsActivity.this.ledRow) {
                                headerCell2.setText(LocaleController.getString("NotificationsLed", R.string.NotificationsLed));
                                break;
                            }
                        }
                        headerCell2.setText(LocaleController.getString("ProfilePopupNotification", R.string.ProfilePopupNotification));
                        break;
                    }
                    headerCell2.setText(LocaleController.getString("General", R.string.General));
                    break;
                    break;
                case 1:
                    if (view == null) {
                        headerCell = new TextSettingsCell(this.mContext) {
                            public boolean onTouchEvent(MotionEvent event) {
                                if (VERSION.SDK_INT >= 21 && getBackground() != null && (event.getAction() == 0 || event.getAction() == 2)) {
                                    getBackground().setHotspot(event.getX(), event.getY());
                                }
                                return super.onTouchEvent(event);
                            }
                        };
                        headerCell.setBackgroundResource(R.drawable.list_selector_white);
                    }
                    TextSettingsCell textCell = (TextSettingsCell) view;
                    preferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
                    if (i != ProfileNotificationsActivity.this.soundRow) {
                        int value;
                        if (i != ProfileNotificationsActivity.this.vibrateRow) {
                            if (i != ProfileNotificationsActivity.this.notificationsRow) {
                                if (i != ProfileNotificationsActivity.this.priorityRow) {
                                    if (i == ProfileNotificationsActivity.this.smartRow) {
                                        int notifyMaxCount = preferences.getInt("smart_max_count_" + ProfileNotificationsActivity.this.dialog_id, 2);
                                        int notifyDelay = preferences.getInt("smart_delay_" + ProfileNotificationsActivity.this.dialog_id, 180);
                                        if (notifyMaxCount != 0) {
                                            String minutes = LocaleController.formatPluralString("Minutes", notifyDelay / 60);
                                            textCell.setTextAndValue(LocaleController.getString("SmartNotifications", R.string.SmartNotifications), LocaleController.formatString("SmartNotificationsInfo", R.string.SmartNotificationsInfo, Integer.valueOf(notifyMaxCount), minutes), ProfileNotificationsActivity.this.priorityRow != -1);
                                            break;
                                        }
                                        textCell.setTextAndValue(LocaleController.getString("SmartNotifications", R.string.SmartNotifications), LocaleController.getString("SmartNotificationsDisabled", R.string.SmartNotificationsDisabled), ProfileNotificationsActivity.this.priorityRow != -1);
                                        break;
                                    }
                                }
                                value = preferences.getInt("priority_" + ProfileNotificationsActivity.this.dialog_id, 3);
                                if (value != 0) {
                                    if (value != 1) {
                                        if (value != 2) {
                                            if (value == 3) {
                                                textCell.setTextAndValue(LocaleController.getString("NotificationsPriority", R.string.NotificationsPriority), LocaleController.getString("NotificationsPrioritySettings", R.string.NotificationsPrioritySettings), false);
                                                break;
                                            }
                                        }
                                        textCell.setTextAndValue(LocaleController.getString("NotificationsPriority", R.string.NotificationsPriority), LocaleController.getString("NotificationsPriorityMax", R.string.NotificationsPriorityMax), false);
                                        break;
                                    }
                                    textCell.setTextAndValue(LocaleController.getString("NotificationsPriority", R.string.NotificationsPriority), LocaleController.getString("NotificationsPriorityHigh", R.string.NotificationsPriorityHigh), false);
                                    break;
                                }
                                textCell.setTextAndValue(LocaleController.getString("NotificationsPriority", R.string.NotificationsPriority), LocaleController.getString("NotificationsPriorityDefault", R.string.NotificationsPriorityDefault), false);
                                break;
                            }
                            value = preferences.getInt("notify2_" + ProfileNotificationsActivity.this.dialog_id, 0);
                            if (value != 0) {
                                if (value != 1) {
                                    if (value != 2) {
                                        if (value == 3) {
                                            String val;
                                            int delta = preferences.getInt("notifyuntil_" + ProfileNotificationsActivity.this.dialog_id, 0) - ConnectionsManager.getInstance().getCurrentTime();
                                            if (delta <= 0) {
                                                val = LocaleController.getString("Enabled", R.string.Enabled);
                                            } else if (delta < 3600) {
                                                val = LocaleController.formatString("WillUnmuteIn", R.string.WillUnmuteIn, LocaleController.formatPluralString("Minutes", delta / 60));
                                            } else if (delta < 86400) {
                                                val = LocaleController.formatString("WillUnmuteIn", R.string.WillUnmuteIn, LocaleController.formatPluralString("Hours", (int) Math.ceil((double) ((((float) delta) / BitmapDescriptorFactory.HUE_YELLOW) / BitmapDescriptorFactory.HUE_YELLOW))));
                                            } else if (delta < 31536000) {
                                                val = LocaleController.formatString("WillUnmuteIn", R.string.WillUnmuteIn, LocaleController.formatPluralString("Days", (int) Math.ceil((double) (((((float) delta) / BitmapDescriptorFactory.HUE_YELLOW) / BitmapDescriptorFactory.HUE_YELLOW) / 24.0f))));
                                            } else {
                                                val = null;
                                            }
                                            if (val == null) {
                                                textCell.setTextAndValue(LocaleController.getString("Notifications", R.string.Notifications), LocaleController.getString("NotificationsDisabled", R.string.NotificationsDisabled), true);
                                                break;
                                            }
                                            textCell.setTextAndValue(LocaleController.getString("Notifications", R.string.Notifications), val, true);
                                            break;
                                        }
                                    }
                                    textCell.setTextAndValue(LocaleController.getString("Notifications", R.string.Notifications), LocaleController.getString("NotificationsDisabled", R.string.NotificationsDisabled), true);
                                    break;
                                }
                                textCell.setTextAndValue(LocaleController.getString("Notifications", R.string.Notifications), LocaleController.getString("Enabled", R.string.Enabled), true);
                                break;
                            }
                            textCell.setTextAndValue(LocaleController.getString("Notifications", R.string.Notifications), LocaleController.getString("Default", R.string.Default), true);
                            break;
                        }
                        value = preferences.getInt("vibrate_" + ProfileNotificationsActivity.this.dialog_id, 0);
                        String string;
                        String string2;
                        boolean z;
                        if (value != 0 && value != 4) {
                            if (value != 1) {
                                if (value != 2) {
                                    if (value == 3) {
                                        string = LocaleController.getString("Vibrate", R.string.Vibrate);
                                        string2 = LocaleController.getString("Long", R.string.Long);
                                        z = (ProfileNotificationsActivity.this.smartRow == -1 && ProfileNotificationsActivity.this.priorityRow == -1) ? false : true;
                                        textCell.setTextAndValue(string, string2, z);
                                        break;
                                    }
                                }
                                string = LocaleController.getString("Vibrate", R.string.Vibrate);
                                string2 = LocaleController.getString("VibrationDisabled", R.string.VibrationDisabled);
                                z = (ProfileNotificationsActivity.this.smartRow == -1 && ProfileNotificationsActivity.this.priorityRow == -1) ? false : true;
                                textCell.setTextAndValue(string, string2, z);
                                break;
                            }
                            string = LocaleController.getString("Vibrate", R.string.Vibrate);
                            string2 = LocaleController.getString("Short", R.string.Short);
                            z = (ProfileNotificationsActivity.this.smartRow == -1 && ProfileNotificationsActivity.this.priorityRow == -1) ? false : true;
                            textCell.setTextAndValue(string, string2, z);
                            break;
                        }
                        string = LocaleController.getString("Vibrate", R.string.Vibrate);
                        string2 = LocaleController.getString("VibrationDefault", R.string.VibrationDefault);
                        z = (ProfileNotificationsActivity.this.smartRow == -1 && ProfileNotificationsActivity.this.priorityRow == -1) ? false : true;
                        textCell.setTextAndValue(string, string2, z);
                        break;
                    }
                    String value2 = preferences.getString("sound_" + ProfileNotificationsActivity.this.dialog_id, LocaleController.getString("SoundDefault", R.string.SoundDefault));
                    if (value2.equals("NoSound")) {
                        value2 = LocaleController.getString("NoSound", R.string.NoSound);
                    }
                    textCell.setTextAndValue(LocaleController.getString("Sound", R.string.Sound), value2, true);
                    break;
                    break;
                case 2:
                    if (view == null) {
                        headerCell = new TextInfoPrivacyCell(this.mContext);
                    }
                    TextInfoPrivacyCell textCell2 = (TextInfoPrivacyCell) view;
                    if (i != ProfileNotificationsActivity.this.popupInfoRow) {
                        if (i != ProfileNotificationsActivity.this.ledInfoRow) {
                            if (i == ProfileNotificationsActivity.this.priorityInfoRow) {
                                if (ProfileNotificationsActivity.this.priorityRow == -1) {
                                    textCell2.setText("");
                                } else {
                                    textCell2.setText(LocaleController.getString("PriorityInfo", R.string.PriorityInfo));
                                }
                                view.setBackgroundResource(R.drawable.greydivider);
                                break;
                            }
                        }
                        textCell2.setText(LocaleController.getString("NotificationsLedInfo", R.string.NotificationsLedInfo));
                        view.setBackgroundResource(R.drawable.greydivider_bottom);
                        break;
                    }
                    textCell2.setText(LocaleController.getString("ProfilePopupNotificationInfo", R.string.ProfilePopupNotificationInfo));
                    view.setBackgroundResource(R.drawable.greydivider);
                    break;
                    break;
                case 3:
                    int color;
                    if (view == null) {
                        headerCell = new TextColorCell(this.mContext) {
                            public boolean onTouchEvent(MotionEvent event) {
                                if (VERSION.SDK_INT >= 21 && getBackground() != null && (event.getAction() == 0 || event.getAction() == 2)) {
                                    getBackground().setHotspot(event.getX(), event.getY());
                                }
                                return super.onTouchEvent(event);
                            }
                        };
                        headerCell.setBackgroundResource(R.drawable.list_selector_white);
                    }
                    TextColorCell textCell3 = (TextColorCell) view;
                    preferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
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
                            textCell3.setTextAndColor(LocaleController.getString("NotificationsLedColor", R.string.NotificationsLedColor), color, false);
                            break;
                        }
                    }
                    textCell3.setTextAndColor(LocaleController.getString("NotificationsLedColor", R.string.NotificationsLedColor), color, false);
                case 4:
                    if (view == null) {
                        headerCell = new RadioCell(this.mContext) {
                            public boolean onTouchEvent(MotionEvent event) {
                                if (VERSION.SDK_INT >= 21 && getBackground() != null && (event.getAction() == 0 || event.getAction() == 2)) {
                                    getBackground().setHotspot(event.getX(), event.getY());
                                }
                                return super.onTouchEvent(event);
                            }
                        };
                        headerCell.setBackgroundResource(R.drawable.list_selector_white);
                    }
                    RadioCell radioCell = (RadioCell) view;
                    preferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
                    int popup = preferences.getInt("popup_" + ProfileNotificationsActivity.this.dialog_id, 0);
                    if (popup == 0) {
                        if (preferences.getInt(((int) ProfileNotificationsActivity.this.dialog_id) < 0 ? "popupGroup" : "popupAll", 0) != 0) {
                            popup = 1;
                        } else {
                            popup = 2;
                        }
                    }
                    if (i != ProfileNotificationsActivity.this.popupEnabledRow) {
                        if (i == ProfileNotificationsActivity.this.popupDisabledRow) {
                            radioCell.setText(LocaleController.getString("Disabled", R.string.Disabled), popup == 2, false);
                            radioCell.setTag(Integer.valueOf(2));
                            break;
                        }
                    }
                    radioCell.setText(LocaleController.getString("Enabled", R.string.Enabled), popup == 1, true);
                    radioCell.setTag(Integer.valueOf(1));
                    break;
                    break;
            }
            return view;
        }

        public int getItemViewType(int i) {
            if (i == ProfileNotificationsActivity.this.generalRow || i == ProfileNotificationsActivity.this.popupRow || i == ProfileNotificationsActivity.this.ledRow) {
                return 0;
            }
            if (i == ProfileNotificationsActivity.this.soundRow || i == ProfileNotificationsActivity.this.vibrateRow || i == ProfileNotificationsActivity.this.notificationsRow || i == ProfileNotificationsActivity.this.priorityRow || i == ProfileNotificationsActivity.this.smartRow) {
                return 1;
            }
            if (i == ProfileNotificationsActivity.this.popupInfoRow || i == ProfileNotificationsActivity.this.ledInfoRow || i == ProfileNotificationsActivity.this.priorityInfoRow) {
                return 2;
            }
            if (i == ProfileNotificationsActivity.this.colorRow) {
                return 3;
            }
            if (i == ProfileNotificationsActivity.this.popupEnabledRow || i == ProfileNotificationsActivity.this.popupDisabledRow) {
                return 4;
            }
            return 0;
        }

        public int getViewTypeCount() {
            return 5;
        }

        public boolean isEmpty() {
            return false;
        }
    }

    public ProfileNotificationsActivity(Bundle args) {
        super(args);
        this.dialog_id = args.getLong("dialog_id");
    }

    public boolean onFragmentCreate() {
        int i = this.rowCount;
        this.rowCount = i + 1;
        this.generalRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.notificationsRow = i;
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
            i = this.rowCount;
            this.rowCount = i + 1;
            this.priorityInfoRow = i;
        } else {
            this.priorityRow = -1;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.priorityInfoRow = i;
        }
        int lower_id = (int) this.dialog_id;
        boolean isChannel;
        if (lower_id < 0) {
            Chat chat = MessagesController.getInstance().getChat(Integer.valueOf(lower_id));
            isChannel = (chat == null || !ChatObject.isChannel(chat) || chat.megagroup) ? false : true;
        } else {
            isChannel = false;
        }
        if (!(lower_id == 0 || isChannel)) {
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
        i = this.rowCount;
        this.rowCount = i + 1;
        this.ledRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.colorRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.ledInfoRow = i;
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.notificationsSettingsUpdated);
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.notificationsSettingsUpdated);
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("CustomNotifications", R.string.CustomNotifications));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    ProfileNotificationsActivity.this.finishFragment();
                }
            }
        });
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = this.fragmentView;
        frameLayout.setBackgroundColor(Theme.ACTION_BAR_MODE_SELECTOR_COLOR);
        this.listView = new ListView(context);
        this.listView.setDivider(null);
        this.listView.setDividerHeight(0);
        this.listView.setVerticalScrollBarEnabled(false);
        AndroidUtilities.setListViewEdgeEffectColor(this.listView, AvatarDrawable.getProfileBackColorForId(5));
        frameLayout.addView(this.listView);
        LayoutParams layoutParams = (LayoutParams) this.listView.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        this.listView.setLayoutParams(layoutParams);
        this.listView.setAdapter(new ListAdapter(context));
        this.listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Builder builder;
                if (i == ProfileNotificationsActivity.this.notificationsRow) {
                    if (ProfileNotificationsActivity.this.getParentActivity() != null) {
                        builder = new Builder(ProfileNotificationsActivity.this.getParentActivity());
                        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                        builder.setItems(new CharSequence[]{LocaleController.getString("Default", R.string.Default), LocaleController.getString("Enabled", R.string.Enabled), LocaleController.getString("NotificationsDisabled", R.string.NotificationsDisabled)}, new OnClickListener() {
                            public void onClick(DialogInterface d, int which) {
                                Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
                                editor.putInt("notify2_" + ProfileNotificationsActivity.this.dialog_id, which);
                                if (which == 2) {
                                    NotificationsController.getInstance().removeNotificationsForDialog(ProfileNotificationsActivity.this.dialog_id);
                                }
                                MessagesStorage.getInstance().setDialogFlags(ProfileNotificationsActivity.this.dialog_id, which == 2 ? 1 : 0);
                                editor.commit();
                                TL_dialog dialog = (TL_dialog) MessagesController.getInstance().dialogs_dict.get(Long.valueOf(ProfileNotificationsActivity.this.dialog_id));
                                if (dialog != null) {
                                    dialog.notify_settings = new TL_peerNotifySettings();
                                    if (which == 2) {
                                        dialog.notify_settings.mute_until = ConnectionsManager.DEFAULT_DATACENTER_ID;
                                    }
                                }
                                if (ProfileNotificationsActivity.this.listView != null) {
                                    ProfileNotificationsActivity.this.listView.invalidateViews();
                                }
                                NotificationsController.updateServerNotificationsSettings(ProfileNotificationsActivity.this.dialog_id);
                            }
                        });
                        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                        ProfileNotificationsActivity.this.showDialog(builder.create());
                    }
                } else if (i == ProfileNotificationsActivity.this.soundRow) {
                    try {
                        Intent intent = new Intent("android.intent.action.RINGTONE_PICKER");
                        intent.putExtra("android.intent.extra.ringtone.TYPE", 2);
                        intent.putExtra("android.intent.extra.ringtone.SHOW_DEFAULT", true);
                        intent.putExtra("android.intent.extra.ringtone.DEFAULT_URI", RingtoneManager.getDefaultUri(2));
                        preferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
                        Uri currentSound = null;
                        String defaultPath = null;
                        Uri defaultUri = System.DEFAULT_NOTIFICATION_URI;
                        if (defaultUri != null) {
                            defaultPath = defaultUri.getPath();
                        }
                        String path = preferences.getString("sound_path_" + ProfileNotificationsActivity.this.dialog_id, defaultPath);
                        if (path != null) {
                            if (!path.equals("NoSound")) {
                                currentSound = path.equals(defaultPath) ? defaultUri : Uri.parse(path);
                            }
                        }
                        intent.putExtra("android.intent.extra.ringtone.EXISTING_URI", currentSound);
                        ProfileNotificationsActivity.this.startActivityForResult(intent, 12);
                    } catch (Throwable e) {
                        FileLog.e("tmessages", e);
                    }
                } else if (i == ProfileNotificationsActivity.this.vibrateRow) {
                    builder = new Builder(ProfileNotificationsActivity.this.getParentActivity());
                    builder.setTitle(LocaleController.getString("Vibrate", R.string.Vibrate));
                    builder.setItems(new CharSequence[]{LocaleController.getString("VibrationDefault", R.string.VibrationDefault), LocaleController.getString("Short", R.string.Short), LocaleController.getString("Long", R.string.Long)}, new OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
                            if (which == 0) {
                                editor.putInt("vibrate_" + ProfileNotificationsActivity.this.dialog_id, 0);
                            } else if (which == 1) {
                                editor.putInt("vibrate_" + ProfileNotificationsActivity.this.dialog_id, 1);
                            } else if (which == 2) {
                                editor.putInt("vibrate_" + ProfileNotificationsActivity.this.dialog_id, 3);
                            }
                            editor.commit();
                            if (ProfileNotificationsActivity.this.listView != null) {
                                ProfileNotificationsActivity.this.listView.invalidateViews();
                            }
                        }
                    });
                    builder.setNeutralButton(LocaleController.getString("VibrationDisabled", R.string.VibrationDisabled), new OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit().putInt("vibrate_" + ProfileNotificationsActivity.this.dialog_id, 2).commit();
                            ProfileNotificationsActivity.this.listView.invalidateViews();
                        }
                    });
                    builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                    ProfileNotificationsActivity.this.showDialog(builder.create());
                } else if (i == ProfileNotificationsActivity.this.priorityRow) {
                    builder = new Builder(ProfileNotificationsActivity.this.getParentActivity());
                    builder.setTitle(LocaleController.getString("NotificationsPriority", R.string.NotificationsPriority));
                    builder.setItems(new CharSequence[]{LocaleController.getString("NotificationsPrioritySettings", R.string.NotificationsPrioritySettings), LocaleController.getString("NotificationsPriorityDefault", R.string.NotificationsPriorityDefault), LocaleController.getString("NotificationsPriorityHigh", R.string.NotificationsPriorityHigh), LocaleController.getString("NotificationsPriorityMax", R.string.NotificationsPriorityMax)}, new OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0) {
                                which = 3;
                            } else {
                                which--;
                            }
                            ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit().putInt("priority_" + ProfileNotificationsActivity.this.dialog_id, which).commit();
                            if (ProfileNotificationsActivity.this.listView != null) {
                                ProfileNotificationsActivity.this.listView.invalidateViews();
                            }
                        }
                    });
                    builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                    ProfileNotificationsActivity.this.showDialog(builder.create());
                } else if (i == ProfileNotificationsActivity.this.smartRow) {
                    if (ProfileNotificationsActivity.this.getParentActivity() != null) {
                        final Context context1 = ProfileNotificationsActivity.this.getParentActivity();
                        preferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
                        int notifyMaxCount = preferences.getInt("smart_max_count_" + ProfileNotificationsActivity.this.dialog_id, 2);
                        int notifyDelay = preferences.getInt("smart_delay_" + ProfileNotificationsActivity.this.dialog_id, 180);
                        if (notifyMaxCount == 0) {
                            notifyMaxCount = 2;
                        }
                        int selected = ((((notifyDelay / 60) - 1) * 10) + notifyMaxCount) - 1;
                        ListView list = new ListView(ProfileNotificationsActivity.this.getParentActivity());
                        list.setDividerHeight(0);
                        list.setClipToPadding(true);
                        list.setDivider(null);
                        final int i2 = selected;
                        list.setAdapter(new BaseFragmentAdapter() {
                            public int getCount() {
                                return 100;
                            }

                            public View getView(int i, View view, ViewGroup viewGroup) {
                                TextView textView;
                                if (view == null) {
                                    view = new TextView(context1) {
                                        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                                            super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), C.ENCODING_PCM_32BIT));
                                        }
                                    };
                                    textView = (TextView) view;
                                    textView.setGravity(17);
                                    textView.setTextSize(1, 18.0f);
                                    textView.setSingleLine(true);
                                    textView.setEllipsize(TruncateAt.END);
                                }
                                textView = (TextView) view;
                                textView.setTextColor(i == i2 ? -13333567 : -14606047);
                                int notifyDelay = i / 10;
                                String times = LocaleController.formatPluralString("Times", (i % 10) + 1);
                                String minutes = LocaleController.formatPluralString("Minutes", notifyDelay + 1);
                                textView.setText(LocaleController.formatString("SmartNotificationsDetail", R.string.SmartNotificationsDetail, times, minutes));
                                return view;
                            }
                        });
                        list.setPadding(0, AndroidUtilities.dp(12.0f), 0, AndroidUtilities.dp(8.0f));
                        list.setOnItemClickListener(new OnItemClickListener() {
                            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                                if (position >= 0 && position < 100) {
                                    int notifyMaxCount = (position % 10) + 1;
                                    int notifyDelay = (position / 10) + 1;
                                    SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
                                    preferences.edit().putInt("smart_max_count_" + ProfileNotificationsActivity.this.dialog_id, notifyMaxCount).commit();
                                    preferences.edit().putInt("smart_delay_" + ProfileNotificationsActivity.this.dialog_id, notifyDelay * 60).commit();
                                    if (ProfileNotificationsActivity.this.listView != null) {
                                        ProfileNotificationsActivity.this.listView.invalidateViews();
                                    }
                                    ProfileNotificationsActivity.this.dismissCurrentDialig();
                                }
                            }
                        });
                        builder = new Builder(ProfileNotificationsActivity.this.getParentActivity());
                        builder.setTitle(LocaleController.getString("SmartNotificationsAlert", R.string.SmartNotificationsAlert));
                        builder.setView(list);
                        builder.setPositiveButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                        builder.setNegativeButton(LocaleController.getString("SmartNotificationsDisabled", R.string.SmartNotificationsDisabled), new OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit().putInt("smart_max_count_" + ProfileNotificationsActivity.this.dialog_id, 0).commit();
                                if (ProfileNotificationsActivity.this.listView != null) {
                                    ProfileNotificationsActivity.this.listView.invalidateViews();
                                }
                                ProfileNotificationsActivity.this.dismissCurrentDialig();
                            }
                        });
                        ProfileNotificationsActivity.this.showDialog(builder.create());
                    }
                } else if (i == ProfileNotificationsActivity.this.colorRow) {
                    if (ProfileNotificationsActivity.this.getParentActivity() != null) {
                        ProfileNotificationsActivity.this.showDialog(AlertsCreator.createColorSelectDialog(ProfileNotificationsActivity.this.getParentActivity(), ProfileNotificationsActivity.this.dialog_id, false, false, new Runnable() {
                            public void run() {
                                if (ProfileNotificationsActivity.this.listView != null) {
                                    ProfileNotificationsActivity.this.listView.invalidateViews();
                                }
                            }
                        }));
                    }
                } else if (i == ProfileNotificationsActivity.this.popupEnabledRow) {
                    ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit().putInt("popup_" + ProfileNotificationsActivity.this.dialog_id, 1).commit();
                    ((RadioCell) view).setChecked(true, true);
                    view = ProfileNotificationsActivity.this.listView.findViewWithTag(Integer.valueOf(2));
                    if (view != null) {
                        ((RadioCell) view).setChecked(false, true);
                    }
                } else if (i == ProfileNotificationsActivity.this.popupDisabledRow) {
                    ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit().putInt("popup_" + ProfileNotificationsActivity.this.dialog_id, 2).commit();
                    ProfileNotificationsActivity.this.listView.invalidateViews();
                    ((RadioCell) view).setChecked(true, true);
                    view = ProfileNotificationsActivity.this.listView.findViewWithTag(Integer.valueOf(1));
                    if (view != null) {
                        ((RadioCell) view).setChecked(false, true);
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
                    if (ringtone.equals(System.DEFAULT_NOTIFICATION_URI)) {
                        name = LocaleController.getString("SoundDefault", R.string.SoundDefault);
                    } else {
                        name = rng.getTitle(getParentActivity());
                    }
                    rng.stop();
                }
            }
            Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
            if (requestCode == 12) {
                if (name != null) {
                    editor.putString("sound_" + this.dialog_id, name);
                    editor.putString("sound_path_" + this.dialog_id, ringtone.toString());
                } else {
                    editor.putString("sound_" + this.dialog_id, "NoSound");
                    editor.putString("sound_path_" + this.dialog_id, "NoSound");
                }
            }
            editor.commit();
            this.listView.invalidateViews();
        }
    }

    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationCenter.notificationsSettingsUpdated) {
            this.listView.invalidateViews();
        }
    }
}
