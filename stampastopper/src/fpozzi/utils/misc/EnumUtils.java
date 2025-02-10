package fpozzi.utils.misc;

public class EnumUtils
{

	public static <T extends Enum<T>> T valueOfIgnoreCase(Class<T> enumeration, String name) throws IllegalArgumentException
	{

		for (T enumValue : enumeration.getEnumConstants())
		{
			if (enumValue.name().equalsIgnoreCase(name))
			{
				return enumValue;
			}
		}

		throw new IllegalArgumentException(String.format("There is no value with name '%s' in Enum %s", name, enumeration.getName()));
	}

}
