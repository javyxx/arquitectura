package com.paradigma.arquitecture.util;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class EntityConverter {
	protected final static Logger log = LoggerFactory.getLogger(EntityConverter.class);

	public static void populate(Object entity, DBObject results) {
		try {
			for (Field field : entity.getClass().getDeclaredFields()) {

				Object fieldValue = getField(results, field);
				if (fieldValue != null && fieldValue instanceof BasicDBList) {
					BasicDBList list = (BasicDBList) fieldValue;
					List<Object> finalList = populateList(field, list);
					field.setAccessible(true);
					field.set(entity, finalList);
				} else if (fieldValue != null && results.get(field.getName()) instanceof DBObject) {
					Object subEntity = field.getType().newInstance();
					populate(subEntity, (DBObject) results.get(field.getName()));
					field.setAccessible(true);
					field.set(entity, subEntity);

				} else if (fieldValue != null && field.getType().isAssignableFrom(fieldValue.getClass())) {
					field.setAccessible(true);
					field.set(entity, fieldValue);
				}

			}
		} catch (Exception e) {
			log.error("Error to populate entity", e);
		}

	}

	private static List<Object> populateList(Field field, BasicDBList list)
			throws InstantiationException, IllegalAccessException {
		List<Object> finalList = new ArrayList<>();
		for (Object element : list) {
			field.getGenericType();
			if (element instanceof DBObject) {
				ParameterizedType stringListType = (ParameterizedType) field.getGenericType();
				Class<?> typeOfList = (Class<?>) stringListType.getActualTypeArguments()[0];
				Object entitySubList = typeOfList.newInstance();
				populate(entitySubList, (DBObject) element);
				finalList.add(entitySubList);
			} else {
				finalList.add(element);

			}
		}
		return finalList;
	}

	protected static Object getField(DBObject results, Field field) {
		String fieldName = field.getName();
		if (field.getName().equals("id")) {
			fieldName = "_id";
		}
		if (results.containsField(fieldName)) {
			return getFinalField((BasicDBObject) results, fieldName, field.getType());

		}
		return null;
	}

	private static Object getFinalField(BasicDBObject results, String fieldName, Class<?> type) {
		if (type.isEnum()) {
			return Enum.valueOf((Class) type, results.getString(fieldName));
		}

		if (type.isAssignableFrom(Integer.class)) {
			return results.getInt(fieldName);
		}

		if (type.isAssignableFrom(Double.class)) {
			return results.getDouble(fieldName);
		}

		if (type.isAssignableFrom(String.class)) {
			return results.getString(fieldName);
		}
		if (type.isAssignableFrom(Boolean.class)) {
			return results.getBoolean(fieldName);
		}
		if (type.isAssignableFrom(Date.class)) {
			return results.getDate(fieldName);
		}
		if (type.isAssignableFrom(Long.class)) {
			return results.getLong(fieldName);
		}
		if (type.isAssignableFrom(ObjectId.class)) {
			return results.getObjectId(fieldName);
		}

		return results.get(fieldName);
	}

}
