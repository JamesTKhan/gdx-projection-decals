package com.github.jamestkhan.decals;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;

/**
 * @author JamesTKhan
 * @version September 05, 2023
 */
public class ProjectionDecal {

    // The virtual camera to project the decal from
    protected PerspectiveCamera virtualCamera;

    // The texture to project
    protected Texture texture;

    // If true, will stretch the texture out to the edges of the frustum
    protected boolean stretch = false;

    private boolean scrolling = false;

    private float currentScroll = 0.0f;

    private float scrollSpeed = 0.1f;

    public ProjectionDecal(PerspectiveCamera virtualCamera, Texture texture) {
        this.virtualCamera = virtualCamera;
        this.texture = texture;
    }

    public void update(float delta) {
        if (scrolling) {
            currentScroll += scrollSpeed * delta;
        }
    }

    public void setVirtualCamera(PerspectiveCamera virtualCamera) {
        this.virtualCamera = virtualCamera;
    }

    public PerspectiveCamera getVirtualCamera() {
        return virtualCamera;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public Texture getTexture() {
        return texture;
    }

    public void setStretch(boolean stretch) {
        this.stretch = stretch;
    }

    public boolean isStretch() {
        return stretch;
    }

    public void setScrolling(boolean scrolling) {
        this.scrolling = scrolling;
    }

    public boolean isScrolling() {
        return scrolling;
    }

    public float getCurrentScroll() {
        return currentScroll;
    }

    public void setScrollSpeed(float scrollSpeed) {
        this.scrollSpeed = scrollSpeed;
    }

    public float getScrollSpeed() {
        return scrollSpeed;
    }
}
