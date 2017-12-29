package com.coremedia.iso.boxes;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.coremedia.iso.Utf8;
import com.googlecode.mp4parser.AbstractFullBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import java.nio.ByteBuffer;
import org.aspectj.lang.JoinPoint.StaticPart;
import org.aspectj.runtime.reflect.Factory;

public class DescriptionBox extends AbstractFullBox {
    public static final String TYPE = "dscp";
    private static final /* synthetic */ StaticPart ajc$tjp_0 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_1 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_2 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_3 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_4 = null;
    private String description;
    private String language;

    static {
        ajc$preClinit();
    }

    private static /* synthetic */ void ajc$preClinit() {
        Factory factory = new Factory("DescriptionBox.java", DescriptionBox.class);
        ajc$tjp_0 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "getLanguage", "com.coremedia.iso.boxes.DescriptionBox", TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, "java.lang.String"), 40);
        ajc$tjp_1 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "getDescription", "com.coremedia.iso.boxes.DescriptionBox", TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, "java.lang.String"), 44);
        ajc$tjp_2 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "toString", "com.coremedia.iso.boxes.DescriptionBox", TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, "java.lang.String"), 67);
        ajc$tjp_3 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "setLanguage", "com.coremedia.iso.boxes.DescriptionBox", "java.lang.String", "language", TtmlNode.ANONYMOUS_REGION_ID, "void"), 71);
        ajc$tjp_4 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "setDescription", "com.coremedia.iso.boxes.DescriptionBox", "java.lang.String", "description", TtmlNode.ANONYMOUS_REGION_ID, "void"), 75);
    }

    public DescriptionBox() {
        super(TYPE);
    }

    public String getLanguage() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_0, this, this));
        return this.language;
    }

    public String getDescription() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_1, this, this));
        return this.description;
    }

    protected long getContentSize() {
        return (long) (Utf8.utf8StringLengthInBytes(this.description) + 7);
    }

    public void _parseDetails(ByteBuffer content) {
        parseVersionAndFlags(content);
        this.language = IsoTypeReader.readIso639(content);
        this.description = IsoTypeReader.readString(content);
    }

    protected void getContent(ByteBuffer byteBuffer) {
        writeVersionAndFlags(byteBuffer);
        IsoTypeWriter.writeIso639(byteBuffer, this.language);
        byteBuffer.put(Utf8.convert(this.description));
        byteBuffer.put((byte) 0);
    }

    public String toString() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_2, this, this));
        return "DescriptionBox[language=" + getLanguage() + ";description=" + getDescription() + "]";
    }

    public void setLanguage(String language) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_3, (Object) this, (Object) this, (Object) language));
        this.language = language;
    }

    public void setDescription(String description) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_4, (Object) this, (Object) this, (Object) description));
        this.description = description;
    }
}
