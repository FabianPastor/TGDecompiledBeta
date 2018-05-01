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
        shortStringMaker.shortTypeNames = true;
        shortStringMaker.includeArgs = false;
        shortStringMaker.includeThrows = false;
        shortStringMaker.includeModifiers = false;
        shortStringMaker.shortPrimaryTypeNames = true;
        shortStringMaker.includeJoinPointTypeName = false;
        shortStringMaker.includeEnclosingPoint = false;
        shortStringMaker.cacheOffset = 0;
        middleStringMaker.shortTypeNames = true;
        middleStringMaker.includeArgs = true;
        middleStringMaker.includeThrows = false;
        middleStringMaker.includeModifiers = false;
        middleStringMaker.shortPrimaryTypeNames = false;
        shortStringMaker.cacheOffset = 1;
        longStringMaker.shortTypeNames = false;
        longStringMaker.includeArgs = true;
        longStringMaker.includeThrows = false;
        longStringMaker.includeModifiers = true;
        longStringMaker.shortPrimaryTypeNames = false;
        longStringMaker.shortKindName = false;
        longStringMaker.cacheOffset = 2;
    }

    String makeKindName(String str) {
        int lastIndexOf = str.lastIndexOf(45);
        if (lastIndexOf == -1) {
            return str;
        }
        return str.substring(lastIndexOf + 1);
    }

    String makeModifiersString(int i) {
        if (!this.includeModifiers) {
            return TtmlNode.ANONYMOUS_REGION_ID;
        }
        i = Modifier.toString(i);
        if (i.length() == 0) {
            return TtmlNode.ANONYMOUS_REGION_ID;
        }
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(i);
        stringBuffer.append(" ");
        return stringBuffer.toString();
    }

    String stripPackageName(String str) {
        int lastIndexOf = str.lastIndexOf(46);
        if (lastIndexOf == -1) {
            return str;
        }
        return str.substring(lastIndexOf + 1);
    }

    String makeTypeName(Class cls, String str, boolean z) {
        if (cls == null) {
            return "ANONYMOUS";
        }
        if (cls.isArray()) {
            cls = cls.getComponentType();
            str = new StringBuffer();
            str.append(makeTypeName(cls, cls.getName(), z));
            str.append("[]");
            return str.toString();
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
            } else if (clsArr.length == null) {
                stringBuffer.append("()");
            } else {
                stringBuffer.append("(..)");
            }
        }
    }

    public void addThrows(StringBuffer stringBuffer, Class[] clsArr) {
        if (this.includeThrows && clsArr != null) {
            if (clsArr.length != 0) {
                stringBuffer.append(" throws ");
                addTypeNames(stringBuffer, clsArr);
            }
        }
    }
}
