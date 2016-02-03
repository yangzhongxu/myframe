package com.yzx.frames.tool.fm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EventHandle {

	private static HashMap<Object, Map<String, EventObserve>> eventMap = new HashMap<Object, Map<String, EventObserve>>(0);

	/********************************************************************************
	 * @param who
	 *            谁
	 * @param eventKey
	 *            注册了什么事件
	 * @param obs
	 *            想干啥
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
	 *            谁
	 * @param eventKey
	 *            取消注册事件
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
	 *            什么事发生了
	 * @param data
	 *            数据是啥
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
	 *            谁
	 * @param eventId
	 *            有没有注册这个事件
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
	 * 取消事件的全部对应动作
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
