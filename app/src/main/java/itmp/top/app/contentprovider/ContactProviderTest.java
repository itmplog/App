package itmp.top.app.contentprovider;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.ArrayList;

import itmp.top.app.R;

public class ContactProviderTest extends AppCompatActivity {

    private final int REQUIREPERMISSION_RTN = 0x11;
    private Button query;
    private Button add;
    private EditText name;
    private EditText phone;
    private EditText email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_provider_test);

        query = (Button) findViewById(R.id.query);
        add = (Button) findViewById(R.id.add);
        name = (EditText) findViewById(R.id.name);
        phone = (EditText) findViewById(R.id.phone);
        email = (EditText) findViewById(R.id.email);

        query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(ContactProviderTest.this,
                        Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ContactProviderTest.this,
                            new String[]{Manifest.permission.READ_CONTACTS},
                            REQUIREPERMISSION_RTN);
                } else {
                    readContacts();
                }
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(ContactProviderTest.this,
                        Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ContactProviderTest.this,
                            new String[]{Manifest.permission.READ_CONTACTS},
                            REQUIREPERMISSION_RTN);
                } else {
                    writeContacts();
                }
            }
        });
    }

    public void readContacts() {
        AsyncReadContacts asyncReadContacts = new AsyncReadContacts();
        asyncReadContacts.execute();
    }

    public void writeContacts() {
        String cName = name.getText().toString();
        String cPhone = phone.getText().toString();
        String cEmail = email.getText().toString();

        if (!TextUtils.isEmpty(cName) && !TextUtils.isEmpty(cPhone) && !TextUtils.isEmpty(cEmail)) {
            Log.v("mem", cName + cPhone + cEmail);
            ContentValues contentValues = new ContentValues();
            Uri rawContactUri = getContentResolver().insert(ContactsContract.RawContacts.CONTENT_URI, contentValues);
            long rawContactId = ContentUris.parseId(rawContactUri);
            contentValues.clear();

            contentValues.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
            contentValues.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
            contentValues.put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, cName);
            getContentResolver().insert(ContactsContract.Data.CONTENT_URI, contentValues);
            contentValues.clear();

            contentValues.put(ContactsContract.CommonDataKinds.StructuredName.RAW_CONTACT_ID, rawContactId);
            contentValues.put(ContactsContract.CommonDataKinds.StructuredName.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
            contentValues.put(ContactsContract.CommonDataKinds.Phone.NUMBER, cPhone);
            contentValues.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
            getContentResolver().insert(ContactsContract.Data.CONTENT_URI, contentValues);
            contentValues.clear();

            contentValues.put(ContactsContract.CommonDataKinds.StructuredName.RAW_CONTACT_ID, rawContactId);
            contentValues.put(ContactsContract.CommonDataKinds.StructuredName.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE);
            contentValues.put(ContactsContract.CommonDataKinds.Email.DATA, cEmail);
            contentValues.put(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK);
            getContentResolver().insert(ContactsContract.Data.CONTENT_URI, contentValues);
            contentValues.clear();

            Snackbar.make(query, "联系人添加成功", Snackbar.LENGTH_SHORT).setAction("", null).show();

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUIREPERMISSION_RTN:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    readContacts();
                } else {
                    Snackbar.make(query, "Need Permission.", Snackbar.LENGTH_LONG).setAction("Request Again", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ActivityCompat.requestPermissions(ContactProviderTest.this,
                                    new String[]{Manifest.permission.READ_CONTACTS},
                                    REQUIREPERMISSION_RTN);
                        }
                    }).show();
                }
                break;
            default:
                break;
        }
    }

    public class AsyncReadContacts extends AsyncTask<Void, Void, Void> {
        final ArrayList<String> names = new ArrayList<String>();
        final ArrayList<ArrayList<String>> details = new ArrayList<ArrayList<String>>();
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {

            progressDialog = new ProgressDialog(ContactProviderTest.this);
            progressDialog.setTitle("正在读取联系人");
            progressDialog.setMessage("联系人正在读取中, 请等待....");
            progressDialog.setCancelable(false);
            progressDialog.setMax(100);
            // Log.d("pre", length + "");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // 设置进度条风格
            progressDialog.setIndeterminate(true); // set the indeterminate for true  cause it will be downloaded so soon
            progressDialog.show();

        }

        @Override
        protected Void doInBackground(Void... params) {

            Cursor cursor = getContentResolver().query(
                    ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
            while (cursor.moveToNext())

            {
                String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                names.add(name);

                Cursor phones = getContentResolver().query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
                ArrayList<String> detail = new ArrayList<String>();
                while (phones.moveToNext()) {
                    String phoneNum = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    detail.add("电话号码： " + phoneNum);
                }
                phones.close();
                Cursor emails = getContentResolver().query(
                        ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + contactId,
                        null, null
                );
                while (emails.moveToNext()) {
                    String emailAddr = emails.getString(emails.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                    detail.add("邮件地址: " + emailAddr);
                }
                emails.close();
                details.add(detail);
            }
            cursor.close();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            progressDialog.dismiss();
            View resultDialog = getLayoutInflater().inflate(
                    R.layout.contact_provider_query_result, null
            );

            ExpandableListView expandableListView = (ExpandableListView) resultDialog.findViewById(R.id.resultDialog);

            ExpandableListAdapter expandableListAdapter = new BaseExpandableListAdapter() {
                @Override
                public int getChildrenCount(int i) {
                    return details.get(i).size();
                }

                @Override
                public Object getChild(int i, int i1) {
                    return details.get(i).get(i1);
                }

                @Override
                public long getChildId(int i, int i1) {
                    return i1;
                }

                public TextView getTextView() {
                    AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
                    );
                    TextView textView = new TextView(ContactProviderTest.this);
                    textView.setLayoutParams(lp);
                    textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
                    textView.setPadding(80, 40, 0
                            , 0);
                    textView.setTextSize(20);
                    return textView;
                }

                @Override
                public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
                    TextView textView = getTextView();
                    textView.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    textView.setText(getChild(i, i1).toString());
                    return textView;
                }

                @Override
                public Object getGroup(int i) {
                    return names.get(i);
                }

                @Override
                public int getGroupCount() {
                    return names.size();
                }

                @Override
                public long getGroupId(int i) {
                    return i;
                }

                @Override
                public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
                    TextView textView = getTextView();
                    textView.setText(getGroup(i).toString());
                    return textView;
                }

                @Override
                public boolean isChildSelectable(int i, int i1) {
                    return true;
                }

                @Override
                public boolean hasStableIds() {
                    return true;
                }
            };

            expandableListView.setAdapter(expandableListAdapter);

            new AlertDialog.Builder(ContactProviderTest.this)
                    .setView(resultDialog)
                    .setPositiveButton("确定", null)
                    .show();
        }

        @Override
        protected void onProgressUpdate(Void... values) {

        }
    }
}
