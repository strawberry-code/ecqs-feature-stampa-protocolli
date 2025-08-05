package com.ecqs.features.stampa.pdf

import com.openhtmltopdf.extend.FSSupplier
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder
import org.springframework.stereotype.Service
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import java.io.ByteArrayOutputStream
import java.io.InputStream

@Service
class PdfService(private val templateEngine: TemplateEngine) {

    fun generaPdf(template: String, context: Map<String, Any>): ByteArray {
        val ctx = Context()
        ctx.setVariables(context)

        val html = templateEngine.process(template, ctx)

        val os = ByteArrayOutputStream()
        val builder = PdfRendererBuilder()
        builder.useFastMode()

        val fontSupplier = FSSupplier<InputStream> { this.javaClass.classLoader.getResourceAsStream("fonts/Roboto-Regular.ttf") }
        builder.useFont(fontSupplier, "Roboto")

        builder.withHtmlContent(html, "classpath:/templates/")
        builder.toStream(os)
        builder.run()

        return os.toByteArray()
    }
}
