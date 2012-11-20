package libshapedraw.demos;

import org.lwjgl.opengl.GL11;

import libshapedraw.LibShapeDraw;
import libshapedraw.MinecraftAccess;
import libshapedraw.primitive.Axis;
import libshapedraw.primitive.Color;
import libshapedraw.primitive.Vector3;
import libshapedraw.shape.Shape;
import libshapedraw.transform.ShapeRotate;

/**
 * Any class in libshapedraw.shapes can be extended with new behaviors.
 * <p>
 * The built-in Shapes try to cover as many use cases as possible though: if
 * you have an idea for a reuseable feature, feel free to request that it be
 * added to LibShapeDraw!
 */
public class mod_LSDDemoShapeCustom extends BaseMod {
    public static final String ABOUT = "" +
            "Define a custom Shape class, allowing for behaviors not covered by the existing built-in Shape types.\n" +
            "/tp to x=0, z=0 to see the shape in action!";

    /**
     * A wireframe triangle outline that must've had too much coffee to drink:
     * its 3 points jitter constantly.
     * <p>
     * We could have extended WireframeShape instead for additional convenience
     * methods. This is just a simple demo though, so we extend Shape.
     */
    public static class MyShape extends Shape {
        private final Color color;

        public MyShape(Vector3 origin, Color color) {
            super(origin);
            setRelativeToOrigin(true);
            if (color == null) {
                throw new IllegalArgumentException("color cannot be null");
            }
            this.color = color;
        }

        @Override
        protected void renderShape(MinecraftAccess mc) {
            color.glApply();
            GL11.glLineWidth(2.0F);
            mc.startDrawing(GL11.GL_LINE_LOOP);
            buf.setRandom().scale(0.1);
            mc.addVertex(buf);
            buf.setRandom().scale(0.1).addY(8.0);
            mc.addVertex(buf);
            buf.setRandom().scale(0.1).addX(8.0);
            mc.addVertex(buf);
            mc.finishDrawing();
        }
        // Persist a vector to the instance so we don't have keep allocating
        // throwaway instances every render frame.
        private final Vector3 buf = new Vector3();

        public Color getColor() {
            return color;
        }
    }

    private LibShapeDraw libShapeDraw;

    @Override
    public String getVersion() {
        return "demo";
    }

    @Override
    public void load() {
        libShapeDraw = new LibShapeDraw().verifyInitialized();

        MyShape myShape = new MyShape(
                new Vector3(20, 63, 20),
                Color.INDIGO.copy().setAlpha(0.75));
        libShapeDraw.addShape(myShape);

        // Even though the Shape is custom-built it works like any other Shape.
        // We can add transforms, animations, etc.
        myShape.getColor().animateStartLoop(Color.CRIMSON, true, 5000);
        myShape.addTransform(
                new ShapeRotate(0.0, Axis.Y)
                .animateStartLoop(360.0, false, 30000));
    }
}
