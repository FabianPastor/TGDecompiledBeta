package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.app.Activity;
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
import android.text.TextUtils;
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
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationsController;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Dialog;
import org.telegram.tgnet.TLRPC$TL_peerNotifySettings;
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
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.NotificationsSettingsActivity;
import org.telegram.ui.ProfileNotificationsActivity;

public class ProfileNotificationsActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    /* access modifiers changed from: private */
    public ListAdapter adapter;
    /* access modifiers changed from: private */
    public boolean addingException;
    /* access modifiers changed from: private */
    public AnimatorSet animatorSet;
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
    public long dialog_id;
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
        this.dialog_id = bundle.getLong("dialog_id");
        this.addingException = bundle.getBoolean("exception", false);
    }

    /* JADX WARNING: Removed duplicated region for block: B:33:0x00ce  */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x00e9  */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x0129  */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x012b  */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x015c  */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x0170  */
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
            r1 = 0
            int r1 = r1 + r3
            r8.rowCount = r1
            r8.avatarRow = r0
            int r4 = r1 + 1
            r8.rowCount = r4
            r8.avatarSectionRow = r1
            r8.customRow = r2
            r8.customInfoRow = r2
            goto L_0x002a
        L_0x001a:
            r8.avatarRow = r2
            r8.avatarSectionRow = r2
            r1 = 0
            int r1 = r1 + r3
            r8.rowCount = r1
            r8.customRow = r0
            int r4 = r1 + 1
            r8.rowCount = r4
            r8.customInfoRow = r1
        L_0x002a:
            int r1 = r8.rowCount
            int r4 = r1 + 1
            r8.rowCount = r4
            r8.generalRow = r1
            boolean r1 = r8.addingException
            if (r1 == 0) goto L_0x003d
            int r1 = r4 + 1
            r8.rowCount = r1
            r8.enableRow = r4
            goto L_0x003f
        L_0x003d:
            r8.enableRow = r2
        L_0x003f:
            long r4 = r8.dialog_id
            int r1 = (int) r4
            if (r1 == 0) goto L_0x004d
            int r1 = r8.rowCount
            int r4 = r1 + 1
            r8.rowCount = r4
            r8.previewRow = r1
            goto L_0x004f
        L_0x004d:
            r8.previewRow = r2
        L_0x004f:
            int r1 = r8.rowCount
            int r4 = r1 + 1
            r8.rowCount = r4
            r8.soundRow = r1
            int r1 = r4 + 1
            r8.rowCount = r1
            r8.vibrateRow = r4
            long r4 = r8.dialog_id
            int r5 = (int) r4
            if (r5 >= 0) goto L_0x0069
            int r4 = r1 + 1
            r8.rowCount = r4
            r8.smartRow = r1
            goto L_0x006b
        L_0x0069:
            r8.smartRow = r2
        L_0x006b:
            int r1 = android.os.Build.VERSION.SDK_INT
            r4 = 21
            if (r1 < r4) goto L_0x007a
            int r1 = r8.rowCount
            int r4 = r1 + 1
            r8.rowCount = r4
            r8.priorityRow = r1
            goto L_0x007c
        L_0x007a:
            r8.priorityRow = r2
        L_0x007c:
            int r1 = r8.rowCount
            int r4 = r1 + 1
            r8.rowCount = r4
            r8.priorityInfoRow = r1
            long r4 = r8.dialog_id
            int r1 = (int) r4
            if (r1 >= 0) goto L_0x00a4
            int r4 = r8.currentAccount
            org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r4)
            int r5 = -r1
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            org.telegram.tgnet.TLRPC$Chat r4 = r4.getChat(r5)
            boolean r5 = org.telegram.messenger.ChatObject.isChannel(r4)
            if (r5 == 0) goto L_0x00a4
            boolean r4 = r4.megagroup
            if (r4 != 0) goto L_0x00a4
            r4 = 1
            goto L_0x00a5
        L_0x00a4:
            r4 = 0
        L_0x00a5:
            if (r1 == 0) goto L_0x00c4
            if (r4 != 0) goto L_0x00c4
            int r4 = r8.rowCount
            int r5 = r4 + 1
            r8.rowCount = r5
            r8.popupRow = r4
            int r4 = r5 + 1
            r8.rowCount = r4
            r8.popupEnabledRow = r5
            int r5 = r4 + 1
            r8.rowCount = r5
            r8.popupDisabledRow = r4
            int r4 = r5 + 1
            r8.rowCount = r4
            r8.popupInfoRow = r5
            goto L_0x00cc
        L_0x00c4:
            r8.popupRow = r2
            r8.popupEnabledRow = r2
            r8.popupDisabledRow = r2
            r8.popupInfoRow = r2
        L_0x00cc:
            if (r1 <= 0) goto L_0x00e9
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
            goto L_0x00f1
        L_0x00e9:
            r8.callsRow = r2
            r8.callsVibrateRow = r2
            r8.ringtoneRow = r2
            r8.ringtoneInfoRow = r2
        L_0x00f1:
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
            long r4 = r8.dialog_id
            r2.append(r4)
            java.lang.String r2 = r2.toString()
            boolean r2 = r1.getBoolean(r2, r0)
            if (r2 != 0) goto L_0x012b
            boolean r2 = r8.addingException
            if (r2 == 0) goto L_0x0129
            goto L_0x012b
        L_0x0129:
            r2 = 0
            goto L_0x012c
        L_0x012b:
            r2 = 1
        L_0x012c:
            r8.customEnabled = r2
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r4 = "notify2_"
            r2.append(r4)
            long r5 = r8.dialog_id
            r2.append(r5)
            java.lang.String r2 = r2.toString()
            boolean r2 = r1.contains(r2)
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            r5.append(r4)
            long r6 = r8.dialog_id
            r5.append(r6)
            java.lang.String r4 = r5.toString()
            int r1 = r1.getInt(r4, r0)
            if (r1 != 0) goto L_0x0170
            if (r2 == 0) goto L_0x0161
            r8.notificationsEnabled = r3
            goto L_0x017d
        L_0x0161:
            int r0 = r8.currentAccount
            org.telegram.messenger.NotificationsController r0 = org.telegram.messenger.NotificationsController.getInstance(r0)
            long r1 = r8.dialog_id
            boolean r0 = r0.isGlobalNotificationsEnabled((long) r1)
            r8.notificationsEnabled = r0
            goto L_0x017d
        L_0x0170:
            if (r1 != r3) goto L_0x0175
            r8.notificationsEnabled = r3
            goto L_0x017d
        L_0x0175:
            r2 = 2
            if (r1 != r2) goto L_0x017b
            r8.notificationsEnabled = r0
            goto L_0x017d
        L_0x017b:
            r8.notificationsEnabled = r0
        L_0x017d:
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

    public View createView(final Context context) {
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    if (!ProfileNotificationsActivity.this.addingException && ProfileNotificationsActivity.this.notificationsEnabled && ProfileNotificationsActivity.this.customEnabled) {
                        SharedPreferences.Editor edit = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount).edit();
                        edit.putInt("notify2_" + ProfileNotificationsActivity.this.dialog_id, 0).commit();
                    }
                } else if (i == 1) {
                    SharedPreferences notificationsSettings = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount);
                    SharedPreferences.Editor edit2 = notificationsSettings.edit();
                    edit2.putBoolean("custom_" + ProfileNotificationsActivity.this.dialog_id, true);
                    TLRPC$Dialog tLRPC$Dialog = MessagesController.getInstance(ProfileNotificationsActivity.this.currentAccount).dialogs_dict.get(ProfileNotificationsActivity.this.dialog_id);
                    if (ProfileNotificationsActivity.this.notificationsEnabled) {
                        edit2.putInt("notify2_" + ProfileNotificationsActivity.this.dialog_id, 0);
                        MessagesStorage.getInstance(ProfileNotificationsActivity.this.currentAccount).setDialogFlags(ProfileNotificationsActivity.this.dialog_id, 0);
                        if (tLRPC$Dialog != null) {
                            tLRPC$Dialog.notify_settings = new TLRPC$TL_peerNotifySettings();
                        }
                    } else {
                        edit2.putInt("notify2_" + ProfileNotificationsActivity.this.dialog_id, 2);
                        NotificationsController.getInstance(ProfileNotificationsActivity.this.currentAccount).removeNotificationsForDialog(ProfileNotificationsActivity.this.dialog_id);
                        MessagesStorage.getInstance(ProfileNotificationsActivity.this.currentAccount).setDialogFlags(ProfileNotificationsActivity.this.dialog_id, 1);
                        if (tLRPC$Dialog != null) {
                            TLRPC$TL_peerNotifySettings tLRPC$TL_peerNotifySettings = new TLRPC$TL_peerNotifySettings();
                            tLRPC$Dialog.notify_settings = tLRPC$TL_peerNotifySettings;
                            tLRPC$TL_peerNotifySettings.mute_until = Integer.MAX_VALUE;
                        }
                    }
                    edit2.commit();
                    NotificationsController.getInstance(ProfileNotificationsActivity.this.currentAccount).updateServerNotificationsSettings(ProfileNotificationsActivity.this.dialog_id);
                    if (ProfileNotificationsActivity.this.delegate != null) {
                        NotificationsSettingsActivity.NotificationException notificationException = new NotificationsSettingsActivity.NotificationException();
                        notificationException.did = ProfileNotificationsActivity.this.dialog_id;
                        notificationException.hasCustom = true;
                        int i2 = notificationsSettings.getInt("notify2_" + ProfileNotificationsActivity.this.dialog_id, 0);
                        notificationException.notify = i2;
                        if (i2 != 0) {
                            notificationException.muteUntil = notificationsSettings.getInt("notifyuntil_" + ProfileNotificationsActivity.this.dialog_id, 0);
                        }
                        ProfileNotificationsActivity.this.delegate.didCreateNewException(notificationException);
                    }
                }
                ProfileNotificationsActivity.this.finishFragment();
            }
        });
        if (this.addingException) {
            this.actionBar.setTitle(LocaleController.getString("NotificationsNewException", NUM));
            this.actionBar.createMenu().addItem(1, (CharSequence) LocaleController.getString("Done", NUM).toUpperCase());
        } else {
            this.actionBar.setTitle(LocaleController.getString("CustomNotifications", NUM));
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
                if (i == ProfileNotificationsActivity.this.customRow && (view instanceof TextCheckBoxCell)) {
                    SharedPreferences notificationsSettings = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount);
                    ProfileNotificationsActivity profileNotificationsActivity = ProfileNotificationsActivity.this;
                    boolean unused = profileNotificationsActivity.customEnabled = true ^ profileNotificationsActivity.customEnabled;
                    ProfileNotificationsActivity profileNotificationsActivity2 = ProfileNotificationsActivity.this;
                    boolean unused2 = profileNotificationsActivity2.notificationsEnabled = profileNotificationsActivity2.customEnabled;
                    SharedPreferences.Editor edit = notificationsSettings.edit();
                    edit.putBoolean("custom_" + ProfileNotificationsActivity.this.dialog_id, ProfileNotificationsActivity.this.customEnabled).commit();
                    ((TextCheckBoxCell) view).setChecked(ProfileNotificationsActivity.this.customEnabled);
                    ProfileNotificationsActivity.this.checkRowsEnabled();
                } else if (ProfileNotificationsActivity.this.customEnabled && view.isEnabled()) {
                    Uri uri = null;
                    int i2 = 2;
                    if (i == ProfileNotificationsActivity.this.soundRow) {
                        try {
                            Intent intent = new Intent("android.intent.action.RINGTONE_PICKER");
                            intent.putExtra("android.intent.extra.ringtone.TYPE", 2);
                            intent.putExtra("android.intent.extra.ringtone.SHOW_DEFAULT", true);
                            intent.putExtra("android.intent.extra.ringtone.SHOW_SILENT", true);
                            intent.putExtra("android.intent.extra.ringtone.DEFAULT_URI", RingtoneManager.getDefaultUri(2));
                            SharedPreferences notificationsSettings2 = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount);
                            Uri uri2 = Settings.System.DEFAULT_NOTIFICATION_URI;
                            String path = uri2 != null ? uri2.getPath() : null;
                            String string = notificationsSettings2.getString("sound_path_" + ProfileNotificationsActivity.this.dialog_id, path);
                            if (string != null && !string.equals("NoSound")) {
                                uri = string.equals(path) ? uri2 : Uri.parse(string);
                            }
                            intent.putExtra("android.intent.extra.ringtone.EXISTING_URI", uri);
                            ProfileNotificationsActivity.this.startActivityForResult(intent, 12);
                        } catch (Exception e) {
                            FileLog.e((Throwable) e);
                        }
                    } else if (i == ProfileNotificationsActivity.this.ringtoneRow) {
                        try {
                            Intent intent2 = new Intent("android.intent.action.RINGTONE_PICKER");
                            intent2.putExtra("android.intent.extra.ringtone.TYPE", 1);
                            intent2.putExtra("android.intent.extra.ringtone.SHOW_DEFAULT", true);
                            intent2.putExtra("android.intent.extra.ringtone.SHOW_SILENT", true);
                            intent2.putExtra("android.intent.extra.ringtone.DEFAULT_URI", RingtoneManager.getDefaultUri(1));
                            SharedPreferences notificationsSettings3 = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount);
                            Uri uri3 = Settings.System.DEFAULT_NOTIFICATION_URI;
                            String path2 = uri3 != null ? uri3.getPath() : null;
                            String string2 = notificationsSettings3.getString("ringtone_path_" + ProfileNotificationsActivity.this.dialog_id, path2);
                            if (string2 != null && !string2.equals("NoSound")) {
                                uri = string2.equals(path2) ? uri3 : Uri.parse(string2);
                            }
                            intent2.putExtra("android.intent.extra.ringtone.EXISTING_URI", uri);
                            ProfileNotificationsActivity.this.startActivityForResult(intent2, 13);
                        } catch (Exception e2) {
                            FileLog.e((Throwable) e2);
                        }
                    } else if (i == ProfileNotificationsActivity.this.vibrateRow) {
                        ProfileNotificationsActivity profileNotificationsActivity3 = ProfileNotificationsActivity.this;
                        profileNotificationsActivity3.showDialog(AlertsCreator.createVibrationSelectDialog(profileNotificationsActivity3.getParentActivity(), ProfileNotificationsActivity.this.dialog_id, false, false, new Runnable() {
                            public final void run() {
                                ProfileNotificationsActivity.AnonymousClass3.this.lambda$onItemClick$0$ProfileNotificationsActivity$3();
                            }
                        }));
                    } else if (i == ProfileNotificationsActivity.this.enableRow) {
                        TextCheckCell textCheckCell = (TextCheckCell) view;
                        boolean unused3 = ProfileNotificationsActivity.this.notificationsEnabled = !textCheckCell.isChecked();
                        textCheckCell.setChecked(ProfileNotificationsActivity.this.notificationsEnabled);
                        ProfileNotificationsActivity.this.checkRowsEnabled();
                    } else if (i == ProfileNotificationsActivity.this.previewRow) {
                        TextCheckCell textCheckCell2 = (TextCheckCell) view;
                        SharedPreferences.Editor edit2 = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount).edit();
                        edit2.putBoolean("content_preview_" + ProfileNotificationsActivity.this.dialog_id, !textCheckCell2.isChecked()).commit();
                        textCheckCell2.setChecked(textCheckCell2.isChecked() ^ true);
                    } else if (i == ProfileNotificationsActivity.this.callsVibrateRow) {
                        ProfileNotificationsActivity profileNotificationsActivity4 = ProfileNotificationsActivity.this;
                        profileNotificationsActivity4.showDialog(AlertsCreator.createVibrationSelectDialog(profileNotificationsActivity4.getParentActivity(), ProfileNotificationsActivity.this.dialog_id, "calls_vibrate_", new Runnable() {
                            public final void run() {
                                ProfileNotificationsActivity.AnonymousClass3.this.lambda$onItemClick$1$ProfileNotificationsActivity$3();
                            }
                        }));
                    } else if (i == ProfileNotificationsActivity.this.priorityRow) {
                        ProfileNotificationsActivity profileNotificationsActivity5 = ProfileNotificationsActivity.this;
                        profileNotificationsActivity5.showDialog(AlertsCreator.createPrioritySelectDialog(profileNotificationsActivity5.getParentActivity(), ProfileNotificationsActivity.this.dialog_id, -1, new Runnable() {
                            public final void run() {
                                ProfileNotificationsActivity.AnonymousClass3.this.lambda$onItemClick$2$ProfileNotificationsActivity$3();
                            }
                        }));
                    } else if (i == ProfileNotificationsActivity.this.smartRow) {
                        if (ProfileNotificationsActivity.this.getParentActivity() != null) {
                            final Activity parentActivity = ProfileNotificationsActivity.this.getParentActivity();
                            SharedPreferences notificationsSettings4 = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount);
                            int i3 = notificationsSettings4.getInt("smart_max_count_" + ProfileNotificationsActivity.this.dialog_id, 2);
                            int i4 = notificationsSettings4.getInt("smart_delay_" + ProfileNotificationsActivity.this.dialog_id, 180);
                            if (i3 != 0) {
                                i2 = i3;
                            }
                            final int i5 = ((((i4 / 60) - 1) * 10) + i2) - 1;
                            RecyclerListView recyclerListView = new RecyclerListView(ProfileNotificationsActivity.this.getParentActivity());
                            recyclerListView.setLayoutManager(new LinearLayoutManager(context, 1, false));
                            recyclerListView.setClipToPadding(true);
                            recyclerListView.setAdapter(new RecyclerListView.SelectionAdapter(this) {
                                public int getItemCount() {
                                    return 100;
                                }

                                public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
                                    return true;
                                }

                                public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                                    AnonymousClass1 r3 = new TextView(this, parentActivity) {
                                        /* access modifiers changed from: protected */
                                        public void onMeasure(int i, int i2) {
                                            super.onMeasure(View.MeasureSpec.makeMeasureSpec(i, NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), NUM));
                                        }
                                    };
                                    r3.setGravity(17);
                                    r3.setTextSize(1, 18.0f);
                                    r3.setSingleLine(true);
                                    r3.setEllipsize(TextUtils.TruncateAt.END);
                                    r3.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                                    return new RecyclerListView.Holder(r3);
                                }

                                public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
                                    TextView textView = (TextView) viewHolder.itemView;
                                    textView.setTextColor(Theme.getColor(i == i5 ? "dialogTextGray" : "dialogTextBlack"));
                                    textView.setText(LocaleController.formatString("SmartNotificationsDetail", NUM, LocaleController.formatPluralString("Times", (i % 10) + 1), LocaleController.formatPluralString("Minutes", (i / 10) + 1)));
                                }
                            });
                            recyclerListView.setPadding(0, AndroidUtilities.dp(12.0f), 0, AndroidUtilities.dp(8.0f));
                            recyclerListView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
                                public final void onItemClick(View view, int i) {
                                    ProfileNotificationsActivity.AnonymousClass3.this.lambda$onItemClick$3$ProfileNotificationsActivity$3(view, i);
                                }
                            });
                            AlertDialog.Builder builder = new AlertDialog.Builder(ProfileNotificationsActivity.this.getParentActivity());
                            builder.setTitle(LocaleController.getString("SmartNotificationsAlert", NUM));
                            builder.setView(recyclerListView);
                            builder.setPositiveButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                            builder.setNegativeButton(LocaleController.getString("SmartNotificationsDisabled", NUM), new DialogInterface.OnClickListener() {
                                public final void onClick(DialogInterface dialogInterface, int i) {
                                    ProfileNotificationsActivity.AnonymousClass3.this.lambda$onItemClick$4$ProfileNotificationsActivity$3(dialogInterface, i);
                                }
                            });
                            ProfileNotificationsActivity.this.showDialog(builder.create());
                        }
                    } else if (i == ProfileNotificationsActivity.this.colorRow) {
                        if (ProfileNotificationsActivity.this.getParentActivity() != null) {
                            ProfileNotificationsActivity profileNotificationsActivity6 = ProfileNotificationsActivity.this;
                            profileNotificationsActivity6.showDialog(AlertsCreator.createColorSelectDialog(profileNotificationsActivity6.getParentActivity(), ProfileNotificationsActivity.this.dialog_id, -1, new Runnable() {
                                public final void run() {
                                    ProfileNotificationsActivity.AnonymousClass3.this.lambda$onItemClick$5$ProfileNotificationsActivity$3();
                                }
                            }));
                        }
                    } else if (i == ProfileNotificationsActivity.this.popupEnabledRow) {
                        SharedPreferences.Editor edit3 = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount).edit();
                        edit3.putInt("popup_" + ProfileNotificationsActivity.this.dialog_id, 1).commit();
                        ((RadioCell) view).setChecked(true, true);
                        View findViewWithTag = ProfileNotificationsActivity.this.listView.findViewWithTag(2);
                        if (findViewWithTag != null) {
                            ((RadioCell) findViewWithTag).setChecked(false, true);
                        }
                    } else if (i == ProfileNotificationsActivity.this.popupDisabledRow) {
                        SharedPreferences.Editor edit4 = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount).edit();
                        edit4.putInt("popup_" + ProfileNotificationsActivity.this.dialog_id, 2).commit();
                        ((RadioCell) view).setChecked(true, true);
                        View findViewWithTag2 = ProfileNotificationsActivity.this.listView.findViewWithTag(1);
                        if (findViewWithTag2 != null) {
                            ((RadioCell) findViewWithTag2).setChecked(false, true);
                        }
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
                    SharedPreferences notificationsSettings = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount);
                    SharedPreferences.Editor edit = notificationsSettings.edit();
                    edit.putInt("smart_max_count_" + ProfileNotificationsActivity.this.dialog_id, (i % 10) + 1).commit();
                    SharedPreferences.Editor edit2 = notificationsSettings.edit();
                    edit2.putInt("smart_delay_" + ProfileNotificationsActivity.this.dialog_id, ((i / 10) + 1) * 60).commit();
                    if (ProfileNotificationsActivity.this.adapter != null) {
                        ProfileNotificationsActivity.this.adapter.notifyItemChanged(ProfileNotificationsActivity.this.smartRow);
                    }
                    ProfileNotificationsActivity.this.dismissCurrentDialog();
                }
            }

            public /* synthetic */ void lambda$onItemClick$4$ProfileNotificationsActivity$3(DialogInterface dialogInterface, int i) {
                SharedPreferences.Editor edit = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount).edit();
                edit.putInt("smart_max_count_" + ProfileNotificationsActivity.this.dialog_id, 0).commit();
                if (ProfileNotificationsActivity.this.adapter != null) {
                    ProfileNotificationsActivity.this.adapter.notifyItemChanged(ProfileNotificationsActivity.this.smartRow);
                }
                ProfileNotificationsActivity.this.dismissCurrentDialog();
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
                    edit.putString("sound_" + this.dialog_id, str);
                    edit.putString("sound_path_" + this.dialog_id, uri.toString());
                } else {
                    edit.putString("sound_" + this.dialog_id, "NoSound");
                    edit.putString("sound_path_" + this.dialog_id, "NoSound");
                }
            } else if (i == 13) {
                if (str != null) {
                    edit.putString("ringtone_" + this.dialog_id, str);
                    edit.putString("ringtone_path_" + this.dialog_id, uri.toString());
                } else {
                    edit.putString("ringtone_" + this.dialog_id, "NoSound");
                    edit.putString("ringtone_path_" + this.dialog_id, "NoSound");
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
            View view;
            TextCheckCell textCheckCell;
            switch (i) {
                case 0:
                    HeaderCell headerCell = new HeaderCell(this.context);
                    headerCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    textCheckCell = headerCell;
                    break;
                case 1:
                    TextSettingsCell textSettingsCell = new TextSettingsCell(this.context);
                    textSettingsCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    textCheckCell = textSettingsCell;
                    break;
                case 2:
                    view = new TextInfoPrivacyCell(this.context);
                    break;
                case 3:
                    TextColorCell textColorCell = new TextColorCell(this.context);
                    textColorCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    textCheckCell = textColorCell;
                    break;
                case 4:
                    RadioCell radioCell = new RadioCell(this.context);
                    radioCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    textCheckCell = radioCell;
                    break;
                case 5:
                    TextCheckBoxCell textCheckBoxCell = new TextCheckBoxCell(this.context);
                    textCheckBoxCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    textCheckCell = textCheckBoxCell;
                    break;
                case 6:
                    UserCell2 userCell2 = new UserCell2(this.context, 4, 0);
                    userCell2.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    textCheckCell = userCell2;
                    break;
                case 7:
                    view = new ShadowSectionCell(this.context);
                    break;
                default:
                    TextCheckCell textCheckCell2 = new TextCheckCell(this.context);
                    textCheckCell2.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    textCheckCell = textCheckCell2;
                    break;
            }
            view = textCheckCell;
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view);
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
                        String string = notificationsSettings.getString("sound_" + ProfileNotificationsActivity.this.dialog_id, LocaleController.getString("SoundDefault", NUM));
                        if (string.equals("NoSound")) {
                            string = LocaleController.getString("NoSound", NUM);
                        }
                        textSettingsCell.setTextAndValue(LocaleController.getString("Sound", NUM), string, true);
                        return;
                    } else if (i3 == ProfileNotificationsActivity.this.ringtoneRow) {
                        String string2 = notificationsSettings.getString("ringtone_" + ProfileNotificationsActivity.this.dialog_id, LocaleController.getString("DefaultRingtone", NUM));
                        if (string2.equals("NoSound")) {
                            string2 = LocaleController.getString("NoSound", NUM);
                        }
                        textSettingsCell.setTextAndValue(LocaleController.getString("VoipSettingsRingtone", NUM), string2, false);
                        return;
                    } else if (i3 == ProfileNotificationsActivity.this.vibrateRow) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("vibrate_");
                        String str = "VibrationDefault";
                        sb.append(ProfileNotificationsActivity.this.dialog_id);
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
                            int i5 = notificationsSettings.getInt("priority_" + ProfileNotificationsActivity.this.dialog_id, 3);
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
                            int i6 = notificationsSettings.getInt("smart_max_count_" + ProfileNotificationsActivity.this.dialog_id, 2);
                            int i7 = notificationsSettings.getInt("smart_delay_" + ProfileNotificationsActivity.this.dialog_id, 180);
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
                            int i8 = notificationsSettings.getInt("calls_vibrate_" + ProfileNotificationsActivity.this.dialog_id, 0);
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
                    if (notificationsSettings2.contains("color_" + ProfileNotificationsActivity.this.dialog_id)) {
                        i2 = notificationsSettings2.getInt("color_" + ProfileNotificationsActivity.this.dialog_id, -16776961);
                    } else if (((int) ProfileNotificationsActivity.this.dialog_id) < 0) {
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
                    int i10 = notificationsSettings3.getInt("popup_" + ProfileNotificationsActivity.this.dialog_id, 0);
                    if (i10 == 0) {
                        i10 = notificationsSettings3.getInt(((int) ProfileNotificationsActivity.this.dialog_id) < 0 ? "popupGroup" : "popupAll", 0) != 0 ? 1 : 2;
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
                    TextCheckBoxCell textCheckBoxCell = (TextCheckBoxCell) viewHolder2.itemView;
                    MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount);
                    String string16 = LocaleController.getString("NotificationsEnableCustom", NUM);
                    if (!ProfileNotificationsActivity.this.customEnabled || !ProfileNotificationsActivity.this.notificationsEnabled) {
                        z2 = false;
                    }
                    textCheckBoxCell.setTextAndCheck(string16, z2, false);
                    return;
                case 6:
                    UserCell2 userCell2 = (UserCell2) viewHolder2.itemView;
                    int access$400 = (int) ProfileNotificationsActivity.this.dialog_id;
                    if (access$400 > 0) {
                        tLObject = MessagesController.getInstance(ProfileNotificationsActivity.this.currentAccount).getUser(Integer.valueOf(access$400));
                    } else {
                        tLObject = MessagesController.getInstance(ProfileNotificationsActivity.this.currentAccount).getChat(Integer.valueOf(-access$400));
                    }
                    userCell2.setData(tLObject, (CharSequence) null, (CharSequence) null, 0);
                    return;
                case 8:
                    TextCheckCell textCheckCell = (TextCheckCell) viewHolder2.itemView;
                    SharedPreferences notificationsSettings4 = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount);
                    if (i3 == ProfileNotificationsActivity.this.enableRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("Notifications", NUM), ProfileNotificationsActivity.this.notificationsEnabled, true);
                        return;
                    } else if (i3 == ProfileNotificationsActivity.this.previewRow) {
                        String string17 = LocaleController.getString("MessagePreview", NUM);
                        textCheckCell.setTextAndCheck(string17, notificationsSettings4.getBoolean("content_preview_" + ProfileNotificationsActivity.this.dialog_id, true), true);
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
        $$Lambda$ProfileNotificationsActivity$7A70wCksskw_wSSS5mOEgsRl6M r11 = new ThemeDescription.ThemeDescriptionDelegate() {
            public final void didSetColor() {
                ProfileNotificationsActivity.this.lambda$getThemeDescriptions$0$ProfileNotificationsActivity();
            }
        };
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
        $$Lambda$ProfileNotificationsActivity$7A70wCksskw_wSSS5mOEgsRl6M r9 = r11;
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{UserCell2.class}, new String[]{"statusColor"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) r9, "windowBackgroundWhiteGrayText"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{UserCell2.class}, new String[]{"statusOnlineColor"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) r9, "windowBackgroundWhiteBlueText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{UserCell2.class}, (Paint) null, Theme.avatarDrawables, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_text"));
        $$Lambda$ProfileNotificationsActivity$7A70wCksskw_wSSS5mOEgsRl6M r8 = r11;
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundRed"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundOrange"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundViolet"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundGreen"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundCyan"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundBlue"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundPink"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckBoxCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextCheckBoxCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "checkboxSquareUnchecked"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextCheckBoxCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "checkboxSquareDisabled"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextCheckBoxCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "checkboxSquareBackground"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextCheckBoxCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "checkboxSquareCheck"));
        return arrayList;
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
