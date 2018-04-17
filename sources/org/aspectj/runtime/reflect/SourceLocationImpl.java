package org.aspectj.runtime.reflect;

import org.aspectj.lang.reflect.SourceLocation;

class SourceLocationImpl implements SourceLocation {
    String fileName;
    int line;
    Class withinType;

    SourceLocationImpl(Class withinType, String fileName, int line) {
        this.withinType = withinType;
        this.fileName = fileName;
        this.line = line;
    }

    public String getFileName() {
        return this.fileName;
    }

    public int getLine() {
        return this.line;
    }

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(getFileName());
        stringBuffer.append(":");
        stringBuffer.append(getLine());
        return stringBuffer.toString();
    }
}
