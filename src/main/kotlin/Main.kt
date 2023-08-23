
import data.Repository
import java.io.File
import java.io.IOException
import java.net.URL

data class PartName(val name: String)
data class FileName(val name: String)

object Main {

    @JvmStatic
    fun main(args: Array<String>) {

        val rootDirectory = "/mnt/storage-unit1/durable/Mes Documents MEGA/parapente/question-reponse-data"
        val imagesDirectory = "/mnt/storage-unit1/durable/Mes Documents MEGA/parapente/question-reponse-data/images"
        val outputDirectory = File(rootDirectory, "pdfs")

        val sourceFilenames = mapOf(
            PartName("Aérodynamique") to FileName("aérodynamique-questions-réponses.xml"),
            PartName("Législation") to FileName("législation-questions-réponses.xml"),
            PartName("Matériel") to FileName("matériel-questions-réponses.xml"),
            PartName("Météorologie") to FileName("météorologie-questions-réponses.xml"),
            PartName("Pratique de vol") to FileName( "pratique-de-vol-questions-réponses.xml")
        )

        generatePdfs(outputDirectory, sourceFilenames, rootDirectory, imagesDirectory, showAnswers = false)
        generatePdfs(outputDirectory, sourceFilenames, rootDirectory, imagesDirectory, showAnswers = true)
    }

    private fun generatePdfs(
        outputDirectory: File,
        sourceFilenames: Map<PartName, FileName>,
        rootDirectory: String,
        imagesDirectory: String,
        showAnswers: Boolean
    ) {
        if (!outputDirectory.exists()) outputDirectory.mkdir()

        sourceFilenames.forEach { sourceFilename ->
            val sourceFile = File(rootDirectory, sourceFilename.value.name)
            val repository = Repository(sourceFile)

            val outputFile = File(outputDirectory, buildPdfName(sourceFilename.value.name, showAnswers))

            println("Generating PDF $outputFile from ${sourceFilename.value.name}")
            val pdfCreator = PdfCreator(outputFile, File(imagesDirectory), sourceFilename.key.name)

            repository.questions().forEachIndexed { index, question ->
                pdfCreator.printQuestion(index, question, showAnswers)
            }
            pdfCreator.close()
        }
    }

    private fun buildPdfName(sourceFilename: String, showAnswers: Boolean): String {
        val filenameWithNoExtension = sourceFilename.replace(".xml", "")
        val filenameWithNoExtensionAndAnswer = if (showAnswers) "${filenameWithNoExtension}-avec-réponses" else filenameWithNoExtension
        return "${filenameWithNoExtensionAndAnswer}.pdf"
    }

    fun downloadImages() {
        val directory = File("/mnt/storage-unit1/durable/Mes Documents MEGA/parapente/question-reponse-data")

        for (i in 1..82) {

            try {
                val url = URL("https://elearning.shv-fsvl.ch/pictures/${i}.jpg")
                val imageData = url.readBytes()
                File(directory, "${i}.jpg").writeBytes(imageData)
            } catch (e: IOException) {
                println(e.message)
            }
        }
    }
}

