package Entitys;

import java.util.UUID;

/**
 * Created by shestakov.g on 06.06.2015.
 */
public class groupSku {
    public String getGroupId() {
        return groupId;
    }

    public String getParentId() {
        return parentId;
    }

    public String getGroupName() {
        return groupName;
    }

    private String groupId;
    private String parentId;
    private String groupName;

    public groupSku(String groupId, String parentId, String groupName) {
        this.groupId = groupId;
        this.parentId = parentId;
        this.groupName = groupName;
    }
}
