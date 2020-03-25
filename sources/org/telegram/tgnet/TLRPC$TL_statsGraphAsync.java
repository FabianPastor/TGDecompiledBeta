package org.telegram.tgnet;

public class TLRPC$TL_statsGraphAsync extends TLRPC$StatsGraph {
    public static int constructor = NUM;
    public String token;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.token = abstractSerializedData.readString(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeString(this.token);
    }
}
