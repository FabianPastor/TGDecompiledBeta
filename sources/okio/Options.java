package okio;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.RandomAccess;

public final class Options extends AbstractList<ByteString> implements RandomAccess {
    final ByteString[] byteStrings;
    final int[] trie;

    private Options(ByteString[] byteStringArr, int[] iArr) {
        this.byteStrings = byteStringArr;
        this.trie = iArr;
    }

    public static Options of(ByteString... byteStringArr) {
        int i = 0;
        if (byteStringArr.length == 0) {
            return new Options(new ByteString[0], new int[]{0, -1});
        }
        int i2;
        ArrayList arrayList = new ArrayList(Arrays.asList(byteStringArr));
        Collections.sort(arrayList);
        ArrayList arrayList2 = new ArrayList();
        for (i2 = 0; i2 < arrayList.size(); i2++) {
            arrayList2.add(Integer.valueOf(-1));
        }
        for (i2 = 0; i2 < arrayList.size(); i2++) {
            arrayList2.set(Collections.binarySearch(arrayList, byteStringArr[i2]), Integer.valueOf(i2));
        }
        if (((ByteString) arrayList.get(0)).size() != 0) {
            i2 = 0;
            while (i2 < arrayList.size()) {
                ByteString byteString = (ByteString) arrayList.get(i2);
                int i3 = i2 + 1;
                int i4 = i3;
                while (i4 < arrayList.size()) {
                    ByteString byteString2 = (ByteString) arrayList.get(i4);
                    if (!byteString2.startsWith(byteString)) {
                        continue;
                        break;
                    } else if (byteString2.size() == byteString.size()) {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("duplicate option: ");
                        stringBuilder.append(byteString2);
                        throw new IllegalArgumentException(stringBuilder.toString());
                    } else if (((Integer) arrayList2.get(i4)).intValue() > ((Integer) arrayList2.get(i2)).intValue()) {
                        arrayList.remove(i4);
                        arrayList2.remove(i4);
                    } else {
                        i4++;
                    }
                }
                i2 = i3;
            }
            Buffer buffer = new Buffer();
            buildTrieRecursive(0, buffer, 0, arrayList, 0, arrayList.size(), arrayList2);
            int[] iArr = new int[intCount(buffer)];
            while (i < iArr.length) {
                iArr[i] = buffer.readInt();
                i++;
            }
            if (buffer.exhausted()) {
                return new Options((ByteString[]) byteStringArr.clone(), iArr);
            }
            throw new AssertionError();
        }
        throw new IllegalArgumentException("the empty byte string is not a supported option");
    }

    private static void buildTrieRecursive(long j, Buffer buffer, int i, List<ByteString> list, int i2, int i3, List<Integer> list2) {
        Buffer buffer2 = buffer;
        int i4 = i;
        List<ByteString> list3 = list;
        int i5 = i2;
        int i6 = i3;
        List<Integer> list4 = list2;
        if (i5 < i6) {
            int i7 = i5;
            while (i7 < i6) {
                if (((ByteString) list3.get(i7)).size() >= i4) {
                    i7++;
                } else {
                    throw new AssertionError();
                }
            }
            ByteString byteString = (ByteString) list.get(i2);
            ByteString byteString2 = (ByteString) list3.get(i6 - 1);
            int i8 = -1;
            if (i4 == byteString.size()) {
                i8 = ((Integer) list4.get(i5)).intValue();
                i5++;
                byteString = (ByteString) list3.get(i5);
            }
            int i9 = i5;
            Buffer buffer3;
            int i10;
            int i11;
            if (byteString.getByte(i4) != byteString2.getByte(i4)) {
                Buffer buffer4;
                i7 = 1;
                for (i5 = i9 + 1; i5 < i6; i5++) {
                    if (((ByteString) list3.get(i5 - 1)).getByte(i4) != ((ByteString) list3.get(i5)).getByte(i4)) {
                        i7++;
                    }
                }
                long intCount = ((j + ((long) intCount(buffer))) + 2) + ((long) (i7 * 2));
                buffer2.writeInt(i7);
                buffer2.writeInt(i8);
                i5 = i9;
                while (i5 < i6) {
                    byte b = ((ByteString) list3.get(i5)).getByte(i4);
                    if (i5 == i9 || b != ((ByteString) list3.get(i5 - 1)).getByte(i4)) {
                        buffer2.writeInt(b & 255);
                    }
                    i5++;
                }
                buffer3 = new Buffer();
                i10 = i9;
                while (i10 < i6) {
                    int i12;
                    int i13;
                    byte b2 = ((ByteString) list3.get(i10)).getByte(i4);
                    i7 = i10 + 1;
                    for (i11 = i7; i11 < i6; i11++) {
                        if (b2 != ((ByteString) list3.get(i11)).getByte(i4)) {
                            i12 = i11;
                            break;
                        }
                    }
                    i12 = i6;
                    if (i7 == i12 && i4 + 1 == ((ByteString) list3.get(i10)).size()) {
                        buffer2.writeInt(((Integer) list4.get(i10)).intValue());
                        i13 = i12;
                        buffer4 = buffer3;
                    } else {
                        buffer2.writeInt((int) ((((long) intCount(buffer3)) + intCount) * -1));
                        i13 = i12;
                        buffer4 = buffer3;
                        buildTrieRecursive(intCount, buffer3, i4 + 1, list, i10, i12, list2);
                    }
                    buffer3 = buffer4;
                    i10 = i13;
                }
                buffer4 = buffer3;
                buffer2.write(buffer4, buffer4.size());
                return;
            }
            i10 = Math.min(byteString.size(), byteString2.size());
            i5 = i4;
            int i14 = 0;
            while (i5 < i10 && byteString.getByte(i5) == byteString2.getByte(i5)) {
                i14++;
                i5++;
            }
            long intCount2 = 1 + (((j + ((long) intCount(buffer))) + 2) + ((long) i14));
            buffer2.writeInt(-i14);
            buffer2.writeInt(i8);
            i5 = i4;
            while (true) {
                i11 = i4 + i14;
                if (i5 >= i11) {
                    break;
                }
                buffer2.writeInt(byteString.getByte(i5) & 255);
                i5++;
            }
            if (i9 + 1 != i6) {
                buffer3 = new Buffer();
                buffer2.writeInt((int) ((((long) intCount(buffer3)) + intCount2) * -1));
                buildTrieRecursive(intCount2, buffer3, i11, list, i9, i3, list2);
                buffer2.write(buffer3, buffer3.size());
                return;
            } else if (i11 == ((ByteString) list3.get(i9)).size()) {
                buffer2.writeInt(((Integer) list4.get(i9)).intValue());
                return;
            } else {
                throw new AssertionError();
            }
        }
        throw new AssertionError();
    }

    public ByteString get(int i) {
        return this.byteStrings[i];
    }

    public final int size() {
        return this.byteStrings.length;
    }

    private static int intCount(Buffer buffer) {
        return (int) (buffer.size() / 4);
    }
}
