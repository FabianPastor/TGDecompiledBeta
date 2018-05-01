package org.telegram.messenger.exoplayer2.upstream;

import android.net.Uri;
import java.io.IOException;
import org.telegram.messenger.exoplayer2.upstream.DataSource.Factory;

public final class DummyDataSource implements DataSource {
    public static final Factory FACTORY = new C18561();
    public static final DummyDataSource INSTANCE = new DummyDataSource();

    /* renamed from: org.telegram.messenger.exoplayer2.upstream.DummyDataSource$1 */
    static class C18561 implements Factory {
        C18561() {
        }

        public DataSource createDataSource() {
            return new DummyDataSource();
        }
    }

    public void close() throws IOException {
    }

    public Uri getUri() {
        return null;
    }

    private DummyDataSource() {
    }

    public long open(DataSpec dataSpec) throws IOException {
        throw new IOException("Dummy source");
    }

    public int read(byte[] bArr, int i, int i2) throws IOException {
        throw new UnsupportedOperationException();
    }
}
