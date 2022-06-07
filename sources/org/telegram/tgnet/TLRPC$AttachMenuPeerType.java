package org.telegram.tgnet;

public abstract class TLRPC$AttachMenuPeerType extends TLObject {
    public static TLRPC$AttachMenuPeerType TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$AttachMenuPeerType tLRPC$AttachMenuPeerType;
        switch (i) {
            case -1020528102:
                tLRPC$AttachMenuPeerType = new TLRPC$TL_attachMenuPeerTypeBotPM();
                break;
            case -247016673:
                tLRPC$AttachMenuPeerType = new TLRPC$TL_attachMenuPeerTypePM();
                break;
            case 84480319:
                tLRPC$AttachMenuPeerType = new TLRPC$TL_attachMenuPeerTypeChat();
                break;
            case 2080104188:
                tLRPC$AttachMenuPeerType = new TLRPC$TL_attachMenuPeerTypeBroadcast();
                break;
            case 2104224014:
                tLRPC$AttachMenuPeerType = new TLRPC$TL_attachMenuPeerTypeSameBotPM();
                break;
            default:
                tLRPC$AttachMenuPeerType = null;
                break;
        }
        if (tLRPC$AttachMenuPeerType != null || !z) {
            if (tLRPC$AttachMenuPeerType != null) {
                tLRPC$AttachMenuPeerType.readParams(abstractSerializedData, z);
            }
            return tLRPC$AttachMenuPeerType;
        }
        throw new RuntimeException(String.format("can't parse magic %x in AttachMenuPeerType", new Object[]{Integer.valueOf(i)}));
    }
}
