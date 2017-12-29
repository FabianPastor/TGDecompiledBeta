package com.coremedia.iso.boxes;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import java.nio.ByteBuffer;
import org.aspectj.lang.JoinPoint.StaticPart;
import org.aspectj.runtime.reflect.Factory;

public class SoundMediaHeaderBox extends AbstractMediaHeaderBox {
    public static final String TYPE = "smhd";
    private static final /* synthetic */ StaticPart ajc$tjp_0 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_1 = null;
    private float balance;

    static {
        ajc$preClinit();
    }

    private static /* synthetic */ void ajc$preClinit() {
        Factory factory = new Factory("SoundMediaHeaderBox.java", SoundMediaHeaderBox.class);
        ajc$tjp_0 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "getBalance", "com.coremedia.iso.boxes.SoundMediaHeaderBox", TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, "float"), 36);
        ajc$tjp_1 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "toString", "com.coremedia.iso.boxes.SoundMediaHeaderBox", TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, "java.lang.String"), 58);
    }

    public SoundMediaHeaderBox() {
        super(TYPE);
    }

    public float getBalance() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_0, this, this));
        return this.balance;
    }

    protected long getContentSize() {
        return 8;
    }

    public void _parseDetails(ByteBuffer content) {
        parseVersionAndFlags(content);
        this.balance = IsoTypeReader.readFixedPoint88(content);
        IsoTypeReader.readUInt16(content);
    }

    protected void getContent(ByteBuffer byteBuffer) {
        writeVersionAndFlags(byteBuffer);
        IsoTypeWriter.writeFixedPoint88(byteBuffer, (double) this.balance);
        IsoTypeWriter.writeUInt16(byteBuffer, 0);
    }

    public String toString() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_1, this, this));
        return "SoundMediaHeaderBox[balance=" + getBalance() + "]";
    }
}
