package org.telegram.tgnet;
/* loaded from: classes.dex */
public abstract class TLRPC$account_ResetPasswordResult extends TLObject {
    public static TLRPC$account_ResetPasswordResult TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$account_ResetPasswordResult tLRPC$TL_account_resetPasswordFailedWait;
        if (i == -NUM) {
            tLRPC$TL_account_resetPasswordFailedWait = new TLRPC$TL_account_resetPasswordFailedWait();
        } else if (i != -NUM) {
            tLRPC$TL_account_resetPasswordFailedWait = i != -NUM ? null : new TLRPC$TL_account_resetPasswordRequestedWait();
        } else {
            tLRPC$TL_account_resetPasswordFailedWait = new TLRPC$account_ResetPasswordResult() { // from class: org.telegram.tgnet.TLRPC$TL_account_resetPasswordOk
                public static int constructor = -NUM;

                @Override // org.telegram.tgnet.TLObject
                public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                    abstractSerializedData2.writeInt32(constructor);
                }
            };
        }
        if (tLRPC$TL_account_resetPasswordFailedWait != null || !z) {
            if (tLRPC$TL_account_resetPasswordFailedWait != null) {
                tLRPC$TL_account_resetPasswordFailedWait.readParams(abstractSerializedData, z);
            }
            return tLRPC$TL_account_resetPasswordFailedWait;
        }
        throw new RuntimeException(String.format("can't parse magic %x in account_ResetPasswordResult", Integer.valueOf(i)));
    }
}
