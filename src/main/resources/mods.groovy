MultiplatformModsDotGroovy.make {
    modLoader = 'javafml'
    loaderVersion = '[1,)'

    license = 'LGPL-3.0-or-later'
    issueTrackerUrl = 'https://github.com/lukebemishprojects/ExcavatedVariants/issues'

    mod {
        modId = buildProperties.mod_id
        displayName = buildProperties.mod_name
        version = environmentInfo.version
        displayUrl = 'https://github.com/lukebemishprojects/ExcavatedVariants'
        description = 'Adds data-defined ore variants for stone/ore combinations missing them'
        author = buildProperties.mod_author

        dependencies {
            mod('minecraft') {
                versionRange = ">=${libs.versions.minecraft}"
            }

            onNeoForge {
                mod('neoforge') {
                    versionRange = ">=${libs.versions.neoforge}"
                }
            }

            onFabric {
                mod('fabricloader') {
                    versionRange = ">=${libs.versions.fabric_loader}"
                }
                mod('fabric-api') {
                    versionRange = ">=${libs.versions.fabric_api.split(/\+/)[0]}"
                }
            }

            mod('dynamic_asset_generator') {
                versionRange = ">=${libs.versions.dynassetgen}"
            }
            mod('defaultresources') {
                versionRange = ">=${libs.versions.defaultresources}"
            }
        }

        entrypoints {
            entrypoint 'main', 'dev.lukebemish.excavatedvariants.impl.fabriquilt.fabric.ExcavatedVariantsFabric'
            entrypoint 'main', 'dev.lukebemish.excavatedvariants.impl.fabriquilt.StateCapturer'
            entrypoint 'client', 'dev.lukebemish.excavatedvariants.impl.fabriquilt.ExcavatedVariantsClientFabriQuilt'
        }
    }
    onNeoForge {
        mixins {
            mixin {
                config = 'mixin.excavated_variants.json'
            }
            mixin {
                config = 'mixin.excavated_variants_neoforge.json'
            }
        }
    }
    onFabric {
        mixins {
            mixin {
                config = 'mixin.excavated_variants.json'
            }
            mixin {
                config = 'mixin.excavated_variants_fabriquilt.json'
            }
        }
    }
}
