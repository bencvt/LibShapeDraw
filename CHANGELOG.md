## implemented but not yet released
- add a section to the profiler so users can see the performance impact of
  LibShapeDraw on the shift-F3 debug screen; accessible under
  `root.gameRenderer.level.LibShapeDraw`

## 1.2
- update for Minecraft 1.4
- add update check
- add a few more convenience methods for primitive types
- fix minor bug with isVisibleWhenHidingGui logic

## 1.1
- use a new rendering hook, eliminating graphical glitches near water
- improved Trident animation library integration: built-in interpolators for
  types in `libshapedraw.primitive`
- add getPartialTick method to LSDPreRenderEvent and MinecraftAccess.
- add many other convenience methods, especially for Vector3
- add mcmod.info files and a logo for improved ForgeModLoader integration (Forge
  is still supported but *not* required)

## 1.0
- initial release
