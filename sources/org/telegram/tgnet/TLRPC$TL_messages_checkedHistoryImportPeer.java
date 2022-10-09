package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_messages_checkedHistoryImportPeer extends TLObject {
    public static int constructor = -NUM;
    public String confirm_text;

    public static TLRPC$TL_messages_checkedHistoryImportPeer TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        if (constructor != i) {
            if (z) {
                throw new RuntimeException(String.format("can't parse magic %x in TL_messages_checkedHistoryImportPeer", Integer.valueOf(i)));
            }
            return null;
        }
        TLRPC$TL_messages_checkedHistoryImportPeer tLRPC$TL_messages_checkedHistoryImportPeer = new TLRPC$TL_messages_checkedHistoryImportPeer();
        tLRPC$TL_messages_checkedHistoryImportPeer.readParams(abstractSerializedData, z);
        return tLRPC$TL_messages_checkedHistoryImportPeer;
    }

    @Override // org.telegram.tgnet.TLObject
    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.confirm_text = abstractSerializedData.readString(z);
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeString(this.confirm_text);
    }
}
