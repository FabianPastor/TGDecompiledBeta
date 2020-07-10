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

    /* access modifiers changed from: protected */
    public void createSidx(Track track, SampleTableBox sampleTableBox) {
    }

    public MP4Builder createMovie(Mp4Movie mp4Movie, boolean z) throws Exception {
        this.currentMp4Movie = mp4Movie;
        FileOutputStream fileOutputStream = new FileOutputStream(mp4Movie.getCacheFile());
        this.fos = fileOutputStream;
        this.fc = fileOutputStream.getChannel();
        FileTypeBox createFileTypeBox = createFileTypeBox();
        createFileTypeBox.getBox(this.fc);
        long size = this.dataOffset + createFileTypeBox.getSize();
        this.dataOffset = size;
        this.wroteSinceLastMdat += size;
        this.splitMdat = z;
        this.mdat = new InterleaveChunkMdat();
        this.sizeBuffer = ByteBuffer.allocateDirect(4);
        return this;
    }

    private void flushCurrentMdat() throws Exception {
        long position = this.fc.position();
        this.fc.position(this.mdat.getOffset());
        this.mdat.getBox(this.fc);
        this.fc.position(position);
        this.mdat.setDataOffset(0);
        this.mdat.setContentSize(0);
        this.fos.flush();
        this.fos.getFD().sync();
    }

    public long writeSampleData(int i, ByteBuffer byteBuffer, MediaCodec.BufferInfo bufferInfo, boolean z) throws Exception {
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
        boolean z2 = true;
        if (j >= 32768) {
            if (this.splitMdat) {
                flushCurrentMdat();
                this.writeNewMdat = true;
            }
            this.wroteSinceLastMdat = 0;
        } else {
            z2 = false;
        }
        this.currentMp4Movie.addSample(i, this.dataOffset, bufferInfo);
        if (z) {
            this.sizeBuffer.position(0);
            this.sizeBuffer.putInt(bufferInfo.size - 4);
            this.sizeBuffer.position(0);
            this.fc.write(this.sizeBuffer);
            byteBuffer.position(bufferInfo.offset + 4);
        } else {
            byteBuffer.position(bufferInfo.offset);
        }
        byteBuffer.limit(bufferInfo.offset + bufferInfo.size);
        this.fc.write(byteBuffer);
        this.dataOffset += (long) bufferInfo.size;
        if (!z2) {
            return 0;
        }
        this.fos.flush();
        this.fos.getFD().sync();
        return this.fc.position();
    }

    public long getLastFrameTimestamp(int i) {
        return this.currentMp4Movie.getLastFrameTimestamp(i);
    }

    public int addTrack(MediaFormat mediaFormat, boolean z) {
        return this.currentMp4Movie.addTrack(mediaFormat, z);
    }

    public void finishMovie() throws Exception {
        if (this.mdat.getContentSize() != 0) {
            flushCurrentMdat();
        }
        Iterator<Track> it = this.currentMp4Movie.getTracks().iterator();
        while (it.hasNext()) {
            Track next = it.next();
            ArrayList<Sample> samples = next.getSamples();
            int size = samples.size();
            long[] jArr = new long[size];
            for (int i = 0; i < size; i++) {
                jArr[i] = samples.get(i).getSize();
            }
            this.track2SampleSizes.put(next, jArr);
        }
        createMovieBox(this.currentMp4Movie).getBox(this.fc);
        this.fos.flush();
        this.fos.getFD().sync();
        this.fc.close();
        this.fos.close();
    }

    /* access modifiers changed from: protected */
    public FileTypeBox createFileTypeBox() {
        LinkedList linkedList = new LinkedList();
        linkedList.add("isom");
        linkedList.add("iso2");
        linkedList.add("avc1");
        linkedList.add("mp41");
        return new FileTypeBox("isom", 512, linkedList);
    }

    private static class InterleaveChunkMdat implements Box {
        private long contentSize;
        private long dataOffset;
        private Container parent;

        private boolean isSmallBox(long j) {
            return j + 8 < 4294967296L;
        }

        public String getType() {
            return "mdat";
        }

        public void parse(DataSource dataSource, ByteBuffer byteBuffer, long j, BoxParser boxParser) {
        }

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

        public void setDataOffset(long j) {
            this.dataOffset = j;
        }

        public void setParent(Container container) {
            this.parent = container;
        }

        public void setContentSize(long j) {
            this.contentSize = j;
        }

        public long getContentSize() {
            return this.contentSize;
        }

        public long getSize() {
            return this.contentSize + 16;
        }

        public void getBox(WritableByteChannel writableByteChannel) throws IOException {
            ByteBuffer allocate = ByteBuffer.allocate(16);
            long size = getSize();
            if (isSmallBox(size)) {
                IsoTypeWriter.writeUInt32(allocate, size);
            } else {
                IsoTypeWriter.writeUInt32(allocate, 1);
            }
            allocate.put(IsoFile.fourCCtoBytes("mdat"));
            if (isSmallBox(size)) {
                allocate.put(new byte[8]);
            } else {
                IsoTypeWriter.writeUInt64(allocate, size);
            }
            allocate.rewind();
            writableByteChannel.write(allocate);
        }
    }

    public static long gcd(long j, long j2) {
        return j2 == 0 ? j : gcd(j2, j % j2);
    }

    public long getTimescale(Mp4Movie mp4Movie) {
        long timeScale = !mp4Movie.getTracks().isEmpty() ? (long) mp4Movie.getTracks().iterator().next().getTimeScale() : 0;
        Iterator<Track> it = mp4Movie.getTracks().iterator();
        while (it.hasNext()) {
            timeScale = gcd((long) it.next().getTimeScale(), timeScale);
        }
        return timeScale;
    }

    /* access modifiers changed from: protected */
    public MovieBox createMovieBox(Mp4Movie mp4Movie) {
        MovieBox movieBox = new MovieBox();
        MovieHeaderBox movieHeaderBox = new MovieHeaderBox();
        movieHeaderBox.setCreationTime(new Date());
        movieHeaderBox.setModificationTime(new Date());
        movieHeaderBox.setMatrix(Matrix.ROTATE_0);
        long timescale = getTimescale(mp4Movie);
        Iterator<Track> it = mp4Movie.getTracks().iterator();
        long j = 0;
        while (it.hasNext()) {
            Track next = it.next();
            next.prepare();
            long duration = (next.getDuration() * timescale) / ((long) next.getTimeScale());
            if (duration > j) {
                j = duration;
            }
        }
        movieHeaderBox.setDuration(j);
        movieHeaderBox.setTimescale(timescale);
        movieHeaderBox.setNextTrackId((long) (mp4Movie.getTracks().size() + 1));
        movieBox.addBox(movieHeaderBox);
        Iterator<Track> it2 = mp4Movie.getTracks().iterator();
        while (it2.hasNext()) {
            movieBox.addBox(createTrackBox(it2.next(), mp4Movie));
        }
        return movieBox;
    }

    /* access modifiers changed from: protected */
    public TrackBox createTrackBox(Track track, Mp4Movie mp4Movie) {
        TrackBox trackBox = new TrackBox();
        TrackHeaderBox trackHeaderBox = new TrackHeaderBox();
        trackHeaderBox.setEnabled(true);
        trackHeaderBox.setInMovie(true);
        trackHeaderBox.setInPreview(true);
        if (track.isAudio()) {
            trackHeaderBox.setMatrix(Matrix.ROTATE_0);
        } else {
            trackHeaderBox.setMatrix(mp4Movie.getMatrix());
        }
        trackHeaderBox.setAlternateGroup(0);
        trackHeaderBox.setCreationTime(track.getCreationTime());
        trackHeaderBox.setDuration((track.getDuration() * getTimescale(mp4Movie)) / ((long) track.getTimeScale()));
        trackHeaderBox.setHeight((double) track.getHeight());
        trackHeaderBox.setWidth((double) track.getWidth());
        trackHeaderBox.setLayer(0);
        trackHeaderBox.setModificationTime(new Date());
        trackHeaderBox.setTrackId(track.getTrackId() + 1);
        trackHeaderBox.setVolume(track.getVolume());
        trackBox.addBox(trackHeaderBox);
        MediaBox mediaBox = new MediaBox();
        trackBox.addBox(mediaBox);
        MediaHeaderBox mediaHeaderBox = new MediaHeaderBox();
        mediaHeaderBox.setCreationTime(track.getCreationTime());
        mediaHeaderBox.setDuration(track.getDuration());
        mediaHeaderBox.setTimescale((long) track.getTimeScale());
        mediaHeaderBox.setLanguage("eng");
        mediaBox.addBox(mediaHeaderBox);
        HandlerBox handlerBox = new HandlerBox();
        handlerBox.setName(track.isAudio() ? "SoundHandle" : "VideoHandle");
        handlerBox.setHandlerType(track.getHandler());
        mediaBox.addBox(handlerBox);
        MediaInformationBox mediaInformationBox = new MediaInformationBox();
        mediaInformationBox.addBox(track.getMediaHeaderBox());
        DataInformationBox dataInformationBox = new DataInformationBox();
        DataReferenceBox dataReferenceBox = new DataReferenceBox();
        dataInformationBox.addBox(dataReferenceBox);
        DataEntryUrlBox dataEntryUrlBox = new DataEntryUrlBox();
        dataEntryUrlBox.setFlags(1);
        dataReferenceBox.addBox(dataEntryUrlBox);
        mediaInformationBox.addBox(dataInformationBox);
        mediaInformationBox.addBox(createStbl(track));
        mediaBox.addBox(mediaInformationBox);
        return trackBox;
    }

    /* access modifiers changed from: protected */
    public Box createStbl(Track track) {
        SampleTableBox sampleTableBox = new SampleTableBox();
        createStsd(track, sampleTableBox);
        createStts(track, sampleTableBox);
        createCtts(track, sampleTableBox);
        createStss(track, sampleTableBox);
        createStsc(track, sampleTableBox);
        createStsz(track, sampleTableBox);
        createStco(track, sampleTableBox);
        return sampleTableBox;
    }

    /* access modifiers changed from: protected */
    public void createStsd(Track track, SampleTableBox sampleTableBox) {
        sampleTableBox.addBox(track.getSampleDescriptionBox());
    }

    /* access modifiers changed from: protected */
    public void createCtts(Track track, SampleTableBox sampleTableBox) {
        int[] sampleCompositions = track.getSampleCompositions();
        if (sampleCompositions != null) {
            CompositionTimeToSample.Entry entry = null;
            ArrayList arrayList = new ArrayList();
            for (int i : sampleCompositions) {
                if (entry == null || entry.getOffset() != i) {
                    entry = new CompositionTimeToSample.Entry(1, i);
                    arrayList.add(entry);
                } else {
                    entry.setCount(entry.getCount() + 1);
                }
            }
            CompositionTimeToSample compositionTimeToSample = new CompositionTimeToSample();
            compositionTimeToSample.setEntries(arrayList);
            sampleTableBox.addBox(compositionTimeToSample);
        }
    }

    /* access modifiers changed from: protected */
    public void createStts(Track track, SampleTableBox sampleTableBox) {
        ArrayList arrayList = new ArrayList();
        long[] sampleDurations = track.getSampleDurations();
        TimeToSampleBox.Entry entry = null;
        for (long j : sampleDurations) {
            if (entry == null || entry.getDelta() != j) {
                entry = new TimeToSampleBox.Entry(1, j);
                arrayList.add(entry);
            } else {
                entry.setCount(entry.getCount() + 1);
            }
        }
        TimeToSampleBox timeToSampleBox = new TimeToSampleBox();
        timeToSampleBox.setEntries(arrayList);
        sampleTableBox.addBox(timeToSampleBox);
    }

    /* access modifiers changed from: protected */
    public void createStss(Track track, SampleTableBox sampleTableBox) {
        long[] syncSamples = track.getSyncSamples();
        if (syncSamples != null && syncSamples.length > 0) {
            SyncSampleBox syncSampleBox = new SyncSampleBox();
            syncSampleBox.setSampleNumber(syncSamples);
            sampleTableBox.addBox(syncSampleBox);
        }
    }

    /* access modifiers changed from: protected */
    public void createStsc(Track track, SampleTableBox sampleTableBox) {
        SampleToChunkBox sampleToChunkBox = new SampleToChunkBox();
        sampleToChunkBox.setEntries(new LinkedList());
        int size = track.getSamples().size();
        int i = -1;
        int i2 = 0;
        int i3 = 0;
        int i4 = 1;
        while (i2 < size) {
            Sample sample = track.getSamples().get(i2);
            i3++;
            if (i2 == size + -1 || sample.getOffset() + sample.getSize() != track.getSamples().get(i2 + 1).getOffset()) {
                if (i != i3) {
                    sampleToChunkBox.getEntries().add(new SampleToChunkBox.Entry((long) i4, (long) i3, 1));
                    i = i3;
                }
                i4++;
                i3 = 0;
            }
            i2++;
        }
        sampleTableBox.addBox(sampleToChunkBox);
    }

    /* access modifiers changed from: protected */
    public void createStsz(Track track, SampleTableBox sampleTableBox) {
        SampleSizeBox sampleSizeBox = new SampleSizeBox();
        sampleSizeBox.setSampleSizes(this.track2SampleSizes.get(track));
        sampleTableBox.addBox(sampleSizeBox);
    }

    /* access modifiers changed from: protected */
    public void createStco(Track track, SampleTableBox sampleTableBox) {
        ArrayList arrayList = new ArrayList();
        Iterator<Sample> it = track.getSamples().iterator();
        long j = -1;
        while (it.hasNext()) {
            Sample next = it.next();
            long offset = next.getOffset();
            if (!(j == -1 || j == offset)) {
                j = -1;
            }
            if (j == -1) {
                arrayList.add(Long.valueOf(offset));
            }
            j = next.getSize() + offset;
        }
        long[] jArr = new long[arrayList.size()];
        for (int i = 0; i < arrayList.size(); i++) {
            jArr[i] = ((Long) arrayList.get(i)).longValue();
        }
        StaticChunkOffsetBox staticChunkOffsetBox = new StaticChunkOffsetBox();
        staticChunkOffsetBox.setChunkOffsets(jArr);
        sampleTableBox.addBox(staticChunkOffsetBox);
    }
}
