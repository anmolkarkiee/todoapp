package com.example.todoapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.todoapp.data.TodoRepository;
import com.example.todoapp.model.Task;
import com.example.todoapp.viewmodel.TodoViewModel;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


public class TodoListFragment extends Fragment {

    ArrayList<Task> tasks = new ArrayList<Task>();
    View rootView;
    private TodoViewModel mTodoViewModel;
    RecyclerView todoRecyclerView;
    Button btn;
    TextView createdAgo;
    boolean isInDateSort;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_todo_list, container, false);
        mTodoViewModel = ViewModelProviders.of(this).get(TodoViewModel.class);
        todoRecyclerView = rootView.findViewById(R.id.recyclerview);
        LinearLayoutManager layoutManager= new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        todoRecyclerView.setLayoutManager(layoutManager);
//        createdAgo = rootView.findViewById(R.id.time);
//        Log.d("created ago", "onCreateView: "+createdAgo);
        updateRV();
        isInDateSort=false;

        return rootView;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menu_sort_by_dat:
                updateSort();
                break;

            case  R.id.menu_sort_by_priority:
                updateRV();

        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

    }

    //for displaying todo list in main page
    void updateRV(){
        isInDateSort=false;
        mTodoViewModel.getAllTodos().observe(this, new Observer<List<Task>>() {
            @Override
            public void onChanged(List<Task> todoList) {
                TodoAdapter adapter = new TodoAdapter(todoList);
                todoRecyclerView.setAdapter(adapter);
            }
        });
    }

    public void updateSort(){
        isInDateSort=true;
        mTodoViewModel.getAllTodosByDate().observe(this, new Observer<List<Task>>() {
            @Override
            public void onChanged(List<Task> todoList) {
                TodoAdapter adapter = new TodoAdapter(todoList);
                todoRecyclerView.setAdapter(adapter);
            }
        });
    }
    private class TodoAdapter extends RecyclerView.Adapter<TodoHolder>{
        List<Task> mTodoList;
        public TodoAdapter(List<Task> todoList){
            mTodoList = todoList;
        }
        @NonNull
        @Override
        public TodoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            return new TodoHolder(layoutInflater, parent);
        }
        //for changing the color of todo list according to the priority
        @Override
        public void onBindViewHolder(@NonNull TodoHolder holder, int position) {
            Task todo = mTodoList.get(position);

            LinearLayout layout = (LinearLayout)((ViewGroup)holder.mTitle.getParent());

            holder.bind(todo);

            TextView priority = layout.findViewById(R.id.priority);
            createdAgo = layout.findViewById(R.id.time);

            if(todo.getPriority()==1){
                priority.setText( "Priority  high");
                priority.setTextColor(Color.RED);
            }else if(todo.getPriority()==0){
                priority.setText("Priority medium");
                priority.setTextColor(Color.YELLOW);
            }else{
                priority.setText("Priority low");
                priority.setTextColor(Color.GREEN);
            }


            long seconds = todo.getTimesago();
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                Instant instant = Instant.ofEpochSecond(seconds);
                Instant timestamp1 = Instant.now();
//
                Duration duration = Duration.between(timestamp1, instant);
                long sec = duration.getSeconds();
//
                float hours = sec / 86400;


                createdAgo.setText("created " +hours+   "  hours ago");

            }



        }
        @Override
        public int getItemCount() {
            return mTodoList.size();
        }
        public Task getTodo(int index){
            return mTodoList.get(index);
        }

        public Task getTodoAt(int index){
            return mTodoList.get(index);
        }
    }
    private class TodoHolder extends RecyclerView.ViewHolder{
        TextView mTitle, mDate, mDesprition;
        Button mDeleteBtn;
        CheckBox mCheckBox;
        LinearLayout linearLayout ;
        int onOff =0;

        public TodoHolder(LayoutInflater inflater, ViewGroup parentViewGroup) {
            super(inflater.inflate(R.layout.list_item_todo, parentViewGroup, false));
            mTitle = itemView.findViewById(R.id.list_title);
            mDate = itemView.findViewById(R.id.list_date);
            mDesprition=itemView.findViewById(R.id.list_description);
            mCheckBox = itemView.findViewById(R.id.done);
            linearLayout = itemView.findViewById(R.id.linearLayout);


            mCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(isInDateSort){
                        TodoAdapter adapter = new TodoAdapter(mTodoViewModel.getAllTodosByDate().getValue());
                        int position = getAdapterPosition();
                        Task task = adapter.getTodoAt(position);
                        mTodoViewModel.deleteById(task);
                        Toast.makeText(getActivity(),task.getTitle()+" task has been done", Toast.LENGTH_SHORT).show();
                    }else{
                        TodoAdapter adapter = new TodoAdapter(mTodoViewModel.getAllTodos().getValue());
                        int position = getAdapterPosition();
                        Task task = adapter.getTodoAt(position);
                        mTodoViewModel.deleteById(task);
                        Toast.makeText(getActivity(),task.getTitle()+" task has been done", Toast.LENGTH_SHORT).show();
                    }
                }
            });



            //for updating the list while the user clicks the todo
            mTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                        TodoAdapter adapter = new TodoAdapter(mTodoViewModel.getAllTodos().getValue());
                        int position = getAdapterPosition();
                        Task task = adapter.getTodoAt(position);
                        Intent intent = new Intent(getActivity(),EditTodoActivity.class);
                        intent.putExtra("TodoId", task.getId());
                        startActivity(intent);

                }
            });
            mDesprition.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TodoAdapter adapter = new TodoAdapter(mTodoViewModel.getAllTodos().getValue());
                    int position = getAdapterPosition();
                    Task task = adapter.getTodoAt(position);
                    Intent intent = new Intent(getActivity(),EditTodoActivity.class);
                    intent.putExtra("TodoId", task.getId());
                    startActivity(intent);
                }
            });
            mDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TodoAdapter adapter = new TodoAdapter(mTodoViewModel.getAllTodos().getValue());
                    int position = getAdapterPosition();
                    Task task = adapter.getTodoAt(position);
                    Intent intent = new Intent(getActivity(),EditTodoActivity.class);
                    intent.putExtra("TodoId", task.getId());
                    startActivity(intent);
                }
            });
        }
        //For displaying the list
        //adding commment
        public void bind(Task todo){
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy/MM/dd");
            mTitle.setText(todo.getTitle());
            mDesprition.setText(todo.getDescription());
            mDate.setText(dateFormatter.format(todo.getCreatedDate()));
        }

    }
}