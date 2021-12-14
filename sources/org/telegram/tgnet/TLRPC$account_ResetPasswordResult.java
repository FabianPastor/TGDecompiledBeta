package org.telegram.tgnet;

public abstract class TLRPC$account_ResetPasswordResult extends TLObject {
    public static TLRPC$account_ResetPasswordResult TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$account_ResetPasswordResult tLRPC$account_ResetPasswordResult;
        if (i != -NUM) {
            tLRPC$account_ResetPasswordResult = i != -NUM ? i != -NUM ? null : new TLRPC$TL_account_resetPasswordRequestedWait() : new TLRPC$TL_account_resetPasswordOk();
        } else {
            tLRPC$account_ResetPasswordResult = new TLRPC$TL_account_resetPasswordFailedWait();
        }
        if (tLRPC$account_ResetPasswordResult != null || !z) {
            if (tLRPC$account_ResetPasswordResult != null) {
                tLRPC$account_ResetPasswordResult.readParams(abstractSerializedData, z);
            }
            return tLRPC$account_ResetPasswordResult;
        }
        throw new RuntimeException(String.format("can't parse magic %x in account_ResetPasswordResult", new Object[]{Integer.valueOf(i)}));
    }
}
