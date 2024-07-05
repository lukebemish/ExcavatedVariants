package dev.lukebemish.excavatedvariants.impl.client;

import dev.lukebemish.dynamicassetgenerator.api.ResourceGenerationContext;
import net.minecraft.resources.ResourceLocation;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class BackupFetcher {
    public static InputStream getBlockStateFile(ResourceLocation rl, ResourceGenerationContext context) {
        try {
            ResourceLocation bsLocation = ResourceLocation.fromNamespaceAndPath(rl.getNamespace(), "blockstates/" + rl.getPath() + ".json");
            var resource = context.getResourceSource().getResource(bsLocation);
            if (resource == null) throw new IOException("Resource not found: "+bsLocation);
            return resource.get();
        } catch (IOException e) {
            try {
                ResourceLocation testBS = ResourceLocation.fromNamespaceAndPath(rl.getNamespace(), "excavated_variants_backups/blockstates" + rl.getPath() + ".json");
                var resource = context.getResourceSource().getResource(testBS);
                if (resource == null) throw new IOException("Resource not found: "+testBS);
                return resource.get();
            } catch (IOException e2) {
                String blockstate = "{\"variants\":{\"\":{\"model\":\"" + rl.getNamespace() + ":block/" + rl.getPath() + "\"}}}";
                return new ByteArrayInputStream(blockstate.getBytes());
            }
        }
    }

    public static InputStream getModelFile(ResourceLocation rl, ResourceGenerationContext context) {
        try {
            var modelLocation = ResourceLocation.fromNamespaceAndPath(rl.getNamespace(), "models/" + rl.getPath() + ".json");
            var resource = context.getResourceSource().getResource(modelLocation);
            if (resource == null) throw new IOException("Resource not found: "+modelLocation);
            return resource.get();
        } catch (IOException e) {
            try {
                ResourceLocation testBS = ResourceLocation.fromNamespaceAndPath(rl.getNamespace(), "excavated_variants_backups/models/" + rl.getPath() + ".json");
                var resource = context.getResourceSource().getResource(testBS);
                if (resource == null) throw new IOException("Resource not found: "+testBS);
                return resource.get();
            } catch (IOException e2) {
                String model = "{\"parent\":\"block/cube_all\",\"textures\":{\"all\":\"" + rl.getNamespace() + ":" + rl.getPath() + "\"}}";
                return new ByteArrayInputStream(model.getBytes());
            }
        }
    }
}
