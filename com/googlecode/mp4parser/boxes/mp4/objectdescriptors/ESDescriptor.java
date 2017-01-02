package com.googlecode.mp4parser.boxes.mp4.objectdescriptors;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Descriptor(tags = {3})
public class ESDescriptor extends BaseDescriptor {
    private static Logger log = Logger.getLogger(ESDescriptor.class.getName());
    int URLFlag;
    int URLLength = 0;
    String URLString;
    DecoderConfigDescriptor decoderConfigDescriptor;
    int dependsOnEsId;
    int esId;
    int oCREsId;
    int oCRstreamFlag;
    List<BaseDescriptor> otherDescriptors = new ArrayList();
    int remoteODFlag;
    SLConfigDescriptor slConfigDescriptor;
    int streamDependenceFlag;
    int streamPriority;

    public void parseDetail(ByteBuffer bb) throws IOException {
        BaseDescriptor descriptor;
        long read;
        this.esId = IsoTypeReader.readUInt16(bb);
        int data = IsoTypeReader.readUInt8(bb);
        this.streamDependenceFlag = data >>> 7;
        this.URLFlag = (data >>> 6) & 1;
        this.oCRstreamFlag = (data >>> 5) & 1;
        this.streamPriority = data & 31;
        if (this.streamDependenceFlag == 1) {
            this.dependsOnEsId = IsoTypeReader.readUInt16(bb);
        }
        if (this.URLFlag == 1) {
            this.URLLength = IsoTypeReader.readUInt8(bb);
            this.URLString = IsoTypeReader.readString(bb, this.URLLength);
        }
        if (this.oCRstreamFlag == 1) {
            this.oCREsId = IsoTypeReader.readUInt16(bb);
        }
        int baseSize = (((((getSizeBytes() + 1) + 2) + 1) + (this.streamDependenceFlag == 1 ? 2 : 0)) + (this.URLFlag == 1 ? this.URLLength + 1 : 0)) + (this.oCRstreamFlag == 1 ? 2 : 0);
        int begin = bb.position();
        if (getSize() > baseSize + 2) {
            descriptor = ObjectDescriptorFactory.createFrom(-1, bb);
            read = (long) (bb.position() - begin);
            log.finer(descriptor + " - ESDescriptor1 read: " + read + ", size: " + (descriptor != null ? Integer.valueOf(descriptor.getSize()) : null));
            if (descriptor != null) {
                int size = descriptor.getSize();
                bb.position(begin + size);
                baseSize += size;
            } else {
                baseSize = (int) (((long) baseSize) + read);
            }
            if (descriptor instanceof DecoderConfigDescriptor) {
                this.decoderConfigDescriptor = (DecoderConfigDescriptor) descriptor;
            }
        }
        begin = bb.position();
        if (getSize() > baseSize + 2) {
            descriptor = ObjectDescriptorFactory.createFrom(-1, bb);
            read = (long) (bb.position() - begin);
            log.finer(descriptor + " - ESDescriptor2 read: " + read + ", size: " + (descriptor != null ? Integer.valueOf(descriptor.getSize()) : null));
            if (descriptor != null) {
                size = descriptor.getSize();
                bb.position(begin + size);
                baseSize += size;
            } else {
                baseSize = (int) (((long) baseSize) + read);
            }
            if (descriptor instanceof SLConfigDescriptor) {
                this.slConfigDescriptor = (SLConfigDescriptor) descriptor;
            }
        } else {
            log.warning("SLConfigDescriptor is missing!");
        }
        while (getSize() - baseSize > 2) {
            begin = bb.position();
            descriptor = ObjectDescriptorFactory.createFrom(-1, bb);
            read = (long) (bb.position() - begin);
            log.finer(descriptor + " - ESDescriptor3 read: " + read + ", size: " + (descriptor != null ? Integer.valueOf(descriptor.getSize()) : null));
            if (descriptor != null) {
                size = descriptor.getSize();
                bb.position(begin + size);
                baseSize += size;
            } else {
                baseSize = (int) (((long) baseSize) + read);
            }
            this.otherDescriptors.add(descriptor);
        }
    }

    public int serializedSize() {
        int out = 5;
        if (this.streamDependenceFlag > 0) {
            out = 5 + 2;
        }
        if (this.URLFlag > 0) {
            out += this.URLLength + 1;
        }
        if (this.oCRstreamFlag > 0) {
            out += 2;
        }
        return (out + this.decoderConfigDescriptor.serializedSize()) + this.slConfigDescriptor.serializedSize();
    }

    public ByteBuffer serialize() {
        ByteBuffer out = ByteBuffer.allocate(serializedSize());
        IsoTypeWriter.writeUInt8(out, 3);
        IsoTypeWriter.writeUInt8(out, serializedSize() - 2);
        IsoTypeWriter.writeUInt16(out, this.esId);
        IsoTypeWriter.writeUInt8(out, (((this.streamDependenceFlag << 7) | (this.URLFlag << 6)) | (this.oCRstreamFlag << 5)) | (this.streamPriority & 31));
        if (this.streamDependenceFlag > 0) {
            IsoTypeWriter.writeUInt16(out, this.dependsOnEsId);
        }
        if (this.URLFlag > 0) {
            IsoTypeWriter.writeUInt8(out, this.URLLength);
            IsoTypeWriter.writeUtf8String(out, this.URLString);
        }
        if (this.oCRstreamFlag > 0) {
            IsoTypeWriter.writeUInt16(out, this.oCREsId);
        }
        ByteBuffer dec = this.decoderConfigDescriptor.serialize();
        ByteBuffer sl = this.slConfigDescriptor.serialize();
        out.put(dec.array());
        out.put(sl.array());
        return out;
    }

    public DecoderConfigDescriptor getDecoderConfigDescriptor() {
        return this.decoderConfigDescriptor;
    }

    public SLConfigDescriptor getSlConfigDescriptor() {
        return this.slConfigDescriptor;
    }

    public void setDecoderConfigDescriptor(DecoderConfigDescriptor decoderConfigDescriptor) {
        this.decoderConfigDescriptor = decoderConfigDescriptor;
    }

    public void setSlConfigDescriptor(SLConfigDescriptor slConfigDescriptor) {
        this.slConfigDescriptor = slConfigDescriptor;
    }

    public List<BaseDescriptor> getOtherDescriptors() {
        return this.otherDescriptors;
    }

    public int getoCREsId() {
        return this.oCREsId;
    }

    public void setoCREsId(int oCREsId) {
        this.oCREsId = oCREsId;
    }

    public int getEsId() {
        return this.esId;
    }

    public void setEsId(int esId) {
        this.esId = esId;
    }

    public int getStreamDependenceFlag() {
        return this.streamDependenceFlag;
    }

    public void setStreamDependenceFlag(int streamDependenceFlag) {
        this.streamDependenceFlag = streamDependenceFlag;
    }

    public int getURLFlag() {
        return this.URLFlag;
    }

    public void setURLFlag(int URLFlag) {
        this.URLFlag = URLFlag;
    }

    public int getoCRstreamFlag() {
        return this.oCRstreamFlag;
    }

    public void setoCRstreamFlag(int oCRstreamFlag) {
        this.oCRstreamFlag = oCRstreamFlag;
    }

    public int getStreamPriority() {
        return this.streamPriority;
    }

    public void setStreamPriority(int streamPriority) {
        this.streamPriority = streamPriority;
    }

    public int getURLLength() {
        return this.URLLength;
    }

    public void setURLLength(int URLLength) {
        this.URLLength = URLLength;
    }

    public String getURLString() {
        return this.URLString;
    }

    public void setURLString(String URLString) {
        this.URLString = URLString;
    }

    public int getRemoteODFlag() {
        return this.remoteODFlag;
    }

    public void setRemoteODFlag(int remoteODFlag) {
        this.remoteODFlag = remoteODFlag;
    }

    public int getDependsOnEsId() {
        return this.dependsOnEsId;
    }

    public void setDependsOnEsId(int dependsOnEsId) {
        this.dependsOnEsId = dependsOnEsId;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ESDescriptor");
        sb.append("{esId=").append(this.esId);
        sb.append(", streamDependenceFlag=").append(this.streamDependenceFlag);
        sb.append(", URLFlag=").append(this.URLFlag);
        sb.append(", oCRstreamFlag=").append(this.oCRstreamFlag);
        sb.append(", streamPriority=").append(this.streamPriority);
        sb.append(", URLLength=").append(this.URLLength);
        sb.append(", URLString='").append(this.URLString).append('\'');
        sb.append(", remoteODFlag=").append(this.remoteODFlag);
        sb.append(", dependsOnEsId=").append(this.dependsOnEsId);
        sb.append(", oCREsId=").append(this.oCREsId);
        sb.append(", decoderConfigDescriptor=").append(this.decoderConfigDescriptor);
        sb.append(", slConfigDescriptor=").append(this.slConfigDescriptor);
        sb.append('}');
        return sb.toString();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ESDescriptor that = (ESDescriptor) o;
        if (this.URLFlag != that.URLFlag) {
            return false;
        }
        if (this.URLLength != that.URLLength) {
            return false;
        }
        if (this.dependsOnEsId != that.dependsOnEsId) {
            return false;
        }
        if (this.esId != that.esId) {
            return false;
        }
        if (this.oCREsId != that.oCREsId) {
            return false;
        }
        if (this.oCRstreamFlag != that.oCRstreamFlag) {
            return false;
        }
        if (this.remoteODFlag != that.remoteODFlag) {
            return false;
        }
        if (this.streamDependenceFlag != that.streamDependenceFlag) {
            return false;
        }
        if (this.streamPriority != that.streamPriority) {
            return false;
        }
        if (this.URLString == null ? that.URLString != null : !this.URLString.equals(that.URLString)) {
            return false;
        }
        if (this.decoderConfigDescriptor == null ? that.decoderConfigDescriptor != null : !this.decoderConfigDescriptor.equals(that.decoderConfigDescriptor)) {
            return false;
        }
        if (this.otherDescriptors == null ? that.otherDescriptors != null : !this.otherDescriptors.equals(that.otherDescriptors)) {
            return false;
        }
        if (this.slConfigDescriptor != null) {
            if (this.slConfigDescriptor.equals(that.slConfigDescriptor)) {
                return true;
            }
        } else if (that.slConfigDescriptor == null) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        int hashCode;
        int i = 0;
        int hashCode2 = ((((((((((((((((((this.esId * 31) + this.streamDependenceFlag) * 31) + this.URLFlag) * 31) + this.oCRstreamFlag) * 31) + this.streamPriority) * 31) + this.URLLength) * 31) + (this.URLString != null ? this.URLString.hashCode() : 0)) * 31) + this.remoteODFlag) * 31) + this.dependsOnEsId) * 31) + this.oCREsId) * 31;
        if (this.decoderConfigDescriptor != null) {
            hashCode = this.decoderConfigDescriptor.hashCode();
        } else {
            hashCode = 0;
        }
        hashCode2 = (hashCode2 + hashCode) * 31;
        if (this.slConfigDescriptor != null) {
            hashCode = this.slConfigDescriptor.hashCode();
        } else {
            hashCode = 0;
        }
        hashCode = (hashCode2 + hashCode) * 31;
        if (this.otherDescriptors != null) {
            i = this.otherDescriptors.hashCode();
        }
        return hashCode + i;
    }
}
