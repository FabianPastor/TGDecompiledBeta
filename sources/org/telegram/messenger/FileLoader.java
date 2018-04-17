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
                class C18011 implements FileUploadOperationDelegate {

                    /* renamed from: org.telegram.messenger.FileLoader$3$1$2 */
                    class C01622 implements Runnable {
                        C01622() {
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

                    C18011() {
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
                        FileLoader.fileLoaderQueue.postRunnable(new C01622());
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
                    FileUploadOperation fileUploadOperation = new FileUploadOperation(FileLoader.this.currentAccount, str, z, esimated, i2);
                    if (z) {
                        FileLoader.this.uploadOperationPathsEnc.put(str, fileUploadOperation);
                    } else {
                        FileLoader.this.uploadOperationPaths.put(str, fileUploadOperation);
                    }
                    fileUploadOperation.setDelegate(new C18011());
                    if (z2) {
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
            final String str;
            final Document document2;
            final TL_webDocument tL_webDocument;
            final FileLocation fileLocation;
            if (location != null) {
                fileName = getAttachFileName(location, locationExt);
            } else if (document != null) {
                fileName = getAttachFileName(document);
            } else if (webDocument != null) {
                fileName = getAttachFileName(webDocument);
            } else {
                fileName = null;
                if (fileName == null) {
                    this.loadOperationPathsUI.remove(fileName);
                    str = fileName;
                    document2 = document;
                    tL_webDocument = webDocument;
                    fileLocation = location;
                    fileLoaderQueue.postRunnable(new Runnable() {
                        public void run() {
                            FileLoadOperation operation = (FileLoadOperation) FileLoader.this.loadOperationPaths.remove(str);
                            if (operation != null) {
                                if (!MessageObject.isVoiceDocument(document2)) {
                                    if (!MessageObject.isVoiceWebDocument(tL_webDocument)) {
                                        if (fileLocation == null) {
                                            if (!MessageObject.isImageWebDocument(tL_webDocument)) {
                                                if (!FileLoader.this.loadOperationQueue.remove(operation)) {
                                                    FileLoader.this.currentLoadOperationsCount = FileLoader.this.currentLoadOperationsCount - 1;
                                                }
                                                FileLoader.this.activeFileLoadOperation.remove(operation);
                                                operation.cancel();
                                            }
                                        }
                                        if (!FileLoader.this.photoLoadOperationQueue.remove(operation)) {
                                            FileLoader.this.currentPhotoLoadOperationsCount = FileLoader.this.currentPhotoLoadOperationsCount - 1;
                                        }
                                        operation.cancel();
                                    }
                                }
                                if (!FileLoader.this.audioLoadOperationQueue.remove(operation)) {
                                    FileLoader.this.currentAudioLoadOperationsCount = FileLoader.this.currentAudioLoadOperationsCount - 1;
                                }
                                operation.cancel();
                            }
                        }
                    });
                }
            }
            if (fileName == null) {
                this.loadOperationPathsUI.remove(fileName);
                str = fileName;
                document2 = document;
                tL_webDocument = webDocument;
                fileLocation = location;
                fileLoaderQueue.postRunnable(/* anonymous class already generated */);
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
                this.loadOperationQueue.add(0, operation);
                if (operation.wasStarted()) {
                    this.currentLoadOperationsCount--;
                }
            }
            a++;
        }
    }

    private FileLoadOperation loadFileInternal(Document document, TL_webDocument webDocument, FileLocation location, String locationExt, int locationSize, boolean force, FileStreamLoadOperation stream, int streamOffset, int cacheType) {
        FileStreamLoadOperation fileStreamLoadOperation;
        FileLoader fileLoader = this;
        Document document2 = document;
        TL_webDocument tL_webDocument = webDocument;
        FileLocation fileLocation = location;
        FileStreamLoadOperation operation = stream;
        int i = streamOffset;
        int i2 = cacheType;
        String fileName = null;
        if (fileLocation != null) {
            fileName = getAttachFileName(location, locationExt);
        } else if (document2 != null) {
            fileName = getAttachFileName(document);
        } else if (tL_webDocument != null) {
            fileName = getAttachFileName(webDocument);
        }
        String fileName2 = fileName;
        if (fileName2 != null) {
            if (!fileName2.contains("-2147483648")) {
                if (!(TextUtils.isEmpty(fileName2) || fileName2.contains("-2147483648"))) {
                    fileLoader.loadOperationPathsUI.put(fileName2, Boolean.valueOf(true));
                }
                int type = (FileLoadOperation) fileLoader.loadOperationPaths.get(fileName2);
                int index;
                if (type != null) {
                    if (i != 0 || force) {
                        LinkedList<FileLoadOperation> downloadQueue;
                        type.setForceRequest(true);
                        if (!MessageObject.isVoiceDocument(document)) {
                            if (!MessageObject.isVoiceWebDocument(webDocument)) {
                                if (fileLocation == null) {
                                    if (!MessageObject.isImageWebDocument(webDocument)) {
                                        downloadQueue = fileLoader.loadOperationQueue;
                                        if (downloadQueue != null) {
                                            index = downloadQueue.indexOf(type);
                                            if (index > 0) {
                                                downloadQueue.remove(index);
                                                if (i != 0) {
                                                    downloadQueue.add(0, type);
                                                } else if (downloadQueue == fileLoader.audioLoadOperationQueue) {
                                                    if (type.start(operation, i)) {
                                                        fileLoader.currentAudioLoadOperationsCount++;
                                                    }
                                                } else if (downloadQueue == fileLoader.photoLoadOperationQueue) {
                                                    if (type.start(operation, i)) {
                                                        fileLoader.currentLoadOperationsCount++;
                                                    }
                                                    if (type.wasStarted() && !fileLoader.activeFileLoadOperation.contains(type)) {
                                                        if (operation != null) {
                                                            pauseCurrentFileLoadOperations(type);
                                                        }
                                                        fileLoader.activeFileLoadOperation.add(type);
                                                    }
                                                } else if (type.start(operation, i)) {
                                                    fileLoader.currentPhotoLoadOperationsCount++;
                                                }
                                            } else {
                                                if (operation != null) {
                                                    pauseCurrentFileLoadOperations(type);
                                                }
                                                type.start(operation, i);
                                                if (downloadQueue == fileLoader.loadOperationQueue && !fileLoader.activeFileLoadOperation.contains(type)) {
                                                    fileLoader.activeFileLoadOperation.add(type);
                                                }
                                            }
                                        }
                                    }
                                }
                                downloadQueue = fileLoader.photoLoadOperationQueue;
                                if (downloadQueue != null) {
                                    index = downloadQueue.indexOf(type);
                                    if (index > 0) {
                                        if (operation != null) {
                                            pauseCurrentFileLoadOperations(type);
                                        }
                                        type.start(operation, i);
                                        fileLoader.activeFileLoadOperation.add(type);
                                    } else {
                                        downloadQueue.remove(index);
                                        if (i != 0) {
                                            downloadQueue.add(0, type);
                                        } else if (downloadQueue == fileLoader.audioLoadOperationQueue) {
                                            if (downloadQueue == fileLoader.photoLoadOperationQueue) {
                                                if (type.start(operation, i)) {
                                                    fileLoader.currentLoadOperationsCount++;
                                                }
                                                if (operation != null) {
                                                    pauseCurrentFileLoadOperations(type);
                                                }
                                                fileLoader.activeFileLoadOperation.add(type);
                                            } else if (type.start(operation, i)) {
                                                fileLoader.currentPhotoLoadOperationsCount++;
                                            }
                                        } else if (type.start(operation, i)) {
                                            fileLoader.currentAudioLoadOperationsCount++;
                                        }
                                    }
                                }
                            }
                        }
                        downloadQueue = fileLoader.audioLoadOperationQueue;
                        if (downloadQueue != null) {
                            index = downloadQueue.indexOf(type);
                            if (index > 0) {
                                downloadQueue.remove(index);
                                if (i != 0) {
                                    downloadQueue.add(0, type);
                                } else if (downloadQueue == fileLoader.audioLoadOperationQueue) {
                                    if (type.start(operation, i)) {
                                        fileLoader.currentAudioLoadOperationsCount++;
                                    }
                                } else if (downloadQueue == fileLoader.photoLoadOperationQueue) {
                                    if (type.start(operation, i)) {
                                        fileLoader.currentLoadOperationsCount++;
                                    }
                                    if (operation != null) {
                                        pauseCurrentFileLoadOperations(type);
                                    }
                                    fileLoader.activeFileLoadOperation.add(type);
                                } else if (type.start(operation, i)) {
                                    fileLoader.currentPhotoLoadOperationsCount++;
                                }
                            } else {
                                if (operation != null) {
                                    pauseCurrentFileLoadOperations(type);
                                }
                                type.start(operation, i);
                                fileLoader.activeFileLoadOperation.add(type);
                            }
                        }
                    }
                    return type;
                }
                FileLoadOperation operation2;
                final String finalFileName;
                FileLoadOperation operation3;
                final int finalType;
                final Document document3;
                int i3;
                final TL_webDocument tL_webDocument2;
                final FileLocation fileLocation2;
                int maxCount;
                File tempDir = getDirectory(4);
                File storeDir = tempDir;
                index = 4;
                if (fileLocation != null) {
                    operation2 = new FileLoadOperation(fileLocation, locationExt, locationSize);
                    index = 0;
                } else {
                    String str = locationExt;
                    int i4 = locationSize;
                    if (document2 != null) {
                        type = new FileLoadOperation(document2);
                        if (MessageObject.isVoiceDocument(document)) {
                            index = 1;
                        } else if (MessageObject.isVideoDocument(document)) {
                            index = 2;
                        } else {
                            index = 3;
                        }
                    } else if (tL_webDocument != null) {
                        type = new FileLoadOperation(tL_webDocument);
                        if (MessageObject.isVoiceWebDocument(webDocument)) {
                            index = 1;
                        } else if (MessageObject.isVideoWebDocument(webDocument)) {
                            index = 2;
                        } else if (MessageObject.isImageWebDocument(webDocument)) {
                            index = 0;
                        } else {
                            index = 3;
                        }
                    }
                    operation2 = type;
                }
                type = index;
                if (i2 == 0) {
                    storeDir = getDirectory(type);
                } else if (i2 == 2) {
                    operation2.setEncryptFile(true);
                    operation2.setPaths(fileLoader.currentAccount, storeDir, tempDir);
                    finalFileName = fileName2;
                    operation3 = operation2;
                    finalType = type;
                    i2 = type;
                    document3 = document2;
                    i3 = 0;
                    tL_webDocument2 = tL_webDocument;
                    i3 = 1;
                    fileLocation2 = fileLocation;
                    operation3.setDelegate(new FileLoadOperationDelegate() {
                        public void didFinishLoadingFile(FileLoadOperation operation, File finalFile) {
                            FileLoader.this.loadOperationPathsUI.remove(finalFileName);
                            if (FileLoader.this.delegate != null) {
                                FileLoader.this.delegate.fileDidLoaded(finalFileName, finalFile, finalType);
                            }
                            FileLoader.this.checkDownloadQueue(document3, tL_webDocument2, fileLocation2, finalFileName);
                        }

                        public void didFailedLoadingFile(FileLoadOperation operation, int reason) {
                            FileLoader.this.loadOperationPathsUI.remove(finalFileName);
                            FileLoader.this.checkDownloadQueue(document3, tL_webDocument2, fileLocation2, finalFileName);
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
                    fileLoader.loadOperationPaths.put(fileName2, operation3);
                    maxCount = force ? 3 : i3;
                    if (i2 != i3) {
                        if (i == 0) {
                            if (fileLoader.currentAudioLoadOperationsCount < maxCount) {
                                if (force) {
                                    fileLoader.audioLoadOperationQueue.add(operation3);
                                } else {
                                    fileLoader.audioLoadOperationQueue.add(0, operation3);
                                }
                                fileStreamLoadOperation = stream;
                            }
                        }
                        if (operation3.start(stream, i)) {
                            fileLoader.currentAudioLoadOperationsCount += i3;
                        }
                    } else {
                        fileStreamLoadOperation = stream;
                        if (fileLocation == null) {
                            if (MessageObject.isImageWebDocument(webDocument)) {
                                if (i == 0) {
                                    if (fileLoader.currentLoadOperationsCount < maxCount) {
                                        if (force) {
                                            fileLoader.loadOperationQueue.add(operation3);
                                        } else {
                                            fileLoader.loadOperationQueue.add(0, operation3);
                                        }
                                    }
                                }
                                if (operation3.start(fileStreamLoadOperation, i)) {
                                    fileLoader.currentLoadOperationsCount += i3;
                                    fileLoader.activeFileLoadOperation.add(operation3);
                                }
                                if (operation3.wasStarted() && fileStreamLoadOperation != null) {
                                    pauseCurrentFileLoadOperations(operation3);
                                }
                            }
                        }
                        if (i == 0) {
                            if (fileLoader.currentPhotoLoadOperationsCount < maxCount) {
                                if (force) {
                                    fileLoader.photoLoadOperationQueue.add(operation3);
                                } else {
                                    fileLoader.photoLoadOperationQueue.add(0, operation3);
                                }
                            }
                        }
                        if (operation3.start(fileStreamLoadOperation, i)) {
                            fileLoader.currentPhotoLoadOperationsCount += i3;
                        }
                    }
                    return operation3;
                }
                operation2.setPaths(fileLoader.currentAccount, storeDir, tempDir);
                finalFileName = fileName2;
                operation3 = operation2;
                finalType = type;
                i2 = type;
                document3 = document2;
                i3 = 0;
                tL_webDocument2 = tL_webDocument;
                i3 = 1;
                fileLocation2 = fileLocation;
                operation3.setDelegate(/* anonymous class already generated */);
                fileLoader.loadOperationPaths.put(fileName2, operation3);
                if (force) {
                }
                maxCount = force ? 3 : i3;
                if (i2 != i3) {
                    fileStreamLoadOperation = stream;
                    if (fileLocation == null) {
                        if (MessageObject.isImageWebDocument(webDocument)) {
                            if (i == 0) {
                                if (fileLoader.currentLoadOperationsCount < maxCount) {
                                    if (force) {
                                        fileLoader.loadOperationQueue.add(operation3);
                                    } else {
                                        fileLoader.loadOperationQueue.add(0, operation3);
                                    }
                                }
                            }
                            if (operation3.start(fileStreamLoadOperation, i)) {
                                fileLoader.currentLoadOperationsCount += i3;
                                fileLoader.activeFileLoadOperation.add(operation3);
                            }
                            pauseCurrentFileLoadOperations(operation3);
                        }
                    }
                    if (i == 0) {
                        if (fileLoader.currentPhotoLoadOperationsCount < maxCount) {
                            if (force) {
                                fileLoader.photoLoadOperationQueue.add(operation3);
                            } else {
                                fileLoader.photoLoadOperationQueue.add(0, operation3);
                            }
                        }
                    }
                    if (operation3.start(fileStreamLoadOperation, i)) {
                        fileLoader.currentPhotoLoadOperationsCount += i3;
                    }
                } else {
                    if (i == 0) {
                        if (fileLoader.currentAudioLoadOperationsCount < maxCount) {
                            if (force) {
                                fileLoader.audioLoadOperationQueue.add(operation3);
                            } else {
                                fileLoader.audioLoadOperationQueue.add(0, operation3);
                            }
                            fileStreamLoadOperation = stream;
                        }
                    }
                    if (operation3.start(stream, i)) {
                        fileLoader.currentAudioLoadOperationsCount += i3;
                    }
                }
                return operation3;
            }
        }
        fileStreamLoadOperation = operation;
        return null;
    }

    private void loadFile(Document document, TL_webDocument webDocument, FileLocation location, String locationExt, int locationSize, boolean force, int cacheType) {
        String fileName;
        String fileName2;
        FileLoader fileLoader;
        final Document document2;
        final TL_webDocument tL_webDocument;
        final FileLocation fileLocation;
        final String str;
        final int i;
        final boolean z;
        final int i2;
        if (location != null) {
            fileName = getAttachFileName(location, locationExt);
        } else if (document != null) {
            fileName = getAttachFileName(document);
        } else if (webDocument != null) {
            fileName = getAttachFileName(webDocument);
        } else {
            fileName = null;
            fileName2 = fileName;
            if (!TextUtils.isEmpty(fileName2) || fileName2.contains("-2147483648")) {
                fileLoader = this;
            } else {
                this.loadOperationPathsUI.put(fileName2, Boolean.valueOf(true));
            }
            document2 = document;
            tL_webDocument = webDocument;
            fileLocation = location;
            str = locationExt;
            i = locationSize;
            z = force;
            i2 = cacheType;
            fileLoaderQueue.postRunnable(new Runnable() {
                public void run() {
                    FileLoader.this.loadFileInternal(document2, tL_webDocument, fileLocation, str, i, z, null, 0, i2);
                }
            });
        }
        fileName2 = fileName;
        if (TextUtils.isEmpty(fileName2)) {
        }
        fileLoader = this;
        document2 = document;
        tL_webDocument = webDocument;
        fileLocation = location;
        str = locationExt;
        i = locationSize;
        z = force;
        i2 = cacheType;
        fileLoaderQueue.postRunnable(/* anonymous class already generated */);
    }

    protected FileLoadOperation loadStreamFile(FileStreamLoadOperation stream, Document document, int offset) {
        CountDownLatch semaphore = new CountDownLatch(1);
        FileLoadOperation[] result = new FileLoadOperation[1];
        final FileLoadOperation[] fileLoadOperationArr = result;
        final Document document2 = document;
        final FileStreamLoadOperation fileStreamLoadOperation = stream;
        final int i = offset;
        final CountDownLatch countDownLatch = semaphore;
        fileLoaderQueue.postRunnable(new Runnable() {
            public void run() {
                fileLoadOperationArr[0] = FileLoader.this.loadFileInternal(document2, null, null, null, 0, true, fileStreamLoadOperation, i, 0);
                countDownLatch.countDown();
            }
        });
        try {
            semaphore.await();
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
        return result[0];
    }

    private void checkDownloadQueue(Document document, TL_webDocument webDocument, FileLocation location, String arg1) {
        final String str = arg1;
        final Document document2 = document;
        final TL_webDocument tL_webDocument = webDocument;
        final FileLocation fileLocation = location;
        fileLoaderQueue.postRunnable(new Runnable() {
            public void run() {
                FileLoadOperation operation = (FileLoadOperation) FileLoader.this.loadOperationPaths.remove(str);
                if (!MessageObject.isVoiceDocument(document2)) {
                    if (!MessageObject.isVoiceWebDocument(tL_webDocument)) {
                        if (fileLocation == null) {
                            if (!MessageObject.isImageWebDocument(tL_webDocument)) {
                                if (operation != null) {
                                    if (operation.wasStarted()) {
                                        FileLoader.this.currentLoadOperationsCount = FileLoader.this.currentLoadOperationsCount - 1;
                                    } else {
                                        FileLoader.this.loadOperationQueue.remove(operation);
                                    }
                                    FileLoader.this.activeFileLoadOperation.remove(operation);
                                }
                                while (!FileLoader.this.loadOperationQueue.isEmpty()) {
                                    if (FileLoader.this.currentLoadOperationsCount < (((FileLoadOperation) FileLoader.this.loadOperationQueue.get(0)).isForceRequest() ? 3 : 1)) {
                                        operation = (FileLoadOperation) FileLoader.this.loadOperationQueue.poll();
                                        if (operation != null && operation.start()) {
                                            FileLoader.this.currentLoadOperationsCount = FileLoader.this.currentLoadOperationsCount + 1;
                                            if (!FileLoader.this.activeFileLoadOperation.contains(operation)) {
                                                FileLoader.this.activeFileLoadOperation.add(operation);
                                            }
                                        }
                                    } else {
                                        return;
                                    }
                                }
                                return;
                            }
                        }
                        if (operation != null) {
                            if (operation.wasStarted()) {
                                FileLoader.this.currentPhotoLoadOperationsCount = FileLoader.this.currentPhotoLoadOperationsCount - 1;
                            } else {
                                FileLoader.this.photoLoadOperationQueue.remove(operation);
                            }
                        }
                        while (!FileLoader.this.photoLoadOperationQueue.isEmpty()) {
                            if (FileLoader.this.currentPhotoLoadOperationsCount < (((FileLoadOperation) FileLoader.this.photoLoadOperationQueue.get(0)).isForceRequest() ? 3 : 1)) {
                                operation = (FileLoadOperation) FileLoader.this.photoLoadOperationQueue.poll();
                                if (operation != null && operation.start()) {
                                    FileLoader.this.currentPhotoLoadOperationsCount = FileLoader.this.currentPhotoLoadOperationsCount + 1;
                                }
                            } else {
                                return;
                            }
                        }
                        return;
                    }
                }
                if (operation != null) {
                    if (operation.wasStarted()) {
                        FileLoader.this.currentAudioLoadOperationsCount = FileLoader.this.currentAudioLoadOperationsCount - 1;
                    } else {
                        FileLoader.this.audioLoadOperationQueue.remove(operation);
                    }
                }
                while (!FileLoader.this.audioLoadOperationQueue.isEmpty()) {
                    if (FileLoader.this.currentAudioLoadOperationsCount < (((FileLoadOperation) FileLoader.this.audioLoadOperationQueue.get(0)).isForceRequest() ? 3 : 1)) {
                        operation = (FileLoadOperation) FileLoader.this.audioLoadOperationQueue.poll();
                        if (operation != null && operation.start()) {
                            FileLoader.this.currentAudioLoadOperationsCount = FileLoader.this.currentAudioLoadOperationsCount + 1;
                        }
                    } else {
                        return;
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
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(Utilities.MD5(document.url));
                    stringBuilder.append(".");
                    stringBuilder.append(ImageLoader.getHttpUrlExtension(document.url, getExtensionByMime(document.mime_type)));
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
        ArrayList<PhotoSize> sizes;
        PhotoSize sizeFull;
        if (!(message instanceof TL_messageService)) {
            boolean z = false;
            if (message.media instanceof TL_messageMediaDocument) {
                TLObject tLObject = message.media.document;
                if (message.media.ttl_seconds != 0) {
                    z = true;
                }
                return getPathToAttach(tLObject, z);
            } else if (message.media instanceof TL_messageMediaPhoto) {
                sizes = message.media.photo.sizes;
                if (sizes.size() > 0) {
                    PhotoSize sizeFull2 = getClosestPhotoSizeWithSize(sizes, AndroidUtilities.getPhotoSize());
                    if (sizeFull2 != null) {
                        if (message.media.ttl_seconds != 0) {
                            z = true;
                        }
                        return getPathToAttach(sizeFull2, z);
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
        } else if (message.action.photo != null) {
            sizes = message.action.photo.sizes;
            if (sizes.size() > 0) {
                sizeFull = getClosestPhotoSizeWithSize(sizes, AndroidUtilities.getPhotoSize());
                if (sizeFull != null) {
                    return getPathToAttach(sizeFull);
                }
            }
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
            if (photoSize.location != null && photoSize.location.key == null && (photoSize.location.volume_id != -2147483648L || photoSize.location.local_id >= 0)) {
                if (photoSize.size >= 0) {
                    dir = getDirectory(0);
                }
            }
            dir = getDirectory(4);
        } else if (attach instanceof FileLocation) {
            FileLocation fileLocation = (FileLocation) attach;
            if (fileLocation.key == null) {
                if (fileLocation.volume_id != -2147483648L || fileLocation.local_id >= 0) {
                    dir = getDirectory(0);
                }
            }
            dir = getDirectory(4);
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
        if (sizes != null) {
            if (!sizes.isEmpty()) {
                int lastSide = 0;
                PhotoSize closestObject = null;
                for (int a = 0; a < sizes.size(); a++) {
                    PhotoSize obj = (PhotoSize) sizes.get(a);
                    if (obj != null) {
                        int currentSide;
                        if (byMinSide) {
                            currentSide = obj.f42h >= obj.f43w ? obj.f43w : obj.f42h;
                            if (closestObject == null || ((side > 100 && closestObject.location != null && closestObject.location.dc_id == Integer.MIN_VALUE) || (obj instanceof TL_photoCachedSize) || (side > lastSide && lastSide < currentSide))) {
                                closestObject = obj;
                                lastSide = currentSide;
                            }
                        } else {
                            currentSide = obj.f43w >= obj.f42h ? obj.f43w : obj.f42h;
                            if (closestObject == null || ((side > 100 && closestObject.location != null && closestObject.location.dc_id == Integer.MIN_VALUE) || (obj instanceof TL_photoCachedSize) || (currentSide <= side && lastSide < currentSide))) {
                                closestObject = obj;
                                lastSide = currentSide;
                            }
                        }
                    }
                }
                return closestObject;
            }
        }
        return null;
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
        int lastIndexOf = mime.lastIndexOf(47);
        int index = lastIndexOf;
        if (lastIndexOf != -1) {
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
        if (attach instanceof Document) {
            Document document = (Document) attach;
            String docExt = null;
            Object obj = -1;
            if (null == null) {
                docExt = getDocumentFileName(document);
                if (docExt != null) {
                    int lastIndexOf = docExt.lastIndexOf(46);
                    int idx = lastIndexOf;
                    if (lastIndexOf != -1) {
                        docExt = docExt.substring(idx);
                    }
                }
                docExt = TtmlNode.ANONYMOUS_REGION_ID;
            }
            if (docExt.length() <= 1) {
                if (document.mime_type != null) {
                    String str = document.mime_type;
                    int hashCode = str.hashCode();
                    if (hashCode != 187091926) {
                        if (hashCode == NUM) {
                            if (str.equals(MimeTypes.VIDEO_MP4)) {
                                obj = null;
                            }
                        }
                    } else if (str.equals("audio/ogg")) {
                        obj = 1;
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
            StringBuilder stringBuilder;
            if (document.version == 0) {
                if (docExt.length() > 1) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(document.dc_id);
                    stringBuilder.append("_");
                    stringBuilder.append(document.id);
                    stringBuilder.append(docExt);
                    return stringBuilder.toString();
                }
                stringBuilder = new StringBuilder();
                stringBuilder.append(document.dc_id);
                stringBuilder.append("_");
                stringBuilder.append(document.id);
                return stringBuilder.toString();
            } else if (docExt.length() > 1) {
                stringBuilder = new StringBuilder();
                stringBuilder.append(document.dc_id);
                stringBuilder.append("_");
                stringBuilder.append(document.id);
                stringBuilder.append("_");
                stringBuilder.append(document.version);
                stringBuilder.append(docExt);
                return stringBuilder.toString();
            } else {
                stringBuilder = new StringBuilder();
                stringBuilder.append(document.dc_id);
                stringBuilder.append("_");
                stringBuilder.append(document.id);
                stringBuilder.append("_");
                stringBuilder.append(document.version);
                return stringBuilder.toString();
            }
        } else if (attach instanceof TL_webDocument) {
            TL_webDocument document2 = (TL_webDocument) attach;
            r1 = new StringBuilder();
            r1.append(Utilities.MD5(document2.url));
            r1.append(".");
            r1.append(ImageLoader.getHttpUrlExtension(document2.url, getExtensionByMime(document2.mime_type)));
            return r1.toString();
        } else if (attach instanceof PhotoSize) {
            PhotoSize photo = (PhotoSize) attach;
            if (photo.location != null) {
                if (!(photo.location instanceof TL_fileLocationUnavailable)) {
                    r1 = new StringBuilder();
                    r1.append(photo.location.volume_id);
                    r1.append("_");
                    r1.append(photo.location.local_id);
                    r1.append(".");
                    r1.append(ext != null ? ext : "jpg");
                    return r1.toString();
                }
            }
            return TtmlNode.ANONYMOUS_REGION_ID;
        } else if (!(attach instanceof FileLocation)) {
            return TtmlNode.ANONYMOUS_REGION_ID;
        } else {
            if (attach instanceof TL_fileLocationUnavailable) {
                return TtmlNode.ANONYMOUS_REGION_ID;
            }
            FileLocation location = (FileLocation) attach;
            r1 = new StringBuilder();
            r1.append(location.volume_id);
            r1.append("_");
            r1.append(location.local_id);
            r1.append(".");
            r1.append(ext != null ? ext : "jpg");
            return r1.toString();
        }
    }

    public void deleteFiles(final ArrayList<File> files, final int type) {
        if (files != null) {
            if (!files.isEmpty()) {
                fileLoaderQueue.postRunnable(new Runnable() {
                    public void run() {
                        for (int a = 0; a < files.size(); a++) {
                            File key;
                            File file = (File) files.get(a);
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append(file.getAbsolutePath());
                            stringBuilder.append(".enc");
                            File encrypted = new File(stringBuilder.toString());
                            if (encrypted.exists()) {
                                try {
                                    if (!encrypted.delete()) {
                                        encrypted.deleteOnExit();
                                    }
                                } catch (Throwable e) {
                                    FileLog.m3e(e);
                                }
                                try {
                                    File internalCacheDir = FileLoader.getInternalCacheDir();
                                    StringBuilder stringBuilder2 = new StringBuilder();
                                    stringBuilder2.append(file.getName());
                                    stringBuilder2.append(".enc.key");
                                    key = new File(internalCacheDir, stringBuilder2.toString());
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
                                internalCacheDir = file.getParentFile();
                                stringBuilder2 = new StringBuilder();
                                stringBuilder2.append("q_");
                                stringBuilder2.append(file.getName());
                                key = new File(internalCacheDir, stringBuilder2.toString());
                                if (key.exists() && !key.delete()) {
                                    key.deleteOnExit();
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
}
