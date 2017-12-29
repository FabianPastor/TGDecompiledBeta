package com.coremedia.iso.boxes;

import com.googlecode.mp4parser.AbstractBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import java.nio.ByteBuffer;
import org.aspectj.lang.JoinPoint.StaticPart;
import org.aspectj.runtime.reflect.Factory;

public class UserBox extends AbstractBox {
    public static final String TYPE = "uuid";
    private static final /* synthetic */ StaticPart ajc$tjp_0 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_1 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_2 = null;
    byte[] data;

    static {
        ajc$preClinit();
    }

    private static /* synthetic */ void ajc$preClinit() {
        Factory factory = new Factory("UserBox.java", UserBox.class);
        ajc$tjp_0 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "toString", "com.coremedia.iso.boxes.UserBox", TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, "java.lang.String"), 40);
        ajc$tjp_1 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "getData", "com.coremedia.iso.boxes.UserBox", TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, "[B"), 47);
        ajc$tjp_2 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "setData", "com.coremedia.iso.boxes.UserBox", "[B", "data", TtmlNode.ANONYMOUS_REGION_ID, "void"), 51);
    }

    public UserBox(byte[] userType) {
        super(TYPE, userType);
    }

    protected long getContentSize() {
        return (long) this.data.length;
    }

    public String toString() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_0, this, this));
        return "UserBox[type=" + getType() + ";userType=" + new String(getUserType()) + ";contentLength=" + this.data.length + "]";
    }

    public byte[] getData() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_1, this, this));
        return this.data;
    }

    public void setData(byte[] data) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_2, (Object) this, (Object) this, (Object) data));
        this.data = data;
    }

    public void _parseDetails(ByteBuffer content) {
        this.data = new byte[content.remaining()];
        content.get(this.data);
    }

    protected void getContent(ByteBuffer byteBuffer) {
        byteBuffer.put(this.data);
    }
}
