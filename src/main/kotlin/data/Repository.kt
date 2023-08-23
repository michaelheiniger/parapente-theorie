package data

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import java.io.File

class Repository(file: File) {

    private val document: Root

    init {
        val xmlMapper: ObjectMapper = XmlMapper.builder()
            .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .addModule(KotlinModule.Builder().build())
            .build()

        document = xmlMapper.readValue(file, Root::class.java)
    }

    fun questions(): List<Question> = document.questions
}

data class Root(val questions: List<Question>)

data class Question(
    val id: String,
    val number: String,
    val imageId: String?,
    val answer: Int,
    val question: String,
    val answer1: String,
    val answer2: String,
    val answer3: String,
    val answer4: String,
) {
    fun getAnswers(): List<String> = listOf(answer1, answer2, answer3, answer4).map(::fixTextFormatting)

    private fun fixTextFormatting(text: String): String =
        text.replace("\n", "").replace(Regex("\\s{2,}"), " ")
}