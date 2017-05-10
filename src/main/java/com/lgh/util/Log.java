package com.lgh.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by ligh on 2017/5/9.
 */
public class Log {
    public static final Logger SERVER_QUEUE = LoggerFactory.getLogger("server.queue");
    public static final Logger SERVER_COMMAND = LoggerFactory.getLogger("server.command");
    public static final Logger CLIENT_COMMAND = LoggerFactory.getLogger("client.command");
    public static final Logger SERVER_STARTUP = LoggerFactory.getLogger("server.startup");
    public static final Logger CLIENT_STARTUP = LoggerFactory.getLogger("client.startup");
    public static final Logger SERVER_ERROR = LoggerFactory.getLogger("server.error");
    public static final Logger CLIENT_ERROR = LoggerFactory.getLogger("client.error");
}
