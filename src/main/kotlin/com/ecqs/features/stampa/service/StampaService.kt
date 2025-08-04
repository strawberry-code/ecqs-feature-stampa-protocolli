package com.ecqs.features.stampa.service

import com.ecqs.features.shared.exception.InvalidProtocoloFormatException
import com.ecqs.features.shared.exception.PraticaNotFoundException
import com.ecqs.features.shared.exception.ProtocoloNotFoundException
import com.ecqs.features.stampa.model.Pratica
import com.ecqs.features.stampa.model.TipologiaDocumento
import com.ecqs.features.stampa.service.templater.AllegatoBTemplater
import com.ecqs.features.stampa.service.templater.PreBenestareTemplater
import com.ecqs.features.stampa.service.templater.SinistriTemplater
import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.Resource
import org.springframework.core.io.ResourceLoader
import org.springframework.stereotype.Service
import java.util.Base64

@Service
class StampaService(private val resourceLoader: ResourceLoader) {

    private val MOCK_PRATICA_ID = "N06FOOGHY"
    private val MOCK_PROTOCOLLO = "123-asd"
    private val TEMPLATE_NAME = "template.docx"
    private val MOCK_DB_DOCUMENT_BASE64 = "SGFsbG8sIFdlbHQh" // "Hallo, Welt!"

    data class ProtocolloRecord(
        val idPratica: String,
        val nomeDocumento: String,
        val blob: String, // Base64 encoded document
        val dataEmissione: String
    )

    private val mockProtocolliDb: Map<String, ProtocolloRecord> = mapOf(
        MOCK_PROTOCOLLO to ProtocolloRecord(
            idPratica = MOCK_PRATICA_ID,
            nomeDocumento = "documento_mock.docx",
            blob = MOCK_DB_DOCUMENT_BASE64,
            dataEmissione = "05/08/2025"
        )
    )

    fun getDocument(idPratica: String, tipologia: String, protocollo: String): Resource {
        val pratica = getPraticaFromCordappMock(idPratica)
        val tipologiaEnum = TipologiaDocumento.fromString(tipologia)

        val templater = when (tipologiaEnum) {
            TipologiaDocumento.ALLEGATO_B -> AllegatoBTemplater(resourceLoader)
            TipologiaDocumento.PRE_BENESTARE -> PreBenestareTemplater(resourceLoader)
            TipologiaDocumento.SINISTRI -> SinistriTemplater(resourceLoader)
        }
        val documentBytes = templater.processTemplate(pratica, protocollo)
        // TODO: Implementare la funzione che inserisce un record protocollo nel database di AUX
        // quando viene generato un nuovo protocollo (GET /stampa/{idPratica}/{tipologia}).
        return ByteArrayResource(documentBytes)
    }

    fun getDocumentWithProtocol(protocollo: String): Pair<Resource, String> {
        validateProtocolo(protocollo)
        return getDocumentFromDbMock(protocollo)
    }

    private fun getPraticaFromCordappMock(idPratica: String): Pratica {
        if (idPratica.length != 9 || !idPratica.matches(Regex("[a-zA-Z0-9]+")))
            throw PraticaNotFoundException("Formato idPratica non valido.")

        if (!idPratica.equals(MOCK_PRATICA_ID, ignoreCase = true))
            throw PraticaNotFoundException("Pratica con ID '${idPratica.uppercase()}' non trovata.")

        // Mock di una chiamata a un Corda node per ottenere i dati della pratica
        return Pratica(
            idPratica = MOCK_PRATICA_ID,
            nome = "Mario",
            cognome = "Rossi",
            codiceFiscale = "RSSMRA80A01H501U",
            dataNascita = "01/01/1980",
            importoRichiesto = 10000.0,
            stato = "In Istruttoria"
        )
    }

    private fun validateProtocolo(protocollo: String) {
        val sanitizedProtocollo = protocollo.lowercase()
        if (!sanitizedProtocollo.matches(Regex("^[a-z0-9]{3}-[a-z0-9]{3}$")))
            throw InvalidProtocoloFormatException("Formato protocollo non valido. Accettato: 'xxx-xxx' con caratteri alfanumerici.")

        if (sanitizedProtocollo != MOCK_PROTOCOLLO)
            throw ProtocoloNotFoundException("Protocollo '$protocollo' non trovato.")
    }

    private fun getDocumentFromDbMock(protocollo: String): Pair<Resource, String> {
        val record = mockProtocolliDb[protocollo]
            ?: throw ProtocoloNotFoundException("Protocollo '$protocollo' non trovato nel mock DB.")

        val decodedBytes = Base64.getDecoder().decode(record.blob)
        return Pair(ByteArrayResource(decodedBytes), record.nomeDocumento)
    }
}