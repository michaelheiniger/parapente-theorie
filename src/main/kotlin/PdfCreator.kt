import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.*
import com.itextpdf.layout.element.List
import data.Question
import java.io.File


class PdfCreator(outputFile: File, private val imageDirectory: File) {

    private val document: Document
    private val pdfDocument: PdfDocument

    init {
        pdfDocument = PdfDocument(PdfWriter(outputFile))
        document = Document(pdfDocument, PageSize.A4)
    }

    fun printQuestion(index: Int, question: Question, showAnswers: Boolean) {

        val answersBlock = buildAnswersBlock(question, showAnswers)

        val questionParagraph = Paragraph()
            .add(Text("Question ${index + 1}\n").setBold())
            .add("${question.question}\n")

        addQuestionImage(question, questionParagraph)

        questionParagraph.add(answersBlock)
            .isKeepTogether = true

        document.add(Paragraph(""))
            .add(questionParagraph)
    }

    private fun addQuestionImage(question: Question, questionParagraph: Paragraph) {
        if (question.imageId != null) {
            val imagePath = File(imageDirectory, "${question.imageId}.jpg")
            val imageData = ImageDataFactory.create(imagePath.absolutePath)
            val image = Image(imageData)
                .setMarginTop(10f)
                .setMarginBottom(10f)
    //        image.setWidth(pdfDocument.defaultPageSize.width)
            image.setAutoScaleHeight(true)
            questionParagraph.add(image)
        }
    }

    fun close() {
        document.close()
    }

    private fun buildAnswersBlock(question: Question, showAnswer: Boolean): List {
        val list = List()
        question.getAnswers().forEachIndexed { index, q ->
            val item = ListItem(q)
            if (index == (question.answer - 1) && showAnswer) item.setBold()
            list.add(item)
        }
        return list
    }
}