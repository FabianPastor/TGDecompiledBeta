package org.telegram.messenger.ringtone;

import android.content.SharedPreferences;
import android.text.TextUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$TL_account_getSavedRingtones;
import org.telegram.tgnet.TLRPC$TL_account_savedRingtones;
import org.telegram.tgnet.TLRPC$TL_account_savedRingtonesNotModified;
import org.telegram.tgnet.TLRPC$TL_error;

public class RingtoneDataStore {
    private static volatile long lastReloadTimeMs;
    private static volatile long queryHash;
    public static final HashSet<String> ringtoneSupportedMimeType = new HashSet<>(Arrays.asList(new String[]{"audio/mpeg3", "audio/mpeg", "audio/ogg", "audio/m4a"}));
    private final long clientUserId;
    private final int currentAccount;
    private boolean loaded;
    private int localIds;
    String prefName = null;
    public final ArrayList<CachedTone> userRingtones = new ArrayList<>();

    public RingtoneDataStore(int i) {
        this.currentAccount = i;
        this.clientUserId = UserConfig.getInstance(i).clientUserId;
        SharedPreferences sharedPreferences = getSharedPreferences();
        try {
            queryHash = sharedPreferences.getLong("hash", 0);
            lastReloadTimeMs = sharedPreferences.getLong("lastReload", 0);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        loadUserRingtones();
    }

    public void loadUserRingtones() {
        boolean z = System.currentTimeMillis() - lastReloadTimeMs > 86400000;
        TLRPC$TL_account_getSavedRingtones tLRPC$TL_account_getSavedRingtones = new TLRPC$TL_account_getSavedRingtones();
        tLRPC$TL_account_getSavedRingtones.hash = queryHash;
        if (z) {
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_account_getSavedRingtones, new RingtoneDataStore$$ExternalSyntheticLambda3(this));
            return;
        }
        if (!this.loaded) {
            loadFromPrefs(true);
            this.loaded = true;
        }
        checkRingtoneSoundsLoaded();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadUserRingtones$1(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new RingtoneDataStore$$ExternalSyntheticLambda1(this, tLObject));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadUserRingtones$0(TLObject tLObject) {
        if (tLObject != null) {
            if (tLObject instanceof TLRPC$TL_account_savedRingtonesNotModified) {
                loadFromPrefs(true);
            } else if (tLObject instanceof TLRPC$TL_account_savedRingtones) {
                TLRPC$TL_account_savedRingtones tLRPC$TL_account_savedRingtones = (TLRPC$TL_account_savedRingtones) tLObject;
                saveTones(tLRPC$TL_account_savedRingtones.ringtones);
                SharedPreferences.Editor edit = getSharedPreferences().edit();
                long j = tLRPC$TL_account_savedRingtones.hash;
                queryHash = j;
                SharedPreferences.Editor putLong = edit.putLong("hash", j);
                long currentTimeMillis = System.currentTimeMillis();
                lastReloadTimeMs = currentTimeMillis;
                putLong.putLong("lastReload", currentTimeMillis).apply();
            }
            checkRingtoneSoundsLoaded();
        }
    }

    private void loadFromPrefs(boolean z) {
        SharedPreferences sharedPreferences = getSharedPreferences();
        int i = sharedPreferences.getInt("count", 0);
        this.userRingtones.clear();
        for (int i2 = 0; i2 < i; i2++) {
            String string = sharedPreferences.getString("tone_document" + i2, "");
            String string2 = sharedPreferences.getString("tone_local_path" + i2, "");
            SerializedData serializedData = new SerializedData(Utilities.hexToBytes(string));
            try {
                TLRPC$Document TLdeserialize = TLRPC$Document.TLdeserialize(serializedData, serializedData.readInt32(true), true);
                CachedTone cachedTone = new CachedTone(this);
                cachedTone.document = TLdeserialize;
                cachedTone.localUri = string2;
                int i3 = this.localIds;
                this.localIds = i3 + 1;
                cachedTone.localId = i3;
                this.userRingtones.add(cachedTone);
            } catch (Throwable th) {
                if (!BuildVars.DEBUG_PRIVATE_VERSION) {
                    FileLog.e(th);
                } else {
                    throw th;
                }
            }
        }
        if (z) {
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.onUserRingtonesUpdated, new Object[0]);
        }
    }

    private void saveTones(ArrayList<TLRPC$Document> arrayList) {
        TLRPC$Document tLRPC$Document;
        if (!this.loaded) {
            loadFromPrefs(false);
            this.loaded = true;
        }
        HashMap hashMap = new HashMap();
        Iterator<CachedTone> it = this.userRingtones.iterator();
        while (it.hasNext()) {
            CachedTone next = it.next();
            if (!(next.localUri == null || (tLRPC$Document = next.document) == null)) {
                hashMap.put(Long.valueOf(tLRPC$Document.id), next.localUri);
            }
        }
        this.userRingtones.clear();
        SharedPreferences sharedPreferences = getSharedPreferences();
        sharedPreferences.edit().clear().apply();
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putInt("count", arrayList.size());
        for (int i = 0; i < arrayList.size(); i++) {
            TLRPC$Document tLRPC$Document2 = arrayList.get(i);
            String str = (String) hashMap.get(Long.valueOf(tLRPC$Document2.id));
            SerializedData serializedData = new SerializedData(tLRPC$Document2.getObjectSize());
            tLRPC$Document2.serializeToStream(serializedData);
            edit.putString("tone_document" + i, Utilities.bytesToHex(serializedData.toByteArray()));
            if (str != null) {
                edit.putString("tone_local_path" + i, str);
            }
            CachedTone cachedTone = new CachedTone(this);
            cachedTone.document = tLRPC$Document2;
            cachedTone.localUri = str;
            int i2 = this.localIds;
            this.localIds = i2 + 1;
            cachedTone.localId = i2;
            this.userRingtones.add(cachedTone);
        }
        edit.apply();
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.onUserRingtonesUpdated, new Object[0]);
    }

    public void saveTones() {
        SharedPreferences sharedPreferences = getSharedPreferences();
        sharedPreferences.edit().clear().apply();
        SharedPreferences.Editor edit = sharedPreferences.edit();
        int i = 0;
        for (int i2 = 0; i2 < this.userRingtones.size(); i2++) {
            if (!this.userRingtones.get(i2).uploading) {
                i++;
                TLRPC$Document tLRPC$Document = this.userRingtones.get(i2).document;
                String str = this.userRingtones.get(i2).localUri;
                SerializedData serializedData = new SerializedData(tLRPC$Document.getObjectSize());
                tLRPC$Document.serializeToStream(serializedData);
                edit.putString("tone_document" + i2, Utilities.bytesToHex(serializedData.toByteArray()));
                if (str != null) {
                    edit.putString("tone_local_path" + i2, str);
                }
            }
        }
        edit.putInt("count", i);
        edit.apply();
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.onUserRingtonesUpdated, new Object[0]);
    }

    private SharedPreferences getSharedPreferences() {
        if (this.prefName == null) {
            this.prefName = "ringtones_pref_" + this.clientUserId;
        }
        return ApplicationLoader.applicationContext.getSharedPreferences(this.prefName, 0);
    }

    public void addUploadingTone(String str) {
        CachedTone cachedTone = new CachedTone(this);
        cachedTone.localUri = str;
        int i = this.localIds;
        this.localIds = i + 1;
        cachedTone.localId = i;
        cachedTone.uploading = true;
        this.userRingtones.add(cachedTone);
    }

    public void onRingtoneUploaded(String str, TLRPC$Document tLRPC$Document, boolean z) {
        boolean z2 = true;
        if (z) {
            int i = 0;
            while (true) {
                if (i < this.userRingtones.size()) {
                    if (this.userRingtones.get(i).uploading && str.equals(this.userRingtones.get(i).localUri)) {
                        this.userRingtones.remove(i);
                        break;
                    }
                    i++;
                } else {
                    z2 = false;
                    break;
                }
            }
        } else {
            int i2 = 0;
            while (true) {
                if (i2 < this.userRingtones.size()) {
                    if (this.userRingtones.get(i2).uploading && str.equals(this.userRingtones.get(i2).localUri)) {
                        this.userRingtones.get(i2).uploading = false;
                        this.userRingtones.get(i2).document = tLRPC$Document;
                        break;
                    }
                    i2++;
                } else {
                    z2 = false;
                    break;
                }
            }
            if (z2) {
                saveTones();
            }
        }
        if (z2) {
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.onUserRingtonesUpdated, new Object[0]);
        }
    }

    public String getSoundPath(long j) {
        if (!this.loaded) {
            loadFromPrefs(true);
            this.loaded = true;
        }
        int i = 0;
        while (i < this.userRingtones.size()) {
            if (this.userRingtones.get(i).document == null || this.userRingtones.get(i).document.id != j) {
                i++;
            } else if (!TextUtils.isEmpty(this.userRingtones.get(i).localUri)) {
                return this.userRingtones.get(i).localUri;
            } else {
                return FileLoader.getPathToAttach(this.userRingtones.get(i).document).toString();
            }
        }
        return "NoSound";
    }

    public void checkRingtoneSoundsLoaded() {
        if (!this.loaded) {
            loadFromPrefs(true);
            this.loaded = true;
        }
        Utilities.globalQueue.postRunnable(new RingtoneDataStore$$ExternalSyntheticLambda0(this, new ArrayList(this.userRingtones)));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$checkRingtoneSoundsLoaded$3(ArrayList arrayList) {
        TLRPC$Document tLRPC$Document;
        File pathToAttach;
        for (int i = 0; i < arrayList.size(); i++) {
            CachedTone cachedTone = (CachedTone) arrayList.get(i);
            if ((TextUtils.isEmpty(cachedTone.localUri) || !new File(cachedTone.localUri).exists()) && (tLRPC$Document = cachedTone.document) != null && ((pathToAttach = FileLoader.getPathToAttach(tLRPC$Document)) == null || !pathToAttach.exists())) {
                AndroidUtilities.runOnUIThread(new RingtoneDataStore$$ExternalSyntheticLambda2(this, tLRPC$Document));
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$checkRingtoneSoundsLoaded$2(TLRPC$Document tLRPC$Document) {
        FileLoader.getInstance(this.currentAccount).loadFile(tLRPC$Document, tLRPC$Document, 0, 0);
    }

    public boolean isLoaded() {
        return this.loaded;
    }

    public void remove(TLRPC$Document tLRPC$Document) {
        if (tLRPC$Document != null) {
            if (!this.loaded) {
                loadFromPrefs(true);
                this.loaded = true;
            }
            int i = 0;
            while (i < this.userRingtones.size()) {
                if (this.userRingtones.get(i).document == null || this.userRingtones.get(i).document.id != tLRPC$Document.id) {
                    i++;
                } else {
                    this.userRingtones.remove(i);
                    return;
                }
            }
        }
    }

    public boolean contains(long j) {
        return getDocument(j) != null;
    }

    public void addTone(TLRPC$Document tLRPC$Document) {
        if (tLRPC$Document != null && !contains(tLRPC$Document.id)) {
            CachedTone cachedTone = new CachedTone(this);
            cachedTone.document = tLRPC$Document;
            int i = this.localIds;
            this.localIds = i + 1;
            cachedTone.localId = i;
            cachedTone.uploading = false;
            this.userRingtones.add(cachedTone);
            saveTones();
        }
    }

    public TLRPC$Document getDocument(long j) {
        if (!this.loaded) {
            loadFromPrefs(true);
            this.loaded = true;
        }
        for (int i = 0; i < this.userRingtones.size(); i++) {
            if (this.userRingtones.get(i).document != null && this.userRingtones.get(i).document.id == j) {
                return this.userRingtones.get(i).document;
            }
        }
        return null;
    }

    public class CachedTone {
        public TLRPC$Document document;
        public int localId;
        public String localUri;
        public boolean uploading;

        public CachedTone(RingtoneDataStore ringtoneDataStore) {
        }
    }
}
