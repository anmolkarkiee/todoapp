package com.example.todoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.todoapp.viewmodel.TodoViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton fabAddNew;

    Fragment mfragment;
    FragmentManager mfragmentManager;
    TextView addTodo;
    AlertDialog.Builder mAlterDialog;
    TodoViewModel mTodoViewModel;

    Button completebtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mfragment=new TodoListFragment();
        mfragmentManager=getSupportFragmentManager();
        mfragmentManager.beginTransaction()
                .add(R.id.list_container,mfragment)
                .commit();
//        fabAddNew = findViewById(R.id.fab_add_new_todo);
        addTodo = findViewById(R.id.addItem);
        completebtn = findViewById(R.id.completeAll);

        completebtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                mTodoViewModel.deleteAll();
            }
        });


        //fab icon for referring to edit todo page while clicking
        addTodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,EditTodoActivity.class);
                startActivity(intent);
            }
        });
 
        mTodoViewModel= ViewModelProviders.of(this).get(TodoViewModel.class);
    }
    //for menu options
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    //code for extra options
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId())
        {

            //for exiting the app
            case R.id.menu_logout:
                mAlterDialog = new AlertDialog.Builder(this);
                mAlterDialog.setMessage("Are you sure want to exit?")
                        .setCancelable(false)
                        .setTitle(getString(R.string.app_name));

                mAlterDialog.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences preferences = getApplicationContext().getSharedPreferences("todo_pref", 0);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.apply();
                        finish();
                    }
                });
                mAlterDialog.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                mAlterDialog.show();
                break;


        }
        return super.onOptionsItemSelected(item);
    }
}
