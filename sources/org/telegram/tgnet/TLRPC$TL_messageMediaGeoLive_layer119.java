package org.telegram.tgnet;

public class TLRPC$TL_messageMediaGeoLive_layer119 extends TLRPC$TL_messageMediaGeoLive {
    public static int constructor = NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.geo = TLRPC$GeoPoint.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        this.period = abstractSerializedData.readInt32(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.geo.serializeToStream(abstractSerializedData);
        abstractSerializedData.writeInt32(this.period);
    }
}
