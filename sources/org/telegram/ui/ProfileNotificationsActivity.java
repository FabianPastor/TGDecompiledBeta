package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.TextView;
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
import org.telegram.messenger.R;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$Dialog;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$TL_peerNotifySettings;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
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
    private ListAdapter adapter;
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
    public int customResetRow;
    /* access modifiers changed from: private */
    public int customResetShadowRow;
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
    private RecyclerListView listView;
    private boolean needReset;
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
    public Theme.ResourcesProvider resourcesProvider;
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

        /* renamed from: org.telegram.ui.ProfileNotificationsActivity$ProfileNotificationsActivityDelegate$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static void $default$didRemoveException(ProfileNotificationsActivityDelegate profileNotificationsActivityDelegate, long j) {
            }
        }

        void didCreateNewException(NotificationsSettingsActivity.NotificationException notificationException);

        void didRemoveException(long j);
    }

    public ProfileNotificationsActivity(Bundle bundle) {
        this(bundle, (Theme.ResourcesProvider) null);
    }

    public ProfileNotificationsActivity(Bundle bundle, Theme.ResourcesProvider resourcesProvider2) {
        super(bundle);
        this.resourcesProvider = resourcesProvider2;
        this.dialogId = bundle.getLong("dialog_id");
        this.addingException = bundle.getBoolean("exception", false);
    }

    /* JADX WARNING: Removed duplicated region for block: B:35:0x00d5  */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x00f0  */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x0110  */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x011d  */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x0131  */
    /* JADX WARNING: Removed duplicated region for block: B:44:0x0136  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onFragmentCreate() {
        /*
            r8 = this;
            r0 = 0
            r8.rowCount = r0
            boolean r1 = r8.addingException
            r2 = -1
            r3 = 1
            if (r1 == 0) goto L_0x0016
            r4 = 0
            int r4 = r4 + r3
            r8.rowCount = r4
            r8.avatarRow = r0
            int r5 = r4 + 1
            r8.rowCount = r5
            r8.avatarSectionRow = r4
            goto L_0x001a
        L_0x0016:
            r8.avatarRow = r2
            r8.avatarSectionRow = r2
        L_0x001a:
            int r4 = r8.rowCount
            int r5 = r4 + 1
            r8.rowCount = r5
            r8.generalRow = r4
            if (r1 == 0) goto L_0x002b
            int r1 = r5 + 1
            r8.rowCount = r1
            r8.enableRow = r5
            goto L_0x002d
        L_0x002b:
            r8.enableRow = r2
        L_0x002d:
            long r4 = r8.dialogId
            boolean r1 = org.telegram.messenger.DialogObject.isEncryptedDialog(r4)
            if (r1 != 0) goto L_0x003e
            int r1 = r8.rowCount
            int r4 = r1 + 1
            r8.rowCount = r4
            r8.previewRow = r1
            goto L_0x0040
        L_0x003e:
            r8.previewRow = r2
        L_0x0040:
            int r1 = r8.rowCount
            int r4 = r1 + 1
            r8.rowCount = r4
            r8.soundRow = r1
            int r1 = r4 + 1
            r8.rowCount = r1
            r8.vibrateRow = r4
            long r4 = r8.dialogId
            boolean r1 = org.telegram.messenger.DialogObject.isChatDialog(r4)
            if (r1 == 0) goto L_0x005f
            int r1 = r8.rowCount
            int r4 = r1 + 1
            r8.rowCount = r4
            r8.smartRow = r1
            goto L_0x0061
        L_0x005f:
            r8.smartRow = r2
        L_0x0061:
            int r1 = android.os.Build.VERSION.SDK_INT
            r4 = 21
            if (r1 < r4) goto L_0x0070
            int r1 = r8.rowCount
            int r4 = r1 + 1
            r8.rowCount = r4
            r8.priorityRow = r1
            goto L_0x0072
        L_0x0070:
            r8.priorityRow = r2
        L_0x0072:
            int r1 = r8.rowCount
            int r4 = r1 + 1
            r8.rowCount = r4
            r8.priorityInfoRow = r1
            long r4 = r8.dialogId
            boolean r1 = org.telegram.messenger.DialogObject.isChatDialog(r4)
            if (r1 == 0) goto L_0x009f
            int r1 = r8.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            long r4 = r8.dialogId
            long r4 = -r4
            java.lang.Long r4 = java.lang.Long.valueOf(r4)
            org.telegram.tgnet.TLRPC$Chat r1 = r1.getChat(r4)
            boolean r4 = org.telegram.messenger.ChatObject.isChannel(r1)
            if (r4 == 0) goto L_0x009f
            boolean r1 = r1.megagroup
            if (r1 != 0) goto L_0x009f
            r1 = 1
            goto L_0x00a0
        L_0x009f:
            r1 = 0
        L_0x00a0:
            long r4 = r8.dialogId
            boolean r4 = org.telegram.messenger.DialogObject.isEncryptedDialog(r4)
            if (r4 != 0) goto L_0x00c5
            if (r1 != 0) goto L_0x00c5
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
            goto L_0x00cd
        L_0x00c5:
            r8.popupRow = r2
            r8.popupEnabledRow = r2
            r8.popupDisabledRow = r2
            r8.popupInfoRow = r2
        L_0x00cd:
            long r4 = r8.dialogId
            boolean r1 = org.telegram.messenger.DialogObject.isUserDialog(r4)
            if (r1 == 0) goto L_0x00f0
            int r1 = r8.rowCount
            int r4 = r1 + 1
            r8.rowCount = r4
            r8.callsRow = r1
            int r1 = r4 + 1
            r8.rowCount = r1
            r8.callsVibrateRow = r4
            int r4 = r1 + 1
            r8.rowCount = r4
            r8.ringtoneRow = r1
            int r1 = r4 + 1
            r8.rowCount = r1
            r8.ringtoneInfoRow = r4
            goto L_0x00f8
        L_0x00f0:
            r8.callsRow = r2
            r8.callsVibrateRow = r2
            r8.ringtoneRow = r2
            r8.ringtoneInfoRow = r2
        L_0x00f8:
            int r1 = r8.rowCount
            int r4 = r1 + 1
            r8.rowCount = r4
            r8.ledRow = r1
            int r1 = r4 + 1
            r8.rowCount = r1
            r8.colorRow = r4
            int r4 = r1 + 1
            r8.rowCount = r4
            r8.ledInfoRow = r1
            boolean r1 = r8.addingException
            if (r1 != 0) goto L_0x011d
            int r1 = r4 + 1
            r8.rowCount = r1
            r8.customResetRow = r4
            int r2 = r1 + 1
            r8.rowCount = r2
            r8.customResetShadowRow = r1
            goto L_0x0121
        L_0x011d:
            r8.customResetRow = r2
            r8.customResetShadowRow = r2
        L_0x0121:
            int r1 = r8.currentAccount
            org.telegram.messenger.NotificationsController r1 = org.telegram.messenger.NotificationsController.getInstance(r1)
            long r4 = r8.dialogId
            boolean r1 = r1.isGlobalNotificationsEnabled((long) r4)
            boolean r2 = r8.addingException
            if (r2 == 0) goto L_0x0136
            r0 = r1 ^ 1
            r8.notificationsEnabled = r0
            goto L_0x018b
        L_0x0136:
            int r1 = r8.currentAccount
            android.content.SharedPreferences r1 = org.telegram.messenger.MessagesController.getNotificationsSettings(r1)
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
            if (r1 != 0) goto L_0x017e
            if (r2 == 0) goto L_0x016f
            r8.notificationsEnabled = r3
            goto L_0x018b
        L_0x016f:
            int r0 = r8.currentAccount
            org.telegram.messenger.NotificationsController r0 = org.telegram.messenger.NotificationsController.getInstance(r0)
            long r1 = r8.dialogId
            boolean r0 = r0.isGlobalNotificationsEnabled((long) r1)
            r8.notificationsEnabled = r0
            goto L_0x018b
        L_0x017e:
            if (r1 != r3) goto L_0x0183
            r8.notificationsEnabled = r3
            goto L_0x018b
        L_0x0183:
            r2 = 2
            if (r1 != r2) goto L_0x0189
            r8.notificationsEnabled = r0
            goto L_0x018b
        L_0x0189:
            r8.notificationsEnabled = r0
        L_0x018b:
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
        if (!this.needReset) {
            SharedPreferences.Editor edit = MessagesController.getNotificationsSettings(this.currentAccount).edit();
            edit.putBoolean("custom_" + this.dialogId, true).apply();
        }
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.notificationsSettingsUpdated);
    }

    public Theme.ResourcesProvider getResourceProvider() {
        return this.resourcesProvider;
    }

    public View createView(Context context) {
        this.actionBar.setItemsBackgroundColor(Theme.getColor("avatar_actionBarSelectorBlue", this.resourcesProvider), false);
        this.actionBar.setItemsColor(Theme.getColor("actionBarDefaultIcon", this.resourcesProvider), false);
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    if (!ProfileNotificationsActivity.this.addingException && ProfileNotificationsActivity.this.notificationsEnabled) {
                        SharedPreferences.Editor edit = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount).edit();
                        edit.putInt("notify2_" + ProfileNotificationsActivity.this.dialogId, 0).apply();
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
                    edit2.apply();
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
        ChatAvatarContainer chatAvatarContainer = new ChatAvatarContainer(context, (ChatActivity) null, false, this.resourcesProvider);
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
            this.avatarContainer.setSubtitle(LocaleController.getString("NotificationsNewException", R.string.NotificationsNewException));
            this.actionBar.createMenu().addItem(1, (CharSequence) LocaleController.getString("Done", R.string.Done).toUpperCase());
        } else {
            this.avatarContainer.setSubtitle(LocaleController.getString("CustomNotifications", R.string.CustomNotifications));
        }
        FrameLayout frameLayout = new FrameLayout(context);
        this.fragmentView = frameLayout;
        FrameLayout frameLayout2 = frameLayout;
        frameLayout2.setBackgroundColor(Theme.getColor("windowBackgroundGray", this.resourcesProvider));
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
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new ProfileNotificationsActivity$$ExternalSyntheticLambda7(this, context));
        return this.fragmentView;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$6(Context context, View view, int i) {
        if (view.isEnabled()) {
            Uri uri = null;
            if (i == this.customResetRow) {
                AlertDialog create = new AlertDialog.Builder(context, this.resourcesProvider).setTitle(LocaleController.getString(R.string.ResetCustomNotificationsAlertTitle)).setMessage(LocaleController.getString(R.string.ResetCustomNotificationsAlert)).setPositiveButton(LocaleController.getString(R.string.Reset), new ProfileNotificationsActivity$$ExternalSyntheticLambda0(this)).setNegativeButton(LocaleController.getString(R.string.Cancel), (DialogInterface.OnClickListener) null).create();
                showDialog(create);
                TextView textView = (TextView) create.getButton(-1);
                if (textView != null) {
                    textView.setTextColor(Theme.getColor("dialogTextRed2"));
                }
            } else if (i == this.soundRow) {
                Bundle bundle = new Bundle();
                bundle.putLong("dialog_id", this.dialogId);
                presentFragment(new NotificationsSoundActivity(bundle, this.resourcesProvider));
            } else if (i == this.ringtoneRow) {
                try {
                    Intent intent = new Intent("android.intent.action.RINGTONE_PICKER");
                    intent.putExtra("android.intent.extra.ringtone.TYPE", 1);
                    intent.putExtra("android.intent.extra.ringtone.SHOW_DEFAULT", true);
                    intent.putExtra("android.intent.extra.ringtone.SHOW_SILENT", true);
                    intent.putExtra("android.intent.extra.ringtone.DEFAULT_URI", RingtoneManager.getDefaultUri(1));
                    SharedPreferences notificationsSettings = MessagesController.getNotificationsSettings(this.currentAccount);
                    Uri uri2 = Settings.System.DEFAULT_NOTIFICATION_URI;
                    String path = uri2 != null ? uri2.getPath() : null;
                    String string = notificationsSettings.getString("ringtone_path_" + this.dialogId, path);
                    if (string != null && !string.equals("NoSound")) {
                        uri = string.equals(path) ? uri2 : Uri.parse(string);
                    }
                    intent.putExtra("android.intent.extra.ringtone.EXISTING_URI", uri);
                    startActivityForResult(intent, 13);
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            } else if (i == this.vibrateRow) {
                showDialog(AlertsCreator.createVibrationSelectDialog(getParentActivity(), this.dialogId, false, false, new ProfileNotificationsActivity$$ExternalSyntheticLambda1(this), this.resourcesProvider));
            } else if (i == this.enableRow) {
                TextCheckCell textCheckCell = (TextCheckCell) view;
                boolean z = !textCheckCell.isChecked();
                this.notificationsEnabled = z;
                textCheckCell.setChecked(z);
                checkRowsEnabled();
            } else if (i == this.previewRow) {
                TextCheckCell textCheckCell2 = (TextCheckCell) view;
                MessagesController.getNotificationsSettings(this.currentAccount).edit().putBoolean("content_preview_" + this.dialogId, !textCheckCell2.isChecked()).apply();
                textCheckCell2.setChecked(textCheckCell2.isChecked() ^ true);
            } else if (i == this.callsVibrateRow) {
                showDialog(AlertsCreator.createVibrationSelectDialog(getParentActivity(), this.dialogId, "calls_vibrate_" + this.dialogId, new ProfileNotificationsActivity$$ExternalSyntheticLambda4(this), this.resourcesProvider));
            } else if (i == this.priorityRow) {
                showDialog(AlertsCreator.createPrioritySelectDialog(getParentActivity(), this.dialogId, -1, new ProfileNotificationsActivity$$ExternalSyntheticLambda3(this), this.resourcesProvider));
            } else {
                int i2 = 2;
                if (i == this.smartRow) {
                    if (getParentActivity() != null) {
                        SharedPreferences notificationsSettings2 = MessagesController.getNotificationsSettings(this.currentAccount);
                        int i3 = notificationsSettings2.getInt("smart_max_count_" + this.dialogId, 2);
                        int i4 = notificationsSettings2.getInt("smart_delay_" + this.dialogId, 180);
                        if (i3 != 0) {
                            i2 = i3;
                        }
                        AlertsCreator.createSoundFrequencyPickerDialog(getParentActivity(), i2, i4, new ProfileNotificationsActivity$$ExternalSyntheticLambda6(this), this.resourcesProvider);
                    }
                } else if (i == this.colorRow) {
                    if (getParentActivity() != null) {
                        showDialog(AlertsCreator.createColorSelectDialog(getParentActivity(), this.dialogId, -1, new ProfileNotificationsActivity$$ExternalSyntheticLambda2(this), this.resourcesProvider));
                    }
                } else if (i == this.popupEnabledRow) {
                    MessagesController.getNotificationsSettings(this.currentAccount).edit().putInt("popup_" + this.dialogId, 1).apply();
                    ((RadioCell) view).setChecked(true, true);
                    View findViewWithTag = this.listView.findViewWithTag(2);
                    if (findViewWithTag != null) {
                        ((RadioCell) findViewWithTag).setChecked(false, true);
                    }
                } else if (i == this.popupDisabledRow) {
                    MessagesController.getNotificationsSettings(this.currentAccount).edit().putInt("popup_" + this.dialogId, 2).apply();
                    ((RadioCell) view).setChecked(true, true);
                    View findViewWithTag2 = this.listView.findViewWithTag(1);
                    if (findViewWithTag2 != null) {
                        ((RadioCell) findViewWithTag2).setChecked(false, true);
                    }
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$0(DialogInterface dialogInterface, int i) {
        this.needReset = true;
        SharedPreferences.Editor edit = MessagesController.getNotificationsSettings(this.currentAccount).edit();
        SharedPreferences.Editor putBoolean = edit.putBoolean("custom_" + this.dialogId, false);
        putBoolean.remove("notify2_" + this.dialogId).apply();
        finishFragment();
        ProfileNotificationsActivityDelegate profileNotificationsActivityDelegate = this.delegate;
        if (profileNotificationsActivityDelegate != null) {
            profileNotificationsActivityDelegate.didRemoveException(this.dialogId);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$1() {
        ListAdapter listAdapter = this.adapter;
        if (listAdapter != null) {
            listAdapter.notifyItemChanged(this.vibrateRow);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$2() {
        ListAdapter listAdapter = this.adapter;
        if (listAdapter != null) {
            listAdapter.notifyItemChanged(this.callsVibrateRow);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$3() {
        ListAdapter listAdapter = this.adapter;
        if (listAdapter != null) {
            listAdapter.notifyItemChanged(this.priorityRow);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$4(int i, int i2) {
        SharedPreferences.Editor edit = MessagesController.getNotificationsSettings(this.currentAccount).edit();
        SharedPreferences.Editor putInt = edit.putInt("smart_max_count_" + this.dialogId, i);
        putInt.putInt("smart_delay_" + this.dialogId, i2).apply();
        ListAdapter listAdapter = this.adapter;
        if (listAdapter != null) {
            listAdapter.notifyItemChanged(this.smartRow);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$5() {
        ListAdapter listAdapter = this.adapter;
        if (listAdapter != null) {
            listAdapter.notifyItemChanged(this.colorRow);
        }
    }

    public void onActivityResultFragment(int i, int i2, Intent intent) {
        Ringtone ringtone;
        if (i2 == -1 && intent != null) {
            Uri uri = (Uri) intent.getParcelableExtra("android.intent.extra.ringtone.PICKED_URI");
            String str = null;
            if (!(uri == null || (ringtone = RingtoneManager.getRingtone(ApplicationLoader.applicationContext, uri)) == null)) {
                if (i == 13) {
                    if (uri.equals(Settings.System.DEFAULT_RINGTONE_URI)) {
                        str = LocaleController.getString("DefaultRingtone", R.string.DefaultRingtone);
                    } else {
                        str = ringtone.getTitle(getParentActivity());
                    }
                } else if (uri.equals(Settings.System.DEFAULT_NOTIFICATION_URI)) {
                    str = LocaleController.getString("SoundDefault", R.string.SoundDefault);
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
            edit.apply();
            ListAdapter listAdapter = this.adapter;
            if (listAdapter != null) {
                listAdapter.notifyItemChanged(i == 13 ? this.ringtoneRow : this.soundRow);
            }
        }
    }

    @SuppressLint({"NotifyDataSetChanged"})
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.notificationsSettingsUpdated) {
            try {
                this.adapter.notifyDataSetChanged();
            } catch (Exception unused) {
            }
        }
    }

    public void setDelegate(ProfileNotificationsActivityDelegate profileNotificationsActivityDelegate) {
        this.delegate = profileNotificationsActivityDelegate;
    }

    private void checkRowsEnabled() {
        int childCount = this.listView.getChildCount();
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < childCount; i++) {
            RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.getChildViewHolder(this.listView.getChildAt(i));
            int itemViewType = holder.getItemViewType();
            int adapterPosition = holder.getAdapterPosition();
            if (!(adapterPosition == this.enableRow || adapterPosition == this.customResetRow)) {
                if (itemViewType == 0) {
                    ((HeaderCell) holder.itemView).setEnabled(this.notificationsEnabled, arrayList);
                } else if (itemViewType == 1) {
                    ((TextSettingsCell) holder.itemView).setEnabled(this.notificationsEnabled, arrayList);
                } else if (itemViewType == 2) {
                    ((TextInfoPrivacyCell) holder.itemView).setEnabled(this.notificationsEnabled, arrayList);
                } else if (itemViewType == 3) {
                    ((TextColorCell) holder.itemView).setEnabled(this.notificationsEnabled, arrayList);
                } else if (itemViewType == 4) {
                    ((RadioCell) holder.itemView).setEnabled(this.notificationsEnabled, arrayList);
                } else if (itemViewType == 7 && adapterPosition == this.previewRow) {
                    ((TextCheckCell) holder.itemView).setEnabled(this.notificationsEnabled, arrayList);
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
            if (viewHolder.getAdapterPosition() == ProfileNotificationsActivity.this.previewRow) {
                return ProfileNotificationsActivity.this.notificationsEnabled;
            }
            if (viewHolder.getAdapterPosition() == ProfileNotificationsActivity.this.customResetRow) {
                return true;
            }
            switch (viewHolder.getItemViewType()) {
                case 0:
                case 2:
                case 5:
                case 6:
                    return false;
                case 1:
                case 3:
                case 4:
                    return ProfileNotificationsActivity.this.notificationsEnabled;
                default:
                    return true;
            }
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view;
            View view2;
            switch (i) {
                case 0:
                    view2 = new HeaderCell(this.context, ProfileNotificationsActivity.this.resourcesProvider);
                    view2.setBackgroundColor(ProfileNotificationsActivity.this.getThemedColor("windowBackgroundWhite"));
                    break;
                case 1:
                    view2 = new TextSettingsCell(this.context, ProfileNotificationsActivity.this.resourcesProvider);
                    view2.setBackgroundColor(ProfileNotificationsActivity.this.getThemedColor("windowBackgroundWhite"));
                    break;
                case 2:
                    view = new TextInfoPrivacyCell(this.context, ProfileNotificationsActivity.this.resourcesProvider);
                    break;
                case 3:
                    view2 = new TextColorCell(this.context, ProfileNotificationsActivity.this.resourcesProvider);
                    view2.setBackgroundColor(ProfileNotificationsActivity.this.getThemedColor("windowBackgroundWhite"));
                    break;
                case 4:
                    view2 = new RadioCell(this.context, ProfileNotificationsActivity.this.resourcesProvider);
                    view2.setBackgroundColor(ProfileNotificationsActivity.this.getThemedColor("windowBackgroundWhite"));
                    break;
                case 5:
                    view2 = new UserCell2(this.context, 4, 0, ProfileNotificationsActivity.this.resourcesProvider);
                    view2.setBackgroundColor(ProfileNotificationsActivity.this.getThemedColor("windowBackgroundWhite"));
                    break;
                case 6:
                    view = new ShadowSectionCell(this.context, ProfileNotificationsActivity.this.resourcesProvider);
                    break;
                default:
                    view2 = new TextCheckCell(this.context, ProfileNotificationsActivity.this.resourcesProvider);
                    view2.setBackgroundColor(ProfileNotificationsActivity.this.getThemedColor("windowBackgroundWhite"));
                    break;
            }
            view = view2;
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            int i2;
            TLObject tLObject;
            RecyclerView.ViewHolder viewHolder2 = viewHolder;
            int i3 = i;
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType != 0) {
                boolean z = false;
                boolean z2 = true;
                if (itemViewType == 1) {
                    TextSettingsCell textSettingsCell = (TextSettingsCell) viewHolder2.itemView;
                    SharedPreferences notificationsSettings = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount);
                    if (i3 == ProfileNotificationsActivity.this.customResetRow) {
                        textSettingsCell.setText(LocaleController.getString(R.string.ResetCustomNotifications), false);
                        textSettingsCell.setTextColor(ProfileNotificationsActivity.this.getThemedColor("dialogTextRed"));
                        return;
                    }
                    textSettingsCell.setTextColor(ProfileNotificationsActivity.this.getThemedColor("windowBackgroundWhiteBlackText"));
                    if (i3 == ProfileNotificationsActivity.this.soundRow) {
                        int i4 = R.string.SoundDefault;
                        String string = notificationsSettings.getString("sound_" + ProfileNotificationsActivity.this.dialogId, LocaleController.getString("SoundDefault", i4));
                        long j = notificationsSettings.getLong("sound_document_id_" + ProfileNotificationsActivity.this.dialogId, 0);
                        if (j != 0) {
                            TLRPC$Document document = ProfileNotificationsActivity.this.getMediaDataController().ringtoneDataStore.getDocument(j);
                            if (document == null) {
                                string = LocaleController.getString("CustomSound", R.string.CustomSound);
                            } else {
                                string = NotificationsSoundActivity.trimTitle(document, document.file_name_fixed);
                            }
                        } else if (string.equals("NoSound")) {
                            string = LocaleController.getString("NoSound", R.string.NoSound);
                        } else if (string.equals("Default")) {
                            string = LocaleController.getString("SoundDefault", i4);
                        }
                        textSettingsCell.setTextAndValue(LocaleController.getString("Sound", R.string.Sound), string, true);
                    } else if (i3 == ProfileNotificationsActivity.this.ringtoneRow) {
                        String string2 = notificationsSettings.getString("ringtone_" + ProfileNotificationsActivity.this.dialogId, LocaleController.getString("DefaultRingtone", R.string.DefaultRingtone));
                        if (string2.equals("NoSound")) {
                            string2 = LocaleController.getString("NoSound", R.string.NoSound);
                        }
                        textSettingsCell.setTextAndValue(LocaleController.getString("VoipSettingsRingtone", R.string.VoipSettingsRingtone), string2, false);
                    } else if (i3 == ProfileNotificationsActivity.this.vibrateRow) {
                        int i5 = notificationsSettings.getInt("vibrate_" + ProfileNotificationsActivity.this.dialogId, 0);
                        if (i5 == 0 || i5 == 4) {
                            String string3 = LocaleController.getString("Vibrate", R.string.Vibrate);
                            String string4 = LocaleController.getString("VibrationDefault", R.string.VibrationDefault);
                            if (!(ProfileNotificationsActivity.this.smartRow == -1 && ProfileNotificationsActivity.this.priorityRow == -1)) {
                                z = true;
                            }
                            textSettingsCell.setTextAndValue(string3, string4, z);
                        } else if (i5 == 1) {
                            String string5 = LocaleController.getString("Vibrate", R.string.Vibrate);
                            String string6 = LocaleController.getString("Short", R.string.Short);
                            if (!(ProfileNotificationsActivity.this.smartRow == -1 && ProfileNotificationsActivity.this.priorityRow == -1)) {
                                z = true;
                            }
                            textSettingsCell.setTextAndValue(string5, string6, z);
                        } else if (i5 == 2) {
                            String string7 = LocaleController.getString("Vibrate", R.string.Vibrate);
                            String string8 = LocaleController.getString("VibrationDisabled", R.string.VibrationDisabled);
                            if (!(ProfileNotificationsActivity.this.smartRow == -1 && ProfileNotificationsActivity.this.priorityRow == -1)) {
                                z = true;
                            }
                            textSettingsCell.setTextAndValue(string7, string8, z);
                        } else if (i5 == 3) {
                            String string9 = LocaleController.getString("Vibrate", R.string.Vibrate);
                            String string10 = LocaleController.getString("Long", R.string.Long);
                            if (!(ProfileNotificationsActivity.this.smartRow == -1 && ProfileNotificationsActivity.this.priorityRow == -1)) {
                                z = true;
                            }
                            textSettingsCell.setTextAndValue(string9, string10, z);
                        }
                    } else if (i3 == ProfileNotificationsActivity.this.priorityRow) {
                        int i6 = notificationsSettings.getInt("priority_" + ProfileNotificationsActivity.this.dialogId, 3);
                        if (i6 == 0) {
                            textSettingsCell.setTextAndValue(LocaleController.getString("NotificationsImportance", R.string.NotificationsImportance), LocaleController.getString("NotificationsPriorityHigh", R.string.NotificationsPriorityHigh), false);
                        } else if (i6 == 1 || i6 == 2) {
                            textSettingsCell.setTextAndValue(LocaleController.getString("NotificationsImportance", R.string.NotificationsImportance), LocaleController.getString("NotificationsPriorityUrgent", R.string.NotificationsPriorityUrgent), false);
                        } else if (i6 == 3) {
                            textSettingsCell.setTextAndValue(LocaleController.getString("NotificationsImportance", R.string.NotificationsImportance), LocaleController.getString("NotificationsPrioritySettings", R.string.NotificationsPrioritySettings), false);
                        } else if (i6 == 4) {
                            textSettingsCell.setTextAndValue(LocaleController.getString("NotificationsImportance", R.string.NotificationsImportance), LocaleController.getString("NotificationsPriorityLow", R.string.NotificationsPriorityLow), false);
                        } else if (i6 == 5) {
                            textSettingsCell.setTextAndValue(LocaleController.getString("NotificationsImportance", R.string.NotificationsImportance), LocaleController.getString("NotificationsPriorityMedium", R.string.NotificationsPriorityMedium), false);
                        }
                    } else if (i3 == ProfileNotificationsActivity.this.smartRow) {
                        int i7 = notificationsSettings.getInt("smart_max_count_" + ProfileNotificationsActivity.this.dialogId, 2);
                        int i8 = notificationsSettings.getInt("smart_delay_" + ProfileNotificationsActivity.this.dialogId, 180);
                        if (i7 == 0) {
                            String string11 = LocaleController.getString("SmartNotifications", R.string.SmartNotifications);
                            String string12 = LocaleController.getString("SmartNotificationsDisabled", R.string.SmartNotificationsDisabled);
                            if (ProfileNotificationsActivity.this.priorityRow != -1) {
                                z = true;
                            }
                            textSettingsCell.setTextAndValue(string11, string12, z);
                            return;
                        }
                        String formatPluralString = LocaleController.formatPluralString("Minutes", i8 / 60, new Object[0]);
                        String string13 = LocaleController.getString("SmartNotifications", R.string.SmartNotifications);
                        String formatString = LocaleController.formatString("SmartNotificationsInfo", R.string.SmartNotificationsInfo, Integer.valueOf(i7), formatPluralString);
                        if (ProfileNotificationsActivity.this.priorityRow != -1) {
                            z = true;
                        }
                        textSettingsCell.setTextAndValue(string13, formatString, z);
                    } else if (i3 == ProfileNotificationsActivity.this.callsVibrateRow) {
                        int i9 = notificationsSettings.getInt("calls_vibrate_" + ProfileNotificationsActivity.this.dialogId, 0);
                        if (i9 == 0 || i9 == 4) {
                            textSettingsCell.setTextAndValue(LocaleController.getString("Vibrate", R.string.Vibrate), LocaleController.getString("VibrationDefault", R.string.VibrationDefault), true);
                        } else if (i9 == 1) {
                            textSettingsCell.setTextAndValue(LocaleController.getString("Vibrate", R.string.Vibrate), LocaleController.getString("Short", R.string.Short), true);
                        } else if (i9 == 2) {
                            textSettingsCell.setTextAndValue(LocaleController.getString("Vibrate", R.string.Vibrate), LocaleController.getString("VibrationDisabled", R.string.VibrationDisabled), true);
                        } else if (i9 == 3) {
                            textSettingsCell.setTextAndValue(LocaleController.getString("Vibrate", R.string.Vibrate), LocaleController.getString("Long", R.string.Long), true);
                        }
                    }
                } else if (itemViewType == 2) {
                    TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) viewHolder2.itemView;
                    if (i3 == ProfileNotificationsActivity.this.popupInfoRow) {
                        textInfoPrivacyCell.setText(LocaleController.getString("ProfilePopupNotificationInfo", R.string.ProfilePopupNotificationInfo));
                        textInfoPrivacyCell.setBackground(Theme.getThemedDrawable(this.context, R.drawable.greydivider, "windowBackgroundGrayShadow"));
                    } else if (i3 == ProfileNotificationsActivity.this.ledInfoRow) {
                        textInfoPrivacyCell.setText(LocaleController.getString("NotificationsLedInfo", R.string.NotificationsLedInfo));
                        textInfoPrivacyCell.setBackground(Theme.getThemedDrawable(this.context, R.drawable.greydivider_bottom, "windowBackgroundGrayShadow"));
                    } else if (i3 == ProfileNotificationsActivity.this.priorityInfoRow) {
                        if (ProfileNotificationsActivity.this.priorityRow == -1) {
                            textInfoPrivacyCell.setText("");
                        } else {
                            textInfoPrivacyCell.setText(LocaleController.getString("PriorityInfo", R.string.PriorityInfo));
                        }
                        textInfoPrivacyCell.setBackground(Theme.getThemedDrawable(this.context, R.drawable.greydivider, "windowBackgroundGrayShadow"));
                    } else if (i3 == ProfileNotificationsActivity.this.ringtoneInfoRow) {
                        textInfoPrivacyCell.setText(LocaleController.getString("VoipRingtoneInfo", R.string.VoipRingtoneInfo));
                        textInfoPrivacyCell.setBackground(Theme.getThemedDrawable(this.context, R.drawable.greydivider, "windowBackgroundGrayShadow"));
                    }
                } else if (itemViewType == 3) {
                    TextColorCell textColorCell = (TextColorCell) viewHolder2.itemView;
                    SharedPreferences notificationsSettings2 = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount);
                    if (notificationsSettings2.contains("color_" + ProfileNotificationsActivity.this.dialogId)) {
                        i2 = notificationsSettings2.getInt("color_" + ProfileNotificationsActivity.this.dialogId, -16776961);
                    } else if (DialogObject.isChatDialog(ProfileNotificationsActivity.this.dialogId)) {
                        i2 = notificationsSettings2.getInt("GroupLed", -16776961);
                    } else {
                        i2 = notificationsSettings2.getInt("MessagesLed", -16776961);
                    }
                    int i10 = 0;
                    while (true) {
                        if (i10 >= 9) {
                            break;
                        } else if (TextColorCell.colorsToSave[i10] == i2) {
                            i2 = TextColorCell.colors[i10];
                            break;
                        } else {
                            i10++;
                        }
                    }
                    textColorCell.setTextAndColor(LocaleController.getString("NotificationsLedColor", R.string.NotificationsLedColor), i2, false);
                } else if (itemViewType == 4) {
                    RadioCell radioCell = (RadioCell) viewHolder2.itemView;
                    SharedPreferences notificationsSettings3 = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount);
                    int i11 = notificationsSettings3.getInt("popup_" + ProfileNotificationsActivity.this.dialogId, 0);
                    if (i11 == 0) {
                        i11 = notificationsSettings3.getInt(DialogObject.isChatDialog(ProfileNotificationsActivity.this.dialogId) ? "popupGroup" : "popupAll", 0) != 0 ? 1 : 2;
                    }
                    if (i3 == ProfileNotificationsActivity.this.popupEnabledRow) {
                        String string14 = LocaleController.getString("PopupEnabled", R.string.PopupEnabled);
                        if (i11 == 1) {
                            z = true;
                        }
                        radioCell.setText(string14, z, true);
                        radioCell.setTag(1);
                    } else if (i3 == ProfileNotificationsActivity.this.popupDisabledRow) {
                        String string15 = LocaleController.getString("PopupDisabled", R.string.PopupDisabled);
                        if (i11 != 2) {
                            z2 = false;
                        }
                        radioCell.setText(string15, z2, false);
                        radioCell.setTag(2);
                    }
                } else if (itemViewType == 5) {
                    UserCell2 userCell2 = (UserCell2) viewHolder2.itemView;
                    if (DialogObject.isUserDialog(ProfileNotificationsActivity.this.dialogId)) {
                        tLObject = MessagesController.getInstance(ProfileNotificationsActivity.this.currentAccount).getUser(Long.valueOf(ProfileNotificationsActivity.this.dialogId));
                    } else {
                        tLObject = MessagesController.getInstance(ProfileNotificationsActivity.this.currentAccount).getChat(Long.valueOf(-ProfileNotificationsActivity.this.dialogId));
                    }
                    userCell2.setData(tLObject, (CharSequence) null, (CharSequence) null, 0);
                } else if (itemViewType == 7) {
                    TextCheckCell textCheckCell = (TextCheckCell) viewHolder2.itemView;
                    SharedPreferences notificationsSettings4 = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount);
                    if (i3 == ProfileNotificationsActivity.this.enableRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("Notifications", R.string.Notifications), ProfileNotificationsActivity.this.notificationsEnabled, true);
                    } else if (i3 == ProfileNotificationsActivity.this.previewRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("MessagePreview", R.string.MessagePreview), notificationsSettings4.getBoolean("content_preview_" + ProfileNotificationsActivity.this.dialogId, true), true);
                    }
                }
            } else {
                HeaderCell headerCell = (HeaderCell) viewHolder2.itemView;
                if (i3 == ProfileNotificationsActivity.this.generalRow) {
                    headerCell.setText(LocaleController.getString("General", R.string.General));
                } else if (i3 == ProfileNotificationsActivity.this.popupRow) {
                    headerCell.setText(LocaleController.getString("ProfilePopupNotification", R.string.ProfilePopupNotification));
                } else if (i3 == ProfileNotificationsActivity.this.ledRow) {
                    headerCell.setText(LocaleController.getString("NotificationsLed", R.string.NotificationsLed));
                } else if (i3 == ProfileNotificationsActivity.this.callsRow) {
                    headerCell.setText(LocaleController.getString("VoipNotificationSettings", R.string.VoipNotificationSettings));
                }
            }
        }

        public void onViewAttachedToWindow(RecyclerView.ViewHolder viewHolder) {
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType == 0) {
                ((HeaderCell) viewHolder.itemView).setEnabled(ProfileNotificationsActivity.this.notificationsEnabled, (ArrayList<Animator>) null);
            } else if (itemViewType == 1) {
                TextSettingsCell textSettingsCell = (TextSettingsCell) viewHolder.itemView;
                if (viewHolder.getAdapterPosition() == ProfileNotificationsActivity.this.customResetRow) {
                    textSettingsCell.setEnabled(true, (ArrayList<Animator>) null);
                } else {
                    textSettingsCell.setEnabled(ProfileNotificationsActivity.this.notificationsEnabled, (ArrayList<Animator>) null);
                }
            } else if (itemViewType == 2) {
                ((TextInfoPrivacyCell) viewHolder.itemView).setEnabled(ProfileNotificationsActivity.this.notificationsEnabled, (ArrayList<Animator>) null);
            } else if (itemViewType == 3) {
                ((TextColorCell) viewHolder.itemView).setEnabled(ProfileNotificationsActivity.this.notificationsEnabled, (ArrayList<Animator>) null);
            } else if (itemViewType == 4) {
                ((RadioCell) viewHolder.itemView).setEnabled(ProfileNotificationsActivity.this.notificationsEnabled, (ArrayList<Animator>) null);
            } else if (itemViewType == 7) {
                TextCheckCell textCheckCell = (TextCheckCell) viewHolder.itemView;
                if (viewHolder.getAdapterPosition() == ProfileNotificationsActivity.this.previewRow) {
                    textCheckCell.setEnabled(ProfileNotificationsActivity.this.notificationsEnabled, (ArrayList<Animator>) null);
                } else {
                    textCheckCell.setEnabled(true, (ArrayList<Animator>) null);
                }
            }
        }

        public int getItemViewType(int i) {
            if (i == ProfileNotificationsActivity.this.generalRow || i == ProfileNotificationsActivity.this.popupRow || i == ProfileNotificationsActivity.this.ledRow || i == ProfileNotificationsActivity.this.callsRow) {
                return 0;
            }
            if (i == ProfileNotificationsActivity.this.soundRow || i == ProfileNotificationsActivity.this.vibrateRow || i == ProfileNotificationsActivity.this.priorityRow || i == ProfileNotificationsActivity.this.smartRow || i == ProfileNotificationsActivity.this.ringtoneRow || i == ProfileNotificationsActivity.this.callsVibrateRow || i == ProfileNotificationsActivity.this.customResetRow) {
                return 1;
            }
            if (i == ProfileNotificationsActivity.this.popupInfoRow || i == ProfileNotificationsActivity.this.ledInfoRow || i == ProfileNotificationsActivity.this.priorityInfoRow || i == ProfileNotificationsActivity.this.ringtoneInfoRow) {
                return 2;
            }
            if (i == ProfileNotificationsActivity.this.colorRow) {
                return 3;
            }
            if (i == ProfileNotificationsActivity.this.popupEnabledRow || i == ProfileNotificationsActivity.this.popupDisabledRow) {
                return 4;
            }
            if (i == ProfileNotificationsActivity.this.avatarRow) {
                return 5;
            }
            if (i == ProfileNotificationsActivity.this.avatarSectionRow || i == ProfileNotificationsActivity.this.customResetShadowRow) {
                return 6;
            }
            if (i == ProfileNotificationsActivity.this.enableRow || i == ProfileNotificationsActivity.this.previewRow) {
                return 7;
            }
            return 0;
        }
    }

    public int getNavigationBarColor() {
        return getThemedColor("windowBackgroundGray");
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        ProfileNotificationsActivity$$ExternalSyntheticLambda5 profileNotificationsActivity$$ExternalSyntheticLambda5 = new ProfileNotificationsActivity$$ExternalSyntheticLambda5(this);
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
        ProfileNotificationsActivity$$ExternalSyntheticLambda5 profileNotificationsActivity$$ExternalSyntheticLambda52 = profileNotificationsActivity$$ExternalSyntheticLambda5;
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{UserCell2.class}, new String[]{"statusColor"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) profileNotificationsActivity$$ExternalSyntheticLambda52, "windowBackgroundWhiteGrayText"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{UserCell2.class}, new String[]{"statusOnlineColor"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) profileNotificationsActivity$$ExternalSyntheticLambda52, "windowBackgroundWhiteBlueText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{UserCell2.class}, (Paint) null, Theme.avatarDrawables, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_text"));
        ProfileNotificationsActivity$$ExternalSyntheticLambda5 profileNotificationsActivity$$ExternalSyntheticLambda53 = profileNotificationsActivity$$ExternalSyntheticLambda5;
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, profileNotificationsActivity$$ExternalSyntheticLambda53, "avatar_backgroundRed"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, profileNotificationsActivity$$ExternalSyntheticLambda53, "avatar_backgroundOrange"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, profileNotificationsActivity$$ExternalSyntheticLambda53, "avatar_backgroundViolet"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, profileNotificationsActivity$$ExternalSyntheticLambda53, "avatar_backgroundGreen"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, profileNotificationsActivity$$ExternalSyntheticLambda53, "avatar_backgroundCyan"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, profileNotificationsActivity$$ExternalSyntheticLambda53, "avatar_backgroundBlue"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, profileNotificationsActivity$$ExternalSyntheticLambda53, "avatar_backgroundPink"));
        return arrayList;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$getThemeDescriptions$7() {
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
