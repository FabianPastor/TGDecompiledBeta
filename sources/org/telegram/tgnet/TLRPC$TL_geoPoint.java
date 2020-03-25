package org.telegram.tgnet;

public class TLRPC$TL_geoPoint extends TLRPC$GeoPoint {
    public static int constructor = 43446532;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this._long = abstractSerializedData.readDouble(z);
        this.lat = abstractSerializedData.readDouble(z);
        this.access_hash = abstractSerializedData.readInt64(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeDouble(this._long);
        abstractSerializedData.writeDouble(this.lat);
        abstractSerializedData.writeInt64(this.access_hash);
    }
}
