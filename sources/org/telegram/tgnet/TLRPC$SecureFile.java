package org.telegram.tgnet;
/* loaded from: classes.dex */
public abstract class TLRPC$SecureFile extends TLObject {
    public static TLRPC$SecureFile TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$SecureFile tLRPC$TL_secureFile;
        if (i != -NUM) {
            tLRPC$TL_secureFile = i != NUM ? null : new TLRPC$SecureFile() { // from class: org.telegram.tgnet.TLRPC$TL_secureFileEmpty
                public static int constructor = NUM;

                @Override // org.telegram.tgnet.TLObject
                public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                    abstractSerializedData2.writeInt32(constructor);
                }
            };
        } else {
            tLRPC$TL_secureFile = new TLRPC$TL_secureFile();
        }
        if (tLRPC$TL_secureFile != null || !z) {
            if (tLRPC$TL_secureFile != null) {
                tLRPC$TL_secureFile.readParams(abstractSerializedData, z);
            }
            return tLRPC$TL_secureFile;
        }
        throw new RuntimeException(String.format("can't parse magic %x in SecureFile", Integer.valueOf(i)));
    }
}
