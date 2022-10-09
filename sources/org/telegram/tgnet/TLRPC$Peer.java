package org.telegram.tgnet;
/* loaded from: classes.dex */
public abstract class TLRPC$Peer extends TLObject {
    public long channel_id;
    public long chat_id;
    public long user_id;

    public static TLRPC$Peer TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$Peer tLRPC$Peer;
        switch (i) {
            case -1649296275:
                tLRPC$Peer = new TLRPC$TL_peerUser() { // from class: org.telegram.tgnet.TLRPC$TL_peerUser_layer131
                    public static int constructor = -NUM;

                    @Override // org.telegram.tgnet.TLRPC$TL_peerUser, org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        this.user_id = abstractSerializedData2.readInt32(z2);
                    }

                    @Override // org.telegram.tgnet.TLRPC$TL_peerUser, org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        abstractSerializedData2.writeInt32((int) this.user_id);
                    }
                };
                break;
            case -1566230754:
                tLRPC$Peer = new TLRPC$TL_peerChannel();
                break;
            case -1160714821:
                tLRPC$Peer = new TLRPC$TL_peerChat() { // from class: org.telegram.tgnet.TLRPC$TL_peerChat_layer131
                    public static int constructor = -NUM;

                    @Override // org.telegram.tgnet.TLRPC$TL_peerChat, org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        this.chat_id = abstractSerializedData2.readInt32(z2);
                    }

                    @Override // org.telegram.tgnet.TLRPC$TL_peerChat, org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        abstractSerializedData2.writeInt32((int) this.chat_id);
                    }
                };
                break;
            case -1109531342:
                tLRPC$Peer = new TLRPC$TL_peerChannel() { // from class: org.telegram.tgnet.TLRPC$TL_peerChannel_layer131
                    public static int constructor = -NUM;

                    @Override // org.telegram.tgnet.TLRPC$TL_peerChannel, org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        this.channel_id = abstractSerializedData2.readInt32(z2);
                    }

                    @Override // org.telegram.tgnet.TLRPC$TL_peerChannel, org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        abstractSerializedData2.writeInt32((int) this.channel_id);
                    }
                };
                break;
            case 918946202:
                tLRPC$Peer = new TLRPC$TL_peerChat();
                break;
            case 1498486562:
                tLRPC$Peer = new TLRPC$TL_peerUser();
                break;
            default:
                tLRPC$Peer = null;
                break;
        }
        if (tLRPC$Peer != null || !z) {
            if (tLRPC$Peer != null) {
                tLRPC$Peer.readParams(abstractSerializedData, z);
            }
            return tLRPC$Peer;
        }
        throw new RuntimeException(String.format("can't parse magic %x in Peer", Integer.valueOf(i)));
    }
}
