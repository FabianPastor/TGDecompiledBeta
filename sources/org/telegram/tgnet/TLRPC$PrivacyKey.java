package org.telegram.tgnet;

public abstract class TLRPC$PrivacyKey extends TLObject {
    public static TLRPC$PrivacyKey TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$PrivacyKey tLRPC$PrivacyKey;
        switch (i) {
            case -1777000467:
                tLRPC$PrivacyKey = new TLRPC$TL_privacyKeyProfilePhoto();
                break;
            case -1137792208:
                tLRPC$PrivacyKey = new TLRPC$TL_privacyKeyStatusTimestamp();
                break;
            case -778378131:
                tLRPC$PrivacyKey = new TLRPC$TL_privacyKeyPhoneNumber();
                break;
            case 961092808:
                tLRPC$PrivacyKey = new TLRPC$TL_privacyKeyPhoneP2P();
                break;
            case 1030105979:
                tLRPC$PrivacyKey = new TLRPC$TL_privacyKeyPhoneCall();
                break;
            case 1124062251:
                tLRPC$PrivacyKey = new TLRPC$TL_privacyKeyAddedByPhone();
                break;
            case 1343122938:
                tLRPC$PrivacyKey = new TLRPC$TL_privacyKeyChatInvite();
                break;
            case 1777096355:
                tLRPC$PrivacyKey = new TLRPC$TL_privacyKeyForwards();
                break;
            default:
                tLRPC$PrivacyKey = null;
                break;
        }
        if (tLRPC$PrivacyKey != null || !z) {
            if (tLRPC$PrivacyKey != null) {
                tLRPC$PrivacyKey.readParams(abstractSerializedData, z);
            }
            return tLRPC$PrivacyKey;
        }
        throw new RuntimeException(String.format("can't parse magic %x in PrivacyKey", new Object[]{Integer.valueOf(i)}));
    }
}
