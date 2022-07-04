package org.telegram.tgnet;

public abstract class TLRPC$InputSecureFile extends TLObject {
    public static TLRPC$InputSecureFile TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$InputSecureFile tLRPC$InputSecureFile;
        if (i != NUM) {
            tLRPC$InputSecureFile = i != NUM ? null : new TLRPC$TL_inputSecureFile();
        } else {
            tLRPC$InputSecureFile = new TLRPC$TL_inputSecureFileUploaded();
        }
        if (tLRPC$InputSecureFile != null || !z) {
            if (tLRPC$InputSecureFile != null) {
                tLRPC$InputSecureFile.readParams(abstractSerializedData, z);
            }
            return tLRPC$InputSecureFile;
        }
        throw new RuntimeException(String.format("can't parse magic %x in InputSecureFile", new Object[]{Integer.valueOf(i)}));
    }
}
