package org.telegram.messenger;

import android.net.Uri;
import com.google.android.exoplayer2.upstream.BaseDataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.TransferListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.CountDownLatch;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.TL_document;
import org.telegram.tgnet.TLRPC.TL_documentAttributeAudio;
import org.telegram.tgnet.TLRPC.TL_documentAttributeFilename;
import org.telegram.tgnet.TLRPC.TL_documentAttributeVideo;

public class FileStreamLoadOperation extends BaseDataSource implements FileLoadOperationStream {
    private long bytesRemaining;
    private CountDownLatch countDownLatch;
    private int currentAccount;
    private int currentOffset;
    private Document document;
    private RandomAccessFile file;
    private FileLoadOperation loadOperation;
    private boolean opened;
    private Object parentObject;
    private Uri uri;

    public FileStreamLoadOperation() {
        super(false);
    }

    @Deprecated
    public FileStreamLoadOperation(TransferListener transferListener) {
        this();
        if (transferListener != null) {
            addTransferListener(transferListener);
        }
    }

    public long open(DataSpec dataSpec) throws IOException {
        this.uri = dataSpec.uri;
        this.currentAccount = Utilities.parseInt(this.uri.getQueryParameter("account")).intValue();
        this.parentObject = FileLoader.getInstance(this.currentAccount).getParentObject(Utilities.parseInt(this.uri.getQueryParameter("rid")).intValue());
        this.document = new TL_document();
        this.document.access_hash = Utilities.parseLong(this.uri.getQueryParameter("hash")).longValue();
        this.document.id = Utilities.parseLong(this.uri.getQueryParameter("id")).longValue();
        this.document.size = Utilities.parseInt(this.uri.getQueryParameter("size")).intValue();
        this.document.dc_id = Utilities.parseInt(this.uri.getQueryParameter("dc")).intValue();
        this.document.mime_type = this.uri.getQueryParameter("mime");
        this.document.file_reference = Utilities.hexToBytes(this.uri.getQueryParameter("reference"));
        TL_documentAttributeFilename tL_documentAttributeFilename = new TL_documentAttributeFilename();
        tL_documentAttributeFilename.file_name = this.uri.getQueryParameter("name");
        this.document.attributes.add(tL_documentAttributeFilename);
        if (this.document.mime_type.startsWith("video")) {
            this.document.attributes.add(new TL_documentAttributeVideo());
        } else if (this.document.mime_type.startsWith("audio")) {
            this.document.attributes.add(new TL_documentAttributeAudio());
        }
        FileLoader instance = FileLoader.getInstance(this.currentAccount);
        Document document = this.document;
        Object obj = this.parentObject;
        int i = (int) dataSpec.position;
        this.currentOffset = i;
        this.loadOperation = instance.loadStreamFile(this, document, obj, i);
        long j = dataSpec.length;
        if (j == -1) {
            j = ((long) this.document.size) - dataSpec.position;
        }
        this.bytesRemaining = j;
        if (this.bytesRemaining >= 0) {
            this.opened = true;
            transferStarted(dataSpec);
            FileLoadOperation fileLoadOperation = this.loadOperation;
            if (fileLoadOperation != null) {
                this.file = new RandomAccessFile(fileLoadOperation.getCurrentFile(), "r");
                this.file.seek((long) this.currentOffset);
            }
            return this.bytesRemaining;
        }
        throw new EOFException();
    }

    public int read(byte[] bArr, int i, int i2) throws IOException {
        int i3 = 0;
        if (i2 == 0) {
            return 0;
        }
        long j = this.bytesRemaining;
        if (j == 0) {
            return -1;
        }
        if (j < ((long) i2)) {
            i2 = (int) j;
        }
        while (i3 == 0) {
            try {
                i3 = this.loadOperation.getDownloadedLengthFromOffset(this.currentOffset, i2);
                if (i3 == 0) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("not found bytes ");
                    stringBuilder.append(i);
                    FileLog.d(stringBuilder.toString());
                    FileLoader.getInstance(this.currentAccount).loadStreamFile(this, this.document, this.parentObject, this.currentOffset);
                    this.countDownLatch = new CountDownLatch(1);
                    this.countDownLatch.await();
                }
            } catch (Exception e) {
                throw new IOException(e);
            }
        }
        this.file.readFully(bArr, i, i3);
        this.currentOffset += i3;
        this.bytesRemaining -= (long) i3;
        bytesTransferred(i3);
        return i3;
    }

    public Uri getUri() {
        return this.uri;
    }

    public void close() {
        FileLoadOperation fileLoadOperation = this.loadOperation;
        if (fileLoadOperation != null) {
            fileLoadOperation.removeStreamListener(this);
        }
        CountDownLatch countDownLatch = this.countDownLatch;
        if (countDownLatch != null) {
            countDownLatch.countDown();
        }
        RandomAccessFile randomAccessFile = this.file;
        if (randomAccessFile != null) {
            try {
                randomAccessFile.close();
            } catch (Exception e) {
                FileLog.e(e);
            }
            this.file = null;
        }
        this.uri = null;
        if (this.opened) {
            this.opened = false;
            transferEnded();
        }
    }

    public void newDataAvailable() {
        CountDownLatch countDownLatch = this.countDownLatch;
        if (countDownLatch != null) {
            countDownLatch.countDown();
        }
    }
}
