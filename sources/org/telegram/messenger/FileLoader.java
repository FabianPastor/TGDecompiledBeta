package org.telegram.messenger;

import android.text.TextUtils;
import android.util.SparseArray;
import android.util.SparseIntArray;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import org.telegram.messenger.FileLoadOperation.FileLoadOperationDelegate;
import org.telegram.messenger.FileUploadOperation.FileUploadOperationDelegate;
import org.telegram.messenger.exoplayer2.upstream.DataSource;
import org.telegram.messenger.exoplayer2.upstream.TransferListener;
import org.telegram.messenger.exoplayer2.util.MimeTypes;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.InputEncryptedFile;
import org.telegram.tgnet.TLRPC.InputFile;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_documentAttributeFilename;
import org.telegram.tgnet.TLRPC.TL_fileLocationUnavailable;
import org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
import org.telegram.tgnet.TLRPC.TL_messageMediaInvoice;
import org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
import org.telegram.tgnet.TLRPC.TL_messageMediaWebPage;
import org.telegram.tgnet.TLRPC.TL_messageService;
import org.telegram.tgnet.TLRPC.TL_photoCachedSize;
import org.telegram.tgnet.TLRPC.TL_webDocument;
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
    private ConcurrentHashMap<String, FileLoadOperation> loadOperationPaths = new ConcurrentHashMap();
    private ConcurrentHashMap<String, Boolean> loadOperationPathsUI = new ConcurrentHashMap(10, 1.0f, 2);
    private SparseArray<LinkedList<FileLoadOperation>> loadOperationQueues = new SparseArray();
    private SparseArray<LinkedList<FileLoadOperation>> photoLoadOperationQueues = new SparseArray();
    private ConcurrentHashMap<String, FileUploadOperation> uploadOperationPaths = new ConcurrentHashMap();
    private ConcurrentHashMap<String, FileUploadOperation> uploadOperationPathsEnc = new ConcurrentHashMap();
    private LinkedList<FileUploadOperation> uploadOperationQueue = new LinkedList();
    private HashMap<String, Long> uploadSizes = new HashMap();
    private LinkedList<FileUploadOperation> uploadSmallOperationQueue = new LinkedList();

    /* renamed from: org.telegram.messenger.FileLoader$5 */
    class C01925 implements FileLoadOperationDelegate {
        final /* synthetic */ Document val$document;
        final /* synthetic */ String val$finalFileName;
        final /* synthetic */ int val$finalType;
        final /* synthetic */ FileLocation val$location;
        final /* synthetic */ TL_webDocument val$webDocument;

        C01925(String str, int i, Document document, TL_webDocument tL_webDocument, FileLocation fileLocation) {
            this.val$finalFileName = str;
            this.val$finalType = i;
            this.val$document = document;
            this.val$webDocument = tL_webDocument;
            this.val$location = fileLocation;
        }

        public void didFinishLoadingFile(FileLoadOperation operation, File finalFile) {
            FileLoader.this.loadOperationPathsUI.remove(this.val$finalFileName);
            if (FileLoader.this.delegate != null) {
                FileLoader.this.delegate.fileDidLoaded(this.val$finalFileName, finalFile, this.val$finalType);
            }
            FileLoader.this.checkDownloadQueue(operation.getDatacenterId(), this.val$document, this.val$webDocument, this.val$location, this.val$finalFileName);
        }

        public void didFailedLoadingFile(FileLoadOperation operation, int reason) {
            FileLoader.this.loadOperationPathsUI.remove(this.val$finalFileName);
            FileLoader.this.checkDownloadQueue(operation.getDatacenterId(), this.val$document, this.val$webDocument, this.val$location, this.val$finalFileName);
            if (FileLoader.this.delegate != null) {
                FileLoader.this.delegate.fileDidFailedLoad(this.val$finalFileName, reason);
            }
        }

        public void didChangedLoadProgress(FileLoadOperation operation, float progress) {
            if (FileLoader.this.delegate != null) {
                FileLoader.this.delegate.fileLoadProgressChanged(this.val$finalFileName, progress);
            }
        }
    }

    public interface FileLoaderDelegate {
        void fileDidFailedLoad(String str, int i);

        void fileDidFailedUpload(String str, boolean z);

        void fileDidLoaded(String str, File file, int i);

        void fileDidUploaded(String str, InputFile inputFile, InputEncryptedFile inputEncryptedFile, byte[] bArr, byte[] bArr2, long j);

        void fileLoadProgressChanged(String str, float f);

        void fileUploadProgressChanged(String str, float f, boolean z);
    }

    public static FileLoader getInstance(int num) {
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

    public void cancelUploadFile(final String location, final boolean enc) {
        fileLoaderQueue.postRunnable(new Runnable() {
            public void run() {
                FileUploadOperation operation;
                if (enc) {
                    operation = (FileUploadOperation) FileLoader.this.uploadOperationPathsEnc.get(location);
                } else {
                    operation = (FileUploadOperation) FileLoader.this.uploadOperationPaths.get(location);
                }
                FileLoader.this.uploadSizes.remove(location);
                if (operation != null) {
                    FileLoader.this.uploadOperationPathsEnc.remove(location);
                    FileLoader.this.uploadOperationQueue.remove(operation);
                    FileLoader.this.uploadSmallOperationQueue.remove(operation);
                    operation.cancel();
                }
            }
        });
    }

    public void checkUploadNewDataAvailable(String location, boolean encrypted, long newAvailableSize, long finalSize) {
        final boolean z = encrypted;
        final String str = location;
        final long j = newAvailableSize;
        final long j2 = finalSize;
        fileLoaderQueue.postRunnable(new Runnable() {
            public void run() {
                FileUploadOperation operation;
                if (z) {
                    operation = (FileUploadOperation) FileLoader.this.uploadOperationPathsEnc.get(str);
                } else {
                    operation = (FileUploadOperation) FileLoader.this.uploadOperationPaths.get(str);
                }
                if (operation != null) {
                    operation.checkNewDataAvailable(j, j2);
                } else if (j2 != 0) {
                    FileLoader.this.uploadSizes.put(str, Long.valueOf(j2));
                }
            }
        });
    }

    public void uploadFile(String location, boolean encrypted, boolean small, int type) {
        uploadFile(location, encrypted, small, 0, type);
    }

    public void uploadFile(String location, boolean encrypted, boolean small, int estimatedSize, int type) {
        if (location != null) {
            final boolean z = encrypted;
            final String str = location;
            final int i = estimatedSize;
            final int i2 = type;
            final boolean z2 = small;
            fileLoaderQueue.postRunnable(new Runnable() {

                /* renamed from: org.telegram.messenger.FileLoader$3$1 */
                class C01891 implements FileUploadOperationDelegate {

                    /* renamed from: org.telegram.messenger.FileLoader$3$1$2 */
                    class C01882 implements Runnable {
                        C01882() {
                        }

                        public void run() {
                            if (z) {
                                FileLoader.this.uploadOperationPathsEnc.remove(str);
                            } else {
                                FileLoader.this.uploadOperationPaths.remove(str);
                            }
                            if (FileLoader.this.delegate != null) {
                                FileLoader.this.delegate.fileDidFailedUpload(str, z);
                            }
                            FileUploadOperation operation;
                            if (z2) {
                                FileLoader.this.currentUploadSmallOperationsCount = FileLoader.this.currentUploadSmallOperationsCount - 1;
                                if (FileLoader.this.currentUploadSmallOperationsCount < 1) {
                                    operation = (FileUploadOperation) FileLoader.this.uploadSmallOperationQueue.poll();
                                    if (operation != null) {
                                        FileLoader.this.currentUploadSmallOperationsCount = FileLoader.this.currentUploadSmallOperationsCount + 1;
                                        operation.start();
                                        return;
                                    }
                                    return;
                                }
                                return;
                            }
                            FileLoader.this.currentUploadOperationsCount = FileLoader.this.currentUploadOperationsCount - 1;
                            if (FileLoader.this.currentUploadOperationsCount < 1) {
                                operation = (FileUploadOperation) FileLoader.this.uploadOperationQueue.poll();
                                if (operation != null) {
                                    FileLoader.this.currentUploadOperationsCount = FileLoader.this.currentUploadOperationsCount + 1;
                                    operation.start();
                                }
                            }
                        }
                    }

                    C01891() {
                    }

                    public void didFinishUploadingFile(FileUploadOperation operation, InputFile inputFile, InputEncryptedFile inputEncryptedFile, byte[] key, byte[] iv) {
                        final InputFile inputFile2 = inputFile;
                        final InputEncryptedFile inputEncryptedFile2 = inputEncryptedFile;
                        final byte[] bArr = key;
                        final byte[] bArr2 = iv;
                        final FileUploadOperation fileUploadOperation = operation;
                        FileLoader.fileLoaderQueue.postRunnable(new Runnable() {
                            public void run() {
                                if (z) {
                                    FileLoader.this.uploadOperationPathsEnc.remove(str);
                                } else {
                                    FileLoader.this.uploadOperationPaths.remove(str);
                                }
                                FileUploadOperation operation;
                                if (z2) {
                                    FileLoader.this.currentUploadSmallOperationsCount = FileLoader.this.currentUploadSmallOperationsCount - 1;
                                    if (FileLoader.this.currentUploadSmallOperationsCount < 1) {
                                        operation = (FileUploadOperation) FileLoader.this.uploadSmallOperationQueue.poll();
                                        if (operation != null) {
                                            FileLoader.this.currentUploadSmallOperationsCount = FileLoader.this.currentUploadSmallOperationsCount + 1;
                                            operation.start();
                                        }
                                    }
                                } else {
                                    FileLoader.this.currentUploadOperationsCount = FileLoader.this.currentUploadOperationsCount - 1;
                                    if (FileLoader.this.currentUploadOperationsCount < 1) {
                                        operation = (FileUploadOperation) FileLoader.this.uploadOperationQueue.poll();
                                        if (operation != null) {
                                            FileLoader.this.currentUploadOperationsCount = FileLoader.this.currentUploadOperationsCount + 1;
                                            operation.start();
                                        }
                                    }
                                }
                                if (FileLoader.this.delegate != null) {
                                    FileLoader.this.delegate.fileDidUploaded(str, inputFile2, inputEncryptedFile2, bArr, bArr2, fileUploadOperation.getTotalFileSize());
                                }
                            }
                        });
                    }

                    public void didFailedUploadingFile(FileUploadOperation operation) {
                        FileLoader.fileLoaderQueue.postRunnable(new C01882());
                    }

                    public void didChangedUploadProgress(FileUploadOperation operation, float progress) {
                        if (FileLoader.this.delegate != null) {
                            FileLoader.this.delegate.fileUploadProgressChanged(str, progress, z);
                        }
                    }
                }

                public void run() {
                    if (z) {
                        if (FileLoader.this.uploadOperationPathsEnc.containsKey(str)) {
                            return;
                        }
                    } else if (FileLoader.this.uploadOperationPaths.containsKey(str)) {
                        return;
                    }
                    int esimated = i;
                    if (!(esimated == 0 || ((Long) FileLoader.this.uploadSizes.get(str)) == null)) {
                        esimated = 0;
                        FileLoader.this.uploadSizes.remove(str);
                    }
                    FileUploadOperation operation = new FileUploadOperation(FileLoader.this.currentAccount, str, z, esimated, i2);
                    if (z) {
                        FileLoader.this.uploadOperationPathsEnc.put(str, operation);
                    } else {
                        FileLoader.this.uploadOperationPaths.put(str, operation);
                    }
                    operation.setDelegate(new C01891());
                    if (z2) {
                        if (FileLoader.this.currentUploadSmallOperationsCount < 1) {
                            FileLoader.this.currentUploadSmallOperationsCount = FileLoader.this.currentUploadSmallOperationsCount + 1;
                            operation.start();
                            return;
                        }
                        FileLoader.this.uploadSmallOperationQueue.add(operation);
                    } else if (FileLoader.this.currentUploadOperationsCount < 1) {
                        FileLoader.this.currentUploadOperationsCount = FileLoader.this.currentUploadOperationsCount + 1;
                        operation.start();
                    } else {
                        FileLoader.this.uploadOperationQueue.add(operation);
                    }
                }
            });
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
        cancelLoadFile(document, null, null, null);
    }

    public void cancelLoadFile(TL_webDocument document) {
        cancelLoadFile(null, document, null, null);
    }

    public void cancelLoadFile(PhotoSize photo) {
        cancelLoadFile(null, null, photo.location, null);
    }

    public void cancelLoadFile(FileLocation location, String ext) {
        cancelLoadFile(null, null, location, ext);
    }

    private void cancelLoadFile(Document document, TL_webDocument webDocument, FileLocation location, String locationExt) {
        if (location != null || document != null || webDocument != null) {
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
            if (fileName != null) {
                this.loadOperationPathsUI.remove(fileName);
                final Document document2 = document;
                final TL_webDocument tL_webDocument = webDocument;
                final FileLocation fileLocation = location;
                fileLoaderQueue.postRunnable(new Runnable() {
                    public void run() {
                        FileLoadOperation operation = (FileLoadOperation) FileLoader.this.loadOperationPaths.remove(fileName);
                        if (operation != null) {
                            int datacenterId = operation.getDatacenterId();
                            if (MessageObject.isVoiceDocument(document2) || MessageObject.isVoiceWebDocument(tL_webDocument)) {
                                if (!FileLoader.this.getAudioLoadOperationQueue(datacenterId).remove(operation)) {
                                    FileLoader.this.currentAudioLoadOperationsCount.put(datacenterId, FileLoader.this.currentAudioLoadOperationsCount.get(datacenterId) - 1);
                                }
                            } else if (fileLocation == null && !MessageObject.isImageWebDocument(tL_webDocument)) {
                                if (!FileLoader.this.getLoadOperationQueue(datacenterId).remove(operation)) {
                                    FileLoader.this.currentLoadOperationsCount.put(datacenterId, FileLoader.this.currentLoadOperationsCount.get(datacenterId) - 1);
                                }
                                FileLoader.this.activeFileLoadOperation.remove(operation);
                            } else if (!FileLoader.this.getPhotoLoadOperationQueue(datacenterId).remove(operation)) {
                                FileLoader.this.currentPhotoLoadOperationsCount.put(datacenterId, FileLoader.this.currentPhotoLoadOperationsCount.get(datacenterId) - 1);
                            }
                            operation.cancel();
                        }
                    }
                });
            }
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
            loadFile(null, null, photo.location, ext, photo.size, false, cacheType);
        }
    }

    public void loadFile(Document document, boolean force, int cacheType) {
        if (document != null) {
            if (!(cacheType != 0 || document == null || document.key == null)) {
                cacheType = 1;
            }
            loadFile(document, null, null, null, 0, force, cacheType);
        }
    }

    public void loadFile(TL_webDocument document, boolean force, int cacheType) {
        loadFile(null, document, null, null, 0, force, cacheType);
    }

    public void loadFile(FileLocation location, String ext, int size, int cacheType) {
        if (location != null) {
            if (cacheType == 0 && (size == 0 || !(location == null || location.key == null))) {
                cacheType = 1;
            }
            loadFile(null, null, location, ext, size, true, cacheType);
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

    private org.telegram.messenger.FileLoadOperation loadFileInternal(org.telegram.tgnet.TLRPC.Document r25, org.telegram.tgnet.TLRPC.TL_webDocument r26, org.telegram.tgnet.TLRPC.FileLocation r27, java.lang.String r28, int r29, boolean r30, org.telegram.messenger.FileStreamLoadOperation r31, int r32, int r33) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Unknown predecessor block by arg (r19_2 'operation' org.telegram.messenger.FileLoadOperation) in PHI: PHI: (r19_3 'operation' org.telegram.messenger.FileLoadOperation) = (r19_2 'operation' org.telegram.messenger.FileLoadOperation), (r19_4 'operation' org.telegram.messenger.FileLoadOperation), (r19_4 'operation' org.telegram.messenger.FileLoadOperation), (r19_4 'operation' org.telegram.messenger.FileLoadOperation), (r19_1 'operation' org.telegram.messenger.FileLoadOperation), (r19_5 'operation' org.telegram.messenger.FileLoadOperation), (r19_5 'operation' org.telegram.messenger.FileLoadOperation), (r19_5 'operation' org.telegram.messenger.FileLoadOperation), (r19_5 'operation' org.telegram.messenger.FileLoadOperation) binds: {(r19_2 'operation' org.telegram.messenger.FileLoadOperation)=B:64:0x015d, (r19_4 'operation' org.telegram.messenger.FileLoadOperation)=B:82:0x01f2, (r19_4 'operation' org.telegram.messenger.FileLoadOperation)=B:85:0x01fc, (r19_4 'operation' org.telegram.messenger.FileLoadOperation)=B:86:0x0200, (r19_1 'operation' org.telegram.messenger.FileLoadOperation)=B:87:0x0204, (r19_5 'operation' org.telegram.messenger.FileLoadOperation)=B:90:0x0215, (r19_5 'operation' org.telegram.messenger.FileLoadOperation)=B:93:0x021f, (r19_5 'operation' org.telegram.messenger.FileLoadOperation)=B:96:0x0229, (r19_5 'operation' org.telegram.messenger.FileLoadOperation)=B:97:0x022d}
	at jadx.core.dex.instructions.PhiInsn.replaceArg(PhiInsn.java:79)
	at jadx.core.dex.visitors.ModVisitor.processInvoke(ModVisitor.java:222)
	at jadx.core.dex.visitors.ModVisitor.replaceStep(ModVisitor.java:83)
	at jadx.core.dex.visitors.ModVisitor.visit(ModVisitor.java:68)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:60)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r24 = this;
        r15 = 0;
        if (r27 == 0) goto L_0x0015;
    L_0x0003:
        r15 = getAttachFileName(r27, r28);
    L_0x0007:
        if (r15 == 0) goto L_0x0012;
    L_0x0009:
        r5 = "-2147483648";
        r5 = r15.contains(r5);
        if (r5 == 0) goto L_0x0023;
    L_0x0012:
        r19 = 0;
    L_0x0014:
        return r19;
    L_0x0015:
        if (r25 == 0) goto L_0x001c;
    L_0x0017:
        r15 = getAttachFileName(r25);
        goto L_0x0007;
    L_0x001c:
        if (r26 == 0) goto L_0x0007;
    L_0x001e:
        r15 = getAttachFileName(r26);
        goto L_0x0007;
    L_0x0023:
        r5 = android.text.TextUtils.isEmpty(r15);
        if (r5 != 0) goto L_0x003e;
    L_0x0029:
        r5 = "-2147483648";
        r5 = r15.contains(r5);
        if (r5 != 0) goto L_0x003e;
    L_0x0032:
        r0 = r24;
        r5 = r0.loadOperationPathsUI;
        r8 = 1;
        r8 = java.lang.Boolean.valueOf(r8);
        r5.put(r15, r8);
    L_0x003e:
        r0 = r24;
        r5 = r0.loadOperationPaths;
        r19 = r5.get(r15);
        r19 = (org.telegram.messenger.FileLoadOperation) r19;
        if (r19 == 0) goto L_0x0152;
    L_0x004a:
        if (r32 != 0) goto L_0x004e;
    L_0x004c:
        if (r30 == 0) goto L_0x0014;
    L_0x004e:
        r13 = r19.getDatacenterId();
        r0 = r24;
        r11 = r0.getAudioLoadOperationQueue(r13);
        r0 = r24;
        r20 = r0.getPhotoLoadOperationQueue(r13);
        r0 = r24;
        r17 = r0.getLoadOperationQueue(r13);
        r5 = 1;
        r0 = r19;
        r0.setForceRequest(r5);
        r5 = org.telegram.messenger.MessageObject.isVoiceDocument(r25);
        if (r5 != 0) goto L_0x0076;
    L_0x0070:
        r5 = org.telegram.messenger.MessageObject.isVoiceWebDocument(r26);
        if (r5 == 0) goto L_0x00a9;
    L_0x0076:
        r14 = r11;
    L_0x0077:
        if (r14 == 0) goto L_0x0014;
    L_0x0079:
        r0 = r19;
        r16 = r14.indexOf(r0);
        if (r16 <= 0) goto L_0x0125;
    L_0x0081:
        r0 = r16;
        r14.remove(r0);
        if (r32 == 0) goto L_0x011d;
    L_0x0088:
        if (r14 != r11) goto L_0x00b7;
    L_0x008a:
        r0 = r19;
        r1 = r31;
        r2 = r32;
        r5 = r0.start(r1, r2);
        if (r5 == 0) goto L_0x0014;
    L_0x0096:
        r0 = r24;
        r5 = r0.currentAudioLoadOperationsCount;
        r0 = r24;
        r8 = r0.currentAudioLoadOperationsCount;
        r8 = r8.get(r13);
        r8 = r8 + 1;
        r5.put(r13, r8);
        goto L_0x0014;
    L_0x00a9:
        if (r27 != 0) goto L_0x00b1;
    L_0x00ab:
        r5 = org.telegram.messenger.MessageObject.isImageWebDocument(r26);
        if (r5 == 0) goto L_0x00b4;
    L_0x00b1:
        r14 = r20;
        goto L_0x0077;
    L_0x00b4:
        r14 = r17;
        goto L_0x0077;
    L_0x00b7:
        r0 = r20;
        if (r14 != r0) goto L_0x00da;
    L_0x00bb:
        r0 = r19;
        r1 = r31;
        r2 = r32;
        r5 = r0.start(r1, r2);
        if (r5 == 0) goto L_0x0014;
    L_0x00c7:
        r0 = r24;
        r5 = r0.currentPhotoLoadOperationsCount;
        r0 = r24;
        r8 = r0.currentPhotoLoadOperationsCount;
        r8 = r8.get(r13);
        r8 = r8 + 1;
        r5.put(r13, r8);
        goto L_0x0014;
    L_0x00da:
        r0 = r19;
        r1 = r31;
        r2 = r32;
        r5 = r0.start(r1, r2);
        if (r5 == 0) goto L_0x00f7;
    L_0x00e6:
        r0 = r24;
        r5 = r0.currentLoadOperationsCount;
        r0 = r24;
        r8 = r0.currentLoadOperationsCount;
        r8 = r8.get(r13);
        r8 = r8 + 1;
        r5.put(r13, r8);
    L_0x00f7:
        r5 = r19.wasStarted();
        if (r5 == 0) goto L_0x0014;
    L_0x00fd:
        r0 = r24;
        r5 = r0.activeFileLoadOperation;
        r0 = r19;
        r5 = r5.contains(r0);
        if (r5 != 0) goto L_0x0014;
    L_0x0109:
        if (r31 == 0) goto L_0x0112;
    L_0x010b:
        r0 = r24;
        r1 = r19;
        r0.pauseCurrentFileLoadOperations(r1);
    L_0x0112:
        r0 = r24;
        r5 = r0.activeFileLoadOperation;
        r0 = r19;
        r5.add(r0);
        goto L_0x0014;
    L_0x011d:
        r5 = 0;
        r0 = r19;
        r14.add(r5, r0);
        goto L_0x0014;
    L_0x0125:
        if (r31 == 0) goto L_0x012e;
    L_0x0127:
        r0 = r24;
        r1 = r19;
        r0.pauseCurrentFileLoadOperations(r1);
    L_0x012e:
        r0 = r19;
        r1 = r31;
        r2 = r32;
        r0.start(r1, r2);
        r0 = r17;
        if (r14 != r0) goto L_0x0014;
    L_0x013b:
        r0 = r24;
        r5 = r0.activeFileLoadOperation;
        r0 = r19;
        r5 = r5.contains(r0);
        if (r5 != 0) goto L_0x0014;
    L_0x0147:
        r0 = r24;
        r5 = r0.activeFileLoadOperation;
        r0 = r19;
        r5.add(r0);
        goto L_0x0014;
    L_0x0152:
        r5 = 4;
        r22 = getDirectory(r5);
        r21 = r22;
        r23 = 4;
        if (r27 == 0) goto L_0x01e1;
    L_0x015d:
        r19 = new org.telegram.messenger.FileLoadOperation;
        r0 = r19;
        r1 = r27;
        r2 = r28;
        r3 = r29;
        r0.<init>(r1, r2, r3);
        r23 = 0;
    L_0x016c:
        if (r33 != 0) goto L_0x0231;
    L_0x016e:
        r21 = getDirectory(r23);
    L_0x0172:
        r0 = r24;
        r5 = r0.currentAccount;
        r0 = r19;
        r1 = r21;
        r2 = r22;
        r0.setPaths(r5, r1, r2);
        r6 = r15;
        r7 = r23;
        r4 = new org.telegram.messenger.FileLoader$5;
        r5 = r24;
        r8 = r25;
        r9 = r26;
        r10 = r27;
        r4.<init>(r6, r7, r8, r9, r10);
        r0 = r19;
        r0.setDelegate(r4);
        r13 = r19.getDatacenterId();
        r0 = r24;
        r11 = r0.getAudioLoadOperationQueue(r13);
        r0 = r24;
        r20 = r0.getPhotoLoadOperationQueue(r13);
        r0 = r24;
        r17 = r0.getLoadOperationQueue(r13);
        r0 = r24;
        r5 = r0.loadOperationPaths;
        r0 = r19;
        r5.put(r15, r0);
        if (r30 == 0) goto L_0x023e;
    L_0x01b5:
        r18 = 3;
    L_0x01b7:
        r5 = 1;
        r0 = r23;
        if (r0 != r5) goto L_0x0253;
    L_0x01bc:
        r0 = r24;
        r5 = r0.currentAudioLoadOperationsCount;
        r12 = r5.get(r13);
        if (r32 != 0) goto L_0x01ca;
    L_0x01c6:
        r0 = r18;
        if (r12 >= r0) goto L_0x0242;
    L_0x01ca:
        r0 = r19;
        r1 = r31;
        r2 = r32;
        r5 = r0.start(r1, r2);
        if (r5 == 0) goto L_0x0014;
    L_0x01d6:
        r0 = r24;
        r5 = r0.currentAudioLoadOperationsCount;
        r8 = r12 + 1;
        r5.put(r13, r8);
        goto L_0x0014;
    L_0x01e1:
        if (r25 == 0) goto L_0x0204;
    L_0x01e3:
        r19 = new org.telegram.messenger.FileLoadOperation;
        r0 = r19;
        r1 = r25;
        r0.<init>(r1);
        r5 = org.telegram.messenger.MessageObject.isVoiceDocument(r25);
        if (r5 == 0) goto L_0x01f6;
    L_0x01f2:
        r23 = 1;
        goto L_0x016c;
    L_0x01f6:
        r5 = org.telegram.messenger.MessageObject.isVideoDocument(r25);
        if (r5 == 0) goto L_0x0200;
    L_0x01fc:
        r23 = 2;
        goto L_0x016c;
    L_0x0200:
        r23 = 3;
        goto L_0x016c;
    L_0x0204:
        if (r26 == 0) goto L_0x016c;
    L_0x0206:
        r19 = new org.telegram.messenger.FileLoadOperation;
        r0 = r19;
        r1 = r26;
        r0.<init>(r1);
        r5 = org.telegram.messenger.MessageObject.isVoiceWebDocument(r26);
        if (r5 == 0) goto L_0x0219;
    L_0x0215:
        r23 = 1;
        goto L_0x016c;
    L_0x0219:
        r5 = org.telegram.messenger.MessageObject.isVideoWebDocument(r26);
        if (r5 == 0) goto L_0x0223;
    L_0x021f:
        r23 = 2;
        goto L_0x016c;
    L_0x0223:
        r5 = org.telegram.messenger.MessageObject.isImageWebDocument(r26);
        if (r5 == 0) goto L_0x022d;
    L_0x0229:
        r23 = 0;
        goto L_0x016c;
    L_0x022d:
        r23 = 3;
        goto L_0x016c;
    L_0x0231:
        r5 = 2;
        r0 = r33;
        if (r0 != r5) goto L_0x0172;
    L_0x0236:
        r5 = 1;
        r0 = r19;
        r0.setEncryptFile(r5);
        goto L_0x0172;
    L_0x023e:
        r18 = 1;
        goto L_0x01b7;
    L_0x0242:
        if (r30 == 0) goto L_0x024c;
    L_0x0244:
        r5 = 0;
        r0 = r19;
        r11.add(r5, r0);
        goto L_0x0014;
    L_0x024c:
        r0 = r19;
        r11.add(r0);
        goto L_0x0014;
    L_0x0253:
        if (r27 != 0) goto L_0x025b;
    L_0x0255:
        r5 = org.telegram.messenger.MessageObject.isImageWebDocument(r26);
        if (r5 == 0) goto L_0x0295;
    L_0x025b:
        r0 = r24;
        r5 = r0.currentPhotoLoadOperationsCount;
        r12 = r5.get(r13);
        if (r32 != 0) goto L_0x0269;
    L_0x0265:
        r0 = r18;
        if (r12 >= r0) goto L_0x0280;
    L_0x0269:
        r0 = r19;
        r1 = r31;
        r2 = r32;
        r5 = r0.start(r1, r2);
        if (r5 == 0) goto L_0x0014;
    L_0x0275:
        r0 = r24;
        r5 = r0.currentPhotoLoadOperationsCount;
        r8 = r12 + 1;
        r5.put(r13, r8);
        goto L_0x0014;
    L_0x0280:
        if (r30 == 0) goto L_0x028c;
    L_0x0282:
        r5 = 0;
        r0 = r20;
        r1 = r19;
        r0.add(r5, r1);
        goto L_0x0014;
    L_0x028c:
        r0 = r20;
        r1 = r19;
        r0.add(r1);
        goto L_0x0014;
    L_0x0295:
        r0 = r24;
        r5 = r0.currentLoadOperationsCount;
        r12 = r5.get(r13);
        if (r32 != 0) goto L_0x02a3;
    L_0x029f:
        r0 = r18;
        if (r12 >= r0) goto L_0x02d2;
    L_0x02a3:
        r0 = r19;
        r1 = r31;
        r2 = r32;
        r5 = r0.start(r1, r2);
        if (r5 == 0) goto L_0x02c1;
    L_0x02af:
        r0 = r24;
        r5 = r0.currentLoadOperationsCount;
        r8 = r12 + 1;
        r5.put(r13, r8);
        r0 = r24;
        r5 = r0.activeFileLoadOperation;
        r0 = r19;
        r5.add(r0);
    L_0x02c1:
        r5 = r19.wasStarted();
        if (r5 == 0) goto L_0x0014;
    L_0x02c7:
        if (r31 == 0) goto L_0x0014;
    L_0x02c9:
        r0 = r24;
        r1 = r19;
        r0.pauseCurrentFileLoadOperations(r1);
        goto L_0x0014;
    L_0x02d2:
        if (r30 == 0) goto L_0x02de;
    L_0x02d4:
        r5 = 0;
        r0 = r17;
        r1 = r19;
        r0.add(r5, r1);
        goto L_0x0014;
    L_0x02de:
        r0 = r17;
        r1 = r19;
        r0.add(r1);
        goto L_0x0014;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.FileLoader.loadFileInternal(org.telegram.tgnet.TLRPC$Document, org.telegram.tgnet.TLRPC$TL_webDocument, org.telegram.tgnet.TLRPC$FileLocation, java.lang.String, int, boolean, org.telegram.messenger.FileStreamLoadOperation, int, int):org.telegram.messenger.FileLoadOperation");
    }

    private void loadFile(Document document, TL_webDocument webDocument, FileLocation location, String locationExt, int locationSize, boolean force, int cacheType) {
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
        final Document document2 = document;
        final TL_webDocument tL_webDocument = webDocument;
        final FileLocation fileLocation = location;
        final String str = locationExt;
        final int i = locationSize;
        final boolean z = force;
        final int i2 = cacheType;
        fileLoaderQueue.postRunnable(new Runnable() {
            public void run() {
                FileLoader.this.loadFileInternal(document2, tL_webDocument, fileLocation, str, i, z, null, 0, i2);
            }
        });
    }

    protected FileLoadOperation loadStreamFile(FileStreamLoadOperation stream, Document document, int offset) {
        final CountDownLatch semaphore = new CountDownLatch(1);
        final FileLoadOperation[] result = new FileLoadOperation[1];
        final Document document2 = document;
        final FileStreamLoadOperation fileStreamLoadOperation = stream;
        final int i = offset;
        fileLoaderQueue.postRunnable(new Runnable() {
            public void run() {
                result[0] = FileLoader.this.loadFileInternal(document2, null, null, null, 0, true, fileStreamLoadOperation, i, 0);
                semaphore.countDown();
            }
        });
        try {
            semaphore.await();
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
        return result[0];
    }

    private void checkDownloadQueue(int datacenterId, Document document, TL_webDocument webDocument, FileLocation location, String arg1) {
        final int i = datacenterId;
        final String str = arg1;
        final Document document2 = document;
        final TL_webDocument tL_webDocument = webDocument;
        final FileLocation fileLocation = location;
        fileLoaderQueue.postRunnable(new Runnable() {
            public void run() {
                LinkedList<FileLoadOperation> audioLoadOperationQueue = FileLoader.this.getAudioLoadOperationQueue(i);
                LinkedList<FileLoadOperation> photoLoadOperationQueue = FileLoader.this.getPhotoLoadOperationQueue(i);
                LinkedList<FileLoadOperation> loadOperationQueue = FileLoader.this.getLoadOperationQueue(i);
                FileLoadOperation operation = (FileLoadOperation) FileLoader.this.loadOperationPaths.remove(str);
                int count;
                int maxCount;
                if (MessageObject.isVoiceDocument(document2) || MessageObject.isVoiceWebDocument(tL_webDocument)) {
                    count = FileLoader.this.currentAudioLoadOperationsCount.get(i);
                    if (operation != null) {
                        if (operation.wasStarted()) {
                            count--;
                            FileLoader.this.currentAudioLoadOperationsCount.put(i, count);
                        } else {
                            audioLoadOperationQueue.remove(operation);
                        }
                    }
                    while (!audioLoadOperationQueue.isEmpty()) {
                        if (((FileLoadOperation) audioLoadOperationQueue.get(0)).isForceRequest()) {
                            maxCount = 3;
                        } else {
                            maxCount = 1;
                        }
                        if (count < maxCount) {
                            operation = (FileLoadOperation) audioLoadOperationQueue.poll();
                            if (operation != null && operation.start()) {
                                count++;
                                FileLoader.this.currentAudioLoadOperationsCount.put(i, count);
                            }
                        } else {
                            return;
                        }
                    }
                } else if (fileLocation != null || MessageObject.isImageWebDocument(tL_webDocument)) {
                    count = FileLoader.this.currentPhotoLoadOperationsCount.get(i);
                    if (operation != null) {
                        if (operation.wasStarted()) {
                            count--;
                            FileLoader.this.currentPhotoLoadOperationsCount.put(i, count);
                        } else {
                            photoLoadOperationQueue.remove(operation);
                        }
                    }
                    while (!photoLoadOperationQueue.isEmpty()) {
                        if (((FileLoadOperation) photoLoadOperationQueue.get(0)).isForceRequest()) {
                            maxCount = 3;
                        } else {
                            maxCount = 1;
                        }
                        if (count < maxCount) {
                            operation = (FileLoadOperation) photoLoadOperationQueue.poll();
                            if (operation != null && operation.start()) {
                                count++;
                                FileLoader.this.currentPhotoLoadOperationsCount.put(i, count);
                            }
                        } else {
                            return;
                        }
                    }
                } else {
                    count = FileLoader.this.currentLoadOperationsCount.get(i);
                    if (operation != null) {
                        if (operation.wasStarted()) {
                            count--;
                            FileLoader.this.currentLoadOperationsCount.put(i, count);
                        } else {
                            loadOperationQueue.remove(operation);
                        }
                        FileLoader.this.activeFileLoadOperation.remove(operation);
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
                                FileLoader.this.currentLoadOperationsCount.put(i, count);
                                if (!FileLoader.this.activeFileLoadOperation.contains(operation)) {
                                    FileLoader.this.activeFileLoadOperation.add(operation);
                                }
                            }
                        } else {
                            return;
                        }
                    }
                }
            }
        });
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
        } else if (attach instanceof PhotoSize) {
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
        } else if (attach instanceof TL_webDocument) {
            TL_webDocument document2 = (TL_webDocument) attach;
            if (document2.mime_type.startsWith("image/")) {
                dir = getDirectory(0);
            } else if (document2.mime_type.startsWith("audio/")) {
                dir = getDirectory(1);
            } else if (document2.mime_type.startsWith("video/")) {
                dir = getDirectory(2);
            } else {
                dir = getDirectory(3);
            }
        }
        if (dir == null) {
            return new File(TtmlNode.ANONYMOUS_REGION_ID);
        }
        return new File(dir, getAttachFileName(attach, ext));
    }

    public static FileStreamLoadOperation getStreamLoadOperation(TransferListener<? super DataSource> listener) {
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
                    currentSide = obj.f24h >= obj.f25w ? obj.f25w : obj.f24h;
                    if (closestObject == null || ((side > 100 && closestObject.location != null && closestObject.location.dc_id == Integer.MIN_VALUE) || (obj instanceof TL_photoCachedSize) || (side > lastSide && lastSide < currentSide))) {
                        closestObject = obj;
                        lastSide = currentSide;
                    }
                } else {
                    currentSide = obj.f25w >= obj.f24h ? obj.f25w : obj.f24h;
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
                                int i = 1;
                                break;
                            }
                            break;
                        case 1331848029:
                            if (str.equals(MimeTypes.VIDEO_MP4)) {
                                obj = null;
                                break;
                            }
                            break;
                    }
                    switch (obj) {
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
            if (document.version == 0) {
                if (docExt.length() > 1) {
                    return document.dc_id + "_" + document.id + docExt;
                }
                return document.dc_id + "_" + document.id;
            } else if (docExt.length() > 1) {
                return document.dc_id + "_" + document.id + "_" + document.version + docExt;
            } else {
                return document.dc_id + "_" + document.id + "_" + document.version;
            }
        } else if (attach instanceof TL_webDocument) {
            TL_webDocument document2 = (TL_webDocument) attach;
            return Utilities.MD5(document2.url) + "." + ImageLoader.getHttpUrlExtension(document2.url, getExtensionByMime(document2.mime_type));
        } else if (attach instanceof PhotoSize) {
            PhotoSize photo = (PhotoSize) attach;
            if (photo.location == null || (photo.location instanceof TL_fileLocationUnavailable)) {
                return TtmlNode.ANONYMOUS_REGION_ID;
            }
            r5 = new StringBuilder().append(photo.location.volume_id).append("_").append(photo.location.local_id).append(".");
            if (ext == null) {
                ext = "jpg";
            }
            return r5.append(ext).toString();
        } else if (!(attach instanceof FileLocation)) {
            return TtmlNode.ANONYMOUS_REGION_ID;
        } else {
            if (attach instanceof TL_fileLocationUnavailable) {
                return TtmlNode.ANONYMOUS_REGION_ID;
            }
            FileLocation location = (FileLocation) attach;
            r5 = new StringBuilder().append(location.volume_id).append("_").append(location.local_id).append(".");
            if (ext == null) {
                ext = "jpg";
            }
            return r5.append(ext).toString();
        }
    }

    public void deleteFiles(final ArrayList<File> files, final int type) {
        if (files != null && !files.isEmpty()) {
            fileLoaderQueue.postRunnable(new Runnable() {
                public void run() {
                    for (int a = 0; a < files.size(); a++) {
                        File file = (File) files.get(a);
                        File encrypted = new File(file.getAbsolutePath() + ".enc");
                        if (encrypted.exists()) {
                            try {
                                if (!encrypted.delete()) {
                                    encrypted.deleteOnExit();
                                }
                            } catch (Throwable e) {
                                FileLog.m3e(e);
                            }
                            try {
                                File key = new File(FileLoader.getInternalCacheDir(), file.getName() + ".enc.key");
                                if (!key.delete()) {
                                    key.deleteOnExit();
                                }
                            } catch (Throwable e2) {
                                FileLog.m3e(e2);
                            }
                        } else if (file.exists()) {
                            try {
                                if (!file.delete()) {
                                    file.deleteOnExit();
                                }
                            } catch (Throwable e22) {
                                FileLog.m3e(e22);
                            }
                        }
                        try {
                            File qFile = new File(file.getParentFile(), "q_" + file.getName());
                            if (qFile.exists() && !qFile.delete()) {
                                qFile.deleteOnExit();
                            }
                        } catch (Throwable e222) {
                            FileLog.m3e(e222);
                        }
                    }
                    if (type == 2) {
                        ImageLoader.getInstance().clearMemory();
                    }
                }
            });
        }
    }
}
