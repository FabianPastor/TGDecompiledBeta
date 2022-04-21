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
    private static volatile PhoneFormat Instance = null;
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
        PhoneFormat localInstance = Instance;
        if (localInstance == null) {
            synchronized (PhoneFormat.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    PhoneFormat phoneFormat = new PhoneFormat();
                    localInstance = phoneFormat;
                    Instance = phoneFormat;
                }
            }
        }
        return localInstance;
    }

    public static String strip(String str) {
        StringBuilder res = new StringBuilder(str);
        for (int i = res.length() - 1; i >= 0; i--) {
            if (!"0123456789+*#".contains(res.substring(i, i + 1))) {
                res.deleteCharAt(i);
            }
        }
        return res.toString();
    }

    public static String stripExceptNumbers(String str, boolean includePlus) {
        if (str == null) {
            return null;
        }
        StringBuilder res = new StringBuilder(str);
        String phoneChars = "NUM";
        if (includePlus) {
            phoneChars = phoneChars + "+";
        }
        for (int i = res.length() - 1; i >= 0; i--) {
            if (!phoneChars.contains(res.substring(i, i + 1))) {
                res.deleteCharAt(i);
            }
        }
        return res.toString();
    }

    public static String stripExceptNumbers(String str) {
        return stripExceptNumbers(str, false);
    }

    public PhoneFormat() {
        init((String) null);
    }

    public PhoneFormat(String countryCode) {
        init(countryCode);
    }

    public void init(String countryCode) {
        InputStream stream = null;
        ByteArrayOutputStream bos = null;
        try {
            InputStream stream2 = ApplicationLoader.applicationContext.getAssets().open("PhoneFormats.dat");
            ByteArrayOutputStream bos2 = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            while (true) {
                int read = stream2.read(buf, 0, 1024);
                int len = read;
                if (read == -1) {
                    break;
                }
                bos2.write(buf, 0, len);
            }
            byte[] byteArray = bos2.toByteArray();
            this.data = byteArray;
            ByteBuffer wrap = ByteBuffer.wrap(byteArray);
            this.buffer = wrap;
            wrap.order(ByteOrder.LITTLE_ENDIAN);
            try {
                bos2.close();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            if (stream2 != null) {
                try {
                    stream2.close();
                } catch (Exception e2) {
                    FileLog.e((Throwable) e2);
                }
            }
            if (countryCode == null || countryCode.length() == 0) {
                this.defaultCountry = Locale.getDefault().getCountry().toLowerCase();
            } else {
                this.defaultCountry = countryCode;
            }
            this.callingCodeOffsets = new HashMap<>(255);
            this.callingCodeCountries = new HashMap<>(255);
            this.callingCodeData = new HashMap<>(10);
            this.countryCallingCode = new HashMap<>(255);
            parseDataHeader();
            this.initialzed = true;
        } catch (Exception e3) {
            e3.printStackTrace();
            if (bos != null) {
                try {
                    bos.close();
                } catch (Exception e4) {
                    FileLog.e((Throwable) e4);
                }
            }
            if (stream != null) {
                try {
                    stream.close();
                } catch (Exception e5) {
                    FileLog.e((Throwable) e5);
                }
            }
        } catch (Throwable th) {
            if (bos != null) {
                try {
                    bos.close();
                } catch (Exception e6) {
                    FileLog.e((Throwable) e6);
                }
            }
            if (stream != null) {
                try {
                    stream.close();
                } catch (Exception e7) {
                    FileLog.e((Throwable) e7);
                }
            }
            throw th;
        }
    }

    public String defaultCallingCode() {
        return callingCodeForCountryCode(this.defaultCountry);
    }

    public String callingCodeForCountryCode(String countryCode) {
        return this.countryCallingCode.get(countryCode.toLowerCase());
    }

    public ArrayList countriesForCallingCode(String callingCode) {
        if (callingCode.startsWith("+")) {
            callingCode = callingCode.substring(1);
        }
        return this.callingCodeCountries.get(callingCode);
    }

    public CallingCodeInfo findCallingCodeInfo(String str) {
        CallingCodeInfo res = null;
        int i = 0;
        while (i < 3 && i < str.length() && (res = callingCodeInfo(str.substring(0, i + 1))) == null) {
            i++;
        }
        return res;
    }

    public String format(String orig) {
        if (!this.initialzed) {
            return orig;
        }
        try {
            String str = strip(orig);
            if (str.startsWith("+")) {
                String rest = str.substring(1);
                CallingCodeInfo info = findCallingCodeInfo(rest);
                if (info == null) {
                    return orig;
                }
                String phone = info.format(rest);
                return "+" + phone;
            }
            CallingCodeInfo info2 = callingCodeInfo(this.defaultCallingCode);
            if (info2 == null) {
                return orig;
            }
            String accessCode = info2.matchingAccessCode(str);
            if (accessCode == null) {
                return info2.format(str);
            }
            String rest2 = str.substring(accessCode.length());
            String phone2 = rest2;
            CallingCodeInfo info22 = findCallingCodeInfo(rest2);
            if (info22 != null) {
                phone2 = info22.format(rest2);
            }
            if (phone2.length() == 0) {
                return accessCode;
            }
            return String.format("%s %s", new Object[]{accessCode, phone2});
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return orig;
        }
    }

    public boolean isPhoneNumberValid(String phoneNumber) {
        CallingCodeInfo info2;
        if (!this.initialzed) {
            return true;
        }
        String str = strip(phoneNumber);
        if (str.startsWith("+")) {
            String rest = str.substring(1);
            CallingCodeInfo info = findCallingCodeInfo(rest);
            if (info == null || !info.isValidPhoneNumber(rest)) {
                return false;
            }
            return true;
        }
        CallingCodeInfo info3 = callingCodeInfo(this.defaultCallingCode);
        if (info3 == null) {
            return false;
        }
        String accessCode = info3.matchingAccessCode(str);
        if (accessCode == null) {
            return info3.isValidPhoneNumber(str);
        }
        String rest2 = str.substring(accessCode.length());
        if (rest2.length() == 0 || (info2 = findCallingCodeInfo(rest2)) == null || !info2.isValidPhoneNumber(rest2)) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: package-private */
    public int value32(int offset) {
        if (offset + 4 > this.data.length) {
            return 0;
        }
        this.buffer.position(offset);
        return this.buffer.getInt();
    }

    /* access modifiers changed from: package-private */
    public short value16(int offset) {
        if (offset + 2 > this.data.length) {
            return 0;
        }
        this.buffer.position(offset);
        return this.buffer.getShort();
    }

    public String valueString(int offset) {
        int a = offset;
        while (true) {
            try {
                byte[] bArr = this.data;
                if (a >= bArr.length) {
                    return "";
                }
                if (bArr[a] == 0) {
                    return offset == a - offset ? "" : new String(bArr, offset, a - offset);
                }
                a++;
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        }
    }

    public CallingCodeInfo callingCodeInfo(String callingCode) {
        int block1Len;
        int start;
        int offset;
        boolean z;
        PhoneFormat phoneFormat = this;
        String str = callingCode;
        CallingCodeInfo res = phoneFormat.callingCodeData.get(str);
        if (res == null) {
            Integer num = phoneFormat.callingCodeOffsets.get(str);
            if (num != null) {
                byte[] bytes = phoneFormat.data;
                int start2 = num.intValue();
                int offset2 = start2;
                res = new CallingCodeInfo();
                res.callingCode = str;
                res.countries = phoneFormat.callingCodeCountries.get(str);
                phoneFormat.callingCodeData.put(str, res);
                int block1Len2 = phoneFormat.value16(offset2);
                int offset3 = offset2 + 2 + 2;
                int block2Len = phoneFormat.value16(offset3);
                int offset4 = offset3 + 2 + 2;
                int setCnt = phoneFormat.value16(offset4);
                int offset5 = offset4 + 2 + 2;
                ArrayList<String> strs = new ArrayList<>(5);
                while (true) {
                    String valueString = phoneFormat.valueString(offset5);
                    String str2 = valueString;
                    if (valueString.length() == 0) {
                        break;
                    }
                    strs.add(str2);
                    offset5 += str2.length() + 1;
                }
                res.trunkPrefixes = strs;
                int offset6 = offset5 + 1;
                ArrayList<String> strs2 = new ArrayList<>(5);
                while (true) {
                    String valueString2 = phoneFormat.valueString(offset6);
                    String str3 = valueString2;
                    if (valueString2.length() == 0) {
                        break;
                    }
                    strs2.add(str3);
                    offset6 += str3.length() + 1;
                }
                res.intlPrefixes = strs2;
                ArrayList<RuleSet> ruleSets = new ArrayList<>(setCnt);
                int offset7 = start2 + block1Len2;
                int s = 0;
                while (s < setCnt) {
                    RuleSet ruleSet = new RuleSet();
                    ruleSet.matchLen = phoneFormat.value16(offset7);
                    int offset8 = offset7 + 2;
                    int ruleCnt = phoneFormat.value16(offset8);
                    offset7 = offset8 + 2;
                    ArrayList<PhoneRule> rules = new ArrayList<>(ruleCnt);
                    Integer num2 = num;
                    int r = 0;
                    while (r < ruleCnt) {
                        int setCnt2 = setCnt;
                        ArrayList<String> strs3 = strs2;
                        PhoneRule rule = new PhoneRule();
                        rule.minVal = phoneFormat.value32(offset7);
                        int offset9 = offset7 + 4;
                        rule.maxVal = phoneFormat.value32(offset9);
                        int offset10 = offset9 + 4;
                        int offset11 = offset10 + 1;
                        rule.byte8 = bytes[offset10];
                        int offset12 = offset11 + 1;
                        rule.maxLen = bytes[offset11];
                        int offset13 = offset12 + 1;
                        rule.otherFlag = bytes[offset12];
                        int offset14 = offset13 + 1;
                        rule.prefixLen = bytes[offset13];
                        int offset15 = offset14 + 1;
                        rule.flag12 = bytes[offset14];
                        int offset16 = offset15 + 1;
                        rule.flag13 = bytes[offset15];
                        int strOffset = phoneFormat.value16(offset16);
                        int offset17 = offset16 + 2;
                        byte[] bytes2 = bytes;
                        rule.format = phoneFormat.valueString(start2 + block1Len2 + block2Len + strOffset);
                        int openPos = rule.format.indexOf("[[");
                        if (openPos != -1) {
                            start = start2;
                            offset = offset17;
                            block1Len = block1Len2;
                            rule.format = String.format("%s%s", new Object[]{rule.format.substring(0, openPos), rule.format.substring(rule.format.indexOf("]]") + 2)});
                        } else {
                            start = start2;
                            offset = offset17;
                            block1Len = block1Len2;
                        }
                        rules.add(rule);
                        if (rule.hasIntlPrefix) {
                            z = true;
                            ruleSet.hasRuleWithIntlPrefix = true;
                        } else {
                            z = true;
                        }
                        if (rule.hasTrunkPrefix) {
                            ruleSet.hasRuleWithTrunkPrefix = z;
                        }
                        r++;
                        phoneFormat = this;
                        offset7 = offset;
                        setCnt = setCnt2;
                        start2 = start;
                        strs2 = strs3;
                        bytes = bytes2;
                        block1Len2 = block1Len;
                    }
                    int i = start2;
                    int i2 = block1Len2;
                    int i3 = setCnt;
                    ArrayList<String> arrayList = strs2;
                    ruleSet.rules = rules;
                    ruleSets.add(ruleSet);
                    s++;
                    phoneFormat = this;
                    String str4 = callingCode;
                    num = num2;
                    bytes = bytes;
                }
                byte[] bArr = bytes;
                int i4 = start2;
                int i5 = block1Len2;
                int i6 = setCnt;
                ArrayList<String> arrayList2 = strs2;
                res.ruleSets = ruleSets;
            }
        }
        return res;
    }

    public void parseDataHeader() {
        int count = value32(0);
        int base = (count * 12) + 4;
        int spot = 4;
        for (int i = 0; i < count; i++) {
            String callingCode = valueString(spot);
            int spot2 = spot + 4;
            String country = valueString(spot2);
            int spot3 = spot2 + 4;
            int offset = value32(spot3) + base;
            spot = spot3 + 4;
            if (country.equals(this.defaultCountry)) {
                this.defaultCallingCode = callingCode;
            }
            this.countryCallingCode.put(country, callingCode);
            this.callingCodeOffsets.put(callingCode, Integer.valueOf(offset));
            ArrayList<String> countries = this.callingCodeCountries.get(callingCode);
            if (countries == null) {
                countries = new ArrayList<>();
                this.callingCodeCountries.put(callingCode, countries);
            }
            countries.add(country);
        }
        String str = this.defaultCallingCode;
        if (str != null) {
            callingCodeInfo(str);
        }
    }
}
