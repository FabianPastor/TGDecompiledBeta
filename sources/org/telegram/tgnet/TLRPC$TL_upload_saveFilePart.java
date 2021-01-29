package org.telegram.tgnet;

public class TLRPC$TL_upload_saveFilePart extends TLObject {
    public static int constructor = -NUM;
    public NativeByteBuffer bytes;
    public long file_id;
    public int file_part;

    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$Bool.TLdeserialize(abstractSerializedData, i, z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt64(this.file_id);
        abstractSerializedData.writeInt32(this.file_part);
        abstractSerializedData.writeByteBuffer(this.bytes);
    }

    public void freeResources() {
        NativeByteBuffer nativeByteBuffer;
        if (!this.disableFree && (nativeByteBuffer = this.bytes) != null) {
            nativeByteBuffer.reuse();
            this.bytes = null;
        }
    }
}
