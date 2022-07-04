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
import org.telegram.messenger.FileUploadOperation;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public class FileLoader extends BaseController {
    public static final long DEFAULT_MAX_FILE_SIZE = NUM;
    public static final long DEFAULT_MAX_FILE_SIZE_PREMIUM = 4194304000L;
    public static final int IMAGE_TYPE_ANIMATION = 2;
    public static final int IMAGE_TYPE_LOTTIE = 1;
    public static final int IMAGE_TYPE_SVG = 3;
    public static final int IMAGE_TYPE_SVG_WHITE = 4;
    public static final int IMAGE_TYPE_THEME_PREVIEW = 5;
    private static final FileLoader[] Instance = new FileLoader[4];
    public static final int MEDIA_DIR_AUDIO = 1;
    public static final int MEDIA_DIR_CACHE = 4;
    public static final int MEDIA_DIR_DOCUMENT = 3;
    public static final int MEDIA_DIR_FILES = 5;
    public static final int MEDIA_DIR_IMAGE = 0;
    public static final int MEDIA_DIR_IMAGE_PUBLIC = 100;
    public static final int MEDIA_DIR_VIDEO = 2;
    public static final int MEDIA_DIR_VIDEO_PUBLIC = 101;
    public static final int PRELOAD_CACHE_TYPE = 11;
    public static final int QUEUE_TYPE_AUDIO = 2;
    public static final int QUEUE_TYPE_FILE = 0;
    public static final int QUEUE_TYPE_IMAGE = 1;
    public static final int QUEUE_TYPE_PRELOAD = 3;
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
    private final FilePathDatabase filePathDatabase;
    private String forceLoadingFile;
    private SparseArray<LinkedList<FileLoadOperation>> imageLoadOperationQueues = new SparseArray<>();
    private SparseIntArray imageLoadOperationsCount = new SparseIntArray();
    private int lastReferenceId;
    private ConcurrentHashMap<String, FileLoadOperation> loadOperationPaths = new ConcurrentHashMap<>();
    /* access modifiers changed from: private */
    public ConcurrentHashMap<String, Boolean> loadOperationPathsUI = new ConcurrentHashMap<>(10, 1.0f, 2);
    private HashMap<String, Boolean> loadingVideos = new HashMap<>();
    private ConcurrentHashMap<Integer, Object> parentObjectReferences = new ConcurrentHashMap<>();
    private SparseArray<LinkedList<FileLoadOperation>> preloadingLoadOperationQueues = new SparseArray<>();
    private SparseIntArray preloadingLoadOperationsCount = new SparseIntArray();
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

    public interface FileResolver {
        File getFile();
    }

    static /* synthetic */ int access$708(FileLoader x0) {
        int i = x0.currentUploadSmallOperationsCount;
        x0.currentUploadSmallOperationsCount = i + 1;
        return i;
    }

    static /* synthetic */ int access$710(FileLoader x0) {
        int i = x0.currentUploadSmallOperationsCount;
        x0.currentUploadSmallOperationsCount = i - 1;
        return i;
    }

    static /* synthetic */ int access$908(FileLoader x0) {
        int i = x0.currentUploadOperationsCount;
        x0.currentUploadOperationsCount = i + 1;
        return i;
    }

    static /* synthetic */ int access$910(FileLoader x0) {
        int i = x0.currentUploadOperationsCount;
        x0.currentUploadOperationsCount = i - 1;
        return i;
    }

    public static FileLoader getInstance(int num) {
        FileLoader[] fileLoaderArr = Instance;
        FileLoader localInstance = fileLoaderArr[num];
        if (localInstance == null) {
            synchronized (FileLoader.class) {
                localInstance = fileLoaderArr[num];
                if (localInstance == null) {
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
        this.filePathDatabase = new FilePathDatabase(instance);
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
    public void m1827lambda$setLoadingVideo$0$orgtelegrammessengerFileLoader(TLRPC.Document document, boolean player) {
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
                m1827lambda$setLoadingVideo$0$orgtelegrammessengerFileLoader(document, player);
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
    public void m1825lambda$removeLoadingVideo$1$orgtelegrammessengerFileLoader(TLRPC.Document document, boolean player) {
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
                m1825lambda$removeLoadingVideo$1$orgtelegrammessengerFileLoader(document, player);
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
    public /* synthetic */ void m1816lambda$cancelFileUpload$2$orgtelegrammessengerFileLoader(boolean enc, String location) {
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
    public /* synthetic */ void m1821x5d598CLASSNAME(boolean encrypted, String location, long newAvailableSize, long finalSize) {
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
    public /* synthetic */ void m1824lambda$onNetworkChanged$4$orgtelegrammessengerFileLoader(boolean slow) {
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

    public void uploadFile(String location, boolean encrypted, boolean small, long estimatedSize, int type, boolean forceSmallFile) {
        if (location != null) {
            fileLoaderQueue.postRunnable(new FileLoader$$ExternalSyntheticLambda2(this, encrypted, location, estimatedSize, type, forceSmallFile, small));
        }
    }

    /* renamed from: lambda$uploadFile$5$org-telegram-messenger-FileLoader  reason: not valid java name */
    public /* synthetic */ void m1828lambda$uploadFile$5$orgtelegrammessengerFileLoader(boolean encrypted, String location, long estimatedSize, int type, boolean forceSmallFile, boolean small) {
        long esimated;
        FileUploadOperation operation;
        final boolean z = encrypted;
        final String str = location;
        final boolean z2 = small;
        if (z) {
            if (this.uploadOperationPathsEnc.containsKey(str)) {
                return;
            }
        } else if (this.uploadOperationPaths.containsKey(str)) {
            return;
        }
        long esimated2 = estimatedSize;
        if (esimated2 == 0 || this.uploadSizes.get(str) == null) {
            esimated = esimated2;
        } else {
            this.uploadSizes.remove(str);
            esimated = 0;
        }
        FileUploadOperation fileUploadOperation = new FileUploadOperation(this.currentAccount, location, encrypted, esimated, type);
        FileLoaderDelegate fileLoaderDelegate = this.delegate;
        if (fileLoaderDelegate == null || estimatedSize == 0) {
            operation = fileUploadOperation;
        } else {
            operation = fileUploadOperation;
            fileLoaderDelegate.fileUploadProgressChanged(fileUploadOperation, location, 0, estimatedSize, encrypted);
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
            public /* synthetic */ void m1830xdfee369(boolean encrypted, String location, boolean small, TLRPC.InputFile inputFile, TLRPC.InputEncryptedFile inputEncryptedFile, byte[] key, byte[] iv, FileUploadOperation operation) {
                FileUploadOperation operation12;
                FileUploadOperation operation122;
                String str = location;
                if (encrypted) {
                    FileLoader.this.uploadOperationPathsEnc.remove(location);
                } else {
                    FileLoader.this.uploadOperationPaths.remove(location);
                }
                if (small) {
                    FileLoader.access$710(FileLoader.this);
                    if (FileLoader.this.currentUploadSmallOperationsCount < 1 && (operation122 = (FileUploadOperation) FileLoader.this.uploadSmallOperationQueue.poll()) != null) {
                        FileLoader.access$708(FileLoader.this);
                        operation122.start();
                    }
                } else {
                    FileLoader.access$910(FileLoader.this);
                    if (FileLoader.this.currentUploadOperationsCount < 1 && (operation12 = (FileUploadOperation) FileLoader.this.uploadOperationQueue.poll()) != null) {
                        FileLoader.access$908(FileLoader.this);
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
            public /* synthetic */ void m1829x2708d300(boolean encrypted, String location, boolean small) {
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
                    FileLoader.access$710(FileLoader.this);
                    if (FileLoader.this.currentUploadSmallOperationsCount < 1 && (operation12 = (FileUploadOperation) FileLoader.this.uploadSmallOperationQueue.poll()) != null) {
                        FileLoader.access$708(FileLoader.this);
                        operation12.start();
                        return;
                    }
                    return;
                }
                FileLoader.access$910(FileLoader.this);
                if (FileLoader.this.currentUploadOperationsCount < 1 && (operation1 = (FileUploadOperation) FileLoader.this.uploadOperationQueue.poll()) != null) {
                    FileLoader.access$908(FileLoader.this);
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
            int i = this.currentUploadSmallOperationsCount;
            if (i < 1) {
                this.currentUploadSmallOperationsCount = i + 1;
                operation.start();
                return;
            }
            this.uploadSmallOperationQueue.add(operation);
            return;
        }
        int i2 = this.currentUploadOperationsCount;
        if (i2 < 1) {
            this.currentUploadOperationsCount = i2 + 1;
            operation.start();
            return;
        }
        this.uploadOperationQueue.add(operation);
    }

    private LinkedList<FileLoadOperation> getLoadOperationQueue(int datacenterId, int type) {
        SparseArray<LinkedList<FileLoadOperation>> queues;
        if (type == 3) {
            queues = this.preloadingLoadOperationQueues;
        } else if (type == 2) {
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
        if (type == 3) {
            return this.preloadingLoadOperationsCount;
        }
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
    public /* synthetic */ void m1826xbvar_c(TLRPC.FileLocation location, String ext) {
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
    public /* synthetic */ void m1817lambda$cancelLoadFile$7$orgtelegrammessengerFileLoader(String fileName, boolean deleteFile) {
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
    public /* synthetic */ void m1818lambda$cancelLoadFile$8$orgtelegrammessengerFileLoader() {
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
        int cacheType2;
        TLRPC.Document document2 = document;
        if (document2 != null) {
            if (cacheType != 0 || document2.key == null) {
                cacheType2 = cacheType;
            } else {
                cacheType2 = 1;
            }
            loadFile(document, (SecureDocument) null, (WebFile) null, (TLRPC.TL_fileLocationToBeDeprecated) null, (ImageLocation) null, parentObject, (String) null, 0, priority, cacheType2);
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

    /* JADX WARNING: Code restructure failed: missing block: B:107:0x026a, code lost:
        if (r6.imageType == 2) goto L_0x026f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:135:0x02d5, code lost:
        if (r5 == 2) goto L_0x02da;
     */
    /* JADX WARNING: Removed duplicated region for block: B:154:0x031d  */
    /* JADX WARNING: Removed duplicated region for block: B:224:0x044a  */
    /* JADX WARNING: Removed duplicated region for block: B:226:0x0482  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private org.telegram.messenger.FileLoadOperation loadFileInternal(org.telegram.tgnet.TLRPC.Document r38, org.telegram.messenger.SecureDocument r39, org.telegram.messenger.WebFile r40, org.telegram.tgnet.TLRPC.TL_fileLocationToBeDeprecated r41, org.telegram.messenger.ImageLocation r42, java.lang.Object r43, java.lang.String r44, long r45, int r47, org.telegram.messenger.FileLoadOperationStream r48, int r49, boolean r50, int r51) {
        /*
            r37 = this;
            r7 = r37
            r8 = r38
            r9 = r39
            r10 = r40
            r11 = r41
            r12 = r42
            r13 = r43
            r14 = r47
            r15 = r48
            r6 = r49
            r4 = r50
            r5 = r51
            if (r11 == 0) goto L_0x0022
            r3 = r44
            java.lang.String r0 = getAttachFileName(r11, r3)
            r2 = r0
            goto L_0x003e
        L_0x0022:
            r3 = r44
            if (r9 == 0) goto L_0x002c
            java.lang.String r0 = getAttachFileName(r39)
            r2 = r0
            goto L_0x003e
        L_0x002c:
            if (r8 == 0) goto L_0x0034
            java.lang.String r0 = getAttachFileName(r38)
            r2 = r0
            goto L_0x003e
        L_0x0034:
            if (r10 == 0) goto L_0x003c
            java.lang.String r0 = getAttachFileName(r40)
            r2 = r0
            goto L_0x003e
        L_0x003c:
            r0 = 0
            r2 = r0
        L_0x003e:
            if (r2 == 0) goto L_0x048a
            java.lang.String r0 = "-NUM"
            boolean r1 = r2.contains(r0)
            if (r1 == 0) goto L_0x004f
            r3 = r2
            r13 = r5
            r10 = r6
            r2 = r14
            r14 = r4
            goto L_0x048f
        L_0x004f:
            r1 = 10
            r12 = 1
            if (r5 == r1) goto L_0x0069
            boolean r16 = android.text.TextUtils.isEmpty(r2)
            if (r16 != 0) goto L_0x0069
            boolean r0 = r2.contains(r0)
            if (r0 != 0) goto L_0x0069
            j$.util.concurrent.ConcurrentHashMap<java.lang.String, java.lang.Boolean> r0 = r7.loadOperationPathsUI
            java.lang.Boolean r1 = java.lang.Boolean.valueOf(r12)
            r0.put(r2, r1)
        L_0x0069:
            if (r8 == 0) goto L_0x0089
            boolean r0 = r13 instanceof org.telegram.messenger.MessageObject
            if (r0 == 0) goto L_0x0089
            r0 = r13
            org.telegram.messenger.MessageObject r0 = (org.telegram.messenger.MessageObject) r0
            boolean r0 = r0.putInDownloadsStore
            if (r0 == 0) goto L_0x0089
            r0 = r13
            org.telegram.messenger.MessageObject r0 = (org.telegram.messenger.MessageObject) r0
            boolean r0 = r0.isAnyKindOfSticker()
            if (r0 != 0) goto L_0x0089
            org.telegram.messenger.DownloadController r0 = r37.getDownloadController()
            r1 = r13
            org.telegram.messenger.MessageObject r1 = (org.telegram.messenger.MessageObject) r1
            r0.startDownloadFile(r8, r1)
        L_0x0089:
            j$.util.concurrent.ConcurrentHashMap<java.lang.String, org.telegram.messenger.FileLoadOperation> r0 = r7.loadOperationPaths
            java.lang.Object r0 = r0.get(r2)
            r1 = r0
            org.telegram.messenger.FileLoadOperation r1 = (org.telegram.messenger.FileLoadOperation) r1
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            java.lang.String r12 = " documentName="
            if (r0 == 0) goto L_0x00be
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r3 = "checkFile operation fileName="
            r0.append(r3)
            r0.append(r2)
            r0.append(r12)
            java.lang.String r3 = getDocumentFileName(r38)
            r0.append(r3)
            java.lang.String r3 = " operation="
            r0.append(r3)
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.d(r0)
        L_0x00be:
            r3 = 0
            if (r1 == 0) goto L_0x0141
            r0 = 10
            if (r5 == r0) goto L_0x00ce
            boolean r0 = r1.isPreloadVideoOperation()
            if (r0 == 0) goto L_0x00ce
            r1.setIsPreloadVideoOperation(r3)
        L_0x00ce:
            if (r15 != 0) goto L_0x00d6
            if (r14 <= 0) goto L_0x00d3
            goto L_0x00d6
        L_0x00d3:
            r18 = r2
            goto L_0x013d
        L_0x00d6:
            int r0 = r1.getDatacenterId()
            r12 = 1
            r1.setForceRequest(r12)
            int r12 = r1.getQueueType()
            java.util.LinkedList r3 = r7.getLoadOperationQueue(r0, r12)
            r18 = r2
            android.util.SparseIntArray r2 = r7.getLoadOperationCount(r12)
            int r5 = r3.indexOf(r1)
            if (r5 < 0) goto L_0x0125
            r3.remove(r5)
            if (r15 == 0) goto L_0x0120
            long r13 = (long) r6
            boolean r13 = r1.start(r15, r13, r4)
            if (r13 == 0) goto L_0x0107
            int r13 = r2.get(r0)
            r14 = 1
            int r13 = r13 + r14
            r2.put(r0, r13)
        L_0x0107:
            if (r12 != 0) goto L_0x013d
            boolean r13 = r1.wasStarted()
            if (r13 == 0) goto L_0x013d
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation> r13 = r7.activeFileLoadOperation
            boolean r13 = r13.contains(r1)
            if (r13 != 0) goto L_0x013d
            r7.pauseCurrentFileLoadOperations(r1)
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation> r13 = r7.activeFileLoadOperation
            r13.add(r1)
            goto L_0x013d
        L_0x0120:
            r13 = 0
            r3.add(r13, r1)
            goto L_0x013d
        L_0x0125:
            if (r15 == 0) goto L_0x012a
            r7.pauseCurrentFileLoadOperations(r1)
        L_0x012a:
            long r13 = (long) r6
            r1.start(r15, r13, r4)
            if (r12 != 0) goto L_0x013d
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation> r13 = r7.activeFileLoadOperation
            boolean r13 = r13.contains(r1)
            if (r13 != 0) goto L_0x013d
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation> r13 = r7.activeFileLoadOperation
            r13.add(r1)
        L_0x013d:
            r1.updateProgress()
            return r1
        L_0x0141:
            r18 = r2
            r0 = 10
            r13 = 0
            r14 = 4
            java.io.File r23 = getDirectory(r14)
            r16 = r23
            r19 = 4
            r2 = 0
            r5 = 0
            if (r9 == 0) goto L_0x016f
            org.telegram.messenger.FileLoadOperation r0 = new org.telegram.messenger.FileLoadOperation
            r0.<init>(r9)
            r1 = r0
            r19 = 3
            r13 = r51
            r14 = r4
            r0 = r5
            r26 = r18
            r5 = r19
            r27 = 0
            r4 = r43
            r35 = r2
            r3 = r1
            r1 = r35
            goto L_0x024c
        L_0x016f:
            if (r11 == 0) goto L_0x01a6
            long r2 = r11.volume_id
            int r5 = r11.dc_id
            org.telegram.messenger.FileLoadOperation r21 = new org.telegram.messenger.FileLoadOperation
            r20 = 10
            r0 = r21
            r20 = r1
            r13 = 10
            r1 = r42
            r24 = r2
            r3 = r18
            r2 = r43
            r26 = r3
            r27 = 0
            r3 = r44
            r13 = r51
            r14 = r4
            r18 = r5
            r4 = r45
            r0.<init>(r1, r2, r3, r4)
            r1 = r21
            r19 = 0
            r4 = r43
            r3 = r1
            r0 = r18
            r5 = r19
            r1 = r24
            goto L_0x024c
        L_0x01a6:
            r13 = r51
            r20 = r1
            r14 = r4
            r26 = r18
            r27 = 0
            if (r8 == 0) goto L_0x01ed
            org.telegram.messenger.FileLoadOperation r0 = new org.telegram.messenger.FileLoadOperation
            r4 = r43
            r0.<init>((org.telegram.tgnet.TLRPC.Document) r8, (java.lang.Object) r4)
            r1 = r0
            long r2 = r8.id
            int r5 = r8.dc_id
            boolean r0 = org.telegram.messenger.MessageObject.isVoiceDocument(r38)
            if (r0 == 0) goto L_0x01cf
            r19 = 1
            r0 = r5
            r5 = r19
            r35 = r2
            r3 = r1
            r1 = r35
            goto L_0x024c
        L_0x01cf:
            boolean r0 = org.telegram.messenger.MessageObject.isVideoDocument(r38)
            if (r0 == 0) goto L_0x01e1
            r19 = 2
            r0 = r5
            r5 = r19
            r35 = r2
            r3 = r1
            r1 = r35
            goto L_0x024c
        L_0x01e1:
            r19 = 3
            r0 = r5
            r5 = r19
            r35 = r2
            r3 = r1
            r1 = r35
            goto L_0x024c
        L_0x01ed:
            r4 = r43
            if (r10 == 0) goto L_0x0246
            org.telegram.messenger.FileLoadOperation r0 = new org.telegram.messenger.FileLoadOperation
            int r1 = r7.currentAccount
            r0.<init>((int) r1, (org.telegram.messenger.WebFile) r10)
            r1 = r0
            org.telegram.tgnet.TLRPC$InputWebFileLocation r0 = r10.location
            if (r0 == 0) goto L_0x0208
            r19 = 4
            r0 = r5
            r5 = r19
            r35 = r2
            r3 = r1
            r1 = r35
            goto L_0x024c
        L_0x0208:
            boolean r0 = org.telegram.messenger.MessageObject.isVoiceWebDocument(r40)
            if (r0 == 0) goto L_0x0219
            r19 = 1
            r0 = r5
            r5 = r19
            r35 = r2
            r3 = r1
            r1 = r35
            goto L_0x024c
        L_0x0219:
            boolean r0 = org.telegram.messenger.MessageObject.isVideoWebDocument(r40)
            if (r0 == 0) goto L_0x022a
            r19 = 2
            r0 = r5
            r5 = r19
            r35 = r2
            r3 = r1
            r1 = r35
            goto L_0x024c
        L_0x022a:
            boolean r0 = org.telegram.messenger.MessageObject.isImageWebDocument(r40)
            if (r0 == 0) goto L_0x023b
            r19 = 0
            r0 = r5
            r5 = r19
            r35 = r2
            r3 = r1
            r1 = r35
            goto L_0x024c
        L_0x023b:
            r19 = 3
            r0 = r5
            r5 = r19
            r35 = r2
            r3 = r1
            r1 = r35
            goto L_0x024c
        L_0x0246:
            r1 = r2
            r0 = r5
            r5 = r19
            r3 = r20
        L_0x024c:
            r6 = 11
            r8 = 2
            if (r13 != r6) goto L_0x0256
            r6 = 3
            r9 = r6
            r6 = r42
            goto L_0x0281
        L_0x0256:
            r6 = 1
            if (r5 != r6) goto L_0x0260
            r17 = 2
            r6 = r42
            r9 = r17
            goto L_0x0281
        L_0x0260:
            if (r9 != 0) goto L_0x027e
            if (r11 == 0) goto L_0x026d
            r6 = r42
            if (r6 == 0) goto L_0x0280
            int r9 = r6.imageType
            if (r9 != r8) goto L_0x0280
            goto L_0x026f
        L_0x026d:
            r6 = r42
        L_0x026f:
            boolean r9 = org.telegram.messenger.MessageObject.isImageWebDocument(r40)
            if (r9 != 0) goto L_0x0280
            boolean r9 = org.telegram.messenger.MessageObject.isStickerDocument(r38)
            if (r9 == 0) goto L_0x027c
            goto L_0x0280
        L_0x027c:
            r9 = 0
            goto L_0x0281
        L_0x027e:
            r6 = r42
        L_0x0280:
            r9 = 1
        L_0x0281:
            r17 = r26
            if (r13 == 0) goto L_0x0297
            r8 = 10
            if (r13 != r8) goto L_0x028a
            goto L_0x0297
        L_0x028a:
            r8 = 2
            if (r13 != r8) goto L_0x0291
            r8 = 1
            r3.setEncryptFile(r8)
        L_0x0291:
            r8 = r16
            r29 = r17
            goto L_0x0333
        L_0x0297:
            r18 = 0
            int r8 = (r1 > r18 ? 1 : (r1 == r18 ? 0 : -1))
            if (r8 == 0) goto L_0x032b
            org.telegram.messenger.FilePathDatabase r29 = r37.getFileDatabase()
            r34 = 1
            r30 = r1
            r32 = r0
            r33 = r5
            java.lang.String r8 = r29.getPath(r30, r32, r33, r34)
            r18 = 0
            if (r8 == 0) goto L_0x02c6
            java.io.File r6 = new java.io.File
            r6.<init>(r8)
            boolean r19 = r6.exists()
            if (r19 == 0) goto L_0x02c6
            r18 = 1
            java.lang.String r17 = r6.getName()
            java.io.File r16 = r6.getParentFile()
        L_0x02c6:
            if (r18 != 0) goto L_0x0326
            r6 = r26
            java.io.File r16 = getDirectory(r5)
            r17 = 0
            if (r5 == 0) goto L_0x02d8
            r19 = r6
            r6 = 2
            if (r5 != r6) goto L_0x02f8
            goto L_0x02da
        L_0x02d8:
            r19 = r6
        L_0x02da:
            boolean r6 = r7.canSaveToPublicStorage(r4)
            if (r6 == 0) goto L_0x02f8
            if (r5 != 0) goto L_0x02e9
            r6 = 100
            java.io.File r6 = getDirectory(r6)
            goto L_0x02ef
        L_0x02e9:
            r6 = 101(0x65, float:1.42E-43)
            java.io.File r6 = getDirectory(r6)
        L_0x02ef:
            if (r6 == 0) goto L_0x02f5
            r16 = r6
            r17 = 1
        L_0x02f5:
            r6 = r19
            goto L_0x031b
        L_0x02f8:
            java.lang.String r6 = getDocumentFileName(r38)
            boolean r6 = android.text.TextUtils.isEmpty(r6)
            if (r6 != 0) goto L_0x0319
            boolean r6 = r7.canSaveAsFile(r4)
            if (r6 == 0) goto L_0x0319
            java.lang.String r6 = getDocumentFileName(r38)
            r19 = 5
            java.io.File r19 = getDirectory(r19)
            if (r19 == 0) goto L_0x031b
            r16 = r19
            r17 = 1
            goto L_0x031b
        L_0x0319:
            r6 = r19
        L_0x031b:
            if (r17 == 0) goto L_0x0324
            org.telegram.messenger.FilePathDatabase$PathData r4 = new org.telegram.messenger.FilePathDatabase$PathData
            r4.<init>(r1, r0, r5)
            r3.pathSaveData = r4
        L_0x0324:
            r17 = r6
        L_0x0326:
            r8 = r16
            r29 = r17
            goto L_0x0333
        L_0x032b:
            java.io.File r16 = getDirectory(r5)
            r8 = r16
            r29 = r17
        L_0x0333:
            int r4 = r7.currentAccount
            r16 = r3
            r17 = r4
            r18 = r26
            r19 = r9
            r20 = r8
            r21 = r23
            r22 = r29
            r16.setPaths(r17, r18, r19, r20, r21, r22)
            r4 = 10
            if (r13 != r4) goto L_0x034f
            r6 = 1
            r3.setIsPreloadVideoOperation(r6)
            goto L_0x0350
        L_0x034f:
            r6 = 1
        L_0x0350:
            r19 = r5
            org.telegram.messenger.FileLoader$2 r16 = new org.telegram.messenger.FileLoader$2
            r18 = r0
            r0 = r16
            r20 = r1
            r1 = r37
            r2 = r38
            r4 = r3
            r3 = r43
            r17 = r8
            r8 = r4
            r4 = r26
            r10 = r49
            r11 = 1
            r6 = r9
            r0.<init>(r2, r3, r4, r5, r6)
            r8.setDelegate(r0)
            int r1 = r8.getDatacenterId()
            j$.util.concurrent.ConcurrentHashMap<java.lang.String, org.telegram.messenger.FileLoadOperation> r2 = r7.loadOperationPaths
            r3 = r26
            r2.put(r3, r8)
            r2 = r47
            r8.setPriority(r2)
            r4 = 6
            r6 = 3
            if (r9 != r6) goto L_0x03b3
            if (r2 <= 0) goto L_0x0387
            goto L_0x0388
        L_0x0387:
            r4 = 2
        L_0x0388:
            android.util.SparseIntArray r6 = r7.preloadingLoadOperationsCount
            int r6 = r6.get(r1)
            if (r15 != 0) goto L_0x0394
            if (r6 >= r4) goto L_0x0393
            goto L_0x0394
        L_0x0393:
            r11 = 0
        L_0x0394:
            r16 = r11
            if (r11 == 0) goto L_0x03ab
            r24 = r4
            r22 = r5
            long r4 = (long) r10
            boolean r4 = r8.start(r15, r4, r14)
            if (r4 == 0) goto L_0x03af
            android.util.SparseIntArray r4 = r7.preloadingLoadOperationsCount
            int r5 = r6 + 1
            r4.put(r1, r5)
            goto L_0x03af
        L_0x03ab:
            r24 = r4
            r22 = r5
        L_0x03af:
            r4 = r24
            goto L_0x0446
        L_0x03b3:
            r22 = r5
            r5 = 2
            if (r9 != r5) goto L_0x03e1
            if (r2 <= 0) goto L_0x03bb
            goto L_0x03bc
        L_0x03bb:
            r6 = 1
        L_0x03bc:
            r4 = r6
            android.util.SparseIntArray r5 = r7.audioLoadOperationsCount
            int r6 = r5.get(r1)
            if (r15 != 0) goto L_0x03c9
            if (r6 >= r4) goto L_0x03c8
            goto L_0x03c9
        L_0x03c8:
            r11 = 0
        L_0x03c9:
            r16 = r11
            if (r11 == 0) goto L_0x03de
            r24 = r4
            long r4 = (long) r10
            boolean r4 = r8.start(r15, r4, r14)
            if (r4 == 0) goto L_0x03af
            android.util.SparseIntArray r4 = r7.audioLoadOperationsCount
            int r5 = r6 + 1
            r4.put(r1, r5)
            goto L_0x03af
        L_0x03de:
            r24 = r4
            goto L_0x03af
        L_0x03e1:
            if (r9 != r11) goto L_0x040b
            if (r2 <= 0) goto L_0x03e6
            goto L_0x03e7
        L_0x03e6:
            r4 = 2
        L_0x03e7:
            android.util.SparseIntArray r5 = r7.imageLoadOperationsCount
            int r6 = r5.get(r1)
            if (r15 != 0) goto L_0x03f3
            if (r6 >= r4) goto L_0x03f2
            goto L_0x03f3
        L_0x03f2:
            r11 = 0
        L_0x03f3:
            r16 = r11
            if (r11 == 0) goto L_0x0408
            r24 = r4
            long r4 = (long) r10
            boolean r4 = r8.start(r15, r4, r14)
            if (r4 == 0) goto L_0x03af
            android.util.SparseIntArray r4 = r7.imageLoadOperationsCount
            int r5 = r6 + 1
            r4.put(r1, r5)
            goto L_0x03af
        L_0x0408:
            r24 = r4
            goto L_0x03af
        L_0x040b:
            if (r2 <= 0) goto L_0x0410
            r28 = 4
            goto L_0x0412
        L_0x0410:
            r28 = 1
        L_0x0412:
            r4 = r28
            android.util.SparseIntArray r5 = r7.fileLoadOperationsCount
            int r6 = r5.get(r1)
            if (r15 != 0) goto L_0x0420
            if (r6 >= r4) goto L_0x041f
            goto L_0x0420
        L_0x041f:
            r11 = 0
        L_0x0420:
            r16 = r11
            if (r11 == 0) goto L_0x0444
            r11 = r4
            long r4 = (long) r10
            boolean r4 = r8.start(r15, r4, r14)
            if (r4 == 0) goto L_0x0438
            android.util.SparseIntArray r4 = r7.fileLoadOperationsCount
            int r5 = r6 + 1
            r4.put(r1, r5)
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation> r4 = r7.activeFileLoadOperation
            r4.add(r8)
        L_0x0438:
            boolean r4 = r8.wasStarted()
            if (r4 == 0) goto L_0x0445
            if (r15 == 0) goto L_0x0445
            r7.pauseCurrentFileLoadOperations(r8)
            goto L_0x0445
        L_0x0444:
            r11 = r4
        L_0x0445:
            r4 = r11
        L_0x0446:
            boolean r5 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r5 == 0) goto L_0x0480
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r11 = "loadFileInternal fileName="
            r5.append(r11)
            r5.append(r3)
            r5.append(r12)
            java.lang.String r11 = getDocumentFileName(r38)
            r5.append(r11)
            java.lang.String r11 = " queueType="
            r5.append(r11)
            r5.append(r9)
            java.lang.String r11 = " maxCount="
            r5.append(r11)
            r5.append(r4)
            java.lang.String r11 = " count="
            r5.append(r11)
            r5.append(r6)
            java.lang.String r5 = r5.toString()
            org.telegram.messenger.FileLog.d(r5)
        L_0x0480:
            if (r16 != 0) goto L_0x0489
            java.util.LinkedList r5 = r7.getLoadOperationQueue(r1, r9)
            r7.addOperationToQueue(r8, r5)
        L_0x0489:
            return r8
        L_0x048a:
            r3 = r2
            r13 = r5
            r10 = r6
            r2 = r14
            r14 = r4
        L_0x048f:
            r0 = 0
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.FileLoader.loadFileInternal(org.telegram.tgnet.TLRPC$Document, org.telegram.messenger.SecureDocument, org.telegram.messenger.WebFile, org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated, org.telegram.messenger.ImageLocation, java.lang.Object, java.lang.String, long, int, org.telegram.messenger.FileLoadOperationStream, int, boolean, int):org.telegram.messenger.FileLoadOperation");
    }

    private boolean canSaveAsFile(Object parentObject) {
        if (!(parentObject instanceof MessageObject) || !((MessageObject) parentObject).isDocument()) {
            return false;
        }
        return true;
    }

    private boolean canSaveToPublicStorage(Object parentObject) {
        int flag;
        if (SharedConfig.saveToGalleryFlags != 0 && !BuildVars.NO_SCOPED_STORAGE && (parentObject instanceof MessageObject)) {
            MessageObject messageObject = (MessageObject) parentObject;
            long dialogId = messageObject.getDialogId();
            if (messageObject.isAnyKindOfSticker() || getMessagesController().isChatNoForwards(getMessagesController().getChat(Long.valueOf(-dialogId))) || messageObject.messageOwner.noforwards) {
                return false;
            }
            if (dialogId >= 0) {
                flag = 1;
            } else if (ChatObject.isChannelAndNotMegaGroup(getMessagesController().getChat(Long.valueOf(-dialogId)))) {
                flag = 4;
            } else {
                flag = 2;
            }
            if ((SharedConfig.saveToGalleryFlags & flag) != 0) {
                return true;
            }
        }
        return false;
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

    private void loadFile(TLRPC.Document document, SecureDocument secureDocument, WebFile webDocument, TLRPC.TL_fileLocationToBeDeprecated location, ImageLocation imageLocation, Object parentObject, String locationExt, long locationSize, int priority, int cacheType) {
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
        DispatchQueue dispatchQueue = fileLoaderQueue;
        FileLoader$$ExternalSyntheticLambda9 fileLoader$$ExternalSyntheticLambda9 = r0;
        FileLoader$$ExternalSyntheticLambda9 fileLoader$$ExternalSyntheticLambda92 = new FileLoader$$ExternalSyntheticLambda9(this, document, secureDocument, webDocument, location, imageLocation, parentObject, locationExt, locationSize, priority, cacheType);
        dispatchQueue.postRunnable(fileLoader$$ExternalSyntheticLambda9);
    }

    /* renamed from: lambda$loadFile$9$org-telegram-messenger-FileLoader  reason: not valid java name */
    public /* synthetic */ void m1822lambda$loadFile$9$orgtelegrammessengerFileLoader(TLRPC.Document document, SecureDocument secureDocument, WebFile webDocument, TLRPC.TL_fileLocationToBeDeprecated location, ImageLocation imageLocation, Object parentObject, String locationExt, long locationSize, int priority, int cacheType) {
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
            FileLog.e((Throwable) e, false);
        }
        return result[0];
    }

    /* renamed from: lambda$loadStreamFile$10$org-telegram-messenger-FileLoader  reason: not valid java name */
    public /* synthetic */ void m1823lambda$loadStreamFile$10$orgtelegrammessengerFileLoader(FileLoadOperation[] result, TLRPC.Document document, ImageLocation location, Object parentObject, FileLoadOperationStream stream, int offset, boolean priority, CountDownLatch semaphore) {
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
    public /* synthetic */ void m1820lambda$checkDownloadQueue$11$orgtelegrammessengerFileLoader(String fileName, int datacenterId, int queueType) {
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
            int maxCount = 6;
            int i = 3;
            if (queueType == 3) {
                if (operation2.getPriority() == 0) {
                    maxCount = 2;
                }
            } else if (queueType == 2) {
                if (operation2.getPriority() == 0) {
                    i = 1;
                }
                maxCount = i;
            } else if (queueType != 1) {
                if (operation2.isForceRequest() == 0) {
                    i = 1;
                }
                maxCount = i;
            } else if (operation2.getPriority() == 0) {
                maxCount = 2;
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

    public File getPathToMessage(TLRPC.Message message) {
        return getPathToMessage(message, true);
    }

    public File getPathToMessage(TLRPC.Message message, boolean useFileDatabaseQueue) {
        TLRPC.PhotoSize sizeFull;
        TLRPC.PhotoSize sizeFull2;
        TLRPC.PhotoSize sizeFull3;
        if (message == null) {
            return new File("");
        }
        boolean z = false;
        if (message instanceof TLRPC.TL_messageService) {
            if (message.action.photo != null) {
                ArrayList<TLRPC.PhotoSize> sizes = message.action.photo.sizes;
                if (sizes.size() > 0 && (sizeFull3 = getClosestPhotoSizeWithSize(sizes, AndroidUtilities.getPhotoSize())) != null) {
                    return getPathToAttach(sizeFull3, (String) null, false, useFileDatabaseQueue);
                }
            }
        } else if (message.media instanceof TLRPC.TL_messageMediaDocument) {
            TLRPC.Document document = message.media.document;
            if (message.media.ttl_seconds != 0) {
                z = true;
            }
            return getPathToAttach(document, (String) null, z, useFileDatabaseQueue);
        } else if (message.media instanceof TLRPC.TL_messageMediaPhoto) {
            ArrayList<TLRPC.PhotoSize> sizes2 = message.media.photo.sizes;
            if (sizes2.size() > 0 && (sizeFull2 = getClosestPhotoSizeWithSize(sizes2, AndroidUtilities.getPhotoSize(), false, (TLRPC.PhotoSize) null, true)) != null) {
                if (message.media.ttl_seconds != 0) {
                    z = true;
                }
                return getPathToAttach(sizeFull2, (String) null, z, useFileDatabaseQueue);
            }
        } else if (message.media instanceof TLRPC.TL_messageMediaWebPage) {
            if (message.media.webpage.document != null) {
                return getPathToAttach(message.media.webpage.document, (String) null, false, useFileDatabaseQueue);
            }
            if (message.media.webpage.photo != null) {
                ArrayList<TLRPC.PhotoSize> sizes3 = message.media.webpage.photo.sizes;
                if (sizes3.size() > 0 && (sizeFull = getClosestPhotoSizeWithSize(sizes3, AndroidUtilities.getPhotoSize())) != null) {
                    return getPathToAttach(sizeFull, (String) null, false, useFileDatabaseQueue);
                }
            }
        } else if (message.media instanceof TLRPC.TL_messageMediaInvoice) {
            return getPathToAttach(((TLRPC.TL_messageMediaInvoice) message.media).photo, (String) null, true, useFileDatabaseQueue);
        }
        return new File("");
    }

    public File getPathToAttach(TLObject attach) {
        return getPathToAttach(attach, (String) null, false);
    }

    public File getPathToAttach(TLObject attach, boolean forceCache) {
        return getPathToAttach(attach, (String) null, forceCache);
    }

    public File getPathToAttach(TLObject attach, String ext, boolean forceCache) {
        return getPathToAttach(attach, (String) null, ext, forceCache, true);
    }

    public File getPathToAttach(TLObject attach, String ext, boolean forceCache, boolean useFileDatabaseQueue) {
        return getPathToAttach(attach, (String) null, ext, forceCache, useFileDatabaseQueue);
    }

    public File getPathToAttach(TLObject attach, String size, String ext, boolean forceCache, boolean useFileDatabaseQueue) {
        int type;
        int type2;
        long documentId;
        File dir;
        String path;
        String size2;
        File dir2;
        int type3;
        File dir3;
        File dir4;
        int type4;
        TLObject tLObject = attach;
        String str = ext;
        File dir5 = null;
        long documentId2 = 0;
        int dcId = 0;
        int type5 = 0;
        if (forceCache) {
            boolean z = useFileDatabaseQueue;
            type = 0;
            type2 = 0;
            documentId = 0;
            dir = getDirectory(4);
            String str2 = size;
        } else if (tLObject instanceof TLRPC.Document) {
            TLRPC.Document document = (TLRPC.Document) tLObject;
            if (!TextUtils.isEmpty(document.localPath)) {
                return new File(document.localPath);
            }
            if (document.key != null) {
                type4 = 4;
            } else if (MessageObject.isVoiceDocument(document)) {
                type4 = 1;
            } else if (MessageObject.isVideoDocument(document)) {
                type4 = 2;
            } else {
                type4 = 3;
            }
            long documentId3 = document.id;
            int dcId2 = document.dc_id;
            File dir6 = getDirectory(type4);
            boolean z2 = useFileDatabaseQueue;
            type = type4;
            type2 = dcId2;
            documentId = documentId3;
            dir = dir6;
            String str3 = size;
        } else if (tLObject instanceof TLRPC.Photo) {
            return getPathToAttach(getClosestPhotoSizeWithSize(((TLRPC.Photo) tLObject).sizes, AndroidUtilities.getPhotoSize()), str, false, useFileDatabaseQueue);
        } else {
            boolean z3 = useFileDatabaseQueue;
            if (tLObject instanceof TLRPC.PhotoSize) {
                TLRPC.PhotoSize photoSize = (TLRPC.PhotoSize) tLObject;
                if ((photoSize instanceof TLRPC.TL_photoStrippedSize) || (photoSize instanceof TLRPC.TL_photoPathSize)) {
                    dir4 = null;
                } else if (photoSize.location == null || photoSize.location.key != null || ((photoSize.location.volume_id == -2147483648L && photoSize.location.local_id < 0) || photoSize.size < 0)) {
                    type5 = 4;
                    dir4 = getDirectory(4);
                } else {
                    type5 = 0;
                    dir4 = getDirectory(0);
                }
                long documentId4 = photoSize.location.volume_id;
                type = type5;
                type2 = photoSize.location.dc_id;
                documentId = documentId4;
                dir = dir4;
                String str4 = size;
            } else if (tLObject instanceof TLRPC.TL_videoSize) {
                TLRPC.TL_videoSize videoSize = (TLRPC.TL_videoSize) tLObject;
                if (videoSize.location == null || videoSize.location.key != null || ((videoSize.location.volume_id == -2147483648L && videoSize.location.local_id < 0) || videoSize.size < 0)) {
                    type3 = 4;
                    dir3 = getDirectory(4);
                } else {
                    type3 = 0;
                    dir3 = getDirectory(0);
                }
                long documentId5 = videoSize.location.volume_id;
                type = type3;
                type2 = videoSize.location.dc_id;
                documentId = documentId5;
                dir = dir3;
                String str5 = size;
            } else if (tLObject instanceof TLRPC.FileLocation) {
                TLRPC.FileLocation fileLocation = (TLRPC.FileLocation) tLObject;
                if (fileLocation.key != null || (fileLocation.volume_id == -2147483648L && fileLocation.local_id < 0)) {
                    dir2 = getDirectory(4);
                } else {
                    documentId2 = fileLocation.volume_id;
                    dcId = fileLocation.dc_id;
                    type5 = 0;
                    dir2 = getDirectory(0);
                }
                type = type5;
                type2 = dcId;
                documentId = documentId2;
                dir = dir2;
                String str6 = size;
            } else if ((tLObject instanceof TLRPC.UserProfilePhoto) || (tLObject instanceof TLRPC.ChatPhoto)) {
                if (size == null) {
                    size2 = "s";
                } else {
                    size2 = size;
                }
                if ("s".equals(size2)) {
                    type = 0;
                    type2 = 0;
                    documentId = 0;
                    dir = getDirectory(4);
                    String str7 = size2;
                } else {
                    type = 0;
                    type2 = 0;
                    documentId = 0;
                    dir = getDirectory(0);
                    String str8 = size2;
                }
            } else {
                if (tLObject instanceof WebFile) {
                    WebFile document2 = (WebFile) tLObject;
                    if (document2.mime_type.startsWith("image/")) {
                        dir5 = getDirectory(0);
                    } else if (document2.mime_type.startsWith("audio/")) {
                        dir5 = getDirectory(1);
                    } else if (document2.mime_type.startsWith("video/")) {
                        dir5 = getDirectory(2);
                    } else {
                        dir5 = getDirectory(3);
                    }
                } else if ((tLObject instanceof TLRPC.TL_secureFile) || (tLObject instanceof SecureDocument)) {
                    type = 0;
                    type2 = 0;
                    documentId = 0;
                    dir = getDirectory(4);
                    String str9 = size;
                }
                type = 0;
                type2 = 0;
                documentId = 0;
                dir = dir5;
                String str10 = size;
            }
        }
        if (dir == null) {
            return new File("");
        }
        if (documentId == 0 || (path = getInstance(UserConfig.selectedAccount).getFileDatabase().getPath(documentId, type2, type, useFileDatabaseQueue)) == null) {
            return new File(dir, getAttachFileName(tLObject, str));
        }
        return new File(path);
    }

    /* access modifiers changed from: private */
    public FilePathDatabase getFileDatabase() {
        return this.filePathDatabase;
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
    public /* synthetic */ void m1819x3e68e754(ArrayList messagesToRemove) {
        getDownloadController().recentDownloadingFiles.removeAll(messagesToRemove);
        getNotificationCenter().postNotificationName(NotificationCenter.onDownloadingFilesChanged, new Object[0]);
    }

    public void checkMediaExistance(ArrayList<MessageObject> messageObjects) {
        getFileDatabase().checkMediaExistance(messageObjects);
    }

    public void clearRecentDownloadedFiles() {
        getDownloadController().clearRecentDownloadedFiles();
    }

    public static boolean checkUploadFileSize(int currentAccount, long length) {
        boolean premium = AccountInstance.getInstance(currentAccount).getUserConfig().isPremium();
        if (length < NUM) {
            return true;
        }
        if (length >= 4194304000L || !premium) {
            return false;
        }
        return true;
    }
}
