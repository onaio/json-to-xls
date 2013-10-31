package io.ei.jsontoxls.core;

import org.joda.time.LocalDate;

public class Employee {
    private final String name;
    private final int age;
    private final int payment;
    private final double bonus;

    public Employee(String name, int age, int payment, double bonus) {
        this.name = name;
        this.age = age;
        this.payment = payment;
        this.bonus = bonus;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public int getPayment() {
        return payment;
    }

    public double getBonus() {
        return bonus;
    }

    public LocalDate getBirthdate() {
        return LocalDate.now().minusYears(age);
    }
}
