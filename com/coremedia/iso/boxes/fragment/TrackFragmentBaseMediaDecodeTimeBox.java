package com.coremedia.iso.boxes.fragment;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.AbstractFullBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import java.nio.ByteBuffer;
import org.aspectj.lang.JoinPoint.StaticPart;
import org.aspectj.runtime.internal.Conversions;
import org.aspectj.runtime.reflect.Factory;

public class TrackFragmentBaseMediaDecodeTimeBox extends AbstractFullBox {
    public static final String TYPE = "tfdt";
    private static final /* synthetic */ StaticPart ajc$tjp_0 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_1 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_2 = null;
    private long baseMediaDecodeTime;

    static {
        ajc$preClinit();
    }

    private static /* synthetic */ void ajc$preClinit() {
        Factory factory = new Factory("TrackFragmentBaseMediaDecodeTimeBox.java", TrackFragmentBaseMediaDecodeTimeBox.class);
        ajc$tjp_0 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "getBaseMediaDecodeTime", "com.coremedia.iso.boxes.fragment.TrackFragmentBaseMediaDecodeTimeBox", TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, "long"), 65);
        ajc$tjp_1 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "setBaseMediaDecodeTime", "com.coremedia.iso.boxes.fragment.TrackFragmentBaseMediaDecodeTimeBox", "long", "baseMediaDecodeTime", TtmlNode.ANONYMOUS_REGION_ID, "void"), 69);
        ajc$tjp_2 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "toString", "com.coremedia.iso.boxes.fragment.TrackFragmentBaseMediaDecodeTimeBox", TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, "java.lang.String"), 74);
    }

    public TrackFragmentBaseMediaDecodeTimeBox() {
        super(TYPE);
    }

    protected long getContentSize() {
        return (long) (getVersion() == 0 ? 8 : 12);
    }

    protected void getContent(ByteBuffer byteBuffer) {
        writeVersionAndFlags(byteBuffer);
        if (getVersion() == 1) {
            IsoTypeWriter.writeUInt64(byteBuffer, this.baseMediaDecodeTime);
        } else {
            IsoTypeWriter.writeUInt32(byteBuffer, this.baseMediaDecodeTime);
        }
    }

    public void _parseDetails(ByteBuffer content) {
        parseVersionAndFlags(content);
        if (getVersion() == 1) {
            this.baseMediaDecodeTime = IsoTypeReader.readUInt64(content);
        } else {
            this.baseMediaDecodeTime = IsoTypeReader.readUInt32(content);
        }
    }

    public long getBaseMediaDecodeTime() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_0, this, this));
        return this.baseMediaDecodeTime;
    }

    public void setBaseMediaDecodeTime(long baseMediaDecodeTime) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_1, (Object) this, (Object) this, Conversions.longObject(baseMediaDecodeTime)));
        this.baseMediaDecodeTime = baseMediaDecodeTime;
    }

    public String toString() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_2, this, this));
        return "TrackFragmentBaseMediaDecodeTimeBox{baseMediaDecodeTime=" + this.baseMediaDecodeTime + '}';
    }
}
