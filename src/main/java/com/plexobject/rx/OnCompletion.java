package com.plexobject.rx;

/**
 * This function is invoked when Observable has pushed all data to the
 * subscriber successfully.
 * 
 * @author Shahzad Bhatti
 *
 */
@FunctionalInterface
public interface OnCompletion {
    /**
     * This method is called to notify user that all data is processed and there
     * is no more data.
     */
    void onCompleted();
}
