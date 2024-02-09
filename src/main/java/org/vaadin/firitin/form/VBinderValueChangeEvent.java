package org.vaadin.firitin.form;

import com.vaadin.flow.component.HasValue;

public class VBinderValueChangeEvent<T> implements HasValue.ValueChangeEvent<T> {

    public VBinderValueChangeEvent(VBinder<T> source, boolean fromClient) {
        this.source = source;
        this.fromClient = fromClient;
    }

    private VBinder<T> source;

    private boolean fromClient;

    @Override
    public HasValue getHasValue() {
        return source;
    }

    @Override
    public boolean isFromClient() {
        return fromClient;
    }

    @Override
    public T getOldValue() {
        // This is non-functional currently, probably not even needed, throw instead?
        return null;
    }

    @Override
    public T getValue() {
        return (T) getHasValue().getValue();
    }
}
