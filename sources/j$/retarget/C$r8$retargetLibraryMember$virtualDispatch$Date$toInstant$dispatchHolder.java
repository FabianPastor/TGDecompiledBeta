package j$.retarget;

import j$.time.Instant;
import j$.util.DesugarDate;
import java.util.Date;

/* renamed from: j$.retarget.$r8$retargetLibraryMember$virtualDispatch$Date$toInstant$dispatchHolder  reason: invalid class name */
public /* synthetic */ class C$r8$retargetLibraryMember$virtualDispatch$Date$toInstant$dispatchHolder {
    public static /* synthetic */ Instant toInstant(Date date) {
        return date instanceof C$r8$retargetLibraryMember$virtualDispatch$Date$toInstant$dispatchInterface ? ((C$r8$retargetLibraryMember$virtualDispatch$Date$toInstant$dispatchInterface) date).toInstant() : DesugarDate.toInstant(date);
    }
}
