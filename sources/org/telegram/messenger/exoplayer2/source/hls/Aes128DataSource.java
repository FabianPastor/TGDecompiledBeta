package org.telegram.messenger.exoplayer2.source.hls;

import android.net.Uri;
import java.io.IOException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.telegram.messenger.exoplayer2.upstream.DataSource;
import org.telegram.messenger.exoplayer2.upstream.DataSourceInputStream;
import org.telegram.messenger.exoplayer2.upstream.DataSpec;
import org.telegram.messenger.exoplayer2.util.Assertions;

final class Aes128DataSource implements DataSource {
    private CipherInputStream cipherInputStream;
    private final byte[] encryptionIv;
    private final byte[] encryptionKey;
    private final DataSource upstream;

    public Aes128DataSource(DataSource dataSource, byte[] bArr, byte[] bArr2) {
        this.upstream = dataSource;
        this.encryptionKey = bArr;
        this.encryptionIv = bArr2;
    }

    public long open(DataSpec dataSpec) throws IOException {
        try {
            Cipher instance = Cipher.getInstance("AES/CBC/PKCS7Padding");
            try {
                instance.init(2, new SecretKeySpec(this.encryptionKey, "AES"), new IvParameterSpec(this.encryptionIv));
                this.cipherInputStream = new CipherInputStream(new DataSourceInputStream(this.upstream, dataSpec), instance);
                return -1;
            } catch (DataSpec dataSpec2) {
                throw new RuntimeException(dataSpec2);
            }
        } catch (DataSpec dataSpec22) {
            throw new RuntimeException(dataSpec22);
        }
    }

    public void close() throws IOException {
        this.cipherInputStream = null;
        this.upstream.close();
    }

    public int read(byte[] bArr, int i, int i2) throws IOException {
        Assertions.checkState(this.cipherInputStream != null);
        bArr = this.cipherInputStream.read(bArr, i, i2);
        return bArr < null ? -1 : bArr;
    }

    public Uri getUri() {
        return this.upstream.getUri();
    }
}
