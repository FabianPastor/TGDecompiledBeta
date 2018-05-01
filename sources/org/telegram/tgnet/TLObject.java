package org.telegram.tgnet;

public class TLObject {
    private static final ThreadLocal<NativeByteBuffer> sizeCalculator = new C07071();
    public boolean disableFree = false;
    public int networkType;

    /* renamed from: org.telegram.tgnet.TLObject$1 */
    static class C07071 extends ThreadLocal<NativeByteBuffer> {
        C07071() {
        }

        protected NativeByteBuffer initialValue() {
            return new NativeByteBuffer(true);
        }
    }

    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return null;
    }

    public void freeResources() {
    }

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
    }

    public int getObjectSize() {
        NativeByteBuffer nativeByteBuffer = (NativeByteBuffer) sizeCalculator.get();
        nativeByteBuffer.rewind();
        serializeToStream((AbstractSerializedData) sizeCalculator.get());
        return nativeByteBuffer.length();
    }
}
