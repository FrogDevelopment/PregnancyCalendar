package fr.frogdevelopment.pregnancycalendar.todo;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.FormatStyle;

import java.util.ArrayList;
import java.util.List;

import fr.frogdevelopment.pregnancycalendar.R;

import static fr.frogdevelopment.pregnancycalendar.PregnancyUtils.amenorrheaDate;

public class TodoFragment extends Fragment {

    private DateTimeFormatter dateTimeFormatter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_todo, container, false);
        RecyclerView mRecyclerView = (RecyclerView) rootView.findViewById(R.id.todo_recycler);

        // use this setting to improve performance if you know that changes in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        dateTimeFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL);

        TodoAdapter mAdapter = new TodoAdapter(getTodos());
        mRecyclerView.setAdapter(mAdapter);

        return rootView;
    }

    @NonNull
    private List<Todo> getTodos() {
        List<Todo> todos = new ArrayList<>();

        // sonogram 1 : week 11 -> 13
        Todo sonogram_1 = createTodo(11, 13);
        sonogram_1.resource = R.drawable.sonograph_1;
        sonogram_1.title = R.string.card_sonogram_1_text;
        todos.add(sonogram_1);

        // trisomy 21  detection : week 14 -> 18
        Todo trisomy = createTodo(14, 18);
        trisomy.resource = R.drawable.trisomy;
        trisomy.title = R.string.card_trisomy_21_text;
        todos.add(trisomy);

        // sonogram 2 : week 22 -> 24
        Todo sonogram_2 = createTodo(22, 24);
        sonogram_2.resource = R.drawable.sonograph_2;
        sonogram_2.title = R.string.card_sonogram_2_text;
        todos.add(sonogram_2);

        // sonogram 3 : week 32 -> 34
        Todo sonogram_3 = createTodo(32, 34);
        sonogram_3.resource = R.drawable.sonograph_3;
        sonogram_3.title = R.string.card_sonogram_3_text;
        todos.add(sonogram_3);

        return todos;
    }

    private Todo createTodo(int weekStart, int weekEnd) {
        Todo todo = new Todo();
//        todo.weekStartNumber = weekStart;
        todo.weekStartLabel = amenorrheaDate.plusWeeks(weekStart).format(dateTimeFormatter);
//        todo.weekEndNumber = weekEnd;
        todo.weekEndLabel = amenorrheaDate.plusWeeks(weekEnd).format(dateTimeFormatter);

        return todo;
    }

}
