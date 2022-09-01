package org.telegram.messenger;

import android.text.TextUtils;
import android.util.SparseArray;
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
import org.telegram.tgnet.TLRPC$TL_secureFile;
import org.telegram.tgnet.TLRPC$TL_videoSize;
import org.telegram.tgnet.TLRPC$UserProfilePhoto;
import org.telegram.tgnet.TLRPC$WebDocument;

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
    public static final int PRIORITY_HIGH = 3;
    public static final int PRIORITY_LOW = 0;
    public static final int PRIORITY_NORMAL = 1;
    public static final int PRIORITY_NORMAL_UP = 2;
    private static final int PRIORITY_STREAM = 4;
    /* access modifiers changed from: private */
    public static volatile DispatchQueue fileLoaderQueue = new DispatchQueue("fileUploadQueue");
    private static SparseArray<File> mediaDirs = null;
    private ArrayList<FileLoadOperation> activeFileLoadOperation = new ArrayList<>();
    private final FileLoaderPriorityQueue audioQueue = new FileLoaderPriorityQueue("audioQueue queue", 3);
    /* access modifiers changed from: private */
    public int currentUploadOperationsCount = 0;
    /* access modifiers changed from: private */
    public int currentUploadSmallOperationsCount = 0;
    /* access modifiers changed from: private */
    public FileLoaderDelegate delegate = null;
    private final FilePathDatabase filePathDatabase;
    private final FileLoaderPriorityQueue filesQueue = new FileLoaderPriorityQueue("files queue", 3);
    private String forceLoadingFile;
    private final FileLoaderPriorityQueue imagesQueue = new FileLoaderPriorityQueue("imagesQueue queue", 6);
    private final FileLoaderPriorityQueue largeFilesQueue = new FileLoaderPriorityQueue("large files queue", 1);
    private int lastReferenceId;
    private ConcurrentHashMap<String, FileLoadOperation> loadOperationPaths = new ConcurrentHashMap<>();
    /* access modifiers changed from: private */
    public ConcurrentHashMap<String, LoadOperationUIObject> loadOperationPathsUI = new ConcurrentHashMap<>(10, 1.0f, 2);
    private HashMap<String, Boolean> loadingVideos = new HashMap<>();
    private ConcurrentHashMap<Integer, Object> parentObjectReferences = new ConcurrentHashMap<>();
    private int priorityIncreasePointer;
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

        void fileDidUploaded(String str, TLRPC$InputFile tLRPC$InputFile, TLRPC$InputEncryptedFile tLRPC$InputEncryptedFile, byte[] bArr, byte[] bArr2, long j);

        void fileLoadProgressChanged(FileLoadOperation fileLoadOperation, String str, long j, long j2);

        void fileUploadProgressChanged(FileUploadOperation fileUploadOperation, String str, long j, long j2, boolean z);
    }

    public interface FileResolver {
        File getFile();
    }

    static /* synthetic */ int access$1008(FileLoader fileLoader) {
        int i = fileLoader.currentUploadOperationsCount;
        fileLoader.currentUploadOperationsCount = i + 1;
        return i;
    }

    static /* synthetic */ int access$1010(FileLoader fileLoader) {
        int i = fileLoader.currentUploadOperationsCount;
        fileLoader.currentUploadOperationsCount = i - 1;
        return i;
    }

    static /* synthetic */ int access$808(FileLoader fileLoader) {
        int i = fileLoader.currentUploadSmallOperationsCount;
        fileLoader.currentUploadSmallOperationsCount = i + 1;
        return i;
    }

    static /* synthetic */ int access$810(FileLoader fileLoader) {
        int i = fileLoader.currentUploadSmallOperationsCount;
        fileLoader.currentUploadSmallOperationsCount = i - 1;
        return i;
    }

    private int getPriorityValue(int i) {
        if (i == 4) {
            return Integer.MAX_VALUE;
        }
        if (i == 3) {
            int i2 = this.priorityIncreasePointer + 1;
            this.priorityIncreasePointer = i2;
            return i2 + 1048576;
        } else if (i == 2) {
            int i3 = this.priorityIncreasePointer + 1;
            this.priorityIncreasePointer = i3;
            return i3 + 65536;
        } else if (i == 1) {
            return 65536;
        } else {
            return 0;
        }
    }

    public static FileLoader getInstance(int i) {
        FileLoader[] fileLoaderArr = Instance;
        FileLoader fileLoader = fileLoaderArr[i];
        if (fileLoader == null) {
            synchronized (FileLoader.class) {
                fileLoader = fileLoaderArr[i];
                if (fileLoader == null) {
                    fileLoader = new FileLoader(i);
                    fileLoaderArr[i] = fileLoader;
                }
            }
        }
        return fileLoader;
    }

    public FileLoader(int i) {
        super(i);
        this.filePathDatabase = new FilePathDatabase(i);
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

    public void uploadFile(String str, boolean z, boolean z2, long j, int i, boolean z3) {
        if (str != null) {
            fileLoaderQueue.postRunnable(new FileLoader$$ExternalSyntheticLambda11(this, z, str, j, i, z3, z2));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$uploadFile$5(boolean z, String str, long j, int i, boolean z2, boolean z3) {
        long j2;
        final boolean z4 = z;
        final String str2 = str;
        final boolean z5 = z3;
        if (z4) {
            if (this.uploadOperationPathsEnc.containsKey(str2)) {
                return;
            }
        } else if (this.uploadOperationPaths.containsKey(str2)) {
            return;
        }
        if (j == 0 || this.uploadSizes.get(str2) == null) {
            j2 = j;
        } else {
            this.uploadSizes.remove(str2);
            j2 = 0;
        }
        FileUploadOperation fileUploadOperation = new FileUploadOperation(this.currentAccount, str, z, j2, i);
        FileLoaderDelegate fileLoaderDelegate = this.delegate;
        if (!(fileLoaderDelegate == null || j == 0)) {
            fileLoaderDelegate.fileUploadProgressChanged(fileUploadOperation, str, 0, j, z);
        }
        if (z4) {
            this.uploadOperationPathsEnc.put(str2, fileUploadOperation);
        } else {
            this.uploadOperationPaths.put(str2, fileUploadOperation);
        }
        if (z2) {
            fileUploadOperation.setForceSmallFile();
        }
        fileUploadOperation.setDelegate(new FileUploadOperation.FileUploadOperationDelegate() {
            public void didFinishUploadingFile(FileUploadOperation fileUploadOperation, TLRPC$InputFile tLRPC$InputFile, TLRPC$InputEncryptedFile tLRPC$InputEncryptedFile, byte[] bArr, byte[] bArr2) {
                FileLoader.fileLoaderQueue.postRunnable(new FileLoader$1$$ExternalSyntheticLambda1(this, z4, str2, z5, tLRPC$InputFile, tLRPC$InputEncryptedFile, bArr, bArr2, fileUploadOperation));
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
                    FileLoader.access$810(FileLoader.this);
                    if (FileLoader.this.currentUploadSmallOperationsCount < 1 && (fileUploadOperation3 = (FileUploadOperation) FileLoader.this.uploadSmallOperationQueue.poll()) != null) {
                        FileLoader.access$808(FileLoader.this);
                        fileUploadOperation3.start();
                    }
                } else {
                    FileLoader.access$1010(FileLoader.this);
                    if (FileLoader.this.currentUploadOperationsCount < 1 && (fileUploadOperation2 = (FileUploadOperation) FileLoader.this.uploadOperationQueue.poll()) != null) {
                        FileLoader.access$1008(FileLoader.this);
                        fileUploadOperation2.start();
                    }
                }
                if (FileLoader.this.delegate != null) {
                    FileLoader.this.delegate.fileDidUploaded(str, tLRPC$InputFile, tLRPC$InputEncryptedFile, bArr, bArr2, fileUploadOperation.getTotalFileSize());
                }
            }

            public void didFailedUploadingFile(FileUploadOperation fileUploadOperation) {
                FileLoader.fileLoaderQueue.postRunnable(new FileLoader$1$$ExternalSyntheticLambda0(this, z4, str2, z5));
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
                    FileLoader.access$810(FileLoader.this);
                    if (FileLoader.this.currentUploadSmallOperationsCount < 1 && (fileUploadOperation2 = (FileUploadOperation) FileLoader.this.uploadSmallOperationQueue.poll()) != null) {
                        FileLoader.access$808(FileLoader.this);
                        fileUploadOperation2.start();
                        return;
                    }
                    return;
                }
                FileLoader.access$1010(FileLoader.this);
                if (FileLoader.this.currentUploadOperationsCount < 1 && (fileUploadOperation = (FileUploadOperation) FileLoader.this.uploadOperationQueue.poll()) != null) {
                    FileLoader.access$1008(FileLoader.this);
                    fileUploadOperation.start();
                }
            }

            public void didChangedUploadProgress(FileUploadOperation fileUploadOperation, long j, long j2) {
                if (FileLoader.this.delegate != null) {
                    FileLoader.this.delegate.fileUploadProgressChanged(fileUploadOperation, str2, j, j2, z4);
                }
            }
        });
        if (z5) {
            int i2 = this.currentUploadSmallOperationsCount;
            if (i2 < 1) {
                this.currentUploadSmallOperationsCount = i2 + 1;
                fileUploadOperation.start();
                return;
            }
            this.uploadSmallOperationQueue.add(fileUploadOperation);
            return;
        }
        int i3 = this.currentUploadOperationsCount;
        if (i3 < 1) {
            this.currentUploadOperationsCount = i3 + 1;
            fileUploadOperation.start();
            return;
        }
        this.uploadOperationQueue.add(fileUploadOperation);
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
            fileLoadOperation.setPriority(getPriorityValue(4));
            fileLoadOperation.getQueue().add(fileLoadOperation);
            fileLoadOperation.getQueue().checkLoadingOperations();
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
            LoadOperationUIObject remove = this.loadOperationPathsUI.remove(str2);
            Runnable runnable = remove != null ? remove.loadInternalRunnable : null;
            boolean z2 = remove != null;
            if (runnable != null) {
                fileLoaderQueue.cancelRunnable(runnable);
            }
            fileLoaderQueue.postRunnable(new FileLoader$$ExternalSyntheticLambda2(this, str2));
            if (z2 && tLRPC$Document != null) {
                AndroidUtilities.runOnUIThread(new FileLoader$$ExternalSyntheticLambda1(this));
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$cancelLoadFile$7(String str) {
        FileLoadOperation remove = this.loadOperationPaths.remove(str);
        if (remove != null) {
            remove.getQueue().cancel(remove);
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
        TLRPC$Document tLRPC$Document2 = tLRPC$Document;
        if (tLRPC$Document2 != null) {
            loadFile(tLRPC$Document, (SecureDocument) null, (WebFile) null, (TLRPC$TL_fileLocationToBeDeprecated) null, (ImageLocation) null, obj, (String) null, 0, i, (i2 != 0 || tLRPC$Document2.key == null) ? i2 : 1);
        }
    }

    public void loadFile(WebFile webFile, int i, int i2) {
        loadFile((TLRPC$Document) null, (SecureDocument) null, webFile, (TLRPC$TL_fileLocationToBeDeprecated) null, (ImageLocation) null, (Object) null, (String) null, 0, i, i2);
    }

    /*  JADX ERROR: JadxRuntimeException in pass: IfRegionVisitor
        jadx.core.utils.exceptions.JadxRuntimeException: Don't wrap MOVE or CONST insns: 0x01c4: MOVE  (r0v11 org.telegram.messenger.ImageLocation) = (r39v0 org.telegram.messenger.ImageLocation)
        	at jadx.core.dex.instructions.args.InsnArg.wrapArg(InsnArg.java:164)
        	at jadx.core.dex.visitors.shrink.CodeShrinkVisitor.assignInline(CodeShrinkVisitor.java:133)
        	at jadx.core.dex.visitors.shrink.CodeShrinkVisitor.checkInline(CodeShrinkVisitor.java:118)
        	at jadx.core.dex.visitors.shrink.CodeShrinkVisitor.shrinkBlock(CodeShrinkVisitor.java:65)
        	at jadx.core.dex.visitors.shrink.CodeShrinkVisitor.shrinkMethod(CodeShrinkVisitor.java:43)
        	at jadx.core.dex.visitors.regions.TernaryMod.makeTernaryInsn(TernaryMod.java:122)
        	at jadx.core.dex.visitors.regions.TernaryMod.visitRegion(TernaryMod.java:34)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseIterativeStepInternal(DepthRegionTraversal.java:73)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseIterativeStepInternal(DepthRegionTraversal.java:78)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseIterativeStepInternal(DepthRegionTraversal.java:78)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseIterativeStepInternal(DepthRegionTraversal.java:78)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseIterativeStepInternal(DepthRegionTraversal.java:78)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseIterativeStepInternal(DepthRegionTraversal.java:78)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseIterativeStepInternal(DepthRegionTraversal.java:78)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseIterativeStepInternal(DepthRegionTraversal.java:78)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseIterativeStepInternal(DepthRegionTraversal.java:78)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseIterativeStepInternal(DepthRegionTraversal.java:78)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseIterativeStepInternal(DepthRegionTraversal.java:78)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseIterativeStepInternal(DepthRegionTraversal.java:78)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseIterative(DepthRegionTraversal.java:27)
        	at jadx.core.dex.visitors.regions.IfRegionVisitor.visit(IfRegionVisitor.java:31)
        */
    /* JADX WARNING: Removed duplicated region for block: B:122:0x0241  */
    /* JADX WARNING: Removed duplicated region for block: B:143:0x0290  */
    /* JADX WARNING: Removed duplicated region for block: B:150:0x02b6  */
    /* JADX WARNING: Removed duplicated region for block: B:153:0x02e1  */
    /* JADX WARNING: Removed duplicated region for block: B:157:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:15:0x0044  */
    private org.telegram.messenger.FileLoadOperation loadFileInternal(org.telegram.tgnet.TLRPC$Document r35, org.telegram.messenger.SecureDocument r36, org.telegram.messenger.WebFile r37, org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated r38, org.telegram.messenger.ImageLocation r39, java.lang.Object r40, java.lang.String r41, long r42, int r44, org.telegram.messenger.FileLoadOperationStream r45, long r46, boolean r48, int r49) {
        /*
            r34 = this;
            r6 = r34
            r7 = r35
            r0 = r36
            r1 = r37
            r2 = r38
            r3 = r39
            r4 = r40
            r14 = r45
            r12 = r46
            r15 = r48
            r5 = r49
            if (r2 == 0) goto L_0x0020
            r11 = r41
            java.lang.String r9 = getAttachFileName(r2, r11)
        L_0x001e:
            r10 = r9
            goto L_0x0038
        L_0x0020:
            r11 = r41
            if (r0 == 0) goto L_0x0029
            java.lang.String r9 = getAttachFileName(r36)
            goto L_0x001e
        L_0x0029:
            if (r7 == 0) goto L_0x0030
            java.lang.String r9 = getAttachFileName(r35)
            goto L_0x001e
        L_0x0030:
            if (r1 == 0) goto L_0x0037
            java.lang.String r9 = getAttachFileName(r37)
            goto L_0x001e
        L_0x0037:
            r10 = 0
        L_0x0038:
            if (r10 == 0) goto L_0x0308
            java.lang.String r9 = "-NUM"
            boolean r16 = r10.contains(r9)
            if (r16 == 0) goto L_0x0044
            goto L_0x0308
        L_0x0044:
            r8 = 10
            if (r5 == r8) goto L_0x005f
            boolean r17 = android.text.TextUtils.isEmpty(r10)
            if (r17 != 0) goto L_0x005f
            boolean r9 = r10.contains(r9)
            if (r9 != 0) goto L_0x005f
            j$.util.concurrent.ConcurrentHashMap<java.lang.String, org.telegram.messenger.FileLoader$LoadOperationUIObject> r9 = r6.loadOperationPathsUI
            org.telegram.messenger.FileLoader$LoadOperationUIObject r8 = new org.telegram.messenger.FileLoader$LoadOperationUIObject
            r11 = 0
            r8.<init>()
            r9.put(r10, r8)
        L_0x005f:
            if (r7 == 0) goto L_0x0079
            boolean r8 = r4 instanceof org.telegram.messenger.MessageObject
            if (r8 == 0) goto L_0x0079
            r8 = r4
            org.telegram.messenger.MessageObject r8 = (org.telegram.messenger.MessageObject) r8
            boolean r9 = r8.putInDownloadsStore
            if (r9 == 0) goto L_0x0079
            boolean r9 = r8.isAnyKindOfSticker()
            if (r9 != 0) goto L_0x0079
            org.telegram.messenger.DownloadController r9 = r34.getDownloadController()
            r9.startDownloadFile(r7, r8)
        L_0x0079:
            j$.util.concurrent.ConcurrentHashMap<java.lang.String, org.telegram.messenger.FileLoadOperation> r8 = r6.loadOperationPaths
            java.lang.Object r8 = r8.get(r10)
            org.telegram.messenger.FileLoadOperation r8 = (org.telegram.messenger.FileLoadOperation) r8
            boolean r9 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            java.lang.String r11 = " documentName="
            if (r9 == 0) goto L_0x00ad
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r3 = "checkFile operation fileName="
            r9.append(r3)
            r9.append(r10)
            r9.append(r11)
            java.lang.String r3 = getDocumentFileName(r35)
            r9.append(r3)
            java.lang.String r3 = " operation="
            r9.append(r3)
            r9.append(r8)
            java.lang.String r3 = r9.toString()
            org.telegram.messenger.FileLog.d(r3)
        L_0x00ad:
            if (r14 == 0) goto L_0x00b1
            r9 = 4
            goto L_0x00b3
        L_0x00b1:
            r9 = r44
        L_0x00b3:
            int r9 = r6.getPriorityValue(r9)
            if (r8 == 0) goto L_0x00e9
            r3 = 10
            if (r5 == r3) goto L_0x00c8
            boolean r0 = r8.isPreloadVideoOperation()
            if (r0 == 0) goto L_0x00c8
            r0 = 0
            r8.setIsPreloadVideoOperation(r0)
            goto L_0x00c9
        L_0x00c8:
            r0 = 0
        L_0x00c9:
            if (r9 <= 0) goto L_0x00cd
            r3 = 1
            goto L_0x00ce
        L_0x00cd:
            r3 = 0
        L_0x00ce:
            r8.setForceRequest(r3)
            r8.setPriority(r9)
            r8.setStream(r14, r15, r12)
            org.telegram.messenger.FileLoaderPriorityQueue r0 = r8.getQueue()
            r0.add(r8)
            r8.updateProgress()
            org.telegram.messenger.FileLoaderPriorityQueue r0 = r8.getQueue()
            r0.checkLoadingOperations()
            return r8
        L_0x00e9:
            r3 = 10
            r16 = 0
            r17 = 4
            java.io.File r21 = getDirectory(r17)
            r18 = 3
            r19 = 0
            if (r0 == 0) goto L_0x010f
            org.telegram.messenger.FileLoadOperation r8 = new org.telegram.messenger.FileLoadOperation
            r8.<init>(r0)
            r13 = r8
            r25 = r10
            r26 = r11
            r10 = r19
            r3 = 1
            r8 = 3
            r12 = 0
            r33 = r9
            r9 = r4
            r4 = r33
            goto L_0x01ba
        L_0x010f:
            if (r2 == 0) goto L_0x013b
            long r3 = r2.volume_id
            int r8 = r2.dc_id
            org.telegram.messenger.FileLoadOperation r17 = new org.telegram.messenger.FileLoadOperation
            r23 = r3
            r18 = r8
            r3 = 10
            r8 = r17
            r4 = r9
            r9 = r39
            r25 = r10
            r10 = r40
            r26 = r11
            r11 = r41
            r12 = r42
            r8.<init>(r9, r10, r11, r12)
            r9 = r40
            r13 = r17
            r12 = r18
            r10 = r23
            r3 = 1
            r8 = 0
            goto L_0x01ba
        L_0x013b:
            r4 = r9
            r25 = r10
            r26 = r11
            r3 = 10
            if (r7 == 0) goto L_0x017a
            org.telegram.messenger.FileLoadOperation r8 = new org.telegram.messenger.FileLoadOperation
            r9 = r40
            r8.<init>((org.telegram.tgnet.TLRPC$Document) r7, (java.lang.Object) r9)
            boolean r10 = org.telegram.messenger.MessageObject.isVoiceDocument(r35)
            if (r10 == 0) goto L_0x0157
            r10 = r19
            r12 = 0
            r18 = 1
            goto L_0x0168
        L_0x0157:
            boolean r10 = org.telegram.messenger.MessageObject.isVideoDocument(r35)
            if (r10 == 0) goto L_0x0164
            long r10 = r7.id
            int r12 = r7.dc_id
            r18 = 2
            goto L_0x0168
        L_0x0164:
            long r10 = r7.id
            int r12 = r7.dc_id
        L_0x0168:
            boolean r13 = org.telegram.messenger.MessageObject.isRoundVideoDocument(r35)
            if (r13 == 0) goto L_0x0175
            r13 = r8
            r8 = r18
            r10 = r19
            r3 = 1
            goto L_0x01b9
        L_0x0175:
            r13 = r8
            r8 = r18
            r3 = 1
            goto L_0x01ba
        L_0x017a:
            r9 = r40
            if (r1 == 0) goto L_0x01b4
            org.telegram.messenger.FileLoadOperation r8 = new org.telegram.messenger.FileLoadOperation
            int r10 = r6.currentAccount
            r8.<init>((int) r10, (org.telegram.messenger.WebFile) r1)
            org.telegram.tgnet.TLRPC$InputWebFileLocation r10 = r1.location
            if (r10 == 0) goto L_0x018a
            goto L_0x01b4
        L_0x018a:
            boolean r10 = org.telegram.messenger.MessageObject.isVoiceWebDocument(r37)
            if (r10 == 0) goto L_0x0196
            r13 = r8
            r10 = r19
            r3 = 1
            r8 = 1
            goto L_0x01b9
        L_0x0196:
            boolean r10 = org.telegram.messenger.MessageObject.isVideoWebDocument(r37)
            if (r10 == 0) goto L_0x01a2
            r13 = r8
            r10 = r19
            r3 = 1
            r8 = 2
            goto L_0x01b9
        L_0x01a2:
            boolean r10 = org.telegram.messenger.MessageObject.isImageWebDocument(r37)
            if (r10 == 0) goto L_0x01ae
            r13 = r8
            r10 = r19
            r3 = 1
            r8 = 0
            goto L_0x01b9
        L_0x01ae:
            r13 = r8
            r10 = r19
            r3 = 1
            r8 = 3
            goto L_0x01b9
        L_0x01b4:
            r13 = r8
            r10 = r19
            r3 = 1
            r8 = 4
        L_0x01b9:
            r12 = 0
        L_0x01ba:
            if (r8 != r3) goto L_0x01c0
            org.telegram.messenger.FileLoaderPriorityQueue r0 = r6.audioQueue
        L_0x01be:
            r3 = r0
            goto L_0x01fc
        L_0x01c0:
            if (r0 != 0) goto L_0x01f9
            if (r2 == 0) goto L_0x01cd
            r0 = r39
            if (r0 == 0) goto L_0x01f9
            int r0 = r0.imageType
            r2 = 2
            if (r0 != r2) goto L_0x01f9
        L_0x01cd:
            boolean r0 = org.telegram.messenger.MessageObject.isImageWebDocument(r37)
            if (r0 != 0) goto L_0x01f9
            boolean r0 = org.telegram.messenger.MessageObject.isStickerDocument(r35)
            if (r0 != 0) goto L_0x01f9
            r0 = 1
            boolean r1 = org.telegram.messenger.MessageObject.isAnimatedStickerDocument(r7, r0)
            if (r1 != 0) goto L_0x01f9
            boolean r0 = org.telegram.messenger.MessageObject.isVideoStickerDocument(r35)
            if (r0 == 0) goto L_0x01e7
            goto L_0x01f9
        L_0x01e7:
            if (r7 == 0) goto L_0x01f6
            long r0 = r7.size
            r2 = 20971520(0x1400000, double:1.03613076E-316)
            int r17 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r17 <= 0) goto L_0x01f3
            goto L_0x01f6
        L_0x01f3:
            org.telegram.messenger.FileLoaderPriorityQueue r0 = r6.filesQueue
            goto L_0x01be
        L_0x01f6:
            org.telegram.messenger.FileLoaderPriorityQueue r0 = r6.largeFilesQueue
            goto L_0x01be
        L_0x01f9:
            org.telegram.messenger.FileLoaderPriorityQueue r0 = r6.imagesQueue
            goto L_0x01be
        L_0x01fc:
            if (r5 == 0) goto L_0x020e
            r0 = 10
            if (r5 != r0) goto L_0x0203
            goto L_0x020e
        L_0x0203:
            r0 = 2
            if (r5 != r0) goto L_0x020a
            r0 = 1
            r13.setEncryptFile(r0)
        L_0x020a:
            r20 = r21
            goto L_0x02a3
        L_0x020e:
            int r0 = (r10 > r19 ? 1 : (r10 == r19 ? 0 : -1))
            if (r0 == 0) goto L_0x029d
            org.telegram.messenger.FilePathDatabase r27 = r34.getFileDatabase()
            r32 = 1
            r28 = r10
            r30 = r12
            r31 = r8
            java.lang.String r0 = r27.getPath(r28, r30, r31, r32)
            if (r0 == 0) goto L_0x023a
            java.io.File r1 = new java.io.File
            r1.<init>(r0)
            boolean r0 = r1.exists()
            if (r0 == 0) goto L_0x023a
            java.lang.String r0 = r1.getName()
            java.io.File r1 = r1.getParentFile()
            r2 = r0
            r0 = 1
            goto L_0x023f
        L_0x023a:
            r1 = r21
            r2 = r25
            r0 = 0
        L_0x023f:
            if (r0 != 0) goto L_0x0298
            java.io.File r0 = getDirectory(r8)
            if (r8 == 0) goto L_0x024a
            r1 = 2
            if (r8 != r1) goto L_0x0265
        L_0x024a:
            boolean r1 = r6.canSaveToPublicStorage(r9)
            if (r1 == 0) goto L_0x0265
            if (r8 != 0) goto L_0x0259
            r1 = 100
            java.io.File r1 = getDirectory(r1)
            goto L_0x025f
        L_0x0259:
            r1 = 101(0x65, float:1.42E-43)
            java.io.File r1 = getDirectory(r1)
        L_0x025f:
            if (r1 == 0) goto L_0x028b
            r0 = r1
            r16 = 1
            goto L_0x028b
        L_0x0265:
            java.lang.String r1 = getDocumentFileName(r35)
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x028b
            boolean r1 = r6.canSaveAsFile(r9)
            if (r1 == 0) goto L_0x028b
            java.lang.String r1 = getDocumentFileName(r35)
            r2 = 5
            java.io.File r2 = getDirectory(r2)
            if (r2 == 0) goto L_0x0285
            r0 = r1
            r1 = r2
            r16 = 1
            goto L_0x028e
        L_0x0285:
            r33 = r1
            r1 = r0
            r0 = r33
            goto L_0x028e
        L_0x028b:
            r1 = r0
            r0 = r25
        L_0x028e:
            if (r16 == 0) goto L_0x0297
            org.telegram.messenger.FilePathDatabase$PathData r2 = new org.telegram.messenger.FilePathDatabase$PathData
            r2.<init>(r10, r12, r8)
            r13.pathSaveData = r2
        L_0x0297:
            r2 = r0
        L_0x0298:
            r20 = r1
            r22 = r2
            goto L_0x02a5
        L_0x029d:
            java.io.File r0 = getDirectory(r8)
            r20 = r0
        L_0x02a3:
            r22 = r25
        L_0x02a5:
            int r0 = r6.currentAccount
            r16 = r13
            r17 = r0
            r18 = r25
            r19 = r3
            r16.setPaths(r17, r18, r19, r20, r21, r22)
            r0 = 10
            if (r5 != r0) goto L_0x02ba
            r0 = 1
            r13.setIsPreloadVideoOperation(r0)
        L_0x02ba:
            org.telegram.messenger.FileLoader$2 r10 = new org.telegram.messenger.FileLoader$2
            r0 = r10
            r1 = r34
            r2 = r35
            r11 = r3
            r3 = r40
            r9 = r4
            r4 = r25
            r5 = r8
            r0.<init>(r2, r3, r4, r5)
            r13.setDelegate(r10)
            j$.util.concurrent.ConcurrentHashMap<java.lang.String, org.telegram.messenger.FileLoadOperation> r0 = r6.loadOperationPaths
            r8 = r25
            r0.put(r8, r13)
            r13.setPriority(r9)
            r0 = r46
            r13.setStream(r14, r15, r0)
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x0301
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "loadFileInternal fileName="
            r0.append(r1)
            r0.append(r8)
            r1 = r26
            r0.append(r1)
            java.lang.String r1 = getDocumentFileName(r35)
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.d(r0)
        L_0x0301:
            r11.add(r13)
            r11.checkLoadingOperations()
            return r13
        L_0x0308:
            r0 = 0
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.FileLoader.loadFileInternal(org.telegram.tgnet.TLRPC$Document, org.telegram.messenger.SecureDocument, org.telegram.messenger.WebFile, org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated, org.telegram.messenger.ImageLocation, java.lang.Object, java.lang.String, long, int, org.telegram.messenger.FileLoadOperationStream, long, boolean, int):org.telegram.messenger.FileLoadOperation");
    }

    private boolean canSaveAsFile(Object obj) {
        if (!(obj instanceof MessageObject) || !((MessageObject) obj).isDocument()) {
            return false;
        }
        return true;
    }

    private boolean canSaveToPublicStorage(Object obj) {
        int i;
        if (SharedConfig.saveToGalleryFlags != 0 && !BuildVars.NO_SCOPED_STORAGE && (obj instanceof MessageObject)) {
            MessageObject messageObject = (MessageObject) obj;
            long dialogId = messageObject.getDialogId();
            if (!messageObject.isRoundVideo() && !messageObject.isVoice() && !messageObject.isAnyKindOfSticker()) {
                long j = -dialogId;
                if (!getMessagesController().isChatNoForwards(getMessagesController().getChat(Long.valueOf(j))) && !messageObject.messageOwner.noforwards && !DialogObject.isEncryptedDialog(dialogId)) {
                    if (dialogId >= 0) {
                        i = 1;
                    } else {
                        i = ChatObject.isChannelAndNotMegaGroup(getMessagesController().getChat(Long.valueOf(j))) ? 4 : 2;
                    }
                    if ((i & SharedConfig.saveToGalleryFlags) != 0) {
                        return true;
                    }
                }
            }
        }
        return false;
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

    /* JADX WARNING: Removed duplicated region for block: B:16:0x004e  */
    /* JADX WARNING: Removed duplicated region for block: B:17:0x005d  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void loadFile(org.telegram.tgnet.TLRPC$Document r17, org.telegram.messenger.SecureDocument r18, org.telegram.messenger.WebFile r19, org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated r20, org.telegram.messenger.ImageLocation r21, java.lang.Object r22, java.lang.String r23, long r24, int r26, int r27) {
        /*
            r16 = this;
            r5 = r20
            r13 = 0
            if (r5 == 0) goto L_0x000d
            r8 = r23
            java.lang.String r0 = getAttachFileName(r5, r8)
        L_0x000b:
            r14 = r0
            goto L_0x001e
        L_0x000d:
            r8 = r23
            if (r17 == 0) goto L_0x0016
            java.lang.String r0 = getAttachFileName(r17)
            goto L_0x000b
        L_0x0016:
            if (r19 == 0) goto L_0x001d
            java.lang.String r0 = getAttachFileName(r19)
            goto L_0x000b
        L_0x001d:
            r14 = r13
        L_0x001e:
            org.telegram.messenger.FileLoader$$ExternalSyntheticLambda5 r15 = new org.telegram.messenger.FileLoader$$ExternalSyntheticLambda5
            r0 = r15
            r1 = r16
            r2 = r17
            r3 = r18
            r4 = r19
            r5 = r20
            r6 = r21
            r7 = r22
            r8 = r23
            r9 = r24
            r11 = r26
            r12 = r27
            r0.<init>(r1, r2, r3, r4, r5, r6, r7, r8, r9, r11, r12)
            r0 = 10
            r1 = r27
            if (r1 == r0) goto L_0x005d
            boolean r0 = android.text.TextUtils.isEmpty(r14)
            if (r0 != 0) goto L_0x005d
            java.lang.String r0 = "-NUM"
            boolean r0 = r14.contains(r0)
            if (r0 != 0) goto L_0x005d
            org.telegram.messenger.FileLoader$LoadOperationUIObject r0 = new org.telegram.messenger.FileLoader$LoadOperationUIObject
            r0.<init>()
            r0.loadInternalRunnable = r15
            r1 = r16
            j$.util.concurrent.ConcurrentHashMap<java.lang.String, org.telegram.messenger.FileLoader$LoadOperationUIObject> r2 = r1.loadOperationPathsUI
            r2.put(r14, r0)
            goto L_0x005f
        L_0x005d:
            r1 = r16
        L_0x005f:
            org.telegram.messenger.DispatchQueue r0 = fileLoaderQueue
            r0.postRunnable(r15)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.FileLoader.loadFile(org.telegram.tgnet.TLRPC$Document, org.telegram.messenger.SecureDocument, org.telegram.messenger.WebFile, org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated, org.telegram.messenger.ImageLocation, java.lang.Object, java.lang.String, long, int, int):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadFile$9(TLRPC$Document tLRPC$Document, SecureDocument secureDocument, WebFile webFile, TLRPC$TL_fileLocationToBeDeprecated tLRPC$TL_fileLocationToBeDeprecated, ImageLocation imageLocation, Object obj, String str, long j, int i, int i2) {
        loadFileInternal(tLRPC$Document, secureDocument, webFile, tLRPC$TL_fileLocationToBeDeprecated, imageLocation, obj, str, j, i, (FileLoadOperationStream) null, 0, false, i2);
    }

    /* access modifiers changed from: protected */
    public FileLoadOperation loadStreamFile(FileLoadOperationStream fileLoadOperationStream, TLRPC$Document tLRPC$Document, ImageLocation imageLocation, Object obj, long j, boolean z) {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        FileLoadOperation[] fileLoadOperationArr = new FileLoadOperation[1];
        fileLoaderQueue.postRunnable(new FileLoader$$ExternalSyntheticLambda13(this, fileLoadOperationArr, tLRPC$Document, imageLocation, obj, fileLoadOperationStream, j, z, countDownLatch));
        try {
            countDownLatch.await();
        } catch (Exception e) {
            FileLog.e((Throwable) e, false);
        }
        return fileLoadOperationArr[0];
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadStreamFile$10(FileLoadOperation[] fileLoadOperationArr, TLRPC$Document tLRPC$Document, ImageLocation imageLocation, Object obj, FileLoadOperationStream fileLoadOperationStream, long j, boolean z, CountDownLatch countDownLatch) {
        ImageLocation imageLocation2 = imageLocation;
        String str = null;
        TLRPC$TL_fileLocationToBeDeprecated tLRPC$TL_fileLocationToBeDeprecated = (tLRPC$Document != null || imageLocation2 == null) ? null : imageLocation2.location;
        if (tLRPC$Document == null && imageLocation2 != null) {
            str = "mp4";
        }
        fileLoadOperationArr[0] = loadFileInternal(tLRPC$Document, (SecureDocument) null, (WebFile) null, tLRPC$TL_fileLocationToBeDeprecated, imageLocation, obj, str, (tLRPC$Document != null || imageLocation2 == null) ? 0 : imageLocation2.currentSize, 1, fileLoadOperationStream, j, z, tLRPC$Document == null ? 1 : 0);
        countDownLatch.countDown();
    }

    /* access modifiers changed from: private */
    public void checkDownloadQueue(FileLoaderPriorityQueue fileLoaderPriorityQueue, String str) {
        fileLoaderQueue.postRunnable(new FileLoader$$ExternalSyntheticLambda3(this, str, fileLoaderPriorityQueue));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$checkDownloadQueue$11(String str, FileLoaderPriorityQueue fileLoaderPriorityQueue) {
        fileLoaderPriorityQueue.remove(this.loadOperationPaths.remove(str));
        fileLoaderPriorityQueue.checkLoadingOperations();
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
        } else if (MessageObject.getMedia(tLRPC$Message) instanceof TLRPC$TL_messageMediaDocument) {
            return getAttachFileName(MessageObject.getMedia(tLRPC$Message).document);
        } else {
            if (MessageObject.getMedia(tLRPC$Message) instanceof TLRPC$TL_messageMediaPhoto) {
                ArrayList<TLRPC$PhotoSize> arrayList2 = MessageObject.getMedia(tLRPC$Message).photo.sizes;
                if (arrayList2.size() > 0 && (closestPhotoSizeWithSize2 = getClosestPhotoSizeWithSize(arrayList2, AndroidUtilities.getPhotoSize(), false, (TLRPC$PhotoSize) null, true)) != null) {
                    return getAttachFileName(closestPhotoSizeWithSize2);
                }
            } else if (MessageObject.getMedia(tLRPC$Message) instanceof TLRPC$TL_messageMediaWebPage) {
                if (MessageObject.getMedia(tLRPC$Message).webpage.document != null) {
                    return getAttachFileName(MessageObject.getMedia(tLRPC$Message).webpage.document);
                }
                if (MessageObject.getMedia(tLRPC$Message).webpage.photo != null) {
                    ArrayList<TLRPC$PhotoSize> arrayList3 = MessageObject.getMedia(tLRPC$Message).webpage.photo.sizes;
                    if (arrayList3.size() > 0 && (closestPhotoSizeWithSize = getClosestPhotoSizeWithSize(arrayList3, AndroidUtilities.getPhotoSize())) != null) {
                        return getAttachFileName(closestPhotoSizeWithSize);
                    }
                }
            } else if ((MessageObject.getMedia(tLRPC$Message) instanceof TLRPC$TL_messageMediaInvoice) && (tLRPC$WebDocument = ((TLRPC$TL_messageMediaInvoice) MessageObject.getMedia(tLRPC$Message)).photo) != null) {
                return Utilities.MD5(tLRPC$WebDocument.url) + "." + ImageLoader.getHttpUrlExtension(tLRPC$WebDocument.url, getMimeTypePart(tLRPC$WebDocument.mime_type));
            }
        }
        return "";
    }

    public File getPathToMessage(TLRPC$Message tLRPC$Message) {
        return getPathToMessage(tLRPC$Message, true);
    }

    public File getPathToMessage(TLRPC$Message tLRPC$Message, boolean z) {
        TLRPC$PhotoSize closestPhotoSizeWithSize;
        TLRPC$PhotoSize closestPhotoSizeWithSize2;
        TLRPC$PhotoSize closestPhotoSizeWithSize3;
        if (tLRPC$Message == null) {
            return new File("");
        }
        boolean z2 = false;
        if (tLRPC$Message instanceof TLRPC$TL_messageService) {
            TLRPC$Photo tLRPC$Photo = tLRPC$Message.action.photo;
            if (tLRPC$Photo != null) {
                ArrayList<TLRPC$PhotoSize> arrayList = tLRPC$Photo.sizes;
                if (arrayList.size() > 0 && (closestPhotoSizeWithSize3 = getClosestPhotoSizeWithSize(arrayList, AndroidUtilities.getPhotoSize())) != null) {
                    return getPathToAttach(closestPhotoSizeWithSize3, (String) null, false, z);
                }
            }
        } else if (MessageObject.getMedia(tLRPC$Message) instanceof TLRPC$TL_messageMediaDocument) {
            TLRPC$Document tLRPC$Document = MessageObject.getMedia(tLRPC$Message).document;
            if (MessageObject.getMedia(tLRPC$Message).ttl_seconds != 0) {
                z2 = true;
            }
            return getPathToAttach(tLRPC$Document, (String) null, z2, z);
        } else if (MessageObject.getMedia(tLRPC$Message) instanceof TLRPC$TL_messageMediaPhoto) {
            ArrayList<TLRPC$PhotoSize> arrayList2 = MessageObject.getMedia(tLRPC$Message).photo.sizes;
            if (arrayList2.size() > 0 && (closestPhotoSizeWithSize2 = getClosestPhotoSizeWithSize(arrayList2, AndroidUtilities.getPhotoSize(), false, (TLRPC$PhotoSize) null, true)) != null) {
                if (MessageObject.getMedia(tLRPC$Message).ttl_seconds != 0) {
                    z2 = true;
                }
                return getPathToAttach(closestPhotoSizeWithSize2, (String) null, z2, z);
            }
        } else if (MessageObject.getMedia(tLRPC$Message) instanceof TLRPC$TL_messageMediaWebPage) {
            if (MessageObject.getMedia(tLRPC$Message).webpage.document != null) {
                return getPathToAttach(MessageObject.getMedia(tLRPC$Message).webpage.document, (String) null, false, z);
            }
            if (MessageObject.getMedia(tLRPC$Message).webpage.photo != null) {
                ArrayList<TLRPC$PhotoSize> arrayList3 = MessageObject.getMedia(tLRPC$Message).webpage.photo.sizes;
                if (arrayList3.size() > 0 && (closestPhotoSizeWithSize = getClosestPhotoSizeWithSize(arrayList3, AndroidUtilities.getPhotoSize())) != null) {
                    return getPathToAttach(closestPhotoSizeWithSize, (String) null, false, z);
                }
            }
        } else if (MessageObject.getMedia(tLRPC$Message) instanceof TLRPC$TL_messageMediaInvoice) {
            return getPathToAttach(((TLRPC$TL_messageMediaInvoice) MessageObject.getMedia(tLRPC$Message)).photo, (String) null, true, z);
        }
        return new File("");
    }

    public File getPathToAttach(TLObject tLObject) {
        return getPathToAttach(tLObject, (String) null, false);
    }

    public File getPathToAttach(TLObject tLObject, boolean z) {
        return getPathToAttach(tLObject, (String) null, z);
    }

    public File getPathToAttach(TLObject tLObject, String str, boolean z) {
        return getPathToAttach(tLObject, (String) null, str, z, true);
    }

    public File getPathToAttach(TLObject tLObject, String str, boolean z, boolean z2) {
        return getPathToAttach(tLObject, (String) null, str, z, z2);
    }

    /* JADX WARNING: Removed duplicated region for block: B:101:0x0166  */
    /* JADX WARNING: Removed duplicated region for block: B:103:0x016e  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.io.File getPathToAttach(org.telegram.tgnet.TLObject r11, java.lang.String r12, java.lang.String r13, boolean r14, boolean r15) {
        /*
            r10 = this;
            r0 = 0
            r2 = 0
            r3 = 4
            r4 = 0
            if (r14 == 0) goto L_0x0010
            java.io.File r2 = getDirectory(r3)
        L_0x000b:
            r4 = r0
            r6 = 0
            r7 = 0
            goto L_0x0164
        L_0x0010:
            boolean r14 = r11 instanceof org.telegram.tgnet.TLRPC$Document
            r5 = 2
            r6 = 3
            r7 = 1
            if (r14 == 0) goto L_0x004c
            r12 = r11
            org.telegram.tgnet.TLRPC$Document r12 = (org.telegram.tgnet.TLRPC$Document) r12
            java.lang.String r14 = r12.localPath
            boolean r14 = android.text.TextUtils.isEmpty(r14)
            if (r14 != 0) goto L_0x002a
            java.io.File r11 = new java.io.File
            java.lang.String r12 = r12.localPath
            r11.<init>(r12)
            return r11
        L_0x002a:
            byte[] r14 = r12.key
            if (r14 == 0) goto L_0x002f
            goto L_0x0040
        L_0x002f:
            boolean r14 = org.telegram.messenger.MessageObject.isVoiceDocument(r12)
            if (r14 == 0) goto L_0x0037
            r3 = 1
            goto L_0x0040
        L_0x0037:
            boolean r14 = org.telegram.messenger.MessageObject.isVideoDocument(r12)
            if (r14 == 0) goto L_0x003f
            r3 = 2
            goto L_0x0040
        L_0x003f:
            r3 = 3
        L_0x0040:
            long r4 = r12.id
            int r12 = r12.dc_id
            java.io.File r2 = getDirectory(r3)
        L_0x0048:
            r6 = r12
            r7 = r3
            goto L_0x0164
        L_0x004c:
            boolean r14 = r11 instanceof org.telegram.tgnet.TLRPC$Photo
            if (r14 == 0) goto L_0x0061
            org.telegram.tgnet.TLRPC$Photo r11 = (org.telegram.tgnet.TLRPC$Photo) r11
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r11 = r11.sizes
            int r12 = org.telegram.messenger.AndroidUtilities.getPhotoSize()
            org.telegram.tgnet.TLRPC$PhotoSize r11 = getClosestPhotoSizeWithSize(r11, r12)
            java.io.File r11 = r10.getPathToAttach(r11, r13, r4, r15)
            return r11
        L_0x0061:
            boolean r14 = r11 instanceof org.telegram.tgnet.TLRPC$PhotoSize
            r8 = -2147483648(0xfffffffvar_, double:NaN)
            if (r14 == 0) goto L_0x009f
            r12 = r11
            org.telegram.tgnet.TLRPC$PhotoSize r12 = (org.telegram.tgnet.TLRPC$PhotoSize) r12
            boolean r14 = r12 instanceof org.telegram.tgnet.TLRPC$TL_photoStrippedSize
            if (r14 != 0) goto L_0x0097
            boolean r14 = r12 instanceof org.telegram.tgnet.TLRPC$TL_photoPathSize
            if (r14 == 0) goto L_0x0074
            goto L_0x0097
        L_0x0074:
            org.telegram.tgnet.TLRPC$FileLocation r14 = r12.location
            if (r14 == 0) goto L_0x0091
            byte[] r2 = r14.key
            if (r2 != 0) goto L_0x0091
            long r5 = r14.volume_id
            int r2 = (r5 > r8 ? 1 : (r5 == r8 ? 0 : -1))
            if (r2 != 0) goto L_0x0086
            int r14 = r14.local_id
            if (r14 < 0) goto L_0x0091
        L_0x0086:
            int r14 = r12.size
            if (r14 >= 0) goto L_0x008b
            goto L_0x0091
        L_0x008b:
            java.io.File r14 = getDirectory(r4)
            r2 = r14
            goto L_0x0097
        L_0x0091:
            java.io.File r14 = getDirectory(r3)
            r2 = r14
            goto L_0x0098
        L_0x0097:
            r3 = 0
        L_0x0098:
            org.telegram.tgnet.TLRPC$FileLocation r12 = r12.location
            long r4 = r12.volume_id
            int r12 = r12.dc_id
            goto L_0x0048
        L_0x009f:
            boolean r14 = r11 instanceof org.telegram.tgnet.TLRPC$TL_videoSize
            if (r14 == 0) goto L_0x00d1
            r12 = r11
            org.telegram.tgnet.TLRPC$TL_videoSize r12 = (org.telegram.tgnet.TLRPC$TL_videoSize) r12
            org.telegram.tgnet.TLRPC$FileLocation r14 = r12.location
            if (r14 == 0) goto L_0x00c4
            byte[] r2 = r14.key
            if (r2 != 0) goto L_0x00c4
            long r5 = r14.volume_id
            int r2 = (r5 > r8 ? 1 : (r5 == r8 ? 0 : -1))
            if (r2 != 0) goto L_0x00b8
            int r14 = r14.local_id
            if (r14 < 0) goto L_0x00c4
        L_0x00b8:
            int r14 = r12.size
            if (r14 >= 0) goto L_0x00bd
            goto L_0x00c4
        L_0x00bd:
            java.io.File r14 = getDirectory(r4)
            r2 = r14
            r3 = 0
            goto L_0x00c9
        L_0x00c4:
            java.io.File r14 = getDirectory(r3)
            r2 = r14
        L_0x00c9:
            org.telegram.tgnet.TLRPC$FileLocation r12 = r12.location
            long r4 = r12.volume_id
            int r12 = r12.dc_id
            goto L_0x0048
        L_0x00d1:
            boolean r14 = r11 instanceof org.telegram.tgnet.TLRPC$FileLocation
            if (r14 == 0) goto L_0x00fb
            r12 = r11
            org.telegram.tgnet.TLRPC$FileLocation r12 = (org.telegram.tgnet.TLRPC$FileLocation) r12
            byte[] r14 = r12.key
            if (r14 != 0) goto L_0x00ef
            long r5 = r12.volume_id
            int r14 = (r5 > r8 ? 1 : (r5 == r8 ? 0 : -1))
            if (r14 != 0) goto L_0x00e7
            int r14 = r12.local_id
            if (r14 >= 0) goto L_0x00e7
            goto L_0x00ef
        L_0x00e7:
            int r12 = r12.dc_id
            java.io.File r14 = getDirectory(r4)
            r2 = r14
            goto L_0x00f6
        L_0x00ef:
            java.io.File r12 = getDirectory(r3)
            r2 = r12
            r5 = r0
            r12 = 0
        L_0x00f6:
            r4 = r5
            r7 = 0
            r6 = r12
            goto L_0x0164
        L_0x00fb:
            boolean r14 = r11 instanceof org.telegram.tgnet.TLRPC$UserProfilePhoto
            if (r14 != 0) goto L_0x014d
            boolean r14 = r11 instanceof org.telegram.tgnet.TLRPC$ChatPhoto
            if (r14 == 0) goto L_0x0104
            goto L_0x014d
        L_0x0104:
            boolean r12 = r11 instanceof org.telegram.messenger.WebFile
            if (r12 == 0) goto L_0x013f
            r12 = r11
            org.telegram.messenger.WebFile r12 = (org.telegram.messenger.WebFile) r12
            java.lang.String r14 = r12.mime_type
            java.lang.String r2 = "image/"
            boolean r14 = r14.startsWith(r2)
            if (r14 == 0) goto L_0x011c
            java.io.File r12 = getDirectory(r4)
        L_0x0119:
            r2 = r12
            goto L_0x000b
        L_0x011c:
            java.lang.String r14 = r12.mime_type
            java.lang.String r2 = "audio/"
            boolean r14 = r14.startsWith(r2)
            if (r14 == 0) goto L_0x012b
            java.io.File r12 = getDirectory(r7)
            goto L_0x0119
        L_0x012b:
            java.lang.String r12 = r12.mime_type
            java.lang.String r14 = "video/"
            boolean r12 = r12.startsWith(r14)
            if (r12 == 0) goto L_0x013a
            java.io.File r12 = getDirectory(r5)
            goto L_0x0119
        L_0x013a:
            java.io.File r12 = getDirectory(r6)
            goto L_0x0119
        L_0x013f:
            boolean r12 = r11 instanceof org.telegram.tgnet.TLRPC$TL_secureFile
            if (r12 != 0) goto L_0x0147
            boolean r12 = r11 instanceof org.telegram.messenger.SecureDocument
            if (r12 == 0) goto L_0x000b
        L_0x0147:
            java.io.File r2 = getDirectory(r3)
            goto L_0x000b
        L_0x014d:
            java.lang.String r14 = "s"
            if (r12 != 0) goto L_0x0152
            r12 = r14
        L_0x0152:
            boolean r12 = r14.equals(r12)
            if (r12 == 0) goto L_0x015e
            java.io.File r2 = getDirectory(r3)
            goto L_0x000b
        L_0x015e:
            java.io.File r2 = getDirectory(r4)
            goto L_0x000b
        L_0x0164:
            if (r2 != 0) goto L_0x016e
            java.io.File r11 = new java.io.File
            java.lang.String r12 = ""
            r11.<init>(r12)
            return r11
        L_0x016e:
            int r12 = (r4 > r0 ? 1 : (r4 == r0 ? 0 : -1))
            if (r12 == 0) goto L_0x0189
            int r12 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.FileLoader r12 = getInstance(r12)
            org.telegram.messenger.FilePathDatabase r3 = r12.getFileDatabase()
            r8 = r15
            java.lang.String r12 = r3.getPath(r4, r6, r7, r8)
            if (r12 == 0) goto L_0x0189
            java.io.File r11 = new java.io.File
            r11.<init>(r12)
            return r11
        L_0x0189:
            java.io.File r12 = new java.io.File
            java.lang.String r11 = getAttachFileName(r11, r13)
            r12.<init>(r2, r11)
            return r12
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.FileLoader.getPathToAttach(org.telegram.tgnet.TLObject, java.lang.String, java.lang.String, boolean, boolean):java.io.File");
    }

    /* access modifiers changed from: private */
    public FilePathDatabase getFileDatabase() {
        return this.filePathDatabase;
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
        String str = null;
        if (tLRPC$Document == null) {
            return null;
        }
        String str2 = tLRPC$Document.file_name_fixed;
        if (str2 != null) {
            return str2;
        }
        String str3 = tLRPC$Document.file_name;
        if (str3 == null) {
            for (int i = 0; i < tLRPC$Document.attributes.size(); i++) {
                TLRPC$DocumentAttribute tLRPC$DocumentAttribute = tLRPC$Document.attributes.get(i);
                if (tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeFilename) {
                    str = tLRPC$DocumentAttribute.file_name;
                }
            }
            str3 = str;
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

    public void checkMediaExistance(ArrayList<MessageObject> arrayList) {
        getFileDatabase().checkMediaExistance(arrayList);
    }

    public void clearRecentDownloadedFiles() {
        getDownloadController().clearRecentDownloadedFiles();
    }

    public void clearFilePaths() {
        this.filePathDatabase.clear();
    }

    public static boolean checkUploadFileSize(int i, long j) {
        boolean isPremium = AccountInstance.getInstance(i).getUserConfig().isPremium();
        if (j >= NUM) {
            return j < 4194304000L && isPremium;
        }
        return true;
    }

    private static class LoadOperationUIObject {
        Runnable loadInternalRunnable;

        private LoadOperationUIObject() {
        }
    }
}
