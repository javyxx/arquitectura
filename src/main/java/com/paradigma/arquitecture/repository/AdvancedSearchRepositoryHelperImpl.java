package com.paradigma.arquitecture.repository;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

/**
 * The Class AdvancedSearchRepositoryImpl.
 *
 * @author Javier Ledo Vázquez
 * @version 1.0
 */
public class AdvancedSearchRepositoryHelperImpl implements AdvancedSearchRepositoryHelper {

	private static final String INCLUDE_FIELD = ":1";

	private final MongoOperations mongoOperations;
	private final ConversionService conversionService;

	public AdvancedSearchRepositoryHelperImpl(MongoOperations mongoOperations, ConversionService conversionService) {
		super();
		this.mongoOperations = mongoOperations;
		this.conversionService = conversionService;
	}

	/**
	 * Method to find entities by params
	 */
	public <T> Page<T> findAllByParams(final Map<String, Object> parameters, Class<T> entityClass) {

		Query queryCount = this.createBasicQueryFromParameters(parameters, true, entityClass);
		Long number = this.mongoOperations.count(queryCount, entityClass);

		if (number > 0) {
			Query query = this.createBasicQueryFromParameters(parameters, false, entityClass);

			final List<T> list = this.mongoOperations.find(query, entityClass);

			Integer page = getPage(parameters);
			Integer size = getSize(parameters);

			Sort sort = new Sort(processOrder(parameters));

			PageRequest pageRequest = new PageRequest(page, size, sort);

			return new PageImpl<>(list, pageRequest, number);
		}

		return new PageImpl<>(new ArrayList<>());
	}

	private Query createBasicQueryFromParameters(final Map<String, Object> parameters, boolean count,
			Class<?> entityClass) {
		String fieldsToQuery = "";
		if (parameters.containsKey(AdvancedSearchRepositoryHelper.FIELDS_PARAM)) {
			final String[] fields = parameters.get(AdvancedSearchRepositoryHelper.FIELDS_PARAM).toString()
					.split(AdvancedSearchRepositoryHelper.PARAM_SEPARATOR);
			StringBuffer fieldsTest = null;
			for (final String field : fields) {
				if (fieldsTest == null) {
					fieldsTest = new StringBuffer(field).append(INCLUDE_FIELD);
				} else {
					fieldsTest.append(AdvancedSearchRepositoryHelper.PARAM_SEPARATOR).append(field)
							.append(INCLUDE_FIELD);
				}
			}
			fieldsToQuery = fieldsTest.toString();
		}
		final BasicQuery query = new BasicQuery("{}", "{" + fieldsToQuery + "}");

		if (!count) {
			if (parameters.containsKey(AdvancedSearchRepositoryHelper.PAGE_PARAM)
					|| parameters.containsKey(AdvancedSearchRepositoryHelper.SIZE_PARAM)) {
				Integer page = AdvancedSearchRepositoryHelper.PAGE_DEFAULT;
				Integer size = AdvancedSearchRepositoryHelper.SIZE_DEFAULT;
				if (parameters.get(AdvancedSearchRepositoryHelper.PAGE_PARAM) != null) {
					page = Integer.valueOf(parameters.get(AdvancedSearchRepositoryHelper.PAGE_PARAM).toString());
				}
				if (parameters.get(AdvancedSearchRepositoryHelper.SIZE_PARAM) != null) {
					size = Integer.valueOf(parameters.get(AdvancedSearchRepositoryHelper.SIZE_PARAM).toString());
				}
				final PageRequest pageRequest = new PageRequest(page, size);
				query.with(pageRequest);
			}
		}

		if (parameters.containsKey(AdvancedSearchRepositoryHelper.SORT_PARAM)) {
			final String[] sortParameters = parameters.get(AdvancedSearchRepositoryHelper.SORT_PARAM).toString()
					.split(AdvancedSearchRepositoryHelper.PARAM_SEPARATOR);
			final List<Sort.Order> orders = new ArrayList<Sort.Order>();
			for (final String shortParam : sortParameters) {
				if (shortParam.startsWith("-")) {
					orders.add(new Order(Direction.DESC, shortParam.substring(1)));
				} else {
					orders.add(new Order(Direction.ASC, shortParam));
				}
			}
			final Sort sortQuery = new Sort(orders);
			query.with(sortQuery);
		}

		if (parameters.containsKey(AdvancedSearchRepositoryHelper.SEARCH_PARAM)) {

			final String[] conditions = parameters.get(AdvancedSearchRepositoryHelper.SEARCH_PARAM).toString()
					.split(AdvancedSearchRepositoryHelper.PARAM_SEPARATOR);
			for (String condition : conditions) {
				String[] params = condition.split(AdvancedSearchRepositoryHelper.EQUALS_PARAM + "|"
						+ AdvancedSearchRepositoryHelper.NEGATION_PARAM + "|"
						+ AdvancedSearchRepositoryHelper.GREATER_THAN_PARAM + "|"
						+ AdvancedSearchRepositoryHelper.LESS_THAN_PARAM + "|"
						+ AdvancedSearchRepositoryHelper.LIKE_PARAM);
				String name = params[0];

				if (condition.contains(AdvancedSearchRepositoryHelper.EQUALS_PARAM)) {
					if (params.length == 2) {
						Object value = getValue(entityClass, name, params[1]);
						query.addCriteria(Criteria.where(name).is(value));

					} else {
						Object[] entities = Arrays.copyOfRange(params, 1, params.length);
						List<Object> parameterType = new ArrayList<Object>();
						for (Object value : entities) {
							parameterType.add(getValue(entityClass, name, value));
						}
						query.addCriteria(Criteria.where(name).in(parameterType));

					}
				} else if (condition.contains(AdvancedSearchRepositoryHelper.NEGATION_PARAM)) {
					Object value = getValue(entityClass, name, params[1]);
					query.addCriteria(Criteria.where(name).is(value).not());
				} else if (condition.contains(AdvancedSearchRepositoryHelper.GREATER_THAN_PARAM)) {
					Object value = getValue(entityClass, name, params[1]);
					query.addCriteria(Criteria.where(name).gt(value));
				} else if (condition.contains(AdvancedSearchRepositoryHelper.LESS_THAN_PARAM)) {
					Object value = getValue(entityClass, name, params[1]);
					query.addCriteria(Criteria.where(name).lt(value));

				} else if (condition.contains(AdvancedSearchRepositoryHelper.LIKE_PARAM)) {
					query.addCriteria(Criteria.where(name).regex(generatePattern(params[1])));

				}
			}
		}

		return query;
	}

	private Object getValue(final Class<?> clazz, final String fieldName, final Object object) {
		try {
			final Field field = clazz.getDeclaredField(fieldName);
			Class<?> type = field.getType();
			if (type.equals(List.class)) {
				ParameterizedType pType = (ParameterizedType) field.getGenericType();
				type = (Class<?>) pType.getActualTypeArguments()[0];
			}
			if (type.equals(object.getClass())) {
				if (type.equals(String.class)) {
					return generatePattern(object.toString());
				}
				return object;
			}
			if (this.conversionService.canConvert(object.getClass(), type)) {
				return this.conversionService.convert(object, type);
			} else {
				throw new RuntimeException("Not convert type of field: " + fieldName + " on " + clazz.getSimpleName());
			}

		} catch (final Exception e) {
			throw new RuntimeException("Not found field: " + fieldName + " on " + clazz.getSimpleName());
		}

	}

	private String generatePattern(String strToSearch) {
		String result = strToSearch.toLowerCase();
		for (String[] replacement : AdvancedSearchRepositoryHelper.replacements) {
			result = result.replaceAll(replacement[0], replacement[1]);
		}
		return result;
	}

	private Integer getSize(Map<String, Object> parameters) {
		Integer size = SIZE_DEFAULT;
		if (parameters.get(SIZE_PARAM) != null) {
			size = Integer.valueOf(parameters.get(SIZE_PARAM).toString());
		}
		return size;
	}

	private Integer getPage(Map<String, Object> parameters) {
		Integer page = PAGE_DEFAULT;
		if (parameters.get(PAGE_PARAM) != null) {
			page = Integer.valueOf(parameters.get(PAGE_PARAM).toString());
		}
		return page;
	}

	private List<Order> processOrder(Map<String, Object> parameters) {
		List<Order> orders = new ArrayList<>();
		if (parameters.containsKey(SORT_PARAM)) {
			final String[] sortParameters = parameters.get(SORT_PARAM).toString().split(PARAM_SEPARATOR);
			for (final String shortParam : sortParameters) {
				if (shortParam.startsWith("-")) {
					orders.add(new Order(Direction.DESC, shortParam.substring(1)));
				} else {
					orders.add(new Order(Direction.ASC, shortParam));
				}
			}
		}
		return orders;
	}

}
