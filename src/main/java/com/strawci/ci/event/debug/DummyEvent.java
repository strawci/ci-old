package com.strawci.ci.event.debug;

import com.strawci.ci.event.Event;
import com.strawci.ci.event.HandlerList;

public class DummyEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final String text;
    private final int number;

    public DummyEvent (final String text, final int number) {
        this.text = text;
        this.number = number;
    }

    public String getText () {
        return this.text;
    }

    public int getNumber () {
        return this.number;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}