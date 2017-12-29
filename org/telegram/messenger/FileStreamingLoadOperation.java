package org.telegram.messenger;

import android.net.Uri;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.Semaphore;
import org.telegram.messenger.exoplayer2.upstream.DataSource;
import org.telegram.messenger.exoplayer2.upstream.DataSpec;
import org.telegram.messenger.exoplayer2.upstream.TransferListener;
import org.telegram.messenger.exoplayer2.util.MimeTypes;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.InputFileLocation;
import org.telegram.tgnet.TLRPC.TL_cdnFileHash;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputDocumentFileLocation;
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
import org.telegram.tgnet.TLRPC.Vector;

public class FileStreamingLoadOperation implements DataSource {
    private static final int bigFileSizeFrom = 1048576;
    private static final int cdnChunkCheckSize = 131072;
    private static final int downloadChunkSize = 32768;
    private static final int downloadChunkSizeBig = 131072;
    private static final int maxDownloadRequests = 4;
    private static final int maxDownloadRequestsBig = 2;
    private static final int stateDownloading = 1;
    private static final int stateFailed = 2;
    private static final int stateFinished = 3;
    private static final int stateIdle = 0;
    private int bytesCountPadding;
    private long bytesRemaining;
    private File cacheFileFinal;
    private File cacheFileTemp;
    private File cacheIvTemp;
    private byte[] cdnCheckBytes;
    private int cdnDatacenterId;
    private HashMap<Integer, TL_cdnFileHash> cdnHashes;
    private byte[] cdnIv;
    private byte[] cdnKey;
    private byte[] cdnToken;
    private int currentAccount;
    private int currentDownloadChunkSize;
    private int currentMaxDownloadRequests;
    private int currentType;
    private int datacenter_id;
    private ArrayList<RequestInfo> delayedRequestInfos;
    private int downloadedBytes;
    private ArrayList<RequestInfo> downloadedInfos;
    private String ext;
    private RandomAccessFile fileOutputStream;
    private RandomAccessFile fileReadStream;
    private int firstPacketOffset;
    private RandomAccessFile fiv;
    private boolean isCdn;
    private byte[] iv;
    private byte[] key;
    private int lastCheckedCdnPart;
    private final TransferListener<? super FileStreamingLoadOperation> listener;
    private InputFileLocation location;
    private int nextDownloadOffset;
    private boolean opened;
    private int renameRetryCount;
    private ArrayList<RequestInfo> requestInfos;
    private boolean requestingCdnOffsets;
    private int requestsCount;
    private boolean reuploadingCdn;
    private Semaphore semaphore;
    private volatile int state;
    private File storePath;
    private File tempPath;
    private int totalBytesCount;
    private Uri uri;
    private TL_inputWebFileLocation webLocation;

    private static class RequestInfo {
        private NativeByteBuffer bytes;
        private int offset;
        private int remainingBytes;
        private int requestToken;
        private TL_upload_file response;
        private TL_upload_cdnFile responseCdn;
        private TL_upload_webFile responseWeb;

        private RequestInfo() {
        }
    }

    public FileStreamingLoadOperation() {
        this(null);
    }

    public FileStreamingLoadOperation(TransferListener<? super FileStreamingLoadOperation> listener) {
        this.state = 0;
        this.listener = listener;
    }

    public long open(DataSpec dataSpec) throws IOException {
        boolean z = true;
        this.uri = dataSpec.uri;
        this.location = new TL_inputDocumentFileLocation();
        this.location.id = Utilities.parseLong(this.uri.getQueryParameter(TtmlNode.ATTR_ID)).longValue();
        this.location.access_hash = Utilities.parseLong(this.uri.getQueryParameter("hash")).longValue();
        this.datacenter_id = Utilities.parseInt(this.uri.getQueryParameter("dc")).intValue();
        this.totalBytesCount = Utilities.parseInt(this.uri.getQueryParameter("size")).intValue();
        this.currentAccount = Utilities.parseInt(this.uri.getQueryParameter("account")).intValue();
        this.ext = this.uri.getHost();
        String mime_type = this.uri.getQueryParameter("mime");
        if (this.ext != null) {
            int idx = this.ext.lastIndexOf(46);
            if (idx != -1) {
                this.ext = this.ext.substring(idx);
                if ("audio/ogg".equals(mime_type)) {
                    this.currentType = ConnectionsManager.FileTypeAudio;
                } else if (MimeTypes.VIDEO_MP4.equals(mime_type)) {
                    this.currentType = ConnectionsManager.FileTypeFile;
                } else {
                    this.currentType = ConnectionsManager.FileTypeVideo;
                }
                if (this.ext.length() <= 1) {
                    if (mime_type != null) {
                        switch (mime_type.hashCode()) {
                            case 187091926:
                                if (mime_type.equals("audio/ogg")) {
                                    z = true;
                                    break;
                                }
                                break;
                            case 1331848029:
                                if (mime_type.equals(MimeTypes.VIDEO_MP4)) {
                                    z = false;
                                    break;
                                }
                                break;
                        }
                        switch (z) {
                            case false:
                                this.ext = ".mp4";
                                break;
                            case true:
                                this.ext = ".ogg";
                                break;
                            default:
                                this.ext = TtmlNode.ANONYMOUS_REGION_ID;
                                break;
                        }
                    }
                    this.ext = TtmlNode.ANONYMOUS_REGION_ID;
                }
                this.bytesRemaining = dataSpec.length != -1 ? ((long) this.totalBytesCount) - dataSpec.position : dataSpec.length;
                if (this.bytesRemaining >= 0) {
                    throw new EOFException();
                }
                start();
                this.nextDownloadOffset = (((int) dataSpec.position) / this.currentDownloadChunkSize) * this.currentDownloadChunkSize;
                this.firstPacketOffset = (int) (dataSpec.position - ((long) this.nextDownloadOffset));
                this.opened = true;
                if (this.listener != null) {
                    this.listener.onTransferStart(this, dataSpec);
                }
                return this.bytesRemaining;
            }
        }
        this.ext = TtmlNode.ANONYMOUS_REGION_ID;
        if ("audio/ogg".equals(mime_type)) {
            this.currentType = ConnectionsManager.FileTypeAudio;
        } else if (MimeTypes.VIDEO_MP4.equals(mime_type)) {
            this.currentType = ConnectionsManager.FileTypeFile;
        } else {
            this.currentType = ConnectionsManager.FileTypeVideo;
        }
        if (this.ext.length() <= 1) {
            if (mime_type != null) {
                switch (mime_type.hashCode()) {
                    case 187091926:
                        if (mime_type.equals("audio/ogg")) {
                            z = true;
                            break;
                        }
                        break;
                    case 1331848029:
                        if (mime_type.equals(MimeTypes.VIDEO_MP4)) {
                            z = false;
                            break;
                        }
                        break;
                }
                switch (z) {
                    case false:
                        this.ext = ".mp4";
                        break;
                    case true:
                        this.ext = ".ogg";
                        break;
                    default:
                        this.ext = TtmlNode.ANONYMOUS_REGION_ID;
                        break;
                }
            }
            this.ext = TtmlNode.ANONYMOUS_REGION_ID;
        }
        if (dataSpec.length != -1) {
        }
        this.bytesRemaining = dataSpec.length != -1 ? ((long) this.totalBytesCount) - dataSpec.position : dataSpec.length;
        if (this.bytesRemaining >= 0) {
            start();
            this.nextDownloadOffset = (((int) dataSpec.position) / this.currentDownloadChunkSize) * this.currentDownloadChunkSize;
            this.firstPacketOffset = (int) (dataSpec.position - ((long) this.nextDownloadOffset));
            this.opened = true;
            if (this.listener != null) {
                this.listener.onTransferStart(this, dataSpec);
            }
            return this.bytesRemaining;
        }
        throw new EOFException();
    }

    public int read(byte[] buffer, int offset, int readLength) throws IOException {
        if (readLength == 0) {
            return 0;
        }
        if (this.bytesRemaining == 0) {
            return -1;
        }
        try {
            if (!this.downloadedInfos.isEmpty()) {
                RequestInfo requestInfo = (RequestInfo) this.downloadedInfos.get(0);
            }
            if (this.downloadedInfos.isEmpty()) {
                FileLog.d("request offset " + offset + " and len " + readLength);
                startDownloadRequest(1);
                this.semaphore = new Semaphore(0);
                this.semaphore.acquire();
            }
            RequestInfo requestInfo2 = (RequestInfo) this.downloadedInfos.get(0);
            NativeByteBuffer access$100 = requestInfo2.bytes;
            int bytesRead = Math.min(requestInfo2.remainingBytes, readLength);
            access$100.readBytes(buffer, offset, bytesRead, false);
            requestInfo2.remainingBytes = requestInfo2.remainingBytes - bytesRead;
            if (requestInfo2.remainingBytes == 0) {
                this.firstPacketOffset = 0;
                this.nextDownloadOffset += this.currentDownloadChunkSize;
                if (requestInfo2.response != null) {
                    requestInfo2.response.disableFree = false;
                    requestInfo2.response.freeResources();
                } else if (requestInfo2.responseWeb != null) {
                    requestInfo2.responseWeb.disableFree = false;
                    requestInfo2.responseWeb.freeResources();
                } else if (requestInfo2.responseCdn != null) {
                    requestInfo2.responseCdn.disableFree = false;
                    requestInfo2.responseCdn.freeResources();
                }
                this.downloadedInfos.clear();
            }
            if (bytesRead <= 0) {
                return bytesRead;
            }
            this.bytesRemaining -= (long) bytesRead;
            if (this.listener == null) {
                return bytesRead;
            }
            this.listener.onBytesTransferred(this, bytesRead);
            return bytesRead;
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    public Uri getUri() {
        return this.uri;
    }

    public void close() {
        cleanup();
        this.semaphore.release();
        this.uri = null;
        if (this.opened) {
            this.opened = false;
            if (this.listener != null) {
                this.listener.onTransferEnd(this);
            }
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
            if (this.fiv != null) {
                this.fiv.close();
                this.fiv = null;
            }
        } catch (Throwable e2222) {
            FileLog.e(e2222);
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

    private void clearOperaion(RequestInfo currentInfo) {
        int a;
        int minOffset = ConnectionsManager.DEFAULT_DATACENTER_ID;
        for (a = 0; a < this.requestInfos.size(); a++) {
            RequestInfo info = (RequestInfo) this.requestInfos.get(a);
            minOffset = Math.min(info.offset, minOffset);
            if (!(currentInfo == info || info.requestToken == 0)) {
                ConnectionsManager.getInstance(this.currentAccount).cancelRequest(info.requestToken, true);
            }
        }
        this.requestInfos.clear();
        for (a = 0; a < this.delayedRequestInfos.size(); a++) {
            info = (RequestInfo) this.delayedRequestInfos.get(a);
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
        this.nextDownloadOffset = minOffset;
    }

    private void requestFileOffsets(int offset) {
        if (!this.requestingCdnOffsets) {
            this.requestingCdnOffsets = true;
            TL_upload_getCdnFileHashes req = new TL_upload_getCdnFileHashes();
            req.file_token = this.cdnToken;
            req.offset = offset;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                public void run(TLObject response, TL_error error) {
                    if (error != null) {
                        FileStreamingLoadOperation.this.onFail(false, 0);
                        return;
                    }
                    int a;
                    FileStreamingLoadOperation.this.requestingCdnOffsets = false;
                    Vector vector = (Vector) response;
                    if (!vector.objects.isEmpty()) {
                        if (FileStreamingLoadOperation.this.cdnHashes == null) {
                            FileStreamingLoadOperation.this.cdnHashes = new HashMap();
                        }
                        for (a = 0; a < vector.objects.size(); a++) {
                            TL_cdnFileHash hash = (TL_cdnFileHash) vector.objects.get(a);
                            FileStreamingLoadOperation.this.cdnHashes.put(Integer.valueOf(hash.offset), hash);
                        }
                    }
                    for (a = 0; a < FileStreamingLoadOperation.this.delayedRequestInfos.size(); a++) {
                        RequestInfo delayedRequestInfo = (RequestInfo) FileStreamingLoadOperation.this.delayedRequestInfos.get(a);
                        if (FileStreamingLoadOperation.this.downloadedBytes == delayedRequestInfo.offset) {
                            FileStreamingLoadOperation.this.delayedRequestInfos.remove(a);
                            if (!FileStreamingLoadOperation.this.processRequestResult(delayedRequestInfo, null)) {
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
            }, null, null, 0, this.datacenter_id, 1, true);
        }
    }

    private boolean processRequestResult(RequestInfo requestInfo, TL_error error) {
        if (this.state != 1) {
            return false;
        }
        this.requestInfos.remove(requestInfo);
        if (error == null) {
            try {
                delayRequestInfo(requestInfo);
                NativeByteBuffer bytes = requestInfo.bytes;
                if (bytes == null || bytes.limit() == 0) {
                    onFinishLoadingFile(true);
                    return false;
                }
                int cdnCheckPart;
                int fileOffset;
                int currentBytesSize = bytes.limit();
                if (this.isCdn) {
                    cdnCheckPart = (this.downloadedBytes + currentBytesSize) / 131072;
                    fileOffset = (cdnCheckPart - (this.lastCheckedCdnPart != cdnCheckPart ? 1 : 0)) * 131072;
                    if ((this.cdnHashes != null ? (TL_cdnFileHash) this.cdnHashes.get(Integer.valueOf(fileOffset)) : null) == null) {
                        delayRequestInfo(requestInfo);
                        requestFileOffsets(fileOffset);
                        return true;
                    }
                }
                if (requestInfo.responseCdn != null) {
                    int offset = requestInfo.offset / 16;
                    this.cdnIv[15] = (byte) (offset & 255);
                    this.cdnIv[14] = (byte) ((offset >> 8) & 255);
                    this.cdnIv[13] = (byte) ((offset >> 16) & 255);
                    this.cdnIv[12] = (byte) ((offset >> 24) & 255);
                    Utilities.aesCtrDecryption(bytes.buffer, this.cdnKey, this.cdnIv, 0, bytes.limit());
                }
                this.downloadedBytes += currentBytesSize;
                boolean finishedDownloading = currentBytesSize != this.currentDownloadChunkSize || ((this.totalBytesCount == this.downloadedBytes || this.downloadedBytes % this.currentDownloadChunkSize != 0) && (this.totalBytesCount <= 0 || this.totalBytesCount <= this.downloadedBytes));
                if (this.key != null) {
                    Utilities.aesIgeEncryption(bytes.buffer, this.key, this.iv, false, true, 0, bytes.limit());
                    if (finishedDownloading && this.bytesCountPadding != 0) {
                        bytes.limit(bytes.limit() - this.bytesCountPadding);
                    }
                }
                this.fileOutputStream.getChannel().write(bytes.buffer);
                requestInfo.remainingBytes = requestInfo.bytes.limit() - this.firstPacketOffset;
                requestInfo.bytes.position(this.firstPacketOffset);
                this.downloadedInfos.add(requestInfo);
                if (this.isCdn) {
                    cdnCheckPart = this.downloadedBytes / 131072;
                    if (cdnCheckPart != this.lastCheckedCdnPart || finishedDownloading) {
                        int count;
                        this.fileOutputStream.getFD().sync();
                        fileOffset = (cdnCheckPart - (this.lastCheckedCdnPart != cdnCheckPart ? 1 : 0)) * 131072;
                        TL_cdnFileHash hash = (TL_cdnFileHash) this.cdnHashes.get(Integer.valueOf(fileOffset));
                        if (this.fileReadStream == null) {
                            this.cdnCheckBytes = new byte[131072];
                            this.fileReadStream = new RandomAccessFile(this.cacheFileTemp, "r");
                            if (fileOffset != 0) {
                                this.fileReadStream.seek((long) fileOffset);
                            }
                        }
                        if (this.lastCheckedCdnPart != cdnCheckPart) {
                            count = 131072;
                        } else {
                            count = this.downloadedBytes - (131072 * cdnCheckPart);
                        }
                        this.fileReadStream.readFully(this.cdnCheckBytes, 0, count);
                        if (Arrays.equals(Utilities.computeSHA256(this.cdnCheckBytes, 0, count), hash.hash)) {
                            this.lastCheckedCdnPart = cdnCheckPart;
                        } else {
                            if (this.location != null) {
                                FileLog.e("invalid cdn hash " + this.location + " id = " + this.location.id + " local_id = " + this.location.local_id + " access_hash = " + this.location.access_hash + " volume_id = " + this.location.volume_id + " secret = " + this.location.secret);
                            } else if (this.webLocation != null) {
                                FileLog.e("invalid cdn hash  " + this.webLocation + " id = " + this.webLocation.url + " access_hash = " + this.webLocation.access_hash);
                            }
                            onFail(false, 0);
                            this.cacheFileTemp.delete();
                            return false;
                        }
                    }
                }
                if (this.fiv != null) {
                    this.fiv.seek(0);
                    this.fiv.write(this.iv);
                }
                int a = (this.totalBytesCount <= 0 || this.state == 1) ? 0 : 0;
                while (a < this.delayedRequestInfos.size()) {
                    RequestInfo delayedRequestInfo = (RequestInfo) this.delayedRequestInfos.get(a);
                    if (this.downloadedBytes == delayedRequestInfo.offset) {
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
                            onFinishLoadingFile(true);
                        }
                        if (this.semaphore != null) {
                            this.semaphore.release();
                        }
                    } else {
                        a++;
                    }
                }
                if (finishedDownloading) {
                    onFinishLoadingFile(true);
                }
                if (this.semaphore != null) {
                    this.semaphore.release();
                }
            } catch (Throwable e) {
                onFail(false, 0);
                FileLog.e(e);
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
                this.datacenter_id = val.intValue();
            }
        } else if (error.text.contains("OFFSET_INVALID")) {
            if (this.downloadedBytes % this.currentDownloadChunkSize == 0) {
                try {
                    onFinishLoadingFile(true);
                } catch (Throwable e3) {
                    FileLog.e(e3);
                    onFail(false, 0);
                }
            } else {
                onFail(false, 0);
            }
        } else if (error.text.contains("RETRY_LIMIT")) {
            onFail(false, 2);
        } else {
            if (this.location != null) {
                FileLog.e(TtmlNode.ANONYMOUS_REGION_ID + this.location + " id = " + this.location.id + " local_id = " + this.location.local_id + " access_hash = " + this.location.access_hash + " volume_id = " + this.location.volume_id + " secret = " + this.location.secret);
            } else if (this.webLocation != null) {
                FileLog.e(TtmlNode.ANONYMOUS_REGION_ID + this.webLocation + " id = " + this.webLocation.url + " access_hash = " + this.webLocation.access_hash);
            }
            onFail(false, 0);
        }
        return false;
    }

    public void setPaths(File store, File temp) {
        this.storePath = store;
        this.tempPath = temp;
    }

    public int getCurrentType() {
        return this.currentType;
    }

    public String getFileName() {
        if (this.location != null) {
            return this.location.volume_id + "_" + this.location.local_id + "." + this.ext;
        }
        return Utilities.MD5(this.webLocation.url) + "." + this.ext;
    }

    public boolean start() {
        if (this.state != 0) {
            return false;
        }
        if (this.location == null && this.webLocation == null) {
            onFail(true, 0);
            return false;
        }
        String fileNameTemp;
        String fileNameFinal;
        String fileNameIv = null;
        if (this.webLocation != null) {
            String md5 = Utilities.MD5(this.webLocation.url);
            fileNameTemp = md5 + ".temp";
            fileNameFinal = md5 + "." + this.ext;
            if (this.key != null) {
                fileNameIv = md5 + ".iv";
            }
        } else if (this.location.volume_id == 0 || this.location.local_id == 0) {
            if (this.datacenter_id == 0 || this.location.id == 0) {
                onFail(true, 0);
                return false;
            }
            fileNameTemp = this.datacenter_id + "_" + this.location.id + ".temp";
            fileNameFinal = this.datacenter_id + "_" + this.location.id + this.ext;
            if (this.key != null) {
                fileNameIv = this.datacenter_id + "_" + this.location.id + ".iv";
            }
        } else if (this.datacenter_id == Integer.MIN_VALUE || this.location.volume_id == -2147483648L || this.datacenter_id == 0) {
            onFail(true, 0);
            return false;
        } else {
            fileNameTemp = this.location.volume_id + "_" + this.location.local_id + ".temp";
            fileNameFinal = this.location.volume_id + "_" + this.location.local_id + "." + this.ext;
            if (this.key != null) {
                fileNameIv = this.location.volume_id + "_" + this.location.local_id + ".iv";
            }
        }
        this.currentDownloadChunkSize = this.totalBytesCount >= 1048576 ? 131072 : 32768;
        this.currentMaxDownloadRequests = this.totalBytesCount >= 1048576 ? 2 : 4;
        this.requestInfos = new ArrayList(this.currentMaxDownloadRequests);
        this.delayedRequestInfos = new ArrayList(this.currentMaxDownloadRequests - 1);
        this.downloadedInfos = new ArrayList();
        this.state = 1;
        this.cacheFileFinal = new File(this.storePath, fileNameFinal);
        this.cacheFileTemp = new File(this.tempPath, fileNameTemp);
        if (BuildVars.DEBUG_VERSION) {
            FileLog.d("start loading file to temp = " + this.cacheFileTemp + " final = " + this.cacheFileFinal);
        }
        if (fileNameIv != null) {
            this.cacheIvTemp = new File(this.tempPath, fileNameIv);
            try {
                this.fiv = new RandomAccessFile(this.cacheIvTemp, "rws");
                if (!false) {
                    long len = this.cacheIvTemp.length();
                    if (len <= 0 || len % 32 != 0) {
                        this.downloadedBytes = 0;
                    } else {
                        this.fiv.read(this.iv, 0, 32);
                    }
                }
            } catch (Throwable e) {
                FileLog.e(e);
                this.downloadedBytes = 0;
            }
        }
        try {
            this.fileOutputStream = new RandomAccessFile(this.cacheFileTemp, "rws");
            if (this.downloadedBytes != 0) {
                this.fileOutputStream.seek((long) this.downloadedBytes);
            }
        } catch (Throwable e2) {
            FileLog.e(e2);
        }
        if (this.fileOutputStream != null) {
            return true;
        }
        onFail(true, 0);
        return false;
    }

    public void cancel() {
        Utilities.stageQueue.postRunnable(new Runnable() {
            public void run() {
                if (FileStreamingLoadOperation.this.state != 3 && FileStreamingLoadOperation.this.state != 2) {
                    if (FileStreamingLoadOperation.this.requestInfos != null) {
                        for (int a = 0; a < FileStreamingLoadOperation.this.requestInfos.size(); a++) {
                            RequestInfo requestInfo = (RequestInfo) FileStreamingLoadOperation.this.requestInfos.get(a);
                            if (requestInfo.requestToken != 0) {
                                ConnectionsManager.getInstance(FileStreamingLoadOperation.this.currentAccount).cancelRequest(requestInfo.requestToken, true);
                            }
                        }
                    }
                    FileStreamingLoadOperation.this.onFail(false, 1);
                }
            }
        });
    }

    private void onFinishLoadingFile(final boolean increment) throws Exception {
        if (this.state == 1) {
            this.state = 3;
            cleanup();
            if (this.cacheIvTemp != null) {
                this.cacheIvTemp.delete();
                this.cacheIvTemp = null;
            }
            if (!(this.cacheFileTemp == null || this.cacheFileTemp.renameTo(this.cacheFileFinal))) {
                if (BuildVars.DEBUG_VERSION) {
                    FileLog.e("unable to rename temp = " + this.cacheFileTemp + " to final = " + this.cacheFileFinal + " retry = " + this.renameRetryCount);
                }
                this.renameRetryCount++;
                if (this.renameRetryCount < 3) {
                    this.state = 1;
                    Utilities.stageQueue.postRunnable(new Runnable() {
                        public void run() {
                            try {
                                FileStreamingLoadOperation.this.onFinishLoadingFile(increment);
                            } catch (Exception e) {
                                FileStreamingLoadOperation.this.onFail(false, 0);
                            }
                        }
                    }, 200);
                    return;
                }
                this.cacheFileFinal = this.cacheFileTemp;
            }
            if (BuildVars.DEBUG_VERSION) {
                FileLog.e("finished downloading file to " + this.cacheFileFinal);
            }
            if (!increment) {
                return;
            }
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

    private void delayRequestInfo(RequestInfo requestInfo) {
        if (requestInfo.response != null) {
            requestInfo.response.disableFree = true;
        } else if (requestInfo.responseWeb != null) {
            requestInfo.responseWeb.disableFree = true;
        } else if (requestInfo.responseCdn != null) {
            requestInfo.responseCdn.disableFree = true;
        }
    }

    private void onFail(boolean thread, int reason) {
        cleanup();
        this.state = 2;
        if (thread) {
            Utilities.stageQueue.postRunnable(new Runnable() {
                public void run() {
                    FileStreamingLoadOperation.this.semaphore.release();
                }
            });
        } else {
            this.semaphore.release();
        }
    }

    private void startDownloadRequest(int count) {
        int a = 0;
        while (a < count) {
            if (this.totalBytesCount <= 0 || this.nextDownloadOffset < this.totalBytesCount) {
                int offset;
                TLObject request;
                int i;
                boolean isLast = this.totalBytesCount <= 0 || a == count - 1 || (this.totalBytesCount > 0 && this.nextDownloadOffset + this.currentDownloadChunkSize >= this.totalBytesCount);
                int connectionType = this.requestsCount % 2 == 0 ? 2 : ConnectionsManager.ConnectionTypeDownload2;
                int flags = 34;
                TLObject req;
                if (this.isCdn) {
                    req = new TL_upload_getCdnFile();
                    req.file_token = this.cdnToken;
                    offset = this.nextDownloadOffset;
                    req.offset = offset;
                    req.limit = this.currentDownloadChunkSize;
                    request = req;
                    flags = 34 | 1;
                } else if (this.webLocation != null) {
                    req = new TL_upload_getWebFile();
                    req.location = this.webLocation;
                    offset = this.nextDownloadOffset;
                    req.offset = offset;
                    req.limit = this.currentDownloadChunkSize;
                    request = req;
                } else {
                    req = new TL_upload_getFile();
                    req.location = this.location;
                    offset = this.nextDownloadOffset;
                    req.offset = offset;
                    req.limit = this.currentDownloadChunkSize;
                    request = req;
                }
                final RequestInfo requestInfo = new RequestInfo();
                this.requestInfos.add(requestInfo);
                requestInfo.offset = offset;
                ConnectionsManager instance = ConnectionsManager.getInstance(this.currentAccount);
                RequestDelegate anonymousClass5 = new RequestDelegate() {
                    public void run(TLObject response, TL_error error) {
                        if (!FileStreamingLoadOperation.this.requestInfos.contains(requestInfo)) {
                            return;
                        }
                        if (error != null && (request instanceof TL_upload_getCdnFile) && error.text.equals("FILE_TOKEN_INVALID")) {
                            FileStreamingLoadOperation.this.isCdn = false;
                            FileStreamingLoadOperation.this.clearOperaion(requestInfo);
                            FileStreamingLoadOperation.this.startDownloadRequest(1);
                        } else if (response instanceof TL_upload_fileCdnRedirect) {
                            TL_upload_fileCdnRedirect res = (TL_upload_fileCdnRedirect) response;
                            if (!res.cdn_file_hashes.isEmpty()) {
                                if (FileStreamingLoadOperation.this.cdnHashes == null) {
                                    FileStreamingLoadOperation.this.cdnHashes = new HashMap();
                                }
                                for (int a = 0; a < res.cdn_file_hashes.size(); a++) {
                                    TL_cdnFileHash hash = (TL_cdnFileHash) res.cdn_file_hashes.get(a);
                                    FileStreamingLoadOperation.this.cdnHashes.put(Integer.valueOf(hash.offset), hash);
                                }
                            }
                            if (res.encryption_iv == null || res.encryption_key == null || res.encryption_iv.length != 16 || res.encryption_key.length != 32) {
                                error = new TL_error();
                                error.text = "bad redirect response";
                                error.code = 400;
                                FileStreamingLoadOperation.this.processRequestResult(requestInfo, error);
                                return;
                            }
                            FileStreamingLoadOperation.this.isCdn = true;
                            FileStreamingLoadOperation.this.cdnDatacenterId = res.dc_id;
                            FileStreamingLoadOperation.this.cdnIv = res.encryption_iv;
                            FileStreamingLoadOperation.this.cdnKey = res.encryption_key;
                            FileStreamingLoadOperation.this.cdnToken = res.file_token;
                            FileStreamingLoadOperation.this.clearOperaion(requestInfo);
                            FileStreamingLoadOperation.this.startDownloadRequest(1);
                        } else if (!(response instanceof TL_upload_cdnFileReuploadNeeded)) {
                            if (response instanceof TL_upload_file) {
                                requestInfo.response = (TL_upload_file) response;
                                requestInfo.bytes = requestInfo.response.bytes;
                            } else if (response instanceof TL_upload_webFile) {
                                requestInfo.responseWeb = (TL_upload_webFile) response;
                                requestInfo.bytes = requestInfo.responseWeb.bytes;
                            } else if (response instanceof TL_upload_cdnFile) {
                                requestInfo.responseCdn = (TL_upload_cdnFile) response;
                                requestInfo.bytes = requestInfo.responseCdn.bytes;
                            }
                            if (response != null) {
                                if (FileStreamingLoadOperation.this.currentType == ConnectionsManager.FileTypeAudio) {
                                    StatsController.getInstance(FileStreamingLoadOperation.this.currentAccount).incrementReceivedBytesCount(response.networkType, 3, (long) (response.getObjectSize() + 4));
                                } else if (FileStreamingLoadOperation.this.currentType == ConnectionsManager.FileTypeVideo) {
                                    StatsController.getInstance(FileStreamingLoadOperation.this.currentAccount).incrementReceivedBytesCount(response.networkType, 2, (long) (response.getObjectSize() + 4));
                                } else if (FileStreamingLoadOperation.this.currentType == 16777216) {
                                    StatsController.getInstance(FileStreamingLoadOperation.this.currentAccount).incrementReceivedBytesCount(response.networkType, 4, (long) (response.getObjectSize() + 4));
                                } else if (FileStreamingLoadOperation.this.currentType == ConnectionsManager.FileTypeFile) {
                                    StatsController.getInstance(FileStreamingLoadOperation.this.currentAccount).incrementReceivedBytesCount(response.networkType, 5, (long) (response.getObjectSize() + 4));
                                }
                            }
                            FileStreamingLoadOperation.this.processRequestResult(requestInfo, error);
                        } else if (!FileStreamingLoadOperation.this.reuploadingCdn) {
                            FileStreamingLoadOperation.this.clearOperaion(requestInfo);
                            FileStreamingLoadOperation.this.reuploadingCdn = true;
                            TL_upload_cdnFileReuploadNeeded res2 = (TL_upload_cdnFileReuploadNeeded) response;
                            TL_upload_reuploadCdnFile req = new TL_upload_reuploadCdnFile();
                            req.file_token = FileStreamingLoadOperation.this.cdnToken;
                            req.request_token = res2.request_token;
                            ConnectionsManager.getInstance(FileStreamingLoadOperation.this.currentAccount).sendRequest(req, new RequestDelegate() {
                                public void run(TLObject response, TL_error error) {
                                    FileStreamingLoadOperation.this.reuploadingCdn = false;
                                    if (error == null) {
                                        Vector vector = (Vector) response;
                                        if (!vector.objects.isEmpty()) {
                                            if (FileStreamingLoadOperation.this.cdnHashes == null) {
                                                FileStreamingLoadOperation.this.cdnHashes = new HashMap();
                                            }
                                            for (int a = 0; a < vector.objects.size(); a++) {
                                                TL_cdnFileHash hash = (TL_cdnFileHash) vector.objects.get(a);
                                                FileStreamingLoadOperation.this.cdnHashes.put(Integer.valueOf(hash.offset), hash);
                                            }
                                        }
                                        FileStreamingLoadOperation.this.startDownloadRequest(1);
                                    } else if (error.text.equals("FILE_TOKEN_INVALID") || error.text.equals("REQUEST_TOKEN_INVALID")) {
                                        FileStreamingLoadOperation.this.isCdn = false;
                                        FileStreamingLoadOperation.this.clearOperaion(requestInfo);
                                        FileStreamingLoadOperation.this.startDownloadRequest(1);
                                    } else {
                                        FileStreamingLoadOperation.this.onFail(false, 0);
                                    }
                                }
                            }, null, null, 0, FileStreamingLoadOperation.this.datacenter_id, 1, true);
                        }
                    }
                };
                if (this.isCdn) {
                    i = this.cdnDatacenterId;
                } else {
                    i = this.datacenter_id;
                }
                requestInfo.requestToken = instance.sendRequest(request, anonymousClass5, null, null, flags, i, connectionType, isLast);
                this.requestsCount++;
                a++;
            } else {
                return;
            }
        }
    }
}
