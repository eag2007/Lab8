package org.example.packet.enums;

import java.io.Serializable;

/**
 * enum со всеми командами
 */
public enum ResponseType implements Serializable {
    PUSH("push"),
    ADD("add"),
    ADD_IF_MAX("add_if_max"),
    AVERAGE_OF_DISTANCE("average_of_distance"),
    CLEAR("clear"),
    EXECUTE_SCRIPT("execute_script"),
    EXIT("exit"),
    FILTER_LESS_THAN_DISTANCE("filter_less_than_distance"),
    GENERATE_DATA("generate_data"),
    HELP("help"),
    HISTORY("history"),
    INFO("info"),
    LOGOUT("logout"),
    REMOVE_ALL_BY_DISTANCE("remove_all_by_distance"),
    REMOVE_BY_ID("remove_by_id"),
    REMOVE_FIRST("remove_first"),
    SEE("see"),
    SHOW("show"),
    STATUS("status"),
    SUBSCRIBE("subscribe"),
    TASK_STATUS("task_status"),
    UPDATE("update"),
    LOGIN("login"),
    REGISTER("register"),
    ERROR("error");

    private final String type;

    ResponseType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }
}
