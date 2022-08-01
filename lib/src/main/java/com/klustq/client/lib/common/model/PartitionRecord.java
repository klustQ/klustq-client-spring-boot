package com.klustq.client.lib.common.model;

import org.springframework.lang.NonNull;

/**
 * @class PartitionRecord a message hold by a partition waiting
 * to be consumed
 */
public class PartitionRecord {
    @NonNull
    protected Integer offset;
    protected Integer partition;
    protected String topic;
    protected String groupId;

    @NonNull
    protected TopicMessage data;

    public PartitionRecord() {
    }

    public PartitionRecord(@NonNull Integer offset, Integer partition,
                           String topic, String groupId, @NonNull TopicMessage data) {
        this.offset = offset;
        this.topic = topic;
        this.groupId = groupId;
        this.data = data;
        this.partition = partition;
    }



    @NonNull
    public Integer getOffset() {
        return offset;
    }

    public void setOffset(@NonNull Integer offset) {
        this.offset = offset;
    }

    public Integer getPartition() {
        return partition;
    }

    public void setPartition(Integer partition) {
        this.partition = partition;
    }

    @NonNull
    public TopicMessage getData() {
        return data;
    }

    public void setData(@NonNull TopicMessage data) {
        this.data = data;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}
