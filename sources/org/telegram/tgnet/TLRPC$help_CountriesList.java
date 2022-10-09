package org.telegram.tgnet;
/* loaded from: classes.dex */
public abstract class TLRPC$help_CountriesList extends TLObject {
    public static TLRPC$help_CountriesList TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$help_CountriesList tLRPC$TL_help_countriesList;
        if (i != -NUM) {
            tLRPC$TL_help_countriesList = i != -NUM ? null : new TLRPC$help_CountriesList() { // from class: org.telegram.tgnet.TLRPC$TL_help_countriesListNotModified
                public static int constructor = -NUM;

                @Override // org.telegram.tgnet.TLObject
                public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                    abstractSerializedData2.writeInt32(constructor);
                }
            };
        } else {
            tLRPC$TL_help_countriesList = new TLRPC$TL_help_countriesList();
        }
        if (tLRPC$TL_help_countriesList != null || !z) {
            if (tLRPC$TL_help_countriesList != null) {
                tLRPC$TL_help_countriesList.readParams(abstractSerializedData, z);
            }
            return tLRPC$TL_help_countriesList;
        }
        throw new RuntimeException(String.format("can't parse magic %x in help_CountriesList", Integer.valueOf(i)));
    }
}
