package org.telegram.messenger;

import android.text.TextUtils;
import android.util.SparseArray;
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

public class FileLoader {
    private static volatile FileLoader[] Instance = new FileLoader[3];
    public static final int MEDIA_DIR_AUDIO = 1;
    public static final int MEDIA_DIR_CACHE = 4;
    public static final int MEDIA_DIR_DOCUMENT = 3;
    public static final int MEDIA_DIR_IMAGE = 0;
    public static final int MEDIA_DIR_VIDEO = 2;
    private static volatile DispatchQueue fileLoaderQueue = new DispatchQueue("fileUploadQueue");
    private static SparseArray<File> mediaDirs;
    private ArrayList<FileLoadOperation> activeFileLoadOperation = new ArrayList();
    private LinkedList<FileLoadOperation> audioLoadOperationQueue = new LinkedList();
    private int currentAccount;
    private int currentAudioLoadOperationsCount = 0;
    private int currentLoadOperationsCount = 0;
    private int currentPhotoLoadOperationsCount = 0;
    private int currentUploadOperationsCount = 0;
    private int currentUploadSmallOperationsCount = 0;
    private FileLoaderDelegate delegate = null;
    private ConcurrentHashMap<String, FileLoadOperation> loadOperationPaths = new ConcurrentHashMap();
    private ConcurrentHashMap<String, Boolean> loadOperationPathsUI = new ConcurrentHashMap(10, 1.0f, 2);
    private LinkedList<FileLoadOperation> loadOperationQueue = new LinkedList();
    private LinkedList<FileLoadOperation> photoLoadOperationQueue = new LinkedList();
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
        this.currentAccount = i;
    }

    public static void setMediaDirs(SparseArray<File> sparseArray) {
        mediaDirs = sparseArray;
    }

    public static File checkDirectory(int i) {
        return (File) mediaDirs.get(i);
    }

    public static java.io.File getDirectory(int r2) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
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
        r0 = mediaDirs;
        r0 = r0.get(r2);
        r0 = (java.io.File) r0;
        if (r0 != 0) goto L_0x0016;
    L_0x000a:
        r1 = 4;
        if (r2 == r1) goto L_0x0016;
    L_0x000d:
        r2 = mediaDirs;
        r2 = r2.get(r1);
        r0 = r2;
        r0 = (java.io.File) r0;
    L_0x0016:
        r2 = r0.isDirectory();	 Catch:{ Exception -> 0x001f }
        if (r2 != 0) goto L_0x001f;	 Catch:{ Exception -> 0x001f }
    L_0x001c:
        r0.mkdirs();	 Catch:{ Exception -> 0x001f }
    L_0x001f:
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.FileLoader.getDirectory(int):java.io.File");
    }

    public void cancelUploadFile(final String str, final boolean z) {
        fileLoaderQueue.postRunnable(new Runnable() {
            public void run() {
                FileUploadOperation fileUploadOperation;
                if (z) {
                    fileUploadOperation = (FileUploadOperation) FileLoader.this.uploadOperationPathsEnc.get(str);
                } else {
                    fileUploadOperation = (FileUploadOperation) FileLoader.this.uploadOperationPaths.get(str);
                }
                FileLoader.this.uploadSizes.remove(str);
                if (fileUploadOperation != null) {
                    FileLoader.this.uploadOperationPathsEnc.remove(str);
                    FileLoader.this.uploadOperationQueue.remove(fileUploadOperation);
                    FileLoader.this.uploadSmallOperationQueue.remove(fileUploadOperation);
                    fileUploadOperation.cancel();
                }
            }
        });
    }

    public void checkUploadNewDataAvailable(String str, boolean z, long j, long j2) {
        final boolean z2 = z;
        final String str2 = str;
        final long j3 = j;
        final long j4 = j2;
        fileLoaderQueue.postRunnable(new Runnable() {
            public void run() {
                FileUploadOperation fileUploadOperation;
                if (z2) {
                    fileUploadOperation = (FileUploadOperation) FileLoader.this.uploadOperationPathsEnc.get(str2);
                } else {
                    fileUploadOperation = (FileUploadOperation) FileLoader.this.uploadOperationPaths.get(str2);
                }
                if (fileUploadOperation != null) {
                    fileUploadOperation.checkNewDataAvailable(j3, j4);
                } else if (j4 != 0) {
                    FileLoader.this.uploadSizes.put(str2, Long.valueOf(j4));
                }
            }
        });
    }

    public void uploadFile(String str, boolean z, boolean z2, int i) {
        uploadFile(str, z, z2, 0, i);
    }

    public void uploadFile(String str, boolean z, boolean z2, int i, int i2) {
        if (str != null) {
            final boolean z3 = z;
            final String str2 = str;
            final int i3 = i;
            final int i4 = i2;
            final boolean z4 = z2;
            fileLoaderQueue.postRunnable(new Runnable() {

                /* renamed from: org.telegram.messenger.FileLoader$3$1 */
                class C18011 implements FileUploadOperationDelegate {

                    /* renamed from: org.telegram.messenger.FileLoader$3$1$2 */
                    class C01622 implements Runnable {
                        C01622() {
                        }

                        public void run() {
                            if (z3) {
                                FileLoader.this.uploadOperationPathsEnc.remove(str2);
                            } else {
                                FileLoader.this.uploadOperationPaths.remove(str2);
                            }
                            if (FileLoader.this.delegate != null) {
                                FileLoader.this.delegate.fileDidFailedUpload(str2, z3);
                            }
                            FileUploadOperation fileUploadOperation;
                            if (z4) {
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
                    }

                    C18011() {
                    }

                    public void didFinishUploadingFile(FileUploadOperation fileUploadOperation, InputFile inputFile, InputEncryptedFile inputEncryptedFile, byte[] bArr, byte[] bArr2) {
                        final InputFile inputFile2 = inputFile;
                        final InputEncryptedFile inputEncryptedFile2 = inputEncryptedFile;
                        final byte[] bArr3 = bArr;
                        final byte[] bArr4 = bArr2;
                        final FileUploadOperation fileUploadOperation2 = fileUploadOperation;
                        FileLoader.fileLoaderQueue.postRunnable(new Runnable() {
                            public void run() {
                                if (z3) {
                                    FileLoader.this.uploadOperationPathsEnc.remove(str2);
                                } else {
                                    FileLoader.this.uploadOperationPaths.remove(str2);
                                }
                                FileUploadOperation fileUploadOperation;
                                if (z4) {
                                    FileLoader.this.currentUploadSmallOperationsCount = FileLoader.this.currentUploadSmallOperationsCount - 1;
                                    if (FileLoader.this.currentUploadSmallOperationsCount < 1) {
                                        fileUploadOperation = (FileUploadOperation) FileLoader.this.uploadSmallOperationQueue.poll();
                                        if (fileUploadOperation != null) {
                                            FileLoader.this.currentUploadSmallOperationsCount = FileLoader.this.currentUploadSmallOperationsCount + 1;
                                            fileUploadOperation.start();
                                        }
                                    }
                                } else {
                                    FileLoader.this.currentUploadOperationsCount = FileLoader.this.currentUploadOperationsCount - 1;
                                    if (FileLoader.this.currentUploadOperationsCount < 1) {
                                        fileUploadOperation = (FileUploadOperation) FileLoader.this.uploadOperationQueue.poll();
                                        if (fileUploadOperation != null) {
                                            FileLoader.this.currentUploadOperationsCount = FileLoader.this.currentUploadOperationsCount + 1;
                                            fileUploadOperation.start();
                                        }
                                    }
                                }
                                if (FileLoader.this.delegate != null) {
                                    FileLoader.this.delegate.fileDidUploaded(str2, inputFile2, inputEncryptedFile2, bArr3, bArr4, fileUploadOperation2.getTotalFileSize());
                                }
                            }
                        });
                    }

                    public void didFailedUploadingFile(FileUploadOperation fileUploadOperation) {
                        FileLoader.fileLoaderQueue.postRunnable(new C01622());
                    }

                    public void didChangedUploadProgress(FileUploadOperation fileUploadOperation, float f) {
                        if (FileLoader.this.delegate != null) {
                            FileLoader.this.delegate.fileUploadProgressChanged(str2, f, z3);
                        }
                    }
                }

                public void run() {
                    if (z3) {
                        if (FileLoader.this.uploadOperationPathsEnc.containsKey(str2)) {
                            return;
                        }
                    } else if (FileLoader.this.uploadOperationPaths.containsKey(str2)) {
                        return;
                    }
                    int i = i3;
                    if (!(i == 0 || ((Long) FileLoader.this.uploadSizes.get(str2)) == null)) {
                        i = 0;
                        FileLoader.this.uploadSizes.remove(str2);
                    }
                    FileUploadOperation fileUploadOperation = new FileUploadOperation(FileLoader.this.currentAccount, str2, z3, i, i4);
                    if (z3) {
                        FileLoader.this.uploadOperationPathsEnc.put(str2, fileUploadOperation);
                    } else {
                        FileLoader.this.uploadOperationPaths.put(str2, fileUploadOperation);
                    }
                    fileUploadOperation.setDelegate(new C18011());
                    if (z4) {
                        if (FileLoader.this.currentUploadSmallOperationsCount < 1) {
                            FileLoader.this.currentUploadSmallOperationsCount = FileLoader.this.currentUploadSmallOperationsCount + 1;
                            fileUploadOperation.start();
                        } else {
                            FileLoader.this.uploadSmallOperationQueue.add(fileUploadOperation);
                        }
                    } else if (FileLoader.this.currentUploadOperationsCount < 1) {
                        FileLoader.this.currentUploadOperationsCount = FileLoader.this.currentUploadOperationsCount + 1;
                        fileUploadOperation.start();
                    } else {
                        FileLoader.this.uploadOperationQueue.add(fileUploadOperation);
                    }
                }
            });
        }
    }

    public void cancelLoadFile(Document document) {
        cancelLoadFile(document, null, null, null);
    }

    public void cancelLoadFile(TL_webDocument tL_webDocument) {
        cancelLoadFile(null, tL_webDocument, null, null);
    }

    public void cancelLoadFile(PhotoSize photoSize) {
        cancelLoadFile(null, null, photoSize.location, null);
    }

    public void cancelLoadFile(FileLocation fileLocation, String str) {
        cancelLoadFile(null, null, fileLocation, str);
    }

    private void cancelLoadFile(Document document, TL_webDocument tL_webDocument, FileLocation fileLocation, String str) {
        if (fileLocation != null || document != null || tL_webDocument != null) {
            str = fileLocation != null ? getAttachFileName(fileLocation, str) : document != null ? getAttachFileName(document) : tL_webDocument != null ? getAttachFileName(tL_webDocument) : null;
            final String str2 = str;
            if (str2 != null) {
                this.loadOperationPathsUI.remove(str2);
                final Document document2 = document;
                final TL_webDocument tL_webDocument2 = tL_webDocument;
                final FileLocation fileLocation2 = fileLocation;
                fileLoaderQueue.postRunnable(new Runnable() {
                    public void run() {
                        FileLoadOperation fileLoadOperation = (FileLoadOperation) FileLoader.this.loadOperationPaths.remove(str2);
                        if (fileLoadOperation != null) {
                            if (!MessageObject.isVoiceDocument(document2)) {
                                if (!MessageObject.isVoiceWebDocument(tL_webDocument2)) {
                                    if (fileLocation2 == null) {
                                        if (!MessageObject.isImageWebDocument(tL_webDocument2)) {
                                            if (!FileLoader.this.loadOperationQueue.remove(fileLoadOperation)) {
                                                FileLoader.this.currentLoadOperationsCount = FileLoader.this.currentLoadOperationsCount - 1;
                                            }
                                            FileLoader.this.activeFileLoadOperation.remove(fileLoadOperation);
                                            fileLoadOperation.cancel();
                                        }
                                    }
                                    if (!FileLoader.this.photoLoadOperationQueue.remove(fileLoadOperation)) {
                                        FileLoader.this.currentPhotoLoadOperationsCount = FileLoader.this.currentPhotoLoadOperationsCount - 1;
                                    }
                                    fileLoadOperation.cancel();
                                }
                            }
                            if (!FileLoader.this.audioLoadOperationQueue.remove(fileLoadOperation)) {
                                FileLoader.this.currentAudioLoadOperationsCount = FileLoader.this.currentAudioLoadOperationsCount - 1;
                            }
                            fileLoadOperation.cancel();
                        }
                    }
                });
            }
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

    public void loadFile(PhotoSize photoSize, String str, int i) {
        if (photoSize != null) {
            if (i == 0 && photoSize != null && (photoSize.size == 0 || photoSize.location.key != null)) {
                i = 1;
            }
            String str2 = str;
            loadFile(null, null, photoSize.location, str2, photoSize.size, false, i);
        }
    }

    public void loadFile(Document document, boolean z, int i) {
        if (document != null) {
            if (!(i != 0 || document == null || document.key == null)) {
                i = 1;
            }
            loadFile(document, null, null, null, 0, z, i);
        }
    }

    public void loadFile(TL_webDocument tL_webDocument, boolean z, int i) {
        loadFile(null, tL_webDocument, null, null, 0, z, i);
    }

    public void loadFile(FileLocation fileLocation, String str, int i, int i2) {
        if (fileLocation != null) {
            if (i2 == 0 && (i == 0 || !(fileLocation == null || fileLocation.key == null))) {
                i2 = 1;
            }
            loadFile(null, null, fileLocation, str, i, true, i2);
        }
    }

    private void pauseCurrentFileLoadOperations(FileLoadOperation fileLoadOperation) {
        int i = 0;
        while (i < this.activeFileLoadOperation.size()) {
            FileLoadOperation fileLoadOperation2 = (FileLoadOperation) this.activeFileLoadOperation.get(i);
            if (fileLoadOperation2 != fileLoadOperation) {
                this.activeFileLoadOperation.remove(fileLoadOperation2);
                i--;
                fileLoadOperation2.pause();
                this.loadOperationQueue.add(0, fileLoadOperation2);
                if (fileLoadOperation2.wasStarted()) {
                    this.currentLoadOperationsCount--;
                }
            }
            i++;
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private FileLoadOperation loadFileInternal(Document document, TL_webDocument tL_webDocument, FileLocation fileLocation, String str, int i, boolean z, FileStreamLoadOperation fileStreamLoadOperation, int i2, int i3) {
        String attachFileName;
        String str2;
        FileLoadOperation fileLoadOperation;
        LinkedList linkedList;
        int indexOf;
        File directory;
        FileLoadOperation fileLoadOperation2;
        int i4;
        File file;
        FileLoadOperation fileLoadOperation3;
        FileLoadOperation fileLoadOperation4;
        final int i5;
        C18025 c18025;
        C18025 c180252;
        final TL_webDocument tL_webDocument2;
        int i6;
        final FileLocation fileLocation2;
        FileLoader fileLoader = this;
        final Document document2 = document;
        TL_webDocument tL_webDocument3 = tL_webDocument;
        FileLocation fileLocation3 = fileLocation;
        FileStreamLoadOperation fileStreamLoadOperation2 = fileStreamLoadOperation;
        int i7 = i2;
        int i8 = i3;
        if (fileLocation3 != null) {
            attachFileName = getAttachFileName(fileLocation, str);
        } else if (document2 != null) {
            attachFileName = getAttachFileName(document);
        } else if (tL_webDocument3 != null) {
            attachFileName = getAttachFileName(tL_webDocument);
        } else {
            str2 = null;
            if (str2 != null) {
                if (str2.contains("-2147483648")) {
                    if (!(TextUtils.isEmpty(str2) || str2.contains("-2147483648"))) {
                        fileLoader.loadOperationPathsUI.put(str2, Boolean.valueOf(true));
                    }
                    fileLoadOperation = (FileLoadOperation) fileLoader.loadOperationPaths.get(str2);
                    if (fileLoadOperation == null) {
                        if (i7 != 0 || z) {
                            fileLoadOperation.setForceRequest(true);
                            if (!MessageObject.isVoiceDocument(document)) {
                                if (MessageObject.isVoiceWebDocument(tL_webDocument)) {
                                    if (fileLocation3 == null) {
                                        if (MessageObject.isImageWebDocument(tL_webDocument)) {
                                            linkedList = fileLoader.loadOperationQueue;
                                            if (linkedList != null) {
                                                indexOf = linkedList.indexOf(fileLoadOperation);
                                                if (indexOf <= 0) {
                                                    linkedList.remove(indexOf);
                                                    if (i7 != 0) {
                                                        linkedList.add(0, fileLoadOperation);
                                                    } else if (linkedList != fileLoader.audioLoadOperationQueue) {
                                                        if (fileLoadOperation.start(fileStreamLoadOperation2, i7)) {
                                                            fileLoader.currentAudioLoadOperationsCount++;
                                                        }
                                                    } else if (linkedList == fileLoader.photoLoadOperationQueue) {
                                                        if (fileLoadOperation.start(fileStreamLoadOperation2, i7)) {
                                                            fileLoader.currentLoadOperationsCount++;
                                                        }
                                                        if (fileLoadOperation.wasStarted() && !fileLoader.activeFileLoadOperation.contains(fileLoadOperation)) {
                                                            if (fileStreamLoadOperation2 != null) {
                                                                pauseCurrentFileLoadOperations(fileLoadOperation);
                                                            }
                                                            fileLoader.activeFileLoadOperation.add(fileLoadOperation);
                                                        }
                                                    } else if (fileLoadOperation.start(fileStreamLoadOperation2, i7)) {
                                                        fileLoader.currentPhotoLoadOperationsCount++;
                                                    }
                                                } else {
                                                    if (fileStreamLoadOperation2 != null) {
                                                        pauseCurrentFileLoadOperations(fileLoadOperation);
                                                    }
                                                    fileLoadOperation.start(fileStreamLoadOperation2, i7);
                                                    if (linkedList == fileLoader.loadOperationQueue && !fileLoader.activeFileLoadOperation.contains(fileLoadOperation)) {
                                                        fileLoader.activeFileLoadOperation.add(fileLoadOperation);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    linkedList = fileLoader.photoLoadOperationQueue;
                                    if (linkedList != null) {
                                        indexOf = linkedList.indexOf(fileLoadOperation);
                                        if (indexOf <= 0) {
                                            if (fileStreamLoadOperation2 != null) {
                                                pauseCurrentFileLoadOperations(fileLoadOperation);
                                            }
                                            fileLoadOperation.start(fileStreamLoadOperation2, i7);
                                            fileLoader.activeFileLoadOperation.add(fileLoadOperation);
                                        } else {
                                            linkedList.remove(indexOf);
                                            if (i7 != 0) {
                                                linkedList.add(0, fileLoadOperation);
                                            } else if (linkedList != fileLoader.audioLoadOperationQueue) {
                                                if (linkedList == fileLoader.photoLoadOperationQueue) {
                                                    if (fileLoadOperation.start(fileStreamLoadOperation2, i7)) {
                                                        fileLoader.currentLoadOperationsCount++;
                                                    }
                                                    if (fileStreamLoadOperation2 != null) {
                                                        pauseCurrentFileLoadOperations(fileLoadOperation);
                                                    }
                                                    fileLoader.activeFileLoadOperation.add(fileLoadOperation);
                                                } else if (fileLoadOperation.start(fileStreamLoadOperation2, i7)) {
                                                    fileLoader.currentPhotoLoadOperationsCount++;
                                                }
                                            } else if (fileLoadOperation.start(fileStreamLoadOperation2, i7)) {
                                                fileLoader.currentAudioLoadOperationsCount++;
                                            }
                                        }
                                    }
                                }
                            }
                            linkedList = fileLoader.audioLoadOperationQueue;
                            if (linkedList != null) {
                                indexOf = linkedList.indexOf(fileLoadOperation);
                                if (indexOf <= 0) {
                                    linkedList.remove(indexOf);
                                    if (i7 != 0) {
                                        linkedList.add(0, fileLoadOperation);
                                    } else if (linkedList != fileLoader.audioLoadOperationQueue) {
                                        if (fileLoadOperation.start(fileStreamLoadOperation2, i7)) {
                                            fileLoader.currentAudioLoadOperationsCount++;
                                        }
                                    } else if (linkedList == fileLoader.photoLoadOperationQueue) {
                                        if (fileLoadOperation.start(fileStreamLoadOperation2, i7)) {
                                            fileLoader.currentLoadOperationsCount++;
                                        }
                                        if (fileStreamLoadOperation2 != null) {
                                            pauseCurrentFileLoadOperations(fileLoadOperation);
                                        }
                                        fileLoader.activeFileLoadOperation.add(fileLoadOperation);
                                    } else if (fileLoadOperation.start(fileStreamLoadOperation2, i7)) {
                                        fileLoader.currentPhotoLoadOperationsCount++;
                                    }
                                } else {
                                    if (fileStreamLoadOperation2 != null) {
                                        pauseCurrentFileLoadOperations(fileLoadOperation);
                                    }
                                    fileLoadOperation.start(fileStreamLoadOperation2, i7);
                                    fileLoader.activeFileLoadOperation.add(fileLoadOperation);
                                }
                            }
                        }
                        return fileLoadOperation;
                    }
                    directory = getDirectory(4);
                    if (fileLocation3 == null) {
                        fileLoadOperation = new FileLoadOperation(fileLocation3, str, i);
                    } else {
                        if (document2 != null) {
                            fileLoadOperation = new FileLoadOperation(document2);
                            if (!MessageObject.isVoiceDocument(document)) {
                            }
                        } else if (tL_webDocument3 == null) {
                            fileLoadOperation = new FileLoadOperation(tL_webDocument3);
                            if (MessageObject.isVoiceWebDocument(tL_webDocument)) {
                                if (MessageObject.isVideoWebDocument(tL_webDocument)) {
                                    if (MessageObject.isImageWebDocument(tL_webDocument)) {
                                    }
                                    fileLoadOperation2 = fileLoadOperation;
                                    i4 = 3;
                                    if (i8 != 0) {
                                        if (i8 == 2) {
                                            fileLoadOperation2.setEncryptFile(true);
                                        }
                                        file = directory;
                                    } else {
                                        file = getDirectory(i4);
                                    }
                                    fileLoadOperation2.setPaths(fileLoader.currentAccount, file, directory);
                                    fileLoadOperation3 = fileLoadOperation2;
                                    attachFileName = str2;
                                    fileLoadOperation4 = fileLoadOperation3;
                                    i5 = i4;
                                    c18025 = c180252;
                                    tL_webDocument2 = tL_webDocument3;
                                    i6 = i4;
                                    fileLocation2 = fileLocation3;
                                    c180252 = new FileLoadOperationDelegate() {
                                        public void didFinishLoadingFile(FileLoadOperation fileLoadOperation, File file) {
                                            FileLoader.this.loadOperationPathsUI.remove(attachFileName);
                                            if (FileLoader.this.delegate != null) {
                                                FileLoader.this.delegate.fileDidLoaded(attachFileName, file, i5);
                                            }
                                            FileLoader.this.checkDownloadQueue(document2, tL_webDocument2, fileLocation2, attachFileName);
                                        }

                                        public void didFailedLoadingFile(FileLoadOperation fileLoadOperation, int i) {
                                            FileLoader.this.loadOperationPathsUI.remove(attachFileName);
                                            FileLoader.this.checkDownloadQueue(document2, tL_webDocument2, fileLocation2, attachFileName);
                                            if (FileLoader.this.delegate != null) {
                                                FileLoader.this.delegate.fileDidFailedLoad(attachFileName, i);
                                            }
                                        }

                                        public void didChangedLoadProgress(FileLoadOperation fileLoadOperation, float f) {
                                            if (FileLoader.this.delegate != null) {
                                                FileLoader.this.delegate.fileLoadProgressChanged(attachFileName, f);
                                            }
                                        }
                                    };
                                    fileLoadOperation4.setDelegate(c18025);
                                    fileLoader.loadOperationPaths.put(str2, fileLoadOperation4);
                                    if (z) {
                                    }
                                    if (i6 != 1) {
                                        if (fileLocation3 == null) {
                                            if (MessageObject.isImageWebDocument(tL_webDocument)) {
                                                if (i7 == 0) {
                                                    if (fileLoader.currentLoadOperationsCount < i8) {
                                                        if (z) {
                                                            fileLoader.loadOperationQueue.add(0, fileLoadOperation4);
                                                        } else {
                                                            fileLoader.loadOperationQueue.add(fileLoadOperation4);
                                                        }
                                                    }
                                                }
                                                if (fileLoadOperation4.start(fileStreamLoadOperation2, i7)) {
                                                    fileLoader.currentLoadOperationsCount++;
                                                    fileLoader.activeFileLoadOperation.add(fileLoadOperation4);
                                                }
                                                pauseCurrentFileLoadOperations(fileLoadOperation4);
                                            }
                                        }
                                        if (i7 == 0) {
                                            if (fileLoader.currentPhotoLoadOperationsCount < i8) {
                                                if (z) {
                                                    fileLoader.photoLoadOperationQueue.add(0, fileLoadOperation4);
                                                } else {
                                                    fileLoader.photoLoadOperationQueue.add(fileLoadOperation4);
                                                }
                                            }
                                        }
                                        if (fileLoadOperation4.start(fileStreamLoadOperation2, i7)) {
                                            fileLoader.currentPhotoLoadOperationsCount++;
                                        }
                                    } else {
                                        if (i7 == 0) {
                                            if (fileLoader.currentAudioLoadOperationsCount < i8) {
                                                if (z) {
                                                    fileLoader.audioLoadOperationQueue.add(0, fileLoadOperation4);
                                                } else {
                                                    fileLoader.audioLoadOperationQueue.add(fileLoadOperation4);
                                                }
                                            }
                                        }
                                        if (fileLoadOperation4.start(fileStreamLoadOperation2, i7)) {
                                            fileLoader.currentAudioLoadOperationsCount++;
                                        }
                                    }
                                    return fileLoadOperation4;
                                }
                                fileLoadOperation2 = fileLoadOperation;
                                i4 = 2;
                                if (i8 != 0) {
                                    file = getDirectory(i4);
                                } else {
                                    if (i8 == 2) {
                                        fileLoadOperation2.setEncryptFile(true);
                                    }
                                    file = directory;
                                }
                                fileLoadOperation2.setPaths(fileLoader.currentAccount, file, directory);
                                fileLoadOperation3 = fileLoadOperation2;
                                attachFileName = str2;
                                fileLoadOperation4 = fileLoadOperation3;
                                i5 = i4;
                                c18025 = c180252;
                                tL_webDocument2 = tL_webDocument3;
                                i6 = i4;
                                fileLocation2 = fileLocation3;
                                c180252 = /* anonymous class already generated */;
                                fileLoadOperation4.setDelegate(c18025);
                                fileLoader.loadOperationPaths.put(str2, fileLoadOperation4);
                                i8 = z ? 3 : 1;
                                if (i6 != 1) {
                                    if (i7 == 0) {
                                        if (fileLoader.currentAudioLoadOperationsCount < i8) {
                                            if (z) {
                                                fileLoader.audioLoadOperationQueue.add(0, fileLoadOperation4);
                                            } else {
                                                fileLoader.audioLoadOperationQueue.add(fileLoadOperation4);
                                            }
                                        }
                                    }
                                    if (fileLoadOperation4.start(fileStreamLoadOperation2, i7)) {
                                        fileLoader.currentAudioLoadOperationsCount++;
                                    }
                                } else {
                                    if (fileLocation3 == null) {
                                        if (MessageObject.isImageWebDocument(tL_webDocument)) {
                                            if (i7 == 0) {
                                                if (fileLoader.currentLoadOperationsCount < i8) {
                                                    if (z) {
                                                        fileLoader.loadOperationQueue.add(0, fileLoadOperation4);
                                                    } else {
                                                        fileLoader.loadOperationQueue.add(fileLoadOperation4);
                                                    }
                                                }
                                            }
                                            if (fileLoadOperation4.start(fileStreamLoadOperation2, i7)) {
                                                fileLoader.currentLoadOperationsCount++;
                                                fileLoader.activeFileLoadOperation.add(fileLoadOperation4);
                                            }
                                            if (fileLoadOperation4.wasStarted() && fileStreamLoadOperation2 != null) {
                                                pauseCurrentFileLoadOperations(fileLoadOperation4);
                                            }
                                        }
                                    }
                                    if (i7 == 0) {
                                        if (fileLoader.currentPhotoLoadOperationsCount < i8) {
                                            if (z) {
                                                fileLoader.photoLoadOperationQueue.add(0, fileLoadOperation4);
                                            } else {
                                                fileLoader.photoLoadOperationQueue.add(fileLoadOperation4);
                                            }
                                        }
                                    }
                                    if (fileLoadOperation4.start(fileStreamLoadOperation2, i7)) {
                                        fileLoader.currentPhotoLoadOperationsCount++;
                                    }
                                }
                                return fileLoadOperation4;
                            }
                        } else {
                            i4 = 4;
                            fileLoadOperation2 = fileLoadOperation;
                            if (i8 != 0) {
                                file = getDirectory(i4);
                            } else {
                                if (i8 == 2) {
                                    fileLoadOperation2.setEncryptFile(true);
                                }
                                file = directory;
                            }
                            fileLoadOperation2.setPaths(fileLoader.currentAccount, file, directory);
                            fileLoadOperation3 = fileLoadOperation2;
                            attachFileName = str2;
                            fileLoadOperation4 = fileLoadOperation3;
                            i5 = i4;
                            c18025 = c180252;
                            tL_webDocument2 = tL_webDocument3;
                            i6 = i4;
                            fileLocation2 = fileLocation3;
                            c180252 = /* anonymous class already generated */;
                            fileLoadOperation4.setDelegate(c18025);
                            fileLoader.loadOperationPaths.put(str2, fileLoadOperation4);
                            if (z) {
                            }
                            if (i6 != 1) {
                                if (i7 == 0) {
                                    if (fileLoader.currentAudioLoadOperationsCount < i8) {
                                        if (z) {
                                            fileLoader.audioLoadOperationQueue.add(0, fileLoadOperation4);
                                        } else {
                                            fileLoader.audioLoadOperationQueue.add(fileLoadOperation4);
                                        }
                                    }
                                }
                                if (fileLoadOperation4.start(fileStreamLoadOperation2, i7)) {
                                    fileLoader.currentAudioLoadOperationsCount++;
                                }
                            } else {
                                if (fileLocation3 == null) {
                                    if (MessageObject.isImageWebDocument(tL_webDocument)) {
                                        if (i7 == 0) {
                                            if (fileLoader.currentLoadOperationsCount < i8) {
                                                if (z) {
                                                    fileLoader.loadOperationQueue.add(0, fileLoadOperation4);
                                                } else {
                                                    fileLoader.loadOperationQueue.add(fileLoadOperation4);
                                                }
                                            }
                                        }
                                        if (fileLoadOperation4.start(fileStreamLoadOperation2, i7)) {
                                            fileLoader.currentLoadOperationsCount++;
                                            fileLoader.activeFileLoadOperation.add(fileLoadOperation4);
                                        }
                                        pauseCurrentFileLoadOperations(fileLoadOperation4);
                                    }
                                }
                                if (i7 == 0) {
                                    if (fileLoader.currentPhotoLoadOperationsCount < i8) {
                                        if (z) {
                                            fileLoader.photoLoadOperationQueue.add(0, fileLoadOperation4);
                                        } else {
                                            fileLoader.photoLoadOperationQueue.add(fileLoadOperation4);
                                        }
                                    }
                                }
                                if (fileLoadOperation4.start(fileStreamLoadOperation2, i7)) {
                                    fileLoader.currentPhotoLoadOperationsCount++;
                                }
                            }
                            return fileLoadOperation4;
                        }
                        fileLoadOperation2 = fileLoadOperation;
                        i4 = 1;
                        if (i8 != 0) {
                            if (i8 == 2) {
                                fileLoadOperation2.setEncryptFile(true);
                            }
                            file = directory;
                        } else {
                            file = getDirectory(i4);
                        }
                        fileLoadOperation2.setPaths(fileLoader.currentAccount, file, directory);
                        fileLoadOperation3 = fileLoadOperation2;
                        attachFileName = str2;
                        fileLoadOperation4 = fileLoadOperation3;
                        i5 = i4;
                        c18025 = c180252;
                        tL_webDocument2 = tL_webDocument3;
                        i6 = i4;
                        fileLocation2 = fileLocation3;
                        c180252 = /* anonymous class already generated */;
                        fileLoadOperation4.setDelegate(c18025);
                        fileLoader.loadOperationPaths.put(str2, fileLoadOperation4);
                        if (z) {
                        }
                        if (i6 != 1) {
                            if (fileLocation3 == null) {
                                if (MessageObject.isImageWebDocument(tL_webDocument)) {
                                    if (i7 == 0) {
                                        if (fileLoader.currentLoadOperationsCount < i8) {
                                            if (z) {
                                                fileLoader.loadOperationQueue.add(fileLoadOperation4);
                                            } else {
                                                fileLoader.loadOperationQueue.add(0, fileLoadOperation4);
                                            }
                                        }
                                    }
                                    if (fileLoadOperation4.start(fileStreamLoadOperation2, i7)) {
                                        fileLoader.currentLoadOperationsCount++;
                                        fileLoader.activeFileLoadOperation.add(fileLoadOperation4);
                                    }
                                    pauseCurrentFileLoadOperations(fileLoadOperation4);
                                }
                            }
                            if (i7 == 0) {
                                if (fileLoader.currentPhotoLoadOperationsCount < i8) {
                                    if (z) {
                                        fileLoader.photoLoadOperationQueue.add(fileLoadOperation4);
                                    } else {
                                        fileLoader.photoLoadOperationQueue.add(0, fileLoadOperation4);
                                    }
                                }
                            }
                            if (fileLoadOperation4.start(fileStreamLoadOperation2, i7)) {
                                fileLoader.currentPhotoLoadOperationsCount++;
                            }
                        } else {
                            if (i7 == 0) {
                                if (fileLoader.currentAudioLoadOperationsCount < i8) {
                                    if (z) {
                                        fileLoader.audioLoadOperationQueue.add(fileLoadOperation4);
                                    } else {
                                        fileLoader.audioLoadOperationQueue.add(0, fileLoadOperation4);
                                    }
                                }
                            }
                            if (fileLoadOperation4.start(fileStreamLoadOperation2, i7)) {
                                fileLoader.currentAudioLoadOperationsCount++;
                            }
                        }
                        return fileLoadOperation4;
                    }
                    fileLoadOperation2 = fileLoadOperation;
                    i4 = 0;
                    if (i8 != 0) {
                        file = getDirectory(i4);
                    } else {
                        if (i8 == 2) {
                            fileLoadOperation2.setEncryptFile(true);
                        }
                        file = directory;
                    }
                    fileLoadOperation2.setPaths(fileLoader.currentAccount, file, directory);
                    fileLoadOperation3 = fileLoadOperation2;
                    attachFileName = str2;
                    fileLoadOperation4 = fileLoadOperation3;
                    i5 = i4;
                    c18025 = c180252;
                    tL_webDocument2 = tL_webDocument3;
                    i6 = i4;
                    fileLocation2 = fileLocation3;
                    c180252 = /* anonymous class already generated */;
                    fileLoadOperation4.setDelegate(c18025);
                    fileLoader.loadOperationPaths.put(str2, fileLoadOperation4);
                    if (z) {
                    }
                    if (i6 != 1) {
                        if (i7 == 0) {
                            if (fileLoader.currentAudioLoadOperationsCount < i8) {
                                if (z) {
                                    fileLoader.audioLoadOperationQueue.add(0, fileLoadOperation4);
                                } else {
                                    fileLoader.audioLoadOperationQueue.add(fileLoadOperation4);
                                }
                            }
                        }
                        if (fileLoadOperation4.start(fileStreamLoadOperation2, i7)) {
                            fileLoader.currentAudioLoadOperationsCount++;
                        }
                    } else {
                        if (fileLocation3 == null) {
                            if (MessageObject.isImageWebDocument(tL_webDocument)) {
                                if (i7 == 0) {
                                    if (fileLoader.currentLoadOperationsCount < i8) {
                                        if (z) {
                                            fileLoader.loadOperationQueue.add(0, fileLoadOperation4);
                                        } else {
                                            fileLoader.loadOperationQueue.add(fileLoadOperation4);
                                        }
                                    }
                                }
                                if (fileLoadOperation4.start(fileStreamLoadOperation2, i7)) {
                                    fileLoader.currentLoadOperationsCount++;
                                    fileLoader.activeFileLoadOperation.add(fileLoadOperation4);
                                }
                                pauseCurrentFileLoadOperations(fileLoadOperation4);
                            }
                        }
                        if (i7 == 0) {
                            if (fileLoader.currentPhotoLoadOperationsCount < i8) {
                                if (z) {
                                    fileLoader.photoLoadOperationQueue.add(0, fileLoadOperation4);
                                } else {
                                    fileLoader.photoLoadOperationQueue.add(fileLoadOperation4);
                                }
                            }
                        }
                        if (fileLoadOperation4.start(fileStreamLoadOperation2, i7)) {
                            fileLoader.currentPhotoLoadOperationsCount++;
                        }
                    }
                    return fileLoadOperation4;
                }
            }
            return null;
        }
        str2 = attachFileName;
        if (str2 != null) {
            if (str2.contains("-2147483648")) {
                fileLoader.loadOperationPathsUI.put(str2, Boolean.valueOf(true));
                fileLoadOperation = (FileLoadOperation) fileLoader.loadOperationPaths.get(str2);
                if (fileLoadOperation == null) {
                    directory = getDirectory(4);
                    if (fileLocation3 == null) {
                        if (document2 != null) {
                            fileLoadOperation = new FileLoadOperation(document2);
                            if (MessageObject.isVoiceDocument(document)) {
                            }
                        } else if (tL_webDocument3 == null) {
                            i4 = 4;
                            fileLoadOperation2 = fileLoadOperation;
                            if (i8 != 0) {
                                if (i8 == 2) {
                                    fileLoadOperation2.setEncryptFile(true);
                                }
                                file = directory;
                            } else {
                                file = getDirectory(i4);
                            }
                            fileLoadOperation2.setPaths(fileLoader.currentAccount, file, directory);
                            fileLoadOperation3 = fileLoadOperation2;
                            attachFileName = str2;
                            fileLoadOperation4 = fileLoadOperation3;
                            i5 = i4;
                            c18025 = c180252;
                            tL_webDocument2 = tL_webDocument3;
                            i6 = i4;
                            fileLocation2 = fileLocation3;
                            c180252 = /* anonymous class already generated */;
                            fileLoadOperation4.setDelegate(c18025);
                            fileLoader.loadOperationPaths.put(str2, fileLoadOperation4);
                            if (z) {
                            }
                            if (i6 != 1) {
                                if (fileLocation3 == null) {
                                    if (MessageObject.isImageWebDocument(tL_webDocument)) {
                                        if (i7 == 0) {
                                            if (fileLoader.currentLoadOperationsCount < i8) {
                                                if (z) {
                                                    fileLoader.loadOperationQueue.add(fileLoadOperation4);
                                                } else {
                                                    fileLoader.loadOperationQueue.add(0, fileLoadOperation4);
                                                }
                                            }
                                        }
                                        if (fileLoadOperation4.start(fileStreamLoadOperation2, i7)) {
                                            fileLoader.currentLoadOperationsCount++;
                                            fileLoader.activeFileLoadOperation.add(fileLoadOperation4);
                                        }
                                        pauseCurrentFileLoadOperations(fileLoadOperation4);
                                    }
                                }
                                if (i7 == 0) {
                                    if (fileLoader.currentPhotoLoadOperationsCount < i8) {
                                        if (z) {
                                            fileLoader.photoLoadOperationQueue.add(fileLoadOperation4);
                                        } else {
                                            fileLoader.photoLoadOperationQueue.add(0, fileLoadOperation4);
                                        }
                                    }
                                }
                                if (fileLoadOperation4.start(fileStreamLoadOperation2, i7)) {
                                    fileLoader.currentPhotoLoadOperationsCount++;
                                }
                            } else {
                                if (i7 == 0) {
                                    if (fileLoader.currentAudioLoadOperationsCount < i8) {
                                        if (z) {
                                            fileLoader.audioLoadOperationQueue.add(fileLoadOperation4);
                                        } else {
                                            fileLoader.audioLoadOperationQueue.add(0, fileLoadOperation4);
                                        }
                                    }
                                }
                                if (fileLoadOperation4.start(fileStreamLoadOperation2, i7)) {
                                    fileLoader.currentAudioLoadOperationsCount++;
                                }
                            }
                            return fileLoadOperation4;
                        } else {
                            fileLoadOperation = new FileLoadOperation(tL_webDocument3);
                            if (MessageObject.isVoiceWebDocument(tL_webDocument)) {
                                if (MessageObject.isVideoWebDocument(tL_webDocument)) {
                                    if (MessageObject.isImageWebDocument(tL_webDocument)) {
                                    }
                                    fileLoadOperation2 = fileLoadOperation;
                                    i4 = 3;
                                    if (i8 != 0) {
                                        file = getDirectory(i4);
                                    } else {
                                        if (i8 == 2) {
                                            fileLoadOperation2.setEncryptFile(true);
                                        }
                                        file = directory;
                                    }
                                    fileLoadOperation2.setPaths(fileLoader.currentAccount, file, directory);
                                    fileLoadOperation3 = fileLoadOperation2;
                                    attachFileName = str2;
                                    fileLoadOperation4 = fileLoadOperation3;
                                    i5 = i4;
                                    c18025 = c180252;
                                    tL_webDocument2 = tL_webDocument3;
                                    i6 = i4;
                                    fileLocation2 = fileLocation3;
                                    c180252 = /* anonymous class already generated */;
                                    fileLoadOperation4.setDelegate(c18025);
                                    fileLoader.loadOperationPaths.put(str2, fileLoadOperation4);
                                    if (z) {
                                    }
                                    if (i6 != 1) {
                                        if (i7 == 0) {
                                            if (fileLoader.currentAudioLoadOperationsCount < i8) {
                                                if (z) {
                                                    fileLoader.audioLoadOperationQueue.add(0, fileLoadOperation4);
                                                } else {
                                                    fileLoader.audioLoadOperationQueue.add(fileLoadOperation4);
                                                }
                                            }
                                        }
                                        if (fileLoadOperation4.start(fileStreamLoadOperation2, i7)) {
                                            fileLoader.currentAudioLoadOperationsCount++;
                                        }
                                    } else {
                                        if (fileLocation3 == null) {
                                            if (MessageObject.isImageWebDocument(tL_webDocument)) {
                                                if (i7 == 0) {
                                                    if (fileLoader.currentLoadOperationsCount < i8) {
                                                        if (z) {
                                                            fileLoader.loadOperationQueue.add(0, fileLoadOperation4);
                                                        } else {
                                                            fileLoader.loadOperationQueue.add(fileLoadOperation4);
                                                        }
                                                    }
                                                }
                                                if (fileLoadOperation4.start(fileStreamLoadOperation2, i7)) {
                                                    fileLoader.currentLoadOperationsCount++;
                                                    fileLoader.activeFileLoadOperation.add(fileLoadOperation4);
                                                }
                                                pauseCurrentFileLoadOperations(fileLoadOperation4);
                                            }
                                        }
                                        if (i7 == 0) {
                                            if (fileLoader.currentPhotoLoadOperationsCount < i8) {
                                                if (z) {
                                                    fileLoader.photoLoadOperationQueue.add(0, fileLoadOperation4);
                                                } else {
                                                    fileLoader.photoLoadOperationQueue.add(fileLoadOperation4);
                                                }
                                            }
                                        }
                                        if (fileLoadOperation4.start(fileStreamLoadOperation2, i7)) {
                                            fileLoader.currentPhotoLoadOperationsCount++;
                                        }
                                    }
                                    return fileLoadOperation4;
                                }
                                fileLoadOperation2 = fileLoadOperation;
                                i4 = 2;
                                if (i8 != 0) {
                                    if (i8 == 2) {
                                        fileLoadOperation2.setEncryptFile(true);
                                    }
                                    file = directory;
                                } else {
                                    file = getDirectory(i4);
                                }
                                fileLoadOperation2.setPaths(fileLoader.currentAccount, file, directory);
                                fileLoadOperation3 = fileLoadOperation2;
                                attachFileName = str2;
                                fileLoadOperation4 = fileLoadOperation3;
                                i5 = i4;
                                c18025 = c180252;
                                tL_webDocument2 = tL_webDocument3;
                                i6 = i4;
                                fileLocation2 = fileLocation3;
                                c180252 = /* anonymous class already generated */;
                                fileLoadOperation4.setDelegate(c18025);
                                fileLoader.loadOperationPaths.put(str2, fileLoadOperation4);
                                if (z) {
                                }
                                if (i6 != 1) {
                                    if (fileLocation3 == null) {
                                        if (MessageObject.isImageWebDocument(tL_webDocument)) {
                                            if (i7 == 0) {
                                                if (fileLoader.currentLoadOperationsCount < i8) {
                                                    if (z) {
                                                        fileLoader.loadOperationQueue.add(fileLoadOperation4);
                                                    } else {
                                                        fileLoader.loadOperationQueue.add(0, fileLoadOperation4);
                                                    }
                                                }
                                            }
                                            if (fileLoadOperation4.start(fileStreamLoadOperation2, i7)) {
                                                fileLoader.currentLoadOperationsCount++;
                                                fileLoader.activeFileLoadOperation.add(fileLoadOperation4);
                                            }
                                            pauseCurrentFileLoadOperations(fileLoadOperation4);
                                        }
                                    }
                                    if (i7 == 0) {
                                        if (fileLoader.currentPhotoLoadOperationsCount < i8) {
                                            if (z) {
                                                fileLoader.photoLoadOperationQueue.add(fileLoadOperation4);
                                            } else {
                                                fileLoader.photoLoadOperationQueue.add(0, fileLoadOperation4);
                                            }
                                        }
                                    }
                                    if (fileLoadOperation4.start(fileStreamLoadOperation2, i7)) {
                                        fileLoader.currentPhotoLoadOperationsCount++;
                                    }
                                } else {
                                    if (i7 == 0) {
                                        if (fileLoader.currentAudioLoadOperationsCount < i8) {
                                            if (z) {
                                                fileLoader.audioLoadOperationQueue.add(fileLoadOperation4);
                                            } else {
                                                fileLoader.audioLoadOperationQueue.add(0, fileLoadOperation4);
                                            }
                                        }
                                    }
                                    if (fileLoadOperation4.start(fileStreamLoadOperation2, i7)) {
                                        fileLoader.currentAudioLoadOperationsCount++;
                                    }
                                }
                                return fileLoadOperation4;
                            }
                        }
                        fileLoadOperation2 = fileLoadOperation;
                        i4 = 1;
                        if (i8 != 0) {
                            file = getDirectory(i4);
                        } else {
                            if (i8 == 2) {
                                fileLoadOperation2.setEncryptFile(true);
                            }
                            file = directory;
                        }
                        fileLoadOperation2.setPaths(fileLoader.currentAccount, file, directory);
                        fileLoadOperation3 = fileLoadOperation2;
                        attachFileName = str2;
                        fileLoadOperation4 = fileLoadOperation3;
                        i5 = i4;
                        c18025 = c180252;
                        tL_webDocument2 = tL_webDocument3;
                        i6 = i4;
                        fileLocation2 = fileLocation3;
                        c180252 = /* anonymous class already generated */;
                        fileLoadOperation4.setDelegate(c18025);
                        fileLoader.loadOperationPaths.put(str2, fileLoadOperation4);
                        if (z) {
                        }
                        if (i6 != 1) {
                            if (i7 == 0) {
                                if (fileLoader.currentAudioLoadOperationsCount < i8) {
                                    if (z) {
                                        fileLoader.audioLoadOperationQueue.add(0, fileLoadOperation4);
                                    } else {
                                        fileLoader.audioLoadOperationQueue.add(fileLoadOperation4);
                                    }
                                }
                            }
                            if (fileLoadOperation4.start(fileStreamLoadOperation2, i7)) {
                                fileLoader.currentAudioLoadOperationsCount++;
                            }
                        } else {
                            if (fileLocation3 == null) {
                                if (MessageObject.isImageWebDocument(tL_webDocument)) {
                                    if (i7 == 0) {
                                        if (fileLoader.currentLoadOperationsCount < i8) {
                                            if (z) {
                                                fileLoader.loadOperationQueue.add(0, fileLoadOperation4);
                                            } else {
                                                fileLoader.loadOperationQueue.add(fileLoadOperation4);
                                            }
                                        }
                                    }
                                    if (fileLoadOperation4.start(fileStreamLoadOperation2, i7)) {
                                        fileLoader.currentLoadOperationsCount++;
                                        fileLoader.activeFileLoadOperation.add(fileLoadOperation4);
                                    }
                                    pauseCurrentFileLoadOperations(fileLoadOperation4);
                                }
                            }
                            if (i7 == 0) {
                                if (fileLoader.currentPhotoLoadOperationsCount < i8) {
                                    if (z) {
                                        fileLoader.photoLoadOperationQueue.add(0, fileLoadOperation4);
                                    } else {
                                        fileLoader.photoLoadOperationQueue.add(fileLoadOperation4);
                                    }
                                }
                            }
                            if (fileLoadOperation4.start(fileStreamLoadOperation2, i7)) {
                                fileLoader.currentPhotoLoadOperationsCount++;
                            }
                        }
                        return fileLoadOperation4;
                    }
                    fileLoadOperation = new FileLoadOperation(fileLocation3, str, i);
                    fileLoadOperation2 = fileLoadOperation;
                    i4 = 0;
                    if (i8 != 0) {
                        if (i8 == 2) {
                            fileLoadOperation2.setEncryptFile(true);
                        }
                        file = directory;
                    } else {
                        file = getDirectory(i4);
                    }
                    fileLoadOperation2.setPaths(fileLoader.currentAccount, file, directory);
                    fileLoadOperation3 = fileLoadOperation2;
                    attachFileName = str2;
                    fileLoadOperation4 = fileLoadOperation3;
                    i5 = i4;
                    c18025 = c180252;
                    tL_webDocument2 = tL_webDocument3;
                    i6 = i4;
                    fileLocation2 = fileLocation3;
                    c180252 = /* anonymous class already generated */;
                    fileLoadOperation4.setDelegate(c18025);
                    fileLoader.loadOperationPaths.put(str2, fileLoadOperation4);
                    if (z) {
                    }
                    if (i6 != 1) {
                        if (fileLocation3 == null) {
                            if (MessageObject.isImageWebDocument(tL_webDocument)) {
                                if (i7 == 0) {
                                    if (fileLoader.currentLoadOperationsCount < i8) {
                                        if (z) {
                                            fileLoader.loadOperationQueue.add(fileLoadOperation4);
                                        } else {
                                            fileLoader.loadOperationQueue.add(0, fileLoadOperation4);
                                        }
                                    }
                                }
                                if (fileLoadOperation4.start(fileStreamLoadOperation2, i7)) {
                                    fileLoader.currentLoadOperationsCount++;
                                    fileLoader.activeFileLoadOperation.add(fileLoadOperation4);
                                }
                                pauseCurrentFileLoadOperations(fileLoadOperation4);
                            }
                        }
                        if (i7 == 0) {
                            if (fileLoader.currentPhotoLoadOperationsCount < i8) {
                                if (z) {
                                    fileLoader.photoLoadOperationQueue.add(fileLoadOperation4);
                                } else {
                                    fileLoader.photoLoadOperationQueue.add(0, fileLoadOperation4);
                                }
                            }
                        }
                        if (fileLoadOperation4.start(fileStreamLoadOperation2, i7)) {
                            fileLoader.currentPhotoLoadOperationsCount++;
                        }
                    } else {
                        if (i7 == 0) {
                            if (fileLoader.currentAudioLoadOperationsCount < i8) {
                                if (z) {
                                    fileLoader.audioLoadOperationQueue.add(fileLoadOperation4);
                                } else {
                                    fileLoader.audioLoadOperationQueue.add(0, fileLoadOperation4);
                                }
                            }
                        }
                        if (fileLoadOperation4.start(fileStreamLoadOperation2, i7)) {
                            fileLoader.currentAudioLoadOperationsCount++;
                        }
                    }
                    return fileLoadOperation4;
                }
                fileLoadOperation.setForceRequest(true);
                if (MessageObject.isVoiceDocument(document)) {
                    if (MessageObject.isVoiceWebDocument(tL_webDocument)) {
                        if (fileLocation3 == null) {
                            if (MessageObject.isImageWebDocument(tL_webDocument)) {
                                linkedList = fileLoader.loadOperationQueue;
                                if (linkedList != null) {
                                    indexOf = linkedList.indexOf(fileLoadOperation);
                                    if (indexOf <= 0) {
                                        if (fileStreamLoadOperation2 != null) {
                                            pauseCurrentFileLoadOperations(fileLoadOperation);
                                        }
                                        fileLoadOperation.start(fileStreamLoadOperation2, i7);
                                        fileLoader.activeFileLoadOperation.add(fileLoadOperation);
                                    } else {
                                        linkedList.remove(indexOf);
                                        if (i7 != 0) {
                                            linkedList.add(0, fileLoadOperation);
                                        } else if (linkedList != fileLoader.audioLoadOperationQueue) {
                                            if (linkedList == fileLoader.photoLoadOperationQueue) {
                                                if (fileLoadOperation.start(fileStreamLoadOperation2, i7)) {
                                                    fileLoader.currentLoadOperationsCount++;
                                                }
                                                if (fileStreamLoadOperation2 != null) {
                                                    pauseCurrentFileLoadOperations(fileLoadOperation);
                                                }
                                                fileLoader.activeFileLoadOperation.add(fileLoadOperation);
                                            } else if (fileLoadOperation.start(fileStreamLoadOperation2, i7)) {
                                                fileLoader.currentPhotoLoadOperationsCount++;
                                            }
                                        } else if (fileLoadOperation.start(fileStreamLoadOperation2, i7)) {
                                            fileLoader.currentAudioLoadOperationsCount++;
                                        }
                                    }
                                }
                                return fileLoadOperation;
                            }
                        }
                        linkedList = fileLoader.photoLoadOperationQueue;
                        if (linkedList != null) {
                            indexOf = linkedList.indexOf(fileLoadOperation);
                            if (indexOf <= 0) {
                                linkedList.remove(indexOf);
                                if (i7 != 0) {
                                    linkedList.add(0, fileLoadOperation);
                                } else if (linkedList != fileLoader.audioLoadOperationQueue) {
                                    if (fileLoadOperation.start(fileStreamLoadOperation2, i7)) {
                                        fileLoader.currentAudioLoadOperationsCount++;
                                    }
                                } else if (linkedList == fileLoader.photoLoadOperationQueue) {
                                    if (fileLoadOperation.start(fileStreamLoadOperation2, i7)) {
                                        fileLoader.currentLoadOperationsCount++;
                                    }
                                    if (fileStreamLoadOperation2 != null) {
                                        pauseCurrentFileLoadOperations(fileLoadOperation);
                                    }
                                    fileLoader.activeFileLoadOperation.add(fileLoadOperation);
                                } else if (fileLoadOperation.start(fileStreamLoadOperation2, i7)) {
                                    fileLoader.currentPhotoLoadOperationsCount++;
                                }
                            } else {
                                if (fileStreamLoadOperation2 != null) {
                                    pauseCurrentFileLoadOperations(fileLoadOperation);
                                }
                                fileLoadOperation.start(fileStreamLoadOperation2, i7);
                                fileLoader.activeFileLoadOperation.add(fileLoadOperation);
                            }
                        }
                        return fileLoadOperation;
                    }
                }
                linkedList = fileLoader.audioLoadOperationQueue;
                if (linkedList != null) {
                    indexOf = linkedList.indexOf(fileLoadOperation);
                    if (indexOf <= 0) {
                        if (fileStreamLoadOperation2 != null) {
                            pauseCurrentFileLoadOperations(fileLoadOperation);
                        }
                        fileLoadOperation.start(fileStreamLoadOperation2, i7);
                        fileLoader.activeFileLoadOperation.add(fileLoadOperation);
                    } else {
                        linkedList.remove(indexOf);
                        if (i7 != 0) {
                            linkedList.add(0, fileLoadOperation);
                        } else if (linkedList != fileLoader.audioLoadOperationQueue) {
                            if (linkedList == fileLoader.photoLoadOperationQueue) {
                                if (fileLoadOperation.start(fileStreamLoadOperation2, i7)) {
                                    fileLoader.currentLoadOperationsCount++;
                                }
                                if (fileStreamLoadOperation2 != null) {
                                    pauseCurrentFileLoadOperations(fileLoadOperation);
                                }
                                fileLoader.activeFileLoadOperation.add(fileLoadOperation);
                            } else if (fileLoadOperation.start(fileStreamLoadOperation2, i7)) {
                                fileLoader.currentPhotoLoadOperationsCount++;
                            }
                        } else if (fileLoadOperation.start(fileStreamLoadOperation2, i7)) {
                            fileLoader.currentAudioLoadOperationsCount++;
                        }
                    }
                }
                return fileLoadOperation;
            }
        }
        return null;
    }

    private void loadFile(Document document, TL_webDocument tL_webDocument, FileLocation fileLocation, String str, int i, boolean z, int i2) {
        FileLoader fileLoader;
        Object attachFileName = fileLocation != null ? getAttachFileName(fileLocation, str) : document != null ? getAttachFileName(document) : tL_webDocument != null ? getAttachFileName(tL_webDocument) : null;
        if (TextUtils.isEmpty(attachFileName) || attachFileName.contains("-2147483648")) {
            fileLoader = this;
        } else {
            this.loadOperationPathsUI.put(attachFileName, Boolean.valueOf(true));
        }
        final Document document2 = document;
        final TL_webDocument tL_webDocument2 = tL_webDocument;
        final FileLocation fileLocation2 = fileLocation;
        final String str2 = str;
        final int i3 = i;
        final boolean z2 = z;
        final int i4 = i2;
        fileLoaderQueue.postRunnable(new Runnable() {
            public void run() {
                FileLoader.this.loadFileInternal(document2, tL_webDocument2, fileLocation2, str2, i3, z2, null, 0, i4);
            }
        });
    }

    protected FileLoadOperation loadStreamFile(FileStreamLoadOperation fileStreamLoadOperation, Document document, int i) {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        FileLoadOperation[] fileLoadOperationArr = new FileLoadOperation[1];
        final FileLoadOperation[] fileLoadOperationArr2 = fileLoadOperationArr;
        final Document document2 = document;
        final FileStreamLoadOperation fileStreamLoadOperation2 = fileStreamLoadOperation;
        final int i2 = i;
        final CountDownLatch countDownLatch2 = countDownLatch;
        fileLoaderQueue.postRunnable(new Runnable() {
            public void run() {
                fileLoadOperationArr2[0] = FileLoader.this.loadFileInternal(document2, null, null, null, 0, true, fileStreamLoadOperation2, i2, 0);
                countDownLatch2.countDown();
            }
        });
        try {
            countDownLatch.await();
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
        return fileLoadOperationArr[null];
    }

    private void checkDownloadQueue(Document document, TL_webDocument tL_webDocument, FileLocation fileLocation, String str) {
        final String str2 = str;
        final Document document2 = document;
        final TL_webDocument tL_webDocument2 = tL_webDocument;
        final FileLocation fileLocation2 = fileLocation;
        fileLoaderQueue.postRunnable(new Runnable() {
            public void run() {
                FileLoadOperation fileLoadOperation = (FileLoadOperation) FileLoader.this.loadOperationPaths.remove(str2);
                if (!MessageObject.isVoiceDocument(document2)) {
                    if (!MessageObject.isVoiceWebDocument(tL_webDocument2)) {
                        if (fileLocation2 == null) {
                            if (!MessageObject.isImageWebDocument(tL_webDocument2)) {
                                if (fileLoadOperation != null) {
                                    if (fileLoadOperation.wasStarted()) {
                                        FileLoader.this.currentLoadOperationsCount = FileLoader.this.currentLoadOperationsCount - 1;
                                    } else {
                                        FileLoader.this.loadOperationQueue.remove(fileLoadOperation);
                                    }
                                    FileLoader.this.activeFileLoadOperation.remove(fileLoadOperation);
                                }
                                while (!FileLoader.this.loadOperationQueue.isEmpty()) {
                                    if (FileLoader.this.currentLoadOperationsCount < (((FileLoadOperation) FileLoader.this.loadOperationQueue.get(0)).isForceRequest() ? 3 : 1)) {
                                        fileLoadOperation = (FileLoadOperation) FileLoader.this.loadOperationQueue.poll();
                                        if (fileLoadOperation != null && fileLoadOperation.start()) {
                                            FileLoader.this.currentLoadOperationsCount = FileLoader.this.currentLoadOperationsCount + 1;
                                            if (!FileLoader.this.activeFileLoadOperation.contains(fileLoadOperation)) {
                                                FileLoader.this.activeFileLoadOperation.add(fileLoadOperation);
                                            }
                                        }
                                    } else {
                                        return;
                                    }
                                }
                                return;
                            }
                        }
                        if (fileLoadOperation != null) {
                            if (fileLoadOperation.wasStarted()) {
                                FileLoader.this.currentPhotoLoadOperationsCount = FileLoader.this.currentPhotoLoadOperationsCount - 1;
                            } else {
                                FileLoader.this.photoLoadOperationQueue.remove(fileLoadOperation);
                            }
                        }
                        while (!FileLoader.this.photoLoadOperationQueue.isEmpty()) {
                            if (FileLoader.this.currentPhotoLoadOperationsCount < (((FileLoadOperation) FileLoader.this.photoLoadOperationQueue.get(0)).isForceRequest() ? 3 : 1)) {
                                fileLoadOperation = (FileLoadOperation) FileLoader.this.photoLoadOperationQueue.poll();
                                if (fileLoadOperation != null && fileLoadOperation.start()) {
                                    FileLoader.this.currentPhotoLoadOperationsCount = FileLoader.this.currentPhotoLoadOperationsCount + 1;
                                }
                            } else {
                                return;
                            }
                        }
                        return;
                    }
                }
                if (fileLoadOperation != null) {
                    if (fileLoadOperation.wasStarted()) {
                        FileLoader.this.currentAudioLoadOperationsCount = FileLoader.this.currentAudioLoadOperationsCount - 1;
                    } else {
                        FileLoader.this.audioLoadOperationQueue.remove(fileLoadOperation);
                    }
                }
                while (!FileLoader.this.audioLoadOperationQueue.isEmpty()) {
                    if (FileLoader.this.currentAudioLoadOperationsCount < (((FileLoadOperation) FileLoader.this.audioLoadOperationQueue.get(0)).isForceRequest() ? 3 : 1)) {
                        fileLoadOperation = (FileLoadOperation) FileLoader.this.audioLoadOperationQueue.poll();
                        if (fileLoadOperation != null && fileLoadOperation.start()) {
                            FileLoader.this.currentAudioLoadOperationsCount = FileLoader.this.currentAudioLoadOperationsCount + 1;
                        }
                    } else {
                        return;
                    }
                }
            }
        });
    }

    public void setDelegate(FileLoaderDelegate fileLoaderDelegate) {
        this.delegate = fileLoaderDelegate;
    }

    public static String getMessageFileName(Message message) {
        if (message == null) {
            return TtmlNode.ANONYMOUS_REGION_ID;
        }
        if (message instanceof TL_messageService) {
            if (message.action.photo != null) {
                message = message.action.photo.sizes;
                if (message.size() > 0) {
                    message = getClosestPhotoSizeWithSize(message, AndroidUtilities.getPhotoSize());
                    if (message != null) {
                        return getAttachFileName(message);
                    }
                }
            }
        } else if (message.media instanceof TL_messageMediaDocument) {
            return getAttachFileName(message.media.document);
        } else {
            if (message.media instanceof TL_messageMediaPhoto) {
                message = message.media.photo.sizes;
                if (message.size() > 0) {
                    message = getClosestPhotoSizeWithSize(message, AndroidUtilities.getPhotoSize());
                    if (message != null) {
                        return getAttachFileName(message);
                    }
                }
            } else if (message.media instanceof TL_messageMediaWebPage) {
                if (message.media.webpage.document != null) {
                    return getAttachFileName(message.media.webpage.document);
                }
                if (message.media.webpage.photo != null) {
                    message = message.media.webpage.photo.sizes;
                    if (message.size() > 0) {
                        message = getClosestPhotoSizeWithSize(message, AndroidUtilities.getPhotoSize());
                        if (message != null) {
                            return getAttachFileName(message);
                        }
                    }
                } else if (message.media instanceof TL_messageMediaInvoice) {
                    return getAttachFileName(((TL_messageMediaInvoice) message.media).photo);
                }
            } else if (message.media instanceof TL_messageMediaInvoice) {
                message = ((TL_messageMediaInvoice) message.media).photo;
                if (message != null) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(Utilities.MD5(message.url));
                    stringBuilder.append(".");
                    stringBuilder.append(ImageLoader.getHttpUrlExtension(message.url, getExtensionByMime(message.mime_type)));
                    return stringBuilder.toString();
                }
            }
        }
        return TtmlNode.ANONYMOUS_REGION_ID;
    }

    public static File getPathToMessage(Message message) {
        if (message == null) {
            return new File(TtmlNode.ANONYMOUS_REGION_ID);
        }
        if (!(message instanceof TL_messageService)) {
            boolean z = false;
            TLObject tLObject;
            if (message.media instanceof TL_messageMediaDocument) {
                tLObject = message.media.document;
                if (message.media.ttl_seconds != null) {
                    z = true;
                }
                return getPathToAttach(tLObject, z);
            } else if (message.media instanceof TL_messageMediaPhoto) {
                ArrayList arrayList = message.media.photo.sizes;
                if (arrayList.size() > 0) {
                    tLObject = getClosestPhotoSizeWithSize(arrayList, AndroidUtilities.getPhotoSize());
                    if (tLObject != null) {
                        if (message.media.ttl_seconds != null) {
                            z = true;
                        }
                        return getPathToAttach(tLObject, z);
                    }
                }
            } else if (message.media instanceof TL_messageMediaWebPage) {
                if (message.media.webpage.document != null) {
                    return getPathToAttach(message.media.webpage.document);
                }
                if (message.media.webpage.photo != null) {
                    message = message.media.webpage.photo.sizes;
                    if (message.size() > 0) {
                        message = getClosestPhotoSizeWithSize(message, AndroidUtilities.getPhotoSize());
                        if (message != null) {
                            return getPathToAttach(message);
                        }
                    }
                }
            } else if (message.media instanceof TL_messageMediaInvoice) {
                return getPathToAttach(((TL_messageMediaInvoice) message.media).photo, true);
            }
        } else if (message.action.photo != null) {
            message = message.action.photo.sizes;
            if (message.size() > 0) {
                message = getClosestPhotoSizeWithSize(message, AndroidUtilities.getPhotoSize());
                if (message != null) {
                    return getPathToAttach(message);
                }
            }
        }
        return new File(TtmlNode.ANONYMOUS_REGION_ID);
    }

    public static File getPathToAttach(TLObject tLObject) {
        return getPathToAttach(tLObject, null, false);
    }

    public static File getPathToAttach(TLObject tLObject, boolean z) {
        return getPathToAttach(tLObject, null, z);
    }

    public static File getPathToAttach(TLObject tLObject, String str, boolean z) {
        if (z) {
            z = getDirectory(4);
        } else if (tLObject instanceof Document) {
            Document document = (Document) tLObject;
            z = document.key != null ? getDirectory(4) : MessageObject.isVoiceDocument(document) ? getDirectory(1) : MessageObject.isVideoDocument(document) ? getDirectory(2) : getDirectory(3);
        } else if (tLObject instanceof PhotoSize) {
            PhotoSize photoSize = (PhotoSize) tLObject;
            if (photoSize.location != null && photoSize.location.key == null && (photoSize.location.volume_id != -2147483648L || photoSize.location.local_id >= 0)) {
                if (photoSize.size < false) {
                    z = getDirectory(0);
                }
            }
            z = getDirectory(4);
        } else if (tLObject instanceof FileLocation) {
            FileLocation fileLocation = (FileLocation) tLObject;
            if (fileLocation.key == null) {
                if (fileLocation.volume_id != -2147483648L || fileLocation.local_id < false) {
                    z = getDirectory(0);
                }
            }
            z = getDirectory(4);
        } else if (tLObject instanceof TL_webDocument) {
            TL_webDocument tL_webDocument = (TL_webDocument) tLObject;
            z = tL_webDocument.mime_type.startsWith("image/") ? getDirectory(0) : tL_webDocument.mime_type.startsWith("audio/") ? getDirectory(1) : tL_webDocument.mime_type.startsWith("video/") ? getDirectory(2) : getDirectory(3);
        } else {
            z = false;
        }
        if (z) {
            return new File(z, getAttachFileName(tLObject, str));
        }
        return new File(TtmlNode.ANONYMOUS_REGION_ID);
    }

    public static FileStreamLoadOperation getStreamLoadOperation(TransferListener<? super DataSource> transferListener) {
        return new FileStreamLoadOperation(transferListener);
    }

    public static PhotoSize getClosestPhotoSizeWithSize(ArrayList<PhotoSize> arrayList, int i) {
        return getClosestPhotoSizeWithSize(arrayList, i, false);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static PhotoSize getClosestPhotoSizeWithSize(ArrayList<PhotoSize> arrayList, int i, boolean z) {
        PhotoSize photoSize = null;
        if (arrayList != null) {
            if (!arrayList.isEmpty()) {
                int i2 = 0;
                int i3 = 0;
                while (i2 < arrayList.size()) {
                    PhotoSize photoSize2 = (PhotoSize) arrayList.get(i2);
                    if (photoSize2 != null) {
                        int i4;
                        if (z) {
                            i4 = photoSize2.f42h >= photoSize2.f43w ? photoSize2.f43w : photoSize2.f42h;
                            if (photoSize != null && ((i <= 100 || photoSize.location == null || photoSize.location.dc_id != Integer.MIN_VALUE) && !(photoSize2 instanceof TL_photoCachedSize))) {
                                if (i > i3 && i3 < i4) {
                                }
                            }
                        } else {
                            i4 = photoSize2.f43w >= photoSize2.f42h ? photoSize2.f43w : photoSize2.f42h;
                            if (photoSize != null) {
                                if (i > 100) {
                                    if (photoSize.location != null) {
                                    }
                                }
                                if (!(photoSize2 instanceof TL_photoCachedSize)) {
                                    if (i4 <= i) {
                                        if (i3 >= i4) {
                                        }
                                    }
                                }
                            }
                        }
                        photoSize = photoSize2;
                        i3 = i4;
                    }
                    i2++;
                }
                return photoSize;
            }
        }
        return null;
    }

    public static java.lang.String getFileExtension(java.io.File r1) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
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
        r1 = r1.getName();
        r0 = 46;
        r0 = r1.lastIndexOf(r0);	 Catch:{ Exception -> 0x0011 }
        r0 = r0 + 1;	 Catch:{ Exception -> 0x0011 }
        r1 = r1.substring(r0);	 Catch:{ Exception -> 0x0011 }
        return r1;
    L_0x0011:
        r1 = "";
        return r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.FileLoader.getFileExtension(java.io.File):java.lang.String");
    }

    public static String fixFileName(String str) {
        return str != null ? str.replaceAll("[\u0001-\u001f<>:\"/\\\\|?*\u007f]+", TtmlNode.ANONYMOUS_REGION_ID).trim() : str;
    }

    public static String getDocumentFileName(Document document) {
        String str = null;
        if (document != null) {
            if (document.file_name != null) {
                str = document.file_name;
            } else {
                for (int i = 0; i < document.attributes.size(); i++) {
                    DocumentAttribute documentAttribute = (DocumentAttribute) document.attributes.get(i);
                    if (documentAttribute instanceof TL_documentAttributeFilename) {
                        str = documentAttribute.file_name;
                    }
                }
            }
        }
        document = fixFileName(str);
        return document != null ? document : TtmlNode.ANONYMOUS_REGION_ID;
    }

    public static String getExtensionByMime(String str) {
        int lastIndexOf = str.lastIndexOf(47);
        return lastIndexOf != -1 ? str.substring(lastIndexOf + 1) : TtmlNode.ANONYMOUS_REGION_ID;
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
            documentFileName = TtmlNode.ANONYMOUS_REGION_ID;
        }
        return documentFileName.toUpperCase();
    }

    public static String getAttachFileName(TLObject tLObject) {
        return getAttachFileName(tLObject, null);
    }

    public static String getAttachFileName(TLObject tLObject, String str) {
        StringBuilder stringBuilder;
        if (tLObject instanceof Document) {
            int lastIndexOf;
            Document document = (Document) tLObject;
            str = getDocumentFileName(document);
            Object obj = -1;
            if (str != null) {
                lastIndexOf = str.lastIndexOf(46);
                if (lastIndexOf != -1) {
                    str = str.substring(lastIndexOf);
                    if (str.length() <= 1) {
                        if (document.mime_type != null) {
                            str = document.mime_type;
                            lastIndexOf = str.hashCode();
                            if (lastIndexOf == 187091926) {
                                if (lastIndexOf != NUM) {
                                    if (str.equals(MimeTypes.VIDEO_MP4) != null) {
                                        obj = null;
                                    }
                                }
                            } else if (str.equals("audio/ogg") != null) {
                                obj = 1;
                            }
                            switch (obj) {
                                case null:
                                    str = ".mp4";
                                    break;
                                case 1:
                                    str = ".ogg";
                                    break;
                                default:
                                    str = TtmlNode.ANONYMOUS_REGION_ID;
                                    break;
                            }
                        }
                        str = TtmlNode.ANONYMOUS_REGION_ID;
                    }
                    if (document.version != 0) {
                        if (str.length() <= 1) {
                            stringBuilder = new StringBuilder();
                            stringBuilder.append(document.dc_id);
                            stringBuilder.append("_");
                            stringBuilder.append(document.id);
                            stringBuilder.append(str);
                            return stringBuilder.toString();
                        }
                        str = new StringBuilder();
                        str.append(document.dc_id);
                        str.append("_");
                        str.append(document.id);
                        return str.toString();
                    } else if (str.length() <= 1) {
                        stringBuilder = new StringBuilder();
                        stringBuilder.append(document.dc_id);
                        stringBuilder.append("_");
                        stringBuilder.append(document.id);
                        stringBuilder.append("_");
                        stringBuilder.append(document.version);
                        stringBuilder.append(str);
                        return stringBuilder.toString();
                    } else {
                        str = new StringBuilder();
                        str.append(document.dc_id);
                        str.append("_");
                        str.append(document.id);
                        str.append("_");
                        str.append(document.version);
                        return str.toString();
                    }
                }
            }
            str = TtmlNode.ANONYMOUS_REGION_ID;
            if (str.length() <= 1) {
                if (document.mime_type != null) {
                    str = document.mime_type;
                    lastIndexOf = str.hashCode();
                    if (lastIndexOf == 187091926) {
                        if (str.equals("audio/ogg") != null) {
                            obj = 1;
                        }
                    } else if (lastIndexOf != NUM) {
                        if (str.equals(MimeTypes.VIDEO_MP4) != null) {
                            obj = null;
                        }
                    }
                    switch (obj) {
                        case null:
                            str = ".mp4";
                            break;
                        case 1:
                            str = ".ogg";
                            break;
                        default:
                            str = TtmlNode.ANONYMOUS_REGION_ID;
                            break;
                    }
                }
                str = TtmlNode.ANONYMOUS_REGION_ID;
            }
            if (document.version != 0) {
                if (str.length() <= 1) {
                    str = new StringBuilder();
                    str.append(document.dc_id);
                    str.append("_");
                    str.append(document.id);
                    str.append("_");
                    str.append(document.version);
                    return str.toString();
                }
                stringBuilder = new StringBuilder();
                stringBuilder.append(document.dc_id);
                stringBuilder.append("_");
                stringBuilder.append(document.id);
                stringBuilder.append("_");
                stringBuilder.append(document.version);
                stringBuilder.append(str);
                return stringBuilder.toString();
            } else if (str.length() <= 1) {
                str = new StringBuilder();
                str.append(document.dc_id);
                str.append("_");
                str.append(document.id);
                return str.toString();
            } else {
                stringBuilder = new StringBuilder();
                stringBuilder.append(document.dc_id);
                stringBuilder.append("_");
                stringBuilder.append(document.id);
                stringBuilder.append(str);
                return stringBuilder.toString();
            }
        } else if (tLObject instanceof TL_webDocument) {
            TL_webDocument tL_webDocument = (TL_webDocument) tLObject;
            str = new StringBuilder();
            str.append(Utilities.MD5(tL_webDocument.url));
            str.append(".");
            str.append(ImageLoader.getHttpUrlExtension(tL_webDocument.url, getExtensionByMime(tL_webDocument.mime_type)));
            return str.toString();
        } else if (tLObject instanceof PhotoSize) {
            PhotoSize photoSize = (PhotoSize) tLObject;
            if (photoSize.location != null) {
                if (!(photoSize.location instanceof TL_fileLocationUnavailable)) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(photoSize.location.volume_id);
                    stringBuilder.append("_");
                    stringBuilder.append(photoSize.location.local_id);
                    stringBuilder.append(".");
                    if (str == null) {
                        str = "jpg";
                    }
                    stringBuilder.append(str);
                    return stringBuilder.toString();
                }
            }
            return TtmlNode.ANONYMOUS_REGION_ID;
        } else if (!(tLObject instanceof FileLocation)) {
            return TtmlNode.ANONYMOUS_REGION_ID;
        } else {
            if (tLObject instanceof TL_fileLocationUnavailable) {
                return TtmlNode.ANONYMOUS_REGION_ID;
            }
            FileLocation fileLocation = (FileLocation) tLObject;
            stringBuilder = new StringBuilder();
            stringBuilder.append(fileLocation.volume_id);
            stringBuilder.append("_");
            stringBuilder.append(fileLocation.local_id);
            stringBuilder.append(".");
            if (str == null) {
                str = "jpg";
            }
            stringBuilder.append(str);
            return stringBuilder.toString();
        }
    }

    public void deleteFiles(final ArrayList<File> arrayList, final int i) {
        if (arrayList != null) {
            if (!arrayList.isEmpty()) {
                fileLoaderQueue.postRunnable(new Runnable() {
                    public void run() {
                        for (int i = 0; i < arrayList.size(); i++) {
                            File file = (File) arrayList.get(i);
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append(file.getAbsolutePath());
                            stringBuilder.append(".enc");
                            File file2 = new File(stringBuilder.toString());
                            if (file2.exists()) {
                                try {
                                    if (!file2.delete()) {
                                        file2.deleteOnExit();
                                    }
                                } catch (Throwable e) {
                                    FileLog.m3e(e);
                                }
                                try {
                                    File internalCacheDir = FileLoader.getInternalCacheDir();
                                    StringBuilder stringBuilder2 = new StringBuilder();
                                    stringBuilder2.append(file.getName());
                                    stringBuilder2.append(".enc.key");
                                    file2 = new File(internalCacheDir, stringBuilder2.toString());
                                    if (!file2.delete()) {
                                        file2.deleteOnExit();
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
                                internalCacheDir = file.getParentFile();
                                stringBuilder2 = new StringBuilder();
                                stringBuilder2.append("q_");
                                stringBuilder2.append(file.getName());
                                file2 = new File(internalCacheDir, stringBuilder2.toString());
                                if (file2.exists() && !file2.delete()) {
                                    file2.deleteOnExit();
                                }
                            } catch (Throwable e3) {
                                FileLog.m3e(e3);
                            }
                        }
                        if (i == 2) {
                            ImageLoader.getInstance().clearMemory();
                        }
                    }
                });
            }
        }
    }
}
