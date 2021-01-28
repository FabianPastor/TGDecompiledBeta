package org.telegram.tgnet;

public class TLRPC$TL_channelLocation extends TLRPC$ChannelLocation {
    public static int constructor = NUM;
    public String address;
    public TLRPC$GeoPoint geo_point;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.geo_point = TLRPC$GeoPoint.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        this.address = abstractSerializedData.readString(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.geo_point.serializeToStream(abstractSerializedData);
        abstractSerializedData.writeString(this.address);
    }
}
