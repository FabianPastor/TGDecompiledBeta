package org.telegram.messenger.exoplayer2.upstream;

import android.net.Uri;
import android.util.Base64;
import java.io.IOException;
import java.net.URLDecoder;
import org.telegram.messenger.exoplayer2.C0542C;
import org.telegram.messenger.exoplayer2.ParserException;

public final class DataSchemeDataSource implements DataSource {
    public static final String SCHEME_DATA = "data";
    private int bytesRead;
    private byte[] data;
    private DataSpec dataSpec;

    public long open(DataSpec dataSpec) throws IOException {
        this.dataSpec = dataSpec;
        dataSpec = dataSpec.uri;
        String scheme = dataSpec.getScheme();
        if (SCHEME_DATA.equals(scheme)) {
            String[] split = dataSpec.getSchemeSpecificPart().split(",");
            if (split.length > 2) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Unexpected URI format: ");
                stringBuilder.append(dataSpec);
                throw new ParserException(stringBuilder.toString());
            }
            dataSpec = split[1];
            if (split[0].contains(";base64")) {
                try {
                    this.data = Base64.decode(dataSpec, 0);
                } catch (Throwable e) {
                    StringBuilder stringBuilder2 = new StringBuilder();
                    stringBuilder2.append("Error while parsing Base64 encoded string: ");
                    stringBuilder2.append(dataSpec);
                    throw new ParserException(stringBuilder2.toString(), e);
                }
            }
            this.data = URLDecoder.decode(dataSpec, C0542C.ASCII_NAME).getBytes();
            return (long) this.data.length;
        }
        stringBuilder = new StringBuilder();
        stringBuilder.append("Unsupported scheme: ");
        stringBuilder.append(scheme);
        throw new ParserException(stringBuilder.toString());
    }

    public int read(byte[] bArr, int i, int i2) {
        if (i2 == 0) {
            return null;
        }
        int length = this.data.length - this.bytesRead;
        if (length == 0) {
            return -1;
        }
        i2 = Math.min(i2, length);
        System.arraycopy(this.data, this.bytesRead, bArr, i, i2);
        this.bytesRead += i2;
        return i2;
    }

    public Uri getUri() {
        return this.dataSpec != null ? this.dataSpec.uri : null;
    }

    public void close() throws IOException {
        this.dataSpec = null;
        this.data = null;
    }
}
