/*
 * PowerNukkit Universal Worlds & Converters for Minecraft
 * Copyright (C) 2020  José Roberto de Araújo Júnior
 *   
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *   
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *   
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.powernukkit.converters.platform.base

import org.powernukkit.converters.platform.api.MinecraftEdition
import org.powernukkit.converters.platform.api.NamespacedId
import org.powernukkit.converters.platform.api.Platform
import org.powernukkit.converters.platform.api.block.*
import org.powernukkit.converters.platform.universal.UniversalPlatform
import org.powernukkit.converters.platform.universal.block.*
import org.powernukkit.converters.platform.universal.definitions.model.block.type.ModelExtraBlock

/**
 * @author joserobjr
 * @since 2020-10-13
 */
abstract class BasePlatform<
        P : Platform<P>,
        BlockProperty : PlatformBlockProperty<P>,
        BlockEntityType : PlatformBlockEntityType<P>,
        BlockType : PlatformBlockType<P>,
        BlockState : PlatformBlockState<P>,
        BlockPropertyValue : PlatformBlockPropertyValue<P>,
        BlockEntityDataType : PlatformBlockEntityDataType<P>,
        >(
    val universal: UniversalPlatform,
    name: String,
    minecraftEdition: MinecraftEdition,

    ) : Platform<P>(name, minecraftEdition) {
    val blockPropertiesByUniversalId = universal.blockPropertiesById
        .mapValues { (_, universalProperty) ->
            createBlockProperty(universalProperty.getEditionId(minecraftEdition), universalProperty)
        }

    val blockEntityTypesById =
        checkNotNull(universal.blockEntityTypesByEditionId[minecraftEdition]) { "The universal platform is missing block entity types definitions for $minecraftEdition" }
            .mapValues { (id, universalEntityType) ->
                val values = universalEntityType.data.values.asSequence()
                    .map { it.getEditionId(minecraftEdition) to it }
                    .filterNot { (id) -> id == "_missing_" }
                    .associate { (id, universal) -> id to createBlockEntityDataType(universal) }

                createBlockEntityType(id, universalEntityType, values)
            }

    val blockTypesById =
        checkNotNull(universal.blockTypesByEditionId[minecraftEdition]) { "The universal platform is missing block types definitions for $minecraftEdition" }
            .let { universalTypes ->
                val mainTypes = universalTypes.mapValues { (id, universalBlockType) ->
                    createBlockType(id, universalBlockType)
                }

                val extraTypes = universalTypes.values.asSequence()
                    .flatMap { universalType ->
                        universalType.extraBlocks[minecraftEdition]?.asSequence()
                            ?.map {
                                val id = NamespacedId(it.id)
                                id to createBlockType(id, universalType, it)
                            } ?: emptySequence()
                    }.toMap()

                mainTypes + extraTypes
            }

    final override val airBlockType = checkNotNull(blockTypesById[NamespacedId("air")]) {
        "The minecraft:air block type is not registered"
    }

    @Suppress("LeakingThis")
    final override val airBlockState = createBlockState(airBlockType)

    protected abstract fun createBlockProperty(id: String, universal: UniversalBlockProperty): BlockProperty
    protected abstract fun createBlockEntityType(
        id: String,
        universal: UniversalBlockEntityType,
        values: Map<String, BlockEntityDataType>
    ): BlockEntityType

    protected abstract fun createBlockEntityDataType(universal: UniversalBlockEntityDataType): BlockEntityDataType
    protected abstract fun createBlockType(
        id: NamespacedId,
        universal: UniversalBlockType,
        extra: ModelExtraBlock? = null
    ): BlockType

    protected abstract fun createBlockState(blockType: BlockType): BlockState

    protected abstract fun createBlockPropertyValue(
        int: Int,
        universalValue: UniversalBlockPropertyValue
    ): BlockPropertyValue

    protected abstract fun createBlockPropertyValue(
        string: String,
        universalValue: UniversalBlockPropertyValue
    ): BlockPropertyValue

    protected abstract fun createBlockPropertyValue(
        boolean: Boolean,
        universalValue: UniversalBlockPropertyValue
    ): BlockPropertyValue

    protected open fun createBlockPropertyValue(universalValue: UniversalBlockPropertyValue): BlockPropertyValue {
        val value = universalValue.getEditionValue(minecraftEdition)
        val int = value.toIntOrNull()
        if (int != null) {
            return createBlockPropertyValue(int, universalValue)
        }
        if (value == "true" || value == "false") {
            return createBlockPropertyValue(value.toBoolean(), universalValue)
        }
        return createBlockPropertyValue(value, universalValue)
    }
    
    internal fun createBlockPropertyValueList(universal: UniversalBlockProperty): List<BlockPropertyValue> {
        return universal.values.map(this::createBlockPropertyValue)
    }
}
