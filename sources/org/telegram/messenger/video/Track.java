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
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.exoplayer2.C0539C;

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
    class C06691 implements Comparator<SamplePresentationTime> {
        C06691() {
        }

        public int compare(SamplePresentationTime o1, SamplePresentationTime o2) {
            if (o1.presentationTime > o2.presentationTime) {
                return 1;
            }
            if (o1.presentationTime < o2.presentationTime) {
                return -1;
            }
            return 0;
        }
    }

    private class SamplePresentationTime {
        private long dt;
        private int index;
        private long presentationTime;

        public SamplePresentationTime(int idx, long time) {
            this.index = idx;
            this.presentationTime = time;
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

    public Track(int id, MediaFormat format, boolean audio) {
        MediaFormat mediaFormat = format;
        this.trackId = (long) id;
        this.isAudio = audio;
        if (this.isAudio) {
            r0.volume = 1.0f;
            r0.timeScale = mediaFormat.getInteger("sample-rate");
            r0.handler = "soun";
            r0.headerBox = new SoundMediaHeaderBox();
            r0.sampleDescriptionBox = new SampleDescriptionBox();
            AudioSampleEntry audioSampleEntry = new AudioSampleEntry(AudioSampleEntry.TYPE3);
            audioSampleEntry.setChannelCount(mediaFormat.getInteger("channel-count"));
            audioSampleEntry.setSampleRate((long) mediaFormat.getInteger("sample-rate"));
            audioSampleEntry.setDataReferenceIndex(1);
            audioSampleEntry.setSampleSize(16);
            ESDescriptorBox esds = new ESDescriptorBox();
            ESDescriptor descriptor = new ESDescriptor();
            descriptor.setEsId(0);
            SLConfigDescriptor slConfigDescriptor = new SLConfigDescriptor();
            slConfigDescriptor.setPredefined(2);
            descriptor.setSlConfigDescriptor(slConfigDescriptor);
            DecoderConfigDescriptor decoderConfigDescriptor = new DecoderConfigDescriptor();
            decoderConfigDescriptor.setObjectTypeIndication(64);
            decoderConfigDescriptor.setStreamType(5);
            decoderConfigDescriptor.setBufferSizeDB(1536);
            if (mediaFormat.containsKey("max-bitrate")) {
                decoderConfigDescriptor.setMaxBitRate((long) mediaFormat.getInteger("max-bitrate"));
            } else {
                decoderConfigDescriptor.setMaxBitRate(96000);
            }
            decoderConfigDescriptor.setAvgBitRate((long) r0.timeScale);
            AudioSpecificConfig audioSpecificConfig = new AudioSpecificConfig();
            audioSpecificConfig.setAudioObjectType(2);
            audioSpecificConfig.setSamplingFrequencyIndex(((Integer) samplingFrequencyIndexMap.get(Integer.valueOf((int) audioSampleEntry.getSampleRate()))).intValue());
            audioSpecificConfig.setChannelConfiguration(audioSampleEntry.getChannelCount());
            decoderConfigDescriptor.setAudioSpecificInfo(audioSpecificConfig);
            descriptor.setDecoderConfigDescriptor(decoderConfigDescriptor);
            ByteBuffer data = descriptor.serialize();
            esds.setEsDescriptor(descriptor);
            esds.setData(data);
            audioSampleEntry.addBox(esds);
            r0.sampleDescriptionBox.addBox(audioSampleEntry);
            return;
        }
        r0.width = mediaFormat.getInteger("width");
        r0.height = mediaFormat.getInteger("height");
        r0.timeScale = 90000;
        r0.syncSamples = new LinkedList();
        r0.handler = "vide";
        r0.headerBox = new VideoMediaHeaderBox();
        r0.sampleDescriptionBox = new SampleDescriptionBox();
        String mime = mediaFormat.getString("mime");
        if (mime.equals("video/avc")) {
            int level;
            VisualSampleEntry visualSampleEntry = new VisualSampleEntry(VisualSampleEntry.TYPE3);
            visualSampleEntry.setDataReferenceIndex(1);
            visualSampleEntry.setDepth(24);
            visualSampleEntry.setFrameCount(1);
            visualSampleEntry.setHorizresolution(72.0d);
            visualSampleEntry.setVertresolution(72.0d);
            visualSampleEntry.setWidth(r0.width);
            visualSampleEntry.setHeight(r0.height);
            AvcConfigurationBox avcConfigurationBox = new AvcConfigurationBox();
            if (mediaFormat.getByteBuffer("csd-0") != null) {
                ArrayList<byte[]> spsArray = new ArrayList();
                ByteBuffer spsBuff = mediaFormat.getByteBuffer("csd-0");
                spsBuff.position(4);
                byte[] spsBytes = new byte[spsBuff.remaining()];
                spsBuff.get(spsBytes);
                spsArray.add(spsBytes);
                ArrayList<byte[]> ppsArray = new ArrayList();
                ByteBuffer ppsBuff = mediaFormat.getByteBuffer("csd-1");
                ppsBuff.position(4);
                byte[] ppsBytes = new byte[ppsBuff.remaining()];
                ppsBuff.get(ppsBytes);
                ppsArray.add(ppsBytes);
                avcConfigurationBox.setSequenceParameterSets(spsArray);
                avcConfigurationBox.setPictureParameterSets(ppsArray);
            }
            if (mediaFormat.containsKey("level")) {
                level = mediaFormat.getInteger("level");
                if (level == 1) {
                    avcConfigurationBox.setAvcLevelIndication(1);
                } else if (level == 32) {
                    avcConfigurationBox.setAvcLevelIndication(2);
                } else if (level == 4) {
                    avcConfigurationBox.setAvcLevelIndication(11);
                } else if (level == 8) {
                    avcConfigurationBox.setAvcLevelIndication(12);
                } else if (level == 16) {
                    avcConfigurationBox.setAvcLevelIndication(13);
                } else if (level == 64) {
                    avcConfigurationBox.setAvcLevelIndication(21);
                } else if (level == 128) {
                    avcConfigurationBox.setAvcLevelIndication(22);
                } else if (level == 256) {
                    avcConfigurationBox.setAvcLevelIndication(3);
                } else if (level == 512) {
                    avcConfigurationBox.setAvcLevelIndication(31);
                } else if (level == 1024) {
                    avcConfigurationBox.setAvcLevelIndication(32);
                } else if (level == 2048) {
                    avcConfigurationBox.setAvcLevelIndication(4);
                } else if (level == 4096) {
                    avcConfigurationBox.setAvcLevelIndication(41);
                } else if (level == MessagesController.UPDATE_MASK_CHANNEL) {
                    avcConfigurationBox.setAvcLevelIndication(42);
                } else if (level == MessagesController.UPDATE_MASK_CHAT_ADMINS) {
                    avcConfigurationBox.setAvcLevelIndication(5);
                } else if (level == 32768) {
                    avcConfigurationBox.setAvcLevelIndication(51);
                } else if (level == C0539C.DEFAULT_BUFFER_SEGMENT_SIZE) {
                    avcConfigurationBox.setAvcLevelIndication(52);
                } else if (level == 2) {
                    avcConfigurationBox.setAvcLevelIndication(27);
                }
            } else {
                avcConfigurationBox.setAvcLevelIndication(13);
            }
            if (mediaFormat.containsKey("profile")) {
                level = mediaFormat.getInteger("profile");
                if (level == 1) {
                    avcConfigurationBox.setAvcProfileIndication(66);
                } else if (level == 2) {
                    avcConfigurationBox.setAvcProfileIndication(77);
                } else if (level == 4) {
                    avcConfigurationBox.setAvcProfileIndication(88);
                } else if (level == 8) {
                    avcConfigurationBox.setAvcProfileIndication(100);
                } else if (level == 16) {
                    avcConfigurationBox.setAvcProfileIndication(110);
                } else if (level == 32) {
                    avcConfigurationBox.setAvcProfileIndication(122);
                } else if (level == 64) {
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
            r0.sampleDescriptionBox.addBox(visualSampleEntry);
        } else if (mime.equals("video/mp4v")) {
            VisualSampleEntry visualSampleEntry2 = new VisualSampleEntry(VisualSampleEntry.TYPE1);
            visualSampleEntry2.setDataReferenceIndex(1);
            visualSampleEntry2.setDepth(24);
            visualSampleEntry2.setFrameCount(1);
            visualSampleEntry2.setHorizresolution(72.0d);
            visualSampleEntry2.setVertresolution(72.0d);
            visualSampleEntry2.setWidth(r0.width);
            visualSampleEntry2.setHeight(r0.height);
            r0.sampleDescriptionBox.addBox(visualSampleEntry2);
        }
    }

    public long getTrackId() {
        return this.trackId;
    }

    public void addSample(long offset, BufferInfo bufferInfo) {
        boolean z = true;
        if (this.isAudio || (bufferInfo.flags & 1) == 0) {
            z = false;
        }
        boolean isSyncFrame = z;
        this.samples.add(new Sample(offset, (long) bufferInfo.size));
        if (this.syncSamples != null && isSyncFrame) {
            this.syncSamples.add(Integer.valueOf(this.samples.size()));
        }
        this.samplePresentationTimes.add(new SamplePresentationTime(this.samplePresentationTimes.size(), ((bufferInfo.presentationTimeUs * ((long) this.timeScale)) + 500000) / C0539C.MICROS_PER_SECOND));
    }

    public void prepare() {
        int a;
        ArrayList<SamplePresentationTime> original = new ArrayList(this.samplePresentationTimes);
        Collections.sort(this.samplePresentationTimes, new C06691());
        this.sampleDurations = new long[this.samplePresentationTimes.size()];
        boolean outOfOrder = false;
        long minDelta = Long.MAX_VALUE;
        long lastPresentationTimeUs = 0;
        for (a = 0; a < r0.samplePresentationTimes.size(); a++) {
            SamplePresentationTime presentationTime = (SamplePresentationTime) r0.samplePresentationTimes.get(a);
            long delta = presentationTime.presentationTime - lastPresentationTimeUs;
            lastPresentationTimeUs = presentationTime.presentationTime;
            r0.sampleDurations[presentationTime.index] = delta;
            if (presentationTime.index != 0) {
                r0.duration += delta;
            }
            if (delta != 0) {
                minDelta = Math.min(minDelta, delta);
            }
            if (presentationTime.index != a) {
                outOfOrder = true;
            }
        }
        if (r0.sampleDurations.length > 0) {
            r0.sampleDurations[0] = minDelta;
            r0.duration += minDelta;
        }
        a = 1;
        while (a < original.size()) {
            boolean outOfOrder2 = outOfOrder;
            ((SamplePresentationTime) original.get(a)).dt = r0.sampleDurations[a] + ((SamplePresentationTime) original.get(a - 1)).dt;
            a++;
            outOfOrder = outOfOrder2;
        }
        if (outOfOrder) {
            r0.sampleCompositions = new int[r0.samplePresentationTimes.size()];
            int a2 = 0;
            while (true) {
                a = a2;
                if (a < r0.samplePresentationTimes.size()) {
                    SamplePresentationTime presentationTime2 = (SamplePresentationTime) r0.samplePresentationTimes.get(a);
                    r0.sampleCompositions[presentationTime2.index] = (int) (presentationTime2.presentationTime - presentationTime2.dt);
                    a2 = a + 1;
                } else {
                    return;
                }
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
                long[] returns = new long[this.syncSamples.size()];
                for (int i = 0; i < this.syncSamples.size(); i++) {
                    returns[i] = (long) ((Integer) this.syncSamples.get(i)).intValue();
                }
                return returns;
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
