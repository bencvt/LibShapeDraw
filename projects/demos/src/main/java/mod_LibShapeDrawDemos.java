import libshapedraw.ApiInfo;
import libshapedraw.LibShapeDraw;
import libshapedraw.event.LSDEventListener;
import libshapedraw.event.LSDGameTickEvent;
import libshapedraw.event.LSDPreRenderEvent;
import libshapedraw.event.LSDRespawnEvent;
import libshapedraw.internal.LSDController;
import libshapedraw.internal.LSDModDirectory;
import net.minecraft.client.Minecraft;

import org.lwjgl.input.Keyboard;

/**
 * Quick-and-dirty ModLoader mod that loads demos that the user selects.
 * <p>
 * The demos themselves are actually ModLoader mods too in all but name:
 * they're intentionally not named starting with "mod_" so that this class can
 * load them on demand.
 */
public class mod_LibShapeDrawDemos extends BaseMod implements LSDEventListener {
    private static class Demo {
        public final int key;
        public final String name;
        public final String[] about;
        public final Class<? extends BaseMod> modClass;
        public BaseMod modInstance;
        @SuppressWarnings("unchecked")
        public Demo(int key, String className) {
            this.key = key;
            name = className;
            try {
                modClass = (Class<? extends BaseMod>) getClass().getClassLoader().loadClass(className);
                about = ((String) modClass.getField("ABOUT").get(null)).split("\n");
            } catch (Exception e) {
                throw new RuntimeException("unable to load demo " + String.valueOf(className), e);
            }
        }
    }
    private final Demo[] demos = new Demo[] {
            new Demo(Keyboard.KEY_0, "LSDDemoBasic"),
            new Demo(Keyboard.KEY_1, "LSDDemoBasicCheckInstall"),
            new Demo(Keyboard.KEY_2, "LSDDemoEvents"),
            new Demo(Keyboard.KEY_3, "LSDDemoLogo"),
            new Demo(Keyboard.KEY_4, "LSDDemoTridentBasic"),
            new Demo(Keyboard.KEY_5, "LSDDemoTridentDynamic"),
    };
    private Minecraft minecraft;
    private boolean inMenu;
    private LSDRespawnEvent savedFakeRespawnEvent;

    @Override
    public String getName() {
        return ApiInfo.getName() + " Demos";
    }

    @Override
    public String getVersion() {
        return ApiInfo.getVersion();
    }

    @Override
    public void load() {
        minecraft = ModLoader.getMinecraftInstance();
        ModLoader.setInGameHook(this, true, false); // every render frame.
        new LibShapeDraw().verifyInitialized().addEventListener(this);
    }

    @Override
    public boolean onTickInGame(float partialTick, Minecraft minecraft) {
        // obf: Minecraft.currentScreen
        if (minecraft.r != null) {
            return true;
        }
        if (inMenu) {
            if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
                inMenu = false;
            } else if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
                if (LSDController.getInstance().dump()) {
                    chatText("\u00a79LibShapeDraw API state dumped, see log file in " + LSDModDirectory.DIRECTORY);
                } else {
                    chatText("\u00a79Logging is disabled. You can re-enable it by editing the settings file in " + LSDModDirectory.DIRECTORY);
                }
                inMenu = false;
            } else {
                drawText("\u00a79Select LibShapeDraw demo to load, or press D to dump state:", 2, 2, 0xffffffff);
                int y = 12;
                for (Demo demo : demos) {
                    if (Keyboard.isKeyDown(demo.key)) {
                        inMenu = false;
                        // The full URL prefix doesn't quite fit. :-(
                        // https://github.com/bencvt/LibShapeDraw/blob/master/src/demos/java/LSDDemo*.java
                        chatText("\u00a79Source code: http://bit.ly/" + demo.name);
                        for (String line : demo.about) {
                            chatText(line);
                        }
                        if (demo.modInstance == null) {
                            try {
                                demo.modInstance = demo.modClass.newInstance();
                            } catch (Exception e) {
                                throw new RuntimeException("unable to instantiate demo mod " + demo.modClass, e);
                            }
                            demo.modInstance.load();
                            if (demo.modInstance instanceof LSDEventListener) {
                                ((LSDEventListener) demo.modInstance).onRespawn(savedFakeRespawnEvent);
                            }
                            return true;
                        } else {
                            chatText("This demo is already loaded.");
                        }
                    }
                    String line = Keyboard.getKeyName(demo.key) + ": " + demo.name;
                    if (demo.modInstance != null) {
                        line = "\u00a77" + line + " (loaded)";
                    }
                    drawText(line, 2, y, 0xffffffff);
                    y += 10;
                }
                drawText("\u00a79Performance check: shift-F3, root.gameRenderer.level.LibShapeDraw", 2, y, 0xffffffff);
            }
        } else if (Keyboard.isKeyDown(Keyboard.KEY_L)) {
            inMenu = true;
        }
        return true;
    }

    @Override
    public void onRespawn(LSDRespawnEvent event) {
        savedFakeRespawnEvent = event;
        chatText("\u00a79Press L to load LibShapeDraw demos!");
    }

    @Override
    public void onGameTick(LSDGameTickEvent event) {
        // do nothing
    }

    @Override
    public void onPreRender(LSDPreRenderEvent event) {
        // do nothing
    }

    public void chatText(String message) {
        // obf: Minecraft.ingameGUI, GuiIngame.getChatGUI, GuiNewChat.printChatMessage
        minecraft.v.b().a(message);
    }

    public void drawText(String message, int x, int y, int argb) {
        // obf: Minecraft.fontRenderer, FontRenderer.drawStringWithShadow
        minecraft.p.a(message, x, y, argb);
    }
}
