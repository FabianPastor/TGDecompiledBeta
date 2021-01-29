package org.telegram.tgnet;

public class TLRPC$TL_baseThemeDay extends TLRPC$BaseTheme {
    public static int constructor = -69724536;

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
    }
}
