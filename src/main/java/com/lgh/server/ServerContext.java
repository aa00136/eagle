package com.lgh.server;

import com.lgh.model.command.Command;

/**
 * Created by ligh on 2017/5/10.
 */
public class ServerContext {
    private static final ThreadLocal<Command> CONTEXT = new ThreadLocal<Command>();

    public static void put(Command cmd) {
        CONTEXT.set(cmd);
    }

    public static Command getAndRemove() {
        Command data = CONTEXT.get();
        CONTEXT.remove();
        return data;
    }

    public static Command get() {
        Command data = CONTEXT.get();
        return data;
    }

    public static void remove() {
        CONTEXT.remove();
    }
}
