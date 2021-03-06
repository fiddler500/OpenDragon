/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Libraries;

import java.util.HashMap;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

public class ReikaFluidHelper {

	private static HashMap<Fluid, FluidContainer> containers = new HashMap();

	public static void mapContainerToFluid(Fluid f, ItemStack empty, ItemStack filled) {
		containers.put(f, new FluidContainer(filled, empty));
	}

	public static ItemStack getFilledContainerFor(Fluid f) {
		FluidContainer fc = containers.get(f);
		ItemStack is = fc != null ? fc.filled : null;
		return is != null ? is.copy() : null;
	}

	public static ItemStack getEmptyContainerFor(Fluid f) {
		FluidContainer fc = containers.get(f);
		ItemStack is = fc != null ? fc.empty : null;
		return is != null ? is.copy() : null;
	}

	public static boolean isInfinite(Fluid f) {
		return f == FluidRegistry.WATER;
	}

	private static class FluidContainer {

		private final ItemStack filled;
		private final ItemStack empty;

		private FluidContainer(ItemStack fill, ItemStack emp) {
			filled = fill;
			empty = emp;
		}

	}

}
