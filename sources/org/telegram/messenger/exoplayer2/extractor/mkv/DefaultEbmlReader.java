package org.telegram.messenger.exoplayer2.extractor.mkv;

import java.io.IOException;
import java.util.Stack;
import org.telegram.messenger.exoplayer2.ParserException;
import org.telegram.messenger.exoplayer2.extractor.ExtractorInput;
import org.telegram.messenger.exoplayer2.util.Assertions;

final class DefaultEbmlReader implements EbmlReader {
    private static final int ELEMENT_STATE_READ_CONTENT = 2;
    private static final int ELEMENT_STATE_READ_CONTENT_SIZE = 1;
    private static final int ELEMENT_STATE_READ_ID = 0;
    private static final int MAX_ID_BYTES = 4;
    private static final int MAX_INTEGER_ELEMENT_SIZE_BYTES = 8;
    private static final int MAX_LENGTH_BYTES = 8;
    private static final int VALID_FLOAT32_ELEMENT_SIZE_BYTES = 4;
    private static final int VALID_FLOAT64_ELEMENT_SIZE_BYTES = 8;
    private long elementContentSize;
    private int elementId;
    private int elementState;
    private final Stack<MasterElement> masterElementsStack = new Stack();
    private EbmlReaderOutput output;
    private final byte[] scratch = new byte[8];
    private final VarintReader varintReader = new VarintReader();

    private static final class MasterElement {
        private final long elementEndPosition;
        private final int elementId;

        private MasterElement(int elementId, long elementEndPosition) {
            this.elementId = elementId;
            this.elementEndPosition = elementEndPosition;
        }
    }

    DefaultEbmlReader() {
    }

    public void init(EbmlReaderOutput eventHandler) {
        this.output = eventHandler;
    }

    public void reset() {
        this.elementState = 0;
        this.masterElementsStack.clear();
        this.varintReader.reset();
    }

    public boolean read(ExtractorInput input) throws IOException, InterruptedException {
        ExtractorInput extractorInput = input;
        Assertions.checkState(this.output != null);
        while (true) {
            if (r0.masterElementsStack.isEmpty() || input.getPosition() < ((MasterElement) r0.masterElementsStack.peek()).elementEndPosition) {
                long result;
                if (r0.elementState == 0) {
                    result = r0.varintReader.readUnsignedVarint(extractorInput, true, false, 4);
                    if (result == -2) {
                        result = maybeResyncToNextLevel1Element(input);
                    }
                    if (result == -1) {
                        return false;
                    }
                    r0.elementId = (int) result;
                    r0.elementState = 1;
                }
                if (r0.elementState == 1) {
                    r0.elementContentSize = r0.varintReader.readUnsignedVarint(extractorInput, false, true, 8);
                    r0.elementState = 2;
                }
                int type = r0.output.getElementType(r0.elementId);
                StringBuilder stringBuilder;
                switch (type) {
                    case 0:
                        extractorInput.skipFully((int) r0.elementContentSize);
                        r0.elementState = 0;
                    case 1:
                        result = input.getPosition();
                        long elementEndPosition = result + r0.elementContentSize;
                        r0.masterElementsStack.add(new MasterElement(r0.elementId, elementEndPosition));
                        r0.output.startMasterElement(r0.elementId, result, r0.elementContentSize);
                        r0.elementState = 0;
                        return true;
                    case 2:
                        if (r0.elementContentSize > 8) {
                            stringBuilder = new StringBuilder();
                            stringBuilder.append("Invalid integer size: ");
                            stringBuilder.append(r0.elementContentSize);
                            throw new ParserException(stringBuilder.toString());
                        }
                        r0.output.integerElement(r0.elementId, readInteger(extractorInput, (int) r0.elementContentSize));
                        r0.elementState = 0;
                        return true;
                    case 3:
                        if (r0.elementContentSize > 2147483647L) {
                            stringBuilder = new StringBuilder();
                            stringBuilder.append("String element size: ");
                            stringBuilder.append(r0.elementContentSize);
                            throw new ParserException(stringBuilder.toString());
                        }
                        r0.output.stringElement(r0.elementId, readString(extractorInput, (int) r0.elementContentSize));
                        r0.elementState = 0;
                        return true;
                    case 4:
                        r0.output.binaryElement(r0.elementId, (int) r0.elementContentSize, extractorInput);
                        r0.elementState = 0;
                        return true;
                    case 5:
                        if (r0.elementContentSize == 4 || r0.elementContentSize == 8) {
                            r0.output.floatElement(r0.elementId, readFloat(extractorInput, (int) r0.elementContentSize));
                            r0.elementState = 0;
                            return true;
                        }
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("Invalid float size: ");
                        stringBuilder.append(r0.elementContentSize);
                        throw new ParserException(stringBuilder.toString());
                    default:
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("Invalid element type ");
                        stringBuilder.append(type);
                        throw new ParserException(stringBuilder.toString());
                }
            }
            r0.output.endMasterElement(((MasterElement) r0.masterElementsStack.pop()).elementId);
            return true;
        }
    }

    private long maybeResyncToNextLevel1Element(ExtractorInput input) throws IOException, InterruptedException {
        input.resetPeekPosition();
        while (true) {
            input.peekFully(this.scratch, 0, 4);
            int varintLength = VarintReader.parseUnsignedVarintLength(this.scratch[0]);
            if (varintLength != -1 && varintLength <= 4) {
                int potentialId = (int) VarintReader.assembleVarint(this.scratch, varintLength, false);
                if (this.output.isLevel1Element(potentialId)) {
                    input.skipFully(varintLength);
                    return (long) potentialId;
                }
            }
            input.skipFully(1);
        }
    }

    private long readInteger(ExtractorInput input, int byteLength) throws IOException, InterruptedException {
        int i = 0;
        input.readFully(this.scratch, 0, byteLength);
        long value = 0;
        while (true) {
            int i2 = i;
            if (i2 >= byteLength) {
                return value;
            }
            value = (value << 8) | ((long) (this.scratch[i2] & 255));
            i = i2 + 1;
        }
    }

    private double readFloat(ExtractorInput input, int byteLength) throws IOException, InterruptedException {
        long integerValue = readInteger(input, byteLength);
        if (byteLength == 4) {
            return (double) Float.intBitsToFloat((int) integerValue);
        }
        return Double.longBitsToDouble(integerValue);
    }

    private String readString(ExtractorInput input, int byteLength) throws IOException, InterruptedException {
        if (byteLength == 0) {
            return TtmlNode.ANONYMOUS_REGION_ID;
        }
        byte[] stringBytes = new byte[byteLength];
        input.readFully(stringBytes, 0, byteLength);
        return new String(stringBytes);
    }
}
