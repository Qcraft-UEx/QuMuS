package edu.uclm.alarcos.qmutator.reperdao;

import java.lang.reflect.Field;

import org.json.JSONObject;

public class Jsoner {

	public static JSONObject toJSON(Object payload) throws Exception {
		JSONObject jso = new JSONObject();
		Class<?> clazz = payload.getClass();
		Field[] fields = clazz.getDeclaredFields();
		Field field;
		Object value;
		Class<?> fieldType;
		for (int i=0; i<fields.length; i++) {
			field = fields[i];
			field.setAccessible(true);
			value = field.get(payload);
			fieldType = field.getType();
			if (fieldType.isPrimitive() || fieldType==String.class)
				jso.put(field.getName(), value);
			
		}
		return jso;
	}

}
