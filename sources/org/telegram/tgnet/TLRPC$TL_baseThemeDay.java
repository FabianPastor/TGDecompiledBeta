package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_baseThemeDay extends TLRPC$BaseTheme {
    public static int constructor = -69724536;

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
    }
}
