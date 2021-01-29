package org.telegram.tgnet;

public class TLRPC$TL_userProfilePhoto_layer115 extends TLRPC$TL_userProfilePhoto {
    public static int constructor = -NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.photo_id = abstractSerializedData.readInt64(z);
        this.photo_small = TLRPC$FileLocation.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        this.photo_big = TLRPC$FileLocation.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        this.dc_id = abstractSerializedData.readInt32(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt64(this.photo_id);
        this.photo_small.serializeToStream(abstractSerializedData);
        this.photo_big.serializeToStream(abstractSerializedData);
        abstractSerializedData.writeInt32(this.dc_id);
    }
}
