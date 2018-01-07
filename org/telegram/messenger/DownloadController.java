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
    private HashMap<String, FileDownloadProgressListener> addLaterArray = new HashMap();
    private ArrayList<DownloadObject> audioDownloadQueue = new ArrayList();
    private int currentAccount;
    private ArrayList<FileDownloadProgressListener> deleteLaterArray = new ArrayList();
    private ArrayList<DownloadObject> documentDownloadQueue = new ArrayList();
    private HashMap<String, DownloadObject> downloadQueueKeys = new HashMap();
    private ArrayList<DownloadObject> gifDownloadQueue = new ArrayList();
    public boolean globalAutodownloadEnabled;
    private int lastCheckMask = 0;
    private int lastTag = 0;
    private boolean listenerInProgress = false;
    private HashMap<String, ArrayList<MessageObject>> loadingFileMessagesObservers = new HashMap();
    private HashMap<String, ArrayList<WeakReference<FileDownloadProgressListener>>> loadingFileObservers = new HashMap();
    public int[] mobileDataDownloadMask = new int[4];
    public int[] mobileMaxFileSize = new int[7];
    private ArrayList<DownloadObject> musicDownloadQueue = new ArrayList();
    private SparseArray<String> observersByTag = new SparseArray();
    private ArrayList<DownloadObject> photoDownloadQueue = new ArrayList();
    public int[] roamingDownloadMask = new int[4];
    public int[] roamingMaxFileSize = new int[7];
    private LongSparseArray<Long> typingTimes = new LongSparseArray();
    private ArrayList<DownloadObject> videoDownloadQueue = new ArrayList();
    private ArrayList<DownloadObject> videoMessageDownloadQueue = new ArrayList();
    public int[] wifiDownloadMask = new int[4];
    public int[] wifiMaxFileSize = new int[7];

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
                try {
                    localInstance = Instance[num];
                    if (localInstance == null) {
                        DownloadController[] downloadControllerArr = Instance;
                        DownloadController localInstance2 = new DownloadController(num);
                        try {
                            downloadControllerArr[num] = localInstance2;
                            localInstance = localInstance2;
                        } catch (Throwable th) {
                            Throwable th2 = th;
                            localInstance = localInstance2;
                            throw th2;
                        }
                    }
                } catch (Throwable th3) {
                    th2 = th3;
                    throw th2;
                }
            }
        }
        return localInstance;
    }

    public DownloadController(int instance) {
        this.currentAccount = instance;
        SharedPreferences preferences = MessagesController.getMainSettings(this.currentAccount);
        int a = 0;
        while (a < 4) {
            String key = "mobileDataDownloadMask" + (a == 0 ? TtmlNode.ANONYMOUS_REGION_ID : Integer.valueOf(a));
            if (a == 0 || preferences.contains(key)) {
                Object obj;
                this.mobileDataDownloadMask[a] = preferences.getInt(key, 115);
                this.wifiDownloadMask[a] = preferences.getInt("wifiDownloadMask" + (a == 0 ? TtmlNode.ANONYMOUS_REGION_ID : Integer.valueOf(a)), 115);
                int[] iArr = this.roamingDownloadMask;
                StringBuilder append = new StringBuilder().append("roamingDownloadMask");
                if (a == 0) {
                    obj = TtmlNode.ANONYMOUS_REGION_ID;
                } else {
                    obj = Integer.valueOf(a);
                }
                iArr[a] = preferences.getInt(append.append(obj).toString(), 0);
            } else {
                this.mobileDataDownloadMask[a] = this.mobileDataDownloadMask[0];
                this.wifiDownloadMask[a] = this.wifiDownloadMask[0];
                this.roamingDownloadMask[a] = this.roamingDownloadMask[0];
            }
            a++;
        }
        for (a = 0; a < 7; a++) {
            int sdefault;
            if (a == 1) {
                sdefault = 2097152;
            } else if (a == 6) {
                sdefault = 5242880;
            } else {
                sdefault = 10485760;
            }
            this.mobileMaxFileSize[a] = preferences.getInt("mobileMaxDownloadSize" + a, sdefault);
            this.wifiMaxFileSize[a] = preferences.getInt("wifiMaxDownloadSize" + a, sdefault);
            this.roamingMaxFileSize[a] = preferences.getInt("roamingMaxDownloadSize" + a, sdefault);
        }
        this.globalAutodownloadEnabled = preferences.getBoolean("globalAutodownloadEnabled", true);
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                NotificationCenter.getInstance(DownloadController.this.currentAccount).addObserver(DownloadController.this, NotificationCenter.FileDidFailedLoad);
                NotificationCenter.getInstance(DownloadController.this.currentAccount).addObserver(DownloadController.this, NotificationCenter.FileDidLoaded);
                NotificationCenter.getInstance(DownloadController.this.currentAccount).addObserver(DownloadController.this, NotificationCenter.FileLoadProgressChanged);
                NotificationCenter.getInstance(DownloadController.this.currentAccount).addObserver(DownloadController.this, NotificationCenter.FileUploadProgressChanged);
                NotificationCenter.getInstance(DownloadController.this.currentAccount).addObserver(DownloadController.this, NotificationCenter.httpFileDidLoaded);
                NotificationCenter.getInstance(DownloadController.this.currentAccount).addObserver(DownloadController.this, NotificationCenter.httpFileDidFailedLoad);
            }
        });
        ApplicationLoader.applicationContext.registerReceiver(new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                DownloadController.this.checkAutodownloadSettings();
            }
        }, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
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
        if (!this.globalAutodownloadEnabled) {
            return 0;
        }
        int[] masksArray;
        int result = 0;
        if (ConnectionsManager.isConnectedToWiFi()) {
            masksArray = this.wifiDownloadMask;
        } else if (ConnectionsManager.isRoaming()) {
            masksArray = this.roamingDownloadMask;
        } else {
            masksArray = this.mobileDataDownloadMask;
        }
        for (int a = 0; a < 4; a++) {
            int mask = 0;
            if ((masksArray[a] & 1) != 0) {
                mask = 0 | 1;
            }
            if ((masksArray[a] & 2) != 0) {
                mask |= 2;
            }
            if ((masksArray[a] & 64) != 0) {
                mask |= 64;
            }
            if ((masksArray[a] & 4) != 0) {
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
        }
        return result;
    }

    protected int getAutodownloadMaskAll() {
        if (!this.globalAutodownloadEnabled) {
            return 0;
        }
        int mask = 0;
        int a = 0;
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
            if (!((this.mobileDataDownloadMask[a] & 4) == 0 && (this.wifiDownloadMask[a] & 4) == 0 && (this.roamingDownloadMask[a] & 4) == 0)) {
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
                    FileLoader.getInstance(this.currentAccount).cancelLoadFile((Document) ((DownloadObject) this.musicDownloadQueue.get(a)).object);
                }
                this.musicDownloadQueue.clear();
            } else if (this.musicDownloadQueue.isEmpty()) {
                newDownloadObjectsAvailable(16);
            }
            if ((currentMask & 32) == 0) {
                for (a = 0; a < this.gifDownloadQueue.size(); a++) {
                    FileLoader.getInstance(this.currentAccount).cancelLoadFile((Document) ((DownloadObject) this.gifDownloadQueue.get(a)).object);
                }
                this.gifDownloadQueue.clear();
            } else if (this.gifDownloadQueue.isEmpty()) {
                newDownloadObjectsAvailable(32);
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
            if ((mask & 64) == 0) {
                MessagesStorage.getInstance(this.currentAccount).clearDownloadQueue(64);
            }
            if ((mask & 4) == 0) {
                MessagesStorage.getInstance(this.currentAccount).clearDownloadQueue(4);
            }
            if ((mask & 8) == 0) {
                MessagesStorage.getInstance(this.currentAccount).clearDownloadQueue(8);
            }
            if ((mask & 16) == 0) {
                MessagesStorage.getInstance(this.currentAccount).clearDownloadQueue(16);
            }
            if ((mask & 32) == 0) {
                MessagesStorage.getInstance(this.currentAccount).clearDownloadQueue(32);
            }
        }
    }

    public boolean canDownloadMedia(MessageObject messageObject) {
        return canDownloadMedia(messageObject.messageOwner);
    }

    public boolean canDownloadMedia(Message message) {
        if (!this.globalAutodownloadEnabled) {
            return false;
        }
        int type;
        int index;
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
            index = 2;
        } else if (MessageObject.isMegagroup(message)) {
            index = 2;
        } else {
            index = 3;
        }
        int mask;
        int maxSize;
        if (ConnectionsManager.isConnectedToWiFi()) {
            mask = this.wifiDownloadMask[index];
            maxSize = this.wifiMaxFileSize[maskToIndex(type)];
        } else if (ConnectionsManager.isRoaming()) {
            mask = this.roamingDownloadMask[index];
            maxSize = this.roamingMaxFileSize[maskToIndex(type)];
        } else {
            mask = this.mobileDataDownloadMask[index];
            maxSize = this.mobileMaxFileSize[maskToIndex(type)];
        }
        if ((type == 1 || MessageObject.getMessageSize(message) <= maxSize) && (mask & type) != 0) {
            return true;
        }
        return false;
    }

    protected int getCurrentDownloadMask() {
        if (!this.globalAutodownloadEnabled) {
            return 0;
        }
        int mask;
        int a;
        if (ConnectionsManager.isConnectedToWiFi()) {
            mask = 0;
            for (a = 0; a < 4; a++) {
                mask |= this.wifiDownloadMask[a];
            }
            return mask;
        } else if (ConnectionsManager.isRoaming()) {
            mask = 0;
            for (a = 0; a < 4; a++) {
                mask |= this.roamingDownloadMask[a];
            }
            return mask;
        } else {
            mask = 0;
            for (a = 0; a < 4; a++) {
                mask |= this.mobileDataDownloadMask[a];
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
                    path = FileLoader.getAttachFileName(downloadObject.object);
                } else {
                    path = FileLoader.getAttachFileName(downloadObject.object);
                }
                if (!this.downloadQueueKeys.containsKey(path)) {
                    boolean added = true;
                    if (downloadObject.object instanceof PhotoSize) {
                        FileLoader.getInstance(this.currentAccount).loadFile((PhotoSize) downloadObject.object, null, downloadObject.secret ? 2 : 0);
                    } else if (downloadObject.object instanceof Document) {
                        FileLoader.getInstance(this.currentAccount).loadFile((Document) downloadObject.object, false, downloadObject.secret ? 2 : 0);
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
        ArrayList<WeakReference<FileDownloadProgressListener>> arrayList;
        int a;
        WeakReference<FileDownloadProgressListener> reference;
        if (id == NotificationCenter.FileDidFailedLoad || id == NotificationCenter.httpFileDidFailedLoad) {
            this.listenerInProgress = true;
            fileName = args[0];
            arrayList = (ArrayList) this.loadingFileObservers.get(fileName);
            if (arrayList != null) {
                for (a = 0; a < arrayList.size(); a++) {
                    reference = (WeakReference) arrayList.get(a);
                    if (reference.get() != null) {
                        ((FileDownloadProgressListener) reference.get()).onFailedDownload(fileName);
                        this.observersByTag.remove(((FileDownloadProgressListener) reference.get()).getObserverTag());
                    }
                }
                this.loadingFileObservers.remove(fileName);
            }
            this.listenerInProgress = false;
            processLaterArrays();
            checkDownloadFinished(fileName, ((Integer) args[1]).intValue());
        } else if (id == NotificationCenter.FileDidLoaded || id == NotificationCenter.httpFileDidLoaded) {
            this.listenerInProgress = true;
            fileName = (String) args[0];
            ArrayList<MessageObject> messageObjects = (ArrayList) this.loadingFileMessagesObservers.get(fileName);
            if (messageObjects != null) {
                for (a = 0; a < messageObjects.size(); a++) {
                    ((MessageObject) messageObjects.get(a)).mediaExists = true;
                }
                this.loadingFileMessagesObservers.remove(fileName);
            }
            arrayList = (ArrayList) this.loadingFileObservers.get(fileName);
            if (arrayList != null) {
                for (a = 0; a < arrayList.size(); a++) {
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
                r20 = arrayList.iterator();
                while (r20.hasNext()) {
                    reference = (WeakReference) r20.next();
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
                r20 = arrayList.iterator();
                while (r20.hasNext()) {
                    reference = (WeakReference) r20.next();
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
