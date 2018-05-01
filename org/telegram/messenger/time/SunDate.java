package org.telegram.messenger.time;

import java.util.Calendar;
import java.util.TimeZone;

public class SunDate
{
  private static final double DEGRAD = 0.017453292519943295D;
  private static final double INV360 = 0.002777777777777778D;
  private static final double RADEG = 57.29577951308232D;
  
  private static double GMST0(double paramDouble)
  {
    return revolution(818.9874D + 0.985647352D * paramDouble);
  }
  
  private static double acosd(double paramDouble)
  {
    return 57.29577951308232D * Math.acos(paramDouble);
  }
  
  private static double atan2d(double paramDouble1, double paramDouble2)
  {
    return 57.29577951308232D * Math.atan2(paramDouble1, paramDouble2);
  }
  
  public static int[] calculateSunriseSunset(double paramDouble1, double paramDouble2)
  {
    Calendar localCalendar = Calendar.getInstance();
    localCalendar.setTimeInMillis(System.currentTimeMillis());
    double[] arrayOfDouble = new double[2];
    sunRiseSetForYear(localCalendar.get(1), localCalendar.get(2) + 1, localCalendar.get(5), paramDouble2, paramDouble1, arrayOfDouble);
    int i = TimeZone.getDefault().getOffset(System.currentTimeMillis()) / 1000 / 60;
    int j = (int)(arrayOfDouble[0] * 60.0D) + i;
    int k = (int)(arrayOfDouble[1] * 60.0D) + i;
    if (j < 0)
    {
      i = j + 1440;
      if (k >= 0) {
        break label155;
      }
      j = k + 1440;
    }
    for (;;)
    {
      return new int[] { i, j };
      i = j;
      if (j <= 1440) {
        break;
      }
      i = j - 1440;
      break;
      label155:
      j = k;
      if (k > 1440) {
        j = k + 1440;
      }
    }
  }
  
  private static double cosd(double paramDouble)
  {
    return Math.cos(0.017453292519943295D * paramDouble);
  }
  
  private static long days_since_2000_Jan_0(int paramInt1, int paramInt2, int paramInt3)
  {
    return 367L * paramInt1 - ((paramInt2 + 9) / 12 + paramInt1) * 7 / 4 + paramInt2 * 275 / 9 + paramInt3 - 730530L;
  }
  
  private static double rev180(double paramDouble)
  {
    return paramDouble - 360.0D * Math.floor(0.002777777777777778D * paramDouble + 0.5D);
  }
  
  private static double revolution(double paramDouble)
  {
    return paramDouble - 360.0D * Math.floor(0.002777777777777778D * paramDouble);
  }
  
  private static double sind(double paramDouble)
  {
    return Math.sin(0.017453292519943295D * paramDouble);
  }
  
  private static int sunRiseSetForYear(int paramInt1, int paramInt2, int paramInt3, double paramDouble1, double paramDouble2, double[] paramArrayOfDouble)
  {
    return sunRiseSetHelperForYear(paramInt1, paramInt2, paramInt3, paramDouble1, paramDouble2, -0.5833333333333334D, 1, paramArrayOfDouble);
  }
  
  private static int sunRiseSetHelperForYear(int paramInt1, int paramInt2, int paramInt3, double paramDouble1, double paramDouble2, double paramDouble3, int paramInt4, double[] paramArrayOfDouble)
  {
    double[] arrayOfDouble1 = new double[1];
    double[] arrayOfDouble2 = new double[1];
    double[] arrayOfDouble3 = new double[1];
    int i = 0;
    double d1 = days_since_2000_Jan_0(paramInt1, paramInt2, paramInt3) + 0.5D - paramDouble1 / 360.0D;
    paramDouble1 = revolution(GMST0(d1) + 180.0D + paramDouble1);
    sun_RA_decAtDay(d1, arrayOfDouble1, arrayOfDouble2, arrayOfDouble3);
    d1 = 12.0D - rev180(paramDouble1 - arrayOfDouble1[0]) / 15.0D;
    double d2 = 0.2666D / arrayOfDouble3[0];
    paramDouble1 = paramDouble3;
    if (paramInt4 != 0) {
      paramDouble1 = paramDouble3 - d2;
    }
    paramDouble1 = (sind(paramDouble1) - sind(paramDouble2) * sind(arrayOfDouble2[0])) / (cosd(paramDouble2) * cosd(arrayOfDouble2[0]));
    if (paramDouble1 >= 1.0D)
    {
      paramInt1 = -1;
      paramDouble1 = 0.0D;
    }
    for (;;)
    {
      paramArrayOfDouble[0] = (d1 - paramDouble1);
      paramArrayOfDouble[1] = (d1 + paramDouble1);
      return paramInt1;
      if (paramDouble1 <= -1.0D)
      {
        paramInt1 = 1;
        paramDouble1 = 12.0D;
      }
      else
      {
        paramDouble1 = acosd(paramDouble1) / 15.0D;
        paramInt1 = i;
      }
    }
  }
  
  private static void sun_RA_decAtDay(double paramDouble, double[] paramArrayOfDouble1, double[] paramArrayOfDouble2, double[] paramArrayOfDouble3)
  {
    double[] arrayOfDouble = new double[1];
    sunposAtDay(paramDouble, arrayOfDouble, paramArrayOfDouble3);
    double d1 = paramArrayOfDouble3[0] * cosd(arrayOfDouble[0]);
    double d2 = paramArrayOfDouble3[0] * sind(arrayOfDouble[0]);
    double d3 = 23.4393D - 3.563E-7D * paramDouble;
    paramDouble = d2 * cosd(d3);
    d3 = sind(d3);
    paramArrayOfDouble1[0] = atan2d(paramDouble, d1);
    paramArrayOfDouble2[0] = atan2d(d2 * d3, Math.sqrt(d1 * d1 + paramDouble * paramDouble));
  }
  
  private static void sunposAtDay(double paramDouble, double[] paramArrayOfDouble1, double[] paramArrayOfDouble2)
  {
    double d1 = revolution(356.047D + 0.9856002585D * paramDouble);
    double d2 = 0.016709D - 1.151E-9D * paramDouble;
    double d3 = 57.29577951308232D * d2 * sind(d1) * (1.0D + cosd(d1) * d2) + d1;
    d1 = cosd(d3) - d2;
    d2 = Math.sqrt(1.0D - d2 * d2) * sind(d3);
    paramArrayOfDouble2[0] = Math.sqrt(d1 * d1 + d2 * d2);
    paramArrayOfDouble1[0] = (atan2d(d2, d1) + (282.9404D + 4.70935E-5D * paramDouble));
    if (paramArrayOfDouble1[0] >= 360.0D) {
      paramArrayOfDouble1[0] -= 360.0D;
    }
  }
  
  private static double tand(double paramDouble)
  {
    return Math.tan(0.017453292519943295D * paramDouble);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/time/SunDate.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */