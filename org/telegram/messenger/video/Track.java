package org.telegram.messenger.video;

import android.annotation.TargetApi;
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
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import org.telegram.messenger.exoplayer.C;
import org.telegram.messenger.volley.DefaultRetryPolicy;

@TargetApi(16)
public class Track {
    private static Map<Integer, Integer> samplingFrequencyIndexMap = new HashMap();
    private Date creationTime = new Date();
    private long duration = 0;
    private boolean first = true;
    private String handler;
    private AbstractMediaHeaderBox headerBox = null;
    private int height;
    private boolean isAudio = false;
    private long lastPresentationTimeUs = 0;
    private SampleDescriptionBox sampleDescriptionBox = null;
    private ArrayList<Long> sampleDurations = new ArrayList();
    private ArrayList<Sample> samples = new ArrayList();
    private LinkedList<Integer> syncSamples = null;
    private int timeScale;
    private long trackId = 0;
    private float volume = 0.0f;
    private int width;

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

    public Track(int id, MediaFormat format, boolean audio) throws Exception {
        this.trackId = (long) id;
        this.isAudio = audio;
        if (this.isAudio) {
            this.sampleDurations.add(Long.valueOf(1024));
            this.duration = 1024;
            this.volume = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
            this.timeScale = format.getInteger("sample-rate");
            this.handler = "soun";
            this.headerBox = new SoundMediaHeaderBox();
            this.sampleDescriptionBox = new SampleDescriptionBox();
            AudioSampleEntry audioSampleEntry = new AudioSampleEntry(AudioSampleEntry.TYPE3);
            audioSampleEntry.setChannelCount(format.getInteger("channel-count"));
            audioSampleEntry.setSampleRate((long) format.getInteger("sample-rate"));
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
            decoderConfigDescriptor.setMaxBitRate(96000);
            decoderConfigDescriptor.setAvgBitRate(96000);
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
            this.sampleDescriptionBox.addBox(audioSampleEntry);
            return;
        }
        this.sampleDurations.add(Long.valueOf(3015));
        this.duration = 3015;
        this.width = format.getInteger("width");
        this.height = format.getInteger("height");
        this.timeScale = 90000;
        this.syncSamples = new LinkedList();
        this.handler = "vide";
        this.headerBox = new VideoMediaHeaderBox();
        this.sampleDescriptionBox = new SampleDescriptionBox();
        String mime = format.getString("mime");
        VisualSampleEntry visualSampleEntry;
        if (mime.equals("video/avc")) {
            visualSampleEntry = new VisualSampleEntry(VisualSampleEntry.TYPE3);
            visualSampleEntry.setDataReferenceIndex(1);
            visualSampleEntry.setDepth(24);
            visualSampleEntry.setFrameCount(1);
            visualSampleEntry.setHorizresolution(72.0d);
            visualSampleEntry.setVertresolution(72.0d);
            visualSampleEntry.setWidth(this.width);
            visualSampleEntry.setHeight(this.height);
            AvcConfigurationBox avcConfigurationBox = new AvcConfigurationBox();
            if (format.getByteBuffer("csd-0") != null) {
                ArrayList<byte[]> spsArray = new ArrayList();
                ByteBuffer spsBuff = format.getByteBuffer("csd-0");
                spsBuff.position(4);
                Object spsBytes = new byte[spsBuff.remaining()];
                spsBuff.get(spsBytes);
                spsArray.add(spsBytes);
                ArrayList<byte[]> ppsArray = new ArrayList();
                ByteBuffer ppsBuff = format.getByteBuffer("csd-1");
                ppsBuff.position(4);
                byte[] ppsBytes = new byte[ppsBuff.remaining()];
                ppsBuff.get(ppsBytes);
                ppsArray.add(ppsBytes);
                avcConfigurationBox.setSequenceParameterSets(spsArray);
                avcConfigurationBox.setPictureParameterSets(ppsArray);
            }
            avcConfigurationBox.setAvcLevelIndication(13);
            avcConfigurationBox.setAvcProfileIndication(100);
            avcConfigurationBox.setBitDepthLumaMinus8(-1);
            avcConfigurationBox.setBitDepthChromaMinus8(-1);
            avcConfigurationBox.setChromaFormat(-1);
            avcConfigurationBox.setConfigurationVersion(1);
            avcConfigurationBox.setLengthSizeMinusOne(3);
            avcConfigurationBox.setProfileCompatibility(0);
            visualSampleEntry.addBox(avcConfigurationBox);
            this.sampleDescriptionBox.addBox(visualSampleEntry);
        } else if (mime.equals("video/mp4v")) {
            visualSampleEntry = new VisualSampleEntry(VisualSampleEntry.TYPE1);
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

    public void addSample(long offset, BufferInfo bufferInfo) {
        long delta = bufferInfo.presentationTimeUs - this.lastPresentationTimeUs;
        if (delta >= 0) {
            boolean isSyncFrame;
            if (this.isAudio || (bufferInfo.flags & 1) == 0) {
                isSyncFrame = false;
            } else {
                isSyncFrame = true;
            }
            this.samples.add(new Sample(offset, (long) bufferInfo.size));
            if (this.syncSamples != null && isSyncFrame) {
                this.syncSamples.add(Integer.valueOf(this.samples.size()));
            }
            delta = ((((long) this.timeScale) * delta) + 500000) / C.MICROS_PER_SECOND;
            this.lastPresentationTimeUs = bufferInfo.presentationTimeUs;
            if (!this.first) {
                this.sampleDurations.add(this.sampleDurations.size() - 1, Long.valueOf(delta));
                this.duration += delta;
            }
            this.first = false;
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

    public SampleDescriptionBox getSampleDescriptionBox() {
        return this.sampleDescriptionBox;
    }

    public long[] getSyncSamples() {
        if (this.syncSamples == null || this.syncSamples.isEmpty()) {
            return null;
        }
        long[] returns = new long[this.syncSamples.size()];
        for (int i = 0; i < this.syncSamples.size(); i++) {
            returns[i] = (long) ((Integer) this.syncSamples.get(i)).intValue();
        }
        return returns;
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

    public ArrayList<Long> getSampleDurations() {
        return this.sampleDurations;
    }

    public boolean isAudio() {
        return this.isAudio;
    }
}
