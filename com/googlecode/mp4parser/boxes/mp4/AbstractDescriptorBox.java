package com.googlecode.mp4parser.boxes.mp4;

import com.googlecode.mp4parser.AbstractFullBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.BaseDescriptor;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.ObjectDescriptorFactory;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.aspectj.lang.JoinPoint.StaticPart;
import org.aspectj.runtime.reflect.Factory;

public class AbstractDescriptorBox extends AbstractFullBox {
    private static final /* synthetic */ StaticPart ajc$tjp_0 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_1 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_2 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_3 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_4 = null;
    private static Logger log = Logger.getLogger(AbstractDescriptorBox.class.getName());
    protected ByteBuffer data;
    protected BaseDescriptor descriptor;

    private static /* synthetic */ void ajc$preClinit() {
        Factory factory = new Factory("AbstractDescriptorBox.java", AbstractDescriptorBox.class);
        ajc$tjp_0 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "getData", "com.googlecode.mp4parser.boxes.mp4.AbstractDescriptorBox", TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, "java.nio.ByteBuffer"), 42);
        ajc$tjp_1 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "getDescriptor", "com.googlecode.mp4parser.boxes.mp4.AbstractDescriptorBox", TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, "com.googlecode.mp4parser.boxes.mp4.objectdescriptors.BaseDescriptor"), 58);
        ajc$tjp_2 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "getDescriptorAsString", "com.googlecode.mp4parser.boxes.mp4.AbstractDescriptorBox", TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, "java.lang.String"), 62);
        ajc$tjp_3 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "setDescriptor", "com.googlecode.mp4parser.boxes.mp4.AbstractDescriptorBox", "com.googlecode.mp4parser.boxes.mp4.objectdescriptors.BaseDescriptor", "descriptor", TtmlNode.ANONYMOUS_REGION_ID, "void"), 66);
        ajc$tjp_4 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "setData", "com.googlecode.mp4parser.boxes.mp4.AbstractDescriptorBox", "java.nio.ByteBuffer", "data", TtmlNode.ANONYMOUS_REGION_ID, "void"), 70);
    }

    static {
        ajc$preClinit();
    }

    public AbstractDescriptorBox(String type) {
        super(type);
    }

    protected void getContent(ByteBuffer byteBuffer) {
        writeVersionAndFlags(byteBuffer);
        this.data.rewind();
        byteBuffer.put(this.data);
    }

    protected long getContentSize() {
        return (long) (this.data.limit() + 4);
    }

    public void setDescriptor(BaseDescriptor descriptor) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_3, (Object) this, (Object) this, (Object) descriptor));
        this.descriptor = descriptor;
    }

    public void setData(ByteBuffer data) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_4, (Object) this, (Object) this, (Object) data));
        this.data = data;
    }

    public void _parseDetails(ByteBuffer content) {
        parseVersionAndFlags(content);
        this.data = content.slice();
        content.position(content.position() + content.remaining());
        try {
            this.data.rewind();
            this.descriptor = ObjectDescriptorFactory.createFrom(-1, this.data);
        } catch (IOException e) {
            log.log(Level.WARNING, "Error parsing ObjectDescriptor", e);
        } catch (IndexOutOfBoundsException e2) {
            log.log(Level.WARNING, "Error parsing ObjectDescriptor", e2);
        }
    }
}
