package org.lavajuno.mirrorlog.client;

public class ClientState {
    private String component_name;

    public ClientState() {
        component_name = "(not specified)";
    }

    public void setComponentName(String component_name) {
        this.component_name = component_name;
    }

    public String getComponentName() {
        return this.component_name;
    }
}
