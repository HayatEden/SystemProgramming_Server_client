package bgu.spl.net.impl.rci;

import java.io.Serializable;

public interface Command<T> {

    boolean execute(T arg);
}
