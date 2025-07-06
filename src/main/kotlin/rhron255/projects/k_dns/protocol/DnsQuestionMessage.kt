package rhron255.projects.k_dns.protocol

import rhron255.projects.k_dns.utils.getAsciiChar
import rhron255.projects.k_dns.utils.getCurrentByte
import rhron255.projects.k_dns.utils.skip
import java.nio.ByteBuffer

class DnsQuestionMessage {
    val header: DnsHeader
    val questions: List<String>
    val questionType: RecordType
    val questionClass: RecordClass

    constructor(byteArray: ByteArray) {
        val parsedQuestions = mutableListOf<String>()
        val bytes = ByteBuffer.wrap(byteArray)
        header = DnsHeader(bytes)
        var questionCount = header.questionCount
        val question = StringBuilder()

        while (bytes.hasRemaining() && questionCount > 0) {
            val length = bytes.get().toInt()
            (0 until length).forEach { _ ->
                question.append(bytes.getAsciiChar())
            }
            if (bytes.getCurrentByte() == 0x00.toByte()) {
                bytes.skip(1)
                parsedQuestions.add(question.toString())
                question.clear()
                questionCount--
            } else {
                question.append(".")
            }
        }

        questions = parsedQuestions.toList()

        questionType = RecordType.fromBytes(bytes)
        questionClass = RecordClass.fromBytes(bytes)
    }

    override fun toString() = "DnsQuestionMessage{type='$questionType',class='$questionClass',questions=$questions}"
}