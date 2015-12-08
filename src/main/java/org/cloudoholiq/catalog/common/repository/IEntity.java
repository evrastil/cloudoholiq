package org.cloudoholiq.catalog.common.repository;

import java.util.UUID;

public interface IEntity {

    public int getTimestamp();

    public void setTimestamp(int timestamp);

    public UUID getId();

    public void setId(UUID id) ;
}
