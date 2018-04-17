package org.telegram.messenger;

import android.util.SparseArray;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import org.telegram.messenger.exoplayer2.util.MimeTypes;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.InputFileLocation;
import org.telegram.tgnet.TLRPC.TL_document;
import org.telegram.tgnet.TLRPC.TL_documentEncrypted;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_fileEncryptedLocation;
import org.telegram.tgnet.TLRPC.TL_fileHash;
import org.telegram.tgnet.TLRPC.TL_fileLocation;
import org.telegram.tgnet.TLRPC.TL_inputDocumentFileLocation;
import org.telegram.tgnet.TLRPC.TL_inputEncryptedFileLocation;
import org.telegram.tgnet.TLRPC.TL_inputFileLocation;
import org.telegram.tgnet.TLRPC.TL_inputWebFileLocation;
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
import org.telegram.tgnet.TLRPC.TL_webDocument;
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
    private int datacenter_id;
    private ArrayList<RequestInfo> delayedRequestInfos;
    private FileLoadOperationDelegate delegate;
    private volatile int downloadedBytes;
    private boolean encryptFile;
    private byte[] encryptIv;
    private byte[] encryptKey;
    private String ext;
    private RandomAccessFile fileOutputStream;
    private RandomAccessFile filePartsStream;
    private RandomAccessFile fileReadStream;
    private RandomAccessFile fiv;
    private boolean isCdn;
    private boolean isForceRequest;
    private byte[] iv;
    private byte[] key;
    private InputFileLocation location;
    private ArrayList<Range> notCheckedCdnRanges;
    private ArrayList<Range> notLoadedBytesRanges;
    private volatile ArrayList<Range> notLoadedBytesRangesCopy;
    private ArrayList<Range> notRequestedBytesRanges;
    private volatile boolean paused;
    private int renameRetryCount;
    private ArrayList<RequestInfo> requestInfos;
    private int requestedBytesCount;
    private boolean requestingCdnOffsets;
    private int requestsCount;
    private boolean reuploadingCdn;
    private boolean started;
    private volatile int state;
    private File storePath;
    private ArrayList<FileStreamLoadOperation> streamListeners;
    private int streamStartOffset;
    private File tempPath;
    private int totalBytesCount;
    private TL_inputWebFileLocation webLocation;

    /* renamed from: org.telegram.messenger.FileLoadOperation$4 */
    class C01494 implements Runnable {
        C01494() {
        }

        public void run() {
            FileLoadOperation.this.paused = true;
        }
    }

    /* renamed from: org.telegram.messenger.FileLoadOperation$6 */
    class C01516 implements Runnable {
        C01516() {
        }

        public void run() {
            FileLoadOperation.this.startDownloadRequest();
        }
    }

    /* renamed from: org.telegram.messenger.FileLoadOperation$7 */
    class C01527 implements Runnable {
        C01527() {
        }

        public void run() {
            if (FileLoadOperation.this.totalBytesCount == 0 || FileLoadOperation.this.downloadedBytes != FileLoadOperation.this.totalBytesCount) {
                FileLoadOperation.this.startDownloadRequest();
                return;
            }
            try {
                FileLoadOperation.this.onFinishLoadingFile(false);
            } catch (Exception e) {
                FileLoadOperation.this.onFail(true, 0);
            }
        }
    }

    /* renamed from: org.telegram.messenger.FileLoadOperation$8 */
    class C01538 implements Runnable {
        C01538() {
        }

        public void run() {
            if (FileLoadOperation.this.state != 3) {
                if (FileLoadOperation.this.state != 2) {
                    if (FileLoadOperation.this.requestInfos != null) {
                        for (int a = 0; a < FileLoadOperation.this.requestInfos.size(); a++) {
                            RequestInfo requestInfo = (RequestInfo) FileLoadOperation.this.requestInfos.get(a);
                            if (requestInfo.requestToken != 0) {
                                ConnectionsManager.getInstance(FileLoadOperation.this.currentAccount).cancelRequest(requestInfo.requestToken, true);
                            }
                        }
                    }
                    FileLoadOperation.this.onFail(false, 1);
                }
            }
        }
    }

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

    private static class RequestInfo {
        private int offset;
        private int requestToken;
        private TL_upload_file response;
        private TL_upload_cdnFile responseCdn;
        private TL_upload_webFile responseWeb;

        private RequestInfo() {
        }
    }

    public FileLoadOperation(FileLocation photoLocation, String extension, int size) {
        this.state = 0;
        if (photoLocation instanceof TL_fileEncryptedLocation) {
            this.location = new TL_inputEncryptedFileLocation();
            this.location.id = photoLocation.volume_id;
            this.location.volume_id = photoLocation.volume_id;
            this.location.access_hash = photoLocation.secret;
            this.location.local_id = photoLocation.local_id;
            this.iv = new byte[32];
            System.arraycopy(photoLocation.iv, 0, this.iv, 0, this.iv.length);
            this.key = photoLocation.key;
            this.datacenter_id = photoLocation.dc_id;
        } else if (photoLocation instanceof TL_fileLocation) {
            this.location = new TL_inputFileLocation();
            this.location.volume_id = photoLocation.volume_id;
            this.location.secret = photoLocation.secret;
            this.location.local_id = photoLocation.local_id;
            this.datacenter_id = photoLocation.dc_id;
            this.allowDisordererFileSave = true;
        }
        this.currentType = 16777216;
        this.totalBytesCount = size;
        this.ext = extension != null ? extension : "jpg";
    }

    public FileLoadOperation(TL_webDocument webDocument) {
        this.state = 0;
        this.webLocation = new TL_inputWebFileLocation();
        this.webLocation.url = webDocument.url;
        this.webLocation.access_hash = webDocument.access_hash;
        this.totalBytesCount = webDocument.size;
        this.datacenter_id = webDocument.dc_id;
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

    public FileLoadOperation(Document documentLocation) {
        this.state = 0;
        try {
            int idx;
            String str;
            if (documentLocation instanceof TL_documentEncrypted) {
                this.location = new TL_inputEncryptedFileLocation();
                this.location.id = documentLocation.id;
                this.location.access_hash = documentLocation.access_hash;
                this.datacenter_id = documentLocation.dc_id;
                this.iv = new byte[32];
                System.arraycopy(documentLocation.iv, 0, this.iv, 0, this.iv.length);
                this.key = documentLocation.key;
            } else if (documentLocation instanceof TL_document) {
                this.location = new TL_inputDocumentFileLocation();
                this.location.id = documentLocation.id;
                this.location.access_hash = documentLocation.access_hash;
                this.datacenter_id = documentLocation.dc_id;
                this.allowDisordererFileSave = true;
            }
            this.totalBytesCount = documentLocation.size;
            if (!(this.key == null || this.totalBytesCount % 16 == 0)) {
                this.bytesCountPadding = 16 - (this.totalBytesCount % 16);
                this.totalBytesCount += this.bytesCountPadding;
            }
            this.ext = FileLoader.getDocumentFileName(documentLocation);
            int i = -1;
            if (this.ext != null) {
                int lastIndexOf = this.ext.lastIndexOf(46);
                idx = lastIndexOf;
                if (lastIndexOf != -1) {
                    this.ext = this.ext.substring(idx);
                    if ("audio/ogg".equals(documentLocation.mime_type)) {
                        this.currentType = ConnectionsManager.FileTypeAudio;
                    } else if (MimeTypes.VIDEO_MP4.equals(documentLocation.mime_type)) {
                        this.currentType = ConnectionsManager.FileTypeFile;
                    } else {
                        this.currentType = ConnectionsManager.FileTypeVideo;
                    }
                    if (this.ext.length() <= 1) {
                        if (documentLocation.mime_type != null) {
                            str = documentLocation.mime_type;
                            idx = str.hashCode();
                            if (idx == 187091926) {
                                if (idx != NUM) {
                                    if (str.equals(MimeTypes.VIDEO_MP4)) {
                                        i = 0;
                                    }
                                }
                            } else if (str.equals("audio/ogg")) {
                                i = true;
                            }
                            switch (i) {
                                case 0:
                                    this.ext = ".mp4";
                                    break;
                                case 1:
                                    this.ext = ".ogg";
                                    break;
                                default:
                                    this.ext = TtmlNode.ANONYMOUS_REGION_ID;
                                    break;
                            }
                        }
                        this.ext = TtmlNode.ANONYMOUS_REGION_ID;
                    }
                }
            }
            this.ext = TtmlNode.ANONYMOUS_REGION_ID;
            if ("audio/ogg".equals(documentLocation.mime_type)) {
                this.currentType = ConnectionsManager.FileTypeAudio;
            } else if (MimeTypes.VIDEO_MP4.equals(documentLocation.mime_type)) {
                this.currentType = ConnectionsManager.FileTypeFile;
            } else {
                this.currentType = ConnectionsManager.FileTypeVideo;
            }
            if (this.ext.length() <= 1) {
                if (documentLocation.mime_type != null) {
                    str = documentLocation.mime_type;
                    idx = str.hashCode();
                    if (idx == 187091926) {
                        if (str.equals("audio/ogg")) {
                            i = true;
                        }
                    } else if (idx != NUM) {
                        if (str.equals(MimeTypes.VIDEO_MP4)) {
                            i = 0;
                        }
                    }
                    switch (i) {
                        case 0:
                            this.ext = ".mp4";
                            break;
                        case 1:
                            this.ext = ".ogg";
                            break;
                        default:
                            this.ext = TtmlNode.ANONYMOUS_REGION_ID;
                            break;
                    }
                }
                this.ext = TtmlNode.ANONYMOUS_REGION_ID;
            }
        } catch (Throwable e) {
            FileLog.m3e(e);
            onFail(true, 0);
        }
    }

    public void setEncryptFile(boolean value) {
        this.encryptFile = value;
        if (this.encryptFile) {
            this.allowDisordererFileSave = false;
        }
    }

    public void setForceRequest(boolean forceRequest) {
        this.isForceRequest = forceRequest;
    }

    public boolean isForceRequest() {
        return this.isForceRequest;
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
        if (ranges != null) {
            if (end >= start) {
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
    }

    private void addPart(ArrayList<Range> ranges, int start, int end, boolean save) {
        if (ranges != null) {
            if (end >= start) {
                int a;
                Range range;
                boolean modified = false;
                int count = ranges.size();
                int a2 = 0;
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
                if (save) {
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
                            FileLog.m3e(e);
                        }
                        if (this.streamListeners != null) {
                            count = this.streamListeners.size();
                            while (a2 < count) {
                                ((FileStreamLoadOperation) this.streamListeners.get(a2)).newDataAvailable();
                                a2++;
                            }
                        }
                    } else if (BuildVars.LOGS_ENABLED) {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(this.cacheFileFinal);
                        stringBuilder.append(" downloaded duplicate file part ");
                        stringBuilder.append(start);
                        stringBuilder.append(" - ");
                        stringBuilder.append(end);
                        FileLog.m1e(stringBuilder.toString());
                    }
                }
            }
        }
    }

    protected File getCurrentFile() {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final File[] result = new File[1];
        Utilities.stageQueue.postRunnable(new Runnable() {
            public void run() {
                if (FileLoadOperation.this.state == 3) {
                    result[0] = FileLoadOperation.this.cacheFileFinal;
                } else {
                    result[0] = FileLoadOperation.this.cacheFileTemp;
                }
                countDownLatch.countDown();
            }
        });
        try {
            countDownLatch.await();
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
        return result[0];
    }

    private int getDownloadedLengthFromOffsetInternal(ArrayList<Range> ranges, int offset, int length) {
        if (!(ranges == null || this.state == 3)) {
            if (!ranges.isEmpty()) {
                int count = ranges.size();
                int availableLength = length;
                Range minRange = null;
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
            }
        }
        if (this.downloadedBytes == 0) {
            return length;
        }
        return Math.min(length, Math.max(this.downloadedBytes - offset, 0));
    }

    protected float getDownloadedLengthFromOffset(float progress) {
        ArrayList<Range> ranges = this.notLoadedBytesRangesCopy;
        if (this.totalBytesCount != 0) {
            if (ranges != null) {
                return (((float) getDownloadedLengthFromOffsetInternal(ranges, (int) (((float) this.totalBytesCount) * progress), this.totalBytesCount)) / ((float) this.totalBytesCount)) + progress;
            }
        }
        return 0.0f;
    }

    protected int getDownloadedLengthFromOffset(int offset, int length) {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        int[] result = new int[1];
        final int[] iArr = result;
        final int i = offset;
        final int i2 = length;
        final CountDownLatch countDownLatch2 = countDownLatch;
        Utilities.stageQueue.postRunnable(new Runnable() {
            public void run() {
                iArr[0] = FileLoadOperation.this.getDownloadedLengthFromOffsetInternal(FileLoadOperation.this.notLoadedBytesRanges, i, i2);
                countDownLatch2.countDown();
            }
        });
        try {
            countDownLatch.await();
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
        return result[0];
    }

    public String getFileName() {
        if (this.location != null) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(this.location.volume_id);
            stringBuilder.append("_");
            stringBuilder.append(this.location.local_id);
            stringBuilder.append(".");
            stringBuilder.append(this.ext);
            return stringBuilder.toString();
        }
        stringBuilder = new StringBuilder();
        stringBuilder.append(Utilities.MD5(this.webLocation.url));
        stringBuilder.append(".");
        stringBuilder.append(this.ext);
        return stringBuilder.toString();
    }

    protected void removeStreamListener(final FileStreamLoadOperation operation) {
        Utilities.stageQueue.postRunnable(new Runnable() {
            public void run() {
                if (FileLoadOperation.this.streamListeners != null) {
                    FileLoadOperation.this.streamListeners.remove(operation);
                }
            }
        });
    }

    private void copytNotLoadedRanges() {
        if (this.notLoadedBytesRanges != null) {
            this.notLoadedBytesRangesCopy = new ArrayList(this.notLoadedBytesRanges);
        }
    }

    public void pause() {
        if (this.state == 1) {
            Utilities.stageQueue.postRunnable(new C01494());
        }
    }

    public boolean start() {
        return start(null, 0);
    }

    public boolean start(FileStreamLoadOperation stream, int streamOffset) {
        Throwable e;
        long totalDownloadedLen;
        int size;
        Range alreadyStarted;
        StringBuilder stringBuilder;
        final FileStreamLoadOperation fileStreamLoadOperation = stream;
        final int i = streamOffset;
        if (this.currentDownloadChunkSize == 0) {
            r1.currentDownloadChunkSize = r1.totalBytesCount >= 1048576 ? 131072 : 32768;
            int i2 = r1.totalBytesCount;
            r1.currentMaxDownloadRequests = 4;
        }
        final boolean alreadyStarted2 = r1.state != 0;
        boolean wasPaused = r1.paused;
        r1.paused = false;
        if (fileStreamLoadOperation != null) {
            Utilities.stageQueue.postRunnable(new Runnable() {
                public void run() {
                    if (FileLoadOperation.this.streamListeners == null) {
                        FileLoadOperation.this.streamListeners = new ArrayList();
                    }
                    FileLoadOperation.this.streamStartOffset = (i / FileLoadOperation.this.currentDownloadChunkSize) * FileLoadOperation.this.currentDownloadChunkSize;
                    FileLoadOperation.this.streamListeners.add(fileStreamLoadOperation);
                    if (alreadyStarted2) {
                        FileLoadOperation.this.startDownloadRequest();
                    }
                }
            });
        } else if (wasPaused && alreadyStarted2) {
            Utilities.stageQueue.postRunnable(new C01516());
        }
        if (alreadyStarted2) {
            return wasPaused;
        }
        if (r1.location == null && r1.webLocation == null) {
            onFail(true, 0);
            return false;
        }
        String fileNameTemp;
        String fileNameFinal;
        r1.streamStartOffset = (i / r1.currentDownloadChunkSize) * r1.currentDownloadChunkSize;
        if (r1.allowDisordererFileSave && r1.totalBytesCount > 0 && r1.totalBytesCount > r1.currentDownloadChunkSize) {
            r1.notLoadedBytesRanges = new ArrayList();
            r1.notRequestedBytesRanges = new ArrayList();
        }
        String fileNameParts = null;
        String fileNameIv = null;
        StringBuilder stringBuilder2;
        if (r1.webLocation != null) {
            String md5 = Utilities.MD5(r1.webLocation.url);
            if (r1.encryptFile) {
                fileNameTemp = new StringBuilder();
                fileNameTemp.append(md5);
                fileNameTemp.append(".temp.enc");
                fileNameTemp = fileNameTemp.toString();
                fileNameFinal = new StringBuilder();
                fileNameFinal.append(md5);
                fileNameFinal.append(".");
                fileNameFinal.append(r1.ext);
                fileNameFinal.append(".enc");
                fileNameFinal = fileNameFinal.toString();
                if (r1.key != null) {
                    StringBuilder stringBuilder3 = new StringBuilder();
                    stringBuilder3.append(md5);
                    stringBuilder3.append(".iv.enc");
                    fileNameIv = stringBuilder3.toString();
                }
            } else {
                stringBuilder2 = new StringBuilder();
                stringBuilder2.append(md5);
                stringBuilder2.append(".temp");
                fileNameTemp = stringBuilder2.toString();
                stringBuilder2 = new StringBuilder();
                stringBuilder2.append(md5);
                stringBuilder2.append(".");
                stringBuilder2.append(r1.ext);
                fileNameFinal = stringBuilder2.toString();
                if (r1.key != null) {
                    stringBuilder2 = new StringBuilder();
                    stringBuilder2.append(md5);
                    stringBuilder2.append(".iv");
                    fileNameIv = stringBuilder2.toString();
                }
            }
        } else if (r1.location.volume_id == 0 || r1.location.local_id == 0) {
            boolean z;
            if (r1.datacenter_id == 0) {
                z = false;
                alreadyStarted2 = true;
            } else if (r1.location.id == 0) {
                boolean z2 = alreadyStarted2;
                z = false;
                alreadyStarted2 = true;
            } else if (r1.encryptFile) {
                stringBuilder2 = new StringBuilder();
                stringBuilder2.append(r1.datacenter_id);
                stringBuilder2.append("_");
                stringBuilder2.append(r1.location.id);
                stringBuilder2.append(".temp.enc");
                fileNameTemp = stringBuilder2.toString();
                stringBuilder2 = new StringBuilder();
                stringBuilder2.append(r1.datacenter_id);
                stringBuilder2.append("_");
                stringBuilder2.append(r1.location.id);
                stringBuilder2.append(r1.ext);
                stringBuilder2.append(".enc");
                fileNameFinal = stringBuilder2.toString();
                if (r1.key != null) {
                    stringBuilder2 = new StringBuilder();
                    stringBuilder2.append(r1.datacenter_id);
                    stringBuilder2.append("_");
                    stringBuilder2.append(r1.location.id);
                    stringBuilder2.append(".iv.enc");
                    fileNameIv = stringBuilder2.toString();
                }
            } else {
                stringBuilder2 = new StringBuilder();
                stringBuilder2.append(r1.datacenter_id);
                stringBuilder2.append("_");
                stringBuilder2.append(r1.location.id);
                stringBuilder2.append(".temp");
                fileNameTemp = stringBuilder2.toString();
                stringBuilder2 = new StringBuilder();
                stringBuilder2.append(r1.datacenter_id);
                stringBuilder2.append("_");
                stringBuilder2.append(r1.location.id);
                stringBuilder2.append(r1.ext);
                fileNameFinal = stringBuilder2.toString();
                if (r1.key != null) {
                    stringBuilder2 = new StringBuilder();
                    stringBuilder2.append(r1.datacenter_id);
                    stringBuilder2.append("_");
                    stringBuilder2.append(r1.location.id);
                    stringBuilder2.append(".iv");
                    fileNameIv = stringBuilder2.toString();
                }
                if (r1.notLoadedBytesRanges != null) {
                    stringBuilder2 = new StringBuilder();
                    stringBuilder2.append(r1.datacenter_id);
                    stringBuilder2.append("_");
                    stringBuilder2.append(r1.location.id);
                    stringBuilder2.append(".pt");
                    fileNameParts = stringBuilder2.toString();
                }
            }
            onFail(alreadyStarted2, z);
            return z;
        } else {
            if (!(r1.datacenter_id == Integer.MIN_VALUE || r1.location.volume_id == -2147483648L)) {
                if (r1.datacenter_id != 0) {
                    if (r1.encryptFile) {
                        stringBuilder2 = new StringBuilder();
                        stringBuilder2.append(r1.location.volume_id);
                        stringBuilder2.append("_");
                        stringBuilder2.append(r1.location.local_id);
                        stringBuilder2.append(".temp.enc");
                        fileNameTemp = stringBuilder2.toString();
                        stringBuilder2 = new StringBuilder();
                        stringBuilder2.append(r1.location.volume_id);
                        stringBuilder2.append("_");
                        stringBuilder2.append(r1.location.local_id);
                        stringBuilder2.append(".");
                        stringBuilder2.append(r1.ext);
                        stringBuilder2.append(".enc");
                        fileNameFinal = stringBuilder2.toString();
                        if (r1.key != null) {
                            stringBuilder2 = new StringBuilder();
                            stringBuilder2.append(r1.location.volume_id);
                            stringBuilder2.append("_");
                            stringBuilder2.append(r1.location.local_id);
                            stringBuilder2.append(".iv.enc");
                            fileNameIv = stringBuilder2.toString();
                        }
                    } else {
                        stringBuilder2 = new StringBuilder();
                        stringBuilder2.append(r1.location.volume_id);
                        stringBuilder2.append("_");
                        stringBuilder2.append(r1.location.local_id);
                        stringBuilder2.append(".temp");
                        fileNameTemp = stringBuilder2.toString();
                        stringBuilder2 = new StringBuilder();
                        stringBuilder2.append(r1.location.volume_id);
                        stringBuilder2.append("_");
                        stringBuilder2.append(r1.location.local_id);
                        stringBuilder2.append(".");
                        stringBuilder2.append(r1.ext);
                        fileNameFinal = stringBuilder2.toString();
                        if (r1.key != null) {
                            String fileNameIv2 = new StringBuilder();
                            fileNameIv2.append(r1.location.volume_id);
                            fileNameIv2.append("_");
                            fileNameIv2.append(r1.location.local_id);
                            fileNameIv2.append(".iv");
                            fileNameIv = fileNameIv2.toString();
                        }
                        if (r1.notLoadedBytesRanges != null) {
                            stringBuilder2 = new StringBuilder();
                            stringBuilder2.append(r1.location.volume_id);
                            stringBuilder2.append("_");
                            stringBuilder2.append(r1.location.local_id);
                            stringBuilder2.append(".pt");
                            fileNameParts = stringBuilder2.toString();
                        }
                    }
                }
            }
            onFail(true, 0);
            return false;
        }
        r1.requestInfos = new ArrayList(r1.currentMaxDownloadRequests);
        r1.delayedRequestInfos = new ArrayList(r1.currentMaxDownloadRequests - 1);
        r1.state = 1;
        r1.cacheFileFinal = new File(r1.storePath, fileNameFinal);
        boolean finalFileExist = r1.cacheFileFinal.exists();
        if (!(!finalFileExist || r1.totalBytesCount == 0 || ((long) r1.totalBytesCount) == r1.cacheFileFinal.length())) {
            r1.cacheFileFinal.delete();
            finalFileExist = false;
        }
        if (finalFileExist) {
            r1.started = true;
            try {
                onFinishLoadingFile(false);
                alreadyStarted2 = true;
            } catch (Exception e2) {
                alreadyStarted2 = true;
                onFail(true, 0);
            }
        } else {
            long len;
            r1.cacheFileTemp = new File(r1.tempPath, fileNameTemp);
            boolean newKeyGenerated = false;
            if (r1.encryptFile) {
                File internalCacheDir = FileLoader.getInternalCacheDir();
                StringBuilder stringBuilder4 = new StringBuilder();
                stringBuilder4.append(fileNameFinal);
                stringBuilder4.append(".key");
                File keyFile = new File(internalCacheDir, stringBuilder4.toString());
                try {
                    RandomAccessFile file = new RandomAccessFile(keyFile, "rws");
                    len = keyFile.length();
                    r1.encryptKey = new byte[32];
                    r1.encryptIv = new byte[16];
                    if (len <= 0 || len % 48 != 0) {
                        Utilities.random.nextBytes(r1.encryptKey);
                        Utilities.random.nextBytes(r1.encryptIv);
                        file.write(r1.encryptKey);
                        file.write(r1.encryptIv);
                        newKeyGenerated = true;
                    } else {
                        file.read(r1.encryptKey, 0, 32);
                        file.read(r1.encryptIv, 0, 16);
                    }
                    try {
                        file.getChannel().close();
                    } catch (Throwable e3) {
                        FileLog.m3e(e3);
                    }
                    file.close();
                } catch (Throwable e32) {
                    FileLog.m3e(e32);
                }
            }
            if (fileNameParts != null) {
                r1.cacheFileParts = new File(r1.tempPath, fileNameParts);
                try {
                    r1.filePartsStream = new RandomAccessFile(r1.cacheFileParts, "rws");
                    long len2 = r1.filePartsStream.length();
                    if (len2 % 8 == 4) {
                        len = len2 - 4;
                        int count = r1.filePartsStream.readInt();
                        if (((long) count) <= len / 2) {
                            int a = 0;
                            while (a < count) {
                                int start = r1.filePartsStream.readInt();
                                int end = r1.filePartsStream.readInt();
                                z2 = alreadyStarted2;
                                try {
                                    r1.notLoadedBytesRanges.add(new Range(start, end));
                                    r1.notRequestedBytesRanges.add(new Range(start, end));
                                    a++;
                                    alreadyStarted2 = z2;
                                    i = streamOffset;
                                } catch (Throwable e322) {
                                    e = e322;
                                }
                            }
                        }
                    }
                } catch (Throwable e3222) {
                    z2 = alreadyStarted2;
                    e = e3222;
                    FileLog.m3e(e);
                    if (r1.cacheFileTemp.exists()) {
                        r1.notLoadedBytesRanges.add(new Range(0, r1.totalBytesCount));
                        r1.notRequestedBytesRanges.add(new Range(0, r1.totalBytesCount));
                    } else if (newKeyGenerated) {
                        r1.cacheFileTemp.delete();
                    } else {
                        totalDownloadedLen = r1.cacheFileTemp.length();
                        if (fileNameIv != null) {
                        }
                        alreadyStarted2 = (((int) r1.cacheFileTemp.length()) / r1.currentDownloadChunkSize) * r1.currentDownloadChunkSize;
                        r1.downloadedBytes = alreadyStarted2;
                        r1.requestedBytesCount = alreadyStarted2;
                        r1.notLoadedBytesRanges.add(new Range(r1.downloadedBytes, r1.totalBytesCount));
                        r1.notRequestedBytesRanges.add(new Range(r1.downloadedBytes, r1.totalBytesCount));
                    }
                    if (r1.notLoadedBytesRanges != null) {
                        r1.downloadedBytes = r1.totalBytesCount;
                        size = r1.notLoadedBytesRanges.size();
                        for (i = 0; i < size; i++) {
                            alreadyStarted = (Range) r1.notLoadedBytesRanges.get(i);
                            r1.downloadedBytes -= alreadyStarted.end - alreadyStarted.start;
                        }
                    }
                    if (BuildVars.LOGS_ENABLED) {
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("start loading file to temp = ");
                        stringBuilder.append(r1.cacheFileTemp);
                        stringBuilder.append(" final = ");
                        stringBuilder.append(r1.cacheFileFinal);
                        FileLog.m0d(stringBuilder.toString());
                    }
                    if (fileNameIv != null) {
                        r1.cacheIvTemp = new File(r1.tempPath, fileNameIv);
                        try {
                            r1.fiv = new RandomAccessFile(r1.cacheIvTemp, "rws");
                            totalDownloadedLen = r1.cacheIvTemp.length();
                            if (totalDownloadedLen > 0) {
                            }
                            r1.downloadedBytes = 0;
                            r1.requestedBytesCount = 0;
                        } catch (Throwable e32222) {
                            FileLog.m3e(e32222);
                            r1.downloadedBytes = 0;
                            r1.requestedBytesCount = 0;
                        }
                    }
                    copytNotLoadedRanges();
                    r1.delegate.didChangedLoadProgress(r1, Math.min(1.0f, ((float) r1.downloadedBytes) / ((float) r1.totalBytesCount)));
                    r1.fileOutputStream = new RandomAccessFile(r1.cacheFileTemp, "rws");
                    if (r1.downloadedBytes != 0) {
                        r1.fileOutputStream.seek((long) r1.downloadedBytes);
                    }
                    if (r1.fileOutputStream != null) {
                        r1.started = true;
                        Utilities.stageQueue.postRunnable(new C01527());
                        alreadyStarted2 = true;
                        return alreadyStarted2;
                    }
                    onFail(true, 0);
                    return false;
                }
            }
            if (r1.cacheFileTemp.exists()) {
                if (r1.notLoadedBytesRanges != null && r1.notLoadedBytesRanges.isEmpty()) {
                    r1.notLoadedBytesRanges.add(new Range(0, r1.totalBytesCount));
                    r1.notRequestedBytesRanges.add(new Range(0, r1.totalBytesCount));
                }
            } else if (newKeyGenerated) {
                r1.cacheFileTemp.delete();
            } else {
                totalDownloadedLen = r1.cacheFileTemp.length();
                if (fileNameIv != null || totalDownloadedLen % ((long) r1.currentDownloadChunkSize) == 0) {
                    alreadyStarted2 = (((int) r1.cacheFileTemp.length()) / r1.currentDownloadChunkSize) * r1.currentDownloadChunkSize;
                    r1.downloadedBytes = alreadyStarted2;
                    r1.requestedBytesCount = alreadyStarted2;
                } else {
                    r1.downloadedBytes = 0;
                    r1.requestedBytesCount = 0;
                }
                if (r1.notLoadedBytesRanges && r1.notLoadedBytesRanges.isEmpty()) {
                    r1.notLoadedBytesRanges.add(new Range(r1.downloadedBytes, r1.totalBytesCount));
                    r1.notRequestedBytesRanges.add(new Range(r1.downloadedBytes, r1.totalBytesCount));
                }
            }
            if (r1.notLoadedBytesRanges != null) {
                r1.downloadedBytes = r1.totalBytesCount;
                size = r1.notLoadedBytesRanges.size();
                for (i = 0; i < size; i++) {
                    alreadyStarted = (Range) r1.notLoadedBytesRanges.get(i);
                    r1.downloadedBytes -= alreadyStarted.end - alreadyStarted.start;
                }
            }
            if (BuildVars.LOGS_ENABLED) {
                stringBuilder = new StringBuilder();
                stringBuilder.append("start loading file to temp = ");
                stringBuilder.append(r1.cacheFileTemp);
                stringBuilder.append(" final = ");
                stringBuilder.append(r1.cacheFileFinal);
                FileLog.m0d(stringBuilder.toString());
            }
            if (fileNameIv != null) {
                r1.cacheIvTemp = new File(r1.tempPath, fileNameIv);
                r1.fiv = new RandomAccessFile(r1.cacheIvTemp, "rws");
                if (!(r1.downloadedBytes == 0 || newKeyGenerated)) {
                    totalDownloadedLen = r1.cacheIvTemp.length();
                    if (totalDownloadedLen > 0 || totalDownloadedLen % 32 != 0) {
                        r1.downloadedBytes = 0;
                        r1.requestedBytesCount = 0;
                    } else {
                        r1.fiv.read(r1.iv, 0, 32);
                    }
                }
            }
            if (r1.downloadedBytes != 0 && r1.totalBytesCount > 0) {
                copytNotLoadedRanges();
                r1.delegate.didChangedLoadProgress(r1, Math.min(1.0f, ((float) r1.downloadedBytes) / ((float) r1.totalBytesCount)));
            }
            try {
                r1.fileOutputStream = new RandomAccessFile(r1.cacheFileTemp, "rws");
                if (r1.downloadedBytes != 0) {
                    r1.fileOutputStream.seek((long) r1.downloadedBytes);
                }
            } catch (Throwable e322222) {
                FileLog.m3e(e322222);
            }
            if (r1.fileOutputStream != null) {
                onFail(true, 0);
                return false;
            }
            r1.started = true;
            Utilities.stageQueue.postRunnable(new C01527());
            alreadyStarted2 = true;
        }
        return alreadyStarted2;
    }

    public boolean isPaused() {
        return this.paused;
    }

    public void cancel() {
        Utilities.stageQueue.postRunnable(new C01538());
    }

    private void cleanup() {
        try {
            if (this.fileOutputStream != null) {
                try {
                    this.fileOutputStream.getChannel().close();
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
                this.fileOutputStream.close();
                this.fileOutputStream = null;
            }
        } catch (Throwable e2) {
            FileLog.m3e(e2);
        }
        try {
            if (this.fileReadStream != null) {
                try {
                    this.fileReadStream.getChannel().close();
                } catch (Throwable e22) {
                    FileLog.m3e(e22);
                }
                this.fileReadStream.close();
                this.fileReadStream = null;
            }
        } catch (Throwable e222) {
            FileLog.m3e(e222);
        }
        try {
            if (this.filePartsStream != null) {
                try {
                    this.filePartsStream.getChannel().close();
                } catch (Throwable e2222) {
                    FileLog.m3e(e2222);
                }
                this.filePartsStream.close();
                this.filePartsStream = null;
            }
        } catch (Throwable e22222) {
            FileLog.m3e(e22222);
        }
        try {
            if (this.fiv != null) {
                this.fiv.close();
                this.fiv = null;
            }
        } catch (Throwable e3) {
            FileLog.m3e(e3);
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

    private void onFinishLoadingFile(final boolean increment) throws Exception {
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
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("unable to rename temp = ");
                    stringBuilder.append(this.cacheFileTemp);
                    stringBuilder.append(" to final = ");
                    stringBuilder.append(this.cacheFileFinal);
                    stringBuilder.append(" retry = ");
                    stringBuilder.append(this.renameRetryCount);
                    FileLog.m1e(stringBuilder.toString());
                }
                this.renameRetryCount++;
                if (this.renameRetryCount < 3) {
                    this.state = 1;
                    Utilities.stageQueue.postRunnable(new Runnable() {
                        public void run() {
                            try {
                                FileLoadOperation.this.onFinishLoadingFile(increment);
                            } catch (Exception e) {
                                FileLoadOperation.this.onFail(false, 0);
                            }
                        }
                    }, 200);
                    return;
                }
                this.cacheFileFinal = this.cacheFileTemp;
            }
            if (BuildVars.LOGS_ENABLED) {
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append("finished downloading file to ");
                stringBuilder2.append(this.cacheFileFinal);
                FileLog.m0d(stringBuilder2.toString());
            }
            this.delegate.didFinishLoadingFile(this, this.cacheFileFinal);
            if (increment) {
                if (this.currentType == ConnectionsManager.FileTypeAudio) {
                    StatsController.getInstance(this.currentAccount).incrementReceivedItemsCount(ConnectionsManager.getCurrentNetworkType(), 3, 1);
                } else if (this.currentType == ConnectionsManager.FileTypeVideo) {
                    StatsController.getInstance(this.currentAccount).incrementReceivedItemsCount(ConnectionsManager.getCurrentNetworkType(), 2, 1);
                } else if (this.currentType == 16777216) {
                    StatsController.getInstance(this.currentAccount).incrementReceivedItemsCount(ConnectionsManager.getCurrentNetworkType(), 4, 1);
                } else if (this.currentType == ConnectionsManager.FileTypeFile) {
                    StatsController.getInstance(this.currentAccount).incrementReceivedItemsCount(ConnectionsManager.getCurrentNetworkType(), 5, 1);
                }
            }
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
            TLObject req = new TL_upload_getCdnFileHashes();
            req.file_token = this.cdnToken;
            req.offset = offset;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                public void run(TLObject response, TL_error error) {
                    if (error != null) {
                        FileLoadOperation.this.onFail(false, 0);
                        return;
                    }
                    int a;
                    FileLoadOperation.this.requestingCdnOffsets = false;
                    Vector vector = (Vector) response;
                    if (!vector.objects.isEmpty()) {
                        if (FileLoadOperation.this.cdnHashes == null) {
                            FileLoadOperation.this.cdnHashes = new SparseArray();
                        }
                        for (a = 0; a < vector.objects.size(); a++) {
                            TL_fileHash hash = (TL_fileHash) vector.objects.get(a);
                            FileLoadOperation.this.cdnHashes.put(hash.offset, hash);
                        }
                    }
                    a = 0;
                    while (a < FileLoadOperation.this.delayedRequestInfos.size()) {
                        RequestInfo delayedRequestInfo = (RequestInfo) FileLoadOperation.this.delayedRequestInfos.get(a);
                        if (FileLoadOperation.this.notLoadedBytesRanges == null) {
                            if (FileLoadOperation.this.downloadedBytes != delayedRequestInfo.offset) {
                                a++;
                            }
                        }
                        FileLoadOperation.this.delayedRequestInfos.remove(a);
                        if (!FileLoadOperation.this.processRequestResult(delayedRequestInfo, null)) {
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
            }, null, null, 0, this.datacenter_id, 1, true);
        }
    }

    private boolean processRequestResult(RequestInfo requestInfo, TL_error error) {
        TL_error tL_error = error;
        if (this.state != 1) {
            return false;
        }
        boolean finishedDownloading;
        r1.requestInfos.remove(requestInfo);
        if (tL_error == null) {
            try {
                if (r1.notLoadedBytesRanges != null || r1.downloadedBytes == requestInfo.offset) {
                    NativeByteBuffer bytes;
                    NativeByteBuffer nativeByteBuffer;
                    int currentBytesSize;
                    int fileOffset;
                    int offset;
                    int offset2;
                    FileChannel channel;
                    int size;
                    boolean checked;
                    int a;
                    Range range;
                    int fileOffset2;
                    int availableSize;
                    FileChannel fileChannel;
                    RequestInfo delayedRequestInfo;
                    if (requestInfo.response != null) {
                        bytes = requestInfo.response.bytes;
                    } else if (requestInfo.responseWeb != null) {
                        bytes = requestInfo.responseWeb.bytes;
                    } else if (requestInfo.responseCdn != null) {
                        bytes = requestInfo.responseCdn.bytes;
                    } else {
                        bytes = null;
                        if (bytes != null) {
                            nativeByteBuffer = bytes;
                        } else if (bytes.limit() != 0) {
                            nativeByteBuffer = bytes;
                        } else {
                            currentBytesSize = bytes.limit();
                            if (r1.isCdn) {
                                fileOffset = (requestInfo.offset / 131072) * 131072;
                                if ((r1.cdnHashes == null ? (TL_fileHash) r1.cdnHashes.get(fileOffset) : null) == null) {
                                    delayRequestInfo(requestInfo);
                                    requestFileOffsets(fileOffset);
                                    return true;
                                }
                            }
                            if (requestInfo.responseCdn != null) {
                                offset = requestInfo.offset / 16;
                                r1.cdnIv[15] = (byte) (offset & 255);
                                r1.cdnIv[14] = (byte) ((offset >> 8) & 255);
                                r1.cdnIv[13] = (byte) ((offset >> 16) & 255);
                                r1.cdnIv[12] = (byte) ((offset >> 24) & 255);
                                Utilities.aesCtrDecryption(bytes.buffer, r1.cdnKey, r1.cdnIv, 0, bytes.limit());
                            }
                            r1.downloadedBytes += currentBytesSize;
                            if (r1.totalBytesCount <= 0) {
                                finishedDownloading = r1.downloadedBytes < r1.totalBytesCount;
                            } else {
                                if (currentBytesSize == r1.currentDownloadChunkSize) {
                                    if (r1.totalBytesCount == r1.downloadedBytes || r1.downloadedBytes % r1.currentDownloadChunkSize != 0) {
                                        if (r1.totalBytesCount > 0) {
                                            if (r1.totalBytesCount <= r1.downloadedBytes) {
                                            }
                                        }
                                    }
                                    finishedDownloading = false;
                                }
                                finishedDownloading = true;
                            }
                            if (r1.key != null) {
                                Utilities.aesIgeEncryption(bytes.buffer, r1.key, r1.iv, false, true, 0, bytes.limit());
                                if (finishedDownloading && r1.bytesCountPadding != 0) {
                                    bytes.limit(bytes.limit() - r1.bytesCountPadding);
                                }
                            }
                            if (r1.encryptFile) {
                                offset2 = requestInfo.offset / 16;
                                r1.encryptIv[15] = (byte) (offset2 & 255);
                                r1.encryptIv[14] = (byte) ((offset2 >> 8) & 255);
                                r1.encryptIv[13] = (byte) ((offset2 >> 16) & 255);
                                r1.encryptIv[12] = (byte) ((offset2 >> 24) & 255);
                                Utilities.aesCtrDecryption(bytes.buffer, r1.encryptKey, r1.encryptIv, 0, bytes.limit());
                            }
                            if (r1.notLoadedBytesRanges != null) {
                                r1.fileOutputStream.seek((long) requestInfo.offset);
                            }
                            channel = r1.fileOutputStream.getChannel();
                            channel.write(bytes.buffer);
                            addPart(r1.notLoadedBytesRanges, requestInfo.offset, requestInfo.offset + currentBytesSize, true);
                            if (r1.isCdn) {
                                offset = requestInfo.offset / 131072;
                                size = r1.notCheckedCdnRanges.size();
                                checked = true;
                                for (a = 0; a < size; a++) {
                                    range = (Range) r1.notCheckedCdnRanges.get(a);
                                    if (range.start > offset && offset <= range.end) {
                                        checked = false;
                                        break;
                                    }
                                }
                                if (!checked) {
                                    fileOffset2 = offset * 131072;
                                    availableSize = getDownloadedLengthFromOffsetInternal(r1.notLoadedBytesRanges, fileOffset2, 131072);
                                    if (availableSize != 0) {
                                        if (availableSize != 131072 && (r1.totalBytesCount <= 0 || availableSize != r1.totalBytesCount - fileOffset2)) {
                                            if (r1.totalBytesCount <= 0 || !finishedDownloading) {
                                                fileChannel = channel;
                                                nativeByteBuffer = bytes;
                                                if (r1.fiv != null) {
                                                    r1.fiv.seek(0);
                                                    r1.fiv.write(r1.iv);
                                                }
                                                if (r1.totalBytesCount > 0 && r1.state == 1) {
                                                    copytNotLoadedRanges();
                                                    r1.delegate.didChangedLoadProgress(r1, Math.min(1.0f, ((float) r1.downloadedBytes) / ((float) r1.totalBytesCount)));
                                                }
                                                offset2 = 0;
                                                while (offset2 < r1.delayedRequestInfos.size()) {
                                                    delayedRequestInfo = (RequestInfo) r1.delayedRequestInfos.get(offset2);
                                                    if (r1.notLoadedBytesRanges == null) {
                                                        if (r1.downloadedBytes != delayedRequestInfo.offset) {
                                                            offset2++;
                                                        }
                                                    }
                                                    r1.delayedRequestInfos.remove(offset2);
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
                                                }
                                                if (finishedDownloading) {
                                                    startDownloadRequest();
                                                } else {
                                                    onFinishLoadingFile(true);
                                                }
                                            }
                                        }
                                        TL_fileHash hash = (TL_fileHash) r1.cdnHashes.get(fileOffset2);
                                        if (r1.fileReadStream == null) {
                                            r1.cdnCheckBytes = new byte[131072];
                                            r1.fileReadStream = new RandomAccessFile(r1.cacheFileTemp, "r");
                                        }
                                        r1.fileReadStream.seek((long) fileOffset2);
                                        r1.fileReadStream.readFully(r1.cdnCheckBytes, 0, availableSize);
                                        if (Arrays.equals(Utilities.computeSHA256(r1.cdnCheckBytes, 0, availableSize), hash.hash)) {
                                            r1.cdnHashes.remove(fileOffset2);
                                            addPart(r1.notCheckedCdnRanges, offset, offset + 1, false);
                                            if (r1.fiv != null) {
                                                r1.fiv.seek(0);
                                                r1.fiv.write(r1.iv);
                                            }
                                            copytNotLoadedRanges();
                                            r1.delegate.didChangedLoadProgress(r1, Math.min(1.0f, ((float) r1.downloadedBytes) / ((float) r1.totalBytesCount)));
                                            offset2 = 0;
                                            while (offset2 < r1.delayedRequestInfos.size()) {
                                                delayedRequestInfo = (RequestInfo) r1.delayedRequestInfos.get(offset2);
                                                if (r1.notLoadedBytesRanges == null) {
                                                    if (r1.downloadedBytes != delayedRequestInfo.offset) {
                                                        offset2++;
                                                    }
                                                }
                                                r1.delayedRequestInfos.remove(offset2);
                                                if (processRequestResult(delayedRequestInfo, null)) {
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
                                                    onFinishLoadingFile(true);
                                                } else {
                                                    startDownloadRequest();
                                                }
                                            }
                                            if (finishedDownloading) {
                                                startDownloadRequest();
                                            } else {
                                                onFinishLoadingFile(true);
                                            }
                                        } else {
                                            if (!BuildVars.LOGS_ENABLED) {
                                            } else if (r1.location != null) {
                                                r5 = new StringBuilder();
                                                r5.append("invalid cdn hash ");
                                                r5.append(r1.location);
                                                r5.append(" id = ");
                                                r5.append(r1.location.id);
                                                r5.append(" local_id = ");
                                                r5.append(r1.location.local_id);
                                                r5.append(" access_hash = ");
                                                r5.append(r1.location.access_hash);
                                                r5.append(" volume_id = ");
                                                r5.append(r1.location.volume_id);
                                                r5.append(" secret = ");
                                                r5.append(r1.location.secret);
                                                FileLog.m1e(r5.toString());
                                            } else {
                                                if (r1.webLocation != null) {
                                                    r5 = new StringBuilder();
                                                    r5.append("invalid cdn hash  ");
                                                    r5.append(r1.webLocation);
                                                    r5.append(" id = ");
                                                    r5.append(r1.webLocation.url);
                                                    r5.append(" access_hash = ");
                                                    r5.append(r1.webLocation.access_hash);
                                                    FileLog.m1e(r5.toString());
                                                }
                                            }
                                            onFail(false, 0);
                                            r1.cacheFileTemp.delete();
                                            return false;
                                        }
                                    }
                                }
                            }
                            nativeByteBuffer = bytes;
                            if (r1.fiv != null) {
                                r1.fiv.seek(0);
                                r1.fiv.write(r1.iv);
                            }
                            copytNotLoadedRanges();
                            r1.delegate.didChangedLoadProgress(r1, Math.min(1.0f, ((float) r1.downloadedBytes) / ((float) r1.totalBytesCount)));
                            offset2 = 0;
                            while (offset2 < r1.delayedRequestInfos.size()) {
                                delayedRequestInfo = (RequestInfo) r1.delayedRequestInfos.get(offset2);
                                if (r1.notLoadedBytesRanges == null) {
                                    if (r1.downloadedBytes != delayedRequestInfo.offset) {
                                        offset2++;
                                    }
                                }
                                r1.delayedRequestInfos.remove(offset2);
                                if (processRequestResult(delayedRequestInfo, null)) {
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
                                    onFinishLoadingFile(true);
                                } else {
                                    startDownloadRequest();
                                }
                            }
                            if (finishedDownloading) {
                                startDownloadRequest();
                            } else {
                                onFinishLoadingFile(true);
                            }
                        }
                        onFinishLoadingFile(true);
                        return false;
                    }
                    if (bytes != null) {
                        nativeByteBuffer = bytes;
                    } else if (bytes.limit() != 0) {
                        currentBytesSize = bytes.limit();
                        if (r1.isCdn) {
                            fileOffset = (requestInfo.offset / 131072) * 131072;
                            if (r1.cdnHashes == null) {
                            }
                            if ((r1.cdnHashes == null ? (TL_fileHash) r1.cdnHashes.get(fileOffset) : null) == null) {
                                delayRequestInfo(requestInfo);
                                requestFileOffsets(fileOffset);
                                return true;
                            }
                        }
                        if (requestInfo.responseCdn != null) {
                            offset = requestInfo.offset / 16;
                            r1.cdnIv[15] = (byte) (offset & 255);
                            r1.cdnIv[14] = (byte) ((offset >> 8) & 255);
                            r1.cdnIv[13] = (byte) ((offset >> 16) & 255);
                            r1.cdnIv[12] = (byte) ((offset >> 24) & 255);
                            Utilities.aesCtrDecryption(bytes.buffer, r1.cdnKey, r1.cdnIv, 0, bytes.limit());
                        }
                        r1.downloadedBytes += currentBytesSize;
                        if (r1.totalBytesCount <= 0) {
                            if (currentBytesSize == r1.currentDownloadChunkSize) {
                                if (r1.totalBytesCount > 0) {
                                    if (r1.totalBytesCount <= r1.downloadedBytes) {
                                    }
                                    finishedDownloading = false;
                                }
                            }
                            finishedDownloading = true;
                        } else if (r1.downloadedBytes < r1.totalBytesCount) {
                        }
                        if (r1.key != null) {
                            Utilities.aesIgeEncryption(bytes.buffer, r1.key, r1.iv, false, true, 0, bytes.limit());
                            bytes.limit(bytes.limit() - r1.bytesCountPadding);
                        }
                        if (r1.encryptFile) {
                            offset2 = requestInfo.offset / 16;
                            r1.encryptIv[15] = (byte) (offset2 & 255);
                            r1.encryptIv[14] = (byte) ((offset2 >> 8) & 255);
                            r1.encryptIv[13] = (byte) ((offset2 >> 16) & 255);
                            r1.encryptIv[12] = (byte) ((offset2 >> 24) & 255);
                            Utilities.aesCtrDecryption(bytes.buffer, r1.encryptKey, r1.encryptIv, 0, bytes.limit());
                        }
                        if (r1.notLoadedBytesRanges != null) {
                            r1.fileOutputStream.seek((long) requestInfo.offset);
                        }
                        channel = r1.fileOutputStream.getChannel();
                        channel.write(bytes.buffer);
                        addPart(r1.notLoadedBytesRanges, requestInfo.offset, requestInfo.offset + currentBytesSize, true);
                        if (r1.isCdn) {
                            offset = requestInfo.offset / 131072;
                            size = r1.notCheckedCdnRanges.size();
                            checked = true;
                            for (a = 0; a < size; a++) {
                                range = (Range) r1.notCheckedCdnRanges.get(a);
                                if (range.start > offset) {
                                }
                            }
                            if (checked) {
                                fileOffset2 = offset * 131072;
                                availableSize = getDownloadedLengthFromOffsetInternal(r1.notLoadedBytesRanges, fileOffset2, 131072);
                                if (availableSize != 0) {
                                    if (r1.totalBytesCount <= 0) {
                                    }
                                    fileChannel = channel;
                                    nativeByteBuffer = bytes;
                                    if (r1.fiv != null) {
                                        r1.fiv.seek(0);
                                        r1.fiv.write(r1.iv);
                                    }
                                    copytNotLoadedRanges();
                                    r1.delegate.didChangedLoadProgress(r1, Math.min(1.0f, ((float) r1.downloadedBytes) / ((float) r1.totalBytesCount)));
                                    offset2 = 0;
                                    while (offset2 < r1.delayedRequestInfos.size()) {
                                        delayedRequestInfo = (RequestInfo) r1.delayedRequestInfos.get(offset2);
                                        if (r1.notLoadedBytesRanges == null) {
                                            if (r1.downloadedBytes != delayedRequestInfo.offset) {
                                                offset2++;
                                            }
                                        }
                                        r1.delayedRequestInfos.remove(offset2);
                                        if (processRequestResult(delayedRequestInfo, null)) {
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
                                            onFinishLoadingFile(true);
                                        } else {
                                            startDownloadRequest();
                                        }
                                    }
                                    if (finishedDownloading) {
                                        startDownloadRequest();
                                    } else {
                                        onFinishLoadingFile(true);
                                    }
                                }
                            }
                        }
                        nativeByteBuffer = bytes;
                        if (r1.fiv != null) {
                            r1.fiv.seek(0);
                            r1.fiv.write(r1.iv);
                        }
                        copytNotLoadedRanges();
                        r1.delegate.didChangedLoadProgress(r1, Math.min(1.0f, ((float) r1.downloadedBytes) / ((float) r1.totalBytesCount)));
                        offset2 = 0;
                        while (offset2 < r1.delayedRequestInfos.size()) {
                            delayedRequestInfo = (RequestInfo) r1.delayedRequestInfos.get(offset2);
                            if (r1.notLoadedBytesRanges == null) {
                                if (r1.downloadedBytes != delayedRequestInfo.offset) {
                                    offset2++;
                                }
                            }
                            r1.delayedRequestInfos.remove(offset2);
                            if (processRequestResult(delayedRequestInfo, null)) {
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
                                onFinishLoadingFile(true);
                            } else {
                                startDownloadRequest();
                            }
                        }
                        if (finishedDownloading) {
                            startDownloadRequest();
                        } else {
                            onFinishLoadingFile(true);
                        }
                    } else {
                        nativeByteBuffer = bytes;
                    }
                    onFinishLoadingFile(true);
                    return false;
                }
                delayRequestInfo(requestInfo);
                return false;
            } catch (Throwable e) {
                Throwable e2 = e;
                onFail(false, 0);
                FileLog.m3e(e2);
            }
        } else if (tL_error.text.contains("FILE_MIGRATE_")) {
            Integer val;
            Scanner scanner = new Scanner(tL_error.text.replace("FILE_MIGRATE_", TtmlNode.ANONYMOUS_REGION_ID));
            scanner.useDelimiter(TtmlNode.ANONYMOUS_REGION_ID);
            try {
                val = Integer.valueOf(scanner.nextInt());
            } catch (Exception e3) {
                val = null;
            }
            if (val == null) {
                onFail(false, 0);
            } else {
                r1.datacenter_id = val.intValue();
                r1.downloadedBytes = 0;
                r1.requestedBytesCount = 0;
                startDownloadRequest();
            }
        } else {
            boolean z;
            if (!tL_error.text.contains("OFFSET_INVALID")) {
                z = false;
                if (tL_error.text.contains("RETRY_LIMIT")) {
                    onFail(false, 2);
                } else {
                    if (BuildVars.LOGS_ENABLED) {
                        StringBuilder stringBuilder;
                        if (r1.location != null) {
                            stringBuilder = new StringBuilder();
                            stringBuilder.append(TtmlNode.ANONYMOUS_REGION_ID);
                            stringBuilder.append(r1.location);
                            stringBuilder.append(" id = ");
                            stringBuilder.append(r1.location.id);
                            stringBuilder.append(" local_id = ");
                            stringBuilder.append(r1.location.local_id);
                            stringBuilder.append(" access_hash = ");
                            stringBuilder.append(r1.location.access_hash);
                            stringBuilder.append(" volume_id = ");
                            stringBuilder.append(r1.location.volume_id);
                            stringBuilder.append(" secret = ");
                            stringBuilder.append(r1.location.secret);
                            FileLog.m1e(stringBuilder.toString());
                        } else if (r1.webLocation != null) {
                            stringBuilder = new StringBuilder();
                            stringBuilder.append(TtmlNode.ANONYMOUS_REGION_ID);
                            stringBuilder.append(r1.webLocation);
                            stringBuilder.append(" id = ");
                            stringBuilder.append(r1.webLocation.url);
                            stringBuilder.append(" access_hash = ");
                            stringBuilder.append(r1.webLocation.access_hash);
                            FileLog.m1e(stringBuilder.toString());
                        }
                    }
                    finishedDownloading = false;
                    onFail(false, 0);
                    return finishedDownloading;
                }
            } else if (r1.downloadedBytes % r1.currentDownloadChunkSize == 0) {
                try {
                    onFinishLoadingFile(true);
                } catch (Throwable e4) {
                    FileLog.m3e(e4);
                    z = false;
                    onFail(false, 0);
                }
            } else {
                z = false;
                onFail(false, 0);
            }
            finishedDownloading = z;
            return finishedDownloading;
        }
        finishedDownloading = false;
        return finishedDownloading;
    }

    private void onFail(boolean thread, final int reason) {
        cleanup();
        this.state = 2;
        if (thread) {
            Utilities.stageQueue.postRunnable(new Runnable() {
                public void run() {
                    FileLoadOperation.this.delegate.didFailedLoadingFile(FileLoadOperation.this, reason);
                }
            });
        } else {
            this.delegate.didFailedLoadingFile(this, reason);
        }
    }

    private void clearOperaion(RequestInfo currentInfo) {
        int a;
        int minOffset = ConnectionsManager.DEFAULT_DATACENTER_ID;
        for (a = 0; a < this.requestInfos.size(); a++) {
            RequestInfo info = (RequestInfo) this.requestInfos.get(a);
            minOffset = Math.min(info.offset, minOffset);
            removePart(this.notRequestedBytesRanges, info.offset, info.offset + this.currentDownloadChunkSize);
            if (currentInfo != info) {
                if (info.requestToken != 0) {
                    ConnectionsManager.getInstance(this.currentAccount).cancelRequest(info.requestToken, true);
                }
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

    private void startDownloadRequest() {
        if (!this.paused && r0.state == 1) {
            if (r0.requestInfos.size() + r0.delayedRequestInfos.size() < r0.currentMaxDownloadRequests) {
                int count = 1;
                boolean z = false;
                if (r0.totalBytesCount > 0) {
                    count = Math.max(0, r0.currentMaxDownloadRequests - r0.requestInfos.size());
                }
                int a = 0;
                while (a < count) {
                    int size;
                    int b;
                    if (r0.notRequestedBytesRanges != null) {
                        size = r0.notRequestedBytesRanges.size();
                        int minStreamStart = ConnectionsManager.DEFAULT_DATACENTER_ID;
                        int minStart = ConnectionsManager.DEFAULT_DATACENTER_ID;
                        for (b = z; b < size; b++) {
                            Range range = (Range) r0.notRequestedBytesRanges.get(b);
                            if (r0.streamStartOffset != 0) {
                                if (range.start <= r0.streamStartOffset && range.end > r0.streamStartOffset) {
                                    minStreamStart = r0.streamStartOffset;
                                    minStart = ConnectionsManager.DEFAULT_DATACENTER_ID;
                                    break;
                                } else if (r0.streamStartOffset < range.start && range.start < minStreamStart) {
                                    minStreamStart = range.start;
                                }
                            }
                            minStart = Math.min(minStart, range.start);
                        }
                        if (minStreamStart == ConnectionsManager.DEFAULT_DATACENTER_ID) {
                            if (minStart == ConnectionsManager.DEFAULT_DATACENTER_ID) {
                                break;
                            }
                            b = minStart;
                        } else {
                            b = minStreamStart;
                        }
                        size = b;
                    } else {
                        size = r0.requestedBytesCount;
                    }
                    if (r0.notRequestedBytesRanges != null) {
                        addPart(r0.notRequestedBytesRanges, size, r0.currentDownloadChunkSize + size, z);
                    }
                    if (r0.totalBytesCount > 0 && size >= r0.totalBytesCount) {
                        break;
                    }
                    boolean isLast;
                    int connectionType;
                    TLObject req;
                    TLObject request;
                    final RequestInfo requestInfo;
                    if (r0.totalBytesCount > 0 && a != count - 1) {
                        if (r0.totalBytesCount <= 0 || r0.currentDownloadChunkSize + size < r0.totalBytesCount) {
                            isLast = z;
                            connectionType = r0.requestsCount % 2 != 0 ? 2 : ConnectionsManager.ConnectionTypeDownload2;
                            b = (r0.isForceRequest ? 32 : z) | 2;
                            if (r0.isCdn) {
                                req = new TL_upload_getCdnFile();
                                req.file_token = r0.cdnToken;
                                req.offset = size;
                                req.limit = r0.currentDownloadChunkSize;
                                request = req;
                                b |= 1;
                            } else if (r0.webLocation == null) {
                                req = new TL_upload_getWebFile();
                                req.location = r0.webLocation;
                                req.offset = size;
                                req.limit = r0.currentDownloadChunkSize;
                                request = req;
                            } else {
                                request = new TL_upload_getFile();
                                request.location = r0.location;
                                request.offset = size;
                                request.limit = r0.currentDownloadChunkSize;
                            }
                            req = request;
                            r0.requestedBytesCount += r0.currentDownloadChunkSize;
                            requestInfo = new RequestInfo();
                            r0.requestInfos.add(requestInfo);
                            requestInfo.offset = size;
                            requestInfo.requestToken = ConnectionsManager.getInstance(r0.currentAccount).sendRequest(req, new RequestDelegate() {

                                /* renamed from: org.telegram.messenger.FileLoadOperation$12$1 */
                                class C17941 implements RequestDelegate {
                                    C17941() {
                                    }

                                    public void run(TLObject response, TL_error error) {
                                        int a = 0;
                                        FileLoadOperation.this.reuploadingCdn = false;
                                        if (error == null) {
                                            Vector vector = (Vector) response;
                                            if (!vector.objects.isEmpty()) {
                                                if (FileLoadOperation.this.cdnHashes == null) {
                                                    FileLoadOperation.this.cdnHashes = new SparseArray();
                                                }
                                                while (a < vector.objects.size()) {
                                                    TL_fileHash hash = (TL_fileHash) vector.objects.get(a);
                                                    FileLoadOperation.this.cdnHashes.put(hash.offset, hash);
                                                    a++;
                                                }
                                            }
                                            FileLoadOperation.this.startDownloadRequest();
                                            return;
                                        }
                                        if (!error.text.equals("FILE_TOKEN_INVALID")) {
                                            if (!error.text.equals("REQUEST_TOKEN_INVALID")) {
                                                FileLoadOperation.this.onFail(false, 0);
                                                return;
                                            }
                                        }
                                        FileLoadOperation.this.isCdn = false;
                                        FileLoadOperation.this.clearOperaion(requestInfo);
                                        FileLoadOperation.this.startDownloadRequest();
                                    }
                                }

                                public void run(TLObject response, TL_error error) {
                                    if (!FileLoadOperation.this.requestInfos.contains(requestInfo)) {
                                        return;
                                    }
                                    if (error != null && (req instanceof TL_upload_getCdnFile) && error.text.equals("FILE_TOKEN_INVALID")) {
                                        FileLoadOperation.this.isCdn = false;
                                        FileLoadOperation.this.clearOperaion(requestInfo);
                                        FileLoadOperation.this.startDownloadRequest();
                                        return;
                                    }
                                    if (response instanceof TL_upload_fileCdnRedirect) {
                                        TL_upload_fileCdnRedirect res = (TL_upload_fileCdnRedirect) response;
                                        if (!res.file_hashes.isEmpty()) {
                                            if (FileLoadOperation.this.cdnHashes == null) {
                                                FileLoadOperation.this.cdnHashes = new SparseArray();
                                            }
                                            for (int a = 0; a < res.file_hashes.size(); a++) {
                                                TL_fileHash hash = (TL_fileHash) res.file_hashes.get(a);
                                                FileLoadOperation.this.cdnHashes.put(hash.offset, hash);
                                            }
                                        }
                                        if (!(res.encryption_iv == null || res.encryption_key == null || res.encryption_iv.length != 16)) {
                                            if (res.encryption_key.length == 32) {
                                                FileLoadOperation.this.isCdn = true;
                                                if (FileLoadOperation.this.notCheckedCdnRanges == null) {
                                                    FileLoadOperation.this.notCheckedCdnRanges = new ArrayList();
                                                    FileLoadOperation.this.notCheckedCdnRanges.add(new Range(0, FileLoadOperation.maxCdnParts));
                                                }
                                                FileLoadOperation.this.cdnDatacenterId = res.dc_id;
                                                FileLoadOperation.this.cdnIv = res.encryption_iv;
                                                FileLoadOperation.this.cdnKey = res.encryption_key;
                                                FileLoadOperation.this.cdnToken = res.file_token;
                                                FileLoadOperation.this.clearOperaion(requestInfo);
                                                FileLoadOperation.this.startDownloadRequest();
                                            }
                                        }
                                        error = new TL_error();
                                        error.text = "bad redirect response";
                                        error.code = 400;
                                        FileLoadOperation.this.processRequestResult(requestInfo, error);
                                    } else if (!(response instanceof TL_upload_cdnFileReuploadNeeded)) {
                                        if (response instanceof TL_upload_file) {
                                            requestInfo.response = (TL_upload_file) response;
                                        } else if (response instanceof TL_upload_webFile) {
                                            requestInfo.responseWeb = (TL_upload_webFile) response;
                                            if (FileLoadOperation.this.totalBytesCount == 0 && requestInfo.responseWeb.size != 0) {
                                                FileLoadOperation.this.totalBytesCount = requestInfo.responseWeb.size;
                                            }
                                        } else {
                                            requestInfo.responseCdn = (TL_upload_cdnFile) response;
                                        }
                                        if (response != null) {
                                            if (FileLoadOperation.this.currentType == ConnectionsManager.FileTypeAudio) {
                                                StatsController.getInstance(FileLoadOperation.this.currentAccount).incrementReceivedBytesCount(response.networkType, 3, (long) (response.getObjectSize() + 4));
                                            } else if (FileLoadOperation.this.currentType == ConnectionsManager.FileTypeVideo) {
                                                StatsController.getInstance(FileLoadOperation.this.currentAccount).incrementReceivedBytesCount(response.networkType, 2, (long) (response.getObjectSize() + 4));
                                            } else if (FileLoadOperation.this.currentType == 16777216) {
                                                StatsController.getInstance(FileLoadOperation.this.currentAccount).incrementReceivedBytesCount(response.networkType, 4, (long) (response.getObjectSize() + 4));
                                            } else if (FileLoadOperation.this.currentType == ConnectionsManager.FileTypeFile) {
                                                StatsController.getInstance(FileLoadOperation.this.currentAccount).incrementReceivedBytesCount(response.networkType, 5, (long) (response.getObjectSize() + 4));
                                            }
                                        }
                                        FileLoadOperation.this.processRequestResult(requestInfo, error);
                                    } else if (!FileLoadOperation.this.reuploadingCdn) {
                                        FileLoadOperation.this.clearOperaion(requestInfo);
                                        FileLoadOperation.this.reuploadingCdn = true;
                                        TL_upload_cdnFileReuploadNeeded res2 = (TL_upload_cdnFileReuploadNeeded) response;
                                        TLObject req = new TL_upload_reuploadCdnFile();
                                        req.file_token = FileLoadOperation.this.cdnToken;
                                        req.request_token = res2.request_token;
                                        ConnectionsManager.getInstance(FileLoadOperation.this.currentAccount).sendRequest(req, new C17941(), null, null, 0, FileLoadOperation.this.datacenter_id, 1, true);
                                    }
                                }
                            }, null, null, b, r0.isCdn ? r0.cdnDatacenterId : r0.datacenter_id, connectionType, isLast);
                            r0.requestsCount++;
                            a++;
                            z = false;
                        }
                    }
                    isLast = true;
                    if (r0.requestsCount % 2 != 0) {
                    }
                    if (r0.isForceRequest) {
                    }
                    b = (r0.isForceRequest ? 32 : z) | 2;
                    if (r0.isCdn) {
                        req = new TL_upload_getCdnFile();
                        req.file_token = r0.cdnToken;
                        req.offset = size;
                        req.limit = r0.currentDownloadChunkSize;
                        request = req;
                        b |= 1;
                    } else if (r0.webLocation == null) {
                        request = new TL_upload_getFile();
                        request.location = r0.location;
                        request.offset = size;
                        request.limit = r0.currentDownloadChunkSize;
                    } else {
                        req = new TL_upload_getWebFile();
                        req.location = r0.webLocation;
                        req.offset = size;
                        req.limit = r0.currentDownloadChunkSize;
                        request = req;
                    }
                    req = request;
                    r0.requestedBytesCount += r0.currentDownloadChunkSize;
                    requestInfo = new RequestInfo();
                    r0.requestInfos.add(requestInfo);
                    requestInfo.offset = size;
                    if (r0.isCdn) {
                    }
                    requestInfo.requestToken = ConnectionsManager.getInstance(r0.currentAccount).sendRequest(req, /* anonymous class already generated */, null, null, b, r0.isCdn ? r0.cdnDatacenterId : r0.datacenter_id, connectionType, isLast);
                    r0.requestsCount++;
                    a++;
                    z = false;
                }
            }
        }
    }

    public void setDelegate(FileLoadOperationDelegate delegate) {
        this.delegate = delegate;
    }
}
