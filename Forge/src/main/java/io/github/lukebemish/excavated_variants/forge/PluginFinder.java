package io.github.lukebemish.excavated_variants.forge;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Type;

import java.util.*;

public final class PluginFinder {
    private static final Logger LOGGER = LogManager.getLogger();

    public static <T> List<T> getInstances(Class<?> annotationClass, Class<T> instanceClass) {
        var annotationType = Type.getType(annotationClass);
        var scanData = ModList.get().getAllScanData();
        Set<String> names = new LinkedHashSet<>();
        for (ModFileScanData scanDatum : scanData) {
            Iterable<ModFileScanData.AnnotationData> annotations = scanDatum.getAnnotations();
            for (ModFileScanData.AnnotationData a : annotations) {
                if (Objects.equals(a.annotationType(), annotationType)) {
                    String memberName = a.memberName();
                    names.add(memberName);
                }
            }
        }
        List<T> instances = new ArrayList<>();
        for (String name : names) {
            try {
                var clazz = Class.forName(name);
                var asSubclass = clazz.asSubclass(instanceClass);
                var constructor = asSubclass.getDeclaredConstructor();
                var instance = constructor.newInstance();
                instances.add(instance);
            } catch (ReflectiveOperationException | LinkageError e) {
                LOGGER.error("Failed to load: {}", name, e);
            }
        }
        return instances;
    }
}
