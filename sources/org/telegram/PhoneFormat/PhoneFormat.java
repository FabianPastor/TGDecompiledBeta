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
        StringBuilder stringBuilder = new StringBuilder(str);
        for (int length = stringBuilder.length() - 1; length >= 0; length--) {
            if (!"0123456789+*#".contains(stringBuilder.substring(length, length + 1))) {
                stringBuilder.deleteCharAt(length);
            }
        }
        return stringBuilder.toString();
    }

    public static String stripExceptNumbers(String str, boolean z) {
        if (str == null) {
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder(str);
        str = "NUM";
        if (z) {
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append(str);
            stringBuilder2.append("+");
            str = stringBuilder2.toString();
        }
        for (int length = stringBuilder.length() - 1; length >= 0; length--) {
            if (!str.contains(stringBuilder.substring(length, length + 1))) {
                stringBuilder.deleteCharAt(length);
            }
        }
        return stringBuilder.toString();
    }

    public static String stripExceptNumbers(String str) {
        return stripExceptNumbers(str, false);
    }

    public PhoneFormat() {
        init(null);
    }

    public PhoneFormat(String str) {
        init(str);
    }

    /* JADX WARNING: Removed duplicated region for block: B:39:0x009b A:{SYNTHETIC, Splitter:B:39:0x009b} */
    /* JADX WARNING: Removed duplicated region for block: B:44:0x00a5 A:{SYNTHETIC, Splitter:B:44:0x00a5} */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x009b A:{SYNTHETIC, Splitter:B:39:0x009b} */
    /* JADX WARNING: Removed duplicated region for block: B:44:0x00a5 A:{SYNTHETIC, Splitter:B:44:0x00a5} */
    /* JADX WARNING: Removed duplicated region for block: B:52:0x00b2 A:{SYNTHETIC, Splitter:B:52:0x00b2} */
    /* JADX WARNING: Removed duplicated region for block: B:57:0x00bc A:{SYNTHETIC, Splitter:B:57:0x00bc} */
    /* JADX WARNING: Removed duplicated region for block: B:52:0x00b2 A:{SYNTHETIC, Splitter:B:52:0x00b2} */
    /* JADX WARNING: Removed duplicated region for block: B:57:0x00bc A:{SYNTHETIC, Splitter:B:57:0x00bc} */
    public void init(java.lang.String r8) {
        /*
        r7 = this;
        r0 = 0;
        r1 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0094, all -> 0x0090 }
        r1 = r1.getAssets();	 Catch:{ Exception -> 0x0094, all -> 0x0090 }
        r2 = "PhoneFormats.dat";
        r1 = r1.open(r2);	 Catch:{ Exception -> 0x0094, all -> 0x0090 }
        r2 = new java.io.ByteArrayOutputStream;	 Catch:{ Exception -> 0x008e }
        r2.<init>();	 Catch:{ Exception -> 0x008e }
        r0 = 1024; // 0x400 float:1.435E-42 double:5.06E-321;
        r3 = new byte[r0];	 Catch:{ Exception -> 0x008b, all -> 0x0089 }
    L_0x0016:
        r4 = 0;
        r5 = r1.read(r3, r4, r0);	 Catch:{ Exception -> 0x008b, all -> 0x0089 }
        r6 = -1;
        if (r5 == r6) goto L_0x0022;
    L_0x001e:
        r2.write(r3, r4, r5);	 Catch:{ Exception -> 0x008b, all -> 0x0089 }
        goto L_0x0016;
    L_0x0022:
        r0 = r2.toByteArray();	 Catch:{ Exception -> 0x008b, all -> 0x0089 }
        r7.data = r0;	 Catch:{ Exception -> 0x008b, all -> 0x0089 }
        r0 = r7.data;	 Catch:{ Exception -> 0x008b, all -> 0x0089 }
        r0 = java.nio.ByteBuffer.wrap(r0);	 Catch:{ Exception -> 0x008b, all -> 0x0089 }
        r7.buffer = r0;	 Catch:{ Exception -> 0x008b, all -> 0x0089 }
        r0 = r7.buffer;	 Catch:{ Exception -> 0x008b, all -> 0x0089 }
        r3 = java.nio.ByteOrder.LITTLE_ENDIAN;	 Catch:{ Exception -> 0x008b, all -> 0x0089 }
        r0.order(r3);	 Catch:{ Exception -> 0x008b, all -> 0x0089 }
        r2.close();	 Catch:{ Exception -> 0x003b }
        goto L_0x003f;
    L_0x003b:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x003f:
        if (r1 == 0) goto L_0x0049;
    L_0x0041:
        r1.close();	 Catch:{ Exception -> 0x0045 }
        goto L_0x0049;
    L_0x0045:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x0049:
        if (r8 == 0) goto L_0x0054;
    L_0x004b:
        r0 = r8.length();
        if (r0 == 0) goto L_0x0054;
    L_0x0051:
        r7.defaultCountry = r8;
        goto L_0x0062;
    L_0x0054:
        r8 = java.util.Locale.getDefault();
        r8 = r8.getCountry();
        r8 = r8.toLowerCase();
        r7.defaultCountry = r8;
    L_0x0062:
        r8 = new java.util.HashMap;
        r0 = 255; // 0xff float:3.57E-43 double:1.26E-321;
        r8.<init>(r0);
        r7.callingCodeOffsets = r8;
        r8 = new java.util.HashMap;
        r8.<init>(r0);
        r7.callingCodeCountries = r8;
        r8 = new java.util.HashMap;
        r1 = 10;
        r8.<init>(r1);
        r7.callingCodeData = r8;
        r8 = new java.util.HashMap;
        r8.<init>(r0);
        r7.countryCallingCode = r8;
        r7.parseDataHeader();
        r8 = 1;
        r7.initialzed = r8;
        return;
    L_0x0089:
        r8 = move-exception;
        goto L_0x00b0;
    L_0x008b:
        r8 = move-exception;
        r0 = r2;
        goto L_0x0096;
    L_0x008e:
        r8 = move-exception;
        goto L_0x0096;
    L_0x0090:
        r8 = move-exception;
        r1 = r0;
        r2 = r1;
        goto L_0x00b0;
    L_0x0094:
        r8 = move-exception;
        r1 = r0;
    L_0x0096:
        r8.printStackTrace();	 Catch:{ all -> 0x00ae }
        if (r0 == 0) goto L_0x00a3;
    L_0x009b:
        r0.close();	 Catch:{ Exception -> 0x009f }
        goto L_0x00a3;
    L_0x009f:
        r8 = move-exception;
        org.telegram.messenger.FileLog.e(r8);
    L_0x00a3:
        if (r1 == 0) goto L_0x00ad;
    L_0x00a5:
        r1.close();	 Catch:{ Exception -> 0x00a9 }
        goto L_0x00ad;
    L_0x00a9:
        r8 = move-exception;
        org.telegram.messenger.FileLog.e(r8);
    L_0x00ad:
        return;
    L_0x00ae:
        r8 = move-exception;
        r2 = r0;
    L_0x00b0:
        if (r2 == 0) goto L_0x00ba;
    L_0x00b2:
        r2.close();	 Catch:{ Exception -> 0x00b6 }
        goto L_0x00ba;
    L_0x00b6:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x00ba:
        if (r1 == 0) goto L_0x00c4;
    L_0x00bc:
        r1.close();	 Catch:{ Exception -> 0x00c0 }
        goto L_0x00c4;
    L_0x00c0:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x00c4:
        goto L_0x00c6;
    L_0x00c5:
        throw r8;
    L_0x00c6:
        goto L_0x00c5;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.PhoneFormat.PhoneFormat.init(java.lang.String):void");
    }

    public String defaultCallingCode() {
        return callingCodeForCountryCode(this.defaultCountry);
    }

    public String callingCodeForCountryCode(String str) {
        return (String) this.countryCallingCode.get(str.toLowerCase());
    }

    public ArrayList countriesForCallingCode(String str) {
        Object str2;
        if (str2.startsWith("+")) {
            str2 = str2.substring(1);
        }
        return (ArrayList) this.callingCodeCountries.get(str2);
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
        String str2 = "+";
        if (!this.initialzed) {
            return str;
        }
        try {
            String strip = strip(str);
            if (strip.startsWith(str2)) {
                strip = strip.substring(1);
                CallingCodeInfo findCallingCodeInfo = findCallingCodeInfo(strip);
                if (findCallingCodeInfo != null) {
                    strip = findCallingCodeInfo.format(strip);
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(str2);
                    stringBuilder.append(strip);
                    str = stringBuilder.toString();
                }
                return str;
            }
            CallingCodeInfo callingCodeInfo = callingCodeInfo(this.defaultCallingCode);
            if (callingCodeInfo == null) {
                return str;
            }
            String matchingAccessCode = callingCodeInfo.matchingAccessCode(strip);
            if (matchingAccessCode == null) {
                return callingCodeInfo.format(strip);
            }
            str2 = strip.substring(matchingAccessCode.length());
            CallingCodeInfo findCallingCodeInfo2 = findCallingCodeInfo(str2);
            if (findCallingCodeInfo2 != null) {
                str2 = findCallingCodeInfo2.format(str2);
            }
            if (str2.length() == 0) {
                return matchingAccessCode;
            }
            return String.format("%s %s", new Object[]{matchingAccessCode, str2});
        } catch (Exception e) {
            FileLog.e(e);
            return str;
        }
    }

    public boolean isPhoneNumberValid(String str) {
        boolean z = true;
        if (!this.initialzed) {
            return true;
        }
        str = strip(str);
        CallingCodeInfo findCallingCodeInfo;
        if (str.startsWith("+")) {
            str = str.substring(1);
            findCallingCodeInfo = findCallingCodeInfo(str);
            if (findCallingCodeInfo == null || !findCallingCodeInfo.isValidPhoneNumber(str)) {
                z = false;
            }
            return z;
        }
        findCallingCodeInfo = callingCodeInfo(this.defaultCallingCode);
        if (findCallingCodeInfo == null) {
            return false;
        }
        String matchingAccessCode = findCallingCodeInfo.matchingAccessCode(str);
        if (matchingAccessCode == null) {
            return findCallingCodeInfo.isValidPhoneNumber(str);
        }
        str = str.substring(matchingAccessCode.length());
        if (str.length() == 0) {
            return false;
        }
        findCallingCodeInfo = findCallingCodeInfo(str);
        if (findCallingCodeInfo == null || !findCallingCodeInfo.isValidPhoneNumber(str)) {
            z = false;
        }
        return z;
    }

    /* Access modifiers changed, original: 0000 */
    public int value32(int i) {
        if (i + 4 > this.data.length) {
            return 0;
        }
        this.buffer.position(i);
        return this.buffer.getInt();
    }

    /* Access modifiers changed, original: 0000 */
    public short value16(int i) {
        if (i + 2 > this.data.length) {
            return (short) 0;
        }
        this.buffer.position(i);
        return this.buffer.getShort();
    }

    public String valueString(int i) {
        String str = "";
        int i2 = i;
        while (i2 < this.data.length) {
            try {
                if (this.data[i2] == (byte) 0) {
                    i2 -= i;
                    if (i == i2) {
                        return str;
                    }
                    return new String(this.data, i, i2);
                }
                i2++;
            } catch (Exception e) {
                e.printStackTrace();
                return str;
            }
        }
        return str;
    }

    public CallingCodeInfo callingCodeInfo(String str) {
        PhoneFormat phoneFormat = this;
        String str2 = str;
        CallingCodeInfo callingCodeInfo = (CallingCodeInfo) phoneFormat.callingCodeData.get(str2);
        if (callingCodeInfo != null) {
            return callingCodeInfo;
        }
        Integer num = (Integer) phoneFormat.callingCodeOffsets.get(str2);
        if (num == null) {
            return callingCodeInfo;
        }
        byte[] bArr = phoneFormat.data;
        int intValue = num.intValue();
        CallingCodeInfo callingCodeInfo2 = new CallingCodeInfo();
        callingCodeInfo2.callingCode = str2;
        callingCodeInfo2.countries = (ArrayList) phoneFormat.callingCodeCountries.get(str2);
        phoneFormat.callingCodeData.put(str2, callingCodeInfo2);
        short value16 = phoneFormat.value16(intValue);
        int i = 2;
        int i2 = (intValue + 2) + 2;
        short value162 = phoneFormat.value16(i2);
        i2 = (i2 + 2) + 2;
        short value163 = phoneFormat.value16(i2);
        i2 = (i2 + 2) + 2;
        ArrayList arrayList = new ArrayList(5);
        while (true) {
            String valueString = phoneFormat.valueString(i2);
            if (valueString.length() == 0) {
                break;
            }
            arrayList.add(valueString);
            i2 += valueString.length() + 1;
        }
        callingCodeInfo2.trunkPrefixes = arrayList;
        i2++;
        arrayList = new ArrayList(5);
        while (true) {
            String valueString2 = phoneFormat.valueString(i2);
            if (valueString2.length() == 0) {
                break;
            }
            arrayList.add(valueString2);
            i2 += valueString2.length() + 1;
        }
        callingCodeInfo2.intlPrefixes = arrayList;
        ArrayList arrayList2 = new ArrayList(value163);
        intValue += value16;
        int i3 = intValue;
        short s = (short) 0;
        while (s < value163) {
            byte[] bArr2;
            int i4;
            RuleSet ruleSet = new RuleSet();
            ruleSet.matchLen = phoneFormat.value16(i3);
            i3 += i;
            short value164 = phoneFormat.value16(i3);
            i3 += i;
            ArrayList arrayList3 = new ArrayList(value164);
            int i5 = i3;
            short s2 = (short) 0;
            while (s2 < value164) {
                boolean z;
                PhoneRule phoneRule = new PhoneRule();
                phoneRule.minVal = phoneFormat.value32(i5);
                i5 += 4;
                phoneRule.maxVal = phoneFormat.value32(i5);
                i5 += 4;
                int i6 = i5 + 1;
                phoneRule.byte8 = bArr[i5];
                i5 = i6 + 1;
                phoneRule.maxLen = bArr[i6];
                i6 = i5 + 1;
                phoneRule.otherFlag = bArr[i5];
                i5 = i6 + 1;
                phoneRule.prefixLen = bArr[i6];
                i6 = i5 + 1;
                phoneRule.flag12 = bArr[i5];
                i5 = i6 + 1;
                phoneRule.flag13 = bArr[i6];
                value16 = phoneFormat.value16(i5);
                i5 += i;
                phoneRule.format = phoneFormat.valueString((intValue + value162) + value16);
                i6 = phoneRule.format.indexOf("[[");
                if (i6 != -1) {
                    int indexOf = phoneRule.format.indexOf("]]");
                    bArr2 = bArr;
                    r2 = new Object[2];
                    i4 = intValue;
                    r2[0] = phoneRule.format.substring(0, i6);
                    i = 2;
                    z = true;
                    r2[1] = phoneRule.format.substring(indexOf + 2);
                    phoneRule.format = String.format("%s%s", r2);
                } else {
                    bArr2 = bArr;
                    i4 = intValue;
                    z = true;
                    i = 2;
                }
                arrayList3.add(phoneRule);
                if (phoneRule.hasIntlPrefix) {
                    ruleSet.hasRuleWithIntlPrefix = z;
                }
                if (phoneRule.hasTrunkPrefix) {
                    ruleSet.hasRuleWithTrunkPrefix = z;
                }
                s2++;
                phoneFormat = this;
                bArr = bArr2;
                intValue = i4;
            }
            bArr2 = bArr;
            i4 = intValue;
            ruleSet.rules = arrayList3;
            arrayList2.add(ruleSet);
            s++;
            phoneFormat = this;
            i3 = i5;
            intValue = i4;
        }
        callingCodeInfo2.ruleSets = arrayList2;
        return callingCodeInfo2;
    }

    public void parseDataHeader() {
        int i = 0;
        int value32 = value32(0);
        int i2 = (value32 * 12) + 4;
        int i3 = 4;
        while (i < value32) {
            String valueString = valueString(i3);
            i3 += 4;
            String valueString2 = valueString(i3);
            i3 += 4;
            int value322 = value32(i3) + i2;
            i3 += 4;
            if (valueString2.equals(this.defaultCountry)) {
                this.defaultCallingCode = valueString;
            }
            this.countryCallingCode.put(valueString2, valueString);
            this.callingCodeOffsets.put(valueString, Integer.valueOf(value322));
            ArrayList arrayList = (ArrayList) this.callingCodeCountries.get(valueString);
            if (arrayList == null) {
                arrayList = new ArrayList();
                this.callingCodeCountries.put(valueString, arrayList);
            }
            arrayList.add(valueString2);
            i++;
        }
        String str = this.defaultCallingCode;
        if (str != null) {
            callingCodeInfo(str);
        }
    }
}
