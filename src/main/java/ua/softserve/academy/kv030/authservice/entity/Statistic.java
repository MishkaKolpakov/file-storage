package ua.softserve.academy.kv030.authservice.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.util.Date;

@JsonIdentityInfo(generator=ObjectIdGenerators.IntSequenceGenerator.class,property="@id", scope = Statistic.class)
public class Statistic {

    private String id;
    private Date fileUploadDate;
    private Long userId;
    private String operationName;
    private Long size;

    public Statistic(){}

    public Statistic(String id){
        this.id = id;
    }

    public Statistic(String id, Date fileUploadDate, Long userId, String operationName, Long size) {
        this.id = id;
        this.fileUploadDate = fileUploadDate;
        this.userId = userId;
        this.operationName = operationName;
        this.size = size;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getFileUploadDate() {
        return fileUploadDate;
    }

    public void setFileUploadDate(Date fileUploadDate) {
        this.fileUploadDate = fileUploadDate;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getOperationName() {
        return operationName;
    }

    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "Statistic{" +
                "id='" + id + '\'' +
                ", fileUploadDate=" + fileUploadDate +
                ", userId=" + userId +
                ", operationName='" + operationName + '\'' +
                ", size=" + size +
                '}';
    }
}
