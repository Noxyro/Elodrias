/*
 * Copyright (c) 2020 - Elodrias Project (https://elodrias.de)
 * by Noxyro (https://noxyro.me or https://github.com/noxyro)
 * This program comes with ABSOLUTELY NO WARRANTY.
 * This is free software, published under the GNU GPL v3 license
 * and you are welcome to redistribute it under certain conditions.
 * See full license (in project or at https://www.gnu.org/licenses/) for more details.
 *
 */

package de.elodrias.module.exception

import de.elodrias.exception.NotRegisteredException
import de.elodrias.module.Feature

class ModuleNotRegisteredException(name: String) : NotRegisteredException(Feature::class.java, name)