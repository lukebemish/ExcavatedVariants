package io.github.lukebemish.excavated_variants.forge;

import io.github.lukebemish.excavated_variants.ICompat;
import io.github.lukebemish.excavated_variants.api.ExcavatedVariantsListener;
import io.github.lukebemish.excavated_variants.api.IOreListModifier;
import com.google.auto.service.AutoService;

import java.util.List;

@AutoService(ICompat.class)
public class CompatImpl implements ICompat {
    public List<IOreListModifier> getOreListModifiers() {
        return PluginFinder.getInstances(ExcavatedVariantsListener.class, IOreListModifier.class);
    }
}
