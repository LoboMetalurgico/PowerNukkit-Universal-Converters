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

package org.powernukkit.converters.converter

import org.powernukkit.converters.platform.api.BlockContainer
import org.powernukkit.converters.platform.api.NamespacedId
import org.powernukkit.converters.platform.api.Platform
import org.powernukkit.converters.platform.api.block.PlatformBlock
import org.powernukkit.converters.platform.api.block.PlatformBlockPropertyValue
import org.powernukkit.converters.platform.api.block.PlatformBlockState
import org.powernukkit.converters.platform.api.block.PlatformBlockType

/**
 * @author joserobjr
 * @since 2020-10-17
 */
open class BlockPropertyValuesConverter<FromPlatform : Platform<FromPlatform>, ToPlatform : Platform<ToPlatform>>(
    val fromPlatform: FromPlatform,
    val toPlatform: ToPlatform,

    val adapters: Adapters<NamespacedId, BlockPropertyValuesAdapter<FromPlatform, ToPlatform>>,
) {
    open fun convert(
        fromValues: Map<String, PlatformBlockPropertyValue<FromPlatform>>,
        toType: PlatformBlockType<ToPlatform>,
        fromState: PlatformBlockState<FromPlatform>,
        fromLayer: Int,
        fromLayers: List<PlatformBlockState<FromPlatform>>,
        fromBlock: PlatformBlock<FromPlatform>,
        fromContainer: BlockContainer<FromPlatform>
    ): Map<String, PlatformBlockPropertyValue<ToPlatform>> {

        var result: Map<String, PlatformBlockPropertyValue<ToPlatform>>? = null

        fun List<BlockPropertyValuesAdapter<FromPlatform, ToPlatform>>.applyAdapters() {

            result = fold(result) { current, adapter ->
                adapter.adaptBlockPropertyValues(
                    fromPlatform, toPlatform, fromValues, toType, current,
                    fromState, fromLayer, fromLayers, fromBlock, fromContainer
                )
            }

        }

        adapters.firstAdapters.applyAdapters()
        adapters.fromAdapters[fromState.type.id]?.applyAdapters()
        adapters.toAdapters[toType.id]?.applyAdapters()
        adapters.lastAdapters.applyAdapters()
        adapters.lastToAdapters[toType.id]?.applyAdapters()
        
        return checkNotNull(result) {
            "Could not convert the properties from the block state $fromState to type $toType, from platform ${fromPlatform.name} to ${toPlatform.name}"
        }
    }
}
