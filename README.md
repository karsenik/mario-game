# Super Mario Bros. Clone (Java)

A 2D platformer inspired by *Super Mario Bros.*, built in Java with object-oriented design and the [StdDraw](https://introcs.cs.princeton.edu/java/stdlib/javadoc/StdDraw.html) library. Run, jump, avoid enemies, collect coins, use portals and reach the exit pipe across four levels.
 🎥 [Watch the gameplay video](https://youtu.be/1MdcnjWWnR0?si=E9BgWNEj3sanbNVo) 

## Features

- **Four levels:** each one adds a new challenge, from simple platforming to enemy avoidance, coin collection and a custom projectile level.
- **Platformer physics:** constant gravity, jumping that only works from the ground, and smooth horizontal movement.
- **Portals and pipes:** red portals teleport Mario between distant areas, and the exit pipe opens once the level goal is complete.
- **Patrolling enemies:** enemies move horizontally within their own assigned regions.
- **Custom Level 4:** enemies shoot vertical fireballs at a fixed rhythm, so jumps and portal entries must be timed carefully.
- **Calibration menu:** adjust the game speed (FPS) to match your computer before starting.
- **HUD:** level number, timer, death counter and a hint for the current objective.

## Controls

| Key | Action |
|---|---|
| `A` / `D` | Move left / right |
| `W` | Jump (only on the ground) |
| `S` | Crouch to enter the lower portal |
| `SPACE` | Start the game / continue after a level |
| `←` / `→` | Adjust FPS calibration (menu) |
| `R` | Restart |
| `C` | Camera |


## Levels

| Level | Focus |
|---|---|
| 1 | Basics: navigate static platforms with no hazards |
| 2 | Enemy avoidance: patrolling enemies with horizontal movement limits |
| 3 | Completion task: collect all coins to activate the exit pipe |
| 4 | Custom challenge: enemies launch vertical fireballs every 40 frames |

After each level a banner appears and the game continues when you press `SPACE`. After Level 4 the end screen is shown.

## How It Works

### Input handling
`Mario.handleInput()` moves Mario horizontally and triggers jumps. Jumping is edge-triggered: it only fires on the first frame the key is pressed and only while Mario is on the ground, so holding `W` does not cause repeated jumps.

### Collision detection
Collisions use Axis-Aligned Bounding Boxes (AABB). The distance between two centers is compared with the sum of their half-sizes on both axes. Every movement is checked before it is applied, so Mario never ends up inside a block.

### Portals and pipes
- Normal pipes are solid. The exit pipe becomes passable only when the level goal is done.
- The lower portal is solid unless Mario crouches with `S`.
- Side portals only let Mario through at their center (about 40% of the width), otherwise they act as walls.
- A `justTeleported` flag prevents Mario from bouncing between portals endlessly, and he gets a small upward boost on exit.

### Projectiles (Level 4)
Each enemy fires a projectile every 40 frames. Projectiles move with a constant upward velocity and use the same AABB check as everything else. A hit triggers Mario's death sequence.

### FPS calibration
The game speed depends on the computer, so the menu lets you change the pause time of the game loop. FPS is approximately `1000 / pauseDuration`.

## Class Overview

| Class | Role |
|---|---|
| `Mario` | Player state, movement, gravity, sprites, death and respawn |
| `Enemy` | Patrolling units with boundary checks and a `shoot()` method for Level 4 |
| `Map` | Static blocks, pipes, portals and coins, plus collision and teleport logic |
| `Level` | Data for one stage: ID, enemies, coins and hint text |
| `Game` | Main loop, menu, level banners, HUD, camera and end screen |

## Project Structure

```
├── src/       // Java source code
├── assets/    // sprites and images
└── README.md
```


## Getting Started

### Requirements
- Java JDK 17 or newer
- `StdDraw.java` (included in the repo / [download here](https://introcs.cs.princeton.edu/java/stdlib/))


### Run

```bash
# compile
javac *.java

# run
java Main
```
