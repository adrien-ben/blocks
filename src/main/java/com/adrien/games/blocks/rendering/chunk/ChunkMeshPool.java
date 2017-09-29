package com.adrien.games.blocks.rendering.chunk;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class ChunkMeshPool {

    private final Stack<ChunkMesh> pool;
    private final List<ChunkMesh> usedChunkMeshes;

    public ChunkMeshPool(final int size) {
        this.pool = new Stack<>();
        this.usedChunkMeshes = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            final ChunkMesh chunkMesh = new ChunkMesh();
            this.pool.push(chunkMesh);
        }
    }

    public ChunkMesh getChunkMesh() {
        if (this.pool.isEmpty()) {
            throw new IllegalStateException("The chunk mesh pool does not have other mesh available. "
                    + "Try to allocate a bigger pool or use less meshes.");
        }
        final ChunkMesh nextFreeChunkMesh = this.pool.pop();
        this.usedChunkMeshes.add(nextFreeChunkMesh);
        return nextFreeChunkMesh;
    }

    public void releaseChunkMesh(final ChunkMesh mesh) {
        final boolean removed = this.usedChunkMeshes.remove(mesh);
        if (!removed) {
            throw new IllegalArgumentException("You are trying to release a chunk mesh that you did not get "
                    + "from this pool instance.");
        }
        mesh.reset();
        this.pool.push(mesh);
    }

    public void destroy() {
        this.pool.forEach(ChunkMesh::destroy);
        this.usedChunkMeshes.forEach(ChunkMesh::destroy);
    }

}
