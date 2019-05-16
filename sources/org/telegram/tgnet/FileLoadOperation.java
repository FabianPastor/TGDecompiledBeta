package org.telegram.tgnet;

import java.io.File;

public class FileLoadOperation {
    private long address;
    private FileLoadOperationDelegate delegate;
    private boolean isForceRequest;
    private boolean started;

    public static native void native_cancelLoadOperation(long j);

    public static native long native_createLoadOpetation(int i, long j, long j2, long j3, int i2, byte[] bArr, byte[] bArr2, String str, int i3, int i4, String str2, String str3, Object obj);

    public static native void native_startLoadOperation(long j);

    public FileLoadOperation(int i, long j, long j2, long j3, int i2, byte[] bArr, byte[] bArr2, String str, int i3, int i4, File file, File file2, FileLoadOperationDelegate fileLoadOperationDelegate) {
        this.address = native_createLoadOpetation(i, j, j2, j3, i2, bArr, bArr2, str, i3, i4, file.getAbsolutePath(), file2.getAbsolutePath(), fileLoadOperationDelegate);
        this.delegate = fileLoadOperationDelegate;
    }

    public void setForceRequest(boolean z) {
        this.isForceRequest = z;
    }

    public boolean isForceRequest() {
        return this.isForceRequest;
    }

    public void start() {
        if (!this.started) {
            long j = this.address;
            if (j == 0) {
                this.delegate.onFailed(0);
                return;
            }
            this.started = true;
            native_startLoadOperation(j);
        }
    }

    public void cancel() {
        if (this.started) {
            long j = this.address;
            if (j != 0) {
                native_cancelLoadOperation(j);
            }
        }
    }

    public boolean wasStarted() {
        return this.started;
    }
}
