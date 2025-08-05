package com.ecqs.features.stampa.templater

import com.ecqs.features.stampa.model.Pratica

class SinistriTemplater : DocumentTemplater() {
    override fun getDocumentTitle(pratica: Pratica): String {
        return "Sinistri"
    }

    override fun getTemplateData(pratica: Pratica, protocollo: String): Map<String, Any> {
        val campiTemplate = listOf(
            "ID Pratica: ${pratica.idPratica}",
            "Stato Pratica: ${pratica.stato}",
            "Nome: ${pratica.nome}",
            "Cognome: ${pratica.cognome}",
            "Codice Fiscale: ${pratica.codiceFiscale}",
            "Data Nascita: ${pratica.dataNascita}",
            "Importo Richiesto: ${pratica.importoRichiesto}",
            "Durata Mesi: ${pratica.durataMesi}",
            "Rata Mensile: ${pratica.rataMensile}",
            "Data Richiesta: ${pratica.dataRichiesta}",
            "Tipo Finanziamento: ${pratica.tipoFinanziamento}"
        )
        return mapOf(
            "DOCUMENT_TITLE" to getDocumentTitle(pratica),
            "DATA_EMISSIONE" to getCurrentFormattedDate(),
            "PROTOCOLLO" to protocollo,
            "CAMPI_TEMPLATE" to campiTemplate.joinToString("\n")
        )
    }
}
