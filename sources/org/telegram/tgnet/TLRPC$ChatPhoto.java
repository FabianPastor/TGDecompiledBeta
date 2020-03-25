package org.telegram.tgnet;

public abstract class TLRPC$ChatPhoto extends TLObject {
    public int dc_id;
    public TLRPC$FileLocation photo_big;
    public TLRPC$FileLocation photo_small;

    public static TLRPC$ChatPhoto TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$ChatPhoto tLRPC$ChatPhoto;
        if (i != NUM) {
            tLRPC$ChatPhoto = i != NUM ? i != NUM ? null : new TLRPC$TL_chatPhoto_layer97() : new TLRPC$TL_chatPhoto();
        } else {
            tLRPC$ChatPhoto = new TLRPC$TL_chatPhotoEmpty();
        }
        if (tLRPC$ChatPhoto != null || !z) {
            if (tLRPC$ChatPhoto != null) {
                tLRPC$ChatPhoto.readParams(abstractSerializedData, z);
            }
            return tLRPC$ChatPhoto;
        }
        throw new RuntimeException(String.format("can't parse magic %x in ChatPhoto", new Object[]{Integer.valueOf(i)}));
    }
}
