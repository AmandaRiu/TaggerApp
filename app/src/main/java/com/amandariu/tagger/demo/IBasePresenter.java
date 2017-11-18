package com.amandariu.tagger.demo;

/**
 * This interface should be implemented by all the Presenters in this project for
 * an MVP design.
 *
 * @author Amanda Riu
 */
public interface IBasePresenter {
    /**
     * Initialize the presenter.
     */
    void start();

    /**
     * The view is being destroyed. Clear any references in the presenter.
     */
    void destroyView();
}
