package org.telegram.tgnet;
/* loaded from: classes.dex */
public abstract class TLRPC$SecurePasswordKdfAlgo extends TLObject {
    public static TLRPC$SecurePasswordKdfAlgo TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$SecurePasswordKdfAlgo tLRPC$TL_securePasswordKdfAlgoSHA512;
        if (i == -NUM) {
            tLRPC$TL_securePasswordKdfAlgoSHA512 = new TLRPC$TL_securePasswordKdfAlgoSHA512();
        } else if (i == -NUM) {
            tLRPC$TL_securePasswordKdfAlgoSHA512 = new TLRPC$TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000();
        } else {
            tLRPC$TL_securePasswordKdfAlgoSHA512 = i != 4883767 ? null : new TLRPC$TL_securePasswordKdfAlgoUnknown();
        }
        if (tLRPC$TL_securePasswordKdfAlgoSHA512 != null || !z) {
            if (tLRPC$TL_securePasswordKdfAlgoSHA512 != null) {
                tLRPC$TL_securePasswordKdfAlgoSHA512.readParams(abstractSerializedData, z);
            }
            return tLRPC$TL_securePasswordKdfAlgoSHA512;
        }
        throw new RuntimeException(String.format("can't parse magic %x in SecurePasswordKdfAlgo", Integer.valueOf(i)));
    }
}
