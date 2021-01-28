package org.telegram.tgnet;

public class TLRPC$TL_maskCoords extends TLObject {
    public static int constructor = -NUM;
    public int n;
    public double x;
    public double y;
    public double zoom;

    public static TLRPC$TL_maskCoords TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        if (constructor == i) {
            TLRPC$TL_maskCoords tLRPC$TL_maskCoords = new TLRPC$TL_maskCoords();
            tLRPC$TL_maskCoords.readParams(abstractSerializedData, z);
            return tLRPC$TL_maskCoords;
        } else if (!z) {
            return null;
        } else {
            throw new RuntimeException(String.format("can't parse magic %x in TL_maskCoords", new Object[]{Integer.valueOf(i)}));
        }
    }

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.n = abstractSerializedData.readInt32(z);
        this.x = abstractSerializedData.readDouble(z);
        this.y = abstractSerializedData.readDouble(z);
        this.zoom = abstractSerializedData.readDouble(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.n);
        abstractSerializedData.writeDouble(this.x);
        abstractSerializedData.writeDouble(this.y);
        abstractSerializedData.writeDouble(this.zoom);
    }
}
