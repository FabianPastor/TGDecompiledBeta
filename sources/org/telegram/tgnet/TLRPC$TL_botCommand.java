package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_botCommand extends TLObject {
    public static int constructor = -NUM;
    public String command;
    public String description;

    public static TLRPC$TL_botCommand TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        if (constructor != i) {
            if (z) {
                throw new RuntimeException(String.format("can't parse magic %x in TL_botCommand", Integer.valueOf(i)));
            }
            return null;
        }
        TLRPC$TL_botCommand tLRPC$TL_botCommand = new TLRPC$TL_botCommand();
        tLRPC$TL_botCommand.readParams(abstractSerializedData, z);
        return tLRPC$TL_botCommand;
    }

    @Override // org.telegram.tgnet.TLObject
    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.command = abstractSerializedData.readString(z);
        this.description = abstractSerializedData.readString(z);
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeString(this.command);
        abstractSerializedData.writeString(this.description);
    }
}
