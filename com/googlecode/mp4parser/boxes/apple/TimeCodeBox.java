package com.googlecode.mp4parser.boxes.apple;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.coremedia.iso.boxes.sampleentry.SampleEntry;
import com.googlecode.mp4parser.AbstractBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import java.nio.ByteBuffer;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.JoinPoint.StaticPart;
import org.aspectj.runtime.internal.Conversions;
import org.aspectj.runtime.reflect.Factory;
import org.telegram.messenger.exoplayer2.extractor.ts.TsExtractor;

public class TimeCodeBox extends AbstractBox implements SampleEntry {
    public static final String TYPE = "tmcd";
    private static final /* synthetic */ StaticPart ajc$tjp_0 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_1 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_10 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_11 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_12 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_13 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_14 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_15 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_16 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_2 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_3 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_4 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_5 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_6 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_7 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_8 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_9 = null;
    int dataReferenceIndex;
    long flags;
    int frameDuration;
    int numberOfFrames;
    int reserved1;
    int reserved2;
    byte[] rest = new byte[0];
    int timeScale;

    static {
        ajc$preClinit();
    }

    private static /* synthetic */ void ajc$preClinit() {
        Factory factory = new Factory("TimeCodeBox.java", TimeCodeBox.class);
        ajc$tjp_0 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig("1", "getDataReferenceIndex", "com.googlecode.mp4parser.boxes.apple.TimeCodeBox", "", "", "", "int"), 81);
        ajc$tjp_1 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig("1", "setDataReferenceIndex", "com.googlecode.mp4parser.boxes.apple.TimeCodeBox", "int", "dataReferenceIndex", "", "void"), 85);
        ajc$tjp_10 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig("1", "setReserved1", "com.googlecode.mp4parser.boxes.apple.TimeCodeBox", "int", "reserved1", "", "void"), (int) TsExtractor.TS_STREAM_TYPE_HDMV_DTS);
        ajc$tjp_11 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig("1", "getReserved2", "com.googlecode.mp4parser.boxes.apple.TimeCodeBox", "", "", "", "int"), (int) TsExtractor.TS_STREAM_TYPE_SPLICE_INFO);
        ajc$tjp_12 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig("1", "setReserved2", "com.googlecode.mp4parser.boxes.apple.TimeCodeBox", "int", "reserved2", "", "void"), (int) TsExtractor.TS_STREAM_TYPE_DTS);
        ajc$tjp_13 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig("1", "getFlags", "com.googlecode.mp4parser.boxes.apple.TimeCodeBox", "", "", "", "long"), 142);
        ajc$tjp_14 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig("1", "setFlags", "com.googlecode.mp4parser.boxes.apple.TimeCodeBox", "long", "flags", "", "void"), 146);
        ajc$tjp_15 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig("1", "getRest", "com.googlecode.mp4parser.boxes.apple.TimeCodeBox", "", "", "", "[B"), 150);
        ajc$tjp_16 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig("1", "setRest", "com.googlecode.mp4parser.boxes.apple.TimeCodeBox", "[B", "rest", "", "void"), 154);
        ajc$tjp_2 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig("1", "toString", "com.googlecode.mp4parser.boxes.apple.TimeCodeBox", "", "", "", "java.lang.String"), 91);
        ajc$tjp_3 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig("1", "getTimeScale", "com.googlecode.mp4parser.boxes.apple.TimeCodeBox", "", "", "", "int"), 102);
        ajc$tjp_4 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig("1", "setTimeScale", "com.googlecode.mp4parser.boxes.apple.TimeCodeBox", "int", "timeScale", "", "void"), 106);
        ajc$tjp_5 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig("1", "getFrameDuration", "com.googlecode.mp4parser.boxes.apple.TimeCodeBox", "", "", "", "int"), 110);
        ajc$tjp_6 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig("1", "setFrameDuration", "com.googlecode.mp4parser.boxes.apple.TimeCodeBox", "int", "frameDuration", "", "void"), 114);
        ajc$tjp_7 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig("1", "getNumberOfFrames", "com.googlecode.mp4parser.boxes.apple.TimeCodeBox", "", "", "", "int"), 118);
        ajc$tjp_8 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig("1", "setNumberOfFrames", "com.googlecode.mp4parser.boxes.apple.TimeCodeBox", "int", "numberOfFrames", "", "void"), 122);
        ajc$tjp_9 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig("1", "getReserved1", "com.googlecode.mp4parser.boxes.apple.TimeCodeBox", "", "", "", "int"), 126);
    }

    public TimeCodeBox() {
        super(TYPE);
    }

    protected long getContentSize() {
        return (long) (this.rest.length + 28);
    }

    protected void getContent(ByteBuffer bb) {
        bb.put(new byte[6]);
        IsoTypeWriter.writeUInt16(bb, this.dataReferenceIndex);
        bb.putInt(this.reserved1);
        IsoTypeWriter.writeUInt32(bb, this.flags);
        bb.putInt(this.timeScale);
        bb.putInt(this.frameDuration);
        IsoTypeWriter.writeUInt8(bb, this.numberOfFrames);
        IsoTypeWriter.writeUInt24(bb, this.reserved2);
        bb.put(this.rest);
    }

    protected void _parseDetails(ByteBuffer content) {
        content.position(6);
        this.dataReferenceIndex = IsoTypeReader.readUInt16(content);
        this.reserved1 = content.getInt();
        this.flags = IsoTypeReader.readUInt32(content);
        this.timeScale = content.getInt();
        this.frameDuration = content.getInt();
        this.numberOfFrames = IsoTypeReader.readUInt8(content);
        this.reserved2 = IsoTypeReader.readUInt24(content);
        this.rest = new byte[content.remaining()];
        content.get(this.rest);
    }

    public int getDataReferenceIndex() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_0, this, this));
        return this.dataReferenceIndex;
    }

    public void setDataReferenceIndex(int dataReferenceIndex) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_1, (Object) this, (Object) this, Conversions.intObject(dataReferenceIndex)));
        this.dataReferenceIndex = dataReferenceIndex;
    }

    public String toString() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_2, this, this));
        return "TimeCodeBox{timeScale=" + this.timeScale + ", frameDuration=" + this.frameDuration + ", numberOfFrames=" + this.numberOfFrames + ", reserved1=" + this.reserved1 + ", reserved2=" + this.reserved2 + ", flags=" + this.flags + '}';
    }

    public int getTimeScale() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_3, this, this));
        return this.timeScale;
    }

    public void setTimeScale(int timeScale) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_4, (Object) this, (Object) this, Conversions.intObject(timeScale)));
        this.timeScale = timeScale;
    }

    public int getFrameDuration() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_5, this, this));
        return this.frameDuration;
    }

    public void setFrameDuration(int frameDuration) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_6, (Object) this, (Object) this, Conversions.intObject(frameDuration)));
        this.frameDuration = frameDuration;
    }

    public int getNumberOfFrames() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_7, this, this));
        return this.numberOfFrames;
    }

    public void setNumberOfFrames(int numberOfFrames) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_8, (Object) this, (Object) this, Conversions.intObject(numberOfFrames)));
        this.numberOfFrames = numberOfFrames;
    }

    public int getReserved1() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_9, this, this));
        return this.reserved1;
    }

    public void setReserved1(int reserved1) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_10, (Object) this, (Object) this, Conversions.intObject(reserved1)));
        this.reserved1 = reserved1;
    }

    public int getReserved2() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_11, this, this));
        return this.reserved2;
    }

    public void setReserved2(int reserved2) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_12, (Object) this, (Object) this, Conversions.intObject(reserved2)));
        this.reserved2 = reserved2;
    }

    public long getFlags() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_13, this, this));
        return this.flags;
    }

    public void setFlags(long flags) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_14, (Object) this, (Object) this, Conversions.longObject(flags)));
        this.flags = flags;
    }

    public byte[] getRest() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_15, this, this));
        return this.rest;
    }

    public void setRest(byte[] rest) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_16, (Object) this, (Object) this, (Object) rest));
        this.rest = rest;
    }
}
