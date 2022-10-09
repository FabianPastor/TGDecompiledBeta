package org.telegram.tgnet;
/* loaded from: classes.dex */
public abstract class TLRPC$upload_CdnFile extends TLObject {
    public NativeByteBuffer bytes;
    public byte[] request_token;

    public static TLRPC$upload_CdnFile TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$upload_CdnFile tLRPC$TL_upload_cdnFile;
        if (i == -NUM) {
            tLRPC$TL_upload_cdnFile = new TLRPC$TL_upload_cdnFile();
        } else {
            tLRPC$TL_upload_cdnFile = i != -NUM ? null : new TLRPC$upload_CdnFile() { // from class: org.telegram.tgnet.TLRPC$TL_upload_cdnFileReuploadNeeded
                public static int constructor = -NUM;

                @Override // org.telegram.tgnet.TLObject
                public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                    this.request_token = abstractSerializedData2.readByteArray(z2);
                }

                @Override // org.telegram.tgnet.TLObject
                public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                    abstractSerializedData2.writeInt32(constructor);
                    abstractSerializedData2.writeByteArray(this.request_token);
                }
            };
        }
        if (tLRPC$TL_upload_cdnFile != null || !z) {
            if (tLRPC$TL_upload_cdnFile != null) {
                tLRPC$TL_upload_cdnFile.readParams(abstractSerializedData, z);
            }
            return tLRPC$TL_upload_cdnFile;
        }
        throw new RuntimeException(String.format("can't parse magic %x in upload_CdnFile", Integer.valueOf(i)));
    }
}
