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
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.NotificationsController;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC.TL_dialog;
import org.telegram.tgnet.TLRPC.TL_peerNotifySettings;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Adapters.BaseFragmentAdapter;
import org.telegram.ui.Cells.TextColorCell;
import org.telegram.ui.Cells.TextDetailSettingsCell;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.ColorPickerView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.NumberPicker;

public class ProfileNotificationsActivity extends BaseFragment implements NotificationCenterDelegate {
    private long dialog_id;
    private ListView listView;
    private int rowCount = 0;
    private int settingsLedRow;
    private int settingsNotificationsRow;
    private int settingsPriorityRow;
    private int settingsSoundRow;
    private int settingsVibrateRow;
    private int smartRow;

    private class ListAdapter extends BaseFragmentAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean areAllItemsEnabled() {
            return true;
        }

        public boolean isEnabled(int i) {
            return true;
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
            int type = getItemViewType(i);
            View textDetailSettingsCell;
            SharedPreferences preferences;
            if (type == 0) {
                if (view == null) {
                    textDetailSettingsCell = new TextDetailSettingsCell(this.mContext);
                }
                TextDetailSettingsCell textCell = (TextDetailSettingsCell) view;
                preferences = this.mContext.getSharedPreferences("Notifications", 0);
                int value;
                if (i == ProfileNotificationsActivity.this.settingsVibrateRow) {
                    value = preferences.getInt("vibrate_" + ProfileNotificationsActivity.this.dialog_id, 0);
                    if (value == 0) {
                        textCell.setTextAndValue(LocaleController.getString("Vibrate", R.string.Vibrate), LocaleController.getString("SettingsDefault", R.string.SettingsDefault), true);
                    } else if (value == 1) {
                        textCell.setTextAndValue(LocaleController.getString("Vibrate", R.string.Vibrate), LocaleController.getString("Short", R.string.Short), true);
                    } else if (value == 2) {
                        textCell.setTextAndValue(LocaleController.getString("Vibrate", R.string.Vibrate), LocaleController.getString("VibrationDisabled", R.string.VibrationDisabled), true);
                    } else if (value == 3) {
                        textCell.setTextAndValue(LocaleController.getString("Vibrate", R.string.Vibrate), LocaleController.getString("Long", R.string.Long), true);
                    } else if (value == 4) {
                        textCell.setTextAndValue(LocaleController.getString("Vibrate", R.string.Vibrate), LocaleController.getString("SystemDefault", R.string.SystemDefault), true);
                    }
                } else if (i == ProfileNotificationsActivity.this.settingsNotificationsRow) {
                    value = preferences.getInt("notify2_" + ProfileNotificationsActivity.this.dialog_id, 0);
                    if (value == 0) {
                        textCell.setTextAndValue(LocaleController.getString("Notifications", R.string.Notifications), LocaleController.getString("Default", R.string.Default), true);
                    } else if (value == 1) {
                        textCell.setTextAndValue(LocaleController.getString("Notifications", R.string.Notifications), LocaleController.getString("Enabled", R.string.Enabled), true);
                    } else if (value == 2) {
                        textCell.setTextAndValue(LocaleController.getString("Notifications", R.string.Notifications), LocaleController.getString("NotificationsDisabled", R.string.NotificationsDisabled), true);
                    } else if (value == 3) {
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
                        if (val != null) {
                            textCell.setTextAndValue(LocaleController.getString("Notifications", R.string.Notifications), val, true);
                        } else {
                            textCell.setTextAndValue(LocaleController.getString("Notifications", R.string.Notifications), LocaleController.getString("NotificationsDisabled", R.string.NotificationsDisabled), true);
                        }
                    }
                } else if (i == ProfileNotificationsActivity.this.settingsSoundRow) {
                    String value2 = preferences.getString("sound_" + ProfileNotificationsActivity.this.dialog_id, LocaleController.getString("SoundDefault", R.string.SoundDefault));
                    if (value2.equals("NoSound")) {
                        value2 = LocaleController.getString("NoSound", R.string.NoSound);
                    }
                    textCell.setTextAndValue(LocaleController.getString("Sound", R.string.Sound), value2, true);
                } else if (i == ProfileNotificationsActivity.this.settingsPriorityRow) {
                    value = preferences.getInt("priority_" + ProfileNotificationsActivity.this.dialog_id, 3);
                    if (value == 0) {
                        textCell.setTextAndValue(LocaleController.getString("NotificationsPriority", R.string.NotificationsPriority), LocaleController.getString("NotificationsPriorityDefault", R.string.NotificationsPriorityDefault), true);
                    } else if (value == 1) {
                        textCell.setTextAndValue(LocaleController.getString("NotificationsPriority", R.string.NotificationsPriority), LocaleController.getString("NotificationsPriorityHigh", R.string.NotificationsPriorityHigh), true);
                    } else if (value == 2) {
                        textCell.setTextAndValue(LocaleController.getString("NotificationsPriority", R.string.NotificationsPriority), LocaleController.getString("NotificationsPriorityMax", R.string.NotificationsPriorityMax), true);
                    } else if (value == 3) {
                        textCell.setTextAndValue(LocaleController.getString("NotificationsPriority", R.string.NotificationsPriority), LocaleController.getString("SettingsDefault", R.string.SettingsDefault), true);
                    }
                } else if (i == ProfileNotificationsActivity.this.smartRow) {
                    int notifyMaxCount = preferences.getInt("smart_max_count_" + ProfileNotificationsActivity.this.dialog_id, 2);
                    int notifyDelay = preferences.getInt("smart_delay_" + ProfileNotificationsActivity.this.dialog_id, 180);
                    if (notifyMaxCount == 0) {
                        textCell.setTextAndValue(LocaleController.getString("SmartNotifications", R.string.SmartNotifications), LocaleController.getString("SmartNotificationsDisabled", R.string.SmartNotificationsDisabled), true);
                    } else {
                        String times = LocaleController.formatPluralString("Times", notifyMaxCount);
                        String minutes = LocaleController.formatPluralString("Minutes", notifyDelay / 60);
                        textCell.setTextAndValue(LocaleController.getString("SmartNotifications", R.string.SmartNotifications), LocaleController.formatString("SmartNotificationsInfo", R.string.SmartNotificationsInfo, times, minutes), true);
                    }
                }
            } else if (type == 1) {
                if (view == null) {
                    textDetailSettingsCell = new TextColorCell(this.mContext);
                }
                TextColorCell textCell2 = (TextColorCell) view;
                preferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
                if (preferences.contains("color_" + ProfileNotificationsActivity.this.dialog_id)) {
                    textCell2.setTextAndColor(LocaleController.getString("LedColor", R.string.LedColor), preferences.getInt("color_" + ProfileNotificationsActivity.this.dialog_id, -16711936), false);
                } else if (((int) ProfileNotificationsActivity.this.dialog_id) < 0) {
                    textCell2.setTextAndColor(LocaleController.getString("LedColor", R.string.LedColor), preferences.getInt("GroupLed", -16711936), false);
                } else {
                    textCell2.setTextAndColor(LocaleController.getString("LedColor", R.string.LedColor), preferences.getInt("MessagesLed", -16711936), false);
                }
            }
            return view;
        }

        public int getItemViewType(int i) {
            if (i == ProfileNotificationsActivity.this.settingsNotificationsRow || i == ProfileNotificationsActivity.this.settingsVibrateRow || i == ProfileNotificationsActivity.this.settingsSoundRow || i == ProfileNotificationsActivity.this.settingsPriorityRow || i == ProfileNotificationsActivity.this.smartRow || i != ProfileNotificationsActivity.this.settingsLedRow) {
                return 0;
            }
            return 1;
        }

        public int getViewTypeCount() {
            return 2;
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
        this.settingsNotificationsRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.settingsVibrateRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.settingsSoundRow = i;
        if (VERSION.SDK_INT >= 21) {
            i = this.rowCount;
            this.rowCount = i + 1;
            this.settingsPriorityRow = i;
        } else {
            this.settingsPriorityRow = -1;
        }
        if (((int) this.dialog_id) < 0) {
            i = this.rowCount;
            this.rowCount = i + 1;
            this.smartRow = i;
        } else {
            this.smartRow = 1;
        }
        i = this.rowCount;
        this.rowCount = i + 1;
        this.settingsLedRow = i;
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
        this.actionBar.setTitle(LocaleController.getString("NotificationsAndSounds", R.string.NotificationsAndSounds));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    ProfileNotificationsActivity.this.finishFragment();
                }
            }
        });
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = this.fragmentView;
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
                if (i == ProfileNotificationsActivity.this.settingsVibrateRow) {
                    builder = new Builder(ProfileNotificationsActivity.this.getParentActivity());
                    builder.setTitle(LocaleController.getString("Vibrate", R.string.Vibrate));
                    builder.setItems(new CharSequence[]{LocaleController.getString("VibrationDisabled", R.string.VibrationDisabled), LocaleController.getString("SettingsDefault", R.string.SettingsDefault), LocaleController.getString("SystemDefault", R.string.SystemDefault), LocaleController.getString("Short", R.string.Short), LocaleController.getString("Long", R.string.Long)}, new OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
                            if (which == 0) {
                                editor.putInt("vibrate_" + ProfileNotificationsActivity.this.dialog_id, 2);
                            } else if (which == 1) {
                                editor.putInt("vibrate_" + ProfileNotificationsActivity.this.dialog_id, 0);
                            } else if (which == 2) {
                                editor.putInt("vibrate_" + ProfileNotificationsActivity.this.dialog_id, 4);
                            } else if (which == 3) {
                                editor.putInt("vibrate_" + ProfileNotificationsActivity.this.dialog_id, 1);
                            } else if (which == 4) {
                                editor.putInt("vibrate_" + ProfileNotificationsActivity.this.dialog_id, 3);
                            }
                            editor.commit();
                            if (ProfileNotificationsActivity.this.listView != null) {
                                ProfileNotificationsActivity.this.listView.invalidateViews();
                            }
                        }
                    });
                    builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                    ProfileNotificationsActivity.this.showDialog(builder.create());
                } else if (i == ProfileNotificationsActivity.this.settingsNotificationsRow) {
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
                } else if (i == ProfileNotificationsActivity.this.settingsSoundRow) {
                    try {
                        Intent tmpIntent = new Intent("android.intent.action.RINGTONE_PICKER");
                        tmpIntent.putExtra("android.intent.extra.ringtone.TYPE", 2);
                        tmpIntent.putExtra("android.intent.extra.ringtone.SHOW_DEFAULT", true);
                        tmpIntent.putExtra("android.intent.extra.ringtone.DEFAULT_URI", RingtoneManager.getDefaultUri(2));
                        preferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
                        Uri currentSound = null;
                        String defaultPath = null;
                        Uri defaultUri = System.DEFAULT_NOTIFICATION_URI;
                        if (defaultUri != null) {
                            defaultPath = defaultUri.getPath();
                        }
                        String path = preferences.getString("sound_path_" + ProfileNotificationsActivity.this.dialog_id, defaultPath);
                        if (!(path == null || path.equals("NoSound"))) {
                            currentSound = path.equals(defaultPath) ? defaultUri : Uri.parse(path);
                        }
                        tmpIntent.putExtra("android.intent.extra.ringtone.EXISTING_URI", currentSound);
                        ProfileNotificationsActivity.this.startActivityForResult(tmpIntent, 12);
                    } catch (Throwable e) {
                        FileLog.e("tmessages", e);
                    }
                } else if (i == ProfileNotificationsActivity.this.settingsLedRow) {
                    if (ProfileNotificationsActivity.this.getParentActivity() != null) {
                        linearLayout = new LinearLayout(ProfileNotificationsActivity.this.getParentActivity());
                        linearLayout.setOrientation(1);
                        final ColorPickerView colorPickerView = new ColorPickerView(ProfileNotificationsActivity.this.getParentActivity());
                        linearLayout.addView(colorPickerView, LayoutHelper.createLinear(-2, -2, 17));
                        preferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
                        if (preferences.contains("color_" + ProfileNotificationsActivity.this.dialog_id)) {
                            colorPickerView.setOldCenterColor(preferences.getInt("color_" + ProfileNotificationsActivity.this.dialog_id, -16711936));
                        } else if (((int) ProfileNotificationsActivity.this.dialog_id) < 0) {
                            colorPickerView.setOldCenterColor(preferences.getInt("GroupLed", -16711936));
                        } else {
                            colorPickerView.setOldCenterColor(preferences.getInt("MessagesLed", -16711936));
                        }
                        builder = new Builder(ProfileNotificationsActivity.this.getParentActivity());
                        builder.setTitle(LocaleController.getString("LedColor", R.string.LedColor));
                        builder.setView(linearLayout);
                        builder.setPositiveButton(LocaleController.getString("Set", R.string.Set), new OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int which) {
                                Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
                                editor.putInt("color_" + ProfileNotificationsActivity.this.dialog_id, colorPickerView.getColor());
                                editor.commit();
                                ProfileNotificationsActivity.this.listView.invalidateViews();
                            }
                        });
                        builder.setNeutralButton(LocaleController.getString("LedDisabled", R.string.LedDisabled), new OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
                                editor.putInt("color_" + ProfileNotificationsActivity.this.dialog_id, 0);
                                editor.commit();
                                ProfileNotificationsActivity.this.listView.invalidateViews();
                            }
                        });
                        builder.setNegativeButton(LocaleController.getString("Default", R.string.Default), new OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
                                editor.remove("color_" + ProfileNotificationsActivity.this.dialog_id);
                                editor.commit();
                                ProfileNotificationsActivity.this.listView.invalidateViews();
                            }
                        });
                        ProfileNotificationsActivity.this.showDialog(builder.create());
                    }
                } else if (i == ProfileNotificationsActivity.this.settingsPriorityRow) {
                    builder = new Builder(ProfileNotificationsActivity.this.getParentActivity());
                    builder.setTitle(LocaleController.getString("NotificationsPriority", R.string.NotificationsPriority));
                    builder.setItems(new CharSequence[]{LocaleController.getString("SettingsDefault", R.string.SettingsDefault), LocaleController.getString("NotificationsPriorityDefault", R.string.NotificationsPriorityDefault), LocaleController.getString("NotificationsPriorityHigh", R.string.NotificationsPriorityHigh), LocaleController.getString("NotificationsPriorityMax", R.string.NotificationsPriorityMax)}, new OnClickListener() {
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
                } else if (i == ProfileNotificationsActivity.this.smartRow && ProfileNotificationsActivity.this.getParentActivity() != null) {
                    preferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
                    int notifyMaxCount = preferences.getInt("smart_max_count_" + ProfileNotificationsActivity.this.dialog_id, 2);
                    int notifyDelay = preferences.getInt("smart_delay_" + ProfileNotificationsActivity.this.dialog_id, 180);
                    if (notifyMaxCount == 0) {
                        notifyMaxCount = 2;
                    }
                    linearLayout = new LinearLayout(ProfileNotificationsActivity.this.getParentActivity());
                    linearLayout.setOrientation(1);
                    LinearLayout linearLayout2 = new LinearLayout(ProfileNotificationsActivity.this.getParentActivity());
                    linearLayout2.setOrientation(0);
                    linearLayout.addView(linearLayout2);
                    LinearLayout.LayoutParams layoutParams1 = (LinearLayout.LayoutParams) linearLayout2.getLayoutParams();
                    layoutParams1.width = -2;
                    layoutParams1.height = -2;
                    layoutParams1.gravity = 49;
                    linearLayout2.setLayoutParams(layoutParams1);
                    View textView = new TextView(ProfileNotificationsActivity.this.getParentActivity());
                    textView.setText(LocaleController.getString("SmartNotificationsSoundAtMost", R.string.SmartNotificationsSoundAtMost));
                    textView.setTextSize(1, 18.0f);
                    linearLayout2.addView(textView);
                    layoutParams1 = (LinearLayout.LayoutParams) textView.getLayoutParams();
                    layoutParams1.width = -2;
                    layoutParams1.height = -2;
                    layoutParams1.gravity = 19;
                    textView.setLayoutParams(layoutParams1);
                    textView = new NumberPicker(ProfileNotificationsActivity.this.getParentActivity());
                    textView.setMinValue(1);
                    textView.setMaxValue(10);
                    textView.setValue(notifyMaxCount);
                    linearLayout2.addView(textView);
                    layoutParams1 = (LinearLayout.LayoutParams) textView.getLayoutParams();
                    layoutParams1.width = AndroidUtilities.dp(50.0f);
                    textView.setLayoutParams(layoutParams1);
                    textView = new TextView(ProfileNotificationsActivity.this.getParentActivity());
                    textView.setText(LocaleController.getString("SmartNotificationsTimes", R.string.SmartNotificationsTimes));
                    textView.setTextSize(1, 18.0f);
                    linearLayout2.addView(textView);
                    layoutParams1 = (LinearLayout.LayoutParams) textView.getLayoutParams();
                    layoutParams1.width = -2;
                    layoutParams1.height = -2;
                    layoutParams1.gravity = 19;
                    textView.setLayoutParams(layoutParams1);
                    linearLayout2 = new LinearLayout(ProfileNotificationsActivity.this.getParentActivity());
                    linearLayout2.setOrientation(0);
                    linearLayout.addView(linearLayout2);
                    layoutParams1 = (LinearLayout.LayoutParams) linearLayout2.getLayoutParams();
                    layoutParams1.width = -2;
                    layoutParams1.height = -2;
                    layoutParams1.gravity = 49;
                    linearLayout2.setLayoutParams(layoutParams1);
                    textView = new TextView(ProfileNotificationsActivity.this.getParentActivity());
                    textView.setText(LocaleController.getString("SmartNotificationsWithin", R.string.SmartNotificationsWithin));
                    textView.setTextSize(1, 18.0f);
                    linearLayout2.addView(textView);
                    layoutParams1 = (LinearLayout.LayoutParams) textView.getLayoutParams();
                    layoutParams1.width = -2;
                    layoutParams1.height = -2;
                    layoutParams1.gravity = 19;
                    textView.setLayoutParams(layoutParams1);
                    final NumberPicker numberPickerMinutes = new NumberPicker(ProfileNotificationsActivity.this.getParentActivity());
                    numberPickerMinutes.setMinValue(1);
                    numberPickerMinutes.setMaxValue(10);
                    numberPickerMinutes.setValue(notifyDelay / 60);
                    linearLayout2.addView(numberPickerMinutes);
                    layoutParams1 = (LinearLayout.LayoutParams) numberPickerMinutes.getLayoutParams();
                    layoutParams1.width = AndroidUtilities.dp(50.0f);
                    numberPickerMinutes.setLayoutParams(layoutParams1);
                    textView = new TextView(ProfileNotificationsActivity.this.getParentActivity());
                    textView.setText(LocaleController.getString("SmartNotificationsMinutes", R.string.SmartNotificationsMinutes));
                    textView.setTextSize(1, 18.0f);
                    linearLayout2.addView(textView);
                    layoutParams1 = (LinearLayout.LayoutParams) textView.getLayoutParams();
                    layoutParams1.width = -2;
                    layoutParams1.height = -2;
                    layoutParams1.gravity = 19;
                    textView.setLayoutParams(layoutParams1);
                    builder = new Builder(ProfileNotificationsActivity.this.getParentActivity());
                    builder.setTitle(LocaleController.getString("SmartNotifications", R.string.SmartNotifications));
                    builder.setView(linearLayout);
                    final View view2 = textView;
                    builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
                            preferences.edit().putInt("smart_max_count_" + ProfileNotificationsActivity.this.dialog_id, view2.getValue()).commit();
                            preferences.edit().putInt("smart_delay_" + ProfileNotificationsActivity.this.dialog_id, numberPickerMinutes.getValue() * 60).commit();
                            if (ProfileNotificationsActivity.this.listView != null) {
                                ProfileNotificationsActivity.this.listView.invalidateViews();
                            }
                        }
                    });
                    builder.setNegativeButton(LocaleController.getString("SmartNotificationsDisabled", R.string.SmartNotificationsDisabled), new OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit().putInt("smart_max_count_" + ProfileNotificationsActivity.this.dialog_id, 0).commit();
                            if (ProfileNotificationsActivity.this.listView != null) {
                                ProfileNotificationsActivity.this.listView.invalidateViews();
                            }
                        }
                    });
                    ProfileNotificationsActivity.this.showDialog(builder.create());
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
