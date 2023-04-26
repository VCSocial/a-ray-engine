package dev.vcsocial.lazerwizard.core.helper.lifecycle;

import jakarta.inject.Singleton;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;

import java.util.Map;
import java.util.Set;

@Singleton
public class LifeCycleEventBroker implements AutoCloseable {

    private static final Logger LOGGER = LogManager.getLogger(LifeCycleEventBroker.class);

    private final Map<Class<?>, Class<? extends LifeCycleEvent>> consumerDesiredEventMap;
    private final Set<LifeCycleEvent> registeredEvents;

    public LifeCycleEventBroker() {
        consumerDesiredEventMap = UnifiedMap.newMap();
        registeredEvents = Sets.mutable.empty();
    }

    public void registerConsumer(Class<?> consumerClass, Class<? extends LifeCycleEvent> lifeCycleEventClass) {
        consumerDesiredEventMap.put(consumerClass, lifeCycleEventClass);
        LOGGER.debug("Consumer [consumerClass={}] is requesting events of class [lifeCycleEventClass={}]",
                consumerClass, lifeCycleEventClass);
    }

    public void registerEvent(LifeCycleEvent lifeCycleEvent) {
        registeredEvents.add(lifeCycleEvent);
        LOGGER.debug("Event [lifeCycleEventClass={}] was registered", lifeCycleEvent.getClass());
    }

    public boolean containsEvent(Class<? extends LifeCycleEvent> lifeCycleEventClass) {
        for (var event : registeredEvents) {
            if (event.getClass().equals(lifeCycleEventClass)) {
                return true;
            }
        }
        return false;
    }

    public void acknowledgeEvent(Class<?> consumerClass, Class<? extends LifeCycleEvent> lifeCycleEventClass) {
        LOGGER.debug("Consumer [consumerClass={}] is attempting to ack receipt of [lifeCycleEventClass={}]",
            consumerClass, lifeCycleEventClass);

        // For now, we are ignoring event removal once no consumer is interested we close this instance
        consumerDesiredEventMap.remove(consumerClass);
        if (consumerDesiredEventMap.isEmpty()) {
            close();
        }
    }

    @Override
    public void close() {
        LOGGER.debug("Closing {} as all consumers have acknowledged receipt of an event", this.getClass().getName());
    }
}
