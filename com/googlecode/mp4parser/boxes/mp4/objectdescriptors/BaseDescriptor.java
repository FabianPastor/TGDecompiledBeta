package com.googlecode.mp4parser.boxes.mp4.objectdescriptors;

import com.coremedia.iso.IsoTypeReader;
import java.io.IOException;
import java.nio.ByteBuffer;

@Descriptor(tags = {0})
public abstract class BaseDescriptor {
    static final /* synthetic */ boolean $assertionsDisabled = (!BaseDescriptor.class.desiredAssertionStatus());
    int sizeBytes;
    int sizeOfInstance;
    int tag;

    public abstract void parseDetail(ByteBuffer byteBuffer) throws IOException;

    public int getTag() {
        return this.tag;
    }

    public int getSize() {
        return (this.sizeOfInstance + 1) + this.sizeBytes;
    }

    public int getSizeOfInstance() {
        return this.sizeOfInstance;
    }

    public int getSizeBytes() {
        return this.sizeBytes;
    }

    public final void parse(int tag, ByteBuffer bb) throws IOException {
        this.tag = tag;
        int tmp = IsoTypeReader.readUInt8(bb);
        int i = 0 + 1;
        this.sizeOfInstance = tmp & 127;
        while ((tmp >>> 7) == 1) {
            tmp = IsoTypeReader.readUInt8(bb);
            i++;
            this.sizeOfInstance = (this.sizeOfInstance << 7) | (tmp & 127);
        }
        this.sizeBytes = i;
        ByteBuffer detailSource = bb.slice();
        detailSource.limit(this.sizeOfInstance);
        parseDetail(detailSource);
        if ($assertionsDisabled || detailSource.remaining() == 0) {
            bb.position(bb.position() + this.sizeOfInstance);
            return;
        }
        throw new AssertionError(new StringBuilder(String.valueOf(getClass().getSimpleName())).append(" has not been fully parsed").toString());
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("BaseDescriptor");
        sb.append("{tag=").append(this.tag);
        sb.append(", sizeOfInstance=").append(this.sizeOfInstance);
        sb.append('}');
        return sb.toString();
    }
}
