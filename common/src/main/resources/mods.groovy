/*
 * Copyright (C) 2024 Luke Bemish and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

import modsdotgroovy.Dependency

ModsDotGroovy.make {
    modLoader = 'javafml'
    loaderVersion = '[1,)'

    license = 'LGPL-3.0-or-later'
    issueTrackerUrl = 'https://github.com/lukebemishprojects/ExcavatedVariants/issues'

    mod {
        modId = this.buildProperties['mod_id']
        displayName = this.buildProperties['mod_name']
        version = this.version
        displayUrl = 'https://github.com/lukebemishprojects/ExcavatedVariants'

        description = 'Adds data-defined ore variants for stone/ore combinations missing them'
        authors = [this.buildProperties['mod_author'] as String]

        dependencies {
            mod 'minecraft', {
                def minor = this.libs.versions.minecraft.split(/\./)[1] as int
                versionRange = "[${this.libs.versions.minecraft},1.${minor+1}.0)"
            }

            onForge {
                neoforge = ">=${this.libs.versions.neoforge}"
            }

            onFabric {
                mod 'fabricloader', {
                    versionRange = ">=${this.libs.versions.fabric.loader}"
                }
                mod 'fabric-api', {
                    versionRange = ">=${this.libs.versions.fabric.api.split(/\+/)[0]}"
                }
            }

            mod('dynamic_asset_generator') {
                versionRange = ">=${this.libs.versions.dynassetgen}"
            }
            mod('defaultresources') {
                versionRange = ">=${this.libs.versions.defaultresources}"
            }
        }

        onForge {
            dependencies = dependencies.collect { dep ->
                new Dependency() {
                    @Override
                    Map asForgeMap() {
                        def map = dep.asForgeMap()
                        map.remove('mandatory')
                        map.put('type', this.mandatory ? 'required' : 'optional')
                        return map
                    }
                }
            }
        }

        entrypoints {
            entrypoint 'main', [
                    'dev.lukebemish.excavatedvariants.impl.fabriquilt.fabric.ExcavatedVariantsFabric',
                    'dev.lukebemish.excavatedvariants.impl.fabriquilt.StateCapturer'
            ]
            entrypoint 'client', ['dev.lukebemish.excavatedvariants.impl.fabriquilt.ExcavatedVariantsClientFabriQuilt']
        }
    }
    onForge {
        mixins = [
                ['config':'mixin.excavated_variants.json'],
                ['config':'mixin.excavated_variants_neoforge.json']
        ]
    }
    onFabric {
        mixin = [
                'mixin.excavated_variants.json',
                'mixin.excavated_variants_fabriquilt.json'
        ]
    }
}
