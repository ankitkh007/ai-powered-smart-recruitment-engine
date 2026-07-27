package com.recruitment.engine.dto.request;

import com.recruitment.engine.entity.enums.JobStatus;
import jakarta.validation.constraints.NotNull;

public class JobStatusUpdateRequestDto {

    @NotNull(message = "Status is required")
    private JobStatus status;

    public JobStatus getStatus() {
        return status;
    }

    public void setStatus(JobStatus status) {
        this.status = status;
    }
}