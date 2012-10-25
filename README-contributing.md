## Contributing to LibShapeDraw

If you're an open-source Java developer looking to improve LibShapeDraw,
welcome! All reasonable pull requests are considered; feel free to fork and PR.

If you'd like to discuss potential major changes, you can open an issue on
GitHub or take it to IRC. (See the Contact section in the main `README.md`.)

Not a developer? No worries: any bug reports, feature requests, and general
comments are much appreciated. Please feel free to open an issue on GitHub.

Also if you're handy with graphic design, please contact me (bencvt)!
LibShapeDraw could use a better logo. :)

## Building LibShapeDraw from source

If you just want to use the API in your own mod, feel free to skip this section
and use a prebuilt jar.

1.  Install [Maven](http://maven.apache.org/).
2.  Copy the contents of your Minecraft's `bin` directory to `lib`. Be sure to
    include the `natives` subdirectory; the test suite needs them.
    `minecraft.jar` should be vanilla with only ModLoader (or Forge) patched in.
3.  Create `lib/minecraft-deobf.jar` using MCP. Details in the next section.
4.  Run Maven; the jars are located in `projects/*/target/`.

That's all there is to it.

There is no need to use Forge or reference any Forge classes when compiling
either. ModLoader is enough. You *can* compile against a `lib/minecraft.jar`
with Forge patched in instead of ModLoader, however.

If you prefer to use an IDE, here's one recommended method:

1.  Follow the above steps first, making sure everything compiles.
2.  Install [Eclipse](http://www.eclipse.org/) and a
    [Maven integration plugin](http://wiki.eclipse.org/M2E).
3.  Import the Maven project to your workspace.

### Creating a `minecraft-deobf.jar` for `LibShapeDraw-VERSION-dev.jar`

First of all, what's the point of these jars? Short answer: to make developers'
lives easier.

Long answer: Writing a mod using Minecraft code deobfuscated by MCP works fine.
Adding the LibShapeDraw jar to the classpath works fine too; you'll be able to
compile without issue. However, trying to debug such a mod is problematic.
Obfuscation and deobfuscation won't mix.

By necessity, the main release jar for LibShapeDraw references obfuscated
Minecraft code. As a side note, LibShapeDraw does not use MCP directly, instead
handling obfuscation on its own. The main reason for this is that MCP really
doesn't integrate well with Maven and Git.

However, mod developers can (and generally should) still use MCP along with
LibShapeDraw. The *How to add the LibShapeDraw jar to the classpath in MCP*
section in the main `README.md` has the details.

So, the special dev jar (`LibShapeDraw-VERSION-dev.jar`) exists to support mod
devs using MCP. It's identical to the normal release except that:

 +  `mod_LibShapeDraw` links to deobfuscated identifiers in
    `minecraft-deobf.jar` rather than `minecraft.jar`. This allows devs to run
    their mod with the dev jar without having to reobfuscate and redeploy.
 +  The update check is disabled by default.

That's the story behind `minecraft-deobf.jar`. Here's how to generate it:

1.  Install MCP in another directory and decompile a `minecraft.jar` with
    ModLoader patched in. Or install MCP with the Forge sources. Either works.
2.  Open up a terminal window and change to the `bin/` subdirectory under the
    MCP directory.
3.  Type `jar cfv (repo-dir)/LibShapeDraw/lib/minecraft-deobf.jar .` (including
    the dot at the end, and substituting `(repo-dir)` as appropriate.)

## Tips and tricks

 +  You can enable debug dumps and tweak a few other global settings by copying  
    `libshapedraw/internal/default-settings.properties`  
    from the jar to the file system at  
    `<minecraft dir>/mods/LibShapeDraw/settings.properties`.

 +  If you're not using MCP or another system that provides a handy way to
    launch Minecraft in a dev environment (i.e., insulated from your actual
    Minecraft installation), see
    `src/test/java/launcher/StartMinecraftDev`.

## Development roadmap

The official Minecraft API keeps getting pushed back; Mojang's current plan is
for it to be released in **Minecraft 1.5** after a rendering engine overhaul.

Obviously both the Minecraft API and the rendering engine overhaul are very
relevant to LibShapeDraw. The current plan is for **LibShapeDraw 2.0** to be
released along with Minecraft 1.5, with the following adjustments:

 +  Make LibShapeDraw a *plugin* rather than a *mod*, to standardize and
    simplify installation (no more messing with .class files!) This will also
    remove the ModLoader/Forge requirement.

 +  If possible, LibShapeDraw's semantics will *not* change significantly, but
    the underlying implementation will certainly have to change to match
    Minecraft's overhauled rendering engine. More details to come.

Planned documentation improvements:

 +  Automate Javadoc generation and publish them somewhere, probably GitHub.

 +  Maintain high JUnit test code coverage.

 +  Expand the demos and make them easier to run, perhaps publishing a separate
    `LibShapeDraw-VERSION-demos.jar` containing just the demos.

 +  Automate creation of the dev release jar (`LibShapeDraw-VERSION-deobf.jar`).

 +  Create a complete tutorial on how to start a fresh mod that uses
    LibShapeDraw.

Possible features, unprioritized (maybe before 2.0, maybe after, maybe never...
feedback and patches welcome):

 +  More built-in Shapes, perhaps with texture support as well.

 +  Expand the scope of the library to offer GUI/HUD tools. Like the animation
    component, these tools would be strictly optional; mods would be free to
    pick-and-choose the components they want to use. This will feature will
    likely be minimalistic until at least Minecraft 1.5, which overhauls the
    rendering process (see above).

 +  Make the API thread-safe if it's possible to do so without a major
    performance hit. (And if mods actually have a need to modify shapes/etc.
    from a different thread.)

 +  Add a plugin channel to allow servers to access the API on the client.
    The end user would be able to disable this feature if they'd rather not let
    the server draw shapes on their screen.
