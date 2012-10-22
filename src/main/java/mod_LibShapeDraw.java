import java.lang.reflect.Field;

import libshapedraw.ApiInfo;
import libshapedraw.MinecraftAccess;
import libshapedraw.internal.LSDController;
import libshapedraw.internal.LSDUtil;
import libshapedraw.primitive.ReadonlyVector3;
import libshapedraw.primitive.Vector3;
import net.minecraft.client.Minecraft;

/**
 * Internal class. Client code using the API should ignore this.
 * Rather, instantiate LibShapeDraw.
 * <p>
 * This is a ModLoader mod that links itself to the internal API Controller,
 * providing it data and events from Minecraft. This class does the bare
 * minimum of processing before passing these off to the Controller. I.e., this
 * class is a thin wrapper for Minecraft used by LibShapeDraw.
 * <p>
 * As a wrapper, all direct interaction with Minecraft objects passes through
 * this class, making the LibShapeDraw API itself clean and free of obfuscated
 * code. (There is a single exception: LSDModDirectory.DIRECTORY.)
 */
public class mod_LibShapeDraw extends BaseMod implements MinecraftAccess {
    /**
     * The official Minecraft API will eventually provide standard entry points
     * for adding rendering hooks, but in the meantime we have to do some
     * hackish stuff to add our hook.
     * <p>
     * Option 1 is the naive, quick-and-dirty method: patch or proxy the
     * EntityRender class. However this class is already being modified by many
     * mods, including Optifine, ModLoader, and Forge. Introducing yet another
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
     * shapes near water.
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
     * Anyway, this method is a roundabout hack, but it works. It will almost
     * certainly break at some point when the rendering engine is overhauled in
     * Minecraft 1.5, but this is also when the official Minecraft API is
     * scheduled to finally be released.
     * <p>
     * The sooner the better.
     */
    // obf: Profiler
    public class ProfilerProxy extends jx {
        // obf: endStartSection
        @Override
        public void c(String sectionName) {
            if (sectionName.equals("hand")) {
                float partialTick = getPartialTick();
                // obf: Minecraft.gameSettings, GameSettings.hideGUI, Minecraft.currentScreen
                controller.render(getPlayerCoords(partialTick), minecraft.y.R && minecraft.r == null);
                renderHeartbeat = true;
            }
            super.c(sectionName);
        }
    }

    private Minecraft minecraft;
    private ari timer; // obf: Timer
    private LSDController controller;
    private boolean renderHeartbeat;
    private boolean renderHeartbroken;
    private Object curWorld;
    private axb curPlayer; // obf: EntityClientPlayerMP
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
    public void load() {
        // obf: Minecraft.getMinecraft
        minecraft = Minecraft.x();
        // Get a reference to Minecraft's timer so we can get the partial
        // tick time for rendering (it's not passed to the profiler directly).
        // obf: Timer
        timer = (ari) LSDUtil.getFieldValue(LSDUtil.getFieldByType(Minecraft.class, ari.class, 0), minecraft);

        installRenderHook();
        ModLoader.setInGameHook(this, true, true); // game ticks only, not every render frame.
        LSDController.getLog().info(getClass().getName() + " loaded");
    }

    /** Use reflection to install the profiler proxy class. */
    private void installRenderHook() {
        Class<? super ProfilerProxy> profilerClass = ProfilerProxy.class.getSuperclass();
        Field fieldProfiler = LSDUtil.getFieldByType(Minecraft.class, profilerClass, 0);
        Object profilerOrig = LSDUtil.getFieldValue(fieldProfiler, minecraft);
        if (profilerOrig.getClass() != profilerClass) {
            // We probably overwrote some other mod's hook. :-(
            LSDController.getLog().warning("mod incompatibility detected: profiler already proxied!");
        }
        ProfilerProxy profilerProxy = new ProfilerProxy();
        LSDUtil.setFinalField(fieldProfiler, minecraft, profilerProxy);

        // Copy all field values from origProfiler to newProfiler
        for (Field f : profilerClass.getDeclaredFields()) {
            f.setAccessible(true);
            Object origValue = LSDUtil.getFieldValue(f, profilerOrig);
            LSDUtil.setFinalField(f, profilerProxy, origValue);
            LSDController.getLog().fine("copied profiler field " +
                    f + " = " + String.valueOf(origValue));
        }
    }

    // obf: NetClientHandler
    @Override
    public void clientConnect(awq netClientHandler) {
        LSDController.getLog().info(getClass().getName() + " new server connection");
        curWorld = null;
        curPlayer = null;
        curDimension = null;
    }

    @Override
    public boolean onTickInGame(float partialTick, Minecraft minecraft) {
        ReadonlyVector3 playerCoords = getPlayerCoords(partialTick);

        // obf: Minecraft.theWorld, Minecraft.thePlayer
        if (curWorld != minecraft.e || curPlayer != minecraft.g) {
            curWorld = minecraft.e;
            curPlayer = minecraft.g; 

            // Dispatch respawn event to Controller.
            int newDimension = curPlayer.ap; // obf: Entity.dimension
            controller.respawn(playerCoords,
                    curDimension == null,
                    curDimension == null || curDimension != newDimension);
            curDimension = newDimension;
        }

        // Dispatch game tick event to Controller.
        controller.gameTick(playerCoords);

        // Make sure our render hook is still working.
        // obf: skipRenderWorld
        if (!renderHeartbeat && !renderHeartbroken && !minecraft.w) {
            // Some other mod probably overwrote our hook. :-(
            LSDController.getLog().warning("mod incompatibility detected: render hook not working!");
            renderHeartbroken = true; // don't spam log
        }
        renderHeartbeat = false;

        return true;
    }

    /**
     * Get the player's current coordinates, adjusted for movement that occurs
     * between game ticks.
     */
    private ReadonlyVector3 getPlayerCoords(float partialTick) {
        if (curPlayer == null) {
            return Vector3.ZEROS;
        }
        // obf: Entity.prevPosX, Entity.prevPosY, Entity.prevPosZ, Entity.posX, Entity.posY, Entity.posZ
        return new Vector3(
                curPlayer.q + partialTick*(curPlayer.t - curPlayer.q),
                curPlayer.r + partialTick*(curPlayer.u - curPlayer.r),
                curPlayer.s + partialTick*(curPlayer.v - curPlayer.s));
    }

    // ====
    // MinecraftAccess implementation
    // ====

    @Override
    public MinecraftAccess startDrawing(int mode) {
        // obf: Tessellator.instance, Tessellator.startDrawing
        aza.a.b(mode);
        return this;
    }

    @Override
    public MinecraftAccess addVertex(double x, double y, double z) {
        // obf: Tessellator.instance, Tessellator.addVertex
        aza.a.a(x, y, z);
        return this;
    }

    @Override
    public MinecraftAccess addVertex(ReadonlyVector3 coords) {
        // obf: Tessellator.instance, Tessellator.addVertex
        aza.a.a(coords.getX(), coords.getY(), coords.getZ());
        return this;
    }

    @Override
    public MinecraftAccess finishDrawing() {
        // obf: Tessellator.instance, Tessellator.draw
        aza.a.a();
        return this;
    }

    @Override
    public MinecraftAccess enableStandardItemLighting() {
        // obf: RenderHelper.enableStandardItemLighting
        aqi.b();
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
}
