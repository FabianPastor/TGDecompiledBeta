package org.telegram.PhoneFormat;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.FileLog;

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
        init((String) null);
    }

    /* JADX WARNING: Removed duplicated region for block: B:40:0x0097 A[SYNTHETIC, Splitter:B:40:0x0097] */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x00a1 A[SYNTHETIC, Splitter:B:45:0x00a1] */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x00ad A[SYNTHETIC, Splitter:B:51:0x00ad] */
    /* JADX WARNING: Removed duplicated region for block: B:56:0x00b7 A[SYNTHETIC, Splitter:B:56:0x00b7] */
    /* JADX WARNING: Removed duplicated region for block: B:63:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void init(java.lang.String r8) {
        /*
            r7 = this;
            r0 = 0
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0090, all -> 0x008d }
            android.content.res.AssetManager r1 = r1.getAssets()     // Catch:{ Exception -> 0x0090, all -> 0x008d }
            java.lang.String r2 = "PhoneFormats.dat"
            java.io.InputStream r1 = r1.open(r2)     // Catch:{ Exception -> 0x0090, all -> 0x008d }
            java.io.ByteArrayOutputStream r2 = new java.io.ByteArrayOutputStream     // Catch:{ Exception -> 0x008b }
            r2.<init>()     // Catch:{ Exception -> 0x008b }
            r0 = 1024(0x400, float:1.435E-42)
            byte[] r3 = new byte[r0]     // Catch:{ Exception -> 0x0088, all -> 0x0085 }
        L_0x0016:
            r4 = 0
            int r5 = r1.read(r3, r4, r0)     // Catch:{ Exception -> 0x0088, all -> 0x0085 }
            r6 = -1
            if (r5 == r6) goto L_0x0022
            r2.write(r3, r4, r5)     // Catch:{ Exception -> 0x0088, all -> 0x0085 }
            goto L_0x0016
        L_0x0022:
            byte[] r0 = r2.toByteArray()     // Catch:{ Exception -> 0x0088, all -> 0x0085 }
            r7.data = r0     // Catch:{ Exception -> 0x0088, all -> 0x0085 }
            java.nio.ByteBuffer r0 = java.nio.ByteBuffer.wrap(r0)     // Catch:{ Exception -> 0x0088, all -> 0x0085 }
            r7.buffer = r0     // Catch:{ Exception -> 0x0088, all -> 0x0085 }
            java.nio.ByteOrder r3 = java.nio.ByteOrder.LITTLE_ENDIAN     // Catch:{ Exception -> 0x0088, all -> 0x0085 }
            r0.order(r3)     // Catch:{ Exception -> 0x0088, all -> 0x0085 }
            r2.close()     // Catch:{ Exception -> 0x0037 }
            goto L_0x003b
        L_0x0037:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x003b:
            if (r1 == 0) goto L_0x0045
            r1.close()     // Catch:{ Exception -> 0x0041 }
            goto L_0x0045
        L_0x0041:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0045:
            if (r8 == 0) goto L_0x0050
            int r0 = r8.length()
            if (r0 == 0) goto L_0x0050
            r7.defaultCountry = r8
            goto L_0x005e
        L_0x0050:
            java.util.Locale r8 = java.util.Locale.getDefault()
            java.lang.String r8 = r8.getCountry()
            java.lang.String r8 = r8.toLowerCase()
            r7.defaultCountry = r8
        L_0x005e:
            java.util.HashMap r8 = new java.util.HashMap
            r0 = 255(0xff, float:3.57E-43)
            r8.<init>(r0)
            r7.callingCodeOffsets = r8
            java.util.HashMap r8 = new java.util.HashMap
            r8.<init>(r0)
            r7.callingCodeCountries = r8
            java.util.HashMap r8 = new java.util.HashMap
            r1 = 10
            r8.<init>(r1)
            r7.callingCodeData = r8
            java.util.HashMap r8 = new java.util.HashMap
            r8.<init>(r0)
            r7.countryCallingCode = r8
            r7.parseDataHeader()
            r8 = 1
            r7.initialzed = r8
            return
        L_0x0085:
            r8 = move-exception
            r0 = r2
            goto L_0x00ab
        L_0x0088:
            r8 = move-exception
            r0 = r2
            goto L_0x0092
        L_0x008b:
            r8 = move-exception
            goto L_0x0092
        L_0x008d:
            r8 = move-exception
            r1 = r0
            goto L_0x00ab
        L_0x0090:
            r8 = move-exception
            r1 = r0
        L_0x0092:
            r8.printStackTrace()     // Catch:{ all -> 0x00aa }
            if (r0 == 0) goto L_0x009f
            r0.close()     // Catch:{ Exception -> 0x009b }
            goto L_0x009f
        L_0x009b:
            r8 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r8)
        L_0x009f:
            if (r1 == 0) goto L_0x00a9
            r1.close()     // Catch:{ Exception -> 0x00a5 }
            goto L_0x00a9
        L_0x00a5:
            r8 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r8)
        L_0x00a9:
            return
        L_0x00aa:
            r8 = move-exception
        L_0x00ab:
            if (r0 == 0) goto L_0x00b5
            r0.close()     // Catch:{ Exception -> 0x00b1 }
            goto L_0x00b5
        L_0x00b1:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x00b5:
            if (r1 == 0) goto L_0x00bf
            r1.close()     // Catch:{ Exception -> 0x00bb }
            goto L_0x00bf
        L_0x00bb:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x00bf:
            goto L_0x00c1
        L_0x00c0:
            throw r8
        L_0x00c1:
            goto L_0x00c0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.PhoneFormat.PhoneFormat.init(java.lang.String):void");
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
            if (matchingAccessCode == null) {
                return callingCodeInfo.format(strip);
            }
            String substring2 = strip.substring(matchingAccessCode.length());
            CallingCodeInfo findCallingCodeInfo2 = findCallingCodeInfo(substring2);
            if (findCallingCodeInfo2 != null) {
                substring2 = findCallingCodeInfo2.format(substring2);
            }
            if (substring2.length() == 0) {
                return matchingAccessCode;
            }
            return String.format("%s %s", new Object[]{matchingAccessCode, substring2});
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return str;
        }
    }

    /* access modifiers changed from: package-private */
    public int value32(int i) {
        if (i + 4 > this.data.length) {
            return 0;
        }
        this.buffer.position(i);
        return this.buffer.getInt();
    }

    /* access modifiers changed from: package-private */
    public short value16(int i) {
        if (i + 2 > this.data.length) {
            return 0;
        }
        this.buffer.position(i);
        return this.buffer.getShort();
    }

    public String valueString(int i) {
        int i2 = i;
        while (i2 < this.data.length) {
            try {
                if (this.data[i2] == 0) {
                    int i3 = i2 - i;
                    if (i == i3) {
                        return "";
                    }
                    return new String(this.data, i, i3);
                }
                i2++;
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        }
        return "";
    }

    public CallingCodeInfo callingCodeInfo(String str) {
        Integer num;
        int i;
        byte[] bArr;
        PhoneFormat phoneFormat = this;
        String str2 = str;
        CallingCodeInfo callingCodeInfo = phoneFormat.callingCodeData.get(str2);
        if (callingCodeInfo != null || (num = phoneFormat.callingCodeOffsets.get(str2)) == null) {
            return callingCodeInfo;
        }
        byte[] bArr2 = phoneFormat.data;
        int intValue = num.intValue();
        CallingCodeInfo callingCodeInfo2 = new CallingCodeInfo();
        callingCodeInfo2.callingCode = str2;
        ArrayList arrayList = phoneFormat.callingCodeCountries.get(str2);
        phoneFormat.callingCodeData.put(str2, callingCodeInfo2);
        short value16 = phoneFormat.value16(intValue);
        int i2 = 2;
        int i3 = intValue + 2 + 2;
        short value162 = phoneFormat.value16(i3);
        int i4 = i3 + 2 + 2;
        short value163 = phoneFormat.value16(i4);
        int i5 = i4 + 2 + 2;
        ArrayList<String> arrayList2 = new ArrayList<>(5);
        while (true) {
            String valueString = phoneFormat.valueString(i5);
            if (valueString.length() == 0) {
                break;
            }
            arrayList2.add(valueString);
            i5 += valueString.length() + 1;
        }
        callingCodeInfo2.trunkPrefixes = arrayList2;
        int i6 = i5 + 1;
        ArrayList<String> arrayList3 = new ArrayList<>(5);
        while (true) {
            String valueString2 = phoneFormat.valueString(i6);
            if (valueString2.length() == 0) {
                break;
            }
            arrayList3.add(valueString2);
            i6 += valueString2.length() + 1;
        }
        callingCodeInfo2.intlPrefixes = arrayList3;
        ArrayList<RuleSet> arrayList4 = new ArrayList<>(value163);
        int i7 = intValue + value16;
        int i8 = i7;
        int i9 = 0;
        while (i9 < value163) {
            RuleSet ruleSet = new RuleSet();
            ruleSet.matchLen = phoneFormat.value16(i8);
            int i10 = i8 + i2;
            short value164 = phoneFormat.value16(i10);
            i8 = i10 + i2;
            ArrayList<PhoneRule> arrayList5 = new ArrayList<>(value164);
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
                    phoneRule.format = String.format("%s%s", new Object[]{phoneRule.format.substring(0, indexOf), phoneRule.format.substring(phoneRule.format.indexOf("]]") + 2)});
                } else {
                    bArr = bArr2;
                    i = i7;
                    i2 = 2;
                }
                arrayList5.add(phoneRule);
                boolean z = phoneRule.hasIntlPrefix;
                boolean z2 = phoneRule.hasTrunkPrefix;
                i11++;
                phoneFormat = this;
                bArr2 = bArr;
                i7 = i;
            }
            byte[] bArr3 = bArr2;
            ruleSet.rules = arrayList5;
            arrayList4.add(ruleSet);
            i9++;
            phoneFormat = this;
            i7 = i7;
        }
        callingCodeInfo2.ruleSets = arrayList4;
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
            ArrayList arrayList = this.callingCodeCountries.get(valueString);
            if (arrayList == null) {
                arrayList = new ArrayList();
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
