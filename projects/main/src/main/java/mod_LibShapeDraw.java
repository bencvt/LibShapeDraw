// THIS SOURCE FILE WAS AUTOMATICALLY GENERATED. DO NOT MANUALLY EDIT.
// Edit projects/dev/src/main/java/net/minecraft/src/mod_LibShapeDraw.java
// and then run the projects/dev/src/main/python/obfuscate.py script.

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import libshapedraw.ApiInfo;
import libshapedraw.MinecraftAccess;
import libshapedraw.internal.LSDController;
import libshapedraw.internal.LSDUtil;
import libshapedraw.internal.LSDUtil.NullList;
import libshapedraw.internal.LSDUtil.NullMap;
import libshapedraw.primitive.ReadonlyVector3;
import libshapedraw.primitive.Vector3;
import net.minecraft.client.Minecraft;

/**
 * Internal class. Mods using the LibShapeDraw API can safely ignore this.
 * Rather, instantiate {@link libshapedraw.LibShapeDraw}.
 * <p>
 * This is a ModLoader mod (also compatible with Forge/FML) that links itself
 * to the internal API Controller, providing it data and events from Minecraft.
 * This class does the bare minimum of processing before passing these off to
 * the controller. I.e., this class is a thin wrapper for Minecraft used by
 * LibShapeDraw.
 * <p>
 * As a wrapper, all direct interaction with Minecraft objects passes through
 * this class, making the LibShapeDraw API itself clean and free of obfuscated
 * code. (There is a single exception: LSDModDirectory.getMinecraftDir.)
 */
public class mod_LibShapeDraw extends BaseMod implements MinecraftAccess {
    /**
     * Install our render hook by inserting a proxy for Minecraft.mcProfiler.
     * <p>
     * The long-awaited official Minecraft API will hopefully provide standard
     * entry points for client mods (like this one!) that require rendering
     * hooks. Until then, we have to do some hackish stuff to add our hook.
     * <p>
     * Option 1 is the naive, quick-and-dirty method: patch or proxy the
     * EntityRender class. However this class is already being modified by many
     * mods, including OptiFine, ModLoader, and Forge. Introducing yet another
     * mutually incompatible mod is a poor choice. Compatibility is a key goal
     * of LibShapeDraw.
     * <p>
     * Option 2 is to use Forge's hooks. This is also not an acceptable option:
     * not everyone uses Forge. LibShapeDraw supports Forge but does not
     * require it.
     * <p>
     * Option 3 is to register a fake entity and add our render hook to it.
     * This is a valid, highly-compatible approach, used successfully by
     * several mods (including LibShapeDraw v1.0). However this has a key
     * drawback: entities are rendered before water, clouds, and other
     * elements. This can result in ugly graphical glitches when rendering
     * semi-transparent shapes near water.
     * <p>
     * Option 4, which is what this class implements, is an even more egregious
     * hack than option 1 or 3. The Profiler class is of course intended for
     * debugging, gathering metrics on how long it takes to render each
     * element.
     * <p>
     * As it happens, the point at which we want to insert our hook occurs just
     * before the player's hand is rendered. The profiler's context gets
     * switched at this point, giving us our hook! Furthermore, we're able to
     * proxy the Profiler class instead of modifying it directly, fulfilling
     * another one of LibShapeDraw's goals: no bytecode modification of vanilla
     * classes.
     * <p>
     * This doesn't guarantee compatibility with every mod: If another mod is
     * trying to proxy the Profiler class as well for some reason, Bad Things
     * might happen. If in EntityRender.renderWorld the call to
     * Profiler.endStartSection("hand") is removed by another mod patching that
     * class, Bad Things will definitely happen. We can and do check for these
     * cases, however.
     * <p>
     * Anyway, this method is a roundabout hack, but it works. It may very well
     * break at some point when the rendering engine is overhauled in Minecraft
     * 1.5, but this is also when the official Minecraft API is scheduled to
     * finally be released.
     * <p>
     * The sooner the better.
     */
    // obf: Profiler
    public class Proxy extends kh {
        /**
         * Keep a reference to the old Profiler if we detect that another mod
         * is also proxying it. Play nice with others!
         */
        // obf: Profiler
        protected kh orig;

        @Override
        // obf: Profiler.startSection
        public void a(String sectionName) {
            // obf: Profiler.startSection
            super.a(sectionName);
            if (orig != null) {
                // obf: Profiler.startSection
                orig.a(sectionName);
            }
        }

        @Override
        // obf: Profiler.endSection
        public void b() {
            // obf: Profiler.endSection
            super.b();
            if (orig != null) {
                // obf: Profiler.endSection
                orig.b();
            }
        }

        @Override
        // obf: Profiler.endStartSection
        public void c(String sectionName) {
            if (sectionName.equals("hand")) {
                // obf: Profiler.endStartSection
                super.c("LibShapeDraw"); // we'll take the blame :-)
                render();
            }
            // obf: Profiler.endStartSection
            super.c(sectionName);
            if (orig != null) {
                // obf: Profiler.endStartSection
                orig.c(sectionName);
            }
        }
    }

    private Minecraft minecraft;
    // obf: Timer
    private asr timer;
    private Proxy proxy;
    private LSDController controller;
    private boolean renderHeartbeat;
    private boolean renderHeartbroken;
    private Object curWorld;
    // obf: EntityClientPlayerMP
    private ayk curPlayer;
    private Integer curDimension;

    public mod_LibShapeDraw() {
        controller = LSDController.getInstance();
        controller.initialize(this);
    }

    @Override
    public String getName() {
        return ApiInfo.getName();
    }

    @Override
    public String getVersion() {
        return ApiInfo.getVersion();
    }

    @Override
    public String getPriorities() {
        // Request that this mod gets loaded last. Ideally, we want to be the
        // last mod to potentially modify Minecraft.mcProfiler.
        return "after:*";
    }

    @Override
    public void load() {
        // Do nothing; wait until modsLoaded.
    }

    @Override
    public void modsLoaded() {
        // obf: Minecraft.getMinecraft
        minecraft = Minecraft.x();
        // Get a reference to Minecraft's timer so we can get the partial
        // tick time for rendering (it's not passed to the profiler directly).
        // 
        // There's only one Timer field declared by Minecraft so it's safe to
        // look it up by type.
        // obf: Timer
        timer = (asr) LSDUtil.getFieldValue(LSDUtil.getFieldByType(Minecraft.class, asr.class, 0), minecraft);

        installRenderHook();
        ModLoader.setInGameHook(this, true, true); // game ticks only, not every render frame.
        LSDController.getLog().info(getClass().getName() + " loaded");
    }

    /**
     * Use reflection to install the profiler proxy class, overwriting
     * Minecraft.mcProfiler.
     */
    private void installRenderHook() {
        final Class<? super Proxy> vanillaClass = Proxy.class.getSuperclass();
        proxy = new Proxy();
        // There's only one Profiler field declared by Minecraft so it's safe
        // to look it up by type.
        final Field fp = LSDUtil.getFieldByType(Minecraft.class, vanillaClass, 0);
        // obf: Profiler
        proxy.orig = (kh) LSDUtil.getFieldValue(fp, minecraft);
        final String origClass = proxy.orig.getClass().getName();
        LSDController.getLog().info(
                "installing render hook using profiler proxy, replacing " + origClass);
        LSDUtil.setFinalField(fp, minecraft, proxy);

        // Copy all vanilla-defined field values from the original profiler to
        // the new proxy.
        for (Field f : vanillaClass.getDeclaredFields()) {
            f.setAccessible(true);
            Object origValue = LSDUtil.getFieldValue(f, proxy.orig);
            LSDUtil.setFinalField(f, proxy, origValue);
            LSDController.getLog().fine("copied profiler field " + f + " = " + String.valueOf(origValue));
            // "Neuter" the original profiler by changing its vanilla-defined
            // reference types to new dummy instances.
            if (f.getType() == List.class) {
                LSDUtil.setFinalField(f, proxy.orig, new NullList());
            } else if (f.getType() == Map.class) {
                LSDUtil.setFinalField(f, proxy.orig, new NullMap());
            }
        }

        if (proxy.orig.getClass() == vanillaClass) {
            // No need to keep a reference to the original profiler.
            proxy.orig = null;
        } else {
            // We overwrote some other mod's hook, so keep the reference to the
            // other mod's proxy. This will ensure that the other mod still
            // receives its expected events.
            // 
            // Log a (hopefully benign) warning message if we don't recognize
            // the other proxy class.
            if (!origClass.equals("com.mumfrey.liteloader.core.HookProfiler")) {
                LSDController.getLog().warning(
                        "possible mod incompatibility detected: replaced unknown profiler proxy class " + origClass);
            }
        }
    }

    @Override
    // obf: NetClientHandler
    public void clientConnect(axz netClientHandler) {
        LSDController.getLog().info(getClass().getName() + " new server connection");
        curWorld = null;
        curPlayer = null;
        curDimension = null;
    }

    @Override
    public boolean onTickInGame(float partialTick, Minecraft minecraft) {
        ReadonlyVector3 playerCoords = getPlayerCoords();

        // obf: Minecraft.theWorld, Minecraft.thePlayer
        if (curWorld != minecraft.e || curPlayer != minecraft.g) {
            // obf: Minecraft.theWorld
            curWorld = minecraft.e;
            // obf: Minecraft.thePlayer
            curPlayer = minecraft.g; 

            // Dispatch respawn event to Controller.
            // obf: Entity.dimension
            int newDimension = curPlayer.ap;
            controller.respawn(playerCoords,
                    curDimension == null,
                    curDimension == null || curDimension != newDimension);
            curDimension = newDimension;
        }

        // Dispatch game tick event to Controller.
        controller.gameTick(playerCoords);

        // Make sure our render hook is still working.
        // obf: Minecraft.skipRenderWorld
        if (!renderHeartbeat && !renderHeartbroken && !minecraft.w) {
            // Despite our best efforts when installing the profiler proxy,
            // some other mod probably overwrote our hook without providing a
            // compatibility layer like we do. :-(
            // 
            // Attempting to reinstall our hook would be futile: chances are it
            // would simply be overwritten again by the next tick. Rather than
            // participating in an inter-mod slap fight, back down and log an
            // error message. No need to crash Minecraft.
            Object newProxy = LSDUtil.getFieldValue(
                    LSDUtil.getFieldByType(Minecraft.class, Proxy.class.getSuperclass(), 0), minecraft);
            String message = "mod incompatibility detected: render hook not working! Minecraft.mcProfiler is " +
                    (newProxy == null ? "null" : newProxy.getClass().getName());
            LSDController.getLog().warning(message);
            sendChatMessage("\u00a7c[" + getName() + "] " + message);
            renderHeartbroken = true; // don't spam log
        }
        renderHeartbeat = false;

        return true;
    }

    /** Dispatch render event to Controller. */
    protected void render() {
        // obf: Minecraft.gameSettings, GameSettings.hideGUI, Minecraft.currentScreen
        controller.render(getPlayerCoords(), minecraft.y.S && minecraft.r == null);
        renderHeartbeat = true;
    }

    /**
     * Get the player's current coordinates, adjusted for movement that occurs
     * between game ticks.
     */
    private ReadonlyVector3 getPlayerCoords() {
        if (curPlayer == null) {
            return Vector3.ZEROS;
        }
        float partialTick = getPartialTick();
        return new Vector3(
                // obf: Entity.prevPosX, Entity.posX
                curPlayer.q + partialTick*(curPlayer.t - curPlayer.q),
                // obf: Entity.prevPosY, Entity.posY
                curPlayer.r + partialTick*(curPlayer.u - curPlayer.r),
                // obf: Entity.prevPosZ, Entity.posZ
                curPlayer.s + partialTick*(curPlayer.v - curPlayer.s));
    }

    // ====
    // MinecraftAccess implementation
    // ====

    @Override
    public MinecraftAccess startDrawing(int mode) {
        // obf: Tessellator, Tessellator.instance, Tessellator.startDrawing
        bao.a.b(mode);
        return this;
    }

    @Override
    public MinecraftAccess addVertex(double x, double y, double z) {
        // obf: Tessellator, Tessellator.instance, Tessellator.addVertex
        bao.a.a(x, y, z);
        return this;
    }

    @Override
    public MinecraftAccess addVertex(ReadonlyVector3 coords) {
        // obf: Tessellator, Tessellator.instance, Tessellator.addVertex
        bao.a.a(coords.getX(), coords.getY(), coords.getZ());
        return this;
    }

    @Override
    public MinecraftAccess finishDrawing() {
        // obf: Tessellator, Tessellator.instance, Tessellator.draw
        bao.a.a();
        return this;
    }

    @Override
    public MinecraftAccess enableStandardItemLighting() {
        // obf: RenderHelper, RenderHelper.enableStandardItemLighting
        aro.b();
        return this;
    }

    @Override
    public MinecraftAccess sendChatMessage(String message) {
        boolean visible = chatWindowExists();
        LSDController.getLog().info("sendChatMessage visible=" + visible + " message=" + message);
        if (visible) {
            // obf: Minecraft.ingameGUI, GuiIngame.getChatGUI, GuiNewChat.printChatMessage
            minecraft.v.b().a(message);
        }
        return this;
    }

    @Override
    public boolean chatWindowExists() {
        // obf: Minecraft.ingameGUI, GuiIngame.getChatGUI
        return minecraft != null && minecraft.v != null && minecraft.v.b() != null;
    }

    @Override
    public float getPartialTick() {
        // obf: Timer.renderPartialTicks
        return timer == null ? 0.0F : timer.c;
    }


    @Override
    public MinecraftAccess profilerStartSection(String sectionName) {
        if (proxy != null) {
            // obf: Profiler.startSection
            proxy.a(sectionName);
        }
        return this;
    }

    @Override
    public MinecraftAccess profilerEndSection() {
        if (proxy != null) {
            // obf: Profiler.endSection
            proxy.b();
        }
        return this;
    }

    @Override
    public MinecraftAccess profilerEndStartSection(String sectionName) {
        if (proxy != null) {
            // obf: Profiler.endStartSection
            proxy.c(sectionName);
        }
        return this;
    }
}
