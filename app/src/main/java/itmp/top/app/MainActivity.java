package itmp.top.app;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.listView);
        PackageInfo packageInfo = null;

        final ArrayList<String> arrayList = new ArrayList<>();
        final ArrayList<String> classNames = new ArrayList<>();

        try {
            packageInfo = getPackageManager().getPackageInfo(this.getPackageName(), PackageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        for (ActivityInfo activityInfo : packageInfo.activities) {
            //Log.v("info", activityInfo.toString());
            //mActivities = new ArrayList<ActivityInfo>(Arrays.asList(packageInfo.activities));
            if (activityInfo.parentActivityName != null) {
                classNames.add(activityInfo.name);
                arrayList.add(activityInfo.name.substring(activityInfo.name.lastIndexOf('.') + 1));
            }
            Log.v("ac", activityInfo.name);
        }


        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(adapter);

        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);

        //listView.setAnimation(new MyAnimation(displayMetrics.widthPixels / 2,
        //      displayMetrics.heightPixels / 2, 4000));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClassName(getPackageName(), classNames.get(position));
                startActivity(intent);
            }
        });

        listView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                view.setBackgroundColor(Color.MAGENTA);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}
