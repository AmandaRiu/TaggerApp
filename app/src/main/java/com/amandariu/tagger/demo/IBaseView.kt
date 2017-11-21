package com.amandariu.tagger.demo

/**
 * This interface should be implemented by any View classes in this project for MVP
 * design.
 *
 * @author Amanda Riu
 */
interface IBaseView<T> {
    /**
     * Set the presenter for the view.
     * @param presenter The presenter for this view.
     */
    fun setPresenter(presenter: T)
}
