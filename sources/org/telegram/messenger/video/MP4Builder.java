package org.telegram.messenger.video;

import android.media.MediaCodec;
import android.media.MediaFormat;
import com.coremedia.iso.BoxParser;
import com.coremedia.iso.IsoFile;
import com.coremedia.iso.IsoTypeWriter;
import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.CompositionTimeToSample;
import com.coremedia.iso.boxes.Container;
import com.coremedia.iso.boxes.DataEntryUrlBox;
import com.coremedia.iso.boxes.DataInformationBox;
import com.coremedia.iso.boxes.DataReferenceBox;
import com.coremedia.iso.boxes.FileTypeBox;
import com.coremedia.iso.boxes.HandlerBox;
import com.coremedia.iso.boxes.MediaBox;
import com.coremedia.iso.boxes.MediaHeaderBox;
import com.coremedia.iso.boxes.MediaInformationBox;
import com.coremedia.iso.boxes.MovieBox;
import com.coremedia.iso.boxes.MovieHeaderBox;
import com.coremedia.iso.boxes.SampleSizeBox;
import com.coremedia.iso.boxes.SampleTableBox;
import com.coremedia.iso.boxes.SampleToChunkBox;
import com.coremedia.iso.boxes.StaticChunkOffsetBox;
import com.coremedia.iso.boxes.SyncSampleBox;
import com.coremedia.iso.boxes.TimeToSampleBox;
import com.coremedia.iso.boxes.TrackBox;
import com.coremedia.iso.boxes.TrackHeaderBox;
import com.googlecode.mp4parser.DataSource;
import com.googlecode.mp4parser.util.Matrix;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class MP4Builder {
    private Mp4Movie currentMp4Movie = null;
    private long dataOffset = 0;
    private FileChannel fc = null;
    private FileOutputStream fos = null;
    private InterleaveChunkMdat mdat = null;
    private ByteBuffer sizeBuffer = null;
    private boolean splitMdat;
    private HashMap<Track, long[]> track2SampleSizes = new HashMap<>();
    private boolean wasFirstVideoFrame;
    private boolean writeNewMdat = true;
    private long wroteSinceLastMdat = 0;

    public MP4Builder createMovie(Mp4Movie mp4Movie, boolean split) throws Exception {
        this.currentMp4Movie = mp4Movie;
        FileOutputStream fileOutputStream = new FileOutputStream(mp4Movie.getCacheFile());
        this.fos = fileOutputStream;
        this.fc = fileOutputStream.getChannel();
        FileTypeBox fileTypeBox = createFileTypeBox();
        fileTypeBox.getBox(this.fc);
        long size = this.dataOffset + fileTypeBox.getSize();
        this.dataOffset = size;
        this.wroteSinceLastMdat += size;
        this.splitMdat = split;
        this.mdat = new InterleaveChunkMdat();
        this.sizeBuffer = ByteBuffer.allocateDirect(4);
        return this;
    }

    private void flushCurrentMdat() throws Exception {
        long oldPosition = this.fc.position();
        this.fc.position(this.mdat.getOffset());
        this.mdat.getBox(this.fc);
        this.fc.position(oldPosition);
        this.mdat.setDataOffset(0);
        this.mdat.setContentSize(0);
        this.fos.flush();
        this.fos.getFD().sync();
    }

    public long writeSampleData(int trackIndex, ByteBuffer byteBuf, MediaCodec.BufferInfo bufferInfo, boolean writeLength) throws Exception {
        if (this.writeNewMdat) {
            this.mdat.setContentSize(0);
            this.mdat.getBox(this.fc);
            this.mdat.setDataOffset(this.dataOffset);
            this.dataOffset += 16;
            this.wroteSinceLastMdat += 16;
            this.writeNewMdat = false;
        }
        InterleaveChunkMdat interleaveChunkMdat = this.mdat;
        interleaveChunkMdat.setContentSize(interleaveChunkMdat.getContentSize() + ((long) bufferInfo.size));
        long j = this.wroteSinceLastMdat + ((long) bufferInfo.size);
        this.wroteSinceLastMdat = j;
        boolean flush = false;
        if (j >= 32768) {
            if (this.splitMdat) {
                flushCurrentMdat();
                this.writeNewMdat = true;
            }
            flush = true;
            this.wroteSinceLastMdat = 0;
        }
        this.currentMp4Movie.addSample(trackIndex, this.dataOffset, bufferInfo);
        if (writeLength) {
            this.sizeBuffer.position(0);
            this.sizeBuffer.putInt(bufferInfo.size - 4);
            this.sizeBuffer.position(0);
            this.fc.write(this.sizeBuffer);
            byteBuf.position(bufferInfo.offset + 4);
        } else {
            byteBuf.position(bufferInfo.offset);
        }
        byteBuf.limit(bufferInfo.offset + bufferInfo.size);
        this.fc.write(byteBuf);
        this.dataOffset += (long) bufferInfo.size;
        if (!flush) {
            return 0;
        }
        this.fos.flush();
        this.fos.getFD().sync();
        return this.fc.position();
    }

    public long getLastFrameTimestamp(int trackIndex) {
        return this.currentMp4Movie.getLastFrameTimestamp(trackIndex);
    }

    public int addTrack(MediaFormat mediaFormat, boolean isAudio) {
        return this.currentMp4Movie.addTrack(mediaFormat, isAudio);
    }

    public void finishMovie() throws Exception {
        if (this.mdat.getContentSize() != 0) {
            flushCurrentMdat();
        }
        Iterator<Track> it = this.currentMp4Movie.getTracks().iterator();
        while (it.hasNext()) {
            Track track = it.next();
            List<Sample> samples = track.getSamples();
            long[] sizes = new long[samples.size()];
            for (int i = 0; i < sizes.length; i++) {
                sizes[i] = samples.get(i).getSize();
            }
            this.track2SampleSizes.put(track, sizes);
        }
        createMovieBox(this.currentMp4Movie).getBox(this.fc);
        this.fos.flush();
        this.fos.getFD().sync();
        this.fc.close();
        this.fos.close();
    }

    /* access modifiers changed from: protected */
    public FileTypeBox createFileTypeBox() {
        LinkedList<String> minorBrands = new LinkedList<>();
        minorBrands.add("isom");
        minorBrands.add("iso2");
        minorBrands.add("avc1");
        minorBrands.add("mp41");
        return new FileTypeBox("isom", 512, minorBrands);
    }

    private static class InterleaveChunkMdat implements Box {
        private long contentSize;
        private long dataOffset;
        private Container parent;

        private InterleaveChunkMdat() {
            this.contentSize = NUM;
            this.dataOffset = 0;
        }

        public Container getParent() {
            return this.parent;
        }

        public long getOffset() {
            return this.dataOffset;
        }

        public void setDataOffset(long offset) {
            this.dataOffset = offset;
        }

        public void setParent(Container parent2) {
            this.parent = parent2;
        }

        public void setContentSize(long contentSize2) {
            this.contentSize = contentSize2;
        }

        public long getContentSize() {
            return this.contentSize;
        }

        public String getType() {
            return "mdat";
        }

        public long getSize() {
            return this.contentSize + 16;
        }

        private boolean isSmallBox(long contentSize2) {
            return 8 + contentSize2 < 4294967296L;
        }

        public void parse(DataSource dataSource, ByteBuffer header, long contentSize2, BoxParser boxParser) {
        }

        public void getBox(WritableByteChannel writableByteChannel) throws IOException {
            ByteBuffer bb = ByteBuffer.allocate(16);
            long size = getSize();
            if (isSmallBox(size)) {
                IsoTypeWriter.writeUInt32(bb, size);
            } else {
                IsoTypeWriter.writeUInt32(bb, 1);
            }
            bb.put(IsoFile.fourCCtoBytes("mdat"));
            if (isSmallBox(size)) {
                bb.put(new byte[8]);
            } else {
                IsoTypeWriter.writeUInt64(bb, size);
            }
            bb.rewind();
            writableByteChannel.write(bb);
        }
    }

    public static long gcd(long a, long b) {
        if (b == 0) {
            return a;
        }
        return gcd(b, a % b);
    }

    public long getTimescale(Mp4Movie mp4Movie) {
        long timescale = 0;
        if (!mp4Movie.getTracks().isEmpty()) {
            timescale = (long) mp4Movie.getTracks().iterator().next().getTimeScale();
        }
        Iterator<Track> it = mp4Movie.getTracks().iterator();
        while (it.hasNext()) {
            timescale = gcd((long) it.next().getTimeScale(), timescale);
        }
        return timescale;
    }

    /* access modifiers changed from: protected */
    public MovieBox createMovieBox(Mp4Movie movie) {
        MovieBox movieBox = new MovieBox();
        MovieHeaderBox mvhd = new MovieHeaderBox();
        mvhd.setCreationTime(new Date());
        mvhd.setModificationTime(new Date());
        mvhd.setMatrix(Matrix.ROTATE_0);
        long movieTimeScale = getTimescale(movie);
        long duration = 0;
        Iterator<Track> it = movie.getTracks().iterator();
        while (it.hasNext()) {
            Track track = it.next();
            track.prepare();
            long tracksDuration = (track.getDuration() * movieTimeScale) / ((long) track.getTimeScale());
            if (tracksDuration > duration) {
                duration = tracksDuration;
            }
        }
        mvhd.setDuration(duration);
        mvhd.setTimescale(movieTimeScale);
        mvhd.setNextTrackId((long) (movie.getTracks().size() + 1));
        movieBox.addBox(mvhd);
        Iterator<Track> it2 = movie.getTracks().iterator();
        while (it2.hasNext()) {
            movieBox.addBox(createTrackBox(it2.next(), movie));
        }
        return movieBox;
    }

    /* access modifiers changed from: protected */
    public TrackBox createTrackBox(Track track, Mp4Movie movie) {
        TrackBox trackBox = new TrackBox();
        TrackHeaderBox tkhd = new TrackHeaderBox();
        tkhd.setEnabled(true);
        tkhd.setInMovie(true);
        tkhd.setInPreview(true);
        if (track.isAudio()) {
            tkhd.setMatrix(Matrix.ROTATE_0);
        } else {
            tkhd.setMatrix(movie.getMatrix());
        }
        tkhd.setAlternateGroup(0);
        tkhd.setCreationTime(track.getCreationTime());
        tkhd.setDuration((track.getDuration() * getTimescale(movie)) / ((long) track.getTimeScale()));
        tkhd.setHeight((double) track.getHeight());
        tkhd.setWidth((double) track.getWidth());
        tkhd.setLayer(0);
        tkhd.setModificationTime(new Date());
        tkhd.setTrackId(track.getTrackId() + 1);
        tkhd.setVolume(track.getVolume());
        trackBox.addBox(tkhd);
        MediaBox mdia = new MediaBox();
        trackBox.addBox(mdia);
        MediaHeaderBox mdhd = new MediaHeaderBox();
        mdhd.setCreationTime(track.getCreationTime());
        mdhd.setDuration(track.getDuration());
        mdhd.setTimescale((long) track.getTimeScale());
        mdhd.setLanguage("eng");
        mdia.addBox(mdhd);
        HandlerBox hdlr = new HandlerBox();
        hdlr.setName(track.isAudio() ? "SoundHandle" : "VideoHandle");
        hdlr.setHandlerType(track.getHandler());
        mdia.addBox(hdlr);
        MediaInformationBox minf = new MediaInformationBox();
        minf.addBox(track.getMediaHeaderBox());
        DataInformationBox dinf = new DataInformationBox();
        DataReferenceBox dref = new DataReferenceBox();
        dinf.addBox(dref);
        DataEntryUrlBox url = new DataEntryUrlBox();
        url.setFlags(1);
        dref.addBox(url);
        minf.addBox(dinf);
        minf.addBox(createStbl(track));
        mdia.addBox(minf);
        return trackBox;
    }

    /* access modifiers changed from: protected */
    public Box createStbl(Track track) {
        SampleTableBox stbl = new SampleTableBox();
        createStsd(track, stbl);
        createStts(track, stbl);
        createCtts(track, stbl);
        createStss(track, stbl);
        createStsc(track, stbl);
        createStsz(track, stbl);
        createStco(track, stbl);
        return stbl;
    }

    /* access modifiers changed from: protected */
    public void createStsd(Track track, SampleTableBox stbl) {
        stbl.addBox(track.getSampleDescriptionBox());
    }

    /* access modifiers changed from: protected */
    public void createCtts(Track track, SampleTableBox stbl) {
        int[] sampleCompositions = track.getSampleCompositions();
        if (sampleCompositions != null) {
            CompositionTimeToSample.Entry lastEntry = null;
            List<CompositionTimeToSample.Entry> entries = new ArrayList<>();
            for (int offset : sampleCompositions) {
                if (lastEntry == null || lastEntry.getOffset() != offset) {
                    lastEntry = new CompositionTimeToSample.Entry(1, offset);
                    entries.add(lastEntry);
                } else {
                    lastEntry.setCount(lastEntry.getCount() + 1);
                }
            }
            CompositionTimeToSample ctts = new CompositionTimeToSample();
            ctts.setEntries(entries);
            stbl.addBox(ctts);
        }
    }

    /* access modifiers changed from: protected */
    public void createStts(Track track, SampleTableBox stbl) {
        TimeToSampleBox.Entry lastEntry = null;
        List<TimeToSampleBox.Entry> entries = new ArrayList<>();
        long[] deltas = track.getSampleDurations();
        for (long delta : deltas) {
            if (lastEntry == null || lastEntry.getDelta() != delta) {
                lastEntry = new TimeToSampleBox.Entry(1, delta);
                entries.add(lastEntry);
            } else {
                lastEntry.setCount(lastEntry.getCount() + 1);
            }
        }
        TimeToSampleBox stts = new TimeToSampleBox();
        stts.setEntries(entries);
        stbl.addBox(stts);
    }

    /* access modifiers changed from: protected */
    public void createStss(Track track, SampleTableBox stbl) {
        long[] syncSamples = track.getSyncSamples();
        if (syncSamples != null && syncSamples.length > 0) {
            SyncSampleBox stss = new SyncSampleBox();
            stss.setSampleNumber(syncSamples);
            stbl.addBox(stss);
        }
    }

    /* access modifiers changed from: protected */
    public void createStsc(Track track, SampleTableBox stbl) {
        int samplesCount;
        SampleToChunkBox stsc = new SampleToChunkBox();
        stsc.setEntries(new LinkedList());
        int lastChunkNumber = 1;
        int lastSampleCount = 0;
        int previousWritedChunkCount = -1;
        int samplesCount2 = track.getSamples().size();
        int a = 0;
        while (a < samplesCount2) {
            Sample sample = track.getSamples().get(a);
            long offset = sample.getOffset();
            long lastOffset = offset + sample.getSize();
            lastSampleCount++;
            boolean write = false;
            if (a == samplesCount2 - 1) {
                write = true;
            } else if (lastOffset != track.getSamples().get(a + 1).getOffset()) {
                write = true;
            }
            if (write) {
                if (previousWritedChunkCount != lastSampleCount) {
                    List<SampleToChunkBox.Entry> entries = stsc.getEntries();
                    int i = previousWritedChunkCount;
                    samplesCount = samplesCount2;
                    Sample sample2 = sample;
                    long j = offset;
                    SampleToChunkBox.Entry entry = r15;
                    SampleToChunkBox.Entry entry2 = new SampleToChunkBox.Entry((long) lastChunkNumber, (long) lastSampleCount, 1);
                    entries.add(entry);
                    previousWritedChunkCount = lastSampleCount;
                } else {
                    int i2 = previousWritedChunkCount;
                    samplesCount = samplesCount2;
                    Sample sample3 = sample;
                    long j2 = offset;
                }
                lastSampleCount = 0;
                lastChunkNumber++;
            } else {
                int i3 = previousWritedChunkCount;
                samplesCount = samplesCount2;
                Sample sample4 = sample;
                long j3 = offset;
            }
            a++;
            samplesCount2 = samplesCount;
        }
        int i4 = previousWritedChunkCount;
        stbl.addBox(stsc);
    }

    /* access modifiers changed from: protected */
    public void createStsz(Track track, SampleTableBox stbl) {
        SampleSizeBox stsz = new SampleSizeBox();
        stsz.setSampleSizes(this.track2SampleSizes.get(track));
        stbl.addBox(stsz);
    }

    /* access modifiers changed from: protected */
    public void createSidx(Track track, SampleTableBox stbl) {
    }

    /* access modifiers changed from: protected */
    public void createStco(Track track, SampleTableBox stbl) {
        ArrayList<Long> chunksOffsets = new ArrayList<>();
        long lastOffset = -1;
        Iterator<Sample> it = track.getSamples().iterator();
        while (it.hasNext()) {
            Sample sample = it.next();
            long offset = sample.getOffset();
            if (!(lastOffset == -1 || lastOffset == offset)) {
                lastOffset = -1;
            }
            if (lastOffset == -1) {
                chunksOffsets.add(Long.valueOf(offset));
            }
            lastOffset = offset + sample.getSize();
        }
        long[] chunkOffsetsLong = new long[chunksOffsets.size()];
        for (int a = 0; a < chunksOffsets.size(); a++) {
            chunkOffsetsLong[a] = chunksOffsets.get(a).longValue();
        }
        StaticChunkOffsetBox stco = new StaticChunkOffsetBox();
        stco.setChunkOffsets(chunkOffsetsLong);
        stbl.addBox(stco);
    }
}
