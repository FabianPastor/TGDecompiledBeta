package org.telegram.tgnet;

public class TLRPC$TL_messages_acceptEncryption extends TLObject {
    public static int constructor = NUM;
    public byte[] g_b;
    public long key_fingerprint;
    public TLRPC$TL_inputEncryptedChat peer;

    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$EncryptedChat.TLdeserialize(abstractSerializedData, i, z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.peer.serializeToStream(abstractSerializedData);
        abstractSerializedData.writeByteArray(this.g_b);
        abstractSerializedData.writeInt64(this.key_fingerprint);
    }
}
