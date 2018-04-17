package org.telegram.messenger.exoplayer2.metadata.id3;

import android.util.Log;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import org.telegram.messenger.exoplayer2.C0542C;
import org.telegram.messenger.exoplayer2.metadata.Metadata;
import org.telegram.messenger.exoplayer2.metadata.MetadataDecoder;
import org.telegram.messenger.exoplayer2.metadata.MetadataInputBuffer;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;
import org.telegram.messenger.exoplayer2.util.Util;

public final class Id3Decoder implements MetadataDecoder {
    private static final int FRAME_FLAG_V3_HAS_GROUP_IDENTIFIER = 32;
    private static final int FRAME_FLAG_V3_IS_COMPRESSED = 128;
    private static final int FRAME_FLAG_V3_IS_ENCRYPTED = 64;
    private static final int FRAME_FLAG_V4_HAS_DATA_LENGTH = 1;
    private static final int FRAME_FLAG_V4_HAS_GROUP_IDENTIFIER = 64;
    private static final int FRAME_FLAG_V4_IS_COMPRESSED = 8;
    private static final int FRAME_FLAG_V4_IS_ENCRYPTED = 4;
    private static final int FRAME_FLAG_V4_IS_UNSYNCHRONIZED = 2;
    public static final int ID3_HEADER_LENGTH = 10;
    public static final int ID3_TAG = Util.getIntegerCodeForString("ID3");
    private static final int ID3_TEXT_ENCODING_ISO_8859_1 = 0;
    private static final int ID3_TEXT_ENCODING_UTF_16 = 1;
    private static final int ID3_TEXT_ENCODING_UTF_16BE = 2;
    private static final int ID3_TEXT_ENCODING_UTF_8 = 3;
    private static final String TAG = "Id3Decoder";
    private final FramePredicate framePredicate;

    public interface FramePredicate {
        boolean evaluate(int i, int i2, int i3, int i4, int i5);
    }

    private static final class Id3Header {
        private final int framesSize;
        private final boolean isUnsynchronized;
        private final int majorVersion;

        public Id3Header(int majorVersion, boolean isUnsynchronized, int framesSize) {
            this.majorVersion = majorVersion;
            this.isUnsynchronized = isUnsynchronized;
            this.framesSize = framesSize;
        }
    }

    public Id3Decoder() {
        this(null);
    }

    public Id3Decoder(FramePredicate framePredicate) {
        this.framePredicate = framePredicate;
    }

    public Metadata decode(MetadataInputBuffer inputBuffer) {
        ByteBuffer buffer = inputBuffer.data;
        return decode(buffer.array(), buffer.limit());
    }

    public Metadata decode(byte[] data, int size) {
        List id3Frames = new ArrayList();
        ParsableByteArray id3Data = new ParsableByteArray(data, size);
        Id3Header id3Header = decodeHeader(id3Data);
        if (id3Header == null) {
            return null;
        }
        int startPosition = id3Data.getPosition();
        int frameHeaderSize = id3Header.majorVersion == 2 ? 6 : 10;
        int framesSize = id3Header.framesSize;
        if (id3Header.isUnsynchronized) {
            framesSize = removeUnsynchronization(id3Data, id3Header.framesSize);
        }
        id3Data.setLimit(startPosition + framesSize);
        boolean unsignedIntFrameSizeHack = false;
        if (!validateFrames(id3Data, id3Header.majorVersion, frameHeaderSize, false)) {
            if (id3Header.majorVersion == 4 && validateFrames(id3Data, 4, frameHeaderSize, true)) {
                unsignedIntFrameSizeHack = true;
            } else {
                String str = TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Failed to validate ID3 tag with majorVersion=");
                stringBuilder.append(id3Header.majorVersion);
                Log.w(str, stringBuilder.toString());
                return null;
            }
        }
        while (id3Data.bytesLeft() >= frameHeaderSize) {
            Id3Frame frame = decodeFrame(id3Header.majorVersion, id3Data, unsignedIntFrameSizeHack, frameHeaderSize, this.framePredicate);
            if (frame != null) {
                id3Frames.add(frame);
            }
        }
        return new Metadata(id3Frames);
    }

    private static Id3Header decodeHeader(ParsableByteArray data) {
        if (data.bytesLeft() < 10) {
            Log.w(TAG, "Data too short to be an ID3 tag");
            return null;
        }
        int id = data.readUnsignedInt24();
        if (id != ID3_TAG) {
            String str = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Unexpected first three bytes of ID3 tag header: ");
            stringBuilder.append(id);
            Log.w(str, stringBuilder.toString());
            return null;
        }
        int majorVersion = data.readUnsignedByte();
        boolean z = true;
        data.skipBytes(1);
        int flags = data.readUnsignedByte();
        int framesSize = data.readSynchSafeInt();
        if (majorVersion == 2) {
            if ((flags & 64) != 0) {
                Log.w(TAG, "Skipped ID3 tag with majorVersion=2 and undefined compression scheme");
                return null;
            }
        } else if (majorVersion == 3) {
            if ((flags & 64) != 0) {
                extendedHeaderSize = data.readInt();
                data.skipBytes(extendedHeaderSize);
                framesSize -= extendedHeaderSize + 4;
            }
        } else if (majorVersion == 4) {
            if ((flags & 64) != 0) {
                extendedHeaderSize = data.readSynchSafeInt();
                data.skipBytes(extendedHeaderSize - 4);
                framesSize -= extendedHeaderSize;
            }
            if ((flags & 16) != 0) {
                framesSize -= 10;
            }
        } else {
            String str2 = TAG;
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("Skipped ID3 tag with unsupported majorVersion=");
            stringBuilder2.append(majorVersion);
            Log.w(str2, stringBuilder2.toString());
            return null;
        }
        if (majorVersion >= 4 || (flags & 128) == 0) {
            z = false;
        }
        return new Id3Header(majorVersion, z, framesSize);
    }

    private static boolean validateFrames(ParsableByteArray id3Data, int majorVersion, int frameHeaderSize, boolean unsignedIntFrameSizeHack) {
        Throwable th;
        ParsableByteArray parsableByteArray = id3Data;
        int i = majorVersion;
        int startPosition = id3Data.getPosition();
        while (true) {
            try {
                boolean z = true;
                if (id3Data.bytesLeft() >= frameHeaderSize) {
                    int id;
                    long frameSize;
                    int flags;
                    if (i >= 3) {
                        id = id3Data.readInt();
                        frameSize = id3Data.readUnsignedInt();
                        flags = id3Data.readUnsignedShort();
                    } else {
                        id = id3Data.readUnsignedInt24();
                        frameSize = (long) id3Data.readUnsignedInt24();
                        flags = 0;
                    }
                    if (id == 0 && frameSize == 0 && flags == 0) {
                        parsableByteArray.setPosition(startPosition);
                        return true;
                    }
                    if (i == 4 && !unsignedIntFrameSizeHack) {
                        if ((frameSize & 8421504) != 0) {
                            parsableByteArray.setPosition(startPosition);
                            return false;
                        }
                        frameSize = (((frameSize & 255) | (((frameSize >> 8) & 255) << 7)) | (((frameSize >> 16) & 255) << 14)) | (((frameSize >> 24) & 255) << 21);
                    }
                    boolean hasGroupIdentifier = false;
                    boolean hasDataLength = false;
                    if (i == 4) {
                        hasGroupIdentifier = (flags & 64) != 0;
                        if ((flags & 1) == 0) {
                            z = false;
                        }
                        hasDataLength = z;
                    } else if (i == 3) {
                        hasGroupIdentifier = (flags & 32) != 0;
                        if ((flags & 128) == 0) {
                            z = false;
                        }
                        hasDataLength = z;
                    }
                    int minimumFrameSize = 0;
                    if (hasGroupIdentifier) {
                        minimumFrameSize = 0 + 1;
                    }
                    if (hasDataLength) {
                        minimumFrameSize += 4;
                    }
                    if (frameSize < ((long) minimumFrameSize)) {
                        parsableByteArray.setPosition(startPosition);
                        return false;
                    }
                    try {
                        if (((long) id3Data.bytesLeft()) < frameSize) {
                            parsableByteArray.setPosition(startPosition);
                            return false;
                        }
                        parsableByteArray.skipBytes((int) frameSize);
                    } catch (Throwable th2) {
                        th = th2;
                    }
                } else {
                    parsableByteArray.setPosition(startPosition);
                    return true;
                }
            } catch (Throwable th3) {
                th = th3;
                int i2 = frameHeaderSize;
            }
        }
        Throwable th4 = th;
        parsableByteArray.setPosition(startPosition);
        throw th4;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static Id3Frame decodeFrame(int majorVersion, ParsableByteArray id3Data, boolean unsignedIntFrameSizeHack, int frameHeaderSize, FramePredicate framePredicate) {
        int frameSize;
        UnsupportedEncodingException unsupportedEncodingException;
        Throwable th;
        int frameSize2;
        int i = majorVersion;
        ParsableByteArray parsableByteArray = id3Data;
        int frameId0 = id3Data.readUnsignedByte();
        int frameId1 = id3Data.readUnsignedByte();
        int frameId2 = id3Data.readUnsignedByte();
        int frameId3 = i >= 3 ? id3Data.readUnsignedByte() : 0;
        if (i == 4) {
            frameSize = id3Data.readUnsignedIntToInt();
            if (!unsignedIntFrameSizeHack) {
                frameSize = (((frameSize & 255) | (((frameSize >> 8) & 255) << 7)) | (((frameSize >> 16) & 255) << 14)) | (((frameSize >> 24) & 255) << 21);
            }
        } else if (i == 3) {
            frameSize = id3Data.readUnsignedIntToInt();
        } else {
            frameSize = id3Data.readUnsignedInt24();
        }
        int frameSize3 = frameSize;
        int flags = i >= 3 ? id3Data.readUnsignedShort() : 0;
        if (frameId0 == 0 && frameId1 == 0 && frameId2 == 0 && frameId3 == 0 && frameSize3 == 0 && flags == 0) {
            parsableByteArray.setPosition(id3Data.limit());
            return null;
        }
        int nextFramePosition = id3Data.getPosition() + frameSize3;
        if (nextFramePosition > id3Data.limit()) {
            Log.w(TAG, "Frame size exceeds remaining tag data");
            parsableByteArray.setPosition(id3Data.limit());
            return null;
        }
        int nextFramePosition2;
        if (framePredicate != null) {
            nextFramePosition2 = nextFramePosition;
            int flags2 = flags;
            if (!framePredicate.evaluate(i, frameId0, frameId1, frameId2, frameId3)) {
                parsableByteArray.setPosition(nextFramePosition2);
                return null;
            }
        }
        nextFramePosition2 = nextFramePosition;
        flags2 = flags;
        boolean isCompressed = false;
        boolean isEncrypted = false;
        boolean isUnsynchronized = false;
        boolean hasDataLength = false;
        boolean hasGroupIdentifier = false;
        if (i == 3) {
            isCompressed = (flags2 & 128) != 0;
            isEncrypted = (flags2 & 64) != 0;
            hasGroupIdentifier = (flags2 & 32) != 0;
            hasDataLength = isCompressed;
        } else if (i == 4) {
            hasGroupIdentifier = (flags2 & 64) != 0;
            isCompressed = (flags2 & 8) != 0;
            isEncrypted = (flags2 & 4) != 0;
            isUnsynchronized = (flags2 & 2) != 0;
            hasDataLength = (flags2 & 1) != 0;
        }
        boolean isEncrypted2 = isEncrypted;
        boolean isUnsynchronized2 = isUnsynchronized;
        boolean hasDataLength2 = hasDataLength;
        boolean hasGroupIdentifier2 = hasGroupIdentifier;
        if (!isCompressed) {
            if (!isEncrypted2) {
                Id3Frame frame;
                int i2;
                String str;
                StringBuilder stringBuilder;
                if (hasGroupIdentifier2) {
                    frameSize3--;
                    parsableByteArray.skipBytes(1);
                }
                if (hasDataLength2) {
                    frameSize3 -= 4;
                    parsableByteArray.skipBytes(4);
                }
                frameSize = frameSize3;
                if (isUnsynchronized2) {
                    frameSize = removeUnsynchronization(parsableByteArray, frameSize);
                }
                flags = frameSize;
                if (frameId0 == 84 && frameId1 == 88 && frameId2 == 88 && (i == 2 || frameId3 == 88)) {
                    try {
                        frame = decodeTxxxFrame(parsableByteArray, flags);
                    } catch (UnsupportedEncodingException e) {
                        unsupportedEncodingException = e;
                        i2 = flags;
                        Log.w(TAG, "Unsupported character encoding");
                        parsableByteArray.setPosition(nextFramePosition2);
                        return null;
                    } catch (Throwable th2) {
                        th = th2;
                        i2 = flags;
                        parsableByteArray.setPosition(nextFramePosition2);
                        throw th;
                    }
                }
                if (frameId0 == 84) {
                    frame = decodeTextInformationFrame(parsableByteArray, flags, getFrameId(i, frameId0, frameId1, frameId2, frameId3));
                } else if (frameId0 == 87 && frameId1 == 88 && frameId2 == 88 && (i == 2 || frameId3 == 88)) {
                    frame = decodeWxxxFrame(parsableByteArray, flags);
                } else if (frameId0 == 87) {
                    frame = decodeUrlLinkFrame(parsableByteArray, flags, getFrameId(i, frameId0, frameId1, frameId2, frameId3));
                } else if (frameId0 == 80 && frameId1 == 82 && frameId2 == 73 && frameId3 == 86) {
                    frame = decodePrivFrame(parsableByteArray, flags);
                } else if (frameId0 == 71 && frameId1 == 69 && frameId2 == 79 && (frameId3 == 66 || i == 2)) {
                    frame = decodeGeobFrame(parsableByteArray, flags);
                } else {
                    if (i == 2) {
                        if (frameId0 == 80 && frameId1 == 73 && frameId2 == 67) {
                        }
                        if (frameId0 != 67 && frameId1 == 79 && frameId2 == 77 && (frameId3 == 77 || i == 2)) {
                            frame = decodeCommentFrame(parsableByteArray, flags);
                        } else {
                            if (frameId0 != 67 && frameId1 == 72 && frameId2 == 65 && frameId3 == 80) {
                                frameSize2 = flags;
                                try {
                                    frame = decodeChapterFrame(parsableByteArray, flags, i, unsignedIntFrameSizeHack, frameHeaderSize, framePredicate);
                                } catch (UnsupportedEncodingException e2) {
                                    unsupportedEncodingException = e2;
                                    i2 = frameSize2;
                                    Log.w(TAG, "Unsupported character encoding");
                                    parsableByteArray.setPosition(nextFramePosition2);
                                    return null;
                                } catch (Throwable th22) {
                                    th = th22;
                                    i2 = frameSize2;
                                    parsableByteArray.setPosition(nextFramePosition2);
                                    throw th;
                                }
                            }
                            frameSize2 = flags;
                            if (frameId0 != 67 && frameId1 == 84 && frameId2 == 79 && frameId3 == 67) {
                                frame = decodeChapterTOCFrame(parsableByteArray, frameSize2, i, unsignedIntFrameSizeHack, frameHeaderSize, framePredicate);
                            } else {
                                try {
                                    i2 = frameSize2;
                                    try {
                                        frame = decodeBinaryFrame(parsableByteArray, i2, getFrameId(i, frameId0, frameId1, frameId2, frameId3));
                                        if (frame == null) {
                                            str = TAG;
                                            stringBuilder = new StringBuilder();
                                            stringBuilder.append("Failed to decode frame: id=");
                                            stringBuilder.append(getFrameId(i, frameId0, frameId1, frameId2, frameId3));
                                            stringBuilder.append(", frameSize=");
                                            stringBuilder.append(i2);
                                            Log.w(str, stringBuilder.toString());
                                        }
                                        parsableByteArray.setPosition(nextFramePosition2);
                                        return frame;
                                    } catch (UnsupportedEncodingException e22) {
                                        unsupportedEncodingException = e22;
                                        try {
                                            Log.w(TAG, "Unsupported character encoding");
                                            parsableByteArray.setPosition(nextFramePosition2);
                                            return null;
                                        } catch (Throwable th222) {
                                            th = th222;
                                            parsableByteArray.setPosition(nextFramePosition2);
                                            throw th;
                                        }
                                    }
                                } catch (UnsupportedEncodingException e3) {
                                    i2 = frameSize2;
                                    Log.w(TAG, "Unsupported character encoding");
                                    parsableByteArray.setPosition(nextFramePosition2);
                                    return null;
                                } catch (Throwable th2222) {
                                    i2 = frameSize2;
                                    th = th2222;
                                    parsableByteArray.setPosition(nextFramePosition2);
                                    throw th;
                                }
                            }
                            i2 = frameSize2;
                            if (frame == null) {
                                str = TAG;
                                stringBuilder = new StringBuilder();
                                stringBuilder.append("Failed to decode frame: id=");
                                stringBuilder.append(getFrameId(i, frameId0, frameId1, frameId2, frameId3));
                                stringBuilder.append(", frameSize=");
                                stringBuilder.append(i2);
                                Log.w(str, stringBuilder.toString());
                            }
                            parsableByteArray.setPosition(nextFramePosition2);
                            return frame;
                        }
                    }
                    if (frameId0 == 65) {
                        if (frameId1 == 80) {
                            if (frameId2 == 73) {
                            }
                        }
                    }
                    if (frameId0 != 67) {
                    }
                    if (frameId0 != 67) {
                    }
                    frameSize2 = flags;
                    if (frameId0 != 67) {
                    }
                    i2 = frameSize2;
                    frame = decodeBinaryFrame(parsableByteArray, i2, getFrameId(i, frameId0, frameId1, frameId2, frameId3));
                    if (frame == null) {
                        str = TAG;
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("Failed to decode frame: id=");
                        stringBuilder.append(getFrameId(i, frameId0, frameId1, frameId2, frameId3));
                        stringBuilder.append(", frameSize=");
                        stringBuilder.append(i2);
                        Log.w(str, stringBuilder.toString());
                    }
                    parsableByteArray.setPosition(nextFramePosition2);
                    return frame;
                    frame = decodeApicFrame(parsableByteArray, flags, i);
                }
                i2 = flags;
                if (frame == null) {
                    str = TAG;
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("Failed to decode frame: id=");
                    stringBuilder.append(getFrameId(i, frameId0, frameId1, frameId2, frameId3));
                    stringBuilder.append(", frameSize=");
                    stringBuilder.append(i2);
                    Log.w(str, stringBuilder.toString());
                }
                parsableByteArray.setPosition(nextFramePosition2);
                return frame;
                i2 = flags;
                if (frame == null) {
                    str = TAG;
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("Failed to decode frame: id=");
                    stringBuilder.append(getFrameId(i, frameId0, frameId1, frameId2, frameId3));
                    stringBuilder.append(", frameSize=");
                    stringBuilder.append(i2);
                    Log.w(str, stringBuilder.toString());
                }
                parsableByteArray.setPosition(nextFramePosition2);
                return frame;
            }
        }
        Log.w(TAG, "Skipping unsupported compressed or encrypted frame");
        parsableByteArray.setPosition(nextFramePosition2);
        return null;
    }

    private static TextInformationFrame decodeTxxxFrame(ParsableByteArray id3Data, int frameSize) throws UnsupportedEncodingException {
        if (frameSize < 1) {
            return null;
        }
        String value;
        int encoding = id3Data.readUnsignedByte();
        String charset = getCharsetName(encoding);
        byte[] data = new byte[(frameSize - 1)];
        id3Data.readBytes(data, 0, frameSize - 1);
        int descriptionEndIndex = indexOfEos(data, 0, encoding);
        String description = new String(data, 0, descriptionEndIndex, charset);
        int valueStartIndex = delimiterLength(encoding) + descriptionEndIndex;
        if (valueStartIndex < data.length) {
            value = new String(data, valueStartIndex, indexOfEos(data, valueStartIndex, encoding) - valueStartIndex, charset);
        } else {
            value = TtmlNode.ANONYMOUS_REGION_ID;
        }
        return new TextInformationFrame("TXXX", description, value);
    }

    private static TextInformationFrame decodeTextInformationFrame(ParsableByteArray id3Data, int frameSize, String id) throws UnsupportedEncodingException {
        if (frameSize < 1) {
            return null;
        }
        int encoding = id3Data.readUnsignedByte();
        String charset = getCharsetName(encoding);
        byte[] data = new byte[(frameSize - 1)];
        id3Data.readBytes(data, 0, frameSize - 1);
        return new TextInformationFrame(id, null, new String(data, 0, indexOfEos(data, 0, encoding), charset));
    }

    private static UrlLinkFrame decodeWxxxFrame(ParsableByteArray id3Data, int frameSize) throws UnsupportedEncodingException {
        if (frameSize < 1) {
            return null;
        }
        String url;
        int encoding = id3Data.readUnsignedByte();
        String charset = getCharsetName(encoding);
        byte[] data = new byte[(frameSize - 1)];
        id3Data.readBytes(data, 0, frameSize - 1);
        int descriptionEndIndex = indexOfEos(data, 0, encoding);
        String description = new String(data, 0, descriptionEndIndex, charset);
        int urlStartIndex = delimiterLength(encoding) + descriptionEndIndex;
        if (urlStartIndex < data.length) {
            url = new String(data, urlStartIndex, indexOfZeroByte(data, urlStartIndex) - urlStartIndex, "ISO-8859-1");
        } else {
            url = TtmlNode.ANONYMOUS_REGION_ID;
        }
        return new UrlLinkFrame("WXXX", description, url);
    }

    private static UrlLinkFrame decodeUrlLinkFrame(ParsableByteArray id3Data, int frameSize, String id) throws UnsupportedEncodingException {
        byte[] data = new byte[frameSize];
        id3Data.readBytes(data, 0, frameSize);
        return new UrlLinkFrame(id, null, new String(data, 0, indexOfZeroByte(data, 0), "ISO-8859-1"));
    }

    private static PrivFrame decodePrivFrame(ParsableByteArray id3Data, int frameSize) throws UnsupportedEncodingException {
        byte[] data = new byte[frameSize];
        id3Data.readBytes(data, 0, frameSize);
        int ownerEndIndex = indexOfZeroByte(data, 0);
        return new PrivFrame(new String(data, 0, ownerEndIndex, "ISO-8859-1"), copyOfRangeIfValid(data, ownerEndIndex + 1, data.length));
    }

    private static GeobFrame decodeGeobFrame(ParsableByteArray id3Data, int frameSize) throws UnsupportedEncodingException {
        int encoding = id3Data.readUnsignedByte();
        String charset = getCharsetName(encoding);
        byte[] data = new byte[(frameSize - 1)];
        id3Data.readBytes(data, 0, frameSize - 1);
        int mimeTypeEndIndex = indexOfZeroByte(data, 0);
        String mimeType = new String(data, 0, mimeTypeEndIndex, "ISO-8859-1");
        int filenameStartIndex = mimeTypeEndIndex + 1;
        int filenameEndIndex = indexOfEos(data, filenameStartIndex, encoding);
        String filename = new String(data, filenameStartIndex, filenameEndIndex - filenameStartIndex, charset);
        int descriptionStartIndex = delimiterLength(encoding) + filenameEndIndex;
        int descriptionEndIndex = indexOfEos(data, descriptionStartIndex, encoding);
        return new GeobFrame(mimeType, filename, new String(data, descriptionStartIndex, descriptionEndIndex - descriptionStartIndex, charset), copyOfRangeIfValid(data, delimiterLength(encoding) + descriptionEndIndex, data.length));
    }

    private static ApicFrame decodeApicFrame(ParsableByteArray id3Data, int frameSize, int majorVersion) throws UnsupportedEncodingException {
        int mimeTypeEndIndex;
        String mimeType;
        int encoding = id3Data.readUnsignedByte();
        String charset = getCharsetName(encoding);
        byte[] data = new byte[(frameSize - 1)];
        id3Data.readBytes(data, 0, frameSize - 1);
        StringBuilder stringBuilder;
        if (majorVersion == 2) {
            mimeTypeEndIndex = 2;
            stringBuilder = new StringBuilder();
            stringBuilder.append("image/");
            stringBuilder.append(Util.toLowerInvariant(new String(data, 0, 3, "ISO-8859-1")));
            mimeType = stringBuilder.toString();
            if (mimeType.equals("image/jpg")) {
                mimeType = "image/jpeg";
            }
        } else {
            mimeTypeEndIndex = indexOfZeroByte(data, 0);
            mimeType = Util.toLowerInvariant(new String(data, 0, mimeTypeEndIndex, "ISO-8859-1"));
            if (mimeType.indexOf(47) == -1) {
                stringBuilder = new StringBuilder();
                stringBuilder.append("image/");
                stringBuilder.append(mimeType);
                mimeType = stringBuilder.toString();
            }
        }
        int pictureType = data[mimeTypeEndIndex + 1] & 255;
        int descriptionStartIndex = mimeTypeEndIndex + 2;
        int descriptionEndIndex = indexOfEos(data, descriptionStartIndex, encoding);
        return new ApicFrame(mimeType, new String(data, descriptionStartIndex, descriptionEndIndex - descriptionStartIndex, charset), pictureType, copyOfRangeIfValid(data, delimiterLength(encoding) + descriptionEndIndex, data.length));
    }

    private static CommentFrame decodeCommentFrame(ParsableByteArray id3Data, int frameSize) throws UnsupportedEncodingException {
        if (frameSize < 4) {
            return null;
        }
        String text;
        int encoding = id3Data.readUnsignedByte();
        String charset = getCharsetName(encoding);
        byte[] data = new byte[3];
        id3Data.readBytes(data, 0, 3);
        String language = new String(data, 0, 3);
        data = new byte[(frameSize - 4)];
        id3Data.readBytes(data, 0, frameSize - 4);
        int descriptionEndIndex = indexOfEos(data, 0, encoding);
        String description = new String(data, 0, descriptionEndIndex, charset);
        int textStartIndex = delimiterLength(encoding) + descriptionEndIndex;
        if (textStartIndex < data.length) {
            text = new String(data, textStartIndex, indexOfEos(data, textStartIndex, encoding) - textStartIndex, charset);
        } else {
            text = TtmlNode.ANONYMOUS_REGION_ID;
        }
        return new CommentFrame(language, description, text);
    }

    private static ChapterFrame decodeChapterFrame(ParsableByteArray id3Data, int frameSize, int majorVersion, boolean unsignedIntFrameSizeHack, int frameHeaderSize, FramePredicate framePredicate) throws UnsupportedEncodingException {
        ParsableByteArray parsableByteArray = id3Data;
        int framePosition = id3Data.getPosition();
        int chapterIdEndIndex = indexOfZeroByte(parsableByteArray.data, framePosition);
        String chapterId = new String(parsableByteArray.data, framePosition, chapterIdEndIndex - framePosition, "ISO-8859-1");
        parsableByteArray.setPosition(chapterIdEndIndex + 1);
        int startTime = id3Data.readInt();
        int endTime = id3Data.readInt();
        long startOffset = id3Data.readUnsignedInt();
        if (startOffset == 4294967295L) {
            startOffset = -1;
        }
        long startOffset2 = startOffset;
        startOffset = id3Data.readUnsignedInt();
        if (startOffset == 4294967295L) {
            startOffset = -1;
        }
        long endOffset = startOffset;
        ArrayList<Id3Frame> subFrames = new ArrayList();
        int limit = framePosition + frameSize;
        while (true) {
            int limit2 = limit;
            if (id3Data.getPosition() < limit2) {
                Id3Frame frame = decodeFrame(majorVersion, parsableByteArray, unsignedIntFrameSizeHack, frameHeaderSize, framePredicate);
                if (frame != null) {
                    subFrames.add(frame);
                }
                limit = limit2;
            } else {
                int i = majorVersion;
                boolean z = unsignedIntFrameSizeHack;
                int i2 = frameHeaderSize;
                FramePredicate framePredicate2 = framePredicate;
                Id3Frame[] subFrameArray = new Id3Frame[subFrames.size()];
                subFrames.toArray(subFrameArray);
                return new ChapterFrame(chapterId, startTime, endTime, startOffset2, endOffset, subFrameArray);
            }
        }
    }

    private static ChapterTocFrame decodeChapterTOCFrame(ParsableByteArray id3Data, int frameSize, int majorVersion, boolean unsignedIntFrameSizeHack, int frameHeaderSize, FramePredicate framePredicate) throws UnsupportedEncodingException {
        int i;
        ParsableByteArray parsableByteArray = id3Data;
        int framePosition = id3Data.getPosition();
        int elementIdEndIndex = indexOfZeroByte(parsableByteArray.data, framePosition);
        String elementId = new String(parsableByteArray.data, framePosition, elementIdEndIndex - framePosition, "ISO-8859-1");
        parsableByteArray.setPosition(elementIdEndIndex + 1);
        int ctocFlags = id3Data.readUnsignedByte();
        int i2 = 0;
        boolean isOrdered = true;
        boolean isRoot = (ctocFlags & 2) != 0;
        if ((ctocFlags & 1) == 0) {
            isOrdered = false;
        }
        int childCount = id3Data.readUnsignedByte();
        String[] children = new String[childCount];
        while (true) {
            i = i2;
            if (i >= childCount) {
                break;
            }
            i2 = id3Data.getPosition();
            int endIndex = indexOfZeroByte(parsableByteArray.data, i2);
            children[i] = new String(parsableByteArray.data, i2, endIndex - i2, "ISO-8859-1");
            parsableByteArray.setPosition(endIndex + 1);
            i2 = i + 1;
        }
        ArrayList<Id3Frame> subFrames = new ArrayList();
        i = framePosition + frameSize;
        while (true) {
            int limit = i;
            int framePosition2;
            if (id3Data.getPosition() < limit) {
                framePosition2 = framePosition;
                Id3Frame frame = decodeFrame(majorVersion, parsableByteArray, unsignedIntFrameSizeHack, frameHeaderSize, framePredicate);
                if (frame != null) {
                    subFrames.add(frame);
                }
                i = limit;
                framePosition = framePosition2;
            } else {
                int i3 = majorVersion;
                boolean z = unsignedIntFrameSizeHack;
                i2 = frameHeaderSize;
                FramePredicate framePredicate2 = framePredicate;
                framePosition2 = framePosition;
                Id3Frame[] subFrameArray = new Id3Frame[subFrames.size()];
                subFrames.toArray(subFrameArray);
                return new ChapterTocFrame(elementId, isRoot, isOrdered, children, subFrameArray);
            }
        }
    }

    private static BinaryFrame decodeBinaryFrame(ParsableByteArray id3Data, int frameSize, String id) {
        byte[] frame = new byte[frameSize];
        id3Data.readBytes(frame, 0, frameSize);
        return new BinaryFrame(id, frame);
    }

    private static int removeUnsynchronization(ParsableByteArray data, int length) {
        byte[] bytes = data.data;
        int i = data.getPosition();
        while (i + 1 < length) {
            if ((bytes[i] & 255) == 255 && bytes[i + 1] == (byte) 0) {
                System.arraycopy(bytes, i + 2, bytes, i + 1, (length - i) - 2);
                length--;
            }
            i++;
        }
        return length;
    }

    private static String getCharsetName(int encodingByte) {
        switch (encodingByte) {
            case 0:
                return "ISO-8859-1";
            case 1:
                return C0542C.UTF16_NAME;
            case 2:
                return "UTF-16BE";
            case 3:
                return C0542C.UTF8_NAME;
            default:
                return "ISO-8859-1";
        }
    }

    private static String getFrameId(int majorVersion, int frameId0, int frameId1, int frameId2, int frameId3) {
        if (majorVersion == 2) {
            return String.format(Locale.US, "%c%c%c", new Object[]{Integer.valueOf(frameId0), Integer.valueOf(frameId1), Integer.valueOf(frameId2)});
        }
        return String.format(Locale.US, "%c%c%c%c", new Object[]{Integer.valueOf(frameId0), Integer.valueOf(frameId1), Integer.valueOf(frameId2), Integer.valueOf(frameId3)});
    }

    private static int indexOfEos(byte[] data, int fromIndex, int encoding) {
        int terminationPos = indexOfZeroByte(data, fromIndex);
        if (encoding != 0) {
            if (encoding != 3) {
                while (terminationPos < data.length - 1) {
                    if (terminationPos % 2 == 0 && data[terminationPos + 1] == (byte) 0) {
                        return terminationPos;
                    }
                    terminationPos = indexOfZeroByte(data, terminationPos + 1);
                }
                return data.length;
            }
        }
        return terminationPos;
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
        if (encodingByte != 0) {
            if (encodingByte != 3) {
                return 2;
            }
        }
        return 1;
    }

    private static byte[] copyOfRangeIfValid(byte[] data, int from, int to) {
        if (to <= from) {
            return new byte[0];
        }
        return Arrays.copyOfRange(data, from, to);
    }
}
