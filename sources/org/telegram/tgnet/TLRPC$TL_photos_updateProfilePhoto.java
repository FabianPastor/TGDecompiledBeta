package org.telegram.tgnet;

public class TLRPC$TL_photos_updateProfilePhoto extends TLObject {
    public static int constructor = -NUM;
    public TLRPC$InputPhoto id;

    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$UserProfilePhoto.TLdeserialize(abstractSerializedData, i, z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.id.serializeToStream(abstractSerializedData);
    }
}
