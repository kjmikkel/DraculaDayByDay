package com.jensen.draculadaybyday.entries;

// Dramatis personæ
public enum Person {
    JONATHAN_HARKER("Jonathan Harker"),
    MINA_MURRAY("Mina Murray"),
    MINA_HARKER("Mina Harker"),
    LUCY_WESTENRA("Lucy Westenra"),
    QUINCEY_MORRIS("Quiencey Morris"),
    ARTHUR_HOLMWOOD("Arthur Holmwood"),
    DR_SEWARD("Dr. Seward"),
    SAMUEL_F_BILLINGTON("Samuel F. Billington & Son"),
    MESSRS("Messrs. Carter, Paterson & Co."),
    SISTER_AGATHA("Sister Agatha"),
    ABRAHAM_VAN_HELSING("Abraham Van Helsing"),
    PALL_MALL_GAZETTE("The Pall Mall Gazette"),
    PATRICK_HENNESSEY("Patrick Hennessey"),
    WESTMINISTER_GAZETTE("The Westminster Gazette"),
    MITCHELL_AND_SONS("Mitchell, Sons and Candy");

    private final String name;

    Person(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }
}
