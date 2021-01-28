package org.telegram.tgnet;

public abstract class TLRPC$InputUser extends TLObject {
    public long access_hash;
    public int user_id;

    public static TLRPC$InputUser TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$InputUser tLRPC$InputUser;
        switch (i) {
            case -1182234929:
                tLRPC$InputUser = new TLRPC$TL_inputUserEmpty();
                break;
            case -668391402:
                tLRPC$InputUser = new TLRPC$TL_inputUser();
                break;
            case -138301121:
                tLRPC$InputUser = new TLRPC$TL_inputUserSelf();
                break;
            case 756118935:
                tLRPC$InputUser = new TLRPC$TL_inputUserFromMessage();
                break;
            default:
                tLRPC$InputUser = null;
                break;
        }
        if (tLRPC$InputUser != null || !z) {
            if (tLRPC$InputUser != null) {
                tLRPC$InputUser.readParams(abstractSerializedData, z);
            }
            return tLRPC$InputUser;
        }
        throw new RuntimeException(String.format("can't parse magic %x in InputUser", new Object[]{Integer.valueOf(i)}));
    }
}
