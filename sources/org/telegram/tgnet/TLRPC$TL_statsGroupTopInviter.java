package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_statsGroupTopInviter extends TLObject {
    public static int constructor = NUM;
    public int invitations;
    public long user_id;

    public static TLRPC$TL_statsGroupTopInviter TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        if (constructor != i) {
            if (z) {
                throw new RuntimeException(String.format("can't parse magic %x in TL_statsGroupTopInviter", Integer.valueOf(i)));
            }
            return null;
        }
        TLRPC$TL_statsGroupTopInviter tLRPC$TL_statsGroupTopInviter = new TLRPC$TL_statsGroupTopInviter();
        tLRPC$TL_statsGroupTopInviter.readParams(abstractSerializedData, z);
        return tLRPC$TL_statsGroupTopInviter;
    }

    @Override // org.telegram.tgnet.TLObject
    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.user_id = abstractSerializedData.readInt64(z);
        this.invitations = abstractSerializedData.readInt32(z);
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt64(this.user_id);
        abstractSerializedData.writeInt32(this.invitations);
    }
}
