package com.esrc.biosignal.libs;

/**
 * Created by lhw48 on 2016-06-22.
 */
public class State {
    protected int state;

    public int getState() {
        return state;
    }

    public static State fromReceivedData(int state) {
        return new State(state);
    }

    protected State(int state) {
        this.state = state;
    }

    protected State(State otherState) {
        state = otherState.state;
    }

    protected State() {
        this.state = -1;
    }

}
