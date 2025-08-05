package com.ecqs.features.stampa.service

import com.ecqs.features.stampa.model.Pratica
import com.ecqs.features.stampa.model.TipologiaDocumento
import com.ecqs.features.stampa.pdf.PdfService
import com.ecqs.features.stampa.templater.AllegatoBTemplater
import com.ecqs.features.stampa.templater.PreBenestareTemplater
import com.ecqs.features.stampa.templater.SinistriTemplater
import org.springframework.stereotype.Service
import java.util.Base64
import java.util.UUID

@Service
class StampaService(private val pdfService: PdfService) {

    private val MOCK_PRATICA_ID = "N06FOOGHY"

    // Mock database for protocollo records
    private val mockProtocolliDb: MutableMap<String, ProtocolloRecord> = mutableMapOf()

    data class ProtocolloRecord(
        val idPratica: String,
        val nomeDocumento: String,
        val pdfBlob: String, // Base64 encoded PDF
        val dataEmissione: String,
        val tipologia: String
    )

    fun generateAndSavePdf(idPratica: String, tipologia: TipologiaDocumento): ByteArray {
        val pratica = getPraticaFromCordappMock(idPratica)

        val templater = when (tipologia) {
            TipologiaDocumento.PRE_BENESTARE -> PreBenestareTemplater()
            TipologiaDocumento.ALLEGATO_B -> AllegatoBTemplater()
            TipologiaDocumento.SINISTRI -> SinistriTemplater()
        }

        val protocollo = generateNewProtocollo()
        val templateData = templater.getTemplateData(pratica, protocollo)
        val pdfBytes = pdfService.generaPdf(tipologia.templateName, templateData)

        // Simulate saving to database
        val base64Pdf = Base64.getEncoder().encodeToString(pdfBytes)
        val record = ProtocolloRecord(
            idPratica = pratica.idPratica,
            nomeDocumento = "${tipologia.displayName.replace(" ", "_")}_${pratica.idPratica}.pdf",
            pdfBlob = base64Pdf,
            dataEmissione = templater.getCurrentFormattedDate(),
            tipologia = tipologia.name
        )
        mockProtocolliDb[protocollo] = record

        return pdfBytes
    }

    fun getPdfByProtocollo(protocollo: String): ByteArray {
        val record = mockProtocolliDb[protocollo]
            ?: throw IllegalArgumentException("Protocollo '$protocollo' non trovato nel database.")
        return Base64.getDecoder().decode(record.pdfBlob)
    }

    private fun getPraticaFromCordappMock(idPratica: String): Pratica {
        val sanitizedIdPratica = idPratica.uppercase()
        if (sanitizedIdPratica != MOCK_PRATICA_ID) {
            throw IllegalArgumentException("Pratica con ID '$idPratica' non trovata nel database.")
        }
        return Pratica.getMock(sanitizedIdPratica)
    }

    private fun generateNewProtocollo(): String {
        // TODO: La generazione del protocollo deve evitare collisioni con i protocolli esistenti
        // nel DB di AUX (nuovo schema Protocolli).
        val randomString = UUID.randomUUID().toString().replace("-", "").substring(0, 6).uppercase()
        val part1 = randomString.substring(0, 3)
        val part2 = randomString.substring(3, 6)
        return "$part1-$part2"
    }
}