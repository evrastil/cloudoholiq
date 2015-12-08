package org.cloudoholiq.catalog.model.statistic;


import org.cloudoholiq.catalog.model.Entity;

public class VisitEntryLog extends Entity{

    private String ip;
    private String searchQuery;

    public VisitEntryLog() {
    }

    public VisitEntryLog(String ip, String searchQuery) {
        this.ip = ip;
        this.searchQuery = searchQuery;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    @Override
    public String selfPath() {
        return "/api/visits";
    }
}
