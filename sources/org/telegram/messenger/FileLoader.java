package org.telegram.messenger;

import android.text.TextUtils;
import android.util.SparseArray;
import android.util.SparseIntArray;
import j$.util.concurrent.ConcurrentHashMap;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import org.telegram.messenger.FileLoadOperation;
import org.telegram.messenger.FileUploadOperation;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public class FileLoader extends BaseController {
    public static final int IMAGE_TYPE_ANIMATION = 2;
    public static final int IMAGE_TYPE_LOTTIE = 1;
    public static final int IMAGE_TYPE_SVG = 3;
    public static final int IMAGE_TYPE_SVG_WHITE = 4;
    public static final int IMAGE_TYPE_THEME_PREVIEW = 5;
    private static volatile FileLoader[] Instance = new FileLoader[3];
    public static final long MAX_FILE_SIZE = NUM;
    public static final int MEDIA_DIR_AUDIO = 1;
    public static final int MEDIA_DIR_CACHE = 4;
    public static final int MEDIA_DIR_DOCUMENT = 3;
    public static final int MEDIA_DIR_IMAGE = 0;
    public static final int MEDIA_DIR_IMAGE_PUBLIC = 100;
    public static final int MEDIA_DIR_VIDEO = 2;
    public static final int MEDIA_DIR_VIDEO_PUBLIC = 101;
    public static final int QUEUE_TYPE_AUDIO = 2;
    public static final int QUEUE_TYPE_FILE = 0;
    public static final int QUEUE_TYPE_IMAGE = 1;
    /* access modifiers changed from: private */
    public static volatile DispatchQueue fileLoaderQueue = new DispatchQueue("fileUploadQueue");
    private static SparseArray<File> mediaDirs = null;
    private ArrayList<FileLoadOperation> activeFileLoadOperation = new ArrayList<>();
    private SparseArray<LinkedList<FileLoadOperation>> audioLoadOperationQueues = new SparseArray<>();
    private SparseIntArray audioLoadOperationsCount = new SparseIntArray();
    /* access modifiers changed from: private */
    public int currentUploadOperationsCount = 0;
    /* access modifiers changed from: private */
    public int currentUploadSmallOperationsCount = 0;
    /* access modifiers changed from: private */
    public FileLoaderDelegate delegate = null;
    private SparseArray<LinkedList<FileLoadOperation>> fileLoadOperationQueues = new SparseArray<>();
    private SparseIntArray fileLoadOperationsCount = new SparseIntArray();
    private String forceLoadingFile;
    private SparseArray<LinkedList<FileLoadOperation>> imageLoadOperationQueues = new SparseArray<>();
    private SparseIntArray imageLoadOperationsCount = new SparseIntArray();
    private int lastReferenceId;
    private ConcurrentHashMap<String, FileLoadOperation> loadOperationPaths = new ConcurrentHashMap<>();
    /* access modifiers changed from: private */
    public ConcurrentHashMap<String, Boolean> loadOperationPathsUI = new ConcurrentHashMap<>(10, 1.0f, 2);
    private HashMap<String, Boolean> loadingVideos = new HashMap<>();
    private ConcurrentHashMap<Integer, Object> parentObjectReferences = new ConcurrentHashMap<>();
    /* access modifiers changed from: private */
    public ConcurrentHashMap<String, FileUploadOperation> uploadOperationPaths = new ConcurrentHashMap<>();
    /* access modifiers changed from: private */
    public ConcurrentHashMap<String, FileUploadOperation> uploadOperationPathsEnc = new ConcurrentHashMap<>();
    /* access modifiers changed from: private */
    public LinkedList<FileUploadOperation> uploadOperationQueue = new LinkedList<>();
    private HashMap<String, Long> uploadSizes = new HashMap<>();
    /* access modifiers changed from: private */
    public LinkedList<FileUploadOperation> uploadSmallOperationQueue = new LinkedList<>();

    public interface FileLoaderDelegate {
        void fileDidFailedLoad(String str, int i);

        void fileDidFailedUpload(String str, boolean z);

        void fileDidLoaded(String str, File file, Object obj, int i);

        void fileDidUploaded(String str, TLRPC.InputFile inputFile, TLRPC.InputEncryptedFile inputEncryptedFile, byte[] bArr, byte[] bArr2, long j);

        void fileLoadProgressChanged(FileLoadOperation fileLoadOperation, String str, long j, long j2);

        void fileUploadProgressChanged(FileUploadOperation fileUploadOperation, String str, long j, long j2, boolean z);
    }

    static /* synthetic */ int access$608(FileLoader x0) {
        int i = x0.currentUploadSmallOperationsCount;
        x0.currentUploadSmallOperationsCount = i + 1;
        return i;
    }

    static /* synthetic */ int access$610(FileLoader x0) {
        int i = x0.currentUploadSmallOperationsCount;
        x0.currentUploadSmallOperationsCount = i - 1;
        return i;
    }

    static /* synthetic */ int access$808(FileLoader x0) {
        int i = x0.currentUploadOperationsCount;
        x0.currentUploadOperationsCount = i + 1;
        return i;
    }

    static /* synthetic */ int access$810(FileLoader x0) {
        int i = x0.currentUploadOperationsCount;
        x0.currentUploadOperationsCount = i - 1;
        return i;
    }

    public static FileLoader getInstance(int num) {
        FileLoader localInstance = Instance[num];
        if (localInstance == null) {
            synchronized (FileLoader.class) {
                localInstance = Instance[num];
                if (localInstance == null) {
                    FileLoader[] fileLoaderArr = Instance;
                    FileLoader fileLoader = new FileLoader(num);
                    localInstance = fileLoader;
                    fileLoaderArr[num] = fileLoader;
                }
            }
        }
        return localInstance;
    }

    public FileLoader(int instance) {
        super(instance);
    }

    public static void setMediaDirs(SparseArray<File> dirs) {
        mediaDirs = dirs;
    }

    public static File checkDirectory(int type) {
        return mediaDirs.get(type);
    }

    public static File getDirectory(int type) {
        File dir = mediaDirs.get(type);
        if (dir == null && type != 4) {
            dir = mediaDirs.get(4);
        }
        if (dir != null) {
            try {
                if (!dir.isDirectory()) {
                    dir.mkdirs();
                }
            } catch (Exception e) {
            }
        }
        return dir;
    }

    public int getFileReference(Object parentObject) {
        int reference = this.lastReferenceId;
        this.lastReferenceId = reference + 1;
        this.parentObjectReferences.put(Integer.valueOf(reference), parentObject);
        return reference;
    }

    public Object getParentObject(int reference) {
        return this.parentObjectReferences.get(Integer.valueOf(reference));
    }

    /* renamed from: setLoadingVideoInternal */
    public void m563lambda$setLoadingVideo$0$orgtelegrammessengerFileLoader(TLRPC.Document document, boolean player) {
        String key = getAttachFileName(document);
        StringBuilder sb = new StringBuilder();
        sb.append(key);
        sb.append(player ? "p" : "");
        this.loadingVideos.put(sb.toString(), true);
        getNotificationCenter().postNotificationName(NotificationCenter.videoLoadingStateChanged, key);
    }

    public void setLoadingVideo(TLRPC.Document document, boolean player, boolean schedule) {
        if (document != null) {
            if (schedule) {
                AndroidUtilities.runOnUIThread(new FileLoader$$ExternalSyntheticLambda11(this, document, player));
            } else {
                m563lambda$setLoadingVideo$0$orgtelegrammessengerFileLoader(document, player);
            }
        }
    }

    public void setLoadingVideoForPlayer(TLRPC.Document document, boolean player) {
        if (document != null) {
            String key = getAttachFileName(document);
            HashMap<String, Boolean> hashMap = this.loadingVideos;
            StringBuilder sb = new StringBuilder();
            sb.append(key);
            String str = "";
            sb.append(player ? str : "p");
            if (hashMap.containsKey(sb.toString())) {
                HashMap<String, Boolean> hashMap2 = this.loadingVideos;
                StringBuilder sb2 = new StringBuilder();
                sb2.append(key);
                if (player) {
                    str = "p";
                }
                sb2.append(str);
                hashMap2.put(sb2.toString(), true);
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: removeLoadingVideoInternal */
    public void m561lambda$removeLoadingVideo$1$orgtelegrammessengerFileLoader(TLRPC.Document document, boolean player) {
        String key = getAttachFileName(document);
        StringBuilder sb = new StringBuilder();
        sb.append(key);
        sb.append(player ? "p" : "");
        if (this.loadingVideos.remove(sb.toString()) != null) {
            getNotificationCenter().postNotificationName(NotificationCenter.videoLoadingStateChanged, key);
        }
    }

    public void removeLoadingVideo(TLRPC.Document document, boolean player, boolean schedule) {
        if (document != null) {
            if (schedule) {
                AndroidUtilities.runOnUIThread(new FileLoader$$ExternalSyntheticLambda10(this, document, player));
            } else {
                m561lambda$removeLoadingVideo$1$orgtelegrammessengerFileLoader(document, player);
            }
        }
    }

    public boolean isLoadingVideo(TLRPC.Document document, boolean player) {
        if (document != null) {
            HashMap<String, Boolean> hashMap = this.loadingVideos;
            StringBuilder sb = new StringBuilder();
            sb.append(getAttachFileName(document));
            sb.append(player ? "p" : "");
            if (hashMap.containsKey(sb.toString())) {
                return true;
            }
        }
        return false;
    }

    public boolean isLoadingVideoAny(TLRPC.Document document) {
        return isLoadingVideo(document, false) || isLoadingVideo(document, true);
    }

    public void cancelFileUpload(String location, boolean enc) {
        fileLoaderQueue.postRunnable(new FileLoader$$ExternalSyntheticLambda1(this, enc, location));
    }

    /* renamed from: lambda$cancelFileUpload$2$org-telegram-messenger-FileLoader  reason: not valid java name */
    public /* synthetic */ void m552lambda$cancelFileUpload$2$orgtelegrammessengerFileLoader(boolean enc, String location) {
        FileUploadOperation operation;
        if (!enc) {
            operation = this.uploadOperationPaths.get(location);
        } else {
            operation = this.uploadOperationPathsEnc.get(location);
        }
        this.uploadSizes.remove(location);
        if (operation != null) {
            this.uploadOperationPathsEnc.remove(location);
            this.uploadOperationQueue.remove(operation);
            this.uploadSmallOperationQueue.remove(operation);
            operation.cancel();
        }
    }

    public void checkUploadNewDataAvailable(String location, boolean encrypted, long newAvailableSize, long finalSize) {
        fileLoaderQueue.postRunnable(new FileLoader$$ExternalSyntheticLambda3(this, encrypted, location, newAvailableSize, finalSize));
    }

    /* renamed from: lambda$checkUploadNewDataAvailable$3$org-telegram-messenger-FileLoader  reason: not valid java name */
    public /* synthetic */ void m557x5d598CLASSNAME(boolean encrypted, String location, long newAvailableSize, long finalSize) {
        FileUploadOperation operation;
        if (encrypted) {
            operation = this.uploadOperationPathsEnc.get(location);
        } else {
            operation = this.uploadOperationPaths.get(location);
        }
        if (operation != null) {
            operation.checkNewDataAvailable(newAvailableSize, finalSize);
        } else if (finalSize != 0) {
            this.uploadSizes.put(location, Long.valueOf(finalSize));
        }
    }

    public void onNetworkChanged(boolean slow) {
        fileLoaderQueue.postRunnable(new FileLoader$$ExternalSyntheticLambda13(this, slow));
    }

    /* renamed from: lambda$onNetworkChanged$4$org-telegram-messenger-FileLoader  reason: not valid java name */
    public /* synthetic */ void m560lambda$onNetworkChanged$4$orgtelegrammessengerFileLoader(boolean slow) {
        for (Map.Entry<String, FileUploadOperation> entry : this.uploadOperationPaths.entrySet()) {
            entry.getValue().onNetworkChanged(slow);
        }
        for (Map.Entry<String, FileUploadOperation> entry2 : this.uploadOperationPathsEnc.entrySet()) {
            entry2.getValue().onNetworkChanged(slow);
        }
    }

    public void uploadFile(String location, boolean encrypted, boolean small, int type) {
        uploadFile(location, encrypted, small, 0, type, false);
    }

    public void uploadFile(String location, boolean encrypted, boolean small, int estimatedSize, int type, boolean forceSmallFile) {
        if (location != null) {
            fileLoaderQueue.postRunnable(new FileLoader$$ExternalSyntheticLambda2(this, encrypted, location, estimatedSize, type, forceSmallFile, small));
        }
    }

    /* renamed from: lambda$uploadFile$5$org-telegram-messenger-FileLoader  reason: not valid java name */
    public /* synthetic */ void m564lambda$uploadFile$5$orgtelegrammessengerFileLoader(boolean encrypted, String location, int estimatedSize, int type, boolean forceSmallFile, boolean small) {
        int esimated;
        final boolean z = encrypted;
        final String str = location;
        int i = estimatedSize;
        final boolean z2 = small;
        if (z) {
            if (this.uploadOperationPathsEnc.containsKey(str)) {
                return;
            }
        } else if (this.uploadOperationPaths.containsKey(str)) {
            return;
        }
        int esimated2 = estimatedSize;
        if (esimated2 == 0 || this.uploadSizes.get(str) == null) {
            esimated = esimated2;
        } else {
            this.uploadSizes.remove(str);
            esimated = 0;
        }
        FileUploadOperation operation = new FileUploadOperation(this.currentAccount, location, encrypted, esimated, type);
        FileLoaderDelegate fileLoaderDelegate = this.delegate;
        if (!(fileLoaderDelegate == null || i == 0)) {
            fileLoaderDelegate.fileUploadProgressChanged(operation, location, 0, (long) i, encrypted);
        }
        if (z) {
            this.uploadOperationPathsEnc.put(str, operation);
        } else {
            this.uploadOperationPaths.put(str, operation);
        }
        if (forceSmallFile) {
            operation.setForceSmallFile();
        }
        operation.setDelegate(new FileUploadOperation.FileUploadOperationDelegate() {
            public void didFinishUploadingFile(FileUploadOperation operation, TLRPC.InputFile inputFile, TLRPC.InputEncryptedFile inputEncryptedFile, byte[] key, byte[] iv) {
                FileLoader.fileLoaderQueue.postRunnable(new FileLoader$1$$ExternalSyntheticLambda1(this, z, str, z2, inputFile, inputEncryptedFile, key, iv, operation));
            }

            /* renamed from: lambda$didFinishUploadingFile$0$org-telegram-messenger-FileLoader$1  reason: not valid java name */
            public /* synthetic */ void m566xdfee369(boolean encrypted, String location, boolean small, TLRPC.InputFile inputFile, TLRPC.InputEncryptedFile inputEncryptedFile, byte[] key, byte[] iv, FileUploadOperation operation) {
                FileUploadOperation operation12;
                FileUploadOperation operation122;
                String str = location;
                if (encrypted) {
                    FileLoader.this.uploadOperationPathsEnc.remove(location);
                } else {
                    FileLoader.this.uploadOperationPaths.remove(location);
                }
                if (small) {
                    FileLoader.access$610(FileLoader.this);
                    if (FileLoader.this.currentUploadSmallOperationsCount < 1 && (operation122 = (FileUploadOperation) FileLoader.this.uploadSmallOperationQueue.poll()) != null) {
                        FileLoader.access$608(FileLoader.this);
                        operation122.start();
                    }
                } else {
                    FileLoader.access$810(FileLoader.this);
                    if (FileLoader.this.currentUploadOperationsCount < 1 && (operation12 = (FileUploadOperation) FileLoader.this.uploadOperationQueue.poll()) != null) {
                        FileLoader.access$808(FileLoader.this);
                        operation12.start();
                    }
                }
                if (FileLoader.this.delegate != null) {
                    FileLoader.this.delegate.fileDidUploaded(location, inputFile, inputEncryptedFile, key, iv, operation.getTotalFileSize());
                }
            }

            public void didFailedUploadingFile(FileUploadOperation operation) {
                FileLoader.fileLoaderQueue.postRunnable(new FileLoader$1$$ExternalSyntheticLambda0(this, z, str, z2));
            }

            /* renamed from: lambda$didFailedUploadingFile$1$org-telegram-messenger-FileLoader$1  reason: not valid java name */
            public /* synthetic */ void m565x2708d300(boolean encrypted, String location, boolean small) {
                FileUploadOperation operation1;
                FileUploadOperation operation12;
                if (encrypted) {
                    FileLoader.this.uploadOperationPathsEnc.remove(location);
                } else {
                    FileLoader.this.uploadOperationPaths.remove(location);
                }
                if (FileLoader.this.delegate != null) {
                    FileLoader.this.delegate.fileDidFailedUpload(location, encrypted);
                }
                if (small) {
                    FileLoader.access$610(FileLoader.this);
                    if (FileLoader.this.currentUploadSmallOperationsCount < 1 && (operation12 = (FileUploadOperation) FileLoader.this.uploadSmallOperationQueue.poll()) != null) {
                        FileLoader.access$608(FileLoader.this);
                        operation12.start();
                        return;
                    }
                    return;
                }
                FileLoader.access$810(FileLoader.this);
                if (FileLoader.this.currentUploadOperationsCount < 1 && (operation1 = (FileUploadOperation) FileLoader.this.uploadOperationQueue.poll()) != null) {
                    FileLoader.access$808(FileLoader.this);
                    operation1.start();
                }
            }

            public void didChangedUploadProgress(FileUploadOperation operation, long uploadedSize, long totalSize) {
                if (FileLoader.this.delegate != null) {
                    FileLoader.this.delegate.fileUploadProgressChanged(operation, str, uploadedSize, totalSize, z);
                }
            }
        });
        if (z2) {
            int i2 = this.currentUploadSmallOperationsCount;
            if (i2 < 1) {
                this.currentUploadSmallOperationsCount = i2 + 1;
                operation.start();
                return;
            }
            this.uploadSmallOperationQueue.add(operation);
            return;
        }
        int i3 = this.currentUploadOperationsCount;
        if (i3 < 1) {
            this.currentUploadOperationsCount = i3 + 1;
            operation.start();
            return;
        }
        this.uploadOperationQueue.add(operation);
    }

    private LinkedList<FileLoadOperation> getLoadOperationQueue(int datacenterId, int type) {
        SparseArray<LinkedList<FileLoadOperation>> queues;
        if (type == 2) {
            queues = this.audioLoadOperationQueues;
        } else if (type == 1) {
            queues = this.imageLoadOperationQueues;
        } else {
            queues = this.fileLoadOperationQueues;
        }
        LinkedList<FileLoadOperation> queue = queues.get(datacenterId);
        if (queue != null) {
            return queue;
        }
        LinkedList<FileLoadOperation> queue2 = new LinkedList<>();
        queues.put(datacenterId, queue2);
        return queue2;
    }

    private SparseIntArray getLoadOperationCount(int type) {
        if (type == 2) {
            return this.audioLoadOperationsCount;
        }
        if (type == 1) {
            return this.imageLoadOperationsCount;
        }
        return this.fileLoadOperationsCount;
    }

    public void setForceStreamLoadingFile(TLRPC.FileLocation location, String ext) {
        if (location != null) {
            fileLoaderQueue.postRunnable(new FileLoader$$ExternalSyntheticLambda12(this, location, ext));
        }
    }

    /* renamed from: lambda$setForceStreamLoadingFile$6$org-telegram-messenger-FileLoader  reason: not valid java name */
    public /* synthetic */ void m562xbvar_c(TLRPC.FileLocation location, String ext) {
        String attachFileName = getAttachFileName(location, ext);
        this.forceLoadingFile = attachFileName;
        FileLoadOperation operation = this.loadOperationPaths.get(attachFileName);
        if (operation != null) {
            if (operation.isPreloadVideoOperation()) {
                operation.setIsPreloadVideoOperation(false);
            }
            operation.setForceRequest(true);
            int datacenterId = operation.getDatacenterId();
            int queueType = operation.getQueueType();
            LinkedList<FileLoadOperation> downloadQueue = getLoadOperationQueue(datacenterId, queueType);
            SparseIntArray count = getLoadOperationCount(queueType);
            int index = downloadQueue.indexOf(operation);
            if (index >= 0) {
                downloadQueue.remove(index);
                if (operation.start()) {
                    count.put(datacenterId, count.get(datacenterId) + 1);
                }
                if (queueType == 0 && operation.wasStarted() && !this.activeFileLoadOperation.contains(operation)) {
                    pauseCurrentFileLoadOperations(operation);
                    this.activeFileLoadOperation.add(operation);
                    return;
                }
                return;
            }
            pauseCurrentFileLoadOperations(operation);
            operation.start();
            if (queueType == 0 && !this.activeFileLoadOperation.contains(operation)) {
                this.activeFileLoadOperation.add(operation);
            }
        }
    }

    public void cancelLoadFile(TLRPC.Document document) {
        cancelLoadFile(document, false);
    }

    public void cancelLoadFile(TLRPC.Document document, boolean deleteFile) {
        cancelLoadFile(document, (SecureDocument) null, (WebFile) null, (TLRPC.FileLocation) null, (String) null, (String) null, deleteFile);
    }

    public void cancelLoadFile(SecureDocument document) {
        cancelLoadFile((TLRPC.Document) null, document, (WebFile) null, (TLRPC.FileLocation) null, (String) null, (String) null, false);
    }

    public void cancelLoadFile(WebFile document) {
        cancelLoadFile((TLRPC.Document) null, (SecureDocument) null, document, (TLRPC.FileLocation) null, (String) null, (String) null, false);
    }

    public void cancelLoadFile(TLRPC.PhotoSize photo) {
        cancelLoadFile(photo, false);
    }

    public void cancelLoadFile(TLRPC.PhotoSize photo, boolean deleteFile) {
        cancelLoadFile((TLRPC.Document) null, (SecureDocument) null, (WebFile) null, photo.location, (String) null, (String) null, deleteFile);
    }

    public void cancelLoadFile(TLRPC.FileLocation location, String ext) {
        cancelLoadFile(location, ext, false);
    }

    public void cancelLoadFile(TLRPC.FileLocation location, String ext, boolean deleteFile) {
        cancelLoadFile((TLRPC.Document) null, (SecureDocument) null, (WebFile) null, location, ext, (String) null, deleteFile);
    }

    public void cancelLoadFile(String fileName) {
        cancelLoadFile((TLRPC.Document) null, (SecureDocument) null, (WebFile) null, (TLRPC.FileLocation) null, (String) null, fileName, true);
    }

    public void cancelLoadFiles(ArrayList<String> fileNames) {
        int N = fileNames.size();
        for (int a = 0; a < N; a++) {
            cancelLoadFile((TLRPC.Document) null, (SecureDocument) null, (WebFile) null, (TLRPC.FileLocation) null, (String) null, fileNames.get(a), true);
        }
    }

    private void cancelLoadFile(TLRPC.Document document, SecureDocument secureDocument, WebFile webDocument, TLRPC.FileLocation location, String locationExt, String name, boolean deleteFile) {
        String fileName;
        if (location != null || document != null || webDocument != null || secureDocument != null || !TextUtils.isEmpty(name)) {
            if (location != null) {
                fileName = getAttachFileName(location, locationExt);
            } else if (document != null) {
                fileName = getAttachFileName(document);
            } else if (secureDocument != null) {
                fileName = getAttachFileName(secureDocument);
            } else if (webDocument != null) {
                fileName = getAttachFileName(webDocument);
            } else {
                fileName = name;
            }
            boolean removed = this.loadOperationPathsUI.remove(fileName) != null;
            fileLoaderQueue.postRunnable(new FileLoader$$ExternalSyntheticLambda7(this, fileName, deleteFile));
            if (removed && document != null) {
                AndroidUtilities.runOnUIThread(new FileLoader$$ExternalSyntheticLambda5(this));
            }
        }
    }

    /* renamed from: lambda$cancelLoadFile$7$org-telegram-messenger-FileLoader  reason: not valid java name */
    public /* synthetic */ void m553lambda$cancelLoadFile$7$orgtelegrammessengerFileLoader(String fileName, boolean deleteFile) {
        FileLoadOperation operation = this.loadOperationPaths.remove(fileName);
        if (operation != null) {
            int queueType = operation.getQueueType();
            int datacenterId = operation.getDatacenterId();
            if (!getLoadOperationQueue(datacenterId, queueType).remove(operation)) {
                SparseIntArray count = getLoadOperationCount(queueType);
                count.put(datacenterId, count.get(datacenterId) - 1);
            }
            if (queueType == 0) {
                this.activeFileLoadOperation.remove(operation);
            }
            operation.cancel(deleteFile);
        }
    }

    /* renamed from: lambda$cancelLoadFile$8$org-telegram-messenger-FileLoader  reason: not valid java name */
    public /* synthetic */ void m554lambda$cancelLoadFile$8$orgtelegrammessengerFileLoader() {
        getNotificationCenter().postNotificationName(NotificationCenter.onDownloadingFilesChanged, new Object[0]);
    }

    public boolean isLoadingFile(String fileName) {
        return fileName != null && this.loadOperationPathsUI.containsKey(fileName);
    }

    public float getBufferedProgressFromPosition(float position, String fileName) {
        FileLoadOperation loadOperation;
        if (!TextUtils.isEmpty(fileName) && (loadOperation = this.loadOperationPaths.get(fileName)) != null) {
            return loadOperation.getDownloadedLengthFromOffset(position);
        }
        return 0.0f;
    }

    public void loadFile(ImageLocation imageLocation, Object parentObject, String ext, int priority, int cacheType) {
        int cacheType2;
        ImageLocation imageLocation2 = imageLocation;
        if (imageLocation2 != null) {
            if (cacheType != 0 || (!imageLocation.isEncrypted() && (imageLocation2.photoSize == null || imageLocation.getSize() != 0))) {
                cacheType2 = cacheType;
            } else {
                cacheType2 = 1;
            }
            loadFile(imageLocation2.document, imageLocation2.secureDocument, imageLocation2.webFile, imageLocation2.location, imageLocation, parentObject, ext, imageLocation.getSize(), priority, cacheType2);
        }
    }

    public void loadFile(SecureDocument secureDocument, int priority) {
        if (secureDocument != null) {
            loadFile((TLRPC.Document) null, secureDocument, (WebFile) null, (TLRPC.TL_fileLocationToBeDeprecated) null, (ImageLocation) null, (Object) null, (String) null, 0, priority, 1);
        }
    }

    public void loadFile(TLRPC.Document document, Object parentObject, int priority, int cacheType) {
        if (document != null) {
            if (cacheType == 0 && document.key != null) {
                cacheType = 1;
            }
            loadFile(document, (SecureDocument) null, (WebFile) null, (TLRPC.TL_fileLocationToBeDeprecated) null, (ImageLocation) null, parentObject, (String) null, 0, priority, cacheType);
        }
    }

    public void loadFile(WebFile document, int priority, int cacheType) {
        loadFile((TLRPC.Document) null, (SecureDocument) null, document, (TLRPC.TL_fileLocationToBeDeprecated) null, (ImageLocation) null, (Object) null, (String) null, 0, priority, cacheType);
    }

    private void pauseCurrentFileLoadOperations(FileLoadOperation newOperation) {
        int a = 0;
        while (a < this.activeFileLoadOperation.size()) {
            FileLoadOperation operation = this.activeFileLoadOperation.get(a);
            if (operation != newOperation && operation.getDatacenterId() == newOperation.getDatacenterId() && !operation.getFileName().equals(this.forceLoadingFile)) {
                this.activeFileLoadOperation.remove(operation);
                a--;
                int datacenterId = operation.getDatacenterId();
                int queueType = operation.getQueueType();
                LinkedList<FileLoadOperation> downloadQueue = getLoadOperationQueue(datacenterId, queueType);
                SparseIntArray count = getLoadOperationCount(queueType);
                downloadQueue.add(0, operation);
                if (operation.wasStarted()) {
                    count.put(datacenterId, count.get(datacenterId) - 1);
                }
                operation.pause();
            }
            a++;
        }
    }

    private FileLoadOperation loadFileInternal(TLRPC.Document document, SecureDocument secureDocument, WebFile webDocument, TLRPC.TL_fileLocationToBeDeprecated location, ImageLocation imageLocation, Object parentObject, String locationExt, int locationSize, int priority, FileLoadOperationStream stream, int streamOffset, boolean streamPriority, int cacheType) {
        String fileName;
        File storeDir;
        FileLoadOperation operation;
        int type;
        int queueType;
        File storeDir2;
        boolean started;
        TLRPC.Document document2 = document;
        SecureDocument secureDocument2 = secureDocument;
        WebFile webFile = webDocument;
        TLRPC.TL_fileLocationToBeDeprecated tL_fileLocationToBeDeprecated = location;
        ImageLocation imageLocation2 = imageLocation;
        Object obj = parentObject;
        String str = locationExt;
        int i = priority;
        FileLoadOperationStream fileLoadOperationStream = stream;
        int i2 = streamOffset;
        boolean z = streamPriority;
        int i3 = cacheType;
        if (tL_fileLocationToBeDeprecated != null) {
            fileName = getAttachFileName(tL_fileLocationToBeDeprecated, str);
        } else if (secureDocument2 != null) {
            fileName = getAttachFileName(secureDocument);
        } else if (document2 != null) {
            fileName = getAttachFileName(document);
        } else if (webFile != null) {
            fileName = getAttachFileName(webDocument);
        } else {
            fileName = null;
        }
        if (fileName == null) {
            boolean z2 = z;
            int i4 = i2;
            FileLoadOperationStream fileLoadOperationStream2 = fileLoadOperationStream;
            int i5 = i;
            return null;
        } else if (fileName.contains("-NUM")) {
            String str2 = fileName;
            boolean z3 = z;
            int i6 = i2;
            FileLoadOperationStream fileLoadOperationStream3 = fileLoadOperationStream;
            int i7 = i;
            return null;
        } else {
            if (i3 != 10 && !TextUtils.isEmpty(fileName) && !fileName.contains("-NUM")) {
                this.loadOperationPathsUI.put(fileName, true);
            }
            if (document2 != null && (obj instanceof MessageObject) && ((MessageObject) obj).putInDownloadsStore) {
                getDownloadController().startDownloadFile(document2, (MessageObject) obj);
            }
            FileLoadOperation operation2 = this.loadOperationPaths.get(fileName);
            if (operation2 != null) {
                if (i3 != 10 && operation2.isPreloadVideoOperation()) {
                    operation2.setIsPreloadVideoOperation(false);
                }
                if (fileLoadOperationStream != null || i > 0) {
                    int datacenterId = operation2.getDatacenterId();
                    operation2.setForceRequest(true);
                    int queueType2 = operation2.getQueueType();
                    LinkedList<FileLoadOperation> downloadQueue = getLoadOperationQueue(datacenterId, queueType2);
                    String str3 = fileName;
                    SparseIntArray count = getLoadOperationCount(queueType2);
                    int index = downloadQueue.indexOf(operation2);
                    if (index >= 0) {
                        downloadQueue.remove(index);
                        if (fileLoadOperationStream != null) {
                            if (operation2.start(fileLoadOperationStream, i2, z)) {
                                int i8 = index;
                                count.put(datacenterId, count.get(datacenterId) + 1);
                            }
                            if (queueType2 == 0 && operation2.wasStarted() && !this.activeFileLoadOperation.contains(operation2)) {
                                pauseCurrentFileLoadOperations(operation2);
                                this.activeFileLoadOperation.add(operation2);
                            }
                        } else {
                            downloadQueue.add(0, operation2);
                        }
                    } else {
                        if (fileLoadOperationStream != null) {
                            pauseCurrentFileLoadOperations(operation2);
                        }
                        operation2.start(fileLoadOperationStream, i2, z);
                        if (queueType2 == 0 && !this.activeFileLoadOperation.contains(operation2)) {
                            this.activeFileLoadOperation.add(operation2);
                        }
                    }
                } else {
                    String str4 = fileName;
                }
                operation2.updateProgress();
                return operation2;
            }
            String fileName2 = fileName;
            File tempDir = getDirectory(4);
            File storeDir3 = tempDir;
            if (secureDocument2 != null) {
                WebFile webFile2 = webDocument;
                int i9 = locationSize;
                storeDir = storeDir3;
                operation = new FileLoadOperation(secureDocument2);
                type = 3;
            } else if (tL_fileLocationToBeDeprecated != null) {
                WebFile webFile3 = webDocument;
                storeDir = storeDir3;
                operation = new FileLoadOperation(imageLocation2, obj, str, locationSize);
                type = 0;
            } else {
                int i10 = locationSize;
                if (document2 != null) {
                    FileLoadOperation operation3 = new FileLoadOperation(document2, obj);
                    if (MessageObject.isVoiceDocument(document)) {
                        WebFile webFile4 = webDocument;
                        storeDir = storeDir3;
                        operation = operation3;
                        type = 1;
                    } else if (MessageObject.isVideoDocument(document)) {
                        WebFile webFile5 = webDocument;
                        storeDir = storeDir3;
                        operation = operation3;
                        type = 2;
                    } else {
                        WebFile webFile6 = webDocument;
                        storeDir = storeDir3;
                        operation = operation3;
                        type = 3;
                    }
                } else {
                    WebFile webFile7 = webDocument;
                    if (webFile7 != null) {
                        FileLoadOperation fileLoadOperation = operation2;
                        storeDir = storeDir3;
                        FileLoadOperation operation4 = new FileLoadOperation(this.currentAccount, webFile7);
                        if (webFile7.location != null) {
                            operation = operation4;
                            type = 4;
                        } else if (MessageObject.isVoiceWebDocument(webDocument)) {
                            operation = operation4;
                            type = 1;
                        } else if (MessageObject.isVideoWebDocument(webDocument)) {
                            operation = operation4;
                            type = 2;
                        } else if (MessageObject.isImageWebDocument(webDocument)) {
                            operation = operation4;
                            type = 0;
                        } else {
                            operation = operation4;
                            type = 3;
                        }
                    } else {
                        FileLoadOperation operation5 = operation2;
                        storeDir = storeDir3;
                        type = 4;
                        operation = operation5;
                    }
                }
            }
            if (type == 1) {
                queueType = 2;
            } else if (secureDocument2 != null || ((tL_fileLocationToBeDeprecated != null && (imageLocation2 == null || imageLocation2.imageType != 2)) || MessageObject.isImageWebDocument(webDocument))) {
                queueType = 1;
            } else {
                queueType = 0;
            }
            int i11 = cacheType;
            if (i11 == 0 || i11 == 10) {
                storeDir2 = getDirectory(type);
            } else {
                if (i11 == 2) {
                    operation.setEncryptFile(true);
                }
                storeDir2 = storeDir;
            }
            operation.setPaths(this.currentAccount, fileName2, queueType, storeDir2, tempDir);
            if (i11 == 10) {
                operation.setIsPreloadVideoOperation(true);
            }
            final int finalType = type;
            int i12 = type;
            FileLoadOperation operation6 = operation;
            String fileName3 = fileName2;
            int queueType3 = queueType;
            final TLRPC.Document document3 = document;
            boolean z4 = streamPriority;
            int queueType4 = queueType3;
            final Object obj2 = parentObject;
            int i13 = i2;
            final String str5 = fileName3;
            FileLoadOperationStream fileLoadOperationStream4 = fileLoadOperationStream;
            final int i14 = queueType4;
            FileLoadOperation.FileLoadOperationDelegate r0 = new FileLoadOperation.FileLoadOperationDelegate() {
                public void didFinishLoadingFile(FileLoadOperation operation, File finalFile) {
                    if (operation.isPreloadVideoOperation() || !operation.isPreloadFinished()) {
                        if (document3 != null) {
                            Object obj = obj2;
                            if ((obj instanceof MessageObject) && ((MessageObject) obj).putInDownloadsStore) {
                                FileLoader.this.getDownloadController().onDownloadComplete((MessageObject) obj2);
                            }
                        }
                        if (!operation.isPreloadVideoOperation()) {
                            FileLoader.this.loadOperationPathsUI.remove(str5);
                            if (FileLoader.this.delegate != null) {
                                FileLoader.this.delegate.fileDidLoaded(str5, finalFile, obj2, finalType);
                            }
                        }
                        FileLoader.this.checkDownloadQueue(operation.getDatacenterId(), i14, str5);
                    }
                }

                public void didFailedLoadingFile(FileLoadOperation operation, int reason) {
                    FileLoader.this.loadOperationPathsUI.remove(str5);
                    FileLoader.this.checkDownloadQueue(operation.getDatacenterId(), i14, str5);
                    if (FileLoader.this.delegate != null) {
                        FileLoader.this.delegate.fileDidFailedLoad(str5, reason);
                    }
                    if (document3 != null && (obj2 instanceof MessageObject) && reason == 0) {
                        FileLoader.this.getDownloadController().onDownloadFail((MessageObject) obj2, reason);
                    }
                }

                public void didChangedLoadProgress(FileLoadOperation operation, long uploadedSize, long totalSize) {
                    if (FileLoader.this.delegate != null) {
                        FileLoader.this.delegate.fileLoadProgressChanged(operation, str5, uploadedSize, totalSize);
                    }
                }
            };
            operation6.setDelegate(r0);
            int datacenterId2 = operation6.getDatacenterId();
            String fileName4 = fileName3;
            this.loadOperationPaths.put(fileName4, operation6);
            int i15 = priority;
            operation6.setPriority(i15);
            int queueType5 = queueType4;
            if (queueType5 == 2) {
                int maxCount = i15 > 0 ? 3 : 1;
                AnonymousClass2 r17 = r0;
                int count2 = this.audioLoadOperationsCount.get(datacenterId2);
                boolean z5 = fileLoadOperationStream4 != null || count2 < maxCount;
                started = z5;
                if (!z5) {
                    int i16 = finalType;
                } else if (operation6.start(fileLoadOperationStream4, i13, z4)) {
                    String str6 = fileName4;
                    int i17 = finalType;
                    this.audioLoadOperationsCount.put(datacenterId2, count2 + 1);
                } else {
                    int i18 = finalType;
                }
            } else {
                FileLoadOperation.FileLoadOperationDelegate fileLoadOperationDelegate = r0;
                String str7 = fileName4;
                int i19 = finalType;
                boolean z6 = true;
                if (queueType5 == 1) {
                    int maxCount2 = i15 > 0 ? 6 : 2;
                    int count3 = this.imageLoadOperationsCount.get(datacenterId2);
                    if (fileLoadOperationStream4 == null && count3 >= maxCount2) {
                        z6 = false;
                    }
                    started = z6;
                    if (z6 && operation6.start(fileLoadOperationStream4, i13, z4)) {
                        this.imageLoadOperationsCount.put(datacenterId2, count3 + 1);
                    }
                } else {
                    int maxCount3 = i15 > 0 ? 4 : 1;
                    int count4 = this.fileLoadOperationsCount.get(datacenterId2);
                    if (fileLoadOperationStream4 == null && count4 >= maxCount3) {
                        z6 = false;
                    }
                    started = z6;
                    if (z6) {
                        if (operation6.start(fileLoadOperationStream4, i13, z4)) {
                            this.fileLoadOperationsCount.put(datacenterId2, count4 + 1);
                            this.activeFileLoadOperation.add(operation6);
                        }
                        if (operation6.wasStarted() && fileLoadOperationStream4 != null) {
                            pauseCurrentFileLoadOperations(operation6);
                        }
                    }
                }
            }
            if (!started) {
                addOperationToQueue(operation6, getLoadOperationQueue(datacenterId2, queueType5));
            }
            return operation6;
        }
    }

    private void addOperationToQueue(FileLoadOperation operation, LinkedList<FileLoadOperation> queue) {
        int priority = operation.getPriority();
        if (priority > 0) {
            int index = queue.size();
            int a = 0;
            int size = queue.size();
            while (true) {
                if (a >= size) {
                    break;
                } else if (queue.get(a).getPriority() < priority) {
                    index = a;
                    break;
                } else {
                    a++;
                }
            }
            queue.add(index, operation);
            return;
        }
        queue.add(operation);
    }

    private void loadFile(TLRPC.Document document, SecureDocument secureDocument, WebFile webDocument, TLRPC.TL_fileLocationToBeDeprecated location, ImageLocation imageLocation, Object parentObject, String locationExt, int locationSize, int priority, int cacheType) {
        String fileName;
        TLRPC.TL_fileLocationToBeDeprecated tL_fileLocationToBeDeprecated = location;
        if (tL_fileLocationToBeDeprecated != null) {
            fileName = getAttachFileName(tL_fileLocationToBeDeprecated, locationExt);
        } else {
            String str = locationExt;
            if (document != null) {
                fileName = getAttachFileName(document);
            } else if (webDocument != null) {
                fileName = getAttachFileName(webDocument);
            } else {
                fileName = null;
            }
        }
        if (cacheType != 10 && !TextUtils.isEmpty(fileName) && !fileName.contains("-NUM")) {
            this.loadOperationPathsUI.put(fileName, true);
        }
        FileLoader$$ExternalSyntheticLambda9 fileLoader$$ExternalSyntheticLambda9 = r0;
        DispatchQueue dispatchQueue = fileLoaderQueue;
        FileLoader$$ExternalSyntheticLambda9 fileLoader$$ExternalSyntheticLambda92 = new FileLoader$$ExternalSyntheticLambda9(this, document, secureDocument, webDocument, location, imageLocation, parentObject, locationExt, locationSize, priority, cacheType);
        dispatchQueue.postRunnable(fileLoader$$ExternalSyntheticLambda9);
    }

    /* renamed from: lambda$loadFile$9$org-telegram-messenger-FileLoader  reason: not valid java name */
    public /* synthetic */ void m558lambda$loadFile$9$orgtelegrammessengerFileLoader(TLRPC.Document document, SecureDocument secureDocument, WebFile webDocument, TLRPC.TL_fileLocationToBeDeprecated location, ImageLocation imageLocation, Object parentObject, String locationExt, int locationSize, int priority, int cacheType) {
        loadFileInternal(document, secureDocument, webDocument, location, imageLocation, parentObject, locationExt, locationSize, priority, (FileLoadOperationStream) null, 0, false, cacheType);
    }

    /* access modifiers changed from: protected */
    public FileLoadOperation loadStreamFile(FileLoadOperationStream stream, TLRPC.Document document, ImageLocation location, Object parentObject, int offset, boolean priority) {
        CountDownLatch semaphore = new CountDownLatch(1);
        FileLoadOperation[] result = new FileLoadOperation[1];
        fileLoaderQueue.postRunnable(new FileLoader$$ExternalSyntheticLambda4(this, result, document, location, parentObject, stream, offset, priority, semaphore));
        try {
            semaphore.await();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        return result[0];
    }

    /* renamed from: lambda$loadStreamFile$10$org-telegram-messenger-FileLoader  reason: not valid java name */
    public /* synthetic */ void m559lambda$loadStreamFile$10$orgtelegrammessengerFileLoader(FileLoadOperation[] result, TLRPC.Document document, ImageLocation location, Object parentObject, FileLoadOperationStream stream, int offset, boolean priority, CountDownLatch semaphore) {
        ImageLocation imageLocation = location;
        String str = null;
        TLRPC.TL_fileLocationToBeDeprecated tL_fileLocationToBeDeprecated = (document != null || imageLocation == null) ? null : imageLocation.location;
        if (document == null && imageLocation != null) {
            str = "mp4";
        }
        result[0] = loadFileInternal(document, (SecureDocument) null, (WebFile) null, tL_fileLocationToBeDeprecated, location, parentObject, str, (document != null || imageLocation == null) ? 0 : imageLocation.currentSize, 1, stream, offset, priority, document == null ? 1 : 0);
        semaphore.countDown();
    }

    /* access modifiers changed from: private */
    public void checkDownloadQueue(int datacenterId, int queueType, String fileName) {
        fileLoaderQueue.postRunnable(new FileLoader$$ExternalSyntheticLambda6(this, fileName, datacenterId, queueType));
    }

    /* renamed from: lambda$checkDownloadQueue$11$org-telegram-messenger-FileLoader  reason: not valid java name */
    public /* synthetic */ void m556lambda$checkDownloadQueue$11$orgtelegrammessengerFileLoader(String fileName, int datacenterId, int queueType) {
        FileLoadOperation operation = this.loadOperationPaths.remove(fileName);
        LinkedList<FileLoadOperation> queue = getLoadOperationQueue(datacenterId, queueType);
        SparseIntArray operationCount = getLoadOperationCount(queueType);
        int count = operationCount.get(datacenterId);
        if (operation != null) {
            if (operation.wasStarted()) {
                count--;
                operationCount.put(datacenterId, count);
            } else {
                queue.remove(operation);
            }
            if (queueType == 0) {
                this.activeFileLoadOperation.remove(operation);
            }
        }
        while (!queue.isEmpty()) {
            FileLoadOperation operation2 = queue.get(0);
            int maxCount = 3;
            int i = 2;
            if (queueType == 2) {
                if (operation2.getPriority() == 0) {
                    maxCount = 1;
                }
            } else if (queueType == 1) {
                if (operation2.getPriority() != 0) {
                    i = 6;
                }
                maxCount = i;
            } else if (!operation2.isForceRequest()) {
                maxCount = 1;
            }
            if (count < maxCount) {
                FileLoadOperation operation3 = queue.poll();
                if (operation3 != null && operation3.start()) {
                    count++;
                    operationCount.put(datacenterId, count);
                    if (queueType == 0 && !this.activeFileLoadOperation.contains(operation3)) {
                        this.activeFileLoadOperation.add(operation3);
                    }
                }
            } else {
                return;
            }
        }
    }

    public void setDelegate(FileLoaderDelegate fileLoaderDelegate) {
        this.delegate = fileLoaderDelegate;
    }

    public static String getMessageFileName(TLRPC.Message message) {
        TLRPC.WebDocument document;
        TLRPC.PhotoSize sizeFull;
        TLRPC.PhotoSize sizeFull2;
        TLRPC.PhotoSize sizeFull3;
        if (message == null) {
            return "";
        }
        if (message instanceof TLRPC.TL_messageService) {
            if (message.action.photo != null) {
                ArrayList<TLRPC.PhotoSize> sizes = message.action.photo.sizes;
                if (sizes.size() > 0 && (sizeFull3 = getClosestPhotoSizeWithSize(sizes, AndroidUtilities.getPhotoSize())) != null) {
                    return getAttachFileName(sizeFull3);
                }
            }
        } else if (message.media instanceof TLRPC.TL_messageMediaDocument) {
            return getAttachFileName(message.media.document);
        } else {
            if (message.media instanceof TLRPC.TL_messageMediaPhoto) {
                ArrayList<TLRPC.PhotoSize> sizes2 = message.media.photo.sizes;
                if (sizes2.size() > 0 && (sizeFull2 = getClosestPhotoSizeWithSize(sizes2, AndroidUtilities.getPhotoSize(), false, (TLRPC.PhotoSize) null, true)) != null) {
                    return getAttachFileName(sizeFull2);
                }
            } else if (message.media instanceof TLRPC.TL_messageMediaWebPage) {
                if (message.media.webpage.document != null) {
                    return getAttachFileName(message.media.webpage.document);
                }
                if (message.media.webpage.photo != null) {
                    ArrayList<TLRPC.PhotoSize> sizes3 = message.media.webpage.photo.sizes;
                    if (sizes3.size() > 0 && (sizeFull = getClosestPhotoSizeWithSize(sizes3, AndroidUtilities.getPhotoSize())) != null) {
                        return getAttachFileName(sizeFull);
                    }
                }
            } else if ((message.media instanceof TLRPC.TL_messageMediaInvoice) && (document = ((TLRPC.TL_messageMediaInvoice) message.media).photo) != null) {
                return Utilities.MD5(document.url) + "." + ImageLoader.getHttpUrlExtension(document.url, getMimeTypePart(document.mime_type));
            }
        }
        return "";
    }

    public static File getPathToMessage(TLRPC.Message message) {
        TLRPC.PhotoSize sizeFull;
        TLRPC.PhotoSize sizeFull2;
        TLRPC.PhotoSize sizeFull3;
        if (message == null) {
            return new File("");
        }
        if (!(message instanceof TLRPC.TL_messageService)) {
            boolean z = false;
            if (message.media instanceof TLRPC.TL_messageMediaDocument) {
                TLRPC.Document document = message.media.document;
                if (message.media.ttl_seconds != 0) {
                    z = true;
                }
                return getPathToAttach(document, z);
            } else if (message.media instanceof TLRPC.TL_messageMediaPhoto) {
                ArrayList<TLRPC.PhotoSize> sizes = message.media.photo.sizes;
                if (sizes.size() > 0 && (sizeFull2 = getClosestPhotoSizeWithSize(sizes, AndroidUtilities.getPhotoSize(), false, (TLRPC.PhotoSize) null, true)) != null) {
                    if (message.media.ttl_seconds != 0) {
                        z = true;
                    }
                    return getPathToAttach(sizeFull2, z);
                }
            } else if (message.media instanceof TLRPC.TL_messageMediaWebPage) {
                if (message.media.webpage.document != null) {
                    return getPathToAttach(message.media.webpage.document);
                }
                if (message.media.webpage.photo != null) {
                    ArrayList<TLRPC.PhotoSize> sizes2 = message.media.webpage.photo.sizes;
                    if (sizes2.size() > 0 && (sizeFull = getClosestPhotoSizeWithSize(sizes2, AndroidUtilities.getPhotoSize())) != null) {
                        return getPathToAttach(sizeFull);
                    }
                }
            } else if (message.media instanceof TLRPC.TL_messageMediaInvoice) {
                return getPathToAttach(((TLRPC.TL_messageMediaInvoice) message.media).photo, true);
            }
        } else if (message.action.photo != null) {
            ArrayList<TLRPC.PhotoSize> sizes3 = message.action.photo.sizes;
            if (sizes3.size() > 0 && (sizeFull3 = getClosestPhotoSizeWithSize(sizes3, AndroidUtilities.getPhotoSize())) != null) {
                return getPathToAttach(sizeFull3);
            }
        }
        return new File("");
    }

    public static File getPathToAttach(TLObject attach) {
        return getPathToAttach(attach, (String) null, false);
    }

    public static File getPathToAttach(TLObject attach, boolean forceCache) {
        return getPathToAttach(attach, (String) null, forceCache);
    }

    public static File getPathToAttach(TLObject attach, String ext, boolean forceCache) {
        return getPathToAttach(attach, (String) null, ext, forceCache);
    }

    public static File getPathToAttach(TLObject attach, String size, String ext, boolean forceCache) {
        File dir = null;
        if (forceCache) {
            dir = getDirectory(4);
        } else if (attach instanceof TLRPC.Document) {
            TLRPC.Document document = (TLRPC.Document) attach;
            if (document.key != null) {
                dir = getDirectory(4);
            } else if (MessageObject.isVoiceDocument(document)) {
                dir = getDirectory(1);
            } else if (MessageObject.isVideoDocument(document)) {
                dir = getDirectory(2);
            } else {
                dir = getDirectory(3);
            }
        } else if (attach instanceof TLRPC.Photo) {
            return getPathToAttach(getClosestPhotoSizeWithSize(((TLRPC.Photo) attach).sizes, AndroidUtilities.getPhotoSize()), ext, false);
        } else {
            if (attach instanceof TLRPC.PhotoSize) {
                TLRPC.PhotoSize photoSize = (TLRPC.PhotoSize) attach;
                if ((photoSize instanceof TLRPC.TL_photoStrippedSize) || (photoSize instanceof TLRPC.TL_photoPathSize)) {
                    dir = null;
                } else if (photoSize.location == null || photoSize.location.key != null || ((photoSize.location.volume_id == -2147483648L && photoSize.location.local_id < 0) || photoSize.size < 0)) {
                    dir = getDirectory(4);
                } else {
                    dir = getDirectory(0);
                }
            } else if (attach instanceof TLRPC.TL_videoSize) {
                TLRPC.TL_videoSize videoSize = (TLRPC.TL_videoSize) attach;
                if (videoSize.location == null || videoSize.location.key != null || ((videoSize.location.volume_id == -2147483648L && videoSize.location.local_id < 0) || videoSize.size < 0)) {
                    dir = getDirectory(4);
                } else {
                    dir = getDirectory(0);
                }
            } else if (attach instanceof TLRPC.FileLocation) {
                TLRPC.FileLocation fileLocation = (TLRPC.FileLocation) attach;
                if (fileLocation.key != null || (fileLocation.volume_id == -2147483648L && fileLocation.local_id < 0)) {
                    dir = getDirectory(4);
                } else {
                    dir = getDirectory(0);
                }
            } else if ((attach instanceof TLRPC.UserProfilePhoto) || (attach instanceof TLRPC.ChatPhoto)) {
                if (size == null) {
                    size = "s";
                }
                if ("s".equals(size)) {
                    dir = getDirectory(4);
                } else {
                    dir = getDirectory(0);
                }
            } else if (attach instanceof WebFile) {
                WebFile document2 = (WebFile) attach;
                if (document2.mime_type.startsWith("image/")) {
                    dir = getDirectory(0);
                } else if (document2.mime_type.startsWith("audio/")) {
                    dir = getDirectory(1);
                } else if (document2.mime_type.startsWith("video/")) {
                    dir = getDirectory(2);
                } else {
                    dir = getDirectory(3);
                }
            } else if ((attach instanceof TLRPC.TL_secureFile) || (attach instanceof SecureDocument)) {
                dir = getDirectory(4);
            }
        }
        if (dir == null) {
            return new File("");
        }
        return new File(dir, getAttachFileName(attach, ext));
    }

    public static TLRPC.PhotoSize getClosestPhotoSizeWithSize(ArrayList<TLRPC.PhotoSize> sizes, int side) {
        return getClosestPhotoSizeWithSize(sizes, side, false);
    }

    public static TLRPC.PhotoSize getClosestPhotoSizeWithSize(ArrayList<TLRPC.PhotoSize> sizes, int side, boolean byMinSide) {
        return getClosestPhotoSizeWithSize(sizes, side, byMinSide, (TLRPC.PhotoSize) null, false);
    }

    public static TLRPC.PhotoSize getClosestPhotoSizeWithSize(ArrayList<TLRPC.PhotoSize> sizes, int side, boolean byMinSide, TLRPC.PhotoSize toIgnore, boolean ignoreStripped) {
        if (sizes == null || sizes.isEmpty()) {
            return null;
        }
        int lastSide = 0;
        TLRPC.PhotoSize closestObject = null;
        for (int a = 0; a < sizes.size(); a++) {
            TLRPC.PhotoSize obj = sizes.get(a);
            if (obj != null && obj != toIgnore && !(obj instanceof TLRPC.TL_photoSizeEmpty) && !(obj instanceof TLRPC.TL_photoPathSize) && (!ignoreStripped || !(obj instanceof TLRPC.TL_photoStrippedSize))) {
                if (byMinSide) {
                    int currentSide = Math.min(obj.h, obj.w);
                    if (closestObject == null || ((side > 100 && closestObject.location != null && closestObject.location.dc_id == Integer.MIN_VALUE) || (obj instanceof TLRPC.TL_photoCachedSize) || (side > lastSide && lastSide < currentSide))) {
                        closestObject = obj;
                        lastSide = currentSide;
                    }
                } else {
                    int currentSide2 = Math.max(obj.w, obj.h);
                    if (closestObject == null || ((side > 100 && closestObject.location != null && closestObject.location.dc_id == Integer.MIN_VALUE) || (obj instanceof TLRPC.TL_photoCachedSize) || (currentSide2 <= side && lastSide < currentSide2))) {
                        closestObject = obj;
                        lastSide = currentSide2;
                    }
                }
            }
        }
        return closestObject;
    }

    public static TLRPC.TL_photoPathSize getPathPhotoSize(ArrayList<TLRPC.PhotoSize> sizes) {
        if (sizes == null || sizes.isEmpty()) {
            return null;
        }
        for (int a = 0; a < sizes.size(); a++) {
            TLRPC.PhotoSize obj = sizes.get(a);
            if (!(obj instanceof TLRPC.TL_photoPathSize)) {
                return (TLRPC.TL_photoPathSize) obj;
            }
        }
        return null;
    }

    public static String getFileExtension(File file) {
        String name = file.getName();
        try {
            return name.substring(name.lastIndexOf(46) + 1);
        } catch (Exception e) {
            return "";
        }
    }

    public static String fixFileName(String fileName) {
        if (fileName != null) {
            return fileName.replaceAll("[\u0001-\u001f<>‮:\"/\\\\|?*]+", "").trim();
        }
        return fileName;
    }

    public static String getDocumentFileName(TLRPC.Document document) {
        if (document == null) {
            return null;
        }
        if (document.file_name_fixed != null) {
            return document.file_name_fixed;
        }
        String fileName = null;
        if (document != null) {
            if (document.file_name != null) {
                fileName = document.file_name;
            } else {
                for (int a = 0; a < document.attributes.size(); a++) {
                    TLRPC.DocumentAttribute documentAttribute = document.attributes.get(a);
                    if (documentAttribute instanceof TLRPC.TL_documentAttributeFilename) {
                        fileName = documentAttribute.file_name;
                    }
                }
            }
        }
        String fileName2 = fixFileName(fileName);
        return fileName2 != null ? fileName2 : "";
    }

    public static String getMimeTypePart(String mime) {
        int lastIndexOf = mime.lastIndexOf(47);
        int index = lastIndexOf;
        if (lastIndexOf != -1) {
            return mime.substring(index + 1);
        }
        return "";
    }

    public static String getExtensionByMimeType(String mime) {
        if (mime == null) {
            return "";
        }
        char c = 65535;
        switch (mime.hashCode()) {
            case 187091926:
                if (mime.equals("audio/ogg")) {
                    c = 2;
                    break;
                }
                break;
            case 1331848029:
                if (mime.equals("video/mp4")) {
                    c = 0;
                    break;
                }
                break;
            case 2039520277:
                if (mime.equals("video/x-matroska")) {
                    c = 1;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                return ".mp4";
            case 1:
                return ".mkv";
            case 2:
                return ".ogg";
            default:
                return "";
        }
    }

    public static File getInternalCacheDir() {
        return ApplicationLoader.applicationContext.getCacheDir();
    }

    public static String getDocumentExtension(TLRPC.Document document) {
        String fileName = getDocumentFileName(document);
        int idx = fileName.lastIndexOf(46);
        String ext = null;
        if (idx != -1) {
            ext = fileName.substring(idx + 1);
        }
        if (ext == null || ext.length() == 0) {
            ext = document.mime_type;
        }
        if (ext == null) {
            ext = "";
        }
        return ext.toUpperCase();
    }

    public static String getAttachFileName(TLObject attach) {
        return getAttachFileName(attach, (String) null);
    }

    public static String getAttachFileName(TLObject attach, String ext) {
        return getAttachFileName(attach, (String) null, ext);
    }

    public static String getAttachFileName(TLObject attach, String size, String ext) {
        String docExt;
        if (attach instanceof TLRPC.Document) {
            TLRPC.Document document = (TLRPC.Document) attach;
            String docExt2 = getDocumentFileName(document);
            int lastIndexOf = docExt2.lastIndexOf(46);
            int idx = lastIndexOf;
            if (lastIndexOf == -1) {
                docExt = "";
            } else {
                docExt = docExt2.substring(idx);
            }
            if (docExt.length() <= 1) {
                docExt = getExtensionByMimeType(document.mime_type);
            }
            if (docExt.length() > 1) {
                return document.dc_id + "_" + document.id + docExt;
            }
            return document.dc_id + "_" + document.id;
        } else if (attach instanceof SecureDocument) {
            SecureDocument secureDocument = (SecureDocument) attach;
            return secureDocument.secureFile.dc_id + "_" + secureDocument.secureFile.id + ".jpg";
        } else if (attach instanceof TLRPC.TL_secureFile) {
            TLRPC.TL_secureFile secureFile = (TLRPC.TL_secureFile) attach;
            return secureFile.dc_id + "_" + secureFile.id + ".jpg";
        } else if (attach instanceof WebFile) {
            WebFile document2 = (WebFile) attach;
            return Utilities.MD5(document2.url) + "." + ImageLoader.getHttpUrlExtension(document2.url, getMimeTypePart(document2.mime_type));
        } else {
            String str = "jpg";
            if (attach instanceof TLRPC.PhotoSize) {
                TLRPC.PhotoSize photo = (TLRPC.PhotoSize) attach;
                if (photo.location == null || (photo.location instanceof TLRPC.TL_fileLocationUnavailable)) {
                    return "";
                }
                StringBuilder sb = new StringBuilder();
                sb.append(photo.location.volume_id);
                sb.append("_");
                sb.append(photo.location.local_id);
                sb.append(".");
                if (ext != null) {
                    str = ext;
                }
                sb.append(str);
                return sb.toString();
            } else if (attach instanceof TLRPC.TL_videoSize) {
                TLRPC.TL_videoSize video = (TLRPC.TL_videoSize) attach;
                if (video.location == null || (video.location instanceof TLRPC.TL_fileLocationUnavailable)) {
                    return "";
                }
                StringBuilder sb2 = new StringBuilder();
                sb2.append(video.location.volume_id);
                sb2.append("_");
                sb2.append(video.location.local_id);
                sb2.append(".");
                sb2.append(ext != null ? ext : "mp4");
                return sb2.toString();
            } else if (attach instanceof TLRPC.FileLocation) {
                if (attach instanceof TLRPC.TL_fileLocationUnavailable) {
                    return "";
                }
                TLRPC.FileLocation location = (TLRPC.FileLocation) attach;
                StringBuilder sb3 = new StringBuilder();
                sb3.append(location.volume_id);
                sb3.append("_");
                sb3.append(location.local_id);
                sb3.append(".");
                if (ext != null) {
                    str = ext;
                }
                sb3.append(str);
                return sb3.toString();
            } else if (attach instanceof TLRPC.UserProfilePhoto) {
                if (size == null) {
                    size = "s";
                }
                TLRPC.UserProfilePhoto location2 = (TLRPC.UserProfilePhoto) attach;
                if (location2.photo_small == null) {
                    StringBuilder sb4 = new StringBuilder();
                    sb4.append(location2.photo_id);
                    sb4.append("_");
                    sb4.append(size);
                    sb4.append(".");
                    if (ext != null) {
                        str = ext;
                    }
                    sb4.append(str);
                    return sb4.toString();
                } else if ("s".equals(size)) {
                    return getAttachFileName(location2.photo_small, ext);
                } else {
                    return getAttachFileName(location2.photo_big, ext);
                }
            } else if (!(attach instanceof TLRPC.ChatPhoto)) {
                return "";
            } else {
                TLRPC.ChatPhoto location3 = (TLRPC.ChatPhoto) attach;
                if (location3.photo_small == null) {
                    StringBuilder sb5 = new StringBuilder();
                    sb5.append(location3.photo_id);
                    sb5.append("_");
                    sb5.append(size);
                    sb5.append(".");
                    if (ext != null) {
                        str = ext;
                    }
                    sb5.append(str);
                    return sb5.toString();
                } else if ("s".equals(size)) {
                    return getAttachFileName(location3.photo_small, ext);
                } else {
                    return getAttachFileName(location3.photo_big, ext);
                }
            }
        }
    }

    public void deleteFiles(ArrayList<File> files, int type) {
        if (files != null && !files.isEmpty()) {
            fileLoaderQueue.postRunnable(new FileLoader$$ExternalSyntheticLambda0(files, type));
        }
    }

    static /* synthetic */ void lambda$deleteFiles$12(ArrayList files, int type) {
        for (int a = 0; a < files.size(); a++) {
            File file = (File) files.get(a);
            File encrypted = new File(file.getAbsolutePath() + ".enc");
            if (encrypted.exists()) {
                try {
                    if (!encrypted.delete()) {
                        encrypted.deleteOnExit();
                    }
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
                try {
                    File internalCacheDir = getInternalCacheDir();
                    File key = new File(internalCacheDir, file.getName() + ".enc.key");
                    if (!key.delete()) {
                        key.deleteOnExit();
                    }
                } catch (Exception e2) {
                    FileLog.e((Throwable) e2);
                }
            } else if (file.exists()) {
                try {
                    if (!file.delete()) {
                        file.deleteOnExit();
                    }
                } catch (Exception e3) {
                    FileLog.e((Throwable) e3);
                }
            }
            try {
                File parentFile = file.getParentFile();
                File qFile = new File(parentFile, "q_" + file.getName());
                if (qFile.exists() && !qFile.delete()) {
                    qFile.deleteOnExit();
                }
            } catch (Exception e4) {
                FileLog.e((Throwable) e4);
            }
        }
        if (type == 2) {
            ImageLoader.getInstance().clearMemory();
        }
    }

    public static boolean isVideoMimeType(String mime) {
        return "video/mp4".equals(mime) || (SharedConfig.streamMkv && "video/x-matroska".equals(mime));
    }

    public static boolean copyFile(InputStream sourceFile, File destFile) throws IOException {
        return copyFile(sourceFile, destFile, -1);
    }

    public static boolean copyFile(InputStream sourceFile, File destFile, int maxSize) throws IOException {
        FileOutputStream out = new FileOutputStream(destFile);
        byte[] buf = new byte[4096];
        int totalLen = 0;
        while (true) {
            int read = sourceFile.read(buf);
            int len = read;
            if (read <= 0) {
                break;
            }
            Thread.yield();
            out.write(buf, 0, len);
            totalLen += len;
            if (maxSize > 0 && totalLen >= maxSize) {
                break;
            }
        }
        out.getFD().sync();
        out.close();
        return true;
    }

    public static boolean isSamePhoto(TLObject photo1, TLObject photo2) {
        if ((photo1 == null && photo2 != null) || (photo1 != null && photo2 == null)) {
            return false;
        }
        if (photo1 == null && photo2 == null) {
            return true;
        }
        if (photo1.getClass() != photo2.getClass()) {
            return false;
        }
        if (photo1 instanceof TLRPC.UserProfilePhoto) {
            if (((TLRPC.UserProfilePhoto) photo1).photo_id == ((TLRPC.UserProfilePhoto) photo2).photo_id) {
                return true;
            }
            return false;
        } else if (!(photo1 instanceof TLRPC.ChatPhoto) || ((TLRPC.ChatPhoto) photo1).photo_id != ((TLRPC.ChatPhoto) photo2).photo_id) {
            return false;
        } else {
            return true;
        }
    }

    public static boolean isSamePhoto(TLRPC.FileLocation location, TLRPC.Photo photo) {
        if (location == null || !(photo instanceof TLRPC.TL_photo)) {
            return false;
        }
        int N = photo.sizes.size();
        for (int b = 0; b < N; b++) {
            TLRPC.PhotoSize size = photo.sizes.get(b);
            if (size.location != null && size.location.local_id == location.local_id && size.location.volume_id == location.volume_id) {
                return true;
            }
        }
        if ((-location.volume_id) == photo.id) {
            return true;
        }
        return false;
    }

    public static long getPhotoId(TLObject object) {
        if (object instanceof TLRPC.Photo) {
            return ((TLRPC.Photo) object).id;
        }
        if (object instanceof TLRPC.ChatPhoto) {
            return ((TLRPC.ChatPhoto) object).photo_id;
        }
        if (object instanceof TLRPC.UserProfilePhoto) {
            return ((TLRPC.UserProfilePhoto) object).photo_id;
        }
        return 0;
    }

    public void getCurrentLoadingFiles(ArrayList<MessageObject> currentLoadingFiles) {
        currentLoadingFiles.clear();
        currentLoadingFiles.addAll(getDownloadController().downloadingFiles);
        for (int i = 0; i < currentLoadingFiles.size(); i++) {
            currentLoadingFiles.get(i).isDownloadingFile = true;
        }
    }

    public void getRecentLoadingFiles(ArrayList<MessageObject> recentLoadingFiles) {
        recentLoadingFiles.clear();
        recentLoadingFiles.addAll(getDownloadController().recentDownloadingFiles);
        for (int i = 0; i < recentLoadingFiles.size(); i++) {
            recentLoadingFiles.get(i).isDownloadingFile = true;
        }
    }

    public void checkCurrentDownloadsFiles() {
        ArrayList<MessageObject> messagesToRemove = new ArrayList<>();
        ArrayList<MessageObject> messageObjects = new ArrayList<>(getDownloadController().recentDownloadingFiles);
        for (int i = 0; i < messageObjects.size(); i++) {
            messageObjects.get(i).checkMediaExistance();
            if (messageObjects.get(i).mediaExists) {
                messagesToRemove.add(messageObjects.get(i));
            }
        }
        if (messagesToRemove.isEmpty() == 0) {
            AndroidUtilities.runOnUIThread(new FileLoader$$ExternalSyntheticLambda8(this, messagesToRemove));
        }
    }

    /* renamed from: lambda$checkCurrentDownloadsFiles$13$org-telegram-messenger-FileLoader  reason: not valid java name */
    public /* synthetic */ void m555x3e68e754(ArrayList messagesToRemove) {
        getDownloadController().recentDownloadingFiles.removeAll(messagesToRemove);
        getNotificationCenter().postNotificationName(NotificationCenter.onDownloadingFilesChanged, new Object[0]);
    }

    public void clearRecentDownloadedFiles() {
        getDownloadController().clearRecentDownloadedFiles();
    }
}
