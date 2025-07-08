package rhron255.projects.k_dns.protocol.header

import java.nio.ByteBuffer
import kotlin.experimental.and
import kotlin.experimental.inv
import kotlin.experimental.or

class DnsHeader {
    companion object {
        const val BYTES_IN_HEADER = 12

        fun fromBytes(byteArray: ByteArray): DnsHeader {
            val bytes = ByteBuffer.wrap(byteArray)
            val header = ByteArray(BYTES_IN_HEADER)
            bytes.get(header)
            val headerBytes = ByteBuffer.wrap(header)
            return DnsHeader(headerBytes)
        }
    }


    val queryID: Short
    val questionCount: Short
    val isQuestion: Boolean
    val opcode: DnsOpcode
    val authoritativeAnswer: Boolean
    val truncation: Boolean
    val recursionDesired: Boolean
    val recursionAvailable: Boolean
    val responseCode: DnsResponseCode
    val answerCount: Short
    val recordCount: Short
    val additionalResourceCount: Short

    constructor(
        queryID: Short,
        questionCount: Short,
        isQuestion: Boolean,
        opcode: DnsOpcode,
        authoritativeAnswer: Boolean,
        truncation: Boolean,
        recursionDesired: Boolean,
        recursionAvailable: Boolean,
        responseCode: DnsResponseCode,
        answerCount: Short,
        recordCount: Short,
        additionalResourceCount: Short
    ) {
        this.queryID = queryID
        this.questionCount = questionCount
        this.isQuestion = isQuestion
        this.opcode = opcode
        this.authoritativeAnswer = authoritativeAnswer
        this.truncation = truncation
        this.recursionDesired = recursionDesired
        this.recursionAvailable = recursionAvailable
        this.responseCode = responseCode
        this.answerCount = answerCount
        this.recordCount = recordCount
        this.additionalResourceCount = additionalResourceCount
    }

    constructor (byteBuffer: ByteBuffer) {
        queryID = byteBuffer.getShort()
        with(byteBuffer.getShort()) {
            isQuestion = (this and DnsHeaderMasks.QUERY_OR_RESPONSE.mask) == 0.toShort()
            opcode = DnsOpcode.from((this and DnsHeaderMasks.OPCODE.mask).toInt() shr 11)
            authoritativeAnswer = (this and DnsHeaderMasks.AUTHORITATIVE_ANSWER.mask) != 0.toShort()
            truncation = (this and DnsHeaderMasks.TRUNCATION.mask) != 0.toShort()
            recursionDesired = (this and DnsHeaderMasks.RECURSION_DESIRED.mask) != 0.toShort()
            recursionAvailable = (this and DnsHeaderMasks.RECURSION_AVAILABLE.mask) != 0.toShort()
            responseCode = DnsResponseCode.from((this and DnsHeaderMasks.RESPONSE_CODE.mask))
        }
        questionCount = byteBuffer.getShort()
        answerCount = byteBuffer.getShort()
        recordCount = byteBuffer.getShort()
        additionalResourceCount = byteBuffer.getShort()
    }

    override fun toString(): String {
        val stringBuilder = StringBuilder()
        stringBuilder.append("DnsHeader{")
        stringBuilder.append("queryID=$queryID,")
        stringBuilder.append("opcode=$opcode,")
        if (authoritativeAnswer) {
            stringBuilder.append("AA,")
        }
        if (truncation) {
            stringBuilder.append("TC,")
        }
        if (recursionDesired) {
            stringBuilder.append("RD,")
        }
        if (recursionAvailable) {
            stringBuilder.append("RA,")
        }
        stringBuilder.append("responseCode=$responseCode,")
        stringBuilder.append("questionCount=$questionCount,")
        stringBuilder.append("answerCount=$answerCount,")
        stringBuilder.append("recordCount=$recordCount,")
        stringBuilder.append("additionalResourceCount=$additionalResourceCount}")

        return stringBuilder.toString()
    }

    fun toBytes() = ByteBuffer.allocate(BYTES_IN_HEADER).apply {
        putShort(queryID)
        putShort(0.toShort().apply {
            if (isQuestion) this and DnsHeaderMasks.QUERY_OR_RESPONSE.mask.inv() else this or DnsHeaderMasks.QUERY_OR_RESPONSE.mask
            this or (opcode.ordinal shl 11).toShort()
            if (authoritativeAnswer) this or DnsHeaderMasks.AUTHORITATIVE_ANSWER.mask else this and DnsHeaderMasks.AUTHORITATIVE_ANSWER.mask.inv()
            if (truncation) this or DnsHeaderMasks.TRUNCATION.mask else this and DnsHeaderMasks.TRUNCATION.mask.inv()
            if (recursionDesired) this or DnsHeaderMasks.RECURSION_DESIRED.mask else this and DnsHeaderMasks.RECURSION_AVAILABLE.mask.inv()
            if (recursionAvailable) this or DnsHeaderMasks.RECURSION_AVAILABLE.mask else this and DnsHeaderMasks.RECURSION_AVAILABLE.mask.inv()
            this or (responseCode.ordinal).toShort()
        })
        putShort(questionCount)
        putShort(answerCount)
        putShort(recordCount)
        putShort(additionalResourceCount)
    }

    fun copy(
        queryID: Short? = null,
        questionCount: Short? = null,
        isQuestion: Boolean? = null,
        opcode: DnsOpcode? = null,
        authoritativeAnswer: Boolean? = null,
        truncation: Boolean? = null,
        recursionDesired: Boolean? = null,
        recursionAvailable: Boolean? = null,
        responseCode: DnsResponseCode? = null,
        answerCount: Short? = null,
        recordCount: Short? = null,
        additionalResourceCount: Short? = null
    ) = DnsHeader(
        queryID ?: this.queryID,
        questionCount ?: this.questionCount,
        isQuestion ?: this.isQuestion,
        opcode ?: this.opcode,
        authoritativeAnswer ?: this.authoritativeAnswer,
        truncation ?: this.truncation,
        recursionDesired ?: this.recursionDesired,
        recursionAvailable ?: this.recursionAvailable,
        responseCode ?: this.responseCode,
        answerCount ?: this.answerCount,
        recordCount ?: this.recordCount,
        additionalResourceCount ?: this.additionalResourceCount,
    )
}