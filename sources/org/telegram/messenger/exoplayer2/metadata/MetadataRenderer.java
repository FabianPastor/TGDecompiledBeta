package org.telegram.messenger.exoplayer2.metadata;

import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import java.util.Arrays;
import org.telegram.messenger.exoplayer2.BaseRenderer;
import org.telegram.messenger.exoplayer2.ExoPlaybackException;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.FormatHolder;
import org.telegram.messenger.exoplayer2.util.Assertions;

public final class MetadataRenderer extends BaseRenderer implements Callback {
    private static final int MAX_PENDING_METADATA_COUNT = 5;
    private static final int MSG_INVOKE_RENDERER = 0;
    private final MetadataInputBuffer buffer;
    private MetadataDecoder decoder;
    private final MetadataDecoderFactory decoderFactory;
    private final FormatHolder formatHolder;
    private boolean inputStreamEnded;
    private final MetadataOutput output;
    private final Handler outputHandler;
    private final Metadata[] pendingMetadata;
    private int pendingMetadataCount;
    private int pendingMetadataIndex;
    private final long[] pendingMetadataTimestamps;

    @Deprecated
    public interface Output extends MetadataOutput {
    }

    public boolean isReady() {
        return true;
    }

    public MetadataRenderer(MetadataOutput metadataOutput, Looper looper) {
        this(metadataOutput, looper, MetadataDecoderFactory.DEFAULT);
    }

    public MetadataRenderer(MetadataOutput metadataOutput, Looper looper, MetadataDecoderFactory metadataDecoderFactory) {
        super(4);
        this.output = (MetadataOutput) Assertions.checkNotNull(metadataOutput);
        if (looper == null) {
            metadataOutput = null;
        } else {
            metadataOutput = new Handler(looper, this);
        }
        this.outputHandler = metadataOutput;
        this.decoderFactory = (MetadataDecoderFactory) Assertions.checkNotNull(metadataDecoderFactory);
        this.formatHolder = new FormatHolder();
        this.buffer = new MetadataInputBuffer();
        this.pendingMetadata = new Metadata[5];
        this.pendingMetadataTimestamps = new long[5];
    }

    public int supportsFormat(Format format) {
        if (!this.decoderFactory.supportsFormat(format)) {
            return null;
        }
        return BaseRenderer.supportsFormatDrm(null, format.drmInitData) != null ? 4 : 2;
    }

    protected void onStreamChanged(Format[] formatArr, long j) throws ExoPlaybackException {
        this.decoder = this.decoderFactory.createDecoder(formatArr[0]);
    }

    protected void onPositionReset(long j, boolean z) {
        flushPendingMetadata();
        this.inputStreamEnded = 0;
    }

    public void render(long j, long j2) throws ExoPlaybackException {
        if (this.inputStreamEnded == null && this.pendingMetadataCount < 5) {
            this.buffer.clear();
            if (readSource(this.formatHolder, this.buffer, false) == -4) {
                if (this.buffer.isEndOfStream() != null) {
                    this.inputStreamEnded = true;
                } else if (this.buffer.isDecodeOnly() == null) {
                    this.buffer.subsampleOffsetUs = this.formatHolder.format.subsampleOffsetUs;
                    this.buffer.flip();
                    try {
                        j2 = (this.pendingMetadataIndex + this.pendingMetadataCount) % 5;
                        this.pendingMetadata[j2] = this.decoder.decode(this.buffer);
                        this.pendingMetadataTimestamps[j2] = this.buffer.timeUs;
                        this.pendingMetadataCount++;
                    } catch (long j3) {
                        throw ExoPlaybackException.createForRenderer(j3, getIndex());
                    }
                }
            }
        }
        if (this.pendingMetadataCount > null && this.pendingMetadataTimestamps[this.pendingMetadataIndex] <= j3) {
            invokeRenderer(this.pendingMetadata[this.pendingMetadataIndex]);
            this.pendingMetadata[this.pendingMetadataIndex] = 0;
            this.pendingMetadataIndex = (this.pendingMetadataIndex + 1) % 5;
            this.pendingMetadataCount--;
        }
    }

    protected void onDisabled() {
        flushPendingMetadata();
        this.decoder = null;
    }

    public boolean isEnded() {
        return this.inputStreamEnded;
    }

    private void invokeRenderer(Metadata metadata) {
        if (this.outputHandler != null) {
            this.outputHandler.obtainMessage(0, metadata).sendToTarget();
        } else {
            invokeRendererInternal(metadata);
        }
    }

    private void flushPendingMetadata() {
        Arrays.fill(this.pendingMetadata, null);
        this.pendingMetadataIndex = 0;
        this.pendingMetadataCount = 0;
    }

    public boolean handleMessage(Message message) {
        if (message.what != 0) {
            throw new IllegalStateException();
        }
        invokeRendererInternal((Metadata) message.obj);
        return true;
    }

    private void invokeRendererInternal(Metadata metadata) {
        this.output.onMetadata(metadata);
    }
}
