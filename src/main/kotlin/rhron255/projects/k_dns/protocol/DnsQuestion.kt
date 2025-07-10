package rhron255.projects.k_dns.protocol

import rhron255.projects.k_dns.utils.readDomainName
import rhron255.projects.k_dns.utils.toLabelBytes
import java.nio.ByteBuffer

class DnsQuestion {
    val question: String
    val questionType: RecordType
    val questionClass: RecordClass

    constructor(bytes: ByteBuffer) {
        question = bytes.readDomainName()

        questionType = RecordType.fromBytes(bytes)
        questionClass = RecordClass.fromBytes(bytes)
    }

    constructor(question: String, questionType: RecordType, questionClass: RecordClass) {
        this.question = question
        this.questionType = questionType
        this.questionClass = questionClass
    }

    override fun toString() = "DnsQuestion{type='$questionType',class='$questionClass',questions=$question}"

    fun toBytes(): ByteArray {
        val questionBytes = question.toLabelBytes()
        return ByteBuffer.allocate(questionBytes.array().size + 4)
            .put(questionBytes)
            .putShort(questionType.code)
            .putShort(questionClass.code)
            .array()
    }
}