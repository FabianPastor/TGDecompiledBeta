package org.telegram.messenger;

import android.text.TextUtils;
import android.util.SparseArray;
import android.util.SparseIntArray;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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
    /* access modifiers changed from: private */
    public static volatile DispatchQueue fileLoaderQueue = new DispatchQueue("fileUploadQueue");
    private static SparseArray<File> mediaDirs = null;
    private ArrayList<FileLoadOperation> activeFileLoadOperation = new ArrayList<>();
    private SparseArray<LinkedList<FileLoadOperation>> audioLoadOperationQueues = new SparseArray<>();
    private SparseIntArray currentAudioLoadOperationsCount = new SparseIntArray();
    private SparseIntArray currentLoadOperationsCount = new SparseIntArray();
    private SparseIntArray currentPhotoLoadOperationsCount = new SparseIntArray();
    /* access modifiers changed from: private */
    public int currentUploadOperationsCount = 0;
    /* access modifiers changed from: private */
    public int currentUploadSmallOperationsCount = 0;
    /* access modifiers changed from: private */
    public FileLoaderDelegate delegate = null;
    private int lastReferenceId;
    private ConcurrentHashMap<String, FileLoadOperation> loadOperationPaths = new ConcurrentHashMap<>();
    /* access modifiers changed from: private */
    public ConcurrentHashMap<String, Boolean> loadOperationPathsUI = new ConcurrentHashMap<>(10, 1.0f, 2);
    private SparseArray<LinkedList<FileLoadOperation>> loadOperationQueues = new SparseArray<>();
    private HashMap<String, Boolean> loadingVideos = new HashMap<>();
    private ConcurrentHashMap<Integer, Object> parentObjectReferences = new ConcurrentHashMap<>();
    private SparseArray<LinkedList<FileLoadOperation>> photoLoadOperationQueues = new SparseArray<>();
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
        try {
            if (!file.isDirectory()) {
                file.mkdirs();
            }
        } catch (Exception unused) {
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
    public void lambda$setLoadingVideo$0$FileLoader(TLRPC$Document tLRPC$Document, boolean z) {
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
                lambda$setLoadingVideo$0$FileLoader(tLRPC$Document, z);
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
    public void lambda$removeLoadingVideo$1$FileLoader(TLRPC$Document tLRPC$Document, boolean z) {
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
                lambda$removeLoadingVideo$1$FileLoader(tLRPC$Document, z);
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

    public /* synthetic */ void lambda$cancelUploadFile$2$FileLoader(boolean z, String str) {
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

    public /* synthetic */ void lambda$checkUploadNewDataAvailable$3$FileLoader(boolean z, String str, long j, long j2) {
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

    public /* synthetic */ void lambda$onNetworkChanged$4$FileLoader(boolean z) {
        for (Map.Entry<String, FileUploadOperation> value : this.uploadOperationPaths.entrySet()) {
            ((FileUploadOperation) value.getValue()).onNetworkChanged(z);
        }
        for (Map.Entry<String, FileUploadOperation> value2 : this.uploadOperationPathsEnc.entrySet()) {
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

    private LinkedList<FileLoadOperation> getAudioLoadOperationQueue(int i) {
        LinkedList<FileLoadOperation> linkedList = this.audioLoadOperationQueues.get(i);
        if (linkedList != null) {
            return linkedList;
        }
        LinkedList<FileLoadOperation> linkedList2 = new LinkedList<>();
        this.audioLoadOperationQueues.put(i, linkedList2);
        return linkedList2;
    }

    private LinkedList<FileLoadOperation> getPhotoLoadOperationQueue(int i) {
        LinkedList<FileLoadOperation> linkedList = this.photoLoadOperationQueues.get(i);
        if (linkedList != null) {
            return linkedList;
        }
        LinkedList<FileLoadOperation> linkedList2 = new LinkedList<>();
        this.photoLoadOperationQueues.put(i, linkedList2);
        return linkedList2;
    }

    private LinkedList<FileLoadOperation> getLoadOperationQueue(int i) {
        LinkedList<FileLoadOperation> linkedList = this.loadOperationQueues.get(i);
        if (linkedList != null) {
            return linkedList;
        }
        LinkedList<FileLoadOperation> linkedList2 = new LinkedList<>();
        this.loadOperationQueues.put(i, linkedList2);
        return linkedList2;
    }

    public void cancelLoadFile(TLRPC$Document tLRPC$Document) {
        cancelLoadFile(tLRPC$Document, (SecureDocument) null, (WebFile) null, (TLRPC$FileLocation) null, (String) null);
    }

    public void cancelLoadFile(SecureDocument secureDocument) {
        cancelLoadFile((TLRPC$Document) null, secureDocument, (WebFile) null, (TLRPC$FileLocation) null, (String) null);
    }

    public void cancelLoadFile(WebFile webFile) {
        cancelLoadFile((TLRPC$Document) null, (SecureDocument) null, webFile, (TLRPC$FileLocation) null, (String) null);
    }

    public void cancelLoadFile(TLRPC$PhotoSize tLRPC$PhotoSize) {
        cancelLoadFile((TLRPC$Document) null, (SecureDocument) null, (WebFile) null, tLRPC$PhotoSize.location, (String) null);
    }

    public void cancelLoadFile(TLRPC$FileLocation tLRPC$FileLocation, String str) {
        cancelLoadFile((TLRPC$Document) null, (SecureDocument) null, (WebFile) null, tLRPC$FileLocation, str);
    }

    private void cancelLoadFile(TLRPC$Document tLRPC$Document, SecureDocument secureDocument, WebFile webFile, TLRPC$FileLocation tLRPC$FileLocation, String str) {
        String attachFileName;
        if (tLRPC$FileLocation != null || tLRPC$Document != null || webFile != null || secureDocument != null) {
            if (tLRPC$FileLocation != null) {
                attachFileName = getAttachFileName(tLRPC$FileLocation, str);
            } else if (tLRPC$Document != null) {
                attachFileName = getAttachFileName(tLRPC$Document);
            } else if (secureDocument != null) {
                attachFileName = getAttachFileName(secureDocument);
            } else {
                attachFileName = webFile != null ? getAttachFileName(webFile) : null;
            }
            String str2 = attachFileName;
            if (str2 != null) {
                this.loadOperationPathsUI.remove(str2);
                fileLoaderQueue.postRunnable(new Runnable(str2, tLRPC$Document, webFile, secureDocument, tLRPC$FileLocation) {
                    public final /* synthetic */ String f$1;
                    public final /* synthetic */ TLRPC$Document f$2;
                    public final /* synthetic */ WebFile f$3;
                    public final /* synthetic */ SecureDocument f$4;
                    public final /* synthetic */ TLRPC$FileLocation f$5;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r4;
                        this.f$4 = r5;
                        this.f$5 = r6;
                    }

                    public final void run() {
                        FileLoader.this.lambda$cancelLoadFile$6$FileLoader(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
                    }
                });
            }
        }
    }

    public /* synthetic */ void lambda$cancelLoadFile$6$FileLoader(String str, TLRPC$Document tLRPC$Document, WebFile webFile, SecureDocument secureDocument, TLRPC$FileLocation tLRPC$FileLocation) {
        FileLoadOperation remove = this.loadOperationPaths.remove(str);
        if (remove != null) {
            int datacenterId = remove.getDatacenterId();
            if (MessageObject.isVoiceDocument(tLRPC$Document) || MessageObject.isVoiceWebDocument(webFile)) {
                if (!getAudioLoadOperationQueue(datacenterId).remove(remove)) {
                    SparseIntArray sparseIntArray = this.currentAudioLoadOperationsCount;
                    sparseIntArray.put(datacenterId, sparseIntArray.get(datacenterId) - 1);
                }
            } else if (secureDocument == null && tLRPC$FileLocation == null && !MessageObject.isImageWebDocument(webFile)) {
                if (!getLoadOperationQueue(datacenterId).remove(remove)) {
                    SparseIntArray sparseIntArray2 = this.currentLoadOperationsCount;
                    sparseIntArray2.put(datacenterId, sparseIntArray2.get(datacenterId) - 1);
                }
                this.activeFileLoadOperation.remove(remove);
            } else if (!getPhotoLoadOperationQueue(datacenterId).remove(remove)) {
                SparseIntArray sparseIntArray3 = this.currentPhotoLoadOperationsCount;
                sparseIntArray3.put(datacenterId, sparseIntArray3.get(datacenterId) - 1);
            }
            remove.cancel();
        }
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
            if (fileLoadOperation2 != fileLoadOperation && fileLoadOperation2.getDatacenterId() == fileLoadOperation.getDatacenterId()) {
                this.activeFileLoadOperation.remove(fileLoadOperation2);
                i--;
                int datacenterId = fileLoadOperation2.getDatacenterId();
                getLoadOperationQueue(datacenterId).add(0, fileLoadOperation2);
                if (fileLoadOperation2.wasStarted()) {
                    SparseIntArray sparseIntArray = this.currentLoadOperationsCount;
                    sparseIntArray.put(datacenterId, sparseIntArray.get(datacenterId) - 1);
                }
                fileLoadOperation2.pause();
            }
            i++;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:85:0x0154, code lost:
        if (org.telegram.messenger.MessageObject.isVideoDocument(r20) != false) goto L_0x0156;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:93:0x0173, code lost:
        if (org.telegram.messenger.MessageObject.isImageWebDocument(r22) != false) goto L_0x013f;
     */
    /* JADX WARNING: Removed duplicated region for block: B:102:0x0187  */
    /* JADX WARNING: Removed duplicated region for block: B:105:0x0196  */
    /* JADX WARNING: Removed duplicated region for block: B:108:0x01cb  */
    /* JADX WARNING: Removed duplicated region for block: B:119:0x01ee  */
    /* JADX WARNING: Removed duplicated region for block: B:147:0x024c A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:14:0x0043  */
    /* JADX WARNING: Removed duplicated region for block: B:98:0x017e  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private org.telegram.messenger.FileLoadOperation loadFileInternal(org.telegram.tgnet.TLRPC$Document r20, org.telegram.messenger.SecureDocument r21, org.telegram.messenger.WebFile r22, org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated r23, org.telegram.messenger.ImageLocation r24, java.lang.Object r25, java.lang.String r26, int r27, int r28, org.telegram.messenger.FileLoadOperationStream r29, int r30, boolean r31, int r32) {
        /*
            r19 = this;
            r7 = r19
            r4 = r20
            r0 = r21
            r8 = r22
            r9 = r23
            r1 = r25
            r2 = r26
            r10 = r28
            r11 = r29
            r12 = r30
            r13 = r31
            r3 = r32
            r5 = 0
            if (r9 == 0) goto L_0x0021
            java.lang.String r6 = getAttachFileName(r9, r2)
        L_0x001f:
            r14 = r6
            goto L_0x0037
        L_0x0021:
            if (r0 == 0) goto L_0x0028
            java.lang.String r6 = getAttachFileName(r21)
            goto L_0x001f
        L_0x0028:
            if (r4 == 0) goto L_0x002f
            java.lang.String r6 = getAttachFileName(r20)
            goto L_0x001f
        L_0x002f:
            if (r8 == 0) goto L_0x0036
            java.lang.String r6 = getAttachFileName(r22)
            goto L_0x001f
        L_0x0036:
            r14 = r5
        L_0x0037:
            if (r14 == 0) goto L_0x024c
            java.lang.String r6 = "-NUM"
            boolean r15 = r14.contains(r6)
            if (r15 == 0) goto L_0x0043
            goto L_0x024c
        L_0x0043:
            r5 = 10
            if (r3 == r5) goto L_0x005a
            boolean r15 = android.text.TextUtils.isEmpty(r14)
            if (r15 != 0) goto L_0x005a
            boolean r6 = r14.contains(r6)
            if (r6 != 0) goto L_0x005a
            java.util.concurrent.ConcurrentHashMap<java.lang.String, java.lang.Boolean> r6 = r7.loadOperationPathsUI
            java.lang.Boolean r15 = java.lang.Boolean.TRUE
            r6.put(r14, r15)
        L_0x005a:
            java.util.concurrent.ConcurrentHashMap<java.lang.String, org.telegram.messenger.FileLoadOperation> r6 = r7.loadOperationPaths
            java.lang.Object r6 = r6.get(r14)
            org.telegram.messenger.FileLoadOperation r6 = (org.telegram.messenger.FileLoadOperation) r6
            r15 = 1
            if (r6 == 0) goto L_0x0121
            if (r3 == r5) goto L_0x0071
            boolean r1 = r6.isPreloadVideoOperation()
            if (r1 == 0) goto L_0x0071
            r1 = 0
            r6.setIsPreloadVideoOperation(r1)
        L_0x0071:
            if (r11 != 0) goto L_0x0075
            if (r10 <= 0) goto L_0x011d
        L_0x0075:
            int r1 = r6.getDatacenterId()
            java.util.LinkedList r2 = r7.getAudioLoadOperationQueue(r1)
            java.util.LinkedList r3 = r7.getPhotoLoadOperationQueue(r1)
            java.util.LinkedList r5 = r7.getLoadOperationQueue(r1)
            r6.setForceRequest(r15)
            boolean r4 = org.telegram.messenger.MessageObject.isVoiceDocument(r20)
            if (r4 != 0) goto L_0x00a4
            boolean r4 = org.telegram.messenger.MessageObject.isVoiceWebDocument(r22)
            if (r4 == 0) goto L_0x0095
            goto L_0x00a4
        L_0x0095:
            if (r0 != 0) goto L_0x00a2
            if (r9 != 0) goto L_0x00a2
            boolean r0 = org.telegram.messenger.MessageObject.isImageWebDocument(r22)
            if (r0 == 0) goto L_0x00a0
            goto L_0x00a2
        L_0x00a0:
            r0 = r5
            goto L_0x00a5
        L_0x00a2:
            r0 = r3
            goto L_0x00a5
        L_0x00a4:
            r0 = r2
        L_0x00a5:
            if (r0 == 0) goto L_0x011d
            int r4 = r0.indexOf(r6)
            if (r4 < 0) goto L_0x0106
            r0.remove(r4)
            if (r11 == 0) goto L_0x0101
            if (r0 != r2) goto L_0x00c5
            boolean r0 = r6.start(r11, r12, r13)
            if (r0 == 0) goto L_0x011d
            android.util.SparseIntArray r0 = r7.currentAudioLoadOperationsCount
            int r2 = r0.get(r1)
            int r2 = r2 + r15
            r0.put(r1, r2)
            goto L_0x011d
        L_0x00c5:
            if (r0 != r3) goto L_0x00d8
            boolean r0 = r6.start(r11, r12, r13)
            if (r0 == 0) goto L_0x011d
            android.util.SparseIntArray r0 = r7.currentPhotoLoadOperationsCount
            int r2 = r0.get(r1)
            int r2 = r2 + r15
            r0.put(r1, r2)
            goto L_0x011d
        L_0x00d8:
            boolean r0 = r6.start(r11, r12, r13)
            if (r0 == 0) goto L_0x00e8
            android.util.SparseIntArray r0 = r7.currentLoadOperationsCount
            int r2 = r0.get(r1)
            int r2 = r2 + r15
            r0.put(r1, r2)
        L_0x00e8:
            boolean r0 = r6.wasStarted()
            if (r0 == 0) goto L_0x011d
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation> r0 = r7.activeFileLoadOperation
            boolean r0 = r0.contains(r6)
            if (r0 != 0) goto L_0x011d
            if (r11 == 0) goto L_0x00fb
            r7.pauseCurrentFileLoadOperations(r6)
        L_0x00fb:
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation> r0 = r7.activeFileLoadOperation
            r0.add(r6)
            goto L_0x011d
        L_0x0101:
            r1 = 0
            r0.add(r1, r6)
            goto L_0x011d
        L_0x0106:
            if (r11 == 0) goto L_0x010b
            r7.pauseCurrentFileLoadOperations(r6)
        L_0x010b:
            r6.start(r11, r12, r13)
            if (r0 != r5) goto L_0x011d
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation> r0 = r7.activeFileLoadOperation
            boolean r0 = r0.contains(r6)
            if (r0 != 0) goto L_0x011d
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation> r0 = r7.activeFileLoadOperation
            r0.add(r6)
        L_0x011d:
            r6.updateProgress()
            return r6
        L_0x0121:
            r16 = 0
            r17 = 4
            java.io.File r15 = getDirectory(r17)
            r18 = 3
            if (r0 == 0) goto L_0x0134
            org.telegram.messenger.FileLoadOperation r6 = new org.telegram.messenger.FileLoadOperation
            r6.<init>(r0)
        L_0x0132:
            r5 = 3
            goto L_0x0177
        L_0x0134:
            if (r9 == 0) goto L_0x0141
            org.telegram.messenger.FileLoadOperation r6 = new org.telegram.messenger.FileLoadOperation
            r0 = r24
            r5 = r27
            r6.<init>(r0, r1, r2, r5)
        L_0x013f:
            r5 = 0
            goto L_0x0177
        L_0x0141:
            if (r4 == 0) goto L_0x0158
            org.telegram.messenger.FileLoadOperation r6 = new org.telegram.messenger.FileLoadOperation
            r6.<init>((org.telegram.tgnet.TLRPC$Document) r4, (java.lang.Object) r1)
            boolean r0 = org.telegram.messenger.MessageObject.isVoiceDocument(r20)
            if (r0 == 0) goto L_0x0150
        L_0x014e:
            r5 = 1
            goto L_0x0177
        L_0x0150:
            boolean r0 = org.telegram.messenger.MessageObject.isVideoDocument(r20)
            if (r0 == 0) goto L_0x0132
        L_0x0156:
            r5 = 2
            goto L_0x0177
        L_0x0158:
            if (r8 == 0) goto L_0x0176
            org.telegram.messenger.FileLoadOperation r6 = new org.telegram.messenger.FileLoadOperation
            int r0 = r7.currentAccount
            r6.<init>((int) r0, (org.telegram.messenger.WebFile) r8)
            boolean r0 = org.telegram.messenger.MessageObject.isVoiceWebDocument(r22)
            if (r0 == 0) goto L_0x0168
            goto L_0x014e
        L_0x0168:
            boolean r0 = org.telegram.messenger.MessageObject.isVideoWebDocument(r22)
            if (r0 == 0) goto L_0x016f
            goto L_0x0156
        L_0x016f:
            boolean r0 = org.telegram.messenger.MessageObject.isImageWebDocument(r22)
            if (r0 == 0) goto L_0x0132
            goto L_0x013f
        L_0x0176:
            r5 = 4
        L_0x0177:
            if (r3 == 0) goto L_0x0187
            r0 = 10
            if (r3 != r0) goto L_0x017e
            goto L_0x0187
        L_0x017e:
            r2 = 2
            r0 = 1
            if (r3 != r2) goto L_0x0185
            r6.setEncryptFile(r0)
        L_0x0185:
            r1 = r15
            goto L_0x018d
        L_0x0187:
            r0 = 1
            r2 = 2
            java.io.File r1 = getDirectory(r5)
        L_0x018d:
            int r2 = r7.currentAccount
            r6.setPaths(r2, r1, r15)
            r1 = 10
            if (r3 != r1) goto L_0x0199
            r6.setIsPreloadVideoOperation(r0)
        L_0x0199:
            org.telegram.messenger.FileLoader$2 r15 = new org.telegram.messenger.FileLoader$2
            r0 = r15
            r1 = r19
            r16 = 2
            r2 = r14
            r3 = r5
            r4 = r20
            r8 = r5
            r5 = r22
            r9 = r6
            r6 = r23
            r0.<init>(r2, r3, r4, r5, r6)
            r9.setDelegate(r15)
            int r0 = r9.getDatacenterId()
            java.util.LinkedList r1 = r7.getAudioLoadOperationQueue(r0)
            java.util.LinkedList r2 = r7.getPhotoLoadOperationQueue(r0)
            java.util.LinkedList r3 = r7.getLoadOperationQueue(r0)
            java.util.concurrent.ConcurrentHashMap<java.lang.String, org.telegram.messenger.FileLoadOperation> r4 = r7.loadOperationPaths
            r4.put(r14, r9)
            r9.setPriority(r10)
            r4 = 1
            if (r8 != r4) goto L_0x01ee
            if (r10 <= 0) goto L_0x01cf
            r2 = 3
            goto L_0x01d0
        L_0x01cf:
            r2 = 1
        L_0x01d0:
            android.util.SparseIntArray r3 = r7.currentAudioLoadOperationsCount
            int r3 = r3.get(r0)
            if (r11 != 0) goto L_0x01df
            if (r3 >= r2) goto L_0x01db
            goto L_0x01df
        L_0x01db:
            r7.addOperationToQueue(r9, r1)
            goto L_0x01ec
        L_0x01df:
            boolean r1 = r9.start(r11, r12, r13)
            if (r1 == 0) goto L_0x01ec
            android.util.SparseIntArray r1 = r7.currentAudioLoadOperationsCount
            r2 = 1
            int r3 = r3 + r2
            r1.put(r0, r3)
        L_0x01ec:
            r6 = r9
            goto L_0x024b
        L_0x01ee:
            r6 = r9
            if (r23 != 0) goto L_0x022a
            boolean r1 = org.telegram.messenger.MessageObject.isImageWebDocument(r22)
            if (r1 == 0) goto L_0x01f8
            goto L_0x022a
        L_0x01f8:
            if (r10 <= 0) goto L_0x01fc
            r1 = 4
            goto L_0x01fd
        L_0x01fc:
            r1 = 1
        L_0x01fd:
            android.util.SparseIntArray r2 = r7.currentLoadOperationsCount
            int r2 = r2.get(r0)
            if (r11 != 0) goto L_0x020c
            if (r2 >= r1) goto L_0x0208
            goto L_0x020c
        L_0x0208:
            r7.addOperationToQueue(r6, r3)
            goto L_0x024b
        L_0x020c:
            boolean r1 = r6.start(r11, r12, r13)
            if (r1 == 0) goto L_0x021e
            android.util.SparseIntArray r1 = r7.currentLoadOperationsCount
            r3 = 1
            int r2 = r2 + r3
            r1.put(r0, r2)
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation> r0 = r7.activeFileLoadOperation
            r0.add(r6)
        L_0x021e:
            boolean r0 = r6.wasStarted()
            if (r0 == 0) goto L_0x024b
            if (r11 == 0) goto L_0x024b
            r7.pauseCurrentFileLoadOperations(r6)
            goto L_0x024b
        L_0x022a:
            if (r10 <= 0) goto L_0x022e
            r5 = 6
            goto L_0x022f
        L_0x022e:
            r5 = 2
        L_0x022f:
            android.util.SparseIntArray r1 = r7.currentPhotoLoadOperationsCount
            int r1 = r1.get(r0)
            if (r11 != 0) goto L_0x023e
            if (r1 >= r5) goto L_0x023a
            goto L_0x023e
        L_0x023a:
            r7.addOperationToQueue(r6, r2)
            goto L_0x024b
        L_0x023e:
            boolean r2 = r6.start(r11, r12, r13)
            if (r2 == 0) goto L_0x024b
            android.util.SparseIntArray r2 = r7.currentPhotoLoadOperationsCount
            r3 = 1
            int r1 = r1 + r3
            r2.put(r0, r1)
        L_0x024b:
            return r6
        L_0x024c:
            return r5
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
                FileLoader.this.lambda$loadFile$7$FileLoader(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10);
            }
        });
    }

    public /* synthetic */ void lambda$loadFile$7$FileLoader(TLRPC$Document tLRPC$Document, SecureDocument secureDocument, WebFile webFile, TLRPC$TL_fileLocationToBeDeprecated tLRPC$TL_fileLocationToBeDeprecated, ImageLocation imageLocation, Object obj, String str, int i, int i2, int i3) {
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
                FileLoader.this.lambda$loadStreamFile$8$FileLoader(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8);
            }
        });
        try {
            countDownLatch.await();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        return fileLoadOperationArr[0];
    }

    public /* synthetic */ void lambda$loadStreamFile$8$FileLoader(FileLoadOperation[] fileLoadOperationArr, TLRPC$Document tLRPC$Document, ImageLocation imageLocation, Object obj, FileLoadOperationStream fileLoadOperationStream, int i, boolean z, CountDownLatch countDownLatch) {
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
    public void checkDownloadQueue(int i, TLRPC$Document tLRPC$Document, WebFile webFile, TLRPC$FileLocation tLRPC$FileLocation, String str) {
        fileLoaderQueue.postRunnable(new Runnable(i, str, tLRPC$Document, webFile, tLRPC$FileLocation) {
            public final /* synthetic */ int f$1;
            public final /* synthetic */ String f$2;
            public final /* synthetic */ TLRPC$Document f$3;
            public final /* synthetic */ WebFile f$4;
            public final /* synthetic */ TLRPC$FileLocation f$5;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
            }

            public final void run() {
                FileLoader.this.lambda$checkDownloadQueue$9$FileLoader(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
            }
        });
    }

    public /* synthetic */ void lambda$checkDownloadQueue$9$FileLoader(int i, String str, TLRPC$Document tLRPC$Document, WebFile webFile, TLRPC$FileLocation tLRPC$FileLocation) {
        LinkedList<FileLoadOperation> audioLoadOperationQueue = getAudioLoadOperationQueue(i);
        LinkedList<FileLoadOperation> photoLoadOperationQueue = getPhotoLoadOperationQueue(i);
        LinkedList<FileLoadOperation> loadOperationQueue = getLoadOperationQueue(i);
        FileLoadOperation remove = this.loadOperationPaths.remove(str);
        if (MessageObject.isVoiceDocument(tLRPC$Document) || MessageObject.isVoiceWebDocument(webFile)) {
            int i2 = this.currentAudioLoadOperationsCount.get(i);
            if (remove != null) {
                if (remove.wasStarted()) {
                    i2--;
                    this.currentAudioLoadOperationsCount.put(i, i2);
                } else {
                    audioLoadOperationQueue.remove(remove);
                }
            }
            while (!audioLoadOperationQueue.isEmpty()) {
                if (i2 < (audioLoadOperationQueue.get(0).getPriority() != 0 ? 3 : 1)) {
                    FileLoadOperation poll = audioLoadOperationQueue.poll();
                    if (poll != null && poll.start()) {
                        i2++;
                        this.currentAudioLoadOperationsCount.put(i, i2);
                    }
                } else {
                    return;
                }
            }
        } else if (tLRPC$FileLocation != null || MessageObject.isImageWebDocument(webFile)) {
            int i3 = this.currentPhotoLoadOperationsCount.get(i);
            if (remove != null) {
                if (remove.wasStarted()) {
                    i3--;
                    this.currentPhotoLoadOperationsCount.put(i, i3);
                } else {
                    photoLoadOperationQueue.remove(remove);
                }
            }
            while (!photoLoadOperationQueue.isEmpty()) {
                if (i3 < (photoLoadOperationQueue.get(0).getPriority() != 0 ? 6 : 2)) {
                    FileLoadOperation poll2 = photoLoadOperationQueue.poll();
                    if (poll2 != null && poll2.start()) {
                        i3++;
                        this.currentPhotoLoadOperationsCount.put(i, i3);
                    }
                } else {
                    return;
                }
            }
        } else {
            int i4 = this.currentLoadOperationsCount.get(i);
            if (remove != null) {
                if (remove.wasStarted()) {
                    i4--;
                    this.currentLoadOperationsCount.put(i, i4);
                } else {
                    loadOperationQueue.remove(remove);
                }
                this.activeFileLoadOperation.remove(remove);
            }
            while (!loadOperationQueue.isEmpty()) {
                if (i4 < (loadOperationQueue.get(0).isForceRequest() ? 3 : 1)) {
                    FileLoadOperation poll3 = loadOperationQueue.poll();
                    if (poll3 != null && poll3.start()) {
                        i4++;
                        this.currentLoadOperationsCount.put(i, i4);
                        if (!this.activeFileLoadOperation.contains(poll3)) {
                            this.activeFileLoadOperation.add(poll3);
                        }
                    }
                } else {
                    return;
                }
            }
        }
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
                } else if (tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaInvoice) {
                    return getAttachFileName(((TLRPC$TL_messageMediaInvoice) tLRPC$MessageMedia).photo);
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
                return getPathToAttach(getClosestPhotoSizeWithSize(((TLRPC$Photo) tLObject).sizes, AndroidUtilities.getPhotoSize()), str, z);
            } else {
                if (tLObject instanceof TLRPC$PhotoSize) {
                    TLRPC$PhotoSize tLRPC$PhotoSize = (TLRPC$PhotoSize) tLObject;
                    if (!(tLRPC$PhotoSize instanceof TLRPC$TL_photoStrippedSize)) {
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

    /* JADX WARNING: Code restructure failed: missing block: B:19:0x0037, code lost:
        if (r5.dc_id != Integer.MIN_VALUE) goto L_0x0039;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x0054, code lost:
        if (r5.dc_id != Integer.MIN_VALUE) goto L_0x0056;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static org.telegram.tgnet.TLRPC$PhotoSize getClosestPhotoSizeWithSize(java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r8, int r9, boolean r10) {
        /*
            r0 = 0
            if (r8 == 0) goto L_0x0063
            boolean r1 = r8.isEmpty()
            if (r1 == 0) goto L_0x000a
            goto L_0x0063
        L_0x000a:
            r1 = 0
            r2 = 0
        L_0x000c:
            int r3 = r8.size()
            if (r1 >= r3) goto L_0x0063
            java.lang.Object r3 = r8.get(r1)
            org.telegram.tgnet.TLRPC$PhotoSize r3 = (org.telegram.tgnet.TLRPC$PhotoSize) r3
            if (r3 == 0) goto L_0x0060
            boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC$TL_photoSizeEmpty
            if (r4 == 0) goto L_0x001f
            goto L_0x0060
        L_0x001f:
            r4 = -2147483648(0xfffffffvar_, float:-0.0)
            r5 = 100
            if (r10 == 0) goto L_0x0042
            int r6 = r3.h
            int r7 = r3.w
            int r6 = java.lang.Math.min(r6, r7)
            if (r0 == 0) goto L_0x005e
            if (r9 <= r5) goto L_0x0039
            org.telegram.tgnet.TLRPC$FileLocation r5 = r0.location
            if (r5 == 0) goto L_0x0039
            int r5 = r5.dc_id
            if (r5 == r4) goto L_0x005e
        L_0x0039:
            boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC$TL_photoCachedSize
            if (r4 != 0) goto L_0x005e
            if (r9 <= r2) goto L_0x0060
            if (r2 >= r6) goto L_0x0060
            goto L_0x005e
        L_0x0042:
            int r6 = r3.w
            int r7 = r3.h
            int r6 = java.lang.Math.max(r6, r7)
            if (r0 == 0) goto L_0x005e
            if (r9 <= r5) goto L_0x0056
            org.telegram.tgnet.TLRPC$FileLocation r5 = r0.location
            if (r5 == 0) goto L_0x0056
            int r5 = r5.dc_id
            if (r5 == r4) goto L_0x005e
        L_0x0056:
            boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC$TL_photoCachedSize
            if (r4 != 0) goto L_0x005e
            if (r6 > r9) goto L_0x0060
            if (r2 >= r6) goto L_0x0060
        L_0x005e:
            r0 = r3
            r2 = r6
        L_0x0060:
            int r1 = r1 + 1
            goto L_0x000c
        L_0x0063:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(java.util.ArrayList, int, boolean):org.telegram.tgnet.TLRPC$PhotoSize");
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
        char c = 65535;
        int hashCode = str.hashCode();
        if (hashCode != NUM) {
            if (hashCode != NUM) {
                if (hashCode == NUM && str.equals("video/x-matroska")) {
                    c = 1;
                }
            } else if (str.equals("video/mp4")) {
                c = 0;
            }
        } else if (str.equals("audio/ogg")) {
            c = 2;
        }
        if (c == 0) {
            return ".mp4";
        }
        if (c != 1) {
            return c != 2 ? "" : ".ogg";
        }
        return ".mkv";
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
        int lastIndexOf;
        String str2 = "";
        if (tLObject instanceof TLRPC$Document) {
            TLRPC$Document tLRPC$Document = (TLRPC$Document) tLObject;
            String documentFileName = getDocumentFileName(tLRPC$Document);
            if (!(documentFileName == null || (lastIndexOf = documentFileName.lastIndexOf(46)) == -1)) {
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
                    FileLoader.lambda$deleteFiles$10(this.f$0, this.f$1);
                }
            });
        }
    }

    static /* synthetic */ void lambda$deleteFiles$10(ArrayList arrayList, int i) {
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
}
