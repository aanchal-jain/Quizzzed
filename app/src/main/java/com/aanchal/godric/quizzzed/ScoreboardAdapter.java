package com.aanchal.godric.quizzzed;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ScoreboardAdapter extends RecyclerView.Adapter<ScoreboardAdapter.ViewHolder> {
    Context context;
    ArrayList<Player> players;
    public ScoreboardAdapter(Context context, ArrayList<Player> players) {
        this.context = context;
        this.players=players;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder
                (LayoutInflater.
                        from(context).
                        inflate(R.layout.scoreboard_item,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        int[] badge_img = {R.drawable.first,R.drawable.second,R.drawable.third,R.drawable.fourth,R.drawable.fifth};
        Picasso.get().load(players.get(i).getImage()).into(viewHolder.player_img);
        viewHolder.player_name.setText(players.get(i).getUsername());
        viewHolder.badge.setImageResource(badge_img[i]);
        viewHolder.score.setText("Score: "+players.get(i).getScore());
    }

    @Override
    public int getItemCount() {
        if(players!=null)
            return players.size();
        else
            return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView player_img,badge;
        TextView player_name,score;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            player_img = itemView.findViewById(R.id.profile_img_scoreboard);
            badge = itemView.findViewById(R.id.list_badge);
            player_name = itemView.findViewById(R.id.player_name_scoreboard);
            score = itemView.findViewById(R.id.player_score);
        }
    }
}
