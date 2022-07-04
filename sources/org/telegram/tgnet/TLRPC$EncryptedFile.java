package org.telegram.tgnet;

public abstract class TLRPC$EncryptedFile extends TLObject {
    public long access_hash;
    public int dc_id;
    public long id;
    public int key_fingerprint;
    public long size;

    public static TLRPC$EncryptedFile TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$EncryptedFile tLRPC$EncryptedFile;
        if (i != -NUM) {
            tLRPC$EncryptedFile = i != -NUM ? i != NUM ? null : new TLRPC$TL_encryptedFile_layer142() : new TLRPC$TL_encryptedFileEmpty();
        } else {
            tLRPC$EncryptedFile = new TLRPC$TL_encryptedFile();
        }
        if (tLRPC$EncryptedFile != null || !z) {
            if (tLRPC$EncryptedFile != null) {
                tLRPC$EncryptedFile.readParams(abstractSerializedData, z);
            }
            return tLRPC$EncryptedFile;
        }
        throw new RuntimeException(String.format("can't parse magic %x in EncryptedFile", new Object[]{Integer.valueOf(i)}));
    }
}
