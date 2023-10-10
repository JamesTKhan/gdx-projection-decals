package com.github.jamestkhan.decals;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

/**
 * @author JamesTKhan
 * @version September 05, 2023
 */
public class ProjectionDecalRenderer implements Disposable {
    private static final BoundingBox boundingBox = new BoundingBox();
    private static final Vector3 min = new Vector3(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY);
    private static final Vector3 max = new Vector3(Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY);

    protected ProjectiveDecalShader projectiveDecalShader;
    protected final ModelBatch modelBatch;

    // The decal to render
    private ProjectionDecal decal;

    public ProjectionDecalRenderer() {
        this.modelBatch = new ModelBatch(new DefaultShaderProvider() {
            @Override
            protected Shader createShader(Renderable renderable) {
                if (projectiveDecalShader == null) {
                    projectiveDecalShader = new ProjectiveDecalShader(renderable);
                    projectiveDecalShader.setDecal(decal);
                }
                return projectiveDecalShader;
            }

            @Override
            public Shader getShader(Renderable renderable) {
                if (projectiveDecalShader != null) {
                    projectiveDecalShader.setDecal(decal);
                }
                return super.getShader(renderable);
            }
        });
    }
    public void render(Camera camera, ProjectionDecal decal, Environment environment, Array<RenderableProvider> renderables) {
        this.decal = decal;

        if (!isVisible(camera, decal)) {
            return;
        }

        decal.update(Gdx.graphics.getDeltaTime());

        modelBatch.begin(camera);
        modelBatch.render(renderables, environment);
        modelBatch.end();
    }

    private boolean isVisible(Camera camera, ProjectionDecal decal) {
        Vector3[] corners = decal.getVirtualCamera().frustum.planePoints;
        min.set(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY);
        max.set(Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY);

        for (Vector3 corner : corners) {
            if (corner.x < min.x) min.x = corner.x;
            if (corner.y < min.y) min.y = corner.y;
            if (corner.z < min.z) min.z = corner.z;

            if (corner.x > max.x) max.x = corner.x;
            if (corner.y > max.y) max.y = corner.y;
            if (corner.z > max.z) max.z = corner.z;
        }

        boundingBox.set(min, max);

        return camera.frustum.boundsInFrustum(boundingBox);
    }

    @Override
    public void dispose() {
        modelBatch.dispose();
        projectiveDecalShader.dispose();
    }

}
