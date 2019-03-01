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
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.Peer;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_account_autoDownloadSettings;
import org.telegram.tgnet.TLRPC.TL_account_getAutoDownloadSettings;
import org.telegram.tgnet.TLRPC.TL_account_saveAutoDownloadSettings;
import org.telegram.tgnet.TLRPC.TL_autoDownloadSettings;
import org.telegram.tgnet.TLRPC.TL_error;

public class DownloadController implements NotificationCenterDelegate {
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
    private int currentAccount;
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

        public Preset(int m, int p, int v, int f, boolean pv, boolean pm, boolean e, boolean l) {
            this.mask = new int[4];
            this.sizes = new int[4];
            for (int a = 0; a < this.mask.length; a++) {
                this.mask[a] = m;
            }
            this.sizes[0] = p;
            this.sizes[1] = v;
            this.sizes[2] = f;
            this.sizes[3] = 524288;
            this.preloadVideo = pv;
            this.preloadMusic = pm;
            this.lessCallData = l;
            this.enabled = e;
        }

        public Preset(int[] m, int p, int v, int f, boolean pv, boolean pm, boolean e, boolean l) {
            this.mask = new int[4];
            this.sizes = new int[4];
            System.arraycopy(m, 0, this.mask, 0, this.mask.length);
            this.sizes[0] = p;
            this.sizes[1] = v;
            this.sizes[2] = f;
            this.sizes[3] = 524288;
            this.preloadVideo = pv;
            this.preloadMusic = pm;
            this.lessCallData = l;
            this.enabled = e;
        }

        public Preset(String str) {
            boolean z = true;
            this.mask = new int[4];
            this.sizes = new int[4];
            String[] args = str.split("_");
            if (args.length >= 11) {
                boolean z2;
                this.mask[0] = Utilities.parseInt(args[0]).intValue();
                this.mask[1] = Utilities.parseInt(args[1]).intValue();
                this.mask[2] = Utilities.parseInt(args[2]).intValue();
                this.mask[3] = Utilities.parseInt(args[3]).intValue();
                this.sizes[0] = Utilities.parseInt(args[4]).intValue();
                this.sizes[1] = Utilities.parseInt(args[5]).intValue();
                this.sizes[2] = Utilities.parseInt(args[6]).intValue();
                this.sizes[3] = Utilities.parseInt(args[7]).intValue();
                this.preloadVideo = Utilities.parseInt(args[8]).intValue() == 1;
                if (Utilities.parseInt(args[9]).intValue() == 1) {
                    z2 = true;
                } else {
                    z2 = false;
                }
                this.preloadMusic = z2;
                if (Utilities.parseInt(args[10]).intValue() == 1) {
                    z2 = true;
                } else {
                    z2 = false;
                }
                this.enabled = z2;
                if (args.length >= 12) {
                    if (Utilities.parseInt(args[11]).intValue() != 1) {
                        z = false;
                    }
                    this.lessCallData = z;
                }
            }
        }

        public void set(Preset preset) {
            System.arraycopy(preset.mask, 0, this.mask, 0, this.mask.length);
            System.arraycopy(preset.sizes, 0, this.sizes, 0, this.sizes.length);
            this.preloadVideo = preset.preloadVideo;
            this.preloadMusic = preset.preloadMusic;
            this.lessCallData = preset.lessCallData;
        }

        public void set(TL_autoDownloadSettings settings) {
            this.preloadMusic = settings.audio_preload_next;
            this.preloadVideo = settings.video_preload_large;
            this.lessCallData = settings.phonecalls_less_data;
            this.sizes[0] = Math.max(512000, settings.photo_size_max);
            this.sizes[1] = Math.max(512000, settings.video_size_max);
            this.sizes[2] = Math.max(512000, settings.file_size_max);
            for (int a = 0; a < this.mask.length; a++) {
                int[] iArr;
                if (settings.photo_size_max == 0 || settings.disabled) {
                    iArr = this.mask;
                    iArr[a] = iArr[a] & -2;
                } else {
                    iArr = this.mask;
                    iArr[a] = iArr[a] | 1;
                }
                if (settings.video_size_max == 0 || settings.disabled) {
                    iArr = this.mask;
                    iArr[a] = iArr[a] & -5;
                } else {
                    iArr = this.mask;
                    iArr[a] = iArr[a] | 4;
                }
                if (settings.file_size_max == 0 || settings.disabled) {
                    iArr = this.mask;
                    iArr[a] = iArr[a] & -9;
                } else {
                    iArr = this.mask;
                    iArr[a] = iArr[a] | 8;
                }
            }
        }

        public String toString() {
            int i = 1;
            StringBuilder append = new StringBuilder().append(this.mask[0]).append("_").append(this.mask[1]).append("_").append(this.mask[2]).append("_").append(this.mask[3]).append("_").append(this.sizes[0]).append("_").append(this.sizes[1]).append("_").append(this.sizes[2]).append("_").append(this.sizes[3]).append("_").append(this.preloadVideo ? 1 : 0).append("_").append(this.preloadMusic ? 1 : 0).append("_").append(this.enabled ? 1 : 0).append("_");
            if (!this.lessCallData) {
                i = 0;
            }
            return append.append(i).toString();
        }

        public boolean equals(Preset obj) {
            return this.mask[0] == obj.mask[0] && this.mask[1] == obj.mask[1] && this.mask[2] == obj.mask[2] && this.mask[3] == obj.mask[3] && this.sizes[0] == obj.sizes[0] && this.sizes[1] == obj.sizes[1] && this.sizes[2] == obj.sizes[2] && this.sizes[3] == obj.sizes[3] && this.preloadVideo == obj.preloadVideo && this.preloadMusic == obj.preloadMusic;
        }
    }

    public static DownloadController getInstance(int num) {
        Throwable th;
        DownloadController localInstance = Instance[num];
        if (localInstance == null) {
            synchronized (DownloadController.class) {
                try {
                    localInstance = Instance[num];
                    if (localInstance == null) {
                        DownloadController[] downloadControllerArr = Instance;
                        DownloadController localInstance2 = new DownloadController(num);
                        try {
                            downloadControllerArr[num] = localInstance2;
                            localInstance = localInstance2;
                        } catch (Throwable th2) {
                            th = th2;
                            localInstance = localInstance2;
                            throw th;
                        }
                    }
                } catch (Throwable th3) {
                    th = th3;
                    throw th;
                }
            }
        }
        return localInstance;
    }

    public DownloadController(int instance) {
        this.currentAccount = instance;
        SharedPreferences preferences = MessagesController.getMainSettings(this.currentAccount);
        this.lowPreset = new Preset(preferences.getString("preset0", "1_1_1_1_1048576_512000_512000_524288_0_0_1_1"));
        this.mediumPreset = new Preset(preferences.getString("preset1", "13_13_13_13_1048576_10485760_1048576_524288_1_1_1_0"));
        this.highPreset = new Preset(preferences.getString("preset2", "13_13_13_13_1048576_15728640_3145728_524288_1_1_1_0"));
        boolean newConfig = preferences.contains("newConfig") || !UserConfig.getInstance(this.currentAccount).isClientActivated();
        if (newConfig) {
            this.mobilePreset = new Preset(preferences.getString("mobilePreset", this.mediumPreset.toString()));
            this.wifiPreset = new Preset(preferences.getString("wifiPreset", this.highPreset.toString()));
            this.roamingPreset = new Preset(preferences.getString("roamingPreset", this.lowPreset.toString()));
            this.currentMobilePreset = preferences.getInt("currentMobilePreset", 3);
            this.currentWifiPreset = preferences.getInt("currentWifiPreset", 3);
            this.currentRoamingPreset = preferences.getInt("currentRoamingPreset", 3);
            if (!newConfig) {
                preferences.edit().putBoolean("newConfig", true).commit();
            }
        } else {
            int[] mobileDataDownloadMask = new int[4];
            int[] wifiDownloadMask = new int[4];
            int[] roamingDownloadMask = new int[4];
            int[] mobileMaxFileSize = new int[7];
            int[] wifiMaxFileSize = new int[7];
            int[] roamingMaxFileSize = new int[7];
            int a = 0;
            while (a < 4) {
                String key = "mobileDataDownloadMask" + (a == 0 ? "" : Integer.valueOf(a));
                if (a == 0 || preferences.contains(key)) {
                    Object obj;
                    mobileDataDownloadMask[a] = preferences.getInt(key, 13);
                    wifiDownloadMask[a] = preferences.getInt("wifiDownloadMask" + (a == 0 ? "" : Integer.valueOf(a)), 13);
                    StringBuilder append = new StringBuilder().append("roamingDownloadMask");
                    if (a == 0) {
                        obj = "";
                    } else {
                        obj = Integer.valueOf(a);
                    }
                    roamingDownloadMask[a] = preferences.getInt(append.append(obj).toString(), 1);
                } else {
                    mobileDataDownloadMask[a] = mobileDataDownloadMask[0];
                    wifiDownloadMask[a] = wifiDownloadMask[0];
                    roamingDownloadMask[a] = roamingDownloadMask[0];
                }
                a++;
            }
            mobileMaxFileSize[2] = preferences.getInt("mobileMaxDownloadSize2", this.mediumPreset.sizes[1]);
            mobileMaxFileSize[3] = preferences.getInt("mobileMaxDownloadSize3", this.mediumPreset.sizes[2]);
            wifiMaxFileSize[2] = preferences.getInt("wifiMaxDownloadSize2", this.highPreset.sizes[1]);
            wifiMaxFileSize[3] = preferences.getInt("wifiMaxDownloadSize3", this.highPreset.sizes[2]);
            roamingMaxFileSize[2] = preferences.getInt("roamingMaxDownloadSize2", this.lowPreset.sizes[1]);
            roamingMaxFileSize[3] = preferences.getInt("roamingMaxDownloadSize3", this.lowPreset.sizes[2]);
            boolean globalAutodownloadEnabled = preferences.getBoolean("globalAutodownloadEnabled", true);
            this.mobilePreset = new Preset(mobileDataDownloadMask, this.mediumPreset.sizes[0], mobileMaxFileSize[2], mobileMaxFileSize[3], true, true, globalAutodownloadEnabled, false);
            this.wifiPreset = new Preset(wifiDownloadMask, this.highPreset.sizes[0], wifiMaxFileSize[2], wifiMaxFileSize[3], true, true, globalAutodownloadEnabled, false);
            this.roamingPreset = new Preset(roamingDownloadMask, this.lowPreset.sizes[0], roamingMaxFileSize[2], roamingMaxFileSize[3], false, false, globalAutodownloadEnabled, true);
            Editor editor = preferences.edit();
            editor.putBoolean("newConfig", true);
            editor.putString("mobilePreset", this.mobilePreset.toString());
            editor.putString("wifiPreset", this.wifiPreset.toString());
            editor.putString("roamingPreset", this.roamingPreset.toString());
            this.currentMobilePreset = 3;
            editor.putInt("currentMobilePreset", 3);
            this.currentWifiPreset = 3;
            editor.putInt("currentWifiPreset", 3);
            this.currentRoamingPreset = 3;
            editor.putInt("currentRoamingPreset", 3);
            editor.commit();
        }
        AndroidUtilities.runOnUIThread(new DownloadController$$Lambda$0(this));
        ApplicationLoader.applicationContext.registerReceiver(new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                DownloadController.this.checkAutodownloadSettings();
            }
        }, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        if (UserConfig.getInstance(this.currentAccount).isClientActivated()) {
            checkAutodownloadSettings();
        }
    }

    final /* synthetic */ void lambda$new$0$DownloadController() {
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileDidFailedLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.FileLoadProgressChanged);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.FileUploadProgressChanged);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.httpFileDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.httpFileDidFailedLoad);
        loadAutoDownloadConfig(false);
    }

    public void loadAutoDownloadConfig(boolean force) {
        if (!this.loadingAutoDownloadConfig) {
            if (force || Math.abs(System.currentTimeMillis() - UserConfig.getInstance(this.currentAccount).autoDownloadConfigLoadTime) >= 86400000) {
                this.loadingAutoDownloadConfig = true;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_account_getAutoDownloadSettings(), new DownloadController$$Lambda$1(this));
            }
        }
    }

    final /* synthetic */ void lambda$loadAutoDownloadConfig$2$DownloadController(TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new DownloadController$$Lambda$3(this, response));
    }

    final /* synthetic */ void lambda$null$1$DownloadController(TLObject response) {
        this.loadingAutoDownloadConfig = false;
        UserConfig.getInstance(this.currentAccount).autoDownloadConfigLoadTime = System.currentTimeMillis();
        UserConfig.getInstance(this.currentAccount).saveConfig(false);
        if (response != null) {
            TL_account_autoDownloadSettings res = (TL_account_autoDownloadSettings) response;
            this.lowPreset.set(res.low);
            this.mediumPreset.set(res.medium);
            this.highPreset.set(res.high);
            for (int a = 0; a < 3; a++) {
                Preset preset;
                if (a == 0) {
                    preset = this.mobilePreset;
                } else if (a == 1) {
                    preset = this.wifiPreset;
                } else {
                    preset = this.roamingPreset;
                }
                if (preset.equals(this.lowPreset)) {
                    preset.set(res.low);
                } else if (preset.equals(this.mediumPreset)) {
                    preset.set(res.medium);
                } else if (preset.equals(this.highPreset)) {
                    preset.set(res.high);
                }
            }
            Editor editor = MessagesController.getMainSettings(this.currentAccount).edit();
            editor.putString("mobilePreset", this.mobilePreset.toString());
            editor.putString("wifiPreset", this.wifiPreset.toString());
            editor.putString("roamingPreset", this.roamingPreset.toString());
            editor.putString("preset0", this.lowPreset.toString());
            editor.putString("preset1", this.mediumPreset.toString());
            editor.putString("preset2", this.highPreset.toString());
            editor.commit();
            String str1 = this.lowPreset.toString();
            String str2 = this.mediumPreset.toString();
            String str3 = this.highPreset.toString();
            checkAutodownloadSettings();
        }
    }

    public Preset getCurrentMobilePreset() {
        if (this.currentMobilePreset == 0) {
            return this.lowPreset;
        }
        if (this.currentMobilePreset == 1) {
            return this.mediumPreset;
        }
        if (this.currentMobilePreset == 2) {
            return this.highPreset;
        }
        return this.mobilePreset;
    }

    public Preset getCurrentWiFiPreset() {
        if (this.currentWifiPreset == 0) {
            return this.lowPreset;
        }
        if (this.currentWifiPreset == 1) {
            return this.mediumPreset;
        }
        if (this.currentWifiPreset == 2) {
            return this.highPreset;
        }
        return this.wifiPreset;
    }

    public Preset getCurrentRoamingPreset() {
        if (this.currentRoamingPreset == 0) {
            return this.lowPreset;
        }
        if (this.currentRoamingPreset == 1) {
            return this.mediumPreset;
        }
        if (this.currentRoamingPreset == 2) {
            return this.highPreset;
        }
        return this.roamingPreset;
    }

    public static int typeToIndex(int type) {
        if (type == 1) {
            return 0;
        }
        if (type == 2) {
            return 3;
        }
        if (type == 4) {
            return 1;
        }
        if (type == 8) {
            return 2;
        }
        return 0;
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
        int[] masksArray;
        int result;
        int result2 = 0;
        if (ApplicationLoader.isConnectedToWiFi()) {
            if (this.wifiPreset.enabled) {
                masksArray = getCurrentWiFiPreset().mask;
            } else {
                result = 0;
                return 0;
            }
        } else if (ApplicationLoader.isRoaming()) {
            if (this.roamingPreset.enabled) {
                masksArray = getCurrentRoamingPreset().mask;
            } else {
                result = 0;
                return 0;
            }
        } else if (this.mobilePreset.enabled) {
            masksArray = getCurrentMobilePreset().mask;
        } else {
            result = 0;
            return 0;
        }
        for (int a = 0; a < masksArray.length; a++) {
            int mask = 0;
            if ((masksArray[a] & 1) != 0) {
                mask = 0 | 1;
            }
            if ((masksArray[a] & 2) != 0) {
                mask |= 2;
            }
            if ((masksArray[a] & 4) != 0) {
                mask |= 4;
            }
            if ((masksArray[a] & 8) != 0) {
                mask |= 8;
            }
            result2 |= mask << (a * 8);
        }
        result = result2;
        return result2;
    }

    protected int getAutodownloadMaskAll() {
        if (!this.mobilePreset.enabled && !this.roamingPreset.enabled && !this.wifiPreset.enabled) {
            return 0;
        }
        int mask = 0;
        int a = 0;
        while (a < 4) {
            if (!((getCurrentMobilePreset().mask[a] & 1) == 0 && (getCurrentWiFiPreset().mask[a] & 1) == 0 && (getCurrentRoamingPreset().mask[a] & 1) == 0)) {
                mask |= 1;
            }
            if (!((getCurrentMobilePreset().mask[a] & 2) == 0 && (getCurrentWiFiPreset().mask[a] & 2) == 0 && (getCurrentRoamingPreset().mask[a] & 2) == 0)) {
                mask |= 2;
            }
            if (!((getCurrentMobilePreset().mask[a] & 4) == 0 && (getCurrentWiFiPreset().mask[a] & 4) == 0 && (getCurrentRoamingPreset().mask[a] & 4) == 0)) {
                mask |= 4;
            }
            if ((getCurrentMobilePreset().mask[a] & 8) != 0 || (getCurrentWiFiPreset().mask[a] & 8) != 0 || (getCurrentRoamingPreset().mask[a] & 8) != 0) {
                mask |= 8;
            }
            a++;
        }
        return mask;
    }

    public void checkAutodownloadSettings() {
        int currentMask = getCurrentDownloadMask();
        if (currentMask != this.lastCheckMask) {
            int a;
            this.lastCheckMask = currentMask;
            if ((currentMask & 1) == 0) {
                for (a = 0; a < this.photoDownloadQueue.size(); a++) {
                    DownloadObject downloadObject = (DownloadObject) this.photoDownloadQueue.get(a);
                    if (downloadObject.object instanceof PhotoSize) {
                        FileLoader.getInstance(this.currentAccount).cancelLoadFile((PhotoSize) downloadObject.object);
                    } else if (downloadObject.object instanceof Document) {
                        FileLoader.getInstance(this.currentAccount).cancelLoadFile((Document) downloadObject.object);
                    }
                }
                this.photoDownloadQueue.clear();
            } else if (this.photoDownloadQueue.isEmpty()) {
                newDownloadObjectsAvailable(1);
            }
            if ((currentMask & 2) == 0) {
                for (a = 0; a < this.audioDownloadQueue.size(); a++) {
                    FileLoader.getInstance(this.currentAccount).cancelLoadFile((Document) ((DownloadObject) this.audioDownloadQueue.get(a)).object);
                }
                this.audioDownloadQueue.clear();
            } else if (this.audioDownloadQueue.isEmpty()) {
                newDownloadObjectsAvailable(2);
            }
            if ((currentMask & 8) == 0) {
                for (a = 0; a < this.documentDownloadQueue.size(); a++) {
                    FileLoader.getInstance(this.currentAccount).cancelLoadFile(((DownloadObject) this.documentDownloadQueue.get(a)).object);
                }
                this.documentDownloadQueue.clear();
            } else if (this.documentDownloadQueue.isEmpty()) {
                newDownloadObjectsAvailable(8);
            }
            if ((currentMask & 4) == 0) {
                for (a = 0; a < this.videoDownloadQueue.size(); a++) {
                    FileLoader.getInstance(this.currentAccount).cancelLoadFile((Document) ((DownloadObject) this.videoDownloadQueue.get(a)).object);
                }
                this.videoDownloadQueue.clear();
            } else if (this.videoDownloadQueue.isEmpty()) {
                newDownloadObjectsAvailable(4);
            }
            int mask = getAutodownloadMaskAll();
            if (mask == 0) {
                MessagesStorage.getInstance(this.currentAccount).clearDownloadQueue(0);
                return;
            }
            if ((mask & 1) == 0) {
                MessagesStorage.getInstance(this.currentAccount).clearDownloadQueue(1);
            }
            if ((mask & 2) == 0) {
                MessagesStorage.getInstance(this.currentAccount).clearDownloadQueue(2);
            }
            if ((mask & 4) == 0) {
                MessagesStorage.getInstance(this.currentAccount).clearDownloadQueue(4);
            }
            if ((mask & 8) == 0) {
                MessagesStorage.getInstance(this.currentAccount).clearDownloadQueue(8);
            }
        }
    }

    public boolean canDownloadMedia(MessageObject messageObject) {
        return canDownloadMedia(messageObject.messageOwner) == 1;
    }

    public int canDownloadMedia(Message message) {
        int type;
        int index;
        Preset preset;
        int i = 2;
        boolean isVideo = MessageObject.isVideoMessage(message);
        if (isVideo || MessageObject.isGifMessage(message) || MessageObject.isRoundVideoMessage(message)) {
            type = 4;
        } else if (MessageObject.isVoiceMessage(message)) {
            type = 2;
        } else if (MessageObject.isPhoto(message) || MessageObject.isStickerMessage(message)) {
            type = 1;
        } else {
            type = 8;
        }
        Peer peer = message.to_id;
        if (peer == null) {
            index = 1;
        } else if (peer.user_id != 0) {
            if (ContactsController.getInstance(this.currentAccount).contactsDict.containsKey(Integer.valueOf(peer.user_id))) {
                index = 0;
            } else {
                index = 1;
            }
        } else if (peer.chat_id != 0) {
            if (message.from_id == 0 || !ContactsController.getInstance(this.currentAccount).contactsDict.containsKey(Integer.valueOf(message.from_id))) {
                index = 2;
            } else {
                index = 0;
            }
        } else if (!MessageObject.isMegagroup(message)) {
            index = 3;
        } else if (message.from_id == 0 || !ContactsController.getInstance(this.currentAccount).contactsDict.containsKey(Integer.valueOf(message.from_id))) {
            index = 2;
        } else {
            index = 0;
        }
        if (ApplicationLoader.isConnectedToWiFi()) {
            if (!this.wifiPreset.enabled) {
                return 0;
            }
            preset = getCurrentWiFiPreset();
        } else if (ApplicationLoader.isRoaming()) {
            if (!this.roamingPreset.enabled) {
                return 0;
            }
            preset = getCurrentRoamingPreset();
        } else if (!this.mobilePreset.enabled) {
            return 0;
        } else {
            preset = getCurrentMobilePreset();
        }
        int mask = preset.mask[index];
        int maxSize = preset.sizes[typeToIndex(type)];
        int size = MessageObject.getMessageSize(message);
        if (isVideo && preset.preloadVideo && size > maxSize && maxSize > 2097152) {
            if ((mask & type) == 0) {
                i = 0;
            }
            return i;
        } else if (type != 1 && (size == 0 || size > maxSize)) {
            return 0;
        } else {
            if (type == 2 || (mask & type) != 0) {
                return 1;
            }
            return 0;
        }
    }

    protected boolean canDownloadNextTrack() {
        if (ApplicationLoader.isConnectedToWiFi()) {
            if (this.wifiPreset.enabled && getCurrentWiFiPreset().preloadMusic) {
                return true;
            }
            return false;
        } else if (ApplicationLoader.isRoaming()) {
            if (this.roamingPreset.enabled && getCurrentRoamingPreset().preloadMusic) {
                return true;
            }
            return false;
        } else if (this.mobilePreset.enabled && getCurrentMobilePreset().preloadMusic) {
            return true;
        } else {
            return false;
        }
    }

    protected int getCurrentDownloadMask() {
        int mask = 0;
        int a;
        if (ApplicationLoader.isConnectedToWiFi()) {
            if (this.wifiPreset.enabled) {
                mask = 0;
                for (a = 0; a < 4; a++) {
                    mask |= getCurrentWiFiPreset().mask[a];
                }
            }
        } else if (ApplicationLoader.isRoaming()) {
            if (this.roamingPreset.enabled) {
                mask = 0;
                for (a = 0; a < 4; a++) {
                    mask |= getCurrentRoamingPreset().mask[a];
                }
            }
        } else if (this.mobilePreset.enabled) {
            mask = 0;
            for (a = 0; a < 4; a++) {
                mask |= getCurrentMobilePreset().mask[a];
            }
        }
        return mask;
    }

    public void savePresetToServer(int type) {
        Preset preset;
        boolean enabled;
        boolean z;
        int i;
        int i2 = 0;
        TL_account_saveAutoDownloadSettings req = new TL_account_saveAutoDownloadSettings();
        if (type == 0) {
            preset = getCurrentMobilePreset();
            enabled = this.mobilePreset.enabled;
        } else if (type == 1) {
            preset = getCurrentWiFiPreset();
            enabled = this.wifiPreset.enabled;
        } else {
            preset = getCurrentRoamingPreset();
            enabled = this.roamingPreset.enabled;
        }
        req.settings = new TL_autoDownloadSettings();
        req.settings.audio_preload_next = preset.preloadMusic;
        req.settings.video_preload_large = preset.preloadVideo;
        req.settings.phonecalls_less_data = preset.lessCallData;
        TL_autoDownloadSettings tL_autoDownloadSettings = req.settings;
        if (enabled) {
            z = false;
        } else {
            z = true;
        }
        tL_autoDownloadSettings.disabled = z;
        boolean photo = false;
        boolean video = false;
        boolean document = false;
        for (int a = 0; a < preset.mask.length; a++) {
            if ((preset.mask[a] & 1) != 0) {
                photo = true;
            }
            if ((preset.mask[a] & 4) != 0) {
                video = true;
            }
            if ((preset.mask[a] & 8) != 0) {
                document = true;
            }
            if (photo && video && document) {
                break;
            }
        }
        req.settings.photo_size_max = photo ? preset.sizes[0] : 0;
        tL_autoDownloadSettings = req.settings;
        if (video) {
            i = preset.sizes[1];
        } else {
            i = 0;
        }
        tL_autoDownloadSettings.video_size_max = i;
        TL_autoDownloadSettings tL_autoDownloadSettings2 = req.settings;
        if (document) {
            i2 = preset.sizes[2];
        }
        tL_autoDownloadSettings2.file_size_max = i2;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, DownloadController$$Lambda$2.$instance);
    }

    static final /* synthetic */ void lambda$savePresetToServer$3$DownloadController(TLObject response, TL_error error) {
    }

    protected void processDownloadObjects(int type, ArrayList<DownloadObject> objects) {
        if (!objects.isEmpty()) {
            ArrayList<DownloadObject> queue = null;
            if (type == 1) {
                queue = this.photoDownloadQueue;
            } else if (type == 2) {
                queue = this.audioDownloadQueue;
            } else if (type == 4) {
                queue = this.videoDownloadQueue;
            } else if (type == 8) {
                queue = this.documentDownloadQueue;
            }
            for (int a = 0; a < objects.size(); a++) {
                String path;
                DownloadObject downloadObject = (DownloadObject) objects.get(a);
                if (downloadObject.object instanceof Document) {
                    path = FileLoader.getAttachFileName(downloadObject.object);
                } else {
                    path = FileLoader.getAttachFileName(downloadObject.object);
                }
                if (!this.downloadQueueKeys.containsKey(path)) {
                    boolean added = true;
                    if (downloadObject.object instanceof PhotoSize) {
                        FileLoader.getInstance(this.currentAccount).loadFile((PhotoSize) downloadObject.object, null, downloadObject.secret ? 2 : 0);
                    } else if (downloadObject.object instanceof Document) {
                        FileLoader.getInstance(this.currentAccount).loadFile((Document) downloadObject.object, downloadObject.parent, 0, downloadObject.secret ? 2 : 0);
                    } else {
                        added = false;
                    }
                    if (added) {
                        queue.add(downloadObject);
                        this.downloadQueueKeys.put(path, downloadObject);
                    }
                }
            }
        }
    }

    protected void newDownloadObjectsAvailable(int downloadMask) {
        int mask = getCurrentDownloadMask();
        if (!((mask & 1) == 0 || (downloadMask & 1) == 0 || !this.photoDownloadQueue.isEmpty())) {
            MessagesStorage.getInstance(this.currentAccount).getDownloadQueue(1);
        }
        if (!((mask & 2) == 0 || (downloadMask & 2) == 0 || !this.audioDownloadQueue.isEmpty())) {
            MessagesStorage.getInstance(this.currentAccount).getDownloadQueue(2);
        }
        if (!((mask & 4) == 0 || (downloadMask & 4) == 0 || !this.videoDownloadQueue.isEmpty())) {
            MessagesStorage.getInstance(this.currentAccount).getDownloadQueue(4);
        }
        if ((mask & 8) != 0 && (downloadMask & 8) != 0 && this.documentDownloadQueue.isEmpty()) {
            MessagesStorage.getInstance(this.currentAccount).getDownloadQueue(8);
        }
    }

    private void checkDownloadFinished(String fileName, int state) {
        DownloadObject downloadObject = (DownloadObject) this.downloadQueueKeys.get(fileName);
        if (downloadObject != null) {
            this.downloadQueueKeys.remove(fileName);
            if (state == 0 || state == 2) {
                MessagesStorage.getInstance(this.currentAccount).removeFromDownloadQueue(downloadObject.id, downloadObject.type, false);
            }
            if (downloadObject.type == 1) {
                this.photoDownloadQueue.remove(downloadObject);
                if (this.photoDownloadQueue.isEmpty()) {
                    newDownloadObjectsAvailable(1);
                }
            } else if (downloadObject.type == 2) {
                this.audioDownloadQueue.remove(downloadObject);
                if (this.audioDownloadQueue.isEmpty()) {
                    newDownloadObjectsAvailable(2);
                }
            } else if (downloadObject.type == 4) {
                this.videoDownloadQueue.remove(downloadObject);
                if (this.videoDownloadQueue.isEmpty()) {
                    newDownloadObjectsAvailable(4);
                }
            } else if (downloadObject.type == 8) {
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

    public void addLoadingFileObserver(String fileName, FileDownloadProgressListener observer) {
        addLoadingFileObserver(fileName, null, observer);
    }

    public void addLoadingFileObserver(String fileName, MessageObject messageObject, FileDownloadProgressListener observer) {
        if (this.listenerInProgress) {
            this.addLaterArray.put(fileName, observer);
            return;
        }
        removeLoadingFileObserver(observer);
        ArrayList<WeakReference<FileDownloadProgressListener>> arrayList = (ArrayList) this.loadingFileObservers.get(fileName);
        if (arrayList == null) {
            arrayList = new ArrayList();
            this.loadingFileObservers.put(fileName, arrayList);
        }
        arrayList.add(new WeakReference(observer));
        if (messageObject != null) {
            ArrayList<MessageObject> messageObjects = (ArrayList) this.loadingFileMessagesObservers.get(fileName);
            if (messageObjects == null) {
                messageObjects = new ArrayList();
                this.loadingFileMessagesObservers.put(fileName, messageObjects);
            }
            messageObjects.add(messageObject);
        }
        this.observersByTag.put(observer.getObserverTag(), fileName);
    }

    public void removeLoadingFileObserver(FileDownloadProgressListener observer) {
        if (this.listenerInProgress) {
            this.deleteLaterArray.add(observer);
            return;
        }
        String fileName = (String) this.observersByTag.get(observer.getObserverTag());
        if (fileName != null) {
            ArrayList<WeakReference<FileDownloadProgressListener>> arrayList = (ArrayList) this.loadingFileObservers.get(fileName);
            if (arrayList != null) {
                int a = 0;
                while (a < arrayList.size()) {
                    WeakReference<FileDownloadProgressListener> reference = (WeakReference) arrayList.get(a);
                    if (reference.get() == null || reference.get() == observer) {
                        arrayList.remove(a);
                        a--;
                    }
                    a++;
                }
                if (arrayList.isEmpty()) {
                    this.loadingFileObservers.remove(fileName);
                }
            }
            this.observersByTag.remove(observer.getObserverTag());
        }
    }

    private void processLaterArrays() {
        for (Entry<String, FileDownloadProgressListener> listener : this.addLaterArray.entrySet()) {
            addLoadingFileObserver((String) listener.getKey(), (FileDownloadProgressListener) listener.getValue());
        }
        this.addLaterArray.clear();
        Iterator it = this.deleteLaterArray.iterator();
        while (it.hasNext()) {
            removeLoadingFileObserver((FileDownloadProgressListener) it.next());
        }
        this.deleteLaterArray.clear();
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        String fileName;
        ArrayList<WeakReference<FileDownloadProgressListener>> arrayList;
        int size;
        int a;
        WeakReference<FileDownloadProgressListener> reference;
        Float progress;
        if (id == NotificationCenter.fileDidFailedLoad || id == NotificationCenter.httpFileDidFailedLoad) {
            fileName = args[0];
            Integer canceled = args[1];
            this.listenerInProgress = true;
            arrayList = (ArrayList) this.loadingFileObservers.get(fileName);
            if (arrayList != null) {
                size = arrayList.size();
                for (a = 0; a < size; a++) {
                    reference = (WeakReference) arrayList.get(a);
                    if (reference.get() != null) {
                        ((FileDownloadProgressListener) reference.get()).onFailedDownload(fileName, canceled.intValue() == 1);
                        if (canceled.intValue() != 1) {
                            this.observersByTag.remove(((FileDownloadProgressListener) reference.get()).getObserverTag());
                        }
                    }
                }
                if (canceled.intValue() != 1) {
                    this.loadingFileObservers.remove(fileName);
                }
            }
            this.listenerInProgress = false;
            processLaterArrays();
            checkDownloadFinished(fileName, canceled.intValue());
        } else if (id == NotificationCenter.fileDidLoad || id == NotificationCenter.httpFileDidLoad) {
            this.listenerInProgress = true;
            fileName = (String) args[0];
            ArrayList<MessageObject> messageObjects = (ArrayList) this.loadingFileMessagesObservers.get(fileName);
            if (messageObjects != null) {
                size = messageObjects.size();
                for (a = 0; a < size; a++) {
                    ((MessageObject) messageObjects.get(a)).mediaExists = true;
                }
                this.loadingFileMessagesObservers.remove(fileName);
            }
            arrayList = (ArrayList) this.loadingFileObservers.get(fileName);
            if (arrayList != null) {
                size = arrayList.size();
                for (a = 0; a < size; a++) {
                    reference = (WeakReference) arrayList.get(a);
                    if (reference.get() != null) {
                        ((FileDownloadProgressListener) reference.get()).onSuccessDownload(fileName);
                        this.observersByTag.remove(((FileDownloadProgressListener) reference.get()).getObserverTag());
                    }
                }
                this.loadingFileObservers.remove(fileName);
            }
            this.listenerInProgress = false;
            processLaterArrays();
            checkDownloadFinished(fileName, 0);
        } else if (id == NotificationCenter.FileLoadProgressChanged) {
            this.listenerInProgress = true;
            fileName = (String) args[0];
            arrayList = (ArrayList) this.loadingFileObservers.get(fileName);
            if (arrayList != null) {
                progress = args[1];
                size = arrayList.size();
                for (a = 0; a < size; a++) {
                    reference = (WeakReference) arrayList.get(a);
                    if (reference.get() != null) {
                        ((FileDownloadProgressListener) reference.get()).onProgressDownload(fileName, progress.floatValue());
                    }
                }
            }
            this.listenerInProgress = false;
            processLaterArrays();
        } else if (id == NotificationCenter.FileUploadProgressChanged) {
            this.listenerInProgress = true;
            fileName = (String) args[0];
            arrayList = (ArrayList) this.loadingFileObservers.get(fileName);
            if (arrayList != null) {
                progress = (Float) args[1];
                Boolean enc = args[2];
                size = arrayList.size();
                for (a = 0; a < size; a++) {
                    reference = (WeakReference) arrayList.get(a);
                    if (reference.get() != null) {
                        ((FileDownloadProgressListener) reference.get()).onProgressUpload(fileName, progress.floatValue(), enc.booleanValue());
                    }
                }
            }
            this.listenerInProgress = false;
            processLaterArrays();
            try {
                ArrayList<DelayedMessage> delayedMessages = SendMessagesHelper.getInstance(this.currentAccount).getDelayedMessages(fileName);
                if (delayedMessages != null) {
                    for (a = 0; a < delayedMessages.size(); a++) {
                        DelayedMessage delayedMessage = (DelayedMessage) delayedMessages.get(a);
                        if (delayedMessage.encryptedChat == null) {
                            long dialog_id = delayedMessage.peer;
                            Long lastTime;
                            if (delayedMessage.type == 4) {
                                lastTime = (Long) this.typingTimes.get(dialog_id);
                                if (lastTime == null || lastTime.longValue() + 4000 < System.currentTimeMillis()) {
                                    MessageObject messageObject = (MessageObject) delayedMessage.extraHashMap.get(fileName + "_i");
                                    if (messageObject == null || !messageObject.isVideo()) {
                                        MessagesController.getInstance(this.currentAccount).sendTyping(dialog_id, 4, 0);
                                    } else {
                                        MessagesController.getInstance(this.currentAccount).sendTyping(dialog_id, 5, 0);
                                    }
                                    this.typingTimes.put(dialog_id, Long.valueOf(System.currentTimeMillis()));
                                }
                            } else {
                                lastTime = (Long) this.typingTimes.get(dialog_id);
                                Document document = delayedMessage.obj.getDocument();
                                if (lastTime == null || lastTime.longValue() + 4000 < System.currentTimeMillis()) {
                                    if (delayedMessage.obj.isRoundVideo()) {
                                        MessagesController.getInstance(this.currentAccount).sendTyping(dialog_id, 8, 0);
                                    } else if (delayedMessage.obj.isVideo()) {
                                        MessagesController.getInstance(this.currentAccount).sendTyping(dialog_id, 5, 0);
                                    } else if (delayedMessage.obj.isVoice()) {
                                        MessagesController.getInstance(this.currentAccount).sendTyping(dialog_id, 9, 0);
                                    } else if (delayedMessage.obj.getDocument() != null) {
                                        MessagesController.getInstance(this.currentAccount).sendTyping(dialog_id, 3, 0);
                                    } else if (delayedMessage.location != null) {
                                        MessagesController.getInstance(this.currentAccount).sendTyping(dialog_id, 4, 0);
                                    }
                                    this.typingTimes.put(dialog_id, Long.valueOf(System.currentTimeMillis()));
                                }
                            }
                        }
                    }
                }
            } catch (Throwable e) {
                FileLog.e(e);
            }
        }
    }
}
