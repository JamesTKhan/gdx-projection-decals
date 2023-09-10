package com.github.jamestkhan.decals;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.DirectionalLightsAttribute;
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.GdxRuntimeException;

/**
 * @author JamesTKhan
 * @version September 05, 2023
 */
public class ProjectiveDecalShader extends BaseShader {
    private final static String VERTEX_SHADER = "com/github/jamestkhan/shaders/projective_decal.vert.glsl";
    private final static String FRAGMENT_SHADER = "com/github/jamestkhan/shaders/projective_decal.frag.glsl";

    protected final int u_projectiveMatrixLoc = register(new Uniform("u_modelViewProjectionMatrix"));
    protected final int u_decalTextureLoc = register(new Uniform("u_decalTexture"));

    protected final int u_decalProjectionMatrixLoc = register(new Uniform("u_decalMatrix"));
    protected final int u_worldTransform = register(new Uniform("u_worldTransform"));
    protected final int u_decalNearFar = register(new Uniform("u_decalCameraClipping"));
    protected final int u_scrollSpeed = register(new Uniform("u_scrollSpeed"));

    protected final int u_stretchToFrustum = register(new Uniform("u_stretchToFrustum"));
    protected final int u_lightDirection = register(new Uniform("u_lightDirection"));
    protected final int u_ambientColor = register(new Uniform("u_ambientColor"));

    private ProjectionDecal decal;

    private Renderable renderable;
    private float scrollSpeed = 0.0f;

    public ProjectiveDecalShader(Renderable renderable) {
        super();
        this.renderable = renderable;

        String prefix = "";
        if (Gdx.app.getType() == Application.ApplicationType.Desktop) {
            prefix = "#version 120\n";
        } else {
            prefix = "#version 100\n";
        }

        String vert = Gdx.files.classpath(VERTEX_SHADER).readString();
        String frag = Gdx.files.classpath(FRAGMENT_SHADER).readString();
        program = new ShaderProgram(prefix + vert, frag);
        if (!program.isCompiled()) {
            throw new IllegalArgumentException("Error compiling shader: " + program.getLog());
        }
    }

    @Override
    public void init() {
        super.init(program, renderable);
        renderable = null;
    }

    @Override
    public int compareTo(Shader other) {
        return 0;
    }

    @Override
    public boolean canRender(Renderable instance) {
        return true;
    }

    @Override
    public void begin(Camera camera, RenderContext context) {
        this.camera = camera;
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        super.begin(camera, context);
        context.begin();
        context.setCullFace(GL20.GL_BACK);
        context.setBlending(true, GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        context.setDepthTest(GL20.GL_LEQUAL);
//        context.setDepthMask(false);
    }

    @Override
    public void render(Renderable renderable) {
        if (decal == null) {
            throw new GdxRuntimeException("Projection Decal not set");
        }

        Camera decalCamera = decal.getVirtualCamera();

        decalCamera.update();
        camera.update();

        scrollSpeed += 0.0001f;

        set(u_projectiveMatrixLoc, camera.combined);
        set(u_decalProjectionMatrixLoc, decalCamera.combined);
        set(u_decalTextureLoc, context.textureBinder.bind(decal.texture));
        set(u_worldTransform, renderable.worldTransform);
        set(u_decalNearFar, decalCamera.near, decalCamera.far);
        set(u_scrollSpeed, scrollSpeed);
        set(u_stretchToFrustum, decal.isStretch() ? 1 : 0);

        // Get directional light from the environment
        DirectionalLightsAttribute dla = (DirectionalLightsAttribute) renderable.environment.get(DirectionalLightsAttribute.Type);

        set(u_lightDirection, dla.lights.get(0).direction);

        // Ambient
        ColorAttribute ca = (ColorAttribute) renderable.environment.get(ColorAttribute.AmbientLight);
        set(u_ambientColor, ca.color.r, ca.color.g, ca.color.b);

        super.render(renderable);
    }

    @Override
    public void end() {
//        context.setDepthMask(true);
        context.end();
        super.end();

    }

    @Override
    public void dispose() {
        super.dispose();
    }

    public void setDecal(ProjectionDecal decal) {
        this.decal = decal;
    }
}