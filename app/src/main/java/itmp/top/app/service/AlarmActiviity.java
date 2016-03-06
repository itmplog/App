package itmp.top.app.service;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import itmp.top.app.R;

public class AlarmActiviity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_activiity);

        final MediaPlayer mp = MediaPlayer.create(AlarmActiviity.this, R.raw.audio);
        mp.setLooping(true);
        mp.start();

        new AlertDialog.Builder(AlarmActiviity.this).setTitle("闹钟")
                .setMessage("Time Up!!")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mp.stop();
                        mp.release();
                        AlarmActiviity.this.finish();
                    }
                }).show();
    }
}
