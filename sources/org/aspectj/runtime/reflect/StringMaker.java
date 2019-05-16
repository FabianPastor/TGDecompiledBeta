package org.aspectj.runtime.reflect;

import java.lang.reflect.Modifier;

class StringMaker {
    static StringMaker longStringMaker = new StringMaker();
    static StringMaker middleStringMaker = new StringMaker();
    static StringMaker shortStringMaker = new StringMaker();
    int cacheOffset;
    boolean includeArgs = true;
    boolean includeEnclosingPoint = true;
    boolean includeJoinPointTypeName = true;
    boolean includeModifiers = false;
    boolean includeThrows = false;
    boolean shortKindName = true;
    boolean shortPrimaryTypeNames = false;
    boolean shortTypeNames = true;

    StringMaker() {
    }

    static {
        StringMaker stringMaker = shortStringMaker;
        stringMaker.shortTypeNames = true;
        stringMaker.includeArgs = false;
        stringMaker.includeThrows = false;
        stringMaker.includeModifiers = false;
        stringMaker.shortPrimaryTypeNames = true;
        stringMaker.includeJoinPointTypeName = false;
        stringMaker.includeEnclosingPoint = false;
        stringMaker.cacheOffset = 0;
        stringMaker = middleStringMaker;
        stringMaker.shortTypeNames = true;
        stringMaker.includeArgs = true;
        stringMaker.includeThrows = false;
        stringMaker.includeModifiers = false;
        stringMaker.shortPrimaryTypeNames = false;
        shortStringMaker.cacheOffset = 1;
        stringMaker = longStringMaker;
        stringMaker.shortTypeNames = false;
        stringMaker.includeArgs = true;
        stringMaker.includeThrows = false;
        stringMaker.includeModifiers = true;
        stringMaker.shortPrimaryTypeNames = false;
        stringMaker.shortKindName = false;
        stringMaker.cacheOffset = 2;
    }

    /* Access modifiers changed, original: 0000 */
    public String makeKindName(String str) {
        int lastIndexOf = str.lastIndexOf(45);
        if (lastIndexOf == -1) {
            return str;
        }
        return str.substring(lastIndexOf + 1);
    }

    /* Access modifiers changed, original: 0000 */
    public String makeModifiersString(int i) {
        String str = "";
        if (!this.includeModifiers) {
            return str;
        }
        String modifier = Modifier.toString(i);
        if (modifier.length() == 0) {
            return str;
        }
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(modifier);
        stringBuffer.append(" ");
        return stringBuffer.toString();
    }

    /* Access modifiers changed, original: 0000 */
    public String stripPackageName(String str) {
        int lastIndexOf = str.lastIndexOf(46);
        if (lastIndexOf == -1) {
            return str;
        }
        return str.substring(lastIndexOf + 1);
    }

    /* Access modifiers changed, original: 0000 */
    public String makeTypeName(Class cls, String str, boolean z) {
        if (cls == null) {
            return "ANONYMOUS";
        }
        if (cls.isArray()) {
            cls = cls.getComponentType();
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(makeTypeName(cls, cls.getName(), z));
            stringBuffer.append("[]");
            return stringBuffer.toString();
        } else if (z) {
            return stripPackageName(str).replace('$', '.');
        } else {
            return str.replace('$', '.');
        }
    }

    public String makeTypeName(Class cls) {
        return makeTypeName(cls, cls.getName(), this.shortTypeNames);
    }

    public String makePrimaryTypeName(Class cls, String str) {
        return makeTypeName(cls, str, this.shortPrimaryTypeNames);
    }

    public void addTypeNames(StringBuffer stringBuffer, Class[] clsArr) {
        for (int i = 0; i < clsArr.length; i++) {
            if (i > 0) {
                stringBuffer.append(", ");
            }
            stringBuffer.append(makeTypeName(clsArr[i]));
        }
    }

    public void addSignature(StringBuffer stringBuffer, Class[] clsArr) {
        if (clsArr != null) {
            if (this.includeArgs) {
                stringBuffer.append("(");
                addTypeNames(stringBuffer, clsArr);
                stringBuffer.append(")");
            } else if (clsArr.length == 0) {
                stringBuffer.append("()");
            } else {
                stringBuffer.append("(..)");
            }
        }
    }

    public void addThrows(StringBuffer stringBuffer, Class[] clsArr) {
        if (this.includeThrows && clsArr != null && clsArr.length != 0) {
            stringBuffer.append(" throws ");
            addTypeNames(stringBuffer, clsArr);
        }
    }
}
