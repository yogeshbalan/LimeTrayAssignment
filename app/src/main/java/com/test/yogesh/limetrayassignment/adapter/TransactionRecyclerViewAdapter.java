package com.test.yogesh.limetrayassignment.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.test.yogesh.limetrayassignment.R;
import com.test.yogesh.limetrayassignment.model.Expenses;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yogesh on 31/5/16.
 */
public class TransactionRecyclerViewAdapter extends RecyclerView.Adapter<TransactionRecyclerViewAdapter.ViewHolder> {

    View mView;
    Context context;
    List<Expenses> expensesList = new ArrayList<>();

    public TransactionRecyclerViewAdapter(Context context, List<Expenses> expensesList) {
        this.context = context;
        this.expensesList = expensesList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transaction, null);
        // create ViewHolder
        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.description.setText(expensesList.get(position).getDescription());
        holder.status.setText((expensesList.get(position).getState()).toLowerCase());
        holder.amount.setText(String.valueOf("\u20A8 " + expensesList.get(position).getAmount()));
        if (expensesList.get(position).getState().equalsIgnoreCase("unverified")) {
            holder.amount.setTextColor(Color.parseColor("#9E9E9E"));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.imageView.setBackgroundDrawable(context.getDrawable(R.drawable.ic_help_grey_500_36dp));
            }
        } else if (expensesList.get(position).getState().equalsIgnoreCase("fraudulent")) {
            holder.amount.setTextColor(Color.parseColor("#F44336"));
            holder.status.setTextColor(Color.parseColor("#EF9A9A"));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    holder.imageView.setBackgroundDrawable(context.getDrawable(R.drawable.ic_error_red_500_36dp));
                }
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.imageView.setBackgroundDrawable(context.getDrawable(R.drawable.ic_check_circle_green_500_36dp));
            }
        }
    }

    @Override
    public int getItemCount() {
        return expensesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView description, amount, status;
        public ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            description = (TextView) itemView.findViewById(R.id.description);
            amount = (TextView) itemView.findViewById(R.id.amount);
            status = (TextView) itemView.findViewById(R.id.status);
        }
    }

}
