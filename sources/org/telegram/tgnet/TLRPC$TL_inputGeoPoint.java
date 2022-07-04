package org.telegram.tgnet;

public class TLRPC$TL_inputGeoPoint extends TLRPC$InputGeoPoint {
    public static int constructor = NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.flags = abstractSerializedData.readInt32(z);
        this.lat = abstractSerializedData.readDouble(z);
        this._long = abstractSerializedData.readDouble(z);
        if ((this.flags & 1) != 0) {
            this.accuracy_radius = abstractSerializedData.readInt32(z);
        }
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.flags);
        abstractSerializedData.writeDouble(this.lat);
        abstractSerializedData.writeDouble(this._long);
        if ((this.flags & 1) != 0) {
            abstractSerializedData.writeInt32(this.accuracy_radius);
        }
    }
}
