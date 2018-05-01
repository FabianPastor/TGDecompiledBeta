package org.telegram.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

    public static int maskToIndex(int i) {
        return i == 1 ? 0 : i == 2 ? 1 : i == 4 ? 2 : i == 8 ? 3 : i == 16 ? 4 : i == 32 ? 5 : i == 64 ? 6 : 0;
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
        int i2 = 0;
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
        this.currentAccount = i;
        i = MessagesController.getMainSettings(this.currentAccount);
        int i3 = 0;
        while (i3 < 4) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("mobileDataDownloadMask");
            stringBuilder.append(i3 == 0 ? TtmlNode.ANONYMOUS_REGION_ID : Integer.valueOf(i3));
            String stringBuilder2 = stringBuilder.toString();
            if (i3 != 0) {
                if (!i.contains(stringBuilder2)) {
                    this.mobileDataDownloadMask[i3] = this.mobileDataDownloadMask[0];
                    this.wifiDownloadMask[i3] = this.wifiDownloadMask[0];
                    this.roamingDownloadMask[i3] = this.roamingDownloadMask[0];
                    i3++;
                }
            }
            this.mobileDataDownloadMask[i3] = i.getInt(stringBuilder2, 115);
            int[] iArr = this.wifiDownloadMask;
            StringBuilder stringBuilder3 = new StringBuilder();
            stringBuilder3.append("wifiDownloadMask");
            stringBuilder3.append(i3 == 0 ? TtmlNode.ANONYMOUS_REGION_ID : Integer.valueOf(i3));
            iArr[i3] = i.getInt(stringBuilder3.toString(), 115);
            iArr = this.roamingDownloadMask;
            stringBuilder3 = new StringBuilder();
            stringBuilder3.append("roamingDownloadMask");
            stringBuilder3.append(i3 == 0 ? TtmlNode.ANONYMOUS_REGION_ID : Integer.valueOf(i3));
            iArr[i3] = i.getInt(stringBuilder3.toString(), 0);
            i3++;
        }
        while (i2 < 7) {
            int i4 = i2 == 1 ? 2097152 : i2 == 6 ? 5242880 : 10485760;
            int[] iArr2 = this.mobileMaxFileSize;
            stringBuilder = new StringBuilder();
            stringBuilder.append("mobileMaxDownloadSize");
            stringBuilder.append(i2);
            iArr2[i2] = i.getInt(stringBuilder.toString(), i4);
            iArr2 = this.wifiMaxFileSize;
            stringBuilder = new StringBuilder();
            stringBuilder.append("wifiMaxDownloadSize");
            stringBuilder.append(i2);
            iArr2[i2] = i.getInt(stringBuilder.toString(), i4);
            iArr2 = this.roamingMaxFileSize;
            stringBuilder = new StringBuilder();
            stringBuilder.append("roamingMaxDownloadSize");
            stringBuilder.append(i2);
            iArr2[i2] = i.getInt(stringBuilder.toString(), i4);
            i2++;
        }
        this.globalAutodownloadEnabled = i.getBoolean("globalAutodownloadEnabled", true);
        AndroidUtilities.runOnUIThread(new C01451());
        ApplicationLoader.applicationContext.registerReceiver(new C01462(), new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        if (UserConfig.getInstance(this.currentAccount).isClientActivated() != 0) {
            checkAutodownloadSettings();
        }
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
        if (!this.globalAutodownloadEnabled) {
            return 0;
        }
        int[] iArr;
        if (ConnectionsManager.isConnectedToWiFi()) {
            iArr = this.wifiDownloadMask;
        } else if (ConnectionsManager.isRoaming()) {
            iArr = this.roamingDownloadMask;
        } else {
            iArr = this.mobileDataDownloadMask;
        }
        int i = 0;
        int i2 = i;
        while (i < 4) {
            int i3 = 1;
            if ((iArr[i] & 1) == 0) {
                i3 = 0;
            }
            if ((iArr[i] & 2) != 0) {
                i3 |= 2;
            }
            if ((iArr[i] & 64) != 0) {
                i3 |= 64;
            }
            if ((4 & iArr[i]) != 0) {
                i3 |= 4;
            }
            if ((iArr[i] & 8) != 0) {
                i3 |= 8;
            }
            if ((iArr[i] & 16) != 0) {
                i3 |= 16;
            }
            if ((iArr[i] & 32) != 0) {
                i3 |= 32;
            }
            i2 |= i3 << (i * 8);
            i++;
        }
        return i2;
    }

    protected int getAutodownloadMaskAll() {
        int i = 0;
        if (!this.globalAutodownloadEnabled) {
            return 0;
        }
        int i2 = 0;
        while (i < 4) {
            if (!((this.mobileDataDownloadMask[i] & 1) == 0 && (this.wifiDownloadMask[i] & 1) == 0 && (this.roamingDownloadMask[i] & 1) == 0)) {
                i2 |= 1;
            }
            if (!((this.mobileDataDownloadMask[i] & 2) == 0 && (this.wifiDownloadMask[i] & 2) == 0 && (this.roamingDownloadMask[i] & 2) == 0)) {
                i2 |= 2;
            }
            if (!((this.mobileDataDownloadMask[i] & 64) == 0 && (this.wifiDownloadMask[i] & 64) == 0 && (this.roamingDownloadMask[i] & 64) == 0)) {
                i2 |= 64;
            }
            if (!((this.mobileDataDownloadMask[i] & 4) == 0 && (this.wifiDownloadMask[i] & 4) == 0 && (4 & this.roamingDownloadMask[i]) == 0)) {
                i2 |= 4;
            }
            if (!((this.mobileDataDownloadMask[i] & 8) == 0 && (this.wifiDownloadMask[i] & 8) == 0 && (this.roamingDownloadMask[i] & 8) == 0)) {
                i2 |= 8;
            }
            if (!((this.mobileDataDownloadMask[i] & 16) == 0 && (this.wifiDownloadMask[i] & 16) == 0 && (this.roamingDownloadMask[i] & 16) == 0)) {
                i2 |= 16;
            }
            if ((this.mobileDataDownloadMask[i] & 32) != 0 || (this.wifiDownloadMask[i] & 32) != 0 || (this.roamingDownloadMask[i] & 32) != 0) {
                i2 |= 32;
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
                    FileLoader.getInstance(this.currentAccount).cancelLoadFile((PhotoSize) ((DownloadObject) this.photoDownloadQueue.get(i)).object);
                }
                this.photoDownloadQueue.clear();
            } else if (this.photoDownloadQueue.isEmpty()) {
                newDownloadObjectsAvailable(1);
            }
            if ((currentDownloadMask & 2) == 0) {
                for (i = 0; i < this.audioDownloadQueue.size(); i++) {
                    FileLoader.getInstance(this.currentAccount).cancelLoadFile((Document) ((DownloadObject) this.audioDownloadQueue.get(i)).object);
                }
                this.audioDownloadQueue.clear();
            } else if (this.audioDownloadQueue.isEmpty()) {
                newDownloadObjectsAvailable(2);
            }
            if ((currentDownloadMask & 64) == 0) {
                for (i = 0; i < this.videoMessageDownloadQueue.size(); i++) {
                    FileLoader.getInstance(this.currentAccount).cancelLoadFile((Document) ((DownloadObject) this.videoMessageDownloadQueue.get(i)).object);
                }
                this.videoMessageDownloadQueue.clear();
            } else if (this.videoMessageDownloadQueue.isEmpty()) {
                newDownloadObjectsAvailable(64);
            }
            if ((currentDownloadMask & 8) == 0) {
                for (i = 0; i < this.documentDownloadQueue.size(); i++) {
                    FileLoader.getInstance(this.currentAccount).cancelLoadFile((Document) ((DownloadObject) this.documentDownloadQueue.get(i)).object);
                }
                this.documentDownloadQueue.clear();
            } else if (this.documentDownloadQueue.isEmpty()) {
                newDownloadObjectsAvailable(8);
            }
            if ((currentDownloadMask & 4) == 0) {
                for (i = 0; i < this.videoDownloadQueue.size(); i++) {
                    FileLoader.getInstance(this.currentAccount).cancelLoadFile((Document) ((DownloadObject) this.videoDownloadQueue.get(i)).object);
                }
                this.videoDownloadQueue.clear();
            } else if (this.videoDownloadQueue.isEmpty()) {
                newDownloadObjectsAvailable(4);
            }
            if ((currentDownloadMask & 16) == 0) {
                for (i = 0; i < this.musicDownloadQueue.size(); i++) {
                    FileLoader.getInstance(this.currentAccount).cancelLoadFile((Document) ((DownloadObject) this.musicDownloadQueue.get(i)).object);
                }
                this.musicDownloadQueue.clear();
            } else if (this.musicDownloadQueue.isEmpty()) {
                newDownloadObjectsAvailable(16);
            }
            if ((currentDownloadMask & 32) == 0) {
                for (currentDownloadMask = 0; currentDownloadMask < this.gifDownloadQueue.size(); currentDownloadMask++) {
                    FileLoader.getInstance(this.currentAccount).cancelLoadFile((Document) ((DownloadObject) this.gifDownloadQueue.get(currentDownloadMask)).object);
                }
                this.gifDownloadQueue.clear();
            } else if (this.gifDownloadQueue.isEmpty()) {
                newDownloadObjectsAvailable(32);
            }
            currentDownloadMask = getAutodownloadMaskAll();
            if (currentDownloadMask == 0) {
                MessagesStorage.getInstance(this.currentAccount).clearDownloadQueue(0);
            } else {
                if ((currentDownloadMask & 1) == 0) {
                    MessagesStorage.getInstance(this.currentAccount).clearDownloadQueue(1);
                }
                if ((currentDownloadMask & 2) == 0) {
                    MessagesStorage.getInstance(this.currentAccount).clearDownloadQueue(2);
                }
                if ((currentDownloadMask & 64) == 0) {
                    MessagesStorage.getInstance(this.currentAccount).clearDownloadQueue(64);
                }
                if ((currentDownloadMask & 4) == 0) {
                    MessagesStorage.getInstance(this.currentAccount).clearDownloadQueue(4);
                }
                if ((currentDownloadMask & 8) == 0) {
                    MessagesStorage.getInstance(this.currentAccount).clearDownloadQueue(8);
                }
                if ((currentDownloadMask & 16) == 0) {
                    MessagesStorage.getInstance(this.currentAccount).clearDownloadQueue(16);
                }
                if ((currentDownloadMask & 32) == 0) {
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
        int i;
        int i2 = 2;
        int i3 = MessageObject.isPhoto(message) ? 1 : MessageObject.isVoiceMessage(message) ? 2 : MessageObject.isRoundVideoMessage(message) ? 64 : MessageObject.isVideoMessage(message) ? 4 : MessageObject.isMusicMessage(message) ? 16 : MessageObject.isGifMessage(message) ? 32 : 8;
        Peer peer = message.to_id;
        if (peer != null) {
            if (peer.user_id != 0) {
                if (ContactsController.getInstance(this.currentAccount).contactsDict.containsKey(Integer.valueOf(peer.user_id))) {
                    i2 = 0;
                }
            } else if (peer.chat_id == 0) {
                if (!MessageObject.isMegagroup(message)) {
                    i2 = 3;
                }
            }
            if (ConnectionsManager.isConnectedToWiFi()) {
                i2 = this.wifiDownloadMask[i2];
                i = this.wifiMaxFileSize[maskToIndex(i3)];
            } else if (ConnectionsManager.isRoaming()) {
                i2 = this.mobileDataDownloadMask[i2];
                i = this.mobileMaxFileSize[maskToIndex(i3)];
            } else {
                i2 = this.roamingDownloadMask[i2];
                i = this.roamingMaxFileSize[maskToIndex(i3)];
            }
            if ((i3 == 1 || MessageObject.getMessageSize(message) <= r4) && (r2 & i3) != null) {
                z = true;
            }
            return z;
        }
        i2 = 1;
        if (ConnectionsManager.isConnectedToWiFi()) {
            i2 = this.wifiDownloadMask[i2];
            i = this.wifiMaxFileSize[maskToIndex(i3)];
        } else if (ConnectionsManager.isRoaming()) {
            i2 = this.mobileDataDownloadMask[i2];
            i = this.mobileMaxFileSize[maskToIndex(i3)];
        } else {
            i2 = this.roamingDownloadMask[i2];
            i = this.roamingMaxFileSize[maskToIndex(i3)];
        }
        z = true;
        return z;
    }

    protected int getCurrentDownloadMask() {
        int i = 0;
        if (!this.globalAutodownloadEnabled) {
            return 0;
        }
        int i2;
        if (ConnectionsManager.isConnectedToWiFi()) {
            i2 = 0;
            while (i < 4) {
                i2 |= this.wifiDownloadMask[i];
                i++;
            }
            return i2;
        } else if (ConnectionsManager.isRoaming()) {
            i2 = 0;
            while (i < 4) {
                i2 |= this.roamingDownloadMask[i];
                i++;
            }
            return i2;
        } else {
            i2 = 0;
            while (i < 4) {
                i2 |= this.mobileDataDownloadMask[i];
                i++;
            }
            return i2;
        }
    }

    protected void processDownloadObjects(int i, ArrayList<DownloadObject> arrayList) {
        if (!arrayList.isEmpty()) {
            i = i == 1 ? this.photoDownloadQueue : i == 2 ? this.audioDownloadQueue : i == 64 ? this.videoMessageDownloadQueue : i == 4 ? this.videoDownloadQueue : i == 8 ? this.documentDownloadQueue : i == 16 ? this.musicDownloadQueue : i == 32 ? this.gifDownloadQueue : 0;
            for (int i2 = 0; i2 < arrayList.size(); i2++) {
                Object attachFileName;
                DownloadObject downloadObject = (DownloadObject) arrayList.get(i2);
                if (downloadObject.object instanceof Document) {
                    attachFileName = FileLoader.getAttachFileName((Document) downloadObject.object);
                } else {
                    attachFileName = FileLoader.getAttachFileName(downloadObject.object);
                }
                if (!this.downloadQueueKeys.containsKey(attachFileName)) {
                    boolean z;
                    if (downloadObject.object instanceof PhotoSize) {
                        FileLoader.getInstance(this.currentAccount).loadFile((PhotoSize) downloadObject.object, null, downloadObject.secret ? 2 : 0);
                    } else if (downloadObject.object instanceof Document) {
                        FileLoader.getInstance(this.currentAccount).loadFile((Document) downloadObject.object, false, downloadObject.secret ? 2 : 0);
                    } else {
                        z = false;
                        if (z) {
                            i.add(downloadObject);
                            this.downloadQueueKeys.put(attachFileName, downloadObject);
                        }
                    }
                    z = true;
                    if (z) {
                        i.add(downloadObject);
                        this.downloadQueueKeys.put(attachFileName, downloadObject);
                    }
                }
            }
        }
    }

    protected void newDownloadObjectsAvailable(int i) {
        int currentDownloadMask = getCurrentDownloadMask();
        if (!((currentDownloadMask & 1) == 0 || (i & 1) == 0 || !this.photoDownloadQueue.isEmpty())) {
            MessagesStorage.getInstance(this.currentAccount).getDownloadQueue(1);
        }
        if (!((currentDownloadMask & 2) == 0 || (i & 2) == 0 || !this.audioDownloadQueue.isEmpty())) {
            MessagesStorage.getInstance(this.currentAccount).getDownloadQueue(2);
        }
        if (!((currentDownloadMask & 64) == 0 || (i & 64) == 0 || !this.videoMessageDownloadQueue.isEmpty())) {
            MessagesStorage.getInstance(this.currentAccount).getDownloadQueue(64);
        }
        if (!((currentDownloadMask & 4) == 0 || (i & 4) == 0 || !this.videoDownloadQueue.isEmpty())) {
            MessagesStorage.getInstance(this.currentAccount).getDownloadQueue(4);
        }
        if (!((currentDownloadMask & 8) == 0 || (i & 8) == 0 || !this.documentDownloadQueue.isEmpty())) {
            MessagesStorage.getInstance(this.currentAccount).getDownloadQueue(8);
        }
        if (!((currentDownloadMask & 16) == 0 || (i & 16) == 0 || !this.musicDownloadQueue.isEmpty())) {
            MessagesStorage.getInstance(this.currentAccount).getDownloadQueue(16);
        }
        if ((currentDownloadMask & 32) != 0 && (i & 32) != 0 && this.gifDownloadQueue.isEmpty() != 0) {
            MessagesStorage.getInstance(this.currentAccount).getDownloadQueue(32);
        }
    }

    private void checkDownloadFinished(String str, int i) {
        DownloadObject downloadObject = (DownloadObject) this.downloadQueueKeys.get(str);
        if (downloadObject != null) {
            this.downloadQueueKeys.remove(str);
            if (i == 0 || i == 2) {
                MessagesStorage.getInstance(this.currentAccount).removeFromDownloadQueue(downloadObject.id, downloadObject.type, false);
            }
            if (downloadObject.type == 1) {
                this.photoDownloadQueue.remove(downloadObject);
                if (this.photoDownloadQueue.isEmpty() != null) {
                    newDownloadObjectsAvailable(1);
                }
            } else if (downloadObject.type == 2) {
                this.audioDownloadQueue.remove(downloadObject);
                if (this.audioDownloadQueue.isEmpty() != 0) {
                    newDownloadObjectsAvailable(2);
                }
            } else if (downloadObject.type == 64) {
                this.videoMessageDownloadQueue.remove(downloadObject);
                if (this.videoMessageDownloadQueue.isEmpty() != null) {
                    newDownloadObjectsAvailable(64);
                }
            } else if (downloadObject.type == 4) {
                this.videoDownloadQueue.remove(downloadObject);
                if (this.videoDownloadQueue.isEmpty() != null) {
                    newDownloadObjectsAvailable(4);
                }
            } else if (downloadObject.type == 8) {
                this.documentDownloadQueue.remove(downloadObject);
                if (this.documentDownloadQueue.isEmpty() != null) {
                    newDownloadObjectsAvailable(8);
                }
            } else if (downloadObject.type == 16) {
                this.musicDownloadQueue.remove(downloadObject);
                if (this.musicDownloadQueue.isEmpty() != null) {
                    newDownloadObjectsAvailable(16);
                }
            } else if (downloadObject.type == 32) {
                this.gifDownloadQueue.remove(downloadObject);
                if (this.gifDownloadQueue.isEmpty() != null) {
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
        ArrayList arrayList;
        int i3;
        int i4;
        WeakReference weakReference;
        DownloadController downloadController = this;
        int i5 = i;
        if (i5 != NotificationCenter.FileDidFailedLoad) {
            if (i5 != NotificationCenter.httpFileDidFailedLoad) {
                int size;
                ArrayList delayedMessages;
                int i6;
                if (i5 != NotificationCenter.FileDidLoaded) {
                    if (i5 != NotificationCenter.httpFileDidLoaded) {
                        if (i5 == NotificationCenter.FileLoadProgressChanged) {
                            downloadController.listenerInProgress = true;
                            str = (String) objArr[0];
                            arrayList = (ArrayList) downloadController.loadingFileObservers.get(str);
                            if (arrayList != null) {
                                Float f = (Float) objArr[1];
                                size = arrayList.size();
                                for (i3 = 0; i3 < size; i3++) {
                                    WeakReference weakReference2 = (WeakReference) arrayList.get(i3);
                                    if (weakReference2.get() != null) {
                                        ((FileDownloadProgressListener) weakReference2.get()).onProgressDownload(str, f.floatValue());
                                    }
                                }
                            }
                            downloadController.listenerInProgress = false;
                            processLaterArrays();
                            return;
                        } else if (i5 == NotificationCenter.FileUploadProgressChanged) {
                            downloadController.listenerInProgress = true;
                            str = (String) objArr[0];
                            arrayList = (ArrayList) downloadController.loadingFileObservers.get(str);
                            if (arrayList != null) {
                                Float f2 = (Float) objArr[1];
                                Boolean bool = (Boolean) objArr[2];
                                i3 = arrayList.size();
                                for (i4 = 0; i4 < i3; i4++) {
                                    weakReference = (WeakReference) arrayList.get(i4);
                                    if (weakReference.get() != null) {
                                        ((FileDownloadProgressListener) weakReference.get()).onProgressUpload(str, f2.floatValue(), bool.booleanValue());
                                    }
                                }
                            }
                            downloadController.listenerInProgress = false;
                            processLaterArrays();
                            try {
                                delayedMessages = SendMessagesHelper.getInstance(downloadController.currentAccount).getDelayedMessages(str);
                                if (delayedMessages != null) {
                                    for (i6 = 0; i6 < delayedMessages.size(); i6++) {
                                        DelayedMessage delayedMessage = (DelayedMessage) delayedMessages.get(i6);
                                        if (delayedMessage.encryptedChat == null) {
                                            long j = delayedMessage.peer;
                                            Long l;
                                            if (delayedMessage.type == 4) {
                                                l = (Long) downloadController.typingTimes.get(j);
                                                if (l == null || l.longValue() + 4000 < System.currentTimeMillis()) {
                                                    HashMap hashMap = delayedMessage.extraHashMap;
                                                    StringBuilder stringBuilder = new StringBuilder();
                                                    stringBuilder.append(str);
                                                    stringBuilder.append("_i");
                                                    MessageObject messageObject = (MessageObject) hashMap.get(stringBuilder.toString());
                                                    if (messageObject == null || !messageObject.isVideo()) {
                                                        MessagesController.getInstance(downloadController.currentAccount).sendTyping(j, 4, 0);
                                                    } else {
                                                        MessagesController.getInstance(downloadController.currentAccount).sendTyping(j, 5, 0);
                                                    }
                                                    downloadController.typingTimes.put(j, Long.valueOf(System.currentTimeMillis()));
                                                }
                                            } else {
                                                l = (Long) downloadController.typingTimes.get(j);
                                                delayedMessage.obj.getDocument();
                                                if (l == null || l.longValue() + 4000 < System.currentTimeMillis()) {
                                                    if (delayedMessage.obj.isRoundVideo()) {
                                                        MessagesController.getInstance(downloadController.currentAccount).sendTyping(j, 8, 0);
                                                    } else if (delayedMessage.obj.isVideo()) {
                                                        MessagesController.getInstance(downloadController.currentAccount).sendTyping(j, 5, 0);
                                                    } else if (delayedMessage.obj.isVoice()) {
                                                        MessagesController.getInstance(downloadController.currentAccount).sendTyping(j, 9, 0);
                                                    } else if (delayedMessage.obj.getDocument() != null) {
                                                        MessagesController.getInstance(downloadController.currentAccount).sendTyping(j, 3, 0);
                                                    } else if (delayedMessage.location != null) {
                                                        MessagesController.getInstance(downloadController.currentAccount).sendTyping(j, 4, 0);
                                                    }
                                                    downloadController.typingTimes.put(j, Long.valueOf(System.currentTimeMillis()));
                                                }
                                            }
                                        }
                                    }
                                    return;
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
                str = (String) objArr[0];
                delayedMessages = (ArrayList) downloadController.loadingFileMessagesObservers.get(str);
                if (delayedMessages != null) {
                    i6 = delayedMessages.size();
                    for (i3 = 0; i3 < i6; i3++) {
                        ((MessageObject) delayedMessages.get(i3)).mediaExists = true;
                    }
                    downloadController.loadingFileMessagesObservers.remove(str);
                }
                delayedMessages = (ArrayList) downloadController.loadingFileObservers.get(str);
                if (delayedMessages != null) {
                    i6 = delayedMessages.size();
                    for (size = 0; size < i6; size++) {
                        WeakReference weakReference3 = (WeakReference) delayedMessages.get(size);
                        if (weakReference3.get() != null) {
                            ((FileDownloadProgressListener) weakReference3.get()).onSuccessDownload(str);
                            downloadController.observersByTag.remove(((FileDownloadProgressListener) weakReference3.get()).getObserverTag());
                        }
                    }
                    downloadController.loadingFileObservers.remove(str);
                }
                downloadController.listenerInProgress = false;
                processLaterArrays();
                checkDownloadFinished(str, 0);
                return;
            }
        }
        downloadController.listenerInProgress = true;
        str = (String) objArr[0];
        arrayList = (ArrayList) downloadController.loadingFileObservers.get(str);
        if (arrayList != null) {
            i3 = arrayList.size();
            for (i4 = 0; i4 < i3; i4++) {
                weakReference = (WeakReference) arrayList.get(i4);
                if (weakReference.get() != null) {
                    ((FileDownloadProgressListener) weakReference.get()).onFailedDownload(str);
                    downloadController.observersByTag.remove(((FileDownloadProgressListener) weakReference.get()).getObserverTag());
                }
            }
            downloadController.loadingFileObservers.remove(str);
        }
        downloadController.listenerInProgress = false;
        processLaterArrays();
        checkDownloadFinished(str, ((Integer) objArr[1]).intValue());
    }
}
