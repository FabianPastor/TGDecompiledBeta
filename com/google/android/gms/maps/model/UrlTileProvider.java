package com.google.android.gms.maps.model;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

public abstract class UrlTileProvider implements TileProvider {
    private final int zzrW;
    private final int zzrX;

    public UrlTileProvider(int i, int i2) {
        this.zzrW = i;
        this.zzrX = i2;
    }

    private static long zza(InputStream inputStream, OutputStream outputStream) throws IOException {
        byte[] bArr = new byte[4096];
        long j = 0;
        while (true) {
            int read = inputStream.read(bArr);
            if (read == -1) {
                return j;
            }
            outputStream.write(bArr, 0, read);
            j += (long) read;
        }
    }

    public final Tile getTile(int i, int i2, int i3) {
        URL tileUrl = getTileUrl(i, i2, i3);
        if (tileUrl == null) {
            return NO_TILE;
        }
        try {
            int i4 = this.zzrW;
            int i5 = this.zzrX;
            InputStream openStream = tileUrl.openStream();
            OutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            zza(openStream, byteArrayOutputStream);
            return new Tile(i4, i5, byteArrayOutputStream.toByteArray());
        } catch (IOException e) {
            return null;
        }
    }

    public abstract URL getTileUrl(int i, int i2, int i3);
}
