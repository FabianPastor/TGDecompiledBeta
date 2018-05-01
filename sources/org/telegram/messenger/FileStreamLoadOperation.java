package org.telegram.messenger;

import android.net.Uri;
import java.io.EOFException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.CountDownLatch;
import org.telegram.messenger.exoplayer2.upstream.DataSource;
import org.telegram.messenger.exoplayer2.upstream.DataSpec;
import org.telegram.messenger.exoplayer2.upstream.TransferListener;
import org.telegram.messenger.exoplayer2.util.MimeTypes;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.TL_document;
import org.telegram.tgnet.TLRPC.TL_documentAttributeAudio;
import org.telegram.tgnet.TLRPC.TL_documentAttributeFilename;
import org.telegram.tgnet.TLRPC.TL_documentAttributeVideo;

public class FileStreamLoadOperation implements DataSource {
    private long bytesRemaining;
    private CountDownLatch countDownLatch;
    private int currentAccount;
    private int currentOffset;
    private Document document;
    private RandomAccessFile file;
    private final TransferListener<? super FileStreamLoadOperation> listener;
    private FileLoadOperation loadOperation;
    private boolean opened;
    private Uri uri;

    public FileStreamLoadOperation() {
        this(null);
    }

    public FileStreamLoadOperation(TransferListener<? super FileStreamLoadOperation> transferListener) {
        this.listener = transferListener;
    }

    public long open(DataSpec dataSpec) throws IOException {
        this.uri = dataSpec.uri;
        this.currentAccount = Utilities.parseInt(this.uri.getQueryParameter("account")).intValue();
        this.document = new TL_document();
        this.document.access_hash = Utilities.parseLong(this.uri.getQueryParameter("hash")).longValue();
        this.document.id = Utilities.parseLong(this.uri.getQueryParameter(TtmlNode.ATTR_ID)).longValue();
        this.document.size = Utilities.parseInt(this.uri.getQueryParameter("size")).intValue();
        this.document.dc_id = Utilities.parseInt(this.uri.getQueryParameter("dc")).intValue();
        this.document.mime_type = this.uri.getQueryParameter("mime");
        TL_documentAttributeFilename tL_documentAttributeFilename = new TL_documentAttributeFilename();
        tL_documentAttributeFilename.file_name = this.uri.getQueryParameter("name");
        this.document.attributes.add(tL_documentAttributeFilename);
        if (this.document.mime_type.startsWith(MimeTypes.BASE_TYPE_VIDEO)) {
            this.document.attributes.add(new TL_documentAttributeVideo());
        } else if (this.document.mime_type.startsWith(MimeTypes.BASE_TYPE_AUDIO)) {
            this.document.attributes.add(new TL_documentAttributeAudio());
        }
        FileLoader instance = FileLoader.getInstance(this.currentAccount);
        Document document = this.document;
        int i = (int) dataSpec.position;
        this.currentOffset = i;
        this.loadOperation = instance.loadStreamFile(this, document, i);
        this.bytesRemaining = dataSpec.length == -1 ? ((long) this.document.size) - dataSpec.position : dataSpec.length;
        if (this.bytesRemaining < 0) {
            throw new EOFException();
        }
        this.opened = true;
        if (this.listener != null) {
            this.listener.onTransferStart(this, dataSpec);
        }
        this.file = new RandomAccessFile(this.loadOperation.getCurrentFile(), "r");
        this.file.seek((long) this.currentOffset);
        return this.bytesRemaining;
    }

    public int read(byte[] bArr, int i, int i2) throws IOException {
        int i3 = 0;
        if (i2 == 0) {
            return 0;
        }
        if (this.bytesRemaining == 0) {
            return -1;
        }
        try {
            if (this.bytesRemaining < ((long) i2)) {
                i2 = (int) this.bytesRemaining;
            }
            while (i3 == 0) {
                i3 = this.loadOperation.getDownloadedLengthFromOffset(this.currentOffset, i2);
                if (i3 == 0) {
                    if (this.loadOperation.isPaused()) {
                        FileLoader.getInstance(this.currentAccount).loadStreamFile(this, this.document, this.currentOffset);
                    }
                    this.countDownLatch = new CountDownLatch(1);
                    this.countDownLatch.await();
                }
            }
            this.file.readFully(bArr, i, i3);
            this.currentOffset += i3;
            this.bytesRemaining -= (long) i3;
            if (this.listener != null) {
                this.listener.onBytesTransferred(this, i3);
            }
            return i3;
        } catch (byte[] bArr2) {
            throw new IOException(bArr2);
        }
    }

    public Uri getUri() {
        return this.uri;
    }

    public void close() {
        if (this.loadOperation != null) {
            this.loadOperation.removeStreamListener(this);
        }
        if (this.countDownLatch != null) {
            this.countDownLatch.countDown();
        }
        if (this.file != null) {
            try {
                this.file.close();
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
            this.file = null;
        }
        this.uri = null;
        if (this.opened) {
            this.opened = false;
            if (this.listener != null) {
                this.listener.onTransferEnd(this);
            }
        }
    }

    protected void newDataAvailable() {
        if (this.countDownLatch != null) {
            this.countDownLatch.countDown();
        }
    }
}
