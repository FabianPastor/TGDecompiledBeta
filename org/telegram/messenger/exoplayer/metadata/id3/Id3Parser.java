package org.telegram.messenger.exoplayer.metadata.id3;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import org.telegram.messenger.exoplayer.ParserException;
import org.telegram.messenger.exoplayer.metadata.MetadataParser;
import org.telegram.messenger.exoplayer.util.MimeTypes;
import org.telegram.messenger.exoplayer.util.ParsableByteArray;

public final class Id3Parser implements MetadataParser<List<Id3Frame>> {
    private static final int ID3_TEXT_ENCODING_ISO_8859_1 = 0;
    private static final int ID3_TEXT_ENCODING_UTF_16 = 1;
    private static final int ID3_TEXT_ENCODING_UTF_16BE = 2;
    private static final int ID3_TEXT_ENCODING_UTF_8 = 3;

    public boolean canParse(String mimeType) {
        return mimeType.equals(MimeTypes.APPLICATION_ID3);
    }

    public List<Id3Frame> parse(byte[] data, int size) throws ParserException {
        List<Id3Frame> id3Frames = new ArrayList();
        ParsableByteArray id3Data = new ParsableByteArray(data, size);
        int id3Size = parseId3Header(id3Data);
        while (id3Size > 0) {
            int frameId0 = id3Data.readUnsignedByte();
            int frameId1 = id3Data.readUnsignedByte();
            int frameId2 = id3Data.readUnsignedByte();
            int frameId3 = id3Data.readUnsignedByte();
            int frameSize = id3Data.readSynchSafeInt();
            if (frameSize <= 1) {
                break;
            }
            Id3Frame frame;
            id3Data.skipBytes(2);
            if (frameId0 == 84 && frameId1 == 88 && frameId2 == 88 && frameId3 == 88) {
                try {
                    frame = parseTxxxFrame(id3Data, frameSize);
                } catch (Throwable e) {
                    throw new ParserException(e);
                }
            } else if (frameId0 == 80 && frameId1 == 82 && frameId2 == 73 && frameId3 == 86) {
                frame = parsePrivFrame(id3Data, frameSize);
            } else if (frameId0 == 71 && frameId1 == 69 && frameId2 == 79 && frameId3 == 66) {
                frame = parseGeobFrame(id3Data, frameSize);
            } else if (frameId0 == 65 && frameId1 == 80 && frameId2 == 73 && frameId3 == 67) {
                frame = parseApicFrame(id3Data, frameSize);
            } else if (frameId0 == 84) {
                frame = parseTextInformationFrame(id3Data, frameSize, String.format(Locale.US, "%c%c%c%c", new Object[]{Integer.valueOf(frameId0), Integer.valueOf(frameId1), Integer.valueOf(frameId2), Integer.valueOf(frameId3)}));
            } else {
                frame = parseBinaryFrame(id3Data, frameSize, String.format(Locale.US, "%c%c%c%c", new Object[]{Integer.valueOf(frameId0), Integer.valueOf(frameId1), Integer.valueOf(frameId2), Integer.valueOf(frameId3)}));
            }
            id3Frames.add(frame);
            id3Size -= frameSize + 10;
        }
        return Collections.unmodifiableList(id3Frames);
    }

    private static int indexOfEos(byte[] data, int fromIndex, int encoding) {
        int terminationPos = indexOfZeroByte(data, fromIndex);
        if (encoding == 0 || encoding == 3) {
            return terminationPos;
        }
        while (terminationPos < data.length - 1) {
            if (data[terminationPos + 1] == (byte) 0) {
                return terminationPos;
            }
            terminationPos = indexOfZeroByte(data, terminationPos + 1);
        }
        return data.length;
    }

    private static int indexOfZeroByte(byte[] data, int fromIndex) {
        for (int i = fromIndex; i < data.length; i++) {
            if (data[i] == (byte) 0) {
                return i;
            }
        }
        return data.length;
    }

    private static int delimiterLength(int encodingByte) {
        return (encodingByte == 0 || encodingByte == 3) ? 1 : 2;
    }

    private static int parseId3Header(ParsableByteArray id3Buffer) throws ParserException {
        int id1 = id3Buffer.readUnsignedByte();
        int id2 = id3Buffer.readUnsignedByte();
        int id3 = id3Buffer.readUnsignedByte();
        if (id1 == 73 && id2 == 68 && id3 == 51) {
            id3Buffer.skipBytes(2);
            int flags = id3Buffer.readUnsignedByte();
            int id3Size = id3Buffer.readSynchSafeInt();
            if ((flags & 2) != 0) {
                int extendedHeaderSize = id3Buffer.readSynchSafeInt();
                if (extendedHeaderSize > 4) {
                    id3Buffer.skipBytes(extendedHeaderSize - 4);
                }
                id3Size -= extendedHeaderSize;
            }
            if ((flags & 8) != 0) {
                return id3Size - 10;
            }
            return id3Size;
        }
        throw new ParserException(String.format(Locale.US, "Unexpected ID3 file identifier, expected \"ID3\", actual \"%c%c%c\".", new Object[]{Integer.valueOf(id1), Integer.valueOf(id2), Integer.valueOf(id3)}));
    }

    private static TxxxFrame parseTxxxFrame(ParsableByteArray id3Data, int frameSize) throws UnsupportedEncodingException {
        int encoding = id3Data.readUnsignedByte();
        String charset = getCharsetName(encoding);
        byte[] data = new byte[(frameSize - 1)];
        id3Data.readBytes(data, 0, frameSize - 1);
        int descriptionEndIndex = indexOfEos(data, 0, encoding);
        int valueStartIndex = descriptionEndIndex + delimiterLength(encoding);
        return new TxxxFrame(new String(data, 0, descriptionEndIndex, charset), new String(data, valueStartIndex, indexOfEos(data, valueStartIndex, encoding) - valueStartIndex, charset));
    }

    private static PrivFrame parsePrivFrame(ParsableByteArray id3Data, int frameSize) throws UnsupportedEncodingException {
        byte[] data = new byte[frameSize];
        id3Data.readBytes(data, 0, frameSize);
        int ownerEndIndex = indexOfZeroByte(data, 0);
        return new PrivFrame(new String(data, 0, ownerEndIndex, "ISO-8859-1"), Arrays.copyOfRange(data, ownerEndIndex + 1, data.length));
    }

    private static GeobFrame parseGeobFrame(ParsableByteArray id3Data, int frameSize) throws UnsupportedEncodingException {
        int encoding = id3Data.readUnsignedByte();
        String charset = getCharsetName(encoding);
        byte[] data = new byte[(frameSize - 1)];
        id3Data.readBytes(data, 0, frameSize - 1);
        int mimeTypeEndIndex = indexOfZeroByte(data, 0);
        String mimeType = new String(data, 0, mimeTypeEndIndex, "ISO-8859-1");
        int filenameStartIndex = mimeTypeEndIndex + 1;
        int filenameEndIndex = indexOfEos(data, filenameStartIndex, encoding);
        String filename = new String(data, filenameStartIndex, filenameEndIndex - filenameStartIndex, charset);
        int descriptionStartIndex = filenameEndIndex + delimiterLength(encoding);
        int descriptionEndIndex = indexOfEos(data, descriptionStartIndex, encoding);
        return new GeobFrame(mimeType, filename, new String(data, descriptionStartIndex, descriptionEndIndex - descriptionStartIndex, charset), Arrays.copyOfRange(data, descriptionEndIndex + delimiterLength(encoding), data.length));
    }

    private static ApicFrame parseApicFrame(ParsableByteArray id3Data, int frameSize) throws UnsupportedEncodingException {
        int encoding = id3Data.readUnsignedByte();
        String charset = getCharsetName(encoding);
        byte[] data = new byte[(frameSize - 1)];
        id3Data.readBytes(data, 0, frameSize - 1);
        int mimeTypeEndIndex = indexOfZeroByte(data, 0);
        String mimeType = new String(data, 0, mimeTypeEndIndex, "ISO-8859-1");
        int pictureType = data[mimeTypeEndIndex + 1] & 255;
        int descriptionStartIndex = mimeTypeEndIndex + 2;
        int descriptionEndIndex = indexOfEos(data, descriptionStartIndex, encoding);
        return new ApicFrame(mimeType, new String(data, descriptionStartIndex, descriptionEndIndex - descriptionStartIndex, charset), pictureType, Arrays.copyOfRange(data, descriptionEndIndex + delimiterLength(encoding), data.length));
    }

    private static TextInformationFrame parseTextInformationFrame(ParsableByteArray id3Data, int frameSize, String id) throws UnsupportedEncodingException {
        int encoding = id3Data.readUnsignedByte();
        String charset = getCharsetName(encoding);
        byte[] data = new byte[(frameSize - 1)];
        id3Data.readBytes(data, 0, frameSize - 1);
        return new TextInformationFrame(id, new String(data, 0, indexOfEos(data, 0, encoding), charset));
    }

    private static BinaryFrame parseBinaryFrame(ParsableByteArray id3Data, int frameSize, String id) {
        byte[] frame = new byte[frameSize];
        id3Data.readBytes(frame, 0, frameSize);
        return new BinaryFrame(id, frame);
    }

    private static String getCharsetName(int encodingByte) {
        switch (encodingByte) {
            case 0:
                return "ISO-8859-1";
            case 1:
                return "UTF-16";
            case 2:
                return "UTF-16BE";
            case 3:
                return "UTF-8";
            default:
                return "ISO-8859-1";
        }
    }
}
