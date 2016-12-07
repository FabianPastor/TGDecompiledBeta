package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
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
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.AnimatorListenerAdapterProxy;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.exoplayer.C;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.LayoutParams;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Adapters.BaseFragmentAdapter;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.RadioCell;
import org.telegram.ui.Cells.TextCheckBoxCell;
import org.telegram.ui.Cells.TextColorCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;

public class ProfileNotificationsActivity extends BaseFragment implements NotificationCenterDelegate {
    private ListAdapter adapter;
    private AnimatorSet animatorSet;
    private int colorRow;
    private boolean customEnabled;
    private int customInfoRow;
    private int customRow;
    private long dialog_id;
    private int generalRow;
    private int ledInfoRow;
    private int ledRow;
    private RecyclerListView listView;
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

    private class ListAdapter extends Adapter {
        private Context context;

        private class Holder extends ViewHolder {
            public Holder(View itemView) {
                super(itemView);
            }
        }

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
                    view.setBackgroundColor(-1);
                    break;
                case 1:
                    view = new TextSettingsCell(this.context) {
                        public boolean onTouchEvent(MotionEvent event) {
                            if (VERSION.SDK_INT >= 21 && getBackground() != null && (event.getAction() == 0 || event.getAction() == 2)) {
                                getBackground().setHotspot(event.getX(), event.getY());
                            }
                            return super.onTouchEvent(event);
                        }
                    };
                    break;
                case 2:
                    view = new TextInfoPrivacyCell(this.context);
                    break;
                case 3:
                    view = new TextColorCell(this.context) {
                        public boolean onTouchEvent(MotionEvent event) {
                            if (VERSION.SDK_INT >= 21 && getBackground() != null && (event.getAction() == 0 || event.getAction() == 2)) {
                                getBackground().setHotspot(event.getX(), event.getY());
                            }
                            return super.onTouchEvent(event);
                        }
                    };
                    break;
                case 4:
                    view = new RadioCell(this.context) {
                        public boolean onTouchEvent(MotionEvent event) {
                            if (VERSION.SDK_INT >= 21 && getBackground() != null && (event.getAction() == 0 || event.getAction() == 2)) {
                                getBackground().setHotspot(event.getX(), event.getY());
                            }
                            return super.onTouchEvent(event);
                        }
                    };
                    break;
                default:
                    view = new TextCheckBoxCell(this.context) {
                        public boolean onTouchEvent(MotionEvent event) {
                            if (VERSION.SDK_INT >= 21 && getBackground() != null && (event.getAction() == 0 || event.getAction() == 2)) {
                                getBackground().setHotspot(event.getX(), event.getY());
                            }
                            return super.onTouchEvent(event);
                        }
                    };
                    break;
            }
            view.setLayoutParams(new LayoutParams(-1, -2));
            return new Holder(view);
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            SharedPreferences preferences;
            switch (holder.getItemViewType()) {
                case 0:
                    HeaderCell headerCell = holder.itemView;
                    if (position == ProfileNotificationsActivity.this.generalRow) {
                        headerCell.setText(LocaleController.getString("General", R.string.General));
                        return;
                    } else if (position == ProfileNotificationsActivity.this.popupRow) {
                        headerCell.setText(LocaleController.getString("ProfilePopupNotification", R.string.ProfilePopupNotification));
                        return;
                    } else if (position == ProfileNotificationsActivity.this.ledRow) {
                        headerCell.setText(LocaleController.getString("NotificationsLed", R.string.NotificationsLed));
                        return;
                    } else {
                        return;
                    }
                case 1:
                    TextSettingsCell textCell = holder.itemView;
                    preferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
                    if (position == ProfileNotificationsActivity.this.soundRow) {
                        String value = preferences.getString("sound_" + ProfileNotificationsActivity.this.dialog_id, LocaleController.getString("SoundDefault", R.string.SoundDefault));
                        if (value.equals("NoSound")) {
                            value = LocaleController.getString("NoSound", R.string.NoSound);
                        }
                        textCell.setTextAndValue(LocaleController.getString("Sound", R.string.Sound), value, true);
                        return;
                    } else if (position == ProfileNotificationsActivity.this.vibrateRow) {
                        value = preferences.getInt("vibrate_" + ProfileNotificationsActivity.this.dialog_id, 0);
                        String string;
                        String string2;
                        boolean z;
                        if (value == 0 || value == 4) {
                            string = LocaleController.getString("Vibrate", R.string.Vibrate);
                            string2 = LocaleController.getString("VibrationDefault", R.string.VibrationDefault);
                            z = (ProfileNotificationsActivity.this.smartRow == -1 && ProfileNotificationsActivity.this.priorityRow == -1) ? false : true;
                            textCell.setTextAndValue(string, string2, z);
                            return;
                        } else if (value == 1) {
                            string = LocaleController.getString("Vibrate", R.string.Vibrate);
                            string2 = LocaleController.getString("Short", R.string.Short);
                            z = (ProfileNotificationsActivity.this.smartRow == -1 && ProfileNotificationsActivity.this.priorityRow == -1) ? false : true;
                            textCell.setTextAndValue(string, string2, z);
                            return;
                        } else if (value == 2) {
                            string = LocaleController.getString("Vibrate", R.string.Vibrate);
                            string2 = LocaleController.getString("VibrationDisabled", R.string.VibrationDisabled);
                            z = (ProfileNotificationsActivity.this.smartRow == -1 && ProfileNotificationsActivity.this.priorityRow == -1) ? false : true;
                            textCell.setTextAndValue(string, string2, z);
                            return;
                        } else if (value == 3) {
                            string = LocaleController.getString("Vibrate", R.string.Vibrate);
                            string2 = LocaleController.getString("Long", R.string.Long);
                            z = (ProfileNotificationsActivity.this.smartRow == -1 && ProfileNotificationsActivity.this.priorityRow == -1) ? false : true;
                            textCell.setTextAndValue(string, string2, z);
                            return;
                        } else {
                            return;
                        }
                    } else if (position == ProfileNotificationsActivity.this.priorityRow) {
                        value = preferences.getInt("priority_" + ProfileNotificationsActivity.this.dialog_id, 3);
                        if (value == 0) {
                            textCell.setTextAndValue(LocaleController.getString("NotificationsPriority", R.string.NotificationsPriority), LocaleController.getString("NotificationsPriorityDefault", R.string.NotificationsPriorityDefault), false);
                            return;
                        } else if (value == 1) {
                            textCell.setTextAndValue(LocaleController.getString("NotificationsPriority", R.string.NotificationsPriority), LocaleController.getString("NotificationsPriorityHigh", R.string.NotificationsPriorityHigh), false);
                            return;
                        } else if (value == 2) {
                            textCell.setTextAndValue(LocaleController.getString("NotificationsPriority", R.string.NotificationsPriority), LocaleController.getString("NotificationsPriorityMax", R.string.NotificationsPriorityMax), false);
                            return;
                        } else if (value == 3) {
                            textCell.setTextAndValue(LocaleController.getString("NotificationsPriority", R.string.NotificationsPriority), LocaleController.getString("NotificationsPrioritySettings", R.string.NotificationsPrioritySettings), false);
                            return;
                        } else {
                            return;
                        }
                    } else if (position == ProfileNotificationsActivity.this.smartRow) {
                        int notifyMaxCount = preferences.getInt("smart_max_count_" + ProfileNotificationsActivity.this.dialog_id, 2);
                        int notifyDelay = preferences.getInt("smart_delay_" + ProfileNotificationsActivity.this.dialog_id, 180);
                        if (notifyMaxCount == 0) {
                            textCell.setTextAndValue(LocaleController.getString("SmartNotifications", R.string.SmartNotifications), LocaleController.getString("SmartNotificationsDisabled", R.string.SmartNotificationsDisabled), ProfileNotificationsActivity.this.priorityRow != -1);
                            return;
                        }
                        String minutes = LocaleController.formatPluralString("Minutes", notifyDelay / 60);
                        textCell.setTextAndValue(LocaleController.getString("SmartNotifications", R.string.SmartNotifications), LocaleController.formatString("SmartNotificationsInfo", R.string.SmartNotificationsInfo, Integer.valueOf(notifyMaxCount), minutes), ProfileNotificationsActivity.this.priorityRow != -1);
                        return;
                    } else {
                        return;
                    }
                case 2:
                    TextInfoPrivacyCell textCell2 = holder.itemView;
                    if (position == ProfileNotificationsActivity.this.popupInfoRow) {
                        textCell2.setText(LocaleController.getString("ProfilePopupNotificationInfo", R.string.ProfilePopupNotificationInfo));
                        textCell2.setBackgroundResource(R.drawable.greydivider);
                        return;
                    } else if (position == ProfileNotificationsActivity.this.ledInfoRow) {
                        textCell2.setText(LocaleController.getString("NotificationsLedInfo", R.string.NotificationsLedInfo));
                        textCell2.setBackgroundResource(R.drawable.greydivider_bottom);
                        return;
                    } else if (position == ProfileNotificationsActivity.this.priorityInfoRow) {
                        if (ProfileNotificationsActivity.this.priorityRow == -1) {
                            textCell2.setText("");
                        } else {
                            textCell2.setText(LocaleController.getString("PriorityInfo", R.string.PriorityInfo));
                        }
                        textCell2.setBackgroundResource(R.drawable.greydivider);
                        return;
                    } else if (position == ProfileNotificationsActivity.this.customInfoRow) {
                        textCell2.setText(null);
                        textCell2.setBackgroundResource(R.drawable.greydivider);
                        return;
                    } else {
                        return;
                    }
                case 3:
                    int color;
                    TextColorCell textCell3 = holder.itemView;
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
                            return;
                        }
                    }
                    textCell3.setTextAndColor(LocaleController.getString("NotificationsLedColor", R.string.NotificationsLedColor), color, false);
                    return;
                case 4:
                    RadioCell radioCell = holder.itemView;
                    preferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
                    int popup = preferences.getInt("popup_" + ProfileNotificationsActivity.this.dialog_id, 0);
                    if (popup == 0) {
                        if (preferences.getInt(((int) ProfileNotificationsActivity.this.dialog_id) < 0 ? "popupGroup" : "popupAll", 0) != 0) {
                            popup = 1;
                        } else {
                            popup = 2;
                        }
                    }
                    if (position == ProfileNotificationsActivity.this.popupEnabledRow) {
                        radioCell.setText(LocaleController.getString("PopupEnabled", R.string.PopupEnabled), popup == 1, true);
                        radioCell.setTag(Integer.valueOf(1));
                        return;
                    } else if (position == ProfileNotificationsActivity.this.popupDisabledRow) {
                        radioCell.setText(LocaleController.getString("PopupDisabled", R.string.PopupDisabled), popup == 2, false);
                        radioCell.setTag(Integer.valueOf(2));
                        return;
                    } else {
                        return;
                    }
                case 5:
                    holder.itemView.setTextAndCheck(LocaleController.getString("NotificationsEnableCustom", R.string.NotificationsEnableCustom), ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).getBoolean("custom_" + ProfileNotificationsActivity.this.dialog_id, false), false);
                    return;
                default:
                    return;
            }
        }

        public void onViewAttachedToWindow(ViewHolder holder) {
            if (holder.getItemViewType() != 0) {
                if (holder.getItemViewType() != 2) {
                    if (ProfileNotificationsActivity.this.customEnabled || holder.getAdapterPosition() == ProfileNotificationsActivity.this.customRow) {
                        holder.itemView.setBackgroundResource(R.drawable.list_selector_white);
                    } else {
                        holder.itemView.setBackgroundColor(-1);
                    }
                }
                switch (holder.getItemViewType()) {
                    case 1:
                        holder.itemView.setEnabled(ProfileNotificationsActivity.this.customEnabled, null);
                        return;
                    case 2:
                        holder.itemView.setEnabled(ProfileNotificationsActivity.this.customEnabled, null);
                        return;
                    case 3:
                        holder.itemView.setEnabled(ProfileNotificationsActivity.this.customEnabled, null);
                        return;
                    case 4:
                        holder.itemView.setEnabled(ProfileNotificationsActivity.this.customEnabled, null);
                        return;
                    default:
                        return;
                }
            }
        }

        public int getItemViewType(int position) {
            if (position == ProfileNotificationsActivity.this.generalRow || position == ProfileNotificationsActivity.this.popupRow || position == ProfileNotificationsActivity.this.ledRow) {
                return 0;
            }
            if (position == ProfileNotificationsActivity.this.soundRow || position == ProfileNotificationsActivity.this.vibrateRow || position == ProfileNotificationsActivity.this.priorityRow || position == ProfileNotificationsActivity.this.smartRow) {
                return 1;
            }
            if (position == ProfileNotificationsActivity.this.popupInfoRow || position == ProfileNotificationsActivity.this.ledInfoRow || position == ProfileNotificationsActivity.this.priorityInfoRow || position == ProfileNotificationsActivity.this.customInfoRow) {
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
            Chat chat = MessagesController.getInstance().getChat(Integer.valueOf(-lower_id));
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
        i = this.rowCount;
        this.rowCount = i + 1;
        this.ledRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.colorRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.ledInfoRow = i;
        this.customEnabled = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).getBoolean("custom_" + this.dialog_id, false);
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
            public void onItemClick(View view, int position) {
                SharedPreferences preferences;
                if (position == ProfileNotificationsActivity.this.customRow) {
                    preferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
                    ProfileNotificationsActivity.this.customEnabled = !ProfileNotificationsActivity.this.customEnabled;
                    preferences.edit().putBoolean("custom_" + ProfileNotificationsActivity.this.dialog_id, ProfileNotificationsActivity.this.customEnabled).commit();
                    ((TextCheckBoxCell) view).setChecked(ProfileNotificationsActivity.this.customEnabled);
                    int count = ProfileNotificationsActivity.this.listView.getChildCount();
                    ArrayList<Animator> animators = new ArrayList();
                    for (int a = 0; a < count; a++) {
                        Holder holder = (Holder) ProfileNotificationsActivity.this.listView.getChildViewHolder(ProfileNotificationsActivity.this.listView.getChildAt(a));
                        int type = holder.getItemViewType();
                        if (!(holder.getAdapterPosition() == ProfileNotificationsActivity.this.customRow || type == 0)) {
                            if (type != 2) {
                                if (ProfileNotificationsActivity.this.customEnabled || holder.getAdapterPosition() == ProfileNotificationsActivity.this.customRow) {
                                    holder.itemView.setBackgroundResource(R.drawable.list_selector_white);
                                } else {
                                    holder.itemView.setBackgroundColor(-1);
                                }
                            }
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
                        ProfileNotificationsActivity.this.animatorSet.addListener(new AnimatorListenerAdapterProxy() {
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
                    if (position == ProfileNotificationsActivity.this.soundRow) {
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
                    } else if (position == ProfileNotificationsActivity.this.vibrateRow) {
                        ProfileNotificationsActivity.this.showDialog(AlertsCreator.createVibrationSelectDialog(ProfileNotificationsActivity.this.getParentActivity(), ProfileNotificationsActivity.this, ProfileNotificationsActivity.this.dialog_id, false, false, new Runnable() {
                            public void run() {
                                if (ProfileNotificationsActivity.this.adapter != null) {
                                    ProfileNotificationsActivity.this.adapter.notifyItemChanged(ProfileNotificationsActivity.this.vibrateRow);
                                }
                            }
                        }));
                    } else if (position == ProfileNotificationsActivity.this.priorityRow) {
                        ProfileNotificationsActivity.this.showDialog(AlertsCreator.createPrioritySelectDialog(ProfileNotificationsActivity.this.getParentActivity(), ProfileNotificationsActivity.this, ProfileNotificationsActivity.this.dialog_id, false, false, new Runnable() {
                            public void run() {
                                if (ProfileNotificationsActivity.this.adapter != null) {
                                    ProfileNotificationsActivity.this.adapter.notifyItemChanged(ProfileNotificationsActivity.this.priorityRow);
                                }
                            }
                        }));
                    } else if (position == ProfileNotificationsActivity.this.smartRow) {
                        if (ProfileNotificationsActivity.this.getParentActivity() != null) {
                            final Context context1 = ProfileNotificationsActivity.this.getParentActivity();
                            preferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
                            int notifyMaxCount = preferences.getInt("smart_max_count_" + ProfileNotificationsActivity.this.dialog_id, 2);
                            int notifyDelay = preferences.getInt("smart_delay_" + ProfileNotificationsActivity.this.dialog_id, 180);
                            if (notifyMaxCount == 0) {
                                notifyMaxCount = 2;
                            }
                            int selected = ((((notifyDelay / 60) - 1) * 10) + notifyMaxCount) - 1;
                            View listView = new ListView(ProfileNotificationsActivity.this.getParentActivity());
                            listView.setDividerHeight(0);
                            listView.setClipToPadding(true);
                            listView.setDivider(null);
                            final int i = selected;
                            listView.setAdapter(new BaseFragmentAdapter() {
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
                                    textView.setTextColor(i == i ? -13333567 : -14606047);
                                    int notifyDelay = i / 10;
                                    String times = LocaleController.formatPluralString("Times", (i % 10) + 1);
                                    String minutes = LocaleController.formatPluralString("Minutes", notifyDelay + 1);
                                    textView.setText(LocaleController.formatString("SmartNotificationsDetail", R.string.SmartNotificationsDetail, times, minutes));
                                    return view;
                                }
                            });
                            listView.setPadding(0, AndroidUtilities.dp(12.0f), 0, AndroidUtilities.dp(8.0f));
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                                    if (position >= 0 && position < 100) {
                                        int notifyMaxCount = (position % 10) + 1;
                                        int notifyDelay = (position / 10) + 1;
                                        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
                                        preferences.edit().putInt("smart_max_count_" + ProfileNotificationsActivity.this.dialog_id, notifyMaxCount).commit();
                                        preferences.edit().putInt("smart_delay_" + ProfileNotificationsActivity.this.dialog_id, notifyDelay * 60).commit();
                                        if (ProfileNotificationsActivity.this.adapter != null) {
                                            ProfileNotificationsActivity.this.adapter.notifyItemChanged(ProfileNotificationsActivity.this.smartRow);
                                        }
                                        ProfileNotificationsActivity.this.dismissCurrentDialig();
                                    }
                                }
                            });
                            Builder builder = new Builder(ProfileNotificationsActivity.this.getParentActivity());
                            builder.setTitle(LocaleController.getString("SmartNotificationsAlert", R.string.SmartNotificationsAlert));
                            builder.setView(listView);
                            builder.setPositiveButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                            builder.setNegativeButton(LocaleController.getString("SmartNotificationsDisabled", R.string.SmartNotificationsDisabled), new OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit().putInt("smart_max_count_" + ProfileNotificationsActivity.this.dialog_id, 0).commit();
                                    if (ProfileNotificationsActivity.this.adapter != null) {
                                        ProfileNotificationsActivity.this.adapter.notifyItemChanged(ProfileNotificationsActivity.this.smartRow);
                                    }
                                    ProfileNotificationsActivity.this.dismissCurrentDialig();
                                }
                            });
                            ProfileNotificationsActivity.this.showDialog(builder.create());
                        }
                    } else if (position == ProfileNotificationsActivity.this.colorRow) {
                        if (ProfileNotificationsActivity.this.getParentActivity() != null) {
                            ProfileNotificationsActivity.this.showDialog(AlertsCreator.createColorSelectDialog(ProfileNotificationsActivity.this.getParentActivity(), ProfileNotificationsActivity.this.dialog_id, false, false, new Runnable() {
                                public void run() {
                                    if (ProfileNotificationsActivity.this.adapter != null) {
                                        ProfileNotificationsActivity.this.adapter.notifyItemChanged(ProfileNotificationsActivity.this.colorRow);
                                    }
                                }
                            }));
                        }
                    } else if (position == ProfileNotificationsActivity.this.popupEnabledRow) {
                        ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit().putInt("popup_" + ProfileNotificationsActivity.this.dialog_id, 1).commit();
                        ((RadioCell) view).setChecked(true, true);
                        view = ProfileNotificationsActivity.this.listView.findViewWithTag(Integer.valueOf(2));
                        if (view != null) {
                            ((RadioCell) view).setChecked(false, true);
                        }
                    } else if (position == ProfileNotificationsActivity.this.popupDisabledRow) {
                        ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit().putInt("popup_" + ProfileNotificationsActivity.this.dialog_id, 2).commit();
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
            if (this.adapter != null) {
                this.adapter.notifyItemChanged(this.soundRow);
            }
        }
    }

    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationCenter.notificationsSettingsUpdated) {
            this.adapter.notifyDataSetChanged();
        }
    }
}
