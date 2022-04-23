package org.telegram.tgnet;

public abstract class TLRPC$account_SavedRingtone extends TLObject {
    public static TLRPC$account_SavedRingtone TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$account_SavedRingtone tLRPC$account_SavedRingtone;
        if (i != -NUM) {
            tLRPC$account_SavedRingtone = i != NUM ? null : new TLRPC$TL_account_savedRingtoneConverted();
        } else {
            tLRPC$account_SavedRingtone = new TLRPC$TL_account_savedRingtone();
        }
        if (tLRPC$account_SavedRingtone != null || !z) {
            if (tLRPC$account_SavedRingtone != null) {
                tLRPC$account_SavedRingtone.readParams(abstractSerializedData, z);
            }
            return tLRPC$account_SavedRingtone;
        }
        throw new RuntimeException(String.format("can't parse magic %x in account_SavedRingtone", new Object[]{Integer.valueOf(i)}));
    }
}
