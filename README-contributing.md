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
    `minecraft.jar` should be vanilla with either ModLoader or Forge patched in.
3.  Create `lib/minecraft-deobf.jar` using MCP. Details in the next section.
4.  Change directory to `projects/all`.
5.  Run Maven. The output jars are located in `projects/*/target/`.

If you prefer to use an IDE, here's one recommended method:

1.  Follow the above steps first, making sure everything compiles.
2.  Install [Eclipse](http://www.eclipse.org/) and a
    [Maven integration plugin](http://wiki.eclipse.org/M2E).
3.  Import the Maven projects to your workspace.

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
LibShapeDraw. See the *How to add the LibShapeDraw jar to the classpath in MCP*
section in the main `README.md` for details.

So, the special dev jar (`LibShapeDraw-VERSION-dev.jar`) exists to support mod
devs using MCP. It's identical to the normal release except that:

 +  `mod_LibShapeDraw` links to deobfuscated identifiers in
    `minecraft-deobf.jar` rather than `minecraft.jar`. This allows devs to run
    their mod with the dev jar without having to reobfuscate and redeploy.
 +  The source code is included.
 +  The update check is disabled by default.

That's the story behind `minecraft-deobf.jar`. Here's how to generate it:

1.  Install MCP in another directory and decompile a `minecraft.jar` with
    ModLoader patched in. Or install MCP with the Forge sources. Either works.
2.  Open up a terminal window and change to the `bin/` subdirectory under the
    MCP directory.
3.  Type `jar cfv (repo-dir)/LibShapeDraw/lib/minecraft-deobf.jar .` (including
    the dot at the end, and substituting `(repo-dir)` as appropriate.)

## Tips and tricks

 +  You can enable automatic debug dumps and tweak a few other global settings
    by copying `libshapedraw/internal/default-settings.properties`  
    from the jar to the file system at  
    `(minecraft-dir)/mods/LibShapeDraw/settings.properties`. A future version
    of LibShapeDraw will probably auto-copy this file but for now the default
    settings are left in the archive.

 +  Minecraft's built-in profiler (accessible via `shift-F3`) includes a
    section named `root.gameRenderer.level.LibShapeDraw`. You can drill down to
    it using the number keys. It should come as no surprise that having a ton of
    Shapes on-screen simulatenously can cause a significant performance hit. The
    profiler can tell you exactly how much of a hit.

## Development roadmap

The official Minecraft API keeps getting pushed back; Mojang's current plan is
for it to be released in **Minecraft 1.5** after a rendering engine overhaul.

Obviously both the Minecraft API and the rendering engine overhaul are very
relevant to LibShapeDraw. The current plan is for **LibShapeDraw 2.0** to be
released along with Minecraft 1.5, with the following adjustments:

 +  If the Minecraft API includes a client-side plugin loading mechanism, make
    LibShapeDraw a *plugin* rather than a *mod*, to standardize and simplify
    installation (no more messing with .class files!) This would also remove the
    ModLoader/Forge requirement.

 +  If possible, LibShapeDraw's semantics will *not* change significantly, but
    the underlying implementation will likely have to change to match
    Minecraft's overhauled rendering engine.
    
    Mojang has not released any technical details yet, but the overhaul may in
    fact remove Minecraft's long-standing use of the OpenGL Fixed Function
    Pipeline, which was [deprecated](http://www.opengl.org/wiki/Legacy_OpenGL)
    several years ago by OpenGL. Using VBOs (Vertex Buffer Objects), etc.
    instead is usually a significant performance boost as data can persist in
    VRAM rather than being copied every render frame.
    
    The downside is that it's more code to set up all those vertex buffers. But
    if Minecraft itself takes that step, LibShapeDraw will follow.

 +  Any method marked as deprecated in LibShapeDraw 1.x will likely be removed
    in 2.0.

Planned documentation improvements:

 +  Automate Javadoc generation and publish them somewhere, probably GitHub.

 +  Maintain high (except for Trident) JUnit test code coverage.

 +  Expand the demos, perhaps including an example Forge mod as well. Demo
    videos would be nice to have as well so you don't actually have to install
    the demos jar to see it in action.

 +  Create a complete tutorial on how to start a fresh mod that uses
    LibShapeDraw, perhaps using a GitHub wiki.

Possible features, unprioritized (maybe before 2.0, maybe after, maybe never...
feedback and patches welcome):

 +  Optimization: where possible, bypass Minecraft's Tessellator and instead use
    OpenGL VBOs. This is a significant performance gain for Shapes with many
    vertices. This also doesn't have to wait for Minecraft's rendering engine
    overhaul (see above), which might not actually use VBOs after all.

 +  More built-in Shapes, perhaps with texture support as well.

 +  Expand the scope of the library to offer GUI/HUD tools. Like the animation
    component, these tools would be strictly optional; mods would be free to
    pick-and-choose the components they want to use. This will feature will
    likely be minimalistic until at least Minecraft 1.5, which overhauls the
    rendering process (see above).

 +  Add a plugin channel to allow servers to access the API on the client.
    The end user would be able to disable this feature if they'd rather not let
    the server draw shapes on their screen.
