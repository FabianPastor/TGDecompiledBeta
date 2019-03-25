package org.telegram.PhoneFormat;

import com.google.devtools.build.android.desugar.runtime.ThrowableExtension;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
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
        Throwable th;
        PhoneFormat localInstance = Instance;
        if (localInstance == null) {
            synchronized (PhoneFormat.class) {
                try {
                    localInstance = Instance;
                    if (localInstance == null) {
                        PhoneFormat localInstance2 = new PhoneFormat();
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
        init(null);
    }

    public PhoneFormat(String countryCode) {
        init(countryCode);
    }

    /* JADX WARNING: Removed duplicated region for block: B:43:0x009f A:{SYNTHETIC, Splitter:B:43:0x009f} */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x00a4 A:{SYNTHETIC, Splitter:B:46:0x00a4} */
    /* JADX WARNING: Removed duplicated region for block: B:15:0x0030 A:{SYNTHETIC, Splitter:B:15:0x0030} */
    /* JADX WARNING: Removed duplicated region for block: B:58:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:18:0x0035 A:{SYNTHETIC, Splitter:B:18:0x0035} */
    public void init(java.lang.String r11) {
        /*
        r10 = this;
        r9 = 255; // 0xff float:3.57E-43 double:1.26E-321;
        r6 = 0;
        r0 = 0;
        r7 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x00c4 }
        r7 = r7.getAssets();	 Catch:{ Exception -> 0x00c4 }
        r8 = "PhoneFormats.dat";
        r6 = r7.open(r8);	 Catch:{ Exception -> 0x00c4 }
        r1 = new java.io.ByteArrayOutputStream;	 Catch:{ Exception -> 0x00c4 }
        r1.<init>();	 Catch:{ Exception -> 0x00c4 }
        r7 = 1024; // 0x400 float:1.435E-42 double:5.06E-321;
        r2 = new byte[r7];	 Catch:{ Exception -> 0x0029, all -> 0x00c1 }
    L_0x001a:
        r7 = 0;
        r8 = 1024; // 0x400 float:1.435E-42 double:5.06E-321;
        r4 = r6.read(r2, r7, r8);	 Catch:{ Exception -> 0x0029, all -> 0x00c1 }
        r7 = -1;
        if (r4 == r7) goto L_0x0039;
    L_0x0024:
        r7 = 0;
        r1.write(r2, r7, r4);	 Catch:{ Exception -> 0x0029, all -> 0x00c1 }
        goto L_0x001a;
    L_0x0029:
        r3 = move-exception;
        r0 = r1;
    L_0x002b:
        com.google.devtools.build.android.desugar.runtime.ThrowableExtension.printStackTrace(r3);	 Catch:{ all -> 0x009c }
        if (r0 == 0) goto L_0x0033;
    L_0x0030:
        r0.close();	 Catch:{ Exception -> 0x0092 }
    L_0x0033:
        if (r6 == 0) goto L_0x0038;
    L_0x0035:
        r6.close();	 Catch:{ Exception -> 0x0097 }
    L_0x0038:
        return;
    L_0x0039:
        r7 = r1.toByteArray();	 Catch:{ Exception -> 0x0029, all -> 0x00c1 }
        r10.data = r7;	 Catch:{ Exception -> 0x0029, all -> 0x00c1 }
        r7 = r10.data;	 Catch:{ Exception -> 0x0029, all -> 0x00c1 }
        r7 = java.nio.ByteBuffer.wrap(r7);	 Catch:{ Exception -> 0x0029, all -> 0x00c1 }
        r10.buffer = r7;	 Catch:{ Exception -> 0x0029, all -> 0x00c1 }
        r7 = r10.buffer;	 Catch:{ Exception -> 0x0029, all -> 0x00c1 }
        r8 = java.nio.ByteOrder.LITTLE_ENDIAN;	 Catch:{ Exception -> 0x0029, all -> 0x00c1 }
        r7.order(r8);	 Catch:{ Exception -> 0x0029, all -> 0x00c1 }
        if (r1 == 0) goto L_0x0053;
    L_0x0050:
        r1.close();	 Catch:{ Exception -> 0x0088 }
    L_0x0053:
        if (r6 == 0) goto L_0x0058;
    L_0x0055:
        r6.close();	 Catch:{ Exception -> 0x008d }
    L_0x0058:
        if (r11 == 0) goto L_0x00b2;
    L_0x005a:
        r7 = r11.length();
        if (r7 == 0) goto L_0x00b2;
    L_0x0060:
        r10.defaultCountry = r11;
    L_0x0062:
        r7 = new java.util.HashMap;
        r7.<init>(r9);
        r10.callingCodeOffsets = r7;
        r7 = new java.util.HashMap;
        r7.<init>(r9);
        r10.callingCodeCountries = r7;
        r7 = new java.util.HashMap;
        r8 = 10;
        r7.<init>(r8);
        r10.callingCodeData = r7;
        r7 = new java.util.HashMap;
        r7.<init>(r9);
        r10.countryCallingCode = r7;
        r10.parseDataHeader();
        r7 = 1;
        r10.initialzed = r7;
        r0 = r1;
        goto L_0x0038;
    L_0x0088:
        r3 = move-exception;
        org.telegram.messenger.FileLog.e(r3);
        goto L_0x0053;
    L_0x008d:
        r3 = move-exception;
        org.telegram.messenger.FileLog.e(r3);
        goto L_0x0058;
    L_0x0092:
        r3 = move-exception;
        org.telegram.messenger.FileLog.e(r3);
        goto L_0x0033;
    L_0x0097:
        r3 = move-exception;
        org.telegram.messenger.FileLog.e(r3);
        goto L_0x0038;
    L_0x009c:
        r7 = move-exception;
    L_0x009d:
        if (r0 == 0) goto L_0x00a2;
    L_0x009f:
        r0.close();	 Catch:{ Exception -> 0x00a8 }
    L_0x00a2:
        if (r6 == 0) goto L_0x00a7;
    L_0x00a4:
        r6.close();	 Catch:{ Exception -> 0x00ad }
    L_0x00a7:
        throw r7;
    L_0x00a8:
        r3 = move-exception;
        org.telegram.messenger.FileLog.e(r3);
        goto L_0x00a2;
    L_0x00ad:
        r3 = move-exception;
        org.telegram.messenger.FileLog.e(r3);
        goto L_0x00a7;
    L_0x00b2:
        r5 = java.util.Locale.getDefault();
        r7 = r5.getCountry();
        r7 = r7.toLowerCase();
        r10.defaultCountry = r7;
        goto L_0x0062;
    L_0x00c1:
        r7 = move-exception;
        r0 = r1;
        goto L_0x009d;
    L_0x00c4:
        r3 = move-exception;
        goto L_0x002b;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.PhoneFormat.PhoneFormat.init(java.lang.String):void");
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
            String str = strip(orig);
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
        } catch (Exception e) {
            FileLog.e(e);
            return orig;
        }
    }

    public boolean isPhoneNumberValid(String phoneNumber) {
        if (!this.initialzed) {
            return true;
        }
        String str = strip(phoneNumber);
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

    /* Access modifiers changed, original: 0000 */
    public int value32(int offset) {
        if (offset + 4 > this.data.length) {
            return 0;
        }
        this.buffer.position(offset);
        return this.buffer.getInt();
    }

    /* Access modifiers changed, original: 0000 */
    public short value16(int offset) {
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
                    return "";
                } else {
                    return new String(this.data, offset, a - offset);
                }
            } catch (Exception e) {
                ThrowableExtension.printStackTrace(e);
                return "";
            }
        }
        return "";
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
