package com.coremedia.iso.boxes;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.AbstractFullBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import com.googlecode.mp4parser.util.CastUtils;
import java.nio.ByteBuffer;
import org.aspectj.lang.JoinPoint.StaticPart;
import org.aspectj.runtime.reflect.Factory;

public class SyncSampleBox extends AbstractFullBox {
    public static final String TYPE = "stss";
    private static final /* synthetic */ StaticPart ajc$tjp_0 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_1 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_2 = null;
    private long[] sampleNumber;

    static {
        ajc$preClinit();
    }

    private static /* synthetic */ void ajc$preClinit() {
        Factory factory = new Factory("SyncSampleBox.java", SyncSampleBox.class);
        ajc$tjp_0 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "getSampleNumber", "com.coremedia.iso.boxes.SyncSampleBox", TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, "[J"), 46);
        ajc$tjp_1 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "toString", "com.coremedia.iso.boxes.SyncSampleBox", TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, "java.lang.String"), 77);
        ajc$tjp_2 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "setSampleNumber", "com.coremedia.iso.boxes.SyncSampleBox", "[J", "sampleNumber", TtmlNode.ANONYMOUS_REGION_ID, "void"), 81);
    }

    public SyncSampleBox() {
        super(TYPE);
    }

    public long[] getSampleNumber() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_0, this, this));
        return this.sampleNumber;
    }

    protected long getContentSize() {
        return (long) ((this.sampleNumber.length * 4) + 8);
    }

    public void _parseDetails(ByteBuffer content) {
        parseVersionAndFlags(content);
        int entryCount = CastUtils.l2i(IsoTypeReader.readUInt32(content));
        this.sampleNumber = new long[entryCount];
        for (int i = 0; i < entryCount; i++) {
            this.sampleNumber[i] = IsoTypeReader.readUInt32(content);
        }
    }

    protected void getContent(ByteBuffer byteBuffer) {
        writeVersionAndFlags(byteBuffer);
        IsoTypeWriter.writeUInt32(byteBuffer, (long) this.sampleNumber.length);
        for (long aSampleNumber : this.sampleNumber) {
            IsoTypeWriter.writeUInt32(byteBuffer, aSampleNumber);
        }
    }

    public String toString() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_1, this, this));
        return "SyncSampleBox[entryCount=" + this.sampleNumber.length + "]";
    }

    public void setSampleNumber(long[] sampleNumber) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_2, (Object) this, (Object) this, (Object) sampleNumber));
        this.sampleNumber = sampleNumber;
    }
}
