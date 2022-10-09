package org.telegram.tgnet;
/* loaded from: classes.dex */
public abstract class TLRPC$account_SavedRingtone extends TLObject {
    public static TLRPC$account_SavedRingtone TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$account_SavedRingtone tLRPC$account_SavedRingtone;
        if (i != -NUM) {
            tLRPC$account_SavedRingtone = i != NUM ? null : new TLRPC$TL_account_savedRingtoneConverted();
        } else {
            tLRPC$account_SavedRingtone = new TLRPC$account_SavedRingtone() { // from class: org.telegram.tgnet.TLRPC$TL_account_savedRingtone
                public static int constructor = -NUM;

                @Override // org.telegram.tgnet.TLObject
                public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                    abstractSerializedData2.writeInt32(constructor);
                }
            };
        }
        if (tLRPC$account_SavedRingtone != null || !z) {
            if (tLRPC$account_SavedRingtone != null) {
                tLRPC$account_SavedRingtone.readParams(abstractSerializedData, z);
            }
            return tLRPC$account_SavedRingtone;
        }
        throw new RuntimeException(String.format("can't parse magic %x in account_SavedRingtone", Integer.valueOf(i)));
    }
}
