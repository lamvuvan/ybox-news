package io.github.lamvv.yboxnews.util;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by lamvu on 4/15/2017.
 */

public class FeedbackUtils {

    private static final String MAIN_EMAIL = "3amsoft.studio@gmail.com";
    private static final String CC_EMAIL = "lamvv9x@gmail.com";

    public static void sendFeedback(Context context, String subject, String content){
        Intent intentSendMail = new Intent(Intent.ACTION_SEND);
        intentSendMail.setType("text/plain");
        intentSendMail.putExtra(Intent.EXTRA_EMAIL, new String[]{MAIN_EMAIL});
        intentSendMail.putExtra(Intent.EXTRA_CC, new String[]{CC_EMAIL});
        intentSendMail.putExtra(Intent.EXTRA_SUBJECT, subject);
        intentSendMail.putExtra(Intent.EXTRA_TEXT, "");
        try {
            context.startActivity(intentSendMail);
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
        }
    }
}
