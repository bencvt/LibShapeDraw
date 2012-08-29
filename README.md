# For Players

LibShapeDraw is a Minecraft client mod that is required by other mods.
It doesn't do anything on its own. Rather, it provides a flexible set of
drawing tools for those other mods to use.

## Installation

Installing this mod works exactly the same as any other Minecraft client mod.

1.  Make sure [ModLoader](http://www.minecraftforum.net/topic/75440-/) is
    installed as it is a base requirement.
2.  Download and extract the jar for the latest release. You can rename it to a
    .zip if that helps.
3.  Patch the contents of the jar file into your `minecraft.jar`, being sure to
    remove the `META-INF` folders.

Utilities like [Magic Launcher](http://www.minecraftforum.net/topic/939149-/)
can automate this process. Highly recommended! Manually copying `.class` files
is for the birds.

## Troubleshooting

If Minecraft is crashing, check [Minecaft Game Client Support](http://www.minecraftforum.net/forum/151-minecraft-game-client-support/)
on minecraftforums.net as a first step.

If it's not, the next step is to verify that LibShapeDraw is installed properly.
Browse to your Minecraft directory and look for a `mods/LibShapeDraw`
subdirectory. There should be a file named `LibShapeDraw.log` in it; you can
open it in a text editor. If this file doesn't exist or has an old timestamp
then LibShapeDraw is not installed correctly. Try reinstalling.

If you're still stuck with a specific problem, see the contact section at the
bottom. Please have the crash report handy.

# For Developers

## Using the LibShapeDraw API in your project

Quick version: Add the jar to your project's classpath, instantiate
`libshapedraw.LibShapeDraw` somewhere in your code, and go nuts.

For a gentler introduction, browse the demos: `src/test/java/mod_LSDDemo*.java`.

Javadocs are also available.

If you'd like additional guidance, check the contacts section at the bottom.

## Building LibShapeDraw from source

If you just want to use the API in your own mod, feel free to skip this section
and use the prebuilt jar.

1.  Install [Maven](http://maven.apache.org/).
2.  Copy the contents of your Minecraft's `bin` directory to `lib`.  
    `minecraft.jar` should be vanilla with only ModLoader patched in.
3.  Run Maven.

That's all there is to it. If you prefer to use an IDE, here's one way:

1.  Follow the above steps first, making sure everything compiles.
2.  Install [Eclipse](http://www.eclipse.org/) and a
    [Maven integration plugin](http://wiki.eclipse.org/M2E).
3.  Import the Maven project to your workspace.

## Other tips

 +  You can enable debug dumps and tweak a few other global settings by copying  
    `libshapedraw/internal/default-settings.properties`  
    from the jar to the file system at  
    `<minecraft dir>/mods/LibShapeDraw/settings.properties`.

 +  If you're not using MCP or another system that provides a handy way to
    launch Minecraft in a dev environment (i.e., insulated from your actual
    Minecraft installation), see
    `src/test/java/libshapedraw/launcher/StartMinecraftDev`.

## Development roadmap

Planned for 1.1 or later:

 +  More built-in Shapes, perhaps with texture support as well.
 +  Mavenize Javadoc generation and publish them somewhere, probably GitHub.
 +  Improve JUnit test coverage.
 +  Expand the demos.
 +  Trident animation library integration.

### About Trident

Trident is pretty much the only mature open-source Java animation library out
there that isn't part of a much larger framework.

It's also inactive as of August 2010, which actually isn't as bad as it sounds
because the project was already fairly mature and robust. And hey, it's
open-source; nothing's stopping anyone from running with a fork should the need
arise.

Limited documentation, full source code, and releases are available in several
places: the [Trident author's blog](http://www.pushing-pixels.org/category/trident),
the [kenai.com project](http://kenai.com/projects/trident/pages/Home), and
the [GitHub snapshot](https://github.com/kirillcool/trident).

For the sake of simplicity, LibShapeDraw will likely include the Trident classes
in its release jar.

## Contributing

All reasonable pull requests are considered; feel free to fork and PR.

If you'd like to discuss potential major changes, you can open an issue on
GitHub or take it to IRC.

# Contact

This project's official GitHub repo is located at
[github.com/bencvt/LibShapeDraw](https://github.com/bencvt/LibShapeDraw).
Anyone is free to open an issue.

You can also try the [official LibShapeDraw thread on minecraftforum.net](http://www.minecraftforum.net/topic/???-).

Finally, feel free to ping me (bencvt) in IRC. I'm usually idling in various
channels related to Minecraft development on esper.net and freenode.net.
