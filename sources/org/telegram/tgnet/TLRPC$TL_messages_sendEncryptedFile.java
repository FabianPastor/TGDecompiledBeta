package org.telegram.tgnet;

public class TLRPC$TL_messages_sendEncryptedFile extends TLObject {
    public static int constructor = -NUM;
    public NativeByteBuffer data;
    public TLRPC$InputEncryptedFile file;
    public TLRPC$TL_inputEncryptedChat peer;
    public long random_id;

    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$messages_SentEncryptedMessage.TLdeserialize(abstractSerializedData, i, z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.peer.serializeToStream(abstractSerializedData);
        abstractSerializedData.writeInt64(this.random_id);
        abstractSerializedData.writeByteBuffer(this.data);
        this.file.serializeToStream(abstractSerializedData);
    }

    public void freeResources() {
        NativeByteBuffer nativeByteBuffer = this.data;
        if (nativeByteBuffer != null) {
            nativeByteBuffer.reuse();
            this.data = null;
        }
    }
}
