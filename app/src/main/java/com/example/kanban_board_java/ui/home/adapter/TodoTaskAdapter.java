package com.example.kanban_board_java.ui.home.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.kanban_board_java.R;
import com.example.kanban_board_java.data.response.TaskResponse;
import com.example.kanban_board_java.databinding.RcvItemTodoTaskBinding;
import com.example.kanban_board_java.utils.Utils;
import java.util.ArrayList;

public class TodoTaskAdapter extends RecyclerView.Adapter<TodoTaskAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<TaskResponse> list = new ArrayList<>();
    private OnItemClickListener onItemClick;
    private OnItemMenuClickListener onItemMenu;

    public TodoTaskAdapter(Context context) {
        this.context = context;
    }

    public void setOnItemClick(OnItemClickListener onItemClick) {
        this.onItemClick = onItemClick;
    }

    public void setOnItemMenu(OnItemMenuClickListener onItemMenu) {
        this.onItemMenu = onItemMenu;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RcvItemTodoTaskBinding binding = RcvItemTodoTaskBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        TaskResponse data = list.get(position);
        holder.binding.tvTitle.setText(data.getTitle());
        holder.binding.tvDescription.setText(data.getDescription());

        if (data.getSpentTime() != 0L) {
            holder.binding.llSpentTime.setVisibility(android.view.View.VISIBLE);
            holder.binding.tvStart.setText(context.getString(R.string.resume));
            holder.binding.tvSpent.setText(Utils.formatSecondToString(data.getSpentTime()));
        } else {
            holder.binding.llSpentTime.setVisibility(android.view.View.GONE);
            holder.binding.tvStart.setText(context.getString(R.string.start));
        }

        holder.binding.btnStart.setOnClickListener(view -> {
            if (onItemClick != null) {
                onItemClick.onItemClick(data, position);
            }
        });

        holder.binding.btnMore.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(context, holder.binding.btnMore);
            popupMenu.getMenuInflater().inflate(R.menu.task_menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                if (onItemMenu != null) {
                    onItemMenu.onItemMenuClick(data, menuItem.getItemId());
                }
                return true;
            });
            popupMenu.show();
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setData(ArrayList<TaskResponse> data) {
        list = data;
        notifyDataSetChanged();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        RcvItemTodoTaskBinding binding;

        MyViewHolder(RcvItemTodoTaskBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(TaskResponse taskResponse, int position);
    }

    public interface OnItemMenuClickListener {
        void onItemMenuClick(TaskResponse taskResponse, int itemId);
    }
}

