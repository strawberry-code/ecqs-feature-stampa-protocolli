package com.ecqs.features.stampa.model

data class Pratica(
    val idPratica: String,
    val nome: String,
    val cognome: String,
    val codiceFiscale: String,
    val dataNascita: String,
    val importoRichiesto: Double,
    val stato: String,
    val durataMesi: Int,
    val rataMensile: Double,
    val dataRichiesta: String,
    val tipoFinanziamento: String
) {
    companion object {
        fun getMock(idPratica: String): Pratica {
            // This is a mock implementation for development purposes
            return Pratica(
                idPratica = idPratica,
                nome = "Mario",
                cognome = "Rossi",
                codiceFiscale = "RSSMRA80A01H501U",
                dataNascita = "01/01/1980",
                importoRichiesto = 15000.00,
                stato = "Approvata",
                durataMesi = 60,
                rataMensile = 300.00,
                dataRichiesta = "01/08/2025",
                tipoFinanziamento = "Personale"
            )
        }
    }
}
