package org.telegram.messenger.support.customtabs;

import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import java.util.List;

public interface ICustomTabsService extends IInterface {
    Bundle extraCommand(String str, Bundle bundle) throws RemoteException;

    boolean mayLaunchUrl(ICustomTabsCallback iCustomTabsCallback, Uri uri, Bundle bundle, List<Bundle> list) throws RemoteException;

    boolean newSession(ICustomTabsCallback iCustomTabsCallback) throws RemoteException;

    int postMessage(ICustomTabsCallback iCustomTabsCallback, String str, Bundle bundle) throws RemoteException;

    boolean requestPostMessageChannel(ICustomTabsCallback iCustomTabsCallback, Uri uri) throws RemoteException;

    boolean updateVisuals(ICustomTabsCallback iCustomTabsCallback, Bundle bundle) throws RemoteException;

    boolean warmup(long j) throws RemoteException;

    public static abstract class Stub extends Binder implements ICustomTabsService {
        private static final String DESCRIPTOR = "android.support.customtabs.ICustomTabsService";
        static final int TRANSACTION_extraCommand = 5;
        static final int TRANSACTION_mayLaunchUrl = 4;
        static final int TRANSACTION_newSession = 3;
        static final int TRANSACTION_postMessage = 8;
        static final int TRANSACTION_requestPostMessageChannel = 7;
        static final int TRANSACTION_updateVisuals = 6;
        static final int TRANSACTION_warmup = 2;

        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, "android.support.customtabs.ICustomTabsService");
        }

        public static ICustomTabsService asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface("android.support.customtabs.ICustomTabsService");
            return (queryLocalInterface == null || !(queryLocalInterface instanceof ICustomTabsService)) ? new Proxy(iBinder) : (ICustomTabsService) queryLocalInterface;
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v2, resolved type: android.os.Bundle} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v6, resolved type: android.os.Bundle} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v9, resolved type: android.os.Bundle} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v12, resolved type: android.net.Uri} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v15, resolved type: android.os.Bundle} */
        /* JADX WARNING: type inference failed for: r0v1 */
        /* JADX WARNING: type inference failed for: r0v19 */
        /* JADX WARNING: type inference failed for: r0v20 */
        /* JADX WARNING: type inference failed for: r0v21 */
        /* JADX WARNING: type inference failed for: r0v22 */
        /* JADX WARNING: type inference failed for: r0v23 */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean onTransact(int r4, android.os.Parcel r5, android.os.Parcel r6, int r7) throws android.os.RemoteException {
            /*
                r3 = this;
                r0 = 1598968902(0x5f4e5446, float:1.4867585E19)
                java.lang.String r1 = "android.support.customtabs.ICustomTabsService"
                r2 = 1
                if (r4 == r0) goto L_0x0111
                r0 = 0
                switch(r4) {
                    case 2: goto L_0x00ff;
                    case 3: goto L_0x00e9;
                    case 4: goto L_0x00af;
                    case 5: goto L_0x0084;
                    case 6: goto L_0x005f;
                    case 7: goto L_0x003a;
                    case 8: goto L_0x0011;
                    default: goto L_0x000c;
                }
            L_0x000c:
                boolean r4 = super.onTransact(r4, r5, r6, r7)
                return r4
            L_0x0011:
                r5.enforceInterface(r1)
                android.os.IBinder r4 = r5.readStrongBinder()
                org.telegram.messenger.support.customtabs.ICustomTabsCallback r4 = org.telegram.messenger.support.customtabs.ICustomTabsCallback.Stub.asInterface(r4)
                java.lang.String r7 = r5.readString()
                int r1 = r5.readInt()
                if (r1 == 0) goto L_0x002f
                android.os.Parcelable$Creator r0 = android.os.Bundle.CREATOR
                java.lang.Object r5 = r0.createFromParcel(r5)
                r0 = r5
                android.os.Bundle r0 = (android.os.Bundle) r0
            L_0x002f:
                int r4 = r3.postMessage(r4, r7, r0)
                r6.writeNoException()
                r6.writeInt(r4)
                return r2
            L_0x003a:
                r5.enforceInterface(r1)
                android.os.IBinder r4 = r5.readStrongBinder()
                org.telegram.messenger.support.customtabs.ICustomTabsCallback r4 = org.telegram.messenger.support.customtabs.ICustomTabsCallback.Stub.asInterface(r4)
                int r7 = r5.readInt()
                if (r7 == 0) goto L_0x0054
                android.os.Parcelable$Creator r7 = android.net.Uri.CREATOR
                java.lang.Object r5 = r7.createFromParcel(r5)
                r0 = r5
                android.net.Uri r0 = (android.net.Uri) r0
            L_0x0054:
                boolean r4 = r3.requestPostMessageChannel(r4, r0)
                r6.writeNoException()
                r6.writeInt(r4)
                return r2
            L_0x005f:
                r5.enforceInterface(r1)
                android.os.IBinder r4 = r5.readStrongBinder()
                org.telegram.messenger.support.customtabs.ICustomTabsCallback r4 = org.telegram.messenger.support.customtabs.ICustomTabsCallback.Stub.asInterface(r4)
                int r7 = r5.readInt()
                if (r7 == 0) goto L_0x0079
                android.os.Parcelable$Creator r7 = android.os.Bundle.CREATOR
                java.lang.Object r5 = r7.createFromParcel(r5)
                r0 = r5
                android.os.Bundle r0 = (android.os.Bundle) r0
            L_0x0079:
                boolean r4 = r3.updateVisuals(r4, r0)
                r6.writeNoException()
                r6.writeInt(r4)
                return r2
            L_0x0084:
                r5.enforceInterface(r1)
                java.lang.String r4 = r5.readString()
                int r7 = r5.readInt()
                if (r7 == 0) goto L_0x009a
                android.os.Parcelable$Creator r7 = android.os.Bundle.CREATOR
                java.lang.Object r5 = r7.createFromParcel(r5)
                r0 = r5
                android.os.Bundle r0 = (android.os.Bundle) r0
            L_0x009a:
                android.os.Bundle r4 = r3.extraCommand(r4, r0)
                r6.writeNoException()
                if (r4 == 0) goto L_0x00aa
                r6.writeInt(r2)
                r4.writeToParcel(r6, r2)
                goto L_0x00ae
            L_0x00aa:
                r4 = 0
                r6.writeInt(r4)
            L_0x00ae:
                return r2
            L_0x00af:
                r5.enforceInterface(r1)
                android.os.IBinder r4 = r5.readStrongBinder()
                org.telegram.messenger.support.customtabs.ICustomTabsCallback r4 = org.telegram.messenger.support.customtabs.ICustomTabsCallback.Stub.asInterface(r4)
                int r7 = r5.readInt()
                if (r7 == 0) goto L_0x00c9
                android.os.Parcelable$Creator r7 = android.net.Uri.CREATOR
                java.lang.Object r7 = r7.createFromParcel(r5)
                android.net.Uri r7 = (android.net.Uri) r7
                goto L_0x00ca
            L_0x00c9:
                r7 = r0
            L_0x00ca:
                int r1 = r5.readInt()
                if (r1 == 0) goto L_0x00d8
                android.os.Parcelable$Creator r0 = android.os.Bundle.CREATOR
                java.lang.Object r0 = r0.createFromParcel(r5)
                android.os.Bundle r0 = (android.os.Bundle) r0
            L_0x00d8:
                android.os.Parcelable$Creator r1 = android.os.Bundle.CREATOR
                java.util.ArrayList r5 = r5.createTypedArrayList(r1)
                boolean r4 = r3.mayLaunchUrl(r4, r7, r0, r5)
                r6.writeNoException()
                r6.writeInt(r4)
                return r2
            L_0x00e9:
                r5.enforceInterface(r1)
                android.os.IBinder r4 = r5.readStrongBinder()
                org.telegram.messenger.support.customtabs.ICustomTabsCallback r4 = org.telegram.messenger.support.customtabs.ICustomTabsCallback.Stub.asInterface(r4)
                boolean r4 = r3.newSession(r4)
                r6.writeNoException()
                r6.writeInt(r4)
                return r2
            L_0x00ff:
                r5.enforceInterface(r1)
                long r4 = r5.readLong()
                boolean r4 = r3.warmup(r4)
                r6.writeNoException()
                r6.writeInt(r4)
                return r2
            L_0x0111:
                r6.writeString(r1)
                return r2
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.support.customtabs.ICustomTabsService.Stub.onTransact(int, android.os.Parcel, android.os.Parcel, int):boolean");
        }

        private static class Proxy implements ICustomTabsService {
            private IBinder mRemote;

            public String getInterfaceDescriptor() {
                return "android.support.customtabs.ICustomTabsService";
            }

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public boolean warmup(long j) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("android.support.customtabs.ICustomTabsService");
                    obtain.writeLong(j);
                    boolean z = false;
                    this.mRemote.transact(2, obtain, obtain2, 0);
                    obtain2.readException();
                    if (obtain2.readInt() != 0) {
                        z = true;
                    }
                    return z;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public boolean newSession(ICustomTabsCallback iCustomTabsCallback) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("android.support.customtabs.ICustomTabsService");
                    obtain.writeStrongBinder(iCustomTabsCallback != null ? iCustomTabsCallback.asBinder() : null);
                    boolean z = false;
                    this.mRemote.transact(3, obtain, obtain2, 0);
                    obtain2.readException();
                    if (obtain2.readInt() != 0) {
                        z = true;
                    }
                    return z;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public boolean mayLaunchUrl(ICustomTabsCallback iCustomTabsCallback, Uri uri, Bundle bundle, List<Bundle> list) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("android.support.customtabs.ICustomTabsService");
                    obtain.writeStrongBinder(iCustomTabsCallback != null ? iCustomTabsCallback.asBinder() : null);
                    boolean z = true;
                    if (uri != null) {
                        obtain.writeInt(1);
                        uri.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    if (bundle != null) {
                        obtain.writeInt(1);
                        bundle.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    obtain.writeTypedList(list);
                    this.mRemote.transact(4, obtain, obtain2, 0);
                    obtain2.readException();
                    if (obtain2.readInt() == 0) {
                        z = false;
                    }
                    return z;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public Bundle extraCommand(String str, Bundle bundle) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("android.support.customtabs.ICustomTabsService");
                    obtain.writeString(str);
                    if (bundle != null) {
                        obtain.writeInt(1);
                        bundle.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    this.mRemote.transact(5, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0 ? (Bundle) Bundle.CREATOR.createFromParcel(obtain2) : null;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public boolean updateVisuals(ICustomTabsCallback iCustomTabsCallback, Bundle bundle) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("android.support.customtabs.ICustomTabsService");
                    obtain.writeStrongBinder(iCustomTabsCallback != null ? iCustomTabsCallback.asBinder() : null);
                    boolean z = true;
                    if (bundle != null) {
                        obtain.writeInt(1);
                        bundle.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    this.mRemote.transact(6, obtain, obtain2, 0);
                    obtain2.readException();
                    if (obtain2.readInt() == 0) {
                        z = false;
                    }
                    return z;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public boolean requestPostMessageChannel(ICustomTabsCallback iCustomTabsCallback, Uri uri) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("android.support.customtabs.ICustomTabsService");
                    obtain.writeStrongBinder(iCustomTabsCallback != null ? iCustomTabsCallback.asBinder() : null);
                    boolean z = true;
                    if (uri != null) {
                        obtain.writeInt(1);
                        uri.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    this.mRemote.transact(7, obtain, obtain2, 0);
                    obtain2.readException();
                    if (obtain2.readInt() == 0) {
                        z = false;
                    }
                    return z;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public int postMessage(ICustomTabsCallback iCustomTabsCallback, String str, Bundle bundle) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("android.support.customtabs.ICustomTabsService");
                    obtain.writeStrongBinder(iCustomTabsCallback != null ? iCustomTabsCallback.asBinder() : null);
                    obtain.writeString(str);
                    if (bundle != null) {
                        obtain.writeInt(1);
                        bundle.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    this.mRemote.transact(8, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }
    }
}
