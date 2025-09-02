package com.spamalot.arcade.robostar;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerAdapter;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.math.Vector2;

import org.lwjgl.glfw.GLFW;

public class InputController extends ControllerAdapter {
  private Controller controller;
  private final Vector2 move = new Vector2();
  private final Vector2 aim = new Vector2();
  private boolean bombPressed = false;
  private boolean startPressed = false;
  private float deadzone = 0.22f;
  private float aimDeadzone = 0.28f;

  public InputController() {
    // Pick first connected controller, if any
    if (Controllers.getControllers().size > 0) {
      controller = Controllers.getControllers().first();
    }
    Controllers.addListener(this);
  }

  public boolean hasController() {
    return controller != null;
  }

  public String controllerName() {
    return controller == null ? "None" : controller.getName();
  }

  public void update() {
    if (controller == null)
      return;

    float lx = controller.getAxis(GLFW.GLFW_GAMEPAD_AXIS_LEFT_X);
    float ly = controller.getAxis(GLFW.GLFW_GAMEPAD_AXIS_LEFT_Y);
    // Invert Y to make up = -1 become up direction visually
    move.set(applyDeadzone(lx, deadzone), -applyDeadzone(ly, deadzone));

    float rx;
    float ry;
    rx = controller.getAxis(GLFW.GLFW_GAMEPAD_AXIS_RIGHT_X);
    ry = controller.getAxis(GLFW.GLFW_GAMEPAD_AXIS_RIGHT_X);
    aim.set(applyDeadzone(rx, aimDeadzone), -applyDeadzone(ry, aimDeadzone));

    // Bomb on R1 if available, otherwise A
    boolean r1 = false;
    r1 = controller.getButton(GLFW.GLFW_GAMEPAD_AXIS_RIGHT_TRIGGER);

    boolean a = controller.getButton(GLFW.GLFW_GAMEPAD_BUTTON_A);
    bombPressed = r1 || (!r1 && a);

    // Start
    startPressed = controller.getButton(GLFW.GLFW_GAMEPAD_BUTTON_LEFT_THUMB);
  }

  public Vector2 getMove() {
    return move;
  }

  public Vector2 getAim() {
    return aim;
  }

  public boolean pollBombPressed() {
    // edge-trigger: simple polling (you could debounce if desired)
    return bombPressed;
  }

  public boolean isStartPressed() {
    return startPressed;
  }

  private float applyDeadzone(float v, float dz) {
    return Math.abs(v) < dz ? 0f : v;
  }

  @Override
  public void connected(Controller controller) {
    if (this.controller == null)
      this.controller = controller;
    Gdx.app.log("Input", "Controller connected: " + controller.getName());
  }

  @Override
  public void disconnected(Controller controller) {
    if (this.controller == controller)
      this.controller = null;
    Gdx.app.log("Input", "Controller disconnected.");
  }

  public void dispose() {
    Controllers.removeListener(this);
  }
}
