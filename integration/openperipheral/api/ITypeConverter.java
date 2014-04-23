package openperipheral.api;

/**
 * A type converter can automatically convert objects between a Java format
 * and a Lua friendly format.
 * Basic ones are already built in (primitives, itemstacks, tanks), but you
 * can register custom ones.
 * 
 * If you don't think your converter should be handling the object passed in
 * just return null to allow other converters to attempt the conversion.
 * 
 * @author mikeef
 * 
 */
public interface ITypeConverter {

	/**
	 * Convert a lua type to the required type. Tables in Lua are passed in as Maps
	 * 
	 * @param obj
	 * @param expected
	 * @return either null if you're not handling this object, or a valid object of type {@code expected}
	 */
	public Object fromLua(Object obj, Class<?> expected);

	/**
	 * Convert to a Lua friendly format. You can pass most primitives back,
	 * Return only types understood by ComputerCraft API (i.e. primitives, String, Maps and LuaObject), otherwise it will be converted to {@code nil} on Lua side
	 * 
	 * @param obj
	 *            either null if you're not handling this object, or a valid object
	 * @return converted value or null
	 */
	public Object toLua(Object obj);
}
