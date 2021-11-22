package com.main.model;

import java.util.Objects;

public class Request extends Entity<Tuple<Long,Long>> {
    String status;

    public Request(Long u1, Long u2, String status) {
        Tuple<Long,Long> id = new Tuple<>(u1,u2);
        this.setId(id);
        this.status = status;
    }

    public Request(Long u1, Long u2) {
        Tuple<Long,Long> id = new Tuple<>(u1,u2);
        this.setId(id);
        this.status = "pending";
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Request{" +
                "id=" + id +
                ", status='" + status + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Request that)) return false;
        return this.getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(),getStatus());
    }
}
