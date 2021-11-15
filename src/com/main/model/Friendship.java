package com.main.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Friendship extends Entity<Tuple<Long,Long>> {

    LocalDateTime date;

    public Friendship(Long u1, Long u2) {
        if(u1 > u2){
            Long aux = u1;
            u1 = u2;
            u2 = aux;
        }
        Tuple<Long,Long> id = new Tuple<>(u1,u2);
        this.setId(id);
        date = LocalDateTime.now();
    }
    public Friendship(Long u1, Long u2,LocalDateTime time){
        this(u1,u2);
        this.setDate(time);
    }
    public void setDate(LocalDateTime time){
        date = time;
    }
    /**
     * @return the date when the friendship was created
     */
    public LocalDateTime getDate() {
        return date;
    }
    @Override
    public String toString(){
        return this.getId().getLeft() + " " + this.getId().getRight() +", " +
                this.getDate();
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Friendship that)) return false;
        return this.getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(),getDate());
    }
}
