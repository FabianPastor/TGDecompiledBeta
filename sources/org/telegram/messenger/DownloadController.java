package org.telegram.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.LongSparseArray;
import android.util.SparseArray;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.Photo;
import org.telegram.tgnet.TLRPC.TL_account_autoDownloadSettings;
import org.telegram.tgnet.TLRPC.TL_account_getAutoDownloadSettings;
import org.telegram.tgnet.TLRPC.TL_account_saveAutoDownloadSettings;
import org.telegram.tgnet.TLRPC.TL_autoDownloadSettings;
import org.telegram.tgnet.TLRPC.TL_error;

public class DownloadController extends BaseController implements NotificationCenterDelegate {
    public static final int AUTODOWNLOAD_TYPE_AUDIO = 2;
    public static final int AUTODOWNLOAD_TYPE_DOCUMENT = 8;
    public static final int AUTODOWNLOAD_TYPE_PHOTO = 1;
    public static final int AUTODOWNLOAD_TYPE_VIDEO = 4;
    private static volatile DownloadController[] Instance = new DownloadController[3];
    public static final int PRESET_NUM_CHANNEL = 3;
    public static final int PRESET_NUM_CONTACT = 0;
    public static final int PRESET_NUM_GROUP = 2;
    public static final int PRESET_NUM_PM = 1;
    public static final int PRESET_SIZE_NUM_AUDIO = 3;
    public static final int PRESET_SIZE_NUM_DOCUMENT = 2;
    public static final int PRESET_SIZE_NUM_PHOTO = 0;
    public static final int PRESET_SIZE_NUM_VIDEO = 1;
    private HashMap<String, FileDownloadProgressListener> addLaterArray = new HashMap();
    private ArrayList<DownloadObject> audioDownloadQueue = new ArrayList();
    public int currentMobilePreset;
    public int currentRoamingPreset;
    public int currentWifiPreset;
    private ArrayList<FileDownloadProgressListener> deleteLaterArray = new ArrayList();
    private ArrayList<DownloadObject> documentDownloadQueue = new ArrayList();
    private HashMap<String, DownloadObject> downloadQueueKeys = new HashMap();
    public Preset highPreset;
    private int lastCheckMask = 0;
    private int lastTag = 0;
    private boolean listenerInProgress = false;
    private boolean loadingAutoDownloadConfig;
    private HashMap<String, ArrayList<MessageObject>> loadingFileMessagesObservers = new HashMap();
    private HashMap<String, ArrayList<WeakReference<FileDownloadProgressListener>>> loadingFileObservers = new HashMap();
    public Preset lowPreset;
    public Preset mediumPreset;
    public Preset mobilePreset;
    private SparseArray<String> observersByTag = new SparseArray();
    private ArrayList<DownloadObject> photoDownloadQueue = new ArrayList();
    public Preset roamingPreset;
    private LongSparseArray<Long> typingTimes = new LongSparseArray();
    private ArrayList<DownloadObject> videoDownloadQueue = new ArrayList();
    public Preset wifiPreset;

    public interface FileDownloadProgressListener {
        int getObserverTag();

        void onFailedDownload(String str, boolean z);

        void onProgressDownload(String str, float f);

        void onProgressUpload(String str, float f, boolean z);

        void onSuccessDownload(String str);
    }

    public static class Preset {
        public boolean enabled;
        public boolean lessCallData;
        public int[] mask;
        public boolean preloadMusic;
        public boolean preloadVideo;
        public int[] sizes;

        public Preset(int[] iArr, int i, int i2, int i3, boolean z, boolean z2, boolean z3, boolean z4) {
            this.mask = new int[4];
            this.sizes = new int[4];
            int[] iArr2 = this.mask;
            System.arraycopy(iArr, 0, iArr2, 0, iArr2.length);
            iArr = this.sizes;
            iArr[0] = i;
            iArr[1] = i2;
            iArr[2] = i3;
            iArr[3] = 524288;
            this.preloadVideo = z;
            this.preloadMusic = z2;
            this.lessCallData = z4;
            this.enabled = z3;
        }

        public Preset(String str) {
            this.mask = new int[4];
            this.sizes = new int[4];
            String[] split = str.split("_");
            if (split.length >= 11) {
                boolean z = false;
                this.mask[0] = Utilities.parseInt(split[0]).intValue();
                this.mask[1] = Utilities.parseInt(split[1]).intValue();
                this.mask[2] = Utilities.parseInt(split[2]).intValue();
                this.mask[3] = Utilities.parseInt(split[3]).intValue();
                this.sizes[0] = Utilities.parseInt(split[4]).intValue();
                this.sizes[1] = Utilities.parseInt(split[5]).intValue();
                this.sizes[2] = Utilities.parseInt(split[6]).intValue();
                this.sizes[3] = Utilities.parseInt(split[7]).intValue();
                this.preloadVideo = Utilities.parseInt(split[8]).intValue() == 1;
                this.preloadMusic = Utilities.parseInt(split[9]).intValue() == 1;
                this.enabled = Utilities.parseInt(split[10]).intValue() == 1;
                if (split.length >= 12) {
                    if (Utilities.parseInt(split[11]).intValue() == 1) {
                        z = true;
                    }
                    this.lessCallData = z;
                }
            }
        }

        public void set(Preset preset) {
            int[] iArr = preset.mask;
            int[] iArr2 = this.mask;
            System.arraycopy(iArr, 0, iArr2, 0, iArr2.length);
            iArr = preset.sizes;
            iArr2 = this.sizes;
            System.arraycopy(iArr, 0, iArr2, 0, iArr2.length);
            this.preloadVideo = preset.preloadVideo;
            this.preloadMusic = preset.preloadMusic;
            this.lessCallData = preset.lessCallData;
        }

        public void set(TL_autoDownloadSettings tL_autoDownloadSettings) {
            this.preloadMusic = tL_autoDownloadSettings.audio_preload_next;
            this.preloadVideo = tL_autoDownloadSettings.video_preload_large;
            this.lessCallData = tL_autoDownloadSettings.phonecalls_less_data;
            int i = 0;
            this.sizes[0] = Math.max(512000, tL_autoDownloadSettings.photo_size_max);
            this.sizes[1] = Math.max(512000, tL_autoDownloadSettings.video_size_max);
            this.sizes[2] = Math.max(512000, tL_autoDownloadSettings.file_size_max);
            while (true) {
                int[] iArr = this.mask;
                if (i < iArr.length) {
                    if (tL_autoDownloadSettings.photo_size_max == 0 || tL_autoDownloadSettings.disabled) {
                        iArr = this.mask;
                        iArr[i] = iArr[i] & -2;
                    } else {
                        iArr[i] = iArr[i] | 1;
                    }
                    if (tL_autoDownloadSettings.video_size_max == 0 || tL_autoDownloadSettings.disabled) {
                        iArr = this.mask;
                        iArr[i] = iArr[i] & -5;
                    } else {
                        iArr = this.mask;
                        iArr[i] = iArr[i] | 4;
                    }
                    if (tL_autoDownloadSettings.file_size_max == 0 || tL_autoDownloadSettings.disabled) {
                        iArr = this.mask;
                        iArr[i] = iArr[i] & -9;
                    } else {
                        iArr = this.mask;
                        iArr[i] = iArr[i] | 8;
                    }
                    i++;
                } else {
                    return;
                }
            }
        }

        public String toString() {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(this.mask[0]);
            String str = "_";
            stringBuilder.append(str);
            stringBuilder.append(this.mask[1]);
            stringBuilder.append(str);
            stringBuilder.append(this.mask[2]);
            stringBuilder.append(str);
            stringBuilder.append(this.mask[3]);
            stringBuilder.append(str);
            stringBuilder.append(this.sizes[0]);
            stringBuilder.append(str);
            stringBuilder.append(this.sizes[1]);
            stringBuilder.append(str);
            stringBuilder.append(this.sizes[2]);
            stringBuilder.append(str);
            stringBuilder.append(this.sizes[3]);
            stringBuilder.append(str);
            stringBuilder.append(this.preloadVideo);
            stringBuilder.append(str);
            stringBuilder.append(this.preloadMusic);
            stringBuilder.append(str);
            stringBuilder.append(this.enabled);
            stringBuilder.append(str);
            stringBuilder.append(this.lessCallData);
            return stringBuilder.toString();
        }

        public boolean equals(Preset preset) {
            int[] iArr = this.mask;
            int i = iArr[0];
            int[] iArr2 = preset.mask;
            if (i != iArr2[0] || iArr[1] != iArr2[1] || iArr[2] != iArr2[2] || iArr[3] != iArr2[3]) {
                return false;
            }
            iArr = this.sizes;
            int i2 = iArr[0];
            int[] iArr3 = preset.sizes;
            return i2 == iArr3[0] && iArr[1] == iArr3[1] && iArr[2] == iArr3[2] && iArr[3] == iArr3[3] && this.preloadVideo == preset.preloadVideo && this.preloadMusic == preset.preloadMusic;
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

    static /* synthetic */ void lambda$savePresetToServer$3(TLObject tLObject, TL_error tL_error) {
    }

    public static int typeToIndex(int i) {
        return i == 1 ? 0 : i == 2 ? 3 : i == 4 ? 1 : i == 8 ? 2 : 0;
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
        SharedPreferences mainSettings = MessagesController.getMainSettings(this.currentAccount);
        this.lowPreset = new Preset(mainSettings.getString("preset0", "1_1_1_1_1048576_512000_512000_524288_0_0_1_1"));
        this.mediumPreset = new Preset(mainSettings.getString("preset1", "13_13_13_13_1048576_10485760_1048576_524288_1_1_1_0"));
        this.highPreset = new Preset(mainSettings.getString("preset2", "13_13_13_13_1048576_15728640_3145728_524288_1_1_1_0"));
        String str = "newConfig";
        boolean contains = mainSettings.contains(str);
        String str2 = "currentRoamingPreset";
        String str3 = "currentWifiPreset";
        String str4 = "currentMobilePreset";
        String str5 = "roamingPreset";
        String str6 = "wifiPreset";
        String str7 = "mobilePreset";
        if (contains || !getUserConfig().isClientActivated()) {
            String str8 = str3;
            str3 = str2;
            str2 = str8;
            this.mobilePreset = new Preset(mainSettings.getString(str7, this.mediumPreset.toString()));
            this.wifiPreset = new Preset(mainSettings.getString(str6, this.highPreset.toString()));
            this.roamingPreset = new Preset(mainSettings.getString(str5, this.lowPreset.toString()));
            this.currentMobilePreset = mainSettings.getInt(str4, 3);
            this.currentWifiPreset = mainSettings.getInt(str2, 3);
            this.currentRoamingPreset = mainSettings.getInt(str3, 3);
            if (!contains) {
                mainSettings.edit().putBoolean(str, true).commit();
            }
        } else {
            String str9;
            int i2 = 4;
            int[] iArr = new int[4];
            int[] iArr2 = new int[4];
            int[] iArr3 = new int[4];
            int[] iArr4 = new int[7];
            int[] iArr5 = new int[7];
            int[] iArr6 = new int[7];
            String str10 = str2;
            int i3 = 0;
            while (i3 < i2) {
                Object obj;
                StringBuilder stringBuilder = new StringBuilder();
                str9 = str3;
                stringBuilder.append("mobileDataDownloadMask");
                Object obj2 = "";
                if (i3 == 0) {
                    obj = obj2;
                } else {
                    obj = obj2;
                    obj2 = Integer.valueOf(i3);
                }
                stringBuilder.append(obj2);
                String stringBuilder2 = stringBuilder.toString();
                if (i3 == 0 || mainSettings.contains(stringBuilder2)) {
                    iArr[i3] = mainSettings.getInt(stringBuilder2, 13);
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("wifiDownloadMask");
                    stringBuilder.append(i3 == 0 ? obj : Integer.valueOf(i3));
                    iArr2[i3] = mainSettings.getInt(stringBuilder.toString(), 13);
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("roamingDownloadMask");
                    stringBuilder.append(i3 == 0 ? obj : Integer.valueOf(i3));
                    iArr3[i3] = mainSettings.getInt(stringBuilder.toString(), 1);
                } else {
                    iArr[i3] = iArr[0];
                    iArr2[i3] = iArr2[0];
                    iArr3[i3] = iArr3[0];
                }
                i3++;
                str3 = str9;
                i2 = 4;
            }
            str9 = str3;
            iArr4[2] = mainSettings.getInt("mobileMaxDownloadSize2", this.mediumPreset.sizes[1]);
            iArr4[3] = mainSettings.getInt("mobileMaxDownloadSize3", this.mediumPreset.sizes[2]);
            iArr5[2] = mainSettings.getInt("wifiMaxDownloadSize2", this.highPreset.sizes[1]);
            iArr5[3] = mainSettings.getInt("wifiMaxDownloadSize3", this.highPreset.sizes[2]);
            iArr6[2] = mainSettings.getInt("roamingMaxDownloadSize2", this.lowPreset.sizes[1]);
            iArr6[3] = mainSettings.getInt("roamingMaxDownloadSize3", this.lowPreset.sizes[2]);
            contains = mainSettings.getBoolean("globalAutodownloadEnabled", true);
            int[] iArr7 = iArr3;
            int[] iArr8 = iArr2;
            this.mobilePreset = new Preset(iArr, this.mediumPreset.sizes[0], iArr4[2], iArr4[3], true, true, contains, false);
            this.wifiPreset = new Preset(iArr8, this.highPreset.sizes[0], iArr5[2], iArr5[3], true, true, contains, false);
            this.roamingPreset = new Preset(iArr7, this.lowPreset.sizes[0], iArr6[2], iArr6[3], false, false, contains, true);
            Editor edit = mainSettings.edit();
            edit.putBoolean(str, true);
            edit.putString(str7, this.mobilePreset.toString());
            edit.putString(str6, this.wifiPreset.toString());
            edit.putString(str5, this.roamingPreset.toString());
            this.currentMobilePreset = 3;
            edit.putInt(str4, 3);
            this.currentWifiPreset = 3;
            edit.putInt(str9, 3);
            this.currentRoamingPreset = 3;
            edit.putInt(str10, 3);
            edit.commit();
        }
        AndroidUtilities.runOnUIThread(new -$$Lambda$DownloadController$TvQOK4BckOSg64NROgC4NLSY7xY(this));
        ApplicationLoader.applicationContext.registerReceiver(new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                DownloadController.this.checkAutodownloadSettings();
            }
        }, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        if (getUserConfig().isClientActivated()) {
            checkAutodownloadSettings();
        }
    }

    public /* synthetic */ void lambda$new$0$DownloadController() {
        getNotificationCenter().addObserver(this, NotificationCenter.fileDidFailedLoad);
        getNotificationCenter().addObserver(this, NotificationCenter.fileDidLoad);
        getNotificationCenter().addObserver(this, NotificationCenter.FileLoadProgressChanged);
        getNotificationCenter().addObserver(this, NotificationCenter.FileUploadProgressChanged);
        getNotificationCenter().addObserver(this, NotificationCenter.httpFileDidLoad);
        getNotificationCenter().addObserver(this, NotificationCenter.httpFileDidFailedLoad);
        loadAutoDownloadConfig(false);
    }

    public void loadAutoDownloadConfig(boolean z) {
        if (!this.loadingAutoDownloadConfig) {
            if (z || Math.abs(System.currentTimeMillis() - getUserConfig().autoDownloadConfigLoadTime) >= 86400000) {
                this.loadingAutoDownloadConfig = true;
                getConnectionsManager().sendRequest(new TL_account_getAutoDownloadSettings(), new -$$Lambda$DownloadController$Vy_RFVunDaT6j2u2tHT0TGLKrLk(this));
            }
        }
    }

    public /* synthetic */ void lambda$loadAutoDownloadConfig$2$DownloadController(TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$DownloadController$Sppih-WlmNUMeoDE67qxYSN9jq_E(this, tLObject));
    }

    public /* synthetic */ void lambda$null$1$DownloadController(TLObject tLObject) {
        int i = 0;
        this.loadingAutoDownloadConfig = false;
        getUserConfig().autoDownloadConfigLoadTime = System.currentTimeMillis();
        getUserConfig().saveConfig(false);
        if (tLObject != null) {
            TL_account_autoDownloadSettings tL_account_autoDownloadSettings = (TL_account_autoDownloadSettings) tLObject;
            this.lowPreset.set(tL_account_autoDownloadSettings.low);
            this.mediumPreset.set(tL_account_autoDownloadSettings.medium);
            this.highPreset.set(tL_account_autoDownloadSettings.high);
            while (i < 3) {
                Preset preset;
                if (i == 0) {
                    preset = this.mobilePreset;
                } else if (i == 1) {
                    preset = this.wifiPreset;
                } else {
                    preset = this.roamingPreset;
                }
                if (preset.equals(this.lowPreset)) {
                    preset.set(tL_account_autoDownloadSettings.low);
                } else if (preset.equals(this.mediumPreset)) {
                    preset.set(tL_account_autoDownloadSettings.medium);
                } else if (preset.equals(this.highPreset)) {
                    preset.set(tL_account_autoDownloadSettings.high);
                }
                i++;
            }
            Editor edit = MessagesController.getMainSettings(this.currentAccount).edit();
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
        this.typingTimes.clear();
    }

    public int getAutodownloadMask() {
        int[] iArr;
        if (ApplicationLoader.isConnectedToWiFi()) {
            if (!this.wifiPreset.enabled) {
                return 0;
            }
            iArr = getCurrentWiFiPreset().mask;
        } else if (ApplicationLoader.isRoaming()) {
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
            int i3 = 1;
            if ((iArr[i2] & 1) == 0) {
                i3 = 0;
            }
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

    /* Access modifiers changed, original: protected */
    public int getAutodownloadMaskAll() {
        int i = 0;
        if (!this.mobilePreset.enabled && !this.roamingPreset.enabled && !this.wifiPreset.enabled) {
            return 0;
        }
        int i2 = 0;
        while (i < 4) {
            if (!((getCurrentMobilePreset().mask[i] & 1) == 0 && (getCurrentWiFiPreset().mask[i] & 1) == 0 && (getCurrentRoamingPreset().mask[i] & 1) == 0)) {
                i2 |= 1;
            }
            if (!((getCurrentMobilePreset().mask[i] & 2) == 0 && (getCurrentWiFiPreset().mask[i] & 2) == 0 && (getCurrentRoamingPreset().mask[i] & 2) == 0)) {
                i2 |= 2;
            }
            if (!((getCurrentMobilePreset().mask[i] & 4) == 0 && (getCurrentWiFiPreset().mask[i] & 4) == 0 && (4 & getCurrentRoamingPreset().mask[i]) == 0)) {
                i2 |= 4;
            }
            if ((getCurrentMobilePreset().mask[i] & 8) != 0 || (getCurrentWiFiPreset().mask[i] & 8) != 0 || (getCurrentRoamingPreset().mask[i] & 8) != 0) {
                i2 |= 8;
            }
            i++;
        }
        return i2;
    }

    public void checkAutodownloadSettings() {
        int currentDownloadMask = getCurrentDownloadMask();
        if (currentDownloadMask != this.lastCheckMask) {
            int i;
            this.lastCheckMask = currentDownloadMask;
            if ((currentDownloadMask & 1) == 0) {
                for (i = 0; i < this.photoDownloadQueue.size(); i++) {
                    DownloadObject downloadObject = (DownloadObject) this.photoDownloadQueue.get(i);
                    TLObject tLObject = downloadObject.object;
                    if (tLObject instanceof Photo) {
                        getFileLoader().cancelLoadFile(FileLoader.getClosestPhotoSizeWithSize(((Photo) tLObject).sizes, AndroidUtilities.getPhotoSize()));
                    } else if (tLObject instanceof Document) {
                        getFileLoader().cancelLoadFile((Document) downloadObject.object);
                    }
                }
                this.photoDownloadQueue.clear();
            } else if (this.photoDownloadQueue.isEmpty()) {
                newDownloadObjectsAvailable(1);
            }
            if ((currentDownloadMask & 2) == 0) {
                for (i = 0; i < this.audioDownloadQueue.size(); i++) {
                    getFileLoader().cancelLoadFile((Document) ((DownloadObject) this.audioDownloadQueue.get(i)).object);
                }
                this.audioDownloadQueue.clear();
            } else if (this.audioDownloadQueue.isEmpty()) {
                newDownloadObjectsAvailable(2);
            }
            if ((currentDownloadMask & 8) == 0) {
                for (i = 0; i < this.documentDownloadQueue.size(); i++) {
                    getFileLoader().cancelLoadFile((Document) ((DownloadObject) this.documentDownloadQueue.get(i)).object);
                }
                this.documentDownloadQueue.clear();
            } else if (this.documentDownloadQueue.isEmpty()) {
                newDownloadObjectsAvailable(8);
            }
            if ((currentDownloadMask & 4) == 0) {
                for (currentDownloadMask = 0; currentDownloadMask < this.videoDownloadQueue.size(); currentDownloadMask++) {
                    getFileLoader().cancelLoadFile((Document) ((DownloadObject) this.videoDownloadQueue.get(currentDownloadMask)).object);
                }
                this.videoDownloadQueue.clear();
            } else if (this.videoDownloadQueue.isEmpty()) {
                newDownloadObjectsAvailable(4);
            }
            currentDownloadMask = getAutodownloadMaskAll();
            if (currentDownloadMask == 0) {
                getMessagesStorage().clearDownloadQueue(0);
            } else {
                if ((currentDownloadMask & 1) == 0) {
                    getMessagesStorage().clearDownloadQueue(1);
                }
                if ((currentDownloadMask & 2) == 0) {
                    getMessagesStorage().clearDownloadQueue(2);
                }
                if ((currentDownloadMask & 4) == 0) {
                    getMessagesStorage().clearDownloadQueue(4);
                }
                if ((currentDownloadMask & 8) == 0) {
                    getMessagesStorage().clearDownloadQueue(8);
                }
            }
        }
    }

    public boolean canDownloadMedia(MessageObject messageObject) {
        return canDownloadMedia(messageObject.messageOwner) == 1;
    }

    public boolean canDownloadMedia(int i, int i2) {
        Preset currentWiFiPreset;
        boolean z = false;
        if (ApplicationLoader.isConnectedToWiFi()) {
            if (!this.wifiPreset.enabled) {
                return false;
            }
            currentWiFiPreset = getCurrentWiFiPreset();
        } else if (ApplicationLoader.isRoaming()) {
            if (!this.roamingPreset.enabled) {
                return false;
            }
            currentWiFiPreset = getCurrentRoamingPreset();
        } else if (!this.mobilePreset.enabled) {
            return false;
        } else {
            currentWiFiPreset = getCurrentMobilePreset();
        }
        int i3 = currentWiFiPreset.mask[1];
        int i4 = currentWiFiPreset.sizes[typeToIndex(i)];
        if ((i == 1 || (i2 != 0 && i2 <= i4)) && (i == 2 || (i & i3) != 0)) {
            z = true;
        }
        return z;
    }

    /* JADX WARNING: Removed duplicated region for block: B:54:0x00b2  */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x00a6  */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x00a6  */
    /* JADX WARNING: Removed duplicated region for block: B:54:0x00b2  */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x00e1  */
    /* JADX WARNING: Removed duplicated region for block: B:54:0x00b2  */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x00a6  */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x00e1  */
    /* JADX WARNING: Missing block: B:31:0x005f, code skipped:
            if (getContactsController().contactsDict.containsKey(java.lang.Integer.valueOf(r5.user_id)) != false) goto L_0x0061;
     */
    /* JADX WARNING: Missing block: B:38:0x007b, code skipped:
            if (getContactsController().contactsDict.containsKey(java.lang.Integer.valueOf(r10.from_id)) != false) goto L_0x0061;
     */
    /* JADX WARNING: Missing block: B:45:0x009a, code skipped:
            if (getContactsController().contactsDict.containsKey(java.lang.Integer.valueOf(r10.from_id)) != false) goto L_0x0061;
     */
    public int canDownloadMedia(org.telegram.tgnet.TLRPC.Message r10) {
        /*
        r9 = this;
        r0 = 0;
        if (r10 != 0) goto L_0x0004;
    L_0x0003:
        return r0;
    L_0x0004:
        r1 = org.telegram.messenger.MessageObject.isVideoMessage(r10);
        r2 = 2;
        r3 = 1;
        if (r1 != 0) goto L_0x0046;
    L_0x000c:
        r4 = org.telegram.messenger.MessageObject.isGifMessage(r10);
        if (r4 != 0) goto L_0x0046;
    L_0x0012:
        r4 = org.telegram.messenger.MessageObject.isRoundVideoMessage(r10);
        if (r4 != 0) goto L_0x0046;
    L_0x0018:
        r4 = org.telegram.messenger.MessageObject.isGameMessage(r10);
        if (r4 == 0) goto L_0x001f;
    L_0x001e:
        goto L_0x0046;
    L_0x001f:
        r4 = org.telegram.messenger.MessageObject.isVoiceMessage(r10);
        if (r4 == 0) goto L_0x0027;
    L_0x0025:
        r4 = 2;
        goto L_0x0047;
    L_0x0027:
        r4 = org.telegram.messenger.MessageObject.isPhoto(r10);
        if (r4 != 0) goto L_0x0044;
    L_0x002d:
        r4 = org.telegram.messenger.MessageObject.isStickerMessage(r10);
        if (r4 != 0) goto L_0x0044;
    L_0x0033:
        r4 = org.telegram.messenger.MessageObject.isAnimatedStickerMessage(r10);
        if (r4 == 0) goto L_0x003a;
    L_0x0039:
        goto L_0x0044;
    L_0x003a:
        r4 = org.telegram.messenger.MessageObject.getDocument(r10);
        if (r4 == 0) goto L_0x0043;
    L_0x0040:
        r4 = 8;
        goto L_0x0047;
    L_0x0043:
        return r0;
    L_0x0044:
        r4 = 1;
        goto L_0x0047;
    L_0x0046:
        r4 = 4;
    L_0x0047:
        r5 = r10.to_id;
        if (r5 == 0) goto L_0x009f;
    L_0x004b:
        r6 = r5.user_id;
        if (r6 == 0) goto L_0x0063;
    L_0x004f:
        r6 = r9.getContactsController();
        r6 = r6.contactsDict;
        r5 = r5.user_id;
        r5 = java.lang.Integer.valueOf(r5);
        r5 = r6.containsKey(r5);
        if (r5 == 0) goto L_0x009f;
    L_0x0061:
        r5 = 0;
        goto L_0x00a0;
    L_0x0063:
        r5 = r5.chat_id;
        if (r5 == 0) goto L_0x0080;
    L_0x0067:
        r5 = r10.from_id;
        if (r5 == 0) goto L_0x007e;
    L_0x006b:
        r5 = r9.getContactsController();
        r5 = r5.contactsDict;
        r6 = r10.from_id;
        r6 = java.lang.Integer.valueOf(r6);
        r5 = r5.containsKey(r6);
        if (r5 == 0) goto L_0x007e;
    L_0x007d:
        goto L_0x0061;
    L_0x007e:
        r5 = 2;
        goto L_0x00a0;
    L_0x0080:
        r5 = org.telegram.messenger.MessageObject.isMegagroup(r10);
        if (r5 == 0) goto L_0x009d;
    L_0x0086:
        r5 = r10.from_id;
        if (r5 == 0) goto L_0x007e;
    L_0x008a:
        r5 = r9.getContactsController();
        r5 = r5.contactsDict;
        r6 = r10.from_id;
        r6 = java.lang.Integer.valueOf(r6);
        r5 = r5.containsKey(r6);
        if (r5 == 0) goto L_0x007e;
    L_0x009c:
        goto L_0x0061;
    L_0x009d:
        r5 = 3;
        goto L_0x00a0;
    L_0x009f:
        r5 = 1;
    L_0x00a0:
        r6 = org.telegram.messenger.ApplicationLoader.isConnectedToWiFi();
        if (r6 == 0) goto L_0x00b2;
    L_0x00a6:
        r6 = r9.wifiPreset;
        r6 = r6.enabled;
        if (r6 != 0) goto L_0x00ad;
    L_0x00ac:
        return r0;
    L_0x00ad:
        r6 = r9.getCurrentWiFiPreset();
        goto L_0x00cf;
    L_0x00b2:
        r6 = org.telegram.messenger.ApplicationLoader.isRoaming();
        if (r6 == 0) goto L_0x00c4;
    L_0x00b8:
        r6 = r9.roamingPreset;
        r6 = r6.enabled;
        if (r6 != 0) goto L_0x00bf;
    L_0x00be:
        return r0;
    L_0x00bf:
        r6 = r9.getCurrentRoamingPreset();
        goto L_0x00cf;
    L_0x00c4:
        r6 = r9.mobilePreset;
        r6 = r6.enabled;
        if (r6 != 0) goto L_0x00cb;
    L_0x00ca:
        return r0;
    L_0x00cb:
        r6 = r9.getCurrentMobilePreset();
    L_0x00cf:
        r7 = r6.mask;
        r5 = r7[r5];
        r7 = r6.sizes;
        r8 = typeToIndex(r4);
        r7 = r7[r8];
        r10 = org.telegram.messenger.MessageObject.getMessageSize(r10);
        if (r1 == 0) goto L_0x00f1;
    L_0x00e1:
        r1 = r6.preloadVideo;
        if (r1 == 0) goto L_0x00f1;
    L_0x00e5:
        if (r10 <= r7) goto L_0x00f1;
    L_0x00e7:
        r1 = 2097152; // 0x200000 float:2.938736E-39 double:1.0361308E-317;
        if (r7 <= r1) goto L_0x00f1;
    L_0x00eb:
        r10 = r5 & r4;
        if (r10 == 0) goto L_0x00f0;
    L_0x00ef:
        r0 = 2;
    L_0x00f0:
        return r0;
    L_0x00f1:
        if (r4 == r3) goto L_0x00f7;
    L_0x00f3:
        if (r10 == 0) goto L_0x00fe;
    L_0x00f5:
        if (r10 > r7) goto L_0x00fe;
    L_0x00f7:
        if (r4 == r2) goto L_0x00fd;
    L_0x00f9:
        r10 = r5 & r4;
        if (r10 == 0) goto L_0x00fe;
    L_0x00fd:
        r0 = 1;
    L_0x00fe:
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.DownloadController.canDownloadMedia(org.telegram.tgnet.TLRPC$Message):int");
    }

    /* Access modifiers changed, original: protected */
    public boolean canDownloadNextTrack() {
        boolean z = true;
        if (ApplicationLoader.isConnectedToWiFi()) {
            if (!(this.wifiPreset.enabled && getCurrentWiFiPreset().preloadMusic)) {
                z = false;
            }
            return z;
        } else if (ApplicationLoader.isRoaming()) {
            if (!(this.roamingPreset.enabled && getCurrentRoamingPreset().preloadMusic)) {
                z = false;
            }
            return z;
        } else {
            if (!(this.mobilePreset.enabled && getCurrentMobilePreset().preloadMusic)) {
                z = false;
            }
            return z;
        }
    }

    public int getCurrentDownloadMask() {
        int i = 0;
        int i2;
        if (ApplicationLoader.isConnectedToWiFi()) {
            if (!this.wifiPreset.enabled) {
                return 0;
            }
            i2 = 0;
            while (i < 4) {
                i2 |= getCurrentWiFiPreset().mask[i];
                i++;
            }
            return i2;
        } else if (ApplicationLoader.isRoaming()) {
            if (!this.roamingPreset.enabled) {
                return 0;
            }
            i2 = 0;
            while (i < 4) {
                i2 |= getCurrentRoamingPreset().mask[i];
                i++;
            }
            return i2;
        } else if (!this.mobilePreset.enabled) {
            return 0;
        } else {
            i2 = 0;
            while (i < 4) {
                i2 |= getCurrentMobilePreset().mask[i];
                i++;
            }
            return i2;
        }
    }

    public void savePresetToServer(int i) {
        Preset currentMobilePreset;
        boolean z;
        TL_account_saveAutoDownloadSettings tL_account_saveAutoDownloadSettings = new TL_account_saveAutoDownloadSettings();
        if (i == 0) {
            currentMobilePreset = getCurrentMobilePreset();
            z = this.mobilePreset.enabled;
        } else if (i == 1) {
            currentMobilePreset = getCurrentWiFiPreset();
            z = this.wifiPreset.enabled;
        } else {
            currentMobilePreset = getCurrentRoamingPreset();
            z = this.roamingPreset.enabled;
        }
        tL_account_saveAutoDownloadSettings.settings = new TL_autoDownloadSettings();
        TL_autoDownloadSettings tL_autoDownloadSettings = tL_account_saveAutoDownloadSettings.settings;
        tL_autoDownloadSettings.audio_preload_next = currentMobilePreset.preloadMusic;
        tL_autoDownloadSettings.video_preload_large = currentMobilePreset.preloadVideo;
        tL_autoDownloadSettings.phonecalls_less_data = currentMobilePreset.lessCallData;
        int i2 = 0;
        tL_autoDownloadSettings.disabled = !z;
        int i3 = 0;
        Object obj = null;
        Object obj2 = null;
        Object obj3 = null;
        while (true) {
            int[] iArr = currentMobilePreset.mask;
            if (i3 < iArr.length) {
                if ((iArr[i3] & 1) != 0) {
                    obj = 1;
                }
                if ((currentMobilePreset.mask[i3] & 4) != 0) {
                    obj2 = 1;
                }
                if ((currentMobilePreset.mask[i3] & 8) != 0) {
                    obj3 = 1;
                }
                if (obj != null && r5 != null && r6 != null) {
                    break;
                }
                i3++;
            } else {
                break;
            }
        }
        tL_account_saveAutoDownloadSettings.settings.photo_size_max = obj != null ? currentMobilePreset.sizes[0] : 0;
        tL_account_saveAutoDownloadSettings.settings.video_size_max = obj2 != null ? currentMobilePreset.sizes[1] : 0;
        TL_autoDownloadSettings tL_autoDownloadSettings2 = tL_account_saveAutoDownloadSettings.settings;
        if (obj3 != null) {
            i2 = currentMobilePreset.sizes[2];
        }
        tL_autoDownloadSettings2.file_size_max = i2;
        getConnectionsManager().sendRequest(tL_account_saveAutoDownloadSettings, -$$Lambda$DownloadController$0LtKveHOl8NLZKx-EDiX80oSJa0.INSTANCE);
    }

    /* Access modifiers changed, original: protected */
    /* JADX WARNING: Removed duplicated region for block: B:53:0x00b5 A:{SYNTHETIC} */
    /* JADX WARNING: Removed duplicated region for block: B:47:0x00ad  */
    public void processDownloadObjects(int r20, java.util.ArrayList<org.telegram.messenger.DownloadObject> r21) {
        /*
        r19 = this;
        r0 = r19;
        r1 = r20;
        r2 = r21.isEmpty();
        if (r2 == 0) goto L_0x000b;
    L_0x000a:
        return;
    L_0x000b:
        r2 = 2;
        r3 = 0;
        r4 = 1;
        if (r1 != r4) goto L_0x0013;
    L_0x0010:
        r1 = r0.photoDownloadQueue;
        goto L_0x0026;
    L_0x0013:
        if (r1 != r2) goto L_0x0018;
    L_0x0015:
        r1 = r0.audioDownloadQueue;
        goto L_0x0026;
    L_0x0018:
        r5 = 4;
        if (r1 != r5) goto L_0x001e;
    L_0x001b:
        r1 = r0.videoDownloadQueue;
        goto L_0x0026;
    L_0x001e:
        r5 = 8;
        if (r1 != r5) goto L_0x0025;
    L_0x0022:
        r1 = r0.documentDownloadQueue;
        goto L_0x0026;
    L_0x0025:
        r1 = r3;
    L_0x0026:
        r5 = 0;
        r6 = 0;
    L_0x0028:
        r7 = r21.size();
        if (r6 >= r7) goto L_0x00b9;
    L_0x002e:
        r7 = r21;
        r8 = r7.get(r6);
        r8 = (org.telegram.messenger.DownloadObject) r8;
        r9 = r8.object;
        r10 = r9 instanceof org.telegram.tgnet.TLRPC.Document;
        if (r10 == 0) goto L_0x0044;
    L_0x003c:
        r9 = (org.telegram.tgnet.TLRPC.Document) r9;
        r9 = org.telegram.messenger.FileLoader.getAttachFileName(r9);
        r10 = r3;
        goto L_0x005d;
    L_0x0044:
        r10 = r9 instanceof org.telegram.tgnet.TLRPC.Photo;
        if (r10 == 0) goto L_0x005b;
    L_0x0048:
        r9 = org.telegram.messenger.FileLoader.getAttachFileName(r9);
        r10 = r8.object;
        r10 = (org.telegram.tgnet.TLRPC.Photo) r10;
        r10 = r10.sizes;
        r11 = org.telegram.messenger.AndroidUtilities.getPhotoSize();
        r10 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r10, r11);
        goto L_0x005d;
    L_0x005b:
        r9 = r3;
        r10 = r9;
    L_0x005d:
        if (r9 == 0) goto L_0x00b5;
    L_0x005f:
        r11 = r0.downloadQueueKeys;
        r11 = r11.containsKey(r9);
        if (r11 == 0) goto L_0x0068;
    L_0x0067:
        goto L_0x00b5;
    L_0x0068:
        if (r10 == 0) goto L_0x0090;
    L_0x006a:
        r11 = r8.object;
        r11 = (org.telegram.tgnet.TLRPC.Photo) r11;
        r12 = r8.secret;
        if (r12 == 0) goto L_0x0075;
    L_0x0072:
        r18 = 2;
        goto L_0x007e;
    L_0x0075:
        r12 = r8.forceCache;
        if (r12 == 0) goto L_0x007c;
    L_0x0079:
        r18 = 1;
        goto L_0x007e;
    L_0x007c:
        r18 = 0;
    L_0x007e:
        r13 = r19.getFileLoader();
        r14 = org.telegram.messenger.ImageLocation.getForPhoto(r10, r11);
        r15 = r8.parent;
        r16 = 0;
        r17 = 0;
        r13.loadFile(r14, r15, r16, r17, r18);
        goto L_0x00a8;
    L_0x0090:
        r10 = r8.object;
        r11 = r10 instanceof org.telegram.tgnet.TLRPC.Document;
        if (r11 == 0) goto L_0x00aa;
    L_0x0096:
        r10 = (org.telegram.tgnet.TLRPC.Document) r10;
        r11 = r19.getFileLoader();
        r12 = r8.parent;
        r13 = r8.secret;
        if (r13 == 0) goto L_0x00a4;
    L_0x00a2:
        r13 = 2;
        goto L_0x00a5;
    L_0x00a4:
        r13 = 0;
    L_0x00a5:
        r11.loadFile(r10, r12, r5, r13);
    L_0x00a8:
        r10 = 1;
        goto L_0x00ab;
    L_0x00aa:
        r10 = 0;
    L_0x00ab:
        if (r10 == 0) goto L_0x00b5;
    L_0x00ad:
        r1.add(r8);
        r10 = r0.downloadQueueKeys;
        r10.put(r9, r8);
    L_0x00b5:
        r6 = r6 + 1;
        goto L_0x0028;
    L_0x00b9:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.DownloadController.processDownloadObjects(int, java.util.ArrayList):void");
    }

    /* Access modifiers changed, original: protected */
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
        DownloadObject downloadObject = (DownloadObject) this.downloadQueueKeys.get(str);
        if (downloadObject != null) {
            this.downloadQueueKeys.remove(str);
            if (i == 0 || i == 2) {
                getMessagesStorage().removeFromDownloadQueue(downloadObject.id, downloadObject.type, false);
            }
            i = downloadObject.type;
            if (i == 1) {
                this.photoDownloadQueue.remove(downloadObject);
                if (this.photoDownloadQueue.isEmpty()) {
                    newDownloadObjectsAvailable(1);
                }
            } else if (i == 2) {
                this.audioDownloadQueue.remove(downloadObject);
                if (this.audioDownloadQueue.isEmpty()) {
                    newDownloadObjectsAvailable(2);
                }
            } else if (i == 4) {
                this.videoDownloadQueue.remove(downloadObject);
                if (this.videoDownloadQueue.isEmpty()) {
                    newDownloadObjectsAvailable(4);
                }
            } else if (i == 8) {
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
        addLoadingFileObserver(str, null, fileDownloadProgressListener);
    }

    public void addLoadingFileObserver(String str, MessageObject messageObject, FileDownloadProgressListener fileDownloadProgressListener) {
        if (this.listenerInProgress) {
            this.addLaterArray.put(str, fileDownloadProgressListener);
            return;
        }
        removeLoadingFileObserver(fileDownloadProgressListener);
        ArrayList arrayList = (ArrayList) this.loadingFileObservers.get(str);
        if (arrayList == null) {
            arrayList = new ArrayList();
            this.loadingFileObservers.put(str, arrayList);
        }
        arrayList.add(new WeakReference(fileDownloadProgressListener));
        if (messageObject != null) {
            arrayList = (ArrayList) this.loadingFileMessagesObservers.get(str);
            if (arrayList == null) {
                arrayList = new ArrayList();
                this.loadingFileMessagesObservers.put(str, arrayList);
            }
            arrayList.add(messageObject);
        }
        this.observersByTag.put(fileDownloadProgressListener.getObserverTag(), str);
    }

    public void removeLoadingFileObserver(FileDownloadProgressListener fileDownloadProgressListener) {
        if (this.listenerInProgress) {
            this.deleteLaterArray.add(fileDownloadProgressListener);
            return;
        }
        String str = (String) this.observersByTag.get(fileDownloadProgressListener.getObserverTag());
        if (str != null) {
            ArrayList arrayList = (ArrayList) this.loadingFileObservers.get(str);
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
        for (Entry entry : this.addLaterArray.entrySet()) {
            addLoadingFileObserver((String) entry.getKey(), (FileDownloadProgressListener) entry.getValue());
        }
        this.addLaterArray.clear();
        Iterator it = this.deleteLaterArray.iterator();
        while (it.hasNext()) {
            removeLoadingFileObserver((FileDownloadProgressListener) it.next());
        }
        this.deleteLaterArray.clear();
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        String str;
        int size;
        int i3;
        WeakReference weakReference;
        ArrayList arrayList;
        int size2;
        int i4;
        if (i == NotificationCenter.fileDidFailedLoad || i == NotificationCenter.httpFileDidFailedLoad) {
            str = (String) objArr[0];
            Integer num = (Integer) objArr[1];
            this.listenerInProgress = true;
            ArrayList arrayList2 = (ArrayList) this.loadingFileObservers.get(str);
            if (arrayList2 != null) {
                size = arrayList2.size();
                for (i3 = 0; i3 < size; i3++) {
                    weakReference = (WeakReference) arrayList2.get(i3);
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
        } else if (i == NotificationCenter.fileDidLoad || i == NotificationCenter.httpFileDidLoad) {
            this.listenerInProgress = true;
            str = (String) objArr[0];
            arrayList = (ArrayList) this.loadingFileMessagesObservers.get(str);
            if (arrayList != null) {
                size2 = arrayList.size();
                for (size = 0; size < size2; size++) {
                    ((MessageObject) arrayList.get(size)).mediaExists = true;
                }
                this.loadingFileMessagesObservers.remove(str);
            }
            arrayList = (ArrayList) this.loadingFileObservers.get(str);
            if (arrayList != null) {
                size2 = arrayList.size();
                for (i4 = 0; i4 < size2; i4++) {
                    WeakReference weakReference2 = (WeakReference) arrayList.get(i4);
                    if (weakReference2.get() != null) {
                        ((FileDownloadProgressListener) weakReference2.get()).onSuccessDownload(str);
                        this.observersByTag.remove(((FileDownloadProgressListener) weakReference2.get()).getObserverTag());
                    }
                }
                this.loadingFileObservers.remove(str);
            }
            this.listenerInProgress = false;
            processLaterArrays();
            checkDownloadFinished(str, 0);
        } else if (i == NotificationCenter.FileLoadProgressChanged) {
            this.listenerInProgress = true;
            str = (String) objArr[0];
            arrayList = (ArrayList) this.loadingFileObservers.get(str);
            if (arrayList != null) {
                Float f = (Float) objArr[1];
                i4 = arrayList.size();
                for (size = 0; size < i4; size++) {
                    WeakReference weakReference3 = (WeakReference) arrayList.get(size);
                    if (weakReference3.get() != null) {
                        ((FileDownloadProgressListener) weakReference3.get()).onProgressDownload(str, f.floatValue());
                    }
                }
            }
            this.listenerInProgress = false;
            processLaterArrays();
        } else if (i == NotificationCenter.FileUploadProgressChanged) {
            this.listenerInProgress = true;
            str = (String) objArr[0];
            arrayList = (ArrayList) this.loadingFileObservers.get(str);
            if (arrayList != null) {
                Float f2 = (Float) objArr[1];
                Boolean bool = (Boolean) objArr[2];
                size = arrayList.size();
                for (i3 = 0; i3 < size; i3++) {
                    weakReference = (WeakReference) arrayList.get(i3);
                    if (weakReference.get() != null) {
                        ((FileDownloadProgressListener) weakReference.get()).onProgressUpload(str, f2.floatValue(), bool.booleanValue());
                    }
                }
            }
            this.listenerInProgress = false;
            processLaterArrays();
            try {
                arrayList = getSendMessagesHelper().getDelayedMessages(str);
                if (arrayList != null) {
                    for (size2 = 0; size2 < arrayList.size(); size2++) {
                        DelayedMessage delayedMessage = (DelayedMessage) arrayList.get(size2);
                        if (delayedMessage.encryptedChat == null) {
                            long j = delayedMessage.peer;
                            Long l;
                            if (delayedMessage.type == 4) {
                                l = (Long) this.typingTimes.get(j);
                                if (l == null || l.longValue() + 4000 < System.currentTimeMillis()) {
                                    HashMap hashMap = delayedMessage.extraHashMap;
                                    StringBuilder stringBuilder = new StringBuilder();
                                    stringBuilder.append(str);
                                    stringBuilder.append("_i");
                                    MessageObject messageObject = (MessageObject) hashMap.get(stringBuilder.toString());
                                    if (messageObject == null || !messageObject.isVideo()) {
                                        getMessagesController().sendTyping(j, 4, 0);
                                    } else {
                                        getMessagesController().sendTyping(j, 5, 0);
                                    }
                                    this.typingTimes.put(j, Long.valueOf(System.currentTimeMillis()));
                                }
                            } else {
                                l = (Long) this.typingTimes.get(j);
                                delayedMessage.obj.getDocument();
                                if (l == null || l.longValue() + 4000 < System.currentTimeMillis()) {
                                    if (delayedMessage.obj.isRoundVideo()) {
                                        getMessagesController().sendTyping(j, 8, 0);
                                    } else if (delayedMessage.obj.isVideo()) {
                                        getMessagesController().sendTyping(j, 5, 0);
                                    } else if (delayedMessage.obj.isVoice()) {
                                        getMessagesController().sendTyping(j, 9, 0);
                                    } else if (delayedMessage.obj.getDocument() != null) {
                                        getMessagesController().sendTyping(j, 3, 0);
                                    } else if (delayedMessage.photoSize != null) {
                                        getMessagesController().sendTyping(j, 4, 0);
                                    }
                                    this.typingTimes.put(j, Long.valueOf(System.currentTimeMillis()));
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }
}
