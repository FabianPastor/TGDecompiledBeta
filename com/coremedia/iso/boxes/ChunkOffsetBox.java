package com.coremedia.iso.boxes;

import com.googlecode.mp4parser.AbstractFullBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import org.aspectj.lang.JoinPoint.StaticPart;
import org.aspectj.runtime.reflect.Factory;

public abstract class ChunkOffsetBox extends AbstractFullBox {
    private static final /* synthetic */ StaticPart ajc$tjp_0 = null;

    static {
        ajc$preClinit();
    }

    private static /* synthetic */ void ajc$preClinit() {
        Factory factory = new Factory("ChunkOffsetBox.java", ChunkOffsetBox.class);
        ajc$tjp_0 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "toString", "com.coremedia.iso.boxes.ChunkOffsetBox", TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, "java.lang.String"), 18);
    }

    public abstract long[] getChunkOffsets();

    public abstract void setChunkOffsets(long[] jArr);

    public ChunkOffsetBox(String type) {
        super(type);
    }

    public String toString() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_0, this, this));
        return new StringBuilder(String.valueOf(getClass().getSimpleName())).append("[entryCount=").append(getChunkOffsets().length).append("]").toString();
    }
}
