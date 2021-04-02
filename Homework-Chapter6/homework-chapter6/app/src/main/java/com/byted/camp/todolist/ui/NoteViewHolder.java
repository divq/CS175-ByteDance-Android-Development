package com.byted.camp.todolist.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;

import com.byted.camp.todolist.NoteOperator;
import com.byted.camp.todolist.R;
import com.byted.camp.todolist.beans.Note;
import com.byted.camp.todolist.beans.Priority;
import com.byted.camp.todolist.beans.State;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NoteViewHolder extends RecyclerView.ViewHolder {

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT =
            new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss", Locale.ENGLISH);

    private final NoteOperator operator;

    private CheckBox checkBox;
    private TextView contentText;
    private TextView dateText;
    private View deleteBtn;
    private Context mContext;
    public NoteViewHolder(@NonNull View itemView, NoteOperator operator, Context mContext) {
        super(itemView);
        this.operator = operator;
        this.mContext = mContext;
        checkBox = itemView.findViewById(R.id.checkbox);
        contentText = itemView.findViewById(R.id.text_content);
        dateText = itemView.findViewById(R.id.text_date);
        deleteBtn = itemView.findViewById(R.id.btn_delete);
    }

    public void bind(final Note note) {
        contentText.setText(note.getContent());
        dateText.setText(SIMPLE_DATE_FORMAT.format(note.getDate()));

        checkBox.setOnCheckedChangeListener(null);
        checkBox.setChecked(note.getState() == State.DONE);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                note.setState(isChecked ? State.DONE : State.TODO);
                operator.updateNote(note);
            }
        });
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                operator.deleteNote(note);
            }
        });

        if (note.getState() == State.DONE) {
            contentText.setTextColor(Color.GRAY);
            contentText.setPaintFlags(contentText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            contentText.setTextColor(Color.BLACK);
            contentText.setPaintFlags(contentText.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
        }

        itemView.setBackgroundColor(note.getPriority().color);
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showModifyDialog(note);
                return true;
            }
        });
    }

   void showModifyDialog(final Note note)
    {

        final AlertDialog.Builder modifyDialog =
                new AlertDialog.Builder(mContext);
        final View dialogView = LayoutInflater.from(mContext)
                .inflate(R.layout.activity_note_modifier,null);
        modifyDialog.setTitle("编辑todo内容");
        modifyDialog.setView(dialogView);

        final RadioGroup radioGroup = dialogView.findViewById(R.id.radio_group);
        final RadioButton highRadioButton = dialogView.findViewById(R.id.btn_high);
        final RadioButton mediumRadioButton = dialogView.findViewById(R.id.btn_medium);
        final RadioButton lowRadioButton = dialogView.findViewById(R.id.btn_low);
        switch (note.getPriority()) {
            case High:
                highRadioButton.setChecked(true);
                break;
            case Medium:
                mediumRadioButton.setChecked(true);
                break;
            default:
               lowRadioButton.setChecked(true);
        }


        final EditText editText = dialogView.findViewById(R.id.edit_text);
        editText.setText(contentText.getText());

        modifyDialog.setPositiveButton("Confirm",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Note newNode = new Note(note.id);
                        newNode.setState(note.getState());
                        newNode.setContent(editText.getText().toString());
                        newNode.setDate(new Date(System.currentTimeMillis()));
                        switch (radioGroup.getCheckedRadioButtonId()) {
                            case R.id.btn_high:
                                newNode.setPriority(Priority.High);
                                break;
                            case R.id.btn_medium:
                                newNode.setPriority(Priority.Medium);
                                break;
                            default:
                                newNode.setPriority(Priority.Low);
                        }
                        operator.updateNote(newNode);
                }
        });

        modifyDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

        modifyDialog.show();

    }
}
