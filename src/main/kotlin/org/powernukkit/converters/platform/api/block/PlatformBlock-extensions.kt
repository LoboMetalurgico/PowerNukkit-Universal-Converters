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

package org.powernukkit.converters.platform.api.block

import org.powernukkit.converters.math.BlockPos
import org.powernukkit.converters.platform.api.BlockContainer
import org.powernukkit.converters.platform.api.Platform

/**
 * @author joserobjr
 * @since 2020-10-16
 */
fun <P : Platform<P>> PlatformBlock<P>.positionedAt(container: BlockContainer<P>, pos: BlockPos) =
    PositionedBlock(container, pos, this)

@Suppress("UNCHECKED_CAST")
operator fun <P : Platform<P>, Block : PlatformBlock<P>> Block.plus(other: Block): Block {
    val otherIsAir = other.isBlockAir
    return platform.createPlatformBlock(
        blockLayers = if (otherIsAir) this.blockLayers else other.blockLayers,
        blockEntity = other.blockEntity?.takeUnless { otherIsAir } ?: this.blockEntity,
        entities = this.entities + other.entities
    ) as Block
}
