package com.amandariu.tagger.demo

/**
 * This interface should be implemented by all the Presenters in this project for
 * an MVP design.
 *
 * @author Amanda Riu
 */
interface IBasePresenter {
    /**
     * Initialize the presenter.
     */
    fun start()

    /**
     * The view is being destroyed. Clear any references in the presenter.
     */
    fun destroyView()
}
