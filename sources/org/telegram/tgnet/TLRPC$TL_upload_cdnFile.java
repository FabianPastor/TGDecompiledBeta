package org.telegram.tgnet;

public class TLRPC$TL_upload_cdnFile extends TLRPC$upload_CdnFile {
    public static int constructor = -NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.bytes = abstractSerializedData.readByteBuffer(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
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
