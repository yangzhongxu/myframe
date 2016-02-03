package com.yzx.frames.tool.fm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EventHandle {

	private static HashMap<Object, Map<String, EventObserve>> eventMap = new HashMap<Object, Map<String, EventObserve>>(0);

	/********************************************************************************
	 * @param who
	 *            ˭
	 * @param eventKey
	 *            ע����ʲô�¼�
	 * @param obs
	 *            ���ɶ
	 ********************************************************************************/
	public static void registerEvent(Object who, String eventKey, EventObserve todo) {
		Map<String, EventObserve> value = eventMap.get(who);
		if (value == null) {
			value = new HashMap<String, EventObserve>();
			eventMap.put(who, value);
		}
		value.put(eventKey, todo);
	}

	/*******************************************************************************
	 * @param who
	 *            ˭
	 * @param eventKey
	 *            ȡ��ע���¼�
	 ********************************************************************************/
	public static void unRegisterEvent(Object who, String eventKey) {
		if (!eventMap.containsKey(who))
			return;
		Map<String, EventObserve> value = eventMap.get(who);
		if (value == null || value.isEmpty()) {
			eventMap.remove(who);
			return;
		}
		value.remove(eventKey);
		if (value.isEmpty())
			eventMap.remove(who);
	}

	/*********************************************************************************
	 * @param eventKey
	 *            ʲô�·�����
	 * @param data
	 *            ������ɶ
	 ********************************************************************************/
	public static void publishEvent(String eventKey, Object data) {
		if (eventMap.isEmpty())
			return;
		for (Map.Entry<Object, Map<String, EventObserve>> entry : eventMap.entrySet())
			if (entry.getValue() != null) {
				EventObserve obs = entry.getValue().get(eventKey);
				if (obs != null)
					obs.onEvent(entry.getKey(), data);
			}
	}

	/*********************************************************************************
	 * @param who
	 *            ˭
	 * @param eventId
	 *            ��û��ע������¼�
	 ********************************************************************************/
	public static boolean hasEvent(Object who, String eventId) {
		if (eventMap.isEmpty())
			return false;
		Map<String, EventObserve> map = eventMap.get(who);
		if (map == null || map.isEmpty())
			return false;
		return map.containsKey(eventId);
	}

	/*********************************************************************************
	 * ȡ���¼���ȫ����Ӧ����
	 ********************************************************************************/
	public static void unRegisterEvents(String eventId) {
		if (eventMap.isEmpty())
			return;
		ArrayList<Object> whos = new ArrayList<Object>(0);
		for (Map.Entry<Object, Map<String, EventObserve>> entry : eventMap.entrySet())
			if (entry.getValue() != null) {
				entry.getValue().remove(eventId);
				if (entry.getValue().isEmpty())
					whos.add(entry.getKey());
			}
		for (Object who : whos)
			eventMap.remove(who);
	}

	//
	/*
	 * 
	 */
	//

	public static interface EventObserve {
		void onEvent(Object who, Object data);
	}

}
