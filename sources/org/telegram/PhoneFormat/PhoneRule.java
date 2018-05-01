package org.telegram.PhoneFormat;

public class PhoneRule {
    public int byte8;
    public int flag12;
    public int flag13;
    public String format;
    public boolean hasIntlPrefix;
    public boolean hasTrunkPrefix;
    public int maxLen;
    public int maxVal;
    public int minVal;
    public int otherFlag;
    public int prefixLen;

    String format(String str, String str2, String str3) {
        StringBuilder stringBuilder = new StringBuilder(20);
        int i = 0;
        int i2 = i;
        int i3 = i2;
        int i4 = i3;
        int i5 = i4;
        while (i < this.format.length()) {
            char charAt = this.format.charAt(i);
            if (charAt != '#') {
                if (charAt != '(') {
                    if (charAt == 'c') {
                        if (str2 != null) {
                            stringBuilder.append(str2);
                        }
                        i3 = 1;
                    } else if (charAt == 'n') {
                        if (str3 != null) {
                            stringBuilder.append(str3);
                        }
                        i5 = 1;
                    }
                } else if (i2 < str.length()) {
                    i4 = 1;
                }
                if (charAt == ' ' && i > 0) {
                    int i6 = i - 1;
                    if (this.format.charAt(i6) == 'n') {
                        if (str3 == null) {
                        }
                    }
                    if (this.format.charAt(i6) == 'c' && str2 == null) {
                    }
                }
                if (i2 < str.length() || (r5 != 0 && charAt == ')')) {
                    stringBuilder.append(this.format.substring(i, i + 1));
                    if (charAt == ')') {
                        i4 = 0;
                    }
                }
            } else if (i2 < str.length()) {
                int i7 = i2 + 1;
                stringBuilder.append(str.substring(i2, i7));
                i2 = i7;
            } else if (i4 != 0) {
                stringBuilder.append(" ");
            }
            i++;
        }
        if (str2 != null && r4 == 0) {
            stringBuilder.insert(0, String.format("%s ", new Object[]{str2}));
        } else if (str3 != null && r6 == 0) {
            stringBuilder.insert(0, str3);
        }
        return stringBuilder.toString();
    }

    boolean hasIntlPrefix() {
        return (this.flag12 & 2) != 0;
    }

    boolean hasTrunkPrefix() {
        return (this.flag12 & 1) != 0;
    }
}
