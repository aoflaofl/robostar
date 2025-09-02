# Hybrid Arcade Shooter (Maven + libGDX)

A minimal, playable dual-stick arcade shooter prototype inspired by **Robotron** and **Sinistar**.

## Features
- Desktop (LWJGL3) libGDX app, built with Maven
- Dual-analog gamepad input only (tested with Logitech-style pads)
- Simple state machine: **Menu → Play → Game Over**
- Placeholder shapes with `ShapeRenderer` (retro prototype)
- Wraparound world: 3× viewport width/height
- Waves, crystals (for bombs), humans (power-ups), boss per wave
- Score, lives, high score saved via `Preferences`

## Build & Run
```bash
# from project root
mvn -q package
java -jar target/hybrid-arcade-shooter-1.0.0-SNAPSHOT-shaded.jar
# or run directly
mvn -q exec:java
```

## Controls (Gamepad only)
- **Left Stick:** Move
- **Right Stick:** Aim & auto-fire when tilted
- **Right Shoulder (R1):** Launch bomb (falls back to **A** if R1 not present)
- **Start:** Confirm / Advance menu

> If no controller is connected, the menu will prompt you to connect one.
