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
import org.telegram.tgnet.TLRPC$ChatPhoto;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$DocumentAttribute;
import org.telegram.tgnet.TLRPC$FileLocation;
import org.telegram.tgnet.TLRPC$InputEncryptedFile;
import org.telegram.tgnet.TLRPC$InputFile;
import org.telegram.tgnet.TLRPC$Message;
import org.telegram.tgnet.TLRPC$MessageMedia;
import org.telegram.tgnet.TLRPC$Photo;
import org.telegram.tgnet.TLRPC$PhotoSize;
import org.telegram.tgnet.TLRPC$TL_documentAttributeFilename;
import org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated;
import org.telegram.tgnet.TLRPC$TL_fileLocationUnavailable;
import org.telegram.tgnet.TLRPC$TL_messageMediaDocument;
import org.telegram.tgnet.TLRPC$TL_messageMediaInvoice;
import org.telegram.tgnet.TLRPC$TL_messageMediaPhoto;
import org.telegram.tgnet.TLRPC$TL_messageMediaWebPage;
import org.telegram.tgnet.TLRPC$TL_messageService;
import org.telegram.tgnet.TLRPC$TL_photo;
import org.telegram.tgnet.TLRPC$TL_photoPathSize;
import org.telegram.tgnet.TLRPC$TL_photoStrippedSize;
import org.telegram.tgnet.TLRPC$TL_secureFile;
import org.telegram.tgnet.TLRPC$TL_videoSize;
import org.telegram.tgnet.TLRPC$UserProfilePhoto;
import org.telegram.tgnet.TLRPC$WebDocument;
import org.telegram.tgnet.TLRPC$WebPage;

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
    public static final int MEDIA_DIR_VIDEO = 2;
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

        void fileDidLoaded(String str, File file, int i);

        void fileDidUploaded(String str, TLRPC$InputFile tLRPC$InputFile, TLRPC$InputEncryptedFile tLRPC$InputEncryptedFile, byte[] bArr, byte[] bArr2, long j);

        void fileLoadProgressChanged(FileLoadOperation fileLoadOperation, String str, long j, long j2);

        void fileUploadProgressChanged(FileUploadOperation fileUploadOperation, String str, long j, long j2, boolean z);
    }

    static /* synthetic */ int access$608(FileLoader fileLoader) {
        int i = fileLoader.currentUploadSmallOperationsCount;
        fileLoader.currentUploadSmallOperationsCount = i + 1;
        return i;
    }

    static /* synthetic */ int access$610(FileLoader fileLoader) {
        int i = fileLoader.currentUploadSmallOperationsCount;
        fileLoader.currentUploadSmallOperationsCount = i - 1;
        return i;
    }

    static /* synthetic */ int access$808(FileLoader fileLoader) {
        int i = fileLoader.currentUploadOperationsCount;
        fileLoader.currentUploadOperationsCount = i + 1;
        return i;
    }

    static /* synthetic */ int access$810(FileLoader fileLoader) {
        int i = fileLoader.currentUploadOperationsCount;
        fileLoader.currentUploadOperationsCount = i - 1;
        return i;
    }

    public static FileLoader getInstance(int i) {
        FileLoader fileLoader = Instance[i];
        if (fileLoader == null) {
            synchronized (FileLoader.class) {
                fileLoader = Instance[i];
                if (fileLoader == null) {
                    FileLoader[] fileLoaderArr = Instance;
                    FileLoader fileLoader2 = new FileLoader(i);
                    fileLoaderArr[i] = fileLoader2;
                    fileLoader = fileLoader2;
                }
            }
        }
        return fileLoader;
    }

    public FileLoader(int i) {
        super(i);
    }

    public static void setMediaDirs(SparseArray<File> sparseArray) {
        mediaDirs = sparseArray;
    }

    public static File checkDirectory(int i) {
        return mediaDirs.get(i);
    }

    public static File getDirectory(int i) {
        File file = mediaDirs.get(i);
        if (file == null && i != 4) {
            file = mediaDirs.get(4);
        }
        if (file != null) {
            try {
                if (!file.isDirectory()) {
                    file.mkdirs();
                }
            } catch (Exception unused) {
            }
        }
        return file;
    }

    public int getFileReference(Object obj) {
        int i = this.lastReferenceId;
        this.lastReferenceId = i + 1;
        this.parentObjectReferences.put(Integer.valueOf(i), obj);
        return i;
    }

    public Object getParentObject(int i) {
        return this.parentObjectReferences.get(Integer.valueOf(i));
    }

    /* renamed from: setLoadingVideoInternal */
    public void lambda$setLoadingVideo$0(TLRPC$Document tLRPC$Document, boolean z) {
        String attachFileName = getAttachFileName(tLRPC$Document);
        StringBuilder sb = new StringBuilder();
        sb.append(attachFileName);
        sb.append(z ? "p" : "");
        this.loadingVideos.put(sb.toString(), Boolean.TRUE);
        getNotificationCenter().postNotificationName(NotificationCenter.videoLoadingStateChanged, attachFileName);
    }

    public void setLoadingVideo(TLRPC$Document tLRPC$Document, boolean z, boolean z2) {
        if (tLRPC$Document != null) {
            if (z2) {
                AndroidUtilities.runOnUIThread(new FileLoader$$ExternalSyntheticLambda6(this, tLRPC$Document, z));
            } else {
                lambda$setLoadingVideo$0(tLRPC$Document, z);
            }
        }
    }

    public void setLoadingVideoForPlayer(TLRPC$Document tLRPC$Document, boolean z) {
        if (tLRPC$Document != null) {
            String attachFileName = getAttachFileName(tLRPC$Document);
            HashMap<String, Boolean> hashMap = this.loadingVideos;
            StringBuilder sb = new StringBuilder();
            sb.append(attachFileName);
            String str = "";
            sb.append(z ? str : "p");
            if (hashMap.containsKey(sb.toString())) {
                HashMap<String, Boolean> hashMap2 = this.loadingVideos;
                StringBuilder sb2 = new StringBuilder();
                sb2.append(attachFileName);
                if (z) {
                    str = "p";
                }
                sb2.append(str);
                hashMap2.put(sb2.toString(), Boolean.TRUE);
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: removeLoadingVideoInternal */
    public void lambda$removeLoadingVideo$1(TLRPC$Document tLRPC$Document, boolean z) {
        String attachFileName = getAttachFileName(tLRPC$Document);
        StringBuilder sb = new StringBuilder();
        sb.append(attachFileName);
        sb.append(z ? "p" : "");
        if (this.loadingVideos.remove(sb.toString()) != null) {
            getNotificationCenter().postNotificationName(NotificationCenter.videoLoadingStateChanged, attachFileName);
        }
    }

    public void removeLoadingVideo(TLRPC$Document tLRPC$Document, boolean z, boolean z2) {
        if (tLRPC$Document != null) {
            if (z2) {
                AndroidUtilities.runOnUIThread(new FileLoader$$ExternalSyntheticLambda7(this, tLRPC$Document, z));
            } else {
                lambda$removeLoadingVideo$1(tLRPC$Document, z);
            }
        }
    }

    public boolean isLoadingVideo(TLRPC$Document tLRPC$Document, boolean z) {
        if (tLRPC$Document != null) {
            HashMap<String, Boolean> hashMap = this.loadingVideos;
            StringBuilder sb = new StringBuilder();
            sb.append(getAttachFileName(tLRPC$Document));
            sb.append(z ? "p" : "");
            if (hashMap.containsKey(sb.toString())) {
                return true;
            }
        }
        return false;
    }

    public boolean isLoadingVideoAny(TLRPC$Document tLRPC$Document) {
        return isLoadingVideo(tLRPC$Document, false) || isLoadingVideo(tLRPC$Document, true);
    }

    public void cancelFileUpload(String str, boolean z) {
        fileLoaderQueue.postRunnable(new FileLoader$$ExternalSyntheticLambda10(this, z, str));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$cancelFileUpload$2(boolean z, String str) {
        FileUploadOperation fileUploadOperation;
        if (!z) {
            fileUploadOperation = this.uploadOperationPaths.get(str);
        } else {
            fileUploadOperation = this.uploadOperationPathsEnc.get(str);
        }
        this.uploadSizes.remove(str);
        if (fileUploadOperation != null) {
            this.uploadOperationPathsEnc.remove(str);
            this.uploadOperationQueue.remove(fileUploadOperation);
            this.uploadSmallOperationQueue.remove(fileUploadOperation);
            fileUploadOperation.cancel();
        }
    }

    public void checkUploadNewDataAvailable(String str, boolean z, long j, long j2) {
        fileLoaderQueue.postRunnable(new FileLoader$$ExternalSyntheticLambda12(this, z, str, j, j2));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$checkUploadNewDataAvailable$3(boolean z, String str, long j, long j2) {
        FileUploadOperation fileUploadOperation;
        if (z) {
            fileUploadOperation = this.uploadOperationPathsEnc.get(str);
        } else {
            fileUploadOperation = this.uploadOperationPaths.get(str);
        }
        if (fileUploadOperation != null) {
            fileUploadOperation.checkNewDataAvailable(j, j2);
        } else if (j2 != 0) {
            this.uploadSizes.put(str, Long.valueOf(j2));
        }
    }

    public void onNetworkChanged(boolean z) {
        fileLoaderQueue.postRunnable(new FileLoader$$ExternalSyntheticLambda9(this, z));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onNetworkChanged$4(boolean z) {
        for (Map.Entry<String, FileUploadOperation> value : this.uploadOperationPaths.entrySet()) {
            ((FileUploadOperation) value.getValue()).onNetworkChanged(z);
        }
        for (Map.Entry<String, FileUploadOperation> value2 : this.uploadOperationPathsEnc.entrySet()) {
            ((FileUploadOperation) value2.getValue()).onNetworkChanged(z);
        }
    }

    public void uploadFile(String str, boolean z, boolean z2, int i) {
        uploadFile(str, z, z2, 0, i, false);
    }

    public void uploadFile(String str, boolean z, boolean z2, int i, int i2, boolean z3) {
        if (str != null) {
            fileLoaderQueue.postRunnable(new FileLoader$$ExternalSyntheticLambda11(this, z, str, i, i2, z3, z2));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$uploadFile$5(final boolean z, final String str, int i, int i2, boolean z2, boolean z3) {
        int i3;
        boolean z4 = z;
        String str2 = str;
        int i4 = i;
        final boolean z5 = z3;
        if (z4) {
            if (this.uploadOperationPathsEnc.containsKey(str)) {
                return;
            }
        } else if (this.uploadOperationPaths.containsKey(str)) {
            return;
        }
        if (i4 == 0 || this.uploadSizes.get(str) == null) {
            i3 = i4;
        } else {
            this.uploadSizes.remove(str);
            i3 = 0;
        }
        FileUploadOperation fileUploadOperation = new FileUploadOperation(this.currentAccount, str, z, i3, i2);
        FileLoaderDelegate fileLoaderDelegate = this.delegate;
        if (!(fileLoaderDelegate == null || i4 == 0)) {
            fileLoaderDelegate.fileUploadProgressChanged(fileUploadOperation, str, 0, (long) i4, z);
        }
        if (z4) {
            this.uploadOperationPathsEnc.put(str, fileUploadOperation);
        } else {
            this.uploadOperationPaths.put(str, fileUploadOperation);
        }
        if (z2) {
            fileUploadOperation.setForceSmallFile();
        }
        fileUploadOperation.setDelegate(new FileUploadOperation.FileUploadOperationDelegate() {
            public void didFinishUploadingFile(FileUploadOperation fileUploadOperation, TLRPC$InputFile tLRPC$InputFile, TLRPC$InputEncryptedFile tLRPC$InputEncryptedFile, byte[] bArr, byte[] bArr2) {
                FileLoader.fileLoaderQueue.postRunnable(new FileLoader$1$$ExternalSyntheticLambda1(this, z, str, z5, tLRPC$InputFile, tLRPC$InputEncryptedFile, bArr, bArr2, fileUploadOperation));
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$didFinishUploadingFile$0(boolean z, String str, boolean z2, TLRPC$InputFile tLRPC$InputFile, TLRPC$InputEncryptedFile tLRPC$InputEncryptedFile, byte[] bArr, byte[] bArr2, FileUploadOperation fileUploadOperation) {
                FileUploadOperation fileUploadOperation2;
                FileUploadOperation fileUploadOperation3;
                String str2 = str;
                if (z) {
                    FileLoader.this.uploadOperationPathsEnc.remove(str);
                } else {
                    FileLoader.this.uploadOperationPaths.remove(str);
                }
                if (z2) {
                    FileLoader.access$610(FileLoader.this);
                    if (FileLoader.this.currentUploadSmallOperationsCount < 1 && (fileUploadOperation3 = (FileUploadOperation) FileLoader.this.uploadSmallOperationQueue.poll()) != null) {
                        FileLoader.access$608(FileLoader.this);
                        fileUploadOperation3.start();
                    }
                } else {
                    FileLoader.access$810(FileLoader.this);
                    if (FileLoader.this.currentUploadOperationsCount < 1 && (fileUploadOperation2 = (FileUploadOperation) FileLoader.this.uploadOperationQueue.poll()) != null) {
                        FileLoader.access$808(FileLoader.this);
                        fileUploadOperation2.start();
                    }
                }
                if (FileLoader.this.delegate != null) {
                    FileLoader.this.delegate.fileDidUploaded(str, tLRPC$InputFile, tLRPC$InputEncryptedFile, bArr, bArr2, fileUploadOperation.getTotalFileSize());
                }
            }

            public void didFailedUploadingFile(FileUploadOperation fileUploadOperation) {
                FileLoader.fileLoaderQueue.postRunnable(new FileLoader$1$$ExternalSyntheticLambda0(this, z, str, z5));
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$didFailedUploadingFile$1(boolean z, String str, boolean z2) {
                FileUploadOperation fileUploadOperation;
                FileUploadOperation fileUploadOperation2;
                if (z) {
                    FileLoader.this.uploadOperationPathsEnc.remove(str);
                } else {
                    FileLoader.this.uploadOperationPaths.remove(str);
                }
                if (FileLoader.this.delegate != null) {
                    FileLoader.this.delegate.fileDidFailedUpload(str, z);
                }
                if (z2) {
                    FileLoader.access$610(FileLoader.this);
                    if (FileLoader.this.currentUploadSmallOperationsCount < 1 && (fileUploadOperation2 = (FileUploadOperation) FileLoader.this.uploadSmallOperationQueue.poll()) != null) {
                        FileLoader.access$608(FileLoader.this);
                        fileUploadOperation2.start();
                        return;
                    }
                    return;
                }
                FileLoader.access$810(FileLoader.this);
                if (FileLoader.this.currentUploadOperationsCount < 1 && (fileUploadOperation = (FileUploadOperation) FileLoader.this.uploadOperationQueue.poll()) != null) {
                    FileLoader.access$808(FileLoader.this);
                    fileUploadOperation.start();
                }
            }

            public void didChangedUploadProgress(FileUploadOperation fileUploadOperation, long j, long j2) {
                if (FileLoader.this.delegate != null) {
                    FileLoader.this.delegate.fileUploadProgressChanged(fileUploadOperation, str, j, j2, z);
                }
            }
        });
        if (z5) {
            int i5 = this.currentUploadSmallOperationsCount;
            if (i5 < 1) {
                this.currentUploadSmallOperationsCount = i5 + 1;
                fileUploadOperation.start();
                return;
            }
            this.uploadSmallOperationQueue.add(fileUploadOperation);
            return;
        }
        int i6 = this.currentUploadOperationsCount;
        if (i6 < 1) {
            this.currentUploadOperationsCount = i6 + 1;
            fileUploadOperation.start();
            return;
        }
        this.uploadOperationQueue.add(fileUploadOperation);
    }

    private LinkedList<FileLoadOperation> getLoadOperationQueue(int i, int i2) {
        SparseArray<LinkedList<FileLoadOperation>> sparseArray;
        if (i2 == 2) {
            sparseArray = this.audioLoadOperationQueues;
        } else if (i2 == 1) {
            sparseArray = this.imageLoadOperationQueues;
        } else {
            sparseArray = this.fileLoadOperationQueues;
        }
        LinkedList<FileLoadOperation> linkedList = sparseArray.get(i);
        if (linkedList != null) {
            return linkedList;
        }
        LinkedList<FileLoadOperation> linkedList2 = new LinkedList<>();
        sparseArray.put(i, linkedList2);
        return linkedList2;
    }

    private SparseIntArray getLoadOperationCount(int i) {
        if (i == 2) {
            return this.audioLoadOperationsCount;
        }
        if (i == 1) {
            return this.imageLoadOperationsCount;
        }
        return this.fileLoadOperationsCount;
    }

    public void setForceStreamLoadingFile(TLRPC$FileLocation tLRPC$FileLocation, String str) {
        if (tLRPC$FileLocation != null) {
            fileLoaderQueue.postRunnable(new FileLoader$$ExternalSyntheticLambda8(this, tLRPC$FileLocation, str));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setForceStreamLoadingFile$6(TLRPC$FileLocation tLRPC$FileLocation, String str) {
        String attachFileName = getAttachFileName(tLRPC$FileLocation, str);
        this.forceLoadingFile = attachFileName;
        FileLoadOperation fileLoadOperation = this.loadOperationPaths.get(attachFileName);
        if (fileLoadOperation != null) {
            if (fileLoadOperation.isPreloadVideoOperation()) {
                fileLoadOperation.setIsPreloadVideoOperation(false);
            }
            fileLoadOperation.setForceRequest(true);
            int datacenterId = fileLoadOperation.getDatacenterId();
            int queueType = fileLoadOperation.getQueueType();
            LinkedList<FileLoadOperation> loadOperationQueue = getLoadOperationQueue(datacenterId, queueType);
            SparseIntArray loadOperationCount = getLoadOperationCount(queueType);
            int indexOf = loadOperationQueue.indexOf(fileLoadOperation);
            if (indexOf >= 0) {
                loadOperationQueue.remove(indexOf);
                if (fileLoadOperation.start()) {
                    loadOperationCount.put(datacenterId, loadOperationCount.get(datacenterId) + 1);
                }
                if (queueType == 0 && fileLoadOperation.wasStarted() && !this.activeFileLoadOperation.contains(fileLoadOperation)) {
                    pauseCurrentFileLoadOperations(fileLoadOperation);
                    this.activeFileLoadOperation.add(fileLoadOperation);
                    return;
                }
                return;
            }
            pauseCurrentFileLoadOperations(fileLoadOperation);
            fileLoadOperation.start();
            if (queueType == 0 && !this.activeFileLoadOperation.contains(fileLoadOperation)) {
                this.activeFileLoadOperation.add(fileLoadOperation);
            }
        }
    }

    public void cancelLoadFile(TLRPC$Document tLRPC$Document) {
        cancelLoadFile(tLRPC$Document, false);
    }

    public void cancelLoadFile(TLRPC$Document tLRPC$Document, boolean z) {
        cancelLoadFile(tLRPC$Document, (SecureDocument) null, (WebFile) null, (TLRPC$FileLocation) null, (String) null, (String) null, z);
    }

    public void cancelLoadFile(SecureDocument secureDocument) {
        cancelLoadFile((TLRPC$Document) null, secureDocument, (WebFile) null, (TLRPC$FileLocation) null, (String) null, (String) null, false);
    }

    public void cancelLoadFile(WebFile webFile) {
        cancelLoadFile((TLRPC$Document) null, (SecureDocument) null, webFile, (TLRPC$FileLocation) null, (String) null, (String) null, false);
    }

    public void cancelLoadFile(TLRPC$PhotoSize tLRPC$PhotoSize) {
        cancelLoadFile(tLRPC$PhotoSize, false);
    }

    public void cancelLoadFile(TLRPC$PhotoSize tLRPC$PhotoSize, boolean z) {
        cancelLoadFile((TLRPC$Document) null, (SecureDocument) null, (WebFile) null, tLRPC$PhotoSize.location, (String) null, (String) null, z);
    }

    public void cancelLoadFile(TLRPC$FileLocation tLRPC$FileLocation, String str) {
        cancelLoadFile(tLRPC$FileLocation, str, false);
    }

    public void cancelLoadFile(TLRPC$FileLocation tLRPC$FileLocation, String str, boolean z) {
        cancelLoadFile((TLRPC$Document) null, (SecureDocument) null, (WebFile) null, tLRPC$FileLocation, str, (String) null, z);
    }

    public void cancelLoadFile(String str) {
        cancelLoadFile((TLRPC$Document) null, (SecureDocument) null, (WebFile) null, (TLRPC$FileLocation) null, (String) null, str, true);
    }

    public void cancelLoadFiles(ArrayList<String> arrayList) {
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            cancelLoadFile((TLRPC$Document) null, (SecureDocument) null, (WebFile) null, (TLRPC$FileLocation) null, (String) null, arrayList.get(i), true);
        }
    }

    private void cancelLoadFile(TLRPC$Document tLRPC$Document, SecureDocument secureDocument, WebFile webFile, TLRPC$FileLocation tLRPC$FileLocation, String str, String str2, boolean z) {
        if (tLRPC$FileLocation != null || tLRPC$Document != null || webFile != null || secureDocument != null || !TextUtils.isEmpty(str2)) {
            if (tLRPC$FileLocation != null) {
                str2 = getAttachFileName(tLRPC$FileLocation, str);
            } else if (tLRPC$Document != null) {
                str2 = getAttachFileName(tLRPC$Document);
            } else if (secureDocument != null) {
                str2 = getAttachFileName(secureDocument);
            } else if (webFile != null) {
                str2 = getAttachFileName(webFile);
            }
            boolean z2 = this.loadOperationPathsUI.remove(str2) != null;
            fileLoaderQueue.postRunnable(new FileLoader$$ExternalSyntheticLambda3(this, str2, z));
            if (z2 && tLRPC$Document != null) {
                AndroidUtilities.runOnUIThread(new FileLoader$$ExternalSyntheticLambda1(this));
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$cancelLoadFile$7(String str, boolean z) {
        FileLoadOperation remove = this.loadOperationPaths.remove(str);
        if (remove != null) {
            int queueType = remove.getQueueType();
            int datacenterId = remove.getDatacenterId();
            if (!getLoadOperationQueue(datacenterId, queueType).remove(remove)) {
                SparseIntArray loadOperationCount = getLoadOperationCount(queueType);
                loadOperationCount.put(datacenterId, loadOperationCount.get(datacenterId) - 1);
            }
            if (queueType == 0) {
                this.activeFileLoadOperation.remove(remove);
            }
            remove.cancel(z);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$cancelLoadFile$8() {
        getNotificationCenter().postNotificationName(NotificationCenter.onDownloadingFilesChanged, new Object[0]);
    }

    public boolean isLoadingFile(String str) {
        return str != null && this.loadOperationPathsUI.containsKey(str);
    }

    public float getBufferedProgressFromPosition(float f, String str) {
        FileLoadOperation fileLoadOperation;
        if (!TextUtils.isEmpty(str) && (fileLoadOperation = this.loadOperationPaths.get(str)) != null) {
            return fileLoadOperation.getDownloadedLengthFromOffset(f);
        }
        return 0.0f;
    }

    public void loadFile(ImageLocation imageLocation, Object obj, String str, int i, int i2) {
        ImageLocation imageLocation2 = imageLocation;
        if (imageLocation2 != null) {
            loadFile(imageLocation2.document, imageLocation2.secureDocument, imageLocation2.webFile, imageLocation2.location, imageLocation, obj, str, imageLocation.getSize(), i, (i2 != 0 || (!imageLocation.isEncrypted() && (imageLocation2.photoSize == null || imageLocation.getSize() != 0))) ? i2 : 1);
        }
    }

    public void loadFile(SecureDocument secureDocument, int i) {
        if (secureDocument != null) {
            loadFile((TLRPC$Document) null, secureDocument, (WebFile) null, (TLRPC$TL_fileLocationToBeDeprecated) null, (ImageLocation) null, (Object) null, (String) null, 0, i, 1);
        }
    }

    public void loadFile(TLRPC$Document tLRPC$Document, Object obj, int i, int i2) {
        if (tLRPC$Document != null) {
            loadFile(tLRPC$Document, (SecureDocument) null, (WebFile) null, (TLRPC$TL_fileLocationToBeDeprecated) null, (ImageLocation) null, obj, (String) null, 0, i, (i2 != 0 || tLRPC$Document.key == null) ? i2 : 1);
        }
    }

    public void loadFile(WebFile webFile, int i, int i2) {
        loadFile((TLRPC$Document) null, (SecureDocument) null, webFile, (TLRPC$TL_fileLocationToBeDeprecated) null, (ImageLocation) null, (Object) null, (String) null, 0, i, i2);
    }

    private void pauseCurrentFileLoadOperations(FileLoadOperation fileLoadOperation) {
        int i = 0;
        while (i < this.activeFileLoadOperation.size()) {
            FileLoadOperation fileLoadOperation2 = this.activeFileLoadOperation.get(i);
            if (fileLoadOperation2 != fileLoadOperation && fileLoadOperation2.getDatacenterId() == fileLoadOperation.getDatacenterId() && !fileLoadOperation2.getFileName().equals(this.forceLoadingFile)) {
                this.activeFileLoadOperation.remove(fileLoadOperation2);
                i--;
                int datacenterId = fileLoadOperation2.getDatacenterId();
                int queueType = fileLoadOperation2.getQueueType();
                LinkedList<FileLoadOperation> loadOperationQueue = getLoadOperationQueue(datacenterId, queueType);
                SparseIntArray loadOperationCount = getLoadOperationCount(queueType);
                loadOperationQueue.add(0, fileLoadOperation2);
                if (fileLoadOperation2.wasStarted()) {
                    loadOperationCount.put(datacenterId, loadOperationCount.get(datacenterId) - 1);
                }
                fileLoadOperation2.pause();
            }
            i++;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:71:0x0126, code lost:
        if (org.telegram.messenger.MessageObject.isVideoDocument(r25) != false) goto L_0x0128;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:81:0x014a, code lost:
        if (org.telegram.messenger.MessageObject.isImageWebDocument(r27) != false) goto L_0x0111;
     */
    /* JADX WARNING: Removed duplicated region for block: B:103:0x017d  */
    /* JADX WARNING: Removed duplicated region for block: B:106:0x0197  */
    /* JADX WARNING: Removed duplicated region for block: B:109:0x01c2  */
    /* JADX WARNING: Removed duplicated region for block: B:120:0x01e2  */
    /* JADX WARNING: Removed duplicated region for block: B:147:0x0233  */
    /* JADX WARNING: Removed duplicated region for block: B:85:0x0153  */
    /* JADX WARNING: Removed duplicated region for block: B:86:0x0155  */
    /* JADX WARNING: Removed duplicated region for block: B:98:0x0171  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private org.telegram.messenger.FileLoadOperation loadFileInternal(org.telegram.tgnet.TLRPC$Document r25, org.telegram.messenger.SecureDocument r26, org.telegram.messenger.WebFile r27, org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated r28, org.telegram.messenger.ImageLocation r29, java.lang.Object r30, java.lang.String r31, int r32, int r33, org.telegram.messenger.FileLoadOperationStream r34, int r35, boolean r36, int r37) {
        /*
            r24 = this;
            r7 = r24
            r2 = r25
            r0 = r26
            r1 = r27
            r3 = r28
            r4 = r29
            r5 = r30
            r6 = r31
            r8 = r33
            r9 = r34
            r10 = r35
            r11 = r36
            r12 = r37
            r13 = 0
            if (r3 == 0) goto L_0x0022
            java.lang.String r14 = getAttachFileName(r3, r6)
            goto L_0x0038
        L_0x0022:
            if (r0 == 0) goto L_0x0029
            java.lang.String r14 = getAttachFileName(r26)
            goto L_0x0038
        L_0x0029:
            if (r2 == 0) goto L_0x0030
            java.lang.String r14 = getAttachFileName(r25)
            goto L_0x0038
        L_0x0030:
            if (r1 == 0) goto L_0x0037
            java.lang.String r14 = getAttachFileName(r27)
            goto L_0x0038
        L_0x0037:
            r14 = r13
        L_0x0038:
            if (r14 == 0) goto L_0x023a
            java.lang.String r15 = "-NUM"
            boolean r16 = r14.contains(r15)
            if (r16 == 0) goto L_0x0044
            goto L_0x023a
        L_0x0044:
            r13 = 10
            if (r12 == r13) goto L_0x005b
            boolean r16 = android.text.TextUtils.isEmpty(r14)
            if (r16 != 0) goto L_0x005b
            boolean r15 = r14.contains(r15)
            if (r15 != 0) goto L_0x005b
            j$.util.concurrent.ConcurrentHashMap<java.lang.String, java.lang.Boolean> r15 = r7.loadOperationPathsUI
            java.lang.Boolean r13 = java.lang.Boolean.TRUE
            r15.put(r14, r13)
        L_0x005b:
            if (r2 == 0) goto L_0x006f
            boolean r13 = r5 instanceof org.telegram.messenger.MessageObject
            if (r13 == 0) goto L_0x006f
            r13 = r5
            org.telegram.messenger.MessageObject r13 = (org.telegram.messenger.MessageObject) r13
            boolean r15 = r13.putInDownloadsStore
            if (r15 == 0) goto L_0x006f
            org.telegram.messenger.DownloadController r15 = r24.getDownloadController()
            r15.startDownloadFile(r2, r13)
        L_0x006f:
            j$.util.concurrent.ConcurrentHashMap<java.lang.String, org.telegram.messenger.FileLoadOperation> r13 = r7.loadOperationPaths
            java.lang.Object r13 = r13.get(r14)
            org.telegram.messenger.FileLoadOperation r13 = (org.telegram.messenger.FileLoadOperation) r13
            if (r13 == 0) goto L_0x00f1
            r15 = 10
            if (r12 == r15) goto L_0x0087
            boolean r0 = r13.isPreloadVideoOperation()
            if (r0 == 0) goto L_0x0087
            r0 = 0
            r13.setIsPreloadVideoOperation(r0)
        L_0x0087:
            if (r9 != 0) goto L_0x008b
            if (r8 <= 0) goto L_0x00ed
        L_0x008b:
            int r0 = r13.getDatacenterId()
            r1 = 1
            r13.setForceRequest(r1)
            int r2 = r13.getQueueType()
            java.util.LinkedList r3 = r7.getLoadOperationQueue(r0, r2)
            android.util.SparseIntArray r4 = r7.getLoadOperationCount(r2)
            int r5 = r3.indexOf(r13)
            if (r5 < 0) goto L_0x00d6
            r3.remove(r5)
            if (r9 == 0) goto L_0x00d1
            boolean r3 = r13.start(r9, r10, r11)
            if (r3 == 0) goto L_0x00b8
            int r3 = r4.get(r0)
            int r3 = r3 + r1
            r4.put(r0, r3)
        L_0x00b8:
            if (r2 != 0) goto L_0x00ed
            boolean r0 = r13.wasStarted()
            if (r0 == 0) goto L_0x00ed
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation> r0 = r7.activeFileLoadOperation
            boolean r0 = r0.contains(r13)
            if (r0 != 0) goto L_0x00ed
            r7.pauseCurrentFileLoadOperations(r13)
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation> r0 = r7.activeFileLoadOperation
            r0.add(r13)
            goto L_0x00ed
        L_0x00d1:
            r15 = 0
            r3.add(r15, r13)
            goto L_0x00ed
        L_0x00d6:
            if (r9 == 0) goto L_0x00db
            r7.pauseCurrentFileLoadOperations(r13)
        L_0x00db:
            r13.start(r9, r10, r11)
            if (r2 != 0) goto L_0x00ed
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation> r0 = r7.activeFileLoadOperation
            boolean r0 = r0.contains(r13)
            if (r0 != 0) goto L_0x00ed
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation> r0 = r7.activeFileLoadOperation
            r0.add(r13)
        L_0x00ed:
            r13.updateProgress()
            return r13
        L_0x00f1:
            r15 = 0
            r21 = 4
            java.io.File r20 = getDirectory(r21)
            r22 = 3
            r16 = r13
            if (r0 == 0) goto L_0x0107
            org.telegram.messenger.FileLoadOperation r6 = new org.telegram.messenger.FileLoadOperation
            r6.<init>(r0)
            r13 = r6
        L_0x0104:
            r6 = 3
        L_0x0105:
            r15 = 1
            goto L_0x0151
        L_0x0107:
            if (r3 == 0) goto L_0x0113
            org.telegram.messenger.FileLoadOperation r15 = new org.telegram.messenger.FileLoadOperation
            r13 = r32
            r15.<init>(r4, r5, r6, r13)
            r13 = r15
        L_0x0111:
            r6 = 0
            goto L_0x0105
        L_0x0113:
            if (r2 == 0) goto L_0x012a
            org.telegram.messenger.FileLoadOperation r13 = new org.telegram.messenger.FileLoadOperation
            r13.<init>((org.telegram.tgnet.TLRPC$Document) r2, (java.lang.Object) r5)
            boolean r6 = org.telegram.messenger.MessageObject.isVoiceDocument(r25)
            if (r6 == 0) goto L_0x0122
        L_0x0120:
            r6 = 1
            goto L_0x0105
        L_0x0122:
            boolean r6 = org.telegram.messenger.MessageObject.isVideoDocument(r25)
            if (r6 == 0) goto L_0x0104
        L_0x0128:
            r6 = 2
            goto L_0x0105
        L_0x012a:
            if (r1 == 0) goto L_0x014d
            org.telegram.messenger.FileLoadOperation r13 = new org.telegram.messenger.FileLoadOperation
            int r6 = r7.currentAccount
            r13.<init>((int) r6, (org.telegram.messenger.WebFile) r1)
            org.telegram.tgnet.TLRPC$InputWebFileLocation r6 = r1.location
            if (r6 == 0) goto L_0x0138
            goto L_0x014f
        L_0x0138:
            boolean r6 = org.telegram.messenger.MessageObject.isVoiceWebDocument(r27)
            if (r6 == 0) goto L_0x013f
            goto L_0x0120
        L_0x013f:
            boolean r6 = org.telegram.messenger.MessageObject.isVideoWebDocument(r27)
            if (r6 == 0) goto L_0x0146
            goto L_0x0128
        L_0x0146:
            boolean r6 = org.telegram.messenger.MessageObject.isImageWebDocument(r27)
            if (r6 == 0) goto L_0x0104
            goto L_0x0111
        L_0x014d:
            r13 = r16
        L_0x014f:
            r6 = 4
            goto L_0x0105
        L_0x0151:
            if (r6 != r15) goto L_0x0155
            r4 = 2
            goto L_0x016a
        L_0x0155:
            if (r0 != 0) goto L_0x0169
            if (r3 == 0) goto L_0x0160
            if (r4 == 0) goto L_0x0169
            int r0 = r4.imageType
            r3 = 2
            if (r0 != r3) goto L_0x0169
        L_0x0160:
            boolean r0 = org.telegram.messenger.MessageObject.isImageWebDocument(r27)
            if (r0 == 0) goto L_0x0167
            goto L_0x0169
        L_0x0167:
            r4 = 0
            goto L_0x016a
        L_0x0169:
            r4 = 1
        L_0x016a:
            if (r12 == 0) goto L_0x017d
            r0 = 10
            if (r12 != r0) goto L_0x0171
            goto L_0x017d
        L_0x0171:
            r0 = 2
            if (r12 != r0) goto L_0x0179
            r0 = 1
            r13.setEncryptFile(r0)
            goto L_0x017a
        L_0x0179:
            r0 = 1
        L_0x017a:
            r19 = r20
            goto L_0x0184
        L_0x017d:
            r0 = 1
            java.io.File r1 = getDirectory(r6)
            r19 = r1
        L_0x0184:
            int r1 = r7.currentAccount
            r3 = 1
            r23 = 0
            r15 = r13
            r16 = r1
            r17 = r14
            r18 = r4
            r15.setPaths(r16, r17, r18, r19, r20)
            r0 = 10
            if (r12 != r0) goto L_0x019a
            r13.setIsPreloadVideoOperation(r3)
        L_0x019a:
            org.telegram.messenger.FileLoader$2 r12 = new org.telegram.messenger.FileLoader$2
            r0 = r12
            r1 = r24
            r2 = r25
            r15 = 1
            r3 = r30
            r5 = r4
            r4 = r14
            r25 = r5
            r5 = r6
            r6 = r25
            r0.<init>(r2, r3, r4, r5, r6)
            r13.setDelegate(r12)
            int r0 = r13.getDatacenterId()
            j$.util.concurrent.ConcurrentHashMap<java.lang.String, org.telegram.messenger.FileLoadOperation> r1 = r7.loadOperationPaths
            r1.put(r14, r13)
            r13.setPriority(r8)
            r1 = r25
            r2 = 2
            if (r1 != r2) goto L_0x01e2
            if (r8 <= 0) goto L_0x01c6
            r2 = 3
            goto L_0x01c7
        L_0x01c6:
            r2 = 1
        L_0x01c7:
            android.util.SparseIntArray r3 = r7.audioLoadOperationsCount
            int r3 = r3.get(r0)
            if (r9 != 0) goto L_0x01d1
            if (r3 >= r2) goto L_0x01d3
        L_0x01d1:
            r23 = 1
        L_0x01d3:
            if (r23 == 0) goto L_0x0231
            boolean r2 = r13.start(r9, r10, r11)
            if (r2 == 0) goto L_0x0231
            android.util.SparseIntArray r2 = r7.audioLoadOperationsCount
            int r3 = r3 + r15
            r2.put(r0, r3)
            goto L_0x0231
        L_0x01e2:
            if (r1 != r15) goto L_0x0202
            if (r8 <= 0) goto L_0x01e7
            r2 = 6
        L_0x01e7:
            android.util.SparseIntArray r3 = r7.imageLoadOperationsCount
            int r3 = r3.get(r0)
            if (r9 != 0) goto L_0x01f1
            if (r3 >= r2) goto L_0x01f3
        L_0x01f1:
            r23 = 1
        L_0x01f3:
            if (r23 == 0) goto L_0x0231
            boolean r2 = r13.start(r9, r10, r11)
            if (r2 == 0) goto L_0x0231
            android.util.SparseIntArray r2 = r7.imageLoadOperationsCount
            int r3 = r3 + r15
            r2.put(r0, r3)
            goto L_0x0231
        L_0x0202:
            if (r8 <= 0) goto L_0x0206
            r2 = 4
            goto L_0x0207
        L_0x0206:
            r2 = 1
        L_0x0207:
            android.util.SparseIntArray r3 = r7.fileLoadOperationsCount
            int r3 = r3.get(r0)
            if (r9 != 0) goto L_0x0211
            if (r3 >= r2) goto L_0x0213
        L_0x0211:
            r23 = 1
        L_0x0213:
            if (r23 == 0) goto L_0x0231
            boolean r2 = r13.start(r9, r10, r11)
            if (r2 == 0) goto L_0x0226
            android.util.SparseIntArray r2 = r7.fileLoadOperationsCount
            int r3 = r3 + r15
            r2.put(r0, r3)
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation> r2 = r7.activeFileLoadOperation
            r2.add(r13)
        L_0x0226:
            boolean r2 = r13.wasStarted()
            if (r2 == 0) goto L_0x0231
            if (r9 == 0) goto L_0x0231
            r7.pauseCurrentFileLoadOperations(r13)
        L_0x0231:
            if (r23 != 0) goto L_0x023a
            java.util.LinkedList r0 = r7.getLoadOperationQueue(r0, r1)
            r7.addOperationToQueue(r13, r0)
        L_0x023a:
            return r13
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.FileLoader.loadFileInternal(org.telegram.tgnet.TLRPC$Document, org.telegram.messenger.SecureDocument, org.telegram.messenger.WebFile, org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated, org.telegram.messenger.ImageLocation, java.lang.Object, java.lang.String, int, int, org.telegram.messenger.FileLoadOperationStream, int, boolean, int):org.telegram.messenger.FileLoadOperation");
    }

    private void addOperationToQueue(FileLoadOperation fileLoadOperation, LinkedList<FileLoadOperation> linkedList) {
        int priority = fileLoadOperation.getPriority();
        if (priority > 0) {
            int size = linkedList.size();
            int i = 0;
            int size2 = linkedList.size();
            while (true) {
                if (i >= size2) {
                    break;
                } else if (linkedList.get(i).getPriority() < priority) {
                    size = i;
                    break;
                } else {
                    i++;
                }
            }
            linkedList.add(size, fileLoadOperation);
            return;
        }
        linkedList.add(fileLoadOperation);
    }

    private void loadFile(TLRPC$Document tLRPC$Document, SecureDocument secureDocument, WebFile webFile, TLRPC$TL_fileLocationToBeDeprecated tLRPC$TL_fileLocationToBeDeprecated, ImageLocation imageLocation, Object obj, String str, int i, int i2, int i3) {
        String str2;
        TLRPC$TL_fileLocationToBeDeprecated tLRPC$TL_fileLocationToBeDeprecated2 = tLRPC$TL_fileLocationToBeDeprecated;
        if (tLRPC$TL_fileLocationToBeDeprecated2 != null) {
            str2 = getAttachFileName(tLRPC$TL_fileLocationToBeDeprecated2, str);
        } else {
            String str3 = str;
            if (tLRPC$Document != null) {
                str2 = getAttachFileName(tLRPC$Document);
            } else {
                str2 = webFile != null ? getAttachFileName(webFile) : null;
            }
        }
        if (i3 != 10 && !TextUtils.isEmpty(str2) && !str2.contains("-NUM")) {
            this.loadOperationPathsUI.put(str2, Boolean.TRUE);
        }
        fileLoaderQueue.postRunnable(new FileLoader$$ExternalSyntheticLambda5(this, tLRPC$Document, secureDocument, webFile, tLRPC$TL_fileLocationToBeDeprecated, imageLocation, obj, str, i, i2, i3));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadFile$9(TLRPC$Document tLRPC$Document, SecureDocument secureDocument, WebFile webFile, TLRPC$TL_fileLocationToBeDeprecated tLRPC$TL_fileLocationToBeDeprecated, ImageLocation imageLocation, Object obj, String str, int i, int i2, int i3) {
        loadFileInternal(tLRPC$Document, secureDocument, webFile, tLRPC$TL_fileLocationToBeDeprecated, imageLocation, obj, str, i, i2, (FileLoadOperationStream) null, 0, false, i3);
    }

    /* access modifiers changed from: protected */
    public FileLoadOperation loadStreamFile(FileLoadOperationStream fileLoadOperationStream, TLRPC$Document tLRPC$Document, ImageLocation imageLocation, Object obj, int i, boolean z) {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        FileLoadOperation[] fileLoadOperationArr = new FileLoadOperation[1];
        fileLoaderQueue.postRunnable(new FileLoader$$ExternalSyntheticLambda13(this, fileLoadOperationArr, tLRPC$Document, imageLocation, obj, fileLoadOperationStream, i, z, countDownLatch));
        try {
            countDownLatch.await();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        return fileLoadOperationArr[0];
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadStreamFile$10(FileLoadOperation[] fileLoadOperationArr, TLRPC$Document tLRPC$Document, ImageLocation imageLocation, Object obj, FileLoadOperationStream fileLoadOperationStream, int i, boolean z, CountDownLatch countDownLatch) {
        ImageLocation imageLocation2 = imageLocation;
        String str = null;
        TLRPC$TL_fileLocationToBeDeprecated tLRPC$TL_fileLocationToBeDeprecated = (tLRPC$Document != null || imageLocation2 == null) ? null : imageLocation2.location;
        if (tLRPC$Document == null && imageLocation2 != null) {
            str = "mp4";
        }
        fileLoadOperationArr[0] = loadFileInternal(tLRPC$Document, (SecureDocument) null, (WebFile) null, tLRPC$TL_fileLocationToBeDeprecated, imageLocation, obj, str, (tLRPC$Document != null || imageLocation2 == null) ? 0 : imageLocation2.currentSize, 1, fileLoadOperationStream, i, z, tLRPC$Document == null ? 1 : 0);
        countDownLatch.countDown();
    }

    /* access modifiers changed from: private */
    public void checkDownloadQueue(int i, int i2, String str) {
        fileLoaderQueue.postRunnable(new FileLoader$$ExternalSyntheticLambda2(this, str, i, i2));
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0042, code lost:
        if (r7.getPriority() != 0) goto L_0x0058;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0056, code lost:
        if (r7.isForceRequest() != false) goto L_0x0058;
     */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x005a  */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x007d A[SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$checkDownloadQueue$11(java.lang.String r7, int r8, int r9) {
        /*
            r6 = this;
            j$.util.concurrent.ConcurrentHashMap<java.lang.String, org.telegram.messenger.FileLoadOperation> r0 = r6.loadOperationPaths
            java.lang.Object r7 = r0.remove(r7)
            org.telegram.messenger.FileLoadOperation r7 = (org.telegram.messenger.FileLoadOperation) r7
            java.util.LinkedList r0 = r6.getLoadOperationQueue(r8, r9)
            android.util.SparseIntArray r1 = r6.getLoadOperationCount(r9)
            int r2 = r1.get(r8)
            if (r7 == 0) goto L_0x002c
            boolean r3 = r7.wasStarted()
            if (r3 == 0) goto L_0x0022
            int r2 = r2 + -1
            r1.put(r8, r2)
            goto L_0x0025
        L_0x0022:
            r0.remove(r7)
        L_0x0025:
            if (r9 != 0) goto L_0x002c
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation> r3 = r6.activeFileLoadOperation
            r3.remove(r7)
        L_0x002c:
            boolean r7 = r0.isEmpty()
            if (r7 != 0) goto L_0x007d
            r7 = 0
            java.lang.Object r7 = r0.get(r7)
            org.telegram.messenger.FileLoadOperation r7 = (org.telegram.messenger.FileLoadOperation) r7
            r3 = 3
            r4 = 2
            r5 = 1
            if (r9 != r4) goto L_0x0047
            int r7 = r7.getPriority()
            if (r7 == 0) goto L_0x0045
            goto L_0x0058
        L_0x0045:
            r3 = 1
            goto L_0x0058
        L_0x0047:
            if (r9 != r5) goto L_0x0052
            int r7 = r7.getPriority()
            if (r7 == 0) goto L_0x0050
            r4 = 6
        L_0x0050:
            r3 = r4
            goto L_0x0058
        L_0x0052:
            boolean r7 = r7.isForceRequest()
            if (r7 == 0) goto L_0x0045
        L_0x0058:
            if (r2 >= r3) goto L_0x007d
            java.lang.Object r7 = r0.poll()
            org.telegram.messenger.FileLoadOperation r7 = (org.telegram.messenger.FileLoadOperation) r7
            if (r7 == 0) goto L_0x002c
            boolean r3 = r7.start()
            if (r3 == 0) goto L_0x002c
            int r2 = r2 + 1
            r1.put(r8, r2)
            if (r9 != 0) goto L_0x002c
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation> r3 = r6.activeFileLoadOperation
            boolean r3 = r3.contains(r7)
            if (r3 != 0) goto L_0x002c
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation> r3 = r6.activeFileLoadOperation
            r3.add(r7)
            goto L_0x002c
        L_0x007d:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.FileLoader.lambda$checkDownloadQueue$11(java.lang.String, int, int):void");
    }

    public void setDelegate(FileLoaderDelegate fileLoaderDelegate) {
        this.delegate = fileLoaderDelegate;
    }

    public static String getMessageFileName(TLRPC$Message tLRPC$Message) {
        TLRPC$WebDocument tLRPC$WebDocument;
        TLRPC$PhotoSize closestPhotoSizeWithSize;
        TLRPC$PhotoSize closestPhotoSizeWithSize2;
        TLRPC$PhotoSize closestPhotoSizeWithSize3;
        if (tLRPC$Message == null) {
            return "";
        }
        if (tLRPC$Message instanceof TLRPC$TL_messageService) {
            TLRPC$Photo tLRPC$Photo = tLRPC$Message.action.photo;
            if (tLRPC$Photo != null) {
                ArrayList<TLRPC$PhotoSize> arrayList = tLRPC$Photo.sizes;
                if (arrayList.size() > 0 && (closestPhotoSizeWithSize3 = getClosestPhotoSizeWithSize(arrayList, AndroidUtilities.getPhotoSize())) != null) {
                    return getAttachFileName(closestPhotoSizeWithSize3);
                }
            }
        } else {
            TLRPC$MessageMedia tLRPC$MessageMedia = tLRPC$Message.media;
            if (tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaDocument) {
                return getAttachFileName(tLRPC$MessageMedia.document);
            }
            if (tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaPhoto) {
                ArrayList<TLRPC$PhotoSize> arrayList2 = tLRPC$MessageMedia.photo.sizes;
                if (arrayList2.size() > 0 && (closestPhotoSizeWithSize2 = getClosestPhotoSizeWithSize(arrayList2, AndroidUtilities.getPhotoSize(), false, (TLRPC$PhotoSize) null, true)) != null) {
                    return getAttachFileName(closestPhotoSizeWithSize2);
                }
            } else if (tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaWebPage) {
                TLRPC$WebPage tLRPC$WebPage = tLRPC$MessageMedia.webpage;
                TLRPC$Document tLRPC$Document = tLRPC$WebPage.document;
                if (tLRPC$Document != null) {
                    return getAttachFileName(tLRPC$Document);
                }
                TLRPC$Photo tLRPC$Photo2 = tLRPC$WebPage.photo;
                if (tLRPC$Photo2 != null) {
                    ArrayList<TLRPC$PhotoSize> arrayList3 = tLRPC$Photo2.sizes;
                    if (arrayList3.size() > 0 && (closestPhotoSizeWithSize = getClosestPhotoSizeWithSize(arrayList3, AndroidUtilities.getPhotoSize())) != null) {
                        return getAttachFileName(closestPhotoSizeWithSize);
                    }
                }
            } else if ((tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaInvoice) && (tLRPC$WebDocument = ((TLRPC$TL_messageMediaInvoice) tLRPC$MessageMedia).photo) != null) {
                return Utilities.MD5(tLRPC$WebDocument.url) + "." + ImageLoader.getHttpUrlExtension(tLRPC$WebDocument.url, getMimeTypePart(tLRPC$WebDocument.mime_type));
            }
        }
        return "";
    }

    public static File getPathToMessage(TLRPC$Message tLRPC$Message) {
        TLRPC$PhotoSize closestPhotoSizeWithSize;
        TLRPC$PhotoSize closestPhotoSizeWithSize2;
        TLRPC$PhotoSize closestPhotoSizeWithSize3;
        if (tLRPC$Message == null) {
            return new File("");
        }
        if (tLRPC$Message instanceof TLRPC$TL_messageService) {
            TLRPC$Photo tLRPC$Photo = tLRPC$Message.action.photo;
            if (tLRPC$Photo != null) {
                ArrayList<TLRPC$PhotoSize> arrayList = tLRPC$Photo.sizes;
                if (arrayList.size() > 0 && (closestPhotoSizeWithSize3 = getClosestPhotoSizeWithSize(arrayList, AndroidUtilities.getPhotoSize())) != null) {
                    return getPathToAttach(closestPhotoSizeWithSize3);
                }
            }
        } else {
            TLRPC$MessageMedia tLRPC$MessageMedia = tLRPC$Message.media;
            boolean z = false;
            if (tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaDocument) {
                TLRPC$Document tLRPC$Document = tLRPC$MessageMedia.document;
                if (tLRPC$MessageMedia.ttl_seconds != 0) {
                    z = true;
                }
                return getPathToAttach(tLRPC$Document, z);
            } else if (tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaPhoto) {
                ArrayList<TLRPC$PhotoSize> arrayList2 = tLRPC$MessageMedia.photo.sizes;
                if (arrayList2.size() > 0 && (closestPhotoSizeWithSize2 = getClosestPhotoSizeWithSize(arrayList2, AndroidUtilities.getPhotoSize(), false, (TLRPC$PhotoSize) null, true)) != null) {
                    if (tLRPC$Message.media.ttl_seconds != 0) {
                        z = true;
                    }
                    return getPathToAttach(closestPhotoSizeWithSize2, z);
                }
            } else if (tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaWebPage) {
                TLRPC$WebPage tLRPC$WebPage = tLRPC$MessageMedia.webpage;
                TLRPC$Document tLRPC$Document2 = tLRPC$WebPage.document;
                if (tLRPC$Document2 != null) {
                    return getPathToAttach(tLRPC$Document2);
                }
                TLRPC$Photo tLRPC$Photo2 = tLRPC$WebPage.photo;
                if (tLRPC$Photo2 != null) {
                    ArrayList<TLRPC$PhotoSize> arrayList3 = tLRPC$Photo2.sizes;
                    if (arrayList3.size() > 0 && (closestPhotoSizeWithSize = getClosestPhotoSizeWithSize(arrayList3, AndroidUtilities.getPhotoSize())) != null) {
                        return getPathToAttach(closestPhotoSizeWithSize);
                    }
                }
            } else if (tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaInvoice) {
                return getPathToAttach(((TLRPC$TL_messageMediaInvoice) tLRPC$MessageMedia).photo, true);
            }
        }
        return new File("");
    }

    public static File getPathToAttach(TLObject tLObject) {
        return getPathToAttach(tLObject, (String) null, false);
    }

    public static File getPathToAttach(TLObject tLObject, boolean z) {
        return getPathToAttach(tLObject, (String) null, z);
    }

    public static File getPathToAttach(TLObject tLObject, String str, boolean z) {
        return getPathToAttach(tLObject, (String) null, str, z);
    }

    public static File getPathToAttach(TLObject tLObject, String str, String str2, boolean z) {
        File directory;
        File file = null;
        if (z) {
            file = getDirectory(4);
        } else {
            if (tLObject instanceof TLRPC$Document) {
                TLRPC$Document tLRPC$Document = (TLRPC$Document) tLObject;
                if (tLRPC$Document.key != null) {
                    directory = getDirectory(4);
                } else if (MessageObject.isVoiceDocument(tLRPC$Document)) {
                    directory = getDirectory(1);
                } else if (MessageObject.isVideoDocument(tLRPC$Document)) {
                    directory = getDirectory(2);
                } else {
                    directory = getDirectory(3);
                }
            } else if (tLObject instanceof TLRPC$Photo) {
                return getPathToAttach(getClosestPhotoSizeWithSize(((TLRPC$Photo) tLObject).sizes, AndroidUtilities.getPhotoSize()), str2, false);
            } else {
                if (tLObject instanceof TLRPC$PhotoSize) {
                    TLRPC$PhotoSize tLRPC$PhotoSize = (TLRPC$PhotoSize) tLObject;
                    if (!(tLRPC$PhotoSize instanceof TLRPC$TL_photoStrippedSize) && !(tLRPC$PhotoSize instanceof TLRPC$TL_photoPathSize)) {
                        TLRPC$FileLocation tLRPC$FileLocation = tLRPC$PhotoSize.location;
                        if (tLRPC$FileLocation == null || tLRPC$FileLocation.key != null || ((tLRPC$FileLocation.volume_id == -2147483648L && tLRPC$FileLocation.local_id < 0) || tLRPC$PhotoSize.size < 0)) {
                            directory = getDirectory(4);
                        } else {
                            directory = getDirectory(0);
                        }
                    }
                } else if (tLObject instanceof TLRPC$TL_videoSize) {
                    TLRPC$TL_videoSize tLRPC$TL_videoSize = (TLRPC$TL_videoSize) tLObject;
                    TLRPC$FileLocation tLRPC$FileLocation2 = tLRPC$TL_videoSize.location;
                    if (tLRPC$FileLocation2 == null || tLRPC$FileLocation2.key != null || ((tLRPC$FileLocation2.volume_id == -2147483648L && tLRPC$FileLocation2.local_id < 0) || tLRPC$TL_videoSize.size < 0)) {
                        directory = getDirectory(4);
                    } else {
                        directory = getDirectory(0);
                    }
                } else if (tLObject instanceof TLRPC$FileLocation) {
                    TLRPC$FileLocation tLRPC$FileLocation3 = (TLRPC$FileLocation) tLObject;
                    if (tLRPC$FileLocation3.key != null || (tLRPC$FileLocation3.volume_id == -2147483648L && tLRPC$FileLocation3.local_id < 0)) {
                        directory = getDirectory(4);
                    } else {
                        directory = getDirectory(0);
                    }
                } else if ((tLObject instanceof TLRPC$UserProfilePhoto) || (tLObject instanceof TLRPC$ChatPhoto)) {
                    if (str == null) {
                        str = "s";
                    }
                    file = "s".equals(str) ? getDirectory(4) : getDirectory(0);
                } else if (tLObject instanceof WebFile) {
                    WebFile webFile = (WebFile) tLObject;
                    if (webFile.mime_type.startsWith("image/")) {
                        directory = getDirectory(0);
                    } else if (webFile.mime_type.startsWith("audio/")) {
                        directory = getDirectory(1);
                    } else if (webFile.mime_type.startsWith("video/")) {
                        directory = getDirectory(2);
                    } else {
                        directory = getDirectory(3);
                    }
                } else if ((tLObject instanceof TLRPC$TL_secureFile) || (tLObject instanceof SecureDocument)) {
                    file = getDirectory(4);
                }
            }
            file = directory;
        }
        if (file == null) {
            return new File("");
        }
        return new File(file, getAttachFileName(tLObject, str2));
    }

    public static TLRPC$PhotoSize getClosestPhotoSizeWithSize(ArrayList<TLRPC$PhotoSize> arrayList, int i) {
        return getClosestPhotoSizeWithSize(arrayList, i, false);
    }

    public static TLRPC$PhotoSize getClosestPhotoSizeWithSize(ArrayList<TLRPC$PhotoSize> arrayList, int i, boolean z) {
        return getClosestPhotoSizeWithSize(arrayList, i, z, (TLRPC$PhotoSize) null, false);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:25:0x0044, code lost:
        if (r5.dc_id != Integer.MIN_VALUE) goto L_0x0046;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x0061, code lost:
        if (r5.dc_id != Integer.MIN_VALUE) goto L_0x0063;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static org.telegram.tgnet.TLRPC$PhotoSize getClosestPhotoSizeWithSize(java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r8, int r9, boolean r10, org.telegram.tgnet.TLRPC$PhotoSize r11, boolean r12) {
        /*
            r0 = 0
            if (r8 == 0) goto L_0x0070
            boolean r1 = r8.isEmpty()
            if (r1 == 0) goto L_0x000b
            goto L_0x0070
        L_0x000b:
            r1 = 0
            r2 = 0
        L_0x000d:
            int r3 = r8.size()
            if (r1 >= r3) goto L_0x0070
            java.lang.Object r3 = r8.get(r1)
            org.telegram.tgnet.TLRPC$PhotoSize r3 = (org.telegram.tgnet.TLRPC$PhotoSize) r3
            if (r3 == 0) goto L_0x006d
            if (r3 == r11) goto L_0x006d
            boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC$TL_photoSizeEmpty
            if (r4 != 0) goto L_0x006d
            boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC$TL_photoPathSize
            if (r4 != 0) goto L_0x006d
            if (r12 == 0) goto L_0x002c
            boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC$TL_photoStrippedSize
            if (r4 == 0) goto L_0x002c
            goto L_0x006d
        L_0x002c:
            r4 = -2147483648(0xfffffffvar_, float:-0.0)
            r5 = 100
            if (r10 == 0) goto L_0x004f
            int r6 = r3.h
            int r7 = r3.w
            int r6 = java.lang.Math.min(r6, r7)
            if (r0 == 0) goto L_0x006b
            if (r9 <= r5) goto L_0x0046
            org.telegram.tgnet.TLRPC$FileLocation r5 = r0.location
            if (r5 == 0) goto L_0x0046
            int r5 = r5.dc_id
            if (r5 == r4) goto L_0x006b
        L_0x0046:
            boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC$TL_photoCachedSize
            if (r4 != 0) goto L_0x006b
            if (r9 <= r2) goto L_0x006d
            if (r2 >= r6) goto L_0x006d
            goto L_0x006b
        L_0x004f:
            int r6 = r3.w
            int r7 = r3.h
            int r6 = java.lang.Math.max(r6, r7)
            if (r0 == 0) goto L_0x006b
            if (r9 <= r5) goto L_0x0063
            org.telegram.tgnet.TLRPC$FileLocation r5 = r0.location
            if (r5 == 0) goto L_0x0063
            int r5 = r5.dc_id
            if (r5 == r4) goto L_0x006b
        L_0x0063:
            boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC$TL_photoCachedSize
            if (r4 != 0) goto L_0x006b
            if (r6 > r9) goto L_0x006d
            if (r2 >= r6) goto L_0x006d
        L_0x006b:
            r0 = r3
            r2 = r6
        L_0x006d:
            int r1 = r1 + 1
            goto L_0x000d
        L_0x0070:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(java.util.ArrayList, int, boolean, org.telegram.tgnet.TLRPC$PhotoSize, boolean):org.telegram.tgnet.TLRPC$PhotoSize");
    }

    public static TLRPC$TL_photoPathSize getPathPhotoSize(ArrayList<TLRPC$PhotoSize> arrayList) {
        if (arrayList != null && !arrayList.isEmpty()) {
            for (int i = 0; i < arrayList.size(); i++) {
                TLRPC$PhotoSize tLRPC$PhotoSize = arrayList.get(i);
                if (!(tLRPC$PhotoSize instanceof TLRPC$TL_photoPathSize)) {
                    return (TLRPC$TL_photoPathSize) tLRPC$PhotoSize;
                }
            }
        }
        return null;
    }

    public static String getFileExtension(File file) {
        String name = file.getName();
        try {
            return name.substring(name.lastIndexOf(46) + 1);
        } catch (Exception unused) {
            return "";
        }
    }

    public static String fixFileName(String str) {
        return str != null ? str.replaceAll("[\u0001-\u001f<>‮:\"/\\\\|?*]+", "").trim() : str;
    }

    public static String getDocumentFileName(TLRPC$Document tLRPC$Document) {
        String str = tLRPC$Document.file_name_fixed;
        if (str != null) {
            return str;
        }
        String str2 = null;
        String str3 = tLRPC$Document.file_name;
        if (str3 == null) {
            for (int i = 0; i < tLRPC$Document.attributes.size(); i++) {
                TLRPC$DocumentAttribute tLRPC$DocumentAttribute = tLRPC$Document.attributes.get(i);
                if (tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeFilename) {
                    str2 = tLRPC$DocumentAttribute.file_name;
                }
            }
            str3 = str2;
        }
        String fixFileName = fixFileName(str3);
        return fixFileName != null ? fixFileName : "";
    }

    public static String getMimeTypePart(String str) {
        int lastIndexOf = str.lastIndexOf(47);
        return lastIndexOf != -1 ? str.substring(lastIndexOf + 1) : "";
    }

    public static String getExtensionByMimeType(String str) {
        if (str == null) {
            return "";
        }
        char c = 65535;
        switch (str.hashCode()) {
            case 187091926:
                if (str.equals("audio/ogg")) {
                    c = 0;
                    break;
                }
                break;
            case 1331848029:
                if (str.equals("video/mp4")) {
                    c = 1;
                    break;
                }
                break;
            case 2039520277:
                if (str.equals("video/x-matroska")) {
                    c = 2;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                return ".ogg";
            case 1:
                return ".mp4";
            case 2:
                return ".mkv";
            default:
                return "";
        }
    }

    public static File getInternalCacheDir() {
        return ApplicationLoader.applicationContext.getCacheDir();
    }

    public static String getDocumentExtension(TLRPC$Document tLRPC$Document) {
        String documentFileName = getDocumentFileName(tLRPC$Document);
        int lastIndexOf = documentFileName.lastIndexOf(46);
        String substring = lastIndexOf != -1 ? documentFileName.substring(lastIndexOf + 1) : null;
        if (substring == null || substring.length() == 0) {
            substring = tLRPC$Document.mime_type;
        }
        if (substring == null) {
            substring = "";
        }
        return substring.toUpperCase();
    }

    public static String getAttachFileName(TLObject tLObject) {
        return getAttachFileName(tLObject, (String) null);
    }

    public static String getAttachFileName(TLObject tLObject, String str) {
        return getAttachFileName(tLObject, (String) null, str);
    }

    public static String getAttachFileName(TLObject tLObject, String str, String str2) {
        String str3 = "";
        if (tLObject instanceof TLRPC$Document) {
            TLRPC$Document tLRPC$Document = (TLRPC$Document) tLObject;
            String documentFileName = getDocumentFileName(tLRPC$Document);
            int lastIndexOf = documentFileName.lastIndexOf(46);
            if (lastIndexOf != -1) {
                str3 = documentFileName.substring(lastIndexOf);
            }
            if (str3.length() <= 1) {
                str3 = getExtensionByMimeType(tLRPC$Document.mime_type);
            }
            if (str3.length() > 1) {
                return tLRPC$Document.dc_id + "_" + tLRPC$Document.id + str3;
            }
            return tLRPC$Document.dc_id + "_" + tLRPC$Document.id;
        } else if (tLObject instanceof SecureDocument) {
            SecureDocument secureDocument = (SecureDocument) tLObject;
            return secureDocument.secureFile.dc_id + "_" + secureDocument.secureFile.id + ".jpg";
        } else if (tLObject instanceof TLRPC$TL_secureFile) {
            TLRPC$TL_secureFile tLRPC$TL_secureFile = (TLRPC$TL_secureFile) tLObject;
            return tLRPC$TL_secureFile.dc_id + "_" + tLRPC$TL_secureFile.id + ".jpg";
        } else if (tLObject instanceof WebFile) {
            WebFile webFile = (WebFile) tLObject;
            return Utilities.MD5(webFile.url) + "." + ImageLoader.getHttpUrlExtension(webFile.url, getMimeTypePart(webFile.mime_type));
        } else if (tLObject instanceof TLRPC$PhotoSize) {
            TLRPC$PhotoSize tLRPC$PhotoSize = (TLRPC$PhotoSize) tLObject;
            TLRPC$FileLocation tLRPC$FileLocation = tLRPC$PhotoSize.location;
            if (tLRPC$FileLocation == null || (tLRPC$FileLocation instanceof TLRPC$TL_fileLocationUnavailable)) {
                return str3;
            }
            StringBuilder sb = new StringBuilder();
            sb.append(tLRPC$PhotoSize.location.volume_id);
            sb.append("_");
            sb.append(tLRPC$PhotoSize.location.local_id);
            sb.append(".");
            if (str2 == null) {
                str2 = "jpg";
            }
            sb.append(str2);
            return sb.toString();
        } else if (tLObject instanceof TLRPC$TL_videoSize) {
            TLRPC$TL_videoSize tLRPC$TL_videoSize = (TLRPC$TL_videoSize) tLObject;
            TLRPC$FileLocation tLRPC$FileLocation2 = tLRPC$TL_videoSize.location;
            if (tLRPC$FileLocation2 == null || (tLRPC$FileLocation2 instanceof TLRPC$TL_fileLocationUnavailable)) {
                return str3;
            }
            StringBuilder sb2 = new StringBuilder();
            sb2.append(tLRPC$TL_videoSize.location.volume_id);
            sb2.append("_");
            sb2.append(tLRPC$TL_videoSize.location.local_id);
            sb2.append(".");
            if (str2 == null) {
                str2 = "mp4";
            }
            sb2.append(str2);
            return sb2.toString();
        } else if (tLObject instanceof TLRPC$FileLocation) {
            if (tLObject instanceof TLRPC$TL_fileLocationUnavailable) {
                return str3;
            }
            TLRPC$FileLocation tLRPC$FileLocation3 = (TLRPC$FileLocation) tLObject;
            StringBuilder sb3 = new StringBuilder();
            sb3.append(tLRPC$FileLocation3.volume_id);
            sb3.append("_");
            sb3.append(tLRPC$FileLocation3.local_id);
            sb3.append(".");
            if (str2 == null) {
                str2 = "jpg";
            }
            sb3.append(str2);
            return sb3.toString();
        } else if (tLObject instanceof TLRPC$UserProfilePhoto) {
            if (str == null) {
                str = "s";
            }
            TLRPC$UserProfilePhoto tLRPC$UserProfilePhoto = (TLRPC$UserProfilePhoto) tLObject;
            if (tLRPC$UserProfilePhoto.photo_small == null) {
                StringBuilder sb4 = new StringBuilder();
                sb4.append(tLRPC$UserProfilePhoto.photo_id);
                sb4.append("_");
                sb4.append(str);
                sb4.append(".");
                if (str2 == null) {
                    str2 = "jpg";
                }
                sb4.append(str2);
                return sb4.toString();
            } else if ("s".equals(str)) {
                return getAttachFileName(tLRPC$UserProfilePhoto.photo_small, str2);
            } else {
                return getAttachFileName(tLRPC$UserProfilePhoto.photo_big, str2);
            }
        } else if (!(tLObject instanceof TLRPC$ChatPhoto)) {
            return str3;
        } else {
            TLRPC$ChatPhoto tLRPC$ChatPhoto = (TLRPC$ChatPhoto) tLObject;
            if (tLRPC$ChatPhoto.photo_small == null) {
                StringBuilder sb5 = new StringBuilder();
                sb5.append(tLRPC$ChatPhoto.photo_id);
                sb5.append("_");
                sb5.append(str);
                sb5.append(".");
                if (str2 == null) {
                    str2 = "jpg";
                }
                sb5.append(str2);
                return sb5.toString();
            } else if ("s".equals(str)) {
                return getAttachFileName(tLRPC$ChatPhoto.photo_small, str2);
            } else {
                return getAttachFileName(tLRPC$ChatPhoto.photo_big, str2);
            }
        }
    }

    public void deleteFiles(ArrayList<File> arrayList, int i) {
        if (arrayList != null && !arrayList.isEmpty()) {
            fileLoaderQueue.postRunnable(new FileLoader$$ExternalSyntheticLambda0(arrayList, i));
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$deleteFiles$12(ArrayList arrayList, int i) {
        for (int i2 = 0; i2 < arrayList.size(); i2++) {
            File file = (File) arrayList.get(i2);
            File file2 = new File(file.getAbsolutePath() + ".enc");
            if (file2.exists()) {
                try {
                    if (!file2.delete()) {
                        file2.deleteOnExit();
                    }
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
                try {
                    File internalCacheDir = getInternalCacheDir();
                    File file3 = new File(internalCacheDir, file.getName() + ".enc.key");
                    if (!file3.delete()) {
                        file3.deleteOnExit();
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
                File file4 = new File(parentFile, "q_" + file.getName());
                if (file4.exists() && !file4.delete()) {
                    file4.deleteOnExit();
                }
            } catch (Exception e4) {
                FileLog.e((Throwable) e4);
            }
        }
        if (i == 2) {
            ImageLoader.getInstance().clearMemory();
        }
    }

    public static boolean isVideoMimeType(String str) {
        return "video/mp4".equals(str) || (SharedConfig.streamMkv && "video/x-matroska".equals(str));
    }

    public static boolean copyFile(InputStream inputStream, File file) throws IOException {
        return copyFile(inputStream, file, -1);
    }

    public static boolean copyFile(InputStream inputStream, File file, int i) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        byte[] bArr = new byte[4096];
        int i2 = 0;
        while (true) {
            int read = inputStream.read(bArr);
            if (read <= 0) {
                break;
            }
            Thread.yield();
            fileOutputStream.write(bArr, 0, read);
            i2 += read;
            if (i > 0 && i2 >= i) {
                break;
            }
        }
        fileOutputStream.getFD().sync();
        fileOutputStream.close();
        return true;
    }

    public static boolean isSamePhoto(TLObject tLObject, TLObject tLObject2) {
        if ((tLObject == null && tLObject2 != null) || (tLObject != null && tLObject2 == null)) {
            return false;
        }
        if (tLObject == null && tLObject2 == null) {
            return true;
        }
        if (tLObject.getClass() != tLObject2.getClass()) {
            return false;
        }
        if (tLObject instanceof TLRPC$UserProfilePhoto) {
            if (((TLRPC$UserProfilePhoto) tLObject).photo_id == ((TLRPC$UserProfilePhoto) tLObject2).photo_id) {
                return true;
            }
            return false;
        } else if (!(tLObject instanceof TLRPC$ChatPhoto) || ((TLRPC$ChatPhoto) tLObject).photo_id != ((TLRPC$ChatPhoto) tLObject2).photo_id) {
            return false;
        } else {
            return true;
        }
    }

    public static boolean isSamePhoto(TLRPC$FileLocation tLRPC$FileLocation, TLRPC$Photo tLRPC$Photo) {
        if (tLRPC$FileLocation != null && (tLRPC$Photo instanceof TLRPC$TL_photo)) {
            int size = tLRPC$Photo.sizes.size();
            for (int i = 0; i < size; i++) {
                TLRPC$FileLocation tLRPC$FileLocation2 = tLRPC$Photo.sizes.get(i).location;
                if (tLRPC$FileLocation2 != null && tLRPC$FileLocation2.local_id == tLRPC$FileLocation.local_id && tLRPC$FileLocation2.volume_id == tLRPC$FileLocation.volume_id) {
                    return true;
                }
            }
            if ((-tLRPC$FileLocation.volume_id) == tLRPC$Photo.id) {
                return true;
            }
        }
        return false;
    }

    public static long getPhotoId(TLObject tLObject) {
        if (tLObject instanceof TLRPC$Photo) {
            return ((TLRPC$Photo) tLObject).id;
        }
        if (tLObject instanceof TLRPC$ChatPhoto) {
            return ((TLRPC$ChatPhoto) tLObject).photo_id;
        }
        if (tLObject instanceof TLRPC$UserProfilePhoto) {
            return ((TLRPC$UserProfilePhoto) tLObject).photo_id;
        }
        return 0;
    }

    public void getCurrentLoadingFiles(ArrayList<MessageObject> arrayList) {
        arrayList.clear();
        arrayList.addAll(getDownloadController().downloadingFiles);
        for (int i = 0; i < arrayList.size(); i++) {
            arrayList.get(i).isDownloadingFile = true;
        }
    }

    public void getRecentLoadingFiles(ArrayList<MessageObject> arrayList) {
        arrayList.clear();
        arrayList.addAll(getDownloadController().recentDownloadingFiles);
        for (int i = 0; i < arrayList.size(); i++) {
            arrayList.get(i).isDownloadingFile = true;
        }
    }

    public void checkCurrentDownloadsFiles() {
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList(getDownloadController().recentDownloadingFiles);
        for (int i = 0; i < arrayList2.size(); i++) {
            ((MessageObject) arrayList2.get(i)).checkMediaExistance();
            if (((MessageObject) arrayList2.get(i)).mediaExists) {
                arrayList.add((MessageObject) arrayList2.get(i));
            }
        }
        if (!arrayList.isEmpty()) {
            AndroidUtilities.runOnUIThread(new FileLoader$$ExternalSyntheticLambda4(this, arrayList));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$checkCurrentDownloadsFiles$13(ArrayList arrayList) {
        getDownloadController().recentDownloadingFiles.removeAll(arrayList);
        getNotificationCenter().postNotificationName(NotificationCenter.onDownloadingFilesChanged, new Object[0]);
    }

    public void clearRecentDownloadedFiles() {
        getDownloadController().clearRecentDownloadedFiles();
    }
}
