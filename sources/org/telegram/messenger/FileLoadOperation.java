package org.telegram.messenger;

import android.util.SparseArray;
import android.util.SparseIntArray;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.InputFileLocation;
import org.telegram.tgnet.TLRPC.InputWebFileLocation;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_fileEncryptedLocation;
import org.telegram.tgnet.TLRPC.TL_fileHash;
import org.telegram.tgnet.TLRPC.TL_fileLocation;
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
    private static final int preloadMaxBytes = 2097152;
    private static final int stateDownloading = 1;
    private static final int stateFailed = 2;
    private static final int stateFinished = 3;
    private static final int stateIdle = 0;
    private boolean allowDisordererFileSave;
    private int bytesCountPadding;
    private File cacheFileFinal;
    private File cacheFileParts;
    private File cacheFilePreload;
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
    private int foundMoovSize;
    private int initialDatacenterId;
    private boolean isCdn;
    private boolean isForceRequest;
    private boolean isPreloadVideoOperation;
    private byte[] iv;
    private byte[] key;
    private InputFileLocation location;
    private int moovFound;
    private int nextAtomOffset;
    private boolean nextPartWasPreloaded;
    private int nextPreloadDownloadOffset;
    private ArrayList<Range> notCheckedCdnRanges;
    private ArrayList<Range> notLoadedBytesRanges;
    private volatile ArrayList<Range> notLoadedBytesRangesCopy;
    private ArrayList<Range> notRequestedBytesRanges;
    private Object parentObject;
    private volatile boolean paused;
    private boolean preloadFinished;
    private int preloadNotRequestedBytesCount;
    private RandomAccessFile preloadStream;
    private int preloadStreamFileOffset;
    private byte[] preloadTempBuffer;
    private int preloadTempBufferCount;
    private SparseArray<PreloadRange> preloadedBytesRanges;
    private int priority;
    private int renameRetryCount;
    private ArrayList<RequestInfo> requestInfos;
    private int requestedBytesCount;
    private SparseIntArray requestedPreloadedBytesRanges;
    private boolean requestingCdnOffsets;
    protected boolean requestingReference;
    private int requestsCount;
    private boolean reuploadingCdn;
    private boolean started;
    private volatile int state;
    private File storePath;
    private ArrayList<FileLoadOperationStream> streamListeners;
    private int streamStartOffset;
    private boolean supportsPreloading;
    private File tempPath;
    private int totalBytesCount;
    private int totalPreloadedBytes;
    private WebFile webFile;
    private InputWebFileLocation webLocation;

    public interface FileLoadOperationDelegate {
        void didChangedLoadProgress(FileLoadOperation fileLoadOperation, float f);

        void didFailedLoadingFile(FileLoadOperation fileLoadOperation, int i);

        void didFinishLoadingFile(FileLoadOperation fileLoadOperation, File file);
    }

    private static class PreloadRange {
        private int fileOffset;
        private int length;
        private int start;

        private PreloadRange(int o, int s, int l) {
            this.fileOffset = o;
            this.start = s;
            this.length = l;
        }
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
        this.preloadTempBuffer = new byte[16];
        this.state = 0;
        this.parentObject = parent;
        int i;
        if (photoLocation instanceof TL_fileEncryptedLocation) {
            this.location = new TL_inputEncryptedFileLocation();
            this.location.id = photoLocation.volume_id;
            this.location.volume_id = photoLocation.volume_id;
            this.location.access_hash = photoLocation.secret;
            this.location.local_id = photoLocation.local_id;
            this.iv = new byte[32];
            System.arraycopy(photoLocation.iv, 0, this.iv, 0, this.iv.length);
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
        this.preloadTempBuffer = new byte[16];
        this.state = 0;
        this.location = new TL_inputSecureFileLocation();
        this.location.id = secureDocument.secureFile.id;
        this.location.access_hash = secureDocument.secureFile.access_hash;
        this.datacenterId = secureDocument.secureFile.dc_id;
        this.totalBytesCount = secureDocument.secureFile.size;
        this.allowDisordererFileSave = true;
        this.currentType = 67108864;
        this.ext = ".jpg";
    }

    public FileLoadOperation(int instance, WebFile webDocument) {
        this.preloadTempBuffer = new byte[16];
        this.state = 0;
        this.currentAccount = instance;
        this.webFile = webDocument;
        this.webLocation = webDocument.location;
        this.totalBytesCount = webDocument.size;
        int i = MessagesController.getInstance(this.currentAccount).webFileDatacenterId;
        this.datacenterId = i;
        this.initialDatacenterId = i;
        String defaultExt = FileLoader.getMimeTypePart(webDocument.mime_type);
        if (webDocument.mime_type.startsWith("image/")) {
            this.currentType = 16777216;
        } else if (webDocument.mime_type.equals("audio/ogg")) {
            this.currentType = 50331648;
        } else if (webDocument.mime_type.startsWith("video/")) {
            this.currentType = 33554432;
        } else {
            this.currentType = 67108864;
        }
        this.allowDisordererFileSave = true;
        this.ext = ImageLoader.getHttpUrlExtension(webDocument.url, defaultExt);
    }

    /* JADX WARNING: Removed duplicated region for block: B:36:0x00f9 A:{Catch:{ Exception -> 0x00e5 }} */
    /* JADX WARNING: Removed duplicated region for block: B:17:0x0085 A:{Catch:{ Exception -> 0x00e5 }} */
    /* JADX WARNING: Removed duplicated region for block: B:43:? A:{SYNTHETIC, RETURN, Catch:{ Exception -> 0x00e5 }} */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x0091 A:{Catch:{ Exception -> 0x00e5 }} */
    public FileLoadOperation(org.telegram.tgnet.TLRPC.Document r13, java.lang.Object r14) {
        /*
        r12 = this;
        r11 = 1;
        r10 = 0;
        r12.<init>();
        r5 = 16;
        r5 = new byte[r5];
        r12.preloadTempBuffer = r5;
        r12.state = r10;
        r12.parentObject = r14;	 Catch:{ Exception -> 0x00e5 }
        r5 = r13 instanceof org.telegram.tgnet.TLRPC.TL_documentEncrypted;	 Catch:{ Exception -> 0x00e5 }
        if (r5 == 0) goto L_0x009a;
    L_0x0013:
        r5 = new org.telegram.tgnet.TLRPC$TL_inputEncryptedFileLocation;	 Catch:{ Exception -> 0x00e5 }
        r5.<init>();	 Catch:{ Exception -> 0x00e5 }
        r12.location = r5;	 Catch:{ Exception -> 0x00e5 }
        r5 = r12.location;	 Catch:{ Exception -> 0x00e5 }
        r6 = r13.id;	 Catch:{ Exception -> 0x00e5 }
        r5.id = r6;	 Catch:{ Exception -> 0x00e5 }
        r5 = r12.location;	 Catch:{ Exception -> 0x00e5 }
        r6 = r13.access_hash;	 Catch:{ Exception -> 0x00e5 }
        r5.access_hash = r6;	 Catch:{ Exception -> 0x00e5 }
        r5 = r13.dc_id;	 Catch:{ Exception -> 0x00e5 }
        r12.datacenterId = r5;	 Catch:{ Exception -> 0x00e5 }
        r12.initialDatacenterId = r5;	 Catch:{ Exception -> 0x00e5 }
        r5 = 32;
        r5 = new byte[r5];	 Catch:{ Exception -> 0x00e5 }
        r12.iv = r5;	 Catch:{ Exception -> 0x00e5 }
        r5 = r13.iv;	 Catch:{ Exception -> 0x00e5 }
        r6 = 0;
        r7 = r12.iv;	 Catch:{ Exception -> 0x00e5 }
        r8 = 0;
        r9 = r12.iv;	 Catch:{ Exception -> 0x00e5 }
        r9 = r9.length;	 Catch:{ Exception -> 0x00e5 }
        java.lang.System.arraycopy(r5, r6, r7, r8, r9);	 Catch:{ Exception -> 0x00e5 }
        r5 = r13.key;	 Catch:{ Exception -> 0x00e5 }
        r12.key = r5;	 Catch:{ Exception -> 0x00e5 }
    L_0x0042:
        r5 = r13.size;	 Catch:{ Exception -> 0x00e5 }
        r12.totalBytesCount = r5;	 Catch:{ Exception -> 0x00e5 }
        r5 = r12.key;	 Catch:{ Exception -> 0x00e5 }
        if (r5 == 0) goto L_0x0060;
    L_0x004a:
        r4 = 0;
        r5 = r12.totalBytesCount;	 Catch:{ Exception -> 0x00e5 }
        r5 = r5 % 16;
        if (r5 == 0) goto L_0x0060;
    L_0x0051:
        r5 = r12.totalBytesCount;	 Catch:{ Exception -> 0x00e5 }
        r5 = r5 % 16;
        r5 = 16 - r5;
        r12.bytesCountPadding = r5;	 Catch:{ Exception -> 0x00e5 }
        r5 = r12.totalBytesCount;	 Catch:{ Exception -> 0x00e5 }
        r6 = r12.bytesCountPadding;	 Catch:{ Exception -> 0x00e5 }
        r5 = r5 + r6;
        r12.totalBytesCount = r5;	 Catch:{ Exception -> 0x00e5 }
    L_0x0060:
        r5 = org.telegram.messenger.FileLoader.getDocumentFileName(r13);	 Catch:{ Exception -> 0x00e5 }
        r12.ext = r5;	 Catch:{ Exception -> 0x00e5 }
        r5 = r12.ext;	 Catch:{ Exception -> 0x00e5 }
        if (r5 == 0) goto L_0x0075;
    L_0x006a:
        r5 = r12.ext;	 Catch:{ Exception -> 0x00e5 }
        r6 = 46;
        r3 = r5.lastIndexOf(r6);	 Catch:{ Exception -> 0x00e5 }
        r5 = -1;
        if (r3 != r5) goto L_0x00f0;
    L_0x0075:
        r5 = "";
        r12.ext = r5;	 Catch:{ Exception -> 0x00e5 }
    L_0x007a:
        r5 = "audio/ogg";
        r6 = r13.mime_type;	 Catch:{ Exception -> 0x00e5 }
        r5 = r5.equals(r6);	 Catch:{ Exception -> 0x00e5 }
        if (r5 == 0) goto L_0x00f9;
    L_0x0085:
        r5 = 50331648; // 0x3000000 float:3.761582E-37 double:2.4867138E-316;
        r12.currentType = r5;	 Catch:{ Exception -> 0x00e5 }
    L_0x0089:
        r5 = r12.ext;	 Catch:{ Exception -> 0x00e5 }
        r5 = r5.length();	 Catch:{ Exception -> 0x00e5 }
        if (r5 > r11) goto L_0x0099;
    L_0x0091:
        r5 = r13.mime_type;	 Catch:{ Exception -> 0x00e5 }
        r5 = org.telegram.messenger.FileLoader.getExtensionByMimeType(r5);	 Catch:{ Exception -> 0x00e5 }
        r12.ext = r5;	 Catch:{ Exception -> 0x00e5 }
    L_0x0099:
        return;
    L_0x009a:
        r5 = r13 instanceof org.telegram.tgnet.TLRPC.TL_document;	 Catch:{ Exception -> 0x00e5 }
        if (r5 == 0) goto L_0x0042;
    L_0x009e:
        r5 = new org.telegram.tgnet.TLRPC$TL_inputDocumentFileLocation;	 Catch:{ Exception -> 0x00e5 }
        r5.<init>();	 Catch:{ Exception -> 0x00e5 }
        r12.location = r5;	 Catch:{ Exception -> 0x00e5 }
        r5 = r12.location;	 Catch:{ Exception -> 0x00e5 }
        r6 = r13.id;	 Catch:{ Exception -> 0x00e5 }
        r5.id = r6;	 Catch:{ Exception -> 0x00e5 }
        r5 = r12.location;	 Catch:{ Exception -> 0x00e5 }
        r6 = r13.access_hash;	 Catch:{ Exception -> 0x00e5 }
        r5.access_hash = r6;	 Catch:{ Exception -> 0x00e5 }
        r5 = r12.location;	 Catch:{ Exception -> 0x00e5 }
        r6 = r13.file_reference;	 Catch:{ Exception -> 0x00e5 }
        r5.file_reference = r6;	 Catch:{ Exception -> 0x00e5 }
        r5 = r12.location;	 Catch:{ Exception -> 0x00e5 }
        r5 = r5.file_reference;	 Catch:{ Exception -> 0x00e5 }
        if (r5 != 0) goto L_0x00c4;
    L_0x00bd:
        r5 = r12.location;	 Catch:{ Exception -> 0x00e5 }
        r6 = 0;
        r6 = new byte[r6];	 Catch:{ Exception -> 0x00e5 }
        r5.file_reference = r6;	 Catch:{ Exception -> 0x00e5 }
    L_0x00c4:
        r5 = r13.dc_id;	 Catch:{ Exception -> 0x00e5 }
        r12.datacenterId = r5;	 Catch:{ Exception -> 0x00e5 }
        r12.initialDatacenterId = r5;	 Catch:{ Exception -> 0x00e5 }
        r5 = 1;
        r12.allowDisordererFileSave = r5;	 Catch:{ Exception -> 0x00e5 }
        r1 = 0;
        r5 = r13.attributes;	 Catch:{ Exception -> 0x00e5 }
        r0 = r5.size();	 Catch:{ Exception -> 0x00e5 }
    L_0x00d4:
        if (r1 >= r0) goto L_0x0042;
    L_0x00d6:
        r5 = r13.attributes;	 Catch:{ Exception -> 0x00e5 }
        r5 = r5.get(r1);	 Catch:{ Exception -> 0x00e5 }
        r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeVideo;	 Catch:{ Exception -> 0x00e5 }
        if (r5 == 0) goto L_0x00ed;
    L_0x00e0:
        r5 = 1;
        r12.supportsPreloading = r5;	 Catch:{ Exception -> 0x00e5 }
        goto L_0x0042;
    L_0x00e5:
        r2 = move-exception;
        org.telegram.messenger.FileLog.e(r2);
        r12.onFail(r11, r10);
        goto L_0x0099;
    L_0x00ed:
        r1 = r1 + 1;
        goto L_0x00d4;
    L_0x00f0:
        r5 = r12.ext;	 Catch:{ Exception -> 0x00e5 }
        r5 = r5.substring(r3);	 Catch:{ Exception -> 0x00e5 }
        r12.ext = r5;	 Catch:{ Exception -> 0x00e5 }
        goto L_0x007a;
    L_0x00f9:
        r5 = r13.mime_type;	 Catch:{ Exception -> 0x00e5 }
        r5 = org.telegram.messenger.FileLoader.isVideoMimeType(r5);	 Catch:{ Exception -> 0x00e5 }
        if (r5 == 0) goto L_0x0106;
    L_0x0101:
        r5 = 33554432; // 0x2000000 float:9.403955E-38 double:1.6578092E-316;
        r12.currentType = r5;	 Catch:{ Exception -> 0x00e5 }
        goto L_0x0089;
    L_0x0106:
        r5 = 67108864; // 0x4000000 float:1.5046328E-36 double:3.31561842E-316;
        r12.currentType = r5;	 Catch:{ Exception -> 0x00e5 }
        goto L_0x0089;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.FileLoadOperation.<init>(org.telegram.tgnet.TLRPC$Document, java.lang.Object):void");
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
        return this.started && !this.paused;
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
                    FileLog.e(e);
                }
                if (this.streamListeners != null) {
                    count = this.streamListeners.size();
                    for (a = 0; a < count; a++) {
                        ((FileLoadOperationStream) this.streamListeners.get(a)).newDataAvailable();
                    }
                }
            } else if (BuildVars.LOGS_ENABLED) {
                FileLog.e(this.cacheFileFinal + " downloaded duplicate file part " + start + " - " + end);
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
            FileLog.e(e);
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
        } catch (Exception e) {
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

    private void copyNotLoadedRanges() {
        if (this.notLoadedBytesRanges != null) {
            this.notLoadedBytesRangesCopy = new ArrayList(this.notLoadedBytesRanges);
        }
    }

    public void pause() {
        if (this.state == 1) {
            this.paused = true;
        }
    }

    public boolean start() {
        return start(null, 0);
    }

    public boolean start(FileLoadOperationStream stream, int streamOffset) {
        if (this.currentDownloadChunkSize == 0) {
            this.currentDownloadChunkSize = this.totalBytesCount >= 1048576 ? 131072 : 32768;
            this.currentMaxDownloadRequests = this.totalBytesCount >= 1048576 ? 4 : 4;
        }
        boolean alreadyStarted = this.state != 0;
        boolean wasPaused = this.paused;
        this.paused = false;
        if (stream != null) {
            Utilities.stageQueue.postRunnable(new FileLoadOperation$$Lambda$3(this, streamOffset, stream, alreadyStarted));
        } else if (wasPaused && alreadyStarted) {
            Utilities.stageQueue.postRunnable(new FileLoadOperation$$Lambda$4(this));
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
        String fileNamePreload = null;
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
            if (this.datacenterId == 0 || this.location.id == 0) {
                onFail(true, 0);
                return false;
            } else if (this.encryptFile) {
                fileNameTemp = this.datacenterId + "_" + this.location.id + ".temp.enc";
                fileNameFinal = this.datacenterId + "_" + this.location.id + this.ext + ".enc";
                if (this.key != null) {
                    fileNameIv = this.datacenterId + "_" + this.location.id + ".iv.enc";
                }
            } else {
                fileNameTemp = this.datacenterId + "_" + this.location.id + ".temp";
                fileNameFinal = this.datacenterId + "_" + this.location.id + this.ext;
                if (this.key != null) {
                    fileNameIv = this.datacenterId + "_" + this.location.id + ".iv";
                }
                if (this.notLoadedBytesRanges != null) {
                    fileNameParts = this.datacenterId + "_" + this.location.id + ".pt";
                }
                fileNamePreload = this.datacenterId + "_" + this.location.id + ".preload";
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
            fileNamePreload = this.location.volume_id + "_" + this.location.local_id + ".preload";
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
            int size;
            int a;
            this.cacheFileTemp = new File(this.tempPath, fileNameTemp);
            boolean newKeyGenerated = false;
            if (this.encryptFile) {
                File file = new File(FileLoader.getInternalCacheDir(), fileNameFinal + ".key");
                RandomAccessFile file2 = new RandomAccessFile(file, "rws");
                len = file.length();
                this.encryptKey = new byte[32];
                this.encryptIv = new byte[16];
                if (len <= 0 || len % 48 != 0) {
                    try {
                        Utilities.random.nextBytes(this.encryptKey);
                        Utilities.random.nextBytes(this.encryptIv);
                        file2.write(this.encryptKey);
                        file2.write(this.encryptIv);
                        newKeyGenerated = true;
                    } catch (Throwable e2) {
                        FileLog.e(e2);
                    }
                } else {
                    file2.read(this.encryptKey, 0, 32);
                    file2.read(this.encryptIv, 0, 16);
                }
                try {
                    file2.getChannel().close();
                } catch (Throwable e22) {
                    FileLog.e(e22);
                }
                file2.close();
            }
            boolean[] preloaded = new boolean[]{false};
            if (this.supportsPreloading && fileNamePreload != null) {
                this.cacheFilePreload = new File(this.tempPath, fileNamePreload);
                try {
                    this.preloadStream = new RandomAccessFile(this.cacheFilePreload, "rws");
                    len = this.preloadStream.length();
                    this.preloadStreamFileOffset = 1;
                    if (len - ((long) 0) > 1) {
                        preloaded[0] = this.preloadStream.readByte() != (byte) 0;
                        int readOffset = 0 + 1;
                        while (((long) readOffset) < len && len - ((long) readOffset) >= 4) {
                            int offset = this.preloadStream.readInt();
                            readOffset += 4;
                            if (len - ((long) readOffset) < 4 || offset < 0 || offset > this.totalBytesCount) {
                                break;
                            }
                            size = this.preloadStream.readInt();
                            readOffset += 4;
                            if (len - ((long) readOffset) < ((long) size) || size > this.currentDownloadChunkSize) {
                                break;
                            }
                            PreloadRange preloadRange = new PreloadRange(readOffset, offset, size);
                            readOffset += size;
                            this.preloadStream.seek((long) readOffset);
                            if (len - ((long) readOffset) < 12) {
                                break;
                            }
                            this.foundMoovSize = this.preloadStream.readInt();
                            if (this.foundMoovSize != 0) {
                                this.moovFound = this.nextPreloadDownloadOffset > this.totalBytesCount / 2 ? 2 : 1;
                                this.preloadNotRequestedBytesCount = this.foundMoovSize;
                            }
                            this.nextPreloadDownloadOffset = this.preloadStream.readInt();
                            this.nextAtomOffset = this.preloadStream.readInt();
                            readOffset += 12;
                            if (this.preloadedBytesRanges == null) {
                                this.preloadedBytesRanges = new SparseArray();
                            }
                            if (this.requestedPreloadedBytesRanges == null) {
                                this.requestedPreloadedBytesRanges = new SparseIntArray();
                            }
                            this.preloadedBytesRanges.put(offset, preloadRange);
                            this.requestedPreloadedBytesRanges.put(offset, 1);
                            this.totalPreloadedBytes += size;
                            this.preloadStreamFileOffset += size + 20;
                        }
                    }
                    this.preloadStream.seek((long) this.preloadStreamFileOffset);
                } catch (Throwable e222) {
                    FileLog.e(e222);
                }
                if (!this.isPreloadVideoOperation && this.preloadedBytesRanges == null) {
                    this.cacheFilePreload = null;
                    try {
                        if (this.preloadStream != null) {
                            try {
                                this.preloadStream.getChannel().close();
                            } catch (Throwable e2222) {
                                FileLog.e(e2222);
                            }
                            this.preloadStream.close();
                            this.preloadStream = null;
                        }
                    } catch (Throwable e22222) {
                        FileLog.e(e22222);
                    }
                }
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
                } catch (Throwable e222222) {
                    FileLog.e(e222222);
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
                size = this.notLoadedBytesRanges.size();
                for (a = 0; a < size; a++) {
                    Range range = (Range) this.notLoadedBytesRanges.get(a);
                    this.downloadedBytes -= range.end - range.start;
                }
                this.requestedBytesCount = this.downloadedBytes;
            }
            if (BuildVars.LOGS_ENABLED) {
                if (this.isPreloadVideoOperation) {
                    FileLog.d("start preloading file to temp = " + this.cacheFileTemp);
                } else {
                    FileLog.d("start loading file to temp = " + this.cacheFileTemp + " final = " + this.cacheFileFinal);
                }
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
                            this.fiv.read(this.iv, 0, 32);
                        }
                    }
                } catch (Throwable e2222222) {
                    FileLog.e(e2222222);
                    this.downloadedBytes = 0;
                    this.requestedBytesCount = 0;
                }
            }
            if (!(this.isPreloadVideoOperation || this.downloadedBytes == 0 || this.totalBytesCount <= 0)) {
                copyNotLoadedRanges();
                this.delegate.didChangedLoadProgress(this, Math.min(1.0f, ((float) this.downloadedBytes) / ((float) this.totalBytesCount)));
            }
            try {
                this.fileOutputStream = new RandomAccessFile(this.cacheFileTemp, "rws");
                if (this.downloadedBytes != 0) {
                    this.fileOutputStream.seek((long) this.downloadedBytes);
                }
            } catch (Throwable e22222222) {
                FileLog.e(e22222222);
            }
            if (this.fileOutputStream == null) {
                onFail(true, 0);
                return false;
            }
            this.started = true;
            Utilities.stageQueue.postRunnable(new FileLoadOperation$$Lambda$5(this, preloaded));
        }
        return true;
    }

    final /* synthetic */ void lambda$start$3$FileLoadOperation(int streamOffset, FileLoadOperationStream stream, boolean alreadyStarted) {
        if (this.streamListeners == null) {
            this.streamListeners = new ArrayList();
        }
        this.streamStartOffset = (streamOffset / this.currentDownloadChunkSize) * this.currentDownloadChunkSize;
        this.streamListeners.add(stream);
        if (alreadyStarted) {
            if (!(this.preloadedBytesRanges == null || getDownloadedLengthFromOffsetInternal(this.notLoadedBytesRanges, this.streamStartOffset, 1) != 0 || this.preloadedBytesRanges.get(this.streamStartOffset) == null)) {
                this.nextPartWasPreloaded = true;
            }
            startDownloadRequest();
            this.nextPartWasPreloaded = false;
        }
    }

    final /* synthetic */ void lambda$start$4$FileLoadOperation(boolean[] preloaded) {
        if (this.totalBytesCount == 0 || !((this.isPreloadVideoOperation && preloaded[0]) || this.downloadedBytes == this.totalBytesCount)) {
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

    public void setIsPreloadVideoOperation(boolean value) {
        if (this.isPreloadVideoOperation == value) {
            return;
        }
        if (value && this.totalBytesCount <= 2097152) {
            return;
        }
        if (value || !this.isPreloadVideoOperation) {
            this.isPreloadVideoOperation = value;
        } else if (this.state == 3) {
            this.isPreloadVideoOperation = value;
            this.state = 0;
            this.preloadFinished = false;
            start();
        } else if (this.state == 1) {
            Utilities.stageQueue.postRunnable(new FileLoadOperation$$Lambda$6(this, value));
        } else {
            this.isPreloadVideoOperation = value;
        }
    }

    final /* synthetic */ void lambda$setIsPreloadVideoOperation$5$FileLoadOperation(boolean value) {
        this.requestedBytesCount = 0;
        clearOperaion(null, true);
        this.isPreloadVideoOperation = value;
        startDownloadRequest();
    }

    public boolean isPreloadVideoOperation() {
        return this.isPreloadVideoOperation;
    }

    public boolean isPreloadFinished() {
        return this.preloadFinished;
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
                    FileLog.e(e);
                }
                this.fileOutputStream.close();
                this.fileOutputStream = null;
            }
        } catch (Throwable e2) {
            FileLog.e(e2);
        }
        try {
            if (this.preloadStream != null) {
                try {
                    this.preloadStream.getChannel().close();
                } catch (Throwable e22) {
                    FileLog.e(e22);
                }
                this.preloadStream.close();
                this.preloadStream = null;
            }
        } catch (Throwable e222) {
            FileLog.e(e222);
        }
        try {
            if (this.fileReadStream != null) {
                try {
                    this.fileReadStream.getChannel().close();
                } catch (Throwable e2222) {
                    FileLog.e(e2222);
                }
                this.fileReadStream.close();
                this.fileReadStream = null;
            }
        } catch (Throwable e22222) {
            FileLog.e(e22222);
        }
        try {
            if (this.filePartsStream != null) {
                try {
                    this.filePartsStream.getChannel().close();
                } catch (Throwable e222222) {
                    FileLog.e(e222222);
                }
                this.filePartsStream.close();
                this.filePartsStream = null;
            }
        } catch (Throwable e2222222) {
            FileLog.e(e2222222);
        }
        try {
            if (this.fiv != null) {
                this.fiv.close();
                this.fiv = null;
            }
        } catch (Throwable e22222222) {
            FileLog.e(e22222222);
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
            if (this.isPreloadVideoOperation) {
                this.preloadFinished = true;
                if (BuildVars.DEBUG_VERSION) {
                    FileLog.d("finished preloading file to " + this.cacheFileTemp + " loaded " + this.totalPreloadedBytes + " of " + this.totalBytesCount);
                }
            } else {
                if (this.cacheIvTemp != null) {
                    this.cacheIvTemp.delete();
                    this.cacheIvTemp = null;
                }
                if (this.cacheFileParts != null) {
                    this.cacheFileParts.delete();
                    this.cacheFileParts = null;
                }
                if (this.cacheFilePreload != null) {
                    this.cacheFilePreload.delete();
                    this.cacheFilePreload = null;
                }
                if (!(this.cacheFileTemp == null || this.cacheFileTemp.renameTo(this.cacheFileFinal))) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.e("unable to rename temp = " + this.cacheFileTemp + " to final = " + this.cacheFileFinal + " retry = " + this.renameRetryCount);
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
                    FileLog.d("finished downloading file to " + this.cacheFileFinal);
                }
                if (increment) {
                    if (this.currentType == 50331648) {
                        StatsController.getInstance(this.currentAccount).incrementReceivedItemsCount(ApplicationLoader.getCurrentNetworkType(), 3, 1);
                    } else if (this.currentType == 33554432) {
                        StatsController.getInstance(this.currentAccount).incrementReceivedItemsCount(ApplicationLoader.getCurrentNetworkType(), 2, 1);
                    } else if (this.currentType == 16777216) {
                        StatsController.getInstance(this.currentAccount).incrementReceivedItemsCount(ApplicationLoader.getCurrentNetworkType(), 4, 1);
                    } else if (this.currentType == 67108864) {
                        StatsController.getInstance(this.currentAccount).incrementReceivedItemsCount(ApplicationLoader.getCurrentNetworkType(), 5, 1);
                    }
                }
            }
            this.delegate.didFinishLoadingFile(this, this.cacheFileFinal);
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

    private int findNextPreloadDownloadOffset(int atomOffset, int partOffset, NativeByteBuffer partBuffer) {
        int partSize = partBuffer.limit();
        while (true) {
            int i;
            if (this.preloadTempBuffer != null) {
                i = 16;
            } else {
                i = 0;
            }
            if (atomOffset < partOffset - i || atomOffset >= partOffset + partSize) {
                return 0;
            }
            if (atomOffset >= (partOffset + partSize) - 16) {
                this.preloadTempBufferCount = (partOffset + partSize) - atomOffset;
                partBuffer.position(partBuffer.limit() - this.preloadTempBufferCount);
                partBuffer.readBytes(this.preloadTempBuffer, 0, this.preloadTempBufferCount, false);
                return partOffset + partSize;
            }
            if (this.preloadTempBufferCount != 0) {
                partBuffer.position(0);
                partBuffer.readBytes(this.preloadTempBuffer, this.preloadTempBufferCount, 16 - this.preloadTempBufferCount, false);
                this.preloadTempBufferCount = 0;
            } else {
                partBuffer.position(atomOffset - partOffset);
                partBuffer.readBytes(this.preloadTempBuffer, 0, 16, false);
            }
            int atomSize = ((((this.preloadTempBuffer[0] & 255) << 24) + ((this.preloadTempBuffer[1] & 255) << 16)) + ((this.preloadTempBuffer[2] & 255) << 8)) + (this.preloadTempBuffer[3] & 255);
            if (atomSize == 0) {
                return 0;
            }
            if (atomSize == 1) {
                atomSize = ((((this.preloadTempBuffer[12] & 255) << 24) + ((this.preloadTempBuffer[13] & 255) << 16)) + ((this.preloadTempBuffer[14] & 255) << 8)) + (this.preloadTempBuffer[15] & 255);
            }
            if (this.preloadTempBuffer[4] == (byte) 109 && this.preloadTempBuffer[5] == (byte) 111 && this.preloadTempBuffer[6] == (byte) 111 && this.preloadTempBuffer[7] == (byte) 118) {
                return -atomSize;
            }
            if (atomSize + atomOffset >= partOffset + partSize) {
                return atomSize + atomOffset;
            }
            atomOffset += atomSize;
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

    /* JADX WARNING: Removed duplicated region for block: B:179:0x0641 A:{Catch:{ Exception -> 0x02d7 }} */
    /* JADX WARNING: Removed duplicated region for block: B:82:0x02bd A:{Catch:{ Exception -> 0x02d7 }} */
    protected boolean processRequestResult(org.telegram.messenger.FileLoadOperation.RequestInfo r29, org.telegram.tgnet.TLRPC.TL_error r30) {
        /*
        r28 = this;
        r0 = r28;
        r2 = r0.state;
        r3 = 1;
        if (r2 == r3) goto L_0x0037;
    L_0x0007:
        r2 = org.telegram.messenger.BuildVars.DEBUG_VERSION;
        if (r2 == 0) goto L_0x0035;
    L_0x000b:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = "trying to write to finished file ";
        r2 = r2.append(r3);
        r0 = r28;
        r3 = r0.cacheFileFinal;
        r2 = r2.append(r3);
        r3 = " offset ";
        r2 = r2.append(r3);
        r3 = r29.offset;
        r2 = r2.append(r3);
        r2 = r2.toString();
        org.telegram.messenger.FileLog.d(r2);
    L_0x0035:
        r2 = 0;
    L_0x0036:
        return r2;
    L_0x0037:
        r0 = r28;
        r2 = r0.requestInfos;
        r0 = r29;
        r2.remove(r0);
        if (r30 != 0) goto L_0x0646;
    L_0x0042:
        r0 = r28;
        r2 = r0.notLoadedBytesRanges;	 Catch:{ Exception -> 0x02d7 }
        if (r2 != 0) goto L_0x0057;
    L_0x0048:
        r0 = r28;
        r2 = r0.downloadedBytes;	 Catch:{ Exception -> 0x02d7 }
        r3 = r29.offset;	 Catch:{ Exception -> 0x02d7 }
        if (r2 == r3) goto L_0x0057;
    L_0x0052:
        r28.delayRequestInfo(r29);	 Catch:{ Exception -> 0x02d7 }
        r2 = 0;
        goto L_0x0036;
    L_0x0057:
        r2 = r29.response;	 Catch:{ Exception -> 0x02d7 }
        if (r2 == 0) goto L_0x0073;
    L_0x005d:
        r2 = r29.response;	 Catch:{ Exception -> 0x02d7 }
        r11 = r2.bytes;	 Catch:{ Exception -> 0x02d7 }
    L_0x0063:
        if (r11 == 0) goto L_0x006b;
    L_0x0065:
        r2 = r11.limit();	 Catch:{ Exception -> 0x02d7 }
        if (r2 != 0) goto L_0x008f;
    L_0x006b:
        r2 = 1;
        r0 = r28;
        r0.onFinishLoadingFile(r2);	 Catch:{ Exception -> 0x02d7 }
        r2 = 0;
        goto L_0x0036;
    L_0x0073:
        r2 = r29.responseWeb;	 Catch:{ Exception -> 0x02d7 }
        if (r2 == 0) goto L_0x0080;
    L_0x0079:
        r2 = r29.responseWeb;	 Catch:{ Exception -> 0x02d7 }
        r11 = r2.bytes;	 Catch:{ Exception -> 0x02d7 }
        goto L_0x0063;
    L_0x0080:
        r2 = r29.responseCdn;	 Catch:{ Exception -> 0x02d7 }
        if (r2 == 0) goto L_0x008d;
    L_0x0086:
        r2 = r29.responseCdn;	 Catch:{ Exception -> 0x02d7 }
        r11 = r2.bytes;	 Catch:{ Exception -> 0x02d7 }
        goto L_0x0063;
    L_0x008d:
        r11 = 0;
        goto L_0x0063;
    L_0x008f:
        r15 = r11.limit();	 Catch:{ Exception -> 0x02d7 }
        r0 = r28;
        r2 = r0.isCdn;	 Catch:{ Exception -> 0x02d7 }
        if (r2 == 0) goto L_0x00cb;
    L_0x0099:
        r2 = r29.offset;	 Catch:{ Exception -> 0x02d7 }
        r3 = 131072; // 0x20000 float:1.83671E-40 double:6.47582E-319;
        r12 = r2 / r3;
        r2 = 131072; // 0x20000 float:1.83671E-40 double:6.47582E-319;
        r19 = r12 * r2;
        r0 = r28;
        r2 = r0.cdnHashes;	 Catch:{ Exception -> 0x02d7 }
        if (r2 == 0) goto L_0x00c8;
    L_0x00ab:
        r0 = r28;
        r2 = r0.cdnHashes;	 Catch:{ Exception -> 0x02d7 }
        r0 = r19;
        r2 = r2.get(r0);	 Catch:{ Exception -> 0x02d7 }
        r2 = (org.telegram.tgnet.TLRPC.TL_fileHash) r2;	 Catch:{ Exception -> 0x02d7 }
        r21 = r2;
    L_0x00b9:
        if (r21 != 0) goto L_0x00cb;
    L_0x00bb:
        r28.delayRequestInfo(r29);	 Catch:{ Exception -> 0x02d7 }
        r0 = r28;
        r1 = r19;
        r0.requestFileOffsets(r1);	 Catch:{ Exception -> 0x02d7 }
        r2 = 1;
        goto L_0x0036;
    L_0x00c8:
        r21 = 0;
        goto L_0x00b9;
    L_0x00cb:
        r2 = r29.responseCdn;	 Catch:{ Exception -> 0x02d7 }
        if (r2 == 0) goto L_0x011d;
    L_0x00d1:
        r2 = r29.offset;	 Catch:{ Exception -> 0x02d7 }
        r22 = r2 / 16;
        r0 = r28;
        r2 = r0.cdnIv;	 Catch:{ Exception -> 0x02d7 }
        r3 = 15;
        r0 = r22;
        r4 = r0 & 255;
        r4 = (byte) r4;	 Catch:{ Exception -> 0x02d7 }
        r2[r3] = r4;	 Catch:{ Exception -> 0x02d7 }
        r0 = r28;
        r2 = r0.cdnIv;	 Catch:{ Exception -> 0x02d7 }
        r3 = 14;
        r4 = r22 >> 8;
        r4 = r4 & 255;
        r4 = (byte) r4;	 Catch:{ Exception -> 0x02d7 }
        r2[r3] = r4;	 Catch:{ Exception -> 0x02d7 }
        r0 = r28;
        r2 = r0.cdnIv;	 Catch:{ Exception -> 0x02d7 }
        r3 = 13;
        r4 = r22 >> 16;
        r4 = r4 & 255;
        r4 = (byte) r4;	 Catch:{ Exception -> 0x02d7 }
        r2[r3] = r4;	 Catch:{ Exception -> 0x02d7 }
        r0 = r28;
        r2 = r0.cdnIv;	 Catch:{ Exception -> 0x02d7 }
        r3 = 12;
        r4 = r22 >> 24;
        r4 = r4 & 255;
        r4 = (byte) r4;	 Catch:{ Exception -> 0x02d7 }
        r2[r3] = r4;	 Catch:{ Exception -> 0x02d7 }
        r2 = r11.buffer;	 Catch:{ Exception -> 0x02d7 }
        r0 = r28;
        r3 = r0.cdnKey;	 Catch:{ Exception -> 0x02d7 }
        r0 = r28;
        r4 = r0.cdnIv;	 Catch:{ Exception -> 0x02d7 }
        r5 = 0;
        r6 = r11.limit();	 Catch:{ Exception -> 0x02d7 }
        org.telegram.messenger.Utilities.aesCtrDecryption(r2, r3, r4, r5, r6);	 Catch:{ Exception -> 0x02d7 }
    L_0x011d:
        r0 = r28;
        r2 = r0.isPreloadVideoOperation;	 Catch:{ Exception -> 0x02d7 }
        if (r2 == 0) goto L_0x030d;
    L_0x0123:
        r0 = r28;
        r2 = r0.preloadStream;	 Catch:{ Exception -> 0x02d7 }
        r3 = r29.offset;	 Catch:{ Exception -> 0x02d7 }
        r2.writeInt(r3);	 Catch:{ Exception -> 0x02d7 }
        r0 = r28;
        r2 = r0.preloadStream;	 Catch:{ Exception -> 0x02d7 }
        r2.writeInt(r15);	 Catch:{ Exception -> 0x02d7 }
        r0 = r28;
        r2 = r0.preloadStreamFileOffset;	 Catch:{ Exception -> 0x02d7 }
        r2 = r2 + 8;
        r0 = r28;
        r0.preloadStreamFileOffset = r2;	 Catch:{ Exception -> 0x02d7 }
        r0 = r28;
        r2 = r0.preloadStream;	 Catch:{ Exception -> 0x02d7 }
        r13 = r2.getChannel();	 Catch:{ Exception -> 0x02d7 }
        r2 = r11.buffer;	 Catch:{ Exception -> 0x02d7 }
        r13.write(r2);	 Catch:{ Exception -> 0x02d7 }
        r2 = org.telegram.messenger.BuildVars.DEBUG_VERSION;	 Catch:{ Exception -> 0x02d7 }
        if (r2 == 0) goto L_0x0185;
    L_0x0150:
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x02d7 }
        r2.<init>();	 Catch:{ Exception -> 0x02d7 }
        r3 = "save preload file part ";
        r2 = r2.append(r3);	 Catch:{ Exception -> 0x02d7 }
        r0 = r28;
        r3 = r0.cacheFilePreload;	 Catch:{ Exception -> 0x02d7 }
        r2 = r2.append(r3);	 Catch:{ Exception -> 0x02d7 }
        r3 = " offset ";
        r2 = r2.append(r3);	 Catch:{ Exception -> 0x02d7 }
        r3 = r29.offset;	 Catch:{ Exception -> 0x02d7 }
        r2 = r2.append(r3);	 Catch:{ Exception -> 0x02d7 }
        r3 = " size ";
        r2 = r2.append(r3);	 Catch:{ Exception -> 0x02d7 }
        r2 = r2.append(r15);	 Catch:{ Exception -> 0x02d7 }
        r2 = r2.toString();	 Catch:{ Exception -> 0x02d7 }
        org.telegram.messenger.FileLog.d(r2);	 Catch:{ Exception -> 0x02d7 }
    L_0x0185:
        r0 = r28;
        r2 = r0.preloadedBytesRanges;	 Catch:{ Exception -> 0x02d7 }
        if (r2 != 0) goto L_0x0194;
    L_0x018b:
        r2 = new android.util.SparseArray;	 Catch:{ Exception -> 0x02d7 }
        r2.<init>();	 Catch:{ Exception -> 0x02d7 }
        r0 = r28;
        r0.preloadedBytesRanges = r2;	 Catch:{ Exception -> 0x02d7 }
    L_0x0194:
        r0 = r28;
        r2 = r0.preloadedBytesRanges;	 Catch:{ Exception -> 0x02d7 }
        r3 = r29.offset;	 Catch:{ Exception -> 0x02d7 }
        r4 = new org.telegram.messenger.FileLoadOperation$PreloadRange;	 Catch:{ Exception -> 0x02d7 }
        r0 = r28;
        r5 = r0.preloadStreamFileOffset;	 Catch:{ Exception -> 0x02d7 }
        r6 = r29.offset;	 Catch:{ Exception -> 0x02d7 }
        r7 = 0;
        r4.<init>(r5, r6, r15);	 Catch:{ Exception -> 0x02d7 }
        r2.put(r3, r4);	 Catch:{ Exception -> 0x02d7 }
        r0 = r28;
        r2 = r0.totalPreloadedBytes;	 Catch:{ Exception -> 0x02d7 }
        r2 = r2 + r15;
        r0 = r28;
        r0.totalPreloadedBytes = r2;	 Catch:{ Exception -> 0x02d7 }
        r0 = r28;
        r2 = r0.preloadStreamFileOffset;	 Catch:{ Exception -> 0x02d7 }
        r2 = r2 + r15;
        r0 = r28;
        r0.preloadStreamFileOffset = r2;	 Catch:{ Exception -> 0x02d7 }
        r0 = r28;
        r2 = r0.moovFound;	 Catch:{ Exception -> 0x02d7 }
        if (r2 != 0) goto L_0x020c;
    L_0x01c5:
        r0 = r28;
        r2 = r0.nextAtomOffset;	 Catch:{ Exception -> 0x02d7 }
        r3 = r29.offset;	 Catch:{ Exception -> 0x02d7 }
        r0 = r28;
        r22 = r0.findNextPreloadDownloadOffset(r2, r3, r11);	 Catch:{ Exception -> 0x02d7 }
        if (r22 >= 0) goto L_0x02e3;
    L_0x01d5:
        r22 = r22 * -1;
        r0 = r28;
        r2 = r0.nextPreloadDownloadOffset;	 Catch:{ Exception -> 0x02d7 }
        r0 = r28;
        r3 = r0.currentDownloadChunkSize;	 Catch:{ Exception -> 0x02d7 }
        r2 = r2 + r3;
        r0 = r28;
        r0.nextPreloadDownloadOffset = r2;	 Catch:{ Exception -> 0x02d7 }
        r0 = r28;
        r2 = r0.nextPreloadDownloadOffset;	 Catch:{ Exception -> 0x02d7 }
        r0 = r28;
        r3 = r0.totalBytesCount;	 Catch:{ Exception -> 0x02d7 }
        r3 = r3 / 2;
        if (r2 >= r3) goto L_0x02c6;
    L_0x01f0:
        r2 = 1048576; // 0x100000 float:1.469368E-39 double:5.180654E-318;
        r2 = r2 + r22;
        r0 = r28;
        r0.foundMoovSize = r2;	 Catch:{ Exception -> 0x02d7 }
        r0 = r28;
        r0.preloadNotRequestedBytesCount = r2;	 Catch:{ Exception -> 0x02d7 }
        r2 = 1;
        r0 = r28;
        r0.moovFound = r2;	 Catch:{ Exception -> 0x02d7 }
    L_0x0201:
        r2 = -1;
        r0 = r28;
        r0.nextPreloadDownloadOffset = r2;	 Catch:{ Exception -> 0x02d7 }
    L_0x0206:
        r0 = r22;
        r1 = r28;
        r1.nextAtomOffset = r0;	 Catch:{ Exception -> 0x02d7 }
    L_0x020c:
        r0 = r28;
        r2 = r0.preloadStream;	 Catch:{ Exception -> 0x02d7 }
        r0 = r28;
        r3 = r0.foundMoovSize;	 Catch:{ Exception -> 0x02d7 }
        r2.writeInt(r3);	 Catch:{ Exception -> 0x02d7 }
        r0 = r28;
        r2 = r0.preloadStream;	 Catch:{ Exception -> 0x02d7 }
        r0 = r28;
        r3 = r0.nextPreloadDownloadOffset;	 Catch:{ Exception -> 0x02d7 }
        r2.writeInt(r3);	 Catch:{ Exception -> 0x02d7 }
        r0 = r28;
        r2 = r0.preloadStream;	 Catch:{ Exception -> 0x02d7 }
        r0 = r28;
        r3 = r0.nextAtomOffset;	 Catch:{ Exception -> 0x02d7 }
        r2.writeInt(r3);	 Catch:{ Exception -> 0x02d7 }
        r0 = r28;
        r2 = r0.preloadStreamFileOffset;	 Catch:{ Exception -> 0x02d7 }
        r2 = r2 + 12;
        r0 = r28;
        r0.preloadStreamFileOffset = r2;	 Catch:{ Exception -> 0x02d7 }
        r0 = r28;
        r2 = r0.nextPreloadDownloadOffset;	 Catch:{ Exception -> 0x02d7 }
        if (r2 == 0) goto L_0x025b;
    L_0x023d:
        r0 = r28;
        r2 = r0.moovFound;	 Catch:{ Exception -> 0x02d7 }
        if (r2 == 0) goto L_0x0249;
    L_0x0243:
        r0 = r28;
        r2 = r0.foundMoovSize;	 Catch:{ Exception -> 0x02d7 }
        if (r2 < 0) goto L_0x025b;
    L_0x0249:
        r0 = r28;
        r2 = r0.totalPreloadedBytes;	 Catch:{ Exception -> 0x02d7 }
        r3 = 2097152; // 0x200000 float:2.938736E-39 double:1.0361308E-317;
        if (r2 > r3) goto L_0x025b;
    L_0x0251:
        r0 = r28;
        r2 = r0.nextPreloadDownloadOffset;	 Catch:{ Exception -> 0x02d7 }
        r0 = r28;
        r3 = r0.totalBytesCount;	 Catch:{ Exception -> 0x02d7 }
        if (r2 < r3) goto L_0x02f4;
    L_0x025b:
        r20 = 1;
    L_0x025d:
        if (r20 == 0) goto L_0x02f8;
    L_0x025f:
        r0 = r28;
        r2 = r0.preloadStream;	 Catch:{ Exception -> 0x02d7 }
        r4 = 0;
        r2.seek(r4);	 Catch:{ Exception -> 0x02d7 }
        r0 = r28;
        r2 = r0.preloadStream;	 Catch:{ Exception -> 0x02d7 }
        r3 = 1;
        r2.write(r3);	 Catch:{ Exception -> 0x02d7 }
    L_0x0270:
        r9 = 0;
    L_0x0271:
        r0 = r28;
        r2 = r0.delayedRequestInfos;	 Catch:{ Exception -> 0x02d7 }
        r2 = r2.size();	 Catch:{ Exception -> 0x02d7 }
        if (r9 >= r2) goto L_0x02bb;
    L_0x027b:
        r0 = r28;
        r2 = r0.delayedRequestInfos;	 Catch:{ Exception -> 0x02d7 }
        r16 = r2.get(r9);	 Catch:{ Exception -> 0x02d7 }
        r16 = (org.telegram.messenger.FileLoadOperation.RequestInfo) r16;	 Catch:{ Exception -> 0x02d7 }
        r0 = r28;
        r2 = r0.notLoadedBytesRanges;	 Catch:{ Exception -> 0x02d7 }
        if (r2 != 0) goto L_0x0295;
    L_0x028b:
        r0 = r28;
        r2 = r0.downloadedBytes;	 Catch:{ Exception -> 0x02d7 }
        r3 = r16.offset;	 Catch:{ Exception -> 0x02d7 }
        if (r2 != r3) goto L_0x063d;
    L_0x0295:
        r0 = r28;
        r2 = r0.delayedRequestInfos;	 Catch:{ Exception -> 0x02d7 }
        r2.remove(r9);	 Catch:{ Exception -> 0x02d7 }
        r2 = 0;
        r0 = r28;
        r1 = r16;
        r2 = r0.processRequestResult(r1, r2);	 Catch:{ Exception -> 0x02d7 }
        if (r2 != 0) goto L_0x02bb;
    L_0x02a7:
        r2 = r16.response;	 Catch:{ Exception -> 0x02d7 }
        if (r2 == 0) goto L_0x0611;
    L_0x02ad:
        r2 = r16.response;	 Catch:{ Exception -> 0x02d7 }
        r3 = 0;
        r2.disableFree = r3;	 Catch:{ Exception -> 0x02d7 }
        r2 = r16.response;	 Catch:{ Exception -> 0x02d7 }
        r2.freeResources();	 Catch:{ Exception -> 0x02d7 }
    L_0x02bb:
        if (r20 == 0) goto L_0x0641;
    L_0x02bd:
        r2 = 1;
        r0 = r28;
        r0.onFinishLoadingFile(r2);	 Catch:{ Exception -> 0x02d7 }
    L_0x02c3:
        r2 = 0;
        goto L_0x0036;
    L_0x02c6:
        r2 = 2097152; // 0x200000 float:2.938736E-39 double:1.0361308E-317;
        r0 = r28;
        r0.foundMoovSize = r2;	 Catch:{ Exception -> 0x02d7 }
        r0 = r28;
        r0.preloadNotRequestedBytesCount = r2;	 Catch:{ Exception -> 0x02d7 }
        r2 = 2;
        r0 = r28;
        r0.moovFound = r2;	 Catch:{ Exception -> 0x02d7 }
        goto L_0x0201;
    L_0x02d7:
        r17 = move-exception;
        r2 = 0;
        r3 = 0;
        r0 = r28;
        r0.onFail(r2, r3);
        org.telegram.messenger.FileLog.e(r17);
        goto L_0x02c3;
    L_0x02e3:
        r0 = r28;
        r2 = r0.currentDownloadChunkSize;	 Catch:{ Exception -> 0x02d7 }
        r2 = r22 / r2;
        r0 = r28;
        r3 = r0.currentDownloadChunkSize;	 Catch:{ Exception -> 0x02d7 }
        r2 = r2 * r3;
        r0 = r28;
        r0.nextPreloadDownloadOffset = r2;	 Catch:{ Exception -> 0x02d7 }
        goto L_0x0206;
    L_0x02f4:
        r20 = 0;
        goto L_0x025d;
    L_0x02f8:
        r0 = r28;
        r2 = r0.moovFound;	 Catch:{ Exception -> 0x02d7 }
        if (r2 == 0) goto L_0x0270;
    L_0x02fe:
        r0 = r28;
        r2 = r0.foundMoovSize;	 Catch:{ Exception -> 0x02d7 }
        r0 = r28;
        r3 = r0.currentDownloadChunkSize;	 Catch:{ Exception -> 0x02d7 }
        r2 = r2 - r3;
        r0 = r28;
        r0.foundMoovSize = r2;	 Catch:{ Exception -> 0x02d7 }
        goto L_0x0270;
    L_0x030d:
        r0 = r28;
        r2 = r0.downloadedBytes;	 Catch:{ Exception -> 0x02d7 }
        r2 = r2 + r15;
        r0 = r28;
        r0.downloadedBytes = r2;	 Catch:{ Exception -> 0x02d7 }
        r0 = r28;
        r2 = r0.totalBytesCount;	 Catch:{ Exception -> 0x02d7 }
        if (r2 <= 0) goto L_0x054f;
    L_0x031c:
        r0 = r28;
        r2 = r0.downloadedBytes;	 Catch:{ Exception -> 0x02d7 }
        r0 = r28;
        r3 = r0.totalBytesCount;	 Catch:{ Exception -> 0x02d7 }
        if (r2 < r3) goto L_0x054b;
    L_0x0326:
        r20 = 1;
    L_0x0328:
        r0 = r28;
        r2 = r0.key;	 Catch:{ Exception -> 0x02d7 }
        if (r2 == 0) goto L_0x0356;
    L_0x032e:
        r2 = r11.buffer;	 Catch:{ Exception -> 0x02d7 }
        r0 = r28;
        r3 = r0.key;	 Catch:{ Exception -> 0x02d7 }
        r0 = r28;
        r4 = r0.iv;	 Catch:{ Exception -> 0x02d7 }
        r5 = 0;
        r6 = 1;
        r7 = 0;
        r8 = r11.limit();	 Catch:{ Exception -> 0x02d7 }
        org.telegram.messenger.Utilities.aesIgeEncryption(r2, r3, r4, r5, r6, r7, r8);	 Catch:{ Exception -> 0x02d7 }
        if (r20 == 0) goto L_0x0356;
    L_0x0344:
        r0 = r28;
        r2 = r0.bytesCountPadding;	 Catch:{ Exception -> 0x02d7 }
        if (r2 == 0) goto L_0x0356;
    L_0x034a:
        r2 = r11.limit();	 Catch:{ Exception -> 0x02d7 }
        r0 = r28;
        r3 = r0.bytesCountPadding;	 Catch:{ Exception -> 0x02d7 }
        r2 = r2 - r3;
        r11.limit(r2);	 Catch:{ Exception -> 0x02d7 }
    L_0x0356:
        r0 = r28;
        r2 = r0.encryptFile;	 Catch:{ Exception -> 0x02d7 }
        if (r2 == 0) goto L_0x03a8;
    L_0x035c:
        r2 = r29.offset;	 Catch:{ Exception -> 0x02d7 }
        r22 = r2 / 16;
        r0 = r28;
        r2 = r0.encryptIv;	 Catch:{ Exception -> 0x02d7 }
        r3 = 15;
        r0 = r22;
        r4 = r0 & 255;
        r4 = (byte) r4;	 Catch:{ Exception -> 0x02d7 }
        r2[r3] = r4;	 Catch:{ Exception -> 0x02d7 }
        r0 = r28;
        r2 = r0.encryptIv;	 Catch:{ Exception -> 0x02d7 }
        r3 = 14;
        r4 = r22 >> 8;
        r4 = r4 & 255;
        r4 = (byte) r4;	 Catch:{ Exception -> 0x02d7 }
        r2[r3] = r4;	 Catch:{ Exception -> 0x02d7 }
        r0 = r28;
        r2 = r0.encryptIv;	 Catch:{ Exception -> 0x02d7 }
        r3 = 13;
        r4 = r22 >> 16;
        r4 = r4 & 255;
        r4 = (byte) r4;	 Catch:{ Exception -> 0x02d7 }
        r2[r3] = r4;	 Catch:{ Exception -> 0x02d7 }
        r0 = r28;
        r2 = r0.encryptIv;	 Catch:{ Exception -> 0x02d7 }
        r3 = 12;
        r4 = r22 >> 24;
        r4 = r4 & 255;
        r4 = (byte) r4;	 Catch:{ Exception -> 0x02d7 }
        r2[r3] = r4;	 Catch:{ Exception -> 0x02d7 }
        r2 = r11.buffer;	 Catch:{ Exception -> 0x02d7 }
        r0 = r28;
        r3 = r0.encryptKey;	 Catch:{ Exception -> 0x02d7 }
        r0 = r28;
        r4 = r0.encryptIv;	 Catch:{ Exception -> 0x02d7 }
        r5 = 0;
        r6 = r11.limit();	 Catch:{ Exception -> 0x02d7 }
        org.telegram.messenger.Utilities.aesCtrDecryption(r2, r3, r4, r5, r6);	 Catch:{ Exception -> 0x02d7 }
    L_0x03a8:
        r0 = r28;
        r2 = r0.notLoadedBytesRanges;	 Catch:{ Exception -> 0x02d7 }
        if (r2 == 0) goto L_0x03e8;
    L_0x03ae:
        r0 = r28;
        r2 = r0.fileOutputStream;	 Catch:{ Exception -> 0x02d7 }
        r3 = r29.offset;	 Catch:{ Exception -> 0x02d7 }
        r4 = (long) r3;	 Catch:{ Exception -> 0x02d7 }
        r2.seek(r4);	 Catch:{ Exception -> 0x02d7 }
        r2 = org.telegram.messenger.BuildVars.DEBUG_VERSION;	 Catch:{ Exception -> 0x02d7 }
        if (r2 == 0) goto L_0x03e8;
    L_0x03be:
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x02d7 }
        r2.<init>();	 Catch:{ Exception -> 0x02d7 }
        r3 = "save file part ";
        r2 = r2.append(r3);	 Catch:{ Exception -> 0x02d7 }
        r0 = r28;
        r3 = r0.cacheFileFinal;	 Catch:{ Exception -> 0x02d7 }
        r2 = r2.append(r3);	 Catch:{ Exception -> 0x02d7 }
        r3 = " offset ";
        r2 = r2.append(r3);	 Catch:{ Exception -> 0x02d7 }
        r3 = r29.offset;	 Catch:{ Exception -> 0x02d7 }
        r2 = r2.append(r3);	 Catch:{ Exception -> 0x02d7 }
        r2 = r2.toString();	 Catch:{ Exception -> 0x02d7 }
        org.telegram.messenger.FileLog.d(r2);	 Catch:{ Exception -> 0x02d7 }
    L_0x03e8:
        r0 = r28;
        r2 = r0.fileOutputStream;	 Catch:{ Exception -> 0x02d7 }
        r13 = r2.getChannel();	 Catch:{ Exception -> 0x02d7 }
        r2 = r11.buffer;	 Catch:{ Exception -> 0x02d7 }
        r13.write(r2);	 Catch:{ Exception -> 0x02d7 }
        r0 = r28;
        r2 = r0.notLoadedBytesRanges;	 Catch:{ Exception -> 0x02d7 }
        r3 = r29.offset;	 Catch:{ Exception -> 0x02d7 }
        r4 = r29.offset;	 Catch:{ Exception -> 0x02d7 }
        r4 = r4 + r15;
        r5 = 1;
        r0 = r28;
        r0.addPart(r2, r3, r4, r5);	 Catch:{ Exception -> 0x02d7 }
        r0 = r28;
        r2 = r0.isCdn;	 Catch:{ Exception -> 0x02d7 }
        if (r2 == 0) goto L_0x05cb;
    L_0x040e:
        r2 = r29.offset;	 Catch:{ Exception -> 0x02d7 }
        r3 = 131072; // 0x20000 float:1.83671E-40 double:6.47582E-319;
        r12 = r2 / r3;
        r0 = r28;
        r2 = r0.notCheckedCdnRanges;	 Catch:{ Exception -> 0x02d7 }
        r26 = r2.size();	 Catch:{ Exception -> 0x02d7 }
        r14 = 1;
        r9 = 0;
    L_0x0420:
        r0 = r26;
        if (r9 >= r0) goto L_0x043b;
    L_0x0424:
        r0 = r28;
        r2 = r0.notCheckedCdnRanges;	 Catch:{ Exception -> 0x02d7 }
        r23 = r2.get(r9);	 Catch:{ Exception -> 0x02d7 }
        r23 = (org.telegram.messenger.FileLoadOperation.Range) r23;	 Catch:{ Exception -> 0x02d7 }
        r2 = r23.start;	 Catch:{ Exception -> 0x02d7 }
        if (r2 > r12) goto L_0x0581;
    L_0x0434:
        r2 = r23.end;	 Catch:{ Exception -> 0x02d7 }
        if (r12 > r2) goto L_0x0581;
    L_0x043a:
        r14 = 0;
    L_0x043b:
        if (r14 != 0) goto L_0x05cb;
    L_0x043d:
        r2 = 131072; // 0x20000 float:1.83671E-40 double:6.47582E-319;
        r19 = r12 * r2;
        r0 = r28;
        r2 = r0.notLoadedBytesRanges;	 Catch:{ Exception -> 0x02d7 }
        r3 = 131072; // 0x20000 float:1.83671E-40 double:6.47582E-319;
        r0 = r28;
        r1 = r19;
        r10 = r0.getDownloadedLengthFromOffsetInternal(r2, r1, r3);	 Catch:{ Exception -> 0x02d7 }
        if (r10 == 0) goto L_0x05cb;
    L_0x0451:
        r2 = 131072; // 0x20000 float:1.83671E-40 double:6.47582E-319;
        if (r10 == r2) goto L_0x046b;
    L_0x0455:
        r0 = r28;
        r2 = r0.totalBytesCount;	 Catch:{ Exception -> 0x02d7 }
        if (r2 <= 0) goto L_0x0463;
    L_0x045b:
        r0 = r28;
        r2 = r0.totalBytesCount;	 Catch:{ Exception -> 0x02d7 }
        r2 = r2 - r19;
        if (r10 == r2) goto L_0x046b;
    L_0x0463:
        r0 = r28;
        r2 = r0.totalBytesCount;	 Catch:{ Exception -> 0x02d7 }
        if (r2 > 0) goto L_0x05cb;
    L_0x0469:
        if (r20 == 0) goto L_0x05cb;
    L_0x046b:
        r0 = r28;
        r2 = r0.cdnHashes;	 Catch:{ Exception -> 0x02d7 }
        r0 = r19;
        r21 = r2.get(r0);	 Catch:{ Exception -> 0x02d7 }
        r21 = (org.telegram.tgnet.TLRPC.TL_fileHash) r21;	 Catch:{ Exception -> 0x02d7 }
        r0 = r28;
        r2 = r0.fileReadStream;	 Catch:{ Exception -> 0x02d7 }
        if (r2 != 0) goto L_0x0495;
    L_0x047d:
        r2 = 131072; // 0x20000 float:1.83671E-40 double:6.47582E-319;
        r2 = new byte[r2];	 Catch:{ Exception -> 0x02d7 }
        r0 = r28;
        r0.cdnCheckBytes = r2;	 Catch:{ Exception -> 0x02d7 }
        r2 = new java.io.RandomAccessFile;	 Catch:{ Exception -> 0x02d7 }
        r0 = r28;
        r3 = r0.cacheFileTemp;	 Catch:{ Exception -> 0x02d7 }
        r4 = "r";
        r2.<init>(r3, r4);	 Catch:{ Exception -> 0x02d7 }
        r0 = r28;
        r0.fileReadStream = r2;	 Catch:{ Exception -> 0x02d7 }
    L_0x0495:
        r0 = r28;
        r2 = r0.fileReadStream;	 Catch:{ Exception -> 0x02d7 }
        r0 = r19;
        r4 = (long) r0;	 Catch:{ Exception -> 0x02d7 }
        r2.seek(r4);	 Catch:{ Exception -> 0x02d7 }
        r0 = r28;
        r2 = r0.fileReadStream;	 Catch:{ Exception -> 0x02d7 }
        r0 = r28;
        r3 = r0.cdnCheckBytes;	 Catch:{ Exception -> 0x02d7 }
        r4 = 0;
        r2.readFully(r3, r4, r10);	 Catch:{ Exception -> 0x02d7 }
        r0 = r28;
        r2 = r0.cdnCheckBytes;	 Catch:{ Exception -> 0x02d7 }
        r3 = 0;
        r25 = org.telegram.messenger.Utilities.computeSHA256(r2, r3, r10);	 Catch:{ Exception -> 0x02d7 }
        r0 = r21;
        r2 = r0.hash;	 Catch:{ Exception -> 0x02d7 }
        r0 = r25;
        r2 = java.util.Arrays.equals(r0, r2);	 Catch:{ Exception -> 0x02d7 }
        if (r2 != 0) goto L_0x05b6;
    L_0x04c0:
        r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x02d7 }
        if (r2 == 0) goto L_0x053a;
    L_0x04c4:
        r0 = r28;
        r2 = r0.location;	 Catch:{ Exception -> 0x02d7 }
        if (r2 == 0) goto L_0x0585;
    L_0x04ca:
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x02d7 }
        r2.<init>();	 Catch:{ Exception -> 0x02d7 }
        r3 = "invalid cdn hash ";
        r2 = r2.append(r3);	 Catch:{ Exception -> 0x02d7 }
        r0 = r28;
        r3 = r0.location;	 Catch:{ Exception -> 0x02d7 }
        r2 = r2.append(r3);	 Catch:{ Exception -> 0x02d7 }
        r3 = " id = ";
        r2 = r2.append(r3);	 Catch:{ Exception -> 0x02d7 }
        r0 = r28;
        r3 = r0.location;	 Catch:{ Exception -> 0x02d7 }
        r4 = r3.id;	 Catch:{ Exception -> 0x02d7 }
        r2 = r2.append(r4);	 Catch:{ Exception -> 0x02d7 }
        r3 = " local_id = ";
        r2 = r2.append(r3);	 Catch:{ Exception -> 0x02d7 }
        r0 = r28;
        r3 = r0.location;	 Catch:{ Exception -> 0x02d7 }
        r3 = r3.local_id;	 Catch:{ Exception -> 0x02d7 }
        r2 = r2.append(r3);	 Catch:{ Exception -> 0x02d7 }
        r3 = " access_hash = ";
        r2 = r2.append(r3);	 Catch:{ Exception -> 0x02d7 }
        r0 = r28;
        r3 = r0.location;	 Catch:{ Exception -> 0x02d7 }
        r4 = r3.access_hash;	 Catch:{ Exception -> 0x02d7 }
        r2 = r2.append(r4);	 Catch:{ Exception -> 0x02d7 }
        r3 = " volume_id = ";
        r2 = r2.append(r3);	 Catch:{ Exception -> 0x02d7 }
        r0 = r28;
        r3 = r0.location;	 Catch:{ Exception -> 0x02d7 }
        r4 = r3.volume_id;	 Catch:{ Exception -> 0x02d7 }
        r2 = r2.append(r4);	 Catch:{ Exception -> 0x02d7 }
        r3 = " secret = ";
        r2 = r2.append(r3);	 Catch:{ Exception -> 0x02d7 }
        r0 = r28;
        r3 = r0.location;	 Catch:{ Exception -> 0x02d7 }
        r4 = r3.secret;	 Catch:{ Exception -> 0x02d7 }
        r2 = r2.append(r4);	 Catch:{ Exception -> 0x02d7 }
        r2 = r2.toString();	 Catch:{ Exception -> 0x02d7 }
        org.telegram.messenger.FileLog.e(r2);	 Catch:{ Exception -> 0x02d7 }
    L_0x053a:
        r2 = 0;
        r3 = 0;
        r0 = r28;
        r0.onFail(r2, r3);	 Catch:{ Exception -> 0x02d7 }
        r0 = r28;
        r2 = r0.cacheFileTemp;	 Catch:{ Exception -> 0x02d7 }
        r2.delete();	 Catch:{ Exception -> 0x02d7 }
        r2 = 0;
        goto L_0x0036;
    L_0x054b:
        r20 = 0;
        goto L_0x0328;
    L_0x054f:
        r0 = r28;
        r2 = r0.currentDownloadChunkSize;	 Catch:{ Exception -> 0x02d7 }
        if (r15 != r2) goto L_0x057a;
    L_0x0555:
        r0 = r28;
        r2 = r0.totalBytesCount;	 Catch:{ Exception -> 0x02d7 }
        r0 = r28;
        r3 = r0.downloadedBytes;	 Catch:{ Exception -> 0x02d7 }
        if (r2 == r3) goto L_0x056a;
    L_0x055f:
        r0 = r28;
        r2 = r0.downloadedBytes;	 Catch:{ Exception -> 0x02d7 }
        r0 = r28;
        r3 = r0.currentDownloadChunkSize;	 Catch:{ Exception -> 0x02d7 }
        r2 = r2 % r3;
        if (r2 == 0) goto L_0x057e;
    L_0x056a:
        r0 = r28;
        r2 = r0.totalBytesCount;	 Catch:{ Exception -> 0x02d7 }
        if (r2 <= 0) goto L_0x057a;
    L_0x0570:
        r0 = r28;
        r2 = r0.totalBytesCount;	 Catch:{ Exception -> 0x02d7 }
        r0 = r28;
        r3 = r0.downloadedBytes;	 Catch:{ Exception -> 0x02d7 }
        if (r2 > r3) goto L_0x057e;
    L_0x057a:
        r20 = 1;
    L_0x057c:
        goto L_0x0328;
    L_0x057e:
        r20 = 0;
        goto L_0x057c;
    L_0x0581:
        r9 = r9 + 1;
        goto L_0x0420;
    L_0x0585:
        r0 = r28;
        r2 = r0.webLocation;	 Catch:{ Exception -> 0x02d7 }
        if (r2 == 0) goto L_0x053a;
    L_0x058b:
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x02d7 }
        r2.<init>();	 Catch:{ Exception -> 0x02d7 }
        r3 = "invalid cdn hash  ";
        r2 = r2.append(r3);	 Catch:{ Exception -> 0x02d7 }
        r0 = r28;
        r3 = r0.webLocation;	 Catch:{ Exception -> 0x02d7 }
        r2 = r2.append(r3);	 Catch:{ Exception -> 0x02d7 }
        r3 = " id = ";
        r2 = r2.append(r3);	 Catch:{ Exception -> 0x02d7 }
        r3 = r28.getFileName();	 Catch:{ Exception -> 0x02d7 }
        r2 = r2.append(r3);	 Catch:{ Exception -> 0x02d7 }
        r2 = r2.toString();	 Catch:{ Exception -> 0x02d7 }
        org.telegram.messenger.FileLog.e(r2);	 Catch:{ Exception -> 0x02d7 }
        goto L_0x053a;
    L_0x05b6:
        r0 = r28;
        r2 = r0.cdnHashes;	 Catch:{ Exception -> 0x02d7 }
        r0 = r19;
        r2.remove(r0);	 Catch:{ Exception -> 0x02d7 }
        r0 = r28;
        r2 = r0.notCheckedCdnRanges;	 Catch:{ Exception -> 0x02d7 }
        r3 = r12 + 1;
        r4 = 0;
        r0 = r28;
        r0.addPart(r2, r12, r3, r4);	 Catch:{ Exception -> 0x02d7 }
    L_0x05cb:
        r0 = r28;
        r2 = r0.fiv;	 Catch:{ Exception -> 0x02d7 }
        if (r2 == 0) goto L_0x05e5;
    L_0x05d1:
        r0 = r28;
        r2 = r0.fiv;	 Catch:{ Exception -> 0x02d7 }
        r4 = 0;
        r2.seek(r4);	 Catch:{ Exception -> 0x02d7 }
        r0 = r28;
        r2 = r0.fiv;	 Catch:{ Exception -> 0x02d7 }
        r0 = r28;
        r3 = r0.iv;	 Catch:{ Exception -> 0x02d7 }
        r2.write(r3);	 Catch:{ Exception -> 0x02d7 }
    L_0x05e5:
        r0 = r28;
        r2 = r0.totalBytesCount;	 Catch:{ Exception -> 0x02d7 }
        if (r2 <= 0) goto L_0x0270;
    L_0x05eb:
        r0 = r28;
        r2 = r0.state;	 Catch:{ Exception -> 0x02d7 }
        r3 = 1;
        if (r2 != r3) goto L_0x0270;
    L_0x05f2:
        r28.copyNotLoadedRanges();	 Catch:{ Exception -> 0x02d7 }
        r0 = r28;
        r2 = r0.delegate;	 Catch:{ Exception -> 0x02d7 }
        r3 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r0 = r28;
        r4 = r0.downloadedBytes;	 Catch:{ Exception -> 0x02d7 }
        r4 = (float) r4;	 Catch:{ Exception -> 0x02d7 }
        r0 = r28;
        r5 = r0.totalBytesCount;	 Catch:{ Exception -> 0x02d7 }
        r5 = (float) r5;	 Catch:{ Exception -> 0x02d7 }
        r4 = r4 / r5;
        r3 = java.lang.Math.min(r3, r4);	 Catch:{ Exception -> 0x02d7 }
        r0 = r28;
        r2.didChangedLoadProgress(r0, r3);	 Catch:{ Exception -> 0x02d7 }
        goto L_0x0270;
    L_0x0611:
        r2 = r16.responseWeb;	 Catch:{ Exception -> 0x02d7 }
        if (r2 == 0) goto L_0x0627;
    L_0x0617:
        r2 = r16.responseWeb;	 Catch:{ Exception -> 0x02d7 }
        r3 = 0;
        r2.disableFree = r3;	 Catch:{ Exception -> 0x02d7 }
        r2 = r16.responseWeb;	 Catch:{ Exception -> 0x02d7 }
        r2.freeResources();	 Catch:{ Exception -> 0x02d7 }
        goto L_0x02bb;
    L_0x0627:
        r2 = r16.responseCdn;	 Catch:{ Exception -> 0x02d7 }
        if (r2 == 0) goto L_0x02bb;
    L_0x062d:
        r2 = r16.responseCdn;	 Catch:{ Exception -> 0x02d7 }
        r3 = 0;
        r2.disableFree = r3;	 Catch:{ Exception -> 0x02d7 }
        r2 = r16.responseCdn;	 Catch:{ Exception -> 0x02d7 }
        r2.freeResources();	 Catch:{ Exception -> 0x02d7 }
        goto L_0x02bb;
    L_0x063d:
        r9 = r9 + 1;
        goto L_0x0271;
    L_0x0641:
        r28.startDownloadRequest();	 Catch:{ Exception -> 0x02d7 }
        goto L_0x02c3;
    L_0x0646:
        r0 = r30;
        r2 = r0.text;
        r3 = "FILE_MIGRATE_";
        r2 = r2.contains(r3);
        if (r2 == 0) goto L_0x069f;
    L_0x0653:
        r0 = r30;
        r2 = r0.text;
        r3 = "FILE_MIGRATE_";
        r4 = "";
        r18 = r2.replace(r3, r4);
        r24 = new java.util.Scanner;
        r0 = r24;
        r1 = r18;
        r0.<init>(r1);
        r2 = "";
        r0 = r24;
        r0.useDelimiter(r2);
        r2 = r24.nextInt();	 Catch:{ Exception -> 0x0685 }
        r27 = java.lang.Integer.valueOf(r2);	 Catch:{ Exception -> 0x0685 }
    L_0x067a:
        if (r27 != 0) goto L_0x0689;
    L_0x067c:
        r2 = 0;
        r3 = 0;
        r0 = r28;
        r0.onFail(r2, r3);
        goto L_0x02c3;
    L_0x0685:
        r17 = move-exception;
        r27 = 0;
        goto L_0x067a;
    L_0x0689:
        r2 = r27.intValue();
        r0 = r28;
        r0.datacenterId = r2;
        r2 = 0;
        r0 = r28;
        r0.downloadedBytes = r2;
        r0 = r28;
        r0.requestedBytesCount = r2;
        r28.startDownloadRequest();
        goto L_0x02c3;
    L_0x069f:
        r0 = r30;
        r2 = r0.text;
        r3 = "OFFSET_INVALID";
        r2 = r2.contains(r3);
        if (r2 == 0) goto L_0x06d5;
    L_0x06ac:
        r0 = r28;
        r2 = r0.downloadedBytes;
        r0 = r28;
        r3 = r0.currentDownloadChunkSize;
        r2 = r2 % r3;
        if (r2 != 0) goto L_0x06cc;
    L_0x06b7:
        r2 = 1;
        r0 = r28;
        r0.onFinishLoadingFile(r2);	 Catch:{ Exception -> 0x06bf }
        goto L_0x02c3;
    L_0x06bf:
        r17 = move-exception;
        org.telegram.messenger.FileLog.e(r17);
        r2 = 0;
        r3 = 0;
        r0 = r28;
        r0.onFail(r2, r3);
        goto L_0x02c3;
    L_0x06cc:
        r2 = 0;
        r3 = 0;
        r0 = r28;
        r0.onFail(r2, r3);
        goto L_0x02c3;
    L_0x06d5:
        r0 = r30;
        r2 = r0.text;
        r3 = "RETRY_LIMIT";
        r2 = r2.contains(r3);
        if (r2 == 0) goto L_0x06eb;
    L_0x06e2:
        r2 = 0;
        r3 = 2;
        r0 = r28;
        r0.onFail(r2, r3);
        goto L_0x02c3;
    L_0x06eb:
        r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r2 == 0) goto L_0x076d;
    L_0x06ef:
        r0 = r28;
        r2 = r0.location;
        if (r2 == 0) goto L_0x0776;
    L_0x06f5:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r0 = r30;
        r3 = r0.text;
        r2 = r2.append(r3);
        r3 = " ";
        r2 = r2.append(r3);
        r0 = r28;
        r3 = r0.location;
        r2 = r2.append(r3);
        r3 = " id = ";
        r2 = r2.append(r3);
        r0 = r28;
        r3 = r0.location;
        r4 = r3.id;
        r2 = r2.append(r4);
        r3 = " local_id = ";
        r2 = r2.append(r3);
        r0 = r28;
        r3 = r0.location;
        r3 = r3.local_id;
        r2 = r2.append(r3);
        r3 = " access_hash = ";
        r2 = r2.append(r3);
        r0 = r28;
        r3 = r0.location;
        r4 = r3.access_hash;
        r2 = r2.append(r4);
        r3 = " volume_id = ";
        r2 = r2.append(r3);
        r0 = r28;
        r3 = r0.location;
        r4 = r3.volume_id;
        r2 = r2.append(r4);
        r3 = " secret = ";
        r2 = r2.append(r3);
        r0 = r28;
        r3 = r0.location;
        r4 = r3.secret;
        r2 = r2.append(r4);
        r2 = r2.toString();
        org.telegram.messenger.FileLog.e(r2);
    L_0x076d:
        r2 = 0;
        r3 = 0;
        r0 = r28;
        r0.onFail(r2, r3);
        goto L_0x02c3;
    L_0x0776:
        r0 = r28;
        r2 = r0.webLocation;
        if (r2 == 0) goto L_0x076d;
    L_0x077c:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r0 = r30;
        r3 = r0.text;
        r2 = r2.append(r3);
        r3 = " ";
        r2 = r2.append(r3);
        r0 = r28;
        r3 = r0.webLocation;
        r2 = r2.append(r3);
        r3 = " id = ";
        r2 = r2.append(r3);
        r3 = r28.getFileName();
        r2 = r2.append(r3);
        r2 = r2.toString();
        org.telegram.messenger.FileLog.e(r2);
        goto L_0x076d;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.FileLoadOperation.processRequestResult(org.telegram.messenger.FileLoadOperation$RequestInfo, org.telegram.tgnet.TLRPC$TL_error):boolean");
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

    private void clearOperaion(RequestInfo currentInfo, boolean preloadChanged) {
        int a;
        RequestInfo info;
        int minOffset = Integer.MAX_VALUE;
        for (a = 0; a < this.requestInfos.size(); a++) {
            info = (RequestInfo) this.requestInfos.get(a);
            minOffset = Math.min(info.offset, minOffset);
            if (this.isPreloadVideoOperation) {
                this.requestedPreloadedBytesRanges.delete(info.offset);
            } else {
                removePart(this.notRequestedBytesRanges, info.offset, info.offset + this.currentDownloadChunkSize);
            }
            if (!(currentInfo == info || info.requestToken == 0)) {
                ConnectionsManager.getInstance(this.currentAccount).cancelRequest(info.requestToken, true);
            }
        }
        this.requestInfos.clear();
        for (a = 0; a < this.delayedRequestInfos.size(); a++) {
            info = (RequestInfo) this.delayedRequestInfos.get(a);
            if (this.isPreloadVideoOperation) {
                this.requestedPreloadedBytesRanges.delete(info.offset);
            } else {
                removePart(this.notRequestedBytesRanges, info.offset, info.offset + this.currentDownloadChunkSize);
            }
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
        if (!preloadChanged && this.isPreloadVideoOperation) {
            this.requestedBytesCount = this.totalPreloadedBytes;
        } else if (this.notLoadedBytesRanges == null) {
            this.downloadedBytes = minOffset;
            this.requestedBytesCount = minOffset;
        }
    }

    private void requestReference(RequestInfo requestInfo) {
        if (!this.requestingReference) {
            clearOperaion(requestInfo, false);
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
        if (!this.paused && this.state == 1) {
            if (this.nextPartWasPreloaded || this.requestInfos.size() + this.delayedRequestInfos.size() < this.currentMaxDownloadRequests) {
                if (this.isPreloadVideoOperation) {
                    if (this.requestedBytesCount > 2097152) {
                        return;
                    }
                    if (this.moovFound != 0 && this.requestInfos.size() > 0) {
                        return;
                    }
                }
                int count = 1;
                if (!this.nextPartWasPreloaded && (!(this.isPreloadVideoOperation && this.moovFound == 0) && this.totalBytesCount > 0)) {
                    count = Math.max(0, this.currentMaxDownloadRequests - this.requestInfos.size());
                }
                int a = 0;
                while (a < count) {
                    int downloadOffset;
                    if (this.isPreloadVideoOperation) {
                        if (this.moovFound == 0 || this.preloadNotRequestedBytesCount > 0) {
                            if (this.nextPreloadDownloadOffset == -1) {
                                downloadOffset = 0;
                                boolean found = false;
                                int tries = (2097152 / this.currentDownloadChunkSize) + 2;
                                while (tries != 0) {
                                    if (this.requestedPreloadedBytesRanges.get(downloadOffset, 0) != 0) {
                                        downloadOffset += this.currentDownloadChunkSize;
                                        if (downloadOffset > this.totalBytesCount) {
                                            break;
                                        }
                                        if (this.moovFound == 2 && downloadOffset == this.currentDownloadChunkSize * 8) {
                                            downloadOffset = ((this.totalBytesCount - 1048576) / this.currentDownloadChunkSize) * this.currentDownloadChunkSize;
                                        }
                                        tries--;
                                    } else {
                                        found = true;
                                        break;
                                    }
                                }
                                if (!found && this.requestInfos.isEmpty()) {
                                    onFinishLoadingFile(false);
                                }
                            } else {
                                downloadOffset = this.nextPreloadDownloadOffset;
                            }
                            if (this.requestedPreloadedBytesRanges == null) {
                                this.requestedPreloadedBytesRanges = new SparseIntArray();
                            }
                            this.requestedPreloadedBytesRanges.put(downloadOffset, 1);
                            if (BuildVars.DEBUG_VERSION) {
                                FileLog.d("start next preload from " + downloadOffset + " size " + this.totalBytesCount + " for " + this.cacheFilePreload);
                            }
                            this.preloadNotRequestedBytesCount -= this.currentDownloadChunkSize;
                        } else {
                            return;
                        }
                    } else if (this.notRequestedBytesRanges != null) {
                        int size = this.notRequestedBytesRanges.size();
                        int minStart = Integer.MAX_VALUE;
                        int minStreamStart = Integer.MAX_VALUE;
                        for (int b = 0; b < size; b++) {
                            Range range = (Range) this.notRequestedBytesRanges.get(b);
                            if (this.streamStartOffset != 0) {
                                if (range.start <= this.streamStartOffset && range.end > this.streamStartOffset) {
                                    minStreamStart = this.streamStartOffset;
                                    minStart = Integer.MAX_VALUE;
                                    break;
                                } else if (this.streamStartOffset < range.start && range.start < minStreamStart) {
                                    minStreamStart = range.start;
                                }
                            }
                            minStart = Math.min(minStart, range.start);
                        }
                        if (minStreamStart != Integer.MAX_VALUE) {
                            downloadOffset = minStreamStart;
                        } else if (minStart != Integer.MAX_VALUE) {
                            downloadOffset = minStart;
                        } else {
                            return;
                        }
                    } else {
                        downloadOffset = this.requestedBytesCount;
                    }
                    if (!(this.isPreloadVideoOperation || this.notRequestedBytesRanges == null)) {
                        addPart(this.notRequestedBytesRanges, downloadOffset, this.currentDownloadChunkSize + downloadOffset, false);
                    }
                    if (this.totalBytesCount <= 0 || downloadOffset < this.totalBytesCount) {
                        TLObject request;
                        int i;
                        boolean isLast = this.totalBytesCount <= 0 || a == count - 1 || (this.totalBytesCount > 0 && this.currentDownloadChunkSize + downloadOffset >= this.totalBytesCount);
                        int connectionType = this.requestsCount % 2 == 0 ? 2 : 65538;
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
                        if (!(this.isPreloadVideoOperation || !this.supportsPreloading || this.preloadStream == null || this.preloadedBytesRanges == null)) {
                            PreloadRange range2 = (PreloadRange) this.preloadedBytesRanges.get(requestInfo.offset);
                            if (range2 != null) {
                                requestInfo.response = new TL_upload_file();
                                try {
                                    NativeByteBuffer buffer = new NativeByteBuffer(range2.length);
                                    this.preloadStream.seek((long) range2.fileOffset);
                                    this.preloadStream.getChannel().read(buffer.buffer);
                                    buffer.buffer.position(0);
                                    requestInfo.response.bytes = buffer;
                                    Utilities.stageQueue.postRunnable(new FileLoadOperation$$Lambda$11(this, requestInfo));
                                } catch (Exception e) {
                                }
                                a++;
                            }
                        }
                        ConnectionsManager instance = ConnectionsManager.getInstance(this.currentAccount);
                        RequestDelegate fileLoadOperation$$Lambda$12 = new FileLoadOperation$$Lambda$12(this, requestInfo, request);
                        if (this.isCdn) {
                            i = this.cdnDatacenterId;
                        } else {
                            i = this.datacenterId;
                        }
                        requestInfo.requestToken = instance.sendRequest(request, fileLoadOperation$$Lambda$12, null, null, flags, i, connectionType, isLast);
                        this.requestsCount++;
                        a++;
                    } else {
                        return;
                    }
                }
            }
        }
    }

    final /* synthetic */ void lambda$startDownloadRequest$10$FileLoadOperation(RequestInfo requestInfo) {
        processRequestResult(requestInfo, null);
        requestInfo.response.freeResources();
    }

    final /* synthetic */ void lambda$startDownloadRequest$12$FileLoadOperation(RequestInfo requestInfo, TLObject request, TLObject response, TL_error error) {
        if (this.requestInfos.contains(requestInfo)) {
            if (error != null) {
                if (FileRefController.isFileRefError(error.text)) {
                    requestReference(requestInfo);
                    return;
                } else if ((request instanceof TL_upload_getCdnFile) && error.text.equals("FILE_TOKEN_INVALID")) {
                    this.isCdn = false;
                    clearOperaion(requestInfo, false);
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
                    this.notCheckedCdnRanges.add(new Range(0, 12288));
                }
                this.cdnDatacenterId = res.dc_id;
                this.cdnIv = res.encryption_iv;
                this.cdnKey = res.encryption_key;
                this.cdnToken = res.file_token;
                clearOperaion(requestInfo, false);
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
                    if (this.currentType == 50331648) {
                        StatsController.getInstance(this.currentAccount).incrementReceivedBytesCount(response.networkType, 3, (long) (response.getObjectSize() + 4));
                    } else if (this.currentType == 33554432) {
                        StatsController.getInstance(this.currentAccount).incrementReceivedBytesCount(response.networkType, 2, (long) (response.getObjectSize() + 4));
                    } else if (this.currentType == 16777216) {
                        StatsController.getInstance(this.currentAccount).incrementReceivedBytesCount(response.networkType, 4, (long) (response.getObjectSize() + 4));
                    } else if (this.currentType == 67108864) {
                        StatsController.getInstance(this.currentAccount).incrementReceivedBytesCount(response.networkType, 5, (long) (response.getObjectSize() + 4));
                    }
                }
                processRequestResult(requestInfo, error);
            } else if (!this.reuploadingCdn) {
                clearOperaion(requestInfo, false);
                this.reuploadingCdn = true;
                TL_upload_cdnFileReuploadNeeded res2 = (TL_upload_cdnFileReuploadNeeded) response;
                TL_upload_reuploadCdnFile req = new TL_upload_reuploadCdnFile();
                req.file_token = this.cdnToken;
                req.request_token = res2.request_token;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new FileLoadOperation$$Lambda$13(this, requestInfo), null, null, 0, this.datacenterId, 1, true);
            }
        }
    }

    final /* synthetic */ void lambda$null$11$FileLoadOperation(RequestInfo requestInfo, TLObject response1, TL_error error1) {
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
            clearOperaion(requestInfo, false);
            startDownloadRequest();
        } else {
            onFail(false, 0);
        }
    }

    public void setDelegate(FileLoadOperationDelegate delegate) {
        this.delegate = delegate;
    }
}
