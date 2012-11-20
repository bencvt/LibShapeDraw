# For Players

LibShapeDraw is a Minecraft client mod that is required by other mods.
It doesn't do anything on its own. Rather, it provides a set of flexible and
powerful drawing and animation tools for those other mods to use.

See the [official LibShapeDraw thread on minecraftforum.net](http://www.minecraftforum.net/topic/1458931-libshapedraw/)
for some screenshots and videos of what sort visual effects are possible.

## Installation

Installing this mod works exactly the same as any other Minecraft client mod.

1.  Make sure that either
    [ModLoader](http://www.minecraftforum.net/topic/75440-modloader/)
    or [Forge](http://www.minecraftforge.net/forum/) is installed.
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
mod that works with ModLoader or Forge.

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

If your mod could use any of the following things, LibShapeDraw is the library
for you:

 +  The ability to create arbitrary shapes and **draw in the game world itself**
    *without* having to mess with OpenGL contexts, finding a render hook, or
    other tedious details.

 +  The ability to **smoothly animate** shapes (and anything else, really) using
    the easy-to-use Trident animation library built-in to LibShapeDraw.

 +  Vector3 and Color classes, full of well-documented and throughly-tested
    **convenience methods**. Want to calculate the pitch and yaw angles between
    two 3-dimensional points in a couple lines of code? No problem.

## Design goals

LibShapeDraw is designed to be easy to use, both for devs and for players. These
are the design goals to that end:

 +  **Minimal dependencies.** Either ModLoader or Forge is required to get up
    and running. That's all.

 +  **Maximal compatibility.** LibShapeDraw does not modify the bytecode of
    *any* vanilla Minecraft class. You are free to modify Minecraft classes in
    your own mod if needed; LibShapeDraw will not interfere.

 +  **Unobtrusive.** Pick and choose the components you want to use.
    LibShapeDraw is a toolkit for your mod to use. It is *not* a heavy
    DoEverythingThisWay framework.

 +  **Powerful.** What good is an API that doesn't let you do cool stuff? Check
    the demos for some of the many possibilities.

 +  **Concise and clear.** Convenience methods, fluent interfaces, etc. let you
    write less code to do more. That's what LibShapeDraw is all about.

 +  **Well-documented.** The key to success for any API, really.

 +  **Throughly tested.** A full JUnit test suite to help prevent bugs.

 +  **Open source.** MIT-licensed and open to community feedback and patches.

Enough high-level overview stuff. Let's dive in and actually use the API:

## Using the LibShapeDraw API in your project

Add the jar to your project's classpath and instantiate `LibShapeDraw` somewhere
in your code. Create and add some `Shape`s for the API instance to render.

Each Shape type is defined using classes like `Color`, `Vector3`, and
`ShapeTransform`. You can easily animate a shape by calling `animateStart` and
`animateStop` on these classes.

Plenty of sample code is available; see the *Documentation* section below.
Here's a quick example:

        // Create a 1x1x1 wireframe box at x=0, y=63 (sea level), z=0.
        this.libShapeDraw = new LibShapeDraw();
        Vector3 pointA = new Vector3(0, 63, 0);
        Vector3 pointB = new Vector3(1, 64, 1);
        WireframeCuboid shape = new WireframeCuboid(pointA, pointB);
        Color lineColor = Color.CYAN.copy();
        shape.setLineStyle(lineColor, 2.0F, false);
        libShapeDraw.addShape(shape);
        
        // Reposition it up a dozen blocks and make it 2x1x1 instead of 1x1x1.
        pointA.addY(12.0);
        pointB.addY(12.0).addX(1.0);
        
        // Animate it! Smoothly change color from cyan to green and back every 5
        // seconds, and also spin in place, rotating once every 7 seconds.
        lineColor.animateStartLoop(Color.GREEN, true, 5000);
        ShapeRotate rotate = new ShapeRotate(0.0, Axis.Y);
        shape.addTransform(rotate);
        rotate.animateStartLoop(360.0, false, 7000);

## How to add the LibShapeDraw jar to the classpath in MCP

[Minecraft Coder Pack (MCP)](http://mcp.ocean-labs.de/index.php/MCP_Releases)
is an excellent tool for creating mods, letting you work with deobfuscated
Minecraft code. Using external libraries (such as LibShapeDraw) involves a few
extra steps:

1.  **Get a copy of a LibShapeDraw release jar.** You can use either the normal
    release or the special dev release (named `LibShapeDraw-VERSION-dev.jar`).
    Check the [downloads list](https://github.com/bencvt/LibShapeDraw/downloads)
    for the latest.
    
    The dev release is recommended: either release type will work for
    *compiling* your mod, but the dev release also lets you *test/debug* your
    MCP mod without doing a full reobfuscate/deploy.

2.  **Copy the LibShapeDraw jar to `jars/bin`.**

3.  **Edit `conf/mcp.cfg`.** Scroll down to the `[RECOMPILE]` section. There is
    a property named `ClassPathClient`; this is where we will add a reference to
    the LibShapeDraw jar. Add `,%(DirJars)s/bin/LibShapeDraw-VERSION-dev.jar`
    (changing `VERSION` as appropriate) to the end of the line.

4.  **If you prefer to use an IDE, add the LibShapeDraw jar to the Eclipse
    project's build path.** Go to the Project Explorer pane and expand
    `jars/bin`. Right-click the LibShapeDraw jar, Build Path, Add to Build Path.
    
    The dev release also includes the source code for convenience, giving you
    easy access to the Javadocs in your IDE.

## Documentation

 +  [Javadocs](http://bencvt.github.com/LibShapeDraw/apidocs/index.html) are
    available.

 +  [Browse the demos](https://github.com/bencvt/LibShapeDraw/tree/master/projects/demos/src/main/java/libshapedraw/demos),
    located in `projects/demos`. To see the demos in action, you can
    [download](https://github.com/bencvt/LibShapeDraw/downloads) the pre-built
    demos jar and install it like any other mod.

 +  See [README-Trident.md](https://github.com/bencvt/LibShapeDraw/blob/master/README-Trident.md)
    for information about the built-in Trident animation library.

 +  See [README-contributing.md](https://github.com/bencvt/LibShapeDraw/blob/master/README-contributing.md)
    for information about contributing to LibShapeDraw itself, including build
    instructions and a list of features planned for future releases.

 +  If you'd like additional guidance, check the contacts section below.

## Contact

This project's official GitHub repo is located at
[github.com/bencvt/LibShapeDraw](https://github.com/bencvt/LibShapeDraw).
Anyone is free to open an issue.

You can also try the [official LibShapeDraw thread on minecraftforum.net](http://www.minecraftforum.net/topic/1458931-libshapedraw/).

Finally, feel free to ping me (bencvt) in IRC. I'm often lurking in various
channels related to Minecraft development on esper.net and freenode.net.
