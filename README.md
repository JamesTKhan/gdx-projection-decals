# gdx-projection-decals

Experimenting with 3D projection decals. Not a finished library at this point. No jitpack yet so it must be built locally.

```java
private ProjectionDecalRenderer decalRenderer = new ProjectionDecalRenderer();
private DecalDebugRenderer decalDebugRenderer = new DecalDebugRenderer();
        
// Create a projection decal, the camera should be looking at whatever you want to project onto
ProjectionDecal decal = new ProjectionDecal(perspectiveCamera, texture);

...
        
// Render your scene normally...
...
// Render the decal each frame, instances is a list of ModelInstances to project onto
decalRenderer.render(camera, decal, environment, instances);
// Optional debug renderer, renders the decal's projection frustum
decalDebugRenderer.render(camera, decal);
```