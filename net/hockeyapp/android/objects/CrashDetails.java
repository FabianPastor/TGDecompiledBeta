package net.hockeyapp.android.objects;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import net.hockeyapp.android.Constants;
import net.hockeyapp.android.utils.HockeyLog;

public class CrashDetails {
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
    private Date appCrashDate;
    private String appPackage;
    private Date appStartDate;
    private String appVersionCode;
    private String appVersionName;
    private final String crashIdentifier;
    private String deviceManufacturer;
    private String deviceModel;
    private Boolean isXamarinException;
    private String osBuild;
    private String osVersion;
    private String reporterKey;
    private String threadName;
    private String throwableStackTrace;

    public CrashDetails(String crashIdentifier) {
        this.crashIdentifier = crashIdentifier;
        this.isXamarinException = Boolean.valueOf(false);
        this.throwableStackTrace = TtmlNode.ANONYMOUS_REGION_ID;
    }

    public CrashDetails(String crashIdentifier, Throwable throwable) {
        this(crashIdentifier);
        this.isXamarinException = Boolean.valueOf(false);
        Writer stackTraceResult = new StringWriter();
        throwable.printStackTrace(new PrintWriter(stackTraceResult));
        this.throwableStackTrace = stackTraceResult.toString();
    }

    public void writeCrashReport() {
        writeCrashReport(Constants.FILES_PATH + "/" + this.crashIdentifier + ".stacktrace");
    }

    public void writeCrashReport(String path) {
        Throwable e;
        Throwable th;
        HockeyLog.debug("Writing unhandled exception to: " + path);
        BufferedWriter writer = null;
        try {
            BufferedWriter writer2 = new BufferedWriter(new FileWriter(path));
            try {
                writeHeader(writer2, "Package", this.appPackage);
                writeHeader(writer2, "Version Code", this.appVersionCode);
                writeHeader(writer2, "Version Name", this.appVersionName);
                writeHeader(writer2, "Android", this.osVersion);
                writeHeader(writer2, "Android Build", this.osBuild);
                writeHeader(writer2, "Manufacturer", this.deviceManufacturer);
                writeHeader(writer2, "Model", this.deviceModel);
                writeHeader(writer2, "Thread", this.threadName);
                writeHeader(writer2, "CrashReporter Key", this.reporterKey);
                writeHeader(writer2, "Start Date", DATE_FORMAT.format(this.appStartDate));
                writeHeader(writer2, "Date", DATE_FORMAT.format(this.appCrashDate));
                if (this.isXamarinException.booleanValue()) {
                    writeHeader(writer2, "Format", "Xamarin");
                }
                writer2.write("\n");
                writer2.write(this.throwableStackTrace);
                writer2.flush();
                if (writer2 != null) {
                    try {
                        writer2.close();
                    } catch (Throwable e1) {
                        HockeyLog.error("Error saving crash report!", e1);
                        writer = writer2;
                        return;
                    }
                }
                writer = writer2;
            } catch (IOException e2) {
                e = e2;
                writer = writer2;
                try {
                    HockeyLog.error("Error saving crash report!", e);
                    if (writer != null) {
                        try {
                            writer.close();
                        } catch (Throwable e12) {
                            HockeyLog.error("Error saving crash report!", e12);
                        }
                    }
                } catch (Throwable th2) {
                    th = th2;
                    if (writer != null) {
                        try {
                            writer.close();
                        } catch (Throwable e122) {
                            HockeyLog.error("Error saving crash report!", e122);
                        }
                    }
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                writer = writer2;
                if (writer != null) {
                    writer.close();
                }
                throw th;
            }
        } catch (IOException e3) {
            e = e3;
            HockeyLog.error("Error saving crash report!", e);
            if (writer != null) {
                writer.close();
            }
        }
    }

    private void writeHeader(Writer writer, String name, String value) throws IOException {
        writer.write(name + ": " + value + "\n");
    }

    public void setReporterKey(String reporterKey) {
        this.reporterKey = reporterKey;
    }

    public void setAppStartDate(Date appStartDate) {
        this.appStartDate = appStartDate;
    }

    public void setAppCrashDate(Date appCrashDate) {
        this.appCrashDate = appCrashDate;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public void setOsBuild(String osBuild) {
        this.osBuild = osBuild;
    }

    public void setDeviceManufacturer(String deviceManufacturer) {
        this.deviceManufacturer = deviceManufacturer;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    public void setAppPackage(String appPackage) {
        this.appPackage = appPackage;
    }

    public void setAppVersionName(String appVersionName) {
        this.appVersionName = appVersionName;
    }

    public void setAppVersionCode(String appVersionCode) {
        this.appVersionCode = appVersionCode;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }
}
