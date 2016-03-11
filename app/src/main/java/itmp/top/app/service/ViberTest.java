package itmp.top.app.service;

import android.app.Service;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import itmp.top.app.R;

public class ViberTest extends AppCompatActivity {
    Vibrator vibrator;
    private RelativeLayout content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viber_test);
        content = (RelativeLayout) findViewById(R.id.content);
        vibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//getWindow().getDecorView().getRootView()
        Snackbar.make(content, "Viber ON", Snackbar.LENGTH_SHORT).setAction("Undone", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrator.cancel();
            }
        }).show();
        vibrator.vibrate(2000);

        return super.onTouchEvent(event);
    }
}
