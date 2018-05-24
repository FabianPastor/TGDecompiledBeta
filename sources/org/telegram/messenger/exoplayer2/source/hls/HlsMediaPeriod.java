package org.telegram.messenger.exoplayer2.source.hls;

import android.os.Handler;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import org.telegram.messenger.exoplayer2.C0605C;
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

    public HlsMediaPeriod(HlsExtractorFactory extractorFactory, HlsPlaylistTracker playlistTracker, HlsDataSourceFactory dataSourceFactory, int minLoadableRetryCount, EventDispatcher eventDispatcher, Allocator allocator, CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory, boolean allowChunklessPreparation) {
        this.extractorFactory = extractorFactory;
        this.playlistTracker = playlistTracker;
        this.dataSourceFactory = dataSourceFactory;
        this.minLoadableRetryCount = minLoadableRetryCount;
        this.eventDispatcher = eventDispatcher;
        this.allocator = allocator;
        this.compositeSequenceableLoaderFactory = compositeSequenceableLoaderFactory;
        this.allowChunklessPreparation = allowChunklessPreparation;
    }

    public void release() {
        this.playlistTracker.removeListener(this);
        this.continueLoadingHandler.removeCallbacksAndMessages(null);
        for (HlsSampleStreamWrapper sampleStreamWrapper : this.sampleStreamWrappers) {
            sampleStreamWrapper.release();
        }
    }

    public void prepare(MediaPeriod.Callback callback, long positionUs) {
        this.callback = callback;
        this.playlistTracker.addListener(this);
        buildAndPrepareSampleStreamWrappers(positionUs);
    }

    public void maybeThrowPrepareError() throws IOException {
        for (HlsSampleStreamWrapper sampleStreamWrapper : this.sampleStreamWrappers) {
            sampleStreamWrapper.maybeThrowPrepareError();
        }
    }

    public TrackGroupArray getTrackGroups() {
        return this.trackGroups;
    }

    public long selectTracks(TrackSelection[] selections, boolean[] mayRetainStreamFlags, SampleStream[] streams, boolean[] streamResetFlags, long positionUs) {
        int i;
        int[] streamChildIndices = new int[selections.length];
        int[] selectionChildIndices = new int[selections.length];
        for (i = 0; i < selections.length; i++) {
            int i2;
            int j;
            if (streams[i] == null) {
                i2 = -1;
            } else {
                i2 = ((Integer) this.streamWrapperIndices.get(streams[i])).intValue();
            }
            streamChildIndices[i] = i2;
            selectionChildIndices[i] = -1;
            if (selections[i] != null) {
                TrackGroup trackGroup = selections[i].getTrackGroup();
                for (j = 0; j < this.sampleStreamWrappers.length; j++) {
                    if (this.sampleStreamWrappers[j].getTrackGroups().indexOf(trackGroup) != -1) {
                        selectionChildIndices[i] = j;
                        break;
                    }
                }
            }
        }
        boolean forceReset = false;
        this.streamWrapperIndices.clear();
        SampleStream[] newStreams = new SampleStream[selections.length];
        SampleStream[] childStreams = new SampleStream[selections.length];
        TrackSelection[] childSelections = new TrackSelection[selections.length];
        int newEnabledSampleStreamWrapperCount = 0;
        HlsSampleStreamWrapper[] newEnabledSampleStreamWrappers = new HlsSampleStreamWrapper[this.sampleStreamWrappers.length];
        i = 0;
        while (i < this.sampleStreamWrappers.length) {
            j = 0;
            while (j < selections.length) {
                childStreams[j] = streamChildIndices[j] == i ? streams[j] : null;
                childSelections[j] = selectionChildIndices[j] == i ? selections[j] : null;
                j++;
            }
            HlsSampleStreamWrapper sampleStreamWrapper = this.sampleStreamWrappers[i];
            boolean wasReset = sampleStreamWrapper.selectTracks(childSelections, mayRetainStreamFlags, childStreams, streamResetFlags, positionUs, forceReset);
            boolean wrapperEnabled = false;
            for (j = 0; j < selections.length; j++) {
                if (selectionChildIndices[j] == i) {
                    Assertions.checkState(childStreams[j] != null);
                    newStreams[j] = childStreams[j];
                    wrapperEnabled = true;
                    this.streamWrapperIndices.put(childStreams[j], Integer.valueOf(i));
                } else if (streamChildIndices[j] == i) {
                    Assertions.checkState(childStreams[j] == null);
                }
            }
            if (wrapperEnabled) {
                newEnabledSampleStreamWrappers[newEnabledSampleStreamWrapperCount] = sampleStreamWrapper;
                int newEnabledSampleStreamWrapperCount2 = newEnabledSampleStreamWrapperCount + 1;
                if (newEnabledSampleStreamWrapperCount == 0) {
                    sampleStreamWrapper.setIsTimestampMaster(true);
                    if (wasReset || this.enabledSampleStreamWrappers.length == 0 || sampleStreamWrapper != this.enabledSampleStreamWrappers[0]) {
                        this.timestampAdjusterProvider.reset();
                        forceReset = true;
                        newEnabledSampleStreamWrapperCount = newEnabledSampleStreamWrapperCount2;
                    }
                } else {
                    sampleStreamWrapper.setIsTimestampMaster(false);
                }
                newEnabledSampleStreamWrapperCount = newEnabledSampleStreamWrapperCount2;
            }
            i++;
        }
        System.arraycopy(newStreams, 0, streams, 0, newStreams.length);
        this.enabledSampleStreamWrappers = (HlsSampleStreamWrapper[]) Arrays.copyOf(newEnabledSampleStreamWrappers, newEnabledSampleStreamWrapperCount);
        this.compositeSequenceableLoader = this.compositeSequenceableLoaderFactory.createCompositeSequenceableLoader(this.enabledSampleStreamWrappers);
        return positionUs;
    }

    public void discardBuffer(long positionUs, boolean toKeyframe) {
        for (HlsSampleStreamWrapper sampleStreamWrapper : this.enabledSampleStreamWrappers) {
            sampleStreamWrapper.discardBuffer(positionUs, toKeyframe);
        }
    }

    public void reevaluateBuffer(long positionUs) {
        this.compositeSequenceableLoader.reevaluateBuffer(positionUs);
    }

    public boolean continueLoading(long positionUs) {
        return this.compositeSequenceableLoader.continueLoading(positionUs);
    }

    public long getNextLoadPositionUs() {
        return this.compositeSequenceableLoader.getNextLoadPositionUs();
    }

    public long readDiscontinuity() {
        return C0605C.TIME_UNSET;
    }

    public long getBufferedPositionUs() {
        return this.compositeSequenceableLoader.getBufferedPositionUs();
    }

    public long seekToUs(long positionUs) {
        if (this.enabledSampleStreamWrappers.length > 0) {
            boolean forceReset = this.enabledSampleStreamWrappers[0].seekToUs(positionUs, false);
            for (int i = 1; i < this.enabledSampleStreamWrappers.length; i++) {
                this.enabledSampleStreamWrappers[i].seekToUs(positionUs, forceReset);
            }
            if (forceReset) {
                this.timestampAdjusterProvider.reset();
            }
        }
        return positionUs;
    }

    public long getAdjustedSeekPositionUs(long positionUs, SeekParameters seekParameters) {
        return positionUs;
    }

    public void onPrepared() {
        int i = 0;
        int i2 = this.pendingPrepareCount - 1;
        this.pendingPrepareCount = i2;
        if (i2 <= 0) {
            HlsSampleStreamWrapper sampleStreamWrapper;
            int totalTrackGroupCount = 0;
            for (HlsSampleStreamWrapper sampleStreamWrapper2 : this.sampleStreamWrappers) {
                totalTrackGroupCount += sampleStreamWrapper2.getTrackGroups().length;
            }
            TrackGroup[] trackGroupArray = new TrackGroup[totalTrackGroupCount];
            int trackGroupIndex = 0;
            HlsSampleStreamWrapper[] hlsSampleStreamWrapperArr = this.sampleStreamWrappers;
            int length = hlsSampleStreamWrapperArr.length;
            while (i < length) {
                sampleStreamWrapper2 = hlsSampleStreamWrapperArr[i];
                int wrapperTrackGroupCount = sampleStreamWrapper2.getTrackGroups().length;
                int j = 0;
                int trackGroupIndex2 = trackGroupIndex;
                while (j < wrapperTrackGroupCount) {
                    trackGroupIndex = trackGroupIndex2 + 1;
                    trackGroupArray[trackGroupIndex2] = sampleStreamWrapper2.getTrackGroups().get(j);
                    j++;
                    trackGroupIndex2 = trackGroupIndex;
                }
                i++;
                trackGroupIndex = trackGroupIndex2;
            }
            this.trackGroups = new TrackGroupArray(trackGroupArray);
            this.callback.onPrepared(this);
        }
    }

    public void onPlaylistRefreshRequired(HlsUrl url) {
        this.playlistTracker.refreshPlaylist(url);
    }

    public void onContinueLoadingRequested(HlsSampleStreamWrapper sampleStreamWrapper) {
        if (this.trackGroups != null) {
            this.callback.onContinueLoadingRequested(this);
        }
    }

    public void onPlaylistChanged() {
        continuePreparingOrLoading();
    }

    public void onPlaylistBlacklisted(HlsUrl url, long blacklistMs) {
        for (HlsSampleStreamWrapper streamWrapper : this.sampleStreamWrappers) {
            streamWrapper.onPlaylistBlacklisted(url, blacklistMs);
        }
        continuePreparingOrLoading();
    }

    private void buildAndPrepareSampleStreamWrappers(long positionUs) {
        HlsMasterPlaylist masterPlaylist = this.playlistTracker.getMasterPlaylist();
        List<HlsUrl> audioRenditions = masterPlaylist.audios;
        List<HlsUrl> subtitleRenditions = masterPlaylist.subtitles;
        int wrapperCount = (audioRenditions.size() + 1) + subtitleRenditions.size();
        this.sampleStreamWrappers = new HlsSampleStreamWrapper[wrapperCount];
        this.pendingPrepareCount = wrapperCount;
        buildAndPrepareMainSampleStreamWrapper(masterPlaylist, positionUs);
        int currentWrapperIndex = 1;
        int i = 0;
        while (i < audioRenditions.size()) {
            HlsSampleStreamWrapper sampleStreamWrapper = buildSampleStreamWrapper(1, new HlsUrl[]{(HlsUrl) audioRenditions.get(i)}, null, Collections.emptyList(), positionUs);
            int currentWrapperIndex2 = currentWrapperIndex + 1;
            this.sampleStreamWrappers[currentWrapperIndex] = sampleStreamWrapper;
            Format renditionFormat = audioRendition.format;
            if (!this.allowChunklessPreparation || renditionFormat.codecs == null) {
                sampleStreamWrapper.continuePreparing();
            } else {
                TrackGroup[] trackGroupArr = new TrackGroup[1];
                trackGroupArr[0] = new TrackGroup(audioRendition.format);
                sampleStreamWrapper.prepareWithMasterPlaylistInfo(new TrackGroupArray(trackGroupArr), 0);
            }
            i++;
            currentWrapperIndex = currentWrapperIndex2;
        }
        i = 0;
        while (i < subtitleRenditions.size()) {
            HlsUrl url = (HlsUrl) subtitleRenditions.get(i);
            sampleStreamWrapper = buildSampleStreamWrapper(3, new HlsUrl[]{url}, null, Collections.emptyList(), positionUs);
            currentWrapperIndex2 = currentWrapperIndex + 1;
            this.sampleStreamWrappers[currentWrapperIndex] = sampleStreamWrapper;
            trackGroupArr = new TrackGroup[1];
            trackGroupArr[0] = new TrackGroup(url.format);
            sampleStreamWrapper.prepareWithMasterPlaylistInfo(new TrackGroupArray(trackGroupArr), 0);
            i++;
            currentWrapperIndex = currentWrapperIndex2;
        }
        this.enabledSampleStreamWrappers = this.sampleStreamWrappers;
    }

    private void buildAndPrepareMainSampleStreamWrapper(org.telegram.messenger.exoplayer2.source.hls.playlist.HlsMasterPlaylist r25, long r26) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Unknown predecessor block by arg (r18_0 'selectedVariants' java.util.List<org.telegram.messenger.exoplayer2.source.hls.playlist.HlsMasterPlaylist$HlsUrl>) in PHI: PHI: (r18_2 'selectedVariants' java.util.List<org.telegram.messenger.exoplayer2.source.hls.playlist.HlsMasterPlaylist$HlsUrl>) = (r18_1 'selectedVariants' java.util.List<org.telegram.messenger.exoplayer2.source.hls.playlist.HlsMasterPlaylist$HlsUrl>), (r18_0 'selectedVariants' java.util.List<org.telegram.messenger.exoplayer2.source.hls.playlist.HlsMasterPlaylist$HlsUrl>), (r18_0 'selectedVariants' java.util.List<org.telegram.messenger.exoplayer2.source.hls.playlist.HlsMasterPlaylist$HlsUrl>) binds: {(r18_1 'selectedVariants' java.util.List<org.telegram.messenger.exoplayer2.source.hls.playlist.HlsMasterPlaylist$HlsUrl>)=B:14:0x0052, (r18_0 'selectedVariants' java.util.List<org.telegram.messenger.exoplayer2.source.hls.playlist.HlsMasterPlaylist$HlsUrl>)=B:34:0x00cc, (r18_0 'selectedVariants' java.util.List<org.telegram.messenger.exoplayer2.source.hls.playlist.HlsMasterPlaylist$HlsUrl>)=B:35:0x00ce}
	at jadx.core.dex.instructions.PhiInsn.replaceArg(PhiInsn.java:79)
	at jadx.core.dex.visitors.ModVisitor.processInvoke(ModVisitor.java:222)
	at jadx.core.dex.visitors.ModVisitor.replaceStep(ModVisitor.java:83)
	at jadx.core.dex.visitors.ModVisitor.visit(ModVisitor.java:68)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r24 = this;
        r18 = new java.util.ArrayList;
        r0 = r25;
        r3 = r0.variants;
        r0 = r18;
        r0.<init>(r3);
        r13 = new java.util.ArrayList;
        r13.<init>();
        r12 = new java.util.ArrayList;
        r12.<init>();
        r15 = 0;
    L_0x0016:
        r3 = r18.size();
        if (r15 >= r3) goto L_0x004c;
    L_0x001c:
        r0 = r18;
        r19 = r0.get(r15);
        r19 = (org.telegram.messenger.exoplayer2.source.hls.playlist.HlsMasterPlaylist.HlsUrl) r19;
        r0 = r19;
        r14 = r0.format;
        r3 = r14.height;
        if (r3 > 0) goto L_0x0035;
    L_0x002c:
        r3 = r14.codecs;
        r4 = 2;
        r3 = org.telegram.messenger.exoplayer2.util.Util.getCodecsOfType(r3, r4);
        if (r3 == 0) goto L_0x003d;
    L_0x0035:
        r0 = r19;
        r13.add(r0);
    L_0x003a:
        r15 = r15 + 1;
        goto L_0x0016;
    L_0x003d:
        r3 = r14.codecs;
        r4 = 1;
        r3 = org.telegram.messenger.exoplayer2.util.Util.getCodecsOfType(r3, r4);
        if (r3 == 0) goto L_0x003a;
    L_0x0046:
        r0 = r19;
        r12.add(r0);
        goto L_0x003a;
    L_0x004c:
        r3 = r13.isEmpty();
        if (r3 != 0) goto L_0x00c4;
    L_0x0052:
        r18 = r13;
    L_0x0054:
        r3 = r18.isEmpty();
        if (r3 != 0) goto L_0x00d4;
    L_0x005a:
        r3 = 1;
    L_0x005b:
        org.telegram.messenger.exoplayer2.util.Assertions.checkArgument(r3);
        r3 = 0;
        r3 = new org.telegram.messenger.exoplayer2.source.hls.playlist.HlsMasterPlaylist.HlsUrl[r3];
        r0 = r18;
        r5 = r0.toArray(r3);
        r5 = (org.telegram.messenger.exoplayer2.source.hls.playlist.HlsMasterPlaylist.HlsUrl[]) r5;
        r3 = 0;
        r3 = r5[r3];
        r3 = r3.format;
        r11 = r3.codecs;
        r4 = 0;
        r0 = r25;
        r6 = r0.muxedAudioFormat;
        r0 = r25;
        r7 = r0.muxedCaptionFormats;
        r3 = r24;
        r8 = r26;
        r17 = r3.buildSampleStreamWrapper(r4, r5, r6, r7, r8);
        r0 = r24;
        r3 = r0.sampleStreamWrappers;
        r4 = 0;
        r3[r4] = r17;
        r0 = r24;
        r3 = r0.allowChunklessPreparation;
        if (r3 == 0) goto L_0x019e;
    L_0x008e:
        if (r11 == 0) goto L_0x019e;
    L_0x0090:
        r3 = 2;
        r3 = org.telegram.messenger.exoplayer2.util.Util.getCodecsOfType(r11, r3);
        if (r3 == 0) goto L_0x00d6;
    L_0x0097:
        r22 = 1;
    L_0x0099:
        r3 = 1;
        r3 = org.telegram.messenger.exoplayer2.util.Util.getCodecsOfType(r11, r3);
        if (r3 == 0) goto L_0x00d9;
    L_0x00a0:
        r21 = 1;
    L_0x00a2:
        r16 = new java.util.ArrayList;
        r16.<init>();
        if (r22 == 0) goto L_0x013e;
    L_0x00a9:
        r3 = r18.size();
        r0 = new org.telegram.messenger.exoplayer2.Format[r3];
        r23 = r0;
        r15 = 0;
    L_0x00b2:
        r0 = r23;
        r3 = r0.length;
        if (r15 >= r3) goto L_0x00dc;
    L_0x00b7:
        r3 = r5[r15];
        r3 = r3.format;
        r3 = deriveVideoFormat(r3);
        r23[r15] = r3;
        r15 = r15 + 1;
        goto L_0x00b2;
    L_0x00c4:
        r3 = r12.size();
        r4 = r18.size();
        if (r3 >= r4) goto L_0x0054;
    L_0x00ce:
        r0 = r18;
        r0.removeAll(r12);
        goto L_0x0054;
    L_0x00d4:
        r3 = 0;
        goto L_0x005b;
    L_0x00d6:
        r22 = 0;
        goto L_0x0099;
    L_0x00d9:
        r21 = 0;
        goto L_0x00a2;
    L_0x00dc:
        r3 = new org.telegram.messenger.exoplayer2.source.TrackGroup;
        r0 = r23;
        r3.<init>(r0);
        r0 = r16;
        r0.add(r3);
        if (r21 == 0) goto L_0x0118;
    L_0x00ea:
        r0 = r25;
        r3 = r0.muxedAudioFormat;
        if (r3 != 0) goto L_0x00fa;
    L_0x00f0:
        r0 = r25;
        r3 = r0.audios;
        r3 = r3.isEmpty();
        if (r3 == 0) goto L_0x0118;
    L_0x00fa:
        r3 = new org.telegram.messenger.exoplayer2.source.TrackGroup;
        r4 = 1;
        r4 = new org.telegram.messenger.exoplayer2.Format[r4];
        r6 = 0;
        r7 = 0;
        r7 = r5[r7];
        r7 = r7.format;
        r0 = r25;
        r8 = r0.muxedAudioFormat;
        r9 = -1;
        r7 = deriveMuxedAudioFormat(r7, r8, r9);
        r4[r6] = r7;
        r3.<init>(r4);
        r0 = r16;
        r0.add(r3);
    L_0x0118:
        r0 = r25;
        r10 = r0.muxedCaptionFormats;
        if (r10 == 0) goto L_0x016d;
    L_0x011e:
        r15 = 0;
    L_0x011f:
        r3 = r10.size();
        if (r15 >= r3) goto L_0x016d;
    L_0x0125:
        r4 = new org.telegram.messenger.exoplayer2.source.TrackGroup;
        r3 = 1;
        r6 = new org.telegram.messenger.exoplayer2.Format[r3];
        r7 = 0;
        r3 = r10.get(r15);
        r3 = (org.telegram.messenger.exoplayer2.Format) r3;
        r6[r7] = r3;
        r4.<init>(r6);
        r0 = r16;
        r0.add(r4);
        r15 = r15 + 1;
        goto L_0x011f;
    L_0x013e:
        if (r21 == 0) goto L_0x0184;
    L_0x0140:
        r3 = r18.size();
        r2 = new org.telegram.messenger.exoplayer2.Format[r3];
        r15 = 0;
    L_0x0147:
        r3 = r2.length;
        if (r15 >= r3) goto L_0x0163;
    L_0x014a:
        r3 = r5[r15];
        r0 = r3.format;
        r20 = r0;
        r0 = r25;
        r3 = r0.muxedAudioFormat;
        r0 = r20;
        r4 = r0.bitrate;
        r0 = r20;
        r3 = deriveMuxedAudioFormat(r0, r3, r4);
        r2[r15] = r3;
        r15 = r15 + 1;
        goto L_0x0147;
    L_0x0163:
        r3 = new org.telegram.messenger.exoplayer2.source.TrackGroup;
        r3.<init>(r2);
        r0 = r16;
        r0.add(r3);
    L_0x016d:
        r4 = new org.telegram.messenger.exoplayer2.source.TrackGroupArray;
        r3 = 0;
        r3 = new org.telegram.messenger.exoplayer2.source.TrackGroup[r3];
        r0 = r16;
        r3 = r0.toArray(r3);
        r3 = (org.telegram.messenger.exoplayer2.source.TrackGroup[]) r3;
        r4.<init>(r3);
        r3 = 0;
        r0 = r17;
        r0.prepareWithMasterPlaylistInfo(r4, r3);
    L_0x0183:
        return;
    L_0x0184:
        r3 = new java.lang.IllegalArgumentException;
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r6 = "Unexpected codecs attribute: ";
        r4 = r4.append(r6);
        r4 = r4.append(r11);
        r4 = r4.toString();
        r3.<init>(r4);
        throw r3;
    L_0x019e:
        r3 = 1;
        r0 = r17;
        r0.setIsTimestampMaster(r3);
        r17.continuePreparing();
        goto L_0x0183;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.exoplayer2.source.hls.HlsMediaPeriod.buildAndPrepareMainSampleStreamWrapper(org.telegram.messenger.exoplayer2.source.hls.playlist.HlsMasterPlaylist, long):void");
    }

    private HlsSampleStreamWrapper buildSampleStreamWrapper(int trackType, HlsUrl[] variants, Format muxedAudioFormat, List<Format> muxedCaptionFormats, long positionUs) {
        return new HlsSampleStreamWrapper(trackType, this, new HlsChunkSource(this.extractorFactory, this.playlistTracker, variants, this.dataSourceFactory, this.timestampAdjusterProvider, muxedCaptionFormats), this.allocator, positionUs, muxedAudioFormat, this.minLoadableRetryCount, this.eventDispatcher);
    }

    private void continuePreparingOrLoading() {
        if (this.trackGroups != null) {
            this.callback.onContinueLoadingRequested(this);
            return;
        }
        for (HlsSampleStreamWrapper wrapper : this.sampleStreamWrappers) {
            wrapper.continuePreparing();
        }
    }

    private static Format deriveVideoFormat(Format variantFormat) {
        String codecs = Util.getCodecsOfType(variantFormat.codecs, 2);
        return Format.createVideoSampleFormat(variantFormat.id, MimeTypes.getMediaMimeType(codecs), codecs, variantFormat.bitrate, -1, variantFormat.width, variantFormat.height, variantFormat.frameRate, null, null);
    }

    private static Format deriveMuxedAudioFormat(Format variantFormat, Format mediaTagFormat, int bitrate) {
        String codecs;
        int channelCount = -1;
        int selectionFlags = 0;
        String language = null;
        if (mediaTagFormat != null) {
            codecs = mediaTagFormat.codecs;
            channelCount = mediaTagFormat.channelCount;
            selectionFlags = mediaTagFormat.selectionFlags;
            language = mediaTagFormat.language;
        } else {
            codecs = Util.getCodecsOfType(variantFormat.codecs, 1);
        }
        return Format.createAudioSampleFormat(variantFormat.id, MimeTypes.getMediaMimeType(codecs), codecs, bitrate, -1, channelCount, -1, null, null, selectionFlags, language);
    }
}
