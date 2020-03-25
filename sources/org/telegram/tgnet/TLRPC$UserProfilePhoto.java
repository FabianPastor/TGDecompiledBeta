package org.telegram.tgnet;

public abstract class TLRPC$UserProfilePhoto extends TLObject {
    public int dc_id;
    public TLRPC$FileLocation photo_big;
    public long photo_id;
    public TLRPC$FileLocation photo_small;

    public static TLRPC$UserProfilePhoto TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$UserProfilePhoto tLRPC$UserProfilePhoto;
        switch (i) {
            case -1727196013:
                tLRPC$UserProfilePhoto = new TLRPC$TL_userProfilePhoto_old();
                break;
            case -715532088:
                tLRPC$UserProfilePhoto = new TLRPC$TL_userProfilePhoto_layer97();
                break;
            case -321430132:
                tLRPC$UserProfilePhoto = new TLRPC$TL_userProfilePhoto();
                break;
            case 1326562017:
                tLRPC$UserProfilePhoto = new TLRPC$TL_userProfilePhotoEmpty();
                break;
            default:
                tLRPC$UserProfilePhoto = null;
                break;
        }
        if (tLRPC$UserProfilePhoto != null || !z) {
            if (tLRPC$UserProfilePhoto != null) {
                tLRPC$UserProfilePhoto.readParams(abstractSerializedData, z);
            }
            return tLRPC$UserProfilePhoto;
        }
        throw new RuntimeException(String.format("can't parse magic %x in UserProfilePhoto", new Object[]{Integer.valueOf(i)}));
    }
}
