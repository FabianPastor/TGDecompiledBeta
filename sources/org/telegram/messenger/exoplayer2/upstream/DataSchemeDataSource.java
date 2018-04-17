package org.telegram.messenger.exoplayer2.upstream;

import android.net.Uri;
import android.util.Base64;
import java.io.IOException;
import java.net.URLDecoder;
import org.telegram.messenger.exoplayer2.C0539C;
import org.telegram.messenger.exoplayer2.ParserException;

public final class DataSchemeDataSource implements DataSource {
    public static final String SCHEME_DATA = "data";
    private int bytesRead;
    private byte[] data;
    private DataSpec dataSpec;

    public long open(DataSpec dataSpec) throws IOException {
        this.dataSpec = dataSpec;
        Uri uri = dataSpec.uri;
        String scheme = uri.getScheme();
        if (SCHEME_DATA.equals(scheme)) {
            String[] uriParts = uri.getSchemeSpecificPart().split(",");
            if (uriParts.length > 2) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Unexpected URI format: ");
                stringBuilder.append(uri);
                throw new ParserException(stringBuilder.toString());
            }
            String dataString = uriParts[1];
            if (uriParts[0].contains(";base64")) {
                try {
                    this.data = Base64.decode(dataString, 0);
                } catch (IllegalArgumentException e) {
                    StringBuilder stringBuilder2 = new StringBuilder();
                    stringBuilder2.append("Error while parsing Base64 encoded string: ");
                    stringBuilder2.append(dataString);
                    throw new ParserException(stringBuilder2.toString(), e);
                }
            }
            this.data = URLDecoder.decode(dataString, C0539C.ASCII_NAME).getBytes();
            return (long) this.data.length;
        }
        StringBuilder stringBuilder3 = new StringBuilder();
        stringBuilder3.append("Unsupported scheme: ");
        stringBuilder3.append(scheme);
        throw new ParserException(stringBuilder3.toString());
    }

    public int read(byte[] buffer, int offset, int readLength) {
        if (readLength == 0) {
            return 0;
        }
        int remainingBytes = this.data.length - this.bytesRead;
        if (remainingBytes == 0) {
            return -1;
        }
        readLength = Math.min(readLength, remainingBytes);
        System.arraycopy(this.data, this.bytesRead, buffer, offset, readLength);
        this.bytesRead += readLength;
        return readLength;
    }

    public Uri getUri() {
        return this.dataSpec != null ? this.dataSpec.uri : null;
    }

    public void close() throws IOException {
        this.dataSpec = null;
        this.data = null;
    }
}
