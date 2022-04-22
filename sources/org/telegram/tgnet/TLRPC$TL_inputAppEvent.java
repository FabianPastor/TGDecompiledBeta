package org.telegram.tgnet;

public class TLRPC$TL_inputAppEvent extends TLObject {
    public static int constructor = NUM;
    public TLRPC$JSONValue data;
    public long peer;
    public double time;
    public String type;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.time = abstractSerializedData.readDouble(z);
        this.type = abstractSerializedData.readString(z);
        this.peer = abstractSerializedData.readInt64(z);
        this.data = TLRPC$JSONValue.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeDouble(this.time);
        abstractSerializedData.writeString(this.type);
        abstractSerializedData.writeInt64(this.peer);
        this.data.serializeToStream(abstractSerializedData);
    }
}
