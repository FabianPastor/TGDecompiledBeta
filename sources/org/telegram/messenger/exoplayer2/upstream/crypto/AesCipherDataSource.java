package org.telegram.messenger.exoplayer2.upstream.crypto;

import android.net.Uri;
import java.io.IOException;
import org.telegram.messenger.exoplayer2.upstream.DataSource;
import org.telegram.messenger.exoplayer2.upstream.DataSpec;

public final class AesCipherDataSource implements DataSource {
    private AesFlushingCipher cipher;
    private final byte[] secretKey;
    private final DataSource upstream;

    public AesCipherDataSource(byte[] bArr, DataSource dataSource) {
        this.upstream = dataSource;
        this.secretKey = bArr;
    }

    public long open(DataSpec dataSpec) throws IOException {
        long open = this.upstream.open(dataSpec);
        this.cipher = new AesFlushingCipher(2, this.secretKey, CryptoUtil.getFNV64Hash(dataSpec.key), dataSpec.absoluteStreamPosition);
        return open;
    }

    public int read(byte[] bArr, int i, int i2) throws IOException {
        if (i2 == 0) {
            return null;
        }
        i2 = this.upstream.read(bArr, i, i2);
        if (i2 == -1) {
            return -1;
        }
        this.cipher.updateInPlace(bArr, i, i2);
        return i2;
    }

    public void close() throws IOException {
        this.cipher = null;
        this.upstream.close();
    }

    public Uri getUri() {
        return this.upstream.getUri();
    }
}
