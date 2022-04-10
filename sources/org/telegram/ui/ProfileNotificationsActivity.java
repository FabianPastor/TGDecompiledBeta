package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
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
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationsController;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$Dialog;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$TL_peerNotifySettings;
import org.telegram.tgnet.TLRPC$User;
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

    public ProfileNotificationsActivity(Bundle bundle) {
        super(bundle);
        this.dialogId = bundle.getLong("dialog_id");
        this.addingException = bundle.getBoolean("exception", false);
    }

    /* JADX WARNING: Removed duplicated region for block: B:35:0x00e5  */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x0100  */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x0140  */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x0142  */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x0173  */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x0187  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onFragmentCreate() {
        /*
            r8 = this;
            r0 = 0
            r8.rowCount = r0
            boolean r1 = r8.addingException
            r2 = -1
            r3 = 1
            if (r1 == 0) goto L_0x001a
            r4 = 0
            int r4 = r4 + r3
            r8.rowCount = r4
            r8.avatarRow = r0
            int r5 = r4 + 1
            r8.rowCount = r5
            r8.avatarSectionRow = r4
            r8.customRow = r2
            r8.customInfoRow = r2
            goto L_0x002a
        L_0x001a:
            r8.avatarRow = r2
            r8.avatarSectionRow = r2
            r4 = 0
            int r4 = r4 + r3
            r8.rowCount = r4
            r8.customRow = r0
            int r5 = r4 + 1
            r8.rowCount = r5
            r8.customInfoRow = r4
        L_0x002a:
            int r4 = r8.rowCount
            int r5 = r4 + 1
            r8.rowCount = r5
            r8.generalRow = r4
            if (r1 == 0) goto L_0x003b
            int r1 = r5 + 1
            r8.rowCount = r1
            r8.enableRow = r5
            goto L_0x003d
        L_0x003b:
            r8.enableRow = r2
        L_0x003d:
            long r4 = r8.dialogId
            boolean r1 = org.telegram.messenger.DialogObject.isEncryptedDialog(r4)
            if (r1 != 0) goto L_0x004e
            int r1 = r8.rowCount
            int r4 = r1 + 1
            r8.rowCount = r4
            r8.previewRow = r1
            goto L_0x0050
        L_0x004e:
            r8.previewRow = r2
        L_0x0050:
            int r1 = r8.rowCount
            int r4 = r1 + 1
            r8.rowCount = r4
            r8.soundRow = r1
            int r1 = r4 + 1
            r8.rowCount = r1
            r8.vibrateRow = r4
            long r4 = r8.dialogId
            boolean r1 = org.telegram.messenger.DialogObject.isChatDialog(r4)
            if (r1 == 0) goto L_0x006f
            int r1 = r8.rowCount
            int r4 = r1 + 1
            r8.rowCount = r4
            r8.smartRow = r1
            goto L_0x0071
        L_0x006f:
            r8.smartRow = r2
        L_0x0071:
            int r1 = android.os.Build.VERSION.SDK_INT
            r4 = 21
            if (r1 < r4) goto L_0x0080
            int r1 = r8.rowCount
            int r4 = r1 + 1
            r8.rowCount = r4
            r8.priorityRow = r1
            goto L_0x0082
        L_0x0080:
            r8.priorityRow = r2
        L_0x0082:
            int r1 = r8.rowCount
            int r4 = r1 + 1
            r8.rowCount = r4
            r8.priorityInfoRow = r1
            long r4 = r8.dialogId
            boolean r1 = org.telegram.messenger.DialogObject.isChatDialog(r4)
            if (r1 == 0) goto L_0x00af
            int r1 = r8.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            long r4 = r8.dialogId
            long r4 = -r4
            java.lang.Long r4 = java.lang.Long.valueOf(r4)
            org.telegram.tgnet.TLRPC$Chat r1 = r1.getChat(r4)
            boolean r4 = org.telegram.messenger.ChatObject.isChannel(r1)
            if (r4 == 0) goto L_0x00af
            boolean r1 = r1.megagroup
            if (r1 != 0) goto L_0x00af
            r1 = 1
            goto L_0x00b0
        L_0x00af:
            r1 = 0
        L_0x00b0:
            long r4 = r8.dialogId
            boolean r4 = org.telegram.messenger.DialogObject.isEncryptedDialog(r4)
            if (r4 != 0) goto L_0x00d5
            if (r1 != 0) goto L_0x00d5
            int r1 = r8.rowCount
            int r4 = r1 + 1
            r8.rowCount = r4
            r8.popupRow = r1
            int r1 = r4 + 1
            r8.rowCount = r1
            r8.popupEnabledRow = r4
            int r4 = r1 + 1
            r8.rowCount = r4
            r8.popupDisabledRow = r1
            int r1 = r4 + 1
            r8.rowCount = r1
            r8.popupInfoRow = r4
            goto L_0x00dd
        L_0x00d5:
            r8.popupRow = r2
            r8.popupEnabledRow = r2
            r8.popupDisabledRow = r2
            r8.popupInfoRow = r2
        L_0x00dd:
            long r4 = r8.dialogId
            boolean r1 = org.telegram.messenger.DialogObject.isUserDialog(r4)
            if (r1 == 0) goto L_0x0100
            int r1 = r8.rowCount
            int r2 = r1 + 1
            r8.rowCount = r2
            r8.callsRow = r1
            int r1 = r2 + 1
            r8.rowCount = r1
            r8.callsVibrateRow = r2
            int r2 = r1 + 1
            r8.rowCount = r2
            r8.ringtoneRow = r1
            int r1 = r2 + 1
            r8.rowCount = r1
            r8.ringtoneInfoRow = r2
            goto L_0x0108
        L_0x0100:
            r8.callsRow = r2
            r8.callsVibrateRow = r2
            r8.ringtoneRow = r2
            r8.ringtoneInfoRow = r2
        L_0x0108:
            int r1 = r8.rowCount
            int r2 = r1 + 1
            r8.rowCount = r2
            r8.ledRow = r1
            int r1 = r2 + 1
            r8.rowCount = r1
            r8.colorRow = r2
            int r2 = r1 + 1
            r8.rowCount = r2
            r8.ledInfoRow = r1
            int r1 = r8.currentAccount
            android.content.SharedPreferences r1 = org.telegram.messenger.MessagesController.getNotificationsSettings(r1)
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r4 = "custom_"
            r2.append(r4)
            long r4 = r8.dialogId
            r2.append(r4)
            java.lang.String r2 = r2.toString()
            boolean r2 = r1.getBoolean(r2, r0)
            if (r2 != 0) goto L_0x0142
            boolean r2 = r8.addingException
            if (r2 == 0) goto L_0x0140
            goto L_0x0142
        L_0x0140:
            r2 = 0
            goto L_0x0143
        L_0x0142:
            r2 = 1
        L_0x0143:
            r8.customEnabled = r2
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r4 = "notify2_"
            r2.append(r4)
            long r5 = r8.dialogId
            r2.append(r5)
            java.lang.String r2 = r2.toString()
            boolean r2 = r1.contains(r2)
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            r5.append(r4)
            long r6 = r8.dialogId
            r5.append(r6)
            java.lang.String r4 = r5.toString()
            int r1 = r1.getInt(r4, r0)
            if (r1 != 0) goto L_0x0187
            if (r2 == 0) goto L_0x0178
            r8.notificationsEnabled = r3
            goto L_0x0194
        L_0x0178:
            int r0 = r8.currentAccount
            org.telegram.messenger.NotificationsController r0 = org.telegram.messenger.NotificationsController.getInstance(r0)
            long r1 = r8.dialogId
            boolean r0 = r0.isGlobalNotificationsEnabled((long) r1)
            r8.notificationsEnabled = r0
            goto L_0x0194
        L_0x0187:
            if (r1 != r3) goto L_0x018c
            r8.notificationsEnabled = r3
            goto L_0x0194
        L_0x018c:
            r2 = 2
            if (r1 != r2) goto L_0x0192
            r8.notificationsEnabled = r0
            goto L_0x0194
        L_0x0192:
            r8.notificationsEnabled = r0
        L_0x0194:
            int r0 = r8.currentAccount
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getInstance(r0)
            int r1 = org.telegram.messenger.NotificationCenter.notificationsSettingsUpdated
            r0.addObserver(r8, r1)
            boolean r0 = super.onFragmentCreate()
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ProfileNotificationsActivity.onFragmentCreate():boolean");
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.notificationsSettingsUpdated);
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    if (!ProfileNotificationsActivity.this.addingException && ProfileNotificationsActivity.this.notificationsEnabled && ProfileNotificationsActivity.this.customEnabled) {
                        SharedPreferences.Editor edit = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount).edit();
                        edit.putInt("notify2_" + ProfileNotificationsActivity.this.dialogId, 0).commit();
                    }
                } else if (i == 1) {
                    SharedPreferences notificationsSettings = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount);
                    SharedPreferences.Editor edit2 = notificationsSettings.edit();
                    edit2.putBoolean("custom_" + ProfileNotificationsActivity.this.dialogId, true);
                    TLRPC$Dialog tLRPC$Dialog = MessagesController.getInstance(ProfileNotificationsActivity.this.currentAccount).dialogs_dict.get(ProfileNotificationsActivity.this.dialogId);
                    if (ProfileNotificationsActivity.this.notificationsEnabled) {
                        edit2.putInt("notify2_" + ProfileNotificationsActivity.this.dialogId, 0);
                        MessagesStorage.getInstance(ProfileNotificationsActivity.this.currentAccount).setDialogFlags(ProfileNotificationsActivity.this.dialogId, 0);
                        if (tLRPC$Dialog != null) {
                            tLRPC$Dialog.notify_settings = new TLRPC$TL_peerNotifySettings();
                        }
                    } else {
                        edit2.putInt("notify2_" + ProfileNotificationsActivity.this.dialogId, 2);
                        NotificationsController.getInstance(ProfileNotificationsActivity.this.currentAccount).removeNotificationsForDialog(ProfileNotificationsActivity.this.dialogId);
                        MessagesStorage.getInstance(ProfileNotificationsActivity.this.currentAccount).setDialogFlags(ProfileNotificationsActivity.this.dialogId, 1);
                        if (tLRPC$Dialog != null) {
                            TLRPC$TL_peerNotifySettings tLRPC$TL_peerNotifySettings = new TLRPC$TL_peerNotifySettings();
                            tLRPC$Dialog.notify_settings = tLRPC$TL_peerNotifySettings;
                            tLRPC$TL_peerNotifySettings.mute_until = Integer.MAX_VALUE;
                        }
                    }
                    edit2.commit();
                    NotificationsController.getInstance(ProfileNotificationsActivity.this.currentAccount).updateServerNotificationsSettings(ProfileNotificationsActivity.this.dialogId);
                    if (ProfileNotificationsActivity.this.delegate != null) {
                        NotificationsSettingsActivity.NotificationException notificationException = new NotificationsSettingsActivity.NotificationException();
                        notificationException.did = ProfileNotificationsActivity.this.dialogId;
                        notificationException.hasCustom = true;
                        int i2 = notificationsSettings.getInt("notify2_" + ProfileNotificationsActivity.this.dialogId, 0);
                        notificationException.notify = i2;
                        if (i2 != 0) {
                            notificationException.muteUntil = notificationsSettings.getInt("notifyuntil_" + ProfileNotificationsActivity.this.dialogId, 0);
                        }
                        ProfileNotificationsActivity.this.delegate.didCreateNewException(notificationException);
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
            TLRPC$Chat chat = getMessagesController().getChat(Long.valueOf(-this.dialogId));
            this.avatarContainer.setChatAvatar(chat);
            this.avatarContainer.setTitle(chat.title);
        } else {
            TLRPC$User user = getMessagesController().getUser(Long.valueOf(this.dialogId));
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
        FrameLayout frameLayout = new FrameLayout(context);
        this.fragmentView = frameLayout;
        FrameLayout frameLayout2 = frameLayout;
        frameLayout2.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        frameLayout2.addView(recyclerListView, LayoutHelper.createFrame(-1, -1.0f));
        RecyclerListView recyclerListView2 = this.listView;
        ListAdapter listAdapter = new ListAdapter(context);
        this.adapter = listAdapter;
        recyclerListView2.setAdapter(listAdapter);
        this.listView.setItemAnimator((RecyclerView.ItemAnimator) null);
        this.listView.setLayoutAnimation((LayoutAnimationController) null);
        this.listView.setLayoutManager(new LinearLayoutManager(this, context) {
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        });
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public void onItemClick(View view, int i) {
                if (i == ProfileNotificationsActivity.this.customRow && (view instanceof TextCheckCell)) {
                    SharedPreferences notificationsSettings = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount);
                    ProfileNotificationsActivity profileNotificationsActivity = ProfileNotificationsActivity.this;
                    boolean unused = profileNotificationsActivity.customEnabled = true ^ profileNotificationsActivity.customEnabled;
                    ProfileNotificationsActivity profileNotificationsActivity2 = ProfileNotificationsActivity.this;
                    boolean unused2 = profileNotificationsActivity2.notificationsEnabled = profileNotificationsActivity2.customEnabled;
                    notificationsSettings.edit().putBoolean("custom_" + ProfileNotificationsActivity.this.dialogId, ProfileNotificationsActivity.this.customEnabled).apply();
                    TextCheckCell textCheckCell = (TextCheckCell) view;
                    textCheckCell.setChecked(ProfileNotificationsActivity.this.customEnabled);
                    int color = Theme.getColor(ProfileNotificationsActivity.this.customEnabled ? "windowBackgroundChecked" : "windowBackgroundUnchecked");
                    if (ProfileNotificationsActivity.this.customEnabled) {
                        textCheckCell.setBackgroundColorAnimated(ProfileNotificationsActivity.this.customEnabled, color);
                    } else {
                        textCheckCell.setBackgroundColorAnimatedReverse(color);
                    }
                    ProfileNotificationsActivity.this.checkRowsEnabled();
                } else if (ProfileNotificationsActivity.this.customEnabled && view.isEnabled()) {
                    if (i == ProfileNotificationsActivity.this.soundRow) {
                        Bundle bundle = new Bundle();
                        bundle.putLong("dialog_id", ProfileNotificationsActivity.this.dialogId);
                        ProfileNotificationsActivity.this.presentFragment(new NotificationsSoundActivity(bundle));
                    } else if (i == ProfileNotificationsActivity.this.ringtoneRow) {
                        try {
                            Intent intent = new Intent("android.intent.action.RINGTONE_PICKER");
                            intent.putExtra("android.intent.extra.ringtone.TYPE", 1);
                            intent.putExtra("android.intent.extra.ringtone.SHOW_DEFAULT", true);
                            intent.putExtra("android.intent.extra.ringtone.SHOW_SILENT", true);
                            intent.putExtra("android.intent.extra.ringtone.DEFAULT_URI", RingtoneManager.getDefaultUri(1));
                            SharedPreferences notificationsSettings2 = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount);
                            Uri uri = Settings.System.DEFAULT_NOTIFICATION_URI;
                            String path = uri != null ? uri.getPath() : null;
                            String string = notificationsSettings2.getString("ringtone_path_" + ProfileNotificationsActivity.this.dialogId, path);
                            if (string == null || string.equals("NoSound")) {
                                uri = null;
                            } else if (!string.equals(path)) {
                                uri = Uri.parse(string);
                            }
                            intent.putExtra("android.intent.extra.ringtone.EXISTING_URI", uri);
                            ProfileNotificationsActivity.this.startActivityForResult(intent, 13);
                        } catch (Exception e) {
                            FileLog.e((Throwable) e);
                        }
                    } else if (i == ProfileNotificationsActivity.this.vibrateRow) {
                        ProfileNotificationsActivity profileNotificationsActivity3 = ProfileNotificationsActivity.this;
                        profileNotificationsActivity3.showDialog(AlertsCreator.createVibrationSelectDialog(profileNotificationsActivity3.getParentActivity(), ProfileNotificationsActivity.this.dialogId, false, false, new ProfileNotificationsActivity$3$$ExternalSyntheticLambda1(this)));
                    } else if (i == ProfileNotificationsActivity.this.enableRow) {
                        TextCheckCell textCheckCell2 = (TextCheckCell) view;
                        boolean unused3 = ProfileNotificationsActivity.this.notificationsEnabled = !textCheckCell2.isChecked();
                        textCheckCell2.setChecked(ProfileNotificationsActivity.this.notificationsEnabled);
                        ProfileNotificationsActivity.this.checkRowsEnabled();
                    } else if (i == ProfileNotificationsActivity.this.previewRow) {
                        TextCheckCell textCheckCell3 = (TextCheckCell) view;
                        MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount).edit().putBoolean("content_preview_" + ProfileNotificationsActivity.this.dialogId, !textCheckCell3.isChecked()).commit();
                        textCheckCell3.setChecked(textCheckCell3.isChecked() ^ true);
                    } else if (i == ProfileNotificationsActivity.this.callsVibrateRow) {
                        ProfileNotificationsActivity profileNotificationsActivity4 = ProfileNotificationsActivity.this;
                        profileNotificationsActivity4.showDialog(AlertsCreator.createVibrationSelectDialog(profileNotificationsActivity4.getParentActivity(), ProfileNotificationsActivity.this.dialogId, "calls_vibrate_" + ProfileNotificationsActivity.this.dialogId, new ProfileNotificationsActivity$3$$ExternalSyntheticLambda2(this)));
                    } else if (i == ProfileNotificationsActivity.this.priorityRow) {
                        ProfileNotificationsActivity profileNotificationsActivity5 = ProfileNotificationsActivity.this;
                        profileNotificationsActivity5.showDialog(AlertsCreator.createPrioritySelectDialog(profileNotificationsActivity5.getParentActivity(), ProfileNotificationsActivity.this.dialogId, -1, new ProfileNotificationsActivity$3$$ExternalSyntheticLambda3(this)));
                    } else {
                        int i2 = 2;
                        if (i == ProfileNotificationsActivity.this.smartRow) {
                            if (ProfileNotificationsActivity.this.getParentActivity() != null) {
                                ProfileNotificationsActivity.this.getParentActivity();
                                SharedPreferences notificationsSettings3 = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount);
                                int i3 = notificationsSettings3.getInt("smart_max_count_" + ProfileNotificationsActivity.this.dialogId, 2);
                                int i4 = notificationsSettings3.getInt("smart_delay_" + ProfileNotificationsActivity.this.dialogId, 180);
                                if (i3 != 0) {
                                    i2 = i3;
                                }
                                AlertsCreator.createSoundFrequencyPickerDialog(ProfileNotificationsActivity.this.getParentActivity(), i2, i4, new ProfileNotificationsActivity$3$$ExternalSyntheticLambda4(this));
                            }
                        } else if (i == ProfileNotificationsActivity.this.colorRow) {
                            if (ProfileNotificationsActivity.this.getParentActivity() != null) {
                                ProfileNotificationsActivity profileNotificationsActivity6 = ProfileNotificationsActivity.this;
                                profileNotificationsActivity6.showDialog(AlertsCreator.createColorSelectDialog(profileNotificationsActivity6.getParentActivity(), ProfileNotificationsActivity.this.dialogId, -1, new ProfileNotificationsActivity$3$$ExternalSyntheticLambda0(this)));
                            }
                        } else if (i == ProfileNotificationsActivity.this.popupEnabledRow) {
                            MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount).edit().putInt("popup_" + ProfileNotificationsActivity.this.dialogId, 1).commit();
                            ((RadioCell) view).setChecked(true, true);
                            View findViewWithTag = ProfileNotificationsActivity.this.listView.findViewWithTag(2);
                            if (findViewWithTag != null) {
                                ((RadioCell) findViewWithTag).setChecked(false, true);
                            }
                        } else if (i == ProfileNotificationsActivity.this.popupDisabledRow) {
                            MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount).edit().putInt("popup_" + ProfileNotificationsActivity.this.dialogId, 2).commit();
                            ((RadioCell) view).setChecked(true, true);
                            View findViewWithTag2 = ProfileNotificationsActivity.this.listView.findViewWithTag(1);
                            if (findViewWithTag2 != null) {
                                ((RadioCell) findViewWithTag2).setChecked(false, true);
                            }
                        }
                    }
                }
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$onItemClick$0() {
                if (ProfileNotificationsActivity.this.adapter != null) {
                    ProfileNotificationsActivity.this.adapter.notifyItemChanged(ProfileNotificationsActivity.this.vibrateRow);
                }
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$onItemClick$1() {
                if (ProfileNotificationsActivity.this.adapter != null) {
                    ProfileNotificationsActivity.this.adapter.notifyItemChanged(ProfileNotificationsActivity.this.callsVibrateRow);
                }
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$onItemClick$2() {
                if (ProfileNotificationsActivity.this.adapter != null) {
                    ProfileNotificationsActivity.this.adapter.notifyItemChanged(ProfileNotificationsActivity.this.priorityRow);
                }
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$onItemClick$3(int i, int i2) {
                SharedPreferences.Editor edit = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount).edit();
                SharedPreferences.Editor putInt = edit.putInt("smart_max_count_" + ProfileNotificationsActivity.this.dialogId, i);
                putInt.putInt("smart_delay_" + ProfileNotificationsActivity.this.dialogId, i2).apply();
                if (ProfileNotificationsActivity.this.adapter != null) {
                    ProfileNotificationsActivity.this.adapter.notifyItemChanged(ProfileNotificationsActivity.this.smartRow);
                }
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$onItemClick$4() {
                if (ProfileNotificationsActivity.this.adapter != null) {
                    ProfileNotificationsActivity.this.adapter.notifyItemChanged(ProfileNotificationsActivity.this.colorRow);
                }
            }
        });
        return this.fragmentView;
    }

    public void onActivityResultFragment(int i, int i2, Intent intent) {
        Ringtone ringtone;
        if (i2 == -1 && intent != null) {
            Uri uri = (Uri) intent.getParcelableExtra("android.intent.extra.ringtone.PICKED_URI");
            String str = null;
            if (!(uri == null || (ringtone = RingtoneManager.getRingtone(ApplicationLoader.applicationContext, uri)) == null)) {
                if (i == 13) {
                    if (uri.equals(Settings.System.DEFAULT_RINGTONE_URI)) {
                        str = LocaleController.getString("DefaultRingtone", NUM);
                    } else {
                        str = ringtone.getTitle(getParentActivity());
                    }
                } else if (uri.equals(Settings.System.DEFAULT_NOTIFICATION_URI)) {
                    str = LocaleController.getString("SoundDefault", NUM);
                } else {
                    str = ringtone.getTitle(getParentActivity());
                }
                ringtone.stop();
            }
            SharedPreferences.Editor edit = MessagesController.getNotificationsSettings(this.currentAccount).edit();
            if (i == 12) {
                if (str != null) {
                    edit.putString("sound_" + this.dialogId, str);
                    edit.putString("sound_path_" + this.dialogId, uri.toString());
                } else {
                    edit.putString("sound_" + this.dialogId, "NoSound");
                    edit.putString("sound_path_" + this.dialogId, "NoSound");
                }
                getNotificationsController().deleteNotificationChannel(this.dialogId);
            } else if (i == 13) {
                if (str != null) {
                    edit.putString("ringtone_" + this.dialogId, str);
                    edit.putString("ringtone_path_" + this.dialogId, uri.toString());
                } else {
                    edit.putString("ringtone_" + this.dialogId, "NoSound");
                    edit.putString("ringtone_path_" + this.dialogId, "NoSound");
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

    /* access modifiers changed from: private */
    public void checkRowsEnabled() {
        int childCount = this.listView.getChildCount();
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < childCount; i++) {
            RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.getChildViewHolder(this.listView.getChildAt(i));
            int itemViewType = holder.getItemViewType();
            int adapterPosition = holder.getAdapterPosition();
            if (!(adapterPosition == this.customRow || adapterPosition == this.enableRow || itemViewType == 0)) {
                boolean z = true;
                if (itemViewType == 1) {
                    TextSettingsCell textSettingsCell = (TextSettingsCell) holder.itemView;
                    if (!this.customEnabled || !this.notificationsEnabled) {
                        z = false;
                    }
                    textSettingsCell.setEnabled(z, arrayList);
                } else if (itemViewType == 2) {
                    TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) holder.itemView;
                    if (!this.customEnabled || !this.notificationsEnabled) {
                        z = false;
                    }
                    textInfoPrivacyCell.setEnabled(z, arrayList);
                } else if (itemViewType == 3) {
                    TextColorCell textColorCell = (TextColorCell) holder.itemView;
                    if (!this.customEnabled || !this.notificationsEnabled) {
                        z = false;
                    }
                    textColorCell.setEnabled(z, arrayList);
                } else if (itemViewType == 4) {
                    RadioCell radioCell = (RadioCell) holder.itemView;
                    if (!this.customEnabled || !this.notificationsEnabled) {
                        z = false;
                    }
                    radioCell.setEnabled(z, arrayList);
                } else if (itemViewType == 8 && adapterPosition == this.previewRow) {
                    TextCheckCell textCheckCell = (TextCheckCell) holder.itemView;
                    if (!this.customEnabled || !this.notificationsEnabled) {
                        z = false;
                    }
                    textCheckCell.setEnabled(z, arrayList);
                }
            }
        }
        if (!arrayList.isEmpty()) {
            AnimatorSet animatorSet2 = this.animatorSet;
            if (animatorSet2 != null) {
                animatorSet2.cancel();
            }
            AnimatorSet animatorSet3 = new AnimatorSet();
            this.animatorSet = animatorSet3;
            animatorSet3.playTogether(arrayList);
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

        public ListAdapter(Context context2) {
            this.context = context2;
        }

        public int getItemCount() {
            return ProfileNotificationsActivity.this.rowCount;
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            switch (viewHolder.getItemViewType()) {
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
                    TextCheckCell textCheckCell = (TextCheckCell) viewHolder.itemView;
                    if (viewHolder.getAdapterPosition() != ProfileNotificationsActivity.this.previewRow) {
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

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            ShadowSectionCell shadowSectionCell;
            View view;
            switch (i) {
                case 0:
                    view = new HeaderCell(this.context);
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    break;
                case 1:
                    view = new TextSettingsCell(this.context);
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    break;
                case 2:
                    shadowSectionCell = new TextInfoPrivacyCell(this.context);
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
                    TextCheckCell textCheckCell = new TextCheckCell(this.context);
                    textCheckCell.setHeight(56);
                    textCheckCell.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                    textCheckCell.setColors("windowBackgroundCheckText", "switchTrackBlue", "switchTrackBlueChecked", "switchTrackBlueThumb", "switchTrackBlueThumbChecked");
                    shadowSectionCell = textCheckCell;
                    break;
                case 6:
                    view = new UserCell2(this.context, 4, 0);
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    break;
                case 7:
                    shadowSectionCell = new ShadowSectionCell(this.context);
                    break;
                default:
                    view = new TextCheckCell(this.context);
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    break;
            }
            shadowSectionCell = view;
            shadowSectionCell.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(shadowSectionCell);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            int i2;
            TLObject tLObject;
            RecyclerView.ViewHolder viewHolder2 = viewHolder;
            int i3 = i;
            boolean z = false;
            boolean z2 = true;
            switch (viewHolder.getItemViewType()) {
                case 0:
                    HeaderCell headerCell = (HeaderCell) viewHolder2.itemView;
                    if (i3 == ProfileNotificationsActivity.this.generalRow) {
                        headerCell.setText(LocaleController.getString("General", NUM));
                        return;
                    } else if (i3 == ProfileNotificationsActivity.this.popupRow) {
                        headerCell.setText(LocaleController.getString("ProfilePopupNotification", NUM));
                        return;
                    } else if (i3 == ProfileNotificationsActivity.this.ledRow) {
                        headerCell.setText(LocaleController.getString("NotificationsLed", NUM));
                        return;
                    } else if (i3 == ProfileNotificationsActivity.this.callsRow) {
                        headerCell.setText(LocaleController.getString("VoipNotificationSettings", NUM));
                        return;
                    } else {
                        return;
                    }
                case 1:
                    TextSettingsCell textSettingsCell = (TextSettingsCell) viewHolder2.itemView;
                    SharedPreferences notificationsSettings = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount);
                    if (i3 == ProfileNotificationsActivity.this.soundRow) {
                        String string = notificationsSettings.getString("sound_" + ProfileNotificationsActivity.this.dialogId, LocaleController.getString("SoundDefault", NUM));
                        long j = notificationsSettings.getLong("sound_document_id_" + ProfileNotificationsActivity.this.dialogId, 0);
                        if (j != 0) {
                            TLRPC$Document document = ProfileNotificationsActivity.this.getMediaDataController().ringtoneDataStore.getDocument(j);
                            string = NotificationsSoundActivity.trimTitle(document, document.file_name_fixed);
                        } else if (string.equals("NoSound")) {
                            string = LocaleController.getString("NoSound", NUM);
                        } else if (string.equals("Default")) {
                            string = LocaleController.getString("SoundDefault", NUM);
                        }
                        textSettingsCell.setTextAndValue(LocaleController.getString("Sound", NUM), string, true);
                        return;
                    } else if (i3 == ProfileNotificationsActivity.this.ringtoneRow) {
                        String string2 = notificationsSettings.getString("ringtone_" + ProfileNotificationsActivity.this.dialogId, LocaleController.getString("DefaultRingtone", NUM));
                        if (string2.equals("NoSound")) {
                            string2 = LocaleController.getString("NoSound", NUM);
                        }
                        textSettingsCell.setTextAndValue(LocaleController.getString("VoipSettingsRingtone", NUM), string2, false);
                        return;
                    } else if (i3 == ProfileNotificationsActivity.this.vibrateRow) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("vibrate_");
                        String str = "VibrationDefault";
                        sb.append(ProfileNotificationsActivity.this.dialogId);
                        int i4 = notificationsSettings.getInt(sb.toString(), 0);
                        if (i4 == 0 || i4 == 4) {
                            String string3 = LocaleController.getString("Vibrate", NUM);
                            String string4 = LocaleController.getString(str, NUM);
                            if (!(ProfileNotificationsActivity.this.smartRow == -1 && ProfileNotificationsActivity.this.priorityRow == -1)) {
                                z = true;
                            }
                            textSettingsCell.setTextAndValue(string3, string4, z);
                            return;
                        } else if (i4 == 1) {
                            String string5 = LocaleController.getString("Vibrate", NUM);
                            String string6 = LocaleController.getString("Short", NUM);
                            if (!(ProfileNotificationsActivity.this.smartRow == -1 && ProfileNotificationsActivity.this.priorityRow == -1)) {
                                z = true;
                            }
                            textSettingsCell.setTextAndValue(string5, string6, z);
                            return;
                        } else if (i4 == 2) {
                            String string7 = LocaleController.getString("Vibrate", NUM);
                            String string8 = LocaleController.getString("VibrationDisabled", NUM);
                            if (!(ProfileNotificationsActivity.this.smartRow == -1 && ProfileNotificationsActivity.this.priorityRow == -1)) {
                                z = true;
                            }
                            textSettingsCell.setTextAndValue(string7, string8, z);
                            return;
                        } else if (i4 == 3) {
                            String string9 = LocaleController.getString("Vibrate", NUM);
                            String string10 = LocaleController.getString("Long", NUM);
                            if (!(ProfileNotificationsActivity.this.smartRow == -1 && ProfileNotificationsActivity.this.priorityRow == -1)) {
                                z = true;
                            }
                            textSettingsCell.setTextAndValue(string9, string10, z);
                            return;
                        } else {
                            return;
                        }
                    } else {
                        String str2 = "VibrationDefault";
                        if (i3 == ProfileNotificationsActivity.this.priorityRow) {
                            int i5 = notificationsSettings.getInt("priority_" + ProfileNotificationsActivity.this.dialogId, 3);
                            if (i5 == 0) {
                                textSettingsCell.setTextAndValue(LocaleController.getString("NotificationsImportance", NUM), LocaleController.getString("NotificationsPriorityHigh", NUM), false);
                                return;
                            } else if (i5 == 1 || i5 == 2) {
                                textSettingsCell.setTextAndValue(LocaleController.getString("NotificationsImportance", NUM), LocaleController.getString("NotificationsPriorityUrgent", NUM), false);
                                return;
                            } else if (i5 == 3) {
                                textSettingsCell.setTextAndValue(LocaleController.getString("NotificationsImportance", NUM), LocaleController.getString("NotificationsPrioritySettings", NUM), false);
                                return;
                            } else if (i5 == 4) {
                                textSettingsCell.setTextAndValue(LocaleController.getString("NotificationsImportance", NUM), LocaleController.getString("NotificationsPriorityLow", NUM), false);
                                return;
                            } else if (i5 == 5) {
                                textSettingsCell.setTextAndValue(LocaleController.getString("NotificationsImportance", NUM), LocaleController.getString("NotificationsPriorityMedium", NUM), false);
                                return;
                            } else {
                                return;
                            }
                        } else if (i3 == ProfileNotificationsActivity.this.smartRow) {
                            int i6 = notificationsSettings.getInt("smart_max_count_" + ProfileNotificationsActivity.this.dialogId, 2);
                            int i7 = notificationsSettings.getInt("smart_delay_" + ProfileNotificationsActivity.this.dialogId, 180);
                            if (i6 == 0) {
                                String string11 = LocaleController.getString("SmartNotifications", NUM);
                                String string12 = LocaleController.getString("SmartNotificationsDisabled", NUM);
                                if (ProfileNotificationsActivity.this.priorityRow != -1) {
                                    z = true;
                                }
                                textSettingsCell.setTextAndValue(string11, string12, z);
                                return;
                            }
                            String formatPluralString = LocaleController.formatPluralString("Minutes", i7 / 60);
                            String string13 = LocaleController.getString("SmartNotifications", NUM);
                            String formatString = LocaleController.formatString("SmartNotificationsInfo", NUM, Integer.valueOf(i6), formatPluralString);
                            if (ProfileNotificationsActivity.this.priorityRow != -1) {
                                z = true;
                            }
                            textSettingsCell.setTextAndValue(string13, formatString, z);
                            return;
                        } else if (i3 == ProfileNotificationsActivity.this.callsVibrateRow) {
                            int i8 = notificationsSettings.getInt("calls_vibrate_" + ProfileNotificationsActivity.this.dialogId, 0);
                            if (i8 == 0 || i8 == 4) {
                                textSettingsCell.setTextAndValue(LocaleController.getString("Vibrate", NUM), LocaleController.getString(str2, NUM), true);
                                return;
                            } else if (i8 == 1) {
                                textSettingsCell.setTextAndValue(LocaleController.getString("Vibrate", NUM), LocaleController.getString("Short", NUM), true);
                                return;
                            } else if (i8 == 2) {
                                textSettingsCell.setTextAndValue(LocaleController.getString("Vibrate", NUM), LocaleController.getString("VibrationDisabled", NUM), true);
                                return;
                            } else if (i8 == 3) {
                                textSettingsCell.setTextAndValue(LocaleController.getString("Vibrate", NUM), LocaleController.getString("Long", NUM), true);
                                return;
                            } else {
                                return;
                            }
                        } else {
                            return;
                        }
                    }
                case 2:
                    TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) viewHolder2.itemView;
                    if (i3 == ProfileNotificationsActivity.this.popupInfoRow) {
                        textInfoPrivacyCell.setText(LocaleController.getString("ProfilePopupNotificationInfo", NUM));
                        textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.context, NUM, "windowBackgroundGrayShadow"));
                        return;
                    } else if (i3 == ProfileNotificationsActivity.this.ledInfoRow) {
                        textInfoPrivacyCell.setText(LocaleController.getString("NotificationsLedInfo", NUM));
                        textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.context, NUM, "windowBackgroundGrayShadow"));
                        return;
                    } else if (i3 == ProfileNotificationsActivity.this.priorityInfoRow) {
                        if (ProfileNotificationsActivity.this.priorityRow == -1) {
                            textInfoPrivacyCell.setText("");
                        } else {
                            textInfoPrivacyCell.setText(LocaleController.getString("PriorityInfo", NUM));
                        }
                        textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.context, NUM, "windowBackgroundGrayShadow"));
                        return;
                    } else if (i3 == ProfileNotificationsActivity.this.customInfoRow) {
                        textInfoPrivacyCell.setText((CharSequence) null);
                        textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.context, NUM, "windowBackgroundGrayShadow"));
                        return;
                    } else if (i3 == ProfileNotificationsActivity.this.ringtoneInfoRow) {
                        textInfoPrivacyCell.setText(LocaleController.getString("VoipRingtoneInfo", NUM));
                        textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.context, NUM, "windowBackgroundGrayShadow"));
                        return;
                    } else {
                        return;
                    }
                case 3:
                    TextColorCell textColorCell = (TextColorCell) viewHolder2.itemView;
                    SharedPreferences notificationsSettings2 = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount);
                    if (notificationsSettings2.contains("color_" + ProfileNotificationsActivity.this.dialogId)) {
                        i2 = notificationsSettings2.getInt("color_" + ProfileNotificationsActivity.this.dialogId, -16776961);
                    } else if (DialogObject.isChatDialog(ProfileNotificationsActivity.this.dialogId)) {
                        i2 = notificationsSettings2.getInt("GroupLed", -16776961);
                    } else {
                        i2 = notificationsSettings2.getInt("MessagesLed", -16776961);
                    }
                    int i9 = 0;
                    while (true) {
                        if (i9 < 9) {
                            if (TextColorCell.colorsToSave[i9] == i2) {
                                i2 = TextColorCell.colors[i9];
                            } else {
                                i9++;
                            }
                        }
                    }
                    textColorCell.setTextAndColor(LocaleController.getString("NotificationsLedColor", NUM), i2, false);
                    return;
                case 4:
                    RadioCell radioCell = (RadioCell) viewHolder2.itemView;
                    SharedPreferences notificationsSettings3 = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount);
                    int i10 = notificationsSettings3.getInt("popup_" + ProfileNotificationsActivity.this.dialogId, 0);
                    if (i10 == 0) {
                        i10 = notificationsSettings3.getInt(DialogObject.isChatDialog(ProfileNotificationsActivity.this.dialogId) ? "popupGroup" : "popupAll", 0) != 0 ? 1 : 2;
                    }
                    if (i3 == ProfileNotificationsActivity.this.popupEnabledRow) {
                        String string14 = LocaleController.getString("PopupEnabled", NUM);
                        if (i10 == 1) {
                            z = true;
                        }
                        radioCell.setText(string14, z, true);
                        radioCell.setTag(1);
                        return;
                    } else if (i3 == ProfileNotificationsActivity.this.popupDisabledRow) {
                        String string15 = LocaleController.getString("PopupDisabled", NUM);
                        if (i10 != 2) {
                            z2 = false;
                        }
                        radioCell.setText(string15, z2, false);
                        radioCell.setTag(2);
                        return;
                    } else {
                        return;
                    }
                case 5:
                    TextCheckCell textCheckCell = (TextCheckCell) viewHolder2.itemView;
                    textCheckCell.setBackgroundColor(Theme.getColor((!ProfileNotificationsActivity.this.customEnabled || !ProfileNotificationsActivity.this.notificationsEnabled) ? "windowBackgroundUnchecked" : "windowBackgroundChecked"));
                    String string16 = LocaleController.getString("NotificationsEnableCustom", NUM);
                    if (!ProfileNotificationsActivity.this.customEnabled || !ProfileNotificationsActivity.this.notificationsEnabled) {
                        z2 = false;
                    }
                    textCheckCell.setTextAndCheck(string16, z2, false);
                    return;
                case 6:
                    UserCell2 userCell2 = (UserCell2) viewHolder2.itemView;
                    if (DialogObject.isUserDialog(ProfileNotificationsActivity.this.dialogId)) {
                        tLObject = MessagesController.getInstance(ProfileNotificationsActivity.this.currentAccount).getUser(Long.valueOf(ProfileNotificationsActivity.this.dialogId));
                    } else {
                        tLObject = MessagesController.getInstance(ProfileNotificationsActivity.this.currentAccount).getChat(Long.valueOf(-ProfileNotificationsActivity.this.dialogId));
                    }
                    userCell2.setData(tLObject, (CharSequence) null, (CharSequence) null, 0);
                    return;
                case 8:
                    TextCheckCell textCheckCell2 = (TextCheckCell) viewHolder2.itemView;
                    SharedPreferences notificationsSettings4 = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount);
                    if (i3 == ProfileNotificationsActivity.this.enableRow) {
                        textCheckCell2.setTextAndCheck(LocaleController.getString("Notifications", NUM), ProfileNotificationsActivity.this.notificationsEnabled, true);
                        return;
                    } else if (i3 == ProfileNotificationsActivity.this.previewRow) {
                        String string17 = LocaleController.getString("MessagePreview", NUM);
                        textCheckCell2.setTextAndCheck(string17, notificationsSettings4.getBoolean("content_preview_" + ProfileNotificationsActivity.this.dialogId, true), true);
                        return;
                    } else {
                        return;
                    }
                default:
                    return;
            }
        }

        public void onViewAttachedToWindow(RecyclerView.ViewHolder viewHolder) {
            if (viewHolder.getItemViewType() != 0) {
                int itemViewType = viewHolder.getItemViewType();
                boolean z = false;
                if (itemViewType == 1) {
                    TextSettingsCell textSettingsCell = (TextSettingsCell) viewHolder.itemView;
                    if (ProfileNotificationsActivity.this.customEnabled && ProfileNotificationsActivity.this.notificationsEnabled) {
                        z = true;
                    }
                    textSettingsCell.setEnabled(z, (ArrayList<Animator>) null);
                } else if (itemViewType == 2) {
                    TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) viewHolder.itemView;
                    if (ProfileNotificationsActivity.this.customEnabled && ProfileNotificationsActivity.this.notificationsEnabled) {
                        z = true;
                    }
                    textInfoPrivacyCell.setEnabled(z, (ArrayList<Animator>) null);
                } else if (itemViewType == 3) {
                    TextColorCell textColorCell = (TextColorCell) viewHolder.itemView;
                    if (ProfileNotificationsActivity.this.customEnabled && ProfileNotificationsActivity.this.notificationsEnabled) {
                        z = true;
                    }
                    textColorCell.setEnabled(z, (ArrayList<Animator>) null);
                } else if (itemViewType == 4) {
                    RadioCell radioCell = (RadioCell) viewHolder.itemView;
                    if (ProfileNotificationsActivity.this.customEnabled && ProfileNotificationsActivity.this.notificationsEnabled) {
                        z = true;
                    }
                    radioCell.setEnabled(z, (ArrayList<Animator>) null);
                } else if (itemViewType == 8) {
                    TextCheckCell textCheckCell = (TextCheckCell) viewHolder.itemView;
                    if (viewHolder.getAdapterPosition() == ProfileNotificationsActivity.this.previewRow) {
                        if (ProfileNotificationsActivity.this.customEnabled && ProfileNotificationsActivity.this.notificationsEnabled) {
                            z = true;
                        }
                        textCheckCell.setEnabled(z, (ArrayList<Animator>) null);
                        return;
                    }
                    textCheckCell.setEnabled(true, (ArrayList<Animator>) null);
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

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        ProfileNotificationsActivity$$ExternalSyntheticLambda0 profileNotificationsActivity$$ExternalSyntheticLambda0 = new ProfileNotificationsActivity$$ExternalSyntheticLambda0(this);
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{HeaderCell.class, TextSettingsCell.class, TextColorCell.class, RadioCell.class, UserCell2.class, TextCheckCell.class, TextCheckBoxCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteValueText"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextColorCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{RadioCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKBOX, new Class[]{RadioCell.class}, new String[]{"radioButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "radioBackground"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{RadioCell.class}, new String[]{"radioButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "radioBackgroundChecked"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrack"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrackChecked"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{UserCell2.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        ProfileNotificationsActivity$$ExternalSyntheticLambda0 profileNotificationsActivity$$ExternalSyntheticLambda02 = profileNotificationsActivity$$ExternalSyntheticLambda0;
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{UserCell2.class}, new String[]{"statusColor"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) profileNotificationsActivity$$ExternalSyntheticLambda02, "windowBackgroundWhiteGrayText"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{UserCell2.class}, new String[]{"statusOnlineColor"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) profileNotificationsActivity$$ExternalSyntheticLambda02, "windowBackgroundWhiteBlueText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{UserCell2.class}, (Paint) null, Theme.avatarDrawables, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_text"));
        ProfileNotificationsActivity$$ExternalSyntheticLambda0 profileNotificationsActivity$$ExternalSyntheticLambda03 = profileNotificationsActivity$$ExternalSyntheticLambda0;
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, profileNotificationsActivity$$ExternalSyntheticLambda03, "avatar_backgroundRed"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, profileNotificationsActivity$$ExternalSyntheticLambda03, "avatar_backgroundOrange"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, profileNotificationsActivity$$ExternalSyntheticLambda03, "avatar_backgroundViolet"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, profileNotificationsActivity$$ExternalSyntheticLambda03, "avatar_backgroundGreen"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, profileNotificationsActivity$$ExternalSyntheticLambda03, "avatar_backgroundCyan"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, profileNotificationsActivity$$ExternalSyntheticLambda03, "avatar_backgroundBlue"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, profileNotificationsActivity$$ExternalSyntheticLambda03, "avatar_backgroundPink"));
        return arrayList;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$getThemeDescriptions$0() {
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
