package org.telegram.tgnet;
/* loaded from: classes.dex */
public abstract class TLRPC$help_AppUpdate extends TLObject {
    public static TLRPC$help_AppUpdate TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$help_AppUpdate tLRPC$help_AppUpdate;
        if (i != -NUM) {
            tLRPC$help_AppUpdate = i != -NUM ? null : new TLRPC$TL_help_appUpdate();
        } else {
            tLRPC$help_AppUpdate = new TLRPC$help_AppUpdate() { // from class: org.telegram.tgnet.TLRPC$TL_help_noAppUpdate
                public static int constructor = -NUM;

                @Override // org.telegram.tgnet.TLObject
                public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                    abstractSerializedData2.writeInt32(constructor);
                }
            };
        }
        if (tLRPC$help_AppUpdate != null || !z) {
            if (tLRPC$help_AppUpdate != null) {
                tLRPC$help_AppUpdate.readParams(abstractSerializedData, z);
            }
            return tLRPC$help_AppUpdate;
        }
        throw new RuntimeException(String.format("can't parse magic %x in help_AppUpdate", Integer.valueOf(i)));
    }
}
