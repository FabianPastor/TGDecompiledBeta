package org.telegram.tgnet;

public abstract class TLRPC$account_EmailVerified extends TLObject {
    public static TLRPC$account_EmailVerified TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$account_EmailVerified tLRPC$account_EmailVerified;
        if (i != -NUM) {
            tLRPC$account_EmailVerified = i != NUM ? null : new TLRPC$TL_account_emailVerified();
        } else {
            tLRPC$account_EmailVerified = new TLRPC$TL_account_emailVerifiedLogin();
        }
        if (tLRPC$account_EmailVerified != null || !z) {
            if (tLRPC$account_EmailVerified != null) {
                tLRPC$account_EmailVerified.readParams(abstractSerializedData, z);
            }
            return tLRPC$account_EmailVerified;
        }
        throw new RuntimeException(String.format("can't parse magic %x in account_EmailVerified", new Object[]{Integer.valueOf(i)}));
    }
}
