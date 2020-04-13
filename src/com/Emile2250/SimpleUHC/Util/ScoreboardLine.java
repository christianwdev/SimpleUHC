package com.Emile2250.SimpleUHC.Util;

import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

public class ScoreboardLine {

    private int line;
    private Score score;
    private String previous;
    private Objective obj;
    private Scoreboard board;

    public ScoreboardLine(Objective o, String text, int line) {
        this.line = line;
        this.obj = o;
        this.board = obj.getScoreboard();
        this.score = obj.getScore(text);
        this.previous = text;
        this.score.setScore(line);
    }

    public void update(String text) {
        board.resetScores(previous);

        score = obj.getScore(ChatColor.translateAlternateColorCodes('&', text));
        score.setScore(line);

        previous = ChatColor.translateAlternateColorCodes('&', text);
    }

}
