package org.telegram.tgnet;

public class TLRPC$TL_messageActionUserUpdatedPhoto extends TLRPC$MessageAction {
    public static int constructor = NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.newUserPhoto = TLRPC$UserProfilePhoto.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.newUserPhoto.serializeToStream(abstractSerializedData);
    }
}
