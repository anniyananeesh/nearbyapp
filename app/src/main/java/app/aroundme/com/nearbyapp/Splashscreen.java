package app.aroundme.com.nearbyapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class Splashscreen extends Activity {

    private static int SPLASH_TIME_OUT = 3000;
    Animation bounceIn;
    ImageView ivAppSplash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splashscreen);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                Intent i = new Intent(Splashscreen.this, MainActivity.class);
                startActivity(i);

                // close this activity
                finish();
            }

        }, SPLASH_TIME_OUT);

        bounceIn  = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
        ivAppSplash = (ImageView) findViewById(R.id.iv_app_splash);

        ivAppSplash.startAnimation(bounceIn);
    }

}
