package org.telegram.messenger;

import android.util.SparseArray;
import com.google.android.exoplayer2.util.MimeTypes;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.InputFileLocation;
import org.telegram.tgnet.TLRPC.InputWebFileLocation;
import org.telegram.tgnet.TLRPC.TL_document;
import org.telegram.tgnet.TLRPC.TL_documentEncrypted;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_fileEncryptedLocation;
import org.telegram.tgnet.TLRPC.TL_fileHash;
import org.telegram.tgnet.TLRPC.TL_fileLocation;
import org.telegram.tgnet.TLRPC.TL_inputDocumentFileLocation;
import org.telegram.tgnet.TLRPC.TL_inputEncryptedFileLocation;
import org.telegram.tgnet.TLRPC.TL_inputFileLocation;
import org.telegram.tgnet.TLRPC.TL_inputSecureFileLocation;
import org.telegram.tgnet.TLRPC.TL_inputWebFileGeoPointLocation;
import org.telegram.tgnet.TLRPC.TL_upload_cdnFile;
import org.telegram.tgnet.TLRPC.TL_upload_cdnFileReuploadNeeded;
import org.telegram.tgnet.TLRPC.TL_upload_file;
import org.telegram.tgnet.TLRPC.TL_upload_fileCdnRedirect;
import org.telegram.tgnet.TLRPC.TL_upload_getCdnFile;
import org.telegram.tgnet.TLRPC.TL_upload_getCdnFileHashes;
import org.telegram.tgnet.TLRPC.TL_upload_getFile;
import org.telegram.tgnet.TLRPC.TL_upload_getWebFile;
import org.telegram.tgnet.TLRPC.TL_upload_reuploadCdnFile;
import org.telegram.tgnet.TLRPC.TL_upload_webFile;
import org.telegram.tgnet.TLRPC.Vector;

public class FileLoadOperation {
    private static final int bigFileSizeFrom = 1048576;
    private static final int cdnChunkCheckSize = 131072;
    private static final int downloadChunkSize = 32768;
    private static final int downloadChunkSizeBig = 131072;
    private static final int maxCdnParts = 12288;
    private static final int maxDownloadRequests = 4;
    private static final int maxDownloadRequestsBig = 4;
    private static final int stateDownloading = 1;
    private static final int stateFailed = 2;
    private static final int stateFinished = 3;
    private static final int stateIdle = 0;
    private boolean allowDisordererFileSave;
    private int bytesCountPadding;
    private File cacheFileFinal;
    private File cacheFileParts;
    private File cacheFileTemp;
    private File cacheIvTemp;
    private byte[] cdnCheckBytes;
    private int cdnDatacenterId;
    private SparseArray<TL_fileHash> cdnHashes;
    private byte[] cdnIv;
    private byte[] cdnKey;
    private byte[] cdnToken;
    private int currentAccount;
    private int currentDownloadChunkSize;
    private int currentMaxDownloadRequests;
    private int currentType;
    private int datacenterId;
    private ArrayList<RequestInfo> delayedRequestInfos;
    private FileLoadOperationDelegate delegate;
    private int downloadedBytes;
    private boolean encryptFile;
    private byte[] encryptIv;
    private byte[] encryptKey;
    private String ext;
    private RandomAccessFile fileOutputStream;
    private RandomAccessFile filePartsStream;
    private RandomAccessFile fileReadStream;
    private RandomAccessFile fiv;
    private int initialDatacenterId;
    private boolean isCdn;
    private boolean isForceRequest;
    /* renamed from: iv */
    private byte[] var_iv;
    private byte[] key;
    private InputFileLocation location;
    private ArrayList<Range> notCheckedCdnRanges;
    private ArrayList<Range> notLoadedBytesRanges;
    private volatile ArrayList<Range> notLoadedBytesRangesCopy;
    private ArrayList<Range> notRequestedBytesRanges;
    private Object parentObject;
    private volatile boolean paused;
    private int priority;
    private int renameRetryCount;
    private ArrayList<RequestInfo> requestInfos;
    private int requestedBytesCount;
    private boolean requestingCdnOffsets;
    protected boolean requestingReference;
    private int requestsCount;
    private boolean reuploadingCdn;
    private boolean started;
    private volatile int state;
    private File storePath;
    private ArrayList<FileStreamLoadOperation> streamListeners;
    private int streamStartOffset;
    private File tempPath;
    private int totalBytesCount;
    private WebFile webFile;
    private InputWebFileLocation webLocation;

    public interface FileLoadOperationDelegate {
        void didChangedLoadProgress(FileLoadOperation fileLoadOperation, float f);

        void didFailedLoadingFile(FileLoadOperation fileLoadOperation, int i);

        void didFinishLoadingFile(FileLoadOperation fileLoadOperation, File file);
    }

    public static class Range {
        private int end;
        private int start;

        private Range(int s, int e) {
            this.start = s;
            this.end = e;
        }
    }

    protected static class RequestInfo {
        private int offset;
        private int requestToken;
        private TL_upload_file response;
        private TL_upload_cdnFile responseCdn;
        private TL_upload_webFile responseWeb;

        protected RequestInfo() {
        }
    }

    public FileLoadOperation(FileLocation photoLocation, Object parent, String extension, int size) {
        this.state = 0;
        this.parentObject = parent;
        int i;
        if (photoLocation instanceof TL_fileEncryptedLocation) {
            this.location = new TL_inputEncryptedFileLocation();
            this.location.var_id = photoLocation.volume_id;
            this.location.volume_id = photoLocation.volume_id;
            this.location.access_hash = photoLocation.secret;
            this.location.local_id = photoLocation.local_id;
            this.var_iv = new byte[32];
            System.arraycopy(photoLocation.var_iv, 0, this.var_iv, 0, this.var_iv.length);
            this.key = photoLocation.key;
            i = photoLocation.dc_id;
            this.datacenterId = i;
            this.initialDatacenterId = i;
        } else if (photoLocation instanceof TL_fileLocation) {
            this.location = new TL_inputFileLocation();
            this.location.volume_id = photoLocation.volume_id;
            this.location.secret = photoLocation.secret;
            this.location.local_id = photoLocation.local_id;
            this.location.file_reference = photoLocation.file_reference;
            if (this.location.file_reference == null) {
                this.location.file_reference = new byte[0];
            }
            i = photoLocation.dc_id;
            this.datacenterId = i;
            this.initialDatacenterId = i;
            this.allowDisordererFileSave = true;
        }
        this.currentType = 16777216;
        this.totalBytesCount = size;
        if (extension == null) {
            extension = "jpg";
        }
        this.ext = extension;
    }

    public FileLoadOperation(SecureDocument secureDocument) {
        this.state = 0;
        this.location = new TL_inputSecureFileLocation();
        this.location.var_id = secureDocument.secureFile.var_id;
        this.location.access_hash = secureDocument.secureFile.access_hash;
        this.datacenterId = secureDocument.secureFile.dc_id;
        this.totalBytesCount = secureDocument.secureFile.size;
        this.allowDisordererFileSave = true;
        this.currentType = ConnectionsManager.FileTypeFile;
        this.ext = ".jpg";
    }

    public FileLoadOperation(int instance, WebFile webDocument) {
        this.state = 0;
        this.currentAccount = instance;
        this.webFile = webDocument;
        this.webLocation = webDocument.location;
        this.totalBytesCount = webDocument.size;
        int i = MessagesController.getInstance(this.currentAccount).webFileDatacenterId;
        this.datacenterId = i;
        this.initialDatacenterId = i;
        String defaultExt = FileLoader.getExtensionByMime(webDocument.mime_type);
        if (webDocument.mime_type.startsWith("image/")) {
            this.currentType = 16777216;
        } else if (webDocument.mime_type.equals("audio/ogg")) {
            this.currentType = ConnectionsManager.FileTypeAudio;
        } else if (webDocument.mime_type.startsWith("video/")) {
            this.currentType = ConnectionsManager.FileTypeVideo;
        } else {
            this.currentType = ConnectionsManager.FileTypeFile;
        }
        this.allowDisordererFileSave = true;
        this.ext = ImageLoader.getHttpUrlExtension(webDocument.url, defaultExt);
    }

    /* JADX WARNING: Removed duplicated region for block: B:36:0x00e7 A:{Catch:{ Exception -> 0x00d6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:17:0x007f A:{Catch:{ Exception -> 0x00d6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:50:? A:{SYNTHETIC, RETURN, Catch:{ Exception -> 0x00d6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x008b A:{Catch:{ Exception -> 0x00d6 }} */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public FileLoadOperation(Document documentLocation, Object parent) {
        int i = -1;
        this.state = 0;
        try {
            this.parentObject = parent;
            int i2;
            if (documentLocation instanceof TL_documentEncrypted) {
                this.location = new TL_inputEncryptedFileLocation();
                this.location.var_id = documentLocation.var_id;
                this.location.access_hash = documentLocation.access_hash;
                i2 = documentLocation.dc_id;
                this.datacenterId = i2;
                this.initialDatacenterId = i2;
                this.var_iv = new byte[32];
                System.arraycopy(documentLocation.var_iv, 0, this.var_iv, 0, this.var_iv.length);
                this.key = documentLocation.key;
            } else if (documentLocation instanceof TL_document) {
                this.location = new TL_inputDocumentFileLocation();
                this.location.var_id = documentLocation.var_id;
                this.location.access_hash = documentLocation.access_hash;
                this.location.file_reference = documentLocation.file_reference;
                if (this.location.file_reference == null) {
                    this.location.file_reference = new byte[0];
                }
                i2 = documentLocation.dc_id;
                this.datacenterId = i2;
                this.initialDatacenterId = i2;
                this.allowDisordererFileSave = true;
            }
            this.totalBytesCount = documentLocation.size;
            if (!(this.key == null || this.totalBytesCount % 16 == 0)) {
                this.bytesCountPadding = 16 - (this.totalBytesCount % 16);
                this.totalBytesCount += this.bytesCountPadding;
            }
            this.ext = FileLoader.getDocumentFileName(documentLocation);
            if (this.ext != null) {
                int idx = this.ext.lastIndexOf(46);
                if (idx != -1) {
                    this.ext = this.ext.substring(idx);
                    if (!"audio/ogg".equals(documentLocation.mime_type)) {
                        this.currentType = ConnectionsManager.FileTypeAudio;
                    } else if (MimeTypes.VIDEO_MP4.equals(documentLocation.mime_type)) {
                        this.currentType = ConnectionsManager.FileTypeVideo;
                    } else {
                        this.currentType = ConnectionsManager.FileTypeFile;
                    }
                    if (this.ext.length() <= 1) {
                        return;
                    }
                    if (documentLocation.mime_type != null) {
                        String str = documentLocation.mime_type;
                        switch (str.hashCode()) {
                            case 187091926:
                                if (str.equals("audio/ogg")) {
                                    boolean i3 = true;
                                    break;
                                }
                                break;
                            case 1331848029:
                                if (str.equals(MimeTypes.VIDEO_MP4)) {
                                    i3 = 0;
                                    break;
                                }
                                break;
                        }
                        switch (i3) {
                            case 0:
                                this.ext = ".mp4";
                                return;
                            case 1:
                                this.ext = ".ogg";
                                return;
                            default:
                                this.ext = TtmlNode.ANONYMOUS_REGION_ID;
                                return;
                        }
                    }
                    this.ext = TtmlNode.ANONYMOUS_REGION_ID;
                    return;
                }
            }
            this.ext = TtmlNode.ANONYMOUS_REGION_ID;
            if (!"audio/ogg".equals(documentLocation.mime_type)) {
            }
            if (this.ext.length() <= 1) {
            }
        } catch (Throwable e) {
            FileLog.m13e(e);
            onFail(true, 0);
        }
    }

    public void setEncryptFile(boolean value) {
        this.encryptFile = value;
        if (this.encryptFile) {
            this.allowDisordererFileSave = false;
        }
    }

    public int getDatacenterId() {
        return this.initialDatacenterId;
    }

    public void setForceRequest(boolean forceRequest) {
        this.isForceRequest = forceRequest;
    }

    public boolean isForceRequest() {
        return this.isForceRequest;
    }

    public void setPriority(int value) {
        this.priority = value;
    }

    public int getPriority() {
        return this.priority;
    }

    public void setPaths(int instance, File store, File temp) {
        this.storePath = store;
        this.tempPath = temp;
        this.currentAccount = instance;
    }

    public boolean wasStarted() {
        return this.started;
    }

    public int getCurrentType() {
        return this.currentType;
    }

    private void removePart(ArrayList<Range> ranges, int start, int end) {
        if (ranges != null && end >= start) {
            int count = ranges.size();
            boolean modified = false;
            int a = 0;
            while (a < count) {
                Range range = (Range) ranges.get(a);
                if (start == range.end) {
                    range.end = end;
                    modified = true;
                    break;
                } else if (end == range.start) {
                    range.start = start;
                    modified = true;
                    break;
                } else {
                    a++;
                }
            }
            if (!modified) {
                ranges.add(new Range(start, end));
            }
        }
    }

    private void addPart(ArrayList<Range> ranges, int start, int end, boolean save) {
        if (ranges != null && end >= start) {
            int a;
            Range range;
            boolean modified = false;
            int count = ranges.size();
            for (a = 0; a < count; a++) {
                range = (Range) ranges.get(a);
                if (start <= range.start) {
                    if (end >= range.end) {
                        ranges.remove(a);
                        modified = true;
                        break;
                    } else if (end > range.start) {
                        range.start = end;
                        modified = true;
                        break;
                    }
                } else if (end < range.end) {
                    ranges.add(0, new Range(range.start, start));
                    modified = true;
                    range.start = end;
                    break;
                } else if (start < range.end) {
                    range.end = start;
                    modified = true;
                    break;
                }
            }
            if (!save) {
                return;
            }
            if (modified) {
                try {
                    this.filePartsStream.seek(0);
                    count = ranges.size();
                    this.filePartsStream.writeInt(count);
                    for (a = 0; a < count; a++) {
                        range = (Range) ranges.get(a);
                        this.filePartsStream.writeInt(range.start);
                        this.filePartsStream.writeInt(range.end);
                    }
                } catch (Throwable e) {
                    FileLog.m13e(e);
                }
                if (this.streamListeners != null) {
                    count = this.streamListeners.size();
                    for (a = 0; a < count; a++) {
                        ((FileStreamLoadOperation) this.streamListeners.get(a)).newDataAvailable();
                    }
                }
            } else if (BuildVars.LOGS_ENABLED) {
                FileLog.m11e(this.cacheFileFinal + " downloaded duplicate file part " + start + " - " + end);
            }
        }
    }

    protected File getCurrentFile() {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        File[] result = new File[1];
        Utilities.stageQueue.postRunnable(new FileLoadOperation$$Lambda$0(this, result, countDownLatch));
        try {
            countDownLatch.await();
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
        return result[0];
    }

    final /* synthetic */ void lambda$getCurrentFile$0$FileLoadOperation(File[] result, CountDownLatch countDownLatch) {
        if (this.state == 3) {
            result[0] = this.cacheFileFinal;
        } else {
            result[0] = this.cacheFileTemp;
        }
        countDownLatch.countDown();
    }

    private int getDownloadedLengthFromOffsetInternal(ArrayList<Range> ranges, int offset, int length) {
        if (ranges != null && this.state != 3 && !ranges.isEmpty()) {
            int count = ranges.size();
            Range minRange = null;
            int availableLength = length;
            for (int a = 0; a < count; a++) {
                Range range = (Range) ranges.get(a);
                if (offset <= range.start && (minRange == null || range.start < minRange.start)) {
                    minRange = range;
                }
                if (range.start <= offset && range.end > offset) {
                    availableLength = 0;
                }
            }
            if (availableLength == 0) {
                return 0;
            }
            if (minRange != null) {
                return Math.min(length, minRange.start - offset);
            }
            return Math.min(length, Math.max(this.totalBytesCount - offset, 0));
        } else if (this.downloadedBytes == 0) {
            return length;
        } else {
            return Math.min(length, Math.max(this.downloadedBytes - offset, 0));
        }
    }

    protected float getDownloadedLengthFromOffset(float progress) {
        ArrayList<Range> ranges = this.notLoadedBytesRangesCopy;
        if (this.totalBytesCount == 0 || ranges == null) {
            return 0.0f;
        }
        return (((float) getDownloadedLengthFromOffsetInternal(ranges, (int) (((float) this.totalBytesCount) * progress), this.totalBytesCount)) / ((float) this.totalBytesCount)) + progress;
    }

    protected int getDownloadedLengthFromOffset(int offset, int length) {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        int[] result = new int[1];
        Utilities.stageQueue.postRunnable(new FileLoadOperation$$Lambda$1(this, result, offset, length, countDownLatch));
        try {
            countDownLatch.await();
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
        return result[0];
    }

    final /* synthetic */ void lambda$getDownloadedLengthFromOffset$1$FileLoadOperation(int[] result, int offset, int length, CountDownLatch countDownLatch) {
        result[0] = getDownloadedLengthFromOffsetInternal(this.notLoadedBytesRanges, offset, length);
        countDownLatch.countDown();
    }

    public String getFileName() {
        if (this.location != null) {
            return this.location.volume_id + "_" + this.location.local_id + "." + this.ext;
        }
        return Utilities.MD5(this.webFile.url) + "." + this.ext;
    }

    protected void removeStreamListener(FileStreamLoadOperation operation) {
        Utilities.stageQueue.postRunnable(new FileLoadOperation$$Lambda$2(this, operation));
    }

    final /* synthetic */ void lambda$removeStreamListener$2$FileLoadOperation(FileStreamLoadOperation operation) {
        if (this.streamListeners != null) {
            this.streamListeners.remove(operation);
        }
    }

    private void copytNotLoadedRanges() {
        if (this.notLoadedBytesRanges != null) {
            this.notLoadedBytesRangesCopy = new ArrayList(this.notLoadedBytesRanges);
        }
    }

    public void pause() {
        if (this.state == 1) {
            Utilities.stageQueue.postRunnable(new FileLoadOperation$$Lambda$3(this));
        }
    }

    final /* synthetic */ void lambda$pause$3$FileLoadOperation() {
        this.paused = true;
    }

    public boolean start() {
        return start(null, 0);
    }

    public boolean start(FileStreamLoadOperation stream, int streamOffset) {
        if (this.currentDownloadChunkSize == 0) {
            this.currentDownloadChunkSize = this.totalBytesCount >= 1048576 ? 131072 : 32768;
            this.currentMaxDownloadRequests = this.totalBytesCount >= 1048576 ? 4 : 4;
        }
        boolean alreadyStarted = this.state != 0;
        boolean wasPaused = this.paused;
        this.paused = false;
        if (stream != null) {
            Utilities.stageQueue.postRunnable(new FileLoadOperation$$Lambda$4(this, streamOffset, stream, alreadyStarted));
        } else if (wasPaused && alreadyStarted) {
            Utilities.stageQueue.postRunnable(new FileLoadOperation$$Lambda$5(this));
        }
        if (alreadyStarted) {
            return wasPaused;
        }
        if (this.location == null && this.webLocation == null) {
            onFail(true, 0);
            return false;
        }
        String fileNameTemp;
        String fileNameFinal;
        this.streamStartOffset = (streamOffset / this.currentDownloadChunkSize) * this.currentDownloadChunkSize;
        if (this.allowDisordererFileSave && this.totalBytesCount > 0 && this.totalBytesCount > this.currentDownloadChunkSize) {
            this.notLoadedBytesRanges = new ArrayList();
            this.notRequestedBytesRanges = new ArrayList();
        }
        String fileNameParts = null;
        String fileNameIv = null;
        if (this.webLocation != null) {
            String md5 = Utilities.MD5(this.webFile.url);
            if (this.encryptFile) {
                fileNameTemp = md5 + ".temp.enc";
                fileNameFinal = md5 + "." + this.ext + ".enc";
                if (this.key != null) {
                    fileNameIv = md5 + ".iv.enc";
                }
            } else {
                fileNameTemp = md5 + ".temp";
                fileNameFinal = md5 + "." + this.ext;
                if (this.key != null) {
                    fileNameIv = md5 + ".iv";
                }
            }
        } else if (this.location.volume_id == 0 || this.location.local_id == 0) {
            if (this.datacenterId == 0 || this.location.var_id == 0) {
                onFail(true, 0);
                return false;
            } else if (this.encryptFile) {
                fileNameTemp = this.datacenterId + "_" + this.location.var_id + ".temp.enc";
                fileNameFinal = this.datacenterId + "_" + this.location.var_id + this.ext + ".enc";
                if (this.key != null) {
                    fileNameIv = this.datacenterId + "_" + this.location.var_id + ".iv.enc";
                }
            } else {
                fileNameTemp = this.datacenterId + "_" + this.location.var_id + ".temp";
                fileNameFinal = this.datacenterId + "_" + this.location.var_id + this.ext;
                if (this.key != null) {
                    fileNameIv = this.datacenterId + "_" + this.location.var_id + ".iv";
                }
                if (this.notLoadedBytesRanges != null) {
                    fileNameParts = this.datacenterId + "_" + this.location.var_id + ".pt";
                }
            }
        } else if (this.datacenterId == Integer.MIN_VALUE || this.location.volume_id == -2147483648L || this.datacenterId == 0) {
            onFail(true, 0);
            return false;
        } else if (this.encryptFile) {
            fileNameTemp = this.location.volume_id + "_" + this.location.local_id + ".temp.enc";
            fileNameFinal = this.location.volume_id + "_" + this.location.local_id + "." + this.ext + ".enc";
            if (this.key != null) {
                fileNameIv = this.location.volume_id + "_" + this.location.local_id + ".iv.enc";
            }
        } else {
            fileNameTemp = this.location.volume_id + "_" + this.location.local_id + ".temp";
            fileNameFinal = this.location.volume_id + "_" + this.location.local_id + "." + this.ext;
            if (this.key != null) {
                fileNameIv = this.location.volume_id + "_" + this.location.local_id + ".iv";
            }
            if (this.notLoadedBytesRanges != null) {
                fileNameParts = this.location.volume_id + "_" + this.location.local_id + ".pt";
            }
        }
        this.requestInfos = new ArrayList(this.currentMaxDownloadRequests);
        this.delayedRequestInfos = new ArrayList(this.currentMaxDownloadRequests - 1);
        this.state = 1;
        this.cacheFileFinal = new File(this.storePath, fileNameFinal);
        boolean finalFileExist = this.cacheFileFinal.exists();
        if (!(!finalFileExist || this.totalBytesCount == 0 || ((long) this.totalBytesCount) == this.cacheFileFinal.length())) {
            this.cacheFileFinal.delete();
            finalFileExist = false;
        }
        if (finalFileExist) {
            this.started = true;
            try {
                onFinishLoadingFile(false);
            } catch (Exception e) {
                onFail(true, 0);
            }
        } else {
            long len;
            int a;
            this.cacheFileTemp = new File(this.tempPath, fileNameTemp);
            boolean newKeyGenerated = false;
            if (this.encryptFile) {
                File keyFile = new File(FileLoader.getInternalCacheDir(), fileNameFinal + ".key");
                RandomAccessFile file = new RandomAccessFile(keyFile, "rws");
                len = keyFile.length();
                this.encryptKey = new byte[32];
                this.encryptIv = new byte[16];
                if (len <= 0 || len % 48 != 0) {
                    try {
                        Utilities.random.nextBytes(this.encryptKey);
                        Utilities.random.nextBytes(this.encryptIv);
                        file.write(this.encryptKey);
                        file.write(this.encryptIv);
                        newKeyGenerated = true;
                    } catch (Throwable e2) {
                        FileLog.m13e(e2);
                    }
                } else {
                    file.read(this.encryptKey, 0, 32);
                    file.read(this.encryptIv, 0, 16);
                }
                try {
                    file.getChannel().close();
                } catch (Throwable e22) {
                    FileLog.m13e(e22);
                }
                file.close();
            }
            if (fileNameParts != null) {
                this.cacheFileParts = new File(this.tempPath, fileNameParts);
                try {
                    this.filePartsStream = new RandomAccessFile(this.cacheFileParts, "rws");
                    len = this.filePartsStream.length();
                    if (len % 8 == 4) {
                        len -= 4;
                        int count = this.filePartsStream.readInt();
                        if (((long) count) <= len / 2) {
                            for (a = 0; a < count; a++) {
                                int start = this.filePartsStream.readInt();
                                int end = this.filePartsStream.readInt();
                                this.notLoadedBytesRanges.add(new Range(start, end));
                                this.notRequestedBytesRanges.add(new Range(start, end));
                            }
                        }
                    }
                } catch (Throwable e222) {
                    FileLog.m13e(e222);
                }
            }
            if (this.cacheFileTemp.exists()) {
                if (newKeyGenerated) {
                    this.cacheFileTemp.delete();
                } else {
                    long totalDownloadedLen = this.cacheFileTemp.length();
                    if (fileNameIv == null || totalDownloadedLen % ((long) this.currentDownloadChunkSize) == 0) {
                        int length = (((int) this.cacheFileTemp.length()) / this.currentDownloadChunkSize) * this.currentDownloadChunkSize;
                        this.downloadedBytes = length;
                        this.requestedBytesCount = length;
                    } else {
                        this.downloadedBytes = 0;
                        this.requestedBytesCount = 0;
                    }
                    if (this.notLoadedBytesRanges != null && this.notLoadedBytesRanges.isEmpty()) {
                        this.notLoadedBytesRanges.add(new Range(this.downloadedBytes, this.totalBytesCount));
                        this.notRequestedBytesRanges.add(new Range(this.downloadedBytes, this.totalBytesCount));
                    }
                }
            } else if (this.notLoadedBytesRanges != null && this.notLoadedBytesRanges.isEmpty()) {
                this.notLoadedBytesRanges.add(new Range(0, this.totalBytesCount));
                this.notRequestedBytesRanges.add(new Range(0, this.totalBytesCount));
            }
            if (this.notLoadedBytesRanges != null) {
                this.downloadedBytes = this.totalBytesCount;
                int size = this.notLoadedBytesRanges.size();
                for (a = 0; a < size; a++) {
                    Range range = (Range) this.notLoadedBytesRanges.get(a);
                    this.downloadedBytes -= range.end - range.start;
                }
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.m10d("start loading file to temp = " + this.cacheFileTemp + " final = " + this.cacheFileFinal);
            }
            if (fileNameIv != null) {
                this.cacheIvTemp = new File(this.tempPath, fileNameIv);
                try {
                    this.fiv = new RandomAccessFile(this.cacheIvTemp, "rws");
                    if (!(this.downloadedBytes == 0 || newKeyGenerated)) {
                        len = this.cacheIvTemp.length();
                        if (len <= 0 || len % 32 != 0) {
                            this.downloadedBytes = 0;
                            this.requestedBytesCount = 0;
                        } else {
                            this.fiv.read(this.var_iv, 0, 32);
                        }
                    }
                } catch (Throwable e2222) {
                    FileLog.m13e(e2222);
                    this.downloadedBytes = 0;
                    this.requestedBytesCount = 0;
                }
            }
            if (this.downloadedBytes != 0 && this.totalBytesCount > 0) {
                copytNotLoadedRanges();
                this.delegate.didChangedLoadProgress(this, Math.min(1.0f, ((float) this.downloadedBytes) / ((float) this.totalBytesCount)));
            }
            try {
                this.fileOutputStream = new RandomAccessFile(this.cacheFileTemp, "rws");
                if (this.downloadedBytes != 0) {
                    this.fileOutputStream.seek((long) this.downloadedBytes);
                }
            } catch (Throwable e22222) {
                FileLog.m13e(e22222);
            }
            if (this.fileOutputStream == null) {
                onFail(true, 0);
                return false;
            }
            this.started = true;
            Utilities.stageQueue.postRunnable(new FileLoadOperation$$Lambda$6(this));
        }
        return true;
    }

    final /* synthetic */ void lambda$start$4$FileLoadOperation(int streamOffset, FileStreamLoadOperation stream, boolean alreadyStarted) {
        if (this.streamListeners == null) {
            this.streamListeners = new ArrayList();
        }
        this.streamStartOffset = (streamOffset / this.currentDownloadChunkSize) * this.currentDownloadChunkSize;
        this.streamListeners.add(stream);
        if (alreadyStarted) {
            startDownloadRequest();
        }
    }

    final /* synthetic */ void lambda$start$5$FileLoadOperation() {
        if (this.totalBytesCount == 0 || this.downloadedBytes != this.totalBytesCount) {
            startDownloadRequest();
            return;
        }
        try {
            onFinishLoadingFile(false);
        } catch (Exception e) {
            onFail(true, 0);
        }
    }

    public boolean isPaused() {
        return this.paused;
    }

    public void cancel() {
        Utilities.stageQueue.postRunnable(new FileLoadOperation$$Lambda$7(this));
    }

    final /* synthetic */ void lambda$cancel$6$FileLoadOperation() {
        if (this.state != 3 && this.state != 2) {
            if (this.requestInfos != null) {
                for (int a = 0; a < this.requestInfos.size(); a++) {
                    RequestInfo requestInfo = (RequestInfo) this.requestInfos.get(a);
                    if (requestInfo.requestToken != 0) {
                        ConnectionsManager.getInstance(this.currentAccount).cancelRequest(requestInfo.requestToken, true);
                    }
                }
            }
            onFail(false, 1);
        }
    }

    private void cleanup() {
        try {
            if (this.fileOutputStream != null) {
                try {
                    this.fileOutputStream.getChannel().close();
                } catch (Throwable e) {
                    FileLog.m13e(e);
                }
                this.fileOutputStream.close();
                this.fileOutputStream = null;
            }
        } catch (Throwable e2) {
            FileLog.m13e(e2);
        }
        try {
            if (this.fileReadStream != null) {
                try {
                    this.fileReadStream.getChannel().close();
                } catch (Throwable e22) {
                    FileLog.m13e(e22);
                }
                this.fileReadStream.close();
                this.fileReadStream = null;
            }
        } catch (Throwable e222) {
            FileLog.m13e(e222);
        }
        try {
            if (this.filePartsStream != null) {
                try {
                    this.filePartsStream.getChannel().close();
                } catch (Throwable e2222) {
                    FileLog.m13e(e2222);
                }
                this.filePartsStream.close();
                this.filePartsStream = null;
            }
        } catch (Throwable e22222) {
            FileLog.m13e(e22222);
        }
        try {
            if (this.fiv != null) {
                this.fiv.close();
                this.fiv = null;
            }
        } catch (Throwable e222222) {
            FileLog.m13e(e222222);
        }
        if (this.delayedRequestInfos != null) {
            for (int a = 0; a < this.delayedRequestInfos.size(); a++) {
                RequestInfo requestInfo = (RequestInfo) this.delayedRequestInfos.get(a);
                if (requestInfo.response != null) {
                    requestInfo.response.disableFree = false;
                    requestInfo.response.freeResources();
                } else if (requestInfo.responseWeb != null) {
                    requestInfo.responseWeb.disableFree = false;
                    requestInfo.responseWeb.freeResources();
                } else if (requestInfo.responseCdn != null) {
                    requestInfo.responseCdn.disableFree = false;
                    requestInfo.responseCdn.freeResources();
                }
            }
            this.delayedRequestInfos.clear();
        }
    }

    private void onFinishLoadingFile(boolean increment) {
        if (this.state == 1) {
            this.state = 3;
            cleanup();
            if (this.cacheIvTemp != null) {
                this.cacheIvTemp.delete();
                this.cacheIvTemp = null;
            }
            if (this.cacheFileParts != null) {
                this.cacheFileParts.delete();
                this.cacheFileParts = null;
            }
            if (!(this.cacheFileTemp == null || this.cacheFileTemp.renameTo(this.cacheFileFinal))) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.m11e("unable to rename temp = " + this.cacheFileTemp + " to final = " + this.cacheFileFinal + " retry = " + this.renameRetryCount);
                }
                this.renameRetryCount++;
                if (this.renameRetryCount < 3) {
                    this.state = 1;
                    Utilities.stageQueue.postRunnable(new FileLoadOperation$$Lambda$8(this, increment), 200);
                    return;
                }
                this.cacheFileFinal = this.cacheFileTemp;
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.m10d("finished downloading file to " + this.cacheFileFinal);
            }
            this.delegate.didFinishLoadingFile(this, this.cacheFileFinal);
            if (!increment) {
                return;
            }
            if (this.currentType == ConnectionsManager.FileTypeAudio) {
                StatsController.getInstance(this.currentAccount).incrementReceivedItemsCount(ApplicationLoader.getCurrentNetworkType(), 3, 1);
            } else if (this.currentType == ConnectionsManager.FileTypeVideo) {
                StatsController.getInstance(this.currentAccount).incrementReceivedItemsCount(ApplicationLoader.getCurrentNetworkType(), 2, 1);
            } else if (this.currentType == 16777216) {
                StatsController.getInstance(this.currentAccount).incrementReceivedItemsCount(ApplicationLoader.getCurrentNetworkType(), 4, 1);
            } else if (this.currentType == ConnectionsManager.FileTypeFile) {
                StatsController.getInstance(this.currentAccount).incrementReceivedItemsCount(ApplicationLoader.getCurrentNetworkType(), 5, 1);
            }
        }
    }

    final /* synthetic */ void lambda$onFinishLoadingFile$7$FileLoadOperation(boolean increment) {
        try {
            onFinishLoadingFile(increment);
        } catch (Exception e) {
            onFail(false, 0);
        }
    }

    private void delayRequestInfo(RequestInfo requestInfo) {
        this.delayedRequestInfos.add(requestInfo);
        if (requestInfo.response != null) {
            requestInfo.response.disableFree = true;
        } else if (requestInfo.responseWeb != null) {
            requestInfo.responseWeb.disableFree = true;
        } else if (requestInfo.responseCdn != null) {
            requestInfo.responseCdn.disableFree = true;
        }
    }

    private void requestFileOffsets(int offset) {
        if (!this.requestingCdnOffsets) {
            this.requestingCdnOffsets = true;
            TL_upload_getCdnFileHashes req = new TL_upload_getCdnFileHashes();
            req.file_token = this.cdnToken;
            req.offset = offset;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new FileLoadOperation$$Lambda$9(this), null, null, 0, this.datacenterId, 1, true);
        }
    }

    final /* synthetic */ void lambda$requestFileOffsets$8$FileLoadOperation(TLObject response, TL_error error) {
        if (error != null) {
            onFail(false, 0);
            return;
        }
        int a;
        this.requestingCdnOffsets = false;
        Vector vector = (Vector) response;
        if (!vector.objects.isEmpty()) {
            if (this.cdnHashes == null) {
                this.cdnHashes = new SparseArray();
            }
            for (a = 0; a < vector.objects.size(); a++) {
                TL_fileHash hash = (TL_fileHash) vector.objects.get(a);
                this.cdnHashes.put(hash.offset, hash);
            }
        }
        for (a = 0; a < this.delayedRequestInfos.size(); a++) {
            RequestInfo delayedRequestInfo = (RequestInfo) this.delayedRequestInfos.get(a);
            if (this.notLoadedBytesRanges != null || this.downloadedBytes == delayedRequestInfo.offset) {
                this.delayedRequestInfos.remove(a);
                if (!processRequestResult(delayedRequestInfo, null)) {
                    if (delayedRequestInfo.response != null) {
                        delayedRequestInfo.response.disableFree = false;
                        delayedRequestInfo.response.freeResources();
                        return;
                    } else if (delayedRequestInfo.responseWeb != null) {
                        delayedRequestInfo.responseWeb.disableFree = false;
                        delayedRequestInfo.responseWeb.freeResources();
                        return;
                    } else if (delayedRequestInfo.responseCdn != null) {
                        delayedRequestInfo.responseCdn.disableFree = false;
                        delayedRequestInfo.responseCdn.freeResources();
                        return;
                    } else {
                        return;
                    }
                }
                return;
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:143:0x04af A:{Catch:{ Exception -> 0x03c6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:135:0x047a A:{Catch:{ Exception -> 0x03c6 }} */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    protected boolean processRequestResult(RequestInfo requestInfo, TL_error error) {
        if (this.state != 1) {
            if (BuildVars.DEBUG_VERSION) {
                FileLog.m10d("trying to write to finished file " + this.cacheFileFinal + " offset " + requestInfo.offset);
            }
            return false;
        }
        this.requestInfos.remove(requestInfo);
        if (error == null) {
            try {
                if (this.notLoadedBytesRanges != null || this.downloadedBytes == requestInfo.offset) {
                    NativeByteBuffer bytes;
                    if (requestInfo.response != null) {
                        bytes = requestInfo.response.bytes;
                    } else if (requestInfo.responseWeb != null) {
                        bytes = requestInfo.responseWeb.bytes;
                    } else if (requestInfo.responseCdn != null) {
                        bytes = requestInfo.responseCdn.bytes;
                    } else {
                        bytes = null;
                    }
                    if (bytes == null || bytes.limit() == 0) {
                        onFinishLoadingFile(true);
                        return false;
                    }
                    int fileOffset;
                    int offset;
                    int a;
                    int currentBytesSize = bytes.limit();
                    if (this.isCdn) {
                        fileOffset = (requestInfo.offset / 131072) * 131072;
                        if ((this.cdnHashes != null ? (TL_fileHash) this.cdnHashes.get(fileOffset) : null) == null) {
                            delayRequestInfo(requestInfo);
                            requestFileOffsets(fileOffset);
                            return true;
                        }
                    }
                    if (requestInfo.responseCdn != null) {
                        offset = requestInfo.offset / 16;
                        this.cdnIv[15] = (byte) (offset & 255);
                        this.cdnIv[14] = (byte) ((offset >> 8) & 255);
                        this.cdnIv[13] = (byte) ((offset >> 16) & 255);
                        this.cdnIv[12] = (byte) ((offset >> 24) & 255);
                        Utilities.aesCtrDecryption(bytes.buffer, this.cdnKey, this.cdnIv, 0, bytes.limit());
                    }
                    this.downloadedBytes += currentBytesSize;
                    boolean finishedDownloading = this.totalBytesCount > 0 ? this.downloadedBytes >= this.totalBytesCount : currentBytesSize != this.currentDownloadChunkSize || ((this.totalBytesCount == this.downloadedBytes || this.downloadedBytes % this.currentDownloadChunkSize != 0) && (this.totalBytesCount <= 0 || this.totalBytesCount <= this.downloadedBytes));
                    if (this.key != null) {
                        Utilities.aesIgeEncryption(bytes.buffer, this.key, this.var_iv, false, true, 0, bytes.limit());
                        if (finishedDownloading && this.bytesCountPadding != 0) {
                            bytes.limit(bytes.limit() - this.bytesCountPadding);
                        }
                    }
                    if (this.encryptFile) {
                        offset = requestInfo.offset / 16;
                        this.encryptIv[15] = (byte) (offset & 255);
                        this.encryptIv[14] = (byte) ((offset >> 8) & 255);
                        this.encryptIv[13] = (byte) ((offset >> 16) & 255);
                        this.encryptIv[12] = (byte) ((offset >> 24) & 255);
                        Utilities.aesCtrDecryption(bytes.buffer, this.encryptKey, this.encryptIv, 0, bytes.limit());
                    }
                    if (this.notLoadedBytesRanges != null) {
                        this.fileOutputStream.seek((long) requestInfo.offset);
                        if (BuildVars.DEBUG_VERSION) {
                            FileLog.m10d("save file part " + this.cacheFileFinal + " offset " + requestInfo.offset);
                        }
                    }
                    this.fileOutputStream.getChannel().write(bytes.buffer);
                    addPart(this.notLoadedBytesRanges, requestInfo.offset, requestInfo.offset + currentBytesSize, true);
                    if (this.isCdn) {
                        int cdnCheckPart = requestInfo.offset / 131072;
                        int size = this.notCheckedCdnRanges.size();
                        boolean checked = true;
                        for (a = 0; a < size; a++) {
                            Range range = (Range) this.notCheckedCdnRanges.get(a);
                            if (range.start <= cdnCheckPart && cdnCheckPart <= range.end) {
                                checked = false;
                                break;
                            }
                        }
                        if (!checked) {
                            fileOffset = cdnCheckPart * 131072;
                            int availableSize = getDownloadedLengthFromOffsetInternal(this.notLoadedBytesRanges, fileOffset, 131072);
                            if (availableSize != 0 && (availableSize == 131072 || ((this.totalBytesCount > 0 && availableSize == this.totalBytesCount - fileOffset) || (this.totalBytesCount <= 0 && finishedDownloading)))) {
                                TL_fileHash hash = (TL_fileHash) this.cdnHashes.get(fileOffset);
                                if (this.fileReadStream == null) {
                                    this.cdnCheckBytes = new byte[131072];
                                    this.fileReadStream = new RandomAccessFile(this.cacheFileTemp, "r");
                                }
                                this.fileReadStream.seek((long) fileOffset);
                                this.fileReadStream.readFully(this.cdnCheckBytes, 0, availableSize);
                                if (Arrays.equals(Utilities.computeSHA256(this.cdnCheckBytes, 0, availableSize), hash.hash)) {
                                    this.cdnHashes.remove(fileOffset);
                                    addPart(this.notCheckedCdnRanges, cdnCheckPart, cdnCheckPart + 1, false);
                                } else {
                                    if (BuildVars.LOGS_ENABLED) {
                                        if (this.location != null) {
                                            FileLog.m11e("invalid cdn hash " + this.location + " id = " + this.location.var_id + " local_id = " + this.location.local_id + " access_hash = " + this.location.access_hash + " volume_id = " + this.location.volume_id + " secret = " + this.location.secret);
                                        } else if (this.webLocation != null) {
                                            FileLog.m11e("invalid cdn hash  " + this.webLocation + " id = " + getFileName());
                                        }
                                    }
                                    onFail(false, 0);
                                    this.cacheFileTemp.delete();
                                    return false;
                                }
                            }
                        }
                    }
                    if (this.fiv != null) {
                        this.fiv.seek(0);
                        this.fiv.write(this.var_iv);
                    }
                    if (this.totalBytesCount > 0 && this.state == 1) {
                        copytNotLoadedRanges();
                        this.delegate.didChangedLoadProgress(this, Math.min(1.0f, ((float) this.downloadedBytes) / ((float) this.totalBytesCount)));
                    }
                    a = 0;
                    while (a < this.delayedRequestInfos.size()) {
                        RequestInfo delayedRequestInfo = (RequestInfo) this.delayedRequestInfos.get(a);
                        if (this.notLoadedBytesRanges != null || this.downloadedBytes == delayedRequestInfo.offset) {
                            this.delayedRequestInfos.remove(a);
                            if (!processRequestResult(delayedRequestInfo, null)) {
                                if (delayedRequestInfo.response != null) {
                                    delayedRequestInfo.response.disableFree = false;
                                    delayedRequestInfo.response.freeResources();
                                } else if (delayedRequestInfo.responseWeb != null) {
                                    delayedRequestInfo.responseWeb.disableFree = false;
                                    delayedRequestInfo.responseWeb.freeResources();
                                } else if (delayedRequestInfo.responseCdn != null) {
                                    delayedRequestInfo.responseCdn.disableFree = false;
                                    delayedRequestInfo.responseCdn.freeResources();
                                }
                            }
                            if (finishedDownloading) {
                                startDownloadRequest();
                            } else {
                                onFinishLoadingFile(true);
                            }
                        } else {
                            a++;
                        }
                    }
                    if (finishedDownloading) {
                    }
                } else {
                    delayRequestInfo(requestInfo);
                    return false;
                }
            } catch (Throwable e) {
                onFail(false, 0);
                FileLog.m13e(e);
            }
        } else if (error.text.contains("FILE_MIGRATE_")) {
            Integer val;
            Scanner scanner = new Scanner(error.text.replace("FILE_MIGRATE_", TtmlNode.ANONYMOUS_REGION_ID));
            scanner.useDelimiter(TtmlNode.ANONYMOUS_REGION_ID);
            try {
                val = Integer.valueOf(scanner.nextInt());
            } catch (Exception e2) {
                val = null;
            }
            if (val == null) {
                onFail(false, 0);
            } else {
                this.datacenterId = val.intValue();
                this.downloadedBytes = 0;
                this.requestedBytesCount = 0;
                startDownloadRequest();
            }
        } else if (error.text.contains("OFFSET_INVALID")) {
            if (this.downloadedBytes % this.currentDownloadChunkSize == 0) {
                try {
                    onFinishLoadingFile(true);
                } catch (Throwable e3) {
                    FileLog.m13e(e3);
                    onFail(false, 0);
                }
            } else {
                onFail(false, 0);
            }
        } else if (error.text.contains("RETRY_LIMIT")) {
            onFail(false, 2);
        } else {
            if (BuildVars.LOGS_ENABLED) {
                if (this.location != null) {
                    FileLog.m11e(error.text + " " + this.location + " id = " + this.location.var_id + " local_id = " + this.location.local_id + " access_hash = " + this.location.access_hash + " volume_id = " + this.location.volume_id + " secret = " + this.location.secret);
                } else if (this.webLocation != null) {
                    FileLog.m11e(error.text + " " + this.webLocation + " id = " + getFileName());
                }
            }
            onFail(false, 0);
        }
        return false;
    }

    protected void onFail(boolean thread, int reason) {
        cleanup();
        this.state = 2;
        if (thread) {
            Utilities.stageQueue.postRunnable(new FileLoadOperation$$Lambda$10(this, reason));
        } else {
            this.delegate.didFailedLoadingFile(this, reason);
        }
    }

    final /* synthetic */ void lambda$onFail$9$FileLoadOperation(int reason) {
        this.delegate.didFailedLoadingFile(this, reason);
    }

    private void clearOperaion(RequestInfo currentInfo) {
        int a;
        RequestInfo info;
        int minOffset = ConnectionsManager.DEFAULT_DATACENTER_ID;
        for (a = 0; a < this.requestInfos.size(); a++) {
            info = (RequestInfo) this.requestInfos.get(a);
            minOffset = Math.min(info.offset, minOffset);
            removePart(this.notRequestedBytesRanges, info.offset, info.offset + this.currentDownloadChunkSize);
            if (!(currentInfo == info || info.requestToken == 0)) {
                ConnectionsManager.getInstance(this.currentAccount).cancelRequest(info.requestToken, true);
            }
        }
        this.requestInfos.clear();
        for (a = 0; a < this.delayedRequestInfos.size(); a++) {
            info = (RequestInfo) this.delayedRequestInfos.get(a);
            removePart(this.notRequestedBytesRanges, info.offset, info.offset + this.currentDownloadChunkSize);
            if (info.response != null) {
                info.response.disableFree = false;
                info.response.freeResources();
            } else if (info.responseWeb != null) {
                info.responseWeb.disableFree = false;
                info.responseWeb.freeResources();
            } else if (info.responseCdn != null) {
                info.responseCdn.disableFree = false;
                info.responseCdn.freeResources();
            }
            minOffset = Math.min(info.offset, minOffset);
        }
        this.delayedRequestInfos.clear();
        this.requestsCount = 0;
        if (this.notLoadedBytesRanges == null) {
            this.downloadedBytes = minOffset;
            this.requestedBytesCount = minOffset;
        }
    }

    private void requestReference(RequestInfo requestInfo) {
        if (!this.requestingReference) {
            clearOperaion(requestInfo);
            this.requestingReference = true;
            if (this.parentObject instanceof MessageObject) {
                MessageObject messageObject = this.parentObject;
                if (messageObject.getId() < 0 && messageObject.messageOwner.media.webpage != null) {
                    this.parentObject = messageObject.messageOwner.media.webpage;
                }
            }
            FileRefController.getInstance(this.currentAccount).requestReference(this.parentObject, this.location, this, requestInfo);
        }
    }

    protected void startDownloadRequest() {
        if (!this.paused && this.state == 1 && this.requestInfos.size() + this.delayedRequestInfos.size() < this.currentMaxDownloadRequests) {
            int count = 1;
            if (this.totalBytesCount > 0) {
                count = Math.max(0, this.currentMaxDownloadRequests - this.requestInfos.size());
            }
            int a = 0;
            while (a < count) {
                int downloadOffset;
                if (this.notRequestedBytesRanges != null) {
                    int size = this.notRequestedBytesRanges.size();
                    int minStart = ConnectionsManager.DEFAULT_DATACENTER_ID;
                    int minStreamStart = ConnectionsManager.DEFAULT_DATACENTER_ID;
                    for (int b = 0; b < size; b++) {
                        Range range = (Range) this.notRequestedBytesRanges.get(b);
                        if (this.streamStartOffset != 0) {
                            if (range.start <= this.streamStartOffset && range.end > this.streamStartOffset) {
                                minStreamStart = this.streamStartOffset;
                                minStart = ConnectionsManager.DEFAULT_DATACENTER_ID;
                                break;
                            } else if (this.streamStartOffset < range.start && range.start < minStreamStart) {
                                minStreamStart = range.start;
                            }
                        }
                        minStart = Math.min(minStart, range.start);
                    }
                    if (minStreamStart != Integer.MAX_VALUE) {
                        downloadOffset = minStreamStart;
                    } else if (minStart != ConnectionsManager.DEFAULT_DATACENTER_ID) {
                        downloadOffset = minStart;
                    } else {
                        return;
                    }
                }
                downloadOffset = this.requestedBytesCount;
                if (this.notRequestedBytesRanges != null) {
                    addPart(this.notRequestedBytesRanges, downloadOffset, this.currentDownloadChunkSize + downloadOffset, false);
                }
                if (this.totalBytesCount <= 0 || downloadOffset < this.totalBytesCount) {
                    TLObject request;
                    int i;
                    boolean isLast = this.totalBytesCount <= 0 || a == count - 1 || (this.totalBytesCount > 0 && this.currentDownloadChunkSize + downloadOffset >= this.totalBytesCount);
                    int connectionType = this.requestsCount % 2 == 0 ? 2 : ConnectionsManager.ConnectionTypeDownload2;
                    int flags = this.isForceRequest ? 32 : 0;
                    if (!(this.webLocation instanceof TL_inputWebFileGeoPointLocation)) {
                        flags |= 2;
                    }
                    TLObject req;
                    if (this.isCdn) {
                        req = new TL_upload_getCdnFile();
                        req.file_token = this.cdnToken;
                        req.offset = downloadOffset;
                        req.limit = this.currentDownloadChunkSize;
                        request = req;
                        flags |= 1;
                    } else if (this.webLocation != null) {
                        req = new TL_upload_getWebFile();
                        req.location = this.webLocation;
                        req.offset = downloadOffset;
                        req.limit = this.currentDownloadChunkSize;
                        request = req;
                    } else {
                        req = new TL_upload_getFile();
                        req.location = this.location;
                        req.offset = downloadOffset;
                        req.limit = this.currentDownloadChunkSize;
                        request = req;
                    }
                    this.requestedBytesCount += this.currentDownloadChunkSize;
                    RequestInfo requestInfo = new RequestInfo();
                    this.requestInfos.add(requestInfo);
                    requestInfo.offset = downloadOffset;
                    ConnectionsManager instance = ConnectionsManager.getInstance(this.currentAccount);
                    RequestDelegate fileLoadOperation$$Lambda$11 = new FileLoadOperation$$Lambda$11(this, requestInfo, request);
                    if (this.isCdn) {
                        i = this.cdnDatacenterId;
                    } else {
                        i = this.datacenterId;
                    }
                    requestInfo.requestToken = instance.sendRequest(request, fileLoadOperation$$Lambda$11, null, null, flags, i, connectionType, isLast);
                    this.requestsCount++;
                    a++;
                } else {
                    return;
                }
            }
        }
    }

    final /* synthetic */ void lambda$startDownloadRequest$11$FileLoadOperation(RequestInfo requestInfo, TLObject request, TLObject response, TL_error error) {
        if (this.requestInfos.contains(requestInfo)) {
            if (error != null) {
                if (FileRefController.isFileRefError(error.text)) {
                    requestReference(requestInfo);
                    return;
                } else if ((request instanceof TL_upload_getCdnFile) && error.text.equals("FILE_TOKEN_INVALID")) {
                    this.isCdn = false;
                    clearOperaion(requestInfo);
                    startDownloadRequest();
                    return;
                }
            }
            if (response instanceof TL_upload_fileCdnRedirect) {
                TL_upload_fileCdnRedirect res = (TL_upload_fileCdnRedirect) response;
                if (!res.file_hashes.isEmpty()) {
                    if (this.cdnHashes == null) {
                        this.cdnHashes = new SparseArray();
                    }
                    for (int a1 = 0; a1 < res.file_hashes.size(); a1++) {
                        TL_fileHash hash = (TL_fileHash) res.file_hashes.get(a1);
                        this.cdnHashes.put(hash.offset, hash);
                    }
                }
                if (res.encryption_iv == null || res.encryption_key == null || res.encryption_iv.length != 16 || res.encryption_key.length != 32) {
                    error = new TL_error();
                    error.text = "bad redirect response";
                    error.code = 400;
                    processRequestResult(requestInfo, error);
                    return;
                }
                this.isCdn = true;
                if (this.notCheckedCdnRanges == null) {
                    this.notCheckedCdnRanges = new ArrayList();
                    this.notCheckedCdnRanges.add(new Range(0, maxCdnParts));
                }
                this.cdnDatacenterId = res.dc_id;
                this.cdnIv = res.encryption_iv;
                this.cdnKey = res.encryption_key;
                this.cdnToken = res.file_token;
                clearOperaion(requestInfo);
                startDownloadRequest();
            } else if (!(response instanceof TL_upload_cdnFileReuploadNeeded)) {
                if (response instanceof TL_upload_file) {
                    requestInfo.response = (TL_upload_file) response;
                } else if (response instanceof TL_upload_webFile) {
                    requestInfo.responseWeb = (TL_upload_webFile) response;
                    if (this.totalBytesCount == 0 && requestInfo.responseWeb.size != 0) {
                        this.totalBytesCount = requestInfo.responseWeb.size;
                    }
                } else {
                    requestInfo.responseCdn = (TL_upload_cdnFile) response;
                }
                if (response != null) {
                    if (this.currentType == ConnectionsManager.FileTypeAudio) {
                        StatsController.getInstance(this.currentAccount).incrementReceivedBytesCount(response.networkType, 3, (long) (response.getObjectSize() + 4));
                    } else if (this.currentType == ConnectionsManager.FileTypeVideo) {
                        StatsController.getInstance(this.currentAccount).incrementReceivedBytesCount(response.networkType, 2, (long) (response.getObjectSize() + 4));
                    } else if (this.currentType == 16777216) {
                        StatsController.getInstance(this.currentAccount).incrementReceivedBytesCount(response.networkType, 4, (long) (response.getObjectSize() + 4));
                    } else if (this.currentType == ConnectionsManager.FileTypeFile) {
                        StatsController.getInstance(this.currentAccount).incrementReceivedBytesCount(response.networkType, 5, (long) (response.getObjectSize() + 4));
                    }
                }
                processRequestResult(requestInfo, error);
            } else if (!this.reuploadingCdn) {
                clearOperaion(requestInfo);
                this.reuploadingCdn = true;
                TL_upload_cdnFileReuploadNeeded res2 = (TL_upload_cdnFileReuploadNeeded) response;
                TL_upload_reuploadCdnFile req = new TL_upload_reuploadCdnFile();
                req.file_token = this.cdnToken;
                req.request_token = res2.request_token;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new FileLoadOperation$$Lambda$12(this, requestInfo), null, null, 0, this.datacenterId, 1, true);
            }
        }
    }

    final /* synthetic */ void lambda$null$10$FileLoadOperation(RequestInfo requestInfo, TLObject response1, TL_error error1) {
        this.reuploadingCdn = false;
        if (error1 == null) {
            Vector vector = (Vector) response1;
            if (!vector.objects.isEmpty()) {
                if (this.cdnHashes == null) {
                    this.cdnHashes = new SparseArray();
                }
                for (int a1 = 0; a1 < vector.objects.size(); a1++) {
                    TL_fileHash hash = (TL_fileHash) vector.objects.get(a1);
                    this.cdnHashes.put(hash.offset, hash);
                }
            }
            startDownloadRequest();
        } else if (error1.text.equals("FILE_TOKEN_INVALID") || error1.text.equals("REQUEST_TOKEN_INVALID")) {
            this.isCdn = false;
            clearOperaion(requestInfo);
            startDownloadRequest();
        } else {
            onFail(false, 0);
        }
    }

    public void setDelegate(FileLoadOperationDelegate delegate) {
        this.delegate = delegate;
    }
}
