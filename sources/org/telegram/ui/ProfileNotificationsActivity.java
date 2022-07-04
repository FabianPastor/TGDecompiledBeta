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
import android.os.Build;
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
    private static final int done_button = 1;
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
        void didCreateNewException(NotificationsSettingsActivity.NotificationException notificationException);

        void didRemoveException(long j);

        /* renamed from: org.telegram.ui.ProfileNotificationsActivity$ProfileNotificationsActivityDelegate$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static void $default$didRemoveException(ProfileNotificationsActivityDelegate _this, long dialog_id) {
            }
        }
    }

    public ProfileNotificationsActivity(Bundle args) {
        this(args, (Theme.ResourcesProvider) null);
    }

    public ProfileNotificationsActivity(Bundle args, Theme.ResourcesProvider resourcesProvider2) {
        super(args);
        this.resourcesProvider = resourcesProvider2;
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
        } else {
            this.avatarRow = -1;
            this.avatarSectionRow = -1;
        }
        int i2 = this.rowCount;
        int i3 = i2 + 1;
        this.rowCount = i3;
        this.generalRow = i2;
        if (z) {
            this.rowCount = i3 + 1;
            this.enableRow = i3;
        } else {
            this.enableRow = -1;
        }
        if (!DialogObject.isEncryptedDialog(this.dialogId)) {
            int i4 = this.rowCount;
            this.rowCount = i4 + 1;
            this.previewRow = i4;
        } else {
            this.previewRow = -1;
        }
        int i5 = this.rowCount;
        int i6 = i5 + 1;
        this.rowCount = i6;
        this.soundRow = i5;
        this.rowCount = i6 + 1;
        this.vibrateRow = i6;
        if (DialogObject.isChatDialog(this.dialogId)) {
            int i7 = this.rowCount;
            this.rowCount = i7 + 1;
            this.smartRow = i7;
        } else {
            this.smartRow = -1;
        }
        if (Build.VERSION.SDK_INT >= 21) {
            int i8 = this.rowCount;
            this.rowCount = i8 + 1;
            this.priorityRow = i8;
        } else {
            this.priorityRow = -1;
        }
        int i9 = this.rowCount;
        this.rowCount = i9 + 1;
        this.priorityInfoRow = i9;
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
            int i10 = this.rowCount;
            int i11 = i10 + 1;
            this.rowCount = i11;
            this.popupRow = i10;
            int i12 = i11 + 1;
            this.rowCount = i12;
            this.popupEnabledRow = i11;
            int i13 = i12 + 1;
            this.rowCount = i13;
            this.popupDisabledRow = i12;
            this.rowCount = i13 + 1;
            this.popupInfoRow = i13;
        }
        if (DialogObject.isUserDialog(this.dialogId)) {
            int i14 = this.rowCount;
            int i15 = i14 + 1;
            this.rowCount = i15;
            this.callsRow = i14;
            int i16 = i15 + 1;
            this.rowCount = i16;
            this.callsVibrateRow = i15;
            int i17 = i16 + 1;
            this.rowCount = i17;
            this.ringtoneRow = i16;
            this.rowCount = i17 + 1;
            this.ringtoneInfoRow = i17;
        } else {
            this.callsRow = -1;
            this.callsVibrateRow = -1;
            this.ringtoneRow = -1;
            this.ringtoneInfoRow = -1;
        }
        int i18 = this.rowCount;
        int i19 = i18 + 1;
        this.rowCount = i19;
        this.ledRow = i18;
        int i20 = i19 + 1;
        this.rowCount = i20;
        this.colorRow = i19;
        int i21 = i20 + 1;
        this.rowCount = i21;
        this.ledInfoRow = i20;
        if (!this.addingException) {
            int i22 = i21 + 1;
            this.rowCount = i22;
            this.customResetRow = i21;
            this.rowCount = i22 + 1;
            this.customResetShadowRow = i22;
        } else {
            this.customResetRow = -1;
            this.customResetShadowRow = -1;
        }
        boolean defaultEnabled = NotificationsController.getInstance(this.currentAccount).isGlobalNotificationsEnabled(this.dialogId);
        if (this.addingException) {
            this.notificationsEnabled = !defaultEnabled;
        } else {
            SharedPreferences preferences = MessagesController.getNotificationsSettings(this.currentAccount);
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
        }
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.notificationsSettingsUpdated);
        return super.onFragmentCreate();
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
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    if (!ProfileNotificationsActivity.this.addingException && ProfileNotificationsActivity.this.notificationsEnabled) {
                        SharedPreferences.Editor edit = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount).edit();
                        edit.putInt("notify2_" + ProfileNotificationsActivity.this.dialogId, 0).apply();
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
                    editor.apply();
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
        ChatAvatarContainer chatAvatarContainer = new ChatAvatarContainer(context, (ChatActivity) null, false, this.resourcesProvider);
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
        frameLayout.setBackgroundColor(Theme.getColor("windowBackgroundGray", this.resourcesProvider));
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
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new ProfileNotificationsActivity$$ExternalSyntheticLambda7(this, context));
        return this.fragmentView;
    }

    /* renamed from: lambda$createView$6$org-telegram-ui-ProfileNotificationsActivity  reason: not valid java name */
    public /* synthetic */ void m4541lambda$createView$6$orgtelegramuiProfileNotificationsActivity(Context context, View view, int position) {
        if (view.isEnabled()) {
            if (position == this.customResetRow) {
                AlertDialog dialog = new AlertDialog.Builder(context, this.resourcesProvider).setTitle(LocaleController.getString(NUM)).setMessage(LocaleController.getString(NUM)).setPositiveButton(LocaleController.getString(NUM), new ProfileNotificationsActivity$$ExternalSyntheticLambda0(this)).setNegativeButton(LocaleController.getString(NUM), (DialogInterface.OnClickListener) null).create();
                showDialog(dialog);
                TextView button = (TextView) dialog.getButton(-1);
                if (button != null) {
                    button.setTextColor(Theme.getColor("dialogTextRed2"));
                }
            } else if (position == this.soundRow) {
                Bundle bundle = new Bundle();
                bundle.putLong("dialog_id", this.dialogId);
                presentFragment(new NotificationsSoundActivity(bundle, this.resourcesProvider));
            } else if (position == this.ringtoneRow) {
                try {
                    Intent tmpIntent = new Intent("android.intent.action.RINGTONE_PICKER");
                    tmpIntent.putExtra("android.intent.extra.ringtone.TYPE", 1);
                    tmpIntent.putExtra("android.intent.extra.ringtone.SHOW_DEFAULT", true);
                    tmpIntent.putExtra("android.intent.extra.ringtone.SHOW_SILENT", true);
                    tmpIntent.putExtra("android.intent.extra.ringtone.DEFAULT_URI", RingtoneManager.getDefaultUri(1));
                    SharedPreferences preferences = MessagesController.getNotificationsSettings(this.currentAccount);
                    Uri currentSound = null;
                    String defaultPath = null;
                    Uri defaultUri = Settings.System.DEFAULT_NOTIFICATION_URI;
                    if (defaultUri != null) {
                        defaultPath = defaultUri.getPath();
                    }
                    String path = preferences.getString("ringtone_path_" + this.dialogId, defaultPath);
                    if (path != null && !path.equals("NoSound")) {
                        currentSound = path.equals(defaultPath) ? defaultUri : Uri.parse(path);
                    }
                    tmpIntent.putExtra("android.intent.extra.ringtone.EXISTING_URI", currentSound);
                    startActivityForResult(tmpIntent, 13);
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            } else if (position == this.vibrateRow) {
                showDialog(AlertsCreator.createVibrationSelectDialog(getParentActivity(), this.dialogId, false, false, new ProfileNotificationsActivity$$ExternalSyntheticLambda1(this), this.resourcesProvider));
            } else if (position == this.enableRow) {
                TextCheckCell checkCell = (TextCheckCell) view;
                boolean isChecked = true ^ checkCell.isChecked();
                this.notificationsEnabled = isChecked;
                checkCell.setChecked(isChecked);
                checkRowsEnabled();
            } else if (position == this.previewRow) {
                TextCheckCell checkCell2 = (TextCheckCell) view;
                SharedPreferences.Editor edit = MessagesController.getNotificationsSettings(this.currentAccount).edit();
                edit.putBoolean("content_preview_" + this.dialogId, !checkCell2.isChecked()).apply();
                checkCell2.setChecked(true ^ checkCell2.isChecked());
            } else if (position == this.callsVibrateRow) {
                Activity parentActivity = getParentActivity();
                long j = this.dialogId;
                showDialog(AlertsCreator.createVibrationSelectDialog(parentActivity, j, "calls_vibrate_" + this.dialogId, (Runnable) new ProfileNotificationsActivity$$ExternalSyntheticLambda2(this), this.resourcesProvider));
            } else if (position == this.priorityRow) {
                showDialog(AlertsCreator.createPrioritySelectDialog(getParentActivity(), this.dialogId, -1, new ProfileNotificationsActivity$$ExternalSyntheticLambda3(this), this.resourcesProvider));
            } else if (position == this.smartRow) {
                if (getParentActivity() != null) {
                    SharedPreferences preferences2 = MessagesController.getNotificationsSettings(this.currentAccount);
                    int notifyMaxCount = preferences2.getInt("smart_max_count_" + this.dialogId, 2);
                    int notifyDelay = preferences2.getInt("smart_delay_" + this.dialogId, 180);
                    if (notifyMaxCount == 0) {
                        notifyMaxCount = 2;
                    }
                    AlertsCreator.createSoundFrequencyPickerDialog(getParentActivity(), notifyMaxCount, notifyDelay, new ProfileNotificationsActivity$$ExternalSyntheticLambda6(this), this.resourcesProvider);
                }
            } else if (position == this.colorRow) {
                if (getParentActivity() != null) {
                    showDialog(AlertsCreator.createColorSelectDialog(getParentActivity(), this.dialogId, -1, new ProfileNotificationsActivity$$ExternalSyntheticLambda4(this), this.resourcesProvider));
                }
            } else if (position == this.popupEnabledRow) {
                SharedPreferences.Editor edit2 = MessagesController.getNotificationsSettings(this.currentAccount).edit();
                edit2.putInt("popup_" + this.dialogId, 1).apply();
                ((RadioCell) view).setChecked(true, true);
                View view2 = this.listView.findViewWithTag(2);
                if (view2 != null) {
                    ((RadioCell) view2).setChecked(false, true);
                }
            } else if (position == this.popupDisabledRow) {
                SharedPreferences.Editor edit3 = MessagesController.getNotificationsSettings(this.currentAccount).edit();
                edit3.putInt("popup_" + this.dialogId, 2).apply();
                ((RadioCell) view).setChecked(true, true);
                View view3 = this.listView.findViewWithTag(1);
                if (view3 != null) {
                    ((RadioCell) view3).setChecked(false, true);
                }
            }
        }
    }

    /* renamed from: lambda$createView$0$org-telegram-ui-ProfileNotificationsActivity  reason: not valid java name */
    public /* synthetic */ void m4535lambda$createView$0$orgtelegramuiProfileNotificationsActivity(DialogInterface d, int w) {
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

    /* renamed from: lambda$createView$1$org-telegram-ui-ProfileNotificationsActivity  reason: not valid java name */
    public /* synthetic */ void m4536lambda$createView$1$orgtelegramuiProfileNotificationsActivity() {
        ListAdapter listAdapter = this.adapter;
        if (listAdapter != null) {
            listAdapter.notifyItemChanged(this.vibrateRow);
        }
    }

    /* renamed from: lambda$createView$2$org-telegram-ui-ProfileNotificationsActivity  reason: not valid java name */
    public /* synthetic */ void m4537lambda$createView$2$orgtelegramuiProfileNotificationsActivity() {
        ListAdapter listAdapter = this.adapter;
        if (listAdapter != null) {
            listAdapter.notifyItemChanged(this.callsVibrateRow);
        }
    }

    /* renamed from: lambda$createView$3$org-telegram-ui-ProfileNotificationsActivity  reason: not valid java name */
    public /* synthetic */ void m4538lambda$createView$3$orgtelegramuiProfileNotificationsActivity() {
        ListAdapter listAdapter = this.adapter;
        if (listAdapter != null) {
            listAdapter.notifyItemChanged(this.priorityRow);
        }
    }

    /* renamed from: lambda$createView$4$org-telegram-ui-ProfileNotificationsActivity  reason: not valid java name */
    public /* synthetic */ void m4539lambda$createView$4$orgtelegramuiProfileNotificationsActivity(int time, int minute) {
        SharedPreferences.Editor edit = MessagesController.getNotificationsSettings(this.currentAccount).edit();
        SharedPreferences.Editor putInt = edit.putInt("smart_max_count_" + this.dialogId, time);
        putInt.putInt("smart_delay_" + this.dialogId, minute).apply();
        ListAdapter listAdapter = this.adapter;
        if (listAdapter != null) {
            listAdapter.notifyItemChanged(this.smartRow);
        }
    }

    /* renamed from: lambda$createView$5$org-telegram-ui-ProfileNotificationsActivity  reason: not valid java name */
    public /* synthetic */ void m4540lambda$createView$5$orgtelegramuiProfileNotificationsActivity() {
        ListAdapter listAdapter = this.adapter;
        if (listAdapter != null) {
            listAdapter.notifyItemChanged(this.colorRow);
        }
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
            editor.apply();
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

    private void checkRowsEnabled() {
        int count = this.listView.getChildCount();
        ArrayList<Animator> animators = new ArrayList<>();
        for (int a = 0; a < count; a++) {
            RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.getChildViewHolder(this.listView.getChildAt(a));
            int type = holder.getItemViewType();
            int position = holder.getAdapterPosition();
            if (!(position == this.enableRow || position == this.customResetRow)) {
                switch (type) {
                    case 0:
                        ((HeaderCell) holder.itemView).setEnabled(this.notificationsEnabled, animators);
                        break;
                    case 1:
                        ((TextSettingsCell) holder.itemView).setEnabled(this.notificationsEnabled, animators);
                        break;
                    case 2:
                        ((TextInfoPrivacyCell) holder.itemView).setEnabled(this.notificationsEnabled, animators);
                        break;
                    case 3:
                        ((TextColorCell) holder.itemView).setEnabled(this.notificationsEnabled, animators);
                        break;
                    case 4:
                        ((RadioCell) holder.itemView).setEnabled(this.notificationsEnabled, animators);
                        break;
                    case 7:
                        if (position != this.previewRow) {
                            break;
                        } else {
                            ((TextCheckCell) holder.itemView).setEnabled(this.notificationsEnabled, animators);
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
        private static final int VIEW_TYPE_HEADER = 0;
        private static final int VIEW_TYPE_INFO = 2;
        private static final int VIEW_TYPE_RADIO = 4;
        private static final int VIEW_TYPE_SHADOW = 6;
        private static final int VIEW_TYPE_TEXT_CHECK = 7;
        private static final int VIEW_TYPE_TEXT_COLOR = 3;
        private static final int VIEW_TYPE_TEXT_SETTINGS = 1;
        private static final int VIEW_TYPE_USER = 5;
        private Context context;

        public ListAdapter(Context ctx) {
            this.context = ctx;
        }

        public int getItemCount() {
            return ProfileNotificationsActivity.this.rowCount;
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            if (holder.getAdapterPosition() == ProfileNotificationsActivity.this.previewRow) {
                return ProfileNotificationsActivity.this.notificationsEnabled;
            }
            if (holder.getAdapterPosition() == ProfileNotificationsActivity.this.customResetRow) {
                return true;
            }
            switch (holder.getItemViewType()) {
                case 0:
                case 2:
                case 5:
                case 6:
                    return false;
                case 1:
                case 3:
                case 4:
                    return ProfileNotificationsActivity.this.notificationsEnabled;
                case 7:
                    return true;
                default:
                    return true;
            }
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    View view2 = new HeaderCell(this.context, ProfileNotificationsActivity.this.resourcesProvider);
                    view2.setBackgroundColor(ProfileNotificationsActivity.this.getThemedColor("windowBackgroundWhite"));
                    view = view2;
                    break;
                case 1:
                    View view3 = new TextSettingsCell(this.context, ProfileNotificationsActivity.this.resourcesProvider);
                    view3.setBackgroundColor(ProfileNotificationsActivity.this.getThemedColor("windowBackgroundWhite"));
                    view = view3;
                    break;
                case 2:
                    view = new TextInfoPrivacyCell(this.context, ProfileNotificationsActivity.this.resourcesProvider);
                    break;
                case 3:
                    View view4 = new TextColorCell(this.context, ProfileNotificationsActivity.this.resourcesProvider);
                    view4.setBackgroundColor(ProfileNotificationsActivity.this.getThemedColor("windowBackgroundWhite"));
                    view = view4;
                    break;
                case 4:
                    View view5 = new RadioCell(this.context, ProfileNotificationsActivity.this.resourcesProvider);
                    view5.setBackgroundColor(ProfileNotificationsActivity.this.getThemedColor("windowBackgroundWhite"));
                    view = view5;
                    break;
                case 5:
                    View view6 = new UserCell2(this.context, 4, 0, ProfileNotificationsActivity.this.resourcesProvider);
                    view6.setBackgroundColor(ProfileNotificationsActivity.this.getThemedColor("windowBackgroundWhite"));
                    view = view6;
                    break;
                case 6:
                    view = new ShadowSectionCell(this.context, ProfileNotificationsActivity.this.resourcesProvider);
                    break;
                default:
                    View view7 = new TextCheckCell(this.context, ProfileNotificationsActivity.this.resourcesProvider);
                    view7.setBackgroundColor(ProfileNotificationsActivity.this.getThemedColor("windowBackgroundWhite"));
                    view = view7;
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
            boolean z = true;
            boolean z2 = false;
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
                    if (i == ProfileNotificationsActivity.this.customResetRow) {
                        textCell.setText(LocaleController.getString(NUM), false);
                        textCell.setTextColor(ProfileNotificationsActivity.this.getThemedColor("dialogTextRed"));
                        return;
                    }
                    textCell.setTextColor(ProfileNotificationsActivity.this.getThemedColor("windowBackgroundWhiteBlackText"));
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
                            if (ProfileNotificationsActivity.this.smartRow == -1 && ProfileNotificationsActivity.this.priorityRow == -1) {
                                z = false;
                            }
                            textCell.setTextAndValue(string, string2, z);
                            return;
                        } else if (value3 == 1) {
                            String string3 = LocaleController.getString("Vibrate", NUM);
                            String string4 = LocaleController.getString("Short", NUM);
                            if (ProfileNotificationsActivity.this.smartRow == -1 && ProfileNotificationsActivity.this.priorityRow == -1) {
                                z = false;
                            }
                            textCell.setTextAndValue(string3, string4, z);
                            return;
                        } else if (value3 == 2) {
                            String string5 = LocaleController.getString("Vibrate", NUM);
                            String string6 = LocaleController.getString("VibrationDisabled", NUM);
                            if (ProfileNotificationsActivity.this.smartRow == -1 && ProfileNotificationsActivity.this.priorityRow == -1) {
                                z = false;
                            }
                            textCell.setTextAndValue(string5, string6, z);
                            return;
                        } else if (value3 == 3) {
                            String string7 = LocaleController.getString("Vibrate", NUM);
                            String string8 = LocaleController.getString("Long", NUM);
                            if (ProfileNotificationsActivity.this.smartRow == -1 && ProfileNotificationsActivity.this.priorityRow == -1) {
                                z = false;
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
                            if (ProfileNotificationsActivity.this.priorityRow == -1) {
                                z = false;
                            }
                            textCell.setTextAndValue(string9, string10, z);
                            return;
                        }
                        String minutes = LocaleController.formatPluralString("Minutes", notifyDelay / 60, new Object[0]);
                        String string11 = LocaleController.getString("SmartNotifications", NUM);
                        String formatString = LocaleController.formatString("SmartNotificationsInfo", NUM, Integer.valueOf(notifyMaxCount), minutes);
                        if (ProfileNotificationsActivity.this.priorityRow == -1) {
                            z = false;
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
                        textCell2.setBackground(Theme.getThemedDrawable(this.context, NUM, "windowBackgroundGrayShadow"));
                        return;
                    } else if (i == ProfileNotificationsActivity.this.ledInfoRow) {
                        textCell2.setText(LocaleController.getString("NotificationsLedInfo", NUM));
                        textCell2.setBackground(Theme.getThemedDrawable(this.context, NUM, "windowBackgroundGrayShadow"));
                        return;
                    } else if (i == ProfileNotificationsActivity.this.priorityInfoRow) {
                        if (ProfileNotificationsActivity.this.priorityRow == -1) {
                            textCell2.setText("");
                        } else {
                            textCell2.setText(LocaleController.getString("PriorityInfo", NUM));
                        }
                        textCell2.setBackground(Theme.getThemedDrawable(this.context, NUM, "windowBackgroundGrayShadow"));
                        return;
                    } else if (i == ProfileNotificationsActivity.this.ringtoneInfoRow) {
                        textCell2.setText(LocaleController.getString("VoipRingtoneInfo", NUM));
                        textCell2.setBackground(Theme.getThemedDrawable(this.context, NUM, "windowBackgroundGrayShadow"));
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
                            z2 = true;
                        }
                        radioCell.setText(string12, z2, true);
                        radioCell.setTag(1);
                        return;
                    } else if (i == ProfileNotificationsActivity.this.popupDisabledRow) {
                        String string13 = LocaleController.getString("PopupDisabled", NUM);
                        if (popup != 2) {
                            z = false;
                        }
                        radioCell.setText(string13, z, false);
                        radioCell.setTag(2);
                        return;
                    } else {
                        return;
                    }
                case 5:
                    UserCell2 userCell2 = (UserCell2) viewHolder.itemView;
                    if (DialogObject.isUserDialog(ProfileNotificationsActivity.this.dialogId)) {
                        object = MessagesController.getInstance(ProfileNotificationsActivity.this.currentAccount).getUser(Long.valueOf(ProfileNotificationsActivity.this.dialogId));
                    } else {
                        object = MessagesController.getInstance(ProfileNotificationsActivity.this.currentAccount).getChat(Long.valueOf(-ProfileNotificationsActivity.this.dialogId));
                    }
                    userCell2.setData(object, (CharSequence) null, (CharSequence) null, 0);
                    return;
                case 7:
                    TextCheckCell checkCell = (TextCheckCell) viewHolder.itemView;
                    SharedPreferences preferences4 = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount);
                    if (i == ProfileNotificationsActivity.this.enableRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("Notifications", NUM), ProfileNotificationsActivity.this.notificationsEnabled, true);
                        return;
                    } else if (i == ProfileNotificationsActivity.this.previewRow) {
                        String string14 = LocaleController.getString("MessagePreview", NUM);
                        checkCell.setTextAndCheck(string14, preferences4.getBoolean("content_preview_" + ProfileNotificationsActivity.this.dialogId, true), true);
                        return;
                    } else {
                        return;
                    }
                default:
                    return;
            }
        }

        public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
            switch (holder.getItemViewType()) {
                case 0:
                    ((HeaderCell) holder.itemView).setEnabled(ProfileNotificationsActivity.this.notificationsEnabled, (ArrayList<Animator>) null);
                    return;
                case 1:
                    TextSettingsCell textCell = (TextSettingsCell) holder.itemView;
                    if (holder.getAdapterPosition() == ProfileNotificationsActivity.this.customResetRow) {
                        textCell.setEnabled(true, (ArrayList<Animator>) null);
                        return;
                    } else {
                        textCell.setEnabled(ProfileNotificationsActivity.this.notificationsEnabled, (ArrayList<Animator>) null);
                        return;
                    }
                case 2:
                    ((TextInfoPrivacyCell) holder.itemView).setEnabled(ProfileNotificationsActivity.this.notificationsEnabled, (ArrayList<Animator>) null);
                    return;
                case 3:
                    ((TextColorCell) holder.itemView).setEnabled(ProfileNotificationsActivity.this.notificationsEnabled, (ArrayList<Animator>) null);
                    return;
                case 4:
                    ((RadioCell) holder.itemView).setEnabled(ProfileNotificationsActivity.this.notificationsEnabled, (ArrayList<Animator>) null);
                    return;
                case 7:
                    TextCheckCell checkCell = (TextCheckCell) holder.itemView;
                    if (holder.getAdapterPosition() == ProfileNotificationsActivity.this.previewRow) {
                        checkCell.setEnabled(ProfileNotificationsActivity.this.notificationsEnabled, (ArrayList<Animator>) null);
                        return;
                    } else {
                        checkCell.setEnabled(true, (ArrayList<Animator>) null);
                        return;
                    }
                default:
                    return;
            }
        }

        public int getItemViewType(int position) {
            if (position == ProfileNotificationsActivity.this.generalRow || position == ProfileNotificationsActivity.this.popupRow || position == ProfileNotificationsActivity.this.ledRow || position == ProfileNotificationsActivity.this.callsRow) {
                return 0;
            }
            if (position == ProfileNotificationsActivity.this.soundRow || position == ProfileNotificationsActivity.this.vibrateRow || position == ProfileNotificationsActivity.this.priorityRow || position == ProfileNotificationsActivity.this.smartRow || position == ProfileNotificationsActivity.this.ringtoneRow || position == ProfileNotificationsActivity.this.callsVibrateRow || position == ProfileNotificationsActivity.this.customResetRow) {
                return 1;
            }
            if (position == ProfileNotificationsActivity.this.popupInfoRow || position == ProfileNotificationsActivity.this.ledInfoRow || position == ProfileNotificationsActivity.this.priorityInfoRow || position == ProfileNotificationsActivity.this.ringtoneInfoRow) {
                return 2;
            }
            if (position == ProfileNotificationsActivity.this.colorRow) {
                return 3;
            }
            if (position == ProfileNotificationsActivity.this.popupEnabledRow || position == ProfileNotificationsActivity.this.popupDisabledRow) {
                return 4;
            }
            if (position == ProfileNotificationsActivity.this.avatarRow) {
                return 5;
            }
            if (position == ProfileNotificationsActivity.this.avatarSectionRow || position == ProfileNotificationsActivity.this.customResetShadowRow) {
                return 6;
            }
            if (position == ProfileNotificationsActivity.this.enableRow || position == ProfileNotificationsActivity.this.previewRow) {
                return 7;
            }
            return 0;
        }
    }

    public int getNavigationBarColor() {
        return getThemedColor("windowBackgroundGray");
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        ThemeDescription.ThemeDescriptionDelegate cellDelegate = new ProfileNotificationsActivity$$ExternalSyntheticLambda5(this);
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

    /* renamed from: lambda$getThemeDescriptions$7$org-telegram-ui-ProfileNotificationsActivity  reason: not valid java name */
    public /* synthetic */ void m4542x9710d3ce() {
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
