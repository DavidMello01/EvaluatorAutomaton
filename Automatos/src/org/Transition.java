package org;
public class Transition {
    private String fromState;
    private String toState;

    public Transition(String fromState, String toState) {
        this.fromState = fromState;
        this.toState = toState;
    }

    public String getFromState() {
        return fromState;
    }

    public String getToState() {
        return toState;
    }
}

