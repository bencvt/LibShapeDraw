package libshapedraw;

import libshapedraw.primitive.ReadonlyVector3;

/**
 * Minecraft methods that need to be called outside of the bootstrapper (mod_LibShapeDraw).
 * <p>
 * Rather than dirtying up the API with obfuscated code references, delegate the access back
 * the bootstrapper, which implements this interface.
 * <p>
 * Client code can ignore this interface unless implementing a custom Shape class, in which
 * case you will receive an instance in a render method. Nothing special to do; just use
 * these methods if they're useful for what you're doing.
 */
public interface MinecraftAccess {
    /** Tessellator.instance.startDrawing */
    public MinecraftAccess startDrawing(int mode);

    /** Tessellator.instance.addVertex */
    public MinecraftAccess addVertex(double x, double y, double z);

    /** Tessellator.instance.addVertex */
    public MinecraftAccess addVertex(ReadonlyVector3 coords);

    /** Tessellator.instance.draw */
    public MinecraftAccess finishDrawing();

    /** RenderHelper.enableStandardItemLighting */
    public MinecraftAccess enableStandardItemLighting();

    /** Minecraft.ingameGUI.getChatGUI().printChatMessage */
    public MinecraftAccess sendChatMessage(String message);

    /** Minecraft.ingameGUI.getChatGUI() */
    public boolean chatWindowExists();

    /** Minecraft.timer.renderPartialTicks */
    public float getPartialTick();

    /** Minecraft.mcProfiler.startSection */
    public MinecraftAccess profilerStartSection(String sectionName);

    /** Minecraft.mcProfiler.endSection */
    public MinecraftAccess profilerEndSection();

    /** Minecraft.mcProfiler.endStartSection */
    public MinecraftAccess profilerEndStartSection(String sectionName);
}
