package org.telegram.messenger.exoplayer2.audio;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public interface AudioProcessor {
    public static final ByteBuffer EMPTY_BUFFER = ByteBuffer.allocateDirect(0).order(ByteOrder.nativeOrder());

    public static final class UnhandledFormatException extends Exception {
        public UnhandledFormatException(int sampleRateHz, int channelCount, int encoding) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Unhandled format: ");
            stringBuilder.append(sampleRateHz);
            stringBuilder.append(" Hz, ");
            stringBuilder.append(channelCount);
            stringBuilder.append(" channels in encoding ");
            stringBuilder.append(encoding);
            super(stringBuilder.toString());
        }
    }

    boolean configure(int i, int i2, int i3) throws UnhandledFormatException;

    void flush();

    ByteBuffer getOutput();

    int getOutputChannelCount();

    int getOutputEncoding();

    int getOutputSampleRateHz();

    boolean isActive();

    boolean isEnded();

    void queueEndOfStream();

    void queueInput(ByteBuffer byteBuffer);

    void reset();
}
