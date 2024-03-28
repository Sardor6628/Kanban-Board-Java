package com.example.kanban_board_java.ui.completetask.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.kanban_board_java.data.response.TaskResponse;
import com.example.kanban_board_java.databinding.RcvItemCompleteShowTaskBinding;
import com.example.kanban_board_java.utils.Utils;

import java.util.ArrayList;

public class CompleteTaskShowAdapter extends RecyclerView.Adapter<CompleteTaskShowAdapter.MyViewHolder> {

    private final Context context;
    private ArrayList<TaskResponse> list = new ArrayList<>();
    private OnItemClickListener onItemClickListener;

    public CompleteTaskShowAdapter(Context context) {
        this.context = context;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public void setData(ArrayList<TaskResponse> data) {
        list = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RcvItemCompleteShowTaskBinding binding = RcvItemCompleteShowTaskBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        TaskResponse data = list.get(position);
        holder.binding.tvTitle.setText(data.getTitle());
        holder.binding.tvDescription.setText(data.getDescription());
        if (data.getSpentTime() == 0L){
            holder.binding.llSpentTime.setVisibility(View.GONE);
        }

        holder.binding.tvSpent.setText(Utils.formatSecondToString(data.getSpentTime()));

        if (data.getCompletedTime() != 1000L){
            holder.binding.tvCompleteTime.setVisibility(View.VISIBLE);
            holder.binding.tvCompleteTime.setText("Completed on " + Utils.formatString(data.getCompletedTime()/1000, "yyyy/MM/dd, HH:mm:ss"));
        }else{
            holder.binding.tvCompleteTime.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(view -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(data, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnItemClickListener {
        void onItemClick(TaskResponse data, int position);
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        RcvItemCompleteShowTaskBinding binding;

        MyViewHolder(RcvItemCompleteShowTaskBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}