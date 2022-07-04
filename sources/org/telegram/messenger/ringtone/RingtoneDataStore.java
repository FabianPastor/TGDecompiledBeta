package org.telegram.messenger.ringtone;

import android.content.SharedPreferences;
import android.text.TextUtils;
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
import org.telegram.tgnet.TLRPC;

public class RingtoneDataStore {
    private static volatile long lastReloadTimeMs = 0;
    private static volatile long queryHash = 0;
    private static final long reloadTimeoutMs = 86400000;
    public static final HashSet<String> ringtoneSupportedMimeType = new HashSet<>(Arrays.asList(new String[]{"audio/mpeg3", "audio/mpeg", "audio/ogg", "audio/m4a"}));
    private final long clientUserId;
    private final int currentAccount;
    private boolean loaded;
    private int localIds;
    String prefName = null;
    public final ArrayList<CachedTone> userRingtones = new ArrayList<>();

    public RingtoneDataStore(int currentAccount2) {
        this.currentAccount = currentAccount2;
        this.clientUserId = UserConfig.getInstance(currentAccount2).clientUserId;
        SharedPreferences preferences = getSharedPreferences();
        try {
            queryHash = preferences.getLong("hash", 0);
            lastReloadTimeMs = preferences.getLong("lastReload", 0);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        AndroidUtilities.runOnUIThread(new RingtoneDataStore$$ExternalSyntheticLambda1(this));
    }

    /* renamed from: loadUserRingtones */
    public void m2421lambda$new$0$orgtelegrammessengerringtoneRingtoneDataStore() {
        boolean needReload = System.currentTimeMillis() - lastReloadTimeMs > 86400000;
        TLRPC.TL_account_getSavedRingtones req = new TLRPC.TL_account_getSavedRingtones();
        req.hash = queryHash;
        if (needReload) {
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RingtoneDataStore$$ExternalSyntheticLambda5(this));
            return;
        }
        if (!this.loaded) {
            loadFromPrefs(true);
            this.loaded = true;
        }
        checkRingtoneSoundsLoaded();
    }

    /* renamed from: lambda$loadUserRingtones$2$org-telegram-messenger-ringtone-RingtoneDataStore  reason: not valid java name */
    public /* synthetic */ void m2420x9e2ce1f4(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new RingtoneDataStore$$ExternalSyntheticLambda3(this, response));
    }

    /* renamed from: lambda$loadUserRingtones$1$org-telegram-messenger-ringtone-RingtoneDataStore  reason: not valid java name */
    public /* synthetic */ void m2419x5aa1CLASSNAME(TLObject response) {
        if (response != null) {
            if (response instanceof TLRPC.TL_account_savedRingtonesNotModified) {
                loadFromPrefs(true);
            } else if (response instanceof TLRPC.TL_account_savedRingtones) {
                TLRPC.TL_account_savedRingtones res = (TLRPC.TL_account_savedRingtones) response;
                saveTones(res.ringtones);
                SharedPreferences.Editor edit = getSharedPreferences().edit();
                long j = res.hash;
                queryHash = j;
                SharedPreferences.Editor putLong = edit.putLong("hash", j);
                long currentTimeMillis = System.currentTimeMillis();
                lastReloadTimeMs = currentTimeMillis;
                putLong.putLong("lastReload", currentTimeMillis).apply();
            }
            checkRingtoneSoundsLoaded();
        }
    }

    private void loadFromPrefs(boolean notify) {
        SharedPreferences preferences = getSharedPreferences();
        int count = preferences.getInt("count", 0);
        this.userRingtones.clear();
        for (int i = 0; i < count; i++) {
            String value = preferences.getString("tone_document" + i, "");
            String localPath = preferences.getString("tone_local_path" + i, "");
            SerializedData serializedData = new SerializedData(Utilities.hexToBytes(value));
            try {
                TLRPC.Document document = TLRPC.Document.TLdeserialize(serializedData, serializedData.readInt32(true), true);
                CachedTone tone = new CachedTone();
                tone.document = document;
                tone.localUri = localPath;
                int i2 = this.localIds;
                this.localIds = i2 + 1;
                tone.localId = i2;
                this.userRingtones.add(tone);
            } catch (Throwable e) {
                if (!BuildVars.DEBUG_PRIVATE_VERSION) {
                    FileLog.e(e);
                } else {
                    throw e;
                }
            }
        }
        if (notify) {
            AndroidUtilities.runOnUIThread(new RingtoneDataStore$$ExternalSyntheticLambda0(this));
        }
    }

    /* renamed from: lambda$loadFromPrefs$3$org-telegram-messenger-ringtone-RingtoneDataStore  reason: not valid java name */
    public /* synthetic */ void m2418xdfb099d5() {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.onUserRingtonesUpdated, new Object[0]);
    }

    private void saveTones(ArrayList<TLRPC.Document> ringtones) {
        if (!this.loaded) {
            loadFromPrefs(false);
            this.loaded = true;
        }
        HashMap<Long, String> documentIdToLocalFilePath = new HashMap<>();
        Iterator<CachedTone> it = this.userRingtones.iterator();
        while (it.hasNext()) {
            CachedTone cachedTone = it.next();
            if (!(cachedTone.localUri == null || cachedTone.document == null)) {
                documentIdToLocalFilePath.put(Long.valueOf(cachedTone.document.id), cachedTone.localUri);
            }
        }
        this.userRingtones.clear();
        SharedPreferences preferences = getSharedPreferences();
        preferences.edit().clear().apply();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("count", ringtones.size());
        for (int i = 0; i < ringtones.size(); i++) {
            TLRPC.Document document = ringtones.get(i);
            String localPath = documentIdToLocalFilePath.get(Long.valueOf(document.id));
            SerializedData data = new SerializedData(document.getObjectSize());
            document.serializeToStream(data);
            editor.putString("tone_document" + i, Utilities.bytesToHex(data.toByteArray()));
            if (localPath != null) {
                editor.putString("tone_local_path" + i, localPath);
            }
            CachedTone tone = new CachedTone();
            tone.document = document;
            tone.localUri = localPath;
            int i2 = this.localIds;
            this.localIds = i2 + 1;
            tone.localId = i2;
            this.userRingtones.add(tone);
        }
        editor.apply();
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.onUserRingtonesUpdated, new Object[0]);
    }

    public void saveTones() {
        SharedPreferences preferences = getSharedPreferences();
        preferences.edit().clear().apply();
        SharedPreferences.Editor editor = preferences.edit();
        int count = 0;
        for (int i = 0; i < this.userRingtones.size(); i++) {
            if (!this.userRingtones.get(i).uploading) {
                count++;
                TLRPC.Document document = this.userRingtones.get(i).document;
                String localPath = this.userRingtones.get(i).localUri;
                SerializedData data = new SerializedData(document.getObjectSize());
                document.serializeToStream(data);
                editor.putString("tone_document" + i, Utilities.bytesToHex(data.toByteArray()));
                if (localPath != null) {
                    editor.putString("tone_local_path" + i, localPath);
                }
            }
        }
        editor.putInt("count", count);
        editor.apply();
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.onUserRingtonesUpdated, new Object[0]);
    }

    private SharedPreferences getSharedPreferences() {
        if (this.prefName == null) {
            this.prefName = "ringtones_pref_" + this.clientUserId;
        }
        return ApplicationLoader.applicationContext.getSharedPreferences(this.prefName, 0);
    }

    public void addUploadingTone(String filePath) {
        CachedTone cachedTone = new CachedTone();
        cachedTone.localUri = filePath;
        int i = this.localIds;
        this.localIds = i + 1;
        cachedTone.localId = i;
        cachedTone.uploading = true;
        this.userRingtones.add(cachedTone);
    }

    public void onRingtoneUploaded(String filePath, TLRPC.Document document, boolean error) {
        boolean changed = false;
        if (error) {
            int i = 0;
            while (true) {
                if (i < this.userRingtones.size()) {
                    if (this.userRingtones.get(i).uploading && filePath.equals(this.userRingtones.get(i).localUri)) {
                        this.userRingtones.remove(i);
                        changed = true;
                        break;
                    }
                    i++;
                } else {
                    break;
                }
            }
        } else {
            int i2 = 0;
            while (true) {
                if (i2 < this.userRingtones.size()) {
                    if (this.userRingtones.get(i2).uploading && filePath.equals(this.userRingtones.get(i2).localUri)) {
                        this.userRingtones.get(i2).uploading = false;
                        this.userRingtones.get(i2).document = document;
                        changed = true;
                        break;
                    }
                    i2++;
                } else {
                    break;
                }
            }
            if (changed) {
                saveTones();
            }
        }
        if (changed) {
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.onUserRingtonesUpdated, new Object[0]);
        }
    }

    public String getSoundPath(long id) {
        if (!this.loaded) {
            loadFromPrefs(true);
            this.loaded = true;
        }
        int i = 0;
        while (i < this.userRingtones.size()) {
            if (this.userRingtones.get(i).document == null || this.userRingtones.get(i).document.id != id) {
                i++;
            } else if (!TextUtils.isEmpty(this.userRingtones.get(i).localUri)) {
                return this.userRingtones.get(i).localUri;
            } else {
                return FileLoader.getInstance(this.currentAccount).getPathToAttach(this.userRingtones.get(i).document).toString();
            }
        }
        return "NoSound";
    }

    public void checkRingtoneSoundsLoaded() {
        if (!this.loaded) {
            loadFromPrefs(true);
            this.loaded = true;
        }
        Utilities.globalQueue.postRunnable(new RingtoneDataStore$$ExternalSyntheticLambda2(this, new ArrayList<>(this.userRingtones)));
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:0x002a, code lost:
        r2 = r1.document;
     */
    /* renamed from: lambda$checkRingtoneSoundsLoaded$5$org-telegram-messenger-ringtone-RingtoneDataStore  reason: not valid java name */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void m2417x495026ca(java.util.ArrayList r6) {
        /*
            r5 = this;
            r0 = 0
        L_0x0001:
            int r1 = r6.size()
            if (r0 >= r1) goto L_0x0049
            java.lang.Object r1 = r6.get(r0)
            org.telegram.messenger.ringtone.RingtoneDataStore$CachedTone r1 = (org.telegram.messenger.ringtone.RingtoneDataStore.CachedTone) r1
            if (r1 != 0) goto L_0x0010
            goto L_0x0046
        L_0x0010:
            java.lang.String r2 = r1.localUri
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 != 0) goto L_0x0026
            java.io.File r2 = new java.io.File
            java.lang.String r3 = r1.localUri
            r2.<init>(r3)
            boolean r3 = r2.exists()
            if (r3 == 0) goto L_0x0026
            goto L_0x0046
        L_0x0026:
            org.telegram.tgnet.TLRPC$Document r2 = r1.document
            if (r2 == 0) goto L_0x0046
            org.telegram.tgnet.TLRPC$Document r2 = r1.document
            int r3 = r5.currentAccount
            org.telegram.messenger.FileLoader r3 = org.telegram.messenger.FileLoader.getInstance(r3)
            java.io.File r3 = r3.getPathToAttach(r2)
            if (r3 == 0) goto L_0x003e
            boolean r4 = r3.exists()
            if (r4 != 0) goto L_0x0046
        L_0x003e:
            org.telegram.messenger.ringtone.RingtoneDataStore$$ExternalSyntheticLambda4 r4 = new org.telegram.messenger.ringtone.RingtoneDataStore$$ExternalSyntheticLambda4
            r4.<init>(r5, r2)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r4)
        L_0x0046:
            int r0 = r0 + 1
            goto L_0x0001
        L_0x0049:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ringtone.RingtoneDataStore.m2417x495026ca(java.util.ArrayList):void");
    }

    /* renamed from: lambda$checkRingtoneSoundsLoaded$4$org-telegram-messenger-ringtone-RingtoneDataStore  reason: not valid java name */
    public /* synthetic */ void m2416x5CLASSNAME(TLRPC.Document document) {
        FileLoader.getInstance(this.currentAccount).loadFile(document, document, 0, 0);
    }

    public boolean isLoaded() {
        return this.loaded;
    }

    public void remove(TLRPC.Document document) {
        if (document != null) {
            if (!this.loaded) {
                loadFromPrefs(true);
                this.loaded = true;
            }
            int i = 0;
            while (i < this.userRingtones.size()) {
                if (this.userRingtones.get(i).document == null || this.userRingtones.get(i).document.id != document.id) {
                    i++;
                } else {
                    this.userRingtones.remove(i);
                    return;
                }
            }
        }
    }

    public boolean contains(long id) {
        return getDocument(id) != null;
    }

    public void addTone(TLRPC.Document document) {
        if (document != null && !contains(document.id)) {
            CachedTone cachedTone = new CachedTone();
            cachedTone.document = document;
            int i = this.localIds;
            this.localIds = i + 1;
            cachedTone.localId = i;
            cachedTone.uploading = false;
            this.userRingtones.add(cachedTone);
            saveTones();
        }
    }

    public TLRPC.Document getDocument(long id) {
        if (!this.loaded) {
            loadFromPrefs(true);
            this.loaded = true;
        }
        int i = 0;
        while (i < this.userRingtones.size()) {
            try {
                if (this.userRingtones.get(i) != null && this.userRingtones.get(i).document != null && this.userRingtones.get(i).document.id == id) {
                    return this.userRingtones.get(i).document;
                }
                i++;
            } catch (Exception e) {
                FileLog.e((Throwable) e);
                return null;
            }
        }
        return null;
    }

    public class CachedTone {
        public TLRPC.Document document;
        public int localId;
        public String localUri;
        public boolean uploading;

        public CachedTone() {
        }
    }
}
