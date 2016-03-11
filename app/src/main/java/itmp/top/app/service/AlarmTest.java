package itmp.top.app.service;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

import itmp.top.app.R;

public class AlarmTest extends AppCompatActivity {

    private FloatingActionButton fab;
    private FloatingActionButton fab1;
    AlarmManager alarmManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_test);
        alarmManager = (AlarmManager)getSystemService(Service.ALARM_SERVICE);
        fab = (FloatingActionButton)findViewById(R.id.fab);
        fab1 = (FloatingActionButton)findViewById(R.id.fab1);

        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(AlarmTest.this).setTitle("Last Alarm Time")
                        .setMessage(new Date(alarmManager.getNextAlarmClock().getTriggerTime()) + "")
                        .setPositiveButton(getString(android.R.string.yes), null)
                        .show();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar currentTime = Calendar.getInstance();
                new TimePickerDialog(AlarmTest.this, 0,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                Intent intent = new Intent(AlarmTest.this, AlarmActiviity.class);
                                final PendingIntent pi = PendingIntent.getActivity(AlarmTest.this, 0, intent, 0);

                                view.clearFocus();
                                Calendar calendar = Calendar.getInstance();
                                calendar.setTimeInMillis(System.currentTimeMillis());

                                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                calendar.set(Calendar.MINUTE, minute);
                                Log.v("calendar", calendar.toString());
                                Log.v("currrnt", currentTime.toString());
                                alarmManager.set(AlarmManager.RTC_WAKEUP,
                                        calendar.getTimeInMillis(), pi);
                                Log.v("alrm", new Date(alarmManager.getNextAlarmClock().getTriggerTime()) + "");

                                Snackbar.make(fab, "Alarm Set", Snackbar.LENGTH_SHORT).setAction("Undo" ,new FloatingActionButton.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        alarmManager.cancel(pi);
                                        Toast.makeText(getApplicationContext(), "Alarm Off", Toast.LENGTH_SHORT).show();
                                    }
                                }).show();
                            }
                        }, currentTime.get(Calendar.HOUR_OF_DAY), currentTime.get(Calendar.MINUTE), false).show();
            }
        });

    }
}
