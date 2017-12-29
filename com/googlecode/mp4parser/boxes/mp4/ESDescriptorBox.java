package com.googlecode.mp4parser.boxes.mp4;

import com.googlecode.mp4parser.RequiresParseDetailAspect;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.ESDescriptor;
import org.aspectj.lang.JoinPoint.StaticPart;
import org.aspectj.runtime.reflect.Factory;

public class ESDescriptorBox extends AbstractDescriptorBox {
    private static final /* synthetic */ StaticPart ajc$tjp_0 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_1 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_2 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_3 = null;

    static {
        ajc$preClinit();
    }

    private static /* synthetic */ void ajc$preClinit() {
        Factory factory = new Factory("ESDescriptorBox.java", ESDescriptorBox.class);
        ajc$tjp_0 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "getEsDescriptor", "com.googlecode.mp4parser.boxes.mp4.ESDescriptorBox", TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, "com.googlecode.mp4parser.boxes.mp4.objectdescriptors.ESDescriptor"), 33);
        ajc$tjp_1 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "setEsDescriptor", "com.googlecode.mp4parser.boxes.mp4.ESDescriptorBox", "com.googlecode.mp4parser.boxes.mp4.objectdescriptors.ESDescriptor", "esDescriptor", TtmlNode.ANONYMOUS_REGION_ID, "void"), 37);
        ajc$tjp_2 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "equals", "com.googlecode.mp4parser.boxes.mp4.ESDescriptorBox", "java.lang.Object", "o", TtmlNode.ANONYMOUS_REGION_ID, "boolean"), 42);
        ajc$tjp_3 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "hashCode", "com.googlecode.mp4parser.boxes.mp4.ESDescriptorBox", TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, "int"), 53);
    }

    public ESDescriptorBox() {
        super("esds");
    }

    public void setEsDescriptor(ESDescriptor esDescriptor) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_1, (Object) this, (Object) this, (Object) esDescriptor));
        super.setDescriptor(esDescriptor);
    }

    public boolean equals(Object o) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_2, (Object) this, (Object) this, o));
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ESDescriptorBox that = (ESDescriptorBox) o;
        if (this.data != null) {
            if (this.data.equals(that.data)) {
                return true;
            }
        } else if (that.data == null) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_3, this, this));
        return this.data != null ? this.data.hashCode() : 0;
    }
}
