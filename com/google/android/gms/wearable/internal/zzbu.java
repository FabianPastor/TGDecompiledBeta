package com.google.android.gms.wearable.internal;

import android.os.ParcelFileDescriptor;
import android.os.ParcelFileDescriptor.AutoCloseInputStream;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.wearable.DataApi.GetFdForAssetResult;
import java.io.IOException;
import java.io.InputStream;

public final class zzbu implements GetFdForAssetResult {
    private volatile boolean mClosed = false;
    private final Status mStatus;
    private volatile ParcelFileDescriptor zzbSB;
    private volatile InputStream zzbSm;

    public zzbu(Status status, ParcelFileDescriptor parcelFileDescriptor) {
        this.mStatus = status;
        this.zzbSB = parcelFileDescriptor;
    }

    public final ParcelFileDescriptor getFd() {
        if (!this.mClosed) {
            return this.zzbSB;
        }
        throw new IllegalStateException("Cannot access the file descriptor after release().");
    }

    public final InputStream getInputStream() {
        if (this.mClosed) {
            throw new IllegalStateException("Cannot access the input stream after release().");
        } else if (this.zzbSB == null) {
            return null;
        } else {
            if (this.zzbSm == null) {
                this.zzbSm = new AutoCloseInputStream(this.zzbSB);
            }
            return this.zzbSm;
        }
    }

    public final Status getStatus() {
        return this.mStatus;
    }

    public final void release() {
        if (this.zzbSB != null) {
            if (this.mClosed) {
                throw new IllegalStateException("releasing an already released result.");
            }
            try {
                if (this.zzbSm != null) {
                    this.zzbSm.close();
                } else {
                    this.zzbSB.close();
                }
                this.mClosed = true;
                this.zzbSB = null;
                this.zzbSm = null;
            } catch (IOException e) {
            }
        }
    }
}
