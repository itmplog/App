package itmp.top.app.contentprovider;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore.Images.Media;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import itmp.top.app.R;


public class MediaProviderTest extends Activity {
    Button add;
    Button view;
    ListView show;
    ArrayList<String> names = new ArrayList<>();
    ArrayList<String> descs = new ArrayList<>();
    ArrayList<String> fileNames = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_provider_test);
        add = (Button) findViewById(R.id.add);
        view = (Button) findViewById(R.id.view);
        show = (ListView) findViewById(R.id.show);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    0x12
            );
        }
        // 为view按钮的单击事件绑定监听器
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 清空names、descs、fileNames集合里原有的数据
                names.clear();
                descs.clear();
                fileNames.clear();
                // 通过ContentResolver查询所有图片信息
                Cursor cursor = getContentResolver().query(
                        Media.EXTERNAL_CONTENT_URI, null, null, null, null);
                while (cursor.moveToNext()) {
                    // 获取图片的显示名
                    String name = cursor.getString(cursor
                            .getColumnIndex(Media.DISPLAY_NAME));
                    // 获取图片的详细描述
                    String desc = cursor.getString(cursor
                            .getColumnIndex(Media.DESCRIPTION));
                    // 获取图片的保存位置的数据
                    byte[] data = cursor.getBlob(cursor
                            .getColumnIndex(Media.DATA));
                    // 将图片名添加到names集合中
                    names.add(name);
                    // 将图片描述添加到descs集合中
                    descs.add(desc);
                    // 将图片保存路径添加到fileNames集合中
                    fileNames.add(new String(data, 0, data.length - 1));
                }
                // 创建一个List集合，List集合的元素是Map
                List<Map<String, Object>> listItems = new ArrayList<>();
                // 将names、descs两个集合对象的数据转换到Map集合中
                for (int i = 0; i < names.size(); i++) {
                    Map<String, Object> listItem = new HashMap<>();
                    listItem.put("name", names.get(i));
                    listItem.put("desc", descs.get(i));
                    Log.v("lists", listItem.toString());
                    listItems.add(listItem);
                }
                // 创建一个SimpleAdapter
                SimpleAdapter simpleAdapter = new SimpleAdapter(
                        MediaProviderTest.this, listItems,
                        R.layout.line, new String[]{"name", "desc"}
                        , new int[]{R.id.name, R.id.desc});
                // 为show ListView组件设置Adapter
                show.setAdapter(simpleAdapter);
            }
        });
        // 为show ListView的列表项单击事件添加监听器
        show.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent
                    , View source, int position, long id) {
                // 加载view.xml界面布局代表的视图
                View viewDialog = getLayoutInflater().inflate(
                        R.layout.view, null);
                // 获取viewDialog中ID为image的组件
                ImageView image = (ImageView) viewDialog
                        .findViewById(R.id.image);
                // 设置image显示指定图片
                image.setImageBitmap(BitmapFactory.decodeFile(
                        fileNames.get(position)));
                // 使用对话框显示用户单击的图片
                new AlertDialog.Builder(MediaProviderTest.this)
                        .setView(viewDialog).setPositiveButton("确定", null)
                        .show();
            }
        });

        // 为add按钮的单击事件绑定监听器
        add.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 创建ContentValues对象，准备插入数据
                ContentValues values = new ContentValues();
                values.put(Media.DISPLAY_NAME, "jinta");
                values.put(Media.DESCRIPTION, "金塔");
                values.put(Media.MIME_TYPE, "image/jpeg");
                // 插入数据，返回所插入数据对应的Uri
                Uri uri = getContentResolver().insert(
                        Media.EXTERNAL_CONTENT_URI, values);
                // 加载应用程序下的jinta图片
                Bitmap bitmap = BitmapFactory.decodeResource(
                        MediaProviderTest.this.getResources(),
                        R.mipmap.ic_launcher);
                System.out.println("======");
                OutputStream os = null;
                try {
                    // 获取刚插入的数据的Uri对应的输出流
                    os = getContentResolver().openOutputStream(uri); // ①
                    // 将bitmap图片保存到Uri对应的数据节点中
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0x12 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Snackbar.make(show, "Permission Granted.", Snackbar.LENGTH_SHORT).show();
            add.setClickable(true);
            view.setClickable(true);
        } else {
            Snackbar.make(show, "Permission Denied.", Snackbar.LENGTH_SHORT).setAction("Try Again", new OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActivityCompat.requestPermissions(MediaProviderTest.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            0x12
                    );
                }
            }).show();
            add.setClickable(false);
            view.setClickable(false);
        }
    }

}