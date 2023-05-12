package com.example.todoapp;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.todoapp.model.Task;
import com.example.todoapp.viewmodel.TodoViewModel;

import java.time.Instant;
import java.time.LocalTime;
import java.util.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import kotlin.jvm.Throws;


public class EditTodoFragment extends Fragment {

    //view elements
    View rootView;
    EditText txtTitle, txtDescription, txtDate;
    RadioGroup rgPriority;
    RadioButton rbHigh, rbMedium, rbLow, rbSelected;
    CheckBox chkIsCompleted;
    Button btnAdd,btnCancel;
    AlertDialog.Builder mAlterDialog;
    DatePickerDialog mDatePicker;

    public TextView priorities;
    public static final int highPriority=1;
    public static final int mediumPriority=2;
    public static final int lowPriority=3;

    private TodoViewModel mTodoViewModel;

    private int todoId;


    @SuppressLint({"ClickableViewAccessibility", "SimpleDateFormat"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_edit_todo, container, false);
        mTodoViewModel = ViewModelProviders.of(this).get(TodoViewModel.class);
        txtTitle = rootView.findViewById(R.id.edit_txt_title);
        txtDescription = rootView.findViewById(R.id.edit_txt_description);
        txtDate = rootView.findViewById(R.id.edit_txt_date);
        rgPriority = rootView.findViewById(R.id.edit_rg_priority);
        rbHigh = rootView.findViewById(R.id.edit_rb_high);
        rbMedium = rootView.findViewById(R.id.edit_rb_medium);
        rbLow = rootView.findViewById(R.id.edit_rb_low);
        chkIsCompleted = rootView.findViewById(R.id.edit_chk_iscomplete);
        btnAdd = rootView.findViewById(R.id.edit_btn_save);
        btnCancel = rootView.findViewById(R.id.edit_btn_cancel);
        txtDate = rootView.findViewById(R.id.edit_txt_date);
        priorities = (TextView) rootView.findViewById(R.id.priority);


        //adding click listener on save button for adding new todo
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    AddTodo();


            }
        });

        //adding click listener n cancel button
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DisplayAlertDialog();
            }
        });
        txtDate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_DOWN)
                {
                    DispalyTodoDate();
                }

                return false;
            }
        });
        //for updating the todo
        todoId=getActivity().getIntent().getIntExtra("TodoId",-1);
        if (todoId!=-1){
            btnAdd.setText(getText(R.string.edit_update));
            Task todo = mTodoViewModel.getTodoById(todoId);
            txtTitle.setText(todo.getTitle());
            txtDescription.setText(todo.getDescription());
            DateFormat formatter;
            formatter=new SimpleDateFormat("yyyy/MM/dd");
            txtDate.setText(formatter.format(todo.getCreatedDate()));

            if(todo.getPriority()==1){
                rbHigh.setChecked(true);
            }else if(todo.getPriority()==0){
                rbMedium.setChecked(true);
            }else{
                rbLow.setChecked(true);
            }
        }
        return rootView;

    }
    //Alert Dailog Box Cancellation
    void DisplayAlertDialog(){
        mAlterDialog = new AlertDialog.Builder(getContext());
        mAlterDialog.setMessage(getString(R.string.edit_cancel_promt))
                .setCancelable(false)
                .setTitle(getString(R.string.app_name));

        mAlterDialog.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getActivity(),MainActivity.class);
                startActivity(intent);
            }
        });
        mAlterDialog.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        mAlterDialog.show();
    }
    //For intent displaying calender
    void DispalyTodoDate(){
        Calendar calendar = Calendar.getInstance();
        int cDay = calendar.get(Calendar.DAY_OF_MONTH);
        int cMonth = calendar.get(Calendar.MONTH);
        int cYear = calendar.get(Calendar.YEAR);

        mDatePicker=new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                txtDate.setText(year+"/"+month+"/"+dayOfMonth);
            }
        },cYear, cMonth, cDay);
        mDatePicker.show();
    }

    @SuppressLint({"SimpleDateFormat", "NonConstantResourceId"})
    //class created for adding todo with details
    void AddTodo(){
        boolean validate = true;
        Task todo= new Task();
        Date todoDate;
        int priority = 1;
        int checkedPriority = -1;

        //For validating the text field so that nothing is empty
        if (txtTitle.getText().toString().trim().equals("")){
            txtTitle.setError("Title cannot be empty");
            txtTitle.requestFocus();
            validate = false;
        }
        if (txtDescription.getText().toString().trim().equals("")){
            txtDescription.setError("Description cannot be empty");
            txtTitle.requestFocus();
            validate = false;
        }
        if (txtDate.getText().toString().trim().equals("")){
            txtDate.setError("Date cannot be empty");
            txtTitle.requestFocus();
            validate = false;
        }

        todo.setTitle(txtTitle.getText().toString());
        todo.setDescription(txtDescription.getText().toString());


            if(rbHigh.isChecked()){
                todo.setPriority(1);
            }else if(rbMedium.isChecked()){
                todo.setPriority(0);
            }else{
                todo.setPriority(-1);
            }


        try {
            DateFormat formatter;
            formatter=new SimpleDateFormat("yyyy/MM/dd");
            todoDate=(Date)formatter.parse(txtDate.getText().toString());
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                Instant timestamp1 = Instant.now();
                long seconds = timestamp1.getEpochSecond();

                todo.setTimesago(seconds);
            }


            todo.setCreatedDate(todoDate);

        }
        catch(ParseException e){
            e.printStackTrace();
        }
//        checkedPriority=rgPriority.getCheckedRadioButtonId();
//        switch (checkedPriority){
//            case R.id.edit_rb_high:
//                priority = highPriority;
//                break;
//            case R.id.edit_rb_medium:
//                priority = mediumPriority;
//                break;
//            case R.id.edit_rb_low:
//                priority = lowPriority;
//                break;
//        }

        //check if it is for updation or adding new todo
        if (validate){
            if (todoId!= -1){
                todo.setId(todoId);
                mTodoViewModel.update(todo);
                Toast.makeText(getActivity(),getText(R.string.crud_update),Toast.LENGTH_SHORT).show();
            }else{
                mTodoViewModel.insert(todo);
                Toast.makeText(getActivity(),getText(R.string.crud_save),Toast.LENGTH_SHORT).show();
            }
            //After saving or updating return back to main page
            Intent intent = new Intent(getActivity(),MainActivity.class);
            startActivity(intent);
        }
    }
}