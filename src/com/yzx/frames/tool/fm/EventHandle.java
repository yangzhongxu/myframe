package com.yzx.frames.tool.fm;

import java.util.HashMap;
import java.util.Map;

public class EventHandle {

	private static HashMap<Object, Map<String, EventObserve>> eventMap = new HashMap<Object, Map<String, EventObserve>>(0);

	/**
	 * @param who
	 *            ˭
	 * @param eventKey
	 *            ע����ʲô�¼�
	 * @param obs
	 *            ���ɶ
	 */
	public static void registerEvent(Object who, String eventKey, EventObserve todo) {
		Map<String, EventObserve> value = eventMap.get(who);
		if (value == null) {
			value = new HashMap<String, EventObserve>();
			eventMap.put(who, value);
		}
		value.put(eventKey, todo);
	}

	/**
	 * @param who
	 *            ˭
	 * @param eventKey
	 *            ȡ��ע���¼�
	 */
	public static void unRegisterEvent(Object who, String eventKey) {
		Map<String, EventObserve> value = eventMap.get(who);
		if (value == null)
			return;
		value.remove(eventKey);
	}

	/**
	 * @param eventKey
	 *            ʲô�·�����
	 * @param data
	 *            ������ɶ
	 */
	public static void publishEvent(String eventKey, Object data) {
		if (eventMap.isEmpty())
			return;
		for (Map<String, EventObserve> map : eventMap.values()) {
			EventObserve obs = map.get(eventKey);
			obs.onEvent(data);
		}
	}

	//
	/*
	 * 
	 */
	//

	public static interface EventObserve {
		void onEvent(Object data);
	}

}
