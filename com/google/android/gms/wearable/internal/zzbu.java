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
    private volatile ParcelFileDescriptor zzbSD;
    private volatile InputStream zzbSo;

    public zzbu(Status status, ParcelFileDescriptor parcelFileDescriptor) {
        this.mStatus = status;
        this.zzbSD = parcelFileDescriptor;
    }

    public final ParcelFileDescriptor getFd() {
        if (!this.mClosed) {
            return this.zzbSD;
        }
        throw new IllegalStateException("Cannot access the file descriptor after release().");
    }

    public final InputStream getInputStream() {
        if (this.mClosed) {
            throw new IllegalStateException("Cannot access the input stream after release().");
        } else if (this.zzbSD == null) {
            return null;
        } else {
            if (this.zzbSo == null) {
                this.zzbSo = new AutoCloseInputStream(this.zzbSD);
            }
            return this.zzbSo;
        }
    }

    public final Status getStatus() {
        return this.mStatus;
    }

    public final void release() {
        if (this.zzbSD != null) {
            if (this.mClosed) {
                throw new IllegalStateException("releasing an already released result.");
            }
            try {
                if (this.zzbSo != null) {
                    this.zzbSo.close();
                } else {
                    this.zzbSD.close();
                }
                this.mClosed = true;
                this.zzbSD = null;
                this.zzbSo = null;
            } catch (IOException e) {
            }
        }
    }
}
