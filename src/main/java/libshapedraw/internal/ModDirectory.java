package libshapedraw.internal;

import java.io.File;

import libshapedraw.ApiInfo;

import net.minecraft.client.Minecraft;

public class ModDirectory {
    // obf: Minecraft.getMinecraftDir
    public static final File DIRECTORY = new File(Minecraft.b(), "mods" + File.separator + ApiInfo.getName());
}
