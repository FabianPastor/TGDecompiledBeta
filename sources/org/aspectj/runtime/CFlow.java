package org.aspectj.runtime;

public class CFlow {
    private Object _aspect;

    public CFlow() {
        this((Object) null);
    }

    public CFlow(Object _aspect2) {
        this._aspect = _aspect2;
    }

    public Object getAspect() {
        return this._aspect;
    }

    public void setAspect(Object _aspect2) {
        this._aspect = _aspect2;
    }

    public Object get(int index) {
        return null;
    }
}
