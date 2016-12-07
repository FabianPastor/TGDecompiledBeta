package org.telegram.messenger.exoplayer2.source.dash;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.telegram.messenger.exoplayer2.C;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.source.AdaptiveMediaSourceEventListener.EventDispatcher;
import org.telegram.messenger.exoplayer2.source.CompositeSequenceableLoader;
import org.telegram.messenger.exoplayer2.source.MediaPeriod;
import org.telegram.messenger.exoplayer2.source.SampleStream;
import org.telegram.messenger.exoplayer2.source.SequenceableLoader.Callback;
import org.telegram.messenger.exoplayer2.source.TrackGroup;
import org.telegram.messenger.exoplayer2.source.TrackGroupArray;
import org.telegram.messenger.exoplayer2.source.chunk.ChunkSampleStream;
import org.telegram.messenger.exoplayer2.source.dash.DashChunkSource.Factory;
import org.telegram.messenger.exoplayer2.source.dash.manifest.AdaptationSet;
import org.telegram.messenger.exoplayer2.source.dash.manifest.DashManifest;
import org.telegram.messenger.exoplayer2.source.dash.manifest.Period;
import org.telegram.messenger.exoplayer2.source.dash.manifest.Representation;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelection;
import org.telegram.messenger.exoplayer2.upstream.Allocator;
import org.telegram.messenger.exoplayer2.upstream.LoaderErrorThrower;

final class DashMediaPeriod implements MediaPeriod, Callback<ChunkSampleStream<DashChunkSource>> {
    private final Allocator allocator;
    private MediaPeriod.Callback callback;
    private final Factory chunkSourceFactory;
    private final long elapsedRealtimeOffset;
    private final EventDispatcher eventDispatcher;
    final int id;
    private int index;
    private DashManifest manifest;
    private final LoaderErrorThrower manifestLoaderErrorThrower;
    private final int minLoadableRetryCount;
    private Period period;
    private ChunkSampleStream<DashChunkSource>[] sampleStreams = newSampleStreamArray(0);
    private CompositeSequenceableLoader sequenceableLoader = new CompositeSequenceableLoader(this.sampleStreams);
    private final TrackGroupArray trackGroups;

    public DashMediaPeriod(int id, DashManifest manifest, int index, Factory chunkSourceFactory, int minLoadableRetryCount, EventDispatcher eventDispatcher, long elapsedRealtimeOffset, LoaderErrorThrower manifestLoaderErrorThrower, Allocator allocator) {
        this.id = id;
        this.manifest = manifest;
        this.index = index;
        this.chunkSourceFactory = chunkSourceFactory;
        this.minLoadableRetryCount = minLoadableRetryCount;
        this.eventDispatcher = eventDispatcher;
        this.elapsedRealtimeOffset = elapsedRealtimeOffset;
        this.manifestLoaderErrorThrower = manifestLoaderErrorThrower;
        this.allocator = allocator;
        this.period = manifest.getPeriod(index);
        this.trackGroups = buildTrackGroups(this.period);
    }

    public void updateManifest(DashManifest manifest, int index) {
        this.manifest = manifest;
        this.index = index;
        this.period = manifest.getPeriod(index);
        if (this.sampleStreams != null) {
            for (ChunkSampleStream<DashChunkSource> sampleStream : this.sampleStreams) {
                ((DashChunkSource) sampleStream.getChunkSource()).updateManifest(manifest, index);
            }
            this.callback.onContinueLoadingRequested(this);
        }
    }

    public void release() {
        for (ChunkSampleStream<DashChunkSource> sampleStream : this.sampleStreams) {
            sampleStream.release();
        }
    }

    public void prepare(MediaPeriod.Callback callback) {
        this.callback = callback;
        callback.onPrepared(this);
    }

    public void maybeThrowPrepareError() throws IOException {
        this.manifestLoaderErrorThrower.maybeThrowError();
    }

    public TrackGroupArray getTrackGroups() {
        return this.trackGroups;
    }

    public long selectTracks(TrackSelection[] selections, boolean[] mayRetainStreamFlags, SampleStream[] streams, boolean[] streamResetFlags, long positionUs) {
        ArrayList<ChunkSampleStream<DashChunkSource>> sampleStreamsList = new ArrayList();
        int i = 0;
        while (i < selections.length) {
            ChunkSampleStream<DashChunkSource> stream;
            if (streams[i] != null) {
                stream = streams[i];
                if (selections[i] == null || !mayRetainStreamFlags[i]) {
                    stream.release();
                    streams[i] = null;
                } else {
                    sampleStreamsList.add(stream);
                }
            }
            if (streams[i] == null && selections[i] != null) {
                stream = buildSampleStream(selections[i], positionUs);
                sampleStreamsList.add(stream);
                streams[i] = stream;
                streamResetFlags[i] = true;
            }
            i++;
        }
        this.sampleStreams = newSampleStreamArray(sampleStreamsList.size());
        sampleStreamsList.toArray(this.sampleStreams);
        this.sequenceableLoader = new CompositeSequenceableLoader(this.sampleStreams);
        return positionUs;
    }

    public boolean continueLoading(long positionUs) {
        return this.sequenceableLoader.continueLoading(positionUs);
    }

    public long getNextLoadPositionUs() {
        return this.sequenceableLoader.getNextLoadPositionUs();
    }

    public long readDiscontinuity() {
        return C.TIME_UNSET;
    }

    public long getBufferedPositionUs() {
        long bufferedPositionUs = Long.MAX_VALUE;
        for (ChunkSampleStream<DashChunkSource> sampleStream : this.sampleStreams) {
            long rendererBufferedPositionUs = sampleStream.getBufferedPositionUs();
            if (rendererBufferedPositionUs != Long.MIN_VALUE) {
                bufferedPositionUs = Math.min(bufferedPositionUs, rendererBufferedPositionUs);
            }
        }
        return bufferedPositionUs == Long.MAX_VALUE ? Long.MIN_VALUE : bufferedPositionUs;
    }

    public long seekToUs(long positionUs) {
        for (ChunkSampleStream<DashChunkSource> sampleStream : this.sampleStreams) {
            sampleStream.seekToUs(positionUs);
        }
        return positionUs;
    }

    public void onContinueLoadingRequested(ChunkSampleStream<DashChunkSource> chunkSampleStream) {
        this.callback.onContinueLoadingRequested(this);
    }

    private static TrackGroupArray buildTrackGroups(Period period) {
        TrackGroup[] trackGroupArray = new TrackGroup[period.adaptationSets.size()];
        for (int i = 0; i < period.adaptationSets.size(); i++) {
            List<Representation> representations = ((AdaptationSet) period.adaptationSets.get(i)).representations;
            Format[] formats = new Format[representations.size()];
            for (int j = 0; j < formats.length; j++) {
                formats[j] = ((Representation) representations.get(j)).format;
            }
            trackGroupArray[i] = new TrackGroup(formats);
        }
        return new TrackGroupArray(trackGroupArray);
    }

    private ChunkSampleStream<DashChunkSource> buildSampleStream(TrackSelection selection, long positionUs) {
        int adaptationSetIndex = this.trackGroups.indexOf(selection.getTrackGroup());
        AdaptationSet adaptationSet = (AdaptationSet) this.period.adaptationSets.get(adaptationSetIndex);
        return new ChunkSampleStream(adaptationSet.type, this.chunkSourceFactory.createDashChunkSource(this.manifestLoaderErrorThrower, this.manifest, this.index, adaptationSetIndex, selection, this.elapsedRealtimeOffset), this, this.allocator, positionUs, this.minLoadableRetryCount, this.eventDispatcher);
    }

    private static ChunkSampleStream<DashChunkSource>[] newSampleStreamArray(int length) {
        return new ChunkSampleStream[length];
    }
}
