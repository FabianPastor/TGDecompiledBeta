package org.telegram.messenger;

import org.telegram.tgnet.AbstractSerializedData;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public class MessageCustomParamsHelper {
    public static boolean isEmpty(TLRPC.Message message) {
        return message.voiceTranscription == null && !message.voiceTranscriptionOpen && !message.voiceTranscriptionFinal && !message.voiceTranscriptionRated && message.voiceTranscriptionId == 0 && !message.premiumEffectWasPlayed;
    }

    public static void copyParams(TLRPC.Message fromMessage, TLRPC.Message toMessage) {
        toMessage.voiceTranscription = fromMessage.voiceTranscription;
        toMessage.voiceTranscriptionOpen = fromMessage.voiceTranscriptionOpen;
        toMessage.voiceTranscriptionFinal = fromMessage.voiceTranscriptionFinal;
        toMessage.voiceTranscriptionRated = fromMessage.voiceTranscriptionRated;
        toMessage.voiceTranscriptionId = fromMessage.voiceTranscriptionId;
        toMessage.premiumEffectWasPlayed = fromMessage.premiumEffectWasPlayed;
    }

    public static void readLocalParams(TLRPC.Message message, NativeByteBuffer byteBuffer) {
        if (byteBuffer != null) {
            int version = byteBuffer.readInt32(true);
            switch (version) {
                case 1:
                    new Params_v1(message).readParams(byteBuffer, true);
                    return;
                default:
                    throw new RuntimeException("can't read params version = " + version);
            }
        }
    }

    public static NativeByteBuffer writeLocalParams(TLRPC.Message message) {
        if (isEmpty(message)) {
            return null;
        }
        TLObject params = new Params_v1(message);
        try {
            NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(params.getObjectSize());
            params.serializeToStream(nativeByteBuffer);
            return nativeByteBuffer;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static class Params_v1 extends TLObject {
        private static final int VERSION = 1;
        int flags;
        final TLRPC.Message message;

        private Params_v1(TLRPC.Message message2) {
            this.flags = 0;
            this.message = message2;
            this.flags = 0 + (message2.voiceTranscription != null ? 1 : 0);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(1);
            stream.writeInt32(this.flags);
            if ((1 & this.flags) != 0) {
                stream.writeString(this.message.voiceTranscription);
            }
            stream.writeBool(this.message.voiceTranscriptionOpen);
            stream.writeBool(this.message.voiceTranscriptionFinal);
            stream.writeBool(this.message.voiceTranscriptionRated);
            stream.writeInt64(this.message.voiceTranscriptionId);
            stream.writeBool(this.message.premiumEffectWasPlayed);
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            int readInt32 = stream.readInt32(true);
            this.flags = readInt32;
            if ((1 & readInt32) != 0) {
                this.message.voiceTranscription = stream.readString(exception);
            }
            this.message.voiceTranscriptionOpen = stream.readBool(exception);
            this.message.voiceTranscriptionFinal = stream.readBool(exception);
            this.message.voiceTranscriptionRated = stream.readBool(exception);
            this.message.voiceTranscriptionId = stream.readInt64(exception);
            this.message.premiumEffectWasPlayed = stream.readBool(exception);
        }
    }
}
