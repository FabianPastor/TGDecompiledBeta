package org.telegram.tgnet;

public class TLRPC$TL_geoPointEmpty extends TLRPC$GeoPoint {
    public static int constructor = NUM;

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
    }
}
