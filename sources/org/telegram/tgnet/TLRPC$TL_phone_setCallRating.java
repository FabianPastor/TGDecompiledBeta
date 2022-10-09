package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_phone_setCallRating extends TLObject {
    public static int constructor = NUM;
    public String comment;
    public int flags;
    public TLRPC$TL_inputPhoneCall peer;
    public int rating;
    public boolean user_initiative;

    @Override // org.telegram.tgnet.TLObject
    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$Updates.TLdeserialize(abstractSerializedData, i, z);
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        int i = this.user_initiative ? this.flags | 1 : this.flags & (-2);
        this.flags = i;
        abstractSerializedData.writeInt32(i);
        this.peer.serializeToStream(abstractSerializedData);
        abstractSerializedData.writeInt32(this.rating);
        abstractSerializedData.writeString(this.comment);
    }
}
