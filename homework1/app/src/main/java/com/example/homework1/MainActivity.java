package com.example.homework1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        SearchAdapter searchAdapter = new SearchAdapter();
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(searchAdapter);

        List<String> allEntries = new ArrayList<>();
        for(int i=0;i<100;i++)
            allEntries.add("这是第"+String.valueOf(i)+"行");
        searchAdapter.notifyItems(allEntries);

        EditText editText = findViewById(R.id.edittext);
        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                editText.setText("");
            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String string = s.toString();
                if(string.isEmpty())
                    button.setVisibility(View.GONE);
                else
                    button.setVisibility(View.VISIBLE);
                List<String> searchResults = new ArrayList<>();
                for(int i = 0;i < 100; i++)
                    if(allEntries.get(i).contains(string))
                        searchResults.add(allEntries.get(i));
                searchAdapter.notifyItems(searchResults);
            }
        });



    }
    class TextViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView mTextView;

        public TextViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.text);
            itemView.setOnClickListener(this);
        }

        public void bind(String text){mTextView.setText(text);}
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(v.getContext(), DisplayActivity.class);
            intent.putExtra("extra",mTextView.getText().toString());
            v.getContext().startActivity(intent);
        }
    }

    class SearchAdapter extends RecyclerView.Adapter<TextViewHolder>{

        @NonNull
        private List<String> mItems = new ArrayList<>();

        @NonNull
        @Override
        public TextViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new TextViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.single_item,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull TextViewHolder holder, int position) {
            holder.bind(mItems.get(position));
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        public void notifyItems(@NonNull List<String> items){
            mItems.clear();
            mItems.addAll(items);
            notifyDataSetChanged();
        }
    }
}
