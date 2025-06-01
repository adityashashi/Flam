# Fireworks App (OpenGL ES 3.0)

This is a simple fireworks particle system made with OpenGL ES 3.0 on Android. When you tap the screen, a burst of particles appears and fades out.

### Features
- Colorful particles that fade and move
- Tap to trigger a new burst
- Uses GPU shaders for smooth effects

### How It Works
- Particles are created in Java and drawn with OpenGL
- A vertex shader and fragment shader control how they look
- Gravity and lifetime are used to animate them

### How To Run
1. Open the project in Android Studio
2. Connect your Android phone (or use emulator)
3. Press Run

### Requirements
- Android Studio
- Device with OpenGL ES 3.0 support
- minSdkVersion 18