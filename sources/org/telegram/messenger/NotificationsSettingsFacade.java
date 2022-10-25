package org.telegram.messenger;

import android.content.SharedPreferences;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC$Dialog;
import org.telegram.tgnet.TLRPC$NotificationSound;
import org.telegram.tgnet.TLRPC$PeerNotifySettings;
import org.telegram.tgnet.TLRPC$TL_notificationSoundDefault;
import org.telegram.tgnet.TLRPC$TL_notificationSoundLocal;
import org.telegram.tgnet.TLRPC$TL_notificationSoundNone;
import org.telegram.tgnet.TLRPC$TL_notificationSoundRingtone;
/* loaded from: classes.dex */
public class NotificationsSettingsFacade {
    public static final String PROPERTY_CONTENT_PREVIEW = "content_preview_";
    public static final String PROPERTY_CUSTOM = "custom_";
    public static final String PROPERTY_NOTIFY = "notify2_";
    public static final String PROPERTY_NOTIFY_UNTIL = "notifyuntil_";
    public static final String PROPERTY_SILENT = "silent_";
    private final int currentAccount;

    public NotificationsSettingsFacade(int i) {
        this.currentAccount = i;
    }

    public boolean isDefault(long j, int i) {
        NotificationsController.getSharedPrefKey(j, i);
        return false;
    }

    public void clearPreference(long j, int i) {
        String sharedPrefKey = NotificationsController.getSharedPrefKey(j, i);
        SharedPreferences.Editor edit = getPreferences().edit();
        SharedPreferences.Editor remove = edit.remove("notify2_" + sharedPrefKey);
        SharedPreferences.Editor remove2 = remove.remove("custom_" + sharedPrefKey);
        SharedPreferences.Editor remove3 = remove2.remove("notifyuntil_" + sharedPrefKey);
        SharedPreferences.Editor remove4 = remove3.remove("content_preview_" + sharedPrefKey);
        remove4.remove("silent_" + sharedPrefKey).apply();
    }

    public int getProperty(String str, long j, int i, int i2) {
        String sharedPrefKey = NotificationsController.getSharedPrefKey(j, i);
        SharedPreferences preferences = getPreferences();
        if (preferences.contains(str + sharedPrefKey)) {
            SharedPreferences preferences2 = getPreferences();
            return preferences2.getInt(str + sharedPrefKey, i2);
        }
        String sharedPrefKey2 = NotificationsController.getSharedPrefKey(j, 0);
        SharedPreferences preferences3 = getPreferences();
        return preferences3.getInt(str + sharedPrefKey2, i2);
    }

    public long getProperty(String str, long j, int i, long j2) {
        String sharedPrefKey = NotificationsController.getSharedPrefKey(j, i);
        SharedPreferences preferences = getPreferences();
        if (preferences.contains(str + sharedPrefKey)) {
            SharedPreferences preferences2 = getPreferences();
            return preferences2.getLong(str + sharedPrefKey, j2);
        }
        String sharedPrefKey2 = NotificationsController.getSharedPrefKey(j, 0);
        SharedPreferences preferences3 = getPreferences();
        return preferences3.getLong(str + sharedPrefKey2, j2);
    }

    public boolean getProperty(String str, long j, int i, boolean z) {
        String sharedPrefKey = NotificationsController.getSharedPrefKey(j, i);
        SharedPreferences preferences = getPreferences();
        if (preferences.contains(str + sharedPrefKey)) {
            SharedPreferences preferences2 = getPreferences();
            return preferences2.getBoolean(str + sharedPrefKey, z);
        }
        String sharedPrefKey2 = NotificationsController.getSharedPrefKey(j, 0);
        SharedPreferences preferences3 = getPreferences();
        return preferences3.getBoolean(str + sharedPrefKey2, z);
    }

    public String getPropertyString(String str, long j, int i, String str2) {
        String sharedPrefKey = NotificationsController.getSharedPrefKey(j, i);
        SharedPreferences preferences = getPreferences();
        if (preferences.contains(str + sharedPrefKey)) {
            SharedPreferences preferences2 = getPreferences();
            return preferences2.getString(str + sharedPrefKey, str2);
        }
        String sharedPrefKey2 = NotificationsController.getSharedPrefKey(j, 0);
        SharedPreferences preferences3 = getPreferences();
        return preferences3.getString(str + sharedPrefKey2, str2);
    }

    public void removeProperty(String str, long j, int i) {
        String sharedPrefKey = NotificationsController.getSharedPrefKey(j, i);
        SharedPreferences.Editor edit = getPreferences().edit();
        edit.remove(str + sharedPrefKey).apply();
    }

    private SharedPreferences getPreferences() {
        return MessagesController.getNotificationsSettings(this.currentAccount);
    }

    public void applyDialogNotificationsSettings(long j, int i, TLRPC$PeerNotifySettings tLRPC$PeerNotifySettings) {
        int i2;
        int i3;
        if (tLRPC$PeerNotifySettings == null) {
            return;
        }
        String sharedPrefKey = NotificationsController.getSharedPrefKey(j, i);
        MessagesController messagesController = MessagesController.getInstance(this.currentAccount);
        ConnectionsManager connectionsManager = ConnectionsManager.getInstance(this.currentAccount);
        MessagesStorage messagesStorage = MessagesStorage.getInstance(this.currentAccount);
        NotificationsController notificationsController = NotificationsController.getInstance(this.currentAccount);
        SharedPreferences preferences = getPreferences();
        int i4 = preferences.getInt("notify2_" + sharedPrefKey, -1);
        SharedPreferences preferences2 = getPreferences();
        int i5 = preferences2.getInt("notifyuntil_" + sharedPrefKey, 0);
        SharedPreferences.Editor edit = getPreferences().edit();
        if ((tLRPC$PeerNotifySettings.flags & 2) != 0) {
            edit.putBoolean("silent_" + sharedPrefKey, tLRPC$PeerNotifySettings.silent);
        } else {
            edit.remove("silent_" + sharedPrefKey);
        }
        TLRPC$Dialog tLRPC$Dialog = null;
        if (i == 0) {
            tLRPC$Dialog = messagesController.dialogs_dict.get(j);
        }
        if (tLRPC$Dialog != null) {
            tLRPC$Dialog.notify_settings = tLRPC$PeerNotifySettings;
        }
        boolean z = true;
        if ((tLRPC$PeerNotifySettings.flags & 4) == 0) {
            if (i4 != -1) {
                if (tLRPC$Dialog != null) {
                    tLRPC$Dialog.notify_settings.mute_until = 0;
                }
                edit.remove("notify2_" + sharedPrefKey);
            } else {
                z = false;
            }
            if (i == 0) {
                messagesStorage.setDialogFlags(j, 0L);
            }
        } else if (tLRPC$PeerNotifySettings.mute_until > connectionsManager.getCurrentTime()) {
            if (tLRPC$PeerNotifySettings.mute_until <= connectionsManager.getCurrentTime() + 31536000) {
                if (i4 == 3 && i5 == tLRPC$PeerNotifySettings.mute_until) {
                    z = false;
                } else {
                    edit.putInt("notify2_" + sharedPrefKey, 3);
                    edit.putInt("notifyuntil_" + sharedPrefKey, tLRPC$PeerNotifySettings.mute_until);
                    if (tLRPC$Dialog != null) {
                        tLRPC$Dialog.notify_settings.mute_until = 0;
                    }
                }
                i3 = tLRPC$PeerNotifySettings.mute_until;
            } else if (i4 != 2) {
                edit.putInt("notify2_" + sharedPrefKey, 2);
                if (tLRPC$Dialog != null) {
                    tLRPC$Dialog.notify_settings.mute_until = Integer.MAX_VALUE;
                }
                i3 = 0;
            } else {
                i3 = 0;
                z = false;
            }
            if (i == 0) {
                messagesStorage.setDialogFlags(j, (i3 << 32) | 1);
                notificationsController.removeNotificationsForDialog(j);
            }
        } else {
            if (i4 == 0 || i4 == 1) {
                z = false;
            } else {
                if (tLRPC$Dialog != null) {
                    i2 = 0;
                    tLRPC$Dialog.notify_settings.mute_until = 0;
                } else {
                    i2 = 0;
                }
                edit.putInt("notify2_" + sharedPrefKey, i2);
            }
            if (i == 0) {
                messagesStorage.setDialogFlags(j, 0L);
            }
        }
        boolean z2 = z;
        applySoundSettings(tLRPC$PeerNotifySettings.android_sound, edit, j, i, 0, false);
        edit.apply();
        if (!z2) {
            return;
        }
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.notificationsSettingsUpdated, new Object[0]);
    }

    public void applySoundSettings(TLRPC$NotificationSound tLRPC$NotificationSound, SharedPreferences.Editor editor, long j, int i, int i2, boolean z) {
        String str;
        String str2;
        String str3;
        if (tLRPC$NotificationSound == null) {
            return;
        }
        if (j != 0) {
            String sharedPrefKey = NotificationsController.getSharedPrefKey(j, i);
            str = "sound_" + sharedPrefKey;
            str3 = "sound_path_" + sharedPrefKey;
            str2 = "sound_document_id_" + sharedPrefKey;
        } else if (i2 == 0) {
            str = "GroupSound";
            str2 = "GroupSoundDocId";
            str3 = "GroupSoundPath";
        } else if (i2 == 1) {
            str = "GlobalSound";
            str2 = "GlobalSoundDocId";
            str3 = "GlobalSoundPath";
        } else {
            str = "ChannelSound";
            str2 = "ChannelSoundDocId";
            str3 = "ChannelSoundPath";
        }
        if (tLRPC$NotificationSound instanceof TLRPC$TL_notificationSoundDefault) {
            editor.putString(str, "Default");
            editor.putString(str3, "Default");
            editor.remove(str2);
        } else if (tLRPC$NotificationSound instanceof TLRPC$TL_notificationSoundNone) {
            editor.putString(str, "NoSound");
            editor.putString(str3, "NoSound");
            editor.remove(str2);
        } else if (tLRPC$NotificationSound instanceof TLRPC$TL_notificationSoundLocal) {
            TLRPC$TL_notificationSoundLocal tLRPC$TL_notificationSoundLocal = (TLRPC$TL_notificationSoundLocal) tLRPC$NotificationSound;
            editor.putString(str, tLRPC$TL_notificationSoundLocal.title);
            editor.putString(str3, tLRPC$TL_notificationSoundLocal.data);
            editor.remove(str2);
        } else if (!(tLRPC$NotificationSound instanceof TLRPC$TL_notificationSoundRingtone)) {
        } else {
            TLRPC$TL_notificationSoundRingtone tLRPC$TL_notificationSoundRingtone = (TLRPC$TL_notificationSoundRingtone) tLRPC$NotificationSound;
            editor.putLong(str2, tLRPC$TL_notificationSoundRingtone.id);
            MediaDataController.getInstance(this.currentAccount).checkRingtones();
            if (z && j != 0) {
                editor.putBoolean("custom_" + j, true);
            }
            MediaDataController.getInstance(this.currentAccount).ringtoneDataStore.getDocument(tLRPC$TL_notificationSoundRingtone.id);
        }
    }

    public void setSettingsForDialog(TLRPC$Dialog tLRPC$Dialog, TLRPC$PeerNotifySettings tLRPC$PeerNotifySettings) {
        SharedPreferences.Editor edit = getPreferences().edit();
        long peerId = MessageObject.getPeerId(tLRPC$Dialog.peer);
        if ((tLRPC$Dialog.notify_settings.flags & 2) != 0) {
            edit.putBoolean("silent_" + peerId, tLRPC$Dialog.notify_settings.silent);
        } else {
            edit.remove("silent_" + peerId);
        }
        ConnectionsManager connectionsManager = ConnectionsManager.getInstance(this.currentAccount);
        TLRPC$PeerNotifySettings tLRPC$PeerNotifySettings2 = tLRPC$Dialog.notify_settings;
        if ((tLRPC$PeerNotifySettings2.flags & 4) != 0) {
            if (tLRPC$PeerNotifySettings2.mute_until > connectionsManager.getCurrentTime()) {
                if (tLRPC$Dialog.notify_settings.mute_until > connectionsManager.getCurrentTime() + 31536000) {
                    edit.putInt("notify2_" + peerId, 2);
                    tLRPC$Dialog.notify_settings.mute_until = Integer.MAX_VALUE;
                } else {
                    edit.putInt("notify2_" + peerId, 3);
                    edit.putInt("notifyuntil_" + peerId, tLRPC$Dialog.notify_settings.mute_until);
                }
            } else {
                edit.putInt("notify2_" + peerId, 0);
            }
        } else {
            edit.remove("notify2_" + peerId);
        }
        edit.apply();
    }
}
