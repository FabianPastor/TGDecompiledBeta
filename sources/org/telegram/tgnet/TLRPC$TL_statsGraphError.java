package org.telegram.tgnet;

public class TLRPC$TL_statsGraphError extends TLRPC$StatsGraph {
    public static int constructor = -NUM;
    public String error;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.error = abstractSerializedData.readString(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeString(this.error);
    }
}
