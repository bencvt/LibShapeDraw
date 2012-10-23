# For Players

LibShapeDraw is a Minecraft client mod that is required by other mods.
It doesn't do anything on its own. Rather, it provides a set of flexible and
powerful drawing and animation tools for those other mods to use.

See the [official LibShapeDraw thread on minecraftforum.net](http://www.minecraftforum.net/topic/1458931-libshapedraw/)
for some screenshots and videos of what sort visual effects are possible.

## Installation

Installing this mod works exactly the same as any other Minecraft client mod.

1.  Make sure that
    [ModLoader](http://www.minecraftforum.net/topic/75440-modloader/)
    is installed as it is a base requirement. If you prefer, you can use
    [Forge](http://www.minecraftforge.net/forum/) instead;
    LibShapeDraw is compatible with either.
2.  Download and extract the jar for the latest release. You can rename it to a
    .zip if that helps.
3.  Patch the contents of the jar file into your `minecraft.jar`, being sure to
    remove the `META-INF` folders.

Utilities like [Magic Launcher](http://www.minecraftforum.net/topic/939149-magiclauncher/)
can automate this process. Highly recommended! Manually copying `.class` files
is for the birds.

Also, if you prefer to place the jar file in the `mods/` directory instead of
patching `minecraft.jar` directly, you can.

## Compatibility

LibShapeDraw was designed with compatibility in mind. It does not modify *any*
vanilla classes directly and therefore should be compatible with virtually every
mod. Forge is supported but *not* required.

## Troubleshooting

If Minecraft is crashing, check [Minecaft Game Client Support](http://www.minecraftforum.net/forum/151-minecraft-game-client-support/)
on minecraftforums.net as a first step.

If it's not, the next step is to verify that LibShapeDraw is installed properly.
Browse to your Minecraft directory and look for a `mods/LibShapeDraw`
subdirectory. There should be a file named `LibShapeDraw.log` in it; you can
open it in a text editor. If this file doesn't exist or has an old timestamp
then LibShapeDraw is not installed correctly. Try reinstalling.

If you're still stuck with a specific problem, see the contact section at the
bottom. Please have the crash report and `LibShapeDraw.log` handy.

----

# For Developers

LibShapeDraw is designed to be easy to use, both for devs and for players. These
are the design goals to that end:

 +  **Minimal dependencies.**  
    Either ModLoader or Forge is required to get up and running. That's all.

 +  **Maximal compatibility.**  
    LibShapeDraw does not modify the bytecode of *any* vanilla Minecraft class.
    You are free to modify Minecraft classes in your own mod if needed;
    LibShapeDraw will not interfere.

 +  **Unobtrusive.**  
    Pick and choose the components you want to use. LibShapeDraw is a toolkit
    for your mod to use. It is *not* a heavy DoEverythingThisWay framework.

 +  **Concise and clear.**  
    Convenience methods, fluent interfaces, etc. let you write less code to do
    more. That's what LibShapeDraw is all about.

 +  **Powerful.**  
    What good is an API that doesn't let you do cool stuff? Check the demos for
    some of the many possibilities.

 +  **Well-documented.**  
    The key to success for any API, really.

 +  **Open source.**  
    MIT-licensed and open to community feedback and patches.

## Using the LibShapeDraw API in your project

Quick version: Add the jar to your project's classpath, instantiate
`libshapedraw.LibShapeDraw` somewhere in your code, and go nuts. Use the
`libshapedraw.animation.trident.Timeline` class for animations.

For a gentler introduction, browse the demos: `src/test/java/mod_LSDDemo*.java`.

Other resources:

 +  Javadocs are available.
 +  See `README-Trident.md` for information about the built-in Trident animation
    library.
 +  See `README-contributing.md` for information about contributing to
    LibShapeDraw itself, including build instructions and a list of features
    planned for future releases.
 +  If you'd like additional guidance, check the contacts section below.

# Contact

This project's official GitHub repo is located at
[github.com/bencvt/LibShapeDraw](https://github.com/bencvt/LibShapeDraw).
Anyone is free to open an issue.

You can also try the [official LibShapeDraw thread on minecraftforum.net](http://www.minecraftforum.net/topic/1458931-libshapedraw/).

Finally, feel free to ping me (bencvt) in IRC. I'm usually idling in various
channels related to Minecraft development on esper.net and freenode.net.
