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
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import org.telegram.messenger.FileUploadOperation.FileUploadOperationDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.InputEncryptedFile;
import org.telegram.tgnet.TLRPC.InputFile;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.tgnet.TLRPC.Photo;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_documentAttributeFilename;
import org.telegram.tgnet.TLRPC.TL_fileLocationToBeDeprecated;
import org.telegram.tgnet.TLRPC.TL_fileLocationUnavailable;
import org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
import org.telegram.tgnet.TLRPC.TL_messageMediaInvoice;
import org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
import org.telegram.tgnet.TLRPC.TL_messageMediaWebPage;
import org.telegram.tgnet.TLRPC.TL_messageService;
import org.telegram.tgnet.TLRPC.TL_photoStrippedSize;
import org.telegram.tgnet.TLRPC.TL_secureFile;
import org.telegram.tgnet.TLRPC.WebDocument;
import org.telegram.tgnet.TLRPC.WebPage;

public class FileLoader extends BaseController {
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
    private HashMap<String, Boolean> loadingVideos = new HashMap();
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
        return (File) mediaDirs.get(i);
    }

    public static File getDirectory(int i) {
        File file = (File) mediaDirs.get(i);
        if (file == null && i != 4) {
            file = (File) mediaDirs.get(4);
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
    public void lambda$setLoadingVideo$0$FileLoader(Document document, boolean z) {
        String attachFileName = getAttachFileName(document);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(attachFileName);
        stringBuilder.append(z ? "p" : "");
        this.loadingVideos.put(stringBuilder.toString(), Boolean.valueOf(true));
        getNotificationCenter().postNotificationName(NotificationCenter.videoLoadingStateChanged, attachFileName);
    }

    public void setLoadingVideo(Document document, boolean z, boolean z2) {
        if (document != null) {
            if (z2) {
                AndroidUtilities.runOnUIThread(new -$$Lambda$FileLoader$5K2sGZscq7bEKvLM0V2ywbk86iM(this, document, z));
            } else {
                lambda$setLoadingVideo$0$FileLoader(document, z);
            }
        }
    }

    public void setLoadingVideoForPlayer(Document document, boolean z) {
        if (document != null) {
            String attachFileName = getAttachFileName(document);
            HashMap hashMap = this.loadingVideos;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(attachFileName);
            String str = "";
            String str2 = "p";
            stringBuilder.append(z ? str : str2);
            if (hashMap.containsKey(stringBuilder.toString())) {
                hashMap = this.loadingVideos;
                stringBuilder = new StringBuilder();
                stringBuilder.append(attachFileName);
                if (z) {
                    str = str2;
                }
                stringBuilder.append(str);
                hashMap.put(stringBuilder.toString(), Boolean.valueOf(true));
            }
        }
    }

    private void removeLoadingVideoInternal(Document document, boolean z) {
        String attachFileName = getAttachFileName(document);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(attachFileName);
        stringBuilder.append(z ? "p" : "");
        if (this.loadingVideos.remove(stringBuilder.toString()) != null) {
            getNotificationCenter().postNotificationName(NotificationCenter.videoLoadingStateChanged, attachFileName);
        }
    }

    public /* synthetic */ void lambda$removeLoadingVideo$1$FileLoader(Document document, boolean z) {
        removeLoadingVideoInternal(document, z);
    }

    public void removeLoadingVideo(Document document, boolean z, boolean z2) {
        if (document != null) {
            if (z2) {
                AndroidUtilities.runOnUIThread(new -$$Lambda$FileLoader$hhzyW9_Gs7B9dgoeAzy05Nfkj9g(this, document, z));
            } else {
                removeLoadingVideoInternal(document, z);
            }
        }
    }

    public boolean isLoadingVideo(Document document, boolean z) {
        if (document != null) {
            HashMap hashMap = this.loadingVideos;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(getAttachFileName(document));
            stringBuilder.append(z ? "p" : "");
            if (hashMap.containsKey(stringBuilder.toString())) {
                return true;
            }
        }
        return false;
    }

    public boolean isLoadingVideoAny(Document document) {
        return isLoadingVideo(document, false) || isLoadingVideo(document, true);
    }

    public void cancelUploadFile(String str, boolean z) {
        fileLoaderQueue.postRunnable(new -$$Lambda$FileLoader$iP93DCpFk-1vNZP-nXjq8znzYAg(this, z, str));
    }

    public /* synthetic */ void lambda$cancelUploadFile$2$FileLoader(boolean z, String str) {
        Object obj;
        if (z) {
            obj = (FileUploadOperation) this.uploadOperationPathsEnc.get(str);
        } else {
            obj = (FileUploadOperation) this.uploadOperationPaths.get(str);
        }
        this.uploadSizes.remove(str);
        if (obj != null) {
            this.uploadOperationPathsEnc.remove(str);
            this.uploadOperationQueue.remove(obj);
            this.uploadSmallOperationQueue.remove(obj);
            obj.cancel();
        }
    }

    public void checkUploadNewDataAvailable(String str, boolean z, long j, long j2) {
        fileLoaderQueue.postRunnable(new -$$Lambda$FileLoader$BtLqmhj036PHY9Oj2RFTl8bO4mc(this, z, str, j, j2));
    }

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
        fileLoaderQueue.postRunnable(new -$$Lambda$FileLoader$YYo8hp0C6-EIkcQEvJqmJMnz1R4(this, z));
    }

    public /* synthetic */ void lambda$onNetworkChanged$4$FileLoader(boolean z) {
        for (Entry value : this.uploadOperationPaths.entrySet()) {
            ((FileUploadOperation) value.getValue()).onNetworkChanged(z);
        }
        for (Entry value2 : this.uploadOperationPathsEnc.entrySet()) {
            ((FileUploadOperation) value2.getValue()).onNetworkChanged(z);
        }
    }

    public void uploadFile(String str, boolean z, boolean z2, int i) {
        uploadFile(str, z, z2, 0, i);
    }

    public void uploadFile(String str, boolean z, boolean z2, int i, int i2) {
        if (str != null) {
            fileLoaderQueue.postRunnable(new -$$Lambda$FileLoader$VC4JseGAlGdgB-OxKhWFev1MyY0(this, z, str, i, i2, z2));
        }
    }

    public /* synthetic */ void lambda$uploadFile$5$FileLoader(final boolean z, final String str, int i, int i2, final boolean z2) {
        int i3;
        if (z) {
            if (this.uploadOperationPathsEnc.containsKey(str)) {
                return;
            }
        } else if (this.uploadOperationPaths.containsKey(str)) {
            return;
        }
        if (i == 0 || ((Long) this.uploadSizes.get(str)) == null) {
            i3 = i;
        } else {
            this.uploadSizes.remove(str);
            i3 = 0;
        }
        FileUploadOperation fileUploadOperation = new FileUploadOperation(this.currentAccount, str, z, i3, i2);
        if (z) {
            this.uploadOperationPathsEnc.put(str, fileUploadOperation);
        } else {
            this.uploadOperationPaths.put(str, fileUploadOperation);
        }
        fileUploadOperation.setDelegate(new FileUploadOperationDelegate() {
            public void didFinishUploadingFile(FileUploadOperation fileUploadOperation, InputFile inputFile, InputEncryptedFile inputEncryptedFile, byte[] bArr, byte[] bArr2) {
                FileLoader.fileLoaderQueue.postRunnable(new -$$Lambda$FileLoader$1$8qI8Hd84Wsy2NQjG71Z0jivjIew(this, z, str, z2, inputFile, inputEncryptedFile, bArr, bArr2, fileUploadOperation));
            }

            public /* synthetic */ void lambda$didFinishUploadingFile$0$FileLoader$1(boolean z, String str, boolean z2, InputFile inputFile, InputEncryptedFile inputEncryptedFile, byte[] bArr, byte[] bArr2, FileUploadOperation fileUploadOperation) {
                String str2 = str;
                if (z) {
                    FileLoader.this.uploadOperationPathsEnc.remove(str);
                } else {
                    FileLoader.this.uploadOperationPaths.remove(str);
                }
                FileUploadOperation fileUploadOperation2;
                if (z2) {
                    FileLoader.this.currentUploadSmallOperationsCount = FileLoader.this.currentUploadSmallOperationsCount - 1;
                    if (FileLoader.this.currentUploadSmallOperationsCount < 1) {
                        fileUploadOperation2 = (FileUploadOperation) FileLoader.this.uploadSmallOperationQueue.poll();
                        if (fileUploadOperation2 != null) {
                            FileLoader.this.currentUploadSmallOperationsCount = FileLoader.this.currentUploadSmallOperationsCount + 1;
                            fileUploadOperation2.start();
                        }
                    }
                } else {
                    FileLoader.this.currentUploadOperationsCount = FileLoader.this.currentUploadOperationsCount - 1;
                    if (FileLoader.this.currentUploadOperationsCount < 1) {
                        fileUploadOperation2 = (FileUploadOperation) FileLoader.this.uploadOperationQueue.poll();
                        if (fileUploadOperation2 != null) {
                            FileLoader.this.currentUploadOperationsCount = FileLoader.this.currentUploadOperationsCount + 1;
                            fileUploadOperation2.start();
                        }
                    }
                }
                if (FileLoader.this.delegate != null) {
                    FileLoader.this.delegate.fileDidUploaded(str, inputFile, inputEncryptedFile, bArr, bArr2, fileUploadOperation.getTotalFileSize());
                }
            }

            public void didFailedUploadingFile(FileUploadOperation fileUploadOperation) {
                FileLoader.fileLoaderQueue.postRunnable(new -$$Lambda$FileLoader$1$4L6YnCrX_lUiw2AHHpo_e3FY75I(this, z, str, z2));
            }

            public /* synthetic */ void lambda$didFailedUploadingFile$1$FileLoader$1(boolean z, String str, boolean z2) {
                if (z) {
                    FileLoader.this.uploadOperationPathsEnc.remove(str);
                } else {
                    FileLoader.this.uploadOperationPaths.remove(str);
                }
                if (FileLoader.this.delegate != null) {
                    FileLoader.this.delegate.fileDidFailedUpload(str, z);
                }
                FileUploadOperation fileUploadOperation;
                if (z2) {
                    FileLoader.this.currentUploadSmallOperationsCount = FileLoader.this.currentUploadSmallOperationsCount - 1;
                    if (FileLoader.this.currentUploadSmallOperationsCount < 1) {
                        fileUploadOperation = (FileUploadOperation) FileLoader.this.uploadSmallOperationQueue.poll();
                        if (fileUploadOperation != null) {
                            FileLoader.this.currentUploadSmallOperationsCount = FileLoader.this.currentUploadSmallOperationsCount + 1;
                            fileUploadOperation.start();
                            return;
                        }
                        return;
                    }
                    return;
                }
                FileLoader.this.currentUploadOperationsCount = FileLoader.this.currentUploadOperationsCount - 1;
                if (FileLoader.this.currentUploadOperationsCount < 1) {
                    fileUploadOperation = (FileUploadOperation) FileLoader.this.uploadOperationQueue.poll();
                    if (fileUploadOperation != null) {
                        FileLoader.this.currentUploadOperationsCount = FileLoader.this.currentUploadOperationsCount + 1;
                        fileUploadOperation.start();
                    }
                }
            }

            public void didChangedUploadProgress(FileUploadOperation fileUploadOperation, float f) {
                if (FileLoader.this.delegate != null) {
                    FileLoader.this.delegate.fileUploadProgressChanged(str, f, z);
                }
            }
        });
        int i4;
        if (z2) {
            i4 = this.currentUploadSmallOperationsCount;
            if (i4 < 1) {
                this.currentUploadSmallOperationsCount = i4 + 1;
                fileUploadOperation.start();
            } else {
                this.uploadSmallOperationQueue.add(fileUploadOperation);
            }
        } else {
            i4 = this.currentUploadOperationsCount;
            if (i4 < 1) {
                this.currentUploadOperationsCount = i4 + 1;
                fileUploadOperation.start();
            } else {
                this.uploadOperationQueue.add(fileUploadOperation);
            }
        }
    }

    private LinkedList<FileLoadOperation> getAudioLoadOperationQueue(int i) {
        LinkedList<FileLoadOperation> linkedList = (LinkedList) this.audioLoadOperationQueues.get(i);
        if (linkedList != null) {
            return linkedList;
        }
        LinkedList linkedList2 = new LinkedList();
        this.audioLoadOperationQueues.put(i, linkedList2);
        return linkedList2;
    }

    private LinkedList<FileLoadOperation> getPhotoLoadOperationQueue(int i) {
        LinkedList<FileLoadOperation> linkedList = (LinkedList) this.photoLoadOperationQueues.get(i);
        if (linkedList != null) {
            return linkedList;
        }
        LinkedList linkedList2 = new LinkedList();
        this.photoLoadOperationQueues.put(i, linkedList2);
        return linkedList2;
    }

    private LinkedList<FileLoadOperation> getLoadOperationQueue(int i) {
        LinkedList<FileLoadOperation> linkedList = (LinkedList) this.loadOperationQueues.get(i);
        if (linkedList != null) {
            return linkedList;
        }
        LinkedList linkedList2 = new LinkedList();
        this.loadOperationQueues.put(i, linkedList2);
        return linkedList2;
    }

    public void cancelLoadFile(Document document) {
        cancelLoadFile(document, null, null, null, null);
    }

    public void cancelLoadFile(SecureDocument secureDocument) {
        cancelLoadFile(null, secureDocument, null, null, null);
    }

    public void cancelLoadFile(WebFile webFile) {
        cancelLoadFile(null, null, webFile, null, null);
    }

    public void cancelLoadFile(PhotoSize photoSize) {
        cancelLoadFile(null, null, null, photoSize.location, null);
    }

    public void cancelLoadFile(FileLocation fileLocation, String str) {
        cancelLoadFile(null, null, null, fileLocation, str);
    }

    private void cancelLoadFile(Document document, SecureDocument secureDocument, WebFile webFile, FileLocation fileLocation, String str) {
        if (fileLocation != null || document != null || webFile != null || secureDocument != null) {
            str = fileLocation != null ? getAttachFileName(fileLocation, str) : document != null ? getAttachFileName(document) : secureDocument != null ? getAttachFileName(secureDocument) : webFile != null ? getAttachFileName(webFile) : null;
            String str2 = str;
            if (str2 != null) {
                this.loadOperationPathsUI.remove(str2);
                fileLoaderQueue.postRunnable(new -$$Lambda$FileLoader$5pIYqCqDZLULELsU2WZGVaPNvhc(this, str2, document, webFile, secureDocument, fileLocation));
            }
        }
    }

    public /* synthetic */ void lambda$cancelLoadFile$6$FileLoader(String str, Document document, WebFile webFile, SecureDocument secureDocument, FileLocation fileLocation) {
        FileLoadOperation fileLoadOperation = (FileLoadOperation) this.loadOperationPaths.remove(str);
        if (fileLoadOperation != null) {
            int datacenterId = fileLoadOperation.getDatacenterId();
            SparseIntArray sparseIntArray;
            if (MessageObject.isVoiceDocument(document) || MessageObject.isVoiceWebDocument(webFile)) {
                if (!getAudioLoadOperationQueue(datacenterId).remove(fileLoadOperation)) {
                    sparseIntArray = this.currentAudioLoadOperationsCount;
                    sparseIntArray.put(datacenterId, sparseIntArray.get(datacenterId) - 1);
                }
            } else if (secureDocument == null && fileLocation == null && !MessageObject.isImageWebDocument(webFile)) {
                if (!getLoadOperationQueue(datacenterId).remove(fileLoadOperation)) {
                    sparseIntArray = this.currentLoadOperationsCount;
                    sparseIntArray.put(datacenterId, sparseIntArray.get(datacenterId) - 1);
                }
                this.activeFileLoadOperation.remove(fileLoadOperation);
            } else if (!getPhotoLoadOperationQueue(datacenterId).remove(fileLoadOperation)) {
                sparseIntArray = this.currentPhotoLoadOperationsCount;
                sparseIntArray.put(datacenterId, sparseIntArray.get(datacenterId) - 1);
            }
            fileLoadOperation.cancel();
        }
    }

    public boolean isLoadingFile(String str) {
        return this.loadOperationPathsUI.containsKey(str);
    }

    public float getBufferedProgressFromPosition(float f, String str) {
        if (TextUtils.isEmpty(str)) {
            return 0.0f;
        }
        FileLoadOperation fileLoadOperation = (FileLoadOperation) this.loadOperationPaths.get(str);
        if (fileLoadOperation != null) {
            return fileLoadOperation.getDownloadedLengthFromOffset(f);
        }
        return 0.0f;
    }

    public void loadFile(ImageLocation imageLocation, Object obj, String str, int i, int i2) {
        ImageLocation imageLocation2 = imageLocation;
        if (imageLocation2 != null) {
            int i3 = (i2 != 0 || (!imageLocation.isEncrypted() && (imageLocation2.photoSize == null || imageLocation.getSize() != 0))) ? i2 : 1;
            loadFile(imageLocation2.document, imageLocation2.secureDocument, imageLocation2.webFile, imageLocation2.location, imageLocation, obj, str, imageLocation.getSize(), i, i3);
        }
    }

    public void loadFile(SecureDocument secureDocument, int i) {
        if (secureDocument != null) {
            loadFile(null, secureDocument, null, null, null, null, null, 0, i, 1);
        }
    }

    public void loadFile(Document document, Object obj, int i, int i2) {
        if (document != null) {
            int i3 = (i2 != 0 || document.key == null) ? i2 : 1;
            loadFile(document, null, null, null, null, obj, null, 0, i, i3);
        }
    }

    public void loadFile(WebFile webFile, int i, int i2) {
        loadFile(null, null, webFile, null, null, null, null, 0, i, i2);
    }

    private void pauseCurrentFileLoadOperations(FileLoadOperation fileLoadOperation) {
        int i = 0;
        while (i < this.activeFileLoadOperation.size()) {
            FileLoadOperation fileLoadOperation2 = (FileLoadOperation) this.activeFileLoadOperation.get(i);
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

    /* JADX WARNING: Removed duplicated region for block: B:106:0x0196  */
    /* JADX WARNING: Removed duplicated region for block: B:119:0x01ee  */
    /* JADX WARNING: Removed duplicated region for block: B:109:0x01cb  */
    /* JADX WARNING: Removed duplicated region for block: B:97:0x0179  */
    /* JADX WARNING: Removed duplicated region for block: B:106:0x0196  */
    /* JADX WARNING: Removed duplicated region for block: B:109:0x01cb  */
    /* JADX WARNING: Removed duplicated region for block: B:119:0x01ee  */
    /* JADX WARNING: Removed duplicated region for block: B:97:0x0179  */
    /* JADX WARNING: Removed duplicated region for block: B:106:0x0196  */
    /* JADX WARNING: Removed duplicated region for block: B:119:0x01ee  */
    /* JADX WARNING: Removed duplicated region for block: B:109:0x01cb  */
    /* JADX WARNING: Removed duplicated region for block: B:97:0x0179  */
    /* JADX WARNING: Removed duplicated region for block: B:106:0x0196  */
    /* JADX WARNING: Removed duplicated region for block: B:109:0x01cb  */
    /* JADX WARNING: Removed duplicated region for block: B:119:0x01ee  */
    /* JADX WARNING: Removed duplicated region for block: B:12:0x0039  */
    /* JADX WARNING: Missing block: B:86:0x0154, code skipped:
            if (org.telegram.messenger.MessageObject.isVideoDocument(r19) != false) goto L_0x0156;
     */
    /* JADX WARNING: Missing block: B:94:0x0173, code skipped:
            if (org.telegram.messenger.MessageObject.isImageWebDocument(r21) != false) goto L_0x013f;
     */
    private org.telegram.messenger.FileLoadOperation loadFileInternal(org.telegram.tgnet.TLRPC.Document r19, org.telegram.messenger.SecureDocument r20, org.telegram.messenger.WebFile r21, org.telegram.tgnet.TLRPC.TL_fileLocationToBeDeprecated r22, org.telegram.messenger.ImageLocation r23, java.lang.Object r24, java.lang.String r25, int r26, int r27, org.telegram.messenger.FileLoadOperationStream r28, int r29, boolean r30, int r31) {
        /*
        r18 = this;
        r7 = r18;
        r4 = r19;
        r0 = r20;
        r8 = r21;
        r9 = r22;
        r1 = r24;
        r2 = r25;
        r10 = r27;
        r11 = r28;
        r12 = r29;
        r13 = r30;
        r3 = r31;
        r5 = 0;
        if (r9 == 0) goto L_0x0021;
    L_0x001b:
        r6 = getAttachFileName(r9, r2);
    L_0x001f:
        r14 = r6;
        goto L_0x0037;
    L_0x0021:
        if (r0 == 0) goto L_0x0028;
    L_0x0023:
        r6 = getAttachFileName(r20);
        goto L_0x001f;
    L_0x0028:
        if (r4 == 0) goto L_0x002f;
    L_0x002a:
        r6 = getAttachFileName(r19);
        goto L_0x001f;
    L_0x002f:
        if (r8 == 0) goto L_0x0036;
    L_0x0031:
        r6 = getAttachFileName(r21);
        goto L_0x001f;
    L_0x0036:
        r14 = r5;
    L_0x0037:
        if (r14 == 0) goto L_0x024b;
    L_0x0039:
        r6 = "-NUM";
        r15 = r14.contains(r6);
        if (r15 == 0) goto L_0x0043;
    L_0x0041:
        goto L_0x024b;
    L_0x0043:
        r5 = 10;
        r15 = 1;
        if (r3 == r5) goto L_0x005d;
    L_0x0048:
        r16 = android.text.TextUtils.isEmpty(r14);
        if (r16 != 0) goto L_0x005d;
    L_0x004e:
        r6 = r14.contains(r6);
        if (r6 != 0) goto L_0x005d;
    L_0x0054:
        r6 = r7.loadOperationPathsUI;
        r5 = java.lang.Boolean.valueOf(r15);
        r6.put(r14, r5);
    L_0x005d:
        r5 = r7.loadOperationPaths;
        r5 = r5.get(r14);
        r5 = (org.telegram.messenger.FileLoadOperation) r5;
        r6 = 0;
        if (r5 == 0) goto L_0x0124;
    L_0x0068:
        r15 = 10;
        if (r3 == r15) goto L_0x0075;
    L_0x006c:
        r1 = r5.isPreloadVideoOperation();
        if (r1 == 0) goto L_0x0075;
    L_0x0072:
        r5.setIsPreloadVideoOperation(r6);
    L_0x0075:
        if (r11 != 0) goto L_0x0079;
    L_0x0077:
        if (r10 <= 0) goto L_0x0123;
    L_0x0079:
        r1 = r5.getDatacenterId();
        r2 = r7.getAudioLoadOperationQueue(r1);
        r3 = r7.getPhotoLoadOperationQueue(r1);
        r10 = r7.getLoadOperationQueue(r1);
        r14 = 1;
        r5.setForceRequest(r14);
        r4 = org.telegram.messenger.MessageObject.isVoiceDocument(r19);
        if (r4 != 0) goto L_0x00a9;
    L_0x0093:
        r4 = org.telegram.messenger.MessageObject.isVoiceWebDocument(r21);
        if (r4 == 0) goto L_0x009a;
    L_0x0099:
        goto L_0x00a9;
    L_0x009a:
        if (r0 != 0) goto L_0x00a7;
    L_0x009c:
        if (r9 != 0) goto L_0x00a7;
    L_0x009e:
        r0 = org.telegram.messenger.MessageObject.isImageWebDocument(r21);
        if (r0 == 0) goto L_0x00a5;
    L_0x00a4:
        goto L_0x00a7;
    L_0x00a5:
        r0 = r10;
        goto L_0x00aa;
    L_0x00a7:
        r0 = r3;
        goto L_0x00aa;
    L_0x00a9:
        r0 = r2;
    L_0x00aa:
        if (r0 == 0) goto L_0x0123;
    L_0x00ac:
        r4 = r0.indexOf(r5);
        if (r4 < 0) goto L_0x010c;
    L_0x00b2:
        r0.remove(r4);
        if (r11 == 0) goto L_0x0108;
    L_0x00b7:
        if (r0 != r2) goto L_0x00cb;
    L_0x00b9:
        r0 = r5.start(r11, r12, r13);
        if (r0 == 0) goto L_0x0123;
    L_0x00bf:
        r0 = r7.currentAudioLoadOperationsCount;
        r2 = r0.get(r1);
        r4 = 1;
        r2 = r2 + r4;
        r0.put(r1, r2);
        goto L_0x0123;
    L_0x00cb:
        r4 = 1;
        if (r0 != r3) goto L_0x00df;
    L_0x00ce:
        r0 = r5.start(r11, r12, r13);
        if (r0 == 0) goto L_0x0123;
    L_0x00d4:
        r0 = r7.currentPhotoLoadOperationsCount;
        r2 = r0.get(r1);
        r2 = r2 + r4;
        r0.put(r1, r2);
        goto L_0x0123;
    L_0x00df:
        r0 = r5.start(r11, r12, r13);
        if (r0 == 0) goto L_0x00ef;
    L_0x00e5:
        r0 = r7.currentLoadOperationsCount;
        r2 = r0.get(r1);
        r2 = r2 + r4;
        r0.put(r1, r2);
    L_0x00ef:
        r0 = r5.wasStarted();
        if (r0 == 0) goto L_0x0123;
    L_0x00f5:
        r0 = r7.activeFileLoadOperation;
        r0 = r0.contains(r5);
        if (r0 != 0) goto L_0x0123;
    L_0x00fd:
        if (r11 == 0) goto L_0x0102;
    L_0x00ff:
        r7.pauseCurrentFileLoadOperations(r5);
    L_0x0102:
        r0 = r7.activeFileLoadOperation;
        r0.add(r5);
        goto L_0x0123;
    L_0x0108:
        r0.add(r6, r5);
        goto L_0x0123;
    L_0x010c:
        if (r11 == 0) goto L_0x0111;
    L_0x010e:
        r7.pauseCurrentFileLoadOperations(r5);
    L_0x0111:
        r5.start(r11, r12, r13);
        if (r0 != r10) goto L_0x0123;
    L_0x0116:
        r0 = r7.activeFileLoadOperation;
        r0 = r0.contains(r5);
        if (r0 != 0) goto L_0x0123;
    L_0x011e:
        r0 = r7.activeFileLoadOperation;
        r0.add(r5);
    L_0x0123:
        return r5;
    L_0x0124:
        r15 = 4;
        r6 = getDirectory(r15);
        r17 = 3;
        if (r0 == 0) goto L_0x0134;
    L_0x012d:
        r5 = new org.telegram.messenger.FileLoadOperation;
        r5.<init>(r0);
    L_0x0132:
        r15 = 3;
        goto L_0x0177;
    L_0x0134:
        if (r9 == 0) goto L_0x0141;
    L_0x0136:
        r5 = new org.telegram.messenger.FileLoadOperation;
        r0 = r23;
        r15 = r26;
        r5.<init>(r0, r1, r2, r15);
    L_0x013f:
        r15 = 0;
        goto L_0x0177;
    L_0x0141:
        if (r4 == 0) goto L_0x0158;
    L_0x0143:
        r5 = new org.telegram.messenger.FileLoadOperation;
        r5.<init>(r4, r1);
        r0 = org.telegram.messenger.MessageObject.isVoiceDocument(r19);
        if (r0 == 0) goto L_0x0150;
    L_0x014e:
        r15 = 1;
        goto L_0x0177;
    L_0x0150:
        r0 = org.telegram.messenger.MessageObject.isVideoDocument(r19);
        if (r0 == 0) goto L_0x0132;
    L_0x0156:
        r15 = 2;
        goto L_0x0177;
    L_0x0158:
        if (r8 == 0) goto L_0x0176;
    L_0x015a:
        r5 = new org.telegram.messenger.FileLoadOperation;
        r0 = r7.currentAccount;
        r5.<init>(r0, r8);
        r0 = org.telegram.messenger.MessageObject.isVoiceWebDocument(r21);
        if (r0 == 0) goto L_0x0168;
    L_0x0167:
        goto L_0x014e;
    L_0x0168:
        r0 = org.telegram.messenger.MessageObject.isVideoWebDocument(r21);
        if (r0 == 0) goto L_0x016f;
    L_0x016e:
        goto L_0x0156;
    L_0x016f:
        r0 = org.telegram.messenger.MessageObject.isImageWebDocument(r21);
        if (r0 == 0) goto L_0x0132;
    L_0x0175:
        goto L_0x013f;
    L_0x0176:
        r15 = 4;
    L_0x0177:
        if (r3 == 0) goto L_0x0187;
    L_0x0179:
        r0 = 10;
        if (r3 != r0) goto L_0x017e;
    L_0x017d:
        goto L_0x0187;
    L_0x017e:
        r2 = 2;
        r0 = 1;
        if (r3 != r2) goto L_0x0185;
    L_0x0182:
        r5.setEncryptFile(r0);
    L_0x0185:
        r1 = r6;
        goto L_0x018d;
    L_0x0187:
        r0 = 1;
        r2 = 2;
        r1 = getDirectory(r15);
    L_0x018d:
        r2 = r7.currentAccount;
        r5.setPaths(r2, r1, r6);
        r1 = 10;
        if (r3 != r1) goto L_0x0199;
    L_0x0196:
        r5.setIsPreloadVideoOperation(r0);
    L_0x0199:
        r6 = new org.telegram.messenger.FileLoader$2;
        r0 = r6;
        r1 = r18;
        r16 = 2;
        r2 = r14;
        r3 = r15;
        r4 = r19;
        r8 = r5;
        r5 = r21;
        r9 = r6;
        r6 = r22;
        r0.<init>(r2, r3, r4, r5, r6);
        r8.setDelegate(r9);
        r0 = r8.getDatacenterId();
        r1 = r7.getAudioLoadOperationQueue(r0);
        r2 = r7.getPhotoLoadOperationQueue(r0);
        r3 = r7.getLoadOperationQueue(r0);
        r4 = r7.loadOperationPaths;
        r4.put(r14, r8);
        r8.setPriority(r10);
        r4 = 1;
        if (r15 != r4) goto L_0x01ee;
    L_0x01cb:
        if (r10 <= 0) goto L_0x01cf;
    L_0x01cd:
        r2 = 3;
        goto L_0x01d0;
    L_0x01cf:
        r2 = 1;
    L_0x01d0:
        r3 = r7.currentAudioLoadOperationsCount;
        r3 = r3.get(r0);
        if (r11 != 0) goto L_0x01e0;
    L_0x01d8:
        if (r3 >= r2) goto L_0x01db;
    L_0x01da:
        goto L_0x01e0;
    L_0x01db:
        r7.addOperationToQueue(r8, r1);
        goto L_0x024a;
    L_0x01e0:
        r1 = r8.start(r11, r12, r13);
        if (r1 == 0) goto L_0x024a;
    L_0x01e6:
        r1 = r7.currentAudioLoadOperationsCount;
        r2 = 1;
        r3 = r3 + r2;
        r1.put(r0, r3);
        goto L_0x024a;
    L_0x01ee:
        if (r22 != 0) goto L_0x0229;
    L_0x01f0:
        r1 = org.telegram.messenger.MessageObject.isImageWebDocument(r21);
        if (r1 == 0) goto L_0x01f7;
    L_0x01f6:
        goto L_0x0229;
    L_0x01f7:
        if (r10 <= 0) goto L_0x01fb;
    L_0x01f9:
        r1 = 4;
        goto L_0x01fc;
    L_0x01fb:
        r1 = 1;
    L_0x01fc:
        r2 = r7.currentLoadOperationsCount;
        r2 = r2.get(r0);
        if (r11 != 0) goto L_0x020b;
    L_0x0204:
        if (r2 >= r1) goto L_0x0207;
    L_0x0206:
        goto L_0x020b;
    L_0x0207:
        r7.addOperationToQueue(r8, r3);
        goto L_0x024a;
    L_0x020b:
        r1 = r8.start(r11, r12, r13);
        if (r1 == 0) goto L_0x021d;
    L_0x0211:
        r1 = r7.currentLoadOperationsCount;
        r3 = 1;
        r2 = r2 + r3;
        r1.put(r0, r2);
        r0 = r7.activeFileLoadOperation;
        r0.add(r8);
    L_0x021d:
        r0 = r8.wasStarted();
        if (r0 == 0) goto L_0x024a;
    L_0x0223:
        if (r11 == 0) goto L_0x024a;
    L_0x0225:
        r7.pauseCurrentFileLoadOperations(r8);
        goto L_0x024a;
    L_0x0229:
        if (r10 <= 0) goto L_0x022d;
    L_0x022b:
        r15 = 6;
        goto L_0x022e;
    L_0x022d:
        r15 = 2;
    L_0x022e:
        r1 = r7.currentPhotoLoadOperationsCount;
        r1 = r1.get(r0);
        if (r11 != 0) goto L_0x023d;
    L_0x0236:
        if (r1 >= r15) goto L_0x0239;
    L_0x0238:
        goto L_0x023d;
    L_0x0239:
        r7.addOperationToQueue(r8, r2);
        goto L_0x024a;
    L_0x023d:
        r2 = r8.start(r11, r12, r13);
        if (r2 == 0) goto L_0x024a;
    L_0x0243:
        r2 = r7.currentPhotoLoadOperationsCount;
        r3 = 1;
        r1 = r1 + r3;
        r2.put(r0, r1);
    L_0x024a:
        return r8;
    L_0x024b:
        return r5;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.FileLoader.loadFileInternal(org.telegram.tgnet.TLRPC$Document, org.telegram.messenger.SecureDocument, org.telegram.messenger.WebFile, org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated, org.telegram.messenger.ImageLocation, java.lang.Object, java.lang.String, int, int, org.telegram.messenger.FileLoadOperationStream, int, boolean, int):org.telegram.messenger.FileLoadOperation");
    }

    private void addOperationToQueue(FileLoadOperation fileLoadOperation, LinkedList<FileLoadOperation> linkedList) {
        int priority = fileLoadOperation.getPriority();
        if (priority > 0) {
            int size = linkedList.size();
            int size2 = linkedList.size();
            for (int i = 0; i < size2; i++) {
                if (((FileLoadOperation) linkedList.get(i)).getPriority() < priority) {
                    size = i;
                    break;
                }
            }
            linkedList.add(size, fileLoadOperation);
            return;
        }
        linkedList.add(fileLoadOperation);
    }

    private void loadFile(Document document, SecureDocument secureDocument, WebFile webFile, TL_fileLocationToBeDeprecated tL_fileLocationToBeDeprecated, ImageLocation imageLocation, Object obj, String str, int i, int i2, int i3) {
        CharSequence attachFileName;
        TLObject tLObject = tL_fileLocationToBeDeprecated;
        if (tLObject != null) {
            attachFileName = getAttachFileName(tLObject, str);
        } else {
            String str2 = str;
            attachFileName = document != null ? getAttachFileName(document) : webFile != null ? getAttachFileName(webFile) : null;
        }
        if (i3 == 10 || TextUtils.isEmpty(attachFileName) || attachFileName.contains("-NUM")) {
        } else {
            this.loadOperationPathsUI.put(attachFileName, Boolean.valueOf(true));
        }
        fileLoaderQueue.postRunnable(new -$$Lambda$FileLoader$QveLJ1Gqcvj_9l-dGSaDY1G3t6s(this, document, secureDocument, webFile, tL_fileLocationToBeDeprecated, imageLocation, obj, str, i, i2, i3));
    }

    public /* synthetic */ void lambda$loadFile$7$FileLoader(Document document, SecureDocument secureDocument, WebFile webFile, TL_fileLocationToBeDeprecated tL_fileLocationToBeDeprecated, ImageLocation imageLocation, Object obj, String str, int i, int i2, int i3) {
        loadFileInternal(document, secureDocument, webFile, tL_fileLocationToBeDeprecated, imageLocation, obj, str, i, i2, null, 0, false, i3);
    }

    /* Access modifiers changed, original: protected */
    public FileLoadOperation loadStreamFile(FileLoadOperationStream fileLoadOperationStream, Document document, Object obj, int i, boolean z) {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        FileLoadOperation[] fileLoadOperationArr = new FileLoadOperation[1];
        fileLoaderQueue.postRunnable(new -$$Lambda$FileLoader$Y4NJqKKZ1P8H4f3HcmH_MDyWMYY(this, fileLoadOperationArr, document, obj, fileLoadOperationStream, i, z, countDownLatch));
        try {
            countDownLatch.await();
        } catch (Exception e) {
            FileLog.e(e);
        }
        return fileLoadOperationArr[0];
    }

    public /* synthetic */ void lambda$loadStreamFile$8$FileLoader(FileLoadOperation[] fileLoadOperationArr, Document document, Object obj, FileLoadOperationStream fileLoadOperationStream, int i, boolean z, CountDownLatch countDownLatch) {
        fileLoadOperationArr[0] = loadFileInternal(document, null, null, null, null, obj, null, 0, 1, fileLoadOperationStream, i, z, 0);
        countDownLatch.countDown();
    }

    private void checkDownloadQueue(int i, Document document, WebFile webFile, FileLocation fileLocation, String str) {
        fileLoaderQueue.postRunnable(new -$$Lambda$FileLoader$zxmFDKJZWcEsmPLtOCvxbxeqvMw(this, i, str, document, webFile, fileLocation));
    }

    public /* synthetic */ void lambda$checkDownloadQueue$9$FileLoader(int i, String str, Document document, WebFile webFile, FileLocation fileLocation) {
        LinkedList audioLoadOperationQueue = getAudioLoadOperationQueue(i);
        LinkedList photoLoadOperationQueue = getPhotoLoadOperationQueue(i);
        LinkedList loadOperationQueue = getLoadOperationQueue(i);
        FileLoadOperation fileLoadOperation = (FileLoadOperation) this.loadOperationPaths.remove(str);
        int i2;
        if (MessageObject.isVoiceDocument(document) || MessageObject.isVoiceWebDocument(webFile)) {
            i2 = this.currentAudioLoadOperationsCount.get(i);
            if (fileLoadOperation != null) {
                if (fileLoadOperation.wasStarted()) {
                    i2--;
                    this.currentAudioLoadOperationsCount.put(i, i2);
                } else {
                    audioLoadOperationQueue.remove(fileLoadOperation);
                }
            }
            while (!audioLoadOperationQueue.isEmpty()) {
                if (i2 < (((FileLoadOperation) audioLoadOperationQueue.get(0)).getPriority() != 0 ? 3 : 1)) {
                    fileLoadOperation = (FileLoadOperation) audioLoadOperationQueue.poll();
                    if (fileLoadOperation != null && fileLoadOperation.start()) {
                        i2++;
                        this.currentAudioLoadOperationsCount.put(i, i2);
                    }
                } else {
                    return;
                }
            }
        } else if (fileLocation != null || MessageObject.isImageWebDocument(webFile)) {
            i2 = this.currentPhotoLoadOperationsCount.get(i);
            if (fileLoadOperation != null) {
                if (fileLoadOperation.wasStarted()) {
                    i2--;
                    this.currentPhotoLoadOperationsCount.put(i, i2);
                } else {
                    photoLoadOperationQueue.remove(fileLoadOperation);
                }
            }
            while (!photoLoadOperationQueue.isEmpty()) {
                if (i2 < (((FileLoadOperation) photoLoadOperationQueue.get(0)).getPriority() != 0 ? 6 : 2)) {
                    fileLoadOperation = (FileLoadOperation) photoLoadOperationQueue.poll();
                    if (fileLoadOperation != null && fileLoadOperation.start()) {
                        i2++;
                        this.currentPhotoLoadOperationsCount.put(i, i2);
                    }
                } else {
                    return;
                }
            }
        } else {
            i2 = this.currentLoadOperationsCount.get(i);
            if (fileLoadOperation != null) {
                if (fileLoadOperation.wasStarted()) {
                    i2--;
                    this.currentLoadOperationsCount.put(i, i2);
                } else {
                    loadOperationQueue.remove(fileLoadOperation);
                }
                this.activeFileLoadOperation.remove(fileLoadOperation);
            }
            while (!loadOperationQueue.isEmpty()) {
                if (i2 < (((FileLoadOperation) loadOperationQueue.get(0)).isForceRequest() ? 3 : 1)) {
                    fileLoadOperation = (FileLoadOperation) loadOperationQueue.poll();
                    if (fileLoadOperation != null && fileLoadOperation.start()) {
                        i2++;
                        this.currentLoadOperationsCount.put(i, i2);
                        if (!this.activeFileLoadOperation.contains(fileLoadOperation)) {
                            this.activeFileLoadOperation.add(fileLoadOperation);
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

    public static String getMessageFileName(Message message) {
        String str = "";
        if (message == null) {
            return str;
        }
        ArrayList arrayList;
        PhotoSize closestPhotoSizeWithSize;
        if (message instanceof TL_messageService) {
            Photo photo = message.action.photo;
            if (photo != null) {
                arrayList = photo.sizes;
                if (arrayList.size() > 0) {
                    closestPhotoSizeWithSize = getClosestPhotoSizeWithSize(arrayList, AndroidUtilities.getPhotoSize());
                    if (closestPhotoSizeWithSize != null) {
                        return getAttachFileName(closestPhotoSizeWithSize);
                    }
                }
            }
        }
        MessageMedia messageMedia = message.media;
        if (messageMedia instanceof TL_messageMediaDocument) {
            return getAttachFileName(messageMedia.document);
        }
        if (messageMedia instanceof TL_messageMediaPhoto) {
            arrayList = messageMedia.photo.sizes;
            if (arrayList.size() > 0) {
                closestPhotoSizeWithSize = getClosestPhotoSizeWithSize(arrayList, AndroidUtilities.getPhotoSize());
                if (closestPhotoSizeWithSize != null) {
                    return getAttachFileName(closestPhotoSizeWithSize);
                }
            }
        } else if (messageMedia instanceof TL_messageMediaWebPage) {
            WebPage webPage = messageMedia.webpage;
            Document document = webPage.document;
            if (document != null) {
                return getAttachFileName(document);
            }
            Photo photo2 = webPage.photo;
            if (photo2 != null) {
                arrayList = photo2.sizes;
                if (arrayList.size() > 0) {
                    closestPhotoSizeWithSize = getClosestPhotoSizeWithSize(arrayList, AndroidUtilities.getPhotoSize());
                    if (closestPhotoSizeWithSize != null) {
                        return getAttachFileName(closestPhotoSizeWithSize);
                    }
                }
            } else if (messageMedia instanceof TL_messageMediaInvoice) {
                return getAttachFileName(((TL_messageMediaInvoice) messageMedia).photo);
            }
        } else if (messageMedia instanceof TL_messageMediaInvoice) {
            WebDocument webDocument = ((TL_messageMediaInvoice) messageMedia).photo;
            if (webDocument != null) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(Utilities.MD5(webDocument.url));
                stringBuilder.append(".");
                stringBuilder.append(ImageLoader.getHttpUrlExtension(webDocument.url, getMimeTypePart(webDocument.mime_type)));
                return stringBuilder.toString();
            }
        }
        return str;
    }

    public static File getPathToMessage(Message message) {
        String str = "";
        if (message == null) {
            return new File(str);
        }
        Photo photo;
        ArrayList arrayList;
        PhotoSize closestPhotoSizeWithSize;
        if (message instanceof TL_messageService) {
            photo = message.action.photo;
            if (photo != null) {
                arrayList = photo.sizes;
                if (arrayList.size() > 0) {
                    closestPhotoSizeWithSize = getClosestPhotoSizeWithSize(arrayList, AndroidUtilities.getPhotoSize());
                    if (closestPhotoSizeWithSize != null) {
                        return getPathToAttach(closestPhotoSizeWithSize);
                    }
                }
            }
        }
        MessageMedia messageMedia = message.media;
        boolean z = false;
        if (messageMedia instanceof TL_messageMediaDocument) {
            Document document = messageMedia.document;
            if (messageMedia.ttl_seconds != 0) {
                z = true;
            }
            return getPathToAttach(document, z);
        } else if (messageMedia instanceof TL_messageMediaPhoto) {
            ArrayList arrayList2 = messageMedia.photo.sizes;
            if (arrayList2.size() > 0) {
                PhotoSize closestPhotoSizeWithSize2 = getClosestPhotoSizeWithSize(arrayList2, AndroidUtilities.getPhotoSize());
                if (closestPhotoSizeWithSize2 != null) {
                    if (message.media.ttl_seconds != 0) {
                        z = true;
                    }
                    return getPathToAttach(closestPhotoSizeWithSize2, z);
                }
            }
        } else if (messageMedia instanceof TL_messageMediaWebPage) {
            WebPage webPage = messageMedia.webpage;
            Document document2 = webPage.document;
            if (document2 != null) {
                return getPathToAttach(document2);
            }
            photo = webPage.photo;
            if (photo != null) {
                arrayList = photo.sizes;
                if (arrayList.size() > 0) {
                    closestPhotoSizeWithSize = getClosestPhotoSizeWithSize(arrayList, AndroidUtilities.getPhotoSize());
                    if (closestPhotoSizeWithSize != null) {
                        return getPathToAttach(closestPhotoSizeWithSize);
                    }
                }
            }
        } else if (messageMedia instanceof TL_messageMediaInvoice) {
            return getPathToAttach(((TL_messageMediaInvoice) messageMedia).photo, true);
        }
        return new File(str);
    }

    public static File getPathToAttach(TLObject tLObject) {
        return getPathToAttach(tLObject, null, false);
    }

    public static File getPathToAttach(TLObject tLObject, boolean z) {
        return getPathToAttach(tLObject, null, z);
    }

    public static File getPathToAttach(TLObject tLObject, String str, boolean z) {
        File file = null;
        if (z) {
            file = getDirectory(4);
        } else {
            File directory;
            if (tLObject instanceof Document) {
                Document document = (Document) tLObject;
                if (document.key != null) {
                    directory = getDirectory(4);
                } else if (MessageObject.isVoiceDocument(document)) {
                    directory = getDirectory(1);
                } else if (MessageObject.isVideoDocument(document)) {
                    directory = getDirectory(2);
                } else {
                    directory = getDirectory(3);
                }
            } else if (tLObject instanceof Photo) {
                return getPathToAttach(getClosestPhotoSizeWithSize(((Photo) tLObject).sizes, AndroidUtilities.getPhotoSize()), str, z);
            } else {
                if (tLObject instanceof PhotoSize) {
                    PhotoSize photoSize = (PhotoSize) tLObject;
                    if (!(photoSize instanceof TL_photoStrippedSize)) {
                        FileLocation fileLocation = photoSize.location;
                        if (fileLocation == null || fileLocation.key != null || ((fileLocation.volume_id == -2147483648L && fileLocation.local_id < 0) || photoSize.size < 0)) {
                            directory = getDirectory(4);
                        } else {
                            directory = getDirectory(0);
                        }
                    }
                } else if (tLObject instanceof FileLocation) {
                    FileLocation fileLocation2 = (FileLocation) tLObject;
                    if (fileLocation2.key != null || (fileLocation2.volume_id == -2147483648L && fileLocation2.local_id < 0)) {
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
                } else if ((tLObject instanceof TL_secureFile) || (tLObject instanceof SecureDocument)) {
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

    public static PhotoSize getClosestPhotoSizeWithSize(ArrayList<PhotoSize> arrayList, int i) {
        return getClosestPhotoSizeWithSize(arrayList, i, false);
    }

    /* JADX WARNING: Missing block: B:21:0x0036, code skipped:
            if (r5.dc_id == Integer.MIN_VALUE) goto L_0x005d;
     */
    /* JADX WARNING: Missing block: B:34:0x0053, code skipped:
            if (r5.dc_id == Integer.MIN_VALUE) goto L_0x005d;
     */
    public static org.telegram.tgnet.TLRPC.PhotoSize getClosestPhotoSizeWithSize(java.util.ArrayList<org.telegram.tgnet.TLRPC.PhotoSize> r8, int r9, boolean r10) {
        /*
        r0 = 0;
        if (r8 == 0) goto L_0x0062;
    L_0x0003:
        r1 = r8.isEmpty();
        if (r1 == 0) goto L_0x000a;
    L_0x0009:
        goto L_0x0062;
    L_0x000a:
        r1 = 0;
        r2 = 0;
    L_0x000c:
        r3 = r8.size();
        if (r1 >= r3) goto L_0x0062;
    L_0x0012:
        r3 = r8.get(r1);
        r3 = (org.telegram.tgnet.TLRPC.PhotoSize) r3;
        if (r3 == 0) goto L_0x005f;
    L_0x001a:
        r4 = r3 instanceof org.telegram.tgnet.TLRPC.TL_photoSizeEmpty;
        if (r4 == 0) goto L_0x001f;
    L_0x001e:
        goto L_0x005f;
    L_0x001f:
        r4 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
        r5 = 100;
        if (r10 == 0) goto L_0x0041;
    L_0x0025:
        r6 = r3.h;
        r7 = r3.w;
        if (r6 < r7) goto L_0x002c;
    L_0x002b:
        r6 = r7;
    L_0x002c:
        if (r0 == 0) goto L_0x005d;
    L_0x002e:
        if (r9 <= r5) goto L_0x0038;
    L_0x0030:
        r5 = r0.location;
        if (r5 == 0) goto L_0x0038;
    L_0x0034:
        r5 = r5.dc_id;
        if (r5 == r4) goto L_0x005d;
    L_0x0038:
        r4 = r3 instanceof org.telegram.tgnet.TLRPC.TL_photoCachedSize;
        if (r4 != 0) goto L_0x005d;
    L_0x003c:
        if (r9 <= r2) goto L_0x005f;
    L_0x003e:
        if (r2 >= r6) goto L_0x005f;
    L_0x0040:
        goto L_0x005d;
    L_0x0041:
        r6 = r3.w;
        r7 = r3.h;
        if (r6 < r7) goto L_0x0048;
    L_0x0047:
        goto L_0x0049;
    L_0x0048:
        r6 = r7;
    L_0x0049:
        if (r0 == 0) goto L_0x005d;
    L_0x004b:
        if (r9 <= r5) goto L_0x0055;
    L_0x004d:
        r5 = r0.location;
        if (r5 == 0) goto L_0x0055;
    L_0x0051:
        r5 = r5.dc_id;
        if (r5 == r4) goto L_0x005d;
    L_0x0055:
        r4 = r3 instanceof org.telegram.tgnet.TLRPC.TL_photoCachedSize;
        if (r4 != 0) goto L_0x005d;
    L_0x0059:
        if (r6 > r9) goto L_0x005f;
    L_0x005b:
        if (r2 >= r6) goto L_0x005f;
    L_0x005d:
        r0 = r3;
        r2 = r6;
    L_0x005f:
        r1 = r1 + 1;
        goto L_0x000c;
    L_0x0062:
        return r0;
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

    public static String getDocumentFileName(Document document) {
        String str = null;
        if (document != null) {
            String str2 = document.file_name;
            if (str2 != null) {
                str = str2;
            } else {
                for (int i = 0; i < document.attributes.size(); i++) {
                    DocumentAttribute documentAttribute = (DocumentAttribute) document.attributes.get(i);
                    if (documentAttribute instanceof TL_documentAttributeFilename) {
                        str = documentAttribute.file_name;
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
        if (str != null) {
            int i = -1;
            int hashCode = str.hashCode();
            if (hashCode != NUM) {
                if (hashCode != NUM) {
                    if (hashCode == NUM && str.equals("video/x-matroska")) {
                        i = 1;
                    }
                } else if (str.equals("video/mp4")) {
                    i = 0;
                }
            } else if (str.equals("audio/ogg")) {
                i = 2;
            }
            if (i == 0) {
                return ".mp4";
            }
            if (i == 1) {
                return ".mkv";
            }
            if (i == 2) {
                return ".ogg";
            }
        }
        return "";
    }

    public static File getInternalCacheDir() {
        return ApplicationLoader.applicationContext.getCacheDir();
    }

    public static String getDocumentExtension(Document document) {
        String documentFileName = getDocumentFileName(document);
        int lastIndexOf = documentFileName.lastIndexOf(46);
        documentFileName = lastIndexOf != -1 ? documentFileName.substring(lastIndexOf + 1) : null;
        if (documentFileName == null || documentFileName.length() == 0) {
            documentFileName = document.mime_type;
        }
        if (documentFileName == null) {
            documentFileName = "";
        }
        return documentFileName.toUpperCase();
    }

    public static String getAttachFileName(TLObject tLObject) {
        return getAttachFileName(tLObject, null);
    }

    public static String getAttachFileName(TLObject tLObject, String str) {
        String str2 = "";
        String str3 = "_";
        StringBuilder stringBuilder;
        if (tLObject instanceof Document) {
            Document document = (Document) tLObject;
            str = getDocumentFileName(document);
            if (str != null) {
                int lastIndexOf = str.lastIndexOf(46);
                if (lastIndexOf != -1) {
                    str2 = str.substring(lastIndexOf);
                }
            }
            if (str2.length() <= 1) {
                str2 = getExtensionByMimeType(document.mime_type);
            }
            if (str2.length() > 1) {
                stringBuilder = new StringBuilder();
                stringBuilder.append(document.dc_id);
                stringBuilder.append(str3);
                stringBuilder.append(document.id);
                stringBuilder.append(str2);
                return stringBuilder.toString();
            }
            stringBuilder = new StringBuilder();
            stringBuilder.append(document.dc_id);
            stringBuilder.append(str3);
            stringBuilder.append(document.id);
            return stringBuilder.toString();
        }
        String str4 = ".jpg";
        if (tLObject instanceof SecureDocument) {
            SecureDocument secureDocument = (SecureDocument) tLObject;
            stringBuilder = new StringBuilder();
            stringBuilder.append(secureDocument.secureFile.dc_id);
            stringBuilder.append(str3);
            stringBuilder.append(secureDocument.secureFile.id);
            stringBuilder.append(str4);
            return stringBuilder.toString();
        } else if (tLObject instanceof TL_secureFile) {
            TL_secureFile tL_secureFile = (TL_secureFile) tLObject;
            stringBuilder = new StringBuilder();
            stringBuilder.append(tL_secureFile.dc_id);
            stringBuilder.append(str3);
            stringBuilder.append(tL_secureFile.id);
            stringBuilder.append(str4);
            return stringBuilder.toString();
        } else {
            str4 = ".";
            if (tLObject instanceof WebFile) {
                WebFile webFile = (WebFile) tLObject;
                stringBuilder = new StringBuilder();
                stringBuilder.append(Utilities.MD5(webFile.url));
                stringBuilder.append(str4);
                stringBuilder.append(ImageLoader.getHttpUrlExtension(webFile.url, getMimeTypePart(webFile.mime_type)));
                return stringBuilder.toString();
            }
            String str5 = "jpg";
            StringBuilder stringBuilder2;
            if (tLObject instanceof PhotoSize) {
                PhotoSize photoSize = (PhotoSize) tLObject;
                FileLocation fileLocation = photoSize.location;
                if (fileLocation == null || (fileLocation instanceof TL_fileLocationUnavailable)) {
                    return str2;
                }
                stringBuilder2 = new StringBuilder();
                stringBuilder2.append(photoSize.location.volume_id);
                stringBuilder2.append(str3);
                stringBuilder2.append(photoSize.location.local_id);
                stringBuilder2.append(str4);
                if (str == null) {
                    str = str5;
                }
                stringBuilder2.append(str);
                return stringBuilder2.toString();
            } else if (!(tLObject instanceof FileLocation) || (tLObject instanceof TL_fileLocationUnavailable)) {
                return str2;
            } else {
                FileLocation fileLocation2 = (FileLocation) tLObject;
                stringBuilder2 = new StringBuilder();
                stringBuilder2.append(fileLocation2.volume_id);
                stringBuilder2.append(str3);
                stringBuilder2.append(fileLocation2.local_id);
                stringBuilder2.append(str4);
                if (str == null) {
                    str = str5;
                }
                stringBuilder2.append(str);
                return stringBuilder2.toString();
            }
        }
    }

    public void deleteFiles(ArrayList<File> arrayList, int i) {
        if (arrayList != null && !arrayList.isEmpty()) {
            fileLoaderQueue.postRunnable(new -$$Lambda$FileLoader$4dX6FY75qVi0nYcXFAFSA0OGeOs(arrayList, i));
        }
    }

    static /* synthetic */ void lambda$deleteFiles$10(ArrayList arrayList, int i) {
        for (int i2 = 0; i2 < arrayList.size(); i2++) {
            File internalCacheDir;
            StringBuilder stringBuilder;
            File file = (File) arrayList.get(i2);
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append(file.getAbsolutePath());
            stringBuilder2.append(".enc");
            File file2 = new File(stringBuilder2.toString());
            if (file2.exists()) {
                try {
                    if (!file2.delete()) {
                        file2.deleteOnExit();
                    }
                } catch (Exception e) {
                    FileLog.e(e);
                }
                try {
                    internalCacheDir = getInternalCacheDir();
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(file.getName());
                    stringBuilder.append(".enc.key");
                    file2 = new File(internalCacheDir, stringBuilder.toString());
                    if (!file2.delete()) {
                        file2.deleteOnExit();
                    }
                } catch (Exception e2) {
                    FileLog.e(e2);
                }
            } else if (file.exists()) {
                try {
                    if (!file.delete()) {
                        file.deleteOnExit();
                    }
                } catch (Exception e22) {
                    FileLog.e(e22);
                }
            }
            try {
                internalCacheDir = file.getParentFile();
                stringBuilder = new StringBuilder();
                stringBuilder.append("q_");
                stringBuilder.append(file.getName());
                file2 = new File(internalCacheDir, stringBuilder.toString());
                if (file2.exists() && !file2.delete()) {
                    file2.deleteOnExit();
                }
            } catch (Exception e3) {
                FileLog.e(e3);
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
