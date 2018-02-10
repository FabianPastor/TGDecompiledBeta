package org.telegram.messenger.time;

import java.util.Calendar;
import java.util.TimeZone;

public class SunDate {
    private static final double DEGRAD = 0.017453292519943295d;
    private static final double INV360 = 0.002777777777777778d;
    private static final double RADEG = 57.29577951308232d;

    private static long days_since_2000_Jan_0(int y, int m, int d) {
        return ((((367 * ((long) y)) - ((long) (((((m + 9) / 12) + y) * 7) / 4))) + ((long) ((m * 275) / 9))) + ((long) d)) - 730530;
    }

    private static double revolution(double x) {
        return x - (360.0d * Math.floor(INV360 * x));
    }

    private static double rev180(double x) {
        return x - (360.0d * Math.floor((INV360 * x) + 0.5d));
    }

    private static double GMST0(double d) {
        return revolution(818.9874d + (0.985647352d * d));
    }

    private static double sind(double x) {
        return Math.sin(DEGRAD * x);
    }

    private static double cosd(double x) {
        return Math.cos(DEGRAD * x);
    }

    private static double tand(double x) {
        return Math.tan(DEGRAD * x);
    }

    private static double acosd(double x) {
        return RADEG * Math.acos(x);
    }

    private static double atan2d(double y, double x) {
        return RADEG * Math.atan2(y, x);
    }

    private static void sunposAtDay(double p, double[] ot, double[] d) {
        double S = revolution(356.047d + (0.9856002585d * p));
        double l = 282.9404d + (4.70935E-5d * p);
        double a = 0.016709d - (1.151E-9d * p);
        double V = (((RADEG * a) * sind(S)) * (1.0d + (cosd(S) * a))) + S;
        double k = cosd(V) - a;
        double i = Math.sqrt(1.0d - (a * a)) * sind(V);
        d[0] = Math.sqrt((k * k) + (i * i));
        ot[0] = atan2d(i, k) + l;
        if (ot[0] >= 360.0d) {
            ot[0] = ot[0] - 360.0d;
        }
    }

    private static void sun_RA_decAtDay(double d, double[] RA, double[] dec, double[] r) {
        double[] lon = new double[1];
        sunposAtDay(d, lon, r);
        double ys = r[0] * sind(lon[0]);
        double obl_ecl = 23.4393d - (3.563E-7d * d);
        double xe = r[0] * cosd(lon[0]);
        double ye = ys * cosd(obl_ecl);
        double ze = ys * sind(obl_ecl);
        RA[0] = atan2d(ye, xe);
        dec[0] = atan2d(ze, Math.sqrt((xe * xe) + (ye * ye)));
    }

    private static int sunRiseSetHelperForYear(int year, int month, int day, double lon, double lat, double altit, int upper_limb, double[] sun) {
        double t;
        double[] sRA = new double[1];
        double[] sdec = new double[1];
        double[] sr = new double[1];
        int rc = 0;
        double d = (((double) days_since_2000_Jan_0(year, month, day)) + 0.5d) - (lon / 360.0d);
        double sidtime = revolution((GMST0(d) + 180.0d) + lon);
        sun_RA_decAtDay(d, sRA, sdec, sr);
        double tsouth = 12.0d - (rev180(sidtime - sRA[0]) / 15.0d);
        double sradius = 0.2666d / sr[0];
        if (upper_limb != 0) {
            altit -= sradius;
        }
        double cost = (sind(altit) - (sind(lat) * sind(sdec[0]))) / (cosd(lat) * cosd(sdec[0]));
        if (cost >= 1.0d) {
            rc = -1;
            t = 0.0d;
        } else if (cost <= -1.0d) {
            rc = 1;
            t = 12.0d;
        } else {
            t = acosd(cost) / 15.0d;
        }
        sun[0] = tsouth - t;
        sun[1] = tsouth + t;
        return rc;
    }

    private static int sunRiseSetForYear(int year, int month, int day, double lon, double lat, double[] sun) {
        return sunRiseSetHelperForYear(year, month, day, lon, lat, -0.5833333333333334d, 1, sun);
    }

    public static int[] calculateSunriseSunset(double lat, double lon) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        double[] sun = new double[2];
        sunRiseSetForYear(calendar.get(1), calendar.get(2), calendar.get(5), lon, lat, sun);
        int timeZoneOffset = (TimeZone.getDefault().getRawOffset() / 1000) / 60;
        int sunrise = ((int) (sun[0] * 60.0d)) + timeZoneOffset;
        int sunset = ((int) (sun[1] * 60.0d)) + timeZoneOffset;
        if (sunrise < 0) {
            sunrise += 1440;
        } else if (sunrise > 1440) {
            sunrise -= 1440;
        }
        if (sunset < 0) {
            sunset += 1440;
        } else if (sunset > 1440) {
            sunset += 1440;
        }
        return new int[]{sunrise, sunset};
    }
}
