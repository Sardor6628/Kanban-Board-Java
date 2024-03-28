package com.example.kanban_board_java.ui.home.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kanban_board_java.R;
import com.example.kanban_board_java.data.response.TaskResponse;
import com.example.kanban_board_java.databinding.RcvItemCompleteTaskBinding;
import com.example.kanban_board_java.utils.Utils;

import java.util.ArrayList;

public class CompleteTaskAdapter extends RecyclerView.Adapter<CompleteTaskAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<TaskResponse> list = new ArrayList<>();
    private OnItemMenuClickListener onItemMenu;

    public CompleteTaskAdapter(Context context) {
        this.context = context;
    }

    public interface OnItemMenuClickListener {
        void onItemClick(TaskResponse data, int id);
    }

    public void setOnItemMenuClickListener(OnItemMenuClickListener listener) {
        this.onItemMenu = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RcvItemCompleteTaskBinding binding = RcvItemCompleteTaskBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        TaskResponse data = list.get(position);

        holder.binding.tvTitle.setText(data.getTitle());
        holder.binding.tvDescription.setText(data.getDescription());

        if (data.getCompletedTime() != 1000L){
            holder.binding.tvCompleteTime.setVisibility(View.VISIBLE);
            holder.binding.tvCompleteTime.setText("Completed on " + Utils.formatString(data.getCompletedTime()/1000, "yyyy/MM/dd, HH:mm:ss"));
        }else{
            holder.binding.tvCompleteTime.setVisibility(View.GONE);
        }

        if (data.getSpentTime() == 0L) {
            holder.binding.llSpentTime.setVisibility(View.GONE);
        }
        holder.binding.tvSpent.setText(Utils.formatSecondToString(data.getSpentTime()));

        holder.binding.btnMore.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context, holder.binding.btnMore);
            popupMenu.getMenuInflater().inflate(R.menu.task_menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(item -> {
                if (onItemMenu != null) {
                    onItemMenu.onItemClick(data, item.getItemId());
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
        this.list = data;
        notifyDataSetChanged();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        RcvItemCompleteTaskBinding binding;

        MyViewHolder(RcvItemCompleteTaskBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
