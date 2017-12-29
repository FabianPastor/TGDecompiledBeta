package com.google.firebase.iid;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcelable;
import android.os.RemoteException;
import android.support.v4.util.SimpleArrayMap;
import android.util.Log;
import com.google.android.gms.iid.MessengerCompat;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class zzv {
    private static PendingIntent zzicn;
    private static int zzift = 0;
    private final Context zzair;
    private Messenger zzicr;
    private Messenger zzifw;
    private MessengerCompat zzifx;
    private final zzu zznys;
    private final SimpleArrayMap<String, TaskCompletionSource<Bundle>> zznzn = new SimpleArrayMap();

    public zzv(Context context, zzu com_google_firebase_iid_zzu) {
        this.zzair = context;
        this.zznys = com_google_firebase_iid_zzu;
        this.zzicr = new Messenger(new zzw(this, Looper.getMainLooper()));
    }

    private final Bundle zzae(Bundle bundle) throws IOException {
        Bundle zzaf = zzaf(bundle);
        if (zzaf == null || !zzaf.containsKey("google.messenger")) {
            return zzaf;
        }
        zzaf = zzaf(bundle);
        return (zzaf == null || !zzaf.containsKey("google.messenger")) ? zzaf : null;
    }

    private final Bundle zzaf(Bundle bundle) throws IOException {
        String zzavi = zzavi();
        TaskCompletionSource taskCompletionSource = new TaskCompletionSource();
        synchronized (this.zznzn) {
            this.zznzn.put(zzavi, taskCompletionSource);
        }
        if (this.zznys.zzcjf() == 0) {
            throw new IOException("MISSING_INSTANCEID_SERVICE");
        }
        Intent intent = new Intent();
        intent.setPackage("com.google.android.gms");
        if (this.zznys.zzcjf() == 2) {
            intent.setAction("com.google.iid.TOKEN_REQUEST");
        } else {
            intent.setAction("com.google.android.c2dm.intent.REGISTER");
        }
        intent.putExtras(bundle);
        zzd(this.zzair, intent);
        intent.putExtra("kid", new StringBuilder(String.valueOf(zzavi).length() + 5).append("|ID|").append(zzavi).append("|").toString());
        if (Log.isLoggable("FirebaseInstanceId", 3)) {
            String valueOf = String.valueOf(intent.getExtras());
            Log.d("FirebaseInstanceId", new StringBuilder(String.valueOf(valueOf).length() + 8).append("Sending ").append(valueOf).toString());
        }
        intent.putExtra("google.messenger", this.zzicr);
        if (!(this.zzifw == null && this.zzifx == null)) {
            Message obtain = Message.obtain();
            obtain.obj = intent;
            try {
                if (this.zzifw != null) {
                    this.zzifw.send(obtain);
                } else {
                    this.zzifx.send(obtain);
                }
            } catch (RemoteException e) {
                if (Log.isLoggable("FirebaseInstanceId", 3)) {
                    Log.d("FirebaseInstanceId", "Messenger failed, fallback to startService");
                }
            }
            Bundle bundle2 = (Bundle) Tasks.await(taskCompletionSource.getTask(), 30000, TimeUnit.MILLISECONDS);
            synchronized (this.zznzn) {
                this.zznzn.remove(zzavi);
            }
            return bundle2;
        }
        if (this.zznys.zzcjf() == 2) {
            this.zzair.sendBroadcast(intent);
        } else {
            this.zzair.startService(intent);
        }
        try {
            Bundle bundle22 = (Bundle) Tasks.await(taskCompletionSource.getTask(), 30000, TimeUnit.MILLISECONDS);
            synchronized (this.zznzn) {
                this.zznzn.remove(zzavi);
            }
            return bundle22;
        } catch (InterruptedException e2) {
            Log.w("FirebaseInstanceId", "No response");
            throw new IOException("TIMEOUT");
        } catch (TimeoutException e3) {
            Log.w("FirebaseInstanceId", "No response");
            throw new IOException("TIMEOUT");
        } catch (ExecutionException e4) {
            r0 = e4.getCause();
            Throwable cause;
            if (cause instanceof IOException) {
                throw ((IOException) cause);
            }
            throw new IOException(cause);
        } catch (Throwable th) {
            synchronized (this.zznzn) {
                this.zznzn.remove(zzavi);
            }
        }
    }

    private static synchronized String zzavi() {
        String num;
        synchronized (zzv.class) {
            int i = zzift;
            zzift = i + 1;
            num = Integer.toString(i);
        }
        return num;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private final void zzbl(String str, String str2) {
        synchronized (this.zznzn) {
            if (str == null) {
                for (int i = 0; i < this.zznzn.size(); i++) {
                    ((TaskCompletionSource) this.zznzn.valueAt(i)).setException(new IOException(str2));
                }
                this.zznzn.clear();
            } else {
                TaskCompletionSource taskCompletionSource = (TaskCompletionSource) this.zznzn.remove(str);
                if (taskCompletionSource == null) {
                    String str3 = "FirebaseInstanceId";
                    String str4 = "Missing callback for ";
                    String valueOf = String.valueOf(str);
                    Log.w(str3, valueOf.length() != 0 ? str4.concat(valueOf) : new String(str4));
                    return;
                }
                taskCompletionSource.setException(new IOException(str2));
            }
        }
    }

    private static synchronized void zzd(Context context, Intent intent) {
        synchronized (zzv.class) {
            if (zzicn == null) {
                Intent intent2 = new Intent();
                intent2.setPackage("com.google.example.invalidpackage");
                zzicn = PendingIntent.getBroadcast(context, 0, intent2, 0);
            }
            intent.putExtra("app", zzicn);
        }
    }

    private final void zze(Message message) {
        if (message == null || !(message.obj instanceof Intent)) {
            Log.w("FirebaseInstanceId", "Dropping invalid message");
            return;
        }
        Intent intent = (Intent) message.obj;
        intent.setExtrasClassLoader(MessengerCompat.class.getClassLoader());
        if (intent.hasExtra("google.messenger")) {
            Parcelable parcelableExtra = intent.getParcelableExtra("google.messenger");
            if (parcelableExtra instanceof MessengerCompat) {
                this.zzifx = (MessengerCompat) parcelableExtra;
            }
            if (parcelableExtra instanceof Messenger) {
                this.zzifw = (Messenger) parcelableExtra;
            }
        }
        intent = (Intent) message.obj;
        String action = intent.getAction();
        String stringExtra;
        String valueOf;
        String str;
        if ("com.google.android.c2dm.intent.REGISTRATION".equals(action)) {
            CharSequence stringExtra2 = intent.getStringExtra("registration_id");
            if (stringExtra2 == null) {
                stringExtra2 = intent.getStringExtra("unregistered");
            }
            String str2;
            if (stringExtra2 == null) {
                stringExtra = intent.getStringExtra("error");
                if (stringExtra == null) {
                    valueOf = String.valueOf(intent.getExtras());
                    Log.w("FirebaseInstanceId", new StringBuilder(String.valueOf(valueOf).length() + 49).append("Unexpected response, no error or registration id ").append(valueOf).toString());
                    return;
                }
                if (Log.isLoggable("FirebaseInstanceId", 3)) {
                    str = "FirebaseInstanceId";
                    str2 = "Received InstanceID error ";
                    action = String.valueOf(stringExtra);
                    Log.d(str, action.length() != 0 ? str2.concat(action) : new String(str2));
                }
                if (stringExtra.startsWith("|")) {
                    String[] split = stringExtra.split("\\|");
                    if (!"ID".equals(split[1])) {
                        String str3 = "FirebaseInstanceId";
                        String str4 = "Unexpected structured response ";
                        action = String.valueOf(stringExtra);
                        Log.w(str3, action.length() != 0 ? str4.concat(action) : new String(str4));
                    }
                    if (split.length > 2) {
                        action = split[2];
                        str = split[3];
                        if (str.startsWith(":")) {
                            str = str.substring(1);
                        }
                    } else {
                        str = "UNKNOWN";
                        action = null;
                    }
                    intent.putExtra("error", str);
                } else {
                    action = null;
                    str = stringExtra;
                }
                zzbl(action, str);
                return;
            }
            Matcher matcher = Pattern.compile("\\|ID\\|([^|]+)\\|:?+(.*)").matcher(stringExtra2);
            if (matcher.matches()) {
                action = matcher.group(1);
                str = matcher.group(2);
                Bundle extras = intent.getExtras();
                extras.putString("registration_id", str);
                synchronized (this.zznzn) {
                    TaskCompletionSource taskCompletionSource = (TaskCompletionSource) this.zznzn.remove(action);
                    if (taskCompletionSource == null) {
                        stringExtra = "FirebaseInstanceId";
                        str2 = "Missing callback for ";
                        valueOf = String.valueOf(action);
                        Log.w(stringExtra, valueOf.length() != 0 ? str2.concat(valueOf) : new String(str2));
                        return;
                    }
                    taskCompletionSource.setResult(extras);
                }
            } else if (Log.isLoggable("FirebaseInstanceId", 3)) {
                str = "FirebaseInstanceId";
                stringExtra = "Unexpected response string: ";
                valueOf = String.valueOf(stringExtra2);
                Log.d(str, valueOf.length() != 0 ? stringExtra.concat(valueOf) : new String(stringExtra));
            }
        } else if (Log.isLoggable("FirebaseInstanceId", 3)) {
            str = "FirebaseInstanceId";
            stringExtra = "Unexpected response action: ";
            valueOf = String.valueOf(action);
            Log.d(str, valueOf.length() != 0 ? stringExtra.concat(valueOf) : new String(stringExtra));
        }
    }

    final Bundle zzad(Bundle bundle) throws IOException {
        Exception e;
        if (this.zznys.zzcji() < 12000000) {
            return zzae(bundle);
        }
        try {
            return (Bundle) Tasks.await(zzi.zzev(this.zzair).zzi(1, bundle));
        } catch (InterruptedException e2) {
            e = e2;
        } catch (ExecutionException e3) {
            e = e3;
        }
        if (Log.isLoggable("FirebaseInstanceId", 3)) {
            String valueOf = String.valueOf(e);
            Log.d("FirebaseInstanceId", new StringBuilder(String.valueOf(valueOf).length() + 22).append("Error making request: ").append(valueOf).toString());
        }
        return ((e.getCause() instanceof zzs) && ((zzs) e.getCause()).getErrorCode() == 4) ? zzae(bundle) : null;
    }
}
