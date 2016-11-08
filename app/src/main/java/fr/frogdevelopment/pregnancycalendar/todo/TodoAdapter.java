package fr.frogdevelopment.pregnancycalendar.todo;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import fr.frogdevelopment.pregnancycalendar.R;

class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.ViewHolder> {

    private final List<Todo> todos;

    TodoAdapter(List<Todo> todos) {
        this.todos = todos;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_todo, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Todo item = todos.get(position);

        holder.mIconView.setImageResource(item.resource);
        holder.mTitleView.setText(item.title);
        holder.mDescriptionView.setText(item.description);
        holder.mDateView.setText(item.weekStartLabel + " - " + item.weekEndLabel); // fixme
    }

    @Override
    public int getItemCount() {
        return todos.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private ImageView mIconView;
        private TextView mTitleView;
        private TextView mDescriptionView;
        private TextView mDateView;

        ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            mIconView = (ImageView) view.findViewById(R.id.card_icon);
            mTitleView = (TextView) view.findViewById(R.id.card_title);
            mDescriptionView = (TextView) view.findViewById(R.id.card_description);
            mDateView = (TextView) view.findViewById(R.id.card_date);
        }

        @Override
        public void onClick(View view) {
            Toast.makeText(view.getContext(), "Clicked = " + mTitleView.getText(), Toast.LENGTH_SHORT).show();
        }
    }
}
