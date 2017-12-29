package com.coremedia.iso.boxes.fragment;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.AbstractFullBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import java.nio.ByteBuffer;
import org.aspectj.lang.JoinPoint.StaticPart;
import org.aspectj.runtime.internal.Conversions;
import org.aspectj.runtime.reflect.Factory;

public class MovieFragmentRandomAccessOffsetBox extends AbstractFullBox {
    public static final String TYPE = "mfro";
    private static final /* synthetic */ StaticPart ajc$tjp_0 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_1 = null;
    private long mfraSize;

    static {
        ajc$preClinit();
    }

    private static /* synthetic */ void ajc$preClinit() {
        Factory factory = new Factory("MovieFragmentRandomAccessOffsetBox.java", MovieFragmentRandomAccessOffsetBox.class);
        ajc$tjp_0 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "getMfraSize", "com.coremedia.iso.boxes.fragment.MovieFragmentRandomAccessOffsetBox", TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, "long"), 56);
        ajc$tjp_1 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "setMfraSize", "com.coremedia.iso.boxes.fragment.MovieFragmentRandomAccessOffsetBox", "long", "mfraSize", TtmlNode.ANONYMOUS_REGION_ID, "void"), 60);
    }

    public MovieFragmentRandomAccessOffsetBox() {
        super(TYPE);
    }

    protected long getContentSize() {
        return 8;
    }

    public void _parseDetails(ByteBuffer content) {
        parseVersionAndFlags(content);
        this.mfraSize = IsoTypeReader.readUInt32(content);
    }

    protected void getContent(ByteBuffer byteBuffer) {
        writeVersionAndFlags(byteBuffer);
        IsoTypeWriter.writeUInt32(byteBuffer, this.mfraSize);
    }

    public long getMfraSize() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_0, this, this));
        return this.mfraSize;
    }

    public void setMfraSize(long mfraSize) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_1, (Object) this, (Object) this, Conversions.longObject(mfraSize)));
        this.mfraSize = mfraSize;
    }
}
