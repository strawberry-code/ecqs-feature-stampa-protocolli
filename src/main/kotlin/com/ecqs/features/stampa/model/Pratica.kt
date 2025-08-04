package com.ecqs.features.stampa.model

data class Pratica(
    val idPratica: String,
    val nome: String,
    val cognome: String,
    val codiceFiscale: String,
    val dataNascita: String,
    val importoRichiesto: Double,
    val stato: String
)