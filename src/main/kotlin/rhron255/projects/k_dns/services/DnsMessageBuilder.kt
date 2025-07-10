package rhron255.projects.k_dns.services

import rhron255.projects.k_dns.protocol.DnsMessage
import rhron255.projects.k_dns.protocol.DnsQuestion
import rhron255.projects.k_dns.protocol.header.DnsHeader
import rhron255.projects.k_dns.protocol.header.DnsHeader.Companion.BYTES_IN_HEADER
import rhron255.projects.k_dns.protocol.header.DnsOpcode
import rhron255.projects.k_dns.protocol.header.DnsResponseCode
import rhron255.projects.k_dns.protocol.resource_records.ResourceRecord
import rhron255.projects.k_dns.utils.toLabelBytes
import java.util.concurrent.atomic.AtomicInteger

open class DnsMessageBuilder private constructor(private val isQuery: Boolean) {
    class DnsQueryBuilder() : DnsMessageBuilder(false) {
        fun addQuestion(question: DnsQuestion) = this.apply { questions.add(question) }
        fun clearQuestions() = this.apply { questions.clear() }
        fun setOpcode(opcode: DnsOpcode) = apply { this.opcode = opcode }
    }

    // TODO - Not returning the original question here might be a good idea
    class DnsResponseBuilder(questions: List<DnsQuestion>) : DnsMessageBuilder(false) {
        init {
            this.questions.addAll(questions)
        }

        fun addAnswer(record: ResourceRecord<*>) = apply { answers.add(record) }
        fun addAuthoritativeAnswer(record: ResourceRecord<*>) = apply { authoritativeAnswers.add(record) }
        fun addAdditionalRecord(record: ResourceRecord<*>) = apply { additionlRecords.add(record) }
        fun setResponseCode(code: DnsResponseCode) = apply { responseCode = code }
        fun setTruncation(decision: Boolean) = apply { truncation = decision }
        fun setAuthoritative(decision: Boolean) = apply { authoritativeAnswer = decision }
        fun setId(queryId: Short) = apply { this.setId = queryId }
    }

    companion object {
        fun query() = DnsQueryBuilder()
        fun response(questions: List<DnsQuestion> = listOf()) = DnsResponseBuilder(questions)
        private val id = AtomicInteger(0)
    }

    val questions = mutableListOf<DnsQuestion>()
    val answers = mutableListOf<ResourceRecord<*>>()
    val authoritativeAnswers = mutableListOf<ResourceRecord<*>>()
    val additionlRecords = mutableListOf<ResourceRecord<*>>()

    var opcode: DnsOpcode = DnsOpcode.STANDARD_QUERY
    var authoritativeAnswer: Boolean = false
    var truncation: Boolean = false

    //    TODO Decide what to do with recursion
    var recursionDesired: Boolean = true
    var recursionAvailable: Boolean = false
    var responseCode: DnsResponseCode = DnsResponseCode.NO_ERROR

    protected var setId: Short? = null

    fun build(): DnsMessage {
        if (id.get() >= Short.MAX_VALUE) {
            id.set(0)
        }
        val header = DnsHeader(
            queryID = setId ?: id.getAndIncrement().toShort(),
            questionCount = questions.size.toShort(),
            answerCount = answers.size.toShort(),
            recordCount = authoritativeAnswers.size.toShort(),
            additionalResourceCount = additionlRecords.size.toShort(),
            isQuestion = isQuery,
            opcode = opcode,
            authoritativeAnswer = authoritativeAnswer,
            truncation = truncation,
            recursionDesired = recursionDesired,
            recursionAvailable = recursionAvailable,
            responseCode = responseCode,
        )
//        TODO when hardening the code - drop the offsets - could be used maliciously to cause overflows.
        (answers + authoritativeAnswers + additionlRecords).forEach { record ->
            val index = questions.indexOfFirst { it.question.endsWith(record.name) }
            if (index != -1) {
                record.setPointer(
                    questions.slice(0 until index).sumOf { it.toBytes().size } +
                            questions[index].question.removeSuffix(record.name).toLabelBytes()
                                .array().size + BYTES_IN_HEADER
                )
            }
        }

        return DnsMessage(
            header = header,
            questions = questions,
            answers = answers,
            authorityRecords = authoritativeAnswers,
            additionalRecords = additionlRecords,
        )
    }
}