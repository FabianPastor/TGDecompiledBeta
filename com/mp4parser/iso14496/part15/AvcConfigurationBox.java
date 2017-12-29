package com.mp4parser.iso14496.part15;

import com.googlecode.mp4parser.AbstractBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import java.nio.ByteBuffer;
import java.util.List;
import org.aspectj.lang.JoinPoint.StaticPart;
import org.aspectj.runtime.internal.Conversions;
import org.aspectj.runtime.reflect.Factory;

public final class AvcConfigurationBox extends AbstractBox {
    private static final /* synthetic */ StaticPart ajc$tjp_0 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_1 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_10 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_11 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_12 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_13 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_14 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_15 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_16 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_17 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_18 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_19 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_2 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_20 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_21 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_22 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_23 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_24 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_25 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_26 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_27 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_28 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_29 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_3 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_4 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_5 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_6 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_7 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_8 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_9 = null;
    public AvcDecoderConfigurationRecord avcDecoderConfigurationRecord = new AvcDecoderConfigurationRecord();

    static {
        ajc$preClinit();
    }

    private static /* synthetic */ void ajc$preClinit() {
        Factory factory = new Factory("AvcConfigurationBox.java", AvcConfigurationBox.class);
        ajc$tjp_0 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "getConfigurationVersion", "com.mp4parser.iso14496.part15.AvcConfigurationBox", TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, "int"), 44);
        ajc$tjp_1 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "getAvcProfileIndication", "com.mp4parser.iso14496.part15.AvcConfigurationBox", TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, "int"), 48);
        ajc$tjp_10 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "setAvcLevelIndication", "com.mp4parser.iso14496.part15.AvcConfigurationBox", "int", "avcLevelIndication", TtmlNode.ANONYMOUS_REGION_ID, "void"), 84);
        ajc$tjp_11 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "setLengthSizeMinusOne", "com.mp4parser.iso14496.part15.AvcConfigurationBox", "int", "lengthSizeMinusOne", TtmlNode.ANONYMOUS_REGION_ID, "void"), 88);
        ajc$tjp_12 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "setSequenceParameterSets", "com.mp4parser.iso14496.part15.AvcConfigurationBox", "java.util.List", "sequenceParameterSets", TtmlNode.ANONYMOUS_REGION_ID, "void"), 92);
        ajc$tjp_13 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "setPictureParameterSets", "com.mp4parser.iso14496.part15.AvcConfigurationBox", "java.util.List", "pictureParameterSets", TtmlNode.ANONYMOUS_REGION_ID, "void"), 96);
        ajc$tjp_14 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "getChromaFormat", "com.mp4parser.iso14496.part15.AvcConfigurationBox", TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, "int"), 100);
        ajc$tjp_15 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "setChromaFormat", "com.mp4parser.iso14496.part15.AvcConfigurationBox", "int", "chromaFormat", TtmlNode.ANONYMOUS_REGION_ID, "void"), 104);
        ajc$tjp_16 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "getBitDepthLumaMinus8", "com.mp4parser.iso14496.part15.AvcConfigurationBox", TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, "int"), 108);
        ajc$tjp_17 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "setBitDepthLumaMinus8", "com.mp4parser.iso14496.part15.AvcConfigurationBox", "int", "bitDepthLumaMinus8", TtmlNode.ANONYMOUS_REGION_ID, "void"), 112);
        ajc$tjp_18 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "getBitDepthChromaMinus8", "com.mp4parser.iso14496.part15.AvcConfigurationBox", TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, "int"), 116);
        ajc$tjp_19 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "setBitDepthChromaMinus8", "com.mp4parser.iso14496.part15.AvcConfigurationBox", "int", "bitDepthChromaMinus8", TtmlNode.ANONYMOUS_REGION_ID, "void"), 120);
        ajc$tjp_2 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "getProfileCompatibility", "com.mp4parser.iso14496.part15.AvcConfigurationBox", TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, "int"), 52);
        ajc$tjp_20 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "getSequenceParameterSetExts", "com.mp4parser.iso14496.part15.AvcConfigurationBox", TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, "java.util.List"), 124);
        ajc$tjp_21 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "setSequenceParameterSetExts", "com.mp4parser.iso14496.part15.AvcConfigurationBox", "java.util.List", "sequenceParameterSetExts", TtmlNode.ANONYMOUS_REGION_ID, "void"), 128);
        ajc$tjp_22 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "hasExts", "com.mp4parser.iso14496.part15.AvcConfigurationBox", TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, "boolean"), 132);
        ajc$tjp_23 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "setHasExts", "com.mp4parser.iso14496.part15.AvcConfigurationBox", "boolean", "hasExts", TtmlNode.ANONYMOUS_REGION_ID, "void"), 136);
        ajc$tjp_24 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "getContentSize", "com.mp4parser.iso14496.part15.AvcConfigurationBox", TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, "long"), 147);
        ajc$tjp_25 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "getContent", "com.mp4parser.iso14496.part15.AvcConfigurationBox", "java.nio.ByteBuffer", "byteBuffer", TtmlNode.ANONYMOUS_REGION_ID, "void"), 153);
        ajc$tjp_26 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "getSPS", "com.mp4parser.iso14496.part15.AvcConfigurationBox", TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, "[Ljava.lang.String;"), 158);
        ajc$tjp_27 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "getPPS", "com.mp4parser.iso14496.part15.AvcConfigurationBox", TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, "[Ljava.lang.String;"), 162);
        ajc$tjp_28 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "getavcDecoderConfigurationRecord", "com.mp4parser.iso14496.part15.AvcConfigurationBox", TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, "com.mp4parser.iso14496.part15.AvcDecoderConfigurationRecord"), 167);
        ajc$tjp_29 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "toString", "com.mp4parser.iso14496.part15.AvcConfigurationBox", TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, "java.lang.String"), 172);
        ajc$tjp_3 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "getAvcLevelIndication", "com.mp4parser.iso14496.part15.AvcConfigurationBox", TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, "int"), 56);
        ajc$tjp_4 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "getLengthSizeMinusOne", "com.mp4parser.iso14496.part15.AvcConfigurationBox", TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, "int"), 60);
        ajc$tjp_5 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "getSequenceParameterSets", "com.mp4parser.iso14496.part15.AvcConfigurationBox", TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, "java.util.List"), 64);
        ajc$tjp_6 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "getPictureParameterSets", "com.mp4parser.iso14496.part15.AvcConfigurationBox", TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, "java.util.List"), 68);
        ajc$tjp_7 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "setConfigurationVersion", "com.mp4parser.iso14496.part15.AvcConfigurationBox", "int", "configurationVersion", TtmlNode.ANONYMOUS_REGION_ID, "void"), 72);
        ajc$tjp_8 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "setAvcProfileIndication", "com.mp4parser.iso14496.part15.AvcConfigurationBox", "int", "avcProfileIndication", TtmlNode.ANONYMOUS_REGION_ID, "void"), 76);
        ajc$tjp_9 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "setProfileCompatibility", "com.mp4parser.iso14496.part15.AvcConfigurationBox", "int", "profileCompatibility", TtmlNode.ANONYMOUS_REGION_ID, "void"), 80);
    }

    public AvcConfigurationBox() {
        super("avcC");
    }

    public void setConfigurationVersion(int configurationVersion) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_7, (Object) this, (Object) this, Conversions.intObject(configurationVersion)));
        this.avcDecoderConfigurationRecord.configurationVersion = configurationVersion;
    }

    public void setAvcProfileIndication(int avcProfileIndication) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_8, (Object) this, (Object) this, Conversions.intObject(avcProfileIndication)));
        this.avcDecoderConfigurationRecord.avcProfileIndication = avcProfileIndication;
    }

    public void setProfileCompatibility(int profileCompatibility) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_9, (Object) this, (Object) this, Conversions.intObject(profileCompatibility)));
        this.avcDecoderConfigurationRecord.profileCompatibility = profileCompatibility;
    }

    public void setAvcLevelIndication(int avcLevelIndication) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_10, (Object) this, (Object) this, Conversions.intObject(avcLevelIndication)));
        this.avcDecoderConfigurationRecord.avcLevelIndication = avcLevelIndication;
    }

    public void setLengthSizeMinusOne(int lengthSizeMinusOne) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_11, (Object) this, (Object) this, Conversions.intObject(lengthSizeMinusOne)));
        this.avcDecoderConfigurationRecord.lengthSizeMinusOne = lengthSizeMinusOne;
    }

    public void setSequenceParameterSets(List<byte[]> sequenceParameterSets) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_12, (Object) this, (Object) this, (Object) sequenceParameterSets));
        this.avcDecoderConfigurationRecord.sequenceParameterSets = sequenceParameterSets;
    }

    public void setPictureParameterSets(List<byte[]> pictureParameterSets) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_13, (Object) this, (Object) this, (Object) pictureParameterSets));
        this.avcDecoderConfigurationRecord.pictureParameterSets = pictureParameterSets;
    }

    public void setChromaFormat(int chromaFormat) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_15, (Object) this, (Object) this, Conversions.intObject(chromaFormat)));
        this.avcDecoderConfigurationRecord.chromaFormat = chromaFormat;
    }

    public void setBitDepthLumaMinus8(int bitDepthLumaMinus8) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_17, (Object) this, (Object) this, Conversions.intObject(bitDepthLumaMinus8)));
        this.avcDecoderConfigurationRecord.bitDepthLumaMinus8 = bitDepthLumaMinus8;
    }

    public void setBitDepthChromaMinus8(int bitDepthChromaMinus8) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_19, (Object) this, (Object) this, Conversions.intObject(bitDepthChromaMinus8)));
        this.avcDecoderConfigurationRecord.bitDepthChromaMinus8 = bitDepthChromaMinus8;
    }

    public void _parseDetails(ByteBuffer content) {
        this.avcDecoderConfigurationRecord = new AvcDecoderConfigurationRecord(content);
    }

    public long getContentSize() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_24, this, this));
        return this.avcDecoderConfigurationRecord.getContentSize();
    }

    public void getContent(ByteBuffer byteBuffer) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_25, (Object) this, (Object) this, (Object) byteBuffer));
        this.avcDecoderConfigurationRecord.getContent(byteBuffer);
    }

    public String toString() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_29, this, this));
        return "AvcConfigurationBox{avcDecoderConfigurationRecord=" + this.avcDecoderConfigurationRecord + '}';
    }
}
