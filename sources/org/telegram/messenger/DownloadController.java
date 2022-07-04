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
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$Message;
import org.telegram.tgnet.TLRPC$Photo;
import org.telegram.tgnet.TLRPC$PhotoSize;
import org.telegram.tgnet.TLRPC$TL_account_autoDownloadSettings;
import org.telegram.tgnet.TLRPC$TL_account_getAutoDownloadSettings;
import org.telegram.tgnet.TLRPC$TL_account_saveAutoDownloadSettings;
import org.telegram.tgnet.TLRPC$TL_autoDownloadSettings;
import org.telegram.tgnet.TLRPC$TL_error;

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
    private HashMap<String, FileDownloadProgressListener> addLaterArray = new HashMap<>();
    private ArrayList<DownloadObject> audioDownloadQueue = new ArrayList<>();
    Runnable clearUnviewedDownloadsRunnale = new Runnable() {
        public void run() {
            DownloadController.this.clearUnviewedDownloads();
            DownloadController.this.getNotificationCenter().postNotificationName(NotificationCenter.onDownloadingFilesChanged, new Object[0]);
        }
    };
    public int currentMobilePreset;
    public int currentRoamingPreset;
    public int currentWifiPreset;
    private ArrayList<FileDownloadProgressListener> deleteLaterArray = new ArrayList<>();
    private ArrayList<DownloadObject> documentDownloadQueue = new ArrayList<>();
    private HashMap<String, DownloadObject> downloadQueueKeys = new HashMap<>();
    private HashMap<Pair<Long, Integer>, DownloadObject> downloadQueuePairs = new HashMap<>();
    public final ArrayList<MessageObject> downloadingFiles = new ArrayList<>();
    public Preset highPreset;
    private int lastCheckMask = 0;
    private int lastTag = 0;
    private boolean listenerInProgress = false;
    private boolean loadingAutoDownloadConfig;
    private HashMap<String, ArrayList<MessageObject>> loadingFileMessagesObservers = new HashMap<>();
    private HashMap<String, ArrayList<WeakReference<FileDownloadProgressListener>>> loadingFileObservers = new HashMap<>();
    public Preset lowPreset;
    public Preset mediumPreset;
    public Preset mobilePreset;
    private SparseArray<String> observersByTag = new SparseArray<>();
    private ArrayList<DownloadObject> photoDownloadQueue = new ArrayList<>();
    public final ArrayList<MessageObject> recentDownloadingFiles = new ArrayList<>();
    public Preset roamingPreset;
    private LongSparseArray<Long> typingTimes = new LongSparseArray<>();
    public final SparseArray<MessageObject> unviewedDownloads = new SparseArray<>();
    private ArrayList<DownloadObject> videoDownloadQueue = new ArrayList<>();
    public Preset wifiPreset;

    public interface FileDownloadProgressListener {
        int getObserverTag();

        void onFailedDownload(String str, boolean z);

        void onProgressDownload(String str, long j, long j2);

        void onProgressUpload(String str, long j, long j2, boolean z);

        void onSuccessDownload(String str);
    }

    /* access modifiers changed from: private */
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
            String[] strArr;
            this.mask = new int[4];
            this.sizes = new long[4];
            String[] split = str.split("_");
            if (split.length >= 11) {
                boolean z = false;
                this.mask[0] = Utilities.parseInt((CharSequence) split[0]).intValue();
                this.mask[1] = Utilities.parseInt((CharSequence) split[1]).intValue();
                this.mask[2] = Utilities.parseInt((CharSequence) split[2]).intValue();
                this.mask[3] = Utilities.parseInt((CharSequence) split[3]).intValue();
                this.sizes[0] = (long) Utilities.parseInt((CharSequence) split[4]).intValue();
                this.sizes[1] = (long) Utilities.parseInt((CharSequence) split[5]).intValue();
                this.sizes[2] = (long) Utilities.parseInt((CharSequence) split[6]).intValue();
                this.sizes[3] = (long) Utilities.parseInt((CharSequence) split[7]).intValue();
                this.preloadVideo = Utilities.parseInt((CharSequence) split[8]).intValue() == 1;
                this.preloadMusic = Utilities.parseInt((CharSequence) split[9]).intValue() == 1;
                this.enabled = Utilities.parseInt((CharSequence) split[10]).intValue() == 1;
                if (split.length >= 12) {
                    this.lessCallData = Utilities.parseInt((CharSequence) split[11]).intValue() == 1 ? true : z;
                    strArr = null;
                } else {
                    strArr = str2.split("_");
                    this.lessCallData = Utilities.parseInt((CharSequence) strArr[11]).intValue() == 1 ? true : z;
                }
                if (split.length >= 13) {
                    this.maxVideoBitrate = Utilities.parseInt((CharSequence) split[12]).intValue();
                } else {
                    this.maxVideoBitrate = Utilities.parseInt((CharSequence) (strArr == null ? str2.split("_") : strArr)[12]).intValue();
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
            this.sizes[0] = (long) Math.max(512000, tLRPC$TL_autoDownloadSettings.photo_size_max);
            this.sizes[1] = Math.max(512000, tLRPC$TL_autoDownloadSettings.video_size_max);
            this.sizes[2] = Math.max(512000, tLRPC$TL_autoDownloadSettings.file_size_max);
            while (true) {
                int[] iArr = this.mask;
                if (i < iArr.length) {
                    if (tLRPC$TL_autoDownloadSettings.photo_size_max == 0 || tLRPC$TL_autoDownloadSettings.disabled) {
                        iArr[i] = iArr[i] & -2;
                    } else {
                        iArr[i] = iArr[i] | 1;
                    }
                    if (tLRPC$TL_autoDownloadSettings.video_size_max == 0 || tLRPC$TL_autoDownloadSettings.disabled) {
                        iArr[i] = iArr[i] & -5;
                    } else {
                        iArr[i] = iArr[i] | 4;
                    }
                    if (tLRPC$TL_autoDownloadSettings.file_size_max == 0 || tLRPC$TL_autoDownloadSettings.disabled) {
                        iArr[i] = iArr[i] & -9;
                    } else {
                        iArr[i] = iArr[i] | 8;
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
            if (i != iArr2[0] || iArr[1] != iArr2[1] || iArr[2] != iArr2[2] || iArr[3] != iArr2[3]) {
                return false;
            }
            long[] jArr = this.sizes;
            long j = jArr[0];
            long[] jArr2 = preset.sizes;
            return j == jArr2[0] && jArr[1] == jArr2[1] && jArr[2] == jArr2[2] && jArr[3] == jArr2[3] && this.preloadVideo == preset.preloadVideo && this.preloadMusic == preset.preloadMusic && this.maxVideoBitrate == preset.maxVideoBitrate;
        }

        public boolean isEnabled() {
            int i = 0;
            while (true) {
                int[] iArr = this.mask;
                if (i >= iArr.length) {
                    return false;
                }
                if (iArr[i] != 0) {
                    return true;
                }
                i++;
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

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public DownloadController(int i) {
        super(i);
        Object obj;
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
            String str2 = "currentRoamingPreset";
            int i2 = 0;
            for (int i3 = 4; i2 < i3; i3 = 4) {
                StringBuilder sb = new StringBuilder();
                String str3 = str;
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
                str = str3;
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
            edit.putInt(str2, 3);
            edit.commit();
        }
        AndroidUtilities.runOnUIThread(new DownloadController$$ExternalSyntheticLambda0(this));
        ApplicationLoader.applicationContext.registerReceiver(new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                DownloadController.this.checkAutodownloadSettings();
            }
        }, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        if (getUserConfig().isClientActivated()) {
            checkAutodownloadSettings();
        }
    }

    /* access modifiers changed from: private */
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
        if (this.loadingAutoDownloadConfig) {
            return;
        }
        if (z || Math.abs(System.currentTimeMillis() - getUserConfig().autoDownloadConfigLoadTime) >= 86400000) {
            this.loadingAutoDownloadConfig = true;
            getConnectionsManager().sendRequest(new TLRPC$TL_account_getAutoDownloadSettings(), new DownloadController$$ExternalSyntheticLambda12(this));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadAutoDownloadConfig$2(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new DownloadController$$ExternalSyntheticLambda11(this, tLObject));
    }

    /* access modifiers changed from: private */
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

    /* access modifiers changed from: protected */
    public int getAutodownloadMaskAll() {
        if (!this.mobilePreset.enabled && !this.roamingPreset.enabled && !this.wifiPreset.enabled) {
            return 0;
        }
        int i = 0;
        for (int i2 = 0; i2 < 4; i2++) {
            if (!((getCurrentMobilePreset().mask[i2] & 1) == 0 && (getCurrentWiFiPreset().mask[i2] & 1) == 0 && (getCurrentRoamingPreset().mask[i2] & 1) == 0)) {
                i |= 1;
            }
            if (!((getCurrentMobilePreset().mask[i2] & 2) == 0 && (getCurrentWiFiPreset().mask[i2] & 2) == 0 && (getCurrentRoamingPreset().mask[i2] & 2) == 0)) {
                i |= 2;
            }
            if (!((getCurrentMobilePreset().mask[i2] & 4) == 0 && (getCurrentWiFiPreset().mask[i2] & 4) == 0 && (4 & getCurrentRoamingPreset().mask[i2]) == 0)) {
                i |= 4;
            }
            if ((getCurrentMobilePreset().mask[i2] & 8) != 0 || (getCurrentWiFiPreset().mask[i2] & 8) != 0 || (getCurrentRoamingPreset().mask[i2] & 8) != 0) {
                i |= 8;
            }
        }
        return i;
    }

    public void checkAutodownloadSettings() {
        int currentDownloadMask = getCurrentDownloadMask();
        if (currentDownloadMask != this.lastCheckMask) {
            this.lastCheckMask = currentDownloadMask;
            if ((currentDownloadMask & 1) == 0) {
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
            } else if (this.photoDownloadQueue.isEmpty()) {
                newDownloadObjectsAvailable(1);
            }
            if ((currentDownloadMask & 2) == 0) {
                for (int i2 = 0; i2 < this.audioDownloadQueue.size(); i2++) {
                    getFileLoader().cancelLoadFile((TLRPC$Document) this.audioDownloadQueue.get(i2).object);
                }
                this.audioDownloadQueue.clear();
            } else if (this.audioDownloadQueue.isEmpty()) {
                newDownloadObjectsAvailable(2);
            }
            if ((currentDownloadMask & 8) == 0) {
                for (int i3 = 0; i3 < this.documentDownloadQueue.size(); i3++) {
                    getFileLoader().cancelLoadFile((TLRPC$Document) this.documentDownloadQueue.get(i3).object);
                }
                this.documentDownloadQueue.clear();
            } else if (this.documentDownloadQueue.isEmpty()) {
                newDownloadObjectsAvailable(8);
            }
            if ((currentDownloadMask & 4) == 0) {
                for (int i4 = 0; i4 < this.videoDownloadQueue.size(); i4++) {
                    getFileLoader().cancelLoadFile((TLRPC$Document) this.videoDownloadQueue.get(i4).object);
                }
                this.videoDownloadQueue.clear();
            } else if (this.videoDownloadQueue.isEmpty()) {
                newDownloadObjectsAvailable(4);
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
            if ((autodownloadMaskAll & 8) == 0) {
                getMessagesStorage().clearDownloadQueue(8);
            }
        }
    }

    public boolean canDownloadMedia(MessageObject messageObject) {
        return canDownloadMedia(messageObject.messageOwner) == 1;
    }

    public boolean canDownloadMedia(int i, long j) {
        Preset preset;
        int autodownloadNetworkType = ApplicationLoader.getAutodownloadNetworkType();
        if (autodownloadNetworkType == 1) {
            if (!this.wifiPreset.enabled) {
                return false;
            }
            preset = getCurrentWiFiPreset();
        } else if (autodownloadNetworkType == 2) {
            if (!this.roamingPreset.enabled) {
                return false;
            }
            preset = getCurrentRoamingPreset();
        } else if (!this.mobilePreset.enabled) {
            return false;
        } else {
            preset = getCurrentMobilePreset();
        }
        int i2 = preset.mask[1];
        long j2 = preset.sizes[typeToIndex(i)];
        if (i != 1 && (j == 0 || j > j2)) {
            return false;
        }
        if (i == 2 || (i & i2) != 0) {
            return true;
        }
        return false;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:31:0x0067, code lost:
        if (getContactsController().contactsDict.containsKey(java.lang.Long.valueOf(r7.user_id)) != false) goto L_0x0069;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x0089, code lost:
        if (getContactsController().contactsDict.containsKey(java.lang.Long.valueOf(r1.from_id.user_id)) != false) goto L_0x0069;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:51:0x00c8, code lost:
        if (getContactsController().contactsDict.containsKey(java.lang.Long.valueOf(r1.from_id.user_id)) != false) goto L_0x0069;
     */
    /* JADX WARNING: Removed duplicated region for block: B:56:0x00d4  */
    /* JADX WARNING: Removed duplicated region for block: B:60:0x00e0  */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x00ff  */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x010f  */
    /* JADX WARNING: Removed duplicated region for block: B:81:0x012d  */
    /* JADX WARNING: Removed duplicated region for block: B:84:0x0133  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int canDownloadMedia(org.telegram.tgnet.TLRPC$Message r18) {
        /*
            r17 = this;
            r0 = r17
            r1 = r18
            r2 = 0
            if (r1 != 0) goto L_0x0008
            return r2
        L_0x0008:
            boolean r3 = org.telegram.messenger.MessageObject.isVideoMessage(r18)
            r4 = 1
            r5 = 2
            if (r3 != 0) goto L_0x004a
            boolean r6 = org.telegram.messenger.MessageObject.isGifMessage(r18)
            if (r6 != 0) goto L_0x004a
            boolean r6 = org.telegram.messenger.MessageObject.isRoundVideoMessage(r18)
            if (r6 != 0) goto L_0x004a
            boolean r6 = org.telegram.messenger.MessageObject.isGameMessage(r18)
            if (r6 == 0) goto L_0x0023
            goto L_0x004a
        L_0x0023:
            boolean r6 = org.telegram.messenger.MessageObject.isVoiceMessage(r18)
            if (r6 == 0) goto L_0x002b
            r6 = 2
            goto L_0x004b
        L_0x002b:
            boolean r6 = org.telegram.messenger.MessageObject.isPhoto(r18)
            if (r6 != 0) goto L_0x0048
            boolean r6 = org.telegram.messenger.MessageObject.isStickerMessage(r18)
            if (r6 != 0) goto L_0x0048
            boolean r6 = org.telegram.messenger.MessageObject.isAnimatedStickerMessage(r18)
            if (r6 == 0) goto L_0x003e
            goto L_0x0048
        L_0x003e:
            org.telegram.tgnet.TLRPC$Document r6 = org.telegram.messenger.MessageObject.getDocument(r18)
            if (r6 == 0) goto L_0x0047
            r6 = 8
            goto L_0x004b
        L_0x0047:
            return r2
        L_0x0048:
            r6 = 1
            goto L_0x004b
        L_0x004a:
            r6 = 4
        L_0x004b:
            org.telegram.tgnet.TLRPC$Peer r7 = r1.peer_id
            r8 = 0
            if (r7 == 0) goto L_0x00cd
            long r10 = r7.user_id
            int r12 = (r10 > r8 ? 1 : (r10 == r8 ? 0 : -1))
            if (r12 == 0) goto L_0x006b
            org.telegram.messenger.ContactsController r10 = r17.getContactsController()
            j$.util.concurrent.ConcurrentHashMap<java.lang.Long, org.telegram.tgnet.TLRPC$TL_contact> r10 = r10.contactsDict
            long r11 = r7.user_id
            java.lang.Long r7 = java.lang.Long.valueOf(r11)
            boolean r7 = r10.containsKey(r7)
            if (r7 == 0) goto L_0x00cd
        L_0x0069:
            r7 = 0
            goto L_0x00ce
        L_0x006b:
            long r10 = r7.chat_id
            int r12 = (r10 > r8 ? 1 : (r10 == r8 ? 0 : -1))
            if (r12 == 0) goto L_0x008e
            org.telegram.tgnet.TLRPC$Peer r7 = r1.from_id
            boolean r7 = r7 instanceof org.telegram.tgnet.TLRPC$TL_peerUser
            if (r7 == 0) goto L_0x008c
            org.telegram.messenger.ContactsController r7 = r17.getContactsController()
            j$.util.concurrent.ConcurrentHashMap<java.lang.Long, org.telegram.tgnet.TLRPC$TL_contact> r7 = r7.contactsDict
            org.telegram.tgnet.TLRPC$Peer r10 = r1.from_id
            long r10 = r10.user_id
            java.lang.Long r10 = java.lang.Long.valueOf(r10)
            boolean r7 = r7.containsKey(r10)
            if (r7 == 0) goto L_0x008c
            goto L_0x0069
        L_0x008c:
            r7 = 2
            goto L_0x00ce
        L_0x008e:
            long r10 = r7.channel_id
            int r7 = (r10 > r8 ? 1 : (r10 == r8 ? 0 : -1))
            if (r7 == 0) goto L_0x00a5
            org.telegram.messenger.MessagesController r7 = r17.getMessagesController()
            org.telegram.tgnet.TLRPC$Peer r10 = r1.peer_id
            long r10 = r10.channel_id
            java.lang.Long r10 = java.lang.Long.valueOf(r10)
            org.telegram.tgnet.TLRPC$Chat r7 = r7.getChat(r10)
            goto L_0x00a6
        L_0x00a5:
            r7 = 0
        L_0x00a6:
            boolean r10 = org.telegram.messenger.ChatObject.isChannel(r7)
            if (r10 == 0) goto L_0x00cb
            boolean r7 = r7.megagroup
            if (r7 == 0) goto L_0x00cb
            org.telegram.tgnet.TLRPC$Peer r7 = r1.from_id
            boolean r7 = r7 instanceof org.telegram.tgnet.TLRPC$TL_peerUser
            if (r7 == 0) goto L_0x008c
            org.telegram.messenger.ContactsController r7 = r17.getContactsController()
            j$.util.concurrent.ConcurrentHashMap<java.lang.Long, org.telegram.tgnet.TLRPC$TL_contact> r7 = r7.contactsDict
            org.telegram.tgnet.TLRPC$Peer r10 = r1.from_id
            long r10 = r10.user_id
            java.lang.Long r10 = java.lang.Long.valueOf(r10)
            boolean r7 = r7.containsKey(r10)
            if (r7 == 0) goto L_0x008c
            goto L_0x0069
        L_0x00cb:
            r7 = 3
            goto L_0x00ce
        L_0x00cd:
            r7 = 1
        L_0x00ce:
            int r10 = org.telegram.messenger.ApplicationLoader.getAutodownloadNetworkType()
            if (r10 != r4) goto L_0x00e0
            org.telegram.messenger.DownloadController$Preset r10 = r0.wifiPreset
            boolean r10 = r10.enabled
            if (r10 != 0) goto L_0x00db
            return r2
        L_0x00db:
            org.telegram.messenger.DownloadController$Preset r10 = r17.getCurrentWiFiPreset()
            goto L_0x00f9
        L_0x00e0:
            if (r10 != r5) goto L_0x00ee
            org.telegram.messenger.DownloadController$Preset r10 = r0.roamingPreset
            boolean r10 = r10.enabled
            if (r10 != 0) goto L_0x00e9
            return r2
        L_0x00e9:
            org.telegram.messenger.DownloadController$Preset r10 = r17.getCurrentRoamingPreset()
            goto L_0x00f9
        L_0x00ee:
            org.telegram.messenger.DownloadController$Preset r10 = r0.mobilePreset
            boolean r10 = r10.enabled
            if (r10 != 0) goto L_0x00f5
            return r2
        L_0x00f5:
            org.telegram.messenger.DownloadController$Preset r10 = r17.getCurrentMobilePreset()
        L_0x00f9:
            int[] r11 = r10.mask
            r7 = r11[r7]
            if (r6 != r5) goto L_0x010f
            r11 = 524288(0x80000, double:2.590327E-318)
            long[] r13 = r10.sizes
            int r14 = typeToIndex(r6)
            r14 = r13[r14]
            long r11 = java.lang.Math.max(r11, r14)
            goto L_0x0118
        L_0x010f:
            long[] r11 = r10.sizes
            int r12 = typeToIndex(r6)
            r12 = r11[r12]
            r11 = r12
        L_0x0118:
            long r13 = org.telegram.messenger.MessageObject.getMessageSize(r18)
            if (r3 == 0) goto L_0x0133
            boolean r1 = r10.preloadVideo
            if (r1 == 0) goto L_0x0133
            int r1 = (r13 > r11 ? 1 : (r13 == r11 ? 0 : -1))
            if (r1 <= 0) goto L_0x0133
            r15 = 2097152(0x200000, double:1.0361308E-317)
            int r1 = (r11 > r15 ? 1 : (r11 == r15 ? 0 : -1))
            if (r1 <= 0) goto L_0x0133
            r1 = r7 & r6
            if (r1 == 0) goto L_0x0132
            r2 = 2
        L_0x0132:
            return r2
        L_0x0133:
            if (r6 == r4) goto L_0x013d
            int r1 = (r13 > r8 ? 1 : (r13 == r8 ? 0 : -1))
            if (r1 == 0) goto L_0x0144
            int r1 = (r13 > r11 ? 1 : (r13 == r11 ? 0 : -1))
            if (r1 > 0) goto L_0x0144
        L_0x013d:
            if (r6 == r5) goto L_0x0143
            r1 = r7 & r6
            if (r1 == 0) goto L_0x0144
        L_0x0143:
            r2 = 1
        L_0x0144:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.DownloadController.canDownloadMedia(org.telegram.tgnet.TLRPC$Message):int");
    }

    /* access modifiers changed from: protected */
    public boolean canDownloadNextTrack() {
        int autodownloadNetworkType = ApplicationLoader.getAutodownloadNetworkType();
        if (autodownloadNetworkType == 1) {
            if (!this.wifiPreset.enabled || !getCurrentWiFiPreset().preloadMusic) {
                return false;
            }
            return true;
        } else if (autodownloadNetworkType == 2) {
            if (!this.roamingPreset.enabled || !getCurrentRoamingPreset().preloadMusic) {
                return false;
            }
            return true;
        } else if (!this.mobilePreset.enabled || !getCurrentMobilePreset().preloadMusic) {
            return false;
        } else {
            return true;
        }
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
        Preset preset;
        boolean z;
        TLRPC$TL_account_saveAutoDownloadSettings tLRPC$TL_account_saveAutoDownloadSettings = new TLRPC$TL_account_saveAutoDownloadSettings();
        if (i == 0) {
            preset = getCurrentMobilePreset();
            z = this.mobilePreset.enabled;
        } else if (i == 1) {
            preset = getCurrentWiFiPreset();
            z = this.wifiPreset.enabled;
        } else {
            preset = getCurrentRoamingPreset();
            z = this.roamingPreset.enabled;
        }
        TLRPC$TL_autoDownloadSettings tLRPC$TL_autoDownloadSettings = new TLRPC$TL_autoDownloadSettings();
        tLRPC$TL_account_saveAutoDownloadSettings.settings = tLRPC$TL_autoDownloadSettings;
        tLRPC$TL_autoDownloadSettings.audio_preload_next = preset.preloadMusic;
        tLRPC$TL_autoDownloadSettings.video_preload_large = preset.preloadVideo;
        tLRPC$TL_autoDownloadSettings.phonecalls_less_data = preset.lessCallData;
        tLRPC$TL_autoDownloadSettings.video_upload_maxbitrate = preset.maxVideoBitrate;
        tLRPC$TL_autoDownloadSettings.disabled = !z;
        int i2 = 0;
        int i3 = 0;
        boolean z2 = false;
        boolean z3 = false;
        boolean z4 = false;
        while (true) {
            int[] iArr = preset.mask;
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
            i2 = (int) preset.sizes[0];
        }
        tLRPC$TL_autoDownloadSettings2.photo_size_max = i2;
        long j = 0;
        tLRPC$TL_autoDownloadSettings2.video_size_max = z3 ? preset.sizes[1] : 0;
        if (z4) {
            j = preset.sizes[2];
        }
        tLRPC$TL_autoDownloadSettings2.file_size_max = j;
        getConnectionsManager().sendRequest(tLRPC$TL_account_saveAutoDownloadSettings, DownloadController$$ExternalSyntheticLambda13.INSTANCE);
    }

    /* access modifiers changed from: protected */
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

    /* JADX WARNING: type inference failed for: r8v12, types: [java.lang.String] */
    /* JADX WARNING: type inference failed for: r8v16, types: [java.lang.String] */
    /* access modifiers changed from: protected */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x0065  */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x008a  */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x00a7  */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x00c5 A[SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void processDownloadObjects(int r20, java.util.ArrayList<org.telegram.messenger.DownloadObject> r21) {
        /*
            r19 = this;
            r0 = r19
            r1 = r20
            boolean r2 = r21.isEmpty()
            if (r2 == 0) goto L_0x000b
            return
        L_0x000b:
            r2 = 2
            r3 = 1
            if (r1 != r3) goto L_0x0012
            java.util.ArrayList<org.telegram.messenger.DownloadObject> r1 = r0.photoDownloadQueue
            goto L_0x001f
        L_0x0012:
            if (r1 != r2) goto L_0x0017
            java.util.ArrayList<org.telegram.messenger.DownloadObject> r1 = r0.audioDownloadQueue
            goto L_0x001f
        L_0x0017:
            r4 = 4
            if (r1 != r4) goto L_0x001d
            java.util.ArrayList<org.telegram.messenger.DownloadObject> r1 = r0.videoDownloadQueue
            goto L_0x001f
        L_0x001d:
            java.util.ArrayList<org.telegram.messenger.DownloadObject> r1 = r0.documentDownloadQueue
        L_0x001f:
            r4 = 0
            r5 = 0
        L_0x0021:
            int r6 = r21.size()
            if (r5 >= r6) goto L_0x00c9
            r6 = r21
            java.lang.Object r7 = r6.get(r5)
            org.telegram.messenger.DownloadObject r7 = (org.telegram.messenger.DownloadObject) r7
            org.telegram.tgnet.TLObject r8 = r7.object
            boolean r9 = r8 instanceof org.telegram.tgnet.TLRPC$Document
            r10 = 0
            if (r9 == 0) goto L_0x0042
            org.telegram.tgnet.TLRPC$Document r8 = (org.telegram.tgnet.TLRPC$Document) r8
            java.lang.String r8 = org.telegram.messenger.FileLoader.getAttachFileName(r8)
        L_0x003c:
            r18 = r10
            r10 = r8
            r8 = r18
            goto L_0x0058
        L_0x0042:
            boolean r9 = r8 instanceof org.telegram.tgnet.TLRPC$Photo
            if (r9 == 0) goto L_0x0057
            org.telegram.tgnet.TLRPC$Photo r8 = (org.telegram.tgnet.TLRPC$Photo) r8
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r8 = r8.sizes
            int r9 = org.telegram.messenger.AndroidUtilities.getPhotoSize()
            org.telegram.tgnet.TLRPC$PhotoSize r10 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r8, r9)
            java.lang.String r8 = org.telegram.messenger.FileLoader.getAttachFileName(r10)
            goto L_0x003c
        L_0x0057:
            r8 = r10
        L_0x0058:
            if (r10 == 0) goto L_0x00c5
            java.util.HashMap<java.lang.String, org.telegram.messenger.DownloadObject> r9 = r0.downloadQueueKeys
            boolean r9 = r9.containsKey(r10)
            if (r9 == 0) goto L_0x0063
            goto L_0x00c5
        L_0x0063:
            if (r8 == 0) goto L_0x008a
            org.telegram.tgnet.TLObject r9 = r7.object
            org.telegram.tgnet.TLRPC$Photo r9 = (org.telegram.tgnet.TLRPC$Photo) r9
            boolean r11 = r7.secret
            if (r11 == 0) goto L_0x0070
            r17 = 2
            goto L_0x0079
        L_0x0070:
            boolean r11 = r7.forceCache
            if (r11 == 0) goto L_0x0077
            r17 = 1
            goto L_0x0079
        L_0x0077:
            r17 = 0
        L_0x0079:
            org.telegram.messenger.FileLoader r12 = r19.getFileLoader()
            org.telegram.messenger.ImageLocation r13 = org.telegram.messenger.ImageLocation.getForPhoto((org.telegram.tgnet.TLRPC$PhotoSize) r8, (org.telegram.tgnet.TLRPC$Photo) r9)
            java.lang.String r14 = r7.parent
            r15 = 0
            r16 = 0
            r12.loadFile(r13, r14, r15, r16, r17)
            goto L_0x00a2
        L_0x008a:
            org.telegram.tgnet.TLObject r8 = r7.object
            boolean r9 = r8 instanceof org.telegram.tgnet.TLRPC$Document
            if (r9 == 0) goto L_0x00a4
            org.telegram.tgnet.TLRPC$Document r8 = (org.telegram.tgnet.TLRPC$Document) r8
            org.telegram.messenger.FileLoader r9 = r19.getFileLoader()
            java.lang.String r11 = r7.parent
            boolean r12 = r7.secret
            if (r12 == 0) goto L_0x009e
            r12 = 2
            goto L_0x009f
        L_0x009e:
            r12 = 0
        L_0x009f:
            r9.loadFile(r8, r11, r4, r12)
        L_0x00a2:
            r8 = 1
            goto L_0x00a5
        L_0x00a4:
            r8 = 0
        L_0x00a5:
            if (r8 == 0) goto L_0x00c5
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
        L_0x00c5:
            int r5 = r5 + 1
            goto L_0x0021
        L_0x00c9:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.DownloadController.processDownloadObjects(int, java.util.ArrayList):void");
    }

    /* access modifiers changed from: protected */
    public void newDownloadObjectsAvailable(int i) {
        int currentDownloadMask = getCurrentDownloadMask();
        if (!((currentDownloadMask & 1) == 0 || (i & 1) == 0 || !this.photoDownloadQueue.isEmpty())) {
            getMessagesStorage().getDownloadQueue(1);
        }
        if (!((currentDownloadMask & 2) == 0 || (i & 2) == 0 || !this.audioDownloadQueue.isEmpty())) {
            getMessagesStorage().getDownloadQueue(2);
        }
        if (!((currentDownloadMask & 4) == 0 || (i & 4) == 0 || !this.videoDownloadQueue.isEmpty())) {
            getMessagesStorage().getDownloadQueue(4);
        }
        if ((currentDownloadMask & 8) != 0 && (i & 8) != 0 && this.documentDownloadQueue.isEmpty()) {
            getMessagesStorage().getDownloadQueue(8);
        }
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
                if (this.photoDownloadQueue.isEmpty()) {
                    newDownloadObjectsAvailable(1);
                }
            } else if (i2 == 2) {
                this.audioDownloadQueue.remove(downloadObject);
                if (this.audioDownloadQueue.isEmpty()) {
                    newDownloadObjectsAvailable(2);
                }
            } else if (i2 == 4) {
                this.videoDownloadQueue.remove(downloadObject);
                if (this.videoDownloadQueue.isEmpty()) {
                    newDownloadObjectsAvailable(4);
                }
            } else if (i2 == 8) {
                this.documentDownloadQueue.remove(downloadObject);
                if (this.documentDownloadQueue.isEmpty()) {
                    newDownloadObjectsAvailable(8);
                }
            }
        }
    }

    public int generateObserverTag() {
        int i = this.lastTag;
        this.lastTag = i + 1;
        return i;
    }

    public void addLoadingFileObserver(String str, FileDownloadProgressListener fileDownloadProgressListener) {
        addLoadingFileObserver(str, (MessageObject) null, fileDownloadProgressListener);
    }

    public void addLoadingFileObserver(String str, MessageObject messageObject, FileDownloadProgressListener fileDownloadProgressListener) {
        if (this.listenerInProgress) {
            this.addLaterArray.put(str, fileDownloadProgressListener);
            return;
        }
        removeLoadingFileObserver(fileDownloadProgressListener);
        ArrayList arrayList = this.loadingFileObservers.get(str);
        if (arrayList == null) {
            arrayList = new ArrayList();
            this.loadingFileObservers.put(str, arrayList);
        }
        arrayList.add(new WeakReference(fileDownloadProgressListener));
        if (messageObject != null) {
            ArrayList arrayList2 = this.loadingFileMessagesObservers.get(str);
            if (arrayList2 == null) {
                arrayList2 = new ArrayList();
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
        if (str != null) {
            ArrayList arrayList = this.loadingFileObservers.get(str);
            if (arrayList != null) {
                int i = 0;
                while (i < arrayList.size()) {
                    WeakReference weakReference = (WeakReference) arrayList.get(i);
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
    }

    private void processLaterArrays() {
        for (Map.Entry next : this.addLaterArray.entrySet()) {
            addLoadingFileObserver((String) next.getKey(), (FileDownloadProgressListener) next.getValue());
        }
        this.addLaterArray.clear();
        Iterator<FileDownloadProgressListener> it = this.deleteLaterArray.iterator();
        while (it.hasNext()) {
            removeLoadingFileObserver(it.next());
        }
        this.deleteLaterArray.clear();
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.fileLoadFailed || i == NotificationCenter.httpFileDidFailedLoad) {
            String str = objArr[0];
            Integer num = objArr[1];
            this.listenerInProgress = true;
            ArrayList arrayList = this.loadingFileObservers.get(str);
            if (arrayList != null) {
                int size = arrayList.size();
                for (int i3 = 0; i3 < size; i3++) {
                    WeakReference weakReference = (WeakReference) arrayList.get(i3);
                    if (weakReference.get() != null) {
                        ((FileDownloadProgressListener) weakReference.get()).onFailedDownload(str, num.intValue() == 1);
                        if (num.intValue() != 1) {
                            this.observersByTag.remove(((FileDownloadProgressListener) weakReference.get()).getObserverTag());
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
            String str2 = objArr[0];
            ArrayList arrayList2 = this.loadingFileMessagesObservers.get(str2);
            if (arrayList2 != null) {
                int size2 = arrayList2.size();
                for (int i4 = 0; i4 < size2; i4++) {
                    ((MessageObject) arrayList2.get(i4)).mediaExists = true;
                }
                this.loadingFileMessagesObservers.remove(str2);
            }
            ArrayList arrayList3 = this.loadingFileObservers.get(str2);
            if (arrayList3 != null) {
                int size3 = arrayList3.size();
                for (int i5 = 0; i5 < size3; i5++) {
                    WeakReference weakReference2 = (WeakReference) arrayList3.get(i5);
                    if (weakReference2.get() != null) {
                        ((FileDownloadProgressListener) weakReference2.get()).onSuccessDownload(str2);
                        this.observersByTag.remove(((FileDownloadProgressListener) weakReference2.get()).getObserverTag());
                    }
                }
                this.loadingFileObservers.remove(str2);
            }
            this.listenerInProgress = false;
            processLaterArrays();
            checkDownloadFinished(str2, 0);
        } else if (i == NotificationCenter.fileLoadProgressChanged) {
            this.listenerInProgress = true;
            String str3 = objArr[0];
            ArrayList arrayList4 = this.loadingFileObservers.get(str3);
            if (arrayList4 != null) {
                Long l = objArr[1];
                Long l2 = objArr[2];
                int size4 = arrayList4.size();
                for (int i6 = 0; i6 < size4; i6++) {
                    WeakReference weakReference3 = (WeakReference) arrayList4.get(i6);
                    if (weakReference3.get() != null) {
                        ((FileDownloadProgressListener) weakReference3.get()).onProgressDownload(str3, l.longValue(), l2.longValue());
                    }
                }
            }
            this.listenerInProgress = false;
            processLaterArrays();
        } else if (i == NotificationCenter.fileUploadProgressChanged) {
            this.listenerInProgress = true;
            String str4 = objArr[0];
            ArrayList arrayList5 = this.loadingFileObservers.get(str4);
            if (arrayList5 != null) {
                Long l3 = objArr[1];
                Long l4 = objArr[2];
                Boolean bool = objArr[3];
                int size5 = arrayList5.size();
                for (int i7 = 0; i7 < size5; i7++) {
                    WeakReference weakReference4 = (WeakReference) arrayList5.get(i7);
                    if (weakReference4.get() != null) {
                        ((FileDownloadProgressListener) weakReference4.get()).onProgressUpload(str4, l3.longValue(), l4.longValue(), bool.booleanValue());
                    }
                }
            }
            this.listenerInProgress = false;
            processLaterArrays();
            try {
                ArrayList<SendMessagesHelper.DelayedMessage> delayedMessages = getSendMessagesHelper().getDelayedMessages(str4);
                if (delayedMessages != null) {
                    for (int i8 = 0; i8 < delayedMessages.size(); i8++) {
                        SendMessagesHelper.DelayedMessage delayedMessage = delayedMessages.get(i8);
                        if (delayedMessage.encryptedChat == null) {
                            long j = delayedMessage.peer;
                            int i9 = delayedMessage.topMessageId;
                            Long l5 = this.typingTimes.get(j);
                            if (delayedMessage.type != 4) {
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
                            } else if (l5 == null || l5.longValue() + 4000 < System.currentTimeMillis()) {
                                HashMap<Object, Object> hashMap = delayedMessage.extraHashMap;
                                MessageObject messageObject = (MessageObject) hashMap.get(str4 + "_i");
                                if (messageObject != null && messageObject.isVideo()) {
                                    getMessagesController().sendTyping(j, i9, 5, 0);
                                } else if (messageObject == null || messageObject.getDocument() == null) {
                                    getMessagesController().sendTyping(j, i9, 4, 0);
                                } else {
                                    getMessagesController().sendTyping(j, i9, 3, 0);
                                }
                                this.typingTimes.put(j, Long.valueOf(System.currentTimeMillis()));
                            }
                        }
                    }
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    public static float getProgress(long[] jArr) {
        if (jArr == null || jArr.length < 2 || jArr[1] == 0) {
            return 0.0f;
        }
        return Math.min(1.0f, ((float) jArr[0]) / ((float) jArr[1]));
    }

    public void startDownloadFile(TLRPC$Document tLRPC$Document, MessageObject messageObject) {
        if (messageObject.getDocument() != null) {
            AndroidUtilities.runOnUIThread(new DownloadController$$ExternalSyntheticLambda9(this, messageObject));
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x006f  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$startDownloadFile$5(org.telegram.messenger.MessageObject r10) {
        /*
            r9 = this;
            r0 = 0
            r1 = 0
        L_0x0002:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r2 = r9.recentDownloadingFiles
            int r2 = r2.size()
            r3 = 1
            if (r1 >= r2) goto L_0x0036
            java.util.ArrayList<org.telegram.messenger.MessageObject> r2 = r9.recentDownloadingFiles
            java.lang.Object r2 = r2.get(r1)
            org.telegram.messenger.MessageObject r2 = (org.telegram.messenger.MessageObject) r2
            org.telegram.tgnet.TLRPC$Document r2 = r2.getDocument()
            if (r2 == 0) goto L_0x0033
            java.util.ArrayList<org.telegram.messenger.MessageObject> r2 = r9.recentDownloadingFiles
            java.lang.Object r2 = r2.get(r1)
            org.telegram.messenger.MessageObject r2 = (org.telegram.messenger.MessageObject) r2
            org.telegram.tgnet.TLRPC$Document r2 = r2.getDocument()
            long r4 = r2.id
            org.telegram.tgnet.TLRPC$Document r2 = r10.getDocument()
            long r6 = r2.id
            int r2 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r2 != 0) goto L_0x0033
            r1 = 1
            goto L_0x0037
        L_0x0033:
            int r1 = r1 + 1
            goto L_0x0002
        L_0x0036:
            r1 = 0
        L_0x0037:
            if (r1 != 0) goto L_0x006c
            r2 = 0
        L_0x003a:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r4 = r9.downloadingFiles
            int r4 = r4.size()
            if (r2 >= r4) goto L_0x006c
            java.util.ArrayList<org.telegram.messenger.MessageObject> r4 = r9.downloadingFiles
            java.lang.Object r4 = r4.get(r2)
            org.telegram.messenger.MessageObject r4 = (org.telegram.messenger.MessageObject) r4
            org.telegram.tgnet.TLRPC$Document r4 = r4.getDocument()
            if (r4 == 0) goto L_0x0069
            java.util.ArrayList<org.telegram.messenger.MessageObject> r4 = r9.downloadingFiles
            java.lang.Object r4 = r4.get(r2)
            org.telegram.messenger.MessageObject r4 = (org.telegram.messenger.MessageObject) r4
            org.telegram.tgnet.TLRPC$Document r4 = r4.getDocument()
            long r4 = r4.id
            org.telegram.tgnet.TLRPC$Document r6 = r10.getDocument()
            long r6 = r6.id
            int r8 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r8 != 0) goto L_0x0069
            goto L_0x006d
        L_0x0069:
            int r2 = r2 + 1
            goto L_0x003a
        L_0x006c:
            r3 = r1
        L_0x006d:
            if (r3 != 0) goto L_0x0084
            java.util.ArrayList<org.telegram.messenger.MessageObject> r1 = r9.downloadingFiles
            r1.add(r10)
            org.telegram.messenger.MessagesStorage r1 = r9.getMessagesStorage()
            org.telegram.messenger.DispatchQueue r1 = r1.getStorageQueue()
            org.telegram.messenger.DownloadController$$ExternalSyntheticLambda7 r2 = new org.telegram.messenger.DownloadController$$ExternalSyntheticLambda7
            r2.<init>(r9, r10)
            r1.postRunnable(r2)
        L_0x0084:
            org.telegram.messenger.NotificationCenter r10 = r9.getNotificationCenter()
            int r1 = org.telegram.messenger.NotificationCenter.onDownloadingFilesChanged
            java.lang.Object[] r0 = new java.lang.Object[r0]
            r10.postNotificationName(r1, r0)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.DownloadController.lambda$startDownloadFile$5(org.telegram.messenger.MessageObject):void");
    }

    /* access modifiers changed from: private */
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
            FileLog.e((Throwable) e);
        }
    }

    public void onDownloadComplete(MessageObject messageObject) {
        if (messageObject != null) {
            AndroidUtilities.runOnUIThread(new DownloadController$$ExternalSyntheticLambda5(this, messageObject));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onDownloadComplete$7(MessageObject messageObject) {
        boolean z;
        boolean z2;
        int i = 0;
        while (true) {
            z = true;
            if (i >= this.downloadingFiles.size()) {
                z2 = false;
                break;
            } else if (this.downloadingFiles.get(i).getDocument().id == messageObject.getDocument().id) {
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
                } else if (this.recentDownloadingFiles.get(i2).getDocument().id == messageObject.getDocument().id) {
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
            getMessagesStorage().getStorageQueue().postRunnable(new DownloadController$$ExternalSyntheticLambda6(this, messageObject));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onDownloadComplete$6(MessageObject messageObject) {
        try {
            getMessagesStorage().getDatabase().executeFast(String.format(Locale.ENGLISH, "UPDATE downloading_documents SET state = 1, date = %d WHERE hash = %d AND id = %d", new Object[]{Long.valueOf(System.currentTimeMillis()), Integer.valueOf(messageObject.getDocument().dc_id), Long.valueOf(messageObject.getDocument().id)})).stepThis().dispose();
            SQLiteCursor queryFinalized = getMessagesStorage().getDatabase().queryFinalized("SELECT COUNT(*) FROM downloading_documents WHERE state = 1", new Object[0]);
            int intValue = queryFinalized.next() ? queryFinalized.intValue(0) : 0;
            queryFinalized.dispose();
            SQLiteCursor queryFinalized2 = getMessagesStorage().getDatabase().queryFinalized("SELECT state FROM downloading_documents WHERE state = 1", new Object[0]);
            if (queryFinalized2.next()) {
                queryFinalized2.intValue(0);
            }
            queryFinalized2.dispose();
            if (intValue > 100) {
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
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public void onDownloadFail(MessageObject messageObject, int i) {
        if (messageObject != null) {
            AndroidUtilities.runOnUIThread(new DownloadController$$ExternalSyntheticLambda10(this, messageObject, i));
            getMessagesStorage().getStorageQueue().postRunnable(new DownloadController$$ExternalSyntheticLambda8(this, messageObject));
        }
    }

    /* access modifiers changed from: private */
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
            if (i == 0) {
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.showBulletin, 1, LocaleController.formatString("MessageNotFound", NUM, new Object[0]));
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onDownloadFail$9(MessageObject messageObject) {
        try {
            SQLitePreparedStatement executeFast = getMessagesStorage().getDatabase().executeFast("DELETE FROM downloading_documents WHERE hash = ? AND id = ?");
            executeFast.bindInteger(1, messageObject.getDocument().dc_id);
            executeFast.bindLong(2, messageObject.getDocument().id);
            executeFast.step();
            executeFast.dispose();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    private void putToUnviewedDownloads(MessageObject messageObject) {
        this.unviewedDownloads.put(messageObject.getId(), messageObject);
        AndroidUtilities.cancelRunOnUIThread(this.clearUnviewedDownloadsRunnale);
        AndroidUtilities.runOnUIThread(this.clearUnviewedDownloadsRunnale, 60000);
    }

    public void clearUnviewedDownloads() {
        this.unviewedDownloads.clear();
    }

    public void checkUnviewedDownloads(int i, long j) {
        MessageObject messageObject = this.unviewedDownloads.get(i);
        if (messageObject != null && messageObject.getDialogId() == j) {
            this.unviewedDownloads.remove(i);
            if (this.unviewedDownloads.size() == 0) {
                getNotificationCenter().postNotificationName(NotificationCenter.onDownloadingFilesChanged, new Object[0]);
            }
        }
    }

    public boolean hasUnviewedDownloads() {
        return this.unviewedDownloads.size() > 0;
    }

    private class DownloadingDocumentEntry {
        int hash;
        long id;

        private DownloadingDocumentEntry() {
        }
    }

    public void loadDownloadingFiles() {
        getMessagesStorage().getStorageQueue().postRunnable(new DownloadController$$ExternalSyntheticLambda1(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadDownloadingFiles$11() {
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
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
            FileLog.e((Throwable) e);
        }
        getFileLoader().checkMediaExistance(arrayList);
        getFileLoader().checkMediaExistance(arrayList2);
        AndroidUtilities.runOnUIThread(new DownloadController$$ExternalSyntheticLambda4(this, arrayList, arrayList2));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadDownloadingFiles$10(ArrayList arrayList, ArrayList arrayList2) {
        this.downloadingFiles.clear();
        this.downloadingFiles.addAll(arrayList);
        this.recentDownloadingFiles.clear();
        this.recentDownloadingFiles.addAll(arrayList2);
    }

    public void clearRecentDownloadedFiles() {
        this.recentDownloadingFiles.clear();
        getNotificationCenter().postNotificationName(NotificationCenter.onDownloadingFilesChanged, new Object[0]);
        getMessagesStorage().getStorageQueue().postRunnable(new DownloadController$$ExternalSyntheticLambda2(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$clearRecentDownloadedFiles$12() {
        try {
            getMessagesStorage().getDatabase().executeFast("DELETE FROM downloading_documents WHERE state = 1").stepThis().dispose();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public void deleteRecentFiles(ArrayList<MessageObject> arrayList) {
        boolean z;
        for (int i = 0; i < arrayList.size(); i++) {
            int i2 = 0;
            while (true) {
                if (i2 < this.recentDownloadingFiles.size()) {
                    if (arrayList.get(i).getId() == this.recentDownloadingFiles.get(i2).getId() && this.recentDownloadingFiles.get(i2).getDialogId() == arrayList.get(i).getDialogId()) {
                        this.recentDownloadingFiles.remove(i2);
                        z = true;
                        break;
                    }
                    i2++;
                } else {
                    z = false;
                    break;
                }
            }
            if (!z) {
                int i3 = 0;
                while (true) {
                    if (i3 < this.downloadingFiles.size()) {
                        if (arrayList.get(i).getId() == this.downloadingFiles.get(i3).getId() && this.downloadingFiles.get(i3).getDialogId() == arrayList.get(i).getDialogId()) {
                            this.downloadingFiles.remove(i3);
                            break;
                        }
                        i3++;
                    } else {
                        break;
                    }
                }
            }
            arrayList.get(i).putInDownloadsStore = false;
            FileLoader.getInstance(this.currentAccount).loadFile(arrayList.get(i).getDocument(), arrayList.get(i), 0, 0);
            FileLoader.getInstance(this.currentAccount).cancelLoadFile(arrayList.get(i).getDocument(), true);
        }
        getNotificationCenter().postNotificationName(NotificationCenter.onDownloadingFilesChanged, new Object[0]);
        getMessagesStorage().getStorageQueue().postRunnable(new DownloadController$$ExternalSyntheticLambda3(this, arrayList));
    }

    /* access modifiers changed from: private */
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
                    FileLog.e((Throwable) e);
                }
            }
            executeFast.dispose();
        } catch (Exception e2) {
            FileLog.e((Throwable) e2);
        }
    }
}
