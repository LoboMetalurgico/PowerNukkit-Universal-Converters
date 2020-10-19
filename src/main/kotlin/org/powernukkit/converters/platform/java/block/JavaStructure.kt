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

package org.powernukkit.converters.platform.java.block

import org.powernukkit.converters.math.BlockPos
import org.powernukkit.converters.platform.base.BaseConstructors
import org.powernukkit.converters.platform.base.block.BaseStructure
import org.powernukkit.converters.platform.java.JavaPlatform

/**
 * @author joserobjr
 * @since 2020-10-11
 */
class JavaStructure(
    constructors: BaseConstructors<JavaPlatform>,
    worldPos: BlockPos,
    size: Int,
) : BaseStructure<JavaPlatform>(
    constructors,
    worldPos,
    size,
)
