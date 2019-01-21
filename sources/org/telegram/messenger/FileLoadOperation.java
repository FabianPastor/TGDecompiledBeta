package org.telegram.messenger;

import android.util.SparseArray;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import org.telegram.tgnet.ConnectionsManager;
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
    private byte[] iv;
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
            this.currentType = 50331648;
        } else if (webDocument.mime_type.startsWith("video/")) {
            this.currentType = 33554432;
        } else {
            this.currentType = 67108864;
        }
        this.allowDisordererFileSave = true;
        this.ext = ImageLoader.getHttpUrlExtension(webDocument.url, defaultExt);
    }

    /* JADX WARNING: Removed duplicated region for block: B:36:0x00e7 A:{Catch:{ Exception -> 0x00d6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:17:0x007f A:{Catch:{ Exception -> 0x00d6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:50:? A:{SYNTHETIC, RETURN, Catch:{ Exception -> 0x00d6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x008b A:{Catch:{ Exception -> 0x00d6 }} */
    public FileLoadOperation(org.telegram.tgnet.TLRPC.Document r12, java.lang.Object r13) {
        /*
        r11 = this;
        r3 = -1;
        r5 = 1;
        r4 = 0;
        r11.<init>();
        r11.state = r4;
        r11.parentObject = r13;	 Catch:{ Exception -> 0x00d6 }
        r6 = r12 instanceof org.telegram.tgnet.TLRPC.TL_documentEncrypted;	 Catch:{ Exception -> 0x00d6 }
        if (r6 == 0) goto L_0x00a1;
    L_0x000e:
        r6 = new org.telegram.tgnet.TLRPC$TL_inputEncryptedFileLocation;	 Catch:{ Exception -> 0x00d6 }
        r6.<init>();	 Catch:{ Exception -> 0x00d6 }
        r11.location = r6;	 Catch:{ Exception -> 0x00d6 }
        r6 = r11.location;	 Catch:{ Exception -> 0x00d6 }
        r8 = r12.id;	 Catch:{ Exception -> 0x00d6 }
        r6.id = r8;	 Catch:{ Exception -> 0x00d6 }
        r6 = r11.location;	 Catch:{ Exception -> 0x00d6 }
        r8 = r12.access_hash;	 Catch:{ Exception -> 0x00d6 }
        r6.access_hash = r8;	 Catch:{ Exception -> 0x00d6 }
        r6 = r12.dc_id;	 Catch:{ Exception -> 0x00d6 }
        r11.datacenterId = r6;	 Catch:{ Exception -> 0x00d6 }
        r11.initialDatacenterId = r6;	 Catch:{ Exception -> 0x00d6 }
        r6 = 32;
        r6 = new byte[r6];	 Catch:{ Exception -> 0x00d6 }
        r11.iv = r6;	 Catch:{ Exception -> 0x00d6 }
        r6 = r12.iv;	 Catch:{ Exception -> 0x00d6 }
        r7 = 0;
        r8 = r11.iv;	 Catch:{ Exception -> 0x00d6 }
        r9 = 0;
        r10 = r11.iv;	 Catch:{ Exception -> 0x00d6 }
        r10 = r10.length;	 Catch:{ Exception -> 0x00d6 }
        java.lang.System.arraycopy(r6, r7, r8, r9, r10);	 Catch:{ Exception -> 0x00d6 }
        r6 = r12.key;	 Catch:{ Exception -> 0x00d6 }
        r11.key = r6;	 Catch:{ Exception -> 0x00d6 }
    L_0x003d:
        r6 = r12.size;	 Catch:{ Exception -> 0x00d6 }
        r11.totalBytesCount = r6;	 Catch:{ Exception -> 0x00d6 }
        r6 = r11.key;	 Catch:{ Exception -> 0x00d6 }
        if (r6 == 0) goto L_0x005b;
    L_0x0045:
        r2 = 0;
        r6 = r11.totalBytesCount;	 Catch:{ Exception -> 0x00d6 }
        r6 = r6 % 16;
        if (r6 == 0) goto L_0x005b;
    L_0x004c:
        r6 = r11.totalBytesCount;	 Catch:{ Exception -> 0x00d6 }
        r6 = r6 % 16;
        r6 = 16 - r6;
        r11.bytesCountPadding = r6;	 Catch:{ Exception -> 0x00d6 }
        r6 = r11.totalBytesCount;	 Catch:{ Exception -> 0x00d6 }
        r7 = r11.bytesCountPadding;	 Catch:{ Exception -> 0x00d6 }
        r6 = r6 + r7;
        r11.totalBytesCount = r6;	 Catch:{ Exception -> 0x00d6 }
    L_0x005b:
        r6 = org.telegram.messenger.FileLoader.getDocumentFileName(r12);	 Catch:{ Exception -> 0x00d6 }
        r11.ext = r6;	 Catch:{ Exception -> 0x00d6 }
        r6 = r11.ext;	 Catch:{ Exception -> 0x00d6 }
        if (r6 == 0) goto L_0x006f;
    L_0x0065:
        r6 = r11.ext;	 Catch:{ Exception -> 0x00d6 }
        r7 = 46;
        r1 = r6.lastIndexOf(r7);	 Catch:{ Exception -> 0x00d6 }
        if (r1 != r3) goto L_0x00de;
    L_0x006f:
        r6 = "";
        r11.ext = r6;	 Catch:{ Exception -> 0x00d6 }
    L_0x0074:
        r6 = "audio/ogg";
        r7 = r12.mime_type;	 Catch:{ Exception -> 0x00d6 }
        r6 = r6.equals(r7);	 Catch:{ Exception -> 0x00d6 }
        if (r6 == 0) goto L_0x00e7;
    L_0x007f:
        r6 = 50331648; // 0x3000000 float:3.761582E-37 double:2.4867138E-316;
        r11.currentType = r6;	 Catch:{ Exception -> 0x00d6 }
    L_0x0083:
        r6 = r11.ext;	 Catch:{ Exception -> 0x00d6 }
        r6 = r6.length();	 Catch:{ Exception -> 0x00d6 }
        if (r6 > r5) goto L_0x00a0;
    L_0x008b:
        r6 = r12.mime_type;	 Catch:{ Exception -> 0x00d6 }
        if (r6 == 0) goto L_0x011e;
    L_0x008f:
        r6 = r12.mime_type;	 Catch:{ Exception -> 0x00d6 }
        r7 = r6.hashCode();	 Catch:{ Exception -> 0x00d6 }
        switch(r7) {
            case 187091926: goto L_0x0107;
            case 1331848029: goto L_0x00fc;
            default: goto L_0x0098;
        };	 Catch:{ Exception -> 0x00d6 }
    L_0x0098:
        switch(r3) {
            case 0: goto L_0x0112;
            case 1: goto L_0x0118;
            default: goto L_0x009b;
        };	 Catch:{ Exception -> 0x00d6 }
    L_0x009b:
        r3 = "";
        r11.ext = r3;	 Catch:{ Exception -> 0x00d6 }
    L_0x00a0:
        return;
    L_0x00a1:
        r6 = r12 instanceof org.telegram.tgnet.TLRPC.TL_document;	 Catch:{ Exception -> 0x00d6 }
        if (r6 == 0) goto L_0x003d;
    L_0x00a5:
        r6 = new org.telegram.tgnet.TLRPC$TL_inputDocumentFileLocation;	 Catch:{ Exception -> 0x00d6 }
        r6.<init>();	 Catch:{ Exception -> 0x00d6 }
        r11.location = r6;	 Catch:{ Exception -> 0x00d6 }
        r6 = r11.location;	 Catch:{ Exception -> 0x00d6 }
        r8 = r12.id;	 Catch:{ Exception -> 0x00d6 }
        r6.id = r8;	 Catch:{ Exception -> 0x00d6 }
        r6 = r11.location;	 Catch:{ Exception -> 0x00d6 }
        r8 = r12.access_hash;	 Catch:{ Exception -> 0x00d6 }
        r6.access_hash = r8;	 Catch:{ Exception -> 0x00d6 }
        r6 = r11.location;	 Catch:{ Exception -> 0x00d6 }
        r7 = r12.file_reference;	 Catch:{ Exception -> 0x00d6 }
        r6.file_reference = r7;	 Catch:{ Exception -> 0x00d6 }
        r6 = r11.location;	 Catch:{ Exception -> 0x00d6 }
        r6 = r6.file_reference;	 Catch:{ Exception -> 0x00d6 }
        if (r6 != 0) goto L_0x00cb;
    L_0x00c4:
        r6 = r11.location;	 Catch:{ Exception -> 0x00d6 }
        r7 = 0;
        r7 = new byte[r7];	 Catch:{ Exception -> 0x00d6 }
        r6.file_reference = r7;	 Catch:{ Exception -> 0x00d6 }
    L_0x00cb:
        r6 = r12.dc_id;	 Catch:{ Exception -> 0x00d6 }
        r11.datacenterId = r6;	 Catch:{ Exception -> 0x00d6 }
        r11.initialDatacenterId = r6;	 Catch:{ Exception -> 0x00d6 }
        r6 = 1;
        r11.allowDisordererFileSave = r6;	 Catch:{ Exception -> 0x00d6 }
        goto L_0x003d;
    L_0x00d6:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
        r11.onFail(r5, r4);
        goto L_0x00a0;
    L_0x00de:
        r6 = r11.ext;	 Catch:{ Exception -> 0x00d6 }
        r6 = r6.substring(r1);	 Catch:{ Exception -> 0x00d6 }
        r11.ext = r6;	 Catch:{ Exception -> 0x00d6 }
        goto L_0x0074;
    L_0x00e7:
        r6 = "video/mp4";
        r7 = r12.mime_type;	 Catch:{ Exception -> 0x00d6 }
        r6 = r6.equals(r7);	 Catch:{ Exception -> 0x00d6 }
        if (r6 == 0) goto L_0x00f7;
    L_0x00f2:
        r6 = 33554432; // 0x2000000 float:9.403955E-38 double:1.6578092E-316;
        r11.currentType = r6;	 Catch:{ Exception -> 0x00d6 }
        goto L_0x0083;
    L_0x00f7:
        r6 = 67108864; // 0x4000000 float:1.5046328E-36 double:3.31561842E-316;
        r11.currentType = r6;	 Catch:{ Exception -> 0x00d6 }
        goto L_0x0083;
    L_0x00fc:
        r7 = "video/mp4";
        r6 = r6.equals(r7);	 Catch:{ Exception -> 0x00d6 }
        if (r6 == 0) goto L_0x0098;
    L_0x0105:
        r3 = r4;
        goto L_0x0098;
    L_0x0107:
        r7 = "audio/ogg";
        r6 = r6.equals(r7);	 Catch:{ Exception -> 0x00d6 }
        if (r6 == 0) goto L_0x0098;
    L_0x0110:
        r3 = r5;
        goto L_0x0098;
    L_0x0112:
        r3 = ".mp4";
        r11.ext = r3;	 Catch:{ Exception -> 0x00d6 }
        goto L_0x00a0;
    L_0x0118:
        r3 = ".ogg";
        r11.ext = r3;	 Catch:{ Exception -> 0x00d6 }
        goto L_0x00a0;
    L_0x011e:
        r3 = "";
        r11.ext = r3;	 Catch:{ Exception -> 0x00d6 }
        goto L_0x00a0;
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
                    FileLog.e(e);
                }
                if (this.streamListeners != null) {
                    count = this.streamListeners.size();
                    for (a = 0; a < count; a++) {
                        ((FileStreamLoadOperation) this.streamListeners.get(a)).newDataAvailable();
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
        } catch (Throwable e) {
            FileLog.e(e);
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
                        FileLog.e(e2);
                    }
                } else {
                    file.read(this.encryptKey, 0, 32);
                    file.read(this.encryptIv, 0, 16);
                }
                try {
                    file.getChannel().close();
                } catch (Throwable e22) {
                    FileLog.e(e22);
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
                    FileLog.e(e222);
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
                FileLog.d("start loading file to temp = " + this.cacheFileTemp + " final = " + this.cacheFileFinal);
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
                } catch (Throwable e2222) {
                    FileLog.e(e2222);
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
                FileLog.e(e22222);
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
                    FileLog.e(e);
                }
                this.fileOutputStream.close();
                this.fileOutputStream = null;
            }
        } catch (Throwable e2) {
            FileLog.e(e2);
        }
        try {
            if (this.fileReadStream != null) {
                try {
                    this.fileReadStream.getChannel().close();
                } catch (Throwable e22) {
                    FileLog.e(e22);
                }
                this.fileReadStream.close();
                this.fileReadStream = null;
            }
        } catch (Throwable e222) {
            FileLog.e(e222);
        }
        try {
            if (this.filePartsStream != null) {
                try {
                    this.filePartsStream.getChannel().close();
                } catch (Throwable e2222) {
                    FileLog.e(e2222);
                }
                this.filePartsStream.close();
                this.filePartsStream = null;
            }
        } catch (Throwable e22222) {
            FileLog.e(e22222);
        }
        try {
            if (this.fiv != null) {
                this.fiv.close();
                this.fiv = null;
            }
        } catch (Throwable e222222) {
            FileLog.e(e222222);
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
            this.delegate.didFinishLoadingFile(this, this.cacheFileFinal);
            if (!increment) {
                return;
            }
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
        if (r30 != 0) goto L_0x04b4;
    L_0x0042:
        r0 = r28;
        r2 = r0.notLoadedBytesRanges;	 Catch:{ Exception -> 0x03c6 }
        if (r2 != 0) goto L_0x0057;
    L_0x0048:
        r0 = r28;
        r2 = r0.downloadedBytes;	 Catch:{ Exception -> 0x03c6 }
        r3 = r29.offset;	 Catch:{ Exception -> 0x03c6 }
        if (r2 == r3) goto L_0x0057;
    L_0x0052:
        r28.delayRequestInfo(r29);	 Catch:{ Exception -> 0x03c6 }
        r2 = 0;
        goto L_0x0036;
    L_0x0057:
        r2 = r29.response;	 Catch:{ Exception -> 0x03c6 }
        if (r2 == 0) goto L_0x0073;
    L_0x005d:
        r2 = r29.response;	 Catch:{ Exception -> 0x03c6 }
        r11 = r2.bytes;	 Catch:{ Exception -> 0x03c6 }
    L_0x0063:
        if (r11 == 0) goto L_0x006b;
    L_0x0065:
        r2 = r11.limit();	 Catch:{ Exception -> 0x03c6 }
        if (r2 != 0) goto L_0x008f;
    L_0x006b:
        r2 = 1;
        r0 = r28;
        r0.onFinishLoadingFile(r2);	 Catch:{ Exception -> 0x03c6 }
        r2 = 0;
        goto L_0x0036;
    L_0x0073:
        r2 = r29.responseWeb;	 Catch:{ Exception -> 0x03c6 }
        if (r2 == 0) goto L_0x0080;
    L_0x0079:
        r2 = r29.responseWeb;	 Catch:{ Exception -> 0x03c6 }
        r11 = r2.bytes;	 Catch:{ Exception -> 0x03c6 }
        goto L_0x0063;
    L_0x0080:
        r2 = r29.responseCdn;	 Catch:{ Exception -> 0x03c6 }
        if (r2 == 0) goto L_0x008d;
    L_0x0086:
        r2 = r29.responseCdn;	 Catch:{ Exception -> 0x03c6 }
        r11 = r2.bytes;	 Catch:{ Exception -> 0x03c6 }
        goto L_0x0063;
    L_0x008d:
        r11 = 0;
        goto L_0x0063;
    L_0x008f:
        r15 = r11.limit();	 Catch:{ Exception -> 0x03c6 }
        r0 = r28;
        r2 = r0.isCdn;	 Catch:{ Exception -> 0x03c6 }
        if (r2 == 0) goto L_0x00cb;
    L_0x0099:
        r2 = r29.offset;	 Catch:{ Exception -> 0x03c6 }
        r3 = 131072; // 0x20000 float:1.83671E-40 double:6.47582E-319;
        r12 = r2 / r3;
        r2 = 131072; // 0x20000 float:1.83671E-40 double:6.47582E-319;
        r19 = r12 * r2;
        r0 = r28;
        r2 = r0.cdnHashes;	 Catch:{ Exception -> 0x03c6 }
        if (r2 == 0) goto L_0x00c8;
    L_0x00ab:
        r0 = r28;
        r2 = r0.cdnHashes;	 Catch:{ Exception -> 0x03c6 }
        r0 = r19;
        r2 = r2.get(r0);	 Catch:{ Exception -> 0x03c6 }
        r2 = (org.telegram.tgnet.TLRPC.TL_fileHash) r2;	 Catch:{ Exception -> 0x03c6 }
        r21 = r2;
    L_0x00b9:
        if (r21 != 0) goto L_0x00cb;
    L_0x00bb:
        r28.delayRequestInfo(r29);	 Catch:{ Exception -> 0x03c6 }
        r0 = r28;
        r1 = r19;
        r0.requestFileOffsets(r1);	 Catch:{ Exception -> 0x03c6 }
        r2 = 1;
        goto L_0x0036;
    L_0x00c8:
        r21 = 0;
        goto L_0x00b9;
    L_0x00cb:
        r2 = r29.responseCdn;	 Catch:{ Exception -> 0x03c6 }
        if (r2 == 0) goto L_0x011d;
    L_0x00d1:
        r2 = r29.offset;	 Catch:{ Exception -> 0x03c6 }
        r22 = r2 / 16;
        r0 = r28;
        r2 = r0.cdnIv;	 Catch:{ Exception -> 0x03c6 }
        r3 = 15;
        r0 = r22;
        r4 = r0 & 255;
        r4 = (byte) r4;	 Catch:{ Exception -> 0x03c6 }
        r2[r3] = r4;	 Catch:{ Exception -> 0x03c6 }
        r0 = r28;
        r2 = r0.cdnIv;	 Catch:{ Exception -> 0x03c6 }
        r3 = 14;
        r4 = r22 >> 8;
        r4 = r4 & 255;
        r4 = (byte) r4;	 Catch:{ Exception -> 0x03c6 }
        r2[r3] = r4;	 Catch:{ Exception -> 0x03c6 }
        r0 = r28;
        r2 = r0.cdnIv;	 Catch:{ Exception -> 0x03c6 }
        r3 = 13;
        r4 = r22 >> 16;
        r4 = r4 & 255;
        r4 = (byte) r4;	 Catch:{ Exception -> 0x03c6 }
        r2[r3] = r4;	 Catch:{ Exception -> 0x03c6 }
        r0 = r28;
        r2 = r0.cdnIv;	 Catch:{ Exception -> 0x03c6 }
        r3 = 12;
        r4 = r22 >> 24;
        r4 = r4 & 255;
        r4 = (byte) r4;	 Catch:{ Exception -> 0x03c6 }
        r2[r3] = r4;	 Catch:{ Exception -> 0x03c6 }
        r2 = r11.buffer;	 Catch:{ Exception -> 0x03c6 }
        r0 = r28;
        r3 = r0.cdnKey;	 Catch:{ Exception -> 0x03c6 }
        r0 = r28;
        r4 = r0.cdnIv;	 Catch:{ Exception -> 0x03c6 }
        r5 = 0;
        r6 = r11.limit();	 Catch:{ Exception -> 0x03c6 }
        org.telegram.messenger.Utilities.aesCtrDecryption(r2, r3, r4, r5, r6);	 Catch:{ Exception -> 0x03c6 }
    L_0x011d:
        r0 = r28;
        r2 = r0.downloadedBytes;	 Catch:{ Exception -> 0x03c6 }
        r2 = r2 + r15;
        r0 = r28;
        r0.downloadedBytes = r2;	 Catch:{ Exception -> 0x03c6 }
        r0 = r28;
        r2 = r0.totalBytesCount;	 Catch:{ Exception -> 0x03c6 }
        if (r2 <= 0) goto L_0x035f;
    L_0x012c:
        r0 = r28;
        r2 = r0.downloadedBytes;	 Catch:{ Exception -> 0x03c6 }
        r0 = r28;
        r3 = r0.totalBytesCount;	 Catch:{ Exception -> 0x03c6 }
        if (r2 < r3) goto L_0x035b;
    L_0x0136:
        r20 = 1;
    L_0x0138:
        r0 = r28;
        r2 = r0.key;	 Catch:{ Exception -> 0x03c6 }
        if (r2 == 0) goto L_0x0166;
    L_0x013e:
        r2 = r11.buffer;	 Catch:{ Exception -> 0x03c6 }
        r0 = r28;
        r3 = r0.key;	 Catch:{ Exception -> 0x03c6 }
        r0 = r28;
        r4 = r0.iv;	 Catch:{ Exception -> 0x03c6 }
        r5 = 0;
        r6 = 1;
        r7 = 0;
        r8 = r11.limit();	 Catch:{ Exception -> 0x03c6 }
        org.telegram.messenger.Utilities.aesIgeEncryption(r2, r3, r4, r5, r6, r7, r8);	 Catch:{ Exception -> 0x03c6 }
        if (r20 == 0) goto L_0x0166;
    L_0x0154:
        r0 = r28;
        r2 = r0.bytesCountPadding;	 Catch:{ Exception -> 0x03c6 }
        if (r2 == 0) goto L_0x0166;
    L_0x015a:
        r2 = r11.limit();	 Catch:{ Exception -> 0x03c6 }
        r0 = r28;
        r3 = r0.bytesCountPadding;	 Catch:{ Exception -> 0x03c6 }
        r2 = r2 - r3;
        r11.limit(r2);	 Catch:{ Exception -> 0x03c6 }
    L_0x0166:
        r0 = r28;
        r2 = r0.encryptFile;	 Catch:{ Exception -> 0x03c6 }
        if (r2 == 0) goto L_0x01b8;
    L_0x016c:
        r2 = r29.offset;	 Catch:{ Exception -> 0x03c6 }
        r22 = r2 / 16;
        r0 = r28;
        r2 = r0.encryptIv;	 Catch:{ Exception -> 0x03c6 }
        r3 = 15;
        r0 = r22;
        r4 = r0 & 255;
        r4 = (byte) r4;	 Catch:{ Exception -> 0x03c6 }
        r2[r3] = r4;	 Catch:{ Exception -> 0x03c6 }
        r0 = r28;
        r2 = r0.encryptIv;	 Catch:{ Exception -> 0x03c6 }
        r3 = 14;
        r4 = r22 >> 8;
        r4 = r4 & 255;
        r4 = (byte) r4;	 Catch:{ Exception -> 0x03c6 }
        r2[r3] = r4;	 Catch:{ Exception -> 0x03c6 }
        r0 = r28;
        r2 = r0.encryptIv;	 Catch:{ Exception -> 0x03c6 }
        r3 = 13;
        r4 = r22 >> 16;
        r4 = r4 & 255;
        r4 = (byte) r4;	 Catch:{ Exception -> 0x03c6 }
        r2[r3] = r4;	 Catch:{ Exception -> 0x03c6 }
        r0 = r28;
        r2 = r0.encryptIv;	 Catch:{ Exception -> 0x03c6 }
        r3 = 12;
        r4 = r22 >> 24;
        r4 = r4 & 255;
        r4 = (byte) r4;	 Catch:{ Exception -> 0x03c6 }
        r2[r3] = r4;	 Catch:{ Exception -> 0x03c6 }
        r2 = r11.buffer;	 Catch:{ Exception -> 0x03c6 }
        r0 = r28;
        r3 = r0.encryptKey;	 Catch:{ Exception -> 0x03c6 }
        r0 = r28;
        r4 = r0.encryptIv;	 Catch:{ Exception -> 0x03c6 }
        r5 = 0;
        r6 = r11.limit();	 Catch:{ Exception -> 0x03c6 }
        org.telegram.messenger.Utilities.aesCtrDecryption(r2, r3, r4, r5, r6);	 Catch:{ Exception -> 0x03c6 }
    L_0x01b8:
        r0 = r28;
        r2 = r0.notLoadedBytesRanges;	 Catch:{ Exception -> 0x03c6 }
        if (r2 == 0) goto L_0x01f8;
    L_0x01be:
        r0 = r28;
        r2 = r0.fileOutputStream;	 Catch:{ Exception -> 0x03c6 }
        r3 = r29.offset;	 Catch:{ Exception -> 0x03c6 }
        r4 = (long) r3;	 Catch:{ Exception -> 0x03c6 }
        r2.seek(r4);	 Catch:{ Exception -> 0x03c6 }
        r2 = org.telegram.messenger.BuildVars.DEBUG_VERSION;	 Catch:{ Exception -> 0x03c6 }
        if (r2 == 0) goto L_0x01f8;
    L_0x01ce:
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x03c6 }
        r2.<init>();	 Catch:{ Exception -> 0x03c6 }
        r3 = "save file part ";
        r2 = r2.append(r3);	 Catch:{ Exception -> 0x03c6 }
        r0 = r28;
        r3 = r0.cacheFileFinal;	 Catch:{ Exception -> 0x03c6 }
        r2 = r2.append(r3);	 Catch:{ Exception -> 0x03c6 }
        r3 = " offset ";
        r2 = r2.append(r3);	 Catch:{ Exception -> 0x03c6 }
        r3 = r29.offset;	 Catch:{ Exception -> 0x03c6 }
        r2 = r2.append(r3);	 Catch:{ Exception -> 0x03c6 }
        r2 = r2.toString();	 Catch:{ Exception -> 0x03c6 }
        org.telegram.messenger.FileLog.d(r2);	 Catch:{ Exception -> 0x03c6 }
    L_0x01f8:
        r0 = r28;
        r2 = r0.fileOutputStream;	 Catch:{ Exception -> 0x03c6 }
        r13 = r2.getChannel();	 Catch:{ Exception -> 0x03c6 }
        r2 = r11.buffer;	 Catch:{ Exception -> 0x03c6 }
        r13.write(r2);	 Catch:{ Exception -> 0x03c6 }
        r0 = r28;
        r2 = r0.notLoadedBytesRanges;	 Catch:{ Exception -> 0x03c6 }
        r3 = r29.offset;	 Catch:{ Exception -> 0x03c6 }
        r4 = r29.offset;	 Catch:{ Exception -> 0x03c6 }
        r4 = r4 + r15;
        r5 = 1;
        r0 = r28;
        r0.addPart(r2, r3, r4, r5);	 Catch:{ Exception -> 0x03c6 }
        r0 = r28;
        r2 = r0.isCdn;	 Catch:{ Exception -> 0x03c6 }
        if (r2 == 0) goto L_0x03e9;
    L_0x021e:
        r2 = r29.offset;	 Catch:{ Exception -> 0x03c6 }
        r3 = 131072; // 0x20000 float:1.83671E-40 double:6.47582E-319;
        r12 = r2 / r3;
        r0 = r28;
        r2 = r0.notCheckedCdnRanges;	 Catch:{ Exception -> 0x03c6 }
        r26 = r2.size();	 Catch:{ Exception -> 0x03c6 }
        r14 = 1;
        r9 = 0;
    L_0x0230:
        r0 = r26;
        if (r9 >= r0) goto L_0x024b;
    L_0x0234:
        r0 = r28;
        r2 = r0.notCheckedCdnRanges;	 Catch:{ Exception -> 0x03c6 }
        r23 = r2.get(r9);	 Catch:{ Exception -> 0x03c6 }
        r23 = (org.telegram.messenger.FileLoadOperation.Range) r23;	 Catch:{ Exception -> 0x03c6 }
        r2 = r23.start;	 Catch:{ Exception -> 0x03c6 }
        if (r2 > r12) goto L_0x0391;
    L_0x0244:
        r2 = r23.end;	 Catch:{ Exception -> 0x03c6 }
        if (r12 > r2) goto L_0x0391;
    L_0x024a:
        r14 = 0;
    L_0x024b:
        if (r14 != 0) goto L_0x03e9;
    L_0x024d:
        r2 = 131072; // 0x20000 float:1.83671E-40 double:6.47582E-319;
        r19 = r12 * r2;
        r0 = r28;
        r2 = r0.notLoadedBytesRanges;	 Catch:{ Exception -> 0x03c6 }
        r3 = 131072; // 0x20000 float:1.83671E-40 double:6.47582E-319;
        r0 = r28;
        r1 = r19;
        r10 = r0.getDownloadedLengthFromOffsetInternal(r2, r1, r3);	 Catch:{ Exception -> 0x03c6 }
        if (r10 == 0) goto L_0x03e9;
    L_0x0261:
        r2 = 131072; // 0x20000 float:1.83671E-40 double:6.47582E-319;
        if (r10 == r2) goto L_0x027b;
    L_0x0265:
        r0 = r28;
        r2 = r0.totalBytesCount;	 Catch:{ Exception -> 0x03c6 }
        if (r2 <= 0) goto L_0x0273;
    L_0x026b:
        r0 = r28;
        r2 = r0.totalBytesCount;	 Catch:{ Exception -> 0x03c6 }
        r2 = r2 - r19;
        if (r10 == r2) goto L_0x027b;
    L_0x0273:
        r0 = r28;
        r2 = r0.totalBytesCount;	 Catch:{ Exception -> 0x03c6 }
        if (r2 > 0) goto L_0x03e9;
    L_0x0279:
        if (r20 == 0) goto L_0x03e9;
    L_0x027b:
        r0 = r28;
        r2 = r0.cdnHashes;	 Catch:{ Exception -> 0x03c6 }
        r0 = r19;
        r21 = r2.get(r0);	 Catch:{ Exception -> 0x03c6 }
        r21 = (org.telegram.tgnet.TLRPC.TL_fileHash) r21;	 Catch:{ Exception -> 0x03c6 }
        r0 = r28;
        r2 = r0.fileReadStream;	 Catch:{ Exception -> 0x03c6 }
        if (r2 != 0) goto L_0x02a5;
    L_0x028d:
        r2 = 131072; // 0x20000 float:1.83671E-40 double:6.47582E-319;
        r2 = new byte[r2];	 Catch:{ Exception -> 0x03c6 }
        r0 = r28;
        r0.cdnCheckBytes = r2;	 Catch:{ Exception -> 0x03c6 }
        r2 = new java.io.RandomAccessFile;	 Catch:{ Exception -> 0x03c6 }
        r0 = r28;
        r3 = r0.cacheFileTemp;	 Catch:{ Exception -> 0x03c6 }
        r4 = "r";
        r2.<init>(r3, r4);	 Catch:{ Exception -> 0x03c6 }
        r0 = r28;
        r0.fileReadStream = r2;	 Catch:{ Exception -> 0x03c6 }
    L_0x02a5:
        r0 = r28;
        r2 = r0.fileReadStream;	 Catch:{ Exception -> 0x03c6 }
        r0 = r19;
        r4 = (long) r0;	 Catch:{ Exception -> 0x03c6 }
        r2.seek(r4);	 Catch:{ Exception -> 0x03c6 }
        r0 = r28;
        r2 = r0.fileReadStream;	 Catch:{ Exception -> 0x03c6 }
        r0 = r28;
        r3 = r0.cdnCheckBytes;	 Catch:{ Exception -> 0x03c6 }
        r4 = 0;
        r2.readFully(r3, r4, r10);	 Catch:{ Exception -> 0x03c6 }
        r0 = r28;
        r2 = r0.cdnCheckBytes;	 Catch:{ Exception -> 0x03c6 }
        r3 = 0;
        r25 = org.telegram.messenger.Utilities.computeSHA256(r2, r3, r10);	 Catch:{ Exception -> 0x03c6 }
        r0 = r21;
        r2 = r0.hash;	 Catch:{ Exception -> 0x03c6 }
        r0 = r25;
        r2 = java.util.Arrays.equals(r0, r2);	 Catch:{ Exception -> 0x03c6 }
        if (r2 != 0) goto L_0x03d4;
    L_0x02d0:
        r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x03c6 }
        if (r2 == 0) goto L_0x034a;
    L_0x02d4:
        r0 = r28;
        r2 = r0.location;	 Catch:{ Exception -> 0x03c6 }
        if (r2 == 0) goto L_0x0395;
    L_0x02da:
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x03c6 }
        r2.<init>();	 Catch:{ Exception -> 0x03c6 }
        r3 = "invalid cdn hash ";
        r2 = r2.append(r3);	 Catch:{ Exception -> 0x03c6 }
        r0 = r28;
        r3 = r0.location;	 Catch:{ Exception -> 0x03c6 }
        r2 = r2.append(r3);	 Catch:{ Exception -> 0x03c6 }
        r3 = " id = ";
        r2 = r2.append(r3);	 Catch:{ Exception -> 0x03c6 }
        r0 = r28;
        r3 = r0.location;	 Catch:{ Exception -> 0x03c6 }
        r4 = r3.id;	 Catch:{ Exception -> 0x03c6 }
        r2 = r2.append(r4);	 Catch:{ Exception -> 0x03c6 }
        r3 = " local_id = ";
        r2 = r2.append(r3);	 Catch:{ Exception -> 0x03c6 }
        r0 = r28;
        r3 = r0.location;	 Catch:{ Exception -> 0x03c6 }
        r3 = r3.local_id;	 Catch:{ Exception -> 0x03c6 }
        r2 = r2.append(r3);	 Catch:{ Exception -> 0x03c6 }
        r3 = " access_hash = ";
        r2 = r2.append(r3);	 Catch:{ Exception -> 0x03c6 }
        r0 = r28;
        r3 = r0.location;	 Catch:{ Exception -> 0x03c6 }
        r4 = r3.access_hash;	 Catch:{ Exception -> 0x03c6 }
        r2 = r2.append(r4);	 Catch:{ Exception -> 0x03c6 }
        r3 = " volume_id = ";
        r2 = r2.append(r3);	 Catch:{ Exception -> 0x03c6 }
        r0 = r28;
        r3 = r0.location;	 Catch:{ Exception -> 0x03c6 }
        r4 = r3.volume_id;	 Catch:{ Exception -> 0x03c6 }
        r2 = r2.append(r4);	 Catch:{ Exception -> 0x03c6 }
        r3 = " secret = ";
        r2 = r2.append(r3);	 Catch:{ Exception -> 0x03c6 }
        r0 = r28;
        r3 = r0.location;	 Catch:{ Exception -> 0x03c6 }
        r4 = r3.secret;	 Catch:{ Exception -> 0x03c6 }
        r2 = r2.append(r4);	 Catch:{ Exception -> 0x03c6 }
        r2 = r2.toString();	 Catch:{ Exception -> 0x03c6 }
        org.telegram.messenger.FileLog.e(r2);	 Catch:{ Exception -> 0x03c6 }
    L_0x034a:
        r2 = 0;
        r3 = 0;
        r0 = r28;
        r0.onFail(r2, r3);	 Catch:{ Exception -> 0x03c6 }
        r0 = r28;
        r2 = r0.cacheFileTemp;	 Catch:{ Exception -> 0x03c6 }
        r2.delete();	 Catch:{ Exception -> 0x03c6 }
        r2 = 0;
        goto L_0x0036;
    L_0x035b:
        r20 = 0;
        goto L_0x0138;
    L_0x035f:
        r0 = r28;
        r2 = r0.currentDownloadChunkSize;	 Catch:{ Exception -> 0x03c6 }
        if (r15 != r2) goto L_0x038a;
    L_0x0365:
        r0 = r28;
        r2 = r0.totalBytesCount;	 Catch:{ Exception -> 0x03c6 }
        r0 = r28;
        r3 = r0.downloadedBytes;	 Catch:{ Exception -> 0x03c6 }
        if (r2 == r3) goto L_0x037a;
    L_0x036f:
        r0 = r28;
        r2 = r0.downloadedBytes;	 Catch:{ Exception -> 0x03c6 }
        r0 = r28;
        r3 = r0.currentDownloadChunkSize;	 Catch:{ Exception -> 0x03c6 }
        r2 = r2 % r3;
        if (r2 == 0) goto L_0x038e;
    L_0x037a:
        r0 = r28;
        r2 = r0.totalBytesCount;	 Catch:{ Exception -> 0x03c6 }
        if (r2 <= 0) goto L_0x038a;
    L_0x0380:
        r0 = r28;
        r2 = r0.totalBytesCount;	 Catch:{ Exception -> 0x03c6 }
        r0 = r28;
        r3 = r0.downloadedBytes;	 Catch:{ Exception -> 0x03c6 }
        if (r2 > r3) goto L_0x038e;
    L_0x038a:
        r20 = 1;
    L_0x038c:
        goto L_0x0138;
    L_0x038e:
        r20 = 0;
        goto L_0x038c;
    L_0x0391:
        r9 = r9 + 1;
        goto L_0x0230;
    L_0x0395:
        r0 = r28;
        r2 = r0.webLocation;	 Catch:{ Exception -> 0x03c6 }
        if (r2 == 0) goto L_0x034a;
    L_0x039b:
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x03c6 }
        r2.<init>();	 Catch:{ Exception -> 0x03c6 }
        r3 = "invalid cdn hash  ";
        r2 = r2.append(r3);	 Catch:{ Exception -> 0x03c6 }
        r0 = r28;
        r3 = r0.webLocation;	 Catch:{ Exception -> 0x03c6 }
        r2 = r2.append(r3);	 Catch:{ Exception -> 0x03c6 }
        r3 = " id = ";
        r2 = r2.append(r3);	 Catch:{ Exception -> 0x03c6 }
        r3 = r28.getFileName();	 Catch:{ Exception -> 0x03c6 }
        r2 = r2.append(r3);	 Catch:{ Exception -> 0x03c6 }
        r2 = r2.toString();	 Catch:{ Exception -> 0x03c6 }
        org.telegram.messenger.FileLog.e(r2);	 Catch:{ Exception -> 0x03c6 }
        goto L_0x034a;
    L_0x03c6:
        r17 = move-exception;
        r2 = 0;
        r3 = 0;
        r0 = r28;
        r0.onFail(r2, r3);
        org.telegram.messenger.FileLog.e(r17);
    L_0x03d1:
        r2 = 0;
        goto L_0x0036;
    L_0x03d4:
        r0 = r28;
        r2 = r0.cdnHashes;	 Catch:{ Exception -> 0x03c6 }
        r0 = r19;
        r2.remove(r0);	 Catch:{ Exception -> 0x03c6 }
        r0 = r28;
        r2 = r0.notCheckedCdnRanges;	 Catch:{ Exception -> 0x03c6 }
        r3 = r12 + 1;
        r4 = 0;
        r0 = r28;
        r0.addPart(r2, r12, r3, r4);	 Catch:{ Exception -> 0x03c6 }
    L_0x03e9:
        r0 = r28;
        r2 = r0.fiv;	 Catch:{ Exception -> 0x03c6 }
        if (r2 == 0) goto L_0x0403;
    L_0x03ef:
        r0 = r28;
        r2 = r0.fiv;	 Catch:{ Exception -> 0x03c6 }
        r4 = 0;
        r2.seek(r4);	 Catch:{ Exception -> 0x03c6 }
        r0 = r28;
        r2 = r0.fiv;	 Catch:{ Exception -> 0x03c6 }
        r0 = r28;
        r3 = r0.iv;	 Catch:{ Exception -> 0x03c6 }
        r2.write(r3);	 Catch:{ Exception -> 0x03c6 }
    L_0x0403:
        r0 = r28;
        r2 = r0.totalBytesCount;	 Catch:{ Exception -> 0x03c6 }
        if (r2 <= 0) goto L_0x042d;
    L_0x0409:
        r0 = r28;
        r2 = r0.state;	 Catch:{ Exception -> 0x03c6 }
        r3 = 1;
        if (r2 != r3) goto L_0x042d;
    L_0x0410:
        r28.copytNotLoadedRanges();	 Catch:{ Exception -> 0x03c6 }
        r0 = r28;
        r2 = r0.delegate;	 Catch:{ Exception -> 0x03c6 }
        r3 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r0 = r28;
        r4 = r0.downloadedBytes;	 Catch:{ Exception -> 0x03c6 }
        r4 = (float) r4;	 Catch:{ Exception -> 0x03c6 }
        r0 = r28;
        r5 = r0.totalBytesCount;	 Catch:{ Exception -> 0x03c6 }
        r5 = (float) r5;	 Catch:{ Exception -> 0x03c6 }
        r4 = r4 / r5;
        r3 = java.lang.Math.min(r3, r4);	 Catch:{ Exception -> 0x03c6 }
        r0 = r28;
        r2.didChangedLoadProgress(r0, r3);	 Catch:{ Exception -> 0x03c6 }
    L_0x042d:
        r9 = 0;
    L_0x042e:
        r0 = r28;
        r2 = r0.delayedRequestInfos;	 Catch:{ Exception -> 0x03c6 }
        r2 = r2.size();	 Catch:{ Exception -> 0x03c6 }
        if (r9 >= r2) goto L_0x0478;
    L_0x0438:
        r0 = r28;
        r2 = r0.delayedRequestInfos;	 Catch:{ Exception -> 0x03c6 }
        r16 = r2.get(r9);	 Catch:{ Exception -> 0x03c6 }
        r16 = (org.telegram.messenger.FileLoadOperation.RequestInfo) r16;	 Catch:{ Exception -> 0x03c6 }
        r0 = r28;
        r2 = r0.notLoadedBytesRanges;	 Catch:{ Exception -> 0x03c6 }
        if (r2 != 0) goto L_0x0452;
    L_0x0448:
        r0 = r28;
        r2 = r0.downloadedBytes;	 Catch:{ Exception -> 0x03c6 }
        r3 = r16.offset;	 Catch:{ Exception -> 0x03c6 }
        if (r2 != r3) goto L_0x04ac;
    L_0x0452:
        r0 = r28;
        r2 = r0.delayedRequestInfos;	 Catch:{ Exception -> 0x03c6 }
        r2.remove(r9);	 Catch:{ Exception -> 0x03c6 }
        r2 = 0;
        r0 = r28;
        r1 = r16;
        r2 = r0.processRequestResult(r1, r2);	 Catch:{ Exception -> 0x03c6 }
        if (r2 != 0) goto L_0x0478;
    L_0x0464:
        r2 = r16.response;	 Catch:{ Exception -> 0x03c6 }
        if (r2 == 0) goto L_0x0482;
    L_0x046a:
        r2 = r16.response;	 Catch:{ Exception -> 0x03c6 }
        r3 = 0;
        r2.disableFree = r3;	 Catch:{ Exception -> 0x03c6 }
        r2 = r16.response;	 Catch:{ Exception -> 0x03c6 }
        r2.freeResources();	 Catch:{ Exception -> 0x03c6 }
    L_0x0478:
        if (r20 == 0) goto L_0x04af;
    L_0x047a:
        r2 = 1;
        r0 = r28;
        r0.onFinishLoadingFile(r2);	 Catch:{ Exception -> 0x03c6 }
        goto L_0x03d1;
    L_0x0482:
        r2 = r16.responseWeb;	 Catch:{ Exception -> 0x03c6 }
        if (r2 == 0) goto L_0x0497;
    L_0x0488:
        r2 = r16.responseWeb;	 Catch:{ Exception -> 0x03c6 }
        r3 = 0;
        r2.disableFree = r3;	 Catch:{ Exception -> 0x03c6 }
        r2 = r16.responseWeb;	 Catch:{ Exception -> 0x03c6 }
        r2.freeResources();	 Catch:{ Exception -> 0x03c6 }
        goto L_0x0478;
    L_0x0497:
        r2 = r16.responseCdn;	 Catch:{ Exception -> 0x03c6 }
        if (r2 == 0) goto L_0x0478;
    L_0x049d:
        r2 = r16.responseCdn;	 Catch:{ Exception -> 0x03c6 }
        r3 = 0;
        r2.disableFree = r3;	 Catch:{ Exception -> 0x03c6 }
        r2 = r16.responseCdn;	 Catch:{ Exception -> 0x03c6 }
        r2.freeResources();	 Catch:{ Exception -> 0x03c6 }
        goto L_0x0478;
    L_0x04ac:
        r9 = r9 + 1;
        goto L_0x042e;
    L_0x04af:
        r28.startDownloadRequest();	 Catch:{ Exception -> 0x03c6 }
        goto L_0x03d1;
    L_0x04b4:
        r0 = r30;
        r2 = r0.text;
        r3 = "FILE_MIGRATE_";
        r2 = r2.contains(r3);
        if (r2 == 0) goto L_0x050d;
    L_0x04c1:
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
        r2 = r24.nextInt();	 Catch:{ Exception -> 0x04f3 }
        r27 = java.lang.Integer.valueOf(r2);	 Catch:{ Exception -> 0x04f3 }
    L_0x04e8:
        if (r27 != 0) goto L_0x04f7;
    L_0x04ea:
        r2 = 0;
        r3 = 0;
        r0 = r28;
        r0.onFail(r2, r3);
        goto L_0x03d1;
    L_0x04f3:
        r17 = move-exception;
        r27 = 0;
        goto L_0x04e8;
    L_0x04f7:
        r2 = r27.intValue();
        r0 = r28;
        r0.datacenterId = r2;
        r2 = 0;
        r0 = r28;
        r0.downloadedBytes = r2;
        r0 = r28;
        r0.requestedBytesCount = r2;
        r28.startDownloadRequest();
        goto L_0x03d1;
    L_0x050d:
        r0 = r30;
        r2 = r0.text;
        r3 = "OFFSET_INVALID";
        r2 = r2.contains(r3);
        if (r2 == 0) goto L_0x0543;
    L_0x051a:
        r0 = r28;
        r2 = r0.downloadedBytes;
        r0 = r28;
        r3 = r0.currentDownloadChunkSize;
        r2 = r2 % r3;
        if (r2 != 0) goto L_0x053a;
    L_0x0525:
        r2 = 1;
        r0 = r28;
        r0.onFinishLoadingFile(r2);	 Catch:{ Exception -> 0x052d }
        goto L_0x03d1;
    L_0x052d:
        r17 = move-exception;
        org.telegram.messenger.FileLog.e(r17);
        r2 = 0;
        r3 = 0;
        r0 = r28;
        r0.onFail(r2, r3);
        goto L_0x03d1;
    L_0x053a:
        r2 = 0;
        r3 = 0;
        r0 = r28;
        r0.onFail(r2, r3);
        goto L_0x03d1;
    L_0x0543:
        r0 = r30;
        r2 = r0.text;
        r3 = "RETRY_LIMIT";
        r2 = r2.contains(r3);
        if (r2 == 0) goto L_0x0559;
    L_0x0550:
        r2 = 0;
        r3 = 2;
        r0 = r28;
        r0.onFail(r2, r3);
        goto L_0x03d1;
    L_0x0559:
        r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r2 == 0) goto L_0x05db;
    L_0x055d:
        r0 = r28;
        r2 = r0.location;
        if (r2 == 0) goto L_0x05e4;
    L_0x0563:
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
    L_0x05db:
        r2 = 0;
        r3 = 0;
        r0 = r28;
        r0.onFail(r2, r3);
        goto L_0x03d1;
    L_0x05e4:
        r0 = r28;
        r2 = r0.webLocation;
        if (r2 == 0) goto L_0x05db;
    L_0x05ea:
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
        goto L_0x05db;
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

    private void clearOperaion(RequestInfo currentInfo) {
        int a;
        RequestInfo info;
        int minOffset = Integer.MAX_VALUE;
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
                }
                downloadOffset = this.requestedBytesCount;
                if (this.notRequestedBytesRanges != null) {
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
                    this.notCheckedCdnRanges.add(new Range(0, 12288));
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
