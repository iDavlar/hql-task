package by.itacademy.hibernate.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public record Birthday(LocalDate birthDate) implements Comparable<Birthday> {
    public long getAge() {
        return ChronoUnit.YEARS.between(birthDate, LocalDate.now());
    }

    @Override
    public int compareTo(Birthday o) {
        return this.birthDate.compareTo(o.birthDate());
    }
}
