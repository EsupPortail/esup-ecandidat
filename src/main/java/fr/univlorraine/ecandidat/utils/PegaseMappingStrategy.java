package fr.univlorraine.ecandidat.utils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.HeaderColumnNameTranslateMappingStrategy;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

public class PegaseMappingStrategy<T> extends HeaderColumnNameTranslateMappingStrategy<T> {

	private final Map<String, String> columnMap = new HashMap<>();

	public PegaseMappingStrategy(final Class<? extends T> clazz) {

		for (final Field field : clazz.getDeclaredFields()) {
			final CsvBindByName annotation = field.getAnnotation(CsvBindByName.class);
			if (annotation != null) {
				columnMap.put(field.getName().toUpperCase(), annotation.column());
			}
		}
		setType(clazz);
		setColumnOrderOnWrite(new ClassFieldOrderComparator(clazz));
	}

	@Override
	public String getColumnName(final int col) {
		final String name = headerIndex.getByPosition(col);
		return name;
	}

	public String getColumnNameLower(final int col) {
		String name = headerIndex.getByPosition(col);
		if (name != null) {
			name = columnMap.get(name);
		}
		return name;
	}

	@Override
	public String[] generateHeader(final T bean) throws CsvRequiredFieldEmptyException {
		final String[] result = super.generateHeader(bean);
		for (int i = 0; i < result.length; i++) {
			result[i] = getColumnNameLower(i);
		}

		return result;
	}
}

class ClassFieldOrderComparator implements Comparator<String> {

	List<String> fieldNamesInOrderWithinClass;

	public ClassFieldOrderComparator(final Class<?> clazz) {
		fieldNamesInOrderWithinClass = Arrays.stream(clazz.getDeclaredFields())
			.filter(field -> field.getAnnotation(CsvBindByName.class) != null)
			.map(field -> field.getName().toUpperCase())
			.collect(Collectors.toList());
	}

	@Override
	public int compare(final String o1, final String o2) {
		final int fieldIndexo1 = fieldNamesInOrderWithinClass.indexOf(o1);
		final int fieldIndexo2 = fieldNamesInOrderWithinClass.indexOf(o2);
		return Integer.compare(fieldIndexo1, fieldIndexo2);
	}
}