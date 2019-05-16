package org.telegram.messenger.video;

import android.media.MediaCodec.BufferInfo;
import android.media.MediaFormat;
import com.coremedia.iso.boxes.AbstractMediaHeaderBox;
import com.coremedia.iso.boxes.SampleDescriptionBox;
import com.coremedia.iso.boxes.SoundMediaHeaderBox;
import com.coremedia.iso.boxes.VideoMediaHeaderBox;
import com.coremedia.iso.boxes.sampleentry.AudioSampleEntry;
import com.coremedia.iso.boxes.sampleentry.VisualSampleEntry;
import com.googlecode.mp4parser.boxes.mp4.ESDescriptorBox;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.AudioSpecificConfig;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.DecoderConfigDescriptor;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.ESDescriptor;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.SLConfigDescriptor;
import com.mp4parser.iso14496.part15.AvcConfigurationBox;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class Track {
    private static Map<Integer, Integer> samplingFrequencyIndexMap = new HashMap();
    private Date creationTime = new Date();
    private long duration = 0;
    private boolean first = true;
    private String handler;
    private AbstractMediaHeaderBox headerBox;
    private int height;
    private boolean isAudio;
    private int[] sampleCompositions;
    private SampleDescriptionBox sampleDescriptionBox;
    private long[] sampleDurations;
    private ArrayList<SamplePresentationTime> samplePresentationTimes = new ArrayList();
    private ArrayList<Sample> samples = new ArrayList();
    private LinkedList<Integer> syncSamples = null;
    private int timeScale;
    private long trackId;
    private float volume = 0.0f;
    private int width;

    private class SamplePresentationTime {
        private long dt;
        private int index;
        private long presentationTime;

        public SamplePresentationTime(int i, long j) {
            this.index = i;
            this.presentationTime = j;
        }
    }

    static {
        samplingFrequencyIndexMap.put(Integer.valueOf(96000), Integer.valueOf(0));
        samplingFrequencyIndexMap.put(Integer.valueOf(88200), Integer.valueOf(1));
        samplingFrequencyIndexMap.put(Integer.valueOf(64000), Integer.valueOf(2));
        samplingFrequencyIndexMap.put(Integer.valueOf(48000), Integer.valueOf(3));
        samplingFrequencyIndexMap.put(Integer.valueOf(44100), Integer.valueOf(4));
        samplingFrequencyIndexMap.put(Integer.valueOf(32000), Integer.valueOf(5));
        samplingFrequencyIndexMap.put(Integer.valueOf(24000), Integer.valueOf(6));
        samplingFrequencyIndexMap.put(Integer.valueOf(22050), Integer.valueOf(7));
        samplingFrequencyIndexMap.put(Integer.valueOf(16000), Integer.valueOf(8));
        samplingFrequencyIndexMap.put(Integer.valueOf(12000), Integer.valueOf(9));
        samplingFrequencyIndexMap.put(Integer.valueOf(11025), Integer.valueOf(10));
        samplingFrequencyIndexMap.put(Integer.valueOf(8000), Integer.valueOf(11));
    }

    public Track(int i, MediaFormat mediaFormat, boolean z) {
        this.trackId = (long) i;
        this.isAudio = z;
        String str;
        if (this.isAudio) {
            this.volume = 1.0f;
            str = "sample-rate";
            this.timeScale = mediaFormat.getInteger(str);
            this.handler = "soun";
            this.headerBox = new SoundMediaHeaderBox();
            this.sampleDescriptionBox = new SampleDescriptionBox();
            AudioSampleEntry audioSampleEntry = new AudioSampleEntry("mp4a");
            audioSampleEntry.setChannelCount(mediaFormat.getInteger("channel-count"));
            audioSampleEntry.setSampleRate((long) mediaFormat.getInteger(str));
            audioSampleEntry.setDataReferenceIndex(1);
            audioSampleEntry.setSampleSize(16);
            ESDescriptorBox eSDescriptorBox = new ESDescriptorBox();
            ESDescriptor eSDescriptor = new ESDescriptor();
            eSDescriptor.setEsId(0);
            SLConfigDescriptor sLConfigDescriptor = new SLConfigDescriptor();
            sLConfigDescriptor.setPredefined(2);
            eSDescriptor.setSlConfigDescriptor(sLConfigDescriptor);
            DecoderConfigDescriptor decoderConfigDescriptor = new DecoderConfigDescriptor();
            decoderConfigDescriptor.setObjectTypeIndication(64);
            decoderConfigDescriptor.setStreamType(5);
            decoderConfigDescriptor.setBufferSizeDB(1536);
            String str2 = "max-bitrate";
            if (mediaFormat.containsKey(str2)) {
                decoderConfigDescriptor.setMaxBitRate((long) mediaFormat.getInteger(str2));
            } else {
                decoderConfigDescriptor.setMaxBitRate(96000);
            }
            decoderConfigDescriptor.setAvgBitRate((long) this.timeScale);
            AudioSpecificConfig audioSpecificConfig = new AudioSpecificConfig();
            audioSpecificConfig.setAudioObjectType(2);
            audioSpecificConfig.setSamplingFrequencyIndex(((Integer) samplingFrequencyIndexMap.get(Integer.valueOf((int) audioSampleEntry.getSampleRate()))).intValue());
            audioSpecificConfig.setChannelConfiguration(audioSampleEntry.getChannelCount());
            decoderConfigDescriptor.setAudioSpecificInfo(audioSpecificConfig);
            eSDescriptor.setDecoderConfigDescriptor(decoderConfigDescriptor);
            ByteBuffer serialize = eSDescriptor.serialize();
            eSDescriptorBox.setEsDescriptor(eSDescriptor);
            eSDescriptorBox.setData(serialize);
            audioSampleEntry.addBox(eSDescriptorBox);
            this.sampleDescriptionBox.addBox(audioSampleEntry);
            return;
        }
        this.width = mediaFormat.getInteger("width");
        this.height = mediaFormat.getInteger("height");
        this.timeScale = 90000;
        this.syncSamples = new LinkedList();
        this.handler = "vide";
        this.headerBox = new VideoMediaHeaderBox();
        this.sampleDescriptionBox = new SampleDescriptionBox();
        str = mediaFormat.getString("mime");
        VisualSampleEntry visualSampleEntry;
        if (str.equals("video/avc")) {
            visualSampleEntry = new VisualSampleEntry("avc1");
            visualSampleEntry.setDataReferenceIndex(1);
            visualSampleEntry.setDepth(24);
            visualSampleEntry.setFrameCount(1);
            visualSampleEntry.setHorizresolution(72.0d);
            visualSampleEntry.setVertresolution(72.0d);
            visualSampleEntry.setWidth(this.width);
            visualSampleEntry.setHeight(this.height);
            AvcConfigurationBox avcConfigurationBox = new AvcConfigurationBox();
            String str3 = "csd-0";
            if (mediaFormat.getByteBuffer(str3) != null) {
                ArrayList arrayList = new ArrayList();
                ByteBuffer byteBuffer = mediaFormat.getByteBuffer(str3);
                byteBuffer.position(4);
                byte[] bArr = new byte[byteBuffer.remaining()];
                byteBuffer.get(bArr);
                arrayList.add(bArr);
                ArrayList arrayList2 = new ArrayList();
                ByteBuffer byteBuffer2 = mediaFormat.getByteBuffer("csd-1");
                byteBuffer2.position(4);
                byte[] bArr2 = new byte[byteBuffer2.remaining()];
                byteBuffer2.get(bArr2);
                arrayList2.add(bArr2);
                avcConfigurationBox.setSequenceParameterSets(arrayList);
                avcConfigurationBox.setPictureParameterSets(arrayList2);
            }
            str3 = "level";
            if (mediaFormat.containsKey(str3)) {
                int integer = mediaFormat.getInteger(str3);
                if (integer == 1) {
                    avcConfigurationBox.setAvcLevelIndication(1);
                } else if (integer == 32) {
                    avcConfigurationBox.setAvcLevelIndication(2);
                } else if (integer == 4) {
                    avcConfigurationBox.setAvcLevelIndication(11);
                } else if (integer == 8) {
                    avcConfigurationBox.setAvcLevelIndication(12);
                } else if (integer == 16) {
                    avcConfigurationBox.setAvcLevelIndication(13);
                } else if (integer == 64) {
                    avcConfigurationBox.setAvcLevelIndication(21);
                } else if (integer == 128) {
                    avcConfigurationBox.setAvcLevelIndication(22);
                } else if (integer == 256) {
                    avcConfigurationBox.setAvcLevelIndication(3);
                } else if (integer == 512) {
                    avcConfigurationBox.setAvcLevelIndication(31);
                } else if (integer == 1024) {
                    avcConfigurationBox.setAvcLevelIndication(32);
                } else if (integer == 2048) {
                    avcConfigurationBox.setAvcLevelIndication(4);
                } else if (integer == 4096) {
                    avcConfigurationBox.setAvcLevelIndication(41);
                } else if (integer == 8192) {
                    avcConfigurationBox.setAvcLevelIndication(42);
                } else if (integer == 16384) {
                    avcConfigurationBox.setAvcLevelIndication(5);
                } else if (integer == 32768) {
                    avcConfigurationBox.setAvcLevelIndication(51);
                } else if (integer == 65536) {
                    avcConfigurationBox.setAvcLevelIndication(52);
                } else if (integer == 2) {
                    avcConfigurationBox.setAvcLevelIndication(27);
                }
            } else {
                avcConfigurationBox.setAvcLevelIndication(13);
            }
            if (mediaFormat.containsKey("profile")) {
                int integer2 = mediaFormat.getInteger("profile");
                if (integer2 == 1) {
                    avcConfigurationBox.setAvcProfileIndication(66);
                } else if (integer2 == 2) {
                    avcConfigurationBox.setAvcProfileIndication(77);
                } else if (integer2 == 4) {
                    avcConfigurationBox.setAvcProfileIndication(88);
                } else if (integer2 == 8) {
                    avcConfigurationBox.setAvcProfileIndication(100);
                } else if (integer2 == 16) {
                    avcConfigurationBox.setAvcProfileIndication(110);
                } else if (integer2 == 32) {
                    avcConfigurationBox.setAvcProfileIndication(122);
                } else if (integer2 == 64) {
                    avcConfigurationBox.setAvcProfileIndication(244);
                }
            } else {
                avcConfigurationBox.setAvcProfileIndication(100);
            }
            avcConfigurationBox.setBitDepthLumaMinus8(-1);
            avcConfigurationBox.setBitDepthChromaMinus8(-1);
            avcConfigurationBox.setChromaFormat(-1);
            avcConfigurationBox.setConfigurationVersion(1);
            avcConfigurationBox.setLengthSizeMinusOne(3);
            avcConfigurationBox.setProfileCompatibility(0);
            visualSampleEntry.addBox(avcConfigurationBox);
            this.sampleDescriptionBox.addBox(visualSampleEntry);
        } else if (str.equals("video/mp4v")) {
            visualSampleEntry = new VisualSampleEntry("mp4v");
            visualSampleEntry.setDataReferenceIndex(1);
            visualSampleEntry.setDepth(24);
            visualSampleEntry.setFrameCount(1);
            visualSampleEntry.setHorizresolution(72.0d);
            visualSampleEntry.setVertresolution(72.0d);
            visualSampleEntry.setWidth(this.width);
            visualSampleEntry.setHeight(this.height);
            this.sampleDescriptionBox.addBox(visualSampleEntry);
        }
    }

    public long getTrackId() {
        return this.trackId;
    }

    public void addSample(long j, BufferInfo bufferInfo) {
        int i = 1;
        if (this.isAudio || (bufferInfo.flags & 1) == 0) {
            i = 0;
        }
        this.samples.add(new Sample(j, (long) bufferInfo.size));
        LinkedList linkedList = this.syncSamples;
        if (!(linkedList == null || i == 0)) {
            linkedList.add(Integer.valueOf(this.samples.size()));
        }
        ArrayList arrayList = this.samplePresentationTimes;
        arrayList.add(new SamplePresentationTime(arrayList.size(), ((bufferInfo.presentationTimeUs * ((long) this.timeScale)) + 500000) / 1000000));
    }

    public void prepare() {
        int i;
        int i2;
        ArrayList arrayList = new ArrayList(this.samplePresentationTimes);
        Collections.sort(this.samplePresentationTimes, -$$Lambda$Track$WwpAJwhUb2DZllFb8kOYdyyS8pU.INSTANCE);
        this.sampleDurations = new long[this.samplePresentationTimes.size()];
        long j = Long.MAX_VALUE;
        int i3 = 0;
        Object obj = null;
        long j2 = 0;
        while (true) {
            i = 1;
            if (i3 >= this.samplePresentationTimes.size()) {
                break;
            }
            int i4;
            SamplePresentationTime samplePresentationTime = (SamplePresentationTime) this.samplePresentationTimes.get(i3);
            long access$000 = samplePresentationTime.presentationTime - j2;
            j2 = samplePresentationTime.presentationTime;
            this.sampleDurations[samplePresentationTime.index] = access$000;
            if (samplePresentationTime.index != 0) {
                i4 = i3;
                this.duration += access$000;
            } else {
                i4 = i3;
            }
            if (access$000 != 0) {
                j = Math.min(j, access$000);
            }
            i3 = i4;
            if (samplePresentationTime.index != i3) {
                obj = 1;
            }
            i3++;
        }
        long[] jArr = this.sampleDurations;
        if (jArr.length > 0) {
            i2 = 0;
            jArr[0] = j;
            this.duration += j;
        } else {
            i2 = 0;
        }
        while (i < arrayList.size()) {
            ((SamplePresentationTime) arrayList.get(i)).dt = this.sampleDurations[i] + ((SamplePresentationTime) arrayList.get(i - 1)).dt;
            i++;
        }
        if (obj != null) {
            this.sampleCompositions = new int[this.samplePresentationTimes.size()];
            while (i2 < this.samplePresentationTimes.size()) {
                SamplePresentationTime samplePresentationTime2 = (SamplePresentationTime) this.samplePresentationTimes.get(i2);
                this.sampleCompositions[samplePresentationTime2.index] = (int) (samplePresentationTime2.presentationTime - samplePresentationTime2.dt);
                i2++;
            }
        }
    }

    static /* synthetic */ int lambda$prepare$0(SamplePresentationTime samplePresentationTime, SamplePresentationTime samplePresentationTime2) {
        if (samplePresentationTime.presentationTime > samplePresentationTime2.presentationTime) {
            return 1;
        }
        return samplePresentationTime.presentationTime < samplePresentationTime2.presentationTime ? -1 : 0;
    }

    public ArrayList<Sample> getSamples() {
        return this.samples;
    }

    public long getDuration() {
        return this.duration;
    }

    public String getHandler() {
        return this.handler;
    }

    public AbstractMediaHeaderBox getMediaHeaderBox() {
        return this.headerBox;
    }

    public int[] getSampleCompositions() {
        return this.sampleCompositions;
    }

    public SampleDescriptionBox getSampleDescriptionBox() {
        return this.sampleDescriptionBox;
    }

    public long[] getSyncSamples() {
        LinkedList linkedList = this.syncSamples;
        if (linkedList == null || linkedList.isEmpty()) {
            return null;
        }
        long[] jArr = new long[this.syncSamples.size()];
        for (int i = 0; i < this.syncSamples.size(); i++) {
            jArr[i] = (long) ((Integer) this.syncSamples.get(i)).intValue();
        }
        return jArr;
    }

    public int getTimeScale() {
        return this.timeScale;
    }

    public Date getCreationTime() {
        return this.creationTime;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public float getVolume() {
        return this.volume;
    }

    public long[] getSampleDurations() {
        return this.sampleDurations;
    }

    public boolean isAudio() {
        return this.isAudio;
    }
}
