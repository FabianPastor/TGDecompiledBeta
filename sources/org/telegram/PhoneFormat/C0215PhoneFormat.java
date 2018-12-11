package org.telegram.PhoneFormat;

import com.google.devtools.build.android.desugar.runtime.ThrowableExtension;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;

/* renamed from: org.telegram.PhoneFormat.PhoneFormat */
public class CLASSNAMEPhoneFormat {
    private static volatile CLASSNAMEPhoneFormat Instance = null;
    public ByteBuffer buffer;
    public HashMap<String, ArrayList<String>> callingCodeCountries;
    public HashMap<String, CallingCodeInfo> callingCodeData;
    public HashMap<String, Integer> callingCodeOffsets;
    public HashMap<String, String> countryCallingCode;
    public byte[] data;
    public String defaultCallingCode;
    public String defaultCountry;
    private boolean initialzed = false;

    public static CLASSNAMEPhoneFormat getInstance() {
        Throwable th;
        CLASSNAMEPhoneFormat localInstance = Instance;
        if (localInstance == null) {
            synchronized (CLASSNAMEPhoneFormat.class) {
                try {
                    localInstance = Instance;
                    if (localInstance == null) {
                        CLASSNAMEPhoneFormat localInstance2 = new CLASSNAMEPhoneFormat();
                        try {
                            Instance = localInstance2;
                            localInstance = localInstance2;
                        } catch (Throwable th2) {
                            th = th2;
                            localInstance = localInstance2;
                            throw th;
                        }
                    }
                } catch (Throwable th3) {
                    th = th3;
                    throw th;
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
        return CLASSNAMEPhoneFormat.stripExceptNumbers(str, false);
    }

    public CLASSNAMEPhoneFormat() {
        init(null);
    }

    public CLASSNAMEPhoneFormat(String countryCode) {
        init(countryCode);
    }

    /* JADX WARNING: Removed duplicated region for block: B:43:0x009f A:{SYNTHETIC, Splitter: B:43:0x009f} */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x00a4 A:{SYNTHETIC, Splitter: B:46:0x00a4} */
    /* JADX WARNING: Removed duplicated region for block: B:15:0x0030 A:{SYNTHETIC, Splitter: B:15:0x0030} */
    /* JADX WARNING: Removed duplicated region for block: B:58:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:18:0x0035 A:{SYNTHETIC, Splitter: B:18:0x0035} */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void init(String countryCode) {
        Exception e;
        Throwable th;
        InputStream stream = null;
        ByteArrayOutputStream bos = null;
        try {
            stream = ApplicationLoader.applicationContext.getAssets().open("PhoneFormats.dat");
            ByteArrayOutputStream bos2 = new ByteArrayOutputStream();
            try {
                byte[] buf = new byte[1024];
                while (true) {
                    int len = stream.read(buf, 0, 1024);
                    if (len == -1) {
                        break;
                    }
                    bos2.write(buf, 0, len);
                }
                this.data = bos2.toByteArray();
                this.buffer = ByteBuffer.wrap(this.data);
                this.buffer.order(ByteOrder.LITTLE_ENDIAN);
                if (bos2 != null) {
                    try {
                        bos2.close();
                    } catch (Throwable e2) {
                        FileLog.m13e(e2);
                    }
                }
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (Throwable e22) {
                        FileLog.m13e(e22);
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
                bos = bos2;
            } catch (Exception e3) {
                e = e3;
                bos = bos2;
                try {
                    ThrowableExtension.printStackTrace(e);
                    if (bos != null) {
                        try {
                            bos.close();
                        } catch (Throwable e222) {
                            FileLog.m13e(e222);
                        }
                    }
                    if (stream == null) {
                        try {
                            stream.close();
                        } catch (Throwable e2222) {
                            FileLog.m13e(e2222);
                        }
                    }
                } catch (Throwable th2) {
                    th = th2;
                    if (bos != null) {
                    }
                    if (stream != null) {
                    }
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                bos = bos2;
                if (bos != null) {
                    try {
                        bos.close();
                    } catch (Throwable e22222) {
                        FileLog.m13e(e22222);
                    }
                }
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (Throwable e222222) {
                        FileLog.m13e(e222222);
                    }
                }
                throw th;
            }
        } catch (Exception e4) {
            e = e4;
            ThrowableExtension.printStackTrace(e);
            if (bos != null) {
            }
            if (stream == null) {
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
        Object callingCode2;
        if (callingCode2.startsWith("+")) {
            callingCode2 = callingCode2.substring(1);
        }
        return (ArrayList) this.callingCodeCountries.get(callingCode2);
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
            String str = CLASSNAMEPhoneFormat.strip(orig);
            String rest;
            CallingCodeInfo info;
            if (str.startsWith("+")) {
                rest = str.substring(1);
                info = findCallingCodeInfo(rest);
                if (info == null) {
                    return orig;
                }
                return "+" + info.format(rest);
            }
            info = callingCodeInfo(this.defaultCallingCode);
            if (info == null) {
                return orig;
            }
            String accessCode = info.matchingAccessCode(str);
            if (accessCode == null) {
                return info.format(str);
            }
            rest = str.substring(accessCode.length());
            String phone = rest;
            CallingCodeInfo info2 = findCallingCodeInfo(rest);
            if (info2 != null) {
                phone = info2.format(rest);
            }
            if (phone.length() == 0) {
                return accessCode;
            }
            return String.format("%s %s", new Object[]{accessCode, phone});
        } catch (Throwable e) {
            FileLog.m13e(e);
            return orig;
        }
    }

    public boolean isPhoneNumberValid(String phoneNumber) {
        if (!this.initialzed) {
            return true;
        }
        String str = CLASSNAMEPhoneFormat.strip(phoneNumber);
        String rest;
        CallingCodeInfo info;
        if (str.startsWith("+")) {
            rest = str.substring(1);
            info = findCallingCodeInfo(rest);
            if (info == null || !info.isValidPhoneNumber(rest)) {
                return false;
            }
            return true;
        }
        info = callingCodeInfo(this.defaultCallingCode);
        if (info == null) {
            return false;
        }
        String accessCode = info.matchingAccessCode(str);
        if (accessCode == null) {
            return info.isValidPhoneNumber(str);
        }
        rest = str.substring(accessCode.length());
        if (rest.length() == 0) {
            return false;
        }
        CallingCodeInfo info2 = findCallingCodeInfo(rest);
        if (info2 == null || !info2.isValidPhoneNumber(rest)) {
            return false;
        }
        return true;
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
                ThrowableExtension.printStackTrace(e);
                return TtmlNode.ANONYMOUS_REGION_ID;
            }
        }
        return TtmlNode.ANONYMOUS_REGION_ID;
    }

    public CallingCodeInfo callingCodeInfo(String callingCode) {
        CallingCodeInfo res = (CallingCodeInfo) this.callingCodeData.get(callingCode);
        if (res == null) {
            Integer num = (Integer) this.callingCodeOffsets.get(callingCode);
            if (num != null) {
                String str;
                byte[] bytes = this.data;
                int start = num.intValue();
                int offset = start;
                res = new CallingCodeInfo();
                res.callingCode = callingCode;
                res.countries = (ArrayList) this.callingCodeCountries.get(callingCode);
                this.callingCodeData.put(callingCode, res);
                int block1Len = value16(offset);
                offset = (offset + 2) + 2;
                int block2Len = value16(offset);
                offset = (offset + 2) + 2;
                int setCnt = value16(offset);
                offset = (offset + 2) + 2;
                ArrayList<String> strs = new ArrayList(5);
                while (true) {
                    str = valueString(offset);
                    if (str.length() == 0) {
                        break;
                    }
                    strs.add(str);
                    offset += str.length() + 1;
                }
                res.trunkPrefixes = strs;
                offset++;
                strs = new ArrayList(5);
                while (true) {
                    str = valueString(offset);
                    if (str.length() == 0) {
                        break;
                    }
                    strs.add(str);
                    offset += str.length() + 1;
                }
                res.intlPrefixes = strs;
                ArrayList<RuleSet> ruleSets = new ArrayList(setCnt);
                offset = start + block1Len;
                for (int s = 0; s < setCnt; s++) {
                    RuleSet ruleSet = new RuleSet();
                    ruleSet.matchLen = value16(offset);
                    offset += 2;
                    int ruleCnt = value16(offset);
                    offset += 2;
                    ArrayList<PhoneRule> arrayList = new ArrayList(ruleCnt);
                    for (int r = 0; r < ruleCnt; r++) {
                        PhoneRule rule = new PhoneRule();
                        rule.minVal = value32(offset);
                        offset += 4;
                        rule.maxVal = value32(offset);
                        offset += 4;
                        int offset2 = offset + 1;
                        rule.byte8 = bytes[offset];
                        offset = offset2 + 1;
                        rule.maxLen = bytes[offset2];
                        offset2 = offset + 1;
                        rule.otherFlag = bytes[offset];
                        offset = offset2 + 1;
                        rule.prefixLen = bytes[offset2];
                        offset2 = offset + 1;
                        rule.flag12 = bytes[offset];
                        offset = offset2 + 1;
                        rule.flag13 = bytes[offset2];
                        int strOffset = value16(offset);
                        offset += 2;
                        rule.format = valueString(((start + block1Len) + block2Len) + strOffset);
                        if (rule.format.indexOf("[[") != -1) {
                            int closePos = rule.format.indexOf("]]");
                            rule.format = String.format("%s%s", new Object[]{rule.format.substring(0, openPos), rule.format.substring(closePos + 2)});
                        }
                        arrayList.add(rule);
                        if (rule.hasIntlPrefix) {
                            ruleSet.hasRuleWithIntlPrefix = true;
                        }
                        if (rule.hasTrunkPrefix) {
                            ruleSet.hasRuleWithTrunkPrefix = true;
                        }
                    }
                    ruleSet.rules = arrayList;
                    ruleSets.add(ruleSet);
                }
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
        }
        if (this.defaultCallingCode != null) {
            callingCodeInfo(this.defaultCallingCode);
        }
    }
}
