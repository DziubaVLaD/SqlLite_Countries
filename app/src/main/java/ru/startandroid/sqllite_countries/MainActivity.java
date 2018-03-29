package ru.startandroid.sqllite_countries;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnSort;
    DatabaseHelper sqlHelper;
    SQLiteDatabase db;
    Cursor userCursor;
    SimpleCursorAdapter userAdapter;
    ListView userList;
    EditText userFilter;
    RadioGroup rgSort;
    private String orderBy;
    String[] array_countries;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userList = (ListView) findViewById(R.id.userList);
        userFilter = (EditText) findViewById(R.id.userFilter);
        btnSort = (Button) findViewById(R.id.btnSort);
        btnSort.setOnClickListener(this);
        rgSort = (RadioGroup) findViewById(R.id.rgSort);
        String orderBy = null;
        sqlHelper = new DatabaseHelper(getApplicationContext());
        // создаем базу данных
        sqlHelper.create_db();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSort:
                // сортировка по
                switch (rgSort.getCheckedRadioButtonId()) {

                    // население
                    case R.id.rPeople:
                        orderBy = "people";
                        break;
                    // регион
                    case R.id.rRegion:
                        orderBy = "region";
                        break;
                }
                userCursor = db.query("countries", null, null, null,
                        null, null, orderBy);
                break;
        }
        if (userCursor != null) {
            if (userCursor.moveToFirst()) {
                String str;

                int i = 0;

                array_countries = new String[6];
                do {
                    str = "";
                    for (String cn : userCursor.getColumnNames()) {
                        str = str.concat(userCursor.getString(userCursor.getColumnIndex(cn)) + " ");
                    }

                    array_countries[i] = str;
                    i++;
                     adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                             array_countries);

                    userList.setAdapter(adapter);

                } while (userCursor.moveToNext());
            }
            userCursor.close();
        } else {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Cursor is null", Toast.LENGTH_SHORT);
            toast.show();
        }

        userFilter.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // Когда, юзер изменяет текст он работает
                MainActivity.this.adapter.getFilter().filter(cs);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });
        sqlHelper.close();
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            db = sqlHelper.open();
            userList.setAdapter(userAdapter);
        } catch (SQLException ex){}
        onClick(btnSort);
    }
}


