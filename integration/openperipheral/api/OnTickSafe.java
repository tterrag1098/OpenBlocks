package openperipheral.api;

import java.lang.annotation.*;

import net.minecraft.world.WorldProvider;

/**
 * Suppresses warning about unavailable world instance (needed for synchronized {@link OnTick} methods).
 * If target type does not provide World (is not TileEntity or {@link WorldProvider}, method won't be called (exception will be thrown)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE, ElementType.PACKAGE })
public @interface OnTickSafe {
	public boolean value() default true;
}
