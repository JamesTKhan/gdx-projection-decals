package com.github.jamestkhan.decals.debug;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.github.jamestkhan.decals.ProjectionDecal;

/**
 * Renders the frustum of the virtual camera used to project the decal.
 *
 * @author JamesTKhan
 * @version September 08, 2023
 */
public class DecalDebugRenderer {
    public Color frustumColor = Color.GOLD;
    public float frustumLineWidth = 2f;
    public boolean renderFrustumAsLines = false;

    public boolean depthTestEnabled = true;

    private final ShapeRenderer shapeRenderer;

    public DecalDebugRenderer() {
        shapeRenderer = new ShapeRenderer();
    }

    public void render(Camera camera, ProjectionDecal decal) {
        shapeRenderer.setProjectionMatrix(camera.combined);

        Vector3[] frustumCorners = decal.getVirtualCamera().frustum.planePoints;

        if (depthTestEnabled) {
            Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        } else {
            Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
        }

        if (renderFrustumAsLines) {
            renderFrustumAsLines(shapeRenderer, frustumCorners, frustumColor);
        } else {
            renderFrustumAsLines(shapeRenderer, frustumCorners, Color.BLACK);
            renderFrustumAsQuads(shapeRenderer, frustumCorners);
        }

    }

    private void renderFrustumAsQuads(ShapeRenderer shapeRenderer, Vector3[] frustumCorners) {
        // Enable blending so that the frustum is transparent
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(frustumColor);
        shapeRenderer.getColor().a = 0.35f;

        // Draw the near plane as a filled rectangle
        drawRectangle(shapeRenderer, frustumCorners[0], frustumCorners[1], frustumCorners[2], frustumCorners[3]);

        // Draw the far plane as a filled rectangle
        drawRectangle(shapeRenderer, frustumCorners[4], frustumCorners[5], frustumCorners[6], frustumCorners[7]);

        // Connect the corners of the near and far planes
        drawRectangle(shapeRenderer, frustumCorners[0], frustumCorners[4], frustumCorners[5], frustumCorners[1]);
        drawRectangle(shapeRenderer, frustumCorners[1], frustumCorners[5], frustumCorners[6], frustumCorners[2]);
        drawRectangle(shapeRenderer, frustumCorners[2], frustumCorners[6], frustumCorners[7], frustumCorners[3]);
        drawRectangle(shapeRenderer, frustumCorners[3], frustumCorners[7], frustumCorners[4], frustumCorners[0]);

        shapeRenderer.end();

        // Disable blending after rendering
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    private void renderFrustumAsLines(ShapeRenderer shapeRenderer, Vector3[] frustumCorners, Color color) {
        Gdx.gl.glLineWidth(frustumLineWidth);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(color);

        // Draw the near plane
        shapeRenderer.line(frustumCorners[0], frustumCorners[1]);
        shapeRenderer.line(frustumCorners[1], frustumCorners[2]);
        shapeRenderer.line(frustumCorners[2], frustumCorners[3]);
        shapeRenderer.line(frustumCorners[3], frustumCorners[0]);

        // Draw the far plane
        shapeRenderer.line(frustumCorners[4], frustumCorners[5]);
        shapeRenderer.line(frustumCorners[5], frustumCorners[6]);
        shapeRenderer.line(frustumCorners[6], frustumCorners[7]);
        shapeRenderer.line(frustumCorners[7], frustumCorners[4]);

        // Connect the corners of the near and far planes
        shapeRenderer.line(frustumCorners[0], frustumCorners[4]);
        shapeRenderer.line(frustumCorners[1], frustumCorners[5]);
        shapeRenderer.line(frustumCorners[2], frustumCorners[6]);
        shapeRenderer.line(frustumCorners[3], frustumCorners[7]);

        shapeRenderer.end();
        Gdx.gl.glLineWidth(1f);
    }

    // Helper function to draw a filled rectangle using the vertex method
    private void drawRectangle(ShapeRenderer shapeRenderer, Vector3 p1, Vector3 p2, Vector3 p3, Vector3 p4) {
        float colorBits = shapeRenderer.getColor().toFloatBits();

        ImmediateModeRenderer im = shapeRenderer.getRenderer();

        im.vertex(p1.x, p1.y, p1.z);
        im.color(colorBits);
        im.vertex(p2.x, p2.y, p2.z);
        im.color(colorBits);
        im.vertex(p3.x, p3.y, p3.z);
        im.color(colorBits);

        im.vertex(p3.x, p3.y, p3.z);
        im.color(colorBits);
        im.vertex(p4.x, p4.y, p4.z);
        im.color(colorBits);
        im.vertex(p1.x, p1.y, p1.z);
        im.color(colorBits);
    }
}
