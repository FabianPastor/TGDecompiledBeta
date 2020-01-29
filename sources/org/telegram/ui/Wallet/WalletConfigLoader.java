package org.telegram.ui.Wallet;

import android.os.AsyncTask;
import org.telegram.messenger.TonController;

public class WalletConfigLoader extends AsyncTask<Void, Void, String> {
    private String currentUrl;
    private TonController.StringCallback onFinishCallback;

    public static void loadConfig(String str, TonController.StringCallback stringCallback) {
        new WalletConfigLoader(str, stringCallback).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
    }

    public WalletConfigLoader(String str, TonController.StringCallback stringCallback) {
        this.currentUrl = str;
        this.onFinishCallback = stringCallback;
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x0063 A[SYNTHETIC, Splitter:B:29:0x0063] */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x006d A[SYNTHETIC, Splitter:B:34:0x006d] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.String doInBackground(java.lang.Void... r6) {
        /*
            r5 = this;
            r6 = 0
            java.net.URL r0 = new java.net.URL     // Catch:{ all -> 0x005b }
            java.lang.String r1 = r5.currentUrl     // Catch:{ all -> 0x005b }
            r0.<init>(r1)     // Catch:{ all -> 0x005b }
            java.net.URLConnection r0 = r0.openConnection()     // Catch:{ all -> 0x005b }
            java.lang.String r1 = "User-Agent"
            java.lang.String r2 = "Mozilla/5.0 (iPhone; CPU iPhone OS 10_0 like Mac OS X) AppleWebKit/602.1.38 (KHTML, like Gecko) Version/10.0 Mobile/14A5297c Safari/602.1"
            r0.addRequestProperty(r1, r2)     // Catch:{ all -> 0x005b }
            r1 = 1000(0x3e8, float:1.401E-42)
            r0.setConnectTimeout(r1)     // Catch:{ all -> 0x005b }
            r1 = 2000(0x7d0, float:2.803E-42)
            r0.setReadTimeout(r1)     // Catch:{ all -> 0x005b }
            r0.connect()     // Catch:{ all -> 0x005b }
            java.io.InputStream r0 = r0.getInputStream()     // Catch:{ all -> 0x005b }
            java.io.ByteArrayOutputStream r1 = new java.io.ByteArrayOutputStream     // Catch:{ all -> 0x0058 }
            r1.<init>()     // Catch:{ all -> 0x0058 }
            r2 = 32768(0x8000, float:4.5918E-41)
            byte[] r2 = new byte[r2]     // Catch:{ all -> 0x0056 }
        L_0x002e:
            int r3 = r0.read(r2)     // Catch:{ all -> 0x0056 }
            if (r3 <= 0) goto L_0x0039
            r4 = 0
            r1.write(r2, r4, r3)     // Catch:{ all -> 0x0056 }
            goto L_0x002e
        L_0x0039:
            r2 = -1
            java.lang.String r2 = new java.lang.String     // Catch:{ all -> 0x0056 }
            byte[] r3 = r1.toByteArray()     // Catch:{ all -> 0x0056 }
            r2.<init>(r3)     // Catch:{ all -> 0x0056 }
            org.json.JSONObject r3 = new org.json.JSONObject     // Catch:{ all -> 0x0056 }
            r3.<init>(r2)     // Catch:{ all -> 0x0056 }
            if (r0 == 0) goto L_0x0052
            r0.close()     // Catch:{ all -> 0x004e }
            goto L_0x0052
        L_0x004e:
            r6 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r6)
        L_0x0052:
            r1.close()     // Catch:{ Exception -> 0x0055 }
        L_0x0055:
            return r2
        L_0x0056:
            r2 = move-exception
            goto L_0x005e
        L_0x0058:
            r2 = move-exception
            r1 = r6
            goto L_0x005e
        L_0x005b:
            r2 = move-exception
            r0 = r6
            r1 = r0
        L_0x005e:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)     // Catch:{ all -> 0x0071 }
            if (r0 == 0) goto L_0x006b
            r0.close()     // Catch:{ all -> 0x0067 }
            goto L_0x006b
        L_0x0067:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x006b:
            if (r1 == 0) goto L_0x0070
            r1.close()     // Catch:{ Exception -> 0x0070 }
        L_0x0070:
            return r6
        L_0x0071:
            r6 = move-exception
            if (r0 == 0) goto L_0x007c
            r0.close()     // Catch:{ all -> 0x0078 }
            goto L_0x007c
        L_0x0078:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x007c:
            if (r1 == 0) goto L_0x0081
            r1.close()     // Catch:{ Exception -> 0x0081 }
        L_0x0081:
            goto L_0x0083
        L_0x0082:
            throw r6
        L_0x0083:
            goto L_0x0082
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Wallet.WalletConfigLoader.doInBackground(java.lang.Void[]):java.lang.String");
    }

    /* access modifiers changed from: protected */
    public void onPostExecute(String str) {
        TonController.StringCallback stringCallback = this.onFinishCallback;
        if (stringCallback != null) {
            stringCallback.run(str);
        }
    }
}
