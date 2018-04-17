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
        String phoneChars = "0123456789+*#";
        for (int i = res.length() - 1; i >= 0; i--) {
            if (!phoneChars.contains(res.substring(i, i + 1))) {
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
        String phoneChars = "0123456789";
        if (includePlus) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(phoneChars);
            stringBuilder.append("+");
            phoneChars = stringBuilder.toString();
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
        init(null);
    }

    public PhoneFormat(String countryCode) {
        init(countryCode);
    }

    public void init(String countryCode) {
        InputStream stream = null;
        ByteArrayOutputStream bos = null;
        try {
            stream = ApplicationLoader.applicationContext.getAssets().open("PhoneFormats.dat");
            bos = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            while (true) {
                int read = stream.read(buf, 0, 1024);
                int len = read;
                if (read == -1) {
                    break;
                }
                bos.write(buf, 0, len);
            }
            this.data = bos.toByteArray();
            this.buffer = ByteBuffer.wrap(this.data);
            this.buffer.order(ByteOrder.LITTLE_ENDIAN);
            if (bos != null) {
                try {
                    bos.close();
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
            if (stream != null) {
                try {
                    stream.close();
                } catch (Throwable e2) {
                    FileLog.m3e(e2);
                }
            }
            if (countryCode == null || countryCode.length() == 0) {
                this.defaultCountry = Locale.getDefault().getCountry().toLowerCase();
            } else {
                this.defaultCountry = countryCode;
            }
            this.callingCodeOffsets = new HashMap(255);
            this.callingCodeCountries = new HashMap(255);
            this.callingCodeData = new HashMap(10);
            this.countryCallingCode = new HashMap(255);
            parseDataHeader();
            this.initialzed = true;
        } catch (Exception e3) {
            e3.printStackTrace();
            if (bos != null) {
                try {
                    bos.close();
                } catch (Throwable e4) {
                    FileLog.m3e(e4);
                    if (stream != null) {
                        try {
                            stream.close();
                        } catch (Throwable e42) {
                            FileLog.m3e(e42);
                        }
                    }
                }
            }
            if (stream != null) {
                stream.close();
            }
        } catch (Throwable th) {
            if (bos != null) {
                try {
                    bos.close();
                } catch (Throwable e422) {
                    FileLog.m3e(e422);
                    if (stream != null) {
                        try {
                            stream.close();
                        } catch (Throwable e4222) {
                            FileLog.m3e(e4222);
                        }
                    }
                }
            }
            if (stream != null) {
                stream.close();
            }
        }
    }

    public String defaultCallingCode() {
        return callingCodeForCountryCode(this.defaultCountry);
    }

    public String callingCodeForCountryCode(String countryCode) {
        return (String) this.countryCallingCode.get(countryCode.toLowerCase());
    }

    public ArrayList countriesForCallingCode(String callingCode) {
        if (callingCode.startsWith("+")) {
            callingCode = callingCode.substring(1);
        }
        return (ArrayList) this.callingCodeCountries.get(callingCode);
    }

    public CallingCodeInfo findCallingCodeInfo(String str) {
        CallingCodeInfo res = null;
        int i = 0;
        while (i < 3 && i < str.length()) {
            res = callingCodeInfo(str.substring(0, i + 1));
            if (res != null) {
                break;
            }
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
            String phone;
            if (str.startsWith("+")) {
                String rest = str.substring(1);
                CallingCodeInfo info = findCallingCodeInfo(rest);
                if (info == null) {
                    return orig;
                }
                phone = info.format(rest);
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("+");
                stringBuilder.append(phone);
                return stringBuilder.toString();
            }
            CallingCodeInfo info2 = callingCodeInfo(this.defaultCallingCode);
            if (info2 == null) {
                return orig;
            }
            phone = info2.matchingAccessCode(str);
            if (phone == null) {
                return info2.format(str);
            }
            String rest2 = str.substring(phone.length());
            String phone2 = rest2;
            CallingCodeInfo info22 = findCallingCodeInfo(rest2);
            if (info22 != null) {
                phone2 = info22.format(rest2);
            }
            if (phone2.length() == 0) {
                return phone;
            }
            return String.format("%s %s", new Object[]{phone, phone2});
        } catch (Throwable e) {
            FileLog.m3e(e);
            return orig;
        }
    }

    public boolean isPhoneNumberValid(String phoneNumber) {
        boolean z = true;
        if (!this.initialzed) {
            return true;
        }
        String str = strip(phoneNumber);
        if (str.startsWith("+")) {
            String rest = str.substring(1);
            CallingCodeInfo info = findCallingCodeInfo(rest);
            if (info == null || !info.isValidPhoneNumber(rest)) {
                z = false;
            }
            return z;
        }
        CallingCodeInfo info2 = callingCodeInfo(this.defaultCallingCode);
        if (info2 == null) {
            return false;
        }
        String accessCode = info2.matchingAccessCode(str);
        if (accessCode == null) {
            return info2.isValidPhoneNumber(str);
        }
        String rest2 = str.substring(accessCode.length());
        if (rest2.length() == 0) {
            return false;
        }
        CallingCodeInfo info22 = findCallingCodeInfo(rest2);
        if (info22 == null || !info22.isValidPhoneNumber(rest2)) {
            z = false;
        }
        return z;
    }

    int value32(int offset) {
        if (offset + 4 > this.data.length) {
            return 0;
        }
        this.buffer.position(offset);
        return this.buffer.getInt();
    }

    short value16(int offset) {
        if (offset + 2 > this.data.length) {
            return (short) 0;
        }
        this.buffer.position(offset);
        return this.buffer.getShort();
    }

    public String valueString(int offset) {
        int a = offset;
        while (a < this.data.length) {
            try {
                if (this.data[a] != (byte) 0) {
                    a++;
                } else if (offset == a - offset) {
                    return TtmlNode.ANONYMOUS_REGION_ID;
                } else {
                    return new String(this.data, offset, a - offset);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return TtmlNode.ANONYMOUS_REGION_ID;
            }
        }
        return TtmlNode.ANONYMOUS_REGION_ID;
    }

    public CallingCodeInfo callingCodeInfo(String callingCode) {
        String str = callingCode;
        CallingCodeInfo res = (CallingCodeInfo) this.callingCodeData.get(str);
        if (res == null) {
            PhoneFormat phoneFormat;
            Integer num = (Integer) phoneFormat.callingCodeOffsets.get(str);
            if (num != null) {
                String str2;
                int setCnt;
                byte[] bytes;
                int start;
                int block1Len;
                int block2Len;
                byte[] bytes2 = phoneFormat.data;
                int start2 = num.intValue();
                int offset = start2;
                res = new CallingCodeInfo();
                res.callingCode = str;
                res.countries = (ArrayList) phoneFormat.callingCodeCountries.get(str);
                phoneFormat.callingCodeData.put(str, res);
                int block1Len2 = value16(offset);
                offset = (offset + 2) + 2;
                int block2Len2 = value16(offset);
                offset = (offset + 2) + 2;
                int setCnt2 = value16(offset);
                offset = (offset + 2) + 2;
                ArrayList<String> strs = new ArrayList(5);
                while (true) {
                    String valueString = valueString(offset);
                    str2 = valueString;
                    if (valueString.length() == 0) {
                        break;
                    }
                    strs.add(str2);
                    offset += str2.length() + 1;
                }
                res.trunkPrefixes = strs;
                offset++;
                strs = new ArrayList(5);
                while (true) {
                    String valueString2 = valueString(offset);
                    str2 = valueString2;
                    if (valueString2.length() == 0) {
                        break;
                    }
                    strs.add(str2);
                    offset += str2.length() + 1;
                }
                res.intlPrefixes = strs;
                ArrayList<RuleSet> ruleSets = new ArrayList(setCnt2);
                int offset2 = start2 + block1Len2;
                offset = 0;
                while (offset < setCnt2) {
                    int ruleCnt;
                    RuleSet ruleSet = new RuleSet();
                    ruleSet.matchLen = phoneFormat.value16(offset2);
                    offset2 += 2;
                    int ruleCnt2 = phoneFormat.value16(offset2);
                    offset2 += 2;
                    ArrayList<PhoneRule> rules = new ArrayList(ruleCnt2);
                    Integer num2 = num;
                    num = offset2;
                    offset2 = 0;
                    while (offset2 < ruleCnt2) {
                        boolean z;
                        ruleCnt = ruleCnt2;
                        PhoneRule rule = new PhoneRule();
                        setCnt = setCnt2;
                        rule.minVal = phoneFormat.value32(num);
                        num += 4;
                        rule.maxVal = phoneFormat.value32(num);
                        num += 4;
                        setCnt2 = num + 1;
                        rule.byte8 = bytes2[num];
                        num = setCnt2 + 1;
                        rule.maxLen = bytes2[setCnt2];
                        setCnt2 = num + 1;
                        rule.otherFlag = bytes2[num];
                        num = setCnt2 + 1;
                        rule.prefixLen = bytes2[setCnt2];
                        setCnt2 = num + 1;
                        rule.flag12 = bytes2[num];
                        num = setCnt2 + 1;
                        rule.flag13 = bytes2[setCnt2];
                        int offset3 = num + 2;
                        rule.format = phoneFormat.valueString(((start2 + block1Len2) + block2Len2) + phoneFormat.value16(num));
                        int openPos = rule.format.indexOf("[[");
                        if (openPos != -1) {
                            bytes = bytes2;
                            num = rule.format.indexOf("]]");
                            start = start2;
                            block1Len = block1Len2;
                            r7 = new Object[2];
                            block2Len = block2Len2;
                            r7[0] = rule.format.substring(0, openPos);
                            z = true;
                            r7[1] = rule.format.substring(num + 2);
                            rule.format = String.format("%s%s", r7);
                        } else {
                            bytes = bytes2;
                            start = start2;
                            block1Len = block1Len2;
                            block2Len = block2Len2;
                            z = true;
                        }
                        rules.add(rule);
                        if (rule.hasIntlPrefix != null) {
                            ruleSet.hasRuleWithIntlPrefix = z;
                        }
                        if (rule.hasTrunkPrefix != null) {
                            ruleSet.hasRuleWithTrunkPrefix = z;
                        }
                        offset2++;
                        ruleCnt2 = ruleCnt;
                        setCnt2 = setCnt;
                        num = offset3;
                        bytes2 = bytes;
                        start2 = start;
                        block1Len2 = block1Len;
                        block2Len2 = block2Len;
                        phoneFormat = this;
                    }
                    start = start2;
                    block1Len = block1Len2;
                    ruleCnt = ruleCnt2;
                    block2Len = block2Len2;
                    setCnt = setCnt2;
                    ruleSet.rules = rules;
                    ruleSets.add(ruleSet);
                    offset++;
                    offset2 = num;
                    num = num2;
                    block2Len2 = block2Len;
                    phoneFormat = this;
                    str = callingCode;
                }
                bytes = bytes2;
                start = start2;
                block1Len = block1Len2;
                block2Len = block2Len2;
                setCnt = setCnt2;
                res.ruleSets = ruleSets;
            }
        }
        return res;
    }

    public void parseDataHeader() {
        int i = 0;
        int count = value32(0);
        int base = (count * 12) + 4;
        int spot = 4;
        while (i < count) {
            String callingCode = valueString(spot);
            spot += 4;
            String country = valueString(spot);
            spot += 4;
            int offset = value32(spot) + base;
            spot += 4;
            if (country.equals(this.defaultCountry)) {
                this.defaultCallingCode = callingCode;
            }
            this.countryCallingCode.put(country, callingCode);
            this.callingCodeOffsets.put(callingCode, Integer.valueOf(offset));
            ArrayList<String> countries = (ArrayList) this.callingCodeCountries.get(callingCode);
            if (countries == null) {
                countries = new ArrayList();
                this.callingCodeCountries.put(callingCode, countries);
            }
            countries.add(country);
            i++;
        }
        if (this.defaultCallingCode != null) {
            callingCodeInfo(this.defaultCallingCode);
        }
    }
}
