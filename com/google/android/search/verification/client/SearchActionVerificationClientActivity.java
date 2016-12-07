package com.google.android.search.verification.client;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public abstract class SearchActionVerificationClientActivity extends Activity {
    public abstract Class<? extends SearchActionVerificationClientService> getServiceClass();

    protected final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, getServiceClass());
        intent.putExtra(SearchActionVerificationClientService.EXTRA_INTENT, getIntent());
        startService(intent);
        finish();
    }
}
