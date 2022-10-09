package org.telegram.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.util.Pair;
import android.util.SparseArray;
import androidx.collection.LongSparseArray;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import org.telegram.SQLite.SQLiteCursor;
import org.telegram.SQLite.SQLiteDatabase;
import org.telegram.SQLite.SQLitePreparedStatement;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$Message;
import org.telegram.tgnet.TLRPC$Photo;
import org.telegram.tgnet.TLRPC$PhotoSize;
import org.telegram.tgnet.TLRPC$TL_account_autoDownloadSettings;
import org.telegram.tgnet.TLRPC$TL_account_saveAutoDownloadSettings;
import org.telegram.tgnet.TLRPC$TL_autoDownloadSettings;
import org.telegram.tgnet.TLRPC$TL_error;
/* loaded from: classes.dex */
public class DownloadController extends BaseController implements NotificationCenter.NotificationCenterDelegate {
    public static final int AUTODOWNLOAD_TYPE_AUDIO = 2;
    public static final int AUTODOWNLOAD_TYPE_DOCUMENT = 8;
    public static final int AUTODOWNLOAD_TYPE_PHOTO = 1;
    public static final int AUTODOWNLOAD_TYPE_VIDEO = 4;
    private static volatile DownloadController[] Instance = new DownloadController[4];
    public static final int PRESET_NUM_CHANNEL = 3;
    public static final int PRESET_NUM_CONTACT = 0;
    public static final int PRESET_NUM_GROUP = 2;
    public static final int PRESET_NUM_PM = 1;
    public static final int PRESET_SIZE_NUM_AUDIO = 3;
    public static final int PRESET_SIZE_NUM_DOCUMENT = 2;
    public static final int PRESET_SIZE_NUM_PHOTO = 0;
    public static final int PRESET_SIZE_NUM_VIDEO = 1;
    private HashMap<String, FileDownloadProgressListener> addLaterArray;
    private ArrayList<DownloadObject> audioDownloadQueue;
    Runnable clearUnviewedDownloadsRunnale;
    public int currentMobilePreset;
    public int currentRoamingPreset;
    public int currentWifiPreset;
    private ArrayList<FileDownloadProgressListener> deleteLaterArray;
    private ArrayList<DownloadObject> documentDownloadQueue;
    private HashMap<String, DownloadObject> downloadQueueKeys;
    private HashMap<Pair<Long, Integer>, DownloadObject> downloadQueuePairs;
    public final ArrayList<MessageObject> downloadingFiles;
    public Preset highPreset;
    private int lastCheckMask;
    private int lastTag;
    private boolean listenerInProgress;
    private boolean loadingAutoDownloadConfig;
    private HashMap<String, ArrayList<MessageObject>> loadingFileMessagesObservers;
    private HashMap<String, ArrayList<WeakReference<FileDownloadProgressListener>>> loadingFileObservers;
    public Preset lowPreset;
    public Preset mediumPreset;
    public Preset mobilePreset;
    private SparseArray<String> observersByTag;
    private ArrayList<DownloadObject> photoDownloadQueue;
    public final ArrayList<MessageObject> recentDownloadingFiles;
    public Preset roamingPreset;
    private LongSparseArray<Long> typingTimes;
    public final SparseArray<MessageObject> unviewedDownloads;
    private ArrayList<DownloadObject> videoDownloadQueue;
    public Preset wifiPreset;

    /* loaded from: classes.dex */
    public interface FileDownloadProgressListener {
        int getObserverTag();

        void onFailedDownload(String str, boolean z);

        void onProgressDownload(String str, long j, long j2);

        void onProgressUpload(String str, long j, long j2, boolean z);

        void onSuccessDownload(String str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$savePresetToServer$3(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

    public static int typeToIndex(int i) {
        if (i == 1) {
            return 0;
        }
        if (i == 2) {
            return 2;
        }
        if (i == 4) {
            return 1;
        }
        return i == 8 ? 2 : 0;
    }

    /* loaded from: classes.dex */
    public static class Preset {
        public boolean enabled;
        public boolean lessCallData;
        public int[] mask;
        public int maxVideoBitrate;
        public boolean preloadMusic;
        public boolean preloadVideo;
        public long[] sizes;

        public Preset(int[] iArr, long j, long j2, long j3, boolean z, boolean z2, boolean z3, boolean z4, int i) {
            int[] iArr2 = new int[4];
            this.mask = iArr2;
            this.sizes = new long[4];
            System.arraycopy(iArr, 0, iArr2, 0, iArr2.length);
            long[] jArr = this.sizes;
            jArr[0] = j;
            jArr[1] = j2;
            jArr[2] = j3;
            jArr[3] = 524288;
            this.preloadVideo = z;
            this.preloadMusic = z2;
            this.lessCallData = z4;
            this.maxVideoBitrate = i;
            this.enabled = z3;
        }

        public Preset(String str, String str2) {
            String[] split;
            this.mask = new int[4];
            this.sizes = new long[4];
            String[] split2 = str.split("_");
            if (split2.length >= 11) {
                boolean z = false;
                this.mask[0] = Utilities.parseInt((CharSequence) split2[0]).intValue();
                this.mask[1] = Utilities.parseInt((CharSequence) split2[1]).intValue();
                this.mask[2] = Utilities.parseInt((CharSequence) split2[2]).intValue();
                this.mask[3] = Utilities.parseInt((CharSequence) split2[3]).intValue();
                this.sizes[0] = Utilities.parseInt((CharSequence) split2[4]).intValue();
                this.sizes[1] = Utilities.parseInt((CharSequence) split2[5]).intValue();
                this.sizes[2] = Utilities.parseInt((CharSequence) split2[6]).intValue();
                this.sizes[3] = Utilities.parseInt((CharSequence) split2[7]).intValue();
                this.preloadVideo = Utilities.parseInt((CharSequence) split2[8]).intValue() == 1;
                this.preloadMusic = Utilities.parseInt((CharSequence) split2[9]).intValue() == 1;
                this.enabled = Utilities.parseInt((CharSequence) split2[10]).intValue() == 1;
                if (split2.length >= 12) {
                    this.lessCallData = Utilities.parseInt((CharSequence) split2[11]).intValue() == 1 ? true : z;
                    split = null;
                } else {
                    split = str2.split("_");
                    this.lessCallData = Utilities.parseInt((CharSequence) split[11]).intValue() == 1 ? true : z;
                }
                if (split2.length >= 13) {
                    this.maxVideoBitrate = Utilities.parseInt((CharSequence) split2[12]).intValue();
                } else {
                    this.maxVideoBitrate = Utilities.parseInt((CharSequence) (split == null ? str2.split("_") : split)[12]).intValue();
                }
            }
        }

        public void set(Preset preset) {
            int[] iArr = preset.mask;
            int[] iArr2 = this.mask;
            System.arraycopy(iArr, 0, iArr2, 0, iArr2.length);
            long[] jArr = preset.sizes;
            long[] jArr2 = this.sizes;
            System.arraycopy(jArr, 0, jArr2, 0, jArr2.length);
            this.preloadVideo = preset.preloadVideo;
            this.preloadMusic = preset.preloadMusic;
            this.lessCallData = preset.lessCallData;
            this.maxVideoBitrate = preset.maxVideoBitrate;
        }

        public void set(TLRPC$TL_autoDownloadSettings tLRPC$TL_autoDownloadSettings) {
            this.preloadMusic = tLRPC$TL_autoDownloadSettings.audio_preload_next;
            this.preloadVideo = tLRPC$TL_autoDownloadSettings.video_preload_large;
            this.lessCallData = tLRPC$TL_autoDownloadSettings.phonecalls_less_data;
            this.maxVideoBitrate = tLRPC$TL_autoDownloadSettings.video_upload_maxbitrate;
            int i = 0;
            this.sizes[0] = Math.max(512000, tLRPC$TL_autoDownloadSettings.photo_size_max);
            this.sizes[1] = Math.max(512000L, tLRPC$TL_autoDownloadSettings.video_size_max);
            this.sizes[2] = Math.max(512000L, tLRPC$TL_autoDownloadSettings.file_size_max);
            while (true) {
                int[] iArr = this.mask;
                if (i < iArr.length) {
                    if (tLRPC$TL_autoDownloadSettings.photo_size_max != 0 && !tLRPC$TL_autoDownloadSettings.disabled) {
                        iArr[i] = iArr[i] | 1;
                    } else {
                        iArr[i] = iArr[i] & (-2);
                    }
                    if (tLRPC$TL_autoDownloadSettings.video_size_max != 0 && !tLRPC$TL_autoDownloadSettings.disabled) {
                        iArr[i] = iArr[i] | 4;
                    } else {
                        iArr[i] = iArr[i] & (-5);
                    }
                    if (tLRPC$TL_autoDownloadSettings.file_size_max != 0 && !tLRPC$TL_autoDownloadSettings.disabled) {
                        iArr[i] = iArr[i] | 8;
                    } else {
                        iArr[i] = iArr[i] & (-9);
                    }
                    i++;
                } else {
                    return;
                }
            }
        }

        public String toString() {
            return this.mask[0] + "_" + this.mask[1] + "_" + this.mask[2] + "_" + this.mask[3] + "_" + this.sizes[0] + "_" + this.sizes[1] + "_" + this.sizes[2] + "_" + this.sizes[3] + "_" + (this.preloadVideo ? 1 : 0) + "_" + (this.preloadMusic ? 1 : 0) + "_" + (this.enabled ? 1 : 0) + "_" + (this.lessCallData ? 1 : 0) + "_" + this.maxVideoBitrate;
        }

        public boolean equals(Preset preset) {
            int[] iArr = this.mask;
            int i = iArr[0];
            int[] iArr2 = preset.mask;
            if (i == iArr2[0] && iArr[1] == iArr2[1] && iArr[2] == iArr2[2] && iArr[3] == iArr2[3]) {
                long[] jArr = this.sizes;
                long j = jArr[0];
                long[] jArr2 = preset.sizes;
                return j == jArr2[0] && jArr[1] == jArr2[1] && jArr[2] == jArr2[2] && jArr[3] == jArr2[3] && this.preloadVideo == preset.preloadVideo && this.preloadMusic == preset.preloadMusic && this.maxVideoBitrate == preset.maxVideoBitrate;
            }
            return false;
        }

        public boolean isEnabled() {
            int i = 0;
            while (true) {
                int[] iArr = this.mask;
                if (i < iArr.length) {
                    if (iArr[i] != 0) {
                        return true;
                    }
                    i++;
                } else {
                    return false;
                }
            }
        }
    }

    public static DownloadController getInstance(int i) {
        DownloadController downloadController = Instance[i];
        if (downloadController == null) {
            synchronized (DownloadController.class) {
                downloadController = Instance[i];
                if (downloadController == null) {
                    DownloadController[] downloadControllerArr = Instance;
                    DownloadController downloadController2 = new DownloadController(i);
                    downloadControllerArr[i] = downloadController2;
                    downloadController = downloadController2;
                }
            }
        }
        return downloadController;
    }

    public DownloadController(int i) {
        super(i);
        Object obj;
        this.lastCheckMask = 0;
        this.photoDownloadQueue = new ArrayList<>();
        this.audioDownloadQueue = new ArrayList<>();
        this.documentDownloadQueue = new ArrayList<>();
        this.videoDownloadQueue = new ArrayList<>();
        this.downloadQueueKeys = new HashMap<>();
        this.downloadQueuePairs = new HashMap<>();
        this.loadingFileObservers = new HashMap<>();
        this.loadingFileMessagesObservers = new HashMap<>();
        this.observersByTag = new SparseArray<>();
        this.listenerInProgress = false;
        this.addLaterArray = new HashMap<>();
        this.deleteLaterArray = new ArrayList<>();
        this.lastTag = 0;
        this.typingTimes = new LongSparseArray<>();
        this.downloadingFiles = new ArrayList<>();
        this.recentDownloadingFiles = new ArrayList<>();
        this.unviewedDownloads = new SparseArray<>();
        this.clearUnviewedDownloadsRunnale = new Runnable() { // from class: org.telegram.messenger.DownloadController.2
            @Override // java.lang.Runnable
            public void run() {
                DownloadController.this.clearUnviewedDownloads();
                DownloadController.this.getNotificationCenter().postNotificationName(NotificationCenter.onDownloadingFilesChanged, new Object[0]);
            }
        };
        SharedPreferences mainSettings = MessagesController.getMainSettings(this.currentAccount);
        this.lowPreset = new Preset(mainSettings.getString("preset0", "1_1_1_1_1048576_512000_512000_524288_0_0_1_1_50"), "1_1_1_1_1048576_512000_512000_524288_0_0_1_1_50");
        this.mediumPreset = new Preset(mainSettings.getString("preset1", "13_13_13_13_1048576_10485760_1048576_524288_1_1_1_0_100"), "13_13_13_13_1048576_10485760_1048576_524288_1_1_1_0_100");
        this.highPreset = new Preset(mainSettings.getString("preset2", "13_13_13_13_1048576_15728640_3145728_524288_1_1_1_0_100"), "13_13_13_13_1048576_15728640_3145728_524288_1_1_1_0_100");
        boolean contains = mainSettings.contains("newConfig");
        String str = "currentWifiPreset";
        if (contains || !getUserConfig().isClientActivated()) {
            this.mobilePreset = new Preset(mainSettings.getString("mobilePreset", "13_13_13_13_1048576_10485760_1048576_524288_1_1_1_0_100"), "13_13_13_13_1048576_10485760_1048576_524288_1_1_1_0_100");
            this.wifiPreset = new Preset(mainSettings.getString("wifiPreset", "13_13_13_13_1048576_15728640_3145728_524288_1_1_1_0_100"), "13_13_13_13_1048576_15728640_3145728_524288_1_1_1_0_100");
            this.roamingPreset = new Preset(mainSettings.getString("roamingPreset", "1_1_1_1_1048576_512000_512000_524288_0_0_1_1_50"), "1_1_1_1_1048576_512000_512000_524288_0_0_1_1_50");
            this.currentMobilePreset = mainSettings.getInt("currentMobilePreset", 3);
            this.currentWifiPreset = mainSettings.getInt(str, 3);
            this.currentRoamingPreset = mainSettings.getInt("currentRoamingPreset", 3);
            if (!contains) {
                mainSettings.edit().putBoolean("newConfig", true).commit();
            }
        } else {
            int[] iArr = new int[4];
            int[] iArr2 = new int[4];
            int[] iArr3 = new int[4];
            long[] jArr = new long[7];
            long[] jArr2 = new long[7];
            long[] jArr3 = new long[7];
            int i2 = 0;
            for (int i3 = 4; i2 < i3; i3 = 4) {
                StringBuilder sb = new StringBuilder();
                String str2 = str;
                sb.append("mobileDataDownloadMask");
                Object obj2 = "";
                if (i2 == 0) {
                    obj = obj2;
                } else {
                    obj = obj2;
                    obj2 = Integer.valueOf(i2);
                }
                sb.append(obj2);
                String sb2 = sb.toString();
                if (i2 == 0 || mainSettings.contains(sb2)) {
                    iArr[i2] = mainSettings.getInt(sb2, 13);
                    StringBuilder sb3 = new StringBuilder();
                    sb3.append("wifiDownloadMask");
                    sb3.append(i2 == 0 ? obj : Integer.valueOf(i2));
                    iArr2[i2] = mainSettings.getInt(sb3.toString(), 13);
                    StringBuilder sb4 = new StringBuilder();
                    sb4.append("roamingDownloadMask");
                    sb4.append(i2 == 0 ? obj : Integer.valueOf(i2));
                    iArr3[i2] = mainSettings.getInt(sb4.toString(), 1);
                } else {
                    iArr[i2] = iArr[0];
                    iArr2[i2] = iArr2[0];
                    iArr3[i2] = iArr3[0];
                }
                i2++;
                str = str2;
            }
            jArr[2] = mainSettings.getLong("mobileMaxDownloadSize2", this.mediumPreset.sizes[1]);
            jArr[3] = mainSettings.getLong("mobileMaxDownloadSize3", this.mediumPreset.sizes[2]);
            jArr2[2] = mainSettings.getLong("wifiMaxDownloadSize2", this.highPreset.sizes[1]);
            jArr2[3] = mainSettings.getLong("wifiMaxDownloadSize3", this.highPreset.sizes[2]);
            jArr3[2] = mainSettings.getLong("roamingMaxDownloadSize2", this.lowPreset.sizes[1]);
            jArr3[3] = mainSettings.getLong("roamingMaxDownloadSize3", this.lowPreset.sizes[2]);
            boolean z = mainSettings.getBoolean("globalAutodownloadEnabled", true);
            this.mobilePreset = new Preset(iArr, this.mediumPreset.sizes[0], jArr[2], jArr[3], true, true, z, false, 100);
            this.wifiPreset = new Preset(iArr2, this.highPreset.sizes[0], jArr2[2], jArr2[3], true, true, z, false, 100);
            this.roamingPreset = new Preset(iArr3, this.lowPreset.sizes[0], jArr3[2], jArr3[3], false, false, z, true, 50);
            SharedPreferences.Editor edit = mainSettings.edit();
            edit.putBoolean("newConfig", true);
            edit.putString("mobilePreset", this.mobilePreset.toString());
            edit.putString("wifiPreset", this.wifiPreset.toString());
            edit.putString("roamingPreset", this.roamingPreset.toString());
            this.currentMobilePreset = 3;
            edit.putInt("currentMobilePreset", 3);
            this.currentWifiPreset = 3;
            edit.putInt(str, 3);
            this.currentRoamingPreset = 3;
            edit.putInt("currentRoamingPreset", 3);
            edit.commit();
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.DownloadController$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                DownloadController.this.lambda$new$0();
            }
        });
        ApplicationLoader.applicationContext.registerReceiver(new BroadcastReceiver() { // from class: org.telegram.messenger.DownloadController.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                DownloadController.this.checkAutodownloadSettings();
            }
        }, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        if (getUserConfig().isClientActivated()) {
            checkAutodownloadSettings();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        getNotificationCenter().addObserver(this, NotificationCenter.fileLoadFailed);
        getNotificationCenter().addObserver(this, NotificationCenter.fileLoaded);
        getNotificationCenter().addObserver(this, NotificationCenter.fileLoadProgressChanged);
        getNotificationCenter().addObserver(this, NotificationCenter.fileUploadProgressChanged);
        getNotificationCenter().addObserver(this, NotificationCenter.httpFileDidLoad);
        getNotificationCenter().addObserver(this, NotificationCenter.httpFileDidFailedLoad);
        loadAutoDownloadConfig(false);
    }

    public void loadAutoDownloadConfig(boolean z) {
        if (!this.loadingAutoDownloadConfig) {
            if (!z && Math.abs(System.currentTimeMillis() - getUserConfig().autoDownloadConfigLoadTime) < 86400000) {
                return;
            }
            this.loadingAutoDownloadConfig = true;
            getConnectionsManager().sendRequest(new TLObject() { // from class: org.telegram.tgnet.TLRPC$TL_account_getAutoDownloadSettings
                public static int constructor = NUM;

                @Override // org.telegram.tgnet.TLObject
                public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z2) {
                    return TLRPC$TL_account_autoDownloadSettings.TLdeserialize(abstractSerializedData, i, z2);
                }

                @Override // org.telegram.tgnet.TLObject
                public void serializeToStream(AbstractSerializedData abstractSerializedData) {
                    abstractSerializedData.writeInt32(constructor);
                }
            }, new RequestDelegate() { // from class: org.telegram.messenger.DownloadController$$ExternalSyntheticLambda12
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    DownloadController.this.lambda$loadAutoDownloadConfig$2(tLObject, tLRPC$TL_error);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$loadAutoDownloadConfig$2(final TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.DownloadController$$ExternalSyntheticLambda10
            @Override // java.lang.Runnable
            public final void run() {
                DownloadController.this.lambda$loadAutoDownloadConfig$1(tLObject);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$loadAutoDownloadConfig$1(TLObject tLObject) {
        Preset preset;
        this.loadingAutoDownloadConfig = false;
        getUserConfig().autoDownloadConfigLoadTime = System.currentTimeMillis();
        getUserConfig().saveConfig(false);
        if (tLObject != null) {
            TLRPC$TL_account_autoDownloadSettings tLRPC$TL_account_autoDownloadSettings = (TLRPC$TL_account_autoDownloadSettings) tLObject;
            this.lowPreset.set(tLRPC$TL_account_autoDownloadSettings.low);
            this.mediumPreset.set(tLRPC$TL_account_autoDownloadSettings.medium);
            this.highPreset.set(tLRPC$TL_account_autoDownloadSettings.high);
            for (int i = 0; i < 3; i++) {
                if (i == 0) {
                    preset = this.mobilePreset;
                } else if (i == 1) {
                    preset = this.wifiPreset;
                } else {
                    preset = this.roamingPreset;
                }
                if (preset.equals(this.lowPreset)) {
                    preset.set(tLRPC$TL_account_autoDownloadSettings.low);
                } else if (preset.equals(this.mediumPreset)) {
                    preset.set(tLRPC$TL_account_autoDownloadSettings.medium);
                } else if (preset.equals(this.highPreset)) {
                    preset.set(tLRPC$TL_account_autoDownloadSettings.high);
                }
            }
            SharedPreferences.Editor edit = MessagesController.getMainSettings(this.currentAccount).edit();
            edit.putString("mobilePreset", this.mobilePreset.toString());
            edit.putString("wifiPreset", this.wifiPreset.toString());
            edit.putString("roamingPreset", this.roamingPreset.toString());
            edit.putString("preset0", this.lowPreset.toString());
            edit.putString("preset1", this.mediumPreset.toString());
            edit.putString("preset2", this.highPreset.toString());
            edit.commit();
            this.lowPreset.toString();
            this.mediumPreset.toString();
            this.highPreset.toString();
            checkAutodownloadSettings();
        }
    }

    public Preset getCurrentMobilePreset() {
        int i = this.currentMobilePreset;
        if (i == 0) {
            return this.lowPreset;
        }
        if (i == 1) {
            return this.mediumPreset;
        }
        if (i == 2) {
            return this.highPreset;
        }
        return this.mobilePreset;
    }

    public Preset getCurrentWiFiPreset() {
        int i = this.currentWifiPreset;
        if (i == 0) {
            return this.lowPreset;
        }
        if (i == 1) {
            return this.mediumPreset;
        }
        if (i == 2) {
            return this.highPreset;
        }
        return this.wifiPreset;
    }

    public Preset getCurrentRoamingPreset() {
        int i = this.currentRoamingPreset;
        if (i == 0) {
            return this.lowPreset;
        }
        if (i == 1) {
            return this.mediumPreset;
        }
        if (i == 2) {
            return this.highPreset;
        }
        return this.roamingPreset;
    }

    public void cleanup() {
        this.photoDownloadQueue.clear();
        this.audioDownloadQueue.clear();
        this.documentDownloadQueue.clear();
        this.videoDownloadQueue.clear();
        this.downloadQueueKeys.clear();
        this.downloadQueuePairs.clear();
        this.typingTimes.clear();
    }

    public int getMaxVideoBitrate() {
        int autodownloadNetworkType = ApplicationLoader.getAutodownloadNetworkType();
        if (autodownloadNetworkType == 1) {
            return getCurrentWiFiPreset().maxVideoBitrate;
        }
        if (autodownloadNetworkType == 2) {
            return getCurrentRoamingPreset().maxVideoBitrate;
        }
        return getCurrentMobilePreset().maxVideoBitrate;
    }

    public int getAutodownloadMask() {
        int[] iArr;
        int autodownloadNetworkType = ApplicationLoader.getAutodownloadNetworkType();
        if (autodownloadNetworkType == 1) {
            if (!this.wifiPreset.enabled) {
                return 0;
            }
            iArr = getCurrentWiFiPreset().mask;
        } else if (autodownloadNetworkType == 2) {
            if (!this.roamingPreset.enabled) {
                return 0;
            }
            iArr = getCurrentRoamingPreset().mask;
        } else if (!this.mobilePreset.enabled) {
            return 0;
        } else {
            iArr = getCurrentMobilePreset().mask;
        }
        int i = 0;
        for (int i2 = 0; i2 < iArr.length; i2++) {
            int i3 = (iArr[i2] & 1) != 0 ? 1 : 0;
            if ((iArr[i2] & 2) != 0) {
                i3 |= 2;
            }
            if ((iArr[i2] & 4) != 0) {
                i3 |= 4;
            }
            if ((iArr[i2] & 8) != 0) {
                i3 |= 8;
            }
            i |= i3 << (i2 * 8);
        }
        return i;
    }

    protected int getAutodownloadMaskAll() {
        if (this.mobilePreset.enabled || this.roamingPreset.enabled || this.wifiPreset.enabled) {
            int i = 0;
            for (int i2 = 0; i2 < 4; i2++) {
                if ((getCurrentMobilePreset().mask[i2] & 1) != 0 || (getCurrentWiFiPreset().mask[i2] & 1) != 0 || (getCurrentRoamingPreset().mask[i2] & 1) != 0) {
                    i |= 1;
                }
                if ((getCurrentMobilePreset().mask[i2] & 2) != 0 || (getCurrentWiFiPreset().mask[i2] & 2) != 0 || (getCurrentRoamingPreset().mask[i2] & 2) != 0) {
                    i |= 2;
                }
                if ((getCurrentMobilePreset().mask[i2] & 4) != 0 || (getCurrentWiFiPreset().mask[i2] & 4) != 0 || (4 & getCurrentRoamingPreset().mask[i2]) != 0) {
                    i |= 4;
                }
                if ((getCurrentMobilePreset().mask[i2] & 8) != 0 || (getCurrentWiFiPreset().mask[i2] & 8) != 0 || (getCurrentRoamingPreset().mask[i2] & 8) != 0) {
                    i |= 8;
                }
            }
            return i;
        }
        return 0;
    }

    public void checkAutodownloadSettings() {
        int currentDownloadMask = getCurrentDownloadMask();
        if (currentDownloadMask == this.lastCheckMask) {
            return;
        }
        this.lastCheckMask = currentDownloadMask;
        if ((currentDownloadMask & 1) != 0) {
            if (this.photoDownloadQueue.isEmpty()) {
                newDownloadObjectsAvailable(1);
            }
        } else {
            for (int i = 0; i < this.photoDownloadQueue.size(); i++) {
                DownloadObject downloadObject = this.photoDownloadQueue.get(i);
                TLObject tLObject = downloadObject.object;
                if (tLObject instanceof TLRPC$Photo) {
                    getFileLoader().cancelLoadFile(FileLoader.getClosestPhotoSizeWithSize(((TLRPC$Photo) tLObject).sizes, AndroidUtilities.getPhotoSize()));
                } else if (tLObject instanceof TLRPC$Document) {
                    getFileLoader().cancelLoadFile((TLRPC$Document) downloadObject.object);
                }
            }
            this.photoDownloadQueue.clear();
        }
        if ((currentDownloadMask & 2) != 0) {
            if (this.audioDownloadQueue.isEmpty()) {
                newDownloadObjectsAvailable(2);
            }
        } else {
            for (int i2 = 0; i2 < this.audioDownloadQueue.size(); i2++) {
                getFileLoader().cancelLoadFile((TLRPC$Document) this.audioDownloadQueue.get(i2).object);
            }
            this.audioDownloadQueue.clear();
        }
        if ((currentDownloadMask & 8) != 0) {
            if (this.documentDownloadQueue.isEmpty()) {
                newDownloadObjectsAvailable(8);
            }
        } else {
            for (int i3 = 0; i3 < this.documentDownloadQueue.size(); i3++) {
                getFileLoader().cancelLoadFile((TLRPC$Document) this.documentDownloadQueue.get(i3).object);
            }
            this.documentDownloadQueue.clear();
        }
        if ((currentDownloadMask & 4) != 0) {
            if (this.videoDownloadQueue.isEmpty()) {
                newDownloadObjectsAvailable(4);
            }
        } else {
            for (int i4 = 0; i4 < this.videoDownloadQueue.size(); i4++) {
                getFileLoader().cancelLoadFile((TLRPC$Document) this.videoDownloadQueue.get(i4).object);
            }
            this.videoDownloadQueue.clear();
        }
        int autodownloadMaskAll = getAutodownloadMaskAll();
        if (autodownloadMaskAll == 0) {
            getMessagesStorage().clearDownloadQueue(0);
            return;
        }
        if ((autodownloadMaskAll & 1) == 0) {
            getMessagesStorage().clearDownloadQueue(1);
        }
        if ((autodownloadMaskAll & 2) == 0) {
            getMessagesStorage().clearDownloadQueue(2);
        }
        if ((autodownloadMaskAll & 4) == 0) {
            getMessagesStorage().clearDownloadQueue(4);
        }
        if ((autodownloadMaskAll & 8) != 0) {
            return;
        }
        getMessagesStorage().clearDownloadQueue(8);
    }

    public boolean canDownloadMedia(MessageObject messageObject) {
        return canDownloadMedia(messageObject.messageOwner) == 1;
    }

    public boolean canDownloadMedia(int i, long j) {
        Preset currentMobilePreset;
        int autodownloadNetworkType = ApplicationLoader.getAutodownloadNetworkType();
        if (autodownloadNetworkType == 1) {
            if (!this.wifiPreset.enabled) {
                return false;
            }
            currentMobilePreset = getCurrentWiFiPreset();
        } else if (autodownloadNetworkType == 2) {
            if (!this.roamingPreset.enabled) {
                return false;
            }
            currentMobilePreset = getCurrentRoamingPreset();
        } else if (!this.mobilePreset.enabled) {
            return false;
        } else {
            currentMobilePreset = getCurrentMobilePreset();
        }
        int i2 = currentMobilePreset.mask[1];
        long j2 = currentMobilePreset.sizes[typeToIndex(i)];
        if (i == 1 || (j != 0 && j <= j2)) {
            return i == 2 || (i & i2) != 0;
        }
        return false;
    }

    /* JADX WARN: Code restructure failed: missing block: B:35:0x0067, code lost:
        if (getContactsController().contactsDict.containsKey(java.lang.Long.valueOf(r7.user_id)) != false) goto L22;
     */
    /* JADX WARN: Code restructure failed: missing block: B:42:0x0089, code lost:
        if (getContactsController().contactsDict.containsKey(java.lang.Long.valueOf(r18.from_id.user_id)) != false) goto L22;
     */
    /* JADX WARN: Code restructure failed: missing block: B:56:0x00c8, code lost:
        if (getContactsController().contactsDict.containsKey(java.lang.Long.valueOf(r18.from_id.user_id)) != false) goto L22;
     */
    /* JADX WARN: Removed duplicated region for block: B:62:0x00d4  */
    /* JADX WARN: Removed duplicated region for block: B:66:0x00e0  */
    /* JADX WARN: Removed duplicated region for block: B:77:0x00ff  */
    /* JADX WARN: Removed duplicated region for block: B:78:0x010f  */
    /* JADX WARN: Removed duplicated region for block: B:81:0x011e  */
    /* JADX WARN: Removed duplicated region for block: B:92:0x0135  */
    /* JADX WARN: Removed duplicated region for block: B:97:0x013f  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public int canDownloadMedia(org.telegram.tgnet.TLRPC$Message r18) {
        /*
            Method dump skipped, instructions count: 325
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.DownloadController.canDownloadMedia(org.telegram.tgnet.TLRPC$Message):int");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean canDownloadNextTrack() {
        int autodownloadNetworkType = ApplicationLoader.getAutodownloadNetworkType();
        return autodownloadNetworkType == 1 ? this.wifiPreset.enabled && getCurrentWiFiPreset().preloadMusic : autodownloadNetworkType == 2 ? this.roamingPreset.enabled && getCurrentRoamingPreset().preloadMusic : this.mobilePreset.enabled && getCurrentMobilePreset().preloadMusic;
    }

    public int getCurrentDownloadMask() {
        int autodownloadNetworkType = ApplicationLoader.getAutodownloadNetworkType();
        int i = 0;
        if (autodownloadNetworkType == 1) {
            if (!this.wifiPreset.enabled) {
                return 0;
            }
            int i2 = 0;
            while (i < 4) {
                i2 |= getCurrentWiFiPreset().mask[i];
                i++;
            }
            return i2;
        } else if (autodownloadNetworkType == 2) {
            if (!this.roamingPreset.enabled) {
                return 0;
            }
            int i3 = 0;
            while (i < 4) {
                i3 |= getCurrentRoamingPreset().mask[i];
                i++;
            }
            return i3;
        } else if (!this.mobilePreset.enabled) {
            return 0;
        } else {
            int i4 = 0;
            while (i < 4) {
                i4 |= getCurrentMobilePreset().mask[i];
                i++;
            }
            return i4;
        }
    }

    public void savePresetToServer(int i) {
        Preset currentRoamingPreset;
        boolean z;
        TLRPC$TL_account_saveAutoDownloadSettings tLRPC$TL_account_saveAutoDownloadSettings = new TLRPC$TL_account_saveAutoDownloadSettings();
        if (i == 0) {
            currentRoamingPreset = getCurrentMobilePreset();
            z = this.mobilePreset.enabled;
        } else if (i == 1) {
            currentRoamingPreset = getCurrentWiFiPreset();
            z = this.wifiPreset.enabled;
        } else {
            currentRoamingPreset = getCurrentRoamingPreset();
            z = this.roamingPreset.enabled;
        }
        TLRPC$TL_autoDownloadSettings tLRPC$TL_autoDownloadSettings = new TLRPC$TL_autoDownloadSettings();
        tLRPC$TL_account_saveAutoDownloadSettings.settings = tLRPC$TL_autoDownloadSettings;
        tLRPC$TL_autoDownloadSettings.audio_preload_next = currentRoamingPreset.preloadMusic;
        tLRPC$TL_autoDownloadSettings.video_preload_large = currentRoamingPreset.preloadVideo;
        tLRPC$TL_autoDownloadSettings.phonecalls_less_data = currentRoamingPreset.lessCallData;
        tLRPC$TL_autoDownloadSettings.video_upload_maxbitrate = currentRoamingPreset.maxVideoBitrate;
        tLRPC$TL_autoDownloadSettings.disabled = !z;
        int i2 = 0;
        int i3 = 0;
        boolean z2 = false;
        boolean z3 = false;
        boolean z4 = false;
        while (true) {
            int[] iArr = currentRoamingPreset.mask;
            if (i3 < iArr.length) {
                if ((iArr[i3] & 1) != 0) {
                    z2 = true;
                }
                if ((iArr[i3] & 4) != 0) {
                    z3 = true;
                }
                if ((iArr[i3] & 8) != 0) {
                    z4 = true;
                }
                if (z2 && z3 && z4) {
                    break;
                }
                i3++;
            } else {
                break;
            }
        }
        TLRPC$TL_autoDownloadSettings tLRPC$TL_autoDownloadSettings2 = tLRPC$TL_account_saveAutoDownloadSettings.settings;
        if (z2) {
            i2 = (int) currentRoamingPreset.sizes[0];
        }
        tLRPC$TL_autoDownloadSettings2.photo_size_max = i2;
        long j = 0;
        tLRPC$TL_autoDownloadSettings2.video_size_max = z3 ? currentRoamingPreset.sizes[1] : 0L;
        if (z4) {
            j = currentRoamingPreset.sizes[2];
        }
        tLRPC$TL_autoDownloadSettings2.file_size_max = j;
        getConnectionsManager().sendRequest(tLRPC$TL_account_saveAutoDownloadSettings, DownloadController$$ExternalSyntheticLambda13.INSTANCE);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void cancelDownloading(ArrayList<Pair<Long, Integer>> arrayList) {
        TLRPC$PhotoSize closestPhotoSizeWithSize;
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            DownloadObject downloadObject = this.downloadQueuePairs.get(arrayList.get(i));
            if (downloadObject != null) {
                TLObject tLObject = downloadObject.object;
                if (tLObject instanceof TLRPC$Document) {
                    getFileLoader().cancelLoadFile((TLRPC$Document) tLObject, true);
                } else if ((tLObject instanceof TLRPC$Photo) && (closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(((TLRPC$Photo) tLObject).sizes, AndroidUtilities.getPhotoSize())) != null) {
                    getFileLoader().cancelLoadFile(closestPhotoSizeWithSize, true);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:30:0x0065  */
    /* JADX WARN: Removed duplicated region for block: B:38:0x008a  */
    /* JADX WARN: Removed duplicated region for block: B:48:0x00a7  */
    /* JADX WARN: Removed duplicated region for block: B:54:0x00c5 A[SYNTHETIC] */
    /* JADX WARN: Type inference failed for: r8v16, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r8v4, types: [java.lang.String] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void processDownloadObjects(int r20, java.util.ArrayList<org.telegram.messenger.DownloadObject> r21) {
        /*
            r19 = this;
            r0 = r19
            r1 = r20
            boolean r2 = r21.isEmpty()
            if (r2 == 0) goto Lb
            return
        Lb:
            r2 = 2
            r3 = 1
            if (r1 != r3) goto L12
            java.util.ArrayList<org.telegram.messenger.DownloadObject> r1 = r0.photoDownloadQueue
            goto L1f
        L12:
            if (r1 != r2) goto L17
            java.util.ArrayList<org.telegram.messenger.DownloadObject> r1 = r0.audioDownloadQueue
            goto L1f
        L17:
            r4 = 4
            if (r1 != r4) goto L1d
            java.util.ArrayList<org.telegram.messenger.DownloadObject> r1 = r0.videoDownloadQueue
            goto L1f
        L1d:
            java.util.ArrayList<org.telegram.messenger.DownloadObject> r1 = r0.documentDownloadQueue
        L1f:
            r4 = 0
            r5 = 0
        L21:
            int r6 = r21.size()
            if (r5 >= r6) goto Lc9
            r6 = r21
            java.lang.Object r7 = r6.get(r5)
            org.telegram.messenger.DownloadObject r7 = (org.telegram.messenger.DownloadObject) r7
            org.telegram.tgnet.TLObject r8 = r7.object
            boolean r9 = r8 instanceof org.telegram.tgnet.TLRPC$Document
            r10 = 0
            if (r9 == 0) goto L42
            org.telegram.tgnet.TLRPC$Document r8 = (org.telegram.tgnet.TLRPC$Document) r8
            java.lang.String r8 = org.telegram.messenger.FileLoader.getAttachFileName(r8)
        L3c:
            r18 = r10
            r10 = r8
            r8 = r18
            goto L58
        L42:
            boolean r9 = r8 instanceof org.telegram.tgnet.TLRPC$Photo
            if (r9 == 0) goto L57
            org.telegram.tgnet.TLRPC$Photo r8 = (org.telegram.tgnet.TLRPC$Photo) r8
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r8 = r8.sizes
            int r9 = org.telegram.messenger.AndroidUtilities.getPhotoSize()
            org.telegram.tgnet.TLRPC$PhotoSize r10 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r8, r9)
            java.lang.String r8 = org.telegram.messenger.FileLoader.getAttachFileName(r10)
            goto L3c
        L57:
            r8 = r10
        L58:
            if (r10 == 0) goto Lc5
            java.util.HashMap<java.lang.String, org.telegram.messenger.DownloadObject> r9 = r0.downloadQueueKeys
            boolean r9 = r9.containsKey(r10)
            if (r9 == 0) goto L63
            goto Lc5
        L63:
            if (r8 == 0) goto L8a
            org.telegram.tgnet.TLObject r9 = r7.object
            org.telegram.tgnet.TLRPC$Photo r9 = (org.telegram.tgnet.TLRPC$Photo) r9
            boolean r11 = r7.secret
            if (r11 == 0) goto L70
            r17 = 2
            goto L79
        L70:
            boolean r11 = r7.forceCache
            if (r11 == 0) goto L77
            r17 = 1
            goto L79
        L77:
            r17 = 0
        L79:
            org.telegram.messenger.FileLoader r12 = r19.getFileLoader()
            org.telegram.messenger.ImageLocation r13 = org.telegram.messenger.ImageLocation.getForPhoto(r8, r9)
            java.lang.String r14 = r7.parent
            r15 = 0
            r16 = 0
            r12.loadFile(r13, r14, r15, r16, r17)
            goto La2
        L8a:
            org.telegram.tgnet.TLObject r8 = r7.object
            boolean r9 = r8 instanceof org.telegram.tgnet.TLRPC$Document
            if (r9 == 0) goto La4
            org.telegram.tgnet.TLRPC$Document r8 = (org.telegram.tgnet.TLRPC$Document) r8
            org.telegram.messenger.FileLoader r9 = r19.getFileLoader()
            java.lang.String r11 = r7.parent
            boolean r12 = r7.secret
            if (r12 == 0) goto L9e
            r12 = 2
            goto L9f
        L9e:
            r12 = 0
        L9f:
            r9.loadFile(r8, r11, r4, r12)
        La2:
            r8 = 1
            goto La5
        La4:
            r8 = 0
        La5:
            if (r8 == 0) goto Lc5
            r1.add(r7)
            java.util.HashMap<java.lang.String, org.telegram.messenger.DownloadObject> r8 = r0.downloadQueueKeys
            r8.put(r10, r7)
            java.util.HashMap<android.util.Pair<java.lang.Long, java.lang.Integer>, org.telegram.messenger.DownloadObject> r8 = r0.downloadQueuePairs
            android.util.Pair r9 = new android.util.Pair
            long r10 = r7.id
            java.lang.Long r10 = java.lang.Long.valueOf(r10)
            int r11 = r7.type
            java.lang.Integer r11 = java.lang.Integer.valueOf(r11)
            r9.<init>(r10, r11)
            r8.put(r9, r7)
        Lc5:
            int r5 = r5 + 1
            goto L21
        Lc9:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.DownloadController.processDownloadObjects(int, java.util.ArrayList):void");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void newDownloadObjectsAvailable(int i) {
        int currentDownloadMask = getCurrentDownloadMask();
        if ((currentDownloadMask & 1) != 0 && (i & 1) != 0 && this.photoDownloadQueue.isEmpty()) {
            getMessagesStorage().getDownloadQueue(1);
        }
        if ((currentDownloadMask & 2) != 0 && (i & 2) != 0 && this.audioDownloadQueue.isEmpty()) {
            getMessagesStorage().getDownloadQueue(2);
        }
        if ((currentDownloadMask & 4) != 0 && (i & 4) != 0 && this.videoDownloadQueue.isEmpty()) {
            getMessagesStorage().getDownloadQueue(4);
        }
        if ((currentDownloadMask & 8) == 0 || (i & 8) == 0 || !this.documentDownloadQueue.isEmpty()) {
            return;
        }
        getMessagesStorage().getDownloadQueue(8);
    }

    private void checkDownloadFinished(String str, int i) {
        DownloadObject downloadObject = this.downloadQueueKeys.get(str);
        if (downloadObject != null) {
            this.downloadQueueKeys.remove(str);
            this.downloadQueuePairs.remove(new Pair(Long.valueOf(downloadObject.id), Integer.valueOf(downloadObject.type)));
            if (i == 0 || i == 2) {
                getMessagesStorage().removeFromDownloadQueue(downloadObject.id, downloadObject.type, false);
            }
            int i2 = downloadObject.type;
            if (i2 == 1) {
                this.photoDownloadQueue.remove(downloadObject);
                if (!this.photoDownloadQueue.isEmpty()) {
                    return;
                }
                newDownloadObjectsAvailable(1);
            } else if (i2 == 2) {
                this.audioDownloadQueue.remove(downloadObject);
                if (!this.audioDownloadQueue.isEmpty()) {
                    return;
                }
                newDownloadObjectsAvailable(2);
            } else if (i2 == 4) {
                this.videoDownloadQueue.remove(downloadObject);
                if (!this.videoDownloadQueue.isEmpty()) {
                    return;
                }
                newDownloadObjectsAvailable(4);
            } else if (i2 != 8) {
            } else {
                this.documentDownloadQueue.remove(downloadObject);
                if (!this.documentDownloadQueue.isEmpty()) {
                    return;
                }
                newDownloadObjectsAvailable(8);
            }
        }
    }

    public int generateObserverTag() {
        int i = this.lastTag;
        this.lastTag = i + 1;
        return i;
    }

    public void addLoadingFileObserver(String str, FileDownloadProgressListener fileDownloadProgressListener) {
        addLoadingFileObserver(str, null, fileDownloadProgressListener);
    }

    public void addLoadingFileObserver(String str, MessageObject messageObject, FileDownloadProgressListener fileDownloadProgressListener) {
        if (this.listenerInProgress) {
            this.addLaterArray.put(str, fileDownloadProgressListener);
            return;
        }
        removeLoadingFileObserver(fileDownloadProgressListener);
        ArrayList<WeakReference<FileDownloadProgressListener>> arrayList = this.loadingFileObservers.get(str);
        if (arrayList == null) {
            arrayList = new ArrayList<>();
            this.loadingFileObservers.put(str, arrayList);
        }
        arrayList.add(new WeakReference<>(fileDownloadProgressListener));
        if (messageObject != null) {
            ArrayList<MessageObject> arrayList2 = this.loadingFileMessagesObservers.get(str);
            if (arrayList2 == null) {
                arrayList2 = new ArrayList<>();
                this.loadingFileMessagesObservers.put(str, arrayList2);
            }
            arrayList2.add(messageObject);
        }
        this.observersByTag.put(fileDownloadProgressListener.getObserverTag(), str);
    }

    public void removeLoadingFileObserver(FileDownloadProgressListener fileDownloadProgressListener) {
        if (this.listenerInProgress) {
            this.deleteLaterArray.add(fileDownloadProgressListener);
            return;
        }
        String str = this.observersByTag.get(fileDownloadProgressListener.getObserverTag());
        if (str == null) {
            return;
        }
        ArrayList<WeakReference<FileDownloadProgressListener>> arrayList = this.loadingFileObservers.get(str);
        if (arrayList != null) {
            int i = 0;
            while (i < arrayList.size()) {
                WeakReference<FileDownloadProgressListener> weakReference = arrayList.get(i);
                if (weakReference.get() == null || weakReference.get() == fileDownloadProgressListener) {
                    arrayList.remove(i);
                    i--;
                }
                i++;
            }
            if (arrayList.isEmpty()) {
                this.loadingFileObservers.remove(str);
            }
        }
        this.observersByTag.remove(fileDownloadProgressListener.getObserverTag());
    }

    private void processLaterArrays() {
        for (Map.Entry<String, FileDownloadProgressListener> entry : this.addLaterArray.entrySet()) {
            addLoadingFileObserver(entry.getKey(), entry.getValue());
        }
        this.addLaterArray.clear();
        Iterator<FileDownloadProgressListener> it = this.deleteLaterArray.iterator();
        while (it.hasNext()) {
            removeLoadingFileObserver(it.next());
        }
        this.deleteLaterArray.clear();
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.fileLoadFailed || i == NotificationCenter.httpFileDidFailedLoad) {
            String str = (String) objArr[0];
            Integer num = (Integer) objArr[1];
            this.listenerInProgress = true;
            ArrayList<WeakReference<FileDownloadProgressListener>> arrayList = this.loadingFileObservers.get(str);
            if (arrayList != null) {
                int size = arrayList.size();
                for (int i3 = 0; i3 < size; i3++) {
                    WeakReference<FileDownloadProgressListener> weakReference = arrayList.get(i3);
                    if (weakReference.get() != null) {
                        weakReference.get().onFailedDownload(str, num.intValue() == 1);
                        if (num.intValue() != 1) {
                            this.observersByTag.remove(weakReference.get().getObserverTag());
                        }
                    }
                }
                if (num.intValue() != 1) {
                    this.loadingFileObservers.remove(str);
                }
            }
            this.listenerInProgress = false;
            processLaterArrays();
            checkDownloadFinished(str, num.intValue());
        } else if (i == NotificationCenter.fileLoaded || i == NotificationCenter.httpFileDidLoad) {
            this.listenerInProgress = true;
            String str2 = (String) objArr[0];
            ArrayList<MessageObject> arrayList2 = this.loadingFileMessagesObservers.get(str2);
            if (arrayList2 != null) {
                int size2 = arrayList2.size();
                for (int i4 = 0; i4 < size2; i4++) {
                    arrayList2.get(i4).mediaExists = true;
                }
                this.loadingFileMessagesObservers.remove(str2);
            }
            ArrayList<WeakReference<FileDownloadProgressListener>> arrayList3 = this.loadingFileObservers.get(str2);
            if (arrayList3 != null) {
                int size3 = arrayList3.size();
                for (int i5 = 0; i5 < size3; i5++) {
                    WeakReference<FileDownloadProgressListener> weakReference2 = arrayList3.get(i5);
                    if (weakReference2.get() != null) {
                        weakReference2.get().onSuccessDownload(str2);
                        this.observersByTag.remove(weakReference2.get().getObserverTag());
                    }
                }
                this.loadingFileObservers.remove(str2);
            }
            this.listenerInProgress = false;
            processLaterArrays();
            checkDownloadFinished(str2, 0);
        } else if (i == NotificationCenter.fileLoadProgressChanged) {
            this.listenerInProgress = true;
            String str3 = (String) objArr[0];
            ArrayList<WeakReference<FileDownloadProgressListener>> arrayList4 = this.loadingFileObservers.get(str3);
            if (arrayList4 != null) {
                Long l = (Long) objArr[1];
                Long l2 = (Long) objArr[2];
                int size4 = arrayList4.size();
                for (int i6 = 0; i6 < size4; i6++) {
                    WeakReference<FileDownloadProgressListener> weakReference3 = arrayList4.get(i6);
                    if (weakReference3.get() != null) {
                        weakReference3.get().onProgressDownload(str3, l.longValue(), l2.longValue());
                    }
                }
            }
            this.listenerInProgress = false;
            processLaterArrays();
        } else if (i == NotificationCenter.fileUploadProgressChanged) {
            this.listenerInProgress = true;
            String str4 = (String) objArr[0];
            ArrayList<WeakReference<FileDownloadProgressListener>> arrayList5 = this.loadingFileObservers.get(str4);
            if (arrayList5 != null) {
                Long l3 = (Long) objArr[1];
                Long l4 = (Long) objArr[2];
                Boolean bool = (Boolean) objArr[3];
                int size5 = arrayList5.size();
                for (int i7 = 0; i7 < size5; i7++) {
                    WeakReference<FileDownloadProgressListener> weakReference4 = arrayList5.get(i7);
                    if (weakReference4.get() != null) {
                        weakReference4.get().onProgressUpload(str4, l3.longValue(), l4.longValue(), bool.booleanValue());
                    }
                }
            }
            this.listenerInProgress = false;
            processLaterArrays();
            try {
                ArrayList<SendMessagesHelper.DelayedMessage> delayedMessages = getSendMessagesHelper().getDelayedMessages(str4);
                if (delayedMessages == null) {
                    return;
                }
                for (int i8 = 0; i8 < delayedMessages.size(); i8++) {
                    SendMessagesHelper.DelayedMessage delayedMessage = delayedMessages.get(i8);
                    if (delayedMessage.encryptedChat == null) {
                        long j = delayedMessage.peer;
                        int i9 = delayedMessage.topMessageId;
                        Long l5 = this.typingTimes.get(j);
                        if (delayedMessage.type == 4) {
                            if (l5 == null || l5.longValue() + 4000 < System.currentTimeMillis()) {
                                HashMap<Object, Object> hashMap = delayedMessage.extraHashMap;
                                MessageObject messageObject = (MessageObject) hashMap.get(str4 + "_i");
                                if (messageObject != null && messageObject.isVideo()) {
                                    getMessagesController().sendTyping(j, i9, 5, 0);
                                } else if (messageObject != null && messageObject.getDocument() != null) {
                                    getMessagesController().sendTyping(j, i9, 3, 0);
                                } else {
                                    getMessagesController().sendTyping(j, i9, 4, 0);
                                }
                                this.typingTimes.put(j, Long.valueOf(System.currentTimeMillis()));
                            }
                        } else {
                            delayedMessage.obj.getDocument();
                            if (l5 == null || l5.longValue() + 4000 < System.currentTimeMillis()) {
                                if (delayedMessage.obj.isRoundVideo()) {
                                    getMessagesController().sendTyping(j, i9, 8, 0);
                                } else if (delayedMessage.obj.isVideo()) {
                                    getMessagesController().sendTyping(j, i9, 5, 0);
                                } else if (delayedMessage.obj.isVoice()) {
                                    getMessagesController().sendTyping(j, i9, 9, 0);
                                } else if (delayedMessage.obj.getDocument() != null) {
                                    getMessagesController().sendTyping(j, i9, 3, 0);
                                } else if (delayedMessage.photoSize != null) {
                                    getMessagesController().sendTyping(j, i9, 4, 0);
                                }
                                this.typingTimes.put(j, Long.valueOf(System.currentTimeMillis()));
                            }
                        }
                    }
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    public static float getProgress(long[] jArr) {
        if (jArr == null || jArr.length < 2 || jArr[1] == 0) {
            return 0.0f;
        }
        return Math.min(1.0f, ((float) jArr[0]) / ((float) jArr[1]));
    }

    public void startDownloadFile(TLRPC$Document tLRPC$Document, final MessageObject messageObject) {
        if (messageObject.getDocument() == null) {
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.DownloadController$$ExternalSyntheticLambda8
            @Override // java.lang.Runnable
            public final void run() {
                DownloadController.this.lambda$startDownloadFile$5(messageObject);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$startDownloadFile$5(final MessageObject messageObject) {
        boolean z;
        boolean z2;
        int i = 0;
        while (true) {
            z = true;
            if (i >= this.recentDownloadingFiles.size()) {
                z2 = false;
                break;
            } else if (this.recentDownloadingFiles.get(i).getDocument() != null && this.recentDownloadingFiles.get(i).getDocument().id == messageObject.getDocument().id) {
                z2 = true;
                break;
            } else {
                i++;
            }
        }
        if (!z2) {
            for (int i2 = 0; i2 < this.downloadingFiles.size(); i2++) {
                if (this.downloadingFiles.get(i2).getDocument() != null && this.downloadingFiles.get(i2).getDocument().id == messageObject.getDocument().id) {
                    break;
                }
            }
        }
        z = z2;
        if (!z) {
            this.downloadingFiles.add(0, messageObject);
            getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.DownloadController$$ExternalSyntheticLambda6
                @Override // java.lang.Runnable
                public final void run() {
                    DownloadController.this.lambda$startDownloadFile$4(messageObject);
                }
            });
        }
        getNotificationCenter().postNotificationName(NotificationCenter.onDownloadingFilesChanged, new Object[0]);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$startDownloadFile$4(MessageObject messageObject) {
        try {
            NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(messageObject.messageOwner.getObjectSize());
            messageObject.messageOwner.serializeToStream(nativeByteBuffer);
            SQLitePreparedStatement executeFast = getMessagesStorage().getDatabase().executeFast("REPLACE INTO downloading_documents VALUES(?, ?, ?, ?, ?)");
            executeFast.bindByteBuffer(1, nativeByteBuffer);
            executeFast.bindInteger(2, messageObject.getDocument().dc_id);
            executeFast.bindLong(3, messageObject.getDocument().id);
            executeFast.bindLong(4, System.currentTimeMillis());
            executeFast.bindInteger(4, 0);
            executeFast.step();
            executeFast.dispose();
            nativeByteBuffer.reuse();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void onDownloadComplete(final MessageObject messageObject) {
        if (messageObject == null || messageObject.getDocument() == null) {
            return;
        }
        final TLRPC$Document document = messageObject.getDocument();
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.DownloadController$$ExternalSyntheticLambda11
            @Override // java.lang.Runnable
            public final void run() {
                DownloadController.this.lambda$onDownloadComplete$7(document, messageObject);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onDownloadComplete$7(TLRPC$Document tLRPC$Document, final MessageObject messageObject) {
        boolean z;
        boolean z2;
        int i = 0;
        while (true) {
            z = true;
            if (i >= this.downloadingFiles.size()) {
                z2 = false;
                break;
            } else if (this.downloadingFiles.get(i).getDocument() != null && this.downloadingFiles.get(i).getDocument().id == tLRPC$Document.id) {
                this.downloadingFiles.remove(i);
                z2 = true;
                break;
            } else {
                i++;
            }
        }
        if (z2) {
            int i2 = 0;
            while (true) {
                if (i2 >= this.recentDownloadingFiles.size()) {
                    z = false;
                    break;
                } else if (this.recentDownloadingFiles.get(i2).getDocument() != null && this.recentDownloadingFiles.get(i2).getDocument().id == tLRPC$Document.id) {
                    break;
                } else {
                    i2++;
                }
            }
            if (!z) {
                this.recentDownloadingFiles.add(0, messageObject);
                putToUnviewedDownloads(messageObject);
            }
            getNotificationCenter().postNotificationName(NotificationCenter.onDownloadingFilesChanged, new Object[0]);
            getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.DownloadController$$ExternalSyntheticLambda5
                @Override // java.lang.Runnable
                public final void run() {
                    DownloadController.this.lambda$onDownloadComplete$6(messageObject);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onDownloadComplete$6(MessageObject messageObject) {
        try {
            getMessagesStorage().getDatabase().executeFast(String.format(Locale.ENGLISH, "UPDATE downloading_documents SET state = 1, date = %d WHERE hash = %d AND id = %d", Long.valueOf(System.currentTimeMillis()), Integer.valueOf(messageObject.getDocument().dc_id), Long.valueOf(messageObject.getDocument().id))).stepThis().dispose();
            SQLiteCursor queryFinalized = getMessagesStorage().getDatabase().queryFinalized("SELECT COUNT(*) FROM downloading_documents WHERE state = 1", new Object[0]);
            int intValue = queryFinalized.next() ? queryFinalized.intValue(0) : 0;
            queryFinalized.dispose();
            SQLiteCursor queryFinalized2 = getMessagesStorage().getDatabase().queryFinalized("SELECT state FROM downloading_documents WHERE state = 1", new Object[0]);
            if (queryFinalized2.next()) {
                queryFinalized2.intValue(0);
            }
            queryFinalized2.dispose();
            if (intValue <= 100) {
                return;
            }
            SQLiteDatabase database = getMessagesStorage().getDatabase();
            SQLiteCursor queryFinalized3 = database.queryFinalized("SELECT hash, id FROM downloading_documents WHERE state = 1 ORDER BY date ASC LIMIT " + (100 - intValue), new Object[0]);
            ArrayList arrayList = new ArrayList();
            while (queryFinalized3.next()) {
                DownloadingDocumentEntry downloadingDocumentEntry = new DownloadingDocumentEntry();
                downloadingDocumentEntry.hash = queryFinalized3.intValue(0);
                downloadingDocumentEntry.id = queryFinalized3.longValue(1);
                arrayList.add(downloadingDocumentEntry);
            }
            queryFinalized3.dispose();
            SQLitePreparedStatement executeFast = getMessagesStorage().getDatabase().executeFast("DELETE FROM downloading_documents WHERE hash = ? AND id = ?");
            for (int i = 0; i < arrayList.size(); i++) {
                executeFast.requery();
                executeFast.bindInteger(1, ((DownloadingDocumentEntry) arrayList.get(i)).hash);
                executeFast.bindLong(2, ((DownloadingDocumentEntry) arrayList.get(i)).id);
                executeFast.step();
            }
            executeFast.dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void onDownloadFail(final MessageObject messageObject, final int i) {
        if (messageObject == null) {
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.DownloadController$$ExternalSyntheticLambda9
            @Override // java.lang.Runnable
            public final void run() {
                DownloadController.this.lambda$onDownloadFail$8(messageObject, i);
            }
        });
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.DownloadController$$ExternalSyntheticLambda7
            @Override // java.lang.Runnable
            public final void run() {
                DownloadController.this.lambda$onDownloadFail$9(messageObject);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onDownloadFail$8(MessageObject messageObject, int i) {
        boolean z;
        int i2 = 0;
        while (true) {
            if (i2 >= this.downloadingFiles.size()) {
                z = false;
                break;
            } else if (this.downloadingFiles.get(i2).getDocument().id == messageObject.getDocument().id) {
                this.downloadingFiles.remove(i2);
                z = true;
                break;
            } else {
                i2++;
            }
        }
        if (z) {
            getNotificationCenter().postNotificationName(NotificationCenter.onDownloadingFilesChanged, new Object[0]);
            if (i != 0) {
                return;
            }
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.showBulletin, 1, LocaleController.formatString("MessageNotFound", R.string.MessageNotFound, new Object[0]));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onDownloadFail$9(MessageObject messageObject) {
        try {
            SQLitePreparedStatement executeFast = getMessagesStorage().getDatabase().executeFast("DELETE FROM downloading_documents WHERE hash = ? AND id = ?");
            executeFast.bindInteger(1, messageObject.getDocument().dc_id);
            executeFast.bindLong(2, messageObject.getDocument().id);
            executeFast.step();
            executeFast.dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private void putToUnviewedDownloads(MessageObject messageObject) {
        this.unviewedDownloads.put(messageObject.getId(), messageObject);
        AndroidUtilities.cancelRunOnUIThread(this.clearUnviewedDownloadsRunnale);
        AndroidUtilities.runOnUIThread(this.clearUnviewedDownloadsRunnale, 60000L);
    }

    public void clearUnviewedDownloads() {
        this.unviewedDownloads.clear();
    }

    public void checkUnviewedDownloads(int i, long j) {
        MessageObject messageObject = this.unviewedDownloads.get(i);
        if (messageObject == null || messageObject.getDialogId() != j) {
            return;
        }
        this.unviewedDownloads.remove(i);
        if (this.unviewedDownloads.size() != 0) {
            return;
        }
        getNotificationCenter().postNotificationName(NotificationCenter.onDownloadingFilesChanged, new Object[0]);
    }

    public boolean hasUnviewedDownloads() {
        return this.unviewedDownloads.size() > 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class DownloadingDocumentEntry {
        int hash;
        long id;

        private DownloadingDocumentEntry() {
        }
    }

    public void loadDownloadingFiles() {
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.DownloadController$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                DownloadController.this.lambda$loadDownloadingFiles$11();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$loadDownloadingFiles$11() {
        final ArrayList<MessageObject> arrayList = new ArrayList<>();
        final ArrayList<MessageObject> arrayList2 = new ArrayList<>();
        ArrayList arrayList3 = new ArrayList();
        try {
            SQLiteCursor queryFinalized = getMessagesStorage().getDatabase().queryFinalized("SELECT data, state FROM downloading_documents ORDER BY date DESC", new Object[0]);
            while (queryFinalized.next()) {
                NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(0);
                int intValue = queryFinalized.intValue(1);
                if (byteBufferValue != null) {
                    TLRPC$Message TLdeserialize = TLRPC$Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                    if (TLdeserialize != null) {
                        TLdeserialize.readAttachPath(byteBufferValue, UserConfig.getInstance(this.currentAccount).clientUserId);
                        MessageObject messageObject = new MessageObject(this.currentAccount, TLdeserialize, false, false);
                        arrayList3.add(messageObject);
                        if (intValue == 0) {
                            arrayList.add(messageObject);
                        } else {
                            arrayList2.add(messageObject);
                        }
                    }
                    byteBufferValue.reuse();
                }
            }
            queryFinalized.dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
        getFileLoader().checkMediaExistance(arrayList);
        getFileLoader().checkMediaExistance(arrayList2);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.DownloadController$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                DownloadController.this.lambda$loadDownloadingFiles$10(arrayList, arrayList2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$loadDownloadingFiles$10(ArrayList arrayList, ArrayList arrayList2) {
        this.downloadingFiles.clear();
        this.downloadingFiles.addAll(arrayList);
        this.recentDownloadingFiles.clear();
        this.recentDownloadingFiles.addAll(arrayList2);
    }

    public void swapLoadingPriority(MessageObject messageObject, MessageObject messageObject2) {
        int indexOf = this.downloadingFiles.indexOf(messageObject);
        int indexOf2 = this.downloadingFiles.indexOf(messageObject2);
        if (indexOf >= 0 && indexOf2 >= 0) {
            this.downloadingFiles.set(indexOf, messageObject2);
            this.downloadingFiles.set(indexOf2, messageObject);
        }
        updateFilesLoadingPriority();
    }

    public void updateFilesLoadingPriority() {
        for (int size = this.downloadingFiles.size() - 1; size >= 0; size--) {
            if (getFileLoader().isLoadingFile(this.downloadingFiles.get(size).getFileName())) {
                getFileLoader().loadFile(this.downloadingFiles.get(size).getDocument(), this.downloadingFiles.get(size), 2, 0);
            }
        }
    }

    public void clearRecentDownloadedFiles() {
        this.recentDownloadingFiles.clear();
        getNotificationCenter().postNotificationName(NotificationCenter.onDownloadingFilesChanged, new Object[0]);
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.DownloadController$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                DownloadController.this.lambda$clearRecentDownloadedFiles$12();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$clearRecentDownloadedFiles$12() {
        try {
            getMessagesStorage().getDatabase().executeFast("DELETE FROM downloading_documents WHERE state = 1").stepThis().dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void deleteRecentFiles(final ArrayList<MessageObject> arrayList) {
        boolean z;
        for (int i = 0; i < arrayList.size(); i++) {
            int i2 = 0;
            while (true) {
                if (i2 >= this.recentDownloadingFiles.size()) {
                    z = false;
                    break;
                } else if (arrayList.get(i).getId() == this.recentDownloadingFiles.get(i2).getId() && this.recentDownloadingFiles.get(i2).getDialogId() == arrayList.get(i).getDialogId()) {
                    this.recentDownloadingFiles.remove(i2);
                    z = true;
                    break;
                } else {
                    i2++;
                }
            }
            if (!z) {
                int i3 = 0;
                while (true) {
                    if (i3 >= this.downloadingFiles.size()) {
                        break;
                    }
                    if (arrayList.get(i).getId() == this.downloadingFiles.get(i3).getId() && this.downloadingFiles.get(i3).getDialogId() == arrayList.get(i).getDialogId()) {
                        this.downloadingFiles.remove(i3);
                        break;
                    }
                    i3++;
                }
            }
            arrayList.get(i).putInDownloadsStore = false;
            FileLoader.getInstance(this.currentAccount).loadFile(arrayList.get(i).getDocument(), arrayList.get(i), 0, 0);
            FileLoader.getInstance(this.currentAccount).cancelLoadFile(arrayList.get(i).getDocument(), true);
        }
        getNotificationCenter().postNotificationName(NotificationCenter.onDownloadingFilesChanged, new Object[0]);
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.DownloadController$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                DownloadController.this.lambda$deleteRecentFiles$13(arrayList);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$deleteRecentFiles$13(ArrayList arrayList) {
        try {
            SQLitePreparedStatement executeFast = getMessagesStorage().getDatabase().executeFast("DELETE FROM downloading_documents WHERE hash = ? AND id = ?");
            for (int i = 0; i < arrayList.size(); i++) {
                executeFast.requery();
                executeFast.bindInteger(1, ((MessageObject) arrayList.get(i)).getDocument().dc_id);
                executeFast.bindLong(2, ((MessageObject) arrayList.get(i)).getDocument().id);
                executeFast.step();
                try {
                    FileLoader.getInstance(this.currentAccount).getPathToMessage(((MessageObject) arrayList.get(i)).messageOwner).delete();
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
            executeFast.dispose();
        } catch (Exception e2) {
            FileLog.e(e2);
        }
    }

    public boolean isDownloading(int i) {
        for (int i2 = 0; i2 < this.downloadingFiles.size(); i2++) {
            if (this.downloadingFiles.get(i2).messageOwner.id == i) {
                return true;
            }
        }
        return false;
    }
}
