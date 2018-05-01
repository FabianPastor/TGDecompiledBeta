package org.telegram.messenger.exoplayer2.source.smoothstreaming;

import android.util.Base64;
import java.io.IOException;
import java.util.ArrayList;
import org.telegram.messenger.exoplayer2.C0542C;
import org.telegram.messenger.exoplayer2.SeekParameters;
import org.telegram.messenger.exoplayer2.extractor.mp4.TrackEncryptionBox;
import org.telegram.messenger.exoplayer2.source.CompositeSequenceableLoaderFactory;
import org.telegram.messenger.exoplayer2.source.MediaPeriod;
import org.telegram.messenger.exoplayer2.source.MediaSourceEventListener.EventDispatcher;
import org.telegram.messenger.exoplayer2.source.SampleStream;
import org.telegram.messenger.exoplayer2.source.SequenceableLoader;
import org.telegram.messenger.exoplayer2.source.SequenceableLoader.Callback;
import org.telegram.messenger.exoplayer2.source.TrackGroup;
import org.telegram.messenger.exoplayer2.source.TrackGroupArray;
import org.telegram.messenger.exoplayer2.source.chunk.ChunkSampleStream;
import org.telegram.messenger.exoplayer2.source.smoothstreaming.SsChunkSource.Factory;
import org.telegram.messenger.exoplayer2.source.smoothstreaming.manifest.SsManifest;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelection;
import org.telegram.messenger.exoplayer2.upstream.Allocator;
import org.telegram.messenger.exoplayer2.upstream.LoaderErrorThrower;

final class SsMediaPeriod implements MediaPeriod, Callback<ChunkSampleStream<SsChunkSource>> {
    private static final int INITIALIZATION_VECTOR_SIZE = 8;
    private final Allocator allocator;
    private MediaPeriod.Callback callback;
    private final Factory chunkSourceFactory;
    private SequenceableLoader compositeSequenceableLoader;
    private final CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory;
    private final EventDispatcher eventDispatcher;
    private SsManifest manifest;
    private final LoaderErrorThrower manifestLoaderErrorThrower;
    private final int minLoadableRetryCount;
    private ChunkSampleStream<SsChunkSource>[] sampleStreams;
    private final TrackEncryptionBox[] trackEncryptionBoxes;
    private final TrackGroupArray trackGroups;

    public long readDiscontinuity() {
        return C0542C.TIME_UNSET;
    }

    public SsMediaPeriod(SsManifest ssManifest, Factory factory, CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory, int i, EventDispatcher eventDispatcher, LoaderErrorThrower loaderErrorThrower, Allocator allocator) {
        this.chunkSourceFactory = factory;
        this.manifestLoaderErrorThrower = loaderErrorThrower;
        this.minLoadableRetryCount = i;
        this.eventDispatcher = eventDispatcher;
        this.allocator = allocator;
        this.compositeSequenceableLoaderFactory = compositeSequenceableLoaderFactory;
        this.trackGroups = buildTrackGroups(ssManifest);
        factory = ssManifest.protectionElement;
        if (factory != null) {
            byte[] protectionElementKeyId = getProtectionElementKeyId(factory.data);
            this.trackEncryptionBoxes = new TrackEncryptionBox[]{new TrackEncryptionBox(true, null, 8, protectionElementKeyId, 0, 0, null)};
        } else {
            this.trackEncryptionBoxes = null;
        }
        this.manifest = ssManifest;
        this.sampleStreams = newSampleStreamArray(0);
        this.compositeSequenceableLoader = compositeSequenceableLoaderFactory.createCompositeSequenceableLoader(this.sampleStreams);
    }

    public void updateManifest(SsManifest ssManifest) {
        this.manifest = ssManifest;
        for (ChunkSampleStream chunkSource : this.sampleStreams) {
            ((SsChunkSource) chunkSource.getChunkSource()).updateManifest(ssManifest);
        }
        this.callback.onContinueLoadingRequested(this);
    }

    public void release() {
        for (ChunkSampleStream release : this.sampleStreams) {
            release.release();
        }
    }

    public void prepare(MediaPeriod.Callback callback, long j) {
        this.callback = callback;
        callback.onPrepared(this);
    }

    public void maybeThrowPrepareError() throws IOException {
        this.manifestLoaderErrorThrower.maybeThrowError();
    }

    public TrackGroupArray getTrackGroups() {
        return this.trackGroups;
    }

    public long selectTracks(TrackSelection[] trackSelectionArr, boolean[] zArr, SampleStream[] sampleStreamArr, boolean[] zArr2, long j) {
        ArrayList arrayList = new ArrayList();
        int i = 0;
        while (i < trackSelectionArr.length) {
            ChunkSampleStream chunkSampleStream;
            if (sampleStreamArr[i] != null) {
                chunkSampleStream = (ChunkSampleStream) sampleStreamArr[i];
                if (trackSelectionArr[i] != null) {
                    if (zArr[i]) {
                        arrayList.add(chunkSampleStream);
                    }
                }
                chunkSampleStream.release();
                sampleStreamArr[i] = null;
            }
            if (sampleStreamArr[i] == null && trackSelectionArr[i] != null) {
                chunkSampleStream = buildSampleStream(trackSelectionArr[i], j);
                arrayList.add(chunkSampleStream);
                sampleStreamArr[i] = chunkSampleStream;
                zArr2[i] = true;
            }
            i++;
        }
        this.sampleStreams = newSampleStreamArray(arrayList.size());
        arrayList.toArray(this.sampleStreams);
        this.compositeSequenceableLoader = this.compositeSequenceableLoaderFactory.createCompositeSequenceableLoader(this.sampleStreams);
        return j;
    }

    public void discardBuffer(long j, boolean z) {
        for (ChunkSampleStream discardBuffer : this.sampleStreams) {
            discardBuffer.discardBuffer(j, z);
        }
    }

    public void reevaluateBuffer(long j) {
        this.compositeSequenceableLoader.reevaluateBuffer(j);
    }

    public boolean continueLoading(long j) {
        return this.compositeSequenceableLoader.continueLoading(j);
    }

    public long getNextLoadPositionUs() {
        return this.compositeSequenceableLoader.getNextLoadPositionUs();
    }

    public long getBufferedPositionUs() {
        return this.compositeSequenceableLoader.getBufferedPositionUs();
    }

    public long seekToUs(long j) {
        for (ChunkSampleStream seekToUs : this.sampleStreams) {
            seekToUs.seekToUs(j);
        }
        return j;
    }

    public long getAdjustedSeekPositionUs(long j, SeekParameters seekParameters) {
        for (ChunkSampleStream chunkSampleStream : this.sampleStreams) {
            if (chunkSampleStream.primaryTrackType == 2) {
                return chunkSampleStream.getAdjustedSeekPositionUs(j, seekParameters);
            }
        }
        return j;
    }

    public void onContinueLoadingRequested(ChunkSampleStream<SsChunkSource> chunkSampleStream) {
        this.callback.onContinueLoadingRequested(this);
    }

    private ChunkSampleStream<SsChunkSource> buildSampleStream(TrackSelection trackSelection, long j) {
        int indexOf = this.trackGroups.indexOf(trackSelection.getTrackGroup());
        return new ChunkSampleStream(this.manifest.streamElements[indexOf].type, null, null, this.chunkSourceFactory.createChunkSource(this.manifestLoaderErrorThrower, this.manifest, indexOf, trackSelection, this.trackEncryptionBoxes), this, this.allocator, j, this.minLoadableRetryCount, this.eventDispatcher);
    }

    private static TrackGroupArray buildTrackGroups(SsManifest ssManifest) {
        TrackGroup[] trackGroupArr = new TrackGroup[ssManifest.streamElements.length];
        for (int i = 0; i < ssManifest.streamElements.length; i++) {
            trackGroupArr[i] = new TrackGroup(ssManifest.streamElements[i].formats);
        }
        return new TrackGroupArray(trackGroupArr);
    }

    private static ChunkSampleStream<SsChunkSource>[] newSampleStreamArray(int i) {
        return new ChunkSampleStream[i];
    }

    private static byte[] getProtectionElementKeyId(byte[] bArr) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < bArr.length; i += 2) {
            stringBuilder.append((char) bArr[i]);
        }
        bArr = stringBuilder.toString();
        bArr = Base64.decode(bArr.substring(bArr.indexOf("<KID>") + 5, bArr.indexOf("</KID>")), 0);
        swap(bArr, 0, 3);
        swap(bArr, 1, 2);
        swap(bArr, 4, 5);
        swap(bArr, 6, 7);
        return bArr;
    }

    private static void swap(byte[] bArr, int i, int i2) {
        byte b = bArr[i];
        bArr[i] = bArr[i2];
        bArr[i2] = b;
    }
}
