package okio;

import java.io.IOException;
import java.nio.channels.WritableByteChannel;

public interface BufferedSink extends Sink, WritableByteChannel {
    BufferedSink writeByte(int i) throws IOException;

    BufferedSink writeUtf8(String str) throws IOException;

    BufferedSink writeUtf8(String str, int i, int i2) throws IOException;
}
