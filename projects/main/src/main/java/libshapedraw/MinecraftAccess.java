package libshapedraw;

import libshapedraw.primitive.ReadonlyVector3;

/**
 * Provide access to obfuscated Minecraft methods.
 * <p>
 * The main LibShapeDraw API does not directly reference any Minecraft methods.
 * This is for compatibility: any obfuscated code references would make
 * developing mods using the API difficult.
 * <p>
 * Instead, whenever there is a need to invoke a Minecraft method, it happens
 * through this interface. No obfuscation, no fuss, no muss.
 * <p>
 * An instance of this interface is passed to Shapes during rendering for easy
 * access to Minecraft's Tessellator class. An instance can also always be
 * obtained by calling LibShapeDraw.getMinecraftAccess.
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
