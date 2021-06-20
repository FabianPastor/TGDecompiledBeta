package org.telegram.tgnet;

public class TLRPC$TL_chatPhoto extends TLRPC$ChatPhoto {
    public static int constructor = NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        this.flags = readInt32;
        boolean z2 = true;
        if ((readInt32 & 1) == 0) {
            z2 = false;
        }
        this.has_video = z2;
        this.photo_id = abstractSerializedData.readInt64(z);
        if ((this.flags & 2) != 0) {
            this.stripped_thumb = abstractSerializedData.readByteArray(z);
        }
        this.dc_id = abstractSerializedData.readInt32(z);
        TLRPC$TL_fileLocationToBeDeprecated tLRPC$TL_fileLocationToBeDeprecated = new TLRPC$TL_fileLocationToBeDeprecated();
        this.photo_small = tLRPC$TL_fileLocationToBeDeprecated;
        tLRPC$TL_fileLocationToBeDeprecated.volume_id = -this.photo_id;
        tLRPC$TL_fileLocationToBeDeprecated.local_id = 97;
        TLRPC$TL_fileLocationToBeDeprecated tLRPC$TL_fileLocationToBeDeprecated2 = new TLRPC$TL_fileLocationToBeDeprecated();
        this.photo_big = tLRPC$TL_fileLocationToBeDeprecated2;
        tLRPC$TL_fileLocationToBeDeprecated2.volume_id = -this.photo_id;
        tLRPC$TL_fileLocationToBeDeprecated2.local_id = 99;
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        int i = this.has_video ? this.flags | 1 : this.flags & -2;
        this.flags = i;
        abstractSerializedData.writeInt32(i);
        abstractSerializedData.writeInt64(this.photo_id);
        if ((this.flags & 2) != 0) {
            abstractSerializedData.writeByteArray(this.stripped_thumb);
        }
        abstractSerializedData.writeInt32(this.dc_id);
    }
}
