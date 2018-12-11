package org.telegram.messenger;

import android.text.TextUtils;
import android.util.SparseArray;
import android.util.SparseIntArray;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.MimeTypes;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import org.telegram.messenger.FileLoadOperation.FileLoadOperationDelegate;
import org.telegram.messenger.FileUploadOperation.FileUploadOperationDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.InputEncryptedFile;
import org.telegram.tgnet.TLRPC.InputFile;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.Photo;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_documentAttributeFilename;
import org.telegram.tgnet.TLRPC.TL_fileLocationUnavailable;
import org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
import org.telegram.tgnet.TLRPC.TL_messageMediaInvoice;
import org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
import org.telegram.tgnet.TLRPC.TL_messageMediaWebPage;
import org.telegram.tgnet.TLRPC.TL_messageService;
import org.telegram.tgnet.TLRPC.TL_photoCachedSize;
import org.telegram.tgnet.TLRPC.TL_secureFile;
import org.telegram.tgnet.TLRPC.WebDocument;

public class FileLoader {
    private static volatile FileLoader[] Instance = new FileLoader[3];
    public static final int MEDIA_DIR_AUDIO = 1;
    public static final int MEDIA_DIR_CACHE = 4;
    public static final int MEDIA_DIR_DOCUMENT = 3;
    public static final int MEDIA_DIR_IMAGE = 0;
    public static final int MEDIA_DIR_VIDEO = 2;
    private static volatile DispatchQueue fileLoaderQueue = new DispatchQueue("fileUploadQueue");
    private static SparseArray<File> mediaDirs = null;
    private ArrayList<FileLoadOperation> activeFileLoadOperation = new ArrayList();
    private SparseArray<LinkedList<FileLoadOperation>> audioLoadOperationQueues = new SparseArray();
    private int currentAccount;
    private SparseIntArray currentAudioLoadOperationsCount = new SparseIntArray();
    private SparseIntArray currentLoadOperationsCount = new SparseIntArray();
    private SparseIntArray currentPhotoLoadOperationsCount = new SparseIntArray();
    private int currentUploadOperationsCount = 0;
    private int currentUploadSmallOperationsCount = 0;
    private FileLoaderDelegate delegate = null;
    private int lastReferenceId;
    private ConcurrentHashMap<String, FileLoadOperation> loadOperationPaths = new ConcurrentHashMap();
    private ConcurrentHashMap<String, Boolean> loadOperationPathsUI = new ConcurrentHashMap(10, 1.0f, 2);
    private SparseArray<LinkedList<FileLoadOperation>> loadOperationQueues = new SparseArray();
    private ConcurrentHashMap<Integer, Object> parentObjectReferences = new ConcurrentHashMap();
    private SparseArray<LinkedList<FileLoadOperation>> photoLoadOperationQueues = new SparseArray();
    private ConcurrentHashMap<String, FileUploadOperation> uploadOperationPaths = new ConcurrentHashMap();
    private ConcurrentHashMap<String, FileUploadOperation> uploadOperationPathsEnc = new ConcurrentHashMap();
    private LinkedList<FileUploadOperation> uploadOperationQueue = new LinkedList();
    private HashMap<String, Long> uploadSizes = new HashMap();
    private LinkedList<FileUploadOperation> uploadSmallOperationQueue = new LinkedList();

    public interface FileLoaderDelegate {
        void fileDidFailedLoad(String str, int i);

        void fileDidFailedUpload(String str, boolean z);

        void fileDidLoaded(String str, File file, int i);

        void fileDidUploaded(String str, InputFile inputFile, InputEncryptedFile inputEncryptedFile, byte[] bArr, byte[] bArr2, long j);

        void fileLoadProgressChanged(String str, float f);

        void fileUploadProgressChanged(String str, float f, boolean z);
    }

    public static FileLoader getInstance(int num) {
        Throwable th;
        FileLoader localInstance = Instance[num];
        if (localInstance == null) {
            synchronized (FileLoader.class) {
                try {
                    localInstance = Instance[num];
                    if (localInstance == null) {
                        FileLoader[] fileLoaderArr = Instance;
                        FileLoader localInstance2 = new FileLoader(num);
                        try {
                            fileLoaderArr[num] = localInstance2;
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

    public FileLoader(int instance) {
        this.currentAccount = instance;
    }

    public static void setMediaDirs(SparseArray<File> dirs) {
        mediaDirs = dirs;
    }

    public static File checkDirectory(int type) {
        return (File) mediaDirs.get(type);
    }

    public static File getDirectory(int type) {
        File dir = (File) mediaDirs.get(type);
        if (dir == null && type != 4) {
            dir = (File) mediaDirs.get(4);
        }
        try {
            if (!dir.isDirectory()) {
                dir.mkdirs();
            }
        } catch (Exception e) {
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

    public void cancelUploadFile(String location, boolean enc) {
        fileLoaderQueue.postRunnable(new FileLoader$$Lambda$0(this, enc, location));
    }

    final /* synthetic */ void lambda$cancelUploadFile$0$FileLoader(boolean enc, String location) {
        FileUploadOperation operation;
        if (enc) {
            operation = (FileUploadOperation) this.uploadOperationPathsEnc.get(location);
        } else {
            operation = (FileUploadOperation) this.uploadOperationPaths.get(location);
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
        fileLoaderQueue.postRunnable(new FileLoader$$Lambda$1(this, encrypted, location, newAvailableSize, finalSize));
    }

    final /* synthetic */ void lambda$checkUploadNewDataAvailable$1$FileLoader(boolean encrypted, String location, long newAvailableSize, long finalSize) {
        FileUploadOperation operation;
        if (encrypted) {
            operation = (FileUploadOperation) this.uploadOperationPathsEnc.get(location);
        } else {
            operation = (FileUploadOperation) this.uploadOperationPaths.get(location);
        }
        if (operation != null) {
            operation.checkNewDataAvailable(newAvailableSize, finalSize);
        } else if (finalSize != 0) {
            this.uploadSizes.put(location, Long.valueOf(finalSize));
        }
    }

    public void onNetworkChanged(boolean slow) {
        fileLoaderQueue.postRunnable(new FileLoader$$Lambda$2(this, slow));
    }

    final /* synthetic */ void lambda$onNetworkChanged$2$FileLoader(boolean slow) {
        for (Entry<String, FileUploadOperation> entry : this.uploadOperationPaths.entrySet()) {
            ((FileUploadOperation) entry.getValue()).onNetworkChanged(slow);
        }
        for (Entry<String, FileUploadOperation> entry2 : this.uploadOperationPathsEnc.entrySet()) {
            ((FileUploadOperation) entry2.getValue()).onNetworkChanged(slow);
        }
    }

    public void uploadFile(String location, boolean encrypted, boolean small, int type) {
        uploadFile(location, encrypted, small, 0, type);
    }

    public void uploadFile(String location, boolean encrypted, boolean small, int estimatedSize, int type) {
        if (location != null) {
            fileLoaderQueue.postRunnable(new FileLoader$$Lambda$3(this, encrypted, location, estimatedSize, type, small));
        }
    }

    final /* synthetic */ void lambda$uploadFile$3$FileLoader(final boolean encrypted, final String location, int estimatedSize, int type, final boolean small) {
        if (encrypted) {
            if (this.uploadOperationPathsEnc.containsKey(location)) {
                return;
            }
        } else if (this.uploadOperationPaths.containsKey(location)) {
            return;
        }
        int esimated = estimatedSize;
        if (!(esimated == 0 || ((Long) this.uploadSizes.get(location)) == null)) {
            esimated = 0;
            this.uploadSizes.remove(location);
        }
        FileUploadOperation operation = new FileUploadOperation(this.currentAccount, location, encrypted, esimated, type);
        if (encrypted) {
            this.uploadOperationPathsEnc.put(location, operation);
        } else {
            this.uploadOperationPaths.put(location, operation);
        }
        operation.setDelegate(new FileUploadOperationDelegate() {
            public void didFinishUploadingFile(FileUploadOperation operation, InputFile inputFile, InputEncryptedFile inputEncryptedFile, byte[] key, byte[] iv) {
                FileLoader.fileLoaderQueue.postRunnable(new FileLoader$1$$Lambda$0(this, encrypted, location, small, inputFile, inputEncryptedFile, key, iv, operation));
            }

            final /* synthetic */ void lambda$didFinishUploadingFile$0$FileLoader$1(boolean encrypted, String location, boolean small, InputFile inputFile, InputEncryptedFile inputEncryptedFile, byte[] key, byte[] iv, FileUploadOperation operation) {
                if (encrypted) {
                    FileLoader.this.uploadOperationPathsEnc.remove(location);
                } else {
                    FileLoader.this.uploadOperationPaths.remove(location);
                }
                FileUploadOperation operation12;
                if (small) {
                    FileLoader.this.currentUploadSmallOperationsCount = FileLoader.this.currentUploadSmallOperationsCount - 1;
                    if (FileLoader.this.currentUploadSmallOperationsCount < 1) {
                        operation12 = (FileUploadOperation) FileLoader.this.uploadSmallOperationQueue.poll();
                        if (operation12 != null) {
                            FileLoader.this.currentUploadSmallOperationsCount = FileLoader.this.currentUploadSmallOperationsCount + 1;
                            operation12.start();
                        }
                    }
                } else {
                    FileLoader.this.currentUploadOperationsCount = FileLoader.this.currentUploadOperationsCount - 1;
                    if (FileLoader.this.currentUploadOperationsCount < 1) {
                        operation12 = (FileUploadOperation) FileLoader.this.uploadOperationQueue.poll();
                        if (operation12 != null) {
                            FileLoader.this.currentUploadOperationsCount = FileLoader.this.currentUploadOperationsCount + 1;
                            operation12.start();
                        }
                    }
                }
                if (FileLoader.this.delegate != null) {
                    FileLoader.this.delegate.fileDidUploaded(location, inputFile, inputEncryptedFile, key, iv, operation.getTotalFileSize());
                }
            }

            public void didFailedUploadingFile(FileUploadOperation operation) {
                FileLoader.fileLoaderQueue.postRunnable(new FileLoader$1$$Lambda$1(this, encrypted, location, small));
            }

            final /* synthetic */ void lambda$didFailedUploadingFile$1$FileLoader$1(boolean encrypted, String location, boolean small) {
                if (encrypted) {
                    FileLoader.this.uploadOperationPathsEnc.remove(location);
                } else {
                    FileLoader.this.uploadOperationPaths.remove(location);
                }
                if (FileLoader.this.delegate != null) {
                    FileLoader.this.delegate.fileDidFailedUpload(location, encrypted);
                }
                FileUploadOperation operation1;
                if (small) {
                    FileLoader.this.currentUploadSmallOperationsCount = FileLoader.this.currentUploadSmallOperationsCount - 1;
                    if (FileLoader.this.currentUploadSmallOperationsCount < 1) {
                        operation1 = (FileUploadOperation) FileLoader.this.uploadSmallOperationQueue.poll();
                        if (operation1 != null) {
                            FileLoader.this.currentUploadSmallOperationsCount = FileLoader.this.currentUploadSmallOperationsCount + 1;
                            operation1.start();
                            return;
                        }
                        return;
                    }
                    return;
                }
                FileLoader.this.currentUploadOperationsCount = FileLoader.this.currentUploadOperationsCount - 1;
                if (FileLoader.this.currentUploadOperationsCount < 1) {
                    operation1 = (FileUploadOperation) FileLoader.this.uploadOperationQueue.poll();
                    if (operation1 != null) {
                        FileLoader.this.currentUploadOperationsCount = FileLoader.this.currentUploadOperationsCount + 1;
                        operation1.start();
                    }
                }
            }

            public void didChangedUploadProgress(FileUploadOperation operation, float progress) {
                if (FileLoader.this.delegate != null) {
                    FileLoader.this.delegate.fileUploadProgressChanged(location, progress, encrypted);
                }
            }
        });
        if (small) {
            if (this.currentUploadSmallOperationsCount < 1) {
                this.currentUploadSmallOperationsCount++;
                operation.start();
                return;
            }
            this.uploadSmallOperationQueue.add(operation);
        } else if (this.currentUploadOperationsCount < 1) {
            this.currentUploadOperationsCount++;
            operation.start();
        } else {
            this.uploadOperationQueue.add(operation);
        }
    }

    private LinkedList<FileLoadOperation> getAudioLoadOperationQueue(int datacenterId) {
        LinkedList<FileLoadOperation> audioLoadOperationQueue = (LinkedList) this.audioLoadOperationQueues.get(datacenterId);
        if (audioLoadOperationQueue != null) {
            return audioLoadOperationQueue;
        }
        audioLoadOperationQueue = new LinkedList();
        this.audioLoadOperationQueues.put(datacenterId, audioLoadOperationQueue);
        return audioLoadOperationQueue;
    }

    private LinkedList<FileLoadOperation> getPhotoLoadOperationQueue(int datacenterId) {
        LinkedList<FileLoadOperation> photoLoadOperationQueue = (LinkedList) this.photoLoadOperationQueues.get(datacenterId);
        if (photoLoadOperationQueue != null) {
            return photoLoadOperationQueue;
        }
        photoLoadOperationQueue = new LinkedList();
        this.photoLoadOperationQueues.put(datacenterId, photoLoadOperationQueue);
        return photoLoadOperationQueue;
    }

    private LinkedList<FileLoadOperation> getLoadOperationQueue(int datacenterId) {
        LinkedList<FileLoadOperation> loadOperationQueue = (LinkedList) this.loadOperationQueues.get(datacenterId);
        if (loadOperationQueue != null) {
            return loadOperationQueue;
        }
        loadOperationQueue = new LinkedList();
        this.loadOperationQueues.put(datacenterId, loadOperationQueue);
        return loadOperationQueue;
    }

    public void cancelLoadFile(Document document) {
        cancelLoadFile(document, null, null, null, null);
    }

    public void cancelLoadFile(SecureDocument document) {
        cancelLoadFile(null, document, null, null, null);
    }

    public void cancelLoadFile(WebFile document) {
        cancelLoadFile(null, null, document, null, null);
    }

    public void cancelLoadFile(PhotoSize photo) {
        cancelLoadFile(null, null, null, photo.location, null);
    }

    public void cancelLoadFile(FileLocation location, String ext) {
        cancelLoadFile(null, null, null, location, ext);
    }

    private void cancelLoadFile(Document document, SecureDocument secureDocument, WebFile webDocument, FileLocation location, String locationExt) {
        if (location != null || document != null || webDocument != null || secureDocument != null) {
            String fileName;
            if (location != null) {
                fileName = getAttachFileName(location, locationExt);
            } else if (document != null) {
                fileName = getAttachFileName(document);
            } else if (secureDocument != null) {
                fileName = getAttachFileName(secureDocument);
            } else if (webDocument != null) {
                fileName = getAttachFileName(webDocument);
            } else {
                fileName = null;
            }
            if (fileName != null) {
                this.loadOperationPathsUI.remove(fileName);
                fileLoaderQueue.postRunnable(new FileLoader$$Lambda$4(this, fileName, document, webDocument, secureDocument, location));
            }
        }
    }

    final /* synthetic */ void lambda$cancelLoadFile$4$FileLoader(String fileName, Document document, WebFile webDocument, SecureDocument secureDocument, FileLocation location) {
        FileLoadOperation operation = (FileLoadOperation) this.loadOperationPaths.remove(fileName);
        if (operation != null) {
            int datacenterId = operation.getDatacenterId();
            if (MessageObject.isVoiceDocument(document) || MessageObject.isVoiceWebDocument(webDocument)) {
                if (!getAudioLoadOperationQueue(datacenterId).remove(operation)) {
                    this.currentAudioLoadOperationsCount.put(datacenterId, this.currentAudioLoadOperationsCount.get(datacenterId) - 1);
                }
            } else if (secureDocument == null && location == null && !MessageObject.isImageWebDocument(webDocument)) {
                if (!getLoadOperationQueue(datacenterId).remove(operation)) {
                    this.currentLoadOperationsCount.put(datacenterId, this.currentLoadOperationsCount.get(datacenterId) - 1);
                }
                this.activeFileLoadOperation.remove(operation);
            } else if (!getPhotoLoadOperationQueue(datacenterId).remove(operation)) {
                this.currentPhotoLoadOperationsCount.put(datacenterId, this.currentPhotoLoadOperationsCount.get(datacenterId) - 1);
            }
            operation.cancel();
        }
    }

    public boolean isLoadingFile(String fileName) {
        return this.loadOperationPathsUI.containsKey(fileName);
    }

    public float getBufferedProgressFromPosition(float position, String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            return 0.0f;
        }
        FileLoadOperation loadOperation = (FileLoadOperation) this.loadOperationPaths.get(fileName);
        if (loadOperation != null) {
            return loadOperation.getDownloadedLengthFromOffset(position);
        }
        return 0.0f;
    }

    public void loadFile(PhotoSize photo, String ext, int cacheType) {
        if (photo != null) {
            if (cacheType == 0 && photo != null && (photo.size == 0 || photo.location.key != null)) {
                cacheType = 1;
            }
            loadFile(null, null, null, photo.location, null, ext, photo.size, 0, cacheType);
        }
    }

    public void loadFile(SecureDocument secureDocument, int priority) {
        if (secureDocument != null) {
            loadFile(null, secureDocument, null, null, null, null, 0, priority, 1);
        }
    }

    public void loadFile(Document document, Object parentObject, int priority, int cacheType) {
        if (document != null) {
            if (!(cacheType != 0 || document == null || document.key == null)) {
                cacheType = 1;
            }
            loadFile(document, null, null, null, parentObject, null, 0, priority, cacheType);
        }
    }

    public void loadFile(WebFile document, int priority, int cacheType) {
        loadFile(null, null, document, null, null, null, 0, priority, cacheType);
    }

    public void loadFile(FileLocation location, Object parentObject, String ext, int size, int priority, int cacheType) {
        if (location != null) {
            if (cacheType == 0 && (size == 0 || !(location == null || location.key == null))) {
                cacheType = 1;
            }
            loadFile(null, null, null, location, parentObject, ext, size, priority, cacheType);
        }
    }

    private void pauseCurrentFileLoadOperations(FileLoadOperation newOperation) {
        int a = 0;
        while (a < this.activeFileLoadOperation.size()) {
            FileLoadOperation operation = (FileLoadOperation) this.activeFileLoadOperation.get(a);
            if (operation != newOperation) {
                this.activeFileLoadOperation.remove(operation);
                a--;
                operation.pause();
                int datacenterId = operation.getDatacenterId();
                getLoadOperationQueue(datacenterId).add(0, operation);
                if (operation.wasStarted()) {
                    this.currentLoadOperationsCount.put(datacenterId, this.currentLoadOperationsCount.get(datacenterId) - 1);
                }
            }
            a++;
        }
    }

    private FileLoadOperation loadFileInternal(Document document, SecureDocument secureDocument, WebFile webDocument, FileLocation location, Object parentObject, String locationExt, int locationSize, int priority, FileStreamLoadOperation stream, int streamOffset, int cacheType) {
        String fileName = null;
        if (location != null) {
            fileName = getAttachFileName(location, locationExt);
        } else if (secureDocument != null) {
            fileName = getAttachFileName(secureDocument);
        } else if (document != null) {
            fileName = getAttachFileName(document);
        } else if (webDocument != null) {
            fileName = getAttachFileName(webDocument);
        }
        if (fileName != null) {
            if (!fileName.contains("-2147483648")) {
                if (!TextUtils.isEmpty(fileName)) {
                    if (!fileName.contains("-2147483648")) {
                        this.loadOperationPathsUI.put(fileName, Boolean.valueOf(true));
                    }
                }
                FileLoadOperation operation = (FileLoadOperation) this.loadOperationPaths.get(fileName);
                int datacenterId;
                LinkedList<FileLoadOperation> audioLoadOperationQueue;
                LinkedList<FileLoadOperation> photoLoadOperationQueue;
                LinkedList<FileLoadOperation> loadOperationQueue;
                if (operation == null) {
                    File tempDir = getDirectory(4);
                    File storeDir = tempDir;
                    int type = 4;
                    FileLoadOperation fileLoadOperation;
                    if (secureDocument != null) {
                        fileLoadOperation = new FileLoadOperation(secureDocument);
                        type = 3;
                    } else if (location != null) {
                        fileLoadOperation = new FileLoadOperation(location, parentObject, locationExt, locationSize);
                        type = 0;
                    } else if (document != null) {
                        fileLoadOperation = new FileLoadOperation(document, parentObject);
                        if (MessageObject.isVoiceDocument(document)) {
                            type = 1;
                        } else if (MessageObject.isVideoDocument(document)) {
                            type = 2;
                        } else {
                            type = 3;
                        }
                    } else if (webDocument != null) {
                        fileLoadOperation = new FileLoadOperation(this.currentAccount, webDocument);
                        if (MessageObject.isVoiceWebDocument(webDocument)) {
                            type = 1;
                        } else if (MessageObject.isVideoWebDocument(webDocument)) {
                            type = 2;
                        } else if (MessageObject.isImageWebDocument(webDocument)) {
                            type = 0;
                        } else {
                            type = 3;
                        }
                    }
                    if (cacheType == 0) {
                        storeDir = getDirectory(type);
                    } else if (cacheType == 2) {
                        operation.setEncryptFile(true);
                    }
                    operation.setPaths(this.currentAccount, storeDir, tempDir);
                    final String finalFileName = fileName;
                    final int finalType = type;
                    final Document document2 = document;
                    final WebFile webFile = webDocument;
                    final FileLocation fileLocation = location;
                    operation.setDelegate(new FileLoadOperationDelegate() {
                        public void didFinishLoadingFile(FileLoadOperation operation, File finalFile) {
                            FileLoader.this.loadOperationPathsUI.remove(finalFileName);
                            if (FileLoader.this.delegate != null) {
                                FileLoader.this.delegate.fileDidLoaded(finalFileName, finalFile, finalType);
                            }
                            FileLoader.this.checkDownloadQueue(operation.getDatacenterId(), document2, webFile, fileLocation, finalFileName);
                        }

                        public void didFailedLoadingFile(FileLoadOperation operation, int reason) {
                            FileLoader.this.loadOperationPathsUI.remove(finalFileName);
                            FileLoader.this.checkDownloadQueue(operation.getDatacenterId(), document2, webFile, fileLocation, finalFileName);
                            if (FileLoader.this.delegate != null) {
                                FileLoader.this.delegate.fileDidFailedLoad(finalFileName, reason);
                            }
                        }

                        public void didChangedLoadProgress(FileLoadOperation operation, float progress) {
                            if (FileLoader.this.delegate != null) {
                                FileLoader.this.delegate.fileLoadProgressChanged(finalFileName, progress);
                            }
                        }
                    });
                    datacenterId = operation.getDatacenterId();
                    audioLoadOperationQueue = getAudioLoadOperationQueue(datacenterId);
                    photoLoadOperationQueue = getPhotoLoadOperationQueue(datacenterId);
                    loadOperationQueue = getLoadOperationQueue(datacenterId);
                    this.loadOperationPaths.put(fileName, operation);
                    operation.setPriority(priority);
                    int maxCount;
                    int count;
                    if (type == 1) {
                        maxCount = priority > 0 ? 3 : 1;
                        count = this.currentAudioLoadOperationsCount.get(datacenterId);
                        if (streamOffset == 0 && count >= maxCount) {
                            addOperationToQueue(operation, audioLoadOperationQueue);
                            return operation;
                        } else if (!operation.start(stream, streamOffset)) {
                            return operation;
                        } else {
                            this.currentAudioLoadOperationsCount.put(datacenterId, count + 1);
                            return operation;
                        }
                    } else if (location != null || MessageObject.isImageWebDocument(webDocument)) {
                        maxCount = priority > 0 ? 6 : 2;
                        count = this.currentPhotoLoadOperationsCount.get(datacenterId);
                        if (streamOffset == 0 && count >= maxCount) {
                            addOperationToQueue(operation, photoLoadOperationQueue);
                            return operation;
                        } else if (!operation.start(stream, streamOffset)) {
                            return operation;
                        } else {
                            this.currentPhotoLoadOperationsCount.put(datacenterId, count + 1);
                            return operation;
                        }
                    } else {
                        maxCount = priority > 0 ? 3 : 1;
                        count = this.currentLoadOperationsCount.get(datacenterId);
                        if (streamOffset != 0 || count < maxCount) {
                            if (operation.start(stream, streamOffset)) {
                                this.currentLoadOperationsCount.put(datacenterId, count + 1);
                                this.activeFileLoadOperation.add(operation);
                            }
                            if (!operation.wasStarted() || stream == null) {
                                return operation;
                            }
                            pauseCurrentFileLoadOperations(operation);
                            return operation;
                        }
                        addOperationToQueue(operation, loadOperationQueue);
                        return operation;
                    }
                } else if (streamOffset == 0 && priority <= 0) {
                    return operation;
                } else {
                    LinkedList<FileLoadOperation> downloadQueue;
                    datacenterId = operation.getDatacenterId();
                    audioLoadOperationQueue = getAudioLoadOperationQueue(datacenterId);
                    photoLoadOperationQueue = getPhotoLoadOperationQueue(datacenterId);
                    loadOperationQueue = getLoadOperationQueue(datacenterId);
                    operation.setForceRequest(true);
                    if (MessageObject.isVoiceDocument(document) || MessageObject.isVoiceWebDocument(webDocument)) {
                        downloadQueue = audioLoadOperationQueue;
                    } else if (secureDocument == null && location == null && !MessageObject.isImageWebDocument(webDocument)) {
                        downloadQueue = loadOperationQueue;
                    } else {
                        downloadQueue = photoLoadOperationQueue;
                    }
                    if (downloadQueue == null) {
                        return operation;
                    }
                    int index = downloadQueue.indexOf(operation);
                    if (index > 0) {
                        downloadQueue.remove(index);
                        if (streamOffset == 0) {
                            downloadQueue.add(0, operation);
                            return operation;
                        } else if (downloadQueue == audioLoadOperationQueue) {
                            if (!operation.start(stream, streamOffset)) {
                                return operation;
                            }
                            this.currentAudioLoadOperationsCount.put(datacenterId, this.currentAudioLoadOperationsCount.get(datacenterId) + 1);
                            return operation;
                        } else if (downloadQueue != photoLoadOperationQueue) {
                            if (operation.start(stream, streamOffset)) {
                                this.currentLoadOperationsCount.put(datacenterId, this.currentLoadOperationsCount.get(datacenterId) + 1);
                            }
                            if (!operation.wasStarted() || this.activeFileLoadOperation.contains(operation)) {
                                return operation;
                            }
                            if (stream != null) {
                                pauseCurrentFileLoadOperations(operation);
                            }
                            this.activeFileLoadOperation.add(operation);
                            return operation;
                        } else if (!operation.start(stream, streamOffset)) {
                            return operation;
                        } else {
                            this.currentPhotoLoadOperationsCount.put(datacenterId, this.currentPhotoLoadOperationsCount.get(datacenterId) + 1);
                            return operation;
                        }
                    }
                    if (stream != null) {
                        pauseCurrentFileLoadOperations(operation);
                    }
                    operation.start(stream, streamOffset);
                    if (downloadQueue != loadOperationQueue || this.activeFileLoadOperation.contains(operation)) {
                        return operation;
                    }
                    this.activeFileLoadOperation.add(operation);
                    return operation;
                }
            }
        }
        return null;
    }

    private void addOperationToQueue(FileLoadOperation operation, LinkedList<FileLoadOperation> queue) {
        int priority = operation.getPriority();
        if (priority > 0) {
            int index = queue.size();
            int size = queue.size();
            for (int a = 0; a < size; a++) {
                if (((FileLoadOperation) queue.get(a)).getPriority() < priority) {
                    index = a;
                    break;
                }
            }
            queue.add(index, operation);
            return;
        }
        queue.add(operation);
    }

    private void loadFile(Document document, SecureDocument secureDocument, WebFile webDocument, FileLocation location, Object parentObject, String locationExt, int locationSize, int priority, int cacheType) {
        String fileName;
        if (location != null) {
            fileName = getAttachFileName(location, locationExt);
        } else if (document != null) {
            fileName = getAttachFileName(document);
        } else if (webDocument != null) {
            fileName = getAttachFileName(webDocument);
        } else {
            fileName = null;
        }
        if (!(TextUtils.isEmpty(fileName) || fileName.contains("-2147483648"))) {
            this.loadOperationPathsUI.put(fileName, Boolean.valueOf(true));
        }
        fileLoaderQueue.postRunnable(new FileLoader$$Lambda$5(this, document, secureDocument, webDocument, location, parentObject, locationExt, locationSize, priority, cacheType));
    }

    final /* synthetic */ void lambda$loadFile$5$FileLoader(Document document, SecureDocument secureDocument, WebFile webDocument, FileLocation location, Object parentObject, String locationExt, int locationSize, int priority, int cacheType) {
        loadFileInternal(document, secureDocument, webDocument, location, parentObject, locationExt, locationSize, priority, null, 0, cacheType);
    }

    protected FileLoadOperation loadStreamFile(FileStreamLoadOperation stream, Document document, Object parentObject, int offset) {
        CountDownLatch semaphore = new CountDownLatch(1);
        FileLoadOperation[] result = new FileLoadOperation[1];
        fileLoaderQueue.postRunnable(new FileLoader$$Lambda$6(this, result, document, parentObject, stream, offset, semaphore));
        try {
            semaphore.await();
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
        return result[0];
    }

    final /* synthetic */ void lambda$loadStreamFile$6$FileLoader(FileLoadOperation[] result, Document document, Object parentObject, FileStreamLoadOperation stream, int offset, CountDownLatch semaphore) {
        result[0] = loadFileInternal(document, null, null, null, parentObject, null, 0, 1, stream, offset, 0);
        semaphore.countDown();
    }

    private void checkDownloadQueue(int datacenterId, Document document, WebFile webDocument, FileLocation location, String arg1) {
        fileLoaderQueue.postRunnable(new FileLoader$$Lambda$7(this, datacenterId, arg1, document, webDocument, location));
    }

    final /* synthetic */ void lambda$checkDownloadQueue$7$FileLoader(int datacenterId, String arg1, Document document, WebFile webDocument, FileLocation location) {
        LinkedList<FileLoadOperation> audioLoadOperationQueue = getAudioLoadOperationQueue(datacenterId);
        LinkedList<FileLoadOperation> photoLoadOperationQueue = getPhotoLoadOperationQueue(datacenterId);
        LinkedList<FileLoadOperation> loadOperationQueue = getLoadOperationQueue(datacenterId);
        FileLoadOperation operation = (FileLoadOperation) this.loadOperationPaths.remove(arg1);
        int count;
        int maxCount;
        if (MessageObject.isVoiceDocument(document) || MessageObject.isVoiceWebDocument(webDocument)) {
            count = this.currentAudioLoadOperationsCount.get(datacenterId);
            if (operation != null) {
                if (operation.wasStarted()) {
                    count--;
                    this.currentAudioLoadOperationsCount.put(datacenterId, count);
                } else {
                    audioLoadOperationQueue.remove(operation);
                }
            }
            while (!audioLoadOperationQueue.isEmpty()) {
                if (((FileLoadOperation) audioLoadOperationQueue.get(0)).getPriority() != 0) {
                    maxCount = 3;
                } else {
                    maxCount = 1;
                }
                if (count < maxCount) {
                    operation = (FileLoadOperation) audioLoadOperationQueue.poll();
                    if (operation != null && operation.start()) {
                        count++;
                        this.currentAudioLoadOperationsCount.put(datacenterId, count);
                    }
                } else {
                    return;
                }
            }
        } else if (location != null || MessageObject.isImageWebDocument(webDocument)) {
            count = this.currentPhotoLoadOperationsCount.get(datacenterId);
            if (operation != null) {
                if (operation.wasStarted()) {
                    count--;
                    this.currentPhotoLoadOperationsCount.put(datacenterId, count);
                } else {
                    photoLoadOperationQueue.remove(operation);
                }
            }
            while (!photoLoadOperationQueue.isEmpty()) {
                if (count < (((FileLoadOperation) photoLoadOperationQueue.get(0)).getPriority() != 0 ? 6 : 2)) {
                    operation = (FileLoadOperation) photoLoadOperationQueue.poll();
                    if (operation != null && operation.start()) {
                        count++;
                        this.currentPhotoLoadOperationsCount.put(datacenterId, count);
                    }
                } else {
                    return;
                }
            }
        } else {
            count = this.currentLoadOperationsCount.get(datacenterId);
            if (operation != null) {
                if (operation.wasStarted()) {
                    count--;
                    this.currentLoadOperationsCount.put(datacenterId, count);
                } else {
                    loadOperationQueue.remove(operation);
                }
                this.activeFileLoadOperation.remove(operation);
            }
            while (!loadOperationQueue.isEmpty()) {
                if (((FileLoadOperation) loadOperationQueue.get(0)).isForceRequest()) {
                    maxCount = 3;
                } else {
                    maxCount = 1;
                }
                if (count < maxCount) {
                    operation = (FileLoadOperation) loadOperationQueue.poll();
                    if (operation != null && operation.start()) {
                        count++;
                        this.currentLoadOperationsCount.put(datacenterId, count);
                        if (!this.activeFileLoadOperation.contains(operation)) {
                            this.activeFileLoadOperation.add(operation);
                        }
                    }
                } else {
                    return;
                }
            }
        }
    }

    public void setDelegate(FileLoaderDelegate delegate) {
        this.delegate = delegate;
    }

    public static String getMessageFileName(Message message) {
        if (message == null) {
            return TtmlNode.ANONYMOUS_REGION_ID;
        }
        ArrayList<PhotoSize> sizes;
        PhotoSize sizeFull;
        if (message instanceof TL_messageService) {
            if (message.action.photo != null) {
                sizes = message.action.photo.sizes;
                if (sizes.size() > 0) {
                    sizeFull = getClosestPhotoSizeWithSize(sizes, AndroidUtilities.getPhotoSize());
                    if (sizeFull != null) {
                        return getAttachFileName(sizeFull);
                    }
                }
            }
        } else if (message.media instanceof TL_messageMediaDocument) {
            return getAttachFileName(message.media.document);
        } else {
            if (message.media instanceof TL_messageMediaPhoto) {
                sizes = message.media.photo.sizes;
                if (sizes.size() > 0) {
                    sizeFull = getClosestPhotoSizeWithSize(sizes, AndroidUtilities.getPhotoSize());
                    if (sizeFull != null) {
                        return getAttachFileName(sizeFull);
                    }
                }
            } else if (message.media instanceof TL_messageMediaWebPage) {
                if (message.media.webpage.document != null) {
                    return getAttachFileName(message.media.webpage.document);
                }
                if (message.media.webpage.photo != null) {
                    sizes = message.media.webpage.photo.sizes;
                    if (sizes.size() > 0) {
                        sizeFull = getClosestPhotoSizeWithSize(sizes, AndroidUtilities.getPhotoSize());
                        if (sizeFull != null) {
                            return getAttachFileName(sizeFull);
                        }
                    }
                } else if (message.media instanceof TL_messageMediaInvoice) {
                    return getAttachFileName(((TL_messageMediaInvoice) message.media).photo);
                }
            } else if (message.media instanceof TL_messageMediaInvoice) {
                WebDocument document = ((TL_messageMediaInvoice) message.media).photo;
                if (document != null) {
                    return Utilities.MD5(document.url) + "." + ImageLoader.getHttpUrlExtension(document.url, getExtensionByMime(document.mime_type));
                }
            }
        }
        return TtmlNode.ANONYMOUS_REGION_ID;
    }

    public static File getPathToMessage(Message message) {
        boolean z = false;
        boolean z2 = true;
        if (message == null) {
            return new File(TtmlNode.ANONYMOUS_REGION_ID);
        }
        ArrayList<PhotoSize> sizes;
        PhotoSize sizeFull;
        if (message instanceof TL_messageService) {
            if (message.action.photo != null) {
                sizes = message.action.photo.sizes;
                if (sizes.size() > 0) {
                    sizeFull = getClosestPhotoSizeWithSize(sizes, AndroidUtilities.getPhotoSize());
                    if (sizeFull != null) {
                        return getPathToAttach(sizeFull);
                    }
                }
            }
        } else if (message.media instanceof TL_messageMediaDocument) {
            TLObject tLObject = message.media.document;
            if (message.media.ttl_seconds != 0) {
                z = true;
            }
            return getPathToAttach(tLObject, z);
        } else if (message.media instanceof TL_messageMediaPhoto) {
            sizes = message.media.photo.sizes;
            if (sizes.size() > 0) {
                sizeFull = getClosestPhotoSizeWithSize(sizes, AndroidUtilities.getPhotoSize());
                if (sizeFull != null) {
                    if (message.media.ttl_seconds == 0) {
                        z2 = false;
                    }
                    return getPathToAttach(sizeFull, z2);
                }
            }
        } else if (message.media instanceof TL_messageMediaWebPage) {
            if (message.media.webpage.document != null) {
                return getPathToAttach(message.media.webpage.document);
            }
            if (message.media.webpage.photo != null) {
                sizes = message.media.webpage.photo.sizes;
                if (sizes.size() > 0) {
                    sizeFull = getClosestPhotoSizeWithSize(sizes, AndroidUtilities.getPhotoSize());
                    if (sizeFull != null) {
                        return getPathToAttach(sizeFull);
                    }
                }
            }
        } else if (message.media instanceof TL_messageMediaInvoice) {
            return getPathToAttach(((TL_messageMediaInvoice) message.media).photo, true);
        }
        return new File(TtmlNode.ANONYMOUS_REGION_ID);
    }

    public static File getPathToAttach(TLObject attach) {
        return getPathToAttach(attach, null, false);
    }

    public static File getPathToAttach(TLObject attach, boolean forceCache) {
        return getPathToAttach(attach, null, forceCache);
    }

    public static File getPathToAttach(TLObject attach, String ext, boolean forceCache) {
        File dir = null;
        if (forceCache) {
            dir = getDirectory(4);
        } else if (attach instanceof Document) {
            Document document = (Document) attach;
            if (document.key != null) {
                dir = getDirectory(4);
            } else if (MessageObject.isVoiceDocument(document)) {
                dir = getDirectory(1);
            } else if (MessageObject.isVideoDocument(document)) {
                dir = getDirectory(2);
            } else {
                dir = getDirectory(3);
            }
        } else if (attach instanceof Photo) {
            return getPathToAttach(getClosestPhotoSizeWithSize(((Photo) attach).sizes, AndroidUtilities.getPhotoSize()), ext, forceCache);
        } else {
            if (attach instanceof PhotoSize) {
                PhotoSize photoSize = (PhotoSize) attach;
                if (photoSize.location == null || photoSize.location.key != null || ((photoSize.location.volume_id == -2147483648L && photoSize.location.local_id < 0) || photoSize.size < 0)) {
                    dir = getDirectory(4);
                } else {
                    dir = getDirectory(0);
                }
            } else if (attach instanceof FileLocation) {
                FileLocation fileLocation = (FileLocation) attach;
                if (fileLocation.key != null || (fileLocation.volume_id == -2147483648L && fileLocation.local_id < 0)) {
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
            } else if ((attach instanceof TL_secureFile) || (attach instanceof SecureDocument)) {
                dir = getDirectory(4);
            }
        }
        if (dir == null) {
            return new File(TtmlNode.ANONYMOUS_REGION_ID);
        }
        return new File(dir, getAttachFileName(attach, ext));
    }

    public static FileStreamLoadOperation getStreamLoadOperation(TransferListener listener) {
        return new FileStreamLoadOperation(listener);
    }

    public static PhotoSize getClosestPhotoSizeWithSize(ArrayList<PhotoSize> sizes, int side) {
        return getClosestPhotoSizeWithSize(sizes, side, false);
    }

    public static PhotoSize getClosestPhotoSizeWithSize(ArrayList<PhotoSize> sizes, int side, boolean byMinSide) {
        if (sizes == null || sizes.isEmpty()) {
            return null;
        }
        int lastSide = 0;
        PhotoSize closestObject = null;
        for (int a = 0; a < sizes.size(); a++) {
            PhotoSize obj = (PhotoSize) sizes.get(a);
            if (obj != null) {
                int currentSide;
                if (byMinSide) {
                    currentSide = obj.var_h >= obj.var_w ? obj.var_w : obj.var_h;
                    if (closestObject == null || ((side > 100 && closestObject.location != null && closestObject.location.dc_id == Integer.MIN_VALUE) || (obj instanceof TL_photoCachedSize) || (side > lastSide && lastSide < currentSide))) {
                        closestObject = obj;
                        lastSide = currentSide;
                    }
                } else {
                    currentSide = obj.var_w >= obj.var_h ? obj.var_w : obj.var_h;
                    if (closestObject == null || ((side > 100 && closestObject.location != null && closestObject.location.dc_id == Integer.MIN_VALUE) || (obj instanceof TL_photoCachedSize) || (currentSide <= side && lastSide < currentSide))) {
                        closestObject = obj;
                        lastSide = currentSide;
                    }
                }
            }
        }
        return closestObject;
    }

    public static String getFileExtension(File file) {
        String name = file.getName();
        try {
            return name.substring(name.lastIndexOf(46) + 1);
        } catch (Exception e) {
            return TtmlNode.ANONYMOUS_REGION_ID;
        }
    }

    public static String fixFileName(String fileName) {
        if (fileName != null) {
            return fileName.replaceAll("[\u0001-\u001f<>:\"/\\\\|?*\u007f]+", TtmlNode.ANONYMOUS_REGION_ID).trim();
        }
        return fileName;
    }

    public static String getDocumentFileName(Document document) {
        String fileName = null;
        if (document != null) {
            if (document.file_name != null) {
                fileName = document.file_name;
            } else {
                for (int a = 0; a < document.attributes.size(); a++) {
                    DocumentAttribute documentAttribute = (DocumentAttribute) document.attributes.get(a);
                    if (documentAttribute instanceof TL_documentAttributeFilename) {
                        fileName = documentAttribute.file_name;
                    }
                }
            }
        }
        fileName = fixFileName(fileName);
        return fileName != null ? fileName : TtmlNode.ANONYMOUS_REGION_ID;
    }

    public static String getExtensionByMime(String mime) {
        int index = mime.lastIndexOf(47);
        if (index != -1) {
            return mime.substring(index + 1);
        }
        return TtmlNode.ANONYMOUS_REGION_ID;
    }

    public static File getInternalCacheDir() {
        return ApplicationLoader.applicationContext.getCacheDir();
    }

    public static String getDocumentExtension(Document document) {
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
            ext = TtmlNode.ANONYMOUS_REGION_ID;
        }
        return ext.toUpperCase();
    }

    public static String getAttachFileName(TLObject attach) {
        return getAttachFileName(attach, null);
    }

    public static String getAttachFileName(TLObject attach, String ext) {
        Object obj = -1;
        StringBuilder append;
        FileLocation location;
        if (attach instanceof Document) {
            Document document = (Document) attach;
            String docExt = null;
            if (null == null) {
                docExt = getDocumentFileName(document);
                if (docExt != null) {
                    int idx = docExt.lastIndexOf(46);
                    if (idx != -1) {
                        docExt = docExt.substring(idx);
                    }
                }
                docExt = TtmlNode.ANONYMOUS_REGION_ID;
            }
            if (docExt.length() <= 1) {
                if (document.mime_type != null) {
                    String str = document.mime_type;
                    switch (str.hashCode()) {
                        case 187091926:
                            if (str.equals("audio/ogg")) {
                                int obj2 = 1;
                                break;
                            }
                            break;
                        case 1331848029:
                            if (str.equals(MimeTypes.VIDEO_MP4)) {
                                obj2 = null;
                                break;
                            }
                            break;
                    }
                    switch (obj2) {
                        case null:
                            docExt = ".mp4";
                            break;
                        case 1:
                            docExt = ".ogg";
                            break;
                        default:
                            docExt = TtmlNode.ANONYMOUS_REGION_ID;
                            break;
                    }
                }
                docExt = TtmlNode.ANONYMOUS_REGION_ID;
            }
            if (docExt.length() > 1) {
                return document.dc_id + "_" + document.var_id + docExt;
            }
            return document.dc_id + "_" + document.var_id;
        } else if (attach instanceof SecureDocument) {
            SecureDocument secureDocument = (SecureDocument) attach;
            return secureDocument.secureFile.dc_id + "_" + secureDocument.secureFile.var_id + ".jpg";
        } else if (attach instanceof TL_secureFile) {
            TL_secureFile secureFile = (TL_secureFile) attach;
            return secureFile.dc_id + "_" + secureFile.var_id + ".jpg";
        } else if (attach instanceof WebFile) {
            WebFile document2 = (WebFile) attach;
            return Utilities.MD5(document2.url) + "." + ImageLoader.getHttpUrlExtension(document2.url, getExtensionByMime(document2.mime_type));
        } else if (attach instanceof PhotoSize) {
            PhotoSize photo = (PhotoSize) attach;
            if (photo.location == null || (photo.location instanceof TL_fileLocationUnavailable)) {
                return TtmlNode.ANONYMOUS_REGION_ID;
            }
            append = new StringBuilder().append(photo.location.volume_id).append("_").append(photo.location.local_id).append(".");
            if (ext == null) {
                ext = "jpg";
            }
            return append.append(ext).toString();
        } else if (attach instanceof FileLocation) {
            if (attach instanceof TL_fileLocationUnavailable) {
                return TtmlNode.ANONYMOUS_REGION_ID;
            }
            location = (FileLocation) attach;
            append = new StringBuilder().append(location.volume_id).append("_").append(location.local_id).append(".");
            if (ext == null) {
                ext = "jpg";
            }
            return append.append(ext).toString();
        } else if (!(attach instanceof Photo)) {
            return TtmlNode.ANONYMOUS_REGION_ID;
        } else {
            location = (FileLocation) attach;
            append = new StringBuilder().append(location.volume_id).append("_").append(location.local_id).append(".");
            if (ext == null) {
                ext = "jpg";
            }
            return append.append(ext).toString();
        }
    }

    public void deleteFiles(ArrayList<File> files, int type) {
        if (files != null && !files.isEmpty()) {
            fileLoaderQueue.postRunnable(new FileLoader$$Lambda$8(files, type));
        }
    }

    static final /* synthetic */ void lambda$deleteFiles$8$FileLoader(ArrayList files, int type) {
        for (int a = 0; a < files.size(); a++) {
            File file = (File) files.get(a);
            File encrypted = new File(file.getAbsolutePath() + ".enc");
            if (encrypted.exists()) {
                try {
                    if (!encrypted.delete()) {
                        encrypted.deleteOnExit();
                    }
                } catch (Throwable e) {
                    FileLog.m13e(e);
                }
                try {
                    File key = new File(getInternalCacheDir(), file.getName() + ".enc.key");
                    if (!key.delete()) {
                        key.deleteOnExit();
                    }
                } catch (Throwable e2) {
                    FileLog.m13e(e2);
                }
            } else if (file.exists()) {
                try {
                    if (!file.delete()) {
                        file.deleteOnExit();
                    }
                } catch (Throwable e22) {
                    FileLog.m13e(e22);
                }
            }
            try {
                File qFile = new File(file.getParentFile(), "q_" + file.getName());
                if (qFile.exists() && !qFile.delete()) {
                    qFile.deleteOnExit();
                }
            } catch (Throwable e222) {
                FileLog.m13e(e222);
            }
        }
        if (type == 2) {
            ImageLoader.getInstance().clearMemory();
        }
    }
}
