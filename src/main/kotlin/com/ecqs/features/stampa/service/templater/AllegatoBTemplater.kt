package com.ecqs.features.stampa.service.templater

import com.ecqs.features.stampa.model.Pratica
import org.springframework.core.io.ResourceLoader

class AllegatoBTemplater(resourceLoader: ResourceLoader) : DocumentTemplater(resourceLoader) {

    override fun getDocumentTitle(pratica: Pratica): String {
        return "Allegato B"
    }

    override fun getCampiTemplate(pratica: Pratica): List<String> {
        // TODO: Questa lista è mockata. In un'implementazione reale, dovrebbe essere popolata
        // da un JSON o, meglio ancora, implementata in AutoForge con un approccio basato su
        // placeholder da un input/template, evitando la generazione complessa.
        return listOf(
            "Nome: ${pratica.nome}",
            "Cognome: ${pratica.cognome}",
            "Codice Fiscale: ${pratica.codiceFiscale}",
            "Data Nascita: ${pratica.dataNascita}",
            "Importo Richiesto: ${pratica.importoRichiesto}",
            "Stato: ${pratica.stato}"
        )
    }
}