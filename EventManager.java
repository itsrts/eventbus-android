package com.itsrts.utility;

import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * The class is used as an eventbus. The class maintains the threading model of the subscribers and publishers.
 * Created by itsrts on 21/01/16.
 */
public class EventManager {

    private static EventManager eventManager;
    private HashMap<String, ArrayList<EventBusReceiver>> receivers;

    private EventManager() {
        receivers = new HashMap<String, ArrayList<EventBusReceiver>>();
    }

    /**
     * The method returns a singleton object of the event bus
     */
    public static EventManager getInstance() {
        if (eventManager == null)
            eventManager = new EventManager();
        return eventManager;
    }

    /**
     * The mothod returns a new instance of the event bus
     */
    public static EventManager getNewInstance() {
        return new EventManager();
    }

    /**
     * The method subscribes the receiver for the terms
     * @param receiver the receiver or the subscriber
     * @param terms the terms that are to be subscribed or listened
     * @return the recursive reference to the event manager
     */
    public EventManager subscribe(GpEventReceiver receiver, String... terms) {
        for (String term : terms) {
            EventBusReceiver eventBusReceiver = eventManager.new EventBusReceiver(term, receiver);
            ArrayList<EventBusReceiver> list = eventManager.getTerm(term);
            for (int i = 0; i < list.size(); ++i)
                if (list.get(i).equals(receiver))
                    return eventManager;
            list.add(eventBusReceiver);
        }
        return eventManager;
    }

    /**
     * The method un-subscribes the receiver completely from all the terms
     * @param object the receiver or the subscriber
     * @return the recursive reference to the event manager
     */
    public EventManager unSubscribe(Object object) {
        if (object instanceof EventManager.GpEventReceiver)
            unSubscribe((EventManager.GpEventReceiver) object);
        return eventManager;
    }

    /**
     * The method un-subscribes the receiver completely from all the terms
     * @param receiver the receiver or the subscriber
     * @return the recursive reference to the event manager
     */
    private EventManager unSubscribe(GpEventReceiver receiver) {
        Set<String> keys = eventManager.receivers.keySet();
        String[] array = keys.toArray(new String[keys.size()]);
        unSubscribe(receiver, array);
        return eventManager;
    }

    /**
     * The method un-subscribes the receiver completely from all the given terms
     * @param receiver the receiver or the subscriber
     * @param terms the terms that are to be un-subscribed
     * @return the recursive reference to the event manager
     */
    public EventManager unSubscribe(GpEventReceiver receiver, String... terms) {
        for (String term : terms) {
            ArrayList<EventBusReceiver> list = eventManager.getTerm(term);
            list.remove(eventManager.new EventBusReceiver(term, receiver));
            if (list.size() == 0) {
                eventManager.receivers.remove(term);
            }
        }
        return eventManager;
    }

    /**
     * The method un-subscribes all the receivers and objects from the given terms
     * @param terms the terms that are to be un-subscribed
     * @return the recursive reference to the event manager
     */
    public EventManager unSubscribeAll(String... terms) {
        for (String term : terms) {
            eventManager.receivers.remove(term);
        }
        return eventManager;
    }

    /**
     * The method publish an object
     * @param term the term for the object published
     * @return the recursive reference to the event manager
     */
    public EventManager publish(String term, Object object) {
        ArrayList<EventBusReceiver> list = eventManager.getTerm(term);
        for (int i = 0; i < list.size(); ++i) {
            list.get(i).publish(object);
        }
        return eventManager;
    }

    private ArrayList<EventBusReceiver> getTerm(String term) {
        ArrayList<EventBusReceiver> list = receivers.get(term);
        if (list == null) {
            list = new ArrayList<EventBusReceiver>();
            receivers.put(term, list);
        }
        return list;
    }

    public interface GpEventReceiver {
        void onEvent(String term, Object object);
    }

    private class EventBusReceiver {
        String term;
        GpEventReceiver gpEventReceiver;
        Handler handler;

        public EventBusReceiver(String term, GpEventReceiver gpEventReceiver) {
            this.term = term;
            this.gpEventReceiver = gpEventReceiver;
            handler = new Handler(Looper.myLooper());
        }

        @Override
        public boolean equals(Object obj) {
            return obj != null && obj instanceof EventBusReceiver && ((EventBusReceiver) obj).gpEventReceiver == gpEventReceiver;
        }

        private boolean tryCallPush(Object object) {
            try {
                gpEventReceiver.getClass().getMethod("onEvent", String.class, object.getClass())
                        .invoke(gpEventReceiver, term, object);
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        public void publish(final Object object) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (!tryCallPush(object))
                            gpEventReceiver.onEvent(term, object);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}
