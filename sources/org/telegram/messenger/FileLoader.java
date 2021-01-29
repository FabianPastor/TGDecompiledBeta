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
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileUploadOperation;
import org.telegram.tgnet.TLObject;
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

        void fileLoadProgressChanged(String str, long j, long j2);

        void fileUploadProgressChanged(String str, long j, long j2, boolean z);
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
                AndroidUtilities.runOnUIThread(new Runnable(tLRPC$Document, z) {
                    public final /* synthetic */ TLRPC$Document f$1;
                    public final /* synthetic */ boolean f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run() {
                        FileLoader.this.lambda$setLoadingVideo$0$FileLoader(this.f$1, this.f$2);
                    }
                });
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
                AndroidUtilities.runOnUIThread(new Runnable(tLRPC$Document, z) {
                    public final /* synthetic */ TLRPC$Document f$1;
                    public final /* synthetic */ boolean f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run() {
                        FileLoader.this.lambda$removeLoadingVideo$1$FileLoader(this.f$1, this.f$2);
                    }
                });
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

    public void cancelUploadFile(String str, boolean z) {
        fileLoaderQueue.postRunnable(new Runnable(z, str) {
            public final /* synthetic */ boolean f$1;
            public final /* synthetic */ String f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                FileLoader.this.lambda$cancelUploadFile$2$FileLoader(this.f$1, this.f$2);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$cancelUploadFile$2 */
    public /* synthetic */ void lambda$cancelUploadFile$2$FileLoader(boolean z, String str) {
        FileUploadOperation fileUploadOperation;
        if (!z) {
            fileUploadOperation = (FileUploadOperation) this.uploadOperationPaths.get(str);
        } else {
            fileUploadOperation = (FileUploadOperation) this.uploadOperationPathsEnc.get(str);
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
        fileLoaderQueue.postRunnable(new Runnable(z, str, j, j2) {
            public final /* synthetic */ boolean f$1;
            public final /* synthetic */ String f$2;
            public final /* synthetic */ long f$3;
            public final /* synthetic */ long f$4;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r6;
            }

            public final void run() {
                FileLoader.this.lambda$checkUploadNewDataAvailable$3$FileLoader(this.f$1, this.f$2, this.f$3, this.f$4);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$checkUploadNewDataAvailable$3 */
    public /* synthetic */ void lambda$checkUploadNewDataAvailable$3$FileLoader(boolean z, String str, long j, long j2) {
        FileUploadOperation fileUploadOperation;
        if (z) {
            fileUploadOperation = (FileUploadOperation) this.uploadOperationPathsEnc.get(str);
        } else {
            fileUploadOperation = (FileUploadOperation) this.uploadOperationPaths.get(str);
        }
        if (fileUploadOperation != null) {
            fileUploadOperation.checkNewDataAvailable(j, j2);
        } else if (j2 != 0) {
            this.uploadSizes.put(str, Long.valueOf(j2));
        }
    }

    public void onNetworkChanged(boolean z) {
        fileLoaderQueue.postRunnable(new Runnable(z) {
            public final /* synthetic */ boolean f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                FileLoader.this.lambda$onNetworkChanged$4$FileLoader(this.f$1);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onNetworkChanged$4 */
    public /* synthetic */ void lambda$onNetworkChanged$4$FileLoader(boolean z) {
        for (Map.Entry value : this.uploadOperationPaths.entrySet()) {
            ((FileUploadOperation) value.getValue()).onNetworkChanged(z);
        }
        for (Map.Entry value2 : this.uploadOperationPathsEnc.entrySet()) {
            ((FileUploadOperation) value2.getValue()).onNetworkChanged(z);
        }
    }

    public void uploadFile(String str, boolean z, boolean z2, int i) {
        uploadFile(str, z, z2, 0, i);
    }

    public void uploadFile(String str, boolean z, boolean z2, int i, int i2) {
        if (str != null) {
            fileLoaderQueue.postRunnable(new Runnable(z, str, i, i2, z2) {
                public final /* synthetic */ boolean f$1;
                public final /* synthetic */ String f$2;
                public final /* synthetic */ int f$3;
                public final /* synthetic */ int f$4;
                public final /* synthetic */ boolean f$5;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                    this.f$5 = r6;
                }

                public final void run() {
                    FileLoader.this.lambda$uploadFile$5$FileLoader(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$uploadFile$5 */
    public /* synthetic */ void lambda$uploadFile$5$FileLoader(final boolean z, final String str, int i, int i2, boolean z2) {
        int i3;
        boolean z3 = z;
        String str2 = str;
        int i4 = i;
        final boolean z4 = z2;
        if (z3) {
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
        FileLoaderDelegate fileLoaderDelegate = this.delegate;
        if (!(fileLoaderDelegate == null || i4 == 0)) {
            fileLoaderDelegate.fileUploadProgressChanged(str, 0, (long) i4, z);
        }
        FileUploadOperation fileUploadOperation = new FileUploadOperation(this.currentAccount, str, z, i3, i2);
        if (z3) {
            this.uploadOperationPathsEnc.put(str, fileUploadOperation);
        } else {
            this.uploadOperationPaths.put(str, fileUploadOperation);
        }
        fileUploadOperation.setDelegate(new FileUploadOperation.FileUploadOperationDelegate() {
            public void didFinishUploadingFile(FileUploadOperation fileUploadOperation, TLRPC$InputFile tLRPC$InputFile, TLRPC$InputEncryptedFile tLRPC$InputEncryptedFile, byte[] bArr, byte[] bArr2) {
                FileLoader.fileLoaderQueue.postRunnable(new Runnable(z, str, z4, tLRPC$InputFile, tLRPC$InputEncryptedFile, bArr, bArr2, fileUploadOperation) {
                    public final /* synthetic */ boolean f$1;
                    public final /* synthetic */ String f$2;
                    public final /* synthetic */ boolean f$3;
                    public final /* synthetic */ TLRPC$InputFile f$4;
                    public final /* synthetic */ TLRPC$InputEncryptedFile f$5;
                    public final /* synthetic */ byte[] f$6;
                    public final /* synthetic */ byte[] f$7;
                    public final /* synthetic */ FileUploadOperation f$8;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r4;
                        this.f$4 = r5;
                        this.f$5 = r6;
                        this.f$6 = r7;
                        this.f$7 = r8;
                        this.f$8 = r9;
                    }

                    public final void run() {
                        FileLoader.AnonymousClass1.this.lambda$didFinishUploadingFile$0$FileLoader$1(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8);
                    }
                });
            }

            /* access modifiers changed from: private */
            /* renamed from: lambda$didFinishUploadingFile$0 */
            public /* synthetic */ void lambda$didFinishUploadingFile$0$FileLoader$1(boolean z, String str, boolean z2, TLRPC$InputFile tLRPC$InputFile, TLRPC$InputEncryptedFile tLRPC$InputEncryptedFile, byte[] bArr, byte[] bArr2, FileUploadOperation fileUploadOperation) {
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
                FileLoader.fileLoaderQueue.postRunnable(new Runnable(z, str, z4) {
                    public final /* synthetic */ boolean f$1;
                    public final /* synthetic */ String f$2;
                    public final /* synthetic */ boolean f$3;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r4;
                    }

                    public final void run() {
                        FileLoader.AnonymousClass1.this.lambda$didFailedUploadingFile$1$FileLoader$1(this.f$1, this.f$2, this.f$3);
                    }
                });
            }

            /* access modifiers changed from: private */
            /* renamed from: lambda$didFailedUploadingFile$1 */
            public /* synthetic */ void lambda$didFailedUploadingFile$1$FileLoader$1(boolean z, String str, boolean z2) {
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
                    FileLoader.this.delegate.fileUploadProgressChanged(str, j, j2, z);
                }
            }
        });
        if (z4) {
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
            fileLoaderQueue.postRunnable(new Runnable(tLRPC$FileLocation, str) {
                public final /* synthetic */ TLRPC$FileLocation f$1;
                public final /* synthetic */ String f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    FileLoader.this.lambda$setForceStreamLoadingFile$6$FileLoader(this.f$1, this.f$2);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$setForceStreamLoadingFile$6 */
    public /* synthetic */ void lambda$setForceStreamLoadingFile$6$FileLoader(TLRPC$FileLocation tLRPC$FileLocation, String str) {
        String attachFileName = getAttachFileName(tLRPC$FileLocation, str);
        this.forceLoadingFile = attachFileName;
        FileLoadOperation fileLoadOperation = (FileLoadOperation) this.loadOperationPaths.get(attachFileName);
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
            this.loadOperationPathsUI.remove(str2);
            fileLoaderQueue.postRunnable(new Runnable(str2, z) {
                public final /* synthetic */ String f$1;
                public final /* synthetic */ boolean f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    FileLoader.this.lambda$cancelLoadFile$7$FileLoader(this.f$1, this.f$2);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$cancelLoadFile$7 */
    public /* synthetic */ void lambda$cancelLoadFile$7$FileLoader(String str, boolean z) {
        FileLoadOperation fileLoadOperation = (FileLoadOperation) this.loadOperationPaths.remove(str);
        if (fileLoadOperation != null) {
            int queueType = fileLoadOperation.getQueueType();
            int datacenterId = fileLoadOperation.getDatacenterId();
            if (!getLoadOperationQueue(datacenterId, queueType).remove(fileLoadOperation)) {
                SparseIntArray loadOperationCount = getLoadOperationCount(queueType);
                loadOperationCount.put(datacenterId, loadOperationCount.get(datacenterId) - 1);
            }
            if (queueType == 0) {
                this.activeFileLoadOperation.remove(fileLoadOperation);
            }
            fileLoadOperation.cancel(z);
        }
    }

    public boolean isLoadingFile(String str) {
        return str != null && this.loadOperationPathsUI.containsKey(str);
    }

    public float getBufferedProgressFromPosition(float f, String str) {
        FileLoadOperation fileLoadOperation;
        if (!TextUtils.isEmpty(str) && (fileLoadOperation = (FileLoadOperation) this.loadOperationPaths.get(str)) != null) {
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

    /* JADX WARNING: Code restructure failed: missing block: B:65:0x010f, code lost:
        if (org.telegram.messenger.MessageObject.isVideoDocument(r21) != false) goto L_0x0111;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:75:0x0133, code lost:
        if (org.telegram.messenger.MessageObject.isImageWebDocument(r23) != false) goto L_0x00fa;
     */
    /* JADX WARNING: Removed duplicated region for block: B:100:0x0193  */
    /* JADX WARNING: Removed duplicated region for block: B:112:0x01b6  */
    /* JADX WARNING: Removed duplicated region for block: B:142:0x020e  */
    /* JADX WARNING: Removed duplicated region for block: B:78:0x013a  */
    /* JADX WARNING: Removed duplicated region for block: B:79:0x013c  */
    /* JADX WARNING: Removed duplicated region for block: B:91:0x0157  */
    /* JADX WARNING: Removed duplicated region for block: B:94:0x0160  */
    /* JADX WARNING: Removed duplicated region for block: B:97:0x0179  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private org.telegram.messenger.FileLoadOperation loadFileInternal(org.telegram.tgnet.TLRPC$Document r21, org.telegram.messenger.SecureDocument r22, org.telegram.messenger.WebFile r23, org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated r24, org.telegram.messenger.ImageLocation r25, java.lang.Object r26, java.lang.String r27, int r28, int r29, org.telegram.messenger.FileLoadOperationStream r30, int r31, boolean r32, int r33) {
        /*
            r20 = this;
            r0 = r20
            r1 = r21
            r2 = r22
            r3 = r23
            r4 = r24
            r5 = r25
            r6 = r26
            r7 = r27
            r8 = r29
            r9 = r30
            r10 = r31
            r11 = r32
            r12 = r33
            r13 = 0
            if (r4 == 0) goto L_0x0022
            java.lang.String r14 = getAttachFileName(r4, r7)
            goto L_0x0038
        L_0x0022:
            if (r2 == 0) goto L_0x0029
            java.lang.String r14 = getAttachFileName(r22)
            goto L_0x0038
        L_0x0029:
            if (r1 == 0) goto L_0x0030
            java.lang.String r14 = getAttachFileName(r21)
            goto L_0x0038
        L_0x0030:
            if (r3 == 0) goto L_0x0037
            java.lang.String r14 = getAttachFileName(r23)
            goto L_0x0038
        L_0x0037:
            r14 = r13
        L_0x0038:
            if (r14 == 0) goto L_0x0215
            java.lang.String r15 = "-NUM"
            boolean r16 = r14.contains(r15)
            if (r16 == 0) goto L_0x0044
            goto L_0x0215
        L_0x0044:
            r13 = 10
            if (r12 == r13) goto L_0x005b
            boolean r16 = android.text.TextUtils.isEmpty(r14)
            if (r16 != 0) goto L_0x005b
            boolean r15 = r14.contains(r15)
            if (r15 != 0) goto L_0x005b
            j$.util.concurrent.ConcurrentHashMap<java.lang.String, java.lang.Boolean> r15 = r0.loadOperationPathsUI
            java.lang.Boolean r13 = java.lang.Boolean.TRUE
            r15.put(r14, r13)
        L_0x005b:
            j$.util.concurrent.ConcurrentHashMap<java.lang.String, org.telegram.messenger.FileLoadOperation> r13 = r0.loadOperationPaths
            java.lang.Object r13 = r13.get(r14)
            org.telegram.messenger.FileLoadOperation r13 = (org.telegram.messenger.FileLoadOperation) r13
            if (r13 == 0) goto L_0x00dd
            r15 = 10
            if (r12 == r15) goto L_0x0073
            boolean r1 = r13.isPreloadVideoOperation()
            if (r1 == 0) goto L_0x0073
            r1 = 0
            r13.setIsPreloadVideoOperation(r1)
        L_0x0073:
            if (r9 != 0) goto L_0x0077
            if (r8 <= 0) goto L_0x00d9
        L_0x0077:
            int r1 = r13.getDatacenterId()
            r2 = 1
            r13.setForceRequest(r2)
            int r3 = r13.getQueueType()
            java.util.LinkedList r4 = r0.getLoadOperationQueue(r1, r3)
            android.util.SparseIntArray r5 = r0.getLoadOperationCount(r3)
            int r6 = r4.indexOf(r13)
            if (r6 < 0) goto L_0x00c2
            r4.remove(r6)
            if (r9 == 0) goto L_0x00bd
            boolean r4 = r13.start(r9, r10, r11)
            if (r4 == 0) goto L_0x00a4
            int r4 = r5.get(r1)
            int r4 = r4 + r2
            r5.put(r1, r4)
        L_0x00a4:
            if (r3 != 0) goto L_0x00d9
            boolean r1 = r13.wasStarted()
            if (r1 == 0) goto L_0x00d9
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation> r1 = r0.activeFileLoadOperation
            boolean r1 = r1.contains(r13)
            if (r1 != 0) goto L_0x00d9
            r0.pauseCurrentFileLoadOperations(r13)
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation> r1 = r0.activeFileLoadOperation
            r1.add(r13)
            goto L_0x00d9
        L_0x00bd:
            r15 = 0
            r4.add(r15, r13)
            goto L_0x00d9
        L_0x00c2:
            if (r9 == 0) goto L_0x00c7
            r0.pauseCurrentFileLoadOperations(r13)
        L_0x00c7:
            r13.start(r9, r10, r11)
            if (r3 != 0) goto L_0x00d9
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation> r1 = r0.activeFileLoadOperation
            boolean r1 = r1.contains(r13)
            if (r1 != 0) goto L_0x00d9
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation> r1 = r0.activeFileLoadOperation
            r1.add(r13)
        L_0x00d9:
            r13.updateProgress()
            return r13
        L_0x00dd:
            r15 = 0
            r17 = 4
            java.io.File r18 = getDirectory(r17)
            r19 = 3
            r15 = 2
            if (r2 == 0) goto L_0x00f1
            org.telegram.messenger.FileLoadOperation r13 = new org.telegram.messenger.FileLoadOperation
            r13.<init>(r2)
        L_0x00ee:
            r1 = 3
        L_0x00ef:
            r6 = 1
            goto L_0x0138
        L_0x00f1:
            if (r4 == 0) goto L_0x00fc
            org.telegram.messenger.FileLoadOperation r13 = new org.telegram.messenger.FileLoadOperation
            r1 = r28
            r13.<init>(r5, r6, r7, r1)
        L_0x00fa:
            r1 = 0
            goto L_0x00ef
        L_0x00fc:
            if (r1 == 0) goto L_0x0113
            org.telegram.messenger.FileLoadOperation r13 = new org.telegram.messenger.FileLoadOperation
            r13.<init>((org.telegram.tgnet.TLRPC$Document) r1, (java.lang.Object) r6)
            boolean r6 = org.telegram.messenger.MessageObject.isVoiceDocument(r21)
            if (r6 == 0) goto L_0x010b
        L_0x0109:
            r1 = 1
            goto L_0x00ef
        L_0x010b:
            boolean r1 = org.telegram.messenger.MessageObject.isVideoDocument(r21)
            if (r1 == 0) goto L_0x00ee
        L_0x0111:
            r1 = 2
            goto L_0x00ef
        L_0x0113:
            if (r3 == 0) goto L_0x0136
            org.telegram.messenger.FileLoadOperation r13 = new org.telegram.messenger.FileLoadOperation
            int r1 = r0.currentAccount
            r13.<init>((int) r1, (org.telegram.messenger.WebFile) r3)
            org.telegram.tgnet.TLRPC$InputWebFileLocation r1 = r3.location
            if (r1 == 0) goto L_0x0121
            goto L_0x0136
        L_0x0121:
            boolean r1 = org.telegram.messenger.MessageObject.isVoiceWebDocument(r23)
            if (r1 == 0) goto L_0x0128
            goto L_0x0109
        L_0x0128:
            boolean r1 = org.telegram.messenger.MessageObject.isVideoWebDocument(r23)
            if (r1 == 0) goto L_0x012f
            goto L_0x0111
        L_0x012f:
            boolean r1 = org.telegram.messenger.MessageObject.isImageWebDocument(r23)
            if (r1 == 0) goto L_0x00ee
            goto L_0x00fa
        L_0x0136:
            r1 = 4
            goto L_0x00ef
        L_0x0138:
            if (r1 != r6) goto L_0x013c
            r2 = 2
            goto L_0x0150
        L_0x013c:
            if (r2 != 0) goto L_0x014f
            if (r4 == 0) goto L_0x0146
            if (r5 == 0) goto L_0x014f
            int r2 = r5.imageType
            if (r2 != r15) goto L_0x014f
        L_0x0146:
            boolean r2 = org.telegram.messenger.MessageObject.isImageWebDocument(r23)
            if (r2 == 0) goto L_0x014d
            goto L_0x014f
        L_0x014d:
            r2 = 0
            goto L_0x0150
        L_0x014f:
            r2 = 1
        L_0x0150:
            if (r12 == 0) goto L_0x0160
            r3 = 10
            if (r12 != r3) goto L_0x0157
            goto L_0x0160
        L_0x0157:
            if (r12 != r15) goto L_0x015d
            r3 = 1
            r13.setEncryptFile(r3)
        L_0x015d:
            r3 = r18
            goto L_0x0164
        L_0x0160:
            java.io.File r3 = getDirectory(r1)
        L_0x0164:
            int r4 = r0.currentAccount
            r21 = r13
            r22 = r4
            r23 = r14
            r24 = r2
            r25 = r3
            r26 = r18
            r21.setPaths(r22, r23, r24, r25, r26)
            r3 = 10
            if (r12 != r3) goto L_0x017d
            r3 = 1
            r13.setIsPreloadVideoOperation(r3)
        L_0x017d:
            org.telegram.messenger.FileLoader$2 r3 = new org.telegram.messenger.FileLoader$2
            r3.<init>(r14, r1, r2)
            r13.setDelegate(r3)
            int r1 = r13.getDatacenterId()
            j$.util.concurrent.ConcurrentHashMap<java.lang.String, org.telegram.messenger.FileLoadOperation> r3 = r0.loadOperationPaths
            r3.put(r14, r13)
            r13.setPriority(r8)
            if (r2 != r15) goto L_0x01b6
            if (r8 <= 0) goto L_0x0197
            r3 = 3
            goto L_0x0198
        L_0x0197:
            r3 = 1
        L_0x0198:
            android.util.SparseIntArray r4 = r0.audioLoadOperationsCount
            int r4 = r4.get(r1)
            if (r9 != 0) goto L_0x01a5
            if (r4 >= r3) goto L_0x01a3
            goto L_0x01a5
        L_0x01a3:
            r15 = 0
            goto L_0x01a6
        L_0x01a5:
            r15 = 1
        L_0x01a6:
            if (r15 == 0) goto L_0x020c
            boolean r3 = r13.start(r9, r10, r11)
            if (r3 == 0) goto L_0x020c
            android.util.SparseIntArray r3 = r0.audioLoadOperationsCount
            r5 = 1
            int r4 = r4 + r5
            r3.put(r1, r4)
            goto L_0x020c
        L_0x01b6:
            r5 = 1
            if (r2 != r5) goto L_0x01da
            if (r8 <= 0) goto L_0x01bc
            r15 = 6
        L_0x01bc:
            android.util.SparseIntArray r3 = r0.imageLoadOperationsCount
            int r3 = r3.get(r1)
            if (r9 != 0) goto L_0x01c9
            if (r3 >= r15) goto L_0x01c7
            goto L_0x01c9
        L_0x01c7:
            r15 = 0
            goto L_0x01ca
        L_0x01c9:
            r15 = 1
        L_0x01ca:
            if (r15 == 0) goto L_0x020c
            boolean r4 = r13.start(r9, r10, r11)
            if (r4 == 0) goto L_0x020c
            android.util.SparseIntArray r4 = r0.imageLoadOperationsCount
            r5 = 1
            int r3 = r3 + r5
            r4.put(r1, r3)
            goto L_0x020c
        L_0x01da:
            if (r8 <= 0) goto L_0x01de
            r3 = 4
            goto L_0x01df
        L_0x01de:
            r3 = 1
        L_0x01df:
            android.util.SparseIntArray r4 = r0.fileLoadOperationsCount
            int r4 = r4.get(r1)
            if (r9 != 0) goto L_0x01ec
            if (r4 >= r3) goto L_0x01ea
            goto L_0x01ec
        L_0x01ea:
            r15 = 0
            goto L_0x01ed
        L_0x01ec:
            r15 = 1
        L_0x01ed:
            if (r15 == 0) goto L_0x020c
            boolean r3 = r13.start(r9, r10, r11)
            if (r3 == 0) goto L_0x0201
            android.util.SparseIntArray r3 = r0.fileLoadOperationsCount
            r5 = 1
            int r4 = r4 + r5
            r3.put(r1, r4)
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation> r3 = r0.activeFileLoadOperation
            r3.add(r13)
        L_0x0201:
            boolean r3 = r13.wasStarted()
            if (r3 == 0) goto L_0x020c
            if (r9 == 0) goto L_0x020c
            r0.pauseCurrentFileLoadOperations(r13)
        L_0x020c:
            if (r15 != 0) goto L_0x0215
            java.util.LinkedList r1 = r0.getLoadOperationQueue(r1, r2)
            r0.addOperationToQueue(r13, r1)
        L_0x0215:
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
        fileLoaderQueue.postRunnable(new Runnable(tLRPC$Document, secureDocument, webFile, tLRPC$TL_fileLocationToBeDeprecated, imageLocation, obj, str, i, i2, i3) {
            public final /* synthetic */ TLRPC$Document f$1;
            public final /* synthetic */ int f$10;
            public final /* synthetic */ SecureDocument f$2;
            public final /* synthetic */ WebFile f$3;
            public final /* synthetic */ TLRPC$TL_fileLocationToBeDeprecated f$4;
            public final /* synthetic */ ImageLocation f$5;
            public final /* synthetic */ Object f$6;
            public final /* synthetic */ String f$7;
            public final /* synthetic */ int f$8;
            public final /* synthetic */ int f$9;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
                this.f$6 = r7;
                this.f$7 = r8;
                this.f$8 = r9;
                this.f$9 = r10;
                this.f$10 = r11;
            }

            public final void run() {
                FileLoader.this.lambda$loadFile$8$FileLoader(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$loadFile$8 */
    public /* synthetic */ void lambda$loadFile$8$FileLoader(TLRPC$Document tLRPC$Document, SecureDocument secureDocument, WebFile webFile, TLRPC$TL_fileLocationToBeDeprecated tLRPC$TL_fileLocationToBeDeprecated, ImageLocation imageLocation, Object obj, String str, int i, int i2, int i3) {
        loadFileInternal(tLRPC$Document, secureDocument, webFile, tLRPC$TL_fileLocationToBeDeprecated, imageLocation, obj, str, i, i2, (FileLoadOperationStream) null, 0, false, i3);
    }

    /* access modifiers changed from: protected */
    public FileLoadOperation loadStreamFile(FileLoadOperationStream fileLoadOperationStream, TLRPC$Document tLRPC$Document, ImageLocation imageLocation, Object obj, int i, boolean z) {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        FileLoadOperation[] fileLoadOperationArr = new FileLoadOperation[1];
        fileLoaderQueue.postRunnable(new Runnable(fileLoadOperationArr, tLRPC$Document, imageLocation, obj, fileLoadOperationStream, i, z, countDownLatch) {
            public final /* synthetic */ FileLoadOperation[] f$1;
            public final /* synthetic */ TLRPC$Document f$2;
            public final /* synthetic */ ImageLocation f$3;
            public final /* synthetic */ Object f$4;
            public final /* synthetic */ FileLoadOperationStream f$5;
            public final /* synthetic */ int f$6;
            public final /* synthetic */ boolean f$7;
            public final /* synthetic */ CountDownLatch f$8;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
                this.f$6 = r7;
                this.f$7 = r8;
                this.f$8 = r9;
            }

            public final void run() {
                FileLoader.this.lambda$loadStreamFile$9$FileLoader(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8);
            }
        });
        try {
            countDownLatch.await();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        return fileLoadOperationArr[0];
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$loadStreamFile$9 */
    public /* synthetic */ void lambda$loadStreamFile$9$FileLoader(FileLoadOperation[] fileLoadOperationArr, TLRPC$Document tLRPC$Document, ImageLocation imageLocation, Object obj, FileLoadOperationStream fileLoadOperationStream, int i, boolean z, CountDownLatch countDownLatch) {
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
        fileLoaderQueue.postRunnable(new Runnable(str, i, i2) {
            public final /* synthetic */ String f$1;
            public final /* synthetic */ int f$2;
            public final /* synthetic */ int f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                FileLoader.this.lambda$checkDownloadQueue$10$FileLoader(this.f$1, this.f$2, this.f$3);
            }
        });
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
    /* renamed from: lambda$checkDownloadQueue$10 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$checkDownloadQueue$10$FileLoader(java.lang.String r7, int r8, int r9) {
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
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.FileLoader.lambda$checkDownloadQueue$10$FileLoader(java.lang.String, int, int):void");
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
                if (arrayList2.size() > 0 && (closestPhotoSizeWithSize2 = getClosestPhotoSizeWithSize(arrayList2, AndroidUtilities.getPhotoSize())) != null) {
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
                if (arrayList2.size() > 0 && (closestPhotoSizeWithSize2 = getClosestPhotoSizeWithSize(arrayList2, AndroidUtilities.getPhotoSize())) != null) {
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
                return getPathToAttach(getClosestPhotoSizeWithSize(((TLRPC$Photo) tLObject).sizes, AndroidUtilities.getPhotoSize()), str, false);
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
        return new File(file, getAttachFileName(tLObject, str));
    }

    public static TLRPC$PhotoSize getClosestPhotoSizeWithSize(ArrayList<TLRPC$PhotoSize> arrayList, int i) {
        return getClosestPhotoSizeWithSize(arrayList, i, false);
    }

    public static TLRPC$PhotoSize getClosestPhotoSizeWithSize(ArrayList<TLRPC$PhotoSize> arrayList, int i, boolean z) {
        return getClosestPhotoSizeWithSize(arrayList, i, z, (TLRPC$PhotoSize) null);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:22:0x003e, code lost:
        if (r5.dc_id != Integer.MIN_VALUE) goto L_0x0040;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x005b, code lost:
        if (r5.dc_id != Integer.MIN_VALUE) goto L_0x005d;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static org.telegram.tgnet.TLRPC$PhotoSize getClosestPhotoSizeWithSize(java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r8, int r9, boolean r10, org.telegram.tgnet.TLRPC$PhotoSize r11) {
        /*
            r0 = 0
            if (r8 == 0) goto L_0x006a
            boolean r1 = r8.isEmpty()
            if (r1 == 0) goto L_0x000b
            goto L_0x006a
        L_0x000b:
            r1 = 0
            r2 = 0
        L_0x000d:
            int r3 = r8.size()
            if (r1 >= r3) goto L_0x006a
            java.lang.Object r3 = r8.get(r1)
            org.telegram.tgnet.TLRPC$PhotoSize r3 = (org.telegram.tgnet.TLRPC$PhotoSize) r3
            if (r3 == 0) goto L_0x0067
            if (r3 == r11) goto L_0x0067
            boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC$TL_photoSizeEmpty
            if (r4 != 0) goto L_0x0067
            boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC$TL_photoPathSize
            if (r4 == 0) goto L_0x0026
            goto L_0x0067
        L_0x0026:
            r4 = -2147483648(0xfffffffvar_, float:-0.0)
            r5 = 100
            if (r10 == 0) goto L_0x0049
            int r6 = r3.h
            int r7 = r3.w
            int r6 = java.lang.Math.min(r6, r7)
            if (r0 == 0) goto L_0x0065
            if (r9 <= r5) goto L_0x0040
            org.telegram.tgnet.TLRPC$FileLocation r5 = r0.location
            if (r5 == 0) goto L_0x0040
            int r5 = r5.dc_id
            if (r5 == r4) goto L_0x0065
        L_0x0040:
            boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC$TL_photoCachedSize
            if (r4 != 0) goto L_0x0065
            if (r9 <= r2) goto L_0x0067
            if (r2 >= r6) goto L_0x0067
            goto L_0x0065
        L_0x0049:
            int r6 = r3.w
            int r7 = r3.h
            int r6 = java.lang.Math.max(r6, r7)
            if (r0 == 0) goto L_0x0065
            if (r9 <= r5) goto L_0x005d
            org.telegram.tgnet.TLRPC$FileLocation r5 = r0.location
            if (r5 == 0) goto L_0x005d
            int r5 = r5.dc_id
            if (r5 == r4) goto L_0x0065
        L_0x005d:
            boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC$TL_photoCachedSize
            if (r4 != 0) goto L_0x0065
            if (r6 > r9) goto L_0x0067
            if (r2 >= r6) goto L_0x0067
        L_0x0065:
            r0 = r3
            r2 = r6
        L_0x0067:
            int r1 = r1 + 1
            goto L_0x000d
        L_0x006a:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(java.util.ArrayList, int, boolean, org.telegram.tgnet.TLRPC$PhotoSize):org.telegram.tgnet.TLRPC$PhotoSize");
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
        String str = null;
        if (tLRPC$Document != null) {
            String str2 = tLRPC$Document.file_name;
            if (str2 != null) {
                str = str2;
            } else {
                for (int i = 0; i < tLRPC$Document.attributes.size(); i++) {
                    TLRPC$DocumentAttribute tLRPC$DocumentAttribute = tLRPC$Document.attributes.get(i);
                    if (tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeFilename) {
                        str = tLRPC$DocumentAttribute.file_name;
                    }
                }
            }
        }
        String fixFileName = fixFileName(str);
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
        str.hashCode();
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
        String str2 = "";
        if (tLObject instanceof TLRPC$Document) {
            TLRPC$Document tLRPC$Document = (TLRPC$Document) tLObject;
            String documentFileName = getDocumentFileName(tLRPC$Document);
            int lastIndexOf = documentFileName.lastIndexOf(46);
            if (lastIndexOf != -1) {
                str2 = documentFileName.substring(lastIndexOf);
            }
            if (str2.length() <= 1) {
                str2 = getExtensionByMimeType(tLRPC$Document.mime_type);
            }
            if (str2.length() > 1) {
                return tLRPC$Document.dc_id + "_" + tLRPC$Document.id + str2;
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
                return str2;
            }
            StringBuilder sb = new StringBuilder();
            sb.append(tLRPC$PhotoSize.location.volume_id);
            sb.append("_");
            sb.append(tLRPC$PhotoSize.location.local_id);
            sb.append(".");
            if (str == null) {
                str = "jpg";
            }
            sb.append(str);
            return sb.toString();
        } else if (tLObject instanceof TLRPC$TL_videoSize) {
            TLRPC$TL_videoSize tLRPC$TL_videoSize = (TLRPC$TL_videoSize) tLObject;
            TLRPC$FileLocation tLRPC$FileLocation2 = tLRPC$TL_videoSize.location;
            if (tLRPC$FileLocation2 == null || (tLRPC$FileLocation2 instanceof TLRPC$TL_fileLocationUnavailable)) {
                return str2;
            }
            StringBuilder sb2 = new StringBuilder();
            sb2.append(tLRPC$TL_videoSize.location.volume_id);
            sb2.append("_");
            sb2.append(tLRPC$TL_videoSize.location.local_id);
            sb2.append(".");
            if (str == null) {
                str = "mp4";
            }
            sb2.append(str);
            return sb2.toString();
        } else if (!(tLObject instanceof TLRPC$FileLocation) || (tLObject instanceof TLRPC$TL_fileLocationUnavailable)) {
            return str2;
        } else {
            TLRPC$FileLocation tLRPC$FileLocation3 = (TLRPC$FileLocation) tLObject;
            StringBuilder sb3 = new StringBuilder();
            sb3.append(tLRPC$FileLocation3.volume_id);
            sb3.append("_");
            sb3.append(tLRPC$FileLocation3.local_id);
            sb3.append(".");
            if (str == null) {
                str = "jpg";
            }
            sb3.append(str);
            return sb3.toString();
        }
    }

    public void deleteFiles(ArrayList<File> arrayList, int i) {
        if (arrayList != null && !arrayList.isEmpty()) {
            fileLoaderQueue.postRunnable(new Runnable(arrayList, i) {
                public final /* synthetic */ ArrayList f$0;
                public final /* synthetic */ int f$1;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                }

                public final void run() {
                    FileLoader.lambda$deleteFiles$11(this.f$0, this.f$1);
                }
            });
        }
    }

    static /* synthetic */ void lambda$deleteFiles$11(ArrayList arrayList, int i) {
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

    public static boolean isSamePhoto(TLRPC$FileLocation tLRPC$FileLocation, TLRPC$Photo tLRPC$Photo) {
        if (tLRPC$FileLocation != null && (tLRPC$Photo instanceof TLRPC$TL_photo)) {
            int size = tLRPC$Photo.sizes.size();
            for (int i = 0; i < size; i++) {
                TLRPC$FileLocation tLRPC$FileLocation2 = tLRPC$Photo.sizes.get(i).location;
                if (tLRPC$FileLocation2 != null && tLRPC$FileLocation2.local_id == tLRPC$FileLocation.local_id && tLRPC$FileLocation2.volume_id == tLRPC$FileLocation.volume_id) {
                    return true;
                }
            }
        }
        return false;
    }
}
