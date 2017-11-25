package com.testanywhere.core.utilities.reflection;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LifecycleManager
{
    private static boolean isActive = false;
    private static List<Lifecycle> managedObjects = new ArrayList<>();
    private static Lock moLock = new ReentrantLock();

    /**
     * Test to see if the manager is active
     * @return true if active
     */
    public static boolean isActive() {
        return LifecycleManager.isActive;
    }

    /**
     * Activate the lifecycle manager
     * @param active
     */
    public static void setActive(boolean active) {
        LifecycleManager.isActive = active;
    }

    /**
     * Register an object to receive lifecycle events
     * @param object
     */
    public static void register(Lifecycle object) {
        if (LifecycleManager.isActive) {
            LifecycleManager.moLock.lock();
            try {
                if (LifecycleManager.managedObjects == null) {
                    throw new RuntimeException("Unable to register - manager already shut down");
                }

                LifecycleManager.managedObjects.add(object);
            } finally {
                LifecycleManager.moLock.unlock();
            }
        }
    }

    /**
     * Request shutdown for any objects registered with the lifecycle manager
     */
    public synchronized static void shutdown() {
        LifecycleManager.moLock.lock();
        try {
            if (LifecycleManager.managedObjects != null) {
                while (managedObjects.size() > 0) {
                    Lifecycle obj = LifecycleManager.managedObjects.remove(LifecycleManager.managedObjects.size() - 1);
                    obj.shutdown();
                }

                LifecycleManager.managedObjects = null;
            }
        } finally {
            LifecycleManager.moLock.unlock();
        }
    }
}
