package org.telegram.messenger.exoplayer2.metadata;

import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import java.nio.ByteBuffer;
import org.telegram.messenger.exoplayer2.BaseRenderer;
import org.telegram.messenger.exoplayer2.ExoPlaybackException;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.FormatHolder;
import org.telegram.messenger.exoplayer2.decoder.DecoderInputBuffer;
import org.telegram.messenger.exoplayer2.util.Assertions;

public final class MetadataRenderer<T> extends BaseRenderer implements Callback {
    private static final int MSG_INVOKE_RENDERER = 0;
    private final DecoderInputBuffer buffer;
    private final FormatHolder formatHolder;
    private boolean inputStreamEnded;
    private final MetadataDecoder<T> metadataDecoder;
    private final Output<T> output;
    private final Handler outputHandler;
    private T pendingMetadata;
    private long pendingMetadataTimestamp;

    public interface Output<T> {
        void onMetadata(T t);
    }

    public MetadataRenderer(Output<T> output, Looper outputLooper, MetadataDecoder<T> metadataDecoder) {
        super(4);
        this.output = (Output) Assertions.checkNotNull(output);
        this.outputHandler = outputLooper == null ? null : new Handler(outputLooper, this);
        this.metadataDecoder = (MetadataDecoder) Assertions.checkNotNull(metadataDecoder);
        this.formatHolder = new FormatHolder();
        this.buffer = new DecoderInputBuffer(1);
    }

    public int supportsFormat(Format format) {
        return this.metadataDecoder.canDecode(format.sampleMimeType) ? 3 : 0;
    }

    protected void onPositionReset(long positionUs, boolean joining) {
        this.pendingMetadata = null;
        this.inputStreamEnded = false;
    }

    public void render(long positionUs, long elapsedRealtimeUs) throws ExoPlaybackException {
        if (!this.inputStreamEnded && this.pendingMetadata == null) {
            this.buffer.clear();
            if (readSource(this.formatHolder, this.buffer) == -4) {
                if (this.buffer.isEndOfStream()) {
                    this.inputStreamEnded = true;
                } else {
                    this.pendingMetadataTimestamp = this.buffer.timeUs;
                    try {
                        this.buffer.flip();
                        ByteBuffer bufferData = this.buffer.data;
                        this.pendingMetadata = this.metadataDecoder.decode(bufferData.array(), bufferData.limit());
                    } catch (MetadataDecoderException e) {
                        throw ExoPlaybackException.createForRenderer(e, getIndex());
                    }
                }
            }
        }
        if (this.pendingMetadata != null && this.pendingMetadataTimestamp <= positionUs) {
            invokeRenderer(this.pendingMetadata);
            this.pendingMetadata = null;
        }
    }

    protected void onDisabled() {
        this.pendingMetadata = null;
        super.onDisabled();
    }

    public boolean isEnded() {
        return this.inputStreamEnded;
    }

    public boolean isReady() {
        return true;
    }

    private void invokeRenderer(T metadata) {
        if (this.outputHandler != null) {
            this.outputHandler.obtainMessage(0, metadata).sendToTarget();
        } else {
            invokeRendererInternal(metadata);
        }
    }

    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case 0:
                invokeRendererInternal(msg.obj);
                return true;
            default:
                return false;
        }
    }

    private void invokeRendererInternal(T metadata) {
        this.output.onMetadata(metadata);
    }
}
