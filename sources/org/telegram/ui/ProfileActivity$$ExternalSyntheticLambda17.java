package org.telegram.ui;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import org.telegram.ui.ActionBar.ActionBar;

public final /* synthetic */ class ProfileActivity$$ExternalSyntheticLambda17 implements View.OnLongClickListener {
    public final /* synthetic */ ProfileActivity f$0;
    public final /* synthetic */ Context f$1;
    public final /* synthetic */ ImageView f$2;
    public final /* synthetic */ ActionBar f$3;

    public /* synthetic */ ProfileActivity$$ExternalSyntheticLambda17(ProfileActivity profileActivity, Context context, ImageView imageView, ActionBar actionBar) {
        this.f$0 = profileActivity;
        this.f$1 = context;
        this.f$2 = imageView;
        this.f$3 = actionBar;
    }

    public final boolean onLongClick(View view) {
        return this.f$0.lambda$createActionBar$2(this.f$1, this.f$2, this.f$3, view);
    }
}
