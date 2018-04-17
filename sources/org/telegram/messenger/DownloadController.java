package org.telegram.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.util.LongSparseArray;
import android.util.SparseArray;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.Peer;
import org.telegram.tgnet.TLRPC.PhotoSize;

public class DownloadController implements NotificationCenterDelegate {
    public static final int AUTODOWNLOAD_MASK_AUDIO = 2;
    public static final int AUTODOWNLOAD_MASK_DOCUMENT = 8;
    public static final int AUTODOWNLOAD_MASK_GIF = 32;
    public static final int AUTODOWNLOAD_MASK_MUSIC = 16;
    public static final int AUTODOWNLOAD_MASK_PHOTO = 1;
    public static final int AUTODOWNLOAD_MASK_VIDEO = 4;
    public static final int AUTODOWNLOAD_MASK_VIDEOMESSAGE = 64;
    private static volatile DownloadController[] Instance = new DownloadController[3];
    private HashMap<String, FileDownloadProgressListener> addLaterArray;
    private ArrayList<DownloadObject> audioDownloadQueue;
    private int currentAccount;
    private ArrayList<FileDownloadProgressListener> deleteLaterArray;
    private ArrayList<DownloadObject> documentDownloadQueue;
    private HashMap<String, DownloadObject> downloadQueueKeys;
    private ArrayList<DownloadObject> gifDownloadQueue;
    public boolean globalAutodownloadEnabled;
    private int lastCheckMask;
    private int lastTag;
    private boolean listenerInProgress;
    private HashMap<String, ArrayList<MessageObject>> loadingFileMessagesObservers;
    private HashMap<String, ArrayList<WeakReference<FileDownloadProgressListener>>> loadingFileObservers;
    public int[] mobileDataDownloadMask = new int[4];
    public int[] mobileMaxFileSize = new int[7];
    private ArrayList<DownloadObject> musicDownloadQueue;
    private SparseArray<String> observersByTag;
    private ArrayList<DownloadObject> photoDownloadQueue;
    public int[] roamingDownloadMask = new int[4];
    public int[] roamingMaxFileSize = new int[7];
    private LongSparseArray<Long> typingTimes;
    private ArrayList<DownloadObject> videoDownloadQueue;
    private ArrayList<DownloadObject> videoMessageDownloadQueue;
    public int[] wifiDownloadMask = new int[4];
    public int[] wifiMaxFileSize = new int[7];

    /* renamed from: org.telegram.messenger.DownloadController$1 */
    class C01451 implements Runnable {
        C01451() {
        }

        public void run() {
            NotificationCenter.getInstance(DownloadController.this.currentAccount).addObserver(DownloadController.this, NotificationCenter.FileDidFailedLoad);
            NotificationCenter.getInstance(DownloadController.this.currentAccount).addObserver(DownloadController.this, NotificationCenter.FileDidLoaded);
            NotificationCenter.getInstance(DownloadController.this.currentAccount).addObserver(DownloadController.this, NotificationCenter.FileLoadProgressChanged);
            NotificationCenter.getInstance(DownloadController.this.currentAccount).addObserver(DownloadController.this, NotificationCenter.FileUploadProgressChanged);
            NotificationCenter.getInstance(DownloadController.this.currentAccount).addObserver(DownloadController.this, NotificationCenter.httpFileDidLoaded);
            NotificationCenter.getInstance(DownloadController.this.currentAccount).addObserver(DownloadController.this, NotificationCenter.httpFileDidFailedLoad);
        }
    }

    /* renamed from: org.telegram.messenger.DownloadController$2 */
    class C01462 extends BroadcastReceiver {
        C01462() {
        }

        public void onReceive(Context context, Intent intent) {
            DownloadController.this.checkAutodownloadSettings();
        }
    }

    public interface FileDownloadProgressListener {
        int getObserverTag();

        void onFailedDownload(String str);

        void onProgressDownload(String str, float f);

        void onProgressUpload(String str, float f, boolean z);

        void onSuccessDownload(String str);
    }

    public static DownloadController getInstance(int num) {
        DownloadController localInstance = Instance[num];
        if (localInstance == null) {
            synchronized (DownloadController.class) {
                localInstance = Instance[num];
                if (localInstance == null) {
                    DownloadController[] downloadControllerArr = Instance;
                    DownloadController downloadController = new DownloadController(num);
                    localInstance = downloadController;
                    downloadControllerArr[num] = downloadController;
                }
            }
        }
        return localInstance;
    }

    public DownloadController(int instance) {
        int a = 0;
        this.lastCheckMask = 0;
        this.photoDownloadQueue = new ArrayList();
        this.audioDownloadQueue = new ArrayList();
        this.videoMessageDownloadQueue = new ArrayList();
        this.documentDownloadQueue = new ArrayList();
        this.musicDownloadQueue = new ArrayList();
        this.gifDownloadQueue = new ArrayList();
        this.videoDownloadQueue = new ArrayList();
        this.downloadQueueKeys = new HashMap();
        this.loadingFileObservers = new HashMap();
        this.loadingFileMessagesObservers = new HashMap();
        this.observersByTag = new SparseArray();
        this.listenerInProgress = false;
        this.addLaterArray = new HashMap();
        this.deleteLaterArray = new ArrayList();
        this.lastTag = 0;
        this.typingTimes = new LongSparseArray();
        this.currentAccount = instance;
        SharedPreferences preferences = MessagesController.getMainSettings(this.currentAccount);
        int a2 = 0;
        while (a2 < 4) {
            String key = new StringBuilder();
            key.append("mobileDataDownloadMask");
            key.append(a2 == 0 ? TtmlNode.ANONYMOUS_REGION_ID : Integer.valueOf(a2));
            key = key.toString();
            if (a2 != 0) {
                if (!preferences.contains(key)) {
                    this.mobileDataDownloadMask[a2] = this.mobileDataDownloadMask[0];
                    this.wifiDownloadMask[a2] = this.wifiDownloadMask[0];
                    this.roamingDownloadMask[a2] = this.roamingDownloadMask[0];
                    a2++;
                }
            }
            this.mobileDataDownloadMask[a2] = preferences.getInt(key, 115);
            int[] iArr = this.wifiDownloadMask;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("wifiDownloadMask");
            stringBuilder.append(a2 == 0 ? TtmlNode.ANONYMOUS_REGION_ID : Integer.valueOf(a2));
            iArr[a2] = preferences.getInt(stringBuilder.toString(), 115);
            iArr = this.roamingDownloadMask;
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("roamingDownloadMask");
            stringBuilder2.append(a2 == 0 ? TtmlNode.ANONYMOUS_REGION_ID : Integer.valueOf(a2));
            iArr[a2] = preferences.getInt(stringBuilder2.toString(), 0);
            a2++;
        }
        while (true) {
            int a3 = a;
            if (a3 >= 7) {
                break;
            }
            int[] iArr2;
            StringBuilder stringBuilder3;
            if (a3 == 1) {
                a = 2097152;
            } else if (a3 == 6) {
                a = 5242880;
            } else {
                a = 10485760;
                iArr2 = this.mobileMaxFileSize;
                stringBuilder3 = new StringBuilder();
                stringBuilder3.append("mobileMaxDownloadSize");
                stringBuilder3.append(a3);
                iArr2[a3] = preferences.getInt(stringBuilder3.toString(), a);
                iArr2 = this.wifiMaxFileSize;
                stringBuilder3 = new StringBuilder();
                stringBuilder3.append("wifiMaxDownloadSize");
                stringBuilder3.append(a3);
                iArr2[a3] = preferences.getInt(stringBuilder3.toString(), a);
                iArr2 = this.roamingMaxFileSize;
                stringBuilder3 = new StringBuilder();
                stringBuilder3.append("roamingMaxDownloadSize");
                stringBuilder3.append(a3);
                iArr2[a3] = preferences.getInt(stringBuilder3.toString(), a);
                a = a3 + 1;
            }
            iArr2 = this.mobileMaxFileSize;
            stringBuilder3 = new StringBuilder();
            stringBuilder3.append("mobileMaxDownloadSize");
            stringBuilder3.append(a3);
            iArr2[a3] = preferences.getInt(stringBuilder3.toString(), a);
            iArr2 = this.wifiMaxFileSize;
            stringBuilder3 = new StringBuilder();
            stringBuilder3.append("wifiMaxDownloadSize");
            stringBuilder3.append(a3);
            iArr2[a3] = preferences.getInt(stringBuilder3.toString(), a);
            iArr2 = this.roamingMaxFileSize;
            stringBuilder3 = new StringBuilder();
            stringBuilder3.append("roamingMaxDownloadSize");
            stringBuilder3.append(a3);
            iArr2[a3] = preferences.getInt(stringBuilder3.toString(), a);
            a = a3 + 1;
        }
        this.globalAutodownloadEnabled = preferences.getBoolean("globalAutodownloadEnabled", true);
        AndroidUtilities.runOnUIThread(new C01451());
        ApplicationLoader.applicationContext.registerReceiver(new C01462(), new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        if (UserConfig.getInstance(this.currentAccount).isClientActivated()) {
            checkAutodownloadSettings();
        }
    }

    public static int maskToIndex(int mask) {
        if (mask == 1) {
            return 0;
        }
        if (mask == 2) {
            return 1;
        }
        if (mask == 4) {
            return 2;
        }
        if (mask == 8) {
            return 3;
        }
        if (mask == 16) {
            return 4;
        }
        if (mask == 32) {
            return 5;
        }
        if (mask == 64) {
            return 6;
        }
        return 0;
    }

    public void cleanup() {
        this.photoDownloadQueue.clear();
        this.audioDownloadQueue.clear();
        this.videoMessageDownloadQueue.clear();
        this.documentDownloadQueue.clear();
        this.videoDownloadQueue.clear();
        this.musicDownloadQueue.clear();
        this.gifDownloadQueue.clear();
        this.downloadQueueKeys.clear();
        this.typingTimes.clear();
    }

    protected int getAutodownloadMask() {
        int a = 0;
        if (!this.globalAutodownloadEnabled) {
            return 0;
        }
        int[] masksArray;
        int mask;
        int result = 0;
        if (ConnectionsManager.isConnectedToWiFi()) {
            masksArray = this.wifiDownloadMask;
        } else if (ConnectionsManager.isRoaming()) {
            masksArray = this.roamingDownloadMask;
        } else {
            masksArray = this.mobileDataDownloadMask;
            while (a < 4) {
                mask = 0;
                if ((masksArray[a] & 1) != 0) {
                    mask = 0 | 1;
                }
                if ((masksArray[a] & 2) != 0) {
                    mask |= 2;
                }
                if ((masksArray[a] & 64) != 0) {
                    mask |= 64;
                }
                if ((4 & masksArray[a]) != 0) {
                    mask |= 4;
                }
                if ((masksArray[a] & 8) != 0) {
                    mask |= 8;
                }
                if ((masksArray[a] & 16) != 0) {
                    mask |= 16;
                }
                if ((masksArray[a] & 32) != 0) {
                    mask |= 32;
                }
                result |= mask << (a * 8);
                a++;
            }
            return result;
        }
        while (a < 4) {
            mask = 0;
            if ((masksArray[a] & 1) != 0) {
                mask = 0 | 1;
            }
            if ((masksArray[a] & 2) != 0) {
                mask |= 2;
            }
            if ((masksArray[a] & 64) != 0) {
                mask |= 64;
            }
            if ((4 & masksArray[a]) != 0) {
                mask |= 4;
            }
            if ((masksArray[a] & 8) != 0) {
                mask |= 8;
            }
            if ((masksArray[a] & 16) != 0) {
                mask |= 16;
            }
            if ((masksArray[a] & 32) != 0) {
                mask |= 32;
            }
            result |= mask << (a * 8);
            a++;
        }
        return result;
    }

    protected int getAutodownloadMaskAll() {
        int a = 0;
        if (!this.globalAutodownloadEnabled) {
            return 0;
        }
        int mask = 0;
        while (a < 4) {
            if (!((this.mobileDataDownloadMask[a] & 1) == 0 && (this.wifiDownloadMask[a] & 1) == 0 && (this.roamingDownloadMask[a] & 1) == 0)) {
                mask |= 1;
            }
            if (!((this.mobileDataDownloadMask[a] & 2) == 0 && (this.wifiDownloadMask[a] & 2) == 0 && (this.roamingDownloadMask[a] & 2) == 0)) {
                mask |= 2;
            }
            if (!((this.mobileDataDownloadMask[a] & 64) == 0 && (this.wifiDownloadMask[a] & 64) == 0 && (this.roamingDownloadMask[a] & 64) == 0)) {
                mask |= 64;
            }
            if (!((this.mobileDataDownloadMask[a] & 4) == 0 && (this.wifiDownloadMask[a] & 4) == 0 && (4 & this.roamingDownloadMask[a]) == 0)) {
                mask |= 4;
            }
            if (!((this.mobileDataDownloadMask[a] & 8) == 0 && (this.wifiDownloadMask[a] & 8) == 0 && (this.roamingDownloadMask[a] & 8) == 0)) {
                mask |= 8;
            }
            if (!((this.mobileDataDownloadMask[a] & 16) == 0 && (this.wifiDownloadMask[a] & 16) == 0 && (this.roamingDownloadMask[a] & 16) == 0)) {
                mask |= 16;
            }
            if ((this.mobileDataDownloadMask[a] & 32) != 0 || (this.wifiDownloadMask[a] & 32) != 0 || (this.roamingDownloadMask[a] & 32) != 0) {
                mask |= 32;
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
                    FileLoader.getInstance(this.currentAccount).cancelLoadFile((PhotoSize) ((DownloadObject) this.photoDownloadQueue.get(a)).object);
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
            if ((currentMask & 64) == 0) {
                for (a = 0; a < this.videoMessageDownloadQueue.size(); a++) {
                    FileLoader.getInstance(this.currentAccount).cancelLoadFile((Document) ((DownloadObject) this.videoMessageDownloadQueue.get(a)).object);
                }
                this.videoMessageDownloadQueue.clear();
            } else if (this.videoMessageDownloadQueue.isEmpty()) {
                newDownloadObjectsAvailable(64);
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
            if ((currentMask & 16) == 0) {
                for (a = 0; a < this.musicDownloadQueue.size(); a++) {
                    FileLoader.getInstance(this.currentAccount).cancelLoadFile(((DownloadObject) this.musicDownloadQueue.get(a)).object);
                }
                this.musicDownloadQueue.clear();
            } else if (this.musicDownloadQueue.isEmpty()) {
                newDownloadObjectsAvailable(16);
            }
            if ((currentMask & 32) == 0) {
                for (a = 0; a < this.gifDownloadQueue.size(); a++) {
                    FileLoader.getInstance(this.currentAccount).cancelLoadFile(((DownloadObject) this.gifDownloadQueue.get(a)).object);
                }
                this.gifDownloadQueue.clear();
            } else if (this.gifDownloadQueue.isEmpty()) {
                newDownloadObjectsAvailable(32);
            }
            a = getAutodownloadMaskAll();
            if (a == 0) {
                MessagesStorage.getInstance(this.currentAccount).clearDownloadQueue(0);
            } else {
                if ((a & 1) == 0) {
                    MessagesStorage.getInstance(this.currentAccount).clearDownloadQueue(1);
                }
                if ((a & 2) == 0) {
                    MessagesStorage.getInstance(this.currentAccount).clearDownloadQueue(2);
                }
                if ((a & 64) == 0) {
                    MessagesStorage.getInstance(this.currentAccount).clearDownloadQueue(64);
                }
                if ((a & 4) == 0) {
                    MessagesStorage.getInstance(this.currentAccount).clearDownloadQueue(4);
                }
                if ((a & 8) == 0) {
                    MessagesStorage.getInstance(this.currentAccount).clearDownloadQueue(8);
                }
                if ((a & 16) == 0) {
                    MessagesStorage.getInstance(this.currentAccount).clearDownloadQueue(16);
                }
                if ((a & 32) == 0) {
                    MessagesStorage.getInstance(this.currentAccount).clearDownloadQueue(32);
                }
            }
        }
    }

    public boolean canDownloadMedia(MessageObject messageObject) {
        return canDownloadMedia(messageObject.messageOwner);
    }

    public boolean canDownloadMedia(Message message) {
        boolean z = false;
        if (!this.globalAutodownloadEnabled) {
            return false;
        }
        int type;
        Peer peer;
        int index;
        int mask;
        int maxSize;
        if (MessageObject.isPhoto(message)) {
            type = 1;
        } else if (MessageObject.isVoiceMessage(message)) {
            type = 2;
        } else if (MessageObject.isRoundVideoMessage(message)) {
            type = 64;
        } else if (MessageObject.isVideoMessage(message)) {
            type = 4;
        } else if (MessageObject.isMusicMessage(message)) {
            type = 16;
        } else if (MessageObject.isGifMessage(message)) {
            type = 32;
        } else {
            type = 8;
            peer = message.to_id;
            if (peer != null) {
                index = 1;
            } else if (peer.user_id == 0) {
                if (ContactsController.getInstance(this.currentAccount).contactsDict.containsKey(Integer.valueOf(peer.user_id))) {
                    index = 1;
                } else {
                    index = 0;
                }
            } else if (peer.chat_id != 0) {
                index = 2;
            } else if (MessageObject.isMegagroup(message)) {
                index = 3;
            } else {
                index = 2;
            }
            if (ConnectionsManager.isConnectedToWiFi()) {
                mask = this.wifiDownloadMask[index];
                maxSize = this.wifiMaxFileSize[maskToIndex(type)];
            } else if (ConnectionsManager.isRoaming()) {
                mask = this.mobileDataDownloadMask[index];
                maxSize = this.mobileMaxFileSize[maskToIndex(type)];
                if ((type == 1 || MessageObject.getMessageSize(message) <= maxSize) && (mask & type) != 0) {
                    z = true;
                }
                return z;
            } else {
                mask = this.roamingDownloadMask[index];
                maxSize = this.roamingMaxFileSize[maskToIndex(type)];
            }
            z = true;
            return z;
        }
        peer = message.to_id;
        if (peer != null) {
            index = 1;
        } else if (peer.user_id == 0) {
            if (peer.chat_id != 0) {
                index = 2;
            } else if (MessageObject.isMegagroup(message)) {
                index = 3;
            } else {
                index = 2;
            }
        } else if (ContactsController.getInstance(this.currentAccount).contactsDict.containsKey(Integer.valueOf(peer.user_id))) {
            index = 1;
        } else {
            index = 0;
        }
        if (ConnectionsManager.isConnectedToWiFi()) {
            mask = this.wifiDownloadMask[index];
            maxSize = this.wifiMaxFileSize[maskToIndex(type)];
        } else if (ConnectionsManager.isRoaming()) {
            mask = this.mobileDataDownloadMask[index];
            maxSize = this.mobileMaxFileSize[maskToIndex(type)];
            z = true;
            return z;
        } else {
            mask = this.roamingDownloadMask[index];
            maxSize = this.roamingMaxFileSize[maskToIndex(type)];
        }
        z = true;
        return z;
    }

    protected int getCurrentDownloadMask() {
        int a = 0;
        if (!this.globalAutodownloadEnabled) {
            return 0;
        }
        int mask;
        if (ConnectionsManager.isConnectedToWiFi()) {
            mask = 0;
            while (a < 4) {
                mask |= this.wifiDownloadMask[a];
                a++;
            }
            return mask;
        } else if (ConnectionsManager.isRoaming()) {
            mask = 0;
            while (a < 4) {
                mask |= this.roamingDownloadMask[a];
                a++;
            }
            return mask;
        } else {
            mask = 0;
            while (a < 4) {
                mask |= this.mobileDataDownloadMask[a];
                a++;
            }
            return mask;
        }
    }

    protected void processDownloadObjects(int type, ArrayList<DownloadObject> objects) {
        if (!objects.isEmpty()) {
            ArrayList<DownloadObject> queue = null;
            if (type == 1) {
                queue = this.photoDownloadQueue;
            } else if (type == 2) {
                queue = this.audioDownloadQueue;
            } else if (type == 64) {
                queue = this.videoMessageDownloadQueue;
            } else if (type == 4) {
                queue = this.videoDownloadQueue;
            } else if (type == 8) {
                queue = this.documentDownloadQueue;
            } else if (type == 16) {
                queue = this.musicDownloadQueue;
            } else if (type == 32) {
                queue = this.gifDownloadQueue;
            }
            for (int a = 0; a < objects.size(); a++) {
                String path;
                DownloadObject downloadObject = (DownloadObject) objects.get(a);
                if (downloadObject.object instanceof Document) {
                    path = FileLoader.getAttachFileName((Document) downloadObject.object);
                } else {
                    path = FileLoader.getAttachFileName(downloadObject.object);
                }
                if (!this.downloadQueueKeys.containsKey(path)) {
                    boolean added = true;
                    if (downloadObject.object instanceof PhotoSize) {
                        FileLoader.getInstance(this.currentAccount).loadFile((PhotoSize) downloadObject.object, null, downloadObject.secret ? 2 : 0);
                    } else if (downloadObject.object instanceof Document) {
                        FileLoader.getInstance(this.currentAccount).loadFile(downloadObject.object, false, downloadObject.secret ? 2 : 0);
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
        if (!((mask & 64) == 0 || (downloadMask & 64) == 0 || !this.videoMessageDownloadQueue.isEmpty())) {
            MessagesStorage.getInstance(this.currentAccount).getDownloadQueue(64);
        }
        if (!((mask & 4) == 0 || (downloadMask & 4) == 0 || !this.videoDownloadQueue.isEmpty())) {
            MessagesStorage.getInstance(this.currentAccount).getDownloadQueue(4);
        }
        if (!((mask & 8) == 0 || (downloadMask & 8) == 0 || !this.documentDownloadQueue.isEmpty())) {
            MessagesStorage.getInstance(this.currentAccount).getDownloadQueue(8);
        }
        if (!((mask & 16) == 0 || (downloadMask & 16) == 0 || !this.musicDownloadQueue.isEmpty())) {
            MessagesStorage.getInstance(this.currentAccount).getDownloadQueue(16);
        }
        if ((mask & 32) != 0 && (downloadMask & 32) != 0 && this.gifDownloadQueue.isEmpty()) {
            MessagesStorage.getInstance(this.currentAccount).getDownloadQueue(32);
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
            } else if (downloadObject.type == 64) {
                this.videoMessageDownloadQueue.remove(downloadObject);
                if (this.videoMessageDownloadQueue.isEmpty()) {
                    newDownloadObjectsAvailable(64);
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
            } else if (downloadObject.type == 16) {
                this.musicDownloadQueue.remove(downloadObject);
                if (this.musicDownloadQueue.isEmpty()) {
                    newDownloadObjectsAvailable(16);
                }
            } else if (downloadObject.type == 32) {
                this.gifDownloadQueue.remove(downloadObject);
                if (this.gifDownloadQueue.isEmpty()) {
                    newDownloadObjectsAvailable(32);
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
        int a;
        int a2;
        WeakReference<FileDownloadProgressListener> reference;
        DownloadController downloadController = this;
        int i = id;
        if (i != NotificationCenter.FileDidFailedLoad) {
            if (i != NotificationCenter.httpFileDidFailedLoad) {
                if (i != NotificationCenter.FileDidLoaded) {
                    if (i != NotificationCenter.httpFileDidLoaded) {
                        ArrayList<WeakReference<FileDownloadProgressListener>> arrayList;
                        Float progress;
                        int size;
                        if (i == NotificationCenter.FileLoadProgressChanged) {
                            downloadController.listenerInProgress = true;
                            fileName = args[0];
                            arrayList = (ArrayList) downloadController.loadingFileObservers.get(fileName);
                            if (arrayList != null) {
                                progress = args[1];
                                size = arrayList.size();
                                for (a = 0; a < size; a++) {
                                    WeakReference<FileDownloadProgressListener> reference2 = (WeakReference) arrayList.get(a);
                                    if (reference2.get() != null) {
                                        ((FileDownloadProgressListener) reference2.get()).onProgressDownload(fileName, progress.floatValue());
                                    }
                                }
                            }
                            downloadController.listenerInProgress = false;
                            processLaterArrays();
                            return;
                        } else if (i == NotificationCenter.FileUploadProgressChanged) {
                            downloadController.listenerInProgress = true;
                            fileName = (String) args[0];
                            arrayList = (ArrayList) downloadController.loadingFileObservers.get(fileName);
                            if (arrayList != null) {
                                progress = (Float) args[1];
                                Boolean enc = args[2];
                                int size2 = arrayList.size();
                                for (size = 0; size < size2; size++) {
                                    WeakReference<FileDownloadProgressListener> reference3 = (WeakReference) arrayList.get(size);
                                    if (reference3.get() != null) {
                                        ((FileDownloadProgressListener) reference3.get()).onProgressUpload(fileName, progress.floatValue(), enc.booleanValue());
                                    }
                                }
                            }
                            downloadController.listenerInProgress = false;
                            processLaterArrays();
                            try {
                                ArrayList<DelayedMessage> delayedMessages = SendMessagesHelper.getInstance(downloadController.currentAccount).getDelayedMessages(fileName);
                                if (delayedMessages != null) {
                                    for (a = 0; a < delayedMessages.size(); a++) {
                                        DelayedMessage delayedMessage = (DelayedMessage) delayedMessages.get(a);
                                        if (delayedMessage.encryptedChat == null) {
                                            long dialog_id = delayedMessage.peer;
                                            if (delayedMessage.type == 4) {
                                                Long lastTime = (Long) downloadController.typingTimes.get(dialog_id);
                                                if (lastTime == null || lastTime.longValue() + 4000 < System.currentTimeMillis()) {
                                                    HashMap hashMap = delayedMessage.extraHashMap;
                                                    StringBuilder stringBuilder = new StringBuilder();
                                                    stringBuilder.append(fileName);
                                                    stringBuilder.append("_i");
                                                    MessageObject messageObject = (MessageObject) hashMap.get(stringBuilder.toString());
                                                    if (messageObject == null || !messageObject.isVideo()) {
                                                        MessagesController.getInstance(downloadController.currentAccount).sendTyping(dialog_id, 4, 0);
                                                    } else {
                                                        MessagesController.getInstance(downloadController.currentAccount).sendTyping(dialog_id, 5, 0);
                                                    }
                                                    downloadController.typingTimes.put(dialog_id, Long.valueOf(System.currentTimeMillis()));
                                                }
                                            } else {
                                                Long lastTime2 = (Long) downloadController.typingTimes.get(dialog_id);
                                                Document document = delayedMessage.obj.getDocument();
                                                if (lastTime2 == null || lastTime2.longValue() + 4000 < System.currentTimeMillis()) {
                                                    if (delayedMessage.obj.isRoundVideo()) {
                                                        MessagesController.getInstance(downloadController.currentAccount).sendTyping(dialog_id, 8, 0);
                                                    } else if (delayedMessage.obj.isVideo()) {
                                                        MessagesController.getInstance(downloadController.currentAccount).sendTyping(dialog_id, 5, 0);
                                                    } else if (delayedMessage.obj.isVoice()) {
                                                        MessagesController.getInstance(downloadController.currentAccount).sendTyping(dialog_id, 9, 0);
                                                    } else if (delayedMessage.obj.getDocument() != null) {
                                                        MessagesController.getInstance(downloadController.currentAccount).sendTyping(dialog_id, 3, 0);
                                                    } else if (delayedMessage.location != null) {
                                                        MessagesController.getInstance(downloadController.currentAccount).sendTyping(dialog_id, 4, 0);
                                                    }
                                                    downloadController.typingTimes.put(dialog_id, Long.valueOf(System.currentTimeMillis()));
                                                }
                                            }
                                        }
                                    }
                                }
                                return;
                            } catch (Throwable e) {
                                FileLog.m3e(e);
                                return;
                            }
                        } else {
                            return;
                        }
                    }
                }
                downloadController.listenerInProgress = true;
                fileName = (String) args[0];
                ArrayList<MessageObject> messageObjects = (ArrayList) downloadController.loadingFileMessagesObservers.get(fileName);
                if (messageObjects != null) {
                    a = messageObjects.size();
                    for (a2 = 0; a2 < a; a2++) {
                        ((MessageObject) messageObjects.get(a2)).mediaExists = true;
                    }
                    downloadController.loadingFileMessagesObservers.remove(fileName);
                }
                ArrayList<WeakReference<FileDownloadProgressListener>> arrayList2 = (ArrayList) downloadController.loadingFileObservers.get(fileName);
                if (arrayList2 != null) {
                    a = arrayList2.size();
                    for (a2 = 0; a2 < a; a2++) {
                        reference = (WeakReference) arrayList2.get(a2);
                        if (reference.get() != null) {
                            ((FileDownloadProgressListener) reference.get()).onSuccessDownload(fileName);
                            downloadController.observersByTag.remove(((FileDownloadProgressListener) reference.get()).getObserverTag());
                        }
                    }
                    downloadController.loadingFileObservers.remove(fileName);
                }
                downloadController.listenerInProgress = false;
                processLaterArrays();
                checkDownloadFinished(fileName, 0);
                return;
            }
        }
        a2 = 0;
        downloadController.listenerInProgress = true;
        fileName = (String) args[a2];
        ArrayList<WeakReference<FileDownloadProgressListener>> arrayList3 = (ArrayList) downloadController.loadingFileObservers.get(fileName);
        if (arrayList3 != null) {
            a = arrayList3.size();
            for (a2 = 0; a2 < a; a2++) {
                reference = (WeakReference) arrayList3.get(a2);
                if (reference.get() != null) {
                    ((FileDownloadProgressListener) reference.get()).onFailedDownload(fileName);
                    downloadController.observersByTag.remove(((FileDownloadProgressListener) reference.get()).getObserverTag());
                }
            }
            downloadController.loadingFileObservers.remove(fileName);
        }
        downloadController.listenerInProgress = false;
        processLaterArrays();
        checkDownloadFinished(fileName, ((Integer) args[1]).intValue());
    }
}
