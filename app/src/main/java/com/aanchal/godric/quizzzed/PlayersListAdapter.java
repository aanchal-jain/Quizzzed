package com.aanchal.godric.quizzzed;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PlayersListAdapter extends RecyclerView.Adapter<PlayersListAdapter.ViewHolder> {
    Context context;
    ArrayList<Player> players;
    public PlayersListAdapter(Context context, ArrayList<Player> players) {
        this.players=players;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder
                (LayoutInflater.
                from(context).
                inflate(R.layout.players_list_item,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.player_name.setText(players.get(i).getUsername());
        Picasso.get().load(players.get(i).getImage()).into(viewHolder.player_img);
    }

    @Override
    public int getItemCount() {
        return players.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView player_img;
        TextView player_name;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            player_img = itemView.findViewById(R.id.profile_img_list);
            player_name = itemView.findViewById(R.id.player_name);
        }
    }
}
