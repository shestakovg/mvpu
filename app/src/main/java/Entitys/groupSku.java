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

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Integer getOutletCount() {
        return OutletCount;
    }

    public void setOutletCount(Integer outletCount) {
        OutletCount = outletCount;
    }

    public String getColor() {
        return Color;
    }

    public void setColor(String color) {
        Color = color;
    }

    private String groupId;
    private String parentId;
    private String groupName;
    private Integer OutletCount;
    private String Color;

    public Integer getFactOutletCount() {
        return FactOutletCount;
    }

    public void setFactOutletCount(Integer factOutletCount) {
        FactOutletCount = factOutletCount;
    }

    private Integer FactOutletCount;

    private float planAmount = 0;
    private float factAmount = 0;

    public float getPlanAmount() {
        return planAmount;
    }

    public void setPlanAmount(float planAmount) {
        this.planAmount = planAmount;
    }

    public float getFactAmount() {
        return factAmount;
    }

    public void setFactAmount(float factAmount) {
        this.factAmount = factAmount;
    }

    public groupSku(String groupId, String parentId, String groupName, Integer OutletCount, String Color, Integer FactOutletCount) {
        this.groupId = groupId;
        this.parentId = parentId;
        this.groupName = groupName;
        this.Color = Color;
        this.OutletCount = OutletCount;
        this.FactOutletCount = FactOutletCount;
    }
}
