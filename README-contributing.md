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
and use the prebuilt jar.

1.  Install [Maven](http://maven.apache.org/).
2.  Copy the contents of your Minecraft's `bin` directory to `lib`. Be sure to
    include the `natives` subdirectory; the test suite needs them.
    `minecraft.jar` should be vanilla with only ModLoader (or Forge) patched in.
3.  Run Maven.

That's all there is to it.

There is no need to install MCP as LibShapeDraw handles obfuscation on its own.
The main reason for this is that MCP really doesn't integrate well with Maven.
You can still use MCP along with LibShapeDraw in your own mod if you prefer; it
decompiles just fine.

There is no need to use Forge or reference any Forge classes when compiling
either. ModLoader is enough. You *can* compile against a `lib/minecraft.jar`
with Forge patched in instead of ModLoader, however.

If you prefer to use an IDE, here's one recommended method:

1.  Follow the above steps first, making sure everything compiles.
2.  Install [Eclipse](http://www.eclipse.org/) and a
    [Maven integration plugin](http://wiki.eclipse.org/M2E).
3.  Import the Maven project to your workspace.

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

 +  Mavenize Javadoc generation and publish them somewhere, probably GitHub.

 +  Maintain high JUnit test code coverage.

 +  Expand the demos and make them easier to run, perhaps publishing a separate
    `LibShapeDraw-VERSION-demos.jar` containing just the demos.

 +  Create a tutorial on how to start a fresh mod that uses LibShapeDraw.

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
