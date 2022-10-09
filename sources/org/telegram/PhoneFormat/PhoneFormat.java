package org.telegram.PhoneFormat;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
/* loaded from: classes.dex */
public class PhoneFormat {
    private static volatile PhoneFormat Instance;
    public ByteBuffer buffer;
    public HashMap<String, ArrayList<String>> callingCodeCountries;
    public HashMap<String, CallingCodeInfo> callingCodeData;
    public HashMap<String, Integer> callingCodeOffsets;
    public HashMap<String, String> countryCallingCode;
    public byte[] data;
    public String defaultCallingCode;
    public String defaultCountry;
    private boolean initialzed = false;

    public static PhoneFormat getInstance() {
        PhoneFormat phoneFormat = Instance;
        if (phoneFormat == null) {
            synchronized (PhoneFormat.class) {
                phoneFormat = Instance;
                if (phoneFormat == null) {
                    phoneFormat = new PhoneFormat();
                    Instance = phoneFormat;
                }
            }
        }
        return phoneFormat;
    }

    public static String strip(String str) {
        StringBuilder sb = new StringBuilder(str);
        for (int length = sb.length() - 1; length >= 0; length--) {
            if (!"0123456789+*#".contains(sb.substring(length, length + 1))) {
                sb.deleteCharAt(length);
            }
        }
        return sb.toString();
    }

    public static String stripExceptNumbers(String str, boolean z) {
        if (str == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder(str);
        String str2 = "NUM";
        if (z) {
            str2 = str2 + "+";
        }
        for (int length = sb.length() - 1; length >= 0; length--) {
            if (!str2.contains(sb.substring(length, length + 1))) {
                sb.deleteCharAt(length);
            }
        }
        return sb.toString();
    }

    public static String stripExceptNumbers(String str) {
        return stripExceptNumbers(str, false);
    }

    public PhoneFormat() {
        init(null);
    }

    public void init(String str) {
        InputStream inputStream;
        ByteArrayOutputStream byteArrayOutputStream;
        ByteArrayOutputStream byteArrayOutputStream2 = null;
        try {
            inputStream = ApplicationLoader.applicationContext.getAssets().open("PhoneFormats.dat");
            try {
                try {
                    byteArrayOutputStream = new ByteArrayOutputStream();
                } catch (Exception e) {
                    e = e;
                }
            } catch (Throwable th) {
                th = th;
            }
        } catch (Exception e2) {
            e = e2;
            inputStream = null;
        } catch (Throwable th2) {
            th = th2;
            inputStream = null;
        }
        try {
            byte[] bArr = new byte[1024];
            while (true) {
                int read = inputStream.read(bArr, 0, 1024);
                if (read == -1) {
                    break;
                }
                byteArrayOutputStream.write(bArr, 0, read);
            }
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            this.data = byteArray;
            ByteBuffer wrap = ByteBuffer.wrap(byteArray);
            this.buffer = wrap;
            wrap.order(ByteOrder.LITTLE_ENDIAN);
            try {
                byteArrayOutputStream.close();
            } catch (Exception e3) {
                FileLog.e(e3);
            }
            try {
                inputStream.close();
            } catch (Exception e4) {
                FileLog.e(e4);
            }
            if (str != null && str.length() != 0) {
                this.defaultCountry = str;
            } else {
                this.defaultCountry = Locale.getDefault().getCountry().toLowerCase();
            }
            this.callingCodeOffsets = new HashMap<>(255);
            this.callingCodeCountries = new HashMap<>(255);
            this.callingCodeData = new HashMap<>(10);
            this.countryCallingCode = new HashMap<>(255);
            parseDataHeader();
            this.initialzed = true;
        } catch (Exception e5) {
            e = e5;
            byteArrayOutputStream2 = byteArrayOutputStream;
            e.printStackTrace();
            if (byteArrayOutputStream2 != null) {
                try {
                    byteArrayOutputStream2.close();
                } catch (Exception e6) {
                    FileLog.e(e6);
                }
            }
            if (inputStream == null) {
                return;
            }
            try {
                inputStream.close();
            } catch (Exception e7) {
                FileLog.e(e7);
            }
        } catch (Throwable th3) {
            th = th3;
            byteArrayOutputStream2 = byteArrayOutputStream;
            if (byteArrayOutputStream2 != null) {
                try {
                    byteArrayOutputStream2.close();
                } catch (Exception e8) {
                    FileLog.e(e8);
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e9) {
                    FileLog.e(e9);
                }
            }
            throw th;
        }
    }

    public CallingCodeInfo findCallingCodeInfo(String str) {
        CallingCodeInfo callingCodeInfo = null;
        int i = 0;
        while (i < 3 && i < str.length()) {
            i++;
            callingCodeInfo = callingCodeInfo(str.substring(0, i));
            if (callingCodeInfo != null) {
                break;
            }
        }
        return callingCodeInfo;
    }

    public String format(String str) {
        if (!this.initialzed) {
            return str;
        }
        try {
            String strip = strip(str);
            if (strip.startsWith("+")) {
                String substring = strip.substring(1);
                CallingCodeInfo findCallingCodeInfo = findCallingCodeInfo(substring);
                if (findCallingCodeInfo == null) {
                    return str;
                }
                String format = findCallingCodeInfo.format(substring);
                return "+" + format;
            }
            CallingCodeInfo callingCodeInfo = callingCodeInfo(this.defaultCallingCode);
            if (callingCodeInfo == null) {
                return str;
            }
            String matchingAccessCode = callingCodeInfo.matchingAccessCode(strip);
            if (matchingAccessCode != null) {
                String substring2 = strip.substring(matchingAccessCode.length());
                CallingCodeInfo findCallingCodeInfo2 = findCallingCodeInfo(substring2);
                if (findCallingCodeInfo2 != null) {
                    substring2 = findCallingCodeInfo2.format(substring2);
                }
                return substring2.length() == 0 ? matchingAccessCode : String.format("%s %s", matchingAccessCode, substring2);
            }
            return callingCodeInfo.format(strip);
        } catch (Exception e) {
            FileLog.e(e);
            return str;
        }
    }

    int value32(int i) {
        if (i + 4 <= this.data.length) {
            this.buffer.position(i);
            return this.buffer.getInt();
        }
        return 0;
    }

    short value16(int i) {
        if (i + 2 <= this.data.length) {
            this.buffer.position(i);
            return this.buffer.getShort();
        }
        return (short) 0;
    }

    /* JADX WARN: Code restructure failed: missing block: B:11:0x0015, code lost:
        return new java.lang.String(r2, r5, r1);
     */
    /* JADX WARN: Code restructure failed: missing block: B:7:0x000c, code lost:
        r1 = r1 - r5;
     */
    /* JADX WARN: Code restructure failed: missing block: B:8:0x000d, code lost:
        if (r5 != r1) goto L13;
     */
    /* JADX WARN: Code restructure failed: missing block: B:9:0x000f, code lost:
        return "";
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public java.lang.String valueString(int r5) {
        /*
            r4 = this;
            java.lang.String r0 = ""
            r1 = r5
        L3:
            byte[] r2 = r4.data     // Catch: java.lang.Exception -> L1a
            int r3 = r2.length     // Catch: java.lang.Exception -> L1a
            if (r1 >= r3) goto L19
            r3 = r2[r1]     // Catch: java.lang.Exception -> L1a
            if (r3 != 0) goto L16
            int r1 = r1 - r5
            if (r5 != r1) goto L10
            return r0
        L10:
            java.lang.String r3 = new java.lang.String     // Catch: java.lang.Exception -> L1a
            r3.<init>(r2, r5, r1)     // Catch: java.lang.Exception -> L1a
            return r3
        L16:
            int r1 = r1 + 1
            goto L3
        L19:
            return r0
        L1a:
            r5 = move-exception
            r5.printStackTrace()
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.PhoneFormat.PhoneFormat.valueString(int):java.lang.String");
    }

    public CallingCodeInfo callingCodeInfo(String str) {
        Integer num;
        byte[] bArr;
        int i;
        PhoneFormat phoneFormat = this;
        CallingCodeInfo callingCodeInfo = phoneFormat.callingCodeData.get(str);
        if (callingCodeInfo != null || (num = phoneFormat.callingCodeOffsets.get(str)) == null) {
            return callingCodeInfo;
        }
        byte[] bArr2 = phoneFormat.data;
        int intValue = num.intValue();
        CallingCodeInfo callingCodeInfo2 = new CallingCodeInfo();
        callingCodeInfo2.callingCode = str;
        phoneFormat.callingCodeCountries.get(str);
        phoneFormat.callingCodeData.put(str, callingCodeInfo2);
        short value16 = phoneFormat.value16(intValue);
        int i2 = 2;
        int i3 = intValue + 2 + 2;
        short value162 = phoneFormat.value16(i3);
        int i4 = i3 + 2 + 2;
        short value163 = phoneFormat.value16(i4);
        int i5 = i4 + 2 + 2;
        ArrayList<String> arrayList = new ArrayList<>(5);
        while (true) {
            String valueString = phoneFormat.valueString(i5);
            if (valueString.length() == 0) {
                break;
            }
            arrayList.add(valueString);
            i5 += valueString.length() + 1;
        }
        callingCodeInfo2.trunkPrefixes = arrayList;
        int i6 = i5 + 1;
        ArrayList<String> arrayList2 = new ArrayList<>(5);
        while (true) {
            String valueString2 = phoneFormat.valueString(i6);
            if (valueString2.length() == 0) {
                break;
            }
            arrayList2.add(valueString2);
            i6 += valueString2.length() + 1;
        }
        callingCodeInfo2.intlPrefixes = arrayList2;
        ArrayList<RuleSet> arrayList3 = new ArrayList<>(value163);
        int i7 = intValue + value16;
        int i8 = i7;
        int i9 = 0;
        while (i9 < value163) {
            RuleSet ruleSet = new RuleSet();
            ruleSet.matchLen = phoneFormat.value16(i8);
            int i10 = i8 + i2;
            short value164 = phoneFormat.value16(i10);
            i8 = i10 + i2;
            ArrayList<PhoneRule> arrayList4 = new ArrayList<>(value164);
            int i11 = 0;
            while (i11 < value164) {
                PhoneRule phoneRule = new PhoneRule();
                phoneRule.minVal = phoneFormat.value32(i8);
                int i12 = i8 + 4;
                phoneRule.maxVal = phoneFormat.value32(i12);
                int i13 = i12 + 4;
                int i14 = i13 + 1;
                byte b = bArr2[i13];
                int i15 = i14 + 1;
                phoneRule.maxLen = bArr2[i14];
                int i16 = i15 + 1;
                byte b2 = bArr2[i15];
                int i17 = i16 + 1;
                byte b3 = bArr2[i16];
                int i18 = i17 + 1;
                phoneRule.flag12 = bArr2[i17];
                int i19 = i18 + 1;
                byte b4 = bArr2[i18];
                short value165 = phoneFormat.value16(i19);
                i8 = i19 + i2;
                String valueString3 = phoneFormat.valueString(i7 + value162 + value165);
                phoneRule.format = valueString3;
                int indexOf = valueString3.indexOf("[[");
                if (indexOf != -1) {
                    bArr = bArr2;
                    i = i7;
                    i2 = 2;
                    phoneRule.format = String.format("%s%s", phoneRule.format.substring(0, indexOf), phoneRule.format.substring(phoneRule.format.indexOf("]]") + 2));
                } else {
                    bArr = bArr2;
                    i = i7;
                    i2 = 2;
                }
                arrayList4.add(phoneRule);
                i11++;
                phoneFormat = this;
                bArr2 = bArr;
                i7 = i;
            }
            ruleSet.rules = arrayList4;
            arrayList3.add(ruleSet);
            i9++;
            phoneFormat = this;
            i7 = i7;
        }
        callingCodeInfo2.ruleSets = arrayList3;
        return callingCodeInfo2;
    }

    public void parseDataHeader() {
        int value32 = value32(0);
        int i = (value32 * 12) + 4;
        int i2 = 4;
        for (int i3 = 0; i3 < value32; i3++) {
            String valueString = valueString(i2);
            int i4 = i2 + 4;
            String valueString2 = valueString(i4);
            int i5 = i4 + 4;
            int value322 = value32(i5) + i;
            i2 = i5 + 4;
            if (valueString2.equals(this.defaultCountry)) {
                this.defaultCallingCode = valueString;
            }
            this.countryCallingCode.put(valueString2, valueString);
            this.callingCodeOffsets.put(valueString, Integer.valueOf(value322));
            ArrayList<String> arrayList = this.callingCodeCountries.get(valueString);
            if (arrayList == null) {
                arrayList = new ArrayList<>();
                this.callingCodeCountries.put(valueString, arrayList);
            }
            arrayList.add(valueString2);
        }
        String str = this.defaultCallingCode;
        if (str != null) {
            callingCodeInfo(str);
        }
    }
}
