package com.google.android.search.verification.api;

import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface ISearchActionVerificationService extends IInterface {

    public static abstract class Stub extends Binder implements ISearchActionVerificationService {

        private static class Proxy implements ISearchActionVerificationService {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public boolean isSearchAction(Intent intent, Bundle options) throws RemoteException {
                boolean _result = true;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken("com.google.android.search.verification.api.ISearchActionVerificationService");
                    if (intent != null) {
                        _data.writeInt(1);
                        intent.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (options != null) {
                        _data.writeInt(1);
                        options.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() == 0) {
                        _result = false;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public int getVersion() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken("com.google.android.search.verification.api.ISearchActionVerificationService");
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        public static ISearchActionVerificationService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface("com.google.android.search.verification.api.ISearchActionVerificationService");
            if (iin == null || !(iin instanceof ISearchActionVerificationService)) {
                return new Proxy(obj);
            }
            return (ISearchActionVerificationService) iin;
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            switch (code) {
                case 1:
                    Intent _arg0;
                    Bundle _arg1;
                    int i;
                    data.enforceInterface("com.google.android.search.verification.api.ISearchActionVerificationService");
                    if (data.readInt() != 0) {
                        _arg0 = (Intent) Intent.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    if (data.readInt() != 0) {
                        _arg1 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg1 = null;
                    }
                    boolean _result = isSearchAction(_arg0, _arg1);
                    reply.writeNoException();
                    if (_result) {
                        i = 1;
                    } else {
                        i = 0;
                    }
                    reply.writeInt(i);
                    return true;
                case 2:
                    data.enforceInterface("com.google.android.search.verification.api.ISearchActionVerificationService");
                    int _result2 = getVersion();
                    reply.writeNoException();
                    reply.writeInt(_result2);
                    return true;
                case 1598968902:
                    reply.writeString("com.google.android.search.verification.api.ISearchActionVerificationService");
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }
    }

    int getVersion() throws RemoteException;

    boolean isSearchAction(Intent intent, Bundle bundle) throws RemoteException;
}
