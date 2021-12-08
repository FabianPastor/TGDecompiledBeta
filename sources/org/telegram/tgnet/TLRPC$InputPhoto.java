package org.telegram.tgnet;

public abstract class TLRPC$InputPhoto extends TLObject {
    public long access_hash;
    public byte[] file_reference;
    public long id;

    public static TLRPC$InputPhoto TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$InputPhoto tLRPC$InputPhoto;
        if (i != NUM) {
            tLRPC$InputPhoto = i != NUM ? null : new TLRPC$TL_inputPhoto();
        } else {
            tLRPC$InputPhoto = new TLRPC$TL_inputPhotoEmpty();
        }
        if (tLRPC$InputPhoto != null || !z) {
            if (tLRPC$InputPhoto != null) {
                tLRPC$InputPhoto.readParams(abstractSerializedData, z);
            }
            return tLRPC$InputPhoto;
        }
        throw new RuntimeException(String.format("can't parse magic %x in InputPhoto", new Object[]{Integer.valueOf(i)}));
    }
}
