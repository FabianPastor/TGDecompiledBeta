package org.telegram.tgnet;

public abstract class TLRPC$upload_CdnFile extends TLObject {
    public NativeByteBuffer bytes;
    public byte[] request_token;

    public static TLRPC$upload_CdnFile TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$upload_CdnFile tLRPC$upload_CdnFile;
        if (i != -NUM) {
            tLRPC$upload_CdnFile = i != -NUM ? null : new TLRPC$TL_upload_cdnFileReuploadNeeded();
        } else {
            tLRPC$upload_CdnFile = new TLRPC$TL_upload_cdnFile();
        }
        if (tLRPC$upload_CdnFile != null || !z) {
            if (tLRPC$upload_CdnFile != null) {
                tLRPC$upload_CdnFile.readParams(abstractSerializedData, z);
            }
            return tLRPC$upload_CdnFile;
        }
        throw new RuntimeException(String.format("can't parse magic %x in upload_CdnFile", new Object[]{Integer.valueOf(i)}));
    }
}
