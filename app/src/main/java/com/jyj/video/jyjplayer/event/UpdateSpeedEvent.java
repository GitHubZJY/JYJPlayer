package com.jyj.video.jyjplayer.event;

/**
 * Created by 74215 on 2018/12/10.
 */

public class UpdateSpeedEvent {

    private float speed;

    public UpdateSpeedEvent(float speed) {
        this.speed = speed;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }
}
