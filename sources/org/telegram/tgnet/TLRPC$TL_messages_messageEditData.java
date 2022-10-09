package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_messages_messageEditData extends TLObject {
    public static int constructor = NUM;
    public boolean caption;
    public int flags;

    public static TLRPC$TL_messages_messageEditData TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        if (constructor != i) {
            if (z) {
                throw new RuntimeException(String.format("can't parse magic %x in TL_messages_messageEditData", Integer.valueOf(i)));
            }
            return null;
        }
        TLRPC$TL_messages_messageEditData tLRPC$TL_messages_messageEditData = new TLRPC$TL_messages_messageEditData();
        tLRPC$TL_messages_messageEditData.readParams(abstractSerializedData, z);
        return tLRPC$TL_messages_messageEditData;
    }

    @Override // org.telegram.tgnet.TLObject
    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        this.flags = readInt32;
        boolean z2 = true;
        if ((readInt32 & 1) == 0) {
            z2 = false;
        }
        this.caption = z2;
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        int i = this.caption ? this.flags | 1 : this.flags & (-2);
        this.flags = i;
        abstractSerializedData.writeInt32(i);
    }
}
