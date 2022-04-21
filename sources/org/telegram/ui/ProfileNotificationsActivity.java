package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LayoutAnimationController;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationsController;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
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
import org.telegram.ui.Components.ChatAvatarContainer;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.NotificationsSettingsActivity;

public class ProfileNotificationsActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private static final int done_button = 1;
    /* access modifiers changed from: private */
    public ListAdapter adapter;
    /* access modifiers changed from: private */
    public boolean addingException;
    /* access modifiers changed from: private */
    public AnimatorSet animatorSet;
    ChatAvatarContainer avatarContainer;
    /* access modifiers changed from: private */
    public int avatarRow;
    /* access modifiers changed from: private */
    public int avatarSectionRow;
    /* access modifiers changed from: private */
    public int callsRow;
    /* access modifiers changed from: private */
    public int callsVibrateRow;
    /* access modifiers changed from: private */
    public int colorRow;
    /* access modifiers changed from: private */
    public boolean customEnabled;
    /* access modifiers changed from: private */
    public int customInfoRow;
    /* access modifiers changed from: private */
    public int customRow;
    /* access modifiers changed from: private */
    public ProfileNotificationsActivityDelegate delegate;
    /* access modifiers changed from: private */
    public long dialogId;
    /* access modifiers changed from: private */
    public int enableRow;
    /* access modifiers changed from: private */
    public int generalRow;
    /* access modifiers changed from: private */
    public int ledInfoRow;
    /* access modifiers changed from: private */
    public int ledRow;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    /* access modifiers changed from: private */
    public boolean notificationsEnabled;
    /* access modifiers changed from: private */
    public int popupDisabledRow;
    /* access modifiers changed from: private */
    public int popupEnabledRow;
    /* access modifiers changed from: private */
    public int popupInfoRow;
    /* access modifiers changed from: private */
    public int popupRow;
    /* access modifiers changed from: private */
    public int previewRow;
    /* access modifiers changed from: private */
    public int priorityInfoRow;
    /* access modifiers changed from: private */
    public int priorityRow;
    /* access modifiers changed from: private */
    public int ringtoneInfoRow;
    /* access modifiers changed from: private */
    public int ringtoneRow;
    /* access modifiers changed from: private */
    public int rowCount;
    /* access modifiers changed from: private */
    public int smartRow;
    /* access modifiers changed from: private */
    public int soundRow;
    /* access modifiers changed from: private */
    public int vibrateRow;

    public interface ProfileNotificationsActivityDelegate {
        void didCreateNewException(NotificationsSettingsActivity.NotificationException notificationException);
    }

    public ProfileNotificationsActivity(Bundle args) {
        super(args);
        this.dialogId = args.getLong("dialog_id");
        this.addingException = args.getBoolean("exception", false);
    }

    public boolean onFragmentCreate() {
        boolean isChannel;
        this.rowCount = 0;
        boolean z = this.addingException;
        if (z) {
            int i = 0 + 1;
            this.rowCount = i;
            this.avatarRow = 0;
            this.rowCount = i + 1;
            this.avatarSectionRow = i;
            this.customRow = -1;
            this.customInfoRow = -1;
        } else {
            this.avatarRow = -1;
            this.avatarSectionRow = -1;
            int i2 = 0 + 1;
            this.rowCount = i2;
            this.customRow = 0;
            this.rowCount = i2 + 1;
            this.customInfoRow = i2;
        }
        int i3 = this.rowCount;
        int i4 = i3 + 1;
        this.rowCount = i4;
        this.generalRow = i3;
        if (z) {
            this.rowCount = i4 + 1;
            this.enableRow = i4;
        } else {
            this.enableRow = -1;
        }
        if (!DialogObject.isEncryptedDialog(this.dialogId)) {
            int i5 = this.rowCount;
            this.rowCount = i5 + 1;
            this.previewRow = i5;
        } else {
            this.previewRow = -1;
        }
        int i6 = this.rowCount;
        int i7 = i6 + 1;
        this.rowCount = i7;
        this.soundRow = i6;
        this.rowCount = i7 + 1;
        this.vibrateRow = i7;
        if (DialogObject.isChatDialog(this.dialogId)) {
            int i8 = this.rowCount;
            this.rowCount = i8 + 1;
            this.smartRow = i8;
        } else {
            this.smartRow = -1;
        }
        if (Build.VERSION.SDK_INT >= 21) {
            int i9 = this.rowCount;
            this.rowCount = i9 + 1;
            this.priorityRow = i9;
        } else {
            this.priorityRow = -1;
        }
        int i10 = this.rowCount;
        this.rowCount = i10 + 1;
        this.priorityInfoRow = i10;
        if (DialogObject.isChatDialog(this.dialogId)) {
            TLRPC.Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-this.dialogId));
            isChannel = ChatObject.isChannel(chat) && !chat.megagroup;
        } else {
            isChannel = false;
        }
        if (DialogObject.isEncryptedDialog(this.dialogId) || isChannel) {
            this.popupRow = -1;
            this.popupEnabledRow = -1;
            this.popupDisabledRow = -1;
            this.popupInfoRow = -1;
        } else {
            int i11 = this.rowCount;
            int i12 = i11 + 1;
            this.rowCount = i12;
            this.popupRow = i11;
            int i13 = i12 + 1;
            this.rowCount = i13;
            this.popupEnabledRow = i12;
            int i14 = i13 + 1;
            this.rowCount = i14;
            this.popupDisabledRow = i13;
            this.rowCount = i14 + 1;
            this.popupInfoRow = i14;
        }
        if (DialogObject.isUserDialog(this.dialogId)) {
            int i15 = this.rowCount;
            int i16 = i15 + 1;
            this.rowCount = i16;
            this.callsRow = i15;
            int i17 = i16 + 1;
            this.rowCount = i17;
            this.callsVibrateRow = i16;
            int i18 = i17 + 1;
            this.rowCount = i18;
            this.ringtoneRow = i17;
            this.rowCount = i18 + 1;
            this.ringtoneInfoRow = i18;
        } else {
            this.callsRow = -1;
            this.callsVibrateRow = -1;
            this.ringtoneRow = -1;
            this.ringtoneInfoRow = -1;
        }
        int i19 = this.rowCount;
        int i20 = i19 + 1;
        this.rowCount = i20;
        this.ledRow = i19;
        int i21 = i20 + 1;
        this.rowCount = i21;
        this.colorRow = i20;
        this.rowCount = i21 + 1;
        this.ledInfoRow = i21;
        SharedPreferences preferences = MessagesController.getNotificationsSettings(this.currentAccount);
        StringBuilder sb = new StringBuilder();
        sb.append("custom_");
        sb.append(this.dialogId);
        this.customEnabled = preferences.getBoolean(sb.toString(), false) || this.addingException;
        boolean hasOverride = preferences.contains("notify2_" + this.dialogId);
        int value = preferences.getInt("notify2_" + this.dialogId, 0);
        if (value == 0) {
            if (hasOverride) {
                this.notificationsEnabled = true;
            } else {
                this.notificationsEnabled = NotificationsController.getInstance(this.currentAccount).isGlobalNotificationsEnabled(this.dialogId);
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

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    if (!ProfileNotificationsActivity.this.addingException && ProfileNotificationsActivity.this.notificationsEnabled && ProfileNotificationsActivity.this.customEnabled) {
                        SharedPreferences.Editor edit = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount).edit();
                        edit.putInt("notify2_" + ProfileNotificationsActivity.this.dialogId, 0).commit();
                    }
                } else if (id == 1) {
                    SharedPreferences preferences = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("custom_" + ProfileNotificationsActivity.this.dialogId, true);
                    TLRPC.Dialog dialog = MessagesController.getInstance(ProfileNotificationsActivity.this.currentAccount).dialogs_dict.get(ProfileNotificationsActivity.this.dialogId);
                    if (ProfileNotificationsActivity.this.notificationsEnabled) {
                        editor.putInt("notify2_" + ProfileNotificationsActivity.this.dialogId, 0);
                        MessagesStorage.getInstance(ProfileNotificationsActivity.this.currentAccount).setDialogFlags(ProfileNotificationsActivity.this.dialogId, 0);
                        if (dialog != null) {
                            dialog.notify_settings = new TLRPC.TL_peerNotifySettings();
                        }
                    } else {
                        editor.putInt("notify2_" + ProfileNotificationsActivity.this.dialogId, 2);
                        NotificationsController.getInstance(ProfileNotificationsActivity.this.currentAccount).removeNotificationsForDialog(ProfileNotificationsActivity.this.dialogId);
                        MessagesStorage.getInstance(ProfileNotificationsActivity.this.currentAccount).setDialogFlags(ProfileNotificationsActivity.this.dialogId, 1);
                        if (dialog != null) {
                            dialog.notify_settings = new TLRPC.TL_peerNotifySettings();
                            dialog.notify_settings.mute_until = Integer.MAX_VALUE;
                        }
                    }
                    editor.commit();
                    NotificationsController.getInstance(ProfileNotificationsActivity.this.currentAccount).updateServerNotificationsSettings(ProfileNotificationsActivity.this.dialogId);
                    if (ProfileNotificationsActivity.this.delegate != null) {
                        NotificationsSettingsActivity.NotificationException exception = new NotificationsSettingsActivity.NotificationException();
                        exception.did = ProfileNotificationsActivity.this.dialogId;
                        exception.hasCustom = true;
                        exception.notify = preferences.getInt("notify2_" + ProfileNotificationsActivity.this.dialogId, 0);
                        if (exception.notify != 0) {
                            exception.muteUntil = preferences.getInt("notifyuntil_" + ProfileNotificationsActivity.this.dialogId, 0);
                        }
                        ProfileNotificationsActivity.this.delegate.didCreateNewException(exception);
                    }
                }
                ProfileNotificationsActivity.this.finishFragment();
            }
        });
        ChatAvatarContainer chatAvatarContainer = new ChatAvatarContainer(context, (ChatActivity) null, false);
        this.avatarContainer = chatAvatarContainer;
        chatAvatarContainer.setOccupyStatusBar(!AndroidUtilities.isTablet());
        this.actionBar.addView(this.avatarContainer, 0, LayoutHelper.createFrame(-2, -1.0f, 51, !this.inPreviewMode ? 56.0f : 0.0f, 0.0f, 40.0f, 0.0f));
        this.actionBar.setAllowOverlayTitle(false);
        if (this.dialogId < 0) {
            TLRPC.Chat chatLocal = getMessagesController().getChat(Long.valueOf(-this.dialogId));
            this.avatarContainer.setChatAvatar(chatLocal);
            this.avatarContainer.setTitle(chatLocal.title);
        } else {
            TLRPC.User user = getMessagesController().getUser(Long.valueOf(this.dialogId));
            if (user != null) {
                this.avatarContainer.setUserAvatar(user);
                this.avatarContainer.setTitle(ContactsController.formatName(user.first_name, user.last_name));
            }
        }
        if (this.addingException) {
            this.avatarContainer.setSubtitle(LocaleController.getString("NotificationsNewException", NUM));
            this.actionBar.createMenu().addItem(1, (CharSequence) LocaleController.getString("Done", NUM).toUpperCase());
        } else {
            this.avatarContainer.setSubtitle(LocaleController.getString("CustomNotifications", NUM));
        }
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        frameLayout.addView(recyclerListView, LayoutHelper.createFrame(-1, -1.0f));
        RecyclerListView recyclerListView2 = this.listView;
        ListAdapter listAdapter = new ListAdapter(context);
        this.adapter = listAdapter;
        recyclerListView2.setAdapter(listAdapter);
        this.listView.setItemAnimator((RecyclerView.ItemAnimator) null);
        this.listView.setLayoutAnimation((LayoutAnimationController) null);
        this.listView.setLayoutManager(new LinearLayoutManager(context) {
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        });
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public void onItemClick(View view, int position) {
                if (position == ProfileNotificationsActivity.this.customRow && (view instanceof TextCheckCell)) {
                    SharedPreferences preferences = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount);
                    ProfileNotificationsActivity profileNotificationsActivity = ProfileNotificationsActivity.this;
                    boolean unused = profileNotificationsActivity.customEnabled = true ^ profileNotificationsActivity.customEnabled;
                    ProfileNotificationsActivity profileNotificationsActivity2 = ProfileNotificationsActivity.this;
                    boolean unused2 = profileNotificationsActivity2.notificationsEnabled = profileNotificationsActivity2.customEnabled;
                    SharedPreferences.Editor edit = preferences.edit();
                    edit.putBoolean("custom_" + ProfileNotificationsActivity.this.dialogId, ProfileNotificationsActivity.this.customEnabled).apply();
                    TextCheckCell cell = (TextCheckCell) view;
                    cell.setChecked(ProfileNotificationsActivity.this.customEnabled);
                    int clr = Theme.getColor(ProfileNotificationsActivity.this.customEnabled ? "windowBackgroundChecked" : "windowBackgroundUnchecked");
                    if (ProfileNotificationsActivity.this.customEnabled) {
                        cell.setBackgroundColorAnimated(ProfileNotificationsActivity.this.customEnabled, clr);
                    } else {
                        cell.setBackgroundColorAnimatedReverse(clr);
                    }
                    ProfileNotificationsActivity.this.checkRowsEnabled();
                } else if (ProfileNotificationsActivity.this.customEnabled && view.isEnabled()) {
                    if (position == ProfileNotificationsActivity.this.soundRow) {
                        Bundle bundle = new Bundle();
                        bundle.putLong("dialog_id", ProfileNotificationsActivity.this.dialogId);
                        ProfileNotificationsActivity.this.presentFragment(new NotificationsSoundActivity(bundle));
                    } else if (position == ProfileNotificationsActivity.this.ringtoneRow) {
                        try {
                            Intent tmpIntent = new Intent("android.intent.action.RINGTONE_PICKER");
                            tmpIntent.putExtra("android.intent.extra.ringtone.TYPE", 1);
                            tmpIntent.putExtra("android.intent.extra.ringtone.SHOW_DEFAULT", true);
                            tmpIntent.putExtra("android.intent.extra.ringtone.SHOW_SILENT", true);
                            tmpIntent.putExtra("android.intent.extra.ringtone.DEFAULT_URI", RingtoneManager.getDefaultUri(1));
                            SharedPreferences preferences2 = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount);
                            Uri currentSound = null;
                            String defaultPath = null;
                            Uri defaultUri = Settings.System.DEFAULT_NOTIFICATION_URI;
                            if (defaultUri != null) {
                                defaultPath = defaultUri.getPath();
                            }
                            String path = preferences2.getString("ringtone_path_" + ProfileNotificationsActivity.this.dialogId, defaultPath);
                            if (path != null && !path.equals("NoSound")) {
                                currentSound = path.equals(defaultPath) ? defaultUri : Uri.parse(path);
                            }
                            tmpIntent.putExtra("android.intent.extra.ringtone.EXISTING_URI", currentSound);
                            ProfileNotificationsActivity.this.startActivityForResult(tmpIntent, 13);
                        } catch (Exception e) {
                            FileLog.e((Throwable) e);
                        }
                    } else if (position == ProfileNotificationsActivity.this.vibrateRow) {
                        ProfileNotificationsActivity profileNotificationsActivity3 = ProfileNotificationsActivity.this;
                        profileNotificationsActivity3.showDialog(AlertsCreator.createVibrationSelectDialog(profileNotificationsActivity3.getParentActivity(), ProfileNotificationsActivity.this.dialogId, false, false, new ProfileNotificationsActivity$3$$ExternalSyntheticLambda0(this)));
                    } else if (position == ProfileNotificationsActivity.this.enableRow) {
                        TextCheckCell checkCell = (TextCheckCell) view;
                        boolean unused3 = ProfileNotificationsActivity.this.notificationsEnabled = true ^ checkCell.isChecked();
                        checkCell.setChecked(ProfileNotificationsActivity.this.notificationsEnabled);
                        ProfileNotificationsActivity.this.checkRowsEnabled();
                    } else if (position == ProfileNotificationsActivity.this.previewRow) {
                        TextCheckCell checkCell2 = (TextCheckCell) view;
                        SharedPreferences.Editor edit2 = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount).edit();
                        edit2.putBoolean("content_preview_" + ProfileNotificationsActivity.this.dialogId, !checkCell2.isChecked()).commit();
                        checkCell2.setChecked(true ^ checkCell2.isChecked());
                    } else if (position == ProfileNotificationsActivity.this.callsVibrateRow) {
                        ProfileNotificationsActivity profileNotificationsActivity4 = ProfileNotificationsActivity.this;
                        Activity parentActivity = profileNotificationsActivity4.getParentActivity();
                        long access$400 = ProfileNotificationsActivity.this.dialogId;
                        profileNotificationsActivity4.showDialog(AlertsCreator.createVibrationSelectDialog(parentActivity, access$400, "calls_vibrate_" + ProfileNotificationsActivity.this.dialogId, new ProfileNotificationsActivity$3$$ExternalSyntheticLambda1(this)));
                    } else if (position == ProfileNotificationsActivity.this.priorityRow) {
                        ProfileNotificationsActivity profileNotificationsActivity5 = ProfileNotificationsActivity.this;
                        profileNotificationsActivity5.showDialog(AlertsCreator.createPrioritySelectDialog(profileNotificationsActivity5.getParentActivity(), ProfileNotificationsActivity.this.dialogId, -1, new ProfileNotificationsActivity$3$$ExternalSyntheticLambda2(this)));
                    } else if (position == ProfileNotificationsActivity.this.smartRow) {
                        if (ProfileNotificationsActivity.this.getParentActivity() != null) {
                            Activity parentActivity2 = ProfileNotificationsActivity.this.getParentActivity();
                            SharedPreferences preferences3 = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount);
                            int notifyMaxCount = preferences3.getInt("smart_max_count_" + ProfileNotificationsActivity.this.dialogId, 2);
                            int notifyDelay = preferences3.getInt("smart_delay_" + ProfileNotificationsActivity.this.dialogId, 180);
                            if (notifyMaxCount == 0) {
                                notifyMaxCount = 2;
                            }
                            AlertsCreator.createSoundFrequencyPickerDialog(ProfileNotificationsActivity.this.getParentActivity(), notifyMaxCount, notifyDelay, new ProfileNotificationsActivity$3$$ExternalSyntheticLambda4(this));
                        }
                    } else if (position == ProfileNotificationsActivity.this.colorRow) {
                        if (ProfileNotificationsActivity.this.getParentActivity() != null) {
                            ProfileNotificationsActivity profileNotificationsActivity6 = ProfileNotificationsActivity.this;
                            profileNotificationsActivity6.showDialog(AlertsCreator.createColorSelectDialog(profileNotificationsActivity6.getParentActivity(), ProfileNotificationsActivity.this.dialogId, -1, new ProfileNotificationsActivity$3$$ExternalSyntheticLambda3(this)));
                        }
                    } else if (position == ProfileNotificationsActivity.this.popupEnabledRow) {
                        SharedPreferences.Editor edit3 = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount).edit();
                        edit3.putInt("popup_" + ProfileNotificationsActivity.this.dialogId, 1).commit();
                        ((RadioCell) view).setChecked(true, true);
                        View view2 = ProfileNotificationsActivity.this.listView.findViewWithTag(2);
                        if (view2 != null) {
                            ((RadioCell) view2).setChecked(false, true);
                        }
                    } else if (position == ProfileNotificationsActivity.this.popupDisabledRow) {
                        SharedPreferences.Editor edit4 = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount).edit();
                        edit4.putInt("popup_" + ProfileNotificationsActivity.this.dialogId, 2).commit();
                        ((RadioCell) view).setChecked(true, true);
                        View view3 = ProfileNotificationsActivity.this.listView.findViewWithTag(1);
                        if (view3 != null) {
                            ((RadioCell) view3).setChecked(false, true);
                        }
                    }
                }
            }

            /* renamed from: lambda$onItemClick$0$org-telegram-ui-ProfileNotificationsActivity$3  reason: not valid java name */
            public /* synthetic */ void m3179xb2d964b2() {
                if (ProfileNotificationsActivity.this.adapter != null) {
                    ProfileNotificationsActivity.this.adapter.notifyItemChanged(ProfileNotificationsActivity.this.vibrateRow);
                }
            }

            /* renamed from: lambda$onItemClick$1$org-telegram-ui-ProfileNotificationsActivity$3  reason: not valid java name */
            public /* synthetic */ void m3180xeca40691() {
                if (ProfileNotificationsActivity.this.adapter != null) {
                    ProfileNotificationsActivity.this.adapter.notifyItemChanged(ProfileNotificationsActivity.this.callsVibrateRow);
                }
            }

            /* renamed from: lambda$onItemClick$2$org-telegram-ui-ProfileNotificationsActivity$3  reason: not valid java name */
            public /* synthetic */ void m3181x266ea870() {
                if (ProfileNotificationsActivity.this.adapter != null) {
                    ProfileNotificationsActivity.this.adapter.notifyItemChanged(ProfileNotificationsActivity.this.priorityRow);
                }
            }

            /* renamed from: lambda$onItemClick$3$org-telegram-ui-ProfileNotificationsActivity$3  reason: not valid java name */
            public /* synthetic */ void m3182x60394a4f(int time, int minute) {
                SharedPreferences.Editor edit = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount).edit();
                SharedPreferences.Editor putInt = edit.putInt("smart_max_count_" + ProfileNotificationsActivity.this.dialogId, time);
                putInt.putInt("smart_delay_" + ProfileNotificationsActivity.this.dialogId, minute).apply();
                if (ProfileNotificationsActivity.this.adapter != null) {
                    ProfileNotificationsActivity.this.adapter.notifyItemChanged(ProfileNotificationsActivity.this.smartRow);
                }
            }

            /* renamed from: lambda$onItemClick$4$org-telegram-ui-ProfileNotificationsActivity$3  reason: not valid java name */
            public /* synthetic */ void m3183x9a03ec2e() {
                if (ProfileNotificationsActivity.this.adapter != null) {
                    ProfileNotificationsActivity.this.adapter.notifyItemChanged(ProfileNotificationsActivity.this.colorRow);
                }
            }
        });
        return this.fragmentView;
    }

    public void onActivityResultFragment(int requestCode, int resultCode, Intent data) {
        Ringtone rng;
        if (resultCode == -1 && data != null) {
            Uri ringtone = (Uri) data.getParcelableExtra("android.intent.extra.ringtone.PICKED_URI");
            String name = null;
            if (!(ringtone == null || (rng = RingtoneManager.getRingtone(ApplicationLoader.applicationContext, ringtone)) == null)) {
                if (requestCode == 13) {
                    if (ringtone.equals(Settings.System.DEFAULT_RINGTONE_URI)) {
                        name = LocaleController.getString("DefaultRingtone", NUM);
                    } else {
                        name = rng.getTitle(getParentActivity());
                    }
                } else if (ringtone.equals(Settings.System.DEFAULT_NOTIFICATION_URI)) {
                    name = LocaleController.getString("SoundDefault", NUM);
                } else {
                    name = rng.getTitle(getParentActivity());
                }
                rng.stop();
            }
            SharedPreferences.Editor editor = MessagesController.getNotificationsSettings(this.currentAccount).edit();
            if (requestCode == 12) {
                if (name != null) {
                    editor.putString("sound_" + this.dialogId, name);
                    editor.putString("sound_path_" + this.dialogId, ringtone.toString());
                } else {
                    editor.putString("sound_" + this.dialogId, "NoSound");
                    editor.putString("sound_path_" + this.dialogId, "NoSound");
                }
                getNotificationsController().deleteNotificationChannel(this.dialogId);
            } else if (requestCode == 13) {
                if (name != null) {
                    editor.putString("ringtone_" + this.dialogId, name);
                    editor.putString("ringtone_path_" + this.dialogId, ringtone.toString());
                } else {
                    editor.putString("ringtone_" + this.dialogId, "NoSound");
                    editor.putString("ringtone_path_" + this.dialogId, "NoSound");
                }
            }
            editor.commit();
            ListAdapter listAdapter = this.adapter;
            if (listAdapter != null) {
                listAdapter.notifyItemChanged(requestCode == 13 ? this.ringtoneRow : this.soundRow);
            }
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.notificationsSettingsUpdated) {
            try {
                this.adapter.notifyDataSetChanged();
            } catch (Exception e) {
            }
        }
    }

    public void setDelegate(ProfileNotificationsActivityDelegate profileNotificationsActivityDelegate) {
        this.delegate = profileNotificationsActivityDelegate;
    }

    /* access modifiers changed from: private */
    public void checkRowsEnabled() {
        int count = this.listView.getChildCount();
        ArrayList<Animator> animators = new ArrayList<>();
        for (int a = 0; a < count; a++) {
            RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.getChildViewHolder(this.listView.getChildAt(a));
            int type = holder.getItemViewType();
            int position = holder.getAdapterPosition();
            if (!(position == this.customRow || position == this.enableRow)) {
                boolean z = false;
                switch (type) {
                    case 0:
                        HeaderCell textCell = (HeaderCell) holder.itemView;
                        if (this.customEnabled && this.notificationsEnabled) {
                            z = true;
                        }
                        textCell.setEnabled(z, animators);
                        break;
                    case 1:
                        TextSettingsCell textCell2 = (TextSettingsCell) holder.itemView;
                        if (this.customEnabled && this.notificationsEnabled) {
                            z = true;
                        }
                        textCell2.setEnabled(z, animators);
                        break;
                    case 2:
                        TextInfoPrivacyCell textCell3 = (TextInfoPrivacyCell) holder.itemView;
                        if (this.customEnabled && this.notificationsEnabled) {
                            z = true;
                        }
                        textCell3.setEnabled(z, animators);
                        break;
                    case 3:
                        TextColorCell textCell4 = (TextColorCell) holder.itemView;
                        if (this.customEnabled && this.notificationsEnabled) {
                            z = true;
                        }
                        textCell4.setEnabled(z, animators);
                        break;
                    case 4:
                        RadioCell radioCell = (RadioCell) holder.itemView;
                        if (this.customEnabled && this.notificationsEnabled) {
                            z = true;
                        }
                        radioCell.setEnabled(z, animators);
                        break;
                    case 8:
                        if (position != this.previewRow) {
                            break;
                        } else {
                            TextCheckCell checkCell = (TextCheckCell) holder.itemView;
                            if (this.customEnabled && this.notificationsEnabled) {
                                z = true;
                            }
                            checkCell.setEnabled(z, animators);
                            break;
                        }
                }
            }
        }
        if (animators.isEmpty() == 0) {
            AnimatorSet animatorSet2 = this.animatorSet;
            if (animatorSet2 != null) {
                animatorSet2.cancel();
            }
            AnimatorSet animatorSet3 = new AnimatorSet();
            this.animatorSet = animatorSet3;
            animatorSet3.playTogether(animators);
            this.animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    if (animator.equals(ProfileNotificationsActivity.this.animatorSet)) {
                        AnimatorSet unused = ProfileNotificationsActivity.this.animatorSet = null;
                    }
                }
            });
            this.animatorSet.setDuration(150);
            this.animatorSet.start();
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context context;

        public ListAdapter(Context ctx) {
            this.context = ctx;
        }

        public int getItemCount() {
            return ProfileNotificationsActivity.this.rowCount;
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            switch (holder.getItemViewType()) {
                case 0:
                case 2:
                case 6:
                case 7:
                    return false;
                case 1:
                case 3:
                case 4:
                    if (!ProfileNotificationsActivity.this.customEnabled || !ProfileNotificationsActivity.this.notificationsEnabled) {
                        return false;
                    }
                    return true;
                case 8:
                    TextCheckCell textCheckCell = (TextCheckCell) holder.itemView;
                    if (holder.getAdapterPosition() != ProfileNotificationsActivity.this.previewRow) {
                        return true;
                    }
                    if (!ProfileNotificationsActivity.this.customEnabled || !ProfileNotificationsActivity.this.notificationsEnabled) {
                        return false;
                    }
                    return true;
                default:
                    return true;
            }
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    View headerCell = new HeaderCell(this.context);
                    headerCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    view = headerCell;
                    break;
                case 1:
                    View textSettingsCell = new TextSettingsCell(this.context);
                    textSettingsCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    view = textSettingsCell;
                    break;
                case 2:
                    view = new TextInfoPrivacyCell(this.context);
                    break;
                case 3:
                    View textColorCell = new TextColorCell(this.context);
                    textColorCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    view = textColorCell;
                    break;
                case 4:
                    View radioCell = new RadioCell(this.context);
                    radioCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    view = radioCell;
                    break;
                case 5:
                    TextCheckCell checkBoxCell = new TextCheckCell(this.context);
                    checkBoxCell.setHeight(56);
                    checkBoxCell.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                    checkBoxCell.setColors("windowBackgroundCheckText", "switchTrackBlue", "switchTrackBlueChecked", "switchTrackBlueThumb", "switchTrackBlueThumbChecked");
                    TextCheckCell textCheckCell = checkBoxCell;
                    view = checkBoxCell;
                    break;
                case 6:
                    View userCell2 = new UserCell2(this.context, 4, 0);
                    userCell2.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    view = userCell2;
                    break;
                case 7:
                    view = new ShadowSectionCell(this.context);
                    break;
                default:
                    View textCheckCell2 = new TextCheckCell(this.context);
                    textCheckCell2.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    view = textCheckCell2;
                    break;
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            int color;
            TLObject object;
            RecyclerView.ViewHolder viewHolder = holder;
            int i = position;
            boolean z = false;
            boolean z2 = true;
            switch (holder.getItemViewType()) {
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
                    TextSettingsCell textCell = (TextSettingsCell) viewHolder.itemView;
                    SharedPreferences preferences = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount);
                    if (i == ProfileNotificationsActivity.this.soundRow) {
                        String value = preferences.getString("sound_" + ProfileNotificationsActivity.this.dialogId, LocaleController.getString("SoundDefault", NUM));
                        long documentId = preferences.getLong("sound_document_id_" + ProfileNotificationsActivity.this.dialogId, 0);
                        if (documentId != 0) {
                            TLRPC.Document document = ProfileNotificationsActivity.this.getMediaDataController().ringtoneDataStore.getDocument(documentId);
                            if (document == null) {
                                value = LocaleController.getString("CustomSound", NUM);
                            } else {
                                value = NotificationsSoundActivity.trimTitle(document, document.file_name_fixed);
                            }
                        } else if (value.equals("NoSound")) {
                            value = LocaleController.getString("NoSound", NUM);
                        } else if (value.equals("Default")) {
                            value = LocaleController.getString("SoundDefault", NUM);
                        }
                        textCell.setTextAndValue(LocaleController.getString("Sound", NUM), value, true);
                        return;
                    } else if (i == ProfileNotificationsActivity.this.ringtoneRow) {
                        String value2 = preferences.getString("ringtone_" + ProfileNotificationsActivity.this.dialogId, LocaleController.getString("DefaultRingtone", NUM));
                        if (value2.equals("NoSound")) {
                            value2 = LocaleController.getString("NoSound", NUM);
                        }
                        textCell.setTextAndValue(LocaleController.getString("VoipSettingsRingtone", NUM), value2, false);
                        return;
                    } else if (i == ProfileNotificationsActivity.this.vibrateRow) {
                        int value3 = preferences.getInt("vibrate_" + ProfileNotificationsActivity.this.dialogId, 0);
                        if (value3 == 0 || value3 == 4) {
                            String string = LocaleController.getString("Vibrate", NUM);
                            String string2 = LocaleController.getString("VibrationDefault", NUM);
                            if (!(ProfileNotificationsActivity.this.smartRow == -1 && ProfileNotificationsActivity.this.priorityRow == -1)) {
                                z = true;
                            }
                            textCell.setTextAndValue(string, string2, z);
                            return;
                        } else if (value3 == 1) {
                            String string3 = LocaleController.getString("Vibrate", NUM);
                            String string4 = LocaleController.getString("Short", NUM);
                            if (!(ProfileNotificationsActivity.this.smartRow == -1 && ProfileNotificationsActivity.this.priorityRow == -1)) {
                                z = true;
                            }
                            textCell.setTextAndValue(string3, string4, z);
                            return;
                        } else if (value3 == 2) {
                            String string5 = LocaleController.getString("Vibrate", NUM);
                            String string6 = LocaleController.getString("VibrationDisabled", NUM);
                            if (!(ProfileNotificationsActivity.this.smartRow == -1 && ProfileNotificationsActivity.this.priorityRow == -1)) {
                                z = true;
                            }
                            textCell.setTextAndValue(string5, string6, z);
                            return;
                        } else if (value3 == 3) {
                            String string7 = LocaleController.getString("Vibrate", NUM);
                            String string8 = LocaleController.getString("Long", NUM);
                            if (!(ProfileNotificationsActivity.this.smartRow == -1 && ProfileNotificationsActivity.this.priorityRow == -1)) {
                                z = true;
                            }
                            textCell.setTextAndValue(string7, string8, z);
                            return;
                        } else {
                            return;
                        }
                    } else if (i == ProfileNotificationsActivity.this.priorityRow) {
                        int value4 = preferences.getInt("priority_" + ProfileNotificationsActivity.this.dialogId, 3);
                        if (value4 == 0) {
                            textCell.setTextAndValue(LocaleController.getString("NotificationsImportance", NUM), LocaleController.getString("NotificationsPriorityHigh", NUM), false);
                            return;
                        } else if (value4 == 1 || value4 == 2) {
                            textCell.setTextAndValue(LocaleController.getString("NotificationsImportance", NUM), LocaleController.getString("NotificationsPriorityUrgent", NUM), false);
                            return;
                        } else if (value4 == 3) {
                            textCell.setTextAndValue(LocaleController.getString("NotificationsImportance", NUM), LocaleController.getString("NotificationsPrioritySettings", NUM), false);
                            return;
                        } else if (value4 == 4) {
                            textCell.setTextAndValue(LocaleController.getString("NotificationsImportance", NUM), LocaleController.getString("NotificationsPriorityLow", NUM), false);
                            return;
                        } else if (value4 == 5) {
                            textCell.setTextAndValue(LocaleController.getString("NotificationsImportance", NUM), LocaleController.getString("NotificationsPriorityMedium", NUM), false);
                            return;
                        } else {
                            return;
                        }
                    } else if (i == ProfileNotificationsActivity.this.smartRow) {
                        int notifyMaxCount = preferences.getInt("smart_max_count_" + ProfileNotificationsActivity.this.dialogId, 2);
                        int notifyDelay = preferences.getInt("smart_delay_" + ProfileNotificationsActivity.this.dialogId, 180);
                        if (notifyMaxCount == 0) {
                            String string9 = LocaleController.getString("SmartNotifications", NUM);
                            String string10 = LocaleController.getString("SmartNotificationsDisabled", NUM);
                            if (ProfileNotificationsActivity.this.priorityRow != -1) {
                                z = true;
                            }
                            textCell.setTextAndValue(string9, string10, z);
                            return;
                        }
                        String minutes = LocaleController.formatPluralString("Minutes", notifyDelay / 60);
                        String string11 = LocaleController.getString("SmartNotifications", NUM);
                        String formatString = LocaleController.formatString("SmartNotificationsInfo", NUM, Integer.valueOf(notifyMaxCount), minutes);
                        if (ProfileNotificationsActivity.this.priorityRow != -1) {
                            z = true;
                        }
                        textCell.setTextAndValue(string11, formatString, z);
                        return;
                    } else if (i == ProfileNotificationsActivity.this.callsVibrateRow) {
                        int value5 = preferences.getInt("calls_vibrate_" + ProfileNotificationsActivity.this.dialogId, 0);
                        if (value5 == 0 || value5 == 4) {
                            textCell.setTextAndValue(LocaleController.getString("Vibrate", NUM), LocaleController.getString("VibrationDefault", NUM), true);
                            return;
                        } else if (value5 == 1) {
                            textCell.setTextAndValue(LocaleController.getString("Vibrate", NUM), LocaleController.getString("Short", NUM), true);
                            return;
                        } else if (value5 == 2) {
                            textCell.setTextAndValue(LocaleController.getString("Vibrate", NUM), LocaleController.getString("VibrationDisabled", NUM), true);
                            return;
                        } else if (value5 == 3) {
                            textCell.setTextAndValue(LocaleController.getString("Vibrate", NUM), LocaleController.getString("Long", NUM), true);
                            return;
                        } else {
                            return;
                        }
                    } else {
                        return;
                    }
                case 2:
                    TextInfoPrivacyCell textCell2 = (TextInfoPrivacyCell) viewHolder.itemView;
                    if (i == ProfileNotificationsActivity.this.popupInfoRow) {
                        textCell2.setText(LocaleController.getString("ProfilePopupNotificationInfo", NUM));
                        textCell2.setBackgroundDrawable(Theme.getThemedDrawable(this.context, NUM, "windowBackgroundGrayShadow"));
                        return;
                    } else if (i == ProfileNotificationsActivity.this.ledInfoRow) {
                        textCell2.setText(LocaleController.getString("NotificationsLedInfo", NUM));
                        textCell2.setBackgroundDrawable(Theme.getThemedDrawable(this.context, NUM, "windowBackgroundGrayShadow"));
                        return;
                    } else if (i == ProfileNotificationsActivity.this.priorityInfoRow) {
                        if (ProfileNotificationsActivity.this.priorityRow == -1) {
                            textCell2.setText("");
                        } else {
                            textCell2.setText(LocaleController.getString("PriorityInfo", NUM));
                        }
                        textCell2.setBackgroundDrawable(Theme.getThemedDrawable(this.context, NUM, "windowBackgroundGrayShadow"));
                        return;
                    } else if (i == ProfileNotificationsActivity.this.customInfoRow) {
                        textCell2.setText((CharSequence) null);
                        textCell2.setBackgroundDrawable(Theme.getThemedDrawable(this.context, NUM, "windowBackgroundGrayShadow"));
                        return;
                    } else if (i == ProfileNotificationsActivity.this.ringtoneInfoRow) {
                        textCell2.setText(LocaleController.getString("VoipRingtoneInfo", NUM));
                        textCell2.setBackgroundDrawable(Theme.getThemedDrawable(this.context, NUM, "windowBackgroundGrayShadow"));
                        return;
                    } else {
                        return;
                    }
                case 3:
                    TextColorCell textCell3 = (TextColorCell) viewHolder.itemView;
                    SharedPreferences preferences2 = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount);
                    if (preferences2.contains("color_" + ProfileNotificationsActivity.this.dialogId)) {
                        color = preferences2.getInt("color_" + ProfileNotificationsActivity.this.dialogId, -16776961);
                    } else if (DialogObject.isChatDialog(ProfileNotificationsActivity.this.dialogId)) {
                        color = preferences2.getInt("GroupLed", -16776961);
                    } else {
                        color = preferences2.getInt("MessagesLed", -16776961);
                    }
                    int a = 0;
                    while (true) {
                        if (a < 9) {
                            if (TextColorCell.colorsToSave[a] == color) {
                                color = TextColorCell.colors[a];
                            } else {
                                a++;
                            }
                        }
                    }
                    textCell3.setTextAndColor(LocaleController.getString("NotificationsLedColor", NUM), color, false);
                    return;
                case 4:
                    RadioCell radioCell = (RadioCell) viewHolder.itemView;
                    SharedPreferences preferences3 = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount);
                    int popup = preferences3.getInt("popup_" + ProfileNotificationsActivity.this.dialogId, 0);
                    if (popup == 0) {
                        if (preferences3.getInt(DialogObject.isChatDialog(ProfileNotificationsActivity.this.dialogId) ? "popupGroup" : "popupAll", 0) != 0) {
                            popup = 1;
                        } else {
                            popup = 2;
                        }
                    }
                    if (i == ProfileNotificationsActivity.this.popupEnabledRow) {
                        String string12 = LocaleController.getString("PopupEnabled", NUM);
                        if (popup == 1) {
                            z = true;
                        }
                        radioCell.setText(string12, z, true);
                        radioCell.setTag(1);
                        return;
                    } else if (i == ProfileNotificationsActivity.this.popupDisabledRow) {
                        String string13 = LocaleController.getString("PopupDisabled", NUM);
                        if (popup != 2) {
                            z2 = false;
                        }
                        radioCell.setText(string13, z2, false);
                        radioCell.setTag(2);
                        return;
                    } else {
                        return;
                    }
                case 5:
                    TextCheckCell cell = (TextCheckCell) viewHolder.itemView;
                    cell.setBackgroundColor(Theme.getColor((!ProfileNotificationsActivity.this.customEnabled || !ProfileNotificationsActivity.this.notificationsEnabled) ? "windowBackgroundUnchecked" : "windowBackgroundChecked"));
                    String string14 = LocaleController.getString("NotificationsEnableCustom", NUM);
                    if (!ProfileNotificationsActivity.this.customEnabled || !ProfileNotificationsActivity.this.notificationsEnabled) {
                        z2 = false;
                    }
                    cell.setTextAndCheck(string14, z2, false);
                    return;
                case 6:
                    UserCell2 userCell2 = (UserCell2) viewHolder.itemView;
                    if (DialogObject.isUserDialog(ProfileNotificationsActivity.this.dialogId)) {
                        object = MessagesController.getInstance(ProfileNotificationsActivity.this.currentAccount).getUser(Long.valueOf(ProfileNotificationsActivity.this.dialogId));
                    } else {
                        object = MessagesController.getInstance(ProfileNotificationsActivity.this.currentAccount).getChat(Long.valueOf(-ProfileNotificationsActivity.this.dialogId));
                    }
                    userCell2.setData(object, (CharSequence) null, (CharSequence) null, 0);
                    return;
                case 8:
                    TextCheckCell checkCell = (TextCheckCell) viewHolder.itemView;
                    SharedPreferences preferences4 = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount);
                    if (i == ProfileNotificationsActivity.this.enableRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("Notifications", NUM), ProfileNotificationsActivity.this.notificationsEnabled, true);
                        return;
                    } else if (i == ProfileNotificationsActivity.this.previewRow) {
                        String string15 = LocaleController.getString("MessagePreview", NUM);
                        checkCell.setTextAndCheck(string15, preferences4.getBoolean("content_preview_" + ProfileNotificationsActivity.this.dialogId, true), true);
                        return;
                    } else {
                        return;
                    }
                default:
                    return;
            }
        }

        public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
            boolean z = false;
            switch (holder.getItemViewType()) {
                case 0:
                    HeaderCell textCell = (HeaderCell) holder.itemView;
                    if (ProfileNotificationsActivity.this.customEnabled && ProfileNotificationsActivity.this.notificationsEnabled) {
                        z = true;
                    }
                    textCell.setEnabled(z, (ArrayList<Animator>) null);
                    return;
                case 1:
                    TextSettingsCell textCell2 = (TextSettingsCell) holder.itemView;
                    if (ProfileNotificationsActivity.this.customEnabled && ProfileNotificationsActivity.this.notificationsEnabled) {
                        z = true;
                    }
                    textCell2.setEnabled(z, (ArrayList<Animator>) null);
                    return;
                case 2:
                    TextInfoPrivacyCell textCell3 = (TextInfoPrivacyCell) holder.itemView;
                    if (ProfileNotificationsActivity.this.customEnabled && ProfileNotificationsActivity.this.notificationsEnabled) {
                        z = true;
                    }
                    textCell3.setEnabled(z, (ArrayList<Animator>) null);
                    return;
                case 3:
                    TextColorCell textCell4 = (TextColorCell) holder.itemView;
                    if (ProfileNotificationsActivity.this.customEnabled && ProfileNotificationsActivity.this.notificationsEnabled) {
                        z = true;
                    }
                    textCell4.setEnabled(z, (ArrayList<Animator>) null);
                    return;
                case 4:
                    RadioCell radioCell = (RadioCell) holder.itemView;
                    if (ProfileNotificationsActivity.this.customEnabled && ProfileNotificationsActivity.this.notificationsEnabled) {
                        z = true;
                    }
                    radioCell.setEnabled(z, (ArrayList<Animator>) null);
                    return;
                case 8:
                    TextCheckCell checkCell = (TextCheckCell) holder.itemView;
                    if (holder.getAdapterPosition() == ProfileNotificationsActivity.this.previewRow) {
                        if (ProfileNotificationsActivity.this.customEnabled && ProfileNotificationsActivity.this.notificationsEnabled) {
                            z = true;
                        }
                        checkCell.setEnabled(z, (ArrayList<Animator>) null);
                        return;
                    }
                    checkCell.setEnabled(true, (ArrayList<Animator>) null);
                    return;
                default:
                    return;
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
            if (position == ProfileNotificationsActivity.this.enableRow || position == ProfileNotificationsActivity.this.previewRow) {
                return 8;
            }
            return 0;
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        ThemeDescription.ThemeDescriptionDelegate cellDelegate = new ProfileNotificationsActivity$$ExternalSyntheticLambda0(this);
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{HeaderCell.class, TextSettingsCell.class, TextColorCell.class, RadioCell.class, UserCell2.class, TextCheckCell.class, TextCheckBoxCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteValueText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextColorCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{RadioCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKBOX, new Class[]{RadioCell.class}, new String[]{"radioButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "radioBackground"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{RadioCell.class}, new String[]{"radioButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "radioBackgroundChecked"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrack"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrackChecked"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{UserCell2.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{UserCell2.class}, new String[]{"statusColor"}, (Paint[]) null, (Drawable[]) null, cellDelegate, "windowBackgroundWhiteGrayText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{UserCell2.class}, new String[]{"statusOnlineColor"}, (Paint[]) null, (Drawable[]) null, cellDelegate, "windowBackgroundWhiteBlueText"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{UserCell2.class}, (Paint) null, Theme.avatarDrawables, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_text"));
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate = cellDelegate;
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "avatar_backgroundRed"));
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate2 = cellDelegate;
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, "avatar_backgroundOrange"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "avatar_backgroundViolet"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, "avatar_backgroundGreen"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "avatar_backgroundCyan"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, "avatar_backgroundBlue"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "avatar_backgroundPink"));
        return themeDescriptions;
    }

    /* renamed from: lambda$getThemeDescriptions$0$org-telegram-ui-ProfileNotificationsActivity  reason: not valid java name */
    public /* synthetic */ void m3178x84cbvar_() {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            int count = recyclerListView.getChildCount();
            for (int a = 0; a < count; a++) {
                View child = this.listView.getChildAt(a);
                if (child instanceof UserCell2) {
                    ((UserCell2) child).update(0);
                }
            }
        }
    }
}
