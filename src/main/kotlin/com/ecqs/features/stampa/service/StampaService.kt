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
import org.docx4j.Docx4J
import org.docx4j.openpackaging.packages.WordprocessingMLPackage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.Base64

@Service
class StampaService(private val resourceLoader: ResourceLoader) {

    private val MOCK_PRATICA_ID = "N06FOOGH"
    private val MOCK_PROTOCOLLO = "123-asd"
    private val TEMPLATE_NAME = "template.docx"
    private val MOCK_DB_DOCUMENT_BASE64 = "JVBERi0xLjQKJeLjz9MKMSAwIG9iago8PCAvVHlwZSAvQ2F0YWxvZyAvUGFnZXMgMiAwIFIgPj4KZW5kb2JqCjIgMCBvYmoKPDwgL1R5cGUgL1BhZ2VzIC9Db3VudCAxIC9LaWRzIFszIDAgUl0gPj4KZW5kb2JqCjMgMCBvYmoKPDwgL1R5cGUgL1BhZ2UgL1BhcmVudCAyIDAgUiAvTWVkaWFCb3ggWzAgMCA1OTUuMjggODQxLjg5XSAvQ29udGVudHMgNCAwIFIgPj4KZW5kb2JqCjQgMCBvYmoKPDwgL0xlbmd0aCAwID4+CnN0cmVhbQplbmRzdHJlYW0KZW5kb2JqCnhyZWYKMCA1CjAwMDAwMDAwMCA2NTUzNSBmIAowMDAwMDAwMTMgMDAwMDAgbiAKMDAwMDAwMDYxIDAwMDAwIG4gCjAwMDAwMDA5NiAwMDAwMCBuIAowMDAwMDAxNTEgMDAwMDAgbiAKdHJhaWxlcgo8PCAvU2l6ZSA1IC9Sb290IDEgMCBSIC9JbmZvIDYgMCBSID4+CnN0YXJ0eHJlZgo2NQolJUVPRgo=" // base64 di un PDF vuoto

    data class ProtocolloRecord(
        val idPratica: String,
        val nomeDocumento: String,
        val pdfBlob: String, // Base64 encoded PDF
        val dataEmissione: String
    )

    private val mockProtocolliDb: Map<String, ProtocolloRecord> = mapOf(
        MOCK_PROTOCOLLO to ProtocolloRecord(
            idPratica = MOCK_PRATICA_ID,
            nomeDocumento = "documento_mock.pdf",
            pdfBlob = MOCK_DB_DOCUMENT_BASE64,
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
        val pdfBytes = convertToPdf(documentBytes)
        // TODO: Implementare la funzione che inserisce un record protocollo nel database di AUX
        // quando viene generato un nuovo protocollo (GET /stampa/{idPratica}/{tipologia}).
        return ByteArrayResource(pdfBytes)
    }

    fun getDocumentWithProtocol(protocollo: String): Pair<Resource, String> {
        validateProtocolo(protocollo)
        return getDocumentFromDbMock(protocollo)
    }

    private fun getPraticaFromCordappMock(idPratica: String): Pratica {
        if (idPratica.length != 8 || !idPratica.matches(Regex("[a-zA-Z0-9]+")))
            throw PraticaNotFoundException("Formato idPratica non valido. Richiesti 8 caratteri alfanumerici.")

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

        val decodedBytes = Base64.getDecoder().decode(record.pdfBlob)
        return Pair(ByteArrayResource(decodedBytes), record.nomeDocumento)
    }

    private fun convertToPdf(docxBytes: ByteArray): ByteArray {
        val wordMLPackage = WordprocessingMLPackage.load(ByteArrayInputStream(docxBytes))
        val out = ByteArrayOutputStream()
        Docx4J.toPDF(wordMLPackage, out)
        return out.toByteArray()
    }
}