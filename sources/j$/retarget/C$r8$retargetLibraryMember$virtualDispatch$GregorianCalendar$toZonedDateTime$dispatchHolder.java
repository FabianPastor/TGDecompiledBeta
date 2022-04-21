package j$.retarget;

import j$.time.ZonedDateTime;
import j$.util.DesugarGregorianCalendar;
import java.util.GregorianCalendar;

/* renamed from: j$.retarget.$r8$retargetLibraryMember$virtualDispatch$GregorianCalendar$toZonedDateTime$dispatchHolder  reason: invalid class name */
public /* synthetic */ class C$r8$retargetLibraryMember$virtualDispatch$GregorianCalendar$toZonedDateTime$dispatchHolder {
    public static /* synthetic */ ZonedDateTime toZonedDateTime(GregorianCalendar gregorianCalendar) {
        return gregorianCalendar instanceof C$r8$retargetLibraryMember$virtualDispatch$GregorianCalendar$toZonedDateTime$dispatchInterface ? ((C$r8$retargetLibraryMember$virtualDispatch$GregorianCalendar$toZonedDateTime$dispatchInterface) gregorianCalendar).toZonedDateTime() : DesugarGregorianCalendar.toZonedDateTime(gregorianCalendar);
    }
}
