package org.telegram.messenger.video;

import android.media.MediaCodec.BufferInfo;
import android.media.MediaFormat;
import com.coremedia.iso.boxes.AbstractMediaHeaderBox;
import com.coremedia.iso.boxes.Box;
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
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.exoplayer2.C0542C;

public class Track {
    private static Map<Integer, Integer> samplingFrequencyIndexMap = new HashMap();
    private Date creationTime = new Date();
    private long duration = 0;
    private boolean first = true;
    private String handler;
    private AbstractMediaHeaderBox headerBox = null;
    private int height;
    private boolean isAudio = false;
    private int[] sampleCompositions;
    private SampleDescriptionBox sampleDescriptionBox = null;
    private long[] sampleDurations;
    private ArrayList<SamplePresentationTime> samplePresentationTimes = new ArrayList();
    private ArrayList<Sample> samples = new ArrayList();
    private LinkedList<Integer> syncSamples = null;
    private int timeScale;
    private long trackId = 0;
    private float volume = 0.0f;
    private int width;

    /* renamed from: org.telegram.messenger.video.Track$1 */
    class C06721 implements Comparator<SamplePresentationTime> {
        C06721() {
        }

        public int compare(SamplePresentationTime samplePresentationTime, SamplePresentationTime samplePresentationTime2) {
            if (samplePresentationTime.presentationTime > samplePresentationTime2.presentationTime) {
                return 1;
            }
            return samplePresentationTime.presentationTime < samplePresentationTime2.presentationTime ? -1 : null;
        }
    }

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
        if (this.isAudio == 0) {
            this.width = mediaFormat.getInteger("width");
            this.height = mediaFormat.getInteger("height");
            this.timeScale = 90000;
            this.syncSamples = new LinkedList();
            this.handler = "vide";
            this.headerBox = new VideoMediaHeaderBox();
            this.sampleDescriptionBox = new SampleDescriptionBox();
            i = mediaFormat.getString("mime");
            if (i.equals("video/avc")) {
                i = new VisualSampleEntry(VisualSampleEntry.TYPE3);
                i.setDataReferenceIndex(1);
                i.setDepth(24);
                i.setFrameCount(1);
                i.setHorizresolution(72.0d);
                i.setVertresolution(72.0d);
                i.setWidth(this.width);
                i.setHeight(this.height);
                Box avcConfigurationBox = new AvcConfigurationBox();
                if (mediaFormat.getByteBuffer("csd-0") != null) {
                    List arrayList = new ArrayList();
                    ByteBuffer byteBuffer = mediaFormat.getByteBuffer("csd-0");
                    byteBuffer.position(4);
                    Object obj = new byte[byteBuffer.remaining()];
                    byteBuffer.get(obj);
                    arrayList.add(obj);
                    List arrayList2 = new ArrayList();
                    ByteBuffer byteBuffer2 = mediaFormat.getByteBuffer("csd-1");
                    byteBuffer2.position(4);
                    Object obj2 = new byte[byteBuffer2.remaining()];
                    byteBuffer2.get(obj2);
                    arrayList2.add(obj2);
                    avcConfigurationBox.setSequenceParameterSets(arrayList);
                    avcConfigurationBox.setPictureParameterSets(arrayList2);
                }
                if (mediaFormat.containsKey("level")) {
                    int integer = mediaFormat.getInteger("level");
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
                    } else if (integer == MessagesController.UPDATE_MASK_CHANNEL) {
                        avcConfigurationBox.setAvcLevelIndication(42);
                    } else if (integer == MessagesController.UPDATE_MASK_CHAT_ADMINS) {
                        avcConfigurationBox.setAvcLevelIndication(5);
                    } else if (integer == 32768) {
                        avcConfigurationBox.setAvcLevelIndication(51);
                    } else if (integer == C0542C.DEFAULT_BUFFER_SEGMENT_SIZE) {
                        avcConfigurationBox.setAvcLevelIndication(52);
                    } else if (integer == 2) {
                        avcConfigurationBox.setAvcLevelIndication(27);
                    }
                } else {
                    avcConfigurationBox.setAvcLevelIndication(13);
                }
                if (mediaFormat.containsKey("profile")) {
                    mediaFormat = mediaFormat.getInteger("profile");
                    if (mediaFormat == 1) {
                        avcConfigurationBox.setAvcProfileIndication(66);
                    } else if (mediaFormat == 2) {
                        avcConfigurationBox.setAvcProfileIndication(77);
                    } else if (mediaFormat == 4) {
                        avcConfigurationBox.setAvcProfileIndication(88);
                    } else if (mediaFormat == 8) {
                        avcConfigurationBox.setAvcProfileIndication(100);
                    } else if (mediaFormat == 16) {
                        avcConfigurationBox.setAvcProfileIndication(110);
                    } else if (mediaFormat == 32) {
                        avcConfigurationBox.setAvcProfileIndication(122);
                    } else if (mediaFormat == 64) {
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
                i.addBox(avcConfigurationBox);
                this.sampleDescriptionBox.addBox(i);
                return;
            } else if (i.equals("video/mp4v") != 0) {
                i = new VisualSampleEntry(VisualSampleEntry.TYPE1);
                i.setDataReferenceIndex(1);
                i.setDepth(24);
                i.setFrameCount(1);
                i.setHorizresolution(72.0d);
                i.setVertresolution(72.0d);
                i.setWidth(this.width);
                i.setHeight(this.height);
                this.sampleDescriptionBox.addBox(i);
                return;
            } else {
                return;
            }
        }
        this.volume = NUM;
        this.timeScale = mediaFormat.getInteger("sample-rate");
        this.handler = "soun";
        this.headerBox = new SoundMediaHeaderBox();
        this.sampleDescriptionBox = new SampleDescriptionBox();
        i = new AudioSampleEntry(AudioSampleEntry.TYPE3);
        i.setChannelCount(mediaFormat.getInteger("channel-count"));
        i.setSampleRate((long) mediaFormat.getInteger("sample-rate"));
        i.setDataReferenceIndex(1);
        i.setSampleSize(16);
        Box eSDescriptorBox = new ESDescriptorBox();
        ESDescriptor eSDescriptor = new ESDescriptor();
        eSDescriptor.setEsId(0);
        SLConfigDescriptor sLConfigDescriptor = new SLConfigDescriptor();
        sLConfigDescriptor.setPredefined(2);
        eSDescriptor.setSlConfigDescriptor(sLConfigDescriptor);
        DecoderConfigDescriptor decoderConfigDescriptor = new DecoderConfigDescriptor();
        decoderConfigDescriptor.setObjectTypeIndication(64);
        decoderConfigDescriptor.setStreamType(true);
        decoderConfigDescriptor.setBufferSizeDB(true);
        if (mediaFormat.containsKey("max-bitrate")) {
            decoderConfigDescriptor.setMaxBitRate((long) mediaFormat.getInteger("max-bitrate"));
        } else {
            decoderConfigDescriptor.setMaxBitRate(96000);
        }
        decoderConfigDescriptor.setAvgBitRate((long) this.timeScale);
        mediaFormat = new AudioSpecificConfig();
        mediaFormat.setAudioObjectType(2);
        mediaFormat.setSamplingFrequencyIndex(((Integer) samplingFrequencyIndexMap.get(Integer.valueOf((int) i.getSampleRate()))).intValue());
        mediaFormat.setChannelConfiguration(i.getChannelCount());
        decoderConfigDescriptor.setAudioSpecificInfo(mediaFormat);
        eSDescriptor.setDecoderConfigDescriptor(decoderConfigDescriptor);
        mediaFormat = eSDescriptor.serialize();
        eSDescriptorBox.setEsDescriptor(eSDescriptor);
        eSDescriptorBox.setData(mediaFormat);
        i.addBox(eSDescriptorBox);
        this.sampleDescriptionBox.addBox(i);
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
        if (!(this.syncSamples == null || r1 == 0)) {
            this.syncSamples.add(Integer.valueOf(this.samples.size()));
        }
        this.samplePresentationTimes.add(new SamplePresentationTime(this.samplePresentationTimes.size(), ((bufferInfo.presentationTimeUs * ((long) this.timeScale)) + 500000) / C0542C.MICROS_PER_SECOND));
    }

    public void prepare() {
        int i;
        ArrayList arrayList = new ArrayList(this.samplePresentationTimes);
        Collections.sort(this.samplePresentationTimes, new C06721());
        this.sampleDurations = new long[this.samplePresentationTimes.size()];
        long j = Long.MAX_VALUE;
        int i2 = 0;
        long j2 = 0;
        int i3 = 0;
        while (true) {
            int i4 = 1;
            if (i2 >= r0.samplePresentationTimes.size()) {
                break;
            }
            SamplePresentationTime samplePresentationTime = (SamplePresentationTime) r0.samplePresentationTimes.get(i2);
            int i5 = i2;
            long access$000 = samplePresentationTime.presentationTime - j2;
            j2 = samplePresentationTime.presentationTime;
            r0.sampleDurations[samplePresentationTime.index] = access$000;
            if (samplePresentationTime.index != 0) {
                r0.duration += access$000;
            }
            if (access$000 != 0) {
                j = Math.min(j, access$000);
            }
            i2 = i5;
            if (samplePresentationTime.index != i2) {
                i3 = 1;
            }
            i2++;
        }
        if (r0.sampleDurations.length > 0) {
            i = 0;
            r0.sampleDurations[0] = j;
            r0.duration += j;
        } else {
            i = 0;
        }
        while (i4 < arrayList.size()) {
            ((SamplePresentationTime) arrayList.get(i4)).dt = r0.sampleDurations[i4] + ((SamplePresentationTime) arrayList.get(i4 - 1)).dt;
            i4++;
        }
        if (i3 != 0) {
            r0.sampleCompositions = new int[r0.samplePresentationTimes.size()];
            while (i < r0.samplePresentationTimes.size()) {
                SamplePresentationTime samplePresentationTime2 = (SamplePresentationTime) r0.samplePresentationTimes.get(i);
                r0.sampleCompositions[samplePresentationTime2.index] = (int) (samplePresentationTime2.presentationTime - samplePresentationTime2.dt);
                i++;
            }
        }
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
        if (this.syncSamples != null) {
            if (!this.syncSamples.isEmpty()) {
                long[] jArr = new long[this.syncSamples.size()];
                for (int i = 0; i < this.syncSamples.size(); i++) {
                    jArr[i] = (long) ((Integer) this.syncSamples.get(i)).intValue();
                }
                return jArr;
            }
        }
        return null;
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
