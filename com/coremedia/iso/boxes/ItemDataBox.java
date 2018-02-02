package com.coremedia.iso.boxes;

import com.googlecode.mp4parser.AbstractBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import java.nio.ByteBuffer;
import org.aspectj.lang.JoinPoint.StaticPart;
import org.aspectj.runtime.reflect.Factory;
import org.telegram.messenger.exoplayer2.upstream.DataSchemeDataSource;

public class ItemDataBox extends AbstractBox {
    public static final String TYPE = "idat";
    private static final /* synthetic */ StaticPart ajc$tjp_0 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_1 = null;
    ByteBuffer data = ByteBuffer.allocate(0);

    static {
        ajc$preClinit();
    }

    private static /* synthetic */ void ajc$preClinit() {
        Factory factory = new Factory("ItemDataBox.java", ItemDataBox.class);
        ajc$tjp_0 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "getData", "com.coremedia.iso.boxes.ItemDataBox", TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, "java.nio.ByteBuffer"), 19);
        ajc$tjp_1 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "setData", "com.coremedia.iso.boxes.ItemDataBox", "java.nio.ByteBuffer", DataSchemeDataSource.SCHEME_DATA, TtmlNode.ANONYMOUS_REGION_ID, "void"), 23);
    }

    public ItemDataBox() {
        super(TYPE);
    }

    public ByteBuffer getData() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_0, this, this));
        return this.data;
    }

    public void setData(ByteBuffer data) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_1, (Object) this, (Object) this, (Object) data));
        this.data = data;
    }

    protected long getContentSize() {
        return (long) this.data.limit();
    }

    public void _parseDetails(ByteBuffer content) {
        this.data = content.slice();
        content.position(content.position() + content.remaining());
    }

    protected void getContent(ByteBuffer byteBuffer) {
        byteBuffer.put(this.data);
    }
}
