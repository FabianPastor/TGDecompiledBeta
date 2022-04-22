package org.telegram.tgnet;

public class TLRPC$TL_botCommand extends TLObject {
    public static int constructor = -NUM;
    public String command;
    public String description;

    public static TLRPC$TL_botCommand TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        if (constructor == i) {
            TLRPC$TL_botCommand tLRPC$TL_botCommand = new TLRPC$TL_botCommand();
            tLRPC$TL_botCommand.readParams(abstractSerializedData, z);
            return tLRPC$TL_botCommand;
        } else if (!z) {
            return null;
        } else {
            throw new RuntimeException(String.format("can't parse magic %x in TL_botCommand", new Object[]{Integer.valueOf(i)}));
        }
    }

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.command = abstractSerializedData.readString(z);
        this.description = abstractSerializedData.readString(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeString(this.command);
        abstractSerializedData.writeString(this.description);
    }
}
