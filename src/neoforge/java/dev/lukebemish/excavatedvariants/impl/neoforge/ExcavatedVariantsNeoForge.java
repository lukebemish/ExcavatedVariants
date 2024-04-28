package dev.lukebemish.excavatedvariants.impl.neoforge;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.lukebemish.excavatedvariants.impl.BlockAddedCallback;
import dev.lukebemish.excavatedvariants.impl.ExcavatedVariants;
import dev.lukebemish.excavatedvariants.impl.ExcavatedVariantsClient;
import dev.lukebemish.excavatedvariants.impl.RegistriesImpl;
import dev.lukebemish.excavatedvariants.impl.network.AckOresPayload;
import dev.lukebemish.excavatedvariants.impl.network.SyncOresPayload;
import dev.lukebemish.excavatedvariants.impl.worldgen.OreReplacer;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.network.configuration.ICustomConfigurationTask;
import net.neoforged.neoforge.network.event.RegisterConfigurationTasksEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.apache.commons.lang3.mutable.MutableBoolean;

import java.util.function.Consumer;
import java.util.stream.Collectors;

@Mod(ExcavatedVariants.MOD_ID)
public class ExcavatedVariantsNeoForge {
    public static final DeferredRegister<Item> TO_REGISTER = DeferredRegister.create(Registries.ITEM, ExcavatedVariants.MOD_ID);
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Registries.FEATURE, ExcavatedVariants.MOD_ID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ExcavatedVariants.MOD_ID);
    public static final DeferredRegister<MapCodec<? extends BiomeModifier>> BIOME_MODIFIERS = DeferredRegister.create(NeoForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, ExcavatedVariants.MOD_ID);
    public static final Holder<MapCodec<? extends BiomeModifier>> ADD_FEATURE_LATE_MODIFIER = BIOME_MODIFIERS.register("add_feature_late", () ->
            RecordCodecBuilder.<AddFeatureLateModifier>mapCodec(i -> i.group(
                    PlacedFeature.LIST_CODEC.fieldOf("features").forGetter(AddFeatureLateModifier::feature)
            ).apply(i, AddFeatureLateModifier::new))
    );

    public ExcavatedVariantsNeoForge(IEventBus modbus) {
        ExcavatedVariants.init();
        if (FMLLoader.getDist() == Dist.CLIENT) {
            ExcavatedVariantsClient.init();
        }
        modbus.addListener(FMLCommonSetupEvent.class, ExcavatedVariantsNeoForge::commonSetup);
        modbus.addListener(RegisterEvent.class, ExcavatedVariantsNeoForge::registerListener);
        modbus.addListener(RegisterPayloadHandlersEvent.class, ExcavatedVariantsNeoForge::onRegisterPayloads);
        modbus.addListener(ExcavatedVariantsNeoForge::onCollectConfigTasks);
        TO_REGISTER.register(modbus);
        BIOME_MODIFIERS.register(modbus);
        FEATURES.register(modbus);
        CREATIVE_TABS.register(modbus);
        modbus.addListener(FMLCommonSetupEvent.class, e -> {
            //no more race conditions... thanks FML
            e.enqueueWork(() -> NeoForge.EVENT_BUS.register(EventHandler.class));
        });
        //ModList.get().getModContainerById("hyle").ifPresent(container -> MinecraftForge.EVENT_BUS.register(new HyleCompat()));

        FEATURES.register("ore_replacer", OreReplacer::new);
    }

    public static void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(RegistriesImpl::registerRegistries);
    }

    public static void registerListener(RegisterEvent event) {
        event.register(Registries.BLOCK, helper -> {
            BuiltInRegistries.BLOCK.holders().forEach(reference ->
                    BlockAddedCallback.onRegister(reference.value(), reference.key()));
            BlockAddedCallback.setReady();
            BlockAddedCallback.register();
        });
        event.register(Registries.ITEM, helper ->
                ExcavatedVariants.initPostRegister());
    }

    private static final String NEtWORK_VERSION = "1";

    public static void onRegisterPayloads(final RegisterPayloadHandlersEvent event) {
        event.registrar(NEtWORK_VERSION)
                .configurationToClient(SyncOresPayload.TYPE, SyncOresPayload.CODEC, OresConfigTask::handleSync)
                .configurationToServer(AckOresPayload.TYPE, AckOresPayload.CODEC, (payload, ctx) -> ctx.finishCurrentTask(OresConfigTask.TYPE));
    }

    private static final class OresConfigTask implements ICustomConfigurationTask {
        public static final Type TYPE = new Type(ExcavatedVariants.id("ores"));
        @Override
        public void run(Consumer<CustomPacketPayload> sender) {
            var payload = new SyncOresPayload(ExcavatedVariants.COMPLETE_VARIANTS.stream().map(v -> v.fullId).collect(Collectors.toSet()));
            sender.accept(payload);
        }

        @Override
        public Type type() {
            return TYPE;
        }

        public static void handleSync(SyncOresPayload payload, IPayloadContext ctx) {
            MutableBoolean disconnect = new MutableBoolean(false);
            payload.consumeMessage(string -> {
                disconnect.setTrue();
                ctx.disconnect(Component.literal(string));
            });

            if (!disconnect.isTrue()) {
                ctx.reply(new AckOresPayload());
            }
        }
    }

    public static void onCollectConfigTasks(final RegisterConfigurationTasksEvent event) {
        if (!event.getListener().getConnection().isMemoryConnection()) {
            event.register(new OresConfigTask());
        }
    }
}
