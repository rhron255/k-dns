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
    val authorityRecordCount: Short
    val additionalRecordCount: Short

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
        this.authorityRecordCount = recordCount
        this.additionalRecordCount = additionalResourceCount
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
        authorityRecordCount = byteBuffer.getShort()
        additionalRecordCount = byteBuffer.getShort()
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
        stringBuilder.append("recordCount=$authorityRecordCount,")
        stringBuilder.append("additionalResourceCount=$additionalRecordCount}")

        return stringBuilder.toString()
    }

    fun toBytes(): ByteArray = ByteBuffer.allocate(BYTES_IN_HEADER).apply {
        putShort(queryID)
        var headerBits = 0.toShort()
        headerBits = if (isQuestion) headerBits and DnsHeaderMasks.QUERY_OR_RESPONSE.mask.inv() else headerBits or DnsHeaderMasks.QUERY_OR_RESPONSE.mask
        headerBits = headerBits or (opcode.ordinal shl 11).toShort()
        headerBits = if (authoritativeAnswer) headerBits or DnsHeaderMasks.AUTHORITATIVE_ANSWER.mask else headerBits and DnsHeaderMasks.AUTHORITATIVE_ANSWER.mask.inv()
        headerBits = if (truncation) headerBits or DnsHeaderMasks.TRUNCATION.mask else headerBits and DnsHeaderMasks.TRUNCATION.mask.inv()
        headerBits = if (recursionDesired) headerBits or DnsHeaderMasks.RECURSION_DESIRED.mask else headerBits and DnsHeaderMasks.RECURSION_AVAILABLE.mask.inv()
        headerBits = if (recursionAvailable) headerBits or DnsHeaderMasks.RECURSION_AVAILABLE.mask else headerBits and DnsHeaderMasks.RECURSION_AVAILABLE.mask.inv()
        headerBits = headerBits or (responseCode.ordinal).toShort()
        putShort(headerBits)
        putShort(questionCount)
        putShort(answerCount)
        putShort(authorityRecordCount)
        putShort(additionalRecordCount)
    }.array()

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
        recordCount ?: this.authorityRecordCount,
        additionalResourceCount ?: this.additionalRecordCount,
    )
}