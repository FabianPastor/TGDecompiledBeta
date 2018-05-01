package org.telegram.messenger.exoplayer2.source.hls;

import android.os.Handler;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import org.telegram.messenger.exoplayer2.C0542C;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.SeekParameters;
import org.telegram.messenger.exoplayer2.source.CompositeSequenceableLoaderFactory;
import org.telegram.messenger.exoplayer2.source.MediaPeriod;
import org.telegram.messenger.exoplayer2.source.MediaSourceEventListener.EventDispatcher;
import org.telegram.messenger.exoplayer2.source.SampleStream;
import org.telegram.messenger.exoplayer2.source.SequenceableLoader;
import org.telegram.messenger.exoplayer2.source.TrackGroup;
import org.telegram.messenger.exoplayer2.source.TrackGroupArray;
import org.telegram.messenger.exoplayer2.source.hls.HlsSampleStreamWrapper.Callback;
import org.telegram.messenger.exoplayer2.source.hls.playlist.HlsMasterPlaylist;
import org.telegram.messenger.exoplayer2.source.hls.playlist.HlsMasterPlaylist.HlsUrl;
import org.telegram.messenger.exoplayer2.source.hls.playlist.HlsPlaylistTracker;
import org.telegram.messenger.exoplayer2.source.hls.playlist.HlsPlaylistTracker.PlaylistEventListener;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelection;
import org.telegram.messenger.exoplayer2.upstream.Allocator;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.MimeTypes;
import org.telegram.messenger.exoplayer2.util.Util;

public final class HlsMediaPeriod implements MediaPeriod, Callback, PlaylistEventListener {
    private final Allocator allocator;
    private final boolean allowChunklessPreparation;
    private MediaPeriod.Callback callback;
    private SequenceableLoader compositeSequenceableLoader;
    private final CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory;
    private final Handler continueLoadingHandler = new Handler();
    private final HlsDataSourceFactory dataSourceFactory;
    private HlsSampleStreamWrapper[] enabledSampleStreamWrappers = new HlsSampleStreamWrapper[0];
    private final EventDispatcher eventDispatcher;
    private final HlsExtractorFactory extractorFactory;
    private final int minLoadableRetryCount;
    private int pendingPrepareCount;
    private final HlsPlaylistTracker playlistTracker;
    private HlsSampleStreamWrapper[] sampleStreamWrappers = new HlsSampleStreamWrapper[0];
    private final IdentityHashMap<SampleStream, Integer> streamWrapperIndices = new IdentityHashMap();
    private final TimestampAdjusterProvider timestampAdjusterProvider = new TimestampAdjusterProvider();
    private TrackGroupArray trackGroups;

    public long getAdjustedSeekPositionUs(long j, SeekParameters seekParameters) {
        return j;
    }

    public long readDiscontinuity() {
        return C0542C.TIME_UNSET;
    }

    public HlsMediaPeriod(HlsExtractorFactory hlsExtractorFactory, HlsPlaylistTracker hlsPlaylistTracker, HlsDataSourceFactory hlsDataSourceFactory, int i, EventDispatcher eventDispatcher, Allocator allocator, CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory, boolean z) {
        this.extractorFactory = hlsExtractorFactory;
        this.playlistTracker = hlsPlaylistTracker;
        this.dataSourceFactory = hlsDataSourceFactory;
        this.minLoadableRetryCount = i;
        this.eventDispatcher = eventDispatcher;
        this.allocator = allocator;
        this.compositeSequenceableLoaderFactory = compositeSequenceableLoaderFactory;
        this.allowChunklessPreparation = z;
    }

    public void release() {
        this.playlistTracker.removeListener(this);
        this.continueLoadingHandler.removeCallbacksAndMessages(null);
        for (HlsSampleStreamWrapper release : this.sampleStreamWrappers) {
            release.release();
        }
    }

    public void prepare(MediaPeriod.Callback callback, long j) {
        this.callback = callback;
        this.playlistTracker.addListener(this);
        buildAndPrepareSampleStreamWrappers(j);
    }

    public void maybeThrowPrepareError() throws IOException {
        for (HlsSampleStreamWrapper maybeThrowPrepareError : this.sampleStreamWrappers) {
            maybeThrowPrepareError.maybeThrowPrepareError();
        }
    }

    public TrackGroupArray getTrackGroups() {
        return this.trackGroups;
    }

    public long selectTracks(TrackSelection[] trackSelectionArr, boolean[] zArr, SampleStream[] sampleStreamArr, boolean[] zArr2, long j) {
        int i;
        HlsSampleStreamWrapper[] hlsSampleStreamWrapperArr;
        HlsMediaPeriod hlsMediaPeriod = this;
        TrackSelection[] trackSelectionArr2 = trackSelectionArr;
        SampleStream[] sampleStreamArr2 = sampleStreamArr;
        int[] iArr = new int[trackSelectionArr2.length];
        int[] iArr2 = new int[trackSelectionArr2.length];
        for (int i2 = 0; i2 < trackSelectionArr2.length; i2++) {
            int i3;
            int i4;
            if (sampleStreamArr2[i2] == null) {
                i3 = -1;
            } else {
                i3 = ((Integer) hlsMediaPeriod.streamWrapperIndices.get(sampleStreamArr2[i2])).intValue();
            }
            iArr[i2] = i3;
            iArr2[i2] = -1;
            if (trackSelectionArr2[i2] != null) {
                TrackGroup trackGroup = trackSelectionArr2[i2].getTrackGroup();
                for (i4 = 0; i4 < hlsMediaPeriod.sampleStreamWrappers.length; i4++) {
                    if (hlsMediaPeriod.sampleStreamWrappers[i4].getTrackGroups().indexOf(trackGroup) != -1) {
                        iArr2[i2] = i4;
                        break;
                    }
                }
            }
        }
        hlsMediaPeriod.streamWrapperIndices.clear();
        Object obj = new SampleStream[trackSelectionArr2.length];
        SampleStream[] sampleStreamArr3 = new SampleStream[trackSelectionArr2.length];
        TrackSelection[] trackSelectionArr3 = new TrackSelection[trackSelectionArr2.length];
        HlsSampleStreamWrapper[] hlsSampleStreamWrapperArr2 = new HlsSampleStreamWrapper[hlsMediaPeriod.sampleStreamWrappers.length];
        int i5 = 0;
        int i6 = 0;
        boolean z = false;
        while (i6 < hlsMediaPeriod.sampleStreamWrappers.length) {
            int i7 = 0;
            while (i7 < trackSelectionArr2.length) {
                TrackSelection trackSelection = null;
                sampleStreamArr3[i7] = iArr[i7] == i6 ? sampleStreamArr2[i7] : null;
                if (iArr2[i7] == i6) {
                    trackSelection = trackSelectionArr2[i7];
                }
                trackSelectionArr3[i7] = trackSelection;
                i7++;
            }
            HlsSampleStreamWrapper hlsSampleStreamWrapper = hlsMediaPeriod.sampleStreamWrappers[i6];
            HlsSampleStreamWrapper hlsSampleStreamWrapper2 = hlsSampleStreamWrapper;
            i = i5;
            hlsSampleStreamWrapperArr = hlsSampleStreamWrapperArr2;
            int i8 = i6;
            TrackSelection[] trackSelectionArr4 = trackSelectionArr3;
            boolean selectTracks = hlsSampleStreamWrapper.selectTracks(trackSelectionArr3, zArr, sampleStreamArr3, zArr2, j, z);
            i4 = 0;
            boolean z2 = false;
            while (true) {
                boolean z3 = true;
                if (i4 >= trackSelectionArr2.length) {
                    break;
                }
                if (iArr2[i4] == i8) {
                    Assertions.checkState(sampleStreamArr3[i4] != null);
                    obj[i4] = sampleStreamArr3[i4];
                    hlsMediaPeriod.streamWrapperIndices.put(sampleStreamArr3[i4], Integer.valueOf(i8));
                    z2 = true;
                } else if (iArr[i4] == i8) {
                    if (sampleStreamArr3[i4] != null) {
                        z3 = false;
                    }
                    Assertions.checkState(z3);
                }
                i4++;
            }
            if (z2) {
                hlsSampleStreamWrapperArr[i] = hlsSampleStreamWrapper2;
                i5 = i + 1;
                if (i == 0) {
                    hlsSampleStreamWrapper2.setIsTimestampMaster(true);
                    if (!selectTracks && hlsMediaPeriod.enabledSampleStreamWrappers.length != 0) {
                        if (hlsSampleStreamWrapper2 != hlsMediaPeriod.enabledSampleStreamWrappers[0]) {
                        }
                    }
                    hlsMediaPeriod.timestampAdjusterProvider.reset();
                    z = true;
                } else {
                    hlsSampleStreamWrapper2.setIsTimestampMaster(false);
                }
            } else {
                i5 = i;
            }
            i6 = i8 + 1;
            hlsSampleStreamWrapperArr2 = hlsSampleStreamWrapperArr;
            trackSelectionArr3 = trackSelectionArr4;
            sampleStreamArr2 = sampleStreamArr;
        }
        i = i5;
        hlsSampleStreamWrapperArr = hlsSampleStreamWrapperArr2;
        System.arraycopy(obj, 0, sampleStreamArr, 0, obj.length);
        hlsMediaPeriod.enabledSampleStreamWrappers = (HlsSampleStreamWrapper[]) Arrays.copyOf(hlsSampleStreamWrapperArr, i5);
        hlsMediaPeriod.compositeSequenceableLoader = hlsMediaPeriod.compositeSequenceableLoaderFactory.createCompositeSequenceableLoader(hlsMediaPeriod.enabledSampleStreamWrappers);
        return j;
    }

    public void discardBuffer(long j, boolean z) {
        for (HlsSampleStreamWrapper discardBuffer : this.enabledSampleStreamWrappers) {
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
        if (this.enabledSampleStreamWrappers.length > 0) {
            boolean seekToUs = this.enabledSampleStreamWrappers[0].seekToUs(j, false);
            for (int i = 1; i < this.enabledSampleStreamWrappers.length; i++) {
                this.enabledSampleStreamWrappers[i].seekToUs(j, seekToUs);
            }
            if (seekToUs) {
                this.timestampAdjusterProvider.reset();
            }
        }
        return j;
    }

    public void onPrepared() {
        int i = this.pendingPrepareCount - 1;
        this.pendingPrepareCount = i;
        if (i <= 0) {
            HlsSampleStreamWrapper[] hlsSampleStreamWrapperArr = this.sampleStreamWrappers;
            int i2 = 0;
            int i3 = i2;
            while (i2 < hlsSampleStreamWrapperArr.length) {
                i3 += hlsSampleStreamWrapperArr[i2].getTrackGroups().length;
                i2++;
            }
            TrackGroup[] trackGroupArr = new TrackGroup[i3];
            HlsSampleStreamWrapper[] hlsSampleStreamWrapperArr2 = this.sampleStreamWrappers;
            i2 = hlsSampleStreamWrapperArr2.length;
            i3 = 0;
            int i4 = i3;
            while (i3 < i2) {
                HlsSampleStreamWrapper hlsSampleStreamWrapper = hlsSampleStreamWrapperArr2[i3];
                int i5 = hlsSampleStreamWrapper.getTrackGroups().length;
                int i6 = i4;
                i4 = 0;
                while (i4 < i5) {
                    int i7 = i6 + 1;
                    trackGroupArr[i6] = hlsSampleStreamWrapper.getTrackGroups().get(i4);
                    i4++;
                    i6 = i7;
                }
                i3++;
                i4 = i6;
            }
            this.trackGroups = new TrackGroupArray(trackGroupArr);
            this.callback.onPrepared(this);
        }
    }

    public void onPlaylistRefreshRequired(HlsUrl hlsUrl) {
        this.playlistTracker.refreshPlaylist(hlsUrl);
    }

    public void onContinueLoadingRequested(HlsSampleStreamWrapper hlsSampleStreamWrapper) {
        if (this.trackGroups != null) {
            this.callback.onContinueLoadingRequested(this);
        }
    }

    public void onPlaylistChanged() {
        continuePreparingOrLoading();
    }

    public void onPlaylistBlacklisted(HlsUrl hlsUrl, long j) {
        for (HlsSampleStreamWrapper onPlaylistBlacklisted : this.sampleStreamWrappers) {
            onPlaylistBlacklisted.onPlaylistBlacklisted(hlsUrl, j);
        }
        continuePreparingOrLoading();
    }

    private void buildAndPrepareSampleStreamWrappers(long j) {
        HlsMasterPlaylist masterPlaylist = this.playlistTracker.getMasterPlaylist();
        List list = masterPlaylist.audios;
        List list2 = masterPlaylist.subtitles;
        int size = (list.size() + 1) + list2.size();
        this.sampleStreamWrappers = new HlsSampleStreamWrapper[size];
        this.pendingPrepareCount = size;
        long j2 = j;
        buildAndPrepareMainSampleStreamWrapper(masterPlaylist, j2);
        int i = 0;
        int i2 = 1;
        int i3 = 0;
        while (i3 < list.size()) {
            HlsUrl hlsUrl = (HlsUrl) list.get(i3);
            HlsSampleStreamWrapper buildSampleStreamWrapper = buildSampleStreamWrapper(1, new HlsUrl[]{(HlsUrl) list.get(i3)}, null, Collections.emptyList(), j2);
            int i4 = i2 + 1;
            r7.sampleStreamWrappers[i2] = buildSampleStreamWrapper;
            Format format = hlsUrl.format;
            if (!r7.allowChunklessPreparation || format.codecs == null) {
                buildSampleStreamWrapper.continuePreparing();
            } else {
                TrackGroup[] trackGroupArr = new TrackGroup[1];
                trackGroupArr[0] = new TrackGroup(hlsUrl.format);
                buildSampleStreamWrapper.prepareWithMasterPlaylistInfo(new TrackGroupArray(trackGroupArr), 0);
            }
            i3++;
            i2 = i4;
            i = 0;
        }
        int i5 = 0;
        while (i5 < list2.size()) {
            hlsUrl = (HlsUrl) list2.get(i5);
            buildSampleStreamWrapper = buildSampleStreamWrapper(3, new HlsUrl[]{hlsUrl}, null, Collections.emptyList(), j2);
            i4 = i2 + 1;
            r7.sampleStreamWrappers[i2] = buildSampleStreamWrapper;
            trackGroupArr = new TrackGroup[1];
            trackGroupArr[0] = new TrackGroup(hlsUrl.format);
            buildSampleStreamWrapper.prepareWithMasterPlaylistInfo(new TrackGroupArray(trackGroupArr), 0);
            i5++;
            i2 = i4;
        }
        r7.enabledSampleStreamWrappers = r7.sampleStreamWrappers;
    }

    private void buildAndPrepareMainSampleStreamWrapper(HlsMasterPlaylist hlsMasterPlaylist, long j) {
        List list;
        HlsMediaPeriod hlsMediaPeriod = this;
        HlsMasterPlaylist hlsMasterPlaylist2 = hlsMasterPlaylist;
        List arrayList = new ArrayList(hlsMasterPlaylist2.variants);
        ArrayList arrayList2 = new ArrayList();
        Collection arrayList3 = new ArrayList();
        for (int i = 0; i < arrayList.size(); i++) {
            HlsUrl hlsUrl = (HlsUrl) arrayList.get(i);
            Format format = hlsUrl.format;
            if (format.height <= 0) {
                if (Util.getCodecsOfType(format.codecs, 2) == null) {
                    if (Util.getCodecsOfType(format.codecs, 1) != null) {
                        arrayList3.add(hlsUrl);
                    }
                }
            }
            arrayList2.add(hlsUrl);
        }
        if (arrayList2.isEmpty()) {
            if (arrayList3.size() < arrayList.size()) {
                arrayList.removeAll(arrayList3);
            }
            list = arrayList;
        } else {
            list = arrayList2;
        }
        Assertions.checkArgument(list.isEmpty() ^ true);
        HlsUrl[] hlsUrlArr = (HlsUrl[]) list.toArray(new HlsUrl[0]);
        String str = hlsUrlArr[0].format.codecs;
        HlsSampleStreamWrapper buildSampleStreamWrapper = buildSampleStreamWrapper(0, hlsUrlArr, hlsMasterPlaylist2.muxedAudioFormat, hlsMasterPlaylist2.muxedCaptionFormats, j);
        hlsMediaPeriod.sampleStreamWrappers[0] = buildSampleStreamWrapper;
        if (!hlsMediaPeriod.allowChunklessPreparation || str == null) {
            buildSampleStreamWrapper.setIsTimestampMaster(true);
            buildSampleStreamWrapper.continuePreparing();
            return;
        }
        boolean z = Util.getCodecsOfType(str, 2) != null;
        boolean z2 = Util.getCodecsOfType(str, 1) != null;
        List arrayList4 = new ArrayList();
        Format[] formatArr;
        int i2;
        if (z) {
            formatArr = new Format[list.size()];
            for (int i3 = 0; i3 < formatArr.length; i3++) {
                formatArr[i3] = deriveVideoFormat(hlsUrlArr[i3].format);
            }
            arrayList4.add(new TrackGroup(formatArr));
            if (z2 && (hlsMasterPlaylist2.muxedAudioFormat != null || hlsMasterPlaylist2.audios.isEmpty())) {
                arrayList4.add(new TrackGroup(deriveMuxedAudioFormat(hlsUrlArr[0].format, hlsMasterPlaylist2.muxedAudioFormat, -1)));
            }
            List list2 = hlsMasterPlaylist2.muxedCaptionFormats;
            if (list2 != null) {
                for (i2 = 0; i2 < list2.size(); i2++) {
                    arrayList4.add(new TrackGroup((Format) list2.get(i2)));
                }
            }
        } else if (z2) {
            formatArr = new Format[list.size()];
            for (i2 = 0; i2 < formatArr.length; i2++) {
                Format format2 = hlsUrlArr[i2].format;
                formatArr[i2] = deriveMuxedAudioFormat(format2, hlsMasterPlaylist2.muxedAudioFormat, format2.bitrate);
            }
            arrayList4.add(new TrackGroup(formatArr));
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Unexpected codecs attribute: ");
            stringBuilder.append(str);
            throw new IllegalArgumentException(stringBuilder.toString());
        }
        buildSampleStreamWrapper.prepareWithMasterPlaylistInfo(new TrackGroupArray((TrackGroup[]) arrayList4.toArray(new TrackGroup[0])), 0);
    }

    private HlsSampleStreamWrapper buildSampleStreamWrapper(int i, HlsUrl[] hlsUrlArr, Format format, List<Format> list, long j) {
        return new HlsSampleStreamWrapper(i, this, new HlsChunkSource(this.extractorFactory, this.playlistTracker, hlsUrlArr, this.dataSourceFactory, this.timestampAdjusterProvider, list), this.allocator, j, format, this.minLoadableRetryCount, this.eventDispatcher);
    }

    private void continuePreparingOrLoading() {
        if (this.trackGroups != null) {
            this.callback.onContinueLoadingRequested(this);
            return;
        }
        for (HlsSampleStreamWrapper continuePreparing : this.sampleStreamWrappers) {
            continuePreparing.continuePreparing();
        }
    }

    private static Format deriveVideoFormat(Format format) {
        String codecsOfType = Util.getCodecsOfType(format.codecs, 2);
        return Format.createVideoSampleFormat(format.id, MimeTypes.getMediaMimeType(codecsOfType), codecsOfType, format.bitrate, -1, format.width, format.height, format.frameRate, null, null);
    }

    private static Format deriveMuxedAudioFormat(Format format, Format format2, int i) {
        String str;
        int i2;
        int i3;
        String str2;
        Format format3 = format;
        Format format4 = format2;
        if (format4 != null) {
            str = format4.codecs;
            i2 = format4.channelCount;
            i3 = format4.selectionFlags;
            str2 = format4.language;
        } else {
            str = Util.getCodecsOfType(format3.codecs, 1);
            i2 = -1;
            i3 = 0;
            str2 = null;
        }
        String str3 = str2;
        String str4 = str;
        int i4 = i2;
        int i5 = i3;
        return Format.createAudioSampleFormat(format3.id, MimeTypes.getMediaMimeType(str4), str4, i, -1, i4, -1, null, null, i5, str3);
    }
}
