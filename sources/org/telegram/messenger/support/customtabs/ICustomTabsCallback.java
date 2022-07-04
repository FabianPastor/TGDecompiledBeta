package org.telegram.messenger.support.customtabs;

import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface ICustomTabsCallback extends IInterface {
    void extraCallback(String str, Bundle bundle) throws RemoteException;

    void onMessageChannelReady(Bundle bundle) throws RemoteException;

    void onNavigationEvent(int i, Bundle bundle) throws RemoteException;

    void onPostMessage(String str, Bundle bundle) throws RemoteException;

    public static abstract class Stub extends Binder implements ICustomTabsCallback {
        private static final String DESCRIPTOR = "android.support.customtabs.ICustomTabsCallback";
        static final int TRANSACTION_extraCallback = 3;
        static final int TRANSACTION_onMessageChannelReady = 4;
        static final int TRANSACTION_onNavigationEvent = 2;
        static final int TRANSACTION_onPostMessage = 5;

        public Stub() {
            attachInterface(this, "android.support.customtabs.ICustomTabsCallback");
        }

        public static ICustomTabsCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface("android.support.customtabs.ICustomTabsCallback");
            return (iin == null || !(iin instanceof ICustomTabsCallback)) ? new Proxy(obj) : (ICustomTabsCallback) iin;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            Bundle _arg1;
            Bundle _arg12;
            Bundle _arg01;
            Bundle _arg13;
            switch (code) {
                case 2:
                    data.enforceInterface("android.support.customtabs.ICustomTabsCallback");
                    int _arg02 = data.readInt();
                    if (data.readInt() != 0) {
                        _arg1 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg1 = null;
                    }
                    onNavigationEvent(_arg02, _arg1);
                    reply.writeNoException();
                    return true;
                case 3:
                    data.enforceInterface("android.support.customtabs.ICustomTabsCallback");
                    String _arg0 = data.readString();
                    if (data.readInt() != 0) {
                        _arg12 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg12 = null;
                    }
                    extraCallback(_arg0, _arg12);
                    reply.writeNoException();
                    return true;
                case 4:
                    data.enforceInterface("android.support.customtabs.ICustomTabsCallback");
                    if (data.readInt() != 0) {
                        _arg01 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg01 = null;
                    }
                    onMessageChannelReady(_arg01);
                    reply.writeNoException();
                    return true;
                case 5:
                    data.enforceInterface("android.support.customtabs.ICustomTabsCallback");
                    String _arg03 = data.readString();
                    if (data.readInt() != 0) {
                        _arg13 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg13 = null;
                    }
                    onPostMessage(_arg03, _arg13);
                    reply.writeNoException();
                    return true;
                case 1598968902:
                    reply.writeString("android.support.customtabs.ICustomTabsCallback");
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        private static class Proxy implements ICustomTabsCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return "android.support.customtabs.ICustomTabsCallback";
            }

            public void onNavigationEvent(int navigationEvent, Bundle extras) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken("android.support.customtabs.ICustomTabsCallback");
                    _data.writeInt(navigationEvent);
                    if (extras != null) {
                        _data.writeInt(1);
                        extras.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void extraCallback(String callbackName, Bundle args) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken("android.support.customtabs.ICustomTabsCallback");
                    _data.writeString(callbackName);
                    if (args != null) {
                        _data.writeInt(1);
                        args.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void onMessageChannelReady(Bundle extras) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken("android.support.customtabs.ICustomTabsCallback");
                    if (extras != null) {
                        _data.writeInt(1);
                        extras.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void onPostMessage(String message, Bundle extras) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken("android.support.customtabs.ICustomTabsCallback");
                    _data.writeString(message);
                    if (extras != null) {
                        _data.writeInt(1);
                        extras.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }
    }
}
