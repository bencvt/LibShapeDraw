import java.awt.Desktop;
import java.net.URI;

import libshapedraw.ApiInfo;
import libshapedraw.LibShapeDraw;
import libshapedraw.event.LSDEventListener;
import libshapedraw.event.LSDGameTickEvent;
import libshapedraw.event.LSDPreRenderEvent;
import libshapedraw.event.LSDRespawnEvent;
import libshapedraw.primitive.Color;
import net.minecraft.client.Minecraft;

import org.lwjgl.input.Keyboard;

/**
 * Quick-and-dirty ModLoader mod that loads demos that the user selects.
 * <p>
 * The demos themselves are actually ModLoader mods too in all but name:
 * they're located outside of the root package so that this class can load
 * them on demand.
 */
public class mod_LibShapeDrawDemos extends BaseMod implements LSDEventListener {
    public static final String SOURCE_URI_DISPLAY = "https://github.com/bencvt/LibShapeDraw";
    public static final URI SOURCE_URI = URI.create("https://github.com/bencvt/LibShapeDraw/tree/master/projects/demos/src/main/java/libshapedraw/demos");
    public static final int TEXT_ARGB = Color.WHITE.getARGB();

    private static class Demo {
        public final int key;
        public final String name;
        public final String[] about;
        public final Class<? extends libshapedraw.demos.BaseMod> modClass;
        public libshapedraw.demos.BaseMod modInstance;
        @SuppressWarnings("unchecked")
        public Demo(int key, String className) {
            this.key = key;
            name = className;
            className = "libshapedraw.demos." + className;
            try {
                modClass = (Class<? extends libshapedraw.demos.BaseMod>) getClass()
                        .getClassLoader().loadClass(className);
                about = ((String) modClass.getField("ABOUT").get(null)).split("\n");
            } catch (Exception e) {
                throw new RuntimeException("unable to load demo " + String.valueOf(className), e);
            }
        }
    }
    private final Demo[] demos = new Demo[] {
            new Demo(Keyboard.KEY_0, "mod_LSDDemoBasic"),
            new Demo(Keyboard.KEY_1, "mod_LSDDemoBasicCheckInstall"),
            new Demo(Keyboard.KEY_2, "mod_LSDDemoEvents"),
            new Demo(Keyboard.KEY_3, "mod_LSDDemoEventsFreeDraw"),
            new Demo(Keyboard.KEY_4, "mod_LSDDemoLogo"),
            new Demo(Keyboard.KEY_5, "mod_LSDDemoShapeCustom"),
            new Demo(Keyboard.KEY_6, "mod_LSDDemoTridentBasic"),
            new Demo(Keyboard.KEY_7, "mod_LSDDemoTridentDynamic"),
            new Demo(Keyboard.KEY_8, "mod_LSDDemoTridentTimeline"),
    };
    private final LibShapeDraw libShapeDraw = new LibShapeDraw();
    private Minecraft minecraft;
    private boolean inMenu;
    private boolean canOpenUrl;
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
        libShapeDraw.verifyInitialized().addEventListener(this);
    }

    @Override
    public boolean onTickInGame(float partialTick, Minecraft minecraft) {
        // obf: Minecraft.currentScreen
        if (minecraft.r != null) {
            return true;
        }
        if (inMenu) {
            //drawBackground();
            if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE) || Keyboard.isKeyDown(Keyboard.KEY_F3)) {
                inMenu = false;
            } else if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
                // AWT Desktop and LWJGL Keyboard can severely misbehave when
                // switching windows. To prevent the URL from opening dozens of
                // extra times unexpectedly, only allow one URL open per
                // respawn.
                if (canOpenUrl) {
                    canOpenUrl = false;
                    try {
                        Desktop.getDesktop().browse(SOURCE_URI);
                    } catch (Throwable t) {
                        chatText("Unable to open URL directly. Please use a web browser to visit");
                        chatText("  " + SOURCE_URI_DISPLAY);
                    }
                } else {
                    chatText("Your web browser should already be open to");
                    chatText("  " + SOURCE_URI_DISPLAY);
                }
                inMenu = false;
            } else if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
                if (libShapeDraw.debugDump()) {
                    chatText("LibShapeDraw API state dumped, see log file in " + ApiInfo.getModDirectory());
                } else {
                    chatText("Logging is disabled. You can re-enable it by editing the settings file in " + ApiInfo.getModDirectory());
                }
                inMenu = false;
            } else {
                drawText("\u00a7b\u00a7nSelect LibShapeDraw demo to load:", 2, 2, TEXT_ARGB);
                int y = 16;
                for (Demo demo : demos) {
                    if (Keyboard.isKeyDown(demo.key)) {
                        inMenu = false;
                        chatText("\u00a7bLoaded " + demo.name + ":");
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
                            chatText("\u00a77This demo is already loaded.");
                        }
                    }
                    String line;
                    if (demo.modInstance == null) {
                        line = "( \u00a7b" + Keyboard.getKeyName(demo.key) + "\u00a7r ) " + demo.name;
                    } else {
                        line = "\u00a77( " + Keyboard.getKeyName(demo.key) + " ) " + demo.name;
                    }
                    drawText(line, 2, y, TEXT_ARGB);
                    y += 10;
                }
                y += 5;
                if (canOpenUrl) {
                    drawText("( \u00a7bs\u00a7r ) browse demos source code at", 2, y, TEXT_ARGB);
                } else {
                    drawText("\u00a77( s ) browse demos source code at", 2, y, TEXT_ARGB);
                }
                y += 10;
                drawText((canOpenUrl ? "" : "\u00a77") + "\u00a7n" + SOURCE_URI_DISPLAY, 30, y, TEXT_ARGB);
                y += 15;
                drawText("( \u00a7bd\u00a7r ) debug dump LibShapeDraw API state", 2, y, TEXT_ARGB);
                y += 15;
                drawText("( \u00a7bshift-F3\u00a7r ) open Minecraft profiler, then use numbers to drill down to", 2, y, TEXT_ARGB);
                y += 10;
                drawText("\u00a7nroot.gameRenderer.level.LibShapeDraw", 30, y, TEXT_ARGB);
            }
        } else if (Keyboard.isKeyDown(Keyboard.KEY_L)) {
            inMenu = true;
        }
        return true;
    }

    @Override
    public void onRespawn(LSDRespawnEvent event) {
        savedFakeRespawnEvent = event;
        chatText("\u00a7bPress L to load LibShapeDraw demos!");
        canOpenUrl = true;
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
