package org.telegram.messenger.exoplayer;

import java.util.Arrays;
import org.telegram.messenger.exoplayer.MediaCodecUtil.DecoderQueryException;
import org.telegram.messenger.exoplayer.SampleSource.SampleSourceReader;

public abstract class SampleSourceTrackRenderer extends TrackRenderer {
    private long durationUs;
    private SampleSourceReader enabledSource;
    private int enabledSourceTrackIndex;
    private int[] handledSourceIndices;
    private int[] handledSourceTrackIndices;
    private final SampleSourceReader[] sources;

    protected abstract void doSomeWork(long j, long j2, boolean z) throws ExoPlaybackException;

    protected abstract boolean handlesTrack(MediaFormat mediaFormat) throws DecoderQueryException;

    protected abstract void onDiscontinuity(long j) throws ExoPlaybackException;

    public SampleSourceTrackRenderer(SampleSource... sources) {
        this.sources = new SampleSourceReader[sources.length];
        for (int i = 0; i < sources.length; i++) {
            this.sources[i] = sources[i].register();
        }
    }

    protected final boolean doPrepare(long positionUs) throws ExoPlaybackException {
        boolean allSourcesPrepared = true;
        for (SampleSourceReader prepare : this.sources) {
            allSourcesPrepared &= prepare.prepare(positionUs);
        }
        if (!allSourcesPrepared) {
            return false;
        }
        int totalSourceTrackCount = 0;
        for (SampleSourceReader prepare2 : this.sources) {
            totalSourceTrackCount += prepare2.getTrackCount();
        }
        long durationUs = 0;
        int handledTrackCount = 0;
        int[] handledSourceIndices = new int[totalSourceTrackCount];
        int[] handledTrackIndices = new int[totalSourceTrackCount];
        int sourceCount = this.sources.length;
        for (int sourceIndex = 0; sourceIndex < sourceCount; sourceIndex++) {
            SampleSourceReader source = this.sources[sourceIndex];
            int sourceTrackCount = source.getTrackCount();
            int trackIndex = 0;
            while (trackIndex < sourceTrackCount) {
                MediaFormat format = source.getFormat(trackIndex);
                try {
                    if (handlesTrack(format)) {
                        handledSourceIndices[handledTrackCount] = sourceIndex;
                        handledTrackIndices[handledTrackCount] = trackIndex;
                        handledTrackCount++;
                        if (durationUs != -1) {
                            long trackDurationUs = format.durationUs;
                            if (trackDurationUs == -1) {
                                durationUs = -1;
                            } else if (trackDurationUs != -2) {
                                durationUs = Math.max(durationUs, trackDurationUs);
                            }
                        }
                    }
                    trackIndex++;
                } catch (Throwable e) {
                    throw new ExoPlaybackException(e);
                }
            }
        }
        this.durationUs = durationUs;
        this.handledSourceIndices = Arrays.copyOf(handledSourceIndices, handledTrackCount);
        this.handledSourceTrackIndices = Arrays.copyOf(handledTrackIndices, handledTrackCount);
        return true;
    }

    protected void onEnabled(int track, long positionUs, boolean joining) throws ExoPlaybackException {
        positionUs = shiftInputPosition(positionUs);
        this.enabledSource = this.sources[this.handledSourceIndices[track]];
        this.enabledSourceTrackIndex = this.handledSourceTrackIndices[track];
        this.enabledSource.enable(this.enabledSourceTrackIndex, positionUs);
        onDiscontinuity(positionUs);
    }

    protected final void seekTo(long positionUs) throws ExoPlaybackException {
        positionUs = shiftInputPosition(positionUs);
        this.enabledSource.seekToUs(positionUs);
        checkForDiscontinuity(positionUs);
    }

    protected final void doSomeWork(long positionUs, long elapsedRealtimeUs) throws ExoPlaybackException {
        positionUs = shiftInputPosition(positionUs);
        doSomeWork(checkForDiscontinuity(positionUs), elapsedRealtimeUs, this.enabledSource.continueBuffering(this.enabledSourceTrackIndex, positionUs));
    }

    protected long getBufferedPositionUs() {
        return this.enabledSource.getBufferedPositionUs();
    }

    protected long getDurationUs() {
        return this.durationUs;
    }

    protected void maybeThrowError() throws ExoPlaybackException {
        if (this.enabledSource != null) {
            maybeThrowError(this.enabledSource);
            return;
        }
        for (SampleSourceReader maybeThrowError : this.sources) {
            maybeThrowError(maybeThrowError);
        }
    }

    protected void onDisabled() throws ExoPlaybackException {
        this.enabledSource.disable(this.enabledSourceTrackIndex);
        this.enabledSource = null;
    }

    protected void onReleased() throws ExoPlaybackException {
        for (SampleSourceReader release : this.sources) {
            release.release();
        }
    }

    protected final int getTrackCount() {
        return this.handledSourceTrackIndices.length;
    }

    protected final MediaFormat getFormat(int track) {
        return this.sources[this.handledSourceIndices[track]].getFormat(this.handledSourceTrackIndices[track]);
    }

    protected long shiftInputPosition(long positionUs) {
        return positionUs;
    }

    protected final int readSource(long positionUs, MediaFormatHolder formatHolder, SampleHolder sampleHolder) {
        return this.enabledSource.readData(this.enabledSourceTrackIndex, positionUs, formatHolder, sampleHolder);
    }

    private long checkForDiscontinuity(long positionUs) throws ExoPlaybackException {
        long discontinuityPositionUs = this.enabledSource.readDiscontinuity(this.enabledSourceTrackIndex);
        if (discontinuityPositionUs == Long.MIN_VALUE) {
            return positionUs;
        }
        onDiscontinuity(discontinuityPositionUs);
        return discontinuityPositionUs;
    }

    private void maybeThrowError(SampleSourceReader source) throws ExoPlaybackException {
        try {
            source.maybeThrowError();
        } catch (Throwable e) {
            throw new ExoPlaybackException(e);
        }
    }
}
