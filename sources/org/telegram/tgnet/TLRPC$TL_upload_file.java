package org.telegram.tgnet;

public class TLRPC$TL_upload_file extends TLRPC$upload_File {
    public static int constructor = NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.type = TLRPC$storage_FileType.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        this.mtime = abstractSerializedData.readInt32(z);
        this.bytes = abstractSerializedData.readByteBuffer(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.type.serializeToStream(abstractSerializedData);
        abstractSerializedData.writeInt32(this.mtime);
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
