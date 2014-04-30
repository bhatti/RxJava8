package com.plexobject.rx;

/**
 * This interface is used to add behavior for cancellation
 * 
 * @author shahzadbhatti
 *
 */
public interface Cancelable {
    /**
     * Cancel this operation
     */
    void cancel();

    /**
     * Is operation canceled
     * 
     * @return
     */
    boolean isCaneled();
}
