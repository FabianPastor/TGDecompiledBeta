package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_channelAdminLogEventActionToggleForum extends TLRPC$ChannelAdminLogEventAction {
    public static int constructor = 46949251;
    public boolean new_value;

    @Override // org.telegram.tgnet.TLObject
    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.new_value = abstractSerializedData.readBool(z);
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeBool(this.new_value);
    }
}
