package io.github.lamvv.yboxnews.util;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import static io.github.lamvv.yboxnews.common.Constants.CC_EMAIL;
import static io.github.lamvv.yboxnews.common.Constants.MAIN_EMAIL;

/**
 * Created by lamvu on 4/15/2017.
 */

public class FeedbackUtils {

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
