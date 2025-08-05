package com.ecqs.features.stampa.model

enum class TipologiaDocumento(val displayName: String, val templateName: String) {
    PRE_BENESTARE("Pre Benestare", "pre-benestare"),
    ALLEGATO_B("Allegato B", "allegato-b"),
    SINISTRI("Sinistri", "sinistri");

    companion object {
        fun fromString(value: String): TipologiaDocumento {
            return when (value.lowercase()) {
                "pre-benestare" -> PRE_BENESTARE
                "allegato-b" -> ALLEGATO_B
                "sinistri" -> SINISTRI
                else -> throw IllegalArgumentException("Tipologia documento non valida. Valori ammessi: pre-benestare, allegato-b, sinistri.")
            }
        }
    }
}

