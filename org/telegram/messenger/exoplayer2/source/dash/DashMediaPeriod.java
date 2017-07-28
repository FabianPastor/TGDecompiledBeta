package org.telegram.messenger.exoplayer2.source.dash;

import android.util.Pair;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.telegram.messenger.exoplayer2.C;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.source.AdaptiveMediaSourceEventListener.EventDispatcher;
import org.telegram.messenger.exoplayer2.source.CompositeSequenceableLoader;
import org.telegram.messenger.exoplayer2.source.EmptySampleStream;
import org.telegram.messenger.exoplayer2.source.MediaPeriod;
import org.telegram.messenger.exoplayer2.source.SampleStream;
import org.telegram.messenger.exoplayer2.source.SequenceableLoader.Callback;
import org.telegram.messenger.exoplayer2.source.TrackGroup;
import org.telegram.messenger.exoplayer2.source.TrackGroupArray;
import org.telegram.messenger.exoplayer2.source.chunk.ChunkSampleStream;
import org.telegram.messenger.exoplayer2.source.chunk.ChunkSampleStream.EmbeddedSampleStream;
import org.telegram.messenger.exoplayer2.source.dash.DashChunkSource.Factory;
import org.telegram.messenger.exoplayer2.source.dash.manifest.AdaptationSet;
import org.telegram.messenger.exoplayer2.source.dash.manifest.DashManifest;
import org.telegram.messenger.exoplayer2.source.dash.manifest.Representation;
import org.telegram.messenger.exoplayer2.source.dash.manifest.SchemeValuePair;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelection;
import org.telegram.messenger.exoplayer2.upstream.Allocator;
import org.telegram.messenger.exoplayer2.upstream.LoaderErrorThrower;
import org.telegram.messenger.exoplayer2.util.MimeTypes;

final class DashMediaPeriod implements MediaPeriod, Callback<ChunkSampleStream<DashChunkSource>> {
    private List<AdaptationSet> adaptationSets;
    private final Allocator allocator;
    private MediaPeriod.Callback callback;
    private final Factory chunkSourceFactory;
    private final long elapsedRealtimeOffset;
    private final EmbeddedTrackInfo[] embeddedTrackInfos;
    private final EventDispatcher eventDispatcher;
    final int id;
    private DashManifest manifest;
    private final LoaderErrorThrower manifestLoaderErrorThrower;
    private final int minLoadableRetryCount;
    private int periodIndex;
    private ChunkSampleStream<DashChunkSource>[] sampleStreams = newSampleStreamArray(0);
    private CompositeSequenceableLoader sequenceableLoader = new CompositeSequenceableLoader(this.sampleStreams);
    private final TrackGroupArray trackGroups;

    private static final class EmbeddedTrackInfo {
        public final int adaptationSetIndex;
        public final int trackType;

        public EmbeddedTrackInfo(int adaptationSetIndex, int trackType) {
            this.adaptationSetIndex = adaptationSetIndex;
            this.trackType = trackType;
        }
    }

    public DashMediaPeriod(int id, DashManifest manifest, int periodIndex, Factory chunkSourceFactory, int minLoadableRetryCount, EventDispatcher eventDispatcher, long elapsedRealtimeOffset, LoaderErrorThrower manifestLoaderErrorThrower, Allocator allocator) {
        this.id = id;
        this.manifest = manifest;
        this.periodIndex = periodIndex;
        this.chunkSourceFactory = chunkSourceFactory;
        this.minLoadableRetryCount = minLoadableRetryCount;
        this.eventDispatcher = eventDispatcher;
        this.elapsedRealtimeOffset = elapsedRealtimeOffset;
        this.manifestLoaderErrorThrower = manifestLoaderErrorThrower;
        this.allocator = allocator;
        this.adaptationSets = manifest.getPeriod(periodIndex).adaptationSets;
        Pair<TrackGroupArray, EmbeddedTrackInfo[]> result = buildTrackGroups(this.adaptationSets);
        this.trackGroups = (TrackGroupArray) result.first;
        this.embeddedTrackInfos = (EmbeddedTrackInfo[]) result.second;
    }

    public void updateManifest(DashManifest manifest, int periodIndex) {
        this.manifest = manifest;
        this.periodIndex = periodIndex;
        this.adaptationSets = manifest.getPeriod(periodIndex).adaptationSets;
        if (this.sampleStreams != null) {
            for (ChunkSampleStream<DashChunkSource> sampleStream : this.sampleStreams) {
                ((DashChunkSource) sampleStream.getChunkSource()).updateManifest(manifest, periodIndex);
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
        int trackGroupIndex;
        int adaptationSetCount = this.adaptationSets.size();
        HashMap<Integer, ChunkSampleStream<DashChunkSource>> primarySampleStreams = new HashMap();
        int i = 0;
        while (i < selections.length) {
            ChunkSampleStream<DashChunkSource> stream;
            if (streams[i] instanceof ChunkSampleStream) {
                stream = streams[i];
                if (selections[i] == null || !mayRetainStreamFlags[i]) {
                    stream.release();
                    streams[i] = null;
                } else {
                    primarySampleStreams.put(Integer.valueOf(this.trackGroups.indexOf(selections[i].getTrackGroup())), stream);
                }
            }
            if (streams[i] == null && selections[i] != null) {
                trackGroupIndex = this.trackGroups.indexOf(selections[i].getTrackGroup());
                if (trackGroupIndex < adaptationSetCount) {
                    stream = buildSampleStream(trackGroupIndex, selections[i], positionUs);
                    primarySampleStreams.put(Integer.valueOf(trackGroupIndex), stream);
                    streams[i] = stream;
                    streamResetFlags[i] = true;
                }
            }
            i++;
        }
        i = 0;
        while (i < selections.length) {
            if (((streams[i] instanceof EmbeddedSampleStream) || (streams[i] instanceof EmptySampleStream)) && (selections[i] == null || !mayRetainStreamFlags[i])) {
                releaseIfEmbeddedSampleStream(streams[i]);
                streams[i] = null;
            }
            if (selections[i] != null) {
                trackGroupIndex = this.trackGroups.indexOf(selections[i].getTrackGroup());
                if (trackGroupIndex >= adaptationSetCount) {
                    EmbeddedTrackInfo embeddedTrackInfo = this.embeddedTrackInfos[trackGroupIndex - adaptationSetCount];
                    ChunkSampleStream<?> primaryStream = (ChunkSampleStream) primarySampleStreams.get(Integer.valueOf(embeddedTrackInfo.adaptationSetIndex));
                    SampleStream stream2 = streams[i];
                    boolean mayRetainStream = primaryStream == null ? stream2 instanceof EmptySampleStream : (stream2 instanceof EmbeddedSampleStream) && ((EmbeddedSampleStream) stream2).parent == primaryStream;
                    if (!mayRetainStream) {
                        EmptySampleStream emptySampleStream;
                        releaseIfEmbeddedSampleStream(stream2);
                        if (primaryStream == null) {
                            emptySampleStream = new EmptySampleStream();
                        } else {
                            emptySampleStream = primaryStream.selectEmbeddedTrack(positionUs, embeddedTrackInfo.trackType);
                        }
                        streams[i] = emptySampleStream;
                        streamResetFlags[i] = true;
                    }
                }
            }
            i++;
        }
        this.sampleStreams = newSampleStreamArray(primarySampleStreams.size());
        primarySampleStreams.values().toArray(this.sampleStreams);
        this.sequenceableLoader = new CompositeSequenceableLoader(this.sampleStreams);
        return positionUs;
    }

    public void discardBuffer(long positionUs) {
        for (ChunkSampleStream<DashChunkSource> sampleStream : this.sampleStreams) {
            sampleStream.discardUnselectedEmbeddedTracksTo(positionUs);
        }
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

    private static Pair<TrackGroupArray, EmbeddedTrackInfo[]> buildTrackGroups(List<AdaptationSet> adaptationSets) {
        int adaptationSetCount = adaptationSets.size();
        int embeddedTrackCount = getEmbeddedTrackCount(adaptationSets);
        TrackGroup[] trackGroupArray = new TrackGroup[(adaptationSetCount + embeddedTrackCount)];
        EmbeddedTrackInfo[] embeddedTrackInfos = new EmbeddedTrackInfo[embeddedTrackCount];
        int i = 0;
        int embeddedTrackIndex = 0;
        while (i < adaptationSetCount) {
            int embeddedTrackIndex2;
            AdaptationSet adaptationSet = (AdaptationSet) adaptationSets.get(i);
            List<Representation> representations = adaptationSet.representations;
            Format[] formats = new Format[representations.size()];
            for (int j = 0; j < formats.length; j++) {
                formats[j] = ((Representation) representations.get(j)).format;
            }
            trackGroupArray[i] = new TrackGroup(formats);
            if (hasEventMessageTrack(adaptationSet)) {
                Format format = Format.createSampleFormat(adaptationSet.id + ":emsg", MimeTypes.APPLICATION_EMSG, null, -1, null);
                trackGroupArray[adaptationSetCount + embeddedTrackIndex] = new TrackGroup(format);
                embeddedTrackIndex2 = embeddedTrackIndex + 1;
                embeddedTrackInfos[embeddedTrackIndex] = new EmbeddedTrackInfo(i, 4);
            } else {
                embeddedTrackIndex2 = embeddedTrackIndex;
            }
            if (hasCea608Track(adaptationSet)) {
                format = Format.createTextSampleFormat(adaptationSet.id + ":cea608", MimeTypes.APPLICATION_CEA608, null, -1, 0, null, null);
                trackGroupArray[adaptationSetCount + embeddedTrackIndex2] = new TrackGroup(format);
                embeddedTrackIndex = embeddedTrackIndex2 + 1;
                embeddedTrackInfos[embeddedTrackIndex2] = new EmbeddedTrackInfo(i, 3);
                embeddedTrackIndex2 = embeddedTrackIndex;
            }
            i++;
            embeddedTrackIndex = embeddedTrackIndex2;
        }
        return Pair.create(new TrackGroupArray(trackGroupArray), embeddedTrackInfos);
    }

    private ChunkSampleStream<DashChunkSource> buildSampleStream(int adaptationSetIndex, TrackSelection selection, long positionUs) {
        AdaptationSet adaptationSet = (AdaptationSet) this.adaptationSets.get(adaptationSetIndex);
        int i = 0;
        int[] embeddedTrackTypes = new int[2];
        boolean enableEventMessageTrack = hasEventMessageTrack(adaptationSet);
        if (enableEventMessageTrack) {
            int embeddedTrackCount = 0 + 1;
            embeddedTrackTypes[0] = 4;
            i = embeddedTrackCount;
        }
        boolean enableCea608Track = hasCea608Track(adaptationSet);
        if (enableCea608Track) {
            embeddedTrackCount = i + 1;
            embeddedTrackTypes[i] = 3;
            i = embeddedTrackCount;
        }
        if (i < embeddedTrackTypes.length) {
            embeddedTrackTypes = Arrays.copyOf(embeddedTrackTypes, i);
        }
        return new ChunkSampleStream(adaptationSet.type, embeddedTrackTypes, this.chunkSourceFactory.createDashChunkSource(this.manifestLoaderErrorThrower, this.manifest, this.periodIndex, adaptationSetIndex, selection, this.elapsedRealtimeOffset, enableEventMessageTrack, enableCea608Track), this, this.allocator, positionUs, this.minLoadableRetryCount, this.eventDispatcher);
    }

    private static int getEmbeddedTrackCount(List<AdaptationSet> adaptationSets) {
        int embeddedTrackCount = 0;
        for (int i = 0; i < adaptationSets.size(); i++) {
            AdaptationSet adaptationSet = (AdaptationSet) adaptationSets.get(i);
            if (hasEventMessageTrack(adaptationSet)) {
                embeddedTrackCount++;
            }
            if (hasCea608Track(adaptationSet)) {
                embeddedTrackCount++;
            }
        }
        return embeddedTrackCount;
    }

    private static boolean hasEventMessageTrack(AdaptationSet adaptationSet) {
        List<Representation> representations = adaptationSet.representations;
        for (int i = 0; i < representations.size(); i++) {
            if (!((Representation) representations.get(i)).inbandEventStreams.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasCea608Track(AdaptationSet adaptationSet) {
        List<SchemeValuePair> descriptors = adaptationSet.accessibilityDescriptors;
        for (int i = 0; i < descriptors.size(); i++) {
            if ("urn:scte:dash:cc:cea-608:2015".equals(((SchemeValuePair) descriptors.get(i)).schemeIdUri)) {
                return true;
            }
        }
        return false;
    }

    private static ChunkSampleStream<DashChunkSource>[] newSampleStreamArray(int length) {
        return new ChunkSampleStream[length];
    }

    private static void releaseIfEmbeddedSampleStream(SampleStream sampleStream) {
        if (sampleStream instanceof EmbeddedSampleStream) {
            ((EmbeddedSampleStream) sampleStream).release();
        }
    }
}
