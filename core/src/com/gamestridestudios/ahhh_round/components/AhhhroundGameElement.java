package com.gamestridestudios.ahhh_round.components;

public interface AhhhroundGameElement {
    void setVisibility(boolean visible);
    void fadeIn(double time);
    void fadeOut(double time);
    void setPosition(double x, double y);
    void setBounds(double x, double y, double w, double h);
}