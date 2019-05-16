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

    /* Access modifiers changed, original: 0000 */
    public String format(String str, String str2, String str3) {
        StringBuilder stringBuilder = new StringBuilder(20);
        int i = 0;
        Object obj = null;
        int i2 = 0;
        Object obj2 = null;
        Object obj3 = null;
        while (i < this.format.length()) {
            char charAt = this.format.charAt(i);
            if (charAt != '#') {
                if (charAt != '(') {
                    if (charAt == 'c') {
                        if (str2 != null) {
                            stringBuilder.append(str2);
                        }
                        obj = 1;
                    } else if (charAt == 'n') {
                        if (str3 != null) {
                            stringBuilder.append(str3);
                        }
                        obj2 = 1;
                    }
                } else if (i2 < str.length()) {
                    obj3 = 1;
                }
                if (charAt == ' ' && i > 0) {
                    int i3 = i - 1;
                    if (this.format.charAt(i3) == 'n') {
                        if (str3 == null) {
                        }
                    }
                    if (this.format.charAt(i3) == 'c' && str2 == null) {
                    }
                }
                if (i2 < str.length() || (obj3 != null && charAt == ')')) {
                    stringBuilder.append(this.format.substring(i, i + 1));
                    if (charAt == ')') {
                        obj3 = null;
                    }
                }
            } else if (i2 < str.length()) {
                int i4 = i2 + 1;
                stringBuilder.append(str.substring(i2, i4));
                i2 = i4;
            } else if (obj3 != null) {
                stringBuilder.append(" ");
            }
            i++;
        }
        if (str2 != null && obj == null) {
            stringBuilder.insert(0, String.format("%s ", new Object[]{str2}));
        } else if (str3 != null && obj2 == null) {
            stringBuilder.insert(0, str3);
        }
        return stringBuilder.toString();
    }

    /* Access modifiers changed, original: 0000 */
    public boolean hasIntlPrefix() {
        return (this.flag12 & 2) != 0;
    }

    /* Access modifiers changed, original: 0000 */
    public boolean hasTrunkPrefix() {
        return (this.flag12 & 1) != 0;
    }
}
