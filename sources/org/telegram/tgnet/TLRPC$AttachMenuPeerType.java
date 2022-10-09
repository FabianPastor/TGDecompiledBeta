package org.telegram.tgnet;
/* loaded from: classes.dex */
public abstract class TLRPC$AttachMenuPeerType extends TLObject {
    public static TLRPC$AttachMenuPeerType TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$AttachMenuPeerType tLRPC$AttachMenuPeerType;
        switch (i) {
            case -1020528102:
                tLRPC$AttachMenuPeerType = new TLRPC$AttachMenuPeerType() { // from class: org.telegram.tgnet.TLRPC$TL_attachMenuPeerTypeBotPM
                    public static int constructor = -NUM;

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                    }
                };
                break;
            case -247016673:
                tLRPC$AttachMenuPeerType = new TLRPC$AttachMenuPeerType() { // from class: org.telegram.tgnet.TLRPC$TL_attachMenuPeerTypePM
                    public static int constructor = -NUM;

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                    }
                };
                break;
            case 84480319:
                tLRPC$AttachMenuPeerType = new TLRPC$AttachMenuPeerType() { // from class: org.telegram.tgnet.TLRPC$TL_attachMenuPeerTypeChat
                    public static int constructor = 84480319;

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                    }
                };
                break;
            case 2080104188:
                tLRPC$AttachMenuPeerType = new TLRPC$AttachMenuPeerType() { // from class: org.telegram.tgnet.TLRPC$TL_attachMenuPeerTypeBroadcast
                    public static int constructor = NUM;

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                    }
                };
                break;
            case 2104224014:
                tLRPC$AttachMenuPeerType = new TLRPC$AttachMenuPeerType() { // from class: org.telegram.tgnet.TLRPC$TL_attachMenuPeerTypeSameBotPM
                    public static int constructor = NUM;

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                    }
                };
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
        throw new RuntimeException(String.format("can't parse magic %x in AttachMenuPeerType", Integer.valueOf(i)));
    }
}
