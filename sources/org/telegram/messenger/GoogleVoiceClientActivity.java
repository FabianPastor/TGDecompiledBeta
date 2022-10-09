package org.telegram.messenger;

import com.google.android.search.verification.client.SearchActionVerificationClientActivity;
import com.google.android.search.verification.client.SearchActionVerificationClientService;
/* loaded from: classes.dex */
public class GoogleVoiceClientActivity extends SearchActionVerificationClientActivity {
    @Override // com.google.android.search.verification.client.SearchActionVerificationClientActivity
    public Class<? extends SearchActionVerificationClientService> getServiceClass() {
        return GoogleVoiceClientService.class;
    }
}
