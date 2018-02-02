package org.telegram.messenger.exoplayer2.upstream;

import android.net.Uri;
import android.util.Base64;
import java.io.IOException;
import java.net.URLDecoder;
import org.telegram.messenger.exoplayer2.C;
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
                throw new ParserException("Unexpected URI format: " + uri);
            }
            String dataString = uriParts[1];
            if (uriParts[0].contains(";base64")) {
                try {
                    this.data = Base64.decode(dataString, 0);
                } catch (IllegalArgumentException e) {
                    throw new ParserException("Error while parsing Base64 encoded string: " + dataString, e);
                }
            }
            this.data = URLDecoder.decode(dataString, C.ASCII_NAME).getBytes();
            return (long) this.data.length;
        }
        throw new ParserException("Unsupported scheme: " + scheme);
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
