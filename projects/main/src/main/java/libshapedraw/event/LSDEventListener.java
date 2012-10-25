package libshapedraw.event;

public interface LSDEventListener {
    /** @see LSDRespawnEvent */
    public void onRespawn(LSDRespawnEvent event);

    /** @see LSDGameTickEvent */
    public void onGameTick(LSDGameTickEvent event);

    /** @see LSDPreRenderEvent */
    public void onPreRender(LSDPreRenderEvent event);
}
