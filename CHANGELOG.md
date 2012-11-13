## 1.2.2
- updated for Minecraft 1.4.4
- added animation convenience methods to `Color` and `ShapeTransform` objects

## 1.2.1
- updated for Minecraft 1.4.2
- added a section to the profiler so users can see the performance impact of
  LibShapeDraw on the shift-F3 debug screen; accessible under
  `root.gameRenderer.level.LibShapeDraw`
- a few minor internal changes to better support deobfuscation
- added `Timeline.playLoop(boolean reverse)` convenience method

## 1.2
- updated for Minecraft 1.4
- added update check
- added a few more convenience methods for primitive types
- fixed minor bug with `isVisibleWhenHidingGui` logic

## 1.1
- use a new rendering hook, eliminating graphical glitches near water
- improved Trident animation library integration: built-in interpolators for
  types in `libshapedraw.primitive`
- added `getPartialTick` method to `LSDPreRenderEvent` and `MinecraftAccess`
- added many other convenience methods, especially for `Vector3`
- added `mcmod.info` files and a logo for improved ForgeModLoader integration
  (Forge is still supported but *not* required)

## 1.0
- initial release
