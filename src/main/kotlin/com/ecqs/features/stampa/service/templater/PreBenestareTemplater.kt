package com.ecqs.features.stampa.service.templater

import com.ecqs.features.stampa.model.Pratica
import org.springframework.core.io.ResourceLoader

class PreBenestareTemplater(resourceLoader: ResourceLoader) : DocumentTemplater(resourceLoader) {

    override fun getDocumentTitle(pratica: Pratica): String {
        return "Pre Benestare"
    }

    override fun getCampiTemplate(pratica: Pratica): List<String> {
        // TODO: Questa lista è mockata. In un'implementazione reale, dovrebbe essere popolata
        // da un JSON o, meglio ancora, implementata in AutoForge con un approccio basato su
        // placeholder da un input/template, evitando la generazione complessa.
        return listOf(
            "ID Pratica: ${pratica.idPratica}",
            "Nome Richiedente: ${pratica.nome} ${pratica.cognome}",
            "Importo: ${pratica.importoRichiesto}"
        )
    }
}