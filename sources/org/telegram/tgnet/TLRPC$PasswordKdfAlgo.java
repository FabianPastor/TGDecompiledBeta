package org.telegram.tgnet;
/* loaded from: classes.dex */
public abstract class TLRPC$PasswordKdfAlgo extends TLObject {
    public static TLRPC$PasswordKdfAlgo TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$PasswordKdfAlgo tLRPC$TL_passwordKdfAlgoUnknown;
        if (i == -NUM) {
            tLRPC$TL_passwordKdfAlgoUnknown = new TLRPC$TL_passwordKdfAlgoUnknown();
        } else {
            tLRPC$TL_passwordKdfAlgoUnknown = i != NUM ? null : new TLRPC$TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow();
        }
        if (tLRPC$TL_passwordKdfAlgoUnknown != null || !z) {
            if (tLRPC$TL_passwordKdfAlgoUnknown != null) {
                tLRPC$TL_passwordKdfAlgoUnknown.readParams(abstractSerializedData, z);
            }
            return tLRPC$TL_passwordKdfAlgoUnknown;
        }
        throw new RuntimeException(String.format("can't parse magic %x in PasswordKdfAlgo", Integer.valueOf(i)));
    }
}
