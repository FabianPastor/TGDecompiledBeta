package org.telegram.tgnet;

public abstract class TLRPC$account_SavedRingtones extends TLObject {
    public static TLRPC$account_SavedRingtones TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$account_SavedRingtones tLRPC$account_SavedRingtones;
        if (i != -NUM) {
            tLRPC$account_SavedRingtones = i != -67704655 ? null : new TLRPC$TL_account_savedRingtonesNotModified();
        } else {
            tLRPC$account_SavedRingtones = new TLRPC$TL_account_savedRingtones();
        }
        if (tLRPC$account_SavedRingtones != null || !z) {
            if (tLRPC$account_SavedRingtones != null) {
                tLRPC$account_SavedRingtones.readParams(abstractSerializedData, z);
            }
            return tLRPC$account_SavedRingtones;
        }
        throw new RuntimeException(String.format("can't parse magic %x in account_SavedRingtones", new Object[]{Integer.valueOf(i)}));
    }
}
