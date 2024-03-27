package com.example.kanban_board_java.ui.home.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import com.example.kanban_board_java.data.response.TaskResponse;
import com.example.kanban_board_java.databinding.RcvItemProgressTaskBinding;
import com.example.kanban_board_java.utils.Utils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class ProgressTaskAdapter extends RecyclerView.Adapter<ProgressTaskAdapter.MyViewHolder> {

    private Activity context;
    private ArrayList<TaskResponse> list = new ArrayList<>();
    private HashMap<Integer, Timer> timersMap = new HashMap<>();

    private OnItemClickCompleteListener onItemClickComplete;
    private OnItemClickPauseListener onItemClickPause;

    public ProgressTaskAdapter(Activity context) {
        this.context = context;
    }

    public void setOnItemClickComplete(OnItemClickCompleteListener onItemClickComplete) {
        this.onItemClickComplete = onItemClickComplete;
    }

    public void setOnItemClickPause(OnItemClickPauseListener onItemClickPause) {
        this.onItemClickPause = onItemClickPause;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RcvItemProgressTaskBinding binding = RcvItemProgressTaskBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        TaskResponse data = list.get(position);

        holder.binding.tvTitle.setText(data.getTitle());
        holder.binding.tvDescription.setText(data.getDescription());

        long startTime = data.getStartedTime();

        long elapsedTime = Utils.getSecondBetweenTime(startTime, System.currentTimeMillis());
        long totalSecond = elapsedTime + data.getSpentTime();
        holder.binding.tvSpent.setText(Utils.formatSecondToString(totalSecond));

        if (!timersMap.containsKey(position)) {
            Timer timer = new Timer();
            timersMap.put(position, timer);
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        long elapsedTime = Utils.getSecondBetweenTime(startTime, System.currentTimeMillis());
                        long totalSecond = elapsedTime + data.getSpentTime();
                        holder.binding.tvSpent.setText(Utils.formatSecondToString(totalSecond));
                    });
                }
            }, 1000, 1000);
        }

        holder.binding.btnComplete.setOnClickListener(view -> {
            Timer timer = timersMap.remove(position);
            if (timer != null) {
                timer.cancel();
            }
            long elapsedTime1 = Utils.getSecondBetweenTime(startTime, System.currentTimeMillis());
            data.setSpentTime(elapsedTime1 + data.getSpentTime());
            if (onItemClickComplete != null) {
                onItemClickComplete.onItemClickComplete(data, position);
            }
        });

        holder.binding.btnPause.setOnClickListener(view -> {
            Timer timer = timersMap.remove(position);
            if (timer != null) {
                timer.cancel();
            }
            long elapsedTime1 = Utils.getSecondBetweenTime(startTime, System.currentTimeMillis());
            data.setSpentTime(elapsedTime1 + data.getSpentTime());
            if (onItemClickPause != null) {
                onItemClickPause.onItemClickPause(data, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setData(ArrayList<TaskResponse> data) {
        for (Timer timer : timersMap.values()) {
            timer.cancel();
        }
        timersMap.clear();
        list = data;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        RcvItemProgressTaskBinding binding;

        MyViewHolder(RcvItemProgressTaskBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface OnItemClickCompleteListener {
        void onItemClickComplete(TaskResponse taskResponse, int position);
    }

    public interface OnItemClickPauseListener {
        void onItemClickPause(TaskResponse taskResponse, int position);
    }
}

