package com.qeue.ahhh_round.components;


import com.badlogic.gdx.graphics.Texture;

public class Player extends Character {
    private final double INITIAL_SPEED = 1.75;
    private final double INITIAL_JUMP_AIR_TIME = 0.24;
    private final double ACCELERATION = 0.01;

    private double elapsedJumpTime;
    private double jumpStartAngle;
    private boolean currentlyJumping;
    private Double jumpHeight;
    private Runnable finishedJumpCallback;
    private double jumpAirTime;
    private double movementSpeed;

    public Player(double width, double characterRadius, double initialAngleFromCenter, double midpointPercent, Texture texture, double jumpHeight) {
        super(width, characterRadius, initialAngleFromCenter, midpointPercent, texture);
        this.jumpHeight = jumpHeight;
        movementSpeed = INITIAL_SPEED;
        jumpAirTime = INITIAL_JUMP_AIR_TIME;
        elapsedJumpTime = 0;
        jumpStartAngle = 0;
        currentlyJumping = false;
    }

    public void updateAngleAndPosition(double timeSinceLastUpdate, double orbitRadius, Point orbitCenter) {
        setPositionAroundCircle(currentAngleFromCenter + timeSinceLastUpdate * movementSpeed, orbitRadius + getJumpHeight(timeSinceLastUpdate), orbitCenter);
    }

    public void accelerate() {
        double oldSpeed = movementSpeed;
        movementSpeed += ACCELERATION;
        jumpAirTime *= (oldSpeed / movementSpeed);
    }

    public void die() {
        currentlyJumping = false;
        elapsedJumpTime = 0;
        jumpStartAngle = 0;
        movementSpeed = INITIAL_SPEED;
        jumpAirTime = INITIAL_JUMP_AIR_TIME;
    }

    public void jumpWithCallback(Runnable finishedJumpCallback) {
        currentlyJumping = true;
        jumpStartAngle = currentAngleFromCenter;
        elapsedJumpTime = 0;
        this.finishedJumpCallback = finishedJumpCallback;
    }

    public boolean isJumping() {
        return currentlyJumping;
    }

    public double getJumpStartAngle() {
        return jumpStartAngle;
    }

    private double getJumpHeight(double timeSinceLastUpdate) {
        double additionalDistanceFromCenter = 0;
        if (currentlyJumping) {
            elapsedJumpTime += timeSinceLastUpdate;
            additionalDistanceFromCenter = -4.0 * Math.pow(1 / jumpAirTime, 2) * jumpHeight * Math.pow((elapsedJumpTime - jumpAirTime) / 2.0, 2) + jumpHeight;
            if (additionalDistanceFromCenter < 0) {
                additionalDistanceFromCenter = 0;
                elapsedJumpTime = 0;
                currentlyJumping = false;
                finishedJumpCallback.run();
            }
        }
        return additionalDistanceFromCenter;
    }
}
