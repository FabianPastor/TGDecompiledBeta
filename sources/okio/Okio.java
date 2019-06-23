package okio;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

public final class Okio {
    static final Logger logger = Logger.getLogger(Okio.class.getName());

    private Okio() {
    }

    public static BufferedSource buffer(Source source) {
        return new RealBufferedSource(source);
    }

    public static Source source(InputStream inputStream) {
        return source(inputStream, new Timeout());
    }

    private static Source source(final InputStream inputStream, final Timeout timeout) {
        if (inputStream == null) {
            throw new IllegalArgumentException("in == null");
        } else if (timeout != null) {
            return new Source() {
                public long read(Buffer buffer, long j) throws IOException {
                    if (j < 0) {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("byteCount < 0: ");
                        stringBuilder.append(j);
                        throw new IllegalArgumentException(stringBuilder.toString());
                    } else if (j == 0) {
                        return 0;
                    } else {
                        try {
                            timeout.throwIfReached();
                            Segment writableSegment = buffer.writableSegment(1);
                            int read = inputStream.read(writableSegment.data, writableSegment.limit, (int) Math.min(j, (long) (8192 - writableSegment.limit)));
                            if (read == -1) {
                                return -1;
                            }
                            writableSegment.limit += read;
                            j = (long) read;
                            buffer.size += j;
                            return j;
                        } catch (AssertionError e) {
                            if (Okio.isAndroidGetsocknameError(e)) {
                                throw new IOException(e);
                            }
                            throw e;
                        }
                    }
                }

                public void close() throws IOException {
                    inputStream.close();
                }

                public String toString() {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("source(");
                    stringBuilder.append(inputStream);
                    stringBuilder.append(")");
                    return stringBuilder.toString();
                }
            };
        } else {
            throw new IllegalArgumentException("timeout == null");
        }
    }

    static boolean isAndroidGetsocknameError(AssertionError assertionError) {
        return (assertionError.getCause() == null || assertionError.getMessage() == null || !assertionError.getMessage().contains("getsockname failed")) ? false : true;
    }
}
