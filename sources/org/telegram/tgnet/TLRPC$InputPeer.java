package org.telegram.tgnet;
/* loaded from: classes.dex */
public abstract class TLRPC$InputPeer extends TLObject {
    public long access_hash;
    public long channel_id;
    public long chat_id;
    public int msg_id;
    public TLRPC$InputPeer peer;
    public long user_id;

    public static TLRPC$InputPeer TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$InputPeer tLRPC$InputPeer;
        switch (i) {
            case -1667893317:
                tLRPC$InputPeer = new TLRPC$TL_inputPeerChannelFromMessage() { // from class: org.telegram.tgnet.TLRPC$TL_inputPeerChannelFromMessage_layer131
                    public static int constructor = -NUM;

                    @Override // org.telegram.tgnet.TLRPC$TL_inputPeerChannelFromMessage, org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        this.peer = TLRPC$InputPeer.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        this.msg_id = abstractSerializedData2.readInt32(z2);
                        this.channel_id = abstractSerializedData2.readInt32(z2);
                    }

                    @Override // org.telegram.tgnet.TLRPC$TL_inputPeerChannelFromMessage, org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        this.peer.serializeToStream(abstractSerializedData2);
                        abstractSerializedData2.writeInt32(this.msg_id);
                        abstractSerializedData2.writeInt32((int) this.channel_id);
                    }
                };
                break;
            case -1468331492:
                tLRPC$InputPeer = new TLRPC$TL_inputPeerUserFromMessage();
                break;
            case -1121318848:
                tLRPC$InputPeer = new TLRPC$TL_inputPeerChannelFromMessage();
                break;
            case -571955892:
                tLRPC$InputPeer = new TLRPC$TL_inputPeerUser();
                break;
            case 396093539:
                tLRPC$InputPeer = new TLRPC$TL_inputPeerChat() { // from class: org.telegram.tgnet.TLRPC$TL_inputPeerChat_layer131
                    public static int constructor = NUM;

                    @Override // org.telegram.tgnet.TLRPC$TL_inputPeerChat, org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        this.chat_id = abstractSerializedData2.readInt32(z2);
                    }

                    @Override // org.telegram.tgnet.TLRPC$TL_inputPeerChat, org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        abstractSerializedData2.writeInt32((int) this.chat_id);
                    }
                };
                break;
            case 398123750:
                tLRPC$InputPeer = new TLRPC$TL_inputPeerUserFromMessage() { // from class: org.telegram.tgnet.TLRPC$TL_inputPeerUserFromMessage_layer131
                    public static int constructor = NUM;

                    @Override // org.telegram.tgnet.TLRPC$TL_inputPeerUserFromMessage, org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        this.peer = TLRPC$InputPeer.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        this.msg_id = abstractSerializedData2.readInt32(z2);
                        this.user_id = abstractSerializedData2.readInt32(z2);
                    }

                    @Override // org.telegram.tgnet.TLRPC$TL_inputPeerUserFromMessage, org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        this.peer.serializeToStream(abstractSerializedData2);
                        abstractSerializedData2.writeInt32(this.msg_id);
                        abstractSerializedData2.writeInt32((int) this.user_id);
                    }
                };
                break;
            case 548253432:
                tLRPC$InputPeer = new TLRPC$TL_inputPeerChannel() { // from class: org.telegram.tgnet.TLRPC$TL_inputPeerChannel_layer131
                    public static int constructor = NUM;

                    @Override // org.telegram.tgnet.TLRPC$TL_inputPeerChannel, org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        this.channel_id = abstractSerializedData2.readInt32(z2);
                        this.access_hash = abstractSerializedData2.readInt64(z2);
                    }

                    @Override // org.telegram.tgnet.TLRPC$TL_inputPeerChannel, org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        abstractSerializedData2.writeInt32((int) this.channel_id);
                        abstractSerializedData2.writeInt64(this.access_hash);
                    }
                };
                break;
            case 666680316:
                tLRPC$InputPeer = new TLRPC$TL_inputPeerChannel();
                break;
            case 900291769:
                tLRPC$InputPeer = new TLRPC$TL_inputPeerChat();
                break;
            case 2072935910:
                tLRPC$InputPeer = new TLRPC$TL_inputPeerUser() { // from class: org.telegram.tgnet.TLRPC$TL_inputPeerUser_layer131
                    public static int constructor = NUM;

                    @Override // org.telegram.tgnet.TLRPC$TL_inputPeerUser, org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        this.user_id = abstractSerializedData2.readInt32(z2);
                        this.access_hash = abstractSerializedData2.readInt64(z2);
                    }

                    @Override // org.telegram.tgnet.TLRPC$TL_inputPeerUser, org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        abstractSerializedData2.writeInt32((int) this.user_id);
                        abstractSerializedData2.writeInt64(this.access_hash);
                    }
                };
                break;
            case 2107670217:
                tLRPC$InputPeer = new TLRPC$TL_inputPeerSelf();
                break;
            case 2134579434:
                tLRPC$InputPeer = new TLRPC$TL_inputPeerEmpty();
                break;
            default:
                tLRPC$InputPeer = null;
                break;
        }
        if (tLRPC$InputPeer != null || !z) {
            if (tLRPC$InputPeer != null) {
                tLRPC$InputPeer.readParams(abstractSerializedData, z);
            }
            return tLRPC$InputPeer;
        }
        throw new RuntimeException(String.format("can't parse magic %x in InputPeer", Integer.valueOf(i)));
    }
}
