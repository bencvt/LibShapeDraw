package libshapedraw.transform;

import libshapedraw.internal.Controller;

public class ShapeStandardLighting implements ShapeTransform {
    @Override
    public void preRender() {
        Controller.getMinecraftAccess().enableStandardItemLighting();
    }
}
