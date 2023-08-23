
import data.Repository
import java.io.File
import java.io.IOException
import java.net.URL

object Main {

    @JvmStatic
    fun main(args: Array<String>) {

        val rootDirectory = "/mnt/storage-unit1/durable/Mes Documents MEGA/parapente/question-reponse-data"
        val imagesDirectory = "/mnt/storage-unit1/durable/Mes Documents MEGA/parapente/question-reponse-data/images"
        val outputDirectory = File(rootDirectory, "pdfs")

        val sourceFilenames = listOf(
            "aérodynamique-questions-réponses.xml",
            "législation-questions-réponses.xml",
            "matériel-questions-réponses.xml",
            "météorologie-questions-réponses.xml",
            "pratique-de-vol-questions-réponses.xml"
        )

        generatePdfs(outputDirectory, sourceFilenames, rootDirectory, imagesDirectory, showAnswers = false)
        generatePdfs(outputDirectory, sourceFilenames, rootDirectory, imagesDirectory, showAnswers = true)
    }

    private fun generatePdfs(
        outputDirectory: File,
        sourceFilenames: List<String>,
        rootDirectory: String,
        imagesDirectory: String,
        showAnswers: Boolean
    ) {
        if (!outputDirectory.exists()) outputDirectory.mkdir()

        sourceFilenames.forEach { sourceFilename ->
            val sourceFile = File(rootDirectory, sourceFilename)
            val repository = Repository(sourceFile)

            val outputFile = File(outputDirectory, buildPdfName(sourceFilename, showAnswers))

            println("Generating PDF $outputFile from $sourceFilename")
            val pdfCreator = PdfCreator(outputFile, File(imagesDirectory))

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

