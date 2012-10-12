import libshapedraw.ApiInfo;
import libshapedraw.MinecraftAccess;
import libshapedraw.internal.Controller;
import libshapedraw.primitive.ReadonlyVector3;
import libshapedraw.primitive.Vector3;
import net.minecraft.client.Minecraft;

/**
 * Internal class. Client code using the API should ignore this.
 * Rather, instantiate LibShapeDraw.
 * <p>
 * This is a ModLoader mod that links itself to the internal API Controller,
 * providing it data and events from Minecraft. Basically a bootstrapper and
 * an API bridge.
 * <p>
 * As an API bridge, all direct interaction with Minecraft objects passes
 * through this class, making the API itself clean and free of obfuscated
 * code. (There is a single exception: ModDirectory.DIRECTORY.)
 */
public class mod_LibShapeDraw extends BaseMod implements MinecraftAccess {
    private Controller controller;
    private atd curWorld; // obf: WorldClient
    private atg curPlayer; // obf: EntityClientPlayerMP
    private Integer curDimension;

    public mod_LibShapeDraw() {
        controller = Controller.getInstance();
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
        ModLoader.setInGameHook(this, true, true); // game ticks only, not every render frame.
        Controller.getLog().info(getClass().getName() + " loaded");
    }

    // obf: NetClientHandler
    @Override
    public void clientConnect(asv netClientHandler) {
        Controller.getLog().info(getClass().getName() + " new server connection");
        curWorld = null;
        curPlayer = null;
        curDimension = null;
    }

    @Override
    public boolean onTickInGame(float partialTick, Minecraft minecraft) {
        if (curWorld != minecraft.e || curPlayer != minecraft.g) {
            curWorld = minecraft.e; // obf: Minecraft.theWorld
            curPlayer = minecraft.g; // obf: Minecraft.thePlayer

            // Dispatch respawn event to Controller.
            int newDimension = curPlayer.bK; // obf: EntityPlayer.dimension
            controller.respawn(getPlayerCoords(partialTick),
                    curDimension == null,
                    curDimension == null || curDimension != newDimension);
            curDimension = newDimension;
        }

        // Dispatch game tick event to Controller.
        controller.gameTick(getPlayerCoords(partialTick));

        return true;
    }

    /**
     * Get the player's current coordinates, adjusted for movement that occurs
     * between game ticks.
     */
    private ReadonlyVector3 getPlayerCoords(float partialTick) {
        // obf: Entity.prevPosX, Entity.prevPosY, Entity.prevPosZ
        return new Vector3(
                curPlayer.q + partialTick*(curPlayer.t - curPlayer.q),
                curPlayer.r + partialTick*(curPlayer.u - curPlayer.r),
                curPlayer.s + partialTick*(curPlayer.v - curPlayer.s));
    }

    @Override
    public MinecraftAccess startDrawing(int mode) {
        // obf: Tessellator.instance, Tessellator.startDrawing
        ave.a.b(mode);
        return this;
    }

    @Override
    public MinecraftAccess addVertex(double x, double y, double z) {
        // obf: Tessellator.instance, Tessellator.addVertex
        ave.a.a(x, y, z);
        return this;
    }

    @Override
    public MinecraftAccess addVertex(ReadonlyVector3 coords) {
        // obf: Tessellator.instance, Tessellator.addVertex
        ave.a.a(coords.getX(), coords.getY(), coords.getZ());
        return this;
    }

    @Override
    public MinecraftAccess finishDrawing() {
        // obf: Tessellator.instance, Tessellator.draw
        ave.a.a();
        return this;
    }

    @Override
    public MinecraftAccess enableStandardItemLighting() {
        // obf: RenderHelper.enableStandardItemLighting
        ang.b();
        return this;
    }
}
