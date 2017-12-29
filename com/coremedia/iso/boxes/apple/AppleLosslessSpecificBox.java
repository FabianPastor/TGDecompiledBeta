package com.coremedia.iso.boxes.apple;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.AbstractFullBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import java.nio.ByteBuffer;
import org.aspectj.lang.JoinPoint.StaticPart;
import org.aspectj.runtime.internal.Conversions;
import org.aspectj.runtime.reflect.Factory;

public final class AppleLosslessSpecificBox extends AbstractFullBox {
    public static final String TYPE = "alac";
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
    private static final /* synthetic */ StaticPart ajc$tjp_3 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_4 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_5 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_6 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_7 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_8 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_9 = null;
    private long bitRate;
    private int channels;
    private int historyMult;
    private int initialHistory;
    private int kModifier;
    private long maxCodedFrameSize;
    private long maxSamplePerFrame;
    private long sampleRate;
    private int sampleSize;
    private int unknown1;
    private int unknown2;

    static {
        ajc$preClinit();
    }

    private static /* synthetic */ void ajc$preClinit() {
        Factory factory = new Factory("AppleLosslessSpecificBox.java", AppleLosslessSpecificBox.class);
        ajc$tjp_0 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "getMaxSamplePerFrame", "com.coremedia.iso.boxes.apple.AppleLosslessSpecificBox", TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, "long"), 34);
        ajc$tjp_1 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "setMaxSamplePerFrame", "com.coremedia.iso.boxes.apple.AppleLosslessSpecificBox", "int", "maxSamplePerFrame", TtmlNode.ANONYMOUS_REGION_ID, "void"), 38);
        ajc$tjp_10 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "getKModifier", "com.coremedia.iso.boxes.apple.AppleLosslessSpecificBox", TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, "int"), 74);
        ajc$tjp_11 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "setKModifier", "com.coremedia.iso.boxes.apple.AppleLosslessSpecificBox", "int", "kModifier", TtmlNode.ANONYMOUS_REGION_ID, "void"), 78);
        ajc$tjp_12 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "getChannels", "com.coremedia.iso.boxes.apple.AppleLosslessSpecificBox", TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, "int"), 82);
        ajc$tjp_13 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "setChannels", "com.coremedia.iso.boxes.apple.AppleLosslessSpecificBox", "int", "channels", TtmlNode.ANONYMOUS_REGION_ID, "void"), 86);
        ajc$tjp_14 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "getUnknown2", "com.coremedia.iso.boxes.apple.AppleLosslessSpecificBox", TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, "int"), 90);
        ajc$tjp_15 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "setUnknown2", "com.coremedia.iso.boxes.apple.AppleLosslessSpecificBox", "int", "unknown2", TtmlNode.ANONYMOUS_REGION_ID, "void"), 94);
        ajc$tjp_16 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "getMaxCodedFrameSize", "com.coremedia.iso.boxes.apple.AppleLosslessSpecificBox", TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, "long"), 98);
        ajc$tjp_17 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "setMaxCodedFrameSize", "com.coremedia.iso.boxes.apple.AppleLosslessSpecificBox", "int", "maxCodedFrameSize", TtmlNode.ANONYMOUS_REGION_ID, "void"), 102);
        ajc$tjp_18 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "getBitRate", "com.coremedia.iso.boxes.apple.AppleLosslessSpecificBox", TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, "long"), 106);
        ajc$tjp_19 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "setBitRate", "com.coremedia.iso.boxes.apple.AppleLosslessSpecificBox", "int", "bitRate", TtmlNode.ANONYMOUS_REGION_ID, "void"), 110);
        ajc$tjp_2 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "getUnknown1", "com.coremedia.iso.boxes.apple.AppleLosslessSpecificBox", TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, "int"), 42);
        ajc$tjp_20 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "getSampleRate", "com.coremedia.iso.boxes.apple.AppleLosslessSpecificBox", TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, "long"), 114);
        ajc$tjp_21 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "setSampleRate", "com.coremedia.iso.boxes.apple.AppleLosslessSpecificBox", "int", "sampleRate", TtmlNode.ANONYMOUS_REGION_ID, "void"), 118);
        ajc$tjp_3 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "setUnknown1", "com.coremedia.iso.boxes.apple.AppleLosslessSpecificBox", "int", "unknown1", TtmlNode.ANONYMOUS_REGION_ID, "void"), 46);
        ajc$tjp_4 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "getSampleSize", "com.coremedia.iso.boxes.apple.AppleLosslessSpecificBox", TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, "int"), 50);
        ajc$tjp_5 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "setSampleSize", "com.coremedia.iso.boxes.apple.AppleLosslessSpecificBox", "int", "sampleSize", TtmlNode.ANONYMOUS_REGION_ID, "void"), 54);
        ajc$tjp_6 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "getHistoryMult", "com.coremedia.iso.boxes.apple.AppleLosslessSpecificBox", TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, "int"), 58);
        ajc$tjp_7 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "setHistoryMult", "com.coremedia.iso.boxes.apple.AppleLosslessSpecificBox", "int", "historyMult", TtmlNode.ANONYMOUS_REGION_ID, "void"), 62);
        ajc$tjp_8 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "getInitialHistory", "com.coremedia.iso.boxes.apple.AppleLosslessSpecificBox", TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, "int"), 66);
        ajc$tjp_9 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "setInitialHistory", "com.coremedia.iso.boxes.apple.AppleLosslessSpecificBox", "int", "initialHistory", TtmlNode.ANONYMOUS_REGION_ID, "void"), 70);
    }

    public long getMaxSamplePerFrame() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_0, this, this));
        return this.maxSamplePerFrame;
    }

    public void setMaxSamplePerFrame(int maxSamplePerFrame) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_1, (Object) this, (Object) this, Conversions.intObject(maxSamplePerFrame)));
        this.maxSamplePerFrame = (long) maxSamplePerFrame;
    }

    public int getUnknown1() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_2, this, this));
        return this.unknown1;
    }

    public void setUnknown1(int unknown1) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_3, (Object) this, (Object) this, Conversions.intObject(unknown1)));
        this.unknown1 = unknown1;
    }

    public int getSampleSize() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_4, this, this));
        return this.sampleSize;
    }

    public void setSampleSize(int sampleSize) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_5, (Object) this, (Object) this, Conversions.intObject(sampleSize)));
        this.sampleSize = sampleSize;
    }

    public int getHistoryMult() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_6, this, this));
        return this.historyMult;
    }

    public void setHistoryMult(int historyMult) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_7, (Object) this, (Object) this, Conversions.intObject(historyMult)));
        this.historyMult = historyMult;
    }

    public int getInitialHistory() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_8, this, this));
        return this.initialHistory;
    }

    public void setInitialHistory(int initialHistory) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_9, (Object) this, (Object) this, Conversions.intObject(initialHistory)));
        this.initialHistory = initialHistory;
    }

    public int getKModifier() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_10, this, this));
        return this.kModifier;
    }

    public void setKModifier(int kModifier) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_11, (Object) this, (Object) this, Conversions.intObject(kModifier)));
        this.kModifier = kModifier;
    }

    public int getChannels() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_12, this, this));
        return this.channels;
    }

    public void setChannels(int channels) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_13, (Object) this, (Object) this, Conversions.intObject(channels)));
        this.channels = channels;
    }

    public int getUnknown2() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_14, this, this));
        return this.unknown2;
    }

    public void setUnknown2(int unknown2) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_15, (Object) this, (Object) this, Conversions.intObject(unknown2)));
        this.unknown2 = unknown2;
    }

    public long getMaxCodedFrameSize() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_16, this, this));
        return this.maxCodedFrameSize;
    }

    public void setMaxCodedFrameSize(int maxCodedFrameSize) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_17, (Object) this, (Object) this, Conversions.intObject(maxCodedFrameSize)));
        this.maxCodedFrameSize = (long) maxCodedFrameSize;
    }

    public long getBitRate() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_18, this, this));
        return this.bitRate;
    }

    public void setBitRate(int bitRate) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_19, (Object) this, (Object) this, Conversions.intObject(bitRate)));
        this.bitRate = (long) bitRate;
    }

    public long getSampleRate() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_20, this, this));
        return this.sampleRate;
    }

    public void setSampleRate(int sampleRate) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_21, (Object) this, (Object) this, Conversions.intObject(sampleRate)));
        this.sampleRate = (long) sampleRate;
    }

    public void _parseDetails(ByteBuffer content) {
        parseVersionAndFlags(content);
        this.maxSamplePerFrame = IsoTypeReader.readUInt32(content);
        this.unknown1 = IsoTypeReader.readUInt8(content);
        this.sampleSize = IsoTypeReader.readUInt8(content);
        this.historyMult = IsoTypeReader.readUInt8(content);
        this.initialHistory = IsoTypeReader.readUInt8(content);
        this.kModifier = IsoTypeReader.readUInt8(content);
        this.channels = IsoTypeReader.readUInt8(content);
        this.unknown2 = IsoTypeReader.readUInt16(content);
        this.maxCodedFrameSize = IsoTypeReader.readUInt32(content);
        this.bitRate = IsoTypeReader.readUInt32(content);
        this.sampleRate = IsoTypeReader.readUInt32(content);
    }

    protected void getContent(ByteBuffer byteBuffer) {
        writeVersionAndFlags(byteBuffer);
        IsoTypeWriter.writeUInt32(byteBuffer, this.maxSamplePerFrame);
        IsoTypeWriter.writeUInt8(byteBuffer, this.unknown1);
        IsoTypeWriter.writeUInt8(byteBuffer, this.sampleSize);
        IsoTypeWriter.writeUInt8(byteBuffer, this.historyMult);
        IsoTypeWriter.writeUInt8(byteBuffer, this.initialHistory);
        IsoTypeWriter.writeUInt8(byteBuffer, this.kModifier);
        IsoTypeWriter.writeUInt8(byteBuffer, this.channels);
        IsoTypeWriter.writeUInt16(byteBuffer, this.unknown2);
        IsoTypeWriter.writeUInt32(byteBuffer, this.maxCodedFrameSize);
        IsoTypeWriter.writeUInt32(byteBuffer, this.bitRate);
        IsoTypeWriter.writeUInt32(byteBuffer, this.sampleRate);
    }

    public AppleLosslessSpecificBox() {
        super("alac");
    }

    protected long getContentSize() {
        return 28;
    }
}
