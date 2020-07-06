package org.telegram.tgnet;

public abstract class TLRPC$ChatPhoto extends TLObject {
    public int dc_id;
    public int flags;
    public boolean has_video;
    public TLRPC$FileLocation photo_big;
    public TLRPC$FileLocation photo_small;

    public static TLRPC$ChatPhoto TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$ChatPhoto tLRPC$ChatPhoto;
        switch (i) {
            case -770990276:
                tLRPC$ChatPhoto = new TLRPC$TL_chatPhoto();
                break;
            case 935395612:
                tLRPC$ChatPhoto = new TLRPC$TL_chatPhotoEmpty();
                break;
            case 1197267925:
                tLRPC$ChatPhoto = new TLRPC$TL_chatPhoto_layer115();
                break;
            case 1632839530:
                tLRPC$ChatPhoto = new TLRPC$TL_chatPhoto_layer97();
                break;
            default:
                tLRPC$ChatPhoto = null;
                break;
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
