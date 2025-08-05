# ecqs-feature-stampa-protocolli

Servizio REST scritto in **Kotlin** con **Spring Boot** per la generazione e il recupero di documenti PDF relativi a pratiche di prestito. I PDF vengono creati a partire da un template Word (`template.docx`) che viene popolato tramite *poi-tl* e convertito in PDF utilizzando **docx4j-export-fo** e **Apache FOP**.

## Endpoints principali

### `GET /stampa/{idPratica}/{tipologia}`

Genera un nuovo PDF per la pratica indicata.

- `idPratica`: stringa alfanumerica di 8 caratteri.
- `tipologia`: una tra `allegato-b`, `pre-benestare`, `sinistri`.

La risposta contiene il PDF generato con nome file `TIPOLOGIA_idPratica.pdf`.

### `GET /stampa/{protocollo}`

Recupera un PDF precedentemente generato tramite protocollo.

- `protocollo`: stringa alfanumerica nel formato `xxx-xxx`.

La risposta contiene il PDF associato.

## Avvio e test

```bash
./gradlew bootRun   # avvia l'applicazione
./gradlew test      # esegue la suite di test
```
