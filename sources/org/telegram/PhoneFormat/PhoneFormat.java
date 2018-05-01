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
        str = "0123456789+*#";
        for (int length = stringBuilder.length() - 1; length >= 0; length--) {
            if (!str.contains(stringBuilder.substring(length, length + 1))) {
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
        str = "0123456789";
        if (z) {
            z = new StringBuilder();
            z.append(str);
            z.append("+");
            str = z.toString();
        }
        for (z = stringBuilder.length() - 1; z < false; z--) {
            if (!str.contains(stringBuilder.substring(z, z + 1))) {
                stringBuilder.deleteCharAt(z);
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

    public void init(String str) {
        InputStream open;
        ByteArrayOutputStream byteArrayOutputStream;
        ByteArrayOutputStream byteArrayOutputStream2 = null;
        try {
            open = ApplicationLoader.applicationContext.getAssets().open("PhoneFormats.dat");
            try {
                byteArrayOutputStream = new ByteArrayOutputStream();
            } catch (Exception e) {
                str = e;
                try {
                    str.printStackTrace();
                    if (byteArrayOutputStream2 != null) {
                        try {
                            byteArrayOutputStream2.close();
                        } catch (Throwable e2) {
                            FileLog.m3e(e2);
                        }
                    }
                    if (open != null) {
                        try {
                            open.close();
                        } catch (Throwable e22) {
                            FileLog.m3e(e22);
                        }
                    }
                } catch (Throwable th) {
                    str = th;
                    byteArrayOutputStream = byteArrayOutputStream2;
                    if (byteArrayOutputStream != null) {
                        try {
                            byteArrayOutputStream.close();
                        } catch (Throwable e3) {
                            FileLog.m3e(e3);
                        }
                    }
                    if (open != null) {
                        try {
                            open.close();
                        } catch (Throwable e32) {
                            FileLog.m3e(e32);
                        }
                    }
                    throw str;
                }
            }
            try {
                byte[] bArr = new byte[1024];
                while (true) {
                    int read = open.read(bArr, 0, 1024);
                    if (read == -1) {
                        break;
                    }
                    byteArrayOutputStream.write(bArr, 0, read);
                }
                this.data = byteArrayOutputStream.toByteArray();
                this.buffer = ByteBuffer.wrap(this.data);
                this.buffer.order(ByteOrder.LITTLE_ENDIAN);
                if (byteArrayOutputStream != null) {
                    try {
                        byteArrayOutputStream.close();
                    } catch (Throwable e322) {
                        FileLog.m3e(e322);
                    }
                }
                if (open != null) {
                    try {
                        open.close();
                    } catch (Throwable e3222) {
                        FileLog.m3e(e3222);
                    }
                }
                if (str == null || str.length() == 0) {
                    this.defaultCountry = Locale.getDefault().getCountry().toLowerCase();
                } else {
                    this.defaultCountry = str;
                }
                this.callingCodeOffsets = new HashMap(255);
                this.callingCodeCountries = new HashMap(255);
                this.callingCodeData = new HashMap(10);
                this.countryCallingCode = new HashMap(255);
                parseDataHeader();
                this.initialzed = true;
            } catch (Exception e4) {
                str = e4;
                byteArrayOutputStream2 = byteArrayOutputStream;
                str.printStackTrace();
                if (byteArrayOutputStream2 != null) {
                    byteArrayOutputStream2.close();
                }
                if (open != null) {
                    open.close();
                }
            } catch (Throwable th2) {
                str = th2;
                if (byteArrayOutputStream != null) {
                    byteArrayOutputStream.close();
                }
                if (open != null) {
                    open.close();
                }
                throw str;
            }
        } catch (Exception e5) {
            str = e5;
            open = null;
            str.printStackTrace();
            if (byteArrayOutputStream2 != null) {
                byteArrayOutputStream2.close();
            }
            if (open != null) {
                open.close();
            }
        } catch (Throwable th3) {
            str = th3;
            open = null;
            byteArrayOutputStream = open;
            if (byteArrayOutputStream != null) {
                byteArrayOutputStream.close();
            }
            if (open != null) {
                open.close();
            }
            throw str;
        }
    }

    public String defaultCallingCode() {
        return callingCodeForCountryCode(this.defaultCountry);
    }

    public String callingCodeForCountryCode(String str) {
        return (String) this.countryCallingCode.get(str.toLowerCase());
    }

    public ArrayList countriesForCallingCode(String str) {
        if (str.startsWith("+")) {
            str = str.substring(1);
        }
        return (ArrayList) this.callingCodeCountries.get(str);
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
            CallingCodeInfo findCallingCodeInfo;
            if (strip.startsWith("+")) {
                strip = strip.substring(1);
                findCallingCodeInfo = findCallingCodeInfo(strip);
                if (findCallingCodeInfo == null) {
                    return str;
                }
                strip = findCallingCodeInfo.format(strip);
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("+");
                stringBuilder.append(strip);
                return stringBuilder.toString();
            }
            findCallingCodeInfo = callingCodeInfo(this.defaultCallingCode);
            if (findCallingCodeInfo == null) {
                return str;
            }
            String matchingAccessCode = findCallingCodeInfo.matchingAccessCode(strip);
            if (matchingAccessCode == null) {
                return findCallingCodeInfo.format(strip);
            }
            strip = strip.substring(matchingAccessCode.length());
            findCallingCodeInfo = findCallingCodeInfo(strip);
            if (findCallingCodeInfo != null) {
                strip = findCallingCodeInfo.format(strip);
            }
            if (strip.length() == 0) {
                return matchingAccessCode;
            }
            return String.format("%s %s", new Object[]{matchingAccessCode, strip});
        } catch (Throwable e) {
            FileLog.m3e(e);
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
            if (findCallingCodeInfo == null || findCallingCodeInfo.isValidPhoneNumber(str) == null) {
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
        if (findCallingCodeInfo == null || findCallingCodeInfo.isValidPhoneNumber(str) == null) {
            z = false;
        }
        return z;
    }

    int value32(int i) {
        if (i + 4 > this.data.length) {
            return 0;
        }
        this.buffer.position(i);
        return this.buffer.getInt();
    }

    short value16(int i) {
        if (i + 2 > this.data.length) {
            return (short) 0;
        }
        this.buffer.position(i);
        return this.buffer.getShort();
    }

    public String valueString(int i) {
        int i2 = i;
        while (i2 < this.data.length) {
            try {
                if (this.data[i2] == (byte) 0) {
                    i2 -= i;
                    if (i == i2) {
                        return TtmlNode.ANONYMOUS_REGION_ID;
                    }
                    return new String(this.data, i, i2);
                }
                i2++;
            } catch (int i3) {
                i3.printStackTrace();
                return TtmlNode.ANONYMOUS_REGION_ID;
            }
        }
        return TtmlNode.ANONYMOUS_REGION_ID;
    }

    public CallingCodeInfo callingCodeInfo(String str) {
        String str2 = str;
        CallingCodeInfo callingCodeInfo = (CallingCodeInfo) this.callingCodeData.get(str2);
        if (callingCodeInfo != null) {
            return callingCodeInfo;
        }
        PhoneFormat phoneFormat;
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
        short value16 = value16(intValue);
        int i = 2;
        int i2 = (intValue + 2) + 2;
        int value162 = value16(i2);
        i2 = (i2 + 2) + 2;
        short value163 = value16(i2);
        i2 = (i2 + 2) + 2;
        ArrayList arrayList = new ArrayList(5);
        while (true) {
            String valueString = valueString(i2);
            int i3 = 1;
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
            String valueString2 = valueString(i2);
            if (valueString2.length() == 0) {
                break;
            }
            arrayList.add(valueString2);
            i2 += valueString2.length() + 1;
        }
        callingCodeInfo2.intlPrefixes = arrayList;
        ArrayList arrayList2 = new ArrayList(value163);
        intValue += value16;
        int i4 = intValue;
        short s = (short) 0;
        while (s < value163) {
            int i5;
            byte[] bArr2;
            int i6;
            int i7;
            int i8;
            RuleSet ruleSet = new RuleSet();
            ruleSet.matchLen = phoneFormat.value16(i4);
            i4 += i;
            short value164 = phoneFormat.value16(i4);
            i4 += i;
            ArrayList arrayList3 = new ArrayList(value164);
            int i9 = i4;
            short s2 = (short) 0;
            while (s2 < value164) {
                boolean z;
                PhoneRule phoneRule = new PhoneRule();
                phoneRule.minVal = phoneFormat.value32(i9);
                i9 += 4;
                phoneRule.maxVal = phoneFormat.value32(i9);
                i9 += 4;
                i5 = i9 + 1;
                phoneRule.byte8 = bArr[i9];
                i9 = i5 + 1;
                phoneRule.maxLen = bArr[i5];
                i5 = i9 + 1;
                phoneRule.otherFlag = bArr[i9];
                i9 = i5 + 1;
                phoneRule.prefixLen = bArr[i5];
                i5 = i9 + 1;
                phoneRule.flag12 = bArr[i9];
                i9 = i5 + 1;
                phoneRule.flag13 = bArr[i5];
                value16 = phoneFormat.value16(i9);
                i9 += i;
                phoneRule.format = phoneFormat.valueString((intValue + value162) + value16);
                i5 = phoneRule.format.indexOf("[[");
                if (i5 != -1) {
                    int indexOf = phoneRule.format.indexOf("]]");
                    bArr2 = bArr;
                    i6 = intValue;
                    r3 = new Object[2];
                    i7 = value162;
                    r3[0] = phoneRule.format.substring(0, i5);
                    i8 = 2;
                    z = true;
                    r3[1] = phoneRule.format.substring(indexOf + 2);
                    phoneRule.format = String.format("%s%s", r3);
                } else {
                    bArr2 = bArr;
                    i6 = intValue;
                    i7 = value162;
                    z = true;
                    i8 = 2;
                }
                arrayList3.add(phoneRule);
                if (phoneRule.hasIntlPrefix) {
                    ruleSet.hasRuleWithIntlPrefix = z;
                }
                if (phoneRule.hasTrunkPrefix) {
                    ruleSet.hasRuleWithTrunkPrefix = z;
                }
                s2++;
                boolean z2 = z;
                i = i8;
                bArr = bArr2;
                intValue = i6;
                value162 = i7;
                phoneFormat = this;
            }
            bArr2 = bArr;
            i6 = intValue;
            i8 = i;
            i7 = value162;
            i5 = i3;
            ruleSet.rules = arrayList3;
            arrayList2.add(ruleSet);
            s++;
            i4 = i9;
            bArr = bArr2;
            value162 = i7;
            phoneFormat = this;
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
        if (this.defaultCallingCode != null) {
            callingCodeInfo(this.defaultCallingCode);
        }
    }
}
