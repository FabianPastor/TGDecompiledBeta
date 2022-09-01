package org.telegram.ui;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import org.telegram.ui.LoginActivity;

public final /* synthetic */ class LoginActivity$LoginActivityEmailCodeView$$ExternalSyntheticLambda3 implements OnCompleteListener {
    public final /* synthetic */ LoginActivity.LoginActivityEmailCodeView f$0;
    public final /* synthetic */ GoogleSignInClient f$1;

    public /* synthetic */ LoginActivity$LoginActivityEmailCodeView$$ExternalSyntheticLambda3(LoginActivity.LoginActivityEmailCodeView loginActivityEmailCodeView, GoogleSignInClient googleSignInClient) {
        this.f$0 = loginActivityEmailCodeView;
        this.f$1 = googleSignInClient;
    }

    public final void onComplete(Task task) {
        this.f$0.lambda$new$2(this.f$1, task);
    }
}
